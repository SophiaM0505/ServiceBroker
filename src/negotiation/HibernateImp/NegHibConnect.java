package negotiation.HibernateImp;

import java.sql.SQLException;

import negotiation.HibernateImp.Contract;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class NegHibConnect {
	/*public static void hibNegotiation(long id, String people, String colla, String owner, String provider, String app, String stime, String etime, String nstate){ 
        Configuration cfg = new Configuration();
        cfg.configure("neg.cfg.xml");
 
        @SuppressWarnings("deprecation")
		SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
        Negotiation office = new Negotiation();
        office.setId(id);
        office.setUsername(people);
        office.setAcd_vo_group(colla);
        office.setOwner(owner);
        office.setProvider(provider);
        office.setAppname(app);
        office.setStime(stime);
        office.setDuration(etime);
        office.setNegstate(nstate);

 
        Transaction tx = session.beginTransaction();
        session.save(office);
        System.out.println("Negotiation info saved successfully.....!!");
        tx.commit();
        session.close();
        factory.close();
    }
	*/
	  public static void hibContract(long id, String people, String colla, String owner, String provider, String app, String stime, String duration, String service, String date, String status){ 
        Configuration cfg = new Configuration();
        cfg.configure("con.cfg.xml");
 
        @SuppressWarnings("deprecation")
		SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
        Contract contract = new Contract();
        Service service_s = new Service();
        contract.setId(id);
        contract.setUsername(people);
        contract.setGroupname(colla);
        contract.setOwner(owner);
        service_s.setProvider(provider);
        contract.setAppname(app);
        contract.setStartTime(stime);
        //contract.setDuration(duration);
        //contract.setProvider(provider);
        contract.setService(service_s);
        contract.setContractDate(date);

 
        Transaction tx = session.beginTransaction();
        session.save(contract);
        System.out.println("Contract saved successfully.....!!");
        tx.commit();
        session.close();
        factory.close();
    }
	
	/*public static void hibContract(Offer offer, String date, String status){ 
        Configuration cfg = new Configuration();
        cfg.configure("con.cfg.xml");
 
        @SuppressWarnings("deprecation")
		SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
        Contract contract = new Contract();
        contract.setOffer(offer);
        contract.setDate(date);

 
        Transaction tx = session.beginTransaction();
        session.save(contract);
        System.out.println("Contract saved successfully.....!!");
        tx.commit();
        session.close();
        factory.close();
    }*/
	
	public static void hibOffer(Offer offer){
		Configuration cfg = new Configuration();
        cfg.configure("offer.cfg.xml");
 
        @SuppressWarnings("deprecation")
		SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
		
        Transaction tx = session.beginTransaction();
        session.save(offer);
        System.out.println("Offer with offer constrcut saved successfully.....!!");
        tx.commit();
        session.close();
        factory.close();
	}
	
	public static void hibJob(Job job){
		Configuration cfg = new Configuration();
        cfg.configure("job.cfg.xml");
 
        @SuppressWarnings("deprecation")
		SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
		
        Transaction tx = session.beginTransaction();
        session.save(job);
        System.out.println("Offer with offer constrcut saved successfully.....!!");
        tx.commit();
        session.close();
        factory.close();
	}
	
	public static void hibContract(Contract contract){
		Configuration cfg = new Configuration();
        cfg.configure("con.cfg.xml");
 
        @SuppressWarnings("deprecation")
		SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
		
        Transaction tx = session.beginTransaction();
        session.save(contract);
        System.out.println("Contract saved successfully.....!!");
        tx.commit();
        session.close();
        factory.close();
	}
	
	public static void hibConInst(ContractInst info){
		Configuration cfg = new Configuration();
        cfg.configure("coninst.cfg.xml");
 
        @SuppressWarnings("deprecation")
		SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
		
        Transaction tx = session.beginTransaction();
        session.save(info);
        //Helper.writeout("Contract and Instance info saved successfully 1.....!!");
        System.out.println("Contract and Instance info saved successfully.....!!");
        tx.commit();
        session.close();
        factory.close();
	}
	
	/*public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		 //negDelete(6);
		Offer offer = new Offer();
		offer.setApp("water");
		offer.setColla("group");
		offer.setDuration("5");
		offer.setId(555555);
		offer.setOwner("MoU");
		offer.setPeople("manGroup");
		offer.setProvider("EGI");
		offer.setService("unicore");
		offer.setStime("2015");
		hibOffer(offer);
		 //System.out.println(getOffer(1).getService());
		 System.out.println("done");
	 }*/

}
