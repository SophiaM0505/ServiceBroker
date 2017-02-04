package uk.ac.ucl.chem.ccs.AHECore.Security.ACD;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.xml.sax.SAXException;

import uk.ac.ucl.chem.ccs.AHECore.Definition.AHEConfigurationProperty;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.ACDRestClientException;
import uk.ac.ucl.chem.ccs.AHECore.Runtime.AHERuntime;
import uk.ac.ucl.chem.ccs.AHECore.Security.ACD.objects.ACDProxy;


/**
 * ACD Rest Client library
 * 
 * 1. Authenticate User
 * 2. Get Proxy
 * 
 * @author davidc
 *
 */

public class ACDRestClient {

	String uri;
	
	/**
	 * URI call to authenticate a user using ACD
	 * @param username
	 * @param password
	 * @return
	 * @throws ACDRestClientException
	 * @throws ResourceException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	
	public static boolean authenticateUser(String username, String password) throws ACDRestClientException, ResourceException, IOException, SAXException, ParserConfigurationException{
		
		String acd_uri = AHERuntime.getAHERuntime().getAHEConfiguration().getPropertyString(AHEConfigurationProperty.ACD_uri.toString());
		
		if(acd_uri.length() == 0){
			throw new ACDRestClientException("No valid ACD URI specified in AHE configuration file (property name: " + AHEConfigurationProperty.ACD_uri.toString() + ")");
		}
		
		if(!acd_uri.endsWith("/"))
			acd_uri += "/";
		

		ClientResource x = new ClientResource(acd_uri + "login/"+username);
		x.setChallengeResponse(ChallengeScheme.HTTP_BASIC, username, password);
		//x.setChallengeResponse(ChallengeScheme.HTTP_DIGEST, username, password);
		
//		Form form = new Form();
//		form.add("password", password);
		
		java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
		x.get().write(stream);
		//Must release underlying connection. May lead to HTTP starvation if not.
		x.release();
		
		return ACD_XML_Parser.authenticateUser(stream.toString());
	}
	
	/**
	 * URI call to generate a proxy certificate using ACD
	 * @param ahe_acd_id
	 * @param acdcred
	 * @param alias
	 * @return
	 * @throws ACDRestClientException
	 * @throws ResourceException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	
	public static ACDProxy generateProxy(int ahe_acd_id, int acdcred, String alias) throws ACDRestClientException, ResourceException, IOException, SAXException, ParserConfigurationException{
		
		String acd_uri = AHERuntime.getAHERuntime().getAHEConfiguration().getPropertyString(AHEConfigurationProperty.ACD_uri.toString());
		
		if(acd_uri.length() == 0){
			throw new ACDRestClientException("No valid ACD URI specified in AHE configuration file (property name: " + AHEConfigurationProperty.ACD_uri.toString() + ")");
		}
		
		if(!acd_uri.endsWith("/"))
			acd_uri += "/";
		
		ClientResource x = new ClientResource(acd_uri + "user/"+ahe_acd_id+"/acdcred/"+acdcred+"/cmd/generateproxies");
		
		Form form = new Form();
		form.add("alias", alias);
		
		java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
		x.post(form).write(stream);
		
		//Must release underlying connection. May lead to HTTP starvation if not.
		x.release();
		
		return ACD_XML_Parser.generateUserProxy(stream.toString());
		
	}
	
	
	public ACDRestClient(){
	}
	
}
