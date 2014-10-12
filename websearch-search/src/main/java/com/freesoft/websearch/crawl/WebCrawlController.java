/**
 * 
 */
package com.freesoft.websearch.crawl;

import java.lang.reflect.Field;

import com.freesoft.websearch.common.Constrant;
import com.freesoft.websearch.utils.ConfigUtils;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * @author chen
 * 
 */
public class WebCrawlController {

	private CrawlConfig config;
	private CrawlController controller;
	private PageFetcher fetcher;
	private RobotstxtServer robotsServer;

	public WebCrawlController() throws IllegalArgumentException, IllegalAccessException {
		initCrawlConfig();
	}

	/**
	 * 
	 * @return
	 */
	public CrawlController getController() {
		return controller;
	}
	/**
	 * Initail the crawl config
	 * 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public void initCrawlConfig() throws IllegalArgumentException, IllegalAccessException {
		System.out.println("Initail the crawl config.");
		config = new CrawlConfig();

		Field[] fieldArr = config.getClass().getDeclaredFields();
		for (Field filed : fieldArr) {
			String key = filed.getName();

			Object value = ConfigUtils.getPropertyValue(key, filed.getType());
			if (value != null && !"".equals(value)) {
				filed.setAccessible(true);
				filed.set(config, value);
			}
		}

		fetcher = new PageFetcher(config);

		RobotstxtConfig robotsConfig = new RobotstxtConfig();
		String robotUserAgent = ConfigUtils.getPropertyValue(Constrant.ROBOT_USER_AGENT);
		if (robotUserAgent != null) {
			robotsConfig.setUserAgentName(robotUserAgent);
		}
		robotsServer = new RobotstxtServer(robotsConfig, fetcher);
	}

	/**
	 * @throws Exception
	 * 
	 */
	public void startCrawl() throws Exception {
		controller = new CrawlController(config, fetcher, robotsServer);

		//add the crawl URLs
		String crawlUrls = ConfigUtils.getPropertyValue(Constrant.BEGIN_SITE_URLS);
		String[] crawlUrlArr = crawlUrls.split("$$");
		for (String url : crawlUrlArr) {
			controller.addSeed(url);
		}

		//set the thread number
		String threadStr = ConfigUtils.getPropertyValue(Constrant.CRAWLER_THREAD_NUM);
		int numberOfCrawler = Integer.parseInt(threadStr);
		
		// start the crawler
		controller.startNonBlocking(WebBaseCrawler.class, numberOfCrawler);
	}

	/**
	 * 
	 */
	public void stopCrawl() {
		controller.shutdown();
		controller.waitUntilFinish();
	}
}
