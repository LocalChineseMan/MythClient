package org.apache.logging.log4j.message;

import java.io.Serializable;

public class DefaultFlowMessageFactory implements FlowMessageFactory, Serializable {
  private static final String EXIT_DEFAULT_PREFIX = "Exit";
  
  private static final String ENTRY_DEFAULT_PREFIX = "Enter";
  
  private static final long serialVersionUID = 8578655591131397576L;
  
  private final String entryText;
  
  private final String exitText;
  
  public DefaultFlowMessageFactory() {
    this("Enter", "Exit");
  }
  
  public DefaultFlowMessageFactory(String entryText, String exitText) {
    this.entryText = entryText;
    this.exitText = exitText;
  }
  
  public String getEntryText() {
    return this.entryText;
  }
  
  public String getExitText() {
    return this.exitText;
  }
  
  public EntryMessage newEntryMessage(Message message) {
    return (EntryMessage)new SimpleEntryMessage(this.entryText, makeImmutable(message));
  }
  
  private Message makeImmutable(Message message) {
    if (!(message instanceof ReusableMessage))
      return message; 
    return new SimpleMessage(message.getFormattedMessage());
  }
  
  public ExitMessage newExitMessage(EntryMessage message) {
    return (ExitMessage)new SimpleExitMessage(this.exitText, message);
  }
  
  public ExitMessage newExitMessage(Object result, EntryMessage message) {
    return (ExitMessage)new SimpleExitMessage(this.exitText, result, message);
  }
  
  public ExitMessage newExitMessage(Object result, Message message) {
    return (ExitMessage)new SimpleExitMessage(this.exitText, result, message);
  }
  
  private static final class DefaultFlowMessageFactory {}
  
  private static final class DefaultFlowMessageFactory {}
  
  private static class DefaultFlowMessageFactory {}
}
