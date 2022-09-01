package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import sun.misc.Unsafe;

public class ConcurrentLinkedDeque<E> extends AbstractCollection<E> implements Deque<E>, Serializable {
  private static final long serialVersionUID = 876323262645176354L;
  
  private volatile transient Node<E> head;
  
  private volatile transient Node<E> tail;
  
  Node<E> prevTerminator() {
    return (Node)PREV_TERMINATOR;
  }
  
  Node<E> nextTerminator() {
    return (Node)NEXT_TERMINATOR;
  }
  
  static final class ConcurrentLinkedDeque {}
  
  private class ConcurrentLinkedDeque {}
  
  private class ConcurrentLinkedDeque {}
  
  private abstract class ConcurrentLinkedDeque {}
  
  static final class Node<E> {
    volatile Node<E> prev;
    
    volatile E item;
    
    volatile Node<E> next;
    
    private static final Unsafe UNSAFE;
    
    private static final long prevOffset;
    
    private static final long itemOffset;
    
    private static final long nextOffset;
    
    Node() {}
    
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
    
    void lazySetPrev(Node<E> param1Node) {
      UNSAFE.putOrderedObject(this, prevOffset, param1Node);
    }
    
    boolean casPrev(Node<E> param1Node1, Node<E> param1Node2) {
      return UNSAFE.compareAndSwapObject(this, prevOffset, param1Node1, param1Node2);
    }
    
