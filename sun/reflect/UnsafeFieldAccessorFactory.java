package sun.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class UnsafeFieldAccessorFactory {
  static FieldAccessor newFieldAccessor(Field paramField, boolean paramBoolean) {
    Class<?> clazz = paramField.getType();
    boolean bool1 = Modifier.isStatic(paramField.getModifiers());
    boolean bool2 = Modifier.isFinal(paramField.getModifiers());
    boolean bool3 = Modifier.isVolatile(paramField.getModifiers());
    boolean bool4 = (bool2 || bool3) ? true : false;
    boolean bool5 = (bool2 && (bool1 || !paramBoolean)) ? true : false;
    if (bool1) {
      UnsafeFieldAccessorImpl.unsafe.ensureClassInitialized(paramField.getDeclaringClass());
      if (!bool4) {
        if (clazz == boolean.class)
          return (FieldAccessor)new UnsafeStaticBooleanFieldAccessorImpl(paramField); 
        if (clazz == byte.class)
          return (FieldAccessor)new UnsafeStaticByteFieldAccessorImpl(paramField); 
        if (clazz == short.class)
          return (FieldAccessor)new UnsafeStaticShortFieldAccessorImpl(paramField); 
        if (clazz == char.class)
          return (FieldAccessor)new UnsafeStaticCharacterFieldAccessorImpl(paramField); 
        if (clazz == int.class)
          return (FieldAccessor)new UnsafeStaticIntegerFieldAccessorImpl(paramField); 
        if (clazz == long.class)
          return (FieldAccessor)new UnsafeStaticLongFieldAccessorImpl(paramField); 
        if (clazz == float.class)
          return (FieldAccessor)new UnsafeStaticFloatFieldAccessorImpl(paramField); 
        if (clazz == double.class)
          return (FieldAccessor)new UnsafeStaticDoubleFieldAccessorImpl(paramField); 
        return (FieldAccessor)new UnsafeStaticObjectFieldAccessorImpl(paramField);
      } 
      if (clazz == boolean.class)
        return (FieldAccessor)new UnsafeQualifiedStaticBooleanFieldAccessorImpl(paramField, bool5); 
      if (clazz == byte.class)
        return (FieldAccessor)new UnsafeQualifiedStaticByteFieldAccessorImpl(paramField, bool5); 
      if (clazz == short.class)
        return (FieldAccessor)new UnsafeQualifiedStaticShortFieldAccessorImpl(paramField, bool5); 
      if (clazz == char.class)
        return (FieldAccessor)new UnsafeQualifiedStaticCharacterFieldAccessorImpl(paramField, bool5); 
      if (clazz == int.class)
        return (FieldAccessor)new UnsafeQualifiedStaticIntegerFieldAccessorImpl(paramField, bool5); 
      if (clazz == long.class)
        return (FieldAccessor)new UnsafeQualifiedStaticLongFieldAccessorImpl(paramField, bool5); 
      if (clazz == float.class)
        return (FieldAccessor)new UnsafeQualifiedStaticFloatFieldAccessorImpl(paramField, bool5); 
      if (clazz == double.class)
        return (FieldAccessor)new UnsafeQualifiedStaticDoubleFieldAccessorImpl(paramField, bool5); 
      return (FieldAccessor)new UnsafeQualifiedStaticObjectFieldAccessorImpl(paramField, bool5);
    } 
    if (!bool4) {
      if (clazz == boolean.class)
        return (FieldAccessor)new UnsafeBooleanFieldAccessorImpl(paramField); 
      if (clazz == byte.class)
        return (FieldAccessor)new UnsafeByteFieldAccessorImpl(paramField); 
      if (clazz == short.class)
        return (FieldAccessor)new UnsafeShortFieldAccessorImpl(paramField); 
      if (clazz == char.class)
        return (FieldAccessor)new UnsafeCharacterFieldAccessorImpl(paramField); 
      if (clazz == int.class)
        return (FieldAccessor)new UnsafeIntegerFieldAccessorImpl(paramField); 
      if (clazz == long.class)
        return (FieldAccessor)new UnsafeLongFieldAccessorImpl(paramField); 
      if (clazz == float.class)
        return (FieldAccessor)new UnsafeFloatFieldAccessorImpl(paramField); 
      if (clazz == double.class)
        return (FieldAccessor)new UnsafeDoubleFieldAccessorImpl(paramField); 
      return (FieldAccessor)new UnsafeObjectFieldAccessorImpl(paramField);
    } 
    if (clazz == boolean.class)
      return (FieldAccessor)new UnsafeQualifiedBooleanFieldAccessorImpl(paramField, bool5); 
    if (clazz == byte.class)
      return (FieldAccessor)new UnsafeQualifiedByteFieldAccessorImpl(paramField, bool5); 
    if (clazz == short.class)
      return (FieldAccessor)new UnsafeQualifiedShortFieldAccessorImpl(paramField, bool5); 
    if (clazz == char.class)
      return (FieldAccessor)new UnsafeQualifiedCharacterFieldAccessorImpl(paramField, bool5); 
    if (clazz == int.class)
      return (FieldAccessor)new UnsafeQualifiedIntegerFieldAccessorImpl(paramField, bool5); 
    if (clazz == long.class)
      return (FieldAccessor)new UnsafeQualifiedLongFieldAccessorImpl(paramField, bool5); 
    if (clazz == float.class)
      return (FieldAccessor)new UnsafeQualifiedFloatFieldAccessorImpl(paramField, bool5); 
    if (clazz == double.class)
      return (FieldAccessor)new UnsafeQualifiedDoubleFieldAccessorImpl(paramField, bool5); 
    return (FieldAccessor)new UnsafeQualifiedObjectFieldAccessorImpl(paramField, bool5);
  }
}
