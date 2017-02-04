package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import java.util.ArrayList;
import java.util.Arrays;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;

import uk.ac.ucl.chem.ccs.AHECore.API.AHE_API;
import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;
import uk.ac.ucl.chem.ccs.AHECore.Definition.UserRoles;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Engine.ResourceRegisterAPI;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHEModule.Def.rest_arg;

import com.google.gson.Gson;

/**
 * Hanldes /appinst/{appinst_id}/runtime URI
 * 
 * POST - Starts the appinst_id if it is in the waiting state
 * 
 * @author davidc
 *
 */

public class AppInstanceRuntimeResource extends AHEResource{

	String appinst;
	
    @Override
    public void doInit() {

    	this.appinst = (String) getRequestAttributes().get("appinst");

    }
    
    /**
     * Start app_instance workflow
     * @param entity
     * @return ahe xml message
     */
    
	@Post
	public String postResource(Representation entity) {
		
		// Check is User and User Certificate is correct
		if (!AuthenticateUser()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/Post");
		}

		if (!checkAppInstanceOwnership()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return XMLServerMessageAPI.createUserAuthorizationFailedMessage("/Post", getUser().getUsername(),
					"No Authorization to access this App Instance Resource");
		}
    	
    	long app_inst_id = -1;
    	
		try{
			app_inst_id = Long.valueOf(appinst);
		}catch(NumberFormatException e){
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ResourceUtil.ThrowErrorMessage("","No Valid AppInstance Argument found", "AppInstanceResource.java");
		}
		
    	return post_FORM_StartCommand(app_inst_id, new Form(entity));
		
	}
	
    private String post_FORM_StartCommand(long app_inst_id, Form form){
    	
    	RestArgMap argmap = ResourceUtil.getArgumentMap(form);
    	return StartCommand(app_inst_id, argmap);
    		
    }
    
    private String[] extractJSONSingleArray(String jsonary_str){
    	
		String[] array = new Gson().fromJson(jsonary_str, String[].class);
		return array;
    	
    }
    
    private String StartCommand(final long app_inst_id, RestArgMap argmap){
    	
    	if(argmap.size() > 0){
			
			try{

				AppInstance inst = AHEEngine
						.getAppInstanceEntity(app_inst_id);

				if (inst == null) {
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return ResourceUtil.ThrowErrorMessage("/POST /appinst/runtime",
							"Application Instance does not exist for id : "
									+ app_inst_id, "getMonitorCommand");
				}
				
				
				final ArrayList<String> appinst_arg = new ArrayList<String>();
				
    			if(argmap.containsKey(rest_arg.resource_name.toString())){
    				
    				ArrayList<String> keyset = new ArrayList<String>();
    				keyset.addAll(Arrays.asList(argmap.KeySet()));
    				
    				for(String key : keyset){
    					
    					String value = argmap.get(key);
    					
    					if(value.trim().startsWith("[") && value.endsWith("]")){
    						
        					String[] array = extractJSONSingleArray(value);
        					
        					for(String a : array){
        						appinst_arg.add(key);
        						appinst_arg.add(a);
        					}
    						
    						
    					}else{
    						appinst_arg.add(key);
    						appinst_arg.add(value);
    					}
    					
    				}
    				
    				    				
					final String rn = argmap.get(rest_arg.resource_name.toString());

					Resource r = ResourceRegisterAPI.getResource(rn);

					if (r == null) {
						setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
						return ResourceUtil.ThrowErrorMessage("/POST /appinst/runtime",
								"Invalid/Non-existant resource selected : "
										+ rn, "AppInstanceResource.java");
					}

					AHE_API.start(app_inst_id, rn, appinst_arg.toArray(new String[appinst_arg.size()]));

					//Update Inst Object
					inst = AHEEngine.getAppInstanceEntity(app_inst_id);

					return XMLServerMessageAPI.createStartResponseMessage(null,inst);
			
    				
	    			
    			}else{
    				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    				return ResourceUtil.ThrowErrorMessage("/POST /appinst/runtime","Resource Name not specified in Start Command", "AppInstanceResource.java");
    			}
    			
			}catch(AHEException e){
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return ResourceUtil.ThrowErrorMessage("/POST /appinst/runtime",e.toString(), "AppInstanceResource.java");
			}
			
		}else{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ResourceUtil.ThrowErrorMessage("/POST /appinst/runtime","Resource Name not specified in Start Command", "AppInstanceResource.java");

		}
    	
    }
	
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