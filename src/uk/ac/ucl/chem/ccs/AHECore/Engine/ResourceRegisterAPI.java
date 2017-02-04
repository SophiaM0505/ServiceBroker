package uk.ac.ucl.chem.ccs.AHECore.Engine;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;


/**
 * Resource related functions
 * @author davidc
 *
 */

public class ResourceRegisterAPI {

	/**
	 * Get all resources in AHE
	 * @return Resource array
	 */
	
	public static Resource[] getResourceList(){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        
	        Query query = session.createQuery("select ap from Resource ap where ap.active = :active");
	        query.setParameter("active", 1);
	        List r = query.list();
	        
	        Resource[] array = (Resource[]) r.toArray(new Resource[r.size()]);
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        return array;
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
		
		return new Resource[0];

	}
	
	/**
	 * Get all resources that the specified user have access to
	 * @param user AHE User
	 * @return Resource array
	 */
	
	public static Resource[] getResourceList(AHEUser user){
		
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        
	        Query query = session.createQuery("select distinct cred.resource from AHEUser user inner join user.credentials cred where user.id  = :userid AND user.active = :active");
	        query.setParameter("userid", user.getId());
	        query.setParameter("active", true);
	        List r = query.list();
	        
	        Resource[] array = (Resource[]) r.toArray(new Resource[r.size()]);
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        return array;
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
		
		return new Resource[0];
		
//		ArrayList<Resource> results = new ArrayList<Resource>();
//    	Iterator<PlatformCredential> credtoken = user.getCredentials().iterator();
//
//    	while(credtoken.hasNext()){
//    		
//    		PlatformCredential cred = credtoken.next();
//    		
//    		Iterator<Resource> resToken = cred.getResource().iterator();
//    		
//    		while(resToken.hasNext()){
//    			
//    			Resource res = resToken.next();
//    			
//    			if(results.contains(res))
//    			
//    		}
//    		
//    	}
    	
	}
	
	/**
	 * Get Resource by it's id
	 * @param id
	 * @return
	 */
	
	public static Resource getResource(long id){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        


	        Query query = session.createQuery("select ap from Resource ap where ap.id = :id AND ap.active = :active");
	        query.setParameter("active", 1);
	        query.setParameter("id", id);
	        List r = query.list();
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        if(r.size() > 0)
	        	return (Resource) r.get(0);
	        else
	        	return null;
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
		
		return null;

	}
	
	/**
	 * Transfer Resource are searched based on these assumption and criteria
	 * 1) transfer_uri must be a valid uri with schema://hostname/path
	 * 2) A transfer resource end point reference must also be a valid URI
	 * 3) The hostname must match and the paths with the best match is selected
	 * 
	 * @param uri
	 * @return
	 */
	
	public static Resource getResourceByBestMatch(URI uri){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();

	        Query query = session.createQuery("select ap from Resource ap where :uri like lower(concat(ap.endpoint_reference,'%')) AND ap.active = :active");
	        query.setParameter("active", 1);
	        query.setParameter("uri", uri.toString().toLowerCase());
	        List r = query.list();
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        if(r.size() > 0)
	        	return (Resource) r.get(0);
	        else
	        	return null;
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
		
		return null;

	}
	
	/**
	 * Get Resource by its common name
	 * @param common_name common name
	 * @return Resource entity
	 */
	
	public static Resource getResource(String common_name){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();

	        Query query = session.createQuery("select ap from Resource ap where ap.commonname = :commonname AND ap.active = :active");
	        query.setParameter("active", 1);
	        query.setParameter("commonname", common_name);
	        List r = query.list();
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
	        
	        if(r.size() > 0)
	        	return (Resource) r.get(0);
	        else
	        	return null;
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
		
		return null;

	}
	
	/**
	 * Search resource that fulfils the parameter requirements
	 * @param r_interface resource interface
	 * @param type resource type
	 * @param cpucount cpu count
	 * @param arch architecture
	 * @param memory memory
	 * @param vmemory virtual memory
	 * @param opsys operating system
	 * @param walltimelimit wall time limit
	 * @return
	 */
	
	public static Resource[] searchResourceMinimumRequirement(String r_interface, String type, int cpucount, String arch, int memory, int vmemory, String opsys, int walltimelimit){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        
	        String querystr = "select ap from Resource ap where ap.active = :active ";

	        if(r_interface.length() > 0)
	        	querystr += " AND ap.resource_interface = :rinterface";
	        
	        if(type.length() > 0)
	        	querystr += " AND ap.type = :type";
	        
	        if(cpucount > -1)
	        	querystr += " AND ap.cpucount >= :cpucount";
	        
	        if(arch.length() > 0)
	        	querystr += " AND ap.arch = :arch";
	        
	        if(memory > -1)
	        	querystr += " AND ap.memory >= :memory";
	        
	        if(vmemory > -1)
	        	querystr += " AND ap.virtualmemory >= :vmem";
	        
	        if(opsys.length() > 0)
	        	querystr += " AND ap.opsys = :opsys";
	        
	        if(walltimelimit > -1)
	        	querystr += " AND ap.walltimelimit >= :walltimelimit";
	        
	        
	        Query query = session.createQuery(querystr);
	        query.setParameter("active", 1);
	        
	        if(r_interface.length() > 0)
	        	query.setParameter("rinterface", r_interface);
	        
	        if(type.length() > 0)
	        	query.setParameter("type",type);
	        
	        if(cpucount > -1)
	        	query.setParameter("cpucount",cpucount);
	        
	        if(arch.length() > 0)
	        	query.setParameter("arch",arch);
	        
	        if(memory > -1)
	        	query.setParameter("memory",memory);
	        
	        if(vmemory > -1)
	        	query.setParameter("vmem",vmemory);
	        
	        if(opsys.length() > 0)
	        	query.setParameter("opsys",opsys);
	        
	        if(walltimelimit > -1)
	        	query.setParameter("walltimelimit",walltimelimit);
	        
	        List r = query.list();
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        Resource[] array = (Resource[]) r.toArray(new Resource[r.size()]);
	        
	        return array;
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
		
		return new Resource[0];

	}
	
