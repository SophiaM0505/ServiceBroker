package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import negotiation.Contract.AwsContract.AwsEc2ContractContents;
import negotiation.Database.NegotiationDB;
import negotiation.HibernateImp.Contract;
import negotiation.HibernateImp.ContractInst;
import negotiation.HibernateImp.NegHibConnect;
import negotiation.HibernateImp.Offer;
import negotiation.HibernateImp.Service;
import negotiation.Negotiation.NegFunctions;
import negotiation.Negotiation.NegState;
import negotiation.Ontology.OntReasoning;
import negotiation.Ontology.OntUpdate;
import negotiation.Ontology.ServiceReasoning;
import negotiation.Steerer.SteerConnect;
import negotiation.Steerer.StopConnect;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import test.helper.Executable;
import uk.ac.ucl.chem.ccs.AHECore.API.AHE_API;
import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;
import uk.ac.ucl.chem.ccs.AHECore.Definition.AppInstanceStates;
import uk.ac.ucl.chem.ccs.AHECore.Definition.UserRoles;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Engine.ResourceRegisterAPI;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHESecurityException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.FileTransferSyntaxException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.FileStaging;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.JobSubmissionHandler;
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;
import uk.ac.ucl.chem.ccs.AHEModule.Def.rest_arg;

import com.google.gson.Gson;


/**
 * Handles /appinst URI 
 * 
 * GET - list all appinst (user has restricted view. Admins have unrestricted access to all appinst)
 * POST - prepare (if resource is not specified). start app_inst (if app name, resource name are supplied)
 * DELETE - terminate resource
 * 
 * @author davidc
 *
 */

public class AppInstanceResource extends AHEResource{
	
	String appinst;
	//String argument;
	//String user;
	//String group;
	//String app;
	//String start_time;
	//String end_time;
	
	long inst_id;
	public static long contract_id;
	static String appname;
	String startT;
	String duration;
	String username;
	String group;
	String pay;
	
	String usercert_subjectdn = "";
	
