package sun.security.jca;

import java.security.Provider;

public class Providers {
  private static final ThreadLocal<ProviderList> threadLists = new InheritableThreadLocal<>();
  
  private static volatile int threadListsUsed;
  
  private static volatile ProviderList providerList = ProviderList.EMPTY;
  
  private static final String BACKUP_PROVIDER_CLASSNAME = "sun.security.provider.VerificationProvider";
  
  private static final String[] jarVerificationProviders;
  
  static {
    providerList = ProviderList.fromSecurityProperties();
    jarVerificationProviders = new String[] { "sun.security.provider.Sun", "sun.security.rsa.SunRsaSign", "sun.security.ec.SunEC", "sun.security.provider.VerificationProvider" };
  }
  
  public static Provider getSunProvider() {
    try {
      Class<?> clazz = Class.forName(jarVerificationProviders[0]);
      return (Provider)clazz.newInstance();
    } catch (Exception exception) {
      try {
        Class<?> clazz = Class.forName("sun.security.provider.VerificationProvider");
        return (Provider)clazz.newInstance();
      } catch (Exception exception1) {
        throw new RuntimeException("Sun provider not found", exception);
      } 
    } 
  }
  
  public static Object startJarVerification() {
    ProviderList providerList1 = getProviderList();
    ProviderList providerList2 = providerList1.getJarList(jarVerificationProviders);
    return beginThreadProviderList(providerList2);
  }
  
  public static void stopJarVerification(Object paramObject) {
    endThreadProviderList((ProviderList)paramObject);
  }
  
  public static ProviderList getProviderList() {
    ProviderList providerList = getThreadProviderList();
    if (providerList == null)
      providerList = getSystemProviderList(); 
    return providerList;
  }
  
  public static void setProviderList(ProviderList paramProviderList) {
    if (getThreadProviderList() == null) {
      setSystemProviderList(paramProviderList);
    } else {
      changeThreadProviderList(paramProviderList);
    } 
  }
  
  public static ProviderList getFullProviderList() {
    synchronized (Providers.class) {
      ProviderList providerList = getThreadProviderList();
      if (providerList != null) {
        ProviderList providerList3 = providerList.removeInvalid();
        if (providerList3 != providerList) {
          changeThreadProviderList(providerList3);
          providerList = providerList3;
        } 
        return providerList;
      } 
    } 
    ProviderList providerList1 = getSystemProviderList();
    ProviderList providerList2 = providerList1.removeInvalid();
    if (providerList2 != providerList1) {
      setSystemProviderList(providerList2);
      providerList1 = providerList2;
    } 
    return providerList1;
  }
  
  private static ProviderList getSystemProviderList() {
    return providerList;
  }
  
  private static void setSystemProviderList(ProviderList paramProviderList) {
    providerList = paramProviderList;
  }
  
  public static ProviderList getThreadProviderList() {
    if (threadListsUsed == 0)
      return null; 
    return threadLists.get();
  }
  
  private static void changeThreadProviderList(ProviderList paramProviderList) {
    threadLists.set(paramProviderList);
  }
  
  public static synchronized ProviderList beginThreadProviderList(ProviderList paramProviderList) {
    if (ProviderList.debug != null)
      ProviderList.debug.println("ThreadLocal providers: " + paramProviderList); 
    ProviderList providerList = threadLists.get();
    threadListsUsed++;
    threadLists.set(paramProviderList);
    return providerList;
  }
  
  public static synchronized void endThreadProviderList(ProviderList paramProviderList) {
    if (paramProviderList == null) {
      if (ProviderList.debug != null)
        ProviderList.debug.println("Disabling ThreadLocal providers"); 
      threadLists.remove();
    } else {
      if (ProviderList.debug != null)
        ProviderList.debug
          .println("Restoring previous ThreadLocal providers: " + paramProviderList); 
      threadLists.set(paramProviderList);
    } 
    threadListsUsed--;
  }
}
