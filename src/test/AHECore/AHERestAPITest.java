package test.AHECore;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.junit.BeforeClass;
import org.junit.Test;

import test.util.exception.AuthenticationFailedException;
import test.util.exception.AuthorizationFailedException;
import test.util.exception.RESTException;
import test.util.http.AHEHTTPConnector;
import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;
import uk.ac.ucl.chem.ccs.AHECore.Security.AuthenCode;
import uk.ac.ucl.chem.ccs.AHEModule.Def.AHE_SECURITY_TYPE;
import uk.ac.ucl.chem.ccs.AHEModule.Def.ResourceCode;
import uk.ac.ucl.chem.ccs.AHEModule.Def.rest_arg;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.AHEMessageAPI;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.ahe;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.app;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.app_instance;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.credential;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.property;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.resource;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.transfer;
import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.user;

/**
 * Test REST public API
 * 
 * Create User, Resource, Credential, Application
 * Edit User, Resource, Credential Application
 * Map User - Credential
 * Delete mapping
 * Disable User, Resource, Credential, Application
 * 
 * 
 * @author davidc
 *
 */

public class AHERestAPITest {

	
	static AHEHTTPConnector conn;
	
	@BeforeClass
	public static void setup() throws ClientProtocolException, IOException{
		conn = new AHEHTTPConnector("Sofia", "Password454546", "localhost", 8080, false);
		//conn = new AHEHTTPConnector("SuperAdmin", "Password454546", "localhost", 8080, false);
		//conn = new AHEHTTPConnector("admin", "ArfArfMeow1717", "localhost", 8080, false);
		//conn = new AHEHTTPConnector("aheserver","fortccs@2011","localhost",8080,false);
		//conn = new AHEHTTPConnector("davidchang", "davidchang", "localhost", 8080, false);
		//the reason of false: the authenticate within AHEAuthenticator may return true,
		System.out.println(conn.authenticate());
		//System.out.println("set up succeeded");
	}
	
	@Test
	public void AHERestAPITest(){
			
		// Create User

		AddUser();

		// Edit User
		//getUserList();
		/*EditUser();
		DeleteUser();
		getUserList();

		// Create Credential
		AddCred();

		// Edit Credential
		EditCred();
		DeleteCred();
		
		// Create Resource
		AddResource();
		
		// Edit Resource
		EditResource();
		DeleteResource();
		
		// Create App
		AddApp();
		
		// Edit App
		EditApp();
		DeleteApp();
		
		// Map Cred to User
		MapResourceToCred();
		unMapResourceToCred();
		
		// Map Cred to resource
		MapUserToCred();
		unMapUserToCred();
		*/
		// Prepare - add staging - add property - start

		// One start command

		// Clone

		// Upload

		// Upload Proxy

		// Delete

	}
	
	
	private void AddUser(){
		
		try{
		
			HashMap<String, String> arg = new HashMap<String, String>();
			
			//"security_type=ahe_password" --data-urlencode "username=AHEUserTest" --data-urlencode "pwd=TestNo1" --data-urlencode "role=user" --data-urlencode "email=d11@d.com" --data-urlencode "identifier=EMAILADDRESS=this@this.com, CN=UK"
			arg.put(rest_arg.username.toString(), "u1_test");
			arg.put(rest_arg.pwd.toString(), "pwd1");
			arg.put(rest_arg.security_type.toString(), AHE_SECURITY_TYPE.AHE_PASSWORD.toString());
			arg.put(rest_arg.role.toString(), "user");
			arg.put(rest_arg.email.toString(), "u1@test.com");
			arg.put(rest_arg.identifier.toString(), "EMAILADDRESS=this@this.com, CN=UK");
			
			String response = conn.addUser(arg);
			
			ahe msg = AHEMessageAPI.parseXML(response);
			
	//		assertNull(msg.getException());
			
			arg.put(rest_arg.username.toString(), "u2_test");
			arg.put(rest_arg.pwd.toString(), "pwd2");
			arg.put(rest_arg.security_type.toString(), AHE_SECURITY_TYPE.AHE_SSL_CLIENT.toString());
			arg.put(rest_arg.role.toString(), "user");
			arg.put(rest_arg.email.toString(), "u2@test.com");
			arg.put(rest_arg.identifier.toString(), "EMAILADDRESS=this@this.com, CN=UK");
			
			response = conn.addUser(arg);
			msg = AHEMessageAPI.parseXML(response);
	//		assertNull(msg.getException());
			
			arg.put(rest_arg.username.toString(), "edit_test");
			arg.put(rest_arg.pwd.toString(), "pwd3");
			arg.put(rest_arg.security_type.toString(), AHE_SECURITY_TYPE.AHE_SSL_CLIENT.toString());
			arg.put(rest_arg.role.toString(), "user");
			arg.put(rest_arg.email.toString(), "u3@test.com");
			arg.put(rest_arg.identifier.toString(), "EMAILADDRESS=this@this.com, CN=UK");
			
			response = conn.addUser(arg);
			msg = AHEMessageAPI.parseXML(response);
	//		assertNull(msg.getException());
		}catch(Exception e){
			e.printStackTrace();
			assertNull(e);
		}
		
	}
	
