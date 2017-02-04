package uk.ac.ucl.chem.ccs.AHECore.Engine;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.helper.Executable;
import test.helper.Helper;
import uk.ac.ucl.chem.ccs.AHECore.Definition.AppInstanceStates;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AppInstanceAlreadyExistsException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEJobSubmission;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEWorkflow;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstanceArg;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstanceCmds;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppRegistery;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppRegisteryArgTemplate;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.FileStaging;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.AHEMessageUtility;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.JobSubmissionHandler;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.WorkflowAPI;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.AHEMessage;



/**
 * The AHE Engine contains the main AHE logic to launch/monitor/control the application (AppInstance) and workflows. It will provide a set of APIs
 * to modify how these behavior can be extended or altered. This class is the main entry point to access the other classes in this package
 * for AHE 3.0.
 * 
 * 
 * 
 * The main states of AHE Instances occurs at:
 * - Create Workflow (started)
 * - Submit Staging Information
 * - Submit Job
 * - Polling
 * - Waiting
 * - Completed (Suceed or fail)
 * 
 * 
 * @author davidc
 *
 */

public class AHEEngine {

	private static Logger logger = LoggerFactory.getLogger(AHEEngine.class);
	
	public static void main(String[] arg){
		
//		try{
//		
//			//Basic Test workflow, make sure AHE DB is populated and workflow can be run
//			
//			AHEEngine engine = AHERuntime.getAHERuntime().getAhe_engine();
//			
//			//Create Test App Register and test search function
//			AppRegistery reg = APPRegisteryAPI.createAppRegistery("ls", "ngs.rl.ac.uk", "/bin/ls","globus");
//			AppRegistery reg1 = APPRegisteryAPI.createAppRegistery("echo", "ngs.rl.ac.uk", "/bin/echo","globus");
//			AppRegistery reg2 = APPRegisteryAPI.createAppRegistery("AHESORT", "ngs.rl.ac.uk", "./ahe_sort.pl","globus");
//			long reg_id = reg.getId();
//			
//			AppRegistery[] list = APPRegisteryAPI.getApplicationList();
//			
//			
//			System.out.println("App Registery List : " + list.length);
//			
//			for(AppRegistery r : list){
//						System.out.println(r.toString());	
//			}
//			
//			System.out.println("");
//			
//			AppRegistery search = APPRegisteryAPI.getApplication("AHESORT");
//			System.out.println(search.getId());
//			
//			//Prepare and init a workflow
//			
////			AppInstance inst = engine.createAppInstance("Test simulation", search);
//			
//			AppInstance inst = (AppInstance) AHE_API.Prepare(search.getAppname(),"Test simulation", new String[]{rest_arg.arg.toString(),"config.txt"});
//			
//			long app_inst_id = inst.getId();
//			
//			AppInstance search_inst = engine.getAppInstanceEntity(app_inst_id);
//			System.out.println("Instance : " + search_inst.getId() + " using platform : " + search_inst.getAppreg().getResource_code());
//			engine.startWorkflow(app_inst_id);
//			
//			//Set Staging
//			//engine.setFileStageLocation(app_inst_id, "file:////home/davidc/input.txt","gsiftp://ngs.rl.ac.uk//home/ngs0891/input.txt", "","");
//			
//			engine.setFileStageLocation(app_inst_id, new String[0],new String[0], new String[0],new String[0]);
//			search_inst = engine.getAppInstanceEntity(app_inst_id);
//	
//			//Singal File Config done 
//			engine.SignalFileStageConfigurationComplete(app_inst_id);
//			
//			//Submit signal		
//			String[] parameters = {};
//			
//			engine.SignalSubmitCommand(app_inst_id, parameters);
//
//		}catch(AHEException e){
//			e.printStackTrace();
//		} catch (AppAlreadyExistException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (AppInstanceAlreadyExistsException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (AppNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	//Testing only
	//private String workflow_id = "AHEWorkflow";
	//private String workflow_path = "AHEWorkflow.bpmn";
	
	private String workflow_id = "AwsWorkflow";
	private String workflow_path = "main/resources/AwsWorkflow.bpmn";
	
	private WorkflowAPI workflowHandler;
	
	public AHEEngine(){
		
		setWorkflowHandler(new WorkflowAPI());
		getWorkflowHandler().setParent(this);
	}
	
	/**
	 * Clone a copy of an existing AppInstance and put it back in the init wait state
	 * 
	 * @param app_inst_id
	 * @return
	 * @throws AHEException 
	 * @throws AppInstanceAlreadyExistsException 
	 */
	
	public static AppInstance cloneAppInstance(long app_inst_id, String simname) throws AHEException, AppInstanceAlreadyExistsException{
		
		AppInstance toClone = getAppInstanceEntity(app_inst_id);
		
		/**
		 * Create a new App Instance Object
		 * Copy all the properties
		 */
		
		AppInstance cloned = createAppInstance(toClone.getOwner(), simname, toClone.getAppreg());
		
		AHERuntime.getAHERuntime().getAhe_engine().startWorkflow(cloned.getId(), true);
		
		AppInstanceArg[] arg = getAppInstanceArgument(toClone.getId());
		
		for(AppInstanceArg toCopy : arg){
			
			createAppInstanceArg(cloned.getId(), new String[]{toCopy.getArg(),toCopy.getValue()});
			
		}
		
		FileStaging[] files = getFileStageByAppInstId(toClone.getId());
		
		for(FileStaging f : files){
			
			createFileStaging(cloned, f.isStage_in(), f.getSource(), f.getTarget());
			
		}
		
		return cloned;
	}
	
	/**
	 * Creates an AHEWorkflow database entry. This maps an app instance to a JBPM workflow
	 * @param appinst application instance
	 * @param process_id JBPM process id
	 * @param ksession_id JBPM knowledge sesion id
	 * @param workflow_id JBPM workflow id
	 * @param workflow_path JBPM workflow file path
	 * @return
	 */
	
	public static AHEWorkflow createAHEWorkflowMapping(AppInstance appinst, long process_id, int ksession_id, String workflow_id, String workflow_path){
		
		AHEWorkflow mapping = new AHEWorkflow();
		mapping.setAppinstance(appinst);
		mapping.setProcess_id(process_id);
		mapping.setKsession_id(ksession_id);
		mapping.setActive(1);
		mapping.setWorkflow_id(workflow_id);
		mapping.setWorkflow_path(workflow_path);
		mapping.setTimestamp(new Date());
		
		/*Executable.writeout("appinst Id:" + appinst.getId()+
				"process_id: " + process_id+
				"ksession_id: " + ksession_id+
				"workflow id: " + workflow_id+
				"workflow path: " + workflow_path);*/
		HibernateUtil.SaveOrUpdate(mapping);
		
		return mapping;
	}
	
	/**
	 * Retrieve worfklow by process ID
	 * @param process_id
	 * @return
	 */
	
	public static AHEWorkflow getAHEWorkflowEntityByProcessId(long process_id){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();
	        
	        Transaction txn = session.beginTransaction();

	        Query query = session.createQuery("select ap from AHEWorkflow as where as.process_id = :process and as.active = 1");
	        query.setParameter("process", process_id);
	        List r = query.list();
	           
	        session.flush();
	        txn.commit();
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        if(r.size() == 0)
	        	return null;
	        else
	        	return (AHEWorkflow) r.get(0);
	        
	        
    	}catch (Exception e) { 
            e.printStackTrace();
        } 
		
		return null;
		
	}
	
