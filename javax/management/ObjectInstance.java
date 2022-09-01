package javax.management;

import java.io.Serializable;

public class ObjectInstance implements Serializable {
  private static final long serialVersionUID = -4099952623687795850L;
  
  private ObjectName name;
  
  private String className;
  
  public ObjectInstance(String paramString1, String paramString2) throws MalformedObjectNameException {
    this(new ObjectName(paramString1), paramString2);
  }
  
  public ObjectInstance(ObjectName paramObjectName, String paramString) {
    if (paramObjectName.isPattern()) {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Invalid name->" + paramObjectName.toString());
      throw new RuntimeOperationsException(illegalArgumentException);
    } 
    this.name = paramObjectName;
    this.className = paramString;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof ObjectInstance))
      return false; 
    ObjectInstance objectInstance = (ObjectInstance)paramObject;
    if (!this.name.equals(objectInstance.getObjectName()))
      return false; 
    if (this.className == null)
      return (objectInstance.getClassName() == null); 
    return this.className.equals(objectInstance.getClassName());
  }
  
  public int hashCode() {
    boolean bool = (this.className == null) ? false : this.className.hashCode();
    return this.name.hashCode() ^ bool;
  }
  
  public ObjectName getObjectName() {
    return this.name;
  }
  
  public String getClassName() {
    return this.className;
  }
  
  public String toString() {
    return getClassName() + "[" + getObjectName() + "]";
  }
}
