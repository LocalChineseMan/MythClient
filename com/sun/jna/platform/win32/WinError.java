package com.sun.jna.platform.win32;

public interface WinError {
  public static final short FACILITY_WINRM = 51;
  
  public static final short FACILITY_WINDOWSUPDATE = 36;
  
  public static final short FACILITY_WINDOWS_DEFENDER = 80;
  
  public static final short FACILITY_WINDOWS_CE = 24;
  
  public static final short FACILITY_WINDOWS = 8;
  
  public static final short FACILITY_URT = 19;
  
  public static final short FACILITY_UMI = 22;
  
  public static final short FACILITY_TPM_SOFTWARE = 41;
  
  public static final short FACILITY_TPM_SERVICES = 40;
  
  public static final short FACILITY_SXS = 23;
  
  public static final short FACILITY_STORAGE = 3;
  
  public static final short FACILITY_STATE_MANAGEMENT = 34;
  
  public static final short FACILITY_SSPI = 9;
  
  public static final short FACILITY_SCARD = 16;
  
  public static final short FACILITY_SHELL = 39;
  
  public static final short FACILITY_SETUPAPI = 15;
  
  public static final short FACILITY_SECURITY = 9;
  
  public static final short FACILITY_RPC = 1;
  
  public static final short FACILITY_PLA = 48;
  
  public static final short FACILITY_WIN32 = 7;
  
  public static final short FACILITY_CONTROL = 10;
  
  public static final short FACILITY_NULL = 0;
  
  public static final short FACILITY_NDIS = 52;
  
  public static final short FACILITY_METADIRECTORY = 35;
  
  public static final short FACILITY_MSMQ = 14;
  
  public static final short FACILITY_MEDIASERVER = 13;
  
  public static final short FACILITY_INTERNET = 12;
  
  public static final short FACILITY_ITF = 4;
  
  public static final short FACILITY_USERMODE_HYPERVISOR = 53;
  
  public static final short FACILITY_HTTP = 25;
  
  public static final short FACILITY_GRAPHICS = 38;
  
  public static final short FACILITY_FWP = 50;
  
  public static final short FACILITY_FVE = 49;
  
  public static final short FACILITY_USERMODE_FILTER_MANAGER = 31;
  
  public static final short FACILITY_DPLAY = 21;
  
  public static final short FACILITY_DISPATCH = 2;
  
  public static final short FACILITY_DIRECTORYSERVICE = 37;
  
  public static final short FACILITY_CONFIGURATION = 33;
  
  public static final short FACILITY_COMPLUS = 17;
  
  public static final short FACILITY_USERMODE_COMMONLOG = 26;
  
  public static final short FACILITY_CMI = 54;
  
  public static final short FACILITY_CERT = 11;
  
  public static final short FACILITY_BACKGROUNDCOPY = 32;
  
  public static final short FACILITY_ACS = 20;
  
  public static final short FACILITY_AAF = 18;
  
  public static final int ERROR_SUCCESS = 0;
  
  public static final int NO_ERROR = 0;
  
  public static final int SEC_E_OK = 0;
  
  public static final int ERROR_INVALID_FUNCTION = 1;
  
  public static final int ERROR_FILE_NOT_FOUND = 2;
  
  public static final int ERROR_PATH_NOT_FOUND = 3;
  
  public static final int ERROR_TOO_MANY_OPEN_FILES = 4;
  
  public static final int ERROR_ACCESS_DENIED = 5;
  
  public static final int ERROR_INVALID_HANDLE = 6;
  
  public static final int ERROR_ARENA_TRASHED = 7;
  
  public static final int ERROR_NOT_ENOUGH_MEMORY = 8;
  
  public static final int ERROR_INVALID_BLOCK = 9;
  
  public static final int ERROR_BAD_ENVIRONMENT = 10;
  
  public static final int ERROR_BAD_FORMAT = 11;
  
  public static final int ERROR_INVALID_ACCESS = 12;
  
  public static final int ERROR_INVALID_DATA = 13;
  
  public static final int ERROR_OUTOFMEMORY = 14;
  
  public static final int ERROR_INVALID_DRIVE = 15;
  
  public static final int ERROR_CURRENT_DIRECTORY = 16;
  
  public static final int ERROR_NOT_SAME_DEVICE = 17;
  
  public static final int ERROR_NO_MORE_FILES = 18;
  
  public static final int ERROR_WRITE_PROTECT = 19;
  
  public static final int ERROR_BAD_UNIT = 20;
  
  public static final int ERROR_NOT_READY = 21;
  
  public static final int ERROR_BAD_COMMAND = 22;
  
  public static final int ERROR_CRC = 23;
  
  public static final int ERROR_BAD_LENGTH = 24;
  
  public static final int ERROR_SEEK = 25;
  
  public static final int ERROR_NOT_DOS_DISK = 26;
  
  public static final int ERROR_SECTOR_NOT_FOUND = 27;
  
  public static final int ERROR_OUT_OF_PAPER = 28;
  
  public static final int ERROR_WRITE_FAULT = 29;
  
  public static final int ERROR_READ_FAULT = 30;
  
  public static final int ERROR_GEN_FAILURE = 31;
  
  public static final int ERROR_SHARING_VIOLATION = 32;
  
  public static final int ERROR_LOCK_VIOLATION = 33;
  
  public static final int ERROR_WRONG_DISK = 34;
  
  public static final int ERROR_SHARING_BUFFER_EXCEEDED = 36;
  
  public static final int ERROR_HANDLE_EOF = 38;
  
  public static final int ERROR_HANDLE_DISK_FULL = 39;
  
  public static final int ERROR_NOT_SUPPORTED = 50;
  
  public static final int ERROR_REM_NOT_LIST = 51;
  
  public static final int ERROR_DUP_NAME = 52;
  
  public static final int ERROR_BAD_NETPATH = 53;
  
  public static final int ERROR_NETWORK_BUSY = 54;
  
  public static final int ERROR_DEV_NOT_EXIST = 55;
  
  public static final int ERROR_TOO_MANY_CMDS = 56;
  
  public static final int ERROR_ADAP_HDW_ERR = 57;
  
  public static final int ERROR_BAD_NET_RESP = 58;
  
  public static final int ERROR_UNEXP_NET_ERR = 59;
  
  public static final int ERROR_BAD_REM_ADAP = 60;
  
  public static final int ERROR_PRINTQ_FULL = 61;
  
  public static final int ERROR_NO_SPOOL_SPACE = 62;
  
  public static final int ERROR_PRINT_CANCELLED = 63;
  
  public static final int ERROR_NETNAME_DELETED = 64;
  
  public static final int ERROR_NETWORK_ACCESS_DENIED = 65;
  
  public static final int ERROR_BAD_DEV_TYPE = 66;
  
  public static final int ERROR_BAD_NET_NAME = 67;
  
  public static final int ERROR_TOO_MANY_NAMES = 68;
  
  public static final int ERROR_TOO_MANY_SESS = 69;
  
  public static final int ERROR_SHARING_PAUSED = 70;
  
  public static final int ERROR_REQ_NOT_ACCEP = 71;
  
  public static final int ERROR_REDIR_PAUSED = 72;
  
  public static final int ERROR_FILE_EXISTS = 80;
  
  public static final int ERROR_CANNOT_MAKE = 82;
  
  public static final int ERROR_FAIL_I24 = 83;
  
  public static final int ERROR_OUT_OF_STRUCTURES = 84;
  
  public static final int ERROR_ALREADY_ASSIGNED = 85;
  
  public static final int ERROR_INVALID_PASSWORD = 86;
  
  public static final int ERROR_INVALID_PARAMETER = 87;
  
  public static final int ERROR_NET_WRITE_FAULT = 88;
  
  public static final int ERROR_NO_PROC_SLOTS = 89;
  
  public static final int ERROR_TOO_MANY_SEMAPHORES = 100;
  
  public static final int ERROR_EXCL_SEM_ALREADY_OWNED = 101;
  
  public static final int ERROR_SEM_IS_SET = 102;
  
  public static final int ERROR_TOO_MANY_SEM_REQUESTS = 103;
  
  public static final int ERROR_INVALID_AT_INTERRUPT_TIME = 104;
  
  public static final int ERROR_SEM_OWNER_DIED = 105;
  
  public static final int ERROR_SEM_USER_LIMIT = 106;
  
  public static final int ERROR_DISK_CHANGE = 107;
  
  public static final int ERROR_DRIVE_LOCKED = 108;
  
  public static final int ERROR_BROKEN_PIPE = 109;
  
  public static final int ERROR_OPEN_FAILED = 110;
  
  public static final int ERROR_BUFFER_OVERFLOW = 111;
  
  public static final int ERROR_DISK_FULL = 112;
  
  public static final int ERROR_NO_MORE_SEARCH_HANDLES = 113;
  
  public static final int ERROR_INVALID_TARGET_HANDLE = 114;
  
  public static final int ERROR_INVALID_CATEGORY = 117;
  
  public static final int ERROR_INVALID_VERIFY_SWITCH = 118;
  
  public static final int ERROR_BAD_DRIVER_LEVEL = 119;
  
  public static final int ERROR_CALL_NOT_IMPLEMENTED = 120;
  
  public static final int ERROR_SEM_TIMEOUT = 121;
  
  public static final int ERROR_INSUFFICIENT_BUFFER = 122;
  
  public static final int ERROR_INVALID_NAME = 123;
  
  public static final int ERROR_INVALID_LEVEL = 124;
  
  public static final int ERROR_NO_VOLUME_LABEL = 125;
  
  public static final int ERROR_MOD_NOT_FOUND = 126;
  
  public static final int ERROR_PROC_NOT_FOUND = 127;
  
  public static final int ERROR_WAIT_NO_CHILDREN = 128;
  
  public static final int ERROR_CHILD_NOT_COMPLETE = 129;
  
  public static final int ERROR_DIRECT_ACCESS_HANDLE = 130;
  
  public static final int ERROR_NEGATIVE_SEEK = 131;
  
  public static final int ERROR_SEEK_ON_DEVICE = 132;
  
  public static final int ERROR_IS_JOIN_TARGET = 133;
  
  public static final int ERROR_IS_JOINED = 134;
  
  public static final int ERROR_IS_SUBSTED = 135;
  
  public static final int ERROR_NOT_JOINED = 136;
  
  public static final int ERROR_NOT_SUBSTED = 137;
  
  public static final int ERROR_JOIN_TO_JOIN = 138;
  
  public static final int ERROR_SUBST_TO_SUBST = 139;
  
  public static final int ERROR_JOIN_TO_SUBST = 140;
  
  public static final int ERROR_SUBST_TO_JOIN = 141;
  
  public static final int ERROR_BUSY_DRIVE = 142;
  
  public static final int ERROR_SAME_DRIVE = 143;
  
  public static final int ERROR_DIR_NOT_ROOT = 144;
  
  public static final int ERROR_DIR_NOT_EMPTY = 145;
  
  public static final int ERROR_IS_SUBST_PATH = 146;
  
  public static final int ERROR_IS_JOIN_PATH = 147;
  
  public static final int ERROR_PATH_BUSY = 148;
  
  public static final int ERROR_IS_SUBST_TARGET = 149;
  
  public static final int ERROR_SYSTEM_TRACE = 150;
  
  public static final int ERROR_INVALID_EVENT_COUNT = 151;
  
  public static final int ERROR_TOO_MANY_MUXWAITERS = 152;
  
  public static final int ERROR_INVALID_LIST_FORMAT = 153;
  
  public static final int ERROR_LABEL_TOO_LONG = 154;
  
  public static final int ERROR_TOO_MANY_TCBS = 155;
  
  public static final int ERROR_SIGNAL_REFUSED = 156;
  
  public static final int ERROR_DISCARDED = 157;
  
  public static final int ERROR_NOT_LOCKED = 158;
  
  public static final int ERROR_BAD_THREADID_ADDR = 159;
  
  public static final int ERROR_BAD_ARGUMENTS = 160;
  
  public static final int ERROR_BAD_PATHNAME = 161;
  
  public static final int ERROR_SIGNAL_PENDING = 162;
  
  public static final int ERROR_MAX_THRDS_REACHED = 164;
  
  public static final int ERROR_LOCK_FAILED = 167;
  
  public static final int ERROR_BUSY = 170;
  
  public static final int ERROR_CANCEL_VIOLATION = 173;
  
  public static final int ERROR_ATOMIC_LOCKS_NOT_SUPPORTED = 174;
  
  public static final int ERROR_INVALID_SEGMENT_NUMBER = 180;
  
  public static final int ERROR_INVALID_ORDINAL = 182;
  
  public static final int ERROR_ALREADY_EXISTS = 183;
  
  public static final int ERROR_INVALID_FLAG_NUMBER = 186;
  
  public static final int ERROR_SEM_NOT_FOUND = 187;
  
  public static final int ERROR_INVALID_STARTING_CODESEG = 188;
  
  public static final int ERROR_INVALID_STACKSEG = 189;
  
  public static final int ERROR_INVALID_MODULETYPE = 190;
  
  public static final int ERROR_INVALID_EXE_SIGNATURE = 191;
  
  public static final int ERROR_EXE_MARKED_INVALID = 192;
  
  public static final int ERROR_BAD_EXE_FORMAT = 193;
  
  public static final int ERROR_ITERATED_DATA_EXCEEDS_64k = 194;
  
  public static final int ERROR_INVALID_MINALLOCSIZE = 195;
  
  public static final int ERROR_DYNLINK_FROM_INVALID_RING = 196;
  
  public static final int ERROR_IOPL_NOT_ENABLED = 197;
  
  public static final int ERROR_INVALID_SEGDPL = 198;
  
  public static final int ERROR_AUTODATASEG_EXCEEDS_64k = 199;
  
  public static final int ERROR_RING2SEG_MUST_BE_MOVABLE = 200;
  
  public static final int ERROR_RELOC_CHAIN_XEEDS_SEGLIM = 201;
  
  public static final int ERROR_INFLOOP_IN_RELOC_CHAIN = 202;
  
  public static final int ERROR_ENVVAR_NOT_FOUND = 203;
  
  public static final int ERROR_NO_SIGNAL_SENT = 205;
  
  public static final int ERROR_FILENAME_EXCED_RANGE = 206;
  
  public static final int ERROR_RING2_STACK_IN_USE = 207;
  
  public static final int ERROR_META_EXPANSION_TOO_LONG = 208;
  
  public static final int ERROR_INVALID_SIGNAL_NUMBER = 209;
  
  public static final int ERROR_THREAD_1_INACTIVE = 210;
  
  public static final int ERROR_LOCKED = 212;
  
  public static final int ERROR_TOO_MANY_MODULES = 214;
  
  public static final int ERROR_NESTING_NOT_ALLOWED = 215;
  
  public static final int ERROR_EXE_MACHINE_TYPE_MISMATCH = 216;
  
  public static final int ERROR_EXE_CANNOT_MODIFY_SIGNED_BINARY = 217;
  
  public static final int ERROR_EXE_CANNOT_MODIFY_STRONG_SIGNED_BINARY = 218;
  
  public static final int ERROR_FILE_CHECKED_OUT = 220;
  
  public static final int ERROR_CHECKOUT_REQUIRED = 221;
  
  public static final int ERROR_BAD_FILE_TYPE = 222;
  
  public static final int ERROR_FILE_TOO_LARGE = 223;
  
  public static final int ERROR_FORMS_AUTH_REQUIRED = 224;
  
  public static final int ERROR_VIRUS_INFECTED = 225;
  
  public static final int ERROR_VIRUS_DELETED = 226;
  
  public static final int ERROR_PIPE_LOCAL = 229;
  
  public static final int ERROR_BAD_PIPE = 230;
  
  public static final int ERROR_PIPE_BUSY = 231;
  
  public static final int ERROR_NO_DATA = 232;
  
  public static final int ERROR_PIPE_NOT_CONNECTED = 233;
  
  public static final int ERROR_MORE_DATA = 234;
  
  public static final int ERROR_VC_DISCONNECTED = 240;
  
  public static final int ERROR_INVALID_EA_NAME = 254;
  
  public static final int ERROR_EA_LIST_INCONSISTENT = 255;
  
  public static final int WAIT_TIMEOUT = 258;
  
  public static final int ERROR_NO_MORE_ITEMS = 259;
  
  public static final int ERROR_CANNOT_COPY = 266;
  
  public static final int ERROR_DIRECTORY = 267;
  
  public static final int ERROR_EAS_DIDNT_FIT = 275;
  
  public static final int ERROR_EA_FILE_CORRUPT = 276;
  
  public static final int ERROR_EA_TABLE_FULL = 277;
  
  public static final int ERROR_INVALID_EA_HANDLE = 278;
  
  public static final int ERROR_EAS_NOT_SUPPORTED = 282;
  
  public static final int ERROR_NOT_OWNER = 288;
  
  public static final int ERROR_TOO_MANY_POSTS = 298;
  
  public static final int ERROR_PARTIAL_COPY = 299;
  
  public static final int ERROR_OPLOCK_NOT_GRANTED = 300;
  
  public static final int ERROR_INVALID_OPLOCK_PROTOCOL = 301;
  
  public static final int ERROR_DISK_TOO_FRAGMENTED = 302;
  
  public static final int ERROR_DELETE_PENDING = 303;
  
  public static final int ERROR_MR_MID_NOT_FOUND = 317;
  
  public static final int ERROR_SCOPE_NOT_FOUND = 318;
  
  public static final int ERROR_FAIL_NOACTION_REBOOT = 350;
  
  public static final int ERROR_FAIL_SHUTDOWN = 351;
  
  public static final int ERROR_FAIL_RESTART = 352;
  
  public static final int ERROR_MAX_SESSIONS_REACHED = 353;
  
  public static final int ERROR_THREAD_MODE_ALREADY_BACKGROUND = 400;
  
  public static final int ERROR_THREAD_MODE_NOT_BACKGROUND = 401;
  
  public static final int ERROR_PROCESS_MODE_ALREADY_BACKGROUND = 402;
  
  public static final int ERROR_PROCESS_MODE_NOT_BACKGROUND = 403;
  
  public static final int ERROR_INVALID_ADDRESS = 487;
  
  public static final int ERROR_USER_PROFILE_LOAD = 500;
  
  public static final int ERROR_ARITHMETIC_OVERFLOW = 534;
  
  public static final int ERROR_PIPE_CONNECTED = 535;
  
  public static final int ERROR_PIPE_LISTENING = 536;
  
  public static final int ERROR_VERIFIER_STOP = 537;
  
  public static final int ERROR_ABIOS_ERROR = 538;
  
  public static final int ERROR_WX86_WARNING = 539;
  
  public static final int ERROR_WX86_ERROR = 540;
  
  public static final int ERROR_TIMER_NOT_CANCELED = 541;
  
  public static final int ERROR_UNWIND = 542;
  
  public static final int ERROR_BAD_STACK = 543;
  
  public static final int ERROR_INVALID_UNWIND_TARGET = 544;
  
  public static final int ERROR_INVALID_PORT_ATTRIBUTES = 545;
  
  public static final int ERROR_PORT_MESSAGE_TOO_LONG = 546;
  
  public static final int ERROR_INVALID_QUOTA_LOWER = 547;
  
  public static final int ERROR_DEVICE_ALREADY_ATTACHED = 548;
  
  public static final int ERROR_INSTRUCTION_MISALIGNMENT = 549;
  
  public static final int ERROR_PROFILING_NOT_STARTED = 550;
  
  public static final int ERROR_PROFILING_NOT_STOPPED = 551;
  
  public static final int ERROR_COULD_NOT_INTERPRET = 552;
  
  public static final int ERROR_PROFILING_AT_LIMIT = 553;
  
  public static final int ERROR_CANT_WAIT = 554;
  
  public static final int ERROR_CANT_TERMINATE_SELF = 555;
  
  public static final int ERROR_UNEXPECTED_MM_CREATE_ERR = 556;
  
  public static final int ERROR_UNEXPECTED_MM_MAP_ERROR = 557;
  
  public static final int ERROR_UNEXPECTED_MM_EXTEND_ERR = 558;
  
  public static final int ERROR_BAD_FUNCTION_TABLE = 559;
  
  public static final int ERROR_NO_GUID_TRANSLATION = 560;
  
  public static final int ERROR_INVALID_LDT_SIZE = 561;
  
  public static final int ERROR_INVALID_LDT_OFFSET = 563;
  
  public static final int ERROR_INVALID_LDT_DESCRIPTOR = 564;
  
  public static final int ERROR_TOO_MANY_THREADS = 565;
  
  public static final int ERROR_THREAD_NOT_IN_PROCESS = 566;
  
  public static final int ERROR_PAGEFILE_QUOTA_EXCEEDED = 567;
  
  public static final int ERROR_LOGON_SERVER_CONFLICT = 568;
  
  public static final int ERROR_SYNCHRONIZATION_REQUIRED = 569;
  
  public static final int ERROR_NET_OPEN_FAILED = 570;
  
  public static final int ERROR_IO_PRIVILEGE_FAILED = 571;
  
  public static final int ERROR_CONTROL_C_EXIT = 572;
  
  public static final int ERROR_MISSING_SYSTEMFILE = 573;
  
  public static final int ERROR_UNHANDLED_EXCEPTION = 574;
  
  public static final int ERROR_APP_INIT_FAILURE = 575;
  
  public static final int ERROR_PAGEFILE_CREATE_FAILED = 576;
  
  public static final int ERROR_INVALID_IMAGE_HASH = 577;
  
  public static final int ERROR_NO_PAGEFILE = 578;
  
  public static final int ERROR_ILLEGAL_FLOAT_CONTEXT = 579;
  
  public static final int ERROR_NO_EVENT_PAIR = 580;
  
  public static final int ERROR_DOMAIN_CTRLR_CONFIG_ERROR = 581;
  
  public static final int ERROR_ILLEGAL_CHARACTER = 582;
  
  public static final int ERROR_UNDEFINED_CHARACTER = 583;
  
  public static final int ERROR_FLOPPY_VOLUME = 584;
  
  public static final int ERROR_BIOS_FAILED_TO_CONNECT_INTERRUPT = 585;
  
  public static final int ERROR_BACKUP_CONTROLLER = 586;
  
  public static final int ERROR_MUTANT_LIMIT_EXCEEDED = 587;
  
  public static final int ERROR_FS_DRIVER_REQUIRED = 588;
  
  public static final int ERROR_CANNOT_LOAD_REGISTRY_FILE = 589;
  
  public static final int ERROR_DEBUG_ATTACH_FAILED = 590;
  
  public static final int ERROR_SYSTEM_PROCESS_TERMINATED = 591;
  
  public static final int ERROR_DATA_NOT_ACCEPTED = 592;
  
  public static final int ERROR_VDM_HARD_ERROR = 593;
  
  public static final int ERROR_DRIVER_CANCEL_TIMEOUT = 594;
  
  public static final int ERROR_REPLY_MESSAGE_MISMATCH = 595;
  
  public static final int ERROR_LOST_WRITEBEHIND_DATA = 596;
  
  public static final int ERROR_CLIENT_SERVER_PARAMETERS_INVALID = 597;
  
  public static final int ERROR_NOT_TINY_STREAM = 598;
  
  public static final int ERROR_STACK_OVERFLOW_READ = 599;
  
  public static final int ERROR_CONVERT_TO_LARGE = 600;
  
  public static final int ERROR_FOUND_OUT_OF_SCOPE = 601;
  
  public static final int ERROR_ALLOCATE_BUCKET = 602;
  
  public static final int ERROR_MARSHALL_OVERFLOW = 603;
  
  public static final int ERROR_INVALID_VARIANT = 604;
  
  public static final int ERROR_BAD_COMPRESSION_BUFFER = 605;
  
  public static final int ERROR_AUDIT_FAILED = 606;
  
  public static final int ERROR_TIMER_RESOLUTION_NOT_SET = 607;
  
  public static final int ERROR_INSUFFICIENT_LOGON_INFO = 608;
  
  public static final int ERROR_BAD_DLL_ENTRYPOINT = 609;
  
  public static final int ERROR_BAD_SERVICE_ENTRYPOINT = 610;
  
  public static final int ERROR_IP_ADDRESS_CONFLICT1 = 611;
  
  public static final int ERROR_IP_ADDRESS_CONFLICT2 = 612;
  
  public static final int ERROR_REGISTRY_QUOTA_LIMIT = 613;
  
  public static final int ERROR_NO_CALLBACK_ACTIVE = 614;
  
  public static final int ERROR_PWD_TOO_SHORT = 615;
  
  public static final int ERROR_PWD_TOO_RECENT = 616;
  
  public static final int ERROR_PWD_HISTORY_CONFLICT = 617;
  
  public static final int ERROR_UNSUPPORTED_COMPRESSION = 618;
  
  public static final int ERROR_INVALID_HW_PROFILE = 619;
  
  public static final int ERROR_INVALID_PLUGPLAY_DEVICE_PATH = 620;
  
  public static final int ERROR_QUOTA_LIST_INCONSISTENT = 621;
  
  public static final int ERROR_EVALUATION_EXPIRATION = 622;
  
  public static final int ERROR_ILLEGAL_DLL_RELOCATION = 623;
  
  public static final int ERROR_DLL_INIT_FAILED_LOGOFF = 624;
  
  public static final int ERROR_VALIDATE_CONTINUE = 625;
  
  public static final int ERROR_NO_MORE_MATCHES = 626;
  
  public static final int ERROR_RANGE_LIST_CONFLICT = 627;
  
  public static final int ERROR_SERVER_SID_MISMATCH = 628;
  
  public static final int ERROR_CANT_ENABLE_DENY_ONLY = 629;
  
  public static final int ERROR_FLOAT_MULTIPLE_FAULTS = 630;
  
  public static final int ERROR_FLOAT_MULTIPLE_TRAPS = 631;
  
  public static final int ERROR_NOINTERFACE = 632;
  
  public static final int ERROR_DRIVER_FAILED_SLEEP = 633;
  
  public static final int ERROR_CORRUPT_SYSTEM_FILE = 634;
  
  public static final int ERROR_COMMITMENT_MINIMUM = 635;
  
  public static final int ERROR_PNP_RESTART_ENUMERATION = 636;
  
  public static final int ERROR_SYSTEM_IMAGE_BAD_SIGNATURE = 637;
  
  public static final int ERROR_PNP_REBOOT_REQUIRED = 638;
  
  public static final int ERROR_INSUFFICIENT_POWER = 639;
  
  public static final int ERROR_MULTIPLE_FAULT_VIOLATION = 640;
  
  public static final int ERROR_SYSTEM_SHUTDOWN = 641;
  
  public static final int ERROR_PORT_NOT_SET = 642;
  
  public static final int ERROR_DS_VERSION_CHECK_FAILURE = 643;
  
  public static final int ERROR_RANGE_NOT_FOUND = 644;
  
  public static final int ERROR_NOT_SAFE_MODE_DRIVER = 646;
  
  public static final int ERROR_FAILED_DRIVER_ENTRY = 647;
  
  public static final int ERROR_DEVICE_ENUMERATION_ERROR = 648;
  
  public static final int ERROR_MOUNT_POINT_NOT_RESOLVED = 649;
  
  public static final int ERROR_INVALID_DEVICE_OBJECT_PARAMETER = 650;
  
  public static final int ERROR_MCA_OCCURED = 651;
  
  public static final int ERROR_DRIVER_DATABASE_ERROR = 652;
  
  public static final int ERROR_SYSTEM_HIVE_TOO_LARGE = 653;
  
  public static final int ERROR_DRIVER_FAILED_PRIOR_UNLOAD = 654;
  
  public static final int ERROR_VOLSNAP_PREPARE_HIBERNATE = 655;
  
  public static final int ERROR_HIBERNATION_FAILURE = 656;
  
  public static final int ERROR_FILE_SYSTEM_LIMITATION = 665;
  
  public static final int ERROR_ASSERTION_FAILURE = 668;
  
  public static final int ERROR_ACPI_ERROR = 669;
  
  public static final int ERROR_WOW_ASSERTION = 670;
  
  public static final int ERROR_PNP_BAD_MPS_TABLE = 671;
  
  public static final int ERROR_PNP_TRANSLATION_FAILED = 672;
  
  public static final int ERROR_PNP_IRQ_TRANSLATION_FAILED = 673;
  
  public static final int ERROR_PNP_INVALID_ID = 674;
  
  public static final int ERROR_WAKE_SYSTEM_DEBUGGER = 675;
  
  public static final int ERROR_HANDLES_CLOSED = 676;
  
  public static final int ERROR_EXTRANEOUS_INFORMATION = 677;
  
  public static final int ERROR_RXACT_COMMIT_NECESSARY = 678;
  
  public static final int ERROR_MEDIA_CHECK = 679;
  
  public static final int ERROR_GUID_SUBSTITUTION_MADE = 680;
  
  public static final int ERROR_STOPPED_ON_SYMLINK = 681;
  
  public static final int ERROR_LONGJUMP = 682;
  
  public static final int ERROR_PLUGPLAY_QUERY_VETOED = 683;
  
  public static final int ERROR_UNWIND_CONSOLIDATE = 684;
  
  public static final int ERROR_REGISTRY_HIVE_RECOVERED = 685;
  
  public static final int ERROR_DLL_MIGHT_BE_INSECURE = 686;
  
  public static final int ERROR_DLL_MIGHT_BE_INCOMPATIBLE = 687;
  
  public static final int ERROR_DBG_EXCEPTION_NOT_HANDLED = 688;
  
  public static final int ERROR_DBG_REPLY_LATER = 689;
  
  public static final int ERROR_DBG_UNABLE_TO_PROVIDE_HANDLE = 690;
  
  public static final int ERROR_DBG_TERMINATE_THREAD = 691;
  
  public static final int ERROR_DBG_TERMINATE_PROCESS = 692;
  
  public static final int ERROR_DBG_CONTROL_C = 693;
  
  public static final int ERROR_DBG_PRINTEXCEPTION_C = 694;
  
  public static final int ERROR_DBG_RIPEXCEPTION = 695;
  
  public static final int ERROR_DBG_CONTROL_BREAK = 696;
  
  public static final int ERROR_DBG_COMMAND_EXCEPTION = 697;
  
  public static final int ERROR_OBJECT_NAME_EXISTS = 698;
  
  public static final int ERROR_THREAD_WAS_SUSPENDED = 699;
  
  public static final int ERROR_IMAGE_NOT_AT_BASE = 700;
  
  public static final int ERROR_RXACT_STATE_CREATED = 701;
  
  public static final int ERROR_SEGMENT_NOTIFICATION = 702;
  
  public static final int ERROR_BAD_CURRENT_DIRECTORY = 703;
  
  public static final int ERROR_FT_READ_RECOVERY_FROM_BACKUP = 704;
  
  public static final int ERROR_FT_WRITE_RECOVERY = 705;
  
  public static final int ERROR_IMAGE_MACHINE_TYPE_MISMATCH = 706;
  
  public static final int ERROR_RECEIVE_PARTIAL = 707;
  
  public static final int ERROR_RECEIVE_EXPEDITED = 708;
  
  public static final int ERROR_RECEIVE_PARTIAL_EXPEDITED = 709;
  
  public static final int ERROR_EVENT_DONE = 710;
  
  public static final int ERROR_EVENT_PENDING = 711;
  
  public static final int ERROR_CHECKING_FILE_SYSTEM = 712;
  
  public static final int ERROR_FATAL_APP_EXIT = 713;
  
  public static final int ERROR_PREDEFINED_HANDLE = 714;
  
  public static final int ERROR_WAS_UNLOCKED = 715;
  
  public static final int ERROR_SERVICE_NOTIFICATION = 716;
  
  public static final int ERROR_WAS_LOCKED = 717;
  
  public static final int ERROR_LOG_HARD_ERROR = 718;
  
  public static final int ERROR_ALREADY_WIN32 = 719;
  
  public static final int ERROR_IMAGE_MACHINE_TYPE_MISMATCH_EXE = 720;
  
  public static final int ERROR_NO_YIELD_PERFORMED = 721;
  
  public static final int ERROR_TIMER_RESUME_IGNORED = 722;
  
  public static final int ERROR_ARBITRATION_UNHANDLED = 723;
  
  public static final int ERROR_CARDBUS_NOT_SUPPORTED = 724;
  
  public static final int ERROR_MP_PROCESSOR_MISMATCH = 725;
  
  public static final int ERROR_HIBERNATED = 726;
  
  public static final int ERROR_RESUME_HIBERNATION = 727;
  
  public static final int ERROR_FIRMWARE_UPDATED = 728;
  
  public static final int ERROR_DRIVERS_LEAKING_LOCKED_PAGES = 729;
  
  public static final int ERROR_WAKE_SYSTEM = 730;
  
  public static final int ERROR_WAIT_1 = 731;
  
  public static final int ERROR_WAIT_2 = 732;
  
  public static final int ERROR_WAIT_3 = 733;
  
  public static final int ERROR_WAIT_63 = 734;
  
  public static final int ERROR_ABANDONED_WAIT_0 = 735;
  
  public static final int ERROR_ABANDONED_WAIT_63 = 736;
  
  public static final int ERROR_USER_APC = 737;
  
  public static final int ERROR_KERNEL_APC = 738;
  
  public static final int ERROR_ALERTED = 739;
  
  public static final int ERROR_ELEVATION_REQUIRED = 740;
  
  public static final int ERROR_REPARSE = 741;
  
  public static final int ERROR_OPLOCK_BREAK_IN_PROGRESS = 742;
  
  public static final int ERROR_VOLUME_MOUNTED = 743;
  
  public static final int ERROR_RXACT_COMMITTED = 744;
  
  public static final int ERROR_NOTIFY_CLEANUP = 745;
  
  public static final int ERROR_PRIMARY_TRANSPORT_CONNECT_FAILED = 746;
  
  public static final int ERROR_PAGE_FAULT_TRANSITION = 747;
  
  public static final int ERROR_PAGE_FAULT_DEMAND_ZERO = 748;
  
  public static final int ERROR_PAGE_FAULT_COPY_ON_WRITE = 749;
  
  public static final int ERROR_PAGE_FAULT_GUARD_PAGE = 750;
  
  public static final int ERROR_PAGE_FAULT_PAGING_FILE = 751;
  
  public static final int ERROR_CACHE_PAGE_LOCKED = 752;
  
  public static final int ERROR_CRASH_DUMP = 753;
  
  public static final int ERROR_BUFFER_ALL_ZEROS = 754;
  
  public static final int ERROR_REPARSE_OBJECT = 755;
  
  public static final int ERROR_RESOURCE_REQUIREMENTS_CHANGED = 756;
  
  public static final int ERROR_TRANSLATION_COMPLETE = 757;
  
  public static final int ERROR_NOTHING_TO_TERMINATE = 758;
  
  public static final int ERROR_PROCESS_NOT_IN_JOB = 759;
  
  public static final int ERROR_PROCESS_IN_JOB = 760;
  
  public static final int ERROR_VOLSNAP_HIBERNATE_READY = 761;
  
  public static final int ERROR_FSFILTER_OP_COMPLETED_SUCCESSFULLY = 762;
  
  public static final int ERROR_INTERRUPT_VECTOR_ALREADY_CONNECTED = 763;
  
  public static final int ERROR_INTERRUPT_STILL_CONNECTED = 764;
  
  public static final int ERROR_WAIT_FOR_OPLOCK = 765;
  
  public static final int ERROR_DBG_EXCEPTION_HANDLED = 766;
  
  public static final int ERROR_DBG_CONTINUE = 767;
  
  public static final int ERROR_CALLBACK_POP_STACK = 768;
  
  public static final int ERROR_COMPRESSION_DISABLED = 769;
  
  public static final int ERROR_CANTFETCHBACKWARDS = 770;
  
  public static final int ERROR_CANTSCROLLBACKWARDS = 771;
  
  public static final int ERROR_ROWSNOTRELEASED = 772;
  
  public static final int ERROR_BAD_ACCESSOR_FLAGS = 773;
  
  public static final int ERROR_ERRORS_ENCOUNTERED = 774;
  
  public static final int ERROR_NOT_CAPABLE = 775;
  
  public static final int ERROR_REQUEST_OUT_OF_SEQUENCE = 776;
  
  public static final int ERROR_VERSION_PARSE_ERROR = 777;
  
  public static final int ERROR_BADSTARTPOSITION = 778;
  
  public static final int ERROR_MEMORY_HARDWARE = 779;
  
  public static final int ERROR_DISK_REPAIR_DISABLED = 780;
  
  public static final int ERROR_INSUFFICIENT_RESOURCE_FOR_SPECIFIED_SHARED_SECTION_SIZE = 781;
  
  public static final int ERROR_SYSTEM_POWERSTATE_TRANSITION = 782;
  
  public static final int ERROR_SYSTEM_POWERSTATE_COMPLEX_TRANSITION = 783;
  
  public static final int ERROR_MCA_EXCEPTION = 784;
  
  public static final int ERROR_ACCESS_AUDIT_BY_POLICY = 785;
  
  public static final int ERROR_ACCESS_DISABLED_NO_SAFER_UI_BY_POLICY = 786;
  
  public static final int ERROR_ABANDON_HIBERFILE = 787;
  
  public static final int ERROR_LOST_WRITEBEHIND_DATA_NETWORK_DISCONNECTED = 788;
  
  public static final int ERROR_LOST_WRITEBEHIND_DATA_NETWORK_SERVER_ERROR = 789;
  
  public static final int ERROR_LOST_WRITEBEHIND_DATA_LOCAL_DISK_ERROR = 790;
  
  public static final int ERROR_BAD_MCFG_TABLE = 791;
  
  public static final int ERROR_EA_ACCESS_DENIED = 994;
  
  public static final int ERROR_OPERATION_ABORTED = 995;
  
  public static final int ERROR_IO_INCOMPLETE = 996;
  
  public static final int ERROR_IO_PENDING = 997;
  
  public static final int ERROR_NOACCESS = 998;
  
  public static final int ERROR_SWAPERROR = 999;
  
  public static final int ERROR_STACK_OVERFLOW = 1001;
  
  public static final int ERROR_INVALID_MESSAGE = 1002;
  
  public static final int ERROR_CAN_NOT_COMPLETE = 1003;
  
  public static final int ERROR_INVALID_FLAGS = 1004;
  
  public static final int ERROR_UNRECOGNIZED_VOLUME = 1005;
  
  public static final int ERROR_FILE_INVALID = 1006;
  
  public static final int ERROR_FULLSCREEN_MODE = 1007;
  
  public static final int ERROR_NO_TOKEN = 1008;
  
  public static final int ERROR_BADDB = 1009;
  
  public static final int ERROR_BADKEY = 1010;
  
  public static final int ERROR_CANTOPEN = 1011;
  
  public static final int ERROR_CANTREAD = 1012;
  
  public static final int ERROR_CANTWRITE = 1013;
  
  public static final int ERROR_REGISTRY_RECOVERED = 1014;
  
  public static final int ERROR_REGISTRY_CORRUPT = 1015;
  
  public static final int ERROR_REGISTRY_IO_FAILED = 1016;
  
  public static final int ERROR_NOT_REGISTRY_FILE = 1017;
  
  public static final int ERROR_KEY_DELETED = 1018;
  
  public static final int ERROR_NO_LOG_SPACE = 1019;
  
  public static final int ERROR_KEY_HAS_CHILDREN = 1020;
  
  public static final int ERROR_CHILD_MUST_BE_VOLATILE = 1021;
  
  public static final int ERROR_NOTIFY_ENUM_DIR = 1022;
  
  public static final int ERROR_DEPENDENT_SERVICES_RUNNING = 1051;
  
  public static final int ERROR_INVALID_SERVICE_CONTROL = 1052;
  
  public static final int ERROR_SERVICE_REQUEST_TIMEOUT = 1053;
  
  public static final int ERROR_SERVICE_NO_THREAD = 1054;
  
  public static final int ERROR_SERVICE_DATABASE_LOCKED = 1055;
  
  public static final int ERROR_SERVICE_ALREADY_RUNNING = 1056;
  
  public static final int ERROR_INVALID_SERVICE_ACCOUNT = 1057;
  
  public static final int ERROR_SERVICE_DISABLED = 1058;
  
  public static final int ERROR_CIRCULAR_DEPENDENCY = 1059;
  
  public static final int ERROR_SERVICE_DOES_NOT_EXIST = 1060;
  
  public static final int ERROR_SERVICE_CANNOT_ACCEPT_CTRL = 1061;
  
  public static final int ERROR_SERVICE_NOT_ACTIVE = 1062;
  
  public static final int ERROR_FAILED_SERVICE_CONTROLLER_CONNECT = 1063;
  
  public static final int ERROR_EXCEPTION_IN_SERVICE = 1064;
  
  public static final int ERROR_DATABASE_DOES_NOT_EXIST = 1065;
  
  public static final int ERROR_SERVICE_SPECIFIC_ERROR = 1066;
  
  public static final int ERROR_PROCESS_ABORTED = 1067;
  
  public static final int ERROR_SERVICE_DEPENDENCY_FAIL = 1068;
  
  public static final int ERROR_SERVICE_LOGON_FAILED = 1069;
  
  public static final int ERROR_SERVICE_START_HANG = 1070;
  
  public static final int ERROR_INVALID_SERVICE_LOCK = 1071;
  
  public static final int ERROR_SERVICE_MARKED_FOR_DELETE = 1072;
  
  public static final int ERROR_SERVICE_EXISTS = 1073;
  
  public static final int ERROR_ALREADY_RUNNING_LKG = 1074;
  
  public static final int ERROR_SERVICE_DEPENDENCY_DELETED = 1075;
  
  public static final int ERROR_BOOT_ALREADY_ACCEPTED = 1076;
  
  public static final int ERROR_SERVICE_NEVER_STARTED = 1077;
  
  public static final int ERROR_DUPLICATE_SERVICE_NAME = 1078;
  
  public static final int ERROR_DIFFERENT_SERVICE_ACCOUNT = 1079;
  
  public static final int ERROR_CANNOT_DETECT_DRIVER_FAILURE = 1080;
  
  public static final int ERROR_CANNOT_DETECT_PROCESS_ABORT = 1081;
  
  public static final int ERROR_NO_RECOVERY_PROGRAM = 1082;
  
  public static final int ERROR_SERVICE_NOT_IN_EXE = 1083;
  
  public static final int ERROR_NOT_SAFEBOOT_SERVICE = 1084;
  
  public static final int ERROR_END_OF_MEDIA = 1100;
  
  public static final int ERROR_FILEMARK_DETECTED = 1101;
  
  public static final int ERROR_BEGINNING_OF_MEDIA = 1102;
  
  public static final int ERROR_SETMARK_DETECTED = 1103;
  
  public static final int ERROR_NO_DATA_DETECTED = 1104;
  
  public static final int ERROR_PARTITION_FAILURE = 1105;
  
  public static final int ERROR_INVALID_BLOCK_LENGTH = 1106;
  
  public static final int ERROR_DEVICE_NOT_PARTITIONED = 1107;
  
  public static final int ERROR_UNABLE_TO_LOCK_MEDIA = 1108;
  
  public static final int ERROR_UNABLE_TO_UNLOAD_MEDIA = 1109;
  
  public static final int ERROR_MEDIA_CHANGED = 1110;
  
  public static final int ERROR_BUS_RESET = 1111;
  
  public static final int ERROR_NO_MEDIA_IN_DRIVE = 1112;
  
  public static final int ERROR_NO_UNICODE_TRANSLATION = 1113;
  
  public static final int ERROR_DLL_INIT_FAILED = 1114;
  
  public static final int ERROR_SHUTDOWN_IN_PROGRESS = 1115;
  
  public static final int ERROR_NO_SHUTDOWN_IN_PROGRESS = 1116;
  
  public static final int ERROR_IO_DEVICE = 1117;
  
  public static final int ERROR_SERIAL_NO_DEVICE = 1118;
  
  public static final int ERROR_IRQ_BUSY = 1119;
  
  public static final int ERROR_MORE_WRITES = 1120;
  
  public static final int ERROR_COUNTER_TIMEOUT = 1121;
  
  public static final int ERROR_FLOPPY_ID_MARK_NOT_FOUND = 1122;
  
  public static final int ERROR_FLOPPY_WRONG_CYLINDER = 1123;
  
  public static final int ERROR_FLOPPY_UNKNOWN_ERROR = 1124;
  
  public static final int ERROR_FLOPPY_BAD_REGISTERS = 1125;
  
  public static final int ERROR_DISK_RECALIBRATE_FAILED = 1126;
  
  public static final int ERROR_DISK_OPERATION_FAILED = 1127;
  
  public static final int ERROR_DISK_RESET_FAILED = 1128;
  
  public static final int ERROR_EOM_OVERFLOW = 1129;
  
  public static final int ERROR_NOT_ENOUGH_SERVER_MEMORY = 1130;
  
  public static final int ERROR_POSSIBLE_DEADLOCK = 1131;
  
  public static final int ERROR_MAPPED_ALIGNMENT = 1132;
  
  public static final int ERROR_SET_POWER_STATE_VETOED = 1140;
  
  public static final int ERROR_SET_POWER_STATE_FAILED = 1141;
  
  public static final int ERROR_TOO_MANY_LINKS = 1142;
  
  public static final int ERROR_OLD_WIN_VERSION = 1150;
  
  public static final int ERROR_APP_WRONG_OS = 1151;
  
  public static final int ERROR_SINGLE_INSTANCE_APP = 1152;
  
  public static final int ERROR_RMODE_APP = 1153;
  
  public static final int ERROR_INVALID_DLL = 1154;
  
  public static final int ERROR_NO_ASSOCIATION = 1155;
  
  public static final int ERROR_DDE_FAIL = 1156;
  
  public static final int ERROR_DLL_NOT_FOUND = 1157;
  
  public static final int ERROR_NO_MORE_USER_HANDLES = 1158;
  
  public static final int ERROR_MESSAGE_SYNC_ONLY = 1159;
  
  public static final int ERROR_SOURCE_ELEMENT_EMPTY = 1160;
  
  public static final int ERROR_DESTINATION_ELEMENT_FULL = 1161;
  
  public static final int ERROR_ILLEGAL_ELEMENT_ADDRESS = 1162;
  
  public static final int ERROR_MAGAZINE_NOT_PRESENT = 1163;
  
  public static final int ERROR_DEVICE_REINITIALIZATION_NEEDED = 1164;
  
  public static final int ERROR_DEVICE_REQUIRES_CLEANING = 1165;
  
  public static final int ERROR_DEVICE_DOOR_OPEN = 1166;
  
  public static final int ERROR_DEVICE_NOT_CONNECTED = 1167;
  
  public static final int ERROR_NOT_FOUND = 1168;
  
  public static final int ERROR_NO_MATCH = 1169;
  
  public static final int ERROR_SET_NOT_FOUND = 1170;
  
  public static final int ERROR_POINT_NOT_FOUND = 1171;
  
  public static final int ERROR_NO_TRACKING_SERVICE = 1172;
  
  public static final int ERROR_NO_VOLUME_ID = 1173;
  
  public static final int ERROR_UNABLE_TO_REMOVE_REPLACED = 1175;
  
  public static final int ERROR_UNABLE_TO_MOVE_REPLACEMENT = 1176;
  
  public static final int ERROR_UNABLE_TO_MOVE_REPLACEMENT_2 = 1177;
  
  public static final int ERROR_JOURNAL_DELETE_IN_PROGRESS = 1178;
  
  public static final int ERROR_JOURNAL_NOT_ACTIVE = 1179;
  
  public static final int ERROR_POTENTIAL_FILE_FOUND = 1180;
  
  public static final int ERROR_JOURNAL_ENTRY_DELETED = 1181;
  
  public static final int ERROR_SHUTDOWN_IS_SCHEDULED = 1190;
  
  public static final int ERROR_SHUTDOWN_USERS_LOGGED_ON = 1191;
  
  public static final int ERROR_BAD_DEVICE = 1200;
  
  public static final int ERROR_CONNECTION_UNAVAIL = 1201;
  
  public static final int ERROR_DEVICE_ALREADY_REMEMBERED = 1202;
  
  public static final int ERROR_NO_NET_OR_BAD_PATH = 1203;
  
  public static final int ERROR_BAD_PROVIDER = 1204;
  
  public static final int ERROR_CANNOT_OPEN_PROFILE = 1205;
  
  public static final int ERROR_BAD_PROFILE = 1206;
  
  public static final int ERROR_NOT_CONTAINER = 1207;
  
  public static final int ERROR_EXTENDED_ERROR = 1208;
  
  public static final int ERROR_INVALID_GROUPNAME = 1209;
  
  public static final int ERROR_INVALID_COMPUTERNAME = 1210;
  
  public static final int ERROR_INVALID_EVENTNAME = 1211;
  
  public static final int ERROR_INVALID_DOMAINNAME = 1212;
  
  public static final int ERROR_INVALID_SERVICENAME = 1213;
  
  public static final int ERROR_INVALID_NETNAME = 1214;
  
  public static final int ERROR_INVALID_SHARENAME = 1215;
  
  public static final int ERROR_INVALID_PASSWORDNAME = 1216;
  
  public static final int ERROR_INVALID_MESSAGENAME = 1217;
  
  public static final int ERROR_INVALID_MESSAGEDEST = 1218;
  
  public static final int ERROR_SESSION_CREDENTIAL_CONFLICT = 1219;
  
  public static final int ERROR_REMOTE_SESSION_LIMIT_EXCEEDED = 1220;
  
  public static final int ERROR_DUP_DOMAINNAME = 1221;
  
  public static final int ERROR_NO_NETWORK = 1222;
  
  public static final int ERROR_CANCELLED = 1223;
  
  public static final int ERROR_USER_MAPPED_FILE = 1224;
  
  public static final int ERROR_CONNECTION_REFUSED = 1225;
  
  public static final int ERROR_GRACEFUL_DISCONNECT = 1226;
  
  public static final int ERROR_ADDRESS_ALREADY_ASSOCIATED = 1227;
  
  public static final int ERROR_ADDRESS_NOT_ASSOCIATED = 1228;
  
  public static final int ERROR_CONNECTION_INVALID = 1229;
  
  public static final int ERROR_CONNECTION_ACTIVE = 1230;
  
  public static final int ERROR_NETWORK_UNREACHABLE = 1231;
  
  public static final int ERROR_HOST_UNREACHABLE = 1232;
  
  public static final int ERROR_PROTOCOL_UNREACHABLE = 1233;
  
  public static final int ERROR_PORT_UNREACHABLE = 1234;
  
  public static final int ERROR_REQUEST_ABORTED = 1235;
  
  public static final int ERROR_CONNECTION_ABORTED = 1236;
  
  public static final int ERROR_RETRY = 1237;
  
  public static final int ERROR_CONNECTION_COUNT_LIMIT = 1238;
  
  public static final int ERROR_LOGIN_TIME_RESTRICTION = 1239;
  
  public static final int ERROR_LOGIN_WKSTA_RESTRICTION = 1240;
  
  public static final int ERROR_INCORRECT_ADDRESS = 1241;
  
  public static final int ERROR_ALREADY_REGISTERED = 1242;
  
  public static final int ERROR_SERVICE_NOT_FOUND = 1243;
  
  public static final int ERROR_NOT_AUTHENTICATED = 1244;
  
  public static final int ERROR_NOT_LOGGED_ON = 1245;
  
  public static final int ERROR_CONTINUE = 1246;
  
  public static final int ERROR_ALREADY_INITIALIZED = 1247;
  
  public static final int ERROR_NO_MORE_DEVICES = 1248;
  
  public static final int ERROR_NO_SUCH_SITE = 1249;
  
  public static final int ERROR_DOMAIN_CONTROLLER_EXISTS = 1250;
  
  public static final int ERROR_ONLY_IF_CONNECTED = 1251;
  
  public static final int ERROR_OVERRIDE_NOCHANGES = 1252;
  
  public static final int ERROR_BAD_USER_PROFILE = 1253;
  
  public static final int ERROR_NOT_SUPPORTED_ON_SBS = 1254;
  
  public static final int ERROR_SERVER_SHUTDOWN_IN_PROGRESS = 1255;
  
  public static final int ERROR_HOST_DOWN = 1256;
  
  public static final int ERROR_NON_ACCOUNT_SID = 1257;
  
  public static final int ERROR_NON_DOMAIN_SID = 1258;
  
  public static final int ERROR_APPHELP_BLOCK = 1259;
  
  public static final int ERROR_ACCESS_DISABLED_BY_POLICY = 1260;
  
  public static final int ERROR_REG_NAT_CONSUMPTION = 1261;
  
  public static final int ERROR_CSCSHARE_OFFLINE = 1262;
  
  public static final int ERROR_PKINIT_FAILURE = 1263;
  
  public static final int ERROR_SMARTCARD_SUBSYSTEM_FAILURE = 1264;
  
  public static final int ERROR_DOWNGRADE_DETECTED = 1265;
  
  public static final int ERROR_MACHINE_LOCKED = 1271;
  
  public static final int ERROR_CALLBACK_SUPPLIED_INVALID_DATA = 1273;
  
  public static final int ERROR_SYNC_FOREGROUND_REFRESH_REQUIRED = 1274;
  
  public static final int ERROR_DRIVER_BLOCKED = 1275;
  
  public static final int ERROR_INVALID_IMPORT_OF_NON_DLL = 1276;
  
  public static final int ERROR_ACCESS_DISABLED_WEBBLADE = 1277;
  
  public static final int ERROR_ACCESS_DISABLED_WEBBLADE_TAMPER = 1278;
  
  public static final int ERROR_RECOVERY_FAILURE = 1279;
  
  public static final int ERROR_ALREADY_FIBER = 1280;
  
  public static final int ERROR_ALREADY_THREAD = 1281;
  
  public static final int ERROR_STACK_BUFFER_OVERRUN = 1282;
  
  public static final int ERROR_PARAMETER_QUOTA_EXCEEDED = 1283;
  
  public static final int ERROR_DEBUGGER_INACTIVE = 1284;
  
  public static final int ERROR_DELAY_LOAD_FAILED = 1285;
  
  public static final int ERROR_VDM_DISALLOWED = 1286;
  
  public static final int ERROR_UNIDENTIFIED_ERROR = 1287;
  
  public static final int ERROR_INVALID_CRUNTIME_PARAMETER = 1288;
  
  public static final int ERROR_BEYOND_VDL = 1289;
  
  public static final int ERROR_INCOMPATIBLE_SERVICE_SID_TYPE = 1290;
  
  public static final int ERROR_DRIVER_PROCESS_TERMINATED = 1291;
  
  public static final int ERROR_IMPLEMENTATION_LIMIT = 1292;
  
  public static final int ERROR_PROCESS_IS_PROTECTED = 1293;
  
  public static final int ERROR_SERVICE_NOTIFY_CLIENT_LAGGING = 1294;
  
  public static final int ERROR_DISK_QUOTA_EXCEEDED = 1295;
  
  public static final int ERROR_CONTENT_BLOCKED = 1296;
  
  public static final int ERROR_INCOMPATIBLE_SERVICE_PRIVILEGE = 1297;
  
  public static final int ERROR_INVALID_LABEL = 1299;
  
  public static final int ERROR_NOT_ALL_ASSIGNED = 1300;
  
  public static final int ERROR_SOME_NOT_MAPPED = 1301;
  
  public static final int ERROR_NO_QUOTAS_FOR_ACCOUNT = 1302;
  
  public static final int ERROR_LOCAL_USER_SESSION_KEY = 1303;
  
  public static final int ERROR_NULL_LM_PASSWORD = 1304;
  
  public static final int ERROR_UNKNOWN_REVISION = 1305;
  
  public static final int ERROR_REVISION_MISMATCH = 1306;
  
  public static final int ERROR_INVALID_OWNER = 1307;
  
  public static final int ERROR_INVALID_PRIMARY_GROUP = 1308;
  
  public static final int ERROR_NO_IMPERSONATION_TOKEN = 1309;
  
  public static final int ERROR_CANT_DISABLE_MANDATORY = 1310;
  
  public static final int ERROR_NO_LOGON_SERVERS = 1311;
  
  public static final int ERROR_NO_SUCH_LOGON_SESSION = 1312;
  
  public static final int ERROR_NO_SUCH_PRIVILEGE = 1313;
  
  public static final int ERROR_PRIVILEGE_NOT_HELD = 1314;
  
  public static final int ERROR_INVALID_ACCOUNT_NAME = 1315;
  
  public static final int ERROR_USER_EXISTS = 1316;
  
  public static final int ERROR_NO_SUCH_USER = 1317;
  
  public static final int ERROR_GROUP_EXISTS = 1318;
  
  public static final int ERROR_NO_SUCH_GROUP = 1319;
  
  public static final int ERROR_MEMBER_IN_GROUP = 1320;
  
  public static final int ERROR_MEMBER_NOT_IN_GROUP = 1321;
  
  public static final int ERROR_LAST_ADMIN = 1322;
  
  public static final int ERROR_WRONG_PASSWORD = 1323;
  
  public static final int ERROR_ILL_FORMED_PASSWORD = 1324;
  
  public static final int ERROR_PASSWORD_RESTRICTION = 1325;
  
  public static final int ERROR_LOGON_FAILURE = 1326;
  
  public static final int ERROR_ACCOUNT_RESTRICTION = 1327;
  
  public static final int ERROR_INVALID_LOGON_HOURS = 1328;
  
  public static final int ERROR_INVALID_WORKSTATION = 1329;
  
  public static final int ERROR_PASSWORD_EXPIRED = 1330;
  
  public static final int ERROR_ACCOUNT_DISABLED = 1331;
  
  public static final int ERROR_NONE_MAPPED = 1332;
  
  public static final int ERROR_TOO_MANY_LUIDS_REQUESTED = 1333;
  
  public static final int ERROR_LUIDS_EXHAUSTED = 1334;
  
  public static final int ERROR_INVALID_SUB_AUTHORITY = 1335;
  
  public static final int ERROR_INVALID_ACL = 1336;
  
  public static final int ERROR_INVALID_SID = 1337;
  
  public static final int ERROR_INVALID_SECURITY_DESCR = 1338;
  
  public static final int ERROR_BAD_INHERITANCE_ACL = 1340;
  
  public static final int ERROR_SERVER_DISABLED = 1341;
  
  public static final int ERROR_SERVER_NOT_DISABLED = 1342;
  
  public static final int ERROR_INVALID_ID_AUTHORITY = 1343;
  
  public static final int ERROR_ALLOTTED_SPACE_EXCEEDED = 1344;
  
  public static final int ERROR_INVALID_GROUP_ATTRIBUTES = 1345;
  
  public static final int ERROR_BAD_IMPERSONATION_LEVEL = 1346;
  
  public static final int ERROR_CANT_OPEN_ANONYMOUS = 1347;
  
  public static final int ERROR_BAD_VALIDATION_CLASS = 1348;
  
  public static final int ERROR_BAD_TOKEN_TYPE = 1349;
  
  public static final int ERROR_NO_SECURITY_ON_OBJECT = 1350;
  
  public static final int ERROR_CANT_ACCESS_DOMAIN_INFO = 1351;
  
  public static final int ERROR_INVALID_SERVER_STATE = 1352;
  
  public static final int ERROR_INVALID_DOMAIN_STATE = 1353;
  
  public static final int ERROR_INVALID_DOMAIN_ROLE = 1354;
  
  public static final int ERROR_NO_SUCH_DOMAIN = 1355;
  
  public static final int ERROR_DOMAIN_EXISTS = 1356;
  
  public static final int ERROR_DOMAIN_LIMIT_EXCEEDED = 1357;
  
  public static final int ERROR_INTERNAL_DB_CORRUPTION = 1358;
  
  public static final int ERROR_INTERNAL_ERROR = 1359;
  
  public static final int ERROR_GENERIC_NOT_MAPPED = 1360;
  
  public static final int ERROR_BAD_DESCRIPTOR_FORMAT = 1361;
  
  public static final int ERROR_NOT_LOGON_PROCESS = 1362;
  
  public static final int ERROR_LOGON_SESSION_EXISTS = 1363;
  
  public static final int ERROR_NO_SUCH_PACKAGE = 1364;
  
  public static final int ERROR_BAD_LOGON_SESSION_STATE = 1365;
  
  public static final int ERROR_LOGON_SESSION_COLLISION = 1366;
  
  public static final int ERROR_INVALID_LOGON_TYPE = 1367;
  
  public static final int ERROR_CANNOT_IMPERSONATE = 1368;
  
  public static final int ERROR_RXACT_INVALID_STATE = 1369;
  
  public static final int ERROR_RXACT_COMMIT_FAILURE = 1370;
  
  public static final int ERROR_SPECIAL_ACCOUNT = 1371;
  
  public static final int ERROR_SPECIAL_GROUP = 1372;
  
  public static final int ERROR_SPECIAL_USER = 1373;
  
  public static final int ERROR_MEMBERS_PRIMARY_GROUP = 1374;
  
  public static final int ERROR_TOKEN_ALREADY_IN_USE = 1375;
  
  public static final int ERROR_NO_SUCH_ALIAS = 1376;
  
  public static final int ERROR_MEMBER_NOT_IN_ALIAS = 1377;
  
  public static final int ERROR_MEMBER_IN_ALIAS = 1378;
  
  public static final int ERROR_ALIAS_EXISTS = 1379;
  
  public static final int ERROR_LOGON_NOT_GRANTED = 1380;
  
  public static final int ERROR_TOO_MANY_SECRETS = 1381;
  
  public static final int ERROR_SECRET_TOO_LONG = 1382;
  
  public static final int ERROR_INTERNAL_DB_ERROR = 1383;
  
  public static final int ERROR_TOO_MANY_CONTEXT_IDS = 1384;
  
  public static final int ERROR_LOGON_TYPE_NOT_GRANTED = 1385;
  
  public static final int ERROR_NT_CROSS_ENCRYPTION_REQUIRED = 1386;
  
  public static final int ERROR_NO_SUCH_MEMBER = 1387;
  
  public static final int ERROR_INVALID_MEMBER = 1388;
  
  public static final int ERROR_TOO_MANY_SIDS = 1389;
  
  public static final int ERROR_LM_CROSS_ENCRYPTION_REQUIRED = 1390;
  
  public static final int ERROR_NO_INHERITANCE = 1391;
  
  public static final int ERROR_FILE_CORRUPT = 1392;
  
  public static final int ERROR_DISK_CORRUPT = 1393;
  
  public static final int ERROR_NO_USER_SESSION_KEY = 1394;
  
  public static final int ERROR_LICENSE_QUOTA_EXCEEDED = 1395;
  
  public static final int ERROR_WRONG_TARGET_NAME = 1396;
  
  public static final int ERROR_MUTUAL_AUTH_FAILED = 1397;
  
  public static final int ERROR_TIME_SKEW = 1398;
  
  public static final int ERROR_CURRENT_DOMAIN_NOT_ALLOWED = 1399;
  
  public static final int ERROR_INVALID_WINDOW_HANDLE = 1400;
  
  public static final int ERROR_INVALID_MENU_HANDLE = 1401;
  
  public static final int ERROR_INVALID_CURSOR_HANDLE = 1402;
  
  public static final int ERROR_INVALID_ACCEL_HANDLE = 1403;
  
  public static final int ERROR_INVALID_HOOK_HANDLE = 1404;
  
  public static final int ERROR_INVALID_DWP_HANDLE = 1405;
  
  public static final int ERROR_TLW_WITH_WSCHILD = 1406;
  
  public static final int ERROR_CANNOT_FIND_WND_CLASS = 1407;
  
  public static final int ERROR_WINDOW_OF_OTHER_THREAD = 1408;
  
  public static final int ERROR_HOTKEY_ALREADY_REGISTERED = 1409;
  
  public static final int ERROR_CLASS_ALREADY_EXISTS = 1410;
  
  public static final int ERROR_CLASS_DOES_NOT_EXIST = 1411;
  
  public static final int ERROR_CLASS_HAS_WINDOWS = 1412;
  
  public static final int ERROR_INVALID_INDEX = 1413;
  
  public static final int ERROR_INVALID_ICON_HANDLE = 1414;
  
  public static final int ERROR_PRIVATE_DIALOG_INDEX = 1415;
  
  public static final int ERROR_LISTBOX_ID_NOT_FOUND = 1416;
  
  public static final int ERROR_NO_WILDCARD_CHARACTERS = 1417;
  
  public static final int ERROR_CLIPBOARD_NOT_OPEN = 1418;
  
  public static final int ERROR_HOTKEY_NOT_REGISTERED = 1419;
  
  public static final int ERROR_WINDOW_NOT_DIALOG = 1420;
  
  public static final int ERROR_CONTROL_ID_NOT_FOUND = 1421;
  
  public static final int ERROR_INVALID_COMBOBOX_MESSAGE = 1422;
  
  public static final int ERROR_WINDOW_NOT_COMBOBOX = 1423;
  
  public static final int ERROR_INVALID_EDIT_HEIGHT = 1424;
  
  public static final int ERROR_DC_NOT_FOUND = 1425;
  
  public static final int ERROR_INVALID_HOOK_FILTER = 1426;
  
  public static final int ERROR_INVALID_FILTER_PROC = 1427;
  
  public static final int ERROR_HOOK_NEEDS_HMOD = 1428;
  
  public static final int ERROR_GLOBAL_ONLY_HOOK = 1429;
  
  public static final int ERROR_JOURNAL_HOOK_SET = 1430;
  
  public static final int ERROR_HOOK_NOT_INSTALLED = 1431;
  
  public static final int ERROR_INVALID_LB_MESSAGE = 1432;
  
  public static final int ERROR_SETCOUNT_ON_BAD_LB = 1433;
  
  public static final int ERROR_LB_WITHOUT_TABSTOPS = 1434;
  
  public static final int ERROR_DESTROY_OBJECT_OF_OTHER_THREAD = 1435;
  
  public static final int ERROR_CHILD_WINDOW_MENU = 1436;
  
  public static final int ERROR_NO_SYSTEM_MENU = 1437;
  
  public static final int ERROR_INVALID_MSGBOX_STYLE = 1438;
  
  public static final int ERROR_INVALID_SPI_VALUE = 1439;
  
  public static final int ERROR_SCREEN_ALREADY_LOCKED = 1440;
  
  public static final int ERROR_HWNDS_HAVE_DIFF_PARENT = 1441;
  
  public static final int ERROR_NOT_CHILD_WINDOW = 1442;
  
  public static final int ERROR_INVALID_GW_COMMAND = 1443;
  
  public static final int ERROR_INVALID_THREAD_ID = 1444;
  
  public static final int ERROR_NON_MDICHILD_WINDOW = 1445;
  
  public static final int ERROR_POPUP_ALREADY_ACTIVE = 1446;
  
  public static final int ERROR_NO_SCROLLBARS = 1447;
  
  public static final int ERROR_INVALID_SCROLLBAR_RANGE = 1448;
  
  public static final int ERROR_INVALID_SHOWWIN_COMMAND = 1449;
  
  public static final int ERROR_NO_SYSTEM_RESOURCES = 1450;
  
  public static final int ERROR_NONPAGED_SYSTEM_RESOURCES = 1451;
  
  public static final int ERROR_PAGED_SYSTEM_RESOURCES = 1452;
  
  public static final int ERROR_WORKING_SET_QUOTA = 1453;
  
  public static final int ERROR_PAGEFILE_QUOTA = 1454;
  
  public static final int ERROR_COMMITMENT_LIMIT = 1455;
  
  public static final int ERROR_MENU_ITEM_NOT_FOUND = 1456;
  
  public static final int ERROR_INVALID_KEYBOARD_HANDLE = 1457;
  
  public static final int ERROR_HOOK_TYPE_NOT_ALLOWED = 1458;
  
  public static final int ERROR_REQUIRES_INTERACTIVE_WINDOWSTATION = 1459;
  
  public static final int ERROR_TIMEOUT = 1460;
  
  public static final int ERROR_INVALID_MONITOR_HANDLE = 1461;
  
  public static final int ERROR_INCORRECT_SIZE = 1462;
  
  public static final int ERROR_SYMLINK_CLASS_DISABLED = 1463;
  
  public static final int ERROR_SYMLINK_NOT_SUPPORTED = 1464;
  
  public static final int ERROR_XML_PARSE_ERROR = 1465;
  
  public static final int ERROR_XMLDSIG_ERROR = 1466;
  
  public static final int ERROR_RESTART_APPLICATION = 1467;
  
  public static final int ERROR_WRONG_COMPARTMENT = 1468;
  
  public static final int ERROR_AUTHIP_FAILURE = 1469;
  
  public static final int ERROR_EVENTLOG_FILE_CORRUPT = 1500;
  
  public static final int ERROR_EVENTLOG_CANT_START = 1501;
  
  public static final int ERROR_LOG_FILE_FULL = 1502;
  
  public static final int ERROR_EVENTLOG_FILE_CHANGED = 1503;
  
  public static final int ERROR_INVALID_TASK_NAME = 1550;
  
  public static final int ERROR_INVALID_TASK_INDEX = 1551;
  
  public static final int ERROR_THREAD_ALREADY_IN_TASK = 1552;
  
  public static final int ERROR_INSTALL_SERVICE_FAILURE = 1601;
  
  public static final int ERROR_INSTALL_USEREXIT = 1602;
  
  public static final int ERROR_INSTALL_FAILURE = 1603;
  
  public static final int ERROR_INSTALL_SUSPEND = 1604;
  
  public static final int ERROR_UNKNOWN_PRODUCT = 1605;
  
  public static final int ERROR_UNKNOWN_FEATURE = 1606;
  
  public static final int ERROR_UNKNOWN_COMPONENT = 1607;
  
  public static final int ERROR_UNKNOWN_PROPERTY = 1608;
  
  public static final int ERROR_INVALID_HANDLE_STATE = 1609;
  
  public static final int ERROR_BAD_CONFIGURATION = 1610;
  
  public static final int ERROR_INDEX_ABSENT = 1611;
  
  public static final int ERROR_INSTALL_SOURCE_ABSENT = 1612;
  
  public static final int ERROR_INSTALL_PACKAGE_VERSION = 1613;
  
  public static final int ERROR_PRODUCT_UNINSTALLED = 1614;
  
  public static final int ERROR_BAD_QUERY_SYNTAX = 1615;
  
  public static final int ERROR_INVALID_FIELD = 1616;
  
  public static final int ERROR_DEVICE_REMOVED = 1617;
  
  public static final int ERROR_INSTALL_ALREADY_RUNNING = 1618;
  
  public static final int ERROR_INSTALL_PACKAGE_OPEN_FAILED = 1619;
  
  public static final int ERROR_INSTALL_PACKAGE_INVALID = 1620;
  
  public static final int ERROR_INSTALL_UI_FAILURE = 1621;
  
  public static final int ERROR_INSTALL_LOG_FAILURE = 1622;
  
  public static final int ERROR_INSTALL_LANGUAGE_UNSUPPORTED = 1623;
  
  public static final int ERROR_INSTALL_TRANSFORM_FAILURE = 1624;
  
  public static final int ERROR_INSTALL_PACKAGE_REJECTED = 1625;
  
  public static final int ERROR_FUNCTION_NOT_CALLED = 1626;
  
  public static final int ERROR_FUNCTION_FAILED = 1627;
  
  public static final int ERROR_INVALID_TABLE = 1628;
  
  public static final int ERROR_DATATYPE_MISMATCH = 1629;
  
  public static final int ERROR_UNSUPPORTED_TYPE = 1630;
  
  public static final int ERROR_CREATE_FAILED = 1631;
  
  public static final int ERROR_INSTALL_TEMP_UNWRITABLE = 1632;
  
  public static final int ERROR_INSTALL_PLATFORM_UNSUPPORTED = 1633;
  
  public static final int ERROR_INSTALL_NOTUSED = 1634;
  
  public static final int ERROR_PATCH_PACKAGE_OPEN_FAILED = 1635;
  
  public static final int ERROR_PATCH_PACKAGE_INVALID = 1636;
  
  public static final int ERROR_PATCH_PACKAGE_UNSUPPORTED = 1637;
  
  public static final int ERROR_PRODUCT_VERSION = 1638;
  
  public static final int ERROR_INVALID_COMMAND_LINE = 1639;
  
  public static final int ERROR_INSTALL_REMOTE_DISALLOWED = 1640;
  
  public static final int ERROR_SUCCESS_REBOOT_INITIATED = 1641;
  
  public static final int ERROR_PATCH_TARGET_NOT_FOUND = 1642;
  
  public static final int ERROR_PATCH_PACKAGE_REJECTED = 1643;
  
  public static final int ERROR_INSTALL_TRANSFORM_REJECTED = 1644;
  
  public static final int ERROR_INSTALL_REMOTE_PROHIBITED = 1645;
  
  public static final int ERROR_PATCH_REMOVAL_UNSUPPORTED = 1646;
  
  public static final int ERROR_UNKNOWN_PATCH = 1647;
  
  public static final int ERROR_PATCH_NO_SEQUENCE = 1648;
  
  public static final int ERROR_PATCH_REMOVAL_DISALLOWED = 1649;
  
  public static final int ERROR_INVALID_PATCH_XML = 1650;
  
  public static final int ERROR_PATCH_MANAGED_ADVERTISED_PRODUCT = 1651;
  
  public static final int ERROR_INSTALL_SERVICE_SAFEBOOT = 1652;
  
  public static final int RPC_S_INVALID_STRING_BINDING = 1700;
  
  public static final int RPC_S_WRONG_KIND_OF_BINDING = 1701;
  
  public static final int RPC_S_INVALID_BINDING = 1702;
  
  public static final int RPC_S_PROTSEQ_NOT_SUPPORTED = 1703;
  
  public static final int RPC_S_INVALID_RPC_PROTSEQ = 1704;
  
  public static final int RPC_S_INVALID_STRING_UUID = 1705;
  
  public static final int RPC_S_INVALID_ENDPOINT_FORMAT = 1706;
  
  public static final int RPC_S_INVALID_NET_ADDR = 1707;
  
  public static final int RPC_S_NO_ENDPOINT_FOUND = 1708;
  
  public static final int RPC_S_INVALID_TIMEOUT = 1709;
  
  public static final int RPC_S_OBJECT_NOT_FOUND = 1710;
  
  public static final int RPC_S_ALREADY_REGISTERED = 1711;
  
  public static final int RPC_S_TYPE_ALREADY_REGISTERED = 1712;
  
  public static final int RPC_S_ALREADY_LISTENING = 1713;
  
  public static final int RPC_S_NO_PROTSEQS_REGISTERED = 1714;
  
  public static final int RPC_S_NOT_LISTENING = 1715;
  
  public static final int RPC_S_UNKNOWN_MGR_TYPE = 1716;
  
  public static final int RPC_S_UNKNOWN_IF = 1717;
  
  public static final int RPC_S_NO_BINDINGS = 1718;
  
  public static final int RPC_S_NO_PROTSEQS = 1719;
  
  public static final int RPC_S_CANT_CREATE_ENDPOINT = 1720;
  
  public static final int RPC_S_OUT_OF_RESOURCES = 1721;
  
  public static final int RPC_S_SERVER_UNAVAILABLE = 1722;
  
  public static final int RPC_S_SERVER_TOO_BUSY = 1723;
  
  public static final int RPC_S_INVALID_NETWORK_OPTIONS = 1724;
  
  public static final int RPC_S_NO_CALL_ACTIVE = 1725;
  
  public static final int RPC_S_CALL_FAILED = 1726;
  
  public static final int RPC_S_CALL_FAILED_DNE = 1727;
  
  public static final int RPC_S_PROTOCOL_ERROR = 1728;
  
  public static final int RPC_S_PROXY_ACCESS_DENIED = 1729;
  
  public static final int RPC_S_UNSUPPORTED_TRANS_SYN = 1730;
  
  public static final int RPC_S_UNSUPPORTED_TYPE = 1732;
  
  public static final int RPC_S_INVALID_TAG = 1733;
  
  public static final int RPC_S_INVALID_BOUND = 1734;
  
  public static final int RPC_S_NO_ENTRY_NAME = 1735;
  
  public static final int RPC_S_INVALID_NAME_SYNTAX = 1736;
  
  public static final int RPC_S_UNSUPPORTED_NAME_SYNTAX = 1737;
  
  public static final int RPC_S_UUID_NO_ADDRESS = 1739;
  
  public static final int RPC_S_DUPLICATE_ENDPOINT = 1740;
  
  public static final int RPC_S_UNKNOWN_AUTHN_TYPE = 1741;
  
  public static final int RPC_S_MAX_CALLS_TOO_SMALL = 1742;
  
  public static final int RPC_S_STRING_TOO_LONG = 1743;
  
  public static final int RPC_S_PROTSEQ_NOT_FOUND = 1744;
  
  public static final int RPC_S_PROCNUM_OUT_OF_RANGE = 1745;
  
  public static final int RPC_S_BINDING_HAS_NO_AUTH = 1746;
  
  public static final int RPC_S_UNKNOWN_AUTHN_SERVICE = 1747;
  
  public static final int RPC_S_UNKNOWN_AUTHN_LEVEL = 1748;
  
  public static final int RPC_S_INVALID_AUTH_IDENTITY = 1749;
  
  public static final int RPC_S_UNKNOWN_AUTHZ_SERVICE = 1750;
  
  public static final int EPT_S_INVALID_ENTRY = 1751;
  
  public static final int EPT_S_CANT_PERFORM_OP = 1752;
  
  public static final int EPT_S_NOT_REGISTERED = 1753;
  
  public static final int RPC_S_NOTHING_TO_EXPORT = 1754;
  
  public static final int RPC_S_INCOMPLETE_NAME = 1755;
  
  public static final int RPC_S_INVALID_VERS_OPTION = 1756;
  
  public static final int RPC_S_NO_MORE_MEMBERS = 1757;
  
  public static final int RPC_S_NOT_ALL_OBJS_UNEXPORTED = 1758;
  
  public static final int RPC_S_INTERFACE_NOT_FOUND = 1759;
  
  public static final int RPC_S_ENTRY_ALREADY_EXISTS = 1760;
  
  public static final int RPC_S_ENTRY_NOT_FOUND = 1761;
  
  public static final int RPC_S_NAME_SERVICE_UNAVAILABLE = 1762;
  
  public static final int RPC_S_INVALID_NAF_ID = 1763;
  
  public static final int RPC_S_CANNOT_SUPPORT = 1764;
  
  public static final int RPC_S_NO_CONTEXT_AVAILABLE = 1765;
  
  public static final int RPC_S_INTERNAL_ERROR = 1766;
  
  public static final int RPC_S_ZERO_DIVIDE = 1767;
  
  public static final int RPC_S_ADDRESS_ERROR = 1768;
  
  public static final int RPC_S_FP_DIV_ZERO = 1769;
  
  public static final int RPC_S_FP_UNDERFLOW = 1770;
  
  public static final int RPC_S_FP_OVERFLOW = 1771;
  
  public static final int RPC_X_NO_MORE_ENTRIES = 1772;
  
  public static final int RPC_X_SS_CHAR_TRANS_OPEN_FAIL = 1773;
  
  public static final int RPC_X_SS_CHAR_TRANS_SHORT_FILE = 1774;
  
  public static final int RPC_X_SS_IN_NULL_CONTEXT = 1775;
  
  public static final int RPC_X_SS_CONTEXT_DAMAGED = 1777;
  
  public static final int RPC_X_SS_HANDLES_MISMATCH = 1778;
  
  public static final int RPC_X_SS_CANNOT_GET_CALL_HANDLE = 1779;
  
  public static final int RPC_X_NULL_REF_POINTER = 1780;
  
  public static final int RPC_X_ENUM_VALUE_OUT_OF_RANGE = 1781;
  
  public static final int RPC_X_BYTE_COUNT_TOO_SMALL = 1782;
  
  public static final int RPC_X_BAD_STUB_DATA = 1783;
  
  public static final int ERROR_INVALID_USER_BUFFER = 1784;
  
  public static final int ERROR_UNRECOGNIZED_MEDIA = 1785;
  
  public static final int ERROR_NO_TRUST_LSA_SECRET = 1786;
  
  public static final int ERROR_NO_TRUST_SAM_ACCOUNT = 1787;
  
  public static final int ERROR_TRUSTED_DOMAIN_FAILURE = 1788;
  
  public static final int ERROR_TRUSTED_RELATIONSHIP_FAILURE = 1789;
  
  public static final int ERROR_TRUST_FAILURE = 1790;
  
  public static final int RPC_S_CALL_IN_PROGRESS = 1791;
  
  public static final int ERROR_NETLOGON_NOT_STARTED = 1792;
  
  public static final int ERROR_ACCOUNT_EXPIRED = 1793;
  
  public static final int ERROR_REDIRECTOR_HAS_OPEN_HANDLES = 1794;
  
  public static final int ERROR_PRINTER_DRIVER_ALREADY_INSTALLED = 1795;
  
  public static final int ERROR_UNKNOWN_PORT = 1796;
  
  public static final int ERROR_UNKNOWN_PRINTER_DRIVER = 1797;
  
  public static final int ERROR_UNKNOWN_PRINTPROCESSOR = 1798;
  
  public static final int ERROR_INVALID_SEPARATOR_FILE = 1799;
  
  public static final int ERROR_INVALID_PRIORITY = 1800;
  
  public static final int ERROR_INVALID_PRINTER_NAME = 1801;
  
  public static final int ERROR_PRINTER_ALREADY_EXISTS = 1802;
  
  public static final int ERROR_INVALID_PRINTER_COMMAND = 1803;
  
  public static final int ERROR_INVALID_DATATYPE = 1804;
  
  public static final int ERROR_INVALID_ENVIRONMENT = 1805;
  
  public static final int RPC_S_NO_MORE_BINDINGS = 1806;
  
  public static final int ERROR_NOLOGON_INTERDOMAIN_TRUST_ACCOUNT = 1807;
  
  public static final int ERROR_NOLOGON_WORKSTATION_TRUST_ACCOUNT = 1808;
  
  public static final int ERROR_NOLOGON_SERVER_TRUST_ACCOUNT = 1809;
  
  public static final int ERROR_DOMAIN_TRUST_INCONSISTENT = 1810;
  
  public static final int ERROR_SERVER_HAS_OPEN_HANDLES = 1811;
  
  public static final int ERROR_RESOURCE_DATA_NOT_FOUND = 1812;
  
  public static final int ERROR_RESOURCE_TYPE_NOT_FOUND = 1813;
  
  public static final int ERROR_RESOURCE_NAME_NOT_FOUND = 1814;
  
  public static final int ERROR_RESOURCE_LANG_NOT_FOUND = 1815;
  
  public static final int ERROR_NOT_ENOUGH_QUOTA = 1816;
  
  public static final int RPC_S_NO_INTERFACES = 1817;
  
  public static final int RPC_S_CALL_CANCELLED = 1818;
  
  public static final int RPC_S_BINDING_INCOMPLETE = 1819;
  
  public static final int RPC_S_COMM_FAILURE = 1820;
  
  public static final int RPC_S_UNSUPPORTED_AUTHN_LEVEL = 1821;
  
  public static final int RPC_S_NO_PRINC_NAME = 1822;
  
  public static final int RPC_S_NOT_RPC_ERROR = 1823;
  
  public static final int RPC_S_UUID_LOCAL_ONLY = 1824;
  
  public static final int RPC_S_SEC_PKG_ERROR = 1825;
  
  public static final int RPC_S_NOT_CANCELLED = 1826;
  
  public static final int RPC_X_INVALID_ES_ACTION = 1827;
  
  public static final int RPC_X_WRONG_ES_VERSION = 1828;
  
  public static final int RPC_X_WRONG_STUB_VERSION = 1829;
  
  public static final int RPC_X_INVALID_PIPE_OBJECT = 1830;
  
  public static final int RPC_X_WRONG_PIPE_ORDER = 1831;
  
  public static final int RPC_X_WRONG_PIPE_VERSION = 1832;
  
  public static final int RPC_S_GROUP_MEMBER_NOT_FOUND = 1898;
  
  public static final int EPT_S_CANT_CREATE = 1899;
  
  public static final int RPC_S_INVALID_OBJECT = 1900;
  
  public static final int ERROR_INVALID_TIME = 1901;
  
  public static final int ERROR_INVALID_FORM_NAME = 1902;
  
  public static final int ERROR_INVALID_FORM_SIZE = 1903;
  
  public static final int ERROR_ALREADY_WAITING = 1904;
  
  public static final int ERROR_PRINTER_DELETED = 1905;
  
  public static final int ERROR_INVALID_PRINTER_STATE = 1906;
  
  public static final int ERROR_PASSWORD_MUST_CHANGE = 1907;
  
  public static final int ERROR_DOMAIN_CONTROLLER_NOT_FOUND = 1908;
  
  public static final int ERROR_ACCOUNT_LOCKED_OUT = 1909;
  
  public static final int OR_INVALID_OXID = 1910;
  
  public static final int OR_INVALID_OID = 1911;
  
  public static final int OR_INVALID_SET = 1912;
  
  public static final int RPC_S_SEND_INCOMPLETE = 1913;
  
  public static final int RPC_S_INVALID_ASYNC_HANDLE = 1914;
  
  public static final int RPC_S_INVALID_ASYNC_CALL = 1915;
  
  public static final int RPC_X_PIPE_CLOSED = 1916;
  
  public static final int RPC_X_PIPE_DISCIPLINE_ERROR = 1917;
  
  public static final int RPC_X_PIPE_EMPTY = 1918;
  
  public static final int ERROR_NO_SITENAME = 1919;
  
  public static final int ERROR_CANT_ACCESS_FILE = 1920;
  
  public static final int ERROR_CANT_RESOLVE_FILENAME = 1921;
  
  public static final int RPC_S_ENTRY_TYPE_MISMATCH = 1922;
  
  public static final int RPC_S_NOT_ALL_OBJS_EXPORTED = 1923;
  
  public static final int RPC_S_INTERFACE_NOT_EXPORTED = 1924;
  
  public static final int RPC_S_PROFILE_NOT_ADDED = 1925;
  
  public static final int RPC_S_PRF_ELT_NOT_ADDED = 1926;
  
  public static final int RPC_S_PRF_ELT_NOT_REMOVED = 1927;
  
  public static final int RPC_S_GRP_ELT_NOT_ADDED = 1928;
  
  public static final int RPC_S_GRP_ELT_NOT_REMOVED = 1929;
  
  public static final int ERROR_KM_DRIVER_BLOCKED = 1930;
  
  public static final int ERROR_CONTEXT_EXPIRED = 1931;
  
  public static final int ERROR_PER_USER_TRUST_QUOTA_EXCEEDED = 1932;
  
  public static final int ERROR_ALL_USER_TRUST_QUOTA_EXCEEDED = 1933;
  
  public static final int ERROR_USER_DELETE_TRUST_QUOTA_EXCEEDED = 1934;
  
  public static final int ERROR_AUTHENTICATION_FIREWALL_FAILED = 1935;
  
  public static final int ERROR_REMOTE_PRINT_CONNECTIONS_BLOCKED = 1936;
  
  public static final int ERROR_NTLM_BLOCKED = 1937;
  
  public static final int ERROR_INVALID_PIXEL_FORMAT = 2000;
  
  public static final int ERROR_BAD_DRIVER = 2001;
  
  public static final int ERROR_INVALID_WINDOW_STYLE = 2002;
  
  public static final int ERROR_METAFILE_NOT_SUPPORTED = 2003;
  
  public static final int ERROR_TRANSFORM_NOT_SUPPORTED = 2004;
  
  public static final int ERROR_CLIPPING_NOT_SUPPORTED = 2005;
  
  public static final int ERROR_INVALID_CMM = 2010;
  
  public static final int ERROR_INVALID_PROFILE = 2011;
  
  public static final int ERROR_TAG_NOT_FOUND = 2012;
  
  public static final int ERROR_TAG_NOT_PRESENT = 2013;
  
  public static final int ERROR_DUPLICATE_TAG = 2014;
  
  public static final int ERROR_PROFILE_NOT_ASSOCIATED_WITH_DEVICE = 2015;
  
  public static final int ERROR_PROFILE_NOT_FOUND = 2016;
  
  public static final int ERROR_INVALID_COLORSPACE = 2017;
  
  public static final int ERROR_ICM_NOT_ENABLED = 2018;
  
  public static final int ERROR_DELETING_ICM_XFORM = 2019;
  
  public static final int ERROR_INVALID_TRANSFORM = 2020;
  
  public static final int ERROR_COLORSPACE_MISMATCH = 2021;
  
  public static final int ERROR_INVALID_COLORINDEX = 2022;
  
  public static final int ERROR_PROFILE_DOES_NOT_MATCH_DEVICE = 2023;
  
  public static final int ERROR_CONNECTED_OTHER_PASSWORD = 2108;
  
  public static final int ERROR_CONNECTED_OTHER_PASSWORD_DEFAULT = 2109;
  
  public static final int ERROR_BAD_USERNAME = 2202;
  
  public static final int ERROR_NOT_CONNECTED = 2250;
  
  public static final int ERROR_OPEN_FILES = 2401;
  
  public static final int ERROR_ACTIVE_CONNECTIONS = 2402;
  
  public static final int ERROR_DEVICE_IN_USE = 2404;
  
  public static final int ERROR_UNKNOWN_PRINT_MONITOR = 3000;
  
  public static final int ERROR_PRINTER_DRIVER_IN_USE = 3001;
  
  public static final int ERROR_SPOOL_FILE_NOT_FOUND = 3002;
  
  public static final int ERROR_SPL_NO_STARTDOC = 3003;
  
  public static final int ERROR_SPL_NO_ADDJOB = 3004;
  
  public static final int ERROR_PRINT_PROCESSOR_ALREADY_INSTALLED = 3005;
  
  public static final int ERROR_PRINT_MONITOR_ALREADY_INSTALLED = 3006;
  
  public static final int ERROR_INVALID_PRINT_MONITOR = 3007;
  
  public static final int ERROR_PRINT_MONITOR_IN_USE = 3008;
  
  public static final int ERROR_PRINTER_HAS_JOBS_QUEUED = 3009;
  
  public static final int ERROR_SUCCESS_REBOOT_REQUIRED = 3010;
  
  public static final int ERROR_SUCCESS_RESTART_REQUIRED = 3011;
  
  public static final int ERROR_PRINTER_NOT_FOUND = 3012;
  
  public static final int ERROR_PRINTER_DRIVER_WARNED = 3013;
  
  public static final int ERROR_PRINTER_DRIVER_BLOCKED = 3014;
  
  public static final int ERROR_PRINTER_DRIVER_PACKAGE_IN_USE = 3015;
  
  public static final int ERROR_CORE_DRIVER_PACKAGE_NOT_FOUND = 3016;
  
  public static final int ERROR_FAIL_REBOOT_REQUIRED = 3017;
  
  public static final int ERROR_FAIL_REBOOT_INITIATED = 3018;
  
  public static final int ERROR_PRINTER_DRIVER_DOWNLOAD_NEEDED = 3019;
  
  public static final int ERROR_PRINT_JOB_RESTART_REQUIRED = 3020;
  
  public static final int ERROR_IO_REISSUE_AS_CACHED = 3950;
  
  public static final int ERROR_WINS_INTERNAL = 4000;
  
  public static final int ERROR_CAN_NOT_DEL_LOCAL_WINS = 4001;
  
  public static final int ERROR_STATIC_INIT = 4002;
  
  public static final int ERROR_INC_BACKUP = 4003;
  
  public static final int ERROR_FULL_BACKUP = 4004;
  
  public static final int ERROR_REC_NON_EXISTENT = 4005;
  
  public static final int ERROR_RPL_NOT_ALLOWED = 4006;
  
  public static final int ERROR_DHCP_ADDRESS_CONFLICT = 4100;
  
  public static final int ERROR_WMI_GUID_NOT_FOUND = 4200;
  
  public static final int ERROR_WMI_INSTANCE_NOT_FOUND = 4201;
  
  public static final int ERROR_WMI_ITEMID_NOT_FOUND = 4202;
  
  public static final int ERROR_WMI_TRY_AGAIN = 4203;
  
  public static final int ERROR_WMI_DP_NOT_FOUND = 4204;
  
  public static final int ERROR_WMI_UNRESOLVED_INSTANCE_REF = 4205;
  
  public static final int ERROR_WMI_ALREADY_ENABLED = 4206;
  
  public static final int ERROR_WMI_GUID_DISCONNECTED = 4207;
  
  public static final int ERROR_WMI_SERVER_UNAVAILABLE = 4208;
  
  public static final int ERROR_WMI_DP_FAILED = 4209;
  
  public static final int ERROR_WMI_INVALID_MOF = 4210;
  
  public static final int ERROR_WMI_INVALID_REGINFO = 4211;
  
  public static final int ERROR_WMI_ALREADY_DISABLED = 4212;
  
  public static final int ERROR_WMI_READ_ONLY = 4213;
  
  public static final int ERROR_WMI_SET_FAILURE = 4214;
  
  public static final int ERROR_INVALID_MEDIA = 4300;
  
  public static final int ERROR_INVALID_LIBRARY = 4301;
  
  public static final int ERROR_INVALID_MEDIA_POOL = 4302;
  
  public static final int ERROR_DRIVE_MEDIA_MISMATCH = 4303;
  
  public static final int ERROR_MEDIA_OFFLINE = 4304;
  
  public static final int ERROR_LIBRARY_OFFLINE = 4305;
  
  public static final int ERROR_EMPTY = 4306;
  
  public static final int ERROR_NOT_EMPTY = 4307;
  
  public static final int ERROR_MEDIA_UNAVAILABLE = 4308;
  
  public static final int ERROR_RESOURCE_DISABLED = 4309;
  
  public static final int ERROR_INVALID_CLEANER = 4310;
  
  public static final int ERROR_UNABLE_TO_CLEAN = 4311;
  
  public static final int ERROR_OBJECT_NOT_FOUND = 4312;
  
  public static final int ERROR_DATABASE_FAILURE = 4313;
  
  public static final int ERROR_DATABASE_FULL = 4314;
  
  public static final int ERROR_MEDIA_INCOMPATIBLE = 4315;
  
  public static final int ERROR_RESOURCE_NOT_PRESENT = 4316;
  
  public static final int ERROR_INVALID_OPERATION = 4317;
  
  public static final int ERROR_MEDIA_NOT_AVAILABLE = 4318;
  
  public static final int ERROR_DEVICE_NOT_AVAILABLE = 4319;
  
  public static final int ERROR_REQUEST_REFUSED = 4320;
  
  public static final int ERROR_INVALID_DRIVE_OBJECT = 4321;
  
  public static final int ERROR_LIBRARY_FULL = 4322;
  
  public static final int ERROR_MEDIUM_NOT_ACCESSIBLE = 4323;
  
  public static final int ERROR_UNABLE_TO_LOAD_MEDIUM = 4324;
  
  public static final int ERROR_UNABLE_TO_INVENTORY_DRIVE = 4325;
  
  public static final int ERROR_UNABLE_TO_INVENTORY_SLOT = 4326;
  
  public static final int ERROR_UNABLE_TO_INVENTORY_TRANSPORT = 4327;
  
  public static final int ERROR_TRANSPORT_FULL = 4328;
  
  public static final int ERROR_CONTROLLING_IEPORT = 4329;
  
  public static final int ERROR_UNABLE_TO_EJECT_MOUNTED_MEDIA = 4330;
  
  public static final int ERROR_CLEANER_SLOT_SET = 4331;
  
  public static final int ERROR_CLEANER_SLOT_NOT_SET = 4332;
  
  public static final int ERROR_CLEANER_CARTRIDGE_SPENT = 4333;
  
  public static final int ERROR_UNEXPECTED_OMID = 4334;
  
  public static final int ERROR_CANT_DELETE_LAST_ITEM = 4335;
  
  public static final int ERROR_MESSAGE_EXCEEDS_MAX_SIZE = 4336;
  
  public static final int ERROR_VOLUME_CONTAINS_SYS_FILES = 4337;
  
  public static final int ERROR_INDIGENOUS_TYPE = 4338;
  
  public static final int ERROR_NO_SUPPORTING_DRIVES = 4339;
  
  public static final int ERROR_CLEANER_CARTRIDGE_INSTALLED = 4340;
  
  public static final int ERROR_IEPORT_FULL = 4341;
  
  public static final int ERROR_FILE_OFFLINE = 4350;
  
  public static final int ERROR_REMOTE_STORAGE_NOT_ACTIVE = 4351;
  
  public static final int ERROR_REMOTE_STORAGE_MEDIA_ERROR = 4352;
  
  public static final int ERROR_NOT_A_REPARSE_POINT = 4390;
  
  public static final int ERROR_REPARSE_ATTRIBUTE_CONFLICT = 4391;
  
  public static final int ERROR_INVALID_REPARSE_DATA = 4392;
  
  public static final int ERROR_REPARSE_TAG_INVALID = 4393;
  
  public static final int ERROR_REPARSE_TAG_MISMATCH = 4394;
  
  public static final int ERROR_VOLUME_NOT_SIS_ENABLED = 4500;
  
  public static final int ERROR_DEPENDENT_RESOURCE_EXISTS = 5001;
  
  public static final int ERROR_DEPENDENCY_NOT_FOUND = 5002;
  
  public static final int ERROR_DEPENDENCY_ALREADY_EXISTS = 5003;
  
  public static final int ERROR_RESOURCE_NOT_ONLINE = 5004;
  
  public static final int ERROR_HOST_NODE_NOT_AVAILABLE = 5005;
  
  public static final int ERROR_RESOURCE_NOT_AVAILABLE = 5006;
  
  public static final int ERROR_RESOURCE_NOT_FOUND = 5007;
  
  public static final int ERROR_SHUTDOWN_CLUSTER = 5008;
  
  public static final int ERROR_CANT_EVICT_ACTIVE_NODE = 5009;
  
  public static final int ERROR_OBJECT_ALREADY_EXISTS = 5010;
  
  public static final int ERROR_OBJECT_IN_LIST = 5011;
  
  public static final int ERROR_GROUP_NOT_AVAILABLE = 5012;
  
  public static final int ERROR_GROUP_NOT_FOUND = 5013;
  
  public static final int ERROR_GROUP_NOT_ONLINE = 5014;
  
  public static final int ERROR_HOST_NODE_NOT_RESOURCE_OWNER = 5015;
  
  public static final int ERROR_HOST_NODE_NOT_GROUP_OWNER = 5016;
  
  public static final int ERROR_RESMON_CREATE_FAILED = 5017;
  
  public static final int ERROR_RESMON_ONLINE_FAILED = 5018;
  
  public static final int ERROR_RESOURCE_ONLINE = 5019;
  
  public static final int ERROR_QUORUM_RESOURCE = 5020;
  
  public static final int ERROR_NOT_QUORUM_CAPABLE = 5021;
  
  public static final int ERROR_CLUSTER_SHUTTING_DOWN = 5022;
  
  public static final int ERROR_INVALID_STATE = 5023;
  
  public static final int ERROR_RESOURCE_PROPERTIES_STORED = 5024;
  
  public static final int ERROR_NOT_QUORUM_CLASS = 5025;
  
  public static final int ERROR_CORE_RESOURCE = 5026;
  
  public static final int ERROR_QUORUM_RESOURCE_ONLINE_FAILED = 5027;
  
  public static final int ERROR_QUORUMLOG_OPEN_FAILED = 5028;
  
  public static final int ERROR_CLUSTERLOG_CORRUPT = 5029;
  
  public static final int ERROR_CLUSTERLOG_RECORD_EXCEEDS_MAXSIZE = 5030;
  
  public static final int ERROR_CLUSTERLOG_EXCEEDS_MAXSIZE = 5031;
  
  public static final int ERROR_CLUSTERLOG_CHKPOINT_NOT_FOUND = 5032;
  
  public static final int ERROR_CLUSTERLOG_NOT_ENOUGH_SPACE = 5033;
  
  public static final int ERROR_QUORUM_OWNER_ALIVE = 5034;
  
  public static final int ERROR_NETWORK_NOT_AVAILABLE = 5035;
  
  public static final int ERROR_NODE_NOT_AVAILABLE = 5036;
  
  public static final int ERROR_ALL_NODES_NOT_AVAILABLE = 5037;
  
  public static final int ERROR_RESOURCE_FAILED = 5038;
  
  public static final int ERROR_CLUSTER_INVALID_NODE = 5039;
  
  public static final int ERROR_CLUSTER_NODE_EXISTS = 5040;
  
  public static final int ERROR_CLUSTER_JOIN_IN_PROGRESS = 5041;
  
  public static final int ERROR_CLUSTER_NODE_NOT_FOUND = 5042;
  
  public static final int ERROR_CLUSTER_LOCAL_NODE_NOT_FOUND = 5043;
  
  public static final int ERROR_CLUSTER_NETWORK_EXISTS = 5044;
  
  public static final int ERROR_CLUSTER_NETWORK_NOT_FOUND = 5045;
  
  public static final int ERROR_CLUSTER_NETINTERFACE_EXISTS = 5046;
  
  public static final int ERROR_CLUSTER_NETINTERFACE_NOT_FOUND = 5047;
  
  public static final int ERROR_CLUSTER_INVALID_REQUEST = 5048;
  
  public static final int ERROR_CLUSTER_INVALID_NETWORK_PROVIDER = 5049;
  
  public static final int ERROR_CLUSTER_NODE_DOWN = 5050;
  
  public static final int ERROR_CLUSTER_NODE_UNREACHABLE = 5051;
  
  public static final int ERROR_CLUSTER_NODE_NOT_MEMBER = 5052;
  
  public static final int ERROR_CLUSTER_JOIN_NOT_IN_PROGRESS = 5053;
  
  public static final int ERROR_CLUSTER_INVALID_NETWORK = 5054;
  
  public static final int ERROR_CLUSTER_NODE_UP = 5056;
  
  public static final int ERROR_CLUSTER_IPADDR_IN_USE = 5057;
  
  public static final int ERROR_CLUSTER_NODE_NOT_PAUSED = 5058;
  
  public static final int ERROR_CLUSTER_NO_SECURITY_CONTEXT = 5059;
  
  public static final int ERROR_CLUSTER_NETWORK_NOT_INTERNAL = 5060;
  
  public static final int ERROR_CLUSTER_NODE_ALREADY_UP = 5061;
  
  public static final int ERROR_CLUSTER_NODE_ALREADY_DOWN = 5062;
  
  public static final int ERROR_CLUSTER_NETWORK_ALREADY_ONLINE = 5063;
  
  public static final int ERROR_CLUSTER_NETWORK_ALREADY_OFFLINE = 5064;
  
  public static final int ERROR_CLUSTER_NODE_ALREADY_MEMBER = 5065;
  
  public static final int ERROR_CLUSTER_LAST_INTERNAL_NETWORK = 5066;
  
  public static final int ERROR_CLUSTER_NETWORK_HAS_DEPENDENTS = 5067;
  
  public static final int ERROR_INVALID_OPERATION_ON_QUORUM = 5068;
  
  public static final int ERROR_DEPENDENCY_NOT_ALLOWED = 5069;
  
  public static final int ERROR_CLUSTER_NODE_PAUSED = 5070;
  
  public static final int ERROR_NODE_CANT_HOST_RESOURCE = 5071;
  
  public static final int ERROR_CLUSTER_NODE_NOT_READY = 5072;
  
  public static final int ERROR_CLUSTER_NODE_SHUTTING_DOWN = 5073;
  
  public static final int ERROR_CLUSTER_JOIN_ABORTED = 5074;
  
  public static final int ERROR_CLUSTER_INCOMPATIBLE_VERSIONS = 5075;
  
  public static final int ERROR_CLUSTER_MAXNUM_OF_RESOURCES_EXCEEDED = 5076;
  
  public static final int ERROR_CLUSTER_SYSTEM_CONFIG_CHANGED = 5077;
  
  public static final int ERROR_CLUSTER_RESOURCE_TYPE_NOT_FOUND = 5078;
  
  public static final int ERROR_CLUSTER_RESTYPE_NOT_SUPPORTED = 5079;
  
  public static final int ERROR_CLUSTER_RESNAME_NOT_FOUND = 5080;
  
  public static final int ERROR_CLUSTER_NO_RPC_PACKAGES_REGISTERED = 5081;
  
  public static final int ERROR_CLUSTER_OWNER_NOT_IN_PREFLIST = 5082;
  
  public static final int ERROR_CLUSTER_DATABASE_SEQMISMATCH = 5083;
  
  public static final int ERROR_RESMON_INVALID_STATE = 5084;
  
  public static final int ERROR_CLUSTER_GUM_NOT_LOCKER = 5085;
  
  public static final int ERROR_QUORUM_DISK_NOT_FOUND = 5086;
  
  public static final int ERROR_DATABASE_BACKUP_CORRUPT = 5087;
  
  public static final int ERROR_CLUSTER_NODE_ALREADY_HAS_DFS_ROOT = 5088;
  
  public static final int ERROR_RESOURCE_PROPERTY_UNCHANGEABLE = 5089;
  
  public static final int ERROR_CLUSTER_MEMBERSHIP_INVALID_STATE = 5890;
  
  public static final int ERROR_CLUSTER_QUORUMLOG_NOT_FOUND = 5891;
  
  public static final int ERROR_CLUSTER_MEMBERSHIP_HALT = 5892;
  
  public static final int ERROR_CLUSTER_INSTANCE_ID_MISMATCH = 5893;
  
  public static final int ERROR_CLUSTER_NETWORK_NOT_FOUND_FOR_IP = 5894;
  
  public static final int ERROR_CLUSTER_PROPERTY_DATA_TYPE_MISMATCH = 5895;
  
  public static final int ERROR_CLUSTER_EVICT_WITHOUT_CLEANUP = 5896;
  
  public static final int ERROR_CLUSTER_PARAMETER_MISMATCH = 5897;
  
  public static final int ERROR_NODE_CANNOT_BE_CLUSTERED = 5898;
  
  public static final int ERROR_CLUSTER_WRONG_OS_VERSION = 5899;
  
  public static final int ERROR_CLUSTER_CANT_CREATE_DUP_CLUSTER_NAME = 5900;
  
  public static final int ERROR_CLUSCFG_ALREADY_COMMITTED = 5901;
  
  public static final int ERROR_CLUSCFG_ROLLBACK_FAILED = 5902;
  
  public static final int ERROR_CLUSCFG_SYSTEM_DISK_DRIVE_LETTER_CONFLICT = 5903;
  
  public static final int ERROR_CLUSTER_OLD_VERSION = 5904;
  
  public static final int ERROR_CLUSTER_MISMATCHED_COMPUTER_ACCT_NAME = 5905;
  
  public static final int ERROR_CLUSTER_NO_NET_ADAPTERS = 5906;
  
  public static final int ERROR_CLUSTER_POISONED = 5907;
  
  public static final int ERROR_CLUSTER_GROUP_MOVING = 5908;
  
  public static final int ERROR_CLUSTER_RESOURCE_TYPE_BUSY = 5909;
  
  public static final int ERROR_RESOURCE_CALL_TIMED_OUT = 5910;
  
  public static final int ERROR_INVALID_CLUSTER_IPV6_ADDRESS = 5911;
  
  public static final int ERROR_CLUSTER_INTERNAL_INVALID_FUNCTION = 5912;
  
  public static final int ERROR_CLUSTER_PARAMETER_OUT_OF_BOUNDS = 5913;
  
  public static final int ERROR_CLUSTER_PARTIAL_SEND = 5914;
  
  public static final int ERROR_CLUSTER_REGISTRY_INVALID_FUNCTION = 5915;
  
  public static final int ERROR_CLUSTER_INVALID_STRING_TERMINATION = 5916;
  
  public static final int ERROR_CLUSTER_INVALID_STRING_FORMAT = 5917;
  
  public static final int ERROR_CLUSTER_DATABASE_TRANSACTION_IN_PROGRESS = 5918;
  
  public static final int ERROR_CLUSTER_DATABASE_TRANSACTION_NOT_IN_PROGRESS = 5919;
  
  public static final int ERROR_CLUSTER_NULL_DATA = 5920;
  
  public static final int ERROR_CLUSTER_PARTIAL_READ = 5921;
  
  public static final int ERROR_CLUSTER_PARTIAL_WRITE = 5922;
  
  public static final int ERROR_CLUSTER_CANT_DESERIALIZE_DATA = 5923;
  
  public static final int ERROR_DEPENDENT_RESOURCE_PROPERTY_CONFLICT = 5924;
  
  public static final int ERROR_CLUSTER_NO_QUORUM = 5925;
  
  public static final int ERROR_CLUSTER_INVALID_IPV6_NETWORK = 5926;
  
  public static final int ERROR_CLUSTER_INVALID_IPV6_TUNNEL_NETWORK = 5927;
  
  public static final int ERROR_QUORUM_NOT_ALLOWED_IN_THIS_GROUP = 5928;
  
  public static final int ERROR_DEPENDENCY_TREE_TOO_COMPLEX = 5929;
  
  public static final int ERROR_EXCEPTION_IN_RESOURCE_CALL = 5930;
  
  public static final int ERROR_CLUSTER_RHS_FAILED_INITIALIZATION = 5931;
  
  public static final int ERROR_CLUSTER_NOT_INSTALLED = 5932;
  
  public static final int ERROR_CLUSTER_RESOURCES_MUST_BE_ONLINE_ON_THE_SAME_NODE = 5933;
  
  public static final int ERROR_ENCRYPTION_FAILED = 6000;
  
  public static final int ERROR_DECRYPTION_FAILED = 6001;
  
  public static final int ERROR_FILE_ENCRYPTED = 6002;
  
  public static final int ERROR_NO_RECOVERY_POLICY = 6003;
  
  public static final int ERROR_NO_EFS = 6004;
  
  public static final int ERROR_WRONG_EFS = 6005;
  
  public static final int ERROR_NO_USER_KEYS = 6006;
  
  public static final int ERROR_FILE_NOT_ENCRYPTED = 6007;
  
  public static final int ERROR_NOT_EXPORT_FORMAT = 6008;
  
  public static final int ERROR_FILE_READ_ONLY = 6009;
  
  public static final int ERROR_DIR_EFS_DISALLOWED = 6010;
  
  public static final int ERROR_EFS_SERVER_NOT_TRUSTED = 6011;
  
  public static final int ERROR_BAD_RECOVERY_POLICY = 6012;
  
  public static final int ERROR_EFS_ALG_BLOB_TOO_BIG = 6013;
  
  public static final int ERROR_VOLUME_NOT_SUPPORT_EFS = 6014;
  
  public static final int ERROR_EFS_DISABLED = 6015;
  
  public static final int ERROR_EFS_VERSION_NOT_SUPPORT = 6016;
  
  public static final int ERROR_CS_ENCRYPTION_INVALID_SERVER_RESPONSE = 6017;
  
  public static final int ERROR_CS_ENCRYPTION_UNSUPPORTED_SERVER = 6018;
  
  public static final int ERROR_CS_ENCRYPTION_EXISTING_ENCRYPTED_FILE = 6019;
  
  public static final int ERROR_CS_ENCRYPTION_NEW_ENCRYPTED_FILE = 6020;
  
  public static final int ERROR_CS_ENCRYPTION_FILE_NOT_CSE = 6021;
  
  public static final int ERROR_NO_BROWSER_SERVERS_FOUND = 6118;
  
  public static final int SCHED_E_SERVICE_NOT_LOCALSYSTEM = 6200;
  
  public static final int ERROR_LOG_SECTOR_INVALID = 6600;
  
  public static final int ERROR_LOG_SECTOR_PARITY_INVALID = 6601;
  
  public static final int ERROR_LOG_SECTOR_REMAPPED = 6602;
  
  public static final int ERROR_LOG_BLOCK_INCOMPLETE = 6603;
  
  public static final int ERROR_LOG_INVALID_RANGE = 6604;
  
  public static final int ERROR_LOG_BLOCKS_EXHAUSTED = 6605;
  
  public static final int ERROR_LOG_READ_CONTEXT_INVALID = 6606;
  
  public static final int ERROR_LOG_RESTART_INVALID = 6607;
  
  public static final int ERROR_LOG_BLOCK_VERSION = 6608;
  
  public static final int ERROR_LOG_BLOCK_INVALID = 6609;
  
  public static final int ERROR_LOG_READ_MODE_INVALID = 6610;
  
  public static final int ERROR_LOG_NO_RESTART = 6611;
  
  public static final int ERROR_LOG_METADATA_CORRUPT = 6612;
  
  public static final int ERROR_LOG_METADATA_INVALID = 6613;
  
  public static final int ERROR_LOG_METADATA_INCONSISTENT = 6614;
  
  public static final int ERROR_LOG_RESERVATION_INVALID = 6615;
  
  public static final int ERROR_LOG_CANT_DELETE = 6616;
  
  public static final int ERROR_LOG_CONTAINER_LIMIT_EXCEEDED = 6617;
  
  public static final int ERROR_LOG_START_OF_LOG = 6618;
  
  public static final int ERROR_LOG_POLICY_ALREADY_INSTALLED = 6619;
  
  public static final int ERROR_LOG_POLICY_NOT_INSTALLED = 6620;
  
  public static final int ERROR_LOG_POLICY_INVALID = 6621;
  
  public static final int ERROR_LOG_POLICY_CONFLICT = 6622;
  
  public static final int ERROR_LOG_PINNED_ARCHIVE_TAIL = 6623;
  
  public static final int ERROR_LOG_RECORD_NONEXISTENT = 6624;
  
  public static final int ERROR_LOG_RECORDS_RESERVED_INVALID = 6625;
  
  public static final int ERROR_LOG_SPACE_RESERVED_INVALID = 6626;
  
  public static final int ERROR_LOG_TAIL_INVALID = 6627;
  
  public static final int ERROR_LOG_FULL = 6628;
  
  public static final int ERROR_COULD_NOT_RESIZE_LOG = 6629;
  
  public static final int ERROR_LOG_MULTIPLEXED = 6630;
  
  public static final int ERROR_LOG_DEDICATED = 6631;
  
  public static final int ERROR_LOG_ARCHIVE_NOT_IN_PROGRESS = 6632;
  
  public static final int ERROR_LOG_ARCHIVE_IN_PROGRESS = 6633;
  
  public static final int ERROR_LOG_EPHEMERAL = 6634;
  
  public static final int ERROR_LOG_NOT_ENOUGH_CONTAINERS = 6635;
  
  public static final int ERROR_LOG_CLIENT_ALREADY_REGISTERED = 6636;
  
  public static final int ERROR_LOG_CLIENT_NOT_REGISTERED = 6637;
  
  public static final int ERROR_LOG_FULL_HANDLER_IN_PROGRESS = 6638;
  
  public static final int ERROR_LOG_CONTAINER_READ_FAILED = 6639;
  
  public static final int ERROR_LOG_CONTAINER_WRITE_FAILED = 6640;
  
  public static final int ERROR_LOG_CONTAINER_OPEN_FAILED = 6641;
  
  public static final int ERROR_LOG_CONTAINER_STATE_INVALID = 6642;
  
  public static final int ERROR_LOG_STATE_INVALID = 6643;
  
  public static final int ERROR_LOG_PINNED = 6644;
  
  public static final int ERROR_LOG_METADATA_FLUSH_FAILED = 6645;
  
  public static final int ERROR_LOG_INCONSISTENT_SECURITY = 6646;
  
  public static final int ERROR_LOG_APPENDED_FLUSH_FAILED = 6647;
  
  public static final int ERROR_LOG_PINNED_RESERVATION = 6648;
  
  public static final int ERROR_INVALID_TRANSACTION = 6700;
  
  public static final int ERROR_TRANSACTION_NOT_ACTIVE = 6701;
  
  public static final int ERROR_TRANSACTION_REQUEST_NOT_VALID = 6702;
  
  public static final int ERROR_TRANSACTION_NOT_REQUESTED = 6703;
  
  public static final int ERROR_TRANSACTION_ALREADY_ABORTED = 6704;
  
  public static final int ERROR_TRANSACTION_ALREADY_COMMITTED = 6705;
  
  public static final int ERROR_TM_INITIALIZATION_FAILED = 6706;
  
  public static final int ERROR_RESOURCEMANAGER_READ_ONLY = 6707;
  
  public static final int ERROR_TRANSACTION_NOT_JOINED = 6708;
  
  public static final int ERROR_TRANSACTION_SUPERIOR_EXISTS = 6709;
  
  public static final int ERROR_CRM_PROTOCOL_ALREADY_EXISTS = 6710;
  
  public static final int ERROR_TRANSACTION_PROPAGATION_FAILED = 6711;
  
  public static final int ERROR_CRM_PROTOCOL_NOT_FOUND = 6712;
  
  public static final int ERROR_TRANSACTION_INVALID_MARSHALL_BUFFER = 6713;
  
  public static final int ERROR_CURRENT_TRANSACTION_NOT_VALID = 6714;
  
  public static final int ERROR_TRANSACTION_NOT_FOUND = 6715;
  
  public static final int ERROR_RESOURCEMANAGER_NOT_FOUND = 6716;
  
  public static final int ERROR_ENLISTMENT_NOT_FOUND = 6717;
  
  public static final int ERROR_TRANSACTIONMANAGER_NOT_FOUND = 6718;
  
  public static final int ERROR_TRANSACTIONMANAGER_NOT_ONLINE = 6719;
  
  public static final int ERROR_TRANSACTIONMANAGER_RECOVERY_NAME_COLLISION = 6720;
  
  public static final int ERROR_TRANSACTION_NOT_ROOT = 6721;
  
  public static final int ERROR_TRANSACTION_OBJECT_EXPIRED = 6722;
  
  public static final int ERROR_TRANSACTION_RESPONSE_NOT_ENLISTED = 6723;
  
  public static final int ERROR_TRANSACTION_RECORD_TOO_LONG = 6724;
  
  public static final int ERROR_IMPLICIT_TRANSACTION_NOT_SUPPORTED = 6725;
  
  public static final int ERROR_TRANSACTION_INTEGRITY_VIOLATED = 6726;
  
  public static final int ERROR_TRANSACTIONAL_CONFLICT = 6800;
  
  public static final int ERROR_RM_NOT_ACTIVE = 6801;
  
  public static final int ERROR_RM_METADATA_CORRUPT = 6802;
  
  public static final int ERROR_DIRECTORY_NOT_RM = 6803;
  
  public static final int ERROR_TRANSACTIONS_UNSUPPORTED_REMOTE = 6805;
  
  public static final int ERROR_LOG_RESIZE_INVALID_SIZE = 6806;
  
  public static final int ERROR_OBJECT_NO_LONGER_EXISTS = 6807;
  
  public static final int ERROR_STREAM_MINIVERSION_NOT_FOUND = 6808;
  
  public static final int ERROR_STREAM_MINIVERSION_NOT_VALID = 6809;
  
  public static final int ERROR_MINIVERSION_INACCESSIBLE_FROM_SPECIFIED_TRANSACTION = 6810;
  
  public static final int ERROR_CANT_OPEN_MINIVERSION_WITH_MODIFY_INTENT = 6811;
  
  public static final int ERROR_CANT_CREATE_MORE_STREAM_MINIVERSIONS = 6812;
  
  public static final int ERROR_REMOTE_FILE_VERSION_MISMATCH = 6814;
  
  public static final int ERROR_HANDLE_NO_LONGER_VALID = 6815;
  
  public static final int ERROR_NO_TXF_METADATA = 6816;
  
  public static final int ERROR_LOG_CORRUPTION_DETECTED = 6817;
  
  public static final int ERROR_CANT_RECOVER_WITH_HANDLE_OPEN = 6818;
  
  public static final int ERROR_RM_DISCONNECTED = 6819;
  
  public static final int ERROR_ENLISTMENT_NOT_SUPERIOR = 6820;
  
  public static final int ERROR_RECOVERY_NOT_NEEDED = 6821;
  
  public static final int ERROR_RM_ALREADY_STARTED = 6822;
  
  public static final int ERROR_FILE_IDENTITY_NOT_PERSISTENT = 6823;
  
  public static final int ERROR_CANT_BREAK_TRANSACTIONAL_DEPENDENCY = 6824;
  
  public static final int ERROR_CANT_CROSS_RM_BOUNDARY = 6825;
  
  public static final int ERROR_TXF_DIR_NOT_EMPTY = 6826;
  
  public static final int ERROR_INDOUBT_TRANSACTIONS_EXIST = 6827;
  
  public static final int ERROR_TM_VOLATILE = 6828;
  
  public static final int ERROR_ROLLBACK_TIMER_EXPIRED = 6829;
  
  public static final int ERROR_TXF_ATTRIBUTE_CORRUPT = 6830;
  
  public static final int ERROR_EFS_NOT_ALLOWED_IN_TRANSACTION = 6831;
  
  public static final int ERROR_TRANSACTIONAL_OPEN_NOT_ALLOWED = 6832;
  
  public static final int ERROR_LOG_GROWTH_FAILED = 6833;
  
  public static final int ERROR_TRANSACTED_MAPPING_UNSUPPORTED_REMOTE = 6834;
  
  public static final int ERROR_TXF_METADATA_ALREADY_PRESENT = 6835;
  
  public static final int ERROR_TRANSACTION_SCOPE_CALLBACKS_NOT_SET = 6836;
  
  public static final int ERROR_TRANSACTION_REQUIRED_PROMOTION = 6837;
  
  public static final int ERROR_CANNOT_EXECUTE_FILE_IN_TRANSACTION = 6838;
  
  public static final int ERROR_TRANSACTIONS_NOT_FROZEN = 6839;
  
  public static final int ERROR_TRANSACTION_FREEZE_IN_PROGRESS = 6840;
  
  public static final int ERROR_NOT_SNAPSHOT_VOLUME = 6841;
  
  public static final int ERROR_NO_SAVEPOINT_WITH_OPEN_FILES = 6842;
  
  public static final int ERROR_DATA_LOST_REPAIR = 6843;
  
  public static final int ERROR_SPARSE_NOT_ALLOWED_IN_TRANSACTION = 6844;
  
  public static final int ERROR_TM_IDENTITY_MISMATCH = 6845;
  
  public static final int ERROR_FLOATED_SECTION = 6846;
  
  public static final int ERROR_CANNOT_ACCEPT_TRANSACTED_WORK = 6847;
  
  public static final int ERROR_CANNOT_ABORT_TRANSACTIONS = 6848;
  
  public static final int ERROR_BAD_CLUSTERS = 6849;
  
  public static final int ERROR_COMPRESSION_NOT_ALLOWED_IN_TRANSACTION = 6850;
  
  public static final int ERROR_VOLUME_DIRTY = 6851;
  
  public static final int ERROR_NO_LINK_TRACKING_IN_TRANSACTION = 6852;
  
  public static final int ERROR_OPERATION_NOT_SUPPORTED_IN_TRANSACTION = 6853;
  
  public static final int ERROR_CTX_WINSTATION_NAME_INVALID = 7001;
  
  public static final int ERROR_CTX_INVALID_PD = 7002;
  
  public static final int ERROR_CTX_PD_NOT_FOUND = 7003;
  
  public static final int ERROR_CTX_WD_NOT_FOUND = 7004;
  
  public static final int ERROR_CTX_CANNOT_MAKE_EVENTLOG_ENTRY = 7005;
  
  public static final int ERROR_CTX_SERVICE_NAME_COLLISION = 7006;
  
  public static final int ERROR_CTX_CLOSE_PENDING = 7007;
  
  public static final int ERROR_CTX_NO_OUTBUF = 7008;
  
  public static final int ERROR_CTX_MODEM_INF_NOT_FOUND = 7009;
  
  public static final int ERROR_CTX_INVALID_MODEMNAME = 7010;
  
  public static final int ERROR_CTX_MODEM_RESPONSE_ERROR = 7011;
  
  public static final int ERROR_CTX_MODEM_RESPONSE_TIMEOUT = 7012;
  
  public static final int ERROR_CTX_MODEM_RESPONSE_NO_CARRIER = 7013;
  
  public static final int ERROR_CTX_MODEM_RESPONSE_NO_DIALTONE = 7014;
  
  public static final int ERROR_CTX_MODEM_RESPONSE_BUSY = 7015;
  
  public static final int ERROR_CTX_MODEM_RESPONSE_VOICE = 7016;
  
  public static final int ERROR_CTX_TD_ERROR = 7017;
  
  public static final int ERROR_CTX_WINSTATION_NOT_FOUND = 7022;
  
  public static final int ERROR_CTX_WINSTATION_ALREADY_EXISTS = 7023;
  
  public static final int ERROR_CTX_WINSTATION_BUSY = 7024;
  
  public static final int ERROR_CTX_BAD_VIDEO_MODE = 7025;
  
  public static final int ERROR_CTX_GRAPHICS_INVALID = 7035;
  
  public static final int ERROR_CTX_LOGON_DISABLED = 7037;
  
  public static final int ERROR_CTX_NOT_CONSOLE = 7038;
  
  public static final int ERROR_CTX_CLIENT_QUERY_TIMEOUT = 7040;
  
  public static final int ERROR_CTX_CONSOLE_DISCONNECT = 7041;
  
  public static final int ERROR_CTX_CONSOLE_CONNECT = 7042;
  
  public static final int ERROR_CTX_SHADOW_DENIED = 7044;
  
  public static final int ERROR_CTX_WINSTATION_ACCESS_DENIED = 7045;
  
  public static final int ERROR_CTX_INVALID_WD = 7049;
  
  public static final int ERROR_CTX_SHADOW_INVALID = 7050;
  
  public static final int ERROR_CTX_SHADOW_DISABLED = 7051;
  
  public static final int ERROR_CTX_CLIENT_LICENSE_IN_USE = 7052;
  
  public static final int ERROR_CTX_CLIENT_LICENSE_NOT_SET = 7053;
  
  public static final int ERROR_CTX_LICENSE_NOT_AVAILABLE = 7054;
  
  public static final int ERROR_CTX_LICENSE_CLIENT_INVALID = 7055;
  
  public static final int ERROR_CTX_LICENSE_EXPIRED = 7056;
  
  public static final int ERROR_CTX_SHADOW_NOT_RUNNING = 7057;
  
  public static final int ERROR_CTX_SHADOW_ENDED_BY_MODE_CHANGE = 7058;
  
  public static final int ERROR_ACTIVATION_COUNT_EXCEEDED = 7059;
  
  public static final int ERROR_CTX_WINSTATIONS_DISABLED = 7060;
  
  public static final int ERROR_CTX_ENCRYPTION_LEVEL_REQUIRED = 7061;
  
  public static final int ERROR_CTX_SESSION_IN_USE = 7062;
  
  public static final int ERROR_CTX_NO_FORCE_LOGOFF = 7063;
  
  public static final int ERROR_CTX_ACCOUNT_RESTRICTION = 7064;
  
  public static final int ERROR_RDP_PROTOCOL_ERROR = 7065;
  
  public static final int ERROR_CTX_CDM_CONNECT = 7066;
  
  public static final int ERROR_CTX_CDM_DISCONNECT = 7067;
  
  public static final int ERROR_CTX_SECURITY_LAYER_ERROR = 7068;
  
  public static final int ERROR_TS_INCOMPATIBLE_SESSIONS = 7069;
  
  public static final int FRS_ERR_INVALID_API_SEQUENCE = 8001;
  
  public static final int FRS_ERR_STARTING_SERVICE = 8002;
  
  public static final int FRS_ERR_STOPPING_SERVICE = 8003;
  
  public static final int FRS_ERR_INTERNAL_API = 8004;
  
  public static final int FRS_ERR_INTERNAL = 8005;
  
  public static final int FRS_ERR_SERVICE_COMM = 8006;
  
  public static final int FRS_ERR_INSUFFICIENT_PRIV = 8007;
  
  public static final int FRS_ERR_AUTHENTICATION = 8008;
  
  public static final int FRS_ERR_PARENT_INSUFFICIENT_PRIV = 8009;
  
  public static final int FRS_ERR_PARENT_AUTHENTICATION = 8010;
  
  public static final int FRS_ERR_CHILD_TO_PARENT_COMM = 8011;
  
  public static final int FRS_ERR_PARENT_TO_CHILD_COMM = 8012;
  
  public static final int FRS_ERR_SYSVOL_POPULATE = 8013;
  
  public static final int FRS_ERR_SYSVOL_POPULATE_TIMEOUT = 8014;
  
  public static final int FRS_ERR_SYSVOL_IS_BUSY = 8015;
  
  public static final int FRS_ERR_SYSVOL_DEMOTE = 8016;
  
  public static final int FRS_ERR_INVALID_SERVICE_PARAMETER = 8017;
  
  public static final int DS_S_SUCCESS = 0;
  
  public static final int ERROR_DS_NOT_INSTALLED = 8200;
  
  public static final int ERROR_DS_MEMBERSHIP_EVALUATED_LOCALLY = 8201;
  
  public static final int ERROR_DS_NO_ATTRIBUTE_OR_VALUE = 8202;
  
  public static final int ERROR_DS_INVALID_ATTRIBUTE_SYNTAX = 8203;
  
  public static final int ERROR_DS_ATTRIBUTE_TYPE_UNDEFINED = 8204;
  
  public static final int ERROR_DS_ATTRIBUTE_OR_VALUE_EXISTS = 8205;
  
  public static final int ERROR_DS_BUSY = 8206;
  
  public static final int ERROR_DS_UNAVAILABLE = 8207;
  
  public static final int ERROR_DS_NO_RIDS_ALLOCATED = 8208;
  
  public static final int ERROR_DS_NO_MORE_RIDS = 8209;
  
  public static final int ERROR_DS_INCORRECT_ROLE_OWNER = 8210;
  
  public static final int ERROR_DS_RIDMGR_INIT_ERROR = 8211;
  
  public static final int ERROR_DS_OBJ_CLASS_VIOLATION = 8212;
  
  public static final int ERROR_DS_CANT_ON_NON_LEAF = 8213;
  
  public static final int ERROR_DS_CANT_ON_RDN = 8214;
  
  public static final int ERROR_DS_CANT_MOD_OBJ_CLASS = 8215;
  
  public static final int ERROR_DS_CROSS_DOM_MOVE_ERROR = 8216;
  
  public static final int ERROR_DS_GC_NOT_AVAILABLE = 8217;
  
  public static final int ERROR_SHARED_POLICY = 8218;
  
  public static final int ERROR_POLICY_OBJECT_NOT_FOUND = 8219;
  
  public static final int ERROR_POLICY_ONLY_IN_DS = 8220;
  
  public static final int ERROR_PROMOTION_ACTIVE = 8221;
  
  public static final int ERROR_NO_PROMOTION_ACTIVE = 8222;
  
  public static final int ERROR_DS_OPERATIONS_ERROR = 8224;
  
  public static final int ERROR_DS_PROTOCOL_ERROR = 8225;
  
  public static final int ERROR_DS_TIMELIMIT_EXCEEDED = 8226;
  
  public static final int ERROR_DS_SIZELIMIT_EXCEEDED = 8227;
  
  public static final int ERROR_DS_ADMIN_LIMIT_EXCEEDED = 8228;
  
  public static final int ERROR_DS_COMPARE_FALSE = 8229;
  
  public static final int ERROR_DS_COMPARE_TRUE = 8230;
  
  public static final int ERROR_DS_AUTH_METHOD_NOT_SUPPORTED = 8231;
  
  public static final int ERROR_DS_STRONG_AUTH_REQUIRED = 8232;
  
  public static final int ERROR_DS_INAPPROPRIATE_AUTH = 8233;
  
  public static final int ERROR_DS_AUTH_UNKNOWN = 8234;
  
  public static final int ERROR_DS_REFERRAL = 8235;
  
  public static final int ERROR_DS_UNAVAILABLE_CRIT_EXTENSION = 8236;
  
  public static final int ERROR_DS_CONFIDENTIALITY_REQUIRED = 8237;
  
  public static final int ERROR_DS_INAPPROPRIATE_MATCHING = 8238;
  
  public static final int ERROR_DS_CONSTRAINT_VIOLATION = 8239;
  
  public static final int ERROR_DS_NO_SUCH_OBJECT = 8240;
  
  public static final int ERROR_DS_ALIAS_PROBLEM = 8241;
  
  public static final int ERROR_DS_INVALID_DN_SYNTAX = 8242;
  
  public static final int ERROR_DS_IS_LEAF = 8243;
  
  public static final int ERROR_DS_ALIAS_DEREF_PROBLEM = 8244;
  
  public static final int ERROR_DS_UNWILLING_TO_PERFORM = 8245;
  
  public static final int ERROR_DS_LOOP_DETECT = 8246;
  
  public static final int ERROR_DS_NAMING_VIOLATION = 8247;
  
  public static final int ERROR_DS_OBJECT_RESULTS_TOO_LARGE = 8248;
  
  public static final int ERROR_DS_AFFECTS_MULTIPLE_DSAS = 8249;
  
  public static final int ERROR_DS_SERVER_DOWN = 8250;
  
  public static final int ERROR_DS_LOCAL_ERROR = 8251;
  
  public static final int ERROR_DS_ENCODING_ERROR = 8252;
  
  public static final int ERROR_DS_DECODING_ERROR = 8253;
  
  public static final int ERROR_DS_FILTER_UNKNOWN = 8254;
  
  public static final int ERROR_DS_PARAM_ERROR = 8255;
  
  public static final int ERROR_DS_NOT_SUPPORTED = 8256;
  
  public static final int ERROR_DS_NO_RESULTS_RETURNED = 8257;
  
  public static final int ERROR_DS_CONTROL_NOT_FOUND = 8258;
  
  public static final int ERROR_DS_CLIENT_LOOP = 8259;
  
  public static final int ERROR_DS_REFERRAL_LIMIT_EXCEEDED = 8260;
  
  public static final int ERROR_DS_SORT_CONTROL_MISSING = 8261;
  
  public static final int ERROR_DS_OFFSET_RANGE_ERROR = 8262;
  
  public static final int ERROR_DS_ROOT_MUST_BE_NC = 8301;
  
  public static final int ERROR_DS_ADD_REPLICA_INHIBITED = 8302;
  
  public static final int ERROR_DS_ATT_NOT_DEF_IN_SCHEMA = 8303;
  
  public static final int ERROR_DS_MAX_OBJ_SIZE_EXCEEDED = 8304;
  
  public static final int ERROR_DS_OBJ_STRING_NAME_EXISTS = 8305;
  
  public static final int ERROR_DS_NO_RDN_DEFINED_IN_SCHEMA = 8306;
  
  public static final int ERROR_DS_RDN_DOESNT_MATCH_SCHEMA = 8307;
  
  public static final int ERROR_DS_NO_REQUESTED_ATTS_FOUND = 8308;
  
  public static final int ERROR_DS_USER_BUFFER_TO_SMALL = 8309;
  
  public static final int ERROR_DS_ATT_IS_NOT_ON_OBJ = 8310;
  
  public static final int ERROR_DS_ILLEGAL_MOD_OPERATION = 8311;
  
  public static final int ERROR_DS_OBJ_TOO_LARGE = 8312;
  
  public static final int ERROR_DS_BAD_INSTANCE_TYPE = 8313;
  
  public static final int ERROR_DS_MASTERDSA_REQUIRED = 8314;
  
  public static final int ERROR_DS_OBJECT_CLASS_REQUIRED = 8315;
  
  public static final int ERROR_DS_MISSING_REQUIRED_ATT = 8316;
  
  public static final int ERROR_DS_ATT_NOT_DEF_FOR_CLASS = 8317;
  
  public static final int ERROR_DS_ATT_ALREADY_EXISTS = 8318;
  
  public static final int ERROR_DS_CANT_ADD_ATT_VALUES = 8320;
  
  public static final int ERROR_DS_SINGLE_VALUE_CONSTRAINT = 8321;
  
  public static final int ERROR_DS_RANGE_CONSTRAINT = 8322;
  
  public static final int ERROR_DS_ATT_VAL_ALREADY_EXISTS = 8323;
  
  public static final int ERROR_DS_CANT_REM_MISSING_ATT = 8324;
  
  public static final int ERROR_DS_CANT_REM_MISSING_ATT_VAL = 8325;
  
  public static final int ERROR_DS_ROOT_CANT_BE_SUBREF = 8326;
  
  public static final int ERROR_DS_NO_CHAINING = 8327;
  
  public static final int ERROR_DS_NO_CHAINED_EVAL = 8328;
  
  public static final int ERROR_DS_NO_PARENT_OBJECT = 8329;
  
  public static final int ERROR_DS_PARENT_IS_AN_ALIAS = 8330;
  
  public static final int ERROR_DS_CANT_MIX_MASTER_AND_REPS = 8331;
  
  public static final int ERROR_DS_CHILDREN_EXIST = 8332;
  
  public static final int ERROR_DS_OBJ_NOT_FOUND = 8333;
  
  public static final int ERROR_DS_ALIASED_OBJ_MISSING = 8334;
  
  public static final int ERROR_DS_BAD_NAME_SYNTAX = 8335;
  
  public static final int ERROR_DS_ALIAS_POINTS_TO_ALIAS = 8336;
  
  public static final int ERROR_DS_CANT_DEREF_ALIAS = 8337;
  
  public static final int ERROR_DS_OUT_OF_SCOPE = 8338;
  
  public static final int ERROR_DS_OBJECT_BEING_REMOVED = 8339;
  
  public static final int ERROR_DS_CANT_DELETE_DSA_OBJ = 8340;
  
  public static final int ERROR_DS_GENERIC_ERROR = 8341;
  
  public static final int ERROR_DS_DSA_MUST_BE_INT_MASTER = 8342;
  
  public static final int ERROR_DS_CLASS_NOT_DSA = 8343;
  
  public static final int ERROR_DS_INSUFF_ACCESS_RIGHTS = 8344;
  
  public static final int ERROR_DS_ILLEGAL_SUPERIOR = 8345;
  
  public static final int ERROR_DS_ATTRIBUTE_OWNED_BY_SAM = 8346;
  
  public static final int ERROR_DS_NAME_TOO_MANY_PARTS = 8347;
  
  public static final int ERROR_DS_NAME_TOO_LONG = 8348;
  
  public static final int ERROR_DS_NAME_VALUE_TOO_LONG = 8349;
  
  public static final int ERROR_DS_NAME_UNPARSEABLE = 8350;
  
  public static final int ERROR_DS_NAME_TYPE_UNKNOWN = 8351;
  
  public static final int ERROR_DS_NOT_AN_OBJECT = 8352;
  
  public static final int ERROR_DS_SEC_DESC_TOO_SHORT = 8353;
  
  public static final int ERROR_DS_SEC_DESC_INVALID = 8354;
  
  public static final int ERROR_DS_NO_DELETED_NAME = 8355;
  
  public static final int ERROR_DS_SUBREF_MUST_HAVE_PARENT = 8356;
  
  public static final int ERROR_DS_NCNAME_MUST_BE_NC = 8357;
  
  public static final int ERROR_DS_CANT_ADD_SYSTEM_ONLY = 8358;
  
  public static final int ERROR_DS_CLASS_MUST_BE_CONCRETE = 8359;
  
  public static final int ERROR_DS_INVALID_DMD = 8360;
  
  public static final int ERROR_DS_OBJ_GUID_EXISTS = 8361;
  
  public static final int ERROR_DS_NOT_ON_BACKLINK = 8362;
  
  public static final int ERROR_DS_NO_CROSSREF_FOR_NC = 8363;
  
  public static final int ERROR_DS_SHUTTING_DOWN = 8364;
  
  public static final int ERROR_DS_UNKNOWN_OPERATION = 8365;
  
  public static final int ERROR_DS_INVALID_ROLE_OWNER = 8366;
  
  public static final int ERROR_DS_COULDNT_CONTACT_FSMO = 8367;
  
  public static final int ERROR_DS_CROSS_NC_DN_RENAME = 8368;
  
  public static final int ERROR_DS_CANT_MOD_SYSTEM_ONLY = 8369;
  
  public static final int ERROR_DS_REPLICATOR_ONLY = 8370;
  
  public static final int ERROR_DS_OBJ_CLASS_NOT_DEFINED = 8371;
  
  public static final int ERROR_DS_OBJ_CLASS_NOT_SUBCLASS = 8372;
  
  public static final int ERROR_DS_NAME_REFERENCE_INVALID = 8373;
  
  public static final int ERROR_DS_CROSS_REF_EXISTS = 8374;
  
  public static final int ERROR_DS_CANT_DEL_MASTER_CROSSREF = 8375;
  
  public static final int ERROR_DS_SUBTREE_NOTIFY_NOT_NC_HEAD = 8376;
  
  public static final int ERROR_DS_NOTIFY_FILTER_TOO_COMPLEX = 8377;
  
  public static final int ERROR_DS_DUP_RDN = 8378;
  
  public static final int ERROR_DS_DUP_OID = 8379;
  
  public static final int ERROR_DS_DUP_MAPI_ID = 8380;
  
  public static final int ERROR_DS_DUP_SCHEMA_ID_GUID = 8381;
  
  public static final int ERROR_DS_DUP_LDAP_DISPLAY_NAME = 8382;
  
  public static final int ERROR_DS_SEMANTIC_ATT_TEST = 8383;
  
  public static final int ERROR_DS_SYNTAX_MISMATCH = 8384;
  
  public static final int ERROR_DS_EXISTS_IN_MUST_HAVE = 8385;
  
  public static final int ERROR_DS_EXISTS_IN_MAY_HAVE = 8386;
  
  public static final int ERROR_DS_NONEXISTENT_MAY_HAVE = 8387;
  
  public static final int ERROR_DS_NONEXISTENT_MUST_HAVE = 8388;
  
  public static final int ERROR_DS_AUX_CLS_TEST_FAIL = 8389;
  
  public static final int ERROR_DS_NONEXISTENT_POSS_SUP = 8390;
  
  public static final int ERROR_DS_SUB_CLS_TEST_FAIL = 8391;
  
  public static final int ERROR_DS_BAD_RDN_ATT_ID_SYNTAX = 8392;
  
  public static final int ERROR_DS_EXISTS_IN_AUX_CLS = 8393;
  
  public static final int ERROR_DS_EXISTS_IN_SUB_CLS = 8394;
  
  public static final int ERROR_DS_EXISTS_IN_POSS_SUP = 8395;
  
  public static final int ERROR_DS_RECALCSCHEMA_FAILED = 8396;
  
  public static final int ERROR_DS_TREE_DELETE_NOT_FINISHED = 8397;
  
  public static final int ERROR_DS_CANT_DELETE = 8398;
  
  public static final int ERROR_DS_ATT_SCHEMA_REQ_ID = 8399;
  
  public static final int ERROR_DS_BAD_ATT_SCHEMA_SYNTAX = 8400;
  
  public static final int ERROR_DS_CANT_CACHE_ATT = 8401;
  
  public static final int ERROR_DS_CANT_CACHE_CLASS = 8402;
  
  public static final int ERROR_DS_CANT_REMOVE_ATT_CACHE = 8403;
  
  public static final int ERROR_DS_CANT_REMOVE_CLASS_CACHE = 8404;
  
  public static final int ERROR_DS_CANT_RETRIEVE_DN = 8405;
  
  public static final int ERROR_DS_MISSING_SUPREF = 8406;
  
  public static final int ERROR_DS_CANT_RETRIEVE_INSTANCE = 8407;
  
  public static final int ERROR_DS_CODE_INCONSISTENCY = 8408;
  
  public static final int ERROR_DS_DATABASE_ERROR = 8409;
  
  public static final int ERROR_DS_GOVERNSID_MISSING = 8410;
  
  public static final int ERROR_DS_MISSING_EXPECTED_ATT = 8411;
  
  public static final int ERROR_DS_NCNAME_MISSING_CR_REF = 8412;
  
  public static final int ERROR_DS_SECURITY_CHECKING_ERROR = 8413;
  
  public static final int ERROR_DS_SCHEMA_NOT_LOADED = 8414;
  
  public static final int ERROR_DS_SCHEMA_ALLOC_FAILED = 8415;
  
  public static final int ERROR_DS_ATT_SCHEMA_REQ_SYNTAX = 8416;
  
  public static final int ERROR_DS_GCVERIFY_ERROR = 8417;
  
  public static final int ERROR_DS_DRA_SCHEMA_MISMATCH = 8418;
  
  public static final int ERROR_DS_CANT_FIND_DSA_OBJ = 8419;
  
  public static final int ERROR_DS_CANT_FIND_EXPECTED_NC = 8420;
  
  public static final int ERROR_DS_CANT_FIND_NC_IN_CACHE = 8421;
  
  public static final int ERROR_DS_CANT_RETRIEVE_CHILD = 8422;
  
  public static final int ERROR_DS_SECURITY_ILLEGAL_MODIFY = 8423;
  
  public static final int ERROR_DS_CANT_REPLACE_HIDDEN_REC = 8424;
  
  public static final int ERROR_DS_BAD_HIERARCHY_FILE = 8425;
  
  public static final int ERROR_DS_BUILD_HIERARCHY_TABLE_FAILED = 8426;
  
  public static final int ERROR_DS_CONFIG_PARAM_MISSING = 8427;
  
  public static final int ERROR_DS_COUNTING_AB_INDICES_FAILED = 8428;
  
  public static final int ERROR_DS_HIERARCHY_TABLE_MALLOC_FAILED = 8429;
  
  public static final int ERROR_DS_INTERNAL_FAILURE = 8430;
  
  public static final int ERROR_DS_UNKNOWN_ERROR = 8431;
  
  public static final int ERROR_DS_ROOT_REQUIRES_CLASS_TOP = 8432;
  
  public static final int ERROR_DS_REFUSING_FSMO_ROLES = 8433;
  
  public static final int ERROR_DS_MISSING_FSMO_SETTINGS = 8434;
  
  public static final int ERROR_DS_UNABLE_TO_SURRENDER_ROLES = 8435;
  
  public static final int ERROR_DS_DRA_GENERIC = 8436;
  
  public static final int ERROR_DS_DRA_INVALID_PARAMETER = 8437;
  
  public static final int ERROR_DS_DRA_BUSY = 8438;
  
  public static final int ERROR_DS_DRA_BAD_DN = 8439;
  
  public static final int ERROR_DS_DRA_BAD_NC = 8440;
  
  public static final int ERROR_DS_DRA_DN_EXISTS = 8441;
  
  public static final int ERROR_DS_DRA_INTERNAL_ERROR = 8442;
  
  public static final int ERROR_DS_DRA_INCONSISTENT_DIT = 8443;
  
  public static final int ERROR_DS_DRA_CONNECTION_FAILED = 8444;
  
  public static final int ERROR_DS_DRA_BAD_INSTANCE_TYPE = 8445;
  
  public static final int ERROR_DS_DRA_OUT_OF_MEM = 8446;
  
  public static final int ERROR_DS_DRA_MAIL_PROBLEM = 8447;
  
  public static final int ERROR_DS_DRA_REF_ALREADY_EXISTS = 8448;
  
  public static final int ERROR_DS_DRA_REF_NOT_FOUND = 8449;
  
  public static final int ERROR_DS_DRA_OBJ_IS_REP_SOURCE = 8450;
  
  public static final int ERROR_DS_DRA_DB_ERROR = 8451;
  
  public static final int ERROR_DS_DRA_NO_REPLICA = 8452;
  
  public static final int ERROR_DS_DRA_ACCESS_DENIED = 8453;
  
  public static final int ERROR_DS_DRA_NOT_SUPPORTED = 8454;
  
  public static final int ERROR_DS_DRA_RPC_CANCELLED = 8455;
  
  public static final int ERROR_DS_DRA_SOURCE_DISABLED = 8456;
  
  public static final int ERROR_DS_DRA_SINK_DISABLED = 8457;
  
  public static final int ERROR_DS_DRA_NAME_COLLISION = 8458;
  
  public static final int ERROR_DS_DRA_SOURCE_REINSTALLED = 8459;
  
  public static final int ERROR_DS_DRA_MISSING_PARENT = 8460;
  
  public static final int ERROR_DS_DRA_PREEMPTED = 8461;
  
  public static final int ERROR_DS_DRA_ABANDON_SYNC = 8462;
  
  public static final int ERROR_DS_DRA_SHUTDOWN = 8463;
  
  public static final int ERROR_DS_DRA_INCOMPATIBLE_PARTIAL_SET = 8464;
  
  public static final int ERROR_DS_DRA_SOURCE_IS_PARTIAL_REPLICA = 8465;
  
  public static final int ERROR_DS_DRA_EXTN_CONNECTION_FAILED = 8466;
  
  public static final int ERROR_DS_INSTALL_SCHEMA_MISMATCH = 8467;
  
  public static final int ERROR_DS_DUP_LINK_ID = 8468;
  
  public static final int ERROR_DS_NAME_ERROR_RESOLVING = 8469;
  
  public static final int ERROR_DS_NAME_ERROR_NOT_FOUND = 8470;
  
  public static final int ERROR_DS_NAME_ERROR_NOT_UNIQUE = 8471;
  
  public static final int ERROR_DS_NAME_ERROR_NO_MAPPING = 8472;
  
  public static final int ERROR_DS_NAME_ERROR_DOMAIN_ONLY = 8473;
  
  public static final int ERROR_DS_NAME_ERROR_NO_SYNTACTICAL_MAPPING = 8474;
  
  public static final int ERROR_DS_CONSTRUCTED_ATT_MOD = 8475;
  
  public static final int ERROR_DS_WRONG_OM_OBJ_CLASS = 8476;
  
  public static final int ERROR_DS_DRA_REPL_PENDING = 8477;
  
  public static final int ERROR_DS_DS_REQUIRED = 8478;
  
  public static final int ERROR_DS_INVALID_LDAP_DISPLAY_NAME = 8479;
  
  public static final int ERROR_DS_NON_BASE_SEARCH = 8480;
  
  public static final int ERROR_DS_CANT_RETRIEVE_ATTS = 8481;
  
  public static final int ERROR_DS_BACKLINK_WITHOUT_LINK = 8482;
  
  public static final int ERROR_DS_EPOCH_MISMATCH = 8483;
  
  public static final int ERROR_DS_SRC_NAME_MISMATCH = 8484;
  
  public static final int ERROR_DS_SRC_AND_DST_NC_IDENTICAL = 8485;
  
  public static final int ERROR_DS_DST_NC_MISMATCH = 8486;
  
  public static final int ERROR_DS_NOT_AUTHORITIVE_FOR_DST_NC = 8487;
  
  public static final int ERROR_DS_SRC_GUID_MISMATCH = 8488;
  
  public static final int ERROR_DS_CANT_MOVE_DELETED_OBJECT = 8489;
  
  public static final int ERROR_DS_PDC_OPERATION_IN_PROGRESS = 8490;
  
  public static final int ERROR_DS_CROSS_DOMAIN_CLEANUP_REQD = 8491;
  
  public static final int ERROR_DS_ILLEGAL_XDOM_MOVE_OPERATION = 8492;
  
  public static final int ERROR_DS_CANT_WITH_ACCT_GROUP_MEMBERSHPS = 8493;
  
  public static final int ERROR_DS_NC_MUST_HAVE_NC_PARENT = 8494;
  
  public static final int ERROR_DS_CR_IMPOSSIBLE_TO_VALIDATE = 8495;
  
  public static final int ERROR_DS_DST_DOMAIN_NOT_NATIVE = 8496;
  
  public static final int ERROR_DS_MISSING_INFRASTRUCTURE_CONTAINER = 8497;
  
  public static final int ERROR_DS_CANT_MOVE_ACCOUNT_GROUP = 8498;
  
  public static final int ERROR_DS_CANT_MOVE_RESOURCE_GROUP = 8499;
  
  public static final int ERROR_DS_INVALID_SEARCH_FLAG = 8500;
  
  public static final int ERROR_DS_NO_TREE_DELETE_ABOVE_NC = 8501;
  
  public static final int ERROR_DS_COULDNT_LOCK_TREE_FOR_DELETE = 8502;
  
  public static final int ERROR_DS_COULDNT_IDENTIFY_OBJECTS_FOR_TREE_DELETE = 8503;
  
  public static final int ERROR_DS_SAM_INIT_FAILURE = 8504;
  
  public static final int ERROR_DS_SENSITIVE_GROUP_VIOLATION = 8505;
  
  public static final int ERROR_DS_CANT_MOD_PRIMARYGROUPID = 8506;
  
  public static final int ERROR_DS_ILLEGAL_BASE_SCHEMA_MOD = 8507;
  
  public static final int ERROR_DS_NONSAFE_SCHEMA_CHANGE = 8508;
  
  public static final int ERROR_DS_SCHEMA_UPDATE_DISALLOWED = 8509;
  
  public static final int ERROR_DS_CANT_CREATE_UNDER_SCHEMA = 8510;
  
  public static final int ERROR_DS_INSTALL_NO_SRC_SCH_VERSION = 8511;
  
  public static final int ERROR_DS_INSTALL_NO_SCH_VERSION_IN_INIFILE = 8512;
  
  public static final int ERROR_DS_INVALID_GROUP_TYPE = 8513;
  
  public static final int ERROR_DS_NO_NEST_GLOBALGROUP_IN_MIXEDDOMAIN = 8514;
  
  public static final int ERROR_DS_NO_NEST_LOCALGROUP_IN_MIXEDDOMAIN = 8515;
  
  public static final int ERROR_DS_GLOBAL_CANT_HAVE_LOCAL_MEMBER = 8516;
  
  public static final int ERROR_DS_GLOBAL_CANT_HAVE_UNIVERSAL_MEMBER = 8517;
  
  public static final int ERROR_DS_UNIVERSAL_CANT_HAVE_LOCAL_MEMBER = 8518;
  
  public static final int ERROR_DS_GLOBAL_CANT_HAVE_CROSSDOMAIN_MEMBER = 8519;
  
  public static final int ERROR_DS_LOCAL_CANT_HAVE_CROSSDOMAIN_LOCAL_MEMBER = 8520;
  
  public static final int ERROR_DS_HAVE_PRIMARY_MEMBERS = 8521;
  
  public static final int ERROR_DS_STRING_SD_CONVERSION_FAILED = 8522;
  
  public static final int ERROR_DS_NAMING_MASTER_GC = 8523;
  
  public static final int ERROR_DS_DNS_LOOKUP_FAILURE = 8524;
  
  public static final int ERROR_DS_COULDNT_UPDATE_SPNS = 8525;
  
  public static final int ERROR_DS_CANT_RETRIEVE_SD = 8526;
  
  public static final int ERROR_DS_KEY_NOT_UNIQUE = 8527;
  
  public static final int ERROR_DS_WRONG_LINKED_ATT_SYNTAX = 8528;
  
  public static final int ERROR_DS_SAM_NEED_BOOTKEY_PASSWORD = 8529;
  
  public static final int ERROR_DS_SAM_NEED_BOOTKEY_FLOPPY = 8530;
  
  public static final int ERROR_DS_CANT_START = 8531;
  
  public static final int ERROR_DS_INIT_FAILURE = 8532;
  
  public static final int ERROR_DS_NO_PKT_PRIVACY_ON_CONNECTION = 8533;
  
  public static final int ERROR_DS_SOURCE_DOMAIN_IN_FOREST = 8534;
  
  public static final int ERROR_DS_DESTINATION_DOMAIN_NOT_IN_FOREST = 8535;
  
  public static final int ERROR_DS_DESTINATION_AUDITING_NOT_ENABLED = 8536;
  
  public static final int ERROR_DS_CANT_FIND_DC_FOR_SRC_DOMAIN = 8537;
  
  public static final int ERROR_DS_SRC_OBJ_NOT_GROUP_OR_USER = 8538;
  
  public static final int ERROR_DS_SRC_SID_EXISTS_IN_FOREST = 8539;
  
  public static final int ERROR_DS_SRC_AND_DST_OBJECT_CLASS_MISMATCH = 8540;
  
  public static final int ERROR_SAM_INIT_FAILURE = 8541;
  
  public static final int ERROR_DS_DRA_SCHEMA_INFO_SHIP = 8542;
  
  public static final int ERROR_DS_DRA_SCHEMA_CONFLICT = 8543;
  
  public static final int ERROR_DS_DRA_EARLIER_SCHEMA_CONFLICT = 8544;
  
  public static final int ERROR_DS_DRA_OBJ_NC_MISMATCH = 8545;
  
  public static final int ERROR_DS_NC_STILL_HAS_DSAS = 8546;
  
  public static final int ERROR_DS_GC_REQUIRED = 8547;
  
  public static final int ERROR_DS_LOCAL_MEMBER_OF_LOCAL_ONLY = 8548;
  
  public static final int ERROR_DS_NO_FPO_IN_UNIVERSAL_GROUPS = 8549;
  
  public static final int ERROR_DS_CANT_ADD_TO_GC = 8550;
  
  public static final int ERROR_DS_NO_CHECKPOINT_WITH_PDC = 8551;
  
  public static final int ERROR_DS_SOURCE_AUDITING_NOT_ENABLED = 8552;
  
  public static final int ERROR_DS_CANT_CREATE_IN_NONDOMAIN_NC = 8553;
  
  public static final int ERROR_DS_INVALID_NAME_FOR_SPN = 8554;
  
  public static final int ERROR_DS_FILTER_USES_CONTRUCTED_ATTRS = 8555;
  
  public static final int ERROR_DS_UNICODEPWD_NOT_IN_QUOTES = 8556;
  
  public static final int ERROR_DS_MACHINE_ACCOUNT_QUOTA_EXCEEDED = 8557;
  
  public static final int ERROR_DS_MUST_BE_RUN_ON_DST_DC = 8558;
  
  public static final int ERROR_DS_SRC_DC_MUST_BE_SP4_OR_GREATER = 8559;
  
  public static final int ERROR_DS_CANT_TREE_DELETE_CRITICAL_OBJ = 8560;
  
  public static final int ERROR_DS_INIT_FAILURE_CONSOLE = 8561;
  
  public static final int ERROR_DS_SAM_INIT_FAILURE_CONSOLE = 8562;
  
  public static final int ERROR_DS_FOREST_VERSION_TOO_HIGH = 8563;
  
  public static final int ERROR_DS_DOMAIN_VERSION_TOO_HIGH = 8564;
  
  public static final int ERROR_DS_FOREST_VERSION_TOO_LOW = 8565;
  
  public static final int ERROR_DS_DOMAIN_VERSION_TOO_LOW = 8566;
  
  public static final int ERROR_DS_INCOMPATIBLE_VERSION = 8567;
  
  public static final int ERROR_DS_LOW_DSA_VERSION = 8568;
  
  public static final int ERROR_DS_NO_BEHAVIOR_VERSION_IN_MIXEDDOMAIN = 8569;
  
  public static final int ERROR_DS_NOT_SUPPORTED_SORT_ORDER = 8570;
  
  public static final int ERROR_DS_NAME_NOT_UNIQUE = 8571;
  
  public static final int ERROR_DS_MACHINE_ACCOUNT_CREATED_PRENT4 = 8572;
  
  public static final int ERROR_DS_OUT_OF_VERSION_STORE = 8573;
  
  public static final int ERROR_DS_INCOMPATIBLE_CONTROLS_USED = 8574;
  
  public static final int ERROR_DS_NO_REF_DOMAIN = 8575;
  
  public static final int ERROR_DS_RESERVED_LINK_ID = 8576;
  
  public static final int ERROR_DS_LINK_ID_NOT_AVAILABLE = 8577;
  
  public static final int ERROR_DS_AG_CANT_HAVE_UNIVERSAL_MEMBER = 8578;
  
  public static final int ERROR_DS_MODIFYDN_DISALLOWED_BY_INSTANCE_TYPE = 8579;
  
  public static final int ERROR_DS_NO_OBJECT_MOVE_IN_SCHEMA_NC = 8580;
  
  public static final int ERROR_DS_MODIFYDN_DISALLOWED_BY_FLAG = 8581;
  
  public static final int ERROR_DS_MODIFYDN_WRONG_GRANDPARENT = 8582;
  
  public static final int ERROR_DS_NAME_ERROR_TRUST_REFERRAL = 8583;
  
  public static final int ERROR_NOT_SUPPORTED_ON_STANDARD_SERVER = 8584;
  
  public static final int ERROR_DS_CANT_ACCESS_REMOTE_PART_OF_AD = 8585;
  
  public static final int ERROR_DS_CR_IMPOSSIBLE_TO_VALIDATE_V2 = 8586;
  
  public static final int ERROR_DS_THREAD_LIMIT_EXCEEDED = 8587;
  
  public static final int ERROR_DS_NOT_CLOSEST = 8588;
  
  public static final int ERROR_DS_CANT_DERIVE_SPN_WITHOUT_SERVER_REF = 8589;
  
  public static final int ERROR_DS_SINGLE_USER_MODE_FAILED = 8590;
  
  public static final int ERROR_DS_NTDSCRIPT_SYNTAX_ERROR = 8591;
  
  public static final int ERROR_DS_NTDSCRIPT_PROCESS_ERROR = 8592;
  
  public static final int ERROR_DS_DIFFERENT_REPL_EPOCHS = 8593;
  
  public static final int ERROR_DS_DRS_EXTENSIONS_CHANGED = 8594;
  
  public static final int ERROR_DS_REPLICA_SET_CHANGE_NOT_ALLOWED_ON_DISABLED_CR = 8595;
  
  public static final int ERROR_DS_NO_MSDS_INTID = 8596;
  
  public static final int ERROR_DS_DUP_MSDS_INTID = 8597;
  
  public static final int ERROR_DS_EXISTS_IN_RDNATTID = 8598;
  
  public static final int ERROR_DS_AUTHORIZATION_FAILED = 8599;
  
  public static final int ERROR_DS_INVALID_SCRIPT = 8600;
  
  public static final int ERROR_DS_REMOTE_CROSSREF_OP_FAILED = 8601;
  
  public static final int ERROR_DS_CROSS_REF_BUSY = 8602;
  
  public static final int ERROR_DS_CANT_DERIVE_SPN_FOR_DELETED_DOMAIN = 8603;
  
  public static final int ERROR_DS_CANT_DEMOTE_WITH_WRITEABLE_NC = 8604;
  
  public static final int ERROR_DS_DUPLICATE_ID_FOUND = 8605;
  
  public static final int ERROR_DS_INSUFFICIENT_ATTR_TO_CREATE_OBJECT = 8606;
  
  public static final int ERROR_DS_GROUP_CONVERSION_ERROR = 8607;
  
  public static final int ERROR_DS_CANT_MOVE_APP_BASIC_GROUP = 8608;
  
  public static final int ERROR_DS_CANT_MOVE_APP_QUERY_GROUP = 8609;
  
  public static final int ERROR_DS_ROLE_NOT_VERIFIED = 8610;
  
  public static final int ERROR_DS_WKO_CONTAINER_CANNOT_BE_SPECIAL = 8611;
  
  public static final int ERROR_DS_DOMAIN_RENAME_IN_PROGRESS = 8612;
  
  public static final int ERROR_DS_EXISTING_AD_CHILD_NC = 8613;
  
  public static final int ERROR_DS_REPL_LIFETIME_EXCEEDED = 8614;
  
  public static final int ERROR_DS_DISALLOWED_IN_SYSTEM_CONTAINER = 8615;
  
  public static final int ERROR_DS_LDAP_SEND_QUEUE_FULL = 8616;
  
  public static final int ERROR_DS_DRA_OUT_SCHEDULE_WINDOW = 8617;
  
  public static final int ERROR_DS_POLICY_NOT_KNOWN = 8618;
  
  public static final int ERROR_NO_SITE_SETTINGS_OBJECT = 8619;
  
  public static final int ERROR_NO_SECRETS = 8620;
  
  public static final int ERROR_NO_WRITABLE_DC_FOUND = 8621;
  
  public static final int ERROR_DS_NO_SERVER_OBJECT = 8622;
  
  public static final int ERROR_DS_NO_NTDSA_OBJECT = 8623;
  
  public static final int ERROR_DS_NON_ASQ_SEARCH = 8624;
  
  public static final int ERROR_DS_AUDIT_FAILURE = 8625;
  
  public static final int ERROR_DS_INVALID_SEARCH_FLAG_SUBTREE = 8626;
  
  public static final int ERROR_DS_INVALID_SEARCH_FLAG_TUPLE = 8627;
  
  public static final int ERROR_DS_HIERARCHY_TABLE_TOO_DEEP = 8628;
  
  public static final int DNS_ERROR_RESPONSE_CODES_BASE = 9000;
  
  public static final int DNS_ERROR_RCODE_NO_ERROR = 0;
  
  public static final int DNS_ERROR_MASK = 9000;
  
  public static final int DNS_ERROR_RCODE_FORMAT_ERROR = 9001;
  
  public static final int DNS_ERROR_RCODE_SERVER_FAILURE = 9002;
  
  public static final int DNS_ERROR_RCODE_NAME_ERROR = 9003;
  
  public static final int DNS_ERROR_RCODE_NOT_IMPLEMENTED = 9004;
  
  public static final int DNS_ERROR_RCODE_REFUSED = 9005;
  
  public static final int DNS_ERROR_RCODE_YXDOMAIN = 9006;
  
  public static final int DNS_ERROR_RCODE_YXRRSET = 9007;
  
  public static final int DNS_ERROR_RCODE_NXRRSET = 9008;
  
  public static final int DNS_ERROR_RCODE_NOTAUTH = 9009;
  
  public static final int DNS_ERROR_RCODE_NOTZONE = 9010;
  
  public static final int DNS_ERROR_RCODE_BADSIG = 9016;
  
  public static final int DNS_ERROR_RCODE_BADKEY = 9017;
  
  public static final int DNS_ERROR_RCODE_BADTIME = 9018;
  
  public static final int DNS_ERROR_RCODE_LAST = 9018;
  
  public static final int DNS_ERROR_PACKET_FMT_BASE = 9500;
  
  public static final int DNS_INFO_NO_RECORDS = 9501;
  
  public static final int DNS_ERROR_BAD_PACKET = 9502;
  
  public static final int DNS_ERROR_NO_PACKET = 9503;
  
  public static final int DNS_ERROR_RCODE = 9504;
  
  public static final int DNS_ERROR_UNSECURE_PACKET = 9505;
  
  public static final int DNS_STATUS_PACKET_UNSECURE = 9505;
  
  public static final int DNS_ERROR_NO_MEMORY = 14;
  
  public static final int DNS_ERROR_INVALID_NAME = 123;
  
  public static final int DNS_ERROR_INVALID_DATA = 13;
  
  public static final int DNS_ERROR_GENERAL_API_BASE = 9550;
  
  public static final int DNS_ERROR_INVALID_TYPE = 9551;
  
  public static final int DNS_ERROR_INVALID_IP_ADDRESS = 9552;
  
  public static final int DNS_ERROR_INVALID_PROPERTY = 9553;
  
  public static final int DNS_ERROR_TRY_AGAIN_LATER = 9554;
  
  public static final int DNS_ERROR_NOT_UNIQUE = 9555;
  
  public static final int DNS_ERROR_NON_RFC_NAME = 9556;
  
  public static final int DNS_STATUS_FQDN = 9557;
  
  public static final int DNS_STATUS_DOTTED_NAME = 9558;
  
  public static final int DNS_STATUS_SINGLE_PART_NAME = 9559;
  
  public static final int DNS_ERROR_INVALID_NAME_CHAR = 9560;
  
  public static final int DNS_ERROR_NUMERIC_NAME = 9561;
  
  public static final int DNS_ERROR_NOT_ALLOWED_ON_ROOT_SERVER = 9562;
  
  public static final int DNS_ERROR_NOT_ALLOWED_UNDER_DELEGATION = 9563;
  
  public static final int DNS_ERROR_CANNOT_FIND_ROOT_HINTS = 9564;
  
  public static final int DNS_ERROR_INCONSISTENT_ROOT_HINTS = 9565;
  
  public static final int DNS_ERROR_DWORD_VALUE_TOO_SMALL = 9566;
  
  public static final int DNS_ERROR_DWORD_VALUE_TOO_LARGE = 9567;
  
  public static final int DNS_ERROR_BACKGROUND_LOADING = 9568;
  
  public static final int DNS_ERROR_NOT_ALLOWED_ON_RODC = 9569;
  
  public static final int DNS_ERROR_NOT_ALLOWED_UNDER_DNAME = 9570;
  
  public static final int DNS_ERROR_ZONE_BASE = 9600;
  
  public static final int DNS_ERROR_ZONE_DOES_NOT_EXIST = 9601;
  
  public static final int DNS_ERROR_NO_ZONE_INFO = 9602;
  
  public static final int DNS_ERROR_INVALID_ZONE_OPERATION = 9603;
  
  public static final int DNS_ERROR_ZONE_CONFIGURATION_ERROR = 9604;
  
  public static final int DNS_ERROR_ZONE_HAS_NO_SOA_RECORD = 9605;
  
  public static final int DNS_ERROR_ZONE_HAS_NO_NS_RECORDS = 9606;
  
  public static final int DNS_ERROR_ZONE_LOCKED = 9607;
  
  public static final int DNS_ERROR_ZONE_CREATION_FAILED = 9608;
  
  public static final int DNS_ERROR_ZONE_ALREADY_EXISTS = 9609;
  
  public static final int DNS_ERROR_AUTOZONE_ALREADY_EXISTS = 9610;
  
  public static final int DNS_ERROR_INVALID_ZONE_TYPE = 9611;
  
  public static final int DNS_ERROR_SECONDARY_REQUIRES_MASTER_IP = 9612;
  
  public static final int DNS_ERROR_ZONE_NOT_SECONDARY = 9613;
  
  public static final int DNS_ERROR_NEED_SECONDARY_ADDRESSES = 9614;
  
  public static final int DNS_ERROR_WINS_INIT_FAILED = 9615;
  
  public static final int DNS_ERROR_NEED_WINS_SERVERS = 9616;
  
  public static final int DNS_ERROR_NBSTAT_INIT_FAILED = 9617;
  
  public static final int DNS_ERROR_SOA_DELETE_INVALID = 9618;
  
  public static final int DNS_ERROR_FORWARDER_ALREADY_EXISTS = 9619;
  
  public static final int DNS_ERROR_ZONE_REQUIRES_MASTER_IP = 9620;
  
  public static final int DNS_ERROR_ZONE_IS_SHUTDOWN = 9621;
  
  public static final int DNS_ERROR_DATAFILE_BASE = 9650;
  
  public static final int DNS_ERROR_PRIMARY_REQUIRES_DATAFILE = 9651;
  
  public static final int DNS_ERROR_INVALID_DATAFILE_NAME = 9652;
  
  public static final int DNS_ERROR_DATAFILE_OPEN_FAILURE = 9653;
  
  public static final int DNS_ERROR_FILE_WRITEBACK_FAILED = 9654;
  
  public static final int DNS_ERROR_DATAFILE_PARSING = 9655;
  
  public static final int DNS_ERROR_DATABASE_BASE = 9700;
  
  public static final int DNS_ERROR_RECORD_DOES_NOT_EXIST = 9701;
  
  public static final int DNS_ERROR_RECORD_FORMAT = 9702;
  
  public static final int DNS_ERROR_NODE_CREATION_FAILED = 9703;
  
  public static final int DNS_ERROR_UNKNOWN_RECORD_TYPE = 9704;
  
  public static final int DNS_ERROR_RECORD_TIMED_OUT = 9705;
  
  public static final int DNS_ERROR_NAME_NOT_IN_ZONE = 9706;
  
  public static final int DNS_ERROR_CNAME_LOOP = 9707;
  
  public static final int DNS_ERROR_NODE_IS_CNAME = 9708;
  
  public static final int DNS_ERROR_CNAME_COLLISION = 9709;
  
  public static final int DNS_ERROR_RECORD_ONLY_AT_ZONE_ROOT = 9710;
  
  public static final int DNS_ERROR_RECORD_ALREADY_EXISTS = 9711;
  
  public static final int DNS_ERROR_SECONDARY_DATA = 9712;
  
  public static final int DNS_ERROR_NO_CREATE_CACHE_DATA = 9713;
  
  public static final int DNS_ERROR_NAME_DOES_NOT_EXIST = 9714;
  
  public static final int DNS_WARNING_PTR_CREATE_FAILED = 9715;
  
  public static final int DNS_WARNING_DOMAIN_UNDELETED = 9716;
  
  public static final int DNS_ERROR_DS_UNAVAILABLE = 9717;
  
  public static final int DNS_ERROR_DS_ZONE_ALREADY_EXISTS = 9718;
  
  public static final int DNS_ERROR_NO_BOOTFILE_IF_DS_ZONE = 9719;
  
  public static final int DNS_ERROR_NODE_IS_DNAME = 9720;
  
  public static final int DNS_ERROR_DNAME_COLLISION = 9721;
  
  public static final int DNS_ERROR_ALIAS_LOOP = 9722;
  
  public static final int DNS_ERROR_OPERATION_BASE = 9750;
  
  public static final int DNS_INFO_AXFR_COMPLETE = 9751;
  
  public static final int DNS_ERROR_AXFR = 9752;
  
  public static final int DNS_INFO_ADDED_LOCAL_WINS = 9753;
  
  public static final int DNS_ERROR_SECURE_BASE = 9800;
  
  public static final int DNS_STATUS_CONTINUE_NEEDED = 9801;
  
  public static final int DNS_ERROR_SETUP_BASE = 9850;
  
  public static final int DNS_ERROR_NO_TCPIP = 9851;
  
  public static final int DNS_ERROR_NO_DNS_SERVERS = 9852;
  
  public static final int DNS_ERROR_DP_BASE = 9900;
  
  public static final int DNS_ERROR_DP_DOES_NOT_EXIST = 9901;
  
  public static final int DNS_ERROR_DP_ALREADY_EXISTS = 9902;
  
  public static final int DNS_ERROR_DP_NOT_ENLISTED = 9903;
  
  public static final int DNS_ERROR_DP_ALREADY_ENLISTED = 9904;
  
  public static final int DNS_ERROR_DP_NOT_AVAILABLE = 9905;
  
  public static final int DNS_ERROR_DP_FSMO_ERROR = 9906;
  
  public static final int WSABASEERR = 10000;
  
  public static final int WSAEINTR = 10004;
  
  public static final int WSAEBADF = 10009;
  
  public static final int WSAEACCES = 10013;
  
  public static final int WSAEFAULT = 10014;
  
  public static final int WSAEINVAL = 10022;
  
  public static final int WSAEMFILE = 10024;
  
  public static final int WSAEWOULDBLOCK = 10035;
  
  public static final int WSAEINPROGRESS = 10036;
  
  public static final int WSAEALREADY = 10037;
  
  public static final int WSAENOTSOCK = 10038;
  
  public static final int WSAEDESTADDRREQ = 10039;
  
  public static final int WSAEMSGSIZE = 10040;
  
  public static final int WSAEPROTOTYPE = 10041;
  
  public static final int WSAENOPROTOOPT = 10042;
  
  public static final int WSAEPROTONOSUPPORT = 10043;
  
  public static final int WSAESOCKTNOSUPPORT = 10044;
  
  public static final int WSAEOPNOTSUPP = 10045;
  
  public static final int WSAEPFNOSUPPORT = 10046;
  
  public static final int WSAEAFNOSUPPORT = 10047;
  
  public static final int WSAEADDRINUSE = 10048;
  
  public static final int WSAEADDRNOTAVAIL = 10049;
  
  public static final int WSAENETDOWN = 10050;
  
  public static final int WSAENETUNREACH = 10051;
  
  public static final int WSAENETRESET = 10052;
  
  public static final int WSAECONNABORTED = 10053;
  
  public static final int WSAECONNRESET = 10054;
  
  public static final int WSAENOBUFS = 10055;
  
  public static final int WSAEISCONN = 10056;
  
  public static final int WSAENOTCONN = 10057;
  
  public static final int WSAESHUTDOWN = 10058;
  
  public static final int WSAETOOMANYREFS = 10059;
  
  public static final int WSAETIMEDOUT = 10060;
  
  public static final int WSAECONNREFUSED = 10061;
  
  public static final int WSAELOOP = 10062;
  
  public static final int WSAENAMETOOLONG = 10063;
  
  public static final int WSAEHOSTDOWN = 10064;
  
  public static final int WSAEHOSTUNREACH = 10065;
  
  public static final int WSAENOTEMPTY = 10066;
  
  public static final int WSAEPROCLIM = 10067;
  
  public static final int WSAEUSERS = 10068;
  
  public static final int WSAEDQUOT = 10069;
  
  public static final int WSAESTALE = 10070;
  
  public static final int WSAEREMOTE = 10071;
  
  public static final int WSASYSNOTREADY = 10091;
  
  public static final int WSAVERNOTSUPPORTED = 10092;
  
  public static final int WSANOTINITIALISED = 10093;
  
  public static final int WSAEDISCON = 10101;
  
  public static final int WSAENOMORE = 10102;
  
  public static final int WSAECANCELLED = 10103;
  
  public static final int WSAEINVALIDPROCTABLE = 10104;
  
  public static final int WSAEINVALIDPROVIDER = 10105;
  
  public static final int WSAEPROVIDERFAILEDINIT = 10106;
  
  public static final int WSASYSCALLFAILURE = 10107;
  
  public static final int WSASERVICE_NOT_FOUND = 10108;
  
  public static final int WSATYPE_NOT_FOUND = 10109;
  
  public static final int WSA_E_NO_MORE = 10110;
  
  public static final int WSA_E_CANCELLED = 10111;
  
  public static final int WSAEREFUSED = 10112;
  
  public static final int WSAHOST_NOT_FOUND = 11001;
  
  public static final int WSATRY_AGAIN = 11002;
  
  public static final int WSANO_RECOVERY = 11003;
  
  public static final int WSANO_DATA = 11004;
  
  public static final int WSA_QOS_RECEIVERS = 11005;
  
  public static final int WSA_QOS_SENDERS = 11006;
  
  public static final int WSA_QOS_NO_SENDERS = 11007;
  
  public static final int WSA_QOS_NO_RECEIVERS = 11008;
  
  public static final int WSA_QOS_REQUEST_CONFIRMED = 11009;
  
  public static final int WSA_QOS_ADMISSION_FAILURE = 11010;
  
  public static final int WSA_QOS_POLICY_FAILURE = 11011;
  
  public static final int WSA_QOS_BAD_STYLE = 11012;
  
  public static final int WSA_QOS_BAD_OBJECT = 11013;
  
  public static final int WSA_QOS_TRAFFIC_CTRL_ERROR = 11014;
  
  public static final int WSA_QOS_GENERIC_ERROR = 11015;
  
  public static final int WSA_QOS_ESERVICETYPE = 11016;
  
  public static final int WSA_QOS_EFLOWSPEC = 11017;
  
  public static final int WSA_QOS_EPROVSPECBUF = 11018;
  
  public static final int WSA_QOS_EFILTERSTYLE = 11019;
  
  public static final int WSA_QOS_EFILTERTYPE = 11020;
  
  public static final int WSA_QOS_EFILTERCOUNT = 11021;
  
  public static final int WSA_QOS_EOBJLENGTH = 11022;
  
  public static final int WSA_QOS_EFLOWCOUNT = 11023;
  
  public static final int WSA_QOS_EUNKOWNPSOBJ = 11024;
  
  public static final int WSA_QOS_EPOLICYOBJ = 11025;
  
  public static final int WSA_QOS_EFLOWDESC = 11026;
  
  public static final int WSA_QOS_EPSFLOWSPEC = 11027;
  
  public static final int WSA_QOS_EPSFILTERSPEC = 11028;
  
  public static final int WSA_QOS_ESDMODEOBJ = 11029;
  
  public static final int WSA_QOS_ESHAPERATEOBJ = 11030;
  
  public static final int WSA_QOS_RESERVED_PETYPE = 11031;
  
  public static final int ERROR_IPSEC_QM_POLICY_EXISTS = 13000;
  
  public static final int ERROR_IPSEC_QM_POLICY_NOT_FOUND = 13001;
  
  public static final int ERROR_IPSEC_QM_POLICY_IN_USE = 13002;
  
  public static final int ERROR_IPSEC_MM_POLICY_EXISTS = 13003;
  
  public static final int ERROR_IPSEC_MM_POLICY_NOT_FOUND = 13004;
  
  public static final int ERROR_IPSEC_MM_POLICY_IN_USE = 13005;
  
  public static final int ERROR_IPSEC_MM_FILTER_EXISTS = 13006;
  
  public static final int ERROR_IPSEC_MM_FILTER_NOT_FOUND = 13007;
  
  public static final int ERROR_IPSEC_TRANSPORT_FILTER_EXISTS = 13008;
  
  public static final int ERROR_IPSEC_TRANSPORT_FILTER_NOT_FOUND = 13009;
  
  public static final int ERROR_IPSEC_MM_AUTH_EXISTS = 13010;
  
  public static final int ERROR_IPSEC_MM_AUTH_NOT_FOUND = 13011;
  
  public static final int ERROR_IPSEC_MM_AUTH_IN_USE = 13012;
  
  public static final int ERROR_IPSEC_DEFAULT_MM_POLICY_NOT_FOUND = 13013;
  
  public static final int ERROR_IPSEC_DEFAULT_MM_AUTH_NOT_FOUND = 13014;
  
  public static final int ERROR_IPSEC_DEFAULT_QM_POLICY_NOT_FOUND = 13015;
  
  public static final int ERROR_IPSEC_TUNNEL_FILTER_EXISTS = 13016;
  
  public static final int ERROR_IPSEC_TUNNEL_FILTER_NOT_FOUND = 13017;
  
  public static final int ERROR_IPSEC_MM_FILTER_PENDING_DELETION = 13018;
  
  public static final int ERROR_IPSEC_TRANSPORT_FILTER_PENDING_DELETION = 13019;
  
  public static final int ERROR_IPSEC_TUNNEL_FILTER_PENDING_DELETION = 13020;
  
  public static final int ERROR_IPSEC_MM_POLICY_PENDING_DELETION = 13021;
  
  public static final int ERROR_IPSEC_MM_AUTH_PENDING_DELETION = 13022;
  
  public static final int ERROR_IPSEC_QM_POLICY_PENDING_DELETION = 13023;
  
  public static final int WARNING_IPSEC_MM_POLICY_PRUNED = 13024;
  
  public static final int WARNING_IPSEC_QM_POLICY_PRUNED = 13025;
  
  public static final int ERROR_IPSEC_IKE_NEG_STATUS_BEGIN = 13800;
  
  public static final int ERROR_IPSEC_IKE_AUTH_FAIL = 13801;
  
  public static final int ERROR_IPSEC_IKE_ATTRIB_FAIL = 13802;
  
  public static final int ERROR_IPSEC_IKE_NEGOTIATION_PENDING = 13803;
  
  public static final int ERROR_IPSEC_IKE_GENERAL_PROCESSING_ERROR = 13804;
  
  public static final int ERROR_IPSEC_IKE_TIMED_OUT = 13805;
  
  public static final int ERROR_IPSEC_IKE_NO_CERT = 13806;
  
  public static final int ERROR_IPSEC_IKE_SA_DELETED = 13807;
  
  public static final int ERROR_IPSEC_IKE_SA_REAPED = 13808;
  
  public static final int ERROR_IPSEC_IKE_MM_ACQUIRE_DROP = 13809;
  
  public static final int ERROR_IPSEC_IKE_QM_ACQUIRE_DROP = 13810;
  
  public static final int ERROR_IPSEC_IKE_QUEUE_DROP_MM = 13811;
  
  public static final int ERROR_IPSEC_IKE_QUEUE_DROP_NO_MM = 13812;
  
  public static final int ERROR_IPSEC_IKE_DROP_NO_RESPONSE = 13813;
  
  public static final int ERROR_IPSEC_IKE_MM_DELAY_DROP = 13814;
  
  public static final int ERROR_IPSEC_IKE_QM_DELAY_DROP = 13815;
  
  public static final int ERROR_IPSEC_IKE_ERROR = 13816;
  
  public static final int ERROR_IPSEC_IKE_CRL_FAILED = 13817;
  
  public static final int ERROR_IPSEC_IKE_INVALID_KEY_USAGE = 13818;
  
  public static final int ERROR_IPSEC_IKE_INVALID_CERT_TYPE = 13819;
  
  public static final int ERROR_IPSEC_IKE_NO_PRIVATE_KEY = 13820;
  
  public static final int ERROR_IPSEC_IKE_DH_FAIL = 13822;
  
  public static final int ERROR_IPSEC_IKE_INVALID_HEADER = 13824;
  
  public static final int ERROR_IPSEC_IKE_NO_POLICY = 13825;
  
  public static final int ERROR_IPSEC_IKE_INVALID_SIGNATURE = 13826;
  
  public static final int ERROR_IPSEC_IKE_KERBEROS_ERROR = 13827;
  
  public static final int ERROR_IPSEC_IKE_NO_PUBLIC_KEY = 13828;
  
  public static final int ERROR_IPSEC_IKE_PROCESS_ERR = 13829;
  
  public static final int ERROR_IPSEC_IKE_PROCESS_ERR_SA = 13830;
  
  public static final int ERROR_IPSEC_IKE_PROCESS_ERR_PROP = 13831;
  
  public static final int ERROR_IPSEC_IKE_PROCESS_ERR_TRANS = 13832;
  
  public static final int ERROR_IPSEC_IKE_PROCESS_ERR_KE = 13833;
  
  public static final int ERROR_IPSEC_IKE_PROCESS_ERR_ID = 13834;
  
  public static final int ERROR_IPSEC_IKE_PROCESS_ERR_CERT = 13835;
  
  public static final int ERROR_IPSEC_IKE_PROCESS_ERR_CERT_REQ = 13836;
  
  public static final int ERROR_IPSEC_IKE_PROCESS_ERR_HASH = 13837;
  
  public static final int ERROR_IPSEC_IKE_PROCESS_ERR_SIG = 13838;
  
  public static final int ERROR_IPSEC_IKE_PROCESS_ERR_NONCE = 13839;
  
  public static final int ERROR_IPSEC_IKE_PROCESS_ERR_NOTIFY = 13840;
  
  public static final int ERROR_IPSEC_IKE_PROCESS_ERR_DELETE = 13841;
  
  public static final int ERROR_IPSEC_IKE_PROCESS_ERR_VENDOR = 13842;
  
  public static final int ERROR_IPSEC_IKE_INVALID_PAYLOAD = 13843;
  
  public static final int ERROR_IPSEC_IKE_LOAD_SOFT_SA = 13844;
  
  public static final int ERROR_IPSEC_IKE_SOFT_SA_TORN_DOWN = 13845;
  
  public static final int ERROR_IPSEC_IKE_INVALID_COOKIE = 13846;
  
  public static final int ERROR_IPSEC_IKE_NO_PEER_CERT = 13847;
  
  public static final int ERROR_IPSEC_IKE_PEER_CRL_FAILED = 13848;
  
  public static final int ERROR_IPSEC_IKE_POLICY_CHANGE = 13849;
  
  public static final int ERROR_IPSEC_IKE_NO_MM_POLICY = 13850;
  
  public static final int ERROR_IPSEC_IKE_NOTCBPRIV = 13851;
  
  public static final int ERROR_IPSEC_IKE_SECLOADFAIL = 13852;
  
  public static final int ERROR_IPSEC_IKE_FAILSSPINIT = 13853;
  
  public static final int ERROR_IPSEC_IKE_FAILQUERYSSP = 13854;
  
  public static final int ERROR_IPSEC_IKE_SRVACQFAIL = 13855;
  
  public static final int ERROR_IPSEC_IKE_SRVQUERYCRED = 13856;
  
  public static final int ERROR_IPSEC_IKE_GETSPIFAIL = 13857;
  
  public static final int ERROR_IPSEC_IKE_INVALID_FILTER = 13858;
  
  public static final int ERROR_IPSEC_IKE_OUT_OF_MEMORY = 13859;
  
  public static final int ERROR_IPSEC_IKE_ADD_UPDATE_KEY_FAILED = 13860;
  
  public static final int ERROR_IPSEC_IKE_INVALID_POLICY = 13861;
  
  public static final int ERROR_IPSEC_IKE_UNKNOWN_DOI = 13862;
  
  public static final int ERROR_IPSEC_IKE_INVALID_SITUATION = 13863;
  
  public static final int ERROR_IPSEC_IKE_DH_FAILURE = 13864;
  
  public static final int ERROR_IPSEC_IKE_INVALID_GROUP = 13865;
  
  public static final int ERROR_IPSEC_IKE_ENCRYPT = 13866;
  
  public static final int ERROR_IPSEC_IKE_DECRYPT = 13867;
  
  public static final int ERROR_IPSEC_IKE_POLICY_MATCH = 13868;
  
  public static final int ERROR_IPSEC_IKE_UNSUPPORTED_ID = 13869;
  
  public static final int ERROR_IPSEC_IKE_INVALID_HASH = 13870;
  
  public static final int ERROR_IPSEC_IKE_INVALID_HASH_ALG = 13871;
  
  public static final int ERROR_IPSEC_IKE_INVALID_HASH_SIZE = 13872;
  
  public static final int ERROR_IPSEC_IKE_INVALID_ENCRYPT_ALG = 13873;
  
  public static final int ERROR_IPSEC_IKE_INVALID_AUTH_ALG = 13874;
  
  public static final int ERROR_IPSEC_IKE_INVALID_SIG = 13875;
  
  public static final int ERROR_IPSEC_IKE_LOAD_FAILED = 13876;
  
  public static final int ERROR_IPSEC_IKE_RPC_DELETE = 13877;
  
  public static final int ERROR_IPSEC_IKE_BENIGN_REINIT = 13878;
  
  public static final int ERROR_IPSEC_IKE_INVALID_RESPONDER_LIFETIME_NOTIFY = 13879;
  
  public static final int ERROR_IPSEC_IKE_INVALID_CERT_KEYLEN = 13881;
  
  public static final int ERROR_IPSEC_IKE_MM_LIMIT = 13882;
  
  public static final int ERROR_IPSEC_IKE_NEGOTIATION_DISABLED = 13883;
  
  public static final int ERROR_IPSEC_IKE_QM_LIMIT = 13884;
  
  public static final int ERROR_IPSEC_IKE_MM_EXPIRED = 13885;
  
  public static final int ERROR_IPSEC_IKE_PEER_MM_ASSUMED_INVALID = 13886;
  
  public static final int ERROR_IPSEC_IKE_CERT_CHAIN_POLICY_MISMATCH = 13887;
  
  public static final int ERROR_IPSEC_IKE_UNEXPECTED_MESSAGE_ID = 13888;
  
  public static final int ERROR_IPSEC_IKE_INVALID_AUTH_PAYLOAD = 13889;
  
  public static final int ERROR_IPSEC_IKE_DOS_COOKIE_SENT = 13890;
  
  public static final int ERROR_IPSEC_IKE_SHUTTING_DOWN = 13891;
  
  public static final int ERROR_IPSEC_IKE_CGA_AUTH_FAILED = 13892;
  
  public static final int ERROR_IPSEC_IKE_PROCESS_ERR_NATOA = 13893;
  
  public static final int ERROR_IPSEC_IKE_INVALID_MM_FOR_QM = 13894;
  
  public static final int ERROR_IPSEC_IKE_QM_EXPIRED = 13895;
  
  public static final int ERROR_IPSEC_IKE_TOO_MANY_FILTERS = 13896;
  
  public static final int ERROR_IPSEC_IKE_NEG_STATUS_END = 13897;
  
  public static final int ERROR_IPSEC_BAD_SPI = 13910;
  
  public static final int ERROR_IPSEC_SA_LIFETIME_EXPIRED = 13911;
  
  public static final int ERROR_IPSEC_WRONG_SA = 13912;
  
  public static final int ERROR_IPSEC_REPLAY_CHECK_FAILED = 13913;
  
  public static final int ERROR_IPSEC_INVALID_PACKET = 13914;
  
  public static final int ERROR_IPSEC_INTEGRITY_CHECK_FAILED = 13915;
  
  public static final int ERROR_IPSEC_CLEAR_TEXT_DROP = 13916;
  
  public static final int ERROR_SXS_SECTION_NOT_FOUND = 14000;
  
  public static final int ERROR_SXS_CANT_GEN_ACTCTX = 14001;
  
  public static final int ERROR_SXS_INVALID_ACTCTXDATA_FORMAT = 14002;
  
  public static final int ERROR_SXS_ASSEMBLY_NOT_FOUND = 14003;
  
  public static final int ERROR_SXS_MANIFEST_FORMAT_ERROR = 14004;
  
  public static final int ERROR_SXS_MANIFEST_PARSE_ERROR = 14005;
  
  public static final int ERROR_SXS_ACTIVATION_CONTEXT_DISABLED = 14006;
  
  public static final int ERROR_SXS_KEY_NOT_FOUND = 14007;
  
  public static final int ERROR_SXS_VERSION_CONFLICT = 14008;
  
  public static final int ERROR_SXS_WRONG_SECTION_TYPE = 14009;
  
  public static final int ERROR_SXS_THREAD_QUERIES_DISABLED = 14010;
  
  public static final int ERROR_SXS_PROCESS_DEFAULT_ALREADY_SET = 14011;
  
  public static final int ERROR_SXS_UNKNOWN_ENCODING_GROUP = 14012;
  
  public static final int ERROR_SXS_UNKNOWN_ENCODING = 14013;
  
  public static final int ERROR_SXS_INVALID_XML_NAMESPACE_URI = 14014;
  
  public static final int ERROR_SXS_ROOT_MANIFEST_DEPENDENCY_NOT_INSTALLED = 14015;
  
  public static final int ERROR_SXS_LEAF_MANIFEST_DEPENDENCY_NOT_INSTALLED = 14016;
  
  public static final int ERROR_SXS_INVALID_ASSEMBLY_IDENTITY_ATTRIBUTE = 14017;
  
  public static final int ERROR_SXS_MANIFEST_MISSING_REQUIRED_DEFAULT_NAMESPACE = 14018;
  
  public static final int ERROR_SXS_MANIFEST_INVALID_REQUIRED_DEFAULT_NAMESPACE = 14019;
  
  public static final int ERROR_SXS_PRIVATE_MANIFEST_CROSS_PATH_WITH_REPARSE_POINT = 14020;
  
  public static final int ERROR_SXS_DUPLICATE_DLL_NAME = 14021;
  
  public static final int ERROR_SXS_DUPLICATE_WINDOWCLASS_NAME = 14022;
  
  public static final int ERROR_SXS_DUPLICATE_CLSID = 14023;
  
  public static final int ERROR_SXS_DUPLICATE_IID = 14024;
  
  public static final int ERROR_SXS_DUPLICATE_TLBID = 14025;
  
  public static final int ERROR_SXS_DUPLICATE_PROGID = 14026;
  
  public static final int ERROR_SXS_DUPLICATE_ASSEMBLY_NAME = 14027;
  
  public static final int ERROR_SXS_FILE_HASH_MISMATCH = 14028;
  
  public static final int ERROR_SXS_POLICY_PARSE_ERROR = 14029;
  
  public static final int ERROR_SXS_XML_E_MISSINGQUOTE = 14030;
  
  public static final int ERROR_SXS_XML_E_COMMENTSYNTAX = 14031;
  
  public static final int ERROR_SXS_XML_E_BADSTARTNAMECHAR = 14032;
  
  public static final int ERROR_SXS_XML_E_BADNAMECHAR = 14033;
  
  public static final int ERROR_SXS_XML_E_BADCHARINSTRING = 14034;
  
  public static final int ERROR_SXS_XML_E_XMLDECLSYNTAX = 14035;
  
  public static final int ERROR_SXS_XML_E_BADCHARDATA = 14036;
  
  public static final int ERROR_SXS_XML_E_MISSINGWHITESPACE = 14037;
  
  public static final int ERROR_SXS_XML_E_EXPECTINGTAGEND = 14038;
  
  public static final int ERROR_SXS_XML_E_MISSINGSEMICOLON = 14039;
  
  public static final int ERROR_SXS_XML_E_UNBALANCEDPAREN = 14040;
  
  public static final int ERROR_SXS_XML_E_INTERNALERROR = 14041;
  
  public static final int ERROR_SXS_XML_E_UNEXPECTED_WHITESPACE = 14042;
  
  public static final int ERROR_SXS_XML_E_INCOMPLETE_ENCODING = 14043;
  
  public static final int ERROR_SXS_XML_E_MISSING_PAREN = 14044;
  
  public static final int ERROR_SXS_XML_E_EXPECTINGCLOSEQUOTE = 14045;
  
  public static final int ERROR_SXS_XML_E_MULTIPLE_COLONS = 14046;
  
  public static final int ERROR_SXS_XML_E_INVALID_DECIMAL = 14047;
  
  public static final int ERROR_SXS_XML_E_INVALID_HEXIDECIMAL = 14048;
  
  public static final int ERROR_SXS_XML_E_INVALID_UNICODE = 14049;
  
  public static final int ERROR_SXS_XML_E_WHITESPACEORQUESTIONMARK = 14050;
  
  public static final int ERROR_SXS_XML_E_UNEXPECTEDENDTAG = 14051;
  
  public static final int ERROR_SXS_XML_E_UNCLOSEDTAG = 14052;
  
  public static final int ERROR_SXS_XML_E_DUPLICATEATTRIBUTE = 14053;
  
  public static final int ERROR_SXS_XML_E_MULTIPLEROOTS = 14054;
  
  public static final int ERROR_SXS_XML_E_INVALIDATROOTLEVEL = 14055;
  
  public static final int ERROR_SXS_XML_E_BADXMLDECL = 14056;
  
  public static final int ERROR_SXS_XML_E_MISSINGROOT = 14057;
  
  public static final int ERROR_SXS_XML_E_UNEXPECTEDEOF = 14058;
  
  public static final int ERROR_SXS_XML_E_BADPEREFINSUBSET = 14059;
  
  public static final int ERROR_SXS_XML_E_UNCLOSEDSTARTTAG = 14060;
  
  public static final int ERROR_SXS_XML_E_UNCLOSEDENDTAG = 14061;
  
  public static final int ERROR_SXS_XML_E_UNCLOSEDSTRING = 14062;
  
  public static final int ERROR_SXS_XML_E_UNCLOSEDCOMMENT = 14063;
  
  public static final int ERROR_SXS_XML_E_UNCLOSEDDECL = 14064;
  
  public static final int ERROR_SXS_XML_E_UNCLOSEDCDATA = 14065;
  
  public static final int ERROR_SXS_XML_E_RESERVEDNAMESPACE = 14066;
  
  public static final int ERROR_SXS_XML_E_INVALIDENCODING = 14067;
  
  public static final int ERROR_SXS_XML_E_INVALIDSWITCH = 14068;
  
  public static final int ERROR_SXS_XML_E_BADXMLCASE = 14069;
  
  public static final int ERROR_SXS_XML_E_INVALID_STANDALONE = 14070;
  
  public static final int ERROR_SXS_XML_E_UNEXPECTED_STANDALONE = 14071;
  
  public static final int ERROR_SXS_XML_E_INVALID_VERSION = 14072;
  
  public static final int ERROR_SXS_XML_E_MISSINGEQUALS = 14073;
  
  public static final int ERROR_SXS_PROTECTION_RECOVERY_FAILED = 14074;
  
  public static final int ERROR_SXS_PROTECTION_PUBLIC_KEY_TOO_SHORT = 14075;
  
  public static final int ERROR_SXS_PROTECTION_CATALOG_NOT_VALID = 14076;
  
  public static final int ERROR_SXS_UNTRANSLATABLE_HRESULT = 14077;
  
  public static final int ERROR_SXS_PROTECTION_CATALOG_FILE_MISSING = 14078;
  
  public static final int ERROR_SXS_MISSING_ASSEMBLY_IDENTITY_ATTRIBUTE = 14079;
  
  public static final int ERROR_SXS_INVALID_ASSEMBLY_IDENTITY_ATTRIBUTE_NAME = 14080;
  
  public static final int ERROR_SXS_ASSEMBLY_MISSING = 14081;
  
  public static final int ERROR_SXS_CORRUPT_ACTIVATION_STACK = 14082;
  
  public static final int ERROR_SXS_CORRUPTION = 14083;
  
  public static final int ERROR_SXS_EARLY_DEACTIVATION = 14084;
  
  public static final int ERROR_SXS_INVALID_DEACTIVATION = 14085;
  
  public static final int ERROR_SXS_MULTIPLE_DEACTIVATION = 14086;
  
  public static final int ERROR_SXS_PROCESS_TERMINATION_REQUESTED = 14087;
  
  public static final int ERROR_SXS_RELEASE_ACTIVATION_CONTEXT = 14088;
  
  public static final int ERROR_SXS_SYSTEM_DEFAULT_ACTIVATION_CONTEXT_EMPTY = 14089;
  
  public static final int ERROR_SXS_INVALID_IDENTITY_ATTRIBUTE_VALUE = 14090;
  
  public static final int ERROR_SXS_INVALID_IDENTITY_ATTRIBUTE_NAME = 14091;
  
  public static final int ERROR_SXS_IDENTITY_DUPLICATE_ATTRIBUTE = 14092;
  
  public static final int ERROR_SXS_IDENTITY_PARSE_ERROR = 14093;
  
  public static final int ERROR_MALFORMED_SUBSTITUTION_STRING = 14094;
  
  public static final int ERROR_SXS_INCORRECT_PUBLIC_KEY_TOKEN = 14095;
  
  public static final int ERROR_UNMAPPED_SUBSTITUTION_STRING = 14096;
  
  public static final int ERROR_SXS_ASSEMBLY_NOT_LOCKED = 14097;
  
  public static final int ERROR_SXS_COMPONENT_STORE_CORRUPT = 14098;
  
  public static final int ERROR_ADVANCED_INSTALLER_FAILED = 14099;
  
  public static final int ERROR_XML_ENCODING_MISMATCH = 14100;
  
  public static final int ERROR_SXS_MANIFEST_IDENTITY_SAME_BUT_CONTENTS_DIFFERENT = 14101;
  
  public static final int ERROR_SXS_IDENTITIES_DIFFERENT = 14102;
  
  public static final int ERROR_SXS_ASSEMBLY_IS_NOT_A_DEPLOYMENT = 14103;
  
  public static final int ERROR_SXS_FILE_NOT_PART_OF_ASSEMBLY = 14104;
  
  public static final int ERROR_SXS_MANIFEST_TOO_BIG = 14105;
  
  public static final int ERROR_SXS_SETTING_NOT_REGISTERED = 14106;
  
  public static final int ERROR_SXS_TRANSACTION_CLOSURE_INCOMPLETE = 14107;
  
  public static final int ERROR_SMI_PRIMITIVE_INSTALLER_FAILED = 14108;
  
  public static final int ERROR_GENERIC_COMMAND_FAILED = 14109;
  
  public static final int ERROR_SXS_FILE_HASH_MISSING = 14110;
  
  public static final int ERROR_EVT_INVALID_CHANNEL_PATH = 15000;
  
  public static final int ERROR_EVT_INVALID_QUERY = 15001;
  
  public static final int ERROR_EVT_PUBLISHER_METADATA_NOT_FOUND = 15002;
  
  public static final int ERROR_EVT_EVENT_TEMPLATE_NOT_FOUND = 15003;
  
  public static final int ERROR_EVT_INVALID_PUBLISHER_NAME = 15004;
  
  public static final int ERROR_EVT_INVALID_EVENT_DATA = 15005;
  
  public static final int ERROR_EVT_CHANNEL_NOT_FOUND = 15007;
  
  public static final int ERROR_EVT_MALFORMED_XML_TEXT = 15008;
  
  public static final int ERROR_EVT_SUBSCRIPTION_TO_DIRECT_CHANNEL = 15009;
  
  public static final int ERROR_EVT_CONFIGURATION_ERROR = 15010;
  
  public static final int ERROR_EVT_QUERY_RESULT_STALE = 15011;
  
  public static final int ERROR_EVT_QUERY_RESULT_INVALID_POSITION = 15012;
  
  public static final int ERROR_EVT_NON_VALIDATING_MSXML = 15013;
  
  public static final int ERROR_EVT_FILTER_ALREADYSCOPED = 15014;
  
  public static final int ERROR_EVT_FILTER_NOTELTSET = 15015;
  
  public static final int ERROR_EVT_FILTER_INVARG = 15016;
  
  public static final int ERROR_EVT_FILTER_INVTEST = 15017;
  
  public static final int ERROR_EVT_FILTER_INVTYPE = 15018;
  
  public static final int ERROR_EVT_FILTER_PARSEERR = 15019;
  
  public static final int ERROR_EVT_FILTER_UNSUPPORTEDOP = 15020;
  
  public static final int ERROR_EVT_FILTER_UNEXPECTEDTOKEN = 15021;
  
  public static final int ERROR_EVT_INVALID_OPERATION_OVER_ENABLED_DIRECT_CHANNEL = 15022;
  
  public static final int ERROR_EVT_INVALID_CHANNEL_PROPERTY_VALUE = 15023;
  
  public static final int ERROR_EVT_INVALID_PUBLISHER_PROPERTY_VALUE = 15024;
  
  public static final int ERROR_EVT_CHANNEL_CANNOT_ACTIVATE = 15025;
  
  public static final int ERROR_EVT_FILTER_TOO_COMPLEX = 15026;
  
  public static final int ERROR_EVT_MESSAGE_NOT_FOUND = 15027;
  
  public static final int ERROR_EVT_MESSAGE_ID_NOT_FOUND = 15028;
  
  public static final int ERROR_EVT_UNRESOLVED_VALUE_INSERT = 15029;
  
  public static final int ERROR_EVT_UNRESOLVED_PARAMETER_INSERT = 15030;
  
  public static final int ERROR_EVT_MAX_INSERTS_REACHED = 15031;
  
  public static final int ERROR_EVT_EVENT_DEFINITION_NOT_FOUND = 15032;
  
  public static final int ERROR_EVT_MESSAGE_LOCALE_NOT_FOUND = 15033;
  
  public static final int ERROR_EVT_VERSION_TOO_OLD = 15034;
  
  public static final int ERROR_EVT_VERSION_TOO_NEW = 15035;
  
  public static final int ERROR_EVT_CANNOT_OPEN_CHANNEL_OF_QUERY = 15036;
  
  public static final int ERROR_EVT_PUBLISHER_DISABLED = 15037;
  
  public static final int ERROR_EVT_FILTER_OUT_OF_RANGE = 15038;
  
  public static final int ERROR_EC_SUBSCRIPTION_CANNOT_ACTIVATE = 15080;
  
  public static final int ERROR_EC_LOG_DISABLED = 15081;
  
  public static final int ERROR_EC_CIRCULAR_FORWARDING = 15082;
  
  public static final int ERROR_EC_CREDSTORE_FULL = 15083;
  
  public static final int ERROR_EC_CRED_NOT_FOUND = 15084;
  
  public static final int ERROR_EC_NO_ACTIVE_CHANNEL = 15085;
  
  public static final int ERROR_MUI_FILE_NOT_FOUND = 15100;
  
  public static final int ERROR_MUI_INVALID_FILE = 15101;
  
  public static final int ERROR_MUI_INVALID_RC_CONFIG = 15102;
  
  public static final int ERROR_MUI_INVALID_LOCALE_NAME = 15103;
  
  public static final int ERROR_MUI_INVALID_ULTIMATEFALLBACK_NAME = 15104;
  
  public static final int ERROR_MUI_FILE_NOT_LOADED = 15105;
  
  public static final int ERROR_RESOURCE_ENUM_USER_STOP = 15106;
  
  public static final int ERROR_MUI_INTLSETTINGS_UILANG_NOT_INSTALLED = 15107;
  
  public static final int ERROR_MUI_INTLSETTINGS_INVALID_LOCALE_NAME = 15108;
  
  public static final int ERROR_MCA_INVALID_CAPABILITIES_STRING = 15200;
  
  public static final int ERROR_MCA_INVALID_VCP_VERSION = 15201;
  
  public static final int ERROR_MCA_MONITOR_VIOLATES_MCCS_SPECIFICATION = 15202;
  
  public static final int ERROR_MCA_MCCS_VERSION_MISMATCH = 15203;
  
  public static final int ERROR_MCA_UNSUPPORTED_MCCS_VERSION = 15204;
  
  public static final int ERROR_MCA_INTERNAL_ERROR = 15205;
  
  public static final int ERROR_MCA_INVALID_TECHNOLOGY_TYPE_RETURNED = 15206;
  
  public static final int ERROR_MCA_UNSUPPORTED_COLOR_TEMPERATURE = 15207;
  
  public static final int ERROR_AMBIGUOUS_SYSTEM_DEVICE = 15250;
  
  public static final int ERROR_SYSTEM_DEVICE_NOT_FOUND = 15299;
  
  public static final int SEVERITY_SUCCESS = 0;
  
  public static final int SEVERITY_ERROR = 1;
  
  public static final int FACILITY_NT_BIT = 268435456;
  
  public static final int NOERROR = 0;
  
  public static final int E_UNEXPECTED = -2147418113;
  
  public static final int E_NOTIMPL = -2147467263;
  
  public static final int E_OUTOFMEMORY = -2147024882;
  
  public static final int E_INVALIDARG = -2147024809;
  
  public static final int E_NOINTERFACE = -2147467262;
  
  public static final int E_POINTER = -2147467261;
  
  public static final int E_HANDLE = -2147024890;
  
  public static final int E_ABORT = -2147467260;
  
  public static final int E_FAIL = -2147467259;
  
  public static final int E_ACCESSDENIED = -2147024891;
  
  public static final int E_PENDING = -2147483638;
  
  public static final int CO_E_INIT_TLS = -2147467258;
  
  public static final int CO_E_INIT_SHARED_ALLOCATOR = -2147467257;
  
  public static final int CO_E_INIT_MEMORY_ALLOCATOR = -2147467256;
  
  public static final int CO_E_INIT_CLASS_CACHE = -2147467255;
  
  public static final int CO_E_INIT_RPC_CHANNEL = -2147467254;
  
  public static final int CO_E_INIT_TLS_SET_CHANNEL_CONTROL = -2147467253;
  
  public static final int CO_E_INIT_TLS_CHANNEL_CONTROL = -2147467252;
  
  public static final int CO_E_INIT_UNACCEPTED_USER_ALLOCATOR = -2147467251;
  
  public static final int CO_E_INIT_SCM_MUTEX_EXISTS = -2147467250;
  
  public static final int CO_E_INIT_SCM_FILE_MAPPING_EXISTS = -2147467249;
  
  public static final int CO_E_INIT_SCM_MAP_VIEW_OF_FILE = -2147467248;
  
  public static final int CO_E_INIT_SCM_EXEC_FAILURE = -2147467247;
  
  public static final int CO_E_INIT_ONLY_SINGLE_THREADED = -2147467246;
  
  public static final int CO_E_CANT_REMOTE = -2147467245;
  
  public static final int CO_E_BAD_SERVER_NAME = -2147467244;
  
  public static final int CO_E_WRONG_SERVER_IDENTITY = -2147467243;
  
  public static final int CO_E_OLE1DDE_DISABLED = -2147467242;
  
  public static final int CO_E_RUNAS_SYNTAX = -2147467241;
  
  public static final int CO_E_CREATEPROCESS_FAILURE = -2147467240;
  
  public static final int CO_E_RUNAS_CREATEPROCESS_FAILURE = -2147467239;
  
  public static final int CO_E_RUNAS_LOGON_FAILURE = -2147467238;
  
  public static final int CO_E_LAUNCH_PERMSSION_DENIED = -2147467237;
  
  public static final int CO_E_START_SERVICE_FAILURE = -2147467236;
  
  public static final int CO_E_REMOTE_COMMUNICATION_FAILURE = -2147467235;
  
  public static final int CO_E_SERVER_START_TIMEOUT = -2147467234;
  
  public static final int CO_E_CLSREG_INCONSISTENT = -2147467233;
  
  public static final int CO_E_IIDREG_INCONSISTENT = -2147467232;
  
  public static final int CO_E_NOT_SUPPORTED = -2147467231;
  
  public static final int CO_E_RELOAD_DLL = -2147467230;
  
  public static final int CO_E_MSI_ERROR = -2147467229;
  
  public static final int CO_E_ATTEMPT_TO_CREATE_OUTSIDE_CLIENT_CONTEXT = -2147467228;
  
  public static final int CO_E_SERVER_PAUSED = -2147467227;
  
  public static final int CO_E_SERVER_NOT_PAUSED = -2147467226;
  
  public static final int CO_E_CLASS_DISABLED = -2147467225;
  
  public static final int CO_E_CLRNOTAVAILABLE = -2147467224;
  
  public static final int CO_E_ASYNC_WORK_REJECTED = -2147467223;
  
  public static final int CO_E_SERVER_INIT_TIMEOUT = -2147467222;
  
  public static final int CO_E_NO_SECCTX_IN_ACTIVATE = -2147467221;
  
  public static final int CO_E_TRACKER_CONFIG = -2147467216;
  
  public static final int CO_E_THREADPOOL_CONFIG = -2147467215;
  
  public static final int CO_E_SXS_CONFIG = -2147467214;
  
  public static final int CO_E_MALFORMED_SPN = -2147467213;
  
  public static final WinNT.HRESULT S_OK = new WinNT.HRESULT(0);
  
  public static final WinNT.HRESULT S_FALSE = new WinNT.HRESULT(1);
  
  public static final int OLE_E_FIRST = -2147221504;
  
  public static final int OLE_E_LAST = -2147221249;
  
  public static final int OLE_S_FIRST = 262144;
  
  public static final int OLE_S_LAST = 262399;
  
  public static final int OLE_E_OLEVERB = -2147221504;
  
  public static final int OLE_E_ADVF = -2147221503;
  
  public static final int OLE_E_ENUM_NOMORE = -2147221502;
  
  public static final int OLE_E_ADVISENOTSUPPORTED = -2147221501;
  
  public static final int OLE_E_NOCONNECTION = -2147221500;
  
  public static final int OLE_E_NOTRUNNING = -2147221499;
  
  public static final int OLE_E_NOCACHE = -2147221498;
  
  public static final int OLE_E_BLANK = -2147221497;
  
  public static final int OLE_E_CLASSDIFF = -2147221496;
  
  public static final int OLE_E_CANT_GETMONIKER = -2147221495;
  
  public static final int OLE_E_CANT_BINDTOSOURCE = -2147221494;
  
  public static final int OLE_E_STATIC = -2147221493;
  
  public static final int OLE_E_PROMPTSAVECANCELLED = -2147221492;
  
  public static final int OLE_E_INVALIDRECT = -2147221491;
  
  public static final int OLE_E_WRONGCOMPOBJ = -2147221490;
  
  public static final int OLE_E_INVALIDHWND = -2147221489;
  
  public static final int OLE_E_NOT_INPLACEACTIVE = -2147221488;
  
  public static final int OLE_E_CANTCONVERT = -2147221487;
  
  public static final int OLE_E_NOSTORAGE = -2147221486;
  
  public static final int DV_E_FORMATETC = -2147221404;
  
  public static final int DV_E_DVTARGETDEVICE = -2147221403;
  
  public static final int DV_E_STGMEDIUM = -2147221402;
  
  public static final int DV_E_STATDATA = -2147221401;
  
  public static final int DV_E_LINDEX = -2147221400;
  
  public static final int DV_E_TYMED = -2147221399;
  
  public static final int DV_E_CLIPFORMAT = -2147221398;
  
  public static final int DV_E_DVASPECT = -2147221397;
  
  public static final int DV_E_DVTARGETDEVICE_SIZE = -2147221396;
  
  public static final int DV_E_NOIVIEWOBJECT = -2147221395;
  
  public static final int DRAGDROP_E_FIRST = -2147221248;
  
  public static final int DRAGDROP_E_LAST = -2147221233;
  
  public static final int DRAGDROP_S_FIRST = 262400;
  
  public static final int DRAGDROP_S_LAST = 262415;
  
  public static final int DRAGDROP_E_NOTREGISTERED = -2147221248;
  
  public static final int DRAGDROP_E_ALREADYREGISTERED = -2147221247;
  
  public static final int DRAGDROP_E_INVALIDHWND = -2147221246;
  
  public static final int CLASSFACTORY_E_FIRST = -2147221232;
  
  public static final int CLASSFACTORY_E_LAST = -2147221217;
  
  public static final int CLASSFACTORY_S_FIRST = 262416;
  
  public static final int CLASSFACTORY_S_LAST = 262431;
  
  public static final int CLASS_E_NOAGGREGATION = -2147221232;
  
  public static final int CLASS_E_CLASSNOTAVAILABLE = -2147221231;
  
  public static final int CLASS_E_NOTLICENSED = -2147221230;
  
  public static final int MARSHAL_E_FIRST = -2147221216;
  
  public static final int MARSHAL_E_LAST = -2147221201;
  
  public static final int MARSHAL_S_FIRST = 262432;
  
  public static final int MARSHAL_S_LAST = 262447;
  
  public static final int DATA_E_FIRST = -2147221200;
  
  public static final int DATA_E_LAST = -2147221185;
  
  public static final int DATA_S_FIRST = 262448;
  
  public static final int DATA_S_LAST = 262463;
  
  public static final int VIEW_E_FIRST = -2147221184;
  
  public static final int VIEW_E_LAST = -2147221169;
  
  public static final int VIEW_S_FIRST = 262464;
  
  public static final int VIEW_S_LAST = 262479;
  
  public static final int VIEW_E_DRAW = -2147221184;
  
  public static final int REGDB_E_FIRST = -2147221168;
  
  public static final int REGDB_E_LAST = -2147221153;
  
  public static final int REGDB_S_FIRST = 262480;
  
  public static final int REGDB_S_LAST = 262495;
  
  public static final int REGDB_E_READREGDB = -2147221168;
  
  public static final int REGDB_E_WRITEREGDB = -2147221167;
  
  public static final int REGDB_E_KEYMISSING = -2147221166;
  
  public static final int REGDB_E_INVALIDVALUE = -2147221165;
  
  public static final int REGDB_E_CLASSNOTREG = -2147221164;
  
  public static final int REGDB_E_IIDNOTREG = -2147221163;
  
  public static final int REGDB_E_BADTHREADINGMODEL = -2147221162;
  
  public static final int CAT_E_FIRST = -2147221152;
  
  public static final int CAT_E_LAST = -2147221151;
  
  public static final int CAT_E_CATIDNOEXIST = -2147221152;
  
  public static final int CAT_E_NODESCRIPTION = -2147221151;
  
  public static final int CS_E_FIRST = -2147221148;
  
  public static final int CS_E_LAST = -2147221137;
  
  public static final int CS_E_PACKAGE_NOTFOUND = -2147221148;
  
  public static final int CS_E_NOT_DELETABLE = -2147221147;
  
  public static final int CS_E_CLASS_NOTFOUND = -2147221146;
  
  public static final int CS_E_INVALID_VERSION = -2147221145;
  
  public static final int CS_E_NO_CLASSSTORE = -2147221144;
  
  public static final int CS_E_OBJECT_NOTFOUND = -2147221143;
  
  public static final int CS_E_OBJECT_ALREADY_EXISTS = -2147221142;
  
  public static final int CS_E_INVALID_PATH = -2147221141;
  
  public static final int CS_E_NETWORK_ERROR = -2147221140;
  
  public static final int CS_E_ADMIN_LIMIT_EXCEEDED = -2147221139;
  
  public static final int CS_E_SCHEMA_MISMATCH = -2147221138;
  
  public static final int CS_E_INTERNAL_ERROR = -2147221137;
  
  public static final int CACHE_E_FIRST = -2147221136;
  
  public static final int CACHE_E_LAST = -2147221121;
  
  public static final int CACHE_S_FIRST = 262512;
  
  public static final int CACHE_S_LAST = 262527;
  
  public static final int CACHE_E_NOCACHE_UPDATED = -2147221136;
  
  public static final int OLEOBJ_E_FIRST = -2147221120;
  
  public static final int OLEOBJ_E_LAST = -2147221105;
  
  public static final int OLEOBJ_S_FIRST = 262528;
  
  public static final int OLEOBJ_S_LAST = 262543;
  
  public static final int OLEOBJ_E_NOVERBS = -2147221120;
  
  public static final int OLEOBJ_E_INVALIDVERB = -2147221119;
  
  public static final int CLIENTSITE_E_FIRST = -2147221104;
  
  public static final int CLIENTSITE_E_LAST = -2147221089;
  
  public static final int CLIENTSITE_S_FIRST = 262544;
  
  public static final int CLIENTSITE_S_LAST = 262559;
  
  public static final int INPLACE_E_NOTUNDOABLE = -2147221088;
  
  public static final int INPLACE_E_NOTOOLSPACE = -2147221087;
  
  public static final int INPLACE_E_FIRST = -2147221088;
  
  public static final int INPLACE_E_LAST = -2147221073;
  
  public static final int INPLACE_S_FIRST = 262560;
  
  public static final int INPLACE_S_LAST = 262575;
  
  public static final int ENUM_E_FIRST = -2147221072;
  
  public static final int ENUM_E_LAST = -2147221057;
  
  public static final int ENUM_S_FIRST = 262576;
  
  public static final int ENUM_S_LAST = 262591;
  
  public static final int CONVERT10_E_FIRST = -2147221056;
  
  public static final int CONVERT10_E_LAST = -2147221041;
  
  public static final int CONVERT10_S_FIRST = 262592;
  
  public static final int CONVERT10_S_LAST = 262607;
  
  public static final int CONVERT10_E_OLESTREAM_GET = -2147221056;
  
  public static final int CONVERT10_E_OLESTREAM_PUT = -2147221055;
  
  public static final int CONVERT10_E_OLESTREAM_FMT = -2147221054;
  
  public static final int CONVERT10_E_OLESTREAM_BITMAP_TO_DIB = -2147221053;
  
  public static final int CONVERT10_E_STG_FMT = -2147221052;
  
  public static final int CONVERT10_E_STG_NO_STD_STREAM = -2147221051;
  
  public static final int CONVERT10_E_STG_DIB_TO_BITMAP = -2147221050;
  
  public static final int CLIPBRD_E_FIRST = -2147221040;
  
  public static final int CLIPBRD_E_LAST = -2147221025;
  
  public static final int CLIPBRD_S_FIRST = 262608;
  
  public static final int CLIPBRD_S_LAST = 262623;
  
  public static final int CLIPBRD_E_CANT_OPEN = -2147221040;
  
  public static final int CLIPBRD_E_CANT_EMPTY = -2147221039;
  
  public static final int CLIPBRD_E_CANT_SET = -2147221038;
  
  public static final int CLIPBRD_E_BAD_DATA = -2147221037;
  
  public static final int CLIPBRD_E_CANT_CLOSE = -2147221036;
  
  public static final int MK_E_FIRST = -2147221024;
  
  public static final int MK_E_LAST = -2147221009;
  
  public static final int MK_S_FIRST = 262624;
  
  public static final int MK_S_LAST = 262639;
  
  public static final int MK_E_CONNECTMANUALLY = -2147221024;
  
  public static final int MK_E_EXCEEDEDDEADLINE = -2147221023;
  
  public static final int MK_E_NEEDGENERIC = -2147221022;
  
  public static final int MK_E_UNAVAILABLE = -2147221021;
  
  public static final int MK_E_SYNTAX = -2147221020;
  
  public static final int MK_E_NOOBJECT = -2147221019;
  
  public static final int MK_E_INVALIDEXTENSION = -2147221018;
  
  public static final int MK_E_INTERMEDIATEINTERFACENOTSUPPORTED = -2147221017;
  
  public static final int MK_E_NOTBINDABLE = -2147221016;
  
  public static final int MK_E_NOTBOUND = -2147221015;
  
  public static final int MK_E_CANTOPENFILE = -2147221014;
  
  public static final int MK_E_MUSTBOTHERUSER = -2147221013;
  
  public static final int MK_E_NOINVERSE = -2147221012;
  
  public static final int MK_E_NOSTORAGE = -2147221011;
  
  public static final int MK_E_NOPREFIX = -2147221010;
  
  public static final int MK_E_ENUMERATION_FAILED = -2147221009;
  
  public static final int CO_E_FIRST = -2147221008;
  
  public static final int CO_E_LAST = -2147220993;
  
  public static final int CO_S_FIRST = 262640;
  
  public static final int CO_S_LAST = 262655;
  
  public static final int CO_E_NOTINITIALIZED = -2147221008;
  
  public static final int CO_E_ALREADYINITIALIZED = -2147221007;
  
  public static final int CO_E_CANTDETERMINECLASS = -2147221006;
  
  public static final int CO_E_CLASSSTRING = -2147221005;
  
  public static final int CO_E_IIDSTRING = -2147221004;
  
  public static final int CO_E_APPNOTFOUND = -2147221003;
  
  public static final int CO_E_APPSINGLEUSE = -2147221002;
  
  public static final int CO_E_ERRORINAPP = -2147221001;
  
  public static final int CO_E_DLLNOTFOUND = -2147221000;
  
  public static final int CO_E_ERRORINDLL = -2147220999;
  
  public static final int CO_E_WRONGOSFORAPP = -2147220998;
  
  public static final int CO_E_OBJNOTREG = -2147220997;
  
  public static final int CO_E_OBJISREG = -2147220996;
  
  public static final int CO_E_OBJNOTCONNECTED = -2147220995;
  
  public static final int CO_E_APPDIDNTREG = -2147220994;
  
  public static final int CO_E_RELEASED = -2147220993;
  
  public static final int EVENT_E_FIRST = -2147220992;
  
  public static final int EVENT_E_LAST = -2147220961;
  
  public static final int EVENT_S_FIRST = 262656;
  
  public static final int EVENT_S_LAST = 262687;
  
  public static final int EVENT_S_SOME_SUBSCRIBERS_FAILED = 262656;
  
  public static final int EVENT_E_ALL_SUBSCRIBERS_FAILED = -2147220991;
  
  public static final int EVENT_S_NOSUBSCRIBERS = 262658;
  
  public static final int EVENT_E_QUERYSYNTAX = -2147220989;
  
  public static final int EVENT_E_QUERYFIELD = -2147220988;
  
  public static final int EVENT_E_INTERNALEXCEPTION = -2147220987;
  
  public static final int EVENT_E_INTERNALERROR = -2147220986;
  
  public static final int EVENT_E_INVALID_PER_USER_SID = -2147220985;
  
  public static final int EVENT_E_USER_EXCEPTION = -2147220984;
  
  public static final int EVENT_E_TOO_MANY_METHODS = -2147220983;
  
  public static final int EVENT_E_MISSING_EVENTCLASS = -2147220982;
  
  public static final int EVENT_E_NOT_ALL_REMOVED = -2147220981;
  
  public static final int EVENT_E_COMPLUS_NOT_INSTALLED = -2147220980;
  
  public static final int EVENT_E_CANT_MODIFY_OR_DELETE_UNCONFIGURED_OBJECT = -2147220979;
  
  public static final int EVENT_E_CANT_MODIFY_OR_DELETE_CONFIGURED_OBJECT = -2147220978;
  
  public static final int EVENT_E_INVALID_EVENT_CLASS_PARTITION = -2147220977;
  
  public static final int EVENT_E_PER_USER_SID_NOT_LOGGED_ON = -2147220976;
  
  public static final int XACT_E_FIRST = -2147168256;
  
  public static final int XACT_E_LAST = -2147168215;
  
  public static final int XACT_S_FIRST = 315392;
  
  public static final int XACT_S_LAST = 315408;
  
  public static final int XACT_E_ALREADYOTHERSINGLEPHASE = -2147168256;
  
  public static final int XACT_E_CANTRETAIN = -2147168255;
  
  public static final int XACT_E_COMMITFAILED = -2147168254;
  
  public static final int XACT_E_COMMITPREVENTED = -2147168253;
  
  public static final int XACT_E_HEURISTICABORT = -2147168252;
  
  public static final int XACT_E_HEURISTICCOMMIT = -2147168251;
  
  public static final int XACT_E_HEURISTICDAMAGE = -2147168250;
  
  public static final int XACT_E_HEURISTICDANGER = -2147168249;
  
  public static final int XACT_E_ISOLATIONLEVEL = -2147168248;
  
  public static final int XACT_E_NOASYNC = -2147168247;
  
  public static final int XACT_E_NOENLIST = -2147168246;
  
  public static final int XACT_E_NOISORETAIN = -2147168245;
  
  public static final int XACT_E_NORESOURCE = -2147168244;
  
  public static final int XACT_E_NOTCURRENT = -2147168243;
  
  public static final int XACT_E_NOTRANSACTION = -2147168242;
  
  public static final int XACT_E_NOTSUPPORTED = -2147168241;
  
  public static final int XACT_E_UNKNOWNRMGRID = -2147168240;
  
  public static final int XACT_E_WRONGSTATE = -2147168239;
  
  public static final int XACT_E_WRONGUOW = -2147168238;
  
  public static final int XACT_E_XTIONEXISTS = -2147168237;
  
  public static final int XACT_E_NOIMPORTOBJECT = -2147168236;
  
  public static final int XACT_E_INVALIDCOOKIE = -2147168235;
  
  public static final int XACT_E_INDOUBT = -2147168234;
  
  public static final int XACT_E_NOTIMEOUT = -2147168233;
  
  public static final int XACT_E_ALREADYINPROGRESS = -2147168232;
  
  public static final int XACT_E_ABORTED = -2147168231;
  
  public static final int XACT_E_LOGFULL = -2147168230;
  
  public static final int XACT_E_TMNOTAVAILABLE = -2147168229;
  
  public static final int XACT_E_CONNECTION_DOWN = -2147168228;
  
  public static final int XACT_E_CONNECTION_DENIED = -2147168227;
  
  public static final int XACT_E_REENLISTTIMEOUT = -2147168226;
  
  public static final int XACT_E_TIP_CONNECT_FAILED = -2147168225;
  
  public static final int XACT_E_TIP_PROTOCOL_ERROR = -2147168224;
  
  public static final int XACT_E_TIP_PULL_FAILED = -2147168223;
  
  public static final int XACT_E_DEST_TMNOTAVAILABLE = -2147168222;
  
  public static final int XACT_E_TIP_DISABLED = -2147168221;
  
  public static final int XACT_E_NETWORK_TX_DISABLED = -2147168220;
  
  public static final int XACT_E_PARTNER_NETWORK_TX_DISABLED = -2147168219;
  
  public static final int XACT_E_XA_TX_DISABLED = -2147168218;
  
  public static final int XACT_E_UNABLE_TO_READ_DTC_CONFIG = -2147168217;
  
  public static final int XACT_E_UNABLE_TO_LOAD_DTC_PROXY = -2147168216;
  
  public static final int XACT_E_ABORTING = -2147168215;
  
  public static final int XACT_E_CLERKNOTFOUND = -2147168128;
  
  public static final int XACT_E_CLERKEXISTS = -2147168127;
  
  public static final int XACT_E_RECOVERYINPROGRESS = -2147168126;
  
  public static final int XACT_E_TRANSACTIONCLOSED = -2147168125;
  
  public static final int XACT_E_INVALIDLSN = -2147168124;
  
  public static final int XACT_E_REPLAYREQUEST = -2147168123;
  
  public static final int XACT_S_ASYNC = 315392;
  
  public static final int XACT_S_DEFECT = 315393;
  
  public static final int XACT_S_READONLY = 315394;
  
  public static final int XACT_S_SOMENORETAIN = 315395;
  
  public static final int XACT_S_OKINFORM = 315396;
  
  public static final int XACT_S_MADECHANGESCONTENT = 315397;
  
  public static final int XACT_S_MADECHANGESINFORM = 315398;
  
  public static final int XACT_S_ALLNORETAIN = 315399;
  
  public static final int XACT_S_ABORTING = 315400;
  
  public static final int XACT_S_SINGLEPHASE = 315401;
  
  public static final int XACT_S_LOCALLY_OK = 315402;
  
  public static final int XACT_S_LASTRESOURCEMANAGER = 315408;
  
  public static final int CONTEXT_E_FIRST = -2147164160;
  
  public static final int CONTEXT_E_LAST = -2147164113;
  
  public static final int CONTEXT_S_FIRST = 319488;
  
  public static final int CONTEXT_S_LAST = 319535;
  
  public static final int CONTEXT_E_ABORTED = -2147164158;
  
  public static final int CONTEXT_E_ABORTING = -2147164157;
  
  public static final int CONTEXT_E_NOCONTEXT = -2147164156;
  
  public static final int CONTEXT_E_WOULD_DEADLOCK = -2147164155;
  
  public static final int CONTEXT_E_SYNCH_TIMEOUT = -2147164154;
  
  public static final int CONTEXT_E_OLDREF = -2147164153;
  
  public static final int CONTEXT_E_ROLENOTFOUND = -2147164148;
  
  public static final int CONTEXT_E_TMNOTAVAILABLE = -2147164145;
  
  public static final int CO_E_ACTIVATIONFAILED = -2147164127;
  
  public static final int CO_E_ACTIVATIONFAILED_EVENTLOGGED = -2147164126;
  
  public static final int CO_E_ACTIVATIONFAILED_CATALOGERROR = -2147164125;
  
  public static final int CO_E_ACTIVATIONFAILED_TIMEOUT = -2147164124;
  
  public static final int CO_E_INITIALIZATIONFAILED = -2147164123;
  
  public static final int CONTEXT_E_NOJIT = -2147164122;
  
  public static final int CONTEXT_E_NOTRANSACTION = -2147164121;
  
  public static final int CO_E_THREADINGMODEL_CHANGED = -2147164120;
  
  public static final int CO_E_NOIISINTRINSICS = -2147164119;
  
  public static final int CO_E_NOCOOKIES = -2147164118;
  
  public static final int CO_E_DBERROR = -2147164117;
  
  public static final int CO_E_NOTPOOLED = -2147164116;
  
  public static final int CO_E_NOTCONSTRUCTED = -2147164115;
  
  public static final int CO_E_NOSYNCHRONIZATION = -2147164114;
  
  public static final int CO_E_ISOLEVELMISMATCH = -2147164113;
  
  public static final int CO_E_CALL_OUT_OF_TX_SCOPE_NOT_ALLOWED = -2147164112;
  
  public static final int CO_E_EXIT_TRANSACTION_SCOPE_NOT_CALLED = -2147164111;
  
  public static final int OLE_S_USEREG = 262144;
  
  public static final int OLE_S_STATIC = 262145;
  
  public static final int OLE_S_MAC_CLIPFORMAT = 262146;
  
  public static final int DRAGDROP_S_DROP = 262400;
  
  public static final int DRAGDROP_S_CANCEL = 262401;
  
  public static final int DRAGDROP_S_USEDEFAULTCURSORS = 262402;
  
  public static final int DATA_S_SAMEFORMATETC = 262448;
  
  public static final int VIEW_S_ALREADY_FROZEN = 262464;
  
  public static final int CACHE_S_FORMATETC_NOTSUPPORTED = 262512;
  
  public static final int CACHE_S_SAMECACHE = 262513;
  
  public static final int CACHE_S_SOMECACHES_NOTUPDATED = 262514;
  
  public static final int OLEOBJ_S_INVALIDVERB = 262528;
  
  public static final int OLEOBJ_S_CANNOT_DOVERB_NOW = 262529;
  
  public static final int OLEOBJ_S_INVALIDHWND = 262530;
  
  public static final int INPLACE_S_TRUNCATED = 262560;
  
  public static final int CONVERT10_S_NO_PRESENTATION = 262592;
  
  public static final int MK_S_REDUCED_TO_SELF = 262626;
  
  public static final int MK_S_ME = 262628;
  
  public static final int MK_S_HIM = 262629;
  
  public static final int MK_S_US = 262630;
  
  public static final int MK_S_MONIKERALREADYREGISTERED = 262631;
  
  public static final int SCHED_S_TASK_READY = 267008;
  
  public static final int SCHED_S_TASK_RUNNING = 267009;
  
  public static final int SCHED_S_TASK_DISABLED = 267010;
  
  public static final int SCHED_S_TASK_HAS_NOT_RUN = 267011;
  
  public static final int SCHED_S_TASK_NO_MORE_RUNS = 267012;
  
  public static final int SCHED_S_TASK_NOT_SCHEDULED = 267013;
  
  public static final int SCHED_S_TASK_TERMINATED = 267014;
  
  public static final int SCHED_S_TASK_NO_VALID_TRIGGERS = 267015;
  
  public static final int SCHED_S_EVENT_TRIGGER = 267016;
  
  public static final int SCHED_E_TRIGGER_NOT_FOUND = -2147216631;
  
  public static final int SCHED_E_TASK_NOT_READY = -2147216630;
  
  public static final int SCHED_E_TASK_NOT_RUNNING = -2147216629;
  
  public static final int SCHED_E_SERVICE_NOT_INSTALLED = -2147216628;
  
  public static final int SCHED_E_CANNOT_OPEN_TASK = -2147216627;
  
  public static final int SCHED_E_INVALID_TASK = -2147216626;
  
  public static final int SCHED_E_ACCOUNT_INFORMATION_NOT_SET = -2147216625;
  
  public static final int SCHED_E_ACCOUNT_NAME_NOT_FOUND = -2147216624;
  
  public static final int SCHED_E_ACCOUNT_DBASE_CORRUPT = -2147216623;
  
  public static final int SCHED_E_NO_SECURITY_SERVICES = -2147216622;
  
  public static final int SCHED_E_UNKNOWN_OBJECT_VERSION = -2147216621;
  
  public static final int SCHED_E_UNSUPPORTED_ACCOUNT_OPTION = -2147216620;
  
  public static final int SCHED_E_SERVICE_NOT_RUNNING = -2147216619;
  
  public static final int SCHED_E_UNEXPECTEDNODE = -2147216618;
  
  public static final int SCHED_E_NAMESPACE = -2147216617;
  
  public static final int SCHED_E_INVALIDVALUE = -2147216616;
  
  public static final int SCHED_E_MISSINGNODE = -2147216615;
  
  public static final int SCHED_E_MALFORMEDXML = -2147216614;
  
  public static final int SCHED_S_SOME_TRIGGERS_FAILED = 267035;
  
  public static final int SCHED_S_BATCH_LOGON_PROBLEM = 267036;
  
  public static final int SCHED_E_TOO_MANY_NODES = -2147216611;
  
  public static final int SCHED_E_PAST_END_BOUNDARY = -2147216610;
  
  public static final int SCHED_E_ALREADY_RUNNING = -2147216609;
  
  public static final int SCHED_E_USER_NOT_LOGGED_ON = -2147216608;
  
  public static final int SCHED_E_INVALID_TASK_HASH = -2147216607;
  
  public static final int SCHED_E_SERVICE_NOT_AVAILABLE = -2147216606;
  
  public static final int SCHED_E_SERVICE_TOO_BUSY = -2147216605;
  
  public static final int SCHED_E_TASK_ATTEMPTED = -2147216604;
  
  public static final int SCHED_S_TASK_QUEUED = 267045;
  
  public static final int SCHED_E_TASK_DISABLED = -2147216602;
  
  public static final int SCHED_E_TASK_NOT_V1_COMPAT = -2147216601;
  
  public static final int SCHED_E_START_ON_DEMAND = -2147216600;
  
  public static final int CO_E_CLASS_CREATE_FAILED = -2146959359;
  
  public static final int CO_E_SCM_ERROR = -2146959358;
  
  public static final int CO_E_SCM_RPC_FAILURE = -2146959357;
  
  public static final int CO_E_BAD_PATH = -2146959356;
  
  public static final int CO_E_SERVER_EXEC_FAILURE = -2146959355;
  
  public static final int CO_E_OBJSRV_RPC_FAILURE = -2146959354;
  
  public static final int MK_E_NO_NORMALIZED = -2146959353;
  
  public static final int CO_E_SERVER_STOPPING = -2146959352;
  
  public static final int MEM_E_INVALID_ROOT = -2146959351;
  
  public static final int MEM_E_INVALID_LINK = -2146959344;
  
  public static final int MEM_E_INVALID_SIZE = -2146959343;
  
  public static final int CO_S_NOTALLINTERFACES = 524306;
  
  public static final int CO_S_MACHINENAMENOTFOUND = 524307;
  
  public static final int CO_E_MISSING_DISPLAYNAME = -2146959339;
  
  public static final int CO_E_RUNAS_VALUE_MUST_BE_AAA = -2146959338;
  
  public static final int CO_E_ELEVATION_DISABLED = -2146959337;
  
  public static final int DISP_E_UNKNOWNINTERFACE = -2147352575;
  
  public static final int DISP_E_MEMBERNOTFOUND = -2147352573;
  
  public static final int DISP_E_PARAMNOTFOUND = -2147352572;
  
  public static final int DISP_E_TYPEMISMATCH = -2147352571;
  
  public static final int DISP_E_UNKNOWNNAME = -2147352570;
  
  public static final int DISP_E_NONAMEDARGS = -2147352569;
  
  public static final int DISP_E_BADVARTYPE = -2147352568;
  
  public static final int DISP_E_EXCEPTION = -2147352567;
  
  public static final int DISP_E_OVERFLOW = -2147352566;
  
  public static final int DISP_E_BADINDEX = -2147352565;
  
  public static final int DISP_E_UNKNOWNLCID = -2147352564;
  
  public static final int DISP_E_ARRAYISLOCKED = -2147352563;
  
  public static final int DISP_E_BADPARAMCOUNT = -2147352562;
  
  public static final int DISP_E_PARAMNOTOPTIONAL = -2147352561;
  
  public static final int DISP_E_BADCALLEE = -2147352560;
  
  public static final int DISP_E_NOTACOLLECTION = -2147352559;
  
  public static final int DISP_E_DIVBYZERO = -2147352558;
  
  public static final int DISP_E_BUFFERTOOSMALL = -2147352557;
  
  public static final int TYPE_E_BUFFERTOOSMALL = -2147319786;
  
  public static final int TYPE_E_FIELDNOTFOUND = -2147319785;
  
  public static final int TYPE_E_INVDATAREAD = -2147319784;
  
  public static final int TYPE_E_UNSUPFORMAT = -2147319783;
  
  public static final int TYPE_E_REGISTRYACCESS = -2147319780;
  
  public static final int TYPE_E_LIBNOTREGISTERED = -2147319779;
  
  public static final int TYPE_E_UNDEFINEDTYPE = -2147319769;
  
  public static final int TYPE_E_QUALIFIEDNAMEDISALLOWED = -2147319768;
  
  public static final int TYPE_E_INVALIDSTATE = -2147319767;
  
  public static final int TYPE_E_WRONGTYPEKIND = -2147319766;
  
  public static final int TYPE_E_ELEMENTNOTFOUND = -2147319765;
  
  public static final int TYPE_E_AMBIGUOUSNAME = -2147319764;
  
  public static final int TYPE_E_NAMECONFLICT = -2147319763;
  
  public static final int TYPE_E_UNKNOWNLCID = -2147319762;
  
  public static final int TYPE_E_DLLFUNCTIONNOTFOUND = -2147319761;
  
  public static final int TYPE_E_BADMODULEKIND = -2147317571;
  
  public static final int TYPE_E_SIZETOOBIG = -2147317563;
  
  public static final int TYPE_E_DUPLICATEID = -2147317562;
  
  public static final int TYPE_E_INVALIDID = -2147317553;
  
  public static final int TYPE_E_TYPEMISMATCH = -2147316576;
  
  public static final int TYPE_E_OUTOFBOUNDS = -2147316575;
  
  public static final int TYPE_E_IOERROR = -2147316574;
  
  public static final int TYPE_E_CANTCREATETMPFILE = -2147316573;
  
  public static final int TYPE_E_CANTLOADLIBRARY = -2147312566;
  
  public static final int TYPE_E_INCONSISTENTPROPFUNCS = -2147312509;
  
  public static final int TYPE_E_CIRCULARTYPE = -2147312508;
  
  public static final int STG_E_INVALIDFUNCTION = -2147287039;
  
  public static final int STG_E_FILENOTFOUND = -2147287038;
  
  public static final int STG_E_PATHNOTFOUND = -2147287037;
  
  public static final int STG_E_TOOMANYOPENFILES = -2147287036;
  
  public static final int STG_E_ACCESSDENIED = -2147287035;
  
  public static final int STG_E_INVALIDHANDLE = -2147287034;
  
  public static final int STG_E_INSUFFICIENTMEMORY = -2147287032;
  
  public static final int STG_E_INVALIDPOINTER = -2147287031;
  
  public static final int STG_E_NOMOREFILES = -2147287022;
  
  public static final int STG_E_DISKISWRITEPROTECTED = -2147287021;
  
  public static final int STG_E_SEEKERROR = -2147287015;
  
  public static final int STG_E_WRITEFAULT = -2147287011;
  
  public static final int STG_E_READFAULT = -2147287010;
  
  public static final int STG_E_SHAREVIOLATION = -2147287008;
  
  public static final int STG_E_LOCKVIOLATION = -2147287007;
  
  public static final int STG_E_FILEALREADYEXISTS = -2147286960;
  
  public static final int STG_E_INVALIDPARAMETER = -2147286953;
  
  public static final int STG_E_MEDIUMFULL = -2147286928;
  
  public static final int STG_E_PROPSETMISMATCHED = -2147286800;
  
  public static final int STG_E_ABNORMALAPIEXIT = -2147286790;
  
  public static final int STG_E_INVALIDHEADER = -2147286789;
  
  public static final int STG_E_INVALIDNAME = -2147286788;
  
  public static final int STG_E_UNKNOWN = -2147286787;
  
  public static final int STG_E_UNIMPLEMENTEDFUNCTION = -2147286786;
  
  public static final int STG_E_INVALIDFLAG = -2147286785;
  
  public static final int STG_E_INUSE = -2147286784;
  
  public static final int STG_E_NOTCURRENT = -2147286783;
  
  public static final int STG_E_REVERTED = -2147286782;
  
  public static final int STG_E_CANTSAVE = -2147286781;
  
  public static final int STG_E_OLDFORMAT = -2147286780;
  
  public static final int STG_E_OLDDLL = -2147286779;
  
  public static final int STG_E_SHAREREQUIRED = -2147286778;
  
  public static final int STG_E_NOTFILEBASEDSTORAGE = -2147286777;
  
  public static final int STG_E_EXTANTMARSHALLINGS = -2147286776;
  
  public static final int STG_E_DOCFILECORRUPT = -2147286775;
  
  public static final int STG_E_BADBASEADDRESS = -2147286768;
  
  public static final int STG_E_DOCFILETOOLARGE = -2147286767;
  
  public static final int STG_E_NOTSIMPLEFORMAT = -2147286766;
  
  public static final int STG_E_INCOMPLETE = -2147286527;
  
  public static final int STG_E_TERMINATED = -2147286526;
  
  public static final int STG_S_CONVERTED = 197120;
  
  public static final int STG_S_BLOCK = 197121;
  
  public static final int STG_S_RETRYNOW = 197122;
  
  public static final int STG_S_MONITORING = 197123;
  
  public static final int STG_S_MULTIPLEOPENS = 197124;
  
  public static final int STG_S_CONSOLIDATIONFAILED = 197125;
  
  public static final int STG_S_CANNOTCONSOLIDATE = 197126;
  
  public static final int STG_E_STATUS_COPY_PROTECTION_FAILURE = -2147286267;
  
  public static final int STG_E_CSS_AUTHENTICATION_FAILURE = -2147286266;
  
  public static final int STG_E_CSS_KEY_NOT_PRESENT = -2147286265;
  
  public static final int STG_E_CSS_KEY_NOT_ESTABLISHED = -2147286264;
  
  public static final int STG_E_CSS_SCRAMBLED_SECTOR = -2147286263;
  
  public static final int STG_E_CSS_REGION_MISMATCH = -2147286262;
  
  public static final int STG_E_RESETS_EXHAUSTED = -2147286261;
  
  public static final int RPC_E_CALL_REJECTED = -2147418111;
  
  public static final int RPC_E_CALL_CANCELED = -2147418110;
  
  public static final int RPC_E_CANTPOST_INSENDCALL = -2147418109;
  
  public static final int RPC_E_CANTCALLOUT_INASYNCCALL = -2147418108;
  
  public static final int RPC_E_CANTCALLOUT_INEXTERNALCALL = -2147418107;
  
  public static final int RPC_E_CONNECTION_TERMINATED = -2147418106;
  
  public static final int RPC_E_SERVER_DIED = -2147418105;
  
  public static final int RPC_E_CLIENT_DIED = -2147418104;
  
  public static final int RPC_E_INVALID_DATAPACKET = -2147418103;
  
  public static final int RPC_E_CANTTRANSMIT_CALL = -2147418102;
  
  public static final int RPC_E_CLIENT_CANTMARSHAL_DATA = -2147418101;
  
  public static final int RPC_E_CLIENT_CANTUNMARSHAL_DATA = -2147418100;
  
  public static final int RPC_E_SERVER_CANTMARSHAL_DATA = -2147418099;
  
  public static final int RPC_E_SERVER_CANTUNMARSHAL_DATA = -2147418098;
  
  public static final int RPC_E_INVALID_DATA = -2147418097;
  
  public static final int RPC_E_INVALID_PARAMETER = -2147418096;
  
  public static final int RPC_E_CANTCALLOUT_AGAIN = -2147418095;
  
  public static final int RPC_E_SERVER_DIED_DNE = -2147418094;
  
  public static final int RPC_E_SYS_CALL_FAILED = -2147417856;
  
  public static final int RPC_E_OUT_OF_RESOURCES = -2147417855;
  
  public static final int RPC_E_ATTEMPTED_MULTITHREAD = -2147417854;
  
  public static final int RPC_E_NOT_REGISTERED = -2147417853;
  
  public static final int RPC_E_FAULT = -2147417852;
  
  public static final int RPC_E_SERVERFAULT = -2147417851;
  
  public static final int RPC_E_CHANGED_MODE = -2147417850;
  
  public static final int RPC_E_INVALIDMETHOD = -2147417849;
  
  public static final int RPC_E_DISCONNECTED = -2147417848;
  
  public static final int RPC_E_RETRY = -2147417847;
  
  public static final int RPC_E_SERVERCALL_RETRYLATER = -2147417846;
  
  public static final int RPC_E_SERVERCALL_REJECTED = -2147417845;
  
  public static final int RPC_E_INVALID_CALLDATA = -2147417844;
  
  public static final int RPC_E_CANTCALLOUT_ININPUTSYNCCALL = -2147417843;
  
  public static final int RPC_E_WRONG_THREAD = -2147417842;
  
  public static final int RPC_E_THREAD_NOT_INIT = -2147417841;
  
  public static final int RPC_E_VERSION_MISMATCH = -2147417840;
  
  public static final int RPC_E_INVALID_HEADER = -2147417839;
  
  public static final int RPC_E_INVALID_EXTENSION = -2147417838;
  
  public static final int RPC_E_INVALID_IPID = -2147417837;
  
  public static final int RPC_E_INVALID_OBJECT = -2147417836;
  
  public static final int RPC_S_CALLPENDING = -2147417835;
  
  public static final int RPC_S_WAITONTIMER = -2147417834;
  
  public static final int RPC_E_CALL_COMPLETE = -2147417833;
  
  public static final int RPC_E_UNSECURE_CALL = -2147417832;
  
  public static final int RPC_E_TOO_LATE = -2147417831;
  
  public static final int RPC_E_NO_GOOD_SECURITY_PACKAGES = -2147417830;
  
  public static final int RPC_E_ACCESS_DENIED = -2147417829;
  
  public static final int RPC_E_REMOTE_DISABLED = -2147417828;
  
  public static final int RPC_E_INVALID_OBJREF = -2147417827;
  
  public static final int RPC_E_NO_CONTEXT = -2147417826;
  
  public static final int RPC_E_TIMEOUT = -2147417825;
  
  public static final int RPC_E_NO_SYNC = -2147417824;
  
  public static final int RPC_E_FULLSIC_REQUIRED = -2147417823;
  
  public static final int RPC_E_INVALID_STD_NAME = -2147417822;
  
  public static final int CO_E_FAILEDTOIMPERSONATE = -2147417821;
  
  public static final int CO_E_FAILEDTOGETSECCTX = -2147417820;
  
  public static final int CO_E_FAILEDTOOPENTHREADTOKEN = -2147417819;
  
  public static final int CO_E_FAILEDTOGETTOKENINFO = -2147417818;
  
  public static final int CO_E_TRUSTEEDOESNTMATCHCLIENT = -2147417817;
  
  public static final int CO_E_FAILEDTOQUERYCLIENTBLANKET = -2147417816;
  
  public static final int CO_E_FAILEDTOSETDACL = -2147417815;
  
  public static final int CO_E_ACCESSCHECKFAILED = -2147417814;
  
  public static final int CO_E_NETACCESSAPIFAILED = -2147417813;
  
  public static final int CO_E_WRONGTRUSTEENAMESYNTAX = -2147417812;
  
  public static final int CO_E_INVALIDSID = -2147417811;
  
  public static final int CO_E_CONVERSIONFAILED = -2147417810;
  
  public static final int CO_E_NOMATCHINGSIDFOUND = -2147417809;
  
  public static final int CO_E_LOOKUPACCSIDFAILED = -2147417808;
  
  public static final int CO_E_NOMATCHINGNAMEFOUND = -2147417807;
  
  public static final int CO_E_LOOKUPACCNAMEFAILED = -2147417806;
  
  public static final int CO_E_SETSERLHNDLFAILED = -2147417805;
  
  public static final int CO_E_FAILEDTOGETWINDIR = -2147417804;
  
  public static final int CO_E_PATHTOOLONG = -2147417803;
  
  public static final int CO_E_FAILEDTOGENUUID = -2147417802;
  
  public static final int CO_E_FAILEDTOCREATEFILE = -2147417801;
  
  public static final int CO_E_FAILEDTOCLOSEHANDLE = -2147417800;
  
  public static final int CO_E_EXCEEDSYSACLLIMIT = -2147417799;
  
  public static final int CO_E_ACESINWRONGORDER = -2147417798;
  
  public static final int CO_E_INCOMPATIBLESTREAMVERSION = -2147417797;
  
  public static final int CO_E_FAILEDTOOPENPROCESSTOKEN = -2147417796;
  
  public static final int CO_E_DECODEFAILED = -2147417795;
  
  public static final int CO_E_ACNOTINITIALIZED = -2147417793;
  
  public static final int CO_E_CANCEL_DISABLED = -2147417792;
  
  public static final int RPC_E_UNEXPECTED = -2147352577;
  
  public static final int ERROR_AUDITING_DISABLED = -1073151999;
  
  public static final int ERROR_ALL_SIDS_FILTERED = -1073151998;
  
  public static final int ERROR_BIZRULES_NOT_ENABLED = -1073151997;
  
  public static final int NTE_BAD_UID = -2146893823;
  
  public static final int NTE_BAD_HASH = -2146893822;
  
  public static final int NTE_BAD_KEY = -2146893821;
  
  public static final int NTE_BAD_LEN = -2146893820;
  
  public static final int NTE_BAD_DATA = -2146893819;
  
  public static final int NTE_BAD_SIGNATURE = -2146893818;
  
  public static final int NTE_BAD_VER = -2146893817;
  
  public static final int NTE_BAD_ALGID = -2146893816;
  
  public static final int NTE_BAD_FLAGS = -2146893815;
  
  public static final int NTE_BAD_TYPE = -2146893814;
  
  public static final int NTE_BAD_KEY_STATE = -2146893813;
  
  public static final int NTE_BAD_HASH_STATE = -2146893812;
  
  public static final int NTE_NO_KEY = -2146893811;
  
  public static final int NTE_NO_MEMORY = -2146893810;
  
  public static final int NTE_EXISTS = -2146893809;
  
  public static final int NTE_PERM = -2146893808;
  
  public static final int NTE_NOT_FOUND = -2146893807;
  
  public static final int NTE_DOUBLE_ENCRYPT = -2146893806;
  
  public static final int NTE_BAD_PROVIDER = -2146893805;
  
  public static final int NTE_BAD_PROV_TYPE = -2146893804;
  
  public static final int NTE_BAD_PUBLIC_KEY = -2146893803;
  
  public static final int NTE_BAD_KEYSET = -2146893802;
  
  public static final int NTE_PROV_TYPE_NOT_DEF = -2146893801;
  
  public static final int NTE_PROV_TYPE_ENTRY_BAD = -2146893800;
  
  public static final int NTE_KEYSET_NOT_DEF = -2146893799;
  
  public static final int NTE_KEYSET_ENTRY_BAD = -2146893798;
  
  public static final int NTE_PROV_TYPE_NO_MATCH = -2146893797;
  
  public static final int NTE_SIGNATURE_FILE_BAD = -2146893796;
  
  public static final int NTE_PROVIDER_DLL_FAIL = -2146893795;
  
  public static final int NTE_PROV_DLL_NOT_FOUND = -2146893794;
  
  public static final int NTE_BAD_KEYSET_PARAM = -2146893793;
  
  public static final int NTE_FAIL = -2146893792;
  
  public static final int NTE_SYS_ERR = -2146893791;
  
  public static final int NTE_SILENT_CONTEXT = -2146893790;
  
  public static final int NTE_TOKEN_KEYSET_STORAGE_FULL = -2146893789;
  
  public static final int NTE_TEMPORARY_PROFILE = -2146893788;
  
  public static final int NTE_FIXEDPARAMETER = -2146893787;
  
  public static final int NTE_INVALID_HANDLE = -2146893786;
  
  public static final int NTE_INVALID_PARAMETER = -2146893785;
  
  public static final int NTE_BUFFER_TOO_SMALL = -2146893784;
  
  public static final int NTE_NOT_SUPPORTED = -2146893783;
  
  public static final int NTE_NO_MORE_ITEMS = -2146893782;
  
  public static final int NTE_BUFFERS_OVERLAP = -2146893781;
  
  public static final int NTE_DECRYPTION_FAILURE = -2146893780;
  
  public static final int NTE_INTERNAL_ERROR = -2146893779;
  
  public static final int NTE_UI_REQUIRED = -2146893778;
  
  public static final int NTE_HMAC_NOT_SUPPORTED = -2146893777;
  
  public static final int SEC_E_INSUFFICIENT_MEMORY = -2146893056;
  
  public static final int SEC_E_INVALID_HANDLE = -2146893055;
  
  public static final int SEC_E_UNSUPPORTED_FUNCTION = -2146893054;
  
  public static final int SEC_E_TARGET_UNKNOWN = -2146893053;
  
  public static final int SEC_E_INTERNAL_ERROR = -2146893052;
  
  public static final int SEC_E_SECPKG_NOT_FOUND = -2146893051;
  
  public static final int SEC_E_NOT_OWNER = -2146893050;
  
  public static final int SEC_E_CANNOT_INSTALL = -2146893049;
  
  public static final int SEC_E_INVALID_TOKEN = -2146893048;
  
  public static final int SEC_E_CANNOT_PACK = -2146893047;
  
  public static final int SEC_E_QOP_NOT_SUPPORTED = -2146893046;
  
  public static final int SEC_E_NO_IMPERSONATION = -2146893045;
  
  public static final int SEC_E_LOGON_DENIED = -2146893044;
  
  public static final int SEC_E_UNKNOWN_CREDENTIALS = -2146893043;
  
  public static final int SEC_E_NO_CREDENTIALS = -2146893042;
  
  public static final int SEC_E_MESSAGE_ALTERED = -2146893041;
  
  public static final int SEC_E_OUT_OF_SEQUENCE = -2146893040;
  
  public static final int SEC_E_NO_AUTHENTICATING_AUTHORITY = -2146893039;
  
  public static final int SEC_I_CONTINUE_NEEDED = 590610;
  
  public static final int SEC_I_COMPLETE_NEEDED = 590611;
  
  public static final int SEC_I_COMPLETE_AND_CONTINUE = 590612;
  
  public static final int SEC_I_LOCAL_LOGON = 590613;
  
  public static final int SEC_E_BAD_PKGID = -2146893034;
  
  public static final int SEC_E_CONTEXT_EXPIRED = -2146893033;
  
  public static final int SEC_I_CONTEXT_EXPIRED = 590615;
  
  public static final int SEC_E_INCOMPLETE_MESSAGE = -2146893032;
  
  public static final int SEC_E_INCOMPLETE_CREDENTIALS = -2146893024;
  
  public static final int SEC_E_BUFFER_TOO_SMALL = -2146893023;
  
  public static final int SEC_I_INCOMPLETE_CREDENTIALS = 590624;
  
  public static final int SEC_I_RENEGOTIATE = 590625;
  
  public static final int SEC_E_WRONG_PRINCIPAL = -2146893022;
  
  public static final int SEC_I_NO_LSA_CONTEXT = 590627;
  
  public static final int SEC_E_TIME_SKEW = -2146893020;
  
  public static final int SEC_E_UNTRUSTED_ROOT = -2146893019;
  
  public static final int SEC_E_ILLEGAL_MESSAGE = -2146893018;
  
  public static final int SEC_E_CERT_UNKNOWN = -2146893017;
  
  public static final int SEC_E_CERT_EXPIRED = -2146893016;
  
  public static final int SEC_E_ENCRYPT_FAILURE = -2146893015;
  
  public static final int SEC_E_DECRYPT_FAILURE = -2146893008;
  
  public static final int SEC_E_ALGORITHM_MISMATCH = -2146893007;
  
  public static final int SEC_E_SECURITY_QOS_FAILED = -2146893006;
  
  public static final int SEC_E_UNFINISHED_CONTEXT_DELETED = -2146893005;
  
  public static final int SEC_E_NO_TGT_REPLY = -2146893004;
  
  public static final int SEC_E_NO_IP_ADDRESSES = -2146893003;
  
  public static final int SEC_E_WRONG_CREDENTIAL_HANDLE = -2146893002;
  
  public static final int SEC_E_CRYPTO_SYSTEM_INVALID = -2146893001;
  
  public static final int SEC_E_MAX_REFERRALS_EXCEEDED = -2146893000;
  
  public static final int SEC_E_MUST_BE_KDC = -2146892999;
  
  public static final int SEC_E_STRONG_CRYPTO_NOT_SUPPORTED = -2146892998;
  
  public static final int SEC_E_TOO_MANY_PRINCIPALS = -2146892997;
  
  public static final int SEC_E_NO_PA_DATA = -2146892996;
  
  public static final int SEC_E_PKINIT_NAME_MISMATCH = -2146892995;
  
  public static final int SEC_E_SMARTCARD_LOGON_REQUIRED = -2146892994;
  
  public static final int SEC_E_SHUTDOWN_IN_PROGRESS = -2146892993;
  
  public static final int SEC_E_KDC_INVALID_REQUEST = -2146892992;
  
  public static final int SEC_E_KDC_UNABLE_TO_REFER = -2146892991;
  
  public static final int SEC_E_KDC_UNKNOWN_ETYPE = -2146892990;
  
  public static final int SEC_E_UNSUPPORTED_PREAUTH = -2146892989;
  
  public static final int SEC_E_DELEGATION_REQUIRED = -2146892987;
  
  public static final int SEC_E_BAD_BINDINGS = -2146892986;
  
  public static final int SEC_E_MULTIPLE_ACCOUNTS = -2146892985;
  
  public static final int SEC_E_NO_KERB_KEY = -2146892984;
  
  public static final int SEC_E_CERT_WRONG_USAGE = -2146892983;
  
  public static final int SEC_E_DOWNGRADE_DETECTED = -2146892976;
  
  public static final int SEC_E_SMARTCARD_CERT_REVOKED = -2146892975;
  
  public static final int SEC_E_ISSUING_CA_UNTRUSTED = -2146892974;
  
  public static final int SEC_E_REVOCATION_OFFLINE_C = -2146892973;
  
  public static final int SEC_E_PKINIT_CLIENT_FAILURE = -2146892972;
  
  public static final int SEC_E_SMARTCARD_CERT_EXPIRED = -2146892971;
  
  public static final int SEC_E_NO_S4U_PROT_SUPPORT = -2146892970;
  
  public static final int SEC_E_CROSSREALM_DELEGATION_FAILURE = -2146892969;
  
  public static final int SEC_E_REVOCATION_OFFLINE_KDC = -2146892968;
  
  public static final int SEC_E_ISSUING_CA_UNTRUSTED_KDC = -2146892967;
  
  public static final int SEC_E_KDC_CERT_EXPIRED = -2146892966;
  
  public static final int SEC_E_KDC_CERT_REVOKED = -2146892965;
  
  public static final int SEC_I_SIGNATURE_NEEDED = 590684;
  
  public static final int SEC_E_INVALID_PARAMETER = -2146892963;
  
  public static final int SEC_E_DELEGATION_POLICY = -2146892962;
  
  public static final int SEC_E_POLICY_NLTM_ONLY = -2146892961;
  
  public static final int SEC_I_NO_RENEGOTIATION = 590688;
  
  public static final int SEC_E_NO_SPM = -2146893052;
  
  public static final int SEC_E_NOT_SUPPORTED = -2146893054;
  
  public static final int CRYPT_E_MSG_ERROR = -2146889727;
  
  public static final int CRYPT_E_UNKNOWN_ALGO = -2146889726;
  
  public static final int CRYPT_E_OID_FORMAT = -2146889725;
  
  public static final int CRYPT_E_INVALID_MSG_TYPE = -2146889724;
  
  public static final int CRYPT_E_UNEXPECTED_ENCODING = -2146889723;
  
  public static final int CRYPT_E_AUTH_ATTR_MISSING = -2146889722;
  
  public static final int CRYPT_E_HASH_VALUE = -2146889721;
  
  public static final int CRYPT_E_INVALID_INDEX = -2146889720;
  
  public static final int CRYPT_E_ALREADY_DECRYPTED = -2146889719;
  
  public static final int CRYPT_E_NOT_DECRYPTED = -2146889718;
  
  public static final int CRYPT_E_RECIPIENT_NOT_FOUND = -2146889717;
  
  public static final int CRYPT_E_CONTROL_TYPE = -2146889716;
  
  public static final int CRYPT_E_ISSUER_SERIALNUMBER = -2146889715;
  
  public static final int CRYPT_E_SIGNER_NOT_FOUND = -2146889714;
  
  public static final int CRYPT_E_ATTRIBUTES_MISSING = -2146889713;
  
  public static final int CRYPT_E_STREAM_MSG_NOT_READY = -2146889712;
  
  public static final int CRYPT_E_STREAM_INSUFFICIENT_DATA = -2146889711;
  
  public static final int CRYPT_I_NEW_PROTECTION_REQUIRED = 593938;
  
  public static final int CRYPT_E_BAD_LEN = -2146885631;
  
  public static final int CRYPT_E_BAD_ENCODE = -2146885630;
  
  public static final int CRYPT_E_FILE_ERROR = -2146885629;
  
  public static final int CRYPT_E_NOT_FOUND = -2146885628;
  
  public static final int CRYPT_E_EXISTS = -2146885627;
  
  public static final int CRYPT_E_NO_PROVIDER = -2146885626;
  
  public static final int CRYPT_E_SELF_SIGNED = -2146885625;
  
  public static final int CRYPT_E_DELETED_PREV = -2146885624;
  
  public static final int CRYPT_E_NO_MATCH = -2146885623;
  
  public static final int CRYPT_E_UNEXPECTED_MSG_TYPE = -2146885622;
  
  public static final int CRYPT_E_NO_KEY_PROPERTY = -2146885621;
  
  public static final int CRYPT_E_NO_DECRYPT_CERT = -2146885620;
  
  public static final int CRYPT_E_BAD_MSG = -2146885619;
  
  public static final int CRYPT_E_NO_SIGNER = -2146885618;
  
  public static final int CRYPT_E_PENDING_CLOSE = -2146885617;
  
  public static final int CRYPT_E_REVOKED = -2146885616;
  
  public static final int CRYPT_E_NO_REVOCATION_DLL = -2146885615;
  
  public static final int CRYPT_E_NO_REVOCATION_CHECK = -2146885614;
  
  public static final int CRYPT_E_REVOCATION_OFFLINE = -2146885613;
  
  public static final int CRYPT_E_NOT_IN_REVOCATION_DATABASE = -2146885612;
  
  public static final int CRYPT_E_INVALID_NUMERIC_STRING = -2146885600;
  
  public static final int CRYPT_E_INVALID_PRINTABLE_STRING = -2146885599;
  
  public static final int CRYPT_E_INVALID_IA5_STRING = -2146885598;
  
  public static final int CRYPT_E_INVALID_X500_STRING = -2146885597;
  
  public static final int CRYPT_E_NOT_CHAR_STRING = -2146885596;
  
  public static final int CRYPT_E_FILERESIZED = -2146885595;
  
  public static final int CRYPT_E_SECURITY_SETTINGS = -2146885594;
  
  public static final int CRYPT_E_NO_VERIFY_USAGE_DLL = -2146885593;
  
  public static final int CRYPT_E_NO_VERIFY_USAGE_CHECK = -2146885592;
  
  public static final int CRYPT_E_VERIFY_USAGE_OFFLINE = -2146885591;
  
  public static final int CRYPT_E_NOT_IN_CTL = -2146885590;
  
  public static final int CRYPT_E_NO_TRUSTED_SIGNER = -2146885589;
  
  public static final int CRYPT_E_MISSING_PUBKEY_PARA = -2146885588;
  
  public static final int CRYPT_E_OSS_ERROR = -2146881536;
  
  public static final int OSS_MORE_BUF = -2146881535;
  
  public static final int OSS_NEGATIVE_UINTEGER = -2146881534;
  
  public static final int OSS_PDU_RANGE = -2146881533;
  
  public static final int OSS_MORE_INPUT = -2146881532;
  
  public static final int OSS_DATA_ERROR = -2146881531;
  
  public static final int OSS_BAD_ARG = -2146881530;
  
  public static final int OSS_BAD_VERSION = -2146881529;
  
  public static final int OSS_OUT_MEMORY = -2146881528;
  
  public static final int OSS_PDU_MISMATCH = -2146881527;
  
  public static final int OSS_LIMITED = -2146881526;
  
  public static final int OSS_BAD_PTR = -2146881525;
  
  public static final int OSS_BAD_TIME = -2146881524;
  
  public static final int OSS_INDEFINITE_NOT_SUPPORTED = -2146881523;
  
  public static final int OSS_MEM_ERROR = -2146881522;
  
  public static final int OSS_BAD_TABLE = -2146881521;
  
  public static final int OSS_TOO_LONG = -2146881520;
  
  public static final int OSS_CONSTRAINT_VIOLATED = -2146881519;
  
  public static final int OSS_FATAL_ERROR = -2146881518;
  
  public static final int OSS_ACCESS_SERIALIZATION_ERROR = -2146881517;
  
  public static final int OSS_NULL_TBL = -2146881516;
  
  public static final int OSS_NULL_FCN = -2146881515;
  
  public static final int OSS_BAD_ENCRULES = -2146881514;
  
  public static final int OSS_UNAVAIL_ENCRULES = -2146881513;
  
  public static final int OSS_CANT_OPEN_TRACE_WINDOW = -2146881512;
  
  public static final int OSS_UNIMPLEMENTED = -2146881511;
  
  public static final int OSS_OID_DLL_NOT_LINKED = -2146881510;
  
  public static final int OSS_CANT_OPEN_TRACE_FILE = -2146881509;
  
  public static final int OSS_TRACE_FILE_ALREADY_OPEN = -2146881508;
  
  public static final int OSS_TABLE_MISMATCH = -2146881507;
  
  public static final int OSS_TYPE_NOT_SUPPORTED = -2146881506;
  
  public static final int OSS_REAL_DLL_NOT_LINKED = -2146881505;
  
  public static final int OSS_REAL_CODE_NOT_LINKED = -2146881504;
  
  public static final int OSS_OUT_OF_RANGE = -2146881503;
  
  public static final int OSS_COPIER_DLL_NOT_LINKED = -2146881502;
  
  public static final int OSS_CONSTRAINT_DLL_NOT_LINKED = -2146881501;
  
  public static final int OSS_COMPARATOR_DLL_NOT_LINKED = -2146881500;
  
  public static final int OSS_COMPARATOR_CODE_NOT_LINKED = -2146881499;
  
  public static final int OSS_MEM_MGR_DLL_NOT_LINKED = -2146881498;
  
  public static final int OSS_PDV_DLL_NOT_LINKED = -2146881497;
  
  public static final int OSS_PDV_CODE_NOT_LINKED = -2146881496;
  
  public static final int OSS_API_DLL_NOT_LINKED = -2146881495;
  
  public static final int OSS_BERDER_DLL_NOT_LINKED = -2146881494;
  
  public static final int OSS_PER_DLL_NOT_LINKED = -2146881493;
  
  public static final int OSS_OPEN_TYPE_ERROR = -2146881492;
  
  public static final int OSS_MUTEX_NOT_CREATED = -2146881491;
  
  public static final int OSS_CANT_CLOSE_TRACE_FILE = -2146881490;
  
  public static final int CRYPT_E_ASN1_ERROR = -2146881280;
  
  public static final int CRYPT_E_ASN1_INTERNAL = -2146881279;
  
  public static final int CRYPT_E_ASN1_EOD = -2146881278;
  
  public static final int CRYPT_E_ASN1_CORRUPT = -2146881277;
  
  public static final int CRYPT_E_ASN1_LARGE = -2146881276;
  
  public static final int CRYPT_E_ASN1_CONSTRAINT = -2146881275;
  
  public static final int CRYPT_E_ASN1_MEMORY = -2146881274;
  
  public static final int CRYPT_E_ASN1_OVERFLOW = -2146881273;
  
  public static final int CRYPT_E_ASN1_BADPDU = -2146881272;
  
  public static final int CRYPT_E_ASN1_BADARGS = -2146881271;
  
  public static final int CRYPT_E_ASN1_BADREAL = -2146881270;
  
  public static final int CRYPT_E_ASN1_BADTAG = -2146881269;
  
  public static final int CRYPT_E_ASN1_CHOICE = -2146881268;
  
  public static final int CRYPT_E_ASN1_RULE = -2146881267;
  
  public static final int CRYPT_E_ASN1_UTF8 = -2146881266;
  
  public static final int CRYPT_E_ASN1_PDU_TYPE = -2146881229;
  
  public static final int CRYPT_E_ASN1_NYI = -2146881228;
  
  public static final int CRYPT_E_ASN1_EXTENDED = -2146881023;
  
  public static final int CRYPT_E_ASN1_NOEOD = -2146881022;
  
  public static final int CERTSRV_E_BAD_REQUESTSUBJECT = -2146877439;
  
  public static final int CERTSRV_E_NO_REQUEST = -2146877438;
  
  public static final int CERTSRV_E_BAD_REQUESTSTATUS = -2146877437;
  
  public static final int CERTSRV_E_PROPERTY_EMPTY = -2146877436;
  
  public static final int CERTSRV_E_INVALID_CA_CERTIFICATE = -2146877435;
  
  public static final int CERTSRV_E_SERVER_SUSPENDED = -2146877434;
  
  public static final int CERTSRV_E_ENCODING_LENGTH = -2146877433;
  
  public static final int CERTSRV_E_ROLECONFLICT = -2146877432;
  
  public static final int CERTSRV_E_RESTRICTEDOFFICER = -2146877431;
  
  public static final int CERTSRV_E_KEY_ARCHIVAL_NOT_CONFIGURED = -2146877430;
  
  public static final int CERTSRV_E_NO_VALID_KRA = -2146877429;
  
  public static final int CERTSRV_E_BAD_REQUEST_KEY_ARCHIVAL = -2146877428;
  
  public static final int CERTSRV_E_NO_CAADMIN_DEFINED = -2146877427;
  
  public static final int CERTSRV_E_BAD_RENEWAL_CERT_ATTRIBUTE = -2146877426;
  
  public static final int CERTSRV_E_NO_DB_SESSIONS = -2146877425;
  
  public static final int CERTSRV_E_ALIGNMENT_FAULT = -2146877424;
  
  public static final int CERTSRV_E_ENROLL_DENIED = -2146877423;
  
  public static final int CERTSRV_E_TEMPLATE_DENIED = -2146877422;
  
  public static final int CERTSRV_E_DOWNLEVEL_DC_SSL_OR_UPGRADE = -2146877421;
  
  public static final int CERTSRV_E_UNSUPPORTED_CERT_TYPE = -2146875392;
  
  public static final int CERTSRV_E_NO_CERT_TYPE = -2146875391;
  
  public static final int CERTSRV_E_TEMPLATE_CONFLICT = -2146875390;
  
  public static final int CERTSRV_E_SUBJECT_ALT_NAME_REQUIRED = -2146875389;
  
  public static final int CERTSRV_E_ARCHIVED_KEY_REQUIRED = -2146875388;
  
  public static final int CERTSRV_E_SMIME_REQUIRED = -2146875387;
  
  public static final int CERTSRV_E_BAD_RENEWAL_SUBJECT = -2146875386;
  
  public static final int CERTSRV_E_BAD_TEMPLATE_VERSION = -2146875385;
  
  public static final int CERTSRV_E_TEMPLATE_POLICY_REQUIRED = -2146875384;
  
  public static final int CERTSRV_E_SIGNATURE_POLICY_REQUIRED = -2146875383;
  
  public static final int CERTSRV_E_SIGNATURE_COUNT = -2146875382;
  
  public static final int CERTSRV_E_SIGNATURE_REJECTED = -2146875381;
  
  public static final int CERTSRV_E_ISSUANCE_POLICY_REQUIRED = -2146875380;
  
  public static final int CERTSRV_E_SUBJECT_UPN_REQUIRED = -2146875379;
  
  public static final int CERTSRV_E_SUBJECT_DIRECTORY_GUID_REQUIRED = -2146875378;
  
  public static final int CERTSRV_E_SUBJECT_DNS_REQUIRED = -2146875377;
  
  public static final int CERTSRV_E_ARCHIVED_KEY_UNEXPECTED = -2146875376;
  
  public static final int CERTSRV_E_KEY_LENGTH = -2146875375;
  
  public static final int CERTSRV_E_SUBJECT_EMAIL_REQUIRED = -2146875374;
  
  public static final int CERTSRV_E_UNKNOWN_CERT_TYPE = -2146875373;
  
  public static final int CERTSRV_E_CERT_TYPE_OVERLAP = -2146875372;
  
  public static final int CERTSRV_E_TOO_MANY_SIGNATURES = -2146875371;
  
  public static final int XENROLL_E_KEY_NOT_EXPORTABLE = -2146873344;
  
  public static final int XENROLL_E_CANNOT_ADD_ROOT_CERT = -2146873343;
  
  public static final int XENROLL_E_RESPONSE_KA_HASH_NOT_FOUND = -2146873342;
  
  public static final int XENROLL_E_RESPONSE_UNEXPECTED_KA_HASH = -2146873341;
  
  public static final int XENROLL_E_RESPONSE_KA_HASH_MISMATCH = -2146873340;
  
  public static final int XENROLL_E_KEYSPEC_SMIME_MISMATCH = -2146873339;
  
  public static final int TRUST_E_SYSTEM_ERROR = -2146869247;
  
  public static final int TRUST_E_NO_SIGNER_CERT = -2146869246;
  
  public static final int TRUST_E_COUNTER_SIGNER = -2146869245;
  
  public static final int TRUST_E_CERT_SIGNATURE = -2146869244;
  
  public static final int TRUST_E_TIME_STAMP = -2146869243;
  
  public static final int TRUST_E_BAD_DIGEST = -2146869232;
  
  public static final int TRUST_E_BASIC_CONSTRAINTS = -2146869223;
  
  public static final int TRUST_E_FINANCIAL_CRITERIA = -2146869218;
  
  public static final int MSSIPOTF_E_OUTOFMEMRANGE = -2146865151;
  
  public static final int MSSIPOTF_E_CANTGETOBJECT = -2146865150;
  
  public static final int MSSIPOTF_E_NOHEADTABLE = -2146865149;
  
  public static final int MSSIPOTF_E_BAD_MAGICNUMBER = -2146865148;
  
  public static final int MSSIPOTF_E_BAD_OFFSET_TABLE = -2146865147;
  
  public static final int MSSIPOTF_E_TABLE_TAGORDER = -2146865146;
  
  public static final int MSSIPOTF_E_TABLE_LONGWORD = -2146865145;
  
  public static final int MSSIPOTF_E_BAD_FIRST_TABLE_PLACEMENT = -2146865144;
  
  public static final int MSSIPOTF_E_TABLES_OVERLAP = -2146865143;
  
  public static final int MSSIPOTF_E_TABLE_PADBYTES = -2146865142;
  
  public static final int MSSIPOTF_E_FILETOOSMALL = -2146865141;
  
  public static final int MSSIPOTF_E_TABLE_CHECKSUM = -2146865140;
  
  public static final int MSSIPOTF_E_FILE_CHECKSUM = -2146865139;
  
  public static final int MSSIPOTF_E_FAILED_POLICY = -2146865136;
  
  public static final int MSSIPOTF_E_FAILED_HINTS_CHECK = -2146865135;
  
  public static final int MSSIPOTF_E_NOT_OPENTYPE = -2146865134;
  
  public static final int MSSIPOTF_E_FILE = -2146865133;
  
  public static final int MSSIPOTF_E_CRYPT = -2146865132;
  
  public static final int MSSIPOTF_E_BADVERSION = -2146865131;
  
  public static final int MSSIPOTF_E_DSIG_STRUCTURE = -2146865130;
  
  public static final int MSSIPOTF_E_PCONST_CHECK = -2146865129;
  
  public static final int MSSIPOTF_E_STRUCTURE = -2146865128;
  
  public static final int ERROR_CRED_REQUIRES_CONFIRMATION = -2146865127;
  
  public static final int NTE_OP_OK = 0;
  
  public static final int TRUST_E_PROVIDER_UNKNOWN = -2146762751;
  
  public static final int TRUST_E_ACTION_UNKNOWN = -2146762750;
  
  public static final int TRUST_E_SUBJECT_FORM_UNKNOWN = -2146762749;
  
  public static final int TRUST_E_SUBJECT_NOT_TRUSTED = -2146762748;
  
  public static final int DIGSIG_E_ENCODE = -2146762747;
  
  public static final int DIGSIG_E_DECODE = -2146762746;
  
  public static final int DIGSIG_E_EXTENSIBILITY = -2146762745;
  
  public static final int DIGSIG_E_CRYPTO = -2146762744;
  
  public static final int PERSIST_E_SIZEDEFINITE = -2146762743;
  
  public static final int PERSIST_E_SIZEINDEFINITE = -2146762742;
  
  public static final int PERSIST_E_NOTSELFSIZING = -2146762741;
  
  public static final int TRUST_E_NOSIGNATURE = -2146762496;
  
  public static final int CERT_E_EXPIRED = -2146762495;
  
  public static final int CERT_E_VALIDITYPERIODNESTING = -2146762494;
  
  public static final int CERT_E_ROLE = -2146762493;
  
  public static final int CERT_E_PATHLENCONST = -2146762492;
  
  public static final int CERT_E_CRITICAL = -2146762491;
  
  public static final int CERT_E_PURPOSE = -2146762490;
  
  public static final int CERT_E_ISSUERCHAINING = -2146762489;
  
  public static final int CERT_E_MALFORMED = -2146762488;
  
  public static final int CERT_E_UNTRUSTEDROOT = -2146762487;
  
  public static final int CERT_E_CHAINING = -2146762486;
  
  public static final int TRUST_E_FAIL = -2146762485;
  
  public static final int CERT_E_REVOKED = -2146762484;
  
  public static final int CERT_E_UNTRUSTEDTESTROOT = -2146762483;
  
  public static final int CERT_E_REVOCATION_FAILURE = -2146762482;
  
  public static final int CERT_E_CN_NO_MATCH = -2146762481;
  
  public static final int CERT_E_WRONG_USAGE = -2146762480;
  
  public static final int TRUST_E_EXPLICIT_DISTRUST = -2146762479;
  
  public static final int CERT_E_UNTRUSTEDCA = -2146762478;
  
  public static final int CERT_E_INVALID_POLICY = -2146762477;
  
  public static final int CERT_E_INVALID_NAME = -2146762476;
  
  public static final int SPAPI_E_EXPECTED_SECTION_NAME = -2146500608;
  
  public static final int SPAPI_E_BAD_SECTION_NAME_LINE = -2146500607;
  
  public static final int SPAPI_E_SECTION_NAME_TOO_LONG = -2146500606;
  
  public static final int SPAPI_E_GENERAL_SYNTAX = -2146500605;
  
  public static final int SPAPI_E_WRONG_INF_STYLE = -2146500352;
  
  public static final int SPAPI_E_SECTION_NOT_FOUND = -2146500351;
  
  public static final int SPAPI_E_LINE_NOT_FOUND = -2146500350;
  
  public static final int SPAPI_E_NO_BACKUP = -2146500349;
  
  public static final int SPAPI_E_NO_ASSOCIATED_CLASS = -2146500096;
  
  public static final int SPAPI_E_CLASS_MISMATCH = -2146500095;
  
  public static final int SPAPI_E_DUPLICATE_FOUND = -2146500094;
  
  public static final int SPAPI_E_NO_DRIVER_SELECTED = -2146500093;
  
  public static final int SPAPI_E_KEY_DOES_NOT_EXIST = -2146500092;
  
  public static final int SPAPI_E_INVALID_DEVINST_NAME = -2146500091;
  
  public static final int SPAPI_E_INVALID_CLASS = -2146500090;
  
  public static final int SPAPI_E_DEVINST_ALREADY_EXISTS = -2146500089;
  
  public static final int SPAPI_E_DEVINFO_NOT_REGISTERED = -2146500088;
  
  public static final int SPAPI_E_INVALID_REG_PROPERTY = -2146500087;
  
  public static final int SPAPI_E_NO_INF = -2146500086;
  
  public static final int SPAPI_E_NO_SUCH_DEVINST = -2146500085;
  
  public static final int SPAPI_E_CANT_LOAD_CLASS_ICON = -2146500084;
  
  public static final int SPAPI_E_INVALID_CLASS_INSTALLER = -2146500083;
  
  public static final int SPAPI_E_DI_DO_DEFAULT = -2146500082;
  
  public static final int SPAPI_E_DI_NOFILECOPY = -2146500081;
  
  public static final int SPAPI_E_INVALID_HWPROFILE = -2146500080;
  
  public static final int SPAPI_E_NO_DEVICE_SELECTED = -2146500079;
  
  public static final int SPAPI_E_DEVINFO_LIST_LOCKED = -2146500078;
  
  public static final int SPAPI_E_DEVINFO_DATA_LOCKED = -2146500077;
  
  public static final int SPAPI_E_DI_BAD_PATH = -2146500076;
  
  public static final int SPAPI_E_NO_CLASSINSTALL_PARAMS = -2146500075;
  
  public static final int SPAPI_E_FILEQUEUE_LOCKED = -2146500074;
  
  public static final int SPAPI_E_BAD_SERVICE_INSTALLSECT = -2146500073;
  
  public static final int SPAPI_E_NO_CLASS_DRIVER_LIST = -2146500072;
  
  public static final int SPAPI_E_NO_ASSOCIATED_SERVICE = -2146500071;
  
  public static final int SPAPI_E_NO_DEFAULT_DEVICE_INTERFACE = -2146500070;
  
  public static final int SPAPI_E_DEVICE_INTERFACE_ACTIVE = -2146500069;
  
  public static final int SPAPI_E_DEVICE_INTERFACE_REMOVED = -2146500068;
  
  public static final int SPAPI_E_BAD_INTERFACE_INSTALLSECT = -2146500067;
  
  public static final int SPAPI_E_NO_SUCH_INTERFACE_CLASS = -2146500066;
  
  public static final int SPAPI_E_INVALID_REFERENCE_STRING = -2146500065;
  
  public static final int SPAPI_E_INVALID_MACHINENAME = -2146500064;
  
  public static final int SPAPI_E_REMOTE_COMM_FAILURE = -2146500063;
  
  public static final int SPAPI_E_MACHINE_UNAVAILABLE = -2146500062;
  
  public static final int SPAPI_E_NO_CONFIGMGR_SERVICES = -2146500061;
  
  public static final int SPAPI_E_INVALID_PROPPAGE_PROVIDER = -2146500060;
  
  public static final int SPAPI_E_NO_SUCH_DEVICE_INTERFACE = -2146500059;
  
  public static final int SPAPI_E_DI_POSTPROCESSING_REQUIRED = -2146500058;
  
  public static final int SPAPI_E_INVALID_COINSTALLER = -2146500057;
  
  public static final int SPAPI_E_NO_COMPAT_DRIVERS = -2146500056;
  
  public static final int SPAPI_E_NO_DEVICE_ICON = -2146500055;
  
  public static final int SPAPI_E_INVALID_INF_LOGCONFIG = -2146500054;
  
  public static final int SPAPI_E_DI_DONT_INSTALL = -2146500053;
  
  public static final int SPAPI_E_INVALID_FILTER_DRIVER = -2146500052;
  
  public static final int SPAPI_E_NON_WINDOWS_NT_DRIVER = -2146500051;
  
  public static final int SPAPI_E_NON_WINDOWS_DRIVER = -2146500050;
  
  public static final int SPAPI_E_NO_CATALOG_FOR_OEM_INF = -2146500049;
  
  public static final int SPAPI_E_DEVINSTALL_QUEUE_NONNATIVE = -2146500048;
  
  public static final int SPAPI_E_NOT_DISABLEABLE = -2146500047;
  
  public static final int SPAPI_E_CANT_REMOVE_DEVINST = -2146500046;
  
  public static final int SPAPI_E_INVALID_TARGET = -2146500045;
  
  public static final int SPAPI_E_DRIVER_NONNATIVE = -2146500044;
  
  public static final int SPAPI_E_IN_WOW64 = -2146500043;
  
  public static final int SPAPI_E_SET_SYSTEM_RESTORE_POINT = -2146500042;
  
  public static final int SPAPI_E_INCORRECTLY_COPIED_INF = -2146500041;
  
  public static final int SPAPI_E_SCE_DISABLED = -2146500040;
  
  public static final int SPAPI_E_UNKNOWN_EXCEPTION = -2146500039;
  
  public static final int SPAPI_E_PNP_REGISTRY_ERROR = -2146500038;
  
  public static final int SPAPI_E_REMOTE_REQUEST_UNSUPPORTED = -2146500037;
  
  public static final int SPAPI_E_NOT_AN_INSTALLED_OEM_INF = -2146500036;
  
  public static final int SPAPI_E_INF_IN_USE_BY_DEVICES = -2146500035;
  
  public static final int SPAPI_E_DI_FUNCTION_OBSOLETE = -2146500034;
  
  public static final int SPAPI_E_NO_AUTHENTICODE_CATALOG = -2146500033;
  
  public static final int SPAPI_E_AUTHENTICODE_DISALLOWED = -2146500032;
  
  public static final int SPAPI_E_AUTHENTICODE_TRUSTED_PUBLISHER = -2146500031;
  
  public static final int SPAPI_E_AUTHENTICODE_TRUST_NOT_ESTABLISHED = -2146500030;
  
  public static final int SPAPI_E_AUTHENTICODE_PUBLISHER_NOT_TRUSTED = -2146500029;
  
  public static final int SPAPI_E_SIGNATURE_OSATTRIBUTE_MISMATCH = -2146500028;
  
  public static final int SPAPI_E_ONLY_VALIDATE_VIA_AUTHENTICODE = -2146500027;
  
  public static final int SPAPI_E_DEVICE_INSTALLER_NOT_READY = -2146500026;
  
  public static final int SPAPI_E_DRIVER_STORE_ADD_FAILED = -2146500025;
  
  public static final int SPAPI_E_DEVICE_INSTALL_BLOCKED = -2146500024;
  
  public static final int SPAPI_E_DRIVER_INSTALL_BLOCKED = -2146500023;
  
  public static final int SPAPI_E_WRONG_INF_TYPE = -2146500022;
  
  public static final int SPAPI_E_FILE_HASH_NOT_IN_CATALOG = -2146500021;
  
  public static final int SPAPI_E_DRIVER_STORE_DELETE_FAILED = -2146500020;
  
  public static final int SPAPI_E_UNRECOVERABLE_STACK_OVERFLOW = -2146499840;
  
  public static final int SPAPI_E_ERROR_NOT_INSTALLED = -2146496512;
  
  public static final int SCARD_S_SUCCESS = 0;
  
  public static final int SCARD_F_INTERNAL_ERROR = -2146435071;
  
  public static final int SCARD_E_CANCELLED = -2146435070;
  
  public static final int SCARD_E_INVALID_HANDLE = -2146435069;
  
  public static final int SCARD_E_INVALID_PARAMETER = -2146435068;
  
  public static final int SCARD_E_INVALID_TARGET = -2146435067;
  
  public static final int SCARD_E_NO_MEMORY = -2146435066;
  
  public static final int SCARD_F_WAITED_TOO_LONG = -2146435065;
  
  public static final int SCARD_E_INSUFFICIENT_BUFFER = -2146435064;
  
  public static final int SCARD_E_UNKNOWN_READER = -2146435063;
  
  public static final int SCARD_E_TIMEOUT = -2146435062;
  
  public static final int SCARD_E_SHARING_VIOLATION = -2146435061;
  
  public static final int SCARD_E_NO_SMARTCARD = -2146435060;
  
  public static final int SCARD_E_UNKNOWN_CARD = -2146435059;
  
  public static final int SCARD_E_CANT_DISPOSE = -2146435058;
  
  public static final int SCARD_E_PROTO_MISMATCH = -2146435057;
  
  public static final int SCARD_E_NOT_READY = -2146435056;
  
  public static final int SCARD_E_INVALID_VALUE = -2146435055;
  
  public static final int SCARD_E_SYSTEM_CANCELLED = -2146435054;
  
  public static final int SCARD_F_COMM_ERROR = -2146435053;
  
  public static final int SCARD_F_UNKNOWN_ERROR = -2146435052;
  
  public static final int SCARD_E_INVALID_ATR = -2146435051;
  
  public static final int SCARD_E_NOT_TRANSACTED = -2146435050;
  
  public static final int SCARD_E_READER_UNAVAILABLE = -2146435049;
  
  public static final int SCARD_P_SHUTDOWN = -2146435048;
  
  public static final int SCARD_E_PCI_TOO_SMALL = -2146435047;
  
  public static final int SCARD_E_READER_UNSUPPORTED = -2146435046;
  
  public static final int SCARD_E_DUPLICATE_READER = -2146435045;
  
  public static final int SCARD_E_CARD_UNSUPPORTED = -2146435044;
  
  public static final int SCARD_E_NO_SERVICE = -2146435043;
  
  public static final int SCARD_E_SERVICE_STOPPED = -2146435042;
  
  public static final int SCARD_E_UNEXPECTED = -2146435041;
  
  public static final int SCARD_E_ICC_INSTALLATION = -2146435040;
  
  public static final int SCARD_E_ICC_CREATEORDER = -2146435039;
  
  public static final int SCARD_E_UNSUPPORTED_FEATURE = -2146435038;
  
  public static final int SCARD_E_DIR_NOT_FOUND = -2146435037;
  
  public static final int SCARD_E_FILE_NOT_FOUND = -2146435036;
  
  public static final int SCARD_E_NO_DIR = -2146435035;
  
  public static final int SCARD_E_NO_FILE = -2146435034;
  
  public static final int SCARD_E_NO_ACCESS = -2146435033;
  
  public static final int SCARD_E_WRITE_TOO_MANY = -2146435032;
  
  public static final int SCARD_E_BAD_SEEK = -2146435031;
  
  public static final int SCARD_E_INVALID_CHV = -2146435030;
  
  public static final int SCARD_E_UNKNOWN_RES_MNG = -2146435029;
  
  public static final int SCARD_E_NO_SUCH_CERTIFICATE = -2146435028;
  
  public static final int SCARD_E_CERTIFICATE_UNAVAILABLE = -2146435027;
  
  public static final int SCARD_E_NO_READERS_AVAILABLE = -2146435026;
  
  public static final int SCARD_E_COMM_DATA_LOST = -2146435025;
  
  public static final int SCARD_E_NO_KEY_CONTAINER = -2146435024;
  
  public static final int SCARD_E_SERVER_TOO_BUSY = -2146435023;
  
  public static final int SCARD_W_UNSUPPORTED_CARD = -2146434971;
  
  public static final int SCARD_W_UNRESPONSIVE_CARD = -2146434970;
  
  public static final int SCARD_W_UNPOWERED_CARD = -2146434969;
  
  public static final int SCARD_W_RESET_CARD = -2146434968;
  
  public static final int SCARD_W_REMOVED_CARD = -2146434967;
  
  public static final int SCARD_W_SECURITY_VIOLATION = -2146434966;
  
  public static final int SCARD_W_WRONG_CHV = -2146434965;
  
  public static final int SCARD_W_CHV_BLOCKED = -2146434964;
  
  public static final int SCARD_W_EOF = -2146434963;
  
  public static final int SCARD_W_CANCELLED_BY_USER = -2146434962;
  
  public static final int SCARD_W_CARD_NOT_AUTHENTICATED = -2146434961;
  
  public static final int SCARD_W_CACHE_ITEM_NOT_FOUND = -2146434960;
  
  public static final int SCARD_W_CACHE_ITEM_STALE = -2146434959;
  
  public static final int SCARD_W_CACHE_ITEM_TOO_BIG = -2146434958;
  
  public static final int COMADMIN_E_OBJECTERRORS = -2146368511;
  
  public static final int COMADMIN_E_OBJECTINVALID = -2146368510;
  
  public static final int COMADMIN_E_KEYMISSING = -2146368509;
  
  public static final int COMADMIN_E_ALREADYINSTALLED = -2146368508;
  
  public static final int COMADMIN_E_APP_FILE_WRITEFAIL = -2146368505;
  
  public static final int COMADMIN_E_APP_FILE_READFAIL = -2146368504;
  
  public static final int COMADMIN_E_APP_FILE_VERSION = -2146368503;
  
  public static final int COMADMIN_E_BADPATH = -2146368502;
  
  public static final int COMADMIN_E_APPLICATIONEXISTS = -2146368501;
  
  public static final int COMADMIN_E_ROLEEXISTS = -2146368500;
  
  public static final int COMADMIN_E_CANTCOPYFILE = -2146368499;
  
  public static final int COMADMIN_E_NOUSER = -2146368497;
  
  public static final int COMADMIN_E_INVALIDUSERIDS = -2146368496;
  
  public static final int COMADMIN_E_NOREGISTRYCLSID = -2146368495;
  
  public static final int COMADMIN_E_BADREGISTRYPROGID = -2146368494;
  
  public static final int COMADMIN_E_AUTHENTICATIONLEVEL = -2146368493;
  
  public static final int COMADMIN_E_USERPASSWDNOTVALID = -2146368492;
  
  public static final int COMADMIN_E_CLSIDORIIDMISMATCH = -2146368488;
  
  public static final int COMADMIN_E_REMOTEINTERFACE = -2146368487;
  
  public static final int COMADMIN_E_DLLREGISTERSERVER = -2146368486;
  
  public static final int COMADMIN_E_NOSERVERSHARE = -2146368485;
  
  public static final int COMADMIN_E_DLLLOADFAILED = -2146368483;
  
  public static final int COMADMIN_E_BADREGISTRYLIBID = -2146368482;
  
  public static final int COMADMIN_E_APPDIRNOTFOUND = -2146368481;
  
  public static final int COMADMIN_E_REGISTRARFAILED = -2146368477;
  
  public static final int COMADMIN_E_COMPFILE_DOESNOTEXIST = -2146368476;
  
  public static final int COMADMIN_E_COMPFILE_LOADDLLFAIL = -2146368475;
  
  public static final int COMADMIN_E_COMPFILE_GETCLASSOBJ = -2146368474;
  
  public static final int COMADMIN_E_COMPFILE_CLASSNOTAVAIL = -2146368473;
  
  public static final int COMADMIN_E_COMPFILE_BADTLB = -2146368472;
  
  public static final int COMADMIN_E_COMPFILE_NOTINSTALLABLE = -2146368471;
  
  public static final int COMADMIN_E_NOTCHANGEABLE = -2146368470;
  
  public static final int COMADMIN_E_NOTDELETEABLE = -2146368469;
  
  public static final int COMADMIN_E_SESSION = -2146368468;
  
  public static final int COMADMIN_E_COMP_MOVE_LOCKED = -2146368467;
  
  public static final int COMADMIN_E_COMP_MOVE_BAD_DEST = -2146368466;
  
  public static final int COMADMIN_E_REGISTERTLB = -2146368464;
  
  public static final int COMADMIN_E_SYSTEMAPP = -2146368461;
  
  public static final int COMADMIN_E_COMPFILE_NOREGISTRAR = -2146368460;
  
  public static final int COMADMIN_E_COREQCOMPINSTALLED = -2146368459;
  
  public static final int COMADMIN_E_SERVICENOTINSTALLED = -2146368458;
  
  public static final int COMADMIN_E_PROPERTYSAVEFAILED = -2146368457;
  
  public static final int COMADMIN_E_OBJECTEXISTS = -2146368456;
  
  public static final int COMADMIN_E_COMPONENTEXISTS = -2146368455;
  
  public static final int COMADMIN_E_REGFILE_CORRUPT = -2146368453;
  
  public static final int COMADMIN_E_PROPERTY_OVERFLOW = -2146368452;
  
  public static final int COMADMIN_E_NOTINREGISTRY = -2146368450;
  
  public static final int COMADMIN_E_OBJECTNOTPOOLABLE = -2146368449;
  
  public static final int COMADMIN_E_APPLID_MATCHES_CLSID = -2146368442;
  
  public static final int COMADMIN_E_ROLE_DOES_NOT_EXIST = -2146368441;
  
  public static final int COMADMIN_E_START_APP_NEEDS_COMPONENTS = -2146368440;
  
  public static final int COMADMIN_E_REQUIRES_DIFFERENT_PLATFORM = -2146368439;
  
  public static final int COMADMIN_E_CAN_NOT_EXPORT_APP_PROXY = -2146368438;
  
  public static final int COMADMIN_E_CAN_NOT_START_APP = -2146368437;
  
  public static final int COMADMIN_E_CAN_NOT_EXPORT_SYS_APP = -2146368436;
  
  public static final int COMADMIN_E_CANT_SUBSCRIBE_TO_COMPONENT = -2146368435;
  
  public static final int COMADMIN_E_EVENTCLASS_CANT_BE_SUBSCRIBER = -2146368434;
  
  public static final int COMADMIN_E_LIB_APP_PROXY_INCOMPATIBLE = -2146368433;
  
  public static final int COMADMIN_E_BASE_PARTITION_ONLY = -2146368432;
  
  public static final int COMADMIN_E_START_APP_DISABLED = -2146368431;
  
  public static final int COMADMIN_E_CAT_DUPLICATE_PARTITION_NAME = -2146368425;
  
  public static final int COMADMIN_E_CAT_INVALID_PARTITION_NAME = -2146368424;
  
  public static final int COMADMIN_E_CAT_PARTITION_IN_USE = -2146368423;
  
  public static final int COMADMIN_E_FILE_PARTITION_DUPLICATE_FILES = -2146368422;
  
  public static final int COMADMIN_E_CAT_IMPORTED_COMPONENTS_NOT_ALLOWED = -2146368421;
  
  public static final int COMADMIN_E_AMBIGUOUS_APPLICATION_NAME = -2146368420;
  
  public static final int COMADMIN_E_AMBIGUOUS_PARTITION_NAME = -2146368419;
  
  public static final int COMADMIN_E_REGDB_NOTINITIALIZED = -2146368398;
  
  public static final int COMADMIN_E_REGDB_NOTOPEN = -2146368397;
  
  public static final int COMADMIN_E_REGDB_SYSTEMERR = -2146368396;
  
  public static final int COMADMIN_E_REGDB_ALREADYRUNNING = -2146368395;
  
  public static final int COMADMIN_E_MIG_VERSIONNOTSUPPORTED = -2146368384;
  
  public static final int COMADMIN_E_MIG_SCHEMANOTFOUND = -2146368383;
  
  public static final int COMADMIN_E_CAT_BITNESSMISMATCH = -2146368382;
  
  public static final int COMADMIN_E_CAT_UNACCEPTABLEBITNESS = -2146368381;
  
  public static final int COMADMIN_E_CAT_WRONGAPPBITNESS = -2146368380;
  
  public static final int COMADMIN_E_CAT_PAUSE_RESUME_NOT_SUPPORTED = -2146368379;
  
  public static final int COMADMIN_E_CAT_SERVERFAULT = -2146368378;
  
  public static final int COMQC_E_APPLICATION_NOT_QUEUED = -2146368000;
  
  public static final int COMQC_E_NO_QUEUEABLE_INTERFACES = -2146367999;
  
  public static final int COMQC_E_QUEUING_SERVICE_NOT_AVAILABLE = -2146367998;
  
  public static final int COMQC_E_NO_IPERSISTSTREAM = -2146367997;
  
  public static final int COMQC_E_BAD_MESSAGE = -2146367996;
  
  public static final int COMQC_E_UNAUTHENTICATED = -2146367995;
  
  public static final int COMQC_E_UNTRUSTED_ENQUEUER = -2146367994;
  
  public static final int MSDTC_E_DUPLICATE_RESOURCE = -2146367743;
  
  public static final int COMADMIN_E_OBJECT_PARENT_MISSING = -2146367480;
  
  public static final int COMADMIN_E_OBJECT_DOES_NOT_EXIST = -2146367479;
  
  public static final int COMADMIN_E_APP_NOT_RUNNING = -2146367478;
  
  public static final int COMADMIN_E_INVALID_PARTITION = -2146367477;
  
  public static final int COMADMIN_E_SVCAPP_NOT_POOLABLE_OR_RECYCLABLE = -2146367475;
  
  public static final int COMADMIN_E_USER_IN_SET = -2146367474;
  
  public static final int COMADMIN_E_CANTRECYCLELIBRARYAPPS = -2146367473;
  
  public static final int COMADMIN_E_CANTRECYCLESERVICEAPPS = -2146367471;
  
  public static final int COMADMIN_E_PROCESSALREADYRECYCLED = -2146367470;
  
  public static final int COMADMIN_E_PAUSEDPROCESSMAYNOTBERECYCLED = -2146367469;
  
  public static final int COMADMIN_E_CANTMAKEINPROCSERVICE = -2146367468;
  
  public static final int COMADMIN_E_PROGIDINUSEBYCLSID = -2146367467;
  
  public static final int COMADMIN_E_DEFAULT_PARTITION_NOT_IN_SET = -2146367466;
  
  public static final int COMADMIN_E_RECYCLEDPROCESSMAYNOTBEPAUSED = -2146367465;
  
  public static final int COMADMIN_E_PARTITION_ACCESSDENIED = -2146367464;
  
  public static final int COMADMIN_E_PARTITION_MSI_ONLY = -2146367463;
  
  public static final int COMADMIN_E_LEGACYCOMPS_NOT_ALLOWED_IN_1_0_FORMAT = -2146367462;
  
  public static final int COMADMIN_E_LEGACYCOMPS_NOT_ALLOWED_IN_NONBASE_PARTITIONS = -2146367461;
  
  public static final int COMADMIN_E_COMP_MOVE_SOURCE = -2146367460;
  
  public static final int COMADMIN_E_COMP_MOVE_DEST = -2146367459;
  
  public static final int COMADMIN_E_COMP_MOVE_PRIVATE = -2146367458;
  
  public static final int COMADMIN_E_BASEPARTITION_REQUIRED_IN_SET = -2146367457;
  
  public static final int COMADMIN_E_CANNOT_ALIAS_EVENTCLASS = -2146367456;
  
  public static final int COMADMIN_E_PRIVATE_ACCESSDENIED = -2146367455;
  
  public static final int COMADMIN_E_SAFERINVALID = -2146367454;
  
  public static final int COMADMIN_E_REGISTRY_ACCESSDENIED = -2146367453;
  
  public static final int COMADMIN_E_PARTITIONS_DISABLED = -2146367452;
  
  public static final int ERROR_FLT_IO_COMPLETE = 2031617;
  
  public static final int ERROR_FLT_NO_HANDLER_DEFINED = -2145452031;
  
  public static final int ERROR_FLT_CONTEXT_ALREADY_DEFINED = -2145452030;
  
  public static final int ERROR_FLT_INVALID_ASYNCHRONOUS_REQUEST = -2145452029;
  
  public static final int ERROR_FLT_DISALLOW_FAST_IO = -2145452028;
  
  public static final int ERROR_FLT_INVALID_NAME_REQUEST = -2145452027;
  
  public static final int ERROR_FLT_NOT_SAFE_TO_POST_OPERATION = -2145452026;
  
  public static final int ERROR_FLT_NOT_INITIALIZED = -2145452025;
  
  public static final int ERROR_FLT_FILTER_NOT_READY = -2145452024;
  
  public static final int ERROR_FLT_POST_OPERATION_CLEANUP = -2145452023;
  
  public static final int ERROR_FLT_INTERNAL_ERROR = -2145452022;
  
  public static final int ERROR_FLT_DELETING_OBJECT = -2145452021;
  
  public static final int ERROR_FLT_MUST_BE_NONPAGED_POOL = -2145452020;
  
  public static final int ERROR_FLT_DUPLICATE_ENTRY = -2145452019;
  
  public static final int ERROR_FLT_CBDQ_DISABLED = -2145452018;
  
  public static final int ERROR_FLT_DO_NOT_ATTACH = -2145452017;
  
  public static final int ERROR_FLT_DO_NOT_DETACH = -2145452016;
  
  public static final int ERROR_FLT_INSTANCE_ALTITUDE_COLLISION = -2145452015;
  
  public static final int ERROR_FLT_INSTANCE_NAME_COLLISION = -2145452014;
  
  public static final int ERROR_FLT_FILTER_NOT_FOUND = -2145452013;
  
  public static final int ERROR_FLT_VOLUME_NOT_FOUND = -2145452012;
  
  public static final int ERROR_FLT_INSTANCE_NOT_FOUND = -2145452011;
  
  public static final int ERROR_FLT_CONTEXT_ALLOCATION_NOT_FOUND = -2145452010;
  
  public static final int ERROR_FLT_INVALID_CONTEXT_REGISTRATION = -2145452009;
  
  public static final int ERROR_FLT_NAME_CACHE_MISS = -2145452008;
  
  public static final int ERROR_FLT_NO_DEVICE_OBJECT = -2145452007;
  
  public static final int ERROR_FLT_VOLUME_ALREADY_MOUNTED = -2145452006;
  
  public static final int ERROR_FLT_ALREADY_ENLISTED = -2145452005;
  
  public static final int ERROR_FLT_CONTEXT_ALREADY_LINKED = -2145452004;
  
  public static final int ERROR_FLT_NO_WAITER_FOR_REPLY = -2145452000;
  
  public static final int ERROR_HUNG_DISPLAY_DRIVER_THREAD = -2144993279;
  
  public static final int DWM_E_COMPOSITIONDISABLED = -2144980991;
  
  public static final int DWM_E_REMOTING_NOT_SUPPORTED = -2144980990;
  
  public static final int DWM_E_NO_REDIRECTION_SURFACE_AVAILABLE = -2144980989;
  
  public static final int DWM_E_NOT_QUEUING_PRESENTS = -2144980988;
  
  public static final int ERROR_MONITOR_NO_DESCRIPTOR = -2144989183;
  
  public static final int ERROR_MONITOR_UNKNOWN_DESCRIPTOR_FORMAT = -2144989182;
  
  public static final int ERROR_MONITOR_INVALID_DESCRIPTOR_CHECKSUM = -1071247357;
  
  public static final int ERROR_MONITOR_INVALID_STANDARD_TIMING_BLOCK = -1071247356;
  
  public static final int ERROR_MONITOR_WMI_DATABLOCK_REGISTRATION_FAILED = -1071247355;
  
  public static final int ERROR_MONITOR_INVALID_SERIAL_NUMBER_MONDSC_BLOCK = -1071247354;
  
  public static final int ERROR_MONITOR_INVALID_USER_FRIENDLY_MONDSC_BLOCK = -1071247353;
  
  public static final int ERROR_MONITOR_NO_MORE_DESCRIPTOR_DATA = -1071247352;
  
  public static final int ERROR_MONITOR_INVALID_DETAILED_TIMING_BLOCK = -1071247351;
  
  public static final int ERROR_GRAPHICS_NOT_EXCLUSIVE_MODE_OWNER = -1071243264;
  
  public static final int ERROR_GRAPHICS_INSUFFICIENT_DMA_BUFFER = -1071243263;
  
  public static final int ERROR_GRAPHICS_INVALID_DISPLAY_ADAPTER = -1071243262;
  
  public static final int ERROR_GRAPHICS_ADAPTER_WAS_RESET = -1071243261;
  
  public static final int ERROR_GRAPHICS_INVALID_DRIVER_MODEL = -1071243260;
  
  public static final int ERROR_GRAPHICS_PRESENT_MODE_CHANGED = -1071243259;
  
  public static final int ERROR_GRAPHICS_PRESENT_OCCLUDED = -1071243258;
  
  public static final int ERROR_GRAPHICS_PRESENT_DENIED = -1071243257;
  
  public static final int ERROR_GRAPHICS_CANNOTCOLORCONVERT = -1071243256;
  
  public static final int ERROR_GRAPHICS_DRIVER_MISMATCH = -1071243255;
  
  public static final int ERROR_GRAPHICS_PARTIAL_DATA_POPULATED = 1076240394;
  
  public static final int ERROR_GRAPHICS_NO_VIDEO_MEMORY = -1071243008;
  
  public static final int ERROR_GRAPHICS_CANT_LOCK_MEMORY = -1071243007;
  
  public static final int ERROR_GRAPHICS_ALLOCATION_BUSY = -1071243006;
  
  public static final int ERROR_GRAPHICS_TOO_MANY_REFERENCES = -1071243005;
  
  public static final int ERROR_GRAPHICS_TRY_AGAIN_LATER = -1071243004;
  
  public static final int ERROR_GRAPHICS_TRY_AGAIN_NOW = -1071243003;
  
  public static final int ERROR_GRAPHICS_ALLOCATION_INVALID = -1071243002;
  
  public static final int ERROR_GRAPHICS_UNSWIZZLING_APERTURE_UNAVAILABLE = -1071243001;
  
  public static final int ERROR_GRAPHICS_UNSWIZZLING_APERTURE_UNSUPPORTED = -1071243000;
  
  public static final int ERROR_GRAPHICS_CANT_EVICT_PINNED_ALLOCATION = -1071242999;
  
  public static final int ERROR_GRAPHICS_INVALID_ALLOCATION_USAGE = -1071242992;
  
  public static final int ERROR_GRAPHICS_CANT_RENDER_LOCKED_ALLOCATION = -1071242991;
  
  public static final int ERROR_GRAPHICS_ALLOCATION_CLOSED = -1071242990;
  
  public static final int ERROR_GRAPHICS_INVALID_ALLOCATION_INSTANCE = -1071242989;
  
  public static final int ERROR_GRAPHICS_INVALID_ALLOCATION_HANDLE = -1071242988;
  
  public static final int ERROR_GRAPHICS_WRONG_ALLOCATION_DEVICE = -1071242987;
  
  public static final int ERROR_GRAPHICS_ALLOCATION_CONTENT_LOST = -1071242986;
  
  public static final int ERROR_GRAPHICS_GPU_EXCEPTION_ON_DEVICE = -1071242752;
  
  public static final int ERROR_GRAPHICS_INVALID_VIDPN_TOPOLOGY = -1071242496;
  
  public static final int ERROR_GRAPHICS_VIDPN_TOPOLOGY_NOT_SUPPORTED = -1071242495;
  
  public static final int ERROR_GRAPHICS_VIDPN_TOPOLOGY_CURRENTLY_NOT_SUPPORTED = -1071242494;
  
  public static final int ERROR_GRAPHICS_INVALID_VIDPN = -1071242493;
  
  public static final int ERROR_GRAPHICS_INVALID_VIDEO_PRESENT_SOURCE = -1071242492;
  
  public static final int ERROR_GRAPHICS_INVALID_VIDEO_PRESENT_TARGET = -1071242491;
  
  public static final int ERROR_GRAPHICS_VIDPN_MODALITY_NOT_SUPPORTED = -1071242490;
  
  public static final int ERROR_GRAPHICS_MODE_NOT_PINNED = 2499335;
  
  public static final int ERROR_GRAPHICS_INVALID_VIDPN_SOURCEMODESET = -1071242488;
  
  public static final int ERROR_GRAPHICS_INVALID_VIDPN_TARGETMODESET = -1071242487;
  
  public static final int ERROR_GRAPHICS_INVALID_FREQUENCY = -1071242486;
  
  public static final int ERROR_GRAPHICS_INVALID_ACTIVE_REGION = -1071242485;
  
  public static final int ERROR_GRAPHICS_INVALID_TOTAL_REGION = -1071242484;
  
  public static final int ERROR_GRAPHICS_INVALID_VIDEO_PRESENT_SOURCE_MODE = -1071242480;
  
  public static final int ERROR_GRAPHICS_INVALID_VIDEO_PRESENT_TARGET_MODE = -1071242479;
  
  public static final int ERROR_GRAPHICS_PINNED_MODE_MUST_REMAIN_IN_SET = -1071242478;
  
  public static final int ERROR_GRAPHICS_PATH_ALREADY_IN_TOPOLOGY = -1071242477;
  
  public static final int ERROR_GRAPHICS_MODE_ALREADY_IN_MODESET = -1071242476;
  
  public static final int ERROR_GRAPHICS_INVALID_VIDEOPRESENTSOURCESET = -1071242475;
  
  public static final int ERROR_GRAPHICS_INVALID_VIDEOPRESENTTARGETSET = -1071242474;
  
  public static final int ERROR_GRAPHICS_SOURCE_ALREADY_IN_SET = -1071242473;
  
  public static final int ERROR_GRAPHICS_TARGET_ALREADY_IN_SET = -1071242472;
  
  public static final int ERROR_GRAPHICS_INVALID_VIDPN_PRESENT_PATH = -1071242471;
  
  public static final int ERROR_GRAPHICS_NO_RECOMMENDED_VIDPN_TOPOLOGY = -1071242470;
  
  public static final int ERROR_GRAPHICS_INVALID_MONITOR_FREQUENCYRANGESET = -1071242469;
  
  public static final int ERROR_GRAPHICS_INVALID_MONITOR_FREQUENCYRANGE = -1071242468;
  
  public static final int ERROR_GRAPHICS_FREQUENCYRANGE_NOT_IN_SET = -1071242467;
  
  public static final int ERROR_GRAPHICS_NO_PREFERRED_MODE = 2499358;
  
  public static final int ERROR_GRAPHICS_FREQUENCYRANGE_ALREADY_IN_SET = -1071242465;
  
  public static final int ERROR_GRAPHICS_STALE_MODESET = -1071242464;
  
  public static final int ERROR_GRAPHICS_INVALID_MONITOR_SOURCEMODESET = -1071242463;
  
  public static final int ERROR_GRAPHICS_INVALID_MONITOR_SOURCE_MODE = -1071242462;
  
  public static final int ERROR_GRAPHICS_NO_RECOMMENDED_FUNCTIONAL_VIDPN = -1071242461;
  
  public static final int ERROR_GRAPHICS_MODE_ID_MUST_BE_UNIQUE = -1071242460;
  
  public static final int ERROR_GRAPHICS_EMPTY_ADAPTER_MONITOR_MODE_SUPPORT_INTERSECTION = -1071242459;
  
  public static final int ERROR_GRAPHICS_VIDEO_PRESENT_TARGETS_LESS_THAN_SOURCES = -1071242458;
  
  public static final int ERROR_GRAPHICS_PATH_NOT_IN_TOPOLOGY = -1071242457;
  
  public static final int ERROR_GRAPHICS_ADAPTER_MUST_HAVE_AT_LEAST_ONE_SOURCE = -1071242456;
  
  public static final int ERROR_GRAPHICS_ADAPTER_MUST_HAVE_AT_LEAST_ONE_TARGET = -1071242455;
  
  public static final int ERROR_GRAPHICS_INVALID_MONITORDESCRIPTORSET = -1071242454;
  
  public static final int ERROR_GRAPHICS_INVALID_MONITORDESCRIPTOR = -1071242453;
  
  public static final int ERROR_GRAPHICS_MONITORDESCRIPTOR_NOT_IN_SET = -1071242452;
  
  public static final int ERROR_GRAPHICS_MONITORDESCRIPTOR_ALREADY_IN_SET = -1071242451;
  
  public static final int ERROR_GRAPHICS_MONITORDESCRIPTOR_ID_MUST_BE_UNIQUE = -1071242450;
  
  public static final int ERROR_GRAPHICS_INVALID_VIDPN_TARGET_SUBSET_TYPE = -1071242449;
  
  public static final int ERROR_GRAPHICS_RESOURCES_NOT_RELATED = -1071242448;
  
  public static final int ERROR_GRAPHICS_SOURCE_ID_MUST_BE_UNIQUE = -1071242447;
  
  public static final int ERROR_GRAPHICS_TARGET_ID_MUST_BE_UNIQUE = -1071242446;
  
  public static final int ERROR_GRAPHICS_NO_AVAILABLE_VIDPN_TARGET = -1071242445;
  
  public static final int ERROR_GRAPHICS_MONITOR_COULD_NOT_BE_ASSOCIATED_WITH_ADAPTER = -1071242444;
  
  public static final int ERROR_GRAPHICS_NO_VIDPNMGR = -1071242443;
  
  public static final int ERROR_GRAPHICS_NO_ACTIVE_VIDPN = -1071242442;
  
  public static final int ERROR_GRAPHICS_STALE_VIDPN_TOPOLOGY = -1071242441;
  
  public static final int ERROR_GRAPHICS_MONITOR_NOT_CONNECTED = -1071242440;
  
  public static final int ERROR_GRAPHICS_SOURCE_NOT_IN_TOPOLOGY = -1071242439;
  
  public static final int ERROR_GRAPHICS_INVALID_PRIMARYSURFACE_SIZE = -1071242438;
  
  public static final int ERROR_GRAPHICS_INVALID_VISIBLEREGION_SIZE = -1071242437;
  
  public static final int ERROR_GRAPHICS_INVALID_STRIDE = -1071242436;
  
  public static final int ERROR_GRAPHICS_INVALID_PIXELFORMAT = -1071242435;
  
  public static final int ERROR_GRAPHICS_INVALID_COLORBASIS = -1071242434;
  
  public static final int ERROR_GRAPHICS_INVALID_PIXELVALUEACCESSMODE = -1071242433;
  
  public static final int ERROR_GRAPHICS_TARGET_NOT_IN_TOPOLOGY = -1071242432;
  
  public static final int ERROR_GRAPHICS_NO_DISPLAY_MODE_MANAGEMENT_SUPPORT = -1071242431;
  
  public static final int ERROR_GRAPHICS_VIDPN_SOURCE_IN_USE = -1071242430;
  
  public static final int ERROR_GRAPHICS_CANT_ACCESS_ACTIVE_VIDPN = -1071242429;
  
  public static final int ERROR_GRAPHICS_INVALID_PATH_IMPORTANCE_ORDINAL = -1071242428;
  
  public static final int ERROR_GRAPHICS_INVALID_PATH_CONTENT_GEOMETRY_TRANSFORMATION = -1071242427;
  
  public static final int ERROR_GRAPHICS_PATH_CONTENT_GEOMETRY_TRANSFORMATION_NOT_SUPPORTED = -1071242426;
  
  public static final int ERROR_GRAPHICS_INVALID_GAMMA_RAMP = -1071242425;
  
  public static final int ERROR_GRAPHICS_GAMMA_RAMP_NOT_SUPPORTED = -1071242424;
  
  public static final int ERROR_GRAPHICS_MULTISAMPLING_NOT_SUPPORTED = -1071242423;
  
  public static final int ERROR_GRAPHICS_MODE_NOT_IN_MODESET = -1071242422;
  
  public static final int ERROR_GRAPHICS_DATASET_IS_EMPTY = 2499403;
  
  public static final int ERROR_GRAPHICS_NO_MORE_ELEMENTS_IN_DATASET = 2499404;
  
  public static final int ERROR_GRAPHICS_INVALID_VIDPN_TOPOLOGY_RECOMMENDATION_REASON = -1071242419;
  
  public static final int ERROR_GRAPHICS_INVALID_PATH_CONTENT_TYPE = -1071242418;
  
  public static final int ERROR_GRAPHICS_INVALID_COPYPROTECTION_TYPE = -1071242417;
  
  public static final int ERROR_GRAPHICS_UNASSIGNED_MODESET_ALREADY_EXISTS = -1071242416;
  
  public static final int ERROR_GRAPHICS_PATH_CONTENT_GEOMETRY_TRANSFORMATION_NOT_PINNED = 2499409;
  
  public static final int ERROR_GRAPHICS_INVALID_SCANLINE_ORDERING = -1071242414;
  
  public static final int ERROR_GRAPHICS_TOPOLOGY_CHANGES_NOT_ALLOWED = -1071242413;
  
  public static final int ERROR_GRAPHICS_NO_AVAILABLE_IMPORTANCE_ORDINALS = -1071242412;
  
  public static final int ERROR_GRAPHICS_INCOMPATIBLE_PRIVATE_FORMAT = -1071242411;
  
  public static final int ERROR_GRAPHICS_INVALID_MODE_PRUNING_ALGORITHM = -1071242410;
  
  public static final int ERROR_GRAPHICS_INVALID_MONITOR_CAPABILITY_ORIGIN = -1071242409;
  
  public static final int ERROR_GRAPHICS_INVALID_MONITOR_FREQUENCYRANGE_CONSTRAINT = -1071242408;
  
  public static final int ERROR_GRAPHICS_MAX_NUM_PATHS_REACHED = -1071242407;
  
  public static final int ERROR_GRAPHICS_CANCEL_VIDPN_TOPOLOGY_AUGMENTATION = -1071242406;
  
  public static final int ERROR_GRAPHICS_INVALID_CLIENT_TYPE = -1071242405;
  
  public static final int ERROR_GRAPHICS_CLIENTVIDPN_NOT_SET = -1071242404;
  
  public static final int ERROR_GRAPHICS_SPECIFIED_CHILD_ALREADY_CONNECTED = -1071242240;
  
  public static final int ERROR_GRAPHICS_CHILD_DESCRIPTOR_NOT_SUPPORTED = -1071242239;
  
  public static final int ERROR_GRAPHICS_UNKNOWN_CHILD_STATUS = 1076241455;
  
  public static final int ERROR_GRAPHICS_NOT_A_LINKED_ADAPTER = -1071242192;
  
  public static final int ERROR_GRAPHICS_LEADLINK_NOT_ENUMERATED = -1071242191;
  
  public static final int ERROR_GRAPHICS_CHAINLINKS_NOT_ENUMERATED = -1071242190;
  
  public static final int ERROR_GRAPHICS_ADAPTER_CHAIN_NOT_READY = -1071242189;
  
  public static final int ERROR_GRAPHICS_CHAINLINKS_NOT_STARTED = -1071242188;
  
  public static final int ERROR_GRAPHICS_CHAINLINKS_NOT_POWERED_ON = -1071242187;
  
  public static final int ERROR_GRAPHICS_INCONSISTENT_DEVICE_LINK_STATE = -1071242186;
  
  public static final int ERROR_GRAPHICS_LEADLINK_START_DEFERRED = 1076241463;
  
  public static final int ERROR_GRAPHICS_NOT_POST_DEVICE_DRIVER = -1071242184;
  
  public static final int ERROR_GRAPHICS_POLLING_TOO_FREQUENTLY = 1076241465;
  
  public static final int ERROR_GRAPHICS_START_DEFERRED = 1076241466;
  
  public static final int ERROR_GRAPHICS_ADAPTER_ACCESS_NOT_EXCLUDED = -1071242181;
  
  public static final int ERROR_GRAPHICS_OPM_NOT_SUPPORTED = -1071241984;
  
  public static final int ERROR_GRAPHICS_COPP_NOT_SUPPORTED = -1071241983;
  
  public static final int ERROR_GRAPHICS_UAB_NOT_SUPPORTED = -1071241982;
  
  public static final int ERROR_GRAPHICS_OPM_INVALID_ENCRYPTED_PARAMETERS = -1071241981;
  
  public static final int ERROR_GRAPHICS_OPM_NO_VIDEO_OUTPUTS_EXIST = -1071241979;
  
  public static final int ERROR_GRAPHICS_OPM_INTERNAL_ERROR = -1071241973;
  
  public static final int ERROR_GRAPHICS_OPM_INVALID_HANDLE = -1071241972;
  
  public static final int ERROR_GRAPHICS_PVP_INVALID_CERTIFICATE_LENGTH = -1071241970;
  
  public static final int ERROR_GRAPHICS_OPM_SPANNING_MODE_ENABLED = -1071241969;
  
  public static final int ERROR_GRAPHICS_OPM_THEATER_MODE_ENABLED = -1071241968;
  
  public static final int ERROR_GRAPHICS_PVP_HFS_FAILED = -1071241967;
  
  public static final int ERROR_GRAPHICS_OPM_INVALID_SRM = -1071241966;
  
  public static final int ERROR_GRAPHICS_OPM_OUTPUT_DOES_NOT_SUPPORT_HDCP = -1071241965;
  
  public static final int ERROR_GRAPHICS_OPM_OUTPUT_DOES_NOT_SUPPORT_ACP = -1071241964;
  
  public static final int ERROR_GRAPHICS_OPM_OUTPUT_DOES_NOT_SUPPORT_CGMSA = -1071241963;
  
  public static final int ERROR_GRAPHICS_OPM_HDCP_SRM_NEVER_SET = -1071241962;
  
  public static final int ERROR_GRAPHICS_OPM_RESOLUTION_TOO_HIGH = -1071241961;
  
  public static final int ERROR_GRAPHICS_OPM_ALL_HDCP_HARDWARE_ALREADY_IN_USE = -1071241960;
  
  public static final int ERROR_GRAPHICS_OPM_VIDEO_OUTPUT_NO_LONGER_EXISTS = -1071241958;
  
  public static final int ERROR_GRAPHICS_OPM_SESSION_TYPE_CHANGE_IN_PROGRESS = -1071241957;
  
  public static final int ERROR_GRAPHICS_OPM_VIDEO_OUTPUT_DOES_NOT_HAVE_COPP_SEMANTICS = -1071241956;
  
  public static final int ERROR_GRAPHICS_OPM_INVALID_INFORMATION_REQUEST = -1071241955;
  
  public static final int ERROR_GRAPHICS_OPM_DRIVER_INTERNAL_ERROR = -1071241954;
  
  public static final int ERROR_GRAPHICS_OPM_VIDEO_OUTPUT_DOES_NOT_HAVE_OPM_SEMANTICS = -1071241953;
  
  public static final int ERROR_GRAPHICS_OPM_SIGNALING_NOT_SUPPORTED = -1071241952;
  
  public static final int ERROR_GRAPHICS_OPM_INVALID_CONFIGURATION_REQUEST = -1071241951;
  
  public static final int ERROR_GRAPHICS_I2C_NOT_SUPPORTED = -1071241856;
  
  public static final int ERROR_GRAPHICS_I2C_DEVICE_DOES_NOT_EXIST = -1071241855;
  
  public static final int ERROR_GRAPHICS_I2C_ERROR_TRANSMITTING_DATA = -1071241854;
  
  public static final int ERROR_GRAPHICS_I2C_ERROR_RECEIVING_DATA = -1071241853;
  
  public static final int ERROR_GRAPHICS_DDCCI_VCP_NOT_SUPPORTED = -1071241852;
  
  public static final int ERROR_GRAPHICS_DDCCI_INVALID_DATA = -1071241851;
  
  public static final int ERROR_GRAPHICS_DDCCI_MONITOR_RETURNED_INVALID_TIMING_STATUS_BYTE = -1071241850;
  
  public static final int ERROR_GRAPHICS_MCA_INVALID_CAPABILITIES_STRING = -1071241849;
  
  public static final int ERROR_GRAPHICS_MCA_INTERNAL_ERROR = -1071241848;
  
  public static final int ERROR_GRAPHICS_DDCCI_INVALID_MESSAGE_COMMAND = -1071241847;
  
  public static final int ERROR_GRAPHICS_DDCCI_INVALID_MESSAGE_LENGTH = -1071241846;
  
  public static final int ERROR_GRAPHICS_DDCCI_INVALID_MESSAGE_CHECKSUM = -1071241845;
  
  public static final int ERROR_GRAPHICS_INVALID_PHYSICAL_MONITOR_HANDLE = -1071241844;
  
  public static final int ERROR_GRAPHICS_MONITOR_NO_LONGER_EXISTS = -1071241843;
  
  public static final int ERROR_GRAPHICS_DDCCI_CURRENT_CURRENT_VALUE_GREATER_THAN_MAXIMUM_VALUE = -1071241768;
  
  public static final int ERROR_GRAPHICS_MCA_INVALID_VCP_VERSION = -1071241767;
  
  public static final int ERROR_GRAPHICS_MCA_MONITOR_VIOLATES_MCCS_SPECIFICATION = -1071241766;
  
  public static final int ERROR_GRAPHICS_MCA_MCCS_VERSION_MISMATCH = -1071241765;
  
  public static final int ERROR_GRAPHICS_MCA_UNSUPPORTED_MCCS_VERSION = -1071241764;
  
  public static final int ERROR_GRAPHICS_MCA_INVALID_TECHNOLOGY_TYPE_RETURNED = -1071241762;
  
  public static final int ERROR_GRAPHICS_MCA_UNSUPPORTED_COLOR_TEMPERATURE = -1071241761;
  
  public static final int ERROR_GRAPHICS_ONLY_CONSOLE_SESSION_SUPPORTED = -1071241760;
  
  public static final int ERROR_GRAPHICS_NO_DISPLAY_DEVICE_CORRESPONDS_TO_NAME = -1071241759;
  
  public static final int ERROR_GRAPHICS_DISPLAY_DEVICE_NOT_ATTACHED_TO_DESKTOP = -1071241758;
  
  public static final int ERROR_GRAPHICS_MIRRORING_DEVICES_NOT_SUPPORTED = -1071241757;
  
  public static final int ERROR_GRAPHICS_INVALID_POINTER = -1071241756;
  
  public static final int ERROR_GRAPHICS_NO_MONITORS_CORRESPOND_TO_DISPLAY_DEVICE = -1071241755;
  
  public static final int ERROR_GRAPHICS_PARAMETER_ARRAY_TOO_SMALL = -1071241754;
  
  public static final int ERROR_GRAPHICS_INTERNAL_ERROR = -1071241753;
  
  public static final int ERROR_GRAPHICS_SESSION_TYPE_CHANGE_IN_PROGRESS = -1071249944;
  
  public static final int TPM_E_ERROR_MASK = -2144862208;
  
  public static final int TPM_E_AUTHFAIL = -2144862207;
  
  public static final int TPM_E_BADINDEX = -2144862206;
  
  public static final int TPM_E_BAD_PARAMETER = -2144862205;
  
  public static final int TPM_E_AUDITFAILURE = -2144862204;
  
  public static final int TPM_E_CLEAR_DISABLED = -2144862203;
  
  public static final int TPM_E_DEACTIVATED = -2144862202;
  
  public static final int TPM_E_DISABLED = -2144862201;
  
  public static final int TPM_E_DISABLED_CMD = -2144862200;
  
  public static final int TPM_E_FAIL = -2144862199;
  
  public static final int TPM_E_BAD_ORDINAL = -2144862198;
  
  public static final int TPM_E_INSTALL_DISABLED = -2144862197;
  
  public static final int TPM_E_INVALID_KEYHANDLE = -2144862196;
  
  public static final int TPM_E_KEYNOTFOUND = -2144862195;
  
  public static final int TPM_E_INAPPROPRIATE_ENC = -2144862194;
  
  public static final int TPM_E_MIGRATEFAIL = -2144862193;
  
  public static final int TPM_E_INVALID_PCR_INFO = -2144862192;
  
  public static final int TPM_E_NOSPACE = -2144862191;
  
  public static final int TPM_E_NOSRK = -2144862190;
  
  public static final int TPM_E_NOTSEALED_BLOB = -2144862189;
  
  public static final int TPM_E_OWNER_SET = -2144862188;
  
  public static final int TPM_E_RESOURCES = -2144862187;
  
  public static final int TPM_E_SHORTRANDOM = -2144862186;
  
  public static final int TPM_E_SIZE = -2144862185;
  
  public static final int TPM_E_WRONGPCRVAL = -2144862184;
  
  public static final int TPM_E_BAD_PARAM_SIZE = -2144862183;
  
  public static final int TPM_E_SHA_THREAD = -2144862182;
  
  public static final int TPM_E_SHA_ERROR = -2144862181;
  
  public static final int TPM_E_FAILEDSELFTEST = -2144862180;
  
  public static final int TPM_E_AUTH2FAIL = -2144862179;
  
  public static final int TPM_E_BADTAG = -2144862178;
  
  public static final int TPM_E_IOERROR = -2144862177;
  
  public static final int TPM_E_ENCRYPT_ERROR = -2144862176;
  
  public static final int TPM_E_DECRYPT_ERROR = -2144862175;
  
  public static final int TPM_E_INVALID_AUTHHANDLE = -2144862174;
  
  public static final int TPM_E_NO_ENDORSEMENT = -2144862173;
  
  public static final int TPM_E_INVALID_KEYUSAGE = -2144862172;
  
  public static final int TPM_E_WRONG_ENTITYTYPE = -2144862171;
  
  public static final int TPM_E_INVALID_POSTINIT = -2144862170;
  
  public static final int TPM_E_INAPPROPRIATE_SIG = -2144862169;
  
  public static final int TPM_E_BAD_KEY_PROPERTY = -2144862168;
  
  public static final int TPM_E_BAD_MIGRATION = -2144862167;
  
  public static final int TPM_E_BAD_SCHEME = -2144862166;
  
  public static final int TPM_E_BAD_DATASIZE = -2144862165;
  
  public static final int TPM_E_BAD_MODE = -2144862164;
  
  public static final int TPM_E_BAD_PRESENCE = -2144862163;
  
  public static final int TPM_E_BAD_VERSION = -2144862162;
  
  public static final int TPM_E_NO_WRAP_TRANSPORT = -2144862161;
  
  public static final int TPM_E_AUDITFAIL_UNSUCCESSFUL = -2144862160;
  
  public static final int TPM_E_AUDITFAIL_SUCCESSFUL = -2144862159;
  
  public static final int TPM_E_NOTRESETABLE = -2144862158;
  
  public static final int TPM_E_NOTLOCAL = -2144862157;
  
  public static final int TPM_E_BAD_TYPE = -2144862156;
  
  public static final int TPM_E_INVALID_RESOURCE = -2144862155;
  
  public static final int TPM_E_NOTFIPS = -2144862154;
  
  public static final int TPM_E_INVALID_FAMILY = -2144862153;
  
  public static final int TPM_E_NO_NV_PERMISSION = -2144862152;
  
  public static final int TPM_E_REQUIRES_SIGN = -2144862151;
  
  public static final int TPM_E_KEY_NOTSUPPORTED = -2144862150;
  
  public static final int TPM_E_AUTH_CONFLICT = -2144862149;
  
  public static final int TPM_E_AREA_LOCKED = -2144862148;
  
  public static final int TPM_E_BAD_LOCALITY = -2144862147;
  
  public static final int TPM_E_READ_ONLY = -2144862146;
  
  public static final int TPM_E_PER_NOWRITE = -2144862145;
  
  public static final int TPM_E_FAMILYCOUNT = -2144862144;
  
  public static final int TPM_E_WRITE_LOCKED = -2144862143;
  
  public static final int TPM_E_BAD_ATTRIBUTES = -2144862142;
  
  public static final int TPM_E_INVALID_STRUCTURE = -2144862141;
  
  public static final int TPM_E_KEY_OWNER_CONTROL = -2144862140;
  
  public static final int TPM_E_BAD_COUNTER = -2144862139;
  
  public static final int TPM_E_NOT_FULLWRITE = -2144862138;
  
  public static final int TPM_E_CONTEXT_GAP = -2144862137;
  
  public static final int TPM_E_MAXNVWRITES = -2144862136;
  
  public static final int TPM_E_NOOPERATOR = -2144862135;
  
  public static final int TPM_E_RESOURCEMISSING = -2144862134;
  
  public static final int TPM_E_DELEGATE_LOCK = -2144862133;
  
  public static final int TPM_E_DELEGATE_FAMILY = -2144862132;
  
  public static final int TPM_E_DELEGATE_ADMIN = -2144862131;
  
  public static final int TPM_E_TRANSPORT_NOTEXCLUSIVE = -2144862130;
  
  public static final int TPM_E_OWNER_CONTROL = -2144862129;
  
  public static final int TPM_E_DAA_RESOURCES = -2144862128;
  
  public static final int TPM_E_DAA_INPUT_DATA0 = -2144862127;
  
  public static final int TPM_E_DAA_INPUT_DATA1 = -2144862126;
  
  public static final int TPM_E_DAA_ISSUER_SETTINGS = -2144862125;
  
  public static final int TPM_E_DAA_TPM_SETTINGS = -2144862124;
  
  public static final int TPM_E_DAA_STAGE = -2144862123;
  
  public static final int TPM_E_DAA_ISSUER_VALIDITY = -2144862122;
  
  public static final int TPM_E_DAA_WRONG_W = -2144862121;
  
  public static final int TPM_E_BAD_HANDLE = -2144862120;
  
  public static final int TPM_E_BAD_DELEGATE = -2144862119;
  
  public static final int TPM_E_BADCONTEXT = -2144862118;
  
  public static final int TPM_E_TOOMANYCONTEXTS = -2144862117;
  
  public static final int TPM_E_MA_TICKET_SIGNATURE = -2144862116;
  
  public static final int TPM_E_MA_DESTINATION = -2144862115;
  
  public static final int TPM_E_MA_SOURCE = -2144862114;
  
  public static final int TPM_E_MA_AUTHORITY = -2144862113;
  
  public static final int TPM_E_PERMANENTEK = -2144862111;
  
  public static final int TPM_E_BAD_SIGNATURE = -2144862110;
  
  public static final int TPM_E_NOCONTEXTSPACE = -2144862109;
  
  public static final int TPM_E_COMMAND_BLOCKED = -2144861184;
  
  public static final int TPM_E_INVALID_HANDLE = -2144861183;
  
  public static final int TPM_E_DUPLICATE_VHANDLE = -2144861182;
  
  public static final int TPM_E_EMBEDDED_COMMAND_BLOCKED = -2144861181;
  
  public static final int TPM_E_EMBEDDED_COMMAND_UNSUPPORTED = -2144861180;
  
  public static final int TPM_E_RETRY = -2144860160;
  
  public static final int TPM_E_NEEDS_SELFTEST = -2144860159;
  
  public static final int TPM_E_DOING_SELFTEST = -2144860158;
  
  public static final int TPM_E_DEFEND_LOCK_RUNNING = -2144860157;
  
  public static final int TBS_E_INTERNAL_ERROR = -2144845823;
  
  public static final int TBS_E_BAD_PARAMETER = -2144845822;
  
  public static final int TBS_E_INVALID_OUTPUT_POINTER = -2144845821;
  
  public static final int TBS_E_INVALID_CONTEXT = -2144845820;
  
  public static final int TBS_E_INSUFFICIENT_BUFFER = -2144845819;
  
  public static final int TBS_E_IOERROR = -2144845818;
  
  public static final int TBS_E_INVALID_CONTEXT_PARAM = -2144845817;
  
  public static final int TBS_E_SERVICE_NOT_RUNNING = -2144845816;
  
  public static final int TBS_E_TOO_MANY_TBS_CONTEXTS = -2144845815;
  
  public static final int TBS_E_TOO_MANY_RESOURCES = -2144845814;
  
  public static final int TBS_E_SERVICE_START_PENDING = -2144845813;
  
  public static final int TBS_E_PPI_NOT_SUPPORTED = -2144845812;
  
  public static final int TBS_E_COMMAND_CANCELED = -2144845811;
  
  public static final int TBS_E_BUFFER_TOO_LARGE = -2144845810;
  
  public static final int TBS_E_TPM_NOT_FOUND = -2144845809;
  
  public static final int TBS_E_SERVICE_DISABLED = -2144845808;
  
  public static final int TPMAPI_E_INVALID_STATE = -2144796416;
  
  public static final int TPMAPI_E_NOT_ENOUGH_DATA = -2144796415;
  
  public static final int TPMAPI_E_TOO_MUCH_DATA = -2144796414;
  
  public static final int TPMAPI_E_INVALID_OUTPUT_POINTER = -2144796413;
  
  public static final int TPMAPI_E_INVALID_PARAMETER = -2144796412;
  
  public static final int TPMAPI_E_OUT_OF_MEMORY = -2144796411;
  
  public static final int TPMAPI_E_BUFFER_TOO_SMALL = -2144796410;
  
  public static final int TPMAPI_E_INTERNAL_ERROR = -2144796409;
  
  public static final int TPMAPI_E_ACCESS_DENIED = -2144796408;
  
  public static final int TPMAPI_E_AUTHORIZATION_FAILED = -2144796407;
  
  public static final int TPMAPI_E_INVALID_CONTEXT_HANDLE = -2144796406;
  
  public static final int TPMAPI_E_TBS_COMMUNICATION_ERROR = -2144796405;
  
  public static final int TPMAPI_E_TPM_COMMAND_ERROR = -2144796404;
  
  public static final int TPMAPI_E_MESSAGE_TOO_LARGE = -2144796403;
  
  public static final int TPMAPI_E_INVALID_ENCODING = -2144796402;
  
  public static final int TPMAPI_E_INVALID_KEY_SIZE = -2144796401;
  
  public static final int TPMAPI_E_ENCRYPTION_FAILED = -2144796400;
  
  public static final int TPMAPI_E_INVALID_KEY_PARAMS = -2144796399;
  
  public static final int TPMAPI_E_INVALID_MIGRATION_AUTHORIZATION_BLOB = -2144796398;
  
  public static final int TPMAPI_E_INVALID_PCR_INDEX = -2144796397;
  
  public static final int TPMAPI_E_INVALID_DELEGATE_BLOB = -2144796396;
  
  public static final int TPMAPI_E_INVALID_CONTEXT_PARAMS = -2144796395;
  
  public static final int TPMAPI_E_INVALID_KEY_BLOB = -2144796394;
  
  public static final int TPMAPI_E_INVALID_PCR_DATA = -2144796393;
  
  public static final int TPMAPI_E_INVALID_OWNER_AUTH = -2144796392;
  
  public static final int TPMAPI_E_FIPS_RNG_CHECK_FAILED = -2144796391;
  
  public static final int TBSIMP_E_BUFFER_TOO_SMALL = -2144796160;
  
  public static final int TBSIMP_E_CLEANUP_FAILED = -2144796159;
  
  public static final int TBSIMP_E_INVALID_CONTEXT_HANDLE = -2144796158;
  
  public static final int TBSIMP_E_INVALID_CONTEXT_PARAM = -2144796157;
  
  public static final int TBSIMP_E_TPM_ERROR = -2144796156;
  
  public static final int TBSIMP_E_HASH_BAD_KEY = -2144796155;
  
  public static final int TBSIMP_E_DUPLICATE_VHANDLE = -2144796154;
  
  public static final int TBSIMP_E_INVALID_OUTPUT_POINTER = -2144796153;
  
  public static final int TBSIMP_E_INVALID_PARAMETER = -2144796152;
  
  public static final int TBSIMP_E_RPC_INIT_FAILED = -2144796151;
  
  public static final int TBSIMP_E_SCHEDULER_NOT_RUNNING = -2144796150;
  
  public static final int TBSIMP_E_COMMAND_CANCELED = -2144796149;
  
  public static final int TBSIMP_E_OUT_OF_MEMORY = -2144796148;
  
  public static final int TBSIMP_E_LIST_NO_MORE_ITEMS = -2144796147;
  
  public static final int TBSIMP_E_LIST_NOT_FOUND = -2144796146;
  
  public static final int TBSIMP_E_NOT_ENOUGH_SPACE = -2144796145;
  
  public static final int TBSIMP_E_NOT_ENOUGH_TPM_CONTEXTS = -2144796144;
  
  public static final int TBSIMP_E_COMMAND_FAILED = -2144796143;
  
  public static final int TBSIMP_E_UNKNOWN_ORDINAL = -2144796142;
  
  public static final int TBSIMP_E_RESOURCE_EXPIRED = -2144796141;
  
  public static final int TBSIMP_E_INVALID_RESOURCE = -2144796140;
  
  public static final int TBSIMP_E_NOTHING_TO_UNLOAD = -2144796139;
  
  public static final int TBSIMP_E_HASH_TABLE_FULL = -2144796138;
  
  public static final int TBSIMP_E_TOO_MANY_TBS_CONTEXTS = -2144796137;
  
  public static final int TBSIMP_E_TOO_MANY_RESOURCES = -2144796136;
  
  public static final int TBSIMP_E_PPI_NOT_SUPPORTED = -2144796135;
  
  public static final int TBSIMP_E_TPM_INCOMPATIBLE = -2144796134;
  
  public static final int TPM_E_PPI_ACPI_FAILURE = -2144795904;
  
  public static final int TPM_E_PPI_USER_ABORT = -2144795903;
  
  public static final int TPM_E_PPI_BIOS_FAILURE = -2144795902;
  
  public static final int TPM_E_PPI_NOT_SUPPORTED = -2144795901;
  
  public static final int PLA_E_DCS_NOT_FOUND = -2144337918;
  
  public static final int PLA_E_DCS_IN_USE = -2144337750;
  
  public static final int PLA_E_TOO_MANY_FOLDERS = -2144337851;
  
  public static final int PLA_E_NO_MIN_DISK = -2144337808;
  
  public static final int PLA_E_DCS_ALREADY_EXISTS = -2144337737;
  
  public static final int PLA_S_PROPERTY_IGNORED = 3145984;
  
  public static final int PLA_E_PROPERTY_CONFLICT = -2144337663;
  
  public static final int PLA_E_DCS_SINGLETON_REQUIRED = -2144337662;
  
  public static final int PLA_E_CREDENTIALS_REQUIRED = -2144337661;
  
  public static final int PLA_E_DCS_NOT_RUNNING = -2144337660;
  
  public static final int PLA_E_CONFLICT_INCL_EXCL_API = -2144337659;
  
  public static final int PLA_E_NETWORK_EXE_NOT_VALID = -2144337658;
  
  public static final int PLA_E_EXE_ALREADY_CONFIGURED = -2144337657;
  
  public static final int PLA_E_EXE_PATH_NOT_VALID = -2144337656;
  
  public static final int PLA_E_DC_ALREADY_EXISTS = -2144337655;
  
  public static final int PLA_E_DCS_START_WAIT_TIMEOUT = -2144337654;
  
  public static final int PLA_E_DC_START_WAIT_TIMEOUT = -2144337653;
  
  public static final int PLA_E_REPORT_WAIT_TIMEOUT = -2144337652;
  
  public static final int PLA_E_NO_DUPLICATES = -2144337651;
  
  public static final int PLA_E_EXE_FULL_PATH_REQUIRED = -2144337650;
  
  public static final int PLA_E_INVALID_SESSION_NAME = -2144337649;
  
  public static final int PLA_E_PLA_CHANNEL_NOT_ENABLED = -2144337648;
  
  public static final int PLA_E_TASKSCHED_CHANNEL_NOT_ENABLED = -2144337647;
  
  public static final int PLA_E_RULES_MANAGER_FAILED = -2144337646;
  
  public static final int PLA_E_CABAPI_FAILURE = -2144337645;
  
  public static final int FVE_E_LOCKED_VOLUME = -2144272384;
  
  public static final int FVE_E_NOT_ENCRYPTED = -2144272383;
  
  public static final int FVE_E_NO_TPM_BIOS = -2144272382;
  
  public static final int FVE_E_NO_MBR_METRIC = -2144272381;
  
  public static final int FVE_E_NO_BOOTSECTOR_METRIC = -2144272380;
  
  public static final int FVE_E_NO_BOOTMGR_METRIC = -2144272379;
  
  public static final int FVE_E_WRONG_BOOTMGR = -2144272378;
  
  public static final int FVE_E_SECURE_KEY_REQUIRED = -2144272377;
  
  public static final int FVE_E_NOT_ACTIVATED = -2144272376;
  
  public static final int FVE_E_ACTION_NOT_ALLOWED = -2144272375;
  
  public static final int FVE_E_AD_SCHEMA_NOT_INSTALLED = -2144272374;
  
  public static final int FVE_E_AD_INVALID_DATATYPE = -2144272373;
  
  public static final int FVE_E_AD_INVALID_DATASIZE = -2144272372;
  
  public static final int FVE_E_AD_NO_VALUES = -2144272371;
  
  public static final int FVE_E_AD_ATTR_NOT_SET = -2144272370;
  
  public static final int FVE_E_AD_GUID_NOT_FOUND = -2144272369;
  
  public static final int FVE_E_BAD_INFORMATION = -2144272368;
  
  public static final int FVE_E_TOO_SMALL = -2144272367;
  
  public static final int FVE_E_SYSTEM_VOLUME = -2144272366;
  
  public static final int FVE_E_FAILED_WRONG_FS = -2144272365;
  
  public static final int FVE_E_FAILED_BAD_FS = -2144272364;
  
  public static final int FVE_E_NOT_SUPPORTED = -2144272363;
  
  public static final int FVE_E_BAD_DATA = -2144272362;
  
  public static final int FVE_E_VOLUME_NOT_BOUND = -2144272361;
  
  public static final int FVE_E_TPM_NOT_OWNED = -2144272360;
  
  public static final int FVE_E_NOT_DATA_VOLUME = -2144272359;
  
  public static final int FVE_E_AD_INSUFFICIENT_BUFFER = -2144272358;
  
  public static final int FVE_E_CONV_READ = -2144272357;
  
  public static final int FVE_E_CONV_WRITE = -2144272356;
  
  public static final int FVE_E_KEY_REQUIRED = -2144272355;
  
  public static final int FVE_E_CLUSTERING_NOT_SUPPORTED = -2144272354;
  
  public static final int FVE_E_VOLUME_BOUND_ALREADY = -2144272353;
  
  public static final int FVE_E_OS_NOT_PROTECTED = -2144272352;
  
  public static final int FVE_E_PROTECTION_DISABLED = -2144272351;
  
  public static final int FVE_E_RECOVERY_KEY_REQUIRED = -2144272350;
  
  public static final int FVE_E_FOREIGN_VOLUME = -2144272349;
  
  public static final int FVE_E_OVERLAPPED_UPDATE = -2144272348;
  
  public static final int FVE_E_TPM_SRK_AUTH_NOT_ZERO = -2144272347;
  
  public static final int FVE_E_FAILED_SECTOR_SIZE = -2144272346;
  
  public static final int FVE_E_FAILED_AUTHENTICATION = -2144272345;
  
  public static final int FVE_E_NOT_OS_VOLUME = -2144272344;
  
  public static final int FVE_E_AUTOUNLOCK_ENABLED = -2144272343;
  
  public static final int FVE_E_WRONG_BOOTSECTOR = -2144272342;
  
  public static final int FVE_E_WRONG_SYSTEM_FS = -2144272341;
  
  public static final int FVE_E_POLICY_PASSWORD_REQUIRED = -2144272340;
  
  public static final int FVE_E_CANNOT_SET_FVEK_ENCRYPTED = -2144272339;
  
  public static final int FVE_E_CANNOT_ENCRYPT_NO_KEY = -2144272338;
  
  public static final int FVE_E_BOOTABLE_CDDVD = -2144272336;
  
  public static final int FVE_E_PROTECTOR_EXISTS = -2144272335;
  
  public static final int FVE_E_RELATIVE_PATH = -2144272334;
  
  public static final int FVE_E_PROTECTOR_NOT_FOUND = -2144272333;
  
  public static final int FVE_E_INVALID_KEY_FORMAT = -2144272332;
  
  public static final int FVE_E_INVALID_PASSWORD_FORMAT = -2144272331;
  
  public static final int FVE_E_FIPS_RNG_CHECK_FAILED = -2144272330;
  
  public static final int FVE_E_FIPS_PREVENTS_RECOVERY_PASSWORD = -2144272329;
  
  public static final int FVE_E_FIPS_PREVENTS_EXTERNAL_KEY_EXPORT = -2144272328;
  
  public static final int FVE_E_NOT_DECRYPTED = -2144272327;
  
  public static final int FVE_E_INVALID_PROTECTOR_TYPE = -2144272326;
  
  public static final int FVE_E_NO_PROTECTORS_TO_TEST = -2144272325;
  
  public static final int FVE_E_KEYFILE_NOT_FOUND = -2144272324;
  
  public static final int FVE_E_KEYFILE_INVALID = -2144272323;
  
  public static final int FVE_E_KEYFILE_NO_VMK = -2144272322;
  
  public static final int FVE_E_TPM_DISABLED = -2144272321;
  
  public static final int FVE_E_NOT_ALLOWED_IN_SAFE_MODE = -2144272320;
  
  public static final int FVE_E_TPM_INVALID_PCR = -2144272319;
  
  public static final int FVE_E_TPM_NO_VMK = -2144272318;
  
  public static final int FVE_E_PIN_INVALID = -2144272317;
  
  public static final int FVE_E_AUTH_INVALID_APPLICATION = -2144272316;
  
  public static final int FVE_E_AUTH_INVALID_CONFIG = -2144272315;
  
  public static final int FVE_E_FIPS_DISABLE_PROTECTION_NOT_ALLOWED = -2144272314;
  
  public static final int FVE_E_FS_NOT_EXTENDED = -2144272313;
  
  public static final int FVE_E_FIRMWARE_TYPE_NOT_SUPPORTED = -2144272312;
  
  public static final int FVE_E_NO_LICENSE = -2144272311;
  
  public static final int FVE_E_NOT_ON_STACK = -2144272310;
  
  public static final int FVE_E_FS_MOUNTED = -2144272309;
  
  public static final int FVE_E_TOKEN_NOT_IMPERSONATED = -2144272308;
  
  public static final int FVE_E_DRY_RUN_FAILED = -2144272307;
  
  public static final int FVE_E_REBOOT_REQUIRED = -2144272306;
  
  public static final int FVE_E_DEBUGGER_ENABLED = -2144272305;
  
  public static final int FVE_E_RAW_ACCESS = -2144272304;
  
  public static final int FVE_E_RAW_BLOCKED = -2144272303;
  
  public static final int FVE_E_BCD_APPLICATIONS_PATH_INCORRECT = -2144272302;
  
  public static final int FVE_E_NOT_ALLOWED_IN_VERSION = -2144272301;
  
  public static final int FWP_E_CALLOUT_NOT_FOUND = -2144206847;
  
  public static final int FWP_E_CONDITION_NOT_FOUND = -2144206846;
  
  public static final int FWP_E_FILTER_NOT_FOUND = -2144206845;
  
  public static final int FWP_E_LAYER_NOT_FOUND = -2144206844;
  
  public static final int FWP_E_PROVIDER_NOT_FOUND = -2144206843;
  
  public static final int FWP_E_PROVIDER_CONTEXT_NOT_FOUND = -2144206842;
  
  public static final int FWP_E_SUBLAYER_NOT_FOUND = -2144206841;
  
  public static final int FWP_E_NOT_FOUND = -2144206840;
  
  public static final int FWP_E_ALREADY_EXISTS = -2144206839;
  
  public static final int FWP_E_IN_USE = -2144206838;
  
  public static final int FWP_E_DYNAMIC_SESSION_IN_PROGRESS = -2144206837;
  
  public static final int FWP_E_WRONG_SESSION = -2144206836;
  
  public static final int FWP_E_NO_TXN_IN_PROGRESS = -2144206835;
  
  public static final int FWP_E_TXN_IN_PROGRESS = -2144206834;
  
  public static final int FWP_E_TXN_ABORTED = -2144206833;
  
  public static final int FWP_E_SESSION_ABORTED = -2144206832;
  
  public static final int FWP_E_INCOMPATIBLE_TXN = -2144206831;
  
  public static final int FWP_E_TIMEOUT = -2144206830;
  
  public static final int FWP_E_NET_EVENTS_DISABLED = -2144206829;
  
  public static final int FWP_E_INCOMPATIBLE_LAYER = -2144206828;
  
  public static final int FWP_E_KM_CLIENTS_ONLY = -2144206827;
  
  public static final int FWP_E_LIFETIME_MISMATCH = -2144206826;
  
  public static final int FWP_E_BUILTIN_OBJECT = -2144206825;
  
  public static final int FWP_E_TOO_MANY_CALLOUTS = -2144206824;
  
  public static final int FWP_E_NOTIFICATION_DROPPED = -2144206823;
  
  public static final int FWP_E_TRAFFIC_MISMATCH = -2144206822;
  
  public static final int FWP_E_INCOMPATIBLE_SA_STATE = -2144206821;
  
  public static final int FWP_E_NULL_POINTER = -2144206820;
  
  public static final int FWP_E_INVALID_ENUMERATOR = -2144206819;
  
  public static final int FWP_E_INVALID_FLAGS = -2144206818;
  
  public static final int FWP_E_INVALID_NET_MASK = -2144206817;
  
  public static final int FWP_E_INVALID_RANGE = -2144206816;
  
  public static final int FWP_E_INVALID_INTERVAL = -2144206815;
  
  public static final int FWP_E_ZERO_LENGTH_ARRAY = -2144206814;
  
  public static final int FWP_E_NULL_DISPLAY_NAME = -2144206813;
  
  public static final int FWP_E_INVALID_ACTION_TYPE = -2144206812;
  
  public static final int FWP_E_INVALID_WEIGHT = -2144206811;
  
  public static final int FWP_E_MATCH_TYPE_MISMATCH = -2144206810;
  
  public static final int FWP_E_TYPE_MISMATCH = -2144206809;
  
  public static final int FWP_E_OUT_OF_BOUNDS = -2144206808;
  
  public static final int FWP_E_RESERVED = -2144206807;
  
  public static final int FWP_E_DUPLICATE_CONDITION = -2144206806;
  
  public static final int FWP_E_DUPLICATE_KEYMOD = -2144206805;
  
  public static final int FWP_E_ACTION_INCOMPATIBLE_WITH_LAYER = -2144206804;
  
  public static final int FWP_E_ACTION_INCOMPATIBLE_WITH_SUBLAYER = -2144206803;
  
  public static final int FWP_E_CONTEXT_INCOMPATIBLE_WITH_LAYER = -2144206802;
  
  public static final int FWP_E_CONTEXT_INCOMPATIBLE_WITH_CALLOUT = -2144206801;
  
  public static final int FWP_E_INCOMPATIBLE_AUTH_METHOD = -2144206800;
  
  public static final int FWP_E_INCOMPATIBLE_DH_GROUP = -2144206799;
  
  public static final int FWP_E_EM_NOT_SUPPORTED = -2144206798;
  
  public static final int FWP_E_NEVER_MATCH = -2144206797;
  
  public static final int FWP_E_PROVIDER_CONTEXT_MISMATCH = -2144206796;
  
  public static final int FWP_E_INVALID_PARAMETER = -2144206795;
  
  public static final int FWP_E_TOO_MANY_SUBLAYERS = -2144206794;
  
  public static final int FWP_E_CALLOUT_NOTIFICATION_FAILED = -2144206793;
  
  public static final int FWP_E_INVALID_AUTH_TRANSFORM = -2144206792;
  
  public static final int FWP_E_INVALID_CIPHER_TRANSFORM = -2144206791;
  
  public static final int ERROR_NDIS_INTERFACE_CLOSING = -2144075774;
  
  public static final int ERROR_NDIS_BAD_VERSION = -2144075772;
  
  public static final int ERROR_NDIS_BAD_CHARACTERISTICS = -2144075771;
  
  public static final int ERROR_NDIS_ADAPTER_NOT_FOUND = -2144075770;
  
  public static final int ERROR_NDIS_OPEN_FAILED = -2144075769;
  
  public static final int ERROR_NDIS_DEVICE_FAILED = -2144075768;
  
  public static final int ERROR_NDIS_MULTICAST_FULL = -2144075767;
  
  public static final int ERROR_NDIS_MULTICAST_EXISTS = -2144075766;
  
  public static final int ERROR_NDIS_MULTICAST_NOT_FOUND = -2144075765;
  
  public static final int ERROR_NDIS_REQUEST_ABORTED = -2144075764;
  
  public static final int ERROR_NDIS_RESET_IN_PROGRESS = -2144075763;
  
  public static final int ERROR_NDIS_NOT_SUPPORTED = -2144075589;
  
  public static final int ERROR_NDIS_INVALID_PACKET = -2144075761;
  
  public static final int ERROR_NDIS_ADAPTER_NOT_READY = -2144075759;
  
  public static final int ERROR_NDIS_INVALID_LENGTH = -2144075756;
  
  public static final int ERROR_NDIS_INVALID_DATA = -2144075755;
  
  public static final int ERROR_NDIS_BUFFER_TOO_SHORT = -2144075754;
  
  public static final int ERROR_NDIS_INVALID_OID = -2144075753;
  
  public static final int ERROR_NDIS_ADAPTER_REMOVED = -2144075752;
  
  public static final int ERROR_NDIS_UNSUPPORTED_MEDIA = -2144075751;
  
  public static final int ERROR_NDIS_GROUP_ADDRESS_IN_USE = -2144075750;
  
  public static final int ERROR_NDIS_FILE_NOT_FOUND = -2144075749;
  
  public static final int ERROR_NDIS_ERROR_READING_FILE = -2144075748;
  
  public static final int ERROR_NDIS_ALREADY_MAPPED = -2144075747;
  
  public static final int ERROR_NDIS_RESOURCE_CONFLICT = -2144075746;
  
  public static final int ERROR_NDIS_MEDIA_DISCONNECTED = -2144075745;
  
  public static final int ERROR_NDIS_INVALID_ADDRESS = -2144075742;
  
  public static final int ERROR_NDIS_INVALID_DEVICE_REQUEST = -2144075760;
  
  public static final int ERROR_NDIS_PAUSED = -2144075734;
  
  public static final int ERROR_NDIS_INTERFACE_NOT_FOUND = -2144075733;
  
  public static final int ERROR_NDIS_UNSUPPORTED_REVISION = -2144075732;
  
  public static final int ERROR_NDIS_INVALID_PORT = -2144075731;
  
  public static final int ERROR_NDIS_INVALID_PORT_STATE = -2144075730;
  
  public static final int ERROR_NDIS_LOW_POWER_STATE = -2144075729;
  
  public static final int ERROR_NDIS_DOT11_AUTO_CONFIG_ENABLED = -2144067584;
  
  public static final int ERROR_NDIS_DOT11_MEDIA_IN_USE = -2144067583;
  
  public static final int ERROR_NDIS_DOT11_POWER_STATE_INVALID = -2144067582;
  
  public static final int ERROR_NDIS_INDICATION_REQUIRED = 3407873;
}
