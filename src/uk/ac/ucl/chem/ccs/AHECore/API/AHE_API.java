package uk.ac.ucl.chem.ccs.AHECore.API;

import java.util.ArrayList;
import java.util.Random;

import javax.jws.WebService;

import negotiation.HibernateImp.ContractInst;
import negotiation.HibernateImp.NegHibConnect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.helper.Executable;
import test.helper.Helper;
import uk.ac.ucl.chem.ccs.AHECore.API.Rest.AppInstanceResource;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AppRegisteryAPI;
import uk.ac.ucl.chem.ccs.AHECore.Engine.ResourceRegisterAPI;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AppAlreadyExistException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AppInstanceAlreadyExistsException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AppNotFoundException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstanceArg;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppRegistery;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.WorkflowDescription;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.JobSubmissionHandler;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.WorkflowAPI;
import uk.ac.ucl.chem.ccs.AHEModule.Def.RESOURCE_AUTHEN_TYPE;


/**
 * High Level commands to run, control and monitor AHE Jobs
 * 
 * @author davidc
 *
 */

@WebService(targetNamespace = "http://API.AHECore.ccs.chem.ucl.ac.uk/", portName = "AHE_APIPort", serviceName = "AHE_APIService")
public class AHE_API{

	private static Logger logger = LoggerFactory.getLogger(AHE_API.class);

	/**
	 * Set data staging information
	 * @param AppInstId application instance ID
	 * @param stagein stage in URI String array
	 * @param stageout stage out URI string array
	 * @return
	 */
	
	
	public static void DataStaging(long AppInstId, String[] stagein_src, String[] stagein_dest, String[] stageout_src, String[] stageout_dest) throws AHEException{
		
		AHERuntime.getAHERuntime().getAhe_engine().setFileStageLocation(AppInstId, stagein_src,stagein_dest,stageout_src,stageout_dest);
	}

	/**
	 * Prepare an application instance
	 * @param owner AHE user that this application instance belongs to
	 * @param app_name a valid application name in AHE
	 * @param job_name Name for this application instance
	 * @param workflow_name
	 * @param cpucount optional, CPU requested
	 * @param memory optional, memory requested
	 * @param vmemory optional, virtual memory requested
	 * @param walltimelimit optional, wall time limit
	 * @return
	 * @throws AHEException
	 * @throws AppNotFoundException
	 * @throws AppInstanceAlreadyExistsException
	 */
	
	public static Object Prepare(AHEUser owner, String app_name, String job_name, String workflow_name, int cpucount, int memory, int vmemory, int walltimelimit) throws AHEException, AppNotFoundException, AppInstanceAlreadyExistsException{

		AHEEngine engine = AHERuntime.getAHERuntime().getAhe_engine();
		//Executable.writeout("2 step");
		AppRegistery[] search = AppRegisteryAPI.getApplication(app_name);
		//System.out.println("********** AppRegistery[0]: "+ search[0]);
		//Helper.writeout("********** AppRegistery[0]: "+ search[0].getExecutable());
		
		if(search.length == 0){
			//TODO error handler
			throw new AppNotFoundException("Prepare failed, application not found for : " + app_name);
		}
		
		AppInstance inst = AHEEngine.createAppInstance(owner, job_name, search[0]);
		
		long inst_id = inst.getId(); 
		//long con = 6;
		Random rand = new Random();
	    int temp_id = 100000 + rand.nextInt(900000);
		ContractInst coninst = new ContractInst();
		coninst.setId(temp_id);
		//coninst.setCon_id(con);
		coninst.setConId(AppInstanceResource.contract_id);
		coninst.setInstId(inst_id);
		
		NegHibConnect.hibConInst(coninst);
		
		//Create parameters
		ArrayList<String> arg = new ArrayList<String>();
		
		if(cpucount > -1){
			arg.add("cpucount");
			arg.add(""+cpucount);
		}
		
		if(memory > -1){
			arg.add("memory");
			arg.add(""+memory);
		}
		
		if(vmemory > -1){
			arg.add("vmemory");
			arg.add(""+vmemory);
		}
		
		if(walltimelimit > -1){
			arg.add("walltimelimit");
			arg.add(""+walltimelimit);
		}
		
		if(workflow_name.length() > 0){
			arg.add("workflow_name");
			arg.add(workflow_name);
		}
		
		AHEEngine.createAppInstanceArg(inst.getId(), arg.toArray(new String[arg.size()]));
		
		boolean persistance = true;
		
		//Starts Workflow
		
		if(workflow_name.length() > 0){
			//System.out.println(workflow_name);
			WorkflowDescription d = WorkflowAPI.getWorkflowDescription(workflow_name);
			//Executable.writeout("workflow name: " + workflow_name
				//	+ " workflow file name: " + d.getWorkflow_filename());
            /*WorkflowDescription d = new WorkflowDescription();
			
			d.setWorkflow_name("AHEWorkflow");
			d.setWorkflow_filename("AHEWorkflow.bpmn");
			d.setWorkflow_description("test case");
			d.setAppinst_workflow(true);*/
			
			if(d != null){
				//Executable.writeout("here hello before start wf");
				engine.startWorkflow(inst.getId(),d.getWorkflow_name(),d.getWorkflow_filename(),persistance);
			}else{
				throw new AHEException("AHE_API.java@Prepare(): Invalid workflow name provided : " + workflow_name);
			}
			
		}else
		{
			//Executable.writeout("before AHE_API Prepare engine startWorkflow workflow_name null");
			engine.startWorkflow(inst.getId(),persistance);
		}
		return inst;
	}

