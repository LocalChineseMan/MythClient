package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import sun.misc.Unsafe;

public class SynchronousQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
  private static final long serialVersionUID = -3223113410248163686L;
  
  static abstract class Transferer<E> {
    abstract E transfer(E param1E, boolean param1Boolean, long param1Long);
  }
  
  static final int NCPUS = Runtime.getRuntime().availableProcessors();
  
  static final int maxTimedSpins = (NCPUS < 2) ? 0 : 32;
  
  static final int maxUntimedSpins = maxTimedSpins * 16;
  
  static final long spinForTimeoutThreshold = 1000L;
  
  private volatile transient Transferer<E> transferer;
  
  private ReentrantLock qlock;
  
  private WaitQueue waitingProducers;
  
  private WaitQueue waitingConsumers;
  
  static final class TransferStack<E> extends Transferer<E> {
    static final int REQUEST = 0;
    
    static final int DATA = 1;
    
    static final int FULFILLING = 2;
    
    volatile SNode head;
    
    private static final Unsafe UNSAFE;
    
    private static final long headOffset;
    
    static boolean isFulfilling(int param1Int) {
      return ((param1Int & 0x2) != 0);
    }
    
    static final class SNode {
      volatile SNode next;
      
      volatile SNode match;
      
      volatile Thread waiter;
      
      Object item;
      
      int mode;
      
      private static final Unsafe UNSAFE;
      
      private static final long matchOffset;
      
      private static final long nextOffset;
      
      SNode(Object param2Object) {
        this.item = param2Object;
      }
      
      boolean casNext(SNode param2SNode1, SNode param2SNode2) {
        return (param2SNode1 == this.next && UNSAFE
          .compareAndSwapObject(this, nextOffset, param2SNode1, param2SNode2));
      }
      
      boolean tryMatch(SNode param2SNode) {
        if (this.match == null && UNSAFE
          .compareAndSwapObject(this, matchOffset, null, param2SNode)) {
          Thread thread = this.waiter;
          if (thread != null) {
            this.waiter = null;
            LockSupport.unpark(thread);
          } 
          return true;
        } 
        return (this.match == param2SNode);
      }
      
      void tryCancel() {
        UNSAFE.compareAndSwapObject(this, matchOffset, null, this);
      }
      
      boolean isCancelled() {
        return (this.match == this);
      }
      
      static {
        try {
          UNSAFE = Unsafe.getUnsafe();
          Class<SNode> clazz = SNode.class;
          matchOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("match"));
          nextOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("next"));
        } catch (Exception exception) {
          throw new Error(exception);
        } 
      }
    }
    
    boolean casHead(SNode param1SNode1, SNode param1SNode2) {
      return (param1SNode1 == this.head && UNSAFE
        .compareAndSwapObject(this, headOffset, param1SNode1, param1SNode2));
    }
    
    static SNode snode(SNode param1SNode1, Object param1Object, SNode param1SNode2, int param1Int) {
      if (param1SNode1 == null)
        param1SNode1 = new SNode(param1Object); 
      param1SNode1.mode = param1Int;
      param1SNode1.next = param1SNode2;
      return param1SNode1;
    }
    
    E transfer(E param1E, boolean param1Boolean, long param1Long) {
      SNode sNode = null;
      boolean bool = (param1E == null) ? false : true;
      label60: while (true) {
        SNode sNode1 = this.head;
        if (sNode1 == null || sNode1.mode == bool) {
          if (param1Boolean && param1Long <= 0L) {
            if (sNode1 != null && sNode1.isCancelled()) {
              casHead(sNode1, sNode1.next);
              continue;
            } 
            return null;
          } 
          if (casHead(sNode1, sNode = snode(sNode, param1E, sNode1, bool))) {
            SNode sNode4 = awaitFulfill(sNode, param1Boolean, param1Long);
            if (sNode4 == sNode) {
              clean(sNode);
              return null;
            } 
            if ((sNode1 = this.head) != null && sNode1.next == sNode)
              casHead(sNode1, sNode.next); 
            return !bool ? (E)sNode4.item : (E)sNode.item;
          } 
          continue;
        } 
        if (!isFulfilling(sNode1.mode)) {
          if (sNode1.isCancelled()) {
            casHead(sNode1, sNode1.next);
            continue;
          } 
          if (casHead(sNode1, sNode = snode(sNode, param1E, sNode1, 0x2 | bool))) {
            while (true) {
              SNode sNode4 = sNode.next;
              if (sNode4 == null) {
                casHead(sNode, null);
                sNode = null;
                continue label60;
              } 
              SNode sNode5 = sNode4.next;
              if (sNode4.tryMatch(sNode)) {
                casHead(sNode, sNode5);
                return !bool ? (E)sNode4.item : (E)sNode.item;
              } 
              sNode.casNext(sNode4, sNode5);
            } 
            break;
          } 
          continue;
        } 
        SNode sNode2 = sNode1.next;
        if (sNode2 == null) {
          casHead(sNode1, null);
          continue;
        } 
        SNode sNode3 = sNode2.next;
        if (sNode2.tryMatch(sNode1)) {
          casHead(sNode1, sNode3);
          continue;
        } 
        sNode1.casNext(sNode2, sNode3);
      } 
    }
    
    SNode awaitFulfill(SNode param1SNode, boolean param1Boolean, long param1Long) {
      long l = param1Boolean ? (System.nanoTime() + param1Long) : 0L;
      Thread thread = Thread.currentThread();
      byte b = shouldSpin(param1SNode) ? (param1Boolean ? SynchronousQueue.maxTimedSpins : SynchronousQueue.maxUntimedSpins) : 0;
      while (true) {
        if (thread.isInterrupted())
          param1SNode.tryCancel(); 
        SNode sNode = param1SNode.match;
        if (sNode != null)
          return sNode; 
        if (param1Boolean) {
          param1Long = l - System.nanoTime();
          if (param1Long <= 0L) {
            param1SNode.tryCancel();
            continue;
          } 
        } 
        if (b) {
          b = shouldSpin(param1SNode) ? (b - 1) : 0;
          continue;
        } 
        if (param1SNode.waiter == null) {
          param1SNode.waiter = thread;
          continue;
        } 
        if (!param1Boolean) {
          LockSupport.park(this);
          continue;
        } 
        if (param1Long > 1000L)
          LockSupport.parkNanos(this, param1Long); 
      } 
    }
    
    boolean shouldSpin(SNode param1SNode) {
      SNode sNode = this.head;
      return (sNode == param1SNode || sNode == null || isFulfilling(sNode.mode));
    }
    
    void clean(SNode param1SNode) {
      param1SNode.item = null;
      param1SNode.waiter = null;
      SNode sNode1 = param1SNode.next;
      if (sNode1 != null && sNode1.isCancelled())
        sNode1 = sNode1.next; 
      SNode sNode2;
      while ((sNode2 = this.head) != null && sNode2 != sNode1 && sNode2.isCancelled())
        casHead(sNode2, sNode2.next); 
      while (sNode2 != null && sNode2 != sNode1) {
        SNode sNode = sNode2.next;
        if (sNode != null && sNode.isCancelled()) {
          sNode2.casNext(sNode, sNode.next);
          continue;
        } 
        sNode2 = sNode;
      } 
    }
    
    static {
      try {
        UNSAFE = Unsafe.getUnsafe();
        Class<TransferStack> clazz = TransferStack.class;
        headOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("head"));
      } catch (Exception exception) {
        throw new Error(exception);
      } 
    }
  }
  
  public SynchronousQueue() {
    this(false);
  }
  
  public SynchronousQueue(boolean paramBoolean) {
    this.transferer = paramBoolean ? new TransferQueue<>() : new TransferStack<>();
  }
  
  public void put(E paramE) throws InterruptedException {
    if (paramE == null)
      throw new NullPointerException(); 
    if (this.transferer.transfer(paramE, false, 0L) == null) {
      Thread.interrupted();
      throw new InterruptedException();
    } 
  }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    if (paramE == null)
      throw new NullPointerException(); 
    if (this.transferer.transfer(paramE, true, paramTimeUnit.toNanos(paramLong)) != null)
      return true; 
    if (!Thread.interrupted())
      return false; 
    throw new InterruptedException();
  }
  
  public boolean offer(E paramE) {
    if (paramE == null)
      throw new NullPointerException(); 
    return (this.transferer.transfer(paramE, true, 0L) != null);
  }
  
  public E take() throws InterruptedException {
    E e = this.transferer.transfer(null, false, 0L);
    if (e != null)
      return e; 
    Thread.interrupted();
    throw new InterruptedException();
  }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    E e = this.transferer.transfer(null, true, paramTimeUnit.toNanos(paramLong));
    if (e != null || !Thread.interrupted())
      return e; 
    throw new InterruptedException();
  }
  
  public E poll() {
    return this.transferer.transfer(null, true, 0L);
  }
  
  public boolean isEmpty() {
    return true;
  }
  
  public int size() {
    return 0;
  }
  
  public int remainingCapacity() {
    return 0;
  }
  
  public void clear() {}
  
  public boolean contains(Object paramObject) {
    return false;
  }
  
  public boolean remove(Object paramObject) {
    return false;
  }
  
  public boolean containsAll(Collection<?> paramCollection) {
    return paramCollection.isEmpty();
  }
  
  public boolean removeAll(Collection<?> paramCollection) {
    return false;
  }
  
  public boolean retainAll(Collection<?> paramCollection) {
    return false;
  }
  
  public E peek() {
    return null;
  }
  
  public Iterator<E> iterator() {
    return Collections.emptyIterator();
  }
  
  public Spliterator<E> spliterator() {
    return Spliterators.emptySpliterator();
  }
  
  public Object[] toArray() {
    return new Object[0];
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    if (paramArrayOfT.length > 0)
      paramArrayOfT[0] = null; 
    return paramArrayOfT;
  }
  
  public int drainTo(Collection<? super E> paramCollection) {
    if (paramCollection == null)
      throw new NullPointerException(); 
    if (paramCollection == this)
      throw new IllegalArgumentException(); 
    byte b = 0;
    E e;
    while ((e = poll()) != null) {
      paramCollection.add(e);
      b++;
    } 
    return b;
  }
  
  public int drainTo(Collection<? super E> paramCollection, int paramInt) {
    if (paramCollection == null)
      throw new NullPointerException(); 
    if (paramCollection == this)
      throw new IllegalArgumentException(); 
    byte b = 0;
    E e;
    while (b < paramInt && (e = poll()) != null) {
      paramCollection.add(e);
      b++;
    } 
    return b;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    boolean bool = this.transferer instanceof TransferQueue;
    if (bool) {
      this.qlock = new ReentrantLock(true);
      this.waitingProducers = new FifoWaitQueue();
      this.waitingConsumers = new FifoWaitQueue();
    } else {
      this.qlock = new ReentrantLock();
      this.waitingProducers = new LifoWaitQueue();
      this.waitingConsumers = new LifoWaitQueue();
    } 
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.waitingProducers instanceof FifoWaitQueue) {
      this.transferer = new TransferQueue<>();
    } else {
      this.transferer = new TransferStack<>();
    } 
  }
  
  static long objectFieldOffset(Unsafe paramUnsafe, String paramString, Class<?> paramClass) {
    try {
      return paramUnsafe.objectFieldOffset(paramClass.getDeclaredField(paramString));
    } catch (NoSuchFieldException noSuchFieldException) {
      NoSuchFieldError noSuchFieldError = new NoSuchFieldError(paramString);
      noSuchFieldError.initCause(noSuchFieldException);
      throw noSuchFieldError;
    } 
  }
  
  static final class SynchronousQueue {}
  
  static class SynchronousQueue {}
  
  static class SynchronousQueue {}
  
  static class SynchronousQueue {}
}
