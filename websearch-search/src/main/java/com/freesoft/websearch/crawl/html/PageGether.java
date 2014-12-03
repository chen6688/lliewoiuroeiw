/**
 * 
 */
package com.freesoft.websearch.crawl.html;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.HttpEntityWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.fetcher.AbstractPageFetcher;
import edu.uci.ics.crawler4j.fetcher.CustomFetchStatus;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * @author chen
 * 
 */
public class PageGether extends AbstractPageFetcher {

	private static final Logger log = LoggerFactory.getLogger(PageGether.class);

	private WebClient webClient;
	private final Object mutex = new Object();

	private long lastFetchTime = 0;
	/**
	 * @param config
	 */
	public PageGether(CrawlConfig config) {
		super(config);

		webClient = new WebClient();
		WebClientOptions options = webClient.getOptions();
		options.setJavaScriptEnabled(true);// js analysis
		options.setCssEnabled(true);// css analysis
		options.setThrowExceptionOnFailingStatusCode(false);
		options.setThrowExceptionOnScriptError(false);
		options.setTimeout(10000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uci.ics.crawler4j.fetcher.PageFetcher#fetchHeader(edu.uci.ics.crawler4j
	 * .url.WebURL)
	 */
	@Override
	public PageFetchResult fetchHeader(WebURL webUrl) {
		PageFetchResult fetchResult = new PageFetchResult();
		String toFetchURL = webUrl.getURL();
		WebRequest request = null;
		WebResponse response = null;
		Page page = null;
		try {
			request = new WebRequest(new URL(toFetchURL));
			request.setCharset("UTF-8");
			request.setAdditionalHeader("Referer", toFetchURL);
			request.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");

			synchronized (mutex) {
				long now = (new Date()).getTime();
				if (now - lastFetchTime < config.getPolitenessDelay()) {
					Thread.sleep(config.getPolitenessDelay() - (now - lastFetchTime));
				}
				lastFetchTime = (new Date()).getTime();
			}
			long startTime = System.currentTimeMillis();
			page = webClient.getPage(request);

			fetchResult.setFetchTime(System.currentTimeMillis() - startTime);
			fetchResult.setOtherObj(page);
			response = page.getWebResponse();
			int statusCode = response.getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				if (statusCode != HttpStatus.SC_NOT_FOUND) {
					if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
						String movedToUrl = response.getResponseHeaderValue("Location");
						if (movedToUrl != null) {
							movedToUrl = URLCanonicalizer.getCanonicalURL(movedToUrl, toFetchURL);
							fetchResult.setMovedToUrl(movedToUrl);
						}
						fetchResult.setStatusCode(statusCode);
						return fetchResult;
					}
					log.info("Failed: " + response.getStatusMessage() + ", while fetching " + toFetchURL);
				}
				fetchResult.setStatusCode(statusCode);
				return fetchResult;
			}

			fetchResult.setFetchedUrl(toFetchURL);
			String uri = request.getUrl().getPath();
			if (!uri.equals(toFetchURL)) {
				if (!URLCanonicalizer.getCanonicalURL(uri).equals(toFetchURL)) {
					fetchResult.setFetchedUrl(uri);
				}
			}

			if (fetchResult.getEntity() != null) {
				long size = fetchResult.getEntity().getContentLength();
				if (size == -1) {
					String length = response.getResponseHeaderValue("Content-Length");
					if (length == null) {
						length = response.getResponseHeaderValue("Content-length");
					}
					if (length != null) {
						size = Integer.parseInt(length);
					} else {
						size = -1;
					}
				}
				if (size > config.getMaxDownloadSize()) {
					fetchResult.setStatusCode(CustomFetchStatus.PageTooBig);
					response.cleanUp();
					return fetchResult;
				}

				fetchResult.setStatusCode(HttpStatus.SC_OK);
				return fetchResult;

			}

			response.cleanUp();

		} catch (IOException e) {
			log.error("Fatal transport error: " + e.getMessage() + " while fetching " + toFetchURL + " (link found in doc #" + webUrl.getParentDocid() + ")");
			fetchResult.setStatusCode(CustomFetchStatus.FatalTransportError);
			return fetchResult;
		} catch (InterruptedException e) {
			log.error("Begin to fetch the url error " + toFetchURL, e);
		} finally {
			if (fetchResult.getEntity() == null && response != null) {
				response.cleanUp();
			}
		}
		fetchResult.setStatusCode(CustomFetchStatus.UnknownError);
		return fetchResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.crawler4j.fetcher.AbstractPageFetcher#shutDown()
	 */
	@Override
	public synchronized void shutDown() {

	}

	private static class HtmlUnitEntity extends HttpEntityWrapper {

		/**
		 * @param wrappedEntity
		 */
		public HtmlUnitEntity(HttpEntity wrappedEntity) {
			super(wrappedEntity);
			// TODO Auto-generated constructor stub
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.http.entity.HttpEntityWrapper#getContent()
		 */
		@Override
		public InputStream getContent() throws IOException {
			// TODO Auto-generated method stub
			return super.getContent();
		}
	}
}
