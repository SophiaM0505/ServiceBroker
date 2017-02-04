package uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Workflow Instance are workflows in AHE run by user (generally used to do batch jobs which control AppInstance Workflows)
 * @author davidc
 *
 */

@Entity
@Table(name="WorkflowInstance")
public class WorkflowInstance {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

    @ManyToOne
    @JoinColumn(name="aheuser_id")
	private AHEUser owner;
    
    private String name;
    
    private  String workflow_id;
    
	private String state;
	private String state_info;
    
    private Date timestamp;
    private boolean active;
    
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public AHEUser getOwner() {
		return owner;
	}

	public void setOwner(AHEUser owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWorkflow_id() {
		return workflow_id;
	}

	public void setWorkflow_id(String workflow_id) {
		this.workflow_id = workflow_id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState_info() {
		return state_info;
	}

	public void setState_info(String state_info) {
		this.state_info = state_info;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}


	
}
