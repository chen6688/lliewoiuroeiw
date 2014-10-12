/**
 * 
 */
package com.freesoft.websearch.threads;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.freesoft.websearch.cache.DBFactory;
import com.freesoft.websearch.cache.DataCache;
import com.freesoft.websearch.common.Constrant;
import com.freesoft.websearch.utils.ConfigUtils;

import edu.uci.ics.crawler4j.util.IO;

/**
 * @author chen
 * 
 */
public class WriteContentThread implements Runnable {
	private static Logger log = LoggerFactory.getLogger(WriteContentThread.class);
	private final static int TIME_OUT = 300;
	private boolean isStop = false;

	public WriteContentThread() {
		
	}
	/**
	 * 
	 */
	public void run() {
		log.info("start the WriteContentThread.");
		ConcurrentSkipListMap<String, byte[]> htmlInputMap = DataCache.getHtmlInputMap();
		String savePath = ConfigUtils.getPropertyValue(Constrant.CRAWL_STORAGE_DIR);
		while (!isStop) {

			Map.Entry<String, byte[]> htmlEntry = htmlInputMap.pollFirstEntry();
			if (htmlEntry != null) {
				String filename = htmlEntry.getKey();
				String pageUrl = DataCache.getWebNameValue(filename);
				
//				DBFactory.getUrlInfoDB().put(filename, pageUrl);
//				DBFactory.getUrlInfoDB().put(pageUrl, filename);
				
				IO.writeBytesToFile(htmlEntry.getValue(), savePath +"\\pages\\" + filename);
			} else {
				try {
					Thread.sleep(TIME_OUT);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		log.info("stop the WriteContentThread.");
	}

	public void stop() {
		isStop = true;
	}
}
