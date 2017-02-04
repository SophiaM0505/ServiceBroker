package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import uk.ac.ucl.chem.ccs.AHECore.API.AHE_API;
import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;
import uk.ac.ucl.chem.ccs.AHECore.Definition.UserRoles;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstanceArg;
import uk.ac.ucl.chem.ccs.AHEModule.Def.rest_arg;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * handles /appinst/{appinst_id}/property/{property_id} URI
 * This resource deals with the properties/arguments of an app instance
 * 
 * GET - list out the properties/arguments of appinst_id 
 * POST - create new properties
 * DELETE - delete properties
 * 
 * 
 * @author davidc
 *
 */

public class AppInstancePropertyResource extends AHEResource{

	String appinst;
	String cmd;
	String argument;
	String propertyid; //Can be String key value (add or delete to this item) or long value (specific entry in the db, only edit and delete)
	
    @Override
    public void doInit() {
    	
    	this.appinst = (String) getRequestAttributes().get("appinst");
    	this.propertyid = (String) getRequestAttributes().get("propertyid");
    	this.cmd = (String) getRequestAttributes().get("cmd");

    }

    /**
     * delete appinst properties
     * @param entity
     * @return ahe xml message
     */

	@Delete
	public String deleteResource(Representation entity) {
		
    		
		// Check is User and User Certificate is correct
		if (!AuthenticateUser()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthenticationFailedMessage(cmd);
		}

		if (!checkAppInstanceOwnership()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthorizationFailedMessage(cmd, getUser().getUsername(),
					"No Authorization to access this App Instance Resource");
		}
		
		return delete_Parser(entity);
	}

	/**
	 * create new appinst properties
	 * @param entity
	 * @return ahe xml message
	 */
	
	@Post
	public String postResource(Representation entity) {
		
		// Check is User and User Certificate is correct
		if (!AuthenticateUser()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthenticationFailedMessage(cmd);
		}

		if (!checkAppInstanceOwnership()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthorizationFailedMessage(cmd, getUser().getUsername(),
					"No Authorization to access this App Instance Resource");
		}
		
		return post_FORM_Parser(entity);
	}
	
    /**
     * List appinst properties
     */
	
    @Get
    public String toString() {
    	
		// Check is User and User Certificate is correct
		if (!AuthenticateUser()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthenticationFailedMessage(cmd);
		}

		if (!checkAppInstanceOwnership()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthorizationFailedMessage(cmd, getUser().getUsername(),
					"No Authorization to access this App Instance Resource");
		}
		
        return get_Parser();
    }
	
    private String post_FORM_Parser(Representation entity){
  	
    	long app_inst_id = -1;
    	
    	if(appinst == null){
    		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    		return ResourceUtil.ThrowErrorMessage("","No AppInstance Argument found", "AppInstancePropertyResource.java");
    	}else{
    		
    		try{
    			app_inst_id = Long.valueOf(appinst);
    		}catch(NumberFormatException e){
    			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    			return ResourceUtil.ThrowErrorMessage("","No Valid AppInstance Argument found", "AppInstancePropertyResource.java");
    		}
    	}
    	
    	
    	try{
    		
    		long property_id = Long.parseLong(propertyid);
    		return editPropertyCommand(app_inst_id, property_id, ResourceUtil.getArgumentMap(new Form(entity)));
    	}catch(NumberFormatException e){
    		return addPropertyCommand(app_inst_id, ResourceUtil.getArgumentMap(new Form(entity)));
    	}
    	
    }
    
    private String get_Parser(){

    	cmd = cmd.trim();
    	
    	long app_inst_id = -1;
    	
    	if(appinst == null){
    		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    		return ResourceUtil.ThrowErrorMessage("","No AppInstance Argument found", "AppInstancePropertyResource.java");
    	}else{
    		
    		try{
    			app_inst_id = Long.valueOf(appinst);
    		}catch(NumberFormatException e){
    			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    			return ResourceUtil.ThrowErrorMessage("","No Valid AppInstance Argument found", "AppInstancePropertyResource.java");
    		}
    	}
    	
    	try{
    		
    		long property_id = Long.parseLong(propertyid);
    		return getGetPropertyCommand(app_inst_id, property_id);
    	}catch(NumberFormatException e){
    		return getGetPropertyCommand(app_inst_id, propertyid);
    	}
    	
    	
    }
    
    private String delete_Parser(Representation entity){

    	
    	long app_inst_id = -1;
    	
    	if(appinst == null){
    		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    		return ResourceUtil.ThrowErrorMessage("","No AppInstance Argument found", "AppInstancePropertyResource.java");
    	}else{
    		
    		try{
    			app_inst_id = Long.valueOf(appinst);
    		}catch(NumberFormatException e){
    			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    			return ResourceUtil.ThrowErrorMessage("","No Valid AppInstance Argument found", "AppInstancePropertyResource.java");
    		}
    	}
    	
    	try{
    		
    		long property_id = Long.parseLong(propertyid);
    		return deletePropertyCommand(app_inst_id, propertyid);
    	}catch(NumberFormatException e){
    		return deletePropertyCommand(app_inst_id, propertyid);
    	}
    	

    }
	
