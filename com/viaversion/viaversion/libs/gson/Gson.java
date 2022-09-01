package com.viaversion.viaversion.libs.gson;

import com.viaversion.viaversion.libs.gson.internal.ConstructorConstructor;
import com.viaversion.viaversion.libs.gson.internal.Excluder;
import com.viaversion.viaversion.libs.gson.internal.Primitives;
import com.viaversion.viaversion.libs.gson.internal.Streams;
import com.viaversion.viaversion.libs.gson.internal.bind.ArrayTypeAdapter;
import com.viaversion.viaversion.libs.gson.internal.bind.CollectionTypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.internal.bind.DateTypeAdapter;
import com.viaversion.viaversion.libs.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.internal.bind.JsonTreeReader;
import com.viaversion.viaversion.libs.gson.internal.bind.JsonTreeWriter;
import com.viaversion.viaversion.libs.gson.internal.bind.MapTypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.internal.bind.ObjectTypeAdapter;
import com.viaversion.viaversion.libs.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.internal.bind.SqlDateTypeAdapter;
import com.viaversion.viaversion.libs.gson.internal.bind.TimeTypeAdapter;
import com.viaversion.viaversion.libs.gson.internal.bind.TypeAdapters;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonToken;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import com.viaversion.viaversion.libs.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

public final class Gson {
  static final boolean DEFAULT_JSON_NON_EXECUTABLE = false;
  
  static final boolean DEFAULT_LENIENT = false;
  
  static final boolean DEFAULT_PRETTY_PRINT = false;
  
  static final boolean DEFAULT_ESCAPE_HTML = true;
  
  static final boolean DEFAULT_SERIALIZE_NULLS = false;
  
  static final boolean DEFAULT_COMPLEX_MAP_KEYS = false;
  
  static final boolean DEFAULT_SPECIALIZE_FLOAT_VALUES = false;
  
  private static final TypeToken<?> NULL_KEY_SURROGATE = TypeToken.get(Object.class);
  
  private static final String JSON_NON_EXECUTABLE_PREFIX = ")]}'\n";
  
  private final ThreadLocal<Map<TypeToken<?>, FutureTypeAdapter<?>>> calls = new ThreadLocal<Map<TypeToken<?>, FutureTypeAdapter<?>>>();
  
  private final Map<TypeToken<?>, TypeAdapter<?>> typeTokenCache = new ConcurrentHashMap<TypeToken<?>, TypeAdapter<?>>();
  
  private final ConstructorConstructor constructorConstructor;
  
  private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
  
  final List<TypeAdapterFactory> factories;
  
  final Excluder excluder;
  
  final FieldNamingStrategy fieldNamingStrategy;
  
  final Map<Type, InstanceCreator<?>> instanceCreators;
  
  final boolean serializeNulls;
  
  final boolean complexMapKeySerialization;
  
  final boolean generateNonExecutableJson;
  
  final boolean htmlSafe;
  
  final boolean prettyPrinting;
  
  final boolean lenient;
  
  final boolean serializeSpecialFloatingPointValues;
  
  final String datePattern;
  
  final int dateStyle;
  
  final int timeStyle;
  
  final LongSerializationPolicy longSerializationPolicy;
  
  final List<TypeAdapterFactory> builderFactories;
  
  final List<TypeAdapterFactory> builderHierarchyFactories;
  
  public Gson() {
    this(Excluder.DEFAULT, FieldNamingPolicy.IDENTITY, 
        Collections.emptyMap(), false, false, false, true, false, false, false, LongSerializationPolicy.DEFAULT, null, 2, 2, 
        
        Collections.emptyList(), Collections.emptyList(), 
        Collections.emptyList());
  }
  
