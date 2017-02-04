package negotiation.Steerer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TimerTask;

import negotiation.Database.NegotiationDB;

import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.JobSubmissionHandler;

public class RedQueenTimer extends TimerTask {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		long broker_job_id = JobSubmissionHandler.timer_job_id;
		long contract_id = Long.parseLong(JobSubmissionHandler.paras[2]);
		
		String[] submit_data = new String[3];
		try {
			submit_data = NegotiationDB.getRedQueenSubmission(contract_id);
		} catch (InstantiationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String numjobs = submit_data[0];
		String nefold = submit_data[1];
		String level = submit_data[2];
		final String command = "/opt/test/job_submit_broker_expect.sh " 
		+ numjobs + " " + nefold + " " + broker_job_id + " " + level ;
		//final String command = "/opt/test/job_submit_broker_expect.sh 5 7 " + broker_job_id + " " + "2" ;
		
		System.out.println("%%%%%%%%%%% submitted command: " + command);
		Runtime rt = Runtime.getRuntime();
		try {
			rt.exec(command);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		completeTask();
		try {
			Redqueen.getRedqueenJobId(broker_job_id);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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
		}
		
	}

	private void completeTask() {
        try {
            //assuming it takes 20 secs to complete the task
            Thread.sleep(60000);           
            System.out.println("**********");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
