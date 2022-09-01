package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.Visitor;

public class MethodTypeSignature implements Signature {
  private final FormalTypeParameter[] formalTypeParams;
  
  private final TypeSignature[] parameterTypes;
  
  private final ReturnType returnType;
  
  private final FieldTypeSignature[] exceptionTypes;
  
  private MethodTypeSignature(FormalTypeParameter[] paramArrayOfFormalTypeParameter, TypeSignature[] paramArrayOfTypeSignature, ReturnType paramReturnType, FieldTypeSignature[] paramArrayOfFieldTypeSignature) {
    this.formalTypeParams = paramArrayOfFormalTypeParameter;
    this.parameterTypes = paramArrayOfTypeSignature;
    this.returnType = paramReturnType;
    this.exceptionTypes = paramArrayOfFieldTypeSignature;
  }
  
  public static MethodTypeSignature make(FormalTypeParameter[] paramArrayOfFormalTypeParameter, TypeSignature[] paramArrayOfTypeSignature, ReturnType paramReturnType, FieldTypeSignature[] paramArrayOfFieldTypeSignature) {
    return new MethodTypeSignature(paramArrayOfFormalTypeParameter, paramArrayOfTypeSignature, paramReturnType, paramArrayOfFieldTypeSignature);
  }
  
  public FormalTypeParameter[] getFormalTypeParameters() {
    return this.formalTypeParams;
  }
  
  public TypeSignature[] getParameterTypes() {
    return this.parameterTypes;
  }
  
  public ReturnType getReturnType() {
    return this.returnType;
  }
  
  public FieldTypeSignature[] getExceptionTypes() {
    return this.exceptionTypes;
  }
  
  public void accept(Visitor<?> paramVisitor) {
    paramVisitor.visitMethodTypeSignature(this);
  }
}
