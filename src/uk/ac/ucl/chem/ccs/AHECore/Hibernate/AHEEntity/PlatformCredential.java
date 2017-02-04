package uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEEncryptionException;
import uk.ac.ucl.chem.ccs.AHECore.Security.StringEncrypter;


@Entity
@Table(name="PlatformCredential")
public class PlatformCredential {
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private	long id;
	
//    @ManyToMany
//    @JoinColumn(name="User_ID")
//	private AHEUser owner;
	
	private String credential_id; //Identifier for this platform crendential
	private String platform_interface;
	private String authen_type; //Authentication type, tells AHE what authentication method is used
	
//    @ManyToOne
//    @JoinColumn(name="cred_id")
//	private	Resource resource;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "credential_resource", joinColumns = { @JoinColumn(name = "cred_id") }, inverseJoinColumns = { @JoinColumn(name = "resource_id") })
	private Set<Resource> resource = new HashSet<Resource>(0);
    
    private String registry_path; 
    
    private String credential_alias;
    
	private String credential_location; //User Credential Location or where keystore is located
	private String proxy_location;
	private String user_key;
	private String certificate_directory; //Trust certificate
	
	private String truststore_path;
	private String truststore_password;
	
	//TODO: dev only: insecure, need to change this
	
	private String username;
	private String password; // used for username password combo or keystore password
	
	private Date timestamp;
	private boolean active;
	

	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}

//	public void setOwner(AHEUser owner) {
//		this.owner = owner;
//	}
//
//	public AHEUser getOwner() {
//		return owner;
//	}

//	public void setPlatform_interface(String platform_interface) {
//		this.platform_interface = platform_interface;
//	}
//
//	public String getPlatform_interface() {
//		return platform_interface;
//	}

	public void setProxy_location(String proxy_location) {
		this.proxy_location = proxy_location;
	}

	public String getProxy_location() {
		return proxy_location;
	}

	public void setCertificate_directory(String certificate_directory) {
		this.certificate_directory = certificate_directory;
	}

	public String getCertificate_directory() {
		return certificate_directory;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		
		String encrypt = password;
		
		if(encrypt != null){
			if(encrypt.length() > 0){
				try {
					encrypt = StringEncrypter.encryptStr(password);
				} catch (AHEEncryptionException e) {
				}
			}
		}
		
		this.password = encrypt;
	}

	public String getPassword() {
		
		String decrypt = password;
		
		if(decrypt != null){
			if(decrypt.length() >0){
				try {
					decrypt = StringEncrypter.decryptStr(password);
				} catch (AHEEncryptionException e) {
					e.printStackTrace();
				}
			}
		}
		
		return decrypt;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setCredential_location(String credential_location) {
		this.credential_location = credential_location;
	}

	public String getCredential_location() {
		return credential_location;
	}

	public void setCredential_id(String platform_id) {
		this.credential_id = platform_id;
	}

	public String getCredential_id() {
		return credential_id;
	}

	public void setPlatform_interface(String platform_interface) {
		this.platform_interface = platform_interface;
	}

	public String getPlatform_interface() {
		return platform_interface;
	}

//	public void setResource(Resource resource) {
//		this.resource = resource;
//	}
//
//	public Resource getResource() {
//		return resource;
//	}

	public void setRegistry_path(String registry_path) {
		this.registry_path = registry_path;
	}

	public String getRegistry_path() {
		return registry_path;
	}

	public String getUser_key() {
		return user_key;
	}

	public void setUser_key(String user_key) {
		this.user_key = user_key;
	}

	public String getAuthen_type() {
		return authen_type;
	}

	public void setAuthen_type(String authen_type) {
		this.authen_type = authen_type;
	}

	public String getCredential_alias() {
		return credential_alias;
	}

	public void setCredential_alias(String credential_alias) {
		this.credential_alias = credential_alias;
	}

	public String getTruststore_path() {
		return truststore_path;
	}

	public void setTruststore_path(String truststore_path) {
		this.truststore_path = truststore_path;
	}

	public String getTruststore_password() {
		
		String decrypt = this.truststore_password;
		
		if(decrypt != null){
			if(decrypt.length() >0){
				try {
					decrypt = StringEncrypter.decryptStr(truststore_password);
				} catch (AHEEncryptionException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		return decrypt;
	}

	public void setTruststore_password(String truststore_password) {
		
		String encrypt = truststore_password;
		
		if(encrypt != null){
			if(encrypt.length() > 0){
				try {
					encrypt = StringEncrypter.encryptStr(truststore_password);
				} catch (AHEEncryptionException e) {
				}
			}
		}
		
		this.truststore_password = encrypt;
	}

	public Set<Resource> getResource() {
		return resource;
	}

	public void setResource(Set<Resource> resource) {
		this.resource = resource;
	}


	
}

