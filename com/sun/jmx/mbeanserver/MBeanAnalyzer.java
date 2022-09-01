package com.sun.jmx.mbeanserver;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.NotCompliantMBeanException;

class MBeanAnalyzer<M> {
  void visit(MBeanVisitor<M> paramMBeanVisitor) {
    for (Map.Entry<String, AttrMethods<M>> entry : this.attrMap.entrySet()) {
      String str = (String)entry.getKey();
      AttrMethods attrMethods = (AttrMethods)entry.getValue();
      paramMBeanVisitor.visitAttribute(str, attrMethods.getter, attrMethods.setter);
    } 
    for (Map.Entry<String, List<M>> entry : this.opMap.entrySet()) {
      for (M m : entry.getValue())
        paramMBeanVisitor.visitOperation((String)entry.getKey(), m); 
    } 
  }
  
  private Map<String, List<M>> opMap = Util.newInsertionOrderMap();
  
  private Map<String, AttrMethods<M>> attrMap = Util.newInsertionOrderMap();
  
  static interface MBeanVisitor<M> {
    void visitAttribute(String param1String, M param1M1, M param1M2);
    
    void visitOperation(String param1String, M param1M);
  }
  
  private static class AttrMethods<M> {
    M getter;
    
    M setter;
    
    private AttrMethods() {}
  }
  
  static <M> MBeanAnalyzer<M> analyzer(Class<?> paramClass, MBeanIntrospector<M> paramMBeanIntrospector) throws NotCompliantMBeanException {
    return new MBeanAnalyzer(paramClass, paramMBeanIntrospector);
  }
  
  private MBeanAnalyzer(Class<?> paramClass, MBeanIntrospector<M> paramMBeanIntrospector) throws NotCompliantMBeanException {
    if (!paramClass.isInterface())
      throw new NotCompliantMBeanException("Not an interface: " + paramClass
          .getName()); 
    if (!Modifier.isPublic(paramClass.getModifiers()) && !Introspector.ALLOW_NONPUBLIC_MBEAN)
      throw new NotCompliantMBeanException("Interface is not public: " + paramClass
          .getName()); 
    try {
      initMaps(paramClass, paramMBeanIntrospector);
    } catch (Exception exception) {
      throw Introspector.throwException(paramClass, exception);
    } 
  }
  
  private void initMaps(Class<?> paramClass, MBeanIntrospector<M> paramMBeanIntrospector) throws Exception {
    List<Method> list1 = paramMBeanIntrospector.getMethods(paramClass);
    List<Method> list2 = eliminateCovariantMethods(list1);
    for (Method method : list2) {
      String str1 = method.getName();
      int i = (method.getParameterTypes()).length;
      M m = paramMBeanIntrospector.mFrom(method);
      String str2 = "";
      if (str1.startsWith("get")) {
        str2 = str1.substring(3);
      } else if (str1.startsWith("is") && method
        .getReturnType() == boolean.class) {
        str2 = str1.substring(2);
      } 
      if (str2.length() != 0 && i == 0 && method
        .getReturnType() != void.class) {
        AttrMethods<M> attrMethods = this.attrMap.get(str2);
        if (attrMethods == null) {
          attrMethods = new AttrMethods();
        } else if (attrMethods.getter != null) {
          String str = "Attribute " + str2 + " has more than one getter";
          throw new NotCompliantMBeanException(str);
        } 
        attrMethods.getter = m;
        this.attrMap.put(str2, attrMethods);
        continue;
      } 
      if (str1.startsWith("set") && str1.length() > 3 && i == 1 && method
        
        .getReturnType() == void.class) {
        str2 = str1.substring(3);
        AttrMethods<M> attrMethods = this.attrMap.get(str2);
        if (attrMethods == null) {
          attrMethods = new AttrMethods();
        } else if (attrMethods.setter != null) {
          String str = "Attribute " + str2 + " has more than one setter";
          throw new NotCompliantMBeanException(str);
        } 
        attrMethods.setter = m;
        this.attrMap.put(str2, attrMethods);
        continue;
      } 
      List<?> list = this.opMap.get(str1);
      if (list == null)
        list = Util.newList(); 
      list.add(m);
      this.opMap.put(str1, list);
    } 
    for (Map.Entry<String, AttrMethods<M>> entry : this.attrMap.entrySet()) {
      AttrMethods attrMethods = (AttrMethods)entry.getValue();
      if (!paramMBeanIntrospector.consistent(attrMethods.getter, attrMethods.setter)) {
        String str = "Getter and setter for " + (String)entry.getKey() + " have inconsistent types";
        throw new NotCompliantMBeanException(str);
      } 
    } 
  }
  
  private static class MethodOrder implements Comparator<Method> {
    public int compare(Method param1Method1, Method param1Method2) {
      int i = param1Method1.getName().compareTo(param1Method2.getName());
      if (i != 0)
        return i; 
      Class[] arrayOfClass1 = param1Method1.getParameterTypes();
      Class[] arrayOfClass2 = param1Method2.getParameterTypes();
      if (arrayOfClass1.length != arrayOfClass2.length)
        return arrayOfClass1.length - arrayOfClass2.length; 
      if (!Arrays.equals((Object[])arrayOfClass1, (Object[])arrayOfClass2))
        return Arrays.toString((Object[])arrayOfClass1).compareTo(Arrays.toString((Object[])arrayOfClass2)); 
      Class<?> clazz1 = param1Method1.getReturnType();
      Class<?> clazz2 = param1Method2.getReturnType();
      if (clazz1 == clazz2)
        return 0; 
      if (clazz1.isAssignableFrom(clazz2))
        return -1; 
      return 1;
    }
    
    public static final MethodOrder instance = new MethodOrder();
  }
  
  static List<Method> eliminateCovariantMethods(List<Method> paramList) {
    int i = paramList.size();
    Method[] arrayOfMethod = paramList.<Method>toArray(new Method[i]);
    Arrays.sort(arrayOfMethod, MethodOrder.instance);
    Set<?> set = Util.newSet();
    for (byte b = 1; b < i; b++) {
      Method method1 = arrayOfMethod[b - 1];
      Method method2 = arrayOfMethod[b];
      if (method1.getName().equals(method2.getName()))
        if (Arrays.equals((Object[])method1.getParameterTypes(), (Object[])method2
            .getParameterTypes()))
          if (!set.add(method1))
            throw new RuntimeException("Internal error: duplicate Method");   
    } 
    List<Method> list = Util.newList(paramList);
    list.removeAll(set);
    return list;
  }
}
