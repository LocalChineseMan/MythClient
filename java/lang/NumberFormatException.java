package java.lang;

public class NumberFormatException extends IllegalArgumentException {
  static final long serialVersionUID = -2848938806368998894L;
  
  public NumberFormatException() {}
  
  public NumberFormatException(String paramString) {
    super(paramString);
  }
  
  static NumberFormatException forInputString(String paramString) {
    return new NumberFormatException("For input string: \"" + paramString + "\"");
  }
}
