package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;

public class AttrImpl extends NodeImpl implements Attr, TypeInfo {
  static final long serialVersionUID = 7277707688218972102L;
  
  static final String DTD_URI = "http://www.w3.org/TR/REC-xml";
  
  protected Object value = null;
  
  protected String name;
  
  transient Object type;
  
  protected TextImpl textNode = null;
  
  protected AttrImpl(CoreDocumentImpl ownerDocument, String name) {
    super(ownerDocument);
    this.name = name;
    isSpecified(true);
    hasStringValue(true);
  }
  
  protected AttrImpl() {}
  
  void rename(String name) {
    if (needsSyncData())
      synchronizeData(); 
    this.name = name;
  }
  
  protected void makeChildNode() {
    if (hasStringValue()) {
      if (this.value != null) {
        TextImpl text = (TextImpl)ownerDocument().createTextNode((String)this.value);
        this.value = text;
        text.isFirstChild(true);
        text.previousSibling = text;
        text.ownerNode = this;
        text.isOwned(true);
      } 
      hasStringValue(false);
    } 
  }
  
  void setOwnerDocument(CoreDocumentImpl doc) {
    if (needsSyncChildren())
      synchronizeChildren(); 
    super.setOwnerDocument(doc);
    if (!hasStringValue()) {
      ChildNode child = (ChildNode)this.value;
      for (; child != null; child = child.nextSibling)
        child.setOwnerDocument(doc); 
    } 
  }
  
  public void setIdAttribute(boolean id) {
    if (needsSyncData())
      synchronizeData(); 
    isIdAttribute(id);
  }
  
  public boolean isId() {
    return isIdAttribute();
  }
  
  public Node cloneNode(boolean deep) {
    if (needsSyncChildren())
      synchronizeChildren(); 
    AttrImpl clone = (AttrImpl)super.cloneNode(deep);
    if (!clone.hasStringValue()) {
      clone.value = null;
      for (Node child = (Node)this.value; child != null; 
        child = child.getNextSibling())
        clone.appendChild(child.cloneNode(true)); 
    } 
    clone.isSpecified(true);
    return clone;
  }
  
  public short getNodeType() {
    return 2;
  }
  
  public String getNodeName() {
    if (needsSyncData())
      synchronizeData(); 
    return this.name;
  }
  
  public void setNodeValue(String value) throws DOMException {
    setValue(value);
  }
  
  public String getTypeName() {
    return (String)this.type;
  }
  
  public String getTypeNamespace() {
    if (this.type != null)
      return "http://www.w3.org/TR/REC-xml"; 
    return null;
  }
  
  public TypeInfo getSchemaTypeInfo() {
    return this;
  }
  
  public String getNodeValue() {
    return getValue();
  }
  
  public String getName() {
    if (needsSyncData())
      synchronizeData(); 
    return this.name;
  }
  
