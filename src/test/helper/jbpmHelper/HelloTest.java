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

public class HelloTest {
	
public static void main(String[] arg){
		
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("main/resources/testSub3.bpmn"), ResourceType.BPMN2);
		
		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
		
		StatefulKnowledgeSession ksession1 = kbase.newStatefulKnowledgeSession();

		ksession1.getWorkItemManager().registerWorkItemHandler("First", new FirstWorkItemHandler());
		
		ksession1.getWorkItemManager().registerWorkItemHandler("Second", new SecondWorkItemHandler());
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("variable1", "Sophie");
		
		KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession1, "test3");
		
		//org.drools.runtime.process.ProcessInstance i = ksession1.startProcess("BPMN2-Subprocess", params);
	
		org.drools.runtime.process.ProcessInstance i = ksession1.startProcess("test3", params);		
		//First first = new First();
		//first.setFirst("Sophie");
		
		logger.close();
		
		System.out.println("Monday");
		System.out.println(i.getId());
		
	}

}
