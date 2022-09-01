package java.util;

import java.lang.reflect.Array;

class TimSort<T> {
  private static final int MIN_MERGE = 32;
  
  private final T[] a;
  
  private final Comparator<? super T> c;
  
  private static final int MIN_GALLOP = 7;
  
  private int minGallop = 7;
  
  private static final int INITIAL_TMP_STORAGE_LENGTH = 256;
  
  private T[] tmp;
  
  private int tmpBase;
  
  private int tmpLen;
  
  private int stackSize = 0;
  
  private final int[] runBase;
  
  private final int[] runLen;
  
  private TimSort(T[] paramArrayOfT1, Comparator<? super T> paramComparator, T[] paramArrayOfT2, int paramInt1, int paramInt2) {
    this.a = paramArrayOfT1;
    this.c = paramComparator;
    int i = paramArrayOfT1.length;
    byte b1 = (i < 512) ? (i >>> 1) : 256;
    if (paramArrayOfT2 == null || paramInt2 < b1 || paramInt1 + b1 > paramArrayOfT2.length) {
      Object[] arrayOfObject = (Object[])Array.newInstance(paramArrayOfT1.getClass().getComponentType(), b1);
      this.tmp = (T[])arrayOfObject;
      this.tmpBase = 0;
      this.tmpLen = b1;
    } else {
      this.tmp = paramArrayOfT2;
      this.tmpBase = paramInt1;
      this.tmpLen = paramInt2;
    } 
    byte b2 = (i < 120) ? 5 : ((i < 1542) ? 10 : ((i < 119151) ? 24 : 40));
    this.runBase = new int[b2];
    this.runLen = new int[b2];
  }
  
  static <T> void sort(T[] paramArrayOfT1, int paramInt1, int paramInt2, Comparator<? super T> paramComparator, T[] paramArrayOfT2, int paramInt3, int paramInt4) {
    assert paramComparator != null && paramArrayOfT1 != null && paramInt1 >= 0 && paramInt1 <= paramInt2 && paramInt2 <= paramArrayOfT1.length;
    int i = paramInt2 - paramInt1;
    if (i < 2)
      return; 
    if (i < 32) {
      int k = countRunAndMakeAscending(paramArrayOfT1, paramInt1, paramInt2, paramComparator);
      binarySort(paramArrayOfT1, paramInt1, paramInt2, paramInt1 + k, paramComparator);
      return;
    } 
    TimSort<T> timSort = new TimSort<>(paramArrayOfT1, paramComparator, paramArrayOfT2, paramInt3, paramInt4);
    int j = minRunLength(i);
    do {
      int k = countRunAndMakeAscending(paramArrayOfT1, paramInt1, paramInt2, paramComparator);
      if (k < j) {
        int m = (i <= j) ? i : j;
        binarySort(paramArrayOfT1, paramInt1, paramInt1 + m, paramInt1 + k, paramComparator);
        k = m;
      } 
      timSort.pushRun(paramInt1, k);
      timSort.mergeCollapse();
      paramInt1 += k;
      i -= k;
    } while (i != 0);
    assert paramInt1 == paramInt2;
    timSort.mergeForceCollapse();
    assert timSort.stackSize == 1;
  }
  
  private static <T> void binarySort(T[] paramArrayOfT, int paramInt1, int paramInt2, int paramInt3, Comparator<? super T> paramComparator) {
    assert paramInt1 <= paramInt3 && paramInt3 <= paramInt2;
    if (paramInt3 == paramInt1)
      paramInt3++; 
    for (; paramInt3 < paramInt2; paramInt3++) {
      T t = paramArrayOfT[paramInt3];
      int i = paramInt1;
      int j = paramInt3;
      assert i <= j;
      while (i < j) {
        int m = i + j >>> 1;
        if (paramComparator.compare(t, paramArrayOfT[m]) < 0) {
          j = m;
          continue;
        } 
        i = m + 1;
      } 
      assert i == j;
      int k = paramInt3 - i;
      switch (k) {
        case 2:
          paramArrayOfT[i + 2] = paramArrayOfT[i + 1];
        case 1:
          paramArrayOfT[i + 1] = paramArrayOfT[i];
          break;
        default:
          System.arraycopy(paramArrayOfT, i, paramArrayOfT, i + 1, k);
          break;
      } 
      paramArrayOfT[i] = t;
    } 
  }
  
  private static <T> int countRunAndMakeAscending(T[] paramArrayOfT, int paramInt1, int paramInt2, Comparator<? super T> paramComparator) {
    assert paramInt1 < paramInt2;
    int i = paramInt1 + 1;
    if (i == paramInt2)
      return 1; 
    if (paramComparator.compare(paramArrayOfT[i++], paramArrayOfT[paramInt1]) < 0) {
      while (i < paramInt2 && paramComparator.compare(paramArrayOfT[i], paramArrayOfT[i - 1]) < 0)
        i++; 
      reverseRange((Object[])paramArrayOfT, paramInt1, i);
    } else {
      while (i < paramInt2 && paramComparator.compare(paramArrayOfT[i], paramArrayOfT[i - 1]) >= 0)
        i++; 
    } 
    return i - paramInt1;
  }
  
