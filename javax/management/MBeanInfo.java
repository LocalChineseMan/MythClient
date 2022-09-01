package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class MBeanInfo implements Cloneable, Serializable, DescriptorRead {
  static final long serialVersionUID = -6451021435135161911L;
  
  private transient Descriptor descriptor;
  
  private final String description;
  
  private final String className;
  
  private final MBeanAttributeInfo[] attributes;
  
  private final MBeanOperationInfo[] operations;
  
  private final MBeanConstructorInfo[] constructors;
  
  private final MBeanNotificationInfo[] notifications;
  
  private transient int hashCode;
  
  private final transient boolean arrayGettersSafe;
  
  public MBeanInfo(String paramString1, String paramString2, MBeanAttributeInfo[] paramArrayOfMBeanAttributeInfo, MBeanConstructorInfo[] paramArrayOfMBeanConstructorInfo, MBeanOperationInfo[] paramArrayOfMBeanOperationInfo, MBeanNotificationInfo[] paramArrayOfMBeanNotificationInfo) throws IllegalArgumentException {
    this(paramString1, paramString2, paramArrayOfMBeanAttributeInfo, paramArrayOfMBeanConstructorInfo, paramArrayOfMBeanOperationInfo, paramArrayOfMBeanNotificationInfo, null);
  }
  
  public MBeanInfo(String paramString1, String paramString2, MBeanAttributeInfo[] paramArrayOfMBeanAttributeInfo, MBeanConstructorInfo[] paramArrayOfMBeanConstructorInfo, MBeanOperationInfo[] paramArrayOfMBeanOperationInfo, MBeanNotificationInfo[] paramArrayOfMBeanNotificationInfo, Descriptor paramDescriptor) throws IllegalArgumentException {
    this.className = paramString1;
    this.description = paramString2;
    if (paramArrayOfMBeanAttributeInfo == null)
      paramArrayOfMBeanAttributeInfo = MBeanAttributeInfo.NO_ATTRIBUTES; 
    this.attributes = paramArrayOfMBeanAttributeInfo;
    if (paramArrayOfMBeanOperationInfo == null)
      paramArrayOfMBeanOperationInfo = MBeanOperationInfo.NO_OPERATIONS; 
    this.operations = paramArrayOfMBeanOperationInfo;
    if (paramArrayOfMBeanConstructorInfo == null)
      paramArrayOfMBeanConstructorInfo = MBeanConstructorInfo.NO_CONSTRUCTORS; 
    this.constructors = paramArrayOfMBeanConstructorInfo;
    if (paramArrayOfMBeanNotificationInfo == null)
      paramArrayOfMBeanNotificationInfo = MBeanNotificationInfo.NO_NOTIFICATIONS; 
    this.notifications = paramArrayOfMBeanNotificationInfo;
    if (paramDescriptor == null)
      paramDescriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR; 
    this.descriptor = paramDescriptor;
    this
      .arrayGettersSafe = arrayGettersSafe(getClass(), MBeanInfo.class);
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public String getClassName() {
    return this.className;
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public MBeanAttributeInfo[] getAttributes() {
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = nonNullAttributes();
    if (arrayOfMBeanAttributeInfo.length == 0)
      return arrayOfMBeanAttributeInfo; 
    return (MBeanAttributeInfo[])arrayOfMBeanAttributeInfo.clone();
  }
  
  private MBeanAttributeInfo[] fastGetAttributes() {
    if (this.arrayGettersSafe)
      return nonNullAttributes(); 
    return getAttributes();
  }
  
  private MBeanAttributeInfo[] nonNullAttributes() {
    return (this.attributes == null) ? MBeanAttributeInfo.NO_ATTRIBUTES : this.attributes;
  }
  
  public MBeanOperationInfo[] getOperations() {
    MBeanOperationInfo[] arrayOfMBeanOperationInfo = nonNullOperations();
    if (arrayOfMBeanOperationInfo.length == 0)
      return arrayOfMBeanOperationInfo; 
    return (MBeanOperationInfo[])arrayOfMBeanOperationInfo.clone();
  }
  
  private MBeanOperationInfo[] fastGetOperations() {
    if (this.arrayGettersSafe)
      return nonNullOperations(); 
    return getOperations();
  }
  
  private MBeanOperationInfo[] nonNullOperations() {
    return (this.operations == null) ? MBeanOperationInfo.NO_OPERATIONS : this.operations;
  }
  
  public MBeanConstructorInfo[] getConstructors() {
    MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = nonNullConstructors();
    if (arrayOfMBeanConstructorInfo.length == 0)
      return arrayOfMBeanConstructorInfo; 
    return (MBeanConstructorInfo[])arrayOfMBeanConstructorInfo.clone();
  }
  
  private MBeanConstructorInfo[] fastGetConstructors() {
    if (this.arrayGettersSafe)
      return nonNullConstructors(); 
    return getConstructors();
  }
  
  private MBeanConstructorInfo[] nonNullConstructors() {
    return (this.constructors == null) ? MBeanConstructorInfo.NO_CONSTRUCTORS : this.constructors;
  }
  
  public MBeanNotificationInfo[] getNotifications() {
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = nonNullNotifications();
    if (arrayOfMBeanNotificationInfo.length == 0)
      return arrayOfMBeanNotificationInfo; 
    return (MBeanNotificationInfo[])arrayOfMBeanNotificationInfo.clone();
  }
  
  private MBeanNotificationInfo[] fastGetNotifications() {
    if (this.arrayGettersSafe)
      return nonNullNotifications(); 
    return getNotifications();
  }
  
  private MBeanNotificationInfo[] nonNullNotifications() {
    return (this.notifications == null) ? MBeanNotificationInfo.NO_NOTIFICATIONS : this.notifications;
  }
  
  public Descriptor getDescriptor() {
    return (Descriptor)ImmutableDescriptor.nonNullDescriptor(this.descriptor).clone();
  }
  
  public String toString() {
    return getClass().getName() + "[" + "description=" + 
      getDescription() + ", " + "attributes=" + 
      Arrays.<MBeanAttributeInfo>asList(fastGetAttributes()) + ", " + "constructors=" + 
      Arrays.<MBeanConstructorInfo>asList(fastGetConstructors()) + ", " + "operations=" + 
      Arrays.<MBeanOperationInfo>asList(fastGetOperations()) + ", " + "notifications=" + 
      Arrays.<MBeanNotificationInfo>asList(fastGetNotifications()) + ", " + "descriptor=" + 
      getDescriptor() + "]";
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof MBeanInfo))
      return false; 
    MBeanInfo mBeanInfo = (MBeanInfo)paramObject;
    if (!isEqual(getClassName(), mBeanInfo.getClassName()) || 
      !isEqual(getDescription(), mBeanInfo.getDescription()) || 
      !getDescriptor().equals(mBeanInfo.getDescriptor()))
      return false; 
    return (
      Arrays.equals((Object[])mBeanInfo.fastGetAttributes(), (Object[])fastGetAttributes()) && 
      Arrays.equals((Object[])mBeanInfo.fastGetOperations(), (Object[])fastGetOperations()) && 
      Arrays.equals((Object[])mBeanInfo.fastGetConstructors(), (Object[])fastGetConstructors()) && 
      Arrays.equals((Object[])mBeanInfo.fastGetNotifications(), (Object[])fastGetNotifications()));
  }
  
  public int hashCode() {
    if (this.hashCode != 0)
      return this.hashCode; 
    this
      
      .hashCode = Objects.hash(new Object[] { getClassName(), getDescriptor() }) ^ Arrays.hashCode((Object[])fastGetAttributes()) ^ Arrays.hashCode((Object[])fastGetOperations()) ^ Arrays.hashCode((Object[])fastGetConstructors()) ^ Arrays.hashCode((Object[])fastGetNotifications());
    return this.hashCode;
  }
  
  private static final Map<Class<?>, Boolean> arrayGettersSafeMap = new WeakHashMap<>();
  
  static boolean arrayGettersSafe(Class<?> paramClass1, Class<?> paramClass2) {
    if (paramClass1 == paramClass2)
      return true; 
    synchronized (arrayGettersSafeMap) {
      Boolean bool = arrayGettersSafeMap.get(paramClass1);
      if (bool == null) {
        try {
          ArrayGettersSafeAction arrayGettersSafeAction = new ArrayGettersSafeAction(paramClass1, paramClass2);
          bool = AccessController.<Boolean>doPrivileged(arrayGettersSafeAction);
        } catch (Exception exception) {
          bool = Boolean.valueOf(false);
        } 
        arrayGettersSafeMap.put(paramClass1, bool);
      } 
      return bool.booleanValue();
    } 
  }
  
  private static class ArrayGettersSafeAction implements PrivilegedAction<Boolean> {
    private final Class<?> subclass;
    
    private final Class<?> immutableClass;
    
    ArrayGettersSafeAction(Class<?> param1Class1, Class<?> param1Class2) {
      this.subclass = param1Class1;
      this.immutableClass = param1Class2;
    }
    
    public Boolean run() {
      Method[] arrayOfMethod = this.immutableClass.getMethods();
      for (byte b = 0; b < arrayOfMethod.length; b++) {
        Method method = arrayOfMethod[b];
        String str = method.getName();
        if (str.startsWith("get") && (method
          .getParameterTypes()).length == 0 && method
          .getReturnType().isArray())
          try {
            Method method1 = this.subclass.getMethod(str, new Class[0]);
            if (!method1.equals(method))
              return Boolean.valueOf(false); 
          } catch (NoSuchMethodException noSuchMethodException) {
            return Boolean.valueOf(false);
          }  
      } 
      return Boolean.valueOf(true);
    }
  }
  
  private static boolean isEqual(String paramString1, String paramString2) {
    boolean bool;
    if (paramString1 == null) {
      bool = (paramString2 == null);
    } else {
      bool = paramString1.equals(paramString2);
    } 
    return bool;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (this.descriptor.getClass() == ImmutableDescriptor.class) {
      paramObjectOutputStream.write(1);
      String[] arrayOfString = this.descriptor.getFieldNames();
      paramObjectOutputStream.writeObject(arrayOfString);
      paramObjectOutputStream.writeObject(this.descriptor.getFieldValues(arrayOfString));
    } else {
      paramObjectOutputStream.write(0);
      paramObjectOutputStream.writeObject(this.descriptor);
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    String[] arrayOfString;
    Object[] arrayOfObject;
    paramObjectInputStream.defaultReadObject();
    switch (paramObjectInputStream.read()) {
      case 1:
        arrayOfString = (String[])paramObjectInputStream.readObject();
        arrayOfObject = (Object[])paramObjectInputStream.readObject();
        this.descriptor = (arrayOfString.length == 0) ? ImmutableDescriptor.EMPTY_DESCRIPTOR : new ImmutableDescriptor(arrayOfString, arrayOfObject);
        return;
      case 0:
        this.descriptor = (Descriptor)paramObjectInputStream.readObject();
        if (this.descriptor == null)
          this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR; 
        return;
      case -1:
        this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
        return;
    } 
    throw new StreamCorruptedException("Got unexpected byte.");
  }
}
