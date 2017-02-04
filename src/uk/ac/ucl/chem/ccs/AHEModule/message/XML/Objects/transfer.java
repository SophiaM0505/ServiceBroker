package uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * AHE transfer XML entity
 * @author davidc
 *
 */

@Root
public class transfer {

	@Attribute(required=false)
	Integer id;
	
	@Attribute(required=false)
	String ahe_id;
	
	@Attribute
	private
	String status;
	
	@Element
	String source;
	
	@Element
	String dest;
	
	@Attribute(required=false)
	String stage_dir; // staging direction, in or out

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAhe_id() {
		return ahe_id;
	}

	public void setAhe_id(String ahe_id) {
		this.ahe_id = ahe_id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getStage_dir() {
		return stage_dir;
	}

	public void setStage_dir(String stage_dir) {
		this.stage_dir = stage_dir;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}

