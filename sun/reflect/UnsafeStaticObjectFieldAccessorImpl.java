package sun.reflect;

import java.lang.reflect.Field;

class UnsafeStaticObjectFieldAccessorImpl extends UnsafeStaticFieldAccessorImpl {
  UnsafeStaticObjectFieldAccessorImpl(Field paramField) {
    super(paramField);
  }
  
  public Object get(Object paramObject) throws IllegalArgumentException {
    return unsafe.getObject(this.base, this.fieldOffset);
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
    throw newGetIntIllegalArgumentException();
  }
  
  public long getLong(Object paramObject) throws IllegalArgumentException {
    throw newGetLongIllegalArgumentException();
  }
  
  public float getFloat(Object paramObject) throws IllegalArgumentException {
    throw newGetFloatIllegalArgumentException();
  }
  
  public double getDouble(Object paramObject) throws IllegalArgumentException {
    throw newGetDoubleIllegalArgumentException();
  }
  
  public void set(Object paramObject1, Object paramObject2) throws IllegalArgumentException, IllegalAccessException {
    if (this.isFinal)
      throwFinalFieldIllegalAccessException(paramObject2); 
    if (paramObject2 != null && 
      !this.field.getType().isAssignableFrom(paramObject2.getClass()))
      throwSetIllegalArgumentException(paramObject2); 
    unsafe.putObject(this.base, this.fieldOffset, paramObject2);
  }
  
  public void setBoolean(Object paramObject, boolean paramBoolean) throws IllegalArgumentException, IllegalAccessException {
    throwSetIllegalArgumentException(paramBoolean);
  }
  
  public void setByte(Object paramObject, byte paramByte) throws IllegalArgumentException, IllegalAccessException {
    throwSetIllegalArgumentException(paramByte);
  }
  
  public void setChar(Object paramObject, char paramChar) throws IllegalArgumentException, IllegalAccessException {
    throwSetIllegalArgumentException(paramChar);
  }
  
  public void setShort(Object paramObject, short paramShort) throws IllegalArgumentException, IllegalAccessException {
    throwSetIllegalArgumentException(paramShort);
  }
  
  public void setInt(Object paramObject, int paramInt) throws IllegalArgumentException, IllegalAccessException {
    throwSetIllegalArgumentException(paramInt);
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
