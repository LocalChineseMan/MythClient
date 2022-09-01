package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.Channel;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.InterruptibleChannel;
import sun.misc.SharedSecrets;
import sun.nio.ch.Interruptible;

public abstract class AbstractInterruptibleChannel implements Channel, InterruptibleChannel {
  private final Object closeLock = new Object();
  
  private volatile boolean open = true;
  
  private Interruptible interruptor;
  
  private volatile Thread interrupted;
  
  public final void close() throws IOException {
    synchronized (this.closeLock) {
      if (!this.open)
        return; 
      this.open = false;
      implCloseChannel();
    } 
  }
  
  public final boolean isOpen() {
    return this.open;
  }
  
  protected final void begin() {
    if (this.interruptor == null)
      this.interruptor = new Interruptible() {
          public void interrupt(Thread param1Thread) {
            synchronized (AbstractInterruptibleChannel.this.closeLock) {
              if (!AbstractInterruptibleChannel.this.open)
                return; 
              AbstractInterruptibleChannel.this.open = false;
              AbstractInterruptibleChannel.this.interrupted = param1Thread;
              try {
                AbstractInterruptibleChannel.this.implCloseChannel();
              } catch (IOException iOException) {}
            } 
          }
        }; 
    blockedOn(this.interruptor);
    Thread thread = Thread.currentThread();
    if (thread.isInterrupted())
      this.interruptor.interrupt(thread); 
  }
  
  protected final void end(boolean paramBoolean) throws AsynchronousCloseException {
    blockedOn(null);
    Thread thread = this.interrupted;
    if (thread != null && thread == Thread.currentThread()) {
      thread = null;
      throw new ClosedByInterruptException();
    } 
    if (!paramBoolean && !this.open)
      throw new AsynchronousCloseException(); 
  }
  
  static void blockedOn(Interruptible paramInterruptible) {
    SharedSecrets.getJavaLangAccess().blockedOn(Thread.currentThread(), paramInterruptible);
  }
  
  protected abstract void implCloseChannel() throws IOException;
}
