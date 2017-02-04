package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

import uk.ac.ucl.chem.ccs.AHECore.API.AHE_API;
import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;
import uk.ac.ucl.chem.ccs.AHECore.Engine.ResourceRegisterAPI;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHESecurityException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;
import uk.ac.ucl.chem.ccs.AHEModule.Def.rest_arg;


/**
 * Handles /resource 
 * 
 * GET - List all resources
 * POST - create new resources
 * DELETE - remove resource
 * 
 * @author davidc
 *
 */

public class PlatformResource extends AHEResource{

	String resource_id;
	String cmd;
	String argument;
	
    @Override
    public void doInit() {

    	this.resource_id = (String) getRequestAttributes().get("resource");
    	this.cmd = (String) getRequestAttributes().get("cmd");
    	this.argument = (String) getRequestAttributes().get("arg");
    }
    
    
    private String delete_FORM_Parser(){
    	
    	if(resource_id == null){
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","No valid resource found for : " + resource_id, "PlatformResource.java");
    	}
    	
    	Resource r = ResourceRegisterAPI.getResource(resource_id);
    	
    	if(r == null){

    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","No App Reg Argument found", "AppRegisteryResource.java");
    	}
    	
    	return DeleteResource(resource_id);
    }
    
    private String post_FORM_Parser(Representation entity){
    	
    	if(resource_id == null){
    		return CreateResourceCommand(ResourceUtil.getArgumentMap(new Form(entity)));
    		//return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","No valid resource found for : " + resource_id, "PlatformResource.java");
    	}
    	
    	Resource r = ResourceRegisterAPI.getResource(resource_id);
    	
    	if(r == null){
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","No valid resource found for : " + resource_id, "AppRegisteryResource.java");
    	}
    	
    	return EditResource(ResourceUtil.getArgumentMap(new Form(entity)));

    }
    
