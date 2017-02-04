package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * The restlet application class maps URI to AHE restlet resources.
 * Consult restlet API to see or configure how URI can be mapped to the resources
 * 
 * 
 * 
 * @author davidc
 *
 */

public class AHERestletApplication extends Application{

    @Override
    public Restlet createInboundRoot() {
        
    	//AHEAuthenticator auth = new AHEAuthenticator(getContext());
    	AHEAuthenticator auth = new AHEAuthenticator(getContext());
    	// Create a router
        Router router = new Router(getContext());
        auth.setNext(router);

        String server = "/";

        //Negotiation
        String init_req = "/init/{user}/{group}/{app}/{start_time}/{end_time}";
        
        
        //App Instances
        String appinstance_resource = "/appinst";
        String appinstance = "/appinst/{appinst}";
        //String appinstance_neg="/appinst/{user}/{group}/{app}/{start_time}/{end_time}";

        //App-Instance Data Staging
        String datastage_resource = "/appinst/{appinst}/transfer";
        String datastage = "/appinst/{appinst}/transfer/{transferid}";

        //App-Instance properties
        String appinstprop_resource = "/appinst/{appinst}/property";
        String appinstprop = "/appinst/{appinst}/property/{propertyid}";

        //App-Instance runtime
        String appinstruntime_resource = "/appinst/{appinst}/runtime";
        String appinstclone_resource = "/appinst/{appinst}/clone";
        
        //App Registery Object
        
        String appreg_resource = "/app";
        String appreg = "/app/{appreg}";
        
        //Platform Resource
        String resource_resource = "/resource";
        String resource = "/resource/{resource}";

        
        //Platform - Credential Mapping
        String resource_credential_resource = resource + "/cred";
        String resource_credential = resource + "/cred/{cred}";
        
        //Profile
        String profile_resource = "/profile";
        String profile = "/profile/{profile}";
        
        //Porifle - Credential Mapping
        String profile_credential_resource = profile + "/cred"; 
        String profile_credential = profile + "/cred/{cred}";
        
        //Credential
        String cred_resource = "/cred";
        String cred = "/cred/{cred}";
        
        String cred_proxy_resource = "/cred/{cred}/proxy";
       

        String upload_resource = "/upload";
        String upload = "/upload/{storage}";
        //Data Staging
        
        String transfer_resource = "/transfer";
        String transfer = "/transfer/{transferid}";
//        String transfercmd = "/transfer/{transferid}/cmd/{cmd}"; //TO BE Deprecated
        
        String session_resource = "/session";
        
        String config_resource = "/config";
        
        // Attach the resources to the router
        router.attach(server,ServerResource.class); // Server commands 

//      
        //router.attach(init_req, negotiation.Negotiation.Initiating.class);
        
        //router.attach(appinstance_neg, AppInstanceResource.class);        
        router.attach(appinstance_resource, AppInstanceResource.class);
        router.attach(appinstance, AppInstanceResource.class);

        router.attach(appinstance_resource+"/", AppInstanceResource.class);
        router.attach(appinstance+"/", AppInstanceResource.class);

        
        router.attach(appreg_resource, AppRegisteryResource.class);
        router.attach(appreg, AppRegisteryResource.class);

        router.attach(appreg_resource+"/", AppRegisteryResource.class);
        router.attach(appreg+"/", AppRegisteryResource.class);

        
        router.attach(resource_resource, PlatformResource.class);
        router.attach(resource, PlatformResource.class);

        router.attach(resource_credential,PlatformCredentialResource.class);
        router.attach(resource_credential_resource,PlatformCredentialResource.class);
        
        router.attach(resource_resource+"/", PlatformResource.class);
        router.attach(resource+"/", PlatformResource.class);

        router.attach(resource_credential+"/",PlatformCredentialResource.class);
        
        router.attach(profile_resource, ProfileResource.class);
        router.attach(profile, ProfileResource.class);

        router.attach(profile_credential, ProfileCredentialResource.class);
        router.attach(profile_credential_resource, ProfileCredentialResource.class);
        
        router.attach(profile_resource+"/", ProfileResource.class);
        router.attach(profile+"/", ProfileResource.class);

        router.attach(profile_credential+"/", ProfileCredentialResource.class);
        
        router.attach(cred_resource, CredentialResource.class);
        router.attach(cred, CredentialResource.class);
        router.attach(cred_proxy_resource, CredentialProxyResource.class);
        
        router.attach(cred_resource+"/" , CredentialResource.class);
        router.attach(cred+"/", CredentialResource.class);
        router.attach(cred_proxy_resource + "/", CredentialProxyResource.class);

        router.attach(datastage_resource,AppInstanceDataStageResource.class);
        router.attach(datastage,AppInstanceDataStageResource.class);

        router.attach(datastage+"/",AppInstanceDataStageResource.class);

        
        router.attach(appinstprop_resource,AppInstancePropertyResource.class);
        router.attach(appinstprop,AppInstancePropertyResource.class);
     
        router.attach(appinstprop+"/",AppInstancePropertyResource.class);
     
        
        router.attach(appinstruntime_resource,AppInstanceRuntimeResource.class);
        router.attach(appinstruntime_resource+"/",AppInstanceRuntimeResource.class);
        
        router.attach(appinstclone_resource,AppInstanceCloneResource.class);
        router.attach(appinstclone_resource+"/",AppInstanceCloneResource.class);
        
        router.attach(upload_resource,UploadResource.class);
        router.attach(upload_resource+"/",UploadResource.class);
        
        router.attach(transfer_resource, TransferResource.class);
        router.attach(transfer, TransferResource.class);

        router.attach(transfer_resource+"/", TransferResource.class);
        router.attach(transfer+"/", TransferResource.class);

        
        router.attach(session_resource,SessionResource.class);
        router.attach(session_resource+"/",SessionResource.class); 
        
        router.attach(config_resource,ConfigResource.class); 
        router.attach(config_resource+"/",ConfigResource.class); 
        
        // Return the root router
        return auth;
    }
	
}