    /**
     * Only admin and user owner is allowed to use it
     * @return
     */
    
    private boolean checkAppInstanceOwnership(){

		try {
			
			
	    	if(getUser() != null){
	    		
	    		if(getUser().getRole().equalsIgnoreCase(UserRoles.admin.toString()))
	    			return true;
	    		else{
	    			
					AppInstance app = AHEEngine.getAppInstanceEntity(Long.valueOf(appinst));
	    								
	    			if(app != null){
	    				
	    				if(app.getOwner().getId() == getUser().getId())
	    					return true;
	    				else{
	    					setStatus(Status.CLIENT_ERROR_FORBIDDEN);
	    					return false;
	    				}
	    			}
	    				
	    			
	    		}
	    		
	    	}
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AHEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
    	return false;
    }
    
    
    private String deletePropertyCommand(long app_inst_id, String key){
    	
    	AHEEngine.disableAppInstanceArg(app_inst_id, key);
    	
		AppInstanceArg[] array = AHE_API.listArguments(app_inst_id);		
		return XMLServerMessageAPI.createPropertyListMessage(null,array);
    }
//    
//    private String deletePropertyCommand(long app_inst_id, long arg_id){
//    	
//    	AHEEngine.disableAppInstanceArg(app_inst_id, arg_id);
//    	
//		AppInstanceArg[] array = AHE_API.listArguments(app_inst_id);		
//		return XMLServerMessageAPI.createPropertyListMessage(null,array);
//    }
    
    private String getGetPropertyCommand(long app_inst_id, String key){
    	
			
		AppInstanceArg[] array = AHE_API.getArgument(app_inst_id, key);
		
		if(array.length == 0){
			
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ResourceUtil.ThrowErrorMessage("GET Property", "Invalid property key. No Property found for " + key,"");
			
		}
		
		return XMLServerMessageAPI.createPropertyListMessage(null,array);

    	
    }
    
    private String getGetPropertyCommand(long app_inst_id, long arg_id){
    	
		
		AppInstanceArg[] array = AHEEngine.getAppInstanceArgument(app_inst_id, arg_id);
		
		if(array.length == 0){
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ResourceUtil.ThrowErrorMessage("GET Property", "Invalid property key. No Property found for " + arg_id,"");
			
		}
		
		return XMLServerMessageAPI.createPropertyListMessage(null,array);
    	
    }
    
    private String addPropertyCommand(long app_inst_id, RestArgMap argmap){
    	
		if(argmap.size() > 0){

			
			try {
				
				String[] keyset = argmap.KeySet();
				
				for(String keys : keyset){
					
					String value = argmap.get(keys);
					
					String[] result;
					
					try{
						
						Gson gs = new Gson();
						result = gs.fromJson(value,String[].class);
						
					}catch(JsonSyntaxException e){
						result = new String[]{value};
					}
					
					String[] to_save = new String[result.length*2];
					
					for(int i=0; i < result.length; i++){
						
						to_save[i*2] = keys;
						to_save[i*2+1] = result[i];
						
					}
					
					AHEEngine.createAppInstanceArg(app_inst_id, to_save);
					
				}
								
				AppInstanceArg[] list = AHEEngine.getAppInstanceArgument(app_inst_id);
				
				return XMLServerMessageAPI.createPropertyListMessage(null,list);

			} catch (AHEException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return ResourceUtil.ThrowErrorMessage("SetPropertyCommand", e.getMessage(),"");
			}
			
		}
		
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return ResourceUtil.ThrowErrorMessage("Edit Property", "No Valid argument provided", "AppInstanceResource.java");
    	
    }
    
    private String editPropertyCommand(long app_inst_id, long arg_id, RestArgMap argmap){
    	
		if(argmap.size() > 0){
	
			AppInstanceArg[] arg = AHEEngine.getAppInstanceArgument(
					app_inst_id, arg_id);

			if (arg.length == 0) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return ResourceUtil.ThrowErrorMessage("Edit Property",
						"No valid property object found for " + arg_id, "");
			}

			String value = argmap.get(rest_arg.value.toString());

			if (value == null) {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return ResourceUtil.ThrowErrorMessage("Edit Property",
						"No value specified " + arg_id, "");
			}

			arg[0].setValue(value);
			HibernateUtil.SaveOrUpdate(arg[0]);

			return XMLServerMessageAPI.createPropertyListMessage(null,arg);
				
		}
		
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return ResourceUtil.ThrowErrorMessage("Edit Property", "No Valid argument provided", "AppInstanceResource.java");
    	
    }
    
}