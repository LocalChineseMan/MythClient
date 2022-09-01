package sun.reflect;

import java.lang.reflect.Field;

abstract class UnsafeQualifiedFieldAccessorImpl extends UnsafeFieldAccessorImpl {
  protected final boolean isReadOnly;
  
  UnsafeQualifiedFieldAccessorImpl(Field paramField, boolean paramBoolean) {
    super(paramField);
    this.isReadOnly = paramBoolean;
  }
}
