package java.text;

import sun.text.normalizer.NormalizerBase;

public final class Normalizer {
  public enum Form {
    NFD, NFC, NFKD, NFKC;
  }
  
  public static String normalize(CharSequence paramCharSequence, Form paramForm) {
    return NormalizerBase.normalize(paramCharSequence.toString(), paramForm);
  }
  
  public static boolean isNormalized(CharSequence paramCharSequence, Form paramForm) {
    return NormalizerBase.isNormalized(paramCharSequence.toString(), paramForm);
  }
}
