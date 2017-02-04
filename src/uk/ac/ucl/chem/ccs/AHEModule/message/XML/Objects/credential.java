package uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * AHE credential XML entity
 * @author davidc
 *
 */

@Root
public class credential {

	@Attribute(required=false)
	private
	Integer id;
	
	@Attribute
	String created;
	
	@Attribute
	String name;
	
	@Attribute
	private	String type;
	
	@Element(required=false)
	private
	String cred_location;
	
	@Element(required=false)
	private
	String proxy_location;
	
	@Element(required=false)
	private
	String username;
	
	@Element(required=false)
	private
	String cred_alias;
	
	@Element(required=false)
	private
	String trust_location;
	
	@Element(required=false)
	private
	String user_key;
	
	@Element(required=false)
	private
	String certificate_directory;

	@Element(required=false)
	private
	String registry;
	
	@ElementList(required=false)
	ArrayList<resource> resource_list;
	
	@ElementList(required=false)
	private
	ArrayList<user> owner_list;

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<resource> getResource_list() {
		return resource_list;
	}

	public void setResource_list(ArrayList<resource> resource_list) {
		this.resource_list = resource_list;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<user> getOwner_list() {
		return owner_list;
	}

	public void setOwner_list(ArrayList<user> owner_list) {
		this.owner_list = owner_list;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCred_location() {
		return cred_location;
	}

	public void setCred_location(String cred_location) {
		this.cred_location = cred_location;
	}

	public String getProxy_location() {
		return proxy_location;
	}

	public void setProxy_location(String proxy_location) {
		this.proxy_location = proxy_location;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTrust_location() {
		return trust_location;
	}

	public void setTrust_location(String trust_location) {
		this.trust_location = trust_location;
	}

	public String getCred_alias() {
		return cred_alias;
	}

	public void setCred_alias(String cred_alias) {
		this.cred_alias = cred_alias;
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

	public String getRegistry() {
		return registry;
	}

	public void setRegistry(String registry) {
		this.registry = registry;
	}

	
	
}

