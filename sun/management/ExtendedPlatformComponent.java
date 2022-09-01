package sun.management;

import java.lang.management.PlatformManagedObject;
import java.util.Collections;
import java.util.List;
import jdk.internal.cmm.SystemResourcePressureImpl;
import jdk.management.cmm.SystemResourcePressureMXBean;

public final class ExtendedPlatformComponent {
  private static SystemResourcePressureMXBean cmmBeanImpl = null;
  
  private static synchronized SystemResourcePressureMXBean getCMMBean() {
    if (cmmBeanImpl == null)
      cmmBeanImpl = (SystemResourcePressureMXBean)new SystemResourcePressureImpl(); 
    return cmmBeanImpl;
  }
  
  public static List<? extends PlatformManagedObject> getMXBeans() {
    if (shouldRegisterCMMBean())
      return (List)Collections.singletonList(getCMMBean()); 
    return Collections.emptyList();
  }
  
  public static <T extends PlatformManagedObject> T getMXBean(Class<T> paramClass) {
    if (paramClass != null && "jdk.management.cmm.SystemResourcePressureMXBean"
      .equals(paramClass
        .getName())) {
      if (isUnlockCommercialFeaturesEnabled())
        return paramClass.cast(getCMMBean()); 
      throw new IllegalArgumentException("Cooperative Memory Management is a commercial feature which must be unlocked before being used.  To learn more about commercial features and how to unlock them visit http://www.oracle.com/technetwork/java/javaseproducts/");
    } 
    return null;
  }
  
  private static boolean shouldRegisterCMMBean() {
    if (!isUnlockCommercialFeaturesEnabled())
      return false; 
    boolean bool = false;
    Class<?> clazz = null;
    try {
      ClassLoader classLoader = ClassLoader.getSystemClassLoader();
      if (classLoader == null)
        return false; 
      classLoader = classLoader.getParent();
      clazz = Class.forName("com.oracle.exalogic.ExaManager", false, classLoader);
      Object object = clazz.getMethod("instance", new Class[0]).invoke(null, new Object[0]);
      if (object != null) {
        Object object1 = clazz.getMethod("isExalogicSystem", new Class[0]).invoke(object, new Object[0]);
        bool = ((Boolean)object1).booleanValue();
      } 
      return bool;
    } catch (ClassNotFoundException|NoSuchMethodException|IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException classNotFoundException) {
      return false;
    } 
  }
  
  private static boolean isUnlockCommercialFeaturesEnabled() {
    Flag flag = Flag.getFlag("UnlockCommercialFeatures");
    if (flag != null && "true".equals(flag.getVMOption().getValue()))
      return true; 
    return false;
  }
}
