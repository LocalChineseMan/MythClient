package com.sun.org.apache.xerces.internal.dom;

public final class DeferredAttrNSImpl extends AttrNSImpl implements DeferredNode {
  static final long serialVersionUID = 6074924934945957154L;
  
  protected transient int fNodeIndex;
  
  DeferredAttrNSImpl(DeferredDocumentImpl ownerDocument, int nodeIndex) {
    super(ownerDocument, (String)null);
    this.fNodeIndex = nodeIndex;
    needsSyncData(true);
    needsSyncChildren(true);
  }
  
  public int getNodeIndex() {
    return this.fNodeIndex;
  }
  
  protected void synchronizeData() {
    needsSyncData(false);
    DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)ownerDocument();
    this.name = ownerDocument.getNodeName(this.fNodeIndex);
    int index = this.name.indexOf(':');
    if (index < 0) {
      this.localName = this.name;
    } else {
      this.localName = this.name.substring(index + 1);
    } 
    int extra = ownerDocument.getNodeExtra(this.fNodeIndex);
    isSpecified(((extra & 0x20) != 0));
    isIdAttribute(((extra & 0x200) != 0));
    this.namespaceURI = ownerDocument.getNodeURI(this.fNodeIndex);
    int extraNode = ownerDocument.getLastChild(this.fNodeIndex);
    this.type = ownerDocument.getTypeInfo(extraNode);
  }
  
  protected void synchronizeChildren() {
    DeferredDocumentImpl ownerDocument = (DeferredDocumentImpl)ownerDocument();
    ownerDocument.synchronizeChildren(this, this.fNodeIndex);
  }
}
