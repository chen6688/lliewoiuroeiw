/**
 * 
 */
package com.freesoft.websearch.cache;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author chen
 * 
 */
public class DataCache {

	private static volatile ConcurrentSkipListMap<String, byte[]> htmlInputMap = new ConcurrentSkipListMap<String, byte[]>();
	private static volatile Map<String, String> webNameMap = new ConcurrentHashMap<String, String>();
	private static volatile BlockingQueue<String> errorUrlQueue  = new ArrayBlockingQueue<String>(100000);

	public static ConcurrentSkipListMap<String, byte[]> getHtmlInputMap() {
		return htmlInputMap;
	}

	public static void addHtmlInputMap(String key, byte[] byteContent) throws InterruptedException {
		htmlInputMap.put(key, byteContent);
	}

	public static String getWebNameValue(String Key) {
		return webNameMap.remove(Key);
	}

	public static void addWebNameValue(String key, String value) {
		webNameMap.put(key, value);
	}

	public static BlockingQueue<String> getErrorUrlQueue() {
		return errorUrlQueue;
	}

	public static void addErrorUrlQueue(String webUrl) {
		errorUrlQueue.offer(webUrl);
	}

}