	/**
	 * Get AHE workflow entity by application instance ID
	 * @param appinst_id application instance ID
	 * @return
	 */
	
	public static AHEWorkflow getAHEWorkflowEntityByAppInstId(long appinst_id){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();
	        
	        Transaction txn = session.beginTransaction();

	        Query query = session.createQuery("select map from AHEWorkflow map where map.appinstance.id = :appid and map.active = :active");
	        query.setParameter("appid", appinst_id);
	        query.setParameter("active", 1);
	        List r = query.list();
	        
	        //Executable.writeout("workflow is empty? " + r.isEmpty());
	           
	        session.flush();
	        txn.commit();
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        if(r.size() == 0)
	        	return null;
	        else
	        	return (AHEWorkflow) r.get(0);
	        
	        
    	}catch (Exception e) { 
            e.printStackTrace();
        } 
		
		return null;
		
	}
	
	/**
	 * Remove all of an application instance's file staging information
	 * @param appinst_id AppInst ID
	 */
	
	public static void removeFileStaging(long appinst_id){
		
		FileStaging[] transfer = getFileStageByAppInstId(appinst_id);
		
		for(FileStaging f : transfer){
			
			if(f.getStatus() <= 0){
				f.setActive(0);
				HibernateUtil.SaveOrUpdate(f);
			}
			
		}
		
	}
	
	/**
	 * Remove a specific application instance's transfer information
	 * @param appinst_id application instance ID
	 * @param transferid Transfer to be removed
	 */
	
	public static void removeFileStaging(long appinst_id, long transferid){
		
		FileStaging[] transfer = getFileStage(appinst_id,transferid);
		
		if(transfer != null){
			for(FileStaging f : transfer){
				
				if(f.getStatus() <= 0){
					f.setActive(0);
					HibernateUtil.SaveOrUpdate(f);
				}
				
			}
		}
		
	}
	
	/**
	 * Remove file stage in entity
	 * @param appinst_id
	 */
	
	public static void removeFileStageIn(long appinst_id){
		
		FileStaging[] transfer = getFileStageByAppInstId(appinst_id);
		
		for(FileStaging f : transfer){
			
			if(f.getStatus() <= 0 && f.isStage_in()){
				f.setActive(0);
				HibernateUtil.SaveOrUpdate(f);
			}
			
		}
		
	}
	
	/**
	 * remove file stage out entity
	 * @param appinst_id
	 */
	
	public static void removeFileStageOut(long appinst_id){
		
		FileStaging[] transfer = getFileStageByAppInstId(appinst_id);
		
		for(FileStaging f : transfer){
			
			if(f.getStatus() <= 0 && !f.isStage_in()){
				f.setActive(0);
				HibernateUtil.SaveOrUpdate(f);
			}
			
		}
		
	}
	
	/**
	 * Return an array of FileStage entity based on AppInst ID and Transfer ID
	 * @param appinst_id
	 * @param transferid
	 * @return
	 */
	
	public static FileStaging[] getFileStage(long appinst_id, long transferid){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();
	        
	        Transaction txn = session.beginTransaction();

	        Query query = session.createQuery("select en from FileStaging en where en.appinstance.id = :appid and en.active = :active and en.id = :tranid");
	        query.setParameter("appid", appinst_id);
	        query.setParameter("active", 1);
	        query.setParameter("tranid", transferid);
	        List r = query.list();
	           
	        session.flush();
	        txn.commit();
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();

        	return (FileStaging[]) r.toArray(new FileStaging[r.size()]);
	        
	        
    	}catch (Exception e) { 
            e.printStackTrace();
        } 
		
		return new FileStaging[0];
		
	}
	
	/**
	 * Return File Stage array by AppInst ID
	 * @param appinst_id
	 * @return
	 */
	
	public static FileStaging[] getFileStageByAppInstId(long appinst_id){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();
	        
	        Transaction txn = session.beginTransaction();

	        Query query = session.createQuery("select en from FileStaging en where en.appinstance.id = :appid and en.active = :active");
	        query.setParameter("appid", appinst_id);
	        query.setParameter("active", 1);
	        List r = query.list();
	           
	        session.flush();
	        txn.commit();
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();

        	return (FileStaging[]) r.toArray(new FileStaging[r.size()]);
	        
	        
    	}catch (Exception e) { 
            e.printStackTrace();
        } 
		
		return new FileStaging[0];
		
	}
	
	/**
	 * Retrieve a FileStage array on stage in information of an application instance
	 * @param appinst_id AppInst ID
	 * @return
	 */
	
	public static FileStaging[] getFileStageInByAppInstId(long appinst_id){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();
	        
	        Transaction txn = session.beginTransaction();

	        Query query = session.createQuery("select en from FileStaging en where en.stage_in = :stagein and en.appinstance.id = :appid and en.active = :active");
	        query.setParameter("appid", appinst_id);
	        query.setParameter("active", 1);
	        query.setParameter("stagein", true);
	        List r = query.list();
	           
	        session.flush();
	        txn.commit();
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();

        	return (FileStaging[]) r.toArray(new FileStaging[r.size()]);
	        
	        
    	}catch (Exception e) { 
            e.printStackTrace();
        } 
		
		return null;
		
	}
	
	/**
	 * Retrieve a FileStage array on stage out information of an application instance
	 * @param appinst_id AppInst ID
	 * @return
	 */
	
	public static FileStaging[] getFileStageOutByAppInstId(long appinst_id){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();
	        
	        Transaction txn = session.beginTransaction();

	        Query query = session.createQuery("select en from FileStaging en where en.stage_in = :stagein and en.appinstance.id = :appid and en.active = :active");
	        query.setParameter("appid", appinst_id);
	        query.setParameter("active", 1);
	        query.setParameter("stagein", false);
	        List r = query.list();
	           
	        session.flush();
	        txn.commit();
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();

        	return (FileStaging[]) r.toArray(new FileStaging[r.size()]);
	        
	        
    	}catch (Exception e) { 
            e.printStackTrace();
        } 
		
		return new FileStaging[0];
		
	}
	
	/**
	 * Get AHE Workflow entity
	 * @param app_inst AppInst entity
	 * @return
	 */
	
	public static AHEWorkflow getAHEWorkflowEntity(AppInstance app_inst){
		
    	return getAHEWorkflowEntityByAppInstId(app_inst.getId());
		
	}
	
	/**
	 * Create an AppInstance entity
	 * @param owner Owner
	 * @param simname simulation name
	 * @param appreg Application registry
	 * @return AppInstance entity
	 * @throws AppInstanceAlreadyExistsException
	 */
	
	public static AppInstance createAppInstance(AHEUser owner, String simname, AppRegistery appreg ) throws AppInstanceAlreadyExistsException{
		
		AppInstance app = getAppInstanceEntity(simname);
		
		if(app != null){
			throw new AppInstanceAlreadyExistsException("Application Instance already exists for : " + simname);
		}
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();	        
	        Transaction txn = session.beginTransaction();
	        


	        AppInstance appstate = new AppInstance();
	        
	        appstate.setOwner(owner);
	        appstate.setSimname(simname);
	        appstate.setAppreg(appreg);
	        appstate.setState(AppInstanceStates.Initalising.toString()); //Default state. The workflow must advance the state for checks (i.e. start can only be commend at Wait_UserCmd
	        appstate.setActive(1);
	        appstate.setTimestamp(new Date());
	        
	        session.saveOrUpdate(appstate);
	        
	        session.flush();
	        txn.commit();
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        return appstate;
	        
	        
    	}catch (Exception e) { 
            e.printStackTrace();
        } 
	
    	return null;
	}
	

	/**
	 * Create AppInstanceArg entity. Parameters are in the form of argument -> value. Value can be empty or an json object array in string format
	 * 
	 *       
	 * @param app_reg_id
	 * @param arg
	 * @return
	 */
	
	public static void createAppRegisteryArgTemplate(long app_reg_id, String[] arg) throws AHEException{
		
		if(arg.length%2 != 0){
			ThrowError("Parameter Length incorrect, should be even","createAppRegisteryArgTemplate()");
		}
	
		AppRegistery app_reg = AppRegisteryAPI.getApplication(app_reg_id);
		
		if(app_reg == null){
			ThrowError("App Registery not found","createAppRegisteryArgTemplate()");
		}
		
		
		for(int i=0; i < arg.length; i = i+2){
			
			String a1 = arg[i];
			String value = arg[i+1];
			
			AppRegisteryArgTemplate arg_entity = new AppRegisteryArgTemplate();
			arg_entity.setAppreg(app_reg);
			arg_entity.setArg(a1);
			arg_entity.setValue(value);
			arg_entity.setActive(1);
			arg_entity.setTimestamp(new Date());
			
			//HibernateUtil.SaveOrUpdate(arg_entity);
			
		}
		
	}
	
	
	/**
	 * Create AppInstanceArg entity. Parameters are in the form of argument -> value. Value can be empty or an json object array in string format
	 *       
	 * @param appinst_id
	 * @param arg
	 * @return
	 */

	public static void createAppInstanceArg(long app_inst_id, String[] arg) throws AHEException{
		
		if(arg.length%2 != 0){
			ThrowError("Parameter Length incorrect, should be even","createAppInstanceArg()");
		}
		
		AppInstance inst = getAppInstanceEntity(app_inst_id);
		
		if(inst == null){
			ThrowError("AppInstance not found","createAppInstanceArg()");
		}
		
		
		
		for(int i=0; i < arg.length; i = i+2){
			
			String a1 = arg[i];
			String value = arg[i+1];
			
			AppInstanceArg arg_entity = new AppInstanceArg();
			arg_entity.setAppinstance(inst);
			arg_entity.setArg(a1);
			arg_entity.setValue(value);
			arg_entity.setActive(1);
			arg_entity.setTimestamp(new Date());
			
			HibernateUtil.SaveOrUpdate(arg_entity);
			
		}
		
		
	}
	
	/**
	 * Get AppInst entity by ID
	 * @param id entity ID
	 * @return AppInstance entity
	 * @throws AHEException
	 */
	
	public static AppInstance getAppInstanceEntity(long id) throws AHEException{
	
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        


	        Query query = session.createQuery("select ai from AppInstance ai where ai.id = :id and ai.active = :active");
	        query.setParameter("id", id);
	        query.setParameter("active", 1);
	        List r = query.list();
	        
	     
	        
	        session.flush();
	        txn.commit();
	        session.close();
	        
	       // HibernateUtil.getSessionFactory().close();
        
	        if(r.size() == 0){
	        	Executable.writeout("Job Submission got instance id null.");
	        	return null;}
	        else
	        	return (AppInstance) r.get(0);
	        
	        
    	}catch (Exception e) { 
            e.printStackTrace();
            throw new AHEException("Appinstance not found for : " + id + " : " + e.getMessage());
        } 
		
		
	}
	
	/**
	 * Get AppInst entity by simulation name
	 * @param simname Simulation name
	 * @return AppInstance entity
	 */
	
	public static AppInstance getAppInstanceEntity(String simname){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        


	        Query query = session.createQuery("select ai from AppInstance ai where ai.simname = :simname and ai.active = :active");
	        query.setParameter("simname", simname);
	        query.setParameter("active", 1);
	        List r = query.list();

	        session.flush();
	        txn.commit();
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        if(r.size() == 0)
	        	return null;
	        else
	        	return (AppInstance) r.get(0);
	        
	        
    	}catch (Exception e) { 
            e.printStackTrace();
        } 
		
		return null;
		
	}
	
	/**
	 * Return an ordered array of AppInstance. THe array can be ordered based on the column, ascending or descending and limiting the return result
	 * @param owner AHE owner
	 * @param column_to_order Column to be ordered
	 * @param asc true to order by ascending or false to order by descending
	 * @param limit_to Limit the results of this search
	 * @return
	 */
	
	public static AppInstance[] getAppInstanceListQuery(String owner, String column_to_order, boolean asc, int limit_to){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        Transaction txn = session.beginTransaction();

	        String query_str = "select ai from AppInstance ai where ai.active = :active ";
	        
	        if(owner != null){
	        	query_str += "and ai.owner.username = '"+owner+"' ";
	        }
	        
	        if(column_to_order != null){
	        	
        		query_str += "order by ai."+column_to_order;
        		
        		if(asc){
        			query_str += " asc ";
        		}else
        			query_str += " desc ";
	        	
	        }
	        

	        
	        Query query = session.createQuery(query_str);
	        query.setParameter("active", 1);
	        
	        if(limit_to != -1){
	        	query.setMaxResults(limit_to);
	        }
	        
	        List r = query.list();

	        session.flush();
	        txn.commit();
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
        	return (AppInstance[]) r.toArray(new AppInstance[r.size()]);
	        
	        
    	}catch (Exception e) { 
            e.printStackTrace();
        } 
		
		return new AppInstance[0];
		
	}
	
	/**
	 * Return all Application Instance list
	 * @return
	 */
	
	public static AppInstance[] getAppInstanceList(){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();

	        Query query = session.createQuery("select ai from AppInstance ai where ai.active = :active");
	        query.setParameter("active", 1);
	        List r = query.list();

	        session.flush();
	        txn.commit();
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
        	return (AppInstance[]) r.toArray(new AppInstance[r.size()]);
	        
	        
    	}catch (Exception e) { 
            e.printStackTrace();
        } 
		
		return new AppInstance[0];
		
	}
	
	/**
	 * Return all the application instance started by this owner
	 * @param owner AHE user
	 * @return
	 */
	
	public static AppInstance[] getAppInstanceList(String owner){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();

	        Query query = session.createQuery("select ai from AppInstance ai where ai.active = :active and ai.owner.username = :owner");
	        query.setParameter("active", 1);
	        query.setParameter("owner", owner);
	        List r = query.list();

	        session.flush();
	        txn.commit();
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
        	return (AppInstance[]) r.toArray(new AppInstance[r.size()]);
	        
	        
    	}catch (Exception e) { 
            e.printStackTrace();
        } 
		
		return new AppInstance[0];
		
	}
	
	/**
	 * Return the current state of the application instance
	 * @param id AppInst ID
	 * @return AppInstance state
	 * @throws AHEException
	 */
	
	public static String getCurrentAppInstanceState(long id) throws AHEException{
		
		AppInstance s = getAppInstanceEntity(id);
		
		if(s == null){
			ThrowError("Application Instance not found", "AHEEngine.getCurrentAppInstanceState");
			return "ERROR : Invalid App Instance ID";
		}else
			return s.getState();
		
	}
	
	/**
	 * Set the current state of the application instance and any additonal information
	 * @param id AppInst ID
	 * @param new_state New state
	 * @param info information
	 * @return
	 * @throws AHEException
	 */
	
	public static AppInstance setCurrentAppInstanceState(long id, String new_state, String info) throws AHEException{
		
		AppInstance s = getAppInstanceEntity(id);
		
		if(s == null)
			return null;
		
		s.setState(new_state);
		s.setState_info(info);
		HibernateUtil.SaveOrUpdate(s);
		
		return s;
	}
	
	/**
	 * Set an external JOB ID to this application instance
	 * @param app_inst AppInst ID
	 * @param job_id JOB ID related to this application instance
	 */
	
	public static void setAppInstanceJobID(AppInstance app_inst, String job_id){
		
		app_inst.setSubmit_job_id(job_id);
		HibernateUtil.SaveOrUpdate(app_inst);
		
	}
	
	/**
	 * Suspend this application instance
	 * @param id
	 * @throws AHEException
	 */
	
	public static void suspendAppInstance(long id) throws AHEException{
		
		AppInstance s = getAppInstanceEntity(id);
		
		if(s == null)
			return;
	
		if(s.getState().equalsIgnoreCase(AppInstanceStates.Wait_UserCmd.toString())){
			
			s.setState(AppInstanceStates.Suspended_Wait_UserCmd.toString());
			HibernateUtil.SaveOrUpdate(s);
			
		}else if(s.getState().equalsIgnoreCase(AppInstanceStates.Polling_CheckResource.toString())){
			
			s.setState(AppInstanceStates.Suspended_Polling.toString());
			HibernateUtil.SaveOrUpdate(s);
			
		}
		
	}
	
	/**
	 * Unsuspend this application instance
	 * @param id
	 * @throws AHEException
	 */
	
	public void unSuspendAppInstance(long id) throws AHEException{
		
		AppInstance s = getAppInstanceEntity(id);
		
		if(s == null)
			return;
	
		if(s.getState().equalsIgnoreCase(AppInstanceStates.Suspended_Wait_UserCmd.toString())){
			
			s.setState(AppInstanceStates.Wait_UserCmd.toString());
			HibernateUtil.SaveOrUpdate(s);
			
		}else if(s.getState().equalsIgnoreCase(AppInstanceStates.Suspended_Polling.toString())){
			
			s.setState(AppInstanceStates.Polling_CheckResource.toString());
			HibernateUtil.SaveOrUpdate(s);
			
		}
		
	}
	
	/**
	 * 
	 * Multiple input or output can use comma to separate the file names i.e. url1,url2,url3 etc etc etc, both source and destination array size must match
	 * 
	 * @param app_id Application Instance ID
	 * @param stage_in_src Stage in source URI
	 * @param stage_in_dest Stage in desintation URI
	 * @param stage_out_src Stage out source URI
	 * @param stage_out_dest Stage out destination URI
	 */
	
	public void setFileStageLocation(long app_id, String[] stage_in_src, String[] stage_in_dest, String[] stage_out_src, String[] stage_out_dest) throws AHEException{
		
		AppInstance inst = getAppInstanceEntity(app_id);
		
		if(inst == null){
			ThrowError("AppInstance not found @ setFileStageLocation");
			return;
		}

		for(int i=0; i < stage_in_src.length; i++){
			
			String target = "";
			
			if(stage_in_src.length == stage_in_dest.length)
				target = stage_in_dest[i];
			else if(stage_in_dest.length == 1)
				target = stage_in_dest[0];
			else{
				ThrowError("File staging array length error @ setFileStageLocation");
				return;
			}
				
			createFileStaging(inst, true, stage_in_src[i], target);
			
		}
		
		for(int i=0; i < stage_out_src.length; i++){
			
			String target = "";
			
			if(stage_out_src.length == stage_out_dest.length)
				target = stage_out_dest[i];
			else if(stage_out_dest.length == 1)
				target = stage_out_dest[0];
			else{
				ThrowError("File staging array length error @ setFileStageLocation");
				return;
			}
				
			createFileStaging(inst, false, stage_out_src[i], target);
			
		}
		
		return;
		
	}
	
	/**
	 * Create a file transfer
	 * @param owner AHE owner
	 * @param transfer_syntax Double String array [[src URI],[dest URI]]
	 * @return FileStaging entity array
	 * @throws AHEException
	 */
	
	public FileStaging[] createFileTransfer(AHEUser owner, String[][] transfer_syntax) throws AHEException{
		
		FileStaging[] result = new FileStaging[transfer_syntax[0].length];
		
		for(int i=0; i < transfer_syntax[0].length; i++){

			result[i] = createFileStaging(owner, transfer_syntax[0][i], transfer_syntax[1][i]);
			
		}
		
		return result;
		
	}
	
	/**
	 * Get AppInstance workflow state. This state is based on the JBPM workflow state.
	 * @param app_id
	 * @return
	 * @throws AHEException
	 */
	
	public int getAppInstanceWorkflow_State(long app_id) throws AHEException{
		
		AHEWorkflow mapping = getAHEWorkflowEntityByAppInstId(app_id);
		
		if(mapping == null){
			ThrowError("No Mapping found for app_id : "+app_id, "getAppInstanceWorkflowStatus()");
			return -1;
		}
		
		return getWorkflowHandler().getWorkflowJBPMState(app_id, mapping.getProcess_id());
		
	}
	
	/**
	 * Return the current JBPM node on this AppInstance workflow
	 * @param app_id
	 * @return
	 * @throws AHEException
	 */
	
	public String getAppInstanceWorkflow_CurrentNode(long app_id) throws AHEException{
		
		AHEWorkflow mapping = getAHEWorkflowEntityByAppInstId(app_id);
		
		if(mapping == null){
			ThrowError("No Mapping found for app_id : "+app_id, "getAppInstanceWorkflowStatus()");
			return "ERROR";
		}
		
		return getWorkflowHandler().getCurrnetNode(app_id, mapping.getProcess_id());
		
	}
	
	
	/**
	 * Start this application instance workflow
	 * @param app_id AppInst ID
	 */
	
	public void startWorkflow(long app_id) throws AHEException{
		startWorkflow(app_id,false);		
	}
	
	/**
	 * Start an AppInstnace workflow
	 * @param app_id AppInstance ID
	 * @param persistance Whether this workflow will be persistent
	 * @throws AHEException
	 */
	
	public void startWorkflow(long app_id, boolean persistance) throws AHEException{

		long process_id = -1; 
		long session_id = -1;	
		
		if(persistance){
			//so far, here
			long[] info = getWorkflowHandler().createWorkflow(app_id,workflow_id, workflow_path);
			//Executable.writeout("after AHE_API Prepare engine startWorkflow persistance true");
			process_id = info[0];
			session_id = info[1];
			//Executable.writeout("new process id: " + process_id + "; session id: " + session_id);
			if(process_id == -1 || session_id == -1){
				ThrowError("JBPM Workflow creation failed", "startWorkflow()");
			}
			
		}else
		{
			//Executable.writeout("after AHE_API Prepare engine startWorkflow persistance false");
			process_id = getWorkflowHandler().createWorkflow_NoPersistance(app_id, workflow_id,workflow_path);
		}
		AppInstance inst = getAppInstanceEntity(app_id);
		
		if(inst == null){
			ThrowError("No AppInstance Entity Found for app_id : "+app_id, "startWorkflow()");
		}
		
		createAHEWorkflowMapping(inst, process_id, (int) session_id, workflow_id, workflow_path);
		
	}
	
	/**
	 * Start an application instance workflow
	 * @param app_id AppInst ID
	 * @param workflow_id Workflow to be used
	 * @param workflow_file Workflow file path
	 * @param persistance Whether this workflow will be persistent
	 * @throws AHEException
	 */
	
	public void startWorkflow(long app_id, String workflow_id, String workflow_file, boolean persistance) throws AHEException{
		
		
		long process_id = -1; 
		long session_id = -1;
		
		if(persistance){
			long[] info = getWorkflowHandler().createWorkflow(app_id,workflow_id, workflow_file);
			//Executable.writeout("here22222");
			process_id = info[0];
			session_id = info[1];
			
		}else
			//Executable.writeout("here");
			process_id = getWorkflowHandler().createWorkflow_NoPersistance(app_id, workflow_id, workflow_file);
		
		AppInstance inst = getAppInstanceEntity(app_id);
		
		if(inst == null){
			ThrowError("No AppInstance Entity Found for app_id : "+app_id, "startWorkflow()");
		}
		
		createAHEWorkflowMapping(inst, process_id, (int) session_id, workflow_id, workflow_file);
		
	}
	
