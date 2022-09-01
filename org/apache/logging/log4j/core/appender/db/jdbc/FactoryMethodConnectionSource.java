package org.apache.logging.log4j.core.appender.db.jdbc;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

@Plugin(name = "ConnectionFactory", category = "Core", elementType = "connectionSource", printObject = true)
public final class FactoryMethodConnectionSource extends AbstractConnectionSource {
  private static final Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  private final DataSource dataSource;
  
  private final String description;
  
  private FactoryMethodConnectionSource(DataSource dataSource, String className, String methodName, String returnType) {
    this.dataSource = dataSource;
    this.description = "factory{ public static " + returnType + ' ' + className + '.' + methodName + "() }";
  }
  
  public Connection getConnection() throws SQLException {
    return this.dataSource.getConnection();
  }
  
  public String toString() {
    return this.description;
  }
  
  @PluginFactory
  public static FactoryMethodConnectionSource createConnectionSource(@PluginAttribute("class") String className, @PluginAttribute("method") String methodName) {
    Method method;
    Object object;
    if (Strings.isEmpty(className) || Strings.isEmpty(methodName)) {
      LOGGER.error("No class name or method name specified for the connection factory method.");
      return null;
    } 
    try {
      Class<?> factoryClass = Loader.loadClass(className);
      method = factoryClass.getMethod(methodName, new Class[0]);
    } catch (Exception e) {
      LOGGER.error(e.toString(), e);
      return null;
    } 
    Class<?> returnType = method.getReturnType();
    String returnTypeString = returnType.getName();
    if (returnType == DataSource.class) {
      try {
        object = method.invoke(null, new Object[0]);
        returnTypeString = returnTypeString + "[" + object + ']';
      } catch (Exception e) {
        LOGGER.error(e.toString(), e);
        return null;
      } 
    } else if (returnType == Connection.class) {
      object = new Object(method);
    } else {
      LOGGER.error("Method [{}.{}()] returns unsupported type [{}].", className, methodName, returnType
          .getName());
      return null;
    } 
    return new FactoryMethodConnectionSource((DataSource)object, className, methodName, returnTypeString);
  }
}
