package java.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

class InMemoryCookieStore implements CookieStore {
  private List<HttpCookie> cookieJar = null;
  
  private Map<String, List<HttpCookie>> domainIndex = null;
  
  private Map<URI, List<HttpCookie>> uriIndex = null;
  
  private ReentrantLock lock = null;
  
  public InMemoryCookieStore() {
    this.cookieJar = new ArrayList<>();
    this.domainIndex = new HashMap<>();
    this.uriIndex = new HashMap<>();
    this.lock = new ReentrantLock(false);
  }
  
  public void add(URI paramURI, HttpCookie paramHttpCookie) {
    if (paramHttpCookie == null)
      throw new NullPointerException("cookie is null"); 
    this.lock.lock();
    try {
      this.cookieJar.remove(paramHttpCookie);
      if (paramHttpCookie.getMaxAge() != 0L) {
        this.cookieJar.add(paramHttpCookie);
        if (paramHttpCookie.getDomain() != null)
          addIndex(this.domainIndex, paramHttpCookie.getDomain(), paramHttpCookie); 
        if (paramURI != null)
          addIndex(this.uriIndex, getEffectiveURI(paramURI), paramHttpCookie); 
      } 
    } finally {
      this.lock.unlock();
    } 
  }
  
  public List<HttpCookie> get(URI paramURI) {
    if (paramURI == null)
      throw new NullPointerException("uri is null"); 
    ArrayList<HttpCookie> arrayList = new ArrayList();
    boolean bool = "https".equalsIgnoreCase(paramURI.getScheme());
    this.lock.lock();
    try {
      getInternal1(arrayList, this.domainIndex, paramURI.getHost(), bool);
      getInternal2(arrayList, this.uriIndex, getEffectiveURI(paramURI), bool);
    } finally {
      this.lock.unlock();
    } 
    return arrayList;
  }
  
  public List<HttpCookie> getCookies() {
    List<HttpCookie> list;
    this.lock.lock();
    try {
      Iterator<HttpCookie> iterator = this.cookieJar.iterator();
      while (iterator.hasNext()) {
        if (((HttpCookie)iterator.next()).hasExpired())
          iterator.remove(); 
      } 
    } finally {
      list = Collections.unmodifiableList(this.cookieJar);
      this.lock.unlock();
    } 
    return list;
  }
  
  public List<URI> getURIs() {
    ArrayList<URI> arrayList = new ArrayList();
    this.lock.lock();
    try {
      Iterator<URI> iterator = this.uriIndex.keySet().iterator();
      while (iterator.hasNext()) {
        URI uRI = iterator.next();
        List list = this.uriIndex.get(uRI);
        if (list == null || list.size() == 0)
          iterator.remove(); 
      } 
    } finally {
      arrayList.addAll(this.uriIndex.keySet());
      this.lock.unlock();
    } 
    return arrayList;
  }
  
  public boolean remove(URI paramURI, HttpCookie paramHttpCookie) {
    if (paramHttpCookie == null)
      throw new NullPointerException("cookie is null"); 
    boolean bool = false;
    this.lock.lock();
    try {
      bool = this.cookieJar.remove(paramHttpCookie);
    } finally {
      this.lock.unlock();
    } 
    return bool;
  }
  
  public boolean removeAll() {
    this.lock.lock();
    try {
      if (this.cookieJar.isEmpty())
        return false; 
      this.cookieJar.clear();
      this.domainIndex.clear();
      this.uriIndex.clear();
    } finally {
      this.lock.unlock();
    } 
    return true;
  }
  
  private boolean netscapeDomainMatches(String paramString1, String paramString2) {
    if (paramString1 == null || paramString2 == null)
      return false; 
    boolean bool = ".local".equalsIgnoreCase(paramString1);
    int i = paramString1.indexOf('.');
    if (i == 0)
      i = paramString1.indexOf('.', 1); 
    if (!bool && (i == -1 || i == paramString1.length() - 1))
      return false; 
    int j = paramString2.indexOf('.');
    if (j == -1 && bool)
      return true; 
    int k = paramString1.length();
    int m = paramString2.length() - k;
    if (m == 0)
      return paramString2.equalsIgnoreCase(paramString1); 
    if (m > 0) {
      String str1 = paramString2.substring(0, m);
      String str2 = paramString2.substring(m);
      return str2.equalsIgnoreCase(paramString1);
    } 
    if (m == -1)
      return (paramString1.charAt(0) == '.' && paramString2
        .equalsIgnoreCase(paramString1.substring(1))); 
    return false;
  }
  
