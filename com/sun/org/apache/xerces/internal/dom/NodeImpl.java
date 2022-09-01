package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public abstract class NodeImpl implements Node, NodeList, EventTarget, Cloneable, Serializable {
  public static final short TREE_POSITION_PRECEDING = 1;
  
  public static final short TREE_POSITION_FOLLOWING = 2;
  
  public static final short TREE_POSITION_ANCESTOR = 4;
  
  public static final short TREE_POSITION_DESCENDANT = 8;
  
  public static final short TREE_POSITION_EQUIVALENT = 16;
  
  public static final short TREE_POSITION_SAME_NODE = 32;
  
  public static final short TREE_POSITION_DISCONNECTED = 0;
  
  public static final short DOCUMENT_POSITION_DISCONNECTED = 1;
  
  public static final short DOCUMENT_POSITION_PRECEDING = 2;
  
  public static final short DOCUMENT_POSITION_FOLLOWING = 4;
  
  public static final short DOCUMENT_POSITION_CONTAINS = 8;
  
  public static final short DOCUMENT_POSITION_IS_CONTAINED = 16;
  
  public static final short DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC = 32;
  
  static final long serialVersionUID = -6316591992167219696L;
  
  public static final short ELEMENT_DEFINITION_NODE = 21;
  
  protected NodeImpl ownerNode;
  
  protected short flags;
  
  protected static final short READONLY = 1;
  
  protected static final short SYNCDATA = 2;
  
  protected static final short SYNCCHILDREN = 4;
  
  protected static final short OWNED = 8;
  
  protected static final short FIRSTCHILD = 16;
  
  protected static final short SPECIFIED = 32;
  
  protected static final short IGNORABLEWS = 64;
  
  protected static final short HASSTRING = 128;
  
  protected static final short NORMALIZED = 256;
  
  protected static final short ID = 512;
  
  protected NodeImpl(CoreDocumentImpl ownerDocument) {
    this.ownerNode = ownerDocument;
  }
  
  public NodeImpl() {}
  
  public abstract short getNodeType();
  
  public abstract String getNodeName();
  
  public String getNodeValue() throws DOMException {
    return null;
  }
  
  public void setNodeValue(String x) throws DOMException {}
  
  public Node appendChild(Node newChild) throws DOMException {
    return insertBefore(newChild, null);
  }
  
  public Node cloneNode(boolean deep) {
    NodeImpl newnode;
    if (needsSyncData())
      synchronizeData(); 
    try {
      newnode = (NodeImpl)clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException("**Internal Error**" + e);
    } 
    newnode.ownerNode = ownerDocument();
    newnode.isOwned(false);
    newnode.isReadOnly(false);
    ownerDocument().callUserDataHandlers(this, newnode, (short)1);
    return newnode;
  }
  
  public Document getOwnerDocument() {
    if (isOwned())
      return this.ownerNode.ownerDocument(); 
    return (Document)this.ownerNode;
  }
  
  CoreDocumentImpl ownerDocument() {
    if (isOwned())
      return this.ownerNode.ownerDocument(); 
    return (CoreDocumentImpl)this.ownerNode;
  }
  
  void setOwnerDocument(CoreDocumentImpl doc) {
    if (needsSyncData())
      synchronizeData(); 
    if (!isOwned())
      this.ownerNode = doc; 
  }
  
  protected int getNodeNumber() {
    CoreDocumentImpl cd = (CoreDocumentImpl)getOwnerDocument();
    int nodeNumber = cd.getNodeNumber(this);
    return nodeNumber;
  }
  
  public Node getParentNode() {
    return null;
  }
  
  NodeImpl parentNode() {
    return null;
  }
  
  public Node getNextSibling() {
    return null;
  }
  
  public Node getPreviousSibling() {
    return null;
  }
  
  ChildNode previousSibling() {
    return null;
  }
  
  public NamedNodeMap getAttributes() {
    return null;
  }
  
  public boolean hasAttributes() {
    return false;
  }
  
  public boolean hasChildNodes() {
    return false;
  }
  
  public NodeList getChildNodes() {
    return this;
  }
  
  public Node getFirstChild() {
    return null;
  }
  
  public Node getLastChild() {
    return null;
  }
  
  public Node insertBefore(Node newChild, Node refChild) throws DOMException {
    throw new DOMException((short)3, 
        DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
  }
  
  public Node removeChild(Node oldChild) throws DOMException {
    throw new DOMException((short)8, 
        DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
  }
  
  public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
    throw new DOMException((short)3, 
        DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
  }
  
  public int getLength() {
    return 0;
  }
  
  public Node item(int index) {
    return null;
  }
  
  public void normalize() {}
  
  public boolean isSupported(String feature, String version) {
    return ownerDocument().getImplementation().hasFeature(feature, version);
  }
  
  public String getNamespaceURI() {
    return null;
  }
  
  public String getPrefix() {
    return null;
  }
  
  public void setPrefix(String prefix) throws DOMException {
    throw new DOMException((short)14, 
        DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null));
  }
  
  public String getLocalName() {
    return null;
  }
  
  public void addEventListener(String type, EventListener listener, boolean useCapture) {
    ownerDocument().addEventListener(this, type, listener, useCapture);
  }
  
  public void removeEventListener(String type, EventListener listener, boolean useCapture) {
    ownerDocument().removeEventListener(this, type, listener, useCapture);
  }
  
  public boolean dispatchEvent(Event event) {
    return ownerDocument().dispatchEvent(this, event);
  }
  
  public String getBaseURI() {
    return null;
  }
  
  public short compareTreePosition(Node other) {
    if (this == other)
      return 48; 
    short thisType = getNodeType();
    short otherType = other.getNodeType();
    if (thisType == 6 || thisType == 12 || otherType == 6 || otherType == 12)
      return 0; 
    Node thisAncestor = this;
    Node otherAncestor = other;
    int thisDepth = 0;
    int otherDepth = 0;
    Node node;
    for (node = this; node != null; node = node.getParentNode()) {
      thisDepth++;
      if (node == other)
        return 5; 
      thisAncestor = node;
    } 
    for (node = other; node != null; node = node.getParentNode()) {
      otherDepth++;
      if (node == this)
        return 10; 
      otherAncestor = node;
    } 
    Node thisNode = this;
    Node otherNode = other;
    int thisAncestorType = thisAncestor.getNodeType();
    int otherAncestorType = otherAncestor.getNodeType();
    if (thisAncestorType == 2)
      thisNode = ((AttrImpl)thisAncestor).getOwnerElement(); 
    if (otherAncestorType == 2)
      otherNode = ((AttrImpl)otherAncestor).getOwnerElement(); 
    if (thisAncestorType == 2 && otherAncestorType == 2 && thisNode == otherNode)
      return 16; 
    if (thisAncestorType == 2) {
      thisDepth = 0;
      for (node = thisNode; node != null; node = node.getParentNode()) {
        thisDepth++;
        if (node == otherNode)
          return 1; 
        thisAncestor = node;
      } 
    } 
    if (otherAncestorType == 2) {
      otherDepth = 0;
      for (node = otherNode; node != null; node = node.getParentNode()) {
        otherDepth++;
        if (node == thisNode)
          return 2; 
        otherAncestor = node;
      } 
    } 
    if (thisAncestor != otherAncestor)
      return 0; 
    if (thisDepth > otherDepth) {
      for (int i = 0; i < thisDepth - otherDepth; i++)
        thisNode = thisNode.getParentNode(); 
      if (thisNode == otherNode)
        return 1; 
    } else {
      for (int i = 0; i < otherDepth - thisDepth; i++)
        otherNode = otherNode.getParentNode(); 
      if (otherNode == thisNode)
        return 2; 
    } 
    Node thisNodeP = thisNode.getParentNode();
    Node otherNodeP = otherNode.getParentNode();
    while (thisNodeP != otherNodeP) {
      thisNode = thisNodeP;
      otherNode = otherNodeP;
      thisNodeP = thisNodeP.getParentNode();
      otherNodeP = otherNodeP.getParentNode();
    } 
    Node current = thisNodeP.getFirstChild();
    for (; current != null; 
      current = current.getNextSibling()) {
      if (current == otherNode)
        return 1; 
      if (current == thisNode)
        return 2; 
    } 
    return 0;
  }
  
  public short compareDocumentPosition(Node other) throws DOMException {
    Document thisOwnerDoc, otherOwnerDoc;
    DocumentType container;
    if (this == other)
      return 0; 
    try {
      NodeImpl nodeImpl = (NodeImpl)other;
    } catch (ClassCastException e) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, msg);
    } 
    if (getNodeType() == 9) {
      thisOwnerDoc = (Document)this;
    } else {
      thisOwnerDoc = getOwnerDocument();
    } 
    if (other.getNodeType() == 9) {
      otherOwnerDoc = (Document)other;
    } else {
      otherOwnerDoc = other.getOwnerDocument();
    } 
    if (thisOwnerDoc != otherOwnerDoc && thisOwnerDoc != null && otherOwnerDoc != null) {
      int otherDocNum = ((CoreDocumentImpl)otherOwnerDoc).getNodeNumber();
      int thisDocNum = ((CoreDocumentImpl)thisOwnerDoc).getNodeNumber();
      if (otherDocNum > thisDocNum)
        return 37; 
      return 35;
    } 
    Node thisAncestor = this;
    Node otherAncestor = other;
    int thisDepth = 0;
    int otherDepth = 0;
    Node node;
    for (node = this; node != null; node = node.getParentNode()) {
      thisDepth++;
      if (node == other)
        return 10; 
      thisAncestor = node;
    } 
    for (node = other; node != null; node = node.getParentNode()) {
      otherDepth++;
      if (node == this)
        return 20; 
      otherAncestor = node;
    } 
    int thisAncestorType = thisAncestor.getNodeType();
    int otherAncestorType = otherAncestor.getNodeType();
    Node thisNode = this;
    Node otherNode = other;
    switch (thisAncestorType) {
      case 6:
      case 12:
        container = thisOwnerDoc.getDoctype();
        if (container == otherAncestor)
          return 10; 
        switch (otherAncestorType) {
          case 6:
          case 12:
            if (thisAncestorType != otherAncestorType)
              return (thisAncestorType > otherAncestorType) ? 2 : 4; 
            if (thisAncestorType == 12) {
              if (((NamedNodeMapImpl)container.getNotations()).precedes(otherAncestor, thisAncestor))
                return 34; 
              return 36;
            } 
            if (((NamedNodeMapImpl)container.getEntities()).precedes(otherAncestor, thisAncestor))
              return 34; 
            return 36;
        } 
        thisNode = thisAncestor = thisOwnerDoc;
        break;
      case 10:
        if (otherNode == thisOwnerDoc)
          return 10; 
        if (thisOwnerDoc != null && thisOwnerDoc == otherOwnerDoc)
          return 4; 
        break;
      case 2:
        thisNode = ((AttrImpl)thisAncestor).getOwnerElement();
        if (otherAncestorType == 2) {
          otherNode = ((AttrImpl)otherAncestor).getOwnerElement();
          if (otherNode == thisNode) {
            if (((NamedNodeMapImpl)thisNode.getAttributes()).precedes(other, this))
              return 34; 
            return 36;
          } 
        } 
        thisDepth = 0;
        for (node = thisNode; node != null; node = node.getParentNode()) {
          thisDepth++;
          if (node == otherNode)
            return 10; 
          thisAncestor = node;
        } 
        break;
    } 
    switch (otherAncestorType) {
      case 6:
      case 12:
        container = thisOwnerDoc.getDoctype();
        if (container == this)
          return 20; 
        otherNode = otherAncestor = thisOwnerDoc;
        break;
      case 10:
        if (thisNode == otherOwnerDoc)
          return 20; 
        if (otherOwnerDoc != null && thisOwnerDoc == otherOwnerDoc)
          return 2; 
        break;
      case 2:
        otherDepth = 0;
        otherNode = ((AttrImpl)otherAncestor).getOwnerElement();
        for (node = otherNode; node != null; node = node.getParentNode()) {
          otherDepth++;
          if (node == thisNode)
            return 20; 
          otherAncestor = node;
        } 
        break;
    } 
    if (thisAncestor != otherAncestor) {
      int thisAncestorNum = ((NodeImpl)thisAncestor).getNodeNumber();
      int otherAncestorNum = ((NodeImpl)otherAncestor).getNodeNumber();
      if (thisAncestorNum > otherAncestorNum)
        return 37; 
      return 35;
    } 
    if (thisDepth > otherDepth) {
      for (int i = 0; i < thisDepth - otherDepth; i++)
        thisNode = thisNode.getParentNode(); 
      if (thisNode == otherNode)
        return 2; 
    } else {
      for (int i = 0; i < otherDepth - thisDepth; i++)
        otherNode = otherNode.getParentNode(); 
      if (otherNode == thisNode)
        return 4; 
    } 
    Node thisNodeP = thisNode.getParentNode();
    Node otherNodeP = otherNode.getParentNode();
    while (thisNodeP != otherNodeP) {
      thisNode = thisNodeP;
      otherNode = otherNodeP;
      thisNodeP = thisNodeP.getParentNode();
      otherNodeP = otherNodeP.getParentNode();
    } 
    Node current = thisNodeP.getFirstChild();
    for (; current != null; 
      current = current.getNextSibling()) {
      if (current == otherNode)
        return 2; 
      if (current == thisNode)
        return 4; 
    } 
    return 0;
  }
  
  public String getTextContent() throws DOMException {
    return getNodeValue();
  }
  
  void getTextContent(StringBuffer buf) throws DOMException {
    String content = getNodeValue();
    if (content != null)
      buf.append(content); 
  }
  
  public void setTextContent(String textContent) throws DOMException {
    setNodeValue(textContent);
  }
  
  public boolean isSameNode(Node other) {
    return (this == other);
  }
  
  public boolean isDefaultNamespace(String namespaceURI) {
    String namespace, prefix;
    NodeImpl nodeImpl1;
    short type = getNodeType();
    switch (type) {
      case 1:
        namespace = getNamespaceURI();
        prefix = getPrefix();
        if (prefix == null || prefix.length() == 0) {
          if (namespaceURI == null)
            return (namespace == namespaceURI); 
          return namespaceURI.equals(namespace);
        } 
        if (hasAttributes()) {
          ElementImpl elem = (ElementImpl)this;
          NodeImpl attr = (NodeImpl)elem.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", "xmlns");
          if (attr != null) {
            String value = attr.getNodeValue();
            if (namespaceURI == null)
              return (namespace == value); 
            return namespaceURI.equals(value);
          } 
        } 
        nodeImpl1 = (NodeImpl)getElementAncestor(this);
        if (nodeImpl1 != null)
          return nodeImpl1.isDefaultNamespace(namespaceURI); 
        return false;
      case 9:
        return ((NodeImpl)((Document)this).getDocumentElement()).isDefaultNamespace(namespaceURI);
      case 6:
      case 10:
      case 11:
      case 12:
        return false;
      case 2:
        if (this.ownerNode.getNodeType() == 1)
          return this.ownerNode.isDefaultNamespace(namespaceURI); 
        return false;
    } 
    NodeImpl ancestor = (NodeImpl)getElementAncestor(this);
    if (ancestor != null)
      return ancestor.isDefaultNamespace(namespaceURI); 
    return false;
  }
  
  public String lookupPrefix(String namespaceURI) {
    String namespace;
    if (namespaceURI == null)
      return null; 
    short type = getNodeType();
    switch (type) {
      case 1:
        namespace = getNamespaceURI();
        return lookupNamespacePrefix(namespaceURI, (ElementImpl)this);
      case 9:
        return ((NodeImpl)((Document)this).getDocumentElement()).lookupPrefix(namespaceURI);
      case 6:
      case 10:
      case 11:
      case 12:
        return null;
      case 2:
        if (this.ownerNode.getNodeType() == 1)
          return this.ownerNode.lookupPrefix(namespaceURI); 
        return null;
    } 
    NodeImpl ancestor = (NodeImpl)getElementAncestor(this);
    if (ancestor != null)
      return ancestor.lookupPrefix(namespaceURI); 
    return null;
  }
  
  public String lookupNamespaceURI(String specifiedPrefix) {
    String namespace, prefix;
    NodeImpl nodeImpl1;
    short type = getNodeType();
    switch (type) {
      case 1:
        namespace = getNamespaceURI();
        prefix = getPrefix();
        if (namespace != null) {
          if (specifiedPrefix == null && prefix == specifiedPrefix)
            return namespace; 
          if (prefix != null && prefix.equals(specifiedPrefix))
            return namespace; 
        } 
        if (hasAttributes()) {
          NamedNodeMap map = getAttributes();
          int length = map.getLength();
          for (int i = 0; i < length; i++) {
            Node attr = map.item(i);
            String attrPrefix = attr.getPrefix();
            String value = attr.getNodeValue();
            namespace = attr.getNamespaceURI();
            if (namespace != null && namespace.equals("http://www.w3.org/2000/xmlns/")) {
              if (specifiedPrefix == null && attr
                .getNodeName().equals("xmlns"))
                return value; 
              if (attrPrefix != null && attrPrefix
                .equals("xmlns") && attr
                .getLocalName().equals(specifiedPrefix))
                return value; 
            } 
          } 
        } 
        nodeImpl1 = (NodeImpl)getElementAncestor(this);
        if (nodeImpl1 != null)
          return nodeImpl1.lookupNamespaceURI(specifiedPrefix); 
        return null;
      case 9:
        return ((NodeImpl)((Document)this).getDocumentElement()).lookupNamespaceURI(specifiedPrefix);
      case 6:
      case 10:
      case 11:
      case 12:
        return null;
      case 2:
        if (this.ownerNode.getNodeType() == 1)
          return this.ownerNode.lookupNamespaceURI(specifiedPrefix); 
        return null;
    } 
    NodeImpl ancestor = (NodeImpl)getElementAncestor(this);
    if (ancestor != null)
      return ancestor.lookupNamespaceURI(specifiedPrefix); 
    return null;
  }
  
  Node getElementAncestor(Node currentNode) {
    Node parent = currentNode.getParentNode();
    if (parent != null) {
      short type = parent.getNodeType();
      if (type == 1)
        return parent; 
      return getElementAncestor(parent);
    } 
    return null;
  }
  
  String lookupNamespacePrefix(String namespaceURI, ElementImpl el) {
    String namespace = getNamespaceURI();
    String prefix = getPrefix();
    if (namespace != null && namespace.equals(namespaceURI) && 
      prefix != null) {
      String foundNamespace = el.lookupNamespaceURI(prefix);
      if (foundNamespace != null && foundNamespace.equals(namespaceURI))
        return prefix; 
    } 
    if (hasAttributes()) {
      NamedNodeMap map = getAttributes();
      int length = map.getLength();
      for (int i = 0; i < length; i++) {
        Node attr = map.item(i);
        String attrPrefix = attr.getPrefix();
        String value = attr.getNodeValue();
        namespace = attr.getNamespaceURI();
        if (namespace != null && namespace.equals("http://www.w3.org/2000/xmlns/"))
          if (attr.getNodeName().equals("xmlns") || (attrPrefix != null && attrPrefix
            .equals("xmlns") && value
            .equals(namespaceURI))) {
            String localname = attr.getLocalName();
            String foundNamespace = el.lookupNamespaceURI(localname);
            if (foundNamespace != null && foundNamespace.equals(namespaceURI))
              return localname; 
          }  
      } 
    } 
    NodeImpl ancestor = (NodeImpl)getElementAncestor(this);
    if (ancestor != null)
      return ancestor.lookupNamespacePrefix(namespaceURI, el); 
    return null;
  }
  
  public boolean isEqualNode(Node arg) {
    if (arg == this)
      return true; 
    if (arg.getNodeType() != getNodeType())
      return false; 
    if (getNodeName() == null) {
      if (arg.getNodeName() != null)
        return false; 
    } else if (!getNodeName().equals(arg.getNodeName())) {
      return false;
    } 
    if (getLocalName() == null) {
      if (arg.getLocalName() != null)
        return false; 
    } else if (!getLocalName().equals(arg.getLocalName())) {
      return false;
    } 
    if (getNamespaceURI() == null) {
      if (arg.getNamespaceURI() != null)
        return false; 
    } else if (!getNamespaceURI().equals(arg.getNamespaceURI())) {
      return false;
    } 
    if (getPrefix() == null) {
      if (arg.getPrefix() != null)
        return false; 
    } else if (!getPrefix().equals(arg.getPrefix())) {
      return false;
    } 
    if (getNodeValue() == null) {
      if (arg.getNodeValue() != null)
        return false; 
    } else if (!getNodeValue().equals(arg.getNodeValue())) {
      return false;
    } 
    return true;
  }
  
  public Object getFeature(String feature, String version) {
    return isSupported(feature, version) ? this : null;
  }
  
  public Object setUserData(String key, Object data, UserDataHandler handler) {
    return ownerDocument().setUserData(this, key, data, handler);
  }
  
  public Object getUserData(String key) {
    return ownerDocument().getUserData(this, key);
  }
  
  protected Hashtable getUserDataRecord() {
    return ownerDocument().getUserDataRecord(this);
  }
  
  public void setReadOnly(boolean readOnly, boolean deep) {
    if (needsSyncData())
      synchronizeData(); 
    isReadOnly(readOnly);
  }
  
  public boolean getReadOnly() {
    if (needsSyncData())
      synchronizeData(); 
    return isReadOnly();
  }
  
  public void setUserData(Object data) {
    ownerDocument().setUserData(this, data);
  }
  
  public Object getUserData() {
    return ownerDocument().getUserData(this);
  }
  
  protected void changed() {
    ownerDocument().changed();
  }
  
  protected int changes() {
    return ownerDocument().changes();
  }
  
  protected void synchronizeData() {
    needsSyncData(false);
  }
  
  protected Node getContainer() {
    return null;
  }
  
  final boolean isReadOnly() {
    return ((this.flags & 0x1) != 0);
  }
  
  final void isReadOnly(boolean value) {
    this.flags = (short)(value ? (this.flags | 0x1) : (this.flags & 0xFFFFFFFE));
  }
  
  final boolean needsSyncData() {
    return ((this.flags & 0x2) != 0);
  }
  
  final void needsSyncData(boolean value) {
    this.flags = (short)(value ? (this.flags | 0x2) : (this.flags & 0xFFFFFFFD));
  }
  
  final boolean needsSyncChildren() {
    return ((this.flags & 0x4) != 0);
  }
  
  public final void needsSyncChildren(boolean value) {
    this.flags = (short)(value ? (this.flags | 0x4) : (this.flags & 0xFFFFFFFB));
  }
  
  final boolean isOwned() {
    return ((this.flags & 0x8) != 0);
  }
  
  final void isOwned(boolean value) {
    this.flags = (short)(value ? (this.flags | 0x8) : (this.flags & 0xFFFFFFF7));
  }
  
  final boolean isFirstChild() {
    return ((this.flags & 0x10) != 0);
  }
  
  final void isFirstChild(boolean value) {
    this.flags = (short)(value ? (this.flags | 0x10) : (this.flags & 0xFFFFFFEF));
  }
  
  final boolean isSpecified() {
    return ((this.flags & 0x20) != 0);
  }
  
  final void isSpecified(boolean value) {
    this.flags = (short)(value ? (this.flags | 0x20) : (this.flags & 0xFFFFFFDF));
  }
  
  final boolean internalIsIgnorableWhitespace() {
    return ((this.flags & 0x40) != 0);
  }
  
  final void isIgnorableWhitespace(boolean value) {
    this.flags = (short)(value ? (this.flags | 0x40) : (this.flags & 0xFFFFFFBF));
  }
  
  final boolean hasStringValue() {
    return ((this.flags & 0x80) != 0);
  }
  
  final void hasStringValue(boolean value) {
    this.flags = (short)(value ? (this.flags | 0x80) : (this.flags & 0xFFFFFF7F));
  }
  
  final boolean isNormalized() {
    return ((this.flags & 0x100) != 0);
  }
  
  final void isNormalized(boolean value) {
    if (!value && isNormalized() && this.ownerNode != null)
      this.ownerNode.isNormalized(false); 
    this.flags = (short)(value ? (this.flags | 0x100) : (this.flags & 0xFFFFFEFF));
  }
  
  final boolean isIdAttribute() {
    return ((this.flags & 0x200) != 0);
  }
  
  final void isIdAttribute(boolean value) {
    this.flags = (short)(value ? (this.flags | 0x200) : (this.flags & 0xFFFFFDFF));
  }
  
  public String toString() {
    return "[" + getNodeName() + ": " + getNodeValue() + "]";
  }
  
  private void writeObject(ObjectOutputStream out) throws IOException {
    if (needsSyncData())
      synchronizeData(); 
    out.defaultWriteObject();
  }
}
