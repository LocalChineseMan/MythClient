package sun.security.ssl;

import java.io.IOException;
import java.io.OutputStream;

public class HandshakeOutStream extends OutputStream {
  private SSLSocketImpl socket;
  
  private SSLEngineImpl engine;
  
  OutputRecord r;
  
  HandshakeOutStream(ProtocolVersion paramProtocolVersion1, ProtocolVersion paramProtocolVersion2, HandshakeHash paramHandshakeHash, SSLSocketImpl paramSSLSocketImpl) {
    this.socket = paramSSLSocketImpl;
    this.r = new OutputRecord((byte)22);
    init(paramProtocolVersion1, paramProtocolVersion2, paramHandshakeHash);
  }
  
  HandshakeOutStream(ProtocolVersion paramProtocolVersion1, ProtocolVersion paramProtocolVersion2, HandshakeHash paramHandshakeHash, SSLEngineImpl paramSSLEngineImpl) {
    this.engine = paramSSLEngineImpl;
    this.r = (OutputRecord)new EngineOutputRecord((byte)22, paramSSLEngineImpl);
    init(paramProtocolVersion1, paramProtocolVersion2, paramHandshakeHash);
  }
  
  private void init(ProtocolVersion paramProtocolVersion1, ProtocolVersion paramProtocolVersion2, HandshakeHash paramHandshakeHash) {
    this.r.setVersion(paramProtocolVersion1);
    this.r.setHelloVersion(paramProtocolVersion2);
    this.r.setHandshakeHash(paramHandshakeHash);
  }
  
  void doHashes() {
    this.r.doHashes();
  }
  
  public void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    while (paramInt2 > 0) {
      int i = Math.min(paramInt2, this.r.availableDataBytes());
      if (i == 0) {
        flush();
        continue;
      } 
      this.r.write(paramArrayOfbyte, paramInt1, i);
      paramInt1 += i;
      paramInt2 -= i;
    } 
  }
  
  public void write(int paramInt) throws IOException {
    if (this.r.availableDataBytes() < 1)
      flush(); 
    this.r.write(paramInt);
  }
  
  public void flush() throws IOException {
    if (this.socket != null) {
      try {
        this.socket.writeRecord(this.r);
      } catch (IOException iOException) {
        this.socket.waitForClose(true);
        throw iOException;
      } 
    } else {
      this.engine.writeRecord((EngineOutputRecord)this.r);
    } 
  }
  
  void setFinishedMsg() {
    assert this.socket == null;
    ((EngineOutputRecord)this.r).setFinishedMsg();
  }
  
  void putInt8(int paramInt) throws IOException {
    checkOverflow(paramInt, 256);
    this.r.write(paramInt);
  }
  
  void putInt16(int paramInt) throws IOException {
    checkOverflow(paramInt, 65536);
    if (this.r.availableDataBytes() < 2)
      flush(); 
    this.r.write(paramInt >> 8);
    this.r.write(paramInt);
  }
  
  void putInt24(int paramInt) throws IOException {
    checkOverflow(paramInt, 16777216);
    if (this.r.availableDataBytes() < 3)
      flush(); 
    this.r.write(paramInt >> 16);
    this.r.write(paramInt >> 8);
    this.r.write(paramInt);
  }
  
  void putInt32(int paramInt) throws IOException {
    if (this.r.availableDataBytes() < 4)
      flush(); 
    this.r.write(paramInt >> 24);
    this.r.write(paramInt >> 16);
    this.r.write(paramInt >> 8);
    this.r.write(paramInt);
  }
  
  void putBytes8(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null) {
      putInt8(0);
      return;
    } 
    checkOverflow(paramArrayOfbyte.length, 256);
    putInt8(paramArrayOfbyte.length);
    write(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  public void putBytes16(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null) {
      putInt16(0);
      return;
    } 
    checkOverflow(paramArrayOfbyte.length, 65536);
    putInt16(paramArrayOfbyte.length);
    write(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  void putBytes24(byte[] paramArrayOfbyte) throws IOException {
    if (paramArrayOfbyte == null) {
      putInt24(0);
      return;
    } 
    checkOverflow(paramArrayOfbyte.length, 16777216);
    putInt24(paramArrayOfbyte.length);
    write(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  private void checkOverflow(int paramInt1, int paramInt2) {
    if (paramInt1 >= paramInt2)
      throw new RuntimeException("Field length overflow, the field length (" + paramInt1 + ") should be less than " + paramInt2); 
  }
}
