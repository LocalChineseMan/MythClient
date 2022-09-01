package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import sun.misc.Unsafe;

public class CopyOnWriteArrayList<E> implements List<E>, RandomAccess, Cloneable, Serializable {
  private static final long serialVersionUID = 8673264195747942595L;
  
  final transient ReentrantLock lock;
  
  private volatile transient Object[] array;
  
  private static final Unsafe UNSAFE;
  
  private static final long lockOffset;
  
  final Object[] getArray() {
    return this.array;
  }
  
  final void setArray(Object[] paramArrayOfObject) {
    this.array = paramArrayOfObject;
  }
  
  public CopyOnWriteArrayList() {
    this.lock = new ReentrantLock();
    setArray(new Object[0]);
  }
  
  public CopyOnWriteArrayList(Collection<? extends E> paramCollection) {
    Object[] arrayOfObject;
    this.lock = new ReentrantLock();
    if (paramCollection.getClass() == CopyOnWriteArrayList.class) {
      arrayOfObject = ((CopyOnWriteArrayList)paramCollection).getArray();
    } else {
      arrayOfObject = paramCollection.toArray();
      if (arrayOfObject.getClass() != Object[].class)
        arrayOfObject = Arrays.copyOf(arrayOfObject, arrayOfObject.length, Object[].class); 
    } 
    setArray(arrayOfObject);
  }
  
  public CopyOnWriteArrayList(E[] paramArrayOfE) {
    this.lock = new ReentrantLock();
    setArray(Arrays.copyOf(paramArrayOfE, paramArrayOfE.length, Object[].class));
  }
  
