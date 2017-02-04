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

public class FirstWorkItemHandler implements WorkItemHandler{

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

		ksession1.getWorkItemManager().registerWorkItemHandler("First", new FirstWorkItemHandler()

		);*/
		//arg1.registerWorkItemHandler("First", new FirstWorkItemHandler());
		
		String message = (String) arg0.getParameter("parameter1");
		System.out.println("******" + message);
		First first = new First();
		first.setFirst("hello " + message);
		
		System.out.println("first message: " + message);
		System.out.println(first.getFirst());
		arg1.completeWorkItem(arg0.getId(), null);
	}

}
