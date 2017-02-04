package uk.ac.ucl.chem.ccs.AHECore.Security.ACD.objects;

public class ACDProxy {

	private String username;
	private String password;
	private String subject_dn;
	private String uri;
	private String port;
	private String lifetime;
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSubject_dn() {
		return subject_dn;
	}

	public void setSubject_dn(String subject_dn) {
		this.subject_dn = subject_dn;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getLifetime() {
		return lifetime;
	}

	public void setLifetime(String lifetime) {
		this.lifetime = lifetime;
	}
	
}

