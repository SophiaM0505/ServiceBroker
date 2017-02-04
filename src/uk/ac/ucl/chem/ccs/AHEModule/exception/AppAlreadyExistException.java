package uk.ac.ucl.chem.ccs.AHEModule.exception;

public class AppAlreadyExistException extends Exception {

	public AppAlreadyExistException(String msg){
		super(msg);
	}
	
	public AppAlreadyExistException(){
		super();
	}
	
}
