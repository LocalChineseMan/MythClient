package sun.net.idn;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import sun.text.normalizer.ICUBinary;

final class StringPrepDataReader implements ICUBinary.Authenticate {
  private DataInputStream dataInputStream;
  
  private byte[] unicodeVersion;
  
  public StringPrepDataReader(InputStream paramInputStream) throws IOException {
    this.unicodeVersion = ICUBinary.readHeader(paramInputStream, DATA_FORMAT_ID, this);
    this.dataInputStream = new DataInputStream(paramInputStream);
  }
  
  public void read(byte[] paramArrayOfbyte, char[] paramArrayOfchar) throws IOException {
    this.dataInputStream.read(paramArrayOfbyte);
    for (byte b = 0; b < paramArrayOfchar.length; b++)
      paramArrayOfchar[b] = this.dataInputStream.readChar(); 
  }
  
  public byte[] getDataFormatVersion() {
    return DATA_FORMAT_VERSION;
  }
  
  public boolean isDataVersionAcceptable(byte[] paramArrayOfbyte) {
    return (paramArrayOfbyte[0] == DATA_FORMAT_VERSION[0] && paramArrayOfbyte[2] == DATA_FORMAT_VERSION[2] && paramArrayOfbyte[3] == DATA_FORMAT_VERSION[3]);
  }
  
  public int[] readIndexes(int paramInt) throws IOException {
    int[] arrayOfInt = new int[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfInt[b] = this.dataInputStream.readInt(); 
    return arrayOfInt;
  }
  
  public byte[] getUnicodeVersion() {
    return this.unicodeVersion;
  }
  
  private static final byte[] DATA_FORMAT_ID = new byte[] { 83, 80, 82, 80 };
  
  private static final byte[] DATA_FORMAT_VERSION = new byte[] { 3, 2, 5, 2 };
}