	private void EditUser(){
		
		try{
		
			HashMap<String, String> arg = new HashMap<String, String>();
			
			arg.put(rest_arg.username.toString(), "edit4_test");
			arg.put(rest_arg.security_type.toString(), AHE_SECURITY_TYPE.AHE_PASSWORD.toString());
			arg.put(rest_arg.role.toString(), "admin");
			arg.put(rest_arg.email.toString(), "u5@test.com");
			arg.put(rest_arg.identifier.toString(), "Test Test Test");
		
			String response = conn.editUser("edit_test", arg);
			ahe msg = AHEMessageAPI.parseXML(response);
			
	//		assertNull(msg.getException());
			
			response = conn.listUsers();
			msg = AHEMessageAPI.parseXML(response);
			
			for(user u : msg.getUser_list()){
				
				if(u.getUsername().equals("edit4_test")){
					
					assertEquals("u5@test.com",  u.getEmail());
					assertEquals("Test Test Test",  u.getAlt_identifier());
					assertEquals("admin",  u.getRole());
					assertEquals(AHE_SECURITY_TYPE.AHE_PASSWORD.toString(),  u.getAuthen_type());

					return;

				}
				
			}
			
			assertNull("User not found");
			
			System.out.println(response);
			
		}catch(Exception e){
			e.printStackTrace();
			assertNull(e);
		}
	}
	
