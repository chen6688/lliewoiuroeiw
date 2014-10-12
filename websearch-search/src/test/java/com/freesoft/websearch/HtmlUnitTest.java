/**
 * 
 */
package com.freesoft.websearch;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author chen
 * 
 */
public class HtmlUnitTest {

	@Test
	public void testHtmlParseTime() throws FailingHttpStatusCodeException, IOException {
		String url = "http://www.hao123.com/";// 想采集的网址
		String refer = url;
		URL link = new URL(url);
		WebClient wc = new WebClient();
		WebRequest request = new WebRequest(link);
		request.setCharset("UTF-8");
//		request.setProxyHost("120.120.120.x");
//		request.setProxyPort(8080);
		request.setAdditionalHeader("Referer", refer);// 设置请求报文头里的refer字段
		// //设置请求报文头里的User-Agent字段
		request.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		request.setAdditionalHeader("Accept-Encoding", "gzip");
		// wc.addRequestHeader("User-Agent",
		// "Mozilla/5.0 (Windows NT 5.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		// wc.addRequestHeader和request.setAdditionalHeader功能应该是一样的。选择一个即可。
		// 其他报文头字段可以根据需要添加
		wc.getCookieManager().setCookiesEnabled(false);// 开启cookie管理
		wc.getOptions().setJavaScriptEnabled(true);// 开启js解析。对于变态网页，这个是必须的
		wc.getOptions().setCssEnabled(true);// 开启css解析。对于变态网页，这个是必须的。
		wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
		wc.getOptions().setThrowExceptionOnScriptError(false);
		wc.getOptions().setTimeout(10000);
		// 设置cookie。如果你有cookie，可以在这里设置
//		Set<Cookie> cookies = null;
//		Iterator<Cookie> i = cookies.iterator();
//		while (i.hasNext()) {
//			wc.getCookieManager().addCookie(i.next());
//		}
		// 准备工作已经做好了
		HtmlPage page = null;
		long startTime = System.currentTimeMillis();
		page = wc.getPage(request);
		System.out.println("time=" + (System.currentTimeMillis() - startTime));
		page.getWebResponse().cleanUp();
		if (page == null) {
			System.out.println("采集 " + url + " 失败!!!");
			return;
		}
		
		String content = page.asText();// 网页内容保存在content里
		if (content == null) {
			System.out.println("采集 " + url + " 失败!!!");
			return;
		}
//		System.out.println(page.asXml());
//		outputPageContent(page.asXml());
		
		DomNodeList<DomElement> aNodes=	page.getElementsByTagName("a");
		System.out.println(aNodes.size());
		
//		outputAllUrl(aNodes);
		// 搞定了
//		CookieManager CM = wc.getCookieManager(); // WC = Your WebClient's name
//		Set<Cookie> cookies_ret = CM.getCookies();// 返回的Cookie在这里，下次请求的时候可能可以用上啦。
	}
	
	private void outputPageContent(String content) throws IOException{
		FileOutputStream output = new FileOutputStream("E:\\study\\pages\\testRealPage", false);
		output.write(content.getBytes());
		output.close();
	}
	
	private void outputAllUrl(DomNodeList<DomElement> aNodes) throws IOException {
		FileOutputStream output = new FileOutputStream("E:\\study\\pages\\pageurls", false);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
;		for(int i = 0; i < aNodes.size(); i++) {
			DomElement ele = aNodes.get(i);
			writer.write(ele.getTextContent() + "---------" + ele.getAttribute("href") + "\r\n");
		}
		writer.close();
	}
}
