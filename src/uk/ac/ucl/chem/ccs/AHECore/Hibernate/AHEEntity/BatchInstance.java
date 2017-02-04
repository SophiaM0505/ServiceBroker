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

/**
 * Batch Instance is used by AHE to create multiple jobs. There are two main types of batch jobs, sequential and parameter-based
 * 
 * Sequential jobs are AppInstances executed one after another and can be declared in Job Sequence filed
 * 
 * 
 * Parameter jobs are Appinstances created with a range of variables (identified by ${var}. Variable range is declared using [1:5] or [1,2,4,5] python notation). The range of these 
 * variables and the variable properties are declared and looked for in AppInstanceArg
 * 
 * 
 * @author davidc
 *
 */

@Entity
@Table(name="BatchInstance")
public class BatchInstance {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

    @ManyToOne
    @JoinColumn(name="aheuser_id")
	private
    AHEUser owner;
    
    @ManyToOne
    @JoinColumn(name="appreg_id")
    private AppRegistery appreg;
    
    private String simname;
     
    private  String workflow_id; //Later feature, a AppInstance can be ran on different workflows
 
	private String state;
	
	@Column(columnDefinition="TEXT")
	private String state_info;
	//Date state_change; //Should keep state history, implement this later with supported API

	private AppInstance[] job_sequence;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "BatchAppInstances", joinColumns = { @JoinColumn(name = "batch_id") }, inverseJoinColumns = { @JoinColumn(name = "appinst_id") })
	private Set<AppInstance> appinstlist = new HashSet<AppInstance>(0);
    
	
    private Date timestamp;
    private int active;
	
    public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getSimname() {
		return simname;
	}
	
	public void setSimname(String simname) {
		this.simname = simname;
	}
	
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
//	public Date getStart_time() {
//		return start_time;
//	}
//	
//	public void setStart_time(Date start_time) {
//		this.start_time = start_time;
//	}
//	
//	public Date getEnd_time() {
//		return end_time;
//	}
//	
//	public void setEnd_time(Date end_time) {
//		this.end_time = end_time;
//	}

	public void setAppreg(AppRegistery appreg) {
		this.appreg = appreg;
	}

	public AppRegistery getAppreg() {
		return appreg;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public int getActive() {
		return active;
	}

    public String getWorkflow_id() {
		return workflow_id;
	}

	public void setWorkflow_id(String workflow_id) {
		this.workflow_id = workflow_id;
	}

	public void setState_info(String state_info) {
		this.state_info = state_info;
	}

	public String getState_info() {
		return state_info;
	}

	public void setOwner(AHEUser owner) {
		this.owner = owner;
	}

	public AHEUser getOwner() {
		return owner;
	}

	public AppInstance[] getJob_sequence() {
		return job_sequence;
	}

	public void setJob_sequence(AppInstance[] job_sequence) {
		this.job_sequence = job_sequence;
	}

	public Set<AppInstance> getAppinstlist() {
		return appinstlist;
	}

	public void setAppinstlist(Set<AppInstance> appinstlist) {
		this.appinstlist = appinstlist;
	}
    
  

}

