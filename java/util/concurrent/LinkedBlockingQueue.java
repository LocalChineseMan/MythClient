package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
  private static final long serialVersionUID = -6903933977591709194L;
  
  private final int capacity;
  
  static class Node<E> {
    E item;
    
    Node<E> next;
    
    Node(E param1E) {
      this.item = param1E;
    }
  }
  
  private final AtomicInteger count = new AtomicInteger();
  
  transient Node<E> head;
  
  private transient Node<E> last;
  
  private final ReentrantLock takeLock = new ReentrantLock();
  
  private final Condition notEmpty = this.takeLock.newCondition();
  
  private final ReentrantLock putLock = new ReentrantLock();
  
  private final Condition notFull = this.putLock.newCondition();
  
  private void signalNotEmpty() {
    ReentrantLock reentrantLock = this.takeLock;
    reentrantLock.lock();
    try {
      this.notEmpty.signal();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  private void signalNotFull() {
    ReentrantLock reentrantLock = this.putLock;
    reentrantLock.lock();
    try {
      this.notFull.signal();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  private void enqueue(Node<E> paramNode) {
    this.last = this.last.next = paramNode;
  }
  
  private E dequeue() {
    Node<E> node1 = this.head;
    Node<E> node2 = node1.next;
    node1.next = node1;
    this.head = node2;
    E e = node2.item;
    node2.item = null;
    return e;
  }
  
  void fullyLock() {
    this.putLock.lock();
    this.takeLock.lock();
  }
  
  void fullyUnlock() {
    this.takeLock.unlock();
    this.putLock.unlock();
  }
  
  public LinkedBlockingQueue() {
    this(2147483647);
  }
  
  public LinkedBlockingQueue(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException(); 
    this.capacity = paramInt;
    this.last = this.head = new Node<>(null);
  }
  
  public LinkedBlockingQueue(Collection<? extends E> paramCollection) {
    // Byte code:
    //   0: aload_0
    //   1: ldc 2147483647
    //   3: invokespecial <init> : (I)V
    //   6: aload_0
    //   7: getfield putLock : Ljava/util/concurrent/locks/ReentrantLock;
    //   10: astore_2
    //   11: aload_2
    //   12: invokevirtual lock : ()V
    //   15: iconst_0
    //   16: istore_3
    //   17: aload_1
    //   18: invokeinterface iterator : ()Ljava/util/Iterator;
    //   23: astore #4
    //   25: aload #4
    //   27: invokeinterface hasNext : ()Z
    //   32: ifeq -> 94
    //   35: aload #4
    //   37: invokeinterface next : ()Ljava/lang/Object;
    //   42: astore #5
    //   44: aload #5
    //   46: ifnonnull -> 57
    //   49: new java/lang/NullPointerException
    //   52: dup
    //   53: invokespecial <init> : ()V
    //   56: athrow
    //   57: iload_3
    //   58: aload_0
    //   59: getfield capacity : I
    //   62: if_icmpne -> 75
    //   65: new java/lang/IllegalStateException
    //   68: dup
    //   69: ldc 'Queue full'
    //   71: invokespecial <init> : (Ljava/lang/String;)V
    //   74: athrow
    //   75: aload_0
    //   76: new java/util/concurrent/LinkedBlockingQueue$Node
    //   79: dup
    //   80: aload #5
    //   82: invokespecial <init> : (Ljava/lang/Object;)V
    //   85: invokespecial enqueue : (Ljava/util/concurrent/LinkedBlockingQueue$Node;)V
    //   88: iinc #3, 1
    //   91: goto -> 25
    //   94: aload_0
    //   95: getfield count : Ljava/util/concurrent/atomic/AtomicInteger;
    //   98: iload_3
    //   99: invokevirtual set : (I)V
    //   102: aload_2
    //   103: invokevirtual unlock : ()V
    //   106: goto -> 118
    //   109: astore #6
    //   111: aload_2
    //   112: invokevirtual unlock : ()V
    //   115: aload #6
    //   117: athrow
    //   118: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #277	-> 0
    //   #278	-> 6
    //   #279	-> 11
    //   #281	-> 15
    //   #282	-> 17
    //   #283	-> 44
    //   #284	-> 49
    //   #285	-> 57
    //   #286	-> 65
    //   #287	-> 75
    //   #288	-> 88
    //   #289	-> 91
    //   #290	-> 94
    //   #292	-> 102
    //   #293	-> 106
    //   #292	-> 109
    //   #294	-> 118
    // Exception table:
    //   from	to	target	type
    //   15	102	109	finally
    //   109	111	109	finally
  }
  
  public int size() {
    return this.count.get();
  }
  
  public int remainingCapacity() {
    return this.capacity - this.count.get();
  }
  
  public void put(E paramE) throws InterruptedException {
    if (paramE == null)
      throw new NullPointerException(); 
    int i = -1;
    Node<E> node = new Node<>(paramE);
    ReentrantLock reentrantLock = this.putLock;
    AtomicInteger atomicInteger = this.count;
    reentrantLock.lockInterruptibly();
    try {
      while (atomicInteger.get() == this.capacity)
        this.notFull.await(); 
      enqueue(node);
      i = atomicInteger.getAndIncrement();
      if (i + 1 < this.capacity)
        this.notFull.signal(); 
    } finally {
      reentrantLock.unlock();
    } 
    if (i == 0)
      signalNotEmpty(); 
  }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    if (paramE == null)
      throw new NullPointerException(); 
    long l = paramTimeUnit.toNanos(paramLong);
    int i = -1;
    ReentrantLock reentrantLock = this.putLock;
    AtomicInteger atomicInteger = this.count;
    reentrantLock.lockInterruptibly();
    try {
      while (atomicInteger.get() == this.capacity) {
        if (l <= 0L)
          return false; 
        l = this.notFull.awaitNanos(l);
      } 
      enqueue(new Node<>(paramE));
      i = atomicInteger.getAndIncrement();
      if (i + 1 < this.capacity)
        this.notFull.signal(); 
    } finally {
      reentrantLock.unlock();
    } 
    if (i == 0)
      signalNotEmpty(); 
    return true;
  }
  
  public boolean offer(E paramE) {
    if (paramE == null)
      throw new NullPointerException(); 
    AtomicInteger atomicInteger = this.count;
    if (atomicInteger.get() == this.capacity)
      return false; 
    int i = -1;
    Node<E> node = new Node<>(paramE);
    ReentrantLock reentrantLock = this.putLock;
    reentrantLock.lock();
    try {
      if (atomicInteger.get() < this.capacity) {
        enqueue(node);
        i = atomicInteger.getAndIncrement();
        if (i + 1 < this.capacity)
          this.notFull.signal(); 
      } 
    } finally {
      reentrantLock.unlock();
    } 
    if (i == 0)
      signalNotEmpty(); 
    return (i >= 0);
  }
  
  public E take() throws InterruptedException {
    E e;
    int i = -1;
    AtomicInteger atomicInteger = this.count;
    ReentrantLock reentrantLock = this.takeLock;
    reentrantLock.lockInterruptibly();
    try {
      while (atomicInteger.get() == 0)
        this.notEmpty.await(); 
      e = dequeue();
      i = atomicInteger.getAndDecrement();
      if (i > 1)
        this.notEmpty.signal(); 
    } finally {
      reentrantLock.unlock();
    } 
    if (i == this.capacity)
      signalNotFull(); 
    return e;
  }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    E e = null;
    int i = -1;
    long l = paramTimeUnit.toNanos(paramLong);
    AtomicInteger atomicInteger = this.count;
    ReentrantLock reentrantLock = this.takeLock;
    reentrantLock.lockInterruptibly();
    try {
      while (atomicInteger.get() == 0) {
        if (l <= 0L)
          return null; 
        l = this.notEmpty.awaitNanos(l);
      } 
      e = dequeue();
      i = atomicInteger.getAndDecrement();
      if (i > 1)
        this.notEmpty.signal(); 
    } finally {
      reentrantLock.unlock();
    } 
    if (i == this.capacity)
      signalNotFull(); 
    return e;
  }
  
  public E poll() {
    AtomicInteger atomicInteger = this.count;
    if (atomicInteger.get() == 0)
      return null; 
    E e = null;
    int i = -1;
    ReentrantLock reentrantLock = this.takeLock;
    reentrantLock.lock();
    try {
      if (atomicInteger.get() > 0) {
        e = dequeue();
        i = atomicInteger.getAndDecrement();
        if (i > 1)
          this.notEmpty.signal(); 
      } 
    } finally {
      reentrantLock.unlock();
    } 
    if (i == this.capacity)
      signalNotFull(); 
    return e;
  }
  
  public E peek() {
    if (this.count.get() == 0)
      return null; 
    ReentrantLock reentrantLock = this.takeLock;
    reentrantLock.lock();
    try {
      Node<E> node = this.head.next;
      if (node == null)
        return null; 
      return node.item;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  void unlink(Node<E> paramNode1, Node<E> paramNode2) {
    paramNode1.item = null;
    paramNode2.next = paramNode1.next;
    if (this.last == paramNode1)
      this.last = paramNode2; 
    if (this.count.getAndDecrement() == this.capacity)
      this.notFull.signal(); 
  }
  
  public boolean remove(Object paramObject) {
    if (paramObject == null)
      return false; 
    fullyLock();
    try {
      Node<E> node1 = this.head, node2 = node1.next;
      for (; node2 != null; 
        node1 = node2, node2 = node2.next) {
        if (paramObject.equals(node2.item)) {
          unlink(node2, node1);
          return true;
        } 
      } 
      return false;
    } finally {
      fullyUnlock();
    } 
  }
  
  public boolean contains(Object paramObject) {
    if (paramObject == null)
      return false; 
    fullyLock();
    try {
      for (Node<E> node = this.head.next; node != null; node = node.next) {
        if (paramObject.equals(node.item))
          return true; 
      } 
      return false;
    } finally {
      fullyUnlock();
    } 
  }
  
  public Object[] toArray() {
    fullyLock();
    try {
      int i = this.count.get();
      Object[] arrayOfObject = new Object[i];
      byte b = 0;
      for (Node<E> node = this.head.next; node != null; node = node.next)
        arrayOfObject[b++] = node.item; 
      return arrayOfObject;
    } finally {
      fullyUnlock();
    } 
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    fullyLock();
    try {
      int i = this.count.get();
      if (paramArrayOfT.length < i)
        paramArrayOfT = (T[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i); 
      byte b = 0;
      for (Node<E> node = this.head.next; node != null; node = node.next)
        paramArrayOfT[b++] = (T)node.item; 
      if (paramArrayOfT.length > b)
        paramArrayOfT[b] = null; 
      return paramArrayOfT;
    } finally {
      fullyUnlock();
    } 
  }
  
  public String toString() {
    fullyLock();
    try {
      Node<E> node = this.head.next;
      if (node == null)
        return "[]"; 
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append('[');
      while (true) {
        E e = node.item;
        stringBuilder.append((e == this) ? "(this Collection)" : e);
        node = node.next;
        if (node == null)
          return stringBuilder.append(']').toString(); 
        stringBuilder.append(',').append(' ');
      } 
    } finally {
      fullyUnlock();
    } 
  }
  
  public void clear() {
    fullyLock();
    try {
      Node<E> node1;
      for (Node<E> node2 = this.head; (node1 = node2.next) != null; node2 = node1) {
        node2.next = node2;
        node1.item = null;
      } 
      this.head = this.last;
      if (this.count.getAndSet(0) == this.capacity)
        this.notFull.signal(); 
    } finally {
      fullyUnlock();
    } 
  }
  
  public int drainTo(Collection<? super E> paramCollection) {
    return drainTo(paramCollection, 2147483647);
  }
  
  public int drainTo(Collection<? super E> paramCollection, int paramInt) {
    if (paramCollection == null)
      throw new NullPointerException(); 
    if (paramCollection == this)
      throw new IllegalArgumentException(); 
    if (paramInt <= 0)
      return 0; 
    boolean bool = false;
    ReentrantLock reentrantLock = this.takeLock;
    reentrantLock.lock();
    try {
      int i = Math.min(paramInt, this.count.get());
      Node<E> node = this.head;
    } finally {
      reentrantLock.unlock();
      if (bool)
        signalNotFull(); 
    } 
  }
  
  public Iterator<E> iterator() {
    return new Itr();
  }
  
  private class Itr implements Iterator<E> {
    private LinkedBlockingQueue.Node<E> current;
    
    private LinkedBlockingQueue.Node<E> lastRet;
    
    private E currentElement;
    
    Itr() {
      LinkedBlockingQueue.this.fullyLock();
      try {
        this.current = LinkedBlockingQueue.this.head.next;
        if (this.current != null)
          this.currentElement = this.current.item; 
      } finally {
        LinkedBlockingQueue.this.fullyUnlock();
      } 
    }
    
    public boolean hasNext() {
      return (this.current != null);
    }
    
    private LinkedBlockingQueue.Node<E> nextNode(LinkedBlockingQueue.Node<E> param1Node) {
      while (true) {
        LinkedBlockingQueue.Node<E> node = param1Node.next;
        if (node == param1Node)
          return LinkedBlockingQueue.this.head.next; 
        if (node == null || node.item != null)
          return node; 
        param1Node = node;
      } 
    }
    
    public E next() {
      LinkedBlockingQueue.this.fullyLock();
      try {
        if (this.current == null)
          throw new NoSuchElementException(); 
        E e = this.currentElement;
        this.lastRet = this.current;
        this.current = nextNode(this.current);
        this.currentElement = (this.current == null) ? null : this.current.item;
        return e;
      } finally {
        LinkedBlockingQueue.this.fullyUnlock();
      } 
    }
    
    public void remove() {
      if (this.lastRet == null)
        throw new IllegalStateException(); 
      LinkedBlockingQueue.this.fullyLock();
      try {
        LinkedBlockingQueue.Node<E> node1 = this.lastRet;
        this.lastRet = null;
        LinkedBlockingQueue.Node<E> node2 = LinkedBlockingQueue.this.head, node3 = node2.next;
        for (; node3 != null; 
          node2 = node3, node3 = node3.next) {
          if (node3 == node1) {
            LinkedBlockingQueue.this.unlink(node3, node2);
            break;
          } 
        } 
      } finally {
        LinkedBlockingQueue.this.fullyUnlock();
      } 
    }
  }
  
  public Spliterator<E> spliterator() {
    return new LBQSpliterator(this);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    fullyLock();
    try {
      paramObjectOutputStream.defaultWriteObject();
      for (Node<E> node = this.head.next; node != null; node = node.next)
        paramObjectOutputStream.writeObject(node.item); 
      paramObjectOutputStream.writeObject(null);
    } finally {
      fullyUnlock();
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.count.set(0);
    this.last = this.head = new Node<>(null);
    while (true) {
      Object object = paramObjectInputStream.readObject();
      if (object == null)
        break; 
      add(object);
    } 
  }
  
  static final class LinkedBlockingQueue {}
}
