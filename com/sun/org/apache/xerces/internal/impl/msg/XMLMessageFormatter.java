package com.sun.org.apache.xerces.internal.impl.msg;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class XMLMessageFormatter implements MessageFormatter {
  public static final String XML_DOMAIN = "http://www.w3.org/TR/1998/REC-xml-19980210";
  
  public static final String XMLNS_DOMAIN = "http://www.w3.org/TR/1999/REC-xml-names-19990114";
  
  private Locale fLocale = null;
  
  private ResourceBundle fResourceBundle = null;
  
  public String formatMessage(Locale locale, String key, Object[] arguments) throws MissingResourceException {
    String msg;
    if (this.fResourceBundle == null || locale != this.fLocale) {
      if (locale != null) {
        this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages", locale);
        this.fLocale = locale;
      } 
      if (this.fResourceBundle == null)
        this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages"); 
    } 
    try {
      msg = this.fResourceBundle.getString(key);
      if (arguments != null)
        try {
          msg = MessageFormat.format(msg, arguments);
        } catch (Exception e) {
          msg = this.fResourceBundle.getString("FormatFailed");
          msg = msg + " " + this.fResourceBundle.getString(key);
        }  
    } catch (MissingResourceException e) {
      msg = this.fResourceBundle.getString("BadMessageKey");
      throw new MissingResourceException(key, msg, key);
    } 
    if (msg == null) {
      msg = key;
      if (arguments.length > 0) {
        StringBuffer str = new StringBuffer(msg);
        str.append('?');
        for (int i = 0; i < arguments.length; i++) {
          if (i > 0)
            str.append('&'); 
          str.append(String.valueOf(arguments[i]));
        } 
      } 
    } 
    return msg;
  }
}
