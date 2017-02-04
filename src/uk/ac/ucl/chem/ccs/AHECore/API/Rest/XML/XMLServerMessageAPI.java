package uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AppRegisteryAPI;
import uk.ac.ucl.chem.ccs.AHECore.Engine.ResourceRegisterAPI;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstanceArg;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppRegistery;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.FileStaging;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHEModule.Def.DateFormat;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.AHEMessageAPI;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.ahe;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.app;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.app_instance;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.credential;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.file;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.property;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.resource;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.transfer;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.user;


/**
 * This class generates the public AHE XML messages. It uses SimpleXML. The XML models are located in the AHE Module package.
 * 
 * @author davidc
 *
 */

public class XMLServerMessageAPI {

	/**
	 * Generate Skeleton AHE response message 
	 * @param cmd command that is related to this message
	 * @param timestamp time stamp of this message
	 * @return ahe message object
	 */
	
	private static ahe createSkeletonAHEResponseMsg(String cmd, Date timestamp){
		
		ahe message = new ahe();
		
		message.setCommand(cmd);
		
		if(timestamp != null)
			message.setTimestamp(DateFormat.formatDate(timestamp));
		else		
			message.setTimestamp(DateFormat.formatDate(new Date()));
		
		return message;
	}
	
	/**
	 * Create Failed authentication message
	 * @param cmd command related to this message
	 * @return ahe XML message
	 */
	
