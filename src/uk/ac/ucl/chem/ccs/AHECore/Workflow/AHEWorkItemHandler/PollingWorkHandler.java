package uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler;

import java.util.HashMap;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.helper.Executable;
import uk.ac.ucl.chem.ccs.AHECore.Definition.AppInstanceStates;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.AHEMessageUtility;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.JobSubmissionHandler;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHEModule.Def.AHEPollStatus;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.AHEMessage;


public class PollingWorkHandler implements WorkItemHandler{

	private static Logger logger = LoggerFactory.getLogger(PollingWorkHandler.class);
	
	private static String polling_status_code = "polling_status";
	private static String notification_code =  "notification_message";
	
	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeWorkItem(final WorkItem arg0, final WorkItemManager arg1) {
		System.out.println("Polling Handler");
		
		
		Long id;
		//Integer job_status = 1;
		Integer submit_status = 1;
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		//Annoying Bug
		if(arg0.getParameter("app_inst_id") instanceof String)
			id = Long.valueOf((String) arg0.getParameter("app_inst_id"));
		else
			id = (Long) arg0.getParameter("app_inst_id");
		
		
		//Helper.writeout("here SubmissionWorkHandler started inst id new:" + id);
		Executable.writeout("hello Friday" + id);
		arg1.completeWorkItem(arg0.getId(), null);
		
		//Start asynchronous Work Handler
		/*new Thread(new Runnable() {		
		
			public void run() {
				// TODO Auto-generated method stub
			
				try{
				
					String submission_id = (String) arg0.getParameter("submit_id");
					
					Long id;
					
					//Annoying Bug
					if(arg0.getParameter("app_inst_id") instanceof String)
						id = Long.valueOf((String) arg0.getParameter("app_inst_id"));
					else
						id = (Long) arg0.getParameter("app_inst_id");
					
					
					//Update AppInstance state
					AHEEngine.setCurrentAppInstanceState(id, AppInstanceStates.Polling_CheckResource.toString(),"Polling Resource");
					
					*//**
					 * Use submission_id and query the platform to check the status of the job
					 *//*
					
					AppInstance inst = AHEEngine.getAppInstanceEntity(id);
					
					if(inst.getSubmit_job_id() == null){
						
//						Map<String, Object> result = new HashMap<String, Object>();
//						result.put(polling_status_code, -1);
//						result.put(notification_code,"Job Polling failed for AppInst : " + inst.getId() + ". No Submission ID found");
//						logger.error("No Submission ID found");
//						AHEEngine.setCurrentAppInstanceState(id, AppInstanceStates.Error_Polling.toString(),"No Submission ID found");
//						AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
//						return;
						
						failed(inst, arg0,"Job Polling failed for AppInst : " + inst.getId() + ". No Submission ID found",null);
						return;
						
					}
					
					HashMap<String, Object> result = new HashMap<String, Object>();	
					
		
					try {
						
						boolean completed = false;
						
						while(!completed){
							
							AHEMessage msg = JobSubmissionHandler.job_status(inst);
							
							if(AHEMessageUtility.hasException(msg)){
								failed(inst, arg0, msg.getException()[0],null);
								return;
							}
							
							AHEPollStatus job_status = AHEPollStatus.valueOf(msg.getInformation()[0]); //
			
							if (job_status == null) {
								failed(inst, arg0, msg.getException()[0],null);
								return;
							}
							
							if (job_status == AHEPollStatus.DONE){
								completed = true;
								result.put(polling_status_code, 1);
								logger.info("Job submitted for app_id : " + inst.getId());
								break;
							}else if (job_status == AHEPollStatus.FAILED) {
								
//								result.put(polling_status_code, -1);
//								result.put(notification_code,"Job Polling failed for AppInst : " + inst.getId());
//								completed = true;
//								
//								AHEEngine.setCurrentAppInstanceState(id,
//										AppInstanceStates.Error_Polling.toString(),
//										"Job failed at the Resource");
//								logger.error("Job Submission for job : "+ submission_id	+ " for resource : "+ inst.getAppreg().getResource().getEndpoint_reference() + " has failed");
//								break;
								
								failed(inst, arg0,"Job Submission for job : "+ submission_id	+ " for resource : "+ inst.getAppreg().getResource().getEndpoint_reference() + " has failed",null);
								return;
								
							}else{
								logger.info("Job submission for app_id : " + inst.getId() + " is still pending. Waiting");
								Thread.sleep(30*1000);
							}
							
						}

		
					} catch (Exception e) {
						e.printStackTrace();
						result.put(polling_status_code, -1);
					}
		
					AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
					return;

				}catch(AHEException e){
					e.printStackTrace();
					failed(arg0, e.getMessage());
				}
		
		
			}
		}).start();*/
		

	}
	
//	private void failed(AppInstance inst, WorkItem arg0) throws AHEException{
//		HashMap<String, Object> result = new HashMap<String, Object>();	
//		result.put(polling_status_code, -1);
//		result.put(notification_code,"Job Polling failed for AppInst : " + inst.getId());
//		AHEEngine.setCurrentAppInstanceState(inst.getId(),
//				AppInstanceStates.Error_Polling.toString(),
//				"Job Status can not be retrieved : "
//						+ inst.getAppreg().getResource()
//								.getEndpoint_reference());
//		logger.error("Job Status can not be retrieved : "
//				+ inst.getSubmit_job_id()
//				+ " for resource : "
//				+ inst.getAppreg().getResource()
//						.getEndpoint_reference() + " has failed");
//		AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
//		return;
//		
//	}
	
	private void failed(WorkItem arg0, String errmsg ){
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		logger.error(errmsg);
		result.put(polling_status_code,-1);
		result.put(notification_code,errmsg);
		AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
	}
	
	private void failed(AppInstance inst, WorkItem arg0, String errmsg, Exception e ) throws AHEException{
	
		HashMap<String, Object> result = new HashMap<String, Object>();
		AHEEngine.setCurrentAppInstanceState(inst.getId(), AppInstanceStates.Error_Polling.toString(),errmsg);
		
		if(e != null)
			logger.error(errmsg,e);
		else
			logger.error(errmsg);
		
		result.put(polling_status_code,-1);
		result.put(notification_code,errmsg);
		AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
	}

}

