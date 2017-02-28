package negotiation.Database;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import test.helper.Executable;
import test.helper.Helper;

import negotiation.HibernateImp.Contract;
import negotiation.HibernateImp.Offer;
import negotiation.HibernateImp.Service;
import negotiation.Negotiation.NegState;
//import bitronix.tm.utils.Service;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class NegotiationDB {
	
	private static Connection connection;
	//connnect db
	public static void connectdb() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		//Connection con;
		//Statement sta;
		 
		String url = "jdbc:mysql://localhost/ahecontract";
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		connection = (Connection) DriverManager.getConnection(url, "root", "sophia");
	}
	
	 //public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
	 public static String getNegState(long cont) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		String state = null;
		connectdb();
		Statement sta1 = (Statement) connection.createStatement();
		//String user = "Junyi";
		//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
		ResultSet rs = sta1.executeQuery("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.ID = '" + cont + "';");
		while (rs.next())
	      {
	        //long id = rs.getLong("ID");
	        //String people = rs.getString("PEOPLE");
	        //String colla = rs.getString("COLLA");
	        //String app = rs.getString("APP");
			state = rs.getString("NEGSTATE");
	         
	        // print the results
	        System.out.format("%s \n", state);
	      }
		//System.out.println(re.getString(10).toString());
		sta1.close();
		return state;
	 }
	 
	 public static String getOfferStatus(long offer_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			String state = null;
			connectdb();
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM OFFER WHERE OFFER.ID = '" + offer_id + "';");
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				state = rs.getString("STATUS");
		         
		        // print the results
		        System.out.format("%s \n", state);
		      }
			//System.out.println(re.getString(10).toString());
			sta1.close();
			return state;
		 }
	 
	 public static String getOfferSub(long offer_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			String sub = "a";
			connectdb();
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM OFFER WHERE OFFER.ID = '" + offer_id + "';");
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				sub = rs.getString("SUB");
		         
		        // print the results
		        System.out.format("%s \n", sub);
		      }
			//System.out.println(re.getString(10).toString());
			sta1.close();
			return sub;
		 }
	 
	 public static int getContractSub(long contract_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			int sub = 5;
			connectdb();
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM CONTRACT WHERE CONTRACT.ID = '" + contract_id + "';");
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				sub = rs.getInt("SUB");
		         
		        // print the results
		        System.out.format("%s \n", sub);
		      }
			//System.out.println(re.getString(10).toString());
			sta1.close();
			return sub;
		 }
	 
	 public static String getContractStatus(long contract_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			String state = null;
			connectdb();
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM CONTRACT WHERE CONTRACT.ID = '" + contract_id + "';");
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				state = rs.getString("STATUS");
		         
		        // print the results
		        System.out.format("%s \n", state);
		      }
			//System.out.println(re.getString(10).toString());
			sta1.close();
			return state;
		 }
	 
	 public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		 String current_state = NegotiationDB.getContractStatus(9189);
		 System.out.println("current state is: " + current_state);
	 }
	 
	 public static String getStopApi(long contract_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			String endpoint = null;
			connectdb();
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM CONTRACT WHERE CONTRACT.ID = '" + contract_id + "';");
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				endpoint = rs.getString("endpoint");
		         
		        // print the results
		        System.out.format("%s \n", endpoint);
		      }
			//System.out.println(re.getString(10).toString());
			sta1.close();
			return endpoint;
		 }
	 
	 //public static String provider;
	 public static String getContProvider(long cont) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		 //System.out.println("*************** 1");	
		 String provider = null;
			connectdb();
			//System.out.println("*************** 2");	
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM CONTRACT WHERE CONTRACT.ID = '" + cont + "';");
			//System.out.println("*************** 3");	
			//boolean loop = true;
			//while(provider.equals("initial")){
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				provider = rs.getString("provider");
		        //loop = false; 
		        // print the results
		        //System.out.format("%s \n", provider);
		      }
			//}
			//System.out.println("if rs is null: " + rs.wasNull());
			sta1.close();
			connection.close();
			return provider;
	 }
	 
	 public static String[] getContProMax(long cont) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		 //System.out.println("*************** 1");	
		 String[] data = new String[4];
		 //String provider = null;
			connectdb();
			//System.out.println("*************** 2");	
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM CONTRACT WHERE CONTRACT.ID = '" + cont + "';");
			//System.out.println("*************** 3");	
			//boolean loop = true;
			//while(provider.equals("initial")){
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				data[0] = rs.getString("provider");
				data[1] = String.valueOf(rs.getBoolean("virtual"));
				data[2] = String.valueOf(rs.getDouble("maxCost"));
				data[3] = String.valueOf(rs.getLong("maxDuration"));
		        //loop = false; 
		        // print the results
		        //System.out.format("%s \n", provider);
		      }
			//}
			//System.out.println("if rs is null: " + rs.wasNull());
			sta1.close();
			connection.close();
			return data;
	 }
	 
	 public static String[] getContCloudRed(long cont) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		 //System.out.println("*************** 1");	
		 String[] data = new String[3];
		 String workerN = null;
		 String share = null;
		 double cost = 0;
			connectdb();
			//System.out.println("*************** 2");	
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM CONTRACT WHERE CONTRACT.ID = '" + cont + "';");
			//System.out.println("*************** 3");	
			//boolean loop = true;
			//while(provider.equals("initial")){
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				workerN = rs.getString("worker");
				cost = rs.getDouble("cost");
				share = rs.getString("share");
		        //loop = false; 
		        // print the results
		        //System.out.format("%s \n", provider);
		      }
			//}
			//System.out.println("if rs is null: " + rs.wasNull());
			data[0] = workerN;
			data[1] = String.valueOf(cost);
			data[2] = share;
			sta1.close();
			connection.close();
			return data;
	 }
	 
	 
	 
	 public static Map<String,String> getPaymentInfo(long con_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			//String state = null;
			connectdb();
			/*Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs1 = sta1.executeQuery("SELECT CON_ID FROM CONINST WHERE INST_ID = '" + inst + "';");
			//double cost = rs.getDouble("cost");
			long con_id = 0;
			while (rs1.next())
		      {

				con_id = rs1.getLong("con_id");
				System.out.println("the contract id from db is: " + con_id);
		        // print the results
		        //System.out.format("%s \n", state);
		      }*/
			double charge = 0;
			String user;
            String stime;
            String worker;
            String share;
            String measurement;
            double maxCost;
            long maxTotalCpuT;
            String provider;
            int requiredCpuN;
            
			Map<String, String> values = new HashMap<String, String>();
			
			Statement sta2 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs2 = sta2.executeQuery("SELECT * FROM CONTRACT WHERE CONTRACT.ID = '" + con_id + "';");
			
			while (rs2.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				charge = rs2.getDouble("charge");
				user = rs2.getString("username");
				stime = rs2.getString("startTime");
				worker = rs2.getString("worker");
				share = rs2.getString("share");
				measurement = rs2.getString("measurement");
				maxCost = rs2.getDouble("maxCost");
				maxTotalCpuT = rs2.getLong("maxTotalCpuT");
				provider = rs2.getString("provider");
				requiredCpuN = rs2.getInt("requiredCpuNo");
				//state = rs.getString("NEGSTATE");
				values.put("username", user);
				values.put("charge", String.valueOf(charge));
				values.put("startTime", stime);
				values.put("worker", worker);
				values.put("share", share);
				values.put("measurement", measurement);
				values.put("maxCost", String.valueOf(maxCost));
				values.put("maxTotalCpuT", String.valueOf(maxTotalCpuT));
				values.put("provider", provider);
				values.put("requiredCpuN", String.valueOf(requiredCpuN));
				System.out.println("the user name from db is: " + user);
		        // print the results
		        //System.out.format("%s \n", state);
		      }
			//System.out.println(re.getString(10).toString());
			sta2.close();
			return values;
		 }
	 
	 //public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
	 // ****** this function should check: if the state is AcceptAck, it should generate an entry in contract table
	 public static void updateContractState(long cont, NegState state) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		connectdb();
		String s = state.toString();
		//Statement sta = (Statement) connection.createStatement();
		String query = "UPDATE CONTRACT SET status = '"+ s + "' WHERE ID = '" + cont + "';";
		System.out.println(query);
		PreparedStatement preparedStmt = connection.prepareStatement(query);
	    //preparedStmt.setString(1, NegState.AccessProving.toString());
	 
	      // execute the java preparedstatement
	    preparedStmt.executeUpdate();
	    connection.close();
		
		
	 }
	 
	 public static void setRenegTag(long job_id, int tag) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			//String s = state.toString();
			//Statement sta = (Statement) connection.createStatement();
			String query = "UPDATE JobContract SET tag = '"+ tag +"' WHERE Job_id = '" + job_id + "';";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(1, NegState.AccessProving.toString());
		 
		      // execute the java preparedstatement
		    preparedStmt.executeUpdate();
		    connection.close();
			
			
		 }
	 
	 public static int getRenegTag(long job_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			//String s = state.toString();
			int tag = 0;
			//Statement sta = (Statement) connection.createStatement();
			Statement sta2 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs2 = sta2.executeQuery("SELECT * FROM JobContract WHERE job_id = '" + job_id + "';");
			
			while (rs2.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				tag = rs2.getInt("tag");
				//state = rs.getString("NEGSTATE");

		        // print the results
		        //System.out.format("%s \n", state);
		      }
			//System.out.println(re.getString(10).toString());
			sta2.close();
			return tag;		
		 }
	 
	/* public static long getContractIdfromJobId(long job_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			//String s = state.toString();
			long contract_id = 0;
			//Statement sta = (Statement) connection.createStatement();
			Statement sta2 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs2 = sta2.executeQuery("SELECT * FROM JobContract WHERE job_id = '" + job_id + "';");
			
			while (rs2.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				contract_id = rs2.getInt("contract_id");
				//state = rs.getString("NEGSTATE");

		        // print the results
		        //System.out.format("%s \n", state);
		      }
			//System.out.println(re.getString(10).toString());
			sta2.close();
			return contract_id;
			
			
		 }*/
	 
	 public static String[] getRenegMeta(long contract_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			//String s = state.toString();
			String[] meta = new String[4];

			//Statement sta = (Statement) connection.createStatement();
			Statement sta2 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs2 = sta2.executeQuery("SELECT * FROM CONTRACT WHERE ID = '" + contract_id + "';");
			
			while (rs2.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				//meta[0] = Integer.toString(rs2.getInt("tag"));
				meta[0] = rs2.getString("status");
				meta[1] = rs2.getString("endpoint");
				meta[2] = rs2.getString("worker");
				meta[3] = Integer.toString(rs2.getInt("requiredCpuN"));
				//state = rs.getString("NEGSTATE");

		        // print the results
		        //System.out.format("%s \n", state);
		      }
			//System.out.println(re.getString(10).toString());
			sta2.close();
			return meta;
			
			
		 }
	 
	 public static String[] getRedQueenSubmission(long contract_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			//String s = state.toString();
			String[] meta = new String[3];

			//Statement sta = (Statement) connection.createStatement();
			Statement sta2 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs2 = sta2.executeQuery("SELECT * FROM CONTRACT WHERE ID = '" + contract_id + "';");
			
			while (rs2.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				//meta[0] = Integer.toString(rs2.getInt("tag"));
				meta[0] = rs2.getString("numjobs");
				meta[1] = rs2.getString("nefold");
				meta[2] = rs2.getString("level");				

		        // print the results
		        //System.out.format("%s \n", state);
		      }
			//System.out.println(re.getString(10).toString());
			sta2.close();
			return meta;			
		 }
	 
	 public static String[] getStopMeta(long contract_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			//String s = state.toString();
			String[] meta = new String[2];

			//Statement sta = (Statement) connection.createStatement();
			Statement sta2 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs2 = sta2.executeQuery("SELECT * FROM Contract WHERE ID = '" + contract_id + "';");
			
			while (rs2.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				//meta[0] = Integer.toString(rs2.getInt("tag"));
				meta[0] = rs2.getString("status");
				meta[1] = rs2.getString("worker");
				//state = rs.getString("NEGSTATE");

		        // print the results
		        //System.out.format("%s \n", state);
		      }
			//System.out.println(re.getString(10).toString());
			sta2.close();
			return meta;
			
			
		 }
	 
	 public static void insertJobId(long cont, long job_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			//String s = state.toString();
			//Statement sta = (Statement) connection.createStatement();
			String query = "UPDATE CONTRACT SET jobId = '"+ job_id +"' WHERE ID = '" + cont + "';";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(1, NegState.AccessProving.toString());
		 
		      // execute the java preparedstatement
		    preparedStmt.executeUpdate();
		    connection.close();
			
			
		 }
	 
	 public static void insertBrokerJobId(long broker_job_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			//String s = state.toString();
			//Statement sta = (Statement) connection.createStatement();
			String query = "INSERT INTO JobIds (broker_job_id, redqueen_job_id)" + "VALUES ('" + broker_job_id + "', '" + 0 + "')";
			//String query = "INSERT INTO JobContract (job_id, contract_id, tag)" + "VALUES ('" + job_id + "', '" + contract + "', '" + tag + "')";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(neg_id, offers);
		 
		      // execute the java preparedstatement
		    preparedStmt.executeUpdate();
		    connection.close();			
			
			
		 }
	 
	 public static void insertRedqueenJobId(long broker_job_id, long redqueen_job_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			//String s = state.toString();
			//Statement sta = (Statement) connection.createStatement();
			String query = "UPDATE JobIds SET redqueen_job_id = '" + redqueen_job_id + "' WHERE broker_job_id = '" + broker_job_id + "';";
			//String query = "INSERT INTO JobContract (job_id, contract_id, tag)" + "VALUES ('" + job_id + "', '" + contract + "', '" + tag + "')";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(neg_id, offers);
		 
		      // execute the java preparedstatement
		    preparedStmt.executeUpdate();
		    connection.close();					
		 }
	 
	 public static long getRedqueenJobId(long broker_job_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			//String s = state.toString();
			//Statement sta = (Statement) connection.createStatement();
			long redqueen_job_id = 0;
			Statement sta = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta.executeQuery("SELECT * FROM JobIds WHERE broker_job_id = '" + broker_job_id + "';");
			
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				//meta[0] = Integer.toString(rs2.getInt("tag"));
				redqueen_job_id = rs.getLong("redqueen_job_id");
				//state = rs.getString("NEGSTATE");

		        // print the results
		        //System.out.format("%s \n", state);
		      }
			//System.out.println(re.getString(10).toString());
			sta.close();
			return redqueen_job_id;				
		 }
	 
	 
	 public static void updateContractState(long cont, NegState state, long duration, String end_time) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			String s = state.toString();
			String d = String.valueOf(duration);
			//Statement sta = (Statement) connection.createStatement();
			String query = "UPDATE CONTRACT SET status = '"+ s + "', duration = '"+ d + "', endtime = '"+ end_time + "' WHERE ID = '" + cont + "';";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(1, NegState.AccessProving.toString());
		 
		      // execute the java preparedstatement
		    preparedStmt.executeUpdate();
		    connection.close();			
			
		 }
	 
	 public static void updateContractStartT(long cont, String start_time) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			
			//Statement sta = (Statement) connection.createStatement();
			String query = "UPDATE CONTRACT SET startTime = '"+ start_time + "' WHERE ID = '" + cont + "';";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(1, NegState.AccessProving.toString());
		 
		      // execute the java preparedstatement
		    preparedStmt.executeUpdate();
		    connection.close();			
			
		 }
	 
	 public static void updateContractJob(long cont, NegState state, long job_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			String s = state.toString();
			//String d = String.valueOf(duration);
			//Statement sta = (Statement) connection.createStatement();
			String query = "UPDATE CONTRACT SET status = '"+ s + "', jobId = '"+ job_id + "' WHERE ID = '" + cont + "';";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(1, NegState.AccessProving.toString());
		 
		      // execute the java preparedstatement
		    preparedStmt.executeUpdate();
		    connection.close();			
			
		 }
	 
	 public static void insertNeg(long neg_id, String offers) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();

			//Statement sta = (Statement) connection.createStatement();
			String query = "INSERT INTO NegOffers (Neg_id, offers)" + "VALUES ('" + neg_id + "', '" + offers + "')";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(neg_id, offers);
		 
		      // execute the java preparedstatement
		    preparedStmt.executeUpdate();
		    connection.close();			
			
		 }
	 
	 
	 public static void insertJob(long job_id, long contract, int tag) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();

			//Statement sta = (Statement) connection.createStatement();
			String query = "INSERT INTO JobContract (job_id, contract_id, tag)" + "VALUES ('" + job_id + "', '" + contract + "', '" + tag + "')";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(neg_id, offers);
		 
		      // execute the java preparedstatement
		    preparedStmt.executeUpdate();
		    connection.close();			
			
		 }
	 
	 public static long getConFromJob(long job_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			long contract_id = 0;

			//Statement sta = (Statement) connection.createStatement();
			String query = "SELECT * FROM JobContract WHERE job_id = '" + job_id + "';";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(neg_id, offers);
			ResultSet rs2 = preparedStmt.executeQuery(query);
		 
			while (rs2.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				contract_id = rs2.getInt("contract_id");
				//state = rs.getString("NEGSTATE");

		        // print the results
		        //System.out.format("%s \n", state);
		      }
			//System.out.println(re.getString(10).toString());
			preparedStmt.close();
			return contract_id;
			
		 }
	 
	 /*public static long getOldContract(long job_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		 //System.out.println("*************** 1");	
		 long contract_id = 0;
			connectdb();
			//System.out.println("*************** 2");	
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM JobContract WHERE job_id = '" + job_id + "';");
			//System.out.println("*************** 3");	
			//boolean loop = true;
			//while(provider.equals("initial")){
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				contract_id = rs.getLong("contractId");
		        //loop = false; 
		        // print the results
		        //System.out.format("%s \n", provider);
		      }
			//}
			//System.out.println("if rs is null: " + rs.wasNull());
			sta1.close();
			connection.close();
			return contract_id;
	 }*/
	 
	 public static long getJobId(long contract_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		 //System.out.println("*************** 1");	
		 long job_id = 0;
			connectdb();
			//System.out.println("*************** 2");	
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM CONTRACT WHERE id = '" + contract_id + "';");
			//System.out.println("*************** 3");	
			//boolean loop = true;
			//while(provider.equals("initial")){
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				job_id = rs.getLong("jobId");
		        //loop = false; 
		        // print the results
		        //System.out.format("%s \n", provider);
		      }
			//}
			//System.out.println("if rs is null: " + rs.wasNull());
			sta1.close();
			connection.close();
			return job_id;
	 }
	 
	 public static String getDeadline(long contract_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		 //System.out.println("*************** 1");	
		 String deadline = null;
			connectdb();
			//System.out.println("*************** 2");	
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM CONTRACT WHERE id = '" + contract_id + "';");
			//System.out.println("*************** 3");	
			//boolean loop = true;
			//while(provider.equals("initial")){
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				deadline = rs.getString("deadline");
		        //loop = false; 
		        // print the results
		        //System.out.format("%s \n", provider);
		      }
			//}
			//System.out.println("if rs is null: " + rs.wasNull());
			sta1.close();
			connection.close();
			return deadline;
	 }
	 
	 public static int getOverPri(long contract_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		 //System.out.println("*************** 1");	
		 int pri = 5;
			connectdb();
			//System.out.println("*************** 2");	
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM CONTRACT WHERE id = '" + contract_id + "';");
			//System.out.println("*************** 3");	
			//boolean loop = true;
			//while(provider.equals("initial")){
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				pri = rs.getInt("overPrivilege");
		        //loop = false; 
		        // print the results
		        //System.out.format("%s \n", provider);
		      }
			//}
			//System.out.println("if rs is null: " + rs.wasNull());
			sta1.close();
			connection.close();
			return pri;
	 }
	 
	 public static String getEndpoint(long contract_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		 //System.out.println("*************** 1");	
		 String endpoint = "";
			connectdb();
			//System.out.println("*************** 2");	
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM CONTRACT WHERE id = '" + contract_id + "';");
			//System.out.println("*************** 3");	
			//boolean loop = true;
			//while(provider.equals("initial")){
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				endpoint = rs.getString("endpoint");
		        //loop = false; 
		        // print the results
		        //System.out.format("%s \n", provider);
		      }
			//}
			//System.out.println("if rs is null: " + rs.wasNull());
			sta1.close();
			connection.close();
			return endpoint;
	 }
	 
	 public static long getOldContract(long job_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		 //System.out.println("*************** 1");	
		 long contract_id = 0;
			connectdb();
			//System.out.println("*************** 2");	
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM JobContract WHERE job_id = '" + job_id + "';");
			//System.out.println("*************** 3");	
			//boolean loop = true;
			//while(provider.equals("initial")){
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				contract_id = rs.getLong("contract_id");
		        //loop = false; 
		        // print the results
		        //System.out.format("%s \n", provider);
		      }
			//}
			//System.out.println("if rs is null: " + rs.wasNull());
			sta1.close();
			connection.close();
			return contract_id;
	 }
	 
	 public static String[] getSteererInfo(long contract_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		 //System.out.println("*************** 1");	
		 //long job_id = 0;
		 String worker = null;
		 int required_core = 0;
		 String endPoint = null;
		 String[] results = new String[3];
			connectdb();
			//System.out.println("*************** 2");	
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM CONTRACT WHERE ID = '" + contract_id + "';");
			//System.out.println("*************** 3");	
			//boolean loop = true;
			//while(provider.equals("initial")){
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				//job_id = rs.getLong("jobId");
				worker = rs.getString("worker");
				endPoint = rs.getString("endPoint");
				required_core = rs.getInt("requiredCpuNo");
		        //loop = false; 
		        // print the results
		        //System.out.format("%s \n", provider);
		      }
			//}
			//System.out.println("if rs is null: " + rs.wasNull());
			//results[0] = String.valueOf(job_id);
			results[0] = worker;
			results[1] = endPoint;
			results[2] = String.valueOf(required_core);
			sta1.close();
			connection.close();
			return results;
	 }
	 
	 /*public static String[] getOldContract(long job_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		 //System.out.println("*************** 1");	
		 String[] job_contract = new String[3];
		 long contract_id = 0;
		 String old_node = null;
		 String new_node = null;
			connectdb();
			//System.out.println("*************** 2");	
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM JobContract WHERE job_id = '" + job_id + "';");
			//System.out.println("*************** 3");	
			//boolean loop = true;
			//while(provider.equals("initial")){
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				contract_id = rs.getLong("contractId");
				old_node = rs.getString("oldNode");
				new_node = rs.getString("newNode");
		        //loop = false; 
		        // print the results
		        //System.out.format("%s \n", provider);
		      }
			//}
			//System.out.println("if rs is null: " + rs.wasNull());
			job_contract[0] = Long.toString(contract_id);
			job_contract[1] = old_node;
			job_contract[2] = new_node;
			sta1.close();
			connection.close();
			return job_contract;
	 }*/
	 
	 public static String getNegOffers(long neg_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		 //System.out.println("*************** 1");	
		 String offers = null;
			connectdb();
			//System.out.println("*************** 2");	
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM NegOffers WHERE neg_id = '" + neg_id + "';");
			//System.out.println("*************** 3");	
			//boolean loop = true;
			//while(provider.equals("initial")){
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				offers = rs.getString("offers");
		        //loop = false; 
		        // print the results
		        //System.out.format("%s \n", provider);
		      }
			//}
			//System.out.println("if rs is null: " + rs.wasNull());
			sta1.close();
			connection.close();
			return offers;
	 }
	 
	 public static long getNegID(long offer_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		 //System.out.println("*************** 1");	
		    long neg_id = 0;
			connectdb();
			//System.out.println("*************** 2");	
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM OFFER WHERE ID = '" + offer_id + "';");
			//System.out.println("*************** 3");	
			//boolean loop = true;
			//while(provider.equals("initial")){
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				neg_id = rs.getLong("negId");
		        //loop = false; 
		        // print the results
		        //System.out.format("%s \n", provider);
		      }
			//}
			//System.out.println("if rs is null: " + rs.wasNull());
			sta1.close();
			connection.close();
			return neg_id;
	 }
	 
	 
	 
	 public static void updateContractStateEndT(long cont, NegState state, String end_time) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			String s = state.toString();
			//String d = String.valueOf(duration);
			//Statement sta = (Statement) connection.createStatement();
			String query = "UPDATE CONTRACT SET status = '"+ s + "', endtime = '"+ end_time + "' WHERE ID = '" + cont + "';";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(1, NegState.AccessProving.toString());
		 
		      // execute the java preparedstatement
		    preparedStmt.executeUpdate();
		    connection.close();			
			
		 }
	 
	 public static String updateContractStateComp(long cont, NegState state, String end_time) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			String endPoint = null;
			String s = state.toString();
			//String d = String.valueOf(duration);
			//Statement sta = (Statement) connection.createStatement();
			String query = "UPDATE CONTRACT SET status = '"+ s + "', endtime = '"+ end_time + "' WHERE ID = '" + cont + "';";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(1, NegState.AccessProving.toString());
		 
		      // execute the java preparedstatement
		    preparedStmt.executeUpdate();
		    
		    Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM CONTRACT WHERE ID = '" + cont + "';");
			//System.out.println("*************** 3");	
			//boolean loop = true;
			//while(provider.equals("initial")){
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				endPoint = rs.getString("endPoint");
		        //loop = false; 
		        // print the results
		        //System.out.format("%s \n", provider);
		      }
			//}
			//System.out.println("if rs is null: " + rs.wasNull());
			sta1.close();
		    connection.close();
		    return endPoint;
			
		 }
	 
	 public static void updateContractStateTermi(long cont, NegState state, String end_time) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			String s = state.toString();
			//String d = String.valueOf(duration);
			//Statement sta = (Statement) connection.createStatement();
			String query = "UPDATE CONTRACT SET status = '"+ s + "', endtime = '"+ end_time + "' WHERE ID = '" + cont + "';";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(1, NegState.AccessProving.toString());
		 
		      // execute the java preparedstatement
		    preparedStmt.executeUpdate();
		    connection.close();
			
		 }
	 
	 
	 public static void updateOfferState(long offer_id, NegState state) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			String s = state.toString();
			//Statement sta = (Statement) connection.createStatement();
			String query = "UPDATE OFFER SET status = '"+ s + "' WHERE ID = '" + offer_id + "';";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(1, NegState.AccessProving.toString());
		 
		      // execute the java preparedstatement
		    preparedStmt.executeUpdate();
		    connection.close();
			
			
		 }
	 
	 public static void updateOfferState(long offer_id, NegState state, long duration, String end_time) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			String s = state.toString();
			String d = String.valueOf(duration);
			//Statement sta = (Statement) connection.createStatement();
			String query = "UPDATE OFFER SET status = '"+ s + "', duration = '"+ d + "', endtime = '"+ end_time + "' WHERE ID = '" + offer_id + "';";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(1, NegState.AccessProving.toString());
		 
		      // execute the java preparedstatement
		    preparedStmt.executeUpdate();
		    connection.close();		
			
		 }
	 
	 public static void updateOfferJob(long offer_id, NegState state, long job_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			String s = state.toString();
			//String d = String.valueOf(duration);
			//Statement sta = (Statement) connection.createStatement();
			String query = "UPDATE OFFER SET status = '"+ s + "', jobId = '"+ job_id + "' WHERE ID = '" + offer_id + "';";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(1, NegState.AccessProving.toString());
		 
		      // execute the java preparedstatement
		    preparedStmt.executeUpdate();
		    connection.close();			
			
		 }
	 
	 public static void updateOfferState(long offer_id, NegState state, String end_time) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			connectdb();
			String s = state.toString();
			//String d = String.valueOf(duration);
			//Statement sta = (Statement) connection.createStatement();
			String query = "UPDATE OFFER SET status = '"+ s + "', endtime = '"+ end_time + "' WHERE ID = '" + offer_id + "';";
			System.out.println(query);
			PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(1, NegState.AccessProving.toString());
		 
		      // execute the java preparedstatement
		    preparedStmt.executeUpdate();
		    connection.close();		
			
		 }
	 
	 public static boolean contSearch(long cont) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		 connectdb();
		 boolean result = false;
			//String user = "Junyi";
		    Statement sta = (Statement) connection.createStatement();
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			String query = "SELECT * FROM contract WHERE contract.ID = '" + cont + "';";
			ResultSet rs = sta.executeQuery(query);
			if (!rs.wasNull()){
				result = true;
			}
			else {
				result = false;
			}
			//PreparedStatement preparedStmt = connection.prepareStatement(query);
		    //preparedStmt.setString(1, NegState.AccessProving.toString());
		 
		      // execute the java preparedstatement
		    //preparedStmt.executeUpdate();
		       
		    connection.close();
			return result;
			//System.out.println(re.getString(10).toString());		
		 
	 }
	 
	 public static void contDelete(long cont) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		 connectdb();
			//Statement sta3 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
            String query = "DELETE FROM CONTRACT WHERE ID = '" + cont + "';";
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.executeUpdate();
            connection.close();
		    //sta3.executeQuery("DELETE * FROM contract WHERE ID = '" + cont + "';");
			//sta3.close();
		 
	 }
	 
	 public static void negDelete(long id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		 connectdb();
			//Statement sta4 = (Statement) connection.createStatement();
			
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
            //sta4.executeQuery("DELETE * FROM NEGOTIATION WHERE ID = '" + id + "';");
            String query = "DELETE FROM NEGOTIATION WHERE ID = '" + id + "';";
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.executeUpdate();
            connection.close();
			//sta4.close();
		 
	 }
	 
	 
	 public static HashMap<String, String> getOntValues(long id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		 HashMap<String, String> paras = new HashMap<String, String>();
		 //paras = new String[6];
		 connectdb();
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.ID = '" + id + "';");
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
				paras.put("people", rs.getString("people"));
				paras.put("colla", rs.getString("colla"));
				paras.put("app", rs.getString("app"));
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				//state = rs.getString("NEGSTATE");
		         
		        // print the results
		        //System.out.format("%s \n", state);
		      }
			
			//System.out.println(re.getString(10).toString());
			sta1.close();
			connection.close();
			return paras;
		 
	 }
	 
	 public static HashMap<String, String> getNegValues(long id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		 HashMap<String, String> paras = new HashMap<String, String>();
		 //paras = new String[6];
		 connectdb();
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.ID = '" + id + "';");
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
				paras.put("people", rs.getString("people"));
				paras.put("colla", rs.getString("colla"));
				paras.put("owner", rs.getString("owner"));
				paras.put("provider", rs.getString("provider"));
				paras.put("app", rs.getString("app"));
				paras.put("stime", rs.getString("stime"));
				paras.put("etime", rs.getString("etime"));
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				//state = rs.getString("NEGSTATE");
		         
		        // print the results
		        //System.out.format("%s \n", state);
		      }
			
			//System.out.println(re.getString(10).toString());
			sta1.close();
			connection.close();
			return paras;
		 
	 }
	 
	 public static HashMap<String, String> getConValues(long id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		 HashMap<String, String> paras = new HashMap<String, String>();
		 //paras = new String[6];
		 connectdb();
			Statement sta = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta.executeQuery("SELECT * FROM CONTRACT WHERE CONTRACT.ID = '" + id + "';");
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
				paras.put("groupname", rs.getString("groupname"));
				paras.put("username", rs.getString("username"));
				paras.put("owner", rs.getString("owner"));
				paras.put("provider", rs.getString("provider"));
				paras.put("appname", rs.getString("appname"));
				paras.put("stime", rs.getString("stime"));
				paras.put("etime", rs.getString("etime"));
				paras.put("date", rs.getString("date"));
				paras.put("owner", rs.getString("owner"));
				paras.put("service_name", rs.getString("service_name"));
				paras.put("charge", rs.getString("charge"));
				paras.put("cpuNo", rs.getString("cpuNo"));
				paras.put("instance", rs.getString("instace"));
				paras.put("memory", rs.getString("memory"));
				paras.put("provider", rs.getString("provider"));
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				//state = rs.getString("NEGSTATE");
		         
		        // print the results
		        //System.out.format("%s \n", state);
		      }
			
			//System.out.println(re.getString(10).toString());
			sta.close();
			connection.close();
			return paras;
		 
	 }
	 
	 public static HashMap<String, String> getOfferValues(long id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		 HashMap<String, String> paras = new HashMap<String, String>();
		 //paras = new String[6];
		 connectdb();
			Statement sta = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta.executeQuery("SELECT * FROM OFFER WHERE OFFER.ID = '" + id + "';");
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
				paras.put("id", String.valueOf(rs.getLong("ID")));
				paras.put("groupname", rs.getString("groupname"));
				paras.put("username", rs.getString("username"));
				paras.put("owner", rs.getString("owner"));
				paras.put("provider", rs.getString("provider"));
				paras.put("appname", rs.getString("name"));
				paras.put("duration", rs.getString("duration"));
				paras.put("service_name", rs.getString("service_name"));
				paras.put("stime", rs.getString("stime"));
				paras.put("charge", rs.getString("charge"));
				paras.put("cost", rs.getString("cost"));
				paras.put("cpuNo", rs.getString("cpuNo"));
				paras.put("instance", rs.getString("instace"));
				paras.put("memory", rs.getString("memory"));
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				//state = rs.getString("NEGSTATE");
		         
		        // print the results
		        //System.out.format("%s \n", state);
		      }
			
			//System.out.println(re.getString(10).toString());
			sta.close();
			connection.close();
			return paras;
		 
	 }
	 
	 /*public static Offer getOffer(long id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		 //HashMap<String, String> paras = new HashMap<String, String>();
		 Offer offer = new Offer();
		 //paras = new String[6];
		 connectdb();
			Statement sta = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta.executeQuery("SELECT * FROM offer WHERE offer.ID = '" + id + "';");
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
				offer.setPeople(rs.getString("people"));
				offer.setColla(rs.getString("colla"));
				offer.setOwner(rs.getString("owner"));
				offer.setProvider(rs.getString("provider"));
				offer.setApp(rs.getString("app"));
				offer.setDuration(rs.getString("duration"));
				offer.setServiceId(rs.getLong("service"));
				offer.setStime(rs.getString("stime"));
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				//state = rs.getString("NEGSTATE");
		         
		        // print the results
		        //System.out.format("%s \n", state);
		      }
			
			//System.out.println(re.getString(10).toString());
			sta.close();
			return offer;
		 
	 }*/
	 
	 public static Contract offerToContract(long id, String date) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		//Offer offer = new Offer();
		 //paras = new String[6];
		connectdb();
		Statement sta = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
		ResultSet rs = sta.executeQuery("SELECT * FROM OFFER WHERE OFFER.ID = '" + id + "';");
		Contract contract = new Contract();
		Service service = new Service();
		
		while (rs.next())
	      {
			contract.setGroupname(rs.getString("groupname"));
			contract.setAppname(rs.getString("appname"));
			//contract.(rs.getString("duration"));
			contract.setId(Long.parseLong(rs.getString("id")));
			contract.setOwner(rs.getString("owner"));
			//contract.setStartTime(date);
			contract.setUsername(rs.getString("username"));
			//contract.setContractDate(date);
			contract.setWorker(rs.getString("worker"));
			contract.setShare(rs.getString("share"));
			contract.setMaxCost(Float.parseFloat(rs.getString("maxCost")));
			contract.setMaxTotalCpuT(Long.parseLong(rs.getString("maxTotalCpuT")));
			contract.setMaxDuration(Long.parseLong(rs.getString("maxDuration")));
			contract.setRequiredCpuNo(Integer.parseInt(rs.getString("requiredCpuNo")));
			contract.setSub(rs.getString("sub"));
			contract.setUserBalance(Float.parseFloat(rs.getString("userBalance")));
			contract.setEndpoint(rs.getString("endpoint"));
			contract.setNumjobs(Integer.parseInt(rs.getString("numjobs")));
			contract.setNefold(Integer.parseInt(rs.getString("nefold")));
			contract.setDeadline(rs.getString("deadline"));
			contract.setLevel(Integer.parseInt(rs.getString("level")));
			
			service.setCharge(Float.parseFloat(rs.getString("charge")));
			service.setCost(Float.parseFloat(rs.getString("cost")));
			service.setCpuNo(Integer.parseInt(rs.getString("cpuNo")));
			service.setMemory(Integer.parseInt(rs.getString("memory")));
			service.setInstance(rs.getString("instance"));
			service.setProvider(rs.getString("provider"));
			service.setMeasurement(rs.getString("measurement"));
		
	
			//service.setService_name(rs.getString("service_name"));
	      }
		contract.setService(service);
		sta.close();
		connection.close();
		return contract;
	 }
	 
	 public static Contract combOfferToContract(long id, String date) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			//Offer offer = new Offer();
			 //paras = new String[6];
			connectdb();
			Statement sta = (Statement) connection.createStatement();
				//String user = "Junyi";
				//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta.executeQuery("SELECT * FROM OFFER WHERE OFFER.ID = '" + id + "';");
			Contract contract = new Contract();
			while (rs.next())
		      {
				contract.setId(Long.parseLong(rs.getString("id")));
				contract.setSub(rs.getString("sub"));
				contract.setMaxDuration(Long.parseLong(rs.getString("maxDuration")));
		      }
			
			sta.close();
			connection.close();
			return contract;
	 }
	 
	 public static String[] getContractPayments(long contract_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		 String[] data = new String[4];
		 connectdb();
		 Statement sta1 = (Statement) connection.createStatement();
		 //String user = "Junyi";
		 //System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
		 ResultSet rs = sta1.executeQuery("SELECT * FROM CONTRACT WHERE ID = '" + contract_id + "';");
		 
		 while (rs.next())
	      {
	        //long id = rs.getLong("ID");
	        //String people = rs.getString("PEOPLE");
	        //String colla = rs.getString("COLLA");
	        //String app = rs.getString("APP");
			data[0] = String.valueOf(rs.getLong("maxDuration"));
			data[1] = String.valueOf(rs.getLong("jobId"));
			data[2] = String.valueOf(rs.getLong("maxTotalCpuT"));
			//data[1] = String.valueOf(rs.getDouble("maxCost"));
			//data[2] = rs.getString("worker");
			//data[3] = rs.getString("share");
	         
	        // print the results
	        System.out.format("%s \n", data[0] + "; " + data[1]);
	      }
		//System.out.println(re.getString(10).toString());
		//Helper.writeout(String.valueOf(data.length));
		sta1.close();
		connection.close();
		return data;
	 }
	 
	 public static String[] getRenegInfo(long contract_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		 String[] data = new String[11];
		 connectdb();
		 Statement sta1 = (Statement) connection.createStatement();
		 //String user = "Junyi";
		 //System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
		 ResultSet rs = sta1.executeQuery("SELECT * FROM CONTRACT WHERE ID = '" + contract_id + "';");
		 
		 while (rs.next())
	      {
	        //long id = rs.getLong("ID");
	        //String people = rs.getString("PEOPLE");
	        //String colla = rs.getString("COLLA");
	        //String app = rs.getString("APP");
			//data[0] = String.valueOf(rs.getInt("maxDuration"));
			//data[0] = String.valueOf(rs.getDouble("maxCost"));
			data[0] = String.valueOf(rs.getDouble("charge"));
			data[1] = rs.getString("startTime");
			data[2] = rs.getString("measurement");
			data[3] = rs.getString("worker");
			//data[4] = String.valueOf(rs.getInt("cpuNo"));
			data[4] = String.valueOf(rs.getDouble("maxCost"));
			data[5] = String.valueOf(rs.getInt("requiredCpuNo"));
			data[6] = String.valueOf(rs.getDouble("userBalance")); 
			data[7] = String.valueOf(rs.getLong("maxDuration")); 
			data[8] = String.valueOf(rs.getLong("requiredCpuT"));
			data[9] = String.valueOf(rs.getLong("jobId"));
			data[10] = String.valueOf(rs.getLong("maxTotalCpuT"));
	        // print the results
	        System.out.format("%s \n", data[0] + "; " + data[1]);
	      }
		//System.out.println(re.getString(10).toString());
		//Helper.writeout(String.valueOf(data.length));
		sta1.close();
		connection.close();
		return data;
	 }
	 
	 public static String[] getUserInfo(long contract_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		 String[] data = new String[4];
		 connectdb();
		 Statement sta1 = (Statement) connection.createStatement();
		 //String user = "Junyi";
		 //System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
		 ResultSet rs = sta1.executeQuery("SELECT * FROM CONTRACT WHERE ID = '" + contract_id + "';");
		 
		 while (rs.next())
	      {
	        //long id = rs.getLong("ID");
	        //String people = rs.getString("PEOPLE");
	        //String colla = rs.getString("COLLA");
	        //String app = rs.getString("APP");
			//data[0] = String.valueOf(rs.getInt("maxDuration"));
			//data[0] = String.valueOf(rs.getDouble("maxCost"));
			data[0] = rs.getString("username");
			data[1] = rs.getString("groupname");
			data[2] = rs.getString("appname");
			data[3] = rs.getString("share");
			//data[2] = rs.getString("share");
	         
	        // print the results
	        System.out.format("%s \n", data[0] + "; " + data[1]);
	      }
		//System.out.println(re.getString(10).toString());
		//Helper.writeout(String.valueOf(data.length));
		sta1.close();
		connection.close();
		return data;
	 }
	 
	 public static Long getContractId(long inst_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			long contract_id = 0;
			connectdb();
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM CONTRACTINST WHERE instId = '" + inst_id + "';");
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				contract_id = rs.getLong("conId");
				//Executable.writeout(String.valueOf(contract_id)); 
		        // print the results
		        System.out.format("%s \n", contract_id);
		      }
			//System.out.println(re.getString(10).toString());
			sta1.close();
			connection.close();
			return contract_id;
		 }
	 
	 public static Long getJobIdFromInst(long inst_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			long job_id = 0;
			connectdb();
			Statement sta1 = (Statement) connection.createStatement();
			//String user = "Junyi";
			//System.out.println("SELECT * FROM NEGOTIATION WHERE NEGOTIATION.PEOPLE = '" + name + "';\"");
			ResultSet rs = sta1.executeQuery("SELECT * FROM JobInst WHERE inst_id = '" + inst_id + "';");
			while (rs.next())
		      {
		        //long id = rs.getLong("ID");
		        //String people = rs.getString("PEOPLE");
		        //String colla = rs.getString("COLLA");
		        //String app = rs.getString("APP");
				job_id = rs.getLong("con_id");
				//Executable.writeout(String.valueOf(contract_id)); 
		        // print the results
		        System.out.format("%s \n", job_id);
		      }
			//System.out.println(re.getString(10).toString());
			sta1.close();
			connection.close();
			return job_id;
		 }
	 
	 
	/*public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		 //negDelete(6);
		//getContProvider(55555);
		 //getPaybackInfo(55555);
		 //System.out.println(getOffer(1).getService());
		
		  Calendar cal1 = Calendar.getInstance();
	      SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");
	      Calendar cal2 = Calendar.getInstance();
	      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
   	      String date = sdf1.format(cal1.getTime()) + "T" + sdf.format(cal2.getTime()) + "+7:00";
          System.out.println("current date: " + date);
		  updateOfferState(4627, NegState.ReqTerminated, 1, date);
		  System.out.println("contract id is: " + NegotiationDB.getContractId(7));
		  System.out.println("end");
	 }*/

}
