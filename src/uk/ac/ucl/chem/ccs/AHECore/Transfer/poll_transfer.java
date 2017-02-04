package uk.ac.ucl.chem.ccs.AHECore.Transfer;

import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.FileStaging;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.AHEMessageUtility;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.TransferHandler;
import uk.ac.ucl.chem.ccs.AHEModule.Def.AHEPollStatus;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.AHEMessage;

/**
 * Runnable thread to poll transfer status
 * @author davidc
 *
 */

public class poll_transfer implements Runnable {

	FileStaging transfer;
	
	public poll_transfer(FileStaging file){
		transfer = file;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
		
			do{
				
				AHEMessage pollmsg = TransferHandler.transfer_status(new FileStaging[]{transfer});
				
				if(AHEMessageUtility.hasException(pollmsg)){		
					dispose(false);
					return;
				}
				
				AHEPollStatus status = AHEPollStatus.valueOf(pollmsg.getInformation()[0]);
				
				if(status == AHEPollStatus.DONE){
					dispose(true);
					return;
				}else if(status == AHEPollStatus.PENDING){
					Thread.sleep(30 * 1000);
					continue;
				}else{
					dispose(false);
					return;
				}
				
			}while(true);
		
		}catch(Exception e){
			e.printStackTrace();
			dispose(false);
		}
	}
	
	private void dispose(boolean succeed){
		
		transfer.setStatus(succeed ? 1 : -1);
		HibernateUtil.SaveOrUpdate(transfer);
		
		this.transfer = null;

	}

}


