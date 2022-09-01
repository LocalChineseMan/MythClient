package java.math;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Arrays;

public class BigDecimal extends Number implements Comparable<BigDecimal> {
  private final BigInteger intVal;
  
  private final int scale;
  
  private transient int precision;
  
  private transient String stringCache;
  
  static final long INFLATED = -9223372036854775808L;
  
  private static final BigInteger INFLATED_BIGINT = BigInteger.valueOf(Long.MIN_VALUE);
  
  private final transient long intCompact;
  
  private static final int MAX_COMPACT_DIGITS = 18;
  
  private static final long serialVersionUID = 6108874887143696463L;
  
  private static final ThreadLocal<StringBuilderHelper> threadLocalStringBuilderHelper = (ThreadLocal<StringBuilderHelper>)new Object();
  
  private static final BigDecimal[] zeroThroughTen = new BigDecimal[] { 
      new BigDecimal(BigInteger.ZERO, 0L, 0, 1), new BigDecimal(BigInteger.ONE, 1L, 0, 1), new BigDecimal(
        
        BigInteger.valueOf(2L), 2L, 0, 1), new BigDecimal(
        BigInteger.valueOf(3L), 3L, 0, 1), new BigDecimal(
        BigInteger.valueOf(4L), 4L, 0, 1), new BigDecimal(
        BigInteger.valueOf(5L), 5L, 0, 1), new BigDecimal(
        BigInteger.valueOf(6L), 6L, 0, 1), new BigDecimal(
        BigInteger.valueOf(7L), 7L, 0, 1), new BigDecimal(
        BigInteger.valueOf(8L), 8L, 0, 1), new BigDecimal(
        BigInteger.valueOf(9L), 9L, 0, 1), 
      new BigDecimal(BigInteger.TEN, 10L, 0, 2) };
  
  private static final BigDecimal[] ZERO_SCALED_BY = new BigDecimal[] { 
      zeroThroughTen[0], new BigDecimal(BigInteger.ZERO, 0L, 1, 1), new BigDecimal(BigInteger.ZERO, 0L, 2, 1), new BigDecimal(BigInteger.ZERO, 0L, 3, 1), new BigDecimal(BigInteger.ZERO, 0L, 4, 1), new BigDecimal(BigInteger.ZERO, 0L, 5, 1), new BigDecimal(BigInteger.ZERO, 0L, 6, 1), new BigDecimal(BigInteger.ZERO, 0L, 7, 1), new BigDecimal(BigInteger.ZERO, 0L, 8, 1), new BigDecimal(BigInteger.ZERO, 0L, 9, 1), 
      new BigDecimal(BigInteger.ZERO, 0L, 10, 1), new BigDecimal(BigInteger.ZERO, 0L, 11, 1), new BigDecimal(BigInteger.ZERO, 0L, 12, 1), new BigDecimal(BigInteger.ZERO, 0L, 13, 1), new BigDecimal(BigInteger.ZERO, 0L, 14, 1), new BigDecimal(BigInteger.ZERO, 0L, 15, 1) };
  
  private static final long HALF_LONG_MAX_VALUE = 4611686018427387903L;
  
  private static final long HALF_LONG_MIN_VALUE = -4611686018427387904L;
  
  public static final BigDecimal ZERO = zeroThroughTen[0];
  
  public static final BigDecimal ONE = zeroThroughTen[1];
  
  public static final BigDecimal TEN = zeroThroughTen[10];
  
  public static final int ROUND_UP = 0;
  
  public static final int ROUND_DOWN = 1;
  
  public static final int ROUND_CEILING = 2;
  
  public static final int ROUND_FLOOR = 3;
  
  public static final int ROUND_HALF_UP = 4;
  
  public static final int ROUND_HALF_DOWN = 5;
  
  public static final int ROUND_HALF_EVEN = 6;
  
  public static final int ROUND_UNNECESSARY = 7;
  
  BigDecimal(BigInteger paramBigInteger, long paramLong, int paramInt1, int paramInt2) {
    this.scale = paramInt1;
    this.precision = paramInt2;
    this.intCompact = paramLong;
    this.intVal = paramBigInteger;
  }
  
  public BigDecimal(char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    this(paramArrayOfchar, paramInt1, paramInt2, MathContext.UNLIMITED);
  }
  