    public String DeleteResource(String resource_id){

		try {

			ResourceRegisterAPI.deleteResource(resource_id);

			return XMLServerMessageAPI
					.createInformationMessage("Resource Deleted");

		} catch (AHEException e) {
			e.printStackTrace();
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,
					"/DELETE /resource",
					"Resource delete failed : " + e.getMessage(), "");
		}

    }
    
    /**
     * Deprecate this command
     * move to POST /resource/{resource_id}/credential
     * @param argmap
     * @return
     */
    
    public String AddResourcePlatformCredential(RestArgMap argmap){
    	
    	if(resource_id == null){
        	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"/POST /resource/cred", "No valid resource id provided", "");
    	}
    	
    	Resource res = ResourceRegisterAPI.getResource(resource_id);
    	
    	if(res == null){
        	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"/POST /resource/cred", "No valid resource id provided", "");
    	}
    	
    	if(argmap.containsKey(rest_arg.credential_id.toString())){
        	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"/POST /resource/cred", "No valid credential id provided", "");
    	}
    	
    	try {
			
    		String cred_id = argmap.get(rest_arg.credential_id.toString());
    		
			PlatformCredential cred = SecurityUserAPI.getUserCertificateDetail(getUser(), cred_id);
    		
			if(cred == null){
				//Check if cred belong to user
	        	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"/POST /resource/cred", "User access denied for credential usage", "");
			}

			//Map Credential to Resource
			SecurityUserAPI.addCredentialToResource(cred_id, resource_id);
    		return XMLServerMessageAPI.createInformationMessage("Credential mapped to resource : " + resource_id);

		} catch (AHESecurityException e) {
        	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"/POST /resource/cred", "Failed", "");

		}
    	
    }
    
    public String EditResource(RestArgMap argmap){
    	
    	String name = null;
    	String resource_interface = null;
    	String type = null;
    	String arch = null;
    	int cpucount = -1;
    	int memory = -1;
    	int vmemory = -1;
    	int walltimelimit = -1;
    	int port = -1;
    	String opsys = null;
    	String ip = null;
    	String endpoint_reference = null;
    	String authen_type = null;
    	String description = null;
    	
    	if(argmap.containsKey(rest_arg.name.toString())){
    		name = argmap.get(rest_arg.name.toString());
    	}
    	
    	if(argmap.containsKey(rest_arg.resource_interface.toString()))
    		resource_interface = argmap.get(rest_arg.resource_interface.toString());
    	
    	if(argmap.containsKey(rest_arg.type.toString()))
    		type = argmap.get(rest_arg.type.toString());
    	
    	if(argmap.containsKey(rest_arg.arch.toString()))
    		arch = argmap.get(rest_arg.arch.toString());
    	
    	if(argmap.containsKey(rest_arg.cpucount.toString()))
    		cpucount = Integer.parseInt(argmap.get(rest_arg.cpucount.toString()));

    	if(argmap.containsKey(rest_arg.memory.toString()))
    		memory = Integer.parseInt(argmap.get(rest_arg.memory.toString()));

    	if(argmap.containsKey(rest_arg.vmemory.toString()))
    		vmemory = Integer.parseInt(argmap.get(rest_arg.vmemory.toString()));
    	
    	if(argmap.containsKey(rest_arg.walltimelimit.toString()))
    		walltimelimit = Integer.parseInt(argmap.get(rest_arg.walltimelimit.toString()));
    	
    	if(argmap.containsKey(rest_arg.opsys.toString()))
    		opsys = argmap.get(rest_arg.opsys.toString());
    	
    	if(argmap.containsKey(rest_arg.ip.toString()))
    		ip = argmap.get(rest_arg.ip.toString());
		
    	if(argmap.containsKey(rest_arg.endpoint_reference.toString()))
    		endpoint_reference = argmap.get(rest_arg.endpoint_reference.toString());
				
    	if(argmap.containsKey(rest_arg.port.toString()))
    		port = Integer.parseInt(argmap.get(rest_arg.port.toString()));
    	
    	if(argmap.containsKey(rest_arg.authen_type.toString()))
    		authen_type = argmap.get(rest_arg.authen_type.toString());
    	
    	if(argmap.containsKey(rest_arg.description.toString()))
    		description = argmap.get(rest_arg.description.toString());
    	
    	try {
		
    		ResourceRegisterAPI.updateResource(resource_id, name, endpoint_reference, resource_interface, type, cpucount, arch, memory, vmemory, opsys, ip, walltimelimit,port,authen_type,description);
		
    		return XMLServerMessageAPI.createInformationMessage("Resource Updated");
    		
    	} catch (AHEException e) {
    		e.printStackTrace();
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"EditResource", "Resource update failed : " + e.getMessage(), "");
		} 
    	
    }
    
    
    
	@Put
	public String putResource(Representation entity) {
		
    	
    	if(!AuthenticateUser()){
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage(cmd);
    	}
    	
    	if(!checkAdminAuthorization(getUser()))
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage(cmd, getUser().getUsername(), "No Authorization to access this resource");
    	
		
    	return post_FORM_Parser(entity);
	}

	/**
	 * Remove resource
	 * @return ahe XML message
	 */
	
	@Delete
	public String deleteResource() {
		
    	
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage(cmd);
    	}
    	
    	if(!checkAdminAuthorization(getUser())){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage(cmd, getUser().getUsername(), "No Authorization to access this resource");
    	
    	}
    	if(!checkValidResource()){
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "", "Invalid id provided. No user found : " + resource_id, "PlatformResource.java");
    	}
		
		return delete_FORM_Parser();
	}

	/**
	 * Create new resource
	 * @param entity
	 * @return ahe xml messge
	 */
	
	@Post
	public String postResource(Representation entity) {
		
    	
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage(cmd);
    	}
    	
    	if(!checkAdminAuthorization(getUser())){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage(cmd, getUser().getUsername(), "No Authorization to access this resource");
    	
    	}
		
		
		return post_FORM_Parser(entity);
	}
    
	/**
	 * Get resource list 
	 */

    @Get
    public String toString() {
    	
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage(cmd);
    	}
    	
    	return get_Parser();
    }
    
    private String get_Parser(){
    	
    	if(resource_id == null){
    		
	    	if(checkAdminAuthorization(getUser())){
	    		//If Admin show all resources
	    		return getListResourceCommand();
	    	}else{
	    		//If not admin, show only resources that user have access to
	    		return getListResourceCommand(getUser().getUsername());
	    	}

    	}
    	
    	return getResource();
    	
    }
    
    private String getListResourceCommand(){
    	
    	Resource[] list = ResourceRegisterAPI.getResourceList();

    	return XMLServerMessageAPI.createResourceListMessage(null,list);
    }
    
    private String getListResourceCommand(String user){
    	
		try {
			AHEUser u = SecurityUserAPI.getUser(user);
	    	Resource[] list = ResourceRegisterAPI.getResourceList(u);

	    	return XMLServerMessageAPI.createResourceListMessage(null,list);
		} catch (AHESecurityException e) {
			// TODO Auto-generated catch block
			return XMLServerMessageAPI.createExceptiondMessage("List Resource failed for user, " + user);
		}

    }
    
    private String getResource(){
    	
    	Resource r = ResourceRegisterAPI.getResource(resource_id);
    	
    	if(r==null){
    		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_NOT_FOUND,"", "Platform not found for : " + resource_id, "PlatformResource.java");
    	}
    	
    	return XMLServerMessageAPI.createResourceListMessage(null,new Resource[]{r});
    }
    
    private boolean checkValidResource(){
    	
		Resource r = ResourceRegisterAPI.getResource(resource_id);

		if (r != null)
			return true;

    	
    	
    	return false;
    }
    
    private String CreateResourceCommand(RestArgMap argmap){
    	
       	if(argmap.size() > 0){
			
    		//String s = validateCreateAppCommand(argmap);
			
			if(argmap.containsKey(rest_arg.name.toString()) && argmap.containsKey(rest_arg.resource_interface.toString()) && 
//				argmap.containsKey(rest_arg.type.toString()) && 
//				argmap.containsKey(rest_arg.arch.toString()) && 
//				argmap.containsKey(rest_arg.cpucount.toString()) && 
//				argmap.containsKey(rest_arg.memory.toString()) && 
//				argmap.containsKey(rest_arg.vmemory.toString()) && 
//				argmap.containsKey(rest_arg.walltimelimit.toString()) && 
//				argmap.containsKey(rest_arg.opsys.toString()) &&
				argmap.containsKey(rest_arg.authen_type.toString()) &&
				argmap.containsKey(rest_arg.endpoint_reference.toString())){
				
				
				Resource r = ResourceRegisterAPI.getResource(argmap.get(rest_arg.name.toString()));
				
				if(r != null){
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return ResourceUtil.ThrowErrorMessage("/POST /resource","Resource with name : " + r.getCommonname() + " already exists", "UserCommandResource.java");
				}
				
				int port = -1;
				
				try{
				if(argmap.containsKey(rest_arg.port.toString()))
					port = Integer.parseInt(argmap.get(rest_arg.port.toString()));
				}catch(NumberFormatException e){
					
				}
				
				String ip = "";
				String descrip = "";
				
				if(argmap.containsKey(rest_arg.ip.toString()))
					ip = argmap.get(rest_arg.ip.toString());
				
				if(argmap.containsKey(rest_arg.description.toString()))
					descrip = argmap.get(rest_arg.description.toString());
				
				Resource created = (Resource) AHE_API.insertResourceRegistery(argmap.get(rest_arg.name.toString()), argmap.get(rest_arg.endpoint_reference.toString()), argmap.get(rest_arg.resource_interface.toString()),
						argmap.get(rest_arg.type.toString()), Integer.parseInt(argmap.get(rest_arg.cpucount.toString())), argmap.get(rest_arg.arch.toString()),
						Integer.parseInt(argmap.get(rest_arg.memory.toString())), Integer.parseInt(argmap.get(rest_arg.vmemory.toString())),
						  argmap.get(rest_arg.opsys.toString()), ip,
						  Integer.parseInt(argmap.get(rest_arg.walltimelimit.toString())),port,argmap.get(rest_arg.authen_type.toString()),descrip);
				
				return XMLServerMessageAPI.createResourceResponseMessage(null,created);
				
			}
			
		}   	
       	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    	return ResourceUtil.ThrowErrorMessage("/POST /resource","Create Resource Commmand Failed, required parameters/arguments not provided", "UserCommandResource.java");
    	
    }
    
}