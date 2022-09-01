package javax.management.openmbean;

import javax.management.MBeanParameterInfo;

public interface OpenMBeanOperationInfo {
  String getDescription();
  
  String getName();
  
  MBeanParameterInfo[] getSignature();
  
  int getImpact();
  
  String getReturnType();
  
  OpenType<?> getReturnOpenType();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}
