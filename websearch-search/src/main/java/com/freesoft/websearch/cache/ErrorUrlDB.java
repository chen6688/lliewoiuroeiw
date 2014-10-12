/**
 * 
 */
package com.freesoft.websearch.cache;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;

/**
 * @author chen
 *
 */
public class ErrorUrlDB extends AbstractDB{

	/**
	 * @param env
	 * @param dbName
	 * @param resumable
	 * @throws DatabaseException
	 */
	public ErrorUrlDB(Environment env ) throws DatabaseException {
		super(env, "ErrorUrl", true);
	}

	
}
