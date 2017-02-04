package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import java.util.Date;
import java.util.UUID;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHEModule.Def.rest_arg;

/**
 * Check or generate a session token for a specific user
 * 
 * GET - Check for session tokens
 * POST - Generate session tokens
 * 
 * @author davidc
 *
 */

public class SessionResource extends AHEResource{

	
    @Override
    public void doInit() {
    }

    /**
     * Check for a session token
     * @return
     */
    
    @Get
    public String getResource(){
    	
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("");
    	}
    	
		//Get AHEUser
    	if(getUser().getSession_token() != null){

    		if(getUser().getToken_expiry_timestamp().compareTo(new Date()) > 0){
    			return XMLServerMessageAPI.createInformationMessage(getUser().getSession_token());
    		}
    		
    	}
    	
		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		return XMLServerMessageAPI.createExceptiondMessage("No Session Token Found");
    	
    }
    
    /**
     * Generate a session token
     * @param entity
     * @return
     */
    
	@Post
	public String postResource(Representation entity) {
		
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("");
    	}

    	RestArgMap map = ResourceUtil.getArgumentMap(new Form(entity));
    	
    	int lifetime = 0;
    	
    	if(map.containsKey(rest_arg.time.toString())){
    		
    		try{
    			lifetime = Integer.valueOf(map.get(rest_arg.time.toString()));
    		}catch(Exception e){
    			lifetime = 24* 60 *60*1000;
    		}
    		
    	}else
    		lifetime = 24* 60 *60*1000;
    	
    	Date valid_till = new Date(new Date().getTime() + lifetime);
    	
    	getUser().setSession_token(UUID.randomUUID().toString());
    	getUser().setToken_expiry_timestamp(valid_till);
    	
    	HibernateUtil.SaveOrUpdate(getUser());
    	
    	return XMLServerMessageAPI.createInformationMessage(getUser().getSession_token()+","+getUser().getRole());
    	
	}


}
