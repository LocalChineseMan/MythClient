package javax.crypto;

public class AEADBadTagException extends BadPaddingException {
  private static final long serialVersionUID = -488059093241685509L;
  
  public AEADBadTagException() {}
  
  public AEADBadTagException(String paramString) {
    super(paramString);
  }
}
