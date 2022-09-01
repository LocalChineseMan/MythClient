package sun.awt.datatransfer;

public interface ToolkitThreadBlockedHandler {
  void lock();
  
  void unlock();
  
  void enter();
  
  void exit();
}