  public BigDecimal(char[] paramArrayOfchar, int paramInt1, int paramInt2, MathContext paramMathContext) {
    if (paramInt1 + paramInt2 > paramArrayOfchar.length || paramInt1 < 0)
      throw new NumberFormatException("Bad offset or len arguments for char[] input."); 
    int i = 0;
    int j = 0;
    long l = 0L;
    BigInteger bigInteger = null;
    try {
      boolean bool1 = false;
      if (paramArrayOfchar[paramInt1] == '-') {
        bool1 = true;
        paramInt1++;
        paramInt2--;
      } else if (paramArrayOfchar[paramInt1] == '+') {
        paramInt1++;
        paramInt2--;
      } 
      boolean bool2 = false;
      long l1 = 0L;
      boolean bool3 = (paramInt2 <= 18) ? true : false;
      byte b = 0;
      if (bool3) {
        for (; paramInt2 > 0; paramInt1++, paramInt2--) {
          char c = paramArrayOfchar[paramInt1];
          if (c == '0') {
            if (!i) {
              i = 1;
            } else if (l != 0L) {
              l *= 10L;
              i++;
            } 
            if (bool2)
              j++; 
          } else if (c >= '1' && c <= '9') {
            int n = c - 48;
            if (i != 1 || l != 0L)
              i++; 
            l = l * 10L + n;
            if (bool2)
              j++; 
          } else if (c == '.') {
            if (bool2)
              throw new NumberFormatException(); 
            bool2 = true;
          } else if (Character.isDigit(c)) {
            int n = Character.digit(c, 10);
            if (n == 0) {
              if (i == 0) {
                i = 1;
              } else if (l != 0L) {
                l *= 10L;
                i++;
              } 
            } else {
              if (i != 1 || l != 0L)
                i++; 
              l = l * 10L + n;
            } 
            if (bool2)
              j++; 
          } else {
            if (c == 'e' || c == 'E') {
              l1 = parseExp(paramArrayOfchar, paramInt1, paramInt2);
              if ((int)l1 != l1)
                throw new NumberFormatException(); 
              break;
            } 
            throw new NumberFormatException();
          } 
        } 
        if (i == 0)
          throw new NumberFormatException(); 
        if (l1 != 0L)
          j = adjustScale(j, l1); 
        l = bool1 ? -l : l;
        int k = paramMathContext.precision;
        int m = i - k;
        if (k > 0 && m > 0)
          while (m > 0) {
            j = checkScaleNonZero(j - m);
            l = divideAndRound(l, LONG_TEN_POWERS_TABLE[m], paramMathContext.roundingMode.oldMode);
            i = longDigitLength(l);
            m = i - k;
          }  
      } else {
        char[] arrayOfChar = new char[paramInt2];
        for (; paramInt2 > 0; paramInt1++, paramInt2--) {
          char c = paramArrayOfchar[paramInt1];
          if ((c >= '0' && c <= '9') || Character.isDigit(c)) {
            if (c == '0' || Character.digit(c, 10) == 0) {
              if (i == 0) {
                arrayOfChar[b] = c;
                i = 1;
              } else if (b) {
                arrayOfChar[b++] = c;
                i++;
              } 
            } else {
              if (i != 1 || b != 0)
                i++; 
              arrayOfChar[b++] = c;
            } 
            if (bool2)
              j++; 
          } else if (c == '.') {
            if (bool2)
              throw new NumberFormatException(); 
            bool2 = true;
          } else {
            if (c != 'e' && c != 'E')
              throw new NumberFormatException(); 
            l1 = parseExp(paramArrayOfchar, paramInt1, paramInt2);
            if ((int)l1 != l1)
              throw new NumberFormatException(); 
            break;
          } 
        } 
        if (i == 0)
          throw new NumberFormatException(); 
        if (l1 != 0L)
          j = adjustScale(j, l1); 
        bigInteger = new BigInteger(arrayOfChar, bool1 ? -1 : 1, i);
        l = compactValFor(bigInteger);
        int k = paramMathContext.precision;
        if (k > 0 && i > k) {
          if (l == Long.MIN_VALUE) {
            int m = i - k;
            while (m > 0) {
              j = checkScaleNonZero(j - m);
              bigInteger = divideAndRoundByTenPow(bigInteger, m, paramMathContext.roundingMode.oldMode);
              l = compactValFor(bigInteger);
              if (l != Long.MIN_VALUE) {
                i = longDigitLength(l);
                break;
              } 
              i = bigDigitLength(bigInteger);
              m = i - k;
            } 
          } 
          if (l != Long.MIN_VALUE) {
            int m = i - k;
            while (m > 0) {
              j = checkScaleNonZero(j - m);
              l = divideAndRound(l, LONG_TEN_POWERS_TABLE[m], paramMathContext.roundingMode.oldMode);
              i = longDigitLength(l);
              m = i - k;
            } 
            bigInteger = null;
          } 
        } 
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new NumberFormatException();
    } catch (NegativeArraySizeException negativeArraySizeException) {
      throw new NumberFormatException();
    } 
    this.scale = j;
    this.precision = i;
    this.intCompact = l;
    this.intVal = bigInteger;
  }
  
  private int adjustScale(int paramInt, long paramLong) {
    long l = paramInt - paramLong;
    if (l > 2147483647L || l < -2147483648L)
      throw new NumberFormatException("Scale out of range."); 
    paramInt = (int)l;
    return paramInt;
  }
  
  private static long parseExp(char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    long l = 0L;
    paramInt1++;
    char c = paramArrayOfchar[paramInt1];
    paramInt2--;
    boolean bool = (c == '-') ? true : false;
    if (bool || c == '+') {
      paramInt1++;
      c = paramArrayOfchar[paramInt1];
      paramInt2--;
    } 
    if (paramInt2 <= 0)
      throw new NumberFormatException(); 
    while (paramInt2 > 10 && (c == '0' || Character.digit(c, 10) == 0)) {
      paramInt1++;
      c = paramArrayOfchar[paramInt1];
      paramInt2--;
    } 
    if (paramInt2 > 10)
      throw new NumberFormatException(); 
    for (;; paramInt2--) {
      int i;
      if (c >= '0' && c <= '9') {
        i = c - 48;
      } else {
        i = Character.digit(c, 10);
        if (i < 0)
          throw new NumberFormatException(); 
      } 
      l = l * 10L + i;
      if (paramInt2 == 1)
        break; 
      paramInt1++;
      c = paramArrayOfchar[paramInt1];
    } 
    if (bool)
      l = -l; 
    return l;
  }
  
  public BigDecimal(char[] paramArrayOfchar) {
    this(paramArrayOfchar, 0, paramArrayOfchar.length);
  }
  
  public BigDecimal(char[] paramArrayOfchar, MathContext paramMathContext) {
    this(paramArrayOfchar, 0, paramArrayOfchar.length, paramMathContext);
  }
  
  public BigDecimal(String paramString) {
    this(paramString.toCharArray(), 0, paramString.length());
  }
  
  public BigDecimal(String paramString, MathContext paramMathContext) {
    this(paramString.toCharArray(), 0, paramString.length(), paramMathContext);
  }
  
  public BigDecimal(double paramDouble) {
    this(paramDouble, MathContext.UNLIMITED);
  }
  
  public BigDecimal(double paramDouble, MathContext paramMathContext) {
    BigInteger bigInteger;
    if (Double.isInfinite(paramDouble) || Double.isNaN(paramDouble))
      throw new NumberFormatException("Infinite or NaN"); 
    long l1 = Double.doubleToLongBits(paramDouble);
    boolean bool = (l1 >> 63L == 0L) ? true : true;
    int i = (int)(l1 >> 52L & 0x7FFL);
    long l2 = (i == 0) ? ((l1 & 0xFFFFFFFFFFFFFL) << 1L) : (l1 & 0xFFFFFFFFFFFFFL | 0x10000000000000L);
    i -= 1075;
    if (l2 == 0L) {
      this.intVal = BigInteger.ZERO;
      this.scale = 0;
      this.intCompact = 0L;
      this.precision = 1;
      return;
    } 
    while ((l2 & 0x1L) == 0L) {
      l2 >>= 1L;
      i++;
    } 
    int j = 0;
    long l3 = bool * l2;
    if (i == 0) {
      bigInteger = (l3 == Long.MIN_VALUE) ? INFLATED_BIGINT : null;
    } else {
      if (i < 0) {
        bigInteger = BigInteger.valueOf(5L).pow(-i).multiply(l3);
        j = -i;
      } else {
        bigInteger = BigInteger.valueOf(2L).pow(i).multiply(l3);
      } 
      l3 = compactValFor(bigInteger);
    } 
    int k = 0;
    int m = paramMathContext.precision;
    if (m > 0) {
      int n = paramMathContext.roundingMode.oldMode;
      if (l3 == Long.MIN_VALUE) {
        k = bigDigitLength(bigInteger);
        int i1 = k - m;
        while (i1 > 0) {
          j = checkScaleNonZero(j - i1);
          bigInteger = divideAndRoundByTenPow(bigInteger, i1, n);
          l3 = compactValFor(bigInteger);
          if (l3 != Long.MIN_VALUE)
            break; 
          k = bigDigitLength(bigInteger);
          i1 = k - m;
        } 
      } 
      if (l3 != Long.MIN_VALUE) {
        k = longDigitLength(l3);
        int i1 = k - m;
        while (i1 > 0) {
          j = checkScaleNonZero(j - i1);
          l3 = divideAndRound(l3, LONG_TEN_POWERS_TABLE[i1], paramMathContext.roundingMode.oldMode);
          k = longDigitLength(l3);
          i1 = k - m;
        } 
        bigInteger = null;
      } 
    } 
    this.intVal = bigInteger;
    this.intCompact = l3;
    this.scale = j;
    this.precision = k;
  }
  
  public BigDecimal(BigInteger paramBigInteger) {
    this.scale = 0;
    this.intVal = paramBigInteger;
    this.intCompact = compactValFor(paramBigInteger);
  }
  
  public BigDecimal(BigInteger paramBigInteger, MathContext paramMathContext) {
    this(paramBigInteger, 0, paramMathContext);
  }
  
  public BigDecimal(BigInteger paramBigInteger, int paramInt) {
    this.intVal = paramBigInteger;
    this.intCompact = compactValFor(paramBigInteger);
    this.scale = paramInt;
  }
  
  public BigDecimal(BigInteger paramBigInteger, int paramInt, MathContext paramMathContext) {
    long l = compactValFor(paramBigInteger);
    int i = paramMathContext.precision;
    int j = 0;
    if (i > 0) {
      int k = paramMathContext.roundingMode.oldMode;
      if (l == Long.MIN_VALUE) {
        j = bigDigitLength(paramBigInteger);
        int m = j - i;
        while (m > 0) {
          paramInt = checkScaleNonZero(paramInt - m);
          paramBigInteger = divideAndRoundByTenPow(paramBigInteger, m, k);
          l = compactValFor(paramBigInteger);
          if (l != Long.MIN_VALUE)
            break; 
          j = bigDigitLength(paramBigInteger);
          m = j - i;
        } 
      } 
      if (l != Long.MIN_VALUE) {
        j = longDigitLength(l);
        int m = j - i;
        while (m > 0) {
          paramInt = checkScaleNonZero(paramInt - m);
          l = divideAndRound(l, LONG_TEN_POWERS_TABLE[m], k);
          j = longDigitLength(l);
          m = j - i;
        } 
        paramBigInteger = null;
      } 
    } 
    this.intVal = paramBigInteger;
    this.intCompact = l;
    this.scale = paramInt;
    this.precision = j;
  }
  
  public BigDecimal(int paramInt) {
    this.intCompact = paramInt;
    this.scale = 0;
    this.intVal = null;
  }
  
  public BigDecimal(int paramInt, MathContext paramMathContext) {
    int i = paramMathContext.precision;
    long l = paramInt;
    int j = 0;
    int k = 0;
    if (i > 0) {
      k = longDigitLength(l);
      int m = k - i;
      while (m > 0) {
        j = checkScaleNonZero(j - m);
        l = divideAndRound(l, LONG_TEN_POWERS_TABLE[m], paramMathContext.roundingMode.oldMode);
        k = longDigitLength(l);
        m = k - i;
      } 
    } 
    this.intVal = null;
    this.intCompact = l;
    this.scale = j;
    this.precision = k;
  }
  
  public BigDecimal(long paramLong) {
    this.intCompact = paramLong;
    this.intVal = (paramLong == Long.MIN_VALUE) ? INFLATED_BIGINT : null;
    this.scale = 0;
  }
  
  public BigDecimal(long paramLong, MathContext paramMathContext) {
    int i = paramMathContext.precision;
    int j = paramMathContext.roundingMode.oldMode;
    int k = 0;
    int m = 0;
    BigInteger bigInteger = (paramLong == Long.MIN_VALUE) ? INFLATED_BIGINT : null;
    if (i > 0) {
      if (paramLong == Long.MIN_VALUE) {
        k = 19;
        int n = k - i;
        while (n > 0) {
          m = checkScaleNonZero(m - n);
          bigInteger = divideAndRoundByTenPow(bigInteger, n, j);
          paramLong = compactValFor(bigInteger);
          if (paramLong != Long.MIN_VALUE)
            break; 
          k = bigDigitLength(bigInteger);
          n = k - i;
        } 
      } 
      if (paramLong != Long.MIN_VALUE) {
        k = longDigitLength(paramLong);
        int n = k - i;
        while (n > 0) {
          m = checkScaleNonZero(m - n);
          paramLong = divideAndRound(paramLong, LONG_TEN_POWERS_TABLE[n], paramMathContext.roundingMode.oldMode);
          k = longDigitLength(paramLong);
          n = k - i;
        } 
        bigInteger = null;
      } 
    } 
    this.intVal = bigInteger;
    this.intCompact = paramLong;
    this.scale = m;
    this.precision = k;
  }
  
  public static BigDecimal valueOf(long paramLong, int paramInt) {
    if (paramInt == 0)
      return valueOf(paramLong); 
    if (paramLong == 0L)
      return zeroValueOf(paramInt); 
    return new BigDecimal((paramLong == Long.MIN_VALUE) ? INFLATED_BIGINT : null, paramLong, paramInt, 0);
  }
  
  public static BigDecimal valueOf(long paramLong) {
    if (paramLong >= 0L && paramLong < zeroThroughTen.length)
      return zeroThroughTen[(int)paramLong]; 
    if (paramLong != Long.MIN_VALUE)
      return new BigDecimal(null, paramLong, 0, 0); 
    return new BigDecimal(INFLATED_BIGINT, paramLong, 0, 0);
  }
  
  static BigDecimal valueOf(long paramLong, int paramInt1, int paramInt2) {
    if (paramInt1 == 0 && paramLong >= 0L && paramLong < zeroThroughTen.length)
      return zeroThroughTen[(int)paramLong]; 
    if (paramLong == 0L)
      return zeroValueOf(paramInt1); 
    return new BigDecimal((paramLong == Long.MIN_VALUE) ? INFLATED_BIGINT : null, paramLong, paramInt1, paramInt2);
  }
  
  static BigDecimal valueOf(BigInteger paramBigInteger, int paramInt1, int paramInt2) {
    long l = compactValFor(paramBigInteger);
    if (l == 0L)
      return zeroValueOf(paramInt1); 
    if (paramInt1 == 0 && l >= 0L && l < zeroThroughTen.length)
      return zeroThroughTen[(int)l]; 
    return new BigDecimal(paramBigInteger, l, paramInt1, paramInt2);
  }
  
  static BigDecimal zeroValueOf(int paramInt) {
    if (paramInt >= 0 && paramInt < ZERO_SCALED_BY.length)
      return ZERO_SCALED_BY[paramInt]; 
    return new BigDecimal(BigInteger.ZERO, 0L, paramInt, 1);
  }
  
  public static BigDecimal valueOf(double paramDouble) {
    return new BigDecimal(Double.toString(paramDouble));
  }
  
  public BigDecimal add(BigDecimal paramBigDecimal) {
    if (this.intCompact != Long.MIN_VALUE) {
      if (paramBigDecimal.intCompact != Long.MIN_VALUE)
        return add(this.intCompact, this.scale, paramBigDecimal.intCompact, paramBigDecimal.scale); 
      return add(this.intCompact, this.scale, paramBigDecimal.intVal, paramBigDecimal.scale);
    } 
    if (paramBigDecimal.intCompact != Long.MIN_VALUE)
      return add(paramBigDecimal.intCompact, paramBigDecimal.scale, this.intVal, this.scale); 
    return add(this.intVal, this.scale, paramBigDecimal.intVal, paramBigDecimal.scale);
  }
  
  public BigDecimal add(BigDecimal paramBigDecimal, MathContext paramMathContext) {
    if (paramMathContext.precision == 0)
      return add(paramBigDecimal); 
    BigDecimal bigDecimal = this;
    boolean bool1 = (bigDecimal.signum() == 0) ? true : false;
    boolean bool2 = (paramBigDecimal.signum() == 0) ? true : false;
    if (bool1 || bool2) {
      int i = Math.max(bigDecimal.scale(), paramBigDecimal.scale());
      if (bool1 && bool2)
        return zeroValueOf(i); 
      BigDecimal bigDecimal1 = bool1 ? doRound(paramBigDecimal, paramMathContext) : doRound(bigDecimal, paramMathContext);
      if (bigDecimal1.scale() == i)
        return bigDecimal1; 
      if (bigDecimal1.scale() > i)
        return stripZerosToMatchScale(bigDecimal1.intVal, bigDecimal1.intCompact, bigDecimal1.scale, i); 
      int j = paramMathContext.precision - bigDecimal1.precision();
      int k = i - bigDecimal1.scale();
      if (j >= k)
        return bigDecimal1.setScale(i); 
      return bigDecimal1.setScale(bigDecimal1.scale() + j);
    } 
    long l = bigDecimal.scale - paramBigDecimal.scale;
    if (l != 0L) {
      BigDecimal[] arrayOfBigDecimal = preAlign(bigDecimal, paramBigDecimal, l, paramMathContext);
      matchScale(arrayOfBigDecimal);
      bigDecimal = arrayOfBigDecimal[0];
      paramBigDecimal = arrayOfBigDecimal[1];
    } 
    return doRound(bigDecimal.inflated().add(paramBigDecimal.inflated()), bigDecimal.scale, paramMathContext);
  }
  
  private BigDecimal[] preAlign(BigDecimal paramBigDecimal1, BigDecimal paramBigDecimal2, long paramLong, MathContext paramMathContext) {
    BigDecimal bigDecimal1, bigDecimal2;
    assert paramLong != 0L;
    if (paramLong < 0L) {
      bigDecimal1 = paramBigDecimal1;
      bigDecimal2 = paramBigDecimal2;
    } else {
      bigDecimal1 = paramBigDecimal2;
      bigDecimal2 = paramBigDecimal1;
    } 
    long l1 = bigDecimal1.scale - bigDecimal1.precision() + paramMathContext.precision;
    long l2 = bigDecimal2.scale - bigDecimal2.precision() + 1L;
    if (l2 > (bigDecimal1.scale + 2) && l2 > l1 + 2L)
      bigDecimal2 = valueOf(bigDecimal2.signum(), checkScale(Math.max(bigDecimal1.scale, l1) + 3L)); 
    return new BigDecimal[] { bigDecimal1, bigDecimal2 };
  }
  
  public BigDecimal subtract(BigDecimal paramBigDecimal) {
    if (this.intCompact != Long.MIN_VALUE) {
      if (paramBigDecimal.intCompact != Long.MIN_VALUE)
        return add(this.intCompact, this.scale, -paramBigDecimal.intCompact, paramBigDecimal.scale); 
      return add(this.intCompact, this.scale, paramBigDecimal.intVal.negate(), paramBigDecimal.scale);
    } 
    if (paramBigDecimal.intCompact != Long.MIN_VALUE)
      return add(-paramBigDecimal.intCompact, paramBigDecimal.scale, this.intVal, this.scale); 
    return add(this.intVal, this.scale, paramBigDecimal.intVal.negate(), paramBigDecimal.scale);
  }
  
  public BigDecimal subtract(BigDecimal paramBigDecimal, MathContext paramMathContext) {
    if (paramMathContext.precision == 0)
      return subtract(paramBigDecimal); 
    return add(paramBigDecimal.negate(), paramMathContext);
  }
  
  public BigDecimal multiply(BigDecimal paramBigDecimal) {
    int i = checkScale(this.scale + paramBigDecimal.scale);
    if (this.intCompact != Long.MIN_VALUE) {
      if (paramBigDecimal.intCompact != Long.MIN_VALUE)
        return multiply(this.intCompact, paramBigDecimal.intCompact, i); 
      return multiply(this.intCompact, paramBigDecimal.intVal, i);
    } 
    if (paramBigDecimal.intCompact != Long.MIN_VALUE)
      return multiply(paramBigDecimal.intCompact, this.intVal, i); 
    return multiply(this.intVal, paramBigDecimal.intVal, i);
  }
  
  public BigDecimal multiply(BigDecimal paramBigDecimal, MathContext paramMathContext) {
    if (paramMathContext.precision == 0)
      return multiply(paramBigDecimal); 
    int i = checkScale(this.scale + paramBigDecimal.scale);
    if (this.intCompact != Long.MIN_VALUE) {
      if (paramBigDecimal.intCompact != Long.MIN_VALUE)
        return multiplyAndRound(this.intCompact, paramBigDecimal.intCompact, i, paramMathContext); 
      return multiplyAndRound(this.intCompact, paramBigDecimal.intVal, i, paramMathContext);
    } 
    if (paramBigDecimal.intCompact != Long.MIN_VALUE)
      return multiplyAndRound(paramBigDecimal.intCompact, this.intVal, i, paramMathContext); 
    return multiplyAndRound(this.intVal, paramBigDecimal.intVal, i, paramMathContext);
  }
  
  public BigDecimal divide(BigDecimal paramBigDecimal, int paramInt1, int paramInt2) {
    if (paramInt2 < 0 || paramInt2 > 7)
      throw new IllegalArgumentException("Invalid rounding mode"); 
    if (this.intCompact != Long.MIN_VALUE) {
      if (paramBigDecimal.intCompact != Long.MIN_VALUE)
        return divide(this.intCompact, this.scale, paramBigDecimal.intCompact, paramBigDecimal.scale, paramInt1, paramInt2); 
      return divide(this.intCompact, this.scale, paramBigDecimal.intVal, paramBigDecimal.scale, paramInt1, paramInt2);
    } 
    if (paramBigDecimal.intCompact != Long.MIN_VALUE)
      return divide(this.intVal, this.scale, paramBigDecimal.intCompact, paramBigDecimal.scale, paramInt1, paramInt2); 
    return divide(this.intVal, this.scale, paramBigDecimal.intVal, paramBigDecimal.scale, paramInt1, paramInt2);
  }
  
  public BigDecimal divide(BigDecimal paramBigDecimal, int paramInt, RoundingMode paramRoundingMode) {
    return divide(paramBigDecimal, paramInt, paramRoundingMode.oldMode);
  }
  
  public BigDecimal divide(BigDecimal paramBigDecimal, int paramInt) {
    return divide(paramBigDecimal, this.scale, paramInt);
  }
  
  public BigDecimal divide(BigDecimal paramBigDecimal, RoundingMode paramRoundingMode) {
    return divide(paramBigDecimal, this.scale, paramRoundingMode.oldMode);
  }
  
  public BigDecimal divide(BigDecimal paramBigDecimal) {
    BigDecimal bigDecimal;
    if (paramBigDecimal.signum() == 0) {
      if (signum() == 0)
        throw new ArithmeticException("Division undefined"); 
      throw new ArithmeticException("Division by zero");
    } 
    int i = saturateLong(this.scale - paramBigDecimal.scale);
    if (signum() == 0)
      return zeroValueOf(i); 
    MathContext mathContext = new MathContext((int)Math.min(precision() + 
          (long)Math.ceil(10.0D * paramBigDecimal.precision() / 3.0D), 2147483647L), RoundingMode.UNNECESSARY);
    try {
      bigDecimal = divide(paramBigDecimal, mathContext);
    } catch (ArithmeticException arithmeticException) {
      throw new ArithmeticException("Non-terminating decimal expansion; no exact representable decimal result.");
    } 
    int j = bigDecimal.scale();
    if (i > j)
      return bigDecimal.setScale(i, 7); 
    return bigDecimal;
  }
  
  public BigDecimal divide(BigDecimal paramBigDecimal, MathContext paramMathContext) {
    int i = paramMathContext.precision;
    if (i == 0)
      return divide(paramBigDecimal); 
    BigDecimal bigDecimal = this;
    long l = bigDecimal.scale - paramBigDecimal.scale;
    if (paramBigDecimal.signum() == 0) {
      if (bigDecimal.signum() == 0)
        throw new ArithmeticException("Division undefined"); 
      throw new ArithmeticException("Division by zero");
    } 
    if (bigDecimal.signum() == 0)
      return zeroValueOf(saturateLong(l)); 
    int j = bigDecimal.precision();
    int k = paramBigDecimal.precision();
    if (bigDecimal.intCompact != Long.MIN_VALUE) {
      if (paramBigDecimal.intCompact != Long.MIN_VALUE)
        return divide(bigDecimal.intCompact, j, paramBigDecimal.intCompact, k, l, paramMathContext); 
      return divide(bigDecimal.intCompact, j, paramBigDecimal.intVal, k, l, paramMathContext);
    } 
    if (paramBigDecimal.intCompact != Long.MIN_VALUE)
      return divide(bigDecimal.intVal, j, paramBigDecimal.intCompact, k, l, paramMathContext); 
    return divide(bigDecimal.intVal, j, paramBigDecimal.intVal, k, l, paramMathContext);
  }
  
  public BigDecimal divideToIntegralValue(BigDecimal paramBigDecimal) {
    int i = saturateLong(this.scale - paramBigDecimal.scale);
    if (compareMagnitude(paramBigDecimal) < 0)
      return zeroValueOf(i); 
    if (signum() == 0 && paramBigDecimal.signum() != 0)
      return setScale(i, 7); 
    int j = (int)Math.min(precision() + 
        (long)Math.ceil(10.0D * paramBigDecimal.precision() / 3.0D) + 
        Math.abs(scale() - paramBigDecimal.scale()) + 2L, 2147483647L);
    BigDecimal bigDecimal = divide(paramBigDecimal, new MathContext(j, RoundingMode.DOWN));
    if (bigDecimal.scale > 0) {
      bigDecimal = bigDecimal.setScale(0, RoundingMode.DOWN);
      bigDecimal = stripZerosToMatchScale(bigDecimal.intVal, bigDecimal.intCompact, bigDecimal.scale, i);
    } 
    if (bigDecimal.scale < i)
      bigDecimal = bigDecimal.setScale(i, 7); 
    return bigDecimal;
  }
  
  public BigDecimal divideToIntegralValue(BigDecimal paramBigDecimal, MathContext paramMathContext) {
    if (paramMathContext.precision == 0 || 
      compareMagnitude(paramBigDecimal) < 0)
      return divideToIntegralValue(paramBigDecimal); 
    int i = saturateLong(this.scale - paramBigDecimal.scale);
    BigDecimal bigDecimal = divide(paramBigDecimal, new MathContext(paramMathContext.precision, RoundingMode.DOWN));
    if (bigDecimal.scale() < 0) {
      BigDecimal bigDecimal1 = bigDecimal.multiply(paramBigDecimal);
      if (subtract(bigDecimal1).compareMagnitude(paramBigDecimal) >= 0)
        throw new ArithmeticException("Division impossible"); 
    } else if (bigDecimal.scale() > 0) {
      bigDecimal = bigDecimal.setScale(0, RoundingMode.DOWN);
    } 
    int j;
    if (i > bigDecimal.scale() && (
      j = paramMathContext.precision - bigDecimal.precision()) > 0)
      return bigDecimal.setScale(bigDecimal.scale() + 
          Math.min(j, i - bigDecimal.scale)); 
    return stripZerosToMatchScale(bigDecimal.intVal, bigDecimal.intCompact, bigDecimal.scale, i);
  }
  
  public BigDecimal remainder(BigDecimal paramBigDecimal) {
    BigDecimal[] arrayOfBigDecimal = divideAndRemainder(paramBigDecimal);
    return arrayOfBigDecimal[1];
  }
  
  public BigDecimal remainder(BigDecimal paramBigDecimal, MathContext paramMathContext) {
    BigDecimal[] arrayOfBigDecimal = divideAndRemainder(paramBigDecimal, paramMathContext);
    return arrayOfBigDecimal[1];
  }
  
  public BigDecimal[] divideAndRemainder(BigDecimal paramBigDecimal) {
    BigDecimal[] arrayOfBigDecimal = new BigDecimal[2];
    arrayOfBigDecimal[0] = divideToIntegralValue(paramBigDecimal);
    arrayOfBigDecimal[1] = subtract(arrayOfBigDecimal[0].multiply(paramBigDecimal));
    return arrayOfBigDecimal;
  }
  
  public BigDecimal[] divideAndRemainder(BigDecimal paramBigDecimal, MathContext paramMathContext) {
    if (paramMathContext.precision == 0)
      return divideAndRemainder(paramBigDecimal); 
    BigDecimal[] arrayOfBigDecimal = new BigDecimal[2];
    BigDecimal bigDecimal = this;
    arrayOfBigDecimal[0] = bigDecimal.divideToIntegralValue(paramBigDecimal, paramMathContext);
    arrayOfBigDecimal[1] = bigDecimal.subtract(arrayOfBigDecimal[0].multiply(paramBigDecimal));
    return arrayOfBigDecimal;
  }
  
  public BigDecimal pow(int paramInt) {
    if (paramInt < 0 || paramInt > 999999999)
      throw new ArithmeticException("Invalid operation"); 
    int i = checkScale(this.scale * paramInt);
    return new BigDecimal(inflated().pow(paramInt), i);
  }
  
  public BigDecimal pow(int paramInt, MathContext paramMathContext) {
    if (paramMathContext.precision == 0)
      return pow(paramInt); 
    if (paramInt < -999999999 || paramInt > 999999999)
      throw new ArithmeticException("Invalid operation"); 
    if (paramInt == 0)
      return ONE; 
    BigDecimal bigDecimal1 = this;
    MathContext mathContext = paramMathContext;
    int i = Math.abs(paramInt);
    if (paramMathContext.precision > 0) {
      int j = longDigitLength(i);
      if (j > paramMathContext.precision)
        throw new ArithmeticException("Invalid operation"); 
      mathContext = new MathContext(paramMathContext.precision + j + 1, paramMathContext.roundingMode);
    } 
    BigDecimal bigDecimal2 = ONE;
    boolean bool = false;
    for (byte b = 1;; b++) {
      i += i;
      if (i < 0) {
        bool = true;
        bigDecimal2 = bigDecimal2.multiply(bigDecimal1, mathContext);
      } 
      if (b == 31)
        break; 
      if (bool)
        bigDecimal2 = bigDecimal2.multiply(bigDecimal2, mathContext); 
    } 
    if (paramInt < 0)
      bigDecimal2 = ONE.divide(bigDecimal2, mathContext); 
    return doRound(bigDecimal2, paramMathContext);
  }
  
  public BigDecimal abs() {
    return (signum() < 0) ? negate() : this;
  }
  
  public BigDecimal abs(MathContext paramMathContext) {
    return (signum() < 0) ? negate(paramMathContext) : plus(paramMathContext);
  }
  
  public BigDecimal negate() {
    if (this.intCompact == Long.MIN_VALUE)
      return new BigDecimal(this.intVal.negate(), Long.MIN_VALUE, this.scale, this.precision); 
    return valueOf(-this.intCompact, this.scale, this.precision);
  }
  
  public BigDecimal negate(MathContext paramMathContext) {
    return negate().plus(paramMathContext);
  }
  
  public BigDecimal plus() {
    return this;
  }
  
  public BigDecimal plus(MathContext paramMathContext) {
    if (paramMathContext.precision == 0)
      return this; 
    return doRound(this, paramMathContext);
  }
  
  public int signum() {
    return (this.intCompact != Long.MIN_VALUE) ? 
      Long.signum(this.intCompact) : this.intVal
      .signum();
  }
  
  public int scale() {
    return this.scale;
  }
  
  public int precision() {
    int i = this.precision;
    if (i == 0) {
      long l = this.intCompact;
      if (l != Long.MIN_VALUE) {
        i = longDigitLength(l);
      } else {
        i = bigDigitLength(this.intVal);
      } 
      this.precision = i;
    } 
    return i;
  }
  
  public BigInteger unscaledValue() {
    return inflated();
  }
  
  public BigDecimal round(MathContext paramMathContext) {
    return plus(paramMathContext);
  }
  
  public BigDecimal setScale(int paramInt, RoundingMode paramRoundingMode) {
    return setScale(paramInt, paramRoundingMode.oldMode);
  }
  
  public BigDecimal setScale(int paramInt1, int paramInt2) {
    if (paramInt2 < 0 || paramInt2 > 7)
      throw new IllegalArgumentException("Invalid rounding mode"); 
    int i = this.scale;
    if (paramInt1 == i)
      return this; 
    if (signum() == 0)
      return zeroValueOf(paramInt1); 
    if (this.intCompact != Long.MIN_VALUE) {
      long l = this.intCompact;
      if (paramInt1 > i) {
        int m = checkScale(paramInt1 - i);
        if ((l = longMultiplyPowerTen(l, m)) != Long.MIN_VALUE)
          return valueOf(l, paramInt1); 
        BigInteger bigInteger = bigMultiplyPowerTen(m);
        return new BigDecimal(bigInteger, Long.MIN_VALUE, paramInt1, (this.precision > 0) ? (this.precision + m) : 0);
      } 
      int k = checkScale(i - paramInt1);
      if (k < LONG_TEN_POWERS_TABLE.length)
        return divideAndRound(l, LONG_TEN_POWERS_TABLE[k], paramInt1, paramInt2, paramInt1); 
      return divideAndRound(inflated(), bigTenToThe(k), paramInt1, paramInt2, paramInt1);
    } 
    if (paramInt1 > i) {
      int k = checkScale(paramInt1 - i);
      BigInteger bigInteger = bigMultiplyPowerTen(this.intVal, k);
      return new BigDecimal(bigInteger, Long.MIN_VALUE, paramInt1, (this.precision > 0) ? (this.precision + k) : 0);
    } 
    int j = checkScale(i - paramInt1);
    if (j < LONG_TEN_POWERS_TABLE.length)
      return divideAndRound(this.intVal, LONG_TEN_POWERS_TABLE[j], paramInt1, paramInt2, paramInt1); 
    return divideAndRound(this.intVal, bigTenToThe(j), paramInt1, paramInt2, paramInt1);
  }
  
  public BigDecimal setScale(int paramInt) {
    return setScale(paramInt, 7);
  }
  
  public BigDecimal movePointLeft(int paramInt) {
    int i = checkScale(this.scale + paramInt);
    BigDecimal bigDecimal = new BigDecimal(this.intVal, this.intCompact, i, 0);
    return (bigDecimal.scale < 0) ? bigDecimal.setScale(0, 7) : bigDecimal;
  }
  
  public BigDecimal movePointRight(int paramInt) {
    int i = checkScale(this.scale - paramInt);
    BigDecimal bigDecimal = new BigDecimal(this.intVal, this.intCompact, i, 0);
    return (bigDecimal.scale < 0) ? bigDecimal.setScale(0, 7) : bigDecimal;
  }
  
  public BigDecimal scaleByPowerOfTen(int paramInt) {
    return new BigDecimal(this.intVal, this.intCompact, 
        checkScale(this.scale - paramInt), this.precision);
  }
  
  public BigDecimal stripTrailingZeros() {
    if (this.intCompact == 0L || (this.intVal != null && this.intVal.signum() == 0))
      return ZERO; 
    if (this.intCompact != Long.MIN_VALUE)
      return createAndStripZerosToMatchScale(this.intCompact, this.scale, Long.MIN_VALUE); 
    return createAndStripZerosToMatchScale(this.intVal, this.scale, Long.MIN_VALUE);
  }
  
  public int compareTo(BigDecimal paramBigDecimal) {
    if (this.scale == paramBigDecimal.scale) {
      long l1 = this.intCompact;
      long l2 = paramBigDecimal.intCompact;
      if (l1 != Long.MIN_VALUE && l2 != Long.MIN_VALUE)
        return (l1 != l2) ? ((l1 > l2) ? 1 : -1) : 0; 
    } 
    int i = signum();
    int j = paramBigDecimal.signum();
    if (i != j)
      return (i > j) ? 1 : -1; 
    if (i == 0)
      return 0; 
    int k = compareMagnitude(paramBigDecimal);
    return (i > 0) ? k : -k;
  }
  
  private int compareMagnitude(BigDecimal paramBigDecimal) {
    long l1 = paramBigDecimal.intCompact;
    long l2 = this.intCompact;
    if (l2 == 0L)
      return (l1 == 0L) ? 0 : -1; 
    if (l1 == 0L)
      return 1; 
    long l3 = this.scale - paramBigDecimal.scale;
    if (l3 != 0L) {
      long l4 = precision() - this.scale;
      long l5 = paramBigDecimal.precision() - paramBigDecimal.scale;
      if (l4 < l5)
        return -1; 
      if (l4 > l5)
        return 1; 
      BigInteger bigInteger = null;
      if (l3 < 0L) {
        if (l3 > -2147483648L && (l2 == Long.MIN_VALUE || (
          
          l2 = longMultiplyPowerTen(l2, (int)-l3)) == Long.MIN_VALUE) && l1 == Long.MIN_VALUE) {
          bigInteger = bigMultiplyPowerTen((int)-l3);
          return bigInteger.compareMagnitude(paramBigDecimal.intVal);
        } 
      } else if (l3 <= 2147483647L && (l1 == Long.MIN_VALUE || (
        
        l1 = longMultiplyPowerTen(l1, (int)l3)) == Long.MIN_VALUE) && l2 == Long.MIN_VALUE) {
        bigInteger = paramBigDecimal.bigMultiplyPowerTen((int)l3);
        return this.intVal.compareMagnitude(bigInteger);
      } 
    } 
    if (l2 != Long.MIN_VALUE)
      return (l1 != Long.MIN_VALUE) ? longCompareMagnitude(l2, l1) : -1; 
    if (l1 != Long.MIN_VALUE)
      return 1; 
    return this.intVal.compareMagnitude(paramBigDecimal.intVal);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof BigDecimal))
      return false; 
    BigDecimal bigDecimal = (BigDecimal)paramObject;
    if (paramObject == this)
      return true; 
    if (this.scale != bigDecimal.scale)
      return false; 
    long l1 = this.intCompact;
    long l2 = bigDecimal.intCompact;
    if (l1 != Long.MIN_VALUE) {
      if (l2 == Long.MIN_VALUE)
        l2 = compactValFor(bigDecimal.intVal); 
      return (l2 == l1);
    } 
    if (l2 != Long.MIN_VALUE)
      return (l2 == compactValFor(this.intVal)); 
    return inflated().equals(bigDecimal.inflated());
  }
  
  public BigDecimal min(BigDecimal paramBigDecimal) {
    return (compareTo(paramBigDecimal) <= 0) ? this : paramBigDecimal;
  }
  
  public BigDecimal max(BigDecimal paramBigDecimal) {
    return (compareTo(paramBigDecimal) >= 0) ? this : paramBigDecimal;
  }
  
  public int hashCode() {
    if (this.intCompact != Long.MIN_VALUE) {
      long l = (this.intCompact < 0L) ? -this.intCompact : this.intCompact;
      int i = (int)(((int)(l >>> 32L) * 31) + (l & 0xFFFFFFFFL));
      return 31 * ((this.intCompact < 0L) ? -i : i) + this.scale;
    } 
    return 31 * this.intVal.hashCode() + this.scale;
  }
  
  public String toString() {
    String str = this.stringCache;
    if (str == null)
      this.stringCache = str = layoutChars(true); 
    return str;
  }
  
  public String toEngineeringString() {
    return layoutChars(false);
  }
  
  public String toPlainString() {
    String str;
    if (this.scale == 0) {
      if (this.intCompact != Long.MIN_VALUE)
        return Long.toString(this.intCompact); 
      return this.intVal.toString();
    } 
    if (this.scale < 0) {
      StringBuilder stringBuilder;
      if (signum() == 0)
        return "0"; 
      int i = checkScaleNonZero(-(this.scale));
      if (this.intCompact != Long.MIN_VALUE) {
        stringBuilder = new StringBuilder(20 + i);
        stringBuilder.append(this.intCompact);
      } else {
        String str1 = this.intVal.toString();
        stringBuilder = new StringBuilder(str1.length() + i);
        stringBuilder.append(str1);
      } 
      for (byte b = 0; b < i; b++)
        stringBuilder.append('0'); 
      return stringBuilder.toString();
    } 
    if (this.intCompact != Long.MIN_VALUE) {
      str = Long.toString(Math.abs(this.intCompact));
    } else {
      str = this.intVal.abs().toString();
    } 
    return getValueString(signum(), str, this.scale);
  }
  
  private String getValueString(int paramInt1, String paramString, int paramInt2) {
    StringBuilder stringBuilder;
    int i = paramString.length() - paramInt2;
    if (i == 0)
      return ((paramInt1 < 0) ? "-0." : "0.") + paramString; 
    if (i > 0) {
      stringBuilder = new StringBuilder(paramString);
      stringBuilder.insert(i, '.');
      if (paramInt1 < 0)
        stringBuilder.insert(0, '-'); 
    } else {
      stringBuilder = new StringBuilder(3 - i + paramString.length());
      stringBuilder.append((paramInt1 < 0) ? "-0." : "0.");
      for (byte b = 0; b < -i; b++)
        stringBuilder.append('0'); 
      stringBuilder.append(paramString);
    } 
    return stringBuilder.toString();
  }
  
  public BigInteger toBigInteger() {
    return setScale(0, 1).inflated();
  }
  
  public BigInteger toBigIntegerExact() {
    return setScale(0, 7).inflated();
  }
  
  public long longValue() {
    return (this.intCompact != Long.MIN_VALUE && this.scale == 0) ? this.intCompact : 
      
      toBigInteger().longValue();
  }
  
  public long longValueExact() {
    if (this.intCompact != Long.MIN_VALUE && this.scale == 0)
      return this.intCompact; 
    if (precision() - this.scale > 19)
      throw new ArithmeticException("Overflow"); 
    if (signum() == 0)
      return 0L; 
    if (precision() - this.scale <= 0)
      throw new ArithmeticException("Rounding necessary"); 
    BigDecimal bigDecimal = setScale(0, 7);
    if (bigDecimal.precision() >= 19)
      LongOverflow.check(bigDecimal); 
    return bigDecimal.inflated().longValue();
  }
  
  public int intValue() {
    return (this.intCompact != Long.MIN_VALUE && this.scale == 0) ? (int)this.intCompact : 
      
      toBigInteger().intValue();
  }
  
  public int intValueExact() {
    long l = longValueExact();
    if ((int)l != l)
      throw new ArithmeticException("Overflow"); 
    return (int)l;
  }
  
  public short shortValueExact() {
    long l = longValueExact();
    if ((short)(int)l != l)
      throw new ArithmeticException("Overflow"); 
    return (short)(int)l;
  }
  
  public byte byteValueExact() {
    long l = longValueExact();
    if ((byte)(int)l != l)
      throw new ArithmeticException("Overflow"); 
    return (byte)(int)l;
  }
  
  public float floatValue() {
    if (this.intCompact != Long.MIN_VALUE) {
      if (this.scale == 0)
        return (float)this.intCompact; 
      if (Math.abs(this.intCompact) < 4194304L) {
        if (this.scale > 0 && this.scale < float10pow.length)
          return (float)this.intCompact / float10pow[this.scale]; 
        if (this.scale < 0 && this.scale > -float10pow.length)
          return (float)this.intCompact * float10pow[-this.scale]; 
      } 
    } 
    return Float.parseFloat(toString());
  }
  
  public double doubleValue() {
    if (this.intCompact != Long.MIN_VALUE) {
      if (this.scale == 0)
        return this.intCompact; 
      if (Math.abs(this.intCompact) < 4503599627370496L) {
        if (this.scale > 0 && this.scale < double10pow.length)
          return this.intCompact / double10pow[this.scale]; 
        if (this.scale < 0 && this.scale > -double10pow.length)
          return this.intCompact * double10pow[-this.scale]; 
      } 
    } 
    return Double.parseDouble(toString());
  }
  
  private static final double[] double10pow = new double[] { 
      1.0D, 10.0D, 100.0D, 1000.0D, 10000.0D, 100000.0D, 1000000.0D, 1.0E7D, 1.0E8D, 1.0E9D, 
      1.0E10D, 1.0E11D, 1.0E12D, 1.0E13D, 1.0E14D, 1.0E15D, 1.0E16D, 1.0E17D, 1.0E18D, 1.0E19D, 
      1.0E20D, 1.0E21D, 1.0E22D };
  
  private static final float[] float10pow = new float[] { 
      1.0F, 10.0F, 100.0F, 1000.0F, 10000.0F, 100000.0F, 1000000.0F, 1.0E7F, 1.0E8F, 1.0E9F, 
      1.0E10F };
  
  public BigDecimal ulp() {
    return valueOf(1L, scale(), 1);
  }
  
  private String layoutChars(boolean paramBoolean) {
    char[] arrayOfChar;
    byte b;
    if (this.scale == 0)
      return (this.intCompact != Long.MIN_VALUE) ? 
        Long.toString(this.intCompact) : this.intVal
        .toString(); 
    if (this.scale == 2 && this.intCompact >= 0L && this.intCompact < 2147483647L) {
      int j = (int)this.intCompact % 100;
      int k = (int)this.intCompact / 100;
      return Integer.toString(k) + '.' + StringBuilderHelper.DIGIT_TENS[j] + StringBuilderHelper.DIGIT_ONES[j];
    } 
    StringBuilderHelper stringBuilderHelper = threadLocalStringBuilderHelper.get();
    if (this.intCompact != Long.MIN_VALUE) {
      b = stringBuilderHelper.putIntCompact(Math.abs(this.intCompact));
      arrayOfChar = stringBuilderHelper.getCompactCharArray();
    } else {
      b = 0;
      arrayOfChar = this.intVal.abs().toString().toCharArray();
    } 
    StringBuilder stringBuilder = stringBuilderHelper.getStringBuilder();
    if (signum() < 0)
      stringBuilder.append('-'); 
    int i = arrayOfChar.length - b;
    long l = -(this.scale) + (i - 1);
    if (this.scale >= 0 && l >= -6L) {
      int j = this.scale - i;
      if (j >= 0) {
        stringBuilder.append('0');
        stringBuilder.append('.');
        for (; j > 0; j--)
          stringBuilder.append('0'); 
        stringBuilder.append(arrayOfChar, b, i);
      } else {
        stringBuilder.append(arrayOfChar, b, -j);
        stringBuilder.append('.');
        stringBuilder.append(arrayOfChar, -j + b, this.scale);
      } 
    } else {
      if (paramBoolean) {
        stringBuilder.append(arrayOfChar[b]);
        if (i > 1) {
          stringBuilder.append('.');
          stringBuilder.append(arrayOfChar, b + 1, i - 1);
        } 
      } else {
        int j = (int)(l % 3L);
        if (j < 0)
          j += 3; 
        l -= j;
        j++;
        if (signum() == 0) {
          switch (j) {
            case 1:
              stringBuilder.append('0');
              break;
            case 2:
              stringBuilder.append("0.00");
              l += 3L;
              break;
            case 3:
              stringBuilder.append("0.0");
              l += 3L;
              break;
            default:
              throw new AssertionError("Unexpected sig value " + j);
          } 
        } else if (j >= i) {
          stringBuilder.append(arrayOfChar, b, i);
          for (int k = j - i; k > 0; k--)
            stringBuilder.append('0'); 
        } else {
          stringBuilder.append(arrayOfChar, b, j);
          stringBuilder.append('.');
          stringBuilder.append(arrayOfChar, b + j, i - j);
        } 
      } 
      if (l != 0L) {
        stringBuilder.append('E');
        if (l > 0L)
          stringBuilder.append('+'); 
        stringBuilder.append(l);
      } 
    } 
    return stringBuilder.toString();
  }
  
  private static BigInteger bigTenToThe(int paramInt) {
    if (paramInt < 0)
      return BigInteger.ZERO; 
    if (paramInt < BIG_TEN_POWERS_TABLE_MAX) {
      BigInteger[] arrayOfBigInteger = BIG_TEN_POWERS_TABLE;
      if (paramInt < arrayOfBigInteger.length)
        return arrayOfBigInteger[paramInt]; 
      return expandBigIntegerTenPowers(paramInt);
    } 
    return BigInteger.TEN.pow(paramInt);
  }
  
  private static BigInteger expandBigIntegerTenPowers(int paramInt) {
    synchronized (BigDecimal.class) {
      BigInteger[] arrayOfBigInteger = BIG_TEN_POWERS_TABLE;
      int i = arrayOfBigInteger.length;
      if (i <= paramInt) {
        int j = i << 1;
        while (j <= paramInt)
          j <<= 1; 
        arrayOfBigInteger = Arrays.<BigInteger>copyOf(arrayOfBigInteger, j);
        for (int k = i; k < j; k++)
          arrayOfBigInteger[k] = arrayOfBigInteger[k - 1].multiply(BigInteger.TEN); 
        BIG_TEN_POWERS_TABLE = arrayOfBigInteger;
      } 
      return arrayOfBigInteger[paramInt];
    } 
  }
  
  private static final long[] LONG_TEN_POWERS_TABLE = new long[] { 
      1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 
      10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L };
  
  private static volatile BigInteger[] BIG_TEN_POWERS_TABLE = new BigInteger[] { 
      BigInteger.ONE, 
      
      BigInteger.valueOf(10L), 
      BigInteger.valueOf(100L), 
      BigInteger.valueOf(1000L), 
      BigInteger.valueOf(10000L), 
      BigInteger.valueOf(100000L), 
      BigInteger.valueOf(1000000L), 
      BigInteger.valueOf(10000000L), 
      BigInteger.valueOf(100000000L), 
      BigInteger.valueOf(1000000000L), 
      BigInteger.valueOf(10000000000L), 
      BigInteger.valueOf(100000000000L), 
      BigInteger.valueOf(1000000000000L), 
      BigInteger.valueOf(10000000000000L), 
      BigInteger.valueOf(100000000000000L), 
      BigInteger.valueOf(1000000000000000L), 
      BigInteger.valueOf(10000000000000000L), 
      BigInteger.valueOf(100000000000000000L), 
      BigInteger.valueOf(1000000000000000000L) };
  
  private static final int BIG_TEN_POWERS_TABLE_INITLEN = BIG_TEN_POWERS_TABLE.length;
  
  private static final int BIG_TEN_POWERS_TABLE_MAX = 16 * BIG_TEN_POWERS_TABLE_INITLEN;
  
  private static final long[] THRESHOLDS_TABLE = new long[] { 
      Long.MAX_VALUE, 922337203685477580L, 92233720368547758L, 9223372036854775L, 922337203685477L, 92233720368547L, 9223372036854L, 922337203685L, 92233720368L, 9223372036L, 
      922337203L, 92233720L, 9223372L, 922337L, 92233L, 9223L, 922L, 92L, 9L };
  
  private static final long DIV_NUM_BASE = 4294967296L;
  
  private static long longMultiplyPowerTen(long paramLong, int paramInt) {
    if (paramLong == 0L || paramInt <= 0)
      return paramLong; 
    long[] arrayOfLong1 = LONG_TEN_POWERS_TABLE;
    long[] arrayOfLong2 = THRESHOLDS_TABLE;
    if (paramInt < arrayOfLong1.length && paramInt < arrayOfLong2.length) {
      long l = arrayOfLong1[paramInt];
      if (paramLong == 1L)
        return l; 
      if (Math.abs(paramLong) <= arrayOfLong2[paramInt])
        return paramLong * l; 
    } 
    return Long.MIN_VALUE;
  }
  
  private BigInteger bigMultiplyPowerTen(int paramInt) {
    if (paramInt <= 0)
      return inflated(); 
    if (this.intCompact != Long.MIN_VALUE)
      return bigTenToThe(paramInt).multiply(this.intCompact); 
    return this.intVal.multiply(bigTenToThe(paramInt));
  }
  
  private BigInteger inflated() {
    if (this.intVal == null)
      return BigInteger.valueOf(this.intCompact); 
    return this.intVal;
  }
  
  private static void matchScale(BigDecimal[] paramArrayOfBigDecimal) {
    if ((paramArrayOfBigDecimal[0]).scale == (paramArrayOfBigDecimal[1]).scale)
      return; 
    if ((paramArrayOfBigDecimal[0]).scale < (paramArrayOfBigDecimal[1]).scale) {
      paramArrayOfBigDecimal[0] = paramArrayOfBigDecimal[0].setScale((paramArrayOfBigDecimal[1]).scale, 7);
    } else if ((paramArrayOfBigDecimal[1]).scale < (paramArrayOfBigDecimal[0]).scale) {
      paramArrayOfBigDecimal[1] = paramArrayOfBigDecimal[1].setScale((paramArrayOfBigDecimal[0]).scale, 7);
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.intVal == null) {
      String str = "BigDecimal: null intVal in stream";
      throw new StreamCorruptedException(str);
    } 
    UnsafeHolder.setIntCompactVolatile(this, compactValFor(this.intVal));
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (this.intVal == null)
      UnsafeHolder.setIntValVolatile(this, BigInteger.valueOf(this.intCompact)); 
    paramObjectOutputStream.defaultWriteObject();
  }
  
  static int longDigitLength(long paramLong) {
    assert paramLong != Long.MIN_VALUE;
    if (paramLong < 0L)
      paramLong = -paramLong; 
    if (paramLong < 10L)
      return 1; 
    int i = (64 - Long.numberOfLeadingZeros(paramLong) + 1) * 1233 >>> 12;
    long[] arrayOfLong = LONG_TEN_POWERS_TABLE;
    return (i >= arrayOfLong.length || paramLong < arrayOfLong[i]) ? i : (i + 1);
  }
  
  private static int bigDigitLength(BigInteger paramBigInteger) {
    if (paramBigInteger.signum == 0)
      return 1; 
    int i = (int)((paramBigInteger.bitLength() + 1L) * 646456993L >>> 31L);
    return (paramBigInteger.compareMagnitude(bigTenToThe(i)) < 0) ? i : (i + 1);
  }
  
  private int checkScale(long paramLong) {
    int i = (int)paramLong;
    if (i != paramLong) {
      i = (paramLong > 2147483647L) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
      BigInteger bigInteger;
      if (this.intCompact != 0L && ((bigInteger = this.intVal) == null || bigInteger
        .signum() != 0))
        throw new ArithmeticException((i > 0) ? "Underflow" : "Overflow"); 
    } 
    return i;
  }
  
  private static long compactValFor(BigInteger paramBigInteger) {
    int[] arrayOfInt = paramBigInteger.mag;
    int i = arrayOfInt.length;
    if (i == 0)
      return 0L; 
    int j = arrayOfInt[0];
    if (i > 2 || (i == 2 && j < 0))
      return Long.MIN_VALUE; 
    long l = (i == 2) ? ((arrayOfInt[1] & 0xFFFFFFFFL) + (j << 32L)) : (j & 0xFFFFFFFFL);
    return (paramBigInteger.signum < 0) ? -l : l;
  }
  
  private static int longCompareMagnitude(long paramLong1, long paramLong2) {
    if (paramLong1 < 0L)
      paramLong1 = -paramLong1; 
    if (paramLong2 < 0L)
      paramLong2 = -paramLong2; 
    return (paramLong1 < paramLong2) ? -1 : ((paramLong1 == paramLong2) ? 0 : 1);
  }
  
  private static int saturateLong(long paramLong) {
    int i = (int)paramLong;
    return (paramLong == i) ? i : ((paramLong < 0L) ? Integer.MIN_VALUE : Integer.MAX_VALUE);
  }
  
  private static void print(String paramString, BigDecimal paramBigDecimal) {
    System.err.format("%s:\tintCompact %d\tintVal %d\tscale %d\tprecision %d%n", new Object[] { paramString, 
          
          Long.valueOf(paramBigDecimal.intCompact), paramBigDecimal.intVal, 
          
          Integer.valueOf(paramBigDecimal.scale), 
          Integer.valueOf(paramBigDecimal.precision) });
  }
  
  private BigDecimal audit() {
    if (this.intCompact == Long.MIN_VALUE) {
      if (this.intVal == null) {
        print("audit", this);
        throw new AssertionError("null intVal");
      } 
      if (this.precision > 0 && this.precision != bigDigitLength(this.intVal)) {
        print("audit", this);
        throw new AssertionError("precision mismatch");
      } 
    } else {
      if (this.intVal != null) {
        long l = this.intVal.longValue();
        if (l != this.intCompact) {
          print("audit", this);
          throw new AssertionError("Inconsistent state, intCompact=" + this.intCompact + "\t intVal=" + l);
        } 
      } 
      if (this.precision > 0 && this.precision != longDigitLength(this.intCompact)) {
        print("audit", this);
        throw new AssertionError("precision mismatch");
      } 
    } 
    return this;
  }
  
  private static int checkScaleNonZero(long paramLong) {
    int i = (int)paramLong;
    if (i != paramLong)
      throw new ArithmeticException((i > 0) ? "Underflow" : "Overflow"); 
    return i;
  }
  
  private static int checkScale(long paramLong1, long paramLong2) {
    int i = (int)paramLong2;
    if (i != paramLong2) {
      i = (paramLong2 > 2147483647L) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
      if (paramLong1 != 0L)
        throw new ArithmeticException((i > 0) ? "Underflow" : "Overflow"); 
    } 
    return i;
  }
  
  private static int checkScale(BigInteger paramBigInteger, long paramLong) {
    int i = (int)paramLong;
    if (i != paramLong) {
      i = (paramLong > 2147483647L) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
      if (paramBigInteger.signum() != 0)
        throw new ArithmeticException((i > 0) ? "Underflow" : "Overflow"); 
    } 
    return i;
  }
  
  private static BigDecimal doRound(BigDecimal paramBigDecimal, MathContext paramMathContext) {
    int i = paramMathContext.precision;
    boolean bool = false;
    if (i > 0) {
      BigInteger bigInteger = paramBigDecimal.intVal;
      long l = paramBigDecimal.intCompact;
      int j = paramBigDecimal.scale;
      int k = paramBigDecimal.precision();
      int m = paramMathContext.roundingMode.oldMode;
      if (l == Long.MIN_VALUE) {
        int n = k - i;
        while (n > 0) {
          j = checkScaleNonZero(j - n);
          bigInteger = divideAndRoundByTenPow(bigInteger, n, m);
          bool = true;
          l = compactValFor(bigInteger);
          if (l != Long.MIN_VALUE) {
            k = longDigitLength(l);
            break;
          } 
          k = bigDigitLength(bigInteger);
          n = k - i;
        } 
      } 
      if (l != Long.MIN_VALUE) {
        int n = k - i;
        while (n > 0) {
          j = checkScaleNonZero(j - n);
          l = divideAndRound(l, LONG_TEN_POWERS_TABLE[n], paramMathContext.roundingMode.oldMode);
          bool = true;
          k = longDigitLength(l);
          n = k - i;
          bigInteger = null;
        } 
      } 
      return bool ? new BigDecimal(bigInteger, l, j, k) : paramBigDecimal;
    } 
    return paramBigDecimal;
  }
  
  private static BigDecimal doRound(long paramLong, int paramInt, MathContext paramMathContext) {
    int i = paramMathContext.precision;
    if (i > 0 && i < 19) {
      int j = longDigitLength(paramLong);
      int k = j - i;
      while (k > 0) {
        paramInt = checkScaleNonZero(paramInt - k);
        paramLong = divideAndRound(paramLong, LONG_TEN_POWERS_TABLE[k], paramMathContext.roundingMode.oldMode);
        j = longDigitLength(paramLong);
        k = j - i;
      } 
      return valueOf(paramLong, paramInt, j);
    } 
    return valueOf(paramLong, paramInt);
  }
  
  private static BigDecimal doRound(BigInteger paramBigInteger, int paramInt, MathContext paramMathContext) {
    int i = paramMathContext.precision;
    int j = 0;
    if (i > 0) {
      long l = compactValFor(paramBigInteger);
      int k = paramMathContext.roundingMode.oldMode;
      if (l == Long.MIN_VALUE) {
        j = bigDigitLength(paramBigInteger);
        int m = j - i;
        while (m > 0) {
          paramInt = checkScaleNonZero(paramInt - m);
          paramBigInteger = divideAndRoundByTenPow(paramBigInteger, m, k);
          l = compactValFor(paramBigInteger);
          if (l != Long.MIN_VALUE)
            break; 
          j = bigDigitLength(paramBigInteger);
          m = j - i;
        } 
      } 
      if (l != Long.MIN_VALUE) {
        j = longDigitLength(l);
        int m = j - i;
        while (m > 0) {
          paramInt = checkScaleNonZero(paramInt - m);
          l = divideAndRound(l, LONG_TEN_POWERS_TABLE[m], paramMathContext.roundingMode.oldMode);
          j = longDigitLength(l);
          m = j - i;
        } 
        return valueOf(l, paramInt, j);
      } 
    } 
    return new BigDecimal(paramBigInteger, Long.MIN_VALUE, paramInt, j);
  }
  
  private static BigInteger divideAndRoundByTenPow(BigInteger paramBigInteger, int paramInt1, int paramInt2) {
    if (paramInt1 < LONG_TEN_POWERS_TABLE.length) {
      paramBigInteger = divideAndRound(paramBigInteger, LONG_TEN_POWERS_TABLE[paramInt1], paramInt2);
    } else {
      paramBigInteger = divideAndRound(paramBigInteger, bigTenToThe(paramInt1), paramInt2);
    } 
    return paramBigInteger;
  }
  
  private static BigDecimal divideAndRound(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3) {
    long l1 = paramLong1 / paramLong2;
    if (paramInt2 == 1 && paramInt1 == paramInt3)
      return valueOf(l1, paramInt1); 
    long l2 = paramLong1 % paramLong2;
    boolean bool = (((paramLong1 < 0L) ? true : false) == ((paramLong2 < 0L) ? true : false)) ? true : true;
    if (l2 != 0L) {
      boolean bool1 = needIncrement(paramLong2, paramInt2, bool, l1, l2);
      return valueOf(bool1 ? (l1 + bool) : l1, paramInt1);
    } 
    if (paramInt3 != paramInt1)
      return createAndStripZerosToMatchScale(l1, paramInt1, paramInt3); 
    return valueOf(l1, paramInt1);
  }
  
  private static long divideAndRound(long paramLong1, long paramLong2, int paramInt) {
    long l1 = paramLong1 / paramLong2;
    if (paramInt == 1)
      return l1; 
    long l2 = paramLong1 % paramLong2;
    boolean bool = (((paramLong1 < 0L) ? true : false) == ((paramLong2 < 0L) ? true : false)) ? true : true;
    if (l2 != 0L) {
      boolean bool1 = needIncrement(paramLong2, paramInt, bool, l1, l2);
      return bool1 ? (l1 + bool) : l1;
    } 
    return l1;
  }
  
  private static boolean commonNeedIncrement(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
    switch (paramInt1) {
      case 7:
        throw new ArithmeticException("Rounding necessary");
      case 0:
        return true;
      case 1:
        return false;
      case 2:
        return (paramInt2 > 0);
      case 3:
        return (paramInt2 < 0);
    } 
    assert paramInt1 >= 4 && paramInt1 <= 6 : "Unexpected rounding mode" + 
      RoundingMode.valueOf(paramInt1);
    if (paramInt3 < 0)
      return false; 
    if (paramInt3 > 0)
      return true; 
    assert paramInt3 == 0;
    switch (paramInt1) {
      case 5:
        return false;
      case 4:
        return true;
      case 6:
        return paramBoolean;
    } 
    throw new AssertionError("Unexpected rounding mode" + paramInt1);
  }
  
  private static boolean needIncrement(long paramLong1, int paramInt1, int paramInt2, long paramLong2, long paramLong3) {
    int i;
    assert paramLong3 != 0L;
    if (paramLong3 <= -4611686018427387904L || paramLong3 > 4611686018427387903L) {
      i = 1;
    } else {
      i = longCompareMagnitude(2L * paramLong3, paramLong1);
    } 
    return commonNeedIncrement(paramInt1, paramInt2, i, ((paramLong2 & 0x1L) != 0L));
  }
  
  private static BigInteger divideAndRound(BigInteger paramBigInteger, long paramLong, int paramInt) {
    long l = 0L;
    MutableBigInteger mutableBigInteger1 = null;
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger(paramBigInteger.mag);
    mutableBigInteger1 = new MutableBigInteger();
    l = mutableBigInteger2.divide(paramLong, mutableBigInteger1);
    boolean bool = (l == 0L) ? true : false;
    int i = (paramLong < 0L) ? -paramBigInteger.signum : paramBigInteger.signum;
    if (!bool && 
      needIncrement(paramLong, paramInt, i, mutableBigInteger1, l))
      mutableBigInteger1.add(MutableBigInteger.ONE); 
    return mutableBigInteger1.toBigInteger(i);
  }
  
  private static BigDecimal divideAndRound(BigInteger paramBigInteger, long paramLong, int paramInt1, int paramInt2, int paramInt3) {
    long l = 0L;
    MutableBigInteger mutableBigInteger1 = null;
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger(paramBigInteger.mag);
    mutableBigInteger1 = new MutableBigInteger();
    l = mutableBigInteger2.divide(paramLong, mutableBigInteger1);
    boolean bool = (l == 0L) ? true : false;
    int i = (paramLong < 0L) ? -paramBigInteger.signum : paramBigInteger.signum;
    if (!bool) {
      if (needIncrement(paramLong, paramInt2, i, mutableBigInteger1, l))
        mutableBigInteger1.add(MutableBigInteger.ONE); 
      return mutableBigInteger1.toBigDecimal(i, paramInt1);
    } 
    if (paramInt3 != paramInt1) {
      long l1 = mutableBigInteger1.toCompactValue(i);
      if (l1 != Long.MIN_VALUE)
        return createAndStripZerosToMatchScale(l1, paramInt1, paramInt3); 
      BigInteger bigInteger = mutableBigInteger1.toBigInteger(i);
      return createAndStripZerosToMatchScale(bigInteger, paramInt1, paramInt3);
    } 
    return mutableBigInteger1.toBigDecimal(i, paramInt1);
  }
  
  private static boolean needIncrement(long paramLong1, int paramInt1, int paramInt2, MutableBigInteger paramMutableBigInteger, long paramLong2) {
    int i;
    assert paramLong2 != 0L;
    if (paramLong2 <= -4611686018427387904L || paramLong2 > 4611686018427387903L) {
      i = 1;
    } else {
      i = longCompareMagnitude(2L * paramLong2, paramLong1);
    } 
    return commonNeedIncrement(paramInt1, paramInt2, i, paramMutableBigInteger.isOdd());
  }
  
  private static BigInteger divideAndRound(BigInteger paramBigInteger1, BigInteger paramBigInteger2, int paramInt) {
    MutableBigInteger mutableBigInteger1 = new MutableBigInteger(paramBigInteger1.mag);
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger();
    MutableBigInteger mutableBigInteger3 = new MutableBigInteger(paramBigInteger2.mag);
    MutableBigInteger mutableBigInteger4 = mutableBigInteger1.divide(mutableBigInteger3, mutableBigInteger2);
    boolean bool = mutableBigInteger4.isZero();
    boolean bool1 = (paramBigInteger1.signum != paramBigInteger2.signum) ? true : true;
    if (!bool && 
      needIncrement(mutableBigInteger3, paramInt, bool1, mutableBigInteger2, mutableBigInteger4))
      mutableBigInteger2.add(MutableBigInteger.ONE); 
    return mutableBigInteger2.toBigInteger(bool1);
  }
  
  private static BigDecimal divideAndRound(BigInteger paramBigInteger1, BigInteger paramBigInteger2, int paramInt1, int paramInt2, int paramInt3) {
    MutableBigInteger mutableBigInteger1 = new MutableBigInteger(paramBigInteger1.mag);
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger();
    MutableBigInteger mutableBigInteger3 = new MutableBigInteger(paramBigInteger2.mag);
    MutableBigInteger mutableBigInteger4 = mutableBigInteger1.divide(mutableBigInteger3, mutableBigInteger2);
    boolean bool = mutableBigInteger4.isZero();
    boolean bool1 = (paramBigInteger1.signum != paramBigInteger2.signum) ? true : true;
    if (!bool) {
      if (needIncrement(mutableBigInteger3, paramInt2, bool1, mutableBigInteger2, mutableBigInteger4))
        mutableBigInteger2.add(MutableBigInteger.ONE); 
      return mutableBigInteger2.toBigDecimal(bool1, paramInt1);
    } 
    if (paramInt3 != paramInt1) {
      long l = mutableBigInteger2.toCompactValue(bool1);
      if (l != Long.MIN_VALUE)
        return createAndStripZerosToMatchScale(l, paramInt1, paramInt3); 
      BigInteger bigInteger = mutableBigInteger2.toBigInteger(bool1);
      return createAndStripZerosToMatchScale(bigInteger, paramInt1, paramInt3);
    } 
    return mutableBigInteger2.toBigDecimal(bool1, paramInt1);
  }
  
  private static boolean needIncrement(MutableBigInteger paramMutableBigInteger1, int paramInt1, int paramInt2, MutableBigInteger paramMutableBigInteger2, MutableBigInteger paramMutableBigInteger3) {
    assert !paramMutableBigInteger3.isZero();
    int i = paramMutableBigInteger3.compareHalf(paramMutableBigInteger1);
    return commonNeedIncrement(paramInt1, paramInt2, i, paramMutableBigInteger2.isOdd());
  }
  
  private static BigDecimal createAndStripZerosToMatchScale(BigInteger paramBigInteger, int paramInt, long paramLong) {
    while (paramBigInteger.compareMagnitude(BigInteger.TEN) >= 0 && paramInt > paramLong) {
      if (paramBigInteger.testBit(0))
        break; 
      BigInteger[] arrayOfBigInteger = paramBigInteger.divideAndRemainder(BigInteger.TEN);
      if (arrayOfBigInteger[1].signum() != 0)
        break; 
      paramBigInteger = arrayOfBigInteger[0];
      paramInt = checkScale(paramBigInteger, paramInt - 1L);
    } 
    return valueOf(paramBigInteger, paramInt, 0);
  }
  
  private static BigDecimal createAndStripZerosToMatchScale(long paramLong1, int paramInt, long paramLong2) {
    while (Math.abs(paramLong1) >= 10L && paramInt > paramLong2 && (
      paramLong1 & 0x1L) == 0L) {
      long l = paramLong1 % 10L;
      if (l != 0L)
        break; 
      paramLong1 /= 10L;
      paramInt = checkScale(paramLong1, paramInt - 1L);
    } 
    return valueOf(paramLong1, paramInt);
  }
  
  private static BigDecimal stripZerosToMatchScale(BigInteger paramBigInteger, long paramLong, int paramInt1, int paramInt2) {
    if (paramLong != Long.MIN_VALUE)
      return createAndStripZerosToMatchScale(paramLong, paramInt1, paramInt2); 
    return createAndStripZerosToMatchScale((paramBigInteger == null) ? INFLATED_BIGINT : paramBigInteger, paramInt1, paramInt2);
  }
  
  private static long add(long paramLong1, long paramLong2) {
    long l = paramLong1 + paramLong2;
    if (((l ^ paramLong1) & (l ^ paramLong2)) >= 0L)
      return l; 
    return Long.MIN_VALUE;
  }
  
  private static BigDecimal add(long paramLong1, long paramLong2, int paramInt) {
    long l = add(paramLong1, paramLong2);
    if (l != Long.MIN_VALUE)
      return valueOf(l, paramInt); 
    return new BigDecimal(BigInteger.valueOf(paramLong1).add(paramLong2), paramInt);
  }
  
  private static BigDecimal add(long paramLong1, int paramInt1, long paramLong2, int paramInt2) {
    long l1 = paramInt1 - paramInt2;
    if (l1 == 0L)
      return add(paramLong1, paramLong2, paramInt1); 
    if (l1 < 0L) {
      int j = checkScale(paramLong1, -l1);
      long l = longMultiplyPowerTen(paramLong1, j);
      if (l != Long.MIN_VALUE)
        return add(l, paramLong2, paramInt2); 
      BigInteger bigInteger1 = bigMultiplyPowerTen(paramLong1, j).add(paramLong2);
      return ((paramLong1 ^ paramLong2) >= 0L) ? new BigDecimal(bigInteger1, Long.MIN_VALUE, paramInt2, 0) : 
        
        valueOf(bigInteger1, paramInt2, 0);
    } 
    int i = checkScale(paramLong2, l1);
    long l2 = longMultiplyPowerTen(paramLong2, i);
    if (l2 != Long.MIN_VALUE)
      return add(paramLong1, l2, paramInt1); 
    BigInteger bigInteger = bigMultiplyPowerTen(paramLong2, i).add(paramLong1);
    return ((paramLong1 ^ paramLong2) >= 0L) ? new BigDecimal(bigInteger, Long.MIN_VALUE, paramInt1, 0) : 
      
      valueOf(bigInteger, paramInt1, 0);
  }
  
  private static BigDecimal add(long paramLong, int paramInt1, BigInteger paramBigInteger, int paramInt2) {
    BigInteger bigInteger;
    int i = paramInt1;
    long l = i - paramInt2;
    boolean bool = (Long.signum(paramLong) == paramBigInteger.signum) ? true : false;
    if (l < 0L) {
      int j = checkScale(paramLong, -l);
      i = paramInt2;
      long l1 = longMultiplyPowerTen(paramLong, j);
      if (l1 == Long.MIN_VALUE) {
        bigInteger = paramBigInteger.add(bigMultiplyPowerTen(paramLong, j));
      } else {
        bigInteger = paramBigInteger.add(l1);
      } 
    } else {
      int j = checkScale(paramBigInteger, l);
      paramBigInteger = bigMultiplyPowerTen(paramBigInteger, j);
      bigInteger = paramBigInteger.add(paramLong);
    } 
    return bool ? new BigDecimal(bigInteger, Long.MIN_VALUE, i, 0) : 
      
      valueOf(bigInteger, i, 0);
  }
  
  private static BigDecimal add(BigInteger paramBigInteger1, int paramInt1, BigInteger paramBigInteger2, int paramInt2) {
    int i = paramInt1;
    long l = i - paramInt2;
    if (l != 0L)
      if (l < 0L) {
        int j = checkScale(paramBigInteger1, -l);
        i = paramInt2;
        paramBigInteger1 = bigMultiplyPowerTen(paramBigInteger1, j);
      } else {
        int j = checkScale(paramBigInteger2, l);
        paramBigInteger2 = bigMultiplyPowerTen(paramBigInteger2, j);
      }  
    BigInteger bigInteger = paramBigInteger1.add(paramBigInteger2);
    return (paramBigInteger1.signum == paramBigInteger2.signum) ? new BigDecimal(bigInteger, Long.MIN_VALUE, i, 0) : 
      
      valueOf(bigInteger, i, 0);
  }
  
  private static BigInteger bigMultiplyPowerTen(long paramLong, int paramInt) {
    if (paramInt <= 0)
      return BigInteger.valueOf(paramLong); 
    return bigTenToThe(paramInt).multiply(paramLong);
  }
  
  private static BigInteger bigMultiplyPowerTen(BigInteger paramBigInteger, int paramInt) {
    if (paramInt <= 0)
      return paramBigInteger; 
    if (paramInt < LONG_TEN_POWERS_TABLE.length)
      return paramBigInteger.multiply(LONG_TEN_POWERS_TABLE[paramInt]); 
    return paramBigInteger.multiply(bigTenToThe(paramInt));
  }
  
  private static BigDecimal divideSmallFastPath(long paramLong1, int paramInt1, long paramLong2, int paramInt2, long paramLong3, MathContext paramMathContext) {
    BigDecimal bigDecimal;
    int i = paramMathContext.precision;
    int j = paramMathContext.roundingMode.oldMode;
    assert paramInt1 <= paramInt2 && paramInt2 < 18 && i < 18;
    int k = paramInt2 - paramInt1;
    long l = (k == 0) ? paramLong1 : longMultiplyPowerTen(paramLong1, k);
    int m = longCompareMagnitude(l, paramLong2);
    if (m > 0) {
      paramInt2--;
      int n = checkScaleNonZero(paramLong3 + paramInt2 - paramInt1 + i);
      if (checkScaleNonZero(i + paramInt2 - paramInt1) > 0) {
        int i1 = checkScaleNonZero(i + paramInt2 - paramInt1);
        long l1;
        if ((l1 = longMultiplyPowerTen(paramLong1, i1)) == Long.MIN_VALUE) {
          bigDecimal = null;
          if (i - 1 >= 0 && i - 1 < LONG_TEN_POWERS_TABLE.length)
            bigDecimal = multiplyDivideAndRound(LONG_TEN_POWERS_TABLE[i - 1], l, paramLong2, n, j, checkScaleNonZero(paramLong3)); 
          if (bigDecimal == null) {
            BigInteger bigInteger = bigMultiplyPowerTen(l, i - 1);
            bigDecimal = divideAndRound(bigInteger, paramLong2, n, j, 
                checkScaleNonZero(paramLong3));
          } 
        } else {
          bigDecimal = divideAndRound(l1, paramLong2, n, j, checkScaleNonZero(paramLong3));
        } 
      } else {
        int i1 = checkScaleNonZero(paramInt1 - i);
        if (i1 == paramInt2) {
          bigDecimal = divideAndRound(paramLong1, paramLong2, n, j, checkScaleNonZero(paramLong3));
        } else {
          int i2 = checkScaleNonZero(i1 - paramInt2);
          long l1;
          if ((l1 = longMultiplyPowerTen(paramLong2, i2)) == Long.MIN_VALUE) {
            BigInteger bigInteger = bigMultiplyPowerTen(paramLong2, i2);
            bigDecimal = divideAndRound(BigInteger.valueOf(paramLong1), bigInteger, n, j, 
                checkScaleNonZero(paramLong3));
          } else {
            bigDecimal = divideAndRound(paramLong1, l1, n, j, checkScaleNonZero(paramLong3));
          } 
        } 
      } 
    } else {
      int n = checkScaleNonZero(paramLong3 + paramInt2 - paramInt1 + i);
      if (m == 0) {
        bigDecimal = roundedTenPower((((l < 0L) ? true : false) == ((paramLong2 < 0L) ? true : false)) ? 1 : -1, i, n, checkScaleNonZero(paramLong3));
      } else {
        long l1;
        if ((l1 = longMultiplyPowerTen(l, i)) == Long.MIN_VALUE) {
          bigDecimal = null;
          if (i < LONG_TEN_POWERS_TABLE.length)
            bigDecimal = multiplyDivideAndRound(LONG_TEN_POWERS_TABLE[i], l, paramLong2, n, j, checkScaleNonZero(paramLong3)); 
          if (bigDecimal == null) {
            BigInteger bigInteger = bigMultiplyPowerTen(l, i);
            bigDecimal = divideAndRound(bigInteger, paramLong2, n, j, 
                checkScaleNonZero(paramLong3));
          } 
        } else {
          bigDecimal = divideAndRound(l1, paramLong2, n, j, checkScaleNonZero(paramLong3));
        } 
      } 
    } 
    return doRound(bigDecimal, paramMathContext);
  }
  
  private static BigDecimal divide(long paramLong1, int paramInt1, long paramLong2, int paramInt2, long paramLong3, MathContext paramMathContext) {
    BigDecimal bigDecimal;
    int i = paramMathContext.precision;
    if (paramInt1 <= paramInt2 && paramInt2 < 18 && i < 18)
      return divideSmallFastPath(paramLong1, paramInt1, paramLong2, paramInt2, paramLong3, paramMathContext); 
    if (compareMagnitudeNormalized(paramLong1, paramInt1, paramLong2, paramInt2) > 0)
      paramInt2--; 
    int j = paramMathContext.roundingMode.oldMode;
    int k = checkScaleNonZero(paramLong3 + paramInt2 - paramInt1 + i);
    if (checkScaleNonZero(i + paramInt2 - paramInt1) > 0) {
      int m = checkScaleNonZero(i + paramInt2 - paramInt1);
      long l;
      if ((l = longMultiplyPowerTen(paramLong1, m)) == Long.MIN_VALUE) {
        BigInteger bigInteger = bigMultiplyPowerTen(paramLong1, m);
        bigDecimal = divideAndRound(bigInteger, paramLong2, k, j, checkScaleNonZero(paramLong3));
      } else {
        bigDecimal = divideAndRound(l, paramLong2, k, j, checkScaleNonZero(paramLong3));
      } 
    } else {
      int m = checkScaleNonZero(paramInt1 - i);
      if (m == paramInt2) {
        bigDecimal = divideAndRound(paramLong1, paramLong2, k, j, checkScaleNonZero(paramLong3));
      } else {
        int n = checkScaleNonZero(m - paramInt2);
        long l;
        if ((l = longMultiplyPowerTen(paramLong2, n)) == Long.MIN_VALUE) {
          BigInteger bigInteger = bigMultiplyPowerTen(paramLong2, n);
          bigDecimal = divideAndRound(BigInteger.valueOf(paramLong1), bigInteger, k, j, 
              checkScaleNonZero(paramLong3));
        } else {
          bigDecimal = divideAndRound(paramLong1, l, k, j, checkScaleNonZero(paramLong3));
        } 
      } 
    } 
    return doRound(bigDecimal, paramMathContext);
  }
  
  private static BigDecimal divide(BigInteger paramBigInteger, int paramInt1, long paramLong1, int paramInt2, long paramLong2, MathContext paramMathContext) {
    BigDecimal bigDecimal;
    if (-compareMagnitudeNormalized(paramLong1, paramInt2, paramBigInteger, paramInt1) > 0)
      paramInt2--; 
    int i = paramMathContext.precision;
    int j = paramMathContext.roundingMode.oldMode;
    int k = checkScaleNonZero(paramLong2 + paramInt2 - paramInt1 + i);
    if (checkScaleNonZero(i + paramInt2 - paramInt1) > 0) {
      int m = checkScaleNonZero(i + paramInt2 - paramInt1);
      BigInteger bigInteger = bigMultiplyPowerTen(paramBigInteger, m);
      bigDecimal = divideAndRound(bigInteger, paramLong1, k, j, checkScaleNonZero(paramLong2));
    } else {
      int m = checkScaleNonZero(paramInt1 - i);
      if (m == paramInt2) {
        bigDecimal = divideAndRound(paramBigInteger, paramLong1, k, j, checkScaleNonZero(paramLong2));
      } else {
        int n = checkScaleNonZero(m - paramInt2);
        long l;
        if ((l = longMultiplyPowerTen(paramLong1, n)) == Long.MIN_VALUE) {
          BigInteger bigInteger = bigMultiplyPowerTen(paramLong1, n);
          bigDecimal = divideAndRound(paramBigInteger, bigInteger, k, j, checkScaleNonZero(paramLong2));
        } else {
          bigDecimal = divideAndRound(paramBigInteger, l, k, j, checkScaleNonZero(paramLong2));
        } 
      } 
    } 
    return doRound(bigDecimal, paramMathContext);
  }
  
  private static BigDecimal divide(long paramLong1, int paramInt1, BigInteger paramBigInteger, int paramInt2, long paramLong2, MathContext paramMathContext) {
    BigDecimal bigDecimal;
    if (compareMagnitudeNormalized(paramLong1, paramInt1, paramBigInteger, paramInt2) > 0)
      paramInt2--; 
    int i = paramMathContext.precision;
    int j = paramMathContext.roundingMode.oldMode;
    int k = checkScaleNonZero(paramLong2 + paramInt2 - paramInt1 + i);
    if (checkScaleNonZero(i + paramInt2 - paramInt1) > 0) {
      int m = checkScaleNonZero(i + paramInt2 - paramInt1);
      BigInteger bigInteger = bigMultiplyPowerTen(paramLong1, m);
      bigDecimal = divideAndRound(bigInteger, paramBigInteger, k, j, checkScaleNonZero(paramLong2));
    } else {
      int m = checkScaleNonZero(paramInt1 - i);
      int n = checkScaleNonZero(m - paramInt2);
      BigInteger bigInteger = bigMultiplyPowerTen(paramBigInteger, n);
      bigDecimal = divideAndRound(BigInteger.valueOf(paramLong1), bigInteger, k, j, checkScaleNonZero(paramLong2));
    } 
    return doRound(bigDecimal, paramMathContext);
  }
  
  private static BigDecimal divide(BigInteger paramBigInteger1, int paramInt1, BigInteger paramBigInteger2, int paramInt2, long paramLong, MathContext paramMathContext) {
    BigDecimal bigDecimal;
    if (compareMagnitudeNormalized(paramBigInteger1, paramInt1, paramBigInteger2, paramInt2) > 0)
      paramInt2--; 
    int i = paramMathContext.precision;
    int j = paramMathContext.roundingMode.oldMode;
    int k = checkScaleNonZero(paramLong + paramInt2 - paramInt1 + i);
    if (checkScaleNonZero(i + paramInt2 - paramInt1) > 0) {
      int m = checkScaleNonZero(i + paramInt2 - paramInt1);
      BigInteger bigInteger = bigMultiplyPowerTen(paramBigInteger1, m);
      bigDecimal = divideAndRound(bigInteger, paramBigInteger2, k, j, checkScaleNonZero(paramLong));
    } else {
      int m = checkScaleNonZero(paramInt1 - i);
      int n = checkScaleNonZero(m - paramInt2);
      BigInteger bigInteger = bigMultiplyPowerTen(paramBigInteger2, n);
      bigDecimal = divideAndRound(paramBigInteger1, bigInteger, k, j, checkScaleNonZero(paramLong));
    } 
    return doRound(bigDecimal, paramMathContext);
  }
  
  private static BigDecimal multiplyDivideAndRound(long paramLong1, long paramLong2, long paramLong3, int paramInt1, int paramInt2, int paramInt3) {
    int i = Long.signum(paramLong1) * Long.signum(paramLong2) * Long.signum(paramLong3);
    paramLong1 = Math.abs(paramLong1);
    paramLong2 = Math.abs(paramLong2);
    paramLong3 = Math.abs(paramLong3);
    long l1 = paramLong1 >>> 32L;
    long l2 = paramLong1 & 0xFFFFFFFFL;
    long l3 = paramLong2 >>> 32L;
    long l4 = paramLong2 & 0xFFFFFFFFL;
    long l5 = l2 * l4;
    long l6 = l5 & 0xFFFFFFFFL;
    long l7 = l5 >>> 32L;
    l5 = l1 * l4 + l7;
    l7 = l5 & 0xFFFFFFFFL;
    long l8 = l5 >>> 32L;
    l5 = l2 * l3 + l7;
    l7 = l5 & 0xFFFFFFFFL;
    l8 += l5 >>> 32L;
    long l9 = l8 >>> 32L;
    l8 &= 0xFFFFFFFFL;
    l5 = l1 * l3 + l8;
    l8 = l5 & 0xFFFFFFFFL;
    l9 = (l5 >>> 32L) + l9 & 0xFFFFFFFFL;
    long l10 = make64(l9, l8);
    long l11 = make64(l7, l6);
    return divideAndRound128(l10, l11, paramLong3, i, paramInt1, paramInt2, paramInt3);
  }
  
  private static BigDecimal divideAndRound128(long paramLong1, long paramLong2, long paramLong3, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramLong1 >= paramLong3)
      return null; 
    int i = Long.numberOfLeadingZeros(paramLong3);
    paramLong3 <<= i;
    long l1 = paramLong3 >>> 32L;
    long l2 = paramLong3 & 0xFFFFFFFFL;
    long l6 = paramLong2 << i;
    long l7 = l6 >>> 32L;
    long l8 = l6 & 0xFFFFFFFFL;
    l6 = paramLong1 << i | paramLong2 >>> 64 - i;
    long l9 = l6 & 0xFFFFFFFFL;
    l6 = divWord(l6, l1);
    long l3 = l6 & 0xFFFFFFFFL;
    long l5 = l6 >>> 32L;
    while (l3 >= 4294967296L || unsignedLongCompare(l3 * l2, make64(l5, l7))) {
      l3--;
      l5 += l1;
      if (l5 >= 4294967296L)
        break; 
    } 
    l6 = mulsub(l9, l7, l1, l2, l3);
    l7 = l6 & 0xFFFFFFFFL;
    l6 = divWord(l6, l1);
    long l4 = l6 & 0xFFFFFFFFL;
    l5 = l6 >>> 32L;
    while (l4 >= 4294967296L || unsignedLongCompare(l4 * l2, make64(l5, l8))) {
      l4--;
      l5 += l1;
      if (l5 >= 4294967296L)
        break; 
    } 
    if ((int)l3 < 0) {
      MutableBigInteger mutableBigInteger = new MutableBigInteger(new int[] { (int)l3, (int)l4 });
      if (paramInt3 == 1 && paramInt2 == paramInt4)
        return mutableBigInteger.toBigDecimal(paramInt1, paramInt2); 
      long l = mulsub(l7, l8, l1, l2, l4) >>> i;
      if (l != 0L) {
        if (needIncrement(paramLong3 >>> i, paramInt3, paramInt1, mutableBigInteger, l))
          mutableBigInteger.add(MutableBigInteger.ONE); 
        return mutableBigInteger.toBigDecimal(paramInt1, paramInt2);
      } 
      if (paramInt4 != paramInt2) {
        BigInteger bigInteger = mutableBigInteger.toBigInteger(paramInt1);
        return createAndStripZerosToMatchScale(bigInteger, paramInt2, paramInt4);
      } 
      return mutableBigInteger.toBigDecimal(paramInt1, paramInt2);
    } 
    long l10 = make64(l3, l4);
    l10 *= paramInt1;
    if (paramInt3 == 1 && paramInt2 == paramInt4)
      return valueOf(l10, paramInt2); 
    long l11 = mulsub(l7, l8, l1, l2, l4) >>> i;
    if (l11 != 0L) {
      boolean bool = needIncrement(paramLong3 >>> i, paramInt3, paramInt1, l10, l11);
      return valueOf(bool ? (l10 + paramInt1) : l10, paramInt2);
    } 
    if (paramInt4 != paramInt2)
      return createAndStripZerosToMatchScale(l10, paramInt2, paramInt4); 
    return valueOf(l10, paramInt2);
  }
  
  private static BigDecimal roundedTenPower(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt3 > paramInt4) {
      int i = paramInt3 - paramInt4;
      if (i < paramInt2)
        return scaledTenPow(paramInt2 - i, paramInt1, paramInt4); 
      return valueOf(paramInt1, paramInt3 - paramInt2);
    } 
    return scaledTenPow(paramInt2, paramInt1, paramInt3);
  }
  
  static BigDecimal scaledTenPow(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt1 < LONG_TEN_POWERS_TABLE.length)
      return valueOf(paramInt2 * LONG_TEN_POWERS_TABLE[paramInt1], paramInt3); 
    BigInteger bigInteger = bigTenToThe(paramInt1);
    if (paramInt2 == -1)
      bigInteger = bigInteger.negate(); 
    return new BigDecimal(bigInteger, Long.MIN_VALUE, paramInt3, paramInt1 + 1);
  }
  
  private static long divWord(long paramLong1, long paramLong2) {
    if (paramLong2 == 1L) {
      long l = (int)paramLong1;
      return l & 0xFFFFFFFFL;
    } 
    long l2 = (paramLong1 >>> 1L) / (paramLong2 >>> 1L);
    long l1 = paramLong1 - l2 * paramLong2;
    while (l1 < 0L) {
      l1 += paramLong2;
      l2--;
    } 
    while (l1 >= paramLong2) {
      l1 -= paramLong2;
      l2++;
    } 
    return l1 << 32L | l2 & 0xFFFFFFFFL;
  }
  
  private static long make64(long paramLong1, long paramLong2) {
    return paramLong1 << 32L | paramLong2;
  }
  
  private static long mulsub(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) {
    long l = paramLong2 - paramLong5 * paramLong4;
    return make64(paramLong1 + (l >>> 32L) - paramLong5 * paramLong3, l & 0xFFFFFFFFL);
  }
  
  private static boolean unsignedLongCompare(long paramLong1, long paramLong2) {
    return (paramLong1 + Long.MIN_VALUE > paramLong2 + Long.MIN_VALUE);
  }
  
  private static boolean unsignedLongCompareEq(long paramLong1, long paramLong2) {
    return (paramLong1 + Long.MIN_VALUE >= paramLong2 + Long.MIN_VALUE);
  }
  
  private static int compareMagnitudeNormalized(long paramLong1, int paramInt1, long paramLong2, int paramInt2) {
    int i = paramInt1 - paramInt2;
    if (i != 0)
      if (i < 0) {
        paramLong1 = longMultiplyPowerTen(paramLong1, -i);
      } else {
        paramLong2 = longMultiplyPowerTen(paramLong2, i);
      }  
    if (paramLong1 != Long.MIN_VALUE)
      return (paramLong2 != Long.MIN_VALUE) ? longCompareMagnitude(paramLong1, paramLong2) : -1; 
    return 1;
  }
  
  private static int compareMagnitudeNormalized(long paramLong, int paramInt1, BigInteger paramBigInteger, int paramInt2) {
    if (paramLong == 0L)
      return -1; 
    int i = paramInt1 - paramInt2;
    if (i < 0 && 
      longMultiplyPowerTen(paramLong, -i) == Long.MIN_VALUE)
      return bigMultiplyPowerTen(paramLong, -i).compareMagnitude(paramBigInteger); 
    return -1;
  }
  
  private static int compareMagnitudeNormalized(BigInteger paramBigInteger1, int paramInt1, BigInteger paramBigInteger2, int paramInt2) {
    int i = paramInt1 - paramInt2;
    if (i < 0)
      return bigMultiplyPowerTen(paramBigInteger1, -i).compareMagnitude(paramBigInteger2); 
    return paramBigInteger1.compareMagnitude(bigMultiplyPowerTen(paramBigInteger2, i));
  }
  
  private static long multiply(long paramLong1, long paramLong2) {
    long l1 = paramLong1 * paramLong2;
    long l2 = Math.abs(paramLong1);
    long l3 = Math.abs(paramLong2);
    if ((l2 | l3) >>> 31L == 0L || paramLong2 == 0L || l1 / paramLong2 == paramLong1)
      return l1; 
    return Long.MIN_VALUE;
  }
  
  private static BigDecimal multiply(long paramLong1, long paramLong2, int paramInt) {
    long l = multiply(paramLong1, paramLong2);
    if (l != Long.MIN_VALUE)
      return valueOf(l, paramInt); 
    return new BigDecimal(BigInteger.valueOf(paramLong1).multiply(paramLong2), Long.MIN_VALUE, paramInt, 0);
  }
  
  private static BigDecimal multiply(long paramLong, BigInteger paramBigInteger, int paramInt) {
    if (paramLong == 0L)
      return zeroValueOf(paramInt); 
    return new BigDecimal(paramBigInteger.multiply(paramLong), Long.MIN_VALUE, paramInt, 0);
  }
  
  private static BigDecimal multiply(BigInteger paramBigInteger1, BigInteger paramBigInteger2, int paramInt) {
    return new BigDecimal(paramBigInteger1.multiply(paramBigInteger2), Long.MIN_VALUE, paramInt, 0);
  }
  
  private static BigDecimal multiplyAndRound(long paramLong1, long paramLong2, int paramInt, MathContext paramMathContext) {
    long l1 = multiply(paramLong1, paramLong2);
    if (l1 != Long.MIN_VALUE)
      return doRound(l1, paramInt, paramMathContext); 
    int i = 1;
    if (paramLong1 < 0L) {
      paramLong1 = -paramLong1;
      i = -1;
    } 
    if (paramLong2 < 0L) {
      paramLong2 = -paramLong2;
      i *= -1;
    } 
    long l2 = paramLong1 >>> 32L;
    long l3 = paramLong1 & 0xFFFFFFFFL;
    long l4 = paramLong2 >>> 32L;
    long l5 = paramLong2 & 0xFFFFFFFFL;
    l1 = l3 * l5;
    long l6 = l1 & 0xFFFFFFFFL;
    long l7 = l1 >>> 32L;
    l1 = l2 * l5 + l7;
    l7 = l1 & 0xFFFFFFFFL;
    long l8 = l1 >>> 32L;
    l1 = l3 * l4 + l7;
    l7 = l1 & 0xFFFFFFFFL;
    l8 += l1 >>> 32L;
    long l9 = l8 >>> 32L;
    l8 &= 0xFFFFFFFFL;
    l1 = l2 * l4 + l8;
    l8 = l1 & 0xFFFFFFFFL;
    l9 = (l1 >>> 32L) + l9 & 0xFFFFFFFFL;
    long l10 = make64(l9, l8);
    long l11 = make64(l7, l6);
    BigDecimal bigDecimal = doRound128(l10, l11, i, paramInt, paramMathContext);
    if (bigDecimal != null)
      return bigDecimal; 
    bigDecimal = new BigDecimal(BigInteger.valueOf(paramLong1).multiply(paramLong2 * i), Long.MIN_VALUE, paramInt, 0);
    return doRound(bigDecimal, paramMathContext);
  }
  
  private static BigDecimal multiplyAndRound(long paramLong, BigInteger paramBigInteger, int paramInt, MathContext paramMathContext) {
    if (paramLong == 0L)
      return zeroValueOf(paramInt); 
    return doRound(paramBigInteger.multiply(paramLong), paramInt, paramMathContext);
  }
  
  private static BigDecimal multiplyAndRound(BigInteger paramBigInteger1, BigInteger paramBigInteger2, int paramInt, MathContext paramMathContext) {
    return doRound(paramBigInteger1.multiply(paramBigInteger2), paramInt, paramMathContext);
  }
  
  private static BigDecimal doRound128(long paramLong1, long paramLong2, int paramInt1, int paramInt2, MathContext paramMathContext) {
    int i = paramMathContext.precision;
    BigDecimal bigDecimal = null;
    int j;
    if ((j = precision(paramLong1, paramLong2) - i) > 0 && j < LONG_TEN_POWERS_TABLE.length) {
      paramInt2 = checkScaleNonZero(paramInt2 - j);
      bigDecimal = divideAndRound128(paramLong1, paramLong2, LONG_TEN_POWERS_TABLE[j], paramInt1, paramInt2, paramMathContext.roundingMode.oldMode, paramInt2);
    } 
    if (bigDecimal != null)
      return doRound(bigDecimal, paramMathContext); 
    return null;
  }
  
  private static final long[][] LONGLONG_TEN_POWERS_TABLE = new long[][] { 
      { 0L, -8446744073709551616L }, { 5L, 7766279631452241920L }, { 54L, 3875820019684212736L }, { 542L, 1864712049423024128L }, { 5421L, 200376420520689664L }, { 54210L, 2003764205206896640L }, { 542101L, 1590897978359414784L }, { 5421010L, -2537764290115403776L }, { 54210108L, -6930898827444486144L }, { 542101086L, 4477988020393345024L }, 
      { 5421010862L, 7886392056514347008L }, { 54210108624L, 5076944270305263616L }, { 542101086242L, -4570789518076018688L }, { 5421010862427L, -8814407033341083648L }, { 54210108624275L, 4089650035136921600L }, { 542101086242752L, 4003012203950112768L }, { 5421010862427522L, 3136633892082024448L }, { 54210108624275221L, -5527149226598858752L }, { 542101086242752217L, 68739955140067328L }, { 5421010862427522170L, 687399551400673280L } };
  
  private static int precision(long paramLong1, long paramLong2) {
    if (paramLong1 == 0L) {
      if (paramLong2 >= 0L)
        return longDigitLength(paramLong2); 
      return unsignedLongCompareEq(paramLong2, LONGLONG_TEN_POWERS_TABLE[0][1]) ? 20 : 19;
    } 
    int i = (128 - Long.numberOfLeadingZeros(paramLong1) + 1) * 1233 >>> 12;
    int j = i - 19;
    return (j >= LONGLONG_TEN_POWERS_TABLE.length || longLongCompareMagnitude(paramLong1, paramLong2, LONGLONG_TEN_POWERS_TABLE[j][0], LONGLONG_TEN_POWERS_TABLE[j][1])) ? i : (i + 1);
  }
  
  private static boolean longLongCompareMagnitude(long paramLong1, long paramLong2, long paramLong3, long paramLong4) {
    if (paramLong1 != paramLong3)
      return (paramLong1 < paramLong3); 
    return (paramLong2 + Long.MIN_VALUE < paramLong4 + Long.MIN_VALUE);
  }
  
  private static BigDecimal divide(long paramLong1, int paramInt1, long paramLong2, int paramInt2, int paramInt3, int paramInt4) {
    if (checkScale(paramLong1, paramInt3 + paramInt2) > paramInt1) {
      int k = paramInt3 + paramInt2;
      int m = k - paramInt1;
      if (m < LONG_TEN_POWERS_TABLE.length) {
        long l1 = paramLong1;
        if ((l1 = longMultiplyPowerTen(l1, m)) != Long.MIN_VALUE)
          return divideAndRound(l1, paramLong2, paramInt3, paramInt4, paramInt3); 
        BigDecimal bigDecimal = multiplyDivideAndRound(LONG_TEN_POWERS_TABLE[m], paramLong1, paramLong2, paramInt3, paramInt4, paramInt3);
        if (bigDecimal != null)
          return bigDecimal; 
      } 
      BigInteger bigInteger1 = bigMultiplyPowerTen(paramLong1, m);
      return divideAndRound(bigInteger1, paramLong2, paramInt3, paramInt4, paramInt3);
    } 
    int i = checkScale(paramLong2, paramInt1 - paramInt3);
    int j = i - paramInt2;
    long l = paramLong2;
    if (j < LONG_TEN_POWERS_TABLE.length && (l = longMultiplyPowerTen(l, j)) != Long.MIN_VALUE)
      return divideAndRound(paramLong1, l, paramInt3, paramInt4, paramInt3); 
    BigInteger bigInteger = bigMultiplyPowerTen(paramLong2, j);
    return divideAndRound(BigInteger.valueOf(paramLong1), bigInteger, paramInt3, paramInt4, paramInt3);
  }
  
  private static BigDecimal divide(BigInteger paramBigInteger, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4) {
    if (checkScale(paramBigInteger, paramInt3 + paramInt2) > paramInt1) {
      int k = paramInt3 + paramInt2;
      int m = k - paramInt1;
      BigInteger bigInteger1 = bigMultiplyPowerTen(paramBigInteger, m);
      return divideAndRound(bigInteger1, paramLong, paramInt3, paramInt4, paramInt3);
    } 
    int i = checkScale(paramLong, paramInt1 - paramInt3);
    int j = i - paramInt2;
    long l = paramLong;
    if (j < LONG_TEN_POWERS_TABLE.length && (l = longMultiplyPowerTen(l, j)) != Long.MIN_VALUE)
      return divideAndRound(paramBigInteger, l, paramInt3, paramInt4, paramInt3); 
    BigInteger bigInteger = bigMultiplyPowerTen(paramLong, j);
    return divideAndRound(paramBigInteger, bigInteger, paramInt3, paramInt4, paramInt3);
  }
  
  private static BigDecimal divide(long paramLong, int paramInt1, BigInteger paramBigInteger, int paramInt2, int paramInt3, int paramInt4) {
    if (checkScale(paramLong, paramInt3 + paramInt2) > paramInt1) {
      int k = paramInt3 + paramInt2;
      int m = k - paramInt1;
      BigInteger bigInteger1 = bigMultiplyPowerTen(paramLong, m);
      return divideAndRound(bigInteger1, paramBigInteger, paramInt3, paramInt4, paramInt3);
    } 
    int i = checkScale(paramBigInteger, paramInt1 - paramInt3);
    int j = i - paramInt2;
    BigInteger bigInteger = bigMultiplyPowerTen(paramBigInteger, j);
    return divideAndRound(BigInteger.valueOf(paramLong), bigInteger, paramInt3, paramInt4, paramInt3);
  }
  
  private static BigDecimal divide(BigInteger paramBigInteger1, int paramInt1, BigInteger paramBigInteger2, int paramInt2, int paramInt3, int paramInt4) {
    if (checkScale(paramBigInteger1, paramInt3 + paramInt2) > paramInt1) {
      int k = paramInt3 + paramInt2;
      int m = k - paramInt1;
      BigInteger bigInteger1 = bigMultiplyPowerTen(paramBigInteger1, m);
      return divideAndRound(bigInteger1, paramBigInteger2, paramInt3, paramInt4, paramInt3);
    } 
    int i = checkScale(paramBigInteger2, paramInt1 - paramInt3);
    int j = i - paramInt2;
    BigInteger bigInteger = bigMultiplyPowerTen(paramBigInteger2, j);
    return divideAndRound(paramBigInteger1, bigInteger, paramInt3, paramInt4, paramInt3);
  }
  
  private static class BigDecimal {}
  
  static class BigDecimal {}
  
  private static class BigDecimal {}
}
