package sun.reflect.generics.factory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import sun.reflect.generics.tree.FieldTypeSignature;

public interface GenericsFactory {
  TypeVariable<?> makeTypeVariable(String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature);
  
  ParameterizedType makeParameterizedType(Type paramType1, Type[] paramArrayOfType, Type paramType2);
  
  TypeVariable<?> findTypeVariable(String paramString);
  
  WildcardType makeWildcard(FieldTypeSignature[] paramArrayOfFieldTypeSignature1, FieldTypeSignature[] paramArrayOfFieldTypeSignature2);
  
  Type makeNamedType(String paramString);
  
  Type makeArrayType(Type paramType);
  
  Type makeByte();
  
  Type makeBool();
  
  Type makeShort();
  
  Type makeChar();
  
  Type makeInt();
  
  Type makeLong();
  
  Type makeFloat();
  
  Type makeDouble();
  
  Type makeVoid();
}
