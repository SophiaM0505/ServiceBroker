package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import java.util.ArrayList;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

import uk.ac.ucl.chem.ccs.AHECore.API.AHE_API;
import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AppRegisteryAPI;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHESecurityException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AppAlreadyExistException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppRegistery;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppRegisteryArgTemplate;
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;
import uk.ac.ucl.chem.ccs.AHEModule.Def.rest_arg;

import com.google.gson.Gson;


/**
 * 
 * handles /app URI 
 * 
 * GET - list all apps (user can only see apps that they have access to. Admins have unlimited access)
 * POST - create new app
 * DELETE - delete app
 * 
 * @author davidc
 *
 */

public class AppRegisteryResource extends AHEResource{

	String appreg;
	String argument;
	
    @Override
    public void doInit() {
    	
    	this.appreg = (String) getRequestAttributes().get("appreg");
    	this.argument = (String) getRequestAttributes().get("arg");

    }


    private String post_FORM_Parser(Representation entity){
    	
    	
    	long app_reg_id = -1;
    	
    	if(appreg == null){
    		return CreateAppCommand(ResourceUtil.getArgumentMap(new Form(entity)));
    	}else{
    		
    		try{
    			app_reg_id = Long.valueOf(appreg);
    		}catch(NumberFormatException e){
    			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","No Valid App Reg found", "AppRegisteryResource.java");
    		}
    	}
    	
    	return EditAppRegistery(ResourceUtil.getArgumentMap(new Form(entity)));

    }
    
