package uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name="FileStaging")
public class FileStaging {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
    @ManyToOne
    @JoinColumn(name="app_inst_id")
	private AppInstance appinstance; 
	
    private String identifier; //Job submission identifier
    
	private String source; //filename, destination
	private String target;
	
	private boolean stage_in; // stage_in = 0, stage_out = 1 (Used for AppInstance file transfer)
	

	private int status; //-1 failed, 0 pending, 1 completed
	
	private int active;
	private Date timestamp;
	
    @ManyToOne
    @JoinColumn(name="user_id")
	private AHEUser user; //For File transfers that are independent of AppInstance operations
	
//    @ManyToOne
//    @JoinColumn(name="resource_id")
//	private Resource resource; //Redundant, TODO: there are two resources per file transfer. They should be extracted from source, target uri
//	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}

	public void setAppinstance(AppInstance appinstance) {
		this.appinstance = appinstance;
	}

	public AppInstance getAppinstance() {
		return appinstance;
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

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getTarget() {
		return target;
	}

	public void setStage_in(boolean stage_in) {
		this.stage_in = stage_in;
	}

	public boolean isStage_in() {
		return stage_in;
	}

	public AHEUser getUser() {
		return user;
	}

	public void setUser(AHEUser user) {
		this.user = user;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}
