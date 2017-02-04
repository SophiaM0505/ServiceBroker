package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import org.restlet.data.ChallengeScheme;
import org.restlet.data.ClientInfo;
import org.restlet.data.Status;
import org.restlet.engine.header.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.resource.ServerResource;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.util.Series;

import uk.ac.ucl.chem.ccs.AHECore.Definition.UserRoles;
import uk.ac.ucl.chem.ccs.AHECore.Definition.VPHShareHeader;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHESecurityException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;
import uk.ac.ucl.chem.ccs.AHECore.Security.ACD.ACDRestClient;
import uk.ac.ucl.chem.ccs.AHEModule.Def.AHE_SECURITY_TYPE;

/**
 * All AHE restlet resource class extend this class. This class provides basic functions such as authentication, user role check and user setting 
 * @author davidc
 *
 */

public class AHEResource extends ServerResource {

	private AHEUser user;
	
	/**
	 * Authenticates user. User can be authenticated in a number of ways.
	 * 
	 * 1) AHE username password - the simplest way to authenticate user
	 * 2) AHE SSL client - the server checks if the certificate presented by the user is valid
	 * 3) ACD - acd based authentication
	 * 4) VPHSEC - vph-share based authentication. VPHSEC authentication is handled EXTERNALLY and the server is protected under the VPH-Share security framework. In this mode, as long as a user id is in the user registry, the user is assumed to be valid. 
	 * 
	 * @return true if it is a valid user
	 */
	
	protected boolean AuthenticateUser(){
		
		//Try Certificate first

		List<Certificate> r =  getRequest().getClientInfo().getCertificates();
		
    	if(r != null){
    		System.out.println(r.size());
    		//return true;
    		
	    	if(r.size() > 0){
		    	
	    		Object[] ary = r.toArray(new Object[r.size()]);
		    	X509Certificate cert = (X509Certificate) ary[0];
		    	//System.out.println(cert.getSubjectDN().toString());
		    	//return true;
		    	try {
					
		    		//LdapName name = new LdapName(cert.getSubjectDN().toString());
					//System.out.println(SecurityUserAPI.getUserBySubjectDN(cert.getSubjectDN().toString()));
		    		AHEUser user_found = SecurityUserAPI.getUserBySubjectDN(cert.getSubjectDN().toString());
		    		setUser(user_found);
		    		return true;
		    		
				} catch (AHESecurityException e) {
					return false;
				}
		    	
	    	}
    	}
    	
    	//VPH-Share authentication method.. If header exists, check for mapping
    	//THe authentication is handled by the VPH-Share security proxy which is installed externally
    	
    	/*if(AHERuntime.getAHERuntime().isVphsec()){
    		
	    	if(getRequest().getAttributes().get("org.restlet.http.headers") != null){
	    		
	    		Series<Header> headers = (Series<Header>) getRequest().getAttributes().get("org.restlet.http.headers");
	    		
	    		//Get VPH-Share Header Token
	    		String vph_token = headers.getValues(VPHShareHeader.Vph_Security_Token_Decoded.toString());
	    		
	    		if(vph_token != null){ //If VPH-Share authentication is enabled
	    			
	    			//Get uid as per https://neon1.net/mod_auth_pubtkt/install.html Ticket format
	    			
	    			String[] parameters = vph_token.split(";");
	    			
	    			for(String s : parameters){
	    				
	    				String[] keypair = s.split("=");
	    				
	    				if(keypair[0].equalsIgnoreCase("uid")){
	    					
	    					try {
								
	    						AHEUser check_user = SecurityUserAPI.getUser(keypair[1]);
	    						
	    						if(check_user == null)
	    							return false;
	    						
	    						setUser(check_user);
	    						return true;
	    						
							} catch (AHESecurityException e) {
								return false;
							}
	    					
	    				}
	    				
	    			}
	    		
	    		}
	    		
	    	}
	    	
	    	return false;
    	}*/
    	
    	//Try Username Password authentication
		/*if(getRequest().getChallengeResponse() != null){
			
			String ident = getRequest().getChallengeResponse().getIdentifier();

			try {
				
				AHEUser check_user = SecurityUserAPI.getUser(ident);

				if(AHE_SECURITY_TYPE.AHE_PASSWORD == AHE_SECURITY_TYPE.valueOf(check_user.getSecurity_type())){
					
					String pwd = String.valueOf(getRequest().getChallengeResponse().getSecret());
					
					//Check Temporary Password Token First
					if(pwd.equals(check_user.getSession_token())){
						//Checks if session token is valid
						if(check_user.getToken_expiry_timestamp().compareTo(new Date()) > 0){
							setUser(check_user);
							return true;
						}else{
							return false;
						}
						
					}
					
					//Need to hash password and compare it
					byte[] compare = SecurityUserAPI.getHash(pwd, check_user.getEmail().getBytes());
					
					if(Arrays.equals(check_user.getHash_pwd(), compare)){
						getClientInfo().setAuthenticated(true);
						setUser(check_user);
						return true;
					}else{
						ChallengeAuthenticator authentiation = new ChallengeAuthenticator(getContext(), true,ChallengeScheme.HTTP_BASIC,"AHE");
						authentiation.challenge(getResponse(), false);
						return false;
					}
					
				}else if(AHE_SECURITY_TYPE.ACD == AHE_SECURITY_TYPE.valueOf(check_user.getSecurity_type())){
					
					String pwd = String.valueOf(getRequest().getChallengeResponse().getSecret());
					
					if(ACDRestClient.authenticateUser(ident, pwd)){
						getClientInfo().setAuthenticated(true);
						setUser(check_user);
						return true;
					}else{
						
						ChallengeAuthenticator authentiation = new ChallengeAuthenticator(getContext(), true,ChallengeScheme.HTTP_BASIC,"AHE");
						authentiation.challenge(getResponse(), false);
						return false;
					}
					
				}
				
			} catch (Exception e) {
				return false;
			}
			

			
		}*/
		
       //if(getRequest().getChallengeResponse() != null){
			String ident = "Sofia";
			//String ident = getRequest().getChallengeResponse().getIdentifier();

			try {
				
				AHEUser check_user = SecurityUserAPI.getUser(ident);

				if(AHE_SECURITY_TYPE.AHE_PASSWORD == AHE_SECURITY_TYPE.valueOf(check_user.getSecurity_type())){
					
					//String pwd = String.valueOf(getRequest().getChallengeResponse().getSecret());
					String pwd = "sofia0505";
					//Check Temporary Password Token First
					if(pwd.equals(check_user.getSession_token())){
						//Checks if session token is valid
						if(check_user.getToken_expiry_timestamp().compareTo(new Date()) > 0){
							setUser(check_user);
							return true;
						}else{
							return false;
						}
						
					}
					
					//Need to hash password and compare it
					byte[] compare = SecurityUserAPI.getHash(pwd, check_user.getEmail().getBytes());
					
					if(Arrays.equals(check_user.getHash_pwd(), compare)){
						getClientInfo().setAuthenticated(true);
						setUser(check_user);
						return true;
					}else{
						ChallengeAuthenticator authentiation = new ChallengeAuthenticator(getContext(), true,ChallengeScheme.HTTP_BASIC,"AHE");
						authentiation.challenge(getResponse(), false);
						return false;
					}
					
				}else if(AHE_SECURITY_TYPE.ACD == AHE_SECURITY_TYPE.valueOf(check_user.getSecurity_type())){
					
					String pwd = String.valueOf(getRequest().getChallengeResponse().getSecret());
					
					if(ACDRestClient.authenticateUser(ident, pwd)){
						getClientInfo().setAuthenticated(true);
						setUser(check_user);
						return true;
					}else{
						
						ChallengeAuthenticator authentiation = new ChallengeAuthenticator(getContext(), true,ChallengeScheme.HTTP_BASIC,"AHE");
						authentiation.challenge(getResponse(), false);
						return false;
					}
					
				}
				
			} catch (Exception e) {
				return false;
			}
			

			
		//}
		
		ChallengeAuthenticator authentiation = new ChallengeAuthenticator(getContext(), true,ChallengeScheme.HTTP_BASIC,"");
		authentiation.challenge(getResponse(), false);
		return false;
    	//return true;
	}

