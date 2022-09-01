package sun.invoke.util;

import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class BytecodeDescriptor {
  public static List<Class<?>> parseMethod(String paramString, ClassLoader paramClassLoader) {
    return parseMethod(paramString, 0, paramString.length(), paramClassLoader);
  }
  
  static List<Class<?>> parseMethod(String paramString, int paramInt1, int paramInt2, ClassLoader paramClassLoader) {
    if (paramClassLoader == null)
      paramClassLoader = ClassLoader.getSystemClassLoader(); 
    String str = paramString;
    int[] arrayOfInt = { paramInt1 };
    ArrayList<Class<?>> arrayList = new ArrayList();
    if (arrayOfInt[0] < paramInt2 && str.charAt(arrayOfInt[0]) == '(') {
      arrayOfInt[0] = arrayOfInt[0] + 1;
      while (arrayOfInt[0] < paramInt2 && str.charAt(arrayOfInt[0]) != ')') {
        Class<?> clazz1 = parseSig(str, arrayOfInt, paramInt2, paramClassLoader);
        if (clazz1 == null || clazz1 == void.class)
          parseError(str, "bad argument type"); 
        arrayList.add(clazz1);
      } 
      arrayOfInt[0] = arrayOfInt[0] + 1;
    } else {
      parseError(str, "not a method type");
    } 
    Class<?> clazz = parseSig(str, arrayOfInt, paramInt2, paramClassLoader);
    if (clazz == null || arrayOfInt[0] != paramInt2)
      parseError(str, "bad return type"); 
    arrayList.add(clazz);
    return arrayList;
  }
  
  private static void parseError(String paramString1, String paramString2) {
    throw new IllegalArgumentException("bad signature: " + paramString1 + ": " + paramString2);
  }
  
  private static Class<?> parseSig(String paramString, int[] paramArrayOfint, int paramInt, ClassLoader paramClassLoader) {
    if (paramArrayOfint[0] == paramInt)
      return null; 
    paramArrayOfint[0] = paramArrayOfint[0] + 1;
    char c = paramString.charAt(paramArrayOfint[0]);
    if (c == 'L') {
      int i = paramArrayOfint[0], j = paramString.indexOf(';', i);
      if (j < 0)
        return null; 
      paramArrayOfint[0] = j + 1;
      String str = paramString.substring(i, j).replace('/', '.');
      try {
        return paramClassLoader.loadClass(str);
      } catch (ClassNotFoundException classNotFoundException) {
        throw new TypeNotPresentException(str, classNotFoundException);
      } 
    } 
    if (c == '[') {
      Class<?> clazz = parseSig(paramString, paramArrayOfint, paramInt, paramClassLoader);
      if (clazz != null)
        clazz = Array.newInstance(clazz, 0).getClass(); 
      return clazz;
    } 
    return Wrapper.forBasicType(c).primitiveType();
  }
  
  public static String unparse(Class<?> paramClass) {
    StringBuilder stringBuilder = new StringBuilder();
    unparseSig(paramClass, stringBuilder);
    return stringBuilder.toString();
  }
  
  public static String unparse(MethodType paramMethodType) {
    return unparseMethod(paramMethodType.returnType(), paramMethodType.parameterList());
  }
  
  public static String unparse(Object paramObject) {
    if (paramObject instanceof Class)
      return unparse((Class)paramObject); 
    if (paramObject instanceof MethodType)
      return unparse((MethodType)paramObject); 
    return (String)paramObject;
  }
  
  public static String unparseMethod(Class<?> paramClass, List<Class<?>> paramList) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('(');
    for (Class<?> clazz : paramList)
      unparseSig(clazz, stringBuilder); 
    stringBuilder.append(')');
    unparseSig(paramClass, stringBuilder);
    return stringBuilder.toString();
  }
  
  private static void unparseSig(Class<?> paramClass, StringBuilder paramStringBuilder) {
    char c = Wrapper.forBasicType(paramClass).basicTypeChar();
    if (c != 'L') {
      paramStringBuilder.append(c);
    } else {
      boolean bool = !paramClass.isArray() ? true : false;
      if (bool)
        paramStringBuilder.append('L'); 
      paramStringBuilder.append(paramClass.getName().replace('.', '/'));
      if (bool)
        paramStringBuilder.append(';'); 
    } 
  }
}
