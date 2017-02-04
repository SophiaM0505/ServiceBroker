package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHESecurityException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;
import uk.ac.ucl.chem.ccs.AHEModule.Def.rest_arg;

/**
 * This class handles /profile/{pro_id}/cred/{cred_id}
 * 
 * GET - list credentials mapped to this profile
 * POST - map credential to profile (credential_id={cred_id} must be supplied in message body). 
 * TODO URL based cred_id will be supported later
 * DELETE - remove credential - profile mapping
 * 
 * @author davidc
 *
 */

public class ProfileCredentialResource extends AHEResource{

	String profile_id;
	String credential_id;

    @Override
    public void doInit() {
    	this.profile_id = (String) getRequestAttributes().get("profile");
    	this.credential_id = (String) getRequestAttributes().get("cred");

    }
    
    /**
     * Get user certificate list
     * @return ahe XML message
     */
    
    @Get
    public String getHandler(){
    	
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("GET "+ this.getReference().getPath());
    	}
    	
    	if(!checkAdminAuthorization(getUser())){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("GET "+ this.getReference().getPath(), getUser().getUsername(), "No Authorization to access this resource");
    	}

		try {
			
//			AHEUser user = SecurityUserAPI.getUser(profile_id);
//			
//	    	if(user == null){
//	    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","User not found for : " + profile_id, "");
//	    	}
	    	
	    	PlatformCredential[] credlist = SecurityUserAPI.getUserCertificateList(profile_id);
	    	
	    	return XMLServerMessageAPI.createCredentialListMessage(null,credlist);
			
		} catch (AHESecurityException e) {
			e.printStackTrace();
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","Exception", "");
		}

    }
    
    /**
     * Map credential to user
     * @param entity
     * @return ahe xml message
     */
    
    @Post
    public String postHandler(Representation entity){
    	
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("POST "+ this.getReference().getPath());
    	}
    	
    	if(!checkAdminAuthorization(getUser())){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("POST "+ this.getReference().getPath(), getUser().getUsername(), "No Authorization to access this resource");
    	}
    	
		try {
			
			AHEUser u = SecurityUserAPI.getUser(profile_id);
			
	    	if(u == null){
	    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","User not found for : " + profile_id, "");
	    	}
	    	
	    	if(u.getId() == getUser().getId()){ //remove persistance in same session problem
	    		u = getUser();
	    	}
	    	
	    	return MapCredentialToUser(u, ResourceUtil.getArgumentMap(new Form(entity)));
	    	
		} catch (AHESecurityException e) {
			e.printStackTrace();
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","Exception", "");
		}

    }
    
    /**
     * remove credential from user
     * @return ahe xml message
     */
    
    @Delete
    public String deleteHandler(){
    	
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("DELETE "+ this.getReference().getPath());
    	}
    	
    	if(!checkAdminAuthorization(getUser())){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("DELETE "+ this.getReference().getPath(), getUser().getUsername(), "No Authorization to access this resource");
    	}
    	
		try {
			
			AHEUser u = SecurityUserAPI.getUser(profile_id);
			
	    	if(u == null){
	    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","User not found for : " + profile_id, "");
	    	}
	    	
    		PlatformCredential cred = SecurityUserAPI.getPlatformCredential(credential_id);
			
			if(cred == null){
				return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","Credential not found for : " + credential_id, "");
			}
			
			if(u.getId() == getUser().getId()){ //remove persistance in same session problem
				u = getUser();
			}
			
			SecurityUserAPI.removeCredentialfromUser(u, cred);
    		return XMLServerMessageAPI.createInformationMessage("Credential unmapped from user : " + cred.getCredential_id());
			
		} catch (AHESecurityException e) {
			e.printStackTrace();
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","Exception", "");
		}

    }
    
    public String MapCredentialToUser(AHEUser u, RestArgMap argmap){
    	
    	if(!argmap.containsKey(rest_arg.credential_id.toString())){
        	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"/POST /profile/cred", "No valid credential id provided", "");
    	}
    	
    	try {
			
    		String cred_id = argmap.get(rest_arg.credential_id.toString());
    		
			PlatformCredential cred = SecurityUserAPI.getPlatformCredential(cred_id);
    		
			if(cred == null){
				//Check if cred belong to user
	        	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"/POST /profile/cred", "Credential Not found", "");
			}

			//Map Credential to Resource
			SecurityUserAPI.addCredentialToUser(u,cred);
    		return XMLServerMessageAPI.createInformationMessage("Credential mapped to user : " + cred_id);

		} catch (AHESecurityException e) {
        	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"/POST /profile/cred", "Failed", "");

		}
    	
    }

}
