package com.sun.xml.internal.stream.util;

public class BufferAllocator {
  public static int SMALL_SIZE_LIMIT = 128;
  
  public static int MEDIUM_SIZE_LIMIT = 2048;
  
  public static int LARGE_SIZE_LIMIT = 8192;
  
  char[] smallCharBuffer;
  
  char[] mediumCharBuffer;
  
  char[] largeCharBuffer;
  
  byte[] smallByteBuffer;
  
  byte[] mediumByteBuffer;
  
  byte[] largeByteBuffer;
  
  public char[] getCharBuffer(int size) {
    if (size <= SMALL_SIZE_LIMIT) {
      char[] buffer = this.smallCharBuffer;
      this.smallCharBuffer = null;
      return buffer;
    } 
    if (size <= MEDIUM_SIZE_LIMIT) {
      char[] buffer = this.mediumCharBuffer;
      this.mediumCharBuffer = null;
      return buffer;
    } 
    if (size <= LARGE_SIZE_LIMIT) {
      char[] buffer = this.largeCharBuffer;
      this.largeCharBuffer = null;
      return buffer;
    } 
    return null;
  }
  
  public void returnCharBuffer(char[] c) {
    if (c == null)
      return; 
    if (c.length <= SMALL_SIZE_LIMIT) {
      this.smallCharBuffer = c;
    } else if (c.length <= MEDIUM_SIZE_LIMIT) {
      this.mediumCharBuffer = c;
    } else if (c.length <= LARGE_SIZE_LIMIT) {
      this.largeCharBuffer = c;
    } 
  }
  
  public byte[] getByteBuffer(int size) {
    if (size <= SMALL_SIZE_LIMIT) {
      byte[] buffer = this.smallByteBuffer;
      this.smallByteBuffer = null;
      return buffer;
    } 
    if (size <= MEDIUM_SIZE_LIMIT) {
      byte[] buffer = this.mediumByteBuffer;
      this.mediumByteBuffer = null;
      return buffer;
    } 
    if (size <= LARGE_SIZE_LIMIT) {
      byte[] buffer = this.largeByteBuffer;
      this.largeByteBuffer = null;
      return buffer;
    } 
    return null;
  }
  
  public void returnByteBuffer(byte[] b) {
    if (b == null)
      return; 
    if (b.length <= SMALL_SIZE_LIMIT) {
      this.smallByteBuffer = b;
    } else if (b.length <= MEDIUM_SIZE_LIMIT) {
      this.mediumByteBuffer = b;
    } else if (b.length <= LARGE_SIZE_LIMIT) {
      this.largeByteBuffer = b;
    } 
  }
}
