package com.ibm.icu.lang;

import com.ibm.icu.impl.IllegalIcuArgumentException;
import com.ibm.icu.impl.Norm2AllModes;
import com.ibm.icu.impl.UBiDiProps;
import com.ibm.icu.impl.UCaseProps;
import com.ibm.icu.impl.UCharacterName;
import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.impl.UCharacterUtility;
import com.ibm.icu.impl.UPropertyAliases;
import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.util.RangeValueIterator;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.ValueIterator;
import com.ibm.icu.util.VersionInfo;
import java.util.Locale;

public final class UCharacter implements UCharacterEnums$ECharacterCategory, UCharacterEnums$ECharacterDirection {
  public static final int MIN_VALUE = 0;
  
  public static final int MAX_VALUE = 1114111;
  
  public static final int SUPPLEMENTARY_MIN_VALUE = 65536;
  
  public static final int REPLACEMENT_CHAR = 65533;
  
  public static final double NO_NUMERIC_VALUE = -1.23456789E8D;
  
  public static final int MIN_RADIX = 2;
  
  public static final int MAX_RADIX = 36;
  
  public static final int TITLECASE_NO_LOWERCASE = 256;
  
  public static final int TITLECASE_NO_BREAK_ADJUSTMENT = 512;
  
  public static final int FOLD_CASE_DEFAULT = 0;
  
  public static final int FOLD_CASE_EXCLUDE_SPECIAL_I = 1;
  
  public static final char MIN_HIGH_SURROGATE = '?';
  
  public static final char MAX_HIGH_SURROGATE = '?';
  
  public static final char MIN_LOW_SURROGATE = '?';
  
  public static final char MAX_LOW_SURROGATE = '?';
  
  public static final char MIN_SURROGATE = '?';
  
  public static final char MAX_SURROGATE = '?';
  
  public static final int MIN_SUPPLEMENTARY_CODE_POINT = 65536;
  
  public static final int MAX_CODE_POINT = 1114111;
  
  public static final int MIN_CODE_POINT = 0;
  
  private static final int LAST_CHAR_MASK_ = 65535;
  
  private static final int NO_BREAK_SPACE_ = 160;
  
  private static final int FIGURE_SPACE_ = 8199;
  
  private static final int NARROW_NO_BREAK_SPACE_ = 8239;
  
  private static final int IDEOGRAPHIC_NUMBER_ZERO_ = 12295;
  
  private static final int CJK_IDEOGRAPH_FIRST_ = 19968;
  
  private static final int CJK_IDEOGRAPH_SECOND_ = 20108;
  
  private static final int CJK_IDEOGRAPH_THIRD_ = 19977;
  
  private static final int CJK_IDEOGRAPH_FOURTH_ = 22235;
  
  private static final int CJK_IDEOGRAPH_FIFTH_ = 20116;
  
  private static final int CJK_IDEOGRAPH_SIXTH_ = 20845;
  
  private static final int CJK_IDEOGRAPH_SEVENTH_ = 19971;
  
  private static final int CJK_IDEOGRAPH_EIGHTH_ = 20843;
  
  private static final int CJK_IDEOGRAPH_NINETH_ = 20061;
  
  private static final int APPLICATION_PROGRAM_COMMAND_ = 159;
  
  private static final int UNIT_SEPARATOR_ = 31;
  
  private static final int DELETE_ = 127;
  
  private static final int CJK_IDEOGRAPH_COMPLEX_ZERO_ = 38646;
  
  private static final int CJK_IDEOGRAPH_COMPLEX_ONE_ = 22777;
  
  private static final int CJK_IDEOGRAPH_COMPLEX_TWO_ = 36019;
  
  private static final int CJK_IDEOGRAPH_COMPLEX_THREE_ = 21443;
  
  private static final int CJK_IDEOGRAPH_COMPLEX_FOUR_ = 32902;
  
  private static final int CJK_IDEOGRAPH_COMPLEX_FIVE_ = 20237;
  
  private static final int CJK_IDEOGRAPH_COMPLEX_SIX_ = 38520;
  
