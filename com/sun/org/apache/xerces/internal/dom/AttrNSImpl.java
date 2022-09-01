package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import org.w3c.dom.DOMException;

public class AttrNSImpl extends AttrImpl {
  static final long serialVersionUID = -781906615369795414L;
  
  static final String xmlnsURI = "http://www.w3.org/2000/xmlns/";
  
  static final String xmlURI = "http://www.w3.org/XML/1998/namespace";
  
  protected String namespaceURI;
  
  protected String localName;
  
  public AttrNSImpl() {}
  
  protected AttrNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName) {
    super(ownerDocument, qualifiedName);
    setName(namespaceURI, qualifiedName);
  }
  
  private void setName(String namespaceURI, String qname) {
    CoreDocumentImpl ownerDocument = ownerDocument();
    this.namespaceURI = namespaceURI;
    if (namespaceURI != null)
      this.namespaceURI = (namespaceURI.length() == 0) ? null : namespaceURI; 
    int colon1 = qname.indexOf(':');
    int colon2 = qname.lastIndexOf(':');
    ownerDocument.checkNamespaceWF(qname, colon1, colon2);
    if (colon1 < 0) {
      this.localName = qname;
      if (ownerDocument.errorChecking) {
        ownerDocument.checkQName(null, this.localName);
        if ((qname.equals("xmlns") && (namespaceURI == null || 
          !namespaceURI.equals(NamespaceContext.XMLNS_URI))) || (namespaceURI != null && namespaceURI
          .equals(NamespaceContext.XMLNS_URI) && 
          !qname.equals("xmlns"))) {
          String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
          throw new DOMException((short)14, msg);
        } 
      } 
    } else {
      String prefix = qname.substring(0, colon1);
      this.localName = qname.substring(colon2 + 1);
      ownerDocument.checkQName(prefix, this.localName);
      ownerDocument.checkDOMNSErr(prefix, namespaceURI);
    } 
  }
  
  public AttrNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName, String localName) {
    super(ownerDocument, qualifiedName);
    this.localName = localName;
    this.namespaceURI = namespaceURI;
  }
  
  protected AttrNSImpl(CoreDocumentImpl ownerDocument, String value) {
    super(ownerDocument, value);
  }
  
  void rename(String namespaceURI, String qualifiedName) {
    if (needsSyncData())
      synchronizeData(); 
    this.name = qualifiedName;
    setName(namespaceURI, qualifiedName);
  }
  
  public void setValues(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName, String localName) {
    this.textNode = null;
    this.flags = 0;
    isSpecified(true);
    hasStringValue(true);
    setOwnerDocument(ownerDocument);
    this.localName = localName;
    this.namespaceURI = namespaceURI;
    this.name = qualifiedName;
    this.value = null;
  }
  
  public String getNamespaceURI() {
    if (needsSyncData())
      synchronizeData(); 
    return this.namespaceURI;
  }
  
  public String getPrefix() {
    if (needsSyncData())
      synchronizeData(); 
    int index = this.name.indexOf(':');
    return (index < 0) ? null : this.name.substring(0, index);
  }
  
  public void setPrefix(String prefix) throws DOMException {
    if (needsSyncData())
      synchronizeData(); 
    if ((ownerDocument()).errorChecking) {
      if (isReadOnly()) {
        String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, msg);
      } 
      if (prefix != null && prefix.length() != 0) {
        if (!CoreDocumentImpl.isXMLName(prefix, ownerDocument().isXML11Version())) {
          String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
          throw new DOMException((short)5, msg);
        } 
        if (this.namespaceURI == null || prefix.indexOf(':') >= 0) {
          String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
          throw new DOMException((short)14, msg);
        } 
        if (prefix.equals("xmlns")) {
          if (!this.namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
            throw new DOMException((short)14, msg);
          } 
        } else if (prefix.equals("xml")) {
          if (!this.namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
            throw new DOMException((short)14, msg);
          } 
        } else if (this.name.equals("xmlns")) {
          String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
          throw new DOMException((short)14, msg);
        } 
      } 
    } 
    if (prefix != null && prefix.length() != 0) {
      this.name = prefix + ":" + this.localName;
    } else {
      this.name = this.localName;
    } 
  }
  
  public String getLocalName() {
    if (needsSyncData())
      synchronizeData(); 
    return this.localName;
  }
  
  public String getTypeName() {
    if (this.type != null) {
      if (this.type instanceof XSSimpleTypeDecl)
        return ((XSSimpleTypeDecl)this.type).getName(); 
      return (String)this.type;
    } 
    return null;
  }
  
  public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg, int derivationMethod) {
    if (this.type != null && 
      this.type instanceof XSSimpleTypeDecl)
      return ((XSSimpleTypeDecl)this.type).isDOMDerivedFrom(typeNamespaceArg, typeNameArg, derivationMethod); 
    return false;
  }
  
  public String getTypeNamespace() {
    if (this.type != null) {
      if (this.type instanceof XSSimpleTypeDecl)
        return ((XSSimpleTypeDecl)this.type).getNamespace(); 
      return "http://www.w3.org/TR/REC-xml";
    } 
    return null;
  }
}
