package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Objects;

public class MBeanNotificationInfo extends MBeanFeatureInfo implements Cloneable {
  static final long serialVersionUID = -3888371564530107064L;
  
  private static final String[] NO_TYPES = new String[0];
  
  static final MBeanNotificationInfo[] NO_NOTIFICATIONS = new MBeanNotificationInfo[0];
  
  private String[] types;
  
  private final transient boolean arrayGettersSafe;
  
  public MBeanNotificationInfo(String[] paramArrayOfString, String paramString1, String paramString2) {
    this(paramArrayOfString, paramString1, paramString2, (Descriptor)null);
  }
  
  public MBeanNotificationInfo(String[] paramArrayOfString, String paramString1, String paramString2, Descriptor paramDescriptor) {
    super(paramString1, paramString2, paramDescriptor);
    this
      .types = (paramArrayOfString != null && paramArrayOfString.length > 0) ? (String[])paramArrayOfString.clone() : NO_TYPES;
    this
      .arrayGettersSafe = MBeanInfo.arrayGettersSafe(getClass(), MBeanNotificationInfo.class);
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public String[] getNotifTypes() {
    if (this.types.length == 0)
      return NO_TYPES; 
    return (String[])this.types.clone();
  }
  
  private String[] fastGetNotifTypes() {
    if (this.arrayGettersSafe)
      return this.types; 
    return getNotifTypes();
  }
  
  public String toString() {
    return getClass().getName() + "[" + "description=" + 
      getDescription() + ", " + "name=" + 
      getName() + ", " + "notifTypes=" + 
      Arrays.<String>asList(fastGetNotifTypes()) + ", " + "descriptor=" + 
      getDescriptor() + "]";
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof MBeanNotificationInfo))
      return false; 
    MBeanNotificationInfo mBeanNotificationInfo = (MBeanNotificationInfo)paramObject;
    return (Objects.equals(mBeanNotificationInfo.getName(), getName()) && 
      Objects.equals(mBeanNotificationInfo.getDescription(), getDescription()) && 
      Objects.equals(mBeanNotificationInfo.getDescriptor(), getDescriptor()) && 
      Arrays.equals((Object[])mBeanNotificationInfo.fastGetNotifTypes(), (Object[])fastGetNotifTypes()));
  }
  
  public int hashCode() {
    int i = getName().hashCode();
    for (byte b = 0; b < this.types.length; b++)
      i ^= this.types[b].hashCode(); 
    return i;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    String[] arrayOfString = (String[])getField.get("types", (Object)null);
    this.types = (arrayOfString != null && arrayOfString.length != 0) ? (String[])arrayOfString.clone() : NO_TYPES;
  }
}
