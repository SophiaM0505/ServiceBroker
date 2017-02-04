package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import java.net.URISyntaxException;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;
import uk.ac.ucl.chem.ccs.AHECore.Definition.UserRoles;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEDataTransferException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.FileTransferSyntaxException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.PlatformCredentialException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.FileStaging;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHECore.Transfer.AHETransferAPI;
import uk.ac.ucl.chem.ccs.AHEModule.Def.rest_arg;

import com.google.gson.Gson;

/**
 * 
 * POST - Transfer (post) create a new transfer
 * GET - Transfer/{id} (get) get status of transfer TODO? (post) restart for failed or repeat file transfer, repeat will automatically create a new file transfer object id
 * 
 * This resources uses the variable "transfer" to specify what are to be transfered. The format of transfer uri is the
 * same as teh stagein, stageout which uses the JSON array [src,target] or [[src_list],[target_list]]
 * 
 * i.e POST -data "transfer=[[file://home/file.txt][gsiftp://outside.org/w]]
 * 
 * 
 * @author davidc
 *
 */

public class TransferResource extends AHEResource{

	String tran_id;
//	String cmd;
	String argument;
	
    @Override
    public void doInit() {
    	this.tran_id = (String) getRequestAttributes().get("tran_id");
//    	this.cmd = (String) getRequestAttributes().get("cmd");
    	this.argument = (String) getRequestAttributes().get("arg");
    }
    
    /**
     * Start a transfer
     * @param entity
     * @return
     */
    
	@Post
	public String postResource(Representation entity) {
		
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/POST /transfer");
    	}
    	
//    	if(!checkAdminAuthorization(user)){
//    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
//    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage(cmd, user, "No Authorization to access this resource");
//    	}
		
		return post_FORM_Parser(entity);
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	
	private String post_FORM_Parser(Representation entity) {

    	if(entity == null){
    		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    		return ResourceUtil.ThrowErrorMessage("","No Data found", TransferResource.class.toString());
    	}
		
    	if(tran_id == null){

		   return initTransfer(getUser(),ResourceUtil.getArgumentMap(new Form(entity)));

    	}

		return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "",
		"POST Method not supported",
		TransferResource.class.toString());
    	
	}
	
	
	private String initTransfer(AHEUser owner, RestArgMap argmap){
		
		String transfer = argmap.get(rest_arg.transfer.toString());
		
		if(transfer == null){
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "",
			"HTTP POST: No Valid Argument found",
			TransferResource.class.toString());
		}
		
	
		try {

			String[][] transfer_cmd = parseTransfer(transfer);
			FileStaging[] transfers = AHERuntime.getAHERuntime().getAhe_engine().createFileTransfer(owner,transfer_cmd);

			for(int i=0; i < transfers.length; i++){
				
				try{
				
					AHETransferAPI.transfer(transfers[i]);

				} catch (AHEDataTransferException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					transfers[i].setStatus(-1);
					HibernateUtil.SaveOrUpdate(transfers[i]);
					
					return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "",
					"Data transfer failed : " + e,
					TransferResource.class.toString());
				} catch (PlatformCredentialException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					transfers[i].setStatus(-1);
					HibernateUtil.SaveOrUpdate(transfers[i]);
					return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "",
					"Platform Credential error : " + e,
					TransferResource.class.toString());
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					transfers[i].setStatus(-1);
					HibernateUtil.SaveOrUpdate(transfers[i]);
					return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "",
					"Invalid URI : " + e,
					TransferResource.class.toString());
				}

			}
			
			return XMLServerMessageAPI.createFileTransferMessage(null,transfers);
			
		} catch (FileTransferSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "",
			"Invalid Transfer syntax. Must be valid URI using JSON array [[src_uri][dest_uri]]",
			TransferResource.class.toString());
		} catch (AHEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST, "",
			"AHE Exception : " + e,
			TransferResource.class.toString());
		}

	}
	
	/**
	 * Check a transfer
	 * @return
	 */
	
    @Get
    public String getResource() {
    	
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/GET /transfer");
    	}
    	
    	if(tran_id == null){
    		
    		if(getUser().getRole().equalsIgnoreCase(UserRoles.admin.toString()))
    			return list_all_transfer();
    		else
    			return list_transfer(getUser());
		
    	}else{
    		
    		//List Detail for this specific tran_id
			try {
				
				int file_id = Integer.parseInt(tran_id);
				
				if(getUser().getRole().equalsIgnoreCase(UserRoles.admin.toString()))
					return get_transfer_detail_ADMIN(file_id);
				else
					return get_transfer_detail(getUser(),file_id);
		    	
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				 return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,
				 "Transfer id must be a valid integer",
				 e.toString(),
				 TransferResource.class.toString());
			}
    		
    		
    	}


    }
    
    private String list_transfer(AHEUser owner){
    	
    	FileStaging[] x = AHETransferAPI.getFileTransfer(owner);
    	return XMLServerMessageAPI.createFileTransferMessage(null,x);
    	
    }
    
    private String list_all_transfer(){
    	
    	FileStaging[] x = AHETransferAPI.getAllFileTransfers();
    	return XMLServerMessageAPI.createFileTransferMessage(null,x);
    	
    }
    
    private String get_transfer_detail(AHEUser owner, int id){
    	
    	FileStaging x = AHETransferAPI.getFileTransfer(owner,id);
    	return XMLServerMessageAPI.createFileTransferMessage(null,x);
    	
    }
    
    private String get_transfer_detail_ADMIN(int id){
    	
    	FileStaging x = AHETransferAPI.getFileTransfer_admin(id);
    	return XMLServerMessageAPI.createFileTransferMessage(null,x);
    	
    }
    
    private String get_Parser(){
    	return ThrowErrorWithHTTPCode(Status.CLIENT_ERROR_BAD_REQUEST,"","HTTP GET: No Valid Command Argument found", "CredentialResource.java");
    }
    
    private String[][] parseTransfer(String transfer_syntax) throws FileTransferSyntaxException{
    	
    	boolean single = true;
    	
    	if(transfer_syntax.replaceAll("[^\\[]", "").length() > 1){
    		single = false;
    	}

    	if(single){
    		
    		String[] array = new Gson().fromJson(transfer_syntax, String[].class);

    		return new String[][]{{array[0]},{array[1]}};
    		
    	}else{
    		
    		String[][] array = new Gson().fromJson(transfer_syntax, String[][].class);
    		
    		if(array.length == 2){
    			return array;
    		}else{
    			//throw error
    			throw new FileTransferSyntaxException("Data Staging Format error");
    		}
    		
    	}
    	
    }

}