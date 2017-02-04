package test.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import uk.ac.ucl.chem.ccs.AHECore.Definition.AHEConfigurationProperty;
import uk.ac.ucl.chem.ccs.AHECore.Engine.ResourceRegisterAPI;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHESecurityException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.NoModuleProviderException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.WorkflowDescription;
import uk.ac.ucl.chem.ccs.AHECore.ModuleHandler.ModuleAccess;
import uk.ac.ucl.chem.ccs.AHECore.Security.AuthenCode;
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;
import uk.ac.ucl.chem.ccs.AHECore.Workflow.WorkflowAPI;

public class Helper {
	public static void helper() throws AHESecurityException, NoModuleProviderException, URISyntaxException{
		//public static void main(String[] arg) throws NoModuleProviderException, URISyntaxException{
		
		PlatformCredential aws_cre = SecurityUserAPI.createAwsCredentailDetail(
				"aws_credential",AuthenCode.aws_default, "alliance",
				"", "", 
				"/Users/zeqianmeng/Desktop/PhD/research/3rd_year/2nd_phase/aws/aws_credentials/testbed_ec2_key.pem", 
				"/Users/zeqianmeng/Desktop/PhD/research/3rd_year/2nd_phase/aws/aws_credentials","");
		
		Resource resource = ResourceRegisterAPI.getResource("alliance");
		System.out.println("cpu count: " + resource.getCpucount());
		System.out.println("======" + SecurityUserAPI.getResource_PlatformCredentialList(resource));
		
		PlatformCredential pgc = SecurityUserAPI.getPlatformCredential("aws_credential");
		System.out.println("=====" + aws_cre.getAuthen_type());
		AHEUser sophie = SecurityUserAPI.getUser("Sofia");
		SecurityUserAPI.addCredentialToUser(sophie, pgc);	
		System.out.println("*****" + sophie.getCredentials().iterator().next());
		
		/*String[] provider_info;
		URI job_service;
		provider_info = ModuleAccess.getModuleProvider().getModuleProperty(AHEConfigurationProperty.module_jobservice_globus.toString());
		job_service = new URI(provider_info[0]);
		System.out.println(job_service);*/
		//Resource resource = ResourceRegisterAPI.getResource("alliance");
		//SecurityUserAPI.getResource_PlatformCredentialList(resource);
		//System.out.println(SecurityUserAPI.getResource_PlatformCredentialList(resource).length);
		//SecurityUserAPI.addCredentialToResource("aws_credential", "alliance");
	    //WorkflowAPI.deleteWorkflowDescription("AwsWorkflow");
		//HibernateUtil.Delete(145);
		//WorkflowAPI.createWorkflowDescription("AwsWorkflow", "main/resources/AwsWorkflow.bpmn", "awstest", false);
		//WorkflowDescription d = WorkflowAPI.getWorkflowDescription("AHEWorkflow2");
		//URI link = new URI("www.google.co.uk");
		//System.out.println(link.getPath());
		//System.out.println(d.getId());
		//WorkflowAPI.deleteWorkflowDescription("AwsWorkflow");
		//System.out.println(d.getWorkflow_filename());
	}
	public static void writeout(String content) {
		try {
			File file = new File("/Users/zeqianmeng/Documents/workspace/AHE3/test_data/helper.txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
