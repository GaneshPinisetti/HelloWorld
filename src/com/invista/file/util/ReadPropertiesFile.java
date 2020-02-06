package com.invista.file.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadPropertiesFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		readProperties("database.properties");
	}
	
	public static Properties readProperties(String fileName){
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = ReadPropertiesFile.class.getClassLoader().getResourceAsStream(fileName);
			// load a properties file
			prop.load(input);			
			//Set<Object> keys = prop.keySet();			
			/*for(Object key: keys){				
				System.out.println(prop.getProperty(key.toString()));
				
			}*/				
			return prop;

		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
