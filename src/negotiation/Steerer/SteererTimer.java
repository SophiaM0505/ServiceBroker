package negotiation.Steerer;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import negotiation.Database.NegotiationDB;
import negotiation.Negotiation.NegState;
import negotiation.Ontology.OntUpdate;

import test.helper.Executable;
import test.helper.Helper;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.JobSubmissionHandler;


public class SteererTimer extends TimerTask {
 
	static long duration_ms;
	final String post = ":8080/steering/managerStop";
	//final String pre = "http://";
    @Override
    public void run() {
    	
    	//final String uri = "http://ec2-52-17-86-19.eu-west-1.compute.amazonaws.com:8080/steering/managerStop";
    	//final String uri = "http://ec2-52-31-37-191.eu-west-1.compute.amazonaws.com:8080/steering/managerStop";
    	//String uri = "http://ec2-52-50-140-112.eu-west-1.compute.amazonaws.com:8080/steering/managerStop";
        //String uri = JobSubmissionHandler.paras[0];
        String user = JobSubmissionHandler.paras[0];
        String app = JobSubmissionHandler.paras[1];
        long contract_id = Long.parseLong(JobSubmissionHandler.paras[2]);

        //Helper.writeout("the info passed to SteererTimer: " + user + " ; " + app + " ; " + contract_id);
        
        //to get maxCost and maxDuration from database
        String[] data = new String[4];
        try {
        	//to get job_id as well
			data = NegotiationDB.getContractPayments(contract_id);
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        //duration_ms = 180 * 1000;
        //duration_s = max_duration * 180 * 1000;
        //pass these values for ontology update
        //double max_cost = Double.parseDouble(data[1]);
        //String worker = data[2];
        //String share = data[3];
        long job_id = Long.parseLong(data[1]);
        long old_contract = 6;
        long max_duration;
        
        //if job_id == 0
        //String policy = share.substring(0, share.length()-5) + "Policy";
        
        System.out.println("Timer task in SteererTimer for contract " + contract_id + " started at: "+new Date());
        //Helper.writeout("Timer task in SteererTimer started at:"+new Date());
        //completeTask();
        //Helper.writeout("Timer task in SteererTimer finished at:"+new Date());
        //Executable.writeout("Timer task in SteererTimer finished at:"+new Date());
        System.out.println("Timer task in SteererTimer for contract " + contract_id + " finished at: "+new Date()); 
        
        // to cancel timer
        JobSubmissionHandler.timer.cancel();
        JobSubmissionHandler.timer.purge();
        
        Calendar cal1 = Calendar.getInstance();
	    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");
	    Calendar cal2 = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
     	String stop_date = sdf1.format(cal1.getTime()) + "T" + sdf.format(cal2.getTime()) + "+7:00";
        //to search JobContract if include new contract id
        //if not, then its re-neg, then nothing below happens       
        
      //System.out.println("" + user + " ; " + app + " ; " + contract);
     	// uri here is for sending 'stop' to SteererService to stop steered app
        
        //boolean update = false;
        if(app.equalsIgnoreCase("WaterSteering")){
        	try {
    			//old_contract = NegotiationDB.getJobId(contract_id);
    			old_contract = NegotiationDB.getOldContract(job_id);
    		} catch (InstantiationException e2) {
    			// TODO Auto-generated catch block
    			e2.printStackTrace();
    		} catch (IllegalAccessException e2) {
    			// TODO Auto-generated catch block
    			e2.printStackTrace();
    		} catch (ClassNotFoundException e2) {
    			// TODO Auto-generated catch block
    			e2.printStackTrace();
    		} catch (SQLException e2) {
    			// TODO Auto-generated catch block
    			e2.printStackTrace();
    		} catch (InterruptedException e2) {
    			// TODO Auto-generated catch block
    			e2.printStackTrace();
    		}
        	//String response = SteererConnect.connectSteerer(uri, user, app, contract_id);
        	//String response = "stopped";
        	max_duration = Long.parseLong(data[0]);
            System.out.println("max duration fetched is: " + max_duration);
            duration_ms = 120 * 1000;
            completeTask();
        	//if(!response.equalsIgnoreCase("stopped")){
        		//for Cloud's case, does not need to update when max reaches
        		//but need to update contract state
        	  //  update = false;
        	//}
            String steerer_uri;
            
        	//if 'stopped' returned, update ontologies: user's balance in Policy and group's balance in Share with 'maxCost'
        	try {
            	//to update group's balance in Share with 'maxCost'

                //if its re-negotiation
                if(old_contract != contract_id){
            		//get update sub-offers
                	
            		String offers_comb = NegotiationDB.getOfferSub(contract_id);
           		    String[] offers_arr = offers_comb.split(";");
           		    for(String offer: offers_arr){
           		      System.out.println("******* sub offer id: " + offer);
           			  //long temp_con_id = Long.parseLong(offer);
        		      //OntUpdate.mPolicyShareCompleteReduce(temp_con_id, stop_date);
        		      NegotiationDB.updateOfferState(contract_id, NegState.ReqTerminated, stop_date);
            	      String uri_pre = NegotiationDB.updateContractStateComp(contract_id, NegState.ReqTerminated, stop_date);
            	      // to send stop command to steerer
            	      steerer_uri = uri_pre + post;
            	      StopConnect.stopConnect(steerer_uri);
           		    }
           		    
           		    //update the combined contract of sub-contracts' state
           		    NegotiationDB.updateOfferState(contract_id, NegState.ReqTerminated, stop_date);
					NegotiationDB.updateContractStateEndT(contract_id, NegState.ReqTerminated, stop_date);
        		    System.out.println("-------complete balance reduced");
        		    String old_contract_state = null;
        			try {
        				System.out.println("====== job id: " + old_contract);
        				//old_contract = NegotiationDB.getOldContract(job_id);
        				System.out.println("====== old contract number: " + old_contract);
        				old_contract_state = NegotiationDB.getContractStatus(old_contract);
        			} catch (InstantiationException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (IllegalAccessException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (ClassNotFoundException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} catch (SQLException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		    if(!old_contract_state.equalsIgnoreCase(NegState.ReqTerminated.toString())){
        				//String contract_status = NegotiationDB.getContractStatus(old_contract);
        				//if(contract_status.equalsIgnoreCase(NegState.Contracted.toString())){
        				    try {
        						//OntUpdate.mPolicyShareCompleteReReduce(old_contract, stop_date);
        						NegotiationDB.updateOfferState(old_contract, NegState.ReqTerminated, stop_date);
        						String uri_pre = NegotiationDB.updateContractStateComp(contract_id, NegState.ReqTerminated, stop_date);
        	            	      // to send stop command to steerer
        	            	      steerer_uri = uri_pre + post;
        	            	      StopConnect.stopConnect(steerer_uri);
        						System.out.println("-------complete balance for renegotiation reduced");
        					} catch (InstantiationException e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
        					} catch (IllegalAccessException e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
        					} catch (ClassNotFoundException e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
        					} catch (SQLException e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
        					}
        				//}
        			}
                }
                //to deal with negotiation case
                else{
                	int tag = NegotiationDB.getRenegTag(job_id);
                	if(tag == 0){

                	      //OntUpdate.mPolicyShareCompleteReduce(contract_id, stop_date);
                	      NegotiationDB.updateOfferState(contract_id, NegState.ReqTerminated, stop_date);
                	      String uri_pre = NegotiationDB.updateContractStateComp(contract_id, NegState.ReqTerminated, stop_date);
                	      // to send stop command to steerer
                	      steerer_uri = uri_pre + post;
                	      StopConnect.stopConnect(steerer_uri);
               		    
                	}
                }
        		//
        		//fetched_job_id = NegotiationDB.getJobId(contract_id);
        	
        		/*if(job_id != 0){
        			//update for old contract
        			long old_contract = NegotiationDB.getOldContract(job_id);
        			//need to update user's balance?
        			// need, but no need to rebound his balance with max before reducing the actual cost
        			OntUpdate.mPolicyShareCompleteReReduce(old_contract, stop_date);
        		}*/
        		
    			//OntUpdate.mPolicyCloudReduce(max_cost, policy, user);
    		} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
     	if(app.equalsIgnoreCase("CompSteering")){
     		//to calculate the count down value from deadline
			String deadline = null;
			try {
				deadline = NegotiationDB.getDeadline(contract_id);
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//Calendar cal1 = Calendar.getInstance();
		    //SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");
		    //Calendar cal2 = Calendar.getInstance();
	        //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	        String current_time = sdf.format(cal2.getTime());
	        int hour = Integer.parseInt(convert(current_time.substring(0, 2)));
	        int min = Integer.parseInt(convert(current_time.substring(3, 5)));
	        int sec = Integer.parseInt(convert(current_time.substring(6, 8)));
       	    //String date = sdf1.format(cal1.getTime()) + "T" + sdf.format(cal2.getTime()) + "+7:00";
	        System.out.println("************** deadline: " + deadline);
	        int d_hour = Integer.parseInt(convert(deadline.substring(11, 13)));
	        int d_min = Integer.parseInt(convert(deadline.substring(14, 16)));
	        int d_sec = Integer.parseInt(convert(deadline.substring(17, 19)));
	        
	        long count_down;
	        int count_s;
	        int count_m;
	        int count_h;
	        if(d_sec < sec){
	        	count_s = d_sec + 60 - sec;
	        	//System.out.println("count_s " + count_s);
	        	count_m = d_min - 1;
	        	//System.out.println("count_m " + count_m);
	        	if(d_min < min){
	        		count_m = count_m + 60 - min;
	        		//System.out.println("d_min" + d_min);
	        		//System.out.println("min " + min);
	        		count_h = d_hour - 1 - hour;
	        		//System.out.println("1 count_m " + count_m);
	        		//System.out.println("1 count_h " + count_h);
	        	}
	        	else{
	        		count_m = d_min - 1 - min;
	        		count_h = d_hour - hour;
	        		//System.out.println("2 count_m " + count_m);
	        		//System.out.println("2 count_h " + count_h);
	        	}
	        }
	        else{
	        	count_s = d_sec - sec;
	        	if(d_min < min){
	        		count_m = d_min + 60 - min;
	        		count_h = d_hour - 1 - hour;
	        	}
	        	else{
	        		//d_min >= min
	        		count_m = d_min - min;
	        		count_h = d_hour - hour;
	        	}
	        }
	        count_down = count_s + count_m*60 + count_h*3600;
	        duration_ms = count_down * 1000;
	        completeTask();
     		try {
     			//if return true, meaning job completed, no need to update ontologies
     			    //it would be updated by broker calling programs in desktop/redqueen
     			//if return false, meaning job not completed, but as deadline reaches, need to kill the job
     			    //but no need to update ontologies, as dont know how many resources consumed, would be killed by broker 
     			      //fetching file as well
				if(Redqueen.checkRedqueenComplete(job_id)){
					NegotiationDB.updateContractStateTermi(contract_id, NegState.Completed, stop_date);
					//update = false;
					//return;
				}
				else{
					NegotiationDB.updateContractStateTermi(contract_id, NegState.Terminated, stop_date);
					//update = false;
				}
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     	}
     	//long fetched_job_id = 0;
        //initially, for this use case, no 'duration' data is inserted 	
     	
        //if(update){
        //if its re-negotiation
        //if(old_contract != contract_id){
        // to send 'stop' command to SteererService
        //SteererConnnect.connectSteerer with stop uri
        	
        	//if 'stopped' returned, update database: offer and contract state to 'ReqTerminated', and 'duration' to 'maxDuration'

        //}
		
    //}
    }
 
    private void completeTask() {
        try {
            //assuming it takes 20 secs to complete the task
            Thread.sleep(duration_ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static String convert(String b){
		String a = "";
		if(b.startsWith("0")){
			a = b.substring(1);
		}
		else{
			a = b;
		}
		return a;
	}
     
   /* public static void main(String args[]){
        TimerTask timerTask = new SteererTimer();
        //running timer task as daemon thread
        Timer timer = new Timer(true);
        System.out.println("***begin Timer task started at:"+new Date());
        timer.schedule(timerTask, 0);
        System.out.println("***end Timer task started at:"+new Date());
        ///timer.scheduleAtFixedRate(timerTask, 0, 10*1000);
        System.out.println("TimerTask started***");
        //cancel after sometime
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timer.cancel();
        System.out.println("TimerTask stopped");
        System.out.println("Timer task stopped at:"+new Date());
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/
 

}
