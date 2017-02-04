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
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.FileTransferSyntaxException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.FileStaging;
import uk.ac.ucl.chem.ccs.AHEModule.Def.rest_arg;

import com.google.gson.Gson;

/**
 * 
 * handles /appinst/{appinst_id}/transfer/{transfer_id} URI
 * This resource handles the data transfer of an appinst
 * 
 * GET - list out staging information
 * POST - create new data staging
 * DELETE - remove data staging information
 * 
 * @author davidc
 *
 */

public class AppInstanceDataStageResource extends AHEResource{

	String appinst;
	String cmd;
	String argument;
	String transferid;
	
    @Override
    public void doInit() {
    	
    	this.appinst = (String) getRequestAttributes().get("appinst");
    	this.transferid = (String) getRequestAttributes().get("transferid");
    	this.cmd = (String) getRequestAttributes().get("cmd");

    }
    
    /**
     * Delete data staging information
     * @param entity
     * @return
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
	 * Create new data staging
	 * @param entity
	 * @return
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
	 * List data staging 
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
    	long transfer_id = -1;
    	
    	if(appinst == null){
    		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    		return ResourceUtil.ThrowErrorMessage("","No AppInstance Argument found", "DataStageResource.java");
    	}else{
    		
    		try{
    			app_inst_id = Long.valueOf(appinst);
    		}catch(NumberFormatException e){
    			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    			return ResourceUtil.ThrowErrorMessage("","No Valid AppInstance Argument found", "DataStageResource.java");
    		}
    	}
    	
    	if(transferid == null){
    		//Setup transfer
    		return SetDataStagingCommand(app_inst_id, ResourceUtil.getArgumentMap(new Form(entity)));
    	}else{
    		
    		try{
    			transfer_id = Long.valueOf(transferid);
    		}catch(NumberFormatException e){
    			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    			return ResourceUtil.ThrowErrorMessage("","No Valid AppInstance Argument found", "DataStageResource.java");
    		}
    	}
    	
    	return editDataStaging(app_inst_id,transfer_id,ResourceUtil.getArgumentMap(new Form(entity)));
    	
    	
    }
    
    private String SetDataStagingCommand(long app_inst_id, RestArgMap argmap){
    	
    	//Need to clear old Data stage out information
    	
    	
    	FileStaging[] staging = AHEEngine.getFileStageByAppInstId(app_inst_id);
    	
    	for(FileStaging f : staging){
    		f.setActive(0);
    		HibernateUtil.SaveOrUpdate(f);
    	}
    	
		if(argmap.size() > 0){
			
			String stagein, stageout = null;

			try{
							
				/**
				 * stagein and stageout should be in the format of ["src","dest"] or [["src1","src2"],["dest_folder"]]
				 */
				
				String[] stagein_src = new String[0];
				String[] stagein_dest = new String[0];
				String[] stageout_src = new String[0];
				String[] stageout_dest = new String[0];

				if(argmap.containsKey(rest_arg.stagein.toString())){
					stagein = argmap.get(rest_arg.stagein.toString());
					
					String[][] map = parseStagingString(stagein);
					stagein_src = map[0];
					stagein_dest = map[1];
				}
				
				if(argmap.containsKey(rest_arg.stageout.toString())){
					stageout = argmap.get(rest_arg.stageout.toString());

					String[][] map = parseStagingString(stageout);
					stageout_src = map[0];
					stageout_dest = map[1];
					
				}
				
    			AppInstance inst = AHEEngine.getAppInstanceEntity(app_inst_id);
				
    			if(inst == null){
    				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    				return ResourceUtil.ThrowErrorMessage("/POST /appinst/transfer", "Application Instance does not exist for id : " + app_inst_id, "setDataStaging");
    			}

    			AHE_API.DataStaging(app_inst_id, stagein_src, stagein_dest, stageout_src, stageout_dest);

