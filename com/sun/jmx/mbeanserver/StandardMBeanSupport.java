package com.sun.jmx.mbeanserver;

import java.lang.reflect.Method;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class StandardMBeanSupport extends MBeanSupport<Method> {
  public <T> StandardMBeanSupport(T paramT, Class<T> paramClass) throws NotCompliantMBeanException {
    super(paramT, paramClass);
  }
  
  MBeanIntrospector<Method> getMBeanIntrospector() {
    return StandardMBeanIntrospector.getInstance();
  }
  
  Object getCookie() {
    return null;
  }
  
  public void register(MBeanServer paramMBeanServer, ObjectName paramObjectName) {}
  
  public void unregister() {}
  
  public MBeanInfo getMBeanInfo() {
    MBeanInfo mBeanInfo = super.getMBeanInfo();
    Class<?> clazz = getResource().getClass();
    if (StandardMBeanIntrospector.isDefinitelyImmutableInfo(clazz))
      return mBeanInfo; 
    return new MBeanInfo(mBeanInfo.getClassName(), mBeanInfo.getDescription(), mBeanInfo
        .getAttributes(), mBeanInfo.getConstructors(), mBeanInfo
        .getOperations(), 
        MBeanIntrospector.findNotifications(getResource()), mBeanInfo
        .getDescriptor());
  }
}
