package com.sun.org.apache.xerces.internal.utils;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

public final class XMLLimitAnalyzer {
  private final int[] values = new int[(XMLSecurityManager.Limit.values()).length];
  
  private final int[] totalValue = new int[(XMLSecurityManager.Limit.values()).length];
  
  private final String[] names = new String[(XMLSecurityManager.Limit.values()).length];
  
  private final Map[] caches = new Map[(XMLSecurityManager.Limit.values()).length];
  
  private String entityStart;
  
  private String entityEnd;
  
  public void addValue(XMLSecurityManager.Limit limit, String entityName, int value) {
    addValue(limit.ordinal(), entityName, value);
  }
  
  public void addValue(int index, String entityName, int value) {
    Map<String, Integer> cache;
    if (index == XMLSecurityManager.Limit.ENTITY_EXPANSION_LIMIT.ordinal() || index == XMLSecurityManager.Limit.MAX_OCCUR_NODE_LIMIT
      .ordinal() || index == XMLSecurityManager.Limit.ELEMENT_ATTRIBUTE_LIMIT
      .ordinal()) {
      this.totalValue[index] = this.totalValue[index] + value;
      return;
    } 
    if (index == XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT.ordinal()) {
      this.totalValue[index] = value;
      return;
    } 
    if (this.caches[index] == null) {
      cache = new HashMap<>(10);
      this.caches[index] = cache;
    } else {
      cache = this.caches[index];
    } 
    int accumulatedValue = value;
    if (cache.containsKey(entityName)) {
      accumulatedValue += ((Integer)cache.get(entityName)).intValue();
      cache.put(entityName, Integer.valueOf(accumulatedValue));
    } else {
      cache.put(entityName, Integer.valueOf(value));
    } 
    if (accumulatedValue > this.values[index]) {
      this.values[index] = accumulatedValue;
      this.names[index] = entityName;
    } 
    if (index == XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT.ordinal() || index == XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT
      .ordinal())
      this.totalValue[XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal()] = this.totalValue[XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal()] + value; 
  }
  
  public int getValue(XMLSecurityManager.Limit limit) {
    return this.values[limit.ordinal()];
  }
  
  public int getValue(int index) {
    return this.values[index];
  }
  
  public int getTotalValue(XMLSecurityManager.Limit limit) {
    return this.totalValue[limit.ordinal()];
  }
  
  public int getTotalValue(int index) {
    return this.totalValue[index];
  }
  
  public int getValueByIndex(int index) {
    return this.values[index];
  }
  
  public void startEntity(String name) {
    this.entityStart = name;
  }
  
  public boolean isTracking(String name) {
    if (this.entityStart == null)
      return false; 
    return this.entityStart.equals(name);
  }
  
  public void endEntity(XMLSecurityManager.Limit limit, String name) {
    this.entityStart = "";
    Map<String, Integer> cache = this.caches[limit.ordinal()];
    if (cache != null)
      cache.remove(name); 
  }
  
  public void debugPrint(XMLSecurityManager securityManager) {
    Formatter formatter = new Formatter();
    System.out.println(formatter.format("%30s %15s %15s %15s %30s", new Object[] { "Property", "Limit", "Total size", "Size", "Entity Name" }));
    for (XMLSecurityManager.Limit limit : XMLSecurityManager.Limit.values()) {
      formatter = new Formatter();
      System.out.println(formatter.format("%30s %15d %15d %15d %30s", new Object[] { limit
              .name(), 
              Integer.valueOf(securityManager.getLimit(limit)), 
              Integer.valueOf(this.totalValue[limit.ordinal()]), 
              Integer.valueOf(this.values[limit.ordinal()]), this.names[limit
                .ordinal()] }));
    } 
  }
  
  public enum XMLLimitAnalyzer {
  
  }
}
