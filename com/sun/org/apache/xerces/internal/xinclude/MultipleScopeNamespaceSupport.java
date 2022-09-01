package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.util.Enumeration;

public class MultipleScopeNamespaceSupport extends NamespaceSupport {
  protected int[] fScope = new int[8];
  
  protected int fCurrentScope;
  
  public MultipleScopeNamespaceSupport() {
    this.fCurrentScope = 0;
    this.fScope[0] = 0;
  }
  
  public MultipleScopeNamespaceSupport(NamespaceContext context) {
    super(context);
    this.fCurrentScope = 0;
    this.fScope[0] = 0;
  }
  
  public Enumeration getAllPrefixes() {
    int count = 0;
    if (this.fPrefixes.length < this.fNamespace.length / 2) {
      String[] prefixes = new String[this.fNamespaceSize];
      this.fPrefixes = prefixes;
    } 
    String prefix = null;
    boolean unique = true;
    int i = this.fContext[this.fScope[this.fCurrentScope]];
    for (; i <= this.fNamespaceSize - 2; 
      i += 2) {
      prefix = this.fNamespace[i];
      for (int k = 0; k < count; k++) {
        if (this.fPrefixes[k] == prefix) {
          unique = false;
          break;
        } 
      } 
      if (unique)
        this.fPrefixes[count++] = prefix; 
      unique = true;
    } 
    return new NamespaceSupport.Prefixes(this, this.fPrefixes, count);
  }
  
  public int getScopeForContext(int context) {
    int scope = this.fCurrentScope;
    while (context < this.fScope[scope])
      scope--; 
    return scope;
  }
  
  public String getPrefix(String uri) {
    return getPrefix(uri, this.fNamespaceSize, this.fContext[this.fScope[this.fCurrentScope]]);
  }
  
  public String getURI(String prefix) {
    return getURI(prefix, this.fNamespaceSize, this.fContext[this.fScope[this.fCurrentScope]]);
  }
  
  public String getPrefix(String uri, int context) {
    return getPrefix(uri, this.fContext[context + 1], this.fContext[this.fScope[getScopeForContext(context)]]);
  }
  
  public String getURI(String prefix, int context) {
    return getURI(prefix, this.fContext[context + 1], this.fContext[this.fScope[getScopeForContext(context)]]);
  }
  
  public String getPrefix(String uri, int start, int end) {
    if (uri == NamespaceContext.XML_URI)
      return XMLSymbols.PREFIX_XML; 
    if (uri == NamespaceContext.XMLNS_URI)
      return XMLSymbols.PREFIX_XMLNS; 
    for (int i = start; i > end; i -= 2) {
      if (this.fNamespace[i - 1] == uri && 
        getURI(this.fNamespace[i - 2]) == uri)
        return this.fNamespace[i - 2]; 
    } 
    return null;
  }
  
  public String getURI(String prefix, int start, int end) {
    if (prefix == XMLSymbols.PREFIX_XML)
      return NamespaceContext.XML_URI; 
    if (prefix == XMLSymbols.PREFIX_XMLNS)
      return NamespaceContext.XMLNS_URI; 
    for (int i = start; i > end; i -= 2) {
      if (this.fNamespace[i - 2] == prefix)
        return this.fNamespace[i - 1]; 
    } 
    return null;
  }
  
  public void reset() {
    this.fCurrentContext = this.fScope[this.fCurrentScope];
    this.fNamespaceSize = this.fContext[this.fCurrentContext];
  }
  
  public void pushScope() {
    if (this.fCurrentScope + 1 == this.fScope.length) {
      int[] contextarray = new int[this.fScope.length * 2];
      System.arraycopy(this.fScope, 0, contextarray, 0, this.fScope.length);
      this.fScope = contextarray;
    } 
    pushContext();
    this.fScope[++this.fCurrentScope] = this.fCurrentContext;
  }
  
  public void popScope() {
    this.fCurrentContext = this.fScope[this.fCurrentScope--];
    popContext();
  }
}
