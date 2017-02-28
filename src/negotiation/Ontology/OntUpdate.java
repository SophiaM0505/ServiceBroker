package negotiation.Ontology;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import negotiation.Database.NegotiationDB;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.OWLEntityRemover;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class OntUpdate {
	
	static double cost;
	//final static String mfile_path = "file:/Users/zeqianmeng/Desktop/ontology/";
	final static String mfile_path = "file:/opt/AHE3/ontology/";
	final static String ns = "http://www.owl-ontologies.com/alliance#";
	
public static void mPolicyGridReduce(String name, String grp, String app) throws OWLOntologyStorageException, OWLOntologyCreationException{
		//the parameter values can get from user input in AppInstanceResource
	    //String mfile = "file:/opt/AHE3/ontology/mPolicy.owl";
		//String mfile = "file:/Users/zeqianmeng/Desktop/ontology/mPolicy.owl";
		String mfile = mfile_path + "mPolicy.owl";
		OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		OWLOntology ontology2 = manager2.loadOntology(IRI.create(mfile));
		String ns = "http://www.owl-ontologies.com/alliance#";
		
		PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
		//System.out.println("part2 done.");
		
		reasoner2.getKB().realize();
		
		//nameInput = name;
		int URemMemNo, URemCpuNo;
		
		String ecpu = "PhysicalCPUs";
		String emem = "VirtualMemorySize";
		
		name = "Sofia";
		grp = "ManGroup";
		app = "WaterSteering";
		
		OWLDataProperty  Avalcpu = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + ecpu));
		OWLDataProperty  Avalmem = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + emem));
		OWLNamedIndividual userAPol = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + name));
		
		URemMemNo = userAPol.getDataPropertyValues(Avalmem, ontology2).iterator().next().parseInteger();
		URemCpuNo = userAPol.getDataPropertyValues(Avalcpu, ontology2).iterator().next().parseInteger();
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLNamedIndividual userA = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + name));
		
		//to get required amount of resource info in hasAppEnvironment
		//int memNo = Integer.parseInt(appInds.getObjectPropertyValues(hasApp, ontology).iterator().next().getDataPropertyValues(hasRmem, ontology).iterator().next().getLiteral());
	    //System.out.println("Memory: " + memNo);
	    //int cpuTime = Integer.parseInt(appInds.getObjectPropertyValues(hasApp, ontology).iterator().next().getDataPropertyValues(hasRcpu, ontology).iterator().next().getLiteral());
		//user remaining resources after negotiation
		//int newURMem = URemMemNo - memNo;
		//int newURCpu = URemCpuNo - cpuTime;
	
		int newURMem = URemMemNo - 5;
		int newURCpu = URemCpuNo - 5;
		
		//int[] newNos = new int[10];
		//newNos[0] = newMem;
		//newNos[1] = newCpu;
		
		//for(int news:newNos){
		
		
		//change info in user's available memory in policy
		OWLEntityRemover remover1 = new OWLEntityRemover(manager2, Collections.singleton(ontology2));
		//userA.getDataPropertyValues(Rmem, ontology).clear();
		Avalmem.accept(remover1);
		//userA.getDataPropertyValues(Rmem, ontology).add(memLit);
		//remover.visit(userA);
		//OWLIndividualVisitor vis = null;
		//Rmem.accept(remover);
		//userA.accept(vis);
		manager2.applyChanges(remover1.getChanges());
        remover1.reset();
        
        manager2.saveOntology(ontology2);
		OWLDataPropertyAssertionAxiom dpass = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(Avalmem, userA, newURMem);
		AddAxiom change = new AddAxiom(ontology2, dpass);
        manager2.applyChange(change);
        manager2.saveOntology(ontology2);
        
        System.out.println("user's remaining memory: " + userAPol.getDataPropertyValues(Avalmem, ontology2).iterator().next().parseInteger());
        
        //change info in user's available cpu in policy
        OWLEntityRemover remover2 = new OWLEntityRemover(manager2, Collections.singleton(ontology2));
		//userA.getDataPropertyValues(Rmem, ontology).clear();
        Avalcpu.accept(remover2);
		//userA.getDataPropertyValues(Rmem, ontology).add(memLit);
		//remover.visit(userA);
		//OWLIndividualVisitor vis = null;
		//Rmem.accept(remover);
		//userA.accept(vis);
		manager2.applyChanges(remover2.getChanges());
        remover2.reset();
        
        manager2.saveOntology(ontology2);
		OWLDataPropertyAssertionAxiom dpass2 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(Avalcpu, userA, newURCpu);
		AddAxiom change2 = new AddAxiom(ontology2, dpass2);
        manager2.applyChange(change2);
        manager2.saveOntology(ontology2);
        
        System.out.println("user's remaining cpu: " + userAPol.getDataPropertyValues(Avalcpu, ontology2).iterator().next().parseInteger());
		//}
		
		System.out.println("*************PART4 DONE");
		
	}
