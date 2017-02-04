package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import java.util.HashMap;

/**
 * HashMap<String,String> all keys are treated as case insensitive. 
 * @author davidc
 *
 */

public class RestArgMap {

	HashMap<String, String> arg_map;
	
	public RestArgMap(){
		arg_map = new HashMap<String, String>();
	}
	
	public int size(){
		return arg_map.size();
	}
	
	public String[] KeySet(){
		return arg_map.keySet().toArray(new String[arg_map.size()]);
	}
	
	public boolean containsKey(String key){
		return arg_map.containsKey(key.toLowerCase());
	}
	
	public String get(String key){
		return arg_map.get(key.toLowerCase());
	}
	
	public String put(String key, String value){
		return arg_map.put(key.toLowerCase(), value);
	}
	
	public void clear(){
		arg_map.clear();
	}
	
	public void dispose(){
		arg_map.clear();
	}
	
}
