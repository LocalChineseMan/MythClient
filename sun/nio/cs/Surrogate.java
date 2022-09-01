package sun.nio.cs;

import java.nio.CharBuffer;
import java.nio.charset.CoderResult;

public class Surrogate {
  public static final char MIN_HIGH = '?';
  
  public static final char MAX_HIGH = '?';
  
  public static final char MIN_LOW = '?';
  
  public static final char MAX_LOW = '?';
  
  public static final char MIN = '?';
  
  public static final char MAX = '?';
  
  public static final int UCS4_MIN = 65536;
  
  public static final int UCS4_MAX = 1114111;
  
  public static boolean isHigh(int paramInt) {
    return (55296 <= paramInt && paramInt <= 56319);
  }
  
  public static boolean isLow(int paramInt) {
    return (56320 <= paramInt && paramInt <= 57343);
  }
  
  public static boolean is(int paramInt) {
    return (55296 <= paramInt && paramInt <= 57343);
  }
  
  public static boolean neededFor(int paramInt) {
    return Character.isSupplementaryCodePoint(paramInt);
  }
  
  public static char high(int paramInt) {
    assert Character.isSupplementaryCodePoint(paramInt);
    return Character.highSurrogate(paramInt);
  }
  
  public static char low(int paramInt) {
    assert Character.isSupplementaryCodePoint(paramInt);
    return Character.lowSurrogate(paramInt);
  }
  
  public static int toUCS4(char paramChar1, char paramChar2) {
    assert Character.isHighSurrogate(paramChar1) && Character.isLowSurrogate(paramChar2);
    return Character.toCodePoint(paramChar1, paramChar2);
  }
  
  public static class Surrogate {}
  
  public static class Parser {
    private int character;
    
    private CoderResult error = CoderResult.UNDERFLOW;
    
    private boolean isPair;
    
    public int character() {
      assert this.error == null;
      return this.character;
    }
    
    public boolean isPair() {
      assert this.error == null;
      return this.isPair;
    }
    
    public int increment() {
      assert this.error == null;
      return this.isPair ? 2 : 1;
    }
    
    public CoderResult error() {
      assert this.error != null;
      return this.error;
    }
    
    public CoderResult unmappableResult() {
      assert this.error == null;
      return CoderResult.unmappableForLength(this.isPair ? 2 : 1);
    }
    
    public int parse(char param1Char, CharBuffer param1CharBuffer) {
      if (Character.isHighSurrogate(param1Char)) {
        if (!param1CharBuffer.hasRemaining()) {
          this.error = CoderResult.UNDERFLOW;
          return -1;
        } 
        char c = param1CharBuffer.get();
        if (Character.isLowSurrogate(c)) {
          this.character = Character.toCodePoint(param1Char, c);
          this.isPair = true;
          this.error = null;
          return this.character;
        } 
        this.error = CoderResult.malformedForLength(1);
        return -1;
      } 
      if (Character.isLowSurrogate(param1Char)) {
        this.error = CoderResult.malformedForLength(1);
        return -1;
      } 
      this.character = param1Char;
      this.isPair = false;
      this.error = null;
      return this.character;
    }
    
    public int parse(char param1Char, char[] param1ArrayOfchar, int param1Int1, int param1Int2) {
      assert param1ArrayOfchar[param1Int1] == param1Char;
      if (Character.isHighSurrogate(param1Char)) {
        if (param1Int2 - param1Int1 < 2) {
          this.error = CoderResult.UNDERFLOW;
          return -1;
        } 
        char c = param1ArrayOfchar[param1Int1 + 1];
        if (Character.isLowSurrogate(c)) {
          this.character = Character.toCodePoint(param1Char, c);
          this.isPair = true;
          this.error = null;
          return this.character;
        } 
        this.error = CoderResult.malformedForLength(1);
        return -1;
      } 
      if (Character.isLowSurrogate(param1Char)) {
        this.error = CoderResult.malformedForLength(1);
        return -1;
      } 
      this.character = param1Char;
      this.isPair = false;
      this.error = null;
      return this.character;
    }
  }
}
