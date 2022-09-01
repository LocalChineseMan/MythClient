package io.netty.util;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

public final class NetUtil {
  public static final Inet4Address LOCALHOST4;
  
  public static final Inet6Address LOCALHOST6;
  
  public static final InetAddress LOCALHOST;
  
  public static final NetworkInterface LOOPBACK_IF;
  
  public static final int SOMAXCONN;
  
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(NetUtil.class);
  
  static {
    byte[] LOCALHOST4_BYTES = { Byte.MAX_VALUE, 0, 0, 1 };
    byte[] LOCALHOST6_BYTES = { 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 1 };
    Inet4Address localhost4 = null;
    try {
      localhost4 = (Inet4Address)InetAddress.getByAddress(LOCALHOST4_BYTES);
    } catch (Exception e) {
      PlatformDependent.throwException(e);
    } 
    LOCALHOST4 = localhost4;
    Inet6Address localhost6 = null;
    try {
      localhost6 = (Inet6Address)InetAddress.getByAddress(LOCALHOST6_BYTES);
    } catch (Exception e) {
      PlatformDependent.throwException(e);
    } 
    LOCALHOST6 = localhost6;
    List<NetworkInterface> ifaces = new ArrayList<NetworkInterface>();
    try {
      for (Enumeration<NetworkInterface> i = NetworkInterface.getNetworkInterfaces(); i.hasMoreElements(); ) {
        NetworkInterface iface = i.nextElement();
        if (iface.getInetAddresses().hasMoreElements())
          ifaces.add(iface); 
      } 
    } catch (SocketException e) {
      logger.warn("Failed to retrieve the list of available network interfaces", e);
    } 
    NetworkInterface loopbackIface = null;
    InetAddress loopbackAddr = null;
    label103: for (NetworkInterface iface : ifaces) {
      for (Enumeration<InetAddress> i = iface.getInetAddresses(); i.hasMoreElements(); ) {
        InetAddress addr = i.nextElement();
        if (addr.isLoopbackAddress()) {
          loopbackIface = iface;
          loopbackAddr = addr;
          break label103;
        } 
      } 
    } 
    if (loopbackIface == null)
      try {
        for (NetworkInterface iface : ifaces) {
          if (iface.isLoopback()) {
            Enumeration<InetAddress> i = iface.getInetAddresses();
            if (i.hasMoreElements()) {
              loopbackIface = iface;
              loopbackAddr = i.nextElement();
              break;
            } 
          } 
        } 
        if (loopbackIface == null)
          logger.warn("Failed to find the loopback interface"); 
      } catch (SocketException e) {
        logger.warn("Failed to find the loopback interface", e);
      }  
    if (loopbackIface != null) {
      logger.debug("Loopback interface: {} ({}, {})", new Object[] { loopbackIface.getName(), loopbackIface.getDisplayName(), loopbackAddr.getHostAddress() });
    } else if (loopbackAddr == null) {
      try {
        if (NetworkInterface.getByInetAddress(LOCALHOST6) != null) {
          logger.debug("Using hard-coded IPv6 localhost address: {}", localhost6);
          loopbackAddr = localhost6;
        } 
      } catch (Exception e) {
      
      } finally {
        if (loopbackAddr == null) {
          logger.debug("Using hard-coded IPv4 localhost address: {}", localhost4);
          loopbackAddr = localhost4;
        } 
      } 
    } 
    LOOPBACK_IF = loopbackIface;
    LOCALHOST = loopbackAddr;
    int somaxconn = PlatformDependent.isWindows() ? 200 : 128;
    File file = new File("/proc/sys/net/core/somaxconn");
    if (file.exists()) {
      BufferedReader in = null;
      try {
        in = new BufferedReader(new FileReader(file));
        somaxconn = Integer.parseInt(in.readLine());
        if (logger.isDebugEnabled())
          logger.debug("{}: {}", file, Integer.valueOf(somaxconn)); 
      } catch (Exception e) {
        logger.debug("Failed to get SOMAXCONN from: {}", file, e);
      } finally {
        if (in != null)
          try {
            in.close();
          } catch (Exception e) {} 
      } 
    } else if (logger.isDebugEnabled()) {
      logger.debug("{}: {} (non-existent)", file, Integer.valueOf(somaxconn));
    } 
    SOMAXCONN = somaxconn;
  }
  
