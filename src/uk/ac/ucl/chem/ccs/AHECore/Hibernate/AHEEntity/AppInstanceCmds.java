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
 * AppInstanceCmds stores the AHE commands and parameters (AppInstanceCmdParameter) that has been executed on the specific
 * AppInstance. (History log)
 * 
 * 
 * @author davidc
 *
 */

@Entity
@Table(name="AppInstanceCmds")
public class AppInstanceCmds {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

    @ManyToOne
    @JoinColumn(name="appinstance_id")
    private AppInstance appinstance;
	
    private String command;
    private Date timestamp;
    private int active;
	
    public long getId() {
		return id;
	}
	
    public void setId(long id) {
		this.id = id;
	}
		
    public String getCommand() {
		return command;
	}
	
    public void setCommand(String command) {
		this.command = command;
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



}
