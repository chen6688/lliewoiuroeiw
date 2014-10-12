/**
 * 
 */
package com.freesoft.websearch.cache;

import java.io.File;

import com.freesoft.websearch.common.Constrant;
import com.freesoft.websearch.utils.ConfigUtils;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

/**
 * @author chen
 * 
 */
public class DBFactory {

	private static UrlInfoDB urlInfoDB;

	private static ErrorUrlDB errorUrlDB;

	public static UrlInfoDB getUrlInfoDB() {
		return urlInfoDB;
	}

	public static ErrorUrlDB getErrorUrlDB() {
		return errorUrlDB;
	}

	public static void initDB() {
		System.setProperty("je.env.isTransactional", "true");
		String resumableCrawling = ConfigUtils.getPropertyValue(Constrant.CRAWL_RESUMABLE);
		boolean resumable = Boolean.parseBoolean(resumableCrawling);
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		envConfig.setTransactional(resumable);
		envConfig.setLocking(resumable);

		File envHome = new File(ConfigUtils.getPropertyValue(Constrant.CRAWL_STORAGE_DIR), "/pageurl");
		Environment env = new Environment(envHome, envConfig);

		urlInfoDB = new UrlInfoDB(env);
		errorUrlDB = new ErrorUrlDB(env);
	}

}
