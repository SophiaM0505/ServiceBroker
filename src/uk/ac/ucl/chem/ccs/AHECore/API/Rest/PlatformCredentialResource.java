package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;
import uk.ac.ucl.chem.ccs.AHECore.Definition.UserRoles;
import uk.ac.ucl.chem.ccs.AHECore.Engine.ResourceRegisterAPI;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHESecurityException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;
import uk.ac.ucl.chem.ccs.AHEModule.Def.rest_arg;

/**
 * 
 * hanldes /resource/{res_id}/cred/{cred_id} URI
 * 
 * GET - list credentials mapped to this resource
 * POST - map credential to resource (credential_id={cred_id} must be supplied in message body). 
 * DELETE - remove credential - resource mapping
 * 
 * @author davidc
 *
 */

public class PlatformCredentialResource extends AHEResource{

	String resource_name;
	String cred_name;
	
    @Override
    public void doInit() {
    	this.resource_name = (String) getRequestAttributes().get("resource");
    	this.cred_name = (String) getRequestAttributes().get("cred");

    }
    
    /**
     * Checks if resource is mapped to credential
     * @return ahe xml message
     */
    
    @Get
    public String getHandler(){
    	
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("GET "+ this.getReference().getPath());
    	}
    	
    	Resource r = ResourceRegisterAPI.getResource(resource_name);

    	if(r == null){
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","No resource found", "");
    	}
    	
    	if(checkAdminAuthorization(getUser())){
    		//If admin, show all credentials
        	PlatformCredential[] list = SecurityUserAPI.getResource_PlatformCredentialList(r);
        	return XMLServerMessageAPI.createCredentialListMessage(null,list);
    	}else{
    	
    		//if normal user show only credentials that the user have
    		//TODO
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("GET "+ this.getReference().getPath(), getUser().getUsername(),"");
    	}
    }
    
    /**
     * maps credential to resource
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
    	
    	Resource r = ResourceRegisterAPI.getResource(resource_name);

    	if(r == null){
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","Resource not found for : " + resource_name, "");
    	}
    	
    	
    	//Map Credential id to resource
    	return AddResourcePlatformCredential(ResourceUtil.getArgumentMap(new Form(entity)));
    	
    }
    
    /**
     * removes credential mapping from resource
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
    	
    	Resource r = ResourceRegisterAPI.getResource(resource_name);

    	if(r == null){
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","Resource not found for : " + resource_name, "");
    	}
    
    	if(cred_name == null){
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","Credential information not provided", "");
    		
    	}
    	
    	try {
			
    		PlatformCredential cred = SecurityUserAPI.getPlatformCredential(cred_name);
			
			if(cred == null){
				return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","Credential not found for : " + cred_name, "");
			}
			
			SecurityUserAPI.removeCredentialfromPlatform(r, cred); 
    		return XMLServerMessageAPI.createInformationMessage("Credential ("+ cred_name +") removed from resource (" + resource_name+")");

			
		} catch (AHESecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"DELETE "+ this.getReference().getPath(), "Failed", "");
		}

    }
    
    public String AddResourcePlatformCredential(RestArgMap argmap){
    	
    	if(!argmap.containsKey(rest_arg.credential_id.toString())){
        	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"/POST /cred", "No valid credential id provided", "");
    	}
    	
    	try {
			
    		String cred_id = argmap.get(rest_arg.credential_id.toString());
    		
			PlatformCredential cred = SecurityUserAPI.getUserCertificateDetail(getUser(), cred_id);
    		
			if(cred == null && !getUser().getRole().equalsIgnoreCase(UserRoles.admin.toString())){
				//Check if cred belong to user
	        	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"/POST /cred", "User access denied to access this credential : " + cred_id, "");
			}

			//Map Credential to Resource
			SecurityUserAPI.addCredentialToResource(cred_id, resource_name);
    		return XMLServerMessageAPI.createInformationMessage("Credential mapped to resource : " + resource_name);

		} catch (AHESecurityException e) {
        	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"/POST /cred", "Failed", "");

		}
    	
    }
    

    

}
