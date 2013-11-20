package com.voiceblue.connection;

import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class HttpsClient extends DefaultHttpClient {

	public static DefaultHttpClient getClient(String username, String password) throws Exception {
		
		DefaultHttpClient client = getClient();
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
		
        ((AbstractHttpClient) client).getCredentialsProvider()
         								.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), 
        		 										creds);
		return client;
	}
	
	public static DefaultHttpClient getClient() throws Exception {
		// Setup a custom SSL Factory object which simply ignore the certificates
		// validation and accept all type of self signed certificates
		
		SSLSocketFactory sslFactory = new SimpleSSLSocketFactory(null);
		sslFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		 
		// Enable HTTP parameters
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		 
		// Register the HTTP and HTTPS Protocols. For HTTPS, register our custom SSL Factory object.
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", sslFactory, 443));
		 
		// Create a new connection manager using the newly created registry and then create a new HTTP client
		// using this connection manager
		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
		
		return new DefaultHttpClient(ccm, params);
	}

}
