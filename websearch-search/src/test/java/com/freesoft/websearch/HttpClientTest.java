package com.freesoft.websearch;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class HttpClientTest extends TestCase {

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(HttpClientTest.class);
	}
	
	public void testWebGet() throws ClientProtocolException, IOException{
		HttpClient client  = new DefaultHttpClient();
		
		HttpPost post = new HttpPost("http://115.28.144.215:8080/zhenxin/a/login");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("username","admin"));
		nvps.add(new BasicNameValuePair("password","admin"));
		nvps.add(new BasicNameValuePair("rememberMe","true"));
		post.setEntity(new UrlEncodedFormEntity(nvps,"UTF-8"));
		
		HttpResponse getRes = client.execute(post);
		int status = getRes.getStatusLine().getStatusCode();
//		String location = getRes.getHeaders("Location")[0].getValue();
//		System.out.println(status + "=" + location);
		printHeaderStatus(getRes);
//		post.abort();
//		post = new HttpPost(location);
//		getRes = client.execute(post);
//		status = getRes.getStatusLine().getStatusCode();
//		printHeaderStatus(getRes);
//		
		if(status == 200) {
			InputStream stream = getRes.getEntity().getContent();
			DataInputStream datainput = new DataInputStream(stream);
			String line = datainput.readLine();
			while(line != null) {
				System.out.println(line);
				line = datainput.readLine();
			}
		}
	}
	
	private void printHeaderStatus(HttpResponse getRes) {
		System.out.println(getRes.getStatusLine().getStatusCode());
		for(Header head : getRes.getAllHeaders()) {
			System.out.println(head.getName() + "==" + head.getValue());
		}
		
	}
}
