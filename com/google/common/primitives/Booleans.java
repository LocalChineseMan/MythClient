package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@GwtCompatible
public final class Booleans {
  public static int hashCode(boolean value) {
    return value ? 1231 : 1237;
  }
  
  public static int compare(boolean a, boolean b) {
    return (a == b) ? 0 : (a ? 1 : -1);
  }
  
  public static boolean contains(boolean[] array, boolean target) {
    for (boolean value : array) {
      if (value == target)
        return true; 
    } 
    return false;
  }
  
  public static int indexOf(boolean[] array, boolean target) {
    return indexOf(array, target, 0, array.length);
  }
  
  private static int indexOf(boolean[] array, boolean target, int start, int end) {
    for (int i = start; i < end; i++) {
      if (array[i] == target)
        return i; 
    } 
    return -1;
  }
  
  public static int indexOf(boolean[] array, boolean[] target) {
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
  
  public static int lastIndexOf(boolean[] array, boolean target) {
    return lastIndexOf(array, target, 0, array.length);
  }
  
  private static int lastIndexOf(boolean[] array, boolean target, int start, int end) {
    for (int i = end - 1; i >= start; i--) {
      if (array[i] == target)
        return i; 
    } 
    return -1;
  }
  
  public static boolean[] concat(boolean[]... arrays) {
    int length = 0;
    for (boolean[] array : arrays)
      length += array.length; 
    boolean[] result = new boolean[length];
    int pos = 0;
    for (boolean[] array : arrays) {
      System.arraycopy(array, 0, result, pos, array.length);
      pos += array.length;
    } 
    return result;
  }
  
  public static boolean[] ensureCapacity(boolean[] array, int minLength, int padding) {
    Preconditions.checkArgument((minLength >= 0), "Invalid minLength: %s", new Object[] { Integer.valueOf(minLength) });
    Preconditions.checkArgument((padding >= 0), "Invalid padding: %s", new Object[] { Integer.valueOf(padding) });
    return (array.length < minLength) ? copyOf(array, minLength + padding) : array;
  }
  
  private static boolean[] copyOf(boolean[] original, int length) {
    boolean[] copy = new boolean[length];
    System.arraycopy(original, 0, copy, 0, Math.min(original.length, length));
    return copy;
  }
  
  public static String join(String separator, boolean... array) {
    Preconditions.checkNotNull(separator);
    if (array.length == 0)
      return ""; 
    StringBuilder builder = new StringBuilder(array.length * 7);
    builder.append(array[0]);
    for (int i = 1; i < array.length; i++)
      builder.append(separator).append(array[i]); 
    return builder.toString();
  }
  
  public static Comparator<boolean[]> lexicographicalComparator() {
    return (Comparator<boolean[]>)LexicographicalComparator.INSTANCE;
  }
  
  public static boolean[] toArray(Collection<Boolean> collection) {
    if (collection instanceof BooleanArrayAsList)
      return ((BooleanArrayAsList)collection).toBooleanArray(); 
    Object[] boxedArray = collection.toArray();
    int len = boxedArray.length;
    boolean[] array = new boolean[len];
    for (int i = 0; i < len; i++)
      array[i] = ((Boolean)Preconditions.checkNotNull(boxedArray[i])).booleanValue(); 
    return array;
  }
  
  public static List<Boolean> asList(boolean... backingArray) {
    if (backingArray.length == 0)
      return Collections.emptyList(); 
    return (List<Boolean>)new BooleanArrayAsList(backingArray);
  }
  
  @Beta
  public static int countTrue(boolean... values) {
    int count = 0;
    for (boolean value : values) {
      if (value)
        count++; 
    } 
    return count;
  }
  
  private static class Booleans {}
  
  private enum Booleans {
  
  }
}
