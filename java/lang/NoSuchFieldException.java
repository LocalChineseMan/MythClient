package java.lang;

public class NoSuchFieldException extends ReflectiveOperationException {
  private static final long serialVersionUID = -6143714805279938260L;
  
  public NoSuchFieldException() {}
  
  public NoSuchFieldException(String paramString) {
    super(paramString);
  }
}
