package sun.text.normalizer;

import java.text.ParsePosition;
import java.util.Iterator;
import java.util.TreeSet;

public class UnicodeSet implements UnicodeMatcher {
  private static final int LOW = 0;
  
  private static final int HIGH = 1114112;
  
  public static final int MIN_VALUE = 0;
  
  public static final int MAX_VALUE = 1114111;
  
  private int len;
  
  private int[] list;
  
  private int[] rangeList;
  
  private int[] buffer;
  
  TreeSet<String> strings = new TreeSet<>();
  
  private String pat = null;
  
  private static final int START_EXTRA = 16;
  
  private static final int GROW_EXTRA = 16;
  
  private static UnicodeSet[] INCLUSIONS = null;
  
  public UnicodeSet() {
    this.list = new int[17];
    this.list[this.len++] = 1114112;
  }
  
  public UnicodeSet(int paramInt1, int paramInt2) {
    this();
    complement(paramInt1, paramInt2);
  }
  
  public UnicodeSet(String paramString) {
    this();
    applyPattern(paramString, (ParsePosition)null, (SymbolTable)null, 1);
  }
  
  public UnicodeSet set(UnicodeSet paramUnicodeSet) {
    this.list = (int[])paramUnicodeSet.list.clone();
    this.len = paramUnicodeSet.len;
    this.pat = paramUnicodeSet.pat;
    this.strings = (TreeSet<String>)paramUnicodeSet.strings.clone();
    return this;
  }
  
  public final UnicodeSet applyPattern(String paramString) {
    return applyPattern(paramString, (ParsePosition)null, (SymbolTable)null, 1);
  }
  
  private static void _appendToPat(StringBuffer paramStringBuffer, String paramString, boolean paramBoolean) {
    for (int i = 0; i < paramString.length(); i += UTF16.getCharCount(i))
      _appendToPat(paramStringBuffer, UTF16.charAt(paramString, i), paramBoolean); 
  }
  
  private static void _appendToPat(StringBuffer paramStringBuffer, int paramInt, boolean paramBoolean) {
    if (paramBoolean && Utility.isUnprintable(paramInt))
      if (Utility.escapeUnprintable(paramStringBuffer, paramInt))
        return;  
    switch (paramInt) {
      case 36:
      case 38:
      case 45:
      case 58:
      case 91:
      case 92:
      case 93:
      case 94:
      case 123:
      case 125:
        paramStringBuffer.append('\\');
        break;
      default:
        if (UCharacterProperty.isRuleWhiteSpace(paramInt))
          paramStringBuffer.append('\\'); 
        break;
    } 
    UTF16.append(paramStringBuffer, paramInt);
  }
  
  private StringBuffer _toPattern(StringBuffer paramStringBuffer, boolean paramBoolean) {
    if (this.pat != null) {
      byte b = 0;
      for (int i = 0; i < this.pat.length(); ) {
        int j = UTF16.charAt(this.pat, i);
        i += UTF16.getCharCount(j);
        if (paramBoolean && Utility.isUnprintable(j)) {
          if (b % 2 == 1)
            paramStringBuffer.setLength(paramStringBuffer.length() - 1); 
          Utility.escapeUnprintable(paramStringBuffer, j);
          b = 0;
          continue;
        } 
        UTF16.append(paramStringBuffer, j);
        if (j == 92) {
          b++;
          continue;
        } 
        b = 0;
      } 
      return paramStringBuffer;
    } 
    return _generatePattern(paramStringBuffer, paramBoolean, true);
  }
  
