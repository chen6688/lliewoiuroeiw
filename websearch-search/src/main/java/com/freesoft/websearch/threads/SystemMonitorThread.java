package com.freesoft.websearch.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemMonitorThread extends Thread {
	
	private static Logger log = LoggerFactory.getLogger(SystemMonitorThread.class);
	
	@Override
	public void run() {
		log.info("start the system monitor thread");
	}
}
