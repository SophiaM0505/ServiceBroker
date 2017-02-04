package uk.ac.ucl.chem.ccs.AHECore.Hibernate;

import java.io.File;
import java.net.URL;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEJobSubmission;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEUser;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AHEWorkflow;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstance;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstanceArg;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppInstanceCmds;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppRegistery;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.AppRegisteryArgTemplate;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.FileStaging;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.PlatformCredential;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.Resource;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.WorkflowDescription;
import uk.ac.ucl.chem.ccs.AHECore.Hibernate.AHEEntity.WorkflowInstance;


public class HibernateUtil {

	private static final SessionFactory sessionFactory = buildSessionFactory();
	private static ServiceRegistry serviceRegistry;
	
	/**
	 * Build session factory
	 * @return
	 */
	
	private static SessionFactory buildSessionFactory() {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			Configuration c = new Configuration();
			
			c.addAnnotatedClass(AHEWorkflow.class);
			c.addAnnotatedClass(AHEJobSubmission.class);
			c.addAnnotatedClass(AppRegistery.class);
			c.addAnnotatedClass(AppRegisteryArgTemplate.class);
			c.addAnnotatedClass(AppInstance.class);
			c.addAnnotatedClass(AppInstanceArg.class);
			c.addAnnotatedClass(AppInstanceCmds.class);
			c.addAnnotatedClass(FileStaging.class);
			c.addAnnotatedClass(Resource.class);
			c.addAnnotatedClass(AHEUser.class);
			c.addAnnotatedClass(PlatformCredential.class);
			c.addAnnotatedClass(WorkflowDescription.class);
			c.addAnnotatedClass(WorkflowInstance.class);
			
			//File hibUrl = new File("/Users/zeqianmeng/Desktop/AHEhibernate.cfg.xml");
			File hibUrl = new File("/opt/AHE3/AHEhibernate.cfg.xml");
			c.configure(hibUrl);
			//c.configure("/Users/zeqianmeng/Documents/workspace/AHE3/src/main/AHEhibernate.cfg.xml");
			serviceRegistry = new ServiceRegistryBuilder().applySettings(c.getProperties()).buildServiceRegistry();
			
			return c.buildSessionFactory(serviceRegistry);
			
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * Save an entity
	 * @param entity
	 */
	
	public static void Save(Object entity) {

		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();



			session.save(entity);

			session.flush();
			txn.commit();
			session.close();

//			HibernateUtil.getSessionFactory().close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void merge(Object entity) {

		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();

			session.merge(entity);

			session.flush();
			txn.commit();
			session.close();

			//HibernateUtil.getSessionFactory().close();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	/**
	 * Save or update an entity
	 * @param entity
	 */
	
	public static void SaveOrUpdate(Object entity) {

		try {

			Session session = HibernateUtil.getSessionFactory().openSession();

			Transaction txn = session.beginTransaction();

			session.saveOrUpdate(entity);
			session.flush();
			txn.commit();
			session.close();

//			HibernateUtil.getSessionFactory().close();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}
	
	/**
	 * Get session factory
	 * @return
	 */

	
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public static void terminate(){
		sessionFactory.close();
	}

	public static void Delete(long id) {

		try {

			Session session = HibernateUtil.getSessionFactory().openSession();
			WorkflowDescription wd = (WorkflowDescription) session.get(WorkflowDescription.class, id);
			Transaction txn = session.beginTransaction();

			//session.delete("from WorkflowDescription e where e.id = " + workflow_name);
			session.delete(wd);
			session.flush();
			txn.commit();
			session.close();

//			HibernateUtil.getSessionFactory().close();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}
}

