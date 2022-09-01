package java.lang;

public class ArrayIndexOutOfBoundsException extends IndexOutOfBoundsException {
  private static final long serialVersionUID = -5116101128118950844L;
  
  public ArrayIndexOutOfBoundsException() {}
  
  public ArrayIndexOutOfBoundsException(int paramInt) {
    super("Array index out of range: " + paramInt);
  }
  
  public ArrayIndexOutOfBoundsException(String paramString) {
    super(paramString);
  }
}