  Gson(Excluder excluder, FieldNamingStrategy fieldNamingStrategy, Map<Type, InstanceCreator<?>> instanceCreators, boolean serializeNulls, boolean complexMapKeySerialization, boolean generateNonExecutableGson, boolean htmlSafe, boolean prettyPrinting, boolean lenient, boolean serializeSpecialFloatingPointValues, LongSerializationPolicy longSerializationPolicy, String datePattern, int dateStyle, int timeStyle, List<TypeAdapterFactory> builderFactories, List<TypeAdapterFactory> builderHierarchyFactories, List<TypeAdapterFactory> factoriesToBeAdded) {
    this.excluder = excluder;
    this.fieldNamingStrategy = fieldNamingStrategy;
    this.instanceCreators = instanceCreators;
    this.constructorConstructor = new ConstructorConstructor(instanceCreators);
    this.serializeNulls = serializeNulls;
    this.complexMapKeySerialization = complexMapKeySerialization;
    this.generateNonExecutableJson = generateNonExecutableGson;
    this.htmlSafe = htmlSafe;
    this.prettyPrinting = prettyPrinting;
    this.lenient = lenient;
    this.serializeSpecialFloatingPointValues = serializeSpecialFloatingPointValues;
    this.longSerializationPolicy = longSerializationPolicy;
    this.datePattern = datePattern;
    this.dateStyle = dateStyle;
    this.timeStyle = timeStyle;
    this.builderFactories = builderFactories;
    this.builderHierarchyFactories = builderHierarchyFactories;
    List<TypeAdapterFactory> factories = new ArrayList<TypeAdapterFactory>();
    factories.add(TypeAdapters.JSON_ELEMENT_FACTORY);
    factories.add(ObjectTypeAdapter.FACTORY);
    factories.add(excluder);
    factories.addAll(factoriesToBeAdded);
    factories.add(TypeAdapters.STRING_FACTORY);
    factories.add(TypeAdapters.INTEGER_FACTORY);
    factories.add(TypeAdapters.BOOLEAN_FACTORY);
    factories.add(TypeAdapters.BYTE_FACTORY);
    factories.add(TypeAdapters.SHORT_FACTORY);
    TypeAdapter<Number> longAdapter = longAdapter(longSerializationPolicy);
    factories.add(TypeAdapters.newFactory(long.class, Long.class, longAdapter));
    factories.add(TypeAdapters.newFactory(double.class, Double.class, 
          doubleAdapter(serializeSpecialFloatingPointValues)));
    factories.add(TypeAdapters.newFactory(float.class, Float.class, 
          floatAdapter(serializeSpecialFloatingPointValues)));
    factories.add(TypeAdapters.NUMBER_FACTORY);
    factories.add(TypeAdapters.ATOMIC_INTEGER_FACTORY);
    factories.add(TypeAdapters.ATOMIC_BOOLEAN_FACTORY);
    factories.add(TypeAdapters.newFactory(AtomicLong.class, atomicLongAdapter(longAdapter)));
    factories.add(TypeAdapters.newFactory(AtomicLongArray.class, atomicLongArrayAdapter(longAdapter)));
    factories.add(TypeAdapters.ATOMIC_INTEGER_ARRAY_FACTORY);
    factories.add(TypeAdapters.CHARACTER_FACTORY);
    factories.add(TypeAdapters.STRING_BUILDER_FACTORY);
    factories.add(TypeAdapters.STRING_BUFFER_FACTORY);
    factories.add(TypeAdapters.newFactory(BigDecimal.class, TypeAdapters.BIG_DECIMAL));
    factories.add(TypeAdapters.newFactory(BigInteger.class, TypeAdapters.BIG_INTEGER));
    factories.add(TypeAdapters.URL_FACTORY);
    factories.add(TypeAdapters.URI_FACTORY);
    factories.add(TypeAdapters.UUID_FACTORY);
    factories.add(TypeAdapters.CURRENCY_FACTORY);
    factories.add(TypeAdapters.LOCALE_FACTORY);
    factories.add(TypeAdapters.INET_ADDRESS_FACTORY);
    factories.add(TypeAdapters.BIT_SET_FACTORY);
    factories.add(DateTypeAdapter.FACTORY);
    factories.add(TypeAdapters.CALENDAR_FACTORY);
    factories.add(TimeTypeAdapter.FACTORY);
    factories.add(SqlDateTypeAdapter.FACTORY);
    factories.add(TypeAdapters.TIMESTAMP_FACTORY);
    factories.add(ArrayTypeAdapter.FACTORY);
    factories.add(TypeAdapters.CLASS_FACTORY);
    factories.add(new CollectionTypeAdapterFactory(this.constructorConstructor));
    factories.add(new MapTypeAdapterFactory(this.constructorConstructor, complexMapKeySerialization));
    this.jsonAdapterFactory = new JsonAdapterAnnotationTypeAdapterFactory(this.constructorConstructor);
    factories.add(this.jsonAdapterFactory);
    factories.add(TypeAdapters.ENUM_FACTORY);
    factories.add(new ReflectiveTypeAdapterFactory(this.constructorConstructor, fieldNamingStrategy, excluder, this.jsonAdapterFactory));
    this.factories = Collections.unmodifiableList(factories);
  }
  
  public GsonBuilder newBuilder() {
    return new GsonBuilder(this);
  }
  
  public Excluder excluder() {
    return this.excluder;
  }
  
  public FieldNamingStrategy fieldNamingStrategy() {
    return this.fieldNamingStrategy;
  }
  
