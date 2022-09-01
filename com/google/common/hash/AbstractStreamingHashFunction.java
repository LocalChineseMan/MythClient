package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.nio.charset.Charset;

abstract class AbstractStreamingHashFunction implements HashFunction {
  public <T> HashCode hashObject(T instance, Funnel<? super T> funnel) {
    return newHasher().<T>putObject(instance, funnel).hash();
  }
  
  public HashCode hashUnencodedChars(CharSequence input) {
    return newHasher().putUnencodedChars(input).hash();
  }
  
  public HashCode hashString(CharSequence input, Charset charset) {
    return newHasher().putString(input, charset).hash();
  }
  
  public HashCode hashInt(int input) {
    return newHasher().putInt(input).hash();
  }
  
  public HashCode hashLong(long input) {
    return newHasher().putLong(input).hash();
  }
  
  public HashCode hashBytes(byte[] input) {
    return newHasher().putBytes(input).hash();
  }
  
  public HashCode hashBytes(byte[] input, int off, int len) {
    return newHasher().putBytes(input, off, len).hash();
  }
  
  public Hasher newHasher(int expectedInputSize) {
    Preconditions.checkArgument((expectedInputSize >= 0));
    return newHasher();
  }
  
  protected static abstract class AbstractStreamingHashFunction {}
}
