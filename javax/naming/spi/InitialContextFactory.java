package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;

public interface InitialContextFactory {
  Context getInitialContext(Hashtable<?, ?> paramHashtable) throws NamingException;
}
