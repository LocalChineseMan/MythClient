package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;
import sun.misc.Unsafe;

public class Random implements Serializable {
  static final long serialVersionUID = 3905348978240129619L;
  
  private final AtomicLong seed;
  
  private static final long multiplier = 25214903917L;
  
  private static final long addend = 11L;
  
  private static final long mask = 281474976710655L;
  
  private static final double DOUBLE_UNIT = 1.1102230246251565E-16D;
  
  static final String BadBound = "bound must be positive";
  
  static final String BadRange = "bound must be greater than origin";
  
  static final String BadSize = "size must be non-negative";
  
  public Random() {
    this(seedUniquifier() ^ System.nanoTime());
  }
  
  private static long seedUniquifier() {
    while (true) {
      long l1 = seedUniquifier.get();
      long l2 = l1 * 181783497276652981L;
      if (seedUniquifier.compareAndSet(l1, l2))
        return l2; 
    } 
  }
  
  private static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);
  
  private double nextNextGaussian;
  
  private boolean haveNextNextGaussian;
  
  public Random(long paramLong) {
    this.haveNextNextGaussian = false;
    if (getClass() == Random.class) {
      this.seed = new AtomicLong(initialScramble(paramLong));
    } else {
      this.seed = new AtomicLong();
      setSeed(paramLong);
    } 
  }
  
  private static long initialScramble(long paramLong) {
    return (paramLong ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL;
  }
  
  public synchronized void setSeed(long paramLong) {
    this.seed.set(initialScramble(paramLong));
    this.haveNextNextGaussian = false;
  }
  
  protected int next(int paramInt) {
    AtomicLong atomicLong = this.seed;
    while (true) {
      long l1 = atomicLong.get();
      long l2 = l1 * 25214903917L + 11L & 0xFFFFFFFFFFFFL;
      if (atomicLong.compareAndSet(l1, l2))
        return (int)(l2 >>> 48 - paramInt); 
    } 
  }
  
  public void nextBytes(byte[] paramArrayOfbyte) {
    byte b;
    int i;
    for (b = 0, i = paramArrayOfbyte.length; b < i; ) {
      int j = nextInt();
      int k = Math.min(i - b, 4);
      for (; k-- > 0; j >>= 8)
        paramArrayOfbyte[b++] = (byte)j; 
    } 
  }
  
  final long internalNextLong(long paramLong1, long paramLong2) {
    long l = nextLong();
    if (paramLong1 < paramLong2) {
      long l1 = paramLong2 - paramLong1, l2 = l1 - 1L;
      if ((l1 & l2) == 0L) {
        l = (l & l2) + paramLong1;
      } else if (l1 > 0L) {
        long l3 = l >>> 1L;
        while (l3 + l2 - (l = l3 % l1) < 0L)
          l3 = nextLong() >>> 1L; 
        l += paramLong1;
      } else {
        while (l < paramLong1 || l >= paramLong2)
          l = nextLong(); 
      } 
    } 
    return l;
  }
  
  final int internalNextInt(int paramInt1, int paramInt2) {
    if (paramInt1 < paramInt2) {
      int i = paramInt2 - paramInt1;
      if (i > 0)
        return nextInt(i) + paramInt1; 
      while (true) {
        int j = nextInt();
        if (j >= paramInt1 && j < paramInt2)
          return j; 
      } 
    } 
    return nextInt();
  }
  
  final double internalNextDouble(double paramDouble1, double paramDouble2) {
    double d = nextDouble();
    if (paramDouble1 < paramDouble2) {
      d = d * (paramDouble2 - paramDouble1) + paramDouble1;
      if (d >= paramDouble2)
        d = Double.longBitsToDouble(Double.doubleToLongBits(paramDouble2) - 1L); 
    } 
    return d;
  }
  
  public int nextInt() {
    return next(32);
  }
  
  public int nextInt(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("bound must be positive"); 
    int i = next(31);
    int j = paramInt - 1;
    if ((paramInt & j) == 0) {
      i = (int)(paramInt * i >> 31L);
    } else {
      int k = i;
      while (k - (i = k % paramInt) + j < 0)
        k = next(31); 
    } 
    return i;
  }
  
  public long nextLong() {
    return (next(32) << 32L) + next(32);
  }
  
  public boolean nextBoolean() {
    return (next(1) != 0);
  }
  
  public float nextFloat() {
    return next(24) / 1.6777216E7F;
  }
  
  public double nextDouble() {
    return ((next(26) << 27L) + next(27)) * 1.1102230246251565E-16D;
  }
  
  public synchronized double nextGaussian() {
    if (this.haveNextNextGaussian) {
      this.haveNextNextGaussian = false;
      return this.nextNextGaussian;
    } 
    while (true) {
      double d1 = 2.0D * nextDouble() - 1.0D;
      double d2 = 2.0D * nextDouble() - 1.0D;
      double d3 = d1 * d1 + d2 * d2;
      if (d3 < 1.0D && d3 != 0.0D) {
        double d = StrictMath.sqrt(-2.0D * StrictMath.log(d3) / d3);
        this.nextNextGaussian = d2 * d;
        this.haveNextNextGaussian = true;
        return d1 * d;
      } 
    } 
  }
  
  public IntStream ints(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    return StreamSupport.intStream((Spliterator.OfInt)new RandomIntsSpliterator(this, 0L, paramLong, 2147483647, 0), false);
  }
  
  public IntStream ints() {
    return StreamSupport.intStream((Spliterator.OfInt)new RandomIntsSpliterator(this, 0L, Long.MAX_VALUE, 2147483647, 0), false);
  }
  
  public IntStream ints(long paramLong, int paramInt1, int paramInt2) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    if (paramInt1 >= paramInt2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.intStream((Spliterator.OfInt)new RandomIntsSpliterator(this, 0L, paramLong, paramInt1, paramInt2), false);
  }
  
  public IntStream ints(int paramInt1, int paramInt2) {
    if (paramInt1 >= paramInt2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.intStream((Spliterator.OfInt)new RandomIntsSpliterator(this, 0L, Long.MAX_VALUE, paramInt1, paramInt2), false);
  }
  
  public LongStream longs(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    return StreamSupport.longStream((Spliterator.OfLong)new RandomLongsSpliterator(this, 0L, paramLong, Long.MAX_VALUE, 0L), false);
  }
  
  public LongStream longs() {
    return StreamSupport.longStream((Spliterator.OfLong)new RandomLongsSpliterator(this, 0L, Long.MAX_VALUE, Long.MAX_VALUE, 0L), false);
  }
  
  public LongStream longs(long paramLong1, long paramLong2, long paramLong3) {
    if (paramLong1 < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    if (paramLong2 >= paramLong3)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.longStream((Spliterator.OfLong)new RandomLongsSpliterator(this, 0L, paramLong1, paramLong2, paramLong3), false);
  }
  
  public LongStream longs(long paramLong1, long paramLong2) {
    if (paramLong1 >= paramLong2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.longStream((Spliterator.OfLong)new RandomLongsSpliterator(this, 0L, Long.MAX_VALUE, paramLong1, paramLong2), false);
  }
  
  public DoubleStream doubles(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    return StreamSupport.doubleStream((Spliterator.OfDouble)new RandomDoublesSpliterator(this, 0L, paramLong, Double.MAX_VALUE, 0.0D), false);
  }
  
  public DoubleStream doubles() {
    return StreamSupport.doubleStream((Spliterator.OfDouble)new RandomDoublesSpliterator(this, 0L, Long.MAX_VALUE, Double.MAX_VALUE, 0.0D), false);
  }
  
  public DoubleStream doubles(long paramLong, double paramDouble1, double paramDouble2) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    if (paramDouble1 >= paramDouble2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.doubleStream((Spliterator.OfDouble)new RandomDoublesSpliterator(this, 0L, paramLong, paramDouble1, paramDouble2), false);
  }
  
  public DoubleStream doubles(double paramDouble1, double paramDouble2) {
    if (paramDouble1 >= paramDouble2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.doubleStream((Spliterator.OfDouble)new RandomDoublesSpliterator(this, 0L, Long.MAX_VALUE, paramDouble1, paramDouble2), false);
  }
  
  private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("seed", long.class), new ObjectStreamField("nextNextGaussian", double.class), new ObjectStreamField("haveNextNextGaussian", boolean.class) };
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    long l = getField.get("seed", -1L);
    if (l < 0L)
      throw new StreamCorruptedException("Random: invalid seed"); 
    resetSeed(l);
    this.nextNextGaussian = getField.get("nextNextGaussian", 0.0D);
    this.haveNextNextGaussian = getField.get("haveNextNextGaussian", false);
  }
  
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("seed", this.seed.get());
    putField.put("nextNextGaussian", this.nextNextGaussian);
    putField.put("haveNextNextGaussian", this.haveNextNextGaussian);
    paramObjectOutputStream.writeFields();
  }
  
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final long seedOffset;
  
  static {
    try {
      seedOffset = unsafe.objectFieldOffset(Random.class.getDeclaredField("seed"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  private void resetSeed(long paramLong) {
    unsafe.putObjectVolatile(this, seedOffset, new AtomicLong(paramLong));
  }
  
  static final class Random {}
  
  static final class Random {}
  
  static final class Random {}
}
