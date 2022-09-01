package sun.reflect.generics.scope;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ClassScope extends AbstractScope<Class<?>> implements Scope {
  private ClassScope(Class<?> paramClass) {
    super(paramClass);
  }
  
  protected Scope computeEnclosingScope() {
    Class<?> clazz1 = getRecvr();
    Method method = clazz1.getEnclosingMethod();
    if (method != null)
      return MethodScope.make(method); 
    Constructor<?> constructor = clazz1.getEnclosingConstructor();
    if (constructor != null)
      return ConstructorScope.make(constructor); 
    Class<?> clazz2 = clazz1.getEnclosingClass();
    if (clazz2 != null)
      return make(clazz2); 
    return DummyScope.make();
  }
  
  public static ClassScope make(Class<?> paramClass) {
    return new ClassScope(paramClass);
  }
}
