package uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StagingErrorWorkHandler implements WorkItemHandler{

	private static Logger logger = LoggerFactory.getLogger(StagingErrorWorkHandler.class);
	
	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeWorkItem(WorkItem arg0, WorkItemManager arg1) {

		arg1.completeWorkItem(arg0.getId(), null);
	}

}
