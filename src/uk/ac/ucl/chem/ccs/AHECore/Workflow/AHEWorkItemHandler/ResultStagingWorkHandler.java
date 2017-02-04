package uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ucl.chem.ccs.AHECore.Definition.AppInstanceStates;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Engine.ResourceRegisterAPI;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.FileStaging;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.AHEMessageUtility;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.TransferHandler;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHEModule.Def.AHEPollStatus;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.AHEMessage;



/**
 * fetch_status variable is used to signal whether to proceed with workflow or end it with an error
 * @author davidc
 *
 */

public class ResultStagingWorkHandler implements WorkItemHandler{

	private static Logger logger = LoggerFactory.getLogger(ResultStagingWorkHandler.class);
	private static String notification_code =  "notification_message";
	
	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// TODO Auto-generated method stub
		
	}

	private String fetch_status_code = "fetch_status";
	
	@Override
	public void executeWorkItem(final WorkItem arg0,final WorkItemManager arg1) {
		System.out.println("Result Staging Handler");
		
		//Start asynchronous Work Handler
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
		
				try{
				
					Long id;
					int fetch_status = 1;
					
					boolean hasFailed = false;
					
					//Annoying Bug
					if(arg0.getParameter("app_inst_id") instanceof String)
						id = Long.valueOf((String) arg0.getParameter("app_inst_id"));
					else
						id = (Long) arg0.getParameter("app_inst_id");
					
					//Get Staging In information and initiate transfer
					//AppInstance inst = AHEEngine.getAppInstanceEntity(id);
					
					//AppInstance inst = AHEEngine.getAppInstanceEntity(id);
					FileStaging[] out = AHEEngine.getFileStageOutByAppInstId(id);

					//Update AppInstance Status
					AHEEngine.setCurrentAppInstanceState(id, AppInstanceStates.Data_Stageout.toString(),"Fetching Result Data");

					//Sort FileStaging into groups
					HashMap<String, ArrayList<FileStaging>> map = new HashMap<String, ArrayList<FileStaging>>();
					
					for(int i=0; i < out.length; i++){
						
						URI uri = null;
						
						try {
							uri = new URI(out[i].getSource());
						} catch (URISyntaxException e) {
							e.printStackTrace();
							logger.error("Data Staging failed",e);
							fetch_status = -1;
							hasFailed = true;
						}
						
						Resource res = ResourceRegisterAPI.getResourceByBestMatch(uri);
						
						if(map.containsKey(res.getResource_interface())){
							map.get(res.getResource_interface()).add(out[i]);
							continue;
						}else{
							ArrayList<FileStaging> array = new ArrayList<FileStaging>();
							array.add(out[i]);
							map.put(res.getResource_interface(), array);
						}
						
					}
					
					String[] keyset = map.keySet().toArray(new String[map.size()]);
					
					for(int i=0; i < keyset.length; i++){
						
						ArrayList<FileStaging> temp = map.get(keyset[i]);
						
						try{
							
							AHEMessage msg = TransferHandler.transfer(temp.toArray(new FileStaging[temp.size()]));
							
							if(AHEMessageUtility.hasException(msg)){
								
								String er1 = "";
								
								if(msg.getException() != null){
									er1 = msg.getException()[0];
								}
								fetch_status = -1;
								hasFailed = true;
								logger.error("Data Staging failed : " + er1);
							}
//							else{
//								
//								if(msg.getInformation().length > 0){
//									
//									//Update FileStaging with uid
//									for(int j=0; j < temp.size(); j++){
//										
//										temp.get(j).setIdentifier(msg.getInformation()[0]);
//										HibernateUtil.SaveOrUpdate(temp.get(j));
//																		
//									}
//									
//								}
//								
//							}
						
						}catch(Exception e){
							logger.error("Result retrieval failed",e);
							fetch_status = -1;
							hasFailed = true;
						}
		
						
					}
					
					
					//Update AppInstance state
					if(hasFailed) {
						AHEEngine.setCurrentAppInstanceState(id, AppInstanceStates.Error_ResultStaged.toString(),"Error: data stage out failed");
						
					} else {
						AHEEngine.setCurrentAppInstanceState(id, AppInstanceStates.Workflow_Running.toString(),"Result Staged");
					}
					
					Map<String, Object> result = new HashMap<String, Object>();
					
					if(hasFailed){
						fetch_status = -1;
						result.put(notification_code,"Result data fetching failed for AppInst : " + id);	
					}
						
					result.put(fetch_status_code, fetch_status);
					
					AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
		
					//arg1.completeWorkItem(arg0.getId(), result);
		
					return;
			
			
			
				}catch(AHEException e){
					e.printStackTrace();
				}
		
			}
		}).start();
		
	}
	
	private void failed(WorkItem arg0, String errmsg ){
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		logger.error(errmsg);
		result.put(fetch_status_code,-1);
		result.put(notification_code,errmsg);
		AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);
	}
	
	private void failed(AppInstance inst, WorkItem arg0, String errmsg, Exception e ) throws AHEException{
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		AHEEngine.setCurrentAppInstanceState(inst.getId(), AppInstanceStates.Error_ResultStaged.toString(),errmsg);
		
		if(e != null)
			logger.error(errmsg,e);
		else
			logger.error(errmsg);
		
		result.put(fetch_status_code,-1);
		result.put(notification_code,errmsg);
		AHERuntime.getAHERuntime().getAhe_engine().getWorkflowHandler().getStatefulKnowledgeSession().getWorkItemManager().completeWorkItem(arg0.getId(), result);

	}


}
