package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class Wildcard implements TypeArgument {
  private FieldTypeSignature[] upperBounds;
  
  private FieldTypeSignature[] lowerBounds;
  
  private Wildcard(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2) {
    this.upperBounds = paramArrayOfFieldTypeSignature1;
    this.lowerBounds = paramArrayOfFieldTypeSignature2;
  }
  
  private static final FieldTypeSignature[] emptyBounds = new FieldTypeSignature[0];
  
  public static Wildcard make(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2) {
    return new Wildcard(paramArrayOfFieldTypeSignature1, paramArrayOfFieldTypeSignature2);
  }
  
  public FieldTypeSignature[] getUpperBounds() {
    return this.upperBounds;
  }
  
  public FieldTypeSignature[] getLowerBounds() {
    if (this.lowerBounds.length == 1 && this.lowerBounds[0] == 
      BottomSignature.make())
      return emptyBounds; 
    return this.lowerBounds;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) {
    paramTypeTreeVisitor.visitWildcard(this);
  }
}
