package uk.ac.ucl.chem.ccs.AHECore.Workflow;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.MapGlobalResolver;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.BPMN2ProcessFactory;
import org.drools.compiler.ProcessBuilderFactory;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.marshalling.impl.ProcessMarshallerFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.ProcessRuntimeFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jbpm.bpmn2.BPMN2ProcessProviderImpl;
import org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
//import org.kie.api.runtime.KieSession;
//import org.kie.api.runtime.manager.RuntimeEngine;
//import org.kie.api.runtime.manager.RuntimeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import test.helper.Executable;
//import test.helper.Helper;
//import test.helper.jbpmHelper.SubmitWorkItemHandler;
import uk.ac.ucl.chem.ccs.AHECore.Definition.AHEConfigurationProperty;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEWorkflow;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.WorkflowDescription;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.WorkflowInstance;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.DataStagingWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.ErrorWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.FinishWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.InitWorkflowWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.JobCompletedHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.NotificationHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.PollingErrorWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.PollingWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.PostProcessingWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.PreProcessingWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.PrepareWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.ResultStagingWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.SchedulerWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.StageInPollWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.StageOutPollWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.StagingErrorWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.SubmissionAwsErrorWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.SubmissionErrorWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.SubmissionWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.SubmitCompletedHandler;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * 
 * A job workflow controls a workflow's life cycle and provides API to control
 * it
 * 
 * NOTE: A process needs the following internal variables
 * 1. Polling_Status - Job_Not_Completed, Completed, Error
 * 2. App_Instance_Id 
 * 
 * 
 * Currently, it is one KnowledgeSession per KnowledgeBase
 * 
 * @author davidc
 * 
 */

public class WorkflowAPI {

	private static Logger logger = LoggerFactory.getLogger(WorkflowAPI.class);
	EntityManagerFactory emf;
	
	private AHEEngine parent;
	PoolingDataSource ds1;
	HashMap<String, KnowledgeBase> kbase_map;
	
	private String j2se_datasource_name = "jdbc/jbpmds";

	private StatefulKnowledgeSession session;
	
	public static void main(String[] arg) {
		AHERuntime.getAHERuntime().setStandalone_mode(true);
		//WorkflowAPI test = new WorkflowAPI();
		//test.createWorkflow(4, "AHEWorkflow","AHEWorkflow.bpmn");
		//test.createWorkflow_NoPersistance(14, "BasicAHEWorkflow","BasicAHEWorkflow.bpmn");
		
	}
	


	public WorkflowAPI(){
	
		kbase_map = new HashMap<String, KnowledgeBase>();
	
	}
	
	/**
	 * Get Current JBPM node
	 * @param appinstance_id
	 * @param processid
	 * @return
	 */
	
