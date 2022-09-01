package com.viaversion.viaversion.api.minecraft.nbt;

import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class BinaryTagIO {
  public static CompoundTag readPath(Path path) throws IOException {
    return readInputStream(Files.newInputStream(path, new java.nio.file.OpenOption[0]));
  }
  
  public static CompoundTag readInputStream(InputStream input) throws IOException {
    DataInputStream dis = new DataInputStream(input);
    try {
      CompoundTag compoundTag = readDataInput(dis);
      dis.close();
      return compoundTag;
    } catch (Throwable throwable) {
      try {
        dis.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public static CompoundTag readCompressedPath(Path path) throws IOException {
    return readCompressedInputStream(Files.newInputStream(path, new java.nio.file.OpenOption[0]));
  }
  
  public static CompoundTag readCompressedInputStream(InputStream input) throws IOException {
    DataInputStream dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(input)));
    try {
      CompoundTag compoundTag = readDataInput(dis);
      dis.close();
      return compoundTag;
    } catch (Throwable throwable) {
      try {
        dis.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public static CompoundTag readDataInput(DataInput input) throws IOException {
    byte type = input.readByte();
    if (type != 10)
      throw new IOException(String.format("Expected root tag to be a CompoundTag, was %s", new Object[] { Byte.valueOf(type) })); 
    input.skipBytes(input.readUnsignedShort());
    CompoundTag compoundTag = new CompoundTag();
    compoundTag.read(input);
    return compoundTag;
  }
  
  public static void writePath(CompoundTag tag, Path path) throws IOException {
    writeOutputStream(tag, Files.newOutputStream(path, new java.nio.file.OpenOption[0]));
  }
  
  public static void writeOutputStream(CompoundTag tag, OutputStream output) throws IOException {
    DataOutputStream dos = new DataOutputStream(output);
    try {
      writeDataOutput(tag, dos);
      dos.close();
    } catch (Throwable throwable) {
      try {
        dos.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public static void writeCompressedPath(CompoundTag tag, Path path) throws IOException {
    writeCompressedOutputStream(tag, Files.newOutputStream(path, new java.nio.file.OpenOption[0]));
  }
  
  public static void writeCompressedOutputStream(CompoundTag tag, OutputStream output) throws IOException {
    DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(output));
    try {
      writeDataOutput(tag, dos);
      dos.close();
    } catch (Throwable throwable) {
      try {
        dos.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public static void writeDataOutput(CompoundTag tag, DataOutput output) throws IOException {
    output.writeByte(10);
    output.writeUTF("");
    tag.write(output);
  }
  
  public static CompoundTag readString(String input) throws IOException {
    try {
      CharBuffer buffer = new CharBuffer(input);
      TagStringReader parser = new TagStringReader(buffer);
      CompoundTag tag = parser.compound();
      if (buffer.skipWhitespace().hasMore())
        throw new IOException("Document had trailing content after first CompoundTag"); 
      return tag;
    } catch (StringTagParseException ex) {
      throw new IOException(ex);
    } 
  }
  
  public static String writeString(CompoundTag tag) throws IOException {
    StringBuilder sb = new StringBuilder();
    TagStringWriter emit = new TagStringWriter(sb);
    try {
      emit.writeTag((Tag)tag);
      emit.close();
    } catch (Throwable throwable) {
      try {
        emit.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return sb.toString();
  }
}
