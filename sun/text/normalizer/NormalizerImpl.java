package sun.text.normalizer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class NormalizerImpl {
  static final NormalizerImpl IMPL;
  
  static final int UNSIGNED_BYTE_MASK = 255;
  
  static final long UNSIGNED_INT_MASK = 4294967295L;
  
  private static final String DATA_FILE_NAME = "/sun/text/resources/unorm.icu";
  
  public static final int QC_NFC = 17;
  
  public static final int QC_NFKC = 34;
  
  public static final int QC_NFD = 4;
  
  public static final int QC_NFKD = 8;
  
  public static final int QC_ANY_NO = 15;
  
  public static final int QC_MAYBE = 16;
  
  public static final int QC_ANY_MAYBE = 48;
  
  public static final int QC_MASK = 63;
  
  private static final int COMBINES_FWD = 64;
  
  private static final int COMBINES_BACK = 128;
  
  public static final int COMBINES_ANY = 192;
  
  private static final int CC_SHIFT = 8;
  
  public static final int CC_MASK = 65280;
  
  private static final int EXTRA_SHIFT = 16;
  
  private static final long MIN_SPECIAL = 4227858432L;
  
  private static final long SURROGATES_TOP = 4293918720L;
  
  private static final long MIN_HANGUL = 4293918720L;
  
  private static final long JAMO_V_TOP = 4294115328L;
  
  static final int INDEX_TRIE_SIZE = 0;
  
  static final int INDEX_CHAR_COUNT = 1;
  
  static final int INDEX_COMBINE_DATA_COUNT = 2;
  
  public static final int INDEX_MIN_NFC_NO_MAYBE = 6;
  
  public static final int INDEX_MIN_NFKC_NO_MAYBE = 7;
  
  public static final int INDEX_MIN_NFD_NO_MAYBE = 8;
  
  public static final int INDEX_MIN_NFKD_NO_MAYBE = 9;
  
  static final int INDEX_FCD_TRIE_SIZE = 10;
  
  static final int INDEX_AUX_TRIE_SIZE = 11;
  
  static final int INDEX_TOP = 32;
  
  private static final int AUX_UNSAFE_SHIFT = 11;
  
  private static final int AUX_COMP_EX_SHIFT = 10;
  
  private static final int AUX_NFC_SKIPPABLE_F_SHIFT = 12;
  
  private static final int AUX_MAX_FNC = 1024;
  
  private static final int AUX_UNSAFE_MASK = 2048;
  
  private static final int AUX_FNC_MASK = 1023;
  
  private static final int AUX_COMP_EX_MASK = 1024;
  
  private static final long AUX_NFC_SKIP_F_MASK = 4096L;
  
  private static final int MAX_BUFFER_SIZE = 20;
  
  private static FCDTrieImpl fcdTrieImpl;
  
  private static NormTrieImpl normTrieImpl;
  
  private static AuxTrieImpl auxTrieImpl;
  
  private static int[] indexes;
  
  private static char[] combiningTable;
  
  private static char[] extraData;
  
  private static boolean isDataLoaded;
  
  private static boolean isFormatVersion_2_1;
  
  private static boolean isFormatVersion_2_2;
  
  private static byte[] unicodeVersion;
  
  private static final int DATA_BUFFER_SIZE = 25000;
  
  public static final int MIN_WITH_LEAD_CC = 768;
  
  private static final int DECOMP_FLAG_LENGTH_HAS_CC = 128;
  
  private static final int DECOMP_LENGTH_MASK = 127;
  
  private static final int BMP_INDEX_LENGTH = 2048;
  
  private static final int SURROGATE_BLOCK_BITS = 5;
  
  public static final int JAMO_L_BASE = 4352;
  
  public static final int JAMO_V_BASE = 4449;
  
  public static final int JAMO_T_BASE = 4519;
  
  public static final int HANGUL_BASE = 44032;
  
  public static final int JAMO_L_COUNT = 19;
  
  public static final int JAMO_V_COUNT = 21;
  
  public static final int JAMO_T_COUNT = 28;
  
  public static final int HANGUL_COUNT = 11172;
  
  private static final int OPTIONS_NX_MASK = 31;
  
  private static final int OPTIONS_UNICODE_MASK = 224;
  
  public static final int OPTIONS_SETS_MASK = 255;
  
  static {
    try {
      IMPL = new NormalizerImpl();
    } catch (Exception exception) {
      throw new RuntimeException(exception.getMessage());
    } 
  }
  
  static final class NormTrieImpl implements Trie.DataManipulate {
    static IntTrie normTrie = null;
    
    public int getFoldingOffset(int param1Int) {
      return 2048 + (param1Int >> 11 & 0x7FE0);
    }
  }
  
  static final class FCDTrieImpl implements Trie.DataManipulate {
    static CharTrie fcdTrie = null;
    
    public int getFoldingOffset(int param1Int) {
      return param1Int;
    }
  }
  
  static final class AuxTrieImpl implements Trie.DataManipulate {
    static CharTrie auxTrie = null;
    
    public int getFoldingOffset(int param1Int) {
      return (param1Int & 0x3FF) << 5;
    }
  }
  
  public static int getFromIndexesArr(int paramInt) {
    return indexes[paramInt];
  }
  
  private NormalizerImpl() throws IOException {
    if (!isDataLoaded) {
      InputStream inputStream = ICUData.getRequiredStream("/sun/text/resources/unorm.icu");
      BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 25000);
      NormalizerDataReader normalizerDataReader = new NormalizerDataReader(bufferedInputStream);
      indexes = normalizerDataReader.readIndexes(32);
      byte[] arrayOfByte1 = new byte[indexes[0]];
      int i = indexes[2];
      combiningTable = new char[i];
      int j = indexes[1];
      extraData = new char[j];
      byte[] arrayOfByte2 = new byte[indexes[10]];
      byte[] arrayOfByte3 = new byte[indexes[11]];
      fcdTrieImpl = new FCDTrieImpl();
      normTrieImpl = new NormTrieImpl();
      auxTrieImpl = new AuxTrieImpl();
      normalizerDataReader.read(arrayOfByte1, arrayOfByte2, arrayOfByte3, extraData, combiningTable);
      NormTrieImpl.normTrie = new IntTrie(new ByteArrayInputStream(arrayOfByte1), normTrieImpl);
      FCDTrieImpl.fcdTrie = new CharTrie(new ByteArrayInputStream(arrayOfByte2), fcdTrieImpl);
      AuxTrieImpl.auxTrie = new CharTrie(new ByteArrayInputStream(arrayOfByte3), auxTrieImpl);
      isDataLoaded = true;
      byte[] arrayOfByte4 = normalizerDataReader.getDataFormatVersion();
      isFormatVersion_2_1 = (arrayOfByte4[0] > 2 || (arrayOfByte4[0] == 2 && arrayOfByte4[1] >= 1));
      isFormatVersion_2_2 = (arrayOfByte4[0] > 2 || (arrayOfByte4[0] == 2 && arrayOfByte4[1] >= 2));
      unicodeVersion = normalizerDataReader.getUnicodeVersion();
      bufferedInputStream.close();
    } 
  }
  
  private static boolean isHangulWithoutJamoT(char paramChar) {
    paramChar = (char)(paramChar - 44032);
    return (paramChar < '⮤' && paramChar % 28 == 0);
  }
  
  private static boolean isNorm32Regular(long paramLong) {
    return (paramLong < 4227858432L);
  }
  
  private static boolean isNorm32LeadSurrogate(long paramLong) {
    return (4227858432L <= paramLong && paramLong < 4293918720L);
  }
  
  private static boolean isNorm32HangulOrJamo(long paramLong) {
    return (paramLong >= 4293918720L);
  }
  
  private static boolean isJamoVTNorm32JamoV(long paramLong) {
    return (paramLong < 4294115328L);
  }
  
  public static long getNorm32(char paramChar) {
    return 0xFFFFFFFFL & NormTrieImpl.normTrie.getLeadValue(paramChar);
  }
  
  public static long getNorm32FromSurrogatePair(long paramLong, char paramChar) {
    return 0xFFFFFFFFL & NormTrieImpl.normTrie.getTrailValue((int)paramLong, paramChar);
  }
  
  private static long getNorm32(int paramInt) {
    return 0xFFFFFFFFL & NormTrieImpl.normTrie.getCodePointValue(paramInt);
  }
  
  private static long getNorm32(char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    long l = getNorm32(paramArrayOfchar[paramInt1]);
    if ((l & paramInt2) > 0L && isNorm32LeadSurrogate(l))
      l = getNorm32FromSurrogatePair(l, paramArrayOfchar[paramInt1 + 1]); 
    return l;
  }
  
  public static VersionInfo getUnicodeVersion() {
    return VersionInfo.getInstance(unicodeVersion[0], unicodeVersion[1], unicodeVersion[2], unicodeVersion[3]);
  }
  
  public static char getFCD16(char paramChar) {
    return FCDTrieImpl.fcdTrie.getLeadValue(paramChar);
  }
  
  public static char getFCD16FromSurrogatePair(char paramChar1, char paramChar2) {
    return FCDTrieImpl.fcdTrie.getTrailValue(paramChar1, paramChar2);
  }
  
  public static int getFCD16(int paramInt) {
    return FCDTrieImpl.fcdTrie.getCodePointValue(paramInt);
  }
  
  private static int getExtraDataIndex(long paramLong) {
    return (int)(paramLong >> 16L);
  }
  
  private static int decompose(long paramLong, int paramInt, DecomposeArgs paramDecomposeArgs) {
    int i = getExtraDataIndex(paramLong);
    paramDecomposeArgs.length = extraData[i++];
    if ((paramLong & paramInt & 0x8L) != 0L && paramDecomposeArgs.length >= 256) {
      i += (paramDecomposeArgs.length >> 7 & 0x1) + (paramDecomposeArgs.length & 0x7F);
      paramDecomposeArgs.length >>= 8;
    } 
    if ((paramDecomposeArgs.length & 0x80) > 0) {
      char c = extraData[i++];
      paramDecomposeArgs.cc = 0xFF & c >> 8;
      paramDecomposeArgs.trailCC = 0xFF & c;
    } else {
      paramDecomposeArgs.cc = paramDecomposeArgs.trailCC = 0;
    } 
    paramDecomposeArgs.length &= 0x7F;
    return i;
  }
  
  private static int decompose(long paramLong, DecomposeArgs paramDecomposeArgs) {
    int i = getExtraDataIndex(paramLong);
    paramDecomposeArgs.length = extraData[i++];
    if ((paramDecomposeArgs.length & 0x80) > 0) {
      char c = extraData[i++];
      paramDecomposeArgs.cc = 0xFF & c >> 8;
      paramDecomposeArgs.trailCC = 0xFF & c;
    } else {
      paramDecomposeArgs.cc = paramDecomposeArgs.trailCC = 0;
    } 
    paramDecomposeArgs.length &= 0x7F;
    return i;
  }
  
  private static int getNextCC(NextCCArgs paramNextCCArgs) {
    paramNextCCArgs.c = paramNextCCArgs.source[paramNextCCArgs.next++];
    long l = getNorm32(paramNextCCArgs.c);
    if ((l & 0xFF00L) == 0L) {
      paramNextCCArgs.c2 = Character.MIN_VALUE;
      return 0;
    } 
    if (!isNorm32LeadSurrogate(l)) {
      paramNextCCArgs.c2 = Character.MIN_VALUE;
    } else if (paramNextCCArgs.next != paramNextCCArgs.limit && 
      UTF16.isTrailSurrogate(paramNextCCArgs.c2 = paramNextCCArgs.source[paramNextCCArgs.next])) {
      paramNextCCArgs.next++;
      l = getNorm32FromSurrogatePair(l, paramNextCCArgs.c2);
    } else {
      paramNextCCArgs.c2 = Character.MIN_VALUE;
      return 0;
    } 
    return (int)(0xFFL & l >> 8L);
  }
  
  private static long getPrevNorm32(PrevArgs paramPrevArgs, int paramInt1, int paramInt2) {
    paramPrevArgs.c = paramPrevArgs.src[--paramPrevArgs.current];
    paramPrevArgs.c2 = Character.MIN_VALUE;
    if (paramPrevArgs.c < paramInt1)
      return 0L; 
    if (!UTF16.isSurrogate(paramPrevArgs.c))
      return getNorm32(paramPrevArgs.c); 
    if (UTF16.isLeadSurrogate(paramPrevArgs.c))
      return 0L; 
    if (paramPrevArgs.current != paramPrevArgs.start && 
      UTF16.isLeadSurrogate(paramPrevArgs.c2 = paramPrevArgs.src[paramPrevArgs.current - 1])) {
      paramPrevArgs.current--;
      long l = getNorm32(paramPrevArgs.c2);
      if ((l & paramInt2) == 0L)
        return 0L; 
      return getNorm32FromSurrogatePair(l, paramPrevArgs.c);
    } 
    paramPrevArgs.c2 = Character.MIN_VALUE;
    return 0L;
  }
  
  private static int getPrevCC(PrevArgs paramPrevArgs) {
    return (int)(0xFFL & getPrevNorm32(paramPrevArgs, 768, 65280) >> 8L);
  }
  
  public static boolean isNFDSafe(long paramLong, int paramInt1, int paramInt2) {
    if ((paramLong & paramInt1) == 0L)
      return true; 
    if (isNorm32Regular(paramLong) && (paramLong & paramInt2) != 0L) {
      DecomposeArgs decomposeArgs = new DecomposeArgs(null);
      decompose(paramLong, paramInt2, decomposeArgs);
      return (decomposeArgs.cc == 0);
    } 
    return ((paramLong & 0xFF00L) == 0L);
  }
  
  public static boolean isTrueStarter(long paramLong, int paramInt1, int paramInt2) {
    if ((paramLong & paramInt1) == 0L)
      return true; 
    if ((paramLong & paramInt2) != 0L) {
      DecomposeArgs decomposeArgs = new DecomposeArgs(null);
      int i = decompose(paramLong, paramInt2, decomposeArgs);
      if (decomposeArgs.cc == 0) {
        int j = paramInt1 & 0x3F;
        if ((getNorm32(extraData, i, j) & j) == 0L)
          return true; 
      } 
    } 
    return false;
  }
  
  private static int insertOrdered(char[] paramArrayOfchar, int paramInt1, int paramInt2, int paramInt3, char paramChar1, char paramChar2, int paramInt4) {
    int i = paramInt4;
    if (paramInt1 < paramInt2 && paramInt4 != 0) {
      int j = paramInt2, k = j;
      PrevArgs prevArgs = new PrevArgs(null);
      prevArgs.current = paramInt2;
      prevArgs.start = paramInt1;
      prevArgs.src = paramArrayOfchar;
      int m = getPrevCC(prevArgs);
      k = prevArgs.current;
      if (paramInt4 < m) {
        i = m;
        j = k;
        while (paramInt1 < k) {
          m = getPrevCC(prevArgs);
          k = prevArgs.current;
          if (paramInt4 >= m)
            break; 
          j = k;
        } 
        int n = paramInt3;
        do {
          paramArrayOfchar[--n] = paramArrayOfchar[--paramInt2];
        } while (j != paramInt2);
      } 
    } 
    paramArrayOfchar[paramInt2] = paramChar1;
    if (paramChar2 != '\000')
      paramArrayOfchar[paramInt2 + 1] = paramChar2; 
    return i;
  }
  
  private static int mergeOrdered(char[] paramArrayOfchar1, int paramInt1, int paramInt2, char[] paramArrayOfchar2, int paramInt3, int paramInt4, boolean paramBoolean) {
    int i = 0;
    boolean bool = (paramInt2 == paramInt3) ? true : false;
    NextCCArgs nextCCArgs = new NextCCArgs(null);
    nextCCArgs.source = paramArrayOfchar2;
    nextCCArgs.next = paramInt3;
    nextCCArgs.limit = paramInt4;
    if (paramInt1 != paramInt2 || !paramBoolean)
      while (nextCCArgs.next < nextCCArgs.limit) {
        int k = getNextCC(nextCCArgs);
        if (k == 0) {
          i = 0;
          if (bool) {
            paramInt2 = nextCCArgs.next;
          } else {
            paramArrayOfchar2[paramInt2++] = nextCCArgs.c;
            if (nextCCArgs.c2 != '\000')
              paramArrayOfchar2[paramInt2++] = nextCCArgs.c2; 
          } 
          if (paramBoolean)
            break; 
          paramInt1 = paramInt2;
          continue;
        } 
        int j = paramInt2 + ((nextCCArgs.c2 == '\000') ? 1 : 2);
        i = insertOrdered(paramArrayOfchar1, paramInt1, paramInt2, j, nextCCArgs.c, nextCCArgs.c2, k);
        paramInt2 = j;
      }  
    if (nextCCArgs.next == nextCCArgs.limit)
      return i; 
    if (!bool)
      while (true) {
        paramArrayOfchar1[paramInt2++] = paramArrayOfchar2[nextCCArgs.next++];
        if (nextCCArgs.next == nextCCArgs.limit) {
          nextCCArgs.limit = paramInt2;
          break;
        } 
      }  
    PrevArgs prevArgs = new PrevArgs(null);
    prevArgs.src = paramArrayOfchar2;
    prevArgs.start = paramInt1;
    prevArgs.current = nextCCArgs.limit;
    return getPrevCC(prevArgs);
  }
  
  private static int mergeOrdered(char[] paramArrayOfchar1, int paramInt1, int paramInt2, char[] paramArrayOfchar2, int paramInt3, int paramInt4) {
    return mergeOrdered(paramArrayOfchar1, paramInt1, paramInt2, paramArrayOfchar2, paramInt3, paramInt4, true);
  }
  
  public static NormalizerBase.QuickCheckResult quickCheck(char[] paramArrayOfchar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, UnicodeSet paramUnicodeSet) {
    ComposePartArgs composePartArgs = new ComposePartArgs(null);
    int j = paramInt1;
    if (!isDataLoaded)
      return NormalizerBase.MAYBE; 
    int i = 0xFF00 | paramInt4;
    NormalizerBase.QuickCheckResult quickCheckResult = NormalizerBase.YES;
    char c = Character.MIN_VALUE;
    while (true) {
      if (paramInt1 == paramInt2)
        return quickCheckResult; 
      long l;
      char c1;
      if ((c1 = paramArrayOfchar[paramInt1++]) >= paramInt3 && ((
        l = getNorm32(c1)) & i) != 0L) {
        boolean bool;
        if (isNorm32LeadSurrogate(l)) {
          if (paramInt1 != paramInt2 && UTF16.isTrailSurrogate(bool = paramArrayOfchar[paramInt1])) {
            paramInt1++;
            l = getNorm32FromSurrogatePair(l, bool);
          } else {
            l = 0L;
            bool = false;
          } 
        } else {
          bool = false;
        } 
        if (nx_contains(paramUnicodeSet, c1, bool))
          l = 0L; 
        char c2 = (char)(int)(l >> 8L & 0xFFL);
        if (c2 != '\000' && c2 < c)
          return NormalizerBase.NO; 
        c = c2;
        long l1 = l & paramInt4;
        if ((l1 & 0xFL) >= 1L) {
          quickCheckResult = NormalizerBase.NO;
          break;
        } 
        if (l1 != 0L) {
          if (paramBoolean) {
            quickCheckResult = NormalizerBase.MAYBE;
            continue;
          } 
          int m = paramInt4 << 2 & 0xF;
          int k = paramInt1 - 1;
          if (UTF16.isTrailSurrogate(paramArrayOfchar[k]))
            k--; 
          k = findPreviousStarter(paramArrayOfchar, j, k, i, m, (char)paramInt3);
          paramInt1 = findNextStarter(paramArrayOfchar, paramInt1, paramInt2, paramInt4, m, (char)paramInt3);
          composePartArgs.prevCC = c;
          char[] arrayOfChar = composePart(composePartArgs, k, paramArrayOfchar, paramInt1, paramInt2, paramInt5, paramUnicodeSet);
          if (0 != strCompare(arrayOfChar, 0, composePartArgs.length, paramArrayOfchar, k, paramInt1, false)) {
            quickCheckResult = NormalizerBase.NO;
            break;
          } 
        } 
        continue;
      } 
      c = Character.MIN_VALUE;
    } 
    return quickCheckResult;
  }
  
  public static int decompose(char[] paramArrayOfchar1, int paramInt1, int paramInt2, char[] paramArrayOfchar2, int paramInt3, int paramInt4, boolean paramBoolean, int[] paramArrayOfint, UnicodeSet paramUnicodeSet) {
    byte b;
    char c1, arrayOfChar[] = new char[3];
    int i2 = paramInt3;
    int i3 = paramInt1;
    if (!paramBoolean) {
      c1 = (char)indexes[8];
      b = 4;
    } else {
      c1 = (char)indexes[9];
      b = 8;
    } 
    int i = 0xFF00 | b;
    int j = 0;
    int m = 0;
    long l = 0L;
    char c = Character.MIN_VALUE;
    int i1 = 0;
    int n = -1, k = n;
    while (true) {
      int i5;
      char c2, arrayOfChar1[];
      int i4 = i3;
      while (i3 != paramInt2 && ((c = paramArrayOfchar1[i3]) < c1 || ((
        l = getNorm32(c)) & i) == 0L)) {
        m = 0;
        i3++;
      } 
      if (i3 != i4) {
        i5 = i3 - i4;
        if (i2 + i5 <= paramInt4)
          System.arraycopy(paramArrayOfchar1, i4, paramArrayOfchar2, i2, i5); 
        i2 += i5;
        j = i2;
      } 
      if (i3 == paramInt2)
        break; 
      i3++;
      if (isNorm32HangulOrJamo(l)) {
        if (nx_contains(paramUnicodeSet, c)) {
          c2 = Character.MIN_VALUE;
          arrayOfChar1 = null;
          i5 = 1;
        } else {
          arrayOfChar1 = arrayOfChar;
          i1 = 0;
          k = n = 0;
          c = (char)(c - 44032);
          c2 = (char)(c % 28);
          c = (char)(c / 28);
          if (c2 > '\000') {
            arrayOfChar[2] = (char)(4519 + c2);
            i5 = 3;
          } else {
            i5 = 2;
          } 
          arrayOfChar[1] = (char)(4449 + c % 21);
          arrayOfChar[0] = (char)(4352 + c / 21);
        } 
      } else {
        if (isNorm32Regular(l)) {
          c2 = Character.MIN_VALUE;
          i5 = 1;
        } else if (i3 != paramInt2 && 
          UTF16.isTrailSurrogate(c2 = paramArrayOfchar1[i3])) {
          i3++;
          i5 = 2;
          l = getNorm32FromSurrogatePair(l, c2);
        } else {
          c2 = Character.MIN_VALUE;
          i5 = 1;
          l = 0L;
        } 
        if (nx_contains(paramUnicodeSet, c, c2)) {
          k = n = 0;
          arrayOfChar1 = null;
        } else if ((l & b) == 0L) {
          k = n = (int)(0xFFL & l >> 8L);
          arrayOfChar1 = null;
          i1 = -1;
        } else {
          DecomposeArgs decomposeArgs = new DecomposeArgs(null);
          i1 = decompose(l, b, decomposeArgs);
          arrayOfChar1 = extraData;
          i5 = decomposeArgs.length;
          k = decomposeArgs.cc;
          n = decomposeArgs.trailCC;
          if (i5 == 1) {
            c = arrayOfChar1[i1];
            c2 = Character.MIN_VALUE;
            arrayOfChar1 = null;
            i1 = -1;
          } 
        } 
      } 
      if (i2 + i5 <= paramInt4) {
        int i6 = i2;
        if (arrayOfChar1 == null) {
          if (k != 0 && k < m) {
            i2 += i5;
            n = insertOrdered(paramArrayOfchar2, j, i6, i2, c, c2, k);
          } else {
            paramArrayOfchar2[i2++] = c;
            if (c2 != '\000')
              paramArrayOfchar2[i2++] = c2; 
          } 
        } else if (k != 0 && k < m) {
          i2 += i5;
          n = mergeOrdered(paramArrayOfchar2, j, i6, arrayOfChar1, i1, i1 + i5);
        } else {
          do {
            paramArrayOfchar2[i2++] = arrayOfChar1[i1++];
          } while (--i5 > 0);
        } 
      } else {
        i2 += i5;
      } 
      m = n;
      if (m == 0)
        j = i2; 
    } 
    paramArrayOfint[0] = m;
    return i2 - paramInt3;
  }
  
  private static int getNextCombining(NextCombiningArgs paramNextCombiningArgs, int paramInt, UnicodeSet paramUnicodeSet) {
    paramNextCombiningArgs.c = paramNextCombiningArgs.source[paramNextCombiningArgs.start++];
    long l = getNorm32(paramNextCombiningArgs.c);
    paramNextCombiningArgs.c2 = Character.MIN_VALUE;
    paramNextCombiningArgs.combiningIndex = 0;
    paramNextCombiningArgs.cc = Character.MIN_VALUE;
    if ((l & 0xFFC0L) == 0L)
      return 0; 
    if (!isNorm32Regular(l)) {
      if (isNorm32HangulOrJamo(l)) {
        paramNextCombiningArgs.combiningIndex = (int)(0xFFFFFFFFL & (0xFFF0L | l >> 16L));
        return (int)(l & 0xC0L);
      } 
      if (paramNextCombiningArgs.start != paramInt && UTF16.isTrailSurrogate(paramNextCombiningArgs.c2 = paramNextCombiningArgs.source[paramNextCombiningArgs.start])) {
        paramNextCombiningArgs.start++;
        l = getNorm32FromSurrogatePair(l, paramNextCombiningArgs.c2);
      } else {
        paramNextCombiningArgs.c2 = Character.MIN_VALUE;
        return 0;
      } 
    } 
    if (nx_contains(paramUnicodeSet, paramNextCombiningArgs.c, paramNextCombiningArgs.c2))
      return 0; 
    paramNextCombiningArgs.cc = (char)(int)(l >> 8L & 0xFFL);
    int i = (int)(l & 0xC0L);
    if (i != 0) {
      int j = getExtraDataIndex(l);
      paramNextCombiningArgs.combiningIndex = (j > 0) ? extraData[j - 1] : 0;
    } 
    return i;
  }
  
  private static int getCombiningIndexFromStarter(char paramChar1, char paramChar2) {
    long l = getNorm32(paramChar1);
    if (paramChar2 != '\000')
      l = getNorm32FromSurrogatePair(l, paramChar2); 
    return extraData[getExtraDataIndex(l) - 1];
  }
  
  private static int combine(char[] paramArrayOfchar, int paramInt1, int paramInt2, int[] paramArrayOfint) {
    char c;
    if (paramArrayOfint.length < 2)
      throw new IllegalArgumentException(); 
    while (true) {
      c = paramArrayOfchar[paramInt1++];
      if (c >= paramInt2)
        break; 
      paramInt1 += ((paramArrayOfchar[paramInt1] & 0x8000) != 0) ? 2 : 1;
    } 
    if ((c & 0x7FFF) == paramInt2) {
      boolean bool;
      int j = paramArrayOfchar[paramInt1];
      int i = (int)(0xFFFFFFFFL & ((j & 0x2000) + 1));
      if ((j & 0x8000) != 0) {
        if ((j & 0x4000) != 0) {
          int k = (int)(0xFFFFFFFFL & (j & 0x3FF | 0xD800));
          bool = paramArrayOfchar[paramInt1 + 1];
        } else {
          j = paramArrayOfchar[paramInt1 + 1];
          bool = false;
        } 
      } else {
        j = j & 0x1FFF;
        bool = false;
      } 
      paramArrayOfint[0] = j;
      paramArrayOfint[1] = bool;
      return i;
    } 
    return 0;
  }
  
  private static char recompose(RecomposeArgs paramRecomposeArgs, int paramInt, UnicodeSet paramUnicodeSet) {
    int j = 0, k = 0;
    int[] arrayOfInt = new int[2];
    int m = -1;
    int i = 0;
    boolean bool = false;
    char c = Character.MIN_VALUE;
    NextCombiningArgs nextCombiningArgs = new NextCombiningArgs(null);
    nextCombiningArgs.source = paramRecomposeArgs.source;
    nextCombiningArgs.cc = Character.MIN_VALUE;
    nextCombiningArgs.c2 = Character.MIN_VALUE;
    while (true) {
      nextCombiningArgs.start = paramRecomposeArgs.start;
      int n = getNextCombining(nextCombiningArgs, paramRecomposeArgs.limit, paramUnicodeSet);
      int i1 = nextCombiningArgs.combiningIndex;
      paramRecomposeArgs.start = nextCombiningArgs.start;
      if ((n & 0x80) != 0 && m != -1)
        if ((i1 & 0x8000) != 0) {
          if ((paramInt & 0x100) != 0 || !c) {
            int i2 = -1;
            n = 0;
            nextCombiningArgs.c2 = paramRecomposeArgs.source[m];
            if (i1 == 65522) {
              nextCombiningArgs.c2 = (char)(nextCombiningArgs.c2 - 4352);
              if (nextCombiningArgs.c2 < '\023') {
                i2 = paramRecomposeArgs.start - 1;
                nextCombiningArgs.c = (char)(44032 + (nextCombiningArgs.c2 * 21 + nextCombiningArgs.c - 4449) * 28);
                if (paramRecomposeArgs.start != paramRecomposeArgs.limit && (nextCombiningArgs.c2 = (char)(paramRecomposeArgs.source[paramRecomposeArgs.start] - 4519)) < '\034') {
                  paramRecomposeArgs.start++;
                  nextCombiningArgs.c = (char)(nextCombiningArgs.c + nextCombiningArgs.c2);
                } else {
                  n = 64;
                } 
                if (!nx_contains(paramUnicodeSet, nextCombiningArgs.c)) {
                  paramRecomposeArgs.source[m] = nextCombiningArgs.c;
                } else {
                  if (!isHangulWithoutJamoT(nextCombiningArgs.c))
                    paramRecomposeArgs.start--; 
                  i2 = paramRecomposeArgs.start;
                } 
              } 
            } else if (isHangulWithoutJamoT(nextCombiningArgs.c2)) {
              nextCombiningArgs.c2 = (char)(nextCombiningArgs.c2 + nextCombiningArgs.c - 4519);
              if (!nx_contains(paramUnicodeSet, nextCombiningArgs.c2)) {
                i2 = paramRecomposeArgs.start - 1;
                paramRecomposeArgs.source[m] = nextCombiningArgs.c2;
              } 
            } 
            if (i2 != -1) {
              int i3 = i2;
              int i4 = paramRecomposeArgs.start;
              while (i4 < paramRecomposeArgs.limit)
                paramRecomposeArgs.source[i3++] = paramRecomposeArgs.source[i4++]; 
              paramRecomposeArgs.start = i2;
              paramRecomposeArgs.limit = i3;
            } 
            nextCombiningArgs.c2 = Character.MIN_VALUE;
            if (n != 0) {
              if (paramRecomposeArgs.start == paramRecomposeArgs.limit)
                return (char)c; 
              i = 65520;
              continue;
            } 
          } 
        } else {
          int i2;
          if ((i & 0x8000) == 0 && (((paramInt & 0x100) != 0) ? (c != nextCombiningArgs.cc || !c) : (c < nextCombiningArgs.cc || c == Character.MIN_VALUE)) && 0 != (
            
            i2 = combine(combiningTable, i, i1, arrayOfInt)) && 
            
            !nx_contains(paramUnicodeSet, (char)j, (char)k)) {
            j = arrayOfInt[0];
            k = arrayOfInt[1];
            int i3 = (nextCombiningArgs.c2 == '\000') ? (paramRecomposeArgs.start - 1) : (paramRecomposeArgs.start - 2);
            paramRecomposeArgs.source[m] = (char)j;
            if (bool) {
              if (k != 0) {
                paramRecomposeArgs.source[m + 1] = (char)k;
              } else {
                bool = false;
                int i4 = m + 1;
                int i5 = i4 + 1;
                while (i5 < i3)
                  paramRecomposeArgs.source[i4++] = paramRecomposeArgs.source[i5++]; 
                i3--;
              } 
            } else if (k != 0) {
              bool = true;
              paramRecomposeArgs.source[m + 1] = (char)k;
            } 
            if (i3 < paramRecomposeArgs.start) {
              int i4 = i3;
              int i5 = paramRecomposeArgs.start;
              while (i5 < paramRecomposeArgs.limit)
                paramRecomposeArgs.source[i4++] = paramRecomposeArgs.source[i5++]; 
              paramRecomposeArgs.start = i3;
              paramRecomposeArgs.limit = i4;
            } 
            if (paramRecomposeArgs.start == paramRecomposeArgs.limit)
              return (char)c; 
            if (i2 > 1) {
              i = getCombiningIndexFromStarter((char)j, (char)k);
              continue;
            } 
            m = -1;
            continue;
          } 
        }  
      c = nextCombiningArgs.cc;
      if (paramRecomposeArgs.start == paramRecomposeArgs.limit)
        return (char)c; 
      if (nextCombiningArgs.cc == '\000') {
        if ((n & 0x40) != 0) {
          if (nextCombiningArgs.c2 == '\000') {
            bool = false;
            m = paramRecomposeArgs.start - 1;
          } else {
            bool = false;
            m = paramRecomposeArgs.start - 2;
          } 
          i = i1;
          continue;
        } 
        m = -1;
        continue;
      } 
      if ((paramInt & 0x2000) != 0)
        m = -1; 
    } 
  }
  
  private static int findPreviousStarter(char[] paramArrayOfchar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, char paramChar) {
    PrevArgs prevArgs = new PrevArgs(null);
    prevArgs.src = paramArrayOfchar;
    prevArgs.start = paramInt1;
    prevArgs.current = paramInt2;
    while (prevArgs.start < prevArgs.current) {
      long l = getPrevNorm32(prevArgs, paramChar, paramInt3 | paramInt4);
      if (isTrueStarter(l, paramInt3, paramInt4))
        break; 
    } 
    return prevArgs.current;
  }
  
  private static int findNextStarter(char[] paramArrayOfchar, int paramInt1, int paramInt2, int paramInt3, int paramInt4, char paramChar) {
    int i = 0xFF00 | paramInt3;
    DecomposeArgs decomposeArgs = new DecomposeArgs(null);
    while (paramInt1 != paramInt2) {
      boolean bool;
      char c = paramArrayOfchar[paramInt1];
      if (c < paramChar)
        break; 
      long l = getNorm32(c);
      if ((l & i) == 0L)
        break; 
      if (isNorm32LeadSurrogate(l)) {
        if (paramInt1 + 1 == paramInt2 || 
          !UTF16.isTrailSurrogate(bool = paramArrayOfchar[paramInt1 + 1]))
          break; 
        l = getNorm32FromSurrogatePair(l, bool);
        if ((l & i) == 0L)
          break; 
      } else {
        bool = false;
      } 
      if ((l & paramInt4) != 0L) {
        int j = decompose(l, paramInt4, decomposeArgs);
        if (decomposeArgs.cc == 0 && (getNorm32(extraData, j, paramInt3) & paramInt3) == 0L)
          break; 
      } 
      paramInt1 += !bool ? 1 : 2;
    } 
    return paramInt1;
  }
  
  private static char[] composePart(ComposePartArgs paramComposePartArgs, int paramInt1, char[] paramArrayOfchar, int paramInt2, int paramInt3, int paramInt4, UnicodeSet paramUnicodeSet) {
    boolean bool = ((paramInt4 & 0x1000) != 0) ? true : false;
    int[] arrayOfInt = new int[1];
    char[] arrayOfChar = new char[(paramInt3 - paramInt1) * 20];
    while (true) {
      paramComposePartArgs.length = decompose(paramArrayOfchar, paramInt1, paramInt2, arrayOfChar, 0, arrayOfChar.length, bool, arrayOfInt, paramUnicodeSet);
      if (paramComposePartArgs.length <= arrayOfChar.length)
        break; 
      arrayOfChar = new char[paramComposePartArgs.length];
    } 
    int i = paramComposePartArgs.length;
    if (paramComposePartArgs.length >= 2) {
      RecomposeArgs recomposeArgs = new RecomposeArgs(null);
      recomposeArgs.source = arrayOfChar;
      recomposeArgs.start = 0;
      recomposeArgs.limit = i;
      paramComposePartArgs.prevCC = recompose(recomposeArgs, paramInt4, paramUnicodeSet);
      i = recomposeArgs.limit;
    } 
    paramComposePartArgs.length = i;
    return arrayOfChar;
  }
  
  private static boolean composeHangul(char paramChar1, char paramChar2, long paramLong, char[] paramArrayOfchar1, int[] paramArrayOfint, int paramInt1, boolean paramBoolean, char[] paramArrayOfchar2, int paramInt2, UnicodeSet paramUnicodeSet) {
    int i = paramArrayOfint[0];
    if (isJamoVTNorm32JamoV(paramLong)) {
      paramChar1 = (char)(paramChar1 - 4352);
      if (paramChar1 < '\023') {
        paramChar2 = (char)(44032 + (paramChar1 * 21 + paramChar2 - 4449) * 28);
        if (i != paramInt1) {
          char c1 = paramArrayOfchar1[i];
          char c2;
          if ((c2 = (char)(c1 - 4519)) < '\034') {
            i++;
            paramChar2 = (char)(paramChar2 + c2);
          } else if (paramBoolean) {
            paramLong = getNorm32(c1);
            if (isNorm32Regular(paramLong) && (paramLong & 0x8L) != 0L) {
              DecomposeArgs decomposeArgs = new DecomposeArgs(null);
              int j = decompose(paramLong, 8, decomposeArgs);
              if (decomposeArgs.length == 1 && (c2 = (char)(extraData[j] - 4519)) < '\034') {
                i++;
                paramChar2 = (char)(paramChar2 + c2);
              } 
            } 
          } 
        } 
        if (nx_contains(paramUnicodeSet, paramChar2)) {
          if (!isHangulWithoutJamoT(paramChar2))
            i--; 
          return false;
        } 
        paramArrayOfchar2[paramInt2] = paramChar2;
        paramArrayOfint[0] = i;
        return true;
      } 
    } else if (isHangulWithoutJamoT(paramChar1)) {
      paramChar2 = (char)(paramChar1 + paramChar2 - 4519);
      if (nx_contains(paramUnicodeSet, paramChar2))
        return false; 
      paramArrayOfchar2[paramInt2] = paramChar2;
      paramArrayOfint[0] = i;
      return true;
    } 
    return false;
  }
  
  public static int compose(char[] paramArrayOfchar1, int paramInt1, int paramInt2, char[] paramArrayOfchar2, int paramInt3, int paramInt4, int paramInt5, UnicodeSet paramUnicodeSet) {
    byte b;
    char c1;
    int[] arrayOfInt = new int[1];
    int n = paramInt3;
    int i1 = paramInt1;
    if ((paramInt5 & 0x1000) != 0) {
      c1 = (char)indexes[7];
      b = 34;
    } else {
      c1 = (char)indexes[6];
      b = 17;
    } 
    int i = i1;
    int j = 0xFF00 | b;
    int k = 0;
    int m = 0;
    long l = 0L;
    char c = Character.MIN_VALUE;
    while (true) {
      int i3;
      char c2;
      int i4, i2 = i1;
      while (i1 != paramInt2 && ((c = paramArrayOfchar1[i1]) < c1 || ((
        l = getNorm32(c)) & j) == 0L)) {
        m = 0;
        i1++;
      } 
      if (i1 != i2) {
        i3 = i1 - i2;
        if (n + i3 <= paramInt4)
          System.arraycopy(paramArrayOfchar1, i2, paramArrayOfchar2, n, i3); 
        n += i3;
        k = n;
        i = i1 - 1;
        if (UTF16.isTrailSurrogate(paramArrayOfchar1[i]) && i2 < i && 
          
          UTF16.isLeadSurrogate(paramArrayOfchar1[i - 1]))
          i--; 
        i2 = i1;
      } 
      if (i1 == paramInt2)
        break; 
      i1++;
      if (isNorm32HangulOrJamo(l)) {
        m = i4 = 0;
        k = n;
        arrayOfInt[0] = i1;
        if (n > 0)
          if (composeHangul(paramArrayOfchar1[i2 - 1], c, l, paramArrayOfchar1, arrayOfInt, paramInt2, ((paramInt5 & 0x1000) != 0), paramArrayOfchar2, (n <= paramInt4) ? (n - 1) : 0, paramUnicodeSet)) {
            i1 = arrayOfInt[0];
            i = i1;
            continue;
          }  
        i1 = arrayOfInt[0];
        c2 = Character.MIN_VALUE;
        i3 = 1;
        i = i2;
      } else {
        if (isNorm32Regular(l)) {
          c2 = Character.MIN_VALUE;
          i3 = 1;
        } else if (i1 != paramInt2 && 
          UTF16.isTrailSurrogate(c2 = paramArrayOfchar1[i1])) {
          i1++;
          i3 = 2;
          l = getNorm32FromSurrogatePair(l, c2);
        } else {
          c2 = Character.MIN_VALUE;
          i3 = 1;
          l = 0L;
        } 
        ComposePartArgs composePartArgs = new ComposePartArgs(null);
        if (nx_contains(paramUnicodeSet, c, c2)) {
          i4 = 0;
        } else if ((l & b) == 0L) {
          i4 = (int)(0xFFL & l >> 8L);
        } else {
          int i5 = b << 2 & 0xF;
          if (isTrueStarter(l, 0xFF00 | b, i5)) {
            i = i2;
          } else {
            n -= i2 - i;
          } 
          i1 = findNextStarter(paramArrayOfchar1, i1, paramInt2, b, i5, c1);
          composePartArgs.prevCC = m;
          composePartArgs.length = i3;
          char[] arrayOfChar = composePart(composePartArgs, i, paramArrayOfchar1, i1, paramInt2, paramInt5, paramUnicodeSet);
          if (arrayOfChar == null)
            break; 
          m = composePartArgs.prevCC;
          i3 = composePartArgs.length;
          if (n + composePartArgs.length <= paramInt4) {
            byte b1 = 0;
            while (b1 < composePartArgs.length) {
              paramArrayOfchar2[n++] = arrayOfChar[b1++];
              i3--;
            } 
          } else {
            n += i3;
          } 
          i = i1;
          continue;
        } 
      } 
      if (n + i3 <= paramInt4) {
        if (i4 != 0 && i4 < m) {
          int i5 = n;
          n += i3;
          m = insertOrdered(paramArrayOfchar2, k, i5, n, c, c2, i4);
          continue;
        } 
        paramArrayOfchar2[n++] = c;
        if (c2 != '\000')
          paramArrayOfchar2[n++] = c2; 
        m = i4;
        continue;
      } 
      n += i3;
      m = i4;
    } 
    return n - paramInt3;
  }
  
  public static int getCombiningClass(int paramInt) {
    long l = getNorm32(paramInt);
    return (int)(l >> 8L & 0xFFL);
  }
  
  public static boolean isFullCompositionExclusion(int paramInt) {
    if (isFormatVersion_2_1) {
      char c = AuxTrieImpl.auxTrie.getCodePointValue(paramInt);
      return ((c & 0x400) != 0);
    } 
    return false;
  }
  
  public static boolean isCanonSafeStart(int paramInt) {
    if (isFormatVersion_2_1) {
      char c = AuxTrieImpl.auxTrie.getCodePointValue(paramInt);
      return ((c & 0x800) == 0);
    } 
    return false;
  }
  
  public static boolean isNFSkippable(int paramInt, NormalizerBase.Mode paramMode, long paramLong) {
    paramLong &= 0xFFFFFFFFL;
    long l = getNorm32(paramInt);
    if ((l & paramLong) != 0L)
      return false; 
    if (paramMode == NormalizerBase.NFD || paramMode == NormalizerBase.NFKD || paramMode == NormalizerBase.NONE)
      return true; 
    if ((l & 0x4L) == 0L)
      return true; 
    if (isNorm32HangulOrJamo(l))
      return !isHangulWithoutJamoT((char)paramInt); 
    if (!isFormatVersion_2_2)
      return false; 
    char c = AuxTrieImpl.auxTrie.getCodePointValue(paramInt);
    return ((c & 0x1000L) == 0L);
  }
  
  public static UnicodeSet addPropertyStarts(UnicodeSet paramUnicodeSet) {
    TrieIterator trieIterator1 = new TrieIterator(NormTrieImpl.normTrie);
    RangeValueIterator.Element element1 = new RangeValueIterator.Element();
    while (trieIterator1.next(element1))
      paramUnicodeSet.add(element1.start); 
    TrieIterator trieIterator2 = new TrieIterator(FCDTrieImpl.fcdTrie);
    RangeValueIterator.Element element2 = new RangeValueIterator.Element();
    while (trieIterator2.next(element2))
      paramUnicodeSet.add(element2.start); 
    if (isFormatVersion_2_1) {
      TrieIterator trieIterator = new TrieIterator(AuxTrieImpl.auxTrie);
      RangeValueIterator.Element element = new RangeValueIterator.Element();
      while (trieIterator.next(element))
        paramUnicodeSet.add(element.start); 
    } 
    for (char c = '가'; c < '힤'; c += '\034') {
      paramUnicodeSet.add(c);
      paramUnicodeSet.add(c + 1);
    } 
    paramUnicodeSet.add(55204);
    return paramUnicodeSet;
  }
  
  public static final int quickCheck(int paramInt1, int paramInt2) {
    int[] arrayOfInt = { 0, 0, 4, 8, 17, 34 };
    int i = (int)getNorm32(paramInt1) & arrayOfInt[paramInt2];
    if (i == 0)
      return 1; 
    if ((i & 0xF) != 0)
      return 0; 
    return 2;
  }
  
  private static int strCompare(char[] paramArrayOfchar1, int paramInt1, int paramInt2, char[] paramArrayOfchar2, int paramInt3, int paramInt4, boolean paramBoolean) {
    char c1, c2;
    boolean bool;
    int i = paramInt1;
    int j = paramInt3;
    int n = paramInt2 - paramInt1;
    int i1 = paramInt4 - paramInt3;
    if (n < i1) {
      bool = true;
      k = i + n;
    } else if (n == i1) {
      bool = false;
      k = i + n;
    } else {
      bool = true;
      k = i + i1;
    } 
    if (paramArrayOfchar1 == paramArrayOfchar2)
      return bool; 
    while (true) {
      if (paramInt1 == k)
        return bool; 
      c1 = paramArrayOfchar1[paramInt1];
      c2 = paramArrayOfchar2[paramInt3];
      if (c1 != c2)
        break; 
      paramInt1++;
      paramInt3++;
    } 
    int k = i + n;
    int m = j + i1;
    if (c1 >= '?' && c2 >= '?' && paramBoolean) {
      if ((c1 > '?' || paramInt1 + 1 == k || 
        
        !UTF16.isTrailSurrogate(paramArrayOfchar1[paramInt1 + 1])) && (
        
        !UTF16.isTrailSurrogate(c1) || i == paramInt1 || 
        !UTF16.isLeadSurrogate(paramArrayOfchar1[paramInt1 - 1])))
        c1 = (char)(c1 - 10240); 
      if ((c2 > '?' || paramInt3 + 1 == m || 
        
        !UTF16.isTrailSurrogate(paramArrayOfchar2[paramInt3 + 1])) && (
        
        !UTF16.isTrailSurrogate(c2) || j == paramInt3 || 
        !UTF16.isLeadSurrogate(paramArrayOfchar2[paramInt3 - 1])))
        c2 = (char)(c2 - 10240); 
    } 
    return c1 - c2;
  }
  
  private static final UnicodeSet[] nxCache = new UnicodeSet[256];
  
  private static final int NX_HANGUL = 1;
  
  private static final int NX_CJK_COMPAT = 2;
  
  public static final int BEFORE_PRI_29 = 256;
  
  public static final int OPTIONS_COMPAT = 4096;
  
  public static final int OPTIONS_COMPOSE_CONTIGUOUS = 8192;
  
  public static final int WITHOUT_CORRIGENDUM4_CORRECTIONS = 262144;
  
  private static final synchronized UnicodeSet internalGetNXHangul() {
    if (nxCache[1] == null)
      nxCache[1] = new UnicodeSet(44032, 55203); 
    return nxCache[1];
  }
  
  private static final synchronized UnicodeSet internalGetNXCJKCompat() {
    if (nxCache[2] == null) {
      UnicodeSet unicodeSet1 = new UnicodeSet("[:Ideographic:]");
      UnicodeSet unicodeSet2 = new UnicodeSet();
      UnicodeSetIterator unicodeSetIterator = new UnicodeSetIterator(unicodeSet1);
      while (unicodeSetIterator.nextRange() && unicodeSetIterator.codepoint != UnicodeSetIterator.IS_STRING) {
        int i = unicodeSetIterator.codepoint;
        int j = unicodeSetIterator.codepointEnd;
        while (i <= j) {
          long l = getNorm32(i);
          if ((l & 0x4L) > 0L)
            unicodeSet2.add(i); 
          i++;
        } 
      } 
      nxCache[2] = unicodeSet2;
    } 
    return nxCache[2];
  }
  
  private static final synchronized UnicodeSet internalGetNXUnicode(int paramInt) {
    paramInt &= 0xE0;
    if (paramInt == 0)
      return null; 
    if (nxCache[paramInt] == null) {
      UnicodeSet unicodeSet = new UnicodeSet();
      switch (paramInt) {
        case 32:
          unicodeSet.applyPattern("[:^Age=3.2:]");
          break;
        default:
          return null;
      } 
      nxCache[paramInt] = unicodeSet;
    } 
    return nxCache[paramInt];
  }
  
  private static final synchronized UnicodeSet internalGetNX(int paramInt) {
    paramInt &= 0xFF;
    if (nxCache[paramInt] == null) {
      if (paramInt == 1)
        return internalGetNXHangul(); 
      if (paramInt == 2)
        return internalGetNXCJKCompat(); 
      if ((paramInt & 0xE0) != 0 && (paramInt & 0x1F) == 0)
        return internalGetNXUnicode(paramInt); 
      UnicodeSet unicodeSet1 = new UnicodeSet();
      UnicodeSet unicodeSet2;
      if ((paramInt & 0x1) != 0 && null != (unicodeSet2 = internalGetNXHangul()))
        unicodeSet1.addAll(unicodeSet2); 
      if ((paramInt & 0x2) != 0 && null != (unicodeSet2 = internalGetNXCJKCompat()))
        unicodeSet1.addAll(unicodeSet2); 
      if ((paramInt & 0xE0) != 0 && null != (unicodeSet2 = internalGetNXUnicode(paramInt)))
        unicodeSet1.addAll(unicodeSet2); 
      nxCache[paramInt] = unicodeSet1;
    } 
    return nxCache[paramInt];
  }
  
  public static final UnicodeSet getNX(int paramInt) {
    if ((paramInt &= 0xFF) == 0)
      return null; 
    return internalGetNX(paramInt);
  }
  
  private static final boolean nx_contains(UnicodeSet paramUnicodeSet, int paramInt) {
    return (paramUnicodeSet != null && paramUnicodeSet.contains(paramInt));
  }
  
  private static final boolean nx_contains(UnicodeSet paramUnicodeSet, char paramChar1, char paramChar2) {
    return (paramUnicodeSet != null && paramUnicodeSet.contains((paramChar2 == '\000') ? paramChar1 : UCharacterProperty.getRawSupplementary(paramChar1, paramChar2)));
  }
  
  public static int getDecompose(int[] paramArrayOfint, String[] paramArrayOfString) {
    DecomposeArgs decomposeArgs = new DecomposeArgs(null);
    boolean bool = false;
    long l = 0L;
    int i = -1;
    int j = 0;
    byte b = 0;
    while (++i < 195102) {
      if (i == 12543) {
        i = 63744;
      } else if (i == 65536) {
        i = 119134;
      } else if (i == 119233) {
        i = 194560;
      } 
      l = getNorm32(i);
      if ((l & 0x4L) != 0L && b < paramArrayOfint.length) {
        paramArrayOfint[b] = i;
        j = decompose(l, decomposeArgs);
        paramArrayOfString[b++] = new String(extraData, j, decomposeArgs.length);
      } 
    } 
    return b;
  }
  
  private static boolean needSingleQuotation(char paramChar) {
    return ((paramChar >= '\t' && paramChar <= '\r') || (paramChar >= ' ' && paramChar <= '/') || (paramChar >= ':' && paramChar <= '@') || (paramChar >= '[' && paramChar <= '`') || (paramChar >= '{' && paramChar <= '~'));
  }
  
  public static String canonicalDecomposeWithSingleQuotation(String paramString) {
    char[] arrayOfChar1 = paramString.toCharArray();
    int i = 0;
    int j = arrayOfChar1.length;
    char[] arrayOfChar2 = new char[arrayOfChar1.length * 3];
    int k = 0;
    int m = arrayOfChar2.length;
    char[] arrayOfChar3 = new char[3];
    byte b = 4;
    char c1 = (char)indexes[8];
    int n = 0xFF00 | b;
    int i1 = 0;
    int i3 = 0;
    long l = 0L;
    char c = Character.MIN_VALUE;
    int i5 = 0;
    int i4 = -1, i2 = i4;
    while (true) {
      int i7;
      boolean bool;
      char[] arrayOfChar;
      int i6 = i;
      while (i != j && ((c = arrayOfChar1[i]) < c1 || ((
        
        l = getNorm32(c)) & n) == 0L || (c >= '가' && c <= '힣'))) {
        i3 = 0;
        i++;
      } 
      if (i != i6) {
        i7 = i - i6;
        if (k + i7 <= m)
          System.arraycopy(arrayOfChar1, i6, arrayOfChar2, k, i7); 
        k += i7;
        i1 = k;
      } 
      if (i == j)
        break; 
      i++;
      if (isNorm32Regular(l)) {
        bool = false;
        i7 = 1;
      } else if (i != j && 
        Character.isLowSurrogate(bool = arrayOfChar1[i])) {
        i++;
        i7 = 2;
        l = getNorm32FromSurrogatePair(l, bool);
      } else {
        bool = false;
        i7 = 1;
        l = 0L;
      } 
      if ((l & b) == 0L) {
        i2 = i4 = (int)(0xFFL & l >> 8L);
        arrayOfChar = null;
        i5 = -1;
      } else {
        DecomposeArgs decomposeArgs = new DecomposeArgs(null);
        i5 = decompose(l, b, decomposeArgs);
        arrayOfChar = extraData;
        i7 = decomposeArgs.length;
        i2 = decomposeArgs.cc;
        i4 = decomposeArgs.trailCC;
        if (i7 == 1) {
          c = arrayOfChar[i5];
          bool = false;
          arrayOfChar = null;
          i5 = -1;
        } 
      } 
      if (k + i7 * 3 >= m) {
        char[] arrayOfChar4 = new char[m * 2];
        System.arraycopy(arrayOfChar2, 0, arrayOfChar4, 0, k);
        arrayOfChar2 = arrayOfChar4;
        m = arrayOfChar2.length;
      } 
      int i8 = k;
      if (arrayOfChar == null) {
        if (needSingleQuotation(c)) {
          arrayOfChar2[k++] = '\'';
          arrayOfChar2[k++] = c;
          arrayOfChar2[k++] = '\'';
          i4 = 0;
        } else if (i2 != 0 && i2 < i3) {
          k += i7;
          i4 = insertOrdered(arrayOfChar2, i1, i8, k, c, bool, i2);
        } else {
          arrayOfChar2[k++] = c;
          if (bool)
            arrayOfChar2[k++] = bool; 
        } 
      } else if (needSingleQuotation(arrayOfChar[i5])) {
        arrayOfChar2[k++] = '\'';
        arrayOfChar2[k++] = arrayOfChar[i5++];
        arrayOfChar2[k++] = '\'';
        i7--;
        do {
          arrayOfChar2[k++] = arrayOfChar[i5++];
        } while (--i7 > 0);
      } else if (i2 != 0 && i2 < i3) {
        k += i7;
        i4 = mergeOrdered(arrayOfChar2, i1, i8, arrayOfChar, i5, i5 + i7);
      } else {
        do {
          arrayOfChar2[k++] = arrayOfChar[i5++];
        } while (--i7 > 0);
      } 
      i3 = i4;
      if (i3 == 0)
        i1 = k; 
    } 
    return new String(arrayOfChar2, 0, k);
  }
  
  private static final char[][] corrigendum4MappingTable = new char[][] { { '?', '?' }, { '弳' }, { '䎫' }, { '窮' }, { '䵗' } };
  
  public static String convert(String paramString) {
    if (paramString == null)
      return null; 
    int i = -1;
    StringBuffer stringBuffer = new StringBuffer();
    UCharacterIterator uCharacterIterator = UCharacterIterator.getInstance(paramString);
    while ((i = uCharacterIterator.nextCodePoint()) != -1) {
      switch (i) {
        case 194664:
          stringBuffer.append(corrigendum4MappingTable[0]);
          continue;
        case 194676:
          stringBuffer.append(corrigendum4MappingTable[1]);
          continue;
        case 194847:
          stringBuffer.append(corrigendum4MappingTable[2]);
          continue;
        case 194911:
          stringBuffer.append(corrigendum4MappingTable[3]);
          continue;
        case 195007:
          stringBuffer.append(corrigendum4MappingTable[4]);
          continue;
      } 
      UTF16.append(stringBuffer, i);
    } 
    return stringBuffer.toString();
  }
  
  private static final class NormalizerImpl {}
  
  private static final class NormalizerImpl {}
  
  private static final class NormalizerImpl {}
  
  private static final class NormalizerImpl {}
  
  private static final class NormalizerImpl {}
  
  private static final class NormalizerImpl {}
}
