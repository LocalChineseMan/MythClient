package sun.security.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.HashMap;

public class ManifestDigester {
  public static final String MF_MAIN_ATTRS = "Manifest-Main-Attributes";
  
  private byte[] rawBytes;
  
  private HashMap<String, Entry> entries;
  
  static class Position {
    int endOfFirstLine;
    
    int endOfSection;
    
    int startOfNext;
  }
  
  private boolean findSection(int paramInt, Position paramPosition) {
    int i = paramInt, j = this.rawBytes.length;
    int k = paramInt;
    boolean bool = true;
    paramPosition.endOfFirstLine = -1;
    while (i < j) {
      byte b = this.rawBytes[i];
      switch (b) {
        case 13:
          if (paramPosition.endOfFirstLine == -1)
            paramPosition.endOfFirstLine = i - 1; 
          if (i < j && this.rawBytes[i + 1] == 10)
            i++; 
        case 10:
          if (paramPosition.endOfFirstLine == -1)
            paramPosition.endOfFirstLine = i - 1; 
          if (bool || i == j - 1) {
            if (i == j - 1) {
              paramPosition.endOfSection = i;
            } else {
              paramPosition.endOfSection = k;
            } 
            paramPosition.startOfNext = i + 1;
            return true;
          } 
          k = i;
          bool = true;
          break;
        default:
          bool = false;
          break;
      } 
      i++;
    } 
    return false;
  }
  
  public ManifestDigester(byte[] paramArrayOfbyte) {
    this.rawBytes = paramArrayOfbyte;
    this.entries = new HashMap<>();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    Position position = new Position();
    if (!findSection(0, position))
      return; 
    this.entries.put("Manifest-Main-Attributes", new Entry(0, position.endOfSection + 1, position.startOfNext, this.rawBytes));
    int i = position.startOfNext;
    while (findSection(i, position)) {
      int j = position.endOfFirstLine - i + 1;
      int k = position.endOfSection - i + 1;
      int m = position.startOfNext - i;
      if (j > 6 && 
        isNameAttr(paramArrayOfbyte, i)) {
        StringBuilder stringBuilder = new StringBuilder(k);
        try {
          stringBuilder.append(new String(paramArrayOfbyte, i + 6, j - 6, "UTF8"));
          int n = i + j;
          if (n - i < k)
            if (paramArrayOfbyte[n] == 13) {
              n += 2;
            } else {
              n++;
            }  
          while (n - i < k && 
            paramArrayOfbyte[n++] == 32) {
            int i2, i1 = n;
            while (n - i < k && paramArrayOfbyte[n++] != 10);
            if (paramArrayOfbyte[n - 1] != 10)
              return; 
            if (paramArrayOfbyte[n - 2] == 13) {
              i2 = n - i1 - 2;
            } else {
              i2 = n - i1 - 1;
            } 
            stringBuilder.append(new String(paramArrayOfbyte, i1, i2, "UTF8"));
          } 
          this.entries.put(stringBuilder.toString(), new Entry(i, k, m, this.rawBytes));
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
          throw new IllegalStateException("UTF8 not available on platform");
        } 
      } 
      i = position.startOfNext;
    } 
  }
  
  private boolean isNameAttr(byte[] paramArrayOfbyte, int paramInt) {
    return ((paramArrayOfbyte[paramInt] == 78 || paramArrayOfbyte[paramInt] == 110) && (paramArrayOfbyte[paramInt + 1] == 97 || paramArrayOfbyte[paramInt + 1] == 65) && (paramArrayOfbyte[paramInt + 2] == 109 || paramArrayOfbyte[paramInt + 2] == 77) && (paramArrayOfbyte[paramInt + 3] == 101 || paramArrayOfbyte[paramInt + 3] == 69) && paramArrayOfbyte[paramInt + 4] == 58 && paramArrayOfbyte[paramInt + 5] == 32);
  }
  
  public static class Entry {
    int offset;
    
    int length;
    
    int lengthWithBlankLine;
    
    byte[] rawBytes;
    
    boolean oldStyle;
    
    public Entry(int param1Int1, int param1Int2, int param1Int3, byte[] param1ArrayOfbyte) {
      this.offset = param1Int1;
      this.length = param1Int2;
      this.lengthWithBlankLine = param1Int3;
      this.rawBytes = param1ArrayOfbyte;
    }
    
    public byte[] digest(MessageDigest param1MessageDigest) {
      param1MessageDigest.reset();
      if (this.oldStyle) {
        doOldStyle(param1MessageDigest, this.rawBytes, this.offset, this.lengthWithBlankLine);
      } else {
        param1MessageDigest.update(this.rawBytes, this.offset, this.lengthWithBlankLine);
      } 
      return param1MessageDigest.digest();
    }
    
    private void doOldStyle(MessageDigest param1MessageDigest, byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) {
      int i = param1Int1;
      int j = param1Int1;
      int k = param1Int1 + param1Int2;
      byte b = -1;
      while (i < k) {
        if (param1ArrayOfbyte[i] == 13 && b == 32) {
          param1MessageDigest.update(param1ArrayOfbyte, j, i - j - 1);
          j = i;
        } 
        b = param1ArrayOfbyte[i];
        i++;
      } 
      param1MessageDigest.update(param1ArrayOfbyte, j, i - j);
    }
    
    public byte[] digestWorkaround(MessageDigest param1MessageDigest) {
      param1MessageDigest.reset();
      param1MessageDigest.update(this.rawBytes, this.offset, this.length);
      return param1MessageDigest.digest();
    }
  }
  
  public Entry get(String paramString, boolean paramBoolean) {
    Entry entry = this.entries.get(paramString);
    if (entry != null)
      entry.oldStyle = paramBoolean; 
    return entry;
  }
  
  public byte[] manifestDigest(MessageDigest paramMessageDigest) {
    paramMessageDigest.reset();
    paramMessageDigest.update(this.rawBytes, 0, this.rawBytes.length);
    return paramMessageDigest.digest();
  }
}
