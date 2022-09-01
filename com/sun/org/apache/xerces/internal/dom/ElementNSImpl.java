package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;

public class ElementNSImpl extends ElementImpl {
  static final long serialVersionUID = -9142310625494392642L;
  
  static final String xmlURI = "http://www.w3.org/XML/1998/namespace";
  
  protected String namespaceURI;
  
  protected String localName;
  
  transient XSTypeDefinition type;
  
  protected ElementNSImpl() {}
  
  protected ElementNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName) throws DOMException {
    super(ownerDocument, qualifiedName);
    setName(namespaceURI, qualifiedName);
  }
  
  private void setName(String namespaceURI, String qname) {
    this.namespaceURI = namespaceURI;
    if (namespaceURI != null)
      this.namespaceURI = (namespaceURI.length() == 0) ? null : namespaceURI; 
    if (qname == null) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
      throw new DOMException((short)14, msg);
    } 
    int colon1 = qname.indexOf(':');
    int colon2 = qname.lastIndexOf(':');
    this.ownerDocument.checkNamespaceWF(qname, colon1, colon2);
    if (colon1 < 0) {
      this.localName = qname;
      if (this.ownerDocument.errorChecking) {
        this.ownerDocument.checkQName(null, this.localName);
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
      if (this.ownerDocument.errorChecking) {
        if (namespaceURI == null || (prefix.equals("xml") && !namespaceURI.equals(NamespaceContext.XML_URI))) {
          String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
          throw new DOMException((short)14, msg);
        } 
        this.ownerDocument.checkQName(prefix, this.localName);
        this.ownerDocument.checkDOMNSErr(prefix, namespaceURI);
      } 
    } 
  }
  
  protected ElementNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName, String localName) throws DOMException {
    super(ownerDocument, qualifiedName);
    this.localName = localName;
    this.namespaceURI = namespaceURI;
  }
  
  protected ElementNSImpl(CoreDocumentImpl ownerDocument, String value) {
    super(ownerDocument, value);
  }
  
  void rename(String namespaceURI, String qualifiedName) {
    if (needsSyncData())
      synchronizeData(); 
    this.name = qualifiedName;
    setName(namespaceURI, qualifiedName);
    reconcileDefaultAttributes();
  }
  
  protected void setValues(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName, String localName) {
    this.firstChild = null;
    this.previousSibling = null;
    this.nextSibling = null;
    this.fNodeListCache = null;
    this.attributes = null;
    this.flags = 0;
    setOwnerDocument(ownerDocument);
    needsSyncData(true);
    this.name = qualifiedName;
    this.localName = localName;
    this.namespaceURI = namespaceURI;
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
    if (this.ownerDocument.errorChecking) {
      if (isReadOnly()) {
        String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, msg);
      } 
      if (prefix != null && prefix.length() != 0) {
        if (!CoreDocumentImpl.isXMLName(prefix, this.ownerDocument.isXML11Version())) {
          String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
          throw new DOMException((short)5, msg);
        } 
        if (this.namespaceURI == null || prefix.indexOf(':') >= 0) {
          String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
          throw new DOMException((short)14, msg);
        } 
        if (prefix.equals("xml") && 
          !this.namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
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
  
  public String getBaseURI() {
    if (needsSyncData())
      synchronizeData(); 
    if (this.attributes != null) {
      Attr attrNode = (Attr)this.attributes.getNamedItemNS("http://www.w3.org/XML/1998/namespace", "base");
      if (attrNode != null) {
        String uri = attrNode.getNodeValue();
        if (uri.length() != 0) {
          try {
            uri = (new URI(uri)).toString();
          } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException e) {
            NodeImpl parentOrOwner = (parentNode() != null) ? parentNode() : this.ownerNode;
            String parentBaseURI = (parentOrOwner != null) ? parentOrOwner.getBaseURI() : null;
            if (parentBaseURI != null) {
              try {
                uri = (new URI(new URI(parentBaseURI), uri)).toString();
              } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException ex) {
                return null;
              } 
              return uri;
            } 
            return null;
          } 
          return uri;
        } 
      } 
    } 
    String parentElementBaseURI = (parentNode() != null) ? parentNode().getBaseURI() : null;
    if (parentElementBaseURI != null)
      try {
        return (new URI(parentElementBaseURI)).toString();
      } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException e) {
        return null;
      }  
    String baseURI = (this.ownerNode != null) ? this.ownerNode.getBaseURI() : null;
    if (baseURI != null)
      try {
        return (new URI(baseURI)).toString();
      } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException e) {
        return null;
      }  
    return null;
  }
  
  public String getTypeName() {
    if (this.type != null) {
      if (this.type instanceof XSSimpleTypeDecl)
        return ((XSSimpleTypeDecl)this.type).getTypeName(); 
      if (this.type instanceof XSComplexTypeDecl)
        return ((XSComplexTypeDecl)this.type).getTypeName(); 
    } 
    return null;
  }
  
  public String getTypeNamespace() {
    if (this.type != null)
      return this.type.getNamespace(); 
    return null;
  }
  
  public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg, int derivationMethod) {
    if (needsSyncData())
      synchronizeData(); 
    if (this.type != null) {
      if (this.type instanceof XSSimpleTypeDecl)
        return ((XSSimpleTypeDecl)this.type).isDOMDerivedFrom(typeNamespaceArg, typeNameArg, derivationMethod); 
      if (this.type instanceof XSComplexTypeDecl)
        return ((XSComplexTypeDecl)this.type).isDOMDerivedFrom(typeNamespaceArg, typeNameArg, derivationMethod); 
    } 
    return false;
  }
  
  public void setType(XSTypeDefinition type) {
    this.type = type;
  }
}