  private static void reverseRange(Object[] paramArrayOfObject, int paramInt1, int paramInt2) {
    paramInt2--;
    while (paramInt1 < paramInt2) {
      Object object = paramArrayOfObject[paramInt1];
      paramArrayOfObject[paramInt1++] = paramArrayOfObject[paramInt2];
      paramArrayOfObject[paramInt2--] = object;
    } 
  }
  
  private static int minRunLength(int paramInt) {
    assert paramInt >= 0;
    int i = 0;
    while (paramInt >= 32) {
      i |= paramInt & 0x1;
      paramInt >>= 1;
    } 
    return paramInt + i;
  }
  
  private void pushRun(int paramInt1, int paramInt2) {
    this.runBase[this.stackSize] = paramInt1;
    this.runLen[this.stackSize] = paramInt2;
    this.stackSize++;
  }
  
  private void mergeCollapse() {
    while (this.stackSize > 1) {
      int i = this.stackSize - 2;
      if (i > 0 && this.runLen[i - 1] <= this.runLen[i] + this.runLen[i + 1]) {
        if (this.runLen[i - 1] < this.runLen[i + 1])
          i--; 
        mergeAt(i);
        continue;
      } 
      if (this.runLen[i] <= this.runLen[i + 1])
        mergeAt(i); 
    } 
  }
  
  private void mergeForceCollapse() {
    while (this.stackSize > 1) {
      int i = this.stackSize - 2;
      if (i > 0 && this.runLen[i - 1] < this.runLen[i + 1])
        i--; 
      mergeAt(i);
    } 
  }
  
  private void mergeAt(int paramInt) {
    assert this.stackSize >= 2;
    assert paramInt >= 0;
    assert paramInt == this.stackSize - 2 || paramInt == this.stackSize - 3;
    int i = this.runBase[paramInt];
    int j = this.runLen[paramInt];
    int k = this.runBase[paramInt + 1];
    int m = this.runLen[paramInt + 1];
    assert j > 0 && m > 0;
    assert i + j == k;
    this.runLen[paramInt] = j + m;
    if (paramInt == this.stackSize - 3) {
      this.runBase[paramInt + 1] = this.runBase[paramInt + 2];
      this.runLen[paramInt + 1] = this.runLen[paramInt + 2];
    } 
    this.stackSize--;
    int n = gallopRight(this.a[k], this.a, i, j, 0, this.c);
    assert n >= 0;
    i += n;
    j -= n;
    if (j == 0)
      return; 
    m = gallopLeft(this.a[i + j - 1], this.a, k, m, m - 1, this.c);
    assert m >= 0;
    if (m == 0)
      return; 
    if (j <= m) {
      mergeLo(i, j, k, m);
    } else {
      mergeHi(i, j, k, m);
    } 
  }
  
  private static <T> int gallopLeft(T paramT, T[] paramArrayOfT, int paramInt1, int paramInt2, int paramInt3, Comparator<? super T> paramComparator) {
    assert paramInt2 > 0 && paramInt3 >= 0 && paramInt3 < paramInt2;
    int i = 0;
    int j = 1;
    if (paramComparator.compare(paramT, paramArrayOfT[paramInt1 + paramInt3]) > 0) {
      int k = paramInt2 - paramInt3;
      while (j < k && paramComparator.compare(paramT, paramArrayOfT[paramInt1 + paramInt3 + j]) > 0) {
        i = j;
        j = (j << 1) + 1;
        if (j <= 0)
          j = k; 
      } 
      if (j > k)
        j = k; 
      i += paramInt3;
      j += paramInt3;
    } else {
      int k = paramInt3 + 1;
      while (j < k && paramComparator.compare(paramT, paramArrayOfT[paramInt1 + paramInt3 - j]) <= 0) {
        i = j;
        j = (j << 1) + 1;
        if (j <= 0)
          j = k; 
      } 
      if (j > k)
        j = k; 
      int m = i;
      i = paramInt3 - j;
      j = paramInt3 - m;
    } 
    assert -1 <= i && i < j && j <= paramInt2;
    i++;
    while (i < j) {
      int k = i + (j - i >>> 1);
      if (paramComparator.compare(paramT, paramArrayOfT[paramInt1 + k]) > 0) {
        i = k + 1;
        continue;
      } 
      j = k;
    } 
    assert i == j;
    return j;
  }
  
