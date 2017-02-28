package uk.ac.ucl.chem.ccs.AHECore.ModuleHandler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import negotiation.Database.NegotiationDB;
import negotiation.Steerer.RedQueenTimer;
import negotiation.Steerer.Redqueen;
import negotiation.Steerer.SteerConnect;
import negotiation.Steerer.SteererTimer;

import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.helper.AwsSocketClient;
import test.helper.Executable;
import test.helper.Helper;
import uk.ac.ucl.chem.ccs.AHECore.Definition.AHEConfigurationProperty;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEMyProxyDelegationException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.ModuleHandlerException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.NoModuleProviderException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.PlatformCredentialException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHEModule.Def.RESOURCE_AUTHEN_TYPE;
import uk.ac.ucl.chem.ccs.AHEModule.Def.ResourceCode;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.AHEMessage;

import com.google.gson.Gson;

public class JobSubmissionHandler {

//	public static void main(String[] arg){
//		
//		ClientResource x = new ClientResource("http://localhost:8113/gram");
//		ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
//		try {
//			x.post("{mewo meowmeow}").write(stream);
//		} catch (ResourceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		System.out.println(stream.toString());
//	}
	
	private static Logger logger = LoggerFactory.getLogger(JobSubmissionHandler.class);
	
	/**
	 * submit a job 
	 * @param user owner
	 * @param appinst application instance
	 * @return AHEMessage. If the call was successful, the information field should be populated with the job submission id. If not successful. The exception array should not be null
	 * @throws ModuleHandlerException
	 * @throws URISyntaxException
	 * @throws ResourceException
	 * @throws IOException
	 * @throws PlatformCredentialException 
	 * @throws AHEMyProxyDelegationException 
	 * @throws NoModuleProviderException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	
	public static String[] paras = new String[3];
	public static Timer timer = new Timer(true);
	public static Timer cluster_timer = new Timer(true);
	public static long timer_job_id;
	public static String deadline;
	
	public static AHEMessage submit_job(AppInstance appinst) throws ModuleHandlerException, URISyntaxException, ResourceException, IOException, PlatformCredentialException, AHEMyProxyDelegationException, NoModuleProviderException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		
		logger.info("Init job submission service call");
		
		String res = appinst.getAppreg().getResource().getResource_interface();
		String site = appinst.getAppreg().getResource().getEndpoint_reference();

		//Executable.writeout("********** inside submit_job: "+ res);
		// to get user+app for connection with steerer service
		String user = appinst.getOwner().getUsername();
		String app = appinst.getAppreg().getAppname();
		
		//String[] paras;
		//String user = "Sofia";
		//String app = "WaterSteering";
		
		URI job_service;
		
		//Submit Job
		
		AHEMessage msg = AHEMessageUtility.generateJobSubmissionMessage(appinst);
		
		String[] provider_info;
		
		if(res.equalsIgnoreCase(ResourceCode.GLOBUS.toString())){
			
			provider_info = ModuleAccess.getModuleProvider().getModuleProperty(AHEConfigurationProperty.module_jobservice_globus.toString());
			job_service = new URI(provider_info[0]);
			
			if(appinst.getAppreg().getResource().getAuthen_type().equalsIgnoreCase(RESOURCE_AUTHEN_TYPE.PROXY_CERTIFICATE.toString())){

				if(!MyProxyHandler.checkMyProxyValidity( msg)){
					MyProxyHandler.generateMyProxyCertificate(msg);
				}
			
			}
			
		}else if(res.equalsIgnoreCase(ResourceCode.UNICORE_UCC.toString())){
			
			provider_info = ModuleAccess.getModuleProvider().getModuleProperty(AHEConfigurationProperty.module_jobservice_ucc.toString());
			job_service = new URI(provider_info[0]);
			
		}else if(res.equalsIgnoreCase(ResourceCode.UNICORE_BES.toString())){
			
			provider_info = ModuleAccess.getModuleProvider().getModuleProperty(AHEConfigurationProperty.module_jobservice_uccbes.toString());
			job_service = new URI(provider_info[0]);
			
		}else if(res.equalsIgnoreCase(ResourceCode.QCG.toString())){
			
			provider_info = ModuleAccess.getModuleProvider().getModuleProperty(AHEConfigurationProperty.module_jobservice_qcg.toString());
			job_service = new URI(provider_info[0]);
		
			if(msg.getCredentials().getProxy_location() != null || (msg.getCredentials().getCredential_location() != null && msg.getCredentials().getPassword() != null)){
				if(!MyProxyHandler.checkMyProxyValidity( msg)){
					MyProxyHandler.generateMyProxyCertificate(msg);
				}
			}
			
		}else if(res.equalsIgnoreCase(ResourceCode.AWS.toString())){
			// to call steerer service Sim_attach api here with parameters user+app
			
			provider_info = ModuleAccess.getModuleProvider().getModuleProperty(AHEConfigurationProperty.module_jobservice_aws.toString());
			// steerer service uri
			job_service = new URI(provider_info[0]);
			// to connect to aws
			//Executable.writeout("Job Submission Handler aws submission succeeded HERE.");
			//AwsSocketClient.callAws(job_service);
			
			//to get inst_id
			long inst_id = appinst.getId();
			System.out.println("instance id: " + inst_id);
			//to connect with steerer service using restlet client
			long contract = NegotiationDB.getContractId(inst_id);
			long job_id = NegotiationDB.getJobId(contract);
			/*String[] results = NegotiationDB.getSteererInfo(contract);
			long job_id = Long.parseLong(results[0]);
			String worker = results[1];
			int core = Integer.parseInt(results[2]);*/
			//long job_id = NegotiationDB.getJobId(contract);
			//long job_id = NegotiationDB.getJobIdFromInst(inst_id);
			System.out.println("contract id fetched in job submission handler: " + contract);
			
