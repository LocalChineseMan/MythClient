package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import sun.misc.Unsafe;

public class ConcurrentSkipListMap<K, V> extends AbstractMap<K, V> implements ConcurrentNavigableMap<K, V>, Cloneable, Serializable {
  private static final long serialVersionUID = -8627078645895051609L;
  
  private static final Object BASE_HEADER = new Object();
  
  private volatile transient HeadIndex<K, V> head;
  
  final Comparator<? super K> comparator;
  
  private transient KeySet<K> keySet;
  
  private transient EntrySet<K, V> entrySet;
  
  private transient Values<V> values;
  
  private transient ConcurrentNavigableMap<K, V> descendingMap;
  
  private static final int EQ = 1;
  
  private static final int LT = 2;
  
  private static final int GT = 0;
  
  private static final Unsafe UNSAFE;
  
  private static final long headOffset;
  
  private static final long SECONDARY;
  
  private void initialize() {
    this.keySet = null;
    this.entrySet = null;
    this.values = null;
    this.descendingMap = null;
    this.head = new HeadIndex<>(new Node<>(null, BASE_HEADER, null), null, null, 1);
  }
  
  private boolean casHead(HeadIndex<K, V> paramHeadIndex1, HeadIndex<K, V> paramHeadIndex2) {
    return UNSAFE.compareAndSwapObject(this, headOffset, paramHeadIndex1, paramHeadIndex2);
  }
  
  static final class Node<K, V> {
    final K key;
    
    volatile Object value;
    
    volatile Node<K, V> next;
    
    private static final Unsafe UNSAFE;
    
    private static final long valueOffset;
    
    private static final long nextOffset;
    
    Node(K param1K, Object param1Object, Node<K, V> param1Node) {
      this.key = param1K;
      this.value = param1Object;
      this.next = param1Node;
    }
    
    Node(Node<K, V> param1Node) {
      this.key = null;
      this.value = this;
      this.next = param1Node;
    }
    
    boolean casValue(Object param1Object1, Object param1Object2) {
      return UNSAFE.compareAndSwapObject(this, valueOffset, param1Object1, param1Object2);
    }
    
    boolean casNext(Node<K, V> param1Node1, Node<K, V> param1Node2) {
      return UNSAFE.compareAndSwapObject(this, nextOffset, param1Node1, param1Node2);
    }
    
    boolean isMarker() {
      return (this.value == this);
    }
    
    boolean isBaseHeader() {
      return (this.value == ConcurrentSkipListMap.BASE_HEADER);
    }
    
    boolean appendMarker(Node<K, V> param1Node) {
      return casNext(param1Node, new Node(param1Node));
    }
    
    void helpDelete(Node<K, V> param1Node1, Node<K, V> param1Node2) {
      if (param1Node2 == this.next && this == param1Node1.next)
        if (param1Node2 == null || param1Node2.value != param1Node2) {
          casNext(param1Node2, new Node(param1Node2));
        } else {
          param1Node1.casNext(this, param1Node2.next);
        }  
    }
    
    V getValidValue() {
      Object object = this.value;
      if (object == this || object == ConcurrentSkipListMap.BASE_HEADER)
        return null; 
      return (V)object;
    }
    
    AbstractMap.SimpleImmutableEntry<K, V> createSnapshot() {
      Object object1 = this.value;
      if (object1 == null || object1 == this || object1 == ConcurrentSkipListMap.BASE_HEADER)
        return null; 
      Object object2 = object1;
      return new AbstractMap.SimpleImmutableEntry<>(this.key, (V)object2);
    }
    