    			return XMLServerMessageAPI.createDataStagedResponseMessage(null,inst);
			
			}catch(AHEException e){
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return ResourceUtil.ThrowErrorMessage("/POST /appinst/transfer", e.toString(), "AppInstanceResource.java");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return ResourceUtil.ThrowErrorMessage("/POST /appinst/transfer", e.toString(), "AppInstanceResource.java");
			}

		}else{
			AppInstance inst;
			try {
				inst = AHEEngine.getAppInstanceEntity(app_inst_id);
				return XMLServerMessageAPI.createDataStagedResponseMessage(null,inst);
			} catch (AHEException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return ResourceUtil.ThrowErrorMessage("/POST /appinst/transfer", e.toString(), "AppInstanceResource.java");
			}
			
		}
		

    }
    
    private String editDataStaging(long app_inst_id, long transfer_id, RestArgMap argmap){
    	
    	//Need to clear old Data stage out information
    	
    	
    	FileStaging[] staging = AHEEngine.getFileStage(app_inst_id, transfer_id);
    	
    	if(staging.length == 0){
    		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ResourceUtil.ThrowErrorMessage("/POST /appinst/transfer", "File Transfer not found for id : " + transfer_id, "AppInstanceResource.java");
    	}
    	
		if(argmap.size() > 0){
		
			try{
				
				AppInstance inst = AHEEngine.getAppInstanceEntity(app_inst_id);
				
				/**
				 * individual Commands are
				 * source=url&target=url&type=in
				 */
				

				String source = argmap.get(rest_arg.source.toString());
				String target = argmap.get(rest_arg.target.toString());
				String type = argmap.get(rest_arg.type.toString());
    			
				if(source != null)
					staging[0].setSource(source);
				
				if(target != null)
					staging[0].setTarget(target);
				
				if(type != null){
					
					if(type.equalsIgnoreCase("in"))
						staging[0].setStage_in(true);
					else
						staging[0].setStage_in(false);
					
				}
    			
				HibernateUtil.SaveOrUpdate(staging[0]);
				
    			return XMLServerMessageAPI.createDataStagedResponseMessage(null,inst);
			
			}catch(AHEException e){
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return ResourceUtil.ThrowErrorMessage("/POST /appinst/transfer", e.toString(), "AppInstanceResource.java");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return ResourceUtil.ThrowErrorMessage("/POST /appinst/transfer", e.toString(), "AppInstanceResource.java");
			}
			
		}
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return ResourceUtil.ThrowErrorMessage("/POST /appinst/transfer", "Invalid command setup", "AppInstanceResource.java");
    }
    
    private String get_Parser(){
   	
    	long app_inst_id = -1;
    	long transfer_id = -1;
    	
    	if(appinst == null){
    		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    		return ResourceUtil.ThrowErrorMessage("","No AppInstance Argument found", "DataStageResource.java");
    	}else{
    		
    		try{
    			app_inst_id = Long.valueOf(appinst);
    		}catch(NumberFormatException e){
    			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    			return ResourceUtil.ThrowErrorMessage("","No Valid AppInstance Argument found", "DataStageResource.java");
    		}
    	}
    	
    	if(transferid == null){
    		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    		return ResourceUtil.ThrowErrorMessage("","No Transferid Argument found", "DataStageResource.java");
    	}else{
    		
    		try{
    			transfer_id = Long.valueOf(transferid);
    		}catch(NumberFormatException e){
    			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    			return ResourceUtil.ThrowErrorMessage("","No Valid AppInstance Argument found", "DataStageResource.java");
    		}
    	}
    	
    	return getDataStagingCommand(app_inst_id,transfer_id);

    }
    
    private String delete_Parser(Representation entity){
    	

    	long app_inst_id = -1;
    	long transfer_id = -1;
    	
    	if(appinst == null){
    		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    		return ResourceUtil.ThrowErrorMessage("","No AppInstance Argument found", "DataStageResource.java");
    	}else{
    		
    		try{
    			app_inst_id = Long.valueOf(appinst);
    		}catch(NumberFormatException e){
    			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    			return ResourceUtil.ThrowErrorMessage("","No Valid AppInstance Argument found", "DataStageResource.java");
    		}
    	}
    	
    	if(transferid == null){
    		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    		return ResourceUtil.ThrowErrorMessage("","No Transferid Argument found", "DataStageResource.java");
    	}else{
    		
    		try{
    			transfer_id = Long.valueOf(transferid);
    		}catch(NumberFormatException e){
    			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    			return ResourceUtil.ThrowErrorMessage("","No Valid AppInstance Argument found", "DataStageResource.java");
    		}
    	}
    	
    	return deleteDataStaging(app_inst_id, transfer_id, null);
    	
    	//setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    	//return ResourceUtil.ThrowErrorMessage("","HTTP DELETE: No Valid Command Argument found", "DataStageResource.java");
    }
    
    private String deleteDataStaging(long app_inst_id, long transferid, RestArgMap argmap){
    	

		AHEEngine.removeFileStaging(app_inst_id, transferid);

		try {
			AppInstance inst = AHEEngine.getAppInstanceEntity(app_inst_id);
			return XMLServerMessageAPI.createDataStagingStatusMesage("DELETE Data Transfer", inst);
		} catch (AHEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ResourceUtil.ThrowErrorMessage("DELETE", "No Valid argument provided", "DataStageResource.java");
	    	
		}

    	
    }
    
    private String getDataStagingCommand(long app_inst_id, long transferid){
    	
		FileStaging[] transfer = AHEEngine.getFileStage(app_inst_id, transferid);
		return XMLServerMessageAPI.getDataTransferList(null,transfer);
    	
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
    
    private String[][] parseStagingString(String stage_str) throws FileTransferSyntaxException{
    	
    	boolean single = true;
    	
    	if(stage_str.replaceAll("[^\\[]", "").length() > 1){
    		single = false;
    	}

    	if(single){
    		
    		String[] array = new Gson().fromJson(stage_str, String[].class);

    		return new String[][]{{array[0]},{array[1]}};
    		
    	}else{
    
    		String[][] array = new Gson().fromJson(stage_str, String[][].class);
    		
    		if(array.length == 2){
    			return array;
    		}else{
    			//throw error
    			throw new FileTransferSyntaxException("Data Staging Format error");
    		}
    		
    	}
    	
    }

}
