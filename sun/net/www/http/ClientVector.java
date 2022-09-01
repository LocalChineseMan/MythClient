package sun.net.www.http;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Stack;

class ClientVector extends Stack<KeepAliveEntry> {
  private static final long serialVersionUID = -8680532108106489459L;
  
  int nap;
  
  ClientVector(int paramInt) {
    this.nap = paramInt;
  }
  
  synchronized HttpClient get() {
    if (empty())
      return null; 
    HttpClient httpClient = null;
    long l = System.currentTimeMillis();
    do {
      KeepAliveEntry keepAliveEntry = pop();
      if (l - keepAliveEntry.idleStartTime > this.nap) {
        keepAliveEntry.hc.closeServer();
      } else {
        httpClient = keepAliveEntry.hc;
      } 
    } while (httpClient == null && !empty());
    return httpClient;
  }
  
  synchronized void put(HttpClient paramHttpClient) {
    if (size() >= KeepAliveCache.getMaxConnections()) {
      paramHttpClient.closeServer();
    } else {
      push(new KeepAliveEntry(paramHttpClient, System.currentTimeMillis()));
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    throw new NotSerializableException();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    throw new NotSerializableException();
  }
}
