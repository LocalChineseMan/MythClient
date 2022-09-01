package fr.litarvan.openauth.microsoft;

import fr.litarvan.openauth.microsoft.model.request.MinecraftLoginRequest;
import fr.litarvan.openauth.microsoft.model.request.XSTSAuthorizationProperties;
import fr.litarvan.openauth.microsoft.model.request.XboxLiveLoginProperties;
import fr.litarvan.openauth.microsoft.model.request.XboxLoginRequest;
import fr.litarvan.openauth.microsoft.model.response.MicrosoftRefreshResponse;
import fr.litarvan.openauth.microsoft.model.response.MinecraftLoginResponse;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import fr.litarvan.openauth.microsoft.model.response.MinecraftStoreResponse;
import fr.litarvan.openauth.microsoft.model.response.XboxLoginResponse;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MicrosoftAuthenticator {
  public static final String MICROSOFT_AUTHORIZATION_ENDPOINT = "https://login.live.com/oauth20_authorize.srf";
  
  public static final String MICROSOFT_TOKEN_ENDPOINT = "https://login.live.com/oauth20_token.srf";
  
  public static final String MICROSOFT_REDIRECTION_ENDPOINT = "https://login.live.com/oauth20_desktop.srf";
  
  public static final String XBOX_LIVE_AUTH_HOST = "user.auth.xboxlive.com";
  
  public static final String XBOX_LIVE_CLIENT_ID = "000000004C12AE6F";
  
  public static final String XBOX_LIVE_SERVICE_SCOPE = "service::user.auth.xboxlive.com::MBI_SSL";
  
  public static final String XBOX_LIVE_AUTHORIZATION_ENDPOINT = "https://user.auth.xboxlive.com/user/authenticate";
  
  public static final String XSTS_AUTHORIZATION_ENDPOINT = "https://xsts.auth.xboxlive.com/xsts/authorize";
  
  public static final String MINECRAFT_AUTH_ENDPOINT = "https://api.minecraftservices.com/authentication/login_with_xbox";
  
  public static final String XBOX_LIVE_AUTH_RELAY = "http://auth.xboxlive.com";
  
  public static final String MINECRAFT_AUTH_RELAY = "rp://api.minecraftservices.com/";
  
  public static final String MINECRAFT_STORE_ENDPOINT = "https://api.minecraftservices.com/entitlements/mcstore";
  
  public static final String MINECRAFT_PROFILE_ENDPOINT = "https://api.minecraftservices.com/minecraft/profile";
  
  public static final String MINECRAFT_STORE_IDENTIFIER = "game_minecraft";
  
  private final HttpClient http = new HttpClient();
  
  public MicrosoftAuthResult loginWithCredentials(String email, String password) throws MicrosoftAuthenticationException {
    HttpURLConnection result;
    CookieHandler currentHandler = CookieHandler.getDefault();
    CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    Map<String, String> params = new HashMap<>();
    params.put("login", email);
    params.put("loginfmt", email);
    params.put("passwd", password);
    try {
      PreAuthData authData = preAuthRequest();
      params.put("PPFT", authData.getPPFT());
      result = this.http.followRedirects(this.http.postForm(authData.getUrlPost(), params));
    } finally {
      CookieHandler.setDefault(currentHandler);
    } 
    try {
      return loginWithTokens(extractTokens(result.getURL().toString()));
    } catch (MicrosoftAuthenticationException e) {
      if (match("identity/confirm", this.http.readResponse(result)) != null)
        throw new MicrosoftAuthenticationException("User has enabled double-authentication or must allow sign-in on https://account.live.com/activity"); 
      throw e;
    } 
  }
  
  public MicrosoftAuthResult loginWithWebview() throws MicrosoftAuthenticationException {
    try {
      return loginWithAsyncWebview().get();
    } catch (InterruptedException|java.util.concurrent.ExecutionException e) {
      throw new MicrosoftAuthenticationException(e);
    } 
  }
  
  public CompletableFuture<MicrosoftAuthResult> loginWithAsyncWebview() {
    String url = String.format("%s?%s", new Object[] { "https://login.live.com/oauth20_authorize.srf", this.http.buildParams(getLoginParams()) });
    LoginFrame frame = new LoginFrame();
    return frame.start(url).thenApplyAsync(result -> {
          try {
            return loginWithTokens(extractTokens(result));
          } catch (MicrosoftAuthenticationException e) {
            throw new CompletionException(e);
          } 
        });
  }
  
  public MicrosoftAuthResult loginWithRefreshToken(String refreshToken) throws MicrosoftAuthenticationException {
    Map<String, String> params = getLoginParams();
    params.put("refresh_token", refreshToken);
    params.put("grant_type", "refresh_token");
    MicrosoftRefreshResponse response = this.http.<MicrosoftRefreshResponse>postFormGetJson("https://login.live.com/oauth20_token.srf", params, MicrosoftRefreshResponse.class);
    return loginWithTokens(new AuthTokens(response.getAccessToken(), response.getRefreshToken()));
  }
  
  public MicrosoftAuthResult loginWithTokens(AuthTokens tokens) throws MicrosoftAuthenticationException {
    XboxLoginResponse xboxLiveResponse = xboxLiveLogin(tokens.getAccessToken());
    XboxLoginResponse xstsResponse = xstsLogin(xboxLiveResponse.getToken());
    String userHash = xstsResponse.getDisplayClaims().getUsers()[0].getUserHash();
    MinecraftLoginResponse minecraftResponse = minecraftLogin(userHash, xstsResponse.getToken());
    MinecraftStoreResponse storeResponse = this.http.<MinecraftStoreResponse>getJson("https://api.minecraftservices.com/entitlements/mcstore", minecraftResponse
        
        .getAccessToken(), MinecraftStoreResponse.class);
    if (Arrays.<MinecraftStoreResponse.StoreProduct>stream(storeResponse.getItems()).noneMatch(item -> item.getName().equals("game_minecraft")))
      throw new MicrosoftAuthenticationException("Player didn't buy Minecraft Java Edition or did not migrate its account"); 
    MinecraftProfile profile = this.http.<MinecraftProfile>getJson("https://api.minecraftservices.com/minecraft/profile", minecraftResponse
        
        .getAccessToken(), MinecraftProfile.class);
    return new MicrosoftAuthResult(profile, minecraftResponse.getAccessToken(), tokens.getRefreshToken());
  }
  
  protected PreAuthData preAuthRequest() throws MicrosoftAuthenticationException {
    Map<String, String> params = getLoginParams();
    params.put("display", "touch");
    params.put("locale", "en");
    String result = this.http.getText("https://login.live.com/oauth20_authorize.srf", params);
    String ppft = match("sFTTag:'.*value=\"([^\"]*)\"", result);
    String urlPost = match("urlPost: ?'(.+?(?='))", result);
    return new PreAuthData(ppft, urlPost);
  }
  
  protected XboxLoginResponse xboxLiveLogin(String accessToken) throws MicrosoftAuthenticationException {
    XboxLiveLoginProperties properties = new XboxLiveLoginProperties("RPS", "user.auth.xboxlive.com", accessToken);
    XboxLoginRequest<XboxLiveLoginProperties> request = new XboxLoginRequest(properties, "http://auth.xboxlive.com", "JWT");
    return this.http.<XboxLoginResponse>postJson("https://user.auth.xboxlive.com/user/authenticate", request, XboxLoginResponse.class);
  }
  
  protected XboxLoginResponse xstsLogin(String xboxLiveToken) throws MicrosoftAuthenticationException {
    XSTSAuthorizationProperties properties = new XSTSAuthorizationProperties("RETAIL", new String[] { xboxLiveToken });
    XboxLoginRequest<XSTSAuthorizationProperties> request = new XboxLoginRequest(properties, "rp://api.minecraftservices.com/", "JWT");
    return this.http.<XboxLoginResponse>postJson("https://xsts.auth.xboxlive.com/xsts/authorize", request, XboxLoginResponse.class);
  }
  
  protected MinecraftLoginResponse minecraftLogin(String userHash, String xstsToken) throws MicrosoftAuthenticationException {
    MinecraftLoginRequest request = new MinecraftLoginRequest(String.format("XBL3.0 x=%s;%s", new Object[] { userHash, xstsToken }));
    return this.http.<MinecraftLoginResponse>postJson("https://api.minecraftservices.com/authentication/login_with_xbox", request, MinecraftLoginResponse.class);
  }
  
  protected Map<String, String> getLoginParams() {
    Map<String, String> params = new HashMap<>();
    params.put("client_id", "000000004C12AE6F");
    params.put("redirect_uri", "https://login.live.com/oauth20_desktop.srf");
    params.put("scope", "service::user.auth.xboxlive.com::MBI_SSL");
    params.put("response_type", "token");
    return params;
  }
  
  protected AuthTokens extractTokens(String url) throws MicrosoftAuthenticationException {
    return new AuthTokens(extractValue(url, "access_token"), extractValue(url, "refresh_token"));
  }
  
  protected String extractValue(String url, String key) throws MicrosoftAuthenticationException {
    String matched = match(key + "=([^&]*)", url);
    if (matched == null)
      throw new MicrosoftAuthenticationException("Invalid credentials or tokens"); 
    try {
      return URLDecoder.decode(matched, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new MicrosoftAuthenticationException(e);
    } 
  }
  
  protected String match(String regex, String content) {
    Matcher matcher = Pattern.compile(regex).matcher(content);
    if (!matcher.find())
      return null; 
    return matcher.group(1);
  }
}