//	/**
//	 * 
//	 * @param app_id
//	 */
//	
//	public void SignalFileStageConfigurationComplete(long app_id) throws AHEException{
//		
//		AHEWorkflow mapping = getAHEWorkflowEntityByAppInstId(app_id);
//		
//		if(mapping != null){
//		
//			if(mapping.getProcess_id() > -1){
//				getWorkflowHandler().FileStageConfigurationCompleteSignal(app_id,mapping.getProcess_id());
//				return;
//			}
//		}
//			
//		ThrowError("No App Instance Workflow mapping found", "SignalFileStageConfigurationComplete()");
//		
//	}
	
	/**
	 * In some workflows, a submit signal will have to be issued to submit the job.
	 * @param app_id AppInst ID
	 */
	
	public void SignalSubmitCommand(long app_id, String resource_name, String[] parameters) throws AHEException{
		
		AHEWorkflow mapping = getAHEWorkflowEntityByAppInstId(app_id);
		//System.out.println(mapping.getWorkflow_id());
		//return;
		//Executable.writeout("Signal Submit command process id "+ mapping.getProcess_id());
		if(mapping != null){
			
			AppInstance app_inst = getAppInstanceEntity(app_id);
			//return;
			//Executable.writeout("Signal Submit command app instance id "+ app_inst.getId());
			if(!app_inst.getState().equalsIgnoreCase(AppInstanceStates.Wait_UserCmd.toString())){
				//For the Start signal to commence, the AHE App Instance must be in the state of Wait_UserCmd
				//Executable.writeout("Signal Submit command error");
				ThrowError("App Instance is in an incorrect state to commence for execution. Current state : " + app_inst.getState(), "SignalSubmitCommand()");
				
			}
			
			
			if(mapping.getProcess_id() > -1){
				//Executable.writeout("Signal Submit command engine before workflow running state");
				//Update inst to Running
				AHEEngine.setCurrentAppInstanceState(app_id, AppInstanceStates.Workflow_Running.toString(),"Workflow Running");
				//Executable.writeout("Signal Submit command engine workflow running state");
				//Reload App_inst to latest state
				app_inst = getAppInstanceEntity(app_id);
				
				//Update to Correct App Resource
				AppRegistery final_app = AppRegisteryAPI.getApplication(app_inst.getAppreg().getAppname(), resource_name);
				
				if(final_app == null){
					ThrowError("Application: " + app_inst.getAppreg().getAppname() + " not found on resource: "+resource_name, "SignalSubmitCommand()");
					return;
				}
				
				//Update AppInstance on the correct App and Resource information
				app_inst.setAppreg(final_app);
				HibernateUtil.SaveOrUpdate(app_inst);
				
				//Setup Argument related to that app so the workflow looks for "arg" variable
				createAppInstanceArg(app_id, parameters);
				//Helper.writeout("Signal Submit command last step get process id......." + mapping.getProcess_id());
				//getWorkflowHandler().SubmissionSignal(app_id,mapping.getProcess_id());
				
				return;
			}
			
		}
		//Executable.writeout("workflow process id is -1");
			
		ThrowError("No App Instance Workflow mapping found for app_id : " + app_id, "SignalSubmitCommand()");
		
	}
	
	/**
	 * Create an Job Submission entity
	 * @param app_id AppInstance ID
	 * @param submission_id Submission ID
	 * @param status status
	 * @return
	 * @throws AHEException
	 */

	public static AHEJobSubmission createAHEJobSubmission(long app_id, String submission_id, String status) throws AHEException{
		
		AppInstance inst = AHEEngine.getAppInstanceEntity(app_id);
		
		if(inst == null){
			return null;
		}
		
		AHEJobSubmission entity = new AHEJobSubmission();
		entity.setActive(1);
		entity.setTimestamp(new Date());
		entity.setAppinstance(inst);
		entity.setSubmission_id(submission_id);
		entity.setStatus(status);
		entity.setLast_status_check(new Date());
		
		HibernateUtil.SaveOrUpdate(entity);
		
		return entity;
		
	}
	
	/**
	 * Create FileStaging Entity
	 * @param app_inst AppInstace that this file staging belongs to
	 * @param stage_in Whether this file staging is stage in or stage out
	 * @param source Source URI
	 * @param target Target URI
	 * @return
	 */
	
	public static FileStaging createFileStaging(AppInstance app_inst, boolean stage_in, String source, String target){
		
		FileStaging entity = new FileStaging();
		entity.setActive(1);
		entity.setTimestamp(new Date());
		entity.setAppinstance(app_inst);
		entity.setStage_in(stage_in);
		entity.setStatus(0);
		entity.setSource(source);
		entity.setTarget(target);
		
//		Resource r = ResourceRegisterAPI.getResource(resource_name);
//		
//		if(r != null)
//			entity.setResource(r);
		
		HibernateUtil.SaveOrUpdate(entity);
		
		return entity;
		
	}
	
	/**
	 * Create a File Staging that is not attached to an AppInstance
	 * @param owner AHE Owner
	 * @param source Source URI
	 * @param target Target URI
	 * @return
	 */
	
	public static FileStaging createFileStaging(AHEUser owner, String source, String target){
		
		FileStaging entity = new FileStaging();
		entity.setActive(1);
		entity.setTimestamp(new Date());
		entity.setAppinstance(null);
		entity.setStage_in(false);
		entity.setStatus(0);
		entity.setSource(source);
		entity.setTarget(target);
		entity.setUser(owner);
		
		HibernateUtil.SaveOrUpdate(entity);
		
		return entity;
		
	}
	
	/**
	 * TODO make sure its the latest cmd, sorted by date
	 * @param app_inst_id
	 */
	
	public static AppInstanceCmds getAppInstanceCmd(long app_inst_id){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        
	        Query query = session.createQuery("select cmd from AppInstanceCmds cmd where cmd.appinstance.id = :id and cmd.active = :active order by cmd.timestamp desc");
	        query.setParameter("id", app_inst_id);
	        query.setParameter("active", 1);
	        List r = query.list();

	        session.flush();
	        txn.commit();
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        if(r.size() == 0)
	        	return null;
	        else
	        	return (AppInstanceCmds) r.get(0);
	        
	        
    	}catch (Exception e) { 
            e.printStackTrace();
        } 
		
		return null;
		
	}
	
