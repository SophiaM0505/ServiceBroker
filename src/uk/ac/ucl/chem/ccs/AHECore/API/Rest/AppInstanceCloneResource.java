package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import java.util.UUID;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;
import uk.ac.ucl.chem.ccs.AHECore.Definition.UserRoles;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AppInstanceAlreadyExistsException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHEModule.Def.rest_arg;

/**
 * handles /appinst/{appinst_id}/clone
 * 
 * Creates a copy of {appinst_id} and sets the state to waiting user command
 * 
 * GET - clone appinst
 * POST - clone appinst (can optionally change sim name)
 * 
 * @author davidc
 *
 */

public class AppInstanceCloneResource  extends AHEResource{

	String appinst;
	
    @Override
    public void doInit() {
    	
    	this.appinst = (String) getRequestAttributes().get("appinst");

    }
    
    /**
     * Clone app instance
     * @return
     */
    
    @Get
    public String get_handler() {
    	
		// Check is User and User Certificate is correct
		if (!AuthenticateUser()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthenticationFailedMessage("");
		}

		if (!checkAppInstanceOwnership()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthorizationFailedMessage("", getUser().getUsername(),
					"No Authorization to access this App Instance Resource");
		}
		
    	long app_inst_id = -1;
    	
		try {
			app_inst_id = Long.valueOf(appinst);
		} catch (NumberFormatException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ResourceUtil.ThrowErrorMessage("",
					"No Valid AppInstance Argument found",
					"AppInstanceResource.java");
		}
		
        return appinst_clone(app_inst_id, generateRandomJobName());
    }
    
    /**
     * Clone app instance
     * @param entity
     * @return
     */
    
    @Post
    public String POST_handler(Representation entity) {
    	
		// Check is User and User Certificate is correct
		if (!AuthenticateUser()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthenticationFailedMessage("");
		}

		if (!checkAppInstanceOwnership()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthorizationFailedMessage("", getUser().getUsername(),
					"No Authorization to access this App Instance Resource");
		}
		
    	long app_inst_id = -1;
    	
		try {
			app_inst_id = Long.valueOf(appinst);
		} catch (NumberFormatException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ResourceUtil.ThrowErrorMessage("",
					"No Valid AppInstance Argument found",
					"AppInstanceResource.java");
		}
		
		RestArgMap map = ResourceUtil.getArgumentMap(new Form(entity));
		
		String name = "";
		
		if(map.containsKey(rest_arg.appname.toString())){
			name = map.get(rest_arg.appname.toString());
		}else
			name = generateRandomJobName();
		
        return appinst_clone(app_inst_id, name);
    }
    
    
    private String generateRandomJobName(){

		int count = 10; //safety switch
		
		for(int i=0; i < count; i++){
			
			String test_name = UUID.randomUUID().toString();
			
			AppInstance search = AHEEngine.getAppInstanceEntity(test_name);
			
			if(search == null){
				return test_name;
			}

		}
    	
		return null;
    }
    
    private String appinst_clone(long app_inst_id, String new_name){
    	
		try {
			AppInstance inst = AHEEngine.cloneAppInstance(app_inst_id, new_name);
			return XMLServerMessageAPI.createPrepareResponsMessage(null,inst);
		} catch (AHEException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ResourceUtil.ThrowErrorMessage("",e.toString(), "AppInstanceResource.java");
		} catch (AppInstanceAlreadyExistsException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ResourceUtil.ThrowErrorMessage("",e.toString(), "AppInstanceResource.java");
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

}