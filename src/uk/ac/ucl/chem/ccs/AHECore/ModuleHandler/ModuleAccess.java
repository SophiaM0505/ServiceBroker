package uk.ac.ucl.chem.ccs.AHECore.ModuleHandler;

import java.io.File;
import java.util.HashMap;

import test.helper.Executable;
import uk.ac.ucl.chem.ccs.AHECore.Configuration.AHEConfigFile;
import uk.ac.ucl.chem.ccs.AHECore.Configuration.ConfigFiles;
import uk.ac.ucl.chem.ccs.AHECore.Definition.AHEConfigurationProperty;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.NoModuleProviderException;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;

/**
 * Provide a very simple balance loading if more than one module is provided
 * @author davidc
 *
 */

public class ModuleAccess {
	
	public static void main(String[] arg){
		
			Executable.writeout("Module access reached.");
			AHEConfigFile base_config = new AHEConfigFile("config/ahe.properties");
			
			String[] keys = base_config.getKeys(AHEConfigurationProperty.module_jobservice_globus.toString());
			
			for(int i=0; i < keys.length; i++){
				System.out.println(keys[i]);
				System.out.println(base_config.getPropertyString(keys[i]));
			}
		
	}
	
	private static ModuleAccess provider;
	
	HashMap<String, Integer> counter;
	
	private ModuleAccess(){
		counter = new HashMap<String, Integer>();
	}
	
	public static ModuleAccess getModuleProvider(){
		
		if(provider == null)
			provider = new ModuleAccess();
		
		return provider;
		
	}
	
	/**
	 * Get module property
	 * @param module_id
	 * @return
	 * @throws NoModuleProviderException
	 */
	
	public String[] getModuleProperty(String module_id) throws NoModuleProviderException{
		
		String[] providers = AHERuntime.getAHERuntime().getConfig_map().get(AHERuntime.ahe_config_filename).getKeys(module_id);
		
		if(providers == null || providers.length == 0){
			throw new NoModuleProviderException("No Module provider found for : " + module_id);
		}
		
		int count = 0;
		
		if(!counter.containsKey(module_id)){
			counter.put(module_id, count);
		}else{
			
			count = counter.get(module_id);
			count++;
			counter.put(module_id, count);
			
		}
		
		//Get URI
		
		String[] result;
		String uri = AHERuntime.getAHERuntime().getConfig_map().get(AHERuntime.ahe_config_filename).getPropertyString(providers[count%providers.length]);
		
		//Check for Username password
		String[] cred = getModuleCredential(providers[count%providers.length]);
		
		if(cred.length == 0){
			result = new String[]{uri};
		}else{
			result = new String[]{uri,cred[0],cred[1]};
		}
		
		
		return result;
		
	}
	
	/**
	 * Reverse lookup from URI to look for username and password of that module server
	 * @param uri
	 * @return module_id for URI or if not found, an empty string
	 */
	
	public String getFirstMatchModuleIDfromURI(String uri){
				
		ConfigFiles configfile = AHERuntime.getAHERuntime().getConfig_map().get(AHERuntime.ahe_config_filename);
		
		String[] job_providers = configfile.getKeys(AHEConfigurationProperty.module_jobservice.toString());

		for(String id : job_providers){
			
			String search_uri = configfile.getPropertyString(id);
			
			if(uri.endsWith("/")){
				if(!search_uri.endsWith("/")){
					search_uri += "/";
				}
			}
			
			if(search_uri.equalsIgnoreCase(uri)){
				return id;
			}
			
		}
		
		String[] transfer_providers = configfile.getKeys(AHEConfigurationProperty.module_transferservice.toString());
		
		for(String id : transfer_providers){
			
			String search_uri = configfile.getPropertyString(id);
			
			if(uri.endsWith("/")){
				if(!search_uri.endsWith("/")){
					search_uri += "/";
				}
			}
			
			if(search_uri.equalsIgnoreCase(uri)){
				return id;
			}
			
		}
		
		String[] myproxy_providers = configfile.getKeys(AHEConfigurationProperty.module_security_myproxy.toString());

		for(String id : myproxy_providers){
			
			String search_uri = configfile.getPropertyString(id);
			
			if(uri.endsWith("/")){
				if(!search_uri.endsWith("/")){
					search_uri += "/";
				}
			}
			
			if(search_uri.equalsIgnoreCase(uri)){
				return id;
			}
			
		}
		return "";
	}
	
	/**
	 * module.username.module_id
	 * 
	 * 
	 * @param module_config_key
	 * @return String array {username,password}
	 */
	
	public String[] getModuleCredential(String module_config_key){
		
		String[] config_component = module_config_key.split("\\.");
		
		if(config_component.length > 2){
			
			String module_id = "";
			
			for(int i= 2; i < config_component.length; i++){
				
				module_id += config_component[i];
				
				if(i != config_component.length-1){
					module_id += ".";
				}
				
			}
			
			String config_username_path = AHEConfigurationProperty.module_username.toString() +"."+ module_id;
			String config_pwd_path = AHEConfigurationProperty.module_password.toString() +"."+ module_id;
			
			String[] cred = new String[2];
			
			cred[0] = AHERuntime.getAHERuntime().getConfig_map().get(AHERuntime.ahe_config_filename).getPropertyString(config_username_path);
			cred[1] = AHERuntime.getAHERuntime().getConfig_map().get(AHERuntime.ahe_config_filename).getPropertyString(config_pwd_path);

			return cred;
			
		}
		
		return new String[0];
	}
	
	/**
	 * Search Module credential by URI
	 * @param uri
	 * @return
	 */
	
	public String[] searchModuleCredentialByURI(String uri){
		
		String search = getFirstMatchModuleIDfromURI(uri);
		
		if(search.length() == 0)
			return new String[0];
		
		return getModuleCredential(search);
		
	}

}
