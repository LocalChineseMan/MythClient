package com.sun.jna.platform.win32;

import com.sun.jna.FromNativeContext;
import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface WinNT extends WinError, WinDef, WinBase, BaseTSD {
  public static final int MINCHAR = 128;
  
  public static final int MAXCHAR = 127;
  
  public static final int MINSHORT = 32768;
  
  public static final int MAXSHORT = 32767;
  
  public static final int MINLONG = -2147483648;
  
  public static final int MAXLONG = 2147483647;
  
  public static final int MAXBYTE = 255;
  
  public static final int MAXWORD = 65535;
  
  public static final int MAXDWORD = -1;
  
  public static final int DELETE = 65536;
  
  public static final int READ_CONTROL = 131072;
  
  public static final int WRITE_DAC = 262144;
  
  public static final int WRITE_OWNER = 524288;
  
  public static final int SYNCHRONIZE = 1048576;
  
  public static final int STANDARD_RIGHTS_REQUIRED = 983040;
  
  public static final int STANDARD_RIGHTS_READ = 131072;
  
  public static final int STANDARD_RIGHTS_WRITE = 131072;
  
  public static final int STANDARD_RIGHTS_EXECUTE = 131072;
  
  public static final int STANDARD_RIGHTS_ALL = 2031616;
  
  public static final int SPECIFIC_RIGHTS_ALL = 65535;
  
  public static final int MUTANT_QUERY_STATE = 1;
  
  public static final int MUTANT_ALL_ACCESS = 2031617;
  
  public static final int TOKEN_ASSIGN_PRIMARY = 1;
  
  public static final int TOKEN_DUPLICATE = 2;
  
  public static final int TOKEN_IMPERSONATE = 4;
  
  public static final int TOKEN_QUERY = 8;
  
  public static final int TOKEN_QUERY_SOURCE = 16;
  
  public static final int TOKEN_ADJUST_PRIVILEGES = 32;
  
  public static final int TOKEN_ADJUST_GROUPS = 64;
  
  public static final int TOKEN_ADJUST_DEFAULT = 128;
  
  public static final int TOKEN_ADJUST_SESSIONID = 256;
  
  public static final int TOKEN_ALL_ACCESS_P = 983295;
  
  public static final int TOKEN_ALL_ACCESS = 983551;
  
  public static final int TOKEN_READ = 131080;
  
  public static final int TOKEN_WRITE = 131296;
  
  public static final int TOKEN_EXECUTE = 131072;
  
  public static final int THREAD_TERMINATE = 1;
  
  public static final int THREAD_SUSPEND_RESUME = 2;
  
  public static final int THREAD_GET_CONTEXT = 8;
  
  public static final int THREAD_SET_CONTEXT = 16;
  
  public static final int THREAD_QUERY_INFORMATION = 64;
  
  public static final int THREAD_SET_INFORMATION = 32;
  
  public static final int THREAD_SET_THREAD_TOKEN = 128;
  
  public static final int THREAD_IMPERSONATE = 256;
  
  public static final int THREAD_DIRECT_IMPERSONATION = 512;
  
  public static final int THREAD_SET_LIMITED_INFORMATION = 1024;
  
  public static final int THREAD_QUERY_LIMITED_INFORMATION = 2048;
  
  public static final int THREAD_ALL_ACCESS = 2032639;
  
  public static final int LTP_PC_SMT = 1;
  
  public static final int FILE_READ_DATA = 1;
  
  public static final int FILE_LIST_DIRECTORY = 1;
  
  public static final int FILE_WRITE_DATA = 2;
  
  public static final int FILE_ADD_FILE = 2;
  
  public static final int FILE_APPEND_DATA = 4;
  
  public static final int FILE_ADD_SUBDIRECTORY = 4;
  
  public static final int FILE_CREATE_PIPE_INSTANCE = 4;
  
  public static final int FILE_READ_EA = 8;
  
  public static final int FILE_WRITE_EA = 16;
  
  public static final int FILE_EXECUTE = 32;
  
  public static final int FILE_TRAVERSE = 32;
  
  public static final int FILE_DELETE_CHILD = 64;
  
  public static final int FILE_READ_ATTRIBUTES = 128;
  
  public static final int FILE_WRITE_ATTRIBUTES = 256;
  
  public static final int FILE_ALL_ACCESS = 2032127;
  
  public static final int FILE_GENERIC_READ = 1179785;
  
  public static final int FILE_GENERIC_WRITE = 1179926;
  
  public static final int FILE_GENERIC_EXECUTE = 1179808;
  
  public static final int CREATE_NEW = 1;
  
  public static final int CREATE_ALWAYS = 2;
  
  public static final int OPEN_EXISTING = 3;
  
  public static final int OPEN_ALWAYS = 4;
  
  public static final int TRUNCATE_EXISTING = 5;
  
  public static final int FILE_FLAG_WRITE_THROUGH = -2147483648;
  
  public static final int FILE_FLAG_OVERLAPPED = 1073741824;
  
  public static final int FILE_FLAG_NO_BUFFERING = 536870912;
  
  public static final int FILE_FLAG_RANDOM_ACCESS = 268435456;
  
  public static final int FILE_FLAG_SEQUENTIAL_SCAN = 134217728;
  
  public static final int FILE_FLAG_DELETE_ON_CLOSE = 67108864;
  
  public static final int FILE_FLAG_BACKUP_SEMANTICS = 33554432;
  
  public static final int FILE_FLAG_POSIX_SEMANTICS = 16777216;
  
  public static final int FILE_FLAG_OPEN_REPARSE_POINT = 2097152;
  
  public static final int FILE_FLAG_OPEN_NO_RECALL = 1048576;
  
  public static final int GENERIC_READ = -2147483648;
  
  public static final int GENERIC_WRITE = 1073741824;
  
  public static final int GENERIC_EXECUTE = 536870912;
  
  public static final int GENERIC_ALL = 268435456;
  
  public static final int ACCESS_SYSTEM_SECURITY = 16777216;
  
  public static final int PAGE_GUARD = 256;
  
  public static final int PAGE_NOACCESS = 1;
  
  public static final int PAGE_READONLY = 2;
  
  public static final int PAGE_READWRITE = 4;
  
  public static final int PAGE_WRITECOPY = 8;
  
  public static final int PAGE_EXECUTE = 16;
  
  public static final int PAGE_EXECUTE_READ = 32;
  
  public static final int PAGE_EXECUTE_READWRITE = 64;
  
  public static final int SECTION_QUERY = 1;
  
  public static final int SECTION_MAP_WRITE = 2;
  
  public static final int SECTION_MAP_READ = 4;
  
  public static final int SECTION_MAP_EXECUTE = 8;
  
  public static final int SECTION_EXTEND_SIZE = 16;
  
  public static final int SECTION_ALL_ACCESS = 983071;
  
  public static final int SECTION_MAP_EXECUTE_EXPLICIT = 32;
  
  public static final int FILE_SHARE_READ = 1;
  
  public static final int FILE_SHARE_WRITE = 2;
  
  public static final int FILE_SHARE_DELETE = 4;
  
  public static final int FILE_TYPE_CHAR = 2;
  
  public static final int FILE_TYPE_DISK = 1;
  
  public static final int FILE_TYPE_PIPE = 3;
  
  public static final int FILE_TYPE_REMOTE = 32768;
  
  public static final int FILE_TYPE_UNKNOWN = 0;
  
  public static final int FILE_ATTRIBUTE_READONLY = 1;
  
  public static final int FILE_ATTRIBUTE_HIDDEN = 2;
  
  public static final int FILE_ATTRIBUTE_SYSTEM = 4;
  
  public static final int FILE_ATTRIBUTE_DIRECTORY = 16;
  
  public static final int FILE_ATTRIBUTE_ARCHIVE = 32;
  
  public static final int FILE_ATTRIBUTE_DEVICE = 64;
  
  public static final int FILE_ATTRIBUTE_NORMAL = 128;
  
  public static final int FILE_ATTRIBUTE_TEMPORARY = 256;
  
  public static final int FILE_ATTRIBUTE_SPARSE_FILE = 512;
  
  public static final int FILE_ATTRIBUTE_REPARSE_POINT = 1024;
  
  public static final int FILE_ATTRIBUTE_COMPRESSED = 2048;
  
  public static final int FILE_ATTRIBUTE_OFFLINE = 4096;
  
  public static final int FILE_ATTRIBUTE_NOT_CONTENT_INDEXED = 8192;
  
  public static final int FILE_ATTRIBUTE_ENCRYPTED = 16384;
  
  public static final int FILE_ATTRIBUTE_VIRTUAL = 65536;
  
  public static final int FILE_NOTIFY_CHANGE_FILE_NAME = 1;
  
  public static final int FILE_NOTIFY_CHANGE_DIR_NAME = 2;
  
  public static final int FILE_NOTIFY_CHANGE_NAME = 3;
  
  public static final int FILE_NOTIFY_CHANGE_ATTRIBUTES = 4;
  
  public static final int FILE_NOTIFY_CHANGE_SIZE = 8;
  
  public static final int FILE_NOTIFY_CHANGE_LAST_WRITE = 16;
  
  public static final int FILE_NOTIFY_CHANGE_LAST_ACCESS = 32;
  
  public static final int FILE_NOTIFY_CHANGE_CREATION = 64;
  
  public static final int FILE_NOTIFY_CHANGE_SECURITY = 256;
  
  public static final int FILE_ACTION_ADDED = 1;
  
  public static final int FILE_ACTION_REMOVED = 2;
  
  public static final int FILE_ACTION_MODIFIED = 3;
  
  public static final int FILE_ACTION_RENAMED_OLD_NAME = 4;
  
  public static final int FILE_ACTION_RENAMED_NEW_NAME = 5;
  
  public static final int FILE_CASE_SENSITIVE_SEARCH = 1;
  
  public static final int FILE_CASE_PRESERVED_NAMES = 2;
  
  public static final int FILE_UNICODE_ON_DISK = 4;
  
  public static final int FILE_PERSISTENT_ACLS = 8;
  
  public static final int FILE_FILE_COMPRESSION = 16;
  
  public static final int FILE_VOLUME_QUOTAS = 32;
  
  public static final int FILE_SUPPORTS_SPARSE_FILES = 64;
  
  public static final int FILE_SUPPORTS_REPARSE_POINTS = 128;
  
  public static final int FILE_SUPPORTS_REMOTE_STORAGE = 256;
  
  public static final int FILE_VOLUME_IS_COMPRESSED = 32768;
  
  public static final int FILE_SUPPORTS_OBJECT_IDS = 65536;
  
  public static final int FILE_SUPPORTS_ENCRYPTION = 131072;
  
  public static final int FILE_NAMED_STREAMS = 262144;
  
  public static final int FILE_READ_ONLY_VOLUME = 524288;
  
  public static final int FILE_SEQUENTIAL_WRITE_ONCE = 1048576;
  
  public static final int FILE_SUPPORTS_TRANSACTIONS = 2097152;
  
  public static final int FILE_SUPPORTS_HARD_LINKS = 4194304;
  
  public static final int FILE_SUPPORTS_EXTENDED_ATTRIBUTES = 8388608;
  
  public static final int FILE_SUPPORTS_OPEN_BY_FILE_ID = 16777216;
  
  public static final int FILE_SUPPORTS_USN_JOURNAL = 33554432;
  
  public static final int IO_REPARSE_TAG_MOUNT_POINT = -1610612733;
  
  public static final int IO_REPARSE_TAG_HSM = -1073741820;
  
  public static final int IO_REPARSE_TAG_HSM2 = -2147483642;
  
  public static final int IO_REPARSE_TAG_SIS = -2147483641;
  
  public static final int IO_REPARSE_TAG_WIM = -2147483640;
  
  public static final int IO_REPARSE_TAG_CSV = -2147483639;
  
  public static final int IO_REPARSE_TAG_DFS = -2147483638;
  
  public static final int IO_REPARSE_TAG_SYMLINK = -1610612724;
  
  public static final int IO_REPARSE_TAG_DFSR = -2147483630;
  
  public static final int DDD_RAW_TARGET_PATH = 1;
  
  public static final int DDD_REMOVE_DEFINITION = 2;
  
  public static final int DDD_EXACT_MATCH_ON_REMOVE = 4;
  
  public static final int DDD_NO_BROADCAST_SYSTEM = 8;
  
  public static final int COMPRESSION_FORMAT_NONE = 0;
  
  public static final int COMPRESSION_FORMAT_DEFAULT = 1;
  
  public static final int COMPRESSION_FORMAT_LZNT1 = 2;
  
  public static final int COMPRESSION_FORMAT_XPRESS = 3;
  
  public static final int COMPRESSION_FORMAT_XPRESS_HUFF = 4;
  
  public static final int COMPRESSION_ENGINE_STANDARD = 0;
  
  public static final int COMPRESSION_ENGINE_MAXIMUM = 256;
  
  public static final int COMPRESSION_ENGINE_HIBER = 512;
  
  public static final int KEY_QUERY_VALUE = 1;
  
  public static final int KEY_SET_VALUE = 2;
  
  public static final int KEY_CREATE_SUB_KEY = 4;
  
  public static final int KEY_ENUMERATE_SUB_KEYS = 8;
  
  public static final int KEY_NOTIFY = 16;
  
  public static final int KEY_CREATE_LINK = 32;
  
  public static final int KEY_WOW64_32KEY = 512;
  
  public static final int KEY_WOW64_64KEY = 256;
  
  public static final int KEY_WOW64_RES = 768;
  
  public static final int KEY_READ = 131097;
  
  public static final int KEY_WRITE = 131078;
  
  public static final int KEY_EXECUTE = 131097;
  
  public static final int KEY_ALL_ACCESS = 983103;
  
  public static final int REG_OPTION_RESERVED = 0;
  
  public static final int REG_OPTION_NON_VOLATILE = 0;
  
  public static final int REG_OPTION_VOLATILE = 1;
  
  public static final int REG_OPTION_CREATE_LINK = 2;
  
  public static final int REG_OPTION_BACKUP_RESTORE = 4;
  
  public static final int REG_OPTION_OPEN_LINK = 8;
  
  public static final int REG_LEGAL_OPTION = 15;
  
  public static final int REG_CREATED_NEW_KEY = 1;
  
  public static final int REG_OPENED_EXISTING_KEY = 2;
  
  public static final int REG_STANDARD_FORMAT = 1;
  
  public static final int REG_LATEST_FORMAT = 2;
  
  public static final int REG_NO_COMPRESSION = 4;
  
  public static final int REG_WHOLE_HIVE_VOLATILE = 1;
  
  public static final int REG_REFRESH_HIVE = 2;
  
  public static final int REG_NO_LAZY_FLUSH = 4;
  
  public static final int REG_FORCE_RESTORE = 8;
  
  public static final int REG_APP_HIVE = 16;
  
  public static final int REG_PROCESS_PRIVATE = 32;
  
  public static final int REG_START_JOURNAL = 64;
  
  public static final int REG_HIVE_EXACT_FILE_GROWTH = 128;
  
  public static final int REG_HIVE_NO_RM = 256;
  
  public static final int REG_HIVE_SINGLE_LOG = 512;
  
  public static final int REG_FORCE_UNLOAD = 1;
  
  public static final int REG_NOTIFY_CHANGE_NAME = 1;
  
  public static final int REG_NOTIFY_CHANGE_ATTRIBUTES = 2;
  
  public static final int REG_NOTIFY_CHANGE_LAST_SET = 4;
  
  public static final int REG_NOTIFY_CHANGE_SECURITY = 8;
  
  public static final int REG_NOTIFY_THREAD_AGNOSTIC = 268435456;
  
  public static final int REG_LEGAL_CHANGE_FILTER = 268435471;
  
  public static final int REG_NONE = 0;
  
  public static final int REG_SZ = 1;
  
  public static final int REG_EXPAND_SZ = 2;
  
  public static final int REG_BINARY = 3;
  
  public static final int REG_DWORD = 4;
  
  public static final int REG_DWORD_LITTLE_ENDIAN = 4;
  
  public static final int REG_DWORD_BIG_ENDIAN = 5;
  
  public static final int REG_LINK = 6;
  
  public static final int REG_MULTI_SZ = 7;
  
  public static final int REG_RESOURCE_LIST = 8;
  
  public static final int REG_FULL_RESOURCE_DESCRIPTOR = 9;
  
  public static final int REG_RESOURCE_REQUIREMENTS_LIST = 10;
  
  public static final int REG_QWORD = 11;
  
  public static final int REG_QWORD_LITTLE_ENDIAN = 11;
  
  public static final int SID_REVISION = 1;
  
  public static final int SID_MAX_SUB_AUTHORITIES = 15;
  
  public static final int SID_RECOMMENDED_SUB_AUTHORITIES = 1;
  
  public static final int SECURITY_MAX_SID_SIZE = 68;
  
  public static final int VER_EQUAL = 1;
  
  public static final int VER_GREATER = 2;
  
  public static final int VER_GREATER_EQUAL = 3;
  
  public static final int VER_LESS = 4;
  
  public static final int VER_LESS_EQUAL = 5;
  
  public static final int VER_AND = 6;
  
  public static final int VER_OR = 7;
  
  public static final int VER_CONDITION_MASK = 7;
  
  public static final int VER_NUM_BITS_PER_CONDITION_MASK = 3;
  
  public static final int VER_MINORVERSION = 1;
  
  public static final int VER_MAJORVERSION = 2;
  
  public static final int VER_BUILDNUMBER = 4;
  
  public static final int VER_PLATFORMID = 8;
  
  public static final int VER_SERVICEPACKMINOR = 16;
  
  public static final int VER_SERVICEPACKMAJOR = 32;
  
  public static final int VER_SUITENAME = 64;
  
  public static final int VER_PRODUCT_TYPE = 128;
  
  public static final int VER_NT_WORKSTATION = 1;
  
  public static final int VER_NT_DOMAIN_CONTROLLER = 2;
  
  public static final int VER_NT_SERVER = 3;
  
  public static final int VER_PLATFORM_WIN32s = 0;
  
  public static final int VER_PLATFORM_WIN32_WINDOWS = 1;
  
  public static final int VER_PLATFORM_WIN32_NT = 2;
  
  public static final short WIN32_WINNT_NT4 = 1024;
  
  public static final short WIN32_WINNT_WIN2K = 1280;
  
  public static final short WIN32_WINNT_WINXP = 1281;
  
  public static final short WIN32_WINNT_WS03 = 1282;
  
  public static final short WIN32_WINNT_WIN6 = 1536;
  
  public static final short WIN32_WINNT_VISTA = 1536;
  
  public static final short WIN32_WINNT_WS08 = 1536;
  
  public static final short WIN32_WINNT_LONGHORN = 1536;
  
  public static final short WIN32_WINNT_WIN7 = 1537;
  
  public static final short WIN32_WINNT_WIN8 = 1538;
  
  public static final short WIN32_WINNT_WINBLUE = 1539;
  
  public static final short WIN32_WINNT_WINTHRESHOLD = 2560;
  
  public static final short WIN32_WINNT_WIN10 = 2560;
  
  public static final int EVENTLOG_SEQUENTIAL_READ = 1;
  
  public static final int EVENTLOG_SEEK_READ = 2;
  
  public static final int EVENTLOG_FORWARDS_READ = 4;
  
  public static final int EVENTLOG_BACKWARDS_READ = 8;
  
  public static final int EVENTLOG_SUCCESS = 0;
  
  public static final int EVENTLOG_ERROR_TYPE = 1;
  
  public static final int EVENTLOG_WARNING_TYPE = 2;
  
  public static final int EVENTLOG_INFORMATION_TYPE = 4;
  
  public static final int EVENTLOG_AUDIT_SUCCESS = 8;
  
  public static final int EVENTLOG_AUDIT_FAILURE = 16;
  
  public static final int SERVICE_KERNEL_DRIVER = 1;
  
  public static final int SERVICE_FILE_SYSTEM_DRIVER = 2;
  
  public static final int SERVICE_ADAPTER = 4;
  
  public static final int SERVICE_RECOGNIZER_DRIVER = 8;
  
  public static final int SERVICE_DRIVER = 11;
  
  public static final int SERVICE_WIN32_OWN_PROCESS = 16;
  
  public static final int SERVICE_WIN32_SHARE_PROCESS = 32;
  
  public static final int SERVICE_WIN32 = 48;
  
  public static final int SERVICE_INTERACTIVE_PROCESS = 256;
  
  public static final int SERVICE_TYPE_ALL = 319;
  
  public static final int SERVICE_BOOT_START = 0;
  
  public static final int SERVICE_SYSTEM_START = 1;
  
  public static final int SERVICE_AUTO_START = 2;
  
  public static final int SERVICE_DEMAND_START = 3;
  
  public static final int SERVICE_DISABLED = 4;
  
  public static final int SERVICE_ERROR_IGNORE = 0;
  
  public static final int SERVICE_ERROR_NORMAL = 1;
  
  public static final int SERVICE_ERROR_SEVERE = 2;
  
  public static final int SERVICE_ERROR_CRITICAL = 3;
  
  public static final int STATUS_PENDING = 259;
  
  public static final String SE_CREATE_TOKEN_NAME = "SeCreateTokenPrivilege";
  
  public static final String SE_ASSIGNPRIMARYTOKEN_NAME = "SeAssignPrimaryTokenPrivilege";
  
  public static final String SE_LOCK_MEMORY_NAME = "SeLockMemoryPrivilege";
  
  public static final String SE_INCREASE_QUOTA_NAME = "SeIncreaseQuotaPrivilege";
  
  public static final String SE_UNSOLICITED_INPUT_NAME = "SeUnsolicitedInputPrivilege";
  
  public static final String SE_MACHINE_ACCOUNT_NAME = "SeMachineAccountPrivilege";
  
  public static final String SE_TCB_NAME = "SeTcbPrivilege";
  
  public static final String SE_SECURITY_NAME = "SeSecurityPrivilege";
  
  public static final String SE_TAKE_OWNERSHIP_NAME = "SeTakeOwnershipPrivilege";
  
  public static final String SE_LOAD_DRIVER_NAME = "SeLoadDriverPrivilege";
  
  public static final String SE_SYSTEM_PROFILE_NAME = "SeSystemProfilePrivilege";
  
  public static final String SE_SYSTEMTIME_NAME = "SeSystemtimePrivilege";
  
  public static final String SE_PROF_SINGLE_PROCESS_NAME = "SeProfileSingleProcessPrivilege";
  
  public static final String SE_INC_BASE_PRIORITY_NAME = "SeIncreaseBasePriorityPrivilege";
  
  public static final String SE_CREATE_PAGEFILE_NAME = "SeCreatePagefilePrivilege";
  
  public static final String SE_CREATE_PERMANENT_NAME = "SeCreatePermanentPrivilege";
  
  public static final String SE_BACKUP_NAME = "SeBackupPrivilege";
  
  public static final String SE_RESTORE_NAME = "SeRestorePrivilege";
  
  public static final String SE_SHUTDOWN_NAME = "SeShutdownPrivilege";
  
  public static final String SE_DEBUG_NAME = "SeDebugPrivilege";
  
  public static final String SE_AUDIT_NAME = "SeAuditPrivilege";
  
  public static final String SE_SYSTEM_ENVIRONMENT_NAME = "SeSystemEnvironmentPrivilege";
  
  public static final String SE_CHANGE_NOTIFY_NAME = "SeChangeNotifyPrivilege";
  
  public static final String SE_REMOTE_SHUTDOWN_NAME = "SeRemoteShutdownPrivilege";
  
  public static final String SE_UNDOCK_NAME = "SeUndockPrivilege";
  
  public static final String SE_SYNC_AGENT_NAME = "SeSyncAgentPrivilege";
  
  public static final String SE_ENABLE_DELEGATION_NAME = "SeEnableDelegationPrivilege";
  
  public static final String SE_MANAGE_VOLUME_NAME = "SeManageVolumePrivilege";
  
  public static final String SE_IMPERSONATE_NAME = "SeImpersonatePrivilege";
  
  public static final String SE_CREATE_GLOBAL_NAME = "SeCreateGlobalPrivilege";
  
  public static final int SE_PRIVILEGE_ENABLED_BY_DEFAULT = 1;
  
  public static final int SE_PRIVILEGE_ENABLED = 2;
  
  public static final int SE_PRIVILEGE_REMOVED = 4;
  
  public static final int SE_PRIVILEGE_USED_FOR_ACCESS = -2147483648;
  
  public static final int PROCESS_CREATE_PROCESS = 128;
  
  public static final int PROCESS_CREATE_THREAD = 2;
  
  public static final int PROCESS_DUP_HANDLE = 64;
  
  public static final int PROCESS_ALL_ACCESS = 2039803;
  
  public static final int PROCESS_QUERY_INFORMATION = 1024;
  
  public static final int PROCESS_QUERY_LIMITED_INFORMATION = 4096;
  
  public static final int PROCESS_SET_INFORMATION = 512;
  
  public static final int PROCESS_SET_QUOTA = 256;
  
  public static final int PROCESS_SUSPEND_RESUME = 2048;
  
  public static final int PROCESS_TERMINATE = 1;
  
  public static final int PROCESS_NAME_NATIVE = 1;
  
  public static final int PROCESS_VM_OPERATION = 8;
  
  public static final int PROCESS_VM_READ = 16;
  
  public static final int PROCESS_VM_WRITE = 32;
  
  public static final int PROCESS_SYNCHRONIZE = 1048576;
  
  public static final int OWNER_SECURITY_INFORMATION = 1;
  
  public static final int GROUP_SECURITY_INFORMATION = 2;
  
  public static final int DACL_SECURITY_INFORMATION = 4;
  
  public static final int SACL_SECURITY_INFORMATION = 8;
  
  public static final int LABEL_SECURITY_INFORMATION = 16;
  
  public static final int PROTECTED_DACL_SECURITY_INFORMATION = -2147483648;
  
  public static final int PROTECTED_SACL_SECURITY_INFORMATION = 1073741824;
  
  public static final int UNPROTECTED_DACL_SECURITY_INFORMATION = 536870912;
  
  public static final int UNPROTECTED_SACL_SECURITY_INFORMATION = 268435456;
  
  public static final int SE_OWNER_DEFAULTED = 1;
  
  public static final int SE_GROUP_DEFAULTED = 2;
  
  public static final int SE_DACL_PRESENT = 4;
  
  public static final int SE_DACL_DEFAULTED = 8;
  
  public static final int SE_SACL_PRESENT = 16;
  
  public static final int SE_SACL_DEFAULTED = 32;
  
  public static final int SE_DACL_AUTO_INHERIT_REQ = 256;
  
  public static final int SE_SACL_AUTO_INHERIT_REQ = 512;
  
  public static final int SE_DACL_AUTO_INHERITED = 1024;
  
  public static final int SE_SACL_AUTO_INHERITED = 2048;
  
  public static final int SE_DACL_PROTECTED = 4096;
  
  public static final int SE_SACL_PROTECTED = 8192;
  
  public static final int SE_RM_CONTROL_VALID = 16384;
  
  public static final int SE_SELF_RELATIVE = 32768;
  
  public static final int SECURITY_DESCRIPTOR_REVISION = 1;
  
  public static final int ACL_REVISION = 2;
  
  public static final int ACL_REVISION_DS = 4;
  
  public static final int ACL_REVISION1 = 1;
  
  public static final int ACL_REVISION2 = 2;
  
  public static final int ACL_REVISION3 = 3;
  
  public static final int ACL_REVISION4 = 4;
  
  public static final int MIN_ACL_REVISION = 2;
  
  public static final int MAX_ACL_REVISION = 4;
  
  public static final byte ACCESS_ALLOWED_ACE_TYPE = 0;
  
  public static final byte ACCESS_DENIED_ACE_TYPE = 1;
  
  public static final byte SYSTEM_AUDIT_ACE_TYPE = 2;
  
  public static final byte SYSTEM_ALARM_ACE_TYPE = 3;
  
  public static final byte ACCESS_ALLOWED_COMPOUND_ACE_TYPE = 4;
  
  public static final byte ACCESS_ALLOWED_OBJECT_ACE_TYPE = 5;
  
  public static final byte ACCESS_DENIED_OBJECT_ACE_TYPE = 6;
  
  public static final byte SYSTEM_AUDIT_OBJECT_ACE_TYPE = 7;
  
  public static final byte SYSTEM_ALARM_OBJECT_ACE_TYPE = 8;
  
  public static final byte ACCESS_ALLOWED_CALLBACK_ACE_TYPE = 9;
  
  public static final byte ACCESS_DENIED_CALLBACK_ACE_TYPE = 10;
  
  public static final byte ACCESS_ALLOWED_CALLBACK_OBJECT_ACE_TYPE = 11;
  
  public static final byte ACCESS_DENIED_CALLBACK_OBJECT_ACE_TYPE = 12;
  
  public static final byte SYSTEM_AUDIT_CALLBACK_ACE_TYPE = 13;
  
  public static final byte SYSTEM_ALARM_CALLBACK_ACE_TYPE = 14;
  
  public static final byte SYSTEM_AUDIT_CALLBACK_OBJECT_ACE_TYPE = 15;
  
  public static final byte SYSTEM_ALARM_CALLBACK_OBJECT_ACE_TYPE = 16;
  
  public static final byte SYSTEM_MANDATORY_LABEL_ACE_TYPE = 17;
  
  public static final byte OBJECT_INHERIT_ACE = 1;
  
  public static final byte CONTAINER_INHERIT_ACE = 2;
  
  public static final byte NO_PROPAGATE_INHERIT_ACE = 4;
  
  public static final byte INHERIT_ONLY_ACE = 8;
  
  public static final byte INHERITED_ACE = 16;
  
  public static final byte VALID_INHERIT_FLAGS = 31;
  
  public static final byte CACHE_FULLY_ASSOCIATIVE = -1;
  
  public static final int NUM_DISCHARGE_POLICIES = 4;
  
  public static final int MEM_COMMIT = 4096;
  
  public static final int MEM_FREE = 65536;
  
  public static final int MEM_RESERVE = 8192;
  
  public static final int MEM_IMAGE = 16777216;
  
  public static final int MEM_MAPPED = 262144;
  
  public static final int MEM_PRIVATE = 131072;
  
  public static final int MEM_RESET = 524288;
  
  public static final int MEM_RESET_UNDO = 16777216;
  
  public static final int MEM_LARGE_PAGES = 536870912;
  
  public static final int MEM_PHYSICAL = 4194304;
  
  public static final int MEM_TOP_DOWN = 1048576;
  
  public static final int MEM_COALESCE_PLACEHOLDERS = 1;
  
  public static final int MEM_PRESERVE_PLACEHOLDER = 2;
  
  public static final int MEM_DECOMMIT = 16384;
  
  public static final int MEM_RELEASE = 32768;
  
  public static final byte SECURITY_DYNAMIC_TRACKING = 1;
  
  public static final byte SECURITY_STATIC_TRACKING = 0;
  
  public static final byte BOOLEAN_TRUE = 1;
  
  public static final byte BOOLEAN_FALSE = 0;
  
  public static final int LANG_NEUTRAL = 0;
  
  public static final int LANG_INVARIANT = 127;
  
  public static final int LANG_AFRIKAANS = 54;
  
  public static final int LANG_ALBANIAN = 28;
  
  public static final int LANG_ARABIC = 1;
  
  public static final int LANG_ARMENIAN = 43;
  
  public static final int LANG_ASSAMESE = 77;
  
  public static final int LANG_AZERI = 44;
  
  public static final int LANG_BASQUE = 45;
  
  public static final int LANG_BELARUSIAN = 35;
  
  public static final int LANG_BENGALI = 69;
  
  public static final int LANG_BULGARIAN = 2;
  
  public static final int LANG_CATALAN = 3;
  
  public static final int LANG_CHINESE = 4;
  
  public static final int LANG_CROATIAN = 26;
  
  public static final int LANG_CZECH = 5;
  
  public static final int LANG_DANISH = 6;
  
  public static final int LANG_DIVEHI = 101;
  
  public static final int LANG_DUTCH = 19;
  
  public static final int LANG_ENGLISH = 9;
  
  public static final int LANG_ESTONIAN = 37;
  
  public static final int LANG_FAEROESE = 56;
  
  public static final int LANG_FARSI = 41;
  
  public static final int LANG_FINNISH = 11;
  
  public static final int LANG_FRENCH = 12;
  
  public static final int LANG_GALICIAN = 86;
  
  public static final int LANG_GEORGIAN = 55;
  
  public static final int LANG_GERMAN = 7;
  
  public static final int LANG_GREEK = 8;
  
  public static final int LANG_GUJARATI = 71;
  
  public static final int LANG_HEBREW = 13;
  
  public static final int LANG_HINDI = 57;
  
  public static final int LANG_HUNGARIAN = 14;
  
  public static final int LANG_ICELANDIC = 15;
  
  public static final int LANG_INDONESIAN = 33;
  
  public static final int LANG_ITALIAN = 16;
  
  public static final int LANG_JAPANESE = 17;
  
  public static final int LANG_KANNADA = 75;
  
  public static final int LANG_KASHMIRI = 96;
  
  public static final int LANG_KAZAK = 63;
  
  public static final int LANG_KONKANI = 87;
  
  public static final int LANG_KOREAN = 18;
  
  public static final int LANG_KYRGYZ = 64;
  
  public static final int LANG_LATVIAN = 38;
  
  public static final int LANG_LITHUANIAN = 39;
  
  public static final int LANG_MACEDONIAN = 47;
  
  public static final int LANG_MALAY = 62;
  
  public static final int LANG_MALAYALAM = 76;
  
  public static final int LANG_MANIPURI = 88;
  
  public static final int LANG_MARATHI = 78;
  
  public static final int LANG_MONGOLIAN = 80;
  
  public static final int LANG_NEPALI = 97;
  
  public static final int LANG_NORWEGIAN = 20;
  
  public static final int LANG_ORIYA = 72;
  
  public static final int LANG_POLISH = 21;
  
  public static final int LANG_PORTUGUESE = 22;
  
  public static final int LANG_PUNJABI = 70;
  
  public static final int LANG_ROMANIAN = 24;
  
  public static final int LANG_RUSSIAN = 25;
  
  public static final int LANG_SANSKRIT = 79;
  
  public static final int LANG_SERBIAN = 26;
  
  public static final int LANG_SINDHI = 89;
  
  public static final int LANG_SLOVAK = 27;
  
  public static final int LANG_SLOVENIAN = 36;
  
  public static final int LANG_SPANISH = 10;
  
  public static final int LANG_SWAHILI = 65;
  
  public static final int LANG_SWEDISH = 29;
  
  public static final int LANG_SYRIAC = 90;
  
  public static final int LANG_TAMIL = 73;
  
  public static final int LANG_TATAR = 68;
  
  public static final int LANG_TELUGU = 74;
  
  public static final int LANG_THAI = 30;
  
  public static final int LANG_TURKISH = 31;
  
  public static final int LANG_UKRAINIAN = 34;
  
  public static final int LANG_URDU = 32;
  
  public static final int LANG_UZBEK = 67;
  
  public static final int LANG_VIETNAMESE = 42;
  
  public static final int SUBLANG_NEUTRAL = 0;
  
  public static final int SUBLANG_DEFAULT = 1;
  
  public static final int SUBLANG_SYS_DEFAULT = 2;
  
  public static final int SUBLANG_ARABIC_SAUDI_ARABIA = 1;
  
  public static final int SUBLANG_ARABIC_IRAQ = 2;
  
  public static final int SUBLANG_ARABIC_EGYPT = 3;
  
  public static final int SUBLANG_ARABIC_LIBYA = 4;
  
  public static final int SUBLANG_ARABIC_ALGERIA = 5;
  
  public static final int SUBLANG_ARABIC_MOROCCO = 6;
  
  public static final int SUBLANG_ARABIC_TUNISIA = 7;
  
  public static final int SUBLANG_ARABIC_OMAN = 8;
  
  public static final int SUBLANG_ARABIC_YEMEN = 9;
  
  public static final int SUBLANG_ARABIC_SYRIA = 10;
  
  public static final int SUBLANG_ARABIC_JORDAN = 11;
  
  public static final int SUBLANG_ARABIC_LEBANON = 12;
  
  public static final int SUBLANG_ARABIC_KUWAIT = 13;
  
  public static final int SUBLANG_ARABIC_UAE = 14;
  
  public static final int SUBLANG_ARABIC_BAHRAIN = 15;
  
  public static final int SUBLANG_ARABIC_QATAR = 16;
  
  public static final int SUBLANG_AZERI_LATIN = 1;
  
  public static final int SUBLANG_AZERI_CYRILLIC = 2;
  
  public static final int SUBLANG_CHINESE_TRADITIONAL = 1;
  
  public static final int SUBLANG_CHINESE_SIMPLIFIED = 2;
  
  public static final int SUBLANG_CHINESE_HONGKONG = 3;
  
  public static final int SUBLANG_CHINESE_SINGAPORE = 4;
  
  public static final int SUBLANG_CHINESE_MACAU = 5;
  
  public static final int SUBLANG_DUTCH = 1;
  
  public static final int SUBLANG_DUTCH_BELGIAN = 2;
  
  public static final int SUBLANG_ENGLISH_US = 1;
  
  public static final int SUBLANG_ENGLISH_UK = 2;
  
  public static final int SUBLANG_ENGLISH_AUS = 3;
  
  public static final int SUBLANG_ENGLISH_CAN = 4;
  
  public static final int SUBLANG_ENGLISH_NZ = 5;
  
  public static final int SUBLANG_ENGLISH_EIRE = 6;
  
  public static final int SUBLANG_ENGLISH_SOUTH_AFRICA = 7;
  
  public static final int SUBLANG_ENGLISH_JAMAICA = 8;
  
  public static final int SUBLANG_ENGLISH_CARIBBEAN = 9;
  
  public static final int SUBLANG_ENGLISH_BELIZE = 10;
  
  public static final int SUBLANG_ENGLISH_TRINIDAD = 11;
  
  public static final int SUBLANG_ENGLISH_ZIMBABWE = 12;
  
  public static final int SUBLANG_ENGLISH_PHILIPPINES = 13;
  
  public static final int SUBLANG_FRENCH = 1;
  
  public static final int SUBLANG_FRENCH_BELGIAN = 2;
  
  public static final int SUBLANG_FRENCH_CANADIAN = 3;
  
  public static final int SUBLANG_FRENCH_SWISS = 4;
  
  public static final int SUBLANG_FRENCH_LUXEMBOURG = 5;
  
  public static final int SUBLANG_FRENCH_MONACO = 6;
  
  public static final int SUBLANG_GERMAN = 1;
  
  public static final int SUBLANG_GERMAN_SWISS = 2;
  
  public static final int SUBLANG_GERMAN_AUSTRIAN = 3;
  
  public static final int SUBLANG_GERMAN_LUXEMBOURG = 4;
  
  public static final int SUBLANG_GERMAN_LIECHTENSTEIN = 5;
  
  public static final int SUBLANG_ITALIAN = 1;
  
  public static final int SUBLANG_ITALIAN_SWISS = 2;
  
  public static final int SUBLANG_KASHMIRI_SASIA = 2;
  
  public static final int SUBLANG_KASHMIRI_INDIA = 2;
  
  public static final int SUBLANG_KOREAN = 1;
  
  public static final int SUBLANG_LITHUANIAN = 1;
  
  public static final int SUBLANG_MALAY_MALAYSIA = 1;
  
  public static final int SUBLANG_MALAY_BRUNEI_DARUSSALAM = 2;
  
  public static final int SUBLANG_NEPALI_INDIA = 2;
  
  public static final int SUBLANG_NORWEGIAN_BOKMAL = 1;
  
  public static final int SUBLANG_NORWEGIAN_NYNORSK = 2;
  
  public static final int SUBLANG_PORTUGUESE = 2;
  
  public static final int SUBLANG_PORTUGUESE_BRAZILIAN = 1;
  
  public static final int SUBLANG_SERBIAN_LATIN = 2;
  
  public static final int SUBLANG_SERBIAN_CYRILLIC = 3;
  
  public static final int SUBLANG_SPANISH = 1;
  
  public static final int SUBLANG_SPANISH_MEXICAN = 2;
  
  public static final int SUBLANG_SPANISH_MODERN = 3;
  
  public static final int SUBLANG_SPANISH_GUATEMALA = 4;
  
  public static final int SUBLANG_SPANISH_COSTA_RICA = 5;
  
  public static final int SUBLANG_SPANISH_PANAMA = 6;
  
  public static final int SUBLANG_SPANISH_DOMINICAN_REPUBLIC = 7;
  
  public static final int SUBLANG_SPANISH_VENEZUELA = 8;
  
  public static final int SUBLANG_SPANISH_COLOMBIA = 9;
  
  public static final int SUBLANG_SPANISH_PERU = 10;
  
  public static final int SUBLANG_SPANISH_ARGENTINA = 11;
  
  public static final int SUBLANG_SPANISH_ECUADOR = 12;
  
  public static final int SUBLANG_SPANISH_CHILE = 13;
  
  public static final int SUBLANG_SPANISH_URUGUAY = 14;
  
  public static final int SUBLANG_SPANISH_PARAGUAY = 15;
  
  public static final int SUBLANG_SPANISH_BOLIVIA = 16;
  
  public static final int SUBLANG_SPANISH_EL_SALVADOR = 17;
  
  public static final int SUBLANG_SPANISH_HONDURAS = 18;
  
  public static final int SUBLANG_SPANISH_NICARAGUA = 19;
  
  public static final int SUBLANG_SPANISH_PUERTO_RICO = 20;
  
  public static final int SUBLANG_SWEDISH = 1;
  
  public static final int SUBLANG_SWEDISH_FINLAND = 2;
  
  public static final int SUBLANG_URDU_PAKISTAN = 1;
  
  public static final int SUBLANG_URDU_INDIA = 2;
  
  public static final int SUBLANG_UZBEK_LATIN = 1;
  
  public static final int SUBLANG_UZBEK_CYRILLIC = 2;
  
  public static final int SORT_DEFAULT = 0;
  
  public static final int SORT_JAPANESE_XJIS = 0;
  
  public static final int SORT_JAPANESE_UNICODE = 1;
  
  public static final int SORT_CHINESE_BIG5 = 0;
  
  public static final int SORT_CHINESE_PRCP = 0;
  
  public static final int SORT_CHINESE_UNICODE = 1;
  
  public static final int SORT_CHINESE_PRC = 2;
  
  public static final int SORT_CHINESE_BOPOMOFO = 3;
  
  public static final int SORT_KOREAN_KSC = 0;
  
  public static final int SORT_KOREAN_UNICODE = 1;
  
  public static final int SORT_GERMAN_PHONE_BOOK = 1;
  
  public static final int SORT_HUNGARIAN_DEFAULT = 0;
  
  public static final int SORT_HUNGARIAN_TECHNICAL = 1;
  
  public static final int SORT_GEORGIAN_TRADITIONAL = 0;
  
  public static final int SORT_GEORGIAN_MODERN = 1;
  
  public static final int NLS_VALID_LOCALE_MASK = 1048575;
  
  public static abstract class WinNT {}
  
  public static abstract class WinNT {}
  
  public static abstract class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static abstract class WinNT {}
  
  @FieldOrder({"NextEntryOffset", "Action", "FileNameLength", "FileName"})
  public static class FILE_NOTIFY_INFORMATION extends Structure {
    public int NextEntryOffset;
    
    public int Action;
    
    public int FileNameLength;
    
    public char[] FileName = new char[1];
    
    private FILE_NOTIFY_INFORMATION() {}
    
    public FILE_NOTIFY_INFORMATION(int size) {
      if (size < size())
        throw new IllegalArgumentException("Size must greater than " + 
            size() + ", requested " + size); 
      allocateMemory(size);
    }
    
    public String getFilename() {
      return new String(this.FileName, 0, this.FileNameLength / 2);
    }
    
    public void read() {
      this.FileName = new char[0];
      super.read();
      this.FileName = getPointer().getCharArray(12L, this.FileNameLength / 2);
    }
    
    public FILE_NOTIFY_INFORMATION next() {
      if (this.NextEntryOffset == 0)
        return null; 
      FILE_NOTIFY_INFORMATION next = new FILE_NOTIFY_INFORMATION();
      next.useMemory(getPointer(), this.NextEntryOffset);
      next.read();
      return next;
    }
  }
  
  public static class WinNT {}
  
  @FieldOrder({"u"})
  public static class LARGE_INTEGER extends Structure implements Comparable<LARGE_INTEGER> {
    public UNION u;
    
    public LARGE_INTEGER() {}
    
    public LARGE_INTEGER(long value) {
      this.u = new UNION(value);
    }
    
    public WinDef.DWORD getLow() {
      return this.u.lh.LowPart;
    }
    
    public WinDef.DWORD getHigh() {
      return this.u.lh.HighPart;
    }
    
    public long getValue() {
      return this.u.value;
    }
    
    public int compareTo(LARGE_INTEGER other) {
      return compare(this, other);
    }
    
    public String toString() {
      return (this.u == null) ? "null" : Long.toString(getValue());
    }
    
    public static int compare(LARGE_INTEGER v1, LARGE_INTEGER v2) {
      if (v1 == v2)
        return 0; 
      if (v1 == null)
        return 1; 
      if (v2 == null)
        return -1; 
      return IntegerType.compare(v1.getValue(), v2.getValue());
    }
    
    public static int compare(LARGE_INTEGER v1, long v2) {
      if (v1 == null)
        return 1; 
      return IntegerType.compare(v1.getValue(), v2);
    }
    
    public static class LARGE_INTEGER {}
    
    public static class LARGE_INTEGER {}
    
    public static class LARGE_INTEGER {}
  }
  
  public static class HANDLE extends PointerType {
    private boolean immutable;
    
    public HANDLE() {}
    
    public HANDLE(Pointer p) {
      setPointer(p);
      this.immutable = true;
    }
    
    public Object fromNative(Object nativeValue, FromNativeContext context) {
      Object o = super.fromNative(nativeValue, context);
      if (WinBase.INVALID_HANDLE_VALUE.equals(o))
        return WinBase.INVALID_HANDLE_VALUE; 
      return o;
    }
    
    public void setPointer(Pointer p) {
      if (this.immutable)
        throw new UnsupportedOperationException("immutable reference"); 
      super.setPointer(p);
    }
    
    public String toString() {
      return String.valueOf(getPointer());
    }
  }
  
  public static class HANDLEByReference extends ByReference {
    public HANDLEByReference() {
      this(null);
    }
    
    public HANDLEByReference(WinNT.HANDLE h) {
      super(Native.POINTER_SIZE);
      setValue(h);
    }
    
    public void setValue(WinNT.HANDLE h) {
      getPointer().setPointer(0L, (h != null) ? h.getPointer() : null);
    }
    
    public WinNT.HANDLE getValue() {
      Pointer p = getPointer().getPointer(0L);
      if (p == null)
        return null; 
      if (WinBase.INVALID_HANDLE_VALUE.getPointer().equals(p))
        return WinBase.INVALID_HANDLE_VALUE; 
      WinNT.HANDLE h = new WinNT.HANDLE();
      h.setPointer(p);
      return h;
    }
  }
  
  public static class HRESULT extends NativeLong {
    public HRESULT() {}
    
    public HRESULT(int value) {
      super(value);
    }
  }
  
  public static abstract class WinNT {}
  
  @FieldOrder({"dwOSVersionInfoSize", "dwMajorVersion", "dwMinorVersion", "dwBuildNumber", "dwPlatformId", "szCSDVersion"})
  public static class OSVERSIONINFO extends Structure {
    public WinDef.DWORD dwOSVersionInfoSize;
    
    public WinDef.DWORD dwMajorVersion;
    
    public WinDef.DWORD dwMinorVersion;
    
    public WinDef.DWORD dwBuildNumber;
    
    public WinDef.DWORD dwPlatformId;
    
    public char[] szCSDVersion;
    
    public OSVERSIONINFO() {
      this.szCSDVersion = new char[128];
      this.dwOSVersionInfoSize = new WinDef.DWORD(size());
    }
    
    public OSVERSIONINFO(Pointer memory) {
      super(memory);
      read();
    }
  }
  
  @FieldOrder({"dwOSVersionInfoSize", "dwMajorVersion", "dwMinorVersion", "dwBuildNumber", "dwPlatformId", "szCSDVersion", "wServicePackMajor", "wServicePackMinor", "wSuiteMask", "wProductType", "wReserved"})
  public static class OSVERSIONINFOEX extends Structure {
    public WinDef.DWORD dwOSVersionInfoSize;
    
    public WinDef.DWORD dwMajorVersion;
    
    public WinDef.DWORD dwMinorVersion;
    
    public WinDef.DWORD dwBuildNumber;
    
    public WinDef.DWORD dwPlatformId;
    
    public char[] szCSDVersion;
    
    public WinDef.WORD wServicePackMajor;
    
    public WinDef.WORD wServicePackMinor;
    
    public WinDef.WORD wSuiteMask;
    
    public byte wProductType;
    
    public byte wReserved;
    
    public OSVERSIONINFOEX() {
      this.szCSDVersion = new char[128];
      this.dwOSVersionInfoSize = new WinDef.DWORD(size());
    }
    
    public OSVERSIONINFOEX(Pointer memory) {
      super(memory);
      read();
    }
    
    public int getMajor() {
      return this.dwMajorVersion.intValue();
    }
    
    public int getMinor() {
      return this.dwMinorVersion.intValue();
    }
    
    public int getBuildNumber() {
      return this.dwBuildNumber.intValue();
    }
    
    public int getPlatformId() {
      return this.dwPlatformId.intValue();
    }
    
    public String getServicePack() {
      return Native.toString(this.szCSDVersion);
    }
    
    public int getSuiteMask() {
      return this.wSuiteMask.intValue();
    }
    
    public byte getProductType() {
      return this.wProductType;
    }
  }
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static abstract class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static interface OVERLAPPED_COMPLETION_ROUTINE extends StdCallLibrary.StdCallCallback {
    void callback(int param1Int1, int param1Int2, WinBase.OVERLAPPED param1OVERLAPPED);
  }
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static abstract class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static interface WinNT {}
  
  public static class WinNT {}
  
  public static abstract class WinNT {}
  
  public static interface WinNT {}
  
  public static interface WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  public static class WinNT {}
  
  @FieldOrder({"baseAddress", "allocationBase", "allocationProtect", "regionSize", "state", "protect", "type"})
  public static class MEMORY_BASIC_INFORMATION extends Structure {
    public Pointer baseAddress;
    
    public Pointer allocationBase;
    
    public WinDef.DWORD allocationProtect;
    
    public BaseTSD.SIZE_T regionSize;
    
    public WinDef.DWORD state;
    
    public WinDef.DWORD protect;
    
    public WinDef.DWORD type;
  }
  
  public static final int LANG_SYSTEM_DEFAULT = LocaleMacros.MAKELANGID(0, 2);
  
  public static final int LANG_USER_DEFAULT = LocaleMacros.MAKELANGID(0, 1);
  
  public static final WinDef.LCID LOCALE_SYSTEM_DEFAULT = LocaleMacros.MAKELCID(LANG_SYSTEM_DEFAULT, 0);
  
  public static final WinDef.LCID LOCALE_USER_DEFAULT = LocaleMacros.MAKELCID(LANG_USER_DEFAULT, 0);
  
  public static final WinDef.LCID LOCALE_NEUTRAL = LocaleMacros.MAKELCID(LocaleMacros.MAKELANGID(0, 0), 0);
  
  public static final WinDef.LCID LOCALE_INVARIANT = LocaleMacros.MAKELCID(LocaleMacros.MAKELANGID(127, 0), 0);
  
  public static final int EVENT_MODIFY_STATE = 2;
  
  public static final int EVENT_ALL_ACCESS = 2031619;
  
  public static class WinNT {}
  
  public static final class WinNT {}
  
  @FieldOrder({"ReadOperationCount", "WriteOperationCount", "OtherOperationCount", "ReadTransferCount", "WriteTransferCount", "OtherTransferCount"})
  public static class IO_COUNTERS extends Structure {
    public long ReadOperationCount;
    
    public long WriteOperationCount;
    
    public long OtherOperationCount;
    
    public long ReadTransferCount;
    
    public long WriteTransferCount;
    
    public long OtherTransferCount;
    
    public IO_COUNTERS() {}
    
    public IO_COUNTERS(Pointer memory) {
      super(memory);
    }
  }
}
