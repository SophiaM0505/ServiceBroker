package test.helper.jbpmHelper;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

public class SecondWorkItemHandler implements WorkItemHandler{

	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// TODO Auto-generated method stub
		/*KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("test.bpmn"), ResourceType.BPMN2);
		
		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
		
		StatefulKnowledgeSession ksession1 = kbase.newStatefulKnowledgeSession();

		ksession1.getWorkItemManager().registerWorkItemHandler("Second", new SecondWorkItemHandler()

		);*/
		
		String message = (String) arg0.getParameter("parameter2");
		Second second = new Second();
		second.setSecond("world" + message);
		
		System.out.println("second message: " + message);
		//System.out.println("we are in second.");
		System.out.println(second.getSecond());
		
		arg1.completeWorkItem(arg0.getId(), null);
	}

}
