package test.util.http;


import java.io.IOException;
import java.io.StringWriter;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import test.util.rest_arg;
import test.util.exception.AuthenticationFailedException;
import test.util.exception.AuthorizationFailedException;
import test.util.exception.RESTException;

public class AHEHTTPConnector{

	/*public static void main(String[] arg) throws RESTException, AuthorizationFailedException, AuthenticationFailedException{
		
		AHEHTTPConnector connector = new AHEHTTPConnector("davidchang","davidchang","localhost",8080,false);
		try {
			System.out.println(connector.authenticate());
			//connector.shutdown();
			connector.listUsers();
			connector.shutdown();
			System.out.println("connection closed.");
			//System.out.println(connector.authenticate());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	
	private static String app = "app";
	private static String appinst = "appinst";
	//private static String profile = "profile";
	private static String profile = "listUsers";
	private static String cred = "cred";
	private static String resource = "resource";
	private static String property = "property";
	private static String transfer = "transfer";
	
	private static String clone = "clone";
	private static String runtime = "runtime";
	private static String session = "session";
	
	String username;
	String password;
	DefaultHttpClient client;
	ClientConnectionManager manager;
	
	String hostname;
	int port;
	boolean SSL = true;
	
	BasicHttpContext localcontext;
	private HttpHost host;
	//AuthScope scope;
	
	public AHEHTTPConnector(){
		init();
	}
	
//	public boolean static Authenticate(String username, String password, String hostname, int port, boolean SSL){
//		
//		AHEHTTPConnector connector = new AHEHTTPConnector("vphuser","vphdemo","localhost",8111,true);
//		
//		return false;
//	}
	
	public AHEHTTPConnector(String username, String password, String hostname, int port, boolean SSL){
		
		this.username = username;
		this.password = password;
		this.hostname = hostname;
		this.port = port;
		this.SSL = SSL;
		
		init();
	}
//	
//	public AHEHTTPConnector(AHEUser_State session, String hostname, int port, boolean SSL){
//		
//		this.username = session.getUsername();
//		this.password = session.getSession_token();
//		this.hostname = hostname;
//		this.port = port;
//		this.SSL = SSL;
//		
//		init();
//	}
	
	public void shutdown(){
	
	
		client.getConnectionManager().shutdown();
		
		client = null;
		manager = null;
		localcontext = null;
		//scope = null;
		host = null;
		
	}
	
	private void init(){
		//all these setups are done for client side
		manager = new ThreadSafeClientConnManager();
		client = new DefaultHttpClient(manager);
		//host = new HttpHost(hostname, port, SSL ? "https" : "http");
		//host = new HttpHost(hostname, port, "http");
		host = new HttpHost("http://localhost:8080/");
		
		List<String> authpref = new ArrayList<String>();
		authpref.add(AuthPolicy.BASIC);
		client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authpref);

		//client.getCredentialsProvider().setCredentials(scope,new UsernamePasswordCredentials(username, password));
		setBasicHTTPAuthentication(username,password);
		
		wrapClient(client);
		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(host, basicAuth);

		// Add AuthCache to the execution context
		localcontext = new BasicHttpContext();
		localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);   
		
	}
	
	public void setBasicHTTPAuthentication(String username, String password){
		AuthScope scope = new AuthScope(host.getHostName(), host.getPort());
		client.getCredentialsProvider().setCredentials(scope,new UsernamePasswordCredentials(username, password));
	}

//	
//	public String demo(){
//		
//		DefaultHttpClient httpclient = new DefaultHttpClient();
//		HttpHost host = new HttpHost("ozone.chem.ucl.ac.uk", 2071, "https");
//		
//		List<String> authpref = new ArrayList<String>();
//		authpref.add(AuthPolicy.BASIC);
//		httpclient.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authpref);
//		AuthScope scope = new AuthScope(host.getHostName(), host.getPort());
//		httpclient.getCredentialsProvider().setCredentials(scope,new UsernamePasswordCredentials("vphuser", "vphdemo"));
//
//		StringWriter writer = new StringWriter();
//		
//		
//		try {
//			
//
//			wrapClient(httpclient);
//			// Create AuthCache instance
//			AuthCache authCache = new BasicAuthCache();
//			// Generate BASIC scheme object and add it to the local auth cache
//			BasicScheme basicAuth = new BasicScheme();
//			authCache.put(host, basicAuth);
//
//			// Add AuthCache to the execution context
//			BasicHttpContext localcontext = new BasicHttpContext();
//			localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);    
//			
//			HttpGet httpget = new HttpGet("https://ozone.chem.ucl.ac.uk:2071/user/vphuser/cmd/listapp/");
//	
//			HttpResponse response;
//
//			System.out.println("executing request" + httpget.getRequestLine());
//			
//			response = httpclient.execute(httpget,localcontext);
//			
//            System.out.println(response.getStatusLine());
//			
//
//			
//			HttpEntity entity = response.getEntity();
//			
//
//			IOUtils.copy(entity.getContent(),writer);
//			
//			EntityUtils.consume(entity);
//            
//			System.out.println(writer);
//			
//
//			
//		}  catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
//			 httpclient.getConnectionManager().shutdown();
//		}
//
//		return writer.toString();
//		
//
//		
//		
//	}
	
	private String createURL(String resource, String resource_id){
		
		String scheme = SSL ? "https" : "http";
		String url= "";
		
		//url += scheme + "://"+hostname+":"+port;
		url += "http" + "://"+hostname+":"+port;
		
		if(resource.length() > 0){
			url += "/"+resource;
		}
		
		if(resource_id.length() > 0){
			url += "/"+resource_id;
		}
		
		System.out.println("****** url" + url);
		
		return url;
		
	}
	
	private String createURL(String resource, String resource_id, String sub_resource, String sub_resource_id){
		
		String scheme = SSL ? "https" : "http";
		String url= "";
		
		//url += scheme + "://"+hostname+":"+port;
		url += "http" + "://"+hostname+":"+port;
		
		if(resource.length() > 0){
			url += "/"+resource;
		}
		
		if(resource_id.length() > 0){
			url += "/"+resource_id;
		}
		
		if(sub_resource.length() > 0){
			url += "/"+sub_resource;
		}
		
		if(sub_resource_id.length() > 0){
			url += "/"+sub_resource_id;
		}
		
		return url;
	}
	
//	private void connect(){
//		
//		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "https://ozone.chem.ucl.ac.uk:2071/user/vphuser/cmd/listapp/");
//		builder.setUser("vphuser");
//		builder.setPassword("vphdemo1");
//		
//	    try {
//	      Request response = builder.sendRequest(null, new RequestCallback() {
//	        public void onError(Request request, Throwable exception) {
//	          System.out.println(exception);
//	        }
//
//
//
//			
//			public void onResponseReceived(Request request, Response response) {
//				
//				System.out.println(response);
//				
//			}
//	      });
//	    } catch (RequestException e) {
//	      // Code omitted for clarity
//	    }
//		
//	}
	
	public static HttpClient wrapClient(HttpClient base) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {

				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}


			};
			
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = base.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	
	public boolean authenticate() throws ClientProtocolException, IOException {

		//HttpGet httpget = new HttpGet(createURL( "",""));
		HttpGet httpget = new HttpGet("http://localhost:8080/");
		HttpResponse response;
		response = client.execute(httpget,localcontext);
        System.out.println("*****response AHEHTTPConnector authenticate method: " + response.toString());
		if(response.getStatusLine().getStatusCode() >= 400){
			EntityUtils.consume(response.getEntity());
			return false;
		}else{
			EntityUtils.consume(response.getEntity());
			return true;
		}
		
	}


	
	public String addUser(HashMap<String, String> arg) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		StringWriter writer = new StringWriter();
		
		String username = arg.get(rest_arg.username.toString());
		String role = arg.get(rest_arg.role.toString());
		String sec_type = arg.get(rest_arg.security_type.toString());
		String email = arg.get(rest_arg.email.toString());
		
		HttpPost post = new HttpPost(createURL(profile, ""));

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(rest_arg.username.toString(), username));
        nameValuePairs.add(new BasicNameValuePair(rest_arg.role.toString(),role));
        nameValuePairs.add(new BasicNameValuePair(rest_arg.security_type.toString(),sec_type));
        nameValuePairs.add(new BasicNameValuePair(rest_arg.email.toString(),email));
        
        if(arg.containsKey(rest_arg.pwd.toString()))
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.pwd.toString(), arg.get(rest_arg.pwd.toString())));
 
        if(arg.containsKey(rest_arg.identifier.toString()))
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.identifier.toString(), arg.get(rest_arg.identifier.toString())));
        
        if(arg.containsKey(rest_arg.acd_vo.toString()))
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.acd_vo.toString(), arg.get(rest_arg.acd_vo.toString())));        
        
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}

	
	public String editUser(String user_id, HashMap<String, String> arg) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
		StringWriter writer = new StringWriter();
		
		String username = arg.get(rest_arg.username.toString());
		String role = arg.get(rest_arg.role.toString());
		String sec_type = arg.get(rest_arg.security_type.toString());
		String email = arg.get(rest_arg.email.toString());
		
		HttpPost post = new HttpPost(createURL(profile, user_id));

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		if(arg.containsKey(rest_arg.username.toString()))
			nameValuePairs.add(new BasicNameValuePair(rest_arg.username.toString(), username));
		
		if(arg.containsKey(rest_arg.role.toString()))
			nameValuePairs.add(new BasicNameValuePair(rest_arg.role.toString(),role));
		
		if(arg.containsKey(rest_arg.security_type.toString()))
			nameValuePairs.add(new BasicNameValuePair(rest_arg.security_type.toString(),sec_type));
		
		if(arg.containsKey(rest_arg.email.toString()))
			nameValuePairs.add(new BasicNameValuePair(rest_arg.email.toString(),email));
        
        if(arg.containsKey(rest_arg.pwd.toString()))
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.pwd.toString(), arg.get(rest_arg.pwd.toString())));
 
        if(arg.containsKey(rest_arg.identifier.toString()))
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.identifier.toString(), arg.get(rest_arg.identifier.toString())));
        
        if(arg.containsKey(rest_arg.acd_vo.toString()))
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.acd_vo.toString(), arg.get(rest_arg.acd_vo.toString())));        
        
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String removeUser(String user_id) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		StringWriter writer = new StringWriter();
		
		HttpCustomDelete delete = new HttpCustomDelete(createURL(profile, user_id));
				
		HttpResponse response;
		response = client.execute(delete,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}

	
	public String listCredentials() throws ClientProtocolException, IOException, RESTException, AuthenticationFailedException, AuthorizationFailedException {
		StringWriter writer = new StringWriter();
		
		HttpGet httpget = new HttpGet(createURL(cred, ""));
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		

		
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String addCredential(HashMap<String, String> arg) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		
		StringWriter writer = new StringWriter();
		
		HttpPost post = new HttpPost(createURL(cred, ""));
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		String[] keys = arg.keySet().toArray(new String[arg.size()]);
		
		for(int i=0; i < keys.length; i++){
			nameValuePairs.add(new BasicNameValuePair(keys[i], arg.get(keys[i])));
	 		
		}
		
//		if(arg.containsKey(rest_arg.type.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.type.toString(), arg.get(rest_arg.type.toString())));
// 		
//        if(arg.containsKey(rest_arg.credential_id.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.credential_id.toString(), arg.get(rest_arg.credential_id.toString())));
// 
//        if(arg.containsKey(rest_arg.resource_name.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.resource_name.toString(), arg.get(rest_arg.resource_name.toString())));
//        
//        if(arg.containsKey(rest_arg.credential_location.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.credential_location.toString(), arg.get(rest_arg.credential_location.toString())));        
//        
//        if(arg.containsKey(rest_arg.proxy_location.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.proxy_location.toString(), arg.get(rest_arg.proxy_location.toString())));
//        
//        if(arg.containsKey(rest_arg.user_key.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.user_key.toString(), arg.get(rest_arg.user_key.toString())));
//        
//        if(arg.containsKey(rest_arg.cert_dir.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.cert_dir.toString(), arg.get(rest_arg.cert_dir.toString())));
//        
//        if(arg.containsKey(rest_arg.registery.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.registery.toString(), arg.get(rest_arg.registery.toString())));
//        
//        if(arg.containsKey(rest_arg.username.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.username.toString(), arg.get(rest_arg.username.toString())));
//        
//        if(arg.containsKey(rest_arg.pwd.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.pwd.toString(), arg.get(rest_arg.pwd.toString())));
//        
//        if(arg.containsKey(rest_arg.alias.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.alias.toString(), arg.get(rest_arg.alias.toString())));
//        if(arg.containsKey(rest_arg.trust_path.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.trust_path.toString(), arg.get(rest_arg.trust_path.toString())));
//        if(arg.containsKey(rest_arg.trust_pwd.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.trust_pwd.toString(), arg.get(rest_arg.trust_pwd.toString())));
//        
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String editCredential(String id, HashMap<String, String> arg) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		StringWriter writer = new StringWriter();
		
		HttpPost post = new HttpPost(createURL(cred, id));
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        
		String[] keys = arg.keySet().toArray(new String[arg.size()]);
		
		for(int i=0; i < keys.length; i++){
			nameValuePairs.add(new BasicNameValuePair(keys[i], arg.get(keys[i])));
	 		
		}
		
//        if(arg.containsKey(rest_arg.credential_id.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.credential_id.toString(), arg.get(rest_arg.credential_id.toString())));
// 
//        if(arg.containsKey(rest_arg.resource_name.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.resource_name.toString(), arg.get(rest_arg.resource_name.toString())));
//        
//        if(arg.containsKey(rest_arg.credential_location.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.credential_location.toString(), arg.get(rest_arg.credential_location.toString())));        
//        
//        if(arg.containsKey(rest_arg.proxy_location.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.proxy_location.toString(), arg.get(rest_arg.proxy_location.toString())));
//        
//        if(arg.containsKey(rest_arg.user_key.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.user_key.toString(), arg.get(rest_arg.user_key.toString())));
//        
//        if(arg.containsKey(rest_arg.cert_dir.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.cert_dir.toString(), arg.get(rest_arg.cert_dir.toString())));
//        
//        if(arg.containsKey(rest_arg.registery.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.registery.toString(), arg.get(rest_arg.registery.toString())));
//        
//        if(arg.containsKey(rest_arg.username.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.username.toString(), arg.get(rest_arg.username.toString())));
//        
//        if(arg.containsKey(rest_arg.pwd.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.pwd.toString(), arg.get(rest_arg.pwd.toString())));
//        
//        if(arg.containsKey(rest_arg.alias.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.alias.toString(), arg.get(rest_arg.alias.toString())));
//        if(arg.containsKey(rest_arg.trust_path.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.trust_path.toString(), arg.get(rest_arg.trust_path.toString())));
//        if(arg.containsKey(rest_arg.trust_pwd.toString()))
//        	nameValuePairs.add(new BasicNameValuePair(rest_arg.trust_pwd.toString(), arg.get(rest_arg.trust_pwd.toString())));
//        
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}

	
	public String removeCredential(String id) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
		StringWriter writer = new StringWriter();
		
		HttpCustomDelete delete = new HttpCustomDelete(createURL( cred, id));
				
		HttpResponse response;
		response = client.execute(delete,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String addUserCredential(String user_id, String cred_id) throws IllegalStateException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		StringWriter writer = new StringWriter();
		
		HttpPost post = new HttpPost(createURL(profile, user_id,cred,""));
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair(rest_arg.credential_id.toString(), cred_id));
         post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
		
	}

	
	public String removeUserCredential(String user_id, String cred_id) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
		StringWriter writer = new StringWriter();
		
		HttpCustomDelete delete = new HttpCustomDelete(createURL( profile, user_id, cred, cred_id));
				
		HttpResponse response;
		response = client.execute(delete,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String appInstance_Status(String instance_id) throws ClientProtocolException, IOException, RESTException, AuthenticationFailedException, AuthorizationFailedException {

		StringWriter writer = new StringWriter();
		
		HttpGet httpget = new HttpGet(createURL(appinst, instance_id));
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}

	
	public String appInstance_SetDataStaging(String instance_id, HashMap<String, String> arg) throws ClientProtocolException, IOException, RESTException, AuthenticationFailedException, AuthorizationFailedException {

		StringWriter writer = new StringWriter();
		
		String stagein = arg.get(rest_arg.stagein.toString());
		String stageout = arg.get(rest_arg.stageout.toString());

		
		HttpPost post = new HttpPost(createURL( appinst, instance_id));

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		if(stagein != null)
			nameValuePairs.add(new BasicNameValuePair(rest_arg.stagein.toString(), stagein));
       
		if(stageout != null){
			nameValuePairs.add(new BasicNameValuePair(rest_arg.stageout.toString(), stageout));
		}
		
		
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}

	
//	public String appInstance_SetStageIn(String instance_id,HashMap<String, String> arg) throws ClientProtocolException, IOException, RESTException, AuthenticationFailedException, AuthorizationFailedException {
//
//		StringWriter writer = new StringWriter();
//		
//		String stagein = arg.get(rest_arg.stagein.toString());
//
//
//		
//		HttpPost post = new HttpPost(createURL( appinst, instance_id,transfer,"" ));
//
//		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//		
//		if(stagein != null)
//			nameValuePairs.add(new BasicNameValuePair(rest_arg.stagein.toString(), stagein));
//
//        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//		
//		HttpResponse response;
//		response = client.execute(post,localcontext);
//		HttpEntity entity = response.getEntity();
//		IOUtils.copy(entity.getContent(),writer);
//		
//		EntityUtils.consume(entity);
//		
//		checkResponseStatusCode(response,writer);
//		
//		return writer.toString();
//		
//	}
//
//	
//	public String appInstance_SetStageOut(String instance_id,HashMap<String, String> arg) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
//
//		StringWriter writer = new StringWriter();
//
//		String stageout = arg.get(rest_arg.stageout.toString());
//
//		
//		HttpPost post = new HttpPost(createURL( appinst, instance_id, transfer,""));
//
//		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//		
//
//		if(stageout != null){
//			nameValuePairs.add(new BasicNameValuePair(rest_arg.stageout.toString(), stageout));
//		}
//		
//		
//        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//		
//		HttpResponse response;
//		response = client.execute(post,localcontext);
//		HttpEntity entity = response.getEntity();
//		IOUtils.copy(entity.getContent(),writer);
//		
//		EntityUtils.consume(entity);
//		
//		checkResponseStatusCode(response,writer);
//		
//		return writer.toString();
//		
//	}

	
	public String appInstance_deleteDataStaging(String instance_id) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		StringWriter writer = new StringWriter();
			
		HttpCustomDelete delete = new HttpCustomDelete(createURL( appinst, instance_id, transfer, ""));
				
		HttpResponse response;
		response = client.execute(delete,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}
	
	
	public String appInstance_deleteDataStaging(String instance_id,
			String transfer_id) throws Exception {

		StringWriter writer = new StringWriter();
		
		HttpCustomDelete delete = new HttpCustomDelete(createURL( appinst, instance_id,transfer,transfer_id));
				
		HttpResponse response;
		response = client.execute(delete,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		checkResponseStatusCode(response,writer);
		return writer.toString();
		
	}

	
	public String appInstance_deleteSetStageIn(String instance_id) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		StringWriter writer = new StringWriter();
		
		HttpCustomDelete delete = new HttpCustomDelete(createURL( appinst, instance_id, transfer, ""));
				
		HttpResponse response;
		response = client.execute(delete,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}

	
	public String appInstance_deleteSetStageOut(String instance_id) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		StringWriter writer = new StringWriter();
		
		HttpCustomDelete delete = new HttpCustomDelete(createURL( appinst, instance_id,transfer,""));
				
		HttpResponse response;
		response = client.execute(delete,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}

	
	public String appInstance_getDataStaging(String instance_id) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
		StringWriter writer = new StringWriter();
		
		HttpGet httpget = new HttpGet(createURL( appinst, instance_id,transfer,""));
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	/**
	 * TODO fix this
	 * @throws RESTException 
	 * @throws AuthenticationFailedException 
	 * @throws AuthorizationFailedException 
	 */
	
//	
//	public String appInstance_getStageIn(String instance_id) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
//		StringWriter writer = new StringWriter();
//		
//		HttpGet httpget = new HttpGet(createURL( appinst, instance_id, transfer,""));
//		
//		HttpResponse response;
//		response = client.execute(httpget,localcontext);
//		HttpEntity entity = response.getEntity();
//		IOUtils.copy(entity.getContent(),writer);
//		
//		EntityUtils.consume(entity);
//		
//		checkResponseStatusCode(response,writer);
//		
//		return writer.toString();
//	}
	
