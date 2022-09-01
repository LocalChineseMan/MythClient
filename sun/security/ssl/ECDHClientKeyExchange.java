package sun.security.ssl;

import java.io.IOException;
import java.io.PrintStream;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;

final class ECDHClientKeyExchange extends HandshakeMessage {
  private byte[] encodedPoint;
  
  int messageType() {
    return 16;
  }
  
  byte[] getEncodedPoint() {
    return this.encodedPoint;
  }
  
  ECDHClientKeyExchange(PublicKey paramPublicKey) {
    ECPublicKey eCPublicKey = (ECPublicKey)paramPublicKey;
    ECPoint eCPoint = eCPublicKey.getW();
    ECParameterSpec eCParameterSpec = eCPublicKey.getParams();
    this.encodedPoint = JsseJce.encodePoint(eCPoint, eCParameterSpec.getCurve());
  }
  
  ECDHClientKeyExchange(HandshakeInStream paramHandshakeInStream) throws IOException {
    this.encodedPoint = paramHandshakeInStream.getBytes8();
  }
  
  int messageLength() {
    return this.encodedPoint.length + 1;
  }
  
  void send(HandshakeOutStream paramHandshakeOutStream) throws IOException {
    paramHandshakeOutStream.putBytes8(this.encodedPoint);
  }
  
  void print(PrintStream paramPrintStream) throws IOException {
    paramPrintStream.println("*** ECDHClientKeyExchange");
    if (debug != null && Debug.isOn("verbose"))
      Debug.println(paramPrintStream, "ECDH Public value", this.encodedPoint); 
  }
}
