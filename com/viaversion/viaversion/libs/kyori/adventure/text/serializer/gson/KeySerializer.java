package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import com.viaversion.viaversion.libs.kyori.adventure.key.Key;
import java.io.IOException;

final class KeySerializer extends TypeAdapter<Key> {
  static final TypeAdapter<Key> INSTANCE = (new KeySerializer()).nullSafe();
  
  public void write(JsonWriter out, Key value) throws IOException {
    out.value(value.asString());
  }
  
  public Key read(JsonReader in) throws IOException {
    return Key.key(in.nextString());
  }
}
