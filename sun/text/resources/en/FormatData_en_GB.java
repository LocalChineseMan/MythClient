package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_GB extends ParallelListResourceBundle {
  protected final Object[][] getContents() {
    return new Object[][] { { "TimePatterns", { "HH:mm:ss 'o''clock' z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "EEEE, d MMMM yyyy", "dd MMMM yyyy", "dd-MMM-yyyy", "dd/MM/yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ" } };
  }
}
