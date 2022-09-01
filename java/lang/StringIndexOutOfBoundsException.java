package java.lang;

public class StringIndexOutOfBoundsException extends IndexOutOfBoundsException {
  private static final long serialVersionUID = -6762910422159637258L;
  
  public StringIndexOutOfBoundsException() {}
  
  public StringIndexOutOfBoundsException(String paramString) {
    super(paramString);
  }
  
  public StringIndexOutOfBoundsException(int paramInt) {
    super("String index out of range: " + paramInt);
  }
}
