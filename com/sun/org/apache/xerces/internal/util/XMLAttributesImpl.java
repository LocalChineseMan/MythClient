package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.xml.internal.stream.XMLBufferListener;

public class XMLAttributesImpl implements XMLAttributes, XMLBufferListener {
  protected static final int TABLE_SIZE = 101;
  
  protected static final int SIZE_LIMIT = 20;
  
  protected boolean fNamespaces = true;
  
  protected int fLargeCount = 1;
  
  protected int fLength;
  
  protected Attribute[] fAttributes = new Attribute[4];
  
  protected Attribute[] fAttributeTableView;
  
  protected int[] fAttributeTableViewChainState;
  
  protected int fTableViewBuckets;
  
  protected boolean fIsTableViewConsistent;
  
  public XMLAttributesImpl() {
    this(101);
  }
  
  public XMLAttributesImpl(int tableSize) {
    this.fTableViewBuckets = tableSize;
    for (int i = 0; i < this.fAttributes.length; i++)
      this.fAttributes[i] = new Attribute(); 
  }
  
  public void setNamespaces(boolean namespaces) {
    this.fNamespaces = namespaces;
  }
  
  public int addAttribute(QName name, String type, String value) {
    return addAttribute(name, type, value, null);
  }
  
  public int addAttribute(QName name, String type, String value, XMLString valueCache) {
    int index;
    if (this.fLength < 20) {
      index = (name.uri != null && !name.uri.equals("")) ? getIndexFast(name.uri, name.localpart) : getIndexFast(name.rawname);
      index = this.fLength;
      if (index == -1 && this.fLength++ == this.fAttributes.length) {
        Attribute[] attributes = new Attribute[this.fAttributes.length + 4];
        System.arraycopy(this.fAttributes, 0, attributes, 0, this.fAttributes.length);
        for (int i = this.fAttributes.length; i < attributes.length; i++)
          attributes[i] = new Attribute(); 
        this.fAttributes = attributes;
      } 
    } else if (name.uri == null || name.uri
      .length() == 0 || (
      index = getIndexFast(name.uri, name.localpart)) == -1) {
      if (!this.fIsTableViewConsistent || this.fLength == 20) {
        prepareAndPopulateTableView();
        this.fIsTableViewConsistent = true;
      } 
      int bucket = getTableViewBucket(name.rawname);
      if (this.fAttributeTableViewChainState[bucket] != this.fLargeCount) {
        index = this.fLength;
        if (this.fLength++ == this.fAttributes.length) {
          Attribute[] attributes = new Attribute[this.fAttributes.length << 1];
          System.arraycopy(this.fAttributes, 0, attributes, 0, this.fAttributes.length);
          for (int i = this.fAttributes.length; i < attributes.length; i++)
            attributes[i] = new Attribute(); 
          this.fAttributes = attributes;
        } 
        this.fAttributeTableViewChainState[bucket] = this.fLargeCount;
        (this.fAttributes[index]).next = null;
        this.fAttributeTableView[bucket] = this.fAttributes[index];
      } else {
        Attribute found = this.fAttributeTableView[bucket];
        while (found != null && 
          found.name.rawname != name.rawname)
          found = found.next; 
        if (found == null) {
          index = this.fLength;
          if (this.fLength++ == this.fAttributes.length) {
            Attribute[] attributes = new Attribute[this.fAttributes.length << 1];
            System.arraycopy(this.fAttributes, 0, attributes, 0, this.fAttributes.length);
            for (int i = this.fAttributes.length; i < attributes.length; i++)
              attributes[i] = new Attribute(); 
            this.fAttributes = attributes;
          } 
          (this.fAttributes[index]).next = this.fAttributeTableView[bucket];
          this.fAttributeTableView[bucket] = this.fAttributes[index];
        } else {
          index = getIndexFast(name.rawname);
        } 
      } 
    } 
    Attribute attribute = this.fAttributes[index];
    attribute.name.setValues(name);
    attribute.type = type;
    attribute.value = value;
    attribute.xmlValue = valueCache;
    attribute.nonNormalizedValue = value;
    attribute.specified = false;
    if (attribute.augs != null)
      attribute.augs.removeAllItems(); 
    return index;
  }
  
