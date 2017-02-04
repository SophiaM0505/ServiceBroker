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


public class JobCompletedHandler implements WorkItemHandler{

	private static Logger logger = LoggerFactory.getLogger(JobCompletedHandler.class);
	private static String notification_code =  "notification_message";
	
	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeWorkItem(WorkItem arg0, WorkItemManager arg1) {
		System.out.println("Job Completed Handler");
		
		
		Long id;
		
		//Annoying Bug
		if(arg0.getParameter("app_inst_id") instanceof String)
			id = Long.valueOf((String) arg0.getParameter("app_inst_id"));
		else
			id = (Long) arg0.getParameter("app_inst_id");
		
		//Update AppInstance state
		
		Executable.writeout("job completed handler completed new workflow");
		arg1.completeWorkItem(arg0.getId(), null);
		/*try{
		
			Long id;
			
			//Annoying Bug
			if(arg0.getParameter("app_inst_id") instanceof String)
				id = Long.valueOf((String) arg0.getParameter("app_inst_id"));
			else
				id = (Long) arg0.getParameter("app_inst_id");
			
			//Update AppInstance state
			AHEEngine.setCurrentAppInstanceState(id, AppInstanceStates.Finished.toString(),"Job Completed");
			
			HashMap<String, Object> result = new HashMap<String, Object>();	
			result.put(notification_code, "Job Completed for AppInst : " + id);
			Executable.writeout("job completed handler completed new workflow");
			arg1.completeWorkItem(arg0.getId(), result);
		
		}catch(AHEException e){
			e.printStackTrace();
		}
		*/
		
	}

}

