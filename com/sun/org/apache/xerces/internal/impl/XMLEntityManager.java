package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import com.sun.org.apache.xerces.internal.impl.io.UCSReader;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import com.sun.org.apache.xerces.internal.util.HTTPInputSource;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLEntityDescriptionImpl;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.StaxEntityResolverWrapper;
import com.sun.xml.internal.stream.StaxXMLInputSource;
import com.sun.xml.internal.stream.XMLEntityStorage;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

public class XMLEntityManager implements XMLComponent, XMLEntityResolver {
  public static final int DEFAULT_BUFFER_SIZE = 8192;
  
  public static final int DEFAULT_XMLDECL_BUFFER_SIZE = 64;
  
  public static final int DEFAULT_INTERNAL_BUFFER_SIZE = 1024;
  
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  
  protected boolean fStrictURI;
  
  protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
  
  protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
  
  protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
  
  protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
  
  protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String STANDARD_URI_CONFORMANT = "http://apache.org/xml/features/standard-uri-conformant";
  
  protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
  
  protected static final String STAX_ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/stax-entity-resolver";
  
  protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
  
  protected static final String BUFFER_SIZE = "http://apache.org/xml/properties/input-buffer-size";
  
  protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  
  static final String EXTERNAL_ACCESS_DEFAULT = "all";
  
