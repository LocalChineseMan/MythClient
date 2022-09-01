package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.XMLEntityStorage;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.stream.events.XMLEvent;

public abstract class XMLScanner implements XMLComponent {
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  
  protected static final String VALIDATION = "http://xml.org/sax/features/validation";
  
  protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
  
  protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  protected static final boolean DEBUG_ATTR_NORMALIZATION = false;
  
  private boolean fNeedNonNormalizedValue = false;
  
  protected ArrayList attributeValueCache = new ArrayList();
  
  protected ArrayList stringBufferCache = new ArrayList();
  
  protected int fStringBufferIndex = 0;
  
  protected boolean fAttributeCacheInitDone = false;
  
  protected int fAttributeCacheUsedCount = 0;
  
  protected boolean fValidation = false;
  
  protected boolean fNamespaces;
  
  protected boolean fNotifyCharRefs = false;
  
  protected boolean fParserSettings = true;
  
  protected PropertyManager fPropertyManager = null;
  
  protected SymbolTable fSymbolTable;
  
  protected XMLErrorReporter fErrorReporter;
  
  protected XMLEntityManager fEntityManager = null;
  
  protected XMLEntityStorage fEntityStore = null;
  
  protected XMLSecurityManager fSecurityManager = null;
  
  protected XMLLimitAnalyzer fLimitAnalyzer = null;
  
  protected XMLEvent fEvent;
  
  protected XMLEntityScanner fEntityScanner = null;
  
  protected int fEntityDepth;
  
  protected String fCharRefLiteral = null;
  
  protected boolean fScanningAttribute;
  
  protected boolean fReportEntity;
  
  protected static final String fVersionSymbol = "version".intern();
  
  protected static final String fEncodingSymbol = "encoding".intern();
  
  protected static final String fStandaloneSymbol = "standalone".intern();
  
  protected static final String fAmpSymbol = "amp".intern();
  
  protected static final String fLtSymbol = "lt".intern();
  
  protected static final String fGtSymbol = "gt".intern();
  
  protected static final String fQuotSymbol = "quot".intern();
  
  protected static final String fAposSymbol = "apos".intern();
  
  private XMLString fString = new XMLString();
  
  private XMLStringBuffer fStringBuffer = new XMLStringBuffer();
  
  private XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
  
  private XMLStringBuffer fStringBuffer3 = new XMLStringBuffer();
  
  protected XMLResourceIdentifierImpl fResourceIdentifier = new XMLResourceIdentifierImpl();
  
  int initialCacheCount = 6;
  
