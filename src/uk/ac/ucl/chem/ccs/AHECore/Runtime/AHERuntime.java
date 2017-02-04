package uk.ac.ucl.chem.ccs.AHECore.Runtime;

import java.io.File;
import java.security.Security;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import test.helper.TestFunctions;
import uk.ac.ucl.chem.ccs.AHECore.API.Rest.RestAPI;
import uk.ac.ucl.chem.ccs.AHECore.Configuration.AHEConfigFile;
import uk.ac.ucl.chem.ccs.AHECore.Configuration.ConfigFiles;
import uk.ac.ucl.chem.ccs.AHECore.Definition.AHEConfigurationProperty;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AppRegisteryAPI;
import uk.ac.ucl.chem.ccs.AHECore.Engine.ResourceRegisterAPI;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHESecurityException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AppAlreadyExistException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppRegistery;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHECore.Security.AuthenCode;
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;
import uk.ac.ucl.chem.ccs.AHEModule.Def.AHE_SECURITY_TYPE;
import uk.ac.ucl.chem.ccs.AHEModule.Def.RESOURCE_AUTHEN_TYPE;
import uk.ac.ucl.chem.ccs.AHEModule.Def.ResourceCode;


/**
 * This is the main AHE Core class that initiate the whole AHE Core Application.
 * It populates the internal registeries and data structures and ensure that it is in sync with the persistant data source.
 * 
 * Any configuration or any possible embedded system or DB (i.e. H2 DB, ResetLet standalone configuration) will have to be parsed and initiated here
 * 
 * In the standalone mode. AHE can be started using the follow parameter
 * 
 * -S Secure mode 
 * -SSL SSL mode
 * -SSLClient SSL client authentication
 * -vphsec Enable VPHSEC authentication mode
 * -p Server port
 * -keystore Keystore location for secure mode
 * -keystorepass Keystore password
 * -keypass key password
 * 
 * @author davidc
 *
 */

public class AHERuntime {

	private AHEEngine ahe_engine;
	
	private static AHERuntime aheruntime = null;
	
	String jbpm_persistence_file;
	String ahe_hibernate_file;
	
	final static String config_folder = "config";
	//final static String config_folder = "config";
	final static String data_folder = "data";
	final static String cert_folder = "cert";
	final static String log_folder = "log";
	final static String workflow_folder = "source";
	final static String runtime_path = System.getProperty("user.dir");
	
	public final static String ahe_config_filename = "ahe.properties";
	final static String ahe_config_path = config_folder + File.separator + ahe_config_filename;
	
	//Supports multiple config file
	private HashMap<String,ConfigFiles> config_map;
	
	private static boolean log = true;
	
//	public static boolean SSL_USERAUTHENTICATION = true; // for debugging purposes, security checks can be ignored. This should be removed
	
	private boolean standalone_mode = false;
	
	//private String testing_ds_url = "jdbc:mysql://localhost:3306/jbpm5db";
	//private String testing_ds_username = "aheserver";
	//private String testing_ds_pass = "fortccs@2011";
	
	private final String default_tmp = "/tmp"; //Use this folder if no tmp attribute is set in the config file
	private static RestAPI rest;
	
	private boolean vphsec = false;
	
