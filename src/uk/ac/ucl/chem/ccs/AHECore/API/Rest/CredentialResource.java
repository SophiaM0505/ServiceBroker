package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHESecurityException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Security.AuthenCode;
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;
import uk.ac.ucl.chem.ccs.AHEModule.Def.rest_arg;


/**
 * 
 * Hanldes /cred URI
 * 
 * GET - list all credentials (users can only see credentials that they have access to. Admin can see all credentials)
 * POST - create new credential
 * DELETE - delete specific credential
 * 
 * @author davidc
 *
 */

public class CredentialResource extends AHEResource{

	String cred_id;
	String argument;
	
    @Override
    public void doInit() {
    	this.cred_id = (String) getRequestAttributes().get("cred");
    	this.argument = (String) getRequestAttributes().get("arg");
    }
	
    private String get_Parser(){
    	
    	if(cred_id == null){
    		return getListCredentialsCommand();
    	}

    	try {
    					
        	if(!checkAdminAuthorization(getUser())){
        		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
        		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("", getUser().getUsername(), "No Authorization to access this resource");
        	}
    		
    		//Get Credential info for this particular credential
    		return getCredential();
			
    	
		} catch (AHESecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"",e.getMessage(), "CredentialResource.java");
		}
    	
    	//return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","HTTP GET: No Valid Command Argument found", "CredentialResource.java");
    }
    
    private String getListCredentialsCommand(){
    	
    	
    	if(checkAdminAuthorization(getUser())){
    		PlatformCredential[] list = SecurityUserAPI.getPlatformCredentialList();
    		return XMLServerMessageAPI.createCredentialListMessage(null,list);
    	}else{
    		
    		try{
    			AHEUser u = SecurityUserAPI.getUser(getUser().getUsername());
    			PlatformCredential[] list = u.getCredentials().toArray(new PlatformCredential[u.getCredentials().size()]);
    			return XMLServerMessageAPI.createCredentialListMessage(null,list);
    		}catch(AHESecurityException e){
	    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
	    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_UNAUTHORIZED,"", "User is not authroized to perform this function", "");
    		}
    	}
    }
    
    private String getCredential() throws AHESecurityException{
    	
    	PlatformCredential c = SecurityUserAPI.getPlatformCredential(cred_id);
    	
    	if(c == null){
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_NOT_FOUND,"", "Credential not found for : " + cred_id, "CredentialResource.java");
    	}
    	
    	if(checkAdminAuthorization(getUser()))
    		return XMLServerMessageAPI.createCredentialListMessage(null,new PlatformCredential[]{c});
    	else{
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_UNAUTHORIZED,"", "User is not authroized to perform this function", "CredentialResource.java");

    	}
    }
    
    private String put_FORM_Parser(Representation entity){

    	
    	if(cred_id == null){
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","No valid credential specified", "CredentialResource.java");
    	}
    	
    	try {
    		
			PlatformCredential cred = SecurityUserAPI.getPlatformCredential(cred_id);

			return put_FORM_EditCredential(cred,entity);
    	
		} catch (AHESecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"",e.getMessage(), "CredentialResource.java");
		}
    	
    }
    
    private String post_FORM_Parser(Representation entity){
    	    	
    	if(cred_id == null){
    		return post_FORM_AddCredentialCommand(new Form(entity));
    		//return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","No valid credential specified", "CredentialResource.java");
    	}
    	
    	try {
    		
			PlatformCredential cred = SecurityUserAPI.getPlatformCredential(cred_id);

			return put_FORM_EditCredential(cred,entity);
    	
		} catch (AHESecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"",e.getMessage(), "CredentialResource.java");
		}
    	
//    	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","HTTP POST: No Valid Command Argument found", "CredentialResource.java");
//    	
    }
    
    
    private String delete_Parser(){
    	   	

    	if(cred_id == null){
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","No valid credential specified", "CredentialResource.java");
    	}
    	
    	try {
    		
			PlatformCredential cred = SecurityUserAPI.getPlatformCredential(cred_id);

			return getRemoveCredential(cred);
    	
		} catch (AHESecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"",e.getMessage(), "CredentialResource.java");
		}

    }
