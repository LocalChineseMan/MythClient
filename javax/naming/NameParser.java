package javax.naming;

public interface NameParser {
  Name parse(String paramString) throws NamingException;
}