    @Override
    public void doInit() {
    	
    	//this.appinst = (String) getRequestAttributes().get("appinst");
    	//this.argument = (String) getRequestAttributes().get("arg");
    	//this.app = (String) getRequestAttributes().get("app");
    	//this.user = (String) getRequestAttributes().get("user");
    	//this.group = (String) getRequestAttributes().get("group");
    	//this.start_time = (String) getRequestAttributes().get("start_time");
    	//this.end_time = (String) getRequestAttributes().get("end_time");
    	
    	this.pay = (String) getRequestAttributes().get("start_time");
    }

    
    private String post_FORM_Parser(Representation entity) throws OWLOntologyCreationException, OWLOntologyStorageException, NumberFormatException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, AHESecurityException, InterruptedException, IOException{
    	
    	long app_inst_id = -1;
    	//System.out.println(entity.getSize());
    	
    	// size: 61
    	//Executable.writeout("first step");
    	//System.out.println("entity size: " + entity.getSize());
    	//return "entity size: " + entity.getSize();
    	//System.out.println("appinst value: " + appinst);
    	
    	Form form = new Form(entity);
    	//Set<String> nameSet = form.getNames();
    	
/*    	if(form.isEmpty()){
    		System.out.println("*************** recevied form is empty.");
    	}
    	else if (form.getNames().contains("end_time")) {
    		System.out.println("***************" + form.getFirstValue("contract_id"));
    		System.out.println("***************" + form.getFirstValue("end_time"));
    	}*/
    
    	// user clicks on agree button to send AcceptAck
    	if(form.getNames().contains("contract_id") && !form.getNames().contains("end_time") && 
    			!form.getNames().contains("core") && !form.getNames().contains("complete")){
    		//System.out.println("contract********");
    		//Executable.writeout("1 step");
    		contract_id = Long.parseLong(form.getFirstValue("contract_id"));
    		
    		//long job_id = Long.parseLong(form.getFirstValue("job_id"));
    		//String provider = NegotiationDB.getContProvider(contract_id);
    		//int re_neg = Integer.parseInt(form.getFirstValue("contract_id"));
    		//contract_id = Long.parseLong(form.getFirstValue("contract_id"));
    		
    		String current_state = NegotiationDB.getContractStatus(contract_id);
    		String workflow;
    		//String passedInfo = "";
    		if(form.getNames().contains("terminate")){
    			
    			// to check if contract in 'Completed' state, if it is, inform user, indicating that
    			// balance already reduced in both Grid and Cloud cases
    			if (current_state.equalsIgnoreCase(NegState.Completed.toString())){
    				return "error: contract completed. Cannot be re-negotiated.";
    			}
    			
    			// to check if Contract is in 'Contracted' state; if it does, allow termination
    			if (current_state.equalsIgnoreCase(NegState.Contracted.toString())){
    				NegotiationDB.updateOfferState(contract_id, NegState.ReqTerminated);
    				NegotiationDB.updateContractState(contract_id, NegState.ReqTerminated);
    				// to check if contract requires penalty, if it does, update ontology as well 
    			    return "error: user terminated";
    			}
    			else {
    				long neg_id = NegotiationDB.getNegID(contract_id);
			    	String all_offers = NegotiationDB.getNegOffers(neg_id);
			    	String[] all_offers_arry = all_offers.split(";");
			    	for(String offer : all_offers_arry){
			    		long temp_offer_id = Long.parseLong(offer.split("-")[0]);
			    		NegotiationDB.updateOfferState(temp_offer_id, NegState.Uncontracted);
			    	}
			    	NegotiationDB.updateContractState(contract_id, NegState.Uncontracted);
			    	
    				return contract_id + ":" + "terminated";
    			}
    		}
    		// to deal with re-negotiation, if received AcceptAck contains job_id, indicating its a re-negotiated contract
    		/*if(form.getNames().contains("job_id")){
    			long job_id = Long.parseLong(form.getFirstValue("job_id"));
    			//if existing node, 
    			String[] old_contract = NegotiationDB.getOldContract(job_id); 
    			long old_contract_id = Long.parseLong(old_contract[0]);
    			String old_node = old_contract[1];
    			String new_node = old_contract[2];
    			//to update balance
    			    //if cloud, need to update timer anyway. this means max_duration needs to be updated
    			    //if grid, need to update timer, max_duration needs to be update
    			    //submit
    			//if not existing node,
    			    //submit job to new node as usual			
    			
    		}*/
    		else{
    			long job_id = 0;
    			boolean if_reneg;
        		//******* need to update negotiation state in db
    			System.out.println("%%%%%%%%%***** inside acceptAck current negotiation state: " + current_state);
    			// to check negotiation state is not completed or terminated
    			if (current_state.equalsIgnoreCase(NegState.Completed.toString()) ||
    					//current_state.equalsIgnoreCase(NegState.ReqTerminated.toString()) ||
    					current_state.equalsIgnoreCase(NegState.Terminated.toString())){
    				System.out.println("error: negotiation state is Completed or Terminated.");
    				return "error: negotiation state is Completed or ReqTerminated.";
    			}
    			
    			if (current_state.equalsIgnoreCase(NegState.ReqTerminated.toString())){
    				System.out.println("alarm: negotiation is terminated by manager.");
    				return "alarm: negotiation is terminated by manager.";
    			}
    			
    			// duplicate AcceptAck
    			/*if (current_state.equalsIgnoreCase(NegState.Contracted.toString()) && re_neg == 1){
    				System.out.println("alarm: negotiation is already in Contracted state.");
    				return "alarm: negotiation is already in Contracted state.";
    			}*/
    		
    		    //to distinguish negotiation and re-negotiation cases
    			//if re-negotiation, dont need to reduce user's balance with max in policy ontology
    			//probably re-neg tag sent by client would be easier
    			//long job_id = NegotiationDB.getJobId(contract_id);
    			String provider = null;
    			if_reneg = form.getNames().contains("job_id");
    			if(if_reneg){
        		//if(job_id != 0){
        			//to update only site's balance
    				String offers_comb = NegotiationDB.getOfferSub(contract_id);
    				if(offers_comb.equalsIgnoreCase("")){
    					provider = OntUpdate.shareMaxReduce(contract_id);
    				}
    				else{
    					String[] offers_arr = offers_comb.split(";");
    				
             		    for(String offer: offers_arr){
    				
             			long temp_con_id = Long.parseLong(offer);
        		        provider = OntUpdate.shareMaxReduce(temp_con_id);
        		        
             		  }
    				}
        		    //JobSubmissionHandler.timer.purge();
        		    //JobSubmissionHandler.timer.cancel();
             		 job_id = Long.parseLong(form.getFirstValue("job_id"));
     		         System.out.println("********* renegotiation received job id: " + job_id);
     		    
     		        NegotiationDB.setRenegTag(job_id, 1);
        		    
        		}
    			else {	
    				//TODO: if provider is grid-based, update ontology here
    				//String[] proMax = new String[4];
    	    		//proMax = NegotiationDB.getContProMax(contract_id);
    	    		//String provider = proMax[0];
    	    		//boolean virtual = Boolean.parseBoolean(proMax[1]);
    	    		// to get if user's privilege is over written from database

    	    		provider = OntUpdate.mPolicyShareMaxReduce(contract_id);

    	    		Random rand2 = new Random();
		   	        job_id = rand2.nextInt(10000 + 1);
		   	     System.out.println("********* negotiation created job id: " + job_id);
    			}

    	            // to reduce balance in policy and share
    	            
    	    		//String provider = NegotiationDB.provider;
    	    		//TimeUnit.SECONDS.sleep(5);
    	    		//System.out.println("provider: " + provider);
    	    		//String provider = "UoM_cluster";
    	    		
    			
/*    			    String app = form.getFirstValue("appname");
    	    		System.out.println("app name received: " + app);

    	    			System.out.println("provider: " + provider);
    	    		    if (app.equalsIgnoreCase("WaterSteering") && provider.equalsIgnoreCase("UoM_cluster")){
    	    			    form.removeFirst("appname");
    	    			    form.add("appname", "WaterSteeringUoM");
    	    		    }*/
    				
        		//System.out.println("contract id is : " + contract_id);
        		
        		// to get provider and give workflow name into form submitted to prepare
        		//String provider = NegotiationDB.getContProvider(contract_id);
        		//String provider = "Amazon";
    				
    				//to generate and set job id for a contract
    					//NegotiationDB.updateContractState(contract_id, NegState.Contracted);
        				//NegotiationDB.updateOfferState(contract_id, NegState.Contracted);
    				NegotiationDB.updateContractJob(contract_id, NegState.Contracted, job_id);
    				//create table for job+contract
    				//tag 0 for negotiation, 1 for re-negotiation.
    				if(!if_reneg){
    					//if negotiation, create entry for job+contract
    					//when re-negotiation, can use job_id to get old contract
    					NegotiationDB.insertJob(job_id, contract_id, 0);
    				}
    			
    				//to update other offers' states to uncontracted;
    				/*long neg_id = NegotiationDB.getNegID(contract_id);
    				String offers = NegotiationDB.getNegOffers(neg_id);
    				if(!offers.equalsIgnoreCase("")){
    				    String[] offers_array = offers.split(";");
    				    long temp_offer_id;
    				    //int i=0;
    				    for(String offer:offers_array){
    					    temp_offer_id = Long.parseLong(offer.split("=")[0]);
    					    if(temp_offer_id != contract_id){
    						    NegotiationDB.updateOfferState(temp_offer_id, NegState.Uncontracted);
    					}
    				}
    				}*/
        		
        		if(provider.equalsIgnoreCase("AWS") || provider.equalsIgnoreCase("UoM_cluster")){
        			workflow = "AwsWorkflow";
        		}
        		else{
        			workflow = "AHEWorkflow";
        			//********** todo: to update ontology if provider is grid
        		}
        		
        		Form registry = new Form();
        		//return appinst_prepare(ResourceUtil.getArgumentMap(registry));
    	    
        		registry.add("workflow_name", workflow);
        		
        		
        		String passedInfo = "";
        		String endPoint;
        		
        		if(provider.equalsIgnoreCase("AWS")){
        			registry.add("appname", "WaterSteering");
/*        		if(if_reneg){
        		String offers_comb = NegotiationDB.getOfferSub(contract_id);
       		    String[] offers_arr = offers_comb.split(";");
       		    String contents = null;
       		    for(String offer: offers_arr){
				
       			  long temp_con_id = Long.parseLong(offer);
       			  //******** to get contents for pass to SteerService
       			  
       			  //form.removeFirst("contract_id");
       			  //form.add("contract_id", Long.toString(temp_con_id));
        		// to call Prepare (and add workflow_file)
        		//Form registry = new Form();
        		//return appinst_prepare(ResourceUtil.getArgumentMap(registry));
       		    }
       		    form.add("job_id", Long.toString(job_id));
        		//job submission, namely communication with steerer service is included in bpmn workflow
        		  appinst_prepare(ResourceUtil.getArgumentMap(form));
        		//return "<37 ok";
       		  
        		}
        		else{
        			appinst_prepare(ResourceUtil.getArgumentMap(form));
        		}*/
        		System.out.println("================================" + registry.size());
        		//System.out.println("================================" + form.);
        		appinst_prepare(ResourceUtil.getArgumentMap(registry));
        		//if(if_reneg){
        			
        			String offers_comb = NegotiationDB.getOfferSub(contract_id);
    				//String worker;
    				  //int core;
    				  
    				if(offers_comb.equalsIgnoreCase("")){
    					//NegotiationDB.updateContractStartT(contract_id, date);
    	       			  //String[] results = NegotiationDB.getSteererInfo(contract_id);
    	    			  //worker = results[0];
    					  endPoint = NegotiationDB.getEndpoint(contract_id);
    	    			  //core = Integer.parseInt(results[2]);
    	       			  //to get steerer uri from database (endPoint)
    	       			  //String uri_pre = job_service.toString();
    	    			  passedInfo = passedInfo + contract_id + "," + endPoint + ";";
    				}
    				else{  
    				  
    	       		  String[] offers_arr = offers_comb.split(";");
    	       		  for(String offer: offers_arr){
    	       			  long temp_cont = Long.parseLong(offer);
    	       			  //NegotiationDB.updateContractStartT(temp_cont, date);
    	       			  //String[] results = NegotiationDB.getSteererInfo(temp_cont);
    	    			  //worker = results[0];
    	       			  endPoint = NegotiationDB.getEndpoint(temp_cont);
    	    			  //core = Integer.parseInt(results[2]);
    	       			  //to get steerer uri from database (endPoint)
    	       			  //String uri_pre = job_service.toString();
    	    			  passedInfo = passedInfo + temp_cont + "," + endPoint + ";";
    	       			  //response = SteererConnect.connectSteerer(uri, user, app, job_id, worker, core);
    	       			  //System.out.println("********* job submitted for contract: " + offer);
    	       			//System.out.println("********* re-neg passed information to SteerService: " + passedInfo);
    	       		  }
    				}
    				passedInfo = job_id + "!" + passedInfo;
    				System.out.println("========= passedInfo: " + passedInfo);
    	       		return passedInfo;
        		}
        		else{
        			registry.add("appname", "CompSteering");
        			appinst_prepare(ResourceUtil.getArgumentMap(registry));
        			//to return SteerService 'steer' api, user can call 'steer' api in SteerService using client device
        			
        			endPoint = NegotiationDB.getEndpoint(contract_id);
	    			  //core = Integer.parseInt(results[2]);
	       			  //to get steerer uri from database (endPoint)
	       			  //String uri_pre = job_service.toString();
	    			  passedInfo = endPoint;
	    			  passedInfo = job_id + "!" + passedInfo;
	    				System.out.println("========= passedInfo: " + passedInfo);
        			return passedInfo;
        		}
    	       		
        		//}
        		// to return job_id, contents; contents include contract id, worker, steerer endPoint 
        	    //return "submitted";
    			}
    	}
    	/*if(form.getNames().contains("query_id")){
    		System.out.println("**************** inside here ");
    		long contract_id = Long.parseLong(form.getFirstValue("query_id"));
    		String offers_comb = NegotiationDB.getOfferSub(contract_id);
			//String worker;
			  //int core;
			  String endPoint;
			  String passedInfo = "";
			  
			if(offers_comb.equalsIgnoreCase("")){
				//NegotiationDB.updateContractStartT(contract_id, date);
       			  //String[] results = NegotiationDB.getSteererInfo(contract_id);
    			  //worker = results[0];
    			  endPoint = NegotiationDB.getEndpoint(contract_id);
    			  //core = Integer.parseInt(results[2]);
       			  //to get steerer uri from database (endPoint)
       			  //String uri_pre = job_service.toString();
    			  passedInfo = passedInfo + contract_id + "," + endPoint + ";";
			}
			else{  
			  
       		  String[] offers_arr = offers_comb.split(";");
       		  for(String offer: offers_arr){
       			  long temp_cont = Long.parseLong(offer);
       			  //NegotiationDB.updateContractStartT(temp_cont, date);
       			  //String[] results = NegotiationDB.getSteererInfo(temp_cont);
    			  //worker = results[0];
    			  //endPoint = results[1];
       			  endPoint = NegotiationDB.getEndpoint(contract_id);
    			  //core = Integer.parseInt(results[2]);
       			  //to get steerer uri from database (endPoint)
       			  //String uri_pre = job_service.toString();
    			  passedInfo = passedInfo + temp_cont + "," + endPoint + ";";
       			  //response = SteererConnect.connectSteerer(uri, user, app, job_id, worker, core);
       			  //System.out.println("********* job submitted for contract: " + offer);
       			//System.out.println("********* re-neg passed information to SteerService: " + passedInfo);
       		  }
			}
			String job_id = Long.toString(NegotiationDB.getJobId(contract_id));
			passedInfo = job_id + "!" + passedInfo;
			System.out.println("========= passedInfo: " + passedInfo);
       		return passedInfo;
    		//return Long.toString(NegotiationDB.getJobId(contract_id));
    		
    	}*/
        	if(form.getNames().contains("offer_id")){
        		
        		//String offer_id_s = form.getFirstValue("offer_id");
        		long offer_id = Long.parseLong(form.getFirstValue("offer_id"));
        		String current_state = NegotiationDB.getOfferStatus(offer_id);
        		
        		if(form.getNames().contains("terminate")){
        			// terminate during negotiation does not introduce penalty
        			if (current_state.equalsIgnoreCase(NegState.Negotiation.toString())){
        			    long neg_id = NegotiationDB.getNegID(offer_id);
			    	    String all_offers = NegotiationDB.getNegOffers(neg_id);
			    	    String[] all_offers_arry = all_offers.split(";");
			    	    for(String offer : all_offers_arry){
			    		    long temp_offer_id = Long.parseLong(offer.split("-")[0]);
			    		    if(temp_offer_id == offer_id){
			    		        NegotiationDB.updateContractState(contract_id, NegState.Uncontracted);
			    		    }
			    		    NegotiationDB.updateOfferState(temp_offer_id, NegState.Uncontracted);
			    	    }
			    	
        			//NegotiationDB.updateOfferState(offer_id, NegState.Uncontracted);
        			    return offer_id + ":terminated";
        			}
        		}
        		
        		if(form.getNames().contains("revocation")){
        			if (current_state.equalsIgnoreCase(NegState.Completed.toString()) ||
					    current_state.equalsIgnoreCase(NegState.Uncontracted.toString()) ||
					    current_state.equalsIgnoreCase(NegState.Terminated.toString())){
        				return "error: requested offer already in a final state";
        			}
        			else{
        				// how to enable it continue with the negotiation procedure?
        				// to create a method for the below function??
        				// decision 1: revocation rejected
        				int decision = 1;
        				String revoke_res;
        				
        				//to calculate for decision
        				Random rand = new Random();

        			    // nextInt is normally exclusive of the top value,
        			    // so add 1 to make it inclusive
        			    int randomNum = rand.nextInt(10000 + 1);
        			    
        			    System.out.println("randomNum generated: " + randomNum);
        			    //decision = randomNum % 2;
        			    decision = 0;
        			    
        			    //if revocation accepted, update negotiation state of this offer to Uncontracted
        			    if(decision == 0){
        			    	NegotiationDB.updateOfferState(offer_id, NegState.Uncontracted);
        			    	NegotiationDB.updateContractState(contract_id, NegState.Uncontracted);
        			    	long neg_id = NegotiationDB.getNegID(offer_id);
        			    	String all_offers = NegotiationDB.getNegOffers(neg_id);
        			    	System.out.println("=========== all offers: " + all_offers);
        			    	String other_offers = "";
        			    	String[] all_offers_arry = all_offers.split(";");
        			    	for(String offer : all_offers_arry){
        			    		long temp_offer_id = Long.parseLong(offer.split("-")[0]);
        			    		if(temp_offer_id != offer_id){
        			    			other_offers = other_offers + offer;
        			    		}
        			    	}
        			    	if(other_offers.isEmpty())
        			    	{
        			    		revoke_res = "no other offers available";
                  		        return revoke_res;
        			    	}
        			    	else {
        			    		return other_offers;
        			    	}
        			    }
        			    
        				revoke_res = offer_id + ":" + decision;
          		        return revoke_res;
              
        			}
        			     
        		}
        		else{
        			System.out.println("negotiation current state: " + current_state);
        			if(current_state.equalsIgnoreCase(NegState.Initiating.toString()))
        			{
        		
        		      Calendar cal1 = Calendar.getInstance();
        		      SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");
        		//sdf1.format(cal1.getTime());
        		      Calendar cal2 = Calendar.getInstance();
            	//cal.getTime();
            	      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                   	  String date = sdf1.format(cal1.getTime()) + "T" + sdf.format(cal2.getTime()) + "+7:00";
            	
                   	  boolean if_comb = NegotiationDB.getOfferSub(offer_id).isEmpty();
                   	  if(!if_comb){
                   		  //if offer is combined by sub-offers, loop to update
                   		  //fetch combination of offers
                   		  String offers_comb = NegotiationDB.getOfferSub(offer_id);
                   		  String[] offers_arr = offers_comb.split(";");
                   		  for(String offer: offers_arr){
                   			  long temp_offer_id = Long.parseLong(offer);
                   			  Contract contract = NegotiationDB.offerToContract(temp_offer_id, date);
                         	  
              	              contract.setContractDate(date);
              	              contract.setStatus(NegState.Negotiation.toString());
          		              NegHibConnect.hibContract(contract);
          		              NegotiationDB.updateOfferState(offer_id, NegState.Negotiation);
                   			  
                   		  }
                   		  //to deal with combined-offer to combined-contract
                   		Contract contract = NegotiationDB.combOfferToContract(offer_id, date);
                     	  
              	        contract.setContractDate(date);
              	        //contract.setStartTime(date);
              	        contract.setStatus(NegState.Negotiation.toString());
              	        // generate an entry in db for newly agreed contract
          		        NegHibConnect.hibContract(contract);
          		        NegotiationDB.updateOfferState(offer_id, NegState.Negotiation);
                   	  }
                   	  
            
                   	  else{
                      Contract contract = NegotiationDB.offerToContract(offer_id, date);
                   	  
            	      contract.setContractDate(date);
            	      //contract.setStartTime(date);
            	      contract.setStatus(NegState.Negotiation.toString());
            	     // generate an entry in db for newly agreed contract
        		      NegHibConnect.hibContract(contract);
        		      NegotiationDB.updateOfferState(offer_id, NegState.Negotiation);
                   	  
                   	  }
                   	 
        		// generate and store a pdf version of contract locally
        		// require fetch a lot of info form database...leave it later
        		// returned info: contract id, contract details
        		//String FILE = "/opt/AHE3/contract/" + group + "/" + username + "/" + offer_id + ".pdf";
        		      //String contract_contents = contract_id + ": " + AwsEc2ContractContents.getAwsContract();
                   	//String contract_contents = Long.toString(offer_id);
        		      return Long.toString(offer_id);
        		//return "contract details";
        		}
        			else{
        				return "error: this offer already in state " + current_state;
        			}
        		}
        	}

        	// receive user's negotiation request
        	// input received by ahe3: user name + app name + startT + duration
        	// as is before formal negotiation, no state check
        	if(form.getNames().contains("appname") && !form.getNames().contains("contract_id") && !form.getNames().contains("core") && !form.getNames().contains("duration") && !form.getNames().contains("jobtype")){
        		//System.out.println("entity size: " + entity.getSize());
                //boolean enough = false;
        		//boolean provider = false;
                //Form form = new Form(entity);
                //System.out.println(form.getNames().size());
                //if(form.getNames().size()>1)
               
        		
        		// TO ADD FETCH GROUP via SecurityUserAPI here??
        		pay = form.getFirstValue("start_time");
                username = form.getFirstValue("username");
                //return "here" + userV;
                //AHEUser check_user = SecurityUserAPI.getUser(username);
                //group = check_user.getAcd_vo_group();
                group = form.getFirstValue("group");
                
                //return "*****" + groupV;
                //String groupV = form.getFirstValue("acd_vo_group");
                appname = form.getFirstValue("appname");
               // System.out.println(appV);
                //startT = form.getFirstValue("startT");
                //duration = form.getFirstValue("duration");
                //System.out.println("inputs got by server: " + userV + groupV + appV);
                //neg = true;
                //update negotiation database
                //enough = false;
                
                // to check if user has enough balance and Share can satisfy requirements
        		//enough = OntReasoning.accessCheck(username, group, appname);
        				//OntReasoning.resourceSearch(userV, groupV, appV);
        				//ServiceReasoning.providerSearch();
        		//System.out.println("reasoning result: " + enough);
        		//return "******" + enough;
        		//HashMap<Long, String> offers = new HashMap<Long, String>(); 
        		String offers;
        		System.out.println("payment method receveid in rest server: " + pay);
        		//3rd parameter: core number; 4th parameter: duration; 5th parameter: as soon as possible  
        		offers = OntReasoning.getOffersFromShares(username, group, appname, 0, 0, pay, 0);
        		/*
        	    if(enough){
        			//user has enough resources to run the app 
        			//return userV + groupV + appV + "REASONING DONE!";
        			//return "ok";
        			offers = OntReasoning.resourceSearch(username, group, appname, startT, duration);
        			//offers = "ok";
        			System.out.println("********" + offers);*/
        			//return "********" + offers.get(1);
        			//return "ok";
        			
        			/*if(contract.getId() != 0){
        				//update contract database
        				//NegHibConnect.hibContract(contract);
        				//OntReasoning.policyUpdate(userV, groupV, appV);
        				System.out.println("will continue with ahe3");
        				return "will continue with ahe3";
        				//can return message for user confirmation then back to continue with ahe3
        				//return appinst_prepare(ResourceUtil.getArgumentMap(new Form(entity)));
        			}*/
        			// if service found, return offer id and service name
        			if(!offers.isEmpty()){
        				//******* you need to trim offer info to string for return message to client
        				//String workerN = offers.values().iterator().next();
        				//System.out.println("******workerN: " + workerN);
        				//return workerN;
        			    Random rand = new Random();
        			    long neg_id = rand.nextInt(10000 + 1);
        			    //WRONG: to get neg_id from offer in database
        			    NegotiationDB.insertNeg(neg_id, offers);
        				return offers;
        				//return "hello";
        			}
        			// if no existing service found, try to search for new service providers
        			else{
        				return "Rejected: no matched resources found.";
        				//existing providers cannot satisfy user's demands
        				//activate negotiation with other providers directly, then returns satisfied providers
        				// for user confirmation/selection
        				// then continue with policy update and hibernate contract generation
        				/*
        				HashMap<String, String> request = new HashMap<String, String>();
        				request = OntReasoning.getAppInfo();
        				request.put("appname", appname);
        				request.put("duration", duration);
        				request.put("username", username);
        				request.put("startT", startT);
        				request.put("group", group);
        				
        			    //String service = ServiceReasoning.providerSearch(request);
        				String service = OntReasoning.cloudProviderSearch(request);
        				System.out.println("negotiation activated to search for new providers");
        				//System.out.println("******" + service.keySet());
        				if(service.isEmpty()){
        					return "Rejected: no matched resources found.";
        				}
        				else{
        					return service;
        				}*/
        				//return "negotiation activated to search for new providers";
        				
        			}
        			//return ResourceUtil.ThrowErrorMessage("","REASONING DONE", "AppInstanceResource.java");
        		//}
        		//else
        			//return "Rejected: requester has no enough resources to run the application.";
        			//return ResourceUtil.ThrowErrorMessage("","REASONING DONE", "AppInstanceResource.java");
        		}
        	
        	if(form.getNames().contains("appname") && form.getNames().contains("jobtype")){
        		// to change 'resource' to 'numjobs' and 'nefold'

                username = form.getFirstValue("username");
                group = form.getFirstValue("group");
                appname = form.getFirstValue("appname");
                String job_type = form.getFirstValue("jobtype");
                String deadline = form.getFirstValue("deadline");
                
                //if 'numjobs'>1, 'resource' = 'parallel'; if 'numjobs' = 1, 'resource' = 'serial'
                int numjobs = Integer.parseInt(form.getFirstValue("numjobs"));
                int nefold = Integer.parseInt(form.getFirstValue("nefold"));
                String resource = null;
                
                if(job_type.equalsIgnoreCase("serial")){
                	resource = "normal";
                }
                else if(numjobs > 1){
                	resource = "super";
                }
                else{
                	return "incorrect numjobs input.";
                }

        		String offers;
 
        		offers = OntReasoning.getOffersFromSharesComp(username, group, appname, resource, numjobs, nefold, deadline);

        			if(!offers.isEmpty()){

        			    Random rand = new Random();
        			    long neg_id = rand.nextInt(10000 + 1);
        			    NegotiationDB.insertNeg(neg_id, offers);
        				return offers;
        				//return "hello";
        			}
        			// if no existing service found, try to search for new service providers
        			else{
        				return "Rejected: requester cannot run jobs parallel.";
        				
        			}
        		}
        	
        	
        	if(form.getNames().contains("core") && !form.getNames().contains("contract_id")){
        		group = form.getFirstValue("group");
        		username = form.getFirstValue("username");
        		appname = form.getFirstValue("appname");
        		
        		int core = Integer.parseInt(form.getFirstValue("core"));
        		String offers;
        		
        		//3rd parameter: core number; 4th parameter: duration; 5th parameter: as soon as possible
        		offers = OntReasoning.getOffersFromShares(username, group, appname, core, 0, pay, 0);
        		
        		if(!offers.isEmpty()){
    				//******* you need to trim offer info to string for return message to client
    				//String workerN = offers.values().iterator().next();
    				//System.out.println("******workerN: " + workerN);
    				//return workerN;
    				return offers;
    				//return "hello";
    			}
        		else{
        			return "offer empty";
        		}
        	}
        	
        	// this is for RE-NEGOTIATION (if existing provider cannot meet demands, new negotiation)
        	if(form.getNames().contains("core") && form.getNames().contains("contract_id")){
        		long contract_id = Integer.parseInt(form.getFirstValue("contract_id"));
        		pay = form.getFirstValue("start_time");
        		//group = form.getFirstValue("group");
        		//username = form.getFirstValue("username");
        		//appname = form.getFirstValue("appname");
        		String[] user_info = NegotiationDB.getUserInfo(contract_id); 
        		String username = user_info[0];
        		String groupname = user_info[1];
        		String appname = user_info[2];
        		String share = user_info[3];
        		
        		int core = Integer.parseInt(form.getFirstValue("core"));
        		String offers;
        		
        		//to re-negotiate with existing contract
        		//offers = OntReasoning.reNegotiate(contract_id, core);
        		String current_state = NegotiationDB.getContractStatus(contract_id);
        		
        		if(current_state.equalsIgnoreCase(NegState.Contracted.toString())){
        		//4th parameter: core number; 5th parameter: duration; 6th parameter: as soon as possible; 7th: for re-neg
        		    //offers = OntReasoning.getOffersFromShares(username, groupname, appname, core, 0, pay, contract_id);
        		    
        			//to get the Share which formed the contract
        			offers = OntReasoning.resourceSearch(username, groupname, appname, share, pay, core, contract_id);
        			
        		    if(!offers.isEmpty()){
    			 	//******* you need to trim offer info to string for return message to client
    				//String workerN = offers.values().iterator().next();
    				//System.out.println("******workerN: " + workerN);
    				//return workerN;
        		    	
        		    	//to fetch only required info for steerer: contract_id
        		    	//String[] offer_arr = offers.split(";");
        				//String content = offer_arr[1].split("-")[0];
    				    //return content;
        		    	return offers;
    				//return "hello";
    			    }
        		    else{
        		    	//should negotiation with other shares for this app
        		    	offers = OntReasoning.getOffersFromOtherShares(username, groupname, appname, core, 0, pay, contract_id, share);
        			    return offers;
        		    	//String[] offer_arr = offers.split(";");
        				//String content = offer_arr[1].split("-")[0];
    				    //return content;
        		    }
        		}
        		else{
        			return "error: something wrong during re-negotiation";
        		}
        	}
        	
        	if(form.getNames().contains("duration")){
        		int duration = Integer.parseInt(form.getFirstValue("duration"));
        		group = form.getFirstValue("group");
        		username = form.getFirstValue("username");
        		appname = form.getFirstValue("appname");
        		//int core = 0;
        		String offers;
        		
        		//3rd parameter: core number; 4th parameter: duration; 5th parameter: as soon as possible
        		offers = OntReasoning.getOffersFromShares(username, group, appname, 0, duration, pay, 0);
        		
        		if(!offers.isEmpty()){
    				//******* you need to trim offer info to string for return message to client
    				//String workerN = offers.values().iterator().next();
    				//System.out.println("******workerN: " + workerN);
    				//return workerN;
    				return offers;
    				//return "hello";
    			}
        		else{
        			return "offer empty";
        		}
        	}
        	
        	if(form.getNames().contains("balance")){
                // suppose the values from Steerer service include end_time;
        		String user_name = form.getFirstValue("username");
        		String group = form.getFirstValue("group");
        	    String result;
        		
        	    result = OntReasoning.getBalance(user_name, group);
        	    
        		return result;
        	}
        	
        	//to receive 'stop' from SteerService in Junyi's case (actually, this is not going to happen, as his case prefers
        	  //to let job completes execution)
        	//to receive 'stop' from client in cluster case
        	//you suppose that contract formed with expected duration, while user can also terminate 
        	    //execution before job complete
        	if(form.getNames().contains("stop")){
        		String end_time = null;
        		System.out.println("************ Im here");
        		//boolean stop_site = false;
        		//to stop the timer set by 'maxDuration'
        		JobSubmissionHandler.timer.cancel();
        		JobSubmissionHandler.timer.purge();
        		System.out.println("TimerTask stopped by user request");
    	        System.out.println("Timer task stopped by user request at:"+new Date());
        		
        		//for test case only
        		/*Calendar cal1 = Calendar.getInstance();
    		        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");
    		        Calendar cal2 = Calendar.getInstance();
      	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
             	String end_time = sdf1.format(cal1.getTime()) + "T" + sdf.format(cal2.getTime()) + "+7:00";*/
        		
                // suppose the values from SteererService include end_time;	     		
        		long contract_id = Long.parseLong(form.getFirstValue("contract_id"));
        		//long job_id = Long.parseLong(form.getFirstValue("job_id"));
        		//int tag = NegotiationDB.getRenegTag(job_id); 
        		
        		/*if(form.getNames().contains("end_time")){
        		    end_time = form.getFirstValue("end_time");
        		}*/
        		//else{
        			//to receive 'stop' from client, need to send 'stop' to steerer specifying sites 
        			//to send 'stop' here means calling stop api
        			//stop_site = true;
        			Calendar cal1 = Calendar.getInstance();
        		    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");
        		    Calendar cal2 = Calendar.getInstance();
        		    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        	     	end_time = sdf1.format(cal1.getTime()) + "T" + sdf.format(cal2.getTime()) + "+7:00";
        		//}
        		
        		//String[] meta = NegotiationDB.getRenegMeta(contract_id);
        		//String current_state = NegotiationDB.getContractStatus(contract_id);
        		String current_state = NegotiationDB.getContractStatus(contract_id);
        		//String stop_uri;
        		long job_id = NegotiationDB.getJobId(contract_id);
            	 int tag = NegotiationDB.getRenegTag(job_id); 
        		
        		if(current_state.equalsIgnoreCase(NegState.Contracted.toString())){
        			
        			//if(stop_site){
        				//get site from database, send 'stop' to site's corresponding steerer
        				//String endpoint = NegotiationDB.getStopApi(contract_id);
        				//String endpoint = meta[1];
        				//to connect steerer, rather than head node
        			//stop_uri = RestAPI.worker_endpoint.get(meta[1]);
        			//StopConnect.stopConnect(stop_uri);
        				//SteererConnect.connectSteerer(meta[1], username, appname, job_id, meta[2], Integer.parseInt(meta[3]));
        			//}
        			
        			//to update share
        			//OntUpdate.mPolicyShareCloudPartReduce(contract_id, end_time);
        			///OntUpdate.mPolicyShareCompleteReduce(contract_id, end_time);
        			
        			//to deal with if the job contains sub-contracts
        			boolean if_comb = !NegotiationDB.getOfferSub(contract_id).isEmpty();
                 	  if(if_comb){
                 		  //if offer is combined by sub-offers, loop to update
                 		  //fetch combination of offers
                 		  String contracts_comb = NegotiationDB.getOfferSub(contract_id);
                 		  String[] contracts_arr = contracts_comb.split(";");
                 		  for(String contract: contracts_arr){
                 			  long temp_contract_id = Long.parseLong(contract);
                 			  NegotiationDB.updateContractStateEndT(temp_contract_id, NegState.Completed, end_time);

                 			  //to update balances in ontologies
                 			  OntUpdate.mPolicyShareCompleteReduce(temp_contract_id, end_time);
                 			  
                 			  //to call corresponding steerer's 'stop' api
                 		  }
                 		  //to update the state of combined contract
                 		 NegotiationDB.updateContractStateEndT(contract_id, NegState.Completed, end_time);

                 	  }
                 	  //if re-negotiation
                 	  else if(tag == 1){
           				//long job_id = Long.parseLong(form.getFirstValue("contract_id"));
           				long old_contract = NegotiationDB.getOldContract(job_id);
           				
           				//String[] meta = NegotiationDB.getRenegMeta(old_contract);
           				//meta = NegotiationDB.getRenegMeta(old_contract);
           				String old_contract_state = NegotiationDB.getContractStatus(old_contract);
           				//String old_contract_state = NegotiationDB.getContractStatus(old_contract);
           				if(!old_contract_state.equalsIgnoreCase(NegState.ReqTerminated.toString())){
           					
           					//if(stop_site){
           					    //String[] meta1 = NegotiationDB.getRenegMeta(old_contract);
           					    //SteererConnect.connectSteerer(meta1[1], username, appname, job_id, meta1[2], Integer.parseInt(meta1[3]));
           					//}
           					//stop_uri = RestAPI.worker_endpoint.get(meta[1]);
           					//StopConnect.stopConnect(stop_uri);
           					
           				    OntUpdate.mPolicyShareCompleteReReduce(old_contract, end_time);
           				   NegotiationDB.updateContractStateEndT(contract_id, NegState.Completed, end_time);
               		        //NegotiationDB.updateOfferState(old_contract, NegState.Completed, end_time);
           				   
           				    //to call corresponding steerer's 'stop' api
           				}
           			}
                 	
                 	  //basic case, for cluster case as well
                 	  else{
                 		 NegotiationDB.updateContractStateEndT(contract_id, NegState.Completed, end_time);
                 		 OntUpdate.mPolicyShareCompleteReduce(contract_id, end_time);
                 		 //to update balances in ontologies
                 		 
                 		 //if its cluster case, need to call SteerService 'stop' api to stop job execution
                 	     if(appname.equalsIgnoreCase("CompSteering")){
                 	    	//to call SteerService 'stop' api to stop job execution
                 	     }
                 	     //for Junyi's case, but not going to happen.
                 	     /*else{
                 	    	 //to call corresponding steerer's 'stop' api
                 	     }*/
                 		 
                 	  }
                 	  
                 	 
         			//tag 0 is for negotiation
         			//if re-negotiation, which probably would not happen in cluster's case
         			
        			/*long job_id = NegotiationDB.getJobId(contract_id);
        			//String provider = null;
            		if(job_id != 0){
            			long comb_contract_id = NegotiationDB.getOldContract(job_id);
            			OntUpdate.mPolicyShareCompleteReReduce(comb_contract_id, end_time);
            		}*/
        			//to update database: endTime and status
        			//need to calculate for duration (or not)
        			//NegotiationDB.updateContractState(contract_id, NegState.ReqTerminated, end_time);
        		    //NegotiationDB.updateOfferState(contract_id, NegState.ReqTerminated, end_time);
        			
        		    return "ontology";
        		}
        		else{
        			return "error: cannot find required contract.";
        		}
        		//return "ok";
        	}
        	//'complete' is called by SteerService in cluster case
        	//'complete' is called by worker node in Junyi's case
        	if(form.getNames().contains("complete") && form.getNames().contains("contract_id")){
        		System.out.println("************ Im here");
        		//boolean stop_site = false;
        		//to stop the timer set by 'maxDuration'
        		JobSubmissionHandler.timer.cancel();
        		JobSubmissionHandler.timer.purge();
        		System.out.println("TimerTask stopped by job complete");
    	        System.out.println("Timer task stopped by job complete at:"+new Date());
        		
        		//for test case only
        		/*Calendar cal1 = Calendar.getInstance();
    		        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");
    		        Calendar cal2 = Calendar.getInstance();
      	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
             	String end_time = sdf1.format(cal1.getTime()) + "T" + sdf.format(cal2.getTime()) + "+7:00";*/
        		
                // suppose the values from SteererService include end_time;	     		
        		long contract_id = Long.parseLong(form.getFirstValue("contract_id"));
        		//String end_time = form.getFirstValue("end_time");

        			Calendar cal1 = Calendar.getInstance();
        		    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");
        		    Calendar cal2 = Calendar.getInstance();
        		    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        	     	String end_time = sdf1.format(cal1.getTime()) + "T" + sdf.format(cal2.getTime()) + "+7:00";
        		//}
        		
        		String current_state = NegotiationDB.getContractStatus(contract_id);
        		
        		long job_id = NegotiationDB.getJobId(contract_id);
           	    int tag = NegotiationDB.getRenegTag(job_id); 
       		
       		    if(current_state.equalsIgnoreCase(NegState.Contracted.toString())){
       			
       			
       			//to deal with if the job contains sub-contracts
       			    boolean if_comb = NegotiationDB.getOfferSub(contract_id).isEmpty();
                	    if(!if_comb){
                		  //if offer is combined by sub-offers, loop to update
                		  //fetch combination of offers
                		  String contracts_comb = NegotiationDB.getOfferSub(contract_id);
                		  String[] contracts_arr = contracts_comb.split(";");
                		  for(String contract: contracts_arr){
                			  long temp_contract_id = Long.parseLong(contract);
                			  NegotiationDB.updateContractStateEndT(temp_contract_id, NegState.Completed, end_time);

                			  //to update balances in ontologies
                			  OntUpdate.mPolicyShareCompleteReduce(temp_contract_id, end_time);
                		  }
                		  //to update the state of combined contract
                		 NegotiationDB.updateContractStateEndT(contract_id, NegState.Completed, end_time);

                	  }
                	  
                	    else if(tag == 1){
          				//long job_id = Long.parseLong(form.getFirstValue("contract_id"));
          				    long old_contract = NegotiationDB.getOldContract(job_id);
          				
          				    String[] meta = NegotiationDB.getRenegMeta(old_contract);
          				//meta = NegotiationDB.getRenegMeta(old_contract);
          				    String old_contract_state = meta[0];
          				//String old_contract_state = NegotiationDB.getContractStatus(old_contract);
          				    if(!old_contract_state.equalsIgnoreCase(NegState.ReqTerminated.toString())){
          					
          					//if(stop_site){
          					    //String[] meta1 = NegotiationDB.getRenegMeta(old_contract);
          					    //SteererConnect.connectSteerer(meta1[1], username, appname, job_id, meta1[2], Integer.parseInt(meta1[3]));
          					//}
          					//stop_uri = RestAPI.worker_endpoint.get(meta[1]);
          					//StopConnect.stopConnect(stop_uri);
          					
          				        OntUpdate.mPolicyShareCompleteReReduce(old_contract, end_time);
          				        NegotiationDB.updateContractStateEndT(contract_id, NegState.Completed, end_time);
              		        //NegotiationDB.updateOfferState(old_contract, NegState.Completed, end_time);
          				    }
          			  }
                		         
                	  else{
                		 NegotiationDB.updateContractStateEndT(contract_id, NegState.Completed, end_time);
                		 OntUpdate.mPolicyShareCompleteReduce(contract_id, end_time);
                		 //to update balances in ontologies             		 
                	  }
                	  
                	 
        			//tag 0 is for negotiation
        			//if re-negotiation, which probably would not happen in cluster's case
        			
       			/*long job_id = NegotiationDB.getJobId(contract_id);
       			//String provider = null;
           		if(job_id != 0){
           			long comb_contract_id = NegotiationDB.getOldContract(job_id);
           			OntUpdate.mPolicyShareCompleteReReduce(comb_contract_id, end_time);
           		}*/
       			//to update database: endTime and status
       			//need to calculate for duration (or not)
       			//NegotiationDB.updateContractState(contract_id, NegState.ReqTerminated, end_time);
       		    //NegotiationDB.updateOfferState(contract_id, NegState.ReqTerminated, end_time);
       			
       		    return "ontology";
       		}
       		else{
       			return "error: cannot find required contract.";
       		}
        		//return "ok";
        	}
        	//to deal with complete with job_id case
        	if(form.getNames().contains("complete") && form.getNames().contains("job_id")){
        		System.out.println("************ Im here");
        		//boolean stop_site = false;
        		//to stop the timer set by 'maxDuration'
        		JobSubmissionHandler.timer.cancel();
        		JobSubmissionHandler.timer.purge();
        		System.out.println("TimerTask stopped by job complete");
    	        System.out.println("Timer task stopped by job complete at:"+new Date());
    	     
    	        long job_id = Long.parseLong(form.getFirstValue("job_id"));
        		int if_reg = NegotiationDB.getRenegTag(job_id);
        		
        		Calendar cal1 = Calendar.getInstance();
    		    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");
    		    Calendar cal2 = Calendar.getInstance();
    		    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	     	String comp_time = sdf1.format(cal1.getTime()) + "T" + sdf.format(cal2.getTime()) + "+7:00";
    	     	String state;
    	     	long contract = NegotiationDB.getConFromJob(job_id);
    	     	
    	     	 //tag 0 for negotiation, 1 for re-negotiation.
        		if(if_reg == 0){
        			//to update contract/contracts
        			NegFunctions.updateComplete(contract, comp_time);
        		}
        		else{
        			//to update new contracts
        			NegFunctions.updateComplete(contract, comp_time);
        			
        			//to update old contract
               	    long old_con = NegotiationDB.getOldContract(job_id);
               	    NegFunctions.updateComplete(old_con, comp_time);
        		}
        		return "Completed";
        	}
        	
        	if(form.getNames().contains("complete_time") && form.getNames().contains("job_id")){
        		System.out.println("************ Im here");
        		//boolean stop_site = false;
        		//to stop the timer set by 'maxDuration'
        		JobSubmissionHandler.timer.cancel();
        		JobSubmissionHandler.timer.purge();
        		System.out.println("TimerTask stopped by job complete");
    	        System.out.println("Timer task stopped by job complete at:"+new Date());
    	     
    	        long job_id = Long.parseLong(form.getFirstValue("job_id"));
    	        String comp_time = form.getFirstValue("complete_time");
        		int if_reg = NegotiationDB.getRenegTag(job_id);
        	     	
    	     	long contract = NegotiationDB.getConFromJob(job_id);
    	     	String state = NegotiationDB.getContractStatus(contract);
    	     	
    	     	 //tag 0 for negotiation, 1 for re-negotiation.
    			if(state.equalsIgnoreCase(NegState.Contracted.toString())){
        		    if(if_reg == 0){
        			//to update contract/contracts
        			    NegFunctions.updateComplete(contract, comp_time);
        		}
        		    else{
        			    //to update new contracts
        			    NegFunctions.updateComplete(contract, comp_time);
        			
        			    //to update old contract
               	        long old_con = NegotiationDB.getOldContract(job_id);
               	        NegFunctions.updateComplete(old_con, comp_time);
        		}
    			}
    			else{
    				return "Required contract is not in Contrated state";
    			}
        	}
        	
        	//to terminate during re-negotiation, before user sending Quote (Offer)
        	if(form.getNames().contains("neg_id")){
        		long neg_id = Long.parseLong(form.getFirstValue("neg_id"));
        		if(form.getNames().contains("terminate")){
        			String all_offers = NegotiationDB.getNegOffers(neg_id);
		    	    String[] all_offers_arry = all_offers.split(";");
		    	    for(String offer : all_offers_arry){
		    		    long temp_offer_id = Long.parseLong(offer.split("-")[0]);
		    		    NegotiationDB.updateOfferState(temp_offer_id, NegState.Uncontracted);
		    	    }
        		}
        		return "terminated";
        	}
        		else{
        			//return "something wrong";
        			// maybe better to provider more specific error info to user, according the reasons
        			setStatus(Status.CLIENT_ERROR_REQUESTED_RANGE_NOT_SATISFIABLE);
        			return "error: something wrong with your input values";
        			//return ResourceUtil.ThrowErrorMessage("","User does not have enough balance", "AppInstanceResource.java");
        			//return ResourceUtil.ThrowErrorMessage("","No Available resource found", "AppInstanceResource.java");
        		}

    }
    
    
    private String generateRandomJobName(){

		int count = 10; //safety switch
		
		for(int i=0; i < count; i++){
			
			String test_name = UUID.randomUUID().toString();
			
			AppInstance search = AHEEngine.getAppInstanceEntity(test_name);
			
			if(search == null){
				return test_name;
			}

		}
    	
		return null;
    }
    
