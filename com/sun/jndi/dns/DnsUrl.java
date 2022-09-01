package com.sun.jndi.dns;

import com.sun.jndi.toolkit.url.Uri;
import com.sun.jndi.toolkit.url.UrlUtil;
import java.net.MalformedURLException;
import java.util.StringTokenizer;

public class DnsUrl extends Uri {
  private String domain;
  
  public static DnsUrl[] fromList(String paramString) throws MalformedURLException {
    DnsUrl[] arrayOfDnsUrl1 = new DnsUrl[(paramString.length() + 1) / 2];
    byte b = 0;
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, " ");
    while (stringTokenizer.hasMoreTokens())
      arrayOfDnsUrl1[b++] = new DnsUrl(stringTokenizer.nextToken()); 
    DnsUrl[] arrayOfDnsUrl2 = new DnsUrl[b];
    System.arraycopy(arrayOfDnsUrl1, 0, arrayOfDnsUrl2, 0, b);
    return arrayOfDnsUrl2;
  }
  
  public DnsUrl(String paramString) throws MalformedURLException {
    super(paramString);
    if (!this.scheme.equals("dns"))
      throw new MalformedURLException(paramString + " is not a valid DNS pseudo-URL"); 
    this
      .domain = this.path.startsWith("/") ? this.path.substring(1) : this.path;
    this
      
      .domain = this.domain.equals("") ? "." : UrlUtil.decode(this.domain);
  }
  
  public String getDomain() {
    return this.domain;
  }
}