  private void getInternal1(List<HttpCookie> paramList, Map<String, List<HttpCookie>> paramMap, String paramString, boolean paramBoolean) {
    ArrayList<HttpCookie> arrayList = new ArrayList();
    for (Map.Entry<String, List<HttpCookie>> entry : paramMap.entrySet()) {
      String str = (String)entry.getKey();
      List list = (List)entry.getValue();
      for (HttpCookie httpCookie : list) {
        if ((httpCookie.getVersion() == 0 && netscapeDomainMatches(str, paramString)) || (httpCookie
          .getVersion() == 1 && HttpCookie.domainMatches(str, paramString))) {
          if (this.cookieJar.indexOf(httpCookie) != -1) {
            if (!httpCookie.hasExpired()) {
              if ((paramBoolean || !httpCookie.getSecure()) && 
                !paramList.contains(httpCookie))
                paramList.add(httpCookie); 
              continue;
            } 
            arrayList.add(httpCookie);
            continue;
          } 
          arrayList.add(httpCookie);
        } 
      } 
      for (HttpCookie httpCookie : arrayList) {
        list.remove(httpCookie);
        this.cookieJar.remove(httpCookie);
      } 
      arrayList.clear();
    } 
  }
  
  private <T> void getInternal2(List<HttpCookie> paramList, Map<T, List<HttpCookie>> paramMap, Comparable<T> paramComparable, boolean paramBoolean) {
    // Byte code:
    //   0: aload_2
    //   1: invokeinterface keySet : ()Ljava/util/Set;
    //   6: invokeinterface iterator : ()Ljava/util/Iterator;
    //   11: astore #5
    //   13: aload #5
    //   15: invokeinterface hasNext : ()Z
    //   20: ifeq -> 186
    //   23: aload #5
    //   25: invokeinterface next : ()Ljava/lang/Object;
    //   30: astore #6
    //   32: aload_3
    //   33: aload #6
    //   35: invokeinterface compareTo : (Ljava/lang/Object;)I
    //   40: ifne -> 183
    //   43: aload_2
    //   44: aload #6
    //   46: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
    //   51: checkcast java/util/List
    //   54: astore #7
    //   56: aload #7
    //   58: ifnull -> 183
    //   61: aload #7
    //   63: invokeinterface iterator : ()Ljava/util/Iterator;
    //   68: astore #8
    //   70: aload #8
    //   72: invokeinterface hasNext : ()Z
    //   77: ifeq -> 183
    //   80: aload #8
    //   82: invokeinterface next : ()Ljava/lang/Object;
    //   87: checkcast java/net/HttpCookie
    //   90: astore #9
    //   92: aload_0
    //   93: getfield cookieJar : Ljava/util/List;
    //   96: aload #9
    //   98: invokeinterface indexOf : (Ljava/lang/Object;)I
    //   103: iconst_m1
    //   104: if_icmpeq -> 173
    //   107: aload #9
    //   109: invokevirtual hasExpired : ()Z
    //   112: ifne -> 151
    //   115: iload #4
    //   117: ifne -> 128
    //   120: aload #9
    //   122: invokevirtual getSecure : ()Z
    //   125: ifne -> 180
    //   128: aload_1
    //   129: aload #9
    //   131: invokeinterface contains : (Ljava/lang/Object;)Z
    //   136: ifne -> 180
    //   139: aload_1
    //   140: aload #9
    //   142: invokeinterface add : (Ljava/lang/Object;)Z
    //   147: pop
    //   148: goto -> 180
    //   151: aload #8
    //   153: invokeinterface remove : ()V
    //   158: aload_0
    //   159: getfield cookieJar : Ljava/util/List;
    //   162: aload #9
    //   164: invokeinterface remove : (Ljava/lang/Object;)Z
    //   169: pop
    //   170: goto -> 180
    //   173: aload #8
    //   175: invokeinterface remove : ()V
    //   180: goto -> 70
    //   183: goto -> 13
    //   186: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #328	-> 0
    //   #329	-> 32
    //   #330	-> 43
    //   #332	-> 56
    //   #333	-> 61
    //   #334	-> 70
    //   #335	-> 80
    //   #336	-> 92
    //   #338	-> 107
    //   #340	-> 115
    //   #341	-> 131
    //   #342	-> 139
    //   #344	-> 151
    //   #345	-> 158
    //   #350	-> 173
    //   #352	-> 180
    //   #355	-> 183
    //   #356	-> 186
  }
  
  private <T> void addIndex(Map<T, List<HttpCookie>> paramMap, T paramT, HttpCookie paramHttpCookie) {
    if (paramT != null) {
      List<HttpCookie> list = paramMap.get(paramT);
      if (list != null) {
        list.remove(paramHttpCookie);
        list.add(paramHttpCookie);
      } else {
        list = new ArrayList<>();
        list.add(paramHttpCookie);
        paramMap.put(paramT, list);
      } 
    } 
  }
  
  private URI getEffectiveURI(URI paramURI) {
    URI uRI = null;
    try {
      uRI = new URI("http", paramURI.getHost(), null, null, null);
    } catch (URISyntaxException uRISyntaxException) {
      uRI = paramURI;
    } 
    return uRI;
  }
}
