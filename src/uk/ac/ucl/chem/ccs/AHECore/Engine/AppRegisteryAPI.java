package uk.ac.ucl.chem.ccs.AHECore.Engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AHEException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.AppAlreadyExistException;
import uk.ac.ucl.chem.ccs.AHECore.Exceptions.PlatformCredentialException;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.HibernateUtil;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppRegistery;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHECore.Security.SecurityUserAPI;



/**
 * Application related functions.
 * @author davidc
 *
 */

public class AppRegisteryAPI {

	/**
	 * Get all applications in AHE
	 * @return AppRegistry array
	 */
	
	public static AppRegistery[] getApplicationList(){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        


	        Query query = session.createQuery("select ap from AppRegistery ap where ap.active = :active");
	        query.setParameter("active", 1);
	        List r = query.list();
	        
	        AppRegistery[] array = (AppRegistery[]) r.toArray(new AppRegistery[r.size()]);
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        return array;
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
		
		return null;
	}
	
	/**
	 * Get applications that the specified user can access
	 * @param user AHE User to search for
	 * @return acessible AppRegistry array
	 */
	
	public static AppRegistery[] getApplicationList(AHEUser user){
		
    	ArrayList<AppRegistery> results = new ArrayList<AppRegistery>();
    	AppRegistery[] all = getApplicationList();
    	
    	for(AppRegistery app : all){
    		try {
				SecurityUserAPI.getUserPlatformCredential(user, app.getResource());
				results.add(app);
			} catch (PlatformCredentialException e) {
				continue;
			}
    	}

    	return results.toArray(new AppRegistery[results.size()]);
    	
	}
	
	/**
	 * Returns an application array which is hosted on a specified resource
	 * @param resource_name resource name
	 * @return AppRegistry array that is hosted on a specified resource
	 */
	
	public static AppRegistery[] getApplicationList(String resource_name){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        


	        Query query = session.createQuery("select ap from AppRegistery ap where ap.resource.commonname = :name AND ap.active = :active");
	        query.setParameter("active", 1);
	        query.setParameter("name", resource_name);
	        List r = query.list();
	        
	        AppRegistery[] array = (AppRegistery[]) r.toArray(new AppRegistery[r.size()]);
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        return array;
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
		
		return new AppRegistery[0];
	}
	
//	public static AppRegistery getApplication(String appname, String resource_endpoint){
//		
//    	try{
//        	
//	        Session session = HibernateUtil.getSessionFactory().openSession();
//
//	        
//	        Transaction txn = session.beginTransaction();
//	        
//
//
//	        Query query = session.createQuery("select ap from AppRegistery ap where ap.appname = :appname and ap.endpoint = :end");
//	        query.setParameter("appname", appname);
//	        query.setParameter("end", resource_endpoint);
//	        List r = query.list();
//	        
//
//	        
//	        session.flush();
//	        txn.commit();
//	        session.close();
//	        
//	        //HibernateUtil.getSessionFactory().close();
//        
//	        if(r.size() == 0)
//	        	return null;
//	        else
//	        	return (AppRegistery) r.get(0);
//	        
//	        
//    	}catch (Exception e) { 
//            System.out.println(e.getMessage());
//        } 
//		
//		
//		return null;
//		
//		
//	}
	
	/**
	 * Get an application with specific application name and resouce name
	 * @param appname application name
	 * @param resource_name resource name
	 * @return AppRegistry
	 */
	
	public static AppRegistery getApplication(String appname, String resource_name){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();
	        
	        Transaction txn = session.beginTransaction();
	        
	        Query query = session.createQuery("select ap from AppRegistery ap where ap.resource.commonname = :resource_name AND ap.appname = :appname and ap.active = :active");
	        query.setParameter("appname", appname);
	        query.setParameter("resource_name", resource_name);
	        query.setParameter("active", 1);
	        List r = query.list();     
	     
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        if(r.size() == 0)
	        	return null;
	        else
	        	return (AppRegistery) r.get(0);
	        
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
		
		
		return null;
		
	}
	
	/**
	 * Get application based on its id
	 * @param app_reg_id app id
	 * @return AppRegistery
	 */
	
	public static AppRegistery getApplication(long app_reg_id){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();
	        
	        Transaction txn = session.beginTransaction();

	        Query query = session.createQuery("select ap from AppRegistery ap where ap.id = :id and ap.active = :active");
	        query.setParameter("id", app_reg_id);
	        query.setParameter("active", 1);
	        List r = query.list();
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        if(r.size() == 0)
	        	return null;
	        else
	        	return (AppRegistery) r.get(0);
	        
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
		
		
		return null;
	}
	
	/**
	 * Return an application array that have the specified application name
	 * @param appname application name
	 * @return AppRegistery
	 */
	
	public static AppRegistery[] getApplication(String appname){
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        
	        Query query = session.createQuery("select ap from AppRegistery ap where ap.appname = :appname and ap.active = :active");
	        query.setParameter("appname", appname);
	        query.setParameter("active", 1);
	        List r = query.list();
	        
	     
	        
	        session.flush();
	        txn.commit();
	        
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        return (AppRegistery[]) r.toArray(new AppRegistery[0]);
	        
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
		
		
		return new AppRegistery[0];
	}
	
	/**
	 * Edit an application
	 * @param appid application id to be edited
	 * @param new_appname new application name
	 * @param new_exec new executable
	 * @param new_resource new resource
	 * @param description new description
	 * @return AppRegistery
	 * @throws AppAlreadyExistException
	 * @throws AHEException
	 */
	
