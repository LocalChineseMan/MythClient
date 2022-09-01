package java.text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import sun.misc.FloatingDecimal;

final class DigitList implements Cloneable {
  public static final int MAX_COUNT = 19;
  
  public int decimalAt = 0;
  
  public int count = 0;
  
  public char[] digits = new char[19];
  
  private char[] data;
  
  private RoundingMode roundingMode = RoundingMode.HALF_EVEN;
  
  private boolean isNegative = false;
  
  boolean isZero() {
    for (byte b = 0; b < this.count; b++) {
      if (this.digits[b] != '0')
        return false; 
    } 
    return true;
  }
  
  void setRoundingMode(RoundingMode paramRoundingMode) {
    this.roundingMode = paramRoundingMode;
  }
  
  public void clear() {
    this.decimalAt = 0;
    this.count = 0;
  }
  
  public void append(char paramChar) {
    if (this.count == this.digits.length) {
      char[] arrayOfChar = new char[this.count + 100];
      System.arraycopy(this.digits, 0, arrayOfChar, 0, this.count);
      this.digits = arrayOfChar;
    } 
    this.digits[this.count++] = paramChar;
  }
  
  public final double getDouble() {
    if (this.count == 0)
      return 0.0D; 
    StringBuffer stringBuffer = getStringBuffer();
    stringBuffer.append('.');
    stringBuffer.append(this.digits, 0, this.count);
    stringBuffer.append('E');
    stringBuffer.append(this.decimalAt);
    return Double.parseDouble(stringBuffer.toString());
  }
  
  public final long getLong() {
    if (this.count == 0)
      return 0L; 
    if (isLongMIN_VALUE())
      return Long.MIN_VALUE; 
    StringBuffer stringBuffer = getStringBuffer();
    stringBuffer.append(this.digits, 0, this.count);
    for (int i = this.count; i < this.decimalAt; i++)
      stringBuffer.append('0'); 
    return Long.parseLong(stringBuffer.toString());
  }
  
  public final BigDecimal getBigDecimal() {
    if (this.count == 0) {
      if (this.decimalAt == 0)
        return BigDecimal.ZERO; 
      return new BigDecimal("0E" + this.decimalAt);
    } 
    if (this.decimalAt == this.count)
      return new BigDecimal(this.digits, 0, this.count); 
    return (new BigDecimal(this.digits, 0, this.count)).scaleByPowerOfTen(this.decimalAt - this.count);
  }
  
  boolean fitsIntoLong(boolean paramBoolean1, boolean paramBoolean2) {
    while (this.count > 0 && this.digits[this.count - 1] == '0')
      this.count--; 
    if (this.count == 0)
      return (paramBoolean1 || paramBoolean2); 
    if (this.decimalAt < this.count || this.decimalAt > 19)
      return false; 
    if (this.decimalAt < 19)
      return true; 
    for (byte b = 0; b < this.count; b++) {
      char c1 = this.digits[b], c2 = LONG_MIN_REP[b];
      if (c1 > c2)
        return false; 
      if (c1 < c2)
        return true; 
    } 
    if (this.count < this.decimalAt)
      return true; 
    return !paramBoolean1;
  }
  
  final void set(boolean paramBoolean, double paramDouble, int paramInt) {
    set(paramBoolean, paramDouble, paramInt, true);
  }
  
  final void set(boolean paramBoolean1, double paramDouble, int paramInt, boolean paramBoolean2) {
    FloatingDecimal.BinaryToASCIIConverter binaryToASCIIConverter = FloatingDecimal.getBinaryToASCIIConverter(paramDouble);
    boolean bool1 = binaryToASCIIConverter.digitsRoundedUp();
    boolean bool2 = binaryToASCIIConverter.decimalDigitsExact();
    assert !binaryToASCIIConverter.isExceptional();
    String str = binaryToASCIIConverter.toJavaFormatString();
    set(paramBoolean1, str, bool1, bool2, paramInt, paramBoolean2);
  }
  
