package oshi.hardware.platform.windows;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.Display;
import oshi.hardware.GlobalMemory;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HWDiskStore;
import oshi.hardware.LogicalVolumeGroup;
import oshi.hardware.NetworkIF;
import oshi.hardware.PowerSource;
import oshi.hardware.Sensors;
import oshi.hardware.SoundCard;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractHardwareAbstractionLayer;

@ThreadSafe
public final class WindowsHardwareAbstractionLayer extends AbstractHardwareAbstractionLayer {
  public ComputerSystem createComputerSystem() {
    return (ComputerSystem)new WindowsComputerSystem();
  }
  
  public GlobalMemory createMemory() {
    return (GlobalMemory)new WindowsGlobalMemory();
  }
  
  public CentralProcessor createProcessor() {
    return (CentralProcessor)new WindowsCentralProcessor();
  }
  
  public Sensors createSensors() {
    return (Sensors)new WindowsSensors();
  }
  
  public List<PowerSource> getPowerSources() {
    return WindowsPowerSource.getPowerSources();
  }
  
  public List<HWDiskStore> getDiskStores() {
    return WindowsHWDiskStore.getDisks();
  }
  
  public List<LogicalVolumeGroup> getLogicalVolumeGroups() {
    return WindowsLogicalVolumeGroup.getLogicalVolumeGroups();
  }
  
  public List<Display> getDisplays() {
    return WindowsDisplay.getDisplays();
  }
  
  public List<NetworkIF> getNetworkIFs(boolean includeLocalInterfaces) {
    return WindowsNetworkIF.getNetworks(includeLocalInterfaces);
  }
  
  public List<UsbDevice> getUsbDevices(boolean tree) {
    return WindowsUsbDevice.getUsbDevices(tree);
  }
  
  public List<SoundCard> getSoundCards() {
    return WindowsSoundCard.getSoundCards();
  }
  
  public List<GraphicsCard> getGraphicsCards() {
    return WindowsGraphicsCard.getGraphicsCards();
  }
}