	public static String createUserAuthenticationFailedMessage(String cmd) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());
		message.setException("User Authentication failed");
		
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * Create failed authorization message
	 * @param cmd command related to this message
	 * @param user user that has failed the authorization
	 * @param msg custom message
	 * @return ahe XML message
	 */
	
	public static String createUserAuthorizationFailedMessage(String cmd, String user, String msg) {
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());
		message.setException("User Authentication failed : " + user + ", "+msg);
		
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * Create an ahe information message
	 * @param msg message
	 * @return ahe XML message
	 */
	
	public static String createInformationMessage(String msg) {
		ahe message = createSkeletonAHEResponseMsg(null, new Date());
		message.setInformation(msg);
		
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * Create an ahe exception message
	 * @param msg message
	 * @return ahe XML message
	 */
	
	public static String createExceptiondMessage(String msg) {
		ahe message = createSkeletonAHEResponseMsg(null, new Date());
		message.setException(msg);
		
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * Create an application list message
	 * @param cmd command
	 * @param apps applications
	 * @return ahe XML message
	 */
	
	public static String createApplicationListMessage(String cmd, AppRegistery[] apps) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());

		ArrayList<app> app_list = new ArrayList<app>();
		
		for(int i=0; i < apps.length; i++){
			app_list.add(attachApplicationProperty(apps[i]));			
		}
		
		if(app_list.size() > 0){
			message.setApp_list(app_list);
		}
		
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * create a file list message
	 * @param cmd command
	 * @param file_array file array
	 * @return ahe XML message
	 */
	
	public static String createFileListMessage(String cmd, File[] file_array) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());

		ArrayList<file> file_list = new ArrayList<file>();
		
		for(int i=0; i < file_array.length; i++){
			
			file f = new file();
			f.setFilename(file_array[i].getName());
			
			file_list.add(f);			
		}
		
		if(file_list.size() > 0){
			message.setFile_list(file_list);
		}
		
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * Create a resource list ahe message
	 * @param cmd command
	 * @param res resource array
	 * @return ahe XML message
	 */
	
	public static String createResourceListMessage(String cmd, Resource[] res) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());

		ArrayList<resource> r_list = new ArrayList<resource>();
		
		for(int i=0; i < res.length; i++){
			r_list.add(attachResourceProperty(res[i],false));			
		}
		
		
		if(r_list.size() > 0)
			message.setResource_list(r_list);
		
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * create a user list message
	 * @param cmd command
	 * @param list user list
	 * @return ahe XML message
	 */

	public static String createUserListMessage(String cmd, AHEUser[] list) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());

		ArrayList<user> user_list = new ArrayList<user>();
		
		for(int i=0; i < list.length; i++){
			user_list.add(attachUserProperty(list[i]));			
		}
		
		if(user_list.size() > 0){
			message.setUser_list(user_list);
		}
		
		
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * create a credential list message
	 * @param cmd command 
	 * @param list credential list
	 * @return ahe XML message
	 */
	
	public static String createCredentialListMessage(String cmd, PlatformCredential[] list) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());

		ArrayList<credential> cred_list = new ArrayList<credential>();
		
		for(int i=0; i < list.length; i++){
			cred_list.add(attachPlatformCredentialProperty(list[i]));			
		}
		
		if(cred_list.size() > 0){
			message.setCred_list(cred_list);
		}
		
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * create an application property message
	 * @param cmd command
	 * @param app application registry object
	 * @return ahe XML message
	 */
	
	public static String createApplicationResponseMessage(String cmd, AppRegistery app) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());
		message.setApp(attachApplicationProperty(app));	
		
		return AHEMessageAPI.generateXML(message);
		
	}
	
	/**
	 * create an application instance list message
	 * @param cmd command
	 * @return ahe XML message
	 */
	
	public static String createAppInstanceListMessage(String cmd) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());

		AppInstance[] list = AHEEngine.getAppInstanceList();

		if(list.length > 0 ){
		
			ArrayList<app_instance> appinst_list = new ArrayList<app_instance>();
			
			for(AppInstance inst : list)
				appinst_list.add(attachAppInstance(inst,true,true,true));
		
			if(appinst_list.size() > 0)
				message.setAppinst_list(appinst_list);
			
		}
		
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * create an application instance list message	
	 * @param cmd command
	 * @param owner the owner of these application instances
	 * @return ahe XML message
	 */
	
	public static String createAppInstanceListMessage(String cmd, String owner) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());


		AppInstance[] list = AHEEngine.getAppInstanceList(owner);

		if(list.length > 0 ){
		
			ArrayList<app_instance> appinst_list = new ArrayList<app_instance>();
			
			for(AppInstance inst : list)
				appinst_list.add(attachAppInstance(inst,true,true,true));

			if(appinst_list.size() > 0)
				message.setAppinst_list(appinst_list);
			
		}
		
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * create a resource property message
	 * @param cmd command
	 * @param r resource object
	 * @return ahe XML message
	 */
	
	public static String createResourceResponseMessage(String cmd, Resource r) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());
		message.setRes(attachResourceProperty(r,false));
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * 
	 * Prepares a application container and returns a list of resources
	 * that is capable of running the app with the given minimum specific information provided
	 * 
	 * @param prepared
	 * @return
	 * @ 
	 */
	
	public static String createPrepareResponsMessage(String cmd, AppInstance prepared) {
	
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());
		message.setApp_instance(attachPrepareMessage(prepared));
		
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * create a data staging status message
	 * @param cmd command
	 * @param appinst application instance which contains the data staging information
	 * @return ahe XML message
	 */
	
	public static String createDataStagingStatusMesage(String cmd, AppInstance appinst) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());
		
		message.setApp_instance(attachAppInstance(appinst,true,false,true));
		
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * create a data staging response message
	 * @param cmd command
	 * @param appinst application instance owner
	 * @return ahe XML message
	 */
	
	public static String createDataStagedResponseMessage(String cmd, AppInstance appinst){
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());
		
		message.setApp_instance(attachAppInstance(appinst,true,true,true));
		
		return AHEMessageAPI.generateXML(message);
		
	}
	
	/**
	 * create a file transfer message
	 * @param cmd command
	 * @param transfer file transfer array
	 * @return ahe XML message
	 */
	
	public static String createFileTransferMessage(String cmd, FileStaging[] transfer) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());
		message.setTransfer_list(attachFileTransfer(transfer, false));

		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * Create a single file transfer message
	 * @param cmd command
	 * @param transfer file transfer
	 * @return ahe XML message
	 */
	
	public static String createFileTransferMessage(String cmd, FileStaging transfer) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());
		
		if(transfer != null)
			message.setTransfer_list(attachFileTransfer(new FileStaging[]{transfer}, false));
		else
			message.setInformation("No transfer information found");
		
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * Create a data transfer list message
	 * @param cmd command
	 * @param datastaged file transfer array
	 * @return ahe XML message
	 */
	
	public static String getDataTransferList(String cmd, FileStaging[] datastaged) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());
		message.setTransfer_list(attachFileTransfer(datastaged,true));
		
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * create a start response message
	 * @param cmd command
	 * @param starting application instance to be started
	 * @return ahe XML message
	 */
	
	public static String createStartResponseMessage(String cmd, AppInstance starting) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());
		message.setApp_instance(attachStartMessage( starting));
		
		return AHEMessageAPI.generateXML(message);
	}
	
	/**
	 * create a property list message
	 * @param cmd command
	 * @param array argument array 
	 * @return ahe XML message
	 */
	
	public static String createPropertyListMessage(String cmd, AppInstanceArg[] array) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());
		message.setProperty_list(attachListProperty(array));
		
		return AHEMessageAPI.generateXML(message);
		
	}
	
	/**
	 * Create a configuration property message
	 * @param cmd command
	 * @param array a double string array containing the key-value argument pairs
	 * @return ahe XML message
	 */
	
	public static String createConfigurationList(String cmd, String[][] array) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());
		message.setProperty_list(attachListProperty(array));
		
		return AHEMessageAPI.generateXML(message);
		
	}
	
	/**
	 * create an application status message
	 * @param cmd command
	 * @param monitor application instance to be monitored
	 * @return ahe XML message
	 */
	
	public static String createMonitorResponseMessage(String cmd, AppInstance monitor) {
		
		ahe message = createSkeletonAHEResponseMsg(cmd, new Date());
		message.setApp_instance(attachAppInstance(monitor, true,true,true));

		return AHEMessageAPI.generateXML(message);
	}
	
	private static app attachApplicationProperty(AppRegistery r){
		
		app appreg = new app();
		appreg.setId(Integer.valueOf((int)r.getId()));
		appreg.setName(r.getAppname());
		appreg.setExecutable(r.getExecutable());
		appreg.setResource_id(r.getResource().getCommonname());
		appreg.setUri(r.getResource().getEndpoint_reference());
		
		if(r.getResource().getResource_interface() != null)
			appreg.setResource_interface(r.getResource().getResource_interface());
		
		if(r.getDescription() != null)
			appreg.setDescription(r.getDescription());
		
		return appreg;
		
	}
	
	private static user attachUserProperty(AHEUser user_entity){
		
		user u = new user();
		u.setUsername(user_entity.getUsername());
		u.setEmail(user_entity.getEmail());
		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		u.setCreated(DateFormat.formatDate(user_entity.getTimestamp()));
		
		u.setAuthen_type(user_entity.getSecurity_type());
		u.setRole(user_entity.getRole());
		
		u.setAlt_identifier(user_entity.getAlt_identifer());
		
		Iterator<PlatformCredential> itor = user_entity.getCredentials().iterator();

		if(itor.hasNext()){
		
			ArrayList<credential> cred_list = new ArrayList<credential>();
			
			while(itor.hasNext()){
				
				cred_list.add(attachPlatformCredentialProperty(itor.next()));
				
			}
		
			if(cred_list.size() > 0)
				u.setCred_list(cred_list);
			
		}
		
		return u;
		
	}
	
	private static credential attachPlatformCredentialProperty(PlatformCredential cred_entity){
		
		credential cred = new credential();
		cred.setName(cred_entity.getCredential_id());
		cred.setType(cred_entity.getAuthen_type());
		
		if(cred_entity.getUsername() != null)
			cred.setUsername(cred_entity.getUsername());
		
		if(cred_entity.getCredential_location() != null)
			cred.setCred_location(cred_entity.getCredential_location());
		
		if(cred_entity.getCredential_alias() != null)
			cred.setCred_alias(cred_entity.getCredential_alias());
		
		if(cred_entity.getProxy_location() != null)
			cred.setProxy_location(cred_entity.getProxy_location());
		
		if(cred_entity.getTruststore_path() != null)
			cred.setTrust_location(cred_entity.getTruststore_path());

		if(cred_entity.getUser_key() != null){
			cred.setUser_key(cred_entity.getUser_key());
		}

		if(cred_entity.getCertificate_directory() != null){
			cred.setCertificate_directory(cred_entity.getCertificate_directory());
		}
		
		if(cred_entity.getRegistry_path() != null){
			cred.setRegistry(cred_entity.getRegistry_path());
		}
		
		cred.setCreated(DateFormat.formatDate(cred_entity.getTimestamp()));

		
		Iterator<Resource> it = cred_entity.getResource().iterator();
		
		//Attach Resource used
		
		Set<Resource> resourceset = cred_entity.getResource();
		
		Iterator<Resource> iter = resourceset.iterator();
		
		ArrayList<resource> res_list = new ArrayList<resource>();
		
		while(iter.hasNext()){
			
			Resource res = iter.next();
			res_list.add(attachResourceProperty(res,true));
						
		}
		
		if(res_list.size() > 0)
			cred.setResource_list(res_list);
		
		return cred;
		

	}
	
	private static resource attachResourceProperty(Resource r_entity, boolean reduced_info){
		
		resource res = new resource();
		res.setId(Integer.valueOf((int) r_entity.getId()));
		res.setName(r_entity.getCommonname());
		res.setUri(r_entity.getEndpoint_reference());
		res.setResource_interface(r_entity.getResource_interface());
		
		if(!reduced_info){
		
			if(r_entity.getPort() > -1){
				res.setPort(r_entity.getPort());
			}

			res.setType(r_entity.getType());
			res.setCpu_count(r_entity.getCpucount());
			res.setMemory(r_entity.getMemory());
			res.setVmemory(r_entity.getVirtualmemory());
			res.setWalltimelimit(r_entity.getWalltimelimit());
			res.setArch(r_entity.getArch());
			res.setIp(r_entity.getIp());
			res.setOpsys(r_entity.getOpsys());
		
	
			//Attach Application Information if it exists
			
			AppRegistery[] applist = AppRegisteryAPI.getApplicationList(r_entity.getCommonname());
			
			if(applist.length > 0){
				
				ArrayList<app> app_list = new ArrayList<app>();
				
				for(AppRegistery ar : applist){
					app_list.add(attachApplicationProperty(ar));
				}
				
				res.setApp_list(app_list);
				
			}
		
		}
		
		return res;
		
	}
	
	private static app_instance attachAppInstance(AppInstance ai, boolean datastaging, boolean showAppReg, boolean showArguments){
		
		if(datastaging)
			return attachAppInstance(ai, true, true, showAppReg, showArguments);
		else
			return attachAppInstance(ai, false, false, showAppReg, showArguments);
		
	}
	
	private static ArrayList<transfer> attachFileTransfer(FileStaging[] f_array, boolean attach_staging){
		
		ArrayList<transfer> tran_list = new ArrayList<transfer>();
		
		if(f_array.length > 0){
			
			for(FileStaging f : f_array){
				
				if(attach_staging)
					tran_list.add(attachDataStaging(f));
				else{
					tran_list.add(attachFileTransfer(f));
				}
			}
		
		}
		
		return tran_list;
		
	}
	
	private static app_instance attachAppInstance(AppInstance ai, boolean StageIn, boolean StageOut, boolean showAppReg, boolean showArguments){
		
		app_instance app_inst = new app_instance();
		app_inst.setId(Integer.valueOf((int) ai.getId()));
		app_inst.setStatus(ai.getState());
		app_inst.setName(ai.getSimname());
		
		if(ai.getSubmit_job_id() != null){
			app_inst.setAhe_id(ai.getSubmit_job_id());
		}
		
//		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		app_inst.setTimestamp(DateFormat.formatDate(ai.getTimestamp()));

		
		
		if(StageIn || StageOut){
			
			
			FileStaging[] f_array = AHEEngine.getFileStageByAppInstId(ai.getId());
			
			if(f_array.length > 0){

				ArrayList<transfer> transferlist = new ArrayList<transfer>();
				
				for(FileStaging f : f_array){
					
					if(f.isStage_in() && StageIn)
						transferlist.add(attachDataStaging(f));
					else if(!f.isStage_in() && StageOut)
						transferlist.add(attachDataStaging(f));
				}
			
				app_inst.setTransfer_list(transferlist);
				
			}
		}
				
		if(showAppReg){
			//Create App Registery Information
			app appreg = attachApplicationProperty(ai.getAppreg());
			app_inst.setApp(appreg);
		}
		
		if(showArguments){
			//Attach Arguments
			AppInstanceArg[] arglist = AHEEngine.getAppInstanceArgument(ai.getId());
			
			if(arglist.length > 0){

				ArrayList<property> prop_list = new ArrayList<property>();
				
				
				for(AppInstanceArg arg : arglist){
					
					if(arg.getActive() > 0)
						prop_list.add(attachProperty((int) arg.getId(), arg.getArg(), arg.getValue()));

					
				}
				
				if(prop_list.size() > 0)
					app_inst.setProperty_list(prop_list);
				
			}
		
		}

		return app_inst;
		
	}
	
	private static app_instance attachStartMessage(AppInstance ai){
		
		return attachAppInstance(ai, true, true, true);
		
	}

	
	private static property attachProperty(int id, String name, String value){
		
		property prop = new property();
		prop.setId(id);
		prop.setName(name);
		prop.setValue(value);
		
		return prop;
		
	}
	
	private static ArrayList<property> attachListProperty(AppInstanceArg[] array){
		
		ArrayList<property> prop_list = new ArrayList<property>();
		
		for(int i=0; i < array.length; i++){
			
			property prop = new property();
			prop.setId(Integer.valueOf(((int) array[i].getId())));
			prop.setName(array[i].getArg());
			prop.setValue(array[i].getValue());

			prop_list.add(prop);
		}
		
		return prop_list;
		
	}
	
	private static ArrayList<property> attachListProperty(String[][] array){
		
		ArrayList<property> prop_list = new ArrayList<property>();
		
		for(int i=0; i < array.length; i++){
			
			property prop = new property();
			prop.setName(array[i][0]);
			prop.setValue(array[i][1]);

			prop_list.add(prop);
		}
		
		return prop_list;
		
	}
	
	private static app_instance attachPrepareMessage(AppInstance app_inst){
		
		
		app_instance prepare = attachAppInstance(app_inst, true, false, true);
		
		//Attach Resources With valid requirements
		
		AppInstanceArg[] arglist = AHEEngine.getAppInstanceArgument(app_inst.getId());
		
		int cpucount = -1;
		int memory = -1;
		int vmemory = -1;
		int walltimelimit = -1;
		
		for(AppInstanceArg arg : arglist){
			
			if(arg.getArg().equalsIgnoreCase("cpucount"))
				cpucount = Integer.parseInt(arg.getValue());
			else if(arg.getArg().equalsIgnoreCase("memory"))
				memory = Integer.parseInt(arg.getValue());
			else if(arg.getArg().equalsIgnoreCase("vmemory"))
				vmemory = Integer.parseInt(arg.getValue());
			else if(arg.getArg().equalsIgnoreCase("walltimelimit"))
				walltimelimit = Integer.parseInt(arg.getValue());
			
		}
		
	
		Resource[] searchlist = ResourceRegisterAPI.searchResourceMinimumRequirement("", "", cpucount, "", memory, vmemory, "", walltimelimit);
		
		ArrayList<resource> possible_resource = new ArrayList<resource>();
		
		for(Resource search : searchlist){
			
			AppRegistery r = AppRegisteryAPI.getApplication(app_inst.getAppreg().getAppname(), search.getCommonname());
			
			if(r != null){
				
				resource res = new resource();
				res.setName(search.getCommonname());
				res.setApp_name(app_inst.getAppreg().getAppname());
				res.setCpu_count(search.getCpucount());
				res.setMemory(search.getMemory());
				res.setVmemory(search.getVirtualmemory());
				res.setWalltimelimit(search.getWalltimelimit());
				
				possible_resource.add(res);
			}
			
		}
		
		if(possible_resource.size() > 0)
			prepare.setResource_list(possible_resource);
		
		return prepare;

		
	}
	
	private static transfer attachFileTransfer(FileStaging f){
		
		transfer transfer = new transfer();
		
		transfer.setId(Integer.valueOf((int) f.getId()));
		transfer.setAhe_id(f.getIdentifier());
		transfer.setSource(f.getSource());
		transfer.setDest(f.getTarget());
		
		if(f.getStatus() == 0)
			transfer.setStatus("pending");
		else if(f.getStatus() == 1)
			transfer.setStatus("completed");
		else
			transfer.setStatus("failed");
		
		return transfer;
	}
	
	private static transfer attachDataStaging(FileStaging f){
		
		transfer transfer = new transfer();
		transfer.setStage_dir(f.isStage_in() ? "in" : "out");

		if(f.getStatus() == 0)
			transfer.setStatus("pending");
		else if(f.getStatus() == 1)
			transfer.setStatus("completed");
		else
			transfer.setStatus("failed");
		
		transfer.setId(Integer.valueOf((int) f.getId()));
		transfer.setAhe_id(f.getIdentifier());
		transfer.setSource(f.getSource());
		transfer.setDest(f.getTarget());
		
		return transfer;
	}
	
