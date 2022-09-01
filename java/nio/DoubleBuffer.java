package java.nio;

public abstract class DoubleBuffer extends Buffer implements Comparable<DoubleBuffer> {
  final double[] hb;
  
  final int offset;
  
  boolean isReadOnly;
  
  DoubleBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfdouble, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.hb = paramArrayOfdouble;
    this.offset = paramInt5;
  }
  
  DoubleBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this(paramInt1, paramInt2, paramInt3, paramInt4, (double[])null, 0);
  }
  
  public static DoubleBuffer allocate(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    return new HeapDoubleBuffer(paramInt, paramInt);
  }
  
  public static DoubleBuffer wrap(double[] paramArrayOfdouble, int paramInt1, int paramInt2) {
    try {
      return new HeapDoubleBuffer(paramArrayOfdouble, paramInt1, paramInt2);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IndexOutOfBoundsException();
    } 
  }
  
  public static DoubleBuffer wrap(double[] paramArrayOfdouble) {
    return wrap(paramArrayOfdouble, 0, paramArrayOfdouble.length);
  }
  
  public DoubleBuffer get(double[] paramArrayOfdouble, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfdouble.length);
    if (paramInt2 > remaining())
      throw new BufferUnderflowException(); 
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
      paramArrayOfdouble[j] = get(); 
    return this;
  }
  
  public DoubleBuffer get(double[] paramArrayOfdouble) {
    return get(paramArrayOfdouble, 0, paramArrayOfdouble.length);
  }
  
  public DoubleBuffer put(DoubleBuffer paramDoubleBuffer) {
    if (paramDoubleBuffer == this)
      throw new IllegalArgumentException(); 
    if (isReadOnly())
      throw new ReadOnlyBufferException(); 
    int i = paramDoubleBuffer.remaining();
    if (i > remaining())
      throw new BufferOverflowException(); 
    for (byte b = 0; b < i; b++)
      put(paramDoubleBuffer.get()); 
    return this;
  }
  
  public DoubleBuffer put(double[] paramArrayOfdouble, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfdouble.length);
    if (paramInt2 > remaining())
      throw new BufferOverflowException(); 
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
      put(paramArrayOfdouble[j]); 
    return this;
  }
  
  public final DoubleBuffer put(double[] paramArrayOfdouble) {
    return put(paramArrayOfdouble, 0, paramArrayOfdouble.length);
  }
  
  public final boolean hasArray() {
    return (this.hb != null && !this.isReadOnly);
  }
  
  public final double[] array() {
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
      i = 31 * i + (int)get(k); 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof DoubleBuffer))
      return false; 
    DoubleBuffer doubleBuffer = (DoubleBuffer)paramObject;
    if (remaining() != doubleBuffer.remaining())
      return false; 
    int i = position();
    for (int j = limit() - 1, k = doubleBuffer.limit() - 1; j >= i; j--, k--) {
      if (!equals(get(j), doubleBuffer.get(k)))
        return false; 
    } 
    return true;
  }
  
  private static boolean equals(double paramDouble1, double paramDouble2) {
    return (paramDouble1 == paramDouble2 || (Double.isNaN(paramDouble1) && Double.isNaN(paramDouble2)));
  }
  
  public int compareTo(DoubleBuffer paramDoubleBuffer) {
    int i = position() + Math.min(remaining(), paramDoubleBuffer.remaining());
    for (int j = position(), k = paramDoubleBuffer.position(); j < i; j++, k++) {
      int m = compare(get(j), paramDoubleBuffer.get(k));
      if (m != 0)
        return m; 
    } 
    return remaining() - paramDoubleBuffer.remaining();
  }
  
  private static int compare(double paramDouble1, double paramDouble2) {
    return (paramDouble1 < paramDouble2) ? -1 : ((paramDouble1 > paramDouble2) ? 1 : ((paramDouble1 == paramDouble2) ? 0 : (
      
      Double.isNaN(paramDouble1) ? (Double.isNaN(paramDouble2) ? 0 : 1) : -1)));
  }
  
  public abstract DoubleBuffer slice();
  
  public abstract DoubleBuffer duplicate();
  
  public abstract DoubleBuffer asReadOnlyBuffer();
  
  public abstract double get();
  
  public abstract DoubleBuffer put(double paramDouble);
  
  public abstract double get(int paramInt);
  
  public abstract DoubleBuffer put(int paramInt, double paramDouble);
  
  public abstract DoubleBuffer compact();
  
  public abstract boolean isDirect();
  
  public abstract ByteOrder order();
}
