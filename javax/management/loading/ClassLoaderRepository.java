package javax.management.loading;

public interface ClassLoaderRepository {
  Class<?> loadClass(String paramString) throws ClassNotFoundException;
  
  Class<?> loadClassWithout(ClassLoader paramClassLoader, String paramString) throws ClassNotFoundException;
  
  Class<?> loadClassBefore(ClassLoader paramClassLoader, String paramString) throws ClassNotFoundException;
}