	public static AppRegistery editApplication(long appid, String new_appname, String new_exec, String new_resource, String description) throws AppAlreadyExistException, AHEException{
		
		AppRegistery reg = getApplication(appid);
		
		if(reg == null){
			throw new AHEException("Edit App Registery failed: App Not found for : " + appid);
		}
		
		if(new_resource != null){
			
			Resource r = ResourceRegisterAPI.getResource(new_resource);
			
			if(r == null){
				throw new AHEException("Edit App Registery failed:  Resource Not found for : " + new_resource);
			}
			
			reg.setResource(r);
			
		}
		
		if(new_appname != null){
			
			AppRegistery reg1 = getApplication(new_appname,reg.getResource().getCommonname());

			if(reg1 != null && reg1.getId() != reg.getId()){
				throw new AHEException("Edit App Registery failed: App already exists, can not changed current App name : " + new_appname);
			}
			
			reg.setAppname(new_appname);
			
		}
		
		if(new_exec != null){
			reg.setExecutable(new_exec);
		}
		
		if(new_resource != null){
			
			Resource r = ResourceRegisterAPI.getResource(new_resource);
			
			if(r == null){
				throw new AHEException("Edit App Registery failed:  Resource Not found for : " + new_resource);
			}
			
			reg.setResource(r);
			
		}
		
		if(description != null){
			reg.setDescription(description);
		}
		
		HibernateUtil.SaveOrUpdate(reg);
		
		return reg;
		
	}
	
	/**
	 * Delete application
	 * @param appid application id to be deleted
	 * @return AppRegistery
	 * @throws AppAlreadyExistException
	 * @throws AHEException
	 */
	
	public static AppRegistery deleteApplication(long appid) throws AppAlreadyExistException, AHEException{
		
		AppRegistery reg = getApplication(appid);
		
		if(reg == null){
			throw new AHEException("Edit App Registery failed: App Not found for : " + appid);
		}
		
		reg.setActive(0);
	
		HibernateUtil.SaveOrUpdate(reg);
		
		return reg;
		
	}
	
	/**
	 * Create a new application
	 * @param appname application name
	 * @param executable executable path
	 * @param resource_name resource that the application is hosted
	 * @return AppRegistery
	 * @throws AppAlreadyExistException
	 * @throws AHEException
	 */
	
	public static AppRegistery createApplication(String appname, String executable, String resource_name) throws AppAlreadyExistException, AHEException{
		
//		AppRegistery app = getApplication(appname);
//		
//		if(app != null){
//			throw new AppAlreadyExistException("Creating Duplicate Applicaton : " + appname);
//		}
		
		Resource r = ResourceRegisterAPI.getResource(resource_name);
		
		if(r == null){
			throw new AHEException("Resource Not found");
		}
		
		AppRegistery app_check = AppRegisteryAPI.getApplication(appname, resource_name);
		
		if(app_check != null){
			throw new AHEException("Application Profile : " + appname + " already exists in Resource: " + resource_name);
		}
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        


	        AppRegistery reg = new AppRegistery();
	        
	        reg.setAppname(appname);
	        //reg.setEndpoint(endpoint);
	        reg.setExecutable(executable);
	        //reg.setResource_code(ResourceCode);
	        
	        reg.setResource(r);
	        
	        reg.setActive(1);
	        reg.setTimestamp(new Date());
	        reg.setDescription("");
	        
	        session.saveOrUpdate(reg);
	        
	        session.flush();
	        txn.commit();
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        return reg;
	        
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
	
    	return null;
    	
	}
	
	/**
	 * Create a new application
	 * @param appname application name
	 * @param executable executable path
	 * @param resource_name resource that this application is hosted
	 * @param description application description
	 * @return
	 * @throws AppAlreadyExistException
	 * @throws AHEException
	 */
	
	public static AppRegistery createApplication(String appname, String executable, String resource_name, String description) throws AppAlreadyExistException, AHEException{
		
//		AppRegistery app = getApplication(appname);
//		
//		if(app != null){
//			throw new AppAlreadyExistException("Creating Duplicate Applicaton : " + appname);
//		}
		
		Resource r = ResourceRegisterAPI.getResource(resource_name);
		
		if(r == null){
			throw new AHEException("Resource Not found");
		}
		
		AppRegistery app_check = AppRegisteryAPI.getApplication(appname, resource_name);
		
		if(app_check != null){
			throw new AHEException("Application Profile : " + appname + " already exists in Resource: " + resource_name);
		}
		
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        


	        AppRegistery reg = new AppRegistery();
	        
	        reg.setAppname(appname);
	        //reg.setEndpoint(endpoint);
	        reg.setExecutable(executable);
	        //reg.setResource_code(ResourceCode);
	        
	        reg.setResource(r);
	        reg.setDescription(description);
	        reg.setActive(1);
	        reg.setTimestamp(new Date());
	        
	        session.saveOrUpdate(reg);
	        
	        session.flush();
	        txn.commit();
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        return reg;
	        
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
	
    	return null;
    	
	}
	
	/**
	 * Set whether this application is active
	 * @param app_reg_id application id
	 * @param active active
	 * @return AppRegistery
	 */
	
	public static AppRegistery setApplicationAcive(long app_reg_id, boolean active){
		
		AppRegistery app = getApplication(app_reg_id);
	
    	try{
        	
	        Session session = HibernateUtil.getSessionFactory().openSession();

	        
	        Transaction txn = session.beginTransaction();
	        
	        app.setActive(active ? 1 : 0);

	        
	        session.saveOrUpdate(app);
	        
	        session.flush();
	        txn.commit();
	        session.close();
	        
	        //HibernateUtil.getSessionFactory().close();
        
	        return app;
	        
	        
    	}catch (Exception e) { 
            System.out.println(e.getMessage());
            return null;
        } 
		
	}

}