	/*public static void main(String[] arg) throws Exception{
		AHERuntime.getAHERuntime(new String[]{"standalone"});
		
		rest = new RestAPI();
		rest.createServer(8443);
	}*/
	public static void main(String[] arg){
		
		CommandLineParser parser = new BasicParser();
		Options options = new Options( );
		options.addOption("S", "Secure Mode", false, "Start server using SSL and Client authentication");
		options.addOption("SSL", "SSL Mode", false, "Start server using SSL");
		options.addOption("SSLClient", "SSL Client Authentication Mode", false, "Start server using SSL Client");
		options.addOption("vphsec", "VPH-Share authentication", false, "Enable VPH-Share security mode");
		options.addOption("p", "port", true, "Server port");
		options.addOption("keystore", "keystore", true, "keystore location");
		//options.addOption("u", "username", true, "Server Keystore username");
		options.addOption("keystorepass", "key store password", true, "Server Keystore password");
		options.addOption("keypass", "key pass", true, "key password");
		options.addOption("DEMO_SSL", "DEMO Setup", false, "Use SSL Demo configuration");
		options.addOption("DEMO_PLAIN", "DEMO Setup", false, "Use plain HTTP Demo configuration");
		options.addOption("TestData", "Test Data", false, "Add Test Data to Server");
		
		// Parse the program arguments
		
		try {
			
			CommandLine commandLine = parser.parse( options, arg);
			
			if(commandLine.hasOption("DEMO_SSL")){
				

				//Init AHE
				AHERuntime.getAHERuntime(new String[]{"standalone"});
					
				rest = new RestAPI();
				//rest.createServer_HTTPS("src/main/resources/server.jks","davidchang","davidchang","true",8111);
				rest.createServer_HTTPS("src/main/resources/server.jks","davidchang","davidchang","true",8080);
				
				if(commandLine.hasOption("vphsec")){
					AHERuntime.getAHERuntime().setVphsec(true);
				}
					
				AHERuntime.getAHERuntime().setupTestData();
			
				
			}else if(commandLine.hasOption("DEMO_PLAIN")){
				
				//Init AHE
				AHERuntime.getAHERuntime(new String[]{"standalone"});
					
				rest = new RestAPI();
				rest.createServer(8111);

				if(commandLine.hasOption("vphsec")){
					AHERuntime.getAHERuntime().setVphsec(true);
				}
				
				AHERuntime.getAHERuntime().setupTestData();
				
			}else{
				
				if(!commandLine.hasOption("S") && !commandLine.hasOption("SSL")){
					//Setup simple http server
					AHERuntime.getAHERuntime(new String[]{"standalone"});
					
					rest = new RestAPI();
					int port = 8111;
					
					if(commandLine.hasOption("p"))
						port = Integer.parseInt(commandLine.getOptionValue("p"));
					
					rest.createServer(port);
					
					if(commandLine.hasOption("vphsec")){
						AHERuntime.getAHERuntime().setVphsec(true);
					}
					
					if(commandLine.hasOption("TestData"))
						AHERuntime.getAHERuntime().setupTestData();
					
				}else{
					
					//Secure Mode
					
					boolean clientauth = false;
					boolean ssl = true;
					
					if(commandLine.hasOption("S") || commandLine.hasOption("SSLClient")){
						clientauth = true;
					}
					
					if(!commandLine.hasOption("keystore") || !commandLine.hasOption("keystorepass") || !commandLine.hasOption("keypass")){
						System.out.println("Arguments -keystore, -keystorepass for key store password, -keypass for password must be present for https mode");
						System.exit(1);
					}
					

					
					AHERuntime.getAHERuntime(new String[]{"standalone"});
					
					rest = new RestAPI();
					
					int port = 8443;
                    // int port = 8112;
					if(commandLine.hasOption("p"))
						port = Integer.parseInt(commandLine.getOptionValue("p"));
					
					if(clientauth)
						rest.createServer_HTTPS(commandLine.getOptionValue("keystore"),commandLine.getOptionValue("keystorepass"),commandLine.getOptionValue("keypass"),"true",port);
					else
						rest.createServer_HTTPS(commandLine.getOptionValue("keystore"),commandLine.getOptionValue("keystorepass"),commandLine.getOptionValue("keypass"),"false",port);
					
					if(commandLine.hasOption("vphsec")){
						AHERuntime.getAHERuntime().setVphsec(true);
					}
					
					if(commandLine.hasOption("TestData"))
						AHERuntime.getAHERuntime().setupTestData();
					
					AHERuntime.getAHERuntime().FirstTimeSetupAdminUser();
				}
				
			}
			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	

		
	}
	
	private AHERuntime(String[] arg){
		//startup(arg.length > 0);
		startup(true);
	}
	
	public static AHERuntime getAHERuntime(String[] arg){
		
		if(aheruntime == null){
			
			aheruntime = new AHERuntime(arg);
			return aheruntime;
			
		}else{
			return aheruntime;
		}
		
	}
	
	public static AHERuntime getAHERuntime(){
		
		if(aheruntime == null){
			
			aheruntime = new AHERuntime(new String[0]);
			return aheruntime;
			
		}else{
			return aheruntime;
		}
		
	}
	
	/**
	 * Start the AHE Runtime. In the standalone mode, AHE will create all the necessary file structure. In non-standalone mode the user is required to specify the location of the configuration file
	 * @param standalone
	 */
	
	public void startup(boolean standalone){
	
		this.setStandalone_mode(standalone);
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		setConfig_map(new HashMap<String, ConfigFiles>());
		
		//In Servlet Mode, AHERuntime config info will be set in web.xml
		if(standalone){
			checkFileStructure();
			//String path = runtime_path + File.separator +config_folder + File.separator + ahe_config_filename;
			//System.out.println("@@@@@@@@@@@@@@@@" + path);
			//TestFunctions.writeout(path);
			AHEConfigFile base_config = new AHEConfigFile(runtime_path + File.separator +config_folder + File.separator + ahe_config_filename);
			getConfig_map().put(ahe_config_filename, base_config);
			
		}
		
		setAhe_engine(new AHEEngine());
		
	}
	

	
	public void stop(){
		
				
	}
	

	private void checkFileStructure(){

		File c_folder = new File(runtime_path + File.separator + cert_folder);

		if(!c_folder.exists())
			c_folder.mkdir();
		
		File d_folder = new File(runtime_path + File.separator + data_folder);
		
		if(!d_folder.exists())
			d_folder.mkdir();
		
		File l_folder = new File(runtime_path + File.separator + log_folder);
		
		if(!l_folder.exists())
			l_folder.mkdir();
		
		File w_folder = new File(runtime_path + File.separator + workflow_folder);
		
		if(!w_folder.exists())
			w_folder.mkdir();
		
		File con_folder = new File(runtime_path + File.separator + config_folder);
		
		if(!con_folder.exists())
			con_folder.mkdir();
		
	}
	
	/**
	 * Create a default Admin role account. It is advised that the user change this password immediately
	 * @throws AHESecurityException
	 */
	
	public void FirstTimeSetupAdminUser() throws AHESecurityException{
		
		if(SecurityUserAPI.getUserList().length > 0){
			return;
		}
		
		AHEUser adminuser = SecurityUserAPI.createUser("SuperAdmin", "admin", "Password454546", "emai2@email.com", "","",AHE_SECURITY_TYPE.AHE_PASSWORD);
		
	}
	
	/**
	 * Test data
	 * @throws AHESecurityException
	 */
	
	private void setupTestData() throws AHESecurityException{

		if(ResourceRegisterAPI.getResource("rlNGS") != null){
			System.out.println("Lazy Test: Test Data already exists");
			return;
		}
		
		//Setup Resources
		
		Resource r = new Resource();
		r.setTimestamp(new Date());
		r.setActive(1);
		r.setArch("x64");
		r.setCommonname("rlNGS");
		r.setCpucount(32);
		r.setEndpoint_reference("ngs.rl.ac.uk");
		r.setIp("");
		r.setMemory(1000);
		r.setOpsys("linux");
		r.setResource_interface("globus");
		r.setType("ngs");
		r.setWalltimelimit(1000);
		r.setVirtualmemory(10000);
		
		HibernateUtil.SaveOrUpdate(r);
		
		Resource r2 = ResourceRegisterAPI.createResource("UnicoreBES", "https://omiiei.zam.kfa-juelich.de:6000/Bravo-Site/services/BESFactory?res=default_bes_factory", ResourceCode.UNICORE_BES.toString(), "testgrid", 8, "x64", 1000, 1000, "rock", "", 1000,-1,RESOURCE_AUTHEN_TYPE.UNICORE_KEYSTORE.toString(),"Unicore BES Test Resource");
		
		Resource r5 = ResourceRegisterAPI.createResource("Mavrino", "mavrino.chem.ucl.ac.uk",ResourceCode.QCG.toString(), "cluster", 8, "x64", 1000, 1000, "linux", "", 1000,19000,RESOURCE_AUTHEN_TYPE.PROXY_CERTIFICATE.toString(),"CCS Mavrino - QCG");
		
		Resource r6 = ResourceRegisterAPI.createResource("rlNGS_JobManager", "ngs.rl.ac.uk/jobmanager-lsf",ResourceCode.GLOBUS.toString(), "ngs", 8, "x64", 1000, 1000, "linux", "", 1000,19000,RESOURCE_AUTHEN_TYPE.PROXY_CERTIFICATE.toString(),"");
		Resource t1 = ResourceRegisterAPI.createResource("BunsenGridFTP", "gsiftp://bunsen.chem.ucl.ac.uk//filestage",ResourceCode.GSIFTP.toString(), -1,RESOURCE_AUTHEN_TYPE.PROXY_CERTIFICATE.toString(),"");
		Resource t2 = ResourceRegisterAPI.createResource("MavrinoGridFTP", "gsiftp://mavrino.chem.ucl.ac.uk//mavrino-homes/davidc",ResourceCode.GSIFTP.toString(), -1,RESOURCE_AUTHEN_TYPE.PROXY_CERTIFICATE.toString(),"");
		Resource t3 = ResourceRegisterAPI.createResource("NGS_GridFTP", "gsiftp://ngs.rl.ac.uk//home/ngs0891",ResourceCode.GSIFTP.toString(), -1,RESOURCE_AUTHEN_TYPE.PROXY_CERTIFICATE.toString(),"");
		
		Resource wmin = ResourceRegisterAPI.createResource("wmin_globus", "ngs.wmin.ac.uk", ResourceCode.GLOBUS.toString(), "ngs", 8, "x64", 1000, 1000, "linux", "", 1000,19000,RESOURCE_AUTHEN_TYPE.PROXY_CERTIFICATE.toString(),"");
		Resource wmin_gridftp = ResourceRegisterAPI.createResource("wmin_gridftp", "gsiftp://ngs.wmin.ac.uk//home/ngs/ngs0098",ResourceCode.GSIFTP.toString(), -1,RESOURCE_AUTHEN_TYPE.PROXY_CERTIFICATE.toString(),"");
		
		Resource hector = ResourceRegisterAPI.createResource("hector_globus", "login11b.hector.ac.uk/jobmanager-pbs", ResourceCode.GLOBUS.toString(), "ngs", 8, "x64", 1000, 1000, "linux", "", 1000,19000,RESOURCE_AUTHEN_TYPE.CERTIFICATE.toString(),"");
		
		AppRegistery app3 = new AppRegistery();
		app3.setTimestamp(new Date());
		app3.setActive(1);
		app3.setAppname("date");
		app3.setExecutable("/bin/date");
		app3.setResource(r);
		
		HibernateUtil.SaveOrUpdate(app3);
		
		AppRegistery app5 = new AppRegistery();
		app5.setTimestamp(new Date());
		app5.setActive(1);
		app5.setAppname("ahesort");
		app5.setExecutable("./ahe_sort.pl");
		app5.setResource(r);
		
		HibernateUtil.SaveOrUpdate(app5);

		
		try {
			
			AppRegisteryAPI.createApplication("date", "/bin/date", r2.getCommonname());
			AppRegisteryAPI.createApplication("date", "/bin/date", r5.getCommonname());
			AppRegisteryAPI.createApplication("CMPD", "/mavrino-homes/mapper/apps/CPMD-mavrino/cpmd_job_grid.pbs", r5.getCommonname());

			AppRegisteryAPI.createApplication("ahesort2", "/home/davidc/ahe_sort.pl", r5.getCommonname());

			AppRegisteryAPI.createApplication("grompp", "/mavrino-homes/hall/Software/Linux/gromacs-4.5.4/bin/grompp", r5.getCommonname());
			AppRegisteryAPI.createApplication("mdrun", "/mavrino-homes/hall/Software/Linux/gromacs-4.5.4/bin/mdrun", r5.getCommonname());
			AppRegisteryAPI.createApplication("editconf", "/mavrino-homes/hall/Software/Linux/gromacs-4.5.4/bin/editconf", r5.getCommonname());

			AppRegisteryAPI.createApplication("GROMAC_3", "/usr/ngs/GROMACS_3_3_1", r6.getCommonname());
			AppRegisteryAPI.createApplication("GROMAC_4", "/usr/ngs/GROMACS_4_0", r6.getCommonname());
			
			AppRegisteryAPI.createApplication("ChasteBatchScript", "/home/e10/e10/dchang/chaste_startscript.sh", hector.getCommonname(),"CHASTE Sensitivity batch script. This script takes 3 array arguments for $k, $ical and $ina in the form of {1,2,3} etc");
			
			AppRegisteryAPI.createApplication("date", "/bin/date", wmin.getCommonname());
			
		} catch (AppAlreadyExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AHEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Test Users
		
		AHEUser user1 = SecurityUserAPI.createUser("u455", "admin", "test1", "emai@email.com","emailAddress=aheadmin@ucl.ac.uk,CN=AHE Admin,OU=CCS,O=UCL,L=London,ST=London,C=UK","",AHE_SECURITY_TYPE.AHE_SSL_CLIENT);
		AHEUser user4 = SecurityUserAPI.createUser("tester1", "user", "", "emai2@email.com", "1016","eudat",AHE_SECURITY_TYPE.ACD);
		AHEUser vphuser = SecurityUserAPI.createUser("vphuser", "user", "vphdemo", "emai2@email.com", "blah blah blah","",AHE_SECURITY_TYPE.AHE_PASSWORD);
		AHEUser adminuser = SecurityUserAPI.createUser("admin", "admin", "ArfArfMeow1717", "emai2@email.com", "blah blah blah","",AHE_SECURITY_TYPE.AHE_PASSWORD);

		
		PlatformCredential gc = SecurityUserAPI.createGlobusCredentialDetail("globus_admin_credential",AuthenCode.globus_default, r.getCommonname(),"/home/davidc/.globus/usercert.pem", "/tmp/x509up_u15550", "/home/davidc/.globus/userkey.pem", "/home/davidc/.globus/certificates","");
		PlatformCredential u1 = SecurityUserAPI.createUniCore6CredentialDetail("unicore_1_credential",AuthenCode.unicore_default, r2.getCommonname(), "/home/davidc/.ucc/David_Chang.jks", "https://omiiei.zam.kfa-juelich.de:6000/Registry/services/Registry?res=default_registry", "", "unicore","","");
		PlatformCredential qcg = SecurityUserAPI.createGlobusCredentialDetail("mavrino1",AuthenCode.globus_default, r5.getCommonname(),"/home/davidc/.globus/usercert.pem", "/tmp/x509up_u15550", "/home/davidc/.globus/userkey.pem", "/home/davidc/.globus/certificates","");
		
		SecurityUserAPI.addCredentialToResource(gc.getCredential_id(), hector.getCommonname());
		SecurityUserAPI.addCredentialToResource(gc.getCredential_id(), wmin.getCommonname());
		SecurityUserAPI.addCredentialToResource(gc.getCredential_id(), wmin_gridftp.getCommonname());
		SecurityUserAPI.addCredentialToResource(gc.getCredential_id(), r6.getCommonname());
		SecurityUserAPI.addCredentialToResource(gc.getCredential_id(), t1.getCommonname());
		SecurityUserAPI.addCredentialToResource(gc.getCredential_id(), t2.getCommonname());
		SecurityUserAPI.addCredentialToResource(gc.getCredential_id(), t3.getCommonname());
		
		
		SecurityUserAPI.addCredentialToUser(user4, gc);		
		SecurityUserAPI.addCredentialToUser(user1, gc);
		SecurityUserAPI.addCredentialToUser(user1, u1);
		SecurityUserAPI.addCredentialToUser(user1, qcg);

		
		SecurityUserAPI.addCredentialToUser(vphuser, gc);
		SecurityUserAPI.addCredentialToUser(vphuser, qcg);
		
		SecurityUserAPI.addCredentialToUser(adminuser, gc);
		SecurityUserAPI.addCredentialToUser(adminuser, qcg);
	}

	public void setAhe_engine(AHEEngine ahe_engine) {
		this.ahe_engine = ahe_engine;
	}

	public AHEEngine getAhe_engine() {
		return ahe_engine;
	}

	public void setConfig_map(HashMap<String,ConfigFiles> config_map) {
		this.config_map = config_map;
	}

	public HashMap<String,ConfigFiles> getConfig_map() {
		return config_map;
	}

	public static boolean isLog() {
		return log;
	}

	public static void setLog(boolean log) {
		AHERuntime.log = log;
	}
	
	
	public ConfigFiles getAHEConfiguration(){
		return config_map.get(ahe_config_filename);
	}
	
	public static String getConfigFolderPath(){
		return runtime_path + File.separator + config_folder;
	}
	
	public static String getCertFolderPath(){
		return runtime_path + File.separator + cert_folder;
	}
	
	public static String getDataFolderPath(){
		return runtime_path + File.separator + data_folder;
	}
	
	public void loadConfigurationFile(){
				
		//Load logging information
		
		String logging = getAHEConfiguration().getPropertyString(AHEConfigurationProperty.logging.toString());
		
		if(logging != null){
			
			if(logging.equalsIgnoreCase("true"))
				log	 = true;
			else
				log = false;
			
		}
		
	}

	public boolean isStandalone_mode() {
		return standalone_mode;
	}

	public void setStandalone_mode(boolean standalone_mode) {
		this.standalone_mode = standalone_mode;
	}

	public String get_AHETmpFolder() {
		
		String tmp_folder = getAHEConfiguration().getPropertyString(AHEConfigurationProperty.ahe_tmp.toString());
		
		if(tmp_folder != null)
			return tmp_folder;
		
		return default_tmp;
	}
	
	public void shutdown() throws Exception{
//		rest.stopServer();
		System.exit(0);
	}

	public boolean isVphsec() {
		return vphsec;
	}

	public void setVphsec(boolean vphsec) {
		this.vphsec = vphsec;
	}
	
}
