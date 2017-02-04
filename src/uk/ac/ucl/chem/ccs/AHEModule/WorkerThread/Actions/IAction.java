package uk.ac.ucl.chem.ccs.AHEModule.WorkerThread.Actions;

public abstract class IAction {

	private Object entity;

	public IAction(Object entity){
		this.setEntity(entity);
	}
	
	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}
	
}
