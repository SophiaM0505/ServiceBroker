package uk.ac.ucl.chem.ccs.AHECore.API.Rest;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.jws.WebService;

import negotiation.Steerer.AccountingTimer;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.util.Series;

//import test.helper.Helper;
import test.helper.Helper;
import uk.ac.ucl.chem.ccs.AHECore.API.AHE_API;
import uk.ac.ucl.chem.ccs.AHECore.Definition.AHEConfigurationProperty;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AppRegisteryAPI;
import uk.ac.ucl.chem.ccs.AHECore.Engine.ResourceRegisterAPI;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.WorkflowDescription;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.JobSubmissionHandler;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.ModuleAccess;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHECore.Security.AuthenCode;
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.WorkflowAPI;
import uk.ac.ucl.chem.ccs.AHEModule.Def.AHE_SECURITY_TYPE;
import uk.ac.ucl.chem.ccs.AHEModule.Def.RESOURCE_AUTHEN_TYPE;
import uk.ac.ucl.chem.ccs.AHEModule.Def.ResourceCode;



/**
 * This class contains the necessary code to run a RESTFul web service.
 * 
 * Using Restlet, a Rest can be packaged as
 * 1) Servlet
 * 2) Standalone installation <- easiest
 * 
 * TODO: Need to do a Client API
 * 
 * Current Message Parsing
 * 1) Use URI for now
 * 2) Use XML messaging later
 * 
 * *** Expert User
 * GET, POST, DELETE
 * http://ahe/USER/userid/CMD/cmd/ARG/argument
 * 
 * /w XML instead of argument
 * 
 * 
 * *** Normal User non-related to specific AppInstance
 * GET, POST, DELETE
 * http://ahe/USER/userid/CMD/cmd/ARG/argument
 * 
 * /w XML
 * 
 * *** AppInstance Functions
 * GET, POST, DELETE
 * http://ahe/USER/userid/APPINST/app_instance_id/CMD/cmd/ARG/argument 
 *
 * /w XML
 * 
 * @author davidc
 *
 */

//@WebService(targetNamespace = "https://locahost/", portName = "8443", serviceName = "AHE3")
//(targetNamespace = "https://locahost:8443/", portName = "RestAPIPort", serviceName = "RestAPIService")
// (targetNamespace = "http://Rest.API.AHECore.ccs.chem.ucl.ac.uk/", portName = "RestAPIPort", serviceName = "RestAPIService")
// (targetNamespace = "http://localhost/", portName = "8080", serviceName = "Core")
public class RestAPI {

	public static Map<String, String[]> app_shares = new HashMap<String, String[]>();
	public static Map<String, String> share_policy = new HashMap<String, String>();
	public static Map<String, String> worker_endpoint = new HashMap<String, String>();
	
