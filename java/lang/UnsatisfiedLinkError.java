package java.lang;

public class UnsatisfiedLinkError extends LinkageError {
  private static final long serialVersionUID = -4019343241616879428L;
  
  public UnsatisfiedLinkError() {}
  
  public UnsatisfiedLinkError(String paramString) {
    super(paramString);
  }
}
