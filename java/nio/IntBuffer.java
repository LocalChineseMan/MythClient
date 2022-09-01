package java.nio;

public abstract class IntBuffer extends Buffer implements Comparable<IntBuffer> {
  final int[] hb;
  
  final int offset;
  
  boolean isReadOnly;
  
  IntBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.hb = paramArrayOfint;
    this.offset = paramInt5;
  }
  
  IntBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this(paramInt1, paramInt2, paramInt3, paramInt4, (int[])null, 0);
  }
  
  public static IntBuffer allocate(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    return new HeapIntBuffer(paramInt, paramInt);
  }
  
  public static IntBuffer wrap(int[] paramArrayOfint, int paramInt1, int paramInt2) {
    try {
      return new HeapIntBuffer(paramArrayOfint, paramInt1, paramInt2);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IndexOutOfBoundsException();
    } 
  }
  
  public static IntBuffer wrap(int[] paramArrayOfint) {
    return wrap(paramArrayOfint, 0, paramArrayOfint.length);
  }
  
  public IntBuffer get(int[] paramArrayOfint, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfint.length);
    if (paramInt2 > remaining())
      throw new BufferUnderflowException(); 
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
      paramArrayOfint[j] = get(); 
    return this;
  }
  
  public IntBuffer get(int[] paramArrayOfint) {
    return get(paramArrayOfint, 0, paramArrayOfint.length);
  }
  
  public IntBuffer put(IntBuffer paramIntBuffer) {
    if (paramIntBuffer == this)
      throw new IllegalArgumentException(); 
    if (isReadOnly())
      throw new ReadOnlyBufferException(); 
    int i = paramIntBuffer.remaining();
    if (i > remaining())
      throw new BufferOverflowException(); 
    for (byte b = 0; b < i; b++)
      put(paramIntBuffer.get()); 
    return this;
  }
  
  public IntBuffer put(int[] paramArrayOfint, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfint.length);
    if (paramInt2 > remaining())
      throw new BufferOverflowException(); 
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
      put(paramArrayOfint[j]); 
    return this;
  }
  
  public final IntBuffer put(int[] paramArrayOfint) {
    return put(paramArrayOfint, 0, paramArrayOfint.length);
  }
  
  public final boolean hasArray() {
    return (this.hb != null && !this.isReadOnly);
  }
  
  public final int[] array() {
    if (this.hb == null)
      throw new UnsupportedOperationException(); 
    if (this.isReadOnly)
      throw new ReadOnlyBufferException(); 
    return this.hb;
  }
  
  public final int arrayOffset() {
    if (this.hb == null)
      throw new UnsupportedOperationException(); 
    if (this.isReadOnly)
      throw new ReadOnlyBufferException(); 
    return this.offset;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(getClass().getName());
    stringBuffer.append("[pos=");
    stringBuffer.append(position());
    stringBuffer.append(" lim=");
    stringBuffer.append(limit());
    stringBuffer.append(" cap=");
    stringBuffer.append(capacity());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  public int hashCode() {
    int i = 1;
    int j = position();
    for (int k = limit() - 1; k >= j; k--)
      i = 31 * i + get(k); 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof IntBuffer))
      return false; 
    IntBuffer intBuffer = (IntBuffer)paramObject;
    if (remaining() != intBuffer.remaining())
      return false; 
    int i = position();
    for (int j = limit() - 1, k = intBuffer.limit() - 1; j >= i; j--, k--) {
      if (!equals(get(j), intBuffer.get(k)))
        return false; 
    } 
    return true;
  }
  
  private static boolean equals(int paramInt1, int paramInt2) {
    return (paramInt1 == paramInt2);
  }
  
  public int compareTo(IntBuffer paramIntBuffer) {
    int i = position() + Math.min(remaining(), paramIntBuffer.remaining());
    for (int j = position(), k = paramIntBuffer.position(); j < i; j++, k++) {
      int m = compare(get(j), paramIntBuffer.get(k));
      if (m != 0)
        return m; 
    } 
    return remaining() - paramIntBuffer.remaining();
  }
  
  private static int compare(int paramInt1, int paramInt2) {
    return Integer.compare(paramInt1, paramInt2);
  }
  
  public abstract IntBuffer slice();
  
  public abstract IntBuffer duplicate();
  
  public abstract IntBuffer asReadOnlyBuffer();
  
  public abstract int get();
  
  public abstract IntBuffer put(int paramInt);
  
  public abstract int get(int paramInt);
  
  public abstract IntBuffer put(int paramInt1, int paramInt2);
  
  public abstract IntBuffer compact();
  
  public abstract boolean isDirect();
  
  public abstract ByteOrder order();
}
