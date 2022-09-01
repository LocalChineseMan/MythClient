package sun.reflect;

import java.lang.reflect.Field;

abstract class UnsafeQualifiedStaticFieldAccessorImpl extends UnsafeStaticFieldAccessorImpl {
  protected final boolean isReadOnly;
  
  UnsafeQualifiedStaticFieldAccessorImpl(Field paramField, boolean paramBoolean) {
    super(paramField);
    this.isReadOnly = paramBoolean;
  }
}
