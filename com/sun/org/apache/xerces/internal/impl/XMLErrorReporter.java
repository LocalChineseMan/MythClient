package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.DefaultErrorHandler;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import java.util.Hashtable;
import java.util.Locale;
import org.xml.sax.ErrorHandler;

public class XMLErrorReporter implements XMLComponent {
  public static final short SEVERITY_WARNING = 0;
  
  public static final short SEVERITY_ERROR = 1;
  
  public static final short SEVERITY_FATAL_ERROR = 2;
  
  protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
  
  protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
  
  private static final String[] RECOGNIZED_FEATURES = new String[] { "http://apache.org/xml/features/continue-after-fatal-error" };
  
  private static final Boolean[] FEATURE_DEFAULTS = new Boolean[] { null };
  
  private static final String[] RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/error-handler" };
  
  private static final Object[] PROPERTY_DEFAULTS = new Object[] { null };
  
  protected Locale fLocale;
  
  protected Hashtable fMessageFormatters;
  
  protected XMLErrorHandler fErrorHandler;
  
  protected XMLLocator fLocator;
  
  protected boolean fContinueAfterFatalError;
  
  protected XMLErrorHandler fDefaultErrorHandler;
  
  private ErrorHandler fSaxProxy = null;
  
  public XMLErrorReporter() {
    this.fMessageFormatters = new Hashtable<>();
  }
  
  public void setLocale(Locale locale) {
    this.fLocale = locale;
  }
  
  public Locale getLocale() {
    return this.fLocale;
  }
  
  public void setDocumentLocator(XMLLocator locator) {
    this.fLocator = locator;
  }
  
  public void putMessageFormatter(String domain, MessageFormatter messageFormatter) {
    this.fMessageFormatters.put(domain, messageFormatter);
  }
  
  public MessageFormatter getMessageFormatter(String domain) {
    return (MessageFormatter)this.fMessageFormatters.get(domain);
  }
  
  public MessageFormatter removeMessageFormatter(String domain) {
    return (MessageFormatter)this.fMessageFormatters.remove(domain);
  }
  
  public String reportError(String domain, String key, Object[] arguments, short severity) throws XNIException {
    return reportError(this.fLocator, domain, key, arguments, severity);
  }
  
  public String reportError(String domain, String key, Object[] arguments, short severity, Exception exception) throws XNIException {
    return reportError(this.fLocator, domain, key, arguments, severity, exception);
  }
  
  public String reportError(XMLLocator location, String domain, String key, Object[] arguments, short severity) throws XNIException {
    return reportError(location, domain, key, arguments, severity, null);
  }
  
  public String reportError(XMLLocator location, String domain, String key, Object[] arguments, short severity, Exception exception) throws XNIException {
    String message;
    MessageFormatter messageFormatter = getMessageFormatter(domain);
    if (messageFormatter != null) {
      message = messageFormatter.formatMessage(this.fLocale, key, arguments);
    } else {
      StringBuffer str = new StringBuffer();
      str.append(domain);
      str.append('#');
      str.append(key);
      int argCount = (arguments != null) ? arguments.length : 0;
      if (argCount > 0) {
        str.append('?');
        for (int i = 0; i < argCount; i++) {
          str.append(arguments[i]);
          if (i < argCount - 1)
            str.append('&'); 
        } 
      } 
      message = str.toString();
    } 
    XMLParseException parseException = (exception != null) ? new XMLParseException(location, message, exception) : new XMLParseException(location, message);
    XMLErrorHandler errorHandler = this.fErrorHandler;
    if (errorHandler == null) {
      if (this.fDefaultErrorHandler == null)
        this.fDefaultErrorHandler = new DefaultErrorHandler(); 
      errorHandler = this.fDefaultErrorHandler;
    } 
    switch (severity) {
      case 0:
        errorHandler.warning(domain, key, parseException);
        break;
      case 1:
        errorHandler.error(domain, key, parseException);
        break;
      case 2:
        errorHandler.fatalError(domain, key, parseException);
        if (!this.fContinueAfterFatalError)
          throw parseException; 
        break;
    } 
    return message;
  }
  
  public void reset(XMLComponentManager componentManager) throws XNIException {
    this.fContinueAfterFatalError = componentManager.getFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
    this.fErrorHandler = (XMLErrorHandler)componentManager.getProperty("http://apache.org/xml/properties/internal/error-handler");
  }
  
  public String[] getRecognizedFeatures() {
    return (String[])RECOGNIZED_FEATURES.clone();
  }
  
  public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
    if (featureId.startsWith("http://apache.org/xml/features/")) {
      int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
      if (suffixLength == "continue-after-fatal-error".length() && featureId
        .endsWith("continue-after-fatal-error"))
        this.fContinueAfterFatalError = state; 
    } 
  }
  
  public boolean getFeature(String featureId) throws XMLConfigurationException {
    if (featureId.startsWith("http://apache.org/xml/features/")) {
      int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
      if (suffixLength == "continue-after-fatal-error".length() && featureId
        .endsWith("continue-after-fatal-error"))
        return this.fContinueAfterFatalError; 
    } 
    return false;
  }
  
  public String[] getRecognizedProperties() {
    return (String[])RECOGNIZED_PROPERTIES.clone();
  }
  
  public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
    if (propertyId.startsWith("http://apache.org/xml/properties/")) {
      int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
      if (suffixLength == "internal/error-handler".length() && propertyId
        .endsWith("internal/error-handler"))
        this.fErrorHandler = (XMLErrorHandler)value; 
    } 
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
  
  public XMLErrorHandler getErrorHandler() {
    return this.fErrorHandler;
  }
  
  public ErrorHandler getSAXErrorHandler() {
    if (this.fSaxProxy == null)
      this.fSaxProxy = (ErrorHandler)new Object(this); 
    return this.fSaxProxy;
  }
}
