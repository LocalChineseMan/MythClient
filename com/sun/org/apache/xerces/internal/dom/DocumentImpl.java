package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.dom.events.EventImpl;
import com.sun.org.apache.xerces.internal.dom.events.MutationEventImpl;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.ranges.Range;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.TreeWalker;

public class DocumentImpl extends CoreDocumentImpl implements DocumentTraversal, DocumentEvent, DocumentRange {
  static final long serialVersionUID = 515687835542616694L;
  
  protected Vector iterators;
  
  protected Vector ranges;
  
  protected Hashtable eventListeners;
  
  protected boolean mutationEvents = false;
  
  EnclosingAttr savedEnclosingAttr;
  
  public DocumentImpl() {}
  
  public DocumentImpl(boolean grammarAccess) {
    super(grammarAccess);
  }
  
  public DocumentImpl(DocumentType doctype) {
    super(doctype);
  }
  
  public DocumentImpl(DocumentType doctype, boolean grammarAccess) {
    super(doctype, grammarAccess);
  }
  
  public Node cloneNode(boolean deep) {
    DocumentImpl newdoc = new DocumentImpl();
    callUserDataHandlers((Node)this, (Node)newdoc, (short)1);
    cloneNode((CoreDocumentImpl)newdoc, deep);
    newdoc.mutationEvents = this.mutationEvents;
    return (Node)newdoc;
  }
  
  public DOMImplementation getImplementation() {
    return DOMImplementationImpl.getDOMImplementation();
  }
  
  public NodeIterator createNodeIterator(Node root, short whatToShow, NodeFilter filter) {
    return createNodeIterator(root, whatToShow, filter, true);
  }
  
