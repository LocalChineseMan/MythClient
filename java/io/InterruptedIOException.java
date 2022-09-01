package java.io;

public class InterruptedIOException extends IOException {
  private static final long serialVersionUID = 4020568460727500567L;
  
  public InterruptedIOException() {}
  
  public InterruptedIOException(String paramString) {
    super(paramString);
  }
  
  public int bytesTransferred = 0;
}
