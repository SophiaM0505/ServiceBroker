package negotiation.HibernateImp;

public class ContractInst {
	
	private long conId;
	private long instId;
	private long id;
	
	public void setId(long id){
		this.id = id;
	}
	
	public long getId(){
		return id;
	}
	
	public void setConId(long conId){
		this.conId = conId;
	}
	
	public long getConId(){
		return conId;
	}
	
	public void setInstId(long instId){
		this.instId = instId;
	}
	
	public long getInstId(){
		return instId;
	}

}
