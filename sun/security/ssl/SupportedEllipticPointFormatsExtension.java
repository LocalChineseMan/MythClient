package sun.security.ssl;

import java.io.IOException;
import java.util.ArrayList;
import javax.net.ssl.SSLProtocolException;

final class SupportedEllipticPointFormatsExtension extends HelloExtension {
  static final int FMT_UNCOMPRESSED = 0;
  
  static final int FMT_ANSIX962_COMPRESSED_PRIME = 1;
  
  static final int FMT_ANSIX962_COMPRESSED_CHAR2 = 2;
  
  static final HelloExtension DEFAULT = new SupportedEllipticPointFormatsExtension(new byte[] { 0 });
  
  private final byte[] formats;
  
  private SupportedEllipticPointFormatsExtension(byte[] paramArrayOfbyte) {
    super(ExtensionType.EXT_EC_POINT_FORMATS);
    this.formats = paramArrayOfbyte;
  }
  
  SupportedEllipticPointFormatsExtension(HandshakeInStream paramHandshakeInStream, int paramInt) throws IOException {
    super(ExtensionType.EXT_EC_POINT_FORMATS);
    this.formats = paramHandshakeInStream.getBytes8();
    boolean bool = false;
    for (byte b : this.formats) {
      if (b == 0) {
        bool = true;
        break;
      } 
    } 
    if (!bool)
      throw new SSLProtocolException("Peer does not support uncompressed points"); 
  }
  
  int length() {
    return 5 + this.formats.length;
  }
  
  void send(HandshakeOutStream paramHandshakeOutStream) throws IOException {
    paramHandshakeOutStream.putInt16(this.type.id);
    paramHandshakeOutStream.putInt16(this.formats.length + 1);
    paramHandshakeOutStream.putBytes8(this.formats);
  }
  
  private static String toString(byte paramByte) {
    int i = paramByte & 0xFF;
    switch (i) {
      case 0:
        return "uncompressed";
      case 1:
        return "ansiX962_compressed_prime";
      case 2:
        return "ansiX962_compressed_char2";
    } 
    return "unknown-" + i;
  }
  
  public String toString() {
    ArrayList<String> arrayList = new ArrayList();
    for (byte b : this.formats)
      arrayList.add(toString(b)); 
    return "Extension " + this.type + ", formats: " + arrayList;
  }
}
