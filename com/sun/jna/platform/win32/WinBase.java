package com.sun.jna.platform.win32;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.win32.W32APITypeMapper;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public interface WinBase extends WinDef, BaseTSD {
  public static final WinNT.HANDLE INVALID_HANDLE_VALUE = new WinNT.HANDLE(
      Pointer.createConstant((Native.POINTER_SIZE == 8) ? -1L : 4294967295L));
  
  public static final int WAIT_FAILED = -1;
  
  public static final int WAIT_OBJECT_0 = 0;
  
  public static final int WAIT_ABANDONED = 128;
  
  public static final int WAIT_ABANDONED_0 = 128;
  
  public static final int MAX_COMPUTERNAME_LENGTH = Platform.isMac() ? 15 : 31;
  
  public static final int LOGON32_LOGON_INTERACTIVE = 2;
  
  public static final int LOGON32_LOGON_NETWORK = 3;
  
  public static final int LOGON32_LOGON_BATCH = 4;
  
  public static final int LOGON32_LOGON_SERVICE = 5;
  
  public static final int LOGON32_LOGON_UNLOCK = 7;
  
  public static final int LOGON32_LOGON_NETWORK_CLEARTEXT = 8;
  
  public static final int LOGON32_LOGON_NEW_CREDENTIALS = 9;
  
  public static final int LOGON32_PROVIDER_DEFAULT = 0;
  
  public static final int LOGON32_PROVIDER_WINNT35 = 1;
  
  public static final int LOGON32_PROVIDER_WINNT40 = 2;
  
  public static final int LOGON32_PROVIDER_WINNT50 = 3;
  
  public static final int HANDLE_FLAG_INHERIT = 1;
  
  public static final int HANDLE_FLAG_PROTECT_FROM_CLOSE = 2;
  
  public static final int STARTF_USESHOWWINDOW = 1;
  
  public static final int STARTF_USESIZE = 2;
  
  public static final int STARTF_USEPOSITION = 4;
  
  public static final int STARTF_USECOUNTCHARS = 8;
  
  public static final int STARTF_USEFILLATTRIBUTE = 16;
  
  public static final int STARTF_RUNFULLSCREEN = 32;
  
  public static final int STARTF_FORCEONFEEDBACK = 64;
  
  public static final int STARTF_FORCEOFFFEEDBACK = 128;
  
  public static final int STARTF_USESTDHANDLES = 256;
  
  public static final int DEBUG_PROCESS = 1;
  
  public static final int DEBUG_ONLY_THIS_PROCESS = 2;
  
  public static final int CREATE_SUSPENDED = 4;
  
  public static final int DETACHED_PROCESS = 8;
  
  public static final int CREATE_NEW_CONSOLE = 16;
  
  public static final int CREATE_NEW_PROCESS_GROUP = 512;
  
  public static final int CREATE_UNICODE_ENVIRONMENT = 1024;
  
  public static final int CREATE_SEPARATE_WOW_VDM = 2048;
  
  public static final int CREATE_SHARED_WOW_VDM = 4096;
  
  public static final int CREATE_FORCEDOS = 8192;
  
  public static final int INHERIT_PARENT_AFFINITY = 65536;
  
  public static final int CREATE_PROTECTED_PROCESS = 262144;
  
  public static final int EXTENDED_STARTUPINFO_PRESENT = 524288;
  
  public static final int CREATE_BREAKAWAY_FROM_JOB = 16777216;
  
  public static final int CREATE_PRESERVE_CODE_AUTHZ_LEVEL = 33554432;
  
  public static final int CREATE_DEFAULT_ERROR_MODE = 67108864;
  
  public static final int CREATE_NO_WINDOW = 134217728;
  
  public static final int FILE_ENCRYPTABLE = 0;
  
  public static final int FILE_IS_ENCRYPTED = 1;
  
  public static final int FILE_SYSTEM_ATTR = 2;
  
  public static final int FILE_ROOT_DIR = 3;
  
  public static final int FILE_SYSTEM_DIR = 4;
  
  public static final int FILE_UNKNOWN = 5;
  
  public static final int FILE_SYSTEM_NOT_SUPPORT = 6;
  
  public static final int FILE_USER_DISALLOWED = 7;
  
  public static final int FILE_READ_ONLY = 8;
  
  public static final int FILE_DIR_DISALOWED = 9;
  
  public static final int CREATE_FOR_IMPORT = 1;
  
  public static final int CREATE_FOR_DIR = 2;
  
  public static final int OVERWRITE_HIDDEN = 4;
  
  public static final int INVALID_FILE_SIZE = -1;
  
  public static final int INVALID_SET_FILE_POINTER = -1;
  
  public static final int INVALID_FILE_ATTRIBUTES = -1;
  
  public static final int STILL_ACTIVE = 259;
  
  public static final int FileBasicInfo = 0;
  
  public static final int FileStandardInfo = 1;
  
  public static final int FileNameInfo = 2;
  
  public static final int FileRenameInfo = 3;
  
  public static final int FileDispositionInfo = 4;
  
  public static final int FileAllocationInfo = 5;
  
  public static final int FileEndOfFileInfo = 6;
  
  public static final int FileStreamInfo = 7;
  
  public static final int FileCompressionInfo = 8;
  
  public static final int FileAttributeTagInfo = 9;
  
  public static final int FileIdBothDirectoryInfo = 10;
  
  public static final int FileIdBothDirectoryRestartInfo = 11;
  
  public static final int FileIoPriorityHintInfo = 12;
  
  public static final int FileRemoteProtocolInfo = 13;
  
  public static final int FileFullDirectoryInfo = 14;
  
  public static final int FileFullDirectoryRestartInfo = 15;
  
  public static final int FileStorageInfo = 16;
  
  public static final int FileAlignmentInfo = 17;
  
  public static final int FileIdInfo = 18;
  
  public static final int FileIdExtdDirectoryInfo = 19;
  
  public static final int FileIdExtdDirectoryRestartInfo = 20;
  
  public static final int FILE_MAP_COPY = 1;
  
  public static final int FILE_MAP_WRITE = 2;
  
  public static final int FILE_MAP_READ = 4;
  
  public static final int FILE_MAP_ALL_ACCESS = 983071;
  
  public static final int FILE_MAP_EXECUTE = 32;
  
  public static final int FindExInfoStandard = 0;
  
  public static final int FindExInfoBasic = 1;
  
  public static final int FindExInfoMaxInfoLevel = 2;
  
  public static final int FindExSearchNameMatch = 0;
  
  public static final int FindExSearchLimitToDirectories = 1;
  
  public static final int FindExSearchLimitToDevices = 2;
  
  public static final int LMEM_FIXED = 0;
  
  public static final int LMEM_MOVEABLE = 2;
  
  public static final int LMEM_NOCOMPACT = 16;
  
  public static final int LMEM_NODISCARD = 32;
  
  public static final int LMEM_ZEROINIT = 64;
  
  public static final int LMEM_MODIFY = 128;
  
  public static final int LMEM_DISCARDABLE = 3840;
  
  public static final int LMEM_VALID_FLAGS = 3954;
  
  public static final int LMEM_INVALID_HANDLE = 32768;
  
  public static final int LHND = 66;
  
  public static final int LPTR = 64;
  
  public static final int LMEM_DISCARDED = 16384;
  
  public static final int LMEM_LOCKCOUNT = 255;
  
  public static final int FORMAT_MESSAGE_ALLOCATE_BUFFER = 256;
  
  public static final int FORMAT_MESSAGE_IGNORE_INSERTS = 512;
  
  public static final int FORMAT_MESSAGE_FROM_STRING = 1024;
  
  public static final int FORMAT_MESSAGE_FROM_HMODULE = 2048;
  
  public static final int FORMAT_MESSAGE_FROM_SYSTEM = 4096;
  
  public static final int FORMAT_MESSAGE_ARGUMENT_ARRAY = 8192;
  
  public static final int DRIVE_UNKNOWN = 0;
  
  public static final int DRIVE_NO_ROOT_DIR = 1;
  
  public static final int DRIVE_REMOVABLE = 2;
  
  public static final int DRIVE_FIXED = 3;
  
  public static final int DRIVE_REMOTE = 4;
  
  public static final int DRIVE_CDROM = 5;
  
  public static final int DRIVE_RAMDISK = 6;
  
  public static final int INFINITE = -1;
  
  public static final int MOVEFILE_COPY_ALLOWED = 2;
  
  public static final int MOVEFILE_CREATE_HARDLINK = 16;
  
  public static final int MOVEFILE_DELAY_UNTIL_REBOOT = 4;
  
  public static final int MOVEFILE_FAIL_IF_NOT_TRACKABLE = 32;
  
  public static final int MOVEFILE_REPLACE_EXISTING = 1;
  
  public static final int MOVEFILE_WRITE_THROUGH = 8;
  
  public static final int PIPE_CLIENT_END = 0;
  
  public static final int PIPE_SERVER_END = 1;
  
  public static final int PIPE_ACCESS_DUPLEX = 3;
  
  public static final int PIPE_ACCESS_INBOUND = 1;
  
  public static final int PIPE_ACCESS_OUTBOUND = 2;
  
  public static final int PIPE_TYPE_BYTE = 0;
  
  public static final int PIPE_TYPE_MESSAGE = 4;
  
  public static final int PIPE_READMODE_BYTE = 0;
  
  public static final int PIPE_READMODE_MESSAGE = 2;
  
  public static final int PIPE_WAIT = 0;
  
  public static final int PIPE_NOWAIT = 1;
  
  public static final int PIPE_ACCEPT_REMOTE_CLIENTS = 0;
  
  public static final int PIPE_REJECT_REMOTE_CLIENTS = 8;
  
  public static final int PIPE_UNLIMITED_INSTANCES = 255;
  
  public static final int NMPWAIT_USE_DEFAULT_WAIT = 0;
  
  public static final int NMPWAIT_NOWAIT = 1;
  
  public static final int NMPWAIT_WAIT_FOREVER = -1;
  
  public static final int NOPARITY = 0;
  
  public static final int ODDPARITY = 1;
  
  public static final int EVENPARITY = 2;
  
  public static final int MARKPARITY = 3;
  
  public static final int SPACEPARITY = 4;
  
  public static final int ONESTOPBIT = 0;
  
  public static final int ONE5STOPBITS = 1;
  
  public static final int TWOSTOPBITS = 2;
  
  public static final int CBR_110 = 110;
  
  public static final int CBR_300 = 300;
  
  public static final int CBR_600 = 600;
  
  public static final int CBR_1200 = 1200;
  
  public static final int CBR_2400 = 2400;
  
  public static final int CBR_4800 = 4800;
  
  public static final int CBR_9600 = 9600;
  
  public static final int CBR_14400 = 14400;
  
  public static final int CBR_19200 = 19200;
  
  public static final int CBR_38400 = 38400;
  
  public static final int CBR_56000 = 56000;
  
  public static final int CBR_128000 = 128000;
  
  public static final int CBR_256000 = 256000;
  
  public static final int DTR_CONTROL_DISABLE = 0;
  
  public static final int DTR_CONTROL_ENABLE = 1;
  
  public static final int DTR_CONTROL_HANDSHAKE = 2;
  
  public static final int RTS_CONTROL_DISABLE = 0;
  
  public static final int RTS_CONTROL_ENABLE = 1;
  
  public static final int RTS_CONTROL_HANDSHAKE = 2;
  
  public static final int RTS_CONTROL_TOGGLE = 3;
  
  public static final int ES_AWAYMODE_REQUIRED = 64;
  
  public static final int ES_CONTINUOUS = -2147483648;
  
  public static final int ES_DISPLAY_REQUIRED = 2;
  
  public static final int ES_SYSTEM_REQUIRED = 1;
  
  public static final int ES_USER_PRESENT = 4;
  
  public static final int MUTEX_MODIFY_STATE = 1;
  
  public static final int MUTEX_ALL_ACCESS = 2031617;
  
  public static class WinBase {}
  
  public static class WinBase {}
  
  public static class WinBase {}
  
  public static class WinBase {}
  
  public static class WinBase {}
  
  public static class WinBase {}
  
  public static class WinBase {}
  
  @FieldOrder({"dwLowDateTime", "dwHighDateTime"})
  public static class FILETIME extends Structure {
    public int dwLowDateTime;
    
    public int dwHighDateTime;
    
    private static final long EPOCH_DIFF = 11644473600000L;
    
    public FILETIME(Date date) {
      long rawValue = dateToFileTime(date);
      this.dwHighDateTime = (int)(rawValue >> 32L & 0xFFFFFFFFL);
      this.dwLowDateTime = (int)(rawValue & 0xFFFFFFFFL);
    }
    
    public FILETIME(WinNT.LARGE_INTEGER ft) {
      this.dwHighDateTime = ft.getHigh().intValue();
      this.dwLowDateTime = ft.getLow().intValue();
    }
    
    public FILETIME() {}
    
    public FILETIME(Pointer memory) {
      super(memory);
      read();
    }
    
    public static Date filetimeToDate(int high, int low) {
      long filetime = high << 32L | low & 0xFFFFFFFFL;
      long ms_since_16010101 = filetime / 10000L;
      long ms_since_19700101 = ms_since_16010101 - 11644473600000L;
      return new Date(ms_since_19700101);
    }
    
    public static long dateToFileTime(Date date) {
      long ms_since_19700101 = date.getTime();
      long ms_since_16010101 = ms_since_19700101 + 11644473600000L;
      return ms_since_16010101 * 1000L * 10L;
    }
    
    public Date toDate() {
      return filetimeToDate(this.dwHighDateTime, this.dwLowDateTime);
    }
    
    public long toTime() {
      return toDate().getTime();
    }
    
    public WinDef.DWORDLONG toDWordLong() {
      return new WinDef.DWORDLONG(this.dwHighDateTime << 32L | this.dwLowDateTime & 0xFFFFFFFFL);
    }
    
    public String toString() {
      return super.toString() + ": " + toDate().toString();
    }
    
    public static class FILETIME {}
  }
  
  @FieldOrder({"wYear", "wMonth", "wDayOfWeek", "wDay", "wHour", "wMinute", "wSecond", "wMilliseconds"})
  public static class SYSTEMTIME extends Structure {
    public short wYear;
    
    public short wMonth;
    
    public short wDayOfWeek;
    
    public short wDay;
    
    public short wHour;
    
    public short wMinute;
    
    public short wSecond;
    
    public short wMilliseconds;
    
    public SYSTEMTIME() {}
    
    public SYSTEMTIME(Date date) {
      this(date.getTime());
    }
    
    public SYSTEMTIME(long timestamp) {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(timestamp);
      fromCalendar(cal);
    }
    
    public SYSTEMTIME(Calendar cal) {
      fromCalendar(cal);
    }
    
    public void fromCalendar(Calendar cal) {
      this.wYear = (short)cal.get(1);
      this.wMonth = (short)(1 + cal.get(2) - 0);
      this.wDay = (short)cal.get(5);
      this.wHour = (short)cal.get(11);
      this.wMinute = (short)cal.get(12);
      this.wSecond = (short)cal.get(13);
      this.wMilliseconds = (short)cal.get(14);
      this.wDayOfWeek = (short)(cal.get(7) - 1);
    }
    
    public Calendar toCalendar() {
      Calendar cal = Calendar.getInstance();
      cal.set(1, this.wYear);
      cal.set(2, 0 + this.wMonth - 1);
      cal.set(5, this.wDay);
      cal.set(11, this.wHour);
      cal.set(12, this.wMinute);
      cal.set(13, this.wSecond);
      cal.set(14, this.wMilliseconds);
      return cal;
    }
    
    public String toString() {
      if (this.wYear == 0 && this.wMonth == 0 && this.wDay == 0 && this.wHour == 0 && this.wMinute == 0 && this.wSecond == 0 && this.wMilliseconds == 0)
        return super.toString(); 
      DateFormat dtf = DateFormat.getDateTimeInstance();
      Calendar cal = toCalendar();
      return dtf.format(cal.getTime());
    }
  }
  
  @FieldOrder({"Bias", "StandardName", "StandardDate", "StandardBias", "DaylightName", "DaylightDate", "DaylightBias"})
  public static class TIME_ZONE_INFORMATION extends Structure {
    public WinDef.LONG Bias;
    
    public String StandardName;
    
    public WinBase.SYSTEMTIME StandardDate;
    
    public WinDef.LONG StandardBias;
    
    public String DaylightName;
    
    public WinBase.SYSTEMTIME DaylightDate;
    
    public WinDef.LONG DaylightBias;
    
    public TIME_ZONE_INFORMATION() {
      super(W32APITypeMapper.DEFAULT);
    }
  }
  
  @FieldOrder({"Internal", "InternalHigh", "Offset", "OffsetHigh", "hEvent"})
  public static class OVERLAPPED extends Structure {
    public BaseTSD.ULONG_PTR Internal;
    
    public BaseTSD.ULONG_PTR InternalHigh;
    
    public int Offset;
    
    public int OffsetHigh;
    
    public WinNT.HANDLE hEvent;
  }
  
  @FieldOrder({"processorArchitecture", "dwPageSize", "lpMinimumApplicationAddress", "lpMaximumApplicationAddress", "dwActiveProcessorMask", "dwNumberOfProcessors", "dwProcessorType", "dwAllocationGranularity", "wProcessorLevel", "wProcessorRevision"})
  public static class SYSTEM_INFO extends Structure {
    public UNION processorArchitecture;
    
    public WinDef.DWORD dwPageSize;
    
    public Pointer lpMinimumApplicationAddress;
    
    public Pointer lpMaximumApplicationAddress;
    
    public BaseTSD.DWORD_PTR dwActiveProcessorMask;
    
    public WinDef.DWORD dwNumberOfProcessors;
    
    public WinDef.DWORD dwProcessorType;
    
    public WinDef.DWORD dwAllocationGranularity;
    
    public WinDef.WORD wProcessorLevel;
    
    public WinDef.WORD wProcessorRevision;
    
    public static class SYSTEM_INFO {}
    
    public static class SYSTEM_INFO {}
  }
  
  @FieldOrder({"dwLength", "dwMemoryLoad", "ullTotalPhys", "ullAvailPhys", "ullTotalPageFile", "ullAvailPageFile", "ullTotalVirtual", "ullAvailVirtual", "ullAvailExtendedVirtual"})
  public static class MEMORYSTATUSEX extends Structure {
    public WinDef.DWORD dwLength = new WinDef.DWORD(size());
    
    public WinDef.DWORD dwMemoryLoad;
    
    public WinDef.DWORDLONG ullTotalPhys;
    
    public WinDef.DWORDLONG ullAvailPhys;
    
    public WinDef.DWORDLONG ullTotalPageFile;
    
    public WinDef.DWORDLONG ullAvailPageFile;
    
    public WinDef.DWORDLONG ullTotalVirtual;
    
    public WinDef.DWORDLONG ullAvailVirtual;
    
    public WinDef.DWORDLONG ullAvailExtendedVirtual;
  }
  
  @FieldOrder({"dwLength", "lpSecurityDescriptor", "bInheritHandle"})
  public static class SECURITY_ATTRIBUTES extends Structure {
    public WinDef.DWORD dwLength = new WinDef.DWORD(size());
    
    public Pointer lpSecurityDescriptor;
    
    public boolean bInheritHandle;
  }
  
  @FieldOrder({"cb", "lpReserved", "lpDesktop", "lpTitle", "dwX", "dwY", "dwXSize", "dwYSize", "dwXCountChars", "dwYCountChars", "dwFillAttribute", "dwFlags", "wShowWindow", "cbReserved2", "lpReserved2", "hStdInput", "hStdOutput", "hStdError"})
  public static class STARTUPINFO extends Structure {
    public WinDef.DWORD cb;
    
    public String lpReserved;
    
    public String lpDesktop;
    
    public String lpTitle;
    
    public WinDef.DWORD dwX;
    
    public WinDef.DWORD dwY;
    
    public WinDef.DWORD dwXSize;
    
    public WinDef.DWORD dwYSize;
    
    public WinDef.DWORD dwXCountChars;
    
    public WinDef.DWORD dwYCountChars;
    
    public WinDef.DWORD dwFillAttribute;
    
    public int dwFlags;
    
    public WinDef.WORD wShowWindow;
    
    public WinDef.WORD cbReserved2;
    
    public ByteByReference lpReserved2;
    
    public WinNT.HANDLE hStdInput;
    
    public WinNT.HANDLE hStdOutput;
    
    public WinNT.HANDLE hStdError;
    
    public STARTUPINFO() {
      super(W32APITypeMapper.DEFAULT);
      this.cb = new WinDef.DWORD(size());
    }
  }
  
  @FieldOrder({"hProcess", "hThread", "dwProcessId", "dwThreadId"})
  public static class PROCESS_INFORMATION extends Structure {
    public WinNT.HANDLE hProcess;
    
    public WinNT.HANDLE hThread;
    
    public WinDef.DWORD dwProcessId;
    
    public WinDef.DWORD dwThreadId;
    
    public PROCESS_INFORMATION() {}
    
    public PROCESS_INFORMATION(Pointer memory) {
      super(memory);
      read();
    }
    
    public static class PROCESS_INFORMATION {}
  }
  
  public static interface WinBase {}
  
  @FieldOrder({"foreignLocation"})
  public static class FOREIGN_THREAD_START_ROUTINE extends Structure {
    public WinDef.LPVOID foreignLocation;
  }
  
  public static interface WinBase {}
  
  public static interface WinBase {}
  
  public static interface WinBase {}
  
  @FieldOrder({"ReadIntervalTimeout", "ReadTotalTimeoutMultiplier", "ReadTotalTimeoutConstant", "WriteTotalTimeoutMultiplier", "WriteTotalTimeoutConstant"})
  public static class COMMTIMEOUTS extends Structure {
    public WinDef.DWORD ReadIntervalTimeout;
    
    public WinDef.DWORD ReadTotalTimeoutMultiplier;
    
    public WinDef.DWORD ReadTotalTimeoutConstant;
    
    public WinDef.DWORD WriteTotalTimeoutMultiplier;
    
    public WinDef.DWORD WriteTotalTimeoutConstant;
  }
  
  @FieldOrder({"DCBlength", "BaudRate", "controllBits", "wReserved", "XonLim", "XoffLim", "ByteSize", "Parity", "StopBits", "XonChar", "XoffChar", "ErrorChar", "EofChar", "EvtChar", "wReserved1"})
  public static class DCB extends Structure {
    public WinDef.DWORD DCBlength = new WinDef.DWORD(size());
    
    public WinDef.DWORD BaudRate;
    
    public DCBControllBits controllBits;
    
    public WinDef.WORD wReserved;
    
    public WinDef.WORD XonLim;
    
    public WinDef.WORD XoffLim;
    
    public WinDef.BYTE ByteSize;
    
    public WinDef.BYTE Parity;
    
    public WinDef.BYTE StopBits;
    
    public char XonChar;
    
    public char XoffChar;
    
    public char ErrorChar;
    
    public char EofChar;
    
    public char EvtChar;
    
    public WinDef.WORD wReserved1;
    
    public static class DCB {}
  }
  
  public static interface EnumResTypeProc extends Callback {
    boolean invoke(WinDef.HMODULE param1HMODULE, Pointer param1Pointer1, Pointer param1Pointer2);
  }
  
  public static interface EnumResNameProc extends Callback {
    boolean invoke(WinDef.HMODULE param1HMODULE, Pointer param1Pointer1, Pointer param1Pointer2, Pointer param1Pointer3);
  }
}