    private String appinst_prepare(RestArgMap argmap){
    	//String provider = "aws";
		if(argmap.size() > 0){
			
			
			if(argmap.containsKey(rest_arg.appname.toString())){
				//Basic Prepare Command API, 
				//if resource_name is provided. AHE will execute this as if 
				//all properties, data transfer etc are provided correctly
					
				try{
				
					//Executable.writeout("inside appinst_prepare");
					//return "done";
					String jobname = "";
					
					if(!argmap.containsKey(rest_arg.jobname.toString())){
						
						jobname = generateRandomJobName();
						
						if(jobname == null){
							setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
							return ResourceUtil.ThrowErrorMessage("HTTP /POST /appinst","AppInstance Prepare failed : Unable to generate a unique job name. Please manually specify", "UserCommandResource.java");
						}

					}else{
						jobname = argmap.get(rest_arg.jobname.toString());
					}
					
					
					if(getUser() == null){
						setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
						return ResourceUtil.ThrowErrorMessage("HTTP /POST /appinst","User not found in database : " + getUser().getUsername(), "UserCommandResource.java");
					}
					
					
					int cpucount = -1;
					int memory = -1;
					int vmemory = -1;
					int walltimelimit = -1;
					//String workflow_name = "AHEWorkflow";
					String workflow_name = "AwsWorkflow";
					//String workflow_name = "";
					
					// this is to get required amount of resources for an app from registry/hibernate
					// the codes following could be changed if add ontology reasoning here
					// if your info contained in ontology is consistent with ahe3 registry, this is ok
					
					if(argmap.containsKey(rest_arg.cpucount.toString()))
						cpucount = Integer.parseInt(argmap.get(rest_arg.cpucount.toString()));
					
					if(argmap.containsKey(rest_arg.memory.toString()))
						memory = Integer.parseInt(argmap.get(rest_arg.memory.toString()));
					
					if(argmap.containsKey(rest_arg.vmemory.toString()))
						vmemory = Integer.parseInt(argmap.get(rest_arg.vmemory.toString()));
					
					if(argmap.containsKey(rest_arg.walltimelimit.toString()))
						walltimelimit = Integer.parseInt(argmap.get(rest_arg.walltimelimit.toString()));
					
					if(argmap.containsKey(rest_arg.workflow_name.toString()))
						workflow_name = argmap.get(rest_arg.workflow_name.toString());
					
    				AppInstance inst = (AppInstance) AHE_API.Prepare(getUser(), argmap.get(rest_arg.appname.toString()), jobname,workflow_name, cpucount,memory,vmemory,walltimelimit);		  				
                   /* inst_id = inst.getId(); 
    				//long con = 6;
    				Random rand = new Random();
    			    int temp_id = rand.nextInt(10000 + 1);
    				ContractInst coninst = new ContractInst();
    				coninst.setId(temp_id);
    				//coninst.setCon_id(con);
    				coninst.setConId(contract_id);
    				coninst.setInstId(inst_id);
    				
    				NegHibConnect.hibConInst(coninst);*/
    				
    				/*if(provider.equalsIgnoreCase("aws")){
						JobSubmissionHandler.submit_job(inst);
						return "done";
					}*/
    				
    				if(workflow_name.equals("AwsWorkflow")){
    					//return "ok";
    					//Executable.writeout("workflow name checked");
    					AHEEngine.setCurrentAppInstanceState(inst.getId(), AppInstanceStates.Submit_Completed.toString(),"Job Submitted");
    					//****** when user receives this message for AwsWorkflow, it means job starts
    					//System.out.println("***********test where is wrong: " + XMLServerMessageAPI.createStartResponseMessage(null,inst));
    					return XMLServerMessageAPI.createStartResponseMessage(null,inst);
    					//return "hello";
    				}
    				
    				else {
    				if(argmap.containsKey(rest_arg.stagein.toString()) || argmap.containsKey(rest_arg.stageout.toString())){
    				
    					
    					String stagein, stageout = null;
    					
    					
    					  //stagein and stageout should be in the format of ["src","dest"] or [["src1","src2"],["dest_folder"]]
    					
    					
    					String[] stagein_src = new String[0];
    					String[] stagein_dest = new String[0];
    					String[] stageout_src = new String[0];
    					String[] stageout_dest = new String[0];

    					if(argmap.containsKey(rest_arg.stagein.toString())){
    						stagein = argmap.get(rest_arg.stagein.toString());
    						
    						String[][] map = parseStagingString(stagein);
    						stagein_src = map[0];
    						stagein_dest = map[1];
    					}
    					
    					if(argmap.containsKey(rest_arg.stageout.toString())){
    						stageout = argmap.get(rest_arg.stageout.toString());

    						String[][] map = parseStagingString(stageout);
    						stageout_src = map[0];
    						stageout_dest = map[1];
    						
    					}
    					
    	    			//AHE_API.DataStaging(inst.getId(), stagein_src, stagein_dest, stageout_src, stageout_dest);
    					
    				}
    				}
    				
    				AHEEngine.setCurrentAppInstanceState(inst.getId(), AppInstanceStates.Wait_UserCmd.toString(),"Workflow Initiated");
    				
    				if(!argmap.containsKey(rest_arg.resource_name.toString())){
    					return "no resource name";
    					//return XMLServerMessageAPI.createPrepareResponsMessage(null,inst);
    				}else{
    					//System.out.println(inst.getId());
    					//return "done***"+ inst.getId();
    					return appinst_exec(inst.getId(), argmap);
    				}
    				
    			}catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return ResourceUtil.ThrowErrorMessage("HTTP /POST /appinst","AppInstance Prepare failed : " + e.toString(), "UserCommandResource.java");
				}
    			
			}
			
		}else if(!argmap.containsKey(rest_arg.appname.toString()) && argmap.containsKey(rest_arg.workflow_name.toString()) && argmap.containsKey(rest_arg.jobname.toString())){
			//User is preparing a High level workflow (Batch Job type workflow)
			
			try{
				
				if(getUser() == null){
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return ResourceUtil.ThrowErrorMessage("HTTP /POST /appinst","User not found in database : " + getUser().getUsername(), "UserCommandResource.java");
				}
				
				String workflow_name = argmap.get(rest_arg.workflow_name.toString());
			
				//TODO
				
			}catch (Exception e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return ResourceUtil.ThrowErrorMessage("HTTP /POST /appinst","Workflow Prepare failed : " + e.toString(), "UserCommandResource.java");
			}
			
		}
		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return ResourceUtil.ThrowErrorMessage("HTTP /POST /appinst","Prepare Commmand Failed, required parameters/arguments not provided (appname,uri,exec,platform)", "UserCommandResource.java");
    }

    /**
     * 
     * CMD:
     * SetDataStaging
     * monitor
     * status
     * start
     * terminate
     * getProperty
     * setProperty
     * GetData
     * PutData
     * 
     * @return
     */
    
    
    private String get_Parser(){
    	 	
    	long app_inst_id = -1;
    	
    	if(appinst == null){
    		return getListJobsCommands();
    	}
    	
		try {
			app_inst_id = Long.valueOf(appinst);
			return getStatusCommand(app_inst_id);
		} catch (NumberFormatException e) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ResourceUtil.ThrowErrorMessage("",
					"No Valid AppInstance Argument found",
					"AppInstanceResource.java");
		}
    	
    }
    
    private String getListJobsCommands(){
    			
		if(checkAdminAuthorization(getUser()))
			return XMLServerMessageAPI.createAppInstanceListMessage(null);
		else
			return XMLServerMessageAPI.createAppInstanceListMessage(null,getUser().getUsername());
    	
    }
    
    private String delete_Parser(Representation entity){
    	
    	//Ignore User ID for now
//    	if(cmd == null){
//    		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//    		return ResourceUtil.ThrowErrorMessage("","No Command Argument found", "AppInstanceResource.java");
//    	}
//    	
//    	cmd = cmd.trim();
    	
    	long app_inst_id = -1;
    	
    	if(appinst == null){
    		setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    		return ResourceUtil.ThrowErrorMessage("","No AppInstance Argument found", "AppInstanceResource.java");
    	}else{
    		
    		try{
    			app_inst_id = Long.valueOf(appinst);
    		}catch(NumberFormatException e){
    			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    			return ResourceUtil.ThrowErrorMessage("","No Valid AppInstance Argument found", "AppInstanceResource.java");
    		}
    	}
    	
    	setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    	return ResourceUtil.ThrowErrorMessage("","HTTP DELETE: No Valid Command Argument found", "AppInstanceResource.java");
    }
    

    
