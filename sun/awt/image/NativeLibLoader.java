package sun.awt.image;

import java.security.AccessController;
import java.security.PrivilegedAction;

class NativeLibLoader {
  static void loadLibraries() {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("awt");
            return null;
          }
        });
  }
}
