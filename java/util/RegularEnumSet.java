package java.util;

class RegularEnumSet<E extends Enum<E>> extends EnumSet<E> {
  private static final long serialVersionUID = 3411599620347842686L;
  
  private long elements = 0L;
  
  RegularEnumSet(Class<E> paramClass, Enum<?>[] paramArrayOfEnum) {
    super(paramClass, paramArrayOfEnum);
  }
  
  void addRange(E paramE1, E paramE2) {
    this.elements = -1L >>> paramE1.ordinal() - paramE2.ordinal() - 1 << paramE1.ordinal();
  }
  
  void addAll() {
    if (this.universe.length != 0)
      this.elements = -1L >>> -this.universe.length; 
  }
  
  void complement() {
    if (this.universe.length != 0) {
      this.elements ^= 0xFFFFFFFFFFFFFFFFL;
      this.elements &= -1L >>> -this.universe.length;
    } 
  }
  
  public Iterator<E> iterator() {
    return new EnumSetIterator<>();
  }
  
  private class EnumSetIterator<E extends Enum<E>> implements Iterator<E> {
    long unseen;
    
    long lastReturned = 0L;
    
    EnumSetIterator() {
      this.unseen = RegularEnumSet.this.elements;
    }
    
    public boolean hasNext() {
      return (this.unseen != 0L);
    }
    
    public E next() {
      if (this.unseen == 0L)
        throw new NoSuchElementException(); 
      this.lastReturned = this.unseen & -this.unseen;
      this.unseen -= this.lastReturned;
      return (E)RegularEnumSet.this.universe[Long.numberOfTrailingZeros(this.lastReturned)];
    }
    
    public void remove() {
      if (this.lastReturned == 0L)
        throw new IllegalStateException(); 
      RegularEnumSet.this.elements = RegularEnumSet.this.elements & (this.lastReturned ^ 0xFFFFFFFFFFFFFFFFL);
      this.lastReturned = 0L;
    }
  }
  
  public int size() {
    return Long.bitCount(this.elements);
  }
  
  public boolean isEmpty() {
    return (this.elements == 0L);
  }
  
  public boolean contains(Object paramObject) {
    if (paramObject == null)
      return false; 
    Class<?> clazz = paramObject.getClass();
    if (clazz != this.elementType && clazz.getSuperclass() != this.elementType)
      return false; 
    return ((this.elements & 1L << ((Enum)paramObject).ordinal()) != 0L);
  }
  
  public boolean add(E paramE) {
    typeCheck(paramE);
    long l = this.elements;
    this.elements |= 1L << paramE.ordinal();
    return (this.elements != l);
  }
  
  public boolean remove(Object paramObject) {
    if (paramObject == null)
      return false; 
    Class<?> clazz = paramObject.getClass();
    if (clazz != this.elementType && clazz.getSuperclass() != this.elementType)
      return false; 
    long l = this.elements;
    this.elements &= 1L << ((Enum)paramObject).ordinal() ^ 0xFFFFFFFFFFFFFFFFL;
    return (this.elements != l);
  }
  
  public boolean containsAll(Collection<?> paramCollection) {
    if (!(paramCollection instanceof RegularEnumSet))
      return super.containsAll(paramCollection); 
    RegularEnumSet regularEnumSet = (RegularEnumSet)paramCollection;
    if (regularEnumSet.elementType != this.elementType)
      return regularEnumSet.isEmpty(); 
    return ((regularEnumSet.elements & (this.elements ^ 0xFFFFFFFFFFFFFFFFL)) == 0L);
  }
  
  public boolean addAll(Collection<? extends E> paramCollection) {
    if (!(paramCollection instanceof RegularEnumSet))
      return super.addAll(paramCollection); 
    RegularEnumSet regularEnumSet = (RegularEnumSet)paramCollection;
    if (regularEnumSet.elementType != this.elementType) {
      if (regularEnumSet.isEmpty())
        return false; 
      throw new ClassCastException(regularEnumSet.elementType + " != " + this.elementType);
    } 
    long l = this.elements;
    this.elements |= regularEnumSet.elements;
    return (this.elements != l);
  }
  
  public boolean removeAll(Collection<?> paramCollection) {
    if (!(paramCollection instanceof RegularEnumSet))
      return super.removeAll(paramCollection); 
    RegularEnumSet regularEnumSet = (RegularEnumSet)paramCollection;
    if (regularEnumSet.elementType != this.elementType)
      return false; 
    long l = this.elements;
    this.elements &= regularEnumSet.elements ^ 0xFFFFFFFFFFFFFFFFL;
    return (this.elements != l);
  }
  
  public boolean retainAll(Collection<?> paramCollection) {
    if (!(paramCollection instanceof RegularEnumSet))
      return super.retainAll(paramCollection); 
    RegularEnumSet regularEnumSet = (RegularEnumSet)paramCollection;
    if (regularEnumSet.elementType != this.elementType) {
      boolean bool = (this.elements != 0L) ? true : false;
      this.elements = 0L;
      return bool;
    } 
    long l = this.elements;
    this.elements &= regularEnumSet.elements;
    return (this.elements != l);
  }
  
  public void clear() {
    this.elements = 0L;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof RegularEnumSet))
      return super.equals(paramObject); 
    RegularEnumSet regularEnumSet = (RegularEnumSet)paramObject;
    if (regularEnumSet.elementType != this.elementType)
      return (this.elements == 0L && regularEnumSet.elements == 0L); 
    return (regularEnumSet.elements == this.elements);
  }
}
