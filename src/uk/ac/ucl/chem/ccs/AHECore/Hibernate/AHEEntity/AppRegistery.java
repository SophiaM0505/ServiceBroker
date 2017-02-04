package uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name="AppRegistery")
public class AppRegistery {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
    @ManyToOne
    @JoinColumn(name="resource_id")
    private Resource resource;
    
    private String appname;
    private String executable;
//    private String endpoint; //Moved to Resource
//    private String resource_code; //Moved to Resource
    
    private Date timestamp;
    private int active;
    
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "AppReg_Parameter_JoinTable", joinColumns = { @JoinColumn(name = "appreg_id") }, inverseJoinColumns = { @JoinColumn(name = "appreg_para_id") })
	private Set<AppRegisteryArgTemplate> parameterlist = new HashSet<AppRegisteryArgTemplate>(0);
    
	@Column(columnDefinition="TEXT")
	private
	String description;

	
	public String getAppname() {
		return appname;
	}
	
	public void setAppname(String appname) {
		this.appname = appname;
	}
	
//	public String getEndpoint() {
//		return endpoint;
//	}
//	
//	public void setEndpoint(String endpoint) {
//		this.endpoint = endpoint;
//	}
//	

	public void setParameterlist(Set<AppRegisteryArgTemplate> parameterlist) {
		this.parameterlist = parameterlist;
	}

	public Set<AppRegisteryArgTemplate> getParameterlist() {
		return parameterlist;
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

	public void setExecutable(String executable) {
		this.executable = executable;
	}

	public String getExecutable() {
		return executable;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

//	public void setResource_code(String resource_code) {
//		this.resource_code = resource_code;
//	}
//
//	public String getResource_code() {
//		return resource_code;
//	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Resource getResource() {
		return resource;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	 
}

