package sun.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class BootstrapConstructorAccessorImpl extends ConstructorAccessorImpl {
  private final Constructor<?> constructor;
  
  BootstrapConstructorAccessorImpl(Constructor<?> paramConstructor) {
    this.constructor = paramConstructor;
  }
  
  public Object newInstance(Object[] paramArrayOfObject) throws IllegalArgumentException, InvocationTargetException {
    try {
      return UnsafeFieldAccessorImpl.unsafe.allocateInstance(this.constructor.getDeclaringClass());
    } catch (InstantiationException instantiationException) {
      throw new InvocationTargetException(instantiationException);
    } 
  }
}
