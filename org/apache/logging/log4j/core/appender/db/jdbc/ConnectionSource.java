package org.apache.logging.log4j.core.appender.db.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.logging.log4j.core.LifeCycle;

public interface ConnectionSource extends LifeCycle {
  Connection getConnection() throws SQLException;
  
  String toString();
}
