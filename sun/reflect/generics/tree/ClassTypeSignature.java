package sun.reflect.generics.tree;

import java.util.List;
import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ClassTypeSignature implements FieldTypeSignature {
  private final List<SimpleClassTypeSignature> path;
  
  private ClassTypeSignature(List<SimpleClassTypeSignature> paramList) {
    this.path = paramList;
  }
  
  public static ClassTypeSignature make(List<SimpleClassTypeSignature> paramList) {
    return new ClassTypeSignature(paramList);
  }
  
  public List<SimpleClassTypeSignature> getPath() {
    return this.path;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) {
    paramTypeTreeVisitor.visitClassTypeSignature(this);
  }
}
