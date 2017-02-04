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

public class StageInPollWorkHandler  implements WorkItemHandler{

	private static Logger logger = LoggerFactory.getLogger(StageInPollWorkHandler.class);
	private static String stagein_poll_status_code = "stagein_poll_status";
	private static String notification_code =  "notification_message";
	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void executeWorkItem(final WorkItem arg0,final WorkItemManager arg1) {
		System.out.println("Stage in poller");

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try{
				
					Long id;
					//Integer job_status = 1;
					Integer stagein_poll_status = 1;
					HashMap<String, Object> result = new HashMap<String, Object>();
					
					//Annoying Bug
					if(arg0.getParameter("app_inst_id") instanceof String)
						id = Long.valueOf((String) arg0.getParameter("app_inst_id"));
					else
						id = (Long) arg0.getParameter("app_inst_id");
					
//					AppInstance inst = AHEEngine.getAppInstanceEntity(id);
					
					//Get the unique transfer unique id's and check if they're completed
					
					FileStaging[] in = AHEEngine.getFileStageInByAppInstId(id);
					HashMap<String,ArrayList<FileStaging>> to_check = new HashMap<String,ArrayList<FileStaging>>();
					
					for(int i=0; i < in.length; i++){
						
						if(to_check.containsKey(in[i].getIdentifier())){
							to_check.get(in[i].getIdentifier()).add(in[i]);
						}else{
							
							ArrayList<FileStaging> new_list = new ArrayList<FileStaging>();
							new_list.add(in[i]);
							to_check.put(in[i].getIdentifier(), new_list);
							
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

									failed(id, arg0, "Data Polling failed. Transfer Module has returned an exception msg : " + er1 + " for transfer_id : " + temp.get(0).getIdentifier(), null);
									return;
								}else{
									
									AHEPollStatus status = AHEPollStatus.valueOf(msg.getInformation()[0]);

									if(status == AHEPollStatus.DONE){
										completed = true;
										
										for(int j=0; j < transfers.length; j++){
											
											transfers[j].setStatus(1);
											HibernateUtil.SaveOrUpdate(transfers[j]);
											
										}
										
										stagein_poll_status = 1;
										logger.info("Data Polling check completed for transfer_id : " + temp.get(0).getIdentifier() + ", for app_id : " + id);
										break;
									}else if(status == AHEPollStatus.PENDING){
										//Wait xx time
										//implement a logrithim-time est time scale ?
										logger.info("Data Polling check for transfer_id : " + temp.get(0).getIdentifier() + ", for app_id : " + id + ", still pending. Waiting");
										Thread.sleep(30*1000);
									}else{
										failed(id, arg0, "Data Polling failed (Status: "+ status+") for transfer_id : " + temp.get(0).getIdentifier(), null);
										return;
									}
									
								}
							
							}
							
						}catch(Exception e){
							failed(id, arg0, "Data polling failed for app_id : " + id, e);
							return;
						}
						
					}
					
					//Send the data through for now
					
					if(stagein_poll_status == -1) {
						AHEEngine.setCurrentAppInstanceState(id, AppInstanceStates.Error_DataStaging.toString(),"Data Polling failed" );
						result.put(notification_code,"Data Staging polling failed for AppInst : " + id);
					} else {
						AHEEngine.setCurrentAppInstanceState(id, AppInstanceStates.Workflow_Running.toString(),"Data Polling succeeded");
					}
					
					result.put(stagein_poll_status_code, stagein_poll_status);
					AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
					return;
					
					
				}catch(AHEException e){
					
					e.printStackTrace();
					logger.error("Stage In polling failed",e);
					
					HashMap<String, Object> result = new HashMap<String, Object>();
					result.put(stagein_poll_status_code,-1);
					AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
					return;
				}
				
			}
		}).start();
		
		
	}
	
	private void failed(long app_id, WorkItem arg0, String errmsg, Exception e ) throws AHEException{
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		AHEEngine.setCurrentAppInstanceState(app_id, AppInstanceStates.Error_DataStaging.toString(),errmsg);
		
		if(e != null)
			logger.error(errmsg,e);
		else
			logger.error(errmsg);
		
		result.put(stagein_poll_status_code,-1);
		result.put(notification_code,errmsg);
		AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
	}
	
	private void failed(WorkItem arg0, String errmsg ){
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		logger.error(errmsg);
		result.put(stagein_poll_status_code,-1);
		result.put(notification_code,errmsg);
		AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
	}
	

}

