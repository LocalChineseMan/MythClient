package sun.security.ssl;

import java.util.Arrays;

class Authenticator {
  private final byte[] block;
  
  private static final int BLOCK_SIZE_SSL = 11;
  
  private static final int BLOCK_SIZE_TLS = 13;
  
  Authenticator() {
    this.block = new byte[0];
  }
  
  Authenticator(ProtocolVersion paramProtocolVersion) {
    if (paramProtocolVersion.v >= ProtocolVersion.TLS10.v) {
      this.block = new byte[13];
      this.block[9] = paramProtocolVersion.major;
      this.block[10] = paramProtocolVersion.minor;
    } else {
      this.block = new byte[11];
    } 
  }
  
  final boolean seqNumOverflow() {
    return (this.block.length != 0 && this.block[0] == -1 && this.block[1] == -1 && this.block[2] == -1 && this.block[3] == -1 && this.block[4] == -1 && this.block[5] == -1 && this.block[6] == -1);
  }
  
  final boolean seqNumIsHuge() {
    return (this.block.length != 0 && this.block[0] == -1 && this.block[1] == -1);
  }
  
  final byte[] sequenceNumber() {
    return Arrays.copyOf(this.block, 8);
  }
  
  final byte[] acquireAuthenticationBytes(byte paramByte, int paramInt) {
    byte[] arrayOfByte = (byte[])this.block.clone();
    if (this.block.length != 0) {
      arrayOfByte[8] = paramByte;
      arrayOfByte[arrayOfByte.length - 2] = (byte)(paramInt >> 8);
      arrayOfByte[arrayOfByte.length - 1] = (byte)paramInt;
      byte b = 7;
      for (this.block[b] = (byte)(this.block[b] + 1); b >= 0 && (byte)(this.block[b] + 1) == 0;)
        b--; 
    } 
    return arrayOfByte;
  }
}
