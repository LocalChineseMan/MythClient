package java.io;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;
import sun.misc.VM;
import sun.reflect.misc.ReflectUtil;

public class ObjectInputStream extends InputStream implements ObjectInput, ObjectStreamConstants {
  private static final int NULL_HANDLE = -1;
  
  private static final Object unsharedMarker = new Object();
  
  private static final HashMap<String, Class<?>> primClasses = new HashMap<>(8, 1.0F);
  
  private final BlockDataInputStream bin;
  
  private final ValidationList vlist;
  
  private int depth;
  
  private boolean closed;
  
  private final HandleTable handles;
  
  static {
    primClasses.put("boolean", boolean.class);
    primClasses.put("byte", byte.class);
    primClasses.put("char", char.class);
    primClasses.put("short", short.class);
    primClasses.put("int", int.class);
    primClasses.put("long", long.class);
    primClasses.put("float", float.class);
    primClasses.put("double", double.class);
    primClasses.put("void", void.class);
  }
  
  private int passHandle = -1;
  
  private boolean defaultDataEnd = false;
  
  private byte[] primVals;
  
  private final boolean enableOverride;
  
  private boolean enableResolve;
  
  private SerialCallbackContext curContext;
  
  public ObjectInputStream(InputStream paramInputStream) throws IOException {
    verifySubclass();
    this.bin = new BlockDataInputStream(this, paramInputStream);
    this.handles = new HandleTable(10);
    this.vlist = new ValidationList();
    this.enableOverride = false;
    readStreamHeader();
    this.bin.setBlockDataMode(true);
  }
  
