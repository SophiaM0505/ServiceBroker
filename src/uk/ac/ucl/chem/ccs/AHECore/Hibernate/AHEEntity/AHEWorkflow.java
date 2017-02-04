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
@Table(name="AHEWorkflow")
public class AHEWorkflow {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private long process_id;
	
	private Integer ksession_id;
	
	private String workflow_id;
	private String workflow_path;
	
    @ManyToOne
    @JoinColumn(name="appinstance_id")
    private AppInstance appinstance;
	
    private Date timestamp;
    private int active;


	public void setProcess_id(long process_id) {
		this.process_id = process_id;
	}

	public long getProcess_id() {
		return process_id;
	}

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

	public Integer getKsession_id() {
		return ksession_id;
	}

	public void setKsession_id(Integer ksession_id) {
		this.ksession_id = ksession_id;
	}

	public String getWorkflow_id() {
		return workflow_id;
	}

	public void setWorkflow_id(String workflow_id) {
		this.workflow_id = workflow_id;
	}

	public String getWorkflow_path() {
		return workflow_path;
	}

	public void setWorkflow_path(String workflow_path) {
		this.workflow_path = workflow_path;
	}
	
	
	
	
}