package uk.ac.ucl.chem.ccs.AHECore.Transfer;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEDataTransferException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.FileStaging;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.AHEMessageUtility;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.TransferHandler;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHEModule.Def.AHEPollStatus;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.AHEMessage;

/**
 * Runnable thread that handles two part transfers. A file is transfered to a tmp folder on the AHE server and then transfered to the final destination
 * @author davidc
 *
 */

public class two_part_transfer implements Runnable{

	FileStaging transfer;
	
	public two_part_transfer(FileStaging transfer){
		this.transfer = transfer;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		//Intemdiary transfer is requried, break transfer into two phases
		//source -> file://ahe_tmp/ and file://ahe_tmp/blah to destination
		//**Must make sure that the file written at ahe_tmp is unique and does not overwrite
		//**that file must be delted once staging is complete

		
		try {
			
			
			String unique_file = createUniqueTmpFileName();
			
			FileStaging stage_in_tmp = new FileStaging();
			stage_in_tmp.setStage_in(false);
			stage_in_tmp.setSource(transfer.getSource());
			stage_in_tmp.setTarget("file://" +unique_file);
			stage_in_tmp.setUser(transfer.getUser());
			stage_in_tmp.setTimestamp(new Date());
			stage_in_tmp.setActive(1);
			
			
			AHEMessage msg = TransferHandler.transfer(new FileStaging[]{stage_in_tmp});
			
			if(AHEMessageUtility.hasException(msg)){		
				dispose(false);
				return;
			}
			
			//Transfer Job ID
//			stage_in_tmp.setIdentifier(msg.getInformation()[0]);
			
			//Poll and wait for transfer in to complete and then proceed to transfer out
			
			do{
				
				AHEMessage pollmsg = TransferHandler.transfer_status(new FileStaging[]{stage_in_tmp});
				
				if(AHEMessageUtility.hasException(pollmsg)){		
					dispose(false);
					return;
				}
				
				AHEPollStatus status = AHEPollStatus.valueOf(pollmsg.getInformation()[0]);
				
				if(status == AHEPollStatus.DONE){
					stage_in_tmp.setStatus(1);
					HibernateUtil.SaveOrUpdate(stage_in_tmp);
					break;
				}else if(status == AHEPollStatus.PENDING){
					Thread.sleep(30 * 1000);
					continue;
				}else{
					stage_in_tmp.setStatus(-1);
					HibernateUtil.SaveOrUpdate(stage_in_tmp);
					dispose(false);
					return;
				}
				
			}while(true);
			

			//Stage out
			FileStaging stage_out_tmp = new FileStaging();
			stage_out_tmp.setStage_in(true);
			stage_out_tmp.setSource("file://"+unique_file);
			stage_out_tmp.setTarget(transfer.getTarget());
			stage_out_tmp.setActive(1);
			stage_out_tmp.setTimestamp(new Date());
			stage_out_tmp.setUser(transfer.getUser());

			AHEMessage out_msg = TransferHandler.transfer(new FileStaging[]{stage_out_tmp});
			
			if(AHEMessageUtility.hasException(out_msg)){
				destroyUniqueTmpFileName(unique_file);
				dispose(false);
				return;
			}
			
//			stage_out_tmp.setIdentifier(out_msg.getInformation()[0]);
			
			do{
				
				AHEMessage pollmsg = TransferHandler.transfer_status(new FileStaging[]{stage_out_tmp});
				
				if(AHEMessageUtility.hasException(pollmsg)){		
					dispose(false);
					return;
				}
				
				AHEPollStatus status = AHEPollStatus.valueOf(pollmsg.getInformation()[0]);
				
				if(status == AHEPollStatus.DONE){
					stage_out_tmp.setStatus(1);
					HibernateUtil.SaveOrUpdate(stage_out_tmp);
					break;
				}else if(status == AHEPollStatus.PENDING){
					Thread.sleep(30 * 1000);
					continue;
				}else{
					destroyUniqueTmpFileName(unique_file);
					stage_out_tmp.setStatus(-1);
					HibernateUtil.SaveOrUpdate(stage_out_tmp);
					dispose(false);
					return;
				}
				
			}while(true);
			
			destroyUniqueTmpFileName(unique_file);
			
			//once completed, de-reference transfer
			dispose(true);
			return;
			
		} catch (Exception e) {
			e.printStackTrace();
			dispose(false);
			return;
		}

	}
	
	private void dispose(boolean succeed){
	
		transfer.setStatus(succeed ? 1 : -1);
		HibernateUtil.SaveOrUpdate(transfer);
		
		this.transfer = null;
		
	
		
	}
	
	private static String createUniqueTmpFileName() throws AHEDataTransferException{
		
		String tmp_folder = AHERuntime.getAHERuntime().get_AHETmpFolder();
		
		if(tmp_folder == null || tmp_folder.isEmpty()){

			throw new AHEDataTransferException("2-part transfer failed. No tmp folder specified in AHE conifgurationfile under ahe.tmp parameter");

		}
		
		if(!tmp_folder.endsWith("/"))
			
			tmp_folder += "/";
		
		boolean found = false;
		String unique = "";
		
		while(!found){
			
			unique = UUID.randomUUID().toString();
			
			File check = new File(tmp_folder+unique);
			
			if(!check.exists()){
				return tmp_folder+unique;
			}
			
		}

		throw new AHEDataTransferException("2-part transfer failed. Unable to create unique file name");
		
	}
	
	private static boolean destroyUniqueTmpFileName(String fullpath){
		File f1 = new File(fullpath);
		return f1.delete();
		
	}
}

