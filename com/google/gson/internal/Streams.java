package com.google.gson.internal;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;

public final class Streams {
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
    return (appendable instanceof Writer) ? (Writer)appendable : (Writer)new AppendableWriter(appendable, null);
  }
  
  private static final class Streams {}
}
