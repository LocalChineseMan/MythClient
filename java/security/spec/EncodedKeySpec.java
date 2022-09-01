package java.security.spec;

public abstract class EncodedKeySpec implements KeySpec {
  private byte[] encodedKey;
  
  public EncodedKeySpec(byte[] paramArrayOfbyte) {
    this.encodedKey = (byte[])paramArrayOfbyte.clone();
  }
  
  public byte[] getEncoded() {
    return (byte[])this.encodedKey.clone();
  }
  
  public abstract String getFormat();
}
