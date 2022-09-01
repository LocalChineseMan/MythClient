package java.net;

import java.io.IOException;

public class SocketException extends IOException {
  private static final long serialVersionUID = -5935874303556886934L;
  
  public SocketException(String paramString) {
    super(paramString);
  }
  
  public SocketException() {}
}
