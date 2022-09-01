package javax.management;

import java.io.Serializable;

public interface QueryExp extends Serializable {
  boolean apply(ObjectName paramObjectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException;
  
  void setMBeanServer(MBeanServer paramMBeanServer);
}