	/**
	 * TODO fix this
	 * @throws RESTException 
	 * @throws AuthenticationFailedException 
	 * @throws AuthorizationFailedException 
	 */
//
//	
//	public String appInstance_getStageOut(String instance_id) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
//		StringWriter writer = new StringWriter();
//		
//		HttpGet httpget = new HttpGet(createURL( appinst, instance_id, transfer,""));
//		
//		HttpResponse response;
//		response = client.execute(httpget,localcontext);
//		HttpEntity entity = response.getEntity();
//		IOUtils.copy(entity.getContent(),writer);
//		
//		EntityUtils.consume(entity);
//		
//		checkResponseStatusCode(response,writer);
//		
//		return writer.toString();
//	}

	
	public String appInstance_Clone(String instance_id) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException  {
		StringWriter writer = new StringWriter();
		
		HttpGet httpget = new HttpGet(createURL( appinst, instance_id, clone,""));
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	public String appInstance_Prepare(HashMap<String, String> arg) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		StringWriter writer = new StringWriter();
		
//		String appname = arg.get(rest_arg.appname.toString());
//		String jobname = arg.get(rest_arg.jobname.toString());
//		String cpucount = arg.get(rest_arg.cpucount.toString());
//		String memory = arg.get(rest_arg.memory.toString());
//		String vmemory= arg.get(rest_arg.vmemory.toString());
//		String walltimelimit = arg.get(rest_arg.walltimelimit.toString());
		
		HttpPost post = new HttpPost(createURL( appinst, ""));

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		String[] keys = arg.keySet().toArray(new String[arg.size()]);
		
		for(int i=0; i < keys.length; i++){
			nameValuePairs.add(new BasicNameValuePair(keys[i], arg.get(keys[i])));
	 		
		}
		
//		if(appname != null)
//			nameValuePairs.add(new BasicNameValuePair(rest_arg.appname.toString(), appname));
//       
//		if(jobname != null){
//			nameValuePairs.add(new BasicNameValuePair(rest_arg.jobname.toString(), jobname));
//		}
//		
//		if(cpucount != null)
//			nameValuePairs.add(new BasicNameValuePair(rest_arg.cpucount.toString(),cpucount));
//
//		if(memory  != null)
//			nameValuePairs.add(new BasicNameValuePair(rest_arg.memory.toString(),memory));
//		
//		if(vmemory != null)
//			nameValuePairs.add(new BasicNameValuePair(rest_arg.vmemory.toString(),vmemory));
//		
//		if(walltimelimit != null)
//			nameValuePairs.add(new BasicNameValuePair(rest_arg.walltimelimit.toString(),walltimelimit));
		
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}
	
