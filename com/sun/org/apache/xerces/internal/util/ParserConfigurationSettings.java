package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ParserConfigurationSettings implements XMLComponentManager {
  protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  
  protected Set<String> fRecognizedProperties;
  
  protected Map<String, Object> fProperties;
  
  protected Set<String> fRecognizedFeatures;
  
  protected Map<String, Boolean> fFeatures;
  
  protected XMLComponentManager fParentSettings;
  
  public ParserConfigurationSettings() {
    this(null);
  }
  
  public ParserConfigurationSettings(XMLComponentManager parent) {
    this.fRecognizedFeatures = new HashSet<>();
    this.fRecognizedProperties = new HashSet<>();
    this.fFeatures = new HashMap<>();
    this.fProperties = new HashMap<>();
    this.fParentSettings = parent;
  }
  
  public void addRecognizedFeatures(String[] featureIds) {
    int featureIdsCount = (featureIds != null) ? featureIds.length : 0;
    for (int i = 0; i < featureIdsCount; i++) {
      String featureId = featureIds[i];
      if (!this.fRecognizedFeatures.contains(featureId))
        this.fRecognizedFeatures.add(featureId); 
    } 
  }
  
  public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
    FeatureState checkState = checkFeature(featureId);
    if (checkState.isExceptional())
      throw new XMLConfigurationException(checkState.status, featureId); 
    this.fFeatures.put(featureId, Boolean.valueOf(state));
  }
  
  public void addRecognizedProperties(String[] propertyIds) {
    this.fRecognizedProperties.addAll(Arrays.asList(propertyIds));
  }
  
  public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
    PropertyState checkState = checkProperty(propertyId);
    if (checkState.isExceptional())
      throw new XMLConfigurationException(checkState.status, propertyId); 
    this.fProperties.put(propertyId, value);
  }
  
  public final boolean getFeature(String featureId) throws XMLConfigurationException {
    FeatureState state = getFeatureState(featureId);
    if (state.isExceptional())
      throw new XMLConfigurationException(state.status, featureId); 
    return state.state;
  }
  
  public final boolean getFeature(String featureId, boolean defaultValue) {
    FeatureState state = getFeatureState(featureId);
    if (state.isExceptional())
      return defaultValue; 
    return state.state;
  }
  
  public FeatureState getFeatureState(String featureId) {
    Boolean state = this.fFeatures.get(featureId);
    if (state == null) {
      FeatureState checkState = checkFeature(featureId);
      if (checkState.isExceptional())
        return checkState; 
      return FeatureState.is(false);
    } 
    return FeatureState.is(state.booleanValue());
  }
  
  public final Object getProperty(String propertyId) throws XMLConfigurationException {
    PropertyState state = getPropertyState(propertyId);
    if (state.isExceptional())
      throw new XMLConfigurationException(state.status, propertyId); 
    return state.state;
  }
  
  public final Object getProperty(String propertyId, Object defaultValue) {
    PropertyState state = getPropertyState(propertyId);
    if (state.isExceptional())
      return defaultValue; 
    return state.state;
  }
  
  public PropertyState getPropertyState(String propertyId) {
    Object propertyValue = this.fProperties.get(propertyId);
    if (propertyValue == null) {
      PropertyState state = checkProperty(propertyId);
      if (state.isExceptional())
        return state; 
    } 
    return PropertyState.is(propertyValue);
  }
  
  protected FeatureState checkFeature(String featureId) throws XMLConfigurationException {
    if (!this.fRecognizedFeatures.contains(featureId)) {
      if (this.fParentSettings != null)
        return this.fParentSettings.getFeatureState(featureId); 
      return FeatureState.NOT_RECOGNIZED;
    } 
    return FeatureState.RECOGNIZED;
  }
  
  protected PropertyState checkProperty(String propertyId) throws XMLConfigurationException {
    if (!this.fRecognizedProperties.contains(propertyId))
      if (this.fParentSettings != null) {
        PropertyState state = this.fParentSettings.getPropertyState(propertyId);
        if (state.isExceptional())
          return state; 
      } else {
        return PropertyState.NOT_RECOGNIZED;
      }  
    return PropertyState.RECOGNIZED;
  }
}