  private static <T> int gallopRight(T paramT, T[] paramArrayOfT, int paramInt1, int paramInt2, int paramInt3, Comparator<? super T> paramComparator) {
    assert paramInt2 > 0 && paramInt3 >= 0 && paramInt3 < paramInt2;
    int i = 1;
    int j = 0;
    if (paramComparator.compare(paramT, paramArrayOfT[paramInt1 + paramInt3]) < 0) {
      int k = paramInt3 + 1;
      while (i < k && paramComparator.compare(paramT, paramArrayOfT[paramInt1 + paramInt3 - i]) < 0) {
        j = i;
        i = (i << 1) + 1;
        if (i <= 0)
          i = k; 
      } 
      if (i > k)
        i = k; 
      int m = j;
      j = paramInt3 - i;
      i = paramInt3 - m;
    } else {
      int k = paramInt2 - paramInt3;
      while (i < k && paramComparator.compare(paramT, paramArrayOfT[paramInt1 + paramInt3 + i]) >= 0) {
        j = i;
        i = (i << 1) + 1;
        if (i <= 0)
          i = k; 
      } 
      if (i > k)
        i = k; 
      j += paramInt3;
      i += paramInt3;
    } 
    assert -1 <= j && j < i && i <= paramInt2;
    j++;
    while (j < i) {
      int k = j + (i - j >>> 1);
      if (paramComparator.compare(paramT, paramArrayOfT[paramInt1 + k]) < 0) {
        i = k;
        continue;
      } 
      j = k + 1;
    } 
    assert j == i;
    return i;
  }
  
  private void mergeLo(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    assert paramInt2 > 0 && paramInt4 > 0 && paramInt1 + paramInt2 == paramInt3;
    T[] arrayOfT1 = this.a;
    T[] arrayOfT2 = ensureCapacity(paramInt2);
    int i = this.tmpBase;
    int j = paramInt3;
    int k = paramInt1;
    System.arraycopy(arrayOfT1, paramInt1, arrayOfT2, i, paramInt2);
    arrayOfT1[k++] = arrayOfT1[j++];
    if (--paramInt4 == 0) {
      System.arraycopy(arrayOfT2, i, arrayOfT1, k, paramInt2);
      return;
    } 
    if (paramInt2 == 1) {
      System.arraycopy(arrayOfT1, j, arrayOfT1, k, paramInt4);
      arrayOfT1[k + paramInt4] = arrayOfT2[i];
      return;
    } 
    Comparator<? super T> comparator = this.c;
    int m = this.minGallop;
    while (true) {
      int n = 0;
      int i1 = 0;
      while (true) {
        assert paramInt2 > 1 && paramInt4 > 0;
        if (comparator.compare(arrayOfT1[j], arrayOfT2[i]) < 0) {
          arrayOfT1[k++] = arrayOfT1[j++];
          i1++;
          n = 0;
          if (--paramInt4 == 0)
            break; 
        } else {
          arrayOfT1[k++] = arrayOfT2[i++];
          n++;
          i1 = 0;
          if (--paramInt2 == 1)
            break; 
        } 
        if ((n | i1) >= m) {
          label79: while (true) {
            assert paramInt2 > 1 && paramInt4 > 0;
            n = gallopRight(arrayOfT1[j], arrayOfT2, i, paramInt2, 0, comparator);
            if (n != 0) {
              System.arraycopy(arrayOfT2, i, arrayOfT1, k, n);
              k += n;
              i += n;
              paramInt2 -= n;
              if (paramInt2 <= 1)
                break; 
            } 
            arrayOfT1[k++] = arrayOfT1[j++];
            if (--paramInt4 == 0)
              break; 
            i1 = gallopLeft(arrayOfT2[i], arrayOfT1, j, paramInt4, 0, comparator);
            if (i1 != 0) {
              System.arraycopy(arrayOfT1, j, arrayOfT1, k, i1);
              k += i1;
              j += i1;
              paramInt4 -= i1;
              if (paramInt4 == 0)
                break; 
            } 
            arrayOfT1[k++] = arrayOfT2[i++];
            if (--paramInt2 == 1)
              break; 
            m--;
            if ((((n >= 7) ? 1 : 0) | ((i1 >= 7) ? 1 : 0)) == 0) {
              if (m < 0) {
                m = 0;
                m += 2;
                continue;
              } 
              break label79;
            } 
          } 
          break;
        } 
      } 
      break;
    } 
    this.minGallop = (m < 1) ? 1 : m;
    if (paramInt2 == 1) {
      assert paramInt4 > 0;
      System.arraycopy(arrayOfT1, j, arrayOfT1, k, paramInt4);
      arrayOfT1[k + paramInt4] = arrayOfT2[i];
    } else {
      if (paramInt2 == 0)
        throw new IllegalArgumentException("Comparison method violates its general contract!"); 
      assert paramInt4 == 0;
      assert paramInt2 > 1;
      System.arraycopy(arrayOfT2, i, arrayOfT1, k, paramInt2);
    } 
  }
  
