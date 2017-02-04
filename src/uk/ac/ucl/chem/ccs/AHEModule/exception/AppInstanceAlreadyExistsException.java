package uk.ac.ucl.chem.ccs.AHEModule.exception;

public class AppInstanceAlreadyExistsException extends Exception {

	public AppInstanceAlreadyExistsException(String msg){
		super(msg);
	}
	
	public AppInstanceAlreadyExistsException(){
		super();
	}
	
}