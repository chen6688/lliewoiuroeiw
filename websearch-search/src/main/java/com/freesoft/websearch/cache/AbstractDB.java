/**
 * 
 */
package com.freesoft.websearch.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

/**
 * @author chen
 * 
 */
public abstract class AbstractDB {

	protected Database urlsDB = null;
	protected Environment env;

	protected boolean resumable;

	public AbstractDB(Environment env, String dbName, boolean resumable) throws DatabaseException {
		this.env = env;
		this.resumable = resumable;
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setTransactional(resumable);
		dbConfig.setDeferredWrite(!resumable);
		urlsDB = env.openDatabase(null, dbName, dbConfig);
	}

	/**
	 * 
	 * @param key
	 * @return
	 * @throws DatabaseException
	 */
	public byte[] get(byte[] key) throws DatabaseException {

		OperationStatus result;
		DatabaseEntry deKey = new DatabaseEntry(key);
		DatabaseEntry deValue = null;
		Transaction txn = getTransaction();
		try {
			result = urlsDB.get(txn, deKey, deValue, LockMode.DEFAULT);

		} catch (DatabaseException e) {
			rollback(txn);
			throw e;
		} finally {
			closeDBInfo(null, txn);
		}
		// get the value
		if (OperationStatus.SUCCESS.equals(result) && deValue != null) {
			return deValue.getData();
		}
		return null;
	}

	public Transaction getTransaction() {
		Transaction txn = null;
		if (resumable) {
			txn = env.beginTransaction(null, null);
		}
		return txn;
	}

	/**
	 * 
	 * @param key
	 * @return
	 * @throws DatabaseException
	 */
	public boolean delete(byte[] key) throws DatabaseException {
		OperationStatus result;
		DatabaseEntry deKey = new DatabaseEntry(key);
		Transaction txn = getTransaction();
		try {
			result = urlsDB.delete(txn, deKey);
		} catch (DatabaseException e) {
			rollback(txn);
			throw e;
		} finally {
			closeDBInfo(null, txn);
		}
		return OperationStatus.SUCCESS.equals(result);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @throws DatabaseException
	 */
	public boolean put(byte[] key, byte[] value) throws DatabaseException {
		DatabaseEntry deValue = new DatabaseEntry(key);
		DatabaseEntry deKey = new DatabaseEntry(value);
		Transaction txn = getTransaction();
		OperationStatus result;
		try {
			result = urlsDB.put(txn, deKey, deValue);

		} catch (DatabaseException e) {
			rollback(txn);
			throw e;
		} finally {
			closeDBInfo(null, txn);
		}
		return OperationStatus.SUCCESS.equals(result);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean put(String key, String value) {
		return put(key.getBytes(), value.getBytes());
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean put(String key, Object value) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objStream = null;
		try {
			objStream = new ObjectOutputStream(byteStream);
			objStream.writeObject(value);
		} catch (IOException e) {

		}
		return put(key.getBytes(), byteStream.toByteArray());
	}

	/**
	 * close db info include cursor and transaction
	 * 
	 * @param cursor
	 * @param txn
	 */
	public void closeDBInfo(Cursor cursor, Transaction txn) {
		if (cursor != null) {
			cursor.close();
		}
		if (txn != null) {
			txn.commit();
		}
	}

	/**
	 * 
	 * @param txn
	 */
	public void rollback(Transaction txn) {
		if (txn != null) {
			txn.abort();
			txn = null;
		}
	}

	public long getLength() {
		try {
			return urlsDB.count();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void sync() {
		if (resumable) {
			return;
		}
		if (urlsDB == null) {
			return;
		}
		try {
			urlsDB.sync();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			urlsDB.close();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
}
