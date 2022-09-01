package com.viaversion.viaversion.libs.gson.internal;

import com.viaversion.viaversion.libs.gson.InstanceCreator;
import com.viaversion.viaversion.libs.gson.internal.reflect.ReflectionAccessor;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;

public final class ConstructorConstructor {
  private final Map<Type, InstanceCreator<?>> instanceCreators;
  
  private final ReflectionAccessor accessor = ReflectionAccessor.getInstance();
  
  public ConstructorConstructor(Map<Type, InstanceCreator<?>> instanceCreators) {
    this.instanceCreators = instanceCreators;
  }
  
  public <T> ObjectConstructor<T> get(TypeToken<T> typeToken) {
    Type type = typeToken.getType();
    Class<? super T> rawType = typeToken.getRawType();
    InstanceCreator<T> typeCreator = (InstanceCreator<T>)this.instanceCreators.get(type);
    if (typeCreator != null)
      return (ObjectConstructor<T>)new Object(this, typeCreator, type); 
    InstanceCreator<T> rawTypeCreator = (InstanceCreator<T>)this.instanceCreators.get(rawType);
    if (rawTypeCreator != null)
      return (ObjectConstructor<T>)new Object(this, rawTypeCreator, type); 
    ObjectConstructor<T> defaultConstructor = newDefaultConstructor(rawType);
    if (defaultConstructor != null)
      return defaultConstructor; 
    ObjectConstructor<T> defaultImplementation = newDefaultImplementationConstructor(type, rawType);
    if (defaultImplementation != null)
      return defaultImplementation; 
    return newUnsafeAllocator(type, rawType);
  }
  
  private <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType) {
    try {
      final Constructor<? super T> constructor = rawType.getDeclaredConstructor(new Class[0]);
      if (!constructor.isAccessible())
        this.accessor.makeAccessible(constructor); 
      return new ObjectConstructor<T>() {
          public T construct() {
            try {
              Object[] args = null;
              return constructor.newInstance(args);
            } catch (InstantiationException e) {
              throw new RuntimeException("Failed to invoke " + constructor + " with no args", e);
            } catch (InvocationTargetException e) {
              throw new RuntimeException("Failed to invoke " + constructor + " with no args", e
                  .getTargetException());
            } catch (IllegalAccessException e) {
              throw new AssertionError(e);
            } 
          }
        };
    } catch (NoSuchMethodException e) {
      return null;
    } 
  }
  
  private <T> ObjectConstructor<T> newDefaultImplementationConstructor(Type type, Class<? super T> rawType) {
    if (Collection.class.isAssignableFrom(rawType)) {
      if (SortedSet.class.isAssignableFrom(rawType))
        return (ObjectConstructor<T>)new Object(this); 
      if (EnumSet.class.isAssignableFrom(rawType))
        return (ObjectConstructor<T>)new Object(this, type); 
      if (Set.class.isAssignableFrom(rawType))
        return (ObjectConstructor<T>)new Object(this); 
      if (Queue.class.isAssignableFrom(rawType))
        return (ObjectConstructor<T>)new Object(this); 
      return (ObjectConstructor<T>)new Object(this);
    } 
    if (Map.class.isAssignableFrom(rawType)) {
      if (ConcurrentNavigableMap.class.isAssignableFrom(rawType))
        return (ObjectConstructor<T>)new Object(this); 
      if (ConcurrentMap.class.isAssignableFrom(rawType))
        return (ObjectConstructor<T>)new Object(this); 
      if (SortedMap.class.isAssignableFrom(rawType))
        return (ObjectConstructor<T>)new Object(this); 
      if (type instanceof ParameterizedType && !String.class.isAssignableFrom(
          TypeToken.get(((ParameterizedType)type).getActualTypeArguments()[0]).getRawType()))
        return new ObjectConstructor<T>() {
            public T construct() {
              return (T)new LinkedHashMap<Object, Object>();
            }
          }; 
      return new ObjectConstructor<T>() {
          public T construct() {
            return (T)new LinkedTreeMap<Object, Object>();
          }
        };
    } 
    return null;
  }
  
  private <T> ObjectConstructor<T> newUnsafeAllocator(Type type, Class<? super T> rawType) {
    return (ObjectConstructor<T>)new Object(this, rawType, type);
  }
  
  public String toString() {
    return this.instanceCreators.toString();
  }
}