//	private void attachCommand(Element parent, String cmd){
//		
//		Element elem = getDoc().createElement(AHE_XML_Element.command.toString());
//		elem.setTextContent(cmd);
//		
//		parent.appendChild(elem);
//		
//	}
//	
//	private void attachInformation(Element parent, String message){
//		
//		Element elem = getDoc().createElement(AHE_XML_Element.information.toString());
//		elem.setTextContent(message);
//		
//		parent.appendChild(elem);
//		
//	}
//	
//	private void attachWarning(Element parent, String warning){
//		
//		Element elem = getDoc().createElement(AHE_XML_Element.warning.toString());
//		elem.setTextContent(warning);
//		
//		parent.appendChild(elem);
//		
//	}
//	
//	private void attachException(Element parent, String exce){
//		
//		Element elem = getDoc().createElement(AHE_XML_Element.exception.toString());
//		elem.setTextContent(exce);
//		
//		parent.appendChild(elem);
//		
//	}
	
//	private void attachTimeStamp(Element parent, Date date){
//		
//		Element elem = getDoc().createElement(AHE_XML_Element.timestamp.toString());
//		elem.setTextContent(date.toGMTString());
//		
//		parent.appendChild(elem);
//		
//	}
	
	
//	private void createDocument(){
//		
//		try{
//
//			this.dbf = DocumentBuilderFactory.newInstance();
//			dbf.setNamespaceAware(true);
//			this.db = dbf.newDocumentBuilder();
//			//db.setErrorHandler(new ValidationErrorHandler());
//			setDoc(db.newDocument());
//			
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//		
//	}
	
	
//	private Document parse(Reader document) throws SAXException, IOException {    
//
//		InputSource source = new InputSource(document);
//
//		setDoc(db.parse(source));
//
////		if(schema){
////
////			SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
////			File schemaLocation = new File(schema_file);
////			Schema schema = factory.newSchema(schemaLocation);
////			Validator validator = schema.newValidator();
////
////
////			DOMSource domsource = new DOMSource(doc);
////
////			try {
////				validator.validate(domsource);
////			}catch (SAXException ex) {
////				ex.printStackTrace();
////			}  
//
////		}
//
//
//		return getDoc();
//
//	}
//	
	
