package uk.ac.ucl.chem.ccs.AHECore.ModuleHandler;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Engine.ResourceRegisterAPI;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.ModuleHandlerException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.PlatformCredentialException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstanceArg;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.FileStaging;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.AHEMessage;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.Credential;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.Job;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.Transfer;

/**
 * Generate AHEMessage Object which can be converted to JSON using GSON. The JSON objects are used to communicate
 * with AHE module-services.
 * @author davidc
 *
 */

public class AHEMessageUtility {
	
	private static Logger logger = LoggerFactory.getLogger(AHEMessageUtility.class);
	
	/**
	 * Generate a Job submission AHEMessage JSON object
	 * @param appinst appliation instance
	 * @return AHEMessage JSON object
	 * @throws PlatformCredentialException
	 * @throws ModuleHandlerException
	 */
	
	public static AHEMessage generateJobSubmissionMessage(AppInstance appinst) throws PlatformCredentialException, ModuleHandlerException{
		
		AHEMessage job_submission = new AHEMessage();
		
		PlatformCredential credential = SecurityUserAPI.getUserPlatformCredential(appinst.getOwner(), appinst.getAppreg().getResource());
		
		Credential cred = generateCredential(credential);
		Job job = new Job();
		job.setJob_name(appinst.getSimname());
		job_submission.setCredentials(cred);
		
		job.setExecutable(appinst.getAppreg().getExecutable());
		job.setResourcepath(appinst.getAppreg().getResource().getEndpoint_reference());
		
		if(appinst.getAppreg().getResource().getPort() != -1){
			job.setResource_port(appinst.getAppreg().getResource().getPort());
		}
		
		//Populate Properties and Argument
		AppInstanceArg[] entity_array = AHEEngine.getAppInstanceArgument(appinst.getId());
		String[] str_array = new String[entity_array.length];
		
		for(int i=0; i < entity_array.length; i++){
			AppInstanceArg a = entity_array[i];
			String pair = a.getArg() + "=" + a.getValue();
			str_array[i] = pair;
		}
		
		if(str_array.length != 0){
			job.setArg(str_array);
		}
		
		job_submission.setJob(job);
		
		return job_submission;
	}
	
	/**
	 * Generate a credential AHEMEssage JSON object
	 * @param plat_cred platform credential entity
	 * @return credential AHEMessage JSON object
	 * @throws ModuleHandlerException
	 */
	
	public static Credential generateCredential(PlatformCredential plat_cred) throws ModuleHandlerException{
		
		Credential cred = new Credential();
		

		cred.setCertificate_directory(plat_cred.getCertificate_directory());
		cred.setCredential_location(plat_cred.getCredential_location());
		cred.setProxy_location(plat_cred.getProxy_location());
		cred.setUser_key(plat_cred.getUser_key());
		cred.setPassword(plat_cred.getPassword());
		cred.setRegistry_path(plat_cred.getRegistry_path());
		cred.setTruststore_password(plat_cred.getTruststore_password());
		cred.setTruststore_path(plat_cred.getTruststore_path());
		cred.setType(plat_cred.getAuthen_type());
		cred.setUsername(plat_cred.getUsername());
		cred.setCredential_alias(plat_cred.getCredential_alias());
		
		return cred;
		
	}
	
	/**
	 * Generate a job status request message
	 * @param appinst application instance to be polled
	 * @return AHEMessage object
	 * @throws PlatformCredentialException
	 * @throws ModuleHandlerException
	 */
	
	public static AHEMessage generateJobStatusMessage(AppInstance appinst) throws PlatformCredentialException, ModuleHandlerException{
		
		AHEMessage job_check = new AHEMessage();
		
		PlatformCredential credential = SecurityUserAPI.getUserPlatformCredential(appinst.getOwner(), appinst.getAppreg().getResource());

		Credential cred = generateCredential(credential);
		
		job_check.setCredentials(cred);
		
		Job job = new Job();
		
		try {
			URI test = new URI(appinst.getSubmit_job_id());
			String[] seg = test.getPath().split("/");
			job.setJob_id(seg[seg.length-1]);
		} catch (URISyntaxException e) {
			job.setJob_id(appinst.getSubmit_job_id());
		}
		
		
		job_check.setJob(job);
		
		return job_check;
		
	}
	
