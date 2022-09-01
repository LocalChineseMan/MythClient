package java.nio.channels;

import java.io.IOException;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.channels.spi.SelectorProvider;

public abstract class SelectableChannel extends AbstractInterruptibleChannel implements Channel {
  public abstract SelectorProvider provider();
  
  public abstract int validOps();
  
  public abstract boolean isRegistered();
  
  public abstract SelectionKey keyFor(Selector paramSelector);
  
  public abstract SelectionKey register(Selector paramSelector, int paramInt, Object paramObject) throws ClosedChannelException;
  
  public final SelectionKey register(Selector paramSelector, int paramInt) throws ClosedChannelException {
    return register(paramSelector, paramInt, null);
  }
  
  public abstract SelectableChannel configureBlocking(boolean paramBoolean) throws IOException;
  
  public abstract boolean isBlocking();
  
  public abstract Object blockingLock();
}
