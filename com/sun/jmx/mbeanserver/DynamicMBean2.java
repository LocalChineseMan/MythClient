package com.sun.jmx.mbeanserver;

import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public interface DynamicMBean2 extends DynamicMBean {
  Object getResource();
  
  String getClassName();
  
  void preRegister2(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception;
  
  void registerFailed();
}
