package notthatuwu.xyz.mythrecode.api.utils;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.win32.W32APIOptions;

public interface Kernel32 extends Kernel32 {
  public static final Kernel32 INSTANCE = (Kernel32)Native.loadLibrary("kernel32.dll", Kernel32.class, W32APIOptions.DEFAULT_OPTIONS);
  
  boolean IsDebuggerPresent();
}