	/**
	 * Add an application entry in AHE
	 * @param app_name Application name
	 * @param executable Application executable path
	 * @param resource_name A valid resource in AHE which host this application
	 * @param description optional, a description of this application
	 * @return
	 * @throws AppAlreadyExistException
	 * @throws AHEException
	 */
	
	
	public static AppRegistery insertApplicationRegistery(String app_name, String executable, String resource_name, String description) throws AppAlreadyExistException, AHEException{
		return AppRegisteryAPI.createApplication(app_name, executable, resource_name, description);
	}
	
	/**
	 * Insert a new Resource in AHE
	 * @param name name of the resource
	 * @param endpoint a valid URI endpoint
	 * @param resource_interface Resource interface
	 * @param type Resource type
	 * @param cpucount CPU count
	 * @param arch architecture
	 * @param memory memory
	 * @param vmemory virtual memory
	 * @param opsys operating system
	 * @param ip IP address
	 * @param walltimelimit wall time limit
	 * @param port port (-1 to ignore)
	 * @param authen_type Authentication type
	 * @param description description of resource
	 * @return
	 */
	
	public static Resource insertResourceRegistery(String name, String endpoint, String resource_interface, String type, int cpucount, String arch, int memory, int vmemory, String opsys, String ip, int walltimelimit, int port, String authen_type, String description){
		return ResourceRegisterAPI.createResource(name, endpoint,resource_interface, type, cpucount, arch, memory, vmemory, opsys, ip, walltimelimit, port, authen_type, description);
	}
	
	/**
	 * Returns a list of application in AHE
	 * @return array of AppRegistry
	 */
	
	public static AppRegistery[] getApplicationRegisteryAPI() {
		return AppRegisteryAPI.getApplicationList();
	}

	/**
	 * Set application argument for an application instance
	 * @param AppInstId application instance id
	 * @param key argument key
	 * @param value argument value
	 * @return
	 * @throws AHEException
	 */

	public static void setArgument(long AppInstId, String key, String value) throws AHEException{
		AHEEngine.createAppInstanceArg(AppInstId, new String[]{key,value});
	}
	
	/**
	 * Clone an application instance, creating a copy and set the state to wait_usercmd
	 * @param app_inst_id application id to be cloned
	 * @param new_job_name optional job name
	 * @return
	 * @throws AHEException
	 * @throws AppInstanceAlreadyExistsException
	 */
	
	public static AppInstance cloneAppInstance(long app_inst_id, String new_job_name) throws AHEException, AppInstanceAlreadyExistsException {
		return AHEEngine.cloneAppInstance(app_inst_id, new_job_name);
	}
	
	/**
	 * Start application instance
	 * @param AppInstId application id to start
	 * @param resource_name resource that this application will be executed
	 * @param argument application arguments that will be passed to the resource job manager
	 * @throws AHEException
	 */
	
	public static void start(long AppInstId, String resource_name, String[] argument) throws AHEException{
		//Executable.writeout("AHE_API start");
		//AHERuntime.getAHERuntime().getAhe_engine().SignalSubmitCommand(AppInstId,resource_name, argument);

	}
	
	/**
	 * Get the status of the application instance
	 * @param AppInstId application instance id
	 * @return
	 * @throws AHEException
	 */

	public static Object status(long AppInstId) throws AHEException{
		return AHEEngine.getCurrentAppInstanceState(AppInstId);
	}
	
	/**
	 * Terminate application instance
	 * @param AppInstId application instance id
	 * @return
	 * @throws AHEException
	 */
	
	public static Object Terminate(long AppInstId) throws AHEException {

		return AHERuntime.getAHERuntime().getAhe_engine().terminateAppInstance(AppInstId);
	}

	/**
	 * Get application arguments with the specified key value
	 * @param AppInstId application instance id
	 * @param arg argument key
	 * @return AppInstanceArg array that contains the correct key
	 */

	public static AppInstanceArg[] getArgument(long AppInstId, String arg) {
		return AHEEngine.getAppInstanceArgument(AppInstId, arg);
	}
	
	/**
	 * List all application instance's arguments
	 * @param AppInstId application instance 
	 * @return
	 */
	
	public static AppInstanceArg[] listArguments(long AppInstId){
		return AHEEngine.getAppInstanceArgument(AppInstId);
	}

}

