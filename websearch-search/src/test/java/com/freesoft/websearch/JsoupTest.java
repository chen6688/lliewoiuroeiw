/**
 * 
 */
package com.freesoft.websearch;

import java.io.FileInputStream;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

/**
 * @author chen
 *
 */
public class JsoupTest {

	@Test
	public void testParseTime() throws IOException {
		long startTime = System.currentTimeMillis();
		FileInputStream input = new FileInputStream("E:\\study\\pages\\aadaa268f5794c7f9715a0d29efc6ea4");
		Document doc = Jsoup.parse(input, "UTF-8", "http://www.hao123.com/");
		Elements elm = doc.getElementsByAttribute("href");
		System.out.println(elm.size());
		
		System.out.println("Time=" + (System.currentTimeMillis() - startTime));
		
		for(int i =0 ;i < elm.size(); i ++) {
			Element elml =  elm.get(i);
			System.out.println(elml.text());
		}
	}
}
