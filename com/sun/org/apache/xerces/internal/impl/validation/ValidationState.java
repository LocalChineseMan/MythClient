package com.sun.org.apache.xerces.internal.impl.validation;

import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.util.ArrayList;
import java.util.Locale;

public class ValidationState implements ValidationContext {
  private boolean fExtraChecking = true;
  
  private boolean fFacetChecking = true;
  
  private boolean fNormalize = true;
  
  private boolean fNamespaces = true;
  
  private EntityState fEntityState = null;
  
  private NamespaceContext fNamespaceContext = null;
  
  private SymbolTable fSymbolTable = null;
  
  private Locale fLocale = null;
  
  private ArrayList<String> fIdList;
  
  private ArrayList<String> fIdRefList;
  
  public void setExtraChecking(boolean newValue) {
    this.fExtraChecking = newValue;
  }
  
  public void setFacetChecking(boolean newValue) {
    this.fFacetChecking = newValue;
  }
  
  public void setNormalizationRequired(boolean newValue) {
    this.fNormalize = newValue;
  }
  
  public void setUsingNamespaces(boolean newValue) {
    this.fNamespaces = newValue;
  }
  
  public void setEntityState(EntityState state) {
    this.fEntityState = state;
  }
  
  public void setNamespaceSupport(NamespaceContext namespace) {
    this.fNamespaceContext = namespace;
  }
  
  public void setSymbolTable(SymbolTable sTable) {
    this.fSymbolTable = sTable;
  }
  
  public String checkIDRefID() {
    if (this.fIdList == null && 
      this.fIdRefList != null)
      return this.fIdRefList.get(0); 
    if (this.fIdRefList != null)
      for (int i = 0; i < this.fIdRefList.size(); i++) {
        String key = this.fIdRefList.get(i);
        if (!this.fIdList.contains(key))
          return key; 
      }  
    return null;
  }
  
  public void reset() {
    this.fExtraChecking = true;
    this.fFacetChecking = true;
    this.fNamespaces = true;
    this.fIdList = null;
    this.fIdRefList = null;
    this.fEntityState = null;
    this.fNamespaceContext = null;
    this.fSymbolTable = null;
  }
  
  public void resetIDTables() {
    this.fIdList = null;
    this.fIdRefList = null;
  }
  
  public boolean needExtraChecking() {
    return this.fExtraChecking;
  }
  
  public boolean needFacetChecking() {
    return this.fFacetChecking;
  }
  
  public boolean needToNormalize() {
    return this.fNormalize;
  }
  
  public boolean useNamespaces() {
    return this.fNamespaces;
  }
  
  public boolean isEntityDeclared(String name) {
    if (this.fEntityState != null)
      return this.fEntityState.isEntityDeclared(getSymbol(name)); 
    return false;
  }
  
  public boolean isEntityUnparsed(String name) {
    if (this.fEntityState != null)
      return this.fEntityState.isEntityUnparsed(getSymbol(name)); 
    return false;
  }
  
  public boolean isIdDeclared(String name) {
    if (this.fIdList == null)
      return false; 
    return this.fIdList.contains(name);
  }
  
  public void addId(String name) {
    if (this.fIdList == null)
      this.fIdList = new ArrayList<>(); 
    this.fIdList.add(name);
  }
  
  public void addIdRef(String name) {
    if (this.fIdRefList == null)
      this.fIdRefList = new ArrayList<>(); 
    this.fIdRefList.add(name);
  }
  
  public String getSymbol(String symbol) {
    if (this.fSymbolTable != null)
      return this.fSymbolTable.addSymbol(symbol); 
    return symbol.intern();
  }
  
  public String getURI(String prefix) {
    if (this.fNamespaceContext != null)
      return this.fNamespaceContext.getURI(prefix); 
    return null;
  }
  
  public void setLocale(Locale locale) {
    this.fLocale = locale;
  }
  
  public Locale getLocale() {
    return this.fLocale;
  }
}
