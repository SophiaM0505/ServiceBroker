package uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ucl.chem.ccs.AHECore.Definition.AppInstanceStates;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;


public class InitWorkflowWorkHandler implements WorkItemHandler{

	private static Logger logger = LoggerFactory.getLogger(InitWorkflowWorkHandler.class);
	
	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeWorkItem(WorkItem arg0, WorkItemManager arg1) {
		System.out.println("Init Workflow Handler");
		
		try{
		
			Long id;
			
			//Annoying Bug
			if(arg0.getParameter("app_inst_id") instanceof String)
				id = Long.valueOf((String) arg0.getParameter("app_inst_id"));
			else
				id = (Long) arg0.getParameter("app_inst_id");
			
	
			//Update AppInstance state
			AHEEngine.setCurrentAppInstanceState(id, AppInstanceStates.Wait_UserCmd.toString(),"Workflow Initiated");
			
			arg1.completeWorkItem(arg0.getId(), null);
		
		}catch(AHEException e){
			e.printStackTrace();
		}
		
		
	}

}
