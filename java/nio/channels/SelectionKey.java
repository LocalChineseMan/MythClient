package java.nio.channels;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public abstract class SelectionKey {
  public static final int OP_READ = 1;
  
  public static final int OP_WRITE = 4;
  
  public static final int OP_CONNECT = 8;
  
  public static final int OP_ACCEPT = 16;
  
  public abstract SelectableChannel channel();
  
  public abstract Selector selector();
  
  public abstract boolean isValid();
  
  public abstract void cancel();
  
  public abstract int interestOps();
  
  public abstract SelectionKey interestOps(int paramInt);
  
  public abstract int readyOps();
  
  public final boolean isReadable() {
    return ((readyOps() & 0x1) != 0);
  }
  
  public final boolean isWritable() {
    return ((readyOps() & 0x4) != 0);
  }
  
  public final boolean isConnectable() {
    return ((readyOps() & 0x8) != 0);
  }
  
  public final boolean isAcceptable() {
    return ((readyOps() & 0x10) != 0);
  }
  
  private volatile Object attachment = null;
  
  private static final AtomicReferenceFieldUpdater<SelectionKey, Object> attachmentUpdater = AtomicReferenceFieldUpdater.newUpdater(SelectionKey.class, Object.class, "attachment");
  
  public final Object attach(Object paramObject) {
    return attachmentUpdater.getAndSet(this, paramObject);
  }
  
  public final Object attachment() {
    return this.attachment;
  }
}
