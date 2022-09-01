package sun.security.ssl;

import java.io.IOException;
import java.io.InputStream;
import javax.net.ssl.SSLException;

public class HandshakeInStream extends InputStream {
  InputRecord r;
  
  HandshakeInStream(HandshakeHash paramHandshakeHash) {
    this.r = new InputRecord();
    this.r.setHandshakeHash(paramHandshakeHash);
  }
  
  public int available() {
    return this.r.available();
  }
  
  public int read() throws IOException {
    int i = this.r.read();
    if (i == -1)
      throw new SSLException("Unexpected end of handshake data"); 
    return i;
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    int i = this.r.read(paramArrayOfbyte, paramInt1, paramInt2);
    if (i != paramInt2)
      throw new SSLException("Unexpected end of handshake data"); 
    return i;
  }
  
  public long skip(long paramLong) throws IOException {
    return this.r.skip(paramLong);
  }
  
  public void mark(int paramInt) {
    this.r.mark(paramInt);
  }
  
  public void reset() throws IOException {
    this.r.reset();
  }
  
  public boolean markSupported() {
    return true;
  }
  
  void incomingRecord(InputRecord paramInputRecord) throws IOException {
    this.r.queueHandshake(paramInputRecord);
  }
  
  void digestNow() {
    this.r.doHashes();
  }
  
  void ignore(int paramInt) {
    this.r.ignore(paramInt);
  }
  
  int getInt8() throws IOException {
    return read();
  }
  
  int getInt16() throws IOException {
    return getInt8() << 8 | getInt8();
  }
  
  int getInt24() throws IOException {
    return getInt8() << 16 | getInt8() << 8 | getInt8();
  }
  
  int getInt32() throws IOException {
    return getInt8() << 24 | getInt8() << 16 | getInt8() << 8 | getInt8();
  }
  
  byte[] getBytes8() throws IOException {
    int i = getInt8();
    verifyLength(i);
    byte[] arrayOfByte = new byte[i];
    read(arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  public byte[] getBytes16() throws IOException {
    int i = getInt16();
    verifyLength(i);
    byte[] arrayOfByte = new byte[i];
    read(arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  byte[] getBytes24() throws IOException {
    int i = getInt24();
    verifyLength(i);
    byte[] arrayOfByte = new byte[i];
    read(arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  private void verifyLength(int paramInt) throws SSLException {
    if (paramInt > available())
      throw new SSLException("Not enough data to fill declared vector size"); 
  }
}
