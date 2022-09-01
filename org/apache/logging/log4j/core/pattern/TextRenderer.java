package org.apache.logging.log4j.core.pattern;

public interface TextRenderer {
  void render(String paramString1, StringBuilder paramStringBuilder, String paramString2);
  
  void render(StringBuilder paramStringBuilder1, StringBuilder paramStringBuilder2);
}