//    private String SetDataStaginCommand(long app_inst_id, RestArgMap argmap){
//    	
//    	FileStaging[] in = AHEEngine.getFileStageInByAppInstId(app_inst_id);
//    	
//    	for(FileStaging f : in){
//    		f.setActive(0);
//    		HibernateUtil.SaveOrUpdate(f);
//    	}
//    	
//		if(argmap.size() > 0){
//			
//			String stagein = null;
//
//			try{
//							
//				/**
//				 * stagein and stageout should be in the format of ["src","dest"] or [["src1","src2"],["dest_folder"]]
//				 */
//				
//				String[] stagein_src = new String[0];
//				String[] stagein_dest = new String[0];
//
//
//				if(argmap.containsKey(rest_arg.stagein.toString())){
//					stagein = argmap.get(rest_arg.stagein.toString());
//					
//					String[][] map = parseStagingString(stagein);
//					stagein_src = map[0];
//					stagein_dest = map[1];
//				}
//				
//		
//    			AppInstance inst = AHEEngine.getAppInstanceEntity(app_inst_id);
//				
//    			if(inst == null){
//    				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//    				return ResourceUtil.ThrowErrorMessage("/POST /appinst/", "Application Instance does not exist for id : " + app_inst_id, "setDataStaging");
//    			}
////				
////    			String resource_name = inst.getAppreg().getResource().getCommonname();
////    			
////				if(argmap.containsKey(rest_arg.resource_name.toString())){
////				
////					String rn = argmap.get(rest_arg.resource_name.toString());
////
////					Resource r = ResourceRegisterAPI.getResource(rn);
////	
////					if (r == null) {
////						setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
////						return ResourceUtil.ThrowErrorMessage(AHECommands.SetDataStaging.toString(),
////								"Invalid/Non-existant resource selected : "
////										+ rn, "AppInstanceResource.java");
////					}
////				
////					resource_name = r.getCommonname();
////				}
//				
//				
//    			AHE_API.DataStaging(app_inst_id, stagein_src, stagein_dest, new String[0], new String[0]);
//
//    			return XMLServerMessageAPI.createDataStagedResponseMessage(null,inst);
//			
//			}catch(AHEException e){
//				e.printStackTrace();
//				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//				return ResourceUtil.ThrowErrorMessage("", e.toString(), "AppInstanceResource.java");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//				return ResourceUtil.ThrowErrorMessage("", e.toString(), "AppInstanceResource.java");
//			}
//			
//		}else{
//			AppInstance inst;
//			try {
//				inst = AHEEngine.getAppInstanceEntity(app_inst_id);
//				return XMLServerMessageAPI.createDataStagedResponseMessage(null,inst);
//			} catch (AHEException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//				return ResourceUtil.ThrowErrorMessage("", e.toString(), "AppInstanceResource.java");
//			}
//			
//		}
//
//    }
    