			Calendar cal1 = Calendar.getInstance();
		    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");
		    Calendar cal2 = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
       	    String date = sdf1.format(cal1.getTime()) + "T" + sdf.format(cal2.getTime()) + "+7:00";
       	    //NegotiationDB.updateContractStartT(contract, date);
       	    //final String uri = "http://localhost:8070/steerservice";
			//String uri = job_service.toString();
			//int tag = NegotiationDB.getRenegTag(job_id);
			paras[0] = user;
	        paras[1] = app;
	        paras[2] = String.valueOf(contract);
	        
	        //String passedInfo = "";
	        String response;
			//tag 0 for negotiation without core number, tag 1 for re-negotiation with core number
			//if(tag == 0){
	        //boolean if_reneg = NegotiationDB.getRenegTag(job_id) == 0;
	        // if its not re-negotiation
	        /*if(if_reneg){
				//uri for start 
				//String response = SteererConnect.connectSteerer(uri, user, app, job_id, worker, 0);
	        	NegotiationDB.updateContractStartT(contract, date);
       			//String[] results = NegotiationDB.getSteererInfo(contract);
    			String worker = results[0];
    			String endPoint = results[1];
    			int core = Integer.parseInt(results[2]);
       			  //to get steerer uri from database (endPoint)
       			  //String uri_pre = job_service.toString();
       			  //String uri = steerer_uri + "start";
    			//passedInfo = contract + "," + endPoint + "," + worker + "," + core + ";";
    			//System.out.println("********* job submitted for contract: " + offer);
       			//System.out.println("********* neg passed information to SteerService: " + passedInfo);
       			  //response = SteererConnect.connectSteerer(uri, user, app, job_id, worker, core);
			}
			else{*/
				//uri for steering
				//String response = SteererConnect.connectSteerer(uri, user, app, job_id, worker, core);
				
				String offers_comb = NegotiationDB.getOfferSub(contract);
				//String worker;
				  //int core;
				  //String endPoint;
				  
				if(offers_comb.equalsIgnoreCase("")){
					NegotiationDB.updateContractStartT(contract, date);
					
	       			  /*String[] results = NegotiationDB.getSteererInfo(contract);
	    			  worker = results[0];
	    			  endPoint = results[1];
	    			  core = Integer.parseInt(results[2]);*/
	       			  //to get steerer uri from database (endPoint)
	       			  //String uri_pre = job_service.toString();
	    			  //passedInfo = passedInfo + contract + "," + endPoint + "," + worker + "," + core + ";";
				}
				else{  
				  
	       		  String[] offers_arr = offers_comb.split(";");
	       		  for(String offer: offers_arr){
	       			  long temp_cont = Long.parseLong(offer);
	       			  NegotiationDB.updateContractStartT(temp_cont, date);
	       			  /*String[] results = NegotiationDB.getSteererInfo(temp_cont);
	    			  worker = results[0];
	    			  endPoint = results[1];
	    			  core = Integer.parseInt(results[2]);*/
	       			  //to get steerer uri from database (endPoint)
	       			  //String uri_pre = job_service.toString();
	    			  //passedInfo = passedInfo + temp_cont + "," + endPoint + "," + worker + "," + core + ";";
	       			  //response = SteererConnect.connectSteerer(uri, user, app, job_id, worker, core);
	       			  //System.out.println("********* job submitted for contract: " + offer);
	       			//System.out.println("********* re-neg passed information to SteerService: " + passedInfo);
	       		  }
				}
	       		  //passedInfo = passedInfo + "reneg";
			//}
			
