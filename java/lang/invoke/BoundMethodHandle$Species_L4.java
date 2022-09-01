package java.lang.invoke;

import java.lang.invoke.BoundMethodHandle;
import java.lang.invoke.BoundMethodHandle$Species_L4;
import java.lang.invoke.LambdaForm;
import java.lang.invoke.MethodType;

final class BoundMethodHandle$Species_L4 extends BoundMethodHandle {
  static BoundMethodHandle.SpeciesData SPECIES_DATA = BoundMethodHandle.SpeciesData.getForClass("LLLL", (Class)BoundMethodHandle$Species_L4.class);
  
  final Object argL0;
  
  final Object argL1;
  
  final Object argL2;
  
  final Object argL3;
  
  private BoundMethodHandle$Species_L4(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) {
    super(paramMethodType, paramLambdaForm);
    this.argL0 = paramObject1;
    this.argL1 = paramObject2;
    this.argL2 = paramObject3;
    this.argL3 = paramObject4;
  }
  
  final BoundMethodHandle.SpeciesData speciesData() {
    return SPECIES_DATA;
  }
  
  final int fieldCount() {
    return 4;
  }
  
  static BoundMethodHandle make(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4) {
    return new BoundMethodHandle$Species_L4(paramMethodType, paramLambdaForm, paramObject1, paramObject2, paramObject3, paramObject4);
  }
  
  final BoundMethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm) {
    return new BoundMethodHandle$Species_L4(paramMethodType, paramLambdaForm, this.argL0, this.argL1, this.argL2, this.argL3);
  }
  
  final BoundMethodHandle copyWithExtendL(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject) throws Throwable {
    return SPECIES_DATA.extendWith((byte)0).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argL1, this.argL2, this.argL3, paramObject);
  }
  
  final BoundMethodHandle copyWithExtendI(MethodType paramMethodType, LambdaForm paramLambdaForm, int paramInt) throws Throwable {
    return SPECIES_DATA.extendWith((byte)1).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argL1, this.argL2, this.argL3, paramInt);
  }
  
  final BoundMethodHandle copyWithExtendJ(MethodType paramMethodType, LambdaForm paramLambdaForm, long paramLong) throws Throwable {
    return SPECIES_DATA.extendWith((byte)2).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argL1, this.argL2, this.argL3, paramLong);
  }
  
  final BoundMethodHandle copyWithExtendF(MethodType paramMethodType, LambdaForm paramLambdaForm, float paramFloat) throws Throwable {
    return SPECIES_DATA.extendWith((byte)3).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argL1, this.argL2, this.argL3, paramFloat);
  }
  
  final BoundMethodHandle copyWithExtendD(MethodType paramMethodType, LambdaForm paramLambdaForm, double paramDouble) throws Throwable {
    return SPECIES_DATA.extendWith((byte)4).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argL1, this.argL2, this.argL3, paramDouble);
  }
}
