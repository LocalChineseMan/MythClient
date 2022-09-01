package sun.net.www.http;

class KeepAliveEntry {
  HttpClient hc;
  
  long idleStartTime;
  
  KeepAliveEntry(HttpClient paramHttpClient, long paramLong) {
    this.hc = paramHttpClient;
    this.idleStartTime = paramLong;
  }
}
