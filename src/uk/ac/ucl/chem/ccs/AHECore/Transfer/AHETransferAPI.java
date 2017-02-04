package uk.ac.ucl.chem.ccs.AHECore.Transfer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import uk.ac.ucl.chem.ccs.AHECore.Engine.ResourceRegisterAPI;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEDataTransferException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.PlatformCredentialException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.FileStaging;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.AHEMessageUtility;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.TransferHandler;
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.AHEMessage;


/**
 * This class implements the 2-stage data transfer mechanism.
 * 
 * AHE Data Transfer either pass the original filestaging obejct to the underlying protocl impl. transfer class or create
 * new filestaging objects to temporary transfer a file using one protocol into the AHE server and send it out using another
 * protocol 
 * 
 * @author davidc
 *
 */

public class AHETransferAPI {

	/**
	 * Get all file transfer entity belonging to an appinstance
	 * @param app_inst
	 * @return
	 */
	
	public static FileStaging[] getFileTransfer(AppInstance app_inst){
		
		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();

			Query query = session
					.createQuery("select ap from FileStaging ap where ap.active = :active and ap.appinstance.id = :app_id");
			query.setParameter("active", 1);
			query.setParameter("app_id", app_inst.getId());
			List r = query.list();

			FileStaging[] array = (FileStaging[]) r
					.toArray(new FileStaging[r.size()]);

			session.flush();
			txn.commit();

			session.close();

			//HibernateUtil.getSessionFactory().close();

			return array;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new FileStaging[0];
		
	}
	
	/**
	 * Return all file staging entities
	 * @return
	 */
	
	public static FileStaging[] getAllFileTransfers(){
		
		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();

			Query query = session
					.createQuery("select ap from FileStaging ap where ap.active = :active");
			query.setParameter("active", 1);
			List r = query.list();

			FileStaging[] array = (FileStaging[]) r
					.toArray(new FileStaging[r.size()]);

			session.flush();
			txn.commit();

			session.close();

			//HibernateUtil.getSessionFactory().close();

			return array;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new FileStaging[0];
		
	}
	
	/**
	 * Return an ordered list of FileStaging array ordered by column.
	 * @param user
	 * @param column_to_order Column to be ordered
	 * @param asc true to order by ascending or false by descending order
	 * @param limit_to limit result count
	 * @return
	 */
	
	public static FileStaging[] getFileTransferQuery(AHEUser user, String column_to_order, boolean asc, int limit_to){
		
		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();

			String query_str = "select ap from FileStaging ap where ap.active = :active AND ap.user.id = :user_id ";
			
	        if(column_to_order != null){
	        	
        		query_str += "order by ap."+column_to_order;
        		
        		if(asc){
        			query_str += " asc ";
        		}else
        			query_str += " desc ";
	        
	        	
	        }
			
			Query query = session.createQuery(query_str);
			query.setParameter("active", 1);
			query.setParameter("user_id", user.getId());
			
	        if(limit_to != -1){
	        	query.setMaxResults(limit_to);
	        }
			
			List r = query.list();

			FileStaging[] array = (FileStaging[]) r
					.toArray(new FileStaging[r.size()]);

			session.flush();
			txn.commit();

			session.close();

			//HibernateUtil.getSessionFactory().close();

			return array;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new FileStaging[0];
		
	}
	
	/**
	 * Return file transfer belong to a specific user
	 * @param user
	 * @return
	 */
	
	public static FileStaging[] getFileTransfer(AHEUser user){
	
		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();

			Query query = session
					.createQuery("select ap from FileStaging ap where ap.active = :active AND ap.user.id = :user_id");
			query.setParameter("active", 1);
			query.setParameter("user_id", user.getId());
			List r = query.list();

			FileStaging[] array = (FileStaging[]) r
					.toArray(new FileStaging[r.size()]);

			session.flush();
			txn.commit();

			session.close();

			//HibernateUtil.getSessionFactory().close();

			return array;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new FileStaging[0];
		
	}
	
	/**
	 * Return file staging entity by id
	 * @param file_id
	 * @return
	 */
	
