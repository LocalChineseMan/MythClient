package sun.misc;

import java.util.Arrays;
import java.util.regex.Matcher;

public class FloatingDecimal {
  static final int EXP_SHIFT = 52;
  
  static final long FRACT_HOB = 4503599627370496L;
  
  static final long EXP_ONE = 4607182418800017408L;
  
  static final int MAX_SMALL_BIN_EXP = 62;
  
  static final int MIN_SMALL_BIN_EXP = -21;
  
  static final int MAX_DECIMAL_DIGITS = 15;
  
  static final int MAX_DECIMAL_EXPONENT = 308;
  
  static final int MIN_DECIMAL_EXPONENT = -324;
  
  static final int BIG_DECIMAL_EXPONENT = 324;
  
  static final int MAX_NDIGITS = 1100;
  
  static final int SINGLE_EXP_SHIFT = 23;
  
  static final int SINGLE_FRACT_HOB = 8388608;
  
  static final int SINGLE_MAX_DECIMAL_DIGITS = 7;
  
  static final int SINGLE_MAX_DECIMAL_EXPONENT = 38;
  
  static final int SINGLE_MIN_DECIMAL_EXPONENT = -45;
  
  static final int SINGLE_MAX_NDIGITS = 200;
  
  static final int INT_DECIMAL_DIGITS = 9;
  
  private static final String INFINITY_REP = "Infinity";
  
  public static String toJavaFormatString(double paramDouble) {
    return getBinaryToASCIIConverter(paramDouble).toJavaFormatString();
  }
  
  public static String toJavaFormatString(float paramFloat) {
    return getBinaryToASCIIConverter(paramFloat).toJavaFormatString();
  }
  
  public static void appendTo(double paramDouble, Appendable paramAppendable) {
    getBinaryToASCIIConverter(paramDouble).appendTo(paramAppendable);
  }
  
  public static void appendTo(float paramFloat, Appendable paramAppendable) {
    getBinaryToASCIIConverter(paramFloat).appendTo(paramAppendable);
  }
  
  public static double parseDouble(String paramString) throws NumberFormatException {
    return readJavaFormatString(paramString).doubleValue();
  }
  
  public static float parseFloat(String paramString) throws NumberFormatException {
    return readJavaFormatString(paramString).floatValue();
  }
  
  private static class ExceptionalBinaryToASCIIBuffer implements BinaryToASCIIConverter {
    private final String image;
    
    private boolean isNegative;
    
    public ExceptionalBinaryToASCIIBuffer(String param1String, boolean param1Boolean) {
      this.image = param1String;
      this.isNegative = param1Boolean;
    }
    
    public String toJavaFormatString() {
      return this.image;
    }
    
    public void appendTo(Appendable param1Appendable) {
      if (param1Appendable instanceof StringBuilder) {
        ((StringBuilder)param1Appendable).append(this.image);
      } else if (param1Appendable instanceof StringBuffer) {
        ((StringBuffer)param1Appendable).append(this.image);
      } else {
        assert false;
      } 
    }
    
    public int getDecimalExponent() {
      throw new IllegalArgumentException("Exceptional value does not have an exponent");
    }
    
    public int getDigits(char[] param1ArrayOfchar) {
      throw new IllegalArgumentException("Exceptional value does not have digits");
    }
    
    public boolean isNegative() {
      return this.isNegative;
    }
    
    public boolean isExceptional() {
      return true;
    }
    
    public boolean digitsRoundedUp() {
      throw new IllegalArgumentException("Exceptional value is not rounded");
    }
    
    public boolean decimalDigitsExact() {
      throw new IllegalArgumentException("Exceptional value is not exact");
    }
  }
  
  private static final int INFINITY_LENGTH = "Infinity".length();
  
  private static final String NAN_REP = "NaN";
  
  private static final int NAN_LENGTH = "NaN".length();
  
  private static final BinaryToASCIIConverter B2AC_POSITIVE_INFINITY = new ExceptionalBinaryToASCIIBuffer("Infinity", false);
  
  private static final BinaryToASCIIConverter B2AC_NEGATIVE_INFINITY = new ExceptionalBinaryToASCIIBuffer("-Infinity", true);
  
  private static final BinaryToASCIIConverter B2AC_NOT_A_NUMBER = new ExceptionalBinaryToASCIIBuffer("NaN", false);
  
  private static final BinaryToASCIIConverter B2AC_POSITIVE_ZERO = new BinaryToASCIIBuffer(false, new char[] { '0' });
  
  private static final BinaryToASCIIConverter B2AC_NEGATIVE_ZERO = new BinaryToASCIIBuffer(true, new char[] { '0' });
  
  static class BinaryToASCIIBuffer implements BinaryToASCIIConverter {
    private boolean isNegative;
    
    private int decExponent;
    
    private int firstDigitIndex;
    
    private int nDigits;
    
    private final char[] digits;
    
    private final char[] buffer = new char[26];
    
    private boolean exactDecimalConversion = false;
    
    private boolean decimalDigitsRoundedUp = false;
    
    BinaryToASCIIBuffer() {
      this.digits = new char[20];
    }
    
    BinaryToASCIIBuffer(boolean param1Boolean, char[] param1ArrayOfchar) {
      this.isNegative = param1Boolean;
      this.decExponent = 0;
      this.digits = param1ArrayOfchar;
      this.firstDigitIndex = 0;
      this.nDigits = param1ArrayOfchar.length;
    }
    
    public String toJavaFormatString() {
      int i = getChars(this.buffer);
      return new String(this.buffer, 0, i);
    }
    
    public void appendTo(Appendable param1Appendable) {
      int i = getChars(this.buffer);
      if (param1Appendable instanceof StringBuilder) {
        ((StringBuilder)param1Appendable).append(this.buffer, 0, i);
      } else if (param1Appendable instanceof StringBuffer) {
        ((StringBuffer)param1Appendable).append(this.buffer, 0, i);
      } else {
        assert false;
      } 
    }
    
    public int getDecimalExponent() {
      return this.decExponent;
    }
    
    public int getDigits(char[] param1ArrayOfchar) {
      System.arraycopy(this.digits, this.firstDigitIndex, param1ArrayOfchar, 0, this.nDigits);
      return this.nDigits;
    }
    
    public boolean isNegative() {
      return this.isNegative;
    }
    
