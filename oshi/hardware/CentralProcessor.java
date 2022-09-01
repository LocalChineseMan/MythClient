package oshi.hardware;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface CentralProcessor {
  ProcessorIdentifier getProcessorIdentifier();
  
  long getMaxFreq();
  
  long[] getCurrentFreq();
  
  List<LogicalProcessor> getLogicalProcessors();
  
  List<PhysicalProcessor> getPhysicalProcessors();
  
  double getSystemCpuLoadBetweenTicks(long[] paramArrayOflong);
  
  long[] getSystemCpuLoadTicks();
  
  double[] getSystemLoadAverage(int paramInt);
  
  double[] getProcessorCpuLoadBetweenTicks(long[][] paramArrayOflong);
  
  long[][] getProcessorCpuLoadTicks();
  
  int getLogicalProcessorCount();
  
  int getPhysicalProcessorCount();
  
  int getPhysicalPackageCount();
  
  long getContextSwitches();
  
  long getInterrupts();
  
  public static final class CentralProcessor {}
  
  public static class CentralProcessor {}
  
  public static class CentralProcessor {}
  
  public enum CentralProcessor {
  
  }
}