//    
//    private String getEditCredential(PlatformCredential cred){
//    	
//    	if(argument != null){
//    	
//    		RestArgMap argmap = ResourceUtil.getArgumentMap(argument);
//    		return EditCredential(cred, argmap);
//    		
//    	}else{
//    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"EditCredential", "No Arguments specified", "CredentialResource.java");	
//    	}
//    	
//    }
    
    private String put_FORM_EditCredential(PlatformCredential cred, Representation entity){
    	
    	
    		RestArgMap argmap = ResourceUtil.getArgumentMap(new Form(entity));
    		return EditCredential(cred, argmap);
    		

    	
    }
    
    private String EditCredential(PlatformCredential cred, RestArgMap argmap){
    	 
		try{
			
			PlatformCredential dup = SecurityUserAPI.getPlatformCredential(argmap.get(rest_arg.credential_id.toString()));
			
			if(dup != null && dup.getId() != cred.getId()){
				return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"EditCredential", "New Credential ID already exists", "CredentialResource.java");
			}
			
			String new_cred_id = "";
			String cred_location = "";
			String proxy = "";
			String user_key = "";
			String cert_dir = "";
			String username = "";
			String pwd = "";
			String truststore = "";
			String trustpass = "";
			String alias = "";
			String authen_type = "";
			
			if(argmap.containsKey(rest_arg.credential_id.toString())){
				new_cred_id = argmap.get(rest_arg.credential_id.toString());
			}else{
				new_cred_id = cred.getCredential_location();
			}
			
			if(argmap.containsKey(rest_arg.credential_location.toString())){
				cred_location = argmap.get(rest_arg.credential_location.toString());
			}else{
				cred_location = cred.getCredential_location();
			}
			
			if(argmap.containsKey(rest_arg.proxy_location.toString())){
				proxy = argmap.get(rest_arg.proxy_location.toString());
			}else{
				proxy = cred.getProxy_location();
			}
			
			if(argmap.containsKey(rest_arg.user_key.toString())){
				user_key = argmap.get(rest_arg.user_key.toString());
			}else{
				user_key = cred.getUser_key();
			}
			
			if(argmap.containsKey(rest_arg.cert_dir.toString())){
				cert_dir = argmap.get(rest_arg.cert_dir.toString());
			}else{
				cert_dir = cred.getCertificate_directory();
			}
			
			if(argmap.containsKey(rest_arg.username.toString())){
				username = argmap.get(rest_arg.username.toString());
			}else{
				username = cred.getUsername();
			}
			
			if(argmap.containsKey(rest_arg.pwd.toString())){
				pwd = argmap.get(rest_arg.pwd.toString());
			}else{
				pwd = cred.getPassword();
			}
			
			if(argmap.containsKey(rest_arg.alias.toString())){
				alias = argmap.get(rest_arg.alias.toString());
			}else{
				alias = cred.getCredential_alias();
			}
			
			if(argmap.containsKey(rest_arg.trust_path.toString())){
				truststore = argmap.get(rest_arg.trust_path.toString());
			}else{
				truststore = cred.getTruststore_path();
			}
			
			if(argmap.containsKey(rest_arg.trust_pwd.toString())){
				trustpass = argmap.get(rest_arg.trust_pwd.toString());
			}else{
				trustpass = cred.getTruststore_password();
			}
			
			if(argmap.containsKey(rest_arg.type.toString())){
				authen_type = argmap.get(rest_arg.type.toString());
			}else{
				authen_type = cred.getAuthen_type();
			}
			
			
			
			SecurityUserAPI.editCredentialDetail(cred.getCredential_id(), cred_id, cred_location, proxy, user_key, cert_dir, username, pwd,alias, truststore, trustpass,authen_type);
	    	
			
			return XMLServerMessageAPI.createInformationMessage("Credential information updated");
			
		} catch (AHESecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"/POST /cred", "Edit Credential failed : " + e.getMessage(), "UserCommandResource.java");
		}

    }
    
    private String getRemoveCredential(PlatformCredential cred){
    	
    	try {
			SecurityUserAPI.disablePlatformCredential(cred.getCredential_id());
			return XMLServerMessageAPI.createInformationMessage("Credential Disabled");
		} catch (AHESecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"RemoveCredential", "Remove Credential failed : " + e.getMessage(), "CredentialResource.java");
		}

    }
    
