package com.viaversion.viaversion.libs.gson.internal.bind;

import com.viaversion.viaversion.libs.gson.FieldNamingStrategy;
import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.JsonSyntaxException;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.TypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.annotations.JsonAdapter;
import com.viaversion.viaversion.libs.gson.annotations.SerializedName;
import com.viaversion.viaversion.libs.gson.internal.;
import com.viaversion.viaversion.libs.gson.internal.ConstructorConstructor;
import com.viaversion.viaversion.libs.gson.internal.Excluder;
import com.viaversion.viaversion.libs.gson.internal.ObjectConstructor;
import com.viaversion.viaversion.libs.gson.internal.Primitives;
import com.viaversion.viaversion.libs.gson.internal.reflect.ReflectionAccessor;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonToken;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {
  private final ConstructorConstructor constructorConstructor;
  
  private final FieldNamingStrategy fieldNamingPolicy;
  
  private final Excluder excluder;
  
  private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
  
  private final ReflectionAccessor accessor = ReflectionAccessor.getInstance();
  
  public ReflectiveTypeAdapterFactory(ConstructorConstructor constructorConstructor, FieldNamingStrategy fieldNamingPolicy, Excluder excluder, JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory) {
    this.constructorConstructor = constructorConstructor;
    this.fieldNamingPolicy = fieldNamingPolicy;
    this.excluder = excluder;
    this.jsonAdapterFactory = jsonAdapterFactory;
  }
  
  public boolean excludeField(Field f, boolean serialize) {
    return excludeField(f, serialize, this.excluder);
  }
  
  static boolean excludeField(Field f, boolean serialize, Excluder excluder) {
    return (!excluder.excludeClass(f.getType(), serialize) && !excluder.excludeField(f, serialize));
  }
  
  private List<String> getFieldNames(Field f) {
    SerializedName annotation = f.<SerializedName>getAnnotation(SerializedName.class);
    if (annotation == null) {
      String name = this.fieldNamingPolicy.translateName(f);
      return Collections.singletonList(name);
    } 
    String serializedName = annotation.value();
    String[] alternates = annotation.alternate();
    if (alternates.length == 0)
      return Collections.singletonList(serializedName); 
    List<String> fieldNames = new ArrayList<String>(alternates.length + 1);
    fieldNames.add(serializedName);
    for (String alternate : alternates)
      fieldNames.add(alternate); 
    return fieldNames;
  }
  
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    Class<? super T> raw = type.getRawType();
    if (!Object.class.isAssignableFrom(raw))
      return null; 
    ObjectConstructor<T> constructor = this.constructorConstructor.get(type);
    return new Adapter<T>(constructor, getBoundFields(gson, type, raw));
  }
  
  private BoundField createBoundField(final Gson context, final Field field, String name, final TypeToken<?> fieldType, boolean serialize, boolean deserialize) {
    final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
    JsonAdapter annotation = field.<JsonAdapter>getAnnotation(JsonAdapter.class);
    TypeAdapter<?> mapped = null;
    if (annotation != null)
      mapped = this.jsonAdapterFactory.getTypeAdapter(this.constructorConstructor, context, fieldType, annotation); 
    final boolean jsonAdapterPresent = (mapped != null);
    if (mapped == null)
      mapped = context.getAdapter(fieldType); 
    final TypeAdapter<?> typeAdapter = mapped;
    return new BoundField(name, serialize, deserialize) {
        void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException {
          Object fieldValue = field.get(value);
          TypeAdapter t = jsonAdapterPresent ? typeAdapter : new TypeAdapterRuntimeTypeWrapper(context, typeAdapter, fieldType.getType());
          t.write(writer, fieldValue);
        }
        
        void read(JsonReader reader, Object value) throws IOException, IllegalAccessException {
          Object fieldValue = typeAdapter.read(reader);
          if (fieldValue != null || !isPrimitive)
            field.set(value, fieldValue); 
        }
        
        public boolean writeField(Object value) throws IOException, IllegalAccessException {
          if (!this.serialized)
            return false; 
          Object fieldValue = field.get(value);
          return (fieldValue != value);
        }
      };
  }
  
  private Map<String, BoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw) {
    Map<String, BoundField> result = new LinkedHashMap<String, BoundField>();
    if (raw.isInterface())
      return result; 
    Type declaredType = type.getType();
    while (raw != Object.class) {
      Field[] fields = raw.getDeclaredFields();
      for (Field field : fields) {
        boolean serialize = excludeField(field, true);
        boolean deserialize = excludeField(field, false);
        if (serialize || deserialize) {
          this.accessor.makeAccessible(field);
          Type fieldType = .Gson.Types.resolve(type.getType(), raw, field.getGenericType());
          List<String> fieldNames = getFieldNames(field);
          BoundField previous = null;
          for (int i = 0, size = fieldNames.size(); i < size; i++) {
            String name = fieldNames.get(i);
            if (i != 0)
              serialize = false; 
            BoundField boundField = createBoundField(context, field, name, 
                TypeToken.get(fieldType), serialize, deserialize);
            BoundField replaced = result.put(name, boundField);
            if (previous == null)
              previous = replaced; 
          } 
          if (previous != null)
            throw new IllegalArgumentException(declaredType + " declares multiple JSON fields named " + previous.name); 
        } 
      } 
      type = TypeToken.get(.Gson.Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
      raw = type.getRawType();
    } 
    return result;
  }
  
  static abstract class BoundField {
    final String name;
    
    final boolean serialized;
    
    final boolean deserialized;
    
    protected BoundField(String name, boolean serialized, boolean deserialized) {
      this.name = name;
      this.serialized = serialized;
      this.deserialized = deserialized;
    }
    
    abstract boolean writeField(Object param1Object) throws IOException, IllegalAccessException;
    
    abstract void write(JsonWriter param1JsonWriter, Object param1Object) throws IOException, IllegalAccessException;
    
    abstract void read(JsonReader param1JsonReader, Object param1Object) throws IOException, IllegalAccessException;
  }
  
  public static final class Adapter<T> extends TypeAdapter<T> {
    private final ObjectConstructor<T> constructor;
    
    private final Map<String, ReflectiveTypeAdapterFactory.BoundField> boundFields;
    
    Adapter(ObjectConstructor<T> constructor, Map<String, ReflectiveTypeAdapterFactory.BoundField> boundFields) {
      this.constructor = constructor;
      this.boundFields = boundFields;
    }
    
    public T read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        return null;
      } 
      T instance = (T)this.constructor.construct();
      try {
        in.beginObject();
        while (in.hasNext()) {
          String name = in.nextName();
          ReflectiveTypeAdapterFactory.BoundField field = this.boundFields.get(name);
          if (field == null || !field.deserialized) {
            in.skipValue();
            continue;
          } 
          field.read(in, instance);
        } 
      } catch (IllegalStateException e) {
        throw new JsonSyntaxException(e);
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      } 
      in.endObject();
      return instance;
    }
    
    public void write(JsonWriter out, T value) throws IOException {
      if (value == null) {
        out.nullValue();
        return;
      } 
      out.beginObject();
      try {
        for (ReflectiveTypeAdapterFactory.BoundField boundField : this.boundFields.values()) {
          if (boundField.writeField(value)) {
            out.name(boundField.name);
            boundField.write(out, value);
          } 
        } 
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      } 
      out.endObject();
    }
  }
}
