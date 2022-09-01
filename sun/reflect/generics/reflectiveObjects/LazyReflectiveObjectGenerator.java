package sun.reflect.generics.reflectiveObjects;

import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.visitor.Reifier;

public abstract class LazyReflectiveObjectGenerator {
  private final GenericsFactory factory;
  
  protected LazyReflectiveObjectGenerator(GenericsFactory paramGenericsFactory) {
    this.factory = paramGenericsFactory;
  }
  
  private GenericsFactory getFactory() {
    return this.factory;
  }
  
  protected Reifier getReifier() {
    return Reifier.make(getFactory());
  }
}
