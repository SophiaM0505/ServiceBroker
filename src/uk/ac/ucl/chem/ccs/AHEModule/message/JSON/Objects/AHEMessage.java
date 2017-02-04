package uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects;

public class AHEMessage {

	private String command;
	private String timestamp;
	private String status; //Ack, Failed, Pending
	private String[] information;
	private String[] warning;
	private String[] exception;
	
	private Credential credentials;
	private Job job;
	private Transfer transfer;
	
	public AHEMessage() {
		// TODO Auto-generated constructor stub
	}
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String[] getInformation() {
		return information;
	}

	public void setInformation(String[] information) {
		this.information = information;
	}

	public String[] getWarning() {
		return warning;
	}

	public void setWarning(String[] warning) {
		this.warning = warning;
	}

	public String[] getException() {
		return exception;
	}

	public void setException(String[] exception) {
		this.exception = exception;
	}

	public Credential getCredentials() {
		return credentials;
	}

	public void setCredentials(Credential cred) {
		this.credentials = cred;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Transfer getTransfer() {
		return transfer;
	}

	public void setTransfer(Transfer transfer) {
		this.transfer = transfer;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


}

