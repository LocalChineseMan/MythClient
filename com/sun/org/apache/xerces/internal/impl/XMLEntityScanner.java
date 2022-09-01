package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import com.sun.org.apache.xerces.internal.impl.io.UCSReader;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.XMLBufferListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;
import java.util.Vector;

public class XMLEntityScanner implements XMLLocator {
  protected Entity.ScannedEntity fCurrentEntity = null;
  
  protected int fBufferSize = 8192;
  
  protected XMLEntityManager fEntityManager;
  
  private static final boolean DEBUG_ENCODINGS = false;
  
  private Vector listeners = new Vector();
  
  private static final boolean[] VALID_NAMES = new boolean[127];
  
  private static final boolean DEBUG_BUFFER = false;
  
  private static final boolean DEBUG_SKIP_STRING = false;
  
  private static final EOFException END_OF_DOCUMENT_ENTITY = new EOFException() {
      private static final long serialVersionUID = 980337771224675268L;
      
      public Throwable fillInStackTrace() {
        return this;
      }
    };
  
  protected SymbolTable fSymbolTable = null;
  
  protected XMLErrorReporter fErrorReporter = null;
  
  int[] whiteSpaceLookup = new int[100];
  
  int whiteSpaceLen = 0;
  
  boolean whiteSpaceInfoNeeded = true;
  
  protected boolean fAllowJavaEncodings;
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
  
  protected PropertyManager fPropertyManager = null;
  
  boolean isExternal = false;
  
  static {
    int i;
    for (i = 65; i <= 90; i++)
      VALID_NAMES[i] = true; 
    for (i = 97; i <= 122; i++)
      VALID_NAMES[i] = true; 
    for (i = 48; i <= 57; i++)
      VALID_NAMES[i] = true; 
    VALID_NAMES[45] = true;
    VALID_NAMES[46] = true;
    VALID_NAMES[58] = true;
    VALID_NAMES[95] = true;
  }
  
  boolean xmlVersionSetExplicitly = false;
  
  public XMLEntityScanner(PropertyManager propertyManager, XMLEntityManager entityManager) {
    this.fEntityManager = entityManager;
    reset(propertyManager);
  }
  
  public final void setBufferSize(int size) {
    this.fBufferSize = size;
  }
  
  public void reset(PropertyManager propertyManager) {
    this.fSymbolTable = (SymbolTable)propertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fErrorReporter = (XMLErrorReporter)propertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fCurrentEntity = null;
    this.whiteSpaceLen = 0;
    this.whiteSpaceInfoNeeded = true;
    this.listeners.clear();
  }
  
