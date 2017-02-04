package uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects;

public class Job {

	private String job_name; //The Job name given by the user
	private String executable;
	private String app_name; //Optional
	private String job_id; //Job identifier is the id given by the submission manager after submission
	private String resourcepath; //NOTE should change this to resource_endpoint
	private int resource_port;
	private String[] arg;

	public String getExecutable() {
		return executable;
	}

	public void setExecutable(String executable) {
		this.executable = executable;
	}
	
	public Job() {
		// TODO Auto-generated constructor stub
	}

	public String getResourcepath() {
		return resourcepath;
	}

	public void setResourcepath(String resource) {
		this.resourcepath = resource;
	}

	public String[] getArg() {
		return arg;
	}

	public void setArg(String[] arg) {
		this.arg = arg;
	}

	public String getJob_id() {
		return job_id;
	}

	public void setJob_id(String job_id) {
		this.job_id = job_id;
	}

	public int getResource_port() {
		return resource_port;
	}

	public void setResource_port(int resource_port) {
		this.resource_port = resource_port;
	}

	public String getJob_name() {
		return job_name;
	}

	public void setJob_name(String job_name) {
		this.job_name = job_name;
	}

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

}

