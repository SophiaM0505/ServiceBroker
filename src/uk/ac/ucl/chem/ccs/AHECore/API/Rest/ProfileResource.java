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
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;
import uk.ac.ucl.chem.ccs.AHEModule.Def.AHE_SECURITY_TYPE;
import uk.ac.ucl.chem.ccs.AHEModule.Def.rest_arg;


/**
 * 
 * This class handles all /profile HTTP requests
 * 
 * @author davidc
 *
 */

public class ProfileResource extends AHEResource{

	String profile_id;
	String argument;
	
    @Override
    public void doInit() {
    	this.profile_id = (String) getRequestAttributes().get("profile");
    	this.argument = (String) getRequestAttributes().get("arg");
    }
	
    private String post_FORM_Parser(Representation entity){
    	
    	Form form = new Form(entity);
    	profile_id = form.getFirstValue("profile");

    	if(profile_id == null){
    		return "profile id null";
    		//return AddUserCommand(ResourceUtil.getArgumentMap(new Form(entity)));
    	}
    	
    	try {
    		
    		//return profile_id;
			AHEUser user = SecurityUserAPI.getUser(profile_id);

			if(user == null){
				//return "No user found for : " + profile_id;
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return ResourceUtil.ThrowErrorMessage("","No user found for : " + profile_id, "ProfileResource.java");
			}
    	
			return "profile ok";
			//return put_FORM_EditUser(user, form);
			//return put_FORM_EditUser(user,new Form(entity));
    	
		} catch (AHESecurityException e) {
			//return "exception!";
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			e.printStackTrace();
			return ResourceUtil.ThrowErrorMessage("",e.getMessage(), "ProfileResource.java");
		}

    }
    
    
    private String DELETE_Parser(){
    	
    	if(profile_id == null){
    		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    		return ResourceUtil.ThrowErrorMessage("","No valid profile specified", "ProfileResource.java");
    	}
    	
    	try {
    		
			AHEUser user = SecurityUserAPI.getUser(profile_id);

			if(user == null){
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return ResourceUtil.ThrowErrorMessage("","No user found for : " + profile_id, "ProfileResource.java");
			}
    	
			return DELETE_RemoveUser(user);
    	
		} catch (AHESecurityException e) {

			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ResourceUtil.ThrowErrorMessage("",e.getMessage(), "ProfileResource.java");
		}

    }
    
    private String get_Parser(){
    	

    	
    	if(profile_id == null){
    		return getListUsersCommand();
    	}
    	
    	try {
    		
			AHEUser user = SecurityUserAPI.getUser(profile_id);

			if(user == null){
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return ResourceUtil.ThrowErrorMessage("","No user found for : " + profile_id, "ProfileResource.java");
			}
    	
			return getUser(user);

    	
		} catch (AHESecurityException e) {

			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ResourceUtil.ThrowErrorMessage("",e.getMessage(), "ProfileResource.java");
		}
    	
    }
    
    
    private String put_FORM_EditUser(AHEUser user, Form form){
    	return EditUser(user, ResourceUtil.getArgumentMap(form));
    }
    
    private String EditUser(AHEUser user, RestArgMap argmap){
    	
		if(argmap.size() > 0){
				
			try {
				
				AHEUser dup = SecurityUserAPI.getUser(argmap.get(rest_arg.username.toString()));
				
				if(dup!= null){
					if(dup.getId() != user.getId()){
						setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
						return ResourceUtil.ThrowErrorMessage("EditUser", "Username already exists", "ProfileResource.java");
					}
				}
				
				
				String username = "";
				String role = "";
				String pwd = "";
				String email = "";
				String ident = "";
				String sec_type = "";
				
				if(argmap.containsKey(rest_arg.username.toString())){
					username = argmap.get(rest_arg.username.toString());
				}else{
					username = user.getUsername();
				}
				
				if(argmap.containsKey(rest_arg.role.toString())){
					role = argmap.get(rest_arg.role.toString());
				}else{
					role = user.getRole();
				}
				
				if(argmap.containsKey(rest_arg.email.toString())){
					email = argmap.get(rest_arg.email.toString());
				}else{
					email = user.getEmail();
				}
				
				if(argmap.containsKey(rest_arg.pwd.toString())){
					pwd = argmap.get(rest_arg.pwd.toString());
				}
				
				if(argmap.containsKey(rest_arg.identifier.toString())){
					ident = argmap.get(rest_arg.identifier.toString());
				}else{
					ident = user.getAlt_identifer();
				}
				
				if(argmap.containsKey(rest_arg.security_type.toString())){
					sec_type = argmap.get(rest_arg.security_type.toString());
				}else{
					sec_type = user.getSecurity_type();
				}
				
				AHE_SECURITY_TYPE type = AHE_SECURITY_TYPE.valueOf(sec_type.toUpperCase());
				
				
				
				if(type == null){
					
					AHE_SECURITY_TYPE[] valid = AHE_SECURITY_TYPE.values();
					
					String v = "";
					
					for(AHE_SECURITY_TYPE s : valid){
						v += s.toString() + " ";
					}
					
					return ResourceUtil.ThrowErrorMessage("EditUser", "Invalid AHE Security Mechanism selected for user : " + type.toString() + ". Valid Type includes : ("+v+")", "ProfileResource.java");
				}
				
				SecurityUserAPI.editUser(user.getUsername(), username, pwd, email, role,ident,type);
				
				return XMLServerMessageAPI.createInformationMessage("User information updated");

				
			} catch (AHESecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return ResourceUtil.ThrowErrorMessage("EditUser", "Edit User failed : " + e.getMessage(), "ProfileResource.java");
			}
				
			
		}
    	
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return ResourceUtil.ThrowErrorMessage("EditUser", "Valid arguments not specified (pwd must be specified)", "ProfileResource.java");
    }
    
    
    private String DELETE_RemoveUser(AHEUser user){
    	return RemoveUser(user);
    }
    
