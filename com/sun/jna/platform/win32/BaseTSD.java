package com.sun.jna.platform.win32;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public interface BaseTSD {
  public static class BaseTSD {}
  
  public static class BaseTSD {}
  
  public static class ULONG_PTR extends IntegerType {
    public ULONG_PTR() {
      this(0L);
    }
    
    public ULONG_PTR(long value) {
      super(Native.POINTER_SIZE, value, true);
    }
    
    public Pointer toPointer() {
      return Pointer.createConstant(longValue());
    }
  }
  
  public static class ULONG_PTRByReference extends ByReference {
    public ULONG_PTRByReference() {
      this(new BaseTSD.ULONG_PTR(0L));
    }
    
    public ULONG_PTRByReference(BaseTSD.ULONG_PTR value) {
      super(Native.POINTER_SIZE);
      setValue(value);
    }
    
    public void setValue(BaseTSD.ULONG_PTR value) {
      if (Native.POINTER_SIZE == 4) {
        getPointer().setInt(0L, value.intValue());
      } else {
        getPointer().setLong(0L, value.longValue());
      } 
    }
    
    public BaseTSD.ULONG_PTR getValue() {
      return new BaseTSD.ULONG_PTR((Native.POINTER_SIZE == 4) ? 
          getPointer().getInt(0L) : 
          getPointer().getLong(0L));
    }
  }
  
  public static class BaseTSD {}
  
  public static class SIZE_T extends ULONG_PTR {
    public SIZE_T() {
      this(0L);
    }
    
    public SIZE_T(long value) {
      super(value);
    }
  }
}