    public boolean isExceptional() {
      return false;
    }
    
    public boolean digitsRoundedUp() {
      return this.decimalDigitsRoundedUp;
    }
    
    public boolean decimalDigitsExact() {
      return this.exactDecimalConversion;
    }
    
    private void setSign(boolean param1Boolean) {
      this.isNegative = param1Boolean;
    }
    
    private void developLongDigits(int param1Int1, long param1Long, int param1Int2) {
      if (param1Int2 != 0) {
        long l1 = FDBigInteger.LONG_5_POW[param1Int2] << param1Int2;
        long l2 = param1Long % l1;
        param1Long /= l1;
        param1Int1 += param1Int2;
        if (l2 >= l1 >> 1L)
          param1Long++; 
      } 
      int i = this.digits.length - 1;
      if (param1Long <= 2147483647L) {
        assert param1Long > 0L : param1Long;
        int k = (int)param1Long;
        int j = k % 10;
        k /= 10;
        while (j == 0) {
          param1Int1++;
          j = k % 10;
          k /= 10;
        } 
        while (k != 0) {
          this.digits[i--] = (char)(j + 48);
          param1Int1++;
          j = k % 10;
          k /= 10;
        } 
        this.digits[i] = (char)(j + 48);
      } else {
        int j = (int)(param1Long % 10L);
        param1Long /= 10L;
        while (j == 0) {
          param1Int1++;
          j = (int)(param1Long % 10L);
          param1Long /= 10L;
        } 
        while (param1Long != 0L) {
          this.digits[i--] = (char)(j + 48);
          param1Int1++;
          j = (int)(param1Long % 10L);
          param1Long /= 10L;
        } 
        this.digits[i] = (char)(j + 48);
      } 
      this.decExponent = param1Int1 + 1;
      this.firstDigitIndex = i;
      this.nDigits = this.digits.length - i;
    }
    
    private void dtoa(int param1Int1, long param1Long, int param1Int2, boolean param1Boolean) {
      boolean bool1, bool2;
      long l;
      assert param1Long > 0L;
      assert (param1Long & 0x10000000000000L) != 0L;
      int i = Long.numberOfTrailingZeros(param1Long);
      int j = 53 - i;
      this.decimalDigitsRoundedUp = false;
      this.exactDecimalConversion = false;
      int k = Math.max(0, j - param1Int1 - 1);
      if (param1Int1 <= 62 && param1Int1 >= -21)
        if (k < FDBigInteger.LONG_5_POW.length && j + N_5_BITS[k] < 64)
          if (k == 0) {
            boolean bool;
            if (param1Int1 > param1Int2) {
              bool = insignificantDigitsForPow2(param1Int1 - param1Int2 - 1);
            } else {
              bool = false;
            } 
            if (param1Int1 >= 52) {
              param1Long <<= param1Int1 - 52;
            } else {
              param1Long >>>= 52 - param1Int1;
            } 
            developLongDigits(0, param1Long, bool);
            return;
          }   
      int m = estimateDecExp(param1Long, param1Int1);
      int i1 = Math.max(0, -m);
      int n = i1 + k + param1Int1;
      int i3 = Math.max(0, m);
      int i2 = i3 + k;
      int i5 = i1;
      int i4 = n - param1Int2;
      param1Long >>>= i;
      n -= j - 1;
      int i6 = Math.min(n, i2);
      n -= i6;
      i2 -= i6;
      i4 -= i6;
      if (j == 1)
        i4--; 
      if (i4 < 0) {
        n -= i4;
        i2 -= i4;
        i4 = 0;
      } 
      byte b = 0;
      int i7 = j + n + ((i1 < N_5_BITS.length) ? N_5_BITS[i1] : (i1 * 3));
      int i8 = i2 + 1 + ((i3 + 1 < N_5_BITS.length) ? N_5_BITS[i3 + 1] : ((i3 + 1) * 3));
      if (i7 < 64 && i8 < 64) {
        if (i7 < 32 && i8 < 32) {
          int i10 = (int)param1Long * FDBigInteger.SMALL_5_POW[i1] << n;
          int i11 = FDBigInteger.SMALL_5_POW[i3] << i2;
          int i12 = FDBigInteger.SMALL_5_POW[i5] << i4;
          int i13 = i11 * 10;
          b = 0;
          int i9 = i10 / i11;
          i10 = 10 * i10 % i11;
          i12 *= 10;
          bool1 = (i10 < i12) ? true : false;
          bool2 = (i10 + i12 > i13) ? true : false;
          assert i9 < 10 : i9;
          if (i9 == 0 && !bool2) {
            m--;
          } else {
            this.digits[b++] = (char)(48 + i9);
          } 
          if (!param1Boolean || m < -3 || m >= 8)
            bool2 = bool1 = false; 
          while (!bool1 && !bool2) {
            i9 = i10 / i11;
            i10 = 10 * i10 % i11;
            i12 *= 10;
            assert i9 < 10 : i9;
            if (i12 > 0L) {
              bool1 = (i10 < i12) ? true : false;
              bool2 = (i10 + i12 > i13) ? true : false;
            } else {
              bool1 = true;
              bool2 = true;
            } 
            this.digits[b++] = (char)(48 + i9);
          } 
          l = ((i10 << 1) - i13);
          this.exactDecimalConversion = (i10 == 0);
        } else {
          long l1 = param1Long * FDBigInteger.LONG_5_POW[i1] << n;
          long l2 = FDBigInteger.LONG_5_POW[i3] << i2;
          long l3 = FDBigInteger.LONG_5_POW[i5] << i4;
          long l4 = l2 * 10L;
          b = 0;
          int i9 = (int)(l1 / l2);
          l1 = 10L * l1 % l2;
          l3 *= 10L;
          bool1 = (l1 < l3) ? true : false;
          bool2 = (l1 + l3 > l4) ? true : false;
          assert i9 < 10 : i9;
          if (i9 == 0 && !bool2) {
            m--;
          } else {
            this.digits[b++] = (char)(48 + i9);
          } 
          if (!param1Boolean || m < -3 || m >= 8)
            bool2 = bool1 = false; 
          while (!bool1 && !bool2) {
            i9 = (int)(l1 / l2);
            l1 = 10L * l1 % l2;
            l3 *= 10L;
            assert i9 < 10 : i9;
            if (l3 > 0L) {
              bool1 = (l1 < l3) ? true : false;
              bool2 = (l1 + l3 > l4) ? true : false;
            } else {
              bool1 = true;
              bool2 = true;
            } 
            this.digits[b++] = (char)(48 + i9);
          } 
          l = (l1 << 1L) - l4;
          this.exactDecimalConversion = (l1 == 0L);
        } 
      } else {
        FDBigInteger fDBigInteger1 = FDBigInteger.valueOfPow52(i3, i2);
        int i10 = fDBigInteger1.getNormalizationBias();
        fDBigInteger1 = fDBigInteger1.leftShift(i10);
        FDBigInteger fDBigInteger2 = FDBigInteger.valueOfMulPow52(param1Long, i1, n + i10);
        FDBigInteger fDBigInteger3 = FDBigInteger.valueOfPow52(i5 + 1, i4 + i10 + 1);
        FDBigInteger fDBigInteger4 = FDBigInteger.valueOfPow52(i3 + 1, i2 + i10 + 1);
        b = 0;
        int i9 = fDBigInteger2.quoRemIteration(fDBigInteger1);
        bool1 = (fDBigInteger2.cmp(fDBigInteger3) < 0) ? true : false;
        bool2 = (fDBigInteger4.addAndCmp(fDBigInteger2, fDBigInteger3) <= 0) ? true : false;
        assert i9 < 10 : i9;
        if (i9 == 0 && !bool2) {
          m--;
        } else {
          this.digits[b++] = (char)(48 + i9);
        } 
        if (!param1Boolean || m < -3 || m >= 8)
          bool2 = bool1 = false; 
        while (!bool1 && !bool2) {
          i9 = fDBigInteger2.quoRemIteration(fDBigInteger1);
          assert i9 < 10 : i9;
          fDBigInteger3 = fDBigInteger3.multBy10();
          bool1 = (fDBigInteger2.cmp(fDBigInteger3) < 0) ? true : false;
          bool2 = (fDBigInteger4.addAndCmp(fDBigInteger2, fDBigInteger3) <= 0) ? true : false;
          this.digits[b++] = (char)(48 + i9);
        } 
        if (bool2 && bool1) {
          fDBigInteger2 = fDBigInteger2.leftShift(1);
          l = fDBigInteger2.cmp(fDBigInteger4);
        } else {
          l = 0L;
        } 
        this.exactDecimalConversion = (fDBigInteger2.cmp(FDBigInteger.ZERO) == 0);
      } 
      this.decExponent = m + 1;
      this.firstDigitIndex = 0;
      this.nDigits = b;
      if (bool2)
        if (bool1) {
          if (l == 0L) {
            if ((this.digits[this.firstDigitIndex + this.nDigits - 1] & 0x1) != 0)
              roundup(); 
          } else if (l > 0L) {
            roundup();
          } 
        } else {
          roundup();
        }  
    }
    
