package sun.security.ssl;

import java.io.IOException;
import javax.net.ssl.SSLProtocolException;

final class RenegotiationInfoExtension extends HelloExtension {
  private final byte[] renegotiated_connection;
  
  RenegotiationInfoExtension(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    super(ExtensionType.EXT_RENEGOTIATION_INFO);
    if (paramArrayOfbyte1.length != 0) {
      this.renegotiated_connection = new byte[paramArrayOfbyte1.length + paramArrayOfbyte2.length];
      System.arraycopy(paramArrayOfbyte1, 0, this.renegotiated_connection, 0, paramArrayOfbyte1.length);
      if (paramArrayOfbyte2.length != 0)
        System.arraycopy(paramArrayOfbyte2, 0, this.renegotiated_connection, paramArrayOfbyte1.length, paramArrayOfbyte2.length); 
    } else {
      this.renegotiated_connection = new byte[0];
    } 
  }
  
  RenegotiationInfoExtension(HandshakeInStream paramHandshakeInStream, int paramInt) throws IOException {
    super(ExtensionType.EXT_RENEGOTIATION_INFO);
    if (paramInt < 1)
      throw new SSLProtocolException("Invalid " + this.type + " extension"); 
    int i = paramHandshakeInStream.getInt8();
    if (i + 1 != paramInt)
      throw new SSLProtocolException("Invalid " + this.type + " extension"); 
    this.renegotiated_connection = new byte[i];
    if (i != 0)
      paramHandshakeInStream.read(this.renegotiated_connection, 0, i); 
  }
  
  int length() {
    return 5 + this.renegotiated_connection.length;
  }
  
  void send(HandshakeOutStream paramHandshakeOutStream) throws IOException {
    paramHandshakeOutStream.putInt16(this.type.id);
    paramHandshakeOutStream.putInt16(this.renegotiated_connection.length + 1);
    paramHandshakeOutStream.putBytes8(this.renegotiated_connection);
  }
  
  boolean isEmpty() {
    return (this.renegotiated_connection.length == 0);
  }
  
  byte[] getRenegotiatedConnection() {
    return this.renegotiated_connection;
  }
  
  public String toString() {
    return "Extension " + this.type + ", renegotiated_connection: " + ((this.renegotiated_connection.length == 0) ? "<empty>" : 
      
      Debug.toString(this.renegotiated_connection));
  }
}
