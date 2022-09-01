package com.sun.jna.platform.win32;

import com.sun.jna.IntegerType;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.ptr.ByReference;

public interface WinDef {
  public static final int MAX_PATH = 260;
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class DWORD extends IntegerType implements Comparable<DWORD> {
    public static final int SIZE = 4;
    
    public DWORD() {
      this(0L);
    }
    
    public DWORD(long value) {
      super(4, value, true);
    }
    
    public WinDef.WORD getLow() {
      return new WinDef.WORD(longValue() & 0xFFFFL);
    }
    
    public WinDef.WORD getHigh() {
      return new WinDef.WORD(longValue() >> 16L & 0xFFFFL);
    }
    
    public int compareTo(DWORD other) {
      return compare(this, other);
    }
  }
  
  public static class DWORDByReference extends ByReference {
    public DWORDByReference() {
      this(new WinDef.DWORD(0L));
    }
    
    public DWORDByReference(WinDef.DWORD value) {
      super(4);
      setValue(value);
    }
    
    public void setValue(WinDef.DWORD value) {
      getPointer().setInt(0L, value.intValue());
    }
    
    public WinDef.DWORD getValue() {
      return new WinDef.DWORD(getPointer().getInt(0L));
    }
  }
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class HRSRC extends WinNT.HANDLE {
    public HRSRC() {}
    
    public HRSRC(Pointer p) {
      super(p);
    }
  }
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class HWND extends WinNT.HANDLE {
    public HWND() {}
    
    public HWND(Pointer p) {
      super(p);
    }
  }
  
  public static class HINSTANCE extends WinNT.HANDLE {}
  
  public static class HMODULE extends HINSTANCE {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class ULONGByReference extends ByReference {
    public ULONGByReference() {
      this(new WinDef.ULONG(0L));
    }
    
    public ULONGByReference(WinDef.ULONG value) {
      super(WinDef.ULONG.SIZE);
      setValue(value);
    }
    
    public void setValue(WinDef.ULONG value) {
      getPointer().setInt(0L, value.intValue());
    }
    
    public WinDef.ULONG getValue() {
      return new WinDef.ULONG(getPointer().getInt(0L));
    }
  }
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class LPVOID extends PointerType {
    public LPVOID() {}
    
    public LPVOID(Pointer p) {
      super(p);
    }
  }
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class LCID extends DWORD {
    public LCID() {
      super(0L);
    }
    
    public LCID(long value) {
      super(value);
    }
  }
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
  
  public static class WinDef {}
}
