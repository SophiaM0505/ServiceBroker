package negotiation.Ontology;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import negotiation.HibernateImp.NegHibConnect;
import negotiation.HibernateImp.Offer;
import negotiation.HibernateImp.Service;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class ServiceReasoning {
	static String provider_name;
	static String service_name;
	static double cost;
	static double mem;
	static int cpu_no;
	static String instance;
	static String user_name;
	static int required_cpu;
	final static String duration = "1";
	static String group;
	static String app;
	static String startT;
	static String owner;
	final static String path = "file:/Users/zeqianmeng/Desktop/ontology/";
	//public static boolean providerSearch() throws OWLOntologyCreationException{
	//public static boolean providerSearch(String osInput, String cpuModel, long start, long finish) throws OWLOntologyCreationException{
	//public final static void main(String[] args) throws OWLOntologyStorageException, OWLOntologyCreationException{
	//****** this would be service reasoning without any pre-available related info from user
	//****** but assume such info is available from previous reasoning results
	//public static String providerSearch(HashMap<String, String> resource) throws OWLOntologyCreationException{
	public static String providerSearch(String[] resource, String policy) throws OWLOntologyCreationException{

		String user_name= resource[0];
		String os_input = resource[1];
		String cpu_model = resource[2];
		//System.out.println("+++++++++" + resource[3]);
		double cpu_speed = Double.parseDouble(resource[3]);
		required_cpu = Integer.parseInt(resource[4]);
		//double max_cost = Double.parseDouble(resource[5]);
		    
		    //user_name = resource.get("username");
		    //duration = resource.get("duration");
		    //required_cpu = Integer.parseInt(resource.get("cpuNo"));
		
			boolean bal_result = false;
			//String provider = "AWS_EC2_Services";
			//String provider = "ComputingService";
			final String ns = "http://www.owl-ontologies.com/alliance#";
			//String ns1 = "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#";
			//String file = "file:/opt/AHE3/ontology/service.owl";
			
			String file = "file:/Users/zeqianmeng/Desktop/ontology/AwsServices.owl";
			//String file = "file:/Users/zeqianmeng/Desktop/ontology/cgo_backup/backup3/cgoTest.owl";
			//System.out.println("Reading ontology file " + file + "...");
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology = manager.loadOntology(IRI.create(file));
			
			PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
			
			//initiate for policy reasoning
			//String file2 = "file:/opt/AHE3/ontology/mPolicy.owl";
			String policy_file = path + policy + ".owl";
			//String file2 = "file:/Users/zeqianmeng/Desktop/ontology/ResvPolicy.owl";
			//String file = "file:/Users/zeqianmeng/Desktop/ontology/cgo_backup/backup3/cgoTest.owl";
			//System.out.println("Reading ontology file " + policy_file + "...");
			OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
			OWLOntology ontology2 = manager2.loadOntology(IRI.create(policy_file));
			
			PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
			
		    //System.out.println("*************PART1 START: to search for satisfied services");
			
		    OWLClass csCla = manager.getOWLDataFactory().getOWLClass(IRI.create(ns + "ComputingService"));
			NodeSet<OWLNamedIndividual> csSet = reasoner.getInstances(csCla, false);
			
			OWLClass serCla = manager.getOWLDataFactory().getOWLClass(IRI.create(ns + "Service"));
			NodeSet<OWLNamedIndividual> serSet = reasoner.getInstances(serCla, false);
			
			OWLClass enCla = manager.getOWLDataFactory().getOWLClass(IRI.create(ns + "ExecutionEnvironment"));
			NodeSet<OWLNamedIndividual> enSet = reasoner.getInstances(enCla, false);
			//System.out.println("========" + );
			//System.out.println(csSet.get);
			
			// this can get provider
			// you can fetch provider info beforehand, if demands met, store it; if not, just leave it.
			//System.out.println(serSet.getNodes().toArray()[0]);
			
			//provider = serSet.getNodes().toArray()[1].toString();
			
			String hasOs = "hasOSFamily";
			String hasCpu = "hasCPUModel";
			String cpuNo = "physicalCpus";
			
			String aOs;
			String aCpu;
			
			OWLObjectProperty  hasSer = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasService"));
			OWLObjectProperty  hasEn = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasExeEnvironment"));
			OWLObjectProperty  hasRes = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasResource"));
			OWLObjectProperty  hasOsOP = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + hasOs));
			OWLObjectProperty  hasCpuOP = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + hasCpu));
			//OWLNamedIndividual cs = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + provider));
			OWLNamedIndividual os = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + os_input));
			OWLNamedIndividual cpu = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + cpu_model));
			
			OWLNamedIndividual service = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "AwsServices"));
			//System.out.println(reasoner.getObjectPropertyValues(csSet.iterator().next().getRepresentativeElement(), hasCpuOP));
			//OWLDataProperty  mem = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "Mem"));
			OWLDataProperty  cpuNoPro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + cpuNo));
			OWLDataProperty  cpuSp = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "clockSpeed"));
			
			//HashMap<String, Service> serviceMap = new HashMap<String, Service>();
			Service service1 = new Service();
			String offers = "";
			for(Node<OWLNamedIndividual> csInds : csSet){ 
				//Computing Service
				//OWLNamedIndividual cs = csInds.getEntities();
				OWLNamedIndividual cs = csInds.getRepresentativeElement();
				
				//System.out.println("========" + cs.getTypes(ontology));
				//System.out.println("********" + cs.getTypes(ontology).iterator().next().equals(enCla));
				//if(cs.toStringID().contains("service")){
				if(cs.getTypes(ontology).iterator().next().equals(enCla)){

				Set<OWLIndividual> inds_oss = cs.getObjectPropertyValues(hasOsOP, ontology);

				Set<OWLIndividual> inds_res = cs.getObjectPropertyValues(hasRes, ontology);
			//System.out.println(inds_res.isEmpty());
			    //System.out.println("********" + inds_res.iterator().next().getIndividualsInSignature());
			    boolean output1 = inds_oss.iterator().next().toStringID().contains(os_input);
			    //boolean output1 = inds_ens.iterator().next().hasObjectPropertyValue(hasOsOP, os, ontology);
			    boolean output2 = inds_res.iterator().next().hasObjectPropertyValue(hasCpuOP, cpu, ontology);
			    boolean output = false;
			    if(output1&&output2){
			    	//cpu_no = inds_res.iterator().next().getDataPropertyValues(cpuNoPro, ontology).iterator().next().parseInteger();
			    	//System.out.println("=======" + cs.getDataPropertyValues(cpuNoPro, ontology).iterator().next().parseInteger());
			    	//System.out.println("======= required cpu no: " + required_cpu);
			    	cpu_no = cs.getDataPropertyValues(cpuNoPro, ontology).iterator().next().parseInteger();
			    	boolean output3 = cpu_no >= required_cpu;
			    	//System.out.println("======= cpu speed: " + inds_res.iterator().next().getDataPropertyValues(cpuSp, ontology)
			    		//	.iterator().next().parseDouble());
			    	//System.out.println("output3: " + output3);
			    	boolean output4 = inds_res.iterator().next().getDataPropertyValues(cpuSp, ontology)
			    			.iterator().next().parseDouble() >= cpu_speed;
			    	//System.out.println("output4: " + output4);
			    
			    output = output3&&output4;

			    }
			    else{
			    	output = false;
			    }
			/*// to calculate cost
			    OWLDataProperty  charge = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "charge"));
			    //System.out.println(reasoner.getObjectPropertyValues(cs, hasSer).getFlattened().iterator().hasNext());
			    Set<OWLLiteral> inds_charge = reasoner.getObjectPropertyValues(cs, hasEn).iterator().next().getRepresentativeElement().getDataPropertyValues(charge, ontology);
			    //Set<OWLLiteral> inds_charge = reasoner.getObjectPropertyValues(cs, hasSer).iterator().next().getRepresentativeElement().getDataPropertyValues(charge, ontology);
			    System.out.println("charge is: " + inds_charge.iterator().next().getLiteral());*/
			
			/*if(output){
				long duration = (finish - start)/3600;
				
			}
			else{
				result = false;
			}*/
			//result = output;
			if(output){
				
				//System.out.println("*************PART2 START: to calculate cost for available services");
				
				String bal_s = "balance";
				double URemBal;
				
				OWLNamedIndividual userAPol = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + user_name));
				OWLDataProperty  bal_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + bal_s));
				URemBal = userAPol.getDataPropertyValues(bal_pro, ontology2).iterator().next().parseDouble();
				
				OWLDataProperty  mem_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "virtualMemorySize"));
				//OWLDataProperty  bal_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + bal_s));
				
		    OWLDataProperty  charge_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "charge"));
		    //OWLObjectProperty  instance_pro = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasInstanceModel"));
		    //System.out.println(reasoner.getObjectPropertyValues(cs, hasSer).getFlattened().iterator().hasNext());
		    Set<OWLLiteral> inds_charge = cs.getDataPropertyValues(charge_pro, ontology);
		    //Set<OWLLiteral> inds_charge = reasoner.getObjectPropertyValues(cs, hasSer).iterator().next().getRepresentativeElement().getDataPropertyValues(charge, ontology);
		    double charge = inds_charge.iterator().next().parseDouble();
		    //service1.setCost(inds_charge.iterator().next().getLiteral());
		    cost = charge * Long.parseLong(duration);
		    bal_result = (cost <= URemBal);
		    //long offer_no = 1;
		    
		    OWLNamedIndividual service_ind = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "AwsServices"));
		    //OWLIndividual owner_ind;
		    //String owner;
		    
		    if (bal_result){
		    	
		    	//OWLObjectProperty  hasOwner = manager2.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasOwner"));
		    	//manager2.getOWLDataFactory().getOWLTopObjectProperty();
			    OWLObjectProperty  hasAdminDo = manager2.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasAdminDomain"));
			    //System.out.println("=======" + policy.getObjectPropertyValues(hasAdminDo, ontology2).iterator().next());
			    //owner_ind = policy.getObjectPropertyValues(hasAdminDo, ontology2).iterator().next().getObjectPropertyValues(hasOwner, ontology2).iterator().next();
			    //owner = owner_ind.toStringID().substring(39);
		    	mem = cs.getDataPropertyValues(mem_pro, ontology).iterator().next().parseDouble();;
		    	//mem = inds_res.iterator().next().getDataPropertyValues(mem_pro, ontology).iterator().next().parseInteger();
		    	//instance = inds_res.iterator().next().getObjectPropertyValues(instance_pro, ontology).iterator().next().toStringID().substring(39);
		    	service_name = cs.toStringID();
		    	String provider = service_ind.getObjectPropertyValues(hasAdminDo, ontology).iterator().next().getIndividualsInSignature().iterator().next().toStringID().substring(39);
		    	//System.out.println("-----------" + provider);
		    	//System.out.println("-----------" + service_ind.getObjectPropertyValues(hasAdminDo, ontology).iterator().next().getIndividualsInSignature());
		    	//service1.setService_name(service_name);
				/*service1.setProvider(provider_name);
				service1.setCpuNo(cpu_no);
				service1.setMemory(mem);
				service1.setInstance(instance);
				service1.setCharge(charge);
				service1.setCost(cost);
				
				// create offers for services and update database before returning values to Android
				//serviceMap.put(service_name, service1);
				Offer offer = new Offer();
				
				//contract = new Contract();
				offer.setAppname(app);
				offer.setId(offer_no);
				offer.setUsername(user_name);
				offer.setOwner(owner);
				offer.setGroupname(group);
				offer.setService(service1);
				offer.setStartTime(startT);
				//offer.setDuration(duration);
				
				NegHibConnect.hibOffer(offer);*/
		    	Random rand = new Random();
		   	    long offer_no = rand.nextInt(10000 + 1);
				//offers = offers + offer_no + ","+ provider_name + ", Cloud;";
		    	offers = offers + offer_no + "- Provider: " + provider +  ", CPU Number: " +
		    			cpu_no + ", cost: " + cost + ", Type: Cloud;";
				//offers.put(offer_no, provider + "Grid");
				//offer_no++;
		    	/*System.out.println("charge is: " + charge);
			    System.out.println("total cost is: " + cost);
			    System.out.println("cpu no is: " + cpu_no);
			    System.out.println("mem GiB is: " + mem);*/
			    //System.out.println("instace type is: " + instance);
		    }
		    
		    //System.out.println("charge is: " + inds_charge.iterator().next().getLiteral());
			}
			//return result;
			//System.out.println("the reasoning output is: " + bal_result);
			}			
			}
			return offers;
		}
	
	public static String newProviderSearch(String[] resource) throws OWLOntologyCreationException{

	    String user_name= resource[0];
	    String os_input = resource[1];
	    String cpu_model = resource[2];
	    double cpu_speed = Double.parseDouble(resource[3]);
	    int cpu_no = Integer.parseInt(resource[4]);

	
		boolean result = false;
		//String provider = "AWS_EC2_Services";
		//String provider = "ComputingService";
		final String ns = "http://www.owl-ontologies.com/alliance#";
		//String ns1 = "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#";
		//String file = "file:/opt/AHE3/ontology/service.owl";
		final String file = "file:/Users/zeqianmeng/Desktop/ontology/AwsServices.owl";
		//String file = "file:/Users/zeqianmeng/Desktop/ontology/cgo_backup/backup3/cgoTest.owl";
		System.out.println("Reading ontology file " + file + "...");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(file));
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		
		//initiate for policy reasoning
		//String file2 = "file:/opt/AHE3/ontology/mPolicy.owl";
		/*String file2 = "file:/Users/zeqianmeng/Desktop/ontology/ResvPolicy.owl";
		System.out.println("Reading ontology file " + file2 + "...");
		OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		OWLOntology ontology2 = manager2.loadOntology(IRI.create(file2));
		
		PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
		*/
	    System.out.println("*************PART1 START: to search for satisfied services");
	    
	    OWLClass enCla = manager.getOWLDataFactory().getOWLClass(IRI.create(ns + "ExecutionEnvironment"));
		NodeSet<OWLNamedIndividual> enSet = reasoner.getInstances(enCla, false);
		
		for(Node<OWLNamedIndividual> enInd : enSet) {
			OWLNamedIndividual exeEn = enInd.getRepresentativeElement();
		    System.out.println("111111" + exeEn.toStringID());

		}
	    
		String offers = null;
		/*String hasOs = "hasOSFamily";
		String hasCpu = "hasCPUModel";
		String cpuNo = "physicalCpus";
		
		String aOs;
		String aCpu;
		
		OWLObjectProperty  hasSer = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasService"));
		OWLObjectProperty  hasEn = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasExeEnvironment"));
		OWLObjectProperty  hasRes = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasResource"));
		OWLObjectProperty  hasOsOP = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + hasOs));
		OWLObjectProperty  hasCpuOP = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + hasCpu));
		//OWLNamedIndividual cs = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + provider));
		OWLNamedIndividual os = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + os_input));
		OWLNamedIndividual cpu = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + cpu_model));
		
		OWLNamedIndividual service = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "Service"));
		//System.out.println(reasoner.getObjectPropertyValues(csSet.iterator().next().getRepresentativeElement(), hasCpuOP));
		//OWLDataProperty  mem = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "Mem"));
		OWLDataProperty  cpuNoPro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + cpuNo));
		OWLDataProperty  cpuSp = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "clockSpeed"));
		
		//HashMap<String, Service> serviceMap = new HashMap<String, Service>();
		Service service1 = new Service();
		String offers = null;
		for(Node<OWLNamedIndividual> csInds : csSet){ 
			//Computing Service
			//OWLNamedIndividual cs = csInds.getEntities();
			OWLNamedIndividual cs = csInds.getRepresentativeElement();
			//System.out.println("@@@@@@@@@@" + cs.toStringID());
			//System.out.println("========" + cs.getTypes(ontology));
			//System.out.println("=======" + cs.getTypes(ontology));
			//if(cs.toStringID().contains("service")){
			if(cs.getTypes(ontology).iterator().next().equals(csCla)){
				
				//System.out.println("========" + reasoner.getSuperClasses(csCla, false));
				//System.out.println("========" + cs.getTypes(ontology).iterator().next().equals(csCla));
				//System.out.println("========" + serSet.iterator().next().contains(cs));
			//System.out.println(reasoner.getObjectPropertyValues(cs, hasEn).iterator().next());
			//System.out.println("=======" + reasoner.getObjectPropertyValues(cs, hasEn).iterator().next().getRepresentativeElement());
		//service individuals
		    Set<OWLIndividual> inds_oss = reasoner.getObjectPropertyValues(cs, hasEn).iterator().next().getRepresentativeElement().getObjectPropertyValues(hasOsOP, ontology);
		    //System.out.println("********" + inds_ens.iterator().next().toStringID().contains(osInput));
		//resource individuals
		    //Set<OWLIndividual> inds_res = reasoner.getObjectPropertyValues(cs, hasSer).iterator().next().iterator().next().getObjectPropertyValues(hasEn, ontology)
				//.iterator().next().getObjectPropertyValues(hasRes, ontology);
			
			//System.out.println("LOOK HERE: " + reasoner.getObjectPropertyValues(cs, hasEn).isEmpty());
		    Set<OWLIndividual> inds_res = reasoner.getObjectPropertyValues(cs, hasEn).iterator().next().iterator().next().getObjectPropertyValues(hasRes, ontology);
		//System.out.println(inds_res.isEmpty());
		    //System.out.println("********" + inds_res.iterator().next().getIndividualsInSignature());
		    boolean output1 = inds_oss.iterator().next().toStringID().contains(os_input);
		    //boolean output1 = inds_ens.iterator().next().hasObjectPropertyValue(hasOsOP, os, ontology);
		    boolean output2 = inds_res.iterator().next().hasObjectPropertyValue(hasCpuOP, cpu, ontology);
		    boolean output;
		    if(output1&&output2){
		    	cpu_no = inds_res.iterator().next().getDataPropertyValues(cpuNoPro, ontology).iterator().next().parseInteger();
		    	boolean output3 = cpu_no >= required_cpu;
		    	boolean output4 = inds_res.iterator().next().getDataPropertyValues(cpuSp, ontology)
		    			.iterator().next().parseDouble() >= cpu_speed;
		    
		    output = output3&&output4;
		//boolean output1 = reasoner.getObjectPropertyValues(cs, hasOsOP).iterator().next().iterator().next().hasObjectPropertyValue(hasCpuOP, cpu, ontology);
		//String output1 = reasoner.getObjectPropertyValues(cs, hasOsOP).iterator().next().iterator().next().toStringID();
		//System.out.println("*****OUTPUT: " + cs.getDifferentIndividuals(ontology).contains(os));
		//String output1 = cs.getObjectPropertyValues(hasOsOP, ontology).iterator().next().toString();
		//String output2 = cs.getObjectPropertyValues(hasCpuOP, ontology).iterator().next().toString();
		//System.out.println("****** OUTPUR1: " + output1 + ", OUTPUT2: " + output2);
		    //System.out.println("****** OUTPUT1: " + output1 + "; OUTPUT2: " + output2 + "; Final: " + output);
		    }
		    else{
		    	output = false;
		    }
		// to calculate cost
		    OWLDataProperty  charge = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "charge"));
		    //System.out.println(reasoner.getObjectPropertyValues(cs, hasSer).getFlattened().iterator().hasNext());
		    Set<OWLLiteral> inds_charge = reasoner.getObjectPropertyValues(cs, hasEn).iterator().next().getRepresentativeElement().getDataPropertyValues(charge, ontology);
		    //Set<OWLLiteral> inds_charge = reasoner.getObjectPropertyValues(cs, hasSer).iterator().next().getRepresentativeElement().getDataPropertyValues(charge, ontology);
		    System.out.println("charge is: " + inds_charge.iterator().next().getLiteral());
		
		if(output){
			long duration = (finish - start)/3600;
			
		}
		else{
			result = false;
		}
		result = output;
		if(result){
			for(Node<OWLNamedIndividual> serInd: serSet){
				OWLNamedIndividual ser = serInd.getRepresentativeElement();
				
				//System.out.println("======" + ser.toStringID());
				//System.out.println("*******" + ser.getObjectPropertyValues(hasRes, ontology));
		        //ser.getObjectPropertyValues(hasRes, ontology);
				//if(ser.getObjectPropertyValues(hasSer, ontology).iterator().hasNext()){
				if(ser.hasObjectPropertyValue(hasSer, cs, ontology)){
					provider_name = ser.toStringID().substring(39);
					//serviceMap.put("provider", provider_name);
					//System.out.println("$$$$$$$$$" + ser.getObjectPropertyValues(hasRes, ontology).iterator().next().toStringID());
					//System.out.println("!!!!!!!" + ser.toStringID());
				    //if(ser.getObjectPropertyValues(hasSer, ontology).iterator().next().equals(cs)){
					//ser.getObjectPropertyValues(hasSer, ontology)
					//System.out.println("======" + cs.toStringID());
					service_name = cs.toStringID();
				
					//serviceMap.put("service", value)
					//service_name = ser.getObjectPropertyValues(hasSer, ontology).iterator().next().toStringID().substring(39);
				    System.out.println("provider: " + provider_name + "; service: " + service_name);
					//System.out.println("@@@@@@@" + ser.getObjectPropertyValues(hasSer, ontology).iterator().next().toStringID().substring(39));
				    //}
				}
			}
			//OWLNamedIndividual pro = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "AWS_EC2_Services"));
			//OWLObjectProperty  hasPD = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasProviderDomain"));
			//String pro_name = pro.getObjectPropertyValues(hasPD, ontology).iterator().next().toStringID() ;
			//System.out.println("provider is: " + pro_name);
	// to calculate cost
			
		
			//String ns = "http://www.owl-ontologies.com/alliance#";
			//String ns1 = "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#";
			
			System.out.println("*************PART2 START: to calculate cost for available services");
			
			String bal_s = "balance";
			double URemBal;
			
			OWLNamedIndividual userAPol = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + user_name));
			OWLDataProperty  bal_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + bal_s));
			URemBal = userAPol.getDataPropertyValues(bal_pro, ontology2).iterator().next().parseDouble();
			
			OWLDataProperty  mem_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "mem"));
			//OWLDataProperty  bal_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + bal_s));
			
	    OWLDataProperty  charge_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "charge"));
	    OWLObjectProperty  instance_pro = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasInstanceModel"));
	    //System.out.println(reasoner.getObjectPropertyValues(cs, hasSer).getFlattened().iterator().hasNext());
	    Set<OWLLiteral> inds_charge = reasoner.getObjectPropertyValues(cs, hasEn).iterator().next().getRepresentativeElement().getDataPropertyValues(charge_pro, ontology);
	    //Set<OWLLiteral> inds_charge = reasoner.getObjectPropertyValues(cs, hasSer).iterator().next().getRepresentativeElement().getDataPropertyValues(charge, ontology);
	    double charge = inds_charge.iterator().next().parseDouble();
	    //service1.setCost(inds_charge.iterator().next().getLiteral());
	    cost = charge * Long.parseLong(duration);
	    result = (cost <= URemBal);
	    long offer_no = 1;
	    
	    OWLNamedIndividual policy = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "MappingPolicy"));
	    OWLIndividual owner_ind;
	    //String owner;
	    
	    if (result){
	    	
	    	OWLObjectProperty  hasOwner = manager2.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasOwner"));
		    OWLObjectProperty  hasAdminDo = manager2.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasAdminDomain"));
		    owner_ind = policy.getObjectPropertyValues(hasAdminDo, ontology2).iterator().next().getObjectPropertyValues(hasOwner, ontology2).iterator().next();
		    owner = owner_ind.toStringID().substring(39);
	    	
	    	mem = inds_res.iterator().next().getDataPropertyValues(mem_pro, ontology).iterator().next().parseInteger();
	    	instance = inds_res.iterator().next().getObjectPropertyValues(instance_pro, ontology).iterator().next().toStringID().substring(39);
	    	
	    	//service1.setService_name(service_name);
			service1.setProvider(provider_name);
			service1.setCpuNo(cpu_no);
			service1.setMemory(mem);
			service1.setInstance(instance);
			service1.setCharge(charge);
			service1.setCost(cost);
			
			// create offers for services and update database before returning values to Android
			//serviceMap.put(service_name, service1);
			Offer offer = new Offer();
			
			//contract = new Contract();
			offer.setAppname(app);
			offer.setId(offer_no);
			offer.setUsername(user_name);
			offer.setOwner(owner);
			offer.setGroupname(group);
			offer.setService(service1);
			offer.setStartTime(startT);
			//offer.setDuration(duration);
			
			NegHibConnect.hibOffer(offer);
			
			offers = offers + offer_no + ","+ provider_name + ", Cloud;";
			//offers.put(offer_no, provider + "Grid");
			offer_no++;
	    	System.out.println("charge is: " + charge);
		    System.out.println("total cost is: " + cost);
		    System.out.println("cpu no is: " + cpu_no);
		    System.out.println("mem GiB is: " + mem);
		    System.out.println("instace type is: " + instance);
	    }
	    
	    //System.out.println("charge is: " + inds_charge.iterator().next().getLiteral());
		}
		//return result;
		System.out.println("the reasoning output is: " + result);
		}			
		}*/
		return offers;
	}
	
		
		
		public static String getProvider(){
			return provider_name;
		}
		
		public static String getService(){
			return service_name;
		}
		
		public static double getCost(){
			return cost;
		}
		/*public static boolean costCalculate(long start, long finish){
			//suppose unit for start and finish is second
			boolean result = false;
			
			long duration = (finish - start)/3600;
			
			return result;
			
		}*/
		
		/*public static void main(String[] args) throws OWLOntologyCreationException{
			String[] resources = new String[5];
			String user_name= "Sofia";
			String os_input = "Amazon_Linux";
			String cpu_model = "Intel_Xeon";
			String cpu_speed = "3.3";
			String cpu_no = "2";
			resources[0] = user_name;
			resources[1] = os_input;
			resources[2] = cpu_model;
			resources[3] = cpu_speed;
			resources[4] = cpu_no;
			providerSearch(resources, "ResvPolicy");
			System.out.println("done.");
			//double max_cost = Double.parseDouble("");
		}*/

}
