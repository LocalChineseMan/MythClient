package sun.nio.ch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Random;

class PipeImpl extends Pipe {
  private Pipe.SourceChannel source;
  
  private Pipe.SinkChannel sink;
  
  private static final Random rnd;
  
  static {
    byte[] arrayOfByte = new byte[8];
    boolean bool = IOUtil.randomBytes(arrayOfByte);
    if (bool) {
      rnd = new Random(ByteBuffer.wrap(arrayOfByte).getLong());
    } else {
      rnd = new Random();
    } 
  }
  
  private class Initializer implements PrivilegedExceptionAction<Void> {
    private final SelectorProvider sp;
    
    private IOException ioe = null;
    
    private Initializer(SelectorProvider param1SelectorProvider) {
      this.sp = param1SelectorProvider;
    }
    
    public Void run() throws IOException {
      LoopbackConnector loopbackConnector = new LoopbackConnector();
      loopbackConnector.run();
      if (this.ioe instanceof java.nio.channels.ClosedByInterruptException) {
        this.ioe = null;
        Object object = new Object(this, loopbackConnector);
        object.start();
        while (true) {
          try {
            object.join();
            break;
          } catch (InterruptedException interruptedException) {}
        } 
        Thread.currentThread().interrupt();
      } 
      if (this.ioe != null)
        throw new IOException("Unable to establish loopback connection", this.ioe); 
      return null;
    }
    
    private class LoopbackConnector implements Runnable {
      private LoopbackConnector() {}
      
      public void run() {
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel socketChannel1 = null;
        SocketChannel socketChannel2 = null;
        try {
          InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
          assert inetAddress.isLoopbackAddress();
          InetSocketAddress inetSocketAddress = null;
          while (true) {
            if (serverSocketChannel == null || !serverSocketChannel.isOpen()) {
              serverSocketChannel = ServerSocketChannel.open();
              serverSocketChannel.socket().bind(new InetSocketAddress(inetAddress, 0));
              inetSocketAddress = new InetSocketAddress(inetAddress, serverSocketChannel.socket().getLocalPort());
            } 
            socketChannel1 = SocketChannel.open(inetSocketAddress);
            ByteBuffer byteBuffer = ByteBuffer.allocate(8);
            long l = PipeImpl.rnd.nextLong();
            byteBuffer.putLong(l).flip();
            socketChannel1.write(byteBuffer);
            socketChannel2 = serverSocketChannel.accept();
            byteBuffer.clear();
            socketChannel2.read(byteBuffer);
            byteBuffer.rewind();
            if (byteBuffer.getLong() == l)
              break; 
            socketChannel2.close();
            socketChannel1.close();
          } 
          this.this$1.this$0.source = new SourceChannelImpl(PipeImpl.Initializer.this.sp, socketChannel1);
          this.this$1.this$0.sink = new SinkChannelImpl(PipeImpl.Initializer.this.sp, socketChannel2);
        } catch (IOException iOException) {
          try {
            if (socketChannel1 != null)
              socketChannel1.close(); 
            if (socketChannel2 != null)
              socketChannel2.close(); 
          } catch (IOException iOException1) {}
          PipeImpl.Initializer.this.ioe = iOException;
        } finally {
          try {
            if (serverSocketChannel != null)
              serverSocketChannel.close(); 
          } catch (IOException iOException) {}
        } 
      }
    }
  }
  
  PipeImpl(SelectorProvider paramSelectorProvider) throws IOException {
    try {
      AccessController.doPrivileged((PrivilegedExceptionAction<?>)new Initializer(paramSelectorProvider));
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getCause();
    } 
  }
  
  public Pipe.SourceChannel source() {
    return this.source;
  }
  
  public Pipe.SinkChannel sink() {
    return this.sink;
  }
}
