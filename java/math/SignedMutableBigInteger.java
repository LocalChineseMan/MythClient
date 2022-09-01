package java.math;

class SignedMutableBigInteger extends MutableBigInteger {
  int sign = 1;
  
  SignedMutableBigInteger() {}
  
  SignedMutableBigInteger(int paramInt) {
    super(paramInt);
  }
  
  SignedMutableBigInteger(MutableBigInteger paramMutableBigInteger) {
    super(paramMutableBigInteger);
  }
  
  void signedAdd(SignedMutableBigInteger paramSignedMutableBigInteger) {
    if (this.sign == paramSignedMutableBigInteger.sign) {
      add(paramSignedMutableBigInteger);
    } else {
      this.sign *= subtract(paramSignedMutableBigInteger);
    } 
  }
  
  void signedAdd(MutableBigInteger paramMutableBigInteger) {
    if (this.sign == 1) {
      add(paramMutableBigInteger);
    } else {
      this.sign *= subtract(paramMutableBigInteger);
    } 
  }
  
  void signedSubtract(SignedMutableBigInteger paramSignedMutableBigInteger) {
    if (this.sign == paramSignedMutableBigInteger.sign) {
      this.sign *= subtract(paramSignedMutableBigInteger);
    } else {
      add(paramSignedMutableBigInteger);
    } 
  }
  
  void signedSubtract(MutableBigInteger paramMutableBigInteger) {
    if (this.sign == 1) {
      this.sign *= subtract(paramMutableBigInteger);
    } else {
      add(paramMutableBigInteger);
    } 
    if (this.intLen == 0)
      this.sign = 1; 
  }
  
  public String toString() {
    return toBigInteger(this.sign).toString();
  }
}
