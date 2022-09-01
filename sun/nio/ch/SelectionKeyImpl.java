package sun.nio.ch;

import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectionKey;

public class SelectionKeyImpl extends AbstractSelectionKey {
  final SelChImpl channel;
  
  public final SelectorImpl selector;
  
  private int index;
  
  private volatile int interestOps;
  
  private int readyOps;
  
  SelectionKeyImpl(SelChImpl paramSelChImpl, SelectorImpl paramSelectorImpl) {
    this.channel = paramSelChImpl;
    this.selector = paramSelectorImpl;
  }
  
  public SelectableChannel channel() {
    return (SelectableChannel)this.channel;
  }
  
  public Selector selector() {
    return this.selector;
  }
  
  int getIndex() {
    return this.index;
  }
  
  void setIndex(int paramInt) {
    this.index = paramInt;
  }
  
  private void ensureValid() {
    if (!isValid())
      throw new CancelledKeyException(); 
  }
  
  public int interestOps() {
    ensureValid();
    return this.interestOps;
  }
  
  public SelectionKey interestOps(int paramInt) {
    ensureValid();
    return nioInterestOps(paramInt);
  }
  
  public int readyOps() {
    ensureValid();
    return this.readyOps;
  }
  
  public void nioReadyOps(int paramInt) {
    this.readyOps = paramInt;
  }
  
  public int nioReadyOps() {
    return this.readyOps;
  }
  
  public SelectionKey nioInterestOps(int paramInt) {
    if ((paramInt & (channel().validOps() ^ 0xFFFFFFFF)) != 0)
      throw new IllegalArgumentException(); 
    this.channel.translateAndSetInterestOps(paramInt, this);
    this.interestOps = paramInt;
    return this;
  }
  
  public int nioInterestOps() {
    return this.interestOps;
  }
}
