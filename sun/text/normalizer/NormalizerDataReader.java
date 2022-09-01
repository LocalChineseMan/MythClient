package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class NormalizerDataReader implements ICUBinary.Authenticate {
  private DataInputStream dataInputStream;
  
  private byte[] unicodeVersion;
  
  protected NormalizerDataReader(InputStream paramInputStream) throws IOException {
    this.unicodeVersion = ICUBinary.readHeader(paramInputStream, DATA_FORMAT_ID, this);
    this.dataInputStream = new DataInputStream(paramInputStream);
  }
  
  protected int[] readIndexes(int paramInt) throws IOException {
    int[] arrayOfInt = new int[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfInt[b] = this.dataInputStream.readInt(); 
    return arrayOfInt;
  }
  
  protected void read(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, char[] paramArrayOfchar1, char[] paramArrayOfchar2) throws IOException {
    this.dataInputStream.readFully(paramArrayOfbyte1);
    byte b;
    for (b = 0; b < paramArrayOfchar1.length; b++)
      paramArrayOfchar1[b] = this.dataInputStream.readChar(); 
    for (b = 0; b < paramArrayOfchar2.length; b++)
      paramArrayOfchar2[b] = this.dataInputStream.readChar(); 
    this.dataInputStream.readFully(paramArrayOfbyte2);
    this.dataInputStream.readFully(paramArrayOfbyte3);
  }
  
  public byte[] getDataFormatVersion() {
    return DATA_FORMAT_VERSION;
  }
  
  public boolean isDataVersionAcceptable(byte[] paramArrayOfbyte) {
    return (paramArrayOfbyte[0] == DATA_FORMAT_VERSION[0] && paramArrayOfbyte[2] == DATA_FORMAT_VERSION[2] && paramArrayOfbyte[3] == DATA_FORMAT_VERSION[3]);
  }
  
  public byte[] getUnicodeVersion() {
    return this.unicodeVersion;
  }
  
  private static final byte[] DATA_FORMAT_ID = new byte[] { 78, 111, 114, 109 };
  
  private static final byte[] DATA_FORMAT_VERSION = new byte[] { 2, 2, 5, 2 };
}
