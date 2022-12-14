package com.sun.jna.platform.win32;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.ptr.IntByReference;

public interface Wincon {
  public static final int ATTACH_PARENT_PROCESS = -1;
  
  public static final int CTRL_C_EVENT = 0;
  
  public static final int CTRL_BREAK_EVENT = 1;
  
  public static final int STD_INPUT_HANDLE = -10;
  
  public static final int STD_OUTPUT_HANDLE = -11;
  
  public static final int STD_ERROR_HANDLE = -12;
  
  public static final int CONSOLE_FULLSCREEN = 1;
  
  public static final int CONSOLE_FULLSCREEN_HARDWARE = 2;
  
  public static final int ENABLE_PROCESSED_INPUT = 1;
  
  public static final int ENABLE_LINE_INPUT = 2;
  
  public static final int ENABLE_ECHO_INPUT = 4;
  
  public static final int ENABLE_WINDOW_INPUT = 8;
  
  public static final int ENABLE_MOUSE_INPUT = 16;
  
  public static final int ENABLE_INSERT_MODE = 32;
  
  public static final int ENABLE_QUICK_EDIT_MODE = 64;
  
  public static final int ENABLE_EXTENDED_FLAGS = 128;
  
  public static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
  
  public static final int DISABLE_NEWLINE_AUTO_RETURN = 8;
  
  public static final int ENABLE_VIRTUAL_TERMINAL_INPUT = 512;
  
  public static final int ENABLE_PROCESSED_OUTPUT = 1;
  
  public static final int ENABLE_WRAP_AT_EOL_OUTPUT = 2;
  
  public static final int MAX_CONSOLE_TITLE_LENGTH = 65536;
  
  boolean AllocConsole();
  
  boolean FreeConsole();
  
  boolean AttachConsole(int paramInt);
  
  boolean FlushConsoleInputBuffer(WinNT.HANDLE paramHANDLE);
  
  boolean GenerateConsoleCtrlEvent(int paramInt1, int paramInt2);
  
  int GetConsoleCP();
  
  boolean SetConsoleCP(int paramInt);
  
  int GetConsoleOutputCP();
  
  boolean SetConsoleOutputCP(int paramInt);
  
  WinDef.HWND GetConsoleWindow();
  
  boolean GetNumberOfConsoleInputEvents(WinNT.HANDLE paramHANDLE, IntByReference paramIntByReference);
  
  boolean GetNumberOfConsoleMouseButtons(IntByReference paramIntByReference);
  
  WinNT.HANDLE GetStdHandle(int paramInt);
  
  boolean SetStdHandle(int paramInt, WinNT.HANDLE paramHANDLE);
  
  boolean GetConsoleDisplayMode(IntByReference paramIntByReference);
  
  boolean GetConsoleMode(WinNT.HANDLE paramHANDLE, IntByReference paramIntByReference);
  
  boolean SetConsoleMode(WinNT.HANDLE paramHANDLE, int paramInt);
  
  int GetConsoleTitle(char[] paramArrayOfchar, int paramInt);
  
  int GetConsoleOriginalTitle(char[] paramArrayOfchar, int paramInt);
  
  boolean SetConsoleTitle(String paramString);
  
  boolean GetConsoleScreenBufferInfo(WinNT.HANDLE paramHANDLE, CONSOLE_SCREEN_BUFFER_INFO paramCONSOLE_SCREEN_BUFFER_INFO);
  
  boolean ReadConsoleInput(WinNT.HANDLE paramHANDLE, INPUT_RECORD[] paramArrayOfINPUT_RECORD, int paramInt, IntByReference paramIntByReference);
  
  boolean WriteConsole(WinNT.HANDLE paramHANDLE, String paramString, int paramInt, IntByReference paramIntByReference, WinDef.LPVOID paramLPVOID);
  
  public static class Wincon {}
  
  public static class Wincon {}
  
  @FieldOrder({"dwSize", "dwCursorPosition", "wAttributes", "srWindow", "dwMaximumWindowSize"})
  public static class CONSOLE_SCREEN_BUFFER_INFO extends Structure {
    public Wincon.COORD dwSize;
    
    public Wincon.COORD dwCursorPosition;
    
    public short wAttributes;
    
    public Wincon.SMALL_RECT srWindow;
    
    public Wincon.COORD dwMaximumWindowSize;
    
    public String toString() {
      return String.format("CONSOLE_SCREEN_BUFFER_INFO(%s,%s,%s,%s,%s)", new Object[] { this.dwSize, this.dwCursorPosition, Short.valueOf(this.wAttributes), this.srWindow, this.dwMaximumWindowSize });
    }
  }
  
  @FieldOrder({"EventType", "Event"})
  public static class INPUT_RECORD extends Structure {
    public static final short KEY_EVENT = 1;
    
    public static final short MOUSE_EVENT = 2;
    
    public static final short WINDOW_BUFFER_SIZE_EVENT = 4;
    
    public short EventType;
    
    public Event Event;
    
    public void read() {
      super.read();
      switch (this.EventType) {
        case 1:
          this.Event.setType("KeyEvent");
          break;
        case 2:
          this.Event.setType("MouseEvent");
          break;
        case 4:
          this.Event.setType("WindowBufferSizeEvent");
          break;
      } 
      this.Event.read();
    }
    
    public String toString() {
      return String.format("INPUT_RECORD(%s)", new Object[] { Short.valueOf(this.EventType) });
    }
    
    public static class INPUT_RECORD {}
  }
  
  public static class Wincon {}
  
  public static class Wincon {}
  
  public static class Wincon {}
}
