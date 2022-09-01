package com.viaversion.viaversion.libs.gson.internal.bind;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.TypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.internal.LinkedTreeMap;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonToken;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ObjectTypeAdapter extends TypeAdapter<Object> {
  public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (type.getRawType() == Object.class)
          return (TypeAdapter<T>)new ObjectTypeAdapter(gson); 
        return null;
      }
    };
  
  private final Gson gson;
  
  ObjectTypeAdapter(Gson gson) {
    this.gson = gson;
  }
  
  public Object read(JsonReader in) throws IOException {
    List<Object> list;
    LinkedTreeMap<String, Object> linkedTreeMap;
    JsonToken token = in.peek();
    switch (null.$SwitchMap$com$google$gson$stream$JsonToken[token.ordinal()]) {
      case 1:
        list = new ArrayList();
        in.beginArray();
        while (in.hasNext())
          list.add(read(in)); 
        in.endArray();
        return list;
      case 2:
        linkedTreeMap = new LinkedTreeMap();
        in.beginObject();
        while (in.hasNext())
          linkedTreeMap.put(in.nextName(), read(in)); 
        in.endObject();
        return linkedTreeMap;
      case 3:
        return in.nextString();
      case 4:
        return Double.valueOf(in.nextDouble());
      case 5:
        return Boolean.valueOf(in.nextBoolean());
      case 6:
        in.nextNull();
        return null;
    } 
    throw new IllegalStateException();
  }
  
  public void write(JsonWriter out, Object value) throws IOException {
    if (value == null) {
      out.nullValue();
      return;
    } 
    TypeAdapter<Object> typeAdapter = this.gson.getAdapter(value.getClass());
    if (typeAdapter instanceof ObjectTypeAdapter) {
      out.beginObject();
      out.endObject();
      return;
    } 
    typeAdapter.write(out, value);
  }
}