    private String RemoveUser(AHEUser user){
      	
    	try {
			SecurityUserAPI.disableUser(user.getUsername());
			return XMLServerMessageAPI.createInformationMessage("User disabled");
		} catch (AHESecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ResourceUtil.ThrowErrorMessage("/DELETE /profile","Remove User failed : " + e.getMessage(), "ProfileResource.java");
		}

    }
    
    /**
     * Delete a user
     * @return
     */

	@Delete
	public String deleteResource() {
		
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/DELETE /profile");
    	}
    	
    	if(!checkAdminAuthorization(getUser())){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("/DELETE /profile", getUser().getUsername(), "No Authorization to access this resource");
    	}
    	if(!checkValidResource()){
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "", "Invalid user id provided. No user found : " + profile_id, "ProfileResource.java");	  
    	}
    	
		return DELETE_Parser();
	}

	/**
	 * Create a new user
	 * @param entity
	 * @return
	 */
	
	@Post
	public String postResource(Representation entity) {
		
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/POST /profile");
    	}
    	
    	if(!checkAdminAuthorization(getUser())){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("/POST /profile", getUser().getUsername(), "No Authorization to access this resource");
    	}
		
		return post_FORM_Parser(entity);
	}
    
	/**
	 * Return a list of users
	 */
	
    @Get
    public String toString() {
    	
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/GET /profile");
    	}
    	
    	if(profile_id != null){
    		
    		if(!getUser().getUsername().equalsIgnoreCase(profile_id)){
    			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
	    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("/GET /profile", getUser().getUsername(), "No Authorization to access this resource");
    		}
    		
    	}else{
	    	if(!checkAdminAuthorization(getUser())){
	    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
	    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("/GET /profile", getUser().getUsername(), "No Authorization to access this resource");
	    	}
    	}
    	
    	return get_Parser();
    }
    
    private boolean checkValidResource(){
    	
    	try {
    		
			AHEUser user = SecurityUserAPI.getUser(profile_id);
			
			if(user != null)
				return true;
			
		} catch (AHESecurityException e) {
			return false;
		}
    	
    	
    	return false;
    }
    
    private String getListUsersCommand(){
    	
    	AHEUser[] list = SecurityUserAPI.getUserList();
    	return XMLServerMessageAPI.createUserListMessage(null,list);
    	
    }
    
    private String getUser(AHEUser user) throws AHESecurityException{
    	
    	return XMLServerMessageAPI.createUserListMessage(null,new AHEUser[]{user});
    	
    }
    
    private String AddUserCommand(RestArgMap argmap){
    	
 		if(argmap.size() > 0){
 			
 			if(argmap.containsKey(rest_arg.username.toString()) && argmap.containsKey(rest_arg.role.toString()) 
 					&& argmap.containsKey(rest_arg.pwd.toString())
 					&& argmap.containsKey(rest_arg.email.toString()) && argmap.containsKey(rest_arg.security_type.toString())){
 			
 				
 					try {
 						
 						AHEUser dup = SecurityUserAPI.getUser(argmap.get(rest_arg.username.toString()));
 						
 						if(dup!= null){
 							setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
 							return ResourceUtil.ThrowErrorMessage("Adduser", "New Username already exists", "UserCommandResource.java");
 						}
 						
 						String ident = "";
 						
 						if(argmap.containsKey(rest_arg.identifier.toString())){
 							ident = argmap.get(rest_arg.identifier.toString());
 						}
 						
 						String sec_type = "";
 						
 						if(argmap.containsKey(rest_arg.security_type.toString())){
 							sec_type = argmap.get(rest_arg.security_type.toString());
 						}
 						
 						String vo_group = "";
 						
 						if(argmap.containsKey(rest_arg.acd_vo.toString())){
 							vo_group = argmap.get(rest_arg.acd_vo.toString());
 						}
 						
 						AHE_SECURITY_TYPE type = AHE_SECURITY_TYPE.valueOf(sec_type.toUpperCase());
 						
 						if(type == null){
 							
 							AHE_SECURITY_TYPE[] valid = AHE_SECURITY_TYPE.values();
 							
 							String v = "";
 							
 							for(AHE_SECURITY_TYPE s : valid){
 								v += s.toString() + " ";
 							}
 							setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
 							return ResourceUtil.ThrowErrorMessage("AddUser", "Invalid AHE Authentication Mechanism selected for user : " + type.toString() + ". Valid Type includes : ("+v+")", "UserCommandResource.java");
 						}
 						
 						AHEUser user = SecurityUserAPI.createUser(argmap.get(rest_arg.username.toString()), argmap.get(rest_arg.role.toString()), argmap.get(rest_arg.pwd.toString()), argmap.get(rest_arg.email.toString()),ident,vo_group,type);
 						
 						return XMLServerMessageAPI.createInformationMessage("User Created");
 						
 					} catch (AHESecurityException e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 						setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
 						return ResourceUtil.ThrowErrorMessage("AddUser", "Add user failed : " + e.getMessage(), "UserCommandResource.java");
 					}
 			
 			}
 			
 		}
 		
 		return ResourceUtil.ThrowErrorMessage("AddUser", "Valid Arguments are not specified (username,role,pwd,email,security_type)", "UserCommandResource.java");
     	
     }
    
}
