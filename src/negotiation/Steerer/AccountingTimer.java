package negotiation.Steerer;

import java.io.IOException;
import java.util.TimerTask;

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
		}
		completeTask();
		
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
