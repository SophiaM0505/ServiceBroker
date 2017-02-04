package uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="WorkflowDescription")
public class WorkflowDescription {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private String workflow_name;
	private String workflow_filename;	
	private String workflow_description;
	private boolean appinst_workflow; //Workflow to control other workflows
	
	private Date timestamp;
	private boolean active;

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setWorkflow_name(String workflow_name) {
		this.workflow_name = workflow_name;
	}

	public String getWorkflow_name() {
		return workflow_name;
	}

	public void setWorkflow_filename(String workflow_filename) {
		this.workflow_filename = workflow_filename;
	}

	public String getWorkflow_filename() {
		return workflow_filename;
	}

	public void setWorkflow_description(String workflow_description) {
		this.workflow_description = workflow_description;
	}

	public String getWorkflow_description() {
		return workflow_description;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isAppinst_workflow() {
		return appinst_workflow;
	}

	public void setAppinst_workflow(boolean appinst_workflow) {
		this.appinst_workflow = appinst_workflow;
	}

	
}

