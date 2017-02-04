package uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler;

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


public class SchedulerWorkHandler implements WorkItemHandler{

	private static Logger logger = LoggerFactory.getLogger(SchedulerWorkHandler.class);
	
	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeWorkItem(WorkItem arg0, WorkItemManager arg1) {
		System.out.println("Scheduler Handler");
	
		try{
		
			Map test = arg0.getParameters();
			System.out.println(test);
			
			Integer a = (Integer) test.get("count");
			a++;
			
			Long id;
			
			//Annoying Bug
			if(arg0.getParameter("app_inst_id") instanceof String)
				id = Long.valueOf((String) arg0.getParameter("app_inst_id"));
			else
				id = (Long) arg0.getParameter("app_inst_id");
			
			
			//Currently #timerdelay variable is statically injected from WorkflowAPI.java
			//To update this dynamically, make sure the workflow BPMN allows timerdelay to be injected/ejected 
			//and update from here
			
			//Update AppInstance state
			AHEEngine.setCurrentAppInstanceState(id, AppInstanceStates.Polling_WaitState.toString(),"Polling Wait");
			
			
			Map result = new HashMap<String, Object>();
			result.put("count", a);
			
			arg1.completeWorkItem(arg0.getId(), result);
		
		}catch(AHEException e){
			e.printStackTrace();
		}
		
		
	}

}
