package uk.ac.ucl.chem.ccs.AHEModule.exception;

public class ProxyCredentialException extends Exception {

	
	public ProxyCredentialException(String msg){
		super(msg);
	}
	
	public ProxyCredentialException(){
		super();
	}
	
	public ProxyCredentialException(Throwable e){
		super(e);
	}
	
}