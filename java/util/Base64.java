package java.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Base64 {
  public static Encoder getEncoder() {
    return Encoder.RFC4648;
  }
  
  public static Encoder getUrlEncoder() {
    return Encoder.RFC4648_URLSAFE;
  }
  
  public static Encoder getMimeEncoder() {
    return Encoder.RFC2045;
  }
  
  public static Encoder getMimeEncoder(int paramInt, byte[] paramArrayOfbyte) {
    Objects.requireNonNull(paramArrayOfbyte);
    int[] arrayOfInt = Decoder.fromBase64;
    for (byte b : paramArrayOfbyte) {
      if (arrayOfInt[b & 0xFF] != -1)
        throw new IllegalArgumentException("Illegal base64 line separator character 0x" + 
            Integer.toString(b, 16)); 
    } 
    if (paramInt <= 0)
      return Encoder.RFC4648; 
    return new Encoder(false, paramArrayOfbyte, paramInt >> 2 << 2, true);
  }
  
  public static Decoder getDecoder() {
    return Decoder.RFC4648;
  }
  
  public static Decoder getUrlDecoder() {
    return Decoder.RFC4648_URLSAFE;
  }
  
  public static Decoder getMimeDecoder() {
    return Decoder.RFC2045;
  }
  
  public static class Encoder {
    private final byte[] newline;
    
    private final int linemax;
    
    private final boolean isURL;
    
    private final boolean doPadding;
    
    private Encoder(boolean param1Boolean1, byte[] param1ArrayOfbyte, int param1Int, boolean param1Boolean2) {
      this.isURL = param1Boolean1;
      this.newline = param1ArrayOfbyte;
      this.linemax = param1Int;
      this.doPadding = param1Boolean2;
    }
    
    private static final char[] toBase64 = new char[] { 
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
        'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
        'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
        '8', '9', '+', '/' };
    
    private static final char[] toBase64URL = new char[] { 
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
        'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
        'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
        '8', '9', '-', '_' };
    
    private static final int MIMELINEMAX = 76;
    
    private static final byte[] CRLF = new byte[] { 13, 10 };
    
    static final Encoder RFC4648 = new Encoder(false, null, -1, true);
    
    static final Encoder RFC4648_URLSAFE = new Encoder(true, null, -1, true);
    
    static final Encoder RFC2045 = new Encoder(false, CRLF, 76, true);
    
    private final int outLength(int param1Int) {
      int i = 0;
      if (this.doPadding) {
        i = 4 * (param1Int + 2) / 3;
      } else {
        int j = param1Int % 3;
        i = 4 * param1Int / 3 + ((j == 0) ? 0 : (j + 1));
      } 
      if (this.linemax > 0)
        i += (i - 1) / this.linemax * this.newline.length; 
      return i;
    }
    
    public byte[] encode(byte[] param1ArrayOfbyte) {
      int i = outLength(param1ArrayOfbyte.length);
      byte[] arrayOfByte = new byte[i];
      int j = encode0(param1ArrayOfbyte, 0, param1ArrayOfbyte.length, arrayOfByte);
      if (j != arrayOfByte.length)
        return Arrays.copyOf(arrayOfByte, j); 
      return arrayOfByte;
    }
    
    public int encode(byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2) {
      int i = outLength(param1ArrayOfbyte1.length);
      if (param1ArrayOfbyte2.length < i)
        throw new IllegalArgumentException("Output byte array is too small for encoding all input bytes"); 
      return encode0(param1ArrayOfbyte1, 0, param1ArrayOfbyte1.length, param1ArrayOfbyte2);
    }
    
    public String encodeToString(byte[] param1ArrayOfbyte) {
      byte[] arrayOfByte = encode(param1ArrayOfbyte);
      return new String(arrayOfByte, 0, 0, arrayOfByte.length);
    }
    
    public ByteBuffer encode(ByteBuffer param1ByteBuffer) {
      int i = outLength(param1ByteBuffer.remaining());
      byte[] arrayOfByte = new byte[i];
      int j = 0;
      if (param1ByteBuffer.hasArray()) {
        j = encode0(param1ByteBuffer.array(), param1ByteBuffer
            .arrayOffset() + param1ByteBuffer.position(), param1ByteBuffer
            .arrayOffset() + param1ByteBuffer.limit(), arrayOfByte);
        param1ByteBuffer.position(param1ByteBuffer.limit());
      } else {
        byte[] arrayOfByte1 = new byte[param1ByteBuffer.remaining()];
        param1ByteBuffer.get(arrayOfByte1);
        j = encode0(arrayOfByte1, 0, arrayOfByte1.length, arrayOfByte);
      } 
      if (j != arrayOfByte.length)
        arrayOfByte = Arrays.copyOf(arrayOfByte, j); 
      return ByteBuffer.wrap(arrayOfByte);
    }
    
    public OutputStream wrap(OutputStream param1OutputStream) {
      Objects.requireNonNull(param1OutputStream);
      return new Base64.EncOutputStream(param1OutputStream, this.isURL ? toBase64URL : toBase64, this.newline, this.linemax, this.doPadding);
    }
    
    public Encoder withoutPadding() {
      if (!this.doPadding)
        return this; 
      return new Encoder(this.isURL, this.newline, this.linemax, false);
    }
    
    private int encode0(byte[] param1ArrayOfbyte1, int param1Int1, int param1Int2, byte[] param1ArrayOfbyte2) {
      char[] arrayOfChar = this.isURL ? toBase64URL : toBase64;
      int i = param1Int1;
      int j = (param1Int2 - param1Int1) / 3 * 3;
      int k = param1Int1 + j;
      if (this.linemax > 0 && j > this.linemax / 4 * 3)
        j = this.linemax / 4 * 3; 
      int m = 0;
      while (i < k) {
        int n = Math.min(i + j, k);
        int i1, i2;
        for (i1 = i, i2 = m; i1 < n; ) {
          int i3 = (param1ArrayOfbyte1[i1++] & 0xFF) << 16 | (param1ArrayOfbyte1[i1++] & 0xFF) << 8 | param1ArrayOfbyte1[i1++] & 0xFF;
          param1ArrayOfbyte2[i2++] = (byte)arrayOfChar[i3 >>> 18 & 0x3F];
          param1ArrayOfbyte2[i2++] = (byte)arrayOfChar[i3 >>> 12 & 0x3F];
          param1ArrayOfbyte2[i2++] = (byte)arrayOfChar[i3 >>> 6 & 0x3F];
          param1ArrayOfbyte2[i2++] = (byte)arrayOfChar[i3 & 0x3F];
        } 
        i1 = (n - i) / 3 * 4;
        m += i1;
        i = n;
        if (i1 == this.linemax && i < param1Int2)
          for (byte b : this.newline)
            param1ArrayOfbyte2[m++] = b;  
      } 
      if (i < param1Int2) {
        int n = param1ArrayOfbyte1[i++] & 0xFF;
        param1ArrayOfbyte2[m++] = (byte)arrayOfChar[n >> 2];
        if (i == param1Int2) {
          param1ArrayOfbyte2[m++] = (byte)arrayOfChar[n << 4 & 0x3F];
          if (this.doPadding) {
            param1ArrayOfbyte2[m++] = 61;
            param1ArrayOfbyte2[m++] = 61;
          } 
        } else {
          int i1 = param1ArrayOfbyte1[i++] & 0xFF;
          param1ArrayOfbyte2[m++] = (byte)arrayOfChar[n << 4 & 0x3F | i1 >> 4];
          param1ArrayOfbyte2[m++] = (byte)arrayOfChar[i1 << 2 & 0x3F];
          if (this.doPadding)
            param1ArrayOfbyte2[m++] = 61; 
        } 
      } 
      return m;
    }
  }
  
  public static class Decoder {
    private final boolean isURL;
    
    private final boolean isMIME;
    
    private Decoder(boolean param1Boolean1, boolean param1Boolean2) {
      this.isURL = param1Boolean1;
      this.isMIME = param1Boolean2;
    }
    
    private static final int[] fromBase64 = new int[256];
    
    static {
      Arrays.fill(fromBase64, -1);
      byte b;
      for (b = 0; b < Base64.Encoder.toBase64.length; b++)
        fromBase64[Base64.Encoder.toBase64[b]] = b; 
      fromBase64[61] = -2;
    }
    
    private static final int[] fromBase64URL = new int[256];
    
    static {
      Arrays.fill(fromBase64URL, -1);
      for (b = 0; b < Base64.Encoder.toBase64URL.length; b++)
        fromBase64URL[Base64.Encoder.toBase64URL[b]] = b; 
      fromBase64URL[61] = -2;
    }
    
    static final Decoder RFC4648 = new Decoder(false, false);
    
    static final Decoder RFC4648_URLSAFE = new Decoder(true, false);
    
    static final Decoder RFC2045 = new Decoder(false, true);
    
    public byte[] decode(byte[] param1ArrayOfbyte) {
      byte[] arrayOfByte = new byte[outLength(param1ArrayOfbyte, 0, param1ArrayOfbyte.length)];
      int i = decode0(param1ArrayOfbyte, 0, param1ArrayOfbyte.length, arrayOfByte);
      if (i != arrayOfByte.length)
        arrayOfByte = Arrays.copyOf(arrayOfByte, i); 
      return arrayOfByte;
    }
    
    public byte[] decode(String param1String) {
      return decode(param1String.getBytes(StandardCharsets.ISO_8859_1));
    }
    
    public int decode(byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2) {
      int i = outLength(param1ArrayOfbyte1, 0, param1ArrayOfbyte1.length);
      if (param1ArrayOfbyte2.length < i)
        throw new IllegalArgumentException("Output byte array is too small for decoding all input bytes"); 
      return decode0(param1ArrayOfbyte1, 0, param1ArrayOfbyte1.length, param1ArrayOfbyte2);
    }
    
    public ByteBuffer decode(ByteBuffer param1ByteBuffer) {
      int i = param1ByteBuffer.position();
      try {
        byte[] arrayOfByte1;
        boolean bool;
        int j;
        if (param1ByteBuffer.hasArray()) {
          arrayOfByte1 = param1ByteBuffer.array();
          bool = param1ByteBuffer.arrayOffset() + param1ByteBuffer.position();
          j = param1ByteBuffer.arrayOffset() + param1ByteBuffer.limit();
          param1ByteBuffer.position(param1ByteBuffer.limit());
        } else {
          arrayOfByte1 = new byte[param1ByteBuffer.remaining()];
          param1ByteBuffer.get(arrayOfByte1);
          bool = false;
          j = arrayOfByte1.length;
        } 
        byte[] arrayOfByte2 = new byte[outLength(arrayOfByte1, bool, j)];
        return ByteBuffer.wrap(arrayOfByte2, 0, decode0(arrayOfByte1, bool, j, arrayOfByte2));
      } catch (IllegalArgumentException illegalArgumentException) {
        param1ByteBuffer.position(i);
        throw illegalArgumentException;
      } 
    }
    
    public InputStream wrap(InputStream param1InputStream) {
      Objects.requireNonNull(param1InputStream);
      return new Base64.DecInputStream(param1InputStream, this.isURL ? fromBase64URL : fromBase64, this.isMIME);
    }
    
    private int outLength(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) {
      int[] arrayOfInt = this.isURL ? fromBase64URL : fromBase64;
      int i = 0;
      int j = param1Int2 - param1Int1;
      if (j == 0)
        return 0; 
      if (j < 2) {
        if (this.isMIME && arrayOfInt[0] == -1)
          return 0; 
        throw new IllegalArgumentException("Input byte[] should at least have 2 bytes for base64 bytes");
      } 
      if (this.isMIME) {
        byte b = 0;
        while (param1Int1 < param1Int2) {
          int k = param1ArrayOfbyte[param1Int1++] & 0xFF;
          if (k == 61) {
            j -= param1Int2 - param1Int1 + 1;
            break;
          } 
          if ((k = arrayOfInt[k]) == -1)
            b++; 
        } 
        j -= b;
      } else if (param1ArrayOfbyte[param1Int2 - 1] == 61) {
        i++;
        if (param1ArrayOfbyte[param1Int2 - 2] == 61)
          i++; 
      } 
      if (i == 0 && (j & 0x3) != 0)
        i = 4 - (j & 0x3); 
      return 3 * (j + 3) / 4 - i;
    }
    
    private int decode0(byte[] param1ArrayOfbyte1, int param1Int1, int param1Int2, byte[] param1ArrayOfbyte2) {
      int[] arrayOfInt = this.isURL ? fromBase64URL : fromBase64;
      byte b1 = 0;
      int i = 0;
      byte b2 = 18;
      while (param1Int1 < param1Int2) {
        int j = param1ArrayOfbyte1[param1Int1++] & 0xFF;
        if ((j = arrayOfInt[j]) < 0) {
          if (j == -2) {
            if ((b2 == 6 && (param1Int1 == param1Int2 || param1ArrayOfbyte1[param1Int1++] != 61)) || b2 == 18)
              throw new IllegalArgumentException("Input byte array has wrong 4-byte ending unit"); 
            break;
          } 
          if (this.isMIME)
            continue; 
          throw new IllegalArgumentException("Illegal base64 character " + 
              
              Integer.toString(param1ArrayOfbyte1[param1Int1 - 1], 16));
        } 
        i |= j << b2;
        b2 -= 6;
        if (b2 < 0) {
          param1ArrayOfbyte2[b1++] = (byte)(i >> 16);
          param1ArrayOfbyte2[b1++] = (byte)(i >> 8);
          param1ArrayOfbyte2[b1++] = (byte)i;
          b2 = 18;
          i = 0;
        } 
      } 
      if (b2 == 6) {
        param1ArrayOfbyte2[b1++] = (byte)(i >> 16);
      } else if (b2 == 0) {
        param1ArrayOfbyte2[b1++] = (byte)(i >> 16);
        param1ArrayOfbyte2[b1++] = (byte)(i >> 8);
      } else if (b2 == 12) {
        throw new IllegalArgumentException("Last unit does not have enough valid bits");
      } 
      while (param1Int1 < param1Int2) {
        if (this.isMIME && arrayOfInt[param1ArrayOfbyte1[param1Int1++]] < 0)
          continue; 
        throw new IllegalArgumentException("Input byte array has incorrect ending byte at " + param1Int1);
      } 
      return b1;
    }
  }
  
  private static class Base64 {}
  
  private static class Base64 {}
}
