package uk.ac.ucl.chem.ccs.AHECore.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;


/**
 * AHE Configuration contains the API to configure the AHE serve
 * This includes basic configuration that points to the database and AHE server variables as well 
 * as how APPs that could be run.
 * 
 * @author davidc
 *
 */

public class ConfigFiles {
	
	PropertiesConfiguration config;
	String file;
	
	
	public ConfigFiles(String filepath){
		file = filepath;
		init();
	}
	
	/**
	 * initiates the configuration file. If none is found, create a new file and load default data
	 */
	
	protected void init(){
		
		try{
			
			File configfile = new File(file);
			
			if(!configfile.exists()){
				configfile.createNewFile();
				config = new PropertiesConfiguration(configfile);
				config.setAutoSave(true);
				DefaultData();
				return;
			}
			
			config = new PropertiesConfiguration(configfile);
			config.setAutoSave(true);
			
		}catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	protected void DefaultData(){
	}
	
//	public static void test(){
//		
//
//		try {
//			
//			File configfile = new File("ahe.property");
//			
//			if(!configfile.exists()){
//				configfile.createNewFile();
//			}
//			
//			PropertiesConfiguration config = new PropertiesConfiguration(configfile);
//
//			config.setAutoSave(true);
//			config.setProperty("colors.background", "#000000");
//			config.setProperty("colors.background1", "#000000");
//			config.setProperty("colors.background23", "#000000");
//			
//			String test = config.getString("colors.background");
//			System.out.println(test);
//			
//			//config.save();
//			
//		} catch (ConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		
//	}
	
	/**
	 * Sets configuration property
	 * @param name configuration name
	 * @param value configuration value
	 */
	
	public void setProperty(String name, String value){
		config.setProperty(name, value);
		
		try {
			config.save();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Get String property
	 * @param name configuration name
	 * @return string based configuration value
	 */
	
	public String getPropertyString(String name){
		return config.getString(name);
	}
	
	/**
	 * Get int property
	 * @param name configuration name
	 * @return int based configuration value
	 */
	
	public int getPropertyInt(String name){
		return config.getInt(name);
	}
	
	/**
	 * Get All configuration names
	 * @return String array of configuration names
	 */
	
	public String[] getKeys(){
		
		Iterator<String> it = config.getKeys();
		
		List<String> copy = new ArrayList<String>();
		
		while (it.hasNext())
		    copy.add(it.next());
		
		
		return copy.toArray(new String[copy.size()]);
	}
	
	/**
	 * Get configurations name that contains the specified prefixes
	 * @param prefix prefix to be searched for
	 * @return String array of configuration names with the specified prefixes
	 */
	
	public String[] getKeys(String prefix){
		
		Iterator<String> it = config.getKeys(prefix);
		
		List<String> copy = new ArrayList<String>();
		
		while (it.hasNext())
		    copy.add(it.next());
		
		
		return copy.toArray(new String[copy.size()]);
	}
	
}