    private void roundup() {
      int i = this.firstDigitIndex + this.nDigits - 1;
      char c = this.digits[i];
      if (c == '9') {
        while (c == '9' && i > this.firstDigitIndex) {
          this.digits[i] = '0';
          c = this.digits[--i];
        } 
        if (c == '9') {
          this.decExponent++;
          this.digits[this.firstDigitIndex] = '1';
          return;
        } 
      } 
      this.digits[i] = (char)(c + 1);
      this.decimalDigitsRoundedUp = true;
    }
    
    static int estimateDecExp(long param1Long, int param1Int) {
      double d1 = Double.longBitsToDouble(0x3FF0000000000000L | param1Long & 0xFFFFFFFFFFFFFL);
      double d2 = (d1 - 1.5D) * 0.289529654D + 0.176091259D + param1Int * 0.301029995663981D;
      long l = Double.doubleToRawLongBits(d2);
      int i = (int)((l & 0x7FF0000000000000L) >> 52L) - 1023;
      boolean bool = ((l & Long.MIN_VALUE) != 0L) ? true : false;
      if (i >= 0 && i < 52) {
        long l1 = 4503599627370495L >> i;
        int j = (int)((l & 0xFFFFFFFFFFFFFL | 0x10000000000000L) >> 52 - i);
        return bool ? (((l1 & l) == 0L) ? -j : (-j - 1)) : j;
      } 
      if (i < 0)
        return ((l & Long.MAX_VALUE) == 0L) ? 0 : (bool ? -1 : 0); 
      return (int)d2;
    }
    
    private static int insignificantDigits(int param1Int) {
      byte b;
      for (b = 0; param1Int >= 10L; b++)
        param1Int = (int)(param1Int / 10L); 
      return b;
    }
    
    private static int insignificantDigitsForPow2(int param1Int) {
      if (param1Int > 1 && param1Int < insignificantDigitsNumber.length)
        return insignificantDigitsNumber[param1Int]; 
      return 0;
    }
    
    private static int[] insignificantDigitsNumber = new int[] { 
        0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 
        3, 3, 3, 3, 4, 4, 4, 5, 5, 5, 
        6, 6, 6, 6, 7, 7, 7, 8, 8, 8, 
        9, 9, 9, 9, 10, 10, 10, 11, 11, 11, 
        12, 12, 12, 12, 13, 13, 13, 14, 14, 14, 
        15, 15, 15, 15, 16, 16, 16, 17, 17, 17, 
        18, 18, 18, 19 };
    
    private static final int[] N_5_BITS = new int[] { 
        0, 3, 5, 7, 10, 12, 14, 17, 19, 21, 
        24, 26, 28, 31, 33, 35, 38, 40, 42, 45, 
        47, 49, 52, 54, 56, 59, 61 };
    