	public String appInstance_start(HashMap<String, String> arg) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		StringWriter writer = new StringWriter();
		
//		String appname = arg.get(rest_arg.appname.toString());
//		String jobname = arg.get(rest_arg.jobname.toString());
//		String cpucount = arg.get(rest_arg.cpucount.toString());
//		String memory = arg.get(rest_arg.memory.toString());
//		String vmemory= arg.get(rest_arg.vmemory.toString());
//		String walltimelimit = arg.get(rest_arg.walltimelimit.toString());
		
		HttpPost post = new HttpPost(createURL( appinst, ""));

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		String[] keys = arg.keySet().toArray(new String[arg.size()]);
		
		for(int i=0; i < keys.length; i++){
			nameValuePairs.add(new BasicNameValuePair(keys[i], arg.get(keys[i])));
	 		
		}
		
//		if(appname != null)
//			nameValuePairs.add(new BasicNameValuePair(rest_arg.appname.toString(), appname));
//       
//		if(jobname != null){
//			nameValuePairs.add(new BasicNameValuePair(rest_arg.jobname.toString(), jobname));
//		}
//		
//		if(cpucount != null)
//			nameValuePairs.add(new BasicNameValuePair(rest_arg.cpucount.toString(),cpucount));
//
//		if(memory  != null)
//			nameValuePairs.add(new BasicNameValuePair(rest_arg.memory.toString(),memory));
//		
//		if(vmemory != null)
//			nameValuePairs.add(new BasicNameValuePair(rest_arg.vmemory.toString(),vmemory));
//		
//		if(walltimelimit != null)
//			nameValuePairs.add(new BasicNameValuePair(rest_arg.walltimelimit.toString(),walltimelimit));
		
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}

	
	public String appInstance_Start(String instance_id, HashMap<String, String> arg) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		StringWriter writer = new StringWriter();
		
		Set<String> key = arg.keySet();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		HttpPost post = new HttpPost(createURL( appinst, instance_id, runtime,""));
		
		for(String  s : key){
			nameValuePairs.add(new BasicNameValuePair(s,arg.get(s)));
		}
		
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}

	
	public String appInstance_Terminate(String instance_id) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
		StringWriter writer = new StringWriter();
		
		HttpCustomDelete delete = new HttpCustomDelete(createURL( appinst, instance_id));
				
		HttpResponse response;
		response = client.execute(delete,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String appInstance_SetProperty(String instance_id, HashMap<String, String> arg) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		StringWriter writer = new StringWriter();
		
		Set<String> key = arg.keySet();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		HttpPost post = new HttpPost(createURL( appinst, instance_id, property,""));
		
		for(String  s : key){
			nameValuePairs.add(new BasicNameValuePair(s,arg.get(s)));
		}
		
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}

	
	public String appInstance_GetProperty(String instance_id) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
		
		StringWriter writer = new StringWriter();
		HttpGet httpget = new HttpGet(createURL( appinst, instance_id, property,""));
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String appInstance_DeleteProperty(String instance_id,HashMap<String, String> arg) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		StringWriter writer = new StringWriter();
		
		Set<String> key = arg.keySet();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		HttpCustomDelete delete = new HttpCustomDelete(createURL( appinst, instance_id, property, ""));
		
		for(String  s : key){
			nameValuePairs.add(new BasicNameValuePair(s,arg.get(s)));
		}

        delete.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(delete,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}
	
	
	public String appInstance_DeleteProperty(String instance_id, String prop_id) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		StringWriter writer = new StringWriter();
		
		HttpCustomDelete delete = new HttpCustomDelete(createURL( appinst, instance_id,property,prop_id));
		
		HttpResponse response;
		response = client.execute(delete,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}

	
	public String appInstance_ListProperties(String instance_id) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
		StringWriter writer = new StringWriter();
		
		HttpGet httpget = new HttpGet(createURL( appinst, instance_id, property,""));
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String createResource(HashMap<String, String> arg) throws IllegalStateException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		StringWriter writer = new StringWriter();
		
		String name = arg.get(rest_arg.name.toString());
		String uri = arg.get(rest_arg.endpoint_reference.toString());
		String inter = arg.get(rest_arg.resource_interface.toString());

		String type = arg.get(rest_arg.type.toString());
		String cpucount = arg.get(rest_arg.cpucount.toString());
		String memory = arg.get(rest_arg.memory.toString());
		
		String vmemory = arg.get(rest_arg.vmemory.toString());
		String walltime = arg.get(rest_arg.walltimelimit.toString());
		String arch = arg.get(rest_arg.arch.toString());
		
		String ip = arg.get(rest_arg.ip.toString());
		String opsys = arg.get(rest_arg.opsys.toString());
				
		HttpPost post = new HttpPost(createURL( resource, ""));

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(rest_arg.name.toString(), name));
        nameValuePairs.add(new BasicNameValuePair(rest_arg.endpoint_reference.toString(),uri));
        nameValuePairs.add(new BasicNameValuePair(rest_arg.resource_interface.toString(),inter));
        
        if(type != null)
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.type.toString(), type));
        else
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.type.toString(), ""));
        	
        if(cpucount != null)
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.cpucount.toString(),cpucount));
        else
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.cpucount.toString(),""+0));
        
        if(memory != null)
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.memory.toString(),memory));
        else
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.memory.toString(),""+0));
        
        if(vmemory != null)
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.vmemory.toString(), vmemory));
        else
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.vmemory.toString(), ""+0));
        
        if(walltime != null)
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.walltimelimit.toString(),walltime));
        else
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.walltimelimit.toString(), ""+0));
        
        if(arch != null)
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.arch.toString(),arch));
        else
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.arch.toString(), ""));
        
        if(ip != null)
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.ip.toString(), ip));
        else
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.ip.toString(), ""));
        
        if(opsys != null)
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.opsys.toString(),opsys));
        else
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.opsys.toString(),""));
        
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		
		
		return writer.toString();
		
	}

	
	public String editResource(String id, HashMap<String, String> arg) throws IllegalStateException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
		
		StringWriter writer = new StringWriter();
		
		String name = arg.get(rest_arg.name.toString());
		String uri = arg.get(rest_arg.endpoint_reference.toString());
		String inter = arg.get(rest_arg.resource_interface.toString());

		String type = arg.get(rest_arg.type.toString());
		String cpucount = arg.get(rest_arg.cpucount.toString());
		String memory = arg.get(rest_arg.memory.toString());
		
		String vmemory = arg.get(rest_arg.vmemory.toString());
		String walltime = arg.get(rest_arg.walltimelimit.toString());
		String arch = arg.get(rest_arg.arch.toString());
		
		String ip = arg.get(rest_arg.ip.toString());
		String opsys = arg.get(rest_arg.opsys.toString());
		
		HttpPost post = new HttpPost(createURL( resource, id));

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		if(name != null)
			nameValuePairs.add(new BasicNameValuePair(rest_arg.name.toString(), name));
		
		if(uri != null)
			nameValuePairs.add(new BasicNameValuePair(rest_arg.endpoint_reference.toString(),uri));
		
		if(inter != null)
			nameValuePairs.add(new BasicNameValuePair(rest_arg.resource_interface.toString(),inter));
        
        if(type != null)
        	nameValuePairs.add(new BasicNameValuePair(rest_arg.type.toString(), type));
        
        if(cpucount != null)
        nameValuePairs.add(new BasicNameValuePair(rest_arg.cpucount.toString(),cpucount));
        
        if(memory != null)
        nameValuePairs.add(new BasicNameValuePair(rest_arg.memory.toString(),memory));
        
        if(vmemory != null)
        nameValuePairs.add(new BasicNameValuePair(rest_arg.vmemory.toString(), vmemory));
        
        if(walltime != null)
        nameValuePairs.add(new BasicNameValuePair(rest_arg.walltimelimit.toString(),walltime));
        
        if(arch != null)
        nameValuePairs.add(new BasicNameValuePair(rest_arg.arch.toString(),arch));
        
        if(ip != null)
        nameValuePairs.add(new BasicNameValuePair(rest_arg.ip.toString(), ip));
        
        if(opsys != null)
        nameValuePairs.add(new BasicNameValuePair(rest_arg.opsys.toString(),opsys));
        
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String listResource() throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
		StringWriter writer = new StringWriter();
		
		HttpGet httpget = new HttpGet(createURL( resource, ""));
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String removeResource(String id) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
		StringWriter writer = new StringWriter();
		
		HttpCustomDelete delete = new HttpCustomDelete(createURL( resource, id));
				
		HttpResponse response;
		response = client.execute(delete,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String createApp(HashMap<String, String> arg) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
		
		StringWriter writer = new StringWriter();
		
		String name = arg.get(rest_arg.appname.toString());
		String exec = arg.get(rest_arg.exec.toString());
		String resource = arg.get(rest_arg.resource_name.toString());
		
		HttpPost post = new HttpPost(createURL( app, ""));

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(rest_arg.appname.toString(), name));
        nameValuePairs.add(new BasicNameValuePair(rest_arg.resource_name.toString(),resource));
        nameValuePairs.add(new BasicNameValuePair(rest_arg.exec.toString(),exec));
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String editApp(String app_id, HashMap<String, String> arg) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		StringWriter writer = new StringWriter();
		
		
		String appname = arg.get(rest_arg.appname.toString());
		String exec = arg.get(rest_arg.exec.toString());
		String resourcename = arg.get(rest_arg.resource_name.toString());
		
		HttpPost post = new HttpPost(createURL( app, app_id));

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		if(appname.length() > 0)
			nameValuePairs.add(new BasicNameValuePair(rest_arg.appname.toString(), appname));
       
		if(resourcename.length() > 0)
			nameValuePairs.add(new BasicNameValuePair(rest_arg.resource_name.toString(),resourcename));
		
		if(exec.length() > 0)
			nameValuePairs.add(new BasicNameValuePair(rest_arg.exec.toString(),exec));
		
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}

	
	public String removeApp(String app_id) throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
		StringWriter writer = new StringWriter();
		
		HttpCustomDelete delete = new HttpCustomDelete(createURL( app, app_id));
				
		HttpResponse response;
		response = client.execute(delete,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String listApp() throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {

		StringWriter writer = new StringWriter();
		
		HttpGet httpget = new HttpGet(createURL( app, ""));
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String listUsers() throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
		StringWriter writer = new StringWriter();
		
		//HttpGet httpget = new HttpGet(createURL( profile, ""));
		//HttpGet httpget = new HttpGet("http://localhost:8080/Core/resource/getResource");
		HttpGet httpget = new HttpGet("http://localhost:8080/profile");
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String listJobs() throws ClientProtocolException, IOException, RESTException, AuthorizationFailedException, AuthenticationFailedException {
		StringWriter writer = new StringWriter();
		
		HttpGet httpget = new HttpGet(createURL(appinst, ""));
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String getSessionToken(String username, String password)	throws Exception {
		
		StringWriter writer = new StringWriter();
		
		HttpPost post = new HttpPost(createURL(session, ""));
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}
	
	private void checkResponseStatusCode(HttpResponse response, StringWriter writer) throws RESTException, AuthorizationFailedException, AuthenticationFailedException{
		
		if(response.getStatusLine().getStatusCode() >= 400){
			
			if(response.getStatusLine().getStatusCode() == 401){
				throw new AuthenticationFailedException(writer.toString());
			}
			
			if(response.getStatusLine().getStatusCode() == 403){
				throw new AuthorizationFailedException(writer.toString());
			}
			
			throw new RESTException(writer.toString());
		}
		
	}

	
	public String getApp(int id) throws Exception {
		StringWriter writer = new StringWriter();
		
		HttpGet httpget = new HttpGet(createURL(app, ""+id));
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}
	
	

	
	public String getResource(String name) throws Exception {
		StringWriter writer = new StringWriter();
		
		HttpGet httpget = new HttpGet(createURL(resource, name));
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String getUser(String username) throws Exception {
		StringWriter writer = new StringWriter();
		
		HttpGet httpget = new HttpGet(createURL(profile, username));
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String getCredential(String name) throws Exception {
		StringWriter writer = new StringWriter();
		
		HttpGet httpget = new HttpGet(createURL(cred, name));
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String mapCredentialToResource(String resource_name, String cred_name) throws Exception {
	
		StringWriter writer = new StringWriter();

		
		HttpPost post = new HttpPost(createURL( resource,resource_name,cred,""));

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(rest_arg.credential_id.toString(), cred_name));
       
		
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String unmapCredentialToResource(String resource_name, String cred_name) throws Exception {
		StringWriter writer = new StringWriter();
		
		HttpCustomDelete delete = new HttpCustomDelete(createURL(resource, resource_name,cred,cred_name));
				
		HttpResponse response;
		response = client.execute(delete,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String getServerStatus()
			throws Exception {
		
		StringWriter writer = new StringWriter();
		
		HttpGet httpget = new HttpGet(createURL("",""));
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String listTransfers() throws Exception {
		StringWriter writer = new StringWriter();
		
		HttpGet httpget = new HttpGet(createURL( transfer, ""));
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String getTransfers(int id) throws Exception {
		StringWriter writer = new StringWriter();
		
		HttpGet httpget = new HttpGet(createURL(transfer, ""+id));
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
	}

	
	public String createTransfer(String[] source, String[] target)
			throws Exception {

		StringWriter writer = new StringWriter();
		
		HttpPost post = new HttpPost(createURL( transfer, ""));

		String transfer = "[";
		
		String source_array = "[";
		String target_array = "[";
		
		for(int i=0; i < source.length; i++){
			
			source_array += "'"+source[i]+"'";
			target_array += "'"+target[i]+"'";
			
			if(i != source.length-1){
				source_array += ",";
				target_array += ",";
			}
			
		}
		
		source_array += "]";
		target_array += "]";
		
		transfer += source_array + "," + target_array + "]";
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(rest_arg.transfer.toString(), transfer));

        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}
	
	public String get(String uri) throws Exception{
		
		StringWriter writer = new StringWriter();
		
		HttpGet httpget = new HttpGet(uri);
		
		HttpResponse response;
		response = client.execute(httpget,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}
	
	public String post(String uri, HashMap<String, String> arg) throws Exception{
		
		StringWriter writer = new StringWriter();
		
		HttpPost post = new HttpPost(uri);

		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		String[] key = arg.keySet().toArray(new String[arg.size()]);
		
		for(String k : key){
			nameValuePairs.add(new BasicNameValuePair(k, arg.get(k)));
		}
		
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}
	
	public String post(String uri, String data) throws Exception{
		
		StringWriter writer = new StringWriter();
		
		HttpPost post = new HttpPost(uri);
				
        post.setEntity(new StringEntity(data));
		
		HttpResponse response;
		response = client.execute(post,localcontext);
		HttpEntity entity = response.getEntity();
		IOUtils.copy(entity.getContent(),writer);
		
		EntityUtils.consume(entity);
		
		checkResponseStatusCode(response,writer);
		
		return writer.toString();
		
	}
	
}