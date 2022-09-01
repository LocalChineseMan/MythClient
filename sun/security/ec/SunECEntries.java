package sun.security.ec;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

final class SunECEntries {
  static void putEntries(Map<Object, Object> paramMap, boolean paramBoolean) {
    paramMap.put("KeyFactory.EC", "sun.security.ec.ECKeyFactory");
    paramMap.put("Alg.Alias.KeyFactory.EllipticCurve", "EC");
    paramMap.put("KeyFactory.EC ImplementedIn", "Software");
    paramMap.put("AlgorithmParameters.EC", "sun.security.ec.ECParameters");
    paramMap.put("Alg.Alias.AlgorithmParameters.EllipticCurve", "EC");
    paramMap.put("Alg.Alias.AlgorithmParameters.1.2.840.10045.2.1", "EC");
    paramMap.put("AlgorithmParameters.EC KeySize", "256");
    paramMap.put("AlgorithmParameters.EC ImplementedIn", "Software");
    boolean bool = true;
    StringBuilder stringBuilder = new StringBuilder();
    Pattern pattern = Pattern.compile(",|\\[|\\]");
    Collection<? extends NamedCurve> collection = CurveDB.getSupportedCurves();
    for (NamedCurve namedCurve : collection) {
      if (!bool) {
        stringBuilder.append("|");
      } else {
        bool = false;
      } 
      stringBuilder.append("[");
      String[] arrayOfString = pattern.split(namedCurve.getName());
      for (String str1 : arrayOfString) {
        stringBuilder.append(str1.trim());
        stringBuilder.append(",");
      } 
      stringBuilder.append(namedCurve.getObjectId());
      stringBuilder.append("]");
    } 
    paramMap.put("AlgorithmParameters.EC SupportedCurves", stringBuilder.toString());
    if (!paramBoolean)
      return; 
    paramMap.put("Signature.NONEwithECDSA", "sun.security.ec.ECDSASignature$Raw");
    paramMap.put("Signature.SHA1withECDSA", "sun.security.ec.ECDSASignature$SHA1");
    paramMap.put("Alg.Alias.Signature.OID.1.2.840.10045.4.1", "SHA1withECDSA");
    paramMap.put("Alg.Alias.Signature.1.2.840.10045.4.1", "SHA1withECDSA");
    paramMap.put("Signature.SHA224withECDSA", "sun.security.ec.ECDSASignature$SHA224");
    paramMap.put("Alg.Alias.Signature.OID.1.2.840.10045.4.3.1", "SHA224withECDSA");
    paramMap.put("Alg.Alias.Signature.1.2.840.10045.4.3.1", "SHA224withECDSA");
    paramMap.put("Signature.SHA256withECDSA", "sun.security.ec.ECDSASignature$SHA256");
    paramMap.put("Alg.Alias.Signature.OID.1.2.840.10045.4.3.2", "SHA256withECDSA");
    paramMap.put("Alg.Alias.Signature.1.2.840.10045.4.3.2", "SHA256withECDSA");
    paramMap.put("Signature.SHA384withECDSA", "sun.security.ec.ECDSASignature$SHA384");
    paramMap.put("Alg.Alias.Signature.OID.1.2.840.10045.4.3.3", "SHA384withECDSA");
    paramMap.put("Alg.Alias.Signature.1.2.840.10045.4.3.3", "SHA384withECDSA");
    paramMap.put("Signature.SHA512withECDSA", "sun.security.ec.ECDSASignature$SHA512");
    paramMap.put("Alg.Alias.Signature.OID.1.2.840.10045.4.3.4", "SHA512withECDSA");
    paramMap.put("Alg.Alias.Signature.1.2.840.10045.4.3.4", "SHA512withECDSA");
    String str = "java.security.interfaces.ECPublicKey|java.security.interfaces.ECPrivateKey";
    paramMap.put("Signature.NONEwithECDSA SupportedKeyClasses", str);
    paramMap.put("Signature.SHA1withECDSA SupportedKeyClasses", str);
    paramMap.put("Signature.SHA224withECDSA SupportedKeyClasses", str);
    paramMap.put("Signature.SHA256withECDSA SupportedKeyClasses", str);
    paramMap.put("Signature.SHA384withECDSA SupportedKeyClasses", str);
    paramMap.put("Signature.SHA512withECDSA SupportedKeyClasses", str);
    paramMap.put("Signature.SHA1withECDSA KeySize", "256");
    paramMap.put("Signature.NONEwithECDSA ImplementedIn", "Software");
    paramMap.put("Signature.SHA1withECDSA ImplementedIn", "Software");
    paramMap.put("Signature.SHA224withECDSA ImplementedIn", "Software");
    paramMap.put("Signature.SHA256withECDSA ImplementedIn", "Software");
    paramMap.put("Signature.SHA384withECDSA ImplementedIn", "Software");
    paramMap.put("Signature.SHA512withECDSA ImplementedIn", "Software");
    paramMap.put("KeyPairGenerator.EC", "sun.security.ec.ECKeyPairGenerator");
    paramMap.put("Alg.Alias.KeyPairGenerator.EllipticCurve", "EC");
    paramMap.put("KeyPairGenerator.EC KeySize", "256");
    paramMap.put("KeyPairGenerator.EC ImplementedIn", "Software");
    paramMap.put("KeyAgreement.ECDH", "sun.security.ec.ECDHKeyAgreement");
    paramMap.put("KeyAgreement.ECDH SupportedKeyClasses", str);
    paramMap.put("KeyAgreement.ECDH ImplementedIn", "Software");
  }
}