//    private String SetDataStageoutCommand(long app_inst_id, RestArgMap argmap){
//    	
//    	//Need to clear old Data stage out information
//    	
//    	
//    	FileStaging[] out = AHEEngine.getFileStageOutByAppInstId(app_inst_id);
//    	
//    	for(FileStaging f : out){
//    		f.setActive(0);
//    		HibernateUtil.SaveOrUpdate(f);
//    	}
//    	
//		if(argmap.size() > 0){
//			
//			String stagein, stageout = null;
//
//			try{
//							
//				/**
//				 * stageout should be in the format of ["src","dest"] or [["src1","src2"],["dest_folder"]]
//				 */
//				
//				String[] stageout_src = new String[0];
//				String[] stageout_dest = new String[0];
//				
//				if(argmap.containsKey(rest_arg.stageout.toString())){
//					stageout = argmap.get(rest_arg.stageout.toString());
//
//					String[][] map = parseStagingString(stageout);
//					stageout_src = map[0];
//					stageout_dest = map[1];
//					
//				}
//				
//    			AppInstance inst = AHEEngine.getAppInstanceEntity(app_inst_id);
//				
//    			if(inst == null){
//    				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//    				return ResourceUtil.ThrowErrorMessage(AHECommands.setStageOut.toString(), "Application Instance does not exist for id : " + app_inst_id, "setDataStaging");
//    			}
////				
////    			String resource_name = inst.getAppreg().getResource().getCommonname();
////    			
////				if(argmap.containsKey(rest_arg.resource_name.toString())){
////				
////					String rn = argmap.get(rest_arg.resource_name.toString());
////
////					Resource r = ResourceRegisterAPI.getResource(rn);
////	
////					if (r == null) {
////						setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
////						return ResourceUtil.ThrowErrorMessage(AHECommands.setStageOut.toString(),
////								"Invalid/Non-existant resource selected : "
////										+ rn, "AppInstanceResource.java");
////					}
////				
////					resource_name = r.getCommonname();
////				}
//				
//				
//    			AHE_API.DataStaging(app_inst_id, new String[0], new String[0], stageout_src, stageout_dest);
//
//    			return XMLServerMessageAPI.createDataStagedResponseMessage(null,inst);
//			
//			}catch(AHEException e){
//				e.printStackTrace();
//				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//				return ResourceUtil.ThrowErrorMessage(AHECommands.setStageOut.toString(), e.toString(), "AppInstanceResource.java");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//				return ResourceUtil.ThrowErrorMessage(AHECommands.setStageOut.toString(), e.toString(), "AppInstanceResource.java");
//			}
//			
//		}else{
//			
//			try{
//			
//    			AppInstance inst = AHEEngine.getAppInstanceEntity(app_inst_id);
//    			
//    			if(inst == null){
//    				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//    				return ResourceUtil.ThrowErrorMessage(AHECommands.setStageOut.toString(), "Application Instance does not exist for id : " + app_inst_id, "getMonitorCommand");
//    			}
//    			
//    			return XMLServerMessageAPI.createDataStagedResponseMessage(null,inst);
//			
//			}catch(AHEException e){
//				e.printStackTrace();
//				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//				return ResourceUtil.ThrowErrorMessage(AHECommands.setStageOut.toString(),e.toString(), "AppInstanceResource.java");
//			}
//		}
//
//    }
//    
//    private String SetDataStagingCommand(long app_inst_id, RestArgMap argmap){
//    	
//    	//Need to clear old Data stage out information
//    	
//    	
//    	FileStaging[] staging = AHEEngine.getFileStageByAppInstId(app_inst_id);
//    	
//    	for(FileStaging f : staging){
//    		f.setActive(0);
//    		HibernateUtil.SaveOrUpdate(f);
//    	}
//    	
//		if(argmap.size() > 0){
//			
//			String stagein, stageout = null;
//
//			try{
//							
//				/**
//				 * stagein and stageout should be in the format of ["src","dest"] or [["src1","src2"],["dest_folder"]]
//				 */
//				
//				String[] stagein_src = new String[0];
//				String[] stagein_dest = new String[0];
//				String[] stageout_src = new String[0];
//				String[] stageout_dest = new String[0];
//
//				if(argmap.containsKey(rest_arg.stagein.toString())){
//					stagein = argmap.get(rest_arg.stagein.toString());
//					
//					String[][] map = parseStagingString(stagein);
//					stagein_src = map[0];
//					stagein_dest = map[1];
//				}
//				
//				if(argmap.containsKey(rest_arg.stageout.toString())){
//					stageout = argmap.get(rest_arg.stageout.toString());
//
//					String[][] map = parseStagingString(stageout);
//					stageout_src = map[0];
//					stageout_dest = map[1];
//					
//				}
//				
//    			AppInstance inst = AHEEngine.getAppInstanceEntity(app_inst_id);
//				
//    			if(inst == null){
//    				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//    				return ResourceUtil.ThrowErrorMessage(AHECommands.SetDataStaging.toString(), "Application Instance does not exist for id : " + app_inst_id, "setDataStaging");
//    			}
////				
////    			String resource_name = inst.getAppreg().getResource().getCommonname();
////    			
////				if(argmap.containsKey(rest_arg.resource_name.toString())){
////				
////					String rn = argmap.get(rest_arg.resource_name.toString());
////
////					Resource r = ResourceRegisterAPI.getResource(rn);
////	
////					if (r == null) {
////						setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
////						return ResourceUtil.ThrowErrorMessage(AHECommands.SetDataStaging.toString(),
////								"Invalid/Non-existant resource selected : "
////										+ rn, "AppInstanceResource.java");
////					}
////				
////					resource_name = r.getCommonname();
////				}
//				
//				
//    			AHE_API.DataStaging(app_inst_id, stagein_src, stagein_dest, stageout_src, stageout_dest);
//
//    			return XMLServerMessageAPI.createDataStagedResponseMessage(null,inst);
//			
//			}catch(AHEException e){
//				e.printStackTrace();
//				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//				return ResourceUtil.ThrowErrorMessage(AHECommands.SetDataStaging.toString(), e.toString(), "AppInstanceResource.java");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//				return ResourceUtil.ThrowErrorMessage(AHECommands.SetDataStaging.toString(), e.toString(), "AppInstanceResource.java");
//			}
//			
//		}else{
//			
//			try{
//			
//    			AHE_API.DataStaging(app_inst_id, new String[0], new String[0], new String[0], new String[0]);
//    			AppInstance inst = AHEEngine.getAppInstanceEntity(app_inst_id);
//    			
//    			if(inst == null){
//    				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//    				return ResourceUtil.ThrowErrorMessage(AHECommands.SetDataStaging.toString(), "Application Instance does not exist for id : " + app_inst_id, "getMonitorCommand");
//    			}
//    			
//    			return XMLServerMessageAPI.createDataStagedResponseMessage(null,inst);
//			
//			}catch(AHEException e){
//				e.printStackTrace();
//				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//				return ResourceUtil.ThrowErrorMessage(AHECommands.SetDataStaging.toString(),e.toString(), "AppInstanceResource.java");
//			}
//		}
//
//    }
    
