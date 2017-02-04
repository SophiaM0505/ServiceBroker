package negotiation.Ontology;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import negotiation.Database.NegotiationDB;
import negotiation.HibernateImp.Contract;
import negotiation.HibernateImp.Job;
import negotiation.HibernateImp.NegHibConnect;
import negotiation.HibernateImp.Offer;
import negotiation.HibernateImp.Service;
import negotiation.Negotiation.NegState;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualVisitor;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.util.OWLEntityRemover;

import uk.ac.ucl.chem.ccs.AHECore.API.Rest.AppInstanceResource;
import uk.ac.ucl.chem.ccs.AHECore.API.Rest.RestAPI;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class OntReasoning {
	
	static String grpInput;
	static String username;
	static String appInput; 
	static String startT;
	static long duration;
	// the required app's required amount of resources
	static int cpuNo;
	static long cpuTime;
	static long totalCpuT;
	static int requiredCpuN = 0;
	static int requiredTotalCpuN = 0;
	static long totalClusterT = 0;
	static int level;
	
	static String owner;
	static String provider;
	static String workerN;
	
	static OWLIndividual share_ind;
	static OWLIndividual owner_ind;
	
	static String app_os;
	static String app_cpu_model;
	static String app_cpu_speed;
	
	final static int TRANCATION = 39;
	final static String FIXED = "fixed";
	final static String DYNAMIC = "dynamic";
	final static String SJ = "SingleJob";
	final static String WJ = "WorkflowJob";
	final static String IJ = "InteractiveJob";
	
	static Map<String, String[]> appResource = new HashMap<String, String[]>();
	//static Map<Integer, String> appOffer = new HashMap<Integer, String>();
	static Map<Integer, String> tempOffers = new HashMap<Integer, String>();
	static Map<Integer, String[]> tempOfferMeta = new HashMap<Integer, String[]>();
	//static Map<Integer, Integer[]> OfferCom = new HashMap<Integer, Integer[]>();
	//static Map<Integer, String> OfferCom = new HashMap<Integer, String>();
	static Map<Integer, Integer[]> offerCom = new HashMap<Integer, Integer[]>();

	static double cost = 0;
	static double charge = 0;
	static String[] not_app = new String[10];
	
	//static long offer_no;
	static Integer temp_offer_no = 10;
	static long index = 0;
	//HashMap<Long, String> offers = new HashMap<Long, String>();
	//static String offers = "";
	//static String temp_offers="";
	static String global_offers;
	static double userMaxCost_value;
	//static double site_balance_value;
	static double user_balance_value;
	//static long max_duration;
	static long user_cpuT_value;
	static long max_duration;
	static long maxTotalCpuT;
	/*public static void main(String[] args) throws OWLOntologyCreationException {
		boolean result = reason("UserA", "ManGroup", "visLib");
		System.out.println(result);
	}*/
	
	final static String ns = "http://www.owl-ontologies.com/alliance#";

	final static String mfile_path = "file:/opt/AHE3/ontology/";
	//final static String mfile_path = "file:/Users/zeqianmeng/Desktop/ontology/";
	//final static Stirng 
	
	public static boolean accessCheck(String name, String grp, String app, String shareN, int core, long dur_local) throws OWLOntologyCreationException{
		//String ns = "http://www.owl-ontologies.com/alliance#";
		//String ns1 = "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#";
		//String file = "file:/opt/AHE3/ontology/share.owl";
		//String path = "file:/opt/AHE3/ontology/";
		//String path = "file:/Users/zeqianmeng/Desktop/ontology/";
		String file = mfile_path + shareN + ".owl";
		//System.out.print("Reading SHARE file " + file + "...");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(file));
		//OWLDataFactory factory = manager.getOWLDataFactory();
		
		//OWLOntologyManager pManager = OWLManager.createOWLOntologyManager();
		//OWLOntology pOntology = manager.loadOntology(IRI.create(policyF));		
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		//System.out.println("part1 done.");
		
		//reasoner.getKB().realize();
		//reasoner.getKB().printClassTree();
		
		//String sysInput ="Scientific_Linux_303";
		//String gridIn = "CY01-LCG2";
		/*String grpInput = "ManGroup";
		String username = "UserA";
		String appInput = "visLib";*/
		grpInput = grp;
		username = name;
		appInput = app;
		//System.out.println("inputs: " + grpInput + username + appInput);
		//String rwall = "requestedTotalWallTime";
		String rcpuNo = "physicalCpus";
		String rcpuTime;
		//String rcpuTime = "IndividualCpuTime";
		String group = "hasUserDomain";
		
		duration = 0;
		totalCpuT = 0;
		//String share = "Share";
		//String hasApp = "hasApp";
		
		boolean finalRes = false;
		boolean search_cluster = false;
		boolean cluster_result = false;
		boolean access_result = false;
	
		//OWLNamedIndividual node = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + gridIn));
		OWLObjectProperty  hasApp = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasApp"));
		
		//OWLObjectProperty  hasEx = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + ee));
		
		OWLObjectProperty  belGroup = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + group));
		//OWLDataProperty  hasRwall = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + rwall));
		OWLDataProperty  hasRcpuN = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + rcpuNo));
		//OWLDataProperty  hasRcpuT = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + rcpuTime));
		
		OWLDataProperty  balPro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
		
		////OWLObjectProperty  hasShare = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasShare"));
		//System.out.println("*************START1");
		////OWLDataProperty  mem_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "mainMemorySize"));
		//System.out.println("*************START2");
		OWLClass insC = manager.getOWLDataFactory().getOWLClass(IRI.create(ns + "Share"));
		//Share instances
		NodeSet<OWLNamedIndividual> ins = reasoner.getInstances(insC, false);
		
		//OWLClass app = manager.getOWLDataFactory().getOWLClass(IRI.create(ns + appInput));
		
		// for each Share instance
		/*System.out.println("**********Reasoning: given an operating system name, it searches for a Share owned by \n" +
				"a group named ManGroup, then finds out if this Share contains work nodes with the required os");
				*/
		//System.out.println("**************PART1 START: check if group's Share contains the required app and required resource" +
		//		"amount of the app.");
		//int[] output = new int[10];
		
		//aMap to store app's required resources
		LinkedHashMap<String, Long> appRMap = new LinkedHashMap<String, Long>();
		//int memNo = 0;
		//int cpuTime = 0;
		boolean res1 = true;
		boolean resv = false;
		//int[] mapA = new int[10];
		
		//to get job type from share, as different types of jobs would have different payment methods, corresponding 
		//with different properties in ontology
		OWLNamedIndividual share_indiv = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + shareN));
		OWLObjectProperty  hasAppl = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasAppEnvironment"));
		OWLObjectProperty  hasJobt = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasJobType"));
		String job_whole = share_indiv.getObjectPropertyValues(hasAppl, ontology).iterator().next().
				getObjectPropertyValues(hasJobt, ontology).iterator().next().getTypes(ontology).iterator().next().toString();
		int job_len = job_whole.length();
		String jobs = job_whole.substring(TRANCATION+1, job_len-1);

		//System.out.println("********job type returned: " + jobs);
		if(jobs.equalsIgnoreCase(IJ)){
		    rcpuTime = "minCpuTime";
		}
		else{
			rcpuTime = "individualCpuTime"; 
		}
		//rcpuTime = "IndividualCpuTime";
		requiredCpuN = 0;
		//from here, it starts to match fixed required amount of resources and available resources
		for(Node<OWLNamedIndividual> sameInd : ins) {
			
			OWLNamedIndividual shIns = sameInd.getRepresentativeElement();
			//System.out.println("00000000000 " + shIns.toStringID());   
		    Set<org.semanticweb.owlapi.model.OWLIndividual> shares = shIns.getObjectPropertyValues(belGroup, ontology);
		
		    if(shares.isEmpty()){
				//System.out.println("No Share found.");
		        res1 = false;
		    	
						}
		    // the Share containing ManGroup is RealityGrid 
		    // the Share instance is RealityGrid
		    else if(shares.iterator().next().toStringID().contains(grpInput)){
		    	//share_ind = shares.iterator().next();
		    	share_ind = shIns;
		    	res1 = true;
		    	System.out.println("This is a Share owned by ManGroup: " + share_ind.toStringID());
		    	//shIns: RealityGrid
		    	//System.out.println("SubClass of this Share " + shIns.getObjectPropertyValues(ontology));
		    	//Set<org.semanticweb.owlapi.model.OWLIndividual> serSet = shIns.getObjectPropertyValues(hasser, ontology);
		    	OWLClass appCla = manager.getOWLDataFactory().getOWLClass(IRI.create(ns + "ApplicationEnvironment"));
				NodeSet<OWLNamedIndividual> appSet = reasoner.getInstances(appCla, false);
				//try to get individuals in this Share
				//System.out.println(ins.containsEntity((OWLNamedIndividual) enCla));
				//System.out.println("2222222222222 " + reasoner.getSubObjectProperties(hasgrp, false));
			    // check through all the execution environments, not all ees have a os property
				for(Node<OWLNamedIndividual> appInd : appSet) {
					// check through each execution environment
				    OWLNamedIndividual appInds = appInd.getRepresentativeElement();
				    
				   /* int job_l = appInd.getRepresentativeElement().getTypes(ontology).iterator().next().toString().length();
				    //System.out.println("2******" + job_l);
				    System.out.println("1*******" + appInd.getRepresentativeElement().getTypes(ontology).iterator().next().toString().substring(40, job_l-1));

				    String job = appInd.getRepresentativeElement().getTypes(ontology).iterator().next().toString().substring(40, job_l-1);
				    //OWLObjectProperty  hasJobType = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasJobType"));
			    	//String job = appInds.getIndividualsInSignature().iterator().next().getObjectPropertyValues(hasJobType, ontology).iterator().next().toStringID().substring(TRANCATION);
			    	if (job.equalsIgnoreCase(IJ)){
			    		rcpuTime = "MinCpuTime";
			    		System.out.println("2******" + rcpuTime);
			    	}
			    	else {
			    		rcpuTime = "IndividualCpuTime";
			    		System.out.println("3******" + rcpuTime);
			    	}*/
				    
				    //System.out.println("1*******" + appInds.getIndividualsInSignature().iterator().next().toStringID());
				   
				    //System.out.println("HELLO " + appInds.toStringID());
				    //System.out.println("MENG " + appInds.getObjectPropertyValues(hasApp, ontology));
				    //System.out.println("MENG " + shIns.getObjectPropertyValues(hasser, ontology).iterator().next());
				   // get contents included in an ee instance

				    	//System.out.println("@@@@@@@@@@@@ " + shIns.getObjectPropertyValues(hasser, ontology).iterator().next().getObjectPropertyValues(hasEx, ontology));
				    	//if(shIns.getObjectPropertyValues(hasser, ontology).iterator().next().getObjectPropertyValues(hasEx, ontology).contains(osInds)){
				    	//for(org.semanticweb.owlapi.model.OWLIndividual serIns : serSet){
				    
				    //get required app's required resources
				    //System.out.println("**********" + appInds.getIndividualsInSignature().iterator().next().toStringID());
				    Set<OWLNamedIndividual> appIndSet = appInds.getIndividualsInSignature();
				    //System.out.println("=======" + appInds.getObjectPropertyValues(hasApp, ontology).iterator().next().getDataPropertyValues(hasRmem, ontology).iterator().next().getLiteral());
				    //if(appIndSet.iterator().next().toStringID().contains(appInput)){
				    if(appIndSet.iterator().next().toStringID().contains(appInput)){
				    	
				    	//*******to add code to change between CPUTime and MinCPUTime
				    	/*OWLObjectProperty  hasJobType = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasJobType"));
				    	System.out.println("2*******" + appIndSet.iterator().next().toStringID());
				    	String job = appIndSet.iterator().next().getObjectPropertyValues(hasJobType, ontology).iterator().next().toStringID().substring(TRANCATION);
				    	if (job.equalsIgnoreCase(SJ)){
				    		rcpuTime = "IndividualCpuTime";
				    	}
				    	else{
				    		rcpuTime = "MinCpuTime";
				    	}*/
				    	
				    	OWLDataProperty  hasRcpuT = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + rcpuTime));
				    	search_cluster = true;
				    	//System.out.println("============" + appIndSet.iterator().next().toStringID());
				    	Set<OWLIndividual> appLibSet = appIndSet.iterator().next().getObjectPropertyValues(hasApp, ontology);
				    	//System.out.println("++++++++++" + appIndSet.iterator().next().getObjectPropertyValues(hasApp, ontology));
				    	for(OWLIndividual appLibInd : appLibSet){
				    	//appIndSet.iterator().next().getobj
				    //if(appInds.getObjectPropertyValues(hasApp, ontology).toString().contains(appInput)){
				    		String app_name = appLibInd.toStringID().substring(TRANCATION);
				    		//*****System.out.println("************ required app's resource: " + app_name);
				    		if(app_name.equalsIgnoreCase("simLib") && core != 0){
				    			//your case would go here
				    			cpuNo = core;
				    		}
				    		else{
				    			cpuNo = Integer.parseInt(appLibInd.getDataPropertyValues(hasRcpuN, ontology).iterator().next().getLiteral());
				    			System.out.println("========= cpu no in share: " + cpuNo);
				    		}
				    			//memNo = Integer.parseInt(appInds.getObjectPropertyValues(hasApp, ontology).iterator().next().getDataPropertyValues(hasRmem, ontology).iterator().next().getLiteral());
				    		//*****System.out.println("***********cpu time property: " + hasRcpuT);
				    		if(app_name.equalsIgnoreCase("resvApp") && dur_local != 0)
				    		{
				    			cpuTime = dur_local;
				    			duration = dur_local;
				    			System.out.println("--------- duration is: " + dur_local);
				    			resv = true;
				    		}
				    		else{
				    			//for steering case, the cpu time is the min set by manager
				    			if(core != 0){
				    				cpuNo = core;
				    			}
				    			cpuTime = (long) Double.parseDouble(appLibInd.getDataPropertyValues(hasRcpuT, ontology).iterator().next().getLiteral());
				    			//duration is minCpuTime
				    			duration = cpuTime + duration;
				    		}
				    			//cpuTime = Integer.parseInt(appInds.getObjectPropertyValues(hasApp, ontology).iterator().next().getDataPropertyValues(hasRcpu, ontology).iterator().next().getLiteral());
				    		    //if(cpuNo > cpu_max){
				    		    //cpu_max = cpuNo;
				    		    //}
				    		    String[] resources = new String[8];
				    		    resources[0] = Integer.toString(cpuNo);
				    		    resources[1] = Long.toString(cpuTime);
				    		    appResource.put(app_name, resources);
				    		  //*****System.out.println(cpuNo + "===" + cpuTime);
				    		    totalCpuT = (cpuNo * cpuTime) + totalCpuT;
				    		    System.out.println("totalCpuT type: " + totalCpuT);
				    		    //to get the max cpu number, as it assumes the wf is sequential, wn should have no less cpu no.
				    		    if (cpuNo > requiredCpuN){
				    		    requiredCpuN = cpuNo;
				    		    }
				    		  //*****System.out.println("app required total cpu time: " + totalCpuT);
				    		    //int wallTime = Integer.parseInt(appInds.getObjectPropertyValues(hasApp, ontology).iterator().next().getDataPropertyValues(hasRwall, ontology).iterator().next().getLiteral());
				    		    //System.out.println("Walltime: " + wallTime);
				    		    
				    		    //output[0] = memNo;
				    		    appRMap.put("cpuNo", (long)cpuNo);
				    		    //output[1] = cpuTime;
				    		    appRMap.put("cpuTime",cpuTime);
				    		    }				    	
				    		    finalRes = true;
				    		    //return finalRes;
				    		    //output[2] = wallTime;
				    		  //*****System.out.println("The required app is available, and it reuqires following resources to run in Grid/Cloud: ");
				    		  //*****System.out.println("total CPU time: " + totalCpuT);
				    		  //*****System.out.println("required cpu number: " + requiredCpuN);
				    		    //System.out.println("CPU time: " + cpuTime);
				    	
				    	}
				    		else{
				    			finalRes = false;
				    		}
				    	//System.out.println("************: " + osInds.toStringID());
				    		finalRes = finalRes && res1;
				    }
				//duration = totalCpuT;
				//System.out.println("hhhhhhh " + finalRes);
				    }
		    /*else{
		    	System.out.println("Sorry. No group found.");
		    }*/
             finalRes = finalRes && res1;
				}
	/*	if(search_cluster){
			totalClusterT = 5;
		}*/
		//System.out.println(finalRes);
		//System.out.println("*************PART1 DONE");  
		//System.out.println("*************PART2 START: to check if user has enough resources to run the program");
		
		//String mfile = "file:/opt/AHE3/ontology/mPolicy.owl";
		//String mfile = "file:/Users/zeqianmeng/Desktop/ontology/mPolicy.owl";
		//System.out.println("Reading MAPPING file " + mfile + "...");
		OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		//String policy_file = path + RestAPI.share_policy.get(shareN) + ".owl";
		//String policy_path = "file:/Users/zeqianmeng/Desktop/ontology/";
		
		//for web server test
		//String policy_name = RestAPI.share_policy.get(shareN);
		//String policy_file = mfile_path + policy_name + ".owl";
		//String policy_file = "file:/Users/meng/Desktop/ontology/"+ shareN.s + "ResvPolicy.owl";
		
		//for ontology reasoning performance evaluation
		int end_index = shareN.length() - 5;
		String share_pre = shareN.substring(0, end_index);
		//String policy_file = "file:/opt/AHE3/ontology/" + share_pre + "Policy.owl";
		String policy_file = mfile_path + share_pre + "Policy.owl";
		//System.out.println("*******share file path: " + policy_file);
		//String policy_file = "file:/Users/zeqianmeng/Desktop/ontology/ResvPolicy.owl";
		//System.out.println("******* policy file path: " + policy_file);
		OWLOntology ontology2 = manager2.loadOntology(IRI.create(policy_file));
		
		PelletReasonerFactory.getInstance().createReasoner( ontology2 );
		//System.out.println("part2 done.");
		
		//reasoner2.getKB().realize();
		//reasoner2.getKB().printClassTree();

		//OWLNamedIndividual mgroup = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + grpInput));
		//System.out.println("6666666666 " + mgroup.getAnnotations(ontology));
		
		OWLDataProperty  hascpuT = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "cpuTime"));
		//OWLDataProperty  hasmem = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "VirtualMemorySize"));
		
		//int j = 0;
		boolean result1 = false;
		//Set<OWLAnnotation> users = mgroup.getAnnotations(ontology2);
		result1 = ontology2.containsIndividualInSignature(IRI.create(ns + grpInput));
		//System.out.println("1111111 " + users.contains(username));
		/*for(OWLAnnotation user :users){
			//System.out.println("55555555 " + user.getValue().toString().contains(username));
			result1 = user.getValue().toString().contains(username) || result1;
			
		}*/
		
		//an array inputs to get values of user's available resources
		double avaCpuT;
		//int[] inputs = new int[2];
		//Object[] appRArr = appRMap.values().toArray();
		//System.out.println("HERE " + appRMap.values().toArray().length);
		OWLNamedIndividual userA = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + username));
		if(result1){
			//System.out.println("User membership VARIFIED");
		    
		//System.out.println("7777777777 " + userA.getAnnotations(ontology).size());
		    //System.out.println("========= " + userA.getDataPropertyValues(hasmem, ontology2));
			
			//uses's available resources
		    //inputs[0] = userA.getDataPropertyValues(hasmem, ontology2).iterator().next().parseInteger();
		    //inputs[0] = userA.getDataPropertyValues(hascpu, ontology2).iterator().next().parseInteger();
            avaCpuT = userA.getDataPropertyValues(hascpuT, ontology2).iterator().next().parseDouble();
            user_balance_value = userA.getDataPropertyValues(balPro, ontology2).iterator().next().parseDouble();
            user_cpuT_value = (long) userA.getDataPropertyValues(hascpuT, ontology2).iterator().next().parseDouble();
            //System.out.println("HERE: " + user_balance_value);
          //*****System.out.println("HERE: " + avaCpu);
		    boolean grid_result = false;
		    boolean cloud_result = false;
		    
		    /*if(resv){
		    	OWLDataProperty  maxCost = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "maxCost"));
			    double maxCost_value = userA.getDataPropertyValues(maxCost, ontology2).iterator().next().parseInteger();
			    System.out.println("limitation to user's max cpu time:" + maxCost_value);
			    access_result = maxCost_value >= totalCpuT && avaCpu >= totalCpuT;
		    }
		    else{
		    	access_result = avaCpu >= totalCpuT;
		    }*/
		    
            //Set<OWLAnnotation> ans = userA.getAnnotations(ontology2);
            //int i = 0;
/*		    if(resv){
		    	OWLDataProperty  maxCost = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "maxCost"));
			    maxCost_value = userA.getDataPropertyValues(maxCost, ontology2).iterator().next().parseDouble();
			    System.out.println("max cost value: " + maxCost_value);
		    }*/
		    
		    OWLDataProperty  userMaxCpu = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "maxTotalCpuTime"));
	        maxTotalCpuT = (long) userA.getDataPropertyValues(userMaxCpu, ontology2).iterator().next().parseDouble();
		    
		    // to distinguish its cloud case or cluster case
		    if(!appInput.equalsIgnoreCase("CompSteering")){
		    	// for Junyi's test case
			    OWLDataProperty  userMaxCost = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "maxCost"));
			    userMaxCost_value = userA.getDataPropertyValues(userMaxCost, ontology2).iterator().next().parseDouble();
		    }
		    //max_duration = max_duration;
		    //OWLDataProperty  userMaxCost = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
		    //*****System.out.println("max cost value: " + maxCost_value);
            //System.out.println(avaCpu >= appRMap.get("cpuTime") * appRMap.get("cpuNo"));
            grid_result = avaCpuT >= totalCpuT;
            cluster_result = avaCpuT >= totalClusterT;
            //access_result = grid_result;
            access_result = grid_result || cluster_result || cloud_result;
            //System.out.println("HERE: " + grid_result);
            //move values in aMap to array mapA for loops
            //this requires mapA have the same sequence as inputs
            //mapA[0] = appRMap.get("mem");
            //mapA[1] = appRMap.get("cpu");
            		
		    /*for(int an :inputs){
			
			//System.out.println(an.getSignature() + "has num" + an.getValue().toString().contains("55"));
			    //System.out.print(an.getValue().toString().substring(1, 3) + "**");
			    //System.out.print(i + "**");
			    //System.out.print(output[i] + "**");
			    //System.out.print(Integer.parseInt(an.getValue().toString().substring(1, 3))>output[i]);
			    //System.out.println("** " + (Integer.parseInt(an.getValue().toString().substring(1, 3))>output[i] && result));
			    
		    	//first value in this array is cpu no
		    	//first value of inputs is mem no
		    	//System.out.println("LOOK HERE " + Integer.parseInt(appRArr[i].toString()));
		    	//System.out.println(inputs[i]);
		    	result = an>= Integer.parseInt(appRArr[i].toString()) && result;
			    i++;
		    }
		*/
		    if(access_result){
			    //System.out.println("Requirements CAN be satisfied.");
			    OWLObjectProperty  hasOwner = manager2.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasOwner"));
			    //OWLObjectProperty  hasAdminDo = manager2.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasUserDomain"));
			    OWLObjectProperty  hasAdminDo = manager2.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasAdminDomain"));
			    OWLDataProperty  hasLevel = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "memberLevel"));
			    level = userA.getDataPropertyValues(hasLevel, ontology2).iterator().next().parseInteger();
			    //System.out.println("*********5" + share_ind.getSignature().toString());
			    //System.out.println("*********6" + share_ind.getObjectPropertyValues(hasAdminDo, ontology2).iterator().next().toStringID());
			    owner_ind = share_ind.getObjectPropertyValues(hasAdminDo, ontology2).iterator().next().getObjectPropertyValues(hasOwner, ontology2).iterator().next();
			    owner = owner_ind.toStringID().substring(TRANCATION);
			  //*****System.out.println("======== owner: " + owner_ind.toStringID().substring(39));
			  //*****System.out.println("======== member level: " + level);
			    //System.out.println("========" + share_ind.toStringID());
			    //System.out.println("========" + share_ind.getObjectPropertyValues(hasAdminDo, ontology2).iterator().next().getObjectPropertyValues(hasOwner, ontology2).iterator().next());
			    finalRes = true;
		    }
		    
		    //else reason about cloud service
		    else{
			    //System.out.println("Requirements CANNOT be satisfied.");
			    finalRes = false;
			    return finalRes;
		    }
		    }
		else{
			//System.out.println("User NOT FOUND.");
			finalRes = false;
			return finalRes;
		}
		//System.out.println(finalRes);
		//System.out.println("*************PART2 DONE");
		//*****System.out.println("******** finalRes" + finalRes);
	    return finalRes;	
	}
	
	//static int memNo;
	//static int cpuTime;
	
	// return offer id and work node name
	//public static HashMap<Long, String> resourceSearch(String name, String grp, String app, String startT_in, String duration_in) throws OWLOntologyCreationException{
    //con_id is for renegotiation
	public static String resourceSearch(String name, String grp, String app, String shareN, String pay_method, int core, long con_id) throws OWLOntologyCreationException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
	//public static void resourceSearch(String name, String grp, String app, String startT_in, String duration_in) throws OWLOntologyCreationException{
		//System.out.println("LOOK HERE: " + share_ind);
		//String provider;
    	String offers = "";
    	String temp_offers="";
		String ns = "http://www.owl-ontologies.com/alliance#";
		//String ns1 = "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#";
		//String file = "file:/opt/AHE3/ontology/share.owl";
		//String path = "file:/opt/AHE3/ontology/";
		//String path = "file:/Users/zeqianmeng/Desktop/ontology/";
		String file = mfile_path + shareN + ".owl";
		//String file = "file:/Users/zeqianmeng/Desktop/ontology/share.owl";
		//System.out.println("Reading SHARE file " + file + "...");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(file));
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		
	    //System.out.println("*************PART3 START: to search for satisdied execution environments");
		
		String ecpuNo = "physicalCpus";
		// this cpu time is for exe env check
		String ecpuT = "cpuTime";
		//startT = startT_in;
		//duration = duration_in;
		
		//int memNo = 0;
		//int cpuTime = 0;
		
		boolean finalRes = false;
		
		boolean sepCluster = false;
		boolean sepCloud = false;
		boolean cloud_offer = false;
		boolean resource_search = false;
		
		OWLDataProperty  RcpuNo = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + ecpuNo));
		OWLDataProperty  RcpuT = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + ecpuT));
		OWLDataProperty  balancePro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "balance"));
		OWLDataProperty  chargePro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "charge"));
		//OWLDataProperty  minCpuTPro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "minCpuTime"));
		//OWLDataProperty  mem_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "mainMemorySize"));
		OWLDataProperty  mem_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "virtualMemorySize"));
		
		OWLDataProperty  pay_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "paymentMethod"));
		
		OWLNamedIndividual share = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + shareN));
		//OWLNamedIndividual share = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "UnicoreShare"));
		//share.getDataPropertyValues(arg0, arg1)
		
		OWLClass enCla = manager.getOWLDataFactory().getOWLClass(IRI.create(ns + "ExecutionEnvironment"));
		NodeSet<OWLNamedIndividual> enSet = reasoner.getInstances(enCla, false);
		
		OWLClass serCla = manager.getOWLDataFactory().getOWLClass(IRI.create(ns + "Service"));
		NodeSet<OWLNamedIndividual> serSet = reasoner.getInstances(serCla, false);
		
		//String test = "RealityGrid2";
		//OWLNamedIndividual test_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + test));
		OWLObjectProperty  hasProDomain = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasProviderDomain"));
		//System.out.println("LOOK HERE: " + share.getObjectPropertyValues(hasProDomain, ontology).size());
		provider = share.getObjectPropertyValues(hasProDomain, ontology).iterator().next().toStringID().substring(TRANCATION);
		//System.out.println("*************provider: " + provider);
		
		OWLObjectProperty  hasEnv = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasExeEnvironment"));
		OWLDataProperty  meas_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "measurement"));
		OWLDataProperty  url_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "url"));
		OWLObjectProperty  hasEnd = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasEndpoint"));
		//available resource variables
		int aCpuNo = 0;
		long aCpuT = 0;
		//*****int aMem = 0;
		//double user_balance = 0;
		double site_balance = 0;
		//boolean res2 = false;
		
		//String[] pros;
		//pros = new String[100];
		//int pros_no = 0;
	/*	long offer_no = 1;
		long temp_offer_no = 10;
		long index = 0;
		//HashMap<Long, String> offers = new HashMap<Long, String>();
		String offers = "";
		String temp_offers="";*/
		
		// to generate a random offer id
		/*Random rand = new Random();
	    offer_no = rand.nextInt(10000 + 1);*/
		double old_cost = 0;
		long old_duration = 0;
		double old_charge = 0;
		String contracted_node = null;
		int used_cpuNo = 0;
		long job_id = 0;
		String temp_offers_comb = "";
		//long neg_id = 0;
		Random rand1 = new Random();
   	    long neg_id = rand1.nextInt(10000 + 1);
   	    //String measurement;
   	    
   	    boolean reneg = (con_id != 0);
   	    long used_cpuT = 0;
   	    String endpoint;
   	     //Job job = new Job();
		//to be done
		//int requiredCpuNo;
		//double user_max_cost;
		//double user_balance;
		// this is so far for cloud only
		if(reneg){
			OWLObjectProperty  hasOwner = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasOwner"));
		    //OWLObjectProperty  hasAdminDo = manager2.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasUserDomain"));
		    OWLObjectProperty  hasAdminDo = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasAdminDomain"));
		    owner_ind = share.getObjectPropertyValues(hasAdminDo, ontology).iterator().next().getObjectPropertyValues(hasOwner, ontology).iterator().next();
		    owner = owner_ind.toStringID().substring(TRANCATION);
			
	   	    //contracts = Long.toString(con_id);
	   	    //NegotiationDB.insertJob(job_id, con_id);
			//if con_id included, indicate that this is re-negotiation
			String[] reneg_info = NegotiationDB.getRenegInfo(con_id);
			old_charge = Double.parseDouble(reneg_info[0]);
			//charge = old_charge;
			String start_time = reneg_info[1];
			String measurement = reneg_info[2];
			contracted_node = reneg_info[3];
			//contracted_cpuNo = Integer.parseInt(reneg_info[4]);
			userMaxCost_value = Double.parseDouble(reneg_info[4]);
			used_cpuNo = Integer.parseInt(reneg_info[5]); //used cpu number (previously required cpu number) in this contract
			user_balance_value = Double.parseDouble(reneg_info[6]);
		    max_duration = Long.parseLong(reneg_info[7]);
			totalCpuT = Long.parseLong(reneg_info[8]);
			job_id = Long.parseLong(reneg_info[9]);
			maxTotalCpuT = Long.parseLong(reneg_info[10]);
			
			
	   	    //job.setId(job_id);
	   	    //job.setContractId(con_id);
	   	    //job.setOldNode(contracted_node);
					
			System.out.println("within OntReasoning for renegotiaton, fetched info:" + old_charge + "; " +
			start_time + "; " + measurement + "; " + contracted_node + "; " + used_cpuNo);
			
			Calendar cal1 = Calendar.getInstance();
		        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");

		        Calendar cal2 = Calendar.getInstance();

  	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
         	String current_time = sdf1.format(cal1.getTime()) + "T" + sdf.format(cal2.getTime()) + "+7:00";
         	
         	//to calculate if the charge so far reaches maxCost       	
         	long duration_h = Long.parseLong(current_time.substring(11, 13)) - Long.parseLong(start_time.substring(11, 13));
    		//to calculate duration as hours
    		//if start_mm >= end_mm, then hh = duration_h
    		//if start_mm < end_mm, then hh = duration_h + 1
         	if(measurement.equalsIgnoreCase("hour")){	    
    		//long min_diff = Long.parseLong(end_time.substring(15, 16)) - Long.parseLong(stime.substring(15, 16));
    		//System.out.println("******** start time hh: " + stime.substring(11, 13));
    		//System.out.println("******** end time hh: " + end_time.substring(11, 13));
    		//System.out.println("******** calculated duration_h: " + duration_h);
    		System.out.println("within renegotiation in OntReasoning, inside measurement is hour " + duration_h);
    		    if (duration_h == 0){
    			    old_duration = 1;
    		    }
    		//System.out.println("******** start time mm: " + stime.substring(14, 16));
    		//System.out.println("******** end time mm: " + end_time.substring(14, 16));
    		
    		//System.out.println("******** start time ss: " + stime.substring(17, 19));
    		//System.out.println("******** end time ss: " + end_time.substring(17, 19));
    		
    		    if (duration_h > 0 && Long.parseLong(current_time.substring(14, 16) + current_time.substring(17, 19)) 
    				    <= Long.parseLong(start_time.substring(14, 16) + start_time.substring(17, 19))) {
    			    old_duration  = duration_h;
    		    }
    		
    		    else{
    			    old_duration = duration_h + 1;
    		    }
    		
    		    old_cost = old_duration * old_charge;
    		    
    		    System.out.println("within renegotiation, user already cost:" + old_cost);
    		    //for reneg, the charge would be 0, but you would check this for worker nodes later
    		    if(old_cost < userMaxCost_value){
    		    	//site_balance will be only used when its the same site
    			    //site_balance = site_balance - old_cost;
    			    userMaxCost_value = userMaxCost_value - old_cost;
    			    user_balance_value = user_balance_value - old_cost;
    			    if(site_balance < 0){
    			    	site_balance = 0;
    			    }
    			    if(userMaxCost_value < 0){
    			    	userMaxCost_value = 0;
    			    }
    			    if(user_balance_value < 0){
    			    	user_balance_value = 0;
    			    }
    			    //requiredCpuN = core;
    			    System.out.println("renegotiation, new user max cost: " + userMaxCost_value);
    			    System.out.println("renegotiation, user new balance: " + user_balance_value);
    			    //System.out.println("****************renegotiation:" + contracted_node + "***" + node);
    		    }
         	}
         	else if(measurement.equalsIgnoreCase("second")){
         		long current_m = Long.parseLong(current_time.substring(14, 16));
         		long current_s = Long.parseLong(current_time.substring(17, 19));
         		long start_m = Long.parseLong(start_time.substring(14, 16));
         		long start_s = Long.parseLong(start_time.substring(17, 19));
         		
         		long duration_m = current_m - start_m;
         		long duration_s = current_s - start_s;
         		
         		old_duration = duration_h * 3600 + duration_m * 60 + duration_s;
         		
         		used_cpuT = old_duration * used_cpuNo;
	    		if(used_cpuT < maxTotalCpuT && maxTotalCpuT - used_cpuT >= totalCpuT){
	    			//aCpuT = aCpuT - used_cpuT;
	    			maxTotalCpuT = maxTotalCpuT - used_cpuT;
    			    user_cpuT_value = user_cpuT_value - used_cpuT;
    			    /*if(aCpuT < 0){
    			    	aCpuT = 0;
    			    }*/
    			    if(maxTotalCpuT < 0){
    			    	maxTotalCpuT = 0;
    			    }
    			    if(user_cpuT_value < 0){
    			    	user_cpuT_value = 0;
    			    }
    			    //totalCpuT = 0;
    			    //requiredCpuN = core; 
    			    System.out.println("renegotiation, new max cpu time: " + maxTotalCpuT);
    			    System.out.println("renegotiation, new remaining cpu time: " + user_cpuT_value);
    			    System.out.println("renegotiation, existing contracted node: " + contracted_node);
	    		}
         	}
    		/*if(old_cost < userMaxCost_value && userMaxCost_value - old_cost >= charge){
    			site_balance = site_balance - old_cost;
    			userMaxCost_value = userMaxCost_value - old_cost;
    			user_balance_value = user_balance_value - old_cost;
    		}*/
         	//requiredCpuN = core;
		}
	    boolean coallo = false;
	    //boolean coallo_done = false;
	    requiredTotalCpuN = requiredCpuN;
	    boolean mix_offer = false;
	    ArrayList offer_cpu = new ArrayList();
	    ArrayList offer_nos = new ArrayList();
	    ArrayList offer_endpoints = new ArrayList();
	    int offer_count=0;
	    
	    //HashMap<Long, Integer> hashmap = new HashMap<Long, Integer>();
	    
		for(Node<OWLNamedIndividual> enInd : enSet) {
			// check through each (wrong, should be execution environment) Service
		    OWLNamedIndividual exeEn = enInd.getRepresentativeElement();
		    //System.out.println("111111" + exeEn.toStringID());
		    String node = exeEn.toStringID().substring(TRANCATION);
		    System.out.println("222222" + node);
		    
		    Set<OWLLiteral> cpuNoSets = exeEn.getDataPropertyValues(RcpuNo, ontology);
		    Set<OWLLiteral> cpuTSets = exeEn.getDataPropertyValues(RcpuT, ontology);
		    //Set<OWLLiteral> memSets = exeEn.getDataPropertyValues(mem_pro, ontology);
		    Set<OWLLiteral> balSets = exeEn.getDataPropertyValues(balancePro, ontology);
		    Set<OWLLiteral> chargeSets = exeEn.getDataPropertyValues(chargePro, ontology);
		    Set<OWLLiteral> measSets = exeEn.getDataPropertyValues(meas_pro, ontology);
		    //Set<OWLLiteral> minCpuTSets = exeEn.getDataPropertyValues(minCpuTPro, ontology);
		    //System.out.println("cpu has next?" + cpuSets.iterator().hasNext());
		    //System.out.println("mem has next?" + memSets.iterator().hasNext());
		    //cpuNo = 1;
	    	//totalCpuT = 1;
		    
	    	
	    	// To check for GRID case
		  //*****System.out.println("%%%%%%%%%%%% " + cpuTSets.iterator().hasNext() + cpuNoSets.iterator().hasNext());
		    if(cpuTSets.iterator().hasNext() && cpuNoSets.iterator().hasNext()){
		    	aCpuNo = Integer.parseInt(cpuNoSets.iterator().next().getLiteral());
		    	//aCpuT = Integer.parseInt(cpuTSets.iterator().next().getLiteral());
		    	aCpuT = (long) Double.parseDouble(cpuTSets.iterator().next().getLiteral());
		    	boolean checked = pay_method.contentEquals("asap");
		    	boolean dynamic = false;
		    	//if user's request includes 'as soon as possible', to check is this exe env can meet asap demand
		    	if(checked){
		    	    //boolean pay_res = Boolean.parseBoolean(exeEn.getDataPropertyValues(pay_pro, ontology).iterator().next().getLiteral());
		    		for(Node<OWLNamedIndividual> serInd : serSet) {
		    			OWLNamedIndividual service = serInd.getRepresentativeElement();
		    			// to get the service which provides this exe env
		    			if(service.hasObjectPropertyValue(hasEnv, exeEn, ontology)){
		    			    String pay_method_value =service.getDataPropertyValues(pay_pro, ontology).iterator().next().getLiteral();
			    	        System.out.println("*************" + pay_method_value);
			    	    //if(!pay_res){
			    	        //if the service is provisioned in a dynamic manner
			    	        dynamic = pay_method_value.equalsIgnoreCase("dynamic");
			    	        if(!dynamic){
			    	    	    System.out.println("*************");
			    	    	    // if user requires 'asap', but available service is not'dynamic', then
			    	    	    //search for next service/exe env
			    		        break;
			    	        }
		    			}
		    		}		    		
		    		//continue;
		    	}
		    	if(checked && !dynamic){
		    		continue;
		    	}
		    	//System.out.println("aCpuNo: " + aCpuNo);
		    	//System.out.println("aCpuT: " + aCpuT);
		    	//System.out.println("requiredCpuNo: " + requiredCpuN);
		    	//System.out.println("totalCpuT: " + totalCpuT);
		    	//to compare with required  number (memNo) and available number (aMemNo)
		    	if (reneg){
		    		System.out.println("renegotiaton, " + old_duration + "; " + max_duration + "; " + userMaxCost_value);
		    		
		    		//should compare old_duration < user_maxCost_value/charge
		    		//'duration' here is the minCpuTime defined for app
		    		//remaining duration should be larger than min_duration
		    		//if(old_duration * used_cpuNo < max_duration && max_duration - old_duration >= duration){
	    			    
		    		    aCpuT = aCpuT - used_cpuT;
		    		    if(aCpuT < 0){
	    			    	aCpuT = 0;
	    			    }
	    			    //core for required core number from requester, requireedCpuN for global val hoding core value
	    			    if (contracted_node.equalsIgnoreCase(node)){
	    			    	requiredCpuN = core;
	    			    	System.out.println("1 renegotiation, new required cpu number for existing collaborated node: " + requiredCpuN);
	    			    }
	    			    else{
	    			    	requiredCpuN = core - used_cpuNo;
	    			    	//max_duration = maxTotalCpuT/core;
	    			    	System.out.println("2 renegotiation, new required cpu number for new node: " + requiredCpuN);
	    			    }
	    		}
	    		    /*else{
	    		    	System.out.println("your balance is not sufficent to increase cpu number for grid resources.");
	    		    	break;*/
	    		   // }
		    	//}
		    	
		    	//totalCpuT: total required cpu time for this job; cCpu: available cpu time of this exe env
		    	//aCpuT: exe env's (share) remaining cpu time
		    	//max_duration: user's maxTotalCpuTime for a job (policy). totalCpuT <= max_duration
		    	//user_cpuT_value: user's remaining cpu time (policy). totalCpuT <= user_cpuT_value
		    	if(totalCpuT<=aCpuT && totalCpuT <= maxTotalCpuT && totalCpuT <= user_cpuT_value)
		    	//if(requiredCpuN<=aCpuNo && totalCpuT<=aCpuT)
		    	{
		    	//provider = exeEn.toStringID();
		    	
		    	// get all provider domains
		    	   //System.out.println("Required cpu no: " + cpuNo + "; cpu time: " + cpuTime);
		    		//*****System.out.println("Group's available cpu no: " + aCpuNo + "; cpu time: " + aCpuT);
		    	   //provider = share.getObjectPropertyValues(hasProDomain, ontology).iterator().next().toStringID().substring(TRANCATION);
		    		/*if (con_id != 0){
		    			job.setNewNode(workerN);
		    			NegHibConnect.hibJob(job);
		    		}*/
		    		/*if(reneg){
		    		    Random rand2 = new Random();
			   	        job_id = rand2.nextInt(10000 + 1);
			   	    
			   	        NegotiationDB.insertJob(job_id, con_id);
		    		}*/
		    		workerN = exeEn.toStringID().substring(TRANCATION);
		    	 //*****System.out.println("This provider can provide required resources: " + provider);
		    	   System.out.println("work node: " + workerN);
		    	   String measurement = measSets.iterator().next().getLiteral();
		    	   max_duration = maxTotalCpuT/aCpuNo;
		    	   
		    	   
		    	   endpoint = exeEn.getObjectPropertyValues(hasEnd, ontology).iterator().next().getDataPropertyValues(url_pro, ontology).iterator().next().getLiteral();
		    	   
		    	   System.out.println("*********endpoint in offer" + endpoint);
		    	   System.out.println("*********max total cpu timer in offer" + maxTotalCpuT);
		    	   System.out.println("*********max duration in offer" + max_duration);
		    	   /*int virtual; 
		    	   if(virSets.iterator().next().parseBoolean()){
		    		   virtual = 1;
		    	   }
		    	   else{
		    		   virtual = 0;
		    	   }*/
		    	   
		    	   finalRes = true;
		    	
		    	   Random rand = new Random();
		   	       long offer_no = rand.nextInt(10000 + 1);
		   	       
		   	    if(requiredCpuN<=aCpuNo){
					if(!coallo){
						Service service = new Service();
				    	service.setProvider(provider);
				    	//service.setService_name(workerN);
				    	service.setCpuNo(aCpuNo);
				    	service.setCharge(charge);
				    	service.setCost(cost);
				    	service.setMeasurement(measurement);
				    	//service.setMemory(mem);
				    	
				    	Offer offer = new Offer();
				
							//contract = new Contract();
				    	    offer.setId(offer_no);
				    	    offer.setNegId(neg_id);
				    	    offer.setJobId(job_id);
							offer.setAppname(app);
							offer.setUsername(name);
							offer.setOwner(owner);
							offer.setGroupname(grp);
							offer.setService(service);
							offer.setStartTime(startT);
							offer.setMaxTotalCpuT(max_duration);
							offer.setMaxCost(userMaxCost_value);
							offer.setMaxDuration(max_duration);
							offer.setStatus(NegState.Initiating.toString());
							offer.setLevel(level);
							offer.setWorker(workerN);
							offer.setShare(shareN);
							offer.setRequiredCpuT(totalCpuT);
							offer.setRequiredCpuNo(requiredCpuN);
							offer.setUserBalance(user_balance_value);
							offer.setEndpoint(endpoint);
							offer.setSub("");
							offer.setNefold(0);
							offer.setNumjobs(0);
							
							NegHibConnect.hibOffer(offer);
							String offer_contents = "=" + endpoint + "^";
						/*String offer_contents = "- Provider: "+ provider + ", worker: " +
	    		        		workerN + ", CPU Number: " +
							aCpuNo + ", required CPU Number: " +
							requiredCpuN +  ", Mem: " + mem + ", unit cost: "+ charge + ", type: Cloud;";*/
		                offers = offers + offer_no + offer_contents;
		                cloud_offer = true;
		                System.out.println("=========== 1 " + offers);
		                //return offers;
					}
					else{
						//coallo_done = true;
						if(!workerN.equalsIgnoreCase(contracted_node)){
						    Service service = new Service();
				    	    service.setProvider(provider);
				    	    //service.setService_name(workerN);
				    	    service.setCpuNo(aCpuNo);
				    	    service.setCharge(charge);
				    	    service.setCost(cost);
				    	    service.setMeasurement(measurement);
				    	    //service.setMemory(mem);
				    	
				    	    Offer offer = new Offer();
				
							//contract = new Contract();
				    	    offer.setId(offer_no);
				    	    offer.setNegId(neg_id);
				    	    offer.setJobId(job_id);
							offer.setAppname(app);
							offer.setUsername(name);
							offer.setOwner(owner);
							offer.setGroupname(grp);
							offer.setService(service);
							offer.setStartTime(startT);
							offer.setMaxTotalCpuT(max_duration);
							offer.setMaxCost(userMaxCost_value);
							offer.setMaxDuration(max_duration);
							offer.setStatus(NegState.Initiating.toString());
							offer.setLevel(level);
							offer.setWorker(workerN);
							offer.setShare(shareN);
							offer.setRequiredCpuT(totalCpuT);
							offer.setRequiredCpuNo(requiredCpuN);
							offer.setUserBalance(user_balance_value);
							offer.setEndpoint(endpoint);
							offer.setSub("");
							offer.setNefold(0);
							offer.setNumjobs(0);
							
							NegHibConnect.hibOffer(offer);
							
						    /*String temp_offer_contents = "worker: " +
	    		        		workerN + ", CPU Number: " +
							aCpuNo +  ", Mem: " + mem + ", unit cost: "+ charge + ", type: Cloud;";*/
							String temp_offer_contents = endpoint + "^";
							temp_offers = temp_offers + offer_no + "," + temp_offer_contents;
						    temp_offers_comb = temp_offers_comb + offer_no;
						    //offer.setSub(temp_offers_comb);
						
						    Random rand_comb_offer = new Random();
				   	        long comb_offer_id = rand_comb_offer.nextInt(10000 + 1);
						    /*Offer comb_offer = new Offer();
						    comb_offer.setId(comb_offer_id);
						    comb_offer.setSub(temp_offers_comb);
						    comb_offer.setStatus(NegState.Initiating.toString());
						    NegHibConnect.hibOffer(comb_offer);*/
						    offers = comb_offer_id + "=" + temp_offers;
						    //offers = comb_offer_id + "- Provider: "+ provider + ", " + temp_offers + temp_offer_contents;
						    System.out.println("$$$$$$$$$$$ provider: " + provider + "$$$$$$$$$$$ app: " + app);
						    System.out.println("$$$$$$$$$$$ final offer id: " + comb_offer_id);
						    System.out.println("$$$$$$$$$$$ final offers: " + offers);
						    System.out.println("*********** final sub offers ids: " + temp_offers_comb);
						    return offers;
					}
					}
				}
				
				//offers.put(offer_no, provider + cost + "Cloud");
				//offer_no++;
				//site_balance = site_balance - cost;
				//user_balance_value = user_balance_value - cost;
				
				//sub-offers
				else{
					if(!workerN.equalsIgnoreCase(contracted_node)){
						Service service = new Service();
				    	service.setProvider(provider);
				    	//service.setService_name(workerN);
				    	service.setCpuNo(aCpuNo);
				    	service.setCharge(charge);
				    	service.setCost(cost);
				    	service.setMeasurement(measurement);
				    	//service.setMemory(mem);
				    	
				    	Offer offer = new Offer();
				
							//contract = new Contract();
				    	    offer.setId(offer_no);
				    	    offer.setNegId(neg_id);
				    	    offer.setJobId(job_id);
							offer.setAppname(app);
							offer.setUsername(name);
							offer.setOwner(owner);
							offer.setGroupname(grp);
							offer.setService(service);
							offer.setStartTime(startT);
							offer.setMaxTotalCpuT(max_duration);
							offer.setMaxCost(userMaxCost_value);
							offer.setMaxDuration(max_duration);
							offer.setStatus(NegState.Initiating.toString());
							offer.setLevel(level);
							offer.setWorker(workerN);
							offer.setShare(shareN);
							offer.setRequiredCpuT(totalCpuT);
							offer.setRequiredCpuNo(requiredCpuN);
							offer.setUserBalance(user_balance_value);
							offer.setEndpoint(endpoint);
							offer.setSub("");
							offer.setNefold(0);
							offer.setNumjobs(0);
							
							NegHibConnect.hibOffer(offer); 
							
							String temp_offer_contents = endpoint + ";";
					/*String temp_offer_contents = "worker: " +
    		        		workerN + ", CPU Number: " + aCpuNo +  ", Mem: " + mem + ", unit cost: "+ charge + "; ";*/
					temp_offers = temp_offers + offer_no + "," + temp_offer_contents;
					//System.out.println("$$$$$$$$$$$ temp_offer: " + temp_offers);
					requiredCpuN = requiredCpuN - aCpuNo;	
					used_cpuNo = used_cpuNo + aCpuNo;
					coallo = true;
					//temp_offers_comb format: offer1;offer2
					temp_offers_comb = temp_offers_comb + offer_no + ";";
					System.out.println("$$$$$$$$$$$ offer id: " + temp_offers_comb);
					System.out.println("$$$$$$$$$$$ comb offer max duration: " + duration);
					}
					/*if(coallo_done){
						offers = offer_no + temp_offers;
						//System.out.println("=========== 2 " + offers);
					}*/
				}
		   	       //return offers;
		    	   /*Service service = new Service();
		    	   service.setProvider(provider);
		    	   //service.setService_name(workerN);
		    	   //service.setCpuNo(requiredCpuN);
		    	   service.setCpuNo(aCpuNo);
		    	   service.setMeasurement(measurement);
		    	
		    	   Offer offer = new Offer();
		
					//contract = new Contract();
		    	    offer.setId(offer_no);
					offer.setAppname(app);
					offer.setUsername(name);
					offer.setOwner(owner);
					offer.setGroupname(grp);
					offer.setService(service);
					offer.setStartTime(startT);
					offer.setMaxTotalCpuT(maxTotalCpuT);
					offer.setMaxCost(userMaxCost_value);
					offer.setMaxDuration(max_duration);
					offer.setRequiredCpuT(totalCpuT);
					offer.setRequiredCpuNo(requiredCpuN);
					offer.setStatus(NegState.Initiating.toString());
					offer.setShare(shareN);
					offer.setLevel(level);
					offer.setWorker(workerN);
					offer.setJobId(job_id);
					offer.setNegId(neg_id);
					offer.setEndpoint(endpoint);
					
					NegHibConnect.hibOffer(offer);
					
					offers = offers + offer_no + "- Provider: " + provider +  ", worker: " +
    		        		workerN + ", CPU Number: " +
							aCpuNo + ", required CPU Number: " +
									requiredCpuN + ", Type: Cluster;";
					//offers = offers + offer_no + "-" + endpoint;
					//offers = offers + offer_no + ","+ provider + ", Grid;";
					//offers.put(offer_no, provider + "Grid");
					offer_no++;*/
		    	//System.out.println("+++++++++++here" + finalRes);
		    	//count++;
		    	//System.out.println(count);
					
					//in real case, this should be accumulated 
					//return offers;
		    	}
		    	else if(appResource.size() > 1 && separateGridSearch(aCpuNo, (long)aCpuT)){
		    		sepCluster = true;
		    		
/*		    		// this part should be included in separateGridSearch
		    		workerN = exeEn.toStringID().substring(TRANCATION);
		    		//tempOfferMeta.put(temp_offer_no, value)
		    		offer_meta[1] = workerN;
		    		tempOfferMeta.put(temp_offer_no, offer_meta);

		    		//String meta = tempOfferMeta.get(temp_offer_no);
		    		//meta.concat(str)
		    		System.out.println("hhhhhhh " + temp_offer_no + offer_meta[0] + "***" + offer_meta[1]);
		    		System.out.println("hhhhhhhhhh " + tempOfferMeta.get(temp_offer_no).toString());
		    		temp_offer_no++;*/
		    		
		       	 for(Map.Entry<String, String[]> entry:appResource.entrySet()){
		       		 String appn = entry.getKey();
		       		 String offer_meta[] = new String[5];
		    		 //System.out.println(entry.getValue()[0] + "@@@@@@" + entry.getValue()[1]);
		    		 int rCpuNo = Integer.parseInt(entry.getValue()[0]);
		    		 long rCpuT = Long.parseLong(entry.getValue()[1]);
		    		 //int rCpuT = Integer.parseInt(entry.getValue()[1]);
		    		 if(rCpuNo <= aCpuNo && rCpuT <= aCpuT){
				       			 System.out.println("inside separateGridSearch");
				       			 System.out.println("execution enviroment available cpu no: " + aCpuNo + "; cpu time: " + aCpuT);
				       			 System.out.println(entry.getKey() + " app requireed cpu no: " + rCpuNo + "; cpu time: " + rCpuT);
						    	 //provider = share.getObjectPropertyValues(hasProDomain, ontology).iterator().next().toStringID().substring(TRANCATION);
						    	 //workerN = exeEn.toStringID().substring(TRANCATION);
						    	 System.out.println("This provider can provide required resources: " + provider);
						    	 //System.out.println("work node: " + workerN);
						    	 
						    	 //finalRes = true;
						    	 
							    	/*Service service = new Service();
							    	service.setProvider(provider);
							    	//service.setService_name(workerN);
							    	service.setCpuNo(cpuNo);
							    	
							    	Offer offer = new Offer();
							
										//contract = new Contract();
							    	    offer.setId(temp_offer_no);
										offer.setAppname(appInput);
										offer.setUsername(username);
										offer.setOwner(owner);
										offer.setGroupname(grpInput);
										offer.setService(service);
										offer.setStartTime(startT);
										//offer.setDuration(duration);
										offer.setStatus(NegState.Negotiation.toString());
										offer.setLevel(level);
							            //offer.setWoker(workerN);
										
										NegHibConnect.hibOffer(offer);*/
									
										/*temp_offers = temp_offers + temp_offer_no + "- Provider: " + provider +  ", CPU Number: " +
										aCpuNo + ", Type: Grid;";*/
										temp_offers = temp_offer_no + "- Provider: " + provider +  ", CPU Number: " +
												aCpuNo + ", Type: Grid;";
						    	        //offers = offers + offer_no + "CPU Number: " +
								    		//	 entry.getValue()[0] + ", app_name: " + entry.getKey() + ", Type: Grid.";
						    	 /*String offer_meta = "CPU Number: " +
						    			 entry.getValue()[0] + ", app_name: " + entry.getKey() + ", Type: Grid.";*/
										/*String offer_meta = "CPU Number: " +
											cpuNo + ", app_name: " + entry.getKey() + ", Type: Grid, ";*/
										//String appn = entry.getKey();
										offer_meta[0] = appn;
										// this part should be included in separateGridSearch
							    		workerN = exeEn.toStringID().substring(TRANCATION);
							    		//tempOfferMeta.put(temp_offer_no, value)
							    		offer_meta[1] = workerN;
							    		offer_meta[2] = "Grid";
							    		offer_meta[3] = Integer.toString(aCpuNo);
							    		tempOffers.put(temp_offer_no, temp_offers);
							    		tempOfferMeta.put(temp_offer_no, offer_meta);

							    		//String meta = tempOfferMeta.get(temp_offer_no);
							    		//meta.concat(str)
							    		//*****System.out.println("hhhhhhh " + temp_offer_no + appn + "***" + offer_meta[1]);
							    		//*****System.out.println("hhhhhhhhhh " + tempOfferMeta.get(temp_offer_no)[0]);
							    		temp_offer_no++;
		 
										//offers = offers + offer_no + ","+ provider + ", Grid;";
										//offers.put(offer_no, provider + "Grid");
										//appOffer.put((int) temp_offer_no, entry.getKey());
										//tempOffers.put(temp_offer_no, temp_offers);
										//tempOfferMeta.put(temp_offer_no, offer_meta);
										//temp_offer_no++;
							    		//*****System.out.println("temp offer: " + temp_offers);
										//System.out.println("temp offer meta: " + tempOfferMeta);		 
				       	 }
				    		//System.out.println("total offer number:" + (temp_offer_no-1));
		    			
		    		 }
		    		/*for(Map.Entry<String, String[]> entry:appResource.entrySet()){
		    			System.out.println("appResource values: " + entry.getKey() + ":" + entry.getValue()[0]);
		       		    if(Integer.parseInt(entry.getValue()[0]) <= aCpuNo && 
		       				 Integer.parseInt(entry.getValue()[1]) <= aCpuT){
		       			 
		       			 System.out.println("execution enviroment available cpu no: " + aCpuNo + "; cpu time: " + aCpuT);
				    	 //provider = share.getObjectPropertyValues(hasProDomain, ontology).iterator().next().toStringID().substring(TRANCATION);
				    	 //workerN = exeEn.toStringID().substring(TRANCATION);
				    	 System.out.println("This provider can provide required resources: " + provider);
				    	 //System.out.println("work node: " + workerN);
				    	 
				    	 //finalRes = true;
				    	 
					    	Service service = new Service();
					    	service.setProvider(provider);
					    	service.setService_name(workerN);
					    	service.setCpuNo(cpuNo);
					    	
					    	Offer offer = new Offer();
					
								//contract = new Contract();
					    	    offer.setId(offer_no);
								offer.setAppname(appInput);
								offer.setUsername(username);
								offer.setOwner(owner);
								offer.setGroupname(grpInput);
								offer.setService(service);
								offer.setStime(startT);
								//offer.setDuration(duration);
								offer.setStatus(NegState.Negotiating.toString());
								offer.setLevel(level);
					            offer.setWoker(workerN);
								
								NegHibConnect.hibOffer(offer);
							
								temp_offers = temp_offers + temp_offer_no + "- Provider: " + provider +  ", CPU Number: " +
								cpuNo + ", Type: Grid;";
				    	        //offers = offers + offer_no + "CPU Number: " +
						    		//	 entry.getValue()[0] + ", app_name: " + entry.getKey() + ", Type: Grid.";
				    	 String offer_meta = "CPU Number: " +
				    			 entry.getValue()[0] + ", app_name: " + entry.getKey() + ", Type: Grid.";
								//offers = offers + offer_no + ","+ provider + ", Grid;";
								//offers.put(offer_no, provider + "Grid");
								appOffer.put((int) temp_offer_no, entry.getKey());
								tempOffers.put((int) temp_offer_no, offer_meta);
								temp_offer_no++;
								System.out.println(offer_meta);
		       		 }	       		 
		       	 }
		    		System.out.println("total offer number:" + (temp_offer_no-1));*/
		    		
		    		//String new_offers;
		    	}
		    	/*else if(){
		    		finalRes = false;
		    		//return finalRes;
		    	}*/
		    	else{
		    		finalRes = false;
		    	}
		    	
		    }
		    // To check for CLOUD case contained in this Share
		    // this could also be cloud-based grid
		    else if(balSets.iterator().hasNext() && cpuNoSets.iterator().hasNext()){
		    	//String pay_res = exeEn.getDataPropertyValues(pay_pro, ontology).iterator().next().getLiteral();
			    //System.out.println("2222222" + pay_res);
		    	
	    		aCpuNo = Integer.parseInt(cpuNoSets.iterator().next().getLiteral());
	    		System.out.println("available cpu numner: " + aCpuNo);
	    		//user_balance = Double.parseDouble(balSets.iterator().next().getLiteral());
	    		site_balance = Double.parseDouble(balSets.iterator().next().getLiteral());
	    		charge = Double.parseDouble(chargeSets.iterator().next().getLiteral());
	    		//System.out.println("**********charge: " + charge);
	    		//System.out.println("**********aCpuNo: " + aCpuNo);
	    		//System.out.println("**********balance: " + site_balance);
	    		//distinguish single charge and total charge
	    		if(duration == 0){
	    			//duration = 1;
	    			cost = charge;
	    		}
	    		else{
	    			//duration is the min_duration
	    		    cost =  charge * (duration/3600);
	    		}
	    		double mem;
	    		System.out.println("*****111 charge: " + charge);
	    		System.out.println("*****111 duration: " + duration);
	    		System.out.println("*****111 cost: " + cost);
	    		//System.out.println("*****111 used cpu number: " + contracted_cpuNo);
	    		System.out.println("*****111 user max costvalue: " + userMaxCost_value);
	    		System.out.println("*****222 site balance: " + site_balance);
	    		System.out.println("*****222 available cpu no: " + aCpuNo);
	    		System.out.println("*****222 use balance : " + user_balance_value);
	    		
	    		if(reneg){
	    			
	    			    if (contracted_node.equalsIgnoreCase(node)){
	    			    	requiredCpuN = core;
	    			    	System.out.println("****************1 requiredCpuN:" + requiredCpuN);
	    			    }
	    			    else{
	    			    	//used_cpuNo here depends on if Junyi's code support it
	    			    	//the other case: requiredCpuN > core > contracted_cpuNo
	    			    	//cost =  charge * (duration/3600);
	    			    	//userMaxCost_value = userMaxCost_value - old_charge;
	    			    	//user_balance_value = user_balance_value - old_charge;
	    			    	requiredCpuN = core - used_cpuNo;
	    			    	System.out.println("****************2 requiredCpuN:" + requiredCpuN);
	    			    }
	    		/*}
	    		    else{
	    		    	System.out.println("your balance is not sufficent to increase cpu number for cloud services.");
	    		    	break;
	    		    }*/
	    		}
	    		// cost here is minCost
	    		if(cost<=site_balance && cost <= userMaxCost_value && cost <= user_balance_value){
	    			Random rand = new Random();
			   	    long offer_no = rand.nextInt(10000 + 1);
			   	    //long temp_offer_num = rand.nextInt(10000 + 1);
	    			//to calculate max_duration
			   	    userMaxCost_value = Math.min(userMaxCost_value, Math.min(site_balance, user_balance_value));
			   	    if(reneg){
			   	    	
			   	    	max_duration = (long) (userMaxCost_value /(charge + old_charge) * 3600); 
			   	    }
			   	    else{
			   	    	max_duration = (long) (userMaxCost_value /charge * 3600); 
			   	    }
	    			System.out.println("-------max_duration: " + max_duration);
	    			
	    			Set<OWLLiteral> memSets = exeEn.getDataPropertyValues(mem_pro, ontology);
	    			
	    			/*int virtual;
	    		    if(virSets.iterator().next().parseBoolean()){
	    		    	virtual = 1;
	    		    }
	    		    else{
	    		    	virtual = 0;
	    		    }*/
	    			String measurement = measSets.iterator().next().getLiteral();
	    			
	    			System.out.println("*******measurement: " + measurement);
	    			mem = Double.parseDouble(memSets.iterator().next().getLiteral());
	    			//*****System.out.println("*****111 avaliable mem: " + mem);
	    			/*if (con_id != 0){
		    			job.setNewNode(workerN);
		    			NegHibConnect.hibJob(job);
		    		}*/
	    			//provider = share.getObjectPropertyValues(hasProDomain, ontology).iterator().next().toStringID().substring(39);
	    			/*if(reneg){
	    			    Random rand2 = new Random();
	    	   	        job_id = rand2.nextInt(10000 + 1);
	    	   	        NegotiationDB.insertJob(job_id, con_id);
	    			}*/
	    	   	    
	    			workerN = exeEn.toStringID().substring(TRANCATION);
	    			endpoint = exeEn.getObjectPropertyValues(hasEnd, ontology).iterator().next().getDataPropertyValues(url_pro, ontology).iterator().next().getLiteral();
			    	   
			    	System.out.println("*********endpoint in offer" + endpoint);
			    	//*****System.out.println("*****111 worker node: " + workerN);
			    	//*****System.out.println("*****111This provider can provide required resources: " + provider);
	    		    finalRes = true;
	    		    
	    		    /*Service service = new Service();
			    	service.setProvider(provider);
			    	//service.setService_name(workerN);
			    	service.setCpuNo(aCpuNo);
			    	service.setCharge(charge);
			    	service.setCost(cost);
			    	service.setMeasurement(measurement);
			    	//service.setMemory(mem);
			    	
			    	Offer offer = new Offer();
			
						//contract = new Contract();
			    	    offer.setId(offer_no);
			    	    offer.setNegId(neg_id);
			    	    offer.setJobId(job_id);
						offer.setAppname(app);
						offer.setUsername(name);
						offer.setOwner(owner);
						offer.setGroupname(grp);
						offer.setService(service);
						offer.setStartTime(startT);
						offer.setMaxTotalCpuT(max_duration);
						offer.setMaxCost(userMaxCost_value);
						offer.setMaxDuration(duration);
						offer.setStatus(NegState.Initiating.toString());
						offer.setLevel(level);
						offer.setWorker(workerN);
						offer.setShare(shareN);
						offer.setRequiredCpuT(totalCpuT);
						offer.setRequiredCpuNo(requiredCpuN);
						offer.setUserBalance(user_balance_value);
						offer.setEndpoint(endpoint);
						offer.setSub("");
						
						NegHibConnect.hibOffer(offer);*/
						/*offers = offers + offer_no + "- Provider: "+ provider + ", CPU Number: " +
								aCpuNo +  ", Mem: " + mem + ", unit cost: "+ charge + ", type: Cloud;";*/
						/*String offer_contents = "- Provider: "+ provider + ", worker: " +
	    		        		workerN + ", CPU Number: " +
							aCpuNo + ", required CPU Number: " +
							requiredCpuN +  ", Mem: " + mem + ", unit cost: "+ charge + ", type: Cloud;";*/
						
						if(requiredCpuN<=aCpuNo){
							if(!coallo){
								Service service = new Service();
						    	service.setProvider(provider);
						    	//service.setService_name(workerN);
						    	service.setCpuNo(aCpuNo);
						    	service.setCharge(charge);
						    	service.setCost(cost);
						    	service.setMeasurement(measurement);
						    	//service.setMemory(mem);
						    	
						    	Offer offer = new Offer();
						
									//contract = new Contract();
						    	    offer.setId(offer_no);
						    	    offer.setNegId(neg_id);
						    	    offer.setJobId(job_id);
									offer.setAppname(app);
									offer.setUsername(name);
									offer.setOwner(owner);
									offer.setGroupname(grp);
									offer.setService(service);
									offer.setStartTime(startT);
									offer.setMaxTotalCpuT(max_duration);
									offer.setMaxCost(userMaxCost_value);
									offer.setMaxDuration(max_duration);
									offer.setStatus(NegState.Initiating.toString());
									offer.setLevel(level);
									offer.setWorker(workerN);
									offer.setShare(shareN);
									offer.setRequiredCpuT(totalCpuT);
									offer.setRequiredCpuNo(requiredCpuN);
									offer.setUserBalance(user_balance_value);
									offer.setEndpoint(endpoint);
									offer.setSub("");
									offer.setNefold(0);
									offer.setNumjobs(0);
									
									NegHibConnect.hibOffer(offer);
									String offer_contents = "=" + endpoint + "|";
								/*String offer_contents = "- Provider: "+ provider + ", worker: " +
			    		        		workerN + ", CPU Number: " +
									aCpuNo + ", required CPU Number: " +
									requiredCpuN +  ", Mem: " + mem + ", unit cost: "+ charge + ", type: Cloud;";*/
	    		                offers = offers + offer_no + offer_contents;
	    		                requiredCpuN = requiredTotalCpuN;
	    		                cloud_offer = true;
	    		                System.out.println("=========== 1 " + offers);
	    		                //return offers;
							}
							else{
								//coallo_done = true;
								if(!workerN.equalsIgnoreCase(contracted_node)){
								    Service service = new Service();
						    	    service.setProvider(provider);
						    	    //service.setService_name(workerN);
						    	    service.setCpuNo(aCpuNo);
						    	    service.setCharge(charge);
						    	    service.setCost(cost);
						    	    service.setMeasurement(measurement);
						    	    //service.setMemory(mem);
						    	
						    	    Offer offer = new Offer();
						
									//contract = new Contract();
						    	    offer.setId(offer_no);
						    	    offer.setNegId(neg_id);
						    	    offer.setJobId(job_id);
									offer.setAppname(app);
									offer.setUsername(name);
									offer.setOwner(owner);
									offer.setGroupname(grp);
									offer.setService(service);
									offer.setStartTime(startT);
									offer.setMaxTotalCpuT(max_duration);
									offer.setMaxCost(userMaxCost_value);
									offer.setMaxDuration(max_duration);
									offer.setStatus(NegState.Initiating.toString());
									offer.setLevel(level);
									offer.setWorker(workerN);
									offer.setShare(shareN);
									offer.setRequiredCpuT(totalCpuT);
									offer.setRequiredCpuNo(requiredCpuN);
									offer.setUserBalance(user_balance_value);
									offer.setEndpoint(endpoint);
									offer.setSub("");
									offer.setNefold(0);
									offer.setNumjobs(0);
									
									NegHibConnect.hibOffer(offer);
									
								    /*String temp_offer_contents = "worker: " +
			    		        		workerN + ", CPU Number: " +
									aCpuNo +  ", Mem: " + mem + ", unit cost: "+ charge + ", type: Cloud;";*/
									String temp_offer_contents = endpoint + "|";
									temp_offers = temp_offers + offer_no + "," + temp_offer_contents;
								    temp_offers_comb = temp_offers_comb + offer_no;
								    //offer.setSub(temp_offers_comb);
								
								    Random rand_comb_offer = new Random();
						   	        long comb_offer_id = rand_comb_offer.nextInt(10000 + 1);
								    /*Offer comb_offer = new Offer();
								    comb_offer.setId(comb_offer_id);
								    comb_offer.setSub(temp_offers_comb);
								    comb_offer.setStatus(NegState.Initiating.toString());
								    NegHibConnect.hibOffer(comb_offer);*/
								    offers = comb_offer_id + "=" + temp_offers + offers;
								    //offers = comb_offer_id + "- Provider: "+ provider + ", " + temp_offers + temp_offer_contents;
								    requiredCpuN = requiredTotalCpuN;
								    
								    offer_cpu.add(aCpuNo);
									offer_nos.add(offer_no);
									offer_endpoints.add(endpoint);
									offer_count++;
								    mix_offer = true;
								    System.out.println("$$$$$$$$$$$ final offer id: " + comb_offer_id);
								    System.out.println("$$$$$$$$$$$ final offers: " + offers);
								    System.out.println("*********** final sub offers ids: " + temp_offers_comb);
								    temp_offers="";
								    temp_offers_comb = "";
								    //return offers;
							}
							}
						}
						
						//offers.put(offer_no, provider + cost + "Cloud");
						//offer_no++;
						//site_balance = site_balance - cost;
						//user_balance_value = user_balance_value - cost;
						
						//sub-offers
						else{
							if(!workerN.equalsIgnoreCase(contracted_node)){
								Service service = new Service();
						    	service.setProvider(provider);
						    	//service.setService_name(workerN);
						    	service.setCpuNo(aCpuNo);
						    	service.setCharge(charge);
						    	service.setCost(cost);
						    	service.setMeasurement(measurement);
						    	//service.setMemory(mem);
						    	
						    	Offer offer = new Offer();
						
									//contract = new Contract();
						    	    offer.setId(offer_no);
						    	    offer.setNegId(neg_id);
						    	    offer.setJobId(job_id);
									offer.setAppname(app);
									offer.setUsername(name);
									offer.setOwner(owner);
									offer.setGroupname(grp);
									offer.setService(service);
									offer.setStartTime(startT);
									offer.setMaxTotalCpuT(max_duration);
									offer.setMaxCost(userMaxCost_value);
									offer.setMaxDuration(max_duration);
									offer.setStatus(NegState.Initiating.toString());
									offer.setLevel(level);
									offer.setWorker(workerN);
									offer.setShare(shareN);
									offer.setRequiredCpuT(totalCpuT);
									offer.setRequiredCpuNo(requiredCpuN);
									offer.setUserBalance(user_balance_value);
									offer.setEndpoint(endpoint);
									offer.setSub("");
									offer.setNefold(0);
									offer.setNumjobs(0);
									
									NegHibConnect.hibOffer(offer);
									
									String temp_offer_contents = endpoint + ";";
							/*String temp_offer_contents = "worker: " +
		    		        		workerN + ", CPU Number: " + aCpuNo +  ", Mem: " + mem + ", unit cost: "+ charge + "; ";*/
							temp_offers = temp_offers + offer_no + "," + temp_offer_contents;
							temp_offers_comb = temp_offers_comb + offer_no + ";";
							 //int offer_cpu[];
							 //int offer_count=0;
						    
						    
							System.out.println("$$$$$$$$$$$ temp_offer: " + temp_offers);
							System.out.println("$$$$$$$$$$$ temp_offers_comb: " + temp_offers_comb);
							requiredCpuN = requiredCpuN - aCpuNo;
							int current_requiredCpuN = requiredCpuN;
							used_cpuNo = used_cpuNo + aCpuNo;
							coallo = true;
							//temp_offers_comb format: offer1;offer2
							//temp_offers_comb = temp_offers_comb + offer_no + ";";
							//System.out.println("$$$$$$$$$$$ temp offers combine: " + temp_offers_comb);
							//System.out.println("$$$$$$$$$$$ temp offers: " + temp_offers);
							if(requiredCpuN == 1){
								for(int i=0; i<offer_nos.size(); i++){
									if(requiredCpuN <= (Integer)offer_cpu.get(i)){
										 Random rand_comb_offer = new Random();
								   	     long comb_offer_id = rand_comb_offer.nextInt(10000 + 1);
										 /*Offer comb_offer = new Offer();
										 comb_offer.setId(comb_offer_id);
										 comb_offer.setSub(temp_offers_comb);
										 comb_offer.setStatus(NegState.Initiating.toString());
										 NegHibConnect.hibOffer(comb_offer);*/
								   	     //temp_offers = temp_offers + offer_nos.get(i) + "," + offer_endpoints.get(i) + "^";
										 temp_offers_comb = temp_offers_comb +  offer_nos.get(i) + ";";
										 offers = comb_offer_id + "=" + temp_offers + offer_nos.get(i) + "," + offer_endpoints.get(i) + "^" + offers ;
									}
								}
								offer_cpu.add(aCpuNo);
								offer_nos.add(offer_no);
								offer_endpoints.add(endpoint);
								offer_count++;
								requiredCpuN = requiredTotalCpuN;
								temp_offers="";
								temp_offers_comb="";
							}
							else{
							  if(!mix_offer){
								//before a contract is formed
								//hashmap.put(offer_no, aCpuNo);
								offer_cpu.add(aCpuNo);
								offer_nos.add(offer_no);
								offer_endpoints.add(endpoint);
								offer_count++;
							}
							  else{
							//if(mix_offer){
								System.out.println("requiredCpuN*****: " + requiredCpuN);
								System.out.println("offer_cpu size*****: " + offer_cpu.size());
								System.out.println("offer_nos size*****: " + offer_nos.size());
								System.out.println("offer_endpoints size*****: " + offer_endpoints.size());
								int cpu_so_far;
								//Integer[] offer_cpu = (Integer[]) hashmap.values().toArray();
								long temp_offer_no;
								String temp_temp_offers = "";
								String temp_temp_offer_contents = "";
								String temp_temp_offers_comb = "";
							    final int length = offer_nos.size();
								for(int j=0; j<offer_cpu.size(); j++){
									for(int i=1; i<offer_cpu.size(); i++){
										if(i>j){
										cpu_so_far = (Integer) offer_cpu.get(j);
										System.out.println("cpu_so_far*****: " + cpu_so_far);
										System.out.println("requiredCpuN*****: " + requiredCpuN);
										System.out.println("j*****: " + j);
										System.out.println("i*****: " + i);
										//temp_offers_comb = temp_offers_comb + offer_no + ";";
										//System.out.println("$$$$$$$$$$$ temp offers combine: " + temp_offers_comb);
										//System.out.println("$$$$$$$$$$$ temp offers: " + temp_offers);
										
										if(cpu_so_far < requiredCpuN){
											//if(i!=j && i<offer_cpu.size()){
											System.out.println("@@@@@@@@@ 1");
											//to get 1st old offer info
											temp_offer_no = (Long) offer_nos.get(j);
											temp_temp_offer_contents = temp_offer_no + "," + offer_endpoints.get(j) + ";";
											temp_temp_offers = temp_temp_offers + temp_temp_offer_contents;
											System.out.println("@@@@@@@@@ 1 temp_temp_offer_contents: " + temp_offer_contents);
											//to store 1st old offer no
											temp_temp_offers_comb = temp_temp_offers_comb + temp_offer_no + ";";
											System.out.println("@@@@@@@@@ 1 temp_temp_offers_comb: " + temp_temp_offers_comb);
											//to check if 2nd old offer meet demands
											cpu_so_far = cpu_so_far + (Integer) offer_cpu.get(i);
											//requiredCpuN = requiredCpuN - (Integer) offer_cpu.get(j);
											//i++;
											//add offer id here
											//}
										}
										if(cpu_so_far >= requiredCpuN){
											System.out.println("@@@@@@@@@ 2");
											//to get 2nd old offer no.
											temp_offer_no = (Long) offer_nos.get(i);
											//to store 2nd old offer no.
											temp_temp_offer_contents = temp_offer_no + "," + offer_endpoints.get(i) + "^";
											temp_temp_offers = temp_temp_offers + temp_temp_offer_contents;
											temp_temp_offers_comb = temp_temp_offers_comb + temp_offer_no + ";";
											System.out.println("@@@@@@@@@ 2 temp_temp_offers_comb: " + temp_temp_offers_comb);
											
											//temp_offer_contents = endpoint + "^";
											//temp_offers = temp_offers + offer_no + "," + temp_offer_contents;
											//temp_offers = temp_offers + temp_offer_contents;
										    
										    //offer.setSub(temp_offers_comb);
										
										    Random rand_comb_offer = new Random();
								   	        long comb_offer_id = rand_comb_offer.nextInt(10000 + 1);
										    /*Offer comb_offer = new Offer();
										    comb_offer.setId(comb_offer_id);
										    comb_offer.setSub(temp_offers_comb);
										    comb_offer.setStatus(NegState.Initiating.toString());
										    NegHibConnect.hibOffer(comb_offer);*/
										    offers = comb_offer_id + "=" + temp_offers + temp_temp_offers + offers;
										    //offers = comb_offer_id + "- Provider: "+ provider + ", " + temp_offers + temp_offer_contents;
										    requiredCpuN = current_requiredCpuN;										
										    //offer_cpu.add(aCpuNo);
											//offer_nos.add(offer_no);
											//offer_endpoints.add(endpoint);
											//offer_count++;
											System.out.println("$$$$$$$$$$$ final offers combine: " + temp_offers_comb + temp_temp_offers_comb);
											System.out.println("$$$$$$$$$$$ final offers: " + offers);
											temp_temp_offers="";
											temp_temp_offers_comb = "";
										}
									}
									}
								}
								offer_cpu.add(aCpuNo);
								offer_nos.add(offer_no);
								offer_endpoints.add(endpoint);
								offer_count++;
								requiredCpuN = requiredTotalCpuN;
								temp_offers="";
								temp_offers_comb="";
							}
							}
							
							}
							/*if(coallo_done){
								offers = offer_no + temp_offers;
								//System.out.println("=========== 2 " + offers);
							}*/
						}
						
						//System.out.println("inside cloud search: " + cloud_offer);
					    //return offers;
	    		}
	    		// this could be reserved, as even if for parallel nodes, the requiredCpuN would be the remainded one to be searched for
	    		else if (requiredCpuN>aCpuNo && cost <= userMaxCost_value && cost <= user_balance_value){
	    			
	    			resource_search = true;
	    		}
	    		//if a cloud provider cannot meet all apps' demands, search for partly satisfied ones
	    		//the preferred one should be the one could not be satisfied by grid
	    		else{
	    			//finalRes = false;
	    			//return offers;
	    		    cloud_offer = false;
	    			//separateCloudSearch(int cpuN, int unit_cost, long duration, double balance)
	    		    
	    		    // for workflow job
	    			if(appResource.size() > 1 && separateCloudSearch(cpuNo, cost, site_balance)){
	    				sepCloud = true;
	    				
	    	    		for(Map.Entry<String, String[]> entry:appResource.entrySet()){
	    	    			String appn = entry.getKey();
	   		       		    String offer_meta[] = new String[5];
			       		    if(Integer.parseInt(entry.getValue()[0]) <= aCpuNo && site_balance >= cost){
			       		    	cloud_offer = true;
			       		   //*****System.out.println("appResource values: " + entry.getKey() + ":" + entry.getValue()[0]);
			       		   //*****System.out.println("execution enviroment available cpu no: " + aCpuNo);
					    	 //provider = share.getObjectPropertyValues(hasProDomain, ontology).iterator().next().toStringID().substring(TRANCATION);
					    	 //workerN = exeEn.toStringID().substring(TRANCATION);
			       		   //*****System.out.println("222This provider can provide required resources: " + provider);
					    	 //System.out.println("work node: " + workerN);
					    	 
					    	 //finalRes = true;
					    	 
						    	Service service = new Service();
						    	service.setProvider(provider);
						    	//service.setService_name(workerN);
						    	service.setCpuNo(cpuNo);
						    	
						    	Offer offer = new Offer();
						
									//contract = new Contract();
						    	    offer.setId(temp_offer_no);
									offer.setAppname(appInput);
									offer.setUsername(username);
									offer.setOwner(owner);
									offer.setGroupname(grpInput);
									offer.setService(service);
									offer.setStartTime(startT);
									//offer.setDuration(duration);
									offer.setStatus(NegState.Initiating.toString());
									offer.setLevel(level);
					                //offer.setWoker(workerN);
									
									NegHibConnect.hibOffer(offer);
									
					    	        temp_offers = temp_offer_no + "- Provider: " + provider +  ", CPU Number: " +
					    	        		aCpuNo + ", Type: Cloud.";
					    	 /*String offer_meta = "CPU Number: " +
					    			 entry.getValue()[0] + ", app_name: " + entry.getKey() + ", unit cost: " + charge + ", Type: Cloud.";*/
									//offers = offers + offer_no + ","+ provider + ", Grid;";
									//offers.put(offer_no, provider + "Grid");
									//appOffer.put((int) temp_offer_no, entry.getKey());
									//tempOffers.put(temp_offer_no, offer_meta);
					    	        offer_meta[0] = appn;
									// this part should be included in separateGridSearch
						    		workerN = exeEn.toStringID().substring(TRANCATION);
						    		//tempOfferMeta.put(temp_offer_no, value)
						    		offer_meta[1] = workerN;
						    		offer_meta[2] = "Cloud";
						    		offer_meta[3] = Integer.toString(aCpuNo);
						    		offer_meta[4] = Double.toString(cost);
						    		tempOffers.put(temp_offer_no, temp_offers);
						    		tempOfferMeta.put(temp_offer_no, offer_meta);
									temp_offer_no++;
									System.out.println(offer_meta);
									
			       		 }	       		 
			       	 }
/*	    			if(cost<=balance && Integer.parseInt(appResource.get(not_app)[0])<=aCpuNo){
	    				cloud_offer = cloud_offer || true;
	    				String offer_meta = offer_no + "- Provider: " + provider +  ", CPU Number: " +
	    						cpuNo + ", Type: Grid;";
	    				tempOffers.put((int) offer_no, offer_meta);
	    	        	System.out.println("hello here");
	    	        	System.out.println(offer_meta);
	    	        	
	    			}*/
	    			}
	    			/*if(cloud_offer){
	    				//return offers;
	    				//*****System.out.println("********* im here inside cloud");
	    			}
	    			else{
	    				//*****System.out.println("********* im here outside cloud");
	    				System.out.println("outside cloud search: " + cloud_offer);
	    				return "empty offer";
	    			}*/
	    		}
	    	}
		    else{
		    	//to search for cluster resources
		    	finalRes = false;
		    	//return offers;
		    	//return finalRes;
		    }
		}
		
		if(!offers.isEmpty()){
			//*****System.out.println("single worker node offers: " + offers);
	   	    NegotiationDB.insertNeg(neg_id, offers); 
	   	    
			return offers;
		}
		//System.out.println("HERE IS THE PROVIDER: " + pros[0] + pros[1]);
		//System.out.println("*************PART3 DONE");
		//finalRes = finalRes||res2;
		//*****System.out.println("=======cannot run by single node " + finalRes);
		/*Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");
		//sdf1.format(cal1.getTime());
		Calendar cal2 = Calendar.getInstance();
    	//cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	String date = sdf1.format(cal1.getTime()) + "T" + sdf.format(cal2.getTime()) + "+7:00";
    	
		Contract contract = new Contract();
		if(finalRes){
			//contract = new Contract();
			contract.setApp(appInput);
			contract.setId(55555);
			contract.setPeople(username);
			contract.setProvider(provider);
			contract.setOwner(owner);
			contract.setColla(grpInput);
			contract.setService("water");
			contract.setDate(date);
			contract.setStime("2016");
			//contract.setDuration(duration);
		}*/
		
		//this is to check if this Share can provide resources for required single apps
		//true means a worker node can meet one of the app's requirements
		if(sepCluster || sepCloud){
			System.out.println("speCluster or sepCloud is true.");
			if(canMerge()){
				Random rand = new Random();
		   	    long offer_no = rand.nextInt(10000 + 1);
				//to find out if separated meta-offers can provide an offer
				/*System.out.println("@@@@@@@@@@@@@@@@@@ can merge is true");
				
				Service service = new Service();
		    	service.setProvider(provider);
		    	//service.setService_name(workerN);
		    	service.setCpuNo(aCpuNo);
		    	service.setCost(cost);
		    	
		    	Offer offer = new Offer();
		
					//contract = new Contract();
		    	    offer.setId(offer_no);
					offer.setAppname(appInput);
					offer.setUsername(username);
					offer.setOwner(owner);
					offer.setGroupname(grpInput);
					offer.setService(service);
					offer.setStime(startT);
					//offer.setDuration(duration);
					offer.setStatus(NegState.Negotiating.toString());
					offer.setLevel(level);
	                //offer.setWoker(workerN);
					
					NegHibConnect.hibOffer(offer);*/
				offers = offer_no + "- Provider: " + provider +  ", CPU Number: " +
	        			aCpuNo + ", cost: " + cost + ", Type: Cluster/Cloud;";
				//offers = offer_no + 
	        	//offers = "**************************************";
				//*****System.out.println("after separateSearch hello offers: " + offers);

			}
			else{
				//*****System.out.println("separate offers cannot be merged");
				//offers = "empty";
			}
			return offers;
			/*boolean include_all = true;
			//int not_app_count=0;
			Map<String, Boolean> counting = new HashMap<String, Boolean>();
	        for(Map.Entry<String, String[]> entryr:appResource.entrySet()){
		        for(Entry<Integer, String> entry:appOffer.entrySet()){
		        	//counting.put(entryr.getKey(), false);
   		 
   		            //if(!entry.getValue().equalsIgnoreCase(entryr.getKey())){
   		            if(!entryr.getKey().equalsIgnoreCase(entry.getValue())){
   		        	    //System.out.println(entryr.getKey() + "*****" + entry.getKey());
   		        	    //counting.put(entryr.getKey(), true);
   		        	    //offers = offers + tempOffers.get(entry.getKey());
   		            	include_all = false;
   		 }
   		           else{
   		        	   include_all = false;
   		            	//not_app[not_app_count] = entryr.getKey();
   		            	//System.out.println("hello im here");
   		            }
   		 }		              
   	 }
	        //System.out.println("@@@@@"+ counting.get("visLib"));
	        for(Entry<String, Boolean> entryb:counting.entrySet()){
	        	include_all = entryb.getValue() && include_all;
	        }*/
	        
			//separateSearch true means existing contract can meed demands with two exe envs for two apps
/*	        if (separateSearch()){
	        	offers = offer_no + "- Provider: " + provider +  ", CPU Number: " +
	        			aCpuNo + ", cost: " + cost + ", Type: Cluster/Cloud;";
	        	offers = "**************************************";
	        	System.out.println("after separateSearch hello offers: " + offers);
	        	return offers;
	        }
	        //only part of apps' requirements can be satisfied
	        //to do the privilege check here
	        else{
	        	//if it goes here, it indicates not a single exe env can run this wf
	        	// to search for Offer db see if existing offers match with lower privilege
	        	
	        	// only requester's privilege larger than 1
	        	if(level>1){
	        		//offers = getAlternative();
	        		System.out.println("********************level 1");
	        	}
	        	return "did not go to alternative ";
                //should keep tempOffers
	        	//and search for cloud service
	        }*/
		}
		//if this Share cannot provide resources for required single apps
		else{
			
			if(offers.isEmpty() & resource_search){
				//resource-oriented matchmaking by ServiceReasoning
				//parameters passed: os, cpu-model, cpu-speed, cpu-no
				String[] resource = getAppInfo("ResvShare"); 
				//System.out.println("*******" + resource[0]);
				/*String user_name= username;
				String os_input = app_os;
				String cpu_model = app_cpu_model;
				String cpu_speed = app_cpu_speed;
				String cpu_no = String.valueOf(requiredCpuN);
				
				System.out.println(user_name + " " + os_input + " " + cpu_model + " " + cpu_speed + " " + cpu_no);
				resource[0] = user_name;
				resource[1] = os_input;
				resource[2] = cpu_model;
				resource[3] = cpu_speed;
				resource[4] = cpu_no;*/
				offers = ServiceReasoning.providerSearch(resource, "ResvPolicy");
				//System.out.println("*******" + ServiceReasoning.providerSearch(resource, "ResvPolicy"));
				//System.out.println("im here");
				return offers;
			}
			
			//to seach for cloud service for all required apps
			/*if(level>1)
        		//offers = getAlternative();
				System.out.println("********************2");*/
			else{
				return "empty offer";		
			}
        	}
        	//return "did not go to alternative 2";
		}
		
		//to be continued
		//to match available offers for wf
		/*boolean include = false;
		//int not_app_count=0;
		Map<String, Boolean> counting2 = new HashMap<String, Boolean>();
            int counting = 0;
            String contract = "";
            int contract_no = 1;
            String app_name = appOffer.values().iterator().next();
            //String[] apps = (String[]) appOffer.values().toArray();
            //System.out.println("#######" + apps[0]);
            
            // after this loop, it will get all can be run apps
            // then need to compare all apps includes all required apps
	        for(Entry<Integer, String> entry:appOffer.entrySet()){
	        	String app_t = entry.getValue();
	        	//include = !counting2.containsKey(app_t); 
	        	include = counting2.containsKey(app_t);
	        	//include = app_name.equalsIgnoreCase(app_t);
	        	if(!include){
	        		int offer_n = entry.getKey(); 
	        		String offer_c = tempOffers.get(offer_n);
	            	contract = contract + offer_c;
	            	counting2.put(app_t, true);
	        		//counting2.put(app_t, true);
	        		//System.out.println("&&&&&&&&");
	        	}
		 }
	        boolean include_all_app = true;
	        for(Entry<String, String[]> entry:appResource.entrySet()){
	        	String app_n = entry.getKey();
	        	include_all_app = counting2.containsKey(app_n) && include_all_app;        	
	        }

	        contract = contract_no + "- Provider: " + provider +  ", " + contract + ";";
	        System.out.println(contract);
        
        if (include){
        	//if it goes here, it indicates not a single exe env can run this wf
        	// to search for Offer db see if existing offers match with lower privilege
        	
        	// only requester's privilege larger than 1
        	if(level>1){
        		
        	}
        	
        	offers = offer_no + "- Provider: " + provider +  ", CPU Number: " +
					requiredCpuN + ", Type: Grid;";
        	System.out.println("hello");
        	
        	System.out.println("finally here hello*******");
        	
        	return offers;
        }
        else{
        	System.out.println("finally here*******");
        	return "";
        }*/
		//return offers;
		
	//}
	
    /*public static boolean separateSearch(){
    	boolean separate = false;
    	//int offer_no = 0;
    	for(Map.Entry<String, String[]> entryr:appResource.entrySet()){
	        for(Entry<Integer, String> entry:appOffer.entrySet()){
	        	//counting.put(entryr.getKey(), false);
	        	separate = entryr.getKey().equalsIgnoreCase(entry.getValue()) ||
	        			separate;   
	        	//offer_no = entry.getKey();
		  
		            //if(!entry.getValue().equalsIgnoreCase(entryr.getKey())){
		            if(!entryr.getKey().equalsIgnoreCase(entry.getValue())){
		        	    System.out.println(entryr.getKey() + "*****" + entry.getValue());
		        	    //counting.put(entryr.getKey(), true);
		        	    //offers = offers + tempOffers.get(entry.getKey());
		            	//separate = false;
		            	return false;
		 }
    	//return separate;
    }
    	}
    	//System.out.println("**************" + offers);
    	return separate;
    }*/
    
    public static boolean canMerge(){
    	String offers = null;
    	boolean merge = true;
    	//boolean first_round = true;
    	//String[] workers = new String[5];
    	List<String> sites = new ArrayList<String>();
    	//String app = "";
    	//String site = "";
    	
    	//List<Integer> offer_included = new ArrayList<Integer>();
    	//Integer[] offer_nos = new Integer[2];
    	Set<Integer> offer_comb = new HashSet();
    	List<String> app_included = new ArrayList<String>();
    	Integer[] offer_comb_s = new Integer[5];
    	//Integer[] offer_included = new Integer[5];
    	//int offer_no = 0;
    	System.out.println("*****tempOfferMeta size: " + tempOfferMeta.size());
    	//System.out.println("*****tempOfferMeta here: " + tempOfferMeta.get(12)[0]);
    	int i = 0;
		//int j = 0;
		int k = 1 ;
    	//for(Map.Entry<String, String[]> aentry:appResource.entrySet()){
    		//app = aentry.getKey();  
    	for(Map.Entry<Integer, String[]> entryz:tempOfferMeta.entrySet()){
    		
    		app_included.add(entryz.getValue()[0]);
    		sites.add(entryz.getValue()[1]);
    		offer_comb.add(entryz.getKey());
    		System.out.println("@@@@@0 " + i + " " +  entryz.getKey());
    		System.out.println("@@@@@1 " + i + " " +  entryz.getValue()[0]);
    		System.out.println("@@@@@2 " + i + " " + entryz.getValue()[1]);
    		
    	  for(Map.Entry<Integer, String[]> entry:tempOfferMeta.entrySet()){
    		//int num = 0;
    		System.out.println("*****0 " + i + " " +  entry.getKey());
    		System.out.println("*****1 " + i + " " +  entry.getValue()[0]);
    		System.out.println("*****2 " + i + " " + entry.getValue()[1]);
    		//entry.getValue().equals()
    		//if (app.equalsIgnoreCase(entry.getValue()[0])){
    		//if(!entryz.getKey().equals(entry.getValue()[0])){
    		if(!app_included.contains(entry.getValue()[0]))	{
    			//app_included[i] = entry.getValue()[0];
    			//app = entry.getValue()[0];
    			//if(first_round){
    			//if(!entryz.getValue()[1].equalsIgnoreCase(entry.getValue()[1])){
    			if(!sites.contains(entry.getValue()[1])){
    			//if(!site.equalsIgnoreCase(entry.getValue()[1])){
    				System.out.println("$$$$$0 " + i + " " +  entry.getValue()[0]);
    				System.out.println("$$$$$1 " + i + " " + entry.getValue()[1]);
    				//app_included[i] = entry.getValue()[0]; 
    				//app = entry.getValue()[0];
    				//workers[i] = entry.getValue()[1];
    				
    				//site = entry.getValue()[1];
    				app_included.add(entry.getValue()[0]);
    				//offer_included.add(entry.getKey());
    				//offer_nos[j] = entry.getKey();
    				sites.add(entry.getValue()[1]);
    				
    				//to store offer number
    				offer_comb.add(entry.getKey());
    				int size = app_included.size();
    			    if(size == appResource.size()){
    			    	System.out.println("%%%%%%%%%%%%%% offer comb formed");
    			    	System.out.println("*****&&&&&included temp offer no: " + offer_comb.toString());
    			    	//Set<Integer> tempoffer_comb = new HashSet();
    			    	//tempoffer_comb = offer_comb;
    			    	offer_comb_s = offer_comb.toArray(new Integer[offer_comb.size()]);
    			    	offerCom.put(k, offer_comb_s);
    			    	//System.out.println("*****&&&&&included temp combined offer: " + offer_comb_s);
    			    	//System.out.println("*****&&&&&included temp combined offer: " + offerCom.get(k));
    			    	//System.out.println("*****&&&&&included temp combined offer: " + offerCom.get(k)[0]);
    			    	app_included.clear();
			    		sites.clear();
			    		offer_comb.clear();
			    		System.out.println("*****&&&&&included combined offer: " + offerCom.get(k));
			    		k++;
    			    	/*if (!OfferCom.containsValue(offer_nos)){
    			    	//for(int j = 0; j < 2; j++){
    			    	OfferCom.put(k, offer_nos); 
    			    	j = 0;
    			    	app_included.clear();
    			    	sites.clear();  			    
    			    }*/
    			    	//if combination is not duplicate
    			    	//offer_comb.
    			    	/*if(!offerCom.containsValue(offer_comb)){
    			    		System.out.println("no dupliate combination***");
    			    		offerCom.put(k, offer_comb);
    			    		System.out.println("*****included temp offer no: " + offer_comb);
    			    		app_included.clear();
    			    		sites.clear();
    			    		offer_comb.clear();
    			    		k++;
    			    	}
    			    	else{
    			    		offer_comb.clear();
    			    	}*/
    			    }
    				//offer_included[i] = entry.getKey();
    				//System.out.println("*****included temp offer no: " + entry.getKey());
    				//merge = true;
    				i++;
    				//first_round = true;
    			}
    			}
    			else{
    				
    			}
    		}
    	}
	        
    	//}
    	
/*    	 if (offer_included.isEmpty()){
    		  return false;
    	  }
    		  
    	}*/
    	//System.out.println("$$$$$ i value " + i);
    	int app_size = appResource.size();
    	//System.out.println("$$$$$ combination size " + (i-1) );
    	if(i >= app_size){
    		//System.out.println("$$$$$ offers combination " + offerCom.values());
    		//System.out.println("$$$$$ offers combination 1 " + offerCom.get(2));
    		//String offer_conts = "";
    		//to get temp-offer-no from offerCom first
    		//check type is grid/cloud for different string format
    		//offerCom = <key, Interger[]>, Integer[] is the temp offer numbers
    		for(Map.Entry<Integer, Integer[]> entry:offerCom.entrySet()){
    			//int no = entry.getKey();
    			String offer_conts = "";
    			System.out.println("@@@@@@@@@@@ entry value length: " + offerCom.size());
    			String workers = "";
    			String charge = "";
    			for(int h=0; h<offerCom.size(); h++){
    				//temp offer number
    				int temp_no = entry.getValue()[h];
    				String type = tempOfferMeta.get(temp_no)[2];
    				//int temp_offer_no = entry.getKey();
    			    //System.out.println("@@@@@@@@@@@" + offerCom.get(no).toString());
    			/*offer_conts = offers + offer_no + "- Provider: "+ provider + ", Site:" + tempOfferMeta.get(no)[1] + ", CPU Number: " +
    				tempOfferMeta.get(i)[3] + ", type: " + tempOfferMeta.get(i)[2] + ";";*/
    			    offer_conts = offer_conts + "App: " + tempOfferMeta.get(temp_no)[0] + ", Site: " + tempOfferMeta.get(temp_no)[1] + ", CPU Number: " +
        				tempOfferMeta.get(temp_no)[3] + ", type: " + type + ".";
    			    workers = workers + tempOfferMeta.get(temp_no)[1] + ",";
    			    // if type is cloud, to add cost value or also mem
    			    if(type.equalsIgnoreCase("cloud")){
    			    	charge = tempOfferMeta.get(temp_no)[4];
    			    }
    		        //System.out.println("@@@@@@@@@@@  contents:" + offer_conts);
    		    //offer_no++;
    			    //to add info into database
    			}
    			//System.out.println("@@@@@@@@@@@  workers:" + workers.substring(0, 45));
    			/*Service service = new Service();
		    	service.setProvider(provider);
		    	service.setService_name(workers.substring(0, 45));
		    	service.setCpuNo(cpuNo);
		    	service.setCharge(Double.parseDouble(charge));
		    	
		    	Offer offer = new Offer();
		
					//contract = new Contract();
		    	    offer.setId(offer_no);
					offer.setAppname(appInput);
					offer.setUsername(username);
					offer.setOwner(owner);
					offer.setGroupname(grpInput);
					offer.setService(service);
					offer.setStime(startT);
					//offer.setDuration(duration);
					offer.setStatus(NegState.Negotiating.toString());
					offer.setLevel(level);
	                offer.setWorker(workers.substring(0, 45));
					
					NegHibConnect.hibOffer(offer);*/
    			Random rand = new Random();
		   	    long offer_no = rand.nextInt(10000 + 1);
    			offers = offers + offer_no + "- Provider: "+ provider + ", " + offer_conts + ";";
    			offer_no++;
    		}
    		
    		System.out.println("$$$$$ " + offers);
    		/*System.out.println("$$$$$ 1st offers included contents " + 
    		tempOfferMeta.get(offer_included.get(0))[0] + "***" + tempOfferMeta.get(offer_included.get(0))[1]);
    		System.out.println("$$$$$ 2nd offers included contents " + 
    		tempOfferMeta.get(offer_included.get(1))[0] + "***" + tempOfferMeta.get(offer_included.get(1))[1]);*/
    		merge = true;
    	}
    	else{
    		merge = false;
    	}
    	//System.out.println("**************" + offers);
    	return merge;
    }
    
    public static String clusterSearch(String user, String app, String group, String share, String resource, int numjobs, int nefold, String deadline) throws OWLOntologyCreationException{
    	String offers="";
    	String temp_offers="";
    	//go through share ontology to look for satisfying resources 	
		final String ns = "http://www.owl-ontologies.com/alliance#";
		String file = mfile_path + share + ".owl";
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(file));
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		
		int end_index = share.length() - 5;
		String share_pre = share.substring(0, end_index);
		//String policy_file = "file:/opt/AHE3/ontology/" + share_pre + "Policy.owl";
		String policy_file = mfile_path + share_pre + "Policy.owl";
		OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		OWLOntology ontology2 = manager2.loadOntology(IRI.create(policy_file));
		
		//OWLClass serCla = manager.getOWLDataFactory().getOWLClass(IRI.create(ns + "Service"));
		//NodeSet<OWLNamedIndividual> serSet = reasoner.getInstances(serCla, false);
		
		//get user's level
		OWLNamedIndividual user_ind = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + user));
		OWLDataProperty  user_level_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "memberLevel"));
		int user_level = user_ind.getDataPropertyValues(user_level_pro, ontology2).iterator().next().parseInteger();
		
		//to get user's balance in MappingPolicy, already checked with 
		//this is attained in accessCheck: user_cpuT_value

		
		OWLDataProperty  ser_level_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "serviceLevel"));
		OWLObjectProperty  hasExen = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasExeEnvironment"));
		OWLDataProperty  ser_cpu_t_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "cpuTime"));
		OWLDataProperty  meas_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "measurement"));
		OWLDataProperty  url_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "url"));
		String ecpuNo = "physicalCpus";
		OWLDataProperty  RcpuNo = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + ecpuNo));
		int service_level;
		int aCpuNo;
		String endpoint;
		double ser_cpu_t;
		
		Random rand1 = new Random();
   	    long neg_id = rand1.nextInt(10000 + 1);
		
		OWLNamedIndividual service_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + resource));
		
		OWLObjectProperty  hasEnd = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasEndpoint"));
		
		service_level = service_ind.getDataPropertyValues(ser_level_pro, ontology).iterator().next().parseInteger();
		System.out.println("+++++++++++++ service level in OntReasoning: " + service_level);
		if(user_level >= service_level){
			//to check if service's balance is enough
			OWLIndividual end_ind = service_ind.getObjectPropertyValues(hasExen, ontology).iterator().next();
			ser_cpu_t = end_ind.getDataPropertyValues(ser_cpu_t_pro, ontology).iterator().next().parseDouble();
			//if required cpu time is smaller than service remainging cpu time; if required cpu time is smaller than user's cpu time balance
			//System.out.println("******* clusterSearch, maxTotalCpuT: " + maxTotalCpuT);
			if(totalCpuT <= ser_cpu_t && totalCpuT <= user_cpuT_value){
				   workerN = end_ind.toStringID().substring(TRANCATION);
		    	 //*****System.out.println("This provider can provide required resources: " + provider);
		    	   System.out.println("work node: " + workerN);
		    	   //String measurement = measSets.iterator().next().getLiteral();
		    	   Set<OWLLiteral> cpuNoSets = end_ind.getDataPropertyValues(RcpuNo, ontology);
		    	   aCpuNo = Integer.parseInt(cpuNoSets.iterator().next().getLiteral());
		    	   //max_duration = maxTotalCpuT/aCpuNo;
		    	   
		    	   Set<OWLLiteral> measSets = end_ind.getDataPropertyValues(meas_pro, ontology);
		    	   String measurement = measSets.iterator().next().getLiteral();
		    	   		    	   
		    	   endpoint = end_ind.getObjectPropertyValues(hasEnd, ontology).iterator().next().getDataPropertyValues(url_pro, ontology).iterator().next().getLiteral();
		    	   
		    	   System.out.println("******* clusterSearch, endpoint in offer" + endpoint);
		    	   System.out.println("******* clusterSearch, max total cpu timer in offer" + maxTotalCpuT);
		    	   System.out.println("******* clusterSearch, max duration in offer" + max_duration);
		    	   

		    	   Random rand = new Random();
		   	       long offer_no = rand.nextInt(10000 + 1);
		   	    /*Service service = new Service();
		    	   service.setProvider("UoM_cluster");
		    	   //service.setService_name(workerN);
		    	   service.setCpuNo(aCpuNo);
		    	   //service.setCharge(charge);
		    	   //service.setCost(cost);
		    	   service.setMeasurement(measurement);
		    	   //service.setMemory(mem);
		    	
		    	   Offer offer = new Offer();
		
					//contract = new Contract();
		    	    offer.setId(offer_no);
		    	    offer.setNegId(neg_id);
		    	    //offer.setJobId(job_id);
					offer.setAppname(app);
					offer.setUsername(user);
					offer.setOwner(owner);
					offer.setGroupname(group);
					offer.setService(service);
					//offer.setStartTime(startT);
					offer.setMaxTotalCpuT(maxTotalCpuT);
					//offer.setMaxCost(userMaxCost_value);
					//offer.setMaxDuration(max_duration);
					offer.setStatus(NegState.Initiating.toString());
					offer.setLevel(service_level);
					offer.setOverPrivilege(1);
					offer.setWorker(workerN);
					offer.setShare(share);
					offer.setRequiredCpuT(totalCpuT);
					//offer.setRequiredCpuNo(requiredCpuN);
					//offer.setUserBalance(user_balance_value);
					offer.setEndpoint(endpoint);
					offer.setSub("");
					offer.setNefold(nefold);
					offer.setNumjobs(numjobs);
					offer.setDeadline(deadline);
					
					NegHibConnect.hibOffer(offer);*/
					String offer_contents = "=" + endpoint + ";";
				/*String offer_contents = "- Provider: "+ provider + ", worker: " +
		        		workerN + ", CPU Number: " +
					aCpuNo + ", required CPU Number: " +
					requiredCpuN +  ", Mem: " + mem + ", unit cost: "+ charge + ", type: Cloud;";*/
                offers = offers + offer_no + offer_contents;
			}
		}
		else{
			return "";
			//OWLNamedIndividual super_service = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + resource));
			//to check if large service's balance is enough in Share
			/*OWLIndividual end_ind = service_ind.getObjectPropertyValues(hasExen, ontology).iterator().next();
			ser_cpu_t = end_ind.getDataPropertyValues(ser_cpu_t_pro, ontology).iterator().next().parseDouble();
			
			//to check in MappingPolicy, if super has enough balance
			OWLNamedIndividual super_ind = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "super"));
			long super_cpuT_value = (long) super_ind.getDataPropertyValues(ser_cpu_t_pro, ontology2).iterator().next().parseDouble();
			//if required cpu time is smaller than service remainging cpu time; if required cpu time is smaller than user's cpu time balance
			//System.out.println("******* clusterSearch, maxTotalCpuT: " + maxTotalCpuT);
			if(totalCpuT <= ser_cpu_t && totalCpuT <= super_cpuT_value && totalCpuT <= maxTotalCpuT){
				   workerN = end_ind.toStringID().substring(TRANCATION);
		    	 //*****System.out.println("This provider can provide required resources: " + provider);
		    	   System.out.println("work node: " + workerN);
		    	   
		    	   Set<OWLLiteral> cpuNoSets = end_ind.getDataPropertyValues(RcpuNo, ontology);
		    	   Set<OWLLiteral> measSets = end_ind.getDataPropertyValues(meas_pro, ontology);
		    	   String measurement = measSets.iterator().next().getLiteral();
		    	   
		    	   aCpuNo = Integer.parseInt(cpuNoSets.iterator().next().getLiteral());
		    	   max_duration = maxTotalCpuT/aCpuNo;
		    	   		    	   
		    	   endpoint = end_ind.getObjectPropertyValues(hasEnd, ontology).iterator().next().getDataPropertyValues(url_pro, ontology).iterator().next().getLiteral();
		    	   
		    	   System.out.println("******* clusterSearch, endpoint in offer" + endpoint);
		    	   System.out.println("******* clusterSearch, max total cpu timer in offer" + maxTotalCpuT);
		    	   System.out.println("******* clusterSearch, max duration in offer" + max_duration);
		    	   
		    	   Random rand = new Random();
		   	       long offer_no = rand.nextInt(10000 + 1);
		   	       Service service = new Service();
		    	   service.setProvider(provider);
		    	   //service.setService_name(workerN);
		    	   service.setCpuNo(aCpuNo);
		    	   //service.setCharge(charge);
		    	   //service.setCost(cost);
		    	   service.setMeasurement(measurement);
		    	   //service.setMemory(mem);
		    	
		    	   Offer offer = new Offer();
		
					//contract = new Contract();
		    	    offer.setId(offer_no);
		    	    offer.setNegId(neg_id);
		    	    //offer.setJobId(job_id);
					offer.setAppname(app);
					offer.setUsername(user);
					offer.setOwner(owner);
					offer.setGroupname(group);
					offer.setService(service);
					//offer.setStartTime(startT);
					offer.setMaxTotalCpuT(maxTotalCpuT);
					//offer.setMaxCost(userMaxCost_value);
					offer.setMaxDuration(max_duration);
					offer.setStatus(NegState.Initiating.toString());
					offer.setLevel(service_level);
					offer.setOverPrivilege(0);
					offer.setWorker(workerN);
					offer.setShare(share);
					offer.setRequiredCpuT(totalCpuT);
					//offer.setRequiredCpuNo(requiredCpuN);
					//offer.setUserBalance(user_balance_value);
					offer.setEndpoint(endpoint);
					offer.setSub("");
					
					NegHibConnect.hibOffer(offer);
					String offer_contents = "=" + endpoint + "^";
				String offer_contents = "- Provider: "+ provider + ", worker: " +
		        		workerN + ", CPU Number: " +
					aCpuNo + ", required CPU Number: " +
					requiredCpuN +  ", Mem: " + mem + ", unit cost: "+ charge + ", type: Cloud;";
                offers = offers + offer_no + offer_contents;
			}*/
		}
