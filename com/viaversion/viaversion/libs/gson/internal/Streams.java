package com.viaversion.viaversion.libs.gson.internal;

import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonIOException;
import com.viaversion.viaversion.libs.gson.JsonNull;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.JsonSyntaxException;
import com.viaversion.viaversion.libs.gson.internal.bind.TypeAdapters;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import com.viaversion.viaversion.libs.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;

public final class Streams {
  private Streams() {
    throw new UnsupportedOperationException();
  }
  
  public static JsonElement parse(JsonReader reader) throws JsonParseException {
    boolean isEmpty = true;
    try {
      reader.peek();
      isEmpty = false;
      return (JsonElement)TypeAdapters.JSON_ELEMENT.read(reader);
    } catch (EOFException e) {
      if (isEmpty)
        return (JsonElement)JsonNull.INSTANCE; 
      throw new JsonSyntaxException(e);
    } catch (MalformedJsonException e) {
      throw new JsonSyntaxException(e);
    } catch (IOException e) {
      throw new JsonIOException(e);
    } catch (NumberFormatException e) {
      throw new JsonSyntaxException(e);
    } 
  }
  
  public static void write(JsonElement element, JsonWriter writer) throws IOException {
    TypeAdapters.JSON_ELEMENT.write(writer, element);
  }
  
  public static Writer writerForAppendable(Appendable appendable) {
    return (appendable instanceof Writer) ? (Writer)appendable : (Writer)new AppendableWriter(appendable);
  }
  
  private static final class Streams {}
}
