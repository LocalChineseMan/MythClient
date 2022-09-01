package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class FormalTypeParameter implements TypeTree {
  private final String name;
  
  private final FieldTypeSignature[] bounds;
  
  private FormalTypeParameter(String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature) {
    this.name = paramString;
    this.bounds = paramArrayOfFieldTypeSignature;
  }
  
  public static FormalTypeParameter make(String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature) {
    return new FormalTypeParameter(paramString, paramArrayOfFieldTypeSignature);
  }
  
  public FieldTypeSignature[] getBounds() {
    return this.bounds;
  }
  
  public String getName() {
    return this.name;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) {
    paramTypeTreeVisitor.visitFormalTypeParameter(this);
  }
}
