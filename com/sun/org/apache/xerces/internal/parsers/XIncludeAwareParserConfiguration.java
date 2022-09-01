package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeHandler;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeNamespaceSupport;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;

public class XIncludeAwareParserConfiguration extends XML11Configuration {
  protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
  
  protected static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
  
  protected static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
  
  protected static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
  
  protected static final String XINCLUDE_HANDLER = "http://apache.org/xml/properties/internal/xinclude-handler";
  
  protected static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
  
  protected XIncludeHandler fXIncludeHandler;
  
  protected NamespaceSupport fNonXIncludeNSContext;
  
  protected XIncludeNamespaceSupport fXIncludeNSContext;
  
  protected NamespaceContext fCurrentNSContext;
  
  protected boolean fXIncludeEnabled = false;
  
  public XIncludeAwareParserConfiguration() {
    this((SymbolTable)null, (XMLGrammarPool)null, (XMLComponentManager)null);
  }
  
  public XIncludeAwareParserConfiguration(SymbolTable symbolTable) {
    this(symbolTable, (XMLGrammarPool)null, (XMLComponentManager)null);
  }
  
  public XIncludeAwareParserConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool) {
    this(symbolTable, grammarPool, (XMLComponentManager)null);
  }
  
  public XIncludeAwareParserConfiguration(SymbolTable symbolTable, XMLGrammarPool grammarPool, XMLComponentManager parentSettings) {
    super(symbolTable, grammarPool, parentSettings);
    String[] recognizedFeatures = { "http://xml.org/sax/features/allow-dtd-events-after-endDTD", "http://apache.org/xml/features/xinclude/fixup-base-uris", "http://apache.org/xml/features/xinclude/fixup-language" };
    addRecognizedFeatures(recognizedFeatures);
    String[] recognizedProperties = { "http://apache.org/xml/properties/internal/xinclude-handler", "http://apache.org/xml/properties/internal/namespace-context" };
    addRecognizedProperties(recognizedProperties);
    setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", true);
    setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", true);
    setFeature("http://apache.org/xml/features/xinclude/fixup-language", true);
    this.fNonXIncludeNSContext = new NamespaceSupport();
    this.fCurrentNSContext = this.fNonXIncludeNSContext;
    setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNonXIncludeNSContext);
  }
  
  protected void configurePipeline() {
    super.configurePipeline();
    if (this.fXIncludeEnabled) {
      if (this.fXIncludeHandler == null) {
        this.fXIncludeHandler = new XIncludeHandler();
        setProperty("http://apache.org/xml/properties/internal/xinclude-handler", this.fXIncludeHandler);
        addCommonComponent(this.fXIncludeHandler);
        this.fXIncludeHandler.reset(this);
      } 
      if (this.fCurrentNSContext != this.fXIncludeNSContext) {
        if (this.fXIncludeNSContext == null)
          this.fXIncludeNSContext = new XIncludeNamespaceSupport(); 
        this.fCurrentNSContext = this.fXIncludeNSContext;
        setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fXIncludeNSContext);
      } 
      this.fDTDScanner.setDTDHandler(this.fDTDProcessor);
      this.fDTDProcessor.setDTDSource(this.fDTDScanner);
      this.fDTDProcessor.setDTDHandler(this.fXIncludeHandler);
      this.fXIncludeHandler.setDTDSource(this.fDTDProcessor);
      this.fXIncludeHandler.setDTDHandler(this.fDTDHandler);
      if (this.fDTDHandler != null)
        this.fDTDHandler.setDTDSource(this.fXIncludeHandler); 
      XMLDocumentSource prev = null;
      if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
        prev = this.fSchemaValidator.getDocumentSource();
      } else {
        prev = this.fLastComponent;
        this.fLastComponent = this.fXIncludeHandler;
      } 
      XMLDocumentHandler next = prev.getDocumentHandler();
      prev.setDocumentHandler(this.fXIncludeHandler);
      this.fXIncludeHandler.setDocumentSource(prev);
      if (next != null) {
        this.fXIncludeHandler.setDocumentHandler(next);
        next.setDocumentSource(this.fXIncludeHandler);
      } 
    } else if (this.fCurrentNSContext != this.fNonXIncludeNSContext) {
      this.fCurrentNSContext = this.fNonXIncludeNSContext;
      setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNonXIncludeNSContext);
    } 
  }
  
  protected void configureXML11Pipeline() {
    super.configureXML11Pipeline();
    if (this.fXIncludeEnabled) {
      if (this.fXIncludeHandler == null) {
        this.fXIncludeHandler = new XIncludeHandler();
        setProperty("http://apache.org/xml/properties/internal/xinclude-handler", this.fXIncludeHandler);
        addCommonComponent(this.fXIncludeHandler);
        this.fXIncludeHandler.reset(this);
      } 
      if (this.fCurrentNSContext != this.fXIncludeNSContext) {
        if (this.fXIncludeNSContext == null)
          this.fXIncludeNSContext = new XIncludeNamespaceSupport(); 
        this.fCurrentNSContext = this.fXIncludeNSContext;
        setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fXIncludeNSContext);
      } 
      this.fXML11DTDScanner.setDTDHandler(this.fXML11DTDProcessor);
      this.fXML11DTDProcessor.setDTDSource(this.fXML11DTDScanner);
      this.fXML11DTDProcessor.setDTDHandler(this.fXIncludeHandler);
      this.fXIncludeHandler.setDTDSource(this.fXML11DTDProcessor);
      this.fXIncludeHandler.setDTDHandler(this.fDTDHandler);
      if (this.fDTDHandler != null)
        this.fDTDHandler.setDTDSource(this.fXIncludeHandler); 
      XMLDocumentSource prev = null;
      if (this.fFeatures.get("http://apache.org/xml/features/validation/schema") == Boolean.TRUE) {
        prev = this.fSchemaValidator.getDocumentSource();
      } else {
        prev = this.fLastComponent;
        this.fLastComponent = this.fXIncludeHandler;
      } 
      XMLDocumentHandler next = prev.getDocumentHandler();
      prev.setDocumentHandler(this.fXIncludeHandler);
      this.fXIncludeHandler.setDocumentSource(prev);
      if (next != null) {
        this.fXIncludeHandler.setDocumentHandler(next);
        next.setDocumentSource(this.fXIncludeHandler);
      } 
    } else if (this.fCurrentNSContext != this.fNonXIncludeNSContext) {
      this.fCurrentNSContext = this.fNonXIncludeNSContext;
      setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNonXIncludeNSContext);
    } 
  }
  
  public FeatureState getFeatureState(String featureId) throws XMLConfigurationException {
    if (featureId.equals("http://apache.org/xml/features/internal/parser-settings"))
      return FeatureState.is(this.fConfigUpdated); 
    if (featureId.equals("http://apache.org/xml/features/xinclude"))
      return FeatureState.is(this.fXIncludeEnabled); 
    return getFeatureState0(featureId);
  }
  
  public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
    if (featureId.equals("http://apache.org/xml/features/xinclude")) {
      this.fXIncludeEnabled = state;
      this.fConfigUpdated = true;
      return;
    } 
    super.setFeature(featureId, state);
  }
}
