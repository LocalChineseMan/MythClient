package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class IssuerAlternativeNameExtension extends Extension implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.extensions.IssuerAlternativeName";
  
  public static final String NAME = "IssuerAlternativeName";
  
  public static final String ISSUER_NAME = "issuer_name";
  
  GeneralNames names = null;
  
  private void encodeThis() throws IOException {
    if (this.names == null || this.names.isEmpty()) {
      this.extensionValue = null;
      return;
    } 
    DerOutputStream derOutputStream = new DerOutputStream();
    this.names.encode(derOutputStream);
    this.extensionValue = derOutputStream.toByteArray();
  }
  
  public IssuerAlternativeNameExtension(GeneralNames paramGeneralNames) throws IOException {
    this.names = paramGeneralNames;
    this.extensionId = PKIXExtensions.IssuerAlternativeName_Id;
    this.critical = false;
    encodeThis();
  }
  
  public IssuerAlternativeNameExtension(Boolean paramBoolean, GeneralNames paramGeneralNames) throws IOException {
    this.names = paramGeneralNames;
    this.extensionId = PKIXExtensions.IssuerAlternativeName_Id;
    this.critical = paramBoolean.booleanValue();
    encodeThis();
  }
  
  public IssuerAlternativeNameExtension() {
    this.extensionId = PKIXExtensions.IssuerAlternativeName_Id;
    this.critical = false;
    this.names = new GeneralNames();
  }
  
  public IssuerAlternativeNameExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.extensionId = PKIXExtensions.IssuerAlternativeName_Id;
    this.critical = paramBoolean.booleanValue();
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    if (derValue.data == null) {
      this.names = new GeneralNames();
      return;
    } 
    this.names = new GeneralNames(derValue);
  }
  
  public String toString() {
    String str = super.toString() + "IssuerAlternativeName [\n";
    if (this.names == null) {
      str = str + "  null\n";
    } else {
      for (GeneralName generalName : this.names.names())
        str = str + "  " + generalName + "\n"; 
    } 
    str = str + "]\n";
    return str;
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.IssuerAlternativeName_Id;
      this.critical = false;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramString.equalsIgnoreCase("issuer_name")) {
      if (!(paramObject instanceof GeneralNames))
        throw new IOException("Attribute value should be of type GeneralNames."); 
      this.names = (GeneralNames)paramObject;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:IssuerAlternativeName.");
    } 
    encodeThis();
  }
  
  public GeneralNames get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("issuer_name"))
      return this.names; 
    throw new IOException("Attribute name not recognized by CertAttrSet:IssuerAlternativeName.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("issuer_name")) {
      this.names = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:IssuerAlternativeName.");
    } 
    encodeThis();
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("issuer_name");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() {
    return "IssuerAlternativeName";
  }
}
