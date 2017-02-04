package test.AHEModule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.AHEMessage;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.Credential;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.Job;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.Transfer;

import com.google.gson.Gson;

public class AHEJSONMessageTest {

	Credential authen;
	Gson gson = new Gson();
	@Before
	public void setupCredential(){
		
		authen = new Credential();
		authen.setType("globus");
		authen.setCredential_location("/home/davidc/.globus/usercert.pem");
		authen.setProxy_location("/tmp/x509up_u15550");
		authen.setCertificate_directory("/home/davidc/.globus/certificates");
		authen.setUser_key("/home/davidc/.globus/userkey.pem");
		authen.setPassword("davidHTC2012meow");
		
	}
	
	
	@Test
	public void BasicAHEMessageTest(){
		
		AHEMessage msg = new AHEMessage();
		msg.setCommand("TestCommand");
		msg.setTimestamp(new Date().toString());
		msg.setInformation(new String[]{"info1","info2"});
		
		Gson gson = new Gson();
		System.out.println(gson.toJson(msg));
		
		AHEMessage p = gson.fromJson("{\"command\":\"TestCommand\",\"timestamp\":\"Mon Jul 30 12:49:46 BST 2012\",\"exception\":[\"ex1\",\"ex2\"]}",AHEMessage.class);
		
		assertNull(p.getInformation());
		assertEquals(p.getException()[0], "ex1");
		
	}
	
	@Test
	public void AuthenAHEMessageTest(){
		
		AHEMessage msg = new AHEMessage();
		msg.setCommand("TestCommand");
		msg.setTimestamp(new Date().toString());
		

		
		msg.setCredentials(authen);
		
		Gson gson = new Gson();
		System.out.println(gson.toJson(msg));
		
		String json = "{\"command\":\"TestCommand\",\"timestamp\":\"Mon Jul 30 13:01:50 BST 2012\",\"authen\":{\"type\":\"credentialType1\",\"username\":\"dave\"}}";
		AHEMessage p = gson.fromJson(json,AHEMessage.class);
	}
	
	
	
	@Test
	public void JobTest1(){
		
		AHEMessage msg = new AHEMessage();
//		msg.setCommand("TestCommand");
		msg.setTimestamp(new Date().toString());
		
		msg.setCredentials(authen);
		
		Job job = new Job();
		job.setJob_name("job1");
		job.setExecutable("/bin/ls");
		job.setResourcepath("ngs.rl.ac.uk");
		
		msg.setJob(job);
		Gson gson = new Gson();
		System.out.println(gson.toJson(msg));
	}
	
	@Test
	public void Mavrino(){
		
		AHEMessage msg = new AHEMessage();
//		msg.setCommand("TestCommand");

		
		msg.setCredentials(authen);
		
		Job job = new Job();
		job.setJob_name("mavrinoQcGTest");
		job.setExecutable("/home/davidc/ahe_sort.pl");
		job.setResourcepath("mavrino.chem.ucl.ac.uk");
		job.setResource_port(19000);
		job.setArg(new String[]{"app.arg=[config.txt]"});
		
		msg.setJob(job);
		Gson gson = new Gson();
		System.out.println(gson.toJson(msg));
	}

	
	@Test
	public void JobTest2() {

		AHEMessage msg = new AHEMessage();
		// msg.setCommand("TestCommand");
		msg.setTimestamp(new Date().toString());

		msg.setCredentials(authen);

		Job job = new Job();
		job.setJob_id("	https://ngs.wmin.ac.uk:3041/15670/1343746817/");

		msg.setJob(job);
		Gson gson = new Gson();
		System.out.println(gson.toJson(msg));
	}	
		
	@Test
	public void JobAHEMessageTest(){
		
		AHEMessage msg = new AHEMessage();
//		msg.setCommand("TestCommand");
		msg.setTimestamp(new Date().toString());
		
		msg.setCredentials(authen);
		
		Job job = new Job();
		job.setJob_name("job1");
		job.setExecutable("/bin/ls");
		job.setResourcepath("ngs.rl.ac.uk");
		
		msg.setJob(job);
		job.setArg(new String[]{"arg=arg1","arg=arg2","arg=arg3","arg1.arg2=val4"});
		
		Gson gson = new Gson();
		System.out.println(gson.toJson(msg));
		
		AHEMessage p = gson.fromJson(gson.toJson(msg),AHEMessage.class);
		
		assertEquals("arg=arg1", p.getJob().getArg()[0]);
		assertEquals("arg1.arg2=val4", p.getJob().getArg()[3]);
	}
	
	@Test
	public void TransferAHEMessageTest1(){
		
		AHEMessage msg = new AHEMessage();
	
		msg.setCredentials(authen);

		Transfer tran = new Transfer();
		tran.setPath(new String[][]{{"gsiftp://bunsen.chem.ucl.ac.uk//filestage/input.txt","gsiftp://bunsen.chem.ucl.ac.uk//filestage/config.txt"},{"gsiftp://mavrino.chem.ucl.ac.uk//mavrino-homes/davidc/input.txt","gsiftp://mavrino.chem.ucl.ac.uk//mavrino-homes/davidc/config.txt"}});
		
		msg.setTransfer(tran);
		Gson gson = new Gson();
		System.out.println(gson.toJson(msg));
	}
	
	
	@Test
	public void TransferAHEMessageTest(){
		
		AHEMessage msg = new AHEMessage();
		msg.setCommand("TestCommand");
		msg.setTimestamp(new Date().toString());
		
		msg.setCredentials(authen);

		Transfer tran = new Transfer();
		tran.setPath(new String[][]{{"gsiftp://bunsen.chem.ucl.ac.uk//filestage/input.txt","gsiftp://bunsen.chem.ucl.ac.uk//filestage/config.txt"},{"gsiftp://mavrino.chem.ucl.ac.uk//mavrino-homes/davidc/input.txt","gsiftp://mavrino.chem.ucl.ac.uk//mavrino-homes/davidc/config.txt"}});
		
		msg.setTransfer(tran);
		Gson gson = new Gson();
		System.out.println(gson.toJson(msg));
	}
	
	@Test
	public void TransferSardineModule(){
		
		AHEMessage msg = new AHEMessage();
		msg.setCommand("TestCommand");
		
		Credential webdav = new Credential();
		webdav.setType("WEBDAV");
		webdav.setUsername("demo1");
		webdav.setPassword("d3moPasswd");
		
		msg.setCredentials(webdav);
		
		Transfer tran = new Transfer();
		tran.setPath(new String[][]{{"http://149.156.10.138:8080/lobcder-1.0-SNAPSHOT/pg6130.txt","http://149.156.10.138:8080/lobcder-1.0-SNAPSHOT/services.xml"},{"file://file1.txt","file://file2.xml"}});
		
		msg.setTransfer(tran);
		Gson gson = new Gson();
		System.out.println(gson.toJson(msg));
		
	}

}

