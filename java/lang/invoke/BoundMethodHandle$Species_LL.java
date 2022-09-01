package java.lang.invoke;

import java.lang.invoke.BoundMethodHandle;
import java.lang.invoke.BoundMethodHandle$Species_LL;
import java.lang.invoke.LambdaForm;
import java.lang.invoke.MethodType;

final class BoundMethodHandle$Species_LL extends BoundMethodHandle {
  static BoundMethodHandle.SpeciesData SPECIES_DATA = BoundMethodHandle.SpeciesData.getForClass("LL", (Class)BoundMethodHandle$Species_LL.class);
  
  final Object argL0;
  
  final Object argL1;
  
  private BoundMethodHandle$Species_LL(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject1, Object paramObject2) {
    super(paramMethodType, paramLambdaForm);
    this.argL0 = paramObject1;
    this.argL1 = paramObject2;
  }
  
  final BoundMethodHandle.SpeciesData speciesData() {
    return SPECIES_DATA;
  }
  
  final int fieldCount() {
    return 2;
  }
  
  static BoundMethodHandle make(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject1, Object paramObject2) {
    return new BoundMethodHandle$Species_LL(paramMethodType, paramLambdaForm, paramObject1, paramObject2);
  }
  
  final BoundMethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm) {
    return new BoundMethodHandle$Species_LL(paramMethodType, paramLambdaForm, this.argL0, this.argL1);
  }
  
  final BoundMethodHandle copyWithExtendL(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject) throws Throwable {
    return SPECIES_DATA.extendWith((byte)0).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argL1, paramObject);
  }
  
  final BoundMethodHandle copyWithExtendI(MethodType paramMethodType, LambdaForm paramLambdaForm, int paramInt) throws Throwable {
    return SPECIES_DATA.extendWith((byte)1).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argL1, paramInt);
  }
  
  final BoundMethodHandle copyWithExtendJ(MethodType paramMethodType, LambdaForm paramLambdaForm, long paramLong) throws Throwable {
    return SPECIES_DATA.extendWith((byte)2).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argL1, paramLong);
  }
  
  final BoundMethodHandle copyWithExtendF(MethodType paramMethodType, LambdaForm paramLambdaForm, float paramFloat) throws Throwable {
    return SPECIES_DATA.extendWith((byte)3).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argL1, paramFloat);
  }
  
  final BoundMethodHandle copyWithExtendD(MethodType paramMethodType, LambdaForm paramLambdaForm, double paramDouble) throws Throwable {
    return SPECIES_DATA.extendWith((byte)4).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argL1, paramDouble);
  }
}
