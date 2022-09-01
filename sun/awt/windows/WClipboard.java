package sun.awt.windows;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.util.SortedMap;
import sun.awt.datatransfer.SunClipboard;

final class WClipboard extends SunClipboard {
  private boolean isClipboardViewerRegistered;
  
  WClipboard() {
    super("System");
  }
  
  public long getID() {
    return 0L;
  }
  
  protected void setContentsNative(Transferable paramTransferable) {
    SortedMap<Long, DataFlavor> sortedMap = WDataTransferer.getInstance().getFormatsForTransferable(paramTransferable, getDefaultFlavorTable());
    openClipboard((SunClipboard)this);
    try {
      for (Long long_ : sortedMap.keySet()) {
        DataFlavor dataFlavor = sortedMap.get(long_);
        try {
          byte[] arrayOfByte = WDataTransferer.getInstance().translateTransferable(paramTransferable, dataFlavor, long_.longValue());
          publishClipboardData(long_.longValue(), arrayOfByte);
        } catch (IOException iOException) {
          if (!dataFlavor.isMimeTypeEqual("application/x-java-jvm-local-objectref") || !(iOException instanceof java.io.NotSerializableException))
            iOException.printStackTrace(); 
        } 
      } 
    } finally {
      closeClipboard();
    } 
  }
  
  private void lostSelectionOwnershipImpl() {
    lostOwnershipImpl();
  }
  
  protected void clearNativeContext() {}
  
  static {
    init();
  }
  
  protected void registerClipboardViewerChecked() {
    if (!this.isClipboardViewerRegistered) {
      registerClipboardViewer();
      this.isClipboardViewerRegistered = true;
    } 
  }
  
  protected void unregisterClipboardViewerChecked() {}
  
  private void handleContentsChanged() {
    if (!areFlavorListenersRegistered())
      return; 
    long[] arrayOfLong = null;
    try {
      openClipboard((SunClipboard)null);
      arrayOfLong = getClipboardFormats();
    } catch (IllegalStateException illegalStateException) {
    
    } finally {
      closeClipboard();
    } 
    checkChange(arrayOfLong);
  }
  
  protected Transferable createLocaleTransferable(long[] paramArrayOflong) throws IOException {
    boolean bool = false;
    for (byte b = 0; b < paramArrayOflong.length; b++) {
      if (paramArrayOflong[b] == 16L) {
        bool = true;
        break;
      } 
    } 
    if (!bool)
      return null; 
    byte[] arrayOfByte1 = null;
    try {
      arrayOfByte1 = getClipboardData(16L);
    } catch (IOException iOException) {
      return null;
    } 
    byte[] arrayOfByte2 = arrayOfByte1;
    return (Transferable)new Object(this, arrayOfByte2);
  }
  
  public native void openClipboard(SunClipboard paramSunClipboard) throws IllegalStateException;
  
  public native void closeClipboard();
  
  private native void publishClipboardData(long paramLong, byte[] paramArrayOfbyte);
  
  private static native void init();
  
  protected native long[] getClipboardFormats();
  
  protected native byte[] getClipboardData(long paramLong) throws IOException;
  
  private native void registerClipboardViewer();
}