//	private Document parser(String document) throws SAXException, IOException{
//		return parse(new StringReader(document));
//	}
//	
//	private Element insertTag(Element parent, String TagName, String[] attributeName, String[] attributeValue){
//		
//		Element elem = getDoc().createElement(TagName);
//
//	    for(int i=0; i<attributeName.length; i++){
//	    	
//	    	if(!attributeValue[i].trim().equals(""))
//	    		elem.setAttribute(attributeName[i],attributeValue[i]);
//	    	
//	    }
//	    	    
//		parent.appendChild(elem);
//	    	    
//	    return elem;
//	
//	    
//	}
	
	
	
	
	
//	public static String toString(Element element) {
//
//		if (element == null)
//			return "";
//
//		try {
//
//			TransformerFactory tFactory = TransformerFactory.newInstance();
//			// tFactory.setAttribute("indent-number", new Integer(4));
//			Transformer transformer = tFactory.newTransformer();
//			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
//			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
//					"yes");
//			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
//			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//			transformer.setOutputProperty(
//					"{http://xml.apache.org/xslt}indent-amount", "4");
//
//			StringWriter writer = new StringWriter(256);
//			DOMSource source = new DOMSource(element);
//			transformer.transform(source, new StreamResult(writer));
//
//			return writer.toString();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return "";
//
//	}

