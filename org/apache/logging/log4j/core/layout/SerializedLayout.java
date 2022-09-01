package org.apache.logging.log4j.core.layout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Deprecated
@Plugin(name = "SerializedLayout", category = "Core", elementType = "layout", printObject = true)
public final class SerializedLayout extends AbstractLayout<LogEvent> {
  private static byte[] serializedHeader;
  
  static {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      (new ObjectOutputStream(baos)).close();
      serializedHeader = baos.toByteArray();
    } catch (Exception ex) {
      LOGGER.error("Unable to generate Object stream header", ex);
    } 
  }
  
  private SerializedLayout() {
    super(null, null, null);
    LOGGER.warn("SerializedLayout is deprecated due to the inherent security weakness in Java Serialization, see https://www.owasp.org/index.php/Deserialization_of_untrusted_data Consider using another layout, e.g. JsonLayout");
  }
  
  public byte[] toByteArray(LogEvent event) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (PrivateObjectOutputStream null = new PrivateObjectOutputStream(this, baos)) {
      privateObjectOutputStream.writeObject(event);
      privateObjectOutputStream.reset();
    } catch (IOException ioe) {
      LOGGER.error("Serialization of LogEvent failed.", ioe);
    } 
    return baos.toByteArray();
  }
  
  public LogEvent toSerializable(LogEvent event) {
    return event;
  }
  
  @Deprecated
  @PluginFactory
  public static SerializedLayout createLayout() {
    return new SerializedLayout();
  }
  
  public byte[] getHeader() {
    return serializedHeader;
  }
  
  public String getContentType() {
    return "application/octet-stream";
  }
  
  private class SerializedLayout {}
}
