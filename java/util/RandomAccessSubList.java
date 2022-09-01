package java.util;

class RandomAccessSubList<E> extends SubList<E> implements RandomAccess {
  RandomAccessSubList(AbstractList<E> paramAbstractList, int paramInt1, int paramInt2) {
    super(paramAbstractList, paramInt1, paramInt2);
  }
  
  public List<E> subList(int paramInt1, int paramInt2) {
    return new RandomAccessSubList(this, paramInt1, paramInt2);
  }
}