//	public String toString(){
//
//		if(getDoc() == null)
//			return "NULL";
//
//		try{	
//
//			TransformerFactory tFactory =
//				TransformerFactory.newInstance();
////			tFactory.setAttribute("indent-number", 4);
//			Transformer transformer = tFactory.newTransformer();
//			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
//			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
//			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
//
//			StringWriter writer = new StringWriter(256);
//			DOMSource source = new DOMSource(getDoc());
//			transformer.transform(source,new StreamResult(writer));
//
//			return writer.toString();
//
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//		return "";
//
//	}
//
//	public void writeToFile(String filename) {
//		
//		try {
//			// Prepare the DOM document for writing
//			Source source = new DOMSource(getDoc());
//
//			// Prepare the output file
//			File file = new File(filename);
//			Result result = new StreamResult(file);
//
//			// Write the DOM document to the file
//			TransformerFactory tFactory = TransformerFactory.newInstance();
//			tFactory.setAttribute("indent-number", new Integer(4));
//			
//			Transformer xformer = tFactory.newTransformer();
//			xformer.setOutputProperty(OutputKeys.METHOD, "xml");
//			xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
//			xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
//			xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
//			
//			xformer.transform(source, result);
//			
//		} catch (TransformerConfigurationException e) {
//			System.out.println("ParserFactory : " + e);
//		} catch (TransformerException e) {
//			System.out.println("ParserFactory : " + e);
//		}
//		
//	}
//
//	public Document getDoc() {
//		return doc;
//	}
//
//	public void setDoc(Document doc) {
//		this.doc = doc;
//	}
	
}
