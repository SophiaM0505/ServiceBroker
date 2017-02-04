package uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import negotiation.Database.NegotiationDB;
import negotiation.Ontology.OntUpdate;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.helper.Helper;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;

public class SubmissionAwsErrorWorkHandler implements WorkItemHandler{

	private static Logger logger = LoggerFactory.getLogger(SubmissionAwsErrorWorkHandler.class);
	
	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeWorkItem(final WorkItem arg0, final WorkItemManager arg1) {
		
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
		
				Long id;
				//long con_id;
				//Integer job_status = 1;
			
				
				//Annoying Bug
				if(arg0.getParameter("app_inst_id_comp") instanceof String)
					id = Long.valueOf((String) arg0.getParameter("app_inst_id_comp"));
				else
					id = (Long) arg0.getParameter("app_inst_id_comp");
				
				//AppInstance inst = AHEEngine.getAppInstanceEntity(id);

				//Helper.writeout("before submission aws handler.");
				/*try {
					Helper.writeout("after submission aws handler.");
					//Map<String, String> values = NegotiationDB.getPaybackInfo(id);
					OntUpdate.mPolicyCloudPayback(id);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OWLOntologyCreationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OWLOntologyStorageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				    arg1.completeWorkItem(arg0.getId(), null);
				
				
	}
        }).start();

}
}
