package com.sun.org.apache.xerces.internal.utils;

import com.sun.org.apache.xerces.internal.util.SecurityManager;

public final class XMLSecurityManager {
  private static final int NO_LIMIT = 0;
  
  private final int[] values;
  
  private State[] states;
  
  boolean secureProcessing;
  
  private boolean[] isSet;
  
  public enum State {
    DEFAULT("default"),
    FSP("FEATURE_SECURE_PROCESSING"),
    JAXPDOTPROPERTIES("jaxp.properties"),
    SYSTEMPROPERTY("system property"),
    APIPROPERTY("property");
    
    final String literal;
    
    State(String literal) {
      this.literal = literal;
    }
    
    String literal() {
      return this.literal;
    }
  }
  
  public enum Limit {
    ENTITY_EXPANSION_LIMIT("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", "jdk.xml.entityExpansionLimit", 0, 64000),
    MAX_OCCUR_NODE_LIMIT("http://www.oracle.com/xml/jaxp/properties/maxOccurLimit", "jdk.xml.maxOccurLimit", 0, 5000),
    ELEMENT_ATTRIBUTE_LIMIT("http://www.oracle.com/xml/jaxp/properties/elementAttributeLimit", "jdk.xml.elementAttributeLimit", 0, 10000),
    TOTAL_ENTITY_SIZE_LIMIT("http://www.oracle.com/xml/jaxp/properties/totalEntitySizeLimit", "jdk.xml.totalEntitySizeLimit", 0, 50000000),
    GENERAL_ENTITY_SIZE_LIMIT("http://www.oracle.com/xml/jaxp/properties/maxGeneralEntitySizeLimit", "jdk.xml.maxGeneralEntitySizeLimit", 0, 0),
    PARAMETER_ENTITY_SIZE_LIMIT("http://www.oracle.com/xml/jaxp/properties/maxParameterEntitySizeLimit", "jdk.xml.maxParameterEntitySizeLimit", 0, 1000000),
    MAX_ELEMENT_DEPTH_LIMIT("http://www.oracle.com/xml/jaxp/properties/maxElementDepth", "jdk.xml.maxElementDepth", 0, 0);
    
    final String apiProperty;
    
    final String systemProperty;
    
    final int defaultValue;
    
    final int secureValue;
    
    Limit(String apiProperty, String systemProperty, int value, int secureValue) {
      this.apiProperty = apiProperty;
      this.systemProperty = systemProperty;
      this.defaultValue = value;
      this.secureValue = secureValue;
    }
    
    public boolean equalsAPIPropertyName(String propertyName) {
      return (propertyName == null) ? false : this.apiProperty.equals(propertyName);
    }
    
    public boolean equalsSystemPropertyName(String propertyName) {
      return (propertyName == null) ? false : this.systemProperty.equals(propertyName);
    }
    
    public String apiProperty() {
      return this.apiProperty;
    }
    
    String systemProperty() {
      return this.systemProperty;
    }
    
    int defaultValue() {
      return this.defaultValue;
    }
    
    int secureValue() {
      return this.secureValue;
    }
  }
  
  public enum NameMap {
    ENTITY_EXPANSION_LIMIT("jdk.xml.entityExpansionLimit", "entityExpansionLimit"),
    MAX_OCCUR_NODE_LIMIT("jdk.xml.maxOccurLimit", "maxOccurLimit"),
    ELEMENT_ATTRIBUTE_LIMIT("jdk.xml.elementAttributeLimit", "elementAttributeLimit");
    
    final String newName;
    
    final String oldName;
    
    NameMap(String newName, String oldName) {
      this.newName = newName;
      this.oldName = oldName;
    }
    
    String getOldName(String newName) {
      if (newName.equals(this.newName))
        return this.oldName; 
      return null;
    }
  }
  
  private int indexEntityCountInfo = 10000;
  
  private String printEntityCountInfo = "";
  
  public XMLSecurityManager() {
    this(false);
  }
  
  public XMLSecurityManager(boolean secureProcessing) {
    this.values = new int[(Limit.values()).length];
    this.states = new State[(Limit.values()).length];
    this.isSet = new boolean[(Limit.values()).length];
    this.secureProcessing = secureProcessing;
    for (Limit limit : Limit.values()) {
      if (secureProcessing) {
        this.values[limit.ordinal()] = limit.secureValue;
        this.states[limit.ordinal()] = State.FSP;
      } else {
        this.values[limit.ordinal()] = limit.defaultValue();
        this.states[limit.ordinal()] = State.DEFAULT;
      } 
    } 
    readSystemProperties();
  }
  
  public void setSecureProcessing(boolean secure) {
    this.secureProcessing = secure;
    for (Limit limit : Limit.values()) {
      if (secure) {
        setLimit(limit.ordinal(), State.FSP, limit.secureValue());
      } else {
        setLimit(limit.ordinal(), State.FSP, limit.defaultValue());
      } 
    } 
  }
  
  public boolean isSecureProcessing() {
    return this.secureProcessing;
  }
  
  public boolean setLimit(String propertyName, State state, Object value) {
    int index = getIndex(propertyName);
    if (index > -1) {
      setLimit(index, state, value);
      return true;
    } 
    return false;
  }
  
  public void setLimit(Limit limit, State state, int value) {
    setLimit(limit.ordinal(), state, value);
  }
  
  public void setLimit(int index, State state, Object value) {
    if (index == this.indexEntityCountInfo) {
      this.printEntityCountInfo = (String)value;
    } else {
      int temp;
      if (Integer.class.isAssignableFrom(value.getClass())) {
        temp = ((Integer)value).intValue();
      } else {
        temp = Integer.parseInt((String)value);
        if (temp < 0)
          temp = 0; 
      } 
      setLimit(index, state, temp);
    } 
  }
  
