package javax.management.openmbean;

import java.util.Set;

public interface OpenMBeanParameterInfo {
  String getDescription();
  
  String getName();
  
  OpenType<?> getOpenType();
  
  Object getDefaultValue();
  
  Set<?> getLegalValues();
  
  Comparable<?> getMinValue();
  
  Comparable<?> getMaxValue();
  
  boolean hasDefaultValue();
  
  boolean hasLegalValues();
  
  boolean hasMinValue();
  
  boolean hasMaxValue();
  
  boolean isValue(Object paramObject);
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}