//public static void mPolicyGridReduce() throws OWLOntologyStorageException, OWLOntologyCreationException{
public static void mPolicyGridReduce(HashMap<String, String> values) throws OWLOntologyStorageException, OWLOntologyCreationException{
	//the parameter values can get from user input in AppInstanceResource
	String mfile = mfile_path + "mPolicy.owl";
    //String mfile = "file:/opt/AHE3/ontology/mPolicy.owl";
	//String mfile = "file:/Users/zeqianmeng/Desktop/ontology/mPolicy.owl";
	OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
	OWLOntology ontology2 = manager2.loadOntology(IRI.create(mfile));
	String ns = "http://www.owl-ontologies.com/alliance#";
	
	PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
	//System.out.println("part2 done.");
	
	reasoner2.getKB().realize();
	
	//nameInput = name;
	int URemCpuNo;
	//int URemMemNo;
	String ecpu = "CpuTime";
	//String emem = "VirtualMemorySize";
	
	values = OntReasoning.getAppInfo();
	
	//String name = "Sofia";
	String name = values.get("user");
	//int req_mem = Integer.parseInt(values.get(""));
	int req_cpu = Integer.parseInt(values.get("cpuNo"));
	//int req_cpu = 5;
	//String grp = "ManGroup";
	//String app = "WaterSteering";
	
	OWLDataProperty  Avalcpu = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + ecpu));
	//OWLDataProperty  Avalmem = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + emem));
	OWLNamedIndividual userAPol = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + name));
	
	//URemMemNo = userAPol.getDataPropertyValues(Avalmem, ontology2).iterator().next().parseInteger();
	URemCpuNo = userAPol.getDataPropertyValues(Avalcpu, ontology2).iterator().next().parseInteger();
	
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	OWLNamedIndividual userA = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + name));
	
	//to get required amount of resource info in hasAppEnvironment
	//int memNo = Integer.parseInt(appInds.getObjectPropertyValues(hasApp, ontology).iterator().next().getDataPropertyValues(hasRmem, ontology).iterator().next().getLiteral());
    //System.out.println("Memory: " + memNo);
    //int cpuTime = Integer.parseInt(appInds.getObjectPropertyValues(hasApp, ontology).iterator().next().getDataPropertyValues(hasRcpu, ontology).iterator().next().getLiteral());
	//user remaining resources after negotiation
	//int newURMem = URemMemNo - memNo;
	//int newURCpu = URemCpuNo - cpuTime;

	//int newURMem = URemMemNo - 5;
	int newURCpu = URemCpuNo - req_cpu;
	
	//int[] newNos = new int[10];
	//newNos[0] = newMem;
	//newNos[1] = newCpu;
	
	//for(int news:newNos){
	
	
	//change info in user's available memory in policy
	/*OWLEntityRemover remover1 = new OWLEntityRemover(manager2, Collections.singleton(ontology2));
	//userA.getDataPropertyValues(Rmem, ontology).clear();
	Avalmem.accept(remover1);
	//userA.getDataPropertyValues(Rmem, ontology).add(memLit);
	//remover.visit(userA);
	//OWLIndividualVisitor vis = null;
	//Rmem.accept(remover);
	//userA.accept(vis);
	manager2.applyChanges(remover1.getChanges());
    remover1.reset();
    
    manager2.saveOntology(ontology2);
	OWLDataPropertyAssertionAxiom dpass = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(Avalmem, userA, newURMem);
	AddAxiom change = new AddAxiom(ontology2, dpass);
    manager2.applyChange(change);
    manager2.saveOntology(ontology2);
    
    System.out.println("user's remaining memory: " + userAPol.getDataPropertyValues(Avalmem, ontology2).iterator().next().parseInteger());*/
    
    //change info in user's available cpu in policy
    OWLEntityRemover remover2 = new OWLEntityRemover(manager2, Collections.singleton(ontology2));
	//userA.getDataPropertyValues(Rmem, ontology).clear();
    Avalcpu.accept(remover2);
	//userA.getDataPropertyValues(Rmem, ontology).add(memLit);
	//remover.visit(userA);
	//OWLIndividualVisitor vis = null;
	//Rmem.accept(remover);
	//userA.accept(vis);
	manager2.applyChanges(remover2.getChanges());
    remover2.reset();
    
    manager2.saveOntology(ontology2);
	OWLDataPropertyAssertionAxiom dpass2 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(Avalcpu, userA, newURCpu);
	AddAxiom change2 = new AddAxiom(ontology2, dpass2);
    manager2.applyChange(change2);
    manager2.saveOntology(ontology2);
    
    System.out.println("user's remaining cpu: " + userAPol.getDataPropertyValues(Avalcpu, ontology2).iterator().next().parseInteger());
	//}
	
	System.out.println("*************PART4 DONE");
	
}
	
	/*public final static void main(String[] args) throws OWLOntologyStorageException, OWLOntologyCreationException{
		//accessCheck("UserA", "ManGroup", "visLib");
		//resourceSearch("UserA", "ManGroup", "visLib");
		mPolicyGridReduce();
		System.out.println("Done");
	}*/
    public static void mPolicyShareCloudPartReduce(long cont_id, String end_time) throws OWLOntologyCreationException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, OWLOntologyStorageException{
    	double balance;
    	
    	Map<String, String> values = NegotiationDB.getPaymentInfo(cont_id);
		// get service in the contract, and get total cost of the service, then update ontology
		
		// in this program, cost is the total cost of service, while charge is unit price
		double charge = Double.parseDouble(values.get("charge"));
		String username = values.get("username");
		String stime = values.get("startTime");
		String share = values.get("share");
		String policy = share.substring(0, share.length()-5) + "Policy";
		String worker = values.get("worker");
    	//String mfile = "file:/opt/AHE3/ontology/mPolicy.owl";
    	String mfile = mfile_path + policy +".owl";
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(mfile));
		//String ns = "http://www.owl-ontologies.com/alliance#";
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		//System.out.println("part2 done.");
		
		reasoner.getKB().realize();
		
		
		
		OWLDataProperty  balance_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
		
		OWLNamedIndividual user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + username));
		
		balance = user_ind.getDataPropertyValues(balance_pro, ontology).iterator().next().parseDouble();
		
		long duration = 0;
		
		//to calculate duration as hours
		//if start_mm >= end_mm, then hh = duration_h
		//if start_mm < end_mm, then hh = duration_h + 1
		long duration_h = Long.parseLong(end_time.substring(11, 13)) - Long.parseLong(stime.substring(11, 13));
		//long min_diff = Long.parseLong(end_time.substring(15, 16)) - Long.parseLong(stime.substring(15, 16));
		//System.out.println("******** start time hh: " + stime.substring(11, 13));
		//System.out.println("******** end time hh: " + end_time.substring(11, 13));
		//System.out.println("******** calculated duration_h: " + duration_h);
		
		if (duration_h == 0){
			duration = 1;
		}
		//System.out.println("******** start time mm: " + stime.substring(14, 16));
		//System.out.println("******** end time mm: " + end_time.substring(14, 16));
		
		//System.out.println("******** start time ss: " + stime.substring(17, 19));
		//System.out.println("******** end time ss: " + end_time.substring(17, 19));
		
		if (duration_h > 0 && Long.parseLong(end_time.substring(14, 16) + end_time.substring(17, 19)) 
				<= Long.parseLong(stime.substring(14, 16) + stime.substring(17, 19))) {
			duration  = duration_h;
		}
		
		else{
			duration = duration_h + 1;
		}
		
		//System.out.println("******** calculated duration: " + duration);
		
		cost = duration * charge;
		
		//System.out.println("******** calculated cost: " + cost);
		
		balance = balance - cost;
		
		//System.out.println("******** remaining balance: " + balance);
	
		OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(ontology));
		balance_pro.accept(remover);
		
		manager.applyChanges(remover.getChanges());
        remover.reset();
        manager.saveOntology(ontology);
		OWLDataPropertyAssertionAxiom dpass = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(balance_pro, user_ind, balance);
		AddAxiom change = new AddAxiom(ontology, dpass);
        manager.applyChange(change);
        manager.saveOntology(ontology);
        
        //to update balance in exe env for Share
        String share_file = mfile_path + share + ".owl";
        System.out.println("*****share file path: " + share_file);
    	
    	OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		OWLOntology ontology2 = manager2.loadOntology(IRI.create(share_file));
		//String ns = "http://www.owl-ontologies.com/alliance#";
		
		PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
		//System.out.println("part2 done.");
		
		reasoner2.getKB().realize();
		
		OWLNamedIndividual worker_ind = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + worker));
		OWLDataProperty  bal_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
		
		double share_balance = Double.parseDouble(worker_ind.getDataPropertyValues(bal_pro, ontology2).iterator().next().getLiteral());
	    System.out.println("******** worker's orginal balance: " + share_balance);
	    
	    share_balance = share_balance - cost;
	    System.out.println("******** worker's remaining balance: " + share_balance);
	    OWLEntityRemover remover2 = new OWLEntityRemover(manager2, Collections.singleton(ontology2));
	    bal_pro.accept(remover2);
		
		manager2.applyChanges(remover2.getChanges());
        remover2.reset();
        manager2.saveOntology(ontology2);
		OWLDataPropertyAssertionAxiom dpass2 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(bal_pro, worker_ind, share_balance);
		AddAxiom change2 = new AddAxiom(ontology2, dpass2);
        manager2.applyChange(change2);
        manager2.saveOntology(ontology2);
}
    
  //reduce balance with actually consumed cost or cpu time after job completion
    public static void mPolicyShareCompleteReduce(long cont_id, String end_time) throws OWLOntologyCreationException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, OWLOntologyStorageException, InterruptedException{
    	double balance;
    	double cpu_balance;
    	//System.out.println("Service Broker is updating balances for job completion");
    	System.out.println("Service Broker is updating balances for the stop command received.");
    	Map<String, String> values = NegotiationDB.getPaymentInfo(cont_id);
		// get service in the contract, and get total cost of the service, then update ontology
		
		// in this program, cost is the total cost of service, while charge is unit price
		double charge = Double.parseDouble(values.get("charge"));
		double maxCost = Double.parseDouble(values.get("maxCost"));
		String username = values.get("username");
		String stime = values.get("startTime");
		String share = values.get("share");
		String policy = share.substring(0, share.length()-5) + "Policy";
		String worker = values.get("worker");
		String measurement = values.get("measurement");
		long maxCpuTime = Long.parseLong(values.get("maxTotalCpuT"));
		int requiredCpuN = Integer.parseInt(values.get("requiredCpuN"));
    	//String mfile = "file:/opt/AHE3/ontology/mPolicy.owl";
    	String mfile = mfile_path + policy +".owl";
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(mfile));
		//String ns = "http://www.owl-ontologies.com/alliance#";
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		//System.out.println("part2 done.");
		
		reasoner.getKB().realize();
		
		if(requiredCpuN == 0){
			requiredCpuN = 1;
		}
		
		if(measurement.equalsIgnoreCase("second")){
			//to update cpu time after steering being stopped
			//to update policy: user's remaining cpu time
			long duration = 0;
			
			long duration_h = Long.parseLong(end_time.substring(11, 13)) - Long.parseLong(stime.substring(11, 13));
			long duration_m = Long.parseLong(end_time.substring(14, 16)) - Long.parseLong(stime.substring(14, 16));
			long duration_s = Long.parseLong(end_time.substring(17, 19)) - Long.parseLong(stime.substring(17, 19));
			//System.out.println("=======timer complete duration_h: " + duration_h);
			//System.out.println("=======timer complete duration_m: " + duration_m);
			//System.out.println("=======timer complete duration_s: " + duration_s);
			
			duration = duration_h * 3600 + duration_m * 60 + duration_s;
			System.out.println("Sercice Broker, the time duration (seconds) that the application has run: " + duration);
			
			OWLDataProperty  cpuT_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "cpuTime"));
			
			int pri = NegotiationDB.getOverPri(cont_id);
		
			OWLNamedIndividual user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + username));
		    /*String response;
			//if not over privilege
		    if (pri == 1){
		        //user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "normal"));
		        response = "normal";
			}
			else{
				//if over privilge, to update manager's balance in MappingPolicy
				//user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "super"));
				response = "super";
			}*/
			
			cpu_balance = (long) user_ind.getDataPropertyValues(cpuT_pro, ontology).iterator().next().parseDouble();
			System.out.println("Service Broker, the CURRENT remaining CPU time in MappingPolicy for user " + username + " is: " + cpu_balance);
			
			OWLDataPropertyAssertionAxiom dpass1 = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpuT_pro, user_ind, cpu_balance);
			RemoveAxiom remove_change = new RemoveAxiom(ontology, dpass1);
			manager.applyChange(remove_change);
	        manager.saveOntology(ontology);
			
			cpu_balance = (double) (cpu_balance + maxCpuTime - duration * requiredCpuN);
			System.out.println("Service Broker, the NEW remaining CPU time in MappingPolicy for user " + username + " is: " + cpu_balance);
				
				//System.out.println("******** remaining balance: " + balance);
			
		    /*OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(ontology));
		    cpuT_pro.accept(remover);
				
			manager.applyChanges(remover.getChanges());
		    remover.reset();
		    manager.saveOntology(ontology);*/
		    OWLDataPropertyAssertionAxiom dpass = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpuT_pro, user_ind, cpu_balance);
		    AddAxiom change = new AddAxiom(ontology, dpass);
		    manager.applyChange(change);
		    manager.saveOntology(ontology);
			
		        
		    //to update balance in exe env for Share
		    String share_file = mfile_path + share + ".owl";
		    //System.out.println("*****share file path: " + share_file);
		    	
		    OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
			OWLOntology ontology2 = manager2.loadOntology(IRI.create(share_file));
				//String ns = "http://www.owl-ontologies.com/alliance#";
				
		    PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
				//System.out.println("part2 done.");
				
			reasoner2.getKB().realize();
				  
		    OWLNamedIndividual worker_ind = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + worker));
			OWLDataProperty  cpu_bal_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "cpuTime"));
				
			double share_balance = Double.parseDouble(worker_ind.getDataPropertyValues(cpu_bal_pro, ontology2).iterator().next().getLiteral());
			//System.out.println("******** worker's orginal balance: " + share_balance);
			
			System.out.println("Service Broker, the CURRENT remaining CPU time in Share for resource " + worker + " is: " + share_balance);
			
			OWLDataPropertyAssertionAxiom dpass2 = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpu_bal_pro, worker_ind, share_balance);
			RemoveAxiom remove_change2 = new RemoveAxiom(ontology2, dpass2);
			manager2.applyChange(remove_change2);
	        manager2.saveOntology(ontology2);
			    
			share_balance = share_balance + maxCpuTime - duration * requiredCpuN;
			System.out.println("Service Broker, the NEW remaining CPU time in Share for resource " + worker + " is: " + share_balance);
			//System.out.println("******** worker's remaining balance: " + share_balance);
			/*OWLEntityRemover remover2 = new OWLEntityRemover(manager2, Collections.singleton(ontology2));
			cpu_bal_pro.accept(remover2);
				
		    manager2.applyChanges(remover2.getChanges());
		    remover2.reset();
		    manager2.saveOntology(ontology2);*/
		    OWLDataPropertyAssertionAxiom dpass3 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpu_bal_pro, worker_ind, share_balance);
		    AddAxiom change2 = new AddAxiom(ontology2, dpass3);
		    manager2.applyChange(change2);
		    manager2.saveOntology(ontology2);
		}
		
		else{
		
		    OWLDataProperty  balance_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
		
		    OWLNamedIndividual user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + username));
		    
		    OWLDataProperty  maxCost_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "maxCost"));
		
		    balance = user_ind.getDataPropertyValues(balance_pro, ontology).iterator().next().parseDouble();
		    System.out.println("Service Broker, the requester " + username + "'s CURRENT balance is: " + balance);
		
		    double user_maxCost = user_ind.getDataPropertyValues(maxCost_pro, ontology).iterator().next().parseDouble();
		    System.out.println("In MappingPolicy ontology, the user's maxCost is: " + user_maxCost);
		    
		    long duration = 0;
		
		//to calculate duration as hours
		//if start_mm >= end_mm, then hh = duration_h
		//if start_mm < end_mm, then hh = duration_h + 1
		    long duration_h = Long.parseLong(end_time.substring(11, 13)) - Long.parseLong(stime.substring(11, 13));
		//long min_diff = Long.parseLong(end_time.substring(15, 16)) - Long.parseLong(stime.substring(15, 16));
		//System.out.println("******** start time hh: " + stime.substring(11, 13));
		//System.out.println("******** end time hh: " + end_time.substring(11, 13));
		//System.out.println("******** calculated duration_h: " + duration_h);
		
		    if (duration_h == 0){
			    duration = 1;
		    }
		//System.out.println("******** start time mm: " + stime.substring(14, 16));
		//System.out.println("******** end time mm: " + end_time.substring(14, 16));
		
		//System.out.println("******** start time ss: " + stime.substring(17, 19));
		//System.out.println("******** end time ss: " + end_time.substring(17, 19));
		
		    if (duration_h > 0 && Long.parseLong(end_time.substring(14, 16) + end_time.substring(17, 19)) 
				    <= Long.parseLong(stime.substring(14, 16) + stime.substring(17, 19))) {
			    duration  = duration_h;
		    }
		
		    else{
			    duration = duration_h + 1;
		    }
		
		//System.out.println("******** calculated duration: " + duration);
		
		    cost = duration * charge;
		    System.out.println("The actual cost for job execution is: " + cost);
		    System.out.println("The maxCost reduced when the contract was formed: " + maxCost);
		//System.out.println("******** calculated cost: " + cost);
		    if(cost < maxCost){
		    	    	
		    	OWLDataPropertyAssertionAxiom dpass4 = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(balance_pro, user_ind, balance);
				RemoveAxiom remove_change4 = new RemoveAxiom(ontology, dpass4);
				manager.applyChange(remove_change4);
		        manager.saveOntology(ontology);
		    	
		        balance = balance + user_maxCost - cost;
		
		        System.out.println("Service Broker, the requester " + username + "'s NEW balance is: " + balance);
	
		        /*OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(ontology));
		        balance_pro.accept(remover);
		
		        manager.applyChanges(remover.getChanges());
                remover.reset();
                manager.saveOntology(ontology);*/
		        OWLDataPropertyAssertionAxiom dpass5 = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(balance_pro, user_ind, balance);
		        AddAxiom change = new AddAxiom(ontology, dpass5);
                manager.applyChange(change);
                manager.saveOntology(ontology);
	
        
            //to update balance in exe env for Share
                String share_file = mfile_path + share + ".owl";
                //System.out.println("*****share file path: " + share_file);
    	
    	        OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		        OWLOntology ontology2 = manager2.loadOntology(IRI.create(share_file));
		//String ns = "http://www.owl-ontologies.com/alliance#";
		
		        PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
		//System.out.println("part2 done.");
		
		        reasoner2.getKB().realize();
		  
		        OWLNamedIndividual worker_ind = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + worker));
		        OWLDataProperty  bal_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
		
		        System.out.println("Service Broker is updating the Share ontology.");
		        double share_balance = Double.parseDouble(worker_ind.getDataPropertyValues(bal_pro, ontology2).iterator().next().getLiteral());
	            System.out.println("The resource CURRENT balance is: " + share_balance);
	    
	            OWLDataPropertyAssertionAxiom dpass6 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(bal_pro, worker_ind, share_balance);
				RemoveAxiom remove_change6 = new RemoveAxiom(ontology2, dpass6);
				manager2.applyChange(remove_change6);
		        manager2.saveOntology(ontology2);
	            
	            share_balance = share_balance + maxCost - cost;
	            System.out.println("The resource NEW balance is: " + share_balance);
	            /*OWLEntityRemover remover2 = new OWLEntityRemover(manager2, Collections.singleton(ontology2));
	            bal_pro.accept(remover2);
		
		        manager2.applyChanges(remover2.getChanges());
                remover2.reset();
                manager2.saveOntology(ontology2);*/
		        OWLDataPropertyAssertionAxiom dpass7 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(bal_pro, worker_ind, share_balance);
		        AddAxiom change7 = new AddAxiom(ontology2, dpass7);
                manager2.applyChange(change7);
                manager2.saveOntology(ontology2);
            }
		}
}
    public static void mPolicyShareCompleteReduce(long cont_id, double duration) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, OWLOntologyCreationException, InterruptedException, OWLOntologyStorageException{
    	//double balance;
    	double cpu_balance;
    	
    	Map<String, String> values = NegotiationDB.getPaymentInfo(cont_id);
		
		String username = values.get("username");
		String share = values.get("share");
		String policy = share.substring(0, share.length()-5) + "Policy";
		String worker = values.get("worker");
		long maxCpuTime = Long.parseLong(values.get("maxTotalCpuT"));
		//int requiredCpuN = Integer.parseInt(values.get("requiredCpuN"));
    	//String mfile = "file:/opt/AHE3/ontology/mPolicy.owl";
    	/*String username = "Sofia";
		String share = "Cluster";
		String policy = "ClusterPolicy";
		String worker = "super";
		long maxCpuTime = 5;*/
    	String mfile = mfile_path + policy +".owl";
    	System.out.println("Service Broker is processing on " + policy);
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(mfile));
		//String ns = "http://www.owl-ontologies.com/alliance#";
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		//System.out.println("part2 done.");
		
		reasoner.getKB().realize();
    	
    	OWLDataProperty  cpuT_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "cpuTime"));
		
		/*int pri = NegotiationDB.getOverPri(cont_id);
		String response;
		if (pri == 1){
	        //user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "normal"));
	        response = "normal";
		}
		else{
			//if over privilge, to update manager's balance in MappingPolicy
			//user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "super"));
			response = "super";
		}*/
	
	    OWLNamedIndividual user_ind;

	    user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + username));
		
		cpu_balance = (long) user_ind.getDataPropertyValues(cpuT_pro, ontology).iterator().next().parseDouble();
		System.out.println("Service Broker, the CURRENT balance in MappingPolicy for user " + username + " after application execution is: " + cpu_balance);
		
		OWLDataPropertyAssertionAxiom dpass1 = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpuT_pro, user_ind, cpu_balance);
		RemoveAxiom remove_change = new RemoveAxiom(ontology, dpass1);
		manager.applyChange(remove_change);
        manager.saveOntology(ontology);
		
		cpu_balance = (double) (cpu_balance + maxCpuTime - duration);
		System.out.println("Service Broker, the NEW balance in MappingPolicy for user " + username + " after application execution is: " + cpu_balance);
			
			//System.out.println("******** remaining balance: " + balance);
		
	    /*OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(ontology));
	    cpuT_pro.accept(remover);
			
		manager.applyChanges(remover.getChanges());
	    remover.reset();
	    manager.saveOntology(ontology);*/
	    OWLDataPropertyAssertionAxiom dpass = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpuT_pro, user_ind, cpu_balance);
	    AddAxiom change = new AddAxiom(ontology, dpass);
	    manager.applyChange(change);
	    manager.saveOntology(ontology);
		
	        
	    //to update balance in exe env for Share
	    String share_file = mfile_path + share + ".owl";
	    System.out.println("Service Broker is processing on " + share);
	    System.out.println("*****share file path: " + share_file);
	    	
	    OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		OWLOntology ontology2 = manager2.loadOntology(IRI.create(share_file));
			//String ns = "http://www.owl-ontologies.com/alliance#";
			
	    PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
			//System.out.println("part2 done.");
			
		reasoner2.getKB().realize();
		String response;
		if(worker.contains("large")){
			response = "super";
		}
		else{
			response = "normal";
		}
			  
	    OWLNamedIndividual worker_ind = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + worker));
		OWLDataProperty  cpu_bal_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "cpuTime"));
			
		double share_balance = Double.parseDouble(worker_ind.getDataPropertyValues(cpu_bal_pro, ontology2).iterator().next().getLiteral());
		System.out.println("Service Broker, the CURRENT balance in Share for members with " + response + " privilege is: " + share_balance);
		
		OWLDataPropertyAssertionAxiom dpass2 = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpu_bal_pro, worker_ind, share_balance);
		RemoveAxiom remove_change2 = new RemoveAxiom(ontology2, dpass2);
		manager2.applyChange(remove_change2);
        manager2.saveOntology(ontology2);
		    
		share_balance = share_balance + maxCpuTime - duration;
		System.out.println("Service Broker, the NEW balance in Share for members with " + response + " privilege is: " + share_balance);
		/*OWLEntityRemover remover2 = new OWLEntityRemover(manager2, Collections.singleton(ontology2));
		cpu_bal_pro.accept(remover2);
			
	    manager2.applyChanges(remover2.getChanges());
	    remover2.reset();
	    manager2.saveOntology(ontology2);*/
	    OWLDataPropertyAssertionAxiom dpass3 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpu_bal_pro, worker_ind, share_balance);
	    AddAxiom change2 = new AddAxiom(ontology2, dpass3);
	    manager2.applyChange(change2);
	    manager2.saveOntology(ontology2);
    	
    }
    
    public static void mPolicyShareCompleteReReduce(long cont_id, String end_time) throws OWLOntologyCreationException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, OWLOntologyStorageException{
    	double balance;
    	long cpu_balance;
    	
    	Map<String, String> values = NegotiationDB.getPaymentInfo(cont_id);
		// get service in the contract, and get total cost of the service, then update ontology
		
		// in this program, cost is the total cost of service, while charge is unit price
		double charge = Double.parseDouble(values.get("charge"));
		double maxCost = Double.parseDouble(values.get("maxCost"));
		String username = values.get("username");
		String stime = values.get("startTime");
		String share = values.get("share");
		String policy = share.substring(0, share.length()-5) + "Policy";
		String worker = values.get("worker");
		String measurement = values.get("measurement");
		long maxCpuTime = Long.parseLong(values.get("maxDuration"));
		int requiredCpuN = Integer.parseInt("requiredCpuN");
    	//String mfile = "file:/opt/AHE3/ontology/mPolicy.owl";
    	String mfile = mfile_path + policy +".owl";
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(mfile));
		//String ns = "http://www.owl-ontologies.com/alliance#";
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		//System.out.println("part2 done.");
		
		reasoner.getKB().realize();
		
		if(measurement.equalsIgnoreCase("second")){
			//to update cpu time after steering being stopped
			//to update policy: user's remaining cpu time
			long duration = 0;
			
			long duration_h = Long.parseLong(end_time.substring(11, 13)) - Long.parseLong(stime.substring(11, 13));
			long duration_m = Long.parseLong(end_time.substring(14, 16)) - Long.parseLong(stime.substring(14, 16));
			long duration_s = Long.parseLong(end_time.substring(17, 19)) - Long.parseLong(stime.substring(17, 19));
			duration = duration_h * 3600 + duration_m * 60 + duration_s;
			
			OWLDataProperty  cpuT_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "cpuTime"));
			
			OWLNamedIndividual user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + username));
			
			cpu_balance = (long) user_ind.getDataPropertyValues(cpuT_pro, ontology).iterator().next().parseDouble();
			
			OWLDataPropertyAssertionAxiom dpass1 = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpuT_pro, user_ind, cpu_balance);
			RemoveAxiom remove_change = new RemoveAxiom(ontology, dpass1);
			manager.applyChange(remove_change);
	        manager.saveOntology(ontology);
			
			cpu_balance = cpu_balance - duration * requiredCpuN;

		    OWLDataPropertyAssertionAxiom dpass = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpuT_pro, user_ind, cpu_balance);
		    AddAxiom change = new AddAxiom(ontology, dpass);
		    manager.applyChange(change);
		    manager.saveOntology(ontology);
			
		        
		    //to update balance in exe env for Share
		    String share_file = mfile_path + share + ".owl";
		    System.out.println("*****share file path: " + share_file);
		    	
		    OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
			OWLOntology ontology2 = manager2.loadOntology(IRI.create(share_file));
				//String ns = "http://www.owl-ontologies.com/alliance#";
				
		    PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
				//System.out.println("part2 done.");
				
			reasoner2.getKB().realize();
				  
		    OWLNamedIndividual worker_ind = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + worker));
			OWLDataProperty  cpu_bal_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "cpuTime"));
				
			double share_balance = Double.parseDouble(worker_ind.getDataPropertyValues(cpu_bal_pro, ontology2).iterator().next().getLiteral());
			System.out.println("******** worker's orginal balance: " + share_balance);
			
			OWLDataPropertyAssertionAxiom dpass2 = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpu_bal_pro, worker_ind, share_balance);
			RemoveAxiom remove_change2 = new RemoveAxiom(ontology2, dpass2);
			manager2.applyChange(remove_change2);
	        manager2.saveOntology(ontology2);
			    
			share_balance = share_balance + maxCpuTime - duration * requiredCpuN;
			System.out.println("******** worker's remaining balance: " + share_balance);
			/*OWLEntityRemover remover2 = new OWLEntityRemover(manager2, Collections.singleton(ontology2));
			cpu_bal_pro.accept(remover2);
				
		    manager2.applyChanges(remover2.getChanges());
		    remover2.reset();
		    manager2.saveOntology(ontology2);*/
		    OWLDataPropertyAssertionAxiom dpass3 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpu_bal_pro, worker_ind, share_balance);
		    AddAxiom change2 = new AddAxiom(ontology2, dpass3);
		    manager2.applyChange(change2);
		    manager2.saveOntology(ontology2);
		}
		
		else if(measurement.equalsIgnoreCase("hour")){
		
		    OWLDataProperty  balance_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
		
		    OWLNamedIndividual user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + username));
		
		    balance = user_ind.getDataPropertyValues(balance_pro, ontology).iterator().next().parseDouble();
		
		    long duration = 0;
		
		//to calculate duration as hours
		//if start_mm >= end_mm, then hh = duration_h
		//if start_mm < end_mm, then hh = duration_h + 1
		    long duration_h = Long.parseLong(end_time.substring(11, 13)) - Long.parseLong(stime.substring(11, 13));
		//long min_diff = Long.parseLong(end_time.substring(15, 16)) - Long.parseLong(stime.substring(15, 16));
		//System.out.println("******** start time hh: " + stime.substring(11, 13));
		//System.out.println("******** end time hh: " + end_time.substring(11, 13));
		//System.out.println("******** calculated duration_h: " + duration_h);
		
		    if (duration_h == 0){
			    duration = 1;
		    }
		//System.out.println("******** start time mm: " + stime.substring(14, 16));
		//System.out.println("******** end time mm: " + end_time.substring(14, 16));
		
		//System.out.println("******** start time ss: " + stime.substring(17, 19));
		//System.out.println("******** end time ss: " + end_time.substring(17, 19));
		
		    if (duration_h > 0 && Long.parseLong(end_time.substring(14, 16) + end_time.substring(17, 19)) 
				    <= Long.parseLong(stime.substring(14, 16) + stime.substring(17, 19))) {
			    duration  = duration_h;
		    }
		
		    else{
			    duration = duration_h + 1;
		    }
		
		//System.out.println("******** calculated duration: " + duration);
		
		    cost = duration * charge;
		
		    System.out.println("******** execution cost when ontology update: " + cost);
		    System.out.println("******** max cost when ontology update: " + maxCost);
		//System.out.println("******** calculated cost: " + cost);
		    if(cost < maxCost){
		    	OWLDataPropertyAssertionAxiom dpass4 = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(balance_pro, user_ind, balance);
				RemoveAxiom remove_change4 = new RemoveAxiom(ontology, dpass4);
				manager.applyChange(remove_change4);
		        manager.saveOntology(ontology);
		    	
		        balance = balance - cost;
		
		System.out.println("******** user's remaining balance: " + balance);

		        OWLDataPropertyAssertionAxiom dpass5 = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(balance_pro, user_ind, balance);
		        AddAxiom change = new AddAxiom(ontology, dpass5);
                manager.applyChange(change);
                manager.saveOntology(ontology);
	
        
            //to update balance in exe env for Share
                String share_file = mfile_path + share + ".owl";
                System.out.println("*****share file path: " + share_file);
    	
    	        OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		        OWLOntology ontology2 = manager2.loadOntology(IRI.create(share_file));
		//String ns = "http://www.owl-ontologies.com/alliance#";
		
		        PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
		//System.out.println("part2 done.");
		
		        reasoner2.getKB().realize();
		  
		        OWLNamedIndividual worker_ind = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + worker));
		        OWLDataProperty  bal_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
		
		        double share_balance = Double.parseDouble(worker_ind.getDataPropertyValues(bal_pro, ontology2).iterator().next().getLiteral());
	            System.out.println("******** worker's orginal balance: " + share_balance);
	    
	            OWLDataPropertyAssertionAxiom dpass6 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(bal_pro, worker_ind, share_balance);
				RemoveAxiom remove_change6 = new RemoveAxiom(ontology2, dpass6);
				manager2.applyChange(remove_change6);
		        manager2.saveOntology(ontology2);
	            
	            share_balance = share_balance + maxCost - cost;
	            System.out.println("******** worker's remaining balance: " + share_balance);

		        OWLDataPropertyAssertionAxiom dpass7 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(bal_pro, worker_ind, share_balance);
		        AddAxiom change7 = new AddAxiom(ontology2, dpass7);
                manager2.applyChange(change7);
                manager2.saveOntology(ontology2);
            }
		}
}
    
    //reduce balance with maxCost or maxCpuTime
    public static String mPolicyShareMaxReduce(long cont_id) throws OWLOntologyCreationException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, OWLOntologyStorageException, InterruptedException{
    	double balance;
    	double cpuT;
    	System.out.println("Service Broker updates balances after successful negotiation for a contract with ID: " + cont_id);
        Map<String, String> values = NegotiationDB.getPaymentInfo(cont_id);
		// get service in the contract, and get total cost of the service, then update ontology
		
		// in this program, cost is the total cost of service, while charge is unit price
		//double charge = Double.parseDouble(values.get("charge"));
		String username = values.get("username");
		String share = values.get("share");
		String policy = share.substring(0, share.length()-5) + "Policy";
		String worker = values.get("worker");
		String provider = values.get("provider");
		String measurement = values.get("measurement");
    	String pfile = mfile_path + policy +".owl";
    	
    	//for test
    	/*String username = "Sofia";
		String share = "Cluster";
		String policy = "ClusterPolicy";
		String worker = "cluster.uom.large";
		String provider = "UoM_cluster";
		String measurement = "second";
		String pfile = mfile_path + policy +".owl";*/
    	
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology policy_ontology = manager.loadOntology(IRI.create(pfile));
		//String ns = "http://www.owl-ontologies.com/alliance#";
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( policy_ontology );
		//System.out.println("part2 done.");
		
		reasoner.getKB().realize();
		
		
		if(measurement.equalsIgnoreCase("hour")){
			double maxCost = 6;
			//double maxCost = Double.parseDouble(values.get("maxCost"));
		    OWLDataProperty  balance_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
		
		    OWLNamedIndividual user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + username));
		
		    balance = user_ind.getDataPropertyValues(balance_pro, policy_ontology).iterator().next().parseDouble();
		    
		    System.out.println("Service Broker, the CURRENT balance after max reduction for user " + username + " in Share is: " + balance);
		
		    OWLDataPropertyAssertionAxiom dpass1 = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(balance_pro, user_ind, balance);
		    RemoveAxiom remove_change = new RemoveAxiom(policy_ontology, dpass1);
		    manager.applyChange(remove_change);
            manager.saveOntology(policy_ontology);
		//System.out.println("******** calculated cost: " + cost);
		
		    balance = balance - maxCost;
		    System.out.println("Service Broker, the NEW balance after max reduction for user " + username + " in Share is: " + balance);
		//System.out.println("******** remaining balance: " + balance);
	
		/*OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(ontology));
		balance_pro.accept(remover);
		
		manager.applyChanges(remover.getChanges());
        remover.reset();
        manager.saveOntology(ontology);*/
		
		    OWLDataPropertyAssertionAxiom dpass2 = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(balance_pro, user_ind, balance);
		    AddAxiom add_change = new AddAxiom(policy_ontology, dpass2);
            manager.applyChange(add_change);
            manager.saveOntology(policy_ontology);
        
            //to update balance in exe env for Share
            String share_file = mfile_path + share + ".owl";
            System.out.println("The Share ontology " + share_file + " is being processed.");
    	
    	    OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		    OWLOntology ontology2 = manager2.loadOntology(IRI.create(share_file));
		//String ns = "http://www.owl-ontologies.com/alliance#";
		
		    PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
		 //System.out.println("part2 done.");
		
		    reasoner2.getKB().realize();
		
		    OWLNamedIndividual worker_ind = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + worker));
		    OWLDataProperty  bal_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
		
		    double share_balance = Double.parseDouble(worker_ind.getDataPropertyValues(bal_pro, ontology2).iterator().next().getLiteral());
		    System.out.println("Service Broker, the CURRENT balance for instance " + worker_ind.toStringID().substring(39) + " in Share is: " + share_balance);
		    
		    //System.out.println("******** worker's orginal balance: " + share_balance);
	    
	        OWLDataPropertyAssertionAxiom dpass4 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(bal_pro, worker_ind, share_balance);
		    RemoveAxiom remove_change4 = new RemoveAxiom(ontology2, dpass4);
		    manager2.applyChange(remove_change4);
            manager2.saveOntology(ontology2);
	    
	        share_balance = share_balance - maxCost;
	        System.out.println("Service Broker, the NEW balance for instance " + worker_ind.toStringID().substring(39) + " in Share is: " + share_balance);
	        //System.out.println("******** worker's remaining balance: " + share_balance);
	    /*OWLEntityRemover remover2 = new OWLEntityRemover(manager2, Collections.singleton(ontology2));
	    bal_pro.accept(remover2);
		
		manager2.applyChanges(remover2.getChanges());
        remover2.reset();
        manager2.saveOntology(ontology2);*/
		    OWLDataPropertyAssertionAxiom dpass3 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(bal_pro, worker_ind, share_balance);
		    AddAxiom change2 = new AddAxiom(ontology2, dpass3);
            manager2.applyChange(change2);
            manager2.saveOntology(ontology2);
		}
		else{
			
			int pri = NegotiationDB.getOverPri(cont_id);

			//long maxTotalCpuT = 7;
			long maxTotalCpuT = Long.parseLong(values.get("maxTotalCpuT"));
		    OWLDataProperty  cpuT_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "cpuTime"));
		
		    //to following would be changed to reduce user's balance if change the new idea: balance for user, 'normal' and 'super'		    
		    
		    OWLNamedIndividual user_ind;
		    String response;
			//if not over privilege
		    user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + username));
			if (pri == 1){
		        //user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "normal"));
		        response = "normal";
			}
			else{
				//if over privilge, to update manager's balance in MappingPolicy
				//user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "super"));
				response = "super";
			}
		
		    cpuT = user_ind.getDataPropertyValues(cpuT_pro, policy_ontology).iterator().next().parseDouble();
		    System.out.println("Service Broker, the CURRENT remaining CPU time for user " + username + " in MappingPolicy is: " + cpuT);
		
		    OWLDataPropertyAssertionAxiom dpass1 = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpuT_pro, user_ind, cpuT);
		    RemoveAxiom remove_change = new RemoveAxiom(policy_ontology, dpass1);
		    manager.applyChange(remove_change);
            manager.saveOntology(policy_ontology);
		//System.out.println("******** calculated cost: " + cost);
		
		    cpuT = cpuT - (double) maxTotalCpuT;
		    System.out.println("Service Broker, the MAXIMUM value for CPU time set by the manager is: " + maxTotalCpuT);
		    System.out.println("Service Broker, the NEW balance, which is reduced by the default value set by the manager, of the user is: " + cpuT);
		
		//System.out.println("******** remaining balance: " + balance);
	
		/*OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(ontology));
		balance_pro.accept(remover);
		
		manager.applyChanges(remover.getChanges());
        remover.reset();
        manager.saveOntology(ontology);*/
		
		    OWLDataPropertyAssertionAxiom dpass2 = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpuT_pro, user_ind, cpuT);
		    AddAxiom add_change = new AddAxiom(policy_ontology, dpass2);
            manager.applyChange(add_change);
            manager.saveOntology(policy_ontology);
            
        
            //to update balance in exe env for Share
            //String share_file = mfile_path + "ClusterShare.owl";
            String share_file = mfile_path + share + ".owl";
            //System.out.println("*****share file path: " + share_file);
    	
    	    OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		    OWLOntology ontology2 = manager2.loadOntology(IRI.create(share_file));
		//String ns = "http://www.owl-ontologies.com/alliance#";
		
		    PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
		 //System.out.println("part2 done.");
		
		    reasoner2.getKB().realize();
		
		    OWLNamedIndividual worker_ind = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + worker));
		    OWLDataProperty  cpuT_share_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "cpuTime"));
		
		    double share_cpuT = Double.parseDouble(worker_ind.getDataPropertyValues(cpuT_share_pro, ontology2).iterator().next().getLiteral());
		    System.out.println("Service Broker, the CURRENT balance for " + worker + " in Share is: " + share_cpuT);
	    
	        OWLDataPropertyAssertionAxiom dpass4 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpuT_share_pro, worker_ind, share_cpuT);
		    RemoveAxiom remove_change4 = new RemoveAxiom(ontology2, dpass4);
		    manager2.applyChange(remove_change4);
            manager2.saveOntology(ontology2);
	    
            share_cpuT = share_cpuT - (double) maxTotalCpuT;
            System.out.println("Service Broker, the NEW balance,which is reduced by the default value set by the manager, for resource with " + response + " privilege is: " + share_cpuT);
	    /*OWLEntityRemover remover2 = new OWLEntityRemover(manager2, Collections.singleton(ontology2));
	    bal_pro.accept(remover2);
		
		manager2.applyChanges(remover2.getChanges());
        remover2.reset();
        manager2.saveOntology(ontology2);*/
		    OWLDataPropertyAssertionAxiom dpass3 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpuT_share_pro, worker_ind, share_cpuT);
		    AddAxiom change2 = new AddAxiom(ontology2, dpass3);
            manager2.applyChange(change2);
            manager2.saveOntology(ontology2);
		}
		return provider;
}
    
    public static String shareMaxReduce(long cont_id) throws OWLOntologyCreationException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, OWLOntologyStorageException{
    	// to reduce share policy's (site's) balance only
    	//double balance;
    	//double cpuT;
    	Map<String, String> values = NegotiationDB.getPaymentInfo(cont_id);
		// get service in the contract, and get total cost of the service, then update ontology
		
		// in this program, cost is the total cost of service, while charge is unit price
		//double charge = Double.parseDouble(values.get("charge"));
		//String username = values.get("username");
		//String stime = values.get("startTime");
		String share = values.get("share");
		String policy = share.substring(0, share.length()-5) + "Policy";
		String worker = values.get("worker");
		String provider = values.get("provider");
		String measurement = values.get("measurement");
    	//String mfile = "file:/opt/AHE3/ontology/mPolicy.owl";
    	String sfile = mfile_path + policy +".owl";
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(sfile));
		//String ns = "http://www.owl-ontologies.com/alliance#";
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		//System.out.println("part2 done.");
		
		reasoner.getKB().realize();
		
		String share_file = mfile_path + share + ".owl";
		
		System.out.println("*****share file path: " + share_file);
		OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
	    OWLOntology ontology2 = manager2.loadOntology(IRI.create(share_file));
	//String ns = "http://www.owl-ontologies.com/alliance#";
	
	    PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
	 //System.out.println("part2 done.");
	
	    reasoner2.getKB().realize();
	
		
		if(measurement.equalsIgnoreCase("hour")){
			double maxCost = Double.parseDouble(values.get("maxCost"));
		   // OWLDataProperty  balance_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
        
            //to update balance in exe env for Share
            //String share_file = mfile_path + share + ".owl";
            //System.out.println("*****share file path: " + share_file);
		
		    OWLNamedIndividual worker_ind = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + worker));
		    OWLDataProperty  bal_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
		
		    double share_balance = Double.parseDouble(worker_ind.getDataPropertyValues(bal_pro, ontology2).iterator().next().getLiteral());
	        //System.out.println("******** worker's orginal balance: " + share_balance);
	        System.out.println("Service Broker renegotiation, instance " + worker_ind.toStringID().substring(39) + " CURRENT balance is: " + share_balance);
	    
	        OWLDataPropertyAssertionAxiom dpass4 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(bal_pro, worker_ind, share_balance);
		    RemoveAxiom remove_change4 = new RemoveAxiom(ontology2, dpass4);
		    manager2.applyChange(remove_change4);
            manager2.saveOntology(ontology2);
	    
	        share_balance = share_balance - maxCost;
	        System.out.println("Service Broker renegotiation, instance " + worker_ind.toStringID().substring(39) + " NEW balance is: " + share_balance);
	        //System.out.println("******** worker's remaining balance: " + share_balance);

		    OWLDataPropertyAssertionAxiom dpass3 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(bal_pro, worker_ind, share_balance);
		    AddAxiom change2 = new AddAxiom(ontology2, dpass3);
            manager2.applyChange(change2);
            manager2.saveOntology(ontology2);
		}
		else{
			long maxDuration = Long.parseLong(values.get("maxDuration"));
		    //OWLDataProperty  cpuT_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "cpuTime"));

            //String share_file = mfile_path + share + ".owl";
            
		
		    OWLNamedIndividual worker_ind = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + worker));
		    OWLDataProperty  cpuT_share_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "cpuTime"));
		
		    double share_cpuT = Double.parseDouble(worker_ind.getDataPropertyValues(cpuT_share_pro, ontology2).iterator().next().getLiteral());
	        System.out.println("******** worker's orginal balance: " + share_cpuT);
	    
	        OWLDataPropertyAssertionAxiom dpass4 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpuT_share_pro, worker_ind, share_cpuT);
		    RemoveAxiom remove_change4 = new RemoveAxiom(ontology2, dpass4);
		    manager2.applyChange(remove_change4);
            manager2.saveOntology(ontology2);
	    
            share_cpuT = share_cpuT - (double) maxDuration;
	        System.out.println("******** worker's remaining balance: " + share_cpuT);
	    /*OWLEntityRemover remover2 = new OWLEntityRemover(manager2, Collections.singleton(ontology2));
	    bal_pro.accept(remover2);
		
		manager2.applyChanges(remover2.getChanges());
        remover2.reset();
        manager2.saveOntology(ontology2);*/
		    OWLDataPropertyAssertionAxiom dpass3 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpuT_share_pro, worker_ind, share_cpuT);
		    AddAxiom change2 = new AddAxiom(ontology2, dpass3);
            manager2.applyChange(change2);
            manager2.saveOntology(ontology2);
		}
		return provider;
}
    
    
    public static void mPolicyCloudReduce(double max_cost, String policy, String user) throws OWLOntologyCreationException, OWLOntologyStorageException{
    	String mfile = mfile_path + policy +".owl";
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(mfile));
		//String ns = "http://www.owl-ontologies.com/alliance#";
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		//System.out.println("part2 done.");
		
		reasoner.getKB().realize();
		
        OWLDataProperty  balance_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
		
		OWLNamedIndividual user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + user));
		
		double balance = Double.parseDouble(user_ind.getDataPropertyValues(balance_pro, ontology).iterator().next().getLiteral());
		   System.out.println("******** user's orginal balance: " + balance);
		   
        balance = balance - max_cost;

        System.out.println("******** user's new balance: " + balance);
        OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(ontology));
        balance_pro.accept(remover);

        manager.applyChanges(remover.getChanges());
        remover.reset();
        manager.saveOntology(ontology);
        OWLDataPropertyAssertionAxiom dpass = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(balance_pro, user_ind, balance);
        AddAxiom change = new AddAxiom(ontology, dpass);
        manager.applyChange(change);
        manager.saveOntology(ontology);
    }
    // suppose to update a specific Share
    // to make updates to the execution environment, which will execute the job
