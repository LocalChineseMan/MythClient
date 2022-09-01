package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class ParentNode extends ChildNode {
  static final long serialVersionUID = 2815829867152120872L;
  
  protected CoreDocumentImpl ownerDocument;
  
  protected ChildNode firstChild = null;
  
  protected transient NodeListCache fNodeListCache = null;
  
  protected ParentNode(CoreDocumentImpl ownerDocument) {
    super(ownerDocument);
    this.ownerDocument = ownerDocument;
  }
  
  public Node cloneNode(boolean deep) {
    if (needsSyncChildren())
      synchronizeChildren(); 
    ParentNode newnode = (ParentNode)super.cloneNode(deep);
    newnode.ownerDocument = this.ownerDocument;
    newnode.firstChild = null;
    newnode.fNodeListCache = null;
    if (deep) {
      ChildNode child = this.firstChild;
      for (; child != null; 
        child = child.nextSibling)
        newnode.appendChild(child.cloneNode(true)); 
    } 
    return (Node)newnode;
  }
  
  public Document getOwnerDocument() {
    return this.ownerDocument;
  }
  
  CoreDocumentImpl ownerDocument() {
    return this.ownerDocument;
  }
  
  void setOwnerDocument(CoreDocumentImpl doc) {
    if (needsSyncChildren())
      synchronizeChildren(); 
    ChildNode child = this.firstChild;
    for (; child != null; child = child.nextSibling)
      child.setOwnerDocument(doc); 
    super.setOwnerDocument(doc);
    this.ownerDocument = doc;
  }
  
  public boolean hasChildNodes() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return (this.firstChild != null);
  }
  
  public NodeList getChildNodes() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return (NodeList)this;
  }
  
  public Node getFirstChild() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return this.firstChild;
  }
  
  public Node getLastChild() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return lastChild();
  }
  
  final ChildNode lastChild() {
    return (this.firstChild != null) ? this.firstChild.previousSibling : null;
  }
  
  final void lastChild(ChildNode node) {
    if (this.firstChild != null)
      this.firstChild.previousSibling = node; 
  }
  
  public Node insertBefore(Node newChild, Node refChild) throws DOMException {
    return internalInsertBefore(newChild, refChild, false);
  }
  
  Node internalInsertBefore(Node newChild, Node refChild, boolean replace) throws DOMException {
    boolean errorChecking = this.ownerDocument.errorChecking;
    if (newChild.getNodeType() == 11) {
      if (errorChecking) {
        Node kid = newChild.getFirstChild();
        for (; kid != null; kid = kid.getNextSibling()) {
          if (!this.ownerDocument.isKidOK((Node)this, kid))
            throw new DOMException((short)3, 
                
                DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null)); 
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
      if (isReadOnly())
        throw new DOMException((short)7, 
            
            DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null)); 
      if (newChild.getOwnerDocument() != this.ownerDocument && newChild != this.ownerDocument)
        throw new DOMException((short)4, 
            DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null)); 
      if (!this.ownerDocument.isKidOK((Node)this, newChild))
        throw new DOMException((short)3, 
            DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null)); 
      if (refChild != null && refChild.getParentNode() != this)
        throw new DOMException((short)8, 
            DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null)); 
      if (this.ownerDocument.ancestorChecking) {
        boolean treeSafe = true;
        for (NodeImpl a = (NodeImpl)this; treeSafe && a != null; a = a.parentNode())
          treeSafe = (newChild != a); 
        if (!treeSafe)
          throw new DOMException((short)3, 
              DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null)); 
      } 
    } 
    this.ownerDocument.insertingNode((NodeImpl)this, replace);
    ChildNode newInternal = (ChildNode)newChild;
    Node oldparent = newInternal.parentNode();
    if (oldparent != null)
      oldparent.removeChild(newInternal); 
    ChildNode refInternal = (ChildNode)refChild;
    newInternal.ownerNode = (NodeImpl)this;
    newInternal.isOwned(true);
    if (this.firstChild == null) {
      this.firstChild = newInternal;
      newInternal.isFirstChild(true);
      newInternal.previousSibling = newInternal;
    } else if (refInternal == null) {
      ChildNode lastChild = this.firstChild.previousSibling;
      lastChild.nextSibling = newInternal;
      newInternal.previousSibling = lastChild;
      this.firstChild.previousSibling = newInternal;
    } else if (refChild == this.firstChild) {
      this.firstChild.isFirstChild(false);
      newInternal.nextSibling = this.firstChild;
      newInternal.previousSibling = this.firstChild.previousSibling;
      this.firstChild.previousSibling = newInternal;
      this.firstChild = newInternal;
      newInternal.isFirstChild(true);
    } else {
      ChildNode prev = refInternal.previousSibling;
      newInternal.nextSibling = refInternal;
      prev.nextSibling = newInternal;
      refInternal.previousSibling = newInternal;
      newInternal.previousSibling = prev;
    } 
    changed();
    if (this.fNodeListCache != null) {
      if (this.fNodeListCache.fLength != -1)
        this.fNodeListCache.fLength++; 
      if (this.fNodeListCache.fChildIndex != -1)
        if (this.fNodeListCache.fChild == refInternal) {
          this.fNodeListCache.fChild = newInternal;
        } else {
          this.fNodeListCache.fChildIndex = -1;
        }  
    } 
    this.ownerDocument.insertedNode((NodeImpl)this, newInternal, replace);
    checkNormalizationAfterInsert(newInternal);
    return newChild;
  }
  
  public Node removeChild(Node oldChild) throws DOMException {
    return internalRemoveChild(oldChild, false);
  }
  
  Node internalRemoveChild(Node oldChild, boolean replace) throws DOMException {
    CoreDocumentImpl ownerDocument = ownerDocument();
    if (ownerDocument.errorChecking) {
      if (isReadOnly())
        throw new DOMException((short)7, 
            
            DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null)); 
      if (oldChild != null && oldChild.getParentNode() != this)
        throw new DOMException((short)8, 
            DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null)); 
    } 
    ChildNode oldInternal = (ChildNode)oldChild;
    ownerDocument.removingNode((NodeImpl)this, oldInternal, replace);
    if (this.fNodeListCache != null) {
      if (this.fNodeListCache.fLength != -1)
        this.fNodeListCache.fLength--; 
      if (this.fNodeListCache.fChildIndex != -1)
        if (this.fNodeListCache.fChild == oldInternal) {
          this.fNodeListCache.fChildIndex--;
          this.fNodeListCache.fChild = oldInternal.previousSibling();
        } else {
          this.fNodeListCache.fChildIndex = -1;
        }  
    } 
    if (oldInternal == this.firstChild) {
      oldInternal.isFirstChild(false);
      this.firstChild = oldInternal.nextSibling;
      if (this.firstChild != null) {
        this.firstChild.isFirstChild(true);
        this.firstChild.previousSibling = oldInternal.previousSibling;
      } 
    } else {
      ChildNode prev = oldInternal.previousSibling;
      ChildNode next = oldInternal.nextSibling;
      prev.nextSibling = next;
      if (next == null) {
        this.firstChild.previousSibling = prev;
      } else {
        next.previousSibling = prev;
      } 
    } 
    ChildNode oldPreviousSibling = oldInternal.previousSibling();
    oldInternal.ownerNode = (NodeImpl)ownerDocument;
    oldInternal.isOwned(false);
    oldInternal.nextSibling = null;
    oldInternal.previousSibling = null;
    changed();
    ownerDocument.removedNode((NodeImpl)this, replace);
    checkNormalizationAfterRemove(oldPreviousSibling);
    return oldInternal;
  }
  
  public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
    this.ownerDocument.replacingNode((NodeImpl)this);
    internalInsertBefore(newChild, oldChild, true);
    if (newChild != oldChild)
      internalRemoveChild(oldChild, true); 
    this.ownerDocument.replacedNode((NodeImpl)this);
    return oldChild;
  }
  
  public String getTextContent() throws DOMException {
    Node child = getFirstChild();
    if (child != null) {
      Node next = child.getNextSibling();
      if (next == null)
        return hasTextContent(child) ? ((NodeImpl)child).getTextContent() : ""; 
      if (this.fBufferStr == null) {
        this.fBufferStr = new StringBuffer();
      } else {
        this.fBufferStr.setLength(0);
      } 
      getTextContent(this.fBufferStr);
      return this.fBufferStr.toString();
    } 
    return "";
  }
  
  void getTextContent(StringBuffer buf) throws DOMException {
    Node child = getFirstChild();
    while (child != null) {
      if (hasTextContent(child))
        ((NodeImpl)child).getTextContent(buf); 
      child = child.getNextSibling();
    } 
  }
  
  final boolean hasTextContent(Node child) {
    return (child.getNodeType() != 8 && child
      .getNodeType() != 7 && (child
      .getNodeType() != 3 || 
      !((TextImpl)child).isIgnorableWhitespace()));
  }
  
  public void setTextContent(String textContent) throws DOMException {
    Node child;
    while ((child = getFirstChild()) != null)
      removeChild(child); 
    if (textContent != null && textContent.length() != 0)
      appendChild(ownerDocument().createTextNode(textContent)); 
  }
  
  private int nodeListGetLength() {
    if (this.fNodeListCache == null) {
      if (this.firstChild == null)
        return 0; 
      if (this.firstChild == lastChild())
        return 1; 
      this.fNodeListCache = this.ownerDocument.getNodeListCache(this);
    } 
    if (this.fNodeListCache.fLength == -1) {
      int l;
      ChildNode n;
      if (this.fNodeListCache.fChildIndex != -1 && this.fNodeListCache.fChild != null) {
        l = this.fNodeListCache.fChildIndex;
        n = this.fNodeListCache.fChild;
      } else {
        n = this.firstChild;
        l = 0;
      } 
      while (n != null) {
        l++;
        n = n.nextSibling;
      } 
      this.fNodeListCache.fLength = l;
    } 
    return this.fNodeListCache.fLength;
  }
  
  public int getLength() {
    return nodeListGetLength();
  }
  
  private Node nodeListItem(int index) {
    if (this.fNodeListCache == null) {
      if (this.firstChild == lastChild())
        return (index == 0) ? this.firstChild : null; 
      this.fNodeListCache = this.ownerDocument.getNodeListCache(this);
    } 
    int i = this.fNodeListCache.fChildIndex;
    ChildNode n = this.fNodeListCache.fChild;
    boolean firstAccess = true;
    if (i != -1 && n != null) {
      firstAccess = false;
      if (i < index) {
        while (i < index && n != null) {
          i++;
          n = n.nextSibling;
        } 
      } else if (i > index) {
        while (i > index && n != null) {
          i--;
          n = n.previousSibling();
        } 
      } 
    } else {
      if (index < 0)
        return null; 
      n = this.firstChild;
      for (i = 0; i < index && n != null; i++)
        n = n.nextSibling; 
    } 
    if (!firstAccess && (n == this.firstChild || n == lastChild())) {
      this.fNodeListCache.fChildIndex = -1;
      this.fNodeListCache.fChild = null;
      this.ownerDocument.freeNodeListCache(this.fNodeListCache);
    } else {
      this.fNodeListCache.fChildIndex = i;
      this.fNodeListCache.fChild = n;
    } 
    return n;
  }
  
  public Node item(int index) {
    return nodeListItem(index);
  }
  
  protected final NodeList getChildNodesUnoptimized() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return (NodeList)new Object(this);
  }
  
  public void normalize() {
    if (isNormalized())
      return; 
    if (needsSyncChildren())
      synchronizeChildren(); 
    for (ChildNode kid = this.firstChild; kid != null; kid = kid.nextSibling)
      kid.normalize(); 
    isNormalized(true);
  }
  
  public boolean isEqualNode(Node arg) {
    if (!super.isEqualNode(arg))
      return false; 
    Node child1 = getFirstChild();
    Node child2 = arg.getFirstChild();
    while (child1 != null && child2 != null) {
      if (!((NodeImpl)child1).isEqualNode(child2))
        return false; 
      child1 = child1.getNextSibling();
      child2 = child2.getNextSibling();
    } 
    if (child1 != child2)
      return false; 
    return true;
  }
  
  public void setReadOnly(boolean readOnly, boolean deep) {
    super.setReadOnly(readOnly, deep);
    if (deep) {
      if (needsSyncChildren())
        synchronizeChildren(); 
      ChildNode mykid = this.firstChild;
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
  
  public ParentNode() {}
  
  class ParentNode {}
}
