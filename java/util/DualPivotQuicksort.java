package java.util;

final class DualPivotQuicksort {
  private static final int MAX_RUN_COUNT = 67;
  
  private static final int MAX_RUN_LENGTH = 33;
  
  private static final int QUICKSORT_THRESHOLD = 286;
  
  private static final int INSERTION_SORT_THRESHOLD = 47;
  
  private static final int COUNTING_SORT_THRESHOLD_FOR_BYTE = 29;
  
  private static final int COUNTING_SORT_THRESHOLD_FOR_SHORT_OR_CHAR = 3200;
  
  private static final int NUM_SHORT_VALUES = 65536;
  
  private static final int NUM_CHAR_VALUES = 65536;
  
  private static final int NUM_BYTE_VALUES = 256;
  
  static void sort(int[] paramArrayOfint1, int paramInt1, int paramInt2, int[] paramArrayOfint2, int paramInt3, int paramInt4) {
    int arrayOfInt2[], k, m;
    if (paramInt2 - paramInt1 < 286) {
      sort(paramArrayOfint1, paramInt1, paramInt2, true);
      return;
    } 
    int[] arrayOfInt1 = new int[68];
    byte b = 0;
    arrayOfInt1[0] = paramInt1;
    int i;
    for (i = paramInt1; i < paramInt2; arrayOfInt1[b] = i) {
      if (paramArrayOfint1[i] < paramArrayOfint1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfint1[i - 1] <= paramArrayOfint1[i]);
      } else if (paramArrayOfint1[i] > paramArrayOfint1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfint1[i - 1] >= paramArrayOfint1[i]);
        for (int i1 = arrayOfInt1[b] - 1; ++i1 < --k; ) {
          m = paramArrayOfint1[i1];
          paramArrayOfint1[i1] = paramArrayOfint1[k];
          paramArrayOfint1[k] = m;
        } 
      } else {
        for (byte b1 = 33; ++i <= paramInt2 && paramArrayOfint1[i - 1] == paramArrayOfint1[i];) {
          if (--b1 == 0) {
            sort(paramArrayOfint1, paramInt1, paramInt2, true);
            return;
          } 
        } 
      } 
      if (++b == 67) {
        sort(paramArrayOfint1, paramInt1, paramInt2, true);
        return;
      } 
    } 
    if (arrayOfInt1[b] == paramInt2++) {
      arrayOfInt1[++b] = paramInt2;
    } else if (b == 1) {
      return;
    } 
    i = 0;
    for (int j = 1; (j <<= 1) < b; i = (byte)(i ^ 0x1));
    int n = paramInt2 - paramInt1;
    if (paramArrayOfint2 == null || paramInt4 < n || paramInt3 + n > paramArrayOfint2.length) {
      paramArrayOfint2 = new int[n];
      paramInt3 = 0;
    } 
    if (i == 0) {
      System.arraycopy(paramArrayOfint1, paramInt1, paramArrayOfint2, paramInt3, n);
      arrayOfInt2 = paramArrayOfint1;
      m = 0;
      paramArrayOfint1 = paramArrayOfint2;
      k = paramInt3 - paramInt1;
    } else {
      arrayOfInt2 = paramArrayOfint2;
      k = 0;
      m = paramInt3 - paramInt1;
    } 
    for (; b > 1; b = b1) {
      byte b1;
      int i1;
      for (i1 = (b1 = 0) + 2; i1 <= b; i1 += 2) {
        int i3 = arrayOfInt1[i1], i4 = arrayOfInt1[i1 - 1];
        for (int i5 = arrayOfInt1[i1 - 2], i6 = i5, i7 = i4; i5 < i3; i5++) {
          if (i7 >= i3 || (i6 < i4 && paramArrayOfint1[i6 + k] <= paramArrayOfint1[i7 + k])) {
            arrayOfInt2[i5 + m] = paramArrayOfint1[i6++ + k];
          } else {
            arrayOfInt2[i5 + m] = paramArrayOfint1[i7++ + k];
          } 
        } 
        arrayOfInt1[++b1] = i3;
      } 
      if ((b & 0x1) != 0) {
        int i3;
        for (i1 = paramInt2, i3 = arrayOfInt1[b - 1]; --i1 >= i3;)
          arrayOfInt2[i1 + m] = paramArrayOfint1[i1 + k]; 
        arrayOfInt1[++b1] = paramInt2;
      } 
      int[] arrayOfInt = paramArrayOfint1;
      paramArrayOfint1 = arrayOfInt2;
      arrayOfInt2 = arrayOfInt;
      int i2 = k;
      k = m;
      m = i2;
    } 
  }
  
  private static void sort(int[] paramArrayOfint, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47) {
      if (paramBoolean) {
        int i6;
        for (int i5 = paramInt1; i5 < paramInt2; i6 = ++i5) {
          int i7 = paramArrayOfint[i5 + 1];
          while (i7 < paramArrayOfint[i6]) {
            paramArrayOfint[i6 + 1] = paramArrayOfint[i6];
            if (i6-- == paramInt1)
              break; 
          } 
          paramArrayOfint[i6 + 1] = i7;
        } 
      } else {
        do {
          if (paramInt1 >= paramInt2)
            return; 
        } while (paramArrayOfint[++paramInt1] >= paramArrayOfint[paramInt1 - 1]);
        int i5;
        for (i5 = paramInt1; ++paramInt1 <= paramInt2; i5 = ++paramInt1) {
          int i6 = paramArrayOfint[i5], i7 = paramArrayOfint[paramInt1];
          if (i6 < i7) {
            i7 = i6;
            i6 = paramArrayOfint[paramInt1];
          } 
          while (i6 < paramArrayOfint[--i5])
            paramArrayOfint[i5 + 2] = paramArrayOfint[i5]; 
          paramArrayOfint[++i5 + 1] = i6;
          while (i7 < paramArrayOfint[--i5])
            paramArrayOfint[i5 + 1] = paramArrayOfint[i5]; 
          paramArrayOfint[i5 + 1] = i7;
        } 
        i5 = paramArrayOfint[paramInt2];
        while (i5 < paramArrayOfint[--paramInt2])
          paramArrayOfint[paramInt2 + 1] = paramArrayOfint[paramInt2]; 
        paramArrayOfint[paramInt2 + 1] = i5;
      } 
      return;
    } 
    int j = (i >> 3) + (i >> 6) + 1;
    int k = paramInt1 + paramInt2 >>> 1;
    int m = k - j;
    int n = m - j;
    int i1 = k + j;
    int i2 = i1 + j;
    if (paramArrayOfint[m] < paramArrayOfint[n]) {
      int i5 = paramArrayOfint[m];
      paramArrayOfint[m] = paramArrayOfint[n];
      paramArrayOfint[n] = i5;
    } 
    if (paramArrayOfint[k] < paramArrayOfint[m]) {
      int i5 = paramArrayOfint[k];
      paramArrayOfint[k] = paramArrayOfint[m];
      paramArrayOfint[m] = i5;
      if (i5 < paramArrayOfint[n]) {
        paramArrayOfint[m] = paramArrayOfint[n];
        paramArrayOfint[n] = i5;
      } 
    } 
    if (paramArrayOfint[i1] < paramArrayOfint[k]) {
      int i5 = paramArrayOfint[i1];
      paramArrayOfint[i1] = paramArrayOfint[k];
      paramArrayOfint[k] = i5;
      if (i5 < paramArrayOfint[m]) {
        paramArrayOfint[k] = paramArrayOfint[m];
        paramArrayOfint[m] = i5;
        if (i5 < paramArrayOfint[n]) {
          paramArrayOfint[m] = paramArrayOfint[n];
          paramArrayOfint[n] = i5;
        } 
      } 
    } 
    if (paramArrayOfint[i2] < paramArrayOfint[i1]) {
      int i5 = paramArrayOfint[i2];
      paramArrayOfint[i2] = paramArrayOfint[i1];
      paramArrayOfint[i1] = i5;
      if (i5 < paramArrayOfint[k]) {
        paramArrayOfint[i1] = paramArrayOfint[k];
        paramArrayOfint[k] = i5;
        if (i5 < paramArrayOfint[m]) {
          paramArrayOfint[k] = paramArrayOfint[m];
          paramArrayOfint[m] = i5;
          if (i5 < paramArrayOfint[n]) {
            paramArrayOfint[m] = paramArrayOfint[n];
            paramArrayOfint[n] = i5;
          } 
        } 
      } 
    } 
    int i3 = paramInt1;
    int i4 = paramInt2;
    if (paramArrayOfint[n] != paramArrayOfint[m] && paramArrayOfint[m] != paramArrayOfint[k] && paramArrayOfint[k] != paramArrayOfint[i1] && paramArrayOfint[i1] != paramArrayOfint[i2]) {
      int i5 = paramArrayOfint[m];
      int i6 = paramArrayOfint[i1];
      paramArrayOfint[m] = paramArrayOfint[paramInt1];
      paramArrayOfint[i1] = paramArrayOfint[paramInt2];
      while (paramArrayOfint[++i3] < i5);
      while (paramArrayOfint[--i4] > i6);
      int i7;
      label149: for (i7 = i3 - 1; ++i7 <= i4; ) {
        int i8 = paramArrayOfint[i7];
        if (i8 < i5) {
          paramArrayOfint[i7] = paramArrayOfint[i3];
          paramArrayOfint[i3] = i8;
          i3++;
          continue;
        } 
        if (i8 > i6) {
          while (paramArrayOfint[i4] > i6) {
            if (i4-- == i7)
              break label149; 
          } 
          if (paramArrayOfint[i4] < i5) {
            paramArrayOfint[i7] = paramArrayOfint[i3];
            paramArrayOfint[i3] = paramArrayOfint[i4];
            i3++;
          } else {
            paramArrayOfint[i7] = paramArrayOfint[i4];
          } 
          paramArrayOfint[i4] = i8;
          i4--;
        } 
      } 
      paramArrayOfint[paramInt1] = paramArrayOfint[i3 - 1];
      paramArrayOfint[i3 - 1] = i5;
      paramArrayOfint[paramInt2] = paramArrayOfint[i4 + 1];
      paramArrayOfint[i4 + 1] = i6;
      sort(paramArrayOfint, paramInt1, i3 - 2, paramBoolean);
      sort(paramArrayOfint, i4 + 2, paramInt2, false);
      if (i3 < n && i2 < i4) {
        while (paramArrayOfint[i3] == i5)
          i3++; 
        while (paramArrayOfint[i4] == i6)
          i4--; 
        for (i7 = i3 - 1; ++i7 <= i4; ) {
          int i8 = paramArrayOfint[i7];
          if (i8 == i5) {
            paramArrayOfint[i7] = paramArrayOfint[i3];
            paramArrayOfint[i3] = i8;
            i3++;
            continue;
          } 
          if (i8 == i6) {
            while (paramArrayOfint[i4] == i6) {
              if (i4-- == i7)
                // Byte code: goto -> 1033 
            } 
            if (paramArrayOfint[i4] == i5) {
              paramArrayOfint[i7] = paramArrayOfint[i3];
              paramArrayOfint[i3] = i5;
              i3++;
            } else {
              paramArrayOfint[i7] = paramArrayOfint[i4];
            } 
            paramArrayOfint[i4] = i8;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfint, i3, i4, false);
    } else {
      int i5 = paramArrayOfint[k];
      for (int i6 = i3; i6 <= i4; i6++) {
        if (paramArrayOfint[i6] != i5) {
          int i7 = paramArrayOfint[i6];
          if (i7 < i5) {
            paramArrayOfint[i6] = paramArrayOfint[i3];
            paramArrayOfint[i3] = i7;
            i3++;
          } else {
            while (paramArrayOfint[i4] > i5)
              i4--; 
            if (paramArrayOfint[i4] < i5) {
              paramArrayOfint[i6] = paramArrayOfint[i3];
              paramArrayOfint[i3] = paramArrayOfint[i4];
              i3++;
            } else {
              paramArrayOfint[i6] = i5;
            } 
            paramArrayOfint[i4] = i7;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfint, paramInt1, i3 - 1, paramBoolean);
      sort(paramArrayOfint, i4 + 1, paramInt2, false);
    } 
  }
  
  static void sort(long[] paramArrayOflong1, int paramInt1, int paramInt2, long[] paramArrayOflong2, int paramInt3, int paramInt4) {
    long[] arrayOfLong;
    int k, m;
    if (paramInt2 - paramInt1 < 286) {
      sort(paramArrayOflong1, paramInt1, paramInt2, true);
      return;
    } 
    int[] arrayOfInt = new int[68];
    byte b = 0;
    arrayOfInt[0] = paramInt1;
    int i;
    for (i = paramInt1; i < paramInt2; arrayOfInt[b] = i) {
      if (paramArrayOflong1[i] < paramArrayOflong1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOflong1[i - 1] <= paramArrayOflong1[i]);
      } else if (paramArrayOflong1[i] > paramArrayOflong1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOflong1[i - 1] >= paramArrayOflong1[i]);
        for (int i1 = arrayOfInt[b] - 1; ++i1 < --k; ) {
          long l = paramArrayOflong1[i1];
          paramArrayOflong1[i1] = paramArrayOflong1[k];
          paramArrayOflong1[k] = l;
        } 
      } else {
        for (byte b1 = 33; ++i <= paramInt2 && paramArrayOflong1[i - 1] == paramArrayOflong1[i];) {
          if (--b1 == 0) {
            sort(paramArrayOflong1, paramInt1, paramInt2, true);
            return;
          } 
        } 
      } 
      if (++b == 67) {
        sort(paramArrayOflong1, paramInt1, paramInt2, true);
        return;
      } 
    } 
    if (arrayOfInt[b] == paramInt2++) {
      arrayOfInt[++b] = paramInt2;
    } else if (b == 1) {
      return;
    } 
    i = 0;
    for (int j = 1; (j <<= 1) < b; i = (byte)(i ^ 0x1));
    int n = paramInt2 - paramInt1;
    if (paramArrayOflong2 == null || paramInt4 < n || paramInt3 + n > paramArrayOflong2.length) {
      paramArrayOflong2 = new long[n];
      paramInt3 = 0;
    } 
    if (i == 0) {
      System.arraycopy(paramArrayOflong1, paramInt1, paramArrayOflong2, paramInt3, n);
      arrayOfLong = paramArrayOflong1;
      m = 0;
      paramArrayOflong1 = paramArrayOflong2;
      k = paramInt3 - paramInt1;
    } else {
      arrayOfLong = paramArrayOflong2;
      k = 0;
      m = paramInt3 - paramInt1;
    } 
    for (; b > 1; b = b1) {
      byte b1;
      int i1;
      for (i1 = (b1 = 0) + 2; i1 <= b; i1 += 2) {
        int i3 = arrayOfInt[i1], i4 = arrayOfInt[i1 - 1];
        for (int i5 = arrayOfInt[i1 - 2], i6 = i5, i7 = i4; i5 < i3; i5++) {
          if (i7 >= i3 || (i6 < i4 && paramArrayOflong1[i6 + k] <= paramArrayOflong1[i7 + k])) {
            arrayOfLong[i5 + m] = paramArrayOflong1[i6++ + k];
          } else {
            arrayOfLong[i5 + m] = paramArrayOflong1[i7++ + k];
          } 
        } 
        arrayOfInt[++b1] = i3;
      } 
      if ((b & 0x1) != 0) {
        int i3;
        for (i1 = paramInt2, i3 = arrayOfInt[b - 1]; --i1 >= i3;)
          arrayOfLong[i1 + m] = paramArrayOflong1[i1 + k]; 
        arrayOfInt[++b1] = paramInt2;
      } 
      long[] arrayOfLong1 = paramArrayOflong1;
      paramArrayOflong1 = arrayOfLong;
      arrayOfLong = arrayOfLong1;
      int i2 = k;
      k = m;
      m = i2;
    } 
  }
  
  private static void sort(long[] paramArrayOflong, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47) {
      if (paramBoolean) {
        int i6;
        for (int i5 = paramInt1; i5 < paramInt2; i6 = ++i5) {
          long l = paramArrayOflong[i5 + 1];
          while (l < paramArrayOflong[i6]) {
            paramArrayOflong[i6 + 1] = paramArrayOflong[i6];
            if (i6-- == paramInt1)
              break; 
          } 
          paramArrayOflong[i6 + 1] = l;
        } 
      } else {
        do {
          if (paramInt1 >= paramInt2)
            return; 
        } while (paramArrayOflong[++paramInt1] >= paramArrayOflong[paramInt1 - 1]);
        int i5;
        for (i5 = paramInt1; ++paramInt1 <= paramInt2; i5 = ++paramInt1) {
          long l1 = paramArrayOflong[i5], l2 = paramArrayOflong[paramInt1];
          if (l1 < l2) {
            l2 = l1;
            l1 = paramArrayOflong[paramInt1];
          } 
          while (l1 < paramArrayOflong[--i5])
            paramArrayOflong[i5 + 2] = paramArrayOflong[i5]; 
          paramArrayOflong[++i5 + 1] = l1;
          while (l2 < paramArrayOflong[--i5])
            paramArrayOflong[i5 + 1] = paramArrayOflong[i5]; 
          paramArrayOflong[i5 + 1] = l2;
        } 
        long l = paramArrayOflong[paramInt2];
        while (l < paramArrayOflong[--paramInt2])
          paramArrayOflong[paramInt2 + 1] = paramArrayOflong[paramInt2]; 
        paramArrayOflong[paramInt2 + 1] = l;
      } 
      return;
    } 
    int j = (i >> 3) + (i >> 6) + 1;
    int k = paramInt1 + paramInt2 >>> 1;
    int m = k - j;
    int n = m - j;
    int i1 = k + j;
    int i2 = i1 + j;
    if (paramArrayOflong[m] < paramArrayOflong[n]) {
      long l = paramArrayOflong[m];
      paramArrayOflong[m] = paramArrayOflong[n];
      paramArrayOflong[n] = l;
    } 
    if (paramArrayOflong[k] < paramArrayOflong[m]) {
      long l = paramArrayOflong[k];
      paramArrayOflong[k] = paramArrayOflong[m];
      paramArrayOflong[m] = l;
      if (l < paramArrayOflong[n]) {
        paramArrayOflong[m] = paramArrayOflong[n];
        paramArrayOflong[n] = l;
      } 
    } 
    if (paramArrayOflong[i1] < paramArrayOflong[k]) {
      long l = paramArrayOflong[i1];
      paramArrayOflong[i1] = paramArrayOflong[k];
      paramArrayOflong[k] = l;
      if (l < paramArrayOflong[m]) {
        paramArrayOflong[k] = paramArrayOflong[m];
        paramArrayOflong[m] = l;
        if (l < paramArrayOflong[n]) {
          paramArrayOflong[m] = paramArrayOflong[n];
          paramArrayOflong[n] = l;
        } 
      } 
    } 
    if (paramArrayOflong[i2] < paramArrayOflong[i1]) {
      long l = paramArrayOflong[i2];
      paramArrayOflong[i2] = paramArrayOflong[i1];
      paramArrayOflong[i1] = l;
      if (l < paramArrayOflong[k]) {
        paramArrayOflong[i1] = paramArrayOflong[k];
        paramArrayOflong[k] = l;
        if (l < paramArrayOflong[m]) {
          paramArrayOflong[k] = paramArrayOflong[m];
          paramArrayOflong[m] = l;
          if (l < paramArrayOflong[n]) {
            paramArrayOflong[m] = paramArrayOflong[n];
            paramArrayOflong[n] = l;
          } 
        } 
      } 
    } 
    int i3 = paramInt1;
    int i4 = paramInt2;
    if (paramArrayOflong[n] != paramArrayOflong[m] && paramArrayOflong[m] != paramArrayOflong[k] && paramArrayOflong[k] != paramArrayOflong[i1] && paramArrayOflong[i1] != paramArrayOflong[i2]) {
      long l1 = paramArrayOflong[m];
      long l2 = paramArrayOflong[i1];
      paramArrayOflong[m] = paramArrayOflong[paramInt1];
      paramArrayOflong[i1] = paramArrayOflong[paramInt2];
      while (paramArrayOflong[++i3] < l1);
      while (paramArrayOflong[--i4] > l2);
      int i5;
      label149: for (i5 = i3 - 1; ++i5 <= i4; ) {
        long l = paramArrayOflong[i5];
        if (l < l1) {
          paramArrayOflong[i5] = paramArrayOflong[i3];
          paramArrayOflong[i3] = l;
          i3++;
          continue;
        } 
        if (l > l2) {
          while (paramArrayOflong[i4] > l2) {
            if (i4-- == i5)
              break label149; 
          } 
          if (paramArrayOflong[i4] < l1) {
            paramArrayOflong[i5] = paramArrayOflong[i3];
            paramArrayOflong[i3] = paramArrayOflong[i4];
            i3++;
          } else {
            paramArrayOflong[i5] = paramArrayOflong[i4];
          } 
          paramArrayOflong[i4] = l;
          i4--;
        } 
      } 
      paramArrayOflong[paramInt1] = paramArrayOflong[i3 - 1];
      paramArrayOflong[i3 - 1] = l1;
      paramArrayOflong[paramInt2] = paramArrayOflong[i4 + 1];
      paramArrayOflong[i4 + 1] = l2;
      sort(paramArrayOflong, paramInt1, i3 - 2, paramBoolean);
      sort(paramArrayOflong, i4 + 2, paramInt2, false);
      if (i3 < n && i2 < i4) {
        while (paramArrayOflong[i3] == l1)
          i3++; 
        while (paramArrayOflong[i4] == l2)
          i4--; 
        for (i5 = i3 - 1; ++i5 <= i4; ) {
          long l = paramArrayOflong[i5];
          if (l == l1) {
            paramArrayOflong[i5] = paramArrayOflong[i3];
            paramArrayOflong[i3] = l;
            i3++;
            continue;
          } 
          if (l == l2) {
            while (paramArrayOflong[i4] == l2) {
              if (i4-- == i5)
                // Byte code: goto -> 1065 
            } 
            if (paramArrayOflong[i4] == l1) {
              paramArrayOflong[i5] = paramArrayOflong[i3];
              paramArrayOflong[i3] = l1;
              i3++;
            } else {
              paramArrayOflong[i5] = paramArrayOflong[i4];
            } 
            paramArrayOflong[i4] = l;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOflong, i3, i4, false);
    } else {
      long l = paramArrayOflong[k];
      for (int i5 = i3; i5 <= i4; i5++) {
        if (paramArrayOflong[i5] != l) {
          long l1 = paramArrayOflong[i5];
          if (l1 < l) {
            paramArrayOflong[i5] = paramArrayOflong[i3];
            paramArrayOflong[i3] = l1;
            i3++;
          } else {
            while (paramArrayOflong[i4] > l)
              i4--; 
            if (paramArrayOflong[i4] < l) {
              paramArrayOflong[i5] = paramArrayOflong[i3];
              paramArrayOflong[i3] = paramArrayOflong[i4];
              i3++;
            } else {
              paramArrayOflong[i5] = l;
            } 
            paramArrayOflong[i4] = l1;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOflong, paramInt1, i3 - 1, paramBoolean);
      sort(paramArrayOflong, i4 + 1, paramInt2, false);
    } 
  }
  
  static void sort(short[] paramArrayOfshort1, int paramInt1, int paramInt2, short[] paramArrayOfshort2, int paramInt3, int paramInt4) {
    if (paramInt2 - paramInt1 > 3200) {
      int[] arrayOfInt = new int[65536];
      int i;
      for (i = paramInt1 - 1; ++i <= paramInt2;)
        arrayOfInt[paramArrayOfshort1[i] - -32768] = arrayOfInt[paramArrayOfshort1[i] - -32768] + 1; 
      int j;
      label21: for (i = 65536, j = paramInt2 + 1; j > paramInt1; ) {
        while (arrayOfInt[--i] == 0);
        short s = (short)(i + -32768);
        int k = arrayOfInt[i];
        while (true) {
          paramArrayOfshort1[--j] = s;
          if (--k <= 0)
            continue label21; 
        } 
      } 
    } else {
      doSort(paramArrayOfshort1, paramInt1, paramInt2, paramArrayOfshort2, paramInt3, paramInt4);
    } 
  }
  
  private static void doSort(short[] paramArrayOfshort1, int paramInt1, int paramInt2, short[] paramArrayOfshort2, int paramInt3, int paramInt4) {
    short[] arrayOfShort;
    int k, m;
    if (paramInt2 - paramInt1 < 286) {
      sort(paramArrayOfshort1, paramInt1, paramInt2, true);
      return;
    } 
    int[] arrayOfInt = new int[68];
    byte b = 0;
    arrayOfInt[0] = paramInt1;
    int i;
    for (i = paramInt1; i < paramInt2; arrayOfInt[b] = i) {
      if (paramArrayOfshort1[i] < paramArrayOfshort1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfshort1[i - 1] <= paramArrayOfshort1[i]);
      } else if (paramArrayOfshort1[i] > paramArrayOfshort1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfshort1[i - 1] >= paramArrayOfshort1[i]);
        for (int i1 = arrayOfInt[b] - 1; ++i1 < --k; ) {
          m = paramArrayOfshort1[i1];
          paramArrayOfshort1[i1] = paramArrayOfshort1[k];
          paramArrayOfshort1[k] = m;
        } 
      } else {
        for (byte b1 = 33; ++i <= paramInt2 && paramArrayOfshort1[i - 1] == paramArrayOfshort1[i];) {
          if (--b1 == 0) {
            sort(paramArrayOfshort1, paramInt1, paramInt2, true);
            return;
          } 
        } 
      } 
      if (++b == 67) {
        sort(paramArrayOfshort1, paramInt1, paramInt2, true);
        return;
      } 
    } 
    if (arrayOfInt[b] == paramInt2++) {
      arrayOfInt[++b] = paramInt2;
    } else if (b == 1) {
      return;
    } 
    i = 0;
    for (int j = 1; (j <<= 1) < b; i = (byte)(i ^ 0x1));
    int n = paramInt2 - paramInt1;
    if (paramArrayOfshort2 == null || paramInt4 < n || paramInt3 + n > paramArrayOfshort2.length) {
      paramArrayOfshort2 = new short[n];
      paramInt3 = 0;
    } 
    if (i == 0) {
      System.arraycopy(paramArrayOfshort1, paramInt1, paramArrayOfshort2, paramInt3, n);
      arrayOfShort = paramArrayOfshort1;
      m = 0;
      paramArrayOfshort1 = paramArrayOfshort2;
      k = paramInt3 - paramInt1;
    } else {
      arrayOfShort = paramArrayOfshort2;
      k = 0;
      m = paramInt3 - paramInt1;
    } 
    for (; b > 1; b = b1) {
      byte b1;
      int i1;
      for (i1 = (b1 = 0) + 2; i1 <= b; i1 += 2) {
        int i3 = arrayOfInt[i1], i4 = arrayOfInt[i1 - 1];
        for (int i5 = arrayOfInt[i1 - 2], i6 = i5, i7 = i4; i5 < i3; i5++) {
          if (i7 >= i3 || (i6 < i4 && paramArrayOfshort1[i6 + k] <= paramArrayOfshort1[i7 + k])) {
            arrayOfShort[i5 + m] = paramArrayOfshort1[i6++ + k];
          } else {
            arrayOfShort[i5 + m] = paramArrayOfshort1[i7++ + k];
          } 
        } 
        arrayOfInt[++b1] = i3;
      } 
      if ((b & 0x1) != 0) {
        int i3;
        for (i1 = paramInt2, i3 = arrayOfInt[b - 1]; --i1 >= i3;)
          arrayOfShort[i1 + m] = paramArrayOfshort1[i1 + k]; 
        arrayOfInt[++b1] = paramInt2;
      } 
      short[] arrayOfShort1 = paramArrayOfshort1;
      paramArrayOfshort1 = arrayOfShort;
      arrayOfShort = arrayOfShort1;
      int i2 = k;
      k = m;
      m = i2;
    } 
  }
  
  private static void sort(short[] paramArrayOfshort, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47) {
      if (paramBoolean) {
        int i6;
        for (int i5 = paramInt1; i5 < paramInt2; i6 = ++i5) {
          short s = paramArrayOfshort[i5 + 1];
          while (s < paramArrayOfshort[i6]) {
            paramArrayOfshort[i6 + 1] = paramArrayOfshort[i6];
            if (i6-- == paramInt1)
              break; 
          } 
          paramArrayOfshort[i6 + 1] = s;
        } 
      } else {
        do {
          if (paramInt1 >= paramInt2)
            return; 
        } while (paramArrayOfshort[++paramInt1] >= paramArrayOfshort[paramInt1 - 1]);
        int i5;
        for (i5 = paramInt1; ++paramInt1 <= paramInt2; i5 = ++paramInt1) {
          short s1 = paramArrayOfshort[i5], s2 = paramArrayOfshort[paramInt1];
          if (s1 < s2) {
            s2 = s1;
            s1 = paramArrayOfshort[paramInt1];
          } 
          while (s1 < paramArrayOfshort[--i5])
            paramArrayOfshort[i5 + 2] = paramArrayOfshort[i5]; 
          paramArrayOfshort[++i5 + 1] = s1;
          while (s2 < paramArrayOfshort[--i5])
            paramArrayOfshort[i5 + 1] = paramArrayOfshort[i5]; 
          paramArrayOfshort[i5 + 1] = s2;
        } 
        i5 = paramArrayOfshort[paramInt2];
        while (i5 < paramArrayOfshort[--paramInt2])
          paramArrayOfshort[paramInt2 + 1] = paramArrayOfshort[paramInt2]; 
        paramArrayOfshort[paramInt2 + 1] = i5;
      } 
      return;
    } 
    int j = (i >> 3) + (i >> 6) + 1;
    int k = paramInt1 + paramInt2 >>> 1;
    int m = k - j;
    int n = m - j;
    int i1 = k + j;
    int i2 = i1 + j;
    if (paramArrayOfshort[m] < paramArrayOfshort[n]) {
      short s = paramArrayOfshort[m];
      paramArrayOfshort[m] = paramArrayOfshort[n];
      paramArrayOfshort[n] = s;
    } 
    if (paramArrayOfshort[k] < paramArrayOfshort[m]) {
      short s = paramArrayOfshort[k];
      paramArrayOfshort[k] = paramArrayOfshort[m];
      paramArrayOfshort[m] = s;
      if (s < paramArrayOfshort[n]) {
        paramArrayOfshort[m] = paramArrayOfshort[n];
        paramArrayOfshort[n] = s;
      } 
    } 
    if (paramArrayOfshort[i1] < paramArrayOfshort[k]) {
      short s = paramArrayOfshort[i1];
      paramArrayOfshort[i1] = paramArrayOfshort[k];
      paramArrayOfshort[k] = s;
      if (s < paramArrayOfshort[m]) {
        paramArrayOfshort[k] = paramArrayOfshort[m];
        paramArrayOfshort[m] = s;
        if (s < paramArrayOfshort[n]) {
          paramArrayOfshort[m] = paramArrayOfshort[n];
          paramArrayOfshort[n] = s;
        } 
      } 
    } 
    if (paramArrayOfshort[i2] < paramArrayOfshort[i1]) {
      short s = paramArrayOfshort[i2];
      paramArrayOfshort[i2] = paramArrayOfshort[i1];
      paramArrayOfshort[i1] = s;
      if (s < paramArrayOfshort[k]) {
        paramArrayOfshort[i1] = paramArrayOfshort[k];
        paramArrayOfshort[k] = s;
        if (s < paramArrayOfshort[m]) {
          paramArrayOfshort[k] = paramArrayOfshort[m];
          paramArrayOfshort[m] = s;
          if (s < paramArrayOfshort[n]) {
            paramArrayOfshort[m] = paramArrayOfshort[n];
            paramArrayOfshort[n] = s;
          } 
        } 
      } 
    } 
    int i3 = paramInt1;
    int i4 = paramInt2;
    if (paramArrayOfshort[n] != paramArrayOfshort[m] && paramArrayOfshort[m] != paramArrayOfshort[k] && paramArrayOfshort[k] != paramArrayOfshort[i1] && paramArrayOfshort[i1] != paramArrayOfshort[i2]) {
      short s1 = paramArrayOfshort[m];
      short s2 = paramArrayOfshort[i1];
      paramArrayOfshort[m] = paramArrayOfshort[paramInt1];
      paramArrayOfshort[i1] = paramArrayOfshort[paramInt2];
      while (paramArrayOfshort[++i3] < s1);
      while (paramArrayOfshort[--i4] > s2);
      int i5;
      label149: for (i5 = i3 - 1; ++i5 <= i4; ) {
        short s = paramArrayOfshort[i5];
        if (s < s1) {
          paramArrayOfshort[i5] = paramArrayOfshort[i3];
          paramArrayOfshort[i3] = s;
          i3++;
          continue;
        } 
        if (s > s2) {
          while (paramArrayOfshort[i4] > s2) {
            if (i4-- == i5)
              break label149; 
          } 
          if (paramArrayOfshort[i4] < s1) {
            paramArrayOfshort[i5] = paramArrayOfshort[i3];
            paramArrayOfshort[i3] = paramArrayOfshort[i4];
            i3++;
          } else {
            paramArrayOfshort[i5] = paramArrayOfshort[i4];
          } 
          paramArrayOfshort[i4] = s;
          i4--;
        } 
      } 
      paramArrayOfshort[paramInt1] = paramArrayOfshort[i3 - 1];
      paramArrayOfshort[i3 - 1] = s1;
      paramArrayOfshort[paramInt2] = paramArrayOfshort[i4 + 1];
      paramArrayOfshort[i4 + 1] = s2;
      sort(paramArrayOfshort, paramInt1, i3 - 2, paramBoolean);
      sort(paramArrayOfshort, i4 + 2, paramInt2, false);
      if (i3 < n && i2 < i4) {
        while (paramArrayOfshort[i3] == s1)
          i3++; 
        while (paramArrayOfshort[i4] == s2)
          i4--; 
        for (i5 = i3 - 1; ++i5 <= i4; ) {
          short s = paramArrayOfshort[i5];
          if (s == s1) {
            paramArrayOfshort[i5] = paramArrayOfshort[i3];
            paramArrayOfshort[i3] = s;
            i3++;
            continue;
          } 
          if (s == s2) {
            while (paramArrayOfshort[i4] == s2) {
              if (i4-- == i5)
                // Byte code: goto -> 1033 
            } 
            if (paramArrayOfshort[i4] == s1) {
              paramArrayOfshort[i5] = paramArrayOfshort[i3];
              paramArrayOfshort[i3] = s1;
              i3++;
            } else {
              paramArrayOfshort[i5] = paramArrayOfshort[i4];
            } 
            paramArrayOfshort[i4] = s;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfshort, i3, i4, false);
    } else {
      short s = paramArrayOfshort[k];
      for (int i5 = i3; i5 <= i4; i5++) {
        if (paramArrayOfshort[i5] != s) {
          short s1 = paramArrayOfshort[i5];
          if (s1 < s) {
            paramArrayOfshort[i5] = paramArrayOfshort[i3];
            paramArrayOfshort[i3] = s1;
            i3++;
          } else {
            while (paramArrayOfshort[i4] > s)
              i4--; 
            if (paramArrayOfshort[i4] < s) {
              paramArrayOfshort[i5] = paramArrayOfshort[i3];
              paramArrayOfshort[i3] = paramArrayOfshort[i4];
              i3++;
            } else {
              paramArrayOfshort[i5] = s;
            } 
            paramArrayOfshort[i4] = s1;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfshort, paramInt1, i3 - 1, paramBoolean);
      sort(paramArrayOfshort, i4 + 1, paramInt2, false);
    } 
  }
  
  static void sort(char[] paramArrayOfchar1, int paramInt1, int paramInt2, char[] paramArrayOfchar2, int paramInt3, int paramInt4) {
    if (paramInt2 - paramInt1 > 3200) {
      int[] arrayOfInt = new int[65536];
      int i;
      for (i = paramInt1 - 1; ++i <= paramInt2;)
        arrayOfInt[paramArrayOfchar1[i]] = arrayOfInt[paramArrayOfchar1[i]] + 1; 
      int j;
      label21: for (i = 65536, j = paramInt2 + 1; j > paramInt1; ) {
        while (arrayOfInt[--i] == 0);
        char c = (char)i;
        int k = arrayOfInt[i];
        while (true) {
          paramArrayOfchar1[--j] = c;
          if (--k <= 0)
            continue label21; 
        } 
      } 
    } else {
      doSort(paramArrayOfchar1, paramInt1, paramInt2, paramArrayOfchar2, paramInt3, paramInt4);
    } 
  }
  
  private static void doSort(char[] paramArrayOfchar1, int paramInt1, int paramInt2, char[] paramArrayOfchar2, int paramInt3, int paramInt4) {
    char[] arrayOfChar;
    int k, m;
    if (paramInt2 - paramInt1 < 286) {
      sort(paramArrayOfchar1, paramInt1, paramInt2, true);
      return;
    } 
    int[] arrayOfInt = new int[68];
    byte b = 0;
    arrayOfInt[0] = paramInt1;
    int i;
    for (i = paramInt1; i < paramInt2; arrayOfInt[b] = i) {
      if (paramArrayOfchar1[i] < paramArrayOfchar1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfchar1[i - 1] <= paramArrayOfchar1[i]);
      } else if (paramArrayOfchar1[i] > paramArrayOfchar1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfchar1[i - 1] >= paramArrayOfchar1[i]);
        for (int i1 = arrayOfInt[b] - 1; ++i1 < --k; ) {
          m = paramArrayOfchar1[i1];
          paramArrayOfchar1[i1] = paramArrayOfchar1[k];
          paramArrayOfchar1[k] = m;
        } 
      } else {
        for (byte b1 = 33; ++i <= paramInt2 && paramArrayOfchar1[i - 1] == paramArrayOfchar1[i];) {
          if (--b1 == 0) {
            sort(paramArrayOfchar1, paramInt1, paramInt2, true);
            return;
          } 
        } 
      } 
      if (++b == 67) {
        sort(paramArrayOfchar1, paramInt1, paramInt2, true);
        return;
      } 
    } 
    if (arrayOfInt[b] == paramInt2++) {
      arrayOfInt[++b] = paramInt2;
    } else if (b == 1) {
      return;
    } 
    i = 0;
    for (int j = 1; (j <<= 1) < b; i = (byte)(i ^ 0x1));
    int n = paramInt2 - paramInt1;
    if (paramArrayOfchar2 == null || paramInt4 < n || paramInt3 + n > paramArrayOfchar2.length) {
      paramArrayOfchar2 = new char[n];
      paramInt3 = 0;
    } 
    if (i == 0) {
      System.arraycopy(paramArrayOfchar1, paramInt1, paramArrayOfchar2, paramInt3, n);
      arrayOfChar = paramArrayOfchar1;
      m = 0;
      paramArrayOfchar1 = paramArrayOfchar2;
      k = paramInt3 - paramInt1;
    } else {
      arrayOfChar = paramArrayOfchar2;
      k = 0;
      m = paramInt3 - paramInt1;
    } 
    for (; b > 1; b = b1) {
      byte b1;
      int i1;
      for (i1 = (b1 = 0) + 2; i1 <= b; i1 += 2) {
        int i3 = arrayOfInt[i1], i4 = arrayOfInt[i1 - 1];
        for (int i5 = arrayOfInt[i1 - 2], i6 = i5, i7 = i4; i5 < i3; i5++) {
          if (i7 >= i3 || (i6 < i4 && paramArrayOfchar1[i6 + k] <= paramArrayOfchar1[i7 + k])) {
            arrayOfChar[i5 + m] = paramArrayOfchar1[i6++ + k];
          } else {
            arrayOfChar[i5 + m] = paramArrayOfchar1[i7++ + k];
          } 
        } 
        arrayOfInt[++b1] = i3;
      } 
      if ((b & 0x1) != 0) {
        int i3;
        for (i1 = paramInt2, i3 = arrayOfInt[b - 1]; --i1 >= i3;)
          arrayOfChar[i1 + m] = paramArrayOfchar1[i1 + k]; 
        arrayOfInt[++b1] = paramInt2;
      } 
      char[] arrayOfChar1 = paramArrayOfchar1;
      paramArrayOfchar1 = arrayOfChar;
      arrayOfChar = arrayOfChar1;
      int i2 = k;
      k = m;
      m = i2;
    } 
  }
  
  private static void sort(char[] paramArrayOfchar, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47) {
      if (paramBoolean) {
        int i6;
        for (int i5 = paramInt1; i5 < paramInt2; i6 = ++i5) {
          char c = paramArrayOfchar[i5 + 1];
          while (c < paramArrayOfchar[i6]) {
            paramArrayOfchar[i6 + 1] = paramArrayOfchar[i6];
            if (i6-- == paramInt1)
              break; 
          } 
          paramArrayOfchar[i6 + 1] = c;
        } 
      } else {
        do {
          if (paramInt1 >= paramInt2)
            return; 
        } while (paramArrayOfchar[++paramInt1] >= paramArrayOfchar[paramInt1 - 1]);
        int i5;
        for (i5 = paramInt1; ++paramInt1 <= paramInt2; i5 = ++paramInt1) {
          char c1 = paramArrayOfchar[i5], c2 = paramArrayOfchar[paramInt1];
          if (c1 < c2) {
            c2 = c1;
            c1 = paramArrayOfchar[paramInt1];
          } 
          while (c1 < paramArrayOfchar[--i5])
            paramArrayOfchar[i5 + 2] = paramArrayOfchar[i5]; 
          paramArrayOfchar[++i5 + 1] = c1;
          while (c2 < paramArrayOfchar[--i5])
            paramArrayOfchar[i5 + 1] = paramArrayOfchar[i5]; 
          paramArrayOfchar[i5 + 1] = c2;
        } 
        i5 = paramArrayOfchar[paramInt2];
        while (i5 < paramArrayOfchar[--paramInt2])
          paramArrayOfchar[paramInt2 + 1] = paramArrayOfchar[paramInt2]; 
        paramArrayOfchar[paramInt2 + 1] = i5;
      } 
      return;
    } 
    int j = (i >> 3) + (i >> 6) + 1;
    int k = paramInt1 + paramInt2 >>> 1;
    int m = k - j;
    int n = m - j;
    int i1 = k + j;
    int i2 = i1 + j;
    if (paramArrayOfchar[m] < paramArrayOfchar[n]) {
      char c = paramArrayOfchar[m];
      paramArrayOfchar[m] = paramArrayOfchar[n];
      paramArrayOfchar[n] = c;
    } 
    if (paramArrayOfchar[k] < paramArrayOfchar[m]) {
      char c = paramArrayOfchar[k];
      paramArrayOfchar[k] = paramArrayOfchar[m];
      paramArrayOfchar[m] = c;
      if (c < paramArrayOfchar[n]) {
        paramArrayOfchar[m] = paramArrayOfchar[n];
        paramArrayOfchar[n] = c;
      } 
    } 
    if (paramArrayOfchar[i1] < paramArrayOfchar[k]) {
      char c = paramArrayOfchar[i1];
      paramArrayOfchar[i1] = paramArrayOfchar[k];
      paramArrayOfchar[k] = c;
      if (c < paramArrayOfchar[m]) {
        paramArrayOfchar[k] = paramArrayOfchar[m];
        paramArrayOfchar[m] = c;
        if (c < paramArrayOfchar[n]) {
          paramArrayOfchar[m] = paramArrayOfchar[n];
          paramArrayOfchar[n] = c;
        } 
      } 
    } 
    if (paramArrayOfchar[i2] < paramArrayOfchar[i1]) {
      char c = paramArrayOfchar[i2];
      paramArrayOfchar[i2] = paramArrayOfchar[i1];
      paramArrayOfchar[i1] = c;
      if (c < paramArrayOfchar[k]) {
        paramArrayOfchar[i1] = paramArrayOfchar[k];
        paramArrayOfchar[k] = c;
        if (c < paramArrayOfchar[m]) {
          paramArrayOfchar[k] = paramArrayOfchar[m];
          paramArrayOfchar[m] = c;
          if (c < paramArrayOfchar[n]) {
            paramArrayOfchar[m] = paramArrayOfchar[n];
            paramArrayOfchar[n] = c;
          } 
        } 
      } 
    } 
    int i3 = paramInt1;
    int i4 = paramInt2;
    if (paramArrayOfchar[n] != paramArrayOfchar[m] && paramArrayOfchar[m] != paramArrayOfchar[k] && paramArrayOfchar[k] != paramArrayOfchar[i1] && paramArrayOfchar[i1] != paramArrayOfchar[i2]) {
      char c1 = paramArrayOfchar[m];
      char c2 = paramArrayOfchar[i1];
      paramArrayOfchar[m] = paramArrayOfchar[paramInt1];
      paramArrayOfchar[i1] = paramArrayOfchar[paramInt2];
      while (paramArrayOfchar[++i3] < c1);
      while (paramArrayOfchar[--i4] > c2);
      int i5;
      label149: for (i5 = i3 - 1; ++i5 <= i4; ) {
        char c = paramArrayOfchar[i5];
        if (c < c1) {
          paramArrayOfchar[i5] = paramArrayOfchar[i3];
          paramArrayOfchar[i3] = c;
          i3++;
          continue;
        } 
        if (c > c2) {
          while (paramArrayOfchar[i4] > c2) {
            if (i4-- == i5)
              break label149; 
          } 
          if (paramArrayOfchar[i4] < c1) {
            paramArrayOfchar[i5] = paramArrayOfchar[i3];
            paramArrayOfchar[i3] = paramArrayOfchar[i4];
            i3++;
          } else {
            paramArrayOfchar[i5] = paramArrayOfchar[i4];
          } 
          paramArrayOfchar[i4] = c;
          i4--;
        } 
      } 
      paramArrayOfchar[paramInt1] = paramArrayOfchar[i3 - 1];
      paramArrayOfchar[i3 - 1] = c1;
      paramArrayOfchar[paramInt2] = paramArrayOfchar[i4 + 1];
      paramArrayOfchar[i4 + 1] = c2;
      sort(paramArrayOfchar, paramInt1, i3 - 2, paramBoolean);
      sort(paramArrayOfchar, i4 + 2, paramInt2, false);
      if (i3 < n && i2 < i4) {
        while (paramArrayOfchar[i3] == c1)
          i3++; 
        while (paramArrayOfchar[i4] == c2)
          i4--; 
        for (i5 = i3 - 1; ++i5 <= i4; ) {
          char c = paramArrayOfchar[i5];
          if (c == c1) {
            paramArrayOfchar[i5] = paramArrayOfchar[i3];
            paramArrayOfchar[i3] = c;
            i3++;
            continue;
          } 
          if (c == c2) {
            while (paramArrayOfchar[i4] == c2) {
              if (i4-- == i5)
                // Byte code: goto -> 1033 
            } 
            if (paramArrayOfchar[i4] == c1) {
              paramArrayOfchar[i5] = paramArrayOfchar[i3];
              paramArrayOfchar[i3] = c1;
              i3++;
            } else {
              paramArrayOfchar[i5] = paramArrayOfchar[i4];
            } 
            paramArrayOfchar[i4] = c;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfchar, i3, i4, false);
    } else {
      char c = paramArrayOfchar[k];
      for (int i5 = i3; i5 <= i4; i5++) {
        if (paramArrayOfchar[i5] != c) {
          char c1 = paramArrayOfchar[i5];
          if (c1 < c) {
            paramArrayOfchar[i5] = paramArrayOfchar[i3];
            paramArrayOfchar[i3] = c1;
            i3++;
          } else {
            while (paramArrayOfchar[i4] > c)
              i4--; 
            if (paramArrayOfchar[i4] < c) {
              paramArrayOfchar[i5] = paramArrayOfchar[i3];
              paramArrayOfchar[i3] = paramArrayOfchar[i4];
              i3++;
            } else {
              paramArrayOfchar[i5] = c;
            } 
            paramArrayOfchar[i4] = c1;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfchar, paramInt1, i3 - 1, paramBoolean);
      sort(paramArrayOfchar, i4 + 1, paramInt2, false);
    } 
  }
  
  static void sort(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramInt2 - paramInt1 > 29) {
      int[] arrayOfInt = new int[256];
      int i;
      for (i = paramInt1 - 1; ++i <= paramInt2;)
        arrayOfInt[paramArrayOfbyte[i] - -128] = arrayOfInt[paramArrayOfbyte[i] - -128] + 1; 
      int j;
      label31: for (i = 256, j = paramInt2 + 1; j > paramInt1; ) {
        while (arrayOfInt[--i] == 0);
        byte b = (byte)(i + -128);
        int k = arrayOfInt[i];
        while (true) {
          paramArrayOfbyte[--j] = b;
          if (--k <= 0)
            continue label31; 
        } 
      } 
    } else {
      int j;
      for (int i = paramInt1; i < paramInt2; j = ++i) {
        byte b = paramArrayOfbyte[i + 1];
        while (b < paramArrayOfbyte[j]) {
          paramArrayOfbyte[j + 1] = paramArrayOfbyte[j];
          if (j-- == paramInt1)
            break; 
        } 
        paramArrayOfbyte[j + 1] = b;
      } 
    } 
  }
  
  static void sort(float[] paramArrayOffloat1, int paramInt1, int paramInt2, float[] paramArrayOffloat2, int paramInt3, int paramInt4) {
    while (paramInt1 <= paramInt2 && Float.isNaN(paramArrayOffloat1[paramInt2]))
      paramInt2--; 
    int i;
    for (i = paramInt2; --i >= paramInt1; ) {
      float f = paramArrayOffloat1[i];
      if (f != f) {
        paramArrayOffloat1[i] = paramArrayOffloat1[paramInt2];
        paramArrayOffloat1[paramInt2] = f;
        paramInt2--;
      } 
    } 
    doSort(paramArrayOffloat1, paramInt1, paramInt2, paramArrayOffloat2, paramInt3, paramInt4);
    i = paramInt2;
    while (paramInt1 < i) {
      int m = paramInt1 + i >>> 1;
      float f = paramArrayOffloat1[m];
      if (f < 0.0F) {
        paramInt1 = m + 1;
        continue;
      } 
      i = m;
    } 
    while (paramInt1 <= paramInt2 && Float.floatToRawIntBits(paramArrayOffloat1[paramInt1]) < 0)
      paramInt1++; 
    for (int j = paramInt1, k = paramInt1 - 1; ++j <= paramInt2; ) {
      float f = paramArrayOffloat1[j];
      if (f != 0.0F)
        break; 
      if (Float.floatToRawIntBits(f) < 0) {
        paramArrayOffloat1[j] = 0.0F;
        paramArrayOffloat1[++k] = -0.0F;
      } 
    } 
  }
  
  private static void doSort(float[] paramArrayOffloat1, int paramInt1, int paramInt2, float[] paramArrayOffloat2, int paramInt3, int paramInt4) {
    float[] arrayOfFloat;
    int k, m;
    if (paramInt2 - paramInt1 < 286) {
      sort(paramArrayOffloat1, paramInt1, paramInt2, true);
      return;
    } 
    int[] arrayOfInt = new int[68];
    byte b = 0;
    arrayOfInt[0] = paramInt1;
    int i;
    for (i = paramInt1; i < paramInt2; arrayOfInt[b] = i) {
      if (paramArrayOffloat1[i] < paramArrayOffloat1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOffloat1[i - 1] <= paramArrayOffloat1[i]);
      } else if (paramArrayOffloat1[i] > paramArrayOffloat1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOffloat1[i - 1] >= paramArrayOffloat1[i]);
        for (int i1 = arrayOfInt[b] - 1; ++i1 < --k; ) {
          float f = paramArrayOffloat1[i1];
          paramArrayOffloat1[i1] = paramArrayOffloat1[k];
          paramArrayOffloat1[k] = f;
        } 
      } else {
        for (byte b1 = 33; ++i <= paramInt2 && paramArrayOffloat1[i - 1] == paramArrayOffloat1[i];) {
          if (--b1 == 0) {
            sort(paramArrayOffloat1, paramInt1, paramInt2, true);
            return;
          } 
        } 
      } 
      if (++b == 67) {
        sort(paramArrayOffloat1, paramInt1, paramInt2, true);
        return;
      } 
    } 
    if (arrayOfInt[b] == paramInt2++) {
      arrayOfInt[++b] = paramInt2;
    } else if (b == 1) {
      return;
    } 
    i = 0;
    for (int j = 1; (j <<= 1) < b; i = (byte)(i ^ 0x1));
    int n = paramInt2 - paramInt1;
    if (paramArrayOffloat2 == null || paramInt4 < n || paramInt3 + n > paramArrayOffloat2.length) {
      paramArrayOffloat2 = new float[n];
      paramInt3 = 0;
    } 
    if (i == 0) {
      System.arraycopy(paramArrayOffloat1, paramInt1, paramArrayOffloat2, paramInt3, n);
      arrayOfFloat = paramArrayOffloat1;
      m = 0;
      paramArrayOffloat1 = paramArrayOffloat2;
      k = paramInt3 - paramInt1;
    } else {
      arrayOfFloat = paramArrayOffloat2;
      k = 0;
      m = paramInt3 - paramInt1;
    } 
    for (; b > 1; b = b1) {
      byte b1;
      int i1;
      for (i1 = (b1 = 0) + 2; i1 <= b; i1 += 2) {
        int i3 = arrayOfInt[i1], i4 = arrayOfInt[i1 - 1];
        for (int i5 = arrayOfInt[i1 - 2], i6 = i5, i7 = i4; i5 < i3; i5++) {
          if (i7 >= i3 || (i6 < i4 && paramArrayOffloat1[i6 + k] <= paramArrayOffloat1[i7 + k])) {
            arrayOfFloat[i5 + m] = paramArrayOffloat1[i6++ + k];
          } else {
            arrayOfFloat[i5 + m] = paramArrayOffloat1[i7++ + k];
          } 
        } 
        arrayOfInt[++b1] = i3;
      } 
      if ((b & 0x1) != 0) {
        int i3;
        for (i1 = paramInt2, i3 = arrayOfInt[b - 1]; --i1 >= i3;)
          arrayOfFloat[i1 + m] = paramArrayOffloat1[i1 + k]; 
        arrayOfInt[++b1] = paramInt2;
      } 
      float[] arrayOfFloat1 = paramArrayOffloat1;
      paramArrayOffloat1 = arrayOfFloat;
      arrayOfFloat = arrayOfFloat1;
      int i2 = k;
      k = m;
      m = i2;
    } 
  }
  
  private static void sort(float[] paramArrayOffloat, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47) {
      if (paramBoolean) {
        int i6;
        for (int i5 = paramInt1; i5 < paramInt2; i6 = ++i5) {
          float f = paramArrayOffloat[i5 + 1];
          while (f < paramArrayOffloat[i6]) {
            paramArrayOffloat[i6 + 1] = paramArrayOffloat[i6];
            if (i6-- == paramInt1)
              break; 
          } 
          paramArrayOffloat[i6 + 1] = f;
        } 
      } else {
        do {
          if (paramInt1 >= paramInt2)
            return; 
        } while (paramArrayOffloat[++paramInt1] >= paramArrayOffloat[paramInt1 - 1]);
        int i5;
        for (i5 = paramInt1; ++paramInt1 <= paramInt2; i5 = ++paramInt1) {
          float f1 = paramArrayOffloat[i5], f2 = paramArrayOffloat[paramInt1];
          if (f1 < f2) {
            f2 = f1;
            f1 = paramArrayOffloat[paramInt1];
          } 
          while (f1 < paramArrayOffloat[--i5])
            paramArrayOffloat[i5 + 2] = paramArrayOffloat[i5]; 
          paramArrayOffloat[++i5 + 1] = f1;
          while (f2 < paramArrayOffloat[--i5])
            paramArrayOffloat[i5 + 1] = paramArrayOffloat[i5]; 
          paramArrayOffloat[i5 + 1] = f2;
        } 
        float f = paramArrayOffloat[paramInt2];
        while (f < paramArrayOffloat[--paramInt2])
          paramArrayOffloat[paramInt2 + 1] = paramArrayOffloat[paramInt2]; 
        paramArrayOffloat[paramInt2 + 1] = f;
      } 
      return;
    } 
    int j = (i >> 3) + (i >> 6) + 1;
    int k = paramInt1 + paramInt2 >>> 1;
    int m = k - j;
    int n = m - j;
    int i1 = k + j;
    int i2 = i1 + j;
    if (paramArrayOffloat[m] < paramArrayOffloat[n]) {
      float f = paramArrayOffloat[m];
      paramArrayOffloat[m] = paramArrayOffloat[n];
      paramArrayOffloat[n] = f;
    } 
    if (paramArrayOffloat[k] < paramArrayOffloat[m]) {
      float f = paramArrayOffloat[k];
      paramArrayOffloat[k] = paramArrayOffloat[m];
      paramArrayOffloat[m] = f;
      if (f < paramArrayOffloat[n]) {
        paramArrayOffloat[m] = paramArrayOffloat[n];
        paramArrayOffloat[n] = f;
      } 
    } 
    if (paramArrayOffloat[i1] < paramArrayOffloat[k]) {
      float f = paramArrayOffloat[i1];
      paramArrayOffloat[i1] = paramArrayOffloat[k];
      paramArrayOffloat[k] = f;
      if (f < paramArrayOffloat[m]) {
        paramArrayOffloat[k] = paramArrayOffloat[m];
        paramArrayOffloat[m] = f;
        if (f < paramArrayOffloat[n]) {
          paramArrayOffloat[m] = paramArrayOffloat[n];
          paramArrayOffloat[n] = f;
        } 
      } 
    } 
    if (paramArrayOffloat[i2] < paramArrayOffloat[i1]) {
      float f = paramArrayOffloat[i2];
      paramArrayOffloat[i2] = paramArrayOffloat[i1];
      paramArrayOffloat[i1] = f;
      if (f < paramArrayOffloat[k]) {
        paramArrayOffloat[i1] = paramArrayOffloat[k];
        paramArrayOffloat[k] = f;
        if (f < paramArrayOffloat[m]) {
          paramArrayOffloat[k] = paramArrayOffloat[m];
          paramArrayOffloat[m] = f;
          if (f < paramArrayOffloat[n]) {
            paramArrayOffloat[m] = paramArrayOffloat[n];
            paramArrayOffloat[n] = f;
          } 
        } 
      } 
    } 
    int i3 = paramInt1;
    int i4 = paramInt2;
    if (paramArrayOffloat[n] != paramArrayOffloat[m] && paramArrayOffloat[m] != paramArrayOffloat[k] && paramArrayOffloat[k] != paramArrayOffloat[i1] && paramArrayOffloat[i1] != paramArrayOffloat[i2]) {
      float f1 = paramArrayOffloat[m];
      float f2 = paramArrayOffloat[i1];
      paramArrayOffloat[m] = paramArrayOffloat[paramInt1];
      paramArrayOffloat[i1] = paramArrayOffloat[paramInt2];
      while (paramArrayOffloat[++i3] < f1);
      while (paramArrayOffloat[--i4] > f2);
      int i5;
      label149: for (i5 = i3 - 1; ++i5 <= i4; ) {
        float f = paramArrayOffloat[i5];
        if (f < f1) {
          paramArrayOffloat[i5] = paramArrayOffloat[i3];
          paramArrayOffloat[i3] = f;
          i3++;
          continue;
        } 
        if (f > f2) {
          while (paramArrayOffloat[i4] > f2) {
            if (i4-- == i5)
              break label149; 
          } 
          if (paramArrayOffloat[i4] < f1) {
            paramArrayOffloat[i5] = paramArrayOffloat[i3];
            paramArrayOffloat[i3] = paramArrayOffloat[i4];
            i3++;
          } else {
            paramArrayOffloat[i5] = paramArrayOffloat[i4];
          } 
          paramArrayOffloat[i4] = f;
          i4--;
        } 
      } 
      paramArrayOffloat[paramInt1] = paramArrayOffloat[i3 - 1];
      paramArrayOffloat[i3 - 1] = f1;
      paramArrayOffloat[paramInt2] = paramArrayOffloat[i4 + 1];
      paramArrayOffloat[i4 + 1] = f2;
      sort(paramArrayOffloat, paramInt1, i3 - 2, paramBoolean);
      sort(paramArrayOffloat, i4 + 2, paramInt2, false);
      if (i3 < n && i2 < i4) {
        while (paramArrayOffloat[i3] == f1)
          i3++; 
        while (paramArrayOffloat[i4] == f2)
          i4--; 
        for (i5 = i3 - 1; ++i5 <= i4; ) {
          float f = paramArrayOffloat[i5];
          if (f == f1) {
            paramArrayOffloat[i5] = paramArrayOffloat[i3];
            paramArrayOffloat[i3] = f;
            i3++;
            continue;
          } 
          if (f == f2) {
            while (paramArrayOffloat[i4] == f2) {
              if (i4-- == i5)
                // Byte code: goto -> 1067 
            } 
            if (paramArrayOffloat[i4] == f1) {
              paramArrayOffloat[i5] = paramArrayOffloat[i3];
              paramArrayOffloat[i3] = paramArrayOffloat[i4];
              i3++;
            } else {
              paramArrayOffloat[i5] = paramArrayOffloat[i4];
            } 
            paramArrayOffloat[i4] = f;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOffloat, i3, i4, false);
    } else {
      float f = paramArrayOffloat[k];
      for (int i5 = i3; i5 <= i4; i5++) {
        if (paramArrayOffloat[i5] != f) {
          float f1 = paramArrayOffloat[i5];
          if (f1 < f) {
            paramArrayOffloat[i5] = paramArrayOffloat[i3];
            paramArrayOffloat[i3] = f1;
            i3++;
          } else {
            while (paramArrayOffloat[i4] > f)
              i4--; 
            if (paramArrayOffloat[i4] < f) {
              paramArrayOffloat[i5] = paramArrayOffloat[i3];
              paramArrayOffloat[i3] = paramArrayOffloat[i4];
              i3++;
            } else {
              paramArrayOffloat[i5] = paramArrayOffloat[i4];
            } 
            paramArrayOffloat[i4] = f1;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOffloat, paramInt1, i3 - 1, paramBoolean);
      sort(paramArrayOffloat, i4 + 1, paramInt2, false);
    } 
  }
  
  static void sort(double[] paramArrayOfdouble1, int paramInt1, int paramInt2, double[] paramArrayOfdouble2, int paramInt3, int paramInt4) {
    while (paramInt1 <= paramInt2 && Double.isNaN(paramArrayOfdouble1[paramInt2]))
      paramInt2--; 
    int i;
    for (i = paramInt2; --i >= paramInt1; ) {
      double d = paramArrayOfdouble1[i];
      if (d != d) {
        paramArrayOfdouble1[i] = paramArrayOfdouble1[paramInt2];
        paramArrayOfdouble1[paramInt2] = d;
        paramInt2--;
      } 
    } 
    doSort(paramArrayOfdouble1, paramInt1, paramInt2, paramArrayOfdouble2, paramInt3, paramInt4);
    i = paramInt2;
    while (paramInt1 < i) {
      int m = paramInt1 + i >>> 1;
      double d = paramArrayOfdouble1[m];
      if (d < 0.0D) {
        paramInt1 = m + 1;
        continue;
      } 
      i = m;
    } 
    while (paramInt1 <= paramInt2 && Double.doubleToRawLongBits(paramArrayOfdouble1[paramInt1]) < 0L)
      paramInt1++; 
    for (int j = paramInt1, k = paramInt1 - 1; ++j <= paramInt2; ) {
      double d = paramArrayOfdouble1[j];
      if (d != 0.0D)
        break; 
      if (Double.doubleToRawLongBits(d) < 0L) {
        paramArrayOfdouble1[j] = 0.0D;
        paramArrayOfdouble1[++k] = -0.0D;
      } 
    } 
  }
  
  private static void doSort(double[] paramArrayOfdouble1, int paramInt1, int paramInt2, double[] paramArrayOfdouble2, int paramInt3, int paramInt4) {
    double[] arrayOfDouble;
    int k, m;
    if (paramInt2 - paramInt1 < 286) {
      sort(paramArrayOfdouble1, paramInt1, paramInt2, true);
      return;
    } 
    int[] arrayOfInt = new int[68];
    byte b = 0;
    arrayOfInt[0] = paramInt1;
    int i;
    for (i = paramInt1; i < paramInt2; arrayOfInt[b] = i) {
      if (paramArrayOfdouble1[i] < paramArrayOfdouble1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfdouble1[i - 1] <= paramArrayOfdouble1[i]);
      } else if (paramArrayOfdouble1[i] > paramArrayOfdouble1[i + 1]) {
        while (++i <= paramInt2 && paramArrayOfdouble1[i - 1] >= paramArrayOfdouble1[i]);
        for (int i1 = arrayOfInt[b] - 1; ++i1 < --k; ) {
          double d = paramArrayOfdouble1[i1];
          paramArrayOfdouble1[i1] = paramArrayOfdouble1[k];
          paramArrayOfdouble1[k] = d;
        } 
      } else {
        for (byte b1 = 33; ++i <= paramInt2 && paramArrayOfdouble1[i - 1] == paramArrayOfdouble1[i];) {
          if (--b1 == 0) {
            sort(paramArrayOfdouble1, paramInt1, paramInt2, true);
            return;
          } 
        } 
      } 
      if (++b == 67) {
        sort(paramArrayOfdouble1, paramInt1, paramInt2, true);
        return;
      } 
    } 
    if (arrayOfInt[b] == paramInt2++) {
      arrayOfInt[++b] = paramInt2;
    } else if (b == 1) {
      return;
    } 
    i = 0;
    for (int j = 1; (j <<= 1) < b; i = (byte)(i ^ 0x1));
    int n = paramInt2 - paramInt1;
    if (paramArrayOfdouble2 == null || paramInt4 < n || paramInt3 + n > paramArrayOfdouble2.length) {
      paramArrayOfdouble2 = new double[n];
      paramInt3 = 0;
    } 
    if (i == 0) {
      System.arraycopy(paramArrayOfdouble1, paramInt1, paramArrayOfdouble2, paramInt3, n);
      arrayOfDouble = paramArrayOfdouble1;
      m = 0;
      paramArrayOfdouble1 = paramArrayOfdouble2;
      k = paramInt3 - paramInt1;
    } else {
      arrayOfDouble = paramArrayOfdouble2;
      k = 0;
      m = paramInt3 - paramInt1;
    } 
    for (; b > 1; b = b1) {
      byte b1;
      int i1;
      for (i1 = (b1 = 0) + 2; i1 <= b; i1 += 2) {
        int i3 = arrayOfInt[i1], i4 = arrayOfInt[i1 - 1];
        for (int i5 = arrayOfInt[i1 - 2], i6 = i5, i7 = i4; i5 < i3; i5++) {
          if (i7 >= i3 || (i6 < i4 && paramArrayOfdouble1[i6 + k] <= paramArrayOfdouble1[i7 + k])) {
            arrayOfDouble[i5 + m] = paramArrayOfdouble1[i6++ + k];
          } else {
            arrayOfDouble[i5 + m] = paramArrayOfdouble1[i7++ + k];
          } 
        } 
        arrayOfInt[++b1] = i3;
      } 
      if ((b & 0x1) != 0) {
        int i3;
        for (i1 = paramInt2, i3 = arrayOfInt[b - 1]; --i1 >= i3;)
          arrayOfDouble[i1 + m] = paramArrayOfdouble1[i1 + k]; 
        arrayOfInt[++b1] = paramInt2;
      } 
      double[] arrayOfDouble1 = paramArrayOfdouble1;
      paramArrayOfdouble1 = arrayOfDouble;
      arrayOfDouble = arrayOfDouble1;
      int i2 = k;
      k = m;
      m = i2;
    } 
  }
  
  private static void sort(double[] paramArrayOfdouble, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = paramInt2 - paramInt1 + 1;
    if (i < 47) {
      if (paramBoolean) {
        int i6;
        for (int i5 = paramInt1; i5 < paramInt2; i6 = ++i5) {
          double d = paramArrayOfdouble[i5 + 1];
          while (d < paramArrayOfdouble[i6]) {
            paramArrayOfdouble[i6 + 1] = paramArrayOfdouble[i6];
            if (i6-- == paramInt1)
              break; 
          } 
          paramArrayOfdouble[i6 + 1] = d;
        } 
      } else {
        do {
          if (paramInt1 >= paramInt2)
            return; 
        } while (paramArrayOfdouble[++paramInt1] >= paramArrayOfdouble[paramInt1 - 1]);
        int i5;
        for (i5 = paramInt1; ++paramInt1 <= paramInt2; i5 = ++paramInt1) {
          double d1 = paramArrayOfdouble[i5], d2 = paramArrayOfdouble[paramInt1];
          if (d1 < d2) {
            d2 = d1;
            d1 = paramArrayOfdouble[paramInt1];
          } 
          while (d1 < paramArrayOfdouble[--i5])
            paramArrayOfdouble[i5 + 2] = paramArrayOfdouble[i5]; 
          paramArrayOfdouble[++i5 + 1] = d1;
          while (d2 < paramArrayOfdouble[--i5])
            paramArrayOfdouble[i5 + 1] = paramArrayOfdouble[i5]; 
          paramArrayOfdouble[i5 + 1] = d2;
        } 
        double d = paramArrayOfdouble[paramInt2];
        while (d < paramArrayOfdouble[--paramInt2])
          paramArrayOfdouble[paramInt2 + 1] = paramArrayOfdouble[paramInt2]; 
        paramArrayOfdouble[paramInt2 + 1] = d;
      } 
      return;
    } 
    int j = (i >> 3) + (i >> 6) + 1;
    int k = paramInt1 + paramInt2 >>> 1;
    int m = k - j;
    int n = m - j;
    int i1 = k + j;
    int i2 = i1 + j;
    if (paramArrayOfdouble[m] < paramArrayOfdouble[n]) {
      double d = paramArrayOfdouble[m];
      paramArrayOfdouble[m] = paramArrayOfdouble[n];
      paramArrayOfdouble[n] = d;
    } 
    if (paramArrayOfdouble[k] < paramArrayOfdouble[m]) {
      double d = paramArrayOfdouble[k];
      paramArrayOfdouble[k] = paramArrayOfdouble[m];
      paramArrayOfdouble[m] = d;
      if (d < paramArrayOfdouble[n]) {
        paramArrayOfdouble[m] = paramArrayOfdouble[n];
        paramArrayOfdouble[n] = d;
      } 
    } 
    if (paramArrayOfdouble[i1] < paramArrayOfdouble[k]) {
      double d = paramArrayOfdouble[i1];
      paramArrayOfdouble[i1] = paramArrayOfdouble[k];
      paramArrayOfdouble[k] = d;
      if (d < paramArrayOfdouble[m]) {
        paramArrayOfdouble[k] = paramArrayOfdouble[m];
        paramArrayOfdouble[m] = d;
        if (d < paramArrayOfdouble[n]) {
          paramArrayOfdouble[m] = paramArrayOfdouble[n];
          paramArrayOfdouble[n] = d;
        } 
      } 
    } 
    if (paramArrayOfdouble[i2] < paramArrayOfdouble[i1]) {
      double d = paramArrayOfdouble[i2];
      paramArrayOfdouble[i2] = paramArrayOfdouble[i1];
      paramArrayOfdouble[i1] = d;
      if (d < paramArrayOfdouble[k]) {
        paramArrayOfdouble[i1] = paramArrayOfdouble[k];
        paramArrayOfdouble[k] = d;
        if (d < paramArrayOfdouble[m]) {
          paramArrayOfdouble[k] = paramArrayOfdouble[m];
          paramArrayOfdouble[m] = d;
          if (d < paramArrayOfdouble[n]) {
            paramArrayOfdouble[m] = paramArrayOfdouble[n];
            paramArrayOfdouble[n] = d;
          } 
        } 
      } 
    } 
    int i3 = paramInt1;
    int i4 = paramInt2;
    if (paramArrayOfdouble[n] != paramArrayOfdouble[m] && paramArrayOfdouble[m] != paramArrayOfdouble[k] && paramArrayOfdouble[k] != paramArrayOfdouble[i1] && paramArrayOfdouble[i1] != paramArrayOfdouble[i2]) {
      double d1 = paramArrayOfdouble[m];
      double d2 = paramArrayOfdouble[i1];
      paramArrayOfdouble[m] = paramArrayOfdouble[paramInt1];
      paramArrayOfdouble[i1] = paramArrayOfdouble[paramInt2];
      while (paramArrayOfdouble[++i3] < d1);
      while (paramArrayOfdouble[--i4] > d2);
      int i5;
      label149: for (i5 = i3 - 1; ++i5 <= i4; ) {
        double d = paramArrayOfdouble[i5];
        if (d < d1) {
          paramArrayOfdouble[i5] = paramArrayOfdouble[i3];
          paramArrayOfdouble[i3] = d;
          i3++;
          continue;
        } 
        if (d > d2) {
          while (paramArrayOfdouble[i4] > d2) {
            if (i4-- == i5)
              break label149; 
          } 
          if (paramArrayOfdouble[i4] < d1) {
            paramArrayOfdouble[i5] = paramArrayOfdouble[i3];
            paramArrayOfdouble[i3] = paramArrayOfdouble[i4];
            i3++;
          } else {
            paramArrayOfdouble[i5] = paramArrayOfdouble[i4];
          } 
          paramArrayOfdouble[i4] = d;
          i4--;
        } 
      } 
      paramArrayOfdouble[paramInt1] = paramArrayOfdouble[i3 - 1];
      paramArrayOfdouble[i3 - 1] = d1;
      paramArrayOfdouble[paramInt2] = paramArrayOfdouble[i4 + 1];
      paramArrayOfdouble[i4 + 1] = d2;
      sort(paramArrayOfdouble, paramInt1, i3 - 2, paramBoolean);
      sort(paramArrayOfdouble, i4 + 2, paramInt2, false);
      if (i3 < n && i2 < i4) {
        while (paramArrayOfdouble[i3] == d1)
          i3++; 
        while (paramArrayOfdouble[i4] == d2)
          i4--; 
        for (i5 = i3 - 1; ++i5 <= i4; ) {
          double d = paramArrayOfdouble[i5];
          if (d == d1) {
            paramArrayOfdouble[i5] = paramArrayOfdouble[i3];
            paramArrayOfdouble[i3] = d;
            i3++;
            continue;
          } 
          if (d == d2) {
            while (paramArrayOfdouble[i4] == d2) {
              if (i4-- == i5)
                // Byte code: goto -> 1067 
            } 
            if (paramArrayOfdouble[i4] == d1) {
              paramArrayOfdouble[i5] = paramArrayOfdouble[i3];
              paramArrayOfdouble[i3] = paramArrayOfdouble[i4];
              i3++;
            } else {
              paramArrayOfdouble[i5] = paramArrayOfdouble[i4];
            } 
            paramArrayOfdouble[i4] = d;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfdouble, i3, i4, false);
    } else {
      double d = paramArrayOfdouble[k];
      for (int i5 = i3; i5 <= i4; i5++) {
        if (paramArrayOfdouble[i5] != d) {
          double d1 = paramArrayOfdouble[i5];
          if (d1 < d) {
            paramArrayOfdouble[i5] = paramArrayOfdouble[i3];
            paramArrayOfdouble[i3] = d1;
            i3++;
          } else {
            while (paramArrayOfdouble[i4] > d)
              i4--; 
            if (paramArrayOfdouble[i4] < d) {
              paramArrayOfdouble[i5] = paramArrayOfdouble[i3];
              paramArrayOfdouble[i3] = paramArrayOfdouble[i4];
              i3++;
            } else {
              paramArrayOfdouble[i5] = paramArrayOfdouble[i4];
            } 
            paramArrayOfdouble[i4] = d1;
            i4--;
          } 
        } 
      } 
      sort(paramArrayOfdouble, paramInt1, i3 - 1, paramBoolean);
      sort(paramArrayOfdouble, i4 + 1, paramInt2, false);
    } 
  }
}
