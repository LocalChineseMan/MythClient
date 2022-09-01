package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PolicyMappingsExtension extends Extension implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.extensions.PolicyMappings";
  
  public static final String NAME = "PolicyMappings";
  
  public static final String MAP = "map";
  
  private List<CertificatePolicyMap> maps;
  
  private void encodeThis() throws IOException {
    if (this.maps == null || this.maps.isEmpty()) {
      this.extensionValue = null;
      return;
    } 
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    for (CertificatePolicyMap certificatePolicyMap : this.maps)
      certificatePolicyMap.encode(derOutputStream2); 
    derOutputStream1.write((byte)48, derOutputStream2);
    this.extensionValue = derOutputStream1.toByteArray();
  }
  
  public PolicyMappingsExtension(List<CertificatePolicyMap> paramList) throws IOException {
    this.maps = paramList;
    this.extensionId = PKIXExtensions.PolicyMappings_Id;
    this.critical = false;
    encodeThis();
  }
  
  public PolicyMappingsExtension() {
    this.extensionId = PKIXExtensions.KeyUsage_Id;
    this.critical = false;
    this.maps = new ArrayList<>();
  }
  
  public PolicyMappingsExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.extensionId = PKIXExtensions.PolicyMappings_Id;
    this.critical = paramBoolean.booleanValue();
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    if (derValue.tag != 48)
      throw new IOException("Invalid encoding for PolicyMappingsExtension."); 
    this.maps = new ArrayList<>();
    while (derValue.data.available() != 0) {
      DerValue derValue1 = derValue.data.getDerValue();
      CertificatePolicyMap certificatePolicyMap = new CertificatePolicyMap(derValue1);
      this.maps.add(certificatePolicyMap);
    } 
  }
  
  public String toString() {
    if (this.maps == null)
      return ""; 
    return super.toString() + "PolicyMappings [\n" + this.maps
      .toString() + "]\n";
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.PolicyMappings_Id;
      this.critical = false;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramString.equalsIgnoreCase("map")) {
      if (!(paramObject instanceof List))
        throw new IOException("Attribute value should be of type List."); 
      this.maps = (List<CertificatePolicyMap>)paramObject;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:PolicyMappingsExtension.");
    } 
    encodeThis();
  }
  
  public List<CertificatePolicyMap> get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("map"))
      return this.maps; 
    throw new IOException("Attribute name not recognized by CertAttrSet:PolicyMappingsExtension.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("map")) {
      this.maps = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:PolicyMappingsExtension.");
    } 
    encodeThis();
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("map");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() {
    return "PolicyMappings";
  }
}
