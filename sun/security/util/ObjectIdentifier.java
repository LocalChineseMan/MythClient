package sun.security.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

public final class ObjectIdentifier implements Serializable {
  private byte[] encoding = null;
  
  private volatile transient String stringForm;
  
  private static final long serialVersionUID = 8697030238860181294L;
  
  private Object components = null;
  
  private int componentLen = -1;
  
  private transient boolean componentsCalculated = false;
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.encoding == null)
      init((int[])this.components, this.componentLen); 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (!this.componentsCalculated) {
      int[] arrayOfInt = toIntArray();
      if (arrayOfInt != null) {
        this.components = arrayOfInt;
        this.componentLen = arrayOfInt.length;
      } else {
        this.components = HugeOidNotSupportedByOldJDK.theOne;
      } 
      this.componentsCalculated = true;
    } 
    paramObjectOutputStream.defaultWriteObject();
  }
  
  public ObjectIdentifier(String paramString) throws IOException {
    byte b1 = 46;
    int i = 0;
    int j = 0;
    int k = 0;
    byte[] arrayOfByte = new byte[paramString.length()];
    int m = 0;
    byte b2 = 0;
    try {
      String str = null;
      do {
        int n = 0;
        j = paramString.indexOf(b1, i);
        if (j == -1) {
          str = paramString.substring(i);
          n = paramString.length() - i;
        } else {
          str = paramString.substring(i, j);
          n = j - i;
        } 
        if (n > 9) {
          BigInteger bigInteger = new BigInteger(str);
          if (!b2) {
            checkFirstComponent(bigInteger);
            m = bigInteger.intValue();
          } else {
            if (b2 == 1) {
              checkSecondComponent(m, bigInteger);
              bigInteger = bigInteger.add(BigInteger.valueOf((40 * m)));
            } else {
              checkOtherComponent(b2, bigInteger);
            } 
            k += pack7Oid(bigInteger, arrayOfByte, k);
          } 
        } else {
          int i1 = Integer.parseInt(str);
          if (!b2) {
            checkFirstComponent(i1);
            m = i1;
          } else {
            if (b2 == 1) {
              checkSecondComponent(m, i1);
              i1 += 40 * m;
            } else {
              checkOtherComponent(b2, i1);
            } 
            k += pack7Oid(i1, arrayOfByte, k);
          } 
        } 
        i = j + 1;
        b2++;
      } while (j != -1);
      checkCount(b2);
      this.encoding = new byte[k];
      System.arraycopy(arrayOfByte, 0, this.encoding, 0, k);
      this.stringForm = paramString;
    } catch (IOException iOException) {
      throw iOException;
    } catch (Exception exception) {
      throw new IOException("ObjectIdentifier() -- Invalid format: " + exception
          .toString(), exception);
    } 
  }
  
  public ObjectIdentifier(int[] paramArrayOfint) throws IOException {
    checkCount(paramArrayOfint.length);
    checkFirstComponent(paramArrayOfint[0]);
    checkSecondComponent(paramArrayOfint[0], paramArrayOfint[1]);
    for (byte b = 2; b < paramArrayOfint.length; b++)
      checkOtherComponent(b, paramArrayOfint[b]); 
    init(paramArrayOfint, paramArrayOfint.length);
  }
  
  public ObjectIdentifier(DerInputStream paramDerInputStream) throws IOException {
    byte b = (byte)paramDerInputStream.getByte();
    if (b != 6)
      throw new IOException("ObjectIdentifier() -- data isn't an object ID (tag = " + b + ")"); 
    this.encoding = new byte[paramDerInputStream.getLength()];
    paramDerInputStream.getBytes(this.encoding);
    check(this.encoding);
  }
  
  ObjectIdentifier(DerInputBuffer paramDerInputBuffer) throws IOException {
    DerInputStream derInputStream = new DerInputStream(paramDerInputBuffer);
    this.encoding = new byte[derInputStream.available()];
    derInputStream.getBytes(this.encoding);
    check(this.encoding);
  }
  
  private void init(int[] paramArrayOfint, int paramInt) {
    int i = 0;
    byte[] arrayOfByte = new byte[paramInt * 5 + 1];
    if (paramArrayOfint[1] < Integer.MAX_VALUE - paramArrayOfint[0] * 40) {
      i += pack7Oid(paramArrayOfint[0] * 40 + paramArrayOfint[1], arrayOfByte, i);
    } else {
      BigInteger bigInteger = BigInteger.valueOf(paramArrayOfint[1]);
      bigInteger = bigInteger.add(BigInteger.valueOf((paramArrayOfint[0] * 40)));
      i += pack7Oid(bigInteger, arrayOfByte, i);
    } 
    for (byte b = 2; b < paramInt; b++)
      i += pack7Oid(paramArrayOfint[b], arrayOfByte, i); 
    this.encoding = new byte[i];
    System.arraycopy(arrayOfByte, 0, this.encoding, 0, i);
  }
  
  public static ObjectIdentifier newInternal(int[] paramArrayOfint) {
    try {
      return new ObjectIdentifier(paramArrayOfint);
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  void encode(DerOutputStream paramDerOutputStream) throws IOException {
    paramDerOutputStream.write((byte)6, this.encoding);
  }
  
  @Deprecated
  public boolean equals(ObjectIdentifier paramObjectIdentifier) {
    return equals(paramObjectIdentifier);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof ObjectIdentifier))
      return false; 
    ObjectIdentifier objectIdentifier = (ObjectIdentifier)paramObject;
    return Arrays.equals(this.encoding, objectIdentifier.encoding);
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.encoding);
  }
  
  private int[] toIntArray() {
    int i = this.encoding.length;
    int[] arrayOfInt = new int[20];
    byte b1 = 0;
    int j = 0;
    for (byte b2 = 0; b2 < i; b2++) {
      if ((this.encoding[b2] & 0x80) == 0) {
        if (b2 - j + 1 > 4) {
          BigInteger bigInteger = new BigInteger(pack(this.encoding, j, b2 - j + 1, 7, 8));
          if (j == 0) {
            arrayOfInt[b1++] = 2;
            BigInteger bigInteger1 = bigInteger.subtract(BigInteger.valueOf(80L));
            if (bigInteger1.compareTo(BigInteger.valueOf(2147483647L)) == 1)
              return null; 
            arrayOfInt[b1++] = bigInteger1.intValue();
          } else {
            if (bigInteger.compareTo(BigInteger.valueOf(2147483647L)) == 1)
              return null; 
            arrayOfInt[b1++] = bigInteger.intValue();
          } 
        } else {
          int k = 0;
          for (int m = j; m <= b2; m++) {
            k <<= 7;
            byte b = this.encoding[m];
            k |= b & Byte.MAX_VALUE;
          } 
          if (j == 0) {
            if (k < 80) {
              arrayOfInt[b1++] = k / 40;
              arrayOfInt[b1++] = k % 40;
            } else {
              arrayOfInt[b1++] = 2;
              arrayOfInt[b1++] = k - 80;
            } 
          } else {
            arrayOfInt[b1++] = k;
          } 
        } 
        j = b2 + 1;
      } 
      if (b1 >= arrayOfInt.length)
        arrayOfInt = Arrays.copyOf(arrayOfInt, b1 + 10); 
    } 
    return Arrays.copyOf(arrayOfInt, b1);
  }
  
  public String toString() {
    String str = this.stringForm;
    if (str == null) {
      int i = this.encoding.length;
      StringBuffer stringBuffer = new StringBuffer(i * 4);
      int j = 0;
      for (byte b = 0; b < i; b++) {
        if ((this.encoding[b] & 0x80) == 0) {
          if (j)
            stringBuffer.append('.'); 
          if (b - j + 1 > 4) {
            BigInteger bigInteger = new BigInteger(pack(this.encoding, j, b - j + 1, 7, 8));
            if (j == 0) {
              stringBuffer.append("2.");
              stringBuffer.append(bigInteger.subtract(BigInteger.valueOf(80L)));
            } else {
              stringBuffer.append(bigInteger);
            } 
          } else {
            int k = 0;
            for (int m = j; m <= b; m++) {
              k <<= 7;
              byte b1 = this.encoding[m];
              k |= b1 & Byte.MAX_VALUE;
            } 
            if (j == 0) {
              if (k < 80) {
                stringBuffer.append(k / 40);
                stringBuffer.append('.');
                stringBuffer.append(k % 40);
              } else {
                stringBuffer.append("2.");
                stringBuffer.append(k - 80);
              } 
            } else {
              stringBuffer.append(k);
            } 
          } 
          j = b + 1;
        } 
      } 
      str = stringBuffer.toString();
      this.stringForm = str;
    } 
    return str;
  }
  
  private static byte[] pack(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    assert paramInt3 > 0 && paramInt3 <= 8 : "input NUB must be between 1 and 8";
    assert paramInt4 > 0 && paramInt4 <= 8 : "output NUB must be between 1 and 8";
    if (paramInt3 == paramInt4)
      return (byte[])paramArrayOfbyte.clone(); 
    int i = paramInt2 * paramInt3;
    byte[] arrayOfByte = new byte[(i + paramInt4 - 1) / paramInt4];
    int j = 0;
    int k = (i + paramInt4 - 1) / paramInt4 * paramInt4 - i;
    while (j < i) {
      int m = paramInt3 - j % paramInt3;
      if (m > paramInt4 - k % paramInt4)
        m = paramInt4 - k % paramInt4; 
      arrayOfByte[k / paramInt4] = (byte)(arrayOfByte[k / paramInt4] | (paramArrayOfbyte[paramInt1 + j / paramInt3] + 256 >> paramInt3 - j % paramInt3 - m & (1 << m) - 1) << paramInt4 - k % paramInt4 - m);
      j += m;
      k += m;
    } 
    return arrayOfByte;
  }
  
  private static int pack7Oid(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    byte[] arrayOfByte = pack(paramArrayOfbyte1, paramInt1, paramInt2, 8, 7);
    int i = arrayOfByte.length - 1;
    for (int j = arrayOfByte.length - 2; j >= 0; j--) {
      if (arrayOfByte[j] != 0)
        i = j; 
      arrayOfByte[j] = (byte)(arrayOfByte[j] | 0x80);
    } 
    System.arraycopy(arrayOfByte, i, paramArrayOfbyte2, paramInt3, arrayOfByte.length - i);
    return arrayOfByte.length - i;
  }
  
  private static int pack8(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    byte[] arrayOfByte = pack(paramArrayOfbyte1, paramInt1, paramInt2, 7, 8);
    int i = arrayOfByte.length - 1;
    for (int j = arrayOfByte.length - 2; j >= 0; j--) {
      if (arrayOfByte[j] != 0)
        i = j; 
    } 
    System.arraycopy(arrayOfByte, i, paramArrayOfbyte2, paramInt3, arrayOfByte.length - i);
    return arrayOfByte.length - i;
  }
  
  private static int pack7Oid(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = (byte)(paramInt1 >> 24);
    arrayOfByte[1] = (byte)(paramInt1 >> 16);
    arrayOfByte[2] = (byte)(paramInt1 >> 8);
    arrayOfByte[3] = (byte)paramInt1;
    return pack7Oid(arrayOfByte, 0, 4, paramArrayOfbyte, paramInt2);
  }
  
  private static int pack7Oid(BigInteger paramBigInteger, byte[] paramArrayOfbyte, int paramInt) {
    byte[] arrayOfByte = paramBigInteger.toByteArray();
    return pack7Oid(arrayOfByte, 0, arrayOfByte.length, paramArrayOfbyte, paramInt);
  }
  
  private static void check(byte[] paramArrayOfbyte) throws IOException {
    int i = paramArrayOfbyte.length;
    if (i < 1 || (paramArrayOfbyte[i - 1] & 0x80) != 0)
      throw new IOException("ObjectIdentifier() -- Invalid DER encoding, not ended"); 
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfbyte[b] == Byte.MIN_VALUE && (b == 0 || (paramArrayOfbyte[b - 1] & 0x80) == 0))
        throw new IOException("ObjectIdentifier() -- Invalid DER encoding, useless extra octet detected"); 
    } 
  }
  
  private static void checkCount(int paramInt) throws IOException {
    if (paramInt < 2)
      throw new IOException("ObjectIdentifier() -- Must be at least two oid components "); 
  }
  
  private static void checkFirstComponent(int paramInt) throws IOException {
    if (paramInt < 0 || paramInt > 2)
      throw new IOException("ObjectIdentifier() -- First oid component is invalid "); 
  }
  
  private static void checkFirstComponent(BigInteger paramBigInteger) throws IOException {
    if (paramBigInteger.signum() == -1 || paramBigInteger
      .compareTo(BigInteger.valueOf(2L)) == 1)
      throw new IOException("ObjectIdentifier() -- First oid component is invalid "); 
  }
  
  private static void checkSecondComponent(int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 < 0 || (paramInt1 != 2 && paramInt2 > 39))
      throw new IOException("ObjectIdentifier() -- Second oid component is invalid "); 
  }
  
  private static void checkSecondComponent(int paramInt, BigInteger paramBigInteger) throws IOException {
    if (paramBigInteger.signum() == -1 || (paramInt != 2 && paramBigInteger
      
      .compareTo(BigInteger.valueOf(39L)) == 1))
      throw new IOException("ObjectIdentifier() -- Second oid component is invalid "); 
  }
  
  private static void checkOtherComponent(int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 < 0)
      throw new IOException("ObjectIdentifier() -- oid component #" + (paramInt1 + 1) + " must be non-negative "); 
  }
  
  private static void checkOtherComponent(int paramInt, BigInteger paramBigInteger) throws IOException {
    if (paramBigInteger.signum() == -1)
      throw new IOException("ObjectIdentifier() -- oid component #" + (paramInt + 1) + " must be non-negative "); 
  }
  
  static class ObjectIdentifier {}
}