  public StringBuffer _generatePattern(StringBuffer paramStringBuffer, boolean paramBoolean1, boolean paramBoolean2) {
    paramStringBuffer.append('[');
    int i = getRangeCount();
    if (i > 1 && 
      getRangeStart(0) == 0 && 
      getRangeEnd(i - 1) == 1114111) {
      paramStringBuffer.append('^');
      for (byte b = 1; b < i; b++) {
        int j = getRangeEnd(b - 1) + 1;
        int k = getRangeStart(b) - 1;
        _appendToPat(paramStringBuffer, j, paramBoolean1);
        if (j != k) {
          if (j + 1 != k)
            paramStringBuffer.append('-'); 
          _appendToPat(paramStringBuffer, k, paramBoolean1);
        } 
      } 
    } else {
      for (byte b = 0; b < i; b++) {
        int j = getRangeStart(b);
        int k = getRangeEnd(b);
        _appendToPat(paramStringBuffer, j, paramBoolean1);
        if (j != k) {
          if (j + 1 != k)
            paramStringBuffer.append('-'); 
          _appendToPat(paramStringBuffer, k, paramBoolean1);
        } 
      } 
    } 
    if (paramBoolean2 && this.strings.size() > 0) {
      Iterator<String> iterator = this.strings.iterator();
      while (iterator.hasNext()) {
        paramStringBuffer.append('{');
        _appendToPat(paramStringBuffer, iterator.next(), paramBoolean1);
        paramStringBuffer.append('}');
      } 
    } 
    return paramStringBuffer.append(']');
  }
  
