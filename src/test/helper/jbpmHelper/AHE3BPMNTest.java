package test.helper.jbpmHelper;

import java.util.HashMap;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
//import org.hibernate.annotations.common.util.impl.Log_.logger;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;

import test.helper.jbpmHelper.SubmitWorkItemHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.ErrorWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.InitWorkflowWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.JobCompletedHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.NotificationHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.PollingErrorWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.PollingWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.SubmissionErrorWorkHandler;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.AHEWorkItemHandler.SubmissionWorkHandler;

public class AHE3BPMNTest {
	
	public static void main(String[] arg){
	KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	kbuilder.add(ResourceFactory.newClassPathResource("main/resources/AwsWorkflow_old.bpmn"), ResourceType.BPMN2);
	
	KnowledgeBase kbase = kbuilder.newKnowledgeBase();
	
	StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

	//session.getWorkItemManager().registerWorkItemHandler("InitWorkflow", new InitWorkflowWorkHandler());
	session.getWorkItemManager().registerWorkItemHandler("Submit", new SubmissionWorkHandler());
	//session.getWorkItemManager().registerWorkItemHandler("Polling", new PollingWorkHandler());
	//session.getWorkItemManager().registerWorkItemHandler("SubmissionErrorHandler", new SubmissionErrorWorkHandler());
	//session.getWorkItemManager().registerWorkItemHandler("PollingErrorWorkHandler", new PollingErrorWorkHandler());
	session.getWorkItemManager().registerWorkItemHandler("Notification", new NotificationHandler());
	//session.getWorkItemManager().registerWorkItemHandler("Error", new ErrorWorkHandler());
	session.getWorkItemManager().registerWorkItemHandler("JobCompleted", new JobCompletedHandler());
	
	KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(session, "AwsWorkflow");
	//RuntimeManager manager = createRuntimeManager("test.bpmn");
	//RuntimeManager manager = createRuntimeManager("sample.bpmn");
	//RuntimeEngine engine = getRuntimeEngine(null);
	//KieSession ksession = engine.getKieSession();
//	UserTransaction ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
//	ut.begin();		
	//logger.info("Creating Workflow session id : " + session.getId());
	HashMap<String, Object> params = new HashMap<String, Object>();
	params.put("app_inst_id", "7");
	
	org.drools.runtime.process.ProcessInstance instance = session.startProcess("AwsWorkflow_old", params);
	//session.signalEvent("SubmitSignal", null);
	//ProcessInstance instance = session.startProcess("AwsWorkflow");
	System.out.println("instance id: " + instance.getId() + " instace process id: " + 
	instance.getProcessId());
	logger.close();
	}

}
