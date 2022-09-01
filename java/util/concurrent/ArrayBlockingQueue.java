package java.util.concurrent;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrayBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
  private static final long serialVersionUID = -817911632652898426L;
  
  final Object[] items;
  
  int takeIndex;
  
  int putIndex;
  
  int count;
  
  final ReentrantLock lock;
  
  private final Condition notEmpty;
  
  private final Condition notFull;
  
  transient Itrs itrs = null;
  
  final int dec(int paramInt) {
    return ((paramInt == 0) ? this.items.length : paramInt) - 1;
  }
  
  final E itemAt(int paramInt) {
    return (E)this.items[paramInt];
  }
  
  private static void checkNotNull(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
  }
  
  private void enqueue(E paramE) {
    Object[] arrayOfObject = this.items;
    arrayOfObject[this.putIndex] = paramE;
    if (++this.putIndex == arrayOfObject.length)
      this.putIndex = 0; 
    this.count++;
    this.notEmpty.signal();
  }
  
  private E dequeue() {
    Object[] arrayOfObject = this.items;
    Object object = arrayOfObject[this.takeIndex];
    arrayOfObject[this.takeIndex] = null;
    if (++this.takeIndex == arrayOfObject.length)
      this.takeIndex = 0; 
    this.count--;
    if (this.itrs != null)
      this.itrs.elementDequeued(); 
    this.notFull.signal();
    return (E)object;
  }
  
  void removeAt(int paramInt) {
    Object[] arrayOfObject = this.items;
    if (paramInt == this.takeIndex) {
      arrayOfObject[this.takeIndex] = null;
      if (++this.takeIndex == arrayOfObject.length)
        this.takeIndex = 0; 
      this.count--;
      if (this.itrs != null)
        this.itrs.elementDequeued(); 
    } else {
      int i = this.putIndex;
      int j = paramInt;
      while (true) {
        int k = j + 1;
        if (k == arrayOfObject.length)
          k = 0; 
        if (k != i) {
          arrayOfObject[j] = arrayOfObject[k];
          j = k;
          continue;
        } 
        break;
      } 
      arrayOfObject[j] = null;
      this.putIndex = j;
      this.count--;
      if (this.itrs != null)
        this.itrs.removedAt(paramInt); 
    } 
    this.notFull.signal();
  }
  
  public ArrayBlockingQueue(int paramInt) {
    this(paramInt, false);
  }
  
  public ArrayBlockingQueue(int paramInt, boolean paramBoolean) {
    if (paramInt <= 0)
      throw new IllegalArgumentException(); 
    this.items = new Object[paramInt];
    this.lock = new ReentrantLock(paramBoolean);
    this.notEmpty = this.lock.newCondition();
    this.notFull = this.lock.newCondition();
  }
  
  public ArrayBlockingQueue(int paramInt, boolean paramBoolean, Collection<? extends E> paramCollection) {
    // Byte code:
    //   0: aload_0
    //   1: iload_1
    //   2: iload_2
    //   3: invokespecial <init> : (IZ)V
    //   6: aload_0
    //   7: getfield lock : Ljava/util/concurrent/locks/ReentrantLock;
    //   10: astore #4
    //   12: aload #4
    //   14: invokevirtual lock : ()V
    //   17: iconst_0
    //   18: istore #5
    //   20: aload_3
    //   21: invokeinterface iterator : ()Ljava/util/Iterator;
    //   26: astore #6
    //   28: aload #6
    //   30: invokeinterface hasNext : ()Z
    //   35: ifeq -> 67
    //   38: aload #6
    //   40: invokeinterface next : ()Ljava/lang/Object;
    //   45: astore #7
    //   47: aload #7
    //   49: invokestatic checkNotNull : (Ljava/lang/Object;)V
    //   52: aload_0
    //   53: getfield items : [Ljava/lang/Object;
    //   56: iload #5
    //   58: iinc #5, 1
    //   61: aload #7
    //   63: aastore
    //   64: goto -> 28
    //   67: goto -> 80
    //   70: astore #6
    //   72: new java/lang/IllegalArgumentException
    //   75: dup
    //   76: invokespecial <init> : ()V
    //   79: athrow
    //   80: aload_0
    //   81: iload #5
    //   83: putfield count : I
    //   86: aload_0
    //   87: iload #5
    //   89: iload_1
    //   90: if_icmpne -> 97
    //   93: iconst_0
    //   94: goto -> 99
    //   97: iload #5
    //   99: putfield putIndex : I
    //   102: aload #4
    //   104: invokevirtual unlock : ()V
    //   107: goto -> 120
    //   110: astore #8
    //   112: aload #4
    //   114: invokevirtual unlock : ()V
    //   117: aload #8
    //   119: athrow
    //   120: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #279	-> 0
    //   #281	-> 6
    //   #282	-> 12
    //   #284	-> 17
    //   #286	-> 20
    //   #287	-> 47
    //   #288	-> 52
    //   #289	-> 64
    //   #292	-> 67
    //   #290	-> 70
    //   #291	-> 72
    //   #293	-> 80
    //   #294	-> 86
    //   #296	-> 102
    //   #297	-> 107
    //   #296	-> 110
    //   #298	-> 120
    // Exception table:
    //   from	to	target	type
    //   17	102	110	finally
    //   20	67	70	java/lang/ArrayIndexOutOfBoundsException
    //   110	112	110	finally
  }
  
  public boolean add(E paramE) {
    return super.add(paramE);
  }
  
  public boolean offer(E paramE) {
    checkNotNull(paramE);
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      if (this.count == this.items.length)
        return false; 
      enqueue(paramE);
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void put(E paramE) throws InterruptedException {
    checkNotNull(paramE);
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lockInterruptibly();
    try {
      while (this.count == this.items.length)
        this.notFull.await(); 
      enqueue(paramE);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    checkNotNull(paramE);
    long l = paramTimeUnit.toNanos(paramLong);
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lockInterruptibly();
    try {
      while (this.count == this.items.length) {
        if (l <= 0L)
          return false; 
        l = this.notFull.awaitNanos(l);
      } 
      enqueue(paramE);
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E poll() {
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return (this.count == 0) ? null : dequeue();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E take() throws InterruptedException {
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lockInterruptibly();
    try {
      while (this.count == 0)
        this.notEmpty.await(); 
      return dequeue();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    long l = paramTimeUnit.toNanos(paramLong);
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lockInterruptibly();
    try {
      while (this.count == 0) {
        if (l <= 0L)
          return null; 
        l = this.notEmpty.awaitNanos(l);
      } 
      return dequeue();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E peek() {
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return itemAt(this.takeIndex);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int size() {
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return this.count;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int remainingCapacity() {
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return this.items.length - this.count;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean remove(Object paramObject) {
    if (paramObject == null)
      return false; 
    Object[] arrayOfObject = this.items;
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      if (this.count > 0) {
        int i = this.putIndex;
        int j = this.takeIndex;
        do {
          if (paramObject.equals(arrayOfObject[j])) {
            removeAt(j);
            return true;
          } 
          if (++j != arrayOfObject.length)
            continue; 
          j = 0;
        } while (j != i);
      } 
      return false;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean contains(Object paramObject) {
    if (paramObject == null)
      return false; 
    Object[] arrayOfObject = this.items;
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      if (this.count > 0) {
        int i = this.putIndex;
        int j = this.takeIndex;
        do {
          if (paramObject.equals(arrayOfObject[j]))
            return true; 
          if (++j != arrayOfObject.length)
            continue; 
          j = 0;
        } while (j != i);
      } 
      return false;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public Object[] toArray() {
    Object[] arrayOfObject;
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      int i = this.count;
      arrayOfObject = new Object[i];
      int j = this.items.length - this.takeIndex;
      if (i <= j) {
        System.arraycopy(this.items, this.takeIndex, arrayOfObject, 0, i);
      } else {
        System.arraycopy(this.items, this.takeIndex, arrayOfObject, 0, j);
        System.arraycopy(this.items, 0, arrayOfObject, j, i - j);
      } 
    } finally {
      reentrantLock.unlock();
    } 
    return arrayOfObject;
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    Object[] arrayOfObject = this.items;
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      int i = this.count;
      int j = paramArrayOfT.length;
      if (j < i)
        paramArrayOfT = (T[])Array.newInstance(paramArrayOfT
            .getClass().getComponentType(), i); 
      int k = arrayOfObject.length - this.takeIndex;
      if (i <= k) {
        System.arraycopy(arrayOfObject, this.takeIndex, paramArrayOfT, 0, i);
      } else {
        System.arraycopy(arrayOfObject, this.takeIndex, paramArrayOfT, 0, k);
        System.arraycopy(arrayOfObject, 0, paramArrayOfT, k, i - k);
      } 
      if (j > i)
        paramArrayOfT[i] = null; 
    } finally {
      reentrantLock.unlock();
    } 
    return paramArrayOfT;
  }
  
  public String toString() {
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      int i = this.count;
      if (i == 0)
        return "[]"; 
      Object[] arrayOfObject = this.items;
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append('[');
      int j = this.takeIndex;
      while (true) {
        Object object = arrayOfObject[j];
        stringBuilder.append((object == this) ? "(this Collection)" : object);
        if (--i == 0)
          return stringBuilder.append(']').toString(); 
        stringBuilder.append(',').append(' ');
        if (++j == arrayOfObject.length)
          j = 0; 
      } 
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void clear() {
    Object[] arrayOfObject = this.items;
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      int i = this.count;
      if (i > 0) {
        int j = this.putIndex;
        int k = this.takeIndex;
        while (true) {
          arrayOfObject[k] = null;
          if (++k == arrayOfObject.length)
            k = 0; 
          if (k == j) {
            this.takeIndex = j;
            this.count = 0;
            if (this.itrs != null)
              this.itrs.queueIsEmpty(); 
            for (; i > 0 && reentrantLock.hasWaiters(this.notFull); i--)
              this.notFull.signal(); 
            break;
          } 
        } 
      } 
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int drainTo(Collection<? super E> paramCollection) {
    return drainTo(paramCollection, 2147483647);
  }
  
  public int drainTo(Collection<? super E> paramCollection, int paramInt) {
    checkNotNull(paramCollection);
    if (paramCollection == this)
      throw new IllegalArgumentException(); 
    if (paramInt <= 0)
      return 0; 
    Object[] arrayOfObject = this.items;
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      int i = Math.min(paramInt, this.count);
      int j = this.takeIndex;
      byte b = 0;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public Iterator<E> iterator() {
    return new Itr();
  }
  
  class Itrs {
    private class Node extends WeakReference<ArrayBlockingQueue<E>.Itr> {
      Node next;
      
      Node(ArrayBlockingQueue<E>.Itr param2Itr, Node param2Node) {
        super(param2Itr);
        this.next = param2Node;
      }
    }
    
    int cycles = 0;
    
    private Node head;
    
    private Node sweeper = null;
    
    private static final int SHORT_SWEEP_PROBES = 4;
    
    private static final int LONG_SWEEP_PROBES = 16;
    
    Itrs(ArrayBlockingQueue<E>.Itr param1Itr) {
      register(param1Itr);
    }
    
    void doSomeSweeping(boolean param1Boolean) {
      Node node1, node2;
      boolean bool;
      byte b = param1Boolean ? 16 : 4;
      Node node3 = this.sweeper;
      if (node3 == null) {
        node1 = null;
        node2 = this.head;
        bool = true;
      } else {
        node1 = node3;
        node2 = node1.next;
        bool = false;
      } 
      for (; b > 0; b--) {
        if (node2 == null) {
          if (bool)
            break; 
          node1 = null;
          node2 = this.head;
          bool = true;
        } 
        ArrayBlockingQueue<E>.Itr itr = node2.get();
        Node node = node2.next;
        if (itr == null || itr.isDetached()) {
          b = 16;
          node2.clear();
          node2.next = null;
          if (node1 == null) {
            this.head = node;
            if (node == null) {
              ArrayBlockingQueue.this.itrs = null;
              return;
            } 
          } else {
            node1.next = node;
          } 
        } else {
          node1 = node2;
        } 
        node2 = node;
      } 
      this.sweeper = (node2 == null) ? null : node1;
    }
    
    void register(ArrayBlockingQueue<E>.Itr param1Itr) {
      this.head = new Node(param1Itr, this.head);
    }
    
    void takeIndexWrapped() {
      this.cycles++;
      for (Node node1 = null, node2 = this.head; node2 != null; ) {
        ArrayBlockingQueue<E>.Itr itr = node2.get();
        Node node = node2.next;
        if (itr == null || itr.takeIndexWrapped()) {
          node2.clear();
          node2.next = null;
          if (node1 == null) {
            this.head = node;
          } else {
            node1.next = node;
          } 
        } else {
          node1 = node2;
        } 
        node2 = node;
      } 
      if (this.head == null)
        ArrayBlockingQueue.this.itrs = null; 
    }
    
    void removedAt(int param1Int) {
      for (Node node1 = null, node2 = this.head; node2 != null; ) {
        ArrayBlockingQueue<E>.Itr itr = node2.get();
        Node node = node2.next;
        if (itr == null || itr.removedAt(param1Int)) {
          node2.clear();
          node2.next = null;
          if (node1 == null) {
            this.head = node;
          } else {
            node1.next = node;
          } 
        } else {
          node1 = node2;
        } 
        node2 = node;
      } 
      if (this.head == null)
        ArrayBlockingQueue.this.itrs = null; 
    }
    
    void queueIsEmpty() {
      for (Node node = this.head; node != null; node = node.next) {
        ArrayBlockingQueue<E>.Itr itr = node.get();
        if (itr != null) {
          node.clear();
          itr.shutdown();
        } 
      } 
      this.head = null;
      ArrayBlockingQueue.this.itrs = null;
    }
    
    void elementDequeued() {
      if (ArrayBlockingQueue.this.count == 0) {
        queueIsEmpty();
      } else if (ArrayBlockingQueue.this.takeIndex == 0) {
        takeIndexWrapped();
      } 
    }
  }
  
  private class Itr implements Iterator<E> {
    private int cursor;
    
    private E nextItem;
    
    private int nextIndex;
    
    private E lastItem;
    
    private int lastRet = -1;
    
    private int prevTakeIndex;
    
    private int prevCycles;
    
    private static final int NONE = -1;
    
    private static final int REMOVED = -2;
    
    private static final int DETACHED = -3;
    
    Itr() {
      ReentrantLock reentrantLock = ArrayBlockingQueue.this.lock;
      reentrantLock.lock();
      try {
        if (ArrayBlockingQueue.this.count == 0) {
          this.cursor = -1;
          this.nextIndex = -1;
          this.prevTakeIndex = -3;
        } else {
          int i = ArrayBlockingQueue.this.takeIndex;
          this.prevTakeIndex = i;
          this.nextItem = ArrayBlockingQueue.this.itemAt(this.nextIndex = i);
          this.cursor = incCursor(i);
          if (ArrayBlockingQueue.this.itrs == null) {
            ArrayBlockingQueue.this.itrs = new ArrayBlockingQueue.Itrs(this);
          } else {
            ArrayBlockingQueue.this.itrs.register(this);
            ArrayBlockingQueue.this.itrs.doSomeSweeping(false);
          } 
          this.prevCycles = ArrayBlockingQueue.this.itrs.cycles;
        } 
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    boolean isDetached() {
      return (this.prevTakeIndex < 0);
    }
    
    private int incCursor(int param1Int) {
      if (++param1Int == ArrayBlockingQueue.this.items.length)
        param1Int = 0; 
      if (param1Int == ArrayBlockingQueue.this.putIndex)
        param1Int = -1; 
      return param1Int;
    }
    
    private boolean invalidated(int param1Int1, int param1Int2, long param1Long, int param1Int3) {
      if (param1Int1 < 0)
        return false; 
      int i = param1Int1 - param1Int2;
      if (i < 0)
        i += param1Int3; 
      return (param1Long > i);
    }
    
    private void incorporateDequeues() {
      int i = ArrayBlockingQueue.this.itrs.cycles;
      int j = ArrayBlockingQueue.this.takeIndex;
      int k = this.prevCycles;
      int m = this.prevTakeIndex;
      if (i != k || j != m) {
        int n = ArrayBlockingQueue.this.items.length;
        long l = ((i - k) * n + j - m);
        if (invalidated(this.lastRet, m, l, n))
          this.lastRet = -2; 
        if (invalidated(this.nextIndex, m, l, n))
          this.nextIndex = -2; 
        if (invalidated(this.cursor, m, l, n))
          this.cursor = j; 
        if (this.cursor < 0 && this.nextIndex < 0 && this.lastRet < 0) {
          detach();
        } else {
          this.prevCycles = i;
          this.prevTakeIndex = j;
        } 
      } 
    }
    
    private void detach() {
      if (this.prevTakeIndex >= 0) {
        this.prevTakeIndex = -3;
        ArrayBlockingQueue.this.itrs.doSomeSweeping(true);
      } 
    }
    
    public boolean hasNext() {
      if (this.nextItem != null)
        return true; 
      noNext();
      return false;
    }
    
    private void noNext() {
      ReentrantLock reentrantLock = ArrayBlockingQueue.this.lock;
      reentrantLock.lock();
      try {
        if (!isDetached()) {
          incorporateDequeues();
          if (this.lastRet >= 0) {
            this.lastItem = ArrayBlockingQueue.this.itemAt(this.lastRet);
            detach();
          } 
        } 
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public E next() {
      E e = this.nextItem;
      if (e == null)
        throw new NoSuchElementException(); 
      ReentrantLock reentrantLock = ArrayBlockingQueue.this.lock;
      reentrantLock.lock();
      try {
        if (!isDetached())
          incorporateDequeues(); 
        this.lastRet = this.nextIndex;
        int i = this.cursor;
        if (i >= 0) {
          this.nextItem = ArrayBlockingQueue.this.itemAt(this.nextIndex = i);
          this.cursor = incCursor(i);
        } else {
          this.nextIndex = -1;
          this.nextItem = null;
        } 
      } finally {
        reentrantLock.unlock();
      } 
      return e;
    }
    
    public void remove() {
      ReentrantLock reentrantLock = ArrayBlockingQueue.this.lock;
      reentrantLock.lock();
      try {
        if (!isDetached())
          incorporateDequeues(); 
        int i = this.lastRet;
        this.lastRet = -1;
        if (i >= 0) {
          if (!isDetached()) {
            ArrayBlockingQueue.this.removeAt(i);
          } else {
            E e = this.lastItem;
            this.lastItem = null;
            if (ArrayBlockingQueue.this.itemAt(i) == e)
              ArrayBlockingQueue.this.removeAt(i); 
          } 
        } else if (i == -1) {
          throw new IllegalStateException();
        } 
        if (this.cursor < 0 && this.nextIndex < 0)
          detach(); 
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    void shutdown() {
      this.cursor = -1;
      if (this.nextIndex >= 0)
        this.nextIndex = -2; 
      if (this.lastRet >= 0) {
        this.lastRet = -2;
        this.lastItem = null;
      } 
      this.prevTakeIndex = -3;
    }
    
    private int distance(int param1Int1, int param1Int2, int param1Int3) {
      int i = param1Int1 - param1Int2;
      if (i < 0)
        i += param1Int3; 
      return i;
    }
    
    boolean removedAt(int param1Int) {
      if (isDetached())
        return true; 
      int i = ArrayBlockingQueue.this.itrs.cycles;
      int j = ArrayBlockingQueue.this.takeIndex;
      int k = this.prevCycles;
      int m = this.prevTakeIndex;
      int n = ArrayBlockingQueue.this.items.length;
      int i1 = i - k;
      if (param1Int < j)
        i1++; 
      int i2 = i1 * n + param1Int - m;
      int i3 = this.cursor;
      if (i3 >= 0) {
        int i6 = distance(i3, m, n);
        if (i6 == i2) {
          if (i3 == ArrayBlockingQueue.this.putIndex)
            this.cursor = i3 = -1; 
        } else if (i6 > i2) {
          this.cursor = i3 = ArrayBlockingQueue.this.dec(i3);
        } 
      } 
      int i4 = this.lastRet;
      if (i4 >= 0) {
        int i6 = distance(i4, m, n);
        if (i6 == i2) {
          this.lastRet = i4 = -2;
        } else if (i6 > i2) {
          this.lastRet = i4 = ArrayBlockingQueue.this.dec(i4);
        } 
      } 
      int i5 = this.nextIndex;
      if (i5 >= 0) {
        int i6 = distance(i5, m, n);
        if (i6 == i2) {
          this.nextIndex = i5 = -2;
        } else if (i6 > i2) {
          this.nextIndex = i5 = ArrayBlockingQueue.this.dec(i5);
        } 
      } else if (i3 < 0 && i5 < 0 && i4 < 0) {
        this.prevTakeIndex = -3;
        return true;
      } 
      return false;
    }
    
    boolean takeIndexWrapped() {
      if (isDetached())
        return true; 
      if (ArrayBlockingQueue.this.itrs.cycles - this.prevCycles > 1) {
        shutdown();
        return true;
      } 
      return false;
    }
  }
  
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(this, 4368);
  }
}
