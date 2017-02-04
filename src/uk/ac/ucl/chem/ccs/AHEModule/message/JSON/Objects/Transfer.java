package uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects;

public class Transfer {

	private String id;
	private String[][] path;
	private Argument arg;
	
	public Transfer() {
		// TODO Auto-generated constructor stub
	}

	public Argument getArg() {
		return arg;
	}

	public void setArg(Argument arg) {
		this.arg = arg;
	}

	public String[][] getPath() {
		return path;
	}

	public void setPath(String[][] path) {
		this.path = path;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