/*		for(Node<OWLNamedIndividual> serInd : serSet) {
			OWLNamedIndividual service = serInd.getRepresentativeElement();
			service_level = service.getDataPropertyValues(ser_level_pro, ontology).iterator().next().parseInteger();
			if(user_level >= service_level){
				//to check if service's balance is enough
				OWLIndividual end_ind = service.getObjectPropertyValues(hasExen, ontology).iterator().next();
				double ser_cpu_t = end_ind.getDataPropertyValues(ser_cpu_t_pro, ontology).iterator().next().parseDouble();
				//if required cpu time is smaller than service remainging cpu time; if required cpu time is smaller than user's cpu time balance
				//System.out.println("******* clusterSearch, maxTotalCpuT: " + maxTotalCpuT);
				if(totalCpuT <= ser_cpu_t && totalCpuT <= user_cpuT_value && totalCpuT <= maxTotalCpuT){
					   workerN = end_ind.toStringID().substring(TRANCATION);
			    	 //*****System.out.println("This provider can provide required resources: " + provider);
			    	   System.out.println("work node: " + workerN);
			    	   //String measurement = measSets.iterator().next().getLiteral();
			    	   Set<OWLLiteral> cpuNoSets = end_ind.getDataPropertyValues(RcpuNo, ontology);
			    	   aCpuNo = Integer.parseInt(cpuNoSets.iterator().next().getLiteral());
			    	   max_duration = maxTotalCpuT/aCpuNo;
			    	   		    	   
			    	   endpoint = end_ind.getDataPropertyValues(url_pro, ontology).iterator().next().getLiteral();
			    	   
			    	   System.out.println("******* clusterSearch, endpoint in offer" + endpoint);
			    	   System.out.println("******* clusterSearch, max total cpu timer in offer" + maxTotalCpuT);
			    	   System.out.println("******* clusterSearch, max duration in offer" + max_duration);
				}
			}
		}*/
		
    	
    	return offers;
    }
    
    // this method seach for existing un-contracted offers for satisfying resources 
    public static String getAlternative() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
    	Connection con;
		//Statement sta;
		String url = "jdbc:mysql://localhost/ahecontract";
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    con = (Connection) DriverManager.getConnection(url, "root", "sophia");
	    
	    Statement sta = (Statement) con.createStatement();
		//String user = "Junyi";
		//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
		ResultSet rs = sta.executeQuery("SELECT * FROM OFFER");
		
		while (rs.next()){
			String provider_t = rs.getString("provider");
			double duration_t = Double.parseDouble(rs.getString("duration"));
			//int level_t = rs.getInt("level");
			int level_t = Integer.parseInt(rs.getString("level"));
			if (provider_t.equalsIgnoreCase(provider) && duration_t >= duration
					&& level_t < level){
			    long offer_id = rs.getLong("id");
				String status = rs.getString("status");
				if(status.equalsIgnoreCase("Negotiating")){
					//to change selected offer's status
					String s = "ReqTerminated";
					String query = "UPDATE OFFER SET status = '"+ s + "' WHERE ID = '" + offer_id + "';";
					System.out.println(query);
					PreparedStatement preparedStmt = con.prepareStatement(query);
					preparedStmt.executeUpdate();
					
					Service service = new Service();
			    	service.setProvider(provider);
			    	//service.setService_name(workerN);
			    	service.setCpuNo(cpuNo);
			    	
			    	long offer_no = 2;
			    	Offer offer = new Offer();
			
						//contract = new Contract();
			    	    offer.setId(offer_no);
						offer.setAppname(appInput);
						offer.setUsername(username);
						offer.setOwner(owner);
						offer.setGroupname(grpInput);
						offer.setService(service);
						offer.setStartTime(startT);
						//offer.setDuration(duration);
						offer.setStatus(NegState.Negotiation.toString());
						offer.setLevel(level);
		                offer.setWorker(workerN);
						
						NegHibConnect.hibOffer(offer);
						
						String offers = offer_no + "- Provider: " + provider +  ", CPU Number: " +
								cpuNo + ", Type: Grid;";
						
						sta.close();
						
						return offers;
				}
			}
		}
    	return "none";
    }
    
    //this method search in a exe env for if it can meet one of the apps' demands
    //to compare exe env's available resources with apps' required resources
    //appResource: the app's required resource meta data, [0]cpuN,[1]cpuT
    //parameters: available resources in exe env
    public static boolean separateGridSearch(int cpuN, long cpuT){
    	// to search for both sub-apps
    	//System.out.println("@@@@@@@");
    	boolean result = false;
    	 for(Map.Entry<String, String[]> entry:appResource.entrySet()){
    		 //System.out.println(entry.getValue()[0] + "@@@@@@" + entry.getValue()[1]);
    		 int rCpuNo = Integer.parseInt(entry.getValue()[0]);
    		 int rCpuT = Integer.parseInt(entry.getValue()[1]);
    		 if(rCpuNo <= cpuN && rCpuT <= cpuT){
		       			/* System.out.println("inside separateGridSearch");
		       			 System.out.println("execution enviroment available cpu no: " + cpuN + "; cpu time: " + cpuT);
		       			 System.out.println(entry.getKey() + " app requireed cpu no: " + rCpuNo + "; cpu time: " + rCpuT);
				    	 //provider = share.getObjectPropertyValues(hasProDomain, ontology).iterator().next().toStringID().substring(TRANCATION);
				    	 //workerN = exeEn.toStringID().substring(TRANCATION);
				    	 System.out.println("This provider can provide required resources: " + provider);
				    	 //System.out.println("work node: " + workerN);
				    	 
				    	 //finalRes = true;
				    	 
					    	Service service = new Service();
					    	service.setProvider(provider);
					    	service.setService_name(workerN);
					    	service.setCpuNo(cpuNo);
					    	
					    	Offer offer = new Offer();
					
								//contract = new Contract();
					    	    offer.setId(offer_no);
								offer.setAppname(appInput);
								offer.setUsername(username);
								offer.setOwner(owner);
								offer.setGroupname(grpInput);
								offer.setService(service);
								offer.setStime(startT);
								//offer.setDuration(duration);
								offer.setStatus(NegState.Negotiating.toString());
								offer.setLevel(level);
					            offer.setWoker(workerN);
								
								NegHibConnect.hibOffer(offer);
							
								temp_offers = temp_offers + temp_offer_no + "- Provider: " + provider +  ", CPU Number: " +
								cpuNo + ", Type: Grid;";
				    	        //offers = offers + offer_no + "CPU Number: " +
						    		//	 entry.getValue()[0] + ", app_name: " + entry.getKey() + ", Type: Grid.";
				    	 String offer_meta = "CPU Number: " +
				    			 entry.getValue()[0] + ", app_name: " + entry.getKey() + ", Type: Grid.";
								String offer_meta = "CPU Number: " +
									cpuNo + ", app_name: " + entry.getKey() + ", Type: Grid, ";
								
								offer_meta[0] = entry.getKey();
								// this part should be included in separateGridSearch
					    		workerN = exeEn.toStringID().substring(TRANCATION);
					    		//tempOfferMeta.put(temp_offer_no, value)
					    		offer_meta[1] = workerN;
					    		tempOfferMeta.put(temp_offer_no, offer_meta);

					    		//String meta = tempOfferMeta.get(temp_offer_no);
					    		//meta.concat(str)
					    		System.out.println("hhhhhhh " + temp_offer_no + offer_meta[0] + "***" + offer_meta[1]);
					    		System.out.println("hhhhhhhhhh " + tempOfferMeta.get(temp_offer_no).toString());
					    		temp_offer_no++;
 
								//offers = offers + offer_no + ","+ provider + ", Grid;";
								//offers.put(offer_no, provider + "Grid");
								//appOffer.put((int) temp_offer_no, entry.getKey());
								tempOffers.put(temp_offer_no, temp_offers);
								tempOfferMeta.put(temp_offer_no, offer_meta);
								//temp_offer_no++;
								System.out.println("temp offer: " + temp_offers);*/
								//System.out.println("temp offer meta: " + tempOfferMeta);
								result = true;		 
		       	 }
		    		//System.out.println("total offer number:" + (temp_offer_no-1));
    			
    		 }
    	 return result;
    	 }
    
    public static boolean separateCloudSearch(int cpuN, double cost, double balance){
    	// to search for both sub-apps
    	boolean result = false;
    	 for(Map.Entry<String, String[]> entry:appResource.entrySet()){
    		 if(Integer.parseInt(entry.getValue()[0]) >= cpuN && 
    				 balance >= cost){
    			 result = true;
    		 }
    	 }
    		
    	return result;
    }
    
    public String getClusterOffer(String appName){
    	String offer = "";
    	
    	return offer;
    }
    
   /* public static String getOffersFromShares(String name, String group, String app) throws OWLOntologyCreationException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
    	
    	//to go through all shares obtained by this group
    	String[] shares = RestAPI.app_shares.get(app);
    	String offers = "";
    	for(String share:shares){
    		if (accessCheck(name, group, app, share)){
    		    offers = offers + resourceSearch(name, group, app, share);
    		}
    	}
    	return offers;
    	
    }
    */
    static long duration_t = 0;
