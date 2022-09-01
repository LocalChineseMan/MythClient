package oshi.software.os;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.Who;
import oshi.driver.unix.Xwininfo;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public interface OperatingSystem {
  String getFamily();
  
  String getManufacturer();
  
  OSVersionInfo getVersionInfo();
  
  FileSystem getFileSystem();
  
  InternetProtocolStats getInternetProtocolStats();
  
  default List<OSProcess> getProcesses() {
    return getProcesses(null, null, 0);
  }
  
  List<OSProcess> getProcesses(Predicate<OSProcess> paramPredicate, Comparator<OSProcess> paramComparator, int paramInt);
  
  default List<OSProcess> getProcesses(Collection<Integer> pids) {
    return (List<OSProcess>)pids.stream().map(this::getProcess).filter(Objects::nonNull).filter(ProcessFiltering.VALID_PROCESS)
      .collect(Collectors.toList());
  }
  
  OSProcess getProcess(int paramInt);
  
  List<OSProcess> getChildProcesses(int paramInt1, Predicate<OSProcess> paramPredicate, Comparator<OSProcess> paramComparator, int paramInt2);
  
  List<OSProcess> getDescendantProcesses(int paramInt1, Predicate<OSProcess> paramPredicate, Comparator<OSProcess> paramComparator, int paramInt2);
  
  int getProcessId();
  
  int getProcessCount();
  
  int getThreadCount();
  
  int getBitness();
  
  long getSystemUptime();
  
  long getSystemBootTime();
  
  default boolean isElevated() {
    return (0 == ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("id -u"), -1));
  }
  
  NetworkParams getNetworkParams();
  
  default List<OSService> getServices() {
    return new ArrayList<>();
  }
  
  default List<OSSession> getSessions() {
    return Who.queryWho();
  }
  
  default List<OSDesktopWindow> getDesktopWindows(boolean visibleOnly) {
    return Xwininfo.queryXWindows(visibleOnly);
  }
  
  public static final class OperatingSystem {}
  
  public static class OperatingSystem {}
  
  public static final class OperatingSystem {}
}
