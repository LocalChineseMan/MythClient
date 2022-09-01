package java.lang.invoke;

import java.lang.invoke.BoundMethodHandle;
import java.lang.invoke.BoundMethodHandle$Species_L3;
import java.lang.invoke.LambdaForm;
import java.lang.invoke.MethodType;

final class BoundMethodHandle$Species_L3 extends BoundMethodHandle {
  static BoundMethodHandle.SpeciesData SPECIES_DATA = BoundMethodHandle.SpeciesData.getForClass("LLL", (Class)BoundMethodHandle$Species_L3.class);
  
  final Object argL0;
  
  final Object argL1;
  
  final Object argL2;
  
  private BoundMethodHandle$Species_L3(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject1, Object paramObject2, Object paramObject3) {
    super(paramMethodType, paramLambdaForm);
    this.argL0 = paramObject1;
    this.argL1 = paramObject2;
    this.argL2 = paramObject3;
  }
  
  final BoundMethodHandle.SpeciesData speciesData() {
    return SPECIES_DATA;
  }
  
  final int fieldCount() {
    return 3;
  }
  
  static BoundMethodHandle make(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject1, Object paramObject2, Object paramObject3) {
    return new BoundMethodHandle$Species_L3(paramMethodType, paramLambdaForm, paramObject1, paramObject2, paramObject3);
  }
  
  final BoundMethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm) {
    return new BoundMethodHandle$Species_L3(paramMethodType, paramLambdaForm, this.argL0, this.argL1, this.argL2);
  }
  
  final BoundMethodHandle copyWithExtendL(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject) throws Throwable {
    return SPECIES_DATA.extendWith((byte)0).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argL1, this.argL2, paramObject);
  }
  
  final BoundMethodHandle copyWithExtendI(MethodType paramMethodType, LambdaForm paramLambdaForm, int paramInt) throws Throwable {
    return SPECIES_DATA.extendWith((byte)1).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argL1, this.argL2, paramInt);
  }
  
  final BoundMethodHandle copyWithExtendJ(MethodType paramMethodType, LambdaForm paramLambdaForm, long paramLong) throws Throwable {
    return SPECIES_DATA.extendWith((byte)2).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argL1, this.argL2, paramLong);
  }
  
  final BoundMethodHandle copyWithExtendF(MethodType paramMethodType, LambdaForm paramLambdaForm, float paramFloat) throws Throwable {
    return SPECIES_DATA.extendWith((byte)3).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argL1, this.argL2, paramFloat);
  }
  
  final BoundMethodHandle copyWithExtendD(MethodType paramMethodType, LambdaForm paramLambdaForm, double paramDouble) throws Throwable {
    return SPECIES_DATA.extendWith((byte)4).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argL1, this.argL2, paramDouble);
  }
}
