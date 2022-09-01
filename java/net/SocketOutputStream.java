package java.net;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

class SocketOutputStream extends FileOutputStream {
  static {
    init();
  }
  
  private AbstractPlainSocketImpl impl = null;
  
  private byte[] temp = new byte[1];
  
  private Socket socket = null;
  
  private boolean closing;
  
  SocketOutputStream(AbstractPlainSocketImpl paramAbstractPlainSocketImpl) throws IOException {
    super(paramAbstractPlainSocketImpl.getFileDescriptor());
    this.closing = false;
    this.impl = paramAbstractPlainSocketImpl;
    this.socket = paramAbstractPlainSocketImpl.getSocket();
  }
  
  public final FileChannel getChannel() {
    return null;
  }
  
  private void socketWrite(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 <= 0 || paramInt1 < 0 || paramInt1 + paramInt2 > paramArrayOfbyte.length) {
      if (paramInt2 == 0)
        return; 
      throw new ArrayIndexOutOfBoundsException();
    } 
    FileDescriptor fileDescriptor = this.impl.acquireFD();
    try {
      socketWrite0(fileDescriptor, paramArrayOfbyte, paramInt1, paramInt2);
    } catch (SocketException socketException) {
      if (socketException instanceof sun.net.ConnectionResetException) {
        this.impl.setConnectionResetPending();
        socketException = new SocketException("Connection reset");
      } 
      if (this.impl.isClosedOrPending())
        throw new SocketException("Socket closed"); 
      throw socketException;
    } finally {
      this.impl.releaseFD();
    } 
  }
  
  public void write(int paramInt) throws IOException {
    this.temp[0] = (byte)paramInt;
    socketWrite(this.temp, 0, 1);
  }
  
  public void write(byte[] paramArrayOfbyte) throws IOException {
    socketWrite(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    socketWrite(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public void close() throws IOException {
    if (this.closing)
      return; 
    this.closing = true;
    if (this.socket != null) {
      if (!this.socket.isClosed())
        this.socket.close(); 
    } else {
      this.impl.close();
    } 
    this.closing = false;
  }
  
  protected void finalize() {}
  
  private native void socketWrite0(FileDescriptor paramFileDescriptor, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  private static native void init();
}