	private void DeleteUser(){
		
		try{
		
			conn.removeUser("edit4_test");
		
			String response = conn.listUsers();
			ahe msg = AHEMessageAPI.parseXML(response);
			
			for(user u : msg.getUser_list()){
				
				if(u.getUsername().equals("edit4_test")){
					assertNull("User should've been deleted");
				}
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
			assertNull(e);
		}
		
	}
	
	private void getUserList(){
		
		try{
		
			String response = conn.listUsers();
			ahe msg = AHEMessageAPI.parseXML(response);
			
			//check for u1 and u2
			
			boolean user1_found = false, user2_found =false;
			
			for(user u : msg.getUser_list()){
				
				if(u.getUsername().equals("u1_test"))
					user1_found = true;
				else if(u.getUsername().equals("u2_test"))
					user2_found = true;
				
				
				if(user1_found && user2_found){
					assertTrue(true);
					return;
				}
				
			}
			
			assertTrue(user1_found && user2_found);
			
		}catch(Exception e){
			e.printStackTrace();
			assertNull(e);
		}
		
	}
	
	private void AddResource(){
		
		//curl -k -E aheadmin.pem -X POST https://localhost:8111/resource --data-urlencode "name=TestMeow" --data-urlencode "resource_interface=globus" 
		//--data-urlencode "type=ngs" --data-urlencode "arch=x86" --data-urlencode "cpucount=64" 
		//--data-urlencode "memory=1000" --data-urlencode "vmemory=10000" --data-urlencode "walltimelimit=1000" 
		//--data-urlencode "opsys=rock" --data-urlencode "ip=321323" --data-urlencode "endpoint_reference=test.meow.ac.uk"

		try{
		
			HashMap<String, String> arg = new HashMap<String, String>();
			
			arg.put(rest_arg.name.toString(), "res1_test");
			arg.put(rest_arg.resource_interface.toString(), ResourceCode.GLOBUS.toString());
			arg.put(rest_arg.arch.toString(), "x86");
			arg.put(rest_arg.cpucount.toString(), "64");
			arg.put(rest_arg.memory.toString(), "1000");
			arg.put(rest_arg.vmemory.toString(), "10000");
			arg.put(rest_arg.walltimelimit.toString(), "1000");
			arg.put(rest_arg.opsys.toString(), "win");
			arg.put(rest_arg.ip.toString(), "1.1.1.1");
			arg.put(rest_arg.endpoint_reference.toString(), "endpoint1.ac.uk");
			
			String response = conn.createResource(arg);
			
			arg = new HashMap<String, String>();
			
			arg.put(rest_arg.name.toString(), "res2_test");
			arg.put(rest_arg.resource_interface.toString(), ResourceCode.UNICORE.toString());
			arg.put(rest_arg.arch.toString(), "x86");
			arg.put(rest_arg.cpucount.toString(), "64");
			arg.put(rest_arg.memory.toString(), "5000");
			arg.put(rest_arg.vmemory.toString(), "50000");
			arg.put(rest_arg.walltimelimit.toString(), "5000");
			arg.put(rest_arg.opsys.toString(), "win");
			arg.put(rest_arg.ip.toString(), "15.15.1.1");
			arg.put(rest_arg.endpoint_reference.toString(), "endpoint2.ac.uk");
			
			response = conn.createResource(arg);
			
			arg = new HashMap<String, String>();
			
			arg.put(rest_arg.name.toString(), "res3_test");
			arg.put(rest_arg.resource_interface.toString(), ResourceCode.GSIFTP.toString());
			arg.put(rest_arg.endpoint_reference.toString(), "endpoint3.ac.uk");
			
			response = conn.createResource(arg);
			
			arg = new HashMap<String, String>();
			
			arg.put(rest_arg.name.toString(), "res_edit_test");
			arg.put(rest_arg.resource_interface.toString(), ResourceCode.UNICORE.toString());
			arg.put(rest_arg.arch.toString(), "x86");
			arg.put(rest_arg.cpucount.toString(), "64");
			arg.put(rest_arg.memory.toString(), "5000");
			arg.put(rest_arg.vmemory.toString(), "50000");
			arg.put(rest_arg.walltimelimit.toString(), "5000");
			arg.put(rest_arg.opsys.toString(), "win");
			arg.put(rest_arg.ip.toString(), "15.15.1.1");
			arg.put(rest_arg.endpoint_reference.toString(), "endpoint5.ac.uk");
			
			response = conn.createResource(arg);
			
			//List resource to double check entries
			response = conn.listResource();
			ahe msg = AHEMessageAPI.parseXML(response);
			
			boolean res1 = false, res2 =false, res3 = false;
			
			for(resource r : msg.getResource_list()){
				
				if(r.getName().equals("res1_test")){
					
					assertEquals(ResourceCode.GLOBUS.toString(), r.getResource_interface());
					assertEquals("x86", r.getArch());
					assertEquals("64", r.getCpu_count());
					assertEquals("1000", r.getMemory());
					assertEquals("10000", r.getVmemory());
					assertEquals("10000", r.getWalltimelimit());
					assertEquals("win", r.getOpsys());
					assertEquals("1.1.1.1", r.getIp());
					assertEquals("endpoint1.ac.uk", r.getUri());
					
					res1 = true;
				}else if(r.getName().equals("res2_test")){
					
					res2 = true;
				}else if(r.getName().equals("res3_test")){
					
					assertEquals(ResourceCode.GSIFTP.toString(),r.getResource_interface() );
					assertEquals("endpoint3.ac.uk", r.getUri());
				
					res3 = true;
				}
				
				
				if(res1 && res2 && res3){
					assertTrue(true);
					return;
				}
				
			}
			
			assertTrue(res1 && res2 && res3);
			
		}catch(Exception e){
			e.printStackTrace();
			assertNull(e);
		}
		
	}
	
	private void EditResource(){
		
		try{
		
			HashMap<String, String> arg = new HashMap<String, String>();
			
			arg.put(rest_arg.name.toString(), "edited_resource");
			arg.put(rest_arg.resource_interface.toString(), ResourceCode.GLOBUS.toString());
			arg.put(rest_arg.arch.toString(), "x");
			arg.put(rest_arg.cpucount.toString(), "1");
			arg.put(rest_arg.memory.toString(), "1");
			arg.put(rest_arg.vmemory.toString(), "1");
			arg.put(rest_arg.walltimelimit.toString(), "1");
			arg.put(rest_arg.opsys.toString(), "edit");
			arg.put(rest_arg.ip.toString(), "1111");
			arg.put(rest_arg.endpoint_reference.toString(), "1");
			
			String response = conn.editResource("res_edit_test", arg);
			
			response = conn.listResource();
			ahe msg = AHEMessageAPI.parseXML(response);
	
			
			for(resource r : msg.getResource_list()){
				
				if(r.getName().equals("edited_resource")){
					
					assertEquals(ResourceCode.GLOBUS.toString(), r.getResource_interface());
					assertEquals("x", r.getArch());
					assertEquals((int) 1,(int) r.getCpu_count());
					assertEquals((int) 1,(int) r.getMemory());
					assertEquals((int) 1,(int) r.getVmemory());
					assertEquals((int) 1,(int) r.getWalltimelimit());
					assertEquals("edit", r.getOpsys());
					assertEquals("1111", r.getIp());
					assertEquals("1", r.getUri());
								
					return;				
				}
				
			}
			
			assertTrue(false);
			
		}catch(Exception e){
			e.printStackTrace();
			assertNull(e);
		}
		
	}
	
	private void DeleteResource(){
		
		try{
		
			conn.removeResource("edited_resource");

			String response = conn.listResource();
			ahe msg = AHEMessageAPI.parseXML(response);

			for(resource r : msg.getResource_list()){
				
				if(r.getName().equals("edited_resource")){
					assertNull("Resource should've been deleted");
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			assertNull(e);
		}
		
	}
	
	private void AddCred(){
		
		try {

			HashMap<String, String> arg = new HashMap<String, String>();
			
			arg.put(rest_arg.credential_id.toString(), "cred1_test");
			arg.put(rest_arg.credential_location.toString(), "loc1");
			arg.put(rest_arg.proxy_location.toString(), "proxy1");
			arg.put(rest_arg.user_key.toString(), "key1");
			arg.put(rest_arg.cert_dir.toString(), "cert1");
			arg.put(rest_arg.type.toString(), AuthenCode.globus_default.toString());
			
			String response = conn.addCredential(arg);
		
	
			arg = new HashMap<String, String>();
			
			arg.put(rest_arg.credential_id.toString(), "cred2_test");
			arg.put(rest_arg.credential_location.toString(), "loc2");
			arg.put(rest_arg.registery.toString(), "http://site2");
			arg.put(rest_arg.pwd.toString(), "pwd2");
			arg.put(rest_arg.type.toString(), AuthenCode.unicore_default.toString());
			
			response = conn.addCredential(arg);

			arg = new HashMap<String, String>();
			
			arg.put(rest_arg.credential_id.toString(), "cred3_test");
			arg.put(rest_arg.username.toString(), "username3");
			arg.put(rest_arg.pwd.toString(), "password3");
			arg.put(rest_arg.type.toString(), AuthenCode.username_password.toString());
			
			response = conn.addCredential(arg);
			
			arg = new HashMap<String, String>();
			
			arg.put(rest_arg.credential_id.toString(), "cred_edit_test");
			arg.put(rest_arg.credential_location.toString(), "loc2");
			arg.put(rest_arg.registery.toString(), "http://site2");
			arg.put(rest_arg.pwd.toString(), "pwd2");
			arg.put(rest_arg.credential_location.toString(), "loc1");
			arg.put(rest_arg.proxy_location.toString(), "proxy1");
			arg.put(rest_arg.user_key.toString(), "key1");
			arg.put(rest_arg.cert_dir.toString(), "cert1");
			arg.put(rest_arg.type.toString(), AuthenCode.unicore_default.toString());
			
			response = conn.addCredential(arg);
			
			//List credential
			
			response = conn.listCredentials();
			
			ahe msg = AHEMessageAPI.parseXML(response);
			
			boolean cred1 = false, cred2 = false, cred3 = false;
			
			for(credential c : msg.getCred_list()){
				
				if(c.getName().equals("cred1_test")){
					
					assertEquals("loc1", c.getCred_location());
					assertEquals("proxy1", c.getProxy_location());
					assertEquals("key1", c.getUser_key());
					assertEquals("cert1", c.getCertificate_directory());
					assertEquals(AuthenCode.globus_default.toString(), c.getType());
				
					cred1 = true;
				}else if(c.getName().equals("cred2_test")){
					
					assertEquals("loc2", c.getCred_location());
					assertEquals("http://site2", c.getRegistry());
					assertEquals(AuthenCode.unicore_default.toString(), c.getType());
					
					
					cred2 = true;
				}else if(c.getName().equals("cred3_test")){
					
					assertEquals("username3", c.getUsername());
					assertEquals(AuthenCode.username_password.toString(), c.getType());
					
					cred3 = true;
				}
				
				
			}
			
			assertTrue(cred1 && cred2 && cred3);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
			
	}
	
	private void EditCred(){
		
		try{
		
			HashMap<String, String> arg = new HashMap<String, String>();
			
			arg.put(rest_arg.credential_id.toString(), "edited");
			arg.put(rest_arg.credential_location.toString(), "2");
			arg.put(rest_arg.registery.toString(), "2");
			arg.put(rest_arg.pwd.toString(), "2");
			arg.put(rest_arg.proxy_location.toString(), "2");
			arg.put(rest_arg.user_key.toString(), "2");
			arg.put(rest_arg.cert_dir.toString(), "2");
			arg.put(rest_arg.type.toString(), AuthenCode.globus_default.toString());
			
			String response = conn.editCredential("cred_edit_test", arg);
		
			response = conn.listCredentials();
			
			ahe msg = AHEMessageAPI.parseXML(response);
			
			
			for(credential c : msg.getCred_list()){
				
				if(c.getName().equals("edited")){
					
					assertEquals("2", c.getCred_location());
					assertEquals("2", c.getRegistry());
					assertEquals("2", c.getProxy_location());
					assertEquals("2", c.getUser_key());
					assertEquals("2", c.getCertificate_directory());
					assertEquals( AuthenCode.globus_default.toString(), c.getType());
					return;
				}
				
			}
			
			assertNull("Edited Object Not Found");
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
	}
	
	private void DeleteCred(){
		
		try{
			
			conn.removeCredential("edited");
			
			String response = conn.listCredentials();
			
			ahe msg = AHEMessageAPI.parseXML(response);
			
			
			for(credential c : msg.getCred_list()){
				
				if(c.getName().equals("edited")){
					assertNull("Cred should've been deleted");
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
		
		
	}
	
	private void AddApp(){
		
		//curl -k -E aheadmin.pem -X POST https://localhost:8111/app --data-urlencode "appname=ls" 
		//--data-urlencode "exec=/bin/ls" --data-urlencode "resource_name=TestMeow"

		try {
		
			HashMap<String, String> arg = new HashMap<String, String>();
			
			arg.put(rest_arg.appname.toString(), "ls");
			arg.put(rest_arg.exec.toString(), "bin/test/ls");
			arg.put(rest_arg.resource_name.toString(), "res1_test");
			arg.put(rest_arg.description.toString(), "test test test");
			
			String response = conn.createResource(arg);
			
			arg = new HashMap<String, String>();
			
			arg.put(rest_arg.appname.toString(), "ls");
			arg.put(rest_arg.exec.toString(), "bin/test/ls");
			arg.put(rest_arg.resource_name.toString(), "res2_test");

			response = conn.createResource(arg);
			
			arg = new HashMap<String, String>();
			
			arg.put(rest_arg.appname.toString(), "ls");
			arg.put(rest_arg.exec.toString(), "bin/test/ls");
			arg.put(rest_arg.resource_name.toString(), "res3_test");

			response = conn.createResource(arg);
			
			arg = new HashMap<String, String>();
			
			arg.put(rest_arg.appname.toString(), "date");
			arg.put(rest_arg.exec.toString(), "bin/test/date");
			arg.put(rest_arg.resource_name.toString(), "res2_test");

			response = conn.createResource(arg);
			
			arg = new HashMap<String, String>();
			
			arg.put(rest_arg.appname.toString(), "date_edit_test");
			arg.put(rest_arg.exec.toString(), "bin/test/date");
			arg.put(rest_arg.resource_name.toString(), "res2_test");

			response = conn.createResource(arg);
			
			//Check Resource list
			
			response = conn.listResource();
			ahe msg = AHEMessageAPI.parseXML(response);

			boolean ls1 = false, ls2 = false, ls3 = false, date = false;
			
			for(app a : msg.getApp_list()){
				
				if(a.getName().equalsIgnoreCase("ls") && a.getResource_id().equals("res1_test")){
					
					assertEquals("bin/test/ls", a.getExecutable());
					assertEquals("test test test", a.getDescription());
					
					ls1 = true;
				}else if(a.getName().equalsIgnoreCase("ls") && a.getResource_id().equals("res2_test")){
					ls2 = true;
				}else if(a.getName().equalsIgnoreCase("ls") && a.getResource_id().equals("res3_test")){
					ls3 = true;
				}else if(a.getName().equalsIgnoreCase("date") && a.getResource_id().equals("res2_test")){
					date = true;
				}
				
			}
			
			assertTrue(ls1 && ls2 && ls3 && date);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
		
	}
	
	private void EditApp(){
		
		try{
			
			String response = conn.listResource();
			ahe msg = AHEMessageAPI.parseXML(response);
			
			int app_id = -1;
			
			for(app a : msg.getApp_list()){
				
				if(a.getName().equalsIgnoreCase("date_edit_test") && a.getResource_id().equals("res2_test")){
					app_id = a.getId();
				}
				
			}
			
			if(app_id == -1){
				assertNull("App Not Found");
				return;
			}
			
			HashMap<String, String> arg = new HashMap<String, String>();
			
			arg.put(rest_arg.appname.toString(), "date_edit_test");
			arg.put(rest_arg.exec.toString(), "bin/test/date");
			arg.put(rest_arg.resource_name.toString(), "res1_test");

			response = conn.editResource(""+app_id,arg);
			
			//Check Resource list
			
			response = conn.listResource();
			msg = AHEMessageAPI.parseXML(response);

			
			for(app a : msg.getApp_list()){
				
				if(a.getName().equalsIgnoreCase("date_edit_test") && a.getResource_id().equals("res1_test")){

					assertEquals("bin/test/date", a.getExecutable());
					
					return;
				}
				
			}
			
			assertNull("App Not Found");
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
		
		
	}
	
	private void DeleteApp(){
		
		try{
			
			String response = conn.listResource();
			ahe msg = AHEMessageAPI.parseXML(response);
			
			int app_id = -1;
			
			for(app a : msg.getApp_list()){
				
				if(a.getName().equalsIgnoreCase("date_edit_test") && a.getResource_id().equals("res2_test")){
					app_id = a.getId();
				}
				
			}
			
			if(app_id == -1){
				assertNull("App Not Found");
				return;
			}
			
			conn.removeApp(""+app_id);
			
			response = conn.listResource();
			 msg = AHEMessageAPI.parseXML(response);

			
			for(app a : msg.getApp_list()){
				
				if(a.getName().equalsIgnoreCase("date_edit_test") && a.getResource_id().equals("res2_test")){
					assertNull("App Should've been deleted");
				}
				
			}
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
		
	}
	
	private void MapUserToCred(){
		
		try{
		
			//curl -k -E aheadmin.pem -X POST https://localhost:8111/profile/AHEUserTest/cred -d "credential_id=up1"
			HashMap<String, String> arg = new HashMap<String, String>();
			arg.put(rest_arg.credential_id.toString(), "cred_test1");
			
			String response = conn.addUserCredential("u1_test", "cred1_test");
			response = conn.addUserCredential("u1_test", "cred2_test");
			response = conn.addUserCredential("u2_test", "cred1_test");
			response = conn.addUserCredential("u2_test", "cred3_test");
			
			String r = conn.listUsers();
			ahe msg = AHEMessageAPI.parseXML(r);
			
			boolean u1_test = false, u2_test = false;
			
			for(user u : msg.getUser_list()){
				
				if(u.getUsername().equals("u1_test")){
					
					assertEquals(1, u.getCred_list().size());
					assertEquals("cred2_test", u.getCred_list().get(0));
					
					u1_test = true;
					
				}else if(u.getUsername().equals("u2_test")){
					
					assertEquals(2, u.getCred_list().size());

					boolean cred1 = false, cred2 = false;
					
					for(credential c : u.getCred_list()){
						
						if(c.getName().equals("cred1_test")){
							cred1 = true;
						}else if(c.getName().equals("cred3_test")){
							cred2 = true;
						}
						
					}
					
					assertTrue(cred1 && cred2);
					u2_test = true;
				}
				
			}
			
			assertTrue(u1_test && u2_test);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
			
	}
	
	private void MapResourceToCred(){
		
		try{
			
			//curl -k -E aheadmin.pem -X POST https://localhost:8111/resource/LOBCDER/cred -d "credential_id=up1"
			HashMap<String, String> arg = new HashMap<String, String>();
			arg.put(rest_arg.credential_id.toString(), "cred_test1");
			
			String r = conn.mapCredentialToResource("res1_test","cred1_test");
			r = conn.mapCredentialToResource("res2_test","cred2_test");
			r = conn.mapCredentialToResource("res3_test","cred3_test");
			
			r = conn.listResource();
			ahe msg = AHEMessageAPI.parseXML(r);
			
			boolean cred1 = false, cred2 = false, cred3 = false;
			
			for(credential c : msg.getCred_list()){
				
				if(c.getName().equals("cred1_test")){
					
					assertEquals("res1_test", c.getResource_list().get(0));
					cred1 = true;
				}else if(c.getName().equals("cred2_test")){
					assertEquals("res2_test", c.getResource_list().get(0));
					cred2 = true;
				}else if(c.getName().equals("cred3_test")){
					assertEquals("res3_test", c.getResource_list().get(0));
					cred3= true;
				}
				
				
			}
			
			assertTrue(cred1 && cred2 && cred3);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
		
		
	}
	
	private void unMapUserToCred(){
		
		try{
			
			conn.removeUserCredential("u1_test", "cred1_test");
			conn.removeUserCredential("u2_test", "cred3_test");
			
			String r = conn.listUsers();
			ahe msg = AHEMessageAPI.parseXML(r);
			
			for(user u : msg.getUser_list()){
				
				if(u.getUsername().equals("u1_test")){
					
					for(credential c : u.getCred_list()){
						
						if(c.getName().equals("cred1_test"))
							assertNull("Credential Was not deleted");
						
					}
					
				}else if(u.getUsername().equals("u2_test")){
					
					for(credential c : u.getCred_list()){
						
						if(c.getName().equals("cred3_test"))
							assertNull("Credential Was not deleted");
						
					}
					
				}
				
			}
			
			assertTrue(true);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
		
	}
	
	private void unMapResourceToCred(){
		
		
		try{
			
			conn.unmapCredentialToResource("res1_test","cred1_test");
			
			String r = conn.listResource();
			ahe msg = AHEMessageAPI.parseXML(r);
			
			for(credential c : msg.getCred_list()){
				
				if(c.getName().equals("cred1_test")){
					assertEquals(0, c.getResource_list().size());
				}
				
			}
			
			assertTrue(true);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
		
	}
	
	private void prepare(){
		
		try{
		
			// "appname=grompp&jobname=gh1&cpucount=1&memory=100"
			
			HashMap<String, String> arg = new HashMap<String, String>();
			arg.put(rest_arg.appname.toString(), "ls");
			arg.put(rest_arg.jobname.toString(), "JOB TEST 1");
			arg.put(rest_arg.cpucount.toString(), "1");
			arg.put(rest_arg.memory.toString(), "1");
			arg.put(rest_arg.vmemory.toString(), "1");
			//Prepare Application
			
			String response = conn.appInstance_Prepare(arg);
			
			//List app instance
			
			response = conn.listJobs();
			
			ahe message = AHEMessageAPI.parseXML(response);
			
			for(app_instance c : message.getAppinst_list()){
				
				if(c.getName().equals("JOB TEST 1")){
					assertTrue(true);
					return;
				}
				
			}
			
			assertTrue(false);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
			
	}
	
	private void setupAppInstanceTransfer(){
		
		try{
		
			//List app instance
			
			String response = conn.listJobs();
			
			ahe message = AHEMessageAPI.parseXML(response);
			
			int appinst_id = -1;
			
			for(app_instance c : message.getAppinst_list()){
				
				if(c.getName().equals("JOB TEST 1")){
					appinst_id = c.getId();
					break;
				}
				
			}
			
			if(appinst_id == -1){
				assertNull("AppInst not found");
				return;
			}
			
			HashMap<String, String> arg = new HashMap<String, String>();
			arg.put(rest_arg.stagein.toString(), "[['http://endpoint3.ac.uk/src'],['http://endpoint3.ac.uk/dest']");
			arg.put(rest_arg.stageout.toString(), "[['http://endpoint3.ac.uk/dest'],['http://endpoint3.ac.uk/src']");
			//Setup file transfer
			response = conn.appInstance_SetDataStaging(""+appinst_id, arg);
			
			//List file transfer
			
			response = conn.appInstance_getDataStaging(""+appinst_id);
			message = AHEMessageAPI.parseXML(response);
			
			boolean t1 = false, t2 = false;
			
			for(transfer t : message.getTransfer_list()){
				
				if(t.getSource().equals("http://endpoint3.ac.uk/src") && t.getDest().equals("http://endpoint3.ac.uk/dest")){
					t1 = true;
				}else if(t.getSource().equals("http://endpoint3.ac.uk/dest") && t.getDest().equals("http://endpoint3.ac.uk/src")){
					t2 = true;
				}
				
			}
			
			assertTrue(t1 && t2);
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
		
	}
	
	private void setupAppInstProperty(){
		
		
		try{
			//Setup Job Property
			//app.arg=['-f','quick_sd.mdp','-c','ioned.pdb','-o','em','-p','ioned_topol.top','-maxwarn','1']
			//work_dir='somewhere'
			
			//List app instance
			
			String response = conn.listJobs();
			
			ahe message = AHEMessageAPI.parseXML(response);
			
			int appinst_id = -1;
			
			for(app_instance c : message.getAppinst_list()){
				
				if(c.getName().equals("JOB TEST 1")){
					appinst_id = c.getId();
					break;
				}
				
			}
			
			if(appinst_id == -1){
				assertNull("AppInst not found");
				return;
			}
		
			HashMap<String, String> arg = new HashMap<String, String>();
			arg.put(rest_arg.app_arg.toString(), "['-f','quick_sd.mdp','-c','ioned.pdb','-o','em','-p','ioned_topol.top','-maxwarn','1']");
			arg.put(rest_arg.work_dir.toString(), "workdir");
			
			conn.appInstance_SetProperty(""+appinst_id, arg);
			
			//List job property
		
			response = conn.appInstance_GetProperty(""+appinst_id);
			message = AHEMessageAPI.parseXML(response);
			
			boolean p1 = false, p2 = false;
			
			for(property p : message.getProperty_list()){
				
				if(p.getName().equals(rest_arg.app_arg.toString()) && p.getValue().equals("['-f','quick_sd.mdp','-c','ioned.pdb','-o','em','-p','ioned_topol.top','-maxwarn','1']"))
					p1 = true;
				else if(p.getName().equals(rest_arg.work_dir.toString()) && p.getValue().equals("workdir"))
					p2 = true;
				
			}
			
			assertTrue(p1 && p2);
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
		
	}
	
	private void runAppInstance(){
		
		try{
		
			String response = conn.listJobs();
			
			ahe message = AHEMessageAPI.parseXML(response);
			
			int appinst_id = -1;
			
			for(app_instance c : message.getAppinst_list()){
				
				if(c.getName().equals("JOB TEST 1")){
					appinst_id = c.getId();
					break;
				}
				
			}
			
			if(appinst_id == -1){
				assertNull("AppInst not found");
				return;
			}
			
			conn.appInstance_Start(""+appinst_id, new HashMap<String, String>());
			
			response = conn.appInstance_Status(""+appinst_id);
			ahe msg = AHEMessageAPI.parseXML(response);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNull(e);
		}
		
	}
	
	private void quickLaunchAppInstance(){
		
		//Execute Job in one command
		
		//List transfers
		
		//List properties
		
	}
	
	private void createTransfer(){
		
		
	}
	
	private void cloneAppInstance(){
		
		
	}
	
	private void upload(){
		
		
	}
	
	private void session(){
		
		
	}
	
}