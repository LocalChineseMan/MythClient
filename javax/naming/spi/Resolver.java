package javax.naming.spi;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

public interface Resolver {
  ResolveResult resolveToClass(Name paramName, Class<? extends Context> paramClass) throws NamingException;
  
  ResolveResult resolveToClass(String paramString, Class<? extends Context> paramClass) throws NamingException;
}