  public void removeAllAttributes() {
    this.fLength = 0;
  }
  
  public void removeAttributeAt(int attrIndex) {
    this.fIsTableViewConsistent = false;
    if (attrIndex < this.fLength - 1) {
      Attribute removedAttr = this.fAttributes[attrIndex];
      System.arraycopy(this.fAttributes, attrIndex + 1, this.fAttributes, attrIndex, this.fLength - attrIndex - 1);
      this.fAttributes[this.fLength - 1] = removedAttr;
    } 
    this.fLength--;
  }
  
  public void setName(int attrIndex, QName attrName) {
    (this.fAttributes[attrIndex]).name.setValues(attrName);
  }
  
  public void getName(int attrIndex, QName attrName) {
    attrName.setValues((this.fAttributes[attrIndex]).name);
  }
  
  public void setType(int attrIndex, String attrType) {
    (this.fAttributes[attrIndex]).type = attrType;
  }
  
  public void setValue(int attrIndex, String attrValue) {
    setValue(attrIndex, attrValue, null);
  }
  
  public void setValue(int attrIndex, String attrValue, XMLString value) {
    Attribute attribute = this.fAttributes[attrIndex];
    attribute.value = attrValue;
    attribute.nonNormalizedValue = attrValue;
    attribute.xmlValue = value;
  }
  
  public void setNonNormalizedValue(int attrIndex, String attrValue) {
    if (attrValue == null)
      attrValue = (this.fAttributes[attrIndex]).value; 
    (this.fAttributes[attrIndex]).nonNormalizedValue = attrValue;
  }
  
  public String getNonNormalizedValue(int attrIndex) {
    String value = (this.fAttributes[attrIndex]).nonNormalizedValue;
    return value;
  }
  
  public void setSpecified(int attrIndex, boolean specified) {
    (this.fAttributes[attrIndex]).specified = specified;
  }
  
  public boolean isSpecified(int attrIndex) {
    return (this.fAttributes[attrIndex]).specified;
  }
  
  public int getLength() {
    return this.fLength;
  }
  
  public String getType(int index) {
    if (index < 0 || index >= this.fLength)
      return null; 
    return getReportableType((this.fAttributes[index]).type);
  }
  
  public String getType(String qname) {
    int index = getIndex(qname);
    return (index != -1) ? getReportableType((this.fAttributes[index]).type) : null;
  }
  
  public String getValue(int index) {
    if (index < 0 || index >= this.fLength)
      return null; 
    if ((this.fAttributes[index]).value == null && (this.fAttributes[index]).xmlValue != null)
      (this.fAttributes[index]).value = (this.fAttributes[index]).xmlValue.toString(); 
    return (this.fAttributes[index]).value;
  }
  
  public String getValue(String qname) {
    int index = getIndex(qname);
    if (index == -1)
      return null; 
    if ((this.fAttributes[index]).value == null)
      (this.fAttributes[index]).value = (this.fAttributes[index]).xmlValue.toString(); 
    return (this.fAttributes[index]).value;
  }
  
  public String getName(int index) {
    if (index < 0 || index >= this.fLength)
      return null; 
    return (this.fAttributes[index]).name.rawname;
  }
  
  public int getIndex(String qName) {
    for (int i = 0; i < this.fLength; i++) {
      Attribute attribute = this.fAttributes[i];
      if (attribute.name.rawname != null && attribute.name.rawname
        .equals(qName))
        return i; 
    } 
    return -1;
  }
  
  public int getIndex(String uri, String localPart) {
    for (int i = 0; i < this.fLength; i++) {
      Attribute attribute = this.fAttributes[i];
      if (attribute.name.localpart != null && attribute.name.localpart
        .equals(localPart) && (uri == attribute.name.uri || (uri != null && attribute.name.uri != null && attribute.name.uri
        
        .equals(uri))))
        return i; 
    } 
    return -1;
  }
  
