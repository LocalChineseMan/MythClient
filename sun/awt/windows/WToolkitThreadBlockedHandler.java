package sun.awt.windows;

import sun.awt.Mutex;
import sun.awt.datatransfer.ToolkitThreadBlockedHandler;

final class WToolkitThreadBlockedHandler extends Mutex implements ToolkitThreadBlockedHandler {
  public void enter() {
    if (!isOwned())
      throw new IllegalMonitorStateException(); 
    unlock();
    startSecondaryEventLoop();
    lock();
  }
  
  public void exit() {
    if (!isOwned())
      throw new IllegalMonitorStateException(); 
    WToolkit.quitSecondaryEventLoop();
  }
  
  private native void startSecondaryEventLoop();
}
