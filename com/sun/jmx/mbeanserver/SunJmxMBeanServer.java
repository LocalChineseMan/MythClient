package com.sun.jmx.mbeanserver;

import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;

public interface SunJmxMBeanServer extends MBeanServer {
  MBeanInstantiator getMBeanInstantiator();
  
  boolean interceptorsEnabled();
  
  MBeanServer getMBeanServerInterceptor();
  
  void setMBeanServerInterceptor(MBeanServer paramMBeanServer);
  
  MBeanServerDelegate getMBeanServerDelegate();
}