    static {
      try {
        UNSAFE = Unsafe.getUnsafe();
        Class<Node> clazz = Node.class;
        valueOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("value"));
        nextOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("next"));
      } catch (Exception exception) {
        throw new Error(exception);
      } 
    }
  }
  
  static class Index<K, V> {
    final ConcurrentSkipListMap.Node<K, V> node;
    
    final Index<K, V> down;
    
    volatile Index<K, V> right;
    
    private static final Unsafe UNSAFE;
    
    private static final long rightOffset;
    
    Index(ConcurrentSkipListMap.Node<K, V> param1Node, Index<K, V> param1Index1, Index<K, V> param1Index2) {
      this.node = param1Node;
      this.down = param1Index1;
      this.right = param1Index2;
    }
    
    final boolean casRight(Index<K, V> param1Index1, Index<K, V> param1Index2) {
      return UNSAFE.compareAndSwapObject(this, rightOffset, param1Index1, param1Index2);
    }
    
    final boolean indexesDeletedNode() {
      return (this.node.value == null);
    }
    
    final boolean link(Index<K, V> param1Index1, Index<K, V> param1Index2) {
      ConcurrentSkipListMap.Node<K, V> node = this.node;
      param1Index2.right = param1Index1;
      return (node.value != null && casRight(param1Index1, param1Index2));
    }
    
    final boolean unlink(Index<K, V> param1Index) {
      return (this.node.value != null && casRight(param1Index, param1Index.right));
    }
    
    static {
      try {
        UNSAFE = Unsafe.getUnsafe();
        Class<Index> clazz = Index.class;
        rightOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("right"));
      } catch (Exception exception) {
        throw new Error(exception);
      } 
    }
  }
  
  static final class HeadIndex<K, V> extends Index<K, V> {
    final int level;
    
    HeadIndex(ConcurrentSkipListMap.Node<K, V> param1Node, ConcurrentSkipListMap.Index<K, V> param1Index1, ConcurrentSkipListMap.Index<K, V> param1Index2, int param1Int) {
      super(param1Node, param1Index1, param1Index2);
      this.level = param1Int;
    }
  }
  
  static final int cpr(Comparator<Object> paramComparator, Object paramObject1, Object paramObject2) {
    return (paramComparator != null) ? paramComparator.compare(paramObject1, paramObject2) : ((Comparable<Object>)paramObject1).compareTo(paramObject2);
  }
  
  private Node<K, V> findPredecessor(Object paramObject, Comparator<? super K> paramComparator) {
    if (paramObject == null)
      throw new NullPointerException(); 
    label22: while (true) {
      HeadIndex<K, V> headIndex = this.head;
      Index<K, V> index = headIndex.right;
      while (true) {
        if (index != null) {
          Node<K, V> node = index.node;
          K k = node.key;
          if (node.value == null) {
            if (!headIndex.unlink(index))
              continue label22; 
            index = headIndex.right;
            continue;
          } 
          if (cpr(paramComparator, paramObject, k) > 0) {
            index1 = index;
            index = index.right;
            continue;
          } 
        } 
        Index<K, V> index2;
        if ((index2 = index1.down) == null)
          return index1.node; 
        Index<K, V> index1 = index2;
        index = index2.right;
      } 
      break;
    } 
  }
  
  private Node<K, V> findNode(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
    Comparator<? super K> comparator = this.comparator;
    label31: while (true) {
      Node<K, V> node1 = findPredecessor(paramObject, comparator), node2 = node1.next;
      while (node2 != null) {
        Node<K, V> node = node2.next;
        if (node2 != node1.next)
          continue label31; 
        Object object;
        if ((object = node2.value) == null) {
          node2.helpDelete(node1, node);
          continue label31;
        } 
        if (node1.value != null) {
          if (object == node2)
            continue label31; 
          int i;
          if ((i = cpr(comparator, paramObject, node2.key)) == 0)
            return node2; 
          if (i < 0)
            break; 
          node1 = node2;
          node2 = node;
          continue;
        } 
        continue label31;
      } 
      break;
    } 
    return null;
  }
  
  private V doGet(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
    Comparator<? super K> comparator = this.comparator;
    label31: while (true) {
      Node<K, V> node1 = findPredecessor(paramObject, comparator), node2 = node1.next;
      while (node2 != null) {
        Node<K, V> node = node2.next;
        if (node2 != node1.next)
          continue label31; 
        Object object;
        if ((object = node2.value) == null) {
          node2.helpDelete(node1, node);
          continue label31;
        } 
        if (node1.value != null) {
          if (object == node2)
            continue label31; 
          int i;
          if ((i = cpr(comparator, paramObject, node2.key)) == 0)
            return (V)object; 
          if (i < 0)
            break; 
          node1 = node2;
          node2 = node;
          continue;
        } 
        continue label31;
      } 
      break;
    } 
    return null;
  }
  
  private V doPut(K paramK, V paramV, boolean paramBoolean) {
    Node<K, V> node;
    if (paramK == null)
      throw new NullPointerException(); 
    Comparator<? super K> comparator = this.comparator;
    label101: while (true) {
      Node<K, V> node1 = findPredecessor(paramK, comparator), node2 = node1.next;
      while (node2 != null) {
        Node<K, V> node3 = node2.next;
        if (node2 != node1.next)
          continue label101; 
        Object object;
        if ((object = node2.value) == null) {
          node2.helpDelete(node1, node3);
          continue label101;
        } 
        if (node1.value != null) {
          if (object == node2)
            continue label101; 
          int j;
          if ((j = cpr(comparator, paramK, node2.key)) > 0) {
            node1 = node2;
            node2 = node3;
            continue;
          } 
          if (j == 0) {
            if (!paramBoolean) {
              if (node2.casValue(object, paramV))
                return (V)object; 
              continue;
            } 
            continue;
          } 
          break;
        } 
        continue label101;
      } 
      node = new Node<>(paramK, paramV, node2);
      if (!node1.casNext(node2, node))
        continue; 
      break;
    } 
    int i = ThreadLocalRandom.nextSecondarySeed();
    if ((i & 0x80000001) == 0) {
      int j = 1;
      while (((i >>>= 1) & 0x1) != 0)
        j++; 
      Index<K, V> index = null;
      HeadIndex<K, V> headIndex = this.head;
      int k;
      if (j <= (k = headIndex.level)) {
        for (byte b = 1; b <= j; b++)
          index = new Index<>(node, index, null); 
      } else {
        j = k + 1;
        Index[] arrayOfIndex = new Index[j + 1];
        int n;
        for (n = 1; n <= j; n++)
          arrayOfIndex[n] = index = new Index<>(node, index, null); 
        while (true) {
          headIndex = this.head;
          n = headIndex.level;
          if (j <= n)
            break; 
          HeadIndex<K, V> headIndex1 = headIndex;
          Node<K, V> node1 = headIndex.node;
          for (int i1 = n + 1; i1 <= j; i1++)
            headIndex1 = new HeadIndex<>(node1, headIndex1, arrayOfIndex[i1], i1); 
          if (casHead(headIndex, headIndex1)) {
            headIndex = headIndex1;
            index = arrayOfIndex[j = n];
            break;
          } 
        } 
      } 
      int m = j;
      label105: while (true) {
        int n = headIndex.level;
        HeadIndex<K, V> headIndex1 = headIndex;
        Index<K, V> index1 = headIndex1.right, index2 = index;
        while (headIndex1 != null && index2 != null) {
          if (index1 != null) {
            Node<K, V> node1 = index1.node;
            int i1 = cpr(comparator, paramK, node1.key);
            if (node1.value == null) {
              if (!headIndex1.unlink(index1))
                continue label105; 
              index1 = headIndex1.right;
              continue;
            } 
            if (i1 > 0) {
              index3 = index1;
              index1 = index1.right;
              continue;
            } 
          } 
          if (n == m) {
            if (!index3.link(index1, index2))
              continue label105; 
            if (index2.node.value == null) {
              findNode(paramK);
              break;
            } 
            if (--m == 0)
              break; 
          } 
          if (--n >= m && n < j)
            index2 = index2.down; 
          Index<K, V> index3 = index3.down;
          index1 = index3.right;
        } 
        break;
      } 
    } 
    return null;
  }
  
  final V doRemove(Object paramObject1, Object paramObject2) {
    if (paramObject1 == null)
      throw new NullPointerException(); 
    Comparator<? super K> comparator = this.comparator;
    label44: while (true) {
      Node<K, V> node1 = findPredecessor(paramObject1, comparator), node2 = node1.next;
      while (node2 != null) {
        Node<K, V> node = node2.next;
        if (node2 != node1.next)
          continue label44; 
        Object object;
        if ((object = node2.value) == null) {
          node2.helpDelete(node1, node);
          continue label44;
        } 
        if (node1.value != null) {
          if (object == node2)
            continue label44; 
          int i;
          if ((i = cpr(comparator, paramObject1, node2.key)) < 0)
            break; 
          if (i > 0) {
            node1 = node2;
            node2 = node;
            continue;
          } 
          if (paramObject2 != null) {
            if (!paramObject2.equals(object))
              break; 
            if (!node2.casValue(object, null))
              continue; 
            if (!node2.appendMarker(node) || !node1.casNext(node2, node)) {
              findNode(paramObject1);
            } else {
              findPredecessor(paramObject1, comparator);
              if (this.head.right == null)
                tryReduceLevel(); 
            } 
            return (V)object;
          } 
          continue label44;
        } 
        continue label44;
      } 
      break;
    } 
    return null;
  }
  
  private void tryReduceLevel() {
    HeadIndex<K, V> headIndex1 = this.head;
    HeadIndex<K, V> headIndex2;
    HeadIndex headIndex;
    if (headIndex1.level > 3 && (headIndex2 = (HeadIndex)headIndex1.down) != null && (headIndex = (HeadIndex)headIndex2.down) != null && headIndex.right == null && headIndex2.right == null && headIndex1.right == null && 
      
      casHead(headIndex1, headIndex2) && headIndex1.right != null)
      casHead(headIndex2, headIndex1); 
  }
  
  final Node<K, V> findFirst() {
    while (true) {
      Node<K, V> node1;
      Node<K, V> node2;
      if ((node2 = (node1 = this.head.node).next) == null)
        return null; 
      if (node2.value != null)
        return node2; 
      node2.helpDelete(node1, node2.next);
    } 
  }
  
  private Map.Entry<K, V> doRemoveFirstEntry() {
    Node<K, V> node1, node2, node3;
    Object object1;
    while (true) {
      if ((node2 = (node1 = this.head.node).next) == null)
        return null; 
      node3 = node2.next;
      if (node2 != node1.next)
        continue; 
      object1 = node2.value;
      if (object1 == null) {
        node2.helpDelete(node1, node3);
        continue;
      } 
      if (!node2.casValue(object1, null))
        continue; 
      break;
    } 
    if (!node2.appendMarker(node3) || !node1.casNext(node2, node3))
      findFirst(); 
    clearIndexToFirst();
    Object object2 = object1;
    return new AbstractMap.SimpleImmutableEntry<>(node2.key, (V)object2);
  }
  
  private void clearIndexToFirst() {
    label16: while (true) {
      HeadIndex<K, V> headIndex = this.head;
      while (true) {
        Index<K, V> index2 = headIndex.right;
        if (index2 != null && index2.indexesDeletedNode() && !headIndex.unlink(index2))
          continue label16; 
        Index<K, V> index1;
        if ((index1 = headIndex.down) == null) {
          if (this.head.right == null)
            tryReduceLevel(); 
          return;
        } 
      } 
      break;
    } 
  }
  
  private Map.Entry<K, V> doRemoveLastEntry() {
    Node<K, V> node1, node2, node3;
    Object object1;
    label35: while (true) {
      node1 = findPredecessorOfLast();
      node2 = node1.next;
      if (node2 == null) {
        if (node1.isBaseHeader())
          return null; 
        continue;
      } 
      while (true) {
        node3 = node2.next;
        if (node2 != node1.next)
          continue label35; 
        object1 = node2.value;
        if (object1 == null) {
          node2.helpDelete(node1, node3);
          continue label35;
        } 
        if (node1.value != null) {
          if (object1 == node2)
            continue label35; 
          if (node3 != null) {
            node1 = node2;
            node2 = node3;
            continue;
          } 
          break;
        } 
        continue label35;
      } 
      if (!node2.casValue(object1, null))
        continue; 
      break;
    } 
    K k = node2.key;
    if (!node2.appendMarker(node3) || !node1.casNext(node2, node3)) {
      findNode(k);
    } else {
      findPredecessor(k, this.comparator);
      if (this.head.right == null)
        tryReduceLevel(); 
    } 
    Object object2 = object1;
    return new AbstractMap.SimpleImmutableEntry<>(k, (V)object2);
  }
  
  final Node<K, V> findLast() {
    HeadIndex<K, V> headIndex = this.head;
    while (true) {
      Index<K, V> index3;
      while ((index3 = headIndex.right) != null) {
        if (index3.indexesDeletedNode()) {
          headIndex.unlink(index3);
          headIndex = this.head;
          continue;
        } 
        index1 = index3;
      } 
      Index<K, V> index2;
      if ((index2 = index1.down) != null) {
        index1 = index2;
        continue;
      } 
      Node<K, V> node1 = index1.node, node2 = node1.next;
      while (true) {
        if (node2 == null)
          return node1.isBaseHeader() ? null : node1; 
        Node<K, V> node = node2.next;
        if (node2 != node1.next)
          break; 
        Object object = node2.value;
        if (object == null) {
          node2.helpDelete(node1, node);
          break;
        } 
        if (node1.value == null || object == node2)
          break; 
        node1 = node2;
        node2 = node;
      } 
      Index<K, V> index1 = this.head;
    } 
  }
  
  private Node<K, V> findPredecessorOfLast() {
    Index<K, V> index;
    label19: while (true) {
      index = this.head;
      while (true) {
        Index<K, V> index2;
        if ((index2 = index.right) != null) {
          if (index2.indexesDeletedNode()) {
            index.unlink(index2);
            continue label19;
          } 
          if (index2.node.next != null) {
            index = index2;
            continue;
          } 
        } 
        Index<K, V> index1;
        if ((index1 = index.down) != null) {
          index = index1;
          continue;
        } 
        break;
      } 
      break;
    } 
    return index.node;
  }
  
  final Node<K, V> findNear(K paramK, int paramInt, Comparator<? super K> paramComparator) {
    if (paramK == null)
      throw new NullPointerException(); 
    label40: while (true) {
      Node<K, V> node1 = findPredecessor(paramK, paramComparator), node2 = node1.next;
      while (true) {
        if (node2 == null)
          return ((paramInt & 0x2) == 0 || node1.isBaseHeader()) ? null : node1; 
        Node<K, V> node = node2.next;
        if (node2 != node1.next)
          continue label40; 
        Object object;
        if ((object = node2.value) == null) {
          node2.helpDelete(node1, node);
          continue label40;
        } 
        if (node1.value != null) {
          if (object == node2)
            continue label40; 
          int i = cpr(paramComparator, paramK, node2.key);
          if ((i == 0 && (paramInt & 0x1) != 0) || (i < 0 && (paramInt & 0x2) == 0))
            return node2; 
          if (i <= 0 && (paramInt & 0x2) != 0)
            return node1.isBaseHeader() ? null : node1; 
          node1 = node2;
          node2 = node;
          continue;
        } 
        continue label40;
      } 
      break;
    } 
  }
  
  final AbstractMap.SimpleImmutableEntry<K, V> getNear(K paramK, int paramInt) {
    Comparator<? super K> comparator = this.comparator;
    while (true) {
      Node<K, V> node = findNear(paramK, paramInt, comparator);
      if (node == null)
        return null; 
      AbstractMap.SimpleImmutableEntry<K, V> simpleImmutableEntry = node.createSnapshot();
      if (simpleImmutableEntry != null)
        return simpleImmutableEntry; 
    } 
  }
  
  public ConcurrentSkipListMap() {
    this.comparator = null;
    initialize();
  }
  
  public ConcurrentSkipListMap(Comparator<? super K> paramComparator) {
    this.comparator = paramComparator;
    initialize();
  }
  
  public ConcurrentSkipListMap(Map<? extends K, ? extends V> paramMap) {
    this.comparator = null;
    initialize();
    putAll(paramMap);
  }
  
  public ConcurrentSkipListMap(SortedMap<K, ? extends V> paramSortedMap) {
    this.comparator = paramSortedMap.comparator();
    initialize();
    buildFromSorted(paramSortedMap);
  }
  
  public ConcurrentSkipListMap<K, V> clone() {
    try {
      ConcurrentSkipListMap concurrentSkipListMap = (ConcurrentSkipListMap)super.clone();
      concurrentSkipListMap.initialize();
      concurrentSkipListMap.buildFromSorted((SortedMap<K, ? extends V>)this);
      return concurrentSkipListMap;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError();
    } 
  }
  
  private void buildFromSorted(SortedMap<K, ? extends V> paramSortedMap) {
    if (paramSortedMap == null)
      throw new NullPointerException(); 
    HeadIndex<K, V> headIndex1 = this.head;
    Node<K, V> node = headIndex1.node;
    ArrayList<HeadIndex<K, V>> arrayList = new ArrayList();
    for (byte b = 0; b <= headIndex1.level; b++)
      arrayList.add(null); 
    HeadIndex<K, V> headIndex2 = headIndex1;
    for (int i = headIndex1.level; i > 0; i--) {
      arrayList.set(i, headIndex2);
      Index<K, V> index = headIndex2.down;
    } 
    Iterator<Map.Entry> iterator = paramSortedMap.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry entry = iterator.next();
      int j = ThreadLocalRandom.current().nextInt();
      int k = 0;
      if ((j & 0x80000001) == 0) {
        do {
          k++;
        } while (((j >>>= 1) & 0x1) != 0);
        if (k > headIndex1.level)
          k = headIndex1.level + 1; 
      } 
      Object object1 = entry.getKey();
      Object object2 = entry.getValue();
      if (object1 == null || object2 == null)
        throw new NullPointerException(); 
      Node<Object, Object> node2 = new Node<>(object1, object2, null);
      node.next = (Node)node2;
      Node<Object, Object> node1 = node2;
      if (k > 0) {
        Index<Object, Object> index = null;
        for (byte b1 = 1; b1 <= k; b1++) {
          index = new Index<>(node2, index, null);
          if (b1 > headIndex1.level)
            headIndex1 = new HeadIndex<>(headIndex1.node, headIndex1, (Index)index, b1); 
          if (b1 < arrayList.size()) {
            ((Index)arrayList.get(b1)).right = index;
            arrayList.set(b1, index);
          } else {
            arrayList.add(index);
          } 
        } 
      } 
    } 
    this.head = headIndex1;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    for (Node<K, V> node = findFirst(); node != null; node = node.next) {
      V v = node.getValidValue();
      if (v != null) {
        paramObjectOutputStream.writeObject(node.key);
        paramObjectOutputStream.writeObject(v);
      } 
    } 
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    initialize();
    HeadIndex<K, V> headIndex1 = this.head;
    Node<K, V> node = headIndex1.node;
    ArrayList<HeadIndex<K, V>> arrayList = new ArrayList();
    for (byte b = 0; b <= headIndex1.level; b++)
      arrayList.add(null); 
    HeadIndex<K, V> headIndex2 = headIndex1;
    for (int i = headIndex1.level; i > 0; i--) {
      arrayList.set(i, headIndex2);
      Index<K, V> index = headIndex2.down;
    } 
    while (true) {
      Object object1 = paramObjectInputStream.readObject();
      if (object1 == null)
        break; 
      Object object2 = paramObjectInputStream.readObject();
      if (object2 == null)
        throw new NullPointerException(); 
      Object object3 = object1;
      Object object4 = object2;
      int j = ThreadLocalRandom.current().nextInt();
      int k = 0;
      if ((j & 0x80000001) == 0) {
        do {
          k++;
        } while (((j >>>= 1) & 0x1) != 0);
        if (k > headIndex1.level)
          k = headIndex1.level + 1; 
      } 
      Node<Object, Object> node2 = new Node<>(object3, object4, null);
      node.next = (Node)node2;
      Node<Object, Object> node1 = node2;
      if (k > 0) {
        Index<Object, Object> index = null;
        for (byte b1 = 1; b1 <= k; b1++) {
          index = new Index<>(node2, index, null);
          if (b1 > headIndex1.level)
            headIndex1 = new HeadIndex<>(headIndex1.node, headIndex1, (Index)index, b1); 
          if (b1 < arrayList.size()) {
            ((Index)arrayList.get(b1)).right = index;
            arrayList.set(b1, index);
          } else {
            arrayList.add(index);
          } 
        } 
      } 
    } 
    this.head = headIndex1;
  }
  
  public boolean containsKey(Object paramObject) {
    return (doGet(paramObject) != null);
  }
  
  public V get(Object paramObject) {
    return doGet(paramObject);
  }
  
  public V getOrDefault(Object paramObject, V paramV) {
    V v;
    return ((v = doGet(paramObject)) == null) ? paramV : v;
  }
  
  public V put(K paramK, V paramV) {
    if (paramV == null)
      throw new NullPointerException(); 
    return doPut(paramK, paramV, false);
  }
  
  public V remove(Object paramObject) {
    return doRemove(paramObject, (Object)null);
  }
  
  public boolean containsValue(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
    for (Node<K, V> node = findFirst(); node != null; node = node.next) {
      V v = node.getValidValue();
      if (v != null && paramObject.equals(v))
        return true; 
    } 
    return false;
  }
  
  public int size() {
    long l = 0L;
    for (Node<K, V> node = findFirst(); node != null; node = node.next) {
      if (node.getValidValue() != null)
        l++; 
    } 
    return (l >= 2147483647L) ? Integer.MAX_VALUE : (int)l;
  }
  
  public boolean isEmpty() {
    return (findFirst() == null);
  }
  
  public void clear() {
    initialize();
  }
  
  public V computeIfAbsent(K paramK, Function<? super K, ? extends V> paramFunction) {
    if (paramK == null || paramFunction == null)
      throw new NullPointerException(); 
    V v1;
    V v2;
    if ((v1 = doGet(paramK)) == null && (
      v2 = paramFunction.apply(paramK)) != null) {
      V v;
      v1 = ((v = doPut(paramK, v2, true)) == null) ? v2 : v;
    } 
    return v1;
  }
  
  public V computeIfPresent(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    if (paramK == null || paramBiFunction == null)
      throw new NullPointerException(); 
    Node<K, V> node;
    while ((node = findNode(paramK)) != null) {
      Object object;
      if ((object = node.value) != null) {
        Object object1 = object;
        V v = paramBiFunction.apply(paramK, (V)object1);
        if (v != null) {
          if (node.casValue(object1, v))
            return v; 
          continue;
        } 
        if (doRemove(paramK, object1) != null)
          break; 
      } 
    } 
    return null;
  }
  
  public V compute(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    if (paramK == null || paramBiFunction == null)
      throw new NullPointerException(); 
    label23: while (true) {
      Node<K, V> node;
      while ((node = findNode(paramK)) == null) {
        V v;
        if ((v = paramBiFunction.apply(paramK, null)) == null)
          break label23; 
        if (doPut(paramK, v, true) == null)
          return v; 
      } 
      Object object;
      if ((object = node.value) != null) {
        Object object1 = object;
        V v;
        if ((v = paramBiFunction.apply(paramK, (V)object1)) != null) {
          if (node.casValue(object1, v))
            return v; 
          continue;
        } 
        if (doRemove(paramK, object1) != null)
          break; 
      } 
    } 
    return null;
  }
  
  public V merge(K paramK, V paramV, BiFunction<? super V, ? super V, ? extends V> paramBiFunction) {
    if (paramK == null || paramV == null || paramBiFunction == null)
      throw new NullPointerException(); 
    while (true) {
      Node<K, V> node;
      while ((node = findNode(paramK)) == null) {
        if (doPut(paramK, paramV, true) == null)
          return paramV; 
      } 
      Object object;
      if ((object = node.value) != null) {
        Object object1 = object;
        V v;
        if ((v = paramBiFunction.apply((V)object1, paramV)) != null) {
          if (node.casValue(object1, v))
            return v; 
          continue;
        } 
        if (doRemove(paramK, object1) != null)
          return null; 
      } 
    } 
  }
  
  public NavigableSet<K> keySet() {
    KeySet<K> keySet = this.keySet;
    return (keySet != null) ? keySet : (this.keySet = new KeySet<>((ConcurrentNavigableMap<K, ?>)this));
  }
  
  public NavigableSet<K> navigableKeySet() {
    KeySet<K> keySet = this.keySet;
    return (keySet != null) ? keySet : (this.keySet = new KeySet<>((ConcurrentNavigableMap<K, ?>)this));
  }
  
  public Collection<V> values() {
    Values<V> values = this.values;
    return (values != null) ? values : (this.values = new Values<>((ConcurrentNavigableMap<?, V>)this));
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    EntrySet<K, V> entrySet = this.entrySet;
    return (entrySet != null) ? entrySet : (this.entrySet = new EntrySet<>((ConcurrentNavigableMap<K, V>)this));
  }
  
  public ConcurrentNavigableMap<K, V> descendingMap() {
    ConcurrentNavigableMap<K, V> concurrentNavigableMap = this.descendingMap;
    return (concurrentNavigableMap != null) ? concurrentNavigableMap : (this.descendingMap = new SubMap<>(this, null, false, null, false, true));
  }
  
  public NavigableSet<K> descendingKeySet() {
    return descendingMap().navigableKeySet();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof Map))
      return false; 
    Map map = (Map)paramObject;
    try {
      for (Map.Entry<K, V> entry : entrySet()) {
        if (!entry.getValue().equals(map.get(entry.getKey())))
          return false; 
      } 
      for (Map.Entry entry : map.entrySet()) {
        Object object1 = entry.getKey();
        Object object2 = entry.getValue();
        if (object1 == null || object2 == null || !object2.equals(get(object1)))
          return false; 
      } 
      return true;
    } catch (ClassCastException classCastException) {
      return false;
    } catch (NullPointerException nullPointerException) {
      return false;
    } 
  }
  
  public V putIfAbsent(K paramK, V paramV) {
    if (paramV == null)
      throw new NullPointerException(); 
    return doPut(paramK, paramV, true);
  }
  
  public boolean remove(Object paramObject1, Object paramObject2) {
    if (paramObject1 == null)
      throw new NullPointerException(); 
    return (paramObject2 != null && doRemove(paramObject1, paramObject2) != null);
  }
  
  public boolean replace(K paramK, V paramV1, V paramV2) {
    if (paramK == null || paramV1 == null || paramV2 == null)
      throw new NullPointerException(); 
    while (true) {
      Node<K, V> node;
      if ((node = findNode(paramK)) == null)
        return false; 
      Object object;
      if ((object = node.value) != null) {
        if (!paramV1.equals(object))
          return false; 
        if (node.casValue(object, paramV2))
          return true; 
      } 
    } 
  }
  
  public V replace(K paramK, V paramV) {
    if (paramK == null || paramV == null)
      throw new NullPointerException(); 
    while (true) {
      Node<K, V> node;
      if ((node = findNode(paramK)) == null)
        return null; 
      Object object;
      if ((object = node.value) != null && node.casValue(object, paramV))
        return (V)object; 
    } 
  }
  
  public Comparator<? super K> comparator() {
    return this.comparator;
  }
  
  public K firstKey() {
    Node<K, V> node = findFirst();
    if (node == null)
      throw new NoSuchElementException(); 
    return node.key;
  }
  
  public K lastKey() {
    Node<K, V> node = findLast();
    if (node == null)
      throw new NoSuchElementException(); 
    return node.key;
  }
  
  public ConcurrentNavigableMap<K, V> subMap(K paramK1, boolean paramBoolean1, K paramK2, boolean paramBoolean2) {
    if (paramK1 == null || paramK2 == null)
      throw new NullPointerException(); 
    return new SubMap<>(this, paramK1, paramBoolean1, paramK2, paramBoolean2, false);
  }
  
  public ConcurrentNavigableMap<K, V> headMap(K paramK, boolean paramBoolean) {
    if (paramK == null)
      throw new NullPointerException(); 
    return new SubMap<>(this, null, false, paramK, paramBoolean, false);
  }
  
  public ConcurrentNavigableMap<K, V> tailMap(K paramK, boolean paramBoolean) {
    if (paramK == null)
      throw new NullPointerException(); 
    return new SubMap<>(this, paramK, paramBoolean, null, false, false);
  }
  
  public ConcurrentNavigableMap<K, V> subMap(K paramK1, K paramK2) {
    return subMap(paramK1, true, paramK2, false);
  }
  
  public ConcurrentNavigableMap<K, V> headMap(K paramK) {
    return headMap(paramK, false);
  }
  
  public ConcurrentNavigableMap<K, V> tailMap(K paramK) {
    return tailMap(paramK, true);
  }
  
  public Map.Entry<K, V> lowerEntry(K paramK) {
    return getNear(paramK, 2);
  }
  
  public K lowerKey(K paramK) {
    Node<K, V> node = findNear(paramK, 2, this.comparator);
    return (node == null) ? null : node.key;
  }
  
  public Map.Entry<K, V> floorEntry(K paramK) {
    return getNear(paramK, 3);
  }
  
  public K floorKey(K paramK) {
    Node<K, V> node = findNear(paramK, 3, this.comparator);
    return (node == null) ? null : node.key;
  }
  
  public Map.Entry<K, V> ceilingEntry(K paramK) {
    return getNear(paramK, 1);
  }
  
  public K ceilingKey(K paramK) {
    Node<K, V> node = findNear(paramK, 1, this.comparator);
    return (node == null) ? null : node.key;
  }
  
  public Map.Entry<K, V> higherEntry(K paramK) {
    return getNear(paramK, 0);
  }
  
  public K higherKey(K paramK) {
    Node<K, V> node = findNear(paramK, 0, this.comparator);
    return (node == null) ? null : node.key;
  }
  
  public Map.Entry<K, V> firstEntry() {
    while (true) {
      Node<K, V> node = findFirst();
      if (node == null)
        return null; 
      AbstractMap.SimpleImmutableEntry<K, V> simpleImmutableEntry = node.createSnapshot();
      if (simpleImmutableEntry != null)
        return simpleImmutableEntry; 
    } 
  }
  
  public Map.Entry<K, V> lastEntry() {
    while (true) {
      Node<K, V> node = findLast();
      if (node == null)
        return null; 
      AbstractMap.SimpleImmutableEntry<K, V> simpleImmutableEntry = node.createSnapshot();
      if (simpleImmutableEntry != null)
        return simpleImmutableEntry; 
    } 
  }
  
  public Map.Entry<K, V> pollFirstEntry() {
    return doRemoveFirstEntry();
  }
  
  public Map.Entry<K, V> pollLastEntry() {
    return doRemoveLastEntry();
  }
  
  Iterator<K> keyIterator() {
    return new KeyIterator(this);
  }
  
  Iterator<V> valueIterator() {
    return new ValueIterator(this);
  }
  
  Iterator<Map.Entry<K, V>> entryIterator() {
    return new EntryIterator(this);
  }
  
  static final <E> List<E> toList(Collection<E> paramCollection) {
    // Byte code:
    //   0: new java/util/ArrayList
    //   3: dup
    //   4: invokespecial <init> : ()V
    //   7: astore_1
    //   8: aload_0
    //   9: invokeinterface iterator : ()Ljava/util/Iterator;
    //   14: astore_2
    //   15: aload_2
    //   16: invokeinterface hasNext : ()Z
    //   21: ifeq -> 40
    //   24: aload_2
    //   25: invokeinterface next : ()Ljava/lang/Object;
    //   30: astore_3
    //   31: aload_1
    //   32: aload_3
    //   33: invokevirtual add : (Ljava/lang/Object;)Z
    //   36: pop
    //   37: goto -> 15
    //   40: aload_1
    //   41: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #2372	-> 0
    //   #2373	-> 8
    //   #2374	-> 31
    //   #2375	-> 40
  }
  
  abstract class ConcurrentSkipListMap {}
  
  final class ConcurrentSkipListMap {}
  
  final class ConcurrentSkipListMap {}
  
  final class ConcurrentSkipListMap {}
  
  static final class KeySet<E> extends AbstractSet<E> implements NavigableSet<E> {
    final ConcurrentNavigableMap<E, ?> m;
    
    KeySet(ConcurrentNavigableMap<E, ?> param1ConcurrentNavigableMap) {
      this.m = param1ConcurrentNavigableMap;
    }
    
    public int size() {
      return this.m.size();
    }
    
    public boolean isEmpty() {
      return this.m.isEmpty();
    }
    
    public boolean contains(Object param1Object) {
      return this.m.containsKey(param1Object);
    }
    
    public boolean remove(Object param1Object) {
      return (this.m.remove(param1Object) != null);
    }
    
    public void clear() {
      this.m.clear();
    }
    
    public E lower(E param1E) {
      return this.m.lowerKey(param1E);
    }
    
    public E floor(E param1E) {
      return this.m.floorKey(param1E);
    }
    
    public E ceiling(E param1E) {
      return this.m.ceilingKey(param1E);
    }
    
    public E higher(E param1E) {
      return this.m.higherKey(param1E);
    }
    
    public Comparator<? super E> comparator() {
      return this.m.comparator();
    }
    
    public E first() {
      return this.m.firstKey();
    }
    
    public E last() {
      return this.m.lastKey();
    }
    
    public E pollFirst() {
      Map.Entry<E, ?> entry = this.m.pollFirstEntry();
      return (entry == null) ? null : entry.getKey();
    }
    
    public E pollLast() {
      Map.Entry<E, ?> entry = this.m.pollLastEntry();
      return (entry == null) ? null : entry.getKey();
    }
    
    public Iterator<E> iterator() {
      if (this.m instanceof ConcurrentSkipListMap)
        return ((ConcurrentSkipListMap)this.m).keyIterator(); 
      return ((ConcurrentSkipListMap.SubMap)this.m).keyIterator();
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof Set))
        return false; 
      Collection<?> collection = (Collection)param1Object;
      try {
        return (containsAll(collection) && collection.containsAll(this));
      } catch (ClassCastException classCastException) {
        return false;
      } catch (NullPointerException nullPointerException) {
        return false;
      } 
    }
    
    public Object[] toArray() {
      return ConcurrentSkipListMap.toList(this).toArray();
    }
    
    public <T> T[] toArray(T[] param1ArrayOfT) {
      return (T[])ConcurrentSkipListMap.toList(this).toArray((Object[])param1ArrayOfT);
    }
    
    public Iterator<E> descendingIterator() {
      return descendingSet().iterator();
    }
    
    public NavigableSet<E> subSet(E param1E1, boolean param1Boolean1, E param1E2, boolean param1Boolean2) {
      return new KeySet(this.m.subMap(param1E1, param1Boolean1, param1E2, param1Boolean2));
    }
    
    public NavigableSet<E> headSet(E param1E, boolean param1Boolean) {
      return new KeySet(this.m.headMap(param1E, param1Boolean));
    }
    
    public NavigableSet<E> tailSet(E param1E, boolean param1Boolean) {
      return new KeySet(this.m.tailMap(param1E, param1Boolean));
    }
    
    public NavigableSet<E> subSet(E param1E1, E param1E2) {
      return subSet(param1E1, true, param1E2, false);
    }
    
    public NavigableSet<E> headSet(E param1E) {
      return headSet(param1E, false);
    }
    
    public NavigableSet<E> tailSet(E param1E) {
      return tailSet(param1E, true);
    }
    
    public NavigableSet<E> descendingSet() {
      return new KeySet(this.m.descendingMap());
    }
    
    public Spliterator<E> spliterator() {
      if (this.m instanceof ConcurrentSkipListMap)
        return ((ConcurrentSkipListMap)this.m).keySpliterator(); 
      return (Spliterator<E>)((ConcurrentSkipListMap.SubMap)this.m).keyIterator();
    }
  }
  
  static final class Values<E> extends AbstractCollection<E> {
    final ConcurrentNavigableMap<?, E> m;
    
    Values(ConcurrentNavigableMap<?, E> param1ConcurrentNavigableMap) {
      this.m = param1ConcurrentNavigableMap;
    }
    
    public Iterator<E> iterator() {
      if (this.m instanceof ConcurrentSkipListMap)
        return ((ConcurrentSkipListMap)this.m).valueIterator(); 
      return ((ConcurrentSkipListMap.SubMap)this.m).valueIterator();
    }
    
    public boolean isEmpty() {
      return this.m.isEmpty();
    }
    
    public int size() {
      return this.m.size();
    }
    
    public boolean contains(Object param1Object) {
      return this.m.containsValue(param1Object);
    }
    
    public void clear() {
      this.m.clear();
    }
    
    public Object[] toArray() {
      return ConcurrentSkipListMap.toList(this).toArray();
    }
    
    public <T> T[] toArray(T[] param1ArrayOfT) {
      return (T[])ConcurrentSkipListMap.toList(this).toArray((Object[])param1ArrayOfT);
    }
    
    public Spliterator<E> spliterator() {
      if (this.m instanceof ConcurrentSkipListMap)
        return ((ConcurrentSkipListMap)this.m).valueSpliterator(); 
      return (Spliterator<E>)((ConcurrentSkipListMap.SubMap)this.m).valueIterator();
    }
  }
  
  static final class EntrySet<K1, V1> extends AbstractSet<Map.Entry<K1, V1>> {
    final ConcurrentNavigableMap<K1, V1> m;
    
    EntrySet(ConcurrentNavigableMap<K1, V1> param1ConcurrentNavigableMap) {
      this.m = param1ConcurrentNavigableMap;
    }
    
    public Iterator<Map.Entry<K1, V1>> iterator() {
      if (this.m instanceof ConcurrentSkipListMap)
        return ((ConcurrentSkipListMap)this.m).entryIterator(); 
      return ((ConcurrentSkipListMap.SubMap)this.m).entryIterator();
    }
    
    public boolean contains(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      V1 v1 = this.m.get(entry.getKey());
      return (v1 != null && v1.equals(entry.getValue()));
    }
    
    public boolean remove(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      return this.m.remove(entry.getKey(), entry
          .getValue());
    }
    
    public boolean isEmpty() {
      return this.m.isEmpty();
    }
    
    public int size() {
      return this.m.size();
    }
    
    public void clear() {
      this.m.clear();
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof Set))
        return false; 
      Collection<?> collection = (Collection)param1Object;
      try {
        return (containsAll(collection) && collection.containsAll(this));
      } catch (ClassCastException classCastException) {
        return false;
      } catch (NullPointerException nullPointerException) {
        return false;
      } 
    }
    
    public Object[] toArray() {
      return ConcurrentSkipListMap.toList(this).toArray();
    }
    
    public <T> T[] toArray(T[] param1ArrayOfT) {
      return (T[])ConcurrentSkipListMap.toList(this).toArray((Object[])param1ArrayOfT);
    }
    
    public Spliterator<Map.Entry<K1, V1>> spliterator() {
      if (this.m instanceof ConcurrentSkipListMap)
        return ((ConcurrentSkipListMap)this.m).entrySpliterator(); 
      return (Spliterator<Map.Entry<K1, V1>>)((ConcurrentSkipListMap.SubMap)this.m)
        .entryIterator();
    }
  }
  
  public void forEach(BiConsumer<? super K, ? super V> paramBiConsumer) {
    if (paramBiConsumer == null)
      throw new NullPointerException(); 
    for (Node<K, V> node = findFirst(); node != null; node = node.next) {
      V v;
      if ((v = node.getValidValue()) != null)
        paramBiConsumer.accept(node.key, v); 
    } 
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    if (paramBiFunction == null)
      throw new NullPointerException(); 
    for (Node<K, V> node = findFirst(); node != null; node = node.next) {
      V v;
      while ((v = node.getValidValue()) != null) {
        V v1 = paramBiFunction.apply(node.key, v);
        if (v1 == null)
          throw new NullPointerException(); 
        if (node.casValue(v, v1))
          break; 
      } 
    } 
  }
  
  final KeySpliterator<K, V> keySpliterator() {
    Comparator<? super K> comparator = this.comparator;
    while (true) {
      HeadIndex<K, V> headIndex;
      Node<K, V> node2 = (headIndex = this.head).node;
      Node<K, V> node1;
      if ((node1 = node2.next) == null || node1.value != null)
        return new KeySpliterator<>(comparator, headIndex, node1, null, (node1 == null) ? 0 : Integer.MAX_VALUE); 
      node1.helpDelete(node2, node1.next);
    } 
  }
  
  final ValueSpliterator<K, V> valueSpliterator() {
    Comparator<? super K> comparator = this.comparator;
    while (true) {
      HeadIndex<K, V> headIndex;
      Node<K, V> node2 = (headIndex = this.head).node;
      Node<K, V> node1;
      if ((node1 = node2.next) == null || node1.value != null)
        return new ValueSpliterator<>(comparator, headIndex, node1, null, (node1 == null) ? 0 : Integer.MAX_VALUE); 
      node1.helpDelete(node2, node1.next);
    } 
  }
  
  final EntrySpliterator<K, V> entrySpliterator() {
    Comparator<? super K> comparator = this.comparator;
    while (true) {
      HeadIndex<K, V> headIndex;
      Node<K, V> node2 = (headIndex = this.head).node;
      Node<K, V> node1;
      if ((node1 = node2.next) == null || node1.value != null)
        return new EntrySpliterator<>(comparator, headIndex, node1, null, (node1 == null) ? 0 : Integer.MAX_VALUE); 
      node1.helpDelete(node2, node1.next);
    } 
  }
  
  static {
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class<ConcurrentSkipListMap> clazz = ConcurrentSkipListMap.class;
      headOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("head"));
      Class<Thread> clazz1 = Thread.class;
      SECONDARY = UNSAFE.objectFieldOffset(clazz1.getDeclaredField("threadLocalRandomSecondarySeed"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  static final class ConcurrentSkipListMap {}
  
  static abstract class ConcurrentSkipListMap {}
  
  static final class ConcurrentSkipListMap {}
  
  static final class ConcurrentSkipListMap {}
  
  static final class ConcurrentSkipListMap {}
}
