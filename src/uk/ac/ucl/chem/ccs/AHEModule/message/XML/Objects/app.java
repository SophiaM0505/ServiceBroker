package uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * AHE application XML entity
 * @author davidc
 *
 */

@Root
public class app {

	@Attribute
	Integer id;
	
	@Attribute
	String name;
	
	@Element
	private
	String resource_id;
	
	@Element
	String executable;
	
	@Element
	private
	String uri;
	
	@Element(required=false)
	String resource_interface;
	
	@Element(required=false)
	private
	String description;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExecutable() {
		return executable;
	}

	public void setExecutable(String executable) {
		this.executable = executable;
	}

	public String getResource_interface() {
		return resource_interface;
	}

	public void setResource_interface(String resource_interface) {
		this.resource_interface = resource_interface;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getResource_id() {
		return resource_id;
	}

	public void setResource_id(String resource_id) {
		this.resource_id = resource_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