//public final static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException{
   public static void shareGridReduce() throws OWLOntologyCreationException, OWLOntologyStorageException{
	    //String sfile = "file:/opt/AHE3/ontology/share.owl";
	    String sfile = "file:/Users/zeqianmeng/Desktop/ontology/share.owl";
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(sfile));
		String ns = "http://www.owl-ontologies.com/alliance#";
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		//System.out.println("part2 done.");
		
		reasoner.getKB().realize();
		
		int req_mem = 5;
		int req_cpu = 5;
		
		String ecpu = "PhysicalCPUs";
		String emem = "VirtualMemorySize";
		String exeEn = "wn109.awsec2.alliance";
		
		int rem_mem, rem_cpu;
		int new_mem, new_cpu;
		
		OWLDataProperty  Avalcpu = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + ecpu));
		OWLDataProperty  Avalmem = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + emem));
		OWLNamedIndividual exe_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + exeEn));
		
		rem_mem = exe_ind.getDataPropertyValues(Avalmem, ontology).iterator().next().parseInteger();
		rem_cpu = exe_ind.getDataPropertyValues(Avalcpu, ontology).iterator().next().parseInteger();
		
		new_mem = rem_mem - req_mem;
		new_cpu = rem_cpu - req_cpu;
		System.out.println(rem_mem + "===" + rem_cpu);
		
		OWLEntityRemover remover_mem = new OWLEntityRemover(manager, Collections.singleton(ontology));
		//userA.getDataPropertyValues(Rmem, ontology).clear();
		Avalmem.accept(remover_mem);
		//userA.getDataPropertyValues(Rmem, ontology).add(memLit);
		//remover.visit(userA);
		//OWLIndividualVisitor vis = null;
		//Rmem.accept(remover);
		//userA.accept(vis);
		manager.applyChanges(remover_mem.getChanges());
        remover_mem.reset();
        
        manager.saveOntology(ontology);
		OWLDataPropertyAssertionAxiom dpass = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(Avalmem, exe_ind, new_mem);
		AddAxiom change = new AddAxiom(ontology, dpass);
        manager.applyChange(change);
        manager.saveOntology(ontology);
        
        System.out.println("user's remaining memory: " + exe_ind.getDataPropertyValues(Avalmem, ontology).iterator().next().parseInteger());
        
        //change info in user's available cpu in policy
        OWLEntityRemover remover_cpu = new OWLEntityRemover(manager, Collections.singleton(ontology));
		//userA.getDataPropertyValues(Rmem, ontology).clear();
        Avalcpu.accept(remover_cpu);
		//userA.getDataPropertyValues(Rmem, ontology).add(memLit);
		//remover.visit(userA);
		//OWLIndividualVisitor vis = null;
		//Rmem.accept(remover);
		//userA.accept(vis);
		manager.applyChanges(remover_cpu.getChanges());
        remover_cpu.reset();
        
        manager.saveOntology(ontology);
		OWLDataPropertyAssertionAxiom dpass2 = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(Avalcpu, exe_ind, new_cpu);
		AddAxiom change2 = new AddAxiom(ontology, dpass2);
        manager.applyChange(change2);
        manager.saveOntology(ontology);
        
        System.out.println("user's remaining cpu: " + exe_ind.getDataPropertyValues(Avalcpu, ontology).iterator().next().parseInteger());
		//}
		
		System.out.println("*************DONE");
    }

   public static void shareCloudReduce(long contract_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, OWLOntologyCreationException, OWLOntologyStorageException{
   //public static void shareCloudReduce() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, OWLOntologyCreationException, OWLOntologyStorageException{
	   // get cost from contract info stored in db: user, charge, and stime
	   Map<String, String> values = NegotiationDB.getPaymentInfo(contract_id);
	   double cost_c = cost;
	   String service = values.get("service");
	   //String stime = values.get("stime");
	   
	   //String sfile = "file:/opt/AHE3/ontology/share.owl";
   	   String sfile = "file:/Users/zeqianmeng/Desktop/ontology/share.owl";
	   OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	   OWLOntology ontology = manager.loadOntology(IRI.create(sfile));
	   String ns = "http://www.owl-ontologies.com/alliance#";
		
	   PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		//System.out.println("part2 done.");
		
	   reasoner.getKB().realize();
	   
	   // the info stored in db is service
	   //String service = "ce101.awsec2.alliance";
	   double balance;
	   OWLNamedIndividual service_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + service));
	   OWLObjectProperty  ExeEnv = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasExeEnvironment"));
	   OWLDataProperty  balance_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
	   
	   balance = Double.parseDouble(service_ind.getObjectPropertyValues(ExeEnv, ontology).iterator().next().getDataPropertyValues(balance_pro, ontology)
	   .iterator().next().getLiteral());
	   System.out.println("********" + balance);
	   
	   balance = balance - cost_c;
	   
	   OWLEntityRemover remover_cost = new OWLEntityRemover(manager, Collections.singleton(ontology));
		//userA.getDataPropertyValues(Rmem, ontology).clear();
	   balance_pro.accept(remover_cost);
		//userA.getDataPropertyValues(Rmem, ontology).add(memLit);
		//remover.visit(userA);
		//OWLIndividualVisitor vis = null;
		//Rmem.accept(remover);
		//userA.accept(vis);
		manager.applyChanges(remover_cost.getChanges());
		remover_cost.reset();
      
        manager.saveOntology(ontology);
		OWLDataPropertyAssertionAxiom dpass = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(balance_pro, service_ind.getObjectPropertyValues(ExeEnv, ontology).iterator().next(), balance);
		AddAxiom change = new AddAxiom(ontology, dpass);
        manager.applyChange(change);
        manager.saveOntology(ontology);
      
        System.out.println("user's remaining balance: " + service_ind.getObjectPropertyValues(ExeEnv, ontology).iterator().next().
   		   getDataPropertyValues(balance_pro, ontology).iterator().next().parseDouble());
	   
   }
   
    public static void shareClouBalanceReduce(String share, double max_cost, String worker) throws OWLOntologyCreationException, OWLOntologyStorageException{
    	String share_file = mfile_path + share + ".owl";
    	
    	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(share_file));
		//String ns = "http://www.owl-ontologies.com/alliance#";
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		//System.out.println("part2 done.");
		
		reasoner.getKB().realize();
		
		OWLNamedIndividual worker_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + worker));
		OWLDataProperty  bal_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
		
		double balance = Double.parseDouble(worker_ind.getDataPropertyValues(bal_pro, ontology).iterator().next().getLiteral());
				   System.out.println("******** worker's orginal balance: " + balance);
				   
	    balance = balance - max_cost;
	    
	    OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(ontology));
	    bal_pro.accept(remover);
	    
	    manager.applyChanges(remover.getChanges());
        remover.reset();
        manager.saveOntology(ontology);
        OWLDataPropertyAssertionAxiom dpass = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(bal_pro, worker_ind, balance);
		AddAxiom change = new AddAxiom(ontology, dpass);
        manager.applyChange(change);
        manager.saveOntology(ontology);
    }
    
   /* public static void main(String[] args) throws OWLOntologyCreationException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, OWLOntologyStorageException, InterruptedException{
 	   //double cost = 5.5;
    	//mPolicyShareCompleteReduce(11111, 6.0);
    	mPolicyShareMaxReduce(11111);
 	   System.out.println("done.");
    }*/
   
    public static void shareGridReduce(String service) throws OWLOntologyCreationException, OWLOntologyStorageException{
   	
    	//String sfile = "file:/opt/AHE3/ontology/share.owl";
    	String sfile = "file:/Users/zeqianmeng/Desktop/ontology/share.owl";
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(sfile));
		String ns = "http://www.owl-ontologies.com/alliance#";
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		//System.out.println("part2 done.");
		
		reasoner.getKB().realize();
		
		int rem_cpuT;
		int new_cpuT;
		int req_cpuT = 5;
		
		OWLDataProperty  cpu = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "CpuTime"));

		OWLNamedIndividual service_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + service));
		
		OWLObjectProperty  ExeEnv = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasExeEnvironment"));
		
		System.out.println("********" + service_ind.getObjectPropertyValues(ExeEnv, ontology).iterator().next().toStringID());
		rem_cpuT = Integer.parseInt(service_ind.getObjectPropertyValues(ExeEnv, ontology).iterator().next().getDataPropertyValues(cpu, ontology).iterator().next().getLiteral());
		
		new_cpuT = rem_cpuT - req_cpuT;
		System.out.println("===" + new_cpuT);
		
		OWLEntityRemover remover_cpuT = new OWLEntityRemover(manager, Collections.singleton(ontology));
		//userA.getDataPropertyValues(Rmem, ontology).clear();
		cpu.accept(remover_cpuT);
		//userA.getDataPropertyValues(Rmem, ontology).add(memLit);
		//remover.visit(userA);
		//OWLIndividualVisitor vis = null;
		//Rmem.accept(remover);
		//userA.accept(vis);
		manager.applyChanges(remover_cpuT.getChanges());
       remover_cpuT.reset();
       
       manager.saveOntology(ontology);
		OWLDataPropertyAssertionAxiom dpass = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(cpu, service_ind.getObjectPropertyValues(ExeEnv, ontology).iterator().next(), new_cpuT);
		AddAxiom change = new AddAxiom(ontology, dpass);
       manager.applyChange(change);
       manager.saveOntology(ontology);
       
       System.out.println("user's remaining cpu time: " + service_ind.getObjectPropertyValues(ExeEnv, ontology).iterator().next().
    		   getDataPropertyValues(cpu, ontology).iterator().next().parseDouble());
       
       //change info in user's available cpu in policy
       /*OWLEntityRemover remover_cpu = new OWLEntityRemover(manager, Collections.singleton(ontology));
		//userA.getDataPropertyValues(Rmem, ontology).clear();
       Avalcpu.accept(remover_cpu);
		//userA.getDataPropertyValues(Rmem, ontology).add(memLit);
		//remover.visit(userA);
		//OWLIndividualVisitor vis = null;
		//Rmem.accept(remover);
		//userA.accept(vis);
		manager.applyChanges(remover_cpu.getChanges());
       remover_cpu.reset();
       
       manager.saveOntology(ontology);
		OWLDataPropertyAssertionAxiom dpass2 = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(Avalcpu, exe_ind, new_cpu);
		AddAxiom change2 = new AddAxiom(ontology, dpass2);
       manager.applyChange(change2);
       manager.saveOntology(ontology);
       
       System.out.println("user's remaining cpu: " + exe_ind.getDataPropertyValues(Avalcpu, ontology).iterator().next().parseInteger());
		//}
*/		
		System.out.println("*************DONE");
   }
    /*public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
    	//updateShare("ce102.awsec2.alliance");
    	Calendar cal1 = Calendar.getInstance();
	      SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");
	//sdf1.format(cal1.getTime());
	      Calendar cal2 = Calendar.getInstance();
	//cal.getTime();
	      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
     	  String date = sdf1.format(cal1.getTime()) + "T" + sdf.format(cal2.getTime()) + "+7:00";
     	  System.out.println("current date: " + date);
     	  //this does not change anything in database
    	mPolicyShareCloudPartReduce(1324, date);
    	System.out.println("TEST DONE!");
    	
    	double max_cost = 2.0;
    	String mfile = "file:/Users/zeqianmeng/Desktop/ontology/CloudPolicy.owl";
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(mfile));
		//String ns = "http://www.owl-ontologies.com/alliance#";
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		//System.out.println("part2 done.");
		
		reasoner.getKB().realize();
		
        OWLDataProperty  balance_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
		
		OWLNamedIndividual user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "Sofia"));
		
		double balance = Double.parseDouble(user_ind.getDataPropertyValues(balance_pro, ontology).iterator().next().getLiteral());
		   System.out.println("******** user's orginal balance: " + balance);
		
        balance = balance - max_cost;
        
        OWLLiteral bal_lit = manager.getOWLDataFactory().getOWLLiteral(ns + 95.0);
        user_ind.getDataPropertyValues(balance_pro, ontology).add(bal_lit);

        System.out.println("******** user's new balance: " + balance);
        OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(ontology));
        balance_pro.accept(remover);

        manager.applyChanges(remover.getChanges());
        remover.reset();
        manager.saveOntology(ontology);
        OWLDataPropertyAssertionAxiom dpass = manager.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(balance_pro, user_ind, balance);
        AddAxiom change = new AddAxiom(ontology, dpass);
        manager.applyChange(change);
        manager.saveOntology(ontology);
    }*/
}
