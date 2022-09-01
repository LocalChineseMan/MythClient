package sun.security.util;

import java.util.Comparator;

public class ByteArrayLexOrder implements Comparator<byte[]> {
  public final int compare(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    for (byte b = 0; b < paramArrayOfbyte1.length && b < paramArrayOfbyte2.length; b++) {
      int i = (paramArrayOfbyte1[b] & 0xFF) - (paramArrayOfbyte2[b] & 0xFF);
      if (i != 0)
        return i; 
    } 
    return paramArrayOfbyte1.length - paramArrayOfbyte2.length;
  }
}
