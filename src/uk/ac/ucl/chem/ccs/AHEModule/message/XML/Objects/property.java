package uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * AHE property XML entity
 * @author davidc
 *
 */

@Root
public class property {

	@Attribute(required=false)
	private
	int id;
	
	@Attribute
	private
	String name;
	
	@Attribute
	private
	String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
