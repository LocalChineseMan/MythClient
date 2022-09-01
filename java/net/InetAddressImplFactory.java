package java.net;

class InetAddressImplFactory {
  static InetAddressImpl create() {
    return InetAddress.loadImpl(isIPv6Supported() ? "Inet6AddressImpl" : "Inet4AddressImpl");
  }
  
  static native boolean isIPv6Supported();
}
