package sun.reflect;

class UTF8 {
  static byte[] encode(String paramString) {
    int i = paramString.length();
    byte[] arrayOfByte = new byte[utf8Length(paramString)];
    byte b = 0;
    try {
      for (byte b1 = 0; b1 < i; b1++) {
        int j = paramString.charAt(b1) & Character.MAX_VALUE;
        if (j >= 1 && j <= 127) {
          arrayOfByte[b++] = (byte)j;
        } else if (j == 0 || (j >= 128 && j <= 2047)) {
          arrayOfByte[b++] = (byte)(192 + (j >> 6));
          arrayOfByte[b++] = (byte)(128 + (j & 0x3F));
        } else {
          arrayOfByte[b++] = (byte)(224 + (j >> 12));
          arrayOfByte[b++] = (byte)(128 + (j >> 6 & 0x3F));
          arrayOfByte[b++] = (byte)(128 + (j & 0x3F));
        } 
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new InternalError("Bug in sun.reflect bootstrap UTF-8 encoder", arrayIndexOutOfBoundsException);
    } 
    return arrayOfByte;
  }
  
  private static int utf8Length(String paramString) {
    int i = paramString.length();
    byte b1 = 0;
    for (byte b2 = 0; b2 < i; b2++) {
      int j = paramString.charAt(b2) & Character.MAX_VALUE;
      if (j >= 1 && j <= 127) {
        b1++;
      } else if (j == 0 || (j >= 128 && j <= 2047)) {
        b1 += 2;
      } else {
        b1 += 3;
      } 
    } 
    return b1;
  }
}