  public void setLimit(int index, State state, int value) {
    if (index == this.indexEntityCountInfo) {
      this.printEntityCountInfo = "yes";
    } else if (state.compareTo(this.states[index]) >= 0) {
      this.values[index] = value;
      this.states[index] = state;
      this.isSet[index] = true;
    } 
  }
  
  public String getLimitAsString(String propertyName) {
    int index = getIndex(propertyName);
    if (index > -1)
      return getLimitValueByIndex(index); 
    return null;
  }
  
  public int getLimit(Limit limit) {
    return this.values[limit.ordinal()];
  }
  
  public String getLimitValueAsString(Limit limit) {
    return Integer.toString(this.values[limit.ordinal()]);
  }
  
  public String getLimitValueByIndex(int index) {
    if (index == this.indexEntityCountInfo)
      return this.printEntityCountInfo; 
    return Integer.toString(this.values[index]);
  }
  
  public State getState(Limit limit) {
    return this.states[limit.ordinal()];
  }
  
  public String getStateLiteral(Limit limit) {
    return this.states[limit.ordinal()].literal();
  }
  
  public int getIndex(String propertyName) {
    for (Limit limit : Limit.values()) {
      if (limit.equalsAPIPropertyName(propertyName))
        return limit.ordinal(); 
    } 
    if (propertyName.equals("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo"))
      return this.indexEntityCountInfo; 
    return -1;
  }
  
  public boolean isNoLimit(int limit) {
    return (limit == 0);
  }
  
  public boolean isOverLimit(Limit limit, String entityName, int size, XMLLimitAnalyzer limitAnalyzer) {
    return isOverLimit(limit.ordinal(), entityName, size, limitAnalyzer);
  }
  
  public boolean isOverLimit(int index, String entityName, int size, XMLLimitAnalyzer limitAnalyzer) {
    if (this.values[index] == 0)
      return false; 
    if (size > this.values[index]) {
      limitAnalyzer.addValue(index, entityName, size);
      return true;
    } 
    return false;
  }
  
  public boolean isOverLimit(Limit limit, XMLLimitAnalyzer limitAnalyzer) {
    return isOverLimit(limit.ordinal(), limitAnalyzer);
  }
  
  public boolean isOverLimit(int index, XMLLimitAnalyzer limitAnalyzer) {
    if (this.values[index] == 0)
      return false; 
    if (index == Limit.ELEMENT_ATTRIBUTE_LIMIT.ordinal() || index == Limit.ENTITY_EXPANSION_LIMIT
      .ordinal() || index == Limit.TOTAL_ENTITY_SIZE_LIMIT
      .ordinal() || index == Limit.MAX_ELEMENT_DEPTH_LIMIT
      .ordinal())
      return (limitAnalyzer.getTotalValue(index) > this.values[index]); 
    return (limitAnalyzer.getValue(index) > this.values[index]);
  }
  
  public void debugPrint(XMLLimitAnalyzer limitAnalyzer) {
    if (this.printEntityCountInfo.equals("yes"))
      limitAnalyzer.debugPrint(this); 
  }
  
  public boolean isSet(int index) {
    return this.isSet[index];
  }
  
  public boolean printEntityCountInfo() {
    return this.printEntityCountInfo.equals("yes");
  }
  
  private void readSystemProperties() {
    for (Limit limit : Limit.values()) {
      if (!getSystemProperty(limit, limit.systemProperty()))
        for (NameMap nameMap : NameMap.values()) {
          String oldName = nameMap.getOldName(limit.systemProperty());
          if (oldName != null)
            getSystemProperty(limit, oldName); 
        }  
    } 
  }
  
  private boolean getSystemProperty(Limit limit, String sysPropertyName) {
    try {
      String value = SecuritySupport.getSystemProperty(sysPropertyName);
      if (value != null && !value.equals("")) {
        this.values[limit.ordinal()] = Integer.parseInt(value);
        this.states[limit.ordinal()] = State.SYSTEMPROPERTY;
        return true;
      } 
      value = SecuritySupport.readJAXPProperty(sysPropertyName);
      if (value != null && !value.equals("")) {
        this.values[limit.ordinal()] = Integer.parseInt(value);
        this.states[limit.ordinal()] = State.JAXPDOTPROPERTIES;
        return true;
      } 
    } catch (NumberFormatException e) {
      throw new NumberFormatException("Invalid setting for system property: " + limit.systemProperty());
    } 
    return false;
  }
  
  public static XMLSecurityManager convert(Object value, XMLSecurityManager securityManager) {
    if (value == null) {
      if (securityManager == null)
        securityManager = new XMLSecurityManager(true); 
      return securityManager;
    } 
    if (XMLSecurityManager.class.isAssignableFrom(value.getClass()))
      return (XMLSecurityManager)value; 
    if (securityManager == null)
      securityManager = new XMLSecurityManager(true); 
    if (SecurityManager.class.isAssignableFrom(value.getClass())) {
      SecurityManager origSM = (SecurityManager)value;
      securityManager.setLimit(Limit.MAX_OCCUR_NODE_LIMIT, State.APIPROPERTY, origSM.getMaxOccurNodeLimit());
      securityManager.setLimit(Limit.ENTITY_EXPANSION_LIMIT, State.APIPROPERTY, origSM.getEntityExpansionLimit());
      securityManager.setLimit(Limit.ELEMENT_ATTRIBUTE_LIMIT, State.APIPROPERTY, origSM.getElementAttrLimit());
    } 
    return securityManager;
  }
}