  private static final int CJK_IDEOGRAPH_COMPLEX_SEVEN_ = 26578;
  
  private static final int CJK_IDEOGRAPH_COMPLEX_EIGHT_ = 25420;
  
  private static final int CJK_IDEOGRAPH_COMPLEX_NINE_ = 29590;
  
  private static final int CJK_IDEOGRAPH_TEN_ = 21313;
  
  private static final int CJK_IDEOGRAPH_COMPLEX_TEN_ = 25342;
  
  private static final int CJK_IDEOGRAPH_HUNDRED_ = 30334;
  
  private static final int CJK_IDEOGRAPH_COMPLEX_HUNDRED_ = 20336;
  
  private static final int CJK_IDEOGRAPH_THOUSAND_ = 21315;
  
  private static final int CJK_IDEOGRAPH_COMPLEX_THOUSAND_ = 20191;
  
  private static final int CJK_IDEOGRAPH_TEN_THOUSAND_ = 33356;
  
  private static final int CJK_IDEOGRAPH_HUNDRED_MILLION_ = 20740;
  
  public static int digit(int ch, int radix) {
    if (2 <= radix && radix <= 36) {
      int value = digit(ch);
      if (value < 0)
        value = UCharacterProperty.getEuropeanDigit(ch); 
      return (value < radix) ? value : -1;
    } 
    return -1;
  }
  
  public static int digit(int ch) {
    return UCharacterProperty.INSTANCE.digit(ch);
  }
  
  public static int getNumericValue(int ch) {
    return UCharacterProperty.INSTANCE.getNumericValue(ch);
  }
  
  public static double getUnicodeNumericValue(int ch) {
    return UCharacterProperty.INSTANCE.getUnicodeNumericValue(ch);
  }
  
  public static boolean isSpace(int ch) {
    return (ch <= 32 && (ch == 32 || ch == 9 || ch == 10 || ch == 12 || ch == 13));
  }
  
  public static int getType(int ch) {
    return UCharacterProperty.INSTANCE.getType(ch);
  }
  
  public static boolean isDefined(int ch) {
    return (getType(ch) != 0);
  }
  
  public static boolean isDigit(int ch) {
    return (getType(ch) == 9);
  }
  
  public static boolean isISOControl(int ch) {
    return (ch >= 0 && ch <= 159 && (ch <= 31 || ch >= 127));
  }
  
  public static boolean isLetter(int ch) {
    return ((1 << getType(ch) & 0x3E) != 0);
  }
  
  public static boolean isLetterOrDigit(int ch) {
    return ((1 << getType(ch) & 0x23E) != 0);
  }
  
  public static boolean isJavaLetter(int cp) {
    return isJavaIdentifierStart(cp);
  }
  
  public static boolean isJavaLetterOrDigit(int cp) {
    return isJavaIdentifierPart(cp);
  }
  
  public static boolean isJavaIdentifierStart(int cp) {
    return Character.isJavaIdentifierStart((char)cp);
  }
  
  public static boolean isJavaIdentifierPart(int cp) {
    return Character.isJavaIdentifierPart((char)cp);
  }
  
  public static boolean isLowerCase(int ch) {
    return (getType(ch) == 2);
  }
  
  public static boolean isWhitespace(int ch) {
    return (((1 << getType(ch) & 0x7000) != 0 && ch != 160 && ch != 8199 && ch != 8239) || (ch >= 9 && ch <= 13) || (ch >= 28 && ch <= 31));
  }
  
  public static boolean isSpaceChar(int ch) {
    return ((1 << getType(ch) & 0x7000) != 0);
  }
  
  public static boolean isTitleCase(int ch) {
    return (getType(ch) == 3);
  }
  
  public static boolean isUnicodeIdentifierPart(int ch) {
    return ((1 << getType(ch) & 0x40077E) != 0 || isIdentifierIgnorable(ch));
  }
  
  public static boolean isUnicodeIdentifierStart(int ch) {
    return ((1 << getType(ch) & 0x43E) != 0);
  }
  