	protected boolean AuthenticateUser(String name, String pwd){
		
		//String ident = "Sofia";
		//String ident = name;

		try {
			
			AHEUser check_user = SecurityUserAPI.getUser(name);

			if(AHE_SECURITY_TYPE.AHE_PASSWORD == AHE_SECURITY_TYPE.valueOf(check_user.getSecurity_type())){
				
				//String pwd = psw;
				//String pwd = "sofia0505";
				//Check Temporary Password Token First
				if(pwd.equals(check_user.getSession_token())){
					//Checks if session token is valid
					if(check_user.getToken_expiry_timestamp().compareTo(new Date()) > 0){
						setUser(check_user);
						return true;
					}else{
						return false;
					}
					
				}
				
				//Need to hash password and compare it
				byte[] compare = SecurityUserAPI.getHash(pwd, check_user.getEmail().getBytes());
				
				if(Arrays.equals(check_user.getHash_pwd(), compare)){
					getClientInfo().setAuthenticated(true);
					setUser(check_user);
					return true;
				}else{
					ChallengeAuthenticator authentiation = new ChallengeAuthenticator(getContext(), true,ChallengeScheme.HTTP_BASIC,"AHE");
					authentiation.challenge(getResponse(), false);
					return false;
				}
				
			}		
		} catch (Exception e) {
			return false;
		}
		

		
	//}
	
	ChallengeAuthenticator authentiation = new ChallengeAuthenticator(getContext(), true,ChallengeScheme.HTTP_BASIC,"");
	authentiation.challenge(getResponse(), false);
	return false;
		
	}
	/**
	 * Trim white spaces and sort
	 * @param dirty
	 */
	
	private String[] CleanUpArray(String[] dirty){
		
		for(int i=0; i < dirty.length; i++){
			dirty[i] = dirty[i].trim();
			dirty[i] = dirty[i].toLowerCase();
		}
		
		Arrays.sort(dirty);
		
		return dirty;
	}
	
	/**
	 * Check if user is admin
	 * @param user
	 * @return true if user is admin
	 */
	
    protected boolean checkAdminAuthorization(AHEUser user){

    	if(user != null){
    		
    		if(user.getRole().equalsIgnoreCase(UserRoles.admin.toString()))
    			return true;
    		
    	}

    	return false;
    }
	
    /**
     * Return error message and set HTTP code
     * @param status HTTP CODE
     * @param cmd originating command
     * @param errormsg error message
     * @param src source of error
     * @return ahe xml message
     */
    
    protected String ThrowErrorWithHTTPCode(Status status, String cmd, String errormsg, String src){
    	setStatus(status);
    	return ResourceUtil.ThrowErrorMessage(cmd,errormsg,src);
    }

    /**
     * Authenticated user
     * @return AHEUser entity
     */

	public AHEUser getUser() {
		return user;
	}

	/**
	 * Set authenticated user
	 * @param user
	 */

	public void setUser(AHEUser user) {
		this.user = user;
	}
}