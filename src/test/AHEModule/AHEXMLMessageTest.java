package test.AHEModule;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import uk.ac.ucl.chem.ccs.AHEModule.message.XML.AHEMessageAPI;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.ahe;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.app;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.app_instance;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.credential;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.property;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.resource;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.transfer;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.user;

public class AHEXMLMessageTest {

	@Test
	public void createSimpleAHEMessage(){
		
		ahe message = new ahe();
		message.setInformation("test information");
		message.setException("Exception message");
		message.setWarning("Set Warning message");
		message.setTimestamp(new Date().toString());
		message.setCommand("/GET");
	
		try {
			System.out.println(AHEMessageAPI.generateXML(message));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void createAppListMessage(){
		
		ahe message = new ahe();
		
		app app1 = new app();
		app1.setExecutable("/bin/fs");
		app1.setName("app1");
		app1.setResource_interface("globus");
		app1.setId(1);
		
		app app2 = new app();
		app2.setExecutable("/bin/fs");
		app2.setName("app2");
		app2.setResource_interface("qcg");
		app2.setId(2);
		
		ArrayList<app> list = new ArrayList<app>();
		list.add(app1);
		list.add(app2);
		
		message.setApp_list(list);
		
		try {
			System.out.println(AHEMessageAPI.generateXML(message));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void createCredListMessage(){
		
		credential cred1 = new credential();
		cred1.setCreated(new Date().toString());
		cred1.setName("cred1");
		cred1.setType("globus");
		
		resource res = new resource();
		res.setName("res1");
		
		ArrayList<resource> reslist = new ArrayList<resource>();
		reslist.add(res);
		
		cred1.setResource_list(reslist);
		
		credential cred2 = new credential();
		cred2.setCreated(new Date().toString());
		cred2.setName("cred1");
		cred2.setType("globus");
		
		user user = new user();
		user.setCreated(new Date().toString());
		user.setRole("user");
		user.setUsername("username1");
		user.setAuthen_type("AHE-Password");
		
		ArrayList<user> userlist = new ArrayList<user>();
		userlist.add(user);
		
		cred2.setOwner_list(userlist);
		
		ahe message = new ahe();
		
		ArrayList<credential> list = new ArrayList<credential>();
		list.add(cred1);
		list.add(cred2);
		
		message.setCred_list(list);
		
		try {
			System.out.println(AHEMessageAPI.generateXML(message));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void createResListMessage(){
		
		resource res1 = new resource();
		res1.setName("resource1");
		res1.setArch("linux");
		res1.setCpu_count(5);
		res1.setId(100);
		res1.setIp("42134.42132");
		res1.setMemory(1000);
		res1.setOpsys("ROCK");
		res1.setResource_interface("GSIFTP");
		res1.setUri("blah");
		res1.setVmemory(10000);
		res1.setWalltimelimit(100000);

		ahe message = new ahe();
		
		ArrayList<resource> list = new ArrayList<resource>();
		list.add(res1);
		
		message.setResource_list(list);
		
		try {
			System.out.println(AHEMessageAPI.generateXML(message));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void createAppInstListMessage(){
		
		
		app_instance prepare_msg = new app_instance();
		
		prepare_msg.setId(111);
		prepare_msg.setName("hgfhfghfghgfds");
		prepare_msg.setStatus("polling");
		
		resource res = new resource();
		res.setName("res1");
		
		ArrayList<resource> reslist = new ArrayList<resource>();
		reslist.add(res);
		prepare_msg.setResource_list(reslist);
		
		app_instance appinst1 = new app_instance();
		
		appinst1.setId(155);
		appinst1.setName("fdsfsf");
		appinst1.setStatus("waiting");
		


		property prop1 = new property();
		prop1.setName("arg1");
		prop1.setValue("value1");
		
		property prop2 = new property();
		prop2.setName("arg2");
		prop2.setValue("value2");
		
		ArrayList<property> proplist = new ArrayList<property>();
		proplist.add(prop1);
		proplist.add(prop2);
		appinst1.setProperty_list(proplist);
		
		transfer  tran1 = new transfer();
		tran1.setAhe_id("fdsfsdfsdfs");
		tran1.setId(432424);
		tran1.setStatus("failed");
		tran1.setSource("uri:from");
		tran1.setDest("uri:to");
		tran1.setStage_dir("StageIn");
		
		ArrayList<transfer> tranlist = new ArrayList<transfer>();
		tranlist.add(tran1);
		appinst1.setTransfer_list(tranlist);
		
		
		ahe message = new ahe();
		
		ArrayList<app_instance> list = new ArrayList<app_instance>();
		list.add(appinst1);
		list.add(prepare_msg);
		
		message.setAppinst_list(list);
		
		try {
			System.out.println(AHEMessageAPI.generateXML(message));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void createTranserListMessage(){
		
		transfer  tran1 = new transfer();
		tran1.setAhe_id("fdsfsdfsdfs");
		tran1.setId(432424);
		tran1.setStatus("failed");
		tran1.setSource("uri:from");
		tran1.setDest("uri:to");
		tran1.setStage_dir("StageIn");
		

		ahe message = new ahe();
		
		ArrayList<transfer> list = new ArrayList<transfer>();
		list.add(tran1);
		
		message.setTransfer_list(list);
		
		try {
			System.out.println(AHEMessageAPI.generateXML(message));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