public static String getOffersFromShares(String name, String group, String app, int core, long duration, String payment, long contract_id) throws OWLOntologyCreationException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException{
    	
    	//to go through all shares obtained by this group
    	String[] shares = RestAPI.app_shares.get(app);
    	System.out.println("************shares for watersteering: " + RestAPI.app_shares.get(app)[0]);
    	String offers = "";
    	/*long start_time;
    	long end_time;
    	long duration_m;*/
    	
    	
    	for(String share:shares){
    		//start_time = System.currentTimeMillis();
    		if (accessCheck(name, group, app, share, core, duration)){
    		    offers = offers + resourceSearch(name, group, app, share, payment, core, 0);
    		}
    		/*end_time = System.currentTimeMillis();
    		duration_m = end_time - start_time;
    		duration_t = duration_t + duration_m;
    		
    		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/Users/zeqianmeng/Desktop/eva_out.txt", true)));
    	    out.println(duration_m);
    	    out.println(duration_t);
    	    out.println();
    	    out.close();*/
    	}
    	return offers;
    	
    }
public static String getOffersFromSharesComp(String name, String group, String app, String resource, int numjobs, int nefold, String deadline) throws OWLOntologyCreationException{
	String offers = "";
	//to go through all shares obtained by this group
	String[] shares = RestAPI.app_shares.get(app);
	for(String share:shares){
		//start_time = System.currentTimeMillis();
		if (accessCheck(name, group, app, share, 0, 0)){
		    offers = offers + clusterSearch(name, group, app, share, resource, numjobs, nefold, deadline);
		}
	}
	return offers;
}