	/**
	 * Generate a credential proxy AHEMessage object 
	 * @param credential
	 * @return AHEMessage object
	 * @throws ModuleHandlerException
	 */
	
	public static AHEMessage generateMyProxyMessage(PlatformCredential credential) throws ModuleHandlerException{
		
		AHEMessage msg = new AHEMessage();
		Credential cred = generateCredential(credential);
		msg.setCredentials(cred);
		
		return msg;
	}
	
	/**
	 * Each batch of the transfer must use the same credential per msg
	 * @param user
	 * @param transfers
	 * @return
	 * @throws URISyntaxException
	 * @throws PlatformCredentialException
	 * @throws ModuleHandlerException
	 */
	
	public static AHEMessage generateTransferMessage(FileStaging[] transfers) throws URISyntaxException, PlatformCredentialException, ModuleHandlerException{
		
		String[] src = new String[transfers.length];
		String[] dest = new String[transfers.length];
		
		for(int i=0; i < transfers.length; i++){
			
			src[i] = transfers[i].getSource();
			dest[i] = transfers[i].getTarget();
			
		}
		
		
		
		Resource target_resource = null;
		
		
		if(new URI(transfers[0].getSource()).getScheme().equalsIgnoreCase("file")){
			target_resource = ResourceRegisterAPI.getResourceByBestMatch(new URI(transfers[0].getTarget()));
		}else if(new URI(transfers[0].getTarget()).getScheme().equalsIgnoreCase("file")){
			target_resource = ResourceRegisterAPI.getResourceByBestMatch(new URI(transfers[0].getSource()));
		}else{
			target_resource = ResourceRegisterAPI.getResourceByBestMatch(new URI(transfers[0].getSource()));
		}
		
		AHEUser owner;
		
		if(transfers[0].getAppinstance() != null){
			owner = transfers[0].getAppinstance().getOwner();
		}else{
			owner = transfers[0].getUser();
		}
		
		PlatformCredential credential1 = SecurityUserAPI.getUserPlatformCredential(owner, target_resource);
		
		String[][] array = new String[][]{src,dest};
		
		AHEMessage msg = new AHEMessage();
		
		Credential cred = generateCredential(credential1);
		msg.setCredentials(cred);
		
		Transfer transfer = new Transfer();
		transfer.setPath(array);
		
		msg.setTransfer(transfer);
		
		return msg;
	}
	
	/**
	 * request a transfer status JSON message object
	 * @param transfers
	 * @return AHEMessage object
	 * @throws URISyntaxException
	 * @throws PlatformCredentialException
	 * @throws ModuleHandlerException
	 */

	public static AHEMessage generateTransferStatusMessage(FileStaging[] transfers) throws URISyntaxException, PlatformCredentialException, ModuleHandlerException{
		
		Resource target_resource = null;
		
		
		if(new URI(transfers[0].getSource()).getScheme().equalsIgnoreCase("file")){
			target_resource = ResourceRegisterAPI.getResourceByBestMatch(new URI(transfers[0].getTarget()));
		}else if(new URI(transfers[0].getTarget()).getScheme().equalsIgnoreCase("file")){
			target_resource = ResourceRegisterAPI.getResourceByBestMatch(new URI(transfers[0].getSource()));
		}else{
			target_resource = ResourceRegisterAPI.getResourceByBestMatch(new URI(transfers[0].getSource()));
		}

		AHEUser owner;
		
		if(transfers[0].getAppinstance() != null){
			owner = transfers[0].getAppinstance().getOwner();
		}else{
			owner = transfers[0].getUser();
		}
		
		PlatformCredential credential1 = SecurityUserAPI.getUserPlatformCredential(owner, target_resource);
		
		AHEMessage msg = new AHEMessage();
		Transfer transfer = new Transfer();
		
		try {
			URI test = new URI(transfers[0].getIdentifier());
			String[] seg = test.getPath().split("/");
			transfer.setId(seg[seg.length-1]);
		} catch (URISyntaxException e) {
			transfer.setId(transfers[0].getIdentifier());
		}
		
		msg.setTransfer(transfer);
		
		Credential cred = generateCredential(credential1);
		msg.setCredentials(cred);

		
		return msg;
	}
	
	public static boolean hasException(AHEMessage msg){
		
		if(msg == null){
			return true;
		}
		
		if(msg.getException() != null){
			if(msg.getException().length > 0){
				return true;
			}
		}
		
		return false;
	}
	
}
