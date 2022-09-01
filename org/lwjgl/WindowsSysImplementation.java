package org.lwjgl;

import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import org.lwjgl.opengl.Display;

final class WindowsSysImplementation extends DefaultSysImplementation {
  private static final int JNI_VERSION = 24;
  
  static {
    Sys.initialize();
  }
  
  public int getRequiredJNIVersion() {
    return 24;
  }
  
  public long getTimerResolution() {
    return 1000L;
  }
  
  public long getTime() {
    return nGetTime();
  }
  
  public boolean has64Bit() {
    return true;
  }
  
  private static long getHwnd() {
    if (!Display.isCreated())
      return 0L; 
    try {
      return ((Long)AccessController.<Long>doPrivileged((PrivilegedExceptionAction<Long>)new Object())).longValue();
    } catch (PrivilegedActionException e) {
      throw new Error(e);
    } 
  }
  
  public void alert(String title, String message) {
    if (!Display.isCreated())
      initCommonControls(); 
    LWJGLUtil.log(String.format("*** Alert *** %s\n%s\n", new Object[] { title, message }));
    ByteBuffer titleText = MemoryUtil.encodeUTF16(title);
    ByteBuffer messageText = MemoryUtil.encodeUTF16(message);
    nAlert(getHwnd(), MemoryUtil.getAddress(titleText), MemoryUtil.getAddress(messageText));
  }
  
  public boolean openURL(String url) {
    try {
      LWJGLUtil.execPrivileged(new String[] { "rundll32", "url.dll,FileProtocolHandler", url });
      return true;
    } catch (Exception e) {
      LWJGLUtil.log("Failed to open url (" + url + "): " + e.getMessage());
      return false;
    } 
  }
  
  public String getClipboard() {
    return nGetClipboard();
  }
  
  private static native long nGetTime();
  
  private static native void nAlert(long paramLong1, long paramLong2, long paramLong3);
  
  private static native void initCommonControls();
  
  private static native String nGetClipboard();
}
