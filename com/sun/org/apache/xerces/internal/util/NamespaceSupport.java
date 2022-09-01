package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

public class NamespaceSupport implements NamespaceContext {
  protected String[] fNamespace = new String[32];
  
  protected int fNamespaceSize;
  
  protected int[] fContext = new int[8];
  
  protected int fCurrentContext;
  
  protected String[] fPrefixes = new String[16];
  
  public NamespaceSupport() {}
  
  public NamespaceSupport(NamespaceContext context) {
    pushContext();
    Enumeration<String> prefixes = context.getAllPrefixes();
    while (prefixes.hasMoreElements()) {
      String prefix = prefixes.nextElement();
      String uri = context.getURI(prefix);
      declarePrefix(prefix, uri);
    } 
  }
  
  public void reset() {
    this.fNamespaceSize = 0;
    this.fCurrentContext = 0;
    this.fNamespace[this.fNamespaceSize++] = XMLSymbols.PREFIX_XML;
    this.fNamespace[this.fNamespaceSize++] = NamespaceContext.XML_URI;
    this.fNamespace[this.fNamespaceSize++] = XMLSymbols.PREFIX_XMLNS;
    this.fNamespace[this.fNamespaceSize++] = NamespaceContext.XMLNS_URI;
    this.fContext[this.fCurrentContext] = this.fNamespaceSize;
  }
  
  public void pushContext() {
    if (this.fCurrentContext + 1 == this.fContext.length) {
      int[] contextarray = new int[this.fContext.length * 2];
      System.arraycopy(this.fContext, 0, contextarray, 0, this.fContext.length);
      this.fContext = contextarray;
    } 
    this.fContext[++this.fCurrentContext] = this.fNamespaceSize;
  }
  
  public void popContext() {
    this.fNamespaceSize = this.fContext[this.fCurrentContext--];
  }
  
  public boolean declarePrefix(String prefix, String uri) {
    if (prefix == XMLSymbols.PREFIX_XML || prefix == XMLSymbols.PREFIX_XMLNS)
      return false; 
    for (int i = this.fNamespaceSize; i > this.fContext[this.fCurrentContext]; i -= 2) {
      if (this.fNamespace[i - 2] == prefix) {
        this.fNamespace[i - 1] = uri;
        return true;
      } 
    } 
    if (this.fNamespaceSize == this.fNamespace.length) {
      String[] namespacearray = new String[this.fNamespaceSize * 2];
      System.arraycopy(this.fNamespace, 0, namespacearray, 0, this.fNamespaceSize);
      this.fNamespace = namespacearray;
    } 
    this.fNamespace[this.fNamespaceSize++] = prefix;
    this.fNamespace[this.fNamespaceSize++] = uri;
    return true;
  }
  
  public String getURI(String prefix) {
    for (int i = this.fNamespaceSize; i > 0; i -= 2) {
      if (this.fNamespace[i - 2] == prefix)
        return this.fNamespace[i - 1]; 
    } 
    return null;
  }
  
  public String getPrefix(String uri) {
    for (int i = this.fNamespaceSize; i > 0; i -= 2) {
      if (this.fNamespace[i - 1] == uri && 
        getURI(this.fNamespace[i - 2]) == uri)
        return this.fNamespace[i - 2]; 
    } 
    return null;
  }
  
  public int getDeclaredPrefixCount() {
    return (this.fNamespaceSize - this.fContext[this.fCurrentContext]) / 2;
  }
  
  public String getDeclaredPrefixAt(int index) {
    return this.fNamespace[this.fContext[this.fCurrentContext] + index * 2];
  }
  
  public Iterator getPrefixes() {
    int count = 0;
    if (this.fPrefixes.length < this.fNamespace.length / 2) {
      String[] prefixes = new String[this.fNamespaceSize];
      this.fPrefixes = prefixes;
    } 
    String prefix = null;
    boolean unique = true;
    for (int i = 2; i < this.fNamespaceSize - 2; i += 2) {
      prefix = this.fNamespace[i + 2];
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
    return new IteratorPrefixes(this, this.fPrefixes, count);
  }
  
  public Enumeration getAllPrefixes() {
    int count = 0;
    if (this.fPrefixes.length < this.fNamespace.length / 2) {
      String[] prefixes = new String[this.fNamespaceSize];
      this.fPrefixes = prefixes;
    } 
    String prefix = null;
    boolean unique = true;
    for (int i = 2; i < this.fNamespaceSize - 2; i += 2) {
      prefix = this.fNamespace[i + 2];
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
    return new Prefixes(this, this.fPrefixes, count);
  }
  
  public Vector getPrefixes(String uri) {
    int count = 0;
    String prefix = null;
    boolean unique = true;
    Vector<String> prefixList = new Vector();
    for (int i = this.fNamespaceSize; i > 0; i -= 2) {
      if (this.fNamespace[i - 1] == uri && 
        !prefixList.contains(this.fNamespace[i - 2]))
        prefixList.add(this.fNamespace[i - 2]); 
    } 
    return prefixList;
  }
  
  public boolean containsPrefix(String prefix) {
    for (int i = this.fNamespaceSize; i > 0; i -= 2) {
      if (this.fNamespace[i - 2] == prefix)
        return true; 
    } 
    return false;
  }
  
  public boolean containsPrefixInCurrentContext(String prefix) {
    for (int i = this.fContext[this.fCurrentContext]; i < this.fNamespaceSize; i += 2) {
      if (this.fNamespace[i] == prefix)
        return true; 
    } 
    return false;
  }
  
  protected final class NamespaceSupport {}
  
  protected final class NamespaceSupport {}
}
