package uk.ac.ucl.chem.ccs.AHECore.Exceptions;

public class AHEAuthorizationException extends Exception {

	public AHEAuthorizationException(String msg){
		super(msg);
	}
	
	public AHEAuthorizationException(){
		super();
	}
	
}