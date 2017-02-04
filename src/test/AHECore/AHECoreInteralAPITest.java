package test.AHECore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import uk.ac.ucl.chem.ccs.AHECore.Engine.AHEEngine;
import uk.ac.ucl.chem.ccs.AHECore.Engine.AppRegisteryAPI;
import uk.ac.ucl.chem.ccs.AHECore.Engine.ResourceRegisterAPI;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHESecurityException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AppAlreadyExistException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AppInstanceAlreadyExistsException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppRegistery;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.FileStaging;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHECore.Security.AuthenCode;
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;
import uk.ac.ucl.chem.ccs.AHEModule.Def.AHE_SECURITY_TYPE;
import uk.ac.ucl.chem.ccs.AHEModule.Def.RESOURCE_AUTHEN_TYPE;
//import uk.ac.ucl.chem.ccs.AHEModule.UCC.Hibernate.Entity.CredentialEntity;

/**
 * Test Internal AHE API
 * 
 * Create User, Resource, Credential, Application
 * Edit User, Resource, Credential Application
 * Map User - Credential
 * Delete mapping
 * Disable User, Resource, Credential, Application
 * 
 * @author davidc
 *
 */

public class AHECoreInteralAPITest {

	@Test
	public void CoreInternalTest(){
		
		try {
		
			File databaseFile = new File("data/ahecore.h2.db");
			
			if(databaseFile.exists())
				databaseFile.delete();
			
			//Create
			Resource r = ResourceRegisterAPI.createResource("r_test", "endpoint1.ac.uk", "globus","ngs", 34, "linux", 1000, 100000, "rock", "2.2.2", 1000,-1,RESOURCE_AUTHEN_TYPE.PROXY_CERTIFICATE.toString(),"");
			Resource r1 = ResourceRegisterAPI.createResource("r1_test", "endpoint2.ac.uk", "unicore","ttt", 100, "linux", 2000, 200000, "rock", "2.2.2", 1000,-1,RESOURCE_AUTHEN_TYPE.PROXY_CERTIFICATE.toString(),"");
			
			
			assertEquals(r1.getCommonname(), "r1_test");
			assertEquals(r1.getEndpoint_reference(), "endpoint2.ac.uk");
			assertEquals(r1.getResource_interface(), "unicore");
			assertEquals(r1.getType(), "ttt");
			assertEquals(r1.getCpucount(),100);
			assertEquals(r1.getArch(),"linux");
			assertEquals(r1.getMemory(),2000);
			assertEquals(r1.getVirtualmemory(),200000);
			
			assertEquals(r1.getOpsys(),"rock");
			assertEquals(r1.getIp(),"2.2.2");
			assertEquals(r1.getWalltimelimit(),1000);
			assertEquals(r1.getPort(),-1);
			assertEquals(r1.getAuthen_type(),RESOURCE_AUTHEN_TYPE.PROXY_CERTIFICATE.toString());
			assertEquals(r1.getDescription(),"");
			
			assertNotNull(r);
			assertNotNull(r1);
	
			//List resource
			Resource[] rlist = ResourceRegisterAPI.getResourceList();
			assertEquals("endpoint1.ac.uk", rlist[0].getEndpoint_reference());
			assertEquals(2, rlist.length);
	
			Resource[] rl = ResourceRegisterAPI.searchResourceMinimumRequirement("globus","ngs", 30, "linux", 5, 5, "rock", 1);
			assertEquals(1, rl.length);
			
			Resource[] r2 = ResourceRegisterAPI.searchResourceMinimumRequirement("","", 30, "", 5, 5, "", 1);
			assertEquals(2, r2.length);
			
			//Get Resource
			Resource x = ResourceRegisterAPI.getResource("r1_test");
			
			assertEquals(2000, x.getMemory());
		
		

			Resource updated = ResourceRegisterAPI.updateResource("r_test", "r_test", "new_endpoint", "new_r", "fds", 2, "rock",3, 2, "sys", "1.1.1.1", 2111, 0, RESOURCE_AUTHEN_TYPE.PROXY_CERTIFICATE.toString(), "This is a new description");
			
			assertEquals(updated.getCommonname(), "r_test");
			assertEquals(updated.getEndpoint_reference(), "new_endpoint");
			assertEquals(updated.getResource_interface(), "new_r");
			assertEquals(updated.getType(), "fds");
			assertEquals(updated.getCpucount(),2);
			assertEquals(updated.getArch(),"rock");
			assertEquals(updated.getMemory(),3);
			assertEquals(updated.getVirtualmemory(),2);
			
			assertEquals(updated.getOpsys(),"sys");
			assertEquals(updated.getIp(),"1.1.1.1");
			assertEquals(updated.getWalltimelimit(),2111);
			assertEquals(updated.getPort(),0);
			assertEquals(updated.getAuthen_type(),RESOURCE_AUTHEN_TYPE.PROXY_CERTIFICATE.toString());
			assertEquals(updated.getDescription(),"This is a new description");
			

			
			
			
			AppRegistery reg = null;
			AppRegistery reg1 = null;
			AppRegistery reg3= null;
			

			
			//Create App

			
			reg = AppRegisteryAPI.createApplication("ls", "/bin/ls","r_test");
			reg3 = AppRegisteryAPI.createApplication("ls", "/bin/ls","r1_test");
			reg1 = AppRegisteryAPI.createApplication("echo", "/bin/echo","r1_test");
			
			assertEquals("ls", reg.getAppname());
			assertEquals("/bin/ls", reg.getExecutable());
			assertEquals("r_test",reg.getResource().getCommonname());
			assertEquals("", reg.getDescription());
			
			assertNotNull(reg);
			assertNotNull(reg1);
			
			long reg_id = reg.getId();
			
			//List app
			
			AppRegistery[] list = AppRegisteryAPI.getApplicationList();
			assertTrue(list.length > 0);
			
			//Get App
			
			AppRegistery[] search = AppRegisteryAPI.getApplication("ls");
			assertEquals(2, search.length);
			assertNotNull(AppRegisteryAPI.getApplication("ls", "r1_test"));
			
			//Edit App
			
			AppRegistery appupdated = AppRegisteryAPI.editApplication(reg_id, "ls2", "/bin/ls2", "r_test", "test test test");
			
			assertEquals("ls2", appupdated.getAppname());
			assertEquals("/bin/ls2", appupdated.getExecutable());
			assertEquals("r_test",appupdated.getResource().getCommonname());
			assertEquals("test test test", appupdated.getDescription());


		
			AHEUser user = null;
			AHEUser user1 = null;
			AHEUser user2 = null;
			
		

			
			//Create User
			
			user = SecurityUserAPI.createUser("u1_test", "admin", "dd", "fds@fds.com", "Alternate Id1","ACD_VO",AHE_SECURITY_TYPE.ACD);
			user1 = SecurityUserAPI.createUser("u2_test", "admin", "dd", "fds@fds.com", "Alternate Id2","",AHE_SECURITY_TYPE.AHE_PASSWORD);
			user2 = SecurityUserAPI.createUser("u3_test", "admin", "dd", "fds@fds.com", "emailAddress=aheadmin@ucl.ac.uk,CN=AHE Admin,OU=CCS,O=UCL,L=London,ST=London,C=UK","",AHE_SECURITY_TYPE.AHE_SSL_CLIENT);

			assertEquals("u1_test", user.getUsername());
			assertEquals("u2_test", user1.getUsername());
			assertEquals("u3_test", user2.getUsername());
			
			assertEquals("admin", user.getRole());
			assertEquals("fds@fds.com", user.getEmail());
			assertEquals("Alternate Id1", user.getAlt_identifer());
			assertEquals("ACD_VO", user.getAcd_vo_group());
			assertEquals("ACD", user.getSecurity_type());

			
			//List User
			
			AHEUser[] userlist = SecurityUserAPI.getUserList();
			assertEquals(3, userlist.length);
			
			//Get User
			
			AHEUser searchByUsername = SecurityUserAPI.getUser("u1_test");
			assertNotNull(searchByUsername);
			
			AHEUser searchbyId = SecurityUserAPI.getUserByAltIdent("Alternate Id2");
			assertEquals("u2_test", searchbyId.getUsername());
			
			AHEUser searchbySubject = SecurityUserAPI.getUserBySubjectDN("EMAILADDRESS=aheadmin@ucl.ac.uk,  CN=AHE Admin,OU=CCS,o=UCL,L=London, st=London,C=UK");
			assertEquals("u3_test", searchbySubject.getUsername());

			//Edit User
			
			AHEUser user_updated = SecurityUserAPI.editUser("u1_test", "u1_test", "Woof", "new@em.com", "admin", "new_id", AHE_SECURITY_TYPE.AHE_PASSWORD);
			
			assertEquals("u1_test", user_updated.getUsername());
			assertEquals("new@em.com", user_updated.getEmail());
			assertEquals("admin", user_updated.getRole());
			assertEquals("new_id", user_updated.getAlt_identifer());
			assertEquals("AHE_PASSWORD", user_updated.getSecurity_type());

			//Create Cred
			PlatformCredential generic_cred;
			PlatformCredential cred1;
			PlatformCredential cred2;
			PlatformCredential cred3;

			 generic_cred = SecurityUserAPI.createCredentialDetail("generic_test", AuthenCode.myproxy.toString(), "", "credlocation", "proxy", "userkey", "certdir", "reg", "username", "password", "alias", "trustpath", "trustpassword");
			cred1 = SecurityUserAPI.createCredentialDetail("globus_test", AuthenCode.globus_default.toString(), "", "cred_location1", "proxy1", "userkey1", "cert1", "", "", "pw1", "", "", "");
			cred2 = SecurityUserAPI.createCredentialDetail("unicore_test", AuthenCode.unicore_default.toString(), "", "KEYSTORE", "", "", "", "registry", "alias", "pw1", "cred_alias", "trust_path", "trust_pwd");
			cred3 = SecurityUserAPI.createCredentialDetail("pwd_test", AuthenCode.username_password.toString(), "", "", "", "", "", "", "username", "password", "", "", "");

			assertEquals("generic_test", generic_cred.getCredential_id());
			assertEquals("myproxy", generic_cred.getAuthen_type());
			assertEquals("credlocation", generic_cred.getCredential_location());
			assertEquals("proxy", generic_cred.getProxy_location());
			assertEquals("userkey", generic_cred.getUser_key());
			assertEquals("certdir", generic_cred.getCertificate_directory());
			assertEquals("reg", generic_cred.getRegistry_path());
			assertEquals("username", generic_cred.getUsername());
			assertEquals("password", generic_cred.getPassword());
			assertEquals("alias", generic_cred.getCredential_alias());
			assertEquals("trustpath", generic_cred.getTruststore_path());
			assertEquals("trustpassword", generic_cred.getTruststore_password());
			

			//List Cred
			
			PlatformCredential[] plist = SecurityUserAPI.getPlatformCredentialList();
			assertEquals(4, plist.length);
			
			//Get Cred
			
			PlatformCredential psearch = SecurityUserAPI.getPlatformCredential("globus_test");
			assertNotNull(psearch);
			
			
			//Edit Cred
			
			PlatformCredential pupdated = SecurityUserAPI.editCredentialDetail("generic_test", "generic_test1", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a");
			
			assertEquals("generic_test1", pupdated.getCredential_id());
			assertEquals("a", pupdated.getAuthen_type());
			assertEquals("a", pupdated.getCredential_location());
			assertEquals("a", pupdated.getProxy_location());
			assertEquals("a", pupdated.getUser_key());
			assertEquals("a", pupdated.getCertificate_directory());
			assertEquals("a", pupdated.getUsername());
			assertEquals("a", pupdated.getPassword());
			assertEquals("a", pupdated.getCredential_alias());
			assertEquals("a", pupdated.getTruststore_path());
			assertEquals("a", pupdated.getTruststore_password());
			
			//Map User,Cred
			
			SecurityUserAPI.addCredentialToUser(user, cred1);
			SecurityUserAPI.addCredentialToUser(user, cred3);
			SecurityUserAPI.addCredentialToUser(user1, cred2);
			
			
			//List User cred mapping
			
			PlatformCredential[] user_credlist = SecurityUserAPI.getUserCertificateList(user.getUsername());
			assertEquals(2, user_credlist.length);
			
			PlatformCredential[] user1_credlist = SecurityUserAPI.getUserCertificateList(user1.getUsername());
			assertEquals(1, user1_credlist.length);
			
			//Map Resource, Cred
			
			SecurityUserAPI.addCredentialToResource(cred1.getCredential_id(), r.getCommonname());
			SecurityUserAPI.addCredentialToResource(cred2.getCredential_id(), r1.getCommonname());
			SecurityUserAPI.addCredentialToResource(cred3.getCredential_id(), r.getCommonname());
			
			//List cred - resource mapping
			
			PlatformCredential[] plat_search1 = SecurityUserAPI.getResource_PlatformCredentialList(r);
			assertEquals(2, plat_search1.length);
			
			PlatformCredential[] plat_search2 = SecurityUserAPI.getResource_PlatformCredentialList(r1);
			assertEquals(1, plat_search2.length);


		
		

		
		
		

		
		
		//Create AppInstance
		
		AHEEngine engine = AHERuntime.getAHERuntime().getAhe_engine();
		AppRegistery[] appsearch = AppRegisteryAPI.getApplication("ls");



			AppInstance inst = AHEEngine.createAppInstance(user, "Test simulation", appsearch[0]);
			
			long app_inst_id = inst.getId();
			
			AppInstance search_inst = null;
			
			search_inst = AHEEngine.getAppInstanceEntity(app_inst_id);
			
			assertEquals(app_inst_id, search_inst.getId());
			
			//Create File Staging
			
			AHEEngine.createFileStaging(inst, true, "src1", "targ1");
			AHEEngine.createFileStaging(inst, true, "src2", "targ2");
			AHEEngine.createFileStaging(inst, false, "src3", "targ3");
			AHEEngine.createFileStaging(inst, false, "src4", "targ4");
			AHEEngine.createFileStaging(inst, true, "src1", "targ1");
			
			FileStaging[] array = AHEEngine.getFileStageByAppInstId(app_inst_id);
			
			assertEquals(5, array.length);
			
			assertEquals(true, array[0].isStage_in());
			assertEquals(true, array[1].isStage_in());
			assertEquals(false, array[2].isStage_in());
			assertEquals(false, array[3].isStage_in());
			
			assertEquals("src1", array[0].getSource());
			assertEquals("src2", array[1].getSource());
			assertEquals("src3", array[2].getSource());
			assertEquals("src4", array[3].getSource());
			
			assertEquals("targ1", array[0].getTarget());
			assertEquals("targ2", array[1].getTarget());
			assertEquals("targ3", array[2].getTarget());
			assertEquals("targ4", array[3].getTarget());
			
			FileStaging[] in = AHEEngine.getFileStageInByAppInstId(app_inst_id);
			FileStaging[] out = AHEEngine.getFileStageOutByAppInstId(app_inst_id);
			
			assertEquals(3, in.length);
			assertEquals(2, out.length);
			
			assertEquals("src3", out[0].getSource());
			


		//Delete Mapping
		


			
		SecurityUserAPI.removeCredentialfromPlatform(r.getCommonname(),
				cred1.getCredential_id());
		SecurityUserAPI.removeCredentialfromPlatform(r1.getCommonname(),
				cred2.getCredential_id());
		SecurityUserAPI.removeCredentialfromPlatform(r.getCommonname(),
				cred3.getCredential_id());

		plat_search1 = SecurityUserAPI
				.getResource_PlatformCredentialList(r);
		assertEquals(0, plat_search1.length);

		plat_search2 = SecurityUserAPI
				.getResource_PlatformCredentialList(r1);
		assertEquals(0, plat_search2.length);

		
		//Map User,Cred
		
		SecurityUserAPI.removeCredentialfromUser(user, cred1);
		SecurityUserAPI.removeCredentialfromUser(user, cred3);
		SecurityUserAPI.removeCredentialfromUser(user1, cred2);
		
		
		//List User cred mapping
		
		user_credlist = SecurityUserAPI.getUserCertificateList(user.getUsername());
		assertEquals(0, user_credlist.length);
		
		user1_credlist = SecurityUserAPI.getUserCertificateList(user1.getUsername());
		assertEquals(0, user1_credlist.length);
		
		//Delete Resource, User, App, Cred
		
		ResourceRegisterAPI.deleteResource(r.getCommonname());
		ResourceRegisterAPI.deleteResource(r1.getCommonname());
		
		assertEquals(0, ResourceRegisterAPI.getResourceList().length);
		
		AppRegisteryAPI.deleteApplication(reg.getId());
		AppRegisteryAPI.deleteApplication(reg1.getId());
		AppRegisteryAPI.deleteApplication(reg3.getId());
		
		SecurityUserAPI.disablePlatformCredential(cred1.getCredential_id());
		SecurityUserAPI.disablePlatformCredential(cred2.getCredential_id());
		SecurityUserAPI.disablePlatformCredential(cred3.getCredential_id());
		assertEquals(1, SecurityUserAPI.getPlatformCredentialList());
		
		SecurityUserAPI.disableUser(user.getUsername());
		SecurityUserAPI.disableUser(user1.getUsername());
		SecurityUserAPI.disableUser(user2.getUsername());
		assertEquals(0, SecurityUserAPI.getUserList().length);
		
		} catch (AHEException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			assertNotNull(e1);
		} catch (AppInstanceAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertNotNull(e);
		} catch (AppAlreadyExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AHESecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File databaseFile = new File("data/ahecore.h2.db");
		
		if(databaseFile.exists())
			databaseFile.delete();
		
	}

	
}

