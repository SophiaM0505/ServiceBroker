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
@Table(name="AppRegisteryArgTemplate")
public class AppRegisteryArgTemplate {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

    @ManyToOne
    @JoinColumn(name="appreg_id")
    private AppRegistery appreg;
	
	private String arg;
	private String value;
	private Date timestamp;
	private int active;
	
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

	public void setAppreg(AppRegistery appreg) {
		this.appreg = appreg;
	}

	public AppRegistery getAppreg() {
		return appreg;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public int getActive() {
		return active;
	}
		
}