//  @Get
//  public String getResource(){
//  	return "";
//  }
  
	@Put
	public String putResource(Representation entity) {
    	
		//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/PUT /Cred");
    	}
    	
    	if(!checkAdminAuthorization(getUser())){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("/PUT /Cred", getUser().getUsername(), "No Authorization to access this resource");
    	}
    	
    	if(!checkValidResource()){
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "", "No Credential found for : " + cred_id, "CredentialResource.java");
    	}
		
		return put_FORM_Parser(entity);
	}

	/**
	 * Delete credential
	 * @return ahe xml message
	 */
	
	@Delete
	public String deleteResource() {
    	
		//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/DELETE /Cred");
    	}
    	
    	if(!checkAdminAuthorization(getUser())){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("/DELETE /Cred", getUser().getUsername(), "No Authorization to access this resource");
    	}
    	
    	if(!checkValidResource()){
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "", "No Credential found for : " + cred_id, "CredentialResource.java");
    	}
		
		return delete_Parser();
	}
	
	/**
	 * Create credential
	 * @param entity
	 * @return ahe xml message
	 */

	@Post
	public String postResource(Representation entity) {
		
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/POST /Cred");
    	}
    	
    	if(!checkAdminAuthorization(getUser())){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("/POST /Cred", getUser().getUsername(), "No Authorization to access this resource");
    	}
    	
		
		return post_FORM_Parser(entity);
	}

	/**
	 * Get credential list
	 */
	
    @Get
    public String toString() {
    	
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/POST /Cred");
    	}

    	return get_Parser();
    	
    }
    
    private boolean checkValidResource(){
    	
    	try {
			PlatformCredential cred = SecurityUserAPI.getPlatformCredential(cred_id);
			
			if(cred != null)
				return true;
			
		} catch (AHESecurityException e) {
			return false;
		}
    	
    	
    	return false;
    }
    
    private String post_FORM_AddCredentialCommand(Form form){
    	if(checkAdminAuthorization(getUser()))
    		return AddCredentialCommand(ResourceUtil.getArgumentMap(form));
    	else{
    		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    		return ResourceUtil.ThrowErrorMessage("AddCredential", "User is not authroized to perform this function", "");
    	}
    }
    
    private String AddCredentialCommand(RestArgMap argmap){
    	
 		if(argmap.size() > 0){
 			
 			if(argmap.containsKey(rest_arg.credential_id.toString()) ){
 			
 				try {
 				
 					PlatformCredential dup = SecurityUserAPI.getPlatformCredential(argmap.get(rest_arg.credential_id.toString()));
 					
 					if(dup != null){
 						return ResourceUtil.ThrowErrorMessage("AddCredential", "New Credential ID already exists", "UserCommandResource.java");
 					}
 					
 					String proxy = "";
 					String user_key = "";
 					String cert_dir = "";
 					String username = "";
 					String registery = "";
 					String pwd = "";
 					String type = "";
 					String alias = "";
 					String trust_path = "";
 					String trust_pwd = "";
 					String cred_location = "";
 					
 					if(argmap.containsKey(rest_arg.credential_location.toString())){
 						cred_location = argmap.get(rest_arg.credential_location.toString());
 					}
 					
 					if(argmap.containsKey(rest_arg.proxy_location.toString())){
 						proxy = argmap.get(rest_arg.proxy_location.toString());
 					}
 					
 					if(argmap.containsKey(rest_arg.type.toString())){
 						type = argmap.get(rest_arg.type.toString());
 					}else{
 						type = AuthenCode.myproxy.toString();
 					}
 					
 					if(argmap.containsKey(rest_arg.user_key.toString())){
 						user_key = argmap.get(rest_arg.user_key.toString());
 					}
 					
 					if(argmap.containsKey(rest_arg.cert_dir.toString())){
 						cert_dir = argmap.get(rest_arg.cert_dir.toString());
 					}
 					
 					if(argmap.containsKey(rest_arg.username.toString())){
 						username = argmap.get(rest_arg.username.toString());
 					}
 					
 					if(argmap.containsKey(rest_arg.pwd.toString())){
 						pwd = argmap.get(rest_arg.pwd.toString());
 					}
 					
 					if(argmap.containsKey(rest_arg.registery.toString())){
 						registery = argmap.get(rest_arg.registery.toString());
 					}
 					
 					if(argmap.containsKey(rest_arg.alias.toString())){
 						alias = argmap.get(rest_arg.alias.toString());
 					}

 					if(argmap.containsKey(rest_arg.trust_path.toString())){
 						trust_path = argmap.get(rest_arg.trust_path.toString());
 					}
 					
 					if(argmap.containsKey(rest_arg.trust_pwd.toString())){
 						trust_pwd = argmap.get(rest_arg.trust_pwd.toString());
 					}
 					
 					SecurityUserAPI.createCredentialDetail(argmap.get(rest_arg.credential_id.toString()),type,argmap.get(rest_arg.resource_name.toString()), cred_location, proxy, user_key, cert_dir,registery, username, pwd,alias, trust_path, trust_pwd);
 					return XMLServerMessageAPI.createInformationMessage("Credential Created");
 				} catch (AHESecurityException e) {

 					e.printStackTrace();
 					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
 					return ResourceUtil.ThrowErrorMessage("AddCredential", "Add Credential Failed : " + e.getMessage(), "UserCommandResource.java");
 					
 				}
 				
 			}
 		}
 		
 		return ResourceUtil.ThrowErrorMessage("AddCredential", "No Arguments specified", "UserCommandResource.java");
     	
     }
	
}