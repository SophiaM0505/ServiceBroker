package uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ucl.chem.ccs.AHECore.Definition.AppInstanceStates;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;


public class NotificationHandler implements WorkItemHandler{

	private static Logger logger = LoggerFactory.getLogger(NotificationHandler.class);
	
	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeWorkItem(WorkItem arg0, WorkItemManager arg1) {
		System.out.println("Notification Handler");
		
		Long id;

		// Annoying Bug
		if (arg0.getParameter("app_inst_id") instanceof String)
			id = Long.valueOf((String) arg0.getParameter("app_inst_id"));
		else
			id = (Long) arg0.getParameter("app_inst_id");

		/**
		 * Notification Use Simple Sys.out for now. Implement email or something
		 * later?
		 */

		if (arg0.getParameter("notification_message") != null) {

			String message = (String) arg0.getParameter("notification_message");
			System.out.println("MSG : " + message);

		}

		System.out.println("Notification : TODO implement notification mechanism i.e. email");
		
	}

}