  public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
    this.fAllowJavaEncodings = componentManager.getFeature("http://apache.org/xml/features/allow-java-encodings", false);
    this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fCurrentEntity = null;
    this.whiteSpaceLen = 0;
    this.whiteSpaceInfoNeeded = true;
    this.listeners.clear();
  }
  
  public final void reset(SymbolTable symbolTable, XMLEntityManager entityManager, XMLErrorReporter reporter) {
    this.fCurrentEntity = null;
    this.fSymbolTable = symbolTable;
    this.fEntityManager = entityManager;
    this.fErrorReporter = reporter;
  }
  
  public final String getXMLVersion() {
    if (this.fCurrentEntity != null)
      return this.fCurrentEntity.xmlVersion; 
    return null;
  }
  
  public final void setXMLVersion(String xmlVersion) {
    this.xmlVersionSetExplicitly = true;
    this.fCurrentEntity.xmlVersion = xmlVersion;
  }
  
  public final void setCurrentEntity(Entity.ScannedEntity scannedEntity) {
    this.fCurrentEntity = scannedEntity;
    if (this.fCurrentEntity != null)
      this.isExternal = this.fCurrentEntity.isExternal(); 
  }
  
  public Entity.ScannedEntity getCurrentEntity() {
    return this.fCurrentEntity;
  }
  
  public final String getBaseSystemId() {
    return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getExpandedSystemId() : null;
  }
  
  public void setBaseSystemId(String systemId) {}
  
  public final int getLineNumber() {
    return (this.fCurrentEntity != null) ? this.fCurrentEntity.lineNumber : -1;
  }
  
  public void setLineNumber(int line) {}
  
  public final int getColumnNumber() {
    return (this.fCurrentEntity != null) ? this.fCurrentEntity.columnNumber : -1;
  }
  
  public void setColumnNumber(int col) {}
  
  public final int getCharacterOffset() {
    return (this.fCurrentEntity != null) ? (this.fCurrentEntity.fTotalCountTillLastLoad + this.fCurrentEntity.position) : -1;
  }
  
  public final String getExpandedSystemId() {
    return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getExpandedSystemId() : null;
  }
  
  public void setExpandedSystemId(String systemId) {}
  
  public final String getLiteralSystemId() {
    return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getLiteralSystemId() : null;
  }
  
  public void setLiteralSystemId(String systemId) {}
  
  public final String getPublicId() {
    return (this.fCurrentEntity != null && this.fCurrentEntity.entityLocation != null) ? this.fCurrentEntity.entityLocation.getPublicId() : null;
  }
  
  public void setPublicId(String publicId) {}
  
  public void setVersion(String version) {
    this.fCurrentEntity.version = version;
  }
  
  public String getVersion() {
    if (this.fCurrentEntity != null)
      return this.fCurrentEntity.version; 
    return null;
  }
  
  public final String getEncoding() {
    if (this.fCurrentEntity != null)
      return this.fCurrentEntity.encoding; 
    return null;
  }
  
  public final void setEncoding(String encoding) throws IOException {
    if (this.fCurrentEntity.stream != null)
      if (this.fCurrentEntity.encoding == null || 
        !this.fCurrentEntity.encoding.equals(encoding)) {
        if (this.fCurrentEntity.encoding != null && this.fCurrentEntity.encoding.startsWith("UTF-16")) {
          String ENCODING = encoding.toUpperCase(Locale.ENGLISH);
          if (ENCODING.equals("UTF-16"))
            return; 
          if (ENCODING.equals("ISO-10646-UCS-4")) {
            if (this.fCurrentEntity.encoding.equals("UTF-16BE")) {
              this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)8);
            } else {
              this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)4);
            } 
            return;
          } 
          if (ENCODING.equals("ISO-10646-UCS-2")) {
            if (this.fCurrentEntity.encoding.equals("UTF-16BE")) {
              this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)2);
            } else {
              this.fCurrentEntity.reader = new UCSReader(this.fCurrentEntity.stream, (short)1);
            } 
            return;
          } 
        } 
        this.fCurrentEntity.reader = createReader(this.fCurrentEntity.stream, encoding, null);
        this.fCurrentEntity.encoding = encoding;
      }  
  }
  
  public final boolean isExternal() {
    return this.fCurrentEntity.isExternal();
  }
  
  public int getChar(int relative) throws IOException {
    if (arrangeCapacity(relative + 1, false))
      return this.fCurrentEntity.ch[this.fCurrentEntity.position + relative]; 
    return -1;
  }
  
  public int peekChar() throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
    if (this.isExternal)
      return (c != 13) ? c : 10; 
    return c;
  }
  
  public int scanChar() throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
    if (c == 10 || (c == 13 && this.isExternal)) {
      this.fCurrentEntity.lineNumber++;
      this.fCurrentEntity.columnNumber = 1;
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = (char)c;
        load(1, false, false);
      } 
      if (c == 13 && this.isExternal) {
        if (this.fCurrentEntity.ch[this.fCurrentEntity.position++] != '\n')
          this.fCurrentEntity.position--; 
        c = 10;
      } 
    } 
    this.fCurrentEntity.columnNumber++;
    return c;
  }
  
  public String scanNmtoken() throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int offset = this.fCurrentEntity.position;
    boolean vc = false;
    while (true) {
      char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (c < '') {
        vc = VALID_NAMES[c];
      } else {
        vc = XMLChar.isName(c);
      } 
      if (!vc)
        break; 
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
        int i = this.fCurrentEntity.position - offset;
        invokeListeners(i);
        if (i == this.fCurrentEntity.fBufferSize) {
          char[] tmp = new char[this.fCurrentEntity.fBufferSize * 2];
          System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, i);
          this.fCurrentEntity.ch = tmp;
          this.fCurrentEntity.fBufferSize *= 2;
        } else {
          System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, i);
        } 
        offset = 0;
        if (load(i, false, false))
          break; 
      } 
    } 
    int length = this.fCurrentEntity.position - offset;
    this.fCurrentEntity.columnNumber += length;
    String symbol = null;
    if (length > 0)
      symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length); 
    return symbol;
  }
  
  public String scanName() throws IOException {
    String symbol;
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int offset = this.fCurrentEntity.position;
    if (XMLChar.isNameStart(this.fCurrentEntity.ch[offset])) {
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[offset];
        offset = 0;
        if (load(1, false, false)) {
          this.fCurrentEntity.columnNumber++;
          String str = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
          return str;
        } 
      } 
      boolean vc = false;
      while (true) {
        char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (c < '') {
          vc = VALID_NAMES[c];
        } else {
          vc = XMLChar.isName(c);
        } 
        if (!vc)
          break; 
        if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
          int i = this.fCurrentEntity.position - offset;
          invokeListeners(i);
          if (i == this.fCurrentEntity.fBufferSize) {
            char[] tmp = new char[this.fCurrentEntity.fBufferSize * 2];
            System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, i);
            this.fCurrentEntity.ch = tmp;
            this.fCurrentEntity.fBufferSize *= 2;
          } else {
            System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, i);
          } 
          offset = 0;
          if (load(i, false, false))
            break; 
        } 
      } 
    } 
    int length = this.fCurrentEntity.position - offset;
    this.fCurrentEntity.columnNumber += length;
    if (length > 0) {
      symbol = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
    } else {
      symbol = null;
    } 
    return symbol;
  }
  
  public boolean scanQName(QName qname) throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int offset = this.fCurrentEntity.position;
    if (XMLChar.isNameStart(this.fCurrentEntity.ch[offset])) {
      if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[offset];
        offset = 0;
        if (load(1, false, false)) {
          this.fCurrentEntity.columnNumber++;
          String name = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, 0, 1);
          qname.setValues(null, name, name, null);
          return true;
        } 
      } 
      int index = -1;
      boolean vc = false;
      while (true) {
        char c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
        if (c < '') {
          vc = VALID_NAMES[c];
        } else {
          vc = XMLChar.isName(c);
        } 
        if (!vc)
          break; 
        if (c == ':') {
          if (index != -1)
            break; 
          index = this.fCurrentEntity.position;
        } 
        if (++this.fCurrentEntity.position == this.fCurrentEntity.count) {
          int i = this.fCurrentEntity.position - offset;
          invokeListeners(i);
          if (i == this.fCurrentEntity.fBufferSize) {
            char[] tmp = new char[this.fCurrentEntity.fBufferSize * 2];
            System.arraycopy(this.fCurrentEntity.ch, offset, tmp, 0, i);
            this.fCurrentEntity.ch = tmp;
            this.fCurrentEntity.fBufferSize *= 2;
          } else {
            System.arraycopy(this.fCurrentEntity.ch, offset, this.fCurrentEntity.ch, 0, i);
          } 
          if (index != -1)
            index -= offset; 
          offset = 0;
          if (load(i, false, false))
            break; 
        } 
      } 
      int length = this.fCurrentEntity.position - offset;
      this.fCurrentEntity.columnNumber += length;
      if (length > 0) {
        String prefix = null;
        String localpart = null;
        String rawname = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, length);
        if (index != -1) {
          int prefixLength = index - offset;
          prefix = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, offset, prefixLength);
          int len = length - prefixLength - 1;
          localpart = this.fSymbolTable.addSymbol(this.fCurrentEntity.ch, index + 1, len);
        } else {
          localpart = rawname;
        } 
        qname.setValues(prefix, localpart, rawname, null);
        return true;
      } 
    } 
    return false;
  }
  
  public int scanContent(XMLString content) throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
      load(0, true, true);
    } else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
      invokeListeners(0);
      this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
      load(1, false, false);
      this.fCurrentEntity.position = 0;
    } 
    int offset = this.fCurrentEntity.position;
    int c = this.fCurrentEntity.ch[offset];
    int newlines = 0;
    if (c == 10 || (c == 13 && this.isExternal)) {
      do {
        c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        if (c == 13 && this.isExternal) {
          newlines++;
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            offset = 0;
            this.fCurrentEntity.position = newlines;
            if (load(newlines, false, true))
              break; 
          } 
          if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
            this.fCurrentEntity.position++;
            offset++;
          } else {
            newlines++;
          } 
        } else if (c == 10) {
          newlines++;
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            offset = 0;
            this.fCurrentEntity.position = newlines;
            if (load(newlines, false, true))
              break; 
          } 
        } else {
          this.fCurrentEntity.position--;
          break;
        } 
      } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
      for (int i = offset; i < this.fCurrentEntity.position; i++)
        this.fCurrentEntity.ch[i] = '\n'; 
      int j = this.fCurrentEntity.position - offset;
      if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
        content.setValues(this.fCurrentEntity.ch, offset, j);
        return -1;
      } 
    } 
    while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
      c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
      if (!XMLChar.isContent(c)) {
        this.fCurrentEntity.position--;
        break;
      } 
    } 
    int length = this.fCurrentEntity.position - offset;
    this.fCurrentEntity.columnNumber += length - newlines;
    content.setValues(this.fCurrentEntity.ch, offset, length);
    if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
      c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (c == 13 && this.isExternal)
        c = 10; 
    } else {
      c = -1;
    } 
    return c;
  }
  
  public int scanLiteral(int quote, XMLString content) throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
      load(0, true, true);
    } else if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
      invokeListeners(0);
      this.fCurrentEntity.ch[0] = this.fCurrentEntity.ch[this.fCurrentEntity.count - 1];
      load(1, false, false);
      this.fCurrentEntity.position = 0;
    } 
    int offset = this.fCurrentEntity.position;
    int c = this.fCurrentEntity.ch[offset];
    int newlines = 0;
    if (this.whiteSpaceInfoNeeded)
      this.whiteSpaceLen = 0; 
    if (c == 10 || (c == 13 && this.isExternal)) {
      do {
        c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        if (c == 13 && this.isExternal) {
          newlines++;
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            offset = 0;
            this.fCurrentEntity.position = newlines;
            if (load(newlines, false, true))
              break; 
          } 
          if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
            this.fCurrentEntity.position++;
            offset++;
          } else {
            newlines++;
          } 
        } else if (c == 10) {
          newlines++;
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
            offset = 0;
            this.fCurrentEntity.position = newlines;
            if (load(newlines, false, true))
              break; 
          } 
        } else {
          this.fCurrentEntity.position--;
          break;
        } 
      } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
      int i = 0;
      for (i = offset; i < this.fCurrentEntity.position; i++) {
        this.fCurrentEntity.ch[i] = '\n';
        storeWhiteSpace(i);
      } 
      int j = this.fCurrentEntity.position - offset;
      if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
        content.setValues(this.fCurrentEntity.ch, offset, j);
        return -1;
      } 
    } 
    for (; this.fCurrentEntity.position < this.fCurrentEntity.count; this.fCurrentEntity.position++) {
      c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if ((c == quote && (!this.fCurrentEntity.literal || this.isExternal)) || c == 37 || 
        
        !XMLChar.isContent(c))
        break; 
      if (this.whiteSpaceInfoNeeded && c == 9)
        storeWhiteSpace(this.fCurrentEntity.position); 
    } 
    int length = this.fCurrentEntity.position - offset;
    this.fCurrentEntity.columnNumber += length - newlines;
    content.setValues(this.fCurrentEntity.ch, offset, length);
    if (this.fCurrentEntity.position != this.fCurrentEntity.count) {
      c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
      if (c == quote && this.fCurrentEntity.literal)
        c = -1; 
    } else {
      c = -1;
    } 
    return c;
  }
  
  private void storeWhiteSpace(int whiteSpacePos) {
    if (this.whiteSpaceLen >= this.whiteSpaceLookup.length) {
      int[] tmp = new int[this.whiteSpaceLookup.length + 100];
      System.arraycopy(this.whiteSpaceLookup, 0, tmp, 0, this.whiteSpaceLookup.length);
      this.whiteSpaceLookup = tmp;
    } 
    this.whiteSpaceLookup[this.whiteSpaceLen++] = whiteSpacePos;
  }
  
  public boolean scanData(String delimiter, XMLStringBuffer buffer) throws IOException {
    boolean done = false;
    int delimLen = delimiter.length();
    char charAt0 = delimiter.charAt(0);
    while (true) {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count)
        load(0, true, false); 
      boolean bNextEntity = false;
      while (this.fCurrentEntity.position > this.fCurrentEntity.count - delimLen && !bNextEntity) {
        System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
        bNextEntity = load(this.fCurrentEntity.count - this.fCurrentEntity.position, false, false);
        this.fCurrentEntity.position = 0;
        this.fCurrentEntity.startPosition = 0;
      } 
      if (this.fCurrentEntity.position > this.fCurrentEntity.count - delimLen) {
        int i = this.fCurrentEntity.count - this.fCurrentEntity.position;
        buffer.append(this.fCurrentEntity.ch, this.fCurrentEntity.position, i);
        this.fCurrentEntity.columnNumber += this.fCurrentEntity.count;
        this.fCurrentEntity.baseCharOffset += this.fCurrentEntity.position - this.fCurrentEntity.startPosition;
        this.fCurrentEntity.position = this.fCurrentEntity.count;
        this.fCurrentEntity.startPosition = this.fCurrentEntity.count;
        load(0, true, false);
        return false;
      } 
      int offset = this.fCurrentEntity.position;
      int c = this.fCurrentEntity.ch[offset];
      int newlines = 0;
      if (c == 10 || (c == 13 && this.isExternal)) {
        do {
          c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
          if (c == 13 && this.isExternal) {
            newlines++;
            this.fCurrentEntity.lineNumber++;
            this.fCurrentEntity.columnNumber = 1;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
              offset = 0;
              this.fCurrentEntity.position = newlines;
              if (load(newlines, false, true))
                break; 
            } 
            if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n') {
              this.fCurrentEntity.position++;
              offset++;
            } else {
              newlines++;
            } 
          } else if (c == 10) {
            newlines++;
            this.fCurrentEntity.lineNumber++;
            this.fCurrentEntity.columnNumber = 1;
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
              offset = 0;
              this.fCurrentEntity.position = newlines;
              this.fCurrentEntity.count = newlines;
              if (load(newlines, false, true))
                break; 
            } 
          } else {
            this.fCurrentEntity.position--;
            break;
          } 
        } while (this.fCurrentEntity.position < this.fCurrentEntity.count - 1);
        for (int i = offset; i < this.fCurrentEntity.position; i++)
          this.fCurrentEntity.ch[i] = '\n'; 
        int j = this.fCurrentEntity.position - offset;
        if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
          buffer.append(this.fCurrentEntity.ch, offset, j);
          return true;
        } 
      } 
      label84: while (this.fCurrentEntity.position < this.fCurrentEntity.count) {
        c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
        if (c == charAt0) {
          int delimOffset = this.fCurrentEntity.position - 1;
          for (int i = 1; i < delimLen; i++) {
            if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
              this.fCurrentEntity.position -= i;
              break label84;
            } 
            c = this.fCurrentEntity.ch[this.fCurrentEntity.position++];
            if (delimiter.charAt(i) != c) {
              this.fCurrentEntity.position -= i;
              break;
            } 
          } 
          if (this.fCurrentEntity.position == delimOffset + delimLen) {
            done = true;
            break;
          } 
          continue;
        } 
        if (c == 10 || (this.isExternal && c == 13)) {
          this.fCurrentEntity.position--;
          break;
        } 
        if (XMLChar.isInvalid(c)) {
          this.fCurrentEntity.position--;
          int i = this.fCurrentEntity.position - offset;
          this.fCurrentEntity.columnNumber += i - newlines;
          buffer.append(this.fCurrentEntity.ch, offset, i);
          return true;
        } 
      } 
      int length = this.fCurrentEntity.position - offset;
      this.fCurrentEntity.columnNumber += length - newlines;
      if (done)
        length -= delimLen; 
      buffer.append(this.fCurrentEntity.ch, offset, length);
      if (done)
        return !done; 
    } 
  }
  
  public boolean skipChar(int c) throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    int cc = this.fCurrentEntity.ch[this.fCurrentEntity.position];
    if (cc == c) {
      this.fCurrentEntity.position++;
      if (c == 10) {
        this.fCurrentEntity.lineNumber++;
        this.fCurrentEntity.columnNumber = 1;
      } else {
        this.fCurrentEntity.columnNumber++;
      } 
      return true;
    } 
    if (c == 10 && cc == 13 && this.isExternal) {
      if (this.fCurrentEntity.position == this.fCurrentEntity.count) {
        invokeListeners(1);
        this.fCurrentEntity.ch[0] = (char)cc;
        load(1, false, false);
      } 
      this.fCurrentEntity.position++;
      if (this.fCurrentEntity.ch[this.fCurrentEntity.position] == '\n')
        this.fCurrentEntity.position++; 
      this.fCurrentEntity.lineNumber++;
      this.fCurrentEntity.columnNumber = 1;
      return true;
    } 
    return false;
  }
  
  public boolean isSpace(char ch) {
    return (ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r');
  }
  
  public boolean skipSpaces() throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, true); 
    if (this.fCurrentEntity == null)
      return false; 
    int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
    if (XMLChar.isSpace(c)) {
      do {
        boolean entityChanged = false;
        if (c == 10 || (this.isExternal && c == 13)) {
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            invokeListeners(0);
            this.fCurrentEntity.ch[0] = (char)c;
            entityChanged = load(1, true, false);
            if (!entityChanged) {
              this.fCurrentEntity.position = 0;
            } else if (this.fCurrentEntity == null) {
              return true;
            } 
          } 
          if (c == 13 && this.isExternal)
            if (this.fCurrentEntity.ch[++this.fCurrentEntity.position] != '\n')
              this.fCurrentEntity.position--;  
        } else {
          this.fCurrentEntity.columnNumber++;
        } 
        if (!entityChanged)
          this.fCurrentEntity.position++; 
        if (this.fCurrentEntity.position != this.fCurrentEntity.count)
          continue; 
        load(0, true, true);
        if (this.fCurrentEntity == null)
          return true; 
      } while (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
      return true;
    } 
    return false;
  }
  
  public boolean arrangeCapacity(int length) throws IOException {
    return arrangeCapacity(length, false);
  }
  
  public boolean arrangeCapacity(int length, boolean changeEntity) throws IOException {
    if (this.fCurrentEntity.count - this.fCurrentEntity.position >= length)
      return true; 
    boolean entityChanged = false;
    while (this.fCurrentEntity.count - this.fCurrentEntity.position < length) {
      if (this.fCurrentEntity.ch.length - this.fCurrentEntity.position < length) {
        invokeListeners(0);
        System.arraycopy(this.fCurrentEntity.ch, this.fCurrentEntity.position, this.fCurrentEntity.ch, 0, this.fCurrentEntity.count - this.fCurrentEntity.position);
        this.fCurrentEntity.count -= this.fCurrentEntity.position;
        this.fCurrentEntity.position = 0;
      } 
      if (this.fCurrentEntity.count - this.fCurrentEntity.position < length) {
        int pos = this.fCurrentEntity.position;
        invokeListeners(pos);
        entityChanged = load(this.fCurrentEntity.count, changeEntity, false);
        this.fCurrentEntity.position = pos;
        if (entityChanged)
          break; 
      } 
    } 
    if (this.fCurrentEntity.count - this.fCurrentEntity.position >= length)
      return true; 
    return false;
  }
  
  public boolean skipString(String s) throws IOException {
    int length = s.length();
    if (arrangeCapacity(length, false)) {
      int beforeSkip = this.fCurrentEntity.position;
      int afterSkip = this.fCurrentEntity.position + length - 1;
      int i = length - 1;
      while (s.charAt(i--) == this.fCurrentEntity.ch[afterSkip]) {
        if (afterSkip-- == beforeSkip) {
          this.fCurrentEntity.position += length;
          this.fCurrentEntity.columnNumber += length;
          return true;
        } 
      } 
    } 
    return false;
  }
  
  public boolean skipString(char[] s) throws IOException {
    int length = s.length;
    if (arrangeCapacity(length, false)) {
      int beforeSkip = this.fCurrentEntity.position;
      int afterSkip = this.fCurrentEntity.position + length;
      for (int i = 0; i < length; i++) {
        if (this.fCurrentEntity.ch[beforeSkip++] != s[i])
          return false; 
      } 
      this.fCurrentEntity.position += length;
      this.fCurrentEntity.columnNumber += length;
      return true;
    } 
    return false;
  }
  
  final boolean load(int offset, boolean changeEntity, boolean notify) throws IOException {
    if (notify)
      invokeListeners(offset); 
    this.fCurrentEntity.fTotalCountTillLastLoad += this.fCurrentEntity.fLastCount;
    int length = this.fCurrentEntity.ch.length - offset;
    if (!this.fCurrentEntity.mayReadChunks && length > 64)
      length = 64; 
    int count = this.fCurrentEntity.reader.read(this.fCurrentEntity.ch, offset, length);
    boolean entityChanged = false;
    if (count != -1) {
      if (count != 0) {
        this.fCurrentEntity.fLastCount = count;
        this.fCurrentEntity.count = count + offset;
        this.fCurrentEntity.position = offset;
      } 
    } else {
      this.fCurrentEntity.count = offset;
      this.fCurrentEntity.position = offset;
      entityChanged = true;
      if (changeEntity) {
        this.fEntityManager.endEntity();
        if (this.fCurrentEntity == null)
          throw END_OF_DOCUMENT_ENTITY; 
        if (this.fCurrentEntity.position == this.fCurrentEntity.count)
          load(0, true, false); 
      } 
    } 
    return entityChanged;
  }
  
  protected Reader createReader(InputStream inputStream, String encoding, Boolean isBigEndian) throws IOException {
    if (encoding == null)
      encoding = "UTF-8"; 
    String ENCODING = encoding.toUpperCase(Locale.ENGLISH);
    if (ENCODING.equals("UTF-8"))
      return new UTF8Reader(inputStream, this.fCurrentEntity.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale()); 
    if (ENCODING.equals("US-ASCII"))
      return new ASCIIReader(inputStream, this.fCurrentEntity.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale()); 
    if (ENCODING.equals("ISO-10646-UCS-4")) {
      if (isBigEndian != null) {
        boolean isBE = isBigEndian.booleanValue();
        if (isBE)
          return new UCSReader(inputStream, (short)8); 
        return new UCSReader(inputStream, (short)4);
      } 
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { encoding }, (short)2);
    } 
    if (ENCODING.equals("ISO-10646-UCS-2")) {
      if (isBigEndian != null) {
        boolean isBE = isBigEndian.booleanValue();
        if (isBE)
          return new UCSReader(inputStream, (short)2); 
        return new UCSReader(inputStream, (short)1);
      } 
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { encoding }, (short)2);
    } 
    boolean validIANA = XMLChar.isValidIANAEncoding(encoding);
    boolean validJava = XMLChar.isValidJavaEncoding(encoding);
    if (!validIANA || (this.fAllowJavaEncodings && !validJava)) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { encoding }, (short)2);
      encoding = "ISO-8859-1";
    } 
    String javaEncoding = EncodingMap.getIANA2JavaMapping(ENCODING);
    if (javaEncoding == null) {
      if (this.fAllowJavaEncodings) {
        javaEncoding = encoding;
      } else {
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { encoding }, (short)2);
        javaEncoding = "ISO8859_1";
      } 
    } else if (javaEncoding.equals("ASCII")) {
      return new ASCIIReader(inputStream, this.fBufferSize, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale());
    } 
    return new InputStreamReader(inputStream, javaEncoding);
  }
  
  protected Object[] getEncodingName(byte[] b4, int count) {
    if (count < 2)
      return new Object[] { "UTF-8", null }; 
    int b0 = b4[0] & 0xFF;
    int b1 = b4[1] & 0xFF;
    if (b0 == 254 && b1 == 255)
      return new Object[] { "UTF-16BE", new Boolean(true) }; 
    if (b0 == 255 && b1 == 254)
      return new Object[] { "UTF-16LE", new Boolean(false) }; 
    if (count < 3)
      return new Object[] { "UTF-8", null }; 
    int b2 = b4[2] & 0xFF;
    if (b0 == 239 && b1 == 187 && b2 == 191)
      return new Object[] { "UTF-8", null }; 
    if (count < 4)
      return new Object[] { "UTF-8", null }; 
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
    return new Object[] { "UTF-8", null };
  }
  
  final void print() {}
  
  public void registerListener(XMLBufferListener listener) {
    if (!this.listeners.contains(listener))
      this.listeners.add(listener); 
  }
  
  public void invokeListeners(int loadPos) {
    for (int i = 0; i < this.listeners.size(); i++) {
      XMLBufferListener listener = this.listeners.get(i);
      listener.refresh(loadPos);
    } 
  }
  
  public final boolean skipDeclSpaces() throws IOException {
    if (this.fCurrentEntity.position == this.fCurrentEntity.count)
      load(0, true, false); 
    int c = this.fCurrentEntity.ch[this.fCurrentEntity.position];
    if (XMLChar.isSpace(c)) {
      boolean external = this.fCurrentEntity.isExternal();
      do {
        boolean entityChanged = false;
        if (c == 10 || (external && c == 13)) {
          this.fCurrentEntity.lineNumber++;
          this.fCurrentEntity.columnNumber = 1;
          if (this.fCurrentEntity.position == this.fCurrentEntity.count - 1) {
            this.fCurrentEntity.ch[0] = (char)c;
            entityChanged = load(1, true, false);
            if (!entityChanged)
              this.fCurrentEntity.position = 0; 
          } 
          if (c == 13 && external)
            if (this.fCurrentEntity.ch[++this.fCurrentEntity.position] != '\n')
              this.fCurrentEntity.position--;  
        } else {
          this.fCurrentEntity.columnNumber++;
        } 
        if (!entityChanged)
          this.fCurrentEntity.position++; 
        if (this.fCurrentEntity.position != this.fCurrentEntity.count)
          continue; 
        load(0, true, false);
      } while (XMLChar.isSpace(c = this.fCurrentEntity.ch[this.fCurrentEntity.position]));
      return true;
    } 
    return false;
  }
  
  public XMLEntityScanner() {}
}
