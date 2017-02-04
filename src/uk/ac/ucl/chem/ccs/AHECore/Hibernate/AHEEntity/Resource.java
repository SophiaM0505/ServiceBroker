package uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Resource")
public class Resource {
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private
	long id;
	
	//TODO : Ambigious, should I include scheme ? how do i compare file transfer host to this 
	private String endpoint_reference; //URI withou schema i.e. www.hostname.com/path
	private String resource_interface;
	private String type;
	private int cpucount;
	private String arch;
	private int memory;
	private int virtualmemory;
	private String opsys;
	private String ip;
	private int port;
	private int walltimelimit;
	private String commonname; // Unique name to id this resource
	
	@Column(columnDefinition="TEXT")
	private
	String description;
	
	private String authen_type; //Set from RESOURCE_AUTHEN_TYPE enum in AHE-Module package
	
	private int active;
	private Date timestamp;
	
	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	public void setEndpoint_reference(String endpoint_reference) {
		this.endpoint_reference = endpoint_reference;
	}
	public String getEndpoint_reference() {
		return endpoint_reference;
	}
	public void setResource_interface(String resource_interface) {
		this.resource_interface = resource_interface;
	}
	public String getResource_interface() {
		return resource_interface;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setCpucount(int cpucount) {
		this.cpucount = cpucount;
	}
	public int getCpucount() {
		return cpucount;
	}
	public void setArch(String arch) {
		this.arch = arch;
	}
	public String getArch() {
		return arch;
	}
	public void setMemory(int memory) {
		this.memory = memory;
	}
	public int getMemory() {
		return memory;
	}
	public void setVirtualmemory(int virtualmemory) {
		this.virtualmemory = virtualmemory;
	}
	public int getVirtualmemory() {
		return virtualmemory;
	}
	public void setOpsys(String opsys) {
		this.opsys = opsys;
	}
	public String getOpsys() {
		return opsys;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getIp() {
		return ip;
	}
	public void setWalltimelimit(int walltimelimit) {
		this.walltimelimit = walltimelimit;
	}
	public int getWalltimelimit() {
		return walltimelimit;
	}
	public void setCommonname(String commonname) {
		this.commonname = commonname;
	}
	public String getCommonname() {
		return commonname;
	}
	public void setActive(int active) {
		this.active = active;
	}
	public int getActive() {
		return active;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	public String getAuthen_type() {
		return authen_type;
	}
	public void setAuthen_type(String authen_type) {
		this.authen_type = authen_type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}