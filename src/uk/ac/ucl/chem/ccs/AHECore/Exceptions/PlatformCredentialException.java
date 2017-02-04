package uk.ac.ucl.chem.ccs.AHECore.Exceptions;

public class PlatformCredentialException extends Exception{

	public PlatformCredentialException(){
		super();
	}
	
	public PlatformCredentialException(String s){
		super(s);
	}

	public PlatformCredentialException(Throwable s){
		super(s);
	}
	
}
