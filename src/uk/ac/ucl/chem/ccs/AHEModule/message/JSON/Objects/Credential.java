package uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects;

public class Credential {
	
	private String type;
	private String username;
	private String password;
	
    private String credential_alias; //alias
    
    private String registry_path;
    
	String credential_location; //User Credential Location or where keystore is located
	String proxy_location;
	String user_key;
	String certificate_directory; //Trust certificate
	
	String truststore_path;
	String truststore_password;
	
	public String getCredential_location() {
		return credential_location;
	}

	public void setCredential_location(String credential_location) {
		this.credential_location = credential_location;
	}

	public String getProxy_location() {
		return proxy_location;
	}

	public void setProxy_location(String proxy_location) {
		this.proxy_location = proxy_location;
	}

	public String getUser_key() {
		return user_key;
	}

	public void setUser_key(String user_key) {
		this.user_key = user_key;
	}

	public String getCertificate_directory() {
		return certificate_directory;
	}

	public void setCertificate_directory(String certificate_directory) {
		this.certificate_directory = certificate_directory;
	}

	public String getTruststore_path() {
		return truststore_path;
	}

	public void setTruststore_path(String truststore_path) {
		this.truststore_path = truststore_path;
	}

	public String getTruststore_password() {
		return truststore_password;
	}

	public void setTruststore_password(String truststore_password) {
		this.truststore_password = truststore_password;
	}


	
	public Credential() {
		// TODO Auto-generated constructor stub
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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

	public String getCredential_alias() {
		return credential_alias;
	}

	public void setCredential_alias(String credential_alias) {
		this.credential_alias = credential_alias;
	}

	public String getRegistery_path() {
		return registry_path;
	}

	public void setRegistry_path(String registry_path) {
		this.registry_path = registry_path;
	}


}
