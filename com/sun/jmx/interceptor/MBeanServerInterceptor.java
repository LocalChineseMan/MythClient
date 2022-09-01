package com.sun.jmx.interceptor;

import java.io.ObjectInputStream;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.ReflectionException;
import javax.management.loading.ClassLoaderRepository;

public interface MBeanServerInterceptor extends MBeanServer {
  Object instantiate(String paramString) throws ReflectionException, MBeanException;
  
  Object instantiate(String paramString, ObjectName paramObjectName) throws ReflectionException, MBeanException, InstanceNotFoundException;
  
  Object instantiate(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, MBeanException;
  
  Object instantiate(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, MBeanException, InstanceNotFoundException;
  
  @Deprecated
  ObjectInputStream deserialize(ObjectName paramObjectName, byte[] paramArrayOfbyte) throws InstanceNotFoundException, OperationsException;
  
  @Deprecated
  ObjectInputStream deserialize(String paramString, byte[] paramArrayOfbyte) throws OperationsException, ReflectionException;
  
  @Deprecated
  ObjectInputStream deserialize(String paramString, ObjectName paramObjectName, byte[] paramArrayOfbyte) throws InstanceNotFoundException, OperationsException, ReflectionException;
  
  ClassLoaderRepository getClassLoaderRepository();
}
