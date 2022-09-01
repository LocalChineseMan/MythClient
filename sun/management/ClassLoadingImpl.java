package sun.management;

import java.lang.management.ClassLoadingMXBean;
import javax.management.ObjectName;

class ClassLoadingImpl implements ClassLoadingMXBean {
  private final VMManagement jvm;
  
  ClassLoadingImpl(VMManagement paramVMManagement) {
    this.jvm = paramVMManagement;
  }
  
  public long getTotalLoadedClassCount() {
    return this.jvm.getTotalClassCount();
  }
  
  public int getLoadedClassCount() {
    return this.jvm.getLoadedClassCount();
  }
  
  public long getUnloadedClassCount() {
    return this.jvm.getUnloadedClassCount();
  }
  
  public boolean isVerbose() {
    return this.jvm.getVerboseClass();
  }
  
  public void setVerbose(boolean paramBoolean) {
    Util.checkControlAccess();
    setVerboseClass(paramBoolean);
  }
  
  static native void setVerboseClass(boolean paramBoolean);
  
  public ObjectName getObjectName() {
    return Util.newObjectName("java.lang:type=ClassLoading");
  }
}