//	public static AppInstanceCmds[] getAppInstanceCmdHistory(long app_inst_id){
//		
//    	try{
//        	
//	        Session session = HibernateUtil.getSessionFactory().openSession();
//
//	        
//	        Transaction txn = session.beginTransaction();
//	        
//	        Query query = session.createQuery("select entity from AppInstanceCmds entity where entity.appinstance.id = :id AND entity.active = :active");
//	        query.setParameter("active", 1);
//	        query.setParameter("id", app_inst_id);
//	        List r = query.list();
//	        
//	        AppInstanceCmds[] array = (AppInstanceCmds[]) r.toArray(new AppInstanceCmds[r.size()]);
//	        
//	        session.flush();
//	        txn.commit();
//	        
//	        session.close();
//	        
//	       // HibernateUtil.getSessionFactory().close();
//        
//	        return array;
//	        
//    	}catch (Exception e) { 
//    		e.printStackTrace();
//        } 
//		
//		return new AppInstanceCmds[0];
//	}
	
//	/**
//	 * Get active AppInstance cmd for specific cmd order by the latest cmd issued
//	 * @param app_inst_id
//	 * @param cmd
//	 * @return
//	 */
//	
//	public static AppInstanceCmds[] getAppInstanceCmd(long app_inst_id, String cmd){
//    	
//		try{
//        	
//	        Session session = HibernateUtil.getSessionFactory().openSession();
//
//	        
//	        Transaction txn = session.beginTransaction();
//	        
//	        Query query = session.createQuery("select entity from AppInstanceCmds entity where entity.appinstance.id = :id AND entity.command = :cmd AND entity.active = :active order by entity.timestamp desc");
//	        query.setParameter("active", 1);
//	        query.setParameter("id", app_inst_id);
//	        query.setParameter("cmd", cmd);
//	        List r = query.list();
//	        
//	        AppInstanceCmds[] array = (AppInstanceCmds[]) r.toArray(new AppInstanceCmds[r.size()]);
//	        
//	        session.flush();
//	        txn.commit();
//	        
//	        session.close();
//	        
//	        //HibernateUtil.getSessionFactory().close();
//        
//	        return array;
//	        
//    	}catch (Exception e) { 
//    		e.printStackTrace();
//        } 
//		
//		return new AppInstanceCmds[0];
//	}
//	
//	public static AppRegisteryArgTemplate[] getAppRegisteryArgTemplate(long app_reg_id){
//		
//		try{
//        	
//	        Session session = HibernateUtil.getSessionFactory().openSession();
//
//	        
//	        Transaction txn = session.beginTransaction();
//	        
//	        Query query = session.createQuery("select entity from AppRegisteryArgTemplate entity where entity.appreg.id = :id AND entity.active = :active");
//	        query.setParameter("active", 1);
//	        query.setParameter("id", app_reg_id);
//	        List r = query.list();
//	        
//	        AppRegisteryArgTemplate[] array = (AppRegisteryArgTemplate[]) r.toArray(new AppRegisteryArgTemplate[r.size()]);
//	        
//	        session.flush();
//	        txn.commit();
//	        
//	        session.close();
//	        
//	        //HibernateUtil.getSessionFactory().close();
//        
//	        return array;
//	        
//    	}catch (Exception e) { 
//    		e.printStackTrace();
//        } 
//		
//		return new AppRegisteryArgTemplate[0];
//		
//	}
//	
//	public static AppRegisteryArgTemplate[] getAppRegisteryArgTemplate(long app_reg_id, String argname){
//		
//		try{
//        	
//	        Session session = HibernateUtil.getSessionFactory().openSession();
//
//	        
//	        Transaction txn = session.beginTransaction();
//	        
//	        Query query = session.createQuery("select entity from AppRegisteryArgTemplate entity where entity.arg = :arg AND entity.appreg.id = :id AND entity.active = :active");
//	        query.setParameter("active", 1);
//	        query.setParameter("arg", argname);
//	        query.setParameter("id", app_reg_id);
//	        List r = query.list();
//	        
//	        AppRegisteryArgTemplate[] array = (AppRegisteryArgTemplate[]) r.toArray(new AppRegisteryArgTemplate[r.size()]);
//	        
//	        session.flush();
//	        txn.commit();
//	        
//	        session.close();
//	        
//	        //HibernateUtil.getSessionFactory().close();
//        
//	        return array;
//	        
//    	}catch (Exception e) { 
//    		e.printStackTrace();
//        } 
//		
//		return new AppRegisteryArgTemplate[0];
//		
//	}
//	
//	public static AppRegisteryArgTemplate getAppRegisteryArgTemplate(long app_reg_id, String argname, String value){
//		
//		try{
//        	
//	        Session session = HibernateUtil.getSessionFactory().openSession();
//
//	        
//	        Transaction txn = session.beginTransaction();
//	        
//	        Query query = session.createQuery("select entity from AppRegisteryArgTemplate entity where entity.value = :value and entity.arg = :arg AND entity.appreg.id = :id AND entity.active = :active");
//	        query.setParameter("active", 1);
//	        query.setParameter("arg", argname);
//	        query.setParameter("value", value);
//	        query.setParameter("id", app_reg_id);
//	        List r = query.list();
//	        
//	        session.flush();
//	        txn.commit();
//	        
//	        session.close();
//	        
//	        //HibernateUtil.getSessionFactory().close();
//        
//	        if(r.size() > 0){
//	        	return (AppRegisteryArgTemplate) r.get(0);
//	        }else{
//	        	return null;
//	        }
//	        
//    	}catch (Exception e) { 
//    		e.printStackTrace();
//        } 
//		
//		return null;
//		
//	}
	
	/**
	 * Retrieve all arguments of a specific app instance
	 * @param app_inst_id AppInstance ID
	 * @return AppInstanceArg entity array
	 */
	
	public static AppInstanceArg[] getAppInstanceArgument(long app_inst_id){
		
		try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        
	        Query query = session.createQuery("select entity from AppInstanceArg entity where entity.appinstance.id = :id AND entity.active = :active order by entity.id asc");
	        query.setParameter("active", 1);
	        query.setParameter("id", app_inst_id);
	        List r = query.list();
	        
	        AppInstanceArg[] array = (AppInstanceArg[]) r.toArray(new AppInstanceArg[r.size()]);
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        return array;
	        
    	}catch (Exception e) { 
    		e.printStackTrace();
        } 
		
		return new AppInstanceArg[0];
	}
	
	/**
	 * Check whether an argument key exists in an application instance entity
	 * @param app_inst_id AppInst ID
	 * @param arg_key argument key to be searched for
	 * @return
	 */
	
	public static boolean isAppInstanceArgKeyExist(long app_inst_id, String arg_key){
		
		AppInstanceArg[] search = getAppInstanceArgument(app_inst_id, arg_key);
		
		return (search.length > 0);
		
	}
	
	/**
	 * 
	 * Return all AppInstanceArg entity which contains the specified argument key.
	 * 
	 * @param app_inst_id AppInst ID
	 * @param arg_key argument key
	 * @return
	 */
	
	public static AppInstanceArg[] getAppInstanceArgument(long app_inst_id, String arg_key){
		
		try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        
	        Query query = session.createQuery("select entity from AppInstanceArg entity where entity.arg = :arg AND entity.appinstance.id = :id AND entity.active = :active order by entity.id asc");
	        query.setParameter("active", 1);
	        query.setParameter("arg", arg_key);
	        query.setParameter("id", app_inst_id);
	        List r = query.list();
	        
	        AppInstanceArg[] array = (AppInstanceArg[]) r.toArray(new AppInstanceArg[r.size()]);
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        return array;
	        
    	}catch (Exception e) { 
    		e.printStackTrace();
        } 
		
		return new AppInstanceArg[0];
	}
	
	
	/**
	 * Search for a specific argument based on ID of a specific application instance
	 * @param app_inst_id App instance ID
	 * @param arg_id argument ID
	 * @return
	 */
	
	public static AppInstanceArg[] getAppInstanceArgument(long app_inst_id, long arg_id){
		
		try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        
	        Query query = session.createQuery("select entity from AppInstanceArg entity where entity.id = :appinstid AND entity.appinstance.id = :argid AND entity.active = :active order by entity.id asc");
	        query.setParameter("active", 1);
	        query.setParameter("appinstid", arg_id);
	        query.setParameter("argid", app_inst_id);
	        List r = query.list();
	        
	        AppInstanceArg[] array = (AppInstanceArg[]) r.toArray(new AppInstanceArg[r.size()]);
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        return array;
	        
    	}catch (Exception e) { 
    		e.printStackTrace();
        } 
		
		return new AppInstanceArg[0];
	}
	
	/**
	 * Return AppInstanceArg entity based on the argument key and value
	 * @param app_inst_id AppInstance ID
	 * @param arg_key argument key
	 * @param value argument value
	 * @return
	 */
	
	public static AppInstanceArg[] getAppInstanceArgument(long app_inst_id, String arg_key, String value){
		
		try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        
	        Query query = session.createQuery("select entity from AppInstanceArg entity where entity.arg = :arg AND entity.value = :value AND entity.appinstance.id = :id AND entity.active = :active order by entity.id asc");
	        query.setParameter("active", 1);
	        query.setParameter("arg", arg_key);
	        query.setParameter("value", value);
	        query.setParameter("id", app_inst_id);
	        List r = query.list();
	        
	        AppInstanceArg[] array = (AppInstanceArg[]) r.toArray(new AppInstanceArg[r.size()]);
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        return array;
	        
    	}catch (Exception e) { 
    		e.printStackTrace();
        } 
		
		return new AppInstanceArg[0];
	}
	
	/**
	 * Remove a specifica key,value property
	 * @param app_inst_id
	 * @param arg
	 * @param value
	 */
	
	public static void disableAppInstanceArg(long app_inst_id, String arg, String value){
		
		AppInstanceArg[] remove = getAppInstanceArgument(app_inst_id,arg,value);
		
		for(AppInstanceArg a : remove){
			
			a.setActive(0);
			HibernateUtil.SaveOrUpdate(a);
			
		}
		
	}
	
	/**
	 * Remove all arugment with this key
	 * @param app_inst_id
	 * @param key
	 */
	
	public static void disableAppInstanceArg(long app_inst_id, String key){
		
		AppInstanceArg[] remove = getAppInstanceArgument(app_inst_id,key);
		
		for(AppInstanceArg a : remove){
			
			a.setActive(0);
			HibernateUtil.SaveOrUpdate(a);
			
		}
		
	}
	
	/**
	 * Remove specific argument with arg_id 
	 * @param app_inst_id
	 * @param arg_id
	 */
	
	public static void disableAppInstanceArg(long app_inst_id, long arg_id){
		
		AppInstanceArg[] remove = getAppInstanceArgument(app_inst_id,arg_id);
		
		for(AppInstanceArg a : remove){
			
			a.setActive(0);
			HibernateUtil.SaveOrUpdate(a);
			
		}
		
	}
	
	
