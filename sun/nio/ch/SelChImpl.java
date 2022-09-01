package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.Channel;

public interface SelChImpl extends Channel {
  FileDescriptor getFD();
  
  int getFDVal();
  
  boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl);
  
  boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl);
  
  void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl);
  
  int validOps();
  
  void kill() throws IOException;
}
