package sun.util.locale;

class Extension {
  private final char key;
  
  private String value;
  
  private String id;
  
  protected Extension(char paramChar) {
    this.key = paramChar;
  }
  
  Extension(char paramChar, String paramString) {
    this.key = paramChar;
    setValue(paramString);
  }
  
  protected void setValue(String paramString) {
    this.value = paramString;
    this.id = this.key + "-" + paramString;
  }
  
  public char getKey() {
    return this.key;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public String getID() {
    return this.id;
  }
  
  public String toString() {
    return getID();
  }
}
