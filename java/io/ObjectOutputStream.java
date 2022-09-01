package java.io;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ConcurrentMap;
import sun.reflect.misc.ReflectUtil;
import sun.security.action.GetBooleanAction;

public class ObjectOutputStream extends OutputStream implements ObjectOutput, ObjectStreamConstants {
  private final BlockDataOutputStream bout;
  
  private final HandleTable handles;
  
  private final ReplaceTable subs;
  
  private int protocol = 2;
  
  private int depth;
  
  private byte[] primVals;
  
  private final boolean enableOverride;
  
  private boolean enableReplace;
  
  private SerialCallbackContext curContext;
  
  private PutFieldImpl curPut;
  
  private final DebugTraceInfoStack debugInfoStack;
  
  private static final boolean extendedDebugInfo = ((Boolean)AccessController.<Boolean>doPrivileged(new GetBooleanAction("sun.io.serialization.extendedDebugInfo")))
    
    .booleanValue();
  
  public ObjectOutputStream(OutputStream paramOutputStream) throws IOException {
    verifySubclass();
    this.bout = new BlockDataOutputStream(paramOutputStream);
    this.handles = new HandleTable(10, 3.0F);
    this.subs = new ReplaceTable(10, 3.0F);
    this.enableOverride = false;
    writeStreamHeader();
    this.bout.setBlockDataMode(true);
    if (extendedDebugInfo) {
      this.debugInfoStack = new DebugTraceInfoStack();
    } else {
      this.debugInfoStack = null;
    } 
  }
  