  private void set(boolean paramBoolean1, String paramString, boolean paramBoolean2, boolean paramBoolean3, int paramInt, boolean paramBoolean4) {
    this.isNegative = paramBoolean1;
    int i = paramString.length();
    char[] arrayOfChar = getDataChars(i);
    paramString.getChars(0, i, arrayOfChar, 0);
    this.decimalAt = -1;
    this.count = 0;
    int j = 0;
    byte b1 = 0;
    boolean bool = false;
    for (byte b2 = 0; b2 < i; ) {
      char c = arrayOfChar[b2++];
      if (c == '.') {
        this.decimalAt = this.count;
        continue;
      } 
      if (c == 'e' || c == 'E') {
        j = parseInt(arrayOfChar, b2, i);
        break;
      } 
      if (!bool) {
        bool = (c != '0') ? true : false;
        if (!bool && this.decimalAt != -1)
          b1++; 
      } 
      if (bool)
        this.digits[this.count++] = c; 
    } 
    if (this.decimalAt == -1)
      this.decimalAt = this.count; 
    if (bool)
      this.decimalAt += j - b1; 
    if (paramBoolean4) {
      if (-this.decimalAt > paramInt) {
        this.count = 0;
        return;
      } 
      if (-this.decimalAt == paramInt) {
        if (shouldRoundUp(0, paramBoolean2, paramBoolean3)) {
          this.count = 1;
          this.decimalAt++;
          this.digits[0] = '1';
        } else {
          this.count = 0;
        } 
        return;
      } 
    } 
    while (this.count > 1 && this.digits[this.count - 1] == '0')
      this.count--; 
    round(paramBoolean4 ? (paramInt + this.decimalAt) : paramInt, paramBoolean2, paramBoolean3);
  }
  
