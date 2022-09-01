package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.Spliterator;
import sun.misc.Unsafe;

public class ConcurrentLinkedQueue<E> extends AbstractQueue<E> implements Queue<E>, Serializable {
  private static final long serialVersionUID = 196745693267521676L;
  
  private volatile transient Node<E> head;
  
  private volatile transient Node<E> tail;
  
  private static final Unsafe UNSAFE;
  
  private static final long headOffset;
  
  private static final long tailOffset;
  
  static final class ConcurrentLinkedQueue {}
  
  private class ConcurrentLinkedQueue {}
  
  private static class Node<E> {
    volatile E item;
    
    volatile Node<E> next;
    
    private static final Unsafe UNSAFE;
    
    private static final long itemOffset;
    
    private static final long nextOffset;
    
    Node(E param1E) {
      UNSAFE.putObject(this, itemOffset, param1E);
    }
    
    boolean casItem(E param1E1, E param1E2) {
      return UNSAFE.compareAndSwapObject(this, itemOffset, param1E1, param1E2);
    }
    
    void lazySetNext(Node<E> param1Node) {
      UNSAFE.putOrderedObject(this, nextOffset, param1Node);
    }
    
    boolean casNext(Node<E> param1Node1, Node<E> param1Node2) {
      return UNSAFE.compareAndSwapObject(this, nextOffset, param1Node1, param1Node2);
    }
    
