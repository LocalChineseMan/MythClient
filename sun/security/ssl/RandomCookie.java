package sun.security.ssl;

import java.io.IOException;
import java.io.PrintStream;
import java.security.SecureRandom;

final class RandomCookie {
  byte[] random_bytes;
  
  RandomCookie(SecureRandom paramSecureRandom) {
    int i;
    long l = System.currentTimeMillis() / 1000L;
    if (l < 2147483647L) {
      i = (int)l;
    } else {
      i = Integer.MAX_VALUE;
    } 
    this.random_bytes = new byte[32];
    paramSecureRandom.nextBytes(this.random_bytes);
    this.random_bytes[0] = (byte)(i >> 24);
    this.random_bytes[1] = (byte)(i >> 16);
    this.random_bytes[2] = (byte)(i >> 8);
    this.random_bytes[3] = (byte)i;
  }
  
  RandomCookie(HandshakeInStream paramHandshakeInStream) throws IOException {
    this.random_bytes = new byte[32];
    paramHandshakeInStream.read(this.random_bytes, 0, 32);
  }
  
  void send(HandshakeOutStream paramHandshakeOutStream) throws IOException {
    paramHandshakeOutStream.write(this.random_bytes, 0, 32);
  }
  
  void print(PrintStream paramPrintStream) {
    int i = this.random_bytes[0] << 24;
    i += this.random_bytes[1] << 16;
    i += this.random_bytes[2] << 8;
    i += this.random_bytes[3];
    paramPrintStream.print("GMT: " + i + " ");
    paramPrintStream.print("bytes = { ");
    for (byte b = 4; b < 32; b++) {
      if (b != 4)
        paramPrintStream.print(", "); 
      paramPrintStream.print(this.random_bytes[b] & 0xFF);
    } 
    paramPrintStream.println(" }");
  }
}