  public int getIndexByLocalName(String localPart) {
    for (int i = 0; i < this.fLength; i++) {
      Attribute attribute = this.fAttributes[i];
      if (attribute.name.localpart != null && attribute.name.localpart
        .equals(localPart))
        return i; 
    } 
    return -1;
  }
  
  public String getLocalName(int index) {
    if (!this.fNamespaces)
      return ""; 
    if (index < 0 || index >= this.fLength)
      return null; 
    return (this.fAttributes[index]).name.localpart;
  }
  
  public String getQName(int index) {
    if (index < 0 || index >= this.fLength)
      return null; 
    String rawname = (this.fAttributes[index]).name.rawname;
    return (rawname != null) ? rawname : "";
  }
  
  public QName getQualifiedName(int index) {
    if (index < 0 || index >= this.fLength)
      return null; 
    return (this.fAttributes[index]).name;
  }
  
  public String getType(String uri, String localName) {
    if (!this.fNamespaces)
      return null; 
    int index = getIndex(uri, localName);
    return (index != -1) ? getType(index) : null;
  }
  
  public int getIndexFast(String qName) {
    for (int i = 0; i < this.fLength; i++) {
      Attribute attribute = this.fAttributes[i];
      if (attribute.name.rawname == qName)
        return i; 
    } 
    return -1;
  }
  
  public void addAttributeNS(QName name, String type, String value) {
    int index = this.fLength;
    if (this.fLength++ == this.fAttributes.length) {
      Attribute[] attributes;
      if (this.fLength < 20) {
        attributes = new Attribute[this.fAttributes.length + 4];
      } else {
        attributes = new Attribute[this.fAttributes.length << 1];
      } 
      System.arraycopy(this.fAttributes, 0, attributes, 0, this.fAttributes.length);
      for (int i = this.fAttributes.length; i < attributes.length; i++)
        attributes[i] = new Attribute(); 
      this.fAttributes = attributes;
    } 
    Attribute attribute = this.fAttributes[index];
    attribute.name.setValues(name);
    attribute.type = type;
    attribute.value = value;
    attribute.nonNormalizedValue = value;
    attribute.specified = false;
    attribute.augs.removeAllItems();
  }
  
  public QName checkDuplicatesNS() {
    if (this.fLength <= 20) {
      for (int i = 0; i < this.fLength - 1; i++) {
        Attribute att1 = this.fAttributes[i];
        for (int j = i + 1; j < this.fLength; j++) {
          Attribute att2 = this.fAttributes[j];
          if (att1.name.localpart == att2.name.localpart && att1.name.uri == att2.name.uri)
            return att2.name; 
        } 
      } 
    } else {
      this.fIsTableViewConsistent = false;
      prepareTableView();
      for (int i = this.fLength - 1; i >= 0; i--) {
        Attribute attr = this.fAttributes[i];
        int bucket = getTableViewBucket(attr.name.localpart, attr.name.uri);
        if (this.fAttributeTableViewChainState[bucket] != this.fLargeCount) {
          this.fAttributeTableViewChainState[bucket] = this.fLargeCount;
          attr.next = null;
          this.fAttributeTableView[bucket] = attr;
        } else {
          Attribute found = this.fAttributeTableView[bucket];
          while (found != null) {
            if (found.name.localpart == attr.name.localpart && found.name.uri == attr.name.uri)
              return attr.name; 
            found = found.next;
          } 
          attr.next = this.fAttributeTableView[bucket];
          this.fAttributeTableView[bucket] = attr;
        } 
      } 
    } 
    return null;
  }
  
  public int getIndexFast(String uri, String localPart) {
    for (int i = 0; i < this.fLength; i++) {
      Attribute attribute = this.fAttributes[i];
      if (attribute.name.localpart == localPart && attribute.name.uri == uri)
        return i; 
    } 
    return -1;
  }
  
  private String getReportableType(String type) {
    if (type.charAt(0) == '(')
      return "NMTOKEN"; 
    return type;
  }
  
  protected int getTableViewBucket(String qname) {
    return (qname.hashCode() & Integer.MAX_VALUE) % this.fTableViewBuckets;
  }
  
