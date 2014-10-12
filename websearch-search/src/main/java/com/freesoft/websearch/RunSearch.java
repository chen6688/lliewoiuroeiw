package com.freesoft.websearch;

import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.freesoft.websearch.cache.DBFactory;
import com.freesoft.websearch.crawl.WebCrawlController;
import com.freesoft.websearch.threads.SaveErrorUrlThread;
import com.freesoft.websearch.threads.SystemMonitorThread;
import com.freesoft.websearch.threads.WriteContentThread;
import com.freesoft.websearch.utils.ConfigUtils;

/**
 * run search
 * 
 */
public class RunSearch {

	private static Logger log = LoggerFactory.getLogger(RunSearch.class);
	private static WebCrawlController webCrawl;
	private static WriteContentThread contentThread;

	public static void main(String[] args) throws Exception {
		// initial the configuration
		ConfigUtils.loadAllConfig();
		DBFactory.initDB();

		// start the monitor thread
		SystemMonitorThread sysMThread = new SystemMonitorThread();
		sysMThread.start();

		// start the threads to crawl all of web site
		webCrawl = new WebCrawlController();
		webCrawl.startCrawl();
		
		// start the threads to save the file
		contentThread = new WriteContentThread();
		Executors.newSingleThreadScheduledExecutor().execute(contentThread);
		
		SaveErrorUrlThread errorUrlThread = new SaveErrorUrlThread();
		Executors.newSingleThreadScheduledExecutor().execute(errorUrlThread);
	}
}
