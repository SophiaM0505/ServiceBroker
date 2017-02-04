package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.Authenticator;

/**
 * Use fine grain authentication and authroization for now. i.e. the code is left at ServerResource level. This class is left
 * here if coarse grain security is desired later on.
 * @author davidc
 *
 */

public class AHEAuthenticator extends Authenticator{

	public AHEAuthenticator(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean authenticate(Request arg0, Response arg1) {
//
//		System.out.println(arg0.getChallengeResponse().getIdentifier());
//		System.out.println(arg0.getChallengeResponse().getSecret().toString());
		
		//System.out.println("*******authen with paras");
		return true;
	}
/*	
	protected boolean authenticate(Context context) {
		//
//				System.out.println(arg0.getChallengeResponse().getIdentifier());
//				System.out.println(arg0.getChallengeResponse().getSecret().toString());
		//System.out.println("*******authen WITHOUT paras");
				
				return true;
			}*/

}