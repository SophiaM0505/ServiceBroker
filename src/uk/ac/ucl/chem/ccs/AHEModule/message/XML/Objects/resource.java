package uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * AHE resource XML entity
 * @author davidc
 *
 */

@Root
public class resource {

	@Attribute(required=false)
	Integer id;
	
	@Attribute
	String name;
	
	@Element(required=false)
	String uri;
	
	@Element(required=false)
	String resource_interface;
	
	@Element(required=false)
	String type;
	
	@Element(required=false)
	Integer cpu_count;
	
	@Element(required=false)
	Integer memory;
	
	@Element(required=false)
	Integer vmemory;
	
	@Element(required=false)
	Integer walltimelimit;
	
	@Element(required=false)
	private
	Integer port;
	
	@Element(required=false)
	String arch;
	
	@Element(required=false)
	String ip;

	@Element(required=false)
	String opsys;

	@ElementList(required=false)
	private
	ArrayList<app> app_list;
	
	@Element(required=false)
	private
	String app_name; //used for appinst prepare message;
	
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

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getResource_interface() {
		return resource_interface;
	}

	public void setResource_interface(String resource_interface) {
		this.resource_interface = resource_interface;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getCpu_count() {
		return cpu_count;
	}

	public void setCpu_count(Integer cpu_count) {
		this.cpu_count = cpu_count;
	}

	public Integer getMemory() {
		return memory;
	}

	public void setMemory(Integer memory) {
		this.memory = memory;
	}

	public Integer getVmemory() {
		return vmemory;
	}

	public void setVmemory(Integer vmemory) {
		this.vmemory = vmemory;
	}

	public Integer getWalltimelimit() {
		return walltimelimit;
	}

	public void setWalltimelimit(Integer walltimelimit) {
		this.walltimelimit = walltimelimit;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getOpsys() {
		return opsys;
	}

	public void setOpsys(String opsys) {
		this.opsys = opsys;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public ArrayList<app> getApp_list() {
		return app_list;
	}

	public void setApp_list(ArrayList<app> app_list) {
		this.app_list = app_list;
	}

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}
	
	
	
}

