package com.sun.org.apache.xerces.internal.util;

import java.util.Locale;
import java.util.MissingResourceException;

public interface MessageFormatter {
  String formatMessage(Locale paramLocale, String paramString, Object[] paramArrayOfObject) throws MissingResourceException;
}
