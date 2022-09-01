package sun.security.x509;

public class X509AttributeName {
  private static final char SEPARATOR = '.';
  
  private String prefix = null;
  
  private String suffix = null;
  
  public X509AttributeName(String paramString) {
    int i = paramString.indexOf('.');
    if (i == -1) {
      this.prefix = paramString;
    } else {
      this.prefix = paramString.substring(0, i);
      this.suffix = paramString.substring(i + 1);
    } 
  }
  
  public String getPrefix() {
    return this.prefix;
  }
  
  public String getSuffix() {
    return this.suffix;
  }
}