  public static boolean isIdentifierIgnorable(int ch) {
    if (ch <= 159)
      return (isISOControl(ch) && (ch < 9 || ch > 13) && (ch < 28 || ch > 31)); 
    return (getType(ch) == 16);
  }
  
  public static boolean isUpperCase(int ch) {
    return (getType(ch) == 1);
  }
  
  public static int toLowerCase(int ch) {
    return UCaseProps.INSTANCE.tolower(ch);
  }
  
  public static String toString(int ch) {
    if (ch < 0 || ch > 1114111)
      return null; 
    if (ch < 65536)
      return String.valueOf((char)ch); 
    StringBuilder result = new StringBuilder();
    result.append(UTF16.getLeadSurrogate(ch));
    result.append(UTF16.getTrailSurrogate(ch));
    return result.toString();
  }
  
  public static int toTitleCase(int ch) {
    return UCaseProps.INSTANCE.totitle(ch);
  }
  
  public static int toUpperCase(int ch) {
    return UCaseProps.INSTANCE.toupper(ch);
  }
  
  public static boolean isSupplementary(int ch) {
    return (ch >= 65536 && ch <= 1114111);
  }
  
  public static boolean isBMP(int ch) {
    return (ch >= 0 && ch <= 65535);
  }
  
  public static boolean isPrintable(int ch) {
    int cat = getType(ch);
    return (cat != 0 && cat != 15 && cat != 16 && cat != 17 && cat != 18 && cat != 0);
  }
  
  public static boolean isBaseForm(int ch) {
    int cat = getType(ch);
    return (cat == 9 || cat == 11 || cat == 10 || cat == 1 || cat == 2 || cat == 3 || cat == 4 || cat == 5 || cat == 6 || cat == 7 || cat == 8);
  }
  
  public static int getDirection(int ch) {
    return UBiDiProps.INSTANCE.getClass(ch);
  }
  
  public static boolean isMirrored(int ch) {
    return UBiDiProps.INSTANCE.isMirrored(ch);
  }
  
  public static int getMirror(int ch) {
    return UBiDiProps.INSTANCE.getMirror(ch);
  }
  
  public static int getCombiningClass(int ch) {
    return (Norm2AllModes.getNFCInstance()).decomp.getCombiningClass(ch);
  }
  
  public static boolean isLegal(int ch) {
    if (ch < 0)
      return false; 
    if (ch < 55296)
      return true; 
    if (ch <= 57343)
      return false; 
    if (UCharacterUtility.isNonCharacter(ch))
      return false; 
    return (ch <= 1114111);
  }
  
  public static boolean isLegal(String str) {
    int size = str.length();
    for (int i = 0; i < size; i++) {
      int codepoint = UTF16.charAt(str, i);
      if (!isLegal(codepoint))
        return false; 
      if (isSupplementary(codepoint))
        i++; 
    } 
    return true;
  }
  
  public static VersionInfo getUnicodeVersion() {
    return UCharacterProperty.INSTANCE.m_unicodeVersion_;
  }
  
  public static String getName(int ch) {
    return UCharacterName.INSTANCE.getName(ch, 0);
  }
  
  public static String getName(String s, String separator) {
    if (s.length() == 1)
      return getName(s.charAt(0)); 
    StringBuilder sb = new StringBuilder();
    int i;
    for (i = 0; i < s.length(); i += UTF16.getCharCount(cp)) {
      int cp = UTF16.charAt(s, i);
      if (i != 0)
        sb.append(separator); 
      sb.append(getName(cp));
    } 
    return sb.toString();
  }
  
  public static String getName1_0(int ch) {
    return null;
  }
  
  public static String getExtendedName(int ch) {
    return UCharacterName.INSTANCE.getName(ch, 2);
  }
  
  public static String getNameAlias(int ch) {
    return UCharacterName.INSTANCE.getName(ch, 3);
  }
  
  public static String getISOComment(int ch) {
    return null;
  }
  
  public static int getCharFromName(String name) {
    return UCharacterName.INSTANCE.getCharFromName(0, name);
  }
  
