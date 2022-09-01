package sun.font;

import java.awt.Font;

public abstract class FontAccess {
  private static FontAccess access;
  
  public static synchronized void setFontAccess(FontAccess paramFontAccess) {
    if (access != null)
      throw new InternalError("Attempt to set FontAccessor twice"); 
    access = paramFontAccess;
  }
  
  public static synchronized FontAccess getFontAccess() {
    return access;
  }
  
  public abstract Font2D getFont2D(Font paramFont);
  
  public abstract void setFont2D(Font paramFont, Font2DHandle paramFont2DHandle);
  
  public abstract void setCreatedFont(Font paramFont);
  
  public abstract boolean isCreatedFont(Font paramFont);
}
