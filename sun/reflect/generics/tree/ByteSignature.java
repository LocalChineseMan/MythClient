package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ByteSignature implements BaseType {
  private static final ByteSignature singleton = new ByteSignature();
  
  public static ByteSignature make() {
    return singleton;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) {
    paramTypeTreeVisitor.visitByteSignature(this);
  }
}