	/**
	 * Create a new resource
	 * @param name common name
	 * @param endpoint end point URI
	 * @param resource_interface resource interface
	 * @param type resource type
	 * @param cpucount CPU count
	 * @param arch architecture
	 * @param memory memory
	 * @param vmemory virtual memory
	 * @param opsys operating system
	 * @param ip IP
	 * @param walltimelimit wall time limit
	 * @param authen_type authentication type
	 * @param description description
	 * @return Resource entity
	 */
	
	public static Resource createResource(String name, String endpoint, String resource_interface, String type, int cpucount, String arch, int memory, int vmemory, String opsys, String ip, int walltimelimit, int port, String authen_type, String description){
		
		Resource r = new Resource();
		r.setActive(1);
		r.setTimestamp(new Date());
		
		r.setCommonname(name);
		r.setResource_interface(resource_interface);
		r.setEndpoint_reference(endpoint);
		r.setType(type);
		r.setCpucount(cpucount);
		r.setArch(arch);
		r.setMemory(memory);
		r.setVirtualmemory(vmemory);
		r.setOpsys(opsys);
		r.setIp(ip);
		r.setWalltimelimit(walltimelimit);
		r.setPort(port);
		r.setAuthen_type(authen_type);
		
		if(description != null)
			r.setDescription(description);
		
		HibernateUtil.SaveOrUpdate(r);
		
		return r;
	}
	
	/**
	 * Create a new Resource in AHE
	 * @param name common name
	 * @param endpoint end point URI
	 * @param resource_type resource type
	 * @param port port
	 * @param authen_type authentication type
	 * @param description description
	 * @return
	 */
	
	public static Resource createResource(String name, String endpoint, String resource_type, int port, String authen_type, String description){
		
		Resource r = new Resource();
		r.setActive(1);
		r.setTimestamp(new Date());
		
		r.setCommonname(name);
		r.setResource_interface(resource_type);
		r.setEndpoint_reference(endpoint);
		r.setPort(port);
		r.setAuthen_type(authen_type);
		
		if(description != null)
			r.setDescription(description);
		
		HibernateUtil.SaveOrUpdate(r);
		
		return r;
	}
	
	/**
	 * Update resource. If any of the parameter are set to null, that parameter will be ignored and not be updated
	 * @param common_name Resource with this common name to be edited
	 * @param new_commonname new common name
	 * @param endpoint new end point URI
	 * @param resource_interface new resource interface
	 * @param type new type
	 * @param cpucount new cpu count
	 * @param arch new architecture
	 * @param memory new memory
	 * @param vmemory new virtual memory
	 * @param opsys new Operating system
	 * @param ip new IP
	 * @param walltimelimit new wall time limit
	 * @param port new port
	 * @param authen_type new authentication type
	 * @param description new description
	 * @return
	 * @throws AHEException
	 */
	
	public static Resource updateResource(String common_name, String new_commonname, String endpoint, String resource_interface, String type, int cpucount, String arch, int memory, int vmemory, String opsys, String ip, int walltimelimit, int port, String authen_type, String description) throws AHEException{
		
		Resource r = getResource(common_name);
		
		if(r == null){
			throw new AHEException("Update Resource failed, resource not found : " + common_name);
		}
		
		if(new_commonname != null && !common_name.equalsIgnoreCase(new_commonname)){
			Resource check = getResource(new_commonname);
			
			if(check != null)
				throw new AHEException("Update Resource failed. Resource name is already in use : " + new_commonname);
			
			r.setCommonname(new_commonname);
		}
		
		if(endpoint != null)
			r.setEndpoint_reference(endpoint);
		
		if(resource_interface != null)
			r.setResource_interface(resource_interface);
		
		if(type != null)
			r.setType(type);
		
		if(cpucount > -1)
			r.setCpucount(cpucount);
		
		if(arch != null)
			r.setArch(arch);
		
		if(memory > -1)
			r.setMemory(memory);
		
		if(vmemory > -1)
			r.setVirtualmemory(vmemory);
		
		if(opsys != null)
			r.setOpsys(opsys);
		
		if(ip != null)
			r.setIp(ip);
		
		if(walltimelimit > -1)
			r.setWalltimelimit(walltimelimit);
			
		if(port > -1)
			r.setPort(port);
		
		if(authen_type != null)
			r.setAuthen_type(authen_type);
		
		if(description != null)
			r.setDescription(description);
		
		HibernateUtil.SaveOrUpdate(r);
		
		return r;
	}
	
	/**
	 * remove resource from AHE. This resource is set to inactive
	 * @param common_name resource name that will be deactivated
	 * @return
	 * @throws AHEException
	 */
	
	public static Resource deleteResource(String common_name) throws AHEException{
		
		Resource r = getResource(common_name);
		
		if(r == null){
			throw new AHEException("Update Resource failed, resource not found : " + common_name);
		}
		
		r.setActive(0);
		
		HibernateUtil.SaveOrUpdate(r);
		
		return r;
	}
	
	
}

