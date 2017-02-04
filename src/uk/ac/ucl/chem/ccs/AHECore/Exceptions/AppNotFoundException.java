package uk.ac.ucl.chem.ccs.AHECore.Exceptions;

public class AppNotFoundException extends Exception {

	public AppNotFoundException(String msg){
		super(msg);
	}
	
	public AppNotFoundException(){
		super();
	}
	
}