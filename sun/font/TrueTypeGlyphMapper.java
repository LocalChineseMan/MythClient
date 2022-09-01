package sun.font;

import java.nio.ByteBuffer;
import java.util.Locale;

public class TrueTypeGlyphMapper extends CharToGlyphMapper {
  static final char REVERSE_SOLIDUS = '\\';
  
  static final char JA_YEN = '¥';
  
  static final char JA_FULLWIDTH_TILDE_CHAR = '～';
  
  static final char JA_WAVE_DASH_CHAR = '〜';
  
  static final boolean isJAlocale = Locale.JAPAN.equals(Locale.getDefault());
  
  private final boolean needsJAremapping;
  
  private boolean remapJAWaveDash;
  
  TrueTypeFont font;
  
  CMap cmap;
  
  int numGlyphs;
  
  public TrueTypeGlyphMapper(TrueTypeFont paramTrueTypeFont) {
    this.font = paramTrueTypeFont;
    try {
      this.cmap = CMap.initialize(paramTrueTypeFont);
    } catch (Exception exception) {
      this.cmap = null;
    } 
    if (this.cmap == null)
      handleBadCMAP(); 
    this.missingGlyph = 0;
    ByteBuffer byteBuffer = paramTrueTypeFont.getTableBuffer(1835104368);
    this.numGlyphs = byteBuffer.getChar(4);
    if (FontUtilities.isSolaris && isJAlocale && paramTrueTypeFont.supportsJA()) {
      this.needsJAremapping = true;
      if (FontUtilities.isSolaris8 && 
        getGlyphFromCMAP(12316) == this.missingGlyph)
        this.remapJAWaveDash = true; 
    } else {
      this.needsJAremapping = false;
    } 
  }
  
  public int getNumGlyphs() {
    return this.numGlyphs;
  }
  
  private char getGlyphFromCMAP(int paramInt) {
    try {
      char c = this.cmap.getGlyph(paramInt);
      if (c < this.numGlyphs || c >= '￾')
        return c; 
      if (FontUtilities.isLogging())
        FontUtilities.getLogger()
          .warning(this.font + " out of range glyph id=" + 
            Integer.toHexString(c) + " for char " + 
            Integer.toHexString(paramInt)); 
      return (char)this.missingGlyph;
    } catch (Exception exception) {
      handleBadCMAP();
      return (char)this.missingGlyph;
    } 
  }
  
  private void handleBadCMAP() {
    if (FontUtilities.isLogging())
      FontUtilities.getLogger().severe("Null Cmap for " + this.font + "substituting for this font"); 
    SunFontManager.getInstance().deRegisterBadFont(this.font);
    this.cmap = CMap.theNullCmap;
  }
  
  private final char remapJAChar(char paramChar) {
    switch (paramChar) {
      case '\\':
        return '¥';
      case '〜':
        if (this.remapJAWaveDash)
          return '～'; 
        break;
    } 
    return paramChar;
  }
  
  private final int remapJAIntChar(int paramInt) {
    switch (paramInt) {
      case 92:
        return 165;
      case 12316:
        if (this.remapJAWaveDash)
          return 65374; 
        break;
    } 
    return paramInt;
  }
  
  public int charToGlyph(char paramChar) {
    if (this.needsJAremapping)
      paramChar = remapJAChar(paramChar); 
    char c = getGlyphFromCMAP(paramChar);
    if (this.font.checkUseNatives() && c < this.font.glyphToCharMap.length)
      this.font.glyphToCharMap[c] = paramChar; 
    return c;
  }
  
  public int charToGlyph(int paramInt) {
    if (this.needsJAremapping)
      paramInt = remapJAIntChar(paramInt); 
    char c = getGlyphFromCMAP(paramInt);
    if (this.font.checkUseNatives() && c < this.font.glyphToCharMap.length)
      this.font.glyphToCharMap[c] = (char)paramInt; 
    return c;
  }
  
  public void charsToGlyphs(int paramInt, int[] paramArrayOfint1, int[] paramArrayOfint2) {
    for (byte b = 0; b < paramInt; b++) {
      if (this.needsJAremapping) {
        paramArrayOfint2[b] = getGlyphFromCMAP(remapJAIntChar(paramArrayOfint1[b]));
      } else {
        paramArrayOfint2[b] = getGlyphFromCMAP(paramArrayOfint1[b]);
      } 
      if (this.font.checkUseNatives() && paramArrayOfint2[b] < this.font.glyphToCharMap.length)
        this.font.glyphToCharMap[paramArrayOfint2[b]] = (char)paramArrayOfint1[b]; 
    } 
  }
  
  public void charsToGlyphs(int paramInt, char[] paramArrayOfchar, int[] paramArrayOfint) {
    for (byte b = 0; b < paramInt; b++) {
      int i;
      if (this.needsJAremapping) {
        i = remapJAChar(paramArrayOfchar[b]);
      } else {
        i = paramArrayOfchar[b];
      } 
      if (i >= 55296 && i <= 56319 && b < paramInt - 1) {
        char c = paramArrayOfchar[b + 1];
        if (c >= '?' && c <= '?') {
          i = (i - 55296) * 1024 + c - 56320 + 65536;
          paramArrayOfint[b] = getGlyphFromCMAP(i);
          b++;
          paramArrayOfint[b] = 65535;
          continue;
        } 
      } 
      paramArrayOfint[b] = getGlyphFromCMAP(i);
      if (this.font.checkUseNatives() && paramArrayOfint[b] < this.font.glyphToCharMap.length)
        this.font.glyphToCharMap[paramArrayOfint[b]] = (char)i; 
      continue;
    } 
  }
  
  public boolean charsToGlyphsNS(int paramInt, char[] paramArrayOfchar, int[] paramArrayOfint) {
    for (byte b = 0; b < paramInt; b++) {
      int i;
      if (this.needsJAremapping) {
        i = remapJAChar(paramArrayOfchar[b]);
      } else {
        i = paramArrayOfchar[b];
      } 
      if (i >= 55296 && i <= 56319 && b < paramInt - 1) {
        char c = paramArrayOfchar[b + 1];
        if (c >= '?' && c <= '?') {
          i = (i - 55296) * 1024 + c - 56320 + 65536;
          paramArrayOfint[b + 1] = 65535;
        } 
      } 
      paramArrayOfint[b] = getGlyphFromCMAP(i);
      if (this.font.checkUseNatives() && paramArrayOfint[b] < this.font.glyphToCharMap.length)
        this.font.glyphToCharMap[paramArrayOfint[b]] = (char)i; 
      if (i >= 768) {
        if (FontUtilities.isComplexCharCode(i))
          return true; 
        if (i >= 65536)
          b++; 
      } 
    } 
    return false;
  }
  
  boolean hasSupplementaryChars() {
    return (this.cmap instanceof CMap.CMapFormat8 || this.cmap instanceof CMap.CMapFormat10 || this.cmap instanceof CMap.CMapFormat12);
  }
}
