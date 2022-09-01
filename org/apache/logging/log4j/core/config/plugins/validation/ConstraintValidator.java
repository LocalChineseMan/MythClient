package org.apache.logging.log4j.core.config.plugins.validation;

public interface ConstraintValidator<A extends java.lang.annotation.Annotation> {
  void initialize(A paramA);
  
  boolean isValid(String paramString, Object paramObject);
}