//	public static AppInstanceCmdArg[] getAppInstanceCmdArg(long appinstcmd_id){
//		
//		try{
//        	
//	        Session session = HibernateUtil.getSessionFactory().openSession();
//
//	        
//	        Transaction txn = session.beginTransaction();
//	        
//	        Query query = session.createQuery("select entity from AppInstanceCmdArg entity where entity.appinstancecmd.id = :id AND entity.active = :active");
//	        query.setParameter("active", 1);
//	        query.setParameter("id", appinstcmd_id);
//	        List r = query.list();
//	        
//	        AppInstanceCmdArg[] array = (AppInstanceCmdArg[]) r.toArray(new AppInstanceCmdArg[r.size()]);
//	        
//	        session.flush();
//	        txn.commit();
//	        
//	        session.close();
//	        
//	        //HibernateUtil.getSessionFactory().close();
//        
//	        return array;
//	        
//    	}catch (Exception e) { 
//    		e.printStackTrace();
//        } 
//		
//		return new AppInstanceCmdArg[0];
//	}
	
//	/**
//	 * 
//	 * @param app_id
//	 */
//	
//	public void stopWorkflow(long app_id){
//		
//	}
	

	public void dispose(){
		
	}


	public void setWorkflowHandler(WorkflowAPI workflowHandler) {
		this.workflowHandler = workflowHandler;
	}


	public WorkflowAPI getWorkflowHandler() {
		return workflowHandler;
	}
	
	/**
	 * Implement logger later
	 * @param errormsg
	 */
	
	private static void ThrowError(String errormsg){
		System.out.println(errormsg);
		logger.error(errormsg);
	}
	
	private static void ThrowError(String errormsg, String source) throws AHEException{
		System.out.println(errormsg + " (AHEEngine."+source+")");
		logger.error(errormsg + " : " + source);
		
		 throw new AHEException(errormsg);
	}
	
	/**
	 * Terminate app instance
	 * @param appinst_id
	 * @return
	 * @throws AHEException
	 */
	
	public String terminateAppInstance(long appinst_id) throws AHEException{
		
		AppInstance app = getAppInstanceEntity(appinst_id);
		return terminateAppInstance(app);
		
	}
	
	/**
	 * Terminate AppInstance
	 * 
	 * Stop Workflow
	 * Set AppInstance to end
	 * Terminate any active running jobs
	 * 
	 * @param inst
	 * @return
	 * @throws AHEException 
	 */
	
	public String terminateAppInstance(AppInstance inst) throws AHEException{
		
		//Terminate Job
		AHEMessage msg = null;
		
		try {
			msg = JobSubmissionHandler.terminate(inst);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AHEException(e);
		}
		
		if(AHEMessageUtility.hasException(msg)){
			logger.error("Job termination failed");
			throw new AHEException("Job termination failed");
		}
			
		//Terminate Workflow
			
		AHEWorkflow workflow_id = getAHEWorkflowEntity(inst);
		long process_id = workflow_id.getProcess_id();
		getWorkflowHandler().abort(inst.getId(),process_id);
			
		//Set inst to end
		setCurrentAppInstanceState(inst.getId(), AppInstanceStates.Finished.toString(), "App Instance has been terminated by the user");
				
		return "";
		
	}
	
	/**
	 * Terminate DB connections. Used for Standalone J2SE configuration
	 */
	
	public void TerminateDB(){
		getWorkflowHandler().terminate();
		HibernateUtil.terminate();
	}
	
}
