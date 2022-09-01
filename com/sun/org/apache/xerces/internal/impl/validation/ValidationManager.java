package com.sun.org.apache.xerces.internal.impl.validation;

import java.util.Vector;

public class ValidationManager {
  protected final Vector fVSs = new Vector();
  
  protected boolean fGrammarFound = false;
  
  protected boolean fCachedDTD = false;
  
  public final void addValidationState(ValidationState vs) {
    this.fVSs.addElement(vs);
  }
  
  public final void setEntityState(EntityState state) {
    for (int i = this.fVSs.size() - 1; i >= 0; i--)
      ((ValidationState)this.fVSs.elementAt(i)).setEntityState(state); 
  }
  
  public final void setGrammarFound(boolean grammar) {
    this.fGrammarFound = grammar;
  }
  
  public final boolean isGrammarFound() {
    return this.fGrammarFound;
  }
  
  public final void setCachedDTD(boolean cachedDTD) {
    this.fCachedDTD = cachedDTD;
  }
  
  public final boolean isCachedDTD() {
    return this.fCachedDTD;
  }
  
  public final void reset() {
    this.fVSs.removeAllElements();
    this.fGrammarFound = false;
    this.fCachedDTD = false;
  }
}
