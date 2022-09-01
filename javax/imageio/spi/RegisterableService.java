package javax.imageio.spi;

public interface RegisterableService {
  void onRegistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass);
  
  void onDeregistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass);
}
