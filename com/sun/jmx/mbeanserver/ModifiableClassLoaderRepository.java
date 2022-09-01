package com.sun.jmx.mbeanserver;

import javax.management.ObjectName;
import javax.management.loading.ClassLoaderRepository;

public interface ModifiableClassLoaderRepository extends ClassLoaderRepository {
  void addClassLoader(ClassLoader paramClassLoader);
  
  void removeClassLoader(ClassLoader paramClassLoader);
  
  void addClassLoader(ObjectName paramObjectName, ClassLoader paramClassLoader);
  
  void removeClassLoader(ObjectName paramObjectName);
  
  ClassLoader getClassLoader(ObjectName paramObjectName);
}