  private UnicodeSet add_unchecked(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 > 1114111)
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt1, 6)); 
    if (paramInt2 < 0 || paramInt2 > 1114111)
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt2, 6)); 
    if (paramInt1 < paramInt2) {
      add(range(paramInt1, paramInt2), 2, 0);
    } else if (paramInt1 == paramInt2) {
      add(paramInt1);
    } 
    return this;
  }
  
  public final UnicodeSet add(int paramInt) {
    return add_unchecked(paramInt);
  }
  
  private final UnicodeSet add_unchecked(int paramInt) {
    if (paramInt < 0 || paramInt > 1114111)
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt, 6)); 
    int i = findCodePoint(paramInt);
    if ((i & 0x1) != 0)
      return this; 
    if (paramInt == this.list[i] - 1) {
      this.list[i] = paramInt;
      if (paramInt == 1114111) {
        ensureCapacity(this.len + 1);
        this.list[this.len++] = 1114112;
      } 
      if (i > 0 && paramInt == this.list[i - 1]) {
        System.arraycopy(this.list, i + 1, this.list, i - 1, this.len - i - 1);
        this.len -= 2;
      } 
    } else if (i > 0 && paramInt == this.list[i - 1]) {
      this.list[i - 1] = this.list[i - 1] + 1;
    } else {
      if (this.len + 2 > this.list.length) {
        int[] arrayOfInt = new int[this.len + 2 + 16];
        if (i != 0)
          System.arraycopy(this.list, 0, arrayOfInt, 0, i); 
        System.arraycopy(this.list, i, arrayOfInt, i + 2, this.len - i);
        this.list = arrayOfInt;
      } else {
        System.arraycopy(this.list, i, this.list, i + 2, this.len - i);
      } 
      this.list[i] = paramInt;
      this.list[i + 1] = paramInt + 1;
      this.len += 2;
    } 
    this.pat = null;
    return this;
  }
  
  public final UnicodeSet add(String paramString) {
    int i = getSingleCP(paramString);
    if (i < 0) {
      this.strings.add(paramString);
      this.pat = null;
    } else {
      add_unchecked(i, i);
    } 
    return this;
  }
  
  private static int getSingleCP(String paramString) {
    if (paramString.length() < 1)
      throw new IllegalArgumentException("Can't use zero-length strings in UnicodeSet"); 
    if (paramString.length() > 2)
      return -1; 
    if (paramString.length() == 1)
      return paramString.charAt(0); 
    int i = UTF16.charAt(paramString, 0);
    if (i > 65535)
      return i; 
    return -1;
  }
  
  public UnicodeSet complement(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 > 1114111)
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt1, 6)); 
    if (paramInt2 < 0 || paramInt2 > 1114111)
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt2, 6)); 
    if (paramInt1 <= paramInt2)
      xor(range(paramInt1, paramInt2), 2, 0); 
    this.pat = null;
    return this;
  }
  
  public UnicodeSet complement() {
    if (this.list[0] == 0) {
      System.arraycopy(this.list, 1, this.list, 0, this.len - 1);
      this.len--;
    } else {
      ensureCapacity(this.len + 1);
      System.arraycopy(this.list, 0, this.list, 1, this.len);
      this.list[0] = 0;
      this.len++;
    } 
    this.pat = null;
    return this;
  }
  
  public boolean contains(int paramInt) {
    if (paramInt < 0 || paramInt > 1114111)
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt, 6)); 
    int i = findCodePoint(paramInt);
    return ((i & 0x1) != 0);
  }
  
  private final int findCodePoint(int paramInt) {
    if (paramInt < this.list[0])
      return 0; 
    if (this.len >= 2 && paramInt >= this.list[this.len - 2])
      return this.len - 1; 
    int i = 0;
    int j = this.len - 1;
    while (true) {
      int k = i + j >>> 1;
      if (k == i)
        return j; 
      if (paramInt < this.list[k]) {
        j = k;
        continue;
      } 
      i = k;
    } 
  }
  
  public UnicodeSet addAll(UnicodeSet paramUnicodeSet) {
    add(paramUnicodeSet.list, paramUnicodeSet.len, 0);
    this.strings.addAll(paramUnicodeSet.strings);
    return this;
  }
  
  public UnicodeSet retainAll(UnicodeSet paramUnicodeSet) {
    retain(paramUnicodeSet.list, paramUnicodeSet.len, 0);
    this.strings.retainAll(paramUnicodeSet.strings);
    return this;
  }
  
  public UnicodeSet removeAll(UnicodeSet paramUnicodeSet) {
    retain(paramUnicodeSet.list, paramUnicodeSet.len, 2);
    this.strings.removeAll(paramUnicodeSet.strings);
    return this;
  }
  
  public UnicodeSet clear() {
    this.list[0] = 1114112;
    this.len = 1;
    this.pat = null;
    this.strings.clear();
    return this;
  }
  
  public int getRangeCount() {
    return this.len / 2;
  }
  
  public int getRangeStart(int paramInt) {
    return this.list[paramInt * 2];
  }
  
  public int getRangeEnd(int paramInt) {
    return this.list[paramInt * 2 + 1] - 1;
  }
  
  UnicodeSet applyPattern(String paramString, ParsePosition paramParsePosition, SymbolTable paramSymbolTable, int paramInt) {
    boolean bool = (paramParsePosition == null) ? true : false;
    if (bool)
      paramParsePosition = new ParsePosition(0); 
    StringBuffer stringBuffer = new StringBuffer();
    RuleCharacterIterator ruleCharacterIterator = new RuleCharacterIterator(paramString, paramSymbolTable, paramParsePosition);
    applyPattern(ruleCharacterIterator, paramSymbolTable, stringBuffer, paramInt);
    if (ruleCharacterIterator.inVariable())
      syntaxError(ruleCharacterIterator, "Extra chars in variable value"); 
    this.pat = stringBuffer.toString();
    if (bool) {
      int i = paramParsePosition.getIndex();
      if ((paramInt & 0x1) != 0)
        i = Utility.skipWhitespace(paramString, i); 
      if (i != paramString.length())
        throw new IllegalArgumentException("Parse of \"" + paramString + "\" failed at " + i); 
    } 
    return this;
  }
  
  void applyPattern(RuleCharacterIterator paramRuleCharacterIterator, SymbolTable paramSymbolTable, StringBuffer paramStringBuffer, int paramInt) {
    int i = 3;
    if ((paramInt & 0x1) != 0)
      i |= 0x4; 
    StringBuffer stringBuffer1 = new StringBuffer(), stringBuffer2 = null;
    boolean bool1 = false;
    UnicodeSet unicodeSet = null;
    Object object = null;
    char c1 = Character.MIN_VALUE;
    int j = 0;
    byte b = 0;
    char c2 = Character.MIN_VALUE;
    boolean bool2 = false;
    clear();
    while (b != 2 && !paramRuleCharacterIterator.atEnd()) {
      int k = 0;
      boolean bool = false;
      UnicodeSet unicodeSet1 = null;
      byte b1 = 0;
      if (resemblesPropertyPattern(paramRuleCharacterIterator, i)) {
        b1 = 2;
      } else {
        object = paramRuleCharacterIterator.getPos(object);
        k = paramRuleCharacterIterator.next(i);
        bool = paramRuleCharacterIterator.isEscaped();
        if (k == 91 && !bool) {
          if (b == 1) {
            paramRuleCharacterIterator.setPos(object);
            b1 = 1;
          } else {
            b = 1;
            stringBuffer1.append('[');
            object = paramRuleCharacterIterator.getPos(object);
            k = paramRuleCharacterIterator.next(i);
            bool = paramRuleCharacterIterator.isEscaped();
            if (k == 94 && !bool) {
              bool2 = true;
              stringBuffer1.append('^');
              object = paramRuleCharacterIterator.getPos(object);
              k = paramRuleCharacterIterator.next(i);
              bool = paramRuleCharacterIterator.isEscaped();
            } 
            if (k == 45) {
              bool = true;
            } else {
              paramRuleCharacterIterator.setPos(object);
              continue;
            } 
          } 
        } else if (paramSymbolTable != null) {
          UnicodeMatcher unicodeMatcher = paramSymbolTable.lookupMatcher(k);
          if (unicodeMatcher != null)
            try {
              unicodeSet1 = (UnicodeSet)unicodeMatcher;
              b1 = 3;
            } catch (ClassCastException classCastException) {
              syntaxError(paramRuleCharacterIterator, "Syntax error");
            }  
        } 
      } 
      if (b1 != 0) {
        if (c1 == '\001') {
          if (c2)
            syntaxError(paramRuleCharacterIterator, "Char expected after operator"); 
          add_unchecked(j, j);
          _appendToPat(stringBuffer1, j, false);
          c1 = c2 = Character.MIN_VALUE;
        } 
        if (c2 == '-' || c2 == '&')
          stringBuffer1.append(c2); 
        if (unicodeSet1 == null) {
          if (unicodeSet == null)
            unicodeSet = new UnicodeSet(); 
          unicodeSet1 = unicodeSet;
        } 
        switch (b1) {
          case 1:
            unicodeSet1.applyPattern(paramRuleCharacterIterator, paramSymbolTable, stringBuffer1, paramInt);
            break;
          case 2:
            paramRuleCharacterIterator.skipIgnored(i);
            unicodeSet1.applyPropertyPattern(paramRuleCharacterIterator, stringBuffer1, paramSymbolTable);
            break;
          case 3:
            unicodeSet1._toPattern(stringBuffer1, false);
            break;
        } 
        bool1 = true;
        if (!b) {
          set(unicodeSet1);
          b = 2;
          break;
        } 
        switch (c2) {
          case true:
            removeAll(unicodeSet1);
            break;
          case true:
            retainAll(unicodeSet1);
            break;
          case false:
            addAll(unicodeSet1);
            break;
        } 
        c2 = Character.MIN_VALUE;
        c1 = '\002';
        continue;
      } 
      if (b == 0)
        syntaxError(paramRuleCharacterIterator, "Missing '['"); 
      if (!bool) {
        boolean bool3;
        boolean bool4;
        switch (k) {
          case 93:
            if (c1 == '\001') {
              add_unchecked(j, j);
              _appendToPat(stringBuffer1, j, false);
            } 
            if (c2 == '-') {
              add_unchecked(c2, c2);
              stringBuffer1.append(c2);
            } else if (c2 == '&') {
              syntaxError(paramRuleCharacterIterator, "Trailing '&'");
            } 
            stringBuffer1.append(']');
            b = 2;
            continue;
          case 45:
            if (!c2) {
              if (c1 != Character.MIN_VALUE) {
                c2 = (char)k;
                continue;
              } 
              add_unchecked(k, k);
              k = paramRuleCharacterIterator.next(i);
              bool = paramRuleCharacterIterator.isEscaped();
              if (k == 93 && !bool) {
                stringBuffer1.append("-]");
                b = 2;
                continue;
              } 
            } 
            syntaxError(paramRuleCharacterIterator, "'-' not after char or set");
            break;
          case 38:
            if (c1 == '\002' && c2 == '\000') {
              c2 = (char)k;
              continue;
            } 
            syntaxError(paramRuleCharacterIterator, "'&' not after set");
            break;
          case 94:
            syntaxError(paramRuleCharacterIterator, "'^' not after '['");
            break;
          case 123:
            if (c2 != '\000')
              syntaxError(paramRuleCharacterIterator, "Missing operand after operator"); 
            if (c1 == '\001') {
              add_unchecked(j, j);
              _appendToPat(stringBuffer1, j, false);
            } 
            c1 = Character.MIN_VALUE;
            if (stringBuffer2 == null) {
              stringBuffer2 = new StringBuffer();
            } else {
              stringBuffer2.setLength(0);
            } 
            bool3 = false;
            while (!paramRuleCharacterIterator.atEnd()) {
              k = paramRuleCharacterIterator.next(i);
              bool = paramRuleCharacterIterator.isEscaped();
              if (k == 125 && !bool) {
                bool3 = true;
                break;
              } 
              UTF16.append(stringBuffer2, k);
            } 
            if (stringBuffer2.length() < 1 || !bool3)
              syntaxError(paramRuleCharacterIterator, "Invalid multicharacter string"); 
            add(stringBuffer2.toString());
            stringBuffer1.append('{');
            _appendToPat(stringBuffer1, stringBuffer2.toString(), false);
            stringBuffer1.append('}');
            continue;
          case 36:
            object = paramRuleCharacterIterator.getPos(object);
            k = paramRuleCharacterIterator.next(i);
            bool = paramRuleCharacterIterator.isEscaped();
            bool4 = (k == 93 && !bool) ? true : false;
            if (paramSymbolTable == null && !bool4) {
              k = 36;
              paramRuleCharacterIterator.setPos(object);
              break;
            } 
            if (bool4 && c2 == '\000') {
              if (c1 == '\001') {
                add_unchecked(j, j);
                _appendToPat(stringBuffer1, j, false);
              } 
              add_unchecked(65535);
              bool1 = true;
              stringBuffer1.append('$').append(']');
              b = 2;
              continue;
            } 
            syntaxError(paramRuleCharacterIterator, "Unquoted '$'");
            break;
        } 
      } 
      switch (c1) {
        case 0:
          c1 = '\001';
          j = k;
        case 1:
          if (c2 == '-') {
            if (j >= k)
              syntaxError(paramRuleCharacterIterator, "Invalid range"); 
            add_unchecked(j, k);
            _appendToPat(stringBuffer1, j, false);
            stringBuffer1.append(c2);
            _appendToPat(stringBuffer1, k, false);
            c1 = c2 = Character.MIN_VALUE;
            continue;
          } 
          add_unchecked(j, j);
          _appendToPat(stringBuffer1, j, false);
          j = k;
        case 2:
          if (c2 != '\000')
            syntaxError(paramRuleCharacterIterator, "Set expected after operator"); 
          j = k;
          c1 = '\001';
      } 
    } 
    if (b != 2)
      syntaxError(paramRuleCharacterIterator, "Missing ']'"); 
    paramRuleCharacterIterator.skipIgnored(i);
    if (bool2)
      complement(); 
    if (bool1) {
      paramStringBuffer.append(stringBuffer1.toString());
    } else {
      _generatePattern(paramStringBuffer, false, true);
    } 
  }
  
  private static void syntaxError(RuleCharacterIterator paramRuleCharacterIterator, String paramString) {
    throw new IllegalArgumentException("Error: " + paramString + " at \"" + 
        Utility.escape(paramRuleCharacterIterator.toString()) + '"');
  }
  
  private void ensureCapacity(int paramInt) {
    if (paramInt <= this.list.length)
      return; 
    int[] arrayOfInt = new int[paramInt + 16];
    System.arraycopy(this.list, 0, arrayOfInt, 0, this.len);
    this.list = arrayOfInt;
  }
  
  private void ensureBufferCapacity(int paramInt) {
    if (this.buffer != null && paramInt <= this.buffer.length)
      return; 
    this.buffer = new int[paramInt + 16];
  }
  
  private int[] range(int paramInt1, int paramInt2) {
    if (this.rangeList == null) {
      this.rangeList = new int[] { paramInt1, paramInt2 + 1, 1114112 };
    } else {
      this.rangeList[0] = paramInt1;
      this.rangeList[1] = paramInt2 + 1;
    } 
    return this.rangeList;
  }
  
  private UnicodeSet xor(int[] paramArrayOfint, int paramInt1, int paramInt2) {
    int j;
    ensureBufferCapacity(this.len + paramInt1);
    byte b1 = 0, b2 = 0, b3 = 0;
    int i = this.list[b1++];
    if (paramInt2 == 1 || paramInt2 == 2) {
      j = 0;
      if (paramArrayOfint[b2] == 0) {
        b2++;
        j = paramArrayOfint[b2];
      } 
    } else {
      j = paramArrayOfint[b2++];
    } 
    while (true) {
      while (i < j) {
        this.buffer[b3++] = i;
        i = this.list[b1++];
      } 
      if (j < i) {
        this.buffer[b3++] = j;
        j = paramArrayOfint[b2++];
        continue;
      } 
      if (i != 1114112) {
        i = this.list[b1++];
        j = paramArrayOfint[b2++];
        continue;
      } 
      break;
    } 
    this.buffer[b3++] = 1114112;
    this.len = b3;
    int[] arrayOfInt = this.list;
    this.list = this.buffer;
    this.buffer = arrayOfInt;
    this.pat = null;
    return this;
  }
  
  private UnicodeSet add(int[] paramArrayOfint, int paramInt1, int paramInt2) {
    ensureBufferCapacity(this.len + paramInt1);
    byte b1 = 0, b2 = 0, b3 = 0;
    int i = this.list[b1++];
    int j = paramArrayOfint[b2++];
    while (true) {
      switch (paramInt2) {
        case 0:
          if (i < j) {
            if (b3 && i <= this.buffer[b3 - 1]) {
              i = max(this.list[b1], this.buffer[--b3]);
            } else {
              this.buffer[b3++] = i;
              i = this.list[b1];
            } 
            b1++;
            paramInt2 ^= 0x1;
            continue;
          } 
          if (j < i) {
            if (b3 > 0 && j <= this.buffer[b3 - 1]) {
              j = max(paramArrayOfint[b2], this.buffer[--b3]);
            } else {
              this.buffer[b3++] = j;
              j = paramArrayOfint[b2];
            } 
            b2++;
            paramInt2 ^= 0x2;
            continue;
          } 
          if (i == 1114112)
            break; 
          if (b3 > 0 && i <= this.buffer[b3 - 1]) {
            i = max(this.list[b1], this.buffer[--b3]);
          } else {
            this.buffer[b3++] = i;
            i = this.list[b1];
          } 
          b1++;
          paramInt2 ^= 0x1;
          j = paramArrayOfint[b2++];
          paramInt2 ^= 0x2;
        case 3:
          if (j <= i) {
            if (i == 1114112)
              break; 
            this.buffer[b3++] = i;
          } else {
            if (j == 1114112)
              break; 
            this.buffer[b3++] = j;
          } 
          i = this.list[b1++];
          paramInt2 ^= 0x1;
          j = paramArrayOfint[b2++];
          paramInt2 ^= 0x2;
        case 1:
          if (i < j) {
            this.buffer[b3++] = i;
            i = this.list[b1++];
            paramInt2 ^= 0x1;
            continue;
          } 
          if (j < i) {
            j = paramArrayOfint[b2++];
            paramInt2 ^= 0x2;
            continue;
          } 
          if (i == 1114112)
            break; 
          i = this.list[b1++];
          paramInt2 ^= 0x1;
          j = paramArrayOfint[b2++];
          paramInt2 ^= 0x2;
        case 2:
          if (j < i) {
            this.buffer[b3++] = j;
            j = paramArrayOfint[b2++];
            paramInt2 ^= 0x2;
            continue;
          } 
          if (i < j) {
            i = this.list[b1++];
            paramInt2 ^= 0x1;
            continue;
          } 
          if (i == 1114112)
            break; 
          i = this.list[b1++];
          paramInt2 ^= 0x1;
          j = paramArrayOfint[b2++];
          paramInt2 ^= 0x2;
      } 
    } 
    this.buffer[b3++] = 1114112;
    this.len = b3;
    int[] arrayOfInt = this.list;
    this.list = this.buffer;
    this.buffer = arrayOfInt;
    this.pat = null;
    return this;
  }
  
  private UnicodeSet retain(int[] paramArrayOfint, int paramInt1, int paramInt2) {
    ensureBufferCapacity(this.len + paramInt1);
    byte b1 = 0, b2 = 0, b3 = 0;
    int i = this.list[b1++];
    int j = paramArrayOfint[b2++];
    while (true) {
      switch (paramInt2) {
        case 0:
          if (i < j) {
            i = this.list[b1++];
            paramInt2 ^= 0x1;
            continue;
          } 
          if (j < i) {
            j = paramArrayOfint[b2++];
            paramInt2 ^= 0x2;
            continue;
          } 
          if (i == 1114112)
            break; 
          this.buffer[b3++] = i;
          i = this.list[b1++];
          paramInt2 ^= 0x1;
          j = paramArrayOfint[b2++];
          paramInt2 ^= 0x2;
        case 3:
          if (i < j) {
            this.buffer[b3++] = i;
            i = this.list[b1++];
            paramInt2 ^= 0x1;
            continue;
          } 
          if (j < i) {
            this.buffer[b3++] = j;
            j = paramArrayOfint[b2++];
            paramInt2 ^= 0x2;
            continue;
          } 
          if (i == 1114112)
            break; 
          this.buffer[b3++] = i;
          i = this.list[b1++];
          paramInt2 ^= 0x1;
          j = paramArrayOfint[b2++];
          paramInt2 ^= 0x2;
        case 1:
          if (i < j) {
            i = this.list[b1++];
            paramInt2 ^= 0x1;
            continue;
          } 
          if (j < i) {
            this.buffer[b3++] = j;
            j = paramArrayOfint[b2++];
            paramInt2 ^= 0x2;
            continue;
          } 
          if (i == 1114112)
            break; 
          i = this.list[b1++];
          paramInt2 ^= 0x1;
          j = paramArrayOfint[b2++];
          paramInt2 ^= 0x2;
        case 2:
          if (j < i) {
            j = paramArrayOfint[b2++];
            paramInt2 ^= 0x2;
            continue;
          } 
          if (i < j) {
            this.buffer[b3++] = i;
            i = this.list[b1++];
            paramInt2 ^= 0x1;
            continue;
          } 
          if (i == 1114112)
            break; 
          i = this.list[b1++];
          paramInt2 ^= 0x1;
          j = paramArrayOfint[b2++];
          paramInt2 ^= 0x2;
      } 
    } 
    this.buffer[b3++] = 1114112;
    this.len = b3;
    int[] arrayOfInt = this.list;
    this.list = this.buffer;
    this.buffer = arrayOfInt;
    this.pat = null;
    return this;
  }
  
  private static final int max(int paramInt1, int paramInt2) {
    return (paramInt1 > paramInt2) ? paramInt1 : paramInt2;
  }
  
  static final VersionInfo NO_VERSION = VersionInfo.getInstance(0, 0, 0, 0);
  
  public static final int IGNORE_SPACE = 1;
  
  private static synchronized UnicodeSet getInclusions(int paramInt) {
    if (INCLUSIONS == null)
      INCLUSIONS = new UnicodeSet[9]; 
    if (INCLUSIONS[paramInt] == null) {
      UnicodeSet unicodeSet = new UnicodeSet();
      switch (paramInt) {
        case 2:
          UCharacterProperty.getInstance().upropsvec_addPropertyStarts(unicodeSet);
          break;
        default:
          throw new IllegalStateException("UnicodeSet.getInclusions(unknown src " + paramInt + ")");
      } 
      INCLUSIONS[paramInt] = unicodeSet;
    } 
    return INCLUSIONS[paramInt];
  }
  
  private UnicodeSet applyFilter(Filter paramFilter, int paramInt) {
    clear();
    int i = -1;
    UnicodeSet unicodeSet = getInclusions(paramInt);
    int j = unicodeSet.getRangeCount();
    for (byte b = 0; b < j; b++) {
      int k = unicodeSet.getRangeStart(b);
      int m = unicodeSet.getRangeEnd(b);
      for (int n = k; n <= m; n++) {
        if (paramFilter.contains(n)) {
          if (i < 0)
            i = n; 
        } else if (i >= 0) {
          add_unchecked(i, n - 1);
          i = -1;
        } 
      } 
    } 
    if (i >= 0)
      add_unchecked(i, 1114111); 
    return this;
  }
  
  private static String mungeCharName(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < paramString.length(); ) {
      int j = UTF16.charAt(paramString, i);
      i += UTF16.getCharCount(j);
      if (UCharacterProperty.isRuleWhiteSpace(j)) {
        if (stringBuffer.length() == 0 || stringBuffer
          .charAt(stringBuffer.length() - 1) == ' ')
          continue; 
        j = 32;
      } 
      UTF16.append(stringBuffer, j);
    } 
    if (stringBuffer.length() != 0 && stringBuffer
      .charAt(stringBuffer.length() - 1) == ' ')
      stringBuffer.setLength(stringBuffer.length() - 1); 
    return stringBuffer.toString();
  }
  
  public UnicodeSet applyPropertyAlias(String paramString1, String paramString2, SymbolTable paramSymbolTable) {
    if (paramString2.length() > 0 && 
      paramString1.equals("Age")) {
      VersionInfo versionInfo = VersionInfo.getInstance(mungeCharName(paramString2));
      applyFilter((Filter)new VersionFilter(versionInfo), 2);
      return this;
    } 
    throw new IllegalArgumentException("Unsupported property: " + paramString1);
  }
  
  private static boolean resemblesPropertyPattern(RuleCharacterIterator paramRuleCharacterIterator, int paramInt) {
    boolean bool = false;
    paramInt &= 0xFFFFFFFD;
    Object object = paramRuleCharacterIterator.getPos(null);
    int i = paramRuleCharacterIterator.next(paramInt);
    if (i == 91 || i == 92) {
      int j = paramRuleCharacterIterator.next(paramInt & 0xFFFFFFFB);
      bool = (i == 91) ? ((j == 58) ? true : false) : ((j == 78 || j == 112 || j == 80) ? true : false);
    } 
    paramRuleCharacterIterator.setPos(object);
    return bool;
  }
  
  private UnicodeSet applyPropertyPattern(String paramString, ParsePosition paramParsePosition, SymbolTable paramSymbolTable) {
    String str1, str2;
    int i = paramParsePosition.getIndex();
    if (i + 5 > paramString.length())
      return null; 
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    if (paramString.regionMatches(i, "[:", 0, 2)) {
      bool1 = true;
      i = Utility.skipWhitespace(paramString, i + 2);
      if (i < paramString.length() && paramString.charAt(i) == '^') {
        i++;
        bool3 = true;
      } 
    } else if (paramString.regionMatches(true, i, "\\p", 0, 2) || paramString
      .regionMatches(i, "\\N", 0, 2)) {
      char c = paramString.charAt(i + 1);
      bool3 = (c == 'P') ? true : false;
      bool2 = (c == 'N') ? true : false;
      i = Utility.skipWhitespace(paramString, i + 2);
      if (i == paramString.length() || paramString.charAt(i++) != '{')
        return null; 
    } else {
      return null;
    } 
    int j = paramString.indexOf(bool1 ? ":]" : "}", i);
    if (j < 0)
      return null; 
    int k = paramString.indexOf('=', i);
    if (k >= 0 && k < j && !bool2) {
      str1 = paramString.substring(i, k);
      str2 = paramString.substring(k + 1, j);
    } else {
      str1 = paramString.substring(i, j);
      str2 = "";
      if (bool2) {
        str2 = str1;
        str1 = "na";
      } 
    } 
    applyPropertyAlias(str1, str2, paramSymbolTable);
    if (bool3)
      complement(); 
    paramParsePosition.setIndex(j + (bool1 ? 2 : 1));
    return this;
  }
  
  private void applyPropertyPattern(RuleCharacterIterator paramRuleCharacterIterator, StringBuffer paramStringBuffer, SymbolTable paramSymbolTable) {
    String str = paramRuleCharacterIterator.lookahead();
    ParsePosition parsePosition = new ParsePosition(0);
    applyPropertyPattern(str, parsePosition, paramSymbolTable);
    if (parsePosition.getIndex() == 0)
      syntaxError(paramRuleCharacterIterator, "Invalid property pattern"); 
    paramRuleCharacterIterator.jumpahead(parsePosition.getIndex());
    paramStringBuffer.append(str.substring(0, parsePosition.getIndex()));
  }
  
  private static class UnicodeSet {}
  
  private static interface UnicodeSet {}
}