	public String getCurrnetNode(long appinstance_id, long processid) {

		try {
			
			AHEWorkflow mapping = AHEEngine.getAHEWorkflowEntityByAppInstId(appinstance_id);
			StatefulKnowledgeSession session = getKnowledgeSession(appinstance_id, mapping.getKsession_id());
			
			ProcessInstance inst = session.getProcessInstance(processid);
			
			WorkflowProcessInstance workInstance = (WorkflowProcessInstance) inst;
			String name = workInstance.getNodeInstances().iterator().next().getNodeName();
			
			return name;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;


	}
	
	/**
	 * Get current workflow state for AppInst with process id
	 * @param appinstance_id
	 * @param processid
	 * @return
	 */
	
	public int getWorkflowJBPMState(long appinstance_id, long processid) {

		try {
			
			AHEWorkflow mapping = AHEEngine.getAHEWorkflowEntityByAppInstId(appinstance_id);
			StatefulKnowledgeSession session = getKnowledgeSession(appinstance_id, mapping.getKsession_id());
			
			return session.getProcessInstance(processid).getState();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return -1;

	}
	
	/**
	 * Return workflow of an appinstance
	 * @param app_id
	 * @return
	 */

	public AHEWorkflow getWorkflowByAppInstance(long app_id) {

    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

	        
	        Transaction txn = session.beginTransaction();
	        
	        txn.commit();

	        Query query = session.createQuery("select mapping from AHEWorkflow mapping where mapping.appinstance.id = :appid and as.active =1");
	        query.setParameter("appid", app_id);
	        List r = query.list();

	        session.flush();
	        session.close();
//	        
//	        HibernateUtil.getSessionFactory().close();
        
	        if(r.size() == 0)
	        	return null;
	        else
	        	return (AHEWorkflow) r.get(0);
	        
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
		
		
		return null;
	}

	private ProcessInstance getProcessInstance(long appinstance_id, long processid) {
		
		try {
			
			AHEWorkflow mapping = AHEEngine.getAHEWorkflowEntityByAppInstId(appinstance_id);
			StatefulKnowledgeSession session = getKnowledgeSession(appinstance_id, mapping.getKsession_id());
			
			return session.getProcessInstance(processid);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public void FileStageConfigurationCompleteSignal(long appinstance_id,long processid){
		
		if(ds1 == null && AHERuntime.getAHERuntime().isStandalone_mode()){
			initJ2SEDatasource();
		}
		
		try {
			
			AHEWorkflow mapping = AHEEngine.getAHEWorkflowEntityByAppInstId(appinstance_id);
			StatefulKnowledgeSession session = getKnowledgeSession(appinstance_id, mapping.getKsession_id());

			session.signalEvent("StagingCompleteSignal", null, processid);
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * Issue a submit job signal. This will cause the workflow to submit a job to an external resource
	 * @param appinstance_id
	 * @param processid
	 */
	
	public void SubmissionSignal(long appinstance_id,long processid){
		
		if(ds1 == null && AHERuntime.getAHERuntime().isStandalone_mode()){
			initJ2SEDatasource();
		}
		
		try {
			
			AHEWorkflow mapping = AHEEngine.getAHEWorkflowEntityByAppInstId(appinstance_id);
			StatefulKnowledgeSession session = getKnowledgeSession(appinstance_id, mapping.getKsession_id());
			
			session.signalEvent("SubmitSignal", null, processid);
			//Helper.writeout("here Submission signal ended inst id:" + appinstance_id);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	private void createAppInstanceJBPMMapping(long AppInstanceId, long processId) throws AHEException{
		
		AppInstance inst = getParent().getAppInstanceEntity(AppInstanceId);
		
//		AppInstance inst = new AppInstance();
//		inst.setActive(1);
//		HibernateUtil.SaveOrUpdate(inst);
		
		if(inst != null){
			
			AHEWorkflow mapping = new AHEWorkflow();
			mapping.setProcess_id(processId);
			mapping.setAppinstance(inst);
			
			HibernateUtil.SaveOrUpdate(mapping);
			return;
			
		}else{
			//TODO Throw Exception
			
		}
		
	}
	
	/**
	 * Register WorkItem Handler to JBPM
	 * @param ahe_workflow
	 */
	
	private void registerWorkHanlder(StatefulKnowledgeSession ahe_workflow){
				
		ahe_workflow.getWorkItemManager().registerWorkItemHandler("Submit", new SubmissionWorkHandler());
		
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("Prepare", new PrepareWorkHandler());
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("Staging", new DataStagingWorkHandler());
		
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("Polling", new PollingWorkHandler());
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("SchedulerWait", new SchedulerWorkHandler());
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("Fetch", new ResultStagingWorkHandler());
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("Finish", new FinishWorkHandler());
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("Error", new ErrorWorkHandler());
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("InitWorkflow", new InitWorkflowWorkHandler());
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("Notification", new NotificationHandler());
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("JobCompleted", new JobCompletedHandler());
		ahe_workflow.getWorkItemManager().registerWorkItemHandler("SubmitCompleted", new SubmitCompletedHandler());
		ahe_workflow.getWorkItemManager().registerWorkItemHandler("SubmitAwsError", new SubmissionAwsErrorWorkHandler());
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("StageInPoll", new StageInPollWorkHandler());
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("StageOutPoll", new StageOutPollWorkHandler());

		
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("StagingErrorHandler", new StagingErrorWorkHandler());
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("SubmissionErrorHandler", new SubmissionErrorWorkHandler());
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("ResultStagingErrorHandler", new ResultStagingWorkHandler());
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("PollingErrorWorkHandler", new PollingErrorWorkHandler());
		

		
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("PreProcessing", new PreProcessingWorkHandler());
		//ahe_workflow.getWorkItemManager().registerWorkItemHandler("PostProcessing", new PostProcessingWorkHandler());
		session = ahe_workflow;
		//getKsession().getWorkItemManager().registerWorkItemHandler("JobSubmissionError", new ErrorWorkHandler());
//		ahe_workflow.getWorkItemManager().registerWorkItemHandler("StageInScheduleWait", new StageinPollSchedulerWorker());
//		ahe_workflow.getWorkItemManager().registerWorkItemHandler("StageOutScheduleWait", new StageOutPollSchedulerHandler());
//		ahe_workflow.getWorkItemManager().registerWorkItemHandler("SecuritySetup", new SecuritySetupWorkHandler());
//		ahe_workflow.getWorkItemManager().registerWorkItemHandler("SecurityDispose", new SecurityDisposeWorkHandler());
	}
	
	private HashMap<String, Object> WorkflowParameterMap(Long app_instance_id){
		
		//Setup Interal Process Variables
		HashMap<String, Object> params = new HashMap<String, Object>();
		
		String TimerDelay = "10s"; //Deprecated
		Long app_inst_id = app_instance_id;
		Integer polling_status = 0; //0 : polling, 1 : completed, -1 : error //deprecated
		Integer submit_status = 1; //Submission is successful or not //deprecated 
		Integer fetch_status = 1; //result fetching is successful or not
		Integer staging_status = 1; //staging data is successful or not
		Integer stagein_poll_status = 0;
		Integer stageout_poll_status = 0;
		
//		Integer job_status = 0;
		
		params.put("TimerDelay", TimerDelay);
		params.put("app_inst_id", app_inst_id);
		params.put("polling_status", polling_status);
		params.put("submit_status",submit_status);
		params.put("fetch_status",fetch_status);
		params.put("staging_status",staging_status);
		params.put("stagein_poll_status",stagein_poll_status);
		params.put("stageout_poll_status",stageout_poll_status);
//		params.put("job_status",job_status);
		
		return params;
		
	}
	
	/**
	 * Create a non persistent JBPM workflow
	 * @param app_instance_id
	 * @param process_name
	 * @param workflow_file
	 * @return
	 */
	
	public long createWorkflow_NoPersistance(long app_instance_id, String process_name, String workflow_file){
		
		try {
			// load up the knowledge base
			

				
			KnowledgeBase kbase = readKnowledgeBaseNoPersistance(workflow_file);
			StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
			registerWorkHanlder(ksession);
				
						
			KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
			
			// start a new process instance
			
			System.out.println(app_instance_id);
			
			ProcessInstance i = ksession.startProcess(process_name,WorkflowParameterMap(app_instance_id));
//			ksession.fireAllRules();
			
//			getKsession().signalEvent("StagingCompleteSignal", null,i.getId());
//			getKsession().signalEvent("SubmitSignal", null,i.getId());
			
			logger.close();
			
			return i.getId();
			
		} catch (Throwable t) {
			t.printStackTrace();
			return -1;
		}
		
		
	}
	
	/**
	 * Create a persisten workflow JBPM
	 * @param app_instance_id
	 * @param process_name
	 * @param workflow_file
	 * @return array of process id and session id
	 */
	
	public long[] createWorkflow(long app_instance_id, String process_name, String workflow_file) {

		try {

			if(ds1 == null && AHERuntime.getAHERuntime().isStandalone_mode()){
				initJ2SEDatasource();
			}			
			
//			JBPMHelper.setupDataSource();
			
			//KnowledgeBase base = getKnowledgeBase(workflow_file);
			//StatefulKnowledgeSession session = getKnowledgeSession(base, -1);
			//start
			KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			kbuilder.add(ResourceFactory.newClassPathResource(workflow_file), ResourceType.BPMN2);
			
			KnowledgeBase kbase = kbuilder.newKnowledgeBase();
			
			StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
			
			//registerWorkHanlder(session);

			registerWorkHanlder(session);
			//session.getWorkItemManager().registerWorkItemHandler("Submit", new SubmitWorkItemHandler());
			//session.getWorkItemManager().registerWorkItemHandler("Submit", new SubmissionWorkHandler());
			//stop 
			//session.getWorkItemManager().registerWorkItemHandler("", );
			//RuntimeManager manager = createRuntimeManager("AwsWorkflow.bpmn");
			//RuntimeManager manager = createRuntimeManager("sample.bpmn");
			//RuntimeEngine engine = getRuntimeEngine(null);
			//KieSession ksession = engine.getKieSession();
//			UserTransaction ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
//			ut.begin();		
			logger.info("Creating Workflow session id : " + session.getId());
			//ProcessInstance instance = session.startProcess(process_name);
			ProcessInstance instance = session.startProcess(process_name,WorkflowParameterMap(app_instance_id));
//			session.fireAllRules();
//			ut.commit();
//			logger.close();
			//Helper.writeout("here Workflow API inst id:" + app_instance_id);
			return new long[]{instance.getId(),session.getId()};
			
		} catch (Throwable t) {
			t.printStackTrace();
			return new long[]{-1,-1};
		}

	}

	public void resume(long processid) {

	}

	public void abort(long appinstance_id,long processid) {
		
		try {
			
			AHEWorkflow mapping = AHEEngine.getAHEWorkflowEntityByAppInstId(appinstance_id);
			StatefulKnowledgeSession session = getKnowledgeSession(appinstance_id, mapping.getKsession_id());
			
			session.abortProcessInstance(processid);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * Load the bpmn file into knowledgebase
	 */
	private KnowledgeBase readKnowledgeBase(String workflow_file) throws Exception {
		
		ProcessBuilderFactory.setProcessBuilderFactoryService(new ProcessBuilderFactoryServiceImpl());
		ProcessMarshallerFactory.setProcessMarshallerFactoryService(new ProcessMarshallerFactoryServiceImpl());
		ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
		BPMN2ProcessFactory.setBPMN2ProcessProvider(new BPMN2ProcessProviderImpl());
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource(workflow_file),	ResourceType.BPMN2);
		return kbuilder.newKnowledgeBase();
	}
	
	private KnowledgeBase readKnowledgeBaseNoPersistance(String workflow_file) throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource(workflow_file), ResourceType.BPMN2);
		return kbuilder.newKnowledgeBase();
	}

	/**
	 * Create EntityManagerFactory and register it in the environment Create the
	 * knowledge session that uses JPA to persists runtime state
	 * @param kbase
	 * @return
	 */

	private StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
		
		if(emf == null)
			emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
		
		Environment env = KnowledgeBaseFactory.newEnvironment();
		env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
		env.set(EnvironmentName.TRANSACTION_MANAGER,TransactionManagerServices.getTransactionManager());
		env.set(EnvironmentName.GLOBALS, new MapGlobalResolver());

		Properties properties = new Properties();
		properties.put("drools.processInstanceManagerFactory","org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
		properties.put("drools.processSignalManagerFactory","org.jbpm.persistence.processinstance.JPASignalManagerFactory");
		KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);

		
		return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);

//		return JBPMHelper.newStatefulKnowledgeSession(kbase);

	}
	
	/**
	 * Load an existing Knowledge session
	 * @param sessionId
	 * @param kbase
	 * @return
	 */
	
	private StatefulKnowledgeSession loadKnowledgeSession(int sessionId, KnowledgeBase kbase) {
		
		if(emf == null)
			emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
		
		Environment env = KnowledgeBaseFactory.newEnvironment();
		env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
		env.set(EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager());
		env.set(EnvironmentName.GLOBALS, new MapGlobalResolver());

		Properties properties = new Properties();
		properties
				.put("drools.processInstanceManagerFactory",
						"org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
		properties.put("drools.processSignalManagerFactory",
				"org.jbpm.persistence.processinstance.JPASignalManagerFactory");
		KnowledgeSessionConfiguration config = KnowledgeBaseFactory
				.newKnowledgeSessionConfiguration(properties);

		return JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, config, env);

		//return  JBPMHelper.loadStatefulKnowledgeSession(kbase, sessionId);
	}
	


	public void setParent(AHEEngine parent) {
		this.parent = parent;
	}

	public AHEEngine getParent() {
		return parent;
	}

	/**
	 * Get knowledgebase
	 * @param workflow_file
	 * @return
	 * @throws Exception
	 */

	public KnowledgeBase getKnowledgeBase(String workflow_file) throws Exception{
		
		if(kbase_map.containsKey(workflow_file))
			return kbase_map.get(workflow_file);
		
		KnowledgeBase new_base = readKnowledgeBase(workflow_file);
		kbase_map.put(workflow_file, new_base);
		//Executable.writeout("getKnowledgeBase");
		return new_base;
		
	}
	
	/**
	 * Get knowledge session
	 * @param AppInstance_id
	 * @param session_id
	 * @return
	 * @throws Exception
	 */
	
	public StatefulKnowledgeSession getKnowledgeSession(long AppInstance_id, int session_id) throws Exception{
		
		AHEWorkflow mapping = AHEEngine.getAHEWorkflowEntityByAppInstId(AppInstance_id);
		return getKnowledgeSession(getKnowledgeBase(mapping.getWorkflow_path()), session_id);
		
	}
	
	/**
	 * AHE uses one session for all processes
	 * @param base
	 * @param session_id
	 * @return
	 */
	
	public StatefulKnowledgeSession getKnowledgeSession(KnowledgeBase base, int session_id){
		
		if(getStatefulKnowledgeSession() != null){
			return getStatefulKnowledgeSession();
		}else{
			if(session_id == -1){
				StatefulKnowledgeSession session = createKnowledgeSession(base);
				setStatefulKnowledgeSession(session);
				registerWorkHanlder(session);
				return session;
			}else{
				StatefulKnowledgeSession session = loadKnowledgeSession(session_id, base);
				setStatefulKnowledgeSession(session);
				registerWorkHanlder(session);
				return session;
			}
		}
	
		
		//Create a new one
//		if(session_id == -1){
//			
//			//Check if there are existing sessions in base
//			if(base.getStatefulKnowledgeSessions().size() > 0){
//
//				
//				
//				StatefulKnowledgeSession session = loadKnowledgeSession(base.getStatefulKnowledgeSessions().iterator().next().getId(), base);
//				registerWorkHanlder(session);
//				return session;
//			}else{
//				System.out.println("x112");
//				StatefulKnowledgeSession session = createKnowledgeSession(base);
//				setStatefulKnowledgeSession(session);
//				registerWorkHanlder(session);
//				return session;
//			}
//			
//
//			
//		}else{
//			System.out.println("x111");
//			StatefulKnowledgeSession session = loadKnowledgeSession(session_id, base);
//			registerWorkHanlder(session);
//			return session;
//		}
		
	}
	
//	public void setKsession(StatefulKnowledgeSession ksession) {
//		this.ksession = ksession;
//	}
//
//	public StatefulKnowledgeSession getKsession() {
//		return ksession;
//	}
	
	
	public void terminate(){
		ds1.close();
	}
	
	/**
	 * Create a workflow description entity
	 * @param workflow_name
	 * @param workflow_filename
	 * @param description
	 * @param appinst_workflow
	 */
	
	public static void createWorkflowDescription(String workflow_name, String workflow_filename, String description, boolean appinst_workflow){
		
		WorkflowDescription d = new WorkflowDescription();
		
		d.setWorkflow_name(workflow_name);
		d.setWorkflow_filename(workflow_filename);
		d.setWorkflow_description(description);
		d.setAppinst_workflow(appinst_workflow);
		d.setActive(true);
		d.setTimestamp(new Date());
		
		HibernateUtil.SaveOrUpdate(d);
		//return d;
		
	}
	
	public static void deleteWorkflowDescription(String workflow_name){
		//WorkflowDescription d = new WorkflowDescription();
		//d.setWorkflow_name(workflow_name);
		
		//HibernateUtil.Delete(workflow_name);
	}
	
	/**
	 * return a workflow description entity
	 * @param workflow_name
	 * @return
	 */
	
	public static WorkflowDescription getWorkflowDescription(String workflow_name){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        
	        Query query = session.createQuery("select ap from WorkflowDescription ap where ap.workflow_name = :name AND ap.active = :active");
	        query.setParameter("active", true);
	        query.setParameter("name", workflow_name);
	        List r = query.list();
	        
	        WorkflowDescription[] array = (WorkflowDescription[]) r.toArray(new WorkflowDescription[r.size()]);
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
//	        HibernateUtil.getSessionFactory().close();
        
	        if(array.length > 0)
	        	return array[0];
	        else 
	        	return null;
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
		
		return null;
		
	}
	
	/**
	 * List all workflow description entities
	 * @return
	 */
	
	public static WorkflowDescription[] listWorkflowDescription(){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        
	        Query query = session.createQuery("select ap from WorkflowDescription ap where ap.active = :active");
	        query.setParameter("active", 1);
	        List r = query.list();
	        
	        WorkflowDescription[] array = (WorkflowDescription[]) r.toArray(new WorkflowDescription[r.size()]);
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
//	        HibernateUtil.getSessionFactory().close();
        
	        return array;
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
		
		return new WorkflowDescription[0];
		
	}
	
	/**
	 * Create a workflow instance (This is experimental AHE feature, currently not fully implemented)
	 * @param instance_name
	 * @param workflow_id
	 * @param owner
	 */
	
	public static void createWorkflowInstance(String instance_name, String workflow_id, AHEUser owner){
		
		WorkflowInstance inst = new WorkflowInstance();
		inst.setName(instance_name);
		inst.setWorkflow_id(workflow_id);
		inst.setOwner(owner);
		inst.setActive(true);
		inst.setTimestamp(new Date());
		
		HibernateUtil.SaveOrUpdate(inst);
		
	}
	
	/**
	 * Return an workflow instance 
	 * @param instance_name
	 * @return
	 */
	
	public static WorkflowInstance getWorkflowInstance(String instance_name){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        
	        Query query = session.createQuery("select ap from WorkflowInstance ap where ap.name = :name AND ap.active = :active");
	        query.setParameter("active", true);
	        query.setParameter("name", instance_name);
	        List r = query.list();
	        
	        WorkflowInstance[] array = (WorkflowInstance[]) r.toArray(new WorkflowInstance[r.size()]);
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
//	        HibernateUtil.getSessionFactory().close();
        
	        if(array.length > 0)
	        	return array[0];
	        else 
	        	return null;
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
		
		return null;
		
	}
	
	private void initJ2SEDatasource(){
		
		ds1 = new PoolingDataSource();
		ds1.setUniqueName(j2se_datasource_name);
		ds1.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource"); //Note must use btm-2.1.3.jar, bug in prev version
		//ds1.setClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
		ds1.setMinPoolSize(5);
		ds1.setMaxPoolSize(50);
//		ds1.setMaxIdleTime(600);
		ds1.setAllowLocalTransactions(true);
		ds1.getDriverProperties().put("user", AHERuntime.getAHERuntime().getAHEConfiguration().getPropertyString(AHEConfigurationProperty.j2se_datasource_username.toString()));
		ds1.getDriverProperties().put("password", AHERuntime.getAHERuntime().getAHEConfiguration().getPropertyString(AHEConfigurationProperty.j2se_datasource_password.toString()));
		ds1.getDriverProperties().put("url",AHERuntime.getAHERuntime().getAHEConfiguration().getPropertyString(AHEConfigurationProperty.j2se_datasource_url.toString()));
//		ds1.getDriverProperties().setProperty("driverClassName","com.mysql.jdbc.Driver");
		ds1.getDriverProperties().put("driverClassName", "org.h2.Driver");
		ds1.init();


	}



	public StatefulKnowledgeSession getStatefulKnowledgeSession() {
		return session;
	}



	public void setStatefulKnowledgeSession(StatefulKnowledgeSession session) {
		this.session = session;
	}
	
}