			//My instance
			//String uri = job_service.toString();
			String uri = "http://localhost:8070/steerservice";
			//String uri = "http://ec2-52-31-37-191.eu-west-1.compute.amazonaws.com:8080/steering/start";
			
			//System.out.println("@@@@@@@@@@@@@ " + job_service.toString());
			try{
		    //if (response.equalsIgnoreCase("started")){

		            //timer job
			        TimerTask timerTask = new SteererTimer();
	                //running timer task as daemon thread
	        
			        timer.purge();
			        timer.cancel();
			        Timer this_timer = new Timer();
			        timer = this_timer;
	                //System.out.println("***begin Timer task for contract " + contract + " started at:"+new Date());
	                //Helper.writeout("***begin TimerTask started at:"+new Date());
	                this_timer.schedule(timerTask, 0);
	                //Helper.writeout("***end TimerTask started at:"+new Date());
	                //System.out.println("***end Timer task for contract " + contract + " started at:"+new Date());
	                ///timer.scheduleAtFixedRate(timerTask, 0, 10*1000);
	                //System.out.println("TimerTask started***");
	                //cancel after sometime
	            try {
	                Thread.sleep(30000);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        //timer.cancel();
		    	}
		    //}
		    catch(ResourceException e){
		    	//ResourceException e = null;
		    	AHEMessage exception = new AHEMessage();
				exception.setException(new String[]{e.getStatus() + " : " + e.getMessage()});
				return exception;
		    }
	        //System.out.println("TimerTask stopped");
	        //System.out.println("Timer task stopped at:"+new Date());
		    
			String[] info = {"00005"};
			AHEMessage awsResponse = new AHEMessage();
			awsResponse.setInformation(info);
			//awsResponse.setStatus("ok");
			
			//URI id = new URI(job_service.toString());
			
			// uri here is for job submission to Steerer service 'attach'
			URI id = new URI(uri);

			AHEEngine.setAppInstanceJobID(appinst, id.toString());
			//Helper.writeout(uri + "@@@@@@" + user + "@@@@@@" + app);
			return awsResponse;
		}
		else if(res.equalsIgnoreCase(ResourceCode.REDQUEEN.toString())){
			// to call steerer service Sim_attach api here with parameters user+app
			//System.out.println("***************************");
			provider_info = ModuleAccess.getModuleProvider().getModuleProperty(AHEConfigurationProperty.module_jobservice_redqueen.toString());
			// steerer service uri
			job_service = new URI(provider_info[0]);
			// to connect to aws
			//Executable.writeout("Job Submission Handler aws submission succeeded HERE.");
			//AwsSocketClient.callAws(job_service);
			
			//to get inst_id
			long inst_id = appinst.getId();
			System.out.println("instance id: " + inst_id);
			//to connect with steerer service using restlet client
			long contract = NegotiationDB.getContractId(inst_id);
			long job_id = NegotiationDB.getJobId(contract);
			deadline = NegotiationDB.getDeadline(contract);
			paras[0] = user;
	        paras[1] = app;
	        paras[2] = String.valueOf(contract);
			// to insert broker_job_id
			NegotiationDB.insertBrokerJobId(job_id);
			
			/*String[] results = NegotiationDB.getSteererInfo(contract);
			long job_id = Long.parseLong(results[0]);
			String worker = results[1];
			int core = Integer.parseInt(results[2]);*/
			//long job_id = NegotiationDB.getJobId(contract);
			//long job_id = NegotiationDB.getJobIdFromInst(inst_id);
			System.out.println("Contract id fetched in job submission handler: " + contract);
			
       	    //NegotiationDB.updateContractStartT(contract, date);
       	    //final String uri = "http://localhost:8070/steerservice";
			//String uri = job_service.toString();
			//int tag = NegotiationDB.getRenegTag(job_id);
			/*paras[0] = user;
	        paras[1] = app;
	        paras[2] = String.valueOf(contract);
				
				String offers_comb = NegotiationDB.getOfferSub(contract);
				String passedInfo = "";
				String worker = "";
				  //int core;
				  //String endPoint;
				  
				if(offers_comb.equalsIgnoreCase("")){
					NegotiationDB.updateContractStartT(contract, date);
					passedInfo = passedInfo + contract + "," + worker + ";";
				}
				else{  
				  
	       		  String[] offers_arr = offers_comb.split(";");
	       		  for(String offer: offers_arr){
	       			  long temp_cont = Long.parseLong(offer);
	       			  NegotiationDB.updateContractStartT(temp_cont, date);
	       			  passedInfo = passedInfo + temp_cont + "," + worker + ";";

	       		  }
				}
				passedInfo = job_id + "!" + passedInfo;
				System.out.println("========= passedInfo in JobSubmission: " + passedInfo);*/
	       		  //passedInfo = passedInfo + "reneg";
			//}
			
			
				//to communicate with SteerService to start job execution in cluster
				
			//My instance
			//String uri = job_service.toString();
			String uri = "http://localhost:8070/steerservice";
			//String uri = "http://ec2-52-31-37-191.eu-west-1.compute.amazonaws.com:8080/steering/start";
			
			//to get numjobs and nefold from database
			/*String[] submit_data = new String[3];
			submit_data = NegotiationDB.getRedQueenSubmission(contract);
			String numjobs = submit_data[0];
			String nefold = submit_data[1];
			int service_level = Integer.parseInt(submit_data[2]);*/
			//int service_level;
			//String numjobs = "5";
			//String nefold = "12";
			
			//to submit job to redqueen via scripts, it also copies job_unread.txt to broker instance
			/*final String command = "/opt/test/job_submit_broker_expect.sh " + numjobs + " " + nefold + " " + job_id + " " + "1" ;
			System.out.println("%%%%%%%%%%% submitted command: " + command);
			Runtime rt = Runtime.getRuntime();
			rt.exec(command);*/
			timer_job_id = job_id;
			//this timer is for waiting for fetching redqueen_job_id
			TimerTask timerTask = new RedQueenTimer();
			cluster_timer.purge();
			cluster_timer.cancel();
			
			Timer this_timer = new Timer();
			cluster_timer = this_timer;
			
			this_timer.schedule(timerTask, 0);
			
			try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			//to store broker_job_id and redqueen_job_id into database
			//Redqueen.getRedqueenJobId(job_id);
			
			
			//System.out.println("@@@@@@@@@@@@@ " + job_service.toString());
			//this timer is for deadline timer where deadline is specified by a user
			TimerTask deadline_timerTask = new SteererTimer();
            //running timer task as daemon thread
    
	        timer.purge();
	        timer.cancel();
	        Timer deadline_timer = new Timer();
	        timer = deadline_timer;
            //System.out.println("***begin Timer task for contract " + contract + " started at:"+new Date());
            //Helper.writeout("***begin TimerTask started at:"+new Date());
            deadline_timer.schedule(deadline_timerTask, 0);
            //Helper.writeout("***end TimerTask started at:"+new Date());
            //System.out.println("***end Timer task for contract " + contract + " started at:"+new Date());
            ///timer.scheduleAtFixedRate(timerTask, 0, 10*1000);
            //System.out.println("TimerTask started***");
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			
			/*try{
		    //if (response.equalsIgnoreCase("started")){

		            //timer job
			        TimerTask timerTask = new SteererTimer();
	                //running timer task as daemon thread
	        
			        timer.purge();
			        timer.cancel();
			        Timer this_timer = new Timer();
			        timer = this_timer;
	                System.out.println("***begin Timer task for contract " + contract + " started at:"+new Date());
	                //Helper.writeout("***begin TimerTask started at:"+new Date());
	                this_timer.schedule(timerTask, 0);
	                //Helper.writeout("***end TimerTask started at:"+new Date());
	                System.out.println("***end Timer task for contract " + contract + " started at:"+new Date());
	                ///timer.scheduleAtFixedRate(timerTask, 0, 10*1000);
	                System.out.println("TimerTask started***");
	                //cancel after sometime
	            try {
	                Thread.sleep(30000);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        //timer.cancel();
		    	}
		    //}
		    catch(ResourceException e){
		    	//ResourceException e = null;
		    	AHEMessage exception = new AHEMessage();
				exception.setException(new String[]{e.getStatus() + " : " + e.getMessage()});
				return exception;
		    }*/
	        //System.out.println("TimerTask stopped");
	        //System.out.println("Timer task stopped at:"+new Date());
		    
			String[] info = {"00005"};
			AHEMessage awsResponse = new AHEMessage();
			awsResponse.setInformation(info);
			//awsResponse.setStatus("ok");
			
			//URI id = new URI(job_service.toString());
			
			// uri here is for job submission to Steerer service 'attach'
			URI id = new URI(uri);

			AHEEngine.setAppInstanceJobID(appinst, id.toString());
			//Helper.writeout(uri + "@@@@@@" + user + "@@@@@@" + app);
			return awsResponse;
		}
		else if(res.equalsIgnoreCase(ResourceCode.UNICORE_TESTGRID.toString())){
			// to call steerer service Sim_attach api here with parameters user+app
			
			//provider_info = ModuleAccess.getModuleProvider().getModuleProperty(AHEConfigurationProperty.module_jobservice_aws.toString());
			// steerer service uri
			//job_service = new URI(provider_info[0]);
			// to connect to aws
			//Executable.writeout("Job Submission Handler aws submission succeeded HERE.");
			//AwsSocketClient.callAws(job_service);
			
			//to get inst_id
			long inst_id = appinst.getId();
			
			//to connect with steerer service using restlet client
			//long contract = NegotiationDB.getContractId(inst_id);
			long contract = 1;
			
			String executable = appinst.getAppreg().getExecutable();
			//String uri = job_service.toString();
			String site_c = " -s " + site; ;
			/*String command = "/Applications/MyApp/unicore-ucc-7.4.0/bin/ucc run -c " +
					"/Users/zeqianmeng/./testgrid-preferences -v /Applications/MyApp/unicore-ucc-7.4.0/samples/date.u";*/
			String command = "/Applications/MyApp/unicore-ucc-7.4.0/bin/ucc run -c " +
					"/Users/zeqianmeng/./testgrid-preferences -v " + executable;
			//System.out.println("@@@@@@@@@@@@@ " + job_service.toString());
			//SteererConnect.connectSteerer(uri, user, app, contract);
			try {
				
			    //String command = "ls /opt/AHE3";
			    Runtime rt = Runtime.getRuntime();
			    Process pr = rt.exec(command);
		        //Process pr = rt.exec("ls /opt/test/ && sudo chmod 777 -R /opt/test/");
		    
		        String s = null;
		    
		        BufferedReader stdInput = new BufferedReader(new
	                InputStreamReader(pr.getInputStream()));
		    
		        System.out.println("Here is the standard output of the command:\n");
	            while ((s = stdInput.readLine()) != null) {
	                System.out.println(s);
	            }
			    System.out.println("********");
	            System.exit(0);	            
			    }
			
			catch (IOException e) {
	            System.out.println("*****exception happened when submission to unicore testgrid- here's what I know: ");
	            e.printStackTrace();
	            System.exit(-1);
	        }
			String[] info = {"00005"};
			AHEMessage awsResponse = new AHEMessage();
			awsResponse.setInformation(info);
			//awsResponse.setStatus("ok");
			
			//URI id = new URI(job_service.toString());
			
			// uri here is for job submission to Steerer service 'attach'
			//URI id = new URI(uri);
			String id = "";

			AHEEngine.setAppInstanceJobID(appinst, id);
			//Helper.writeout(uri + "@@@@@@" + user + "@@@@@@" + app);
			return awsResponse;
		}
		else{
			//NOT Supported
			logger.error("Job Submission type not supported : " + res);
			throw new ModuleHandlerException("Job Submission type not supported : " + res);
		}
		

		
		ClientResource x = new ClientResource(job_service.toString());
		
