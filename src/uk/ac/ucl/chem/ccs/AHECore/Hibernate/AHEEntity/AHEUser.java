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

@Entity
@Table(name="AHEUser")
public class AHEUser {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private	long id;
	
	private String username;
	private byte[] hash_pwd;
	private String email;
	String alt_identifer; //alternate identifier, i.e. subject dn of Certificate, ACD id
	private String acd_vo_group;
	
	private String security_type; //Can be AHE_SSL_CLIENT, AHE_PASS, ACD: defined in AHE_SECURITY_TYPE enum

	private String session_token;
	private Date token_expiry_timestamp;
	
	private String role;
	private Date timestamp;
	private boolean active;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "User_Credentials", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "credential_id") })
	private Set<PlatformCredential> credentials = new HashSet<PlatformCredential>(0);
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
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

	public void setRole(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public void setCredentials(Set<PlatformCredential> credentials) {
		this.credentials = credentials;
	}


	public Set<PlatformCredential> getCredentials() {
		return credentials;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setHash_pwd(byte[] hash_pwd) {
		this.hash_pwd = hash_pwd;
	}

	public byte[] getHash_pwd() {
		return hash_pwd;
	}
	
	public String getAlt_identifer() {
		return alt_identifer;
	}

	public void setAlt_identifer(String alt_identifer) {
		this.alt_identifer = alt_identifer;
	}

	public String getSecurity_type() {
		return security_type;
	}

	public void setSecurity_type(String security_type) {
		this.security_type = security_type;
	}

	public String getAcd_vo_group() {
		return acd_vo_group;
	}

	public void setAcd_vo_group(String acd_vo_group) {
		this.acd_vo_group = acd_vo_group;
	}

	public String getSession_token() {
		return session_token;
	}

	public void setSession_token(String session_token) {
		this.session_token = session_token;
	}

	public Date getToken_expiry_timestamp() {
		return token_expiry_timestamp;
	}

	public void setToken_expiry_timestamp(Date token_expiry_timestamp) {
		this.token_expiry_timestamp = token_expiry_timestamp;
	}
}