  public static int getCharFromName1_0(String name) {
    return -1;
  }
  
  public static int getCharFromExtendedName(String name) {
    return UCharacterName.INSTANCE.getCharFromName(2, name);
  }
  
  public static int getCharFromNameAlias(String name) {
    return UCharacterName.INSTANCE.getCharFromName(3, name);
  }
  
  public static String getPropertyName(int property, int nameChoice) {
    return UPropertyAliases.INSTANCE.getPropertyName(property, nameChoice);
  }
  
  public static int getPropertyEnum(CharSequence propertyAlias) {
    int propEnum = UPropertyAliases.INSTANCE.getPropertyEnum(propertyAlias);
    if (propEnum == -1)
      throw new IllegalIcuArgumentException("Invalid name: " + propertyAlias); 
    return propEnum;
  }
  
  public static String getPropertyValueName(int property, int value, int nameChoice) {
    if ((property == 4098 || property == 4112 || property == 4113) && value >= getIntPropertyMinValue(4098) && value <= getIntPropertyMaxValue(4098) && nameChoice >= 0 && nameChoice < 2)
      try {
        return UPropertyAliases.INSTANCE.getPropertyValueName(property, value, nameChoice);
      } catch (IllegalArgumentException e) {
        return null;
      }  
    return UPropertyAliases.INSTANCE.getPropertyValueName(property, value, nameChoice);
  }
  
  public static int getPropertyValueEnum(int property, CharSequence valueAlias) {
    int propEnum = UPropertyAliases.INSTANCE.getPropertyValueEnum(property, valueAlias);
    if (propEnum == -1)
      throw new IllegalIcuArgumentException("Invalid name: " + valueAlias); 
    return propEnum;
  }
  
  public static int getCodePoint(char lead, char trail) {
    if (UTF16.isLeadSurrogate(lead) && UTF16.isTrailSurrogate(trail))
      return UCharacterProperty.getRawSupplementary(lead, trail); 
    throw new IllegalArgumentException("Illegal surrogate characters");
  }
  
  public static int getCodePoint(char char16) {
    if (isLegal(char16))
      return char16; 
    throw new IllegalArgumentException("Illegal codepoint");
  }
  
  public static String toUpperCase(String str) {
    return toUpperCase(ULocale.getDefault(), str);
  }
  
  public static String toLowerCase(String str) {
    return toLowerCase(ULocale.getDefault(), str);
  }
  
  public static String toTitleCase(String str, BreakIterator breakiter) {
    return toTitleCase(ULocale.getDefault(), str, breakiter);
  }
  
  public static String toUpperCase(Locale locale, String str) {
    return toUpperCase(ULocale.forLocale(locale), str);
  }
  
  public static String toUpperCase(ULocale locale, String str) {
    StringContextIterator iter = new StringContextIterator(str);
    StringBuilder result = new StringBuilder(str.length());
    int[] locCache = new int[1];
    if (locale == null)
      locale = ULocale.getDefault(); 
    locCache[0] = 0;
    int c;
    while ((c = iter.nextCaseMapCP()) >= 0) {
      c = UCaseProps.INSTANCE.toFullUpper(c, (UCaseProps.ContextIterator)iter, result, locale, locCache);
      if (c < 0) {
        c ^= 0xFFFFFFFF;
      } else if (c <= 31) {
        continue;
      } 
      result.appendCodePoint(c);
    } 
    return result.toString();
  }
  
  public static String toLowerCase(Locale locale, String str) {
    return toLowerCase(ULocale.forLocale(locale), str);
  }
  
  public static String toLowerCase(ULocale locale, String str) {
    StringContextIterator iter = new StringContextIterator(str);
    StringBuilder result = new StringBuilder(str.length());
    int[] locCache = new int[1];
    if (locale == null)
      locale = ULocale.getDefault(); 
    locCache[0] = 0;
    int c;
    while ((c = iter.nextCaseMapCP()) >= 0) {
      c = UCaseProps.INSTANCE.toFullLower(c, (UCaseProps.ContextIterator)iter, result, locale, locCache);
      if (c < 0) {
        c ^= 0xFFFFFFFF;
      } else if (c <= 31) {
        continue;
      } 
      result.appendCodePoint(c);
    } 
    return result.toString();
  }
  
