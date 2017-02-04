package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;
import uk.ac.ucl.chem.ccs.AHECore.Configuration.ConfigFiles;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;

/**
 * Configuration resource.
 * GET - list current configuration of ahe
 * 
 * @author davidc
 *
 */

public class ConfigResource  extends AHEResource{
	
    @Override
    public void doInit() {
    }

	public ConfigResource() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Get configuration details
	 * @return ahe xml message
	 */
	
	@Get
	public String getHandler(){
		
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/POST Config");
    	}
    	
    	if(!checkAdminAuthorization(getUser())){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("/POST Config", getUser().getUsername(), "No Authorization to access this resource");
    	}
		
		return getConfigurationDetails();
	}
	
	@Post
	public String postHandler(){
		
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/POST Config");
    	}
    	
    	if(!checkAdminAuthorization(getUser())){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("/POST Config", getUser().getUsername(), "No Authorization to access this resource");
    	}
		
		return "";
	}
	
	
	private String getConfigurationDetails(){
		
		ConfigFiles configuration = AHERuntime.getAHERuntime().getConfig_map().get(AHERuntime.ahe_config_filename);
		
		String[] keys = configuration.getKeys();
		String[][] pair = new String[keys.length][2];
		
		for(int i=0; i < pair.length; i++){
			
			pair[i][0] = keys[i];
			pair[i][1] = configuration.getPropertyString(keys[i]);
			
		}
		
		return XMLServerMessageAPI.createConfigurationList("/GET /config", pair);
		
	}
	
}
