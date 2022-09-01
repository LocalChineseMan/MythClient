package sun.security.util;

import java.util.Comparator;

public class ByteArrayTagOrder implements Comparator<byte[]> {
  public final int compare(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    return (paramArrayOfbyte1[0] | 0x20) - (paramArrayOfbyte2[0] | 0x20);
  }
}
