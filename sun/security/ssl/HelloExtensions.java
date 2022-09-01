package sun.security.ssl;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLProtocolException;

final class HelloExtensions {
  private List<HelloExtension> extensions;
  
  private int encodedLength;
  
  HelloExtensions() {
    this.extensions = Collections.emptyList();
  }
  
  HelloExtensions(HandshakeInStream paramHandshakeInStream) throws IOException {
    int i = paramHandshakeInStream.getInt16();
    this.extensions = new ArrayList<>();
    this.encodedLength = i + 2;
    while (i > 0) {
      UnknownExtension unknownExtension;
      int j = paramHandshakeInStream.getInt16();
      int k = paramHandshakeInStream.getInt16();
      ExtensionType extensionType = ExtensionType.get(j);
      if (extensionType == ExtensionType.EXT_SERVER_NAME) {
        ServerNameExtension serverNameExtension = new ServerNameExtension(paramHandshakeInStream, k);
      } else if (extensionType == ExtensionType.EXT_SIGNATURE_ALGORITHMS) {
        SignatureAlgorithmsExtension signatureAlgorithmsExtension = new SignatureAlgorithmsExtension(paramHandshakeInStream, k);
      } else if (extensionType == ExtensionType.EXT_ELLIPTIC_CURVES) {
        SupportedEllipticCurvesExtension supportedEllipticCurvesExtension = new SupportedEllipticCurvesExtension(paramHandshakeInStream, k);
      } else if (extensionType == ExtensionType.EXT_EC_POINT_FORMATS) {
        SupportedEllipticPointFormatsExtension supportedEllipticPointFormatsExtension = new SupportedEllipticPointFormatsExtension(paramHandshakeInStream, k);
      } else if (extensionType == ExtensionType.EXT_RENEGOTIATION_INFO) {
        RenegotiationInfoExtension renegotiationInfoExtension = new RenegotiationInfoExtension(paramHandshakeInStream, k);
      } else {
        unknownExtension = new UnknownExtension(paramHandshakeInStream, k, extensionType);
      } 
      this.extensions.add(unknownExtension);
      i -= k + 4;
    } 
    if (i != 0)
      throw new SSLProtocolException("Error parsing extensions: extra data"); 
  }
  
  List<HelloExtension> list() {
    return this.extensions;
  }
  
  void add(HelloExtension paramHelloExtension) {
    if (this.extensions.isEmpty())
      this.extensions = new ArrayList<>(); 
    this.extensions.add(paramHelloExtension);
    this.encodedLength = -1;
  }
  
  HelloExtension get(ExtensionType paramExtensionType) {
    for (HelloExtension helloExtension : this.extensions) {
      if (helloExtension.type == paramExtensionType)
        return helloExtension; 
    } 
    return null;
  }
  
  int length() {
    if (this.encodedLength >= 0)
      return this.encodedLength; 
    if (this.extensions.isEmpty()) {
      this.encodedLength = 0;
    } else {
      this.encodedLength = 2;
      for (HelloExtension helloExtension : this.extensions)
        this.encodedLength += helloExtension.length(); 
    } 
    return this.encodedLength;
  }
  
  void send(HandshakeOutStream paramHandshakeOutStream) throws IOException {
    int i = length();
    if (i == 0)
      return; 
    paramHandshakeOutStream.putInt16(i - 2);
    for (HelloExtension helloExtension : this.extensions)
      helloExtension.send(paramHandshakeOutStream); 
  }
  
  void print(PrintStream paramPrintStream) throws IOException {
    for (HelloExtension helloExtension : this.extensions)
      paramPrintStream.println(helloExtension.toString()); 
  }
}
