package negotiation.HibernateImp;

public class Offer {
	
	private long id;
	private long negId;
	private long jobId;
	private String username;
	private String groupname;
	private String appname;
	private String startTime;
	private long maxDuration;
	private long maxTotalCpuT;
	private long minCpuTime;
	private String owner;
	private Service service;
	private String status;
	private String worker;
	private String share;
	private int level;
	private int overPrivilege; // 0 for true, 1 for false.
	private String endTime;
	private double maxCost;
	private int requiredCpuNo;
	private long requiredCpuT;
	private double userBalance;
	private String endpoint;
	private String sub;  // String for sub-offer ids for an combined Offer; "" for non sub Offers
	private int numjobs;
	private int nefold;
	private String deadline;
	
	public void setId(long id){
		this.id = id;
	}
	
	public long getId(){
		return id;
	}
	
	public long getNegId(){
		return negId;
	}
	
	public void setNegId(long negId){
		this.negId = negId;
	}
	
	public long getJobId(){
		return jobId;
	}
	
	public void setJobId(long jobId){
		this.jobId = jobId;
	}
	
	public void setGroupname(String groupname){
		this.groupname = groupname;
	}
	
	public String getGroupname(){
		return groupname;
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public String getUsername(){
		return username;
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
	
	public void setStartTime(String starTtime){
		this.startTime = startTime;
	}
	
	public String getEndTime(){
		return endTime;
	}
	
	public void setEndTime(String endTime){
		this.endTime = endTime;
	}
	
	public long getMaxDuration(){
		return maxDuration;
	}
	
	public void setMaxTotalCpuT(long maxTotalCpuT){
		this.maxTotalCpuT = maxTotalCpuT;
	}
	
	public long getMaxTotalCpuT(){
		return maxTotalCpuT;
	}
	
	
	public double getMaxCost(){
		return maxCost;
	}
	
	public void setMaxCost(double maxCost){
		this.maxCost = maxCost;
	}

	public void setMaxDuration(long maxDuration){
		this.maxDuration = maxDuration;
	}
	
	public Service getService(){
		return service;
	}
	
	public void setService(Service service){
		this.service = service;
	}
	
	public String getOwner(){
		return owner;
	}
	
	public void setOwner(String owner){
		this.owner = owner;
	}
	
	public String getStatus(){
		return status;
	}
	
	public void setStatus(String status){
		this.status = status;
	}

	public void setLevel(int level){
		this.level = level;
	}
	
	public int getLevel(){
		return level;
	}
	
	public void setOverPrivilege(int overPrivilege){
		this.overPrivilege = overPrivilege;
	}
	
	public int getOverPrivilege(){
		return overPrivilege;
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
	
	public int getRequiredCpuNo(){
		return requiredCpuNo;
	}
	
	public void setRequiredCpuNo(int requiredCpuNo){
		this.requiredCpuNo = requiredCpuNo;
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
	
	public String getSub(){
		return sub;
	}
	
	public void setSub(String sub){
		this.sub = sub;
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
