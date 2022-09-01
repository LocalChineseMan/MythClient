package org.w3c.dom;

public interface Text extends CharacterData {
  Text splitText(int paramInt) throws DOMException;
  
  boolean isElementContentWhitespace();
  
  String getWholeText();
  
  Text replaceWholeText(String paramString) throws DOMException;
}
