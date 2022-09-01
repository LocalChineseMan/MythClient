package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;

public interface RevalidationHandler extends XMLDocumentFilter {
  boolean characterData(String paramString, Augmentations paramAugmentations);
}
