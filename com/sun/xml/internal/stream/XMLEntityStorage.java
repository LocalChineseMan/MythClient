package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;

public class XMLEntityStorage {
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
  
  protected boolean fWarnDuplicateEntityDef;
  
  protected Hashtable fEntities = new Hashtable<>();
  
  protected Entity.ScannedEntity fCurrentEntity;
  
  private XMLEntityManager fEntityManager;
  
  protected XMLErrorReporter fErrorReporter;
  
  protected PropertyManager fPropertyManager;
  
  protected boolean fInExternalSubset = false;
  
  private static String gUserDir;
  
  private static String gEscapedUserDir;
  
  public XMLEntityStorage(PropertyManager propertyManager) {
    this.fPropertyManager = propertyManager;
  }
  
  public XMLEntityStorage(XMLEntityManager entityManager) {
    this.fEntityManager = entityManager;
  }
  
  public void reset(PropertyManager propertyManager) {
    this.fErrorReporter = (XMLErrorReporter)propertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fEntities.clear();
    this.fCurrentEntity = null;
  }
  
  public void reset() {
    this.fEntities.clear();
    this.fCurrentEntity = null;
  }
  
  public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
    this.fWarnDuplicateEntityDef = componentManager.getFeature("http://apache.org/xml/features/warn-on-duplicate-entitydef", false);
    this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fEntities.clear();
    this.fCurrentEntity = null;
  }
  
  public Entity getEntity(String name) {
    return (Entity)this.fEntities.get(name);
  }
  
  public boolean hasEntities() {
    return (this.fEntities != null);
  }
  
  public int getEntitySize() {
    return this.fEntities.size();
  }
  
  public Enumeration getEntityKeys() {
    return this.fEntities.keys();
  }
  
  public void addInternalEntity(String name, String text) {
    if (!this.fEntities.containsKey(name)) {
      Entity entity = new Entity.InternalEntity(name, text, this.fInExternalSubset);
      this.fEntities.put(name, entity);
    } else if (this.fWarnDuplicateEntityDef) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { name }, (short)0);
    } 
  }
  
  public void addExternalEntity(String name, String publicId, String literalSystemId, String baseSystemId) {
    if (!this.fEntities.containsKey(name)) {
      if (baseSystemId == null)
        if (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null)
          baseSystemId = this.fCurrentEntity.entityLocation.getExpandedSystemId();  
      this.fCurrentEntity = this.fEntityManager.getCurrentEntity();
      Entity entity = new Entity.ExternalEntity(name, new XMLResourceIdentifierImpl(publicId, literalSystemId, baseSystemId, expandSystemId(literalSystemId, baseSystemId)), null, this.fInExternalSubset);
      this.fEntities.put(name, entity);
    } else if (this.fWarnDuplicateEntityDef) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { name }, (short)0);
    } 
  }
  
  public boolean isExternalEntity(String entityName) {
    Entity entity = (Entity)this.fEntities.get(entityName);
    if (entity == null)
      return false; 
    return entity.isExternal();
  }
  
  public boolean isEntityDeclInExternalSubset(String entityName) {
    Entity entity = (Entity)this.fEntities.get(entityName);
    if (entity == null)
      return false; 
    return entity.isEntityDeclInExternalSubset();
  }
  
  public void addUnparsedEntity(String name, String publicId, String systemId, String baseSystemId, String notation) {
    this.fCurrentEntity = this.fEntityManager.getCurrentEntity();
    if (!this.fEntities.containsKey(name)) {
      Entity entity = new Entity.ExternalEntity(name, new XMLResourceIdentifierImpl(publicId, systemId, baseSystemId, null), notation, this.fInExternalSubset);
      this.fEntities.put(name, entity);
    } else if (this.fWarnDuplicateEntityDef) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { name }, (short)0);
    } 
  }
  
  public boolean isUnparsedEntity(String entityName) {
    Entity entity = (Entity)this.fEntities.get(entityName);
    if (entity == null)
      return false; 
    return entity.isUnparsed();
  }
  
  public boolean isDeclaredEntity(String entityName) {
    Entity entity = (Entity)this.fEntities.get(entityName);
    return (entity != null);
  }
  
  public static String expandSystemId(String systemId) {
    return expandSystemId(systemId, null);
  }
  
  private static boolean[] gNeedEscaping = new boolean[128];
  
  private static char[] gAfterEscaping1 = new char[128];
  
  private static char[] gAfterEscaping2 = new char[128];
  
  private static char[] gHexChs = new char[] { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'A', 'B', 'C', 'D', 'E', 'F' };
  
  static {
    for (int i = 0; i <= 31; i++) {
      gNeedEscaping[i] = true;
      gAfterEscaping1[i] = gHexChs[i >> 4];
      gAfterEscaping2[i] = gHexChs[i & 0xF];
    } 
    gNeedEscaping[127] = true;
    gAfterEscaping1[127] = '7';
    gAfterEscaping2[127] = 'F';
    char[] escChs = { 
        ' ', '<', '>', '#', '%', '"', '{', '}', '|', '\\', 
        '^', '~', '[', ']', '`' };
    int len = escChs.length;
    for (int j = 0; j < len; j++) {
      char ch = escChs[j];
      gNeedEscaping[ch] = true;
      gAfterEscaping1[ch] = gHexChs[ch >> 4];
      gAfterEscaping2[ch] = gHexChs[ch & 0xF];
    } 
  }
  
  private static synchronized String getUserDir() {
    String userDir = "";
    try {
      userDir = SecuritySupport.getSystemProperty("user.dir");
    } catch (SecurityException securityException) {}
    if (userDir.length() == 0)
      return ""; 
    if (userDir.equals(gUserDir))
      return gEscapedUserDir; 
    gUserDir = userDir;
    char separator = File.separatorChar;
    userDir = userDir.replace(separator, '/');
    int len = userDir.length();
    StringBuffer buffer = new StringBuffer(len * 3);
    if (len >= 2 && userDir.charAt(1) == ':') {
      int ch = Character.toUpperCase(userDir.charAt(0));
      if (ch >= 65 && ch <= 90)
        buffer.append('/'); 
    } 
    int i = 0;
    for (; i < len; i++) {
      int ch = userDir.charAt(i);
      if (ch >= 128)
        break; 
      if (gNeedEscaping[ch]) {
        buffer.append('%');
        buffer.append(gAfterEscaping1[ch]);
        buffer.append(gAfterEscaping2[ch]);
      } else {
        buffer.append((char)ch);
      } 
    } 
    if (i < len) {
      byte[] bytes = null;
      try {
        bytes = userDir.substring(i).getBytes("UTF-8");
      } catch (UnsupportedEncodingException e) {
        return userDir;
      } 
      len = bytes.length;
      for (i = 0; i < len; i++) {
        byte b = bytes[i];
        if (b < 0) {
          int ch = b + 256;
          buffer.append('%');
          buffer.append(gHexChs[ch >> 4]);
          buffer.append(gHexChs[ch & 0xF]);
        } else if (gNeedEscaping[b]) {
          buffer.append('%');
          buffer.append(gAfterEscaping1[b]);
          buffer.append(gAfterEscaping2[b]);
        } else {
          buffer.append((char)b);
        } 
      } 
    } 
    if (!userDir.endsWith("/"))
      buffer.append('/'); 
    gEscapedUserDir = buffer.toString();
    return gEscapedUserDir;
  }
  
  public static String expandSystemId(String systemId, String baseSystemId) {
    if (systemId == null || systemId.length() == 0)
      return systemId; 
    try {
      new URI(systemId);
      return systemId;
    } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {
      String id = fixURI(systemId);
      URI base = null;
      URI uri = null;
      try {
        if (baseSystemId == null || baseSystemId.length() == 0 || baseSystemId
          .equals(systemId)) {
          String dir = getUserDir();
          base = new URI("file", "", dir, null, null);
        } else {
          try {
            base = new URI(fixURI(baseSystemId));
          } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException e) {
            if (baseSystemId.indexOf(':') != -1) {
              base = new URI("file", "", fixURI(baseSystemId), null, null);
            } else {
              String dir = getUserDir();
              dir = dir + fixURI(baseSystemId);
              base = new URI("file", "", dir, null, null);
            } 
          } 
        } 
        uri = new URI(base, id);
      } catch (Exception exception) {}
      if (uri == null)
        return systemId; 
      return uri.toString();
    } 
  }
  
  protected static String fixURI(String str) {
    str = str.replace(File.separatorChar, '/');
    if (str.length() >= 2) {
      char ch1 = str.charAt(1);
      if (ch1 == ':') {
        char ch0 = Character.toUpperCase(str.charAt(0));
        if (ch0 >= 'A' && ch0 <= 'Z')
          str = "/" + str; 
      } else if (ch1 == '/' && str.charAt(0) == '/') {
        str = "file:" + str;
      } 
    } 
    return str;
  }
  
  public void startExternalSubset() {
    this.fInExternalSubset = true;
  }
  
  public void endExternalSubset() {
    this.fInExternalSubset = false;
  }
}