  public static String toTitleCase(Locale locale, String str, BreakIterator breakiter) {
    return toTitleCase(ULocale.forLocale(locale), str, breakiter);
  }
  
  public static String toTitleCase(ULocale locale, String str, BreakIterator titleIter) {
    return toTitleCase(locale, str, titleIter, 0);
  }
  
  public static String toTitleCase(ULocale locale, String str, BreakIterator titleIter, int options) {
    StringContextIterator iter = new StringContextIterator(str);
    StringBuilder result = new StringBuilder(str.length());
    int[] locCache = new int[1];
    int srcLength = str.length();
    if (locale == null)
      locale = ULocale.getDefault(); 
    locCache[0] = 0;
    if (titleIter == null)
      titleIter = BreakIterator.getWordInstance(locale); 
    titleIter.setText(str);
    boolean isDutch = locale.getLanguage().equals("nl");
    boolean FirstIJ = true;
    int prev = 0;
    boolean isFirstIndex = true;
    while (prev < srcLength) {
      int index;
      if (isFirstIndex) {
        isFirstIndex = false;
        index = titleIter.first();
      } else {
        index = titleIter.next();
      } 
      if (index == -1 || index > srcLength)
        index = srcLength; 
      if (prev < index) {
        int titleStart;
        iter.setLimit(index);
        int c = iter.nextCaseMapCP();
        if ((options & 0x200) == 0 && 0 == UCaseProps.INSTANCE.getType(c)) {
          while ((c = iter.nextCaseMapCP()) >= 0 && 0 == UCaseProps.INSTANCE.getType(c));
          titleStart = iter.getCPStart();
          if (prev < titleStart)
            result.append(str, prev, titleStart); 
        } else {
          titleStart = prev;
        } 
        if (titleStart < index) {
          FirstIJ = true;
          c = UCaseProps.INSTANCE.toFullTitle(c, (UCaseProps.ContextIterator)iter, result, locale, locCache);
          while (true) {
            if (c < 0) {
              c ^= 0xFFFFFFFF;
              result.appendCodePoint(c);
            } else if (c > 31) {
              result.appendCodePoint(c);
            } 
            if ((options & 0x100) != 0) {
              int titleLimit = iter.getCPLimit();
              if (titleLimit < index) {
                String appendStr = str.substring(titleLimit, index);
                if (isDutch && c == 73 && appendStr.startsWith("j"))
                  appendStr = "J" + appendStr.substring(1); 
                result.append(appendStr);
              } 
              iter.moveToLimit();
              break;
            } 
            int nc;
            if ((nc = iter.nextCaseMapCP()) >= 0) {
              if (isDutch && (nc == 74 || nc == 106) && c == 73 && FirstIJ == true) {
                c = 74;
                FirstIJ = false;
                continue;
              } 
              c = UCaseProps.INSTANCE.toFullLower(nc, (UCaseProps.ContextIterator)iter, result, locale, locCache);
              continue;
            } 
            break;
          } 
        } 
      } 
      prev = index;
    } 
    return result.toString();
  }
  
  public static int foldCase(int ch, boolean defaultmapping) {
    return foldCase(ch, defaultmapping ? 0 : 1);
  }
  
  public static String foldCase(String str, boolean defaultmapping) {
    return foldCase(str, defaultmapping ? 0 : 1);
  }
  
  public static int foldCase(int ch, int options) {
    return UCaseProps.INSTANCE.fold(ch, options);
  }
  
  public static final String foldCase(String str, int options) {
    StringBuilder result = new StringBuilder(str.length());
    int length = str.length();
    for (int i = 0; i < length; ) {
      int c = UTF16.charAt(str, i);
      i += UTF16.getCharCount(c);
      c = UCaseProps.INSTANCE.toFullFolding(c, result, options);
      if (c < 0) {
        c ^= 0xFFFFFFFF;
      } else if (c <= 31) {
        continue;
      } 
      result.appendCodePoint(c);
    } 
    return result.toString();
  }
  
