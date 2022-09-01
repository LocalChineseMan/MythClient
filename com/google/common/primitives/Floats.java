package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class Floats {
  public static final int BYTES = 4;
  
  public static int hashCode(float value) {
    return Float.valueOf(value).hashCode();
  }
  
  public static int compare(float a, float b) {
    return Float.compare(a, b);
  }
  
  public static boolean isFinite(float value) {
    return ((Float.NEGATIVE_INFINITY < value)) & ((value < Float.POSITIVE_INFINITY));
  }
  
  public static boolean contains(float[] array, float target) {
    for (float value : array) {
      if (value == target)
        return true; 
    } 
    return false;
  }
  
  public static int indexOf(float[] array, float target) {
    return indexOf(array, target, 0, array.length);
  }
  
  private static int indexOf(float[] array, float target, int start, int end) {
    for (int i = start; i < end; i++) {
      if (array[i] == target)
        return i; 
    } 
    return -1;
  }
  
  public static int indexOf(float[] array, float[] target) {
    Preconditions.checkNotNull(array, "array");
    Preconditions.checkNotNull(target, "target");
    if (target.length == 0)
      return 0; 
    for (int i = 0; i < array.length - target.length + 1; i++) {
      int j = 0;
      while (true) {
        if (j < target.length) {
          if (array[i + j] != target[j])
            break; 
          j++;
          continue;
        } 
        return i;
      } 
    } 
    return -1;
  }
  
  public static int lastIndexOf(float[] array, float target) {
    return lastIndexOf(array, target, 0, array.length);
  }
  
  private static int lastIndexOf(float[] array, float target, int start, int end) {
    for (int i = end - 1; i >= start; i--) {
      if (array[i] == target)
        return i; 
    } 
    return -1;
  }
  
  public static float min(float... array) {
    Preconditions.checkArgument((array.length > 0));
    float min = array[0];
    for (int i = 1; i < array.length; i++)
      min = Math.min(min, array[i]); 
    return min;
  }
  
  public static float max(float... array) {
    Preconditions.checkArgument((array.length > 0));
    float max = array[0];
    for (int i = 1; i < array.length; i++)
      max = Math.max(max, array[i]); 
    return max;
  }
  
  public static float[] concat(float[]... arrays) {
    int length = 0;
    for (float[] array : arrays)
      length += array.length; 
    float[] result = new float[length];
    int pos = 0;
    for (float[] array : arrays) {
      System.arraycopy(array, 0, result, pos, array.length);
      pos += array.length;
    } 
    return result;
  }
  
  @Beta
  public static Converter<String, Float> stringConverter() {
    return (Converter<String, Float>)FloatConverter.INSTANCE;
  }
  
  public static float[] ensureCapacity(float[] array, int minLength, int padding) {
    Preconditions.checkArgument((minLength >= 0), "Invalid minLength: %s", new Object[] { Integer.valueOf(minLength) });
    Preconditions.checkArgument((padding >= 0), "Invalid padding: %s", new Object[] { Integer.valueOf(padding) });
    return (array.length < minLength) ? copyOf(array, minLength + padding) : array;
  }
  
  private static float[] copyOf(float[] original, int length) {
    float[] copy = new float[length];
    System.arraycopy(original, 0, copy, 0, Math.min(original.length, length));
    return copy;
  }
  
  public static String join(String separator, float... array) {
    Preconditions.checkNotNull(separator);
    if (array.length == 0)
      return ""; 
    StringBuilder builder = new StringBuilder(array.length * 12);
    builder.append(array[0]);
    for (int i = 1; i < array.length; i++)
      builder.append(separator).append(array[i]); 
    return builder.toString();
  }
  
  public static Comparator<float[]> lexicographicalComparator() {
    return (Comparator<float[]>)LexicographicalComparator.INSTANCE;
  }
  
  public static float[] toArray(Collection<? extends Number> collection) {
    if (collection instanceof FloatArrayAsList)
      return ((FloatArrayAsList)collection).toFloatArray(); 
    Object[] boxedArray = collection.toArray();
    int len = boxedArray.length;
    float[] array = new float[len];
    for (int i = 0; i < len; i++)
      array[i] = ((Number)Preconditions.checkNotNull(boxedArray[i])).floatValue(); 
    return array;
  }
  
  public static List<Float> asList(float... backingArray) {
    if (backingArray.length == 0)
      return Collections.emptyList(); 
    return (List<Float>)new FloatArrayAsList(backingArray);
  }
  
  @Nullable
  @GwtIncompatible("regular expressions")
  @Beta
  public static Float tryParse(String string) {
    if (Doubles.FLOATING_POINT_PATTERN.matcher(string).matches())
      try {
        return Float.valueOf(Float.parseFloat(string));
      } catch (NumberFormatException e) {} 
    return null;
  }
  
  private static class Floats {}
  
  private enum Floats {
  
  }
  
  private static final class Floats {}
}
