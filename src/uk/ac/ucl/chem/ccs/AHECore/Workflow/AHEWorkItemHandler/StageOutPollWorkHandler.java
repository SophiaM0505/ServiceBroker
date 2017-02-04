package uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler;

import java.util.ArrayList;
import java.util.HashMap;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ucl.chem.ccs.AHECore.Definition.AppInstanceStates;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.FileStaging;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.AHEMessageUtility;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.TransferHandler;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHEModule.Def.AHEPollStatus;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.AHEMessage;

public class StageOutPollWorkHandler  implements WorkItemHandler{

	private static Logger logger = LoggerFactory.getLogger(StageOutPollWorkHandler.class);
	private static String stageout_poll_status_code = "stageout_poll_status";
	private static String notification_code =  "notification_message";
	
	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO Stage In polling should be a separate work item from initiating transfer
	 */

	@Override
	public void executeWorkItem(final WorkItem arg0,final WorkItemManager arg1) {
		System.out.println("Stage out poller");

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					
					Long id;
					//Integer job_status = 1;
					Integer stageout_poll_status = 1;
					HashMap<String, Object> result = new HashMap<String, Object>();
					
					//Annoying Bug
					if(arg0.getParameter("app_inst_id") instanceof String)
						id = Long.valueOf((String) arg0.getParameter("app_inst_id"));
					else
						id = (Long) arg0.getParameter("app_inst_id");
					
					AppInstance inst = AHEEngine.getAppInstanceEntity(id);
					
					//Get the unique transfer unique id's and check if they're completed
					
					FileStaging[] out = AHEEngine.getFileStageOutByAppInstId(id);
					HashMap<String,ArrayList<FileStaging>> to_check = new HashMap<String,ArrayList<FileStaging>>();
					
					for(int i=0; i < out.length; i++){
						
						if(to_check.containsKey(out[i].getIdentifier())){
							to_check.get(out[i].getIdentifier()).add(out[i]);
						}else{
							
							ArrayList<FileStaging> new_list = new ArrayList<FileStaging>();
							new_list.add(out[i]);
							to_check.put(out[i].getIdentifier(), new_list);
							
						}
						
					}
					
					//Poll and Check
					String[] keyset = to_check.keySet().toArray(new String[to_check.size()]);
					
					for(int i=0; i < keyset.length; i++){
						
						ArrayList<FileStaging> temp = to_check.get(keyset[i]);
					
						try{
							
							boolean completed = false;
							FileStaging[] transfers = temp.toArray(new FileStaging[temp.size()]);
							
							while(!completed){
							
								AHEMessage msg = TransferHandler.transfer_status(transfers);
	
								if(AHEMessageUtility.hasException(msg)){
									String er1 = "";
									
									if(msg.getException() != null){
										er1 = msg.getException()[0];
									}

									failed(inst, arg0, "Data Polling failed. Transfer Module has returned an exception msg : " + er1 + " for transfer_id : " + temp.get(0).getIdentifier(), null);
									return;
								}else{
									
									AHEPollStatus status = AHEPollStatus.valueOf(msg.getInformation()[0]);
									
									if(status == AHEPollStatus.DONE){
										
										for(int j=0; j < transfers.length; j++){
											
											transfers[j].setStatus(1);
											HibernateUtil.SaveOrUpdate(transfers[j]);
											
										}
										
										completed = true;
										stageout_poll_status = 1;
										logger.info("Data (Result Staging) Polling check completed for transfer_id : " + temp.get(0).getIdentifier() + ", for app_id : " + id);
										break;
									}else if(status == AHEPollStatus.PENDING){
										//Wait xx time
										//implement a logrithim-time est time scale ?
										logger.info("Data Polling check for transfer_id : " + temp.get(0).getIdentifier() + ", for app_id : " + id + ", still pending. Waiting");
										Thread.sleep(30*1000);
									}else{
										failed(inst, arg0, "Data Polling failed (Status: "+ status+") for transfer_id : ", null);
										return;
									}
									
								}
							
							}
							
						}catch(Exception e){
							failed(inst, arg0, "Data polling failed", e);
							return;
						}
						
					}
					
					//Send the data through for now
					
					if(stageout_poll_status == -1) {
						AHEEngine.setCurrentAppInstanceState(id, AppInstanceStates.Error_ResultStaged.toString(),"Data (Result Fetching) Polling failed" );
						result.put(notification_code,"Result data fetching polling failed for AppInst : " + inst.getId());
					} else {
						AHEEngine.setCurrentAppInstanceState(id, AppInstanceStates.Workflow_Running.toString(),"Data (Result Fetching) Polling succeeded");
					}
					
					result.put(stageout_poll_status_code, stageout_poll_status);
					AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
					return;
					
				}catch(AHEException e){
					
					e.printStackTrace();
					failed(arg0, "Result data fetching polling failed : " + e.getLocalizedMessage());
//					logger.error("Stage Out polling failed",e);
//					
//					HashMap<String, Object> result = new HashMap<String, Object>();
//					result.put(stageout_poll_status_code,-1);
//					result.put(notification_code,"Result data fetching polling failed : " + e.getLocalizedMessage());
//					AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
					return;
				}
				
			}
			
		}).start();
		
	}
	
	private void failed(AppInstance inst, WorkItem arg0, String errmsg, Exception e ) throws AHEException{
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		AHEEngine.setCurrentAppInstanceState(inst.getId(), AppInstanceStates.Error_ResultStaged.toString(),errmsg);
		
		if(e != null)
			logger.error(errmsg,e);
		else
			logger.error(errmsg);
		
		result.put(notification_code,errmsg);
		result.put(stageout_poll_status_code,-1);
		
		AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
		
	}
	
	private void failed(WorkItem arg0, String errmsg ){
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		logger.error(errmsg);
		result.put(stageout_poll_status_code,-1);
		result.put(notification_code,errmsg);
		AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
	}
	


}

