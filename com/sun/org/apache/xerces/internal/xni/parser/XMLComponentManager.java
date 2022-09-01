package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.util.FeatureState;
import com.sun.org.apache.xerces.internal.util.PropertyState;

public interface XMLComponentManager {
  boolean getFeature(String paramString) throws XMLConfigurationException;
  
  boolean getFeature(String paramString, boolean paramBoolean);
  
  Object getProperty(String paramString) throws XMLConfigurationException;
  
  Object getProperty(String paramString, Object paramObject);
  
  FeatureState getFeatureState(String paramString);
  
  PropertyState getPropertyState(String paramString);
}
