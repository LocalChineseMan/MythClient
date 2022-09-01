package sun.reflect.generics.repository;

import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.visitor.Reifier;

public class MethodRepository extends ConstructorRepository {
  private Type returnType;
  
  private MethodRepository(String paramString, GenericsFactory paramGenericsFactory) {
    super(paramString, paramGenericsFactory);
  }
  
  public static MethodRepository make(String paramString, GenericsFactory paramGenericsFactory) {
    return new MethodRepository(paramString, paramGenericsFactory);
  }
  
  public Type getReturnType() {
    if (this.returnType == null) {
      Reifier reifier = getReifier();
      getTree().getReturnType().accept(reifier);
      this.returnType = reifier.getResult();
    } 
    return this.returnType;
  }
}
