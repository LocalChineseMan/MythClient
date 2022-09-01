package joptsimple;

public interface ValueConverter<V> {
  V convert(String paramString);
  
  Class<V> valueType();
  
  String valuePattern();
}