public static String getOffersFromOtherShares(String name, String group, String app, int core, int duration, String payment, long contract_id, String e_share) throws OWLOntologyCreationException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException{
	
	//to go through all shares obtained by this group
	String[] shares = RestAPI.app_shares.get(app);
	System.out.println("************shares for watersteering: " + RestAPI.app_shares.get(app)[0]);
	String offers = "";
	/*long start_time;
	long end_time;
	long duration_m;*/
	
	
	for(String share:shares){
		//start_time = System.currentTimeMillis();
		if(!share.equalsIgnoreCase(e_share)){
		    if (accessCheck(name, group, app, share, core, duration)){
		        offers = offers + resourceSearch(name, group, app, share, payment, core, 0);
		    }
		}
		else{
			return "no offers found";
		}
		/*end_time = System.currentTimeMillis();
		duration_m = end_time - start_time;
		duration_t = duration_t + duration_m;
		
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/Users/zeqianmeng/Desktop/eva_out.txt", true)));
	    out.println(duration_m);
	    out.println(duration_t);
	    out.println();
	    out.close();*/
	}
	return offers;
	
}

/*    public static void getMeta(){
    	
    	provider = share.getObjectPropertyValues(hasProDomain, ontology).iterator().next().toStringID().substring(TRANCATION);
 	    workerN = exeEn.toStringID().substring(TRANCATION);
 	    System.out.println("This provider can provide required resources: " + provider);
 	    System.out.println("work node: " + workerN);

 	    finalRes = true;
 	
 	    Service service = new Service();
 	    service.setProvider(provider);
 	    service.setService_name(workerN);
 	    service.setCpuNo(cpuNo);
 	
 	    Offer offer = new Offer();

			//contract = new Contract();
 	    offer.setId(offer_no);
			offer.setAppname(appInput);
			offer.setUsername(username);
			offer.setOwner(owner);
			offer.setGroupname(grpInput);
			offer.setService(service);
			offer.setStime(startT);
			offer.setDuration(duration);
			offer.setStatus(NegState.Initiating.toString());
			
			NegHibConnect.hibOffer(offer);
			
			offers = offers + offer_no + "- Provider: " + ", CPU Number: " +
			cpuNo + ", Type: Grid;";
    }*/
    
	public static String getOwner(){
		return owner;
	}
	
	public static String getProvider(){
		return provider;
	}
	
	/*public static int getUserLevel(String app, String user) throws OWLOntologyCreationException{
		int level = 0;
		
		String ns = "http://www.owl-ontologies.com/alliance#";
		//String ns1 = "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#";
		String policy = RestAPI.share_policy.get(RestAPI.app_shares.get(app)[0]);
		String file = mfile_path + policy + ".owl";
		//String file = "file:/Users/zeqianmeng/Desktop/ontology/mPolicy.owl";
		//System.out.println("Reading SHARE file " + file + "...");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(file));
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		
		OWLNamedIndividual user_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + user));
		
		return level;
	}*/
	
	public static String getBalance(String user_name, String group) throws OWLOntologyCreationException{
		String ns = "http://www.owl-ontologies.com/alliance#";
		//String ns1 = "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#";
		String file = "file:/opt/AHE3/ontology/mPolicy.owl";
		//String file = "file:/Users/zeqianmeng/Desktop/ontology/mPolicy.owl";
		//System.out.println("Reading SHARE file " + file + "...");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(file));
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		
		String cpuT_p_s = "CpuTime";
		String balance_p_s = "balance";
		
		String balance = "", cpu = "";
		
		String result = "error";
		
		OWLDataProperty  RcpuT = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + cpuT_p_s));
		OWLDataProperty  balancePro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + balance_p_s));
		OWLNamedIndividual share_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "CloudShare"));
		//OWLNamedIndividual share_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "ClusterShare"));
		OWLObjectProperty  hasUserDomain = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasUserDomain"));
		OWLNamedIndividual group_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + group));		
		
		if (share_ind.getObjectPropertyValues(hasUserDomain, ontology).contains(group_ind)){
				
			OWLNamedIndividual people_ind = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + user_name));
			OWLObjectProperty  hasMember = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasMember"));
			
		    if (group_ind.getObjectPropertyValues(hasMember, ontology).contains(people_ind)){
		    	balance = people_ind.getDataPropertyValues(balancePro, ontology).iterator().next().getLiteral();
		    	cpu = people_ind.getDataPropertyValues(RcpuT, ontology).iterator().next().getLiteral();
		    	result = "balace: " + balance + "; remainsing cpu time: " + cpu;
		    }
		}
		else {
			result = "Information does not match.";
		}
		System.out.println(result);
		return result;
	}
	/*public static void main(String[] args) throws OWLOntologyCreationException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, OWLOntologyStorageException, InterruptedException, IOException {
		//System.out.println(accessCheck("Sofia", "ManGroup", "CompSteering", "ClusterShare", 0, 0));
		String offers = clusterSearch("Sofia", "ManGroup", "CompSteering", "ClusterShare", "super", 1, 1, "");
		
		//System.out.println(accessCheck("Sofia", "ManGroup", "WaterSteering", "CloudShare", 2, 0));
		//String offers = resourceSearch("Sofia", "ManGroup", "WaterSteering", "CloudShare", "asap", 2, 0);
		//System.out.println(accessCheck("Sofia", "ManGroup", "ResvApp", "ResvShare", 0, 0));
	    //String offers = resourceSearch("Sofia", "ManGroup", "ResvApp", "ResvShare", true);
		int i;
		long end_time;
		long start_time;
		long duration = 0;
		long duration_t = 0;
		String offers = null;
		
		
		File file = new File("/Users/zeqianmeng/Desktop/eva_out.txt");
		file.createNewFile();
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
	//initial test
		for(i = 0; i <= 99; i++){
			
			start_time = System.currentTimeMillis();
			
	        if(accessCheck("Sofia", "ManGroup", "ResvApp", "ResvShare", 2, 0)){
		        offers = resourceSearch("Sofia", "ManGroup", "ResvApp", "ResvShare");
		    //}

	        end_time = System.currentTimeMillis();
	        duration = end_time - start_time;
    		duration_t = duration_t + duration;
	        
    		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/Users/zeqianmeng/Desktop/eva_out.txt", true)));
    	    out.println(i + "  " + duration);
    	    out.println(duration_t);
    	    out.println();
    	    out.close();
    	    System.out.println(offers);
    	    try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
		}
		}  
		
		//for test with different member
        for(i = 10; i <= 19; i++){
			
			start_time = System.currentTimeMillis();
			
	        if(accessCheck("Junyi", "ManGroup", "ResvApp", "ResvShare", 0, 0)){
		        offers = resourceSearch("Junyi", "ManGroup", "ResvApp", "ResvShare");
		    //}

	        end_time = System.currentTimeMillis();
	        duration = end_time - start_time;
    		duration_t = duration_t + duration;
	        
    		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/Users/zeqianmeng/Desktop/eva_out.txt", true)));
    	    out.println(i + "  " + duration);
    	    out.println(duration_t);
    	    out.println();
    	    out.close();
    	    System.out.println(offers);
    	    try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
		}
		}  
        
        for(i = 20; i <= 29; i++){
			
			start_time = System.currentTimeMillis();
			
	        if(accessCheck("Sofia", "ManGroup", "WaterSteering", "CloudShare", 1, 0)){
			String offers = resourceSearch("Sofia", "ManGroup", "WaterSteering", "CloudShare");
		    //}

	        end_time = System.currentTimeMillis();
	        duration = end_time - start_time;
    		duration_t = duration_t + duration;
	        
    		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/Users/zeqianmeng/Desktop/eva_out.txt", true)));
    	    out.println(i + "  " + duration);
    	    out.println(duration_t);
    	    out.println();
    	    out.close();
    	    System.out.println(offers);
    	    try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
		}
		}  
            //String newLine = System.getProperty("line.separator");
	        
	        //bw.write(1 + " " + duration + newLine + newLine);
	        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/Users/zeqianmeng/Desktop/eva_out.txt", true)));
	        out.println(duration);
    	    out.println();
    		        
	        
		
		
		//bw.close();		
		//System.out.println("Done");
		//System.out.println(accessCheck("Sofia", "ManGroup", "WaterSteering", "ClusterShare"));
		//String offers = resourceSearch("Sofia", "ManGroup", "WaterSteering", "ClusterShare");
		//cloudProviderSearch(getAppInfo());
		//ServiceReasoning.providerSearch(getAppInfo());
		//System.out.println("@@@@@@@@@@" + appResource.get("visLib")[1]);
		//System.out.println("@@@@@@@@@@" + offers);
		//getBalance("Sofia", "ManGroup");
		//shareCloudReduce(5);
		//policyCloudReduce("Sofia", "ResvPolicy", 5);
		System.out.println(offers);
		//System.out.println("@@@@@@@@@@" + getAppInfo("ResvShare"));
		//String[] resource = new String[5]; 
		//String[] resource = getAppInfo("ResvShare");
		//System.out.println("@@@@@@@@@@" + resource[0]);
		//System.out.println("=======" + getAppInfo("ResvShare")[0]);
		//System.out.println("******" + ServiceReasoning.providerSearch(resource, "ResvPolicy"));
		System.out.println("@@@@@@@@@@ done");
		
	}*/
	
	public static void policyGridReduce(String name, String grp, String app) throws OWLOntologyStorageException, OWLOntologyCreationException{
		String mfile = "file:/opt/AHE3/ontology/mPolicy.owl";
		//String mfile = "file:/Users/zeqianmeng/Desktop/ontology/mPolicy.owl";
		OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		OWLOntology ontology2 = manager2.loadOntology(IRI.create(mfile));
		String ns = "http://www.owl-ontologies.com/alliance#";
		
		PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
		//System.out.println("part2 done.");
		
		reasoner2.getKB().realize();
		
		//username = name;
		int URemCpuNo, URemCpuTime;
		
		//String ecpu = "PhysicalCPUs";
		String ecpu = "CpuTime";
		
		double totalAvaCpu;
		
		OWLDataProperty  Avalcpu = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + ecpu));
		//OWLDataProperty  Avalmem = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + emem));
		OWLNamedIndividual userAPol = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + name));
		
		//URemCpuNo = userAPol.getDataPropertyValues(Avalmem, ontology2).iterator().next().parseInteger();
		URemCpuTime = userAPol.getDataPropertyValues(Avalcpu, ontology2).iterator().next().parseInteger();
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLNamedIndividual userA = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + name));
		
		
		//int memNo = Integer.parseInt(appInds.getObjectPropertyValues(hasApp, ontology).iterator().next().getDataPropertyValues(hasRmem, ontology).iterator().next().getLiteral());
	    //System.out.println("Memory: " + memNo);
	    //int cpuTime = Integer.parseInt(appInds.getObjectPropertyValues(hasApp, ontology).iterator().next().getDataPropertyValues(hasRcpu, ontology).iterator().next().getLiteral());
		//user remaining resources after negotiation
		//int newURMem = URemMemNo - memNo;
		//int newURCpu = URemCpuNo - cpuTime;
	
		totalAvaCpu = cpuNo * cpuTime;
		//int newURCpuNo = URemCpuNo - cpuNo;
		double newURCpuTime = URemCpuTime - totalCpuT;
		
		
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
		OWLDataPropertyAssertionAxiom dpass = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(Avalmem, userA, newURCpuNo);
		AddAxiom change = new AddAxiom(ontology2, dpass);
        manager2.applyChange(change);
        manager2.saveOntology(ontology2);
        
        System.out.println("user's remaining cpu time: " + userAPol.getDataPropertyValues(Avalmem, ontology2).iterator().next().parseInteger());
        */
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
		OWLDataPropertyAssertionAxiom dpass2 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(Avalcpu, userA, newURCpuTime);
		AddAxiom change2 = new AddAxiom(ontology2, dpass2);
        manager2.applyChange(change2);
        manager2.saveOntology(ontology2);
        
        System.out.println("user's remaining cpu time: " + userAPol.getDataPropertyValues(Avalcpu, ontology2).iterator().next().parseInteger());
		//}
		
		System.out.println("*************PART4 DONE");
		
	}
	
	public static void policyCloudReduce(String user, String policy_n, double cost) throws OWLOntologyStorageException, OWLOntologyCreationException{
		//String mfile = "file:/opt/AHE3/ontology/mPolicy.owl";
		//String mfile = "file:/Users/zeqianmeng/Desktop/ontology/mPolicy.owl";
		String mfile = mfile_path + policy_n + ".owl";
		OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		OWLOntology ontology2 = manager2.loadOntology(IRI.create(mfile));
		String ns = "http://www.owl-ontologies.com/alliance#";
		
		PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
		//System.out.println("part2 done.");
		
		reasoner2.getKB().realize();
		
		String bal = "balance";
		double aBal;
		
		//int totalAvaCpu;
		
		OWLDataProperty  Avalbal = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + bal));
		//OWLDataProperty  Avalmem = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + emem));
		OWLNamedIndividual user_ind = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + user));
		
		//URemCpuNo = userAPol.getDataPropertyValues(Avalmem, ontology2).iterator().next().parseInteger();
		aBal = user_ind.getDataPropertyValues(Avalbal, ontology2).iterator().next().parseDouble();
		

		double newBal = aBal - cost;
		

        //change info in user's available balance in policy
        OWLEntityRemover remover2 = new OWLEntityRemover(manager2, Collections.singleton(ontology2));
		//userA.getDataPropertyValues(Rmem, ontology).clear();
        Avalbal.accept(remover2);
		//userA.getDataPropertyValues(Rmem, ontology).add(memLit);
		//remover.visit(userA);
		//OWLIndividualVisitor vis = null;
		//Rmem.accept(remover);
		//userA.accept(vis);
		manager2.applyChanges(remover2.getChanges());
        remover2.reset();
        
        manager2.saveOntology(ontology2);
		OWLDataPropertyAssertionAxiom dpass2 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(Avalbal, user_ind, newBal);
		AddAxiom change2 = new AddAxiom(ontology2, dpass2);
        manager2.applyChange(change2);
        manager2.saveOntology(ontology2);
        
        System.out.println("user's updated balance: " + user_ind.getDataPropertyValues(Avalbal, ontology2).iterator().next().parseDouble());
		//}
		
		System.out.println("*************PART4 DONE");
		
	}
	
	public static void shareCloudReduce(long con_id) throws OWLOntologyStorageException, OWLOntologyCreationException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		//String mfile = "file:/opt/AHE3/ontology/mPolicy.owl";
		
		/*String[] data = new String[3];
		data = NegotiationDB.getContCloudRed(con_id);
		String worker = data[0];
		double cost = Double.parseDouble(data[1]);
		String share = data[2];
		
		String mfile = "file:/Users/zeqianmeng/Desktop/ontology/" + share + ".owl";*/
		String mfile = "file:/Users/zeqianmeng/Desktop/ontology/ResvShare.owl";
		//String mfile = mfile_path + policy_n + ".owl";
		OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		OWLOntology ontology2 = manager2.loadOntology(IRI.create(mfile));
		String ns = "http://www.owl-ontologies.com/alliance#";
		
		PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
		//System.out.println("part2 done.");
		
		reasoner2.getKB().realize();
		
		//String[] data = new String[2];
		//data[0] = "amazon.t2.micro";
		//data[1] = "5";
		
		//data = NegotiationDB.getContCloudRed(con_id);
		String worker = "amazon.t2.micro";
		double cost = 5;
		
		String bal = "balance";
		
		double aBal;
		
		//int totalAvaCpu;
		
		OWLDataProperty  Avalbal = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + bal));
		//OWLDataProperty  Avalmem = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + emem));
		OWLNamedIndividual exe_ind = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + worker));
		
		//URemCpuNo = userAPol.getDataPropertyValues(Avalmem, ontology2).iterator().next().parseInteger();
		aBal = exe_ind.getDataPropertyValues(Avalbal, ontology2).iterator().next().parseDouble();
		

		double newBal = aBal - cost;
		

        //change info in user's available balance in policy
        OWLEntityRemover remover2 = new OWLEntityRemover(manager2, Collections.singleton(ontology2));
		//userA.getDataPropertyValues(Rmem, ontology).clear();
        Avalbal.accept(remover2);
		//userA.getDataPropertyValues(Rmem, ontology).add(memLit);
		//remover.visit(userA);
		//OWLIndividualVisitor vis = null;
		//Rmem.accept(remover);
		//userA.accept(vis);
		manager2.applyChanges(remover2.getChanges());
        remover2.reset();
        
        manager2.saveOntology(ontology2);
		OWLDataPropertyAssertionAxiom dpass2 = manager2.getOWLDataFactory().getOWLDataPropertyAssertionAxiom(Avalbal, exe_ind, newBal);
		AddAxiom change2 = new AddAxiom(ontology2, dpass2);
        manager2.applyChange(change2);
        manager2.saveOntology(ontology2);
        
        System.out.println("share's updated balance: " + exe_ind.getDataPropertyValues(Avalbal, ontology2).iterator().next().parseDouble());
		//}
		
		System.out.println("*************PART5 DONE");
		
	}
	
	
	//public final static void main(String[] args) throws OWLOntologyStorageException, OWLOntologyCreationException{
	public static HashMap<String, String> getAppInfo() throws OWLOntologyCreationException{
		
		String ns = "http://www.owl-ontologies.com/alliance#";
		//String ns1 = "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#";
		//String file = "file:/opt/AHE3/ontology/ResvShare.owl";
		String file = "file:/Users/zeqianmeng/Desktop/ontology/ResvShare.owl";
		//System.out.print("Reading SHARE file " + file + "...");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(file));
		//OWLDataFactory factory = manager.getOWLDataFactory();
		
		//OWLOntologyManager pManager = OWLManager.createOWLOntologyManager();
		//OWLOntology pOntology = manager.loadOntology(IRI.create(policyF));		
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		//System.out.println("part1 done.");
		
		reasoner.getKB().realize();
		
		OWLObjectProperty  hasAppEn = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasAppEnvironment"));
		OWLObjectProperty  hasExeEn = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasExeEnvironment"));
		OWLObjectProperty  hasOS = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasOSFamily"));
		OWLObjectProperty  hasRes = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasResource"));
		OWLObjectProperty  hasCpuM = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasCPUModel"));
		OWLDataProperty  cpuSpeed = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "clockSpeed"));
		System.out.println(share_ind.toStringID());
		app_os = share_ind.getObjectPropertyValues(hasAppEn, ontology).iterator().next().
				getObjectPropertyValues(hasExeEn, ontology).iterator().next().getObjectPropertyValues(hasOS, ontology).iterator().next().toStringID().substring(39);
		app_cpu_model = share_ind.getObjectPropertyValues(hasAppEn, ontology).iterator().next().
				getObjectPropertyValues(hasExeEn, ontology).iterator().next().getObjectPropertyValues(hasRes, ontology).iterator().next().getObjectPropertyValues(hasCpuM, ontology).iterator()
				.next().toStringID().substring(39);
		//System.out.println("*******app cpu model: " + app_cpu_model);
		app_cpu_speed = share_ind.getObjectPropertyValues(hasAppEn, ontology).iterator().next().
				getObjectPropertyValues(hasExeEn, ontology).iterator().next().getObjectPropertyValues(hasRes, ontology).iterator().next()
				.getDataPropertyValues(cpuSpeed, ontology).iterator().next().getLiteral();
		//System.out.println("*******app cpu speed: " + app_cpu_speed);
		HashMap<String, String> resource = new HashMap<String, String>();
		resource.put("user", username);
		//resource.put("user", username);
		resource.put("os", app_os);
		resource.put("model", app_cpu_model);
		resource.put("speed", app_cpu_speed);
		//resource.put("startT", startT);
		//resource.put("duration", duration);
		resource.put("cpuNo", Integer.toString(requiredCpuN));
		System.out.println("*********HERE" + app_os + "; " + app_cpu_model + " ," + app_cpu_speed + " .");		
		//System.out.println(share_ind.getObjectPropertyValues(hasAppEn, ontology).iterator().next().getObjectPropertyValues(hasExeEn, ontology).iterator().next().getObjectPropertyValues(hasOS, ontology).iterator().next().toStringID().substring(39));
		return resource;
		
	}
	