  public NodeIterator createNodeIterator(Node root, int whatToShow, NodeFilter filter, boolean entityReferenceExpansion) {
    if (root == null) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, msg);
    } 
    NodeIterator iterator = new NodeIteratorImpl(this, root, whatToShow, filter, entityReferenceExpansion);
    if (this.iterators == null)
      this.iterators = new Vector(); 
    this.iterators.addElement(iterator);
    return iterator;
  }
  
  public TreeWalker createTreeWalker(Node root, short whatToShow, NodeFilter filter) {
    return createTreeWalker(root, whatToShow, filter, true);
  }
  
  public TreeWalker createTreeWalker(Node root, int whatToShow, NodeFilter filter, boolean entityReferenceExpansion) {
    if (root == null) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, msg);
    } 
    return new TreeWalkerImpl(root, whatToShow, filter, entityReferenceExpansion);
  }
  
  void removeNodeIterator(NodeIterator nodeIterator) {
    if (nodeIterator == null)
      return; 
    if (this.iterators == null)
      return; 
    this.iterators.removeElement(nodeIterator);
  }
  
  public Range createRange() {
    if (this.ranges == null)
      this.ranges = new Vector(); 
    Range range = new RangeImpl(this);
    this.ranges.addElement(range);
    return range;
  }
  
  void removeRange(Range range) {
    if (range == null)
      return; 
    if (this.ranges == null)
      return; 
    this.ranges.removeElement(range);
  }
  
  void replacedText(NodeImpl node) {
    if (this.ranges != null) {
      int size = this.ranges.size();
      for (int i = 0; i != size; i++)
        ((RangeImpl)this.ranges.elementAt(i)).receiveReplacedText(node); 
    } 
  }
  
  void deletedText(NodeImpl node, int offset, int count) {
    if (this.ranges != null) {
      int size = this.ranges.size();
      for (int i = 0; i != size; i++)
        ((RangeImpl)this.ranges.elementAt(i)).receiveDeletedText(node, offset, count); 
    } 
  }
  
  void insertedText(NodeImpl node, int offset, int count) {
    if (this.ranges != null) {
      int size = this.ranges.size();
      for (int i = 0; i != size; i++)
        ((RangeImpl)this.ranges.elementAt(i)).receiveInsertedText(node, offset, count); 
    } 
  }
  
  void splitData(Node node, Node newNode, int offset) {
    if (this.ranges != null) {
      int size = this.ranges.size();
      for (int i = 0; i != size; i++)
        ((RangeImpl)this.ranges.elementAt(i)).receiveSplitData(node, newNode, offset); 
    } 
  }
  
  public Event createEvent(String type) throws DOMException {
    if (type.equalsIgnoreCase("Events") || "Event".equals(type))
      return new EventImpl(); 
    if (type.equalsIgnoreCase("MutationEvents") || "MutationEvent"
      .equals(type))
      return new MutationEventImpl(); 
    String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
    throw new DOMException((short)9, msg);
  }
  
  void setMutationEvents(boolean set) {
    this.mutationEvents = set;
  }
  
  boolean getMutationEvents() {
    return this.mutationEvents;
  }
  
  protected void setEventListeners(NodeImpl n, Vector listeners) {
    if (this.eventListeners == null)
      this.eventListeners = new Hashtable<>(); 
    if (listeners == null) {
      this.eventListeners.remove(n);
      if (this.eventListeners.isEmpty())
        this.mutationEvents = false; 
    } else {
      this.eventListeners.put(n, listeners);
      this.mutationEvents = true;
    } 
  }
  
  protected Vector getEventListeners(NodeImpl n) {
    if (this.eventListeners == null)
      return null; 
    return (Vector)this.eventListeners.get(n);
  }
  
  protected void addEventListener(NodeImpl node, String type, EventListener listener, boolean useCapture) {
    if (type == null || type.equals("") || listener == null)
      return; 
    removeEventListener(node, type, listener, useCapture);
    Vector<LEntry> nodeListeners = getEventListeners(node);
    if (nodeListeners == null) {
      nodeListeners = new Vector();
      setEventListeners(node, nodeListeners);
    } 
    nodeListeners.addElement(new LEntry(this, type, listener, useCapture));
    LCount lc = LCount.lookup(type);
    if (useCapture) {
      lc.captures++;
      lc.total++;
    } else {
      lc.bubbles++;
      lc.total++;
    } 
  }
  
  protected void removeEventListener(NodeImpl node, String type, EventListener listener, boolean useCapture) {
    if (type == null || type.equals("") || listener == null)
      return; 
    Vector<LEntry> nodeListeners = getEventListeners(node);
    if (nodeListeners == null)
      return; 
    for (int i = nodeListeners.size() - 1; i >= 0; i--) {
      LEntry le = nodeListeners.elementAt(i);
      if (le.useCapture == useCapture && le.listener == listener && le.type
        .equals(type)) {
        nodeListeners.removeElementAt(i);
        if (nodeListeners.size() == 0)
          setEventListeners(node, (Vector)null); 
        LCount lc = LCount.lookup(type);
        if (useCapture) {
          lc.captures--;
          lc.total--;
          break;
        } 
        lc.bubbles--;
        lc.total--;
        break;
      } 
    } 
  }
  
  protected void copyEventListeners(NodeImpl src, NodeImpl tgt) {
    Vector nodeListeners = getEventListeners(src);
    if (nodeListeners == null)
      return; 
    setEventListeners(tgt, (Vector)nodeListeners.clone());
  }
  
  protected boolean dispatchEvent(NodeImpl node, Event event) {
    if (event == null)
      return false; 
    EventImpl evt = (EventImpl)event;
    if (!evt.initialized || evt.type == null || evt.type.equals("")) {
      String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "UNSPECIFIED_EVENT_TYPE_ERR", null);
      throw new EventException((short)0, msg);
    } 
    LCount lc = LCount.lookup(evt.getType());
    if (lc.total == 0)
      return evt.preventDefault; 
    evt.target = node;
    evt.stopPropagation = false;
    evt.preventDefault = false;
    Vector<Node> pv = new Vector(10, 10);
    Node p = node;
    Node n = p.getParentNode();
    while (n != null) {
      pv.addElement(n);
      p = n;
      n = n.getParentNode();
    } 
    if (lc.captures > 0) {
      evt.eventPhase = 1;
      for (int j = pv.size() - 1; j >= 0 && 
        !evt.stopPropagation; j--) {
        NodeImpl nn = (NodeImpl)pv.elementAt(j);
        evt.currentTarget = nn;
        Vector nodeListeners = getEventListeners(nn);
        if (nodeListeners != null) {
          Vector<LEntry> nl = (Vector)nodeListeners.clone();
          int nlsize = nl.size();
          for (int i = 0; i < nlsize; i++) {
            LEntry le = nl.elementAt(i);
            if (le.useCapture && le.type.equals(evt.type) && nodeListeners
              .contains(le))
              try {
                le.listener.handleEvent(evt);
              } catch (Exception exception) {} 
          } 
        } 
      } 
    } 
    if (lc.bubbles > 0) {
      evt.eventPhase = 2;
      evt.currentTarget = node;
      Vector nodeListeners = getEventListeners(node);
      if (!evt.stopPropagation && nodeListeners != null) {
        Vector<LEntry> nl = (Vector)nodeListeners.clone();
        int nlsize = nl.size();
        for (int i = 0; i < nlsize; i++) {
          LEntry le = nl.elementAt(i);
          if (!le.useCapture && le.type.equals(evt.type) && nodeListeners
            .contains(le))
            try {
              le.listener.handleEvent(evt);
            } catch (Exception exception) {} 
        } 
      } 
      if (evt.bubbles) {
        evt.eventPhase = 3;
        int pvsize = pv.size();
        for (int j = 0; j < pvsize && 
          !evt.stopPropagation; j++) {
          NodeImpl nn = (NodeImpl)pv.elementAt(j);
          evt.currentTarget = nn;
          nodeListeners = getEventListeners(nn);
          if (nodeListeners != null) {
            Vector<LEntry> nl = (Vector)nodeListeners.clone();
            int nlsize = nl.size();
            for (int i = 0; i < nlsize; i++) {
              LEntry le = nl.elementAt(i);
              if (!le.useCapture && le.type.equals(evt.type) && nodeListeners
                .contains(le))
                try {
                  le.listener.handleEvent(evt);
                } catch (Exception exception) {} 
            } 
          } 
        } 
      } 
    } 
    if (lc.defaults <= 0 || !evt.cancelable || !evt.preventDefault);
    return evt.preventDefault;
  }
  
  protected void dispatchEventToSubtree(Node n, Event e) {
    ((NodeImpl)n).dispatchEvent(e);
    if (n.getNodeType() == 1) {
      NamedNodeMap a = n.getAttributes();
      for (int i = a.getLength() - 1; i >= 0; i--)
        dispatchingEventToSubtree(a.item(i), e); 
    } 
    dispatchingEventToSubtree(n.getFirstChild(), e);
  }
  
  protected void dispatchingEventToSubtree(Node n, Event e) {
    if (n == null)
      return; 
    ((NodeImpl)n).dispatchEvent(e);
    if (n.getNodeType() == 1) {
      NamedNodeMap a = n.getAttributes();
      for (int i = a.getLength() - 1; i >= 0; i--)
        dispatchingEventToSubtree(a.item(i), e); 
    } 
    dispatchingEventToSubtree(n.getFirstChild(), e);
    dispatchingEventToSubtree(n.getNextSibling(), e);
  }
  
  protected void dispatchAggregateEvents(NodeImpl node, EnclosingAttr ea) {
    if (ea != null) {
      dispatchAggregateEvents(node, ea.node, ea.oldvalue, (short)1);
    } else {
      dispatchAggregateEvents(node, (AttrImpl)null, (String)null, (short)0);
    } 
  }
  
  protected void dispatchAggregateEvents(NodeImpl node, AttrImpl enclosingAttr, String oldvalue, short change) {
    NodeImpl owner = null;
    if (enclosingAttr != null) {
      LCount lCount = LCount.lookup("DOMAttrModified");
      owner = (NodeImpl)enclosingAttr.getOwnerElement();
      if (lCount.total > 0 && 
        owner != null) {
        MutationEventImpl me = new MutationEventImpl();
        me.initMutationEvent("DOMAttrModified", true, false, enclosingAttr, oldvalue, enclosingAttr
            
            .getNodeValue(), enclosingAttr
            .getNodeName(), change);
        owner.dispatchEvent(me);
      } 
    } 
    LCount lc = LCount.lookup("DOMSubtreeModified");
    if (lc.total > 0) {
      MutationEvent me = new MutationEventImpl();
      me.initMutationEvent("DOMSubtreeModified", true, false, null, null, null, null, (short)0);
      if (enclosingAttr != null) {
        dispatchEvent(enclosingAttr, me);
        if (owner != null)
          dispatchEvent(owner, me); 
      } else {
        dispatchEvent(node, me);
      } 
    } 
  }
  
  protected void saveEnclosingAttr(NodeImpl node) {
    this.savedEnclosingAttr = null;
    LCount lc = LCount.lookup("DOMAttrModified");
    if (lc.total > 0) {
      NodeImpl eventAncestor = node;
      while (true) {
        if (eventAncestor == null)
          return; 
        int type = eventAncestor.getNodeType();
        if (type == 2) {
          EnclosingAttr retval = new EnclosingAttr(this);
          retval.node = (AttrImpl)eventAncestor;
          retval.oldvalue = retval.node.getNodeValue();
          this.savedEnclosingAttr = retval;
          return;
        } 
        if (type == 5) {
          eventAncestor = eventAncestor.parentNode();
          continue;
        } 
        if (type == 3) {
          eventAncestor = eventAncestor.parentNode();
          continue;
        } 
        break;
      } 
      return;
    } 
  }
  
  void modifyingCharacterData(NodeImpl node, boolean replace) {
    if (this.mutationEvents && 
      !replace)
      saveEnclosingAttr(node); 
  }
  
  void modifiedCharacterData(NodeImpl node, String oldvalue, String value, boolean replace) {
    if (this.mutationEvents && 
      !replace) {
      LCount lc = LCount.lookup("DOMCharacterDataModified");
      if (lc.total > 0) {
        MutationEvent me = new MutationEventImpl();
        me.initMutationEvent("DOMCharacterDataModified", true, false, null, oldvalue, value, null, (short)0);
        dispatchEvent(node, me);
      } 
      dispatchAggregateEvents(node, this.savedEnclosingAttr);
    } 
  }
  
  void replacedCharacterData(NodeImpl node, String oldvalue, String value) {
    modifiedCharacterData(node, oldvalue, value, false);
  }
  
  void insertingNode(NodeImpl node, boolean replace) {
    if (this.mutationEvents && 
      !replace)
      saveEnclosingAttr(node); 
  }
  
  void insertedNode(NodeImpl node, NodeImpl newInternal, boolean replace) {
    if (this.mutationEvents) {
      LCount lc = LCount.lookup("DOMNodeInserted");
      if (lc.total > 0) {
        MutationEventImpl me = new MutationEventImpl();
        me.initMutationEvent("DOMNodeInserted", true, false, node, null, null, null, (short)0);
        dispatchEvent(newInternal, me);
      } 
      lc = LCount.lookup("DOMNodeInsertedIntoDocument");
      if (lc.total > 0) {
        NodeImpl eventAncestor = node;
        if (this.savedEnclosingAttr != null)
          eventAncestor = (NodeImpl)this.savedEnclosingAttr.node.getOwnerElement(); 
        if (eventAncestor != null) {
          NodeImpl p = eventAncestor;
          while (p != null) {
            eventAncestor = p;
            if (p.getNodeType() == 2) {
              p = (NodeImpl)((AttrImpl)p).getOwnerElement();
              continue;
            } 
            p = p.parentNode();
          } 
          if (eventAncestor.getNodeType() == 9) {
            MutationEventImpl me = new MutationEventImpl();
            me.initMutationEvent("DOMNodeInsertedIntoDocument", false, false, null, null, null, null, (short)0);
            dispatchEventToSubtree(newInternal, me);
          } 
        } 
      } 
      if (!replace)
        dispatchAggregateEvents(node, this.savedEnclosingAttr); 
    } 
    if (this.ranges != null) {
      int size = this.ranges.size();
      for (int i = 0; i != size; i++)
        ((RangeImpl)this.ranges.elementAt(i)).insertedNodeFromDOM(newInternal); 
    } 
  }
  
  void removingNode(NodeImpl node, NodeImpl oldChild, boolean replace) {
    if (this.iterators != null) {
      int size = this.iterators.size();
      for (int i = 0; i != size; i++)
        ((NodeIteratorImpl)this.iterators.elementAt(i)).removeNode(oldChild); 
    } 
    if (this.ranges != null) {
      int size = this.ranges.size();
      for (int i = 0; i != size; i++)
        ((RangeImpl)this.ranges.elementAt(i)).removeNode(oldChild); 
    } 
    if (this.mutationEvents) {
      if (!replace)
        saveEnclosingAttr(node); 
      LCount lc = LCount.lookup("DOMNodeRemoved");
      if (lc.total > 0) {
        MutationEventImpl me = new MutationEventImpl();
        me.initMutationEvent("DOMNodeRemoved", true, false, node, null, null, null, (short)0);
        dispatchEvent(oldChild, me);
      } 
      lc = LCount.lookup("DOMNodeRemovedFromDocument");
      if (lc.total > 0) {
        NodeImpl eventAncestor = (NodeImpl)this;
        if (this.savedEnclosingAttr != null)
          eventAncestor = (NodeImpl)this.savedEnclosingAttr.node.getOwnerElement(); 
        if (eventAncestor != null) {
          NodeImpl p = eventAncestor.parentNode();
          for (; p != null; p = p.parentNode())
            eventAncestor = p; 
          if (eventAncestor.getNodeType() == 9) {
            MutationEventImpl me = new MutationEventImpl();
            me.initMutationEvent("DOMNodeRemovedFromDocument", false, false, null, null, null, null, (short)0);
            dispatchEventToSubtree(oldChild, me);
          } 
        } 
      } 
    } 
  }
  
  void removedNode(NodeImpl node, boolean replace) {
    if (this.mutationEvents)
      if (!replace)
        dispatchAggregateEvents(node, this.savedEnclosingAttr);  
  }
  
  void replacingNode(NodeImpl node) {
    if (this.mutationEvents)
      saveEnclosingAttr(node); 
  }
  
  void replacingData(NodeImpl node) {
    if (this.mutationEvents)
      saveEnclosingAttr(node); 
  }
  
  void replacedNode(NodeImpl node) {
    if (this.mutationEvents)
      dispatchAggregateEvents(node, this.savedEnclosingAttr); 
  }
  
  void modifiedAttrValue(AttrImpl attr, String oldvalue) {
    if (this.mutationEvents)
      dispatchAggregateEvents(attr, attr, oldvalue, (short)1); 
  }
  
  void setAttrNode(AttrImpl attr, AttrImpl previous) {
    if (this.mutationEvents)
      if (previous == null) {
        dispatchAggregateEvents(attr.ownerNode, attr, (String)null, (short)2);
      } else {
        dispatchAggregateEvents(attr.ownerNode, attr, previous
            .getNodeValue(), (short)1);
      }  
  }
  
  void removedAttrNode(AttrImpl attr, NodeImpl oldOwner, String name) {
    if (this.mutationEvents) {
      LCount lc = LCount.lookup("DOMAttrModified");
      if (lc.total > 0) {
        MutationEventImpl me = new MutationEventImpl();
        me.initMutationEvent("DOMAttrModified", true, false, attr, attr
            
            .getNodeValue(), null, name, (short)3);
        dispatchEvent(oldOwner, me);
      } 
      dispatchAggregateEvents(oldOwner, (AttrImpl)null, (String)null, (short)0);
    } 
  }
  
  void renamedAttrNode(Attr oldAt, Attr newAt) {}
  
  void renamedElement(Element oldEl, Element newEl) {}
  
  class DocumentImpl {}
  
  class DocumentImpl {}
}