    private int getChars(char[] param1ArrayOfchar) {
      assert this.nDigits <= 19 : this.nDigits;
      int i = 0;
      if (this.isNegative) {
        param1ArrayOfchar[0] = '-';
        i = 1;
      } 
      if (this.decExponent > 0 && this.decExponent < 8) {
        int j = Math.min(this.nDigits, this.decExponent);
        System.arraycopy(this.digits, this.firstDigitIndex, param1ArrayOfchar, i, j);
        i += j;
        if (j < this.decExponent) {
          j = this.decExponent - j;
          Arrays.fill(param1ArrayOfchar, i, i + j, '0');
          i += j;
          param1ArrayOfchar[i++] = '.';
          param1ArrayOfchar[i++] = '0';
        } else {
          param1ArrayOfchar[i++] = '.';
          if (j < this.nDigits) {
            int k = this.nDigits - j;
            System.arraycopy(this.digits, this.firstDigitIndex + j, param1ArrayOfchar, i, k);
            i += k;
          } else {
            param1ArrayOfchar[i++] = '0';
          } 
        } 
      } else if (this.decExponent <= 0 && this.decExponent > -3) {
        param1ArrayOfchar[i++] = '0';
        param1ArrayOfchar[i++] = '.';
        if (this.decExponent != 0) {
          Arrays.fill(param1ArrayOfchar, i, i - this.decExponent, '0');
          i -= this.decExponent;
        } 
        System.arraycopy(this.digits, this.firstDigitIndex, param1ArrayOfchar, i, this.nDigits);
        i += this.nDigits;
      } else {
        int j;
        param1ArrayOfchar[i++] = this.digits[this.firstDigitIndex];
        param1ArrayOfchar[i++] = '.';
        if (this.nDigits > 1) {
          System.arraycopy(this.digits, this.firstDigitIndex + 1, param1ArrayOfchar, i, this.nDigits - 1);
          i += this.nDigits - 1;
        } else {
          param1ArrayOfchar[i++] = '0';
        } 
        param1ArrayOfchar[i++] = 'E';
        if (this.decExponent <= 0) {
          param1ArrayOfchar[i++] = '-';
          j = -this.decExponent + 1;
        } else {
          j = this.decExponent - 1;
        } 
        if (j <= 9) {
          param1ArrayOfchar[i++] = (char)(j + 48);
        } else if (j <= 99) {
          param1ArrayOfchar[i++] = (char)(j / 10 + 48);
          param1ArrayOfchar[i++] = (char)(j % 10 + 48);
        } else {
          param1ArrayOfchar[i++] = (char)(j / 100 + 48);
          j %= 100;
          param1ArrayOfchar[i++] = (char)(j / 10 + 48);
          param1ArrayOfchar[i++] = (char)(j % 10 + 48);
        } 
      } 
      return i;
    }
  }
  
  private static final ThreadLocal<BinaryToASCIIBuffer> threadLocalBinaryToASCIIBuffer = new ThreadLocal<BinaryToASCIIBuffer>() {
      protected FloatingDecimal.BinaryToASCIIBuffer initialValue() {
        return new FloatingDecimal.BinaryToASCIIBuffer();
      }
    };
  
  private static BinaryToASCIIBuffer getBinaryToASCIIBuffer() {
    return threadLocalBinaryToASCIIBuffer.get();
  }
  
  static class PreparedASCIIToBinaryBuffer implements ASCIIToBinaryConverter {
    private final double doubleVal;
    
    private final float floatVal;
    
    public PreparedASCIIToBinaryBuffer(double param1Double, float param1Float) {
      this.doubleVal = param1Double;
      this.floatVal = param1Float;
    }
    
    public double doubleValue() {
      return this.doubleVal;
    }
    
    public float floatValue() {
      return this.floatVal;
    }
  }
  
