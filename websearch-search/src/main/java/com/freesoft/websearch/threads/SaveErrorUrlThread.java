/**
 * 
 */
package com.freesoft.websearch.threads;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.freesoft.websearch.cache.DBFactory;
import com.freesoft.websearch.cache.DataCache;

/**
 * @author chen
 * 
 */
public class SaveErrorUrlThread implements Runnable {
	private static Logger log = LoggerFactory.getLogger(SaveErrorUrlThread.class);
	private final static int TIME_OUT = 300;
	private boolean isStop = false;
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		log.info("start the SaveErrorUrlThread.");
		while (!isStop) {
			try {
				String webUrl = DataCache.getErrorUrlQueue().poll(TIME_OUT, TimeUnit.MILLISECONDS);
				DBFactory.getErrorUrlDB().put(webUrl, "");
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	public void stop() {
		isStop = true;
	}
}