	public static void main(String[] arg){
		
		try{
		
			String[] shares_steering = new String[1];
			shares_steering[0] = "CloudShare";
		    //shares_steering[0] = "CloudShare";
			//shares_steering[1] = "ClusterShare";
			app_shares.put("WaterSteering", shares_steering);
			//System.out.println("************shares for watersteering: " + app_shares.get("WaterSteering")[0]);
			//app_shares.put("WaterSteering", value)
			
			String[] shares_cluster = new String[1];
			shares_cluster[0] = "ClusterShare";
			app_shares.put("CompSteering", shares_cluster);
			
			share_policy.put("ClusterShare", "ClusterPolicy");
			share_policy.put("UnicoreShare", "UnicorePolicy");
			share_policy.put("CloudShare", "CloudPolicy");
			
			/*String[] shares_test = new String[1];
			shares_test[0] = "ResvShare";
			app_shares.put("ResvApp", shares_test);
			share_policy.put("ResvShare", "ResvPolicy");*/
			
			
			
			//File file = new File("/opt/AHE3/eva_out.txt");
			//file.createNewFile();
			
			RestAPI rest = new RestAPI();
			//rest.createStandAloneServer();
			//SecurityUserAPI.createUser("Sofia", "admin", "sofia0505", "meng.ucc@gmail.com", "","ManGroup",AHE_SECURITY_TYPE.AHE_PASSWORD);
			//WorkflowAPI.createWorkflowDescription("AwsWorkflow", "main/resources/AwsWorkflow.bpmn", "awstest", false);
			//rest.createJettyServer();
			//rest.createServer_HTTPS("/Users/zeqianmeng/Documents/workspace/AHE3/src/main/resources/server.crt","sofia55","sofia55","true",8443);
			//rest.createServer_HTTPS("/Users/zeqianmeng/Documents/workspace/AHE3/src/main/resources/server.jks","sofia55","sofia55","true",8443);
			
			//set up for RedQueen
			
			rest.createServer(8080);
			
			/*TimerTask accounting_timer = new AccountingTimer();
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(accounting_timer, 0, 1200*1000);
			//timer.scheduleAtFixedRate(accounting_timer, 120*1000, 1200*1000);
			
			try {
	            Thread.sleep(2400*1000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        timer.cancel();
	        timer.purge();
	        System.out.println("******* accounting timer cancelled");
	        try {
	            Thread.sleep(30000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }*/
		
			//final String command = "/opt/test/job_submit_broker_expect.sh" ;
			/*System.out.println("%%%%%%%%%%% submitted command: " + command);
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(command);
			 String s = null;
			    
		        BufferedReader stdInput = new BufferedReader(new
	                InputStreamReader(pr.getInputStream()));
		    
		        System.out.println("Here is the standard output of the command:\n");
	            while ((s = stdInput.readLine()) != null) {
	                System.out.println(s);
	            }
			    System.out.println("********");*/

		/*catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
            System.exit(-1);
        }*/
			
			//to set up env for RedQueen
			//ResourceRegisterAPI.createResource("RedQueen", "https://redqueen.com", ResourceCode.REDQUEEN.toString(), "cluster", 8, "x64", 1000, 1000, "rock", "", 1000,-1,RESOURCE_AUTHEN_TYPE.NOT_REQUIRED.toString(),"RedQueen Cluster");
			/*PlatformCredential credential = SecurityUserAPI.createAwsCredentailDetail(
					"redqueen_credential",AuthenCode.redqueen_default, "RedQueen",
					"", "", 
					"/opt/AHE3/providers/aws/aws_credentials/testbed_redqueen.pem", 
					"/opt/AHE3/providers/aws/aws/redqueen_credentials","");
			AHEUser sophie = SecurityUserAPI.getUser("Sofia");
			SecurityUserAPI.addCredentialToUser(sophie, credential);*/
			//AppRegisteryAPI.createApplication("WaterSteeringUoM", "/opt/AHE3/apps/Executable.java", 
				//	"RedQueen");
			//SecurityUserAPI.addCredentialToResource("redqueen_credential", "RedQueen");
			//PlatformCredential aws_cre = SecurityUserAPI.getPlatformCredential("unicore_testgrid_credential");
			//Resource resource = ResourceRegisterAPI.getResource("Alpha");
			//SecurityUserAPI.addCredentialToResource("unicore_testgrid_credential", "Alpha");
			//System.out.println("======" + aws_cre.getResource().iterator().next().getCommonname());
			
			/*AppRegisteryAPI.createApplication("CompSteering", "/opt/AHE3/apps/Executable.java", 
					"RedQueen");
			SecurityUserAPI.addCredentialToResource("redqueen_credential", "RedQueen");*/
			/*Resource resource = ResourceRegisterAPI.getResource("RedQueen");
			resource.setCommonname("redqueen");
			String[] provider_info;
			URI job_service;
			provider_info = ModuleAccess.getModuleProvider().getModuleProperty(AHEConfigurationProperty.module_jobservice_redqueen.toString());
			job_service = new URI(provider_info[0]);
			System.out.println(job_service);*/
			//set up for UnicoreTestgrid
			/*ResourceRegisterAPI.createResource("UnicoreTestgrid", "https://omiiei.zam.kfa-juelich.de:6000/Bravo-Site/services/TargetSystemService?res=ec494b3c-fb84-4ddc-9e31-18a1e39df827", ResourceCode.UNICORE_TESTGRID.toString(), "testgrid", 8, "x64", 1000, 1000, "rock", "", 1000,-1,RESOURCE_AUTHEN_TYPE.UNICORE_KEYSTORE.toString(),"Unicore BES Test Resource");
			PlatformCredential unicore_testgrid_cre = SecurityUserAPI.createAwsCredentailDetail(
					"unicore_testgrid_credential",AuthenCode.unicore_testgrid, "UnicoreTestgrid",
					"", "", 
					"/opt/AHE3/providers/aws/aws_credentials/testbed_ec2_key.pem", 
					"/opt/AHE3/providers/aws/aws/aws_credentials","");
			Resource resource = ResourceRegisterAPI.getResource("UnicoreTestgrid");
			System.out.println("cpu count: " + resource.getCpucount());
			SecurityUserAPI.addCredentialToUser(sophie, unicore_testgrid_cre);
			AppRegisteryAPI.createApplication("Date", "/Applications/MyApp/unicore-ucc-7.4.0/samples/date.u", 
					"UnicoreTestgrid");*/
			
			//SecurityUserAPI.disableUser("admin");
			//System.out.println(SecurityUserAPI.getUser("Sofia").getRole());
			//AHE_API.insertResourceRegistery("alliance", "manchester", "http:man", "pay-as-you-go", 5, "x86", 5, 5, "linux", "12345", 5, 8000, "pwd", "hello");
			//SecurityUserAPI.createUser("", "admin", "Password454546", "emai2@email.com", "","",AHE_SECURITY_TYPE.AHE_PASSWORD);
			
			//the chunk code: to generate test required 
			
			//to set environment for aws
			//AHE_API.insertResourceRegistery("alliance", "alliance", "AWS", "ResourceCode.AWS.toString()", 8, "x64", 1000, 1000, "linux", "", 1000, 22, "RESOURCE_AUTHEN_TYPE.CERTIFICATE.toString()", "");
      		
			//set up for AWS
			/*ResourceRegisterAPI.createResource("AWS", "https://amazon.aws.com", ResourceCode.AWS.toString(), "cloud", 8, "x64", 1000, 1000, "aws", "", 1000,-1,RESOURCE_AUTHEN_TYPE.NOT_REQUIRED.toString(),"Amazon Cloud");
			AppRegisteryAPI.createApplication("WaterSteering", "/opt/AHE3/apps/Executable.java", 
					"AWS");
			
			PlatformCredential aws_cre = SecurityUserAPI.createAwsCredentailDetail(
					"aws_credential",AuthenCode.aws_default, "AWS",
					"", "", 
					"/opt/AHE3/providers/aws/aws_credentials/testbed_ec2_key.pem", 
					"/opt/AHE3/providers/aws/aws/aws_credentials","");
			//System.out.println("cpu count: " + resource.getCpucount());
			SecurityUserAPI.addCredentialToUser(sophie, aws_cre);	
			System.out.println("*****" + sophie.getCredentials().iterator().next());*/
		
			
			//to set environment for ontology test with app: ResvApp
			/*AppRegisteryAPI.createApplication("ResvApp", "/opt/AHE3/apps/Executable.java", 
					"AWS");*/
			
			
			/*ResourceRegisterAPI.createResource("UnicoreTestgrid", "https://unicore.testgrid.com", ResourceCode.UNICORE_TESTGRID.toString(), "grid", 8, "x64", 1000, 1000, "unicore", "", 1000,-1,RESOURCE_AUTHEN_TYPE.NOT_REQUIRED.toString(),"Unicore TestGrid");
			PlatformCredential unicore_testgrid_cre = SecurityUserAPI.createAwsCredentailDetail(
					"unicore_testgrid_credential",AuthenCode.unicore_testgrid, "UnicoreTestgrid",
					"", "", 
					"/opt/AHE3/providers/aws/aws_credentials/testbed_ec2_key.pem", 
					"/opt/AHE3/providers/aws/aws/aws_credentials","");
			Resource resource_u = ResourceRegisterAPI.getResource("UnicoreTestgrid");
			System.out.println("cpu count: " + resource_u.getCpucount());
			SecurityUserAPI.addCredentialToResource("unicore_testgrid_credential", "UnicoreTestgrid");
			//AHEUser sophie = SecurityUserAPI.getUser("Sofia");
			SecurityUserAPI.addCredentialToUser(sophie, unicore_testgrid_cre);*/
			
			
			
			//System.out.println("=====" + AppRegisteryAPI.getApplicationList(SecurityUserAPI.getUser("Sofia")).length);
			//System.out.println("++++++" + AppRegisteryAPI.geta);
			
			//System.out.println("====" + d.getWorkflow_filename();
			//ResourceRegisterAPI.updateResource("alliance", "alliance", "AWS",ResourceCode.AWS.toString(), "cloud", 8, "x64", 1000, 1000, "linux", "", 1000, 22, RESOURCE_AUTHEN_TYPE.CERTIFICATE.toString(),"");
			//Helper.helper();
			//HibernateUtil.Delete(80);
			//WorkflowDescription d = new WorkflowDescription();
			//d.setWorkflow_name("AHEWorkflow");
			//d.setWorkflow_filename("main/resources/AHEWorkflow.bpmn");
			//d.setWorkflow_description("ahetest");
			//d.setAppinst_workflow(false);			
			//System.out.println(WorkflowAPI.getWorkflowDescription("AHEWorkflow2").getWorkflow_filename());
			/*WorkflowDescription d = new WorkflowDescription();
			//WorkflowAPI.
			WorkflowAPI.deleteWorkflowDescription("AwsWorkflow");
			
			System.out.println(d.getWorkflow_filename());*/
			
			//WorkflowDescription dd = WorkflowAPI.getWorkflowDescription("AwsWorkflow");
			//System.out.println(dd.getWorkflow_filename());
			//AHERuntime.getAHERuntime().getConfig_map().get(AHERuntime.ahe_config_filename).getKeys(AHEConfigurationProperty.module_jobservice_globus.toString());
			//AHERuntime.getAHERuntime().s;
			/*System.out.println(AHERuntime.getAHERuntime().getConfig_map().isEmpty());
			String[] provider_info;
			URI job_service;
			provider_info = ModuleAccess.getModuleProvider().getModuleProperty(AHEConfigurationProperty.module_jobservice_aws.toString());
			job_service = new URI(provider_info[0]);
			System.out.println(job_service);*/
			//Helper.helper();
			//System.out.println(SecurityUserAPI.getUser("Sofia").getAcd_vo_group());
			//System.out.println(ResourceRegisterAPI.getResource("alliance").getAuthen_type());
			//System.out.println("server started.");
			
			//System.out.println("helper done!");
			//rest.stopServer();
			//Resource resource = ResourceRegisterAPI.updateResource("UnicoreTestgrid", "Bravo", "https://omiiei.zam.kfa-juelich.de:6000/Bravo-Site/services/TargetSystemService?res=ec494b3c-fb84-4ddc-9e31-18a1e39df827", ResourceCode.UNICORE_TESTGRID.toString(), "testgrid", 8, "x64", 1000, 1000, "rock", "", 1000,-1,RESOURCE_AUTHEN_TYPE.UNICORE_KEYSTORE.toString(),"Unicore Site Bravo");
			
			//System.out.println(resource.getCommonname());
			//resource.setCommonname("Bravo");
			System.out.println("server started.");
			
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	Component component;
	
	public void stopServer()throws Exception{
		component.stop();		
	}
	
	
	public void createServer_HTTPS(String keystorepath, String keystorepassword, String keypassword, String needclientauthentication, int port) throws Exception{
		
//		AHERuntime.SSL_USERAUTHENTICATION = true;
		
        component = new Component();
        component.getClients().add(Protocol.HTTP);
        //component.getClients().add(Protocol.HTTPS);
        Server server = component.getServers().add(Protocol.HTTPS, port);

        
        System.setProperty("javax.net.ssl.trustStore", keystorepath); 
        System.setProperty("javax.net.ssl.trustStorePassword", keystorepassword); 
        
        Series<Parameter> parameters = server.getContext().getParameters();
//        parameters.add("sslContextFactory", "org.restlet.ext.ssl.PkixSslContextFactory");
//        parameters.add("keystorePath", keystorepath);
//        parameters.add("keystorePassword", keystorepassword);
//        parameters.add("keyPassword", keypassword);
//        parameters.add("keystoreType", "JKS");
        //parameters.add("sslContextFactory", "org.restlet.ext.ssl.PkixSslContextFactory");
        parameters.add("keystoreType", "JKS");
        parameters.add("keystorePath", keystorepath);
        parameters.add("keystorePassword", keystorepassword);
//        parameters.add("truststore", keystorepath);
//        parameters.add("trustPassword", keystorepassword);
        parameters.add("keyPassword", keypassword);
        //parameters.add("needClientAuthentication",needclientauthentication); //Turn this on for client authentication 

        parameters.add("wantClientAuthentication","true");
        
        
        Application app = new AHERestletApplication();
        
        component.getDefaultHost().attachDefault(app);
        component.start();
		
	}
	
	public void createServer(int port) throws Exception{
		
        component = new Component();
        component.getServers().add(Protocol.HTTP, port);
        component.getClients().add(Protocol.HTTP);
        //component.getClients().add(Protocol.HTTPS);
        
        Application app = new AHERestletApplication();
        
        component.getDefaultHost().attachDefault(app);
        component.start();
        
	}
	
}
