package uk.ac.ucl.chem.ccs.AHECore.ModuleHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import uk.ac.ucl.chem.ccs.AHECore.Definition.AHEConfigurationProperty;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEMyProxyDelegationException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.ModuleHandlerException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.NoModuleProviderException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.AHEMessage;

/**
 * Provides MyProxy functions. This class generates the AHEMessage and sends it to the correct AHE module to deal with the proxy request
 * @author davidc
 *
 */

public class MyProxyHandler {

	private static Logger logger = LoggerFactory.getLogger(MyProxyHandler.class);
	
	public MyProxyHandler() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Checks proxy validity
	 * @param credential
	 * @return AHEMessage object with the status
	 * @throws ModuleHandlerException
	 * @throws URISyntaxException
	 * @throws ResourceException
	 * @throws IOException
	 * @throws NoModuleProviderException
	 */
	
	public static AHEMessage checkMyProxyValidity(PlatformCredential credential) throws ModuleHandlerException, URISyntaxException, ResourceException, IOException, NoModuleProviderException{
		
		logger.info("Init MyProxy validity check service call");
		
		AHEMessage msg = AHEMessageUtility.generateMyProxyMessage(credential);
		
		String[] provider_info = ModuleAccess.getModuleProvider().getModuleProperty(AHEConfigurationProperty.module_security_myproxy.toString());
		URI service = new URI(provider_info[0]);
		
		ClientResource x = new ClientResource(service.toString() + "/proxy");
		
		if(provider_info.length > 1){
			//Username & Password is used
			x.setChallengeResponse(ChallengeScheme.HTTP_BASIC, provider_info[1], provider_info[2]);
		}
		
		ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
		
		Gson gson = new Gson();
		
		try{
		
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
	 * Checks MyProxy certificate validity
	 * @param credential
	 * @return proxy certificate validity 
	 * @throws ModuleHandlerException
	 * @throws URISyntaxException
	 * @throws ResourceException
	 * @throws IOException
	 * @throws NoModuleProviderException
	 */
	
	public static boolean checkMyProxyValidity(AHEMessage credential) throws ModuleHandlerException, URISyntaxException, ResourceException, IOException, NoModuleProviderException{
		
		logger.info("Init MyProxy validity check service call");
		
		String[] provider_info = ModuleAccess.getModuleProvider().getModuleProperty(AHEConfigurationProperty.module_security_myproxy.toString());
		URI service = new URI(provider_info[0]);
		
		ClientResource x = new ClientResource(service.toString() + "/proxy");
		
		if(provider_info.length > 1){
			//Username & Password is used
			x.setChallengeResponse(ChallengeScheme.HTTP_BASIC, provider_info[1], provider_info[2]);
		}
		
		ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
		
		Gson gson = new Gson();
		
		try{
			x.post(gson.toJson(credential)).write(stream);
			
			AHEMessage response = gson.fromJson(stream.toString(), AHEMessage.class);
			
			if(!AHEMessageUtility.hasException(response)){
				return true;
			}else
				return false;
		
		}catch(ResourceException e){
			return false;
		}finally{
			x.release();
		}
		
	}
	
	/**
	 * Generate a proxy certificate using the credential information
	 * @param credential credential entity
	 * @return AHEMessage object
	 * @throws ModuleHandlerException
	 * @throws URISyntaxException
	 * @throws ResourceException
	 * @throws IOException
	 * @throws NoModuleProviderException
	 */
	
	public static AHEMessage generateMyProxyCertificate(PlatformCredential credential) throws ModuleHandlerException, URISyntaxException, ResourceException, IOException, NoModuleProviderException{
		
		logger.info("Init Create MyProxy service call");
		
		AHEMessage msg = AHEMessageUtility.generateMyProxyMessage(credential);
		
		String[] provider_info = ModuleAccess.getModuleProvider().getModuleProperty(AHEConfigurationProperty.module_security_myproxy.toString());
		URI service = new URI(provider_info[0]);
		
		ClientResource x = new ClientResource(service.toString() + "");
		
		if(provider_info.length > 1){
			//Username & Password is used
			x.setChallengeResponse(ChallengeScheme.HTTP_BASIC, provider_info[1], provider_info[2]);
		}
		
		ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
		
		Gson gson = new Gson();
		
		try{
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
	 * Generate proxy certificate with the provided credential information
	 * @param credential
	 * @throws ModuleHandlerException
	 * @throws URISyntaxException
	 * @throws ResourceException
	 * @throws IOException
	 * @throws AHEMyProxyDelegationException
	 * @throws NoModuleProviderException
	 */
	
	public static void generateMyProxyCertificate(AHEMessage credential) throws ModuleHandlerException, URISyntaxException, ResourceException, IOException, AHEMyProxyDelegationException, NoModuleProviderException{
		
		logger.info("Init Create MyProxy service call");
		
		
		String[] provider_info = ModuleAccess.getModuleProvider().getModuleProperty(AHEConfigurationProperty.module_security_myproxy.toString());
		URI service = new URI(provider_info[0]);
		
		ClientResource x = new ClientResource(service.toString() + "");
		
		if(provider_info.length > 1){
			//Username & Password is used
			x.setChallengeResponse(ChallengeScheme.HTTP_BASIC, provider_info[1], provider_info[2]);
		}
		
		ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
		
		Gson gson = new Gson();
		
		try{
			
			x.post(gson.toJson(credential)).write(stream);
			AHEMessage response = gson.fromJson(stream.toString(), AHEMessage.class);

		if(AHEMessageUtility.hasException(response))
			throw new AHEMyProxyDelegationException("MyProxy Generation failed");
		
		}catch(ResourceException e){
			throw new AHEMyProxyDelegationException(e);
		}finally{
			x.release();
		}
		
	}

}

