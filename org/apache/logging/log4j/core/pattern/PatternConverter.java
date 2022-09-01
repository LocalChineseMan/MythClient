package org.apache.logging.log4j.core.pattern;

public interface PatternConverter {
  public static final String CATEGORY = "Converter";
  
  void format(Object paramObject, StringBuilder paramStringBuilder);
  
  String getName();
  
  String getStyleClass(Object paramObject);
}