  static final ASCIIToBinaryConverter A2BC_POSITIVE_INFINITY = new PreparedASCIIToBinaryBuffer(Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
  
  static final ASCIIToBinaryConverter A2BC_NEGATIVE_INFINITY = new PreparedASCIIToBinaryBuffer(Double.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
  
  static final ASCIIToBinaryConverter A2BC_NOT_A_NUMBER = new PreparedASCIIToBinaryBuffer(Double.NaN, Float.NaN);
  
  static final ASCIIToBinaryConverter A2BC_POSITIVE_ZERO = new PreparedASCIIToBinaryBuffer(0.0D, 0.0F);
  
  static final ASCIIToBinaryConverter A2BC_NEGATIVE_ZERO = new PreparedASCIIToBinaryBuffer(-0.0D, -0.0F);
  
  static class ASCIIToBinaryBuffer implements ASCIIToBinaryConverter {
    boolean isNegative;
    
    int decExponent;
    
    char[] digits;
    
    int nDigits;
    
    ASCIIToBinaryBuffer(boolean param1Boolean, int param1Int1, char[] param1ArrayOfchar, int param1Int2) {
      this.isNegative = param1Boolean;
      this.decExponent = param1Int1;
      this.digits = param1ArrayOfchar;
      this.nDigits = param1Int2;
    }
    
    public double doubleValue() {
      int i = Math.min(this.nDigits, 16);
      int j = this.digits[0] - 48;
      int k = Math.min(i, 9);
      for (byte b = 1; b < k; b++)
        j = j * 10 + this.digits[b] - 48; 
      long l1 = j;
      for (int m = k; m < i; m++)
        l1 = l1 * 10L + (this.digits[m] - 48); 
      double d = l1;
      int n = this.decExponent - i;
      if (this.nDigits <= 15) {
        if (n == 0 || d == 0.0D)
          return this.isNegative ? -d : d; 
        if (n >= 0) {
          if (n <= MAX_SMALL_TEN) {
            double d1 = d * SMALL_10_POW[n];
            return this.isNegative ? -d1 : d1;
          } 
          int i4 = 15 - i;
          if (n <= MAX_SMALL_TEN + i4) {
            d *= SMALL_10_POW[i4];
            double d1 = d * SMALL_10_POW[n - i4];
            return this.isNegative ? -d1 : d1;
          } 
        } else if (n >= -MAX_SMALL_TEN) {
          double d1 = d / SMALL_10_POW[-n];
          return this.isNegative ? -d1 : d1;
        } 
      } 
      if (n > 0) {
        if (this.decExponent > 309)
          return this.isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; 
        if ((n & 0xF) != 0)
          d *= SMALL_10_POW[n & 0xF]; 
        if ((n >>= 4) != 0) {
          byte b1;
          for (b1 = 0; n > 1; b1++, n >>= 1) {
            if ((n & 0x1) != 0)
              d *= BIG_10_POW[b1]; 
          } 
          double d1 = d * BIG_10_POW[b1];
          if (Double.isInfinite(d1)) {
            d1 = d / 2.0D;
            d1 *= BIG_10_POW[b1];
            if (Double.isInfinite(d1))
              return this.isNegative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY; 
            d1 = Double.MAX_VALUE;
          } 
          d = d1;
        } 
      } else if (n < 0) {
        n = -n;
        if (this.decExponent < -325)
          return this.isNegative ? -0.0D : 0.0D; 
        if ((n & 0xF) != 0)
          d /= SMALL_10_POW[n & 0xF]; 
        if ((n >>= 4) != 0) {
          byte b1;
          for (b1 = 0; n > 1; b1++, n >>= 1) {
            if ((n & 0x1) != 0)
              d *= TINY_10_POW[b1]; 
          } 
          double d1 = d * TINY_10_POW[b1];
          if (d1 == 0.0D) {
            d1 = d * 2.0D;
            d1 *= TINY_10_POW[b1];
            if (d1 == 0.0D)
              return this.isNegative ? -0.0D : 0.0D; 
            d1 = Double.MIN_VALUE;
          } 
          d = d1;
        } 
      } 
      if (this.nDigits > 1100) {
        this.nDigits = 1101;
        this.digits[1100] = '1';
      } 
      FDBigInteger fDBigInteger1 = new FDBigInteger(l1, this.digits, i, this.nDigits);
      n = this.decExponent - this.nDigits;
      long l2 = Double.doubleToRawLongBits(d);
      int i1 = Math.max(0, -n);
      int i2 = Math.max(0, n);
      fDBigInteger1 = fDBigInteger1.multByPow52(i2, 0);
      fDBigInteger1.makeImmutable();
      FDBigInteger fDBigInteger2 = null;
      int i3 = 0;
      do {
        int i11;
        FDBigInteger fDBigInteger4;
        boolean bool;
        int i4 = (int)(l2 >>> 52L);
        long l = l2 & 0xFFFFFFFFFFFFFL;
        if (i4 > 0) {
          l |= 0x10000000000000L;
        } else {
          assert l != 0L : l;
          int i14 = Long.numberOfLeadingZeros(l);
          int i15 = i14 - 11;
          l <<= i15;
          i4 = 1 - i15;
        } 
        i4 -= 1023;
        int i5 = Long.numberOfTrailingZeros(l);
        l >>>= i5;
        int i6 = i4 - 52 + i5;
        int i7 = 53 - i5;
        int i8 = i1;
        int i9 = i2;
        if (i6 >= 0) {
          i8 += i6;
        } else {
          i9 -= i6;
        } 
        int i10 = i8;
        if (i4 <= -1023) {
          i11 = i4 + i5 + 1023;
        } else {
          i11 = 1 + i5;
        } 
        i8 += i11;
        i9 += i11;
        int i12 = Math.min(i8, Math.min(i9, i10));
        i8 -= i12;
        i9 -= i12;
        i10 -= i12;
        FDBigInteger fDBigInteger3 = FDBigInteger.valueOfMulPow52(l, i1, i8);
        if (fDBigInteger2 == null || i3 != i9) {
          fDBigInteger2 = fDBigInteger1.leftShift(i9);
          i3 = i9;
        } 
        int i13;
        if ((i13 = fDBigInteger3.cmp(fDBigInteger2)) > 0) {
          bool = true;
          fDBigInteger4 = fDBigInteger3.leftInplaceSub(fDBigInteger2);
          if (i7 == 1 && i6 > -1022) {
            i10--;
            if (i10 < 0) {
              i10 = 0;
              fDBigInteger4 = fDBigInteger4.leftShift(1);
            } 
          } 
        } else if (i13 < 0) {
          bool = false;
          fDBigInteger4 = fDBigInteger2.rightInplaceSub(fDBigInteger3);
        } else {
          break;
        } 
        i13 = fDBigInteger4.cmpPow52(i1, i10);
        if (i13 < 0)
          break; 
        if (i13 == 0) {
          if ((l2 & 0x1L) != 0L)
            l2 += bool ? -1L : 1L; 
          break;
        } 
        l2 += bool ? -1L : 1L;
      } while (l2 != 0L && l2 != 9218868437227405312L);
      if (this.isNegative)
        l2 |= Long.MIN_VALUE; 
      return Double.longBitsToDouble(l2);
    }
    
    public float floatValue() {
      int i = Math.min(this.nDigits, 8);
      int j = this.digits[0] - 48;
      for (byte b = 1; b < i; b++)
        j = j * 10 + this.digits[b] - 48; 
      float f = j;
      int k = this.decExponent - i;
      if (this.nDigits <= 7) {
        if (k == 0 || f == 0.0F)
          return this.isNegative ? -f : f; 
        if (k >= 0) {
          if (k <= SINGLE_MAX_SMALL_TEN) {
            f *= SINGLE_SMALL_10_POW[k];
            return this.isNegative ? -f : f;
          } 
          int i3 = 7 - i;
          if (k <= SINGLE_MAX_SMALL_TEN + i3) {
            f *= SINGLE_SMALL_10_POW[i3];
            f *= SINGLE_SMALL_10_POW[k - i3];
            return this.isNegative ? -f : f;
          } 
        } else if (k >= -SINGLE_MAX_SMALL_TEN) {
          f /= SINGLE_SMALL_10_POW[-k];
          return this.isNegative ? -f : f;
        } 
      } else if (this.decExponent >= this.nDigits && this.nDigits + this.decExponent <= 15) {
        long l = j;
        for (int i3 = i; i3 < this.nDigits; i3++)
          l = l * 10L + (this.digits[i3] - 48); 
        double d1 = l;
        k = this.decExponent - this.nDigits;
        d1 *= SMALL_10_POW[k];
        f = (float)d1;
        return this.isNegative ? -f : f;
      } 
      double d = f;
      if (k > 0) {
        if (this.decExponent > 39)
          return this.isNegative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY; 
        if ((k & 0xF) != 0)
          d *= SMALL_10_POW[k & 0xF]; 
        if ((k >>= 4) != 0)
          for (byte b1 = 0; k > 0; b1++, k >>= 1) {
            if ((k & 0x1) != 0)
              d *= BIG_10_POW[b1]; 
          }  
      } else if (k < 0) {
        k = -k;
        if (this.decExponent < -46)
          return this.isNegative ? -0.0F : 0.0F; 
        if ((k & 0xF) != 0)
          d /= SMALL_10_POW[k & 0xF]; 
        if ((k >>= 4) != 0)
          for (byte b1 = 0; k > 0; b1++, k >>= 1) {
            if ((k & 0x1) != 0)
              d *= TINY_10_POW[b1]; 
          }  
      } 
      f = Math.max(Float.MIN_VALUE, Math.min(Float.MAX_VALUE, (float)d));
      if (this.nDigits > 200) {
        this.nDigits = 201;
        this.digits[200] = '1';
      } 
      FDBigInteger fDBigInteger1 = new FDBigInteger(j, this.digits, i, this.nDigits);
      k = this.decExponent - this.nDigits;
      int m = Float.floatToRawIntBits(f);
      int n = Math.max(0, -k);
      int i1 = Math.max(0, k);
      fDBigInteger1 = fDBigInteger1.multByPow52(i1, 0);
      fDBigInteger1.makeImmutable();
      FDBigInteger fDBigInteger2 = null;
      int i2 = 0;
      do {
        int i11;
        FDBigInteger fDBigInteger4;
        boolean bool;
        int i3 = m >>> 23;
        int i4 = m & 0x7FFFFF;
        if (i3 > 0) {
          i4 |= 0x800000;
        } else {
          assert i4 != 0 : i4;
          int i14 = Integer.numberOfLeadingZeros(i4);
          int i15 = i14 - 8;
          i4 <<= i15;
          i3 = 1 - i15;
        } 
        i3 -= 127;
        int i5 = Integer.numberOfTrailingZeros(i4);
        i4 >>>= i5;
        int i6 = i3 - 23 + i5;
        int i7 = 24 - i5;
        int i8 = n;
        int i9 = i1;
        if (i6 >= 0) {
          i8 += i6;
        } else {
          i9 -= i6;
        } 
        int i10 = i8;
        if (i3 <= -127) {
          i11 = i3 + i5 + 127;
        } else {
          i11 = 1 + i5;
        } 
        i8 += i11;
        i9 += i11;
        int i12 = Math.min(i8, Math.min(i9, i10));
        i8 -= i12;
        i9 -= i12;
        i10 -= i12;
        FDBigInteger fDBigInteger3 = FDBigInteger.valueOfMulPow52(i4, n, i8);
        if (fDBigInteger2 == null || i2 != i9) {
          fDBigInteger2 = fDBigInteger1.leftShift(i9);
          i2 = i9;
        } 
        int i13;
        if ((i13 = fDBigInteger3.cmp(fDBigInteger2)) > 0) {
          bool = true;
          fDBigInteger4 = fDBigInteger3.leftInplaceSub(fDBigInteger2);
          if (i7 == 1 && i6 > -126) {
            i10--;
            if (i10 < 0) {
              i10 = 0;
              fDBigInteger4 = fDBigInteger4.leftShift(1);
            } 
          } 
        } else if (i13 < 0) {
          bool = false;
          fDBigInteger4 = fDBigInteger2.rightInplaceSub(fDBigInteger3);
        } else {
          break;
        } 
        i13 = fDBigInteger4.cmpPow52(n, i10);
        if (i13 < 0)
          break; 
        if (i13 == 0) {
          if ((m & 0x1) != 0)
            m += bool ? -1 : 1; 
          break;
        } 
        m += bool ? -1 : 1;
      } while (m != 0 && m != 2139095040);
      if (this.isNegative)
        m |= Integer.MIN_VALUE; 
      return Float.intBitsToFloat(m);
    }
    
    private static final double[] SMALL_10_POW = new double[] { 
        1.0D, 10.0D, 100.0D, 1000.0D, 10000.0D, 100000.0D, 1000000.0D, 1.0E7D, 1.0E8D, 1.0E9D, 
        1.0E10D, 1.0E11D, 1.0E12D, 1.0E13D, 1.0E14D, 1.0E15D, 1.0E16D, 1.0E17D, 1.0E18D, 1.0E19D, 
        1.0E20D, 1.0E21D, 1.0E22D };
    
    private static final float[] SINGLE_SMALL_10_POW = new float[] { 
        1.0F, 10.0F, 100.0F, 1000.0F, 10000.0F, 100000.0F, 1000000.0F, 1.0E7F, 1.0E8F, 1.0E9F, 
        1.0E10F };
    
    private static final double[] BIG_10_POW = new double[] { 1.0E16D, 1.0E32D, 1.0E64D, 1.0E128D, 1.0E256D };
    
    private static final double[] TINY_10_POW = new double[] { 1.0E-16D, 1.0E-32D, 1.0E-64D, 1.0E-128D, 1.0E-256D };
    
    private static final int MAX_SMALL_TEN = SMALL_10_POW.length - 1;
    
    private static final int SINGLE_MAX_SMALL_TEN = SINGLE_SMALL_10_POW.length - 1;
  }
  
  public static BinaryToASCIIConverter getBinaryToASCIIConverter(double paramDouble) {
    return getBinaryToASCIIConverter(paramDouble, true);
  }
  
  static BinaryToASCIIConverter getBinaryToASCIIConverter(double paramDouble, boolean paramBoolean) {
    byte b;
    long l1 = Double.doubleToRawLongBits(paramDouble);
    boolean bool = ((l1 & Long.MIN_VALUE) != 0L) ? true : false;
    long l2 = l1 & 0xFFFFFFFFFFFFFL;
    int i = (int)((l1 & 0x7FF0000000000000L) >> 52L);
    if (i == 2047) {
      if (l2 == 0L)
        return bool ? B2AC_NEGATIVE_INFINITY : B2AC_POSITIVE_INFINITY; 
      return B2AC_NOT_A_NUMBER;
    } 
    if (i == 0) {
      if (l2 == 0L)
        return bool ? B2AC_NEGATIVE_ZERO : B2AC_POSITIVE_ZERO; 
      int j = Long.numberOfLeadingZeros(l2);
      int k = j - 11;
      l2 <<= k;
      i = 1 - k;
      b = 64 - j;
    } else {
      l2 |= 0x10000000000000L;
      b = 53;
    } 
    i -= 1023;
    BinaryToASCIIBuffer binaryToASCIIBuffer = getBinaryToASCIIBuffer();
    binaryToASCIIBuffer.setSign(bool);
    binaryToASCIIBuffer.dtoa(i, l2, b, paramBoolean);
    return binaryToASCIIBuffer;
  }
  
  private static BinaryToASCIIConverter getBinaryToASCIIConverter(float paramFloat) {
    byte b;
    int i = Float.floatToRawIntBits(paramFloat);
    boolean bool = ((i & Integer.MIN_VALUE) != 0) ? true : false;
    int j = i & 0x7FFFFF;
    int k = (i & 0x7F800000) >> 23;
    if (k == 255) {
      if (j == 0L)
        return bool ? B2AC_NEGATIVE_INFINITY : B2AC_POSITIVE_INFINITY; 
      return B2AC_NOT_A_NUMBER;
    } 
    if (k == 0) {
      if (j == 0)
        return bool ? B2AC_NEGATIVE_ZERO : B2AC_POSITIVE_ZERO; 
      int m = Integer.numberOfLeadingZeros(j);
      int n = m - 8;
      j <<= n;
      k = 1 - n;
      b = 32 - m;
    } else {
      j |= 0x800000;
      b = 24;
    } 
    k -= 127;
    BinaryToASCIIBuffer binaryToASCIIBuffer = getBinaryToASCIIBuffer();
    binaryToASCIIBuffer.setSign(bool);
    binaryToASCIIBuffer.dtoa(k, j << 29L, b, true);
    return binaryToASCIIBuffer;
  }
  
  static ASCIIToBinaryConverter readJavaFormatString(String paramString) throws NumberFormatException {
    boolean bool1 = false;
    boolean bool2 = false;
    try {
      paramString = paramString.trim();
      int i = paramString.length();
      if (i == 0)
        throw new NumberFormatException("empty String"); 
      byte b = 0;
      switch (paramString.charAt(b)) {
        case '-':
          bool1 = true;
        case '+':
          b++;
          bool2 = true;
          break;
      } 
      char c = paramString.charAt(b);
      if (c == 'N') {
        if (i - b == NAN_LENGTH && paramString.indexOf("NaN", b) == b)
          return A2BC_NOT_A_NUMBER; 
      } else if (c == 'I') {
        if (i - b == INFINITY_LENGTH && paramString.indexOf("Infinity", b) == b)
          return bool1 ? A2BC_NEGATIVE_INFINITY : A2BC_POSITIVE_INFINITY; 
      } else {
        if (c == '0' && 
          i > b + 1) {
          char c1 = paramString.charAt(b + 1);
          if (c1 == 'x' || c1 == 'X')
            return parseHexString(paramString); 
        } 
        char[] arrayOfChar = new char[i];
        int j = 0;
        boolean bool3 = false;
        byte b1 = 0;
        byte b2 = 0;
        byte b3 = 0;
        while (b < i) {
          c = paramString.charAt(b);
          if (c == '0') {
            b2++;
          } else if (c == '.') {
            if (bool3)
              throw new NumberFormatException("multiple points"); 
            b1 = b;
            if (bool2)
              b1--; 
            bool3 = true;
          } else {
            break;
          } 
          b++;
        } 
        while (b < i) {
          c = paramString.charAt(b);
          if (c >= '1' && c <= '9') {
            arrayOfChar[j++] = c;
            b3 = 0;
          } else if (c == '0') {
            arrayOfChar[j++] = c;
            b3++;
          } else if (c == '.') {
            if (bool3)
              throw new NumberFormatException("multiple points"); 
            b1 = b;
            if (bool2)
              b1--; 
            bool3 = true;
          } else {
            break;
          } 
          b++;
        } 
        j -= b3;
        boolean bool4 = (j == 0) ? true : false;
        if (!bool4 || b2 != 0) {
          int k;
          if (bool3) {
            k = b1 - b2;
          } else {
            k = j + b3;
          } 
          if (b < i && ((c = paramString.charAt(b)) == 'e' || c == 'E')) {
            byte b4 = 1;
            int m = 0;
            int n = 214748364;
            boolean bool = false;
            switch (paramString.charAt(++b)) {
              case '-':
                b4 = -1;
              case '+':
                b++;
                break;
            } 
            byte b5 = b;
            while (b < i) {
              if (m >= n)
                bool = true; 
              c = paramString.charAt(b++);
              if (c >= '0' && c <= '9') {
                m = m * 10 + c - 48;
                continue;
              } 
              b--;
            } 
            int i1 = 324 + j + b3;
            if (bool || m > i1) {
              k = b4 * i1;
            } else {
              k += b4 * m;
            } 
            if (b == b5)
              throw new NumberFormatException("For input string: \"" + paramString + "\""); 
          } 
          if (b >= i || (b == i - 1 && (paramString.charAt(b) == 'f' || paramString.charAt(b) == 'F' || paramString.charAt(b) == 'd' || paramString.charAt(b) == 'D'))) {
            if (bool4)
              return bool1 ? A2BC_NEGATIVE_ZERO : A2BC_POSITIVE_ZERO; 
            return new ASCIIToBinaryBuffer(bool1, k, arrayOfChar, j);
          } 
        } 
      } 
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {}
    throw new NumberFormatException("For input string: \"" + paramString + "\"");
  }
  
  static ASCIIToBinaryConverter parseHexString(String paramString) {
    long l1;
    Matcher matcher = HexFloatPattern.access$200().matcher(paramString);
    boolean bool = matcher.matches();
    if (!bool)
      throw new NumberFormatException("For input string: \"" + paramString + "\""); 
    String str1 = matcher.group(1);
    boolean bool1 = (str1 != null && str1.equals("-")) ? true : false;
    String str2 = null;
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    String str4;
    if ((str4 = matcher.group(4)) != null) {
      str2 = stripLeadingZeros(str4);
      k = str2.length();
    } else {
      String str5 = stripLeadingZeros(matcher.group(6));
      k = str5.length();
      String str6 = matcher.group(7);
      m = str6.length();
      str2 = ((str5 == null) ? "" : str5) + str6;
    } 
    str2 = stripLeadingZeros(str2);
    i = str2.length();
    if (k >= 1) {
      j = 4 * (k - 1);
    } else {
      j = -4 * (m - i + 1);
    } 
    if (i == 0)
      return bool1 ? A2BC_NEGATIVE_ZERO : A2BC_POSITIVE_ZERO; 
    String str3 = matcher.group(8);
    m = (str3 == null || str3.equals("+")) ? 1 : 0;
    try {
      l1 = Integer.parseInt(matcher.group(9));
    } catch (NumberFormatException numberFormatException) {
      return bool1 ? ((m != 0) ? A2BC_NEGATIVE_INFINITY : A2BC_NEGATIVE_ZERO) : ((m != 0) ? A2BC_POSITIVE_INFINITY : A2BC_POSITIVE_ZERO);
    } 
    long l2 = ((m != 0) ? 1L : -1L) * l1;
    long l3 = l2 + j;
    boolean bool2 = false;
    boolean bool3 = false;
    byte b1 = 0;
    long l4 = 0L;
    long l5 = getHexDigit(str2, 0);
    if (l5 == 1L) {
      l4 |= l5 << 52L;
      b1 = 48;
    } else if (l5 <= 3L) {
      l4 |= l5 << 51L;
      b1 = 47;
      l3++;
    } else if (l5 <= 7L) {
      l4 |= l5 << 50L;
      b1 = 46;
      l3 += 2L;
    } else if (l5 <= 15L) {
      l4 |= l5 << 49L;
      b1 = 45;
      l3 += 3L;
    } else {
      throw new AssertionError("Result from digit conversion too large!");
    } 
    byte b2 = 0;
    b2 = 1;
    for (; b2 < i && b1 >= 0; 
      b2++) {
      long l = getHexDigit(str2, b2);
      l4 |= l << b1;
      b1 -= 4;
    } 
    if (b2 < i) {
      long l = getHexDigit(str2, b2);
      switch (b1) {
        case -1:
          l4 |= (l & 0xEL) >> 1L;
          bool2 = ((l & 0x1L) != 0L) ? true : false;
          break;
        case -2:
          l4 |= (l & 0xCL) >> 2L;
          bool2 = ((l & 0x2L) != 0L) ? true : false;
          bool3 = ((l & 0x1L) != 0L) ? true : false;
          break;
        case -3:
          l4 |= (l & 0x8L) >> 3L;
          bool2 = ((l & 0x4L) != 0L) ? true : false;
          bool3 = ((l & 0x3L) != 0L) ? true : false;
          break;
        case -4:
          bool2 = ((l & 0x8L) != 0L) ? true : false;
          bool3 = ((l & 0x7L) != 0L) ? true : false;
          break;
        default:
          throw new AssertionError("Unexpected shift distance remainder.");
      } 
      b2++;
      while (b2 < i && !bool3) {
        l = getHexDigit(str2, b2);
        bool3 = (bool3 || l != 0L) ? true : false;
        b2++;
      } 
    } 
    int n = bool1 ? Integer.MIN_VALUE : 0;
    if (l3 >= -126L) {
      if (l3 > 127L) {
        n |= 0x7F800000;
      } else {
        byte b = 28;
        boolean bool5 = ((l4 & (1L << b) - 1L) != 0L || bool2 || bool3) ? true : false;
        int i1 = (int)(l4 >>> b);
        if ((i1 & 0x3) != 1 || bool5)
          i1++; 
        n |= ((int)l3 + 126 << 23) + (i1 >> 1);
      } 
    } else if (l3 >= -150L) {
      int i1 = (int)(-98L - l3);
      assert i1 >= 29;
      assert i1 < 53;
      boolean bool5 = ((l4 & (1L << i1) - 1L) != 0L || bool2 || bool3) ? true : false;
      int i2 = (int)(l4 >>> i1);
      if ((i2 & 0x3) != 1 || bool5)
        i2++; 
      n |= i2 >> 1;
    } 
    float f = Float.intBitsToFloat(n);
    if (l3 > 1023L)
      return bool1 ? A2BC_NEGATIVE_INFINITY : A2BC_POSITIVE_INFINITY; 
    if (l3 <= 1023L && l3 >= -1022L) {
      l4 = l3 + 1023L << 52L & 0x7FF0000000000000L | 0xFFFFFFFFFFFFFL & l4;
    } else {
      if (l3 < -1075L)
        return bool1 ? A2BC_NEGATIVE_ZERO : A2BC_POSITIVE_ZERO; 
      bool3 = (bool3 || bool2) ? true : false;
      bool2 = false;
      int i1 = 53 - (int)l3 - -1074 + 1;
      assert i1 >= 1 && i1 <= 53;
      bool2 = ((l4 & 1L << i1 - 1) != 0L) ? true : false;
      if (i1 > 1) {
        long l = -1L << i1 - 1 ^ 0xFFFFFFFFFFFFFFFFL;
        bool3 = (bool3 || (l4 & l) != 0L) ? true : false;
      } 
      l4 >>= i1;
      l4 = 0x0L | 0xFFFFFFFFFFFFFL & l4;
    } 
    boolean bool4 = ((l4 & 0x1L) == 0L) ? true : false;
    if ((bool4 && bool2 && bool3) || (!bool4 && bool2))
      l4++; 
    double d = bool1 ? Double.longBitsToDouble(l4 | Long.MIN_VALUE) : Double.longBitsToDouble(l4);
    return new PreparedASCIIToBinaryBuffer(d, f);
  }
  
  static String stripLeadingZeros(String paramString) {
    if (!paramString.isEmpty() && paramString.charAt(0) == '0') {
      for (byte b = 1; b < paramString.length(); b++) {
        if (paramString.charAt(b) != '0')
          return paramString.substring(b); 
      } 
      return "";
    } 
    return paramString;
  }
  
  static int getHexDigit(String paramString, int paramInt) {
    int i = Character.digit(paramString.charAt(paramInt), 16);
    if (i <= -1 || i >= 16)
      throw new AssertionError("Unexpected failure of digit conversion of " + paramString
          .charAt(paramInt)); 
    return i;
  }
  
  private static class FloatingDecimal {}
  
  static interface ASCIIToBinaryConverter {
    double doubleValue();
    
    float floatValue();
  }
  
  public static interface BinaryToASCIIConverter {
    String toJavaFormatString();
    
    void appendTo(Appendable param1Appendable);
    
    int getDecimalExponent();
    
    int getDigits(char[] param1ArrayOfchar);
    
    boolean isNegative();
    
    boolean isExceptional();
    
    boolean digitsRoundedUp();
    
    boolean decimalDigitsExact();
  }
}
