package org.apache.logging.log4j.message;

public interface FlowMessageFactory {
  EntryMessage newEntryMessage(Message paramMessage);
  
  ExitMessage newExitMessage(Object paramObject, Message paramMessage);
  
  ExitMessage newExitMessage(EntryMessage paramEntryMessage);
  
  ExitMessage newExitMessage(Object paramObject, EntryMessage paramEntryMessage);
}
