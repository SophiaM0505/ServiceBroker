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
@Table(name="AHEJobSubmission")
public class AHEJobSubmission {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private
	int id;
	
    @ManyToOne
    @JoinColumn(name="appinstance_id")
	private
	AppInstance appinstance;
	
	private String submission_id;
	private String status; //Log this ??? If so create another table or keep it on file?
	private Date last_status_check;

    private Date next_poll_datetime;
    private Date max_poll_datetime;
    private int pollcount;
    private long poll_interval_ms;
	
	private Date timestamp;
	private int active;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setAppinstance(AppInstance appinstance) {
		this.appinstance = appinstance;
	}
	
	public AppInstance getAppinstance() {
		return appinstance;
	}
	
	public void setSubmission_id(String submission_id) {
		this.submission_id = submission_id;
	}
	
	public String getSubmission_id() {
		return submission_id;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setLast_status_check(Date last_status_check) {
		this.last_status_check = last_status_check;
	}
	
	public Date getLast_status_check() {
		return last_status_check;
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

	public void setNext_poll_datetime(Date next_poll_datetime) {
		this.next_poll_datetime = next_poll_datetime;
	}

	public Date getNext_poll_datetime() {
		return next_poll_datetime;
	}

	public void setMax_poll_datetime(Date max_poll_datetime) {
		this.max_poll_datetime = max_poll_datetime;
	}

	public Date getMax_poll_datetime() {
		return max_poll_datetime;
	}

	public void setPollcount(int pollcount) {
		this.pollcount = pollcount;
	}

	public int getPollcount() {
		return pollcount;
	}

	public void setPoll_interval_ms(long poll_interval_ms) {
		this.poll_interval_ms = poll_interval_ms;
	}

	public long getPoll_interval_ms() {
		return poll_interval_ms;
	}
	
}