  public static byte[] createByteArrayFromIpAddressString(String ipAddressString) {
    if (isValidIpV4Address(ipAddressString)) {
      StringTokenizer tokenizer = new StringTokenizer(ipAddressString, ".");
      byte[] byteAddress = new byte[4];
      for (int i = 0; i < 4; i++) {
        String token = tokenizer.nextToken();
        int tempInt = Integer.parseInt(token);
        byteAddress[i] = (byte)tempInt;
      } 
      return byteAddress;
    } 
    if (isValidIpV6Address(ipAddressString)) {
      if (ipAddressString.charAt(0) == '[')
        ipAddressString = ipAddressString.substring(1, ipAddressString.length() - 1); 
      StringTokenizer tokenizer = new StringTokenizer(ipAddressString, ":.", true);
      ArrayList<String> hexStrings = new ArrayList<String>();
      ArrayList<String> decStrings = new ArrayList<String>();
      String token = "";
      String prevToken = "";
      int doubleColonIndex = -1;
      while (tokenizer.hasMoreTokens()) {
        prevToken = token;
        token = tokenizer.nextToken();
        if (":".equals(token)) {
          if (":".equals(prevToken)) {
            doubleColonIndex = hexStrings.size();
            continue;
          } 
          if (!prevToken.isEmpty())
            hexStrings.add(prevToken); 
          continue;
        } 
        if (".".equals(token))
          decStrings.add(prevToken); 
      } 
      if (":".equals(prevToken)) {
        if (":".equals(token)) {
          doubleColonIndex = hexStrings.size();
        } else {
          hexStrings.add(token);
        } 
      } else if (".".equals(prevToken)) {
        decStrings.add(token);
      } 
      int hexStringsLength = 8;
      if (!decStrings.isEmpty())
        hexStringsLength -= 2; 
      if (doubleColonIndex != -1) {
        int numberToInsert = hexStringsLength - hexStrings.size();
        for (int j = 0; j < numberToInsert; j++)
          hexStrings.add(doubleColonIndex, "0"); 
      } 
      byte[] ipByteArray = new byte[16];
      int i;
      for (i = 0; i < hexStrings.size(); i++)
        convertToBytes(hexStrings.get(i), ipByteArray, i * 2); 
      for (i = 0; i < decStrings.size(); i++)
        ipByteArray[i + 12] = (byte)(Integer.parseInt((String)decStrings.get(i)) & 0xFF); 
      return ipByteArray;
    } 
    return null;
  }
  
  private static void convertToBytes(String hexWord, byte[] ipByteArray, int byteIndex) {
    int hexWordLength = hexWord.length();
    int hexWordIndex = 0;
    ipByteArray[byteIndex] = 0;
    ipByteArray[byteIndex + 1] = 0;
    if (hexWordLength > 3) {
      int i = getIntValue(hexWord.charAt(hexWordIndex++));
      ipByteArray[byteIndex] = (byte)(ipByteArray[byteIndex] | i << 4);
    } 
    if (hexWordLength > 2) {
      int i = getIntValue(hexWord.charAt(hexWordIndex++));
      ipByteArray[byteIndex] = (byte)(ipByteArray[byteIndex] | i);
    } 
    if (hexWordLength > 1) {
      int i = getIntValue(hexWord.charAt(hexWordIndex++));
      ipByteArray[byteIndex + 1] = (byte)(ipByteArray[byteIndex + 1] | i << 4);
    } 
    int charValue = getIntValue(hexWord.charAt(hexWordIndex));
    ipByteArray[byteIndex + 1] = (byte)(ipByteArray[byteIndex + 1] | charValue & 0xF);
  }
  
  static int getIntValue(char c) {
    switch (c) {
      case '0':
        return 0;
      case '1':
        return 1;
      case '2':
        return 2;
      case '3':
        return 3;
      case '4':
        return 4;
      case '5':
        return 5;
      case '6':
        return 6;
      case '7':
        return 7;
      case '8':
        return 8;
      case '9':
        return 9;
    } 
    c = Character.toLowerCase(c);
    switch (c) {
      case 'a':
        return 10;
      case 'b':
        return 11;
      case 'c':
        return 12;
      case 'd':
        return 13;
      case 'e':
        return 14;
      case 'f':
        return 15;
    } 
    return 0;
  }
  
