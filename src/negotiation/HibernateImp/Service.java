package negotiation.HibernateImp;

public class Service {
	
	//private String service_name;
	private String provider;
	private double cost;
	private double charge;
	private int cpuNo;
	private int memory;
	private String instance;
	//1 for true, 0 for false
	private String measurement;

	
	/*public void setService_name(String service_name){
		this.service_name = service_name;
	}
	
	public String getService_name(){
		return service_name;
	}
	*/
	public void setProvider(String provider){
		this.provider = provider;
	}
	
	public String getProvider(){
		return provider;
	}
	
	public void setCpuNo(int cpuNo){
		this.cpuNo = cpuNo;
	}
	
	public int getCpuNo(){
		return cpuNo;
	}
	
	public void setMemory(int memory){
		this.memory = memory;
	}
	
	public int getMemory(){
		return memory;
	}
	
	public void setInstance(String instance){
		this.instance = instance;
	}
	
	public String getInstance(){
		return instance;
	}
	
	public void setCharge(double charge){
		this.charge = charge;
	}
	
	public double getCharge(){
		return charge;
	}
	
	public void setCost(double cost){
		this.cost = cost;
	}
	
	public double getCost(){
		return cost;
	}
	
	public void setMeasurement(String measurement){
		this.measurement = measurement;
	}
	
	public String getMeasurement(){
		return measurement;
	}
	
	


}
