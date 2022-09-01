package com.sun.imageio.plugins.png;

import com.sun.imageio.plugins.common.InputStreamAdapter;
import com.sun.imageio.plugins.common.SubImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import javax.imageio.stream.ImageInputStream;

class PNGImageDataEnumeration implements Enumeration<InputStream> {
  boolean firstTime = true;
  
  ImageInputStream stream;
  
  int length;
  
  public PNGImageDataEnumeration(ImageInputStream paramImageInputStream) throws IOException {
    this.stream = paramImageInputStream;
    this.length = paramImageInputStream.readInt();
    int i = paramImageInputStream.readInt();
  }
  
  public InputStream nextElement() {
    try {
      this.firstTime = false;
      SubImageInputStream subImageInputStream = new SubImageInputStream(this.stream, this.length);
      return new InputStreamAdapter(subImageInputStream);
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public boolean hasMoreElements() {
    if (this.firstTime)
      return true; 
    try {
      int i = this.stream.readInt();
      this.length = this.stream.readInt();
      int j = this.stream.readInt();
      if (j == 1229209940)
        return true; 
      return false;
    } catch (IOException iOException) {
      return false;
    } 
  }
}
