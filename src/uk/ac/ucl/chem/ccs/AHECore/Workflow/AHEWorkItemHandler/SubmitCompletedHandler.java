package uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler;

import java.util.HashMap;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.helper.Executable;
import test.helper.Helper;
import uk.ac.ucl.chem.ccs.AHECore.Definition.AppInstanceStates;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;


public class SubmitCompletedHandler implements WorkItemHandler{

	private static Logger logger = LoggerFactory.getLogger(SubmitCompletedHandler.class);
	private static String notification_code =  "notification_message";
	
	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeWorkItem(WorkItem arg0, WorkItemManager arg1) {
		//System.out.println("Submission Completed Handler");
		
/*Long id;
		
		//Annoying Bug
		if(arg0.getParameter("comp_inst_id") instanceof String)
		{
			Helper.writeout("job completed handler completed new +++ workflow id: " + arg0.getParameter("comp_inst_id"));
			id = Long.valueOf((String) arg0.getParameter("comp_inst_id"));
		}
		else
			id = (Long) arg0.getParameter("comp_inst_id");
		
		//Update AppInstance state
		
		//Helper.writeout("job completed handler completed new === workflow id: " + id);
		arg1.completeWorkItem(arg0.getId(), null);*/
		
		try{
		
			Long id;
			
			//Annoying Bug
			if(arg0.getParameter("app_inst_id_comp") instanceof String)
				id = Long.valueOf((String) arg0.getParameter("app_inst_id_comp"));
			else
				id = (Long) arg0.getParameter("app_inst_id_comp");
			
			//Helper.writeout("6 submit completed handler today inst id : " + id);
			
			//Update AppInstance state
			AHEEngine.setCurrentAppInstanceState(id, AppInstanceStates.Submit_Completed.toString(),"Submission Completed");
			//Helper.writeout("submission completed handler completed workflow============");
			
			HashMap<String, Object> result = new HashMap<String, Object>();	
			//result.put(notification_code, "Job Completed for AppInst : " + id);
			//Executable.writeout("submission completed handler completed");
			arg1.completeWorkItem(arg0.getId(), result);
		
		}catch(AHEException e){
			e.printStackTrace();
		}
		
		
		
	}

}

