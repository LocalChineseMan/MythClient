package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class TextImpl extends CharacterDataImpl implements CharacterData, Text {
  static final long serialVersionUID = -5294980852957403469L;
  
  public TextImpl() {}
  
  public TextImpl(CoreDocumentImpl ownerDoc, String data) {
    super(ownerDoc, data);
  }
  
  public void setValues(CoreDocumentImpl ownerDoc, String data) {
    this.flags = 0;
    this.nextSibling = null;
    this.previousSibling = null;
    setOwnerDocument(ownerDoc);
    this.data = data;
  }
  
  public short getNodeType() {
    return 3;
  }
  
  public String getNodeName() {
    return "#text";
  }
  
  public void setIgnorableWhitespace(boolean ignore) {
    if (needsSyncData())
      synchronizeData(); 
    isIgnorableWhitespace(ignore);
  }
  
  public boolean isElementContentWhitespace() {
    if (needsSyncData())
      synchronizeData(); 
    return internalIsIgnorableWhitespace();
  }
  
  public String getWholeText() {
    if (needsSyncData())
      synchronizeData(); 
    if (this.fBufferStr == null) {
      this.fBufferStr = new StringBuffer();
    } else {
      this.fBufferStr.setLength(0);
    } 
    if (this.data != null && this.data.length() != 0)
      this.fBufferStr.append(this.data); 
    getWholeTextBackward(getPreviousSibling(), this.fBufferStr, getParentNode());
    String temp = this.fBufferStr.toString();
    this.fBufferStr.setLength(0);
    getWholeTextForward(getNextSibling(), this.fBufferStr, getParentNode());
    return temp + this.fBufferStr.toString();
  }
  
  protected void insertTextContent(StringBuffer buf) throws DOMException {
    String content = getNodeValue();
    if (content != null)
      buf.insert(0, content); 
  }
  
  private boolean getWholeTextForward(Node node, StringBuffer buffer, Node parent) {
    boolean inEntRef = false;
    if (parent != null)
      inEntRef = (parent.getNodeType() == 5); 
    while (node != null) {
      short type = node.getNodeType();
      if (type == 5) {
        if (getWholeTextForward(node.getFirstChild(), buffer, node))
          return true; 
      } else if (type == 3 || type == 4) {
        ((NodeImpl)node).getTextContent(buffer);
      } else {
        return true;
      } 
      node = node.getNextSibling();
    } 
    if (inEntRef) {
      getWholeTextForward(parent.getNextSibling(), buffer, parent.getParentNode());
      return true;
    } 
    return false;
  }
  
  private boolean getWholeTextBackward(Node node, StringBuffer buffer, Node parent) {
    boolean inEntRef = false;
    if (parent != null)
      inEntRef = (parent.getNodeType() == 5); 
    while (node != null) {
      short type = node.getNodeType();
      if (type == 5) {
        if (getWholeTextBackward(node.getLastChild(), buffer, node))
          return true; 
      } else if (type == 3 || type == 4) {
        ((TextImpl)node).insertTextContent(buffer);
      } else {
        return true;
      } 
      node = node.getPreviousSibling();
    } 
    if (inEntRef) {
      getWholeTextBackward(parent.getPreviousSibling(), buffer, parent.getParentNode());
      return true;
    } 
    return false;
  }
  
  public Text replaceWholeText(String content) throws DOMException {
    if (needsSyncData())
      synchronizeData(); 
    Node parent = getParentNode();
    if (content == null || content.length() == 0) {
      if (parent != null)
        parent.removeChild(this); 
      return null;
    } 
    if ((ownerDocument()).errorChecking) {
      if (!canModifyPrev(this))
        throw new DOMException((short)7, 
            DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null)); 
      if (!canModifyNext(this))
        throw new DOMException((short)7, 
            DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null)); 
    } 
    Text currentNode = null;
    if (isReadOnly()) {
      Text newNode = ownerDocument().createTextNode(content);
      if (parent != null) {
        parent.insertBefore(newNode, this);
        parent.removeChild(this);
        currentNode = newNode;
      } else {
        return newNode;
      } 
    } else {
      setData(content);
      currentNode = this;
    } 
    Node prev = currentNode.getPreviousSibling();
    while (prev != null) {
      if (prev.getNodeType() == 3 || prev
        .getNodeType() == 4 || (prev
        .getNodeType() == 5 && hasTextOnlyChildren(prev))) {
        parent.removeChild(prev);
        prev = currentNode;
        prev = prev.getPreviousSibling();
      } 
    } 
    Node next = currentNode.getNextSibling();
    while (next != null) {
      if (next.getNodeType() == 3 || next
        .getNodeType() == 4 || (next
        .getNodeType() == 5 && hasTextOnlyChildren(next))) {
        parent.removeChild(next);
        next = currentNode;
        next = next.getNextSibling();
      } 
    } 
    return currentNode;
  }
  
  private boolean canModifyPrev(Node node) {
    boolean textLastChild = false;
    Node prev = node.getPreviousSibling();
    while (prev != null) {
      short type = prev.getNodeType();
      if (type == 5) {
        Node lastChild = prev.getLastChild();
        if (lastChild == null)
          return false; 
        while (lastChild != null) {
          short lType = lastChild.getNodeType();
          if (lType == 3 || lType == 4) {
            textLastChild = true;
          } else if (lType == 5) {
            if (!canModifyPrev(lastChild))
              return false; 
            textLastChild = true;
          } else {
            if (textLastChild)
              return false; 
            return true;
          } 
          lastChild = lastChild.getPreviousSibling();
        } 
      } else if (type != 3 && type != 4) {
        return true;
      } 
      prev = prev.getPreviousSibling();
    } 
    return true;
  }
  
  private boolean canModifyNext(Node node) {
    boolean textFirstChild = false;
    Node next = node.getNextSibling();
    while (next != null) {
      short type = next.getNodeType();
      if (type == 5) {
        Node firstChild = next.getFirstChild();
        if (firstChild == null)
          return false; 
        while (firstChild != null) {
          short lType = firstChild.getNodeType();
          if (lType == 3 || lType == 4) {
            textFirstChild = true;
          } else if (lType == 5) {
            if (!canModifyNext(firstChild))
              return false; 
            textFirstChild = true;
          } else {
            if (textFirstChild)
              return false; 
            return true;
          } 
          firstChild = firstChild.getNextSibling();
        } 
      } else if (type != 3 && type != 4) {
        return true;
      } 
      next = next.getNextSibling();
    } 
    return true;
  }
  
  private boolean hasTextOnlyChildren(Node node) {
    Node child = node;
    if (child == null)
      return false; 
    child = child.getFirstChild();
    while (child != null) {
      int type = child.getNodeType();
      if (type == 5)
        return hasTextOnlyChildren(child); 
      if (type != 3 && type != 4 && type != 5)
        return false; 
      child = child.getNextSibling();
    } 
    return true;
  }
  
  public boolean isIgnorableWhitespace() {
    if (needsSyncData())
      synchronizeData(); 
    return internalIsIgnorableWhitespace();
  }
  
  public Text splitText(int offset) throws DOMException {
    if (isReadOnly())
      throw new DOMException((short)7, 
          
          DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null)); 
    if (needsSyncData())
      synchronizeData(); 
    if (offset < 0 || offset > this.data.length())
      throw new DOMException((short)1, 
          DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null)); 
    Text newText = getOwnerDocument().createTextNode(this.data.substring(offset));
    setNodeValue(this.data.substring(0, offset));
    Node parentNode = getParentNode();
    if (parentNode != null)
      parentNode.insertBefore(newText, this.nextSibling); 
    return newText;
  }
  
  public void replaceData(String value) {
    this.data = value;
  }
  
  public String removeData() {
    String olddata = this.data;
    this.data = "";
    return olddata;
  }
}
