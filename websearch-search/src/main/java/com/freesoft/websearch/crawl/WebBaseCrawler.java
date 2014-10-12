package com.freesoft.websearch.crawl;

import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.freesoft.websearch.cache.DataCache;
import com.freesoft.websearch.common.Constrant;
import com.freesoft.websearch.utils.ConfigUtils;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * 
 * @author chen
 * 
 */
public class WebBaseCrawler extends WebCrawlerExtend {
	private static Logger log = LoggerFactory.getLogger(WebBaseCrawler.class);

	private static Pattern FILTERS;
	/**
	 * 
	 */
	@Override
	public void onStart() {
		super.onStart();
		log.info("start the web crawl controller");

		loadCrawlUrlFilter();
	}

	/**
	 * 
	 */
	public void loadCrawlUrlFilter() {
		String crawlUrlFilter = ConfigUtils.getPropertyValue(Constrant.CRAWL_URL_FILETER);
		FILTERS = Pattern.compile(crawlUrlFilter);
	}

	/**
	 * 
	 */
	@Override
	protected void onContentFetchError(WebURL webUrl) {
		DataCache.addErrorUrlQueue(webUrl.getURL());
		log.info("Error page : ", webUrl.getURL());
	}

	/**
	 * 
	 */
	@Override
	protected void onParseError(WebURL webUrl) {
		DataCache.addErrorUrlQueue(webUrl.getURL());
		log.info("Error page : ", webUrl.getURL());
	}

	/**
	 * 
	 */
	@Override
	public void onBeforeExit() {
		super.onBeforeExit();
		log.info("end the web crawl controllere ");
	}

	/**
	 * 
	 */
	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		super.handlePageStatusCode(webUrl, statusCode, statusDescription);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uci.ics.crawler4j.crawler.WebCrawler#onErrorPageStatus(edu.uci.ics
	 * .crawler4j.url.WebURL, int, java.lang.String)
	 */
	@Override
	protected void onErrorPageStatus(WebURL webUrl, int statusCode, String statusDescription) {
		super.onErrorPageStatus(webUrl, statusCode, statusDescription);
		DataCache.addErrorUrlQueue(webUrl.getURL());
		log.info(String.format("Error page info : statusCode=%s  statusDescription=%s url=%s ", statusCode, statusDescription, webUrl.getURL()));
	}

	/**
	 * 
	 */
	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches();
	}

	/**
	 * 
	 */
	@Override
	public void visit(Page page) {
		String pageUrl = page.getWebURL().getURL();
		log.info(String.format("visit the page : %s  pagesize : %s", pageUrl, page.getContentData().length));

		Object pageObj = page.getOtherObj();
		if(pageObj == null) {
			return;
		}
		
		String saveFileName = UUID.randomUUID().toString().replaceAll("-", "");
		DataCache.addWebNameValue(saveFileName, pageUrl);

		try {
			DataCache.addHtmlInputMap(saveFileName, page.getContentData());
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}
}
