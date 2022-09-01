package sun.security.provider;

import java.nio.ByteOrder;
import java.security.AccessController;
import sun.misc.Unsafe;
import sun.security.action.GetPropertyAction;

final class ByteArrayAccess {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final boolean littleEndianUnaligned;
  
  private static final boolean bigEndian;
  
  private static final int byteArrayOfs = unsafe.arrayBaseOffset(byte[].class);
  
  static {
    boolean bool = (unsafe.arrayIndexScale(byte[].class) == 1 && unsafe.arrayIndexScale(int[].class) == 4 && unsafe.arrayIndexScale(long[].class) == 8 && (byteArrayOfs & 0x3) == 0) ? true : false;
    ByteOrder byteOrder = ByteOrder.nativeOrder();
    littleEndianUnaligned = (bool && unaligned() && byteOrder == ByteOrder.LITTLE_ENDIAN);
    bigEndian = (bool && byteOrder == ByteOrder.BIG_ENDIAN);
  }
  
  private static boolean unaligned() {
    String str = AccessController.<String>doPrivileged(new GetPropertyAction("os.arch", ""));
    return (str.equals("i386") || str.equals("x86") || str.equals("amd64") || str
      .equals("x86_64"));
  }
  
  static void b2iLittle(byte[] paramArrayOfbyte, int paramInt1, int[] paramArrayOfint, int paramInt2, int paramInt3) {
    if (paramInt1 < 0 || paramArrayOfbyte.length - paramInt1 < paramInt3 || paramInt2 < 0 || paramArrayOfint.length - paramInt2 < paramInt3 / 4)
      throw new ArrayIndexOutOfBoundsException(); 
    if (littleEndianUnaligned) {
      paramInt1 += byteArrayOfs;
      paramInt3 += paramInt1;
      while (paramInt1 < paramInt3) {
        paramArrayOfint[paramInt2++] = unsafe.getInt(paramArrayOfbyte, paramInt1);
        paramInt1 += 4;
      } 
    } else if (bigEndian && (paramInt1 & 0x3) == 0) {
      paramInt1 += byteArrayOfs;
      paramInt3 += paramInt1;
      while (paramInt1 < paramInt3) {
        paramArrayOfint[paramInt2++] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, paramInt1));
        paramInt1 += 4;
      } 
    } else {
      paramInt3 += paramInt1;
      while (paramInt1 < paramInt3) {
        paramArrayOfint[paramInt2++] = paramArrayOfbyte[paramInt1] & 0xFF | (paramArrayOfbyte[paramInt1 + 1] & 0xFF) << 8 | (paramArrayOfbyte[paramInt1 + 2] & 0xFF) << 16 | paramArrayOfbyte[paramInt1 + 3] << 24;
        paramInt1 += 4;
      } 
    } 
  }
  
  static void b2iLittle64(byte[] paramArrayOfbyte, int paramInt, int[] paramArrayOfint) {
    if (paramInt < 0 || paramArrayOfbyte.length - paramInt < 64 || paramArrayOfint.length < 16)
      throw new ArrayIndexOutOfBoundsException(); 
    if (littleEndianUnaligned) {
      paramInt += byteArrayOfs;
      paramArrayOfint[0] = unsafe.getInt(paramArrayOfbyte, paramInt);
      paramArrayOfint[1] = unsafe.getInt(paramArrayOfbyte, (paramInt + 4));
      paramArrayOfint[2] = unsafe.getInt(paramArrayOfbyte, (paramInt + 8));
      paramArrayOfint[3] = unsafe.getInt(paramArrayOfbyte, (paramInt + 12));
      paramArrayOfint[4] = unsafe.getInt(paramArrayOfbyte, (paramInt + 16));
      paramArrayOfint[5] = unsafe.getInt(paramArrayOfbyte, (paramInt + 20));
      paramArrayOfint[6] = unsafe.getInt(paramArrayOfbyte, (paramInt + 24));
      paramArrayOfint[7] = unsafe.getInt(paramArrayOfbyte, (paramInt + 28));
      paramArrayOfint[8] = unsafe.getInt(paramArrayOfbyte, (paramInt + 32));
      paramArrayOfint[9] = unsafe.getInt(paramArrayOfbyte, (paramInt + 36));
      paramArrayOfint[10] = unsafe.getInt(paramArrayOfbyte, (paramInt + 40));
      paramArrayOfint[11] = unsafe.getInt(paramArrayOfbyte, (paramInt + 44));
      paramArrayOfint[12] = unsafe.getInt(paramArrayOfbyte, (paramInt + 48));
      paramArrayOfint[13] = unsafe.getInt(paramArrayOfbyte, (paramInt + 52));
      paramArrayOfint[14] = unsafe.getInt(paramArrayOfbyte, (paramInt + 56));
      paramArrayOfint[15] = unsafe.getInt(paramArrayOfbyte, (paramInt + 60));
    } else if (bigEndian && (paramInt & 0x3) == 0) {
      paramInt += byteArrayOfs;
      paramArrayOfint[0] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, paramInt));
      paramArrayOfint[1] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 4)));
      paramArrayOfint[2] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 8)));
      paramArrayOfint[3] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 12)));
      paramArrayOfint[4] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 16)));
      paramArrayOfint[5] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 20)));
      paramArrayOfint[6] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 24)));
      paramArrayOfint[7] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 28)));
      paramArrayOfint[8] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 32)));
      paramArrayOfint[9] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 36)));
      paramArrayOfint[10] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 40)));
      paramArrayOfint[11] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 44)));
      paramArrayOfint[12] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 48)));
      paramArrayOfint[13] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 52)));
      paramArrayOfint[14] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 56)));
      paramArrayOfint[15] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 60)));
    } else {
      b2iLittle(paramArrayOfbyte, paramInt, paramArrayOfint, 0, 64);
    } 
  }
  
  static void i2bLittle(int[] paramArrayOfint, int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3) {
    if (paramInt1 < 0 || paramArrayOfint.length - paramInt1 < paramInt3 / 4 || paramInt2 < 0 || paramArrayOfbyte.length - paramInt2 < paramInt3)
      throw new ArrayIndexOutOfBoundsException(); 
    if (littleEndianUnaligned) {
      paramInt2 += byteArrayOfs;
      paramInt3 += paramInt2;
      while (paramInt2 < paramInt3) {
        unsafe.putInt(paramArrayOfbyte, paramInt2, paramArrayOfint[paramInt1++]);
        paramInt2 += 4;
      } 
    } else if (bigEndian && (paramInt2 & 0x3) == 0) {
      paramInt2 += byteArrayOfs;
      paramInt3 += paramInt2;
      while (paramInt2 < paramInt3) {
        unsafe.putInt(paramArrayOfbyte, paramInt2, Integer.reverseBytes(paramArrayOfint[paramInt1++]));
        paramInt2 += 4;
      } 
    } else {
      paramInt3 += paramInt2;
      while (paramInt2 < paramInt3) {
        int i = paramArrayOfint[paramInt1++];
        paramArrayOfbyte[paramInt2++] = (byte)i;
        paramArrayOfbyte[paramInt2++] = (byte)(i >> 8);
        paramArrayOfbyte[paramInt2++] = (byte)(i >> 16);
        paramArrayOfbyte[paramInt2++] = (byte)(i >> 24);
      } 
    } 
  }
  
  static void i2bLittle4(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    if (paramInt2 < 0 || paramArrayOfbyte.length - paramInt2 < 4)
      throw new ArrayIndexOutOfBoundsException(); 
    if (littleEndianUnaligned) {
      unsafe.putInt(paramArrayOfbyte, (byteArrayOfs + paramInt2), paramInt1);
    } else if (bigEndian && (paramInt2 & 0x3) == 0) {
      unsafe.putInt(paramArrayOfbyte, (byteArrayOfs + paramInt2), Integer.reverseBytes(paramInt1));
    } else {
      paramArrayOfbyte[paramInt2] = (byte)paramInt1;
      paramArrayOfbyte[paramInt2 + 1] = (byte)(paramInt1 >> 8);
      paramArrayOfbyte[paramInt2 + 2] = (byte)(paramInt1 >> 16);
      paramArrayOfbyte[paramInt2 + 3] = (byte)(paramInt1 >> 24);
    } 
  }
  
  static void b2iBig(byte[] paramArrayOfbyte, int paramInt1, int[] paramArrayOfint, int paramInt2, int paramInt3) {
    if (paramInt1 < 0 || paramArrayOfbyte.length - paramInt1 < paramInt3 || paramInt2 < 0 || paramArrayOfint.length - paramInt2 < paramInt3 / 4)
      throw new ArrayIndexOutOfBoundsException(); 
    if (littleEndianUnaligned) {
      paramInt1 += byteArrayOfs;
      paramInt3 += paramInt1;
      while (paramInt1 < paramInt3) {
        paramArrayOfint[paramInt2++] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, paramInt1));
        paramInt1 += 4;
      } 
    } else if (bigEndian && (paramInt1 & 0x3) == 0) {
      paramInt1 += byteArrayOfs;
      paramInt3 += paramInt1;
      while (paramInt1 < paramInt3) {
        paramArrayOfint[paramInt2++] = unsafe.getInt(paramArrayOfbyte, paramInt1);
        paramInt1 += 4;
      } 
    } else {
      paramInt3 += paramInt1;
      while (paramInt1 < paramInt3) {
        paramArrayOfint[paramInt2++] = paramArrayOfbyte[paramInt1 + 3] & 0xFF | (paramArrayOfbyte[paramInt1 + 2] & 0xFF) << 8 | (paramArrayOfbyte[paramInt1 + 1] & 0xFF) << 16 | paramArrayOfbyte[paramInt1] << 24;
        paramInt1 += 4;
      } 
    } 
  }
  
  static void b2iBig64(byte[] paramArrayOfbyte, int paramInt, int[] paramArrayOfint) {
    if (paramInt < 0 || paramArrayOfbyte.length - paramInt < 64 || paramArrayOfint.length < 16)
      throw new ArrayIndexOutOfBoundsException(); 
    if (littleEndianUnaligned) {
      paramInt += byteArrayOfs;
      paramArrayOfint[0] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, paramInt));
      paramArrayOfint[1] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 4)));
      paramArrayOfint[2] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 8)));
      paramArrayOfint[3] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 12)));
      paramArrayOfint[4] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 16)));
      paramArrayOfint[5] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 20)));
      paramArrayOfint[6] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 24)));
      paramArrayOfint[7] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 28)));
      paramArrayOfint[8] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 32)));
      paramArrayOfint[9] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 36)));
      paramArrayOfint[10] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 40)));
      paramArrayOfint[11] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 44)));
      paramArrayOfint[12] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 48)));
      paramArrayOfint[13] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 52)));
      paramArrayOfint[14] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 56)));
      paramArrayOfint[15] = Integer.reverseBytes(unsafe.getInt(paramArrayOfbyte, (paramInt + 60)));
    } else if (bigEndian && (paramInt & 0x3) == 0) {
      paramInt += byteArrayOfs;
      paramArrayOfint[0] = unsafe.getInt(paramArrayOfbyte, paramInt);
      paramArrayOfint[1] = unsafe.getInt(paramArrayOfbyte, (paramInt + 4));
      paramArrayOfint[2] = unsafe.getInt(paramArrayOfbyte, (paramInt + 8));
      paramArrayOfint[3] = unsafe.getInt(paramArrayOfbyte, (paramInt + 12));
      paramArrayOfint[4] = unsafe.getInt(paramArrayOfbyte, (paramInt + 16));
      paramArrayOfint[5] = unsafe.getInt(paramArrayOfbyte, (paramInt + 20));
      paramArrayOfint[6] = unsafe.getInt(paramArrayOfbyte, (paramInt + 24));
      paramArrayOfint[7] = unsafe.getInt(paramArrayOfbyte, (paramInt + 28));
      paramArrayOfint[8] = unsafe.getInt(paramArrayOfbyte, (paramInt + 32));
      paramArrayOfint[9] = unsafe.getInt(paramArrayOfbyte, (paramInt + 36));
      paramArrayOfint[10] = unsafe.getInt(paramArrayOfbyte, (paramInt + 40));
      paramArrayOfint[11] = unsafe.getInt(paramArrayOfbyte, (paramInt + 44));
      paramArrayOfint[12] = unsafe.getInt(paramArrayOfbyte, (paramInt + 48));
      paramArrayOfint[13] = unsafe.getInt(paramArrayOfbyte, (paramInt + 52));
      paramArrayOfint[14] = unsafe.getInt(paramArrayOfbyte, (paramInt + 56));
      paramArrayOfint[15] = unsafe.getInt(paramArrayOfbyte, (paramInt + 60));
    } else {
      b2iBig(paramArrayOfbyte, paramInt, paramArrayOfint, 0, 64);
    } 
  }
  
  static void i2bBig(int[] paramArrayOfint, int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3) {
    if (paramInt1 < 0 || paramArrayOfint.length - paramInt1 < paramInt3 / 4 || paramInt2 < 0 || paramArrayOfbyte.length - paramInt2 < paramInt3)
      throw new ArrayIndexOutOfBoundsException(); 
    if (littleEndianUnaligned) {
      paramInt2 += byteArrayOfs;
      paramInt3 += paramInt2;
      while (paramInt2 < paramInt3) {
        unsafe.putInt(paramArrayOfbyte, paramInt2, Integer.reverseBytes(paramArrayOfint[paramInt1++]));
        paramInt2 += 4;
      } 
    } else if (bigEndian && (paramInt2 & 0x3) == 0) {
      paramInt2 += byteArrayOfs;
      paramInt3 += paramInt2;
      while (paramInt2 < paramInt3) {
        unsafe.putInt(paramArrayOfbyte, paramInt2, paramArrayOfint[paramInt1++]);
        paramInt2 += 4;
      } 
    } else {
      paramInt3 += paramInt2;
      while (paramInt2 < paramInt3) {
        int i = paramArrayOfint[paramInt1++];
        paramArrayOfbyte[paramInt2++] = (byte)(i >> 24);
        paramArrayOfbyte[paramInt2++] = (byte)(i >> 16);
        paramArrayOfbyte[paramInt2++] = (byte)(i >> 8);
        paramArrayOfbyte[paramInt2++] = (byte)i;
      } 
    } 
  }
  
  static void i2bBig4(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
    if (paramInt2 < 0 || paramArrayOfbyte.length - paramInt2 < 4)
      throw new ArrayIndexOutOfBoundsException(); 
    if (littleEndianUnaligned) {
      unsafe.putInt(paramArrayOfbyte, (byteArrayOfs + paramInt2), Integer.reverseBytes(paramInt1));
    } else if (bigEndian && (paramInt2 & 0x3) == 0) {
      unsafe.putInt(paramArrayOfbyte, (byteArrayOfs + paramInt2), paramInt1);
    } else {
      paramArrayOfbyte[paramInt2] = (byte)(paramInt1 >> 24);
      paramArrayOfbyte[paramInt2 + 1] = (byte)(paramInt1 >> 16);
      paramArrayOfbyte[paramInt2 + 2] = (byte)(paramInt1 >> 8);
      paramArrayOfbyte[paramInt2 + 3] = (byte)paramInt1;
    } 
  }
  
  static void b2lBig(byte[] paramArrayOfbyte, int paramInt1, long[] paramArrayOflong, int paramInt2, int paramInt3) {
    if (paramInt1 < 0 || paramArrayOfbyte.length - paramInt1 < paramInt3 || paramInt2 < 0 || paramArrayOflong.length - paramInt2 < paramInt3 / 8)
      throw new ArrayIndexOutOfBoundsException(); 
    if (littleEndianUnaligned) {
      paramInt1 += byteArrayOfs;
      paramInt3 += paramInt1;
      while (paramInt1 < paramInt3) {
        paramArrayOflong[paramInt2++] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, paramInt1));
        paramInt1 += 8;
      } 
    } else if (bigEndian && (paramInt1 & 0x3) == 0) {
      paramInt1 += byteArrayOfs;
      paramInt3 += paramInt1;
      while (paramInt1 < paramInt3) {
        paramArrayOflong[paramInt2++] = unsafe
          .getInt(paramArrayOfbyte, paramInt1) << 32L | unsafe
          .getInt(paramArrayOfbyte, (paramInt1 + 4)) & 0xFFFFFFFFL;
        paramInt1 += 8;
      } 
    } else {
      paramInt3 += paramInt1;
      while (paramInt1 < paramInt3) {
        int i = paramArrayOfbyte[paramInt1 + 3] & 0xFF | (paramArrayOfbyte[paramInt1 + 2] & 0xFF) << 8 | (paramArrayOfbyte[paramInt1 + 1] & 0xFF) << 16 | paramArrayOfbyte[paramInt1] << 24;
        paramInt1 += 4;
        int j = paramArrayOfbyte[paramInt1 + 3] & 0xFF | (paramArrayOfbyte[paramInt1 + 2] & 0xFF) << 8 | (paramArrayOfbyte[paramInt1 + 1] & 0xFF) << 16 | paramArrayOfbyte[paramInt1] << 24;
        paramArrayOflong[paramInt2++] = i << 32L | j & 0xFFFFFFFFL;
        paramInt1 += 4;
      } 
    } 
  }
  
  static void b2lBig128(byte[] paramArrayOfbyte, int paramInt, long[] paramArrayOflong) {
    if (paramInt < 0 || paramArrayOfbyte.length - paramInt < 128 || paramArrayOflong.length < 16)
      throw new ArrayIndexOutOfBoundsException(); 
    if (littleEndianUnaligned) {
      paramInt += byteArrayOfs;
      paramArrayOflong[0] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, paramInt));
      paramArrayOflong[1] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, (paramInt + 8)));
      paramArrayOflong[2] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, (paramInt + 16)));
      paramArrayOflong[3] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, (paramInt + 24)));
      paramArrayOflong[4] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, (paramInt + 32)));
      paramArrayOflong[5] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, (paramInt + 40)));
      paramArrayOflong[6] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, (paramInt + 48)));
      paramArrayOflong[7] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, (paramInt + 56)));
      paramArrayOflong[8] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, (paramInt + 64)));
      paramArrayOflong[9] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, (paramInt + 72)));
      paramArrayOflong[10] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, (paramInt + 80)));
      paramArrayOflong[11] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, (paramInt + 88)));
      paramArrayOflong[12] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, (paramInt + 96)));
      paramArrayOflong[13] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, (paramInt + 104)));
      paramArrayOflong[14] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, (paramInt + 112)));
      paramArrayOflong[15] = Long.reverseBytes(unsafe.getLong(paramArrayOfbyte, (paramInt + 120)));
    } else {
      b2lBig(paramArrayOfbyte, paramInt, paramArrayOflong, 0, 128);
    } 
  }
  
  static void l2bBig(long[] paramArrayOflong, int paramInt1, byte[] paramArrayOfbyte, int paramInt2, int paramInt3) {
    if (paramInt1 < 0 || paramArrayOflong.length - paramInt1 < paramInt3 / 8 || paramInt2 < 0 || paramArrayOfbyte.length - paramInt2 < paramInt3)
      throw new ArrayIndexOutOfBoundsException(); 
    paramInt3 += paramInt2;
    while (paramInt2 < paramInt3) {
      long l = paramArrayOflong[paramInt1++];
      paramArrayOfbyte[paramInt2++] = (byte)(int)(l >> 56L);
      paramArrayOfbyte[paramInt2++] = (byte)(int)(l >> 48L);
      paramArrayOfbyte[paramInt2++] = (byte)(int)(l >> 40L);
      paramArrayOfbyte[paramInt2++] = (byte)(int)(l >> 32L);
      paramArrayOfbyte[paramInt2++] = (byte)(int)(l >> 24L);
      paramArrayOfbyte[paramInt2++] = (byte)(int)(l >> 16L);
      paramArrayOfbyte[paramInt2++] = (byte)(int)(l >> 8L);
      paramArrayOfbyte[paramInt2++] = (byte)(int)l;
    } 
  }
}
