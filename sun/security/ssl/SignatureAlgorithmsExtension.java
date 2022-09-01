package sun.security.ssl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.net.ssl.SSLProtocolException;

final class SignatureAlgorithmsExtension extends HelloExtension {
  private Collection<SignatureAndHashAlgorithm> algorithms;
  
  private int algorithmsLen;
  
  SignatureAlgorithmsExtension(Collection<SignatureAndHashAlgorithm> paramCollection) {
    super(ExtensionType.EXT_SIGNATURE_ALGORITHMS);
    this.algorithms = new ArrayList<>(paramCollection);
    this
      .algorithmsLen = SignatureAndHashAlgorithm.sizeInRecord() * this.algorithms.size();
  }
  
  SignatureAlgorithmsExtension(HandshakeInStream paramHandshakeInStream, int paramInt) throws IOException {
    super(ExtensionType.EXT_SIGNATURE_ALGORITHMS);
    this.algorithmsLen = paramHandshakeInStream.getInt16();
    if (this.algorithmsLen == 0 || this.algorithmsLen + 2 != paramInt)
      throw new SSLProtocolException("Invalid " + this.type + " extension"); 
    this.algorithms = new ArrayList<>();
    int i = this.algorithmsLen;
    byte b = 0;
    while (i > 1) {
      int j = paramHandshakeInStream.getInt8();
      int k = paramHandshakeInStream.getInt8();
      SignatureAndHashAlgorithm signatureAndHashAlgorithm = SignatureAndHashAlgorithm.valueOf(j, k, ++b);
      this.algorithms.add(signatureAndHashAlgorithm);
      i -= 2;
    } 
    if (i != 0)
      throw new SSLProtocolException("Invalid server_name extension"); 
  }
  
  Collection<SignatureAndHashAlgorithm> getSignAlgorithms() {
    return this.algorithms;
  }
  
  int length() {
    return 6 + this.algorithmsLen;
  }
  
  void send(HandshakeOutStream paramHandshakeOutStream) throws IOException {
    paramHandshakeOutStream.putInt16(this.type.id);
    paramHandshakeOutStream.putInt16(this.algorithmsLen + 2);
    paramHandshakeOutStream.putInt16(this.algorithmsLen);
    for (SignatureAndHashAlgorithm signatureAndHashAlgorithm : this.algorithms) {
      paramHandshakeOutStream.putInt8(signatureAndHashAlgorithm.getHashValue());
      paramHandshakeOutStream.putInt8(signatureAndHashAlgorithm.getSignatureValue());
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    boolean bool = false;
    for (SignatureAndHashAlgorithm signatureAndHashAlgorithm : this.algorithms) {
      if (bool) {
        stringBuffer.append(", " + signatureAndHashAlgorithm.getAlgorithmName());
        continue;
      } 
      stringBuffer.append(signatureAndHashAlgorithm.getAlgorithmName());
      bool = true;
    } 
    return "Extension " + this.type + ", signature_algorithms: " + stringBuffer;
  }
}
