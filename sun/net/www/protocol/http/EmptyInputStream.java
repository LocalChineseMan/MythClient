package sun.net.www.protocol.http;

import java.io.InputStream;

class EmptyInputStream extends InputStream {
  public int available() {
    return 0;
  }
  
  public int read() {
    return -1;
  }
}
