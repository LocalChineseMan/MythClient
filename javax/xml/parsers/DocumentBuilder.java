package javax.xml.parsers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.validation.Schema;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class DocumentBuilder {
  public void reset() {
    throw new UnsupportedOperationException("This DocumentBuilder, \"" + 
        getClass().getName() + "\", does not support the reset functionality." + "  Specification \"" + 
        getClass().getPackage().getSpecificationTitle() + "\"" + " version \"" + 
        getClass().getPackage().getSpecificationVersion() + "\"");
  }
  
  public Document parse(InputStream is) throws SAXException, IOException {
    if (is == null)
      throw new IllegalArgumentException("InputStream cannot be null"); 
    InputSource in = new InputSource(is);
    return parse(in);
  }
  
  public Document parse(InputStream is, String systemId) throws SAXException, IOException {
    if (is == null)
      throw new IllegalArgumentException("InputStream cannot be null"); 
    InputSource in = new InputSource(is);
    in.setSystemId(systemId);
    return parse(in);
  }
  
  public Document parse(String uri) throws SAXException, IOException {
    if (uri == null)
      throw new IllegalArgumentException("URI cannot be null"); 
    InputSource in = new InputSource(uri);
    return parse(in);
  }
  
  public Document parse(File f) throws SAXException, IOException {
    if (f == null)
      throw new IllegalArgumentException("File cannot be null"); 
    InputSource in = new InputSource(f.toURI().toASCIIString());
    return parse(in);
  }
  
  public abstract Document parse(InputSource paramInputSource) throws SAXException, IOException;
  
  public abstract boolean isNamespaceAware();
  
  public abstract boolean isValidating();
  
  public abstract void setEntityResolver(EntityResolver paramEntityResolver);
  
  public abstract void setErrorHandler(ErrorHandler paramErrorHandler);
  
  public abstract Document newDocument();
  
  public abstract DOMImplementation getDOMImplementation();
  
  public Schema getSchema() {
    throw new UnsupportedOperationException("This parser does not support specification \"" + 
        
        getClass().getPackage().getSpecificationTitle() + "\" version \"" + 
        
        getClass().getPackage().getSpecificationVersion() + "\"");
  }
  
  public boolean isXIncludeAware() {
    throw new UnsupportedOperationException("This parser does not support specification \"" + 
        
        getClass().getPackage().getSpecificationTitle() + "\" version \"" + 
        
        getClass().getPackage().getSpecificationVersion() + "\"");
  }
}