  public void setValue(String newvalue) {
    CoreDocumentImpl ownerDocument = ownerDocument();
    if (ownerDocument.errorChecking && isReadOnly()) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
      throw new DOMException((short)7, msg);
    } 
    Element ownerElement = getOwnerElement();
    String oldvalue = "";
    if (needsSyncData())
      synchronizeData(); 
    if (needsSyncChildren())
      synchronizeChildren(); 
    if (this.value != null) {
      if (ownerDocument.getMutationEvents()) {
        if (hasStringValue()) {
          oldvalue = (String)this.value;
          if (this.textNode == null) {
            this
              .textNode = (TextImpl)ownerDocument.createTextNode((String)this.value);
          } else {
            this.textNode.data = (String)this.value;
          } 
          this.value = this.textNode;
          this.textNode.isFirstChild(true);
          this.textNode.previousSibling = this.textNode;
          this.textNode.ownerNode = this;
          this.textNode.isOwned(true);
          hasStringValue(false);
          internalRemoveChild(this.textNode, true);
        } else {
          oldvalue = getValue();
          while (this.value != null)
            internalRemoveChild((Node)this.value, true); 
        } 
      } else {
        if (hasStringValue()) {
          oldvalue = (String)this.value;
        } else {
          oldvalue = getValue();
          ChildNode firstChild = (ChildNode)this.value;
          firstChild.previousSibling = null;
          firstChild.isFirstChild(false);
          firstChild.ownerNode = ownerDocument;
        } 
        this.value = null;
        needsSyncChildren(false);
      } 
      if (isIdAttribute() && ownerElement != null)
        ownerDocument.removeIdentifier(oldvalue); 
    } 
    isSpecified(true);
    if (ownerDocument.getMutationEvents()) {
      internalInsertBefore(ownerDocument.createTextNode(newvalue), (Node)null, true);
      hasStringValue(false);
      ownerDocument.modifiedAttrValue(this, oldvalue);
    } else {
      this.value = newvalue;
      hasStringValue(true);
      changed();
    } 
    if (isIdAttribute() && ownerElement != null)
      ownerDocument.putIdentifier(newvalue, ownerElement); 
  }
  
  public String getValue() {
    if (needsSyncData())
      synchronizeData(); 
    if (needsSyncChildren())
      synchronizeChildren(); 
    if (this.value == null)
      return ""; 
    if (hasStringValue())
      return (String)this.value; 
    ChildNode firstChild = (ChildNode)this.value;
    String data = null;
    if (firstChild.getNodeType() == 5) {
      data = ((EntityReferenceImpl)firstChild).getEntityRefValue();
    } else {
      data = firstChild.getNodeValue();
    } 
    ChildNode node = firstChild.nextSibling;
    if (node == null || data == null)
      return (data == null) ? "" : data; 
    StringBuffer value = new StringBuffer(data);
    while (node != null) {
      if (node.getNodeType() == 5) {
        data = ((EntityReferenceImpl)node).getEntityRefValue();
        if (data == null)
          return ""; 
        value.append(data);
      } else {
        value.append(node.getNodeValue());
      } 
      node = node.nextSibling;
    } 
    return value.toString();
  }
  
  public boolean getSpecified() {
    if (needsSyncData())
      synchronizeData(); 
    return isSpecified();
  }
  
  public Element getElement() {
    return isOwned() ? (Element)this.ownerNode : null;
  }
  
  public Element getOwnerElement() {
    return isOwned() ? (Element)this.ownerNode : null;
  }
  
  public void normalize() {
    if (isNormalized() || hasStringValue())
      return; 
    ChildNode firstChild = (ChildNode)this.value;
    for (Node kid = firstChild; kid != null; kid = next) {
      Node next = kid.getNextSibling();
      if (kid.getNodeType() == 3)
        if (next != null && next.getNodeType() == 3) {
          ((Text)kid).appendData(next.getNodeValue());
          removeChild(next);
          next = kid;
        } else if (kid.getNodeValue() == null || kid.getNodeValue().length() == 0) {
          removeChild(kid);
        }  
    } 
    isNormalized(true);
  }
  
  public void setSpecified(boolean arg) {
    if (needsSyncData())
      synchronizeData(); 
    isSpecified(arg);
  }
  
  public void setType(Object type) {
    this.type = type;
  }
  
  public String toString() {
    return getName() + "=" + "\"" + getValue() + "\"";
  }
  
  public boolean hasChildNodes() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return (this.value != null);
  }
  
  public NodeList getChildNodes() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return this;
  }
  
  public Node getFirstChild() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    makeChildNode();
    return (Node)this.value;
  }
  
  public Node getLastChild() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return lastChild();
  }
  
  final ChildNode lastChild() {
    makeChildNode();
    return (this.value != null) ? ((ChildNode)this.value).previousSibling : null;
  }
  
  final void lastChild(ChildNode node) {
    if (this.value != null)
      ((ChildNode)this.value).previousSibling = node; 
  }
  
  public Node insertBefore(Node newChild, Node refChild) throws DOMException {
    return internalInsertBefore(newChild, refChild, false);
  }
  
  Node internalInsertBefore(Node newChild, Node refChild, boolean replace) throws DOMException {
    CoreDocumentImpl ownerDocument = ownerDocument();
    boolean errorChecking = ownerDocument.errorChecking;
    if (newChild.getNodeType() == 11) {
      if (errorChecking) {
        Node kid = newChild.getFirstChild();
        for (; kid != null; kid = kid.getNextSibling()) {
          if (!ownerDocument.isKidOK(this, kid)) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
            throw new DOMException((short)3, msg);
          } 
        } 
      } 
      while (newChild.hasChildNodes())
        insertBefore(newChild.getFirstChild(), refChild); 
      return newChild;
    } 
    if (newChild == refChild) {
      refChild = refChild.getNextSibling();
      removeChild(newChild);
      insertBefore(newChild, refChild);
      return newChild;
    } 
    if (needsSyncChildren())
      synchronizeChildren(); 
    if (errorChecking) {
      if (isReadOnly()) {
        String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, msg);
      } 
      if (newChild.getOwnerDocument() != ownerDocument) {
        String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
        throw new DOMException((short)4, msg);
      } 
      if (!ownerDocument.isKidOK(this, newChild)) {
        String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
        throw new DOMException((short)3, msg);
      } 
      if (refChild != null && refChild.getParentNode() != this) {
        String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
        throw new DOMException((short)8, msg);
      } 
      boolean treeSafe = true;
      for (NodeImpl a = this; treeSafe && a != null; a = a.parentNode())
        treeSafe = (newChild != a); 
      if (!treeSafe) {
        String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
        throw new DOMException((short)3, msg);
      } 
    } 
    makeChildNode();
    ownerDocument.insertingNode(this, replace);
    ChildNode newInternal = (ChildNode)newChild;
    Node oldparent = newInternal.parentNode();
    if (oldparent != null)
      oldparent.removeChild(newInternal); 
    ChildNode refInternal = (ChildNode)refChild;
    newInternal.ownerNode = this;
    newInternal.isOwned(true);
    ChildNode firstChild = (ChildNode)this.value;
    if (firstChild == null) {
      this.value = newInternal;
      newInternal.isFirstChild(true);
      newInternal.previousSibling = newInternal;
    } else if (refInternal == null) {
      ChildNode lastChild = firstChild.previousSibling;
      lastChild.nextSibling = newInternal;
      newInternal.previousSibling = lastChild;
      firstChild.previousSibling = newInternal;
    } else if (refChild == firstChild) {
      firstChild.isFirstChild(false);
      newInternal.nextSibling = firstChild;
      newInternal.previousSibling = firstChild.previousSibling;
      firstChild.previousSibling = newInternal;
      this.value = newInternal;
      newInternal.isFirstChild(true);
    } else {
      ChildNode prev = refInternal.previousSibling;
      newInternal.nextSibling = refInternal;
      prev.nextSibling = newInternal;
      refInternal.previousSibling = newInternal;
      newInternal.previousSibling = prev;
    } 
    changed();
    ownerDocument.insertedNode(this, newInternal, replace);
    checkNormalizationAfterInsert(newInternal);
    return newChild;
  }
  
  public Node removeChild(Node oldChild) throws DOMException {
    if (hasStringValue()) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
      throw new DOMException((short)8, msg);
    } 
    return internalRemoveChild(oldChild, false);
  }
  
  Node internalRemoveChild(Node oldChild, boolean replace) throws DOMException {
    CoreDocumentImpl ownerDocument = ownerDocument();
    if (ownerDocument.errorChecking) {
      if (isReadOnly()) {
        String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, msg);
      } 
      if (oldChild != null && oldChild.getParentNode() != this) {
        String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
        throw new DOMException((short)8, msg);
      } 
    } 
    ChildNode oldInternal = (ChildNode)oldChild;
    ownerDocument.removingNode(this, oldInternal, replace);
    if (oldInternal == this.value) {
      oldInternal.isFirstChild(false);
      this.value = oldInternal.nextSibling;
      ChildNode firstChild = (ChildNode)this.value;
      if (firstChild != null) {
        firstChild.isFirstChild(true);
        firstChild.previousSibling = oldInternal.previousSibling;
      } 
    } else {
      ChildNode prev = oldInternal.previousSibling;
      ChildNode next = oldInternal.nextSibling;
      prev.nextSibling = next;
      if (next == null) {
        ChildNode firstChild = (ChildNode)this.value;
        firstChild.previousSibling = prev;
      } else {
        next.previousSibling = prev;
      } 
    } 
    ChildNode oldPreviousSibling = oldInternal.previousSibling();
    oldInternal.ownerNode = ownerDocument;
    oldInternal.isOwned(false);
    oldInternal.nextSibling = null;
    oldInternal.previousSibling = null;
    changed();
    ownerDocument.removedNode(this, replace);
    checkNormalizationAfterRemove(oldPreviousSibling);
    return oldInternal;
  }
  
  public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
    makeChildNode();
    CoreDocumentImpl ownerDocument = ownerDocument();
    ownerDocument.replacingNode(this);
    internalInsertBefore(newChild, oldChild, true);
    if (newChild != oldChild)
      internalRemoveChild(oldChild, true); 
    ownerDocument.replacedNode(this);
    return oldChild;
  }
  
  public int getLength() {
    if (hasStringValue())
      return 1; 
    ChildNode node = (ChildNode)this.value;
    int length = 0;
    for (; node != null; node = node.nextSibling)
      length++; 
    return length;
  }
  
  public Node item(int index) {
    if (hasStringValue()) {
      if (index != 0 || this.value == null)
        return null; 
      makeChildNode();
      return (Node)this.value;
    } 
    if (index < 0)
      return null; 
    ChildNode node = (ChildNode)this.value;
    for (int i = 0; i < index && node != null; i++)
      node = node.nextSibling; 
    return node;
  }
  
  public boolean isEqualNode(Node arg) {
    return super.isEqualNode(arg);
  }
  
  public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg, int derivationMethod) {
    return false;
  }
  
  public void setReadOnly(boolean readOnly, boolean deep) {
    super.setReadOnly(readOnly, deep);
    if (deep) {
      if (needsSyncChildren())
        synchronizeChildren(); 
      if (hasStringValue())
        return; 
      ChildNode mykid = (ChildNode)this.value;
      for (; mykid != null; 
        mykid = mykid.nextSibling) {
        if (mykid.getNodeType() != 5)
          mykid.setReadOnly(readOnly, true); 
      } 
    } 
  }
  
  protected void synchronizeChildren() {
    needsSyncChildren(false);
  }
  
  void checkNormalizationAfterInsert(ChildNode insertedChild) {
    if (insertedChild.getNodeType() == 3) {
      ChildNode prev = insertedChild.previousSibling();
      ChildNode next = insertedChild.nextSibling;
      if ((prev != null && prev.getNodeType() == 3) || (next != null && next
        .getNodeType() == 3))
        isNormalized(false); 
    } else if (!insertedChild.isNormalized()) {
      isNormalized(false);
    } 
  }
  
  void checkNormalizationAfterRemove(ChildNode previousSibling) {
    if (previousSibling != null && previousSibling
      .getNodeType() == 3) {
      ChildNode next = previousSibling.nextSibling;
      if (next != null && next.getNodeType() == 3)
        isNormalized(false); 
    } 
  }
  
  private void writeObject(ObjectOutputStream out) throws IOException {
    if (needsSyncChildren())
      synchronizeChildren(); 
    out.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    ois.defaultReadObject();
    needsSyncChildren(false);
  }
}