  protected ObjectInputStream() throws IOException, SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION); 
    this.bin = null;
    this.handles = null;
    this.vlist = null;
    this.enableOverride = true;
  }
  
  public final Object readObject() throws IOException, ClassNotFoundException {
    if (this.enableOverride)
      return readObjectOverride(); 
    int i = this.passHandle;
    try {
      Object object = readObject0(false);
      this.handles.markDependency(i, this.passHandle);
      ClassNotFoundException classNotFoundException = this.handles.lookupException(this.passHandle);
      if (classNotFoundException != null)
        throw classNotFoundException; 
      if (this.depth == 0)
        this.vlist.doCallbacks(); 
      return object;
    } finally {
      this.passHandle = i;
      if (this.closed && this.depth == 0)
        clear(); 
    } 
  }
  
  protected Object readObjectOverride() throws IOException, ClassNotFoundException {
    return null;
  }
  
  public Object readUnshared() throws IOException, ClassNotFoundException {
    int i = this.passHandle;
    try {
      Object object = readObject0(true);
      this.handles.markDependency(i, this.passHandle);
      ClassNotFoundException classNotFoundException = this.handles.lookupException(this.passHandle);
      if (classNotFoundException != null)
        throw classNotFoundException; 
      if (this.depth == 0)
        this.vlist.doCallbacks(); 
      return object;
    } finally {
      this.passHandle = i;
      if (this.closed && this.depth == 0)
        clear(); 
    } 
  }
  
  public void defaultReadObject() throws IOException, ClassNotFoundException {
    SerialCallbackContext serialCallbackContext = this.curContext;
    if (serialCallbackContext == null)
      throw new NotActiveException("not in call to readObject"); 
    Object object = serialCallbackContext.getObj();
    ObjectStreamClass objectStreamClass = serialCallbackContext.getDesc();
    this.bin.setBlockDataMode(false);
    defaultReadFields(object, objectStreamClass);
    this.bin.setBlockDataMode(true);
    if (!objectStreamClass.hasWriteObjectData())
      this.defaultDataEnd = true; 
    ClassNotFoundException classNotFoundException = this.handles.lookupException(this.passHandle);
    if (classNotFoundException != null)
      throw classNotFoundException; 
  }
  
  public GetField readFields() throws IOException, ClassNotFoundException {
    SerialCallbackContext serialCallbackContext = this.curContext;
    if (serialCallbackContext == null)
      throw new NotActiveException("not in call to readObject"); 
    Object object = serialCallbackContext.getObj();
    ObjectStreamClass objectStreamClass = serialCallbackContext.getDesc();
    this.bin.setBlockDataMode(false);
    GetFieldImpl getFieldImpl = new GetFieldImpl(this, objectStreamClass);
    getFieldImpl.readFields();
    this.bin.setBlockDataMode(true);
    if (!objectStreamClass.hasWriteObjectData())
      this.defaultDataEnd = true; 
    return (GetField)getFieldImpl;
  }
  
  public void registerValidation(ObjectInputValidation paramObjectInputValidation, int paramInt) throws NotActiveException, InvalidObjectException {
    if (this.depth == 0)
      throw new NotActiveException("stream inactive"); 
    this.vlist.register(paramObjectInputValidation, paramInt);
  }
  
  protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass) throws IOException, ClassNotFoundException {
    String str = paramObjectStreamClass.getName();
    try {
      return Class.forName(str, false, latestUserDefinedLoader());
    } catch (ClassNotFoundException classNotFoundException) {
      Class<?> clazz = primClasses.get(str);
      if (clazz != null)
        return clazz; 
      throw classNotFoundException;
    } 
  }
  
  protected Class<?> resolveProxyClass(String[] paramArrayOfString) throws IOException, ClassNotFoundException {
    ClassLoader classLoader1 = latestUserDefinedLoader();
    ClassLoader classLoader2 = null;
    boolean bool = false;
    Class[] arrayOfClass = new Class[paramArrayOfString.length];
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      Class<?> clazz = Class.forName(paramArrayOfString[b], false, classLoader1);
      if ((clazz.getModifiers() & 0x1) == 0)
        if (bool) {
          if (classLoader2 != clazz.getClassLoader())
            throw new IllegalAccessError("conflicting non-public interface class loaders"); 
        } else {
          classLoader2 = clazz.getClassLoader();
          bool = true;
        }  
      arrayOfClass[b] = clazz;
    } 
    try {
      return Proxy.getProxyClass(bool ? classLoader2 : classLoader1, arrayOfClass);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new ClassNotFoundException(null, illegalArgumentException);
    } 
  }
  
  protected Object resolveObject(Object paramObject) throws IOException {
    return paramObject;
  }
  
  protected boolean enableResolveObject(boolean paramBoolean) throws SecurityException {
    if (paramBoolean == this.enableResolve)
      return paramBoolean; 
    if (paramBoolean) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkPermission(SUBSTITUTION_PERMISSION); 
    } 
    this.enableResolve = paramBoolean;
    return !this.enableResolve;
  }
  
  protected void readStreamHeader() throws IOException, StreamCorruptedException {
    short s1 = this.bin.readShort();
    short s2 = this.bin.readShort();
    if (s1 != -21267 || s2 != 5)
      throw new StreamCorruptedException(
          String.format("invalid stream header: %04X%04X", new Object[] { Short.valueOf(s1), Short.valueOf(s2) })); 
  }
  
  protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
    ObjectStreamClass objectStreamClass = new ObjectStreamClass();
    objectStreamClass.readNonProxy(this);
    return objectStreamClass;
  }
  
  public int read() throws IOException {
    return this.bin.read();
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (paramArrayOfbyte == null)
      throw new NullPointerException(); 
    int i = paramInt1 + paramInt2;
    if (paramInt1 < 0 || paramInt2 < 0 || i > paramArrayOfbyte.length || i < 0)
      throw new IndexOutOfBoundsException(); 
    return this.bin.read(paramArrayOfbyte, paramInt1, paramInt2, false);
  }
  
  public int available() throws IOException {
    return this.bin.available();
  }
  
  public void close() throws IOException {
    this.closed = true;
    if (this.depth == 0)
      clear(); 
    this.bin.close();
  }
  
  public boolean readBoolean() throws IOException {
    return this.bin.readBoolean();
  }
  
  public byte readByte() throws IOException {
    return this.bin.readByte();
  }
  
  public int readUnsignedByte() throws IOException {
    return this.bin.readUnsignedByte();
  }
  
  public char readChar() throws IOException {
    return this.bin.readChar();
  }
  
  public short readShort() throws IOException {
    return this.bin.readShort();
  }
  
  public int readUnsignedShort() throws IOException {
    return this.bin.readUnsignedShort();
  }
  
  public int readInt() throws IOException {
    return this.bin.readInt();
  }
  
  public long readLong() throws IOException {
    return this.bin.readLong();
  }
  
  public float readFloat() throws IOException {
    return this.bin.readFloat();
  }
  
  public double readDouble() throws IOException {
    return this.bin.readDouble();
  }
  
  public void readFully(byte[] paramArrayOfbyte) throws IOException {
    this.bin.readFully(paramArrayOfbyte, 0, paramArrayOfbyte.length, false);
  }
  
  public void readFully(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    int i = paramInt1 + paramInt2;
    if (paramInt1 < 0 || paramInt2 < 0 || i > paramArrayOfbyte.length || i < 0)
      throw new IndexOutOfBoundsException(); 
    this.bin.readFully(paramArrayOfbyte, paramInt1, paramInt2, false);
  }
  
  public int skipBytes(int paramInt) throws IOException {
    return this.bin.skipBytes(paramInt);
  }
  
  @Deprecated
  public String readLine() throws IOException {
    return this.bin.readLine();
  }
  
  public String readUTF() throws IOException {
    return this.bin.readUTF();
  }
  
  private static class ObjectInputStream {}
  
  private class ObjectInputStream {}
  
  private static class ObjectInputStream {}
  
  private static class ObjectInputStream {}
  
  private class ObjectInputStream {}
  
  public static abstract class GetField {
    public abstract ObjectStreamClass getObjectStreamClass();
    
    public abstract boolean defaulted(String param1String) throws IOException;
    
    public abstract boolean get(String param1String, boolean param1Boolean) throws IOException;
    
    public abstract byte get(String param1String, byte param1Byte) throws IOException;
    
    public abstract char get(String param1String, char param1Char) throws IOException;
    
    public abstract short get(String param1String, short param1Short) throws IOException;
    
    public abstract int get(String param1String, int param1Int) throws IOException;
    
    public abstract long get(String param1String, long param1Long) throws IOException;
    
    public abstract float get(String param1String, float param1Float) throws IOException;
    
    public abstract double get(String param1String, double param1Double) throws IOException;
    
    public abstract Object get(String param1String, Object param1Object) throws IOException;
  }
  
  private void verifySubclass() {
    Class<?> clazz = getClass();
    if (clazz == ObjectInputStream.class)
      return; 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null)
      return; 
    ObjectStreamClass.processQueue(Caches.subclassAuditsQueue, (ConcurrentMap)Caches.subclassAudits);
    ObjectStreamClass.WeakClassKey weakClassKey = new ObjectStreamClass.WeakClassKey(clazz, Caches.subclassAuditsQueue);
    Boolean bool = Caches.subclassAudits.get(weakClassKey);
    if (bool == null) {
      bool = Boolean.valueOf(auditSubclass(clazz));
      Caches.subclassAudits.putIfAbsent(weakClassKey, bool);
    } 
    if (bool.booleanValue())
      return; 
    securityManager.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
  }
  
  private static boolean auditSubclass(Class<?> paramClass) {
    Boolean bool = AccessController.<Boolean>doPrivileged((PrivilegedAction<Boolean>)new Object(paramClass));
    return bool.booleanValue();
  }
  
  private void clear() {
    this.handles.clear();
    this.vlist.clear();
  }
  
  private Object readObject0(boolean paramBoolean) throws IOException {
    boolean bool = this.bin.getBlockDataMode();
    if (bool) {
      int i = this.bin.currentBlockRemaining();
      if (i > 0)
        throw new OptionalDataException(i); 
      if (this.defaultDataEnd)
        throw new OptionalDataException(true); 
      this.bin.setBlockDataMode(false);
    } 
    byte b;
    while ((b = this.bin.peekByte()) == 121) {
      this.bin.readByte();
      handleReset();
    } 
    this.depth++;
    try {
      Object<?> object;
      switch (b) {
        case 112:
          object = (Object<?>)readNull();
          return object;
        case 113:
          object = (Object<?>)readHandle(paramBoolean);
          return object;
        case 118:
          object = (Object<?>)readClass(paramBoolean);
          return object;
        case 114:
        case 125:
          object = (Object<?>)readClassDesc(paramBoolean);
          return object;
        case 116:
        case 124:
          object = (Object<?>)checkResolve(readString(paramBoolean));
          return object;
        case 117:
          object = (Object<?>)checkResolve(readArray(paramBoolean));
          return object;
        case 126:
          object = (Object<?>)checkResolve(readEnum(paramBoolean));
          return object;
        case 115:
          object = (Object<?>)checkResolve(readOrdinaryObject(paramBoolean));
          return object;
        case 123:
          object = (Object<?>)readFatalException();
          throw new WriteAbortedException("writing aborted", object);
        case 119:
        case 122:
          if (bool) {
            this.bin.setBlockDataMode(true);
            this.bin.peek();
            throw new OptionalDataException(this.bin.currentBlockRemaining());
          } 
          throw new StreamCorruptedException("unexpected block data");
        case 120:
          if (bool)
            throw new OptionalDataException(true); 
          throw new StreamCorruptedException("unexpected end of block data");
      } 
      throw new StreamCorruptedException(String.format("invalid type code: %02X", new Object[] { Byte.valueOf(b) }));
    } finally {
      this.depth--;
      this.bin.setBlockDataMode(bool);
    } 
  }
  
  private Object checkResolve(Object paramObject) throws IOException {
    if (!this.enableResolve || this.handles.lookupException(this.passHandle) != null)
      return paramObject; 
    Object object = resolveObject(paramObject);
    if (object != paramObject)
      this.handles.setObject(this.passHandle, object); 
    return object;
  }
  
  String readTypeString() throws IOException {
    int i = this.passHandle;
    try {
      String str;
      byte b = this.bin.peekByte();
      switch (b) {
        case 112:
          str = (String)readNull();
          return str;
        case 113:
          str = (String)readHandle(false);
          return str;
        case 116:
        case 124:
          str = readString(false);
          return str;
      } 
      throw new StreamCorruptedException(String.format("invalid type code: %02X", new Object[] { Byte.valueOf(b) }));
    } finally {
      this.passHandle = i;
    } 
  }
  
  private Object readNull() throws IOException {
    if (this.bin.readByte() != 112)
      throw new InternalError(); 
    this.passHandle = -1;
    return null;
  }
  
  private Object readHandle(boolean paramBoolean) throws IOException {
    if (this.bin.readByte() != 113)
      throw new InternalError(); 
    this.passHandle = this.bin.readInt() - 8257536;
    if (this.passHandle < 0 || this.passHandle >= this.handles.size())
      throw new StreamCorruptedException(
          String.format("invalid handle value: %08X", new Object[] { Integer.valueOf(this.passHandle + 8257536) })); 
    if (paramBoolean)
      throw new InvalidObjectException("cannot read back reference as unshared"); 
    Object object = this.handles.lookupObject(this.passHandle);
    if (object == unsharedMarker)
      throw new InvalidObjectException("cannot read back reference to unshared object"); 
    return object;
  }
  
  private Class<?> readClass(boolean paramBoolean) throws IOException {
    if (this.bin.readByte() != 118)
      throw new InternalError(); 
    ObjectStreamClass objectStreamClass = readClassDesc(false);
    Class<?> clazz = objectStreamClass.forClass();
    this.passHandle = this.handles.assign(paramBoolean ? unsharedMarker : clazz);
    ClassNotFoundException classNotFoundException = objectStreamClass.getResolveException();
    if (classNotFoundException != null)
      this.handles.markException(this.passHandle, classNotFoundException); 
    this.handles.finish(this.passHandle);
    return clazz;
  }
  
  private ObjectStreamClass readClassDesc(boolean paramBoolean) throws IOException {
    byte b = this.bin.peekByte();
    switch (b) {
      case 112:
        return (ObjectStreamClass)readNull();
      case 113:
        return (ObjectStreamClass)readHandle(paramBoolean);
      case 125:
        return readProxyDesc(paramBoolean);
      case 114:
        return readNonProxyDesc(paramBoolean);
    } 
    throw new StreamCorruptedException(
        String.format("invalid type code: %02X", new Object[] { Byte.valueOf(b) }));
  }
  
  private boolean isCustomSubclass() {
    return 
      (getClass().getClassLoader() != ObjectInputStream.class.getClassLoader());
  }
  
  private ObjectStreamClass readProxyDesc(boolean paramBoolean) throws IOException {
    if (this.bin.readByte() != 125)
      throw new InternalError(); 
    ObjectStreamClass objectStreamClass = new ObjectStreamClass();
    int i = this.handles.assign(paramBoolean ? unsharedMarker : objectStreamClass);
    this.passHandle = -1;
    int j = this.bin.readInt();
    String[] arrayOfString = new String[j];
    for (byte b = 0; b < j; b++)
      arrayOfString[b] = this.bin.readUTF(); 
    Class<?> clazz = null;
    ClassNotFoundException classNotFoundException = null;
    this.bin.setBlockDataMode(true);
    try {
      if ((clazz = resolveProxyClass(arrayOfString)) == null) {
        classNotFoundException = new ClassNotFoundException("null class");
      } else {
        if (!Proxy.isProxyClass(clazz))
          throw new InvalidClassException("Not a proxy"); 
        ReflectUtil.checkProxyPackageAccess(
            getClass().getClassLoader(), clazz
            .getInterfaces());
      } 
    } catch (ClassNotFoundException classNotFoundException1) {
      classNotFoundException = classNotFoundException1;
    } 
    skipCustomData();
    objectStreamClass.initProxy(clazz, classNotFoundException, readClassDesc(false));
    this.handles.finish(i);
    this.passHandle = i;
    return objectStreamClass;
  }
  
  private ObjectStreamClass readNonProxyDesc(boolean paramBoolean) throws IOException {
    if (this.bin.readByte() != 114)
      throw new InternalError(); 
    ObjectStreamClass objectStreamClass1 = new ObjectStreamClass();
    int i = this.handles.assign(paramBoolean ? unsharedMarker : objectStreamClass1);
    this.passHandle = -1;
    ObjectStreamClass objectStreamClass2 = null;
    try {
      objectStreamClass2 = readClassDescriptor();
    } catch (ClassNotFoundException classNotFoundException1) {
      throw (IOException)(new InvalidClassException("failed to read class descriptor"))
        .initCause(classNotFoundException1);
    } 
    Class<?> clazz = null;
    ClassNotFoundException classNotFoundException = null;
    this.bin.setBlockDataMode(true);
    boolean bool = isCustomSubclass();
    try {
      if ((clazz = resolveClass(objectStreamClass2)) == null) {
        classNotFoundException = new ClassNotFoundException("null class");
      } else if (bool) {
        ReflectUtil.checkPackageAccess(clazz);
      } 
    } catch (ClassNotFoundException classNotFoundException1) {
      classNotFoundException = classNotFoundException1;
    } 
    skipCustomData();
    objectStreamClass1.initNonProxy(objectStreamClass2, clazz, classNotFoundException, readClassDesc(false));
    this.handles.finish(i);
    this.passHandle = i;
    return objectStreamClass1;
  }
  
  private String readString(boolean paramBoolean) throws IOException {
    String str;
    byte b = this.bin.readByte();
    switch (b) {
      case 116:
        str = this.bin.readUTF();
        break;
      case 124:
        str = this.bin.readLongUTF();
        break;
      default:
        throw new StreamCorruptedException(
            String.format("invalid type code: %02X", new Object[] { Byte.valueOf(b) }));
    } 
    this.passHandle = this.handles.assign(paramBoolean ? unsharedMarker : str);
    this.handles.finish(this.passHandle);
    return str;
  }
  
  private Object readArray(boolean paramBoolean) throws IOException {
    if (this.bin.readByte() != 117)
      throw new InternalError(); 
    ObjectStreamClass objectStreamClass = readClassDesc(false);
    int i = this.bin.readInt();
    Object object = null;
    Class<?> clazz2 = null;
    Class<?> clazz1;
    if ((clazz1 = objectStreamClass.forClass()) != null) {
      clazz2 = clazz1.getComponentType();
      object = Array.newInstance(clazz2, i);
    } 
    int j = this.handles.assign(paramBoolean ? unsharedMarker : object);
    ClassNotFoundException classNotFoundException = objectStreamClass.getResolveException();
    if (classNotFoundException != null)
      this.handles.markException(j, classNotFoundException); 
    if (clazz2 == null) {
      for (byte b = 0; b < i; b++)
        readObject0(false); 
    } else if (clazz2.isPrimitive()) {
      if (clazz2 == int.class) {
        this.bin.readInts((int[])object, 0, i);
      } else if (clazz2 == byte.class) {
        this.bin.readFully((byte[])object, 0, i, true);
      } else if (clazz2 == long.class) {
        this.bin.readLongs((long[])object, 0, i);
      } else if (clazz2 == float.class) {
        this.bin.readFloats((float[])object, 0, i);
      } else if (clazz2 == double.class) {
        this.bin.readDoubles((double[])object, 0, i);
      } else if (clazz2 == short.class) {
        this.bin.readShorts((short[])object, 0, i);
      } else if (clazz2 == char.class) {
        this.bin.readChars((char[])object, 0, i);
      } else if (clazz2 == boolean.class) {
        this.bin.readBooleans((boolean[])object, 0, i);
      } else {
        throw new InternalError();
      } 
    } else {
      Object[] arrayOfObject = (Object[])object;
      for (byte b = 0; b < i; b++) {
        arrayOfObject[b] = readObject0(false);
        this.handles.markDependency(j, this.passHandle);
      } 
    } 
    this.handles.finish(j);
    this.passHandle = j;
    return object;
  }
  
  private Enum<?> readEnum(boolean paramBoolean) throws IOException {
    if (this.bin.readByte() != 126)
      throw new InternalError(); 
    ObjectStreamClass objectStreamClass = readClassDesc(false);
    if (!objectStreamClass.isEnum())
      throw new InvalidClassException("non-enum class: " + objectStreamClass); 
    int i = this.handles.assign(paramBoolean ? unsharedMarker : null);
    ClassNotFoundException classNotFoundException = objectStreamClass.getResolveException();
    if (classNotFoundException != null)
      this.handles.markException(i, classNotFoundException); 
    String str = readString(false);
    Enum enum_ = null;
    Class<?> clazz = objectStreamClass.forClass();
    if (clazz != null) {
      try {
        Enum enum_1 = (Enum)Enum.valueOf((Class)clazz, str);
        enum_ = enum_1;
      } catch (IllegalArgumentException illegalArgumentException) {
        throw (IOException)(new InvalidObjectException("enum constant " + str + " does not exist in " + clazz))
          
          .initCause(illegalArgumentException);
      } 
      if (!paramBoolean)
        this.handles.setObject(i, enum_); 
    } 
    this.handles.finish(i);
    this.passHandle = i;
    return enum_;
  }
  
  private Object readOrdinaryObject(boolean paramBoolean) throws IOException {
    Object object;
    if (this.bin.readByte() != 115)
      throw new InternalError(); 
    ObjectStreamClass objectStreamClass = readClassDesc(false);
    objectStreamClass.checkDeserialize();
    Class<?> clazz = objectStreamClass.forClass();
    if (clazz == String.class || clazz == Class.class || clazz == ObjectStreamClass.class)
      throw new InvalidClassException("invalid class descriptor"); 
    try {
      object = objectStreamClass.isInstantiable() ? objectStreamClass.newInstance() : null;
    } catch (Exception exception) {
      throw (IOException)(new InvalidClassException(objectStreamClass
          .forClass().getName(), "unable to create instance"))
        .initCause(exception);
    } 
    this.passHandle = this.handles.assign(paramBoolean ? unsharedMarker : object);
    ClassNotFoundException classNotFoundException = objectStreamClass.getResolveException();
    if (classNotFoundException != null)
      this.handles.markException(this.passHandle, classNotFoundException); 
    if (objectStreamClass.isExternalizable()) {
      readExternalData((Externalizable)object, objectStreamClass);
    } else {
      readSerialData(object, objectStreamClass);
    } 
    this.handles.finish(this.passHandle);
    if (object != null && this.handles
      .lookupException(this.passHandle) == null && objectStreamClass
      .hasReadResolveMethod()) {
      Object object1 = objectStreamClass.invokeReadResolve(object);
      if (paramBoolean && object1.getClass().isArray())
        object1 = cloneArray(object1); 
      if (object1 != object)
        this.handles.setObject(this.passHandle, object = object1); 
    } 
    return object;
  }
  
  private void readExternalData(Externalizable paramExternalizable, ObjectStreamClass paramObjectStreamClass) throws IOException {
    SerialCallbackContext serialCallbackContext = this.curContext;
    if (serialCallbackContext != null)
      serialCallbackContext.check(); 
    this.curContext = null;
    try {
      boolean bool = paramObjectStreamClass.hasBlockExternalData();
      if (bool)
        this.bin.setBlockDataMode(true); 
      if (paramExternalizable != null)
        try {
          paramExternalizable.readExternal((ObjectInput)this);
        } catch (ClassNotFoundException classNotFoundException) {
          this.handles.markException(this.passHandle, classNotFoundException);
        }  
      if (bool)
        skipCustomData(); 
    } finally {
      if (serialCallbackContext != null)
        serialCallbackContext.check(); 
      this.curContext = serialCallbackContext;
    } 
  }
  
  private void readSerialData(Object paramObject, ObjectStreamClass paramObjectStreamClass) throws IOException {
    ObjectStreamClass.ClassDataSlot[] arrayOfClassDataSlot = paramObjectStreamClass.getClassDataLayout();
    for (byte b = 0; b < arrayOfClassDataSlot.length; b++) {
      ObjectStreamClass objectStreamClass = (arrayOfClassDataSlot[b]).desc;
      if ((arrayOfClassDataSlot[b]).hasData) {
        if (paramObject == null || this.handles.lookupException(this.passHandle) != null) {
          defaultReadFields((Object)null, objectStreamClass);
        } else if (objectStreamClass.hasReadObjectMethod()) {
          SerialCallbackContext serialCallbackContext = this.curContext;
          if (serialCallbackContext != null)
            serialCallbackContext.check(); 
          try {
            this.curContext = new SerialCallbackContext(paramObject, objectStreamClass);
            this.bin.setBlockDataMode(true);
            objectStreamClass.invokeReadObject(paramObject, this);
          } catch (ClassNotFoundException classNotFoundException) {
            this.handles.markException(this.passHandle, classNotFoundException);
          } finally {
            this.curContext.setUsed();
            if (serialCallbackContext != null)
              serialCallbackContext.check(); 
            this.curContext = serialCallbackContext;
          } 
          this.defaultDataEnd = false;
        } else {
          defaultReadFields(paramObject, objectStreamClass);
        } 
        if (objectStreamClass.hasWriteObjectData()) {
          skipCustomData();
        } else {
          this.bin.setBlockDataMode(false);
        } 
      } else if (paramObject != null && objectStreamClass
        .hasReadObjectNoDataMethod() && this.handles
        .lookupException(this.passHandle) == null) {
        objectStreamClass.invokeReadObjectNoData(paramObject);
      } 
    } 
  }
  
  private void skipCustomData() throws IOException {
    int i = this.passHandle;
    while (true) {
      if (this.bin.getBlockDataMode()) {
        this.bin.skipBlockData();
        this.bin.setBlockDataMode(false);
      } 
      switch (this.bin.peekByte()) {
        case 119:
        case 122:
          this.bin.setBlockDataMode(true);
          continue;
        case 120:
          this.bin.readByte();
          this.passHandle = i;
          return;
      } 
      readObject0(false);
    } 
  }
  
  private void defaultReadFields(Object paramObject, ObjectStreamClass paramObjectStreamClass) throws IOException {
    Class<?> clazz = paramObjectStreamClass.forClass();
    if (clazz != null && paramObject != null && !clazz.isInstance(paramObject))
      throw new ClassCastException(); 
    int i = paramObjectStreamClass.getPrimDataSize();
    if (this.primVals == null || this.primVals.length < i)
      this.primVals = new byte[i]; 
    this.bin.readFully(this.primVals, 0, i, false);
    if (paramObject != null)
      paramObjectStreamClass.setPrimFieldValues(paramObject, this.primVals); 
    int j = this.passHandle;
    ObjectStreamField[] arrayOfObjectStreamField = paramObjectStreamClass.getFields(false);
    Object[] arrayOfObject = new Object[paramObjectStreamClass.getNumObjFields()];
    int k = arrayOfObjectStreamField.length - arrayOfObject.length;
    for (byte b = 0; b < arrayOfObject.length; b++) {
      ObjectStreamField objectStreamField = arrayOfObjectStreamField[k + b];
      arrayOfObject[b] = readObject0(objectStreamField.isUnshared());
      if (objectStreamField.getField() != null)
        this.handles.markDependency(j, this.passHandle); 
    } 
    if (paramObject != null)
      paramObjectStreamClass.setObjFieldValues(paramObject, arrayOfObject); 
    this.passHandle = j;
  }
  
  private IOException readFatalException() throws IOException {
    if (this.bin.readByte() != 123)
      throw new InternalError(); 
    clear();
    return (IOException)readObject0(false);
  }
  
  private void handleReset() throws StreamCorruptedException {
    if (this.depth > 0)
      throw new StreamCorruptedException("unexpected reset; recursion depth: " + this.depth); 
    clear();
  }
  
  private static ClassLoader latestUserDefinedLoader() {
    return VM.latestUserDefinedLoader();
  }
  
  private static Object cloneArray(Object paramObject) {
    if (paramObject instanceof Object[])
      return ((Object[])paramObject).clone(); 
    if (paramObject instanceof boolean[])
      return ((boolean[])paramObject).clone(); 
    if (paramObject instanceof byte[])
      return ((byte[])paramObject).clone(); 
    if (paramObject instanceof char[])
      return ((char[])paramObject).clone(); 
    if (paramObject instanceof double[])
      return ((double[])paramObject).clone(); 
    if (paramObject instanceof float[])
      return ((float[])paramObject).clone(); 
    if (paramObject instanceof int[])
      return ((int[])paramObject).clone(); 
    if (paramObject instanceof long[])
      return ((long[])paramObject).clone(); 
    if (paramObject instanceof short[])
      return ((short[])paramObject).clone(); 
    throw new AssertionError();
  }
  
  private static native void bytesToFloats(byte[] paramArrayOfbyte, int paramInt1, float[] paramArrayOffloat, int paramInt2, int paramInt3);
  
  private static native void bytesToDoubles(byte[] paramArrayOfbyte, int paramInt1, double[] paramArrayOfdouble, int paramInt2, int paramInt3);
  
  private static class ObjectInputStream {}
}