    private String delete_FORM_Parser(){

    	
    	long app_reg_id = -1;
    	
    	if(appreg == null){
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","No App Reg Argument found", "AppRegisteryResource.java");
    	}else{
    		
    		try{
    			app_reg_id = Long.valueOf(appreg);
    		}catch(NumberFormatException e){
    			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","No Valid App Reg found", "AppRegisteryResource.java");
    		}
    	}
    	
    	return deleteAppRegistery();
    }


    
    private String get_Parser(){
    	
    	long app_reg_id = -1;

    	if(appreg == null){

        	if(checkAdminAuthorization(getUser())){
        		//If admin, show all apps
        		return getListAppsCommand();
        	}else{
        		//If normal user show only app user can have access to
        		try {
					return getListAppsCommand(getUser().getUsername());
				} catch (AHESecurityException e) {
					e.printStackTrace();
	    			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"",e.getLocalizedMessage(), "");

				}
        	}
    		
    		
    	}else{
    		
    		try{
    			app_reg_id = Long.valueOf(appreg);
    			return getApps(app_reg_id);
    		}catch(NumberFormatException e){
    			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","No Valid App Reg found", "AppRegisteryResource.java");
    		}
    	}

    }
    
    private String getListAppsCommand(){
    	
		AppRegistery[] list = AppRegisteryAPI.getApplicationList();
	
		return XMLServerMessageAPI.createApplicationListMessage(null,list);
    	
    }
    
    private String getListAppsCommand(String user) throws AHESecurityException{
    	
    	AHEUser u = SecurityUserAPI.getUser(user);
    	
		AppRegistery[] list = AppRegisteryAPI.getApplicationList(u);
	
		return XMLServerMessageAPI.createApplicationListMessage(null,list);
    	
    }
    
    private String getApps(long id){
    	
		AppRegistery app = AppRegisteryAPI.getApplication(id);
	
		if(app == null){
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_NOT_FOUND,"", "Application not found for : " + id, "AppRegisteryResource.java");
		}
		
		return XMLServerMessageAPI.createApplicationListMessage(null,new AppRegistery[]{app});
    	
    }
    
    
    private String EditAppRegistery(RestArgMap argmap){
    	
    	String appname = null;
    	String exec = null;
    	String resource = null;
    	String description = null;
    	
    	if(argmap.containsKey(rest_arg.appname.toString()))
    		appname = argmap.get(rest_arg.appname.toString());
		
    	if(argmap.containsKey(rest_arg.exec.toString()))
    		exec = argmap.get(rest_arg.exec.toString());
    	
    	if(argmap.containsKey(rest_arg.resource_name.toString()))
    		resource = argmap.get(rest_arg.resource_name.toString());
    	
    	if(argmap.containsKey(rest_arg.description.toString()))
    		description = argmap.get(rest_arg.description.toString());
    	
    	try {
    		
    		long app_reg_id = Long.valueOf(appreg);
    		
			AppRegisteryAPI.editApplication(app_reg_id, appname, exec, resource,description);
			return XMLServerMessageAPI.createInformationMessage("AppRegistery updated");
		} catch (AppAlreadyExistException e) {
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","App Update failed : " + e.getMessage(), "AppRegisteryResource.java");
		} catch (AHEException e) {
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","App Update failed : " + e.getMessage(), "AppRegisteryResource.java");
		}
    	
    }
    
    private String deleteAppRegistery(){
    	
		
    	try {
    		
    		long app_reg_id = Long.valueOf(appreg);
    		
			AppRegisteryAPI.deleteApplication(app_reg_id);
			
			return XMLServerMessageAPI.createInformationMessage("DELETED");
		} catch (AppAlreadyExistException e) {
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","App Delete failed : " + e.getMessage(), "AppRegisteryResource.java");
		} catch (AHEException e) {
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","App Delete failed : " + e.getMessage(), "AppRegisteryResource.java");
		}
    	
    }

    /**
     * List App
     */
    
    @Get
    public String toString() {
    	
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/GET /App");
    	}
    	    	
        return get_Parser();
    }

    
	@Put
	public String putResource(Representation entity) {

		// Check is User and User Certificate is correct
		if (!AuthenticateUser()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthenticationFailedMessage(
					"/PUT /app");
		}

		if (!checkAdminAuthorization(getUser())) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthorizationFailedMessage(
					"/PUT /app", getUser().getUsername(), "No Authorization to access this resource");
		}

    	
		
		return post_FORM_Parser(entity);
	}

	/**
	 * Delete APP
	 * @return ahe xml message
	 */
	
	@Delete
	public String deleteResource() {
		


		// Check is User and User Certificate is correct
		if (!AuthenticateUser()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthenticationFailedMessage(
					"/DELETE /app");
		}

		if (!checkAdminAuthorization(getUser())) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthorizationFailedMessage(
					"/DELETE /app", getUser().getUsername(), "No Authorization to access this resource");
		}
    	
	
		
		return delete_FORM_Parser();
	}

	/**
	 * create new app
	 * @param entity
	 * @return ahe xml message
	 */
	
	@Post
	public String postResource(Representation entity) {
		
		// Check is User and User Certificate is correct
		if (!AuthenticateUser()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthenticationFailedMessage(
					"/POST /app");
		}

		if (!checkAdminAuthorization(getUser())) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthorizationFailedMessage(
					"/POST /app", getUser().getUsername(), "No Authorization to access this resource");
		}

    	
		
		return post_FORM_Parser(entity);
	}
	
	
	/**
     * 
     * Create App Command can take in argument that a user can use. 
     * 
     * i.e. /user/1232/cmd/CreateApp/arg/appname=blah&exec=4213&uri=blah&platform&=xx&arg=["string 1 do something",blah2,blah3]&otherarg=[fds,fdsf]
     *    
     * 
     * @return
     */
    
    private String CreateAppCommand(RestArgMap argmap){

    	if(argmap.size() > 0){
			
    		//String s = validateCreateAppCommand(argmap);
			
			if(argmap.containsKey(rest_arg.appname.toString()) && argmap.containsKey(rest_arg.resource_name.toString()) && argmap.containsKey(rest_arg.exec.toString()) ){
				
				try{
					
					String info = null;
					
					if(argmap.containsKey(rest_arg.description.toString())){
						info = argmap.get(rest_arg.description.toString());
					}
					
					AppRegistery reg = (AppRegistery) AHE_API.insertApplicationRegistery(argmap.get(rest_arg.appname.toString()), argmap.get(rest_arg.exec.toString()),argmap.get(rest_arg.resource_name.toString()),info);
					
					/**
					 * Store the non-reserved arg values in the template information and let the relevant connectors deal with that information
					 */
					
					String[] keys = argmap.KeySet();
					
					/**
					 * TODO: fix how arguments can be inserted
					 */
					
					if(keys.length > 4){
						
						ArrayList<String[]> value_pair_array = new ArrayList<String[]>();
						
						int count = 0;
						
						for(int i=0; i < keys.length; i++){
							
							if(!keys[i].equalsIgnoreCase(rest_arg.appname.toString()) && !keys[i].equalsIgnoreCase(rest_arg.exec.toString()) && !keys[i].equalsIgnoreCase(rest_arg.resource_name.toString())){
								
								String argname= keys[i];
								String value = argmap.get(keys[i]);
								
								if(value.contains("[")){
									String[] array = new Gson().fromJson(value, String[].class);
									
									for(int j=0; j < array.length; j++){
										value_pair_array.add(new String[]{argname,array[j]});
									}
									
								}else{
									value_pair_array.add(new String[]{argname,value});
								}
								
								
							}
							
						}
						
						String[] result = new String[value_pair_array.size()*2];
						
						for(int i=0; i < value_pair_array.size(); i++){
							
							result[i*2] = value_pair_array.get(i)[0];
							result[i*2+1] = value_pair_array.get(i)[1];
							
						}
						
						AHEEngine.createAppRegisteryArgTemplate(reg.getId(), result);

					}
					
					
					return XMLServerMessageAPI.createApplicationResponseMessage(null,reg);
				
				}catch(AppAlreadyExistException e){
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return ResourceUtil.ThrowErrorMessage("/POST /app","An Application with the name : " + argmap.get(rest_arg.appname.toString()) + " already exists" , "UserCommandResource.java");
				}catch(AHEException e){
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return ResourceUtil.ThrowErrorMessage("/POST /app","An Application with the name : " + e.getMessage() , "UserCommandResource.java");
				}
			}
			
		}
    	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    	return ResourceUtil.ThrowErrorMessage("/POST /app","Create App Commmand Failed, required parameters/arguments not provided (appname,uri,exec,platform)", "UserCommandResource.java");
    	
    }
}
