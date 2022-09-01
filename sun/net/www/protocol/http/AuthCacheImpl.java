package sun.net.www.protocol.http;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class AuthCacheImpl implements AuthCache {
  HashMap<String, LinkedList<AuthCacheValue>> hashtable = new HashMap<>();
  
  public void setMap(HashMap<String, LinkedList<AuthCacheValue>> paramHashMap) {
    this.hashtable = paramHashMap;
  }
  
  public synchronized void put(String paramString, AuthCacheValue paramAuthCacheValue) {
    LinkedList<AuthCacheValue> linkedList = this.hashtable.get(paramString);
    String str = paramAuthCacheValue.getPath();
    if (linkedList == null) {
      linkedList = new LinkedList();
      this.hashtable.put(paramString, linkedList);
    } 
    ListIterator<AuthCacheValue> listIterator = linkedList.listIterator();
    while (listIterator.hasNext()) {
      AuthenticationInfo authenticationInfo = (AuthenticationInfo)listIterator.next();
      if (authenticationInfo.path == null || authenticationInfo.path.startsWith(str))
        listIterator.remove(); 
    } 
    listIterator.add(paramAuthCacheValue);
  }
  
  public synchronized AuthCacheValue get(String paramString1, String paramString2) {
    Object object = null;
    LinkedList<AuthenticationInfo> linkedList = (LinkedList)this.hashtable.get(paramString1);
    if (linkedList == null || linkedList.size() == 0)
      return null; 
    if (paramString2 == null)
      return linkedList.get(0); 
    ListIterator<AuthenticationInfo> listIterator = linkedList.listIterator();
    while (listIterator.hasNext()) {
      AuthenticationInfo authenticationInfo = listIterator.next();
      if (paramString2.startsWith(authenticationInfo.path))
        return authenticationInfo; 
    } 
    return null;
  }
  
  public synchronized void remove(String paramString, AuthCacheValue paramAuthCacheValue) {
    LinkedList linkedList = this.hashtable.get(paramString);
    if (linkedList == null)
      return; 
    if (paramAuthCacheValue == null) {
      linkedList.clear();
      return;
    } 
    ListIterator<AuthenticationInfo> listIterator = linkedList.listIterator();
    while (listIterator.hasNext()) {
      AuthenticationInfo authenticationInfo = listIterator.next();
      if (paramAuthCacheValue.equals(authenticationInfo))
        listIterator.remove(); 
    } 
  }
}
