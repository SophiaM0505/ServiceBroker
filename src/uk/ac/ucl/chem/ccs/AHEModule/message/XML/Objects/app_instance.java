package uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects;

import java.util.ArrayList;
import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * AHE application instance XML entity
 * @author davidc
 *
 */

@Root
public class app_instance {

	@Attribute
	Integer id;
	
	@Attribute
	String name;
	
	@Attribute(required=false)
	private
	String ahe_id;
	
	@Attribute
	String status;
	
	@Attribute(required=false)
	private
	String timestamp;
	
	@Element(required=false)
	private
	app app;
	
	// Prepare response message

	@ElementList(required = false)
	private ArrayList<resource> resource_list;

	// Normal response message structure

	@ElementList(required = false)
	private ArrayList<property> property_list;

	@ElementList(required = false)
	private ArrayList<transfer> transfer_list;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<resource> getResource_list() {
		return resource_list;
	}

	public void setResource_list(ArrayList<resource> resource_list) {
		this.resource_list = resource_list;
	}

	public ArrayList<property> getProperty_list() {
		return property_list;
	}

	public void setProperty_list(ArrayList<property> property_list) {
		this.property_list = property_list;
	}

	public ArrayList<transfer> getTransfer_list() {
		return transfer_list;
	}

	public void setTransfer_list(ArrayList<transfer> transfer_list) {
		this.transfer_list = transfer_list;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public app getApp() {
		return app;
	}

	public void setApp(app app) {
		this.app = app;
	}

	public String getAhe_id() {
		return ahe_id;
	}

	public void setAhe_id(String ahe_id) {
		this.ahe_id = ahe_id;
	}
	 
	 
	 
}

