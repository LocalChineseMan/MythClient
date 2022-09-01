package sun.reflect.generics.scope;

import java.lang.reflect.Method;

public class MethodScope extends AbstractScope<Method> {
  private MethodScope(Method paramMethod) {
    super(paramMethod);
  }
  
  private Class<?> getEnclosingClass() {
    return getRecvr().getDeclaringClass();
  }
  
  protected Scope computeEnclosingScope() {
    return ClassScope.make(getEnclosingClass());
  }
  
  public static MethodScope make(Method paramMethod) {
    return new MethodScope(paramMethod);
  }
}
