package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PriorityQueue<E> extends AbstractQueue<E> implements Serializable {
  private static final long serialVersionUID = -7720805057305804111L;
  
  private static final int DEFAULT_INITIAL_CAPACITY = 11;
  
  transient Object[] queue;
  
  private int size = 0;
  
  private final Comparator<? super E> comparator;
  
  transient int modCount = 0;
  
  private static final int MAX_ARRAY_SIZE = 2147483639;
  
  public PriorityQueue() {
    this(11, null);
  }
  
  public PriorityQueue(int paramInt) {
    this(paramInt, null);
  }
  
  public PriorityQueue(Comparator<? super E> paramComparator) {
    this(11, paramComparator);
  }
  
  public PriorityQueue(int paramInt, Comparator<? super E> paramComparator) {
    if (paramInt < 1)
      throw new IllegalArgumentException(); 
    this.queue = new Object[paramInt];
    this.comparator = paramComparator;
  }
  
  public PriorityQueue(Collection<? extends E> paramCollection) {
    if (paramCollection instanceof SortedSet) {
      SortedSet<E> sortedSet = (SortedSet)paramCollection;
      this.comparator = sortedSet.comparator();
      initElementsFromCollection(sortedSet);
    } else if (paramCollection instanceof PriorityQueue) {
      PriorityQueue priorityQueue = (PriorityQueue)paramCollection;
      this.comparator = priorityQueue.comparator();
      initFromPriorityQueue(priorityQueue);
    } else {
      this.comparator = null;
      initFromCollection(paramCollection);
    } 
  }
  
  public PriorityQueue(PriorityQueue<? extends E> paramPriorityQueue) {
    this.comparator = paramPriorityQueue.comparator();
    initFromPriorityQueue(paramPriorityQueue);
  }
  
  public PriorityQueue(SortedSet<? extends E> paramSortedSet) {
    this.comparator = paramSortedSet.comparator();
    initElementsFromCollection(paramSortedSet);
  }
  
  private void initFromPriorityQueue(PriorityQueue<? extends E> paramPriorityQueue) {
    if (paramPriorityQueue.getClass() == PriorityQueue.class) {
      this.queue = paramPriorityQueue.toArray();
      this.size = paramPriorityQueue.size();
    } else {
      initFromCollection((Collection<? extends E>)paramPriorityQueue);
    } 
  }
  
  private void initElementsFromCollection(Collection<? extends E> paramCollection) {
    Object[] arrayOfObject = paramCollection.toArray();
    if (arrayOfObject.getClass() != Object[].class)
      arrayOfObject = Arrays.copyOf(arrayOfObject, arrayOfObject.length, Object[].class); 
    int i = arrayOfObject.length;
    if (i == 1 || this.comparator != null)
      for (byte b = 0; b < i; b++) {
        if (arrayOfObject[b] == null)
          throw new NullPointerException(); 
      }  
    this.queue = arrayOfObject;
    this.size = arrayOfObject.length;
  }
  
  private void initFromCollection(Collection<? extends E> paramCollection) {
    initElementsFromCollection(paramCollection);
    heapify();
  }
  
  private void grow(int paramInt) {
    int i = this.queue.length;
    int j = i + ((i < 64) ? (i + 2) : (i >> 1));
    if (j - 2147483639 > 0)
      j = hugeCapacity(paramInt); 
    this.queue = Arrays.copyOf(this.queue, j);
  }
  
  private static int hugeCapacity(int paramInt) {
    if (paramInt < 0)
      throw new OutOfMemoryError(); 
    return (paramInt > 2147483639) ? Integer.MAX_VALUE : 2147483639;
  }
  
  public boolean add(E paramE) {
    return offer(paramE);
  }
  
  public boolean offer(E paramE) {
    if (paramE == null)
      throw new NullPointerException(); 
    this.modCount++;
    int i = this.size;
    if (i >= this.queue.length)
      grow(i + 1); 
    this.size = i + 1;
    if (i == 0) {
      this.queue[0] = paramE;
    } else {
      siftUp(i, paramE);
    } 
    return true;
  }
  
  public E peek() {
    return (this.size == 0) ? null : (E)this.queue[0];
  }
  
  private int indexOf(Object paramObject) {
    if (paramObject != null)
      for (byte b = 0; b < this.size; b++) {
        if (paramObject.equals(this.queue[b]))
          return b; 
      }  
    return -1;
  }
  
  public boolean remove(Object paramObject) {
    int i = indexOf(paramObject);
    if (i == -1)
      return false; 
    removeAt(i);
    return true;
  }
  
  boolean removeEq(Object paramObject) {
    for (byte b = 0; b < this.size; b++) {
      if (paramObject == this.queue[b]) {
        removeAt(b);
        return true;
      } 
    } 
    return false;
  }
  
  public boolean contains(Object paramObject) {
    return (indexOf(paramObject) != -1);
  }
  
  public Object[] toArray() {
    return Arrays.copyOf(this.queue, this.size);
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    int i = this.size;
    if (paramArrayOfT.length < i)
      return (T[])Arrays.<Object, Object>copyOf(this.queue, i, (Class)paramArrayOfT.getClass()); 
    System.arraycopy(this.queue, 0, paramArrayOfT, 0, i);
    if (paramArrayOfT.length > i)
      paramArrayOfT[i] = null; 
    return paramArrayOfT;
  }
  
  public Iterator<E> iterator() {
    return new Itr();
  }
  
  static final class PriorityQueue {}
  
  private final class Itr implements Iterator<E> {
    private int cursor = 0;
    
    private int lastRet = -1;
    
    private ArrayDeque<E> forgetMeNot = null;
    
    private E lastRetElt = null;
    
    private int expectedModCount = PriorityQueue.this.modCount;
    
    public boolean hasNext() {
      return (this.cursor < PriorityQueue.this.size || (this.forgetMeNot != null && 
        !this.forgetMeNot.isEmpty()));
    }
    
    public E next() {
      if (this.expectedModCount != PriorityQueue.this.modCount)
        throw new ConcurrentModificationException(); 
      if (this.cursor < PriorityQueue.this.size)
        return (E)PriorityQueue.this.queue[this.lastRet = this.cursor++]; 
      if (this.forgetMeNot != null) {
        this.lastRet = -1;
        this.lastRetElt = this.forgetMeNot.poll();
        if (this.lastRetElt != null)
          return this.lastRetElt; 
      } 
      throw new NoSuchElementException();
    }
    
    public void remove() {
      if (this.expectedModCount != PriorityQueue.this.modCount)
        throw new ConcurrentModificationException(); 
      if (this.lastRet != -1) {
        Object object = PriorityQueue.this.removeAt(this.lastRet);
        this.lastRet = -1;
        if (object == null) {
          this.cursor--;
        } else {
          if (this.forgetMeNot == null)
            this.forgetMeNot = new ArrayDeque<>(); 
          this.forgetMeNot.add((E)object);
        } 
      } else if (this.lastRetElt != null) {
        PriorityQueue.this.removeEq(this.lastRetElt);
        this.lastRetElt = null;
      } else {
        throw new IllegalStateException();
      } 
      this.expectedModCount = PriorityQueue.this.modCount;
    }
    
    private Itr() {}
  }
  
  public int size() {
    return this.size;
  }
  
  public void clear() {
    this.modCount++;
    for (byte b = 0; b < this.size; b++)
      this.queue[b] = null; 
    this.size = 0;
  }
  
  public E poll() {
    if (this.size == 0)
      return null; 
    int i = --this.size;
    this.modCount++;
    Object object1 = this.queue[0];
    Object object2 = this.queue[i];
    this.queue[i] = null;
    if (i != 0)
      siftDown(0, (E)object2); 
    return (E)object1;
  }
  
  private E removeAt(int paramInt) {
    this.modCount++;
    int i = --this.size;
    if (i == paramInt) {
      this.queue[paramInt] = null;
    } else {
      Object object = this.queue[i];
      this.queue[i] = null;
      siftDown(paramInt, (E)object);
      if (this.queue[paramInt] == object) {
        siftUp(paramInt, (E)object);
        if (this.queue[paramInt] != object)
          return (E)object; 
      } 
    } 
    return null;
  }
  
  private void siftUp(int paramInt, E paramE) {
    if (this.comparator != null) {
      siftUpUsingComparator(paramInt, paramE);
    } else {
      siftUpComparable(paramInt, paramE);
    } 
  }
  
  private void siftUpComparable(int paramInt, E paramE) {
    Comparable<Object> comparable = (Comparable)paramE;
    while (paramInt > 0) {
      int i = paramInt - 1 >>> 1;
      Object object = this.queue[i];
      if (comparable.compareTo(object) >= 0)
        break; 
      this.queue[paramInt] = object;
      paramInt = i;
    } 
    this.queue[paramInt] = comparable;
  }
  
  private void siftUpUsingComparator(int paramInt, E paramE) {
    while (paramInt > 0) {
      int i = paramInt - 1 >>> 1;
      Object object = this.queue[i];
      if (this.comparator.compare(paramE, (E)object) >= 0)
        break; 
      this.queue[paramInt] = object;
      paramInt = i;
    } 
    this.queue[paramInt] = paramE;
  }
  
  private void siftDown(int paramInt, E paramE) {
    if (this.comparator != null) {
      siftDownUsingComparator(paramInt, paramE);
    } else {
      siftDownComparable(paramInt, paramE);
    } 
  }
  
  private void siftDownComparable(int paramInt, E paramE) {
    Comparable<Object> comparable = (Comparable)paramE;
    int i = this.size >>> 1;
    while (paramInt < i) {
      int j = (paramInt << 1) + 1;
      Object object = this.queue[j];
      int k = j + 1;
      if (k < this.size && ((Comparable<Object>)object)
        .compareTo(this.queue[k]) > 0)
        object = this.queue[j = k]; 
      if (comparable.compareTo(object) <= 0)
        break; 
      this.queue[paramInt] = object;
      paramInt = j;
    } 
    this.queue[paramInt] = comparable;
  }
  
  private void siftDownUsingComparator(int paramInt, E paramE) {
    int i = this.size >>> 1;
    while (paramInt < i) {
      int j = (paramInt << 1) + 1;
      Object object = this.queue[j];
      int k = j + 1;
      if (k < this.size && this.comparator
        .compare((E)object, (E)this.queue[k]) > 0)
        object = this.queue[j = k]; 
      if (this.comparator.compare(paramE, (E)object) <= 0)
        break; 
      this.queue[paramInt] = object;
      paramInt = j;
    } 
    this.queue[paramInt] = paramE;
  }
  
  private void heapify() {
    for (int i = (this.size >>> 1) - 1; i >= 0; i--)
      siftDown(i, (E)this.queue[i]); 
  }
  
  public Comparator<? super E> comparator() {
    return this.comparator;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(Math.max(2, this.size + 1));
    for (byte b = 0; b < this.size; b++)
      paramObjectOutputStream.writeObject(this.queue[b]); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    paramObjectInputStream.readInt();
    this.queue = new Object[this.size];
    for (byte b = 0; b < this.size; b++)
      this.queue[b] = paramObjectInputStream.readObject(); 
    heapify();
  }
  
  public final Spliterator<E> spliterator() {
    return new PriorityQueueSpliterator(this, 0, -1, 0);
  }
}
