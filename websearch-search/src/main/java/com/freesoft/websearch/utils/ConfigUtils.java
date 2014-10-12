package com.freesoft.websearch.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Properties;

/**
 * load the config file and provide the config info
 * 
 * @author chen
 * 
 */
public class ConfigUtils {

	private static final String CONFIG_FILE = "config.properties";
	private static Properties configProp = null;
	
	/**
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static boolean loadAllConfig() throws FileNotFoundException, IOException {
		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		File configFile = new File(path, CONFIG_FILE);
		configProp = new Properties();
		configProp.load(new FileInputStream(configFile));
		return true;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public static String getPropertyValue(String key) {
		if(configProp != null) {
			return configProp.getProperty(key);
		}
		return null;
	}
	
	/**
	 * 
	 * @param key
	 * @param type
	 * @return
	 */
	public static Object getPropertyValue(String key, Class<?> clasz) {
		String valueStr = getPropertyValue(key);
		if(valueStr == null || clasz == null) 
			return valueStr;
		
		if(clasz.isPrimitive()) {
			if(int.class.equals(clasz)) {
				return Integer.parseInt(valueStr);
			} else if(boolean.class.equals(clasz)) {
				return Boolean.parseBoolean(valueStr);
			}
		} else {
			return valueStr;
		}
		System.out.println("class=" + clasz.getName() + ","+clasz.isPrimitive()+", value=" + valueStr);
		return null;
	}
}
