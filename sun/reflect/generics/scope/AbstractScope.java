package sun.reflect.generics.scope;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;

public abstract class AbstractScope<D extends GenericDeclaration> implements Scope {
  private final D recvr;
  
  private volatile Scope enclosingScope;
  
  protected AbstractScope(D paramD) {
    this.recvr = paramD;
  }
  
  protected D getRecvr() {
    return this.recvr;
  }
  
  protected abstract Scope computeEnclosingScope();
  
  protected Scope getEnclosingScope() {
    Scope scope = this.enclosingScope;
    if (scope == null) {
      scope = computeEnclosingScope();
      this.enclosingScope = scope;
    } 
    return scope;
  }
  
  public TypeVariable<?> lookup(String paramString) {
    TypeVariable[] arrayOfTypeVariable = (TypeVariable[])getRecvr().getTypeParameters();
    for (TypeVariable<?> typeVariable : arrayOfTypeVariable) {
      if (typeVariable.getName().equals(paramString))
        return typeVariable; 
    } 
    return getEnclosingScope().lookup(paramString);
  }
}