		if(provider_info.length > 1){
			//Username & Password is used
			x.setChallengeResponse(ChallengeScheme.HTTP_BASIC, provider_info[1], provider_info[2]);
		}
		
		ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
		
		Gson gson = new Gson();
		
		try{
		
			x.post(gson.toJson(msg)).write(stream);
			//to create an AHEMessage object from the message returned
			AHEMessage response = gson.fromJson(stream.toString(), AHEMessage.class);	
			
			URI id = new URI(job_service.toString() + "/" + response.getInformation()[0]);

			//store returned job id
			AHEEngine.setAppInstanceJobID(appinst, id.toString());
			
			return response;
		
		}catch(ResourceException e){
			
			AHEMessage exception = new AHEMessage();
			exception.setException(new String[]{e.getStatus() + " : " + e.getMessage()});
			return exception;
			
		}finally{		
			x.release();
		}
		
	}
	
	/**
	 * Check the job status 
	 * @param user owner
	 * @param appinst application instance
	 * @return AHEMessage. If the call was successfull, the information field should be populated with the job status. If not successful. The exception array should not be null
	 * @throws ModuleHandlerException
	 * @throws URISyntaxException
	 * @throws ResourceException
	 * @throws IOException
	 * @throws PlatformCredentialException 
	 * @throws AHEMyProxyDelegationException 
	 */
	
	public static AHEMessage job_status(AppInstance appinst) throws ModuleHandlerException, URISyntaxException, ResourceException, IOException, PlatformCredentialException, AHEMyProxyDelegationException{
		
		logger.info("Init job status check service call");
		
		AHEMessage msg = AHEMessageUtility.generateJobStatusMessage(appinst);
		
		URI id = new URI(appinst.getSubmit_job_id());
		
		String[] path = id.getPath().split("/");
		
		String new_path = "";
		
		for(int i=0; i <path.length-1; i++){
			new_path += path[i] + "/";
		}
		
		String job_service = id.getScheme()+"://"+id.getAuthority() + new_path;

		
		ClientResource x = new ClientResource(job_service.toString() +"job");
		
		String[] cred = ModuleAccess.getModuleProvider().searchModuleCredentialByURI(job_service.toString());

		if(cred.length > 0){
			x.setChallengeResponse(ChallengeScheme.HTTP_BASIC, cred[0], cred[1]);
		}
		
		ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
		
		try{
		
			Gson gson = new Gson();
			
			x.post(gson.toJson(msg)).write(stream);
			
			AHEMessage response = gson.fromJson(stream.toString(), AHEMessage.class);
			return response;
			
		}catch(ResourceException e){
			
			AHEMessage exception = new AHEMessage();
			exception.setException(new String[]{e.getStatus() + " : " + e.getMessage()});
			
			return exception;
			
		}finally{
		
			x.release();
		}

		
	}
	
	/**
	 * Terminate application instance
	 * @param appinst application instance
	 * @return AHEMessage object with the details
	 * @throws ModuleHandlerException
	 * @throws URISyntaxException
	 * @throws ResourceException
	 * @throws IOException
	 * @throws PlatformCredentialException
	 * @throws AHEMyProxyDelegationException
	 */
	
	public static AHEMessage terminate(AppInstance appinst) throws ModuleHandlerException, URISyntaxException, ResourceException, IOException, PlatformCredentialException, AHEMyProxyDelegationException{
		
		logger.info("Init job termination service call");
		
		AHEMessage msg = AHEMessageUtility.generateJobStatusMessage(appinst);
		
		URI id = new URI(appinst.getSubmit_job_id());
		
		String[] path = id.getPath().split("/");
		
		String new_path = "";
		
		for(int i=0; i <path.length-1; i++){
			new_path += path[i] + "/";
		}
		
		String job_service = id.getScheme()+"://"+id.getAuthority() + new_path;

		Gson gson = new Gson();
		ClientResource x = new ClientResource(job_service.toString() +"/job"+"/"+gson.toJson(msg));
		
		String[] cred = ModuleAccess.getModuleProvider().searchModuleCredentialByURI(job_service.toString());

		if(cred.length > 0){
			x.setChallengeResponse(ChallengeScheme.HTTP_BASIC, cred[0], cred[1]);
		}
		
		ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
		
		try{
			x.delete().write(stream);
		
			AHEMessage response = gson.fromJson(stream.toString(), AHEMessage.class);
			
			return response;
		
		}catch(ResourceException e){
			
			AHEMessage exception = new AHEMessage();
			exception.setException(new String[]{e.getStatus() + " : " + e.getMessage()});
			
			return exception;
			
		}finally{
		
			x.release();
		}
		
		
	}
	
}