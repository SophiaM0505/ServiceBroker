package uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects;

import java.util.ArrayList;
import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * AHE user XML entity
 * @author davidc
 *
 */

@Root
public class user {
	
	@Attribute
	String username;
	@Attribute
	String authen_type;
	@Element(required=false)
	String email;
	@Attribute
	String role;
	@Attribute
	String created;
	@Element(required=false)
	private
	String alt_identifier;
	
	@ElementList(required=false)
	private
	ArrayList<credential> cred_list;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAuthen_type() {
		return authen_type;
	}
	public void setAuthen_type(String authen_type) {
		this.authen_type = authen_type;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public ArrayList<credential> getCred_list() {
		return cred_list;
	}
	public void setCred_list(ArrayList<credential> cred_list) {
		this.cred_list = cred_list;
	}
	public String getAlt_identifier() {
		return alt_identifier;
	}
	public void setAlt_identifier(String alt_id) {
		this.alt_identifier = alt_id;
	}
	
	
	
}