  public int size() {
    return (getArray()).length;
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  private static boolean eq(Object paramObject1, Object paramObject2) {
    return (paramObject1 == null) ? ((paramObject2 == null)) : paramObject1.equals(paramObject2);
  }
  
  private static int indexOf(Object paramObject, Object[] paramArrayOfObject, int paramInt1, int paramInt2) {
    if (paramObject == null) {
      for (int i = paramInt1; i < paramInt2; i++) {
        if (paramArrayOfObject[i] == null)
          return i; 
      } 
    } else {
      for (int i = paramInt1; i < paramInt2; i++) {
        if (paramObject.equals(paramArrayOfObject[i]))
          return i; 
      } 
    } 
    return -1;
  }
  
  private static int lastIndexOf(Object paramObject, Object[] paramArrayOfObject, int paramInt) {
    if (paramObject == null) {
      for (int i = paramInt; i >= 0; i--) {
        if (paramArrayOfObject[i] == null)
          return i; 
      } 
    } else {
      for (int i = paramInt; i >= 0; i--) {
        if (paramObject.equals(paramArrayOfObject[i]))
          return i; 
      } 
    } 
    return -1;
  }
  
  public boolean contains(Object paramObject) {
    Object[] arrayOfObject = getArray();
    return (indexOf(paramObject, arrayOfObject, 0, arrayOfObject.length) >= 0);
  }
  
  public int indexOf(Object paramObject) {
    Object[] arrayOfObject = getArray();
    return indexOf(paramObject, arrayOfObject, 0, arrayOfObject.length);
  }
  
  public int indexOf(E paramE, int paramInt) {
    Object[] arrayOfObject = getArray();
    return indexOf(paramE, arrayOfObject, paramInt, arrayOfObject.length);
  }
  
  public int lastIndexOf(Object paramObject) {
    Object[] arrayOfObject = getArray();
    return lastIndexOf(paramObject, arrayOfObject, arrayOfObject.length - 1);
  }
  
  public int lastIndexOf(E paramE, int paramInt) {
    Object[] arrayOfObject = getArray();
    return lastIndexOf(paramE, arrayOfObject, paramInt);
  }
  
  public Object clone() {
    try {
      CopyOnWriteArrayList copyOnWriteArrayList = (CopyOnWriteArrayList)super.clone();
      copyOnWriteArrayList.resetLock();
      return copyOnWriteArrayList;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError();
    } 
  }
  
  public Object[] toArray() {
    Object[] arrayOfObject = getArray();
    return Arrays.copyOf(arrayOfObject, arrayOfObject.length);
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    Object[] arrayOfObject = getArray();
    int i = arrayOfObject.length;
    if (paramArrayOfT.length < i)
      return (T[])Arrays.<Object, Object>copyOf(arrayOfObject, i, (Class)paramArrayOfT.getClass()); 
    System.arraycopy(arrayOfObject, 0, paramArrayOfT, 0, i);
    if (paramArrayOfT.length > i)
      paramArrayOfT[i] = null; 
    return paramArrayOfT;
  }
  
  private E get(Object[] paramArrayOfObject, int paramInt) {
    return (E)paramArrayOfObject[paramInt];
  }
  
  public E get(int paramInt) {
    return get(getArray(), paramInt);
  }
  
  public E set(int paramInt, E paramE) {
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = getArray();
      E e = get(arrayOfObject, paramInt);
      if (e != paramE) {
        int i = arrayOfObject.length;
        Object[] arrayOfObject1 = Arrays.copyOf(arrayOfObject, i);
        arrayOfObject1[paramInt] = paramE;
        setArray(arrayOfObject1);
      } else {
        setArray(arrayOfObject);
      } 
      return e;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean add(E paramE) {
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i + 1);
      arrayOfObject2[i] = paramE;
      setArray(arrayOfObject2);
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void add(int paramInt, E paramE) {
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject2, arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if (paramInt > i || paramInt < 0)
        throw new IndexOutOfBoundsException("Index: " + paramInt + ", Size: " + i); 
      int j = i - paramInt;
      if (j == 0) {
        arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i + 1);
      } else {
        arrayOfObject2 = new Object[i + 1];
        System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, paramInt);
        System.arraycopy(arrayOfObject1, paramInt, arrayOfObject2, paramInt + 1, j);
      } 
      arrayOfObject2[paramInt] = paramE;
      setArray(arrayOfObject2);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E remove(int paramInt) {
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = getArray();
      int i = arrayOfObject.length;
      E e = get(arrayOfObject, paramInt);
      int j = i - paramInt - 1;
      if (j == 0) {
        setArray(Arrays.copyOf(arrayOfObject, i - 1));
      } else {
        Object[] arrayOfObject1 = new Object[i - 1];
        System.arraycopy(arrayOfObject, 0, arrayOfObject1, 0, paramInt);
        System.arraycopy(arrayOfObject, paramInt + 1, arrayOfObject1, paramInt, j);
        setArray(arrayOfObject1);
      } 
      return e;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean remove(Object paramObject) {
    Object[] arrayOfObject = getArray();
    int i = indexOf(paramObject, arrayOfObject, 0, arrayOfObject.length);
    return (i < 0) ? false : remove(paramObject, arrayOfObject, i);
  }
  
  private boolean remove(Object paramObject, Object[] paramArrayOfObject, int paramInt) {
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if (paramArrayOfObject != arrayOfObject1) {
        int j = Math.min(paramInt, i);
        byte b;
        for (b = 0; b < j; b++) {
          if (arrayOfObject1[b] != paramArrayOfObject[b] && eq(paramObject, arrayOfObject1[b])) {
            paramInt = b;
            // Byte code: goto -> 135
          } 
        } 
        if (paramInt >= i) {
          b = 0;
          return b;
        } 
        if (arrayOfObject1[paramInt] != paramObject) {
          paramInt = indexOf(paramObject, arrayOfObject1, paramInt, i);
          if (paramInt < 0) {
            b = 0;
            return b;
          } 
        } 
      } 
      Object[] arrayOfObject2 = new Object[i - 1];
      System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, paramInt);
      System.arraycopy(arrayOfObject1, paramInt + 1, arrayOfObject2, paramInt, i - paramInt - 1);
      setArray(arrayOfObject2);
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  void removeRange(int paramInt1, int paramInt2) {
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = getArray();
      int i = arrayOfObject.length;
      if (paramInt1 < 0 || paramInt2 > i || paramInt2 < paramInt1)
        throw new IndexOutOfBoundsException(); 
      int j = i - paramInt2 - paramInt1;
      int k = i - paramInt2;
      if (k == 0) {
        setArray(Arrays.copyOf(arrayOfObject, j));
      } else {
        Object[] arrayOfObject1 = new Object[j];
        System.arraycopy(arrayOfObject, 0, arrayOfObject1, 0, paramInt1);
        System.arraycopy(arrayOfObject, paramInt2, arrayOfObject1, paramInt1, k);
        setArray(arrayOfObject1);
      } 
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean addIfAbsent(E paramE) {
    Object[] arrayOfObject = getArray();
    return (indexOf(paramE, arrayOfObject, 0, arrayOfObject.length) >= 0) ? false : 
      addIfAbsent(paramE, arrayOfObject);
  }
  
  private boolean addIfAbsent(E paramE, Object[] paramArrayOfObject) {
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if (paramArrayOfObject != arrayOfObject1) {
        int j = Math.min(paramArrayOfObject.length, i);
        byte b;
        for (b = 0; b < j; b++) {
          if (arrayOfObject1[b] != paramArrayOfObject[b] && eq(paramE, arrayOfObject1[b]))
            return false; 
        } 
        if (indexOf(paramE, arrayOfObject1, j, i) >= 0) {
          b = 0;
          return b;
        } 
      } 
      Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i + 1);
      arrayOfObject2[i] = paramE;
      setArray(arrayOfObject2);
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean containsAll(Collection<?> paramCollection) {
    Object[] arrayOfObject = getArray();
    int i = arrayOfObject.length;
    for (Object object : paramCollection) {
      if (indexOf(object, arrayOfObject, 0, i) < 0)
        return false; 
    } 
    return true;
  }
  
  public boolean removeAll(Collection<?> paramCollection) {
    if (paramCollection == null)
      throw new NullPointerException(); 
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = getArray();
      int i = arrayOfObject.length;
      if (i != 0) {
        byte b1 = 0;
        Object[] arrayOfObject1 = new Object[i];
        byte b2;
        for (b2 = 0; b2 < i; b2++) {
          Object object = arrayOfObject[b2];
          if (!paramCollection.contains(object))
            arrayOfObject1[b1++] = object; 
        } 
        if (b1 != i) {
          setArray(Arrays.copyOf(arrayOfObject1, b1));
          b2 = 1;
          return b2;
        } 
      } 
      return false;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean retainAll(Collection<?> paramCollection) {
    if (paramCollection == null)
      throw new NullPointerException(); 
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = getArray();
      int i = arrayOfObject.length;
      if (i != 0) {
        byte b1 = 0;
        Object[] arrayOfObject1 = new Object[i];
        byte b2;
        for (b2 = 0; b2 < i; b2++) {
          Object object = arrayOfObject[b2];
          if (paramCollection.contains(object))
            arrayOfObject1[b1++] = object; 
        } 
        if (b1 != i) {
          setArray(Arrays.copyOf(arrayOfObject1, b1));
          b2 = 1;
          return b2;
        } 
      } 
      return false;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int addAllAbsent(Collection<? extends E> paramCollection) {
    // Byte code:
    //   0: aload_1
    //   1: invokeinterface toArray : ()[Ljava/lang/Object;
    //   6: astore_2
    //   7: aload_2
    //   8: arraylength
    //   9: ifne -> 14
    //   12: iconst_0
    //   13: ireturn
    //   14: aload_0
    //   15: getfield lock : Ljava/util/concurrent/locks/ReentrantLock;
    //   18: astore_3
    //   19: aload_3
    //   20: invokevirtual lock : ()V
    //   23: aload_0
    //   24: invokevirtual getArray : ()[Ljava/lang/Object;
    //   27: astore #4
    //   29: aload #4
    //   31: arraylength
    //   32: istore #5
    //   34: iconst_0
    //   35: istore #6
    //   37: iconst_0
    //   38: istore #7
    //   40: iload #7
    //   42: aload_2
    //   43: arraylength
    //   44: if_icmpge -> 93
    //   47: aload_2
    //   48: iload #7
    //   50: aaload
    //   51: astore #8
    //   53: aload #8
    //   55: aload #4
    //   57: iconst_0
    //   58: iload #5
    //   60: invokestatic indexOf : (Ljava/lang/Object;[Ljava/lang/Object;II)I
    //   63: ifge -> 87
    //   66: aload #8
    //   68: aload_2
    //   69: iconst_0
    //   70: iload #6
    //   72: invokestatic indexOf : (Ljava/lang/Object;[Ljava/lang/Object;II)I
    //   75: ifge -> 87
    //   78: aload_2
    //   79: iload #6
    //   81: iinc #6, 1
    //   84: aload #8
    //   86: aastore
    //   87: iinc #7, 1
    //   90: goto -> 40
    //   93: iload #6
    //   95: ifle -> 127
    //   98: aload #4
    //   100: iload #5
    //   102: iload #6
    //   104: iadd
    //   105: invokestatic copyOf : ([Ljava/lang/Object;I)[Ljava/lang/Object;
    //   108: astore #7
    //   110: aload_2
    //   111: iconst_0
    //   112: aload #7
    //   114: iload #5
    //   116: iload #6
    //   118: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
    //   121: aload_0
    //   122: aload #7
    //   124: invokevirtual setArray : ([Ljava/lang/Object;)V
    //   127: iload #6
    //   129: istore #7
    //   131: aload_3
    //   132: invokevirtual unlock : ()V
    //   135: iload #7
    //   137: ireturn
    //   138: astore #9
    //   140: aload_3
    //   141: invokevirtual unlock : ()V
    //   144: aload #9
    //   146: athrow
    // Line number table:
    //   Java source line number -> byte code offset
    //   #763	-> 0
    //   #764	-> 7
    //   #765	-> 12
    //   #766	-> 14
    //   #767	-> 19
    //   #769	-> 23
    //   #770	-> 29
    //   #771	-> 34
    //   #773	-> 37
    //   #774	-> 47
    //   #775	-> 53
    //   #776	-> 72
    //   #777	-> 78
    //   #773	-> 87
    //   #779	-> 93
    //   #780	-> 98
    //   #781	-> 110
    //   #782	-> 121
    //   #784	-> 127
    //   #786	-> 131
    // Exception table:
    //   from	to	target	type
    //   23	131	138	finally
    //   138	140	138	finally
  }
  
  public void clear() {
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      setArray(new Object[0]);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean addAll(Collection<? extends E> paramCollection) {
    Object[] arrayOfObject = (paramCollection.getClass() == CopyOnWriteArrayList.class) ? ((CopyOnWriteArrayList)paramCollection).getArray() : paramCollection.toArray();
    if (arrayOfObject.length == 0)
      return false; 
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if (i == 0 && arrayOfObject.getClass() == Object[].class) {
        setArray(arrayOfObject);
      } else {
        Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i + arrayOfObject.length);
        System.arraycopy(arrayOfObject, 0, arrayOfObject2, i, arrayOfObject.length);
        setArray(arrayOfObject2);
      } 
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection) {
    Object[] arrayOfObject = paramCollection.toArray();
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject2, arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      if (paramInt > i || paramInt < 0)
        throw new IndexOutOfBoundsException("Index: " + paramInt + ", Size: " + i); 
      if (arrayOfObject.length == 0)
        return false; 
      int j = i - paramInt;
      if (j == 0) {
        arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i + arrayOfObject.length);
      } else {
        arrayOfObject2 = new Object[i + arrayOfObject.length];
        System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, paramInt);
        System.arraycopy(arrayOfObject1, paramInt, arrayOfObject2, paramInt + arrayOfObject.length, j);
      } 
      System.arraycopy(arrayOfObject, 0, arrayOfObject2, paramInt, arrayOfObject.length);
      setArray(arrayOfObject2);
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void forEach(Consumer<? super E> paramConsumer) {
    if (paramConsumer == null)
      throw new NullPointerException(); 
    Object[] arrayOfObject = getArray();
    int i = arrayOfObject.length;
    for (byte b = 0; b < i; b++) {
      Object object = arrayOfObject[b];
      paramConsumer.accept((E)object);
    } 
  }
  
  public boolean removeIf(Predicate<? super E> paramPredicate) {
    if (paramPredicate == null)
      throw new NullPointerException(); 
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = getArray();
      int i = arrayOfObject.length;
      if (i != 0) {
        byte b1 = 0;
        Object[] arrayOfObject1 = new Object[i];
        byte b2;
        for (b2 = 0; b2 < i; b2++) {
          Object object = arrayOfObject[b2];
          if (!paramPredicate.test((E)object))
            arrayOfObject1[b1++] = object; 
        } 
        if (b1 != i) {
          setArray(Arrays.copyOf(arrayOfObject1, b1));
          b2 = 1;
          return b2;
        } 
      } 
      return false;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void replaceAll(UnaryOperator<E> paramUnaryOperator) {
    if (paramUnaryOperator == null)
      throw new NullPointerException(); 
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject1 = getArray();
      int i = arrayOfObject1.length;
      Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, i);
      for (byte b = 0; b < i; b++) {
        Object object = arrayOfObject1[b];
        arrayOfObject2[b] = paramUnaryOperator.apply((E)object);
      } 
      setArray(arrayOfObject2);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void sort(Comparator<? super E> paramComparator) {
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject1 = getArray();
      Object[] arrayOfObject2 = Arrays.copyOf(arrayOfObject1, arrayOfObject1.length);
      Object[] arrayOfObject3 = arrayOfObject2;
      Arrays.sort(arrayOfObject3, (Comparator)paramComparator);
      setArray(arrayOfObject2);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    Object[] arrayOfObject = getArray();
    paramObjectOutputStream.writeInt(arrayOfObject.length);
    for (Object object : arrayOfObject)
      paramObjectOutputStream.writeObject(object); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    resetLock();
    int i = paramObjectInputStream.readInt();
    Object[] arrayOfObject = new Object[i];
    for (byte b = 0; b < i; b++)
      arrayOfObject[b] = paramObjectInputStream.readObject(); 
    setArray(arrayOfObject);
  }
  
  public String toString() {
    return Arrays.toString(getArray());
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof List))
      return false; 
    List list = (List)paramObject;
    Iterator iterator = list.iterator();
    Object[] arrayOfObject = getArray();
    int i = arrayOfObject.length;
    for (byte b = 0; b < i; b++) {
      if (!iterator.hasNext() || !eq(arrayOfObject[b], iterator.next()))
        return false; 
    } 
    if (iterator.hasNext())
      return false; 
    return true;
  }
  
  public int hashCode() {
    int i = 1;
    Object[] arrayOfObject = getArray();
    int j = arrayOfObject.length;
    for (byte b = 0; b < j; b++) {
      Object object = arrayOfObject[b];
      i = 31 * i + ((object == null) ? 0 : object.hashCode());
    } 
    return i;
  }
  
  public Iterator<E> iterator() {
    return new COWIterator<>(getArray(), 0);
  }
  
  public ListIterator<E> listIterator() {
    return new COWIterator<>(getArray(), 0);
  }
  
  public ListIterator<E> listIterator(int paramInt) {
    Object[] arrayOfObject = getArray();
    int i = arrayOfObject.length;
    if (paramInt < 0 || paramInt > i)
      throw new IndexOutOfBoundsException("Index: " + paramInt); 
    return new COWIterator<>(arrayOfObject, paramInt);
  }
  
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(getArray(), 1040);
  }
  
  private static class CopyOnWriteArrayList {}
  
  private static class CopyOnWriteArrayList {}
  
  static final class COWIterator<E> implements ListIterator<E> {
    private final Object[] snapshot;
    
    private int cursor;
    
    private COWIterator(Object[] param1ArrayOfObject, int param1Int) {
      this.cursor = param1Int;
      this.snapshot = param1ArrayOfObject;
    }
    
    public boolean hasNext() {
      return (this.cursor < this.snapshot.length);
    }
    
    public boolean hasPrevious() {
      return (this.cursor > 0);
    }
    
    public E next() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      return (E)this.snapshot[this.cursor++];
    }
    
    public E previous() {
      if (!hasPrevious())
        throw new NoSuchElementException(); 
      return (E)this.snapshot[--this.cursor];
    }
    
    public int nextIndex() {
      return this.cursor;
    }
    
    public int previousIndex() {
      return this.cursor - 1;
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
    
    public void set(E param1E) {
      throw new UnsupportedOperationException();
    }
    
    public void add(E param1E) {
      throw new UnsupportedOperationException();
    }
    
    public void forEachRemaining(Consumer<? super E> param1Consumer) {
      Objects.requireNonNull(param1Consumer);
      Object[] arrayOfObject = this.snapshot;
      int i = arrayOfObject.length;
      for (int j = this.cursor; j < i; j++) {
        Object object = arrayOfObject[j];
        param1Consumer.accept((E)object);
      } 
      this.cursor = i;
    }
  }
  
  public List<E> subList(int paramInt1, int paramInt2) {
    ReentrantLock reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = getArray();
      int i = arrayOfObject.length;
      if (paramInt1 < 0 || paramInt2 > i || paramInt1 > paramInt2)
        throw new IndexOutOfBoundsException(); 
      return new COWSubList(this, paramInt1, paramInt2);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  private void resetLock() {
    UNSAFE.putObjectVolatile(this, lockOffset, new ReentrantLock());
  }
  
  static {
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class<CopyOnWriteArrayList> clazz = CopyOnWriteArrayList.class;
      lockOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("lock"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
}
