package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity;
import java.io.EOFException;
import java.io.IOException;

public class XMLVersionDetector {
  private static final char[] XML11_VERSION = new char[] { '1', '.', '1' };
  
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  
  protected static final String fVersionSymbol = "version".intern();
  
  protected static final String fXMLSymbol = "[xml]".intern();
  
  protected SymbolTable fSymbolTable;
  
  protected XMLErrorReporter fErrorReporter;
  
  protected XMLEntityManager fEntityManager;
  
  protected String fEncoding = null;
  
  private XMLString fVersionNum = new XMLString();
  
  private final char[] fExpectedVersionString = new char[] { 
      '<', '?', 'x', 'm', 'l', ' ', 'v', 'e', 'r', 's', 
      'i', 'o', 'n', '=', ' ', ' ', ' ', ' ', ' ' };
  
  public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
    this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
    this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
    this.fEntityManager = (XMLEntityManager)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
    for (int i = 14; i < this.fExpectedVersionString.length; i++)
      this.fExpectedVersionString[i] = ' '; 
  }
  
  public void startDocumentParsing(XMLEntityHandler scanner, short version) {
    if (version == 1) {
      this.fEntityManager.setScannerVersion((short)1);
    } else {
      this.fEntityManager.setScannerVersion((short)2);
    } 
    this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
    this.fEntityManager.setEntityHandler(scanner);
    scanner.startEntity(fXMLSymbol, this.fEntityManager.getCurrentResourceIdentifier(), this.fEncoding, null);
  }
  
  public short determineDocVersion(XMLInputSource inputSource) throws IOException {
    this.fEncoding = this.fEntityManager.setupCurrentEntity(fXMLSymbol, inputSource, false, true);
    this.fEntityManager.setScannerVersion((short)1);
    XMLEntityScanner scanner = this.fEntityManager.getEntityScanner();
    try {
      if (!scanner.skipString("<?xml"))
        return 1; 
      if (!scanner.skipDeclSpaces()) {
        fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 5);
        return 1;
      } 
      if (!scanner.skipString("version")) {
        fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 6);
        return 1;
      } 
      scanner.skipDeclSpaces();
      if (scanner.peekChar() != 61) {
        fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 13);
        return 1;
      } 
      scanner.scanChar();
      scanner.skipDeclSpaces();
      int quoteChar = scanner.scanChar();
      this.fExpectedVersionString[14] = (char)quoteChar;
      for (int versionPos = 0; versionPos < XML11_VERSION.length; versionPos++)
        this.fExpectedVersionString[15 + versionPos] = (char)scanner.scanChar(); 
      this.fExpectedVersionString[18] = (char)scanner.scanChar();
      fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 19);
      int matched = 0;
      for (; matched < XML11_VERSION.length && 
        this.fExpectedVersionString[15 + matched] == XML11_VERSION[matched]; matched++);
      if (matched == XML11_VERSION.length)
        return 2; 
      return 1;
    } catch (EOFException e) {
      this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "PrematureEOF", null, (short)2);
      return 1;
    } 
  }
  
  private void fixupCurrentEntity(XMLEntityManager manager, char[] scannedChars, int length) {
    Entity.ScannedEntity currentEntity = manager.getCurrentEntity();
    if (currentEntity.count - currentEntity.position + length > currentEntity.ch.length) {
      char[] tempCh = currentEntity.ch;
      currentEntity.ch = new char[length + currentEntity.count - currentEntity.position + 1];
      System.arraycopy(tempCh, 0, currentEntity.ch, 0, tempCh.length);
    } 
    if (currentEntity.position < length) {
      System.arraycopy(currentEntity.ch, currentEntity.position, currentEntity.ch, length, currentEntity.count - currentEntity.position);
      currentEntity.count += length - currentEntity.position;
    } else {
      for (int i = length; i < currentEntity.position; i++)
        currentEntity.ch[i] = ' '; 
    } 
    System.arraycopy(scannedChars, 0, currentEntity.ch, 0, length);
    currentEntity.position = 0;
    currentEntity.baseCharOffset = 0;
    currentEntity.startPosition = 0;
    currentEntity.columnNumber = currentEntity.lineNumber = 1;
  }
}
