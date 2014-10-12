package com.freesoft.websearch;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
		
		HttpGet get = new HttpGet("http://www.hao123.com/robots.txt");
		
		HttpResponse getRes = client.execute(get);
		HttpEntity entiry = getRes.getEntity();
		System.out.println(EntityUtils.toString(entiry,"UTF-8"));
	}
}
