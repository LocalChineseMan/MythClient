package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Hashtable;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class CoreDocumentImpl extends ParentNode implements Document {
  transient DOMNormalizer domNormalizer = null;
  
  transient DOMConfigurationImpl fConfiguration = null;
  
  transient Object fXPathEvaluator = null;
  
  protected int changes = 0;
  
  protected boolean errorChecking = true;
  
  protected boolean ancestorChecking = true;
  
  protected boolean xmlVersionChanged = false;
  
  private int documentNumber = 0;
  
  private int nodeCounter = 0;
  
  private boolean xml11Version = false;
  
  private static final int[] kidOK = new int[13];
  
  static final long serialVersionUID = 0L;
  
  protected DocumentTypeImpl docType;
  
  protected ElementImpl docElement;
  
  transient NodeListCache fFreeNLCache;
  
  protected String encoding;
  
  protected String actualEncoding;
  
  protected String version;
  
  protected boolean standalone;
  
  protected String fDocumentURI;
  
  protected Hashtable userData;
  
  protected Hashtable identifiers;
  
  protected boolean allowGrammarAccess;
  
  private Hashtable nodeTable;
  
  static {
    kidOK[9] = 1410;
    kidOK[1] = 442;
    kidOK[5] = 442;
    kidOK[6] = 442;
    kidOK[11] = 442;
    kidOK[2] = 40;
    kidOK[12] = 0;
    kidOK[4] = 0;
    kidOK[3] = 0;
    kidOK[8] = 0;
    kidOK[7] = 0;
    kidOK[10] = 0;
  }
  
  public CoreDocumentImpl() {
    this(false);
  }
  
  public CoreDocumentImpl(boolean grammarAccess) {
    super((CoreDocumentImpl)null);
    this.ownerDocument = this;
    this.allowGrammarAccess = grammarAccess;
    String systemProp = SecuritySupport.getSystemProperty("http://java.sun.com/xml/dom/properties/ancestor-check");
    if (systemProp != null && 
      systemProp.equalsIgnoreCase("false"))
      this.ancestorChecking = false; 
  }
  
  public CoreDocumentImpl(DocumentType doctype) {
    this(doctype, false);
  }
  
  public CoreDocumentImpl(DocumentType doctype, boolean grammarAccess) {
    this(grammarAccess);
    if (doctype != null) {
      DocumentTypeImpl doctypeImpl;
      try {
        doctypeImpl = (DocumentTypeImpl)doctype;
      } catch (ClassCastException e) {
        String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
        throw new DOMException((short)4, msg);
      } 
      doctypeImpl.ownerDocument = this;
      appendChild(doctype);
    } 
  }
  
  public final Document getOwnerDocument() {
    return null;
  }
  
  public short getNodeType() {
    return 9;
  }
  
  public String getNodeName() {
    return "#document";
  }
  
  public Node cloneNode(boolean deep) {
    CoreDocumentImpl newdoc = new CoreDocumentImpl();
    callUserDataHandlers(this, newdoc, (short)1);
    cloneNode(newdoc, deep);
    return newdoc;
  }
  
  protected void cloneNode(CoreDocumentImpl newdoc, boolean deep) {
    if (needsSyncChildren())
      synchronizeChildren(); 
    if (deep) {
      Hashtable<Object, Object> reversedIdentifiers = null;
      if (this.identifiers != null) {
        reversedIdentifiers = new Hashtable<>();
        Enumeration elementIds = this.identifiers.keys();
        while (elementIds.hasMoreElements()) {
          Object elementId = elementIds.nextElement();
          reversedIdentifiers.put(this.identifiers.get(elementId), elementId);
        } 
      } 
      for (ChildNode kid = this.firstChild; kid != null; 
        kid = kid.nextSibling)
        newdoc.appendChild(newdoc.importNode(kid, true, true, reversedIdentifiers)); 
    } 
    newdoc.allowGrammarAccess = this.allowGrammarAccess;
    newdoc.errorChecking = this.errorChecking;
  }
  
  public Node insertBefore(Node newChild, Node refChild) throws DOMException {
    int type = newChild.getNodeType();
    if (this.errorChecking && ((
      type == 1 && this.docElement != null) || (type == 10 && this.docType != null))) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
      throw new DOMException((short)3, msg);
    } 
    if (newChild.getOwnerDocument() == null && newChild instanceof DocumentTypeImpl)
      ((DocumentTypeImpl)newChild).ownerDocument = this; 
    super.insertBefore(newChild, refChild);
    if (type == 1) {
      this.docElement = (ElementImpl)newChild;
    } else if (type == 10) {
      this.docType = (DocumentTypeImpl)newChild;
    } 
    return newChild;
  }
  
  public Node removeChild(Node oldChild) throws DOMException {
    super.removeChild(oldChild);
    int type = oldChild.getNodeType();
    if (type == 1) {
      this.docElement = null;
    } else if (type == 10) {
      this.docType = null;
    } 
    return oldChild;
  }
  
  public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
    if (newChild.getOwnerDocument() == null && newChild instanceof DocumentTypeImpl)
      ((DocumentTypeImpl)newChild).ownerDocument = this; 
    if (this.errorChecking && ((this.docType != null && oldChild
      .getNodeType() != 10 && newChild
      .getNodeType() == 10) || (this.docElement != null && oldChild
      
      .getNodeType() != 1 && newChild
      .getNodeType() == 1)))
      throw new DOMException((short)3, 
          
          DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null)); 
    super.replaceChild(newChild, oldChild);
    int type = oldChild.getNodeType();
    if (type == 1) {
      this.docElement = (ElementImpl)newChild;
    } else if (type == 10) {
      this.docType = (DocumentTypeImpl)newChild;
    } 
    return oldChild;
  }
  
  public String getTextContent() throws DOMException {
    return null;
  }
  
  public void setTextContent(String textContent) throws DOMException {}
  
  public Object getFeature(String feature, String version) {
    boolean anyVersion = (version == null || version.length() == 0);
    if (feature.equalsIgnoreCase("+XPath") && (anyVersion || version
      .equals("3.0"))) {
      if (this.fXPathEvaluator != null)
        return this.fXPathEvaluator; 
      try {
        Class<?> xpathClass = ObjectFactory.findProviderClass("com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl", true);
        Constructor<?> xpathClassConstr = xpathClass.getConstructor(new Class[] { Document.class });
        Class[] interfaces = xpathClass.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
          if (interfaces[i].getName().equals("org.w3c.dom.xpath.XPathEvaluator")) {
            this.fXPathEvaluator = xpathClassConstr.newInstance(new Object[] { this });
            return this.fXPathEvaluator;
          } 
        } 
        return null;
      } catch (Exception e) {
        return null;
      } 
    } 
    return super.getFeature(feature, version);
  }
  
  public Attr createAttribute(String name) throws DOMException {
    if (this.errorChecking && !isXMLName(name, this.xml11Version)) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, msg);
    } 
    return new AttrImpl(this, name);
  }
  
  public CDATASection createCDATASection(String data) throws DOMException {
    return new CDATASectionImpl(this, data);
  }
  
  public Comment createComment(String data) {
    return new CommentImpl(this, data);
  }
  
  public DocumentFragment createDocumentFragment() {
    return new DocumentFragmentImpl(this);
  }
  
  public Element createElement(String tagName) throws DOMException {
    if (this.errorChecking && !isXMLName(tagName, this.xml11Version)) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, msg);
    } 
    return new ElementImpl(this, tagName);
  }
  
  public EntityReference createEntityReference(String name) throws DOMException {
    if (this.errorChecking && !isXMLName(name, this.xml11Version)) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, msg);
    } 
    return new EntityReferenceImpl(this, name);
  }
  
  public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
    if (this.errorChecking && !isXMLName(target, this.xml11Version)) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, msg);
    } 
    return new ProcessingInstructionImpl(this, target, data);
  }
  
  public Text createTextNode(String data) {
    return new TextImpl(this, data);
  }
  
  public DocumentType getDoctype() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return this.docType;
  }
  
  public Element getDocumentElement() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return this.docElement;
  }
  
  public NodeList getElementsByTagName(String tagname) {
    return new DeepNodeListImpl(this, tagname);
  }
  
  public DOMImplementation getImplementation() {
    return CoreDOMImplementationImpl.getDOMImplementation();
  }
  
  public void setErrorChecking(boolean check) {
    this.errorChecking = check;
  }
  
  public void setStrictErrorChecking(boolean check) {
    this.errorChecking = check;
  }
  
  public boolean getErrorChecking() {
    return this.errorChecking;
  }
  
  public boolean getStrictErrorChecking() {
    return this.errorChecking;
  }
  
  public String getInputEncoding() {
    return this.actualEncoding;
  }
  
  public void setInputEncoding(String value) {
    this.actualEncoding = value;
  }
  
  public void setXmlEncoding(String value) {
    this.encoding = value;
  }
  
  public void setEncoding(String value) {
    setXmlEncoding(value);
  }
  
  public String getXmlEncoding() {
    return this.encoding;
  }
  
  public String getEncoding() {
    return getXmlEncoding();
  }
  
  public void setXmlVersion(String value) {
    if (value.equals("1.0") || value.equals("1.1")) {
      if (!getXmlVersion().equals(value)) {
        this.xmlVersionChanged = true;
        isNormalized(false);
        this.version = value;
      } 
    } else {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, msg);
    } 
    if (getXmlVersion().equals("1.1")) {
      this.xml11Version = true;
    } else {
      this.xml11Version = false;
    } 
  }
  
  public void setVersion(String value) {
    setXmlVersion(value);
  }
  
  public String getXmlVersion() {
    return (this.version == null) ? "1.0" : this.version;
  }
  
  public String getVersion() {
    return getXmlVersion();
  }
  
  public void setXmlStandalone(boolean value) throws DOMException {
    this.standalone = value;
  }
  
  public void setStandalone(boolean value) {
    setXmlStandalone(value);
  }
  
  public boolean getXmlStandalone() {
    return this.standalone;
  }
  
  public boolean getStandalone() {
    return getXmlStandalone();
  }
  
  public String getDocumentURI() {
    return this.fDocumentURI;
  }
  
  public Node renameNode(Node n, String namespaceURI, String name) throws DOMException {
    ElementImpl el;
    AttrImpl at;
    Element element;
    if (this.errorChecking && n.getOwnerDocument() != this && n != this) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
      throw new DOMException((short)4, str);
    } 
    switch (n.getNodeType()) {
      case 1:
        el = (ElementImpl)n;
        if (el instanceof ElementNSImpl) {
          ((ElementNSImpl)el).rename(namespaceURI, name);
          callUserDataHandlers(el, (Node)null, (short)4);
        } else if (namespaceURI == null) {
          if (this.errorChecking) {
            int colon1 = name.indexOf(':');
            if (colon1 != -1) {
              String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
              throw new DOMException((short)14, str);
            } 
            if (!isXMLName(name, this.xml11Version)) {
              String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
              throw new DOMException((short)5, str);
            } 
          } 
          el.rename(name);
          callUserDataHandlers(el, (Node)null, (short)4);
        } else {
          ElementNSImpl nel = new ElementNSImpl(this, namespaceURI, name);
          copyEventListeners(el, nel);
          Hashtable data = removeUserDataTable(el);
          Node parent = el.getParentNode();
          Node nextSib = el.getNextSibling();
          if (parent != null)
            parent.removeChild(el); 
          Node child = el.getFirstChild();
          while (child != null) {
            el.removeChild(child);
            nel.appendChild(child);
            child = el.getFirstChild();
          } 
          nel.moveSpecifiedAttributes(el);
          setUserDataTable(nel, data);
          callUserDataHandlers(el, nel, (short)4);
          if (parent != null)
            parent.insertBefore(nel, nextSib); 
          el = nel;
        } 
        renamedElement((Element)n, el);
        return el;
      case 2:
        at = (AttrImpl)n;
        element = at.getOwnerElement();
        if (element != null)
          element.removeAttributeNode(at); 
        if (n instanceof AttrNSImpl) {
          ((AttrNSImpl)at).rename(namespaceURI, name);
          if (element != null)
            element.setAttributeNodeNS(at); 
          callUserDataHandlers(at, (Node)null, (short)4);
        } else if (namespaceURI == null) {
          at.rename(name);
          if (element != null)
            element.setAttributeNode(at); 
          callUserDataHandlers(at, (Node)null, (short)4);
        } else {
          AttrNSImpl nat = new AttrNSImpl(this, namespaceURI, name);
          copyEventListeners(at, nat);
          Hashtable data = removeUserDataTable(at);
          Node child = at.getFirstChild();
          while (child != null) {
            at.removeChild(child);
            nat.appendChild(child);
            child = at.getFirstChild();
          } 
          setUserDataTable(nat, data);
          callUserDataHandlers(at, nat, (short)4);
          if (element != null)
            element.setAttributeNode(nat); 
          at = nat;
        } 
        renamedAttrNode((Attr)n, at);
        return at;
    } 
    String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
    throw new DOMException((short)9, msg);
  }
  
  public void normalizeDocument() {
    if (isNormalized() && !isNormalizeDocRequired())
      return; 
    if (needsSyncChildren())
      synchronizeChildren(); 
    if (this.domNormalizer == null)
      this.domNormalizer = new DOMNormalizer(); 
    if (this.fConfiguration == null) {
      this.fConfiguration = new DOMConfigurationImpl();
    } else {
      this.fConfiguration.reset();
    } 
    this.domNormalizer.normalizeDocument(this, this.fConfiguration);
    isNormalized(true);
    this.xmlVersionChanged = false;
  }
  
  public DOMConfiguration getDomConfig() {
    if (this.fConfiguration == null)
      this.fConfiguration = new DOMConfigurationImpl(); 
    return this.fConfiguration;
  }
  
  public String getBaseURI() {
    if (this.fDocumentURI != null && this.fDocumentURI.length() != 0)
      try {
        return (new URI(this.fDocumentURI)).toString();
      } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException e) {
        return null;
      }  
    return this.fDocumentURI;
  }
  
  public void setDocumentURI(String documentURI) {
    this.fDocumentURI = documentURI;
  }
  
  public boolean getAsync() {
    return false;
  }
  
  public void setAsync(boolean async) {
    if (async) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, msg);
    } 
  }
  
  public void abort() {}
  
  public boolean load(String uri) {
    return false;
  }
  
  public boolean loadXML(String source) {
    return false;
  }
  
  public String saveXML(Node node) throws DOMException {
    if (this.errorChecking && node != null && this != node
      .getOwnerDocument()) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
      throw new DOMException((short)4, msg);
    } 
    DOMImplementationLS domImplLS = (DOMImplementationLS)DOMImplementationImpl.getDOMImplementation();
    LSSerializer xmlWriter = domImplLS.createLSSerializer();
    if (node == null)
      node = this; 
    return xmlWriter.writeToString(node);
  }
  
  void setMutationEvents(boolean set) {}
  
  boolean getMutationEvents() {
    return false;
  }
  
  public DocumentType createDocumentType(String qualifiedName, String publicID, String systemID) throws DOMException {
    return new DocumentTypeImpl(this, qualifiedName, publicID, systemID);
  }
  
  public Entity createEntity(String name) throws DOMException {
    if (this.errorChecking && !isXMLName(name, this.xml11Version)) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, msg);
    } 
    return new EntityImpl(this, name);
  }
  
  public Notation createNotation(String name) throws DOMException {
    if (this.errorChecking && !isXMLName(name, this.xml11Version)) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, msg);
    } 
    return new NotationImpl(this, name);
  }
  
  public ElementDefinitionImpl createElementDefinition(String name) throws DOMException {
    if (this.errorChecking && !isXMLName(name, this.xml11Version)) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, msg);
    } 
    return new ElementDefinitionImpl(this, name);
  }
  
  protected int getNodeNumber() {
    if (this.documentNumber == 0) {
      CoreDOMImplementationImpl cd = (CoreDOMImplementationImpl)CoreDOMImplementationImpl.getDOMImplementation();
      this.documentNumber = cd.assignDocumentNumber();
    } 
    return this.documentNumber;
  }
  
  protected int getNodeNumber(Node node) {
    int num;
    if (this.nodeTable == null) {
      this.nodeTable = new Hashtable<>();
      num = --this.nodeCounter;
      this.nodeTable.put(node, new Integer(num));
    } else {
      Integer n = (Integer)this.nodeTable.get(node);
      if (n == null) {
        num = --this.nodeCounter;
        this.nodeTable.put(node, new Integer(num));
      } else {
        num = n.intValue();
      } 
    } 
    return num;
  }
  
  public Node importNode(Node source, boolean deep) throws DOMException {
    return importNode(source, deep, false, (Hashtable)null);
  }
  
  private Node importNode(Node source, boolean deep, boolean cloningDoc, Hashtable reversedIdentifiers) throws DOMException {
    Element newElement;
    Entity srcentity;
    DocumentType srcdoctype;
    Notation srcnotation;
    String msg;
    boolean domLevel20;
    EntityImpl newentity;
    DocumentTypeImpl newdoctype;
    NotationImpl newnotation;
    NamedNodeMap sourceAttrs, smap, tmap;
    Node newnode = null;
    Hashtable userData = null;
    if (source instanceof NodeImpl)
      userData = ((NodeImpl)source).getUserDataRecord(); 
    int type = source.getNodeType();
    switch (type) {
      case 1:
        domLevel20 = source.getOwnerDocument().getImplementation().hasFeature("XML", "2.0");
        if (!domLevel20 || source.getLocalName() == null) {
          newElement = createElement(source.getNodeName());
        } else {
          newElement = createElementNS(source.getNamespaceURI(), source
              .getNodeName());
        } 
        sourceAttrs = source.getAttributes();
        if (sourceAttrs != null) {
          int length = sourceAttrs.getLength();
          for (int index = 0; index < length; index++) {
            Attr attr = (Attr)sourceAttrs.item(index);
            if (attr.getSpecified() || cloningDoc) {
              Attr newAttr = (Attr)importNode(attr, true, cloningDoc, reversedIdentifiers);
              if (!domLevel20 || attr
                .getLocalName() == null) {
                newElement.setAttributeNode(newAttr);
              } else {
                newElement.setAttributeNodeNS(newAttr);
              } 
            } 
          } 
        } 
        if (reversedIdentifiers != null) {
          Object elementId = reversedIdentifiers.get(source);
          if (elementId != null) {
            if (this.identifiers == null)
              this.identifiers = new Hashtable<>(); 
            this.identifiers.put(elementId, newElement);
          } 
        } 
        newnode = newElement;
        break;
      case 2:
        if (source.getOwnerDocument().getImplementation().hasFeature("XML", "2.0")) {
          if (source.getLocalName() == null) {
            newnode = createAttribute(source.getNodeName());
          } else {
            newnode = createAttributeNS(source.getNamespaceURI(), source
                .getNodeName());
          } 
        } else {
          newnode = createAttribute(source.getNodeName());
        } 
        if (source instanceof AttrImpl) {
          AttrImpl attr = (AttrImpl)source;
          if (attr.hasStringValue()) {
            AttrImpl newattr = (AttrImpl)newnode;
            newattr.setValue(attr.getValue());
            deep = false;
            break;
          } 
          deep = true;
          break;
        } 
        if (source.getFirstChild() == null) {
          newnode.setNodeValue(source.getNodeValue());
          deep = false;
          break;
        } 
        deep = true;
        break;
      case 3:
        newnode = createTextNode(source.getNodeValue());
        break;
      case 4:
        newnode = createCDATASection(source.getNodeValue());
        break;
      case 5:
        newnode = createEntityReference(source.getNodeName());
        deep = false;
        break;
      case 6:
        srcentity = (Entity)source;
        newentity = (EntityImpl)createEntity(source.getNodeName());
        newentity.setPublicId(srcentity.getPublicId());
        newentity.setSystemId(srcentity.getSystemId());
        newentity.setNotationName(srcentity.getNotationName());
        newentity.isReadOnly(false);
        newnode = newentity;
        break;
      case 7:
        newnode = createProcessingInstruction(source.getNodeName(), source
            .getNodeValue());
        break;
      case 8:
        newnode = createComment(source.getNodeValue());
        break;
      case 10:
        if (!cloningDoc) {
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
          throw new DOMException((short)9, str);
        } 
        srcdoctype = (DocumentType)source;
        newdoctype = (DocumentTypeImpl)createDocumentType(srcdoctype.getNodeName(), srcdoctype
            .getPublicId(), srcdoctype
            .getSystemId());
        smap = srcdoctype.getEntities();
        tmap = newdoctype.getEntities();
        if (smap != null)
          for (int i = 0; i < smap.getLength(); i++)
            tmap.setNamedItem(importNode(smap.item(i), true, true, reversedIdentifiers));  
        smap = srcdoctype.getNotations();
        tmap = newdoctype.getNotations();
        if (smap != null)
          for (int i = 0; i < smap.getLength(); i++)
            tmap.setNamedItem(importNode(smap.item(i), true, true, reversedIdentifiers));  
        newnode = newdoctype;
        break;
      case 11:
        newnode = createDocumentFragment();
        break;
      case 12:
        srcnotation = (Notation)source;
        newnotation = (NotationImpl)createNotation(source.getNodeName());
        newnotation.setPublicId(srcnotation.getPublicId());
        newnotation.setSystemId(srcnotation.getSystemId());
        newnode = newnotation;
        break;
      default:
        msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
        throw new DOMException((short)9, msg);
    } 
    if (userData != null)
      callUserDataHandlers(source, newnode, (short)2, userData); 
    if (deep) {
      Node srckid = source.getFirstChild();
      for (; srckid != null; 
        srckid = srckid.getNextSibling())
        newnode.appendChild(importNode(srckid, true, cloningDoc, reversedIdentifiers)); 
    } 
    if (newnode.getNodeType() == 6)
      ((NodeImpl)newnode).setReadOnly(true, true); 
    return newnode;
  }
  
  public Node adoptNode(Node source) {
    NodeImpl node;
    AttrImpl attr;
    String msg;
    Node parent, child;
    NamedNodeMap entities;
    Node entityNode;
    Hashtable userData = null;
    try {
      node = (NodeImpl)source;
    } catch (ClassCastException e) {
      return null;
    } 
    if (source == null)
      return null; 
    if (source != null && source.getOwnerDocument() != null) {
      DOMImplementation thisImpl = getImplementation();
      DOMImplementation otherImpl = source.getOwnerDocument().getImplementation();
      if (thisImpl != otherImpl)
        if (thisImpl instanceof DOMImplementationImpl && otherImpl instanceof DeferredDOMImplementationImpl) {
          undeferChildren(node);
        } else if (!(thisImpl instanceof DeferredDOMImplementationImpl) || !(otherImpl instanceof DOMImplementationImpl)) {
          return null;
        }  
    } 
    switch (node.getNodeType()) {
      case 2:
        attr = (AttrImpl)node;
        if (attr.getOwnerElement() != null)
          attr.getOwnerElement().removeAttributeNode(attr); 
        attr.isSpecified(true);
        userData = node.getUserDataRecord();
        attr.setOwnerDocument(this);
        if (userData != null)
          setUserDataTable(node, userData); 
        break;
      case 6:
      case 12:
        msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, msg);
      case 9:
      case 10:
        msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
        throw new DOMException((short)9, msg);
      case 5:
        userData = node.getUserDataRecord();
        parent = node.getParentNode();
        if (parent != null)
          parent.removeChild(source); 
        while ((child = node.getFirstChild()) != null)
          node.removeChild(child); 
        node.setOwnerDocument(this);
        if (userData != null)
          setUserDataTable(node, userData); 
        if (this.docType == null)
          break; 
        entities = this.docType.getEntities();
        entityNode = entities.getNamedItem(node.getNodeName());
        if (entityNode == null)
          break; 
        child = entityNode.getFirstChild();
        for (; child != null; child = child.getNextSibling()) {
          Node childClone = child.cloneNode(true);
          node.appendChild(childClone);
        } 
        break;
      case 1:
        userData = node.getUserDataRecord();
        parent = node.getParentNode();
        if (parent != null)
          parent.removeChild(source); 
        node.setOwnerDocument(this);
        if (userData != null)
          setUserDataTable(node, userData); 
        ((ElementImpl)node).reconcileDefaultAttributes();
        break;
      default:
        userData = node.getUserDataRecord();
        parent = node.getParentNode();
        if (parent != null)
          parent.removeChild(source); 
        node.setOwnerDocument(this);
        if (userData != null)
          setUserDataTable(node, userData); 
        break;
    } 
    if (userData != null)
      callUserDataHandlers(source, (Node)null, (short)5, userData); 
    return node;
  }
  
  protected void undeferChildren(Node node) {
    Node top = node;
    while (null != node) {
      if (((NodeImpl)node).needsSyncData())
        ((NodeImpl)node).synchronizeData(); 
      NamedNodeMap attributes = node.getAttributes();
      if (attributes != null) {
        int length = attributes.getLength();
        for (int i = 0; i < length; i++)
          undeferChildren(attributes.item(i)); 
      } 
      Node nextNode = null;
      nextNode = node.getFirstChild();
      while (null == nextNode) {
        if (top.equals(node))
          break; 
        nextNode = node.getNextSibling();
        if (null == nextNode) {
          node = node.getParentNode();
          if (null == node || top.equals(node)) {
            nextNode = null;
            break;
          } 
        } 
      } 
      node = nextNode;
    } 
  }
  
  public Element getElementById(String elementId) {
    return getIdentifier(elementId);
  }
  
  protected final void clearIdentifiers() {
    if (this.identifiers != null)
      this.identifiers.clear(); 
  }
  
  public void putIdentifier(String idName, Element element) {
    if (element == null) {
      removeIdentifier(idName);
      return;
    } 
    if (needsSyncData())
      synchronizeData(); 
    if (this.identifiers == null)
      this.identifiers = new Hashtable<>(); 
    this.identifiers.put(idName, element);
  }
  
  public Element getIdentifier(String idName) {
    if (needsSyncData())
      synchronizeData(); 
    if (this.identifiers == null)
      return null; 
    Element elem = (Element)this.identifiers.get(idName);
    if (elem != null) {
      Node parent = elem.getParentNode();
      while (parent != null) {
        if (parent == this)
          return elem; 
        parent = parent.getParentNode();
      } 
    } 
    return null;
  }
  
  public void removeIdentifier(String idName) {
    if (needsSyncData())
      synchronizeData(); 
    if (this.identifiers == null)
      return; 
    this.identifiers.remove(idName);
  }
  
  public Enumeration getIdentifiers() {
    if (needsSyncData())
      synchronizeData(); 
    if (this.identifiers == null)
      this.identifiers = new Hashtable<>(); 
    return this.identifiers.keys();
  }
  
  public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
    return new ElementNSImpl(this, namespaceURI, qualifiedName);
  }
  
  public Element createElementNS(String namespaceURI, String qualifiedName, String localpart) throws DOMException {
    return new ElementNSImpl(this, namespaceURI, qualifiedName, localpart);
  }
  
  public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
    return new AttrNSImpl(this, namespaceURI, qualifiedName);
  }
  
  public Attr createAttributeNS(String namespaceURI, String qualifiedName, String localpart) throws DOMException {
    return new AttrNSImpl(this, namespaceURI, qualifiedName, localpart);
  }
  
  public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
    return new DeepNodeListImpl(this, namespaceURI, localName);
  }
  
  public Object clone() throws CloneNotSupportedException {
    CoreDocumentImpl newdoc = (CoreDocumentImpl)super.clone();
    newdoc.docType = null;
    newdoc.docElement = null;
    return newdoc;
  }
  
  public static final boolean isXMLName(String s, boolean xml11Version) {
    if (s == null)
      return false; 
    if (!xml11Version)
      return XMLChar.isValidName(s); 
    return XML11Char.isXML11ValidName(s);
  }
  
  public static final boolean isValidQName(String prefix, String local, boolean xml11Version) {
    if (local == null)
      return false; 
    boolean validNCName = false;
    if (!xml11Version) {
      validNCName = ((prefix == null || XMLChar.isValidNCName(prefix)) && XMLChar.isValidNCName(local));
    } else {
      validNCName = ((prefix == null || XML11Char.isXML11ValidNCName(prefix)) && XML11Char.isXML11ValidNCName(local));
    } 
    return validNCName;
  }
  
  protected boolean isKidOK(Node parent, Node child) {
    if (this.allowGrammarAccess && parent
      .getNodeType() == 10)
      return (child.getNodeType() == 1); 
    return (0 != (kidOK[parent.getNodeType()] & 1 << child.getNodeType()));
  }
  
  protected void changed() {
    this.changes++;
  }
  
  protected int changes() {
    return this.changes;
  }
  
  NodeListCache getNodeListCache(ParentNode owner) {
    if (this.fFreeNLCache == null)
      return new NodeListCache(owner); 
    NodeListCache c = this.fFreeNLCache;
    this.fFreeNLCache = this.fFreeNLCache.next;
    c.fChild = null;
    c.fChildIndex = -1;
    c.fLength = -1;
    if (c.fOwner != null)
      c.fOwner.fNodeListCache = null; 
    c.fOwner = owner;
    return c;
  }
  
  void freeNodeListCache(NodeListCache c) {
    c.next = this.fFreeNLCache;
    this.fFreeNLCache = c;
  }
  
  public Object setUserData(Node n, String key, Object data, UserDataHandler handler) {
    Hashtable<Object, Object> t;
    if (data == null) {
      if (this.userData != null) {
        t = (Hashtable)this.userData.get(n);
        if (t != null) {
          Object object = t.remove(key);
          if (object != null) {
            ParentNode.UserDataRecord r = (ParentNode.UserDataRecord)object;
            return r.fData;
          } 
        } 
      } 
      return null;
    } 
    if (this.userData == null) {
      this.userData = new Hashtable<>();
      t = new Hashtable<>();
      this.userData.put(n, t);
    } else {
      t = (Hashtable)this.userData.get(n);
      if (t == null) {
        t = new Hashtable<>();
        this.userData.put(n, t);
      } 
    } 
    Object o = t.put(key, new ParentNode.UserDataRecord(this, data, handler));
    if (o != null) {
      ParentNode.UserDataRecord r = (ParentNode.UserDataRecord)o;
      return r.fData;
    } 
    return null;
  }
  
  public Object getUserData(Node n, String key) {
    if (this.userData == null)
      return null; 
    Hashtable t = (Hashtable)this.userData.get(n);
    if (t == null)
      return null; 
    Object o = t.get(key);
    if (o != null) {
      ParentNode.UserDataRecord r = (ParentNode.UserDataRecord)o;
      return r.fData;
    } 
    return null;
  }
  
  protected Hashtable getUserDataRecord(Node n) {
    if (this.userData == null)
      return null; 
    Hashtable t = (Hashtable)this.userData.get(n);
    if (t == null)
      return null; 
    return t;
  }
  
  Hashtable removeUserDataTable(Node n) {
    if (this.userData == null)
      return null; 
    return (Hashtable)this.userData.get(n);
  }
  
  void setUserDataTable(Node n, Hashtable data) {
    if (this.userData == null)
      this.userData = new Hashtable<>(); 
    if (data != null)
      this.userData.put(n, data); 
  }
  
  void callUserDataHandlers(Node n, Node c, short operation) {
    if (this.userData == null)
      return; 
    if (n instanceof NodeImpl) {
      Hashtable t = ((NodeImpl)n).getUserDataRecord();
      if (t == null || t.isEmpty())
        return; 
      callUserDataHandlers(n, c, operation, t);
    } 
  }
  
  void callUserDataHandlers(Node n, Node c, short operation, Hashtable userData) {
    if (userData == null || userData.isEmpty())
      return; 
    Enumeration<String> keys = userData.keys();
    while (keys.hasMoreElements()) {
      String key = keys.nextElement();
      ParentNode.UserDataRecord r = (ParentNode.UserDataRecord)userData.get(key);
      if (r.fHandler != null)
        r.fHandler.handle(operation, key, r.fData, n, c); 
    } 
  }
  
  protected final void checkNamespaceWF(String qname, int colon1, int colon2) {
    if (!this.errorChecking)
      return; 
    if (colon1 == 0 || colon1 == qname.length() - 1 || colon2 != colon1) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
      throw new DOMException((short)14, msg);
    } 
  }
  
  protected final void checkDOMNSErr(String prefix, String namespace) {
    if (this.errorChecking) {
      if (namespace == null) {
        String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
        throw new DOMException((short)14, msg);
      } 
      if (prefix.equals("xml") && 
        !namespace.equals(NamespaceContext.XML_URI)) {
        String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
        throw new DOMException((short)14, msg);
      } 
      if ((prefix
        .equals("xmlns") && 
        !namespace.equals(NamespaceContext.XMLNS_URI)) || (
        !prefix.equals("xmlns") && namespace
        .equals(NamespaceContext.XMLNS_URI))) {
        String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
        throw new DOMException((short)14, msg);
      } 
    } 
  }
  
  protected final void checkQName(String prefix, String local) {
    if (!this.errorChecking)
      return; 
    boolean validNCName = false;
    if (!this.xml11Version) {
      validNCName = ((prefix == null || XMLChar.isValidNCName(prefix)) && XMLChar.isValidNCName(local));
    } else {
      validNCName = ((prefix == null || XML11Char.isXML11ValidNCName(prefix)) && XML11Char.isXML11ValidNCName(local));
    } 
    if (!validNCName) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, msg);
    } 
  }
  
  boolean isXML11Version() {
    return this.xml11Version;
  }
  
  boolean isNormalizeDocRequired() {
    return true;
  }
  
  boolean isXMLVersionChanged() {
    return this.xmlVersionChanged;
  }
  
  protected void setUserData(NodeImpl n, Object data) {
    setUserData(n, "XERCES1DOMUSERDATA", data, (UserDataHandler)null);
  }
  
  protected Object getUserData(NodeImpl n) {
    return getUserData(n, "XERCES1DOMUSERDATA");
  }
  
  protected void addEventListener(NodeImpl node, String type, EventListener listener, boolean useCapture) {}
  
  protected void removeEventListener(NodeImpl node, String type, EventListener listener, boolean useCapture) {}
  
  protected void copyEventListeners(NodeImpl src, NodeImpl tgt) {}
  
  protected boolean dispatchEvent(NodeImpl node, Event event) {
    return false;
  }
  
  void replacedText(NodeImpl node) {}
  
  void deletedText(NodeImpl node, int offset, int count) {}
  
  void insertedText(NodeImpl node, int offset, int count) {}
  
  void modifyingCharacterData(NodeImpl node, boolean replace) {}
  
  void modifiedCharacterData(NodeImpl node, String oldvalue, String value, boolean replace) {}
  
  void insertingNode(NodeImpl node, boolean replace) {}
  
  void insertedNode(NodeImpl node, NodeImpl newInternal, boolean replace) {}
  
  void removingNode(NodeImpl node, NodeImpl oldChild, boolean replace) {}
  
  void removedNode(NodeImpl node, boolean replace) {}
  
  void replacingNode(NodeImpl node) {}
  
  void replacedNode(NodeImpl node) {}
  
  void replacingData(NodeImpl node) {}
  
  void replacedCharacterData(NodeImpl node, String oldvalue, String value) {}
  
  void modifiedAttrValue(AttrImpl attr, String oldvalue) {}
  
  void setAttrNode(AttrImpl attr, AttrImpl previous) {}
  
  void removedAttrNode(AttrImpl attr, NodeImpl oldOwner, String name) {}
  
  void renamedAttrNode(Attr oldAt, Attr newAt) {}
  
  void renamedElement(Element oldEl, Element newEl) {}
}
