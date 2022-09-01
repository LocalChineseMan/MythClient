package sun.font;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

abstract class CMap {
  static final short ShiftJISEncoding = 2;
  
  static final short GBKEncoding = 3;
  
  static final short Big5Encoding = 4;
  
  static final short WansungEncoding = 5;
  
  static final short JohabEncoding = 6;
  
  static final short MSUnicodeSurrogateEncoding = 10;
  
  static final char noSuchChar = '�';
  
  static final int SHORTMASK = 65535;
  
  static final int INTMASK = -1;
  
  static final char[][] converterMaps = new char[7][];
  
  char[] xlat;
  
  static CMap initialize(TrueTypeFont paramTrueTypeFont) {
    CMap cMap = null;
    short s = -1;
    int i = 0, j = 0, k = 0, m = 0, n = 0, i1 = 0;
    int i2 = 0, i3 = 0;
    boolean bool = false;
    ByteBuffer byteBuffer = paramTrueTypeFont.getTableBuffer(1668112752);
    int i4 = paramTrueTypeFont.getTableSize(1668112752);
    short s1 = byteBuffer.getShort(2);
    for (byte b = 0; b < s1; b++) {
      byteBuffer.position(b * 8 + 4);
      short s2 = byteBuffer.getShort();
      if (s2 == 3) {
        bool = true;
        s = byteBuffer.getShort();
        int i5 = byteBuffer.getInt();
        switch (s) {
          case 0:
            i = i5;
            break;
          case 1:
            j = i5;
            break;
          case 2:
            k = i5;
            break;
          case 3:
            m = i5;
            break;
          case 4:
            n = i5;
            break;
          case 5:
            i1 = i5;
            break;
          case 6:
            i2 = i5;
            break;
          case 10:
            i3 = i5;
            break;
        } 
      } 
    } 
    if (bool) {
      if (i3 != 0) {
        cMap = createCMap(byteBuffer, i3, null);
      } else if (i != 0) {
        cMap = createCMap(byteBuffer, i, null);
      } else if (j != 0) {
        cMap = createCMap(byteBuffer, j, null);
      } else if (k != 0) {
        cMap = createCMap(byteBuffer, k, 
            getConverterMap((short)2));
      } else if (m != 0) {
        cMap = createCMap(byteBuffer, m, 
            getConverterMap((short)3));
      } else if (n != 0) {
        if (FontUtilities.isSolaris && paramTrueTypeFont.platName != null && (paramTrueTypeFont.platName
          .startsWith("/usr/openwin/lib/locale/zh_CN.EUC/X11/fonts/TrueType") || paramTrueTypeFont.platName
          
          .startsWith("/usr/openwin/lib/locale/zh_CN/X11/fonts/TrueType") || paramTrueTypeFont.platName
          
          .startsWith("/usr/openwin/lib/locale/zh/X11/fonts/TrueType"))) {
          cMap = createCMap(byteBuffer, n, 
              getConverterMap((short)3));
        } else {
          cMap = createCMap(byteBuffer, n, 
              getConverterMap((short)4));
        } 
      } else if (i1 != 0) {
        cMap = createCMap(byteBuffer, i1, 
            getConverterMap((short)5));
      } else if (i2 != 0) {
        cMap = createCMap(byteBuffer, i2, 
            getConverterMap((short)6));
      } 
    } else {
      cMap = createCMap(byteBuffer, byteBuffer.getInt(8), null);
    } 
    return cMap;
  }
  
