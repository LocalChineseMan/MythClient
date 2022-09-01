package org.apache.logging.log4j.message;

public interface MessageFactory {
  Message newMessage(Object paramObject);
  
  Message newMessage(String paramString);
  
  Message newMessage(String paramString, Object... paramVarArgs);
}