  public boolean serializeNulls() {
    return this.serializeNulls;
  }
  
  public boolean htmlSafe() {
    return this.htmlSafe;
  }
  
  private TypeAdapter<Number> doubleAdapter(boolean serializeSpecialFloatingPointValues) {
    if (serializeSpecialFloatingPointValues)
      return TypeAdapters.DOUBLE; 
    return new TypeAdapter<Number>() {
        public Double read(JsonReader in) throws IOException {
          if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
          } 
          return Double.valueOf(in.nextDouble());
        }
        
        public void write(JsonWriter out, Number value) throws IOException {
          if (value == null) {
            out.nullValue();
            return;
          } 
          double doubleValue = value.doubleValue();
          Gson.checkValidFloatingPoint(doubleValue);
          out.value(value);
        }
      };
  }
  
  private TypeAdapter<Number> floatAdapter(boolean serializeSpecialFloatingPointValues) {
    if (serializeSpecialFloatingPointValues)
      return TypeAdapters.FLOAT; 
    return new TypeAdapter<Number>() {
        public Float read(JsonReader in) throws IOException {
          if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
          } 
          return Float.valueOf((float)in.nextDouble());
        }
        
        public void write(JsonWriter out, Number value) throws IOException {
          if (value == null) {
            out.nullValue();
            return;
          } 
          float floatValue = value.floatValue();
          Gson.checkValidFloatingPoint(floatValue);
          out.value(value);
        }
      };
  }
  
  static void checkValidFloatingPoint(double value) {
    if (Double.isNaN(value) || Double.isInfinite(value))
      throw new IllegalArgumentException(value + " is not a valid double value as per JSON specification. To override this behavior, use GsonBuilder.serializeSpecialFloatingPointValues() method."); 
  }
  
  private static TypeAdapter<Number> longAdapter(LongSerializationPolicy longSerializationPolicy) {
    if (longSerializationPolicy == LongSerializationPolicy.DEFAULT)
      return TypeAdapters.LONG; 
    return (TypeAdapter<Number>)new Object();
  }
  
  private static TypeAdapter<AtomicLong> atomicLongAdapter(final TypeAdapter<Number> longAdapter) {
    return (new TypeAdapter<AtomicLong>() {
        public void write(JsonWriter out, AtomicLong value) throws IOException {
          longAdapter.write(out, Long.valueOf(value.get()));
        }
        
        public AtomicLong read(JsonReader in) throws IOException {
          Number value = longAdapter.read(in);
          return new AtomicLong(value.longValue());
        }
      }).nullSafe();
  }
  
  private static TypeAdapter<AtomicLongArray> atomicLongArrayAdapter(final TypeAdapter<Number> longAdapter) {
    return (new TypeAdapter<AtomicLongArray>() {
        public void write(JsonWriter out, AtomicLongArray value) throws IOException {
          out.beginArray();
          for (int i = 0, length = value.length(); i < length; i++)
            longAdapter.write(out, Long.valueOf(value.get(i))); 
          out.endArray();
        }
        
        public AtomicLongArray read(JsonReader in) throws IOException {
          List<Long> list = new ArrayList<Long>();
          in.beginArray();
          while (in.hasNext()) {
            long value = ((Number)longAdapter.read(in)).longValue();
            list.add(Long.valueOf(value));
          } 
          in.endArray();
          int length = list.size();
          AtomicLongArray array = new AtomicLongArray(length);
          for (int i = 0; i < length; i++)
            array.set(i, ((Long)list.get(i)).longValue()); 
          return array;
        }
      }).nullSafe();
  }
  
  public <T> TypeAdapter<T> getAdapter(TypeToken<T> type) {
    TypeAdapter<?> cached = this.typeTokenCache.get((type == null) ? NULL_KEY_SURROGATE : type);
    if (cached != null)
      return (TypeAdapter)cached; 
    Map<TypeToken<?>, FutureTypeAdapter<?>> threadCalls = this.calls.get();
    boolean requiresThreadLocalCleanup = false;
    if (threadCalls == null) {
      threadCalls = new HashMap<TypeToken<?>, FutureTypeAdapter<?>>();
      this.calls.set(threadCalls);
      requiresThreadLocalCleanup = true;
    } 
    FutureTypeAdapter<T> ongoingCall = (FutureTypeAdapter<T>)threadCalls.get(type);
    if (ongoingCall != null)
      return ongoingCall; 
    try {
      FutureTypeAdapter<T> call = new FutureTypeAdapter<T>();
      threadCalls.put(type, call);
      for (TypeAdapterFactory factory : this.factories) {
        TypeAdapter<T> candidate = factory.create(this, type);
        if (candidate != null) {
          call.setDelegate(candidate);
          this.typeTokenCache.put(type, candidate);
          return candidate;
        } 
      } 
      throw new IllegalArgumentException("GSON (2.8.7) cannot handle " + type);
    } finally {
      threadCalls.remove(type);
      if (requiresThreadLocalCleanup)
        this.calls.remove(); 
    } 
  }
  
  public <T> TypeAdapter<T> getDelegateAdapter(TypeAdapterFactory skipPast, TypeToken<T> type) {
    JsonAdapterAnnotationTypeAdapterFactory jsonAdapterAnnotationTypeAdapterFactory;
    if (!this.factories.contains(skipPast))
      jsonAdapterAnnotationTypeAdapterFactory = this.jsonAdapterFactory; 
    boolean skipPastFound = false;
    for (TypeAdapterFactory factory : this.factories) {
      if (!skipPastFound) {
        if (factory == jsonAdapterAnnotationTypeAdapterFactory)
          skipPastFound = true; 
        continue;
      } 
      TypeAdapter<T> candidate = factory.create(this, type);
      if (candidate != null)
        return candidate; 
    } 
    throw new IllegalArgumentException("GSON cannot serialize " + type);
  }
  
  public <T> TypeAdapter<T> getAdapter(Class<T> type) {
    return getAdapter(TypeToken.get(type));
  }
  
  public JsonElement toJsonTree(Object src) {
    if (src == null)
      return JsonNull.INSTANCE; 
    return toJsonTree(src, src.getClass());
  }
  
  public JsonElement toJsonTree(Object src, Type typeOfSrc) {
    JsonTreeWriter writer = new JsonTreeWriter();
    toJson(src, typeOfSrc, (JsonWriter)writer);
    return writer.get();
  }
  
  public String toJson(Object src) {
    if (src == null)
      return toJson(JsonNull.INSTANCE); 
    return toJson(src, src.getClass());
  }
  
  public String toJson(Object src, Type typeOfSrc) {
    StringWriter writer = new StringWriter();
    toJson(src, typeOfSrc, writer);
    return writer.toString();
  }
  
  public void toJson(Object src, Appendable writer) throws JsonIOException {
    if (src != null) {
      toJson(src, src.getClass(), writer);
    } else {
      toJson(JsonNull.INSTANCE, writer);
    } 
  }
  
  public void toJson(Object src, Type typeOfSrc, Appendable writer) throws JsonIOException {
    try {
      JsonWriter jsonWriter = newJsonWriter(Streams.writerForAppendable(writer));
      toJson(src, typeOfSrc, jsonWriter);
    } catch (IOException e) {
      throw new JsonIOException(e);
    } 
  }
  
  public void toJson(Object src, Type typeOfSrc, JsonWriter writer) throws JsonIOException {
    TypeAdapter<?> adapter = getAdapter(TypeToken.get(typeOfSrc));
    boolean oldLenient = writer.isLenient();
    writer.setLenient(true);
    boolean oldHtmlSafe = writer.isHtmlSafe();
    writer.setHtmlSafe(this.htmlSafe);
    boolean oldSerializeNulls = writer.getSerializeNulls();
    writer.setSerializeNulls(this.serializeNulls);
    try {
      adapter.write(writer, src);
    } catch (IOException e) {
      throw new JsonIOException(e);
    } catch (AssertionError e) {
      AssertionError error = new AssertionError("AssertionError (GSON 2.8.7): " + e.getMessage());
      error.initCause(e);
      throw error;
    } finally {
      writer.setLenient(oldLenient);
      writer.setHtmlSafe(oldHtmlSafe);
      writer.setSerializeNulls(oldSerializeNulls);
    } 
  }
  
  public String toJson(JsonElement jsonElement) {
    StringWriter writer = new StringWriter();
    toJson(jsonElement, writer);
    return writer.toString();
  }
  
  public void toJson(JsonElement jsonElement, Appendable writer) throws JsonIOException {
    try {
      JsonWriter jsonWriter = newJsonWriter(Streams.writerForAppendable(writer));
      toJson(jsonElement, jsonWriter);
    } catch (IOException e) {
      throw new JsonIOException(e);
    } 
  }
  
  public JsonWriter newJsonWriter(Writer writer) throws IOException {
    if (this.generateNonExecutableJson)
      writer.write(")]}'\n"); 
    JsonWriter jsonWriter = new JsonWriter(writer);
    if (this.prettyPrinting)
      jsonWriter.setIndent("  "); 
    jsonWriter.setSerializeNulls(this.serializeNulls);
    return jsonWriter;
  }
  
  public JsonReader newJsonReader(Reader reader) {
    JsonReader jsonReader = new JsonReader(reader);
    jsonReader.setLenient(this.lenient);
    return jsonReader;
  }
  
  public void toJson(JsonElement jsonElement, JsonWriter writer) throws JsonIOException {
    boolean oldLenient = writer.isLenient();
    writer.setLenient(true);
    boolean oldHtmlSafe = writer.isHtmlSafe();
    writer.setHtmlSafe(this.htmlSafe);
    boolean oldSerializeNulls = writer.getSerializeNulls();
    writer.setSerializeNulls(this.serializeNulls);
    try {
      Streams.write(jsonElement, writer);
    } catch (IOException e) {
      throw new JsonIOException(e);
    } catch (AssertionError e) {
      AssertionError error = new AssertionError("AssertionError (GSON 2.8.7): " + e.getMessage());
      error.initCause(e);
      throw error;
    } finally {
      writer.setLenient(oldLenient);
      writer.setHtmlSafe(oldHtmlSafe);
      writer.setSerializeNulls(oldSerializeNulls);
    } 
  }
  
  public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
    Object object = fromJson(json, classOfT);
    return Primitives.wrap(classOfT).cast(object);
  }
  
  public <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
    if (json == null)
      return null; 
    StringReader reader = new StringReader(json);
    T target = fromJson(reader, typeOfT);
    return target;
  }
  
  public <T> T fromJson(Reader json, Class<T> classOfT) throws JsonSyntaxException, JsonIOException {
    JsonReader jsonReader = newJsonReader(json);
    Object object = fromJson(jsonReader, classOfT);
    assertFullConsumption(object, jsonReader);
    return Primitives.wrap(classOfT).cast(object);
  }
  
  public <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException, JsonSyntaxException {
    JsonReader jsonReader = newJsonReader(json);
    T object = fromJson(jsonReader, typeOfT);
    assertFullConsumption(object, jsonReader);
    return object;
  }
  
  private static void assertFullConsumption(Object obj, JsonReader reader) {
    try {
      if (obj != null && reader.peek() != JsonToken.END_DOCUMENT)
        throw new JsonIOException("JSON document was not fully consumed."); 
    } catch (MalformedJsonException e) {
      throw new JsonSyntaxException(e);
    } catch (IOException e) {
      throw new JsonIOException(e);
    } 
  }
  
  public <T> T fromJson(JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
    boolean isEmpty = true;
    boolean oldLenient = reader.isLenient();
    reader.setLenient(true);
    try {
      reader.peek();
      isEmpty = false;
      TypeToken<T> typeToken = TypeToken.get(typeOfT);
      TypeAdapter<T> typeAdapter = getAdapter(typeToken);
      T object = typeAdapter.read(reader);
      return object;
    } catch (EOFException e) {
      if (isEmpty)
        return null; 
      throw new JsonSyntaxException(e);
    } catch (IllegalStateException e) {
      throw new JsonSyntaxException(e);
    } catch (IOException e) {
      throw new JsonSyntaxException(e);
    } catch (AssertionError e) {
      AssertionError error = new AssertionError("AssertionError (GSON 2.8.7): " + e.getMessage());
      error.initCause(e);
      throw error;
    } finally {
      reader.setLenient(oldLenient);
    } 
  }
  
  public <T> T fromJson(JsonElement json, Class<T> classOfT) throws JsonSyntaxException {
    Object object = fromJson(json, classOfT);
    return Primitives.wrap(classOfT).cast(object);
  }
  
  public <T> T fromJson(JsonElement json, Type typeOfT) throws JsonSyntaxException {
    if (json == null)
      return null; 
    return fromJson((JsonReader)new JsonTreeReader(json), typeOfT);
  }
  
  static class FutureTypeAdapter<T> extends TypeAdapter<T> {
    private TypeAdapter<T> delegate;
    
    public void setDelegate(TypeAdapter<T> typeAdapter) {
      if (this.delegate != null)
        throw new AssertionError(); 
      this.delegate = typeAdapter;
    }
    
    public T read(JsonReader in) throws IOException {
      if (this.delegate == null)
        throw new IllegalStateException(); 
      return this.delegate.read(in);
    }
    
    public void write(JsonWriter out, T value) throws IOException {
      if (this.delegate == null)
        throw new IllegalStateException(); 
      this.delegate.write(out, value);
    }
  }
  
  public String toString() {
    return "{serializeNulls:" + this.serializeNulls + 
      ",factories:" + 
      this.factories + ",instanceCreators:" + 
      this.constructorConstructor + "}";
  }
}
