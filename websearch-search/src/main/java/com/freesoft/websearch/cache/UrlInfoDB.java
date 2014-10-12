/**
 * 
 */
package com.freesoft.websearch.cache;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;

/**
 * @author chen
 *
 */
public class UrlInfoDB extends AbstractDB {
	protected Database urlsDB = null;
	protected Environment env;

	protected boolean resumable;
	private static final String dbName = "UrlInfo";
	/**
	 * @param env
	 * @param dbName
	 * @param resumable
	 * @throws DatabaseException
	 */
	public UrlInfoDB(Environment env) throws DatabaseException {
		super(env, dbName, true);
	}
	
}
