package sun.management;

import com.sun.management.VMOption;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

class Flag {
  private String name;
  
  private Object value;
  
  private VMOption.Origin origin;
  
  private boolean writeable;
  
  private boolean external;
  
  Flag(String paramString, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, VMOption.Origin paramOrigin) {
    this.name = paramString;
    this.value = (paramObject == null) ? "" : paramObject;
    this.origin = paramOrigin;
    this.writeable = paramBoolean1;
    this.external = paramBoolean2;
  }
  
  Object getValue() {
    return this.value;
  }
  
  boolean isWriteable() {
    return this.writeable;
  }
  
  boolean isExternal() {
    return this.external;
  }
  
  VMOption getVMOption() {
    return new VMOption(this.name, this.value.toString(), this.writeable, this.origin);
  }
  
  static Flag getFlag(String paramString) {
    String[] arrayOfString = new String[1];
    arrayOfString[0] = paramString;
    List<Flag> list = getFlags(arrayOfString, 1);
    if (list.isEmpty())
      return null; 
    return list.get(0);
  }
  
  static List<Flag> getAllFlags() {
    int i = getInternalFlagCount();
    return getFlags(null, i);
  }
  
  private static List<Flag> getFlags(String[] paramArrayOfString, int paramInt) {
    Flag[] arrayOfFlag = new Flag[paramInt];
    int i = getFlags(paramArrayOfString, arrayOfFlag, paramInt);
    ArrayList<Flag> arrayList = new ArrayList();
    for (Flag flag : arrayOfFlag) {
      if (flag != null)
        arrayList.add(flag); 
    } 
    return arrayList;
  }
  
  private static native String[] getAllFlagNames();
  
  private static native int getFlags(String[] paramArrayOfString, Flag[] paramArrayOfFlag, int paramInt);
  
  private static native int getInternalFlagCount();
  
  static synchronized native void setLongValue(String paramString, long paramLong);
  
  static synchronized native void setBooleanValue(String paramString, boolean paramBoolean);
  
  static synchronized native void setStringValue(String paramString1, String paramString2);
  
  private static native void initialize();
  
  static {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("management");
            return null;
          }
        });
    initialize();
  }
}
