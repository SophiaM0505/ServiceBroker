package uk.ac.ucl.chem.ccs.AHECore.Exceptions;

public class AHEDataTransferException extends Exception {

	public AHEDataTransferException(){
		super();
	}
	
	public AHEDataTransferException(String msg){
		super(msg);
	}
	
	public AHEDataTransferException(Exception e){
		super(e);
	}
	
}
