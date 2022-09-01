package com.sun.proxy;

import com.sun.jna.LastErrorException;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Wincon;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import notthatuwu.xyz.mythrecode.api.utils.Kernel32;

public final class $Proxy20 extends Proxy implements Kernel32 {
  private static Method m85;
  
  private static Method m44;
  
  private static Method m80;
  
  private static Method m31;
  
  private static Method m13;
  
  private static Method m6;
  
  private static Method m146;
  
  private static Method m90;
  
  private static Method m14;
  
  private static Method m147;
  
  private static Method m183;
  
  private static Method m42;
  
  private static Method m84;
  
  private static Method m125;
  
  private static Method m129;
  
  private static Method m187;
  
  private static Method m56;
  
  private static Method m50;
  
  private static Method m163;
  
  private static Method m12;
  
  private static Method m113;
  
  private static Method m194;
  
  private static Method m122;
  
  private static Method m9;
  
  private static Method m59;
  
  private static Method m53;
  
  private static Method m48;
  
  private static Method m196;
  
  private static Method m4;
  
  private static Method m0;
  
  private static Method m19;
  
  private static Method m190;
  
  private static Method m43;
  
  private static Method m98;
  
  private static Method m157;
  
  private static Method m150;
  
  private static Method m128;
  
  private static Method m182;
  
  private static Method m38;
  
  private static Method m152;
  
  private static Method m18;
  
  private static Method m25;
  
  private static Method m24;
  
  private static Method m3;
  
  private static Method m82;
  
  private static Method m172;
  
  private static Method m199;
  
  private static Method m37;
  
  private static Method m109;
  
  private static Method m1;
  
  private static Method m165;
  
  private static Method m66;
  
  private static Method m34;
  
  private static Method m181;
  
  private static Method m11;
  
  private static Method m47;
  
  private static Method m45;
  
  private static Method m176;
  
  private static Method m15;
  
  private static Method m17;
  
  private static Method m158;
  
  private static Method m160;
  
  private static Method m168;
  
  private static Method m10;
  
  private static Method m40;
  
  private static Method m200;
  
  private static Method m159;
  
  private static Method m23;
  
  private static Method m135;
  
  private static Method m155;
  
  private static Method m61;
  
  private static Method m99;
  
  private static Method m29;
  
  private static Method m81;
  
  private static Method m131;
  
  private static Method m97;
  
  private static Method m201;
  
  private static Method m154;
  
  private static Method m136;
  
  private static Method m120;
  
  private static Method m141;
  
  private static Method m145;
  
  private static Method m55;
  
  private static Method m175;
  
  private static Method m130;
  
  private static Method m72;
  
  private static Method m148;
  
  private static Method m188;
  
  private static Method m102;
  
  private static Method m63;
  
  private static Method m86;
  
  private static Method m170;
  
  private static Method m118;
  
  private static Method m73;
  
  private static Method m189;
  
  private static Method m111;
  
  private static Method m185;
  
  private static Method m20;
  
  private static Method m162;
  
  private static Method m96;
  
  private static Method m126;
  
  private static Method m77;
  
  private static Method m51;
  
  private static Method m140;
  
  private static Method m115;
  
  private static Method m76;
  
  private static Method m26;
  
  private static Method m124;
  
  private static Method m112;
  
  private static Method m180;
  
  private static Method m103;
  
  private static Method m177;
  
  private static Method m149;
  
  private static Method m121;
  
  private static Method m186;
  
  private static Method m49;
  
  private static Method m106;
  
  private static Method m92;
  
  private static Method m95;
  
  private static Method m16;
  
  private static Method m134;
  
  private static Method m184;
  
  private static Method m178;
  
  private static Method m46;
  
  private static Method m35;
  
  private static Method m67;
  
  private static Method m88;
  
  private static Method m33;
  
  private static Method m105;
  
  private static Method m104;
  
  private static Method m198;
  
  private static Method m69;
  
  private static Method m114;
  
  private static Method m36;
  
  private static Method m137;
  
  private static Method m87;
  
  private static Method m78;
  
  private static Method m202;
  
  private static Method m22;
  
  private static Method m133;
  
  private static Method m174;
  
  private static Method m52;
  
  private static Method m192;
  
  private static Method m119;
  
  private static Method m8;
  
  private static Method m94;
  
  private static Method m101;
  
  private static Method m32;
  
  private static Method m54;
  
  private static Method m108;
  
  private static Method m5;
  
  private static Method m60;
  
  private static Method m107;
  
  private static Method m156;
  
  private static Method m7;
  
  private static Method m171;
  
  private static Method m75;
  
  private static Method m93;
  
  private static Method m110;
  
  private static Method m167;
  
  private static Method m191;
  
  private static Method m28;
  
  private static Method m65;
  
  private static Method m193;
  
  private static Method m123;
  
  private static Method m161;
  
  private static Method m164;
  
  private static Method m144;
  
  private static Method m2;
  
  private static Method m83;
  
  private static Method m64;
  
  private static Method m197;
  
  private static Method m100;
  
  private static Method m70;
  
  private static Method m30;
  
  private static Method m117;
  
  private static Method m57;
  
  private static Method m179;
  
  private static Method m127;
  
  private static Method m58;
  
  private static Method m153;
  
  private static Method m74;
  
  private static Method m142;
  
  private static Method m62;
  
  private static Method m166;
  
  private static Method m132;
  
  private static Method m89;
  
  private static Method m151;
  
  private static Method m41;
  
  private static Method m169;
  
  private static Method m143;
  
  private static Method m139;
  
  private static Method m138;
  
  private static Method m79;
  
  private static Method m116;
  
  private static Method m27;
  
  private static Method m71;
  
  private static Method m173;
  
  private static Method m195;
  
  private static Method m39;
  
  private static Method m68;
  
  private static Method m21;
  
  private static Method m91;
  
  public $Proxy20(InvocationHandler paramInvocationHandler) {
    super(paramInvocationHandler);
  }
  
