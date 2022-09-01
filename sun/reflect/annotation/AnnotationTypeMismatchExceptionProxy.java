package sun.reflect.annotation;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.reflect.Method;

class AnnotationTypeMismatchExceptionProxy extends ExceptionProxy {
  private static final long serialVersionUID = 7844069490309503934L;
  
  private Method member;
  
  private String foundType;
  
  AnnotationTypeMismatchExceptionProxy(String paramString) {
    this.foundType = paramString;
  }
  
  AnnotationTypeMismatchExceptionProxy setMember(Method paramMethod) {
    this.member = paramMethod;
    return this;
  }
  
  protected RuntimeException generateException() {
    return new AnnotationTypeMismatchException(this.member, this.foundType);
  }
}