  public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
    this.fParserSettings = componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true);
    if (!this.fParserSettings) {
      init();
      return;
    } 
    this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fEntityManager = (XMLEntityManager)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
    this.fSecurityManager = (XMLSecurityManager)componentManager.getProperty("http://apache.org/xml/properties/security-manager");
    this.fEntityStore = this.fEntityManager.getEntityStore();
    this.fValidation = componentManager.getFeature("http://xml.org/sax/features/validation", false);
    this.fNamespaces = componentManager.getFeature("http://xml.org/sax/features/namespaces", true);
    this.fNotifyCharRefs = componentManager.getFeature("http://apache.org/xml/features/scanner/notify-char-refs", false);
    init();
  }
  
  protected void setPropertyManager(PropertyManager propertyManager) {
    this.fPropertyManager = propertyManager;
  }
  
  public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
    if (propertyId.startsWith("http://apache.org/xml/properties/")) {
      String property = propertyId.substring("http://apache.org/xml/properties/".length());
      if (property.equals("internal/symbol-table")) {
        this.fSymbolTable = (SymbolTable)value;
      } else if (property.equals("internal/error-reporter")) {
        this.fErrorReporter = (XMLErrorReporter)value;
      } else if (property.equals("internal/entity-manager")) {
        this.fEntityManager = (XMLEntityManager)value;
      } 
    } 
    if (propertyId.equals("http://apache.org/xml/properties/security-manager"))
      this.fSecurityManager = (XMLSecurityManager)value; 
  }
  
  public void setFeature(String featureId, boolean value) throws XMLConfigurationException {
    if ("http://xml.org/sax/features/validation".equals(featureId)) {
      this.fValidation = value;
    } else if ("http://apache.org/xml/features/scanner/notify-char-refs".equals(featureId)) {
      this.fNotifyCharRefs = value;
    } 
  }
  
  public boolean getFeature(String featureId) throws XMLConfigurationException {
    if ("http://xml.org/sax/features/validation".equals(featureId))
      return this.fValidation; 
    if ("http://apache.org/xml/features/scanner/notify-char-refs".equals(featureId))
      return this.fNotifyCharRefs; 
    throw new XMLConfigurationException(Status.NOT_RECOGNIZED, featureId);
  }
  
  protected void reset() {
    init();
    this.fValidation = true;
    this.fNotifyCharRefs = false;
  }
  
  public void reset(PropertyManager propertyManager) {
    init();
    this.fSymbolTable = (SymbolTable)propertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fErrorReporter = (XMLErrorReporter)propertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fEntityManager = (XMLEntityManager)propertyManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
    this.fEntityStore = this.fEntityManager.getEntityStore();
    this.fEntityScanner = this.fEntityManager.getEntityScanner();
    this.fSecurityManager = (XMLSecurityManager)propertyManager.getProperty("http://apache.org/xml/properties/security-manager");
    this.fValidation = false;
    this.fNotifyCharRefs = false;
  }
  
  protected void scanXMLDeclOrTextDecl(boolean scanningTextDecl, String[] pseudoAttributeValues) throws IOException, XNIException {
    String version = null;
    String encoding = null;
    String standalone = null;
    int STATE_VERSION = 0;
    int STATE_ENCODING = 1;
    int STATE_STANDALONE = 2;
    int STATE_DONE = 3;
    int state = 0;
    boolean dataFoundForTarget = false;
    boolean sawSpace = this.fEntityScanner.skipSpaces();
    Entity.ScannedEntity currEnt = this.fEntityManager.getCurrentEntity();
    boolean currLiteral = currEnt.literal;
    currEnt.literal = false;
    while (this.fEntityScanner.peekChar() != 63) {
      dataFoundForTarget = true;
      String name = scanPseudoAttribute(scanningTextDecl, this.fString);
      switch (state) {
        case 0:
          if (name.equals(fVersionSymbol)) {
            if (!sawSpace)
              reportFatalError(scanningTextDecl ? "SpaceRequiredBeforeVersionInTextDecl" : "SpaceRequiredBeforeVersionInXMLDecl", null); 
            version = this.fString.toString();
            state = 1;
            if (!versionSupported(version))
              reportFatalError("VersionNotSupported", new Object[] { version }); 
            if (version.equals("1.1")) {
              Entity.ScannedEntity top = this.fEntityManager.getTopLevelEntity();
              if (top != null && (top.version == null || top.version.equals("1.0")))
                reportFatalError("VersionMismatch", null); 
              this.fEntityManager.setScannerVersion((short)2);
            } 
            break;
          } 
          if (name.equals(fEncodingSymbol)) {
            if (!scanningTextDecl)
              reportFatalError("VersionInfoRequired", null); 
            if (!sawSpace)
              reportFatalError(scanningTextDecl ? "SpaceRequiredBeforeEncodingInTextDecl" : "SpaceRequiredBeforeEncodingInXMLDecl", null); 
            encoding = this.fString.toString();
            state = scanningTextDecl ? 3 : 2;
            break;
          } 
          if (scanningTextDecl) {
            reportFatalError("EncodingDeclRequired", null);
            break;
          } 
          reportFatalError("VersionInfoRequired", null);
          break;
        case 1:
          if (name.equals(fEncodingSymbol)) {
            if (!sawSpace)
              reportFatalError(scanningTextDecl ? "SpaceRequiredBeforeEncodingInTextDecl" : "SpaceRequiredBeforeEncodingInXMLDecl", null); 
            encoding = this.fString.toString();
            state = scanningTextDecl ? 3 : 2;
            break;
          } 
          if (!scanningTextDecl && name.equals(fStandaloneSymbol)) {
            if (!sawSpace)
              reportFatalError("SpaceRequiredBeforeStandalone", null); 
            standalone = this.fString.toString();
            state = 3;
            if (!standalone.equals("yes") && !standalone.equals("no"))
              reportFatalError("SDDeclInvalid", new Object[] { standalone }); 
            break;
          } 
          reportFatalError("EncodingDeclRequired", null);
          break;
        case 2:
          if (name.equals(fStandaloneSymbol)) {
            if (!sawSpace)
              reportFatalError("SpaceRequiredBeforeStandalone", null); 
            standalone = this.fString.toString();
            state = 3;
            if (!standalone.equals("yes") && !standalone.equals("no"))
              reportFatalError("SDDeclInvalid", new Object[] { standalone }); 
            break;
          } 
          reportFatalError("SDDeclNameInvalid", null);
          break;
        default:
          reportFatalError("NoMorePseudoAttributes", null);
          break;
      } 
      sawSpace = this.fEntityScanner.skipSpaces();
    } 
    if (currLiteral)
      currEnt.literal = true; 
    if (scanningTextDecl && state != 3)
      reportFatalError("MorePseudoAttributes", null); 
    if (scanningTextDecl) {
      if (!dataFoundForTarget && encoding == null)
        reportFatalError("EncodingDeclRequired", null); 
    } else if (!dataFoundForTarget && version == null) {
      reportFatalError("VersionInfoRequired", null);
    } 
    if (!this.fEntityScanner.skipChar(63))
      reportFatalError("XMLDeclUnterminated", null); 
    if (!this.fEntityScanner.skipChar(62))
      reportFatalError("XMLDeclUnterminated", null); 
    pseudoAttributeValues[0] = version;
    pseudoAttributeValues[1] = encoding;
    pseudoAttributeValues[2] = standalone;
  }
  
  public String scanPseudoAttribute(boolean scanningTextDecl, XMLString value) throws IOException, XNIException {
    String name = scanPseudoAttributeName();
    if (name == null)
      reportFatalError("PseudoAttrNameExpected", null); 
    this.fEntityScanner.skipSpaces();
    if (!this.fEntityScanner.skipChar(61))
      reportFatalError(scanningTextDecl ? "EqRequiredInTextDecl" : "EqRequiredInXMLDecl", new Object[] { name }); 
    this.fEntityScanner.skipSpaces();
    int quote = this.fEntityScanner.peekChar();
    if (quote != 39 && quote != 34)
      reportFatalError(scanningTextDecl ? "QuoteRequiredInTextDecl" : "QuoteRequiredInXMLDecl", new Object[] { name }); 
    this.fEntityScanner.scanChar();
    int c = this.fEntityScanner.scanLiteral(quote, value);
    if (c != quote) {
      this.fStringBuffer2.clear();
      while (true) {
        this.fStringBuffer2.append(value);
        if (c != -1)
          if (c == 38 || c == 37 || c == 60 || c == 93) {
            this.fStringBuffer2.append((char)this.fEntityScanner.scanChar());
          } else if (XMLChar.isHighSurrogate(c)) {
            scanSurrogates(this.fStringBuffer2);
          } else if (isInvalidLiteral(c)) {
            String key = scanningTextDecl ? "InvalidCharInTextDecl" : "InvalidCharInXMLDecl";
            reportFatalError(key, new Object[] { Integer.toString(c, 16) });
            this.fEntityScanner.scanChar();
          }  
        c = this.fEntityScanner.scanLiteral(quote, value);
        if (c == quote) {
          this.fStringBuffer2.append(value);
          value.setValues(this.fStringBuffer2);
          break;
        } 
      } 
    } 
    if (!this.fEntityScanner.skipChar(quote))
      reportFatalError(scanningTextDecl ? "CloseQuoteMissingInTextDecl" : "CloseQuoteMissingInXMLDecl", new Object[] { name }); 
    return name;
  }
  
  private String scanPseudoAttributeName() throws IOException, XNIException {
    int ch = this.fEntityScanner.peekChar();
    switch (ch) {
      case 118:
        if (this.fEntityScanner.skipString(fVersionSymbol))
          return fVersionSymbol; 
        break;
      case 101:
        if (this.fEntityScanner.skipString(fEncodingSymbol))
          return fEncodingSymbol; 
        break;
      case 115:
        if (this.fEntityScanner.skipString(fStandaloneSymbol))
          return fStandaloneSymbol; 
        break;
    } 
    return null;
  }
  
  protected void scanPI(XMLStringBuffer data) throws IOException, XNIException {
    this.fReportEntity = false;
    String target = this.fEntityScanner.scanName();
    if (target == null)
      reportFatalError("PITargetRequired", null); 
    scanPIData(target, data);
    this.fReportEntity = true;
  }
  
  protected void scanPIData(String target, XMLStringBuffer data) throws IOException, XNIException {
    if (target.length() == 3) {
      char c0 = Character.toLowerCase(target.charAt(0));
      char c1 = Character.toLowerCase(target.charAt(1));
      char c2 = Character.toLowerCase(target.charAt(2));
      if (c0 == 'x' && c1 == 'm' && c2 == 'l')
        reportFatalError("ReservedPITarget", null); 
    } 
    if (!this.fEntityScanner.skipSpaces()) {
      if (this.fEntityScanner.skipString("?>"))
        return; 
      reportFatalError("SpaceRequiredInPI", null);
    } 
    if (this.fEntityScanner.scanData("?>", data))
      do {
        int c = this.fEntityScanner.peekChar();
        if (c == -1)
          continue; 
        if (XMLChar.isHighSurrogate(c)) {
          scanSurrogates(data);
        } else if (isInvalidLiteral(c)) {
          reportFatalError("InvalidCharInPI", new Object[] { Integer.toHexString(c) });
          this.fEntityScanner.scanChar();
        } 
      } while (this.fEntityScanner.scanData("?>", data)); 
  }
  
  protected void scanComment(XMLStringBuffer text) throws IOException, XNIException {
    text.clear();
    while (this.fEntityScanner.scanData("--", text)) {
      int c = this.fEntityScanner.peekChar();
      if (c != -1) {
        if (XMLChar.isHighSurrogate(c))
          scanSurrogates(text); 
        if (isInvalidLiteral(c)) {
          reportFatalError("InvalidCharInComment", new Object[] { Integer.toHexString(c) });
          this.fEntityScanner.scanChar();
        } 
      } 
    } 
    if (!this.fEntityScanner.skipChar(62))
      reportFatalError("DashDashInComment", null); 
  }
  
  protected void scanAttributeValue(XMLString value, XMLString nonNormalizedValue, String atName, XMLAttributes attributes, int attrIndex, boolean checkEntities, String eleName) throws IOException, XNIException {
    XMLStringBuffer stringBuffer = null;
    int quote = this.fEntityScanner.peekChar();
    if (quote != 39 && quote != 34)
      reportFatalError("OpenQuoteExpected", new Object[] { eleName, atName }); 
    this.fEntityScanner.scanChar();
    int entityDepth = this.fEntityDepth;
    int c = this.fEntityScanner.scanLiteral(quote, value);
    if (this.fNeedNonNormalizedValue) {
      this.fStringBuffer2.clear();
      this.fStringBuffer2.append(value);
    } 
    if (this.fEntityScanner.whiteSpaceLen > 0)
      normalizeWhitespace(value); 
    if (c != quote) {
      this.fScanningAttribute = true;
      stringBuffer = getStringBuffer();
      stringBuffer.clear();
      while (true) {
        stringBuffer.append(value);
        if (c == 38) {
          this.fEntityScanner.skipChar(38);
          if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue)
            this.fStringBuffer2.append('&'); 
          if (this.fEntityScanner.skipChar(35)) {
            int ch;
            if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue)
              this.fStringBuffer2.append('#'); 
            if (this.fNeedNonNormalizedValue) {
              ch = scanCharReferenceValue(stringBuffer, this.fStringBuffer2);
            } else {
              ch = scanCharReferenceValue(stringBuffer, null);
            } 
            if (ch != -1);
          } else {
            String entityName = this.fEntityScanner.scanName();
            if (entityName == null) {
              reportFatalError("NameRequiredInReference", null);
            } else if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
              this.fStringBuffer2.append(entityName);
            } 
            if (!this.fEntityScanner.skipChar(59)) {
              reportFatalError("SemicolonRequiredInReference", new Object[] { entityName });
            } else if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
              this.fStringBuffer2.append(';');
            } 
            if (entityName == fAmpSymbol) {
              stringBuffer.append('&');
            } else if (entityName == fAposSymbol) {
              stringBuffer.append('\'');
            } else if (entityName == fLtSymbol) {
              stringBuffer.append('<');
            } else if (entityName == fGtSymbol) {
              stringBuffer.append('>');
            } else if (entityName == fQuotSymbol) {
              stringBuffer.append('"');
            } else if (this.fEntityStore.isExternalEntity(entityName)) {
              reportFatalError("ReferenceToExternalEntity", new Object[] { entityName });
            } else {
              if (!this.fEntityStore.isDeclaredEntity(entityName))
                if (checkEntities) {
                  if (this.fValidation)
                    this.fErrorReporter.reportError(this.fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { entityName }, (short)1); 
                } else {
                  reportFatalError("EntityNotDeclared", new Object[] { entityName });
                }  
              this.fEntityManager.startEntity(entityName, true);
            } 
          } 
        } else if (c == 60) {
          reportFatalError("LessthanInAttValue", new Object[] { eleName, atName });
          this.fEntityScanner.scanChar();
          if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue)
            this.fStringBuffer2.append((char)c); 
        } else if (c == 37 || c == 93) {
          this.fEntityScanner.scanChar();
          stringBuffer.append((char)c);
          if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue)
            this.fStringBuffer2.append((char)c); 
        } else if (c == 10 || c == 13) {
          this.fEntityScanner.scanChar();
          stringBuffer.append(' ');
          if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue)
            this.fStringBuffer2.append('\n'); 
        } else if (c != -1 && XMLChar.isHighSurrogate(c)) {
          if (scanSurrogates(this.fStringBuffer3)) {
            stringBuffer.append(this.fStringBuffer3);
            if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue)
              this.fStringBuffer2.append(this.fStringBuffer3); 
          } 
        } else if (c != -1 && isInvalidLiteral(c)) {
          reportFatalError("InvalidCharInAttValue", new Object[] { eleName, atName, 
                Integer.toString(c, 16) });
          this.fEntityScanner.scanChar();
          if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue)
            this.fStringBuffer2.append((char)c); 
        } 
        c = this.fEntityScanner.scanLiteral(quote, value);
        if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue)
          this.fStringBuffer2.append(value); 
        if (this.fEntityScanner.whiteSpaceLen > 0)
          normalizeWhitespace(value); 
        if (c == quote && entityDepth == this.fEntityDepth) {
          stringBuffer.append(value);
          value.setValues(stringBuffer);
          this.fScanningAttribute = false;
          break;
        } 
      } 
    } 
    if (this.fNeedNonNormalizedValue)
      nonNormalizedValue.setValues(this.fStringBuffer2); 
    int cquote = this.fEntityScanner.scanChar();
    if (cquote != quote)
      reportFatalError("CloseQuoteExpected", new Object[] { eleName, atName }); 
  }
  
  protected void scanExternalID(String[] identifiers, boolean optionalSystemId) throws IOException, XNIException {
    String systemId = null;
    String publicId = null;
    if (this.fEntityScanner.skipString("PUBLIC")) {
      if (!this.fEntityScanner.skipSpaces())
        reportFatalError("SpaceRequiredAfterPUBLIC", null); 
      scanPubidLiteral(this.fString);
      publicId = this.fString.toString();
      if (!this.fEntityScanner.skipSpaces() && !optionalSystemId)
        reportFatalError("SpaceRequiredBetweenPublicAndSystem", null); 
    } 
    if (publicId != null || this.fEntityScanner.skipString("SYSTEM")) {
      if (publicId == null && !this.fEntityScanner.skipSpaces())
        reportFatalError("SpaceRequiredAfterSYSTEM", null); 
      int quote = this.fEntityScanner.peekChar();
      if (quote != 39 && quote != 34) {
        if (publicId != null && optionalSystemId) {
          identifiers[0] = null;
          identifiers[1] = publicId;
          return;
        } 
        reportFatalError("QuoteRequiredInSystemID", null);
      } 
      this.fEntityScanner.scanChar();
      XMLString ident = this.fString;
      if (this.fEntityScanner.scanLiteral(quote, ident) != quote) {
        this.fStringBuffer.clear();
        while (true) {
          this.fStringBuffer.append(ident);
          int c = this.fEntityScanner.peekChar();
          if (XMLChar.isMarkup(c) || c == 93) {
            this.fStringBuffer.append((char)this.fEntityScanner.scanChar());
          } else if (c != -1 && isInvalidLiteral(c)) {
            reportFatalError("InvalidCharInSystemID", new Object[] { Integer.toString(c, 16) });
          } 
          if (this.fEntityScanner.scanLiteral(quote, ident) == quote) {
            this.fStringBuffer.append(ident);
            ident = this.fStringBuffer;
            break;
          } 
        } 
      } 
      systemId = ident.toString();
      if (!this.fEntityScanner.skipChar(quote))
        reportFatalError("SystemIDUnterminated", null); 
    } 
    identifiers[0] = systemId;
    identifiers[1] = publicId;
  }
  
  protected boolean scanPubidLiteral(XMLString literal) throws IOException, XNIException {
    int quote = this.fEntityScanner.scanChar();
    if (quote != 39 && quote != 34) {
      reportFatalError("QuoteRequiredInPublicID", null);
      return false;
    } 
    this.fStringBuffer.clear();
    boolean skipSpace = true;
    boolean dataok = true;
    while (true) {
      int c = this.fEntityScanner.scanChar();
      if (c == 32 || c == 10 || c == 13) {
        if (!skipSpace) {
          this.fStringBuffer.append(' ');
          skipSpace = true;
        } 
        continue;
      } 
      if (c == quote) {
        if (skipSpace)
          this.fStringBuffer.length--; 
        literal.setValues(this.fStringBuffer);
        break;
      } 
      if (XMLChar.isPubid(c)) {
        this.fStringBuffer.append((char)c);
        skipSpace = false;
        continue;
      } 
      if (c == -1) {
        reportFatalError("PublicIDUnterminated", null);
        return false;
      } 
      dataok = false;
      reportFatalError("InvalidCharInPublicID", new Object[] { Integer.toHexString(c) });
    } 
    return dataok;
  }
  
  protected void normalizeWhitespace(XMLString value) {
    int i = 0;
    int j = 0;
    int[] buff = this.fEntityScanner.whiteSpaceLookup;
    int buffLen = this.fEntityScanner.whiteSpaceLen;
    int end = value.offset + value.length;
    while (i < buffLen) {
      j = buff[i];
      if (j < end)
        value.ch[j] = ' '; 
      i++;
    } 
  }
  
  public void startEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
    this.fEntityDepth++;
    this.fEntityScanner = this.fEntityManager.getEntityScanner();
    this.fEntityStore = this.fEntityManager.getEntityStore();
  }
  
  public void endEntity(String name, Augmentations augs) throws IOException, XNIException {
    this.fEntityDepth--;
  }
  
  protected int scanCharReferenceValue(XMLStringBuffer buf, XMLStringBuffer buf2) throws IOException, XNIException {
    boolean hex = false;
    if (this.fEntityScanner.skipChar(120)) {
      if (buf2 != null)
        buf2.append('x'); 
      hex = true;
      this.fStringBuffer3.clear();
      boolean digit = true;
      int c = this.fEntityScanner.peekChar();
      digit = ((c >= 48 && c <= 57) || (c >= 97 && c <= 102) || (c >= 65 && c <= 70));
      if (digit) {
        if (buf2 != null)
          buf2.append((char)c); 
        this.fEntityScanner.scanChar();
        this.fStringBuffer3.append((char)c);
        do {
          c = this.fEntityScanner.peekChar();
          digit = ((c >= 48 && c <= 57) || (c >= 97 && c <= 102) || (c >= 65 && c <= 70));
          if (!digit)
            continue; 
          if (buf2 != null)
            buf2.append((char)c); 
          this.fEntityScanner.scanChar();
          this.fStringBuffer3.append((char)c);
        } while (digit);
      } else {
        reportFatalError("HexdigitRequiredInCharRef", null);
      } 
    } else {
      this.fStringBuffer3.clear();
      boolean digit = true;
      int c = this.fEntityScanner.peekChar();
      digit = (c >= 48 && c <= 57);
      if (digit) {
        if (buf2 != null)
          buf2.append((char)c); 
        this.fEntityScanner.scanChar();
        this.fStringBuffer3.append((char)c);
        do {
          c = this.fEntityScanner.peekChar();
          digit = (c >= 48 && c <= 57);
          if (!digit)
            continue; 
          if (buf2 != null)
            buf2.append((char)c); 
          this.fEntityScanner.scanChar();
          this.fStringBuffer3.append((char)c);
        } while (digit);
      } else {
        reportFatalError("DigitRequiredInCharRef", null);
      } 
    } 
    if (!this.fEntityScanner.skipChar(59))
      reportFatalError("SemicolonRequiredInCharRef", null); 
    if (buf2 != null)
      buf2.append(';'); 
    int value = -1;
    try {
      value = Integer.parseInt(this.fStringBuffer3.toString(), hex ? 16 : 10);
      if (isInvalid(value)) {
        StringBuffer errorBuf = new StringBuffer(this.fStringBuffer3.length + 1);
        if (hex)
          errorBuf.append('x'); 
        errorBuf.append(this.fStringBuffer3.ch, this.fStringBuffer3.offset, this.fStringBuffer3.length);
        reportFatalError("InvalidCharRef", new Object[] { errorBuf
              .toString() });
      } 
    } catch (NumberFormatException e) {
      StringBuffer errorBuf = new StringBuffer(this.fStringBuffer3.length + 1);
      if (hex)
        errorBuf.append('x'); 
      errorBuf.append(this.fStringBuffer3.ch, this.fStringBuffer3.offset, this.fStringBuffer3.length);
      reportFatalError("InvalidCharRef", new Object[] { errorBuf
            .toString() });
    } 
    if (!XMLChar.isSupplemental(value)) {
      buf.append((char)value);
    } else {
      buf.append(XMLChar.highSurrogate(value));
      buf.append(XMLChar.lowSurrogate(value));
    } 
    if (this.fNotifyCharRefs && value != -1) {
      String literal = "#" + (hex ? "x" : "") + this.fStringBuffer3.toString();
      if (!this.fScanningAttribute)
        this.fCharRefLiteral = literal; 
    } 
    return value;
  }
  
  protected boolean isInvalid(int value) {
    return XMLChar.isInvalid(value);
  }
  
  protected boolean isInvalidLiteral(int value) {
    return XMLChar.isInvalid(value);
  }
  
  protected boolean isValidNameChar(int value) {
    return XMLChar.isName(value);
  }
  
  protected boolean isValidNCName(int value) {
    return XMLChar.isNCName(value);
  }
  
  protected boolean isValidNameStartChar(int value) {
    return XMLChar.isNameStart(value);
  }
  
  protected boolean versionSupported(String version) {
    return (version.equals("1.0") || version.equals("1.1"));
  }
  
  protected boolean scanSurrogates(XMLStringBuffer buf) throws IOException, XNIException {
    int high = this.fEntityScanner.scanChar();
    int low = this.fEntityScanner.peekChar();
    if (!XMLChar.isLowSurrogate(low)) {
      reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(high, 16) });
      return false;
    } 
    this.fEntityScanner.scanChar();
    int c = XMLChar.supplemental((char)high, (char)low);
    if (isInvalid(c)) {
      reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(c, 16) });
      return false;
    } 
    buf.append((char)high);
    buf.append((char)low);
    return true;
  }
  
  protected void reportFatalError(String msgId, Object[] args) throws XNIException {
    this.fErrorReporter.reportError(this.fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", msgId, args, (short)2);
  }
  
  private void init() {
    this.fEntityScanner = null;
    this.fEntityDepth = 0;
    this.fReportEntity = true;
    this.fResourceIdentifier.clear();
    if (!this.fAttributeCacheInitDone) {
      for (int i = 0; i < this.initialCacheCount; i++) {
        this.attributeValueCache.add(new XMLString());
        this.stringBufferCache.add(new XMLStringBuffer());
      } 
      this.fAttributeCacheInitDone = true;
    } 
    this.fStringBufferIndex = 0;
    this.fAttributeCacheUsedCount = 0;
  }
  
  XMLStringBuffer getStringBuffer() {
    if (this.fStringBufferIndex < this.initialCacheCount || this.fStringBufferIndex < this.stringBufferCache.size())
      return this.stringBufferCache.get(this.fStringBufferIndex++); 
    XMLStringBuffer tmpObj = new XMLStringBuffer();
    this.fStringBufferIndex++;
    this.stringBufferCache.add(tmpObj);
    return tmpObj;
  }
}
