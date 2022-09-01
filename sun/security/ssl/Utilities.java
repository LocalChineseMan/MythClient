package sun.security.ssl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import sun.net.util.IPAddressUtil;

final class Utilities {
  static List<SNIServerName> addToSNIServerNameList(List<SNIServerName> paramList, String paramString) {
    SNIHostName sNIHostName = rawToSNIHostName(paramString);
    if (sNIHostName == null)
      return paramList; 
    int i = paramList.size();
    ArrayList<SNIServerName> arrayList = (i != 0) ? new ArrayList<>(paramList) : new ArrayList(1);
    boolean bool = false;
    for (byte b = 0; b < i; b++) {
      SNIServerName sNIServerName = arrayList.get(b);
      if (sNIServerName.getType() == 0) {
        arrayList.set(b, sNIHostName);
        if (Debug.isOn("ssl"))
          System.out.println(Thread.currentThread().getName() + ", the previous server name in SNI (" + sNIServerName + ") was replaced with (" + sNIHostName + ")"); 
        bool = true;
        break;
      } 
    } 
    if (!bool)
      arrayList.add(sNIHostName); 
    return Collections.unmodifiableList(arrayList);
  }
  
  private static SNIHostName rawToSNIHostName(String paramString) {
    SNIHostName sNIHostName = null;
    if (paramString != null && paramString.indexOf('.') > 0 && 
      !paramString.endsWith(".") && 
      !IPAddressUtil.isIPv4LiteralAddress(paramString) && 
      !IPAddressUtil.isIPv6LiteralAddress(paramString))
      try {
        sNIHostName = new SNIHostName(paramString);
      } catch (IllegalArgumentException illegalArgumentException) {
        if (Debug.isOn("ssl"))
          System.out.println(Thread.currentThread().getName() + ", \"" + paramString + "\" " + "is not a legal HostName for  server name indication"); 
      }  
    return sNIHostName;
  }
}