    static {
      try {
        UNSAFE = Unsafe.getUnsafe();
        Class<Node> clazz = Node.class;
        itemOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("item"));
        nextOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("next"));
      } catch (Exception exception) {
        throw new Error(exception);
      } 
    }
  }
  
  public ConcurrentLinkedQueue() {
    this.head = this.tail = new Node<>(null);
  }
  
  public ConcurrentLinkedQueue(Collection<? extends E> paramCollection) {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial <init> : ()V
    //   4: aconst_null
    //   5: astore_2
    //   6: aconst_null
    //   7: astore_3
    //   8: aload_1
    //   9: invokeinterface iterator : ()Ljava/util/Iterator;
    //   14: astore #4
    //   16: aload #4
    //   18: invokeinterface hasNext : ()Z
    //   23: ifeq -> 75
    //   26: aload #4
    //   28: invokeinterface next : ()Ljava/lang/Object;
    //   33: astore #5
    //   35: aload #5
    //   37: invokestatic checkNotNull : (Ljava/lang/Object;)V
    //   40: new java/util/concurrent/ConcurrentLinkedQueue$Node
    //   43: dup
    //   44: aload #5
    //   46: invokespecial <init> : (Ljava/lang/Object;)V
    //   49: astore #6
    //   51: aload_2
    //   52: ifnonnull -> 63
    //   55: aload #6
    //   57: dup
    //   58: astore_3
    //   59: astore_2
    //   60: goto -> 72
    //   63: aload_3
    //   64: aload #6
    //   66: invokevirtual lazySetNext : (Ljava/util/concurrent/ConcurrentLinkedQueue$Node;)V
    //   69: aload #6
    //   71: astore_3
    //   72: goto -> 16
    //   75: aload_2
    //   76: ifnonnull -> 90
    //   79: new java/util/concurrent/ConcurrentLinkedQueue$Node
    //   82: dup
    //   83: aconst_null
    //   84: invokespecial <init> : (Ljava/lang/Object;)V
    //   87: dup
    //   88: astore_3
    //   89: astore_2
    //   90: aload_0
    //   91: aload_2
    //   92: putfield head : Ljava/util/concurrent/ConcurrentLinkedQueue$Node;
    //   95: aload_0
    //   96: aload_3
    //   97: putfield tail : Ljava/util/concurrent/ConcurrentLinkedQueue$Node;
    //   100: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #268	-> 0
    //   #269	-> 4
    //   #270	-> 8
    //   #271	-> 35
    //   #272	-> 40
    //   #273	-> 51
    //   #274	-> 55
    //   #276	-> 63
    //   #277	-> 69
    //   #279	-> 72
    //   #280	-> 75
    //   #281	-> 79
    //   #282	-> 90
    //   #283	-> 95
    //   #284	-> 100
  }
  
  public boolean add(E paramE) {
    return offer(paramE);
  }
  
  final void updateHead(Node<E> paramNode1, Node<E> paramNode2) {
    if (paramNode1 != paramNode2 && casHead(paramNode1, paramNode2))
      paramNode1.lazySetNext(paramNode1); 
  }
  
  final Node<E> succ(Node<E> paramNode) {
    Node<E> node = paramNode.next;
    return (paramNode == node) ? this.head : node;
  }
  
  public boolean offer(E paramE) {
    checkNotNull(paramE);
    Node<E> node1 = new Node<>(paramE);
    Node<E> node2 = this.tail, node3 = node2;
    while (true) {
      Node<E> node = node3.next;
      if (node == null) {
        if (node3.casNext(null, node1)) {
          if (node3 != node2)
            casTail(node2, node1); 
          return true;
        } 
        continue;
      } 
      if (node3 == node) {
        node3 = (node2 != (node2 = this.tail)) ? node2 : this.head;
        continue;
      } 
      node3 = (node3 != node2 && node2 != (node2 = this.tail)) ? node2 : node;
    } 
  }
  
  public E poll() {
    label21: while (true) {
      Node<E> node1 = this.head, node2 = node1;
      while (true) {
        E e = node2.item;
        if (e != null && node2.casItem(e, null)) {
          if (node2 != node1) {
            Node<E> node3;
            updateHead(node1, ((node3 = node2.next) != null) ? node3 : node2);
          } 
          return e;
        } 
        Node<E> node;
        if ((node = node2.next) == null) {
          updateHead(node1, node2);
          return null;
        } 
        if (node2 == node)
          continue label21; 
        node2 = node;
      } 
      break;
    } 
  }
  
  public E peek() {
    label13: while (true) {
      Node<E> node1 = this.head, node2 = node1;
      while (true) {
        E e = node2.item;
        Node<E> node;
        if (e != null || (node = node2.next) == null) {
          updateHead(node1, node2);
          return e;
        } 
        if (node2 == node)
          continue label13; 
        node2 = node;
      } 
      break;
    } 
  }
  
  Node<E> first() {
    label20: while (true) {
      Node<E> node1 = this.head, node2 = node1;
      while (true) {
        boolean bool = (node2.item != null) ? true : false;
        Node<E> node;
        if (bool || (node = node2.next) == null) {
          updateHead(node1, node2);
          return bool ? node2 : null;
        } 
        if (node2 == node)
          continue label20; 
        node2 = node;
      } 
      break;
    } 
  }
  
  public boolean isEmpty() {
    return (first() == null);
  }
  
  public int size() {
    byte b = 0;
    for (Node<E> node = first(); node != null; node = succ(node)) {
      if (node.item != null)
        if (++b == Integer.MAX_VALUE)
          break;  
    } 
    return b;
  }
  
  public boolean contains(Object paramObject) {
    if (paramObject == null)
      return false; 
    for (Node<E> node = first(); node != null; node = succ(node)) {
      E e = node.item;
      if (e != null && paramObject.equals(e))
        return true; 
    } 
    return false;
  }
  
  public boolean remove(Object paramObject) {
    if (paramObject == null)
      return false; 
    Node<E> node1 = null;
    for (Node<E> node2 = first(); node2 != null; node2 = succ(node2)) {
      E e = node2.item;
      if (e != null && paramObject
        .equals(e) && node2
        .casItem(e, null)) {
        Node<E> node = succ(node2);
        if (node1 != null && node != null)
          node1.casNext(node2, node); 
        return true;
      } 
      node1 = node2;
    } 
    return false;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection) {
    // Byte code:
    //   0: aload_1
    //   1: aload_0
    //   2: if_acmpne -> 13
    //   5: new java/lang/IllegalArgumentException
    //   8: dup
    //   9: invokespecial <init> : ()V
    //   12: athrow
    //   13: aconst_null
    //   14: astore_2
    //   15: aconst_null
    //   16: astore_3
    //   17: aload_1
    //   18: invokeinterface iterator : ()Ljava/util/Iterator;
    //   23: astore #4
    //   25: aload #4
    //   27: invokeinterface hasNext : ()Z
    //   32: ifeq -> 84
    //   35: aload #4
    //   37: invokeinterface next : ()Ljava/lang/Object;
    //   42: astore #5
    //   44: aload #5
    //   46: invokestatic checkNotNull : (Ljava/lang/Object;)V
    //   49: new java/util/concurrent/ConcurrentLinkedQueue$Node
    //   52: dup
    //   53: aload #5
    //   55: invokespecial <init> : (Ljava/lang/Object;)V
    //   58: astore #6
    //   60: aload_2
    //   61: ifnonnull -> 72
    //   64: aload #6
    //   66: dup
    //   67: astore_3
    //   68: astore_2
    //   69: goto -> 81
    //   72: aload_3
    //   73: aload #6
    //   75: invokevirtual lazySetNext : (Ljava/util/concurrent/ConcurrentLinkedQueue$Node;)V
    //   78: aload #6
    //   80: astore_3
    //   81: goto -> 25
    //   84: aload_2
    //   85: ifnonnull -> 90
    //   88: iconst_0
    //   89: ireturn
    //   90: aload_0
    //   91: getfield tail : Ljava/util/concurrent/ConcurrentLinkedQueue$Node;
    //   94: astore #4
    //   96: aload #4
    //   98: astore #5
    //   100: aload #5
    //   102: getfield next : Ljava/util/concurrent/ConcurrentLinkedQueue$Node;
    //   105: astore #6
    //   107: aload #6
    //   109: ifnonnull -> 155
    //   112: aload #5
    //   114: aconst_null
    //   115: aload_2
    //   116: invokevirtual casNext : (Ljava/util/concurrent/ConcurrentLinkedQueue$Node;Ljava/util/concurrent/ConcurrentLinkedQueue$Node;)Z
    //   119: ifeq -> 216
    //   122: aload_0
    //   123: aload #4
    //   125: aload_3
    //   126: invokespecial casTail : (Ljava/util/concurrent/ConcurrentLinkedQueue$Node;Ljava/util/concurrent/ConcurrentLinkedQueue$Node;)Z
    //   129: ifne -> 153
    //   132: aload_0
    //   133: getfield tail : Ljava/util/concurrent/ConcurrentLinkedQueue$Node;
    //   136: astore #4
    //   138: aload_3
    //   139: getfield next : Ljava/util/concurrent/ConcurrentLinkedQueue$Node;
    //   142: ifnonnull -> 153
    //   145: aload_0
    //   146: aload #4
    //   148: aload_3
    //   149: invokespecial casTail : (Ljava/util/concurrent/ConcurrentLinkedQueue$Node;Ljava/util/concurrent/ConcurrentLinkedQueue$Node;)Z
    //   152: pop
    //   153: iconst_1
    //   154: ireturn
    //   155: aload #5
    //   157: aload #6
    //   159: if_acmpne -> 188
    //   162: aload #4
    //   164: aload_0
    //   165: getfield tail : Ljava/util/concurrent/ConcurrentLinkedQueue$Node;
    //   168: dup
    //   169: astore #4
    //   171: if_acmpeq -> 179
    //   174: aload #4
    //   176: goto -> 183
    //   179: aload_0
    //   180: getfield head : Ljava/util/concurrent/ConcurrentLinkedQueue$Node;
    //   183: astore #5
    //   185: goto -> 216
    //   188: aload #5
    //   190: aload #4
    //   192: if_acmpeq -> 212
    //   195: aload #4
    //   197: aload_0
    //   198: getfield tail : Ljava/util/concurrent/ConcurrentLinkedQueue$Node;
    //   201: dup
    //   202: astore #4
    //   204: if_acmpeq -> 212
    //   207: aload #4
    //   209: goto -> 214
    //   212: aload #6
    //   214: astore #5
    //   216: goto -> 100
    // Line number table:
    //   Java source line number -> byte code offset
    //   #518	-> 0
    //   #520	-> 5
    //   #523	-> 13
    //   #524	-> 17
    //   #525	-> 44
    //   #526	-> 49
    //   #527	-> 60
    //   #528	-> 64
    //   #530	-> 72
    //   #531	-> 78
    //   #533	-> 81
    //   #534	-> 84
    //   #535	-> 88
    //   #538	-> 90
    //   #539	-> 100
    //   #540	-> 107
    //   #542	-> 112
    //   #545	-> 122
    //   #548	-> 132
    //   #549	-> 138
    //   #550	-> 145
    //   #552	-> 153
    //   #556	-> 155
    //   #561	-> 162
    //   #564	-> 188
    //   #565	-> 216
  }
  
  public Object[] toArray() {
    ArrayList<E> arrayList = new ArrayList();
    for (Node<E> node = first(); node != null; node = succ(node)) {
      E e = node.item;
      if (e != null)
        arrayList.add(e); 
    } 
    return arrayList.toArray();
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    byte b = 0;
    Node<E> node1;
    for (node1 = first(); node1 != null && b < paramArrayOfT.length; node1 = succ(node1)) {
      E e = node1.item;
      if (e != null)
        paramArrayOfT[b++] = (T)e; 
    } 
    if (node1 == null) {
      if (b < paramArrayOfT.length)
        paramArrayOfT[b] = null; 
      return paramArrayOfT;
    } 
    ArrayList<E> arrayList = new ArrayList();
    for (Node<E> node2 = first(); node2 != null; node2 = succ(node2)) {
      E e = node2.item;
      if (e != null)
        arrayList.add(e); 
    } 
    return arrayList.toArray(paramArrayOfT);
  }
  
  public Iterator<E> iterator() {
    return new Itr(this);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    for (Node<E> node = first(); node != null; node = succ(node)) {
      E e = node.item;
      if (e != null)
        paramObjectOutputStream.writeObject(e); 
    } 
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    Node<E> node1 = null, node2 = null;
    Object object;
    while ((object = paramObjectInputStream.readObject()) != null) {
      Node<E> node = new Node(object);
      if (node1 == null) {
        node1 = node2 = node;
        continue;
      } 
      node2.lazySetNext(node);
      node2 = node;
    } 
    if (node1 == null)
      node1 = node2 = new Node(null); 
    this.head = node1;
    this.tail = node2;
  }
  
  public Spliterator<E> spliterator() {
    return new CLQSpliterator(this);
  }
  
  private static void checkNotNull(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
  }
  
  private boolean casTail(Node<E> paramNode1, Node<E> paramNode2) {
    return UNSAFE.compareAndSwapObject(this, tailOffset, paramNode1, paramNode2);
  }
  
  private boolean casHead(Node<E> paramNode1, Node<E> paramNode2) {
    return UNSAFE.compareAndSwapObject(this, headOffset, paramNode1, paramNode2);
  }
  
  static {
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class<ConcurrentLinkedQueue> clazz = ConcurrentLinkedQueue.class;
      headOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("head"));
      tailOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("tail"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
}
