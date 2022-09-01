package java.lang.invoke;

import java.lang.invoke.InfoFromMemberName;
import java.lang.invoke.MemberName;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.MethodHandleNatives;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;

final class InfoFromMemberName implements MethodHandleInfo {
  private final MemberName member;
  
  private final int referenceKind;
  
  InfoFromMemberName(MethodHandles.Lookup paramLookup, MemberName paramMemberName, byte paramByte) {
    assert paramMemberName.isResolved() || paramMemberName.isMethodHandleInvoke();
    assert paramMemberName.referenceKindIsConsistentWith(paramByte);
    this.member = paramMemberName;
    this.referenceKind = paramByte;
  }
  
  public Class<?> getDeclaringClass() {
    return this.member.getDeclaringClass();
  }
  
  public String getName() {
    return this.member.getName();
  }
  
  public MethodType getMethodType() {
    return this.member.getMethodOrFieldType();
  }
  
  public int getModifiers() {
    return this.member.getModifiers();
  }
  
  public int getReferenceKind() {
    return this.referenceKind;
  }
  
  public String toString() {
    return MethodHandleInfo.toString(getReferenceKind(), getDeclaringClass(), getName(), getMethodType());
  }
  
  public <T extends Member> T reflectAs(Class<T> paramClass, MethodHandles.Lookup paramLookup) {
    if (this.member.isMethodHandleInvoke() && !this.member.isVarargs())
      throw new IllegalArgumentException("cannot reflect signature polymorphic method"); 
    Member member = AccessController.<Member>doPrivileged((PrivilegedAction<Member>)new Object(this));
    try {
      Class<?> clazz = getDeclaringClass();
      byte b = (byte)getReferenceKind();
      paramLookup.checkAccess(b, clazz, convertToMemberName(b, member));
    } catch (IllegalAccessException illegalAccessException) {
      throw new IllegalArgumentException(illegalAccessException);
    } 
    return paramClass.cast(member);
  }
  
  private Member reflectUnchecked() throws ReflectiveOperationException {
    byte b = (byte)getReferenceKind();
    Class<?> clazz = getDeclaringClass();
    boolean bool = Modifier.isPublic(getModifiers());
    if (MethodHandleNatives.refKindIsMethod(b)) {
      if (bool)
        return clazz.getMethod(getName(), getMethodType().parameterArray()); 
      return clazz.getDeclaredMethod(getName(), getMethodType().parameterArray());
    } 
    if (MethodHandleNatives.refKindIsConstructor(b)) {
      if (bool)
        return clazz.getConstructor(getMethodType().parameterArray()); 
      return clazz.getDeclaredConstructor(getMethodType().parameterArray());
    } 
    if (MethodHandleNatives.refKindIsField(b)) {
      if (bool)
        return clazz.getField(getName()); 
      return clazz.getDeclaredField(getName());
    } 
    throw new IllegalArgumentException("referenceKind=" + b);
  }
  
  private static MemberName convertToMemberName(byte paramByte, Member paramMember) throws IllegalAccessException {
    if (paramMember instanceof Method) {
      boolean bool = (paramByte == 7) ? true : false;
      return new MemberName((Method)paramMember, bool);
    } 
    if (paramMember instanceof Constructor)
      return new MemberName((Constructor)paramMember); 
    if (paramMember instanceof Field) {
      boolean bool = (paramByte == 3 || paramByte == 4) ? true : false;
      return new MemberName((Field)paramMember, bool);
    } 
    throw new InternalError(paramMember.getClass().getName());
  }
}
