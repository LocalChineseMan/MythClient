package java.lang.invoke;

import java.lang.invoke.BoundMethodHandle;
import java.lang.invoke.BoundMethodHandle$Species_LI;
import java.lang.invoke.LambdaForm;
import java.lang.invoke.MethodType;

final class BoundMethodHandle$Species_LI extends BoundMethodHandle {
  static BoundMethodHandle.SpeciesData SPECIES_DATA = BoundMethodHandle.SpeciesData.getForClass("LI", (Class)BoundMethodHandle$Species_LI.class);
  
  final Object argL0;
  
  final int argI1;
  
  private BoundMethodHandle$Species_LI(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject, int paramInt) {
    super(paramMethodType, paramLambdaForm);
    this.argL0 = paramObject;
    this.argI1 = paramInt;
  }
  
  final BoundMethodHandle.SpeciesData speciesData() {
    return SPECIES_DATA;
  }
  
  final int fieldCount() {
    return 2;
  }
  
  static BoundMethodHandle make(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject, int paramInt) {
    return new BoundMethodHandle$Species_LI(paramMethodType, paramLambdaForm, paramObject, paramInt);
  }
  
  final BoundMethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm) {
    return new BoundMethodHandle$Species_LI(paramMethodType, paramLambdaForm, this.argL0, this.argI1);
  }
  
  final BoundMethodHandle copyWithExtendL(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject) throws Throwable {
    return SPECIES_DATA.extendWith((byte)0).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argI1, paramObject);
  }
  
  final BoundMethodHandle copyWithExtendI(MethodType paramMethodType, LambdaForm paramLambdaForm, int paramInt) throws Throwable {
    return SPECIES_DATA.extendWith((byte)1).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argI1, paramInt);
  }
  
  final BoundMethodHandle copyWithExtendJ(MethodType paramMethodType, LambdaForm paramLambdaForm, long paramLong) throws Throwable {
    return SPECIES_DATA.extendWith((byte)2).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argI1, paramLong);
  }
  
  final BoundMethodHandle copyWithExtendF(MethodType paramMethodType, LambdaForm paramLambdaForm, float paramFloat) throws Throwable {
    return SPECIES_DATA.extendWith((byte)3).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argI1, paramFloat);
  }
  
  final BoundMethodHandle copyWithExtendD(MethodType paramMethodType, LambdaForm paramLambdaForm, double paramDouble) throws Throwable {
    return SPECIES_DATA.extendWith((byte)4).constructor().invokeBasic(paramMethodType, paramLambdaForm, this.argL0, this.argI1, paramDouble);
  }
}