  static char[] getConverter(short paramShort) {
    String str;
    char c1 = '耀';
    char c2 = '￿';
    switch (paramShort) {
      case 2:
        c1 = '腀';
        c2 = 'ﳼ';
        str = "SJIS";
        break;
      case 3:
        c1 = '腀';
        c2 = 'ﺠ';
        str = "GBK";
        break;
      case 4:
        c1 = 'ꅀ';
        c2 = '﻾';
        str = "Big5";
        break;
      case 5:
        c1 = 'ꆡ';
        c2 = 'ﻞ';
        str = "EUC_KR";
        break;
      case 6:
        c1 = '腁';
        c2 = '﷾';
        str = "Johab";
        break;
      default:
        return null;
    } 
    try {
      char[] arrayOfChar1 = new char[65536];
      for (byte b1 = 0; b1 < 65536; b1++)
        arrayOfChar1[b1] = '�'; 
      byte[] arrayOfByte = new byte[(c2 - c1 + 1) * 2];
      char[] arrayOfChar2 = new char[c2 - c1 + 1];
      byte b2 = 0;
      if (paramShort == 2) {
        for (char c3 = c1; c3 <= c2; c3++) {
          int i = c3 >> 8 & 0xFF;
          if (i >= 161 && i <= 223) {
            arrayOfByte[b2++] = -1;
            arrayOfByte[b2++] = -1;
          } else {
            arrayOfByte[b2++] = (byte)i;
            arrayOfByte[b2++] = (byte)(c3 & 0xFF);
          } 
        } 
      } else {
        for (char c3 = c1; c3 <= c2; c3++) {
          arrayOfByte[b2++] = (byte)(c3 >> 8 & 0xFF);
          arrayOfByte[b2++] = (byte)(c3 & 0xFF);
        } 
      } 
      Charset.forName(str).newDecoder()
        .onMalformedInput(CodingErrorAction.REPLACE)
        .onUnmappableCharacter(CodingErrorAction.REPLACE)
        .replaceWith("\000")
        .decode(ByteBuffer.wrap(arrayOfByte, 0, arrayOfByte.length), 
          CharBuffer.wrap(arrayOfChar2, 0, arrayOfChar2.length), true);
      char c;
      for (c = ' '; c <= '~'; c++)
        arrayOfChar1[c] = (char)c; 
      if (paramShort == 2)
        for (c = '¡'; c <= 'ß'; c++)
          arrayOfChar1[c] = (char)(c - 161 + 65377);  
      System.arraycopy(arrayOfChar2, 0, arrayOfChar1, c1, arrayOfChar2.length);
      char[] arrayOfChar3 = new char[65536];
      for (byte b3 = 0; b3 < 65536; b3++) {
        if (arrayOfChar1[b3] != '�')
          arrayOfChar3[arrayOfChar1[b3]] = (char)b3; 
      } 
      return arrayOfChar3;
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
  
  static char[] getConverterMap(short paramShort) {
    if (converterMaps[paramShort] == null)
      converterMaps[paramShort] = getConverter(paramShort); 
    return converterMaps[paramShort];
  }
  
  static CMap createCMap(ByteBuffer paramByteBuffer, int paramInt, char[] paramArrayOfchar) {
    long l;
    char c = paramByteBuffer.getChar(paramInt);
    if (c < '\b') {
      l = paramByteBuffer.getChar(paramInt + 2);
    } else {
      l = (paramByteBuffer.getInt(paramInt + 4) & 0xFFFFFFFF);
    } 
    if (paramInt + l > paramByteBuffer.capacity() && 
      FontUtilities.isLogging())
      FontUtilities.getLogger().warning("Cmap subtable overflows buffer."); 
    switch (c) {
      case '\000':
        return new CMapFormat0(paramByteBuffer, paramInt);
      case '\002':
        return new CMapFormat2(paramByteBuffer, paramInt, paramArrayOfchar);
      case '\004':
        return new CMapFormat4(paramByteBuffer, paramInt, paramArrayOfchar);
      case '\006':
        return new CMapFormat6(paramByteBuffer, paramInt, paramArrayOfchar);
      case '\b':
        return new CMapFormat8(paramByteBuffer, paramInt, paramArrayOfchar);
      case '\n':
        return new CMapFormat10(paramByteBuffer, paramInt, paramArrayOfchar);
      case '\f':
        return new CMapFormat12(paramByteBuffer, paramInt, paramArrayOfchar);
    } 
    throw new RuntimeException("Cmap format unimplemented: " + paramByteBuffer
        .getChar(paramInt));
  }
  
  static class CMapFormat4 extends CMap {
    int segCount;
    
    int entrySelector;
    
    int rangeShift;
    
    char[] endCount;
    
    char[] startCount;
    
    short[] idDelta;
    
    char[] idRangeOffset;
    
    char[] glyphIds;
    
    CMapFormat4(ByteBuffer param1ByteBuffer, int param1Int, char[] param1ArrayOfchar) {
      this.xlat = param1ArrayOfchar;
      param1ByteBuffer.position(param1Int);
      CharBuffer charBuffer = param1ByteBuffer.asCharBuffer();
      charBuffer.get();
      int i = charBuffer.get();
      if (param1Int + i > param1ByteBuffer.capacity())
        i = param1ByteBuffer.capacity() - param1Int; 
      charBuffer.get();
      this.segCount = charBuffer.get() / 2;
      char c = charBuffer.get();
      this.entrySelector = charBuffer.get();
      this.rangeShift = charBuffer.get() / 2;
      this.startCount = new char[this.segCount];
      this.endCount = new char[this.segCount];
      this.idDelta = new short[this.segCount];
      this.idRangeOffset = new char[this.segCount];
      int j;
      for (j = 0; j < this.segCount; j++)
        this.endCount[j] = charBuffer.get(); 
      charBuffer.get();
      for (j = 0; j < this.segCount; j++)
        this.startCount[j] = charBuffer.get(); 
      for (j = 0; j < this.segCount; j++)
        this.idDelta[j] = (short)charBuffer.get(); 
      for (j = 0; j < this.segCount; j++) {
        char c1 = charBuffer.get();
        this.idRangeOffset[j] = (char)(c1 >> 1 & 0xFFFF);
      } 
      j = (this.segCount * 8 + 16) / 2;
      charBuffer.position(j);
      int k = i / 2 - j;
      this.glyphIds = new char[k];
      for (byte b = 0; b < k; b++)
        this.glyphIds[b] = charBuffer.get(); 
    }
    
    char getGlyph(int param1Int) {
      int i = 0;
      char c = Character.MIN_VALUE;
      int j = getControlCodeGlyph(param1Int, true);
      if (j >= 0)
        return (char)j; 
      if (this.xlat != null)
        param1Int = this.xlat[param1Int]; 
      int k = 0, m = this.startCount.length;
      i = this.startCount.length >> 1;
      while (k < m) {
        if (this.endCount[i] < param1Int) {
          k = i + 1;
        } else {
          m = i;
        } 
        i = k + m >> 1;
      } 
      if (param1Int >= this.startCount[i] && param1Int <= this.endCount[i]) {
        char c1 = this.idRangeOffset[i];
        if (c1 == '\000') {
          c = (char)(param1Int + this.idDelta[i]);
        } else {
          int n = c1 - this.segCount + i + param1Int - this.startCount[i];
          c = this.glyphIds[n];
          if (c != '\000')
            c = (char)(c + this.idDelta[i]); 
        } 
      } 
      if (c != '\000');
      return c;
    }
  }
  
  static class CMap {}
  
  static class CMap {}
  
  static class CMap {}
  
  static class CMap {}
  
  static class CMap {}
  
  static class CMap {}
  
  static class NullCMapClass extends CMap {
    char getGlyph(int param1Int) {
      return Character.MIN_VALUE;
    }
  }
  
  public static final NullCMapClass theNullCmap = new NullCMapClass();
  
  abstract char getGlyph(int paramInt);
  
  final int getControlCodeGlyph(int paramInt, boolean paramBoolean) {
    if (paramInt < 16) {
      switch (paramInt) {
        case 9:
        case 10:
        case 13:
          return 65535;
      } 
    } else if (paramInt >= 8204) {
      if (paramInt <= 8207 || (paramInt >= 8232 && paramInt <= 8238) || (paramInt >= 8298 && paramInt <= 8303))
        return 65535; 
      if (paramBoolean && paramInt >= 65535)
        return 0; 
    } 
    return -1;
  }
}
