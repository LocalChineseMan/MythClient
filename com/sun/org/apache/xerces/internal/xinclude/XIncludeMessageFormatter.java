package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class XIncludeMessageFormatter implements MessageFormatter {
  public static final String XINCLUDE_DOMAIN = "http://www.w3.org/TR/xinclude";
  
  private Locale fLocale = null;
  
  private ResourceBundle fResourceBundle = null;
  
  public String formatMessage(Locale locale, String key, Object[] arguments) throws MissingResourceException {
    if (this.fResourceBundle == null || locale != this.fLocale) {
      if (locale != null) {
        this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XIncludeMessages", locale);
        this.fLocale = locale;
      } 
      if (this.fResourceBundle == null)
        this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XIncludeMessages"); 
    } 
    String msg = this.fResourceBundle.getString(key);
    if (arguments != null)
      try {
        msg = MessageFormat.format(msg, arguments);
      } catch (Exception e) {
        msg = this.fResourceBundle.getString("FormatFailed");
        msg = msg + " " + this.fResourceBundle.getString(key);
      }  
    if (msg == null) {
      msg = this.fResourceBundle.getString("BadMessageKey");
      throw new MissingResourceException(msg, "com.sun.org.apache.xerces.internal.impl.msg.XIncludeMessages", key);
    } 
    return msg;
  }
}
