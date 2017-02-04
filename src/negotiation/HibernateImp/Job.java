package negotiation.HibernateImp;

public class Job {
	
	private long id;
	private long contractId;
	private String oldNode;
	private String newNode;
	
	public void setId(long id){
		this.id = id;
	}
	
	public long getId(){
		return id;
	}
	
	public long getContractId(){
		return contractId;
	}
	
	public void setContractId(long contractId){
		this.contractId = contractId;
	}
	
	public String getOldNode(){
		return oldNode;
	}
	
	public void setOldNode(String oldNode){
		this.oldNode = oldNode;
	}
	
	public String getNewNode(){
		return newNode;
	}
	
	public void setNewNode(String newNode){
		this.newNode = newNode;
	}

}