	public static FileStaging getFileTransfer_admin(int file_id){
		
		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();

			Query query = session
					.createQuery("select ap from FileStaging ap where ap.active = :active and ap.id = :id");
			query.setParameter("active", 1);
			query.setParameter("id", file_id);
			
			List r = query.list();

			FileStaging[] array = (FileStaging[]) r
					.toArray(new FileStaging[r.size()]);

			session.flush();
			txn.commit();

			session.close();

			//HibernateUtil.getSessionFactory().close();

			if(array.length > 0)
			
				return array[0];
			else
				return null;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * Return file transfer entity by user and ID
	 * @param user
	 * @param file_id
	 * @return
	 */
	
	public static FileStaging getFileTransfer(AHEUser user, int file_id){
		
		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();

			Query query = session
					.createQuery("select ap from FileStaging ap where ap.active = :active and ap.user.id = :user_id and ap.id = :id");
			query.setParameter("active", 1);
			query.setParameter("user_id", user.getId());
			query.setParameter("id", file_id);
			
			List r = query.list();

			FileStaging[] array = (FileStaging[]) r
					.toArray(new FileStaging[r.size()]);

			session.flush();
			txn.commit();

			session.close();

			//HibernateUtil.getSessionFactory().close();

			if(array.length > 0)
			
				return array[0];
			else
				return null;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 2-stage transfer if 
	 * 1. scheme from both src and target are different and neither of them are file
	 * 2. webdav/http to webdav/http transfer
	 * 3. Same scheme but different credential attached
	 * 
	 * @param transfer
	 * @return
	 * @throws AHEDataTransferException
	 * @throws URISyntaxException
	 * @throws PlatformCredentialException 
	 */
	
	public static boolean isTwoStageTransfer(FileStaging transfer) throws AHEDataTransferException, URISyntaxException, PlatformCredentialException{
		
		URI uri_from = new URI(transfer.getSource());
		URI uri_to = new URI(transfer.getTarget());
		
		//Same scheme type but different host
		//Check if both host/resource uses the same credentials, if not we need to do an intemediary transfer
		Resource resource1 = ResourceRegisterAPI.getResourceByBestMatch(uri_from);
		Resource resource2 = ResourceRegisterAPI.getResourceByBestMatch(uri_to);

		
		if(resource1 == null || resource2 == null){
			throw new AHEDataTransferException("Resource is not properly setup/found in AHE : (" + uri_from + "," + uri_to + ")");
		}
		
		if(uri_from.getScheme().equalsIgnoreCase("file") || uri_to.getScheme().equalsIgnoreCase("file"))
			return false;
		
		if(uri_from.getScheme().equalsIgnoreCase(uri_to.getScheme())){
			
			if(uri_from.getScheme().equalsIgnoreCase("webdav") || uri_to.getScheme().equalsIgnoreCase("http"))
				return true;
			
			AHEUser owner = null;
			
			if(transfer.getAppinstance() != null){
				owner = transfer.getAppinstance().getOwner();
			}else{
				owner = transfer.getUser();
			}
			
			PlatformCredential credential1 = SecurityUserAPI.getUserPlatformCredential(owner, resource1);
			PlatformCredential credential2 = SecurityUserAPI.getUserPlatformCredential(owner, resource2);
			
			if(credential1 == null || credential2 == null){
				throw new AHEDataTransferException("Credential is not properly setup/found in AHE : (" + uri_from + "," + uri_to + ")");
			}
			
			if(credential1.getId() == credential2.getId()){
				return false;
			}else
				return true;
			
		}else{
			return true;
		}

	}
	
	/**
	 * Other process should check the filetransfer status to see if the transfer has succeeded , failed or pending
	 * @param filetransfer
	 * @throws AHEDataTransferException
	 * @throws PlatformCredentialException
	 * @throws URISyntaxException
	 */
	
	public static void transfer(FileStaging filetransfer) throws AHEDataTransferException, PlatformCredentialException, URISyntaxException{
		
		URI uri_from = new URI(filetransfer.getSource());
		URI uri_to = new URI(filetransfer.getTarget());
		
		//Same scheme type but different host
		//Check if both host/resource uses the same credentials, if not we need to do an intemediary transfer
		Resource resource1 = ResourceRegisterAPI.getResourceByBestMatch(uri_from);
		Resource resource2 = ResourceRegisterAPI.getResourceByBestMatch(uri_to);

		
		if(resource1 == null || resource2 == null){
			throw new AHEDataTransferException("Resource is not properly setup/found in AHE : (" + uri_from + "," + uri_to + ")");
		}
	
		AHEUser owner = null;
		
		if(filetransfer.getAppinstance() != null){
			owner = filetransfer.getAppinstance().getOwner();
		}else{
			owner = filetransfer.getUser();
		}

		if(uri_from.getScheme().equalsIgnoreCase(uri_to.getScheme())){
			
			PlatformCredential credential1 = SecurityUserAPI.getUserPlatformCredential(owner, resource1);
			PlatformCredential credential2 = SecurityUserAPI.getUserPlatformCredential(owner, resource2);
			
			if(credential1 == null || credential2 == null){
				throw new AHEDataTransferException("Credential is not properly setup/found in AHE : (" + uri_from + "," + uri_to + ")");
			}
			
			if(credential1.getId() == credential2.getId()){


				try {
					
					AHEMessage msg = TransferHandler.transfer(new FileStaging[]{filetransfer});
					
					if(AHEMessageUtility.hasException(msg)){
						
						String err = "";
						
						if(msg.getException() != null){
							err = msg.getException()[0];
						}
						
						throw new AHEDataTransferException("Transfer failed : " + err);
					}
					
//					filetransfer.setIdentifier(msg.getInformation()[0]);
//					HibernateUtil.SaveOrUpdate(filetransfer);
					
					poll_transfer poll = new poll_transfer(filetransfer);
					new Thread(poll).start();
					
					return;
					
				} catch (Exception e) {
					throw new AHEDataTransferException(e);
				}
				
				
				
			}else{
			
				two_part_transfer runnable = new two_part_transfer(filetransfer);
				new Thread(runnable).start();
				return;
			}

			
		}else{
			
			//different scheme, may need to use intermediary file transfer method
			//** Note file://blah to gisfto:// are direct transfers, only different schemes not involving file:// requires 2-part transfers
			
			//Same scheme type but different host
			//Check if both host/resource uses the same credentials, if not we need to do an intemediary transfer
			
			if(uri_from.getScheme().equalsIgnoreCase("file") || uri_to.getScheme().equalsIgnoreCase("file")){
				
				
				try {
					AHEMessage msg = TransferHandler.transfer(new FileStaging[]{filetransfer});
					
					if(AHEMessageUtility.hasException(msg)){
						
						String err = "";
						
						if(msg.getException() != null){
							err = msg.getException()[0];
						}
						
						throw new AHEDataTransferException("Transfer failed : " + err);
					}
					
//					filetransfer.setIdentifier(msg.getInformation()[0]);
//					HibernateUtil.SaveOrUpdate(filetransfer);
					
					poll_transfer poll = new poll_transfer(filetransfer);
					new Thread(poll).start();
					
					return;
					
				} catch (Exception e) {
					throw new AHEDataTransferException(e);
				}

				
			}else if(uri_from.getScheme().equalsIgnoreCase("file") && uri_to.getScheme().equalsIgnoreCase("file")){
				//Will Not support this functionality
				return;
			}else{
				
				//Different transfer protocols, use 2-stage transfer
				
				PlatformCredential credential1 = SecurityUserAPI.getUserPlatformCredential(owner, resource1);
				PlatformCredential credential2 = SecurityUserAPI.getUserPlatformCredential(owner, resource2);
				
				if(credential1 == null || credential2 == null){
					throw new AHEDataTransferException("Credential is not properly setup/found in AHE : (" + uri_from + "," + uri_to + ")");
				}
				two_part_transfer runnable = new two_part_transfer(filetransfer);
				new Thread(runnable).start();
				
			}
		}

	}

	

	
	
	

	
}

