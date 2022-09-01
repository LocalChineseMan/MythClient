package sun.reflect;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

class ClassDefiner {
  static final Unsafe unsafe = Unsafe.getUnsafe();
  
  static Class<?> defineClass(String paramString, byte[] paramArrayOfbyte, int paramInt1, int paramInt2, final ClassLoader parentClassLoader) {
    ClassLoader classLoader = AccessController.<ClassLoader>doPrivileged(new PrivilegedAction<ClassLoader>() {
          public ClassLoader run() {
            return (ClassLoader)new DelegatingClassLoader(parentClassLoader);
          }
        });
    return unsafe.defineClass(paramString, paramArrayOfbyte, paramInt1, paramInt2, classLoader, null);
  }
}