    static {
      try {
        UNSAFE = Unsafe.getUnsafe();
        Class<Node> clazz = Node.class;
        prevOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("prev"));
        itemOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("item"));
        nextOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("next"));
      } catch (Exception exception) {
        throw new Error(exception);
      } 
    }
  }
  
  private void linkFirst(E paramE) {
    Node<E> node2, node3;
    checkNotNull(paramE);
    Node<E> node1 = new Node<>(paramE);
    label22: while (true) {
      node3 = node2 = this.head;
      while (true) {
        Node<E> node;
        if ((node = node3.prev) != null && (node = (node3 = node).prev) != null) {
          node3 = (node2 != (node2 = this.head)) ? node2 : node;
          continue;
        } 
        if (node3.next == node3)
          continue label22; 
        node1.lazySetNext(node3);
        if (node3.casPrev(null, node1))
          break; 
      } 
      break;
    } 
    if (node3 != node2)
      casHead(node2, node1); 
  }
  
  private void linkLast(E paramE) {
    Node<E> node2, node3;
    checkNotNull(paramE);
    Node<E> node1 = new Node<>(paramE);
    label22: while (true) {
      node3 = node2 = this.tail;
      while (true) {
        Node<E> node;
        if ((node = node3.next) != null && (node = (node3 = node).next) != null) {
          node3 = (node2 != (node2 = this.tail)) ? node2 : node;
          continue;
        } 
        if (node3.prev == node3)
          continue label22; 
        node1.lazySetPrev(node3);
        if (node3.casNext(null, node1))
          break; 
      } 
      break;
    } 
    if (node3 != node2)
      casTail(node2, node1); 
  }
  
  void unlink(Node<E> paramNode) {
    Node<E> node1 = paramNode.prev;
    Node<E> node2 = paramNode.next;
    if (node1 == null) {
      unlinkFirst(paramNode, node2);
    } else if (node2 == null) {
      unlinkLast(paramNode, node1);
    } else {
      Node<E> node3, node4;
      boolean bool1, bool2;
      byte b = 1;
      Node<E> node5;
      for (node5 = node1;; b++) {
        if (node5.item != null) {
          node3 = node5;
          bool1 = false;
          break;
        } 
        Node<E> node = node5.prev;
        if (node == null) {
          if (node5.next == node5)
            return; 
          node3 = node5;
          bool1 = true;
          break;
        } 
        if (node5 == node)
          return; 
        node5 = node;
      } 
      for (node5 = node2;; b++) {
        if (node5.item != null) {
          node4 = node5;
          bool2 = false;
          break;
        } 
        Node<E> node = node5.next;
        if (node == null) {
          if (node5.prev == node5)
            return; 
          node4 = node5;
          bool2 = true;
          break;
        } 
        if (node5 == node)
          return; 
        node5 = node;
      } 
      if (b < 2 && (bool1 | bool2) != 0)
        return; 
      skipDeletedSuccessors(node3);
      skipDeletedPredecessors(node4);
      if ((bool1 | bool2) != 0 && node3.next == node4 && node4.prev == node3 && (bool1 ? (node3.prev == null) : (node3.item != null)) && (bool2 ? (node4.next == null) : (node4.item != null))) {
        updateHead();
        updateTail();
        paramNode.lazySetPrev(bool1 ? prevTerminator() : paramNode);
        paramNode.lazySetNext(bool2 ? nextTerminator() : paramNode);
      } 
    } 
  }
  
  private void unlinkFirst(Node<E> paramNode1, Node<E> paramNode2) {
    Node<E> node1 = null, node2 = paramNode2;
    while (true) {
      Node<E> node;
      if (node2.item != null || (node = node2.next) == null) {
        if (node1 != null && node2.prev != node2 && paramNode1.casNext(paramNode2, node2)) {
          skipDeletedPredecessors(node2);
          if (paramNode1.prev == null && (node2.next == null || node2.item != null) && node2.prev == paramNode1) {
            updateHead();
            updateTail();
            node1.lazySetNext(node1);
            node1.lazySetPrev(prevTerminator());
          } 
        } 
        return;
      } 
      if (node2 == node)
        return; 
      node1 = node2;
      node2 = node;
    } 
  }
  
  private void unlinkLast(Node<E> paramNode1, Node<E> paramNode2) {
    Node<E> node1 = null, node2 = paramNode2;
    while (true) {
      Node<E> node;
      if (node2.item != null || (node = node2.prev) == null) {
        if (node1 != null && node2.next != node2 && paramNode1.casPrev(paramNode2, node2)) {
          skipDeletedSuccessors(node2);
          if (paramNode1.next == null && (node2.prev == null || node2.item != null) && node2.next == paramNode1) {
            updateHead();
            updateTail();
            node1.lazySetPrev(node1);
            node1.lazySetNext(nextTerminator());
          } 
        } 
        return;
      } 
      if (node2 == node)
        return; 
      node1 = node2;
      node2 = node;
    } 
  }
  
  private final void updateHead() {
    Node<E> node1;
    Node<E> node2;
    label19: while ((node1 = this.head).item == null && (node2 = node1.prev) != null) {
      while (true) {
        Node<E> node;
        if ((node = node2.prev) == null || (node = (node2 = node).prev) == null) {
          if (casHead(node1, node2))
            return; 
          continue label19;
        } 
        if (node1 != this.head)
          continue label19; 
        node2 = node;
      } 
    } 
  }
  
  private final void updateTail() {
    Node<E> node1;
    Node<E> node2;
    label19: while ((node1 = this.tail).item == null && (node2 = node1.next) != null) {
      while (true) {
        Node<E> node;
        if ((node = node2.next) == null || (node = (node2 = node).next) == null) {
          if (casTail(node1, node2))
            return; 
          continue label19;
        } 
        if (node1 != this.tail)
          continue label19; 
        node2 = node;
      } 
    } 
  }
  
  private void skipDeletedPredecessors(Node<E> paramNode) {
    label21: do {
      Node<E> node1 = paramNode.prev;
      Node<E> node2 = node1;
      while (node2.item == null) {
        Node<E> node = node2.prev;
        if (node == null) {
          if (node2.next == node2)
            continue label21; 
          break;
        } 
        if (node2 == node)
          continue label21; 
        node2 = node;
      } 
      if (node1 == node2 || paramNode.casPrev(node1, node2))
        return; 
    } while (paramNode.item != null || paramNode.next == null);
  }
  
  private void skipDeletedSuccessors(Node<E> paramNode) {
    label21: do {
      Node<E> node1 = paramNode.next;
      Node<E> node2 = node1;
      while (node2.item == null) {
        Node<E> node = node2.next;
        if (node == null) {
          if (node2.prev == node2)
            continue label21; 
          break;
        } 
        if (node2 == node)
          continue label21; 
        node2 = node;
      } 
      if (node1 == node2 || paramNode.casNext(node1, node2))
        return; 
    } while (paramNode.item != null || paramNode.prev == null);
  }
  
  final Node<E> succ(Node<E> paramNode) {
    Node<E> node = paramNode.next;
    return (paramNode == node) ? first() : node;
  }
  
  final Node<E> pred(Node<E> paramNode) {
    Node<E> node = paramNode.prev;
    return (paramNode == node) ? last() : node;
  }
  
  Node<E> first() {
    Node<E> node1;
    Node<E> node2;
    do {
      node2 = node1 = this.head;
      Node<E> node;
      while ((node = node2.prev) != null && (node = (node2 = node).prev) != null)
        node2 = (node1 != (node1 = this.head)) ? node1 : node; 
    } while (node2 != node1 && 
      
      !casHead(node1, node2));
    return node2;
  }
  
  Node<E> last() {
    Node<E> node1;
    Node<E> node2;
    do {
      node2 = node1 = this.tail;
      Node<E> node;
      while ((node = node2.next) != null && (node = (node2 = node).next) != null)
        node2 = (node1 != (node1 = this.tail)) ? node1 : node; 
    } while (node2 != node1 && 
      
      !casTail(node1, node2));
    return node2;
  }
  
  private static void checkNotNull(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
  }
  
  private E screenNullResult(E paramE) {
    if (paramE == null)
      throw new NoSuchElementException(); 
    return paramE;
  }
  
  private ArrayList<E> toArrayList() {
    ArrayList<E> arrayList = new ArrayList();
    for (Node<E> node = first(); node != null; node = succ(node)) {
      E e = node.item;
      if (e != null)
        arrayList.add(e); 
    } 
    return arrayList;
  }
  
  public ConcurrentLinkedDeque() {
    this.head = this.tail = new Node<>(null);
  }
  
  public ConcurrentLinkedDeque(Collection<? extends E> paramCollection) {
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
    //   23: ifeq -> 81
    //   26: aload #4
    //   28: invokeinterface next : ()Ljava/lang/Object;
    //   33: astore #5
    //   35: aload #5
    //   37: invokestatic checkNotNull : (Ljava/lang/Object;)V
    //   40: new java/util/concurrent/ConcurrentLinkedDeque$Node
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
    //   60: goto -> 78
    //   63: aload_3
    //   64: aload #6
    //   66: invokevirtual lazySetNext : (Ljava/util/concurrent/ConcurrentLinkedDeque$Node;)V
    //   69: aload #6
    //   71: aload_3
    //   72: invokevirtual lazySetPrev : (Ljava/util/concurrent/ConcurrentLinkedDeque$Node;)V
    //   75: aload #6
    //   77: astore_3
    //   78: goto -> 16
    //   81: aload_0
    //   82: aload_2
    //   83: aload_3
    //   84: invokespecial initHeadTail : (Ljava/util/concurrent/ConcurrentLinkedDeque$Node;Ljava/util/concurrent/ConcurrentLinkedDeque$Node;)V
    //   87: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #846	-> 0
    //   #848	-> 4
    //   #849	-> 8
    //   #850	-> 35
    //   #851	-> 40
    //   #852	-> 51
    //   #853	-> 55
    //   #855	-> 63
    //   #856	-> 69
    //   #857	-> 75
    //   #859	-> 78
    //   #860	-> 81
    //   #861	-> 87
  }
  
  private void initHeadTail(Node<E> paramNode1, Node<E> paramNode2) {
    if (paramNode1 == paramNode2)
      if (paramNode1 == null) {
        paramNode1 = paramNode2 = new Node<>(null);
      } else {
        Node<E> node = new Node(null);
        paramNode2.lazySetNext(node);
        node.lazySetPrev(paramNode2);
        paramNode2 = node;
      }  
    this.head = paramNode1;
    this.tail = paramNode2;
  }
  
  public void addFirst(E paramE) {
    linkFirst(paramE);
  }
  
  public void addLast(E paramE) {
    linkLast(paramE);
  }
  
  public boolean offerFirst(E paramE) {
    linkFirst(paramE);
    return true;
  }
  
  public boolean offerLast(E paramE) {
    linkLast(paramE);
    return true;
  }
  
  public E peekFirst() {
    for (Node<E> node = first(); node != null; node = succ(node)) {
      E e = node.item;
      if (e != null)
        return e; 
    } 
    return null;
  }
  
  public E peekLast() {
    for (Node<E> node = last(); node != null; node = pred(node)) {
      E e = node.item;
      if (e != null)
        return e; 
    } 
    return null;
  }
  
  public E getFirst() {
    return screenNullResult(peekFirst());
  }
  
  public E getLast() {
    return screenNullResult(peekLast());
  }
  
  public E pollFirst() {
    for (Node<E> node = first(); node != null; node = succ(node)) {
      E e = node.item;
      if (e != null && node.casItem(e, null)) {
        unlink(node);
        return e;
      } 
    } 
    return null;
  }
  
  public E pollLast() {
    for (Node<E> node = last(); node != null; node = pred(node)) {
      E e = node.item;
      if (e != null && node.casItem(e, null)) {
        unlink(node);
        return e;
      } 
    } 
    return null;
  }
  
  public E removeFirst() {
    return screenNullResult(pollFirst());
  }
  
  public E removeLast() {
    return screenNullResult(pollLast());
  }
  
  public boolean offer(E paramE) {
    return offerLast(paramE);
  }
  
  public boolean add(E paramE) {
    return offerLast(paramE);
  }
  
  public E poll() {
    return pollFirst();
  }
  
  public E peek() {
    return peekFirst();
  }
  
  public E remove() {
    return removeFirst();
  }
  
  public E pop() {
    return removeFirst();
  }
  
  public E element() {
    return getFirst();
  }
  
  public void push(E paramE) {
    addFirst(paramE);
  }
  
  public boolean removeFirstOccurrence(Object paramObject) {
    checkNotNull(paramObject);
    for (Node<E> node = first(); node != null; node = succ(node)) {
      E e = node.item;
      if (e != null && paramObject.equals(e) && node.casItem(e, null)) {
        unlink(node);
        return true;
      } 
    } 
    return false;
  }
  
  public boolean removeLastOccurrence(Object paramObject) {
    checkNotNull(paramObject);
    for (Node<E> node = last(); node != null; node = pred(node)) {
      E e = node.item;
      if (e != null && paramObject.equals(e) && node.casItem(e, null)) {
        unlink(node);
        return true;
      } 
    } 
    return false;
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
  
  public boolean isEmpty() {
    return (peekFirst() == null);
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
  
  public boolean remove(Object paramObject) {
    return removeFirstOccurrence(paramObject);
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
    //   32: ifeq -> 90
    //   35: aload #4
    //   37: invokeinterface next : ()Ljava/lang/Object;
    //   42: astore #5
    //   44: aload #5
    //   46: invokestatic checkNotNull : (Ljava/lang/Object;)V
    //   49: new java/util/concurrent/ConcurrentLinkedDeque$Node
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
    //   69: goto -> 87
    //   72: aload_3
    //   73: aload #6
    //   75: invokevirtual lazySetNext : (Ljava/util/concurrent/ConcurrentLinkedDeque$Node;)V
    //   78: aload #6
    //   80: aload_3
    //   81: invokevirtual lazySetPrev : (Ljava/util/concurrent/ConcurrentLinkedDeque$Node;)V
    //   84: aload #6
    //   86: astore_3
    //   87: goto -> 25
    //   90: aload_2
    //   91: ifnonnull -> 96
    //   94: iconst_0
    //   95: ireturn
    //   96: aload_0
    //   97: getfield tail : Ljava/util/concurrent/ConcurrentLinkedDeque$Node;
    //   100: astore #4
    //   102: aload #4
    //   104: astore #5
    //   106: aload #5
    //   108: getfield next : Ljava/util/concurrent/ConcurrentLinkedDeque$Node;
    //   111: dup
    //   112: astore #6
    //   114: ifnull -> 155
    //   117: aload #6
    //   119: dup
    //   120: astore #5
    //   122: getfield next : Ljava/util/concurrent/ConcurrentLinkedDeque$Node;
    //   125: dup
    //   126: astore #6
    //   128: ifnull -> 155
    //   131: aload #4
    //   133: aload_0
    //   134: getfield tail : Ljava/util/concurrent/ConcurrentLinkedDeque$Node;
    //   137: dup
    //   138: astore #4
    //   140: if_acmpeq -> 148
    //   143: aload #4
    //   145: goto -> 150
    //   148: aload #6
    //   150: astore #5
    //   152: goto -> 106
    //   155: aload #5
    //   157: getfield prev : Ljava/util/concurrent/ConcurrentLinkedDeque$Node;
    //   160: aload #5
    //   162: if_acmpne -> 168
    //   165: goto -> 96
    //   168: aload_2
    //   169: aload #5
    //   171: invokevirtual lazySetPrev : (Ljava/util/concurrent/ConcurrentLinkedDeque$Node;)V
    //   174: aload #5
    //   176: aconst_null
    //   177: aload_2
    //   178: invokevirtual casNext : (Ljava/util/concurrent/ConcurrentLinkedDeque$Node;Ljava/util/concurrent/ConcurrentLinkedDeque$Node;)Z
    //   181: ifeq -> 106
    //   184: aload_0
    //   185: aload #4
    //   187: aload_3
    //   188: invokespecial casTail : (Ljava/util/concurrent/ConcurrentLinkedDeque$Node;Ljava/util/concurrent/ConcurrentLinkedDeque$Node;)Z
    //   191: ifne -> 215
    //   194: aload_0
    //   195: getfield tail : Ljava/util/concurrent/ConcurrentLinkedDeque$Node;
    //   198: astore #4
    //   200: aload_3
    //   201: getfield next : Ljava/util/concurrent/ConcurrentLinkedDeque$Node;
    //   204: ifnonnull -> 215
    //   207: aload_0
    //   208: aload #4
    //   210: aload_3
    //   211: invokespecial casTail : (Ljava/util/concurrent/ConcurrentLinkedDeque$Node;Ljava/util/concurrent/ConcurrentLinkedDeque$Node;)Z
    //   214: pop
    //   215: iconst_1
    //   216: ireturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #1168	-> 0
    //   #1170	-> 5
    //   #1173	-> 13
    //   #1174	-> 17
    //   #1175	-> 44
    //   #1176	-> 49
    //   #1177	-> 60
    //   #1178	-> 64
    //   #1180	-> 72
    //   #1181	-> 78
    //   #1182	-> 84
    //   #1184	-> 87
    //   #1185	-> 90
    //   #1186	-> 94
    //   #1191	-> 96
    //   #1192	-> 106
    //   #1196	-> 131
    //   #1197	-> 155
    //   #1198	-> 165
    //   #1201	-> 168
    //   #1202	-> 174
    //   #1205	-> 184
    //   #1208	-> 194
    //   #1209	-> 200
    //   #1210	-> 207
    //   #1212	-> 215
  }
  
  public void clear() {
    while (pollFirst() != null);
  }
  
  public Object[] toArray() {
    return toArrayList().toArray();
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    return toArrayList().toArray(paramArrayOfT);
  }
  
  public Iterator<E> iterator() {
    return new Itr(this, null);
  }
  
  public Iterator<E> descendingIterator() {
    return new DescendingItr(this, null);
  }
  
  public Spliterator<E> spliterator() {
    return new CLDSpliterator(this);
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
      node.lazySetPrev(node2);
      node2 = node;
    } 
    initHeadTail(node1, node2);
  }
  
  private boolean casHead(Node<E> paramNode1, Node<E> paramNode2) {
    return UNSAFE.compareAndSwapObject(this, headOffset, paramNode1, paramNode2);
  }
  
  private boolean casTail(Node<E> paramNode1, Node<E> paramNode2) {
    return UNSAFE.compareAndSwapObject(this, tailOffset, paramNode1, paramNode2);
  }
  
  private static final Node<Object> PREV_TERMINATOR = new Node();
  
  static {
    PREV_TERMINATOR.next = PREV_TERMINATOR;
  }
  
  private static final Node<Object> NEXT_TERMINATOR = new Node();
  
  private static final int HOPS = 2;
  
  private static final Unsafe UNSAFE;
  
  private static final long headOffset;
  
  private static final long tailOffset;
  
  static {
    NEXT_TERMINATOR.prev = NEXT_TERMINATOR;
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class<ConcurrentLinkedDeque> clazz = ConcurrentLinkedDeque.class;
      headOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("head"));
      tailOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("tail"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
}
