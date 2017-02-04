package negotiation.HibernateImp;

public class Reservation {
	
	private long id;
	private long offer_id;
	private int level;
	private String cputime;
	private double cost;
	private String appname;
	private String worker;
	
	
	public void setId(long id){
		this.id = id;
	}
	
	public long getId(){
		return id;
	}
	
	public void setOfferId(long offer_id){
		this.offer_id = offer_id;
	}
	
	public long getOfferId(){
		return offer_id;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public int getLevel(){
		return level;
	}
	
	public void setCpuTime(String cputime){
		this.cputime = cputime;
	}
	
	public String getCpuTime(){
		return cputime;
	}

	public void setCost(double cost){
		this.cost = cost;
	}
	
	public double getCost(){
		return cost;
	}
	
	public String getAppname(){
		return appname;
	}
	
	public void setAppname(String appname){
		this.appname = appname;
	}
	
	public String getWorker(){
		return worker;
	}
	
	public void setWoker(String worker){
		this.worker = worker;
	}
	
}
