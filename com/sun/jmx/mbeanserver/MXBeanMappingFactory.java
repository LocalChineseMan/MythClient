package com.sun.jmx.mbeanserver;

import java.lang.reflect.Type;
import javax.management.openmbean.OpenDataException;

public abstract class MXBeanMappingFactory {
  public static final MXBeanMappingFactory DEFAULT = new DefaultMXBeanMappingFactory();
  
  public abstract MXBeanMapping mappingForType(Type paramType, MXBeanMappingFactory paramMXBeanMappingFactory) throws OpenDataException;
}