  public static boolean isValidIpV6Address(String ipAddress) {
    int length = ipAddress.length();
    boolean doubleColon = false;
    int numberOfColons = 0;
    int numberOfPeriods = 0;
    int numberOfPercent = 0;
    StringBuilder word = new StringBuilder();
    char c = Character.MIN_VALUE;
    int offset = 0;
    if (length < 2)
      return false; 
    for (int i = 0; i < length; i++) {
      char prevChar = c;
      c = ipAddress.charAt(i);
      switch (c) {
        case '[':
          if (i != 0)
            return false; 
          if (ipAddress.charAt(length - 1) != ']')
            return false; 
          offset = 1;
          if (length < 4)
            return false; 
          break;
        case ']':
          if (i != length - 1)
            return false; 
          if (ipAddress.charAt(0) != '[')
            return false; 
          break;
        case '.':
          numberOfPeriods++;
          if (numberOfPeriods > 3)
            return false; 
          if (!isValidIp4Word(word.toString()))
            return false; 
          if (numberOfColons != 6 && !doubleColon)
            return false; 
          if (numberOfColons == 7 && ipAddress.charAt(offset) != ':' && ipAddress.charAt(1 + offset) != ':')
            return false; 
          word.delete(0, word.length());
          break;
        case ':':
          if (i == offset && (ipAddress.length() <= i || ipAddress.charAt(i + 1) != ':'))
            return false; 
          numberOfColons++;
          if (numberOfColons > 7)
            return false; 
          if (numberOfPeriods > 0)
            return false; 
          if (prevChar == ':') {
            if (doubleColon)
              return false; 
            doubleColon = true;
          } 
          word.delete(0, word.length());
          break;
        case '%':
          if (numberOfColons == 0)
            return false; 
          numberOfPercent++;
          if (i + 1 >= length)
            return false; 
          try {
            if (Integer.parseInt(ipAddress.substring(i + 1)) < 0)
              return false; 
          } catch (NumberFormatException e) {
            return false;
          } 
          break;
        default:
          if (numberOfPercent == 0) {
            if (word != null && word.length() > 3)
              return false; 
            if (!isValidHexChar(c))
              return false; 
          } 
          word.append(c);
          break;
      } 
    } 
    if (numberOfPeriods > 0) {
      if (numberOfPeriods != 3 || !isValidIp4Word(word.toString()) || numberOfColons >= 7)
        return false; 
    } else {
      if (numberOfColons != 7 && !doubleColon)
        return false; 
      if (numberOfPercent == 0 && 
        word.length() == 0 && ipAddress.charAt(length - 1 - offset) == ':' && ipAddress.charAt(length - 2 - offset) != ':')
        return false; 
    } 
    return true;
  }
  
  public static boolean isValidIp4Word(String word) {
    if (word.length() < 1 || word.length() > 3)
      return false; 
    for (int i = 0; i < word.length(); i++) {
      char c = word.charAt(i);
      if (c < '0' || c > '9')
        return false; 
    } 
    return (Integer.parseInt(word) <= 255);
  }
  
  static boolean isValidHexChar(char c) {
    return ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f'));
  }
  
  public static boolean isValidIpV4Address(String value) {
    int periods = 0;
    int length = value.length();
    if (length > 15)
      return false; 
    StringBuilder word = new StringBuilder();
    for (int i = 0; i < length; i++) {
      char c = value.charAt(i);
      if (c == '.') {
        periods++;
        if (periods > 3)
          return false; 
        if (word.length() == 0)
          return false; 
        if (Integer.parseInt(word.toString()) > 255)
          return false; 
        word.delete(0, word.length());
      } else {
        if (!Character.isDigit(c))
          return false; 
        if (word.length() > 2)
          return false; 
        word.append(c);
      } 
    } 
    if (word.length() == 0 || Integer.parseInt(word.toString()) > 255)
      return false; 
    return (periods == 3);
  }
}
