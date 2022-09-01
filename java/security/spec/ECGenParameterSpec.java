package java.security.spec;

public class ECGenParameterSpec implements AlgorithmParameterSpec {
  private String name;
  
  public ECGenParameterSpec(String paramString) {
    if (paramString == null)
      throw new NullPointerException("stdName is null"); 
    this.name = paramString;
  }
  
  public String getName() {
    return this.name;
  }
}
