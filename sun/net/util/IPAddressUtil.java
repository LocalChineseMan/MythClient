package sun.net.util;

public class IPAddressUtil {
  private static final int INADDR4SZ = 4;
  
  private static final int INADDR16SZ = 16;
  
  private static final int INT16SZ = 2;
  
  public static byte[] textToNumericFormatV4(String paramString) {
    byte[] arrayOfByte = new byte[4];
    long l = 0L;
    byte b1 = 0;
    int i = paramString.length();
    if (i == 0 || i > 15)
      return null; 
    for (byte b2 = 0; b2 < i; b2++) {
      char c = paramString.charAt(b2);
      if (c == '.') {
        if (l < 0L || l > 255L || b1 == 3)
          return null; 
        arrayOfByte[b1++] = (byte)(int)(l & 0xFFL);
        l = 0L;
      } else {
        int j = Character.digit(c, 10);
        if (j < 0)
          return null; 
        l *= 10L;
        l += j;
      } 
    } 
    if (l < 0L || l >= 1L << (4 - b1) * 8)
      return null; 
    switch (b1) {
      case 0:
        arrayOfByte[0] = (byte)(int)(l >> 24L & 0xFFL);
      case 1:
        arrayOfByte[1] = (byte)(int)(l >> 16L & 0xFFL);
      case 2:
        arrayOfByte[2] = (byte)(int)(l >> 8L & 0xFFL);
      case 3:
        arrayOfByte[3] = (byte)(int)(l >> 0L & 0xFFL);
        break;
    } 
    return arrayOfByte;
  }
  
  public static byte[] textToNumericFormatV6(String paramString) {
    if (paramString.length() < 2)
      return null; 
    char[] arrayOfChar = paramString.toCharArray();
    byte[] arrayOfByte1 = new byte[16];
    int j = arrayOfChar.length;
    int k = paramString.indexOf("%");
    if (k == j - 1)
      return null; 
    if (k != -1)
      j = k; 
    byte b = -1;
    byte b1 = 0, b2 = 0;
    if (arrayOfChar[b1] == ':' && 
      arrayOfChar[++b1] != ':')
      return null; 
    byte b3 = b1;
    boolean bool = false;
    int i = 0;
    while (b1 < j) {
      char c = arrayOfChar[b1++];
      int m = Character.digit(c, 16);
      if (m != -1) {
        i <<= 4;
        i |= m;
        if (i > 65535)
          return null; 
        bool = true;
        continue;
      } 
      if (c == ':') {
        b3 = b1;
        if (!bool) {
          if (b != -1)
            return null; 
          b = b2;
          continue;
        } 
        if (b1 == j)
          return null; 
        if (b2 + 2 > 16)
          return null; 
        arrayOfByte1[b2++] = (byte)(i >> 8 & 0xFF);
        arrayOfByte1[b2++] = (byte)(i & 0xFF);
        bool = false;
        i = 0;
        continue;
      } 
      if (c == '.' && b2 + 4 <= 16) {
        String str = paramString.substring(b3, j);
        byte b4 = 0;
        int n = 0;
        while ((n = str.indexOf('.', n)) != -1) {
          b4++;
          n++;
        } 
        if (b4 != 3)
          return null; 
        byte[] arrayOfByte = textToNumericFormatV4(str);
        if (arrayOfByte == null)
          return null; 
        for (byte b5 = 0; b5 < 4; b5++)
          arrayOfByte1[b2++] = arrayOfByte[b5]; 
        bool = false;
        break;
      } 
      return null;
    } 
    if (bool) {
      if (b2 + 2 > 16)
        return null; 
      arrayOfByte1[b2++] = (byte)(i >> 8 & 0xFF);
      arrayOfByte1[b2++] = (byte)(i & 0xFF);
    } 
    if (b != -1) {
      int m = b2 - b;
      if (b2 == 16)
        return null; 
      for (b1 = 1; b1 <= m; b1++) {
        arrayOfByte1[16 - b1] = arrayOfByte1[b + m - b1];
        arrayOfByte1[b + m - b1] = 0;
      } 
      b2 = 16;
    } 
    if (b2 != 16)
      return null; 
    byte[] arrayOfByte2 = convertFromIPv4MappedAddress(arrayOfByte1);
    if (arrayOfByte2 != null)
      return arrayOfByte2; 
    return arrayOfByte1;
  }
  
  public static boolean isIPv4LiteralAddress(String paramString) {
    return (textToNumericFormatV4(paramString) != null);
  }
  
  public static boolean isIPv6LiteralAddress(String paramString) {
    return (textToNumericFormatV6(paramString) != null);
  }
  
  public static byte[] convertFromIPv4MappedAddress(byte[] paramArrayOfbyte) {
    if (isIPv4MappedAddress(paramArrayOfbyte)) {
      byte[] arrayOfByte = new byte[4];
      System.arraycopy(paramArrayOfbyte, 12, arrayOfByte, 0, 4);
      return arrayOfByte;
    } 
    return null;
  }
  
  private static boolean isIPv4MappedAddress(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length < 16)
      return false; 
    if (paramArrayOfbyte[0] == 0 && paramArrayOfbyte[1] == 0 && paramArrayOfbyte[2] == 0 && paramArrayOfbyte[3] == 0 && paramArrayOfbyte[4] == 0 && paramArrayOfbyte[5] == 0 && paramArrayOfbyte[6] == 0 && paramArrayOfbyte[7] == 0 && paramArrayOfbyte[8] == 0 && paramArrayOfbyte[9] == 0 && paramArrayOfbyte[10] == -1 && paramArrayOfbyte[11] == -1)
      return true; 
    return false;
  }
}
