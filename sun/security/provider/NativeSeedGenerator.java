package sun.security.provider;

import java.io.IOException;

class NativeSeedGenerator extends SeedGenerator {
  NativeSeedGenerator(String paramString) throws IOException {
    if (!nativeGenerateSeed(new byte[2]))
      throw new IOException("Required native CryptoAPI features not  available on this machine"); 
  }
  
  private static native boolean nativeGenerateSeed(byte[] paramArrayOfbyte);
  
  void getSeedBytes(byte[] paramArrayOfbyte) {
    if (!nativeGenerateSeed(paramArrayOfbyte))
      throw new InternalError("Unexpected CryptoAPI failure generating seed"); 
  }
}