  protected ObjectOutputStream() throws IOException, SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION); 
    this.bout = null;
    this.handles = null;
    this.subs = null;
    this.enableOverride = true;
    this.debugInfoStack = null;
  }
  
  public void useProtocolVersion(int paramInt) throws IOException {
    if (this.handles.size() != 0)
      throw new IllegalStateException("stream non-empty"); 
    switch (paramInt) {
      case 1:
      case 2:
        this.protocol = paramInt;
        return;
    } 
    throw new IllegalArgumentException("unknown version: " + paramInt);
  }
  
  public final void writeObject(Object paramObject) throws IOException {
    if (this.enableOverride) {
      writeObjectOverride(paramObject);
      return;
    } 
    try {
      writeObject0(paramObject, false);
    } catch (IOException iOException) {
      if (this.depth == 0)
        writeFatalException(iOException); 
      throw iOException;
    } 
  }
  
  protected void writeObjectOverride(Object paramObject) throws IOException {}
  
  public void writeUnshared(Object paramObject) throws IOException {
    try {
      writeObject0(paramObject, true);
    } catch (IOException iOException) {
      if (this.depth == 0)
        writeFatalException(iOException); 
      throw iOException;
    } 
  }
  
  public void defaultWriteObject() throws IOException {
    SerialCallbackContext serialCallbackContext = this.curContext;
    if (serialCallbackContext == null)
      throw new NotActiveException("not in call to writeObject"); 
    Object object = serialCallbackContext.getObj();
    ObjectStreamClass objectStreamClass = serialCallbackContext.getDesc();
    this.bout.setBlockDataMode(false);
    defaultWriteFields(object, objectStreamClass);
    this.bout.setBlockDataMode(true);
  }
  
  public PutField putFields() throws IOException {
    if (this.curPut == null) {
      SerialCallbackContext serialCallbackContext = this.curContext;
      if (serialCallbackContext == null)
        throw new NotActiveException("not in call to writeObject"); 
      Object object = serialCallbackContext.getObj();
      ObjectStreamClass objectStreamClass = serialCallbackContext.getDesc();
      this.curPut = new PutFieldImpl(this, objectStreamClass);
    } 
    return this.curPut;
  }
  
  public void writeFields() throws IOException {
    if (this.curPut == null)
      throw new NotActiveException("no current PutField object"); 
    this.bout.setBlockDataMode(false);
    this.curPut.writeFields();
    this.bout.setBlockDataMode(true);
  }
  
  public void reset() throws IOException {
    if (this.depth != 0)
      throw new IOException("stream active"); 
    this.bout.setBlockDataMode(false);
    this.bout.writeByte(121);
    clear();
    this.bout.setBlockDataMode(true);
  }
  
  protected void annotateClass(Class<?> paramClass) throws IOException {}
  
  protected void annotateProxyClass(Class<?> paramClass) throws IOException {}
  
  protected Object replaceObject(Object paramObject) throws IOException {
    return paramObject;
  }
  
  protected boolean enableReplaceObject(boolean paramBoolean) throws SecurityException {
    if (paramBoolean == this.enableReplace)
      return paramBoolean; 
    if (paramBoolean) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkPermission(SUBSTITUTION_PERMISSION); 
    } 
    this.enableReplace = paramBoolean;
    return !this.enableReplace;
  }
  
  protected void writeStreamHeader() throws IOException {
    this.bout.writeShort(-21267);
    this.bout.writeShort(5);
  }
  
  protected void writeClassDescriptor(ObjectStreamClass paramObjectStreamClass) throws IOException {
    paramObjectStreamClass.writeNonProxy(this);
  }
  
  public void write(int paramInt) throws IOException {
    this.bout.write(paramInt);
  }
  
  public void write(byte[] paramArrayOfbyte) throws IOException {
    this.bout.write(paramArrayOfbyte, 0, paramArrayOfbyte.length, false);
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (paramArrayOfbyte == null)
      throw new NullPointerException(); 
    int i = paramInt1 + paramInt2;
    if (paramInt1 < 0 || paramInt2 < 0 || i > paramArrayOfbyte.length || i < 0)
      throw new IndexOutOfBoundsException(); 
    this.bout.write(paramArrayOfbyte, paramInt1, paramInt2, false);
  }
  
  public void flush() throws IOException {
    this.bout.flush();
  }
  
  protected void drain() throws IOException {
    this.bout.drain();
  }
  
  public void close() throws IOException {
    flush();
    clear();
    this.bout.close();
  }
  
  public void writeBoolean(boolean paramBoolean) throws IOException {
    this.bout.writeBoolean(paramBoolean);
  }
  
  public void writeByte(int paramInt) throws IOException {
    this.bout.writeByte(paramInt);
  }
  
  public void writeShort(int paramInt) throws IOException {
    this.bout.writeShort(paramInt);
  }
  
  public void writeChar(int paramInt) throws IOException {
    this.bout.writeChar(paramInt);
  }
  
  public void writeInt(int paramInt) throws IOException {
    this.bout.writeInt(paramInt);
  }
  
  public void writeLong(long paramLong) throws IOException {
    this.bout.writeLong(paramLong);
  }
  
  public void writeFloat(float paramFloat) throws IOException {
    this.bout.writeFloat(paramFloat);
  }
  
  public void writeDouble(double paramDouble) throws IOException {
    this.bout.writeDouble(paramDouble);
  }
  
  public void writeBytes(String paramString) throws IOException {
    this.bout.writeBytes(paramString);
  }
  
  public void writeChars(String paramString) throws IOException {
    this.bout.writeChars(paramString);
  }
  
  public void writeUTF(String paramString) throws IOException {
    this.bout.writeUTF(paramString);
  }
  
  int getProtocolVersion() {
    return this.protocol;
  }
  
  void writeTypeString(String paramString) throws IOException {
    if (paramString == null) {
      writeNull();
    } else {
      int i;
      if ((i = this.handles.lookup(paramString)) != -1) {
        writeHandle(i);
      } else {
        writeString(paramString, false);
      } 
    } 
  }
  
  private void verifySubclass() {
    Class<?> clazz = getClass();
    if (clazz == ObjectOutputStream.class)
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
    this.subs.clear();
    this.handles.clear();
  }
  
  private void writeObject0(Object paramObject, boolean paramBoolean) throws IOException {
    boolean bool = this.bout.setBlockDataMode(false);
    this.depth++;
    try {
      ObjectStreamClass objectStreamClass;
      if ((paramObject = this.subs.lookup(paramObject)) == null) {
        writeNull();
        return;
      } 
      int i;
      if (!paramBoolean && (i = this.handles.lookup(paramObject)) != -1) {
        writeHandle(i);
        return;
      } 
      if (paramObject instanceof Class) {
        writeClass((Class)paramObject, paramBoolean);
        return;
      } 
      if (paramObject instanceof ObjectStreamClass) {
        writeClassDesc((ObjectStreamClass)paramObject, paramBoolean);
        return;
      } 
      Object object = paramObject;
      Class<?> clazz = paramObject.getClass();
      while (true) {
        objectStreamClass = ObjectStreamClass.lookup(clazz, true);
        Class<?> clazz1;
        if (!objectStreamClass.hasWriteReplaceMethod() || (
          paramObject = objectStreamClass.invokeWriteReplace(paramObject)) == null || (
          clazz1 = paramObject.getClass()) == clazz)
          break; 
        clazz = clazz1;
      } 
      if (this.enableReplace) {
        Object object1 = replaceObject(paramObject);
        if (object1 != paramObject && object1 != null) {
          clazz = object1.getClass();
          objectStreamClass = ObjectStreamClass.lookup(clazz, true);
        } 
        paramObject = object1;
      } 
      if (paramObject != object) {
        this.subs.assign(object, paramObject);
        if (paramObject == null) {
          writeNull();
          return;
        } 
        if (!paramBoolean && (i = this.handles.lookup(paramObject)) != -1) {
          writeHandle(i);
          return;
        } 
        if (paramObject instanceof Class) {
          writeClass((Class)paramObject, paramBoolean);
          return;
        } 
        if (paramObject instanceof ObjectStreamClass) {
          writeClassDesc((ObjectStreamClass)paramObject, paramBoolean);
          return;
        } 
      } 
      if (paramObject instanceof String) {
        writeString((String)paramObject, paramBoolean);
      } else if (clazz.isArray()) {
        writeArray(paramObject, objectStreamClass, paramBoolean);
      } else if (paramObject instanceof Enum) {
        writeEnum((Enum)paramObject, objectStreamClass, paramBoolean);
      } else if (paramObject instanceof Serializable) {
        writeOrdinaryObject(paramObject, objectStreamClass, paramBoolean);
      } else {
        if (extendedDebugInfo)
          throw new NotSerializableException(clazz
              .getName() + "\n" + this.debugInfoStack.toString()); 
        throw new NotSerializableException(clazz.getName());
      } 
    } finally {
      this.depth--;
      this.bout.setBlockDataMode(bool);
    } 
  }
  
  private void writeNull() throws IOException {
    this.bout.writeByte(112);
  }
  
  private void writeHandle(int paramInt) throws IOException {
    this.bout.writeByte(113);
    this.bout.writeInt(8257536 + paramInt);
  }
  
  private void writeClass(Class<?> paramClass, boolean paramBoolean) throws IOException {
    this.bout.writeByte(118);
    writeClassDesc(ObjectStreamClass.lookup(paramClass, true), false);
    this.handles.assign(paramBoolean ? null : paramClass);
  }
  
  private void writeClassDesc(ObjectStreamClass paramObjectStreamClass, boolean paramBoolean) throws IOException {
    if (paramObjectStreamClass == null) {
      writeNull();
    } else {
      int i;
      if (!paramBoolean && (i = this.handles.lookup(paramObjectStreamClass)) != -1) {
        writeHandle(i);
      } else if (paramObjectStreamClass.isProxy()) {
        writeProxyDesc(paramObjectStreamClass, paramBoolean);
      } else {
        writeNonProxyDesc(paramObjectStreamClass, paramBoolean);
      } 
    } 
  }
  
  private boolean isCustomSubclass() {
    return 
      (getClass().getClassLoader() != ObjectOutputStream.class.getClassLoader());
  }
  
  private void writeProxyDesc(ObjectStreamClass paramObjectStreamClass, boolean paramBoolean) throws IOException {
    this.bout.writeByte(125);
    this.handles.assign(paramBoolean ? null : paramObjectStreamClass);
    Class<?> clazz = paramObjectStreamClass.forClass();
    Class[] arrayOfClass = clazz.getInterfaces();
    this.bout.writeInt(arrayOfClass.length);
    for (byte b = 0; b < arrayOfClass.length; b++)
      this.bout.writeUTF(arrayOfClass[b].getName()); 
    this.bout.setBlockDataMode(true);
    if (clazz != null && isCustomSubclass())
      ReflectUtil.checkPackageAccess(clazz); 
    annotateProxyClass(clazz);
    this.bout.setBlockDataMode(false);
    this.bout.writeByte(120);
    writeClassDesc(paramObjectStreamClass.getSuperDesc(), false);
  }
  
  private void writeNonProxyDesc(ObjectStreamClass paramObjectStreamClass, boolean paramBoolean) throws IOException {
    this.bout.writeByte(114);
    this.handles.assign(paramBoolean ? null : paramObjectStreamClass);
    if (this.protocol == 1) {
      paramObjectStreamClass.writeNonProxy(this);
    } else {
      writeClassDescriptor(paramObjectStreamClass);
    } 
    Class<?> clazz = paramObjectStreamClass.forClass();
    this.bout.setBlockDataMode(true);
    if (clazz != null && isCustomSubclass())
      ReflectUtil.checkPackageAccess(clazz); 
    annotateClass(clazz);
    this.bout.setBlockDataMode(false);
    this.bout.writeByte(120);
    writeClassDesc(paramObjectStreamClass.getSuperDesc(), false);
  }
  
  private void writeString(String paramString, boolean paramBoolean) throws IOException {
    this.handles.assign(paramBoolean ? null : paramString);
    long l = this.bout.getUTFLength(paramString);
    if (l <= 65535L) {
      this.bout.writeByte(116);
      this.bout.writeUTF(paramString, l);
    } else {
      this.bout.writeByte(124);
      this.bout.writeLongUTF(paramString, l);
    } 
  }
  
  private void writeArray(Object paramObject, ObjectStreamClass paramObjectStreamClass, boolean paramBoolean) throws IOException {
    this.bout.writeByte(117);
    writeClassDesc(paramObjectStreamClass, false);
    this.handles.assign(paramBoolean ? null : paramObject);
    Class<?> clazz = paramObjectStreamClass.forClass().getComponentType();
    if (clazz.isPrimitive()) {
      if (clazz == int.class) {
        int[] arrayOfInt = (int[])paramObject;
        this.bout.writeInt(arrayOfInt.length);
        this.bout.writeInts(arrayOfInt, 0, arrayOfInt.length);
      } else if (clazz == byte.class) {
        byte[] arrayOfByte = (byte[])paramObject;
        this.bout.writeInt(arrayOfByte.length);
        this.bout.write(arrayOfByte, 0, arrayOfByte.length, true);
      } else if (clazz == long.class) {
        long[] arrayOfLong = (long[])paramObject;
        this.bout.writeInt(arrayOfLong.length);
        this.bout.writeLongs(arrayOfLong, 0, arrayOfLong.length);
      } else if (clazz == float.class) {
        float[] arrayOfFloat = (float[])paramObject;
        this.bout.writeInt(arrayOfFloat.length);
        this.bout.writeFloats(arrayOfFloat, 0, arrayOfFloat.length);
      } else if (clazz == double.class) {
        double[] arrayOfDouble = (double[])paramObject;
        this.bout.writeInt(arrayOfDouble.length);
        this.bout.writeDoubles(arrayOfDouble, 0, arrayOfDouble.length);
      } else if (clazz == short.class) {
        short[] arrayOfShort = (short[])paramObject;
        this.bout.writeInt(arrayOfShort.length);
        this.bout.writeShorts(arrayOfShort, 0, arrayOfShort.length);
      } else if (clazz == char.class) {
        char[] arrayOfChar = (char[])paramObject;
        this.bout.writeInt(arrayOfChar.length);
        this.bout.writeChars(arrayOfChar, 0, arrayOfChar.length);
      } else if (clazz == boolean.class) {
        boolean[] arrayOfBoolean = (boolean[])paramObject;
        this.bout.writeInt(arrayOfBoolean.length);
        this.bout.writeBooleans(arrayOfBoolean, 0, arrayOfBoolean.length);
      } else {
        throw new InternalError();
      } 
    } else {
      Object[] arrayOfObject = (Object[])paramObject;
      int i = arrayOfObject.length;
      this.bout.writeInt(i);
      if (extendedDebugInfo)
        this.debugInfoStack.push("array (class \"" + paramObject
            .getClass().getName() + "\", size: " + i + ")"); 
      try {
        for (byte b = 0; b < i; b++) {
          if (extendedDebugInfo);
        } 
      } finally {
        if (extendedDebugInfo)
          this.debugInfoStack.pop(); 
      } 
    } 
  }
  
  private void writeEnum(Enum<?> paramEnum, ObjectStreamClass paramObjectStreamClass, boolean paramBoolean) throws IOException {
    this.bout.writeByte(126);
    ObjectStreamClass objectStreamClass = paramObjectStreamClass.getSuperDesc();
    writeClassDesc((objectStreamClass.forClass() == Enum.class) ? paramObjectStreamClass : objectStreamClass, false);
    this.handles.assign(paramBoolean ? null : paramEnum);
    writeString(paramEnum.name(), false);
  }
  
  private void writeOrdinaryObject(Object paramObject, ObjectStreamClass paramObjectStreamClass, boolean paramBoolean) throws IOException {
    if (extendedDebugInfo)
      this.debugInfoStack.push(((this.depth == 1) ? "root " : "") + "object (class \"" + paramObject
          
          .getClass().getName() + "\", " + paramObject.toString() + ")"); 
    try {
      paramObjectStreamClass.checkSerialize();
      this.bout.writeByte(115);
      writeClassDesc(paramObjectStreamClass, false);
      this.handles.assign(paramBoolean ? null : paramObject);
      if (paramObjectStreamClass.isExternalizable() && !paramObjectStreamClass.isProxy()) {
        writeExternalData((Externalizable)paramObject);
      } else {
        writeSerialData(paramObject, paramObjectStreamClass);
      } 
    } finally {
      if (extendedDebugInfo)
        this.debugInfoStack.pop(); 
    } 
  }
  
  private void writeExternalData(Externalizable paramExternalizable) throws IOException {
    PutFieldImpl putFieldImpl = this.curPut;
    this.curPut = null;
    if (extendedDebugInfo)
      this.debugInfoStack.push("writeExternal data"); 
    SerialCallbackContext serialCallbackContext = this.curContext;
    try {
      this.curContext = null;
      if (this.protocol == 1) {
        paramExternalizable.writeExternal((ObjectOutput)this);
      } else {
        this.bout.setBlockDataMode(true);
        paramExternalizable.writeExternal((ObjectOutput)this);
        this.bout.setBlockDataMode(false);
        this.bout.writeByte(120);
      } 
    } finally {
      this.curContext = serialCallbackContext;
      if (extendedDebugInfo)
        this.debugInfoStack.pop(); 
    } 
    this.curPut = putFieldImpl;
  }
  
  private void writeSerialData(Object paramObject, ObjectStreamClass paramObjectStreamClass) throws IOException {
    ObjectStreamClass.ClassDataSlot[] arrayOfClassDataSlot = paramObjectStreamClass.getClassDataLayout();
    for (byte b = 0; b < arrayOfClassDataSlot.length; b++) {
      ObjectStreamClass objectStreamClass = (arrayOfClassDataSlot[b]).desc;
      if (objectStreamClass.hasWriteObjectMethod()) {
        PutFieldImpl putFieldImpl = this.curPut;
        this.curPut = null;
        SerialCallbackContext serialCallbackContext = this.curContext;
        if (extendedDebugInfo)
          this.debugInfoStack.push("custom writeObject data (class \"" + objectStreamClass
              
              .getName() + "\")"); 
        try {
          this.curContext = new SerialCallbackContext(paramObject, objectStreamClass);
          this.bout.setBlockDataMode(true);
          objectStreamClass.invokeWriteObject(paramObject, this);
          this.bout.setBlockDataMode(false);
          this.bout.writeByte(120);
        } finally {
          this.curContext.setUsed();
          this.curContext = serialCallbackContext;
          if (extendedDebugInfo)
            this.debugInfoStack.pop(); 
        } 
        this.curPut = putFieldImpl;
      } else {
        defaultWriteFields(paramObject, objectStreamClass);
      } 
    } 
  }
  
  private void defaultWriteFields(Object paramObject, ObjectStreamClass paramObjectStreamClass) throws IOException {
    Class<?> clazz = paramObjectStreamClass.forClass();
    if (clazz != null && paramObject != null && !clazz.isInstance(paramObject))
      throw new ClassCastException(); 
    paramObjectStreamClass.checkDefaultSerialize();
    int i = paramObjectStreamClass.getPrimDataSize();
    if (this.primVals == null || this.primVals.length < i)
      this.primVals = new byte[i]; 
    paramObjectStreamClass.getPrimFieldValues(paramObject, this.primVals);
    this.bout.write(this.primVals, 0, i, false);
    ObjectStreamField[] arrayOfObjectStreamField = paramObjectStreamClass.getFields(false);
    Object[] arrayOfObject = new Object[paramObjectStreamClass.getNumObjFields()];
    int j = arrayOfObjectStreamField.length - arrayOfObject.length;
    paramObjectStreamClass.getObjFieldValues(paramObject, arrayOfObject);
    for (byte b = 0; b < arrayOfObject.length; b++) {
      if (extendedDebugInfo);
    } 
  }
  
  private void writeFatalException(IOException paramIOException) throws IOException {
    clear();
    boolean bool = this.bout.setBlockDataMode(false);
    try {
      this.bout.writeByte(123);
      writeObject0(paramIOException, false);
      clear();
    } finally {
      this.bout.setBlockDataMode(bool);
    } 
  }
  
  private static native void floatsToBytes(float[] paramArrayOffloat, int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3);
  
  private static native void doublesToBytes(double[] paramArrayOfdouble, int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3);
  
  private static class ObjectOutputStream {}
  
  private static class ObjectOutputStream {}
  
  private static class ObjectOutputStream {}
  
  private static class ObjectOutputStream {}
  
  private class ObjectOutputStream {}
  
  public static abstract class ObjectOutputStream {}
  
  private static class ObjectOutputStream {}
}