  public final boolean FreeEnvironmentStrings(Pointer paramPointer) {
    try {
      return ((Boolean)this.h.invoke(this, m85, new Object[] { paramPointer })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinDef.DWORD GetPrivateProfileSectionNames(char[] paramArrayOfchar, WinDef.DWORD paramDWORD, String paramString) {
    try {
      return (WinDef.DWORD)this.h.invoke(this, m44, new Object[] { paramArrayOfchar, paramDWORD, paramString });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetNamedPipeHandleState(WinNT.HANDLE paramHANDLE, IntByReference paramIntByReference1, IntByReference paramIntByReference2, IntByReference paramIntByReference3) {
    try {
      return ((Boolean)this.h.invoke(this, m80, new Object[] { paramHANDLE, paramIntByReference1, paramIntByReference2, paramIntByReference3 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE OpenMutex(int paramInt, boolean paramBoolean, String paramString) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m31, new Object[] { Integer.valueOf(paramInt), Boolean.valueOf(paramBoolean), paramString });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetDiskFreeSpaceEx(String paramString, WinNT.LARGE_INTEGER paramLARGE_INTEGER1, WinNT.LARGE_INTEGER paramLARGE_INTEGER2, WinNT.LARGE_INTEGER paramLARGE_INTEGER3) {
    try {
      return ((Boolean)this.h.invoke(this, m13, new Object[] { paramString, paramLARGE_INTEGER1, paramLARGE_INTEGER2, paramLARGE_INTEGER3 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean FindClose(WinNT.HANDLE paramHANDLE) {
    try {
      return ((Boolean)this.h.invoke(this, m6, new Object[] { paramHANDLE })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinDef.HMODULE LoadLibraryEx(String paramString, WinNT.HANDLE paramHANDLE, int paramInt) {
    try {
      return (WinDef.HMODULE)this.h.invoke(this, m146, new Object[] { paramString, paramHANDLE, Integer.valueOf(paramInt) });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetLogicalProcessorInformationEx(int paramInt, Pointer paramPointer, WinDef.DWORDByReference paramDWORDByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m90, new Object[] { Integer.valueOf(paramInt), paramPointer, paramDWORDByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetVolumePathName(String paramString, char[] paramArrayOfchar, int paramInt) {
    try {
      return ((Boolean)this.h.invoke(this, m14, new Object[] { paramString, paramArrayOfchar, Integer.valueOf(paramInt) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinDef.DWORD GetVersion() {
    try {
      return (WinDef.DWORD)this.h.invoke(this, m147, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean AllocConsole() {
    try {
      return ((Boolean)this.h.invoke(this, m183, null)).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean TransactNamedPipe(WinNT.HANDLE paramHANDLE, byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2, IntByReference paramIntByReference, WinBase.OVERLAPPED paramOVERLAPPED) {
    try {
      return ((Boolean)this.h.invoke(this, m42, new Object[] { paramHANDLE, paramArrayOfbyte1, Integer.valueOf(paramInt1), paramArrayOfbyte2, Integer.valueOf(paramInt2), paramIntByReference, paramOVERLAPPED })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HRESULT UnregisterApplicationRestart() {
    try {
      return (WinNT.HRESULT)this.h.invoke(this, m84, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean ResetEvent(WinNT.HANDLE paramHANDLE) {
    try {
      return ((Boolean)this.h.invoke(this, m125, new Object[] { paramHANDLE })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean CopyFile(String paramString1, String paramString2, boolean paramBoolean) {
    try {
      return ((Boolean)this.h.invoke(this, m129, new Object[] { paramString1, paramString2, Boolean.valueOf(paramBoolean) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetConsoleMode(WinNT.HANDLE paramHANDLE, int paramInt) {
    try {
      return ((Boolean)this.h.invoke(this, m187, new Object[] { paramHANDLE, Integer.valueOf(paramInt) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetNamedPipeServerSessionId(WinNT.HANDLE paramHANDLE, WinDef.ULONGByReference paramULONGByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m56, new Object[] { paramHANDLE, paramULONGByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean WritePrivateProfileString(String paramString1, String paramString2, String paramString3, String paramString4) {
    try {
      return ((Boolean)this.h.invoke(this, m50, new Object[] { paramString1, paramString2, paramString3, paramString4 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean CallNamedPipe(String paramString, byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2, IntByReference paramIntByReference, int paramInt3) {
    try {
      return ((Boolean)this.h.invoke(this, m163, new Object[] { paramString, paramArrayOfbyte1, Integer.valueOf(paramInt1), paramArrayOfbyte2, Integer.valueOf(paramInt2), paramIntByReference, Integer.valueOf(paramInt3) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetVolumeInformation(String paramString, char[] paramArrayOfchar1, int paramInt1, IntByReference paramIntByReference1, IntByReference paramIntByReference2, IntByReference paramIntByReference3, char[] paramArrayOfchar2, int paramInt2) {
    try {
      return ((Boolean)this.h.invoke(this, m12, new Object[] { paramString, paramArrayOfchar1, Integer.valueOf(paramInt1), paramIntByReference1, paramIntByReference2, paramIntByReference3, paramArrayOfchar2, Integer.valueOf(paramInt2) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int GetLastError() {
    try {
      return ((Integer)this.h.invoke(this, m113, null)).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int GetConsoleOriginalTitle(char[] paramArrayOfchar, int paramInt) {
    try {
      return ((Integer)this.h.invoke(this, m194, new Object[] { paramArrayOfchar, Integer.valueOf(paramInt) })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final void SetLastError(int paramInt) {
    try {
      this.h.invoke(this, m122, new Object[] { Integer.valueOf(paramInt) });
      return;
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int GetFileAttributes(String paramString) {
    try {
      return ((Integer)this.h.invoke(this, m9, new Object[] { paramString })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int GetEnvironmentVariable(String paramString, char[] paramArrayOfchar, int paramInt) {
    try {
      return ((Integer)this.h.invoke(this, m59, new Object[] { paramString, paramArrayOfchar, Integer.valueOf(paramInt) })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinDef.DWORD GetPrivateProfileString(String paramString1, String paramString2, String paramString3, char[] paramArrayOfchar, WinDef.DWORD paramDWORD, String paramString4) {
    try {
      return (WinDef.DWORD)this.h.invoke(this, m53, new Object[] { paramString1, paramString2, paramString3, paramArrayOfchar, paramDWORD, paramString4 });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean FileTimeToSystemTime(WinBase.FILETIME paramFILETIME, WinBase.SYSTEMTIME paramSYSTEMTIME) {
    try {
      return ((Boolean)this.h.invoke(this, m48, new Object[] { paramFILETIME, paramSYSTEMTIME })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetConsoleScreenBufferInfo(WinNT.HANDLE paramHANDLE, Wincon.CONSOLE_SCREEN_BUFFER_INFO paramCONSOLE_SCREEN_BUFFER_INFO) {
    try {
      return ((Boolean)this.h.invoke(this, m196, new Object[] { paramHANDLE, paramCONSOLE_SCREEN_BUFFER_INFO })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int hashCode() {
    try {
      return ((Integer)this.h.invoke(this, m0, null)).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE CreateIoCompletionPort(WinNT.HANDLE paramHANDLE1, WinNT.HANDLE paramHANDLE2, Pointer paramPointer, int paramInt) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m19, new Object[] { paramHANDLE1, paramHANDLE2, paramPointer, Integer.valueOf(paramInt) });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetConsoleTitle(String paramString) {
    try {
      return ((Boolean)this.h.invoke(this, m190, new Object[] { paramString })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinDef.LCID GetUserDefaultLCID() {
    try {
      return (WinDef.LCID)this.h.invoke(this, m43, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE CreateFileMapping(WinNT.HANDLE paramHANDLE, WinBase.SECURITY_ATTRIBUTES paramSECURITY_ATTRIBUTES, int paramInt1, int paramInt2, int paramInt3, String paramString) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m98, new Object[] { paramHANDLE, paramSECURITY_ATTRIBUTES, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramString });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinDef.DWORD GetTempPath(WinDef.DWORD paramDWORD, char[] paramArrayOfchar) {
    try {
      return (WinDef.DWORD)this.h.invoke(this, m157, new Object[] { paramDWORD, paramArrayOfchar });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinDef.HRSRC FindResource(WinDef.HMODULE paramHMODULE, Pointer paramPointer1, Pointer paramPointer2) {
    try {
      return (WinDef.HRSRC)this.h.invoke(this, m150, new Object[] { paramHMODULE, paramPointer1, paramPointer2 });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final long GetTickCount64() {
    try {
      return ((Long)this.h.invoke(this, m128, null)).longValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean AttachConsole(int paramInt) {
    try {
      return ((Boolean)this.h.invoke(this, m182, new Object[] { Integer.valueOf(paramInt) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean ReadProcessMemory(WinNT.HANDLE paramHANDLE, Pointer paramPointer1, Pointer paramPointer2, int paramInt, IntByReference paramIntByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m38, new Object[] { paramHANDLE, paramPointer1, paramPointer2, Integer.valueOf(paramInt), paramIntByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int GetFileType(WinNT.HANDLE paramHANDLE) {
    try {
      return ((Integer)this.h.invoke(this, m152, new Object[] { paramHANDLE })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final Pointer LocalFree(Pointer paramPointer) {
    try {
      return (Pointer)this.h.invoke(this, m18, new Object[] { paramPointer });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean CreateDirectory(String paramString, WinBase.SECURITY_ATTRIBUTES paramSECURITY_ATTRIBUTES) {
    try {
      return ((Boolean)this.h.invoke(this, m25, new Object[] { paramString, paramSECURITY_ATTRIBUTES })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean CloseHandle(WinNT.HANDLE paramHANDLE) {
    try {
      return ((Boolean)this.h.invoke(this, m24, new Object[] { paramHANDLE })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean IsDebuggerPresent() {
    try {
      return ((Boolean)this.h.invoke(this, m3, null)).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean EnumResourceNames(WinDef.HMODULE paramHMODULE, Pointer paramPointer1, WinBase.EnumResNameProc paramEnumResNameProc, Pointer paramPointer2) {
    try {
      return ((Boolean)this.h.invoke(this, m82, new Object[] { paramHMODULE, paramPointer1, paramEnumResNameProc, paramPointer2 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean Thread32First(WinNT.HANDLE paramHANDLE, Tlhelp32.THREADENTRY32 paramTHREADENTRY32) {
    try {
      return ((Boolean)this.h.invoke(this, m172, new Object[] { paramHANDLE, paramTHREADENTRY32 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int GetConsoleOutputCP() {
    try {
      return ((Integer)this.h.invoke(this, m199, null)).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean VirtualFreeEx(WinNT.HANDLE paramHANDLE, Pointer paramPointer, BaseTSD.SIZE_T paramSIZE_T, int paramInt) {
    try {
      return ((Boolean)this.h.invoke(this, m37, new Object[] { paramHANDLE, paramPointer, paramSIZE_T, Integer.valueOf(paramInt) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetSystemTime(WinBase.SYSTEMTIME paramSYSTEMTIME) {
    try {
      return ((Boolean)this.h.invoke(this, m109, new Object[] { paramSYSTEMTIME })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean equals(Object paramObject) {
    try {
      return ((Boolean)this.h.invoke(this, m1, new Object[] { paramObject })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean Process32First(WinNT.HANDLE paramHANDLE, Tlhelp32.PROCESSENTRY32 paramPROCESSENTRY32) {
    try {
      return ((Boolean)this.h.invoke(this, m165, new Object[] { paramHANDLE, paramPROCESSENTRY32 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE FindFirstVolumeMountPoint(String paramString, char[] paramArrayOfchar, int paramInt) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m66, new Object[] { paramString, paramArrayOfchar, Integer.valueOf(paramInt) });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean ReleaseMutex(WinNT.HANDLE paramHANDLE) {
    try {
      return ((Boolean)this.h.invoke(this, m34, new Object[] { paramHANDLE })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE GetStdHandle(int paramInt) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m181, new Object[] { Integer.valueOf(paramInt) });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int SetFileTime(WinNT.HANDLE paramHANDLE, WinBase.FILETIME paramFILETIME1, WinBase.FILETIME paramFILETIME2, WinBase.FILETIME paramFILETIME3) {
    try {
      return ((Integer)this.h.invoke(this, m11, new Object[] { paramHANDLE, paramFILETIME1, paramFILETIME2, paramFILETIME3 })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetNamedPipeClientSessionId(WinNT.HANDLE paramHANDLE, WinDef.ULONGByReference paramULONGByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m47, new Object[] { paramHANDLE, paramULONGByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean ProcessIdToSessionId(int paramInt, IntByReference paramIntByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m45, new Object[] { Integer.valueOf(paramInt), paramIntByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean CreateProcessW(String paramString1, char[] paramArrayOfchar, WinBase.SECURITY_ATTRIBUTES paramSECURITY_ATTRIBUTES1, WinBase.SECURITY_ATTRIBUTES paramSECURITY_ATTRIBUTES2, boolean paramBoolean, WinDef.DWORD paramDWORD, Pointer paramPointer, String paramString2, WinBase.STARTUPINFO paramSTARTUPINFO, WinBase.PROCESS_INFORMATION paramPROCESS_INFORMATION) {
    try {
      return ((Boolean)this.h.invoke(this, m176, new Object[] { paramString1, paramArrayOfchar, paramSECURITY_ATTRIBUTES1, paramSECURITY_ATTRIBUTES2, Boolean.valueOf(paramBoolean), paramDWORD, paramPointer, paramString2, paramSTARTUPINFO, paramPROCESS_INFORMATION })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE GetCurrentProcess() {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m15, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int FormatMessage(int paramInt1, Pointer paramPointer1, int paramInt2, int paramInt3, PointerByReference paramPointerByReference, int paramInt4, Pointer paramPointer2) {
    try {
      return ((Integer)this.h.invoke(this, m17, new Object[] { Integer.valueOf(paramInt1), paramPointer1, Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), paramPointerByReference, Integer.valueOf(paramInt4), paramPointer2 })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final BaseTSD.SIZE_T VirtualQueryEx(WinNT.HANDLE paramHANDLE, Pointer paramPointer, WinNT.MEMORY_BASIC_INFORMATION paramMEMORY_BASIC_INFORMATION, BaseTSD.SIZE_T paramSIZE_T) {
    try {
      return (BaseTSD.SIZE_T)this.h.invoke(this, m158, new Object[] { paramHANDLE, paramPointer, paramMEMORY_BASIC_INFORMATION, paramSIZE_T });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetVersionEx(WinNT.OSVERSIONINFOEX paramOSVERSIONINFOEX) {
    try {
      return ((Boolean)this.h.invoke(this, m160, new Object[] { paramOSVERSIONINFOEX })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE OpenThread(int paramInt1, boolean paramBoolean, int paramInt2) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m168, new Object[] { Integer.valueOf(paramInt1), Boolean.valueOf(paramBoolean), Integer.valueOf(paramInt2) });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetFileAttributes(String paramString, WinDef.DWORD paramDWORD) {
    try {
      return ((Boolean)this.h.invoke(this, m10, new Object[] { paramString, paramDWORD })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetVolumeNameForVolumeMountPoint(String paramString, char[] paramArrayOfchar, int paramInt) {
    try {
      return ((Boolean)this.h.invoke(this, m40, new Object[] { paramString, paramArrayOfchar, Integer.valueOf(paramInt) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetConsoleOutputCP(int paramInt) {
    try {
      return ((Boolean)this.h.invoke(this, m200, new Object[] { Integer.valueOf(paramInt) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetVersionEx(WinNT.OSVERSIONINFO paramOSVERSIONINFO) {
    try {
      return ((Boolean)this.h.invoke(this, m159, new Object[] { paramOSVERSIONINFO })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean DeleteFile(String paramString) {
    try {
      return ((Boolean)this.h.invoke(this, m23, new Object[] { paramString })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetFileTime(WinNT.HANDLE paramHANDLE, WinBase.FILETIME paramFILETIME1, WinBase.FILETIME paramFILETIME2, WinBase.FILETIME paramFILETIME3) {
    try {
      return ((Boolean)this.h.invoke(this, m135, new Object[] { paramHANDLE, paramFILETIME1, paramFILETIME2, paramFILETIME3 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final Pointer MapViewOfFile(WinNT.HANDLE paramHANDLE, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    try {
      return (Pointer)this.h.invoke(this, m155, new Object[] { paramHANDLE, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4) });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int ExpandEnvironmentStrings(String paramString, Pointer paramPointer, int paramInt) {
    try {
      return ((Integer)this.h.invoke(this, m61, new Object[] { paramString, paramPointer, Integer.valueOf(paramInt) })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetProcessAffinityMask(WinNT.HANDLE paramHANDLE, BaseTSD.ULONG_PTRByReference paramULONG_PTRByReference1, BaseTSD.ULONG_PTRByReference paramULONG_PTRByReference2) {
    try {
      return ((Boolean)this.h.invoke(this, m99, new Object[] { paramHANDLE, paramULONG_PTRByReference1, paramULONG_PTRByReference2 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean Module32NextW(WinNT.HANDLE paramHANDLE, Tlhelp32.MODULEENTRY32W paramMODULEENTRY32W) {
    try {
      return ((Boolean)this.h.invoke(this, m29, new Object[] { paramHANDLE, paramMODULEENTRY32W })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean DisconnectNamedPipe(WinNT.HANDLE paramHANDLE) {
    try {
      return ((Boolean)this.h.invoke(this, m81, new Object[] { paramHANDLE })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean TerminateProcess(WinNT.HANDLE paramHANDLE, int paramInt) {
    try {
      return ((Boolean)this.h.invoke(this, m131, new Object[] { paramHANDLE, Integer.valueOf(paramInt) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int GetCurrentProcessId() {
    try {
      return ((Integer)this.h.invoke(this, m97, null)).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetNumberOfConsoleMouseButtons(IntByReference paramIntByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m201, new Object[] { paramIntByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean ConnectNamedPipe(WinNT.HANDLE paramHANDLE, WinBase.OVERLAPPED paramOVERLAPPED) {
    try {
      return ((Boolean)this.h.invoke(this, m154, new Object[] { paramHANDLE, paramOVERLAPPED })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE OpenProcess(int paramInt1, boolean paramBoolean, int paramInt2) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m136, new Object[] { Integer.valueOf(paramInt1), Boolean.valueOf(paramBoolean), Integer.valueOf(paramInt2) });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean MoveFile(String paramString1, String paramString2) {
    try {
      return ((Boolean)this.h.invoke(this, m120, new Object[] { paramString1, paramString2 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean DefineDosDevice(int paramInt, String paramString1, String paramString2) {
    try {
      return ((Boolean)this.h.invoke(this, m141, new Object[] { Integer.valueOf(paramInt), paramString1, paramString2 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE FindFirstFileEx(String paramString, int paramInt1, Pointer paramPointer1, int paramInt2, Pointer paramPointer2, WinDef.DWORD paramDWORD) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m145, new Object[] { paramString, Integer.valueOf(paramInt1), paramPointer1, Integer.valueOf(paramInt2), paramPointer2, paramDWORD });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean EnumResourceTypes(WinDef.HMODULE paramHMODULE, WinBase.EnumResTypeProc paramEnumResTypeProc, Pointer paramPointer) {
    try {
      return ((Boolean)this.h.invoke(this, m55, new Object[] { paramHMODULE, paramEnumResTypeProc, paramPointer })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetCommTimeouts(WinNT.HANDLE paramHANDLE, WinBase.COMMTIMEOUTS paramCOMMTIMEOUTS) {
    try {
      return ((Boolean)this.h.invoke(this, m175, new Object[] { paramHANDLE, paramCOMMTIMEOUTS })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final void GetSystemTime(WinBase.SYSTEMTIME paramSYSTEMTIME) {
    try {
      this.h.invoke(this, m130, new Object[] { paramSYSTEMTIME });
      return;
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean FileTimeToLocalFileTime(WinBase.FILETIME paramFILETIME1, WinBase.FILETIME paramFILETIME2) {
    try {
      return ((Boolean)this.h.invoke(this, m72, new Object[] { paramFILETIME1, paramFILETIME2 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean CreateProcess(String paramString1, String paramString2, WinBase.SECURITY_ATTRIBUTES paramSECURITY_ATTRIBUTES1, WinBase.SECURITY_ATTRIBUTES paramSECURITY_ATTRIBUTES2, boolean paramBoolean, WinDef.DWORD paramDWORD, Pointer paramPointer, String paramString3, WinBase.STARTUPINFO paramSTARTUPINFO, WinBase.PROCESS_INFORMATION paramPROCESS_INFORMATION) {
    try {
      return ((Boolean)this.h.invoke(this, m148, new Object[] { paramString1, paramString2, paramSECURITY_ATTRIBUTES1, paramSECURITY_ATTRIBUTES2, Boolean.valueOf(paramBoolean), paramDWORD, paramPointer, paramString3, paramSTARTUPINFO, paramPROCESS_INFORMATION })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int GetConsoleTitle(char[] paramArrayOfchar, int paramInt) {
    try {
      return ((Integer)this.h.invoke(this, m188, new Object[] { paramArrayOfchar, Integer.valueOf(paramInt) })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int WaitForMultipleObjects(int paramInt1, WinNT.HANDLE[] paramArrayOfHANDLE, boolean paramBoolean, int paramInt2) {
    try {
      return ((Integer)this.h.invoke(this, m102, new Object[] { Integer.valueOf(paramInt1), paramArrayOfHANDLE, Boolean.valueOf(paramBoolean), Integer.valueOf(paramInt2) })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetNamedPipeClientComputerName(WinNT.HANDLE paramHANDLE, char[] paramArrayOfchar, int paramInt) {
    try {
      return ((Boolean)this.h.invoke(this, m63, new Object[] { paramHANDLE, paramArrayOfchar, Integer.valueOf(paramInt) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int GetCurrentThreadId() {
    try {
      return ((Integer)this.h.invoke(this, m86, null)).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetDiskFreeSpace(String paramString, WinDef.DWORDByReference paramDWORDByReference1, WinDef.DWORDByReference paramDWORDByReference2, WinDef.DWORDByReference paramDWORDByReference3, WinDef.DWORDByReference paramDWORDByReference4) {
    try {
      return ((Boolean)this.h.invoke(this, m170, new Object[] { paramString, paramDWORDByReference1, paramDWORDByReference2, paramDWORDByReference3, paramDWORDByReference4 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean FlushFileBuffers(WinNT.HANDLE paramHANDLE) {
    try {
      return ((Boolean)this.h.invoke(this, m118, new Object[] { paramHANDLE })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HRESULT RegisterApplicationRestart(char[] paramArrayOfchar, int paramInt) {
    try {
      return (WinNT.HRESULT)this.h.invoke(this, m73, new Object[] { paramArrayOfchar, Integer.valueOf(paramInt) });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetConsoleCP(int paramInt) {
    try {
      return ((Boolean)this.h.invoke(this, m189, new Object[] { Integer.valueOf(paramInt) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetSystemTimes(WinBase.FILETIME paramFILETIME1, WinBase.FILETIME paramFILETIME2, WinBase.FILETIME paramFILETIME3) {
    try {
      return ((Boolean)this.h.invoke(this, m111, new Object[] { paramFILETIME1, paramFILETIME2, paramFILETIME3 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean ReadConsoleInput(WinNT.HANDLE paramHANDLE, Wincon.INPUT_RECORD[] paramArrayOfINPUT_RECORD, int paramInt, IntByReference paramIntByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m185, new Object[] { paramHANDLE, paramArrayOfINPUT_RECORD, Integer.valueOf(paramInt), paramIntByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetQueuedCompletionStatus(WinNT.HANDLE paramHANDLE, IntByReference paramIntByReference, BaseTSD.ULONG_PTRByReference paramULONG_PTRByReference, PointerByReference paramPointerByReference, int paramInt) {
    try {
      return ((Boolean)this.h.invoke(this, m20, new Object[] { paramHANDLE, paramIntByReference, paramULONG_PTRByReference, paramPointerByReference, Integer.valueOf(paramInt) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean PeekNamedPipe(WinNT.HANDLE paramHANDLE, byte[] paramArrayOfbyte, int paramInt, IntByReference paramIntByReference1, IntByReference paramIntByReference2, IntByReference paramIntByReference3) {
    try {
      return ((Boolean)this.h.invoke(this, m162, new Object[] { paramHANDLE, paramArrayOfbyte, Integer.valueOf(paramInt), paramIntByReference1, paramIntByReference2, paramIntByReference3 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetExitCodeProcess(WinNT.HANDLE paramHANDLE, IntByReference paramIntByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m96, new Object[] { paramHANDLE, paramIntByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetLocalTime(WinBase.SYSTEMTIME paramSYSTEMTIME) {
    try {
      return ((Boolean)this.h.invoke(this, m126, new Object[] { paramSYSTEMTIME })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final Pointer GetEnvironmentStrings() {
    try {
      return (Pointer)this.h.invoke(this, m77, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int SetThreadExecutionState(int paramInt) {
    try {
      return ((Integer)this.h.invoke(this, m51, new Object[] { Integer.valueOf(paramInt) })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean WaitNamedPipe(String paramString, int paramInt) {
    try {
      return ((Boolean)this.h.invoke(this, m140, new Object[] { paramString, Integer.valueOf(paramInt) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int GetShortPathName(String paramString, char[] paramArrayOfchar, int paramInt) {
    try {
      return ((Integer)this.h.invoke(this, m115, new Object[] { paramString, paramArrayOfchar, Integer.valueOf(paramInt) })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HRESULT GetApplicationRestartSettings(WinNT.HANDLE paramHANDLE, char[] paramArrayOfchar, IntByReference paramIntByReference1, IntByReference paramIntByReference2) {
    try {
      return (WinNT.HRESULT)this.h.invoke(this, m76, new Object[] { paramHANDLE, paramArrayOfchar, paramIntByReference1, paramIntByReference2 });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int GetDriveType(String paramString) {
    try {
      return ((Integer)this.h.invoke(this, m26, new Object[] { paramString })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int GetProcessId(WinNT.HANDLE paramHANDLE) {
    try {
      return ((Integer)this.h.invoke(this, m124, new Object[] { paramHANDLE })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int GetTickCount() {
    try {
      return ((Integer)this.h.invoke(this, m112, null)).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean WriteConsole(WinNT.HANDLE paramHANDLE, String paramString, int paramInt, IntByReference paramIntByReference, WinDef.LPVOID paramLPVOID) {
    try {
      return ((Boolean)this.h.invoke(this, m180, new Object[] { paramHANDLE, paramString, Integer.valueOf(paramInt), paramIntByReference, paramLPVOID })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetComputerNameEx(int paramInt, char[] paramArrayOfchar, IntByReference paramIntByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m103, new Object[] { Integer.valueOf(paramInt), paramArrayOfchar, paramIntByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int SizeofResource(WinDef.HMODULE paramHMODULE, WinNT.HANDLE paramHANDLE) {
    try {
      return ((Integer)this.h.invoke(this, m177, new Object[] { paramHMODULE, paramHANDLE })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetVolumeLabel(String paramString1, String paramString2) {
    try {
      return ((Boolean)this.h.invoke(this, m149, new Object[] { paramString1, paramString2 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE OpenEvent(int paramInt, boolean paramBoolean, String paramString) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m121, new Object[] { Integer.valueOf(paramInt), Boolean.valueOf(paramBoolean), paramString });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int GetConsoleCP() {
    try {
      return ((Integer)this.h.invoke(this, m186, null)).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean FindVolumeMountPointClose(WinNT.HANDLE paramHANDLE) {
    try {
      return ((Boolean)this.h.invoke(this, m49, new Object[] { paramHANDLE })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final Pointer LockResource(WinNT.HANDLE paramHANDLE) {
    try {
      return (Pointer)this.h.invoke(this, m106, new Object[] { paramHANDLE });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetFileInformationByHandleEx(WinNT.HANDLE paramHANDLE, int paramInt, Pointer paramPointer, WinDef.DWORD paramDWORD) {
    try {
      return ((Boolean)this.h.invoke(this, m92, new Object[] { paramHANDLE, Integer.valueOf(paramInt), paramPointer, paramDWORD })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int WaitForSingleObject(WinNT.HANDLE paramHANDLE, int paramInt) {
    try {
      return ((Integer)this.h.invoke(this, m95, new Object[] { paramHANDLE, Integer.valueOf(paramInt) })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE GetCurrentThread() {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m16, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean CreatePipe(WinNT.HANDLEByReference paramHANDLEByReference1, WinNT.HANDLEByReference paramHANDLEByReference2, WinBase.SECURITY_ATTRIBUTES paramSECURITY_ATTRIBUTES, int paramInt) {
    try {
      return ((Boolean)this.h.invoke(this, m134, new Object[] { paramHANDLEByReference1, paramHANDLEByReference2, paramSECURITY_ATTRIBUTES, Integer.valueOf(paramInt) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean FreeConsole() {
    try {
      return ((Boolean)this.h.invoke(this, m184, null)).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean FreeLibrary(WinDef.HMODULE paramHMODULE) {
    try {
      return ((Boolean)this.h.invoke(this, m178, new Object[] { paramHMODULE })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SystemTimeToFileTime(WinBase.SYSTEMTIME paramSYSTEMTIME, WinBase.FILETIME paramFILETIME) {
    try {
      return ((Boolean)this.h.invoke(this, m46, new Object[] { paramSYSTEMTIME, paramFILETIME })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE CreateMutex(WinBase.SECURITY_ATTRIBUTES paramSECURITY_ATTRIBUTES, boolean paramBoolean, String paramString) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m35, new Object[] { paramSECURITY_ATTRIBUTES, Boolean.valueOf(paramBoolean), paramString });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetNamedPipeClientProcessId(WinNT.HANDLE paramHANDLE, WinDef.ULONGByReference paramULONGByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m67, new Object[] { paramHANDLE, paramULONGByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final void GetNativeSystemInfo(WinBase.SYSTEM_INFO paramSYSTEM_INFO) {
    try {
      this.h.invoke(this, m88, new Object[] { paramSYSTEM_INFO });
      return;
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetProcessTimes(WinNT.HANDLE paramHANDLE, WinBase.FILETIME paramFILETIME1, WinBase.FILETIME paramFILETIME2, WinBase.FILETIME paramFILETIME3, WinBase.FILETIME paramFILETIME4) {
    try {
      return ((Boolean)this.h.invoke(this, m33, new Object[] { paramHANDLE, paramFILETIME1, paramFILETIME2, paramFILETIME3, paramFILETIME4 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean VerifyVersionInfoW(WinNT.OSVERSIONINFOEX paramOSVERSIONINFOEX, int paramInt, long paramLong) {
    try {
      return ((Boolean)this.h.invoke(this, m105, new Object[] { paramOSVERSIONINFOEX, Integer.valueOf(paramInt), Long.valueOf(paramLong) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean QueryFullProcessImageName(WinNT.HANDLE paramHANDLE, int paramInt, char[] paramArrayOfchar, IntByReference paramIntByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m104, new Object[] { paramHANDLE, Integer.valueOf(paramInt), paramArrayOfchar, paramIntByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GenerateConsoleCtrlEvent(int paramInt1, int paramInt2) {
    try {
      return ((Boolean)this.h.invoke(this, m198, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetVolumePathNamesForVolumeName(String paramString, char[] paramArrayOfchar, int paramInt, IntByReference paramIntByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m69, new Object[] { paramString, paramArrayOfchar, Integer.valueOf(paramInt), paramIntByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean DuplicateHandle(WinNT.HANDLE paramHANDLE1, WinNT.HANDLE paramHANDLE2, WinNT.HANDLE paramHANDLE3, WinNT.HANDLEByReference paramHANDLEByReference, int paramInt1, boolean paramBoolean, int paramInt2) {
    try {
      return ((Boolean)this.h.invoke(this, m114, new Object[] { paramHANDLE1, paramHANDLE2, paramHANDLE3, paramHANDLEByReference, Integer.valueOf(paramInt1), Boolean.valueOf(paramBoolean), Integer.valueOf(paramInt2) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int SetErrorMode(int paramInt) {
    try {
      return ((Integer)this.h.invoke(this, m36, new Object[] { Integer.valueOf(paramInt) })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean Thread32Next(WinNT.HANDLE paramHANDLE, Tlhelp32.THREADENTRY32 paramTHREADENTRY32) {
    try {
      return ((Boolean)this.h.invoke(this, m137, new Object[] { paramHANDLE, paramTHREADENTRY32 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final long VerSetConditionMask(long paramLong, int paramInt, byte paramByte) {
    try {
      return ((Long)this.h.invoke(this, m87, new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt), Byte.valueOf(paramByte) })).longValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SystemTimeToTzSpecificLocalTime(WinBase.TIME_ZONE_INFORMATION paramTIME_ZONE_INFORMATION, WinBase.SYSTEMTIME paramSYSTEMTIME1, WinBase.SYSTEMTIME paramSYSTEMTIME2) {
    try {
      return ((Boolean)this.h.invoke(this, m78, new Object[] { paramTIME_ZONE_INFORMATION, paramSYSTEMTIME1, paramSYSTEMTIME2 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetConsoleDisplayMode(IntByReference paramIntByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m202, new Object[] { paramIntByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean ReadDirectoryChangesW(WinNT.HANDLE paramHANDLE, WinNT.FILE_NOTIFY_INFORMATION paramFILE_NOTIFY_INFORMATION, int paramInt1, boolean paramBoolean, int paramInt2, IntByReference paramIntByReference, WinBase.OVERLAPPED paramOVERLAPPED, WinNT.OVERLAPPED_COMPLETION_ROUTINE paramOVERLAPPED_COMPLETION_ROUTINE) {
    try {
      return ((Boolean)this.h.invoke(this, m22, new Object[] { paramHANDLE, paramFILE_NOTIFY_INFORMATION, Integer.valueOf(paramInt1), Boolean.valueOf(paramBoolean), Integer.valueOf(paramInt2), paramIntByReference, paramOVERLAPPED, paramOVERLAPPED_COMPLETION_ROUTINE })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetNamedPipeInfo(WinNT.HANDLE paramHANDLE, IntByReference paramIntByReference1, IntByReference paramIntByReference2, IntByReference paramIntByReference3, IntByReference paramIntByReference4) {
    try {
      return ((Boolean)this.h.invoke(this, m133, new Object[] { paramHANDLE, paramIntByReference1, paramIntByReference2, paramIntByReference3, paramIntByReference4 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetCommState(WinNT.HANDLE paramHANDLE, WinBase.DCB paramDCB) {
    try {
      return ((Boolean)this.h.invoke(this, m174, new Object[] { paramHANDLE, paramDCB })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE CreateToolhelp32Snapshot(WinDef.DWORD paramDWORD1, WinDef.DWORD paramDWORD2) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m52, new Object[] { paramDWORD1, paramDWORD2 });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetStdHandle(int paramInt, WinNT.HANDLE paramHANDLE) {
    try {
      return ((Boolean)this.h.invoke(this, m192, new Object[] { Integer.valueOf(paramInt), paramHANDLE })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE CreateEvent(WinBase.SECURITY_ATTRIBUTES paramSECURITY_ATTRIBUTES, boolean paramBoolean1, boolean paramBoolean2, String paramString) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m119, new Object[] { paramSECURITY_ATTRIBUTES, Boolean.valueOf(paramBoolean1), Boolean.valueOf(paramBoolean2), paramString });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean MoveFileEx(String paramString1, String paramString2, WinDef.DWORD paramDWORD) {
    try {
      return ((Boolean)this.h.invoke(this, m8, new Object[] { paramString1, paramString2, paramDWORD })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinDef.DWORD GetLogicalDriveStrings(WinDef.DWORD paramDWORD, char[] paramArrayOfchar) {
    try {
      return (WinDef.DWORD)this.h.invoke(this, m94, new Object[] { paramDWORD, paramArrayOfchar });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetProcessAffinityMask(WinNT.HANDLE paramHANDLE, BaseTSD.ULONG_PTR paramULONG_PTR) {
    try {
      return ((Boolean)this.h.invoke(this, m101, new Object[] { paramHANDLE, paramULONG_PTR })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final Pointer VirtualAllocEx(WinNT.HANDLE paramHANDLE, Pointer paramPointer, BaseTSD.SIZE_T paramSIZE_T, int paramInt1, int paramInt2) {
    try {
      return (Pointer)this.h.invoke(this, m32, new Object[] { paramHANDLE, paramPointer, paramSIZE_T, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinDef.LCID GetSystemDefaultLCID() {
    try {
      return (WinDef.LCID)this.h.invoke(this, m54, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinDef.HMODULE GetModuleHandle(String paramString) {
    try {
      return (WinDef.HMODULE)this.h.invoke(this, m108, new Object[] { paramString });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE FindFirstFile(String paramString, Pointer paramPointer) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m5, new Object[] { paramString, paramPointer });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int GetPrivateProfileInt(String paramString1, String paramString2, int paramInt, String paramString3) {
    try {
      return ((Integer)this.h.invoke(this, m60, new Object[] { paramString1, paramString2, Integer.valueOf(paramInt), paramString3 })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final Pointer GlobalFree(Pointer paramPointer) {
    try {
      return (Pointer)this.h.invoke(this, m107, new Object[] { paramPointer });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean FindNextVolume(WinNT.HANDLE paramHANDLE, char[] paramArrayOfchar, int paramInt) {
    try {
      return ((Boolean)this.h.invoke(this, m156, new Object[] { paramHANDLE, paramArrayOfchar, Integer.valueOf(paramInt) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean FindNextFile(WinNT.HANDLE paramHANDLE, Pointer paramPointer) {
    try {
      return ((Boolean)this.h.invoke(this, m7, new Object[] { paramHANDLE, paramPointer })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean Process32Next(WinNT.HANDLE paramHANDLE, Tlhelp32.PROCESSENTRY32 paramPROCESSENTRY32) {
    try {
      return ((Boolean)this.h.invoke(this, m171, new Object[] { paramHANDLE, paramPROCESSENTRY32 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE CreateRemoteThread(WinNT.HANDLE paramHANDLE, WinBase.SECURITY_ATTRIBUTES paramSECURITY_ATTRIBUTES, int paramInt1, Pointer paramPointer1, Pointer paramPointer2, int paramInt2, WinDef.DWORDByReference paramDWORDByReference) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m75, new Object[] { paramHANDLE, paramSECURITY_ATTRIBUTES, Integer.valueOf(paramInt1), paramPointer1, paramPointer2, Integer.valueOf(paramInt2), paramDWORDByReference });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetFileInformationByHandle(WinNT.HANDLE paramHANDLE, int paramInt, Pointer paramPointer, WinDef.DWORD paramDWORD) {
    try {
      return ((Boolean)this.h.invoke(this, m93, new Object[] { paramHANDLE, Integer.valueOf(paramInt), paramPointer, paramDWORD })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final void GetLocalTime(WinBase.SYSTEMTIME paramSYSTEMTIME) {
    try {
      this.h.invoke(this, m110, new Object[] { paramSYSTEMTIME });
      return;
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final void GetSystemInfo(WinBase.SYSTEM_INFO paramSYSTEM_INFO) {
    try {
      this.h.invoke(this, m167, new Object[] { paramSYSTEM_INFO });
      return;
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinDef.HWND GetConsoleWindow() {
    try {
      return (WinDef.HWND)this.h.invoke(this, m191, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final void ExitProcess(int paramInt) {
    try {
      this.h.invoke(this, m28, new Object[] { Integer.valueOf(paramInt) });
      return;
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinDef.DWORD GetPrivateProfileSection(String paramString1, char[] paramArrayOfchar, WinDef.DWORD paramDWORD, String paramString2) {
    try {
      return (WinDef.DWORD)this.h.invoke(this, m65, new Object[] { paramString1, paramArrayOfchar, paramDWORD, paramString2 });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetConsoleMode(WinNT.HANDLE paramHANDLE, IntByReference paramIntByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m193, new Object[] { paramHANDLE, paramIntByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetEvent(WinNT.HANDLE paramHANDLE) {
    try {
      return ((Boolean)this.h.invoke(this, m123, new Object[] { paramHANDLE })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean DeviceIoControl(WinNT.HANDLE paramHANDLE, int paramInt1, Pointer paramPointer1, int paramInt2, Pointer paramPointer2, int paramInt3, IntByReference paramIntByReference, Pointer paramPointer3) {
    try {
      return ((Boolean)this.h.invoke(this, m161, new Object[] { paramHANDLE, Integer.valueOf(paramInt1), paramPointer1, Integer.valueOf(paramInt2), paramPointer2, Integer.valueOf(paramInt3), paramIntByReference, paramPointer3 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean IsWow64Process(WinNT.HANDLE paramHANDLE, IntByReference paramIntByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m164, new Object[] { paramHANDLE, paramIntByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE CreateNamedPipe(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, WinBase.SECURITY_ATTRIBUTES paramSECURITY_ATTRIBUTES) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m144, new Object[] { paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), Integer.valueOf(paramInt5), Integer.valueOf(paramInt6), paramSECURITY_ATTRIBUTES });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final String toString() {
    try {
      return (String)this.h.invoke(this, m2, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean WritePrivateProfileSection(String paramString1, String paramString2, String paramString3) {
    try {
      return ((Boolean)this.h.invoke(this, m83, new Object[] { paramString1, paramString2, paramString3 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetEnvironmentVariable(String paramString1, String paramString2) {
    try {
      return ((Boolean)this.h.invoke(this, m64, new Object[] { paramString1, paramString2 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetNumberOfConsoleInputEvents(WinNT.HANDLE paramHANDLE, IntByReference paramIntByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m197, new Object[] { paramHANDLE, paramIntByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int GetProcessVersion(int paramInt) {
    try {
      return ((Integer)this.h.invoke(this, m100, new Object[] { Integer.valueOf(paramInt) })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetExitCodeThread(WinNT.HANDLE paramHANDLE, IntByReference paramIntByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m70, new Object[] { paramHANDLE, paramIntByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final Pointer GetProcAddress(WinDef.HMODULE paramHMODULE, int paramInt) throws LastErrorException {
    try {
      return (Pointer)this.h.invoke(this, m30, new Object[] { paramHMODULE, Integer.valueOf(paramInt) });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean WriteFile(WinNT.HANDLE paramHANDLE, byte[] paramArrayOfbyte, int paramInt, IntByReference paramIntByReference, WinBase.OVERLAPPED paramOVERLAPPED) {
    try {
      return ((Boolean)this.h.invoke(this, m117, new Object[] { paramHANDLE, paramArrayOfbyte, Integer.valueOf(paramInt), paramIntByReference, paramOVERLAPPED })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetHandleInformation(WinNT.HANDLE paramHANDLE, int paramInt1, int paramInt2) {
    try {
      return ((Boolean)this.h.invoke(this, m57, new Object[] { paramHANDLE, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean Module32FirstW(WinNT.HANDLE paramHANDLE, Tlhelp32.MODULEENTRY32W paramMODULEENTRY32W) {
    try {
      return ((Boolean)this.h.invoke(this, m179, new Object[] { paramHANDLE, paramMODULEENTRY32W })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean ReadFile(WinNT.HANDLE paramHANDLE, byte[] paramArrayOfbyte, int paramInt, IntByReference paramIntByReference, WinBase.OVERLAPPED paramOVERLAPPED) {
    try {
      return ((Boolean)this.h.invoke(this, m127, new Object[] { paramHANDLE, paramArrayOfbyte, Integer.valueOf(paramInt), paramIntByReference, paramOVERLAPPED })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean WriteProcessMemory(WinNT.HANDLE paramHANDLE, Pointer paramPointer1, Pointer paramPointer2, int paramInt, IntByReference paramIntByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m58, new Object[] { paramHANDLE, paramPointer1, paramPointer2, Integer.valueOf(paramInt), paramIntByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE LoadResource(WinDef.HMODULE paramHMODULE, WinDef.HRSRC paramHRSRC) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m153, new Object[] { paramHMODULE, paramHRSRC });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE CreateRemoteThread(WinNT.HANDLE paramHANDLE, WinBase.SECURITY_ATTRIBUTES paramSECURITY_ATTRIBUTES, int paramInt, WinBase.FOREIGN_THREAD_START_ROUTINE paramFOREIGN_THREAD_START_ROUTINE, Pointer paramPointer1, WinDef.DWORD paramDWORD, Pointer paramPointer2) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m74, new Object[] { paramHANDLE, paramSECURITY_ATTRIBUTES, Integer.valueOf(paramInt), paramFOREIGN_THREAD_START_ROUTINE, paramPointer1, paramDWORD, paramPointer2 });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetCommState(WinNT.HANDLE paramHANDLE, WinBase.DCB paramDCB) {
    try {
      return ((Boolean)this.h.invoke(this, m142, new Object[] { paramHANDLE, paramDCB })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetVolumeMountPoint(String paramString1, String paramString2) {
    try {
      return ((Boolean)this.h.invoke(this, m62, new Object[] { paramString1, paramString2 })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean PulseEvent(WinNT.HANDLE paramHANDLE) {
    try {
      return ((Boolean)this.h.invoke(this, m166, new Object[] { paramHANDLE })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetComputerName(char[] paramArrayOfchar, IntByReference paramIntByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m132, new Object[] { paramArrayOfchar, paramIntByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetLogicalProcessorInformation(Pointer paramPointer, WinDef.DWORDByReference paramDWORDByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m89, new Object[] { paramPointer, paramDWORDByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean UnmapViewOfFile(Pointer paramPointer) {
    try {
      return ((Boolean)this.h.invoke(this, m151, new Object[] { paramPointer })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetNamedPipeServerProcessId(WinNT.HANDLE paramHANDLE, WinDef.ULONGByReference paramULONGByReference) {
    try {
      return ((Boolean)this.h.invoke(this, m41, new Object[] { paramHANDLE, paramULONGByReference })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE OpenFileMapping(int paramInt, boolean paramBoolean, String paramString) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m169, new Object[] { Integer.valueOf(paramInt), Boolean.valueOf(paramBoolean), paramString });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean SetCommTimeouts(WinNT.HANDLE paramHANDLE, WinBase.COMMTIMEOUTS paramCOMMTIMEOUTS) {
    try {
      return ((Boolean)this.h.invoke(this, m143, new Object[] { paramHANDLE, paramCOMMTIMEOUTS })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean FindVolumeClose(WinNT.HANDLE paramHANDLE) {
    try {
      return ((Boolean)this.h.invoke(this, m139, new Object[] { paramHANDLE })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE FindFirstVolume(char[] paramArrayOfchar, int paramInt) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m138, new Object[] { paramArrayOfchar, Integer.valueOf(paramInt) });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetProcessIoCounters(WinNT.HANDLE paramHANDLE, WinNT.IO_COUNTERS paramIO_COUNTERS) {
    try {
      return ((Boolean)this.h.invoke(this, m79, new Object[] { paramHANDLE, paramIO_COUNTERS })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final Pointer LocalAlloc(int paramInt1, int paramInt2) {
    try {
      return (Pointer)this.h.invoke(this, m116, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final WinNT.HANDLE CreateFile(String paramString, int paramInt1, int paramInt2, WinBase.SECURITY_ATTRIBUTES paramSECURITY_ATTRIBUTES, int paramInt3, int paramInt4, WinNT.HANDLE paramHANDLE) {
    try {
      return (WinNT.HANDLE)this.h.invoke(this, m27, new Object[] { paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramSECURITY_ATTRIBUTES, Integer.valueOf(paramInt3), Integer.valueOf(paramInt4), paramHANDLE });
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean FindNextVolumeMountPoint(WinNT.HANDLE paramHANDLE, char[] paramArrayOfchar, int paramInt) {
    try {
      return ((Boolean)this.h.invoke(this, m71, new Object[] { paramHANDLE, paramArrayOfchar, Integer.valueOf(paramInt) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int QueryDosDevice(String paramString, char[] paramArrayOfchar, int paramInt) {
    try {
      return ((Integer)this.h.invoke(this, m173, new Object[] { paramString, paramArrayOfchar, Integer.valueOf(paramInt) })).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean FlushConsoleInputBuffer(WinNT.HANDLE paramHANDLE) {
    try {
      return ((Boolean)this.h.invoke(this, m195, new Object[] { paramHANDLE })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GetNamedPipeHandleState(WinNT.HANDLE paramHANDLE, IntByReference paramIntByReference1, IntByReference paramIntByReference2, IntByReference paramIntByReference3, IntByReference paramIntByReference4, char[] paramArrayOfchar, int paramInt) {
    try {
      return ((Boolean)this.h.invoke(this, m39, new Object[] { paramHANDLE, paramIntByReference1, paramIntByReference2, paramIntByReference3, paramIntByReference4, paramArrayOfchar, Integer.valueOf(paramInt) })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean DeleteVolumeMountPoint(String paramString) {
    try {
      return ((Boolean)this.h.invoke(this, m68, new Object[] { paramString })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean PostQueuedCompletionStatus(WinNT.HANDLE paramHANDLE, int paramInt, Pointer paramPointer, WinBase.OVERLAPPED paramOVERLAPPED) {
    try {
      return ((Boolean)this.h.invoke(this, m21, new Object[] { paramHANDLE, Integer.valueOf(paramInt), paramPointer, paramOVERLAPPED })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean GlobalMemoryStatusEx(WinBase.MEMORYSTATUSEX paramMEMORYSTATUSEX) {
    try {
      return ((Boolean)this.h.invoke(this, m91, new Object[] { paramMEMORYSTATUSEX })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  static {
    try {
      m85 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FreeEnvironmentStrings", new Class[] { Class.forName("com.sun.jna.Pointer") });
      m44 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetPrivateProfileSectionNames", new Class[] { Class.forName("[C"), Class.forName("com.sun.jna.platform.win32.WinDef$DWORD"), Class.forName("java.lang.String") });
      m80 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetNamedPipeHandleState", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.ptr.IntByReference") });
      m31 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("OpenMutex", new Class[] { int.class, boolean.class, Class.forName("java.lang.String") });
      m13 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetDiskFreeSpaceEx", new Class[] { Class.forName("java.lang.String"), Class.forName("com.sun.jna.platform.win32.WinNT$LARGE_INTEGER"), Class.forName("com.sun.jna.platform.win32.WinNT$LARGE_INTEGER"), Class.forName("com.sun.jna.platform.win32.WinNT$LARGE_INTEGER") });
      m6 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FindClose", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m146 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("LoadLibraryEx", new Class[] { Class.forName("java.lang.String"), Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), int.class });
      m90 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetLogicalProcessorInformationEx", new Class[] { int.class, Class.forName("com.sun.jna.Pointer"), Class.forName("com.sun.jna.platform.win32.WinDef$DWORDByReference") });
      m14 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetVolumePathName", new Class[] { Class.forName("java.lang.String"), Class.forName("[C"), int.class });
      m147 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetVersion", new Class[0]);
      m183 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("AllocConsole", new Class[0]);
      m42 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("TransactNamedPipe", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("[B"), int.class, Class.forName("[B"), int.class, Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.platform.win32.WinBase$OVERLAPPED") });
      m84 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("UnregisterApplicationRestart", new Class[0]);
      m125 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("ResetEvent", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m129 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("CopyFile", new Class[] { Class.forName("java.lang.String"), Class.forName("java.lang.String"), boolean.class });
      m187 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetConsoleMode", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), int.class });
      m56 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetNamedPipeServerSessionId", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinDef$ULONGByReference") });
      m50 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("WritePrivateProfileString", new Class[] { Class.forName("java.lang.String"), Class.forName("java.lang.String"), Class.forName("java.lang.String"), Class.forName("java.lang.String") });
      m163 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("CallNamedPipe", new Class[] { Class.forName("java.lang.String"), Class.forName("[B"), int.class, Class.forName("[B"), int.class, Class.forName("com.sun.jna.ptr.IntByReference"), int.class });
      m12 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetVolumeInformation", new Class[] { Class.forName("java.lang.String"), Class.forName("[C"), int.class, Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("[C"), int.class });
      m113 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetLastError", new Class[0]);
      m194 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetConsoleOriginalTitle", new Class[] { Class.forName("[C"), int.class });
      m122 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetLastError", new Class[] { int.class });
      m9 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetFileAttributes", new Class[] { Class.forName("java.lang.String") });
      m59 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetEnvironmentVariable", new Class[] { Class.forName("java.lang.String"), Class.forName("[C"), int.class });
      m53 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetPrivateProfileString", new Class[] { Class.forName("java.lang.String"), Class.forName("java.lang.String"), Class.forName("java.lang.String"), Class.forName("[C"), Class.forName("com.sun.jna.platform.win32.WinDef$DWORD"), Class.forName("java.lang.String") });
      m48 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FileTimeToSystemTime", new Class[] { Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME"), Class.forName("com.sun.jna.platform.win32.WinBase$SYSTEMTIME") });
      m196 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetConsoleScreenBufferInfo", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.Wincon$CONSOLE_SCREEN_BUFFER_INFO") });
      m4 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("adolf61983", new Class[0]);
      m0 = Class.forName("java.lang.Object").getMethod("hashCode", new Class[0]);
      m19 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("CreateIoCompletionPort", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.Pointer"), int.class });
      m190 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetConsoleTitle", new Class[] { Class.forName("java.lang.String") });
      m43 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetUserDefaultLCID", new Class[0]);
      m98 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("CreateFileMapping", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinBase$SECURITY_ATTRIBUTES"), int.class, int.class, int.class, Class.forName("java.lang.String") });
      m157 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetTempPath", new Class[] { Class.forName("com.sun.jna.platform.win32.WinDef$DWORD"), Class.forName("[C") });
      m150 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FindResource", new Class[] { Class.forName("com.sun.jna.platform.win32.WinDef$HMODULE"), Class.forName("com.sun.jna.Pointer"), Class.forName("com.sun.jna.Pointer") });
      m128 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetTickCount64", new Class[0]);
      m182 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("AttachConsole", new Class[] { int.class });
      m38 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("ReadProcessMemory", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.Pointer"), Class.forName("com.sun.jna.Pointer"), int.class, Class.forName("com.sun.jna.ptr.IntByReference") });
      m152 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetFileType", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m18 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("LocalFree", new Class[] { Class.forName("com.sun.jna.Pointer") });
      m25 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("CreateDirectory", new Class[] { Class.forName("java.lang.String"), Class.forName("com.sun.jna.platform.win32.WinBase$SECURITY_ATTRIBUTES") });
      m24 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("CloseHandle", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m3 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("IsDebuggerPresent", new Class[0]);
      m82 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("EnumResourceNames", new Class[] { Class.forName("com.sun.jna.platform.win32.WinDef$HMODULE"), Class.forName("com.sun.jna.Pointer"), Class.forName("com.sun.jna.platform.win32.WinBase$EnumResNameProc"), Class.forName("com.sun.jna.Pointer") });
      m172 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("Thread32First", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.Tlhelp32$THREADENTRY32") });
      m199 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetConsoleOutputCP", new Class[0]);
      m37 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("VirtualFreeEx", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.Pointer"), Class.forName("com.sun.jna.platform.win32.BaseTSD$SIZE_T"), int.class });
      m109 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetSystemTime", new Class[] { Class.forName("com.sun.jna.platform.win32.WinBase$SYSTEMTIME") });
      m1 = Class.forName("java.lang.Object").getMethod("equals", new Class[] { Class.forName("java.lang.Object") });
      m165 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("Process32First", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.Tlhelp32$PROCESSENTRY32") });
      m66 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FindFirstVolumeMountPoint", new Class[] { Class.forName("java.lang.String"), Class.forName("[C"), int.class });
      m34 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("ReleaseMutex", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m181 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetStdHandle", new Class[] { int.class });
      m11 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetFileTime", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME"), Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME"), Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME") });
      m47 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetNamedPipeClientSessionId", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinDef$ULONGByReference") });
      m45 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("ProcessIdToSessionId", new Class[] { int.class, Class.forName("com.sun.jna.ptr.IntByReference") });
      m176 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("CreateProcessW", new Class[] { Class.forName("java.lang.String"), Class.forName("[C"), Class.forName("com.sun.jna.platform.win32.WinBase$SECURITY_ATTRIBUTES"), Class.forName("com.sun.jna.platform.win32.WinBase$SECURITY_ATTRIBUTES"), boolean.class, Class.forName("com.sun.jna.platform.win32.WinDef$DWORD"), Class.forName("com.sun.jna.Pointer"), Class.forName("java.lang.String"), Class.forName("com.sun.jna.platform.win32.WinBase$STARTUPINFO"), Class.forName("com.sun.jna.platform.win32.WinBase$PROCESS_INFORMATION") });
      m15 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetCurrentProcess", new Class[0]);
      m17 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FormatMessage", new Class[] { int.class, Class.forName("com.sun.jna.Pointer"), int.class, int.class, Class.forName("com.sun.jna.ptr.PointerByReference"), int.class, Class.forName("com.sun.jna.Pointer") });
      m158 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("VirtualQueryEx", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.Pointer"), Class.forName("com.sun.jna.platform.win32.WinNT$MEMORY_BASIC_INFORMATION"), Class.forName("com.sun.jna.platform.win32.BaseTSD$SIZE_T") });
      m160 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetVersionEx", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$OSVERSIONINFOEX") });
      m168 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("OpenThread", new Class[] { int.class, boolean.class, int.class });
      m10 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetFileAttributes", new Class[] { Class.forName("java.lang.String"), Class.forName("com.sun.jna.platform.win32.WinDef$DWORD") });
      m40 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetVolumeNameForVolumeMountPoint", new Class[] { Class.forName("java.lang.String"), Class.forName("[C"), int.class });
      m200 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetConsoleOutputCP", new Class[] { int.class });
      m159 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetVersionEx", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$OSVERSIONINFO") });
      m23 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("DeleteFile", new Class[] { Class.forName("java.lang.String") });
      m135 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetFileTime", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME"), Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME"), Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME") });
      m155 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("MapViewOfFile", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), int.class, int.class, int.class, int.class });
      m61 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("ExpandEnvironmentStrings", new Class[] { Class.forName("java.lang.String"), Class.forName("com.sun.jna.Pointer"), int.class });
      m99 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetProcessAffinityMask", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.BaseTSD$ULONG_PTRByReference"), Class.forName("com.sun.jna.platform.win32.BaseTSD$ULONG_PTRByReference") });
      m29 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("Module32NextW", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.Tlhelp32$MODULEENTRY32W") });
      m81 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("DisconnectNamedPipe", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m131 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("TerminateProcess", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), int.class });
      m97 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetCurrentProcessId", new Class[0]);
      m201 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetNumberOfConsoleMouseButtons", new Class[] { Class.forName("com.sun.jna.ptr.IntByReference") });
      m154 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("ConnectNamedPipe", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinBase$OVERLAPPED") });
      m136 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("OpenProcess", new Class[] { int.class, boolean.class, int.class });
      m120 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("MoveFile", new Class[] { Class.forName("java.lang.String"), Class.forName("java.lang.String") });
      m141 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("DefineDosDevice", new Class[] { int.class, Class.forName("java.lang.String"), Class.forName("java.lang.String") });
      m145 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FindFirstFileEx", new Class[] { Class.forName("java.lang.String"), int.class, Class.forName("com.sun.jna.Pointer"), int.class, Class.forName("com.sun.jna.Pointer"), Class.forName("com.sun.jna.platform.win32.WinDef$DWORD") });
      m55 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("EnumResourceTypes", new Class[] { Class.forName("com.sun.jna.platform.win32.WinDef$HMODULE"), Class.forName("com.sun.jna.platform.win32.WinBase$EnumResTypeProc"), Class.forName("com.sun.jna.Pointer") });
      m175 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetCommTimeouts", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinBase$COMMTIMEOUTS") });
      m130 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetSystemTime", new Class[] { Class.forName("com.sun.jna.platform.win32.WinBase$SYSTEMTIME") });
      m72 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FileTimeToLocalFileTime", new Class[] { Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME"), Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME") });
      m148 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("CreateProcess", new Class[] { Class.forName("java.lang.String"), Class.forName("java.lang.String"), Class.forName("com.sun.jna.platform.win32.WinBase$SECURITY_ATTRIBUTES"), Class.forName("com.sun.jna.platform.win32.WinBase$SECURITY_ATTRIBUTES"), boolean.class, Class.forName("com.sun.jna.platform.win32.WinDef$DWORD"), Class.forName("com.sun.jna.Pointer"), Class.forName("java.lang.String"), Class.forName("com.sun.jna.platform.win32.WinBase$STARTUPINFO"), Class.forName("com.sun.jna.platform.win32.WinBase$PROCESS_INFORMATION") });
      m188 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetConsoleTitle", new Class[] { Class.forName("[C"), int.class });
      m102 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("WaitForMultipleObjects", new Class[] { int.class, Class.forName("[Lcom.sun.jna.platform.win32.WinNT$HANDLE;"), boolean.class, int.class });
      m63 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetNamedPipeClientComputerName", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("[C"), int.class });
      m86 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetCurrentThreadId", new Class[0]);
      m170 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetDiskFreeSpace", new Class[] { Class.forName("java.lang.String"), Class.forName("com.sun.jna.platform.win32.WinDef$DWORDByReference"), Class.forName("com.sun.jna.platform.win32.WinDef$DWORDByReference"), Class.forName("com.sun.jna.platform.win32.WinDef$DWORDByReference"), Class.forName("com.sun.jna.platform.win32.WinDef$DWORDByReference") });
      m118 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FlushFileBuffers", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m73 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("RegisterApplicationRestart", new Class[] { Class.forName("[C"), int.class });
      m189 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetConsoleCP", new Class[] { int.class });
      m111 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetSystemTimes", new Class[] { Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME"), Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME"), Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME") });
      m185 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("ReadConsoleInput", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("[Lcom.sun.jna.platform.win32.Wincon$INPUT_RECORD;"), int.class, Class.forName("com.sun.jna.ptr.IntByReference") });
      m20 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetQueuedCompletionStatus", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.platform.win32.BaseTSD$ULONG_PTRByReference"), Class.forName("com.sun.jna.ptr.PointerByReference"), int.class });
      m162 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("PeekNamedPipe", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("[B"), int.class, Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.ptr.IntByReference") });
      m96 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetExitCodeProcess", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.ptr.IntByReference") });
      m126 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetLocalTime", new Class[] { Class.forName("com.sun.jna.platform.win32.WinBase$SYSTEMTIME") });
      m77 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetEnvironmentStrings", new Class[0]);
      m51 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetThreadExecutionState", new Class[] { int.class });
      m140 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("WaitNamedPipe", new Class[] { Class.forName("java.lang.String"), int.class });
      m115 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetShortPathName", new Class[] { Class.forName("java.lang.String"), Class.forName("[C"), int.class });
      m76 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetApplicationRestartSettings", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("[C"), Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.ptr.IntByReference") });
      m26 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetDriveType", new Class[] { Class.forName("java.lang.String") });
      m124 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetProcessId", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m112 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetTickCount", new Class[0]);
      m180 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("WriteConsole", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("java.lang.String"), int.class, Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.platform.win32.WinDef$LPVOID") });
      m103 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetComputerNameEx", new Class[] { int.class, Class.forName("[C"), Class.forName("com.sun.jna.ptr.IntByReference") });
      m177 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SizeofResource", new Class[] { Class.forName("com.sun.jna.platform.win32.WinDef$HMODULE"), Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m149 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetVolumeLabel", new Class[] { Class.forName("java.lang.String"), Class.forName("java.lang.String") });
      m121 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("OpenEvent", new Class[] { int.class, boolean.class, Class.forName("java.lang.String") });
      m186 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetConsoleCP", new Class[0]);
      m49 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FindVolumeMountPointClose", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m106 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("LockResource", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m92 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetFileInformationByHandleEx", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), int.class, Class.forName("com.sun.jna.Pointer"), Class.forName("com.sun.jna.platform.win32.WinDef$DWORD") });
      m95 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("WaitForSingleObject", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), int.class });
      m16 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetCurrentThread", new Class[0]);
      m134 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("CreatePipe", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLEByReference"), Class.forName("com.sun.jna.platform.win32.WinNT$HANDLEByReference"), Class.forName("com.sun.jna.platform.win32.WinBase$SECURITY_ATTRIBUTES"), int.class });
      m184 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FreeConsole", new Class[0]);
      m178 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FreeLibrary", new Class[] { Class.forName("com.sun.jna.platform.win32.WinDef$HMODULE") });
      m46 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SystemTimeToFileTime", new Class[] { Class.forName("com.sun.jna.platform.win32.WinBase$SYSTEMTIME"), Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME") });
      m35 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("CreateMutex", new Class[] { Class.forName("com.sun.jna.platform.win32.WinBase$SECURITY_ATTRIBUTES"), boolean.class, Class.forName("java.lang.String") });
      m67 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetNamedPipeClientProcessId", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinDef$ULONGByReference") });
      m88 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetNativeSystemInfo", new Class[] { Class.forName("com.sun.jna.platform.win32.WinBase$SYSTEM_INFO") });
      m33 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetProcessTimes", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME"), Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME"), Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME"), Class.forName("com.sun.jna.platform.win32.WinBase$FILETIME") });
      m105 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("VerifyVersionInfoW", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$OSVERSIONINFOEX"), int.class, long.class });
      m104 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("QueryFullProcessImageName", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), int.class, Class.forName("[C"), Class.forName("com.sun.jna.ptr.IntByReference") });
      m198 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GenerateConsoleCtrlEvent", new Class[] { int.class, int.class });
      m69 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetVolumePathNamesForVolumeName", new Class[] { Class.forName("java.lang.String"), Class.forName("[C"), int.class, Class.forName("com.sun.jna.ptr.IntByReference") });
      m114 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("DuplicateHandle", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinNT$HANDLEByReference"), int.class, boolean.class, int.class });
      m36 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetErrorMode", new Class[] { int.class });
      m137 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("Thread32Next", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.Tlhelp32$THREADENTRY32") });
      m87 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("VerSetConditionMask", new Class[] { long.class, int.class, byte.class });
      m78 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SystemTimeToTzSpecificLocalTime", new Class[] { Class.forName("com.sun.jna.platform.win32.WinBase$TIME_ZONE_INFORMATION"), Class.forName("com.sun.jna.platform.win32.WinBase$SYSTEMTIME"), Class.forName("com.sun.jna.platform.win32.WinBase$SYSTEMTIME") });
      m202 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetConsoleDisplayMode", new Class[] { Class.forName("com.sun.jna.ptr.IntByReference") });
      m22 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("ReadDirectoryChangesW", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinNT$FILE_NOTIFY_INFORMATION"), int.class, boolean.class, int.class, Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.platform.win32.WinBase$OVERLAPPED"), Class.forName("com.sun.jna.platform.win32.WinNT$OVERLAPPED_COMPLETION_ROUTINE") });
      m133 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetNamedPipeInfo", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.ptr.IntByReference") });
      m174 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetCommState", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinBase$DCB") });
      m52 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("CreateToolhelp32Snapshot", new Class[] { Class.forName("com.sun.jna.platform.win32.WinDef$DWORD"), Class.forName("com.sun.jna.platform.win32.WinDef$DWORD") });
      m192 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetStdHandle", new Class[] { int.class, Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m119 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("CreateEvent", new Class[] { Class.forName("com.sun.jna.platform.win32.WinBase$SECURITY_ATTRIBUTES"), boolean.class, boolean.class, Class.forName("java.lang.String") });
      m8 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("MoveFileEx", new Class[] { Class.forName("java.lang.String"), Class.forName("java.lang.String"), Class.forName("com.sun.jna.platform.win32.WinDef$DWORD") });
      m94 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetLogicalDriveStrings", new Class[] { Class.forName("com.sun.jna.platform.win32.WinDef$DWORD"), Class.forName("[C") });
      m101 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetProcessAffinityMask", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.BaseTSD$ULONG_PTR") });
      m32 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("VirtualAllocEx", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.Pointer"), Class.forName("com.sun.jna.platform.win32.BaseTSD$SIZE_T"), int.class, int.class });
      m54 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetSystemDefaultLCID", new Class[0]);
      m108 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetModuleHandle", new Class[] { Class.forName("java.lang.String") });
      m5 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FindFirstFile", new Class[] { Class.forName("java.lang.String"), Class.forName("com.sun.jna.Pointer") });
      m60 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetPrivateProfileInt", new Class[] { Class.forName("java.lang.String"), Class.forName("java.lang.String"), int.class, Class.forName("java.lang.String") });
      m107 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GlobalFree", new Class[] { Class.forName("com.sun.jna.Pointer") });
      m156 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FindNextVolume", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("[C"), int.class });
      m7 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FindNextFile", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.Pointer") });
      m171 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("Process32Next", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.Tlhelp32$PROCESSENTRY32") });
      m75 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("CreateRemoteThread", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinBase$SECURITY_ATTRIBUTES"), int.class, Class.forName("com.sun.jna.Pointer"), Class.forName("com.sun.jna.Pointer"), int.class, Class.forName("com.sun.jna.platform.win32.WinDef$DWORDByReference") });
      m93 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetFileInformationByHandle", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), int.class, Class.forName("com.sun.jna.Pointer"), Class.forName("com.sun.jna.platform.win32.WinDef$DWORD") });
      m110 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetLocalTime", new Class[] { Class.forName("com.sun.jna.platform.win32.WinBase$SYSTEMTIME") });
      m167 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetSystemInfo", new Class[] { Class.forName("com.sun.jna.platform.win32.WinBase$SYSTEM_INFO") });
      m191 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetConsoleWindow", new Class[0]);
      m28 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("ExitProcess", new Class[] { int.class });
      m65 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetPrivateProfileSection", new Class[] { Class.forName("java.lang.String"), Class.forName("[C"), Class.forName("com.sun.jna.platform.win32.WinDef$DWORD"), Class.forName("java.lang.String") });
      m193 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetConsoleMode", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.ptr.IntByReference") });
      m123 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetEvent", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m161 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("DeviceIoControl", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), int.class, Class.forName("com.sun.jna.Pointer"), int.class, Class.forName("com.sun.jna.Pointer"), int.class, Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.Pointer") });
      m164 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("IsWow64Process", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.ptr.IntByReference") });
      m144 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("CreateNamedPipe", new Class[] { Class.forName("java.lang.String"), int.class, int.class, int.class, int.class, int.class, int.class, Class.forName("com.sun.jna.platform.win32.WinBase$SECURITY_ATTRIBUTES") });
      m2 = Class.forName("java.lang.Object").getMethod("toString", new Class[0]);
      m83 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("WritePrivateProfileSection", new Class[] { Class.forName("java.lang.String"), Class.forName("java.lang.String"), Class.forName("java.lang.String") });
      m64 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetEnvironmentVariable", new Class[] { Class.forName("java.lang.String"), Class.forName("java.lang.String") });
      m197 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetNumberOfConsoleInputEvents", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.ptr.IntByReference") });
      m100 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetProcessVersion", new Class[] { int.class });
      m70 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetExitCodeThread", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.ptr.IntByReference") });
      m30 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetProcAddress", new Class[] { Class.forName("com.sun.jna.platform.win32.WinDef$HMODULE"), int.class });
      m117 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("WriteFile", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("[B"), int.class, Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.platform.win32.WinBase$OVERLAPPED") });
      m57 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetHandleInformation", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), int.class, int.class });
      m179 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("Module32FirstW", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.Tlhelp32$MODULEENTRY32W") });
      m127 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("ReadFile", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("[B"), int.class, Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.platform.win32.WinBase$OVERLAPPED") });
      m58 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("WriteProcessMemory", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.Pointer"), Class.forName("com.sun.jna.Pointer"), int.class, Class.forName("com.sun.jna.ptr.IntByReference") });
      m153 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("LoadResource", new Class[] { Class.forName("com.sun.jna.platform.win32.WinDef$HMODULE"), Class.forName("com.sun.jna.platform.win32.WinDef$HRSRC") });
      m74 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("CreateRemoteThread", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinBase$SECURITY_ATTRIBUTES"), int.class, Class.forName("com.sun.jna.platform.win32.WinBase$FOREIGN_THREAD_START_ROUTINE"), Class.forName("com.sun.jna.Pointer"), Class.forName("com.sun.jna.platform.win32.WinDef$DWORD"), Class.forName("com.sun.jna.Pointer") });
      m142 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetCommState", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinBase$DCB") });
      m62 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetVolumeMountPoint", new Class[] { Class.forName("java.lang.String"), Class.forName("java.lang.String") });
      m166 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("PulseEvent", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m132 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetComputerName", new Class[] { Class.forName("[C"), Class.forName("com.sun.jna.ptr.IntByReference") });
      m89 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetLogicalProcessorInformation", new Class[] { Class.forName("com.sun.jna.Pointer"), Class.forName("com.sun.jna.platform.win32.WinDef$DWORDByReference") });
      m151 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("UnmapViewOfFile", new Class[] { Class.forName("com.sun.jna.Pointer") });
      m41 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetNamedPipeServerProcessId", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinDef$ULONGByReference") });
      m169 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("OpenFileMapping", new Class[] { int.class, boolean.class, Class.forName("java.lang.String") });
      m143 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("SetCommTimeouts", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinBase$COMMTIMEOUTS") });
      m139 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FindVolumeClose", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m138 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FindFirstVolume", new Class[] { Class.forName("[C"), int.class });
      m79 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetProcessIoCounters", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.platform.win32.WinNT$IO_COUNTERS") });
      m116 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("LocalAlloc", new Class[] { int.class, int.class });
      m27 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("CreateFile", new Class[] { Class.forName("java.lang.String"), int.class, int.class, Class.forName("com.sun.jna.platform.win32.WinBase$SECURITY_ATTRIBUTES"), int.class, int.class, Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m71 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FindNextVolumeMountPoint", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("[C"), int.class });
      m173 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("QueryDosDevice", new Class[] { Class.forName("java.lang.String"), Class.forName("[C"), int.class });
      m195 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("FlushConsoleInputBuffer", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE") });
      m39 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GetNamedPipeHandleState", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("com.sun.jna.ptr.IntByReference"), Class.forName("[C"), int.class });
      m68 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("DeleteVolumeMountPoint", new Class[] { Class.forName("java.lang.String") });
      m21 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("PostQueuedCompletionStatus", new Class[] { Class.forName("com.sun.jna.platform.win32.WinNT$HANDLE"), int.class, Class.forName("com.sun.jna.Pointer"), Class.forName("com.sun.jna.platform.win32.WinBase$OVERLAPPED") });
      m91 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.Kernel32").getMethod("GlobalMemoryStatusEx", new Class[] { Class.forName("com.sun.jna.platform.win32.WinBase$MEMORYSTATUSEX") });
      return;
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError(noSuchMethodException.getMessage());
    } catch (ClassNotFoundException classNotFoundException) {
      throw new NoClassDefFoundError(classNotFoundException.getMessage());
    } 
  }
}
