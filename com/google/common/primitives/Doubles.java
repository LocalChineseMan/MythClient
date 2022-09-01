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
import java.util.regex.Pattern;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class Doubles {
  public static final int BYTES = 8;
  
  public static int hashCode(double value) {
    return Double.valueOf(value).hashCode();
  }
  
  public static int compare(double a, double b) {
    return Double.compare(a, b);
  }
  
  public static boolean isFinite(double value) {
    return ((Double.NEGATIVE_INFINITY < value)) & ((value < Double.POSITIVE_INFINITY));
  }
  
  public static boolean contains(double[] array, double target) {
    for (double value : array) {
      if (value == target)
        return true; 
    } 
    return false;
  }
  
  public static int indexOf(double[] array, double target) {
    return indexOf(array, target, 0, array.length);
  }
  
  private static int indexOf(double[] array, double target, int start, int end) {
    for (int i = start; i < end; i++) {
      if (array[i] == target)
        return i; 
    } 
    return -1;
  }
  
  public static int indexOf(double[] array, double[] target) {
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
  
  public static int lastIndexOf(double[] array, double target) {
    return lastIndexOf(array, target, 0, array.length);
  }
  
  private static int lastIndexOf(double[] array, double target, int start, int end) {
    for (int i = end - 1; i >= start; i--) {
      if (array[i] == target)
        return i; 
    } 
    return -1;
  }
  
  public static double min(double... array) {
    Preconditions.checkArgument((array.length > 0));
    double min = array[0];
    for (int i = 1; i < array.length; i++)
      min = Math.min(min, array[i]); 
    return min;
  }
  
  public static double max(double... array) {
    Preconditions.checkArgument((array.length > 0));
    double max = array[0];
    for (int i = 1; i < array.length; i++)
      max = Math.max(max, array[i]); 
    return max;
  }
  
  public static double[] concat(double[]... arrays) {
    int length = 0;
    for (double[] array : arrays)
      length += array.length; 
    double[] result = new double[length];
    int pos = 0;
    for (double[] array : arrays) {
      System.arraycopy(array, 0, result, pos, array.length);
      pos += array.length;
    } 
    return result;
  }
  
  @Beta
  public static Converter<String, Double> stringConverter() {
    return (Converter<String, Double>)DoubleConverter.INSTANCE;
  }
  
  public static double[] ensureCapacity(double[] array, int minLength, int padding) {
    Preconditions.checkArgument((minLength >= 0), "Invalid minLength: %s", new Object[] { Integer.valueOf(minLength) });
    Preconditions.checkArgument((padding >= 0), "Invalid padding: %s", new Object[] { Integer.valueOf(padding) });
    return (array.length < minLength) ? copyOf(array, minLength + padding) : array;
  }
  
  private static double[] copyOf(double[] original, int length) {
    double[] copy = new double[length];
    System.arraycopy(original, 0, copy, 0, Math.min(original.length, length));
    return copy;
  }
  
  public static String join(String separator, double... array) {
    Preconditions.checkNotNull(separator);
    if (array.length == 0)
      return ""; 
    StringBuilder builder = new StringBuilder(array.length * 12);
    builder.append(array[0]);
    for (int i = 1; i < array.length; i++)
      builder.append(separator).append(array[i]); 
    return builder.toString();
  }
  
  public static Comparator<double[]> lexicographicalComparator() {
    return (Comparator<double[]>)LexicographicalComparator.INSTANCE;
  }
  
  public static double[] toArray(Collection<? extends Number> collection) {
    if (collection instanceof DoubleArrayAsList)
      return ((DoubleArrayAsList)collection).toDoubleArray(); 
    Object[] boxedArray = collection.toArray();
    int len = boxedArray.length;
    double[] array = new double[len];
    for (int i = 0; i < len; i++)
      array[i] = ((Number)Preconditions.checkNotNull(boxedArray[i])).doubleValue(); 
    return array;
  }
  
  public static List<Double> asList(double... backingArray) {
    if (backingArray.length == 0)
      return Collections.emptyList(); 
    return (List<Double>)new DoubleArrayAsList(backingArray);
  }
  
  @GwtIncompatible("regular expressions")
  static final Pattern FLOATING_POINT_PATTERN = fpPattern();
  
  @GwtIncompatible("regular expressions")
  private static Pattern fpPattern() {
    String decimal = "(?:\\d++(?:\\.\\d*+)?|\\.\\d++)";
    String completeDec = decimal + "(?:[eE][+-]?\\d++)?[fFdD]?";
    String hex = "(?:\\p{XDigit}++(?:\\.\\p{XDigit}*+)?|\\.\\p{XDigit}++)";
    String completeHex = "0[xX]" + hex + "[pP][+-]?\\d++[fFdD]?";
    String fpPattern = "[+-]?(?:NaN|Infinity|" + completeDec + "|" + completeHex + ")";
    return Pattern.compile(fpPattern);
  }
  
  @Nullable
  @GwtIncompatible("regular expressions")
  @Beta
  public static Double tryParse(String string) {
    if (FLOATING_POINT_PATTERN.matcher(string).matches())
      try {
        return Double.valueOf(Double.parseDouble(string));
      } catch (NumberFormatException e) {} 
    return null;
  }
  
  private static class Doubles {}
  
  private enum Doubles {
  
  }
  
  private static final class Doubles {}
}
