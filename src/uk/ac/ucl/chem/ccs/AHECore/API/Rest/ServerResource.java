package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import java.util.ArrayList;

import org.restlet.data.Status;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;
import uk.ac.ucl.chem.ccs.AHECore.Configuration.ConfigFiles;
import uk.ac.ucl.chem.ccs.AHECore.Definition.AHEConfigurationProperty;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;

public class ServerResource extends AHEResource{

	
    @Override
    public void doInit() {
    }
	
    /**
     * Check all ahe modules if reachable. AHE will try and attempt to connect to each module.
     * The list of AHE modules is described in the ahe configuration file
     * @return
     */
    
    @Get
    public String getResource(){

    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("");
    	}
    	
    	//Return the status of all servers
    	
    	ConfigFiles ahe_config = AHERuntime.getAHERuntime().getConfig_map().get(AHERuntime.ahe_config_filename);
    	ArrayList<String[]> results = new ArrayList<String[]>();
    	
    	//Search for Globus Modules
    	String[] globus_providers = ahe_config.getKeys(AHEConfigurationProperty.module_jobservice_globus.toString());
		
    	for(int i=0; i < globus_providers.length; i++){
    		
    		String uri = ahe_config.getPropertyString(globus_providers[i]);
    		ClientResource x = new ClientResource(uri);
    		String[] ping = new String[2];
    		ping[0] = globus_providers[i] + ":" + uri ;
    				
    		try{
	    		x.get();
	    		ping[1] = "true";
    		}catch(ResourceException e){
    			ping[1] = "false";
    		}finally{
	    		x.release();
	    		results.add(ping);
    		}
    		
    	}
    	
    	//Search for WebDav
    	String[] webdav_provider = ahe_config.getKeys(AHEConfigurationProperty.module_transferservice_webdav.toString());

    	for(int i=0; i < webdav_provider.length; i++){
    		
    		String uri = ahe_config.getPropertyString(webdav_provider[i]);
    		ClientResource x = new ClientResource(uri);
    		String[] ping = new String[2];
    		ping[0] = webdav_provider[i] + ":" + uri ;
    				
    		try{
	    		x.get();
	    		ping[1] = "true";
    		}catch(ResourceException e){
    			ping[1] = "false";
    		}finally{
	    		x.release();
	    		results.add(ping);
    		}
    		
    	}
    	
    	//Search for GSIFTP
    	String[] gsiftp_provider = ahe_config.getKeys(AHEConfigurationProperty.module_transferservice_gsiftp.toString());

    	for(int i=0; i < gsiftp_provider.length; i++){
    		
    		String uri = ahe_config.getPropertyString(gsiftp_provider[i]);
    		ClientResource x = new ClientResource(uri);
    		String[] ping = new String[2];
    		ping[0] = gsiftp_provider[i] + ":" + uri ;
    				
    		try{
	    		x.get();
	    		ping[1] = "true";
    		}catch(ResourceException e){
    			ping[1] = "false";
    		}finally{
	    		x.release();
	    		results.add(ping);
    		}
    		
    	}
    	
    	//Search for MyProxy
    	String[] myproxy_provider = ahe_config.getKeys(AHEConfigurationProperty.module_security_myproxy.toString());

    	for(int i=0; i < myproxy_provider.length; i++){
    		
    		String uri = ahe_config.getPropertyString(myproxy_provider[i]);
    		ClientResource x = new ClientResource(uri);
    		String[] ping = new String[2];
    		ping[0] = myproxy_provider[i] + ":" + uri ;
    				
    		try{
	    		x.get();
	    		ping[1] = "true";
    		}catch(ResourceException e){
    			ping[1] = "false";
    		}finally{
	    		x.release();
	    		results.add(ping);
    		}
    		
    	}
    	
    	//Search for Unicore - BES & UCC
    	String[] ucc_provider = ahe_config.getKeys(AHEConfigurationProperty.module_jobservice_ucc.toString());
    	String[] uccbes_provider = ahe_config.getKeys(AHEConfigurationProperty.module_jobservice_uccbes.toString());

    	for(int i=0; i < ucc_provider.length; i++){
    		
    		String uri = ahe_config.getPropertyString(ucc_provider[i]);
    		ClientResource x = new ClientResource(uri);
    		String[] ping = new String[2];
    		ping[0] = ucc_provider[i] + ":" + uri ;
    				
    		try{
	    		x.get();
	    		ping[1] = "true";
    		}catch(ResourceException e){
    			ping[1] = "false";
    		}finally{
	    		x.release();
	    		results.add(ping);
    		}
    		
    	}
    	
    	for(int i=0; i < uccbes_provider.length; i++){
    		
    		String uri = ahe_config.getPropertyString(uccbes_provider[i]);
    		ClientResource x = new ClientResource(uri);
    		String[] ping = new String[2];
    		ping[0] = uccbes_provider[i] + ":" + uri ;
    				
    		try{
	    		x.get();
	    		ping[1] = "true";
    		}catch(ResourceException e){
    			ping[1] = "false";
    		}finally{
	    		x.release();
	    		results.add(ping);
    		}
    		
    	}
    	
    	//Search for QcG
    	String[] qcg_provider = ahe_config.getKeys(AHEConfigurationProperty.module_jobservice_qcg.toString());

    	for(int i=0; i < qcg_provider.length; i++){
    		
    		String uri = ahe_config.getPropertyString(qcg_provider[i]);
    		ClientResource x = new ClientResource(uri);
    		String[] ping = new String[2];
    		ping[0] = qcg_provider[i] + ":" + uri ;
    				
    		try{
	    		x.get();
	    		ping[1] = "true";
    		}catch(ResourceException e){
    			ping[1] = "false";
    		}finally{
	    		x.release();
	    		results.add(ping);
    		}
    		
    	}
    	
    	String[][] r = results.toArray(new String[0][0]);
    	
    	return XMLServerMessageAPI.createConfigurationList("/GET /", r);
    }
    
    /**
     * Shut server down
     * @return
     */
    
	@Delete
	public String deleteResource() {
		
    	
    	//Check is User and User Certificate is correct
    	if(!AuthenticateUser()){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthenticationFailedMessage("");
    	}
    	
    	if(!checkAdminAuthorization(getUser())){
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return XMLServerMessageAPI.createUserAuthorizationFailedMessage("", getUser().getUsername(), "No Authorization to access this resource");
    	
    	}
		
		//Shutserver down
    	try {
			AHERuntime.getAHERuntime().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return "";
    	
	}
	
}
