package javax.management;

public interface MBeanServerDelegateMBean {
  String getMBeanServerId();
  
  String getSpecificationName();
  
  String getSpecificationVersion();
  
  String getSpecificationVendor();
  
  String getImplementationName();
  
  String getImplementationVersion();
  
  String getImplementationVendor();
}