  private final void round(int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramInt >= 0 && paramInt < this.count) {
      if (shouldRoundUp(paramInt, paramBoolean1, paramBoolean2)) {
        do {
          paramInt--;
          if (paramInt < 0) {
            this.digits[0] = '1';
            this.decimalAt++;
            paramInt = 0;
            break;
          } 
          this.digits[paramInt] = (char)(this.digits[paramInt] + 1);
        } while (this.digits[paramInt] > '9');
        paramInt++;
      } 
      this.count = paramInt;
      while (this.count > 1 && this.digits[this.count - 1] == '0')
        this.count--; 
    } 
  }
  
  private boolean shouldRoundUp(int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramInt < this.count) {
      int i;
      switch (this.roundingMode) {
        case UP:
          for (i = paramInt; i < this.count; i++) {
            if (this.digits[i] != '0')
              return true; 
          } 
        case DOWN:
          return false;
        case CEILING:
          for (i = paramInt; i < this.count; i++) {
            if (this.digits[i] != '0')
              return !this.isNegative; 
          } 
        case FLOOR:
          for (i = paramInt; i < this.count; i++) {
            if (this.digits[i] != '0')
              return this.isNegative; 
          } 
        case HALF_UP:
        case HALF_DOWN:
          if (this.digits[paramInt] > '5')
            return true; 
          if (this.digits[paramInt] == '5') {
            if (paramInt != this.count - 1)
              return true; 
            if (paramBoolean2)
              return (this.roundingMode == RoundingMode.HALF_UP); 
            return !paramBoolean1;
          } 
        case HALF_EVEN:
          if (this.digits[paramInt] > '5')
            return true; 
          if (this.digits[paramInt] == '5') {
            if (paramInt == this.count - 1) {
              if (paramBoolean1)
                return false; 
              if (!paramBoolean2)
                return true; 
              return (paramInt > 0 && this.digits[paramInt - 1] % 2 != 0);
            } 
            for (i = paramInt + 1; i < this.count; i++) {
              if (this.digits[i] != '0')
                return true; 
            } 
          } 
        case UNNECESSARY:
          for (i = paramInt; i < this.count; i++) {
            if (this.digits[i] != '0')
              throw new ArithmeticException("Rounding needed with the rounding mode being set to RoundingMode.UNNECESSARY"); 
          } 
      } 
      assert false;
    } 
  }
  
  final void set(boolean paramBoolean, long paramLong) {
    set(paramBoolean, paramLong, 0);
  }
  
  final void set(boolean paramBoolean, long paramLong, int paramInt) {
    this.isNegative = paramBoolean;
    if (paramLong <= 0L) {
      if (paramLong == Long.MIN_VALUE) {
        this.decimalAt = this.count = 19;
        System.arraycopy(LONG_MIN_REP, 0, this.digits, 0, this.count);
      } else {
        this.decimalAt = this.count = 0;
      } 
    } else {
      byte b1 = 19;
      while (paramLong > 0L) {
        this.digits[--b1] = (char)(int)(48L + paramLong % 10L);
        paramLong /= 10L;
      } 
      this.decimalAt = 19 - b1;
      byte b2;
      for (b2 = 18; this.digits[b2] == '0'; b2--);
      this.count = b2 - b1 + 1;
      System.arraycopy(this.digits, b1, this.digits, 0, this.count);
    } 
    if (paramInt > 0)
      round(paramInt, false, true); 
  }
  
  final void set(boolean paramBoolean1, BigDecimal paramBigDecimal, int paramInt, boolean paramBoolean2) {
    String str = paramBigDecimal.toString();
    extendDigits(str.length());
    set(paramBoolean1, str, false, true, paramInt, paramBoolean2);
  }
  
  final void set(boolean paramBoolean, BigInteger paramBigInteger, int paramInt) {
    this.isNegative = paramBoolean;
    String str = paramBigInteger.toString();
    int i = str.length();
    extendDigits(i);
    str.getChars(0, i, this.digits, 0);
    this.decimalAt = i;
    int j;
    for (j = i - 1; j >= 0 && this.digits[j] == '0'; j--);
    this.count = j + 1;
    if (paramInt > 0)
      round(paramInt, false, true); 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof DigitList))
      return false; 
    DigitList digitList = (DigitList)paramObject;
    if (this.count != digitList.count || this.decimalAt != digitList.decimalAt)
      return false; 
    for (byte b = 0; b < this.count; b++) {
      if (this.digits[b] != digitList.digits[b])
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    int i = this.decimalAt;
    for (byte b = 0; b < this.count; b++)
      i = i * 37 + this.digits[b]; 
    return i;
  }
  
  public Object clone() {
    try {
      DigitList digitList = (DigitList)super.clone();
      char[] arrayOfChar = new char[this.digits.length];
      System.arraycopy(this.digits, 0, arrayOfChar, 0, this.digits.length);
      digitList.digits = arrayOfChar;
      digitList.tempBuffer = null;
      return digitList;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  private boolean isLongMIN_VALUE() {
    if (this.decimalAt != this.count || this.count != 19)
      return false; 
    for (byte b = 0; b < this.count; b++) {
      if (this.digits[b] != LONG_MIN_REP[b])
        return false; 
    } 
    return true;
  }
  
  private static final int parseInt(char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    boolean bool = true;
    char c;
    if ((c = paramArrayOfchar[paramInt1]) == '-') {
      bool = false;
      paramInt1++;
    } else if (c == '+') {
      paramInt1++;
    } 
    int i = 0;
    while (paramInt1 < paramInt2) {
      c = paramArrayOfchar[paramInt1++];
      if (c >= '0' && c <= '9')
        i = i * 10 + c - 48; 
    } 
    return bool ? i : -i;
  }
  
  private static final char[] LONG_MIN_REP = "9223372036854775808".toCharArray();
  
  private StringBuffer tempBuffer;
  
  public String toString() {
    if (isZero())
      return "0"; 
    StringBuffer stringBuffer = getStringBuffer();
    stringBuffer.append("0.");
    stringBuffer.append(this.digits, 0, this.count);
    stringBuffer.append("x10^");
    stringBuffer.append(this.decimalAt);
    return stringBuffer.toString();
  }
  
  private StringBuffer getStringBuffer() {
    if (this.tempBuffer == null) {
      this.tempBuffer = new StringBuffer(19);
    } else {
      this.tempBuffer.setLength(0);
    } 
    return this.tempBuffer;
  }
  
  private void extendDigits(int paramInt) {
    if (paramInt > this.digits.length)
      this.digits = new char[paramInt]; 
  }
  
  private final char[] getDataChars(int paramInt) {
    if (this.data == null || this.data.length < paramInt)
      this.data = new char[paramInt]; 
    return this.data;
  }
}
