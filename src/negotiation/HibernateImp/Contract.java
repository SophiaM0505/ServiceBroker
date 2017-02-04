package negotiation.HibernateImp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

public class Contract {
	//private Offer offer;
	//private String date;

	private long id;
	long jobId;
	private String username;
	private String groupname;
	private String appname;
	private String startTime;
	private String contractDate;
	private Service service;
	private String owner;
	private String status;
	private String worker;
	private String share;
	private long maxDuration;
	private long maxTotalCpuT;
	private long minCpuTime;
	private long requiredCpuT;
	private String endTime;
	//private String contractTime;
	private double maxCost;
	private int requiredCpuNo;
	private double userBalance;
	private int level;
	private int overPrivilege; // 0 for true, 1 for false.
	private String endpoint;
	private String sub; // 0 for its a sub Contract in an combined Contract; 1 for non sub Contracts
	private int numjobs;
	private int nefold;
	private String deadline;
	
	
	public void setId(long id){
		this.id = id;
	}
	
	public long getId(){
		return id;
	}
	
	public long getJobId(){
		return jobId;
	}
	
	public void setJobId(long jobId){
		this.jobId = jobId;
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public String getUsername(){
		return username;
	}
	
	public void setGroupname(String groupname){
		this.groupname = groupname;
	}
	
	public String getGroupname(){
		return groupname;
	}
	
	public String getAppname(){
		return appname;
	}
	
	public void setAppname(String appname){
		this.appname = appname;
	}
	
	public String getStartTime(){
		return startTime;
	}
	
	public void setStartTime(String startTime){
		this.startTime = startTime;
	}
	
	/*public String getContractTime(){
		return contractTime;
	}
	
	public void setContracTime(String contractTime){
		this.contractTime = contractTime;
	}
	*/
	public String getEndTime(){
		return endTime;
	}
	
	public void setEndTime(String endTime){
		this.endTime = endTime;
	}
	
	public long getMaxDuration(){
		return maxDuration;
	}
	
	public void setMaxDuration(long maxDuration){
		this.maxDuration = maxDuration;
	}
	
	public void setMaxTotalCpuT(long maxTotalCpuT){
		this.maxTotalCpuT = maxTotalCpuT;
	}
	
	public long getMaxTotalCpuT(){
		return maxTotalCpuT;
	}
	
	public Service getService(){
		return service;
	}
	
	public void setService(Service service){
		this.service = service;
	}
	
	public String getContractDate(){
		return contractDate;
	}
	
	public void setContractDate(String contractDate){
		this.contractDate = contractDate;
	}
	
	public String getOwner(){
		return owner;
	}
	
	public void setOwner(String owner){
		this.owner = owner;
	}
	
	public void setStatus(String status){
		this.status = status;
	}
	
	public String getStatus(){
		return status;
	}

	public void setLevel(int level){
		this.level = level;
	}
	
	public int getLevel(){
		return level;
	}
	
	public String getWorker(){
		return worker;
	}
	
	public void setWorker(String worker){
		this.worker = worker;
	}
	
	public String getShare(){
		return share;
	}
	
	public void setShare(String share){
		this.share = share;
	}
	
	
	public void setOverPrivilege(int overPrivilege){
		this.overPrivilege = overPrivilege;
	}
	
	public int getOverPrivilege(){
		return overPrivilege;
	}
	/*public Offer getOffer(){
		return offer;
	}
	
	public void setOffer(Offer offer){
		this.offer = offer;
	}*/
	
	public double getMaxCost(){
		return maxCost;
	}
	
	public void setMaxCost(double maxCost){
		this.maxCost = maxCost;
	}
	
	public int getRequiredCpuNo(){
		return requiredCpuNo;
	}
	
	public void setRequiredCpuNo(int requiredCpuNo){
		this.requiredCpuNo = requiredCpuNo;
	}
	
	public String getSub(){
		return sub;
	}
	
	public void setSub(String sub){
		this.sub = sub;
	}
	
	public long getRequiredCpuT(){
		return requiredCpuT;
	}
	
	public void setRequiredCpuT(long requiredCpuT){
		this.requiredCpuT = requiredCpuT;
	}
	
	public long getMinCpuTime(){
		return minCpuTime;
	}
	
	public void setMinCpuTime(long minCpuTime){
		this.minCpuTime = minCpuTime;
	}

	public double getUserBalance(){
		return userBalance;
	}
	
	public void setUserBalance(double userBalance){
		this.userBalance = userBalance;
	}
	
	public String getEndpoint(){
		return endpoint;
	}
	
	public void setEndpoint(String endpoint){
		this.endpoint = endpoint;
	}
	
	public String getDeadline(){
		return deadline;
	}
	
	public void setDeadline(String deadline){
		this.deadline = deadline;
	}
	
	public int getNumjobs(){
		return numjobs;
	}
	
	public void setNumjobs(int numjobs){
		this.numjobs = numjobs;
	}
	
	public int getNefold(){
		return nefold;
	}
	
	public void setNefold(int nefold){
		this.nefold = nefold;
	}
}
