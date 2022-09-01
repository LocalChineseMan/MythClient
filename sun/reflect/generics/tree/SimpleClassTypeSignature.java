package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class SimpleClassTypeSignature implements FieldTypeSignature {
  private final boolean dollar;
  
  private final String name;
  
  private final TypeArgument[] typeArgs;
  
  private SimpleClassTypeSignature(String paramString, boolean paramBoolean, TypeArgument[] paramArrayOfTypeArgument) {
    this.name = paramString;
    this.dollar = paramBoolean;
    this.typeArgs = paramArrayOfTypeArgument;
  }
  
  public static SimpleClassTypeSignature make(String paramString, boolean paramBoolean, TypeArgument[] paramArrayOfTypeArgument) {
    return new SimpleClassTypeSignature(paramString, paramBoolean, paramArrayOfTypeArgument);
  }
  
  public boolean getDollar() {
    return this.dollar;
  }
  
  public String getName() {
    return this.name;
  }
  
  public TypeArgument[] getTypeArguments() {
    return this.typeArgs;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) {
    paramTypeTreeVisitor.visitSimpleClassTypeSignature(this);
  }
}
