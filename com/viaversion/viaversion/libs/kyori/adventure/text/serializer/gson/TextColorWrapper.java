package com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson;

import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.JsonSyntaxException;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextColor;
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextDecoration;
import java.io.IOException;
import org.jetbrains.annotations.Nullable;

final class TextColorWrapper {
  @Nullable
  final TextColor color;
  
  @Nullable
  final TextDecoration decoration;
  
  final boolean reset;
  
  TextColorWrapper(@Nullable TextColor color, @Nullable TextDecoration decoration, boolean reset) {
    this.color = color;
    this.decoration = decoration;
    this.reset = reset;
  }
  
  static class Serializer extends TypeAdapter<TextColorWrapper> {
    public void write(JsonWriter out, TextColorWrapper value) {
      throw new JsonSyntaxException("Cannot write TextColorWrapper instances");
    }
    
    public TextColorWrapper read(JsonReader in) throws IOException {
      String input = in.nextString();
      TextColor color = TextColorSerializer.fromString(input);
      TextDecoration decoration = (TextDecoration)TextDecoration.NAMES.value(input);
      boolean reset = (decoration == null && input.equals("reset"));
      if (color == null && decoration == null && !reset)
        throw new JsonParseException("Don't know how to parse " + input + " at " + in.getPath()); 
      return new TextColorWrapper(color, decoration, reset);
    }
  }
}
