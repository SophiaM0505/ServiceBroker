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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Arguments for executables stored in the AppInstance
 * 
 * TODO: So far, all AppInstanceArg are assumed to be application related. This may not always be the case. An App Instance can store parameters of platform, application or other types of resource. See AppinstanceArgumentKey and Hiearachy_arg field
 * 
 * 
 * TODO: Use this as a template from Exper-User to normal-user. These values are copied over to AppInstanceCmdArg, unless specifically overwritten by user or specified to be ignored
 * 
 * @author davidc
 *
 */

@Entity
@Table(name="AppInstanceArg")
public class AppInstanceArg {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
    @ManyToOne
    @JoinColumn(name="appinstance_id")
    private AppInstance appinstance;
	
	private String arg; //Argument Key. NOTE this does not have to be unique. An app instance can have multiple arguments with the same key i.e. arg1-> "cmd1" arg1-> "cmd2". JSON equivalent would be {arg1:[cmd1,cmd2]} as json object or simply [cmd1,cmd2] as json array for simplicity
	private String value; 
	private Date timestamp;
	private int active;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "hierarchy_arg", joinColumns = { @JoinColumn(name = "parent_arg_id") }, inverseJoinColumns = { @JoinColumn(name = "child_arg_id") })
	private Set<AppInstanceArg> hierarchy_arg = new HashSet<AppInstanceArg>(0); // Option to Create a hierarchy based argument i.e. key1-> {key1a->b,key2->c}
    
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getArg() {
		return arg;
	}
	
	public void setArg(String arg) {
		this.arg = arg;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public int getActive() {
		return active;
	}

	public void setAppinstance(AppInstance appinstance) {
		this.appinstance = appinstance;
	}

	public AppInstance getAppinstance() {
		return appinstance;
	}

	public Set<AppInstanceArg> getHierarchy_arg() {
		return hierarchy_arg;
	}

	public void setHierarchy_arg(Set<AppInstanceArg> hierarchy_arg) {
		this.hierarchy_arg = hierarchy_arg;
	}


}