  public static int getHanNumericValue(int ch) {
    switch (ch) {
      case 12295:
      case 38646:
        return 0;
      case 19968:
      case 22777:
        return 1;
      case 20108:
      case 36019:
        return 2;
      case 19977:
      case 21443:
        return 3;
      case 22235:
      case 32902:
        return 4;
      case 20116:
      case 20237:
        return 5;
      case 20845:
      case 38520:
        return 6;
      case 19971:
      case 26578:
        return 7;
      case 20843:
      case 25420:
        return 8;
      case 20061:
      case 29590:
        return 9;
      case 21313:
      case 25342:
        return 10;
      case 20336:
      case 30334:
        return 100;
      case 20191:
      case 21315:
        return 1000;
      case 33356:
        return 10000;
      case 20740:
        return 100000000;
    } 
    return -1;
  }
  
  public static RangeValueIterator getTypeIterator() {
    return (RangeValueIterator)new UCharacterTypeIterator();
  }
  
  public static ValueIterator getNameIterator() {
    return (ValueIterator)new UCharacterNameIterator(UCharacterName.INSTANCE, 0);
  }
  
  public static ValueIterator getName1_0Iterator() {
    return (ValueIterator)new DummyValueIterator(null);
  }
  
  public static ValueIterator getExtendedNameIterator() {
    return (ValueIterator)new UCharacterNameIterator(UCharacterName.INSTANCE, 2);
  }
  
  public static VersionInfo getAge(int ch) {
    if (ch < 0 || ch > 1114111)
      throw new IllegalArgumentException("Codepoint out of bounds"); 
    return UCharacterProperty.INSTANCE.getAge(ch);
  }
  
  public static boolean hasBinaryProperty(int ch, int property) {
    return UCharacterProperty.INSTANCE.hasBinaryProperty(ch, property);
  }
  
  public static boolean isUAlphabetic(int ch) {
    return hasBinaryProperty(ch, 0);
  }
  
  public static boolean isULowercase(int ch) {
    return hasBinaryProperty(ch, 22);
  }
  
  public static boolean isUUppercase(int ch) {
    return hasBinaryProperty(ch, 30);
  }
  
  public static boolean isUWhiteSpace(int ch) {
    return hasBinaryProperty(ch, 31);
  }
  
  public static int getIntPropertyValue(int ch, int type) {
    return UCharacterProperty.INSTANCE.getIntPropertyValue(ch, type);
  }
  
  public static String getStringPropertyValue(int propertyEnum, int codepoint, int nameChoice) {
    if ((propertyEnum >= 0 && propertyEnum < 57) || (propertyEnum >= 4096 && propertyEnum < 4117))
      return getPropertyValueName(propertyEnum, getIntPropertyValue(codepoint, propertyEnum), nameChoice); 
    if (propertyEnum == 12288)
      return String.valueOf(getUnicodeNumericValue(codepoint)); 
    switch (propertyEnum) {
      case 16384:
        return getAge(codepoint).toString();
      case 16387:
        return getISOComment(codepoint);
      case 16385:
        return UTF16.valueOf(getMirror(codepoint));
      case 16386:
        return foldCase(UTF16.valueOf(codepoint), true);
      case 16388:
        return toLowerCase(UTF16.valueOf(codepoint));
      case 16389:
        return getName(codepoint);
      case 16390:
        return UTF16.valueOf(foldCase(codepoint, true));
      case 16391:
        return UTF16.valueOf(toLowerCase(codepoint));
      case 16392:
        return UTF16.valueOf(toTitleCase(codepoint));
      case 16393:
        return UTF16.valueOf(toUpperCase(codepoint));
      case 16394:
        return toTitleCase(UTF16.valueOf(codepoint), null);
      case 16395:
        return getName1_0(codepoint);
      case 16396:
        return toUpperCase(UTF16.valueOf(codepoint));
    } 
    throw new IllegalArgumentException("Illegal Property Enum");
  }
  
