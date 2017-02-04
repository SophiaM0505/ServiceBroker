package uk.ac.ucl.chem.ccs.AHECore.Exceptions;

public class ACDRestClientException extends Exception {

	public ACDRestClientException(String msg){
		super(msg);
	}
	
	public ACDRestClientException(){
		super();
	}
	
}