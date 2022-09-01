package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.Extension;
import java.util.Arrays;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class Extension implements Extension {
  protected ObjectIdentifier extensionId = null;
  
  protected boolean critical = false;
  
  protected byte[] extensionValue = null;
  
  private static final int hashMagic = 31;
  
  public Extension() {}
  
  public Extension(DerValue paramDerValue) throws IOException {
    DerInputStream derInputStream = paramDerValue.toDerInputStream();
    this.extensionId = derInputStream.getOID();
    DerValue derValue = derInputStream.getDerValue();
    if (derValue.tag == 1) {
      this.critical = derValue.getBoolean();
      derValue = derInputStream.getDerValue();
      this.extensionValue = derValue.getOctetString();
    } else {
      this.critical = false;
      this.extensionValue = derValue.getOctetString();
    } 
  }
  
  public Extension(ObjectIdentifier paramObjectIdentifier, boolean paramBoolean, byte[] paramArrayOfbyte) throws IOException {
    this.extensionId = paramObjectIdentifier;
    this.critical = paramBoolean;
    DerValue derValue = new DerValue(paramArrayOfbyte);
    this.extensionValue = derValue.getOctetString();
  }
  
  public Extension(Extension paramExtension) {
    this.extensionId = paramExtension.extensionId;
    this.critical = paramExtension.critical;
    this.extensionValue = paramExtension.extensionValue;
  }
  
  public static Extension newExtension(ObjectIdentifier paramObjectIdentifier, boolean paramBoolean, byte[] paramArrayOfbyte) throws IOException {
    Extension extension = new Extension();
    extension.extensionId = paramObjectIdentifier;
    extension.critical = paramBoolean;
    extension.extensionValue = paramArrayOfbyte;
    return extension;
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    if (paramOutputStream == null)
      throw new NullPointerException(); 
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream1.putOID(this.extensionId);
    if (this.critical)
      derOutputStream1.putBoolean(this.critical); 
    derOutputStream1.putOctetString(this.extensionValue);
    derOutputStream2.write((byte)48, derOutputStream1);
    paramOutputStream.write(derOutputStream2.toByteArray());
  }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    if (this.extensionId == null)
      throw new IOException("Null OID to encode for the extension!"); 
    if (this.extensionValue == null)
      throw new IOException("No value to encode for the extension!"); 
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putOID(this.extensionId);
    if (this.critical)
      derOutputStream.putBoolean(this.critical); 
    derOutputStream.putOctetString(this.extensionValue);
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
  
  public boolean isCritical() {
    return this.critical;
  }
  
  public ObjectIdentifier getExtensionId() {
    return this.extensionId;
  }
  
  public byte[] getValue() {
    return (byte[])this.extensionValue.clone();
  }
  
  public byte[] getExtensionValue() {
    return this.extensionValue;
  }
  
  public String getId() {
    return this.extensionId.toString();
  }
  
  public String toString() {
    String str = "ObjectId: " + this.extensionId.toString();
    if (this.critical) {
      str = str + " Criticality=true\n";
    } else {
      str = str + " Criticality=false\n";
    } 
    return str;
  }
  
  public int hashCode() {
    int i = 0;
    if (this.extensionValue != null) {
      byte[] arrayOfByte = this.extensionValue;
      int j = arrayOfByte.length;
      while (j > 0)
        i += j * arrayOfByte[--j]; 
    } 
    i = i * 31 + this.extensionId.hashCode();
    i = i * 31 + (this.critical ? 1231 : 1237);
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof Extension))
      return false; 
    Extension extension = (Extension)paramObject;
    if (this.critical != extension.critical)
      return false; 
    if (!this.extensionId.equals(extension.extensionId))
      return false; 
    return Arrays.equals(this.extensionValue, extension.extensionValue);
  }
}