  public static int getIntPropertyMinValue(int type) {
    return 0;
  }
  
  public static int getIntPropertyMaxValue(int type) {
    return UCharacterProperty.INSTANCE.getIntPropertyMaxValue(type);
  }
  
  public static char forDigit(int digit, int radix) {
    return Character.forDigit(digit, radix);
  }
  
  public static final boolean isValidCodePoint(int cp) {
    return (cp >= 0 && cp <= 1114111);
  }
  
  public static final boolean isSupplementaryCodePoint(int cp) {
    return (cp >= 65536 && cp <= 1114111);
  }
  
  public static boolean isHighSurrogate(char ch) {
    return (ch >= '?' && ch <= '?');
  }
  
  public static boolean isLowSurrogate(char ch) {
    return (ch >= '?' && ch <= '?');
  }
  
  public static final boolean isSurrogatePair(char high, char low) {
    return (isHighSurrogate(high) && isLowSurrogate(low));
  }
  
  public static int charCount(int cp) {
    return UTF16.getCharCount(cp);
  }
  
  public static final int toCodePoint(char high, char low) {
    return UCharacterProperty.getRawSupplementary(high, low);
  }
  
  public static final int codePointAt(CharSequence seq, int index) {
    char c1 = seq.charAt(index++);
    if (isHighSurrogate(c1) && 
      index < seq.length()) {
      char c2 = seq.charAt(index);
      if (isLowSurrogate(c2))
        return toCodePoint(c1, c2); 
    } 
    return c1;
  }
  
  public static final int codePointAt(char[] text, int index) {
    char c1 = text[index++];
    if (isHighSurrogate(c1) && 
      index < text.length) {
      char c2 = text[index];
      if (isLowSurrogate(c2))
        return toCodePoint(c1, c2); 
    } 
    return c1;
  }
  
  public static final int codePointAt(char[] text, int index, int limit) {
    if (index >= limit || limit > text.length)
      throw new IndexOutOfBoundsException(); 
    char c1 = text[index++];
    if (isHighSurrogate(c1) && 
      index < limit) {
      char c2 = text[index];
      if (isLowSurrogate(c2))
        return toCodePoint(c1, c2); 
    } 
    return c1;
  }
  
  public static final int codePointBefore(CharSequence seq, int index) {
    char c2 = seq.charAt(--index);
    if (isLowSurrogate(c2) && 
      index > 0) {
      char c1 = seq.charAt(--index);
      if (isHighSurrogate(c1))
        return toCodePoint(c1, c2); 
    } 
    return c2;
  }
  
  public static final int codePointBefore(char[] text, int index) {
    char c2 = text[--index];
    if (isLowSurrogate(c2) && 
      index > 0) {
      char c1 = text[--index];
      if (isHighSurrogate(c1))
        return toCodePoint(c1, c2); 
    } 
    return c2;
  }
  
  public static final int codePointBefore(char[] text, int index, int limit) {
    if (index <= limit || limit < 0)
      throw new IndexOutOfBoundsException(); 
    char c2 = text[--index];
    if (isLowSurrogate(c2) && 
      index > limit) {
      char c1 = text[--index];
      if (isHighSurrogate(c1))
        return toCodePoint(c1, c2); 
    } 
    return c2;
  }
  
  public static final int toChars(int cp, char[] dst, int dstIndex) {
    if (cp >= 0) {
      if (cp < 65536) {
        dst[dstIndex] = (char)cp;
        return 1;
      } 
      if (cp <= 1114111) {
        dst[dstIndex] = UTF16.getLeadSurrogate(cp);
        dst[dstIndex + 1] = UTF16.getTrailSurrogate(cp);
        return 2;
      } 
    } 
    throw new IllegalArgumentException();
  }
  
  public static final char[] toChars(int cp) {
    if (cp >= 0) {
      if (cp < 65536)
        return new char[] { (char)cp }; 
      if (cp <= 1114111)
        return new char[] { UTF16.getLeadSurrogate(cp), UTF16.getTrailSurrogate(cp) }; 
    } 
    throw new IllegalArgumentException();
  }
  
