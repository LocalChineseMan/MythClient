package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.NamingException;

public interface InitialContextFactoryBuilder {
  InitialContextFactory createInitialContextFactory(Hashtable<?, ?> paramHashtable) throws NamingException;
}
