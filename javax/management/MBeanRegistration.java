package javax.management;

public interface MBeanRegistration {
  ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception;
  
  void postRegister(Boolean paramBoolean);
  
  void preDeregister() throws Exception;
  
  void postDeregister();
}
