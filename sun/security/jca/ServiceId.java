package sun.security.jca;

public final class ServiceId {
  public final String type;
  
  public final String algorithm;
  
  public ServiceId(String paramString1, String paramString2) {
    this.type = paramString1;
    this.algorithm = paramString2;
  }
}
