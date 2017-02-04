package uk.ac.ucl.chem.ccs.AHECore.Exceptions;

public class AppAlreadyExistException extends Exception {

	public AppAlreadyExistException(String msg){
		super(msg);
	}
	
	public AppAlreadyExistException(){
		super();
	}
	
}