public static String[] getAppInfo(String share) throws OWLOntologyCreationException{
	String[] resource = new String[5];
		
		String ns = "http://www.owl-ontologies.com/alliance#";
		//String ns1 = "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#";
		//String file = "file:/opt/AHE3/ontology/ResvShare.owl";
		String file = mfile_path + share + ".owl";
		//String file = "file:/Users/zeqianmeng/Desktop/ontology/ResvShare.owl";
		//System.out.print("Reading SHARE file " + file + "...");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(IRI.create(file));
		//OWLDataFactory factory = manager.getOWLDataFactory();
		
		//OWLOntologyManager pManager = OWLManager.createOWLOntologyManager();
		//OWLOntology pOntology = manager.loadOntology(IRI.create(policyF));		
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );
		//System.out.println("part1 done.");
		
		reasoner.getKB().realize();
		
		OWLObjectProperty  hasAppEn = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasAppEnvironment"));
		OWLObjectProperty  hasExeEn = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasExeEnvironment"));
		OWLObjectProperty  hasOS = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasOSFamily"));
		OWLObjectProperty  hasRes = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasResource"));
		OWLObjectProperty  hasCpuM = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasCPUModel"));
		OWLDataProperty  cpuSpeed = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "clockSpeed"));
		OWLNamedIndividual share_indiv = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + share));
		//System.out.println(share_indiv.toStringID());
		/*app_os = share_ind.getObjectPropertyValues(hasAppEn, ontology).iterator().next().
				getObjectPropertyValues(hasExeEn, ontology).iterator().next().getObjectPropertyValues(hasOS, ontology).iterator().next().toStringID().substring(39);
		app_cpu_model = share_ind.getObjectPropertyValues(hasAppEn, ontology).iterator().next().
				getObjectPropertyValues(hasExeEn, ontology).iterator().next().getObjectPropertyValues(hasRes, ontology).iterator().next().getObjectPropertyValues(hasCpuM, ontology).iterator()
				.next().toStringID().substring(39);
		//System.out.println("*******app cpu model: " + app_cpu_model);
		app_cpu_speed = share_ind.getObjectPropertyValues(hasAppEn, ontology).iterator().next().
				getObjectPropertyValues(hasExeEn, ontology).iterator().next().getObjectPropertyValues(hasRes, ontology).iterator().next()
				.getDataPropertyValues(cpuSpeed, ontology).iterator().next().getLiteral();*/
		
		app_os = share_indiv.getObjectPropertyValues(hasAppEn, ontology).iterator().next().
				getObjectPropertyValues(hasExeEn, ontology).iterator().next().getObjectPropertyValues(hasOS, ontology).iterator().next().toStringID().substring(39);
		app_cpu_model = share_indiv.getObjectPropertyValues(hasAppEn, ontology).iterator().next().
				getObjectPropertyValues(hasExeEn, ontology).iterator().next().getObjectPropertyValues(hasRes, ontology).iterator().next().getObjectPropertyValues(hasCpuM, ontology).iterator()
				.next().toStringID().substring(39);
		//System.out.println("*******app cpu model: " + app_cpu_model);
		app_cpu_speed = share_indiv.getObjectPropertyValues(hasAppEn, ontology).iterator().next().
				getObjectPropertyValues(hasExeEn, ontology).iterator().next().getObjectPropertyValues(hasRes, ontology).iterator().next()
				.getDataPropertyValues(cpuSpeed, ontology).iterator().next().getLiteral();
		//System.out.println("*******app cpu speed: " + app_cpu_speed);
		/*String user_name_input= username;
		String os_input = app_os;
		String cpu_model = app_cpu_model;
		String cpu_speed = app_cpu_speed;
		String cpu_no = String.valueOf(requiredCpuN);*/
		//resource[0] = username;
		resource[0] = "Sofia";
		resource[1] = app_os;
		resource[2] = app_cpu_model;
		resource[3] = String.valueOf(app_cpu_speed);
		resource[4] = String.valueOf(requiredCpuN);
		//System.out.println("*********HERE" + app_os + "; " + app_cpu_model + " ," + app_cpu_speed + " .");	
		//System.out.println("*********HERE " + String.valueOf(app_cpu_speed));
		//System.out.println("*********HERE " + resource[3]);
		//System.out.println(share_ind.getObjectPropertyValues(hasAppEn, ontology).iterator().next().getObjectPropertyValues(hasExeEn, ontology).iterator().next().getObjectPropertyValues(hasOS, ontology).iterator().next().toStringID().substring(39));
		return resource;
		
	}
	
	public static String cloudProviderSearch(HashMap<String, String> resource) throws OWLOntologyCreationException{
		//osInput = Scientific_Linux_303;
	    //cpuModel = CPU_Intel;
		String provider_name = "";
		String service_name = "";
		float cost;
		int mem;
	    String instance;
	    int cpu_no = 0;

		//String osInput = "Scientific_Linux_303";
	    //String cpuModel = "CPU_Intel";
	    String start = "20150711120000";
	    String end = "20150711150000";
	    String duration;
	    
	    //duration = 3;
	    //****** except from info supplied by function call, other should be fetched in resource registry
	    
	    String osInput = app_os;
	    String cpuModel = app_cpu_model;
	    String cpuSpeed = app_cpu_speed;
	    //user_name = resource.get("username");
	    duration = resource.get("duration");
	    //cpuNo = Integer.parseInt(resource.get("cpuNo"));
	
		boolean result = false;
		
		String offers = "";
		long offer_no = 1;

		String ns = "http://www.owl-ontologies.com/alliance#";
		//String ns1 = "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#";
		//String service_file = "file:/opt/AHE3/ontology/service.owl";
	    String service_file = "file:/Users/zeqianmeng/Desktop/ontology/service.owl";
		//String file = "file:/Users/zeqianmeng/Desktop/ontology/cgo_backup/backup3/cgoTest.owl";
		System.out.println("Reading ontology file " + service_file + "...");
		OWLOntologyManager ser_manager = OWLManager.createOWLOntologyManager();
		OWLOntology ser_ontology = ser_manager.loadOntology(IRI.create(service_file));
		
		PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ser_ontology );
		
		//initiate for policy reasoning
		String file2 = "file:/opt/AHE3/ontology/mPolicy.owl";
		//String file2 = "file:/Users/zeqianmeng/Desktop/ontology/mPolicy.owl";
		//String file = "file:/Users/zeqianmeng/Desktop/ontology/cgo_backup/backup3/cgoTest.owl";
		System.out.println("Reading ontology file " + file2 + "...");
		OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
		OWLOntology ontology2 = manager2.loadOntology(IRI.create(file2));
		
		PelletReasoner reasoner2 = PelletReasonerFactory.getInstance().createReasoner( ontology2 );
		
	    System.out.println("*************PART1 START: to search for satisfied services");
		
	    //OWLClass csCla = manager.getOWLDataFactory().getOWLClass(IRI.create(ns + "ComputingService"));
		//NodeSet<OWLNamedIndividual> csSet = reasoner.getInstances(csCla, false);
	    
		
		//System.out.println("========" + );
		//System.out.println(csSet.get);
		
		// this can get provider
		// you can fetch provider info beforehand, if demands met, store it; if not, just leave it.
		//System.out.println(serSet.getNodes().toArray()[0]);
		
		//provider = serSet.getNodes().toArray()[1].toString();
		
		String hasOs = "hasOSFamily";
		String hasCpu = "hasCPUModel";
		String cpu_No = "PhysicalCPUs";
		
		String aOs;
		String aCpu;
		
		OWLObjectProperty  hasSer = ser_manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasComputingService"));
		OWLObjectProperty  hasPro = ser_manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasAdminDomain"));
		OWLObjectProperty  hasEn = ser_manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasExeEnvironment"));
		OWLObjectProperty  hasRes = ser_manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasResource"));
		OWLObjectProperty  hasOsOP = ser_manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + hasOs));
		OWLObjectProperty  hasCpuOP = ser_manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + hasCpu));
		//OWLNamedIndividual cs = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + provider));
		OWLNamedIndividual os = ser_manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + osInput));
		OWLNamedIndividual cpu = ser_manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + cpuModel));
		
		//OWLNamedIndividual service = manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "Service"));
		//System.out.println(reasoner.getObjectPropertyValues(csSet.iterator().next().getRepresentativeElement(), hasCpuOP));
		//OWLDataProperty  mem = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "Mem"));
		OWLDataProperty  cpuNoPro = ser_manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + cpu_No));
		OWLDataProperty  cpuSp = ser_manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "ClockSpeed"));
		
		OWLNamedIndividual service_ind = ser_manager.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + "AWS_EC2_Services"));
        Set<OWLIndividual> ser_set = service_ind.getObjectPropertyValues(hasSer, ser_ontology);
		//System.out.println("@@@@@@@@@@@@@@" + ser_ind.getObjectPropertyValues(hasEn, ontology).toString());
		//OWLClass serCla = manager.getOWLDataFactory().getOWLClass(IRI.create(ns + "ComputingService"));
		//NodeSet<OWLNamedIndividual> serSet = reasoner.getInstances(serCla, false);

		
		//HashMap<String, Service> serviceMap = new HashMap<String, Service>();
		
		for(OWLIndividual csInds : ser_set){ 
			//Computing Service
			service_name = csInds.getIndividualsInSignature().iterator().next().toStringID().substring(39);
			System.out.println("@@@@@@@********" + service_name);
			OWLNamedIndividual sv = csInds.asOWLNamedIndividual();

			Set<OWLIndividual> inds_oss = sv.getObjectPropertyValues(hasEn, ser_ontology).iterator().next().getObjectPropertyValues(hasOsOP, ser_ontology);

			Set<OWLIndividual> inds_res = sv.getObjectPropertyValues(hasEn, ser_ontology).iterator().next().getObjectPropertyValues(hasRes, ser_ontology);

		    boolean output1 = inds_oss.iterator().next().toStringID().contains(osInput);
		    //boolean output1 = inds_ens.iterator().next().hasObjectPropertyValue(hasOsOP, os, ontology);
		    boolean output2 = inds_res.iterator().next().hasObjectPropertyValue(hasCpuOP, cpu, ser_ontology);
		    boolean output;
		    if(output1&&output2){
		    	cpu_no = inds_res.iterator().next().getDataPropertyValues(cpuNoPro, ser_ontology).iterator().next().parseInteger();
		    	boolean output3 = cpu_no >= cpuNo;
		    	boolean output4 = inds_res.iterator().next().getDataPropertyValues(cpuSp, ser_ontology)
		    			.iterator().next().parseDouble() >= Double.parseDouble(cpuSpeed);
		    
		    output = output3&&output4;

		    }
		    else{
		    	output = false;
		    }
		
		if(output){
			////long duration = (finish - start)/3600;
			
		}
		else{
			result = false;
		}
		result = output;
		System.out.println("@@@@@@@********" +  result);
	  if(result){
			//for(OWLIndividual csIndss : ser_set){
				
				//OWLNamedIndividual svv = csIndss.asOWLNamedIndividual();
				
				provider_name = service_ind.getObjectPropertyValues(hasPro, ser_ontology).iterator().next().toStringID().substring(TRANCATION);
				//OWLNamedIndividual ser = serInd.getRepresentativeElement();
				System.out.println("@@@@@@@******** provider: " +  provider_name);

				System.out.println("provider: " + provider_name + "; service: " + service_name);

			
			System.out.println("*************PART2 START: to calculate cost for available services");
			
			String bal_s = "balance";
			double URemBal;
			
			OWLNamedIndividual userAPol = manager2.getOWLDataFactory().getOWLNamedIndividual(IRI.create(ns + username));
			OWLDataProperty  bal_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + bal_s));
			URemBal = userAPol.getDataPropertyValues(bal_pro, ontology2).iterator().next().parseDouble();
			
			OWLDataProperty  mem_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "Mem"));
			//OWLDataProperty  bal_pro = manager2.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + bal_s));
			
	    //OWLDataProperty  charge_pro = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "charge"));
	    OWLObjectProperty  instance_pro = ser_manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ns + "hasInstanceModel"));
	    //System.out.println(reasoner.getObjectPropertyValues(cs, hasSer).getFlattened().iterator().hasNext());
	    //Set<OWLLiteral> inds_charge = reasoner.getObjectPropertyValues(sv, hasEn).iterator().next().getRepresentativeElement().getDataPropertyValues(charge_pro, ontology);
	    //Set<OWLLiteral> inds_charge = reasoner.getObjectPropertyValues(cs, hasSer).iterator().next().getRepresentativeElement().getDataPropertyValues(charge, ontology);
	    
	 // to calculate cost
	    OWLDataProperty  charge_pro = ser_manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ns + "charge"));
	    //System.out.println(reasoner.getObjectPropertyValues(cs, hasSer).getFlattened().iterator().hasNext());
	    Set<OWLLiteral> inds_charge = sv.getObjectPropertyValues(hasEn, ser_ontology).iterator().next().getDataPropertyValues(charge_pro, ser_ontology);
	    //Set<OWLLiteral> inds_charge = reasoner.getObjectPropertyValues(cs, hasSer).iterator().next().getRepresentativeElement().getDataPropertyValues(charge, ontology);
	    System.out.println("charge is: " + inds_charge.iterator().next().getLiteral());
	    float charge = inds_charge.iterator().next().parseFloat();
	    //service1.setCost(inds_charge.iterator().next().getLiteral());
	    cost = charge * Long.parseLong(duration);
	    result = (cost <= URemBal);
	    if (result){
	    	
	    	mem = inds_res.iterator().next().getDataPropertyValues(mem_pro, ser_ontology).iterator().next().parseInteger();
	    	instance = inds_res.iterator().next().getObjectPropertyValues(instance_pro, ser_ontology).iterator().next().toStringID().substring(TRANCATION);
	    	
	    	Service service1 = new Service();
	    	
	    	//service1.setService_name(service_name);
			service1.setProvider(provider_name);
			service1.setCpuNo(cpu_no);
			service1.setMemory(mem);
			service1.setInstance(instance);
			service1.setCharge(charge);
			service1.setCost(cost);
			
			Offer offer = new Offer();
			
			//contract = new Contract();
			offer.setAppname(appInput);
			offer.setId(offer_no);
			offer.setUsername(username);
			offer.setOwner(owner);
			offer.setGroupname(grpInput);
			offer.setService(service1);
			offer.setStartTime(startT);
			//offer.setDuration(duration);
			offer.setStatus(NegState.Negotiation.toString());
			
			// need to change services to offers, and update hibernate db
			NegHibConnect.hibOffer(offer);
			
			//serviceMap.put(service_name, service1);
			offers = offers + offer_no + "- Provider: "+ provider_name + ", Instance type: " + instance +
			", CPU Number: " + cpuNo +  ", Mem: " + mem + ", cost: "+ cost + ", type: Cloud;";
			offer_no++;
			System.out.println("offers returned include: " + offers);
	    	/*System.out.println("charge is: " + charge);
		    System.out.println("total cost is: " + cost);
		    System.out.println("cpu no is: " + cpu_no);
		    System.out.println("mem GiB is: " + mem);
		    System.out.println("instace type is: " + instance);*/
	    }
	    
	    //System.out.println("charge is: " + inds_charge.iterator().next().getLiteral());
		}
		//return result;
		System.out.println("the reasoning output is: " + result);
		//}			
		}
		return offers;
	}
	
	/*public final static void main(String[] args) throws OWLOntologyStorageException, OWLOntologyCreationException{
		accessCheck("Sofia", "ManGroup", "WaterSteering");
		//HashMap<Long, String> offers = new HashMap<Long, String>();
		String offers;
		offers = resourceSearch("Sofia", "ManGroup", "WaterSteering", "20150728", "1");
		System.out.println("********main" + offers);
		System.out.println("@@@@@@" + grpInput);
		//System.out.println("======" + contract.getService());
		//HashMap<String, String> resource = getAppInfo();
		//System.out.println("cpu no: " + resource.get("cpuNo") + ", duration: " + resource.get("duration"));
		//System.out.println(ServiceReasoning.providerSearch(resource));
		//NegHibConnect.hibContract(contract);
		//policyUpdate("Sofia", "ManGroup", "WaterSteering");
		System.out.println("Done");
	}*/

}
