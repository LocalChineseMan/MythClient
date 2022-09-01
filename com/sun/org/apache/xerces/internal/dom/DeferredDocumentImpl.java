package com.sun.org.apache.xerces.internal.dom;

import java.util.ArrayList;
import java.util.Hashtable;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DeferredDocumentImpl extends DocumentImpl implements DeferredNode {
  static final long serialVersionUID = 5186323580749626857L;
  
  private static final boolean DEBUG_PRINT_REF_COUNTS = false;
  
  private static final boolean DEBUG_PRINT_TABLES = false;
  
  private static final boolean DEBUG_IDS = false;
  
  protected static final int CHUNK_SHIFT = 8;
  
  protected static final int CHUNK_SIZE = 256;
  
  protected static final int CHUNK_MASK = 255;
  
  protected static final int INITIAL_CHUNK_COUNT = 32;
  
  protected transient int fNodeCount = 0;
  
  protected transient int[][] fNodeType;
  
  protected transient Object[][] fNodeName;
  
  protected transient Object[][] fNodeValue;
  
  protected transient int[][] fNodeParent;
  
  protected transient int[][] fNodeLastChild;
  
  protected transient int[][] fNodePrevSib;
  
  protected transient Object[][] fNodeURI;
  
  protected transient int[][] fNodeExtra;
  
  protected transient int fIdCount;
  
  protected transient String[] fIdName;
  
  protected transient int[] fIdElement;
  
  protected boolean fNamespacesEnabled = false;
  
  private final transient StringBuilder fBufferStr = new StringBuilder();
  
  private final transient ArrayList fStrChunks = new ArrayList();
  
  public DeferredDocumentImpl() {
    this(false);
  }
  
  public DeferredDocumentImpl(boolean namespacesEnabled) {
    this(namespacesEnabled, false);
  }
  
  public DeferredDocumentImpl(boolean namespaces, boolean grammarAccess) {
    super(grammarAccess);
    needsSyncData(true);
    needsSyncChildren(true);
    this.fNamespacesEnabled = namespaces;
  }
  
  public DOMImplementation getImplementation() {
    return DeferredDOMImplementationImpl.getDOMImplementation();
  }
  
  boolean getNamespacesEnabled() {
    return this.fNamespacesEnabled;
  }
  
  void setNamespacesEnabled(boolean enable) {
    this.fNamespacesEnabled = enable;
  }
  
  public int createDeferredDocument() {
    int nodeIndex = createNode((short)9);
    return nodeIndex;
  }
  
  public int createDeferredDocumentType(String rootElementName, String publicId, String systemId) {
    int nodeIndex = createNode((short)10);
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    setChunkValue(this.fNodeName, rootElementName, chunk, index);
    setChunkValue(this.fNodeValue, publicId, chunk, index);
    setChunkValue(this.fNodeURI, systemId, chunk, index);
    return nodeIndex;
  }
  
  public void setInternalSubset(int doctypeIndex, String subset) {
    int chunk = doctypeIndex >> 8;
    int index = doctypeIndex & 0xFF;
    int extraDataIndex = createNode((short)10);
    int echunk = extraDataIndex >> 8;
    int eindex = extraDataIndex & 0xFF;
    setChunkIndex(this.fNodeExtra, extraDataIndex, chunk, index);
    setChunkValue(this.fNodeValue, subset, echunk, eindex);
  }
  
  public int createDeferredNotation(String notationName, String publicId, String systemId, String baseURI) {
    int nodeIndex = createNode((short)12);
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    int extraDataIndex = createNode((short)12);
    int echunk = extraDataIndex >> 8;
    int eindex = extraDataIndex & 0xFF;
    setChunkValue(this.fNodeName, notationName, chunk, index);
    setChunkValue(this.fNodeValue, publicId, chunk, index);
    setChunkValue(this.fNodeURI, systemId, chunk, index);
    setChunkIndex(this.fNodeExtra, extraDataIndex, chunk, index);
    setChunkValue(this.fNodeName, baseURI, echunk, eindex);
    return nodeIndex;
  }
  
  public int createDeferredEntity(String entityName, String publicId, String systemId, String notationName, String baseURI) {
    int nodeIndex = createNode((short)6);
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    int extraDataIndex = createNode((short)6);
    int echunk = extraDataIndex >> 8;
    int eindex = extraDataIndex & 0xFF;
    setChunkValue(this.fNodeName, entityName, chunk, index);
    setChunkValue(this.fNodeValue, publicId, chunk, index);
    setChunkValue(this.fNodeURI, systemId, chunk, index);
    setChunkIndex(this.fNodeExtra, extraDataIndex, chunk, index);
    setChunkValue(this.fNodeName, notationName, echunk, eindex);
    setChunkValue(this.fNodeValue, (Object)null, echunk, eindex);
    setChunkValue(this.fNodeURI, (Object)null, echunk, eindex);
    int extraDataIndex2 = createNode((short)6);
    int echunk2 = extraDataIndex2 >> 8;
    int eindex2 = extraDataIndex2 & 0xFF;
    setChunkIndex(this.fNodeExtra, extraDataIndex2, echunk, eindex);
    setChunkValue(this.fNodeName, baseURI, echunk2, eindex2);
    return nodeIndex;
  }
  
  public String getDeferredEntityBaseURI(int entityIndex) {
    if (entityIndex != -1) {
      int extraDataIndex = getNodeExtra(entityIndex, false);
      extraDataIndex = getNodeExtra(extraDataIndex, false);
      return getNodeName(extraDataIndex, false);
    } 
    return null;
  }
  
  public void setEntityInfo(int currentEntityDecl, String version, String encoding) {
    int eNodeIndex = getNodeExtra(currentEntityDecl, false);
    if (eNodeIndex != -1) {
      int echunk = eNodeIndex >> 8;
      int eindex = eNodeIndex & 0xFF;
      setChunkValue(this.fNodeValue, version, echunk, eindex);
      setChunkValue(this.fNodeURI, encoding, echunk, eindex);
    } 
  }
  
  public void setTypeInfo(int elementNodeIndex, Object type) {
    int elementChunk = elementNodeIndex >> 8;
    int elementIndex = elementNodeIndex & 0xFF;
    setChunkValue(this.fNodeValue, type, elementChunk, elementIndex);
  }
  
  public void setInputEncoding(int currentEntityDecl, String value) {
    int nodeIndex = getNodeExtra(currentEntityDecl, false);
    int extraDataIndex = getNodeExtra(nodeIndex, false);
    int echunk = extraDataIndex >> 8;
    int eindex = extraDataIndex & 0xFF;
    setChunkValue(this.fNodeValue, value, echunk, eindex);
  }
  
  public int createDeferredEntityReference(String name, String baseURI) {
    int nodeIndex = createNode((short)5);
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    setChunkValue(this.fNodeName, name, chunk, index);
    setChunkValue(this.fNodeValue, baseURI, chunk, index);
    return nodeIndex;
  }
  
  public int createDeferredElement(String elementURI, String elementName, Object type) {
    int elementNodeIndex = createNode((short)1);
    int elementChunk = elementNodeIndex >> 8;
    int elementIndex = elementNodeIndex & 0xFF;
    setChunkValue(this.fNodeName, elementName, elementChunk, elementIndex);
    setChunkValue(this.fNodeURI, elementURI, elementChunk, elementIndex);
    setChunkValue(this.fNodeValue, type, elementChunk, elementIndex);
    return elementNodeIndex;
  }
  
  public int createDeferredElement(String elementName) {
    return createDeferredElement((String)null, elementName);
  }
  
  public int createDeferredElement(String elementURI, String elementName) {
    int elementNodeIndex = createNode((short)1);
    int elementChunk = elementNodeIndex >> 8;
    int elementIndex = elementNodeIndex & 0xFF;
    setChunkValue(this.fNodeName, elementName, elementChunk, elementIndex);
    setChunkValue(this.fNodeURI, elementURI, elementChunk, elementIndex);
    return elementNodeIndex;
  }
  
  public int setDeferredAttribute(int elementNodeIndex, String attrName, String attrURI, String attrValue, boolean specified, boolean id, Object type) {
    int attrNodeIndex = createDeferredAttribute(attrName, attrURI, attrValue, specified);
    int attrChunk = attrNodeIndex >> 8;
    int attrIndex = attrNodeIndex & 0xFF;
    setChunkIndex(this.fNodeParent, elementNodeIndex, attrChunk, attrIndex);
    int elementChunk = elementNodeIndex >> 8;
    int elementIndex = elementNodeIndex & 0xFF;
    int lastAttrNodeIndex = getChunkIndex(this.fNodeExtra, elementChunk, elementIndex);
    if (lastAttrNodeIndex != 0)
      setChunkIndex(this.fNodePrevSib, lastAttrNodeIndex, attrChunk, attrIndex); 
    setChunkIndex(this.fNodeExtra, attrNodeIndex, elementChunk, elementIndex);
    int extra = getChunkIndex(this.fNodeExtra, attrChunk, attrIndex);
    if (id) {
      extra |= 0x200;
      setChunkIndex(this.fNodeExtra, extra, attrChunk, attrIndex);
      String value = getChunkValue(this.fNodeValue, attrChunk, attrIndex);
      putIdentifier(value, elementNodeIndex);
    } 
    if (type != null) {
      int extraDataIndex = createNode((short)20);
      int echunk = extraDataIndex >> 8;
      int eindex = extraDataIndex & 0xFF;
      setChunkIndex(this.fNodeLastChild, extraDataIndex, attrChunk, attrIndex);
      setChunkValue(this.fNodeValue, type, echunk, eindex);
    } 
    return attrNodeIndex;
  }
  
  public int setDeferredAttribute(int elementNodeIndex, String attrName, String attrURI, String attrValue, boolean specified) {
    int attrNodeIndex = createDeferredAttribute(attrName, attrURI, attrValue, specified);
    int attrChunk = attrNodeIndex >> 8;
    int attrIndex = attrNodeIndex & 0xFF;
    setChunkIndex(this.fNodeParent, elementNodeIndex, attrChunk, attrIndex);
    int elementChunk = elementNodeIndex >> 8;
    int elementIndex = elementNodeIndex & 0xFF;
    int lastAttrNodeIndex = getChunkIndex(this.fNodeExtra, elementChunk, elementIndex);
    if (lastAttrNodeIndex != 0)
      setChunkIndex(this.fNodePrevSib, lastAttrNodeIndex, attrChunk, attrIndex); 
    setChunkIndex(this.fNodeExtra, attrNodeIndex, elementChunk, elementIndex);
    return attrNodeIndex;
  }
  
  public int createDeferredAttribute(String attrName, String attrValue, boolean specified) {
    return createDeferredAttribute(attrName, (String)null, attrValue, specified);
  }
  
  public int createDeferredAttribute(String attrName, String attrURI, String attrValue, boolean specified) {
    int nodeIndex = createNode((short)2);
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    setChunkValue(this.fNodeName, attrName, chunk, index);
    setChunkValue(this.fNodeURI, attrURI, chunk, index);
    setChunkValue(this.fNodeValue, attrValue, chunk, index);
    int extra = specified ? 32 : 0;
    setChunkIndex(this.fNodeExtra, extra, chunk, index);
    return nodeIndex;
  }
  
  public int createDeferredElementDefinition(String elementName) {
    int nodeIndex = createNode((short)21);
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    setChunkValue(this.fNodeName, elementName, chunk, index);
    return nodeIndex;
  }
  
  public int createDeferredTextNode(String data, boolean ignorableWhitespace) {
    int nodeIndex = createNode((short)3);
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    setChunkValue(this.fNodeValue, data, chunk, index);
    setChunkIndex(this.fNodeExtra, ignorableWhitespace ? 1 : 0, chunk, index);
    return nodeIndex;
  }
  
  public int createDeferredCDATASection(String data) {
    int nodeIndex = createNode((short)4);
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    setChunkValue(this.fNodeValue, data, chunk, index);
    return nodeIndex;
  }
  
  public int createDeferredProcessingInstruction(String target, String data) {
    int nodeIndex = createNode((short)7);
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    setChunkValue(this.fNodeName, target, chunk, index);
    setChunkValue(this.fNodeValue, data, chunk, index);
    return nodeIndex;
  }
  
  public int createDeferredComment(String data) {
    int nodeIndex = createNode((short)8);
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    setChunkValue(this.fNodeValue, data, chunk, index);
    return nodeIndex;
  }
  
  public int cloneNode(int nodeIndex, boolean deep) {
    int nchunk = nodeIndex >> 8;
    int nindex = nodeIndex & 0xFF;
    int nodeType = this.fNodeType[nchunk][nindex];
    int cloneIndex = createNode((short)nodeType);
    int cchunk = cloneIndex >> 8;
    int cindex = cloneIndex & 0xFF;
    setChunkValue(this.fNodeName, this.fNodeName[nchunk][nindex], cchunk, cindex);
    setChunkValue(this.fNodeValue, this.fNodeValue[nchunk][nindex], cchunk, cindex);
    setChunkValue(this.fNodeURI, this.fNodeURI[nchunk][nindex], cchunk, cindex);
    int extraIndex = this.fNodeExtra[nchunk][nindex];
    if (extraIndex != -1) {
      if (nodeType != 2 && nodeType != 3)
        extraIndex = cloneNode(extraIndex, false); 
      setChunkIndex(this.fNodeExtra, extraIndex, cchunk, cindex);
    } 
    if (deep) {
      int prevIndex = -1;
      int childIndex = getLastChild(nodeIndex, false);
      while (childIndex != -1) {
        int clonedChildIndex = cloneNode(childIndex, deep);
        insertBefore(cloneIndex, clonedChildIndex, prevIndex);
        prevIndex = clonedChildIndex;
        childIndex = getRealPrevSibling(childIndex, false);
      } 
    } 
    return cloneIndex;
  }
  
  public void appendChild(int parentIndex, int childIndex) {
    int pchunk = parentIndex >> 8;
    int pindex = parentIndex & 0xFF;
    int cchunk = childIndex >> 8;
    int cindex = childIndex & 0xFF;
    setChunkIndex(this.fNodeParent, parentIndex, cchunk, cindex);
    int olast = getChunkIndex(this.fNodeLastChild, pchunk, pindex);
    setChunkIndex(this.fNodePrevSib, olast, cchunk, cindex);
    setChunkIndex(this.fNodeLastChild, childIndex, pchunk, pindex);
  }
  
  public int setAttributeNode(int elemIndex, int attrIndex) {
    int echunk = elemIndex >> 8;
    int eindex = elemIndex & 0xFF;
    int achunk = attrIndex >> 8;
    int aindex = attrIndex & 0xFF;
    String attrName = getChunkValue(this.fNodeName, achunk, aindex);
    int oldAttrIndex = getChunkIndex(this.fNodeExtra, echunk, eindex);
    int nextIndex = -1;
    int oachunk = -1;
    int oaindex = -1;
    while (oldAttrIndex != -1) {
      oachunk = oldAttrIndex >> 8;
      oaindex = oldAttrIndex & 0xFF;
      String oldAttrName = getChunkValue(this.fNodeName, oachunk, oaindex);
      if (oldAttrName.equals(attrName))
        break; 
      nextIndex = oldAttrIndex;
      oldAttrIndex = getChunkIndex(this.fNodePrevSib, oachunk, oaindex);
    } 
    if (oldAttrIndex != -1) {
      int i = getChunkIndex(this.fNodePrevSib, oachunk, oaindex);
      if (nextIndex == -1) {
        setChunkIndex(this.fNodeExtra, i, echunk, eindex);
      } else {
        int pchunk = nextIndex >> 8;
        int pindex = nextIndex & 0xFF;
        setChunkIndex(this.fNodePrevSib, i, pchunk, pindex);
      } 
      clearChunkIndex(this.fNodeType, oachunk, oaindex);
      clearChunkValue(this.fNodeName, oachunk, oaindex);
      clearChunkValue(this.fNodeValue, oachunk, oaindex);
      clearChunkIndex(this.fNodeParent, oachunk, oaindex);
      clearChunkIndex(this.fNodePrevSib, oachunk, oaindex);
      int attrTextIndex = clearChunkIndex(this.fNodeLastChild, oachunk, oaindex);
      int atchunk = attrTextIndex >> 8;
      int atindex = attrTextIndex & 0xFF;
      clearChunkIndex(this.fNodeType, atchunk, atindex);
      clearChunkValue(this.fNodeValue, atchunk, atindex);
      clearChunkIndex(this.fNodeParent, atchunk, atindex);
      clearChunkIndex(this.fNodeLastChild, atchunk, atindex);
    } 
    int prevIndex = getChunkIndex(this.fNodeExtra, echunk, eindex);
    setChunkIndex(this.fNodeExtra, attrIndex, echunk, eindex);
    setChunkIndex(this.fNodePrevSib, prevIndex, achunk, aindex);
    return oldAttrIndex;
  }
  
  public void setIdAttributeNode(int elemIndex, int attrIndex) {
    int chunk = attrIndex >> 8;
    int index = attrIndex & 0xFF;
    int extra = getChunkIndex(this.fNodeExtra, chunk, index);
    extra |= 0x200;
    setChunkIndex(this.fNodeExtra, extra, chunk, index);
    String value = getChunkValue(this.fNodeValue, chunk, index);
    putIdentifier(value, elemIndex);
  }
  
  public void setIdAttribute(int attrIndex) {
    int chunk = attrIndex >> 8;
    int index = attrIndex & 0xFF;
    int extra = getChunkIndex(this.fNodeExtra, chunk, index);
    extra |= 0x200;
    setChunkIndex(this.fNodeExtra, extra, chunk, index);
  }
  
  public int insertBefore(int parentIndex, int newChildIndex, int refChildIndex) {
    if (refChildIndex == -1) {
      appendChild(parentIndex, newChildIndex);
      return newChildIndex;
    } 
    int nchunk = newChildIndex >> 8;
    int nindex = newChildIndex & 0xFF;
    int rchunk = refChildIndex >> 8;
    int rindex = refChildIndex & 0xFF;
    int previousIndex = getChunkIndex(this.fNodePrevSib, rchunk, rindex);
    setChunkIndex(this.fNodePrevSib, newChildIndex, rchunk, rindex);
    setChunkIndex(this.fNodePrevSib, previousIndex, nchunk, nindex);
    return newChildIndex;
  }
  
  public void setAsLastChild(int parentIndex, int childIndex) {
    int pchunk = parentIndex >> 8;
    int pindex = parentIndex & 0xFF;
    setChunkIndex(this.fNodeLastChild, childIndex, pchunk, pindex);
  }
  
  public int getParentNode(int nodeIndex) {
    return getParentNode(nodeIndex, false);
  }
  
  public int getParentNode(int nodeIndex, boolean free) {
    if (nodeIndex == -1)
      return -1; 
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    return free ? clearChunkIndex(this.fNodeParent, chunk, index) : 
      getChunkIndex(this.fNodeParent, chunk, index);
  }
  
  public int getLastChild(int nodeIndex) {
    return getLastChild(nodeIndex, true);
  }
  
  public int getLastChild(int nodeIndex, boolean free) {
    if (nodeIndex == -1)
      return -1; 
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    return free ? clearChunkIndex(this.fNodeLastChild, chunk, index) : 
      getChunkIndex(this.fNodeLastChild, chunk, index);
  }
  
  public int getPrevSibling(int nodeIndex) {
    return getPrevSibling(nodeIndex, true);
  }
  
  public int getPrevSibling(int nodeIndex, boolean free) {
    if (nodeIndex == -1)
      return -1; 
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    int type = getChunkIndex(this.fNodeType, chunk, index);
    if (type == 3) {
      do {
        nodeIndex = getChunkIndex(this.fNodePrevSib, chunk, index);
        if (nodeIndex == -1)
          break; 
        chunk = nodeIndex >> 8;
        index = nodeIndex & 0xFF;
        type = getChunkIndex(this.fNodeType, chunk, index);
      } while (type == 3);
    } else {
      nodeIndex = getChunkIndex(this.fNodePrevSib, chunk, index);
    } 
    return nodeIndex;
  }
  
  public int getRealPrevSibling(int nodeIndex) {
    return getRealPrevSibling(nodeIndex, true);
  }
  
  public int getRealPrevSibling(int nodeIndex, boolean free) {
    if (nodeIndex == -1)
      return -1; 
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    return free ? clearChunkIndex(this.fNodePrevSib, chunk, index) : 
      getChunkIndex(this.fNodePrevSib, chunk, index);
  }
  
  public int lookupElementDefinition(String elementName) {
    if (this.fNodeCount > 1) {
      int docTypeIndex = -1;
      int nchunk = 0;
      int nindex = 0;
      int index = getChunkIndex(this.fNodeLastChild, nchunk, nindex);
      for (; index != -1; 
        index = getChunkIndex(this.fNodePrevSib, nchunk, nindex)) {
        nchunk = index >> 8;
        nindex = index & 0xFF;
        if (getChunkIndex(this.fNodeType, nchunk, nindex) == 10) {
          docTypeIndex = index;
          break;
        } 
      } 
      if (docTypeIndex == -1)
        return -1; 
      nchunk = docTypeIndex >> 8;
      nindex = docTypeIndex & 0xFF;
      index = getChunkIndex(this.fNodeLastChild, nchunk, nindex);
      for (; index != -1; 
        index = getChunkIndex(this.fNodePrevSib, nchunk, nindex)) {
        nchunk = index >> 8;
        nindex = index & 0xFF;
        if (getChunkIndex(this.fNodeType, nchunk, nindex) == 21 && 
          
          getChunkValue(this.fNodeName, nchunk, nindex) == elementName)
          return index; 
      } 
    } 
    return -1;
  }
  
  public DeferredNode getNodeObject(int nodeIndex) {
    if (nodeIndex == -1)
      return null; 
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    int type = getChunkIndex(this.fNodeType, chunk, index);
    if (type != 3 && type != 4)
      clearChunkIndex(this.fNodeType, chunk, index); 
    DeferredNode node = null;
    switch (type) {
      case 2:
        if (this.fNamespacesEnabled) {
          node = new DeferredAttrNSImpl(this, nodeIndex);
          break;
        } 
        node = new DeferredAttrImpl(this, nodeIndex);
        break;
      case 4:
        node = new DeferredCDATASectionImpl(this, nodeIndex);
        break;
      case 8:
        node = new DeferredCommentImpl(this, nodeIndex);
        break;
      case 9:
        node = (DeferredNode)this;
        break;
      case 10:
        node = new DeferredDocumentTypeImpl(this, nodeIndex);
        this.docType = (DocumentTypeImpl)node;
        break;
      case 1:
        if (this.fNamespacesEnabled) {
          node = new DeferredElementNSImpl(this, nodeIndex);
        } else {
          node = new DeferredElementImpl(this, nodeIndex);
        } 
        if (this.fIdElement != null) {
          int idIndex = binarySearch(this.fIdElement, 0, this.fIdCount - 1, nodeIndex);
          while (idIndex != -1) {
            String name = this.fIdName[idIndex];
            if (name != null) {
              putIdentifier0(name, (Element)node);
              this.fIdName[idIndex] = null;
            } 
            if (idIndex + 1 < this.fIdCount && this.fIdElement[idIndex + 1] == nodeIndex) {
              idIndex++;
              continue;
            } 
            idIndex = -1;
          } 
        } 
        break;
      case 6:
        node = new DeferredEntityImpl(this, nodeIndex);
        break;
      case 5:
        node = new DeferredEntityReferenceImpl(this, nodeIndex);
        break;
      case 12:
        node = new DeferredNotationImpl(this, nodeIndex);
        break;
      case 7:
        node = new DeferredProcessingInstructionImpl(this, nodeIndex);
        break;
      case 3:
        node = new DeferredTextImpl(this, nodeIndex);
        break;
      case 21:
        node = new DeferredElementDefinitionImpl(this, nodeIndex);
        break;
      default:
        throw new IllegalArgumentException("type: " + type);
    } 
    if (node != null)
      return node; 
    throw new IllegalArgumentException();
  }
  
  public String getNodeName(int nodeIndex) {
    return getNodeName(nodeIndex, true);
  }
  
  public String getNodeName(int nodeIndex, boolean free) {
    if (nodeIndex == -1)
      return null; 
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    return free ? clearChunkValue(this.fNodeName, chunk, index) : 
      getChunkValue(this.fNodeName, chunk, index);
  }
  
  public String getNodeValueString(int nodeIndex) {
    return getNodeValueString(nodeIndex, true);
  }
  
  public String getNodeValueString(int nodeIndex, boolean free) {
    if (nodeIndex == -1)
      return null; 
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    String value = free ? clearChunkValue(this.fNodeValue, chunk, index) : getChunkValue(this.fNodeValue, chunk, index);
    if (value == null)
      return null; 
    int type = getChunkIndex(this.fNodeType, chunk, index);
    if (type == 3) {
      int prevSib = getRealPrevSibling(nodeIndex);
      if (prevSib != -1 && 
        getNodeType(prevSib, false) == 3) {
        this.fStrChunks.add(value);
        do {
          chunk = prevSib >> 8;
          index = prevSib & 0xFF;
          value = getChunkValue(this.fNodeValue, chunk, index);
          this.fStrChunks.add(value);
          prevSib = getChunkIndex(this.fNodePrevSib, chunk, index);
          if (prevSib == -1)
            break; 
        } while (getNodeType(prevSib, false) == 3);
        int chunkCount = this.fStrChunks.size();
        for (int i = chunkCount - 1; i >= 0; i--)
          this.fBufferStr.append(this.fStrChunks.get(i)); 
        value = this.fBufferStr.toString();
        this.fStrChunks.clear();
        this.fBufferStr.setLength(0);
        return value;
      } 
    } else if (type == 4) {
      int child = getLastChild(nodeIndex, false);
      if (child != -1) {
        this.fBufferStr.append(value);
        while (child != -1) {
          chunk = child >> 8;
          index = child & 0xFF;
          value = getChunkValue(this.fNodeValue, chunk, index);
          this.fStrChunks.add(value);
          child = getChunkIndex(this.fNodePrevSib, chunk, index);
        } 
        for (int i = this.fStrChunks.size() - 1; i >= 0; i--)
          this.fBufferStr.append(this.fStrChunks.get(i)); 
        value = this.fBufferStr.toString();
        this.fStrChunks.clear();
        this.fBufferStr.setLength(0);
        return value;
      } 
    } 
    return value;
  }
  
  public String getNodeValue(int nodeIndex) {
    return getNodeValue(nodeIndex, true);
  }
  
  public Object getTypeInfo(int nodeIndex) {
    if (nodeIndex == -1)
      return null; 
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    Object value = (this.fNodeValue[chunk] != null) ? this.fNodeValue[chunk][index] : null;
    if (value != null) {
      this.fNodeValue[chunk][index] = null;
      RefCount c = (RefCount)this.fNodeValue[chunk][256];
      c.fCount--;
      if (c.fCount == 0)
        this.fNodeValue[chunk] = null; 
    } 
    return value;
  }
  
  public String getNodeValue(int nodeIndex, boolean free) {
    if (nodeIndex == -1)
      return null; 
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    return free ? clearChunkValue(this.fNodeValue, chunk, index) : 
      getChunkValue(this.fNodeValue, chunk, index);
  }
  
  public int getNodeExtra(int nodeIndex) {
    return getNodeExtra(nodeIndex, true);
  }
  
  public int getNodeExtra(int nodeIndex, boolean free) {
    if (nodeIndex == -1)
      return -1; 
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    return free ? clearChunkIndex(this.fNodeExtra, chunk, index) : 
      getChunkIndex(this.fNodeExtra, chunk, index);
  }
  
  public short getNodeType(int nodeIndex) {
    return getNodeType(nodeIndex, true);
  }
  
  public short getNodeType(int nodeIndex, boolean free) {
    if (nodeIndex == -1)
      return -1; 
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    return free ? (short)clearChunkIndex(this.fNodeType, chunk, index) : 
      (short)getChunkIndex(this.fNodeType, chunk, index);
  }
  
  public String getAttribute(int elemIndex, String name) {
    if (elemIndex == -1 || name == null)
      return null; 
    int echunk = elemIndex >> 8;
    int eindex = elemIndex & 0xFF;
    int attrIndex = getChunkIndex(this.fNodeExtra, echunk, eindex);
    while (attrIndex != -1) {
      int achunk = attrIndex >> 8;
      int aindex = attrIndex & 0xFF;
      if (getChunkValue(this.fNodeName, achunk, aindex) == name)
        return getChunkValue(this.fNodeValue, achunk, aindex); 
      attrIndex = getChunkIndex(this.fNodePrevSib, achunk, aindex);
    } 
    return null;
  }
  
  public String getNodeURI(int nodeIndex) {
    return getNodeURI(nodeIndex, true);
  }
  
  public String getNodeURI(int nodeIndex, boolean free) {
    if (nodeIndex == -1)
      return null; 
    int chunk = nodeIndex >> 8;
    int index = nodeIndex & 0xFF;
    return free ? clearChunkValue(this.fNodeURI, chunk, index) : 
      getChunkValue(this.fNodeURI, chunk, index);
  }
  
  public void putIdentifier(String name, int elementNodeIndex) {
    if (this.fIdName == null) {
      this.fIdName = new String[64];
      this.fIdElement = new int[64];
    } 
    if (this.fIdCount == this.fIdName.length) {
      String[] idName = new String[this.fIdCount * 2];
      System.arraycopy(this.fIdName, 0, idName, 0, this.fIdCount);
      this.fIdName = idName;
      int[] idElement = new int[idName.length];
      System.arraycopy(this.fIdElement, 0, idElement, 0, this.fIdCount);
      this.fIdElement = idElement;
    } 
    this.fIdName[this.fIdCount] = name;
    this.fIdElement[this.fIdCount] = elementNodeIndex;
    this.fIdCount++;
  }
  
  public void print() {}
  
  public int getNodeIndex() {
    return 0;
  }
  
  protected void synchronizeData() {
    needsSyncData(false);
    if (this.fIdElement != null) {
      IntVector path = new IntVector();
      for (int i = 0; i < this.fIdCount; i++) {
        int elementNodeIndex = this.fIdElement[i];
        String idName = this.fIdName[i];
        if (idName != null) {
          path.removeAllElements();
          int index = elementNodeIndex;
          do {
            path.addElement(index);
            int pchunk = index >> 8;
            int pindex = index & 0xFF;
            index = getChunkIndex(this.fNodeParent, pchunk, pindex);
          } while (index != -1);
          Node place = (Node)this;
          for (int j = path.size() - 2; j >= 0; j--) {
            index = path.elementAt(j);
            Node child = place.getLastChild();
            while (child != null) {
              if (child instanceof DeferredNode) {
                int nodeIndex = ((DeferredNode)child).getNodeIndex();
                if (nodeIndex == index) {
                  place = child;
                  break;
                } 
              } 
              child = child.getPreviousSibling();
            } 
          } 
          Element element = (Element)place;
          putIdentifier0(idName, element);
          this.fIdName[i] = null;
          while (i + 1 < this.fIdCount && this.fIdElement[i + 1] == elementNodeIndex) {
            idName = this.fIdName[++i];
            if (idName == null)
              continue; 
            putIdentifier0(idName, element);
          } 
        } 
      } 
    } 
  }
  
  protected void synchronizeChildren() {
    if (needsSyncData()) {
      synchronizeData();
      if (!needsSyncChildren())
        return; 
    } 
    boolean orig = this.mutationEvents;
    this.mutationEvents = false;
    needsSyncChildren(false);
    getNodeType(0);
    ChildNode first = null;
    ChildNode last = null;
    int index = getLastChild(0);
    for (; index != -1; 
      index = getPrevSibling(index)) {
      ChildNode node = (ChildNode)getNodeObject(index);
      if (last == null) {
        last = node;
      } else {
        first.previousSibling = node;
      } 
      node.ownerNode = (NodeImpl)this;
      node.isOwned(true);
      node.nextSibling = first;
      first = node;
      int type = node.getNodeType();
      if (type == 1) {
        this.docElement = (ElementImpl)node;
      } else if (type == 10) {
        this.docType = (DocumentTypeImpl)node;
      } 
    } 
    if (first != null) {
      this.firstChild = first;
      first.isFirstChild(true);
      lastChild(last);
    } 
    this.mutationEvents = orig;
  }
  
  protected final void synchronizeChildren(AttrImpl a, int nodeIndex) {
    boolean orig = getMutationEvents();
    setMutationEvents(false);
    a.needsSyncChildren(false);
    int last = getLastChild(nodeIndex);
    int prev = getPrevSibling(last);
    if (prev == -1) {
      a.value = getNodeValueString(nodeIndex);
      a.hasStringValue(true);
    } else {
      ChildNode firstNode = null;
      ChildNode lastNode = null;
      int index;
      for (index = last; index != -1; 
        index = getPrevSibling(index)) {
        ChildNode node = (ChildNode)getNodeObject(index);
        if (lastNode == null) {
          lastNode = node;
        } else {
          firstNode.previousSibling = node;
        } 
        node.ownerNode = a;
        node.isOwned(true);
        node.nextSibling = firstNode;
        firstNode = node;
      } 
      if (lastNode != null) {
        a.value = firstNode;
        firstNode.isFirstChild(true);
        a.lastChild(lastNode);
      } 
      a.hasStringValue(false);
    } 
    setMutationEvents(orig);
  }
  
  protected final void synchronizeChildren(ParentNode p, int nodeIndex) {
    boolean orig = getMutationEvents();
    setMutationEvents(false);
    p.needsSyncChildren(false);
    ChildNode firstNode = null;
    ChildNode lastNode = null;
    int index = getLastChild(nodeIndex);
    for (; index != -1; 
      index = getPrevSibling(index)) {
      ChildNode node = (ChildNode)getNodeObject(index);
      if (lastNode == null) {
        lastNode = node;
      } else {
        firstNode.previousSibling = node;
      } 
      node.ownerNode = p;
      node.isOwned(true);
      node.nextSibling = firstNode;
      firstNode = node;
    } 
    if (lastNode != null) {
      p.firstChild = firstNode;
      firstNode.isFirstChild(true);
      p.lastChild(lastNode);
    } 
    setMutationEvents(orig);
  }
  
  protected void ensureCapacity(int chunk) {
    if (this.fNodeType == null) {
      this.fNodeType = new int[32][];
      this.fNodeName = new Object[32][];
      this.fNodeValue = new Object[32][];
      this.fNodeParent = new int[32][];
      this.fNodeLastChild = new int[32][];
      this.fNodePrevSib = new int[32][];
      this.fNodeURI = new Object[32][];
      this.fNodeExtra = new int[32][];
    } else if (this.fNodeType.length <= chunk) {
      int newsize = chunk * 2;
      int[][] newArray = new int[newsize][];
      System.arraycopy(this.fNodeType, 0, newArray, 0, chunk);
      this.fNodeType = newArray;
      Object[][] newStrArray = new Object[newsize][];
      System.arraycopy(this.fNodeName, 0, newStrArray, 0, chunk);
      this.fNodeName = newStrArray;
      newStrArray = new Object[newsize][];
      System.arraycopy(this.fNodeValue, 0, newStrArray, 0, chunk);
      this.fNodeValue = newStrArray;
      newArray = new int[newsize][];
      System.arraycopy(this.fNodeParent, 0, newArray, 0, chunk);
      this.fNodeParent = newArray;
      newArray = new int[newsize][];
      System.arraycopy(this.fNodeLastChild, 0, newArray, 0, chunk);
      this.fNodeLastChild = newArray;
      newArray = new int[newsize][];
      System.arraycopy(this.fNodePrevSib, 0, newArray, 0, chunk);
      this.fNodePrevSib = newArray;
      newStrArray = new Object[newsize][];
      System.arraycopy(this.fNodeURI, 0, newStrArray, 0, chunk);
      this.fNodeURI = newStrArray;
      newArray = new int[newsize][];
      System.arraycopy(this.fNodeExtra, 0, newArray, 0, chunk);
      this.fNodeExtra = newArray;
    } else if (this.fNodeType[chunk] != null) {
      return;
    } 
    createChunk(this.fNodeType, chunk);
    createChunk(this.fNodeName, chunk);
    createChunk(this.fNodeValue, chunk);
    createChunk(this.fNodeParent, chunk);
    createChunk(this.fNodeLastChild, chunk);
    createChunk(this.fNodePrevSib, chunk);
    createChunk(this.fNodeURI, chunk);
    createChunk(this.fNodeExtra, chunk);
  }
  
  protected int createNode(short nodeType) {
    int chunk = this.fNodeCount >> 8;
    int index = this.fNodeCount & 0xFF;
    ensureCapacity(chunk);
    setChunkIndex(this.fNodeType, nodeType, chunk, index);
    return this.fNodeCount++;
  }
  
  protected static int binarySearch(int[] values, int start, int end, int target) {
    while (start <= end) {
      int middle = start + end >>> 1;
      int value = values[middle];
      if (value == target) {
        while (middle > 0 && values[middle - 1] == target)
          middle--; 
        return middle;
      } 
      if (value > target) {
        end = middle - 1;
        continue;
      } 
      start = middle + 1;
    } 
    return -1;
  }
  
  private static final int[] INIT_ARRAY = new int[257];
  
  static {
    for (int i = 0; i < 256; i++)
      INIT_ARRAY[i] = -1; 
  }
  
  private final void createChunk(int[][] data, int chunk) {
    data[chunk] = new int[257];
    System.arraycopy(INIT_ARRAY, 0, data[chunk], 0, 256);
  }
  
  static final class DeferredDocumentImpl {}
  
  static final class RefCount {
    int fCount;
  }
  
  private final void createChunk(Object[][] data, int chunk) {
    data[chunk] = new Object[257];
    data[chunk][256] = new RefCount();
  }
  
  private final int setChunkIndex(int[][] data, int value, int chunk, int index) {
    if (value == -1)
      return clearChunkIndex(data, chunk, index); 
    int[] dataChunk = data[chunk];
    if (dataChunk == null) {
      createChunk(data, chunk);
      dataChunk = data[chunk];
    } 
    int ovalue = dataChunk[index];
    if (ovalue == -1)
      dataChunk[256] = dataChunk[256] + 1; 
    dataChunk[index] = value;
    return ovalue;
  }
  
  private final String setChunkValue(Object[][] data, Object value, int chunk, int index) {
    if (value == null)
      return clearChunkValue(data, chunk, index); 
    Object[] dataChunk = data[chunk];
    if (dataChunk == null) {
      createChunk(data, chunk);
      dataChunk = data[chunk];
    } 
    String ovalue = (String)dataChunk[index];
    if (ovalue == null) {
      RefCount c = (RefCount)dataChunk[256];
      c.fCount++;
    } 
    dataChunk[index] = value;
    return ovalue;
  }
  
  private final int getChunkIndex(int[][] data, int chunk, int index) {
    return (data[chunk] != null) ? data[chunk][index] : -1;
  }
  
  private final String getChunkValue(Object[][] data, int chunk, int index) {
    return (data[chunk] != null) ? (String)data[chunk][index] : null;
  }
  
  private final String getNodeValue(int chunk, int index) {
    Object data = this.fNodeValue[chunk][index];
    if (data == null)
      return null; 
    if (data instanceof String)
      return (String)data; 
    return data.toString();
  }
  
  private final int clearChunkIndex(int[][] data, int chunk, int index) {
    int value = (data[chunk] != null) ? data[chunk][index] : -1;
    if (value != -1) {
      data[chunk][256] = data[chunk][256] - 1;
      data[chunk][index] = -1;
      if (data[chunk][256] == 0)
        data[chunk] = null; 
    } 
    return value;
  }
  
  private final String clearChunkValue(Object[][] data, int chunk, int index) {
    String value = (data[chunk] != null) ? (String)data[chunk][index] : null;
    if (value != null) {
      data[chunk][index] = null;
      RefCount c = (RefCount)data[chunk][256];
      c.fCount--;
      if (c.fCount == 0)
        data[chunk] = null; 
    } 
    return value;
  }
  
  private final void putIdentifier0(String idName, Element element) {
    if (this.identifiers == null)
      this.identifiers = new Hashtable<>(); 
    this.identifiers.put(idName, element);
  }
  
  private static void print(int[] values, int start, int end, int middle, int target) {}
}
