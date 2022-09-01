package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class TypeVariableSignature implements FieldTypeSignature {
  private final String identifier;
  
  private TypeVariableSignature(String paramString) {
    this.identifier = paramString;
  }
  
  public static TypeVariableSignature make(String paramString) {
    return new TypeVariableSignature(paramString);
  }
  
  public String getIdentifier() {
    return this.identifier;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) {
    paramTypeTreeVisitor.visitTypeVariableSignature(this);
  }
}
