package sun.security.ssl;

import java.io.IOException;

abstract class HelloExtension {
  final ExtensionType type;
  
  HelloExtension(ExtensionType paramExtensionType) {
    this.type = paramExtensionType;
  }
  
  abstract int length();
  
  abstract void send(HandshakeOutStream paramHandshakeOutStream) throws IOException;
  
  public abstract String toString();
}
