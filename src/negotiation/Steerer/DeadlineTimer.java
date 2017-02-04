package negotiation.Steerer;

import java.util.TimerTask;

import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.JobSubmissionHandler;

public class DeadlineTimer extends TimerTask {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String deadline = JobSubmissionHandler.deadline;
		
	}

}