  public static byte getDirectionality(int cp) {
    return (byte)getDirection(cp);
  }
  
  public static int codePointCount(CharSequence text, int start, int limit) {
    if (start < 0 || limit < start || limit > text.length())
      throw new IndexOutOfBoundsException("start (" + start + ") or limit (" + limit + ") invalid or out of range 0, " + text.length()); 
    int len = limit - start;
    while (limit > start) {
      char ch = text.charAt(--limit);
      while (ch >= '?' && ch <= '?' && limit > start) {
        ch = text.charAt(--limit);
        if (ch >= '?' && ch <= '?')
          len--; 
      } 
    } 
    return len;
  }
  
  public static int codePointCount(char[] text, int start, int limit) {
    if (start < 0 || limit < start || limit > text.length)
      throw new IndexOutOfBoundsException("start (" + start + ") or limit (" + limit + ") invalid or out of range 0, " + text.length); 
    int len = limit - start;
    while (limit > start) {
      char ch = text[--limit];
      while (ch >= '?' && ch <= '?' && limit > start) {
        ch = text[--limit];
        if (ch >= '?' && ch <= '?')
          len--; 
      } 
    } 
    return len;
  }
  
  public static int offsetByCodePoints(CharSequence text, int index, int codePointOffset) {
    if (index < 0 || index > text.length())
      throw new IndexOutOfBoundsException("index ( " + index + ") out of range 0, " + text.length()); 
    if (codePointOffset < 0) {
      while (++codePointOffset <= 0) {
        char ch = text.charAt(--index);
        while (ch >= '?' && ch <= '?' && index > 0) {
          ch = text.charAt(--index);
          if ((ch < '?' || ch > '?') && 
            ++codePointOffset > 0)
            return index + 1; 
        } 
      } 
    } else {
      int limit = text.length();
      while (--codePointOffset >= 0) {
        char ch = text.charAt(index++);
        while (ch >= '?' && ch <= '?' && index < limit) {
          ch = text.charAt(index++);
          if ((ch < '?' || ch > '?') && 
            --codePointOffset < 0)
            return index - 1; 
        } 
      } 
    } 
    return index;
  }
  
  public static int offsetByCodePoints(char[] text, int start, int count, int index, int codePointOffset) {
    int limit = start + count;
    if (start < 0 || limit < start || limit > text.length || index < start || index > limit)
      throw new IndexOutOfBoundsException("index ( " + index + ") out of range " + start + ", " + limit + " in array 0, " + text.length); 
    if (codePointOffset < 0) {
      while (++codePointOffset <= 0) {
        char ch = text[--index];
        if (index < start)
          throw new IndexOutOfBoundsException("index ( " + index + ") < start (" + start + ")"); 
        while (ch >= '?' && ch <= '?' && index > start) {
          ch = text[--index];
          if ((ch < '?' || ch > '?') && 
            ++codePointOffset > 0)
            return index + 1; 
        } 
      } 
    } else {
      while (--codePointOffset >= 0) {
        char ch = text[index++];
        if (index > limit)
          throw new IndexOutOfBoundsException("index ( " + index + ") > limit (" + limit + ")"); 
        while (ch >= '?' && ch <= '?' && index < limit) {
          ch = text[index++];
          if ((ch < '?' || ch > '?') && 
            --codePointOffset < 0)
            return index - 1; 
        } 
      } 
    } 
    return index;
  }
  
  private static final class UCharacter {}
  
  private static final class UCharacter {}
  
  private static class UCharacter {}
  
  public static interface UCharacter {}
  
  public static interface UCharacter {}
  
  public static interface UCharacter {}
  
  public static interface UCharacter {}
  
  public static interface UCharacter {}
  
  public static interface UCharacter {}
  
  public static interface UCharacter {}
  
  public static interface UCharacter {}
  
  public static interface UCharacter {}
  
  public static interface UCharacter {}
  
  public static final class UCharacter {}
}
