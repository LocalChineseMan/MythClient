package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.Pipe;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

class SinkChannelImpl extends Pipe.SinkChannel implements SelChImpl {
  SocketChannel sc;
  
  public FileDescriptor getFD() {
    return ((SocketChannelImpl)this.sc).getFD();
  }
  
  public int getFDVal() {
    return ((SocketChannelImpl)this.sc).getFDVal();
  }
  
  SinkChannelImpl(SelectorProvider paramSelectorProvider, SocketChannel paramSocketChannel) {
    super(paramSelectorProvider);
    this.sc = paramSocketChannel;
  }
  
  protected void implCloseSelectableChannel() throws IOException {
    if (!isRegistered())
      kill(); 
  }
  
  public void kill() throws IOException {
    this.sc.close();
  }
  
  protected void implConfigureBlocking(boolean paramBoolean) throws IOException {
    this.sc.configureBlocking(paramBoolean);
  }
  
  public boolean translateReadyOps(int paramInt1, int paramInt2, SelectionKeyImpl paramSelectionKeyImpl) {
    int i = paramSelectionKeyImpl.nioInterestOps();
    int j = paramSelectionKeyImpl.nioReadyOps();
    int k = paramInt2;
    if ((paramInt1 & Net.POLLNVAL) != 0)
      throw new Error("POLLNVAL detected"); 
    if ((paramInt1 & (Net.POLLERR | Net.POLLHUP)) != 0) {
      k = i;
      paramSelectionKeyImpl.nioReadyOps(k);
      return ((k & (j ^ 0xFFFFFFFF)) != 0);
    } 
    if ((paramInt1 & Net.POLLOUT) != 0 && (i & 0x4) != 0)
      k |= 0x4; 
    paramSelectionKeyImpl.nioReadyOps(k);
    return ((k & (j ^ 0xFFFFFFFF)) != 0);
  }
  
  public boolean translateAndUpdateReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
    return translateReadyOps(paramInt, paramSelectionKeyImpl.nioReadyOps(), paramSelectionKeyImpl);
  }
  
  public boolean translateAndSetReadyOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
    return translateReadyOps(paramInt, 0, paramSelectionKeyImpl);
  }
  
  public void translateAndSetInterestOps(int paramInt, SelectionKeyImpl paramSelectionKeyImpl) {
    if ((paramInt & 0x4) != 0)
      paramInt = Net.POLLOUT; 
    paramSelectionKeyImpl.selector.putEventOps(paramSelectionKeyImpl, paramInt);
  }
  
  public int write(ByteBuffer paramByteBuffer) throws IOException {
    try {
      return this.sc.write(paramByteBuffer);
    } catch (AsynchronousCloseException asynchronousCloseException) {
      close();
      throw asynchronousCloseException;
    } 
  }
  
  public long write(ByteBuffer[] paramArrayOfByteBuffer) throws IOException {
    try {
      return this.sc.write(paramArrayOfByteBuffer);
    } catch (AsynchronousCloseException asynchronousCloseException) {
      close();
      throw asynchronousCloseException;
    } 
  }
  
  public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      throw new IndexOutOfBoundsException(); 
    try {
      return write(Util.subsequence(paramArrayOfByteBuffer, paramInt1, paramInt2));
    } catch (AsynchronousCloseException asynchronousCloseException) {
      close();
      throw asynchronousCloseException;
    } 
  }
}
