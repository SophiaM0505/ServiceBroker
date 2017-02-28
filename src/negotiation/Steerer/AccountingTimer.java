package negotiation.Steerer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TimerTask;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import uk.ac.ucl.chem.ccs.AHECore.API.Rest.RestAPI;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.JobSubmissionHandler;

public class AccountingTimer extends TimerTask {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			BrokerAccounting.brokerAccounting();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		completeTask();
		RestAPI.accounting_timer.purge();
		RestAPI.accounting_timer.cancel();
		
	}
	
	private void completeTask() {
        try {
            //assuming it takes 60 secs to complete the task
            Thread.sleep(60000);           
            System.out.println("**********");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
