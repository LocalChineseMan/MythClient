package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import sun.nio.ch.Interruptible;

public abstract class AbstractSelector extends Selector {
  private AtomicBoolean selectorOpen = new AtomicBoolean(true);
  
  private final SelectorProvider provider;
  
  private final Set<SelectionKey> cancelledKeys;
  
  private Interruptible interruptor;
  
  protected AbstractSelector(SelectorProvider paramSelectorProvider) {
    this.cancelledKeys = new HashSet<>();
    this.interruptor = null;
    this.provider = paramSelectorProvider;
  }
  
  void cancel(SelectionKey paramSelectionKey) {
    synchronized (this.cancelledKeys) {
      this.cancelledKeys.add(paramSelectionKey);
    } 
  }
  
  public final void close() throws IOException {
    boolean bool = this.selectorOpen.getAndSet(false);
    if (!bool)
      return; 
    implCloseSelector();
  }
  
  protected abstract void implCloseSelector() throws IOException;
  
  public final boolean isOpen() {
    return this.selectorOpen.get();
  }
  
  public final SelectorProvider provider() {
    return this.provider;
  }
  
  protected final Set<SelectionKey> cancelledKeys() {
    return this.cancelledKeys;
  }
  
  protected abstract SelectionKey register(AbstractSelectableChannel paramAbstractSelectableChannel, int paramInt, Object paramObject);
  
  protected final void deregister(AbstractSelectionKey paramAbstractSelectionKey) {
    ((AbstractSelectableChannel)paramAbstractSelectionKey.channel()).removeKey(paramAbstractSelectionKey);
  }
  
  protected final void begin() {
    if (this.interruptor == null)
      this.interruptor = new Interruptible() {
          public void interrupt(Thread param1Thread) {
            AbstractSelector.this.wakeup();
          }
        }; 
    AbstractInterruptibleChannel.blockedOn(this.interruptor);
    Thread thread = Thread.currentThread();
    if (thread.isInterrupted())
      this.interruptor.interrupt(thread); 
  }
  
  protected final void end() {
    AbstractInterruptibleChannel.blockedOn(null);
  }
}