  private void mergeHi(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    assert paramInt2 > 0 && paramInt4 > 0 && paramInt1 + paramInt2 == paramInt3;
    T[] arrayOfT1 = this.a;
    T[] arrayOfT2 = ensureCapacity(paramInt4);
    int i = this.tmpBase;
    System.arraycopy(arrayOfT1, paramInt3, arrayOfT2, i, paramInt4);
    int j = paramInt1 + paramInt2 - 1;
    int k = i + paramInt4 - 1;
    int m = paramInt3 + paramInt4 - 1;
    arrayOfT1[m--] = arrayOfT1[j--];
    if (--paramInt2 == 0) {
      System.arraycopy(arrayOfT2, i, arrayOfT1, m - paramInt4 - 1, paramInt4);
      return;
    } 
    if (paramInt4 == 1) {
      m -= paramInt2;
      j -= paramInt2;
      System.arraycopy(arrayOfT1, j + 1, arrayOfT1, m + 1, paramInt2);
      arrayOfT1[m] = arrayOfT2[k];
      return;
    } 
    Comparator<? super T> comparator = this.c;
    int n = this.minGallop;
    while (true) {
      int i1 = 0;
      int i2 = 0;
      while (true) {
        assert paramInt2 > 0 && paramInt4 > 1;
        if (comparator.compare(arrayOfT2[k], arrayOfT1[j]) < 0) {
          arrayOfT1[m--] = arrayOfT1[j--];
          i1++;
          i2 = 0;
          if (--paramInt2 == 0)
            break; 
        } else {
          arrayOfT1[m--] = arrayOfT2[k--];
          i2++;
          i1 = 0;
          if (--paramInt4 == 1)
            break; 
        } 
        if ((i1 | i2) >= n) {
          label79: while (true) {
            assert paramInt2 > 0 && paramInt4 > 1;
            i1 = paramInt2 - gallopRight(arrayOfT2[k], arrayOfT1, paramInt1, paramInt2, paramInt2 - 1, comparator);
            if (i1 != 0) {
              m -= i1;
              j -= i1;
              paramInt2 -= i1;
              System.arraycopy(arrayOfT1, j + 1, arrayOfT1, m + 1, i1);
              if (paramInt2 == 0)
                break; 
            } 
            arrayOfT1[m--] = arrayOfT2[k--];
            if (--paramInt4 == 1)
              break; 
            i2 = paramInt4 - gallopLeft(arrayOfT1[j], arrayOfT2, i, paramInt4, paramInt4 - 1, comparator);
            if (i2 != 0) {
              m -= i2;
              k -= i2;
              paramInt4 -= i2;
              System.arraycopy(arrayOfT2, k + 1, arrayOfT1, m + 1, i2);
              if (paramInt4 <= 1)
                break; 
            } 
            arrayOfT1[m--] = arrayOfT1[j--];
            if (--paramInt2 == 0)
              break; 
            n--;
            if ((((i1 >= 7) ? 1 : 0) | ((i2 >= 7) ? 1 : 0)) == 0) {
              if (n < 0) {
                n = 0;
                n += 2;
                continue;
              } 
              break label79;
            } 
          } 
          break;
        } 
      } 
      break;
    } 
    this.minGallop = (n < 1) ? 1 : n;
    if (paramInt4 == 1) {
      assert paramInt2 > 0;
      m -= paramInt2;
      j -= paramInt2;
      System.arraycopy(arrayOfT1, j + 1, arrayOfT1, m + 1, paramInt2);
      arrayOfT1[m] = arrayOfT2[k];
    } else {
      if (paramInt4 == 0)
        throw new IllegalArgumentException("Comparison method violates its general contract!"); 
      assert paramInt2 == 0;
      assert paramInt4 > 0;
      System.arraycopy(arrayOfT2, i, arrayOfT1, m - paramInt4 - 1, paramInt4);
    } 
  }
  
  private T[] ensureCapacity(int paramInt) {
    if (this.tmpLen < paramInt) {
      int i = paramInt;
      i |= i >> 1;
      i |= i >> 2;
      i |= i >> 4;
      i |= i >> 8;
      i |= i >> 16;
      i++;
      if (i < 0) {
        i = paramInt;
      } else {
        i = Math.min(i, this.a.length >>> 1);
      } 
      Object[] arrayOfObject = (Object[])Array.newInstance(this.a.getClass().getComponentType(), i);
      this.tmp = (T[])arrayOfObject;
      this.tmpLen = i;
      this.tmpBase = 0;
    } 
    return this.tmp;
  }
}
