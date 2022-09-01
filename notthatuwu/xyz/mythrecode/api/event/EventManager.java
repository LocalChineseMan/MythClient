package notthatuwu.xyz.mythrecode.api.event;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EventManager {
  public static void register(Object o) {
    for (Method method : o.getClass().getDeclaredMethods()) {
      if (!isMethodBad(method))
        register(method, o); 
    } 
  }
  
  public static void register(Object o, Class<? extends Event> clazz) {
    for (Method method : o.getClass().getDeclaredMethods()) {
      if (!isMethodBad(method, clazz))
        register(method, o); 
    } 
  }
  
  private static void register(Method method, Object o) {
    Class<?> clazz = method.getParameterTypes()[0];
    final Data methodData = new Data(o, method, ((EventTarget)method.<EventTarget>getAnnotation(EventTarget.class)).value());
    if (!methodData.target.isAccessible())
      methodData.target.setAccessible(true); 
    if (REGISTRY_MAP.containsKey(clazz)) {
      if (!((ArrayHelper<Data>)REGISTRY_MAP.get(clazz)).contains(methodData)) {
        ((ArrayHelper<Data>)REGISTRY_MAP.get(clazz)).add(methodData);
        sortListValue((Class)clazz);
      } 
    } else {
      REGISTRY_MAP.put(clazz, new ArrayHelper<Data>() {
          
          });
    } 
  }
  
  public static void unregister(Object o) {
    for (ArrayHelper<Data> flexibalArray : REGISTRY_MAP.values()) {
      for (Data methodData : flexibalArray) {
        if (methodData.source.equals(o))
          flexibalArray.remove(methodData); 
      } 
    } 
    cleanMap(true);
  }
  
  public static void unregister(Object o, Class<? extends Event> clazz) {
    if (REGISTRY_MAP.containsKey(clazz)) {
      for (Data methodData : REGISTRY_MAP.get(clazz)) {
        if (methodData.source.equals(o))
          ((ArrayHelper<Data>)REGISTRY_MAP.get(clazz)).remove(methodData); 
      } 
      cleanMap(true);
    } 
  }
  
  public static void cleanMap(boolean b) {
    Iterator<Map.Entry<Class<? extends Event>, ArrayHelper<Data>>> iterator = REGISTRY_MAP.entrySet().iterator();
    while (iterator.hasNext()) {
      if (!b || ((ArrayHelper)((Map.Entry)iterator.next()).getValue()).isEmpty())
        iterator.remove(); 
    } 
  }
  
  public static void removeEnty(Class<? extends Event> clazz) {
    Iterator<Map.Entry<Class<? extends Event>, ArrayHelper<Data>>> iterator = REGISTRY_MAP.entrySet().iterator();
    while (iterator.hasNext()) {
      if (((Class)((Map.Entry)iterator.next()).getKey()).equals(clazz)) {
        iterator.remove();
        break;
      } 
    } 
  }
  
  private static void sortListValue(Class<? extends Event> clazz) {
    ArrayHelper<Data> flexibleArray = new ArrayHelper<>();
    for (byte b : Priority.VALUE_ARRAY) {
      for (Data methodData : REGISTRY_MAP.get(clazz)) {
        if (methodData.priority == b)
          flexibleArray.add(methodData); 
      } 
    } 
    REGISTRY_MAP.put(clazz, flexibleArray);
  }
  
  private static boolean isMethodBad(Method method) {
    return ((method.getParameterTypes()).length != 1 || !method.isAnnotationPresent((Class)EventTarget.class));
  }
  
  private static boolean isMethodBad(Method method, Class<? extends Event> clazz) {
    return (isMethodBad(method) || method.getParameterTypes()[0].equals(clazz));
  }
  
  public static ArrayHelper<Data> get(Class<? extends Event> clazz) {
    return REGISTRY_MAP.get(clazz);
  }
  
  public static void shutdown() {
    REGISTRY_MAP.clear();
  }
  
  private static final Map<Class<? extends Event>, ArrayHelper<Data>> REGISTRY_MAP = new HashMap<>();
}
