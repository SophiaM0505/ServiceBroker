package uk.ac.ucl.chem.ccs.AHEModule.exception;

public class AppNotFoundException extends Exception {

	public AppNotFoundException(String msg){
		super(msg);
	}
	
	public AppNotFoundException(){
		super();
	}
	
}