  private static final String[] RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/validation", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/warn-on-duplicate-entitydef", "http://apache.org/xml/features/standard-uri-conformant" };
  
  private static final Boolean[] FEATURE_DEFAULTS = new Boolean[] { null, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE };
  
  private static final String[] RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/input-buffer-size", "http://apache.org/xml/properties/security-manager", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
  
  private static final Object[] PROPERTY_DEFAULTS = new Object[] { null, null, null, null, new Integer(8192), null, null };
  
  private static final String XMLEntity = "[xml]".intern();
  
  private static final String DTDEntity = "[dtd]".intern();
  
  private static final boolean DEBUG_BUFFER = false;
  
  protected boolean fWarnDuplicateEntityDef;
  
  private static final boolean DEBUG_ENTITIES = false;
  
  private static final boolean DEBUG_ENCODINGS = false;
  
  private static final boolean DEBUG_RESOLVER = false;
  
  protected boolean fValidation;
  
  protected boolean fExternalGeneralEntities;
  
  protected boolean fExternalParameterEntities;
  
  protected boolean fAllowJavaEncodings = true;
  
  protected boolean fLoadExternalDTD = true;
  
  protected SymbolTable fSymbolTable;
  
  protected XMLErrorReporter fErrorReporter;
  
  protected XMLEntityResolver fEntityResolver;
  
  protected StaxEntityResolverWrapper fStaxEntityResolver;
  
  protected PropertyManager fPropertyManager;
  
  boolean fSupportDTD = true;
  
  boolean fReplaceEntityReferences = true;
  
  boolean fSupportExternalEntities = true;
  
  protected String fAccessExternalDTD = "all";
  
  protected ValidationManager fValidationManager;
  
  protected int fBufferSize = 8192;
  
  protected XMLSecurityManager fSecurityManager = null;
  
  protected XMLLimitAnalyzer fLimitAnalyzer = null;
  
  protected int entityExpansionIndex;
  
  protected boolean fStandalone;
  
  protected boolean fInExternalSubset = false;
  
  protected XMLEntityHandler fEntityHandler;
  
  protected XMLEntityScanner fEntityScanner;
  
  protected XMLEntityScanner fXML10EntityScanner;
  
  protected XMLEntityScanner fXML11EntityScanner;
  
  protected int fEntityExpansionCount = 0;
  
  protected Hashtable fEntities = new Hashtable<>();
  
  protected Stack fEntityStack = new Stack();
  
  protected Entity.ScannedEntity fCurrentEntity = null;
  
  boolean fISCreatedByResolver = false;
  
  protected XMLEntityStorage fEntityStorage;
  
  protected final Object[] defaultEncoding = new Object[] { "UTF-8", null };
  
  private final XMLResourceIdentifierImpl fResourceIdentifier = new XMLResourceIdentifierImpl();
  
  private final Augmentations fEntityAugs = new AugmentationsImpl();
  
  private CharacterBufferPool fBufferPool = new CharacterBufferPool(this.fBufferSize, 1024);
  
  private static String gUserDir;
  
  private static URI gUserDirURI;
  
  public XMLEntityManager() {
    this.fEntityStorage = new XMLEntityStorage(this);
    setScannerVersion((short)1);
  }
  
  public XMLEntityManager(PropertyManager propertyManager) {
    this.fPropertyManager = propertyManager;
    this.fEntityStorage = new XMLEntityStorage(this);
    this.fEntityScanner = new XMLEntityScanner(propertyManager, this);
    reset(propertyManager);
  }
  
  public void addInternalEntity(String name, String text) {
    if (!this.fEntities.containsKey(name)) {
      Entity entity = new Entity.InternalEntity(name, text, this.fInExternalSubset);
      this.fEntities.put(name, entity);
    } else if (this.fWarnDuplicateEntityDef) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { name }, (short)0);
    } 
  }
  
  public void addExternalEntity(String name, String publicId, String literalSystemId, String baseSystemId) throws IOException {
    if (!this.fEntities.containsKey(name)) {
      if (baseSystemId == null) {
        int size = this.fEntityStack.size();
        if (size == 0 && this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null)
          baseSystemId = this.fCurrentEntity.entityLocation.getExpandedSystemId(); 
        for (int i = size - 1; i >= 0; i--) {
          Entity.ScannedEntity externalEntity = this.fEntityStack.elementAt(i);
          if (externalEntity.entityLocation != null && externalEntity.entityLocation.getExpandedSystemId() != null) {
            baseSystemId = externalEntity.entityLocation.getExpandedSystemId();
            break;
          } 
        } 
      } 
      Entity entity = new Entity.ExternalEntity(name, new XMLEntityDescriptionImpl(name, publicId, literalSystemId, baseSystemId, expandSystemId(literalSystemId, baseSystemId, false)), null, this.fInExternalSubset);
      this.fEntities.put(name, entity);
    } else if (this.fWarnDuplicateEntityDef) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { name }, (short)0);
    } 
  }
  
  public void addUnparsedEntity(String name, String publicId, String systemId, String baseSystemId, String notation) {
    if (!this.fEntities.containsKey(name)) {
      Entity.ExternalEntity entity = new Entity.ExternalEntity(name, new XMLEntityDescriptionImpl(name, publicId, systemId, baseSystemId, null), notation, this.fInExternalSubset);
      this.fEntities.put(name, entity);
    } else if (this.fWarnDuplicateEntityDef) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION", new Object[] { name }, (short)0);
    } 
  }
  
  public XMLEntityStorage getEntityStore() {
    return this.fEntityStorage;
  }
  
  public XMLEntityScanner getEntityScanner() {
    if (this.fEntityScanner == null) {
      if (this.fXML10EntityScanner == null)
        this.fXML10EntityScanner = new XMLEntityScanner(); 
      this.fXML10EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
      this.fEntityScanner = this.fXML10EntityScanner;
    } 
    return this.fEntityScanner;
  }
  
  public void setScannerVersion(short version) {
    if (version == 1) {
      if (this.fXML10EntityScanner == null)
        this.fXML10EntityScanner = new XMLEntityScanner(); 
      this.fXML10EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
      this.fEntityScanner = this.fXML10EntityScanner;
      this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
    } else {
      if (this.fXML11EntityScanner == null)
        this.fXML11EntityScanner = new XML11EntityScanner(); 
      this.fXML11EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter);
      this.fEntityScanner = this.fXML11EntityScanner;
      this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
    } 
  }
  
  public String setupCurrentEntity(String name, XMLInputSource xmlInputSource, boolean literal, boolean isExternal) throws IOException, XNIException {
    String publicId = xmlInputSource.getPublicId();
    String literalSystemId = xmlInputSource.getSystemId();
    String baseSystemId = xmlInputSource.getBaseSystemId();
    String encoding = xmlInputSource.getEncoding();
    boolean encodingExternallySpecified = (encoding != null);
    Boolean isBigEndian = null;
    InputStream stream = null;
    Reader reader = xmlInputSource.getCharacterStream();
    String expandedSystemId = expandSystemId(literalSystemId, baseSystemId, this.fStrictURI);
    if (baseSystemId == null)
      baseSystemId = expandedSystemId; 
    if (reader == null) {
      stream = xmlInputSource.getByteStream();
      if (stream == null) {
        URL location = new URL(expandedSystemId);
        URLConnection connect = location.openConnection();
        if (!(connect instanceof HttpURLConnection)) {
          stream = connect.getInputStream();
        } else {
          boolean followRedirects = true;
          if (xmlInputSource instanceof HTTPInputSource) {
            HttpURLConnection urlConnection = (HttpURLConnection)connect;
            HTTPInputSource httpInputSource = (HTTPInputSource)xmlInputSource;
            Iterator<Map.Entry<String, String>> propIter = httpInputSource.getHTTPRequestProperties();
            while (propIter.hasNext()) {
              Map.Entry entry = propIter.next();
              urlConnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
            } 
            followRedirects = httpInputSource.getFollowHTTPRedirects();
            if (!followRedirects)
              setInstanceFollowRedirects(urlConnection, followRedirects); 
          } 
          stream = connect.getInputStream();
          if (followRedirects) {
            String redirect = connect.getURL().toString();
            if (!redirect.equals(expandedSystemId)) {
              literalSystemId = redirect;
              expandedSystemId = redirect;
            } 
          } 
        } 
      } 
      stream = new RewindableInputStream(stream);
      if (encoding == null) {
        byte[] b4 = new byte[4];
        int count = 0;
        for (; count < 4; count++)
          b4[count] = (byte)stream.read(); 
        if (count == 4) {
          Object[] encodingDesc = getEncodingName(b4, count);
          encoding = (String)encodingDesc[0];
          isBigEndian = (Boolean)encodingDesc[1];
          stream.reset();
          if (count > 2 && encoding.equals("UTF-8")) {
            int b0 = b4[0] & 0xFF;
            int b1 = b4[1] & 0xFF;
            int b2 = b4[2] & 0xFF;
            if (b0 == 239 && b1 == 187 && b2 == 191)
              stream.skip(3L); 
          } 
          reader = createReader(stream, encoding, isBigEndian);
        } else {
          reader = createReader(stream, encoding, isBigEndian);
        } 
      } else {
        encoding = encoding.toUpperCase(Locale.ENGLISH);
        if (encoding.equals("UTF-8")) {
          int[] b3 = new int[3];
          int count = 0;
          for (; count < 3; count++) {
            b3[count] = stream.read();
            if (b3[count] == -1)
              break; 
          } 
          if (count == 3) {
            if (b3[0] != 239 || b3[1] != 187 || b3[2] != 191)
              stream.reset(); 
          } else {
            stream.reset();
          } 
        } else if (encoding.equals("UTF-16")) {
          int[] b4 = new int[4];
          int count = 0;
          for (; count < 4; count++) {
            b4[count] = stream.read();
            if (b4[count] == -1)
              break; 
          } 
          stream.reset();
          String utf16Encoding = "UTF-16";
          if (count >= 2) {
            int b0 = b4[0];
            int b1 = b4[1];
            if (b0 == 254 && b1 == 255) {
              utf16Encoding = "UTF-16BE";
              isBigEndian = Boolean.TRUE;
            } else if (b0 == 255 && b1 == 254) {
              utf16Encoding = "UTF-16LE";
              isBigEndian = Boolean.FALSE;
            } else if (count == 4) {
              int b2 = b4[2];
              int b3 = b4[3];
              if (b0 == 0 && b1 == 60 && b2 == 0 && b3 == 63) {
                utf16Encoding = "UTF-16BE";
                isBigEndian = Boolean.TRUE;
              } 
              if (b0 == 60 && b1 == 0 && b2 == 63 && b3 == 0) {
                utf16Encoding = "UTF-16LE";
                isBigEndian = Boolean.FALSE;
              } 
            } 
          } 
          reader = createReader(stream, utf16Encoding, isBigEndian);
        } else if (encoding.equals("ISO-10646-UCS-4")) {
          int[] b4 = new int[4];
          int count = 0;
          for (; count < 4; count++) {
            b4[count] = stream.read();
            if (b4[count] == -1)
              break; 
          } 
          stream.reset();
          if (count == 4)
            if (b4[0] == 0 && b4[1] == 0 && b4[2] == 0 && b4[3] == 60) {
              isBigEndian = Boolean.TRUE;
            } else if (b4[0] == 60 && b4[1] == 0 && b4[2] == 0 && b4[3] == 0) {
              isBigEndian = Boolean.FALSE;
            }  
        } else if (encoding.equals("ISO-10646-UCS-2")) {
          int[] b4 = new int[4];
          int count = 0;
          for (; count < 4; count++) {
            b4[count] = stream.read();
            if (b4[count] == -1)
              break; 
          } 
          stream.reset();
          if (count == 4)
            if (b4[0] == 0 && b4[1] == 60 && b4[2] == 0 && b4[3] == 63) {
              isBigEndian = Boolean.TRUE;
            } else if (b4[0] == 60 && b4[1] == 0 && b4[2] == 63 && b4[3] == 0) {
              isBigEndian = Boolean.FALSE;
            }  
        } 
        reader = createReader(stream, encoding, isBigEndian);
      } 
    } 
    if (this.fCurrentEntity != null)
      this.fEntityStack.push(this.fCurrentEntity); 
    this.fCurrentEntity = new Entity.ScannedEntity(name, new XMLResourceIdentifierImpl(publicId, literalSystemId, baseSystemId, expandedSystemId), stream, reader, encoding, literal, encodingExternallySpecified, isExternal);
    this.fCurrentEntity.setEncodingExternallySpecified(encodingExternallySpecified);
    this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
    this.fResourceIdentifier.setValues(publicId, literalSystemId, baseSystemId, expandedSystemId);
    if (this.fLimitAnalyzer != null)
      this.fLimitAnalyzer.startEntity(name); 
    return encoding;
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
  
  public void setStandalone(boolean standalone) {
    this.fStandalone = standalone;
  }
  
  public boolean isStandalone() {
    return this.fStandalone;
  }
  
  public boolean isDeclaredEntity(String entityName) {
    Entity entity = (Entity)this.fEntities.get(entityName);
    return (entity != null);
  }
  
  public boolean isUnparsedEntity(String entityName) {
    Entity entity = (Entity)this.fEntities.get(entityName);
    if (entity == null)
      return false; 
    return entity.isUnparsed();
  }
  
  public XMLResourceIdentifier getCurrentResourceIdentifier() {
    return this.fResourceIdentifier;
  }
  
  public void setEntityHandler(XMLEntityHandler entityHandler) {
    this.fEntityHandler = entityHandler;
  }
  
  public StaxXMLInputSource resolveEntityAsPerStax(XMLResourceIdentifier resourceIdentifier) throws IOException {
    if (resourceIdentifier == null)
      return null; 
    String publicId = resourceIdentifier.getPublicId();
    String literalSystemId = resourceIdentifier.getLiteralSystemId();
    String baseSystemId = resourceIdentifier.getBaseSystemId();
    String expandedSystemId = resourceIdentifier.getExpandedSystemId();
    boolean needExpand = (expandedSystemId == null);
    if (baseSystemId == null && this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) {
      baseSystemId = this.fCurrentEntity.entityLocation.getExpandedSystemId();
      if (baseSystemId != null)
        needExpand = true; 
    } 
    if (needExpand)
      expandedSystemId = expandSystemId(literalSystemId, baseSystemId, false); 
    StaxXMLInputSource staxInputSource = null;
    XMLInputSource xmlInputSource = null;
    XMLResourceIdentifierImpl ri = null;
    if (resourceIdentifier instanceof XMLResourceIdentifierImpl) {
      ri = (XMLResourceIdentifierImpl)resourceIdentifier;
    } else {
      this.fResourceIdentifier.clear();
      ri = this.fResourceIdentifier;
    } 
    ri.setValues(publicId, literalSystemId, baseSystemId, expandedSystemId);
    this.fISCreatedByResolver = false;
    if (this.fStaxEntityResolver != null) {
      staxInputSource = this.fStaxEntityResolver.resolveEntity(ri);
      if (staxInputSource != null)
        this.fISCreatedByResolver = true; 
    } 
    if (this.fEntityResolver != null) {
      xmlInputSource = this.fEntityResolver.resolveEntity(ri);
      if (xmlInputSource != null)
        this.fISCreatedByResolver = true; 
    } 
    if (xmlInputSource != null)
      staxInputSource = new StaxXMLInputSource(xmlInputSource, this.fISCreatedByResolver); 
    if (staxInputSource == null) {
      staxInputSource = new StaxXMLInputSource(new XMLInputSource(publicId, literalSystemId, baseSystemId));
    } else if (staxInputSource.hasXMLStreamOrXMLEventReader()) {
    
    } 
    return staxInputSource;
  }
  
  public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier) throws IOException, XNIException {
    if (resourceIdentifier == null)
      return null; 
    String publicId = resourceIdentifier.getPublicId();
    String literalSystemId = resourceIdentifier.getLiteralSystemId();
    String baseSystemId = resourceIdentifier.getBaseSystemId();
    String expandedSystemId = resourceIdentifier.getExpandedSystemId();
    String namespace = resourceIdentifier.getNamespace();
    boolean needExpand = (expandedSystemId == null);
    if (baseSystemId == null && this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) {
      baseSystemId = this.fCurrentEntity.entityLocation.getExpandedSystemId();
      if (baseSystemId != null)
        needExpand = true; 
    } 
    if (needExpand)
      expandedSystemId = expandSystemId(literalSystemId, baseSystemId, false); 
    XMLInputSource xmlInputSource = null;
    if (this.fEntityResolver != null) {
      resourceIdentifier.setBaseSystemId(baseSystemId);
      resourceIdentifier.setExpandedSystemId(expandedSystemId);
      xmlInputSource = this.fEntityResolver.resolveEntity(resourceIdentifier);
    } 
    if (xmlInputSource == null)
      xmlInputSource = new XMLInputSource(publicId, literalSystemId, baseSystemId); 
    return xmlInputSource;
  }
  
  public void startEntity(String entityName, boolean literal) throws IOException, XNIException {
    Entity entity = this.fEntityStorage.getEntity(entityName);
    if (entity == null) {
      if (this.fEntityHandler != null) {
        String encoding = null;
        this.fResourceIdentifier.clear();
        this.fEntityAugs.removeAllItems();
        this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
        this.fEntityHandler.startEntity(entityName, this.fResourceIdentifier, encoding, this.fEntityAugs);
        this.fEntityAugs.removeAllItems();
        this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
        this.fEntityHandler.endEntity(entityName, this.fEntityAugs);
      } 
      return;
    } 
    boolean external = entity.isExternal();
    Entity.ExternalEntity externalEntity = null;
    String extLitSysId = null, extBaseSysId = null, expandedSystemId = null;
    if (external) {
      externalEntity = (Entity.ExternalEntity)entity;
      extLitSysId = (externalEntity.entityLocation != null) ? externalEntity.entityLocation.getLiteralSystemId() : null;
      extBaseSysId = (externalEntity.entityLocation != null) ? externalEntity.entityLocation.getBaseSystemId() : null;
      expandedSystemId = expandSystemId(extLitSysId, extBaseSysId);
      boolean unparsed = entity.isUnparsed();
      boolean parameter = entityName.startsWith("%");
      boolean general = !parameter;
      if (unparsed || (general && !this.fExternalGeneralEntities) || (parameter && !this.fExternalParameterEntities) || !this.fSupportDTD || !this.fSupportExternalEntities) {
        if (this.fEntityHandler != null) {
          this.fResourceIdentifier.clear();
          String encoding = null;
          this.fResourceIdentifier.setValues((externalEntity.entityLocation != null) ? externalEntity.entityLocation
              .getPublicId() : null, extLitSysId, extBaseSysId, expandedSystemId);
          this.fEntityAugs.removeAllItems();
          this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
          this.fEntityHandler.startEntity(entityName, this.fResourceIdentifier, encoding, this.fEntityAugs);
          this.fEntityAugs.removeAllItems();
          this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
          this.fEntityHandler.endEntity(entityName, this.fEntityAugs);
        } 
        return;
      } 
    } 
    int size = this.fEntityStack.size();
    for (int i = size; i >= 0; i--) {
      Entity activeEntity = (i == size) ? this.fCurrentEntity : this.fEntityStack.elementAt(i);
      if (activeEntity.name == entityName) {
        String path = entityName;
        for (int j = i + 1; j < size; j++) {
          activeEntity = this.fEntityStack.elementAt(j);
          path = path + " -> " + activeEntity.name;
        } 
        path = path + " -> " + this.fCurrentEntity.name;
        path = path + " -> " + entityName;
        this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "RecursiveReference", new Object[] { entityName, path }, (short)2);
        if (this.fEntityHandler != null) {
          this.fResourceIdentifier.clear();
          String encoding = null;
          if (external)
            this.fResourceIdentifier.setValues((externalEntity.entityLocation != null) ? externalEntity.entityLocation
                .getPublicId() : null, extLitSysId, extBaseSysId, expandedSystemId); 
          this.fEntityAugs.removeAllItems();
          this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
          this.fEntityHandler.startEntity(entityName, this.fResourceIdentifier, encoding, this.fEntityAugs);
          this.fEntityAugs.removeAllItems();
          this.fEntityAugs.putItem("ENTITY_SKIPPED", Boolean.TRUE);
          this.fEntityHandler.endEntity(entityName, this.fEntityAugs);
        } 
        return;
      } 
    } 
    StaxXMLInputSource staxInputSource = null;
    XMLInputSource xmlInputSource = null;
    if (external) {
      staxInputSource = resolveEntityAsPerStax(externalEntity.entityLocation);
      xmlInputSource = staxInputSource.getXMLInputSource();
      if (!this.fISCreatedByResolver)
        if (this.fLoadExternalDTD) {
          String accessError = SecuritySupport.checkAccess(expandedSystemId, this.fAccessExternalDTD, "all");
          if (accessError != null)
            this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "AccessExternalEntity", new Object[] { SecuritySupport.sanitizePath(expandedSystemId), accessError }, (short)2); 
        }  
    } else {
      Entity.InternalEntity internalEntity = (Entity.InternalEntity)entity;
      Reader reader = new StringReader(internalEntity.text);
      xmlInputSource = new XMLInputSource(null, null, null, reader, null);
    } 
    startEntity(entityName, xmlInputSource, literal, external);
  }
  
  public void startDocumentEntity(XMLInputSource xmlInputSource) throws IOException, XNIException {
    startEntity(XMLEntity, xmlInputSource, false, true);
  }
  
  public void startDTDEntity(XMLInputSource xmlInputSource) throws IOException, XNIException {
    startEntity(DTDEntity, xmlInputSource, false, true);
  }
  
  public void startExternalSubset() {
    this.fInExternalSubset = true;
  }
  
  public void endExternalSubset() {
    this.fInExternalSubset = false;
  }
  
  public void startEntity(String name, XMLInputSource xmlInputSource, boolean literal, boolean isExternal) throws IOException, XNIException {
    String encoding = setupCurrentEntity(name, xmlInputSource, literal, isExternal);
    this.fEntityExpansionCount++;
    if (this.fLimitAnalyzer != null)
      this.fLimitAnalyzer.addValue(this.entityExpansionIndex, name, 1); 
    if (this.fSecurityManager != null && this.fSecurityManager.isOverLimit(this.entityExpansionIndex, this.fLimitAnalyzer)) {
      this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityExpansionLimitExceeded", new Object[] { this.fSecurityManager
            .getLimitValueByIndex(this.entityExpansionIndex) }, (short)2);
      this.fEntityExpansionCount = 0;
    } 
    if (this.fEntityHandler != null)
      this.fEntityHandler.startEntity(name, this.fResourceIdentifier, encoding, null); 
  }
  
  public Entity.ScannedEntity getCurrentEntity() {
    return this.fCurrentEntity;
  }
  
  public Entity.ScannedEntity getTopLevelEntity() {
    return 
      this.fEntityStack.empty() ? null : this.fEntityStack.elementAt(0);
  }
  
  public void closeReaders() {}
  
  public void endEntity() throws IOException, XNIException {
    Entity.ScannedEntity entity = (this.fEntityStack.size() > 0) ? this.fEntityStack.pop() : null;
    if (this.fCurrentEntity != null)
      try {
        if (this.fLimitAnalyzer != null) {
          this.fLimitAnalyzer.endEntity(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, this.fCurrentEntity.name);
          if (this.fCurrentEntity.name.equals("[xml]"))
            this.fSecurityManager.debugPrint(this.fLimitAnalyzer); 
        } 
        this.fCurrentEntity.close();
      } catch (IOException ex) {
        throw new XNIException(ex);
      }  
    if (this.fEntityHandler != null)
      if (entity == null) {
        this.fEntityAugs.removeAllItems();
        this.fEntityAugs.putItem("LAST_ENTITY", Boolean.TRUE);
        this.fEntityHandler.endEntity(this.fCurrentEntity.name, this.fEntityAugs);
        this.fEntityAugs.removeAllItems();
      } else {
        this.fEntityHandler.endEntity(this.fCurrentEntity.name, null);
      }  
    boolean documentEntity = (this.fCurrentEntity.name == XMLEntity);
    this.fCurrentEntity = entity;
    this.fEntityScanner.setCurrentEntity(this.fCurrentEntity);
    if ((((this.fCurrentEntity == null) ? 1 : 0) & (!documentEntity ? 1 : 0)) != 0)
      throw new EOFException(); 
  }
  
  public void reset(PropertyManager propertyManager) {
    this.fEntityStorage.reset(propertyManager);
    this.fEntityScanner.reset(propertyManager);
    this.fSymbolTable = (SymbolTable)propertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fErrorReporter = (XMLErrorReporter)propertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    try {
      this.fStaxEntityResolver = (StaxEntityResolverWrapper)propertyManager.getProperty("http://apache.org/xml/properties/internal/stax-entity-resolver");
    } catch (XMLConfigurationException e) {
      this.fStaxEntityResolver = null;
    } 
    this.fSupportDTD = ((Boolean)propertyManager.getProperty("javax.xml.stream.supportDTD")).booleanValue();
    this.fReplaceEntityReferences = ((Boolean)propertyManager.getProperty("javax.xml.stream.isReplacingEntityReferences")).booleanValue();
    this.fSupportExternalEntities = ((Boolean)propertyManager.getProperty("javax.xml.stream.isSupportingExternalEntities")).booleanValue();
    this.fLoadExternalDTD = !((Boolean)propertyManager.getProperty("http://java.sun.com/xml/stream/properties/ignore-external-dtd")).booleanValue();
    XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)propertyManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
    this.fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    this.fSecurityManager = (XMLSecurityManager)propertyManager.getProperty("http://apache.org/xml/properties/security-manager");
    this.fEntities.clear();
    this.fEntityStack.removeAllElements();
    this.fCurrentEntity = null;
    this.fValidation = false;
    this.fExternalGeneralEntities = true;
    this.fExternalParameterEntities = true;
    this.fAllowJavaEncodings = true;
  }
  
  public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
    boolean parser_settings = componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
    if (!parser_settings) {
      reset();
      if (this.fEntityScanner != null)
        this.fEntityScanner.reset(componentManager); 
      if (this.fEntityStorage != null)
        this.fEntityStorage.reset(componentManager); 
      return;
    } 
    this.fValidation = componentManager.getFeature("http://xml.org/sax/features/validation", false);
    this.fExternalGeneralEntities = componentManager.getFeature("http://xml.org/sax/features/external-general-entities", true);
    this.fExternalParameterEntities = componentManager.getFeature("http://xml.org/sax/features/external-parameter-entities", true);
    this.fAllowJavaEncodings = componentManager.getFeature("http://apache.org/xml/features/allow-java-encodings", false);
    this.fWarnDuplicateEntityDef = componentManager.getFeature("http://apache.org/xml/features/warn-on-duplicate-entitydef", false);
    this.fStrictURI = componentManager.getFeature("http://apache.org/xml/features/standard-uri-conformant", false);
    this.fLoadExternalDTD = componentManager.getFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
    this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fEntityResolver = (XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver", null);
    this.fStaxEntityResolver = (StaxEntityResolverWrapper)componentManager.getProperty("http://apache.org/xml/properties/internal/stax-entity-resolver", null);
    this.fValidationManager = (ValidationManager)componentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager", null);
    this.fSecurityManager = (XMLSecurityManager)componentManager.getProperty("http://apache.org/xml/properties/security-manager", null);
    this.entityExpansionIndex = this.fSecurityManager.getIndex("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit");
    this.fSupportDTD = true;
    this.fReplaceEntityReferences = true;
    this.fSupportExternalEntities = true;
    XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)componentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", null);
    if (spm == null)
      spm = new XMLSecurityPropertyManager(); 
    this.fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    reset();
    this.fEntityScanner.reset(componentManager);
    this.fEntityStorage.reset(componentManager);
  }
  
  public void reset() {
    this.fStandalone = false;
    this.fEntities.clear();
    this.fEntityStack.removeAllElements();
    this.fEntityExpansionCount = 0;
    this.fCurrentEntity = null;
    if (this.fXML10EntityScanner != null)
      this.fXML10EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter); 
    if (this.fXML11EntityScanner != null)
      this.fXML11EntityScanner.reset(this.fSymbolTable, this, this.fErrorReporter); 
    this.fEntityHandler = null;
  }
  
  public String[] getRecognizedFeatures() {
    return (String[])RECOGNIZED_FEATURES.clone();
  }
  
  public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
    if (featureId.startsWith("http://apache.org/xml/features/")) {
      int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
      if (suffixLength == "allow-java-encodings".length() && featureId
        .endsWith("allow-java-encodings"))
        this.fAllowJavaEncodings = state; 
      if (suffixLength == "nonvalidating/load-external-dtd".length() && featureId
        .endsWith("nonvalidating/load-external-dtd")) {
        this.fLoadExternalDTD = state;
        return;
      } 
    } 
  }
  
  public void setProperty(String propertyId, Object value) {
    if (propertyId.startsWith("http://apache.org/xml/properties/")) {
      int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
      if (suffixLength == "internal/symbol-table".length() && propertyId
        .endsWith("internal/symbol-table")) {
        this.fSymbolTable = (SymbolTable)value;
        return;
      } 
      if (suffixLength == "internal/error-reporter".length() && propertyId
        .endsWith("internal/error-reporter")) {
        this.fErrorReporter = (XMLErrorReporter)value;
        return;
      } 
      if (suffixLength == "internal/entity-resolver".length() && propertyId
        .endsWith("internal/entity-resolver")) {
        this.fEntityResolver = (XMLEntityResolver)value;
        return;
      } 
      if (suffixLength == "input-buffer-size".length() && propertyId
        .endsWith("input-buffer-size")) {
        Integer bufferSize = (Integer)value;
        if (bufferSize != null && bufferSize
          .intValue() > 64) {
          this.fBufferSize = bufferSize.intValue();
          this.fEntityScanner.setBufferSize(this.fBufferSize);
          this.fBufferPool.setExternalBufferSize(this.fBufferSize);
        } 
      } 
      if (suffixLength == "security-manager".length() && propertyId
        .endsWith("security-manager"))
        this.fSecurityManager = (XMLSecurityManager)value; 
    } 
    if (propertyId.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
      XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)value;
      this.fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
    } 
  }
  
  public void setLimitAnalyzer(XMLLimitAnalyzer fLimitAnalyzer) {
    this.fLimitAnalyzer = fLimitAnalyzer;
  }
  
  public String[] getRecognizedProperties() {
    return (String[])RECOGNIZED_PROPERTIES.clone();
  }
  
  public Boolean getFeatureDefault(String featureId) {
    for (int i = 0; i < RECOGNIZED_FEATURES.length; i++) {
      if (RECOGNIZED_FEATURES[i].equals(featureId))
        return FEATURE_DEFAULTS[i]; 
    } 
    return null;
  }
  
  public Object getPropertyDefault(String propertyId) {
    for (int i = 0; i < RECOGNIZED_PROPERTIES.length; i++) {
      if (RECOGNIZED_PROPERTIES[i].equals(propertyId))
        return PROPERTY_DEFAULTS[i]; 
    } 
    return null;
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
  
  private static synchronized URI getUserDir() throws URI.MalformedURIException {
    String userDir = "";
    try {
      userDir = SecuritySupport.getSystemProperty("user.dir");
    } catch (SecurityException securityException) {}
    if (userDir.length() == 0)
      return new URI("file", "", "", null, null); 
    if (gUserDirURI != null && userDir.equals(gUserDir))
      return gUserDirURI; 
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
        return new URI("file", "", userDir, null, null);
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
    gUserDirURI = new URI("file", "", buffer.toString(), null, null);
    return gUserDirURI;
  }
  
  public static void absolutizeAgainstUserDir(URI uri) throws URI.MalformedURIException {
    uri.absolutize(getUserDir());
  }
  
  public static String expandSystemId(String systemId, String baseSystemId) {
    if (systemId == null || systemId.length() == 0)
      return systemId; 
    try {
      URI uRI = new URI(systemId);
      if (uRI != null)
        return systemId; 
    } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {}
    String id = fixURI(systemId);
    URI base = null;
    URI uri = null;
    try {
      if (baseSystemId == null || baseSystemId.length() == 0 || baseSystemId
        .equals(systemId)) {
        String dir = getUserDir().toString();
        base = new URI("file", "", dir, null, null);
      } else {
        try {
          base = new URI(fixURI(baseSystemId));
        } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException e) {
          if (baseSystemId.indexOf(':') != -1) {
            base = new URI("file", "", fixURI(baseSystemId), null, null);
          } else {
            String dir = getUserDir().toString();
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
  
  public static String expandSystemId(String systemId, String baseSystemId, boolean strict) throws URI.MalformedURIException {
    if (systemId == null)
      return null; 
    if (strict) {
      if (systemId == null)
        return null; 
      try {
        new URI(systemId);
        return systemId;
      } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {
        URI base = null;
        if (baseSystemId == null || baseSystemId.length() == 0) {
          base = new URI("file", "", getUserDir().toString(), null, null);
        } else {
          try {
            base = new URI(baseSystemId);
          } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException e) {
            String dir = getUserDir().toString();
            dir = dir + baseSystemId;
            base = new URI("file", "", dir, null, null);
          } 
        } 
        URI uri = new URI(base, systemId);
        return uri.toString();
      } 
    } 
    try {
      return expandSystemIdStrictOff(systemId, baseSystemId);
    } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException e) {
      try {
        return expandSystemIdStrictOff1(systemId, baseSystemId);
      } catch (URISyntaxException uRISyntaxException) {
        if (systemId.length() == 0)
          return systemId; 
        String id = fixURI(systemId);
        URI base = null;
        URI uri = null;
        try {
          if (baseSystemId == null || baseSystemId.length() == 0 || baseSystemId
            .equals(systemId)) {
            base = getUserDir();
          } else {
            try {
              base = new URI(fixURI(baseSystemId).trim());
            } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {
              if (baseSystemId.indexOf(':') != -1) {
                base = new URI("file", "", fixURI(baseSystemId).trim(), null, null);
              } else {
                base = new URI(getUserDir(), fixURI(baseSystemId));
              } 
            } 
          } 
          uri = new URI(base, id.trim());
        } catch (Exception exception) {}
        if (uri == null)
          return systemId; 
        return uri.toString();
      } 
    } 
  }
  
  private static String expandSystemIdStrictOn(String systemId, String baseSystemId) throws URI.MalformedURIException {
    URI systemURI = new URI(systemId, true);
    if (systemURI.isAbsoluteURI())
      return systemId; 
    URI baseURI = null;
    if (baseSystemId == null || baseSystemId.length() == 0) {
      baseURI = getUserDir();
    } else {
      baseURI = new URI(baseSystemId, true);
      if (!baseURI.isAbsoluteURI())
        baseURI.absolutize(getUserDir()); 
    } 
    systemURI.absolutize(baseURI);
    return systemURI.toString();
  }
  
  public static void setInstanceFollowRedirects(HttpURLConnection urlCon, boolean followRedirects) {
    try {
      Method method = HttpURLConnection.class.getMethod("setInstanceFollowRedirects", new Class[] { boolean.class });
      method.invoke(urlCon, new Object[] { followRedirects ? Boolean.TRUE : Boolean.FALSE });
    } catch (Exception exception) {}
  }
  
  private static String expandSystemIdStrictOff(String systemId, String baseSystemId) throws URI.MalformedURIException {
    URI systemURI = new URI(systemId, true);
    if (systemURI.isAbsoluteURI()) {
      if (systemURI.getScheme().length() > 1)
        return systemId; 
      throw new URI.MalformedURIException();
    } 
    URI baseURI = null;
    if (baseSystemId == null || baseSystemId.length() == 0) {
      baseURI = getUserDir();
    } else {
      baseURI = new URI(baseSystemId, true);
      if (!baseURI.isAbsoluteURI())
        baseURI.absolutize(getUserDir()); 
    } 
    systemURI.absolutize(baseURI);
    return systemURI.toString();
  }
  
  private static String expandSystemIdStrictOff1(String systemId, String baseSystemId) throws URISyntaxException, URI.MalformedURIException {
    URI systemURI = new URI(systemId);
    if (systemURI.isAbsolute()) {
      if (systemURI.getScheme().length() > 1)
        return systemId; 
      throw new URISyntaxException(systemId, "the scheme's length is only one character");
    } 
    URI baseURI = null;
    if (baseSystemId == null || baseSystemId.length() == 0) {
      baseURI = getUserDir();
    } else {
      baseURI = new URI(baseSystemId, true);
      if (!baseURI.isAbsoluteURI())
        baseURI.absolutize(getUserDir()); 
    } 
    systemURI = (new URI(baseURI.toString())).resolve(systemURI);
    return systemURI.toString();
  }
  
  protected Object[] getEncodingName(byte[] b4, int count) {
    if (count < 2)
      return this.defaultEncoding; 
    int b0 = b4[0] & 0xFF;
    int b1 = b4[1] & 0xFF;
    if (b0 == 254 && b1 == 255)
      return new Object[] { "UTF-16BE", new Boolean(true) }; 
    if (b0 == 255 && b1 == 254)
      return new Object[] { "UTF-16LE", new Boolean(false) }; 
    if (count < 3)
      return this.defaultEncoding; 
    int b2 = b4[2] & 0xFF;
    if (b0 == 239 && b1 == 187 && b2 == 191)
      return this.defaultEncoding; 
    if (count < 4)
      return this.defaultEncoding; 
    int b3 = b4[3] & 0xFF;
    if (b0 == 0 && b1 == 0 && b2 == 0 && b3 == 60)
      return new Object[] { "ISO-10646-UCS-4", new Boolean(true) }; 
    if (b0 == 60 && b1 == 0 && b2 == 0 && b3 == 0)
      return new Object[] { "ISO-10646-UCS-4", new Boolean(false) }; 
    if (b0 == 0 && b1 == 0 && b2 == 60 && b3 == 0)
      return new Object[] { "ISO-10646-UCS-4", null }; 
    if (b0 == 0 && b1 == 60 && b2 == 0 && b3 == 0)
      return new Object[] { "ISO-10646-UCS-4", null }; 
    if (b0 == 0 && b1 == 60 && b2 == 0 && b3 == 63)
      return new Object[] { "UTF-16BE", new Boolean(true) }; 
    if (b0 == 60 && b1 == 0 && b2 == 63 && b3 == 0)
      return new Object[] { "UTF-16LE", new Boolean(false) }; 
    if (b0 == 76 && b1 == 111 && b2 == 167 && b3 == 148)
      return new Object[] { "CP037", null }; 
    return this.defaultEncoding;
  }
  
  protected Reader createReader(InputStream inputStream, String encoding, Boolean isBigEndian) throws IOException {
    if (encoding == null)
      encoding = "UTF-8"; 
    String ENCODING = encoding.toUpperCase(Locale.ENGLISH);
    if (ENCODING.equals("UTF-8"))
      return new UTF8Reader(inputStream, this.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale()); 
    if (ENCODING.equals("US-ASCII"))
      return new ASCIIReader(inputStream, this.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale()); 
    if (ENCODING.equals("ISO-10646-UCS-4")) {
      if (isBigEndian != null) {
        boolean isBE = isBigEndian.booleanValue();
        if (isBE)
          return new UCSReader(inputStream, (short)8); 
        return new UCSReader(inputStream, (short)4);
      } 
      this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { encoding }, (short)2);
    } 
    if (ENCODING.equals("ISO-10646-UCS-2")) {
      if (isBigEndian != null) {
        boolean isBE = isBigEndian.booleanValue();
        if (isBE)
          return new UCSReader(inputStream, (short)2); 
        return new UCSReader(inputStream, (short)1);
      } 
      this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { encoding }, (short)2);
    } 
    boolean validIANA = XMLChar.isValidIANAEncoding(encoding);
    boolean validJava = XMLChar.isValidJavaEncoding(encoding);
    if (!validIANA || (this.fAllowJavaEncodings && !validJava)) {
      this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { encoding }, (short)2);
      encoding = "ISO-8859-1";
    } 
    String javaEncoding = EncodingMap.getIANA2JavaMapping(ENCODING);
    if (javaEncoding == null)
      if (this.fAllowJavaEncodings) {
        javaEncoding = encoding;
      } else {
        this.fErrorReporter.reportError(getEntityScanner(), "http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { encoding }, (short)2);
        javaEncoding = "ISO8859_1";
      }  
    return new BufferedReader(new InputStreamReader(inputStream, javaEncoding));
  }
  
  public String getPublicId() {
    return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getPublicId() : null;
  }
  
  public String getExpandedSystemId() {
    if (this.fCurrentEntity != null) {
      if (this.fCurrentEntity.entityLocation != null && this.fCurrentEntity.entityLocation
        .getExpandedSystemId() != null)
        return this.fCurrentEntity.entityLocation.getExpandedSystemId(); 
      int size = this.fEntityStack.size();
      for (int i = size - 1; i >= 0; i--) {
        Entity.ScannedEntity externalEntity = this.fEntityStack.elementAt(i);
        if (externalEntity.entityLocation != null && externalEntity.entityLocation
          .getExpandedSystemId() != null)
          return externalEntity.entityLocation.getExpandedSystemId(); 
      } 
    } 
    return null;
  }
  
  public String getLiteralSystemId() {
    if (this.fCurrentEntity != null) {
      if (this.fCurrentEntity.entityLocation != null && this.fCurrentEntity.entityLocation
        .getLiteralSystemId() != null)
        return this.fCurrentEntity.entityLocation.getLiteralSystemId(); 
      int size = this.fEntityStack.size();
      for (int i = size - 1; i >= 0; i--) {
        Entity.ScannedEntity externalEntity = this.fEntityStack.elementAt(i);
        if (externalEntity.entityLocation != null && externalEntity.entityLocation
          .getLiteralSystemId() != null)
          return externalEntity.entityLocation.getLiteralSystemId(); 
      } 
    } 
    return null;
  }
  
  public int getLineNumber() {
    if (this.fCurrentEntity != null) {
      if (this.fCurrentEntity.isExternal())
        return this.fCurrentEntity.lineNumber; 
      int size = this.fEntityStack.size();
      for (int i = size - 1; i > 0; i--) {
        Entity.ScannedEntity firstExternalEntity = this.fEntityStack.elementAt(i);
        if (firstExternalEntity.isExternal())
          return firstExternalEntity.lineNumber; 
      } 
    } 
    return -1;
  }
  
  public int getColumnNumber() {
    if (this.fCurrentEntity != null) {
      if (this.fCurrentEntity.isExternal())
        return this.fCurrentEntity.columnNumber; 
      int size = this.fEntityStack.size();
      for (int i = size - 1; i > 0; i--) {
        Entity.ScannedEntity firstExternalEntity = this.fEntityStack.elementAt(i);
        if (firstExternalEntity.isExternal())
          return firstExternalEntity.columnNumber; 
      } 
    } 
    return -1;
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
    int pos = str.indexOf(' ');
    if (pos >= 0) {
      StringBuilder sb = new StringBuilder(str.length());
      int i;
      for (i = 0; i < pos; i++)
        sb.append(str.charAt(i)); 
      sb.append("%20");
      for (i = pos + 1; i < str.length(); i++) {
        if (str.charAt(i) == ' ') {
          sb.append("%20");
        } else {
          sb.append(str.charAt(i));
        } 
      } 
      str = sb.toString();
    } 
    return str;
  }
  
  final void print() {}
  
  private static class CharacterBuffer {
    private char[] ch;
    
    private boolean isExternal;
    
    public CharacterBuffer(boolean isExternal, int size) {
      this.isExternal = isExternal;
      this.ch = new char[size];
    }
  }
  
  private static class CharacterBufferPool {
    private static final int DEFAULT_POOL_SIZE = 3;
    
    private XMLEntityManager.CharacterBuffer[] fInternalBufferPool;
    
    private XMLEntityManager.CharacterBuffer[] fExternalBufferPool;
    
    private int fExternalBufferSize;
    
    private int fInternalBufferSize;
    
    private int poolSize;
    
    private int fInternalTop;
    
    private int fExternalTop;
    
    public CharacterBufferPool(int externalBufferSize, int internalBufferSize) {
      this(3, externalBufferSize, internalBufferSize);
    }
    
    public CharacterBufferPool(int poolSize, int externalBufferSize, int internalBufferSize) {
      this.fExternalBufferSize = externalBufferSize;
      this.fInternalBufferSize = internalBufferSize;
      this.poolSize = poolSize;
      init();
    }
    
    private void init() {
      this.fInternalBufferPool = new XMLEntityManager.CharacterBuffer[this.poolSize];
      this.fExternalBufferPool = new XMLEntityManager.CharacterBuffer[this.poolSize];
      this.fInternalTop = -1;
      this.fExternalTop = -1;
    }
    
    public XMLEntityManager.CharacterBuffer getBuffer(boolean external) {
      if (external) {
        if (this.fExternalTop > -1)
          return this.fExternalBufferPool[this.fExternalTop--]; 
        return new XMLEntityManager.CharacterBuffer(true, this.fExternalBufferSize);
      } 
      if (this.fInternalTop > -1)
        return this.fInternalBufferPool[this.fInternalTop--]; 
      return new XMLEntityManager.CharacterBuffer(false, this.fInternalBufferSize);
    }
    
    public void returnToPool(XMLEntityManager.CharacterBuffer buffer) {
      if (buffer.isExternal) {
        if (this.fExternalTop < this.fExternalBufferPool.length - 1)
          this.fExternalBufferPool[++this.fExternalTop] = buffer; 
      } else if (this.fInternalTop < this.fInternalBufferPool.length - 1) {
        this.fInternalBufferPool[++this.fInternalTop] = buffer;
      } 
    }
    
    public void setExternalBufferSize(int bufferSize) {
      this.fExternalBufferSize = bufferSize;
      this.fExternalBufferPool = new XMLEntityManager.CharacterBuffer[this.poolSize];
      this.fExternalTop = -1;
    }
  }
  
  protected final class RewindableInputStream extends InputStream {
    private InputStream fInputStream;
    
    private byte[] fData;
    
    private int fStartOffset;
    
    private int fEndOffset;
    
    private int fOffset;
    
    private int fLength;
    
    private int fMark;
    
    public RewindableInputStream(InputStream is) {
      this.fData = new byte[64];
      this.fInputStream = is;
      this.fStartOffset = 0;
      this.fEndOffset = -1;
      this.fOffset = 0;
      this.fLength = 0;
      this.fMark = 0;
    }
    
    public void setStartOffset(int offset) {
      this.fStartOffset = offset;
    }
    
    public void rewind() {
      this.fOffset = this.fStartOffset;
    }
    
    public int read() throws IOException {
      int b = 0;
      if (this.fOffset < this.fLength)
        return this.fData[this.fOffset++] & 0xFF; 
      if (this.fOffset == this.fEndOffset)
        return -1; 
      if (this.fOffset == this.fData.length) {
        byte[] newData = new byte[this.fOffset << 1];
        System.arraycopy(this.fData, 0, newData, 0, this.fOffset);
        this.fData = newData;
      } 
      b = this.fInputStream.read();
      if (b == -1) {
        this.fEndOffset = this.fOffset;
        return -1;
      } 
      this.fData[this.fLength++] = (byte)b;
      this.fOffset++;
      return b & 0xFF;
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
      int bytesLeft = this.fLength - this.fOffset;
      if (bytesLeft == 0) {
        if (this.fOffset == this.fEndOffset)
          return -1; 
        if (XMLEntityManager.this.fCurrentEntity.mayReadChunks || !XMLEntityManager.this.fCurrentEntity.xmlDeclChunkRead) {
          if (!XMLEntityManager.this.fCurrentEntity.xmlDeclChunkRead) {
            XMLEntityManager.this.fCurrentEntity.xmlDeclChunkRead = true;
            len = 28;
          } 
          return this.fInputStream.read(b, off, len);
        } 
        int returnedVal = read();
        if (returnedVal == -1) {
          this.fEndOffset = this.fOffset;
          return -1;
        } 
        b[off] = (byte)returnedVal;
        return 1;
      } 
      if (len < bytesLeft) {
        if (len <= 0)
          return 0; 
      } else {
        len = bytesLeft;
      } 
      if (b != null)
        System.arraycopy(this.fData, this.fOffset, b, off, len); 
      this.fOffset += len;
      return len;
    }
    
    public long skip(long n) throws IOException {
      if (n <= 0L)
        return 0L; 
      int bytesLeft = this.fLength - this.fOffset;
      if (bytesLeft == 0) {
        if (this.fOffset == this.fEndOffset)
          return 0L; 
        return this.fInputStream.skip(n);
      } 
      if (n <= bytesLeft) {
        this.fOffset = (int)(this.fOffset + n);
        return n;
      } 
      this.fOffset += bytesLeft;
      if (this.fOffset == this.fEndOffset)
        return bytesLeft; 
      n -= bytesLeft;
      return this.fInputStream.skip(n) + bytesLeft;
    }
    
    public int available() throws IOException {
      int bytesLeft = this.fLength - this.fOffset;
      if (bytesLeft == 0) {
        if (this.fOffset == this.fEndOffset)
          return -1; 
        return XMLEntityManager.this.fCurrentEntity.mayReadChunks ? this.fInputStream.available() : 0;
      } 
      return bytesLeft;
    }
    
    public void mark(int howMuch) {
      this.fMark = this.fOffset;
    }
    
    public void reset() {
      this.fOffset = this.fMark;
    }
    
    public boolean markSupported() {
      return true;
    }
    
    public void close() throws IOException {
      if (this.fInputStream != null) {
        this.fInputStream.close();
        this.fInputStream = null;
      } 
    }
  }
  
  public void test() {
    this.fEntityStorage.addExternalEntity("entityUsecase1", null, "/space/home/stax/sun/6thJan2004/zephyr/data/test.txt", "/space/home/stax/sun/6thJan2004/zephyr/data/entity.xml");
    this.fEntityStorage.addInternalEntity("entityUsecase2", "<Test>value</Test>");
    this.fEntityStorage.addInternalEntity("entityUsecase3", "value3");
    this.fEntityStorage.addInternalEntity("text", "Hello World.");
    this.fEntityStorage.addInternalEntity("empty-element", "<foo/>");
    this.fEntityStorage.addInternalEntity("balanced-element", "<foo></foo>");
    this.fEntityStorage.addInternalEntity("balanced-element-with-text", "<foo>Hello, World</foo>");
    this.fEntityStorage.addInternalEntity("balanced-element-with-entity", "<foo>&text;</foo>");
    this.fEntityStorage.addInternalEntity("unbalanced-entity", "<foo>");
    this.fEntityStorage.addInternalEntity("recursive-entity", "<foo>&recursive-entity2;</foo>");
    this.fEntityStorage.addInternalEntity("recursive-entity2", "<bar>&recursive-entity3;</bar>");
    this.fEntityStorage.addInternalEntity("recursive-entity3", "<baz>&recursive-entity;</baz>");
    this.fEntityStorage.addInternalEntity("ch", "&#x00A9;");
    this.fEntityStorage.addInternalEntity("ch1", "&#84;");
    this.fEntityStorage.addInternalEntity("% ch2", "param");
  }
}