  protected int getTableViewBucket(String localpart, String uri) {
    if (uri == null)
      return (localpart.hashCode() & Integer.MAX_VALUE) % this.fTableViewBuckets; 
    return (localpart.hashCode() + uri.hashCode() & Integer.MAX_VALUE) % this.fTableViewBuckets;
  }
  
  protected void cleanTableView() {
    if (++this.fLargeCount < 0) {
      if (this.fAttributeTableViewChainState != null)
        for (int i = this.fTableViewBuckets - 1; i >= 0; i--)
          this.fAttributeTableViewChainState[i] = 0;  
      this.fLargeCount = 1;
    } 
  }
  
  protected void prepareTableView() {
    if (this.fAttributeTableView == null) {
      this.fAttributeTableView = new Attribute[this.fTableViewBuckets];
      this.fAttributeTableViewChainState = new int[this.fTableViewBuckets];
    } else {
      cleanTableView();
    } 
  }
  
  protected void prepareAndPopulateTableView() {
    prepareTableView();
    for (int i = 0; i < this.fLength; i++) {
      Attribute attr = this.fAttributes[i];
      int bucket = getTableViewBucket(attr.name.rawname);
      if (this.fAttributeTableViewChainState[bucket] != this.fLargeCount) {
        this.fAttributeTableViewChainState[bucket] = this.fLargeCount;
        attr.next = null;
        this.fAttributeTableView[bucket] = attr;
      } else {
        attr.next = this.fAttributeTableView[bucket];
        this.fAttributeTableView[bucket] = attr;
      } 
    } 
  }
  
  public String getPrefix(int index) {
    if (index < 0 || index >= this.fLength)
      return null; 
    String prefix = (this.fAttributes[index]).name.prefix;
    return (prefix != null) ? prefix : "";
  }
  
  public String getURI(int index) {
    if (index < 0 || index >= this.fLength)
      return null; 
    String uri = (this.fAttributes[index]).name.uri;
    return uri;
  }
  
  public String getValue(String uri, String localName) {
    int index = getIndex(uri, localName);
    return (index != -1) ? getValue(index) : null;
  }
  
  public Augmentations getAugmentations(String uri, String localName) {
    int index = getIndex(uri, localName);
    return (index != -1) ? (this.fAttributes[index]).augs : null;
  }
  
  public Augmentations getAugmentations(String qName) {
    int index = getIndex(qName);
    return (index != -1) ? (this.fAttributes[index]).augs : null;
  }
  
  public Augmentations getAugmentations(int attributeIndex) {
    if (attributeIndex < 0 || attributeIndex >= this.fLength)
      return null; 
    return (this.fAttributes[attributeIndex]).augs;
  }
  
  public void setAugmentations(int attrIndex, Augmentations augs) {
    (this.fAttributes[attrIndex]).augs = augs;
  }
  
  public void setURI(int attrIndex, String uri) {
    (this.fAttributes[attrIndex]).name.uri = uri;
  }
  
  public void setSchemaId(int attrIndex, boolean schemaId) {
    (this.fAttributes[attrIndex]).schemaId = schemaId;
  }
  
  public boolean getSchemaId(int index) {
    if (index < 0 || index >= this.fLength)
      return false; 
    return (this.fAttributes[index]).schemaId;
  }
  
  public boolean getSchemaId(String qname) {
    int index = getIndex(qname);
    return (index != -1) ? (this.fAttributes[index]).schemaId : false;
  }
  
  public boolean getSchemaId(String uri, String localName) {
    if (!this.fNamespaces)
      return false; 
    int index = getIndex(uri, localName);
    return (index != -1) ? (this.fAttributes[index]).schemaId : false;
  }
  
  public void refresh() {
    if (this.fLength > 0)
      for (int i = 0; i < this.fLength; i++)
        getValue(i);  
  }
  
  public void refresh(int pos) {}
  
  static class Attribute {
    public QName name = new QName();
    
    public String type;
    
    public String value;
    
    public XMLString xmlValue;
    
    public String nonNormalizedValue;
    
    public boolean specified;
    
    public boolean schemaId;
    
    public Augmentations augs = new AugmentationsImpl();
    
    public Attribute next;
  }
}
