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
import uk.ac.ucl.chem.ccs.AHECore.Definition.Transfer_Scheme;
import uk.ac.ucl.chem.ccs.AHECore.Engine.ResourceRegisterAPI;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEMyProxyDelegationException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.ModuleHandlerException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.NoModuleProviderException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.PlatformCredentialException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.FileStaging;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHEModule.Def.RESOURCE_AUTHEN_TYPE;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.AHEMessage;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.Transfer;

public class TransferHandler {

	private static Logger logger = LoggerFactory.getLogger(TransferHandler.class);
	
	public TransferHandler() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Each batch of file transfer must use the same credential and each set of transfer in the batch must have matching schema://hostname
	 * @param user
	 * @param files
	 * @return
	 * @throws ModuleHandlerException 
	 * @throws PlatformCredentialException 
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws ResourceException 
	 * @throws AHEMyProxyDelegationException 
	 * @throws NoModuleProviderException 
	 */
	
	public static AHEMessage transfer(FileStaging[] files) throws URISyntaxException, PlatformCredentialException, ModuleHandlerException, ResourceException, IOException, AHEMyProxyDelegationException, NoModuleProviderException{
		
		logger.info("Init. transfer for " + files.length + " obj, service call");
		
		AHEMessage msg = AHEMessageUtility.generateTransferMessage(files);
		
		URI uri1 = new URI(files[0].getSource());
		URI uri2 = new URI(files[0].getTarget());

		Resource target_resource = null;
		
		
		if(new URI(files[0].getSource()).getScheme().equalsIgnoreCase("file")){
			target_resource = ResourceRegisterAPI.getResourceByBestMatch(uri1);
		}else if(new URI(files[0].getTarget()).getScheme().equalsIgnoreCase("file")){
			target_resource = ResourceRegisterAPI.getResourceByBestMatch(uri2);
		}else{
			target_resource = ResourceRegisterAPI.getResourceByBestMatch(uri2);
		}
		
		Gson gson = new Gson();
		
		String[] provider_info;
		
		if(uri1.getScheme().equalsIgnoreCase(Transfer_Scheme.GSIFTP.toString()) || uri2.getScheme().equalsIgnoreCase(Transfer_Scheme.GSIFTP.toString())){
				provider_info = ModuleAccess.getModuleProvider().getModuleProperty(AHEConfigurationProperty.module_transferservice_gsiftp.toString());
		
				//Need to Check MyProxy validity
				if(target_resource.getAuthen_type().equalsIgnoreCase(RESOURCE_AUTHEN_TYPE.PROXY_CERTIFICATE.toString())){
					if(!MyProxyHandler.checkMyProxyValidity(msg)){
						MyProxyHandler.generateMyProxyCertificate(msg);
					}
				}
				
		}else if(uri1.getScheme().equalsIgnoreCase(Transfer_Scheme.WEBDAV.toString()) || uri1.getScheme().equalsIgnoreCase(Transfer_Scheme.HTTP.toString()) || uri2.getScheme().equalsIgnoreCase(Transfer_Scheme.WEBDAV.toString()) || uri2.getScheme().equalsIgnoreCase(Transfer_Scheme.HTTP.toString())){
				provider_info = ModuleAccess.getModuleProvider().getModuleProperty(AHEConfigurationProperty.module_transferservice_webdav.toString());
		}else{
			throw new ModuleHandlerException("Transfer schema not supported : " + uri1.getScheme());
		}
		
		URI service = new URI(provider_info[0]);
		
		ClientResource x = new ClientResource(service.toString() + "");
		
		
		if(provider_info.length > 1){
			//Username & Password is used
			x.setChallengeResponse(ChallengeScheme.HTTP_BASIC, provider_info[1], provider_info[2]);
		}
		
		ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();

		x.post(gson.toJson(msg)).write(stream);
		
		AHEMessage response = gson.fromJson(stream.toString(), AHEMessage.class);
		x.release();
		
		for(int i=0; i < files.length; i++){
			
			if(!AHEMessageUtility.hasException(msg)){
				files[i].setIdentifier(provider_info[0]+"/"+response.getInformation()[0]);
				HibernateUtil.SaveOrUpdate(files[i]);
			}
			
		}
		
		return response;
	}
	
	/**
	 * Get the status of this group of file transfer. This group must be submitted togeather and contain the same AHE transfer ID.
	 * @param files
	 * @return AHEMessage object
	 * @throws URISyntaxException
	 * @throws ModuleHandlerException
	 * @throws ResourceException
	 * @throws IOException
	 * @throws AHEMyProxyDelegationException
	 * @throws PlatformCredentialException
	 */
	
	public static AHEMessage transfer_status(FileStaging[] files) throws URISyntaxException, ModuleHandlerException, ResourceException, IOException, AHEMyProxyDelegationException, PlatformCredentialException{
		
		AHEMessage msg = AHEMessageUtility.generateTransferStatusMessage(files);
	
		URI id = new URI(files[0].getIdentifier());
		
		String[] path = id.getPath().split("/");
		
		String new_path = "";
		
		for(int i=0; i <path.length-1; i++){
			new_path += path[i] + "/";
		}
		
		String service_uri = id.getScheme()+"://"+id.getAuthority() + new_path;

		URI service = new URI(service_uri);

		ClientResource x = new ClientResource(service.toString() + "transfer");
		
		String[] cred = ModuleAccess.getModuleProvider().searchModuleCredentialByURI(service.toString());

		if(cred.length > 0){
			x.setChallengeResponse(ChallengeScheme.HTTP_BASIC, cred[0], cred[1]);
		}
		
		try{
		
			ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
			
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
	
}
