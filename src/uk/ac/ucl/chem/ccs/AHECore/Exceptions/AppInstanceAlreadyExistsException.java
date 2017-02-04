package uk.ac.ucl.chem.ccs.AHECore.Exceptions;

public class AppInstanceAlreadyExistsException extends Exception {

	public AppInstanceAlreadyExistsException(String msg){
		super(msg);
	}
	
	public AppInstanceAlreadyExistsException(){
		super();
	}
	
}