//    private String getDataStagingCommand(long app_inst_id){
//    	
//		try {
//			AppInstance inst = AHEEngine.getAppInstanceEntity(app_inst_id);
//			return XMLServerMessageAPI.createDataStagingStatusMesage(AHECommands.getDataStaging.toString(), inst);
//		} catch (AHEException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//			return ResourceUtil.ThrowErrorMessage(AHECommands.getDataStaging.toString(),e.toString(), "AppInstanceResource.java");
//		}
//    	
//
//    }
    
//    private String getStageInCommand(long app_inst_id){
//    	
//		try {
//			AppInstance inst = AHEEngine.getAppInstanceEntity(app_inst_id);
//			return XMLServerMessageAPI.createDataStagingStatusMesage(AHECommands.getStageIn.toString(), inst);
//		} catch (AHEException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//			return ResourceUtil.ThrowErrorMessage(AHECommands.getStageIn.toString(),e.toString(), "AppInstanceResource.java");
//		}
//    	
//
//    }
    
//    private String getStageOutCommand(long app_inst_id){
//    	
//		try {
//			AppInstance inst = AHEEngine.getAppInstanceEntity(app_inst_id);
//			return XMLServerMessageAPI.createDataStagingStatusMesage(AHECommands.getStageOut.toString(), inst);
//		} catch (AHEException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
//			return ResourceUtil.ThrowErrorMessage(AHECommands.getStageOut.toString(),e.toString(), "AppInstanceResource.java");
//		}
//    }
    
    private String getStatusCommand(long app_inst_id){

		try{
		
			AppInstance inst = AHEEngine.getAppInstanceEntity(app_inst_id);
	    	
			if(inst == null){
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return ResourceUtil.ThrowErrorMessage("", "Application Instance does not exist for id : " + app_inst_id, "getMonitorCommand");
			}
			
			if(!inst.getState().equalsIgnoreCase(AppInstanceStates.Wait_UserCmd.toString()))
				return XMLServerMessageAPI.createMonitorResponseMessage(null,inst);
			else
				return XMLServerMessageAPI.createPrepareResponsMessage(null,inst); //If the instance is still prior the submit stage, list out the resources available
		
		}catch(AHEException e){
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ResourceUtil.ThrowErrorMessage("Monitor", e.getMessage(), "getMonitorCommand");
		}
		
    }
    
    
    
    private String[] extractJSONSingleArray(String jsonary_str){
    	
		String[] array = new Gson().fromJson(jsonary_str, String[].class);
		return array;
    	
    }
    

    
    private String appinst_exec(final long app_inst_id, RestArgMap argmap){
    	
    	if(argmap.size() > 0){
			//return "hello";
			try{

				AppInstance inst = AHEEngine
						.getAppInstanceEntity(app_inst_id);

				//System.out.println("++++" + String.valueOf(inst.getId()));
				//return String.valueOf(inst.getId());
				if (inst == null) {
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return ResourceUtil.ThrowErrorMessage("HTTP /POST /appinst",
							"Application Instance does not exist for id : "
									+ app_inst_id, "getMonitorCommand");
				}
				
				
				final ArrayList<String> appinst_arg = new ArrayList<String>();
				
    			if(argmap.containsKey(rest_arg.resource_name.toString())){
    				
    				ArrayList<String> keyset = new ArrayList<String>();
    				keyset.addAll(Arrays.asList(argmap.KeySet()));
    				
    				for(String key : keyset){
    					
    					if(!key.equalsIgnoreCase(rest_arg.cpucount.toString()) &&
    						!key.equalsIgnoreCase(rest_arg.walltimelimit.toString()) &&
    						!key.equalsIgnoreCase(rest_arg.memory.toString()) &&
    						!key.equalsIgnoreCase(rest_arg.vmemory.toString()) &&
    	    				!key.equalsIgnoreCase(rest_arg.stagein.toString()) &&
    	    	    		!key.equalsIgnoreCase(rest_arg.stageout.toString())
    						){
    						
	    					String value = argmap.get(key);
	    					
	    					if(value.trim().startsWith("[") && value.endsWith("]")){
	    						
	    						try{
		        					String[] array = extractJSONSingleArray(value);
		        					
		        					for(String a : array){
		        						appinst_arg.add(key);
		        						appinst_arg.add(a);
		        					}
		        					
	    						}catch(Exception e){
		    						appinst_arg.add(key);
		    						appinst_arg.add(value);
	    						}
	    						
	    						
	    					}else{
	    						appinst_arg.add(key);
	    						appinst_arg.add(value);
	    					}
    					
    					}
    					
    				}
    				
    				    				
					final String rn = argmap.get(rest_arg.resource_name.toString());

					Resource r = ResourceRegisterAPI.getResource(rn);

					if (r == null) {
						setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
						return ResourceUtil.ThrowErrorMessage("HTTP /POST /appinst",
								"Invalid/Non-existant resource selected : "
										+ rn, "AppInstanceResource.java");
					}

					AHE_API.start(app_inst_id, rn, appinst_arg.toArray(new String[appinst_arg.size()]));

					//Update Inst Object
					inst = AHEEngine.getAppInstanceEntity(app_inst_id);

					return XMLServerMessageAPI.createStartResponseMessage(null,inst);
			
    				
	    			
    			}else{
    				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    				return ResourceUtil.ThrowErrorMessage("HTTP /POST /appinst","Resource Name not specified in Start Command", "AppInstanceResource.java");
    			}
    			
			}catch(AHEException e){
				e.printStackTrace();
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return ResourceUtil.ThrowErrorMessage("HTTP /POST /appinst",e.toString(), "AppInstanceResource.java");
			}
			
		}else{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return ResourceUtil.ThrowErrorMessage("HTTP /POST /appinst","Resource Name not specified in Start Command", "AppInstanceResource.java");

		}
    	
    }

    /**
     * Retrieve appinst list
     */
    
   /* @Get
    public String toString() {

    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/GET /appinst");
    	}
    	
    	if(appinst != null){
	    	if(!checkAppInstanceOwnership()){
	    		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
	    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("/GET /appinst", getUser().getUsername(), "No Authorization to access this App Instance Resource");
	    		
	    	}
    	}
    	
        return get_Parser();
    }*/
    
    /**
     * Only admin and user owner is allowed to use it
     * @return
     */
    
    private boolean checkAppInstanceOwnership(){

		try {
			
	    	if(getUser() != null){
	    		
	    		if(getUser().getRole().equalsIgnoreCase(UserRoles.admin.toString()))
	    			return true;
	    		else{
	    			
					AppInstance app = AHEEngine.getAppInstanceEntity(Long.valueOf(appinst));
	    			
//					System.out.println(appinst);
//					System.out.println(user);
//					System.out.println(app);
//					System.out.println(app.getOwner().getUsername());
					
	    			if(app != null){
	    				
	    				if(app.getOwner().getId() == getUser().getId())
	    					return true;
	    				else{
	    					setStatus(Status.CLIENT_ERROR_FORBIDDEN);
	    					return false;
	    				}
	    			}
	    				
	    			
	    		}
	    		
	    	}
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AHEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setStatus(Status.CLIENT_ERROR_FORBIDDEN);
    	return false;
    }
    
    /**
     * Staging description must be in the form of:
     * [src1,dest1] for simple 1 file transfer or
     * [[src1,src2,src3],[dest1....]] for multiple transfers
     * 
     * @param stage_str
     * @return
     * @throws Exception 
     */
    
    private String[][] parseStagingString(String stage_str) throws FileTransferSyntaxException{
    	
    	boolean single = true;
    	
    	if(stage_str.replaceAll("[^\\[]", "").length() > 1){
    		single = false;
    	}

    	if(single){
    		
    		String[] array = new Gson().fromJson(stage_str, String[].class);

    		return new String[][]{{array[0]},{array[1]}};
    		
    	}else{
    
    		String[][] array = new Gson().fromJson(stage_str, String[][].class);
    		
    		if(array.length == 2){
    			return array;
    		}else{
    			//throw error
    			throw new FileTransferSyntaxException("Data Staging Format error");
    		}
    		
    	}
    	
    }
  
	@Put
	public String putResource(Representation entity) {

   		
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/PUT /appinst");
    	}
    	
    	if(!checkAppInstanceOwnership()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("/PUT /appinst", getUser().getUsername(), "No Authorization to access this App Instance Resource");
    	}
    	
		
		return "";
	}
	
	/**
	 * terminate appinst if it is in the correct state or else it does nothing
	 * @param entity
	 * @return
	 */

	@Delete
	public String deleteResource(Representation entity) {
		
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/DELETE /appinst");
    	}
    	
    	if(!checkAppInstanceOwnership()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("/DELETE /appinst", getUser().getUsername(), "No Authorization to access this App Instance Resource");
    	}
		
		return delete_Parser(entity);
	}

	/**
	 * Prepare or execute an app_inst depending on the arguments supplied
	 * @param entity
	 * @return
	 * @throws OWLOntologyCreationException 
	 * @throws OWLOntologyStorageException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws NumberFormatException 
	 * @throws AHESecurityException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	
	@Post
	public String postResource(Representation entity) throws OWLOntologyCreationException, OWLOntologyStorageException, NumberFormatException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, AHESecurityException, InterruptedException, IOException {
		//System.out.println("point1");
    	//Check is User and User Certificate is correct (the prompt request for user name and pwd)
		//Form form = new Form(entity);
		//String user = 
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("/POST /appinst");
    	}
    	
    	//SecurityUserAPI.getUser(username)
    	//System.out.println("postResource authentication done.");
    	
    	if(appinst != null){
    		System.out.println("postResource appinst not null.");
	    	/*if(!checkAppInstanceOwnership()){
	    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
	    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("/POST /appinst", getUser().getUsername(), "No Authorization to access this App Instance Resource");
	    	}*/
    	}
    	
    	//System.out.println("postResource done.");
		
		return post_FORM_Parser(entity);
	}
	
/*	@Get
	public String getResource() throws OWLOntologyCreationException {
		System.out.println("point2");
		boolean neg = false;
		
		neg = OntReasoning.accessCheck(user, group, app) &&
				OntReasoning.resourceSearch(user, group, app);
				//ServiceReasoning.providerSearch();
		System.out.println("reasoning result2: " + neg);
		return "reasoning result2: " + neg;
	}*/
	
}