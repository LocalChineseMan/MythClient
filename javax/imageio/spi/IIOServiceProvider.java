package javax.imageio.spi;

import java.util.Locale;

public abstract class IIOServiceProvider implements RegisterableService {
  protected String vendorName;
  
  protected String version;
  
  public IIOServiceProvider(String paramString1, String paramString2) {
    if (paramString1 == null)
      throw new IllegalArgumentException("vendorName == null!"); 
    if (paramString2 == null)
      throw new IllegalArgumentException("version == null!"); 
    this.vendorName = paramString1;
    this.version = paramString2;
  }
  
  public IIOServiceProvider() {}
  
  public void onRegistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass) {}
  
  public void onDeregistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass) {}
  
  public String getVendorName() {
    return this.vendorName;
  }
  
  public String getVersion() {
    return this.version;
  }
  
  public abstract String getDescription(Locale paramLocale);
}
