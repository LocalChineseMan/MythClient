package java.nio;

public abstract class FloatBuffer extends Buffer implements Comparable<FloatBuffer> {
  final float[] hb;
  
  final int offset;
  
  boolean isReadOnly;
  
  FloatBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOffloat, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.hb = paramArrayOffloat;
    this.offset = paramInt5;
  }
  
  FloatBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this(paramInt1, paramInt2, paramInt3, paramInt4, (float[])null, 0);
  }
  
  public static FloatBuffer allocate(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    return new HeapFloatBuffer(paramInt, paramInt);
  }
  
  public static FloatBuffer wrap(float[] paramArrayOffloat, int paramInt1, int paramInt2) {
    try {
      return new HeapFloatBuffer(paramArrayOffloat, paramInt1, paramInt2);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IndexOutOfBoundsException();
    } 
  }
  
  public static FloatBuffer wrap(float[] paramArrayOffloat) {
    return wrap(paramArrayOffloat, 0, paramArrayOffloat.length);
  }
  
  public FloatBuffer get(float[] paramArrayOffloat, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOffloat.length);
    if (paramInt2 > remaining())
      throw new BufferUnderflowException(); 
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
      paramArrayOffloat[j] = get(); 
    return this;
  }
  
  public FloatBuffer get(float[] paramArrayOffloat) {
    return get(paramArrayOffloat, 0, paramArrayOffloat.length);
  }
  
  public FloatBuffer put(FloatBuffer paramFloatBuffer) {
    if (paramFloatBuffer == this)
      throw new IllegalArgumentException(); 
    if (isReadOnly())
      throw new ReadOnlyBufferException(); 
    int i = paramFloatBuffer.remaining();
    if (i > remaining())
      throw new BufferOverflowException(); 
    for (byte b = 0; b < i; b++)
      put(paramFloatBuffer.get()); 
    return this;
  }
  
  public FloatBuffer put(float[] paramArrayOffloat, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOffloat.length);
    if (paramInt2 > remaining())
      throw new BufferOverflowException(); 
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
      put(paramArrayOffloat[j]); 
    return this;
  }
  
  public final FloatBuffer put(float[] paramArrayOffloat) {
    return put(paramArrayOffloat, 0, paramArrayOffloat.length);
  }
  
  public final boolean hasArray() {
    return (this.hb != null && !this.isReadOnly);
  }
  
  public final float[] array() {
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
    if (!(paramObject instanceof FloatBuffer))
      return false; 
    FloatBuffer floatBuffer = (FloatBuffer)paramObject;
    if (remaining() != floatBuffer.remaining())
      return false; 
    int i = position();
    for (int j = limit() - 1, k = floatBuffer.limit() - 1; j >= i; j--, k--) {
      if (!equals(get(j), floatBuffer.get(k)))
        return false; 
    } 
    return true;
  }
  
  private static boolean equals(float paramFloat1, float paramFloat2) {
    return (paramFloat1 == paramFloat2 || (Float.isNaN(paramFloat1) && Float.isNaN(paramFloat2)));
  }
  
  public int compareTo(FloatBuffer paramFloatBuffer) {
    int i = position() + Math.min(remaining(), paramFloatBuffer.remaining());
    for (int j = position(), k = paramFloatBuffer.position(); j < i; j++, k++) {
      int m = compare(get(j), paramFloatBuffer.get(k));
      if (m != 0)
        return m; 
    } 
    return remaining() - paramFloatBuffer.remaining();
  }
  
  private static int compare(float paramFloat1, float paramFloat2) {
    return (paramFloat1 < paramFloat2) ? -1 : ((paramFloat1 > paramFloat2) ? 1 : ((paramFloat1 == paramFloat2) ? 0 : (
      
      Float.isNaN(paramFloat1) ? (Float.isNaN(paramFloat2) ? 0 : 1) : -1)));
  }
  
  public abstract FloatBuffer slice();
  
  public abstract FloatBuffer duplicate();
  
  public abstract FloatBuffer asReadOnlyBuffer();
  
  public abstract float get();
  
  public abstract FloatBuffer put(float paramFloat);
  
  public abstract float get(int paramInt);
  
  public abstract FloatBuffer put(int paramInt, float paramFloat);
  
  public abstract FloatBuffer compact();
  
  public abstract boolean isDirect();
  
  public abstract ByteOrder order();
}
