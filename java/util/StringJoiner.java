package java.util;

public final class StringJoiner {
  private final String prefix;
  
  private final String delimiter;
  
  private final String suffix;
  
  private StringBuilder value;
  
  private String emptyValue;
  
  public StringJoiner(CharSequence paramCharSequence) {
    this(paramCharSequence, "", "");
  }
  
  public StringJoiner(CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3) {
    Objects.requireNonNull(paramCharSequence2, "The prefix must not be null");
    Objects.requireNonNull(paramCharSequence1, "The delimiter must not be null");
    Objects.requireNonNull(paramCharSequence3, "The suffix must not be null");
    this.prefix = paramCharSequence2.toString();
    this.delimiter = paramCharSequence1.toString();
    this.suffix = paramCharSequence3.toString();
    this.emptyValue = this.prefix + this.suffix;
  }
  
  public StringJoiner setEmptyValue(CharSequence paramCharSequence) {
    this
      .emptyValue = ((CharSequence)Objects.<CharSequence>requireNonNull(paramCharSequence, "The empty value must not be null")).toString();
    return this;
  }
  
  public String toString() {
    if (this.value == null)
      return this.emptyValue; 
    if (this.suffix.equals(""))
      return this.value.toString(); 
    int i = this.value.length();
    String str = this.value.append(this.suffix).toString();
    this.value.setLength(i);
    return str;
  }
  
  public StringJoiner add(CharSequence paramCharSequence) {
    prepareBuilder().append(paramCharSequence);
    return this;
  }
  
  public StringJoiner merge(StringJoiner paramStringJoiner) {
    Objects.requireNonNull(paramStringJoiner);
    if (paramStringJoiner.value != null) {
      int i = paramStringJoiner.value.length();
      StringBuilder stringBuilder = prepareBuilder();
      stringBuilder.append(paramStringJoiner.value, paramStringJoiner.prefix.length(), i);
    } 
    return this;
  }
  
  private StringBuilder prepareBuilder() {
    if (this.value != null) {
      this.value.append(this.delimiter);
    } else {
      this.value = (new StringBuilder()).append(this.prefix);
    } 
    return this.value;
  }
  
  public int length() {
    return (this.value != null) ? (this.value.length() + this.suffix.length()) : this.emptyValue
      .length();
  }
}
