package sun.java2d.cmm;

import java.io.IOException;
import java.io.InputStream;

public class ProfileDeferralInfo extends InputStream {
  public int colorSpaceType;
  
  public int numComponents;
  
  public int profileClass;
  
  public String filename;
  
  public ProfileDeferralInfo(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    this.filename = paramString;
    this.colorSpaceType = paramInt1;
    this.numComponents = paramInt2;
    this.profileClass = paramInt3;
  }
  
  public int read() throws IOException {
    return 0;
  }
}
