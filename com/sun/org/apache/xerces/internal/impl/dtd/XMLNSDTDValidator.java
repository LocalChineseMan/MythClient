package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XNIException;

public class XMLNSDTDValidator extends XMLDTDValidator {
  private QName fAttributeQName = new QName();
  
  protected final void startNamespaceScope(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
    this.fNamespaceContext.pushContext();
    if (element.prefix == XMLSymbols.PREFIX_XMLNS)
      this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { element.rawname }, (short)2); 
    int length = attributes.getLength();
    for (int i = 0; i < length; i++) {
      String localpart = attributes.getLocalName(i);
      String str1 = attributes.getPrefix(i);
      if (str1 == XMLSymbols.PREFIX_XMLNS || (str1 == XMLSymbols.EMPTY_STRING && localpart == XMLSymbols.PREFIX_XMLNS)) {
        String uri = this.fSymbolTable.addSymbol(attributes.getValue(i));
        if (str1 == XMLSymbols.PREFIX_XMLNS && localpart == XMLSymbols.PREFIX_XMLNS)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { attributes
                
                .getQName(i) }, (short)2); 
        if (uri == NamespaceContext.XMLNS_URI)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { attributes
                
                .getQName(i) }, (short)2); 
        if (localpart == XMLSymbols.PREFIX_XML) {
          if (uri != NamespaceContext.XML_URI)
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { attributes
                  
                  .getQName(i) }, (short)2); 
        } else if (uri == NamespaceContext.XML_URI) {
          this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { attributes
                
                .getQName(i) }, (short)2);
        } 
        str1 = (localpart != XMLSymbols.PREFIX_XMLNS) ? localpart : XMLSymbols.EMPTY_STRING;
        if (uri == XMLSymbols.EMPTY_STRING && localpart != XMLSymbols.PREFIX_XMLNS) {
          this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "EmptyPrefixedAttName", new Object[] { attributes
                
                .getQName(i) }, (short)2);
        } else {
          this.fNamespaceContext.declarePrefix(str1, (uri.length() != 0) ? uri : null);
        } 
      } 
    } 
    String prefix = (element.prefix != null) ? element.prefix : XMLSymbols.EMPTY_STRING;
    element.uri = this.fNamespaceContext.getURI(prefix);
    if (element.prefix == null && element.uri != null)
      element.prefix = XMLSymbols.EMPTY_STRING; 
    if (element.prefix != null && element.uri == null)
      this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { element.prefix, element.rawname }, (short)2); 
    for (int j = 0; j < length; j++) {
      attributes.getName(j, this.fAttributeQName);
      String aprefix = (this.fAttributeQName.prefix != null) ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
      String arawname = this.fAttributeQName.rawname;
      if (arawname == XMLSymbols.PREFIX_XMLNS) {
        this.fAttributeQName.uri = this.fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS);
        attributes.setName(j, this.fAttributeQName);
      } else if (aprefix != XMLSymbols.EMPTY_STRING) {
        this.fAttributeQName.uri = this.fNamespaceContext.getURI(aprefix);
        if (this.fAttributeQName.uri == null)
          this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { element.rawname, arawname, aprefix }, (short)2); 
        attributes.setName(j, this.fAttributeQName);
      } 
    } 
    int attrCount = attributes.getLength();
    for (int k = 0; k < attrCount - 1; k++) {
      String auri = attributes.getURI(k);
      if (auri != null && auri != NamespaceContext.XMLNS_URI) {
        String alocalpart = attributes.getLocalName(k);
        for (int m = k + 1; m < attrCount; m++) {
          String blocalpart = attributes.getLocalName(m);
          String buri = attributes.getURI(m);
          if (alocalpart == blocalpart && auri == buri)
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { element.rawname, alocalpart, auri }, (short)2); 
        } 
      } 
    } 
  }
  
  protected void endNamespaceScope(QName element, Augmentations augs, boolean isEmpty) throws XNIException {
    String eprefix = (element.prefix != null) ? element.prefix : XMLSymbols.EMPTY_STRING;
    element.uri = this.fNamespaceContext.getURI(eprefix);
    if (element.uri != null)
      element.prefix = eprefix; 
    if (this.fDocumentHandler != null && 
      !isEmpty)
      this.fDocumentHandler.endElement(element, augs); 
    this.fNamespaceContext.popContext();
  }
}
