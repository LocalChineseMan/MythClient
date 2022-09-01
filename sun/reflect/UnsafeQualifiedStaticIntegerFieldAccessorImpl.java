package sun.reflect;

import java.lang.reflect.Field;

class UnsafeQualifiedStaticIntegerFieldAccessorImpl extends UnsafeQualifiedStaticFieldAccessorImpl {
  UnsafeQualifiedStaticIntegerFieldAccessorImpl(Field paramField, boolean paramBoolean) {
    super(paramField, paramBoolean);
  }
  
  public Object get(Object paramObject) throws IllegalArgumentException {
    return new Integer(getInt(paramObject));
  }
  
  public boolean getBoolean(Object paramObject) throws IllegalArgumentException {
    throw newGetBooleanIllegalArgumentException();
  }
  
  public byte getByte(Object paramObject) throws IllegalArgumentException {
    throw newGetByteIllegalArgumentException();
  }
  
  public char getChar(Object paramObject) throws IllegalArgumentException {
    throw newGetCharIllegalArgumentException();
  }
  
  public short getShort(Object paramObject) throws IllegalArgumentException {
    throw newGetShortIllegalArgumentException();
  }
  
  public int getInt(Object paramObject) throws IllegalArgumentException {
    return unsafe.getIntVolatile(this.base, this.fieldOffset);
  }
  
  public long getLong(Object paramObject) throws IllegalArgumentException {
    return getInt(paramObject);
  }
  
  public float getFloat(Object paramObject) throws IllegalArgumentException {
    return getInt(paramObject);
  }
  
  public double getDouble(Object paramObject) throws IllegalArgumentException {
    return getInt(paramObject);
  }
  
  public void set(Object paramObject1, Object paramObject2) throws IllegalArgumentException, IllegalAccessException {
    if (this.isReadOnly)
      throwFinalFieldIllegalAccessException(paramObject2); 
    if (paramObject2 == null)
      throwSetIllegalArgumentException(paramObject2); 
    if (paramObject2 instanceof Byte) {
      unsafe.putIntVolatile(this.base, this.fieldOffset, ((Byte)paramObject2).byteValue());
      return;
    } 
    if (paramObject2 instanceof Short) {
      unsafe.putIntVolatile(this.base, this.fieldOffset, ((Short)paramObject2).shortValue());
      return;
    } 
    if (paramObject2 instanceof Character) {
      unsafe.putIntVolatile(this.base, this.fieldOffset, ((Character)paramObject2).charValue());
      return;
    } 
    if (paramObject2 instanceof Integer) {
      unsafe.putIntVolatile(this.base, this.fieldOffset, ((Integer)paramObject2).intValue());
      return;
    } 
    throwSetIllegalArgumentException(paramObject2);
  }
  
  public void setBoolean(Object paramObject, boolean paramBoolean) throws IllegalArgumentException, IllegalAccessException {
    throwSetIllegalArgumentException(paramBoolean);
  }
  
  public void setByte(Object paramObject, byte paramByte) throws IllegalArgumentException, IllegalAccessException {
    setInt(paramObject, paramByte);
  }
  
  public void setChar(Object paramObject, char paramChar) throws IllegalArgumentException, IllegalAccessException {
    setInt(paramObject, paramChar);
  }
  
  public void setShort(Object paramObject, short paramShort) throws IllegalArgumentException, IllegalAccessException {
    setInt(paramObject, paramShort);
  }
  
  public void setInt(Object paramObject, int paramInt) throws IllegalArgumentException, IllegalAccessException {
    if (this.isReadOnly)
      throwFinalFieldIllegalAccessException(paramInt); 
    unsafe.putIntVolatile(this.base, this.fieldOffset, paramInt);
  }
  
  public void setLong(Object paramObject, long paramLong) throws IllegalArgumentException, IllegalAccessException {
    throwSetIllegalArgumentException(paramLong);
  }
  
  public void setFloat(Object paramObject, float paramFloat) throws IllegalArgumentException, IllegalAccessException {
    throwSetIllegalArgumentException(paramFloat);
  }
  
  public void setDouble(Object paramObject, double paramDouble) throws IllegalArgumentException, IllegalAccessException {
    throwSetIllegalArgumentException(paramDouble);
  }
}
