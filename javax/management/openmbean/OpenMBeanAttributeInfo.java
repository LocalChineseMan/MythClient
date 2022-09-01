package javax.management.openmbean;

public interface OpenMBeanAttributeInfo extends OpenMBeanParameterInfo {
  boolean isReadable();
  
  boolean isWritable();
  
  boolean isIs();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}
