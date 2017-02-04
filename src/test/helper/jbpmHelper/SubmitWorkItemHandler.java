package test.helper.jbpmHelper;

import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ucl.chem.ccs.AHECore.Definition.AppInstanceStates;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.AHEMessageUtility;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.JobSubmissionHandler;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.AHEMessage;


/**
 * submit_status variable is used to signal whether to proceed with workflow or end it with an error
 * @author davidc
 *
 */

public class SubmitWorkItemHandler implements WorkItemHandler{

	/*private static Logger logger = LoggerFactory.getLogger(SubmissionWorkHandler.class);
	private static String submit_status_code = "submit_status";
	//private static String job_status_code = "job_status";
	private static String submit_id_code = "submit_id";
	private static String notification_code =  "notification_message";*/
	
	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeWorkItem(final WorkItem arg0,final WorkItemManager arg1){
		
		//System.out.println("Submission Handler: ");
		String hello = "sophia";
		//Submit s = new Submit();
		//s.setHello(hello);
		System.out.println("goes here: " + hello);
		arg1.completeWorkItem(arg0.getId(), null);
		//Start asynchronous Work Handler
		/*new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
		
				try{

					Long id;
					//Integer job_status = 1;
					Integer submit_status = 1;
					HashMap<String, Object> result = new HashMap<String, Object>();
					
					//Annoying Bug
					if(arg0.getParameter("app_inst_id") instanceof String)
						id = Long.valueOf((String) arg0.getParameter("app_inst_id"));
					else
						id = (Long) arg0.getParameter("app_inst_id");
					
					AppInstance inst = AHEEngine.getAppInstanceEntity(id);

					// Update AppInstance state
					AHEEngine.setCurrentAppInstanceState(id,AppInstanceStates.Submit_Job.toString(), "Submitting Job");

					//Submit Job
					
					try {
						logger.info("Submitting job for app_id : " +id);
						AHEMessage msg = JobSubmissionHandler.submit_job(inst);
						
						if(AHEMessageUtility.hasException(msg)){
							failed(inst, arg0,"(AppInst:" + id +")Job Submission Service call failed", null);
							return;
						}
		
						String submit_id = msg.getInformation()[0].trim();
		
						if (submit_id == null) {
							failed(inst, arg0, "(AppInst:" + id +")No Resource Submission ID provided", null);
							return;
						} else if (submit_id.length() == 0) {
							failed(inst, arg0, "(AppInst:" + id +")No Resource Submission ID provided", null);
							return;
						}
		
						logger.info("Job submitted for app_id : " + id + ", job_id : " + submit_id);
						
						//AHEEngine.setAppInstanceJobID(inst, submit_id);
											
						result.put(submit_id_code, submit_id);
						result.put(submit_status_code, submit_status);
		
						AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
		
						
					} catch (Exception e) {
						failed(inst, arg0, "(AppInst:" + id +")Submission failed: " + e.getMessage(),e);
						return;
					}
		
		
				
				}catch(AHEException e){
					e.printStackTrace();
					failed(arg0,"Submission Work handler failed");
				}
				
				

		
			}
		}).start();*/
	}
	
	/*private void failed(WorkItem arg0, String errmsg ){
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		logger.error(errmsg);
		result.put(submit_status_code,-1);
		result.put(notification_code,errmsg);
		AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
	}
	
	private void failed(AppInstance inst, WorkItem arg0, String errmsg, Exception e ) throws AHEException{
	
		HashMap<String, Object> result = new HashMap<String, Object>();
		AHEEngine.setCurrentAppInstanceState(inst.getId(), AppInstanceStates.Error_Job_Submission.toString(),errmsg);
		
		if(e != null)
			logger.error(errmsg,e);
		else
			logger.error(errmsg);
		
		result.put(submit_status_code,-1);
		result.put(notification_code,errmsg);
		AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);

	}*/

}

