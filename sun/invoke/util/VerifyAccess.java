package sun.invoke.util;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import sun.reflect.Reflection;

public class VerifyAccess {
  private static final int PACKAGE_ONLY = 0;
  
  private static final int PACKAGE_ALLOWED = 8;
  
  private static final int PROTECTED_OR_PACKAGE_ALLOWED = 12;
  
  private static final int ALL_ACCESS_MODES = 7;
  
  private static final boolean ALLOW_NESTMATE_ACCESS = false;
  
  public static boolean isMemberAccessible(Class<?> paramClass1, Class<?> paramClass2, int paramInt1, Class<?> paramClass3, int paramInt2) {
    if (paramInt2 == 0)
      return false; 
    assert (paramInt2 & 0x1) != 0 && (paramInt2 & 0xFFFFFFF0) == 0;
    if (!isClassAccessible(paramClass1, paramClass3, paramInt2))
      return false; 
    if (paramClass2 == paramClass3 && (paramInt2 & 0x2) != 0)
      return true; 
    switch (paramInt1 & 0x7) {
      case 1:
        return true;
      case 4:
        assert !paramClass2.isInterface();
        if ((paramInt2 & 0xC) != 0 && 
          isSamePackage(paramClass2, paramClass3))
          return true; 
        if ((paramInt2 & 0x4) == 0)
          return false; 
        if ((paramInt1 & 0x8) != 0 && 
          !isRelatedClass(paramClass1, paramClass3))
          return false; 
        if ((paramInt2 & 0x4) != 0 && 
          isSubClass(paramClass3, paramClass2))
          return true; 
        return false;
      case 0:
        assert !paramClass2.isInterface();
        return ((paramInt2 & 0x8) != 0 && 
          isSamePackage(paramClass2, paramClass3));
      case 2:
        return false;
    } 
    throw new IllegalArgumentException("bad modifiers: " + Modifier.toString(paramInt1));
  }
  
  static boolean isRelatedClass(Class<?> paramClass1, Class<?> paramClass2) {
    return (paramClass1 == paramClass2 || 
      isSubClass(paramClass1, paramClass2) || 
      isSubClass(paramClass2, paramClass1));
  }
  
  static boolean isSubClass(Class<?> paramClass1, Class<?> paramClass2) {
    return (paramClass2.isAssignableFrom(paramClass1) && 
      !paramClass1.isInterface());
  }
  
  static int getClassModifiers(Class<?> paramClass) {
    if (paramClass.isArray() || paramClass.isPrimitive())
      return paramClass.getModifiers(); 
    return Reflection.getClassAccessFlags(paramClass);
  }
  
  public static boolean isClassAccessible(Class<?> paramClass1, Class<?> paramClass2, int paramInt) {
    if (paramInt == 0)
      return false; 
    assert (paramInt & 0x1) != 0 && (paramInt & 0xFFFFFFF0) == 0;
    int i = getClassModifiers(paramClass1);
    if (Modifier.isPublic(i))
      return true; 
    if ((paramInt & 0x8) != 0 && 
      isSamePackage(paramClass2, paramClass1))
      return true; 
    return false;
  }
  
  public static boolean isTypeVisible(Class<?> paramClass1, Class<?> paramClass2) {
    if (paramClass1 == paramClass2)
      return true; 
    for (; paramClass1.isArray(); paramClass1 = paramClass1.getComponentType());
    if (paramClass1.isPrimitive() || paramClass1 == Object.class)
      return true; 
    ClassLoader classLoader1 = paramClass1.getClassLoader();
    if (classLoader1 == null)
      return true; 
    ClassLoader classLoader2 = paramClass2.getClassLoader();
    if (classLoader2 == null)
      return false; 
    if (classLoader1 == classLoader2 || loadersAreRelated(classLoader1, classLoader2, true))
      return true; 
    try {
      Class<?> clazz = classLoader2.loadClass(paramClass1.getName());
      return (paramClass1 == clazz);
    } catch (ClassNotFoundException classNotFoundException) {
      return false;
    } 
  }
  
  public static boolean isTypeVisible(MethodType paramMethodType, Class<?> paramClass) {
    byte b;
    int i;
    for (b = -1, i = paramMethodType.parameterCount(); b < i; b++) {
      Class<?> clazz = (b < 0) ? paramMethodType.returnType() : paramMethodType.parameterType(b);
      if (!isTypeVisible(clazz, paramClass))
        return false; 
    } 
    return true;
  }
  
  public static boolean isSamePackage(Class<?> paramClass1, Class<?> paramClass2) {
    assert !paramClass1.isArray() && !paramClass2.isArray();
    if (paramClass1 == paramClass2)
      return true; 
    if (paramClass1.getClassLoader() != paramClass2.getClassLoader())
      return false; 
    String str1 = paramClass1.getName(), str2 = paramClass2.getName();
    int i = str1.lastIndexOf('.');
    if (i != str2.lastIndexOf('.'))
      return false; 
    for (byte b = 0; b < i; b++) {
      if (str1.charAt(b) != str2.charAt(b))
        return false; 
    } 
    return true;
  }
  
  public static String getPackageName(Class<?> paramClass) {
    assert !paramClass.isArray();
    String str = paramClass.getName();
    int i = str.lastIndexOf('.');
    if (i < 0)
      return ""; 
    return str.substring(0, i);
  }
  
  public static boolean isSamePackageMember(Class<?> paramClass1, Class<?> paramClass2) {
    if (paramClass1 == paramClass2)
      return true; 
    if (!isSamePackage(paramClass1, paramClass2))
      return false; 
    if (getOutermostEnclosingClass(paramClass1) != getOutermostEnclosingClass(paramClass2))
      return false; 
    return true;
  }
  
  private static Class<?> getOutermostEnclosingClass(Class<?> paramClass) {
    Class<?> clazz1 = paramClass;
    for (Class<?> clazz2 = paramClass; (clazz2 = clazz2.getEnclosingClass()) != null;)
      clazz1 = clazz2; 
    return clazz1;
  }
  
  private static boolean loadersAreRelated(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2, boolean paramBoolean) {
    if (paramClassLoader1 == paramClassLoader2 || paramClassLoader1 == null || (paramClassLoader2 == null && !paramBoolean))
      return true; 
    ClassLoader classLoader = paramClassLoader2;
    for (; classLoader != null; classLoader = classLoader.getParent()) {
      if (classLoader == paramClassLoader1)
        return true; 
    } 
    if (paramBoolean)
      return false; 
    classLoader = paramClassLoader1;
    for (; classLoader != null; classLoader = classLoader.getParent()) {
      if (classLoader == paramClassLoader2)
        return true; 
    } 
    return false;
  }
  
  public static boolean classLoaderIsAncestor(Class<?> paramClass1, Class<?> paramClass2) {
    return loadersAreRelated(paramClass1.getClassLoader(), paramClass2.getClassLoader(), true);
  }
}
