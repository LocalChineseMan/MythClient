package com.sun.jna;

import java.lang.reflect.Method;

abstract class VarArgsChecker {
  private VarArgsChecker() {}
  
  private static final class VarArgsChecker {}
  
  private static final class RealVarArgsChecker extends VarArgsChecker {
    private RealVarArgsChecker() {}
    
    boolean isVarArgs(Method m) {
      return m.isVarArgs();
    }
    
    int fixedArgs(Method m) {
      return m.isVarArgs() ? ((m.getParameterTypes()).length - 1) : 0;
    }
  }
  
  static VarArgsChecker create() {
    try {
      Method isVarArgsMethod = Method.class.getMethod("isVarArgs", new Class[0]);
      if (isVarArgsMethod != null)
        return new RealVarArgsChecker(); 
      return (VarArgsChecker)new NoVarArgsChecker(null);
    } catch (NoSuchMethodException e) {
      return (VarArgsChecker)new NoVarArgsChecker(null);
    } catch (SecurityException e) {
      return (VarArgsChecker)new NoVarArgsChecker(null);
    } 
  }
  
  abstract boolean isVarArgs(Method paramMethod);
  
  abstract int fixedArgs(Method paramMethod);
}
