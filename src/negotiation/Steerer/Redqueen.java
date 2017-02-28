package negotiation.Steerer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;

import com.jcabi.ssh.SSHByPassword;
import com.jcabi.ssh.Shell;
import com.jcabi.ssh.Shell.Plain;

import negotiation.Database.NegotiationDB;

public class Redqueen {
	//to process job_unread.txt transferred from desktop
		//public static void main(String[] args) throws IOException, NumberFormatException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
	public static void getRedqueenJobId(long broker_job_id) throws NumberFormatException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
			final String path = "/opt/test/";
			//String broker_job_id = args[0];
			System.out.println("!!!!!!! to get cluster job id");
					//to change job_unread.txt to job_read.txt
					File unread_file = new File(path + "job_unread.txt");
					if(!unread_file.exists()){
						System.out.println("!!!!!!! the job_unread file does not exit!");
						return;
					}
					else{
						BufferedReader read_br = null;
						read_br = new BufferedReader(new FileReader(path + "job_unread.txt"));
						String sCurrentLine;
						String redqueen_job_id;
						//String broker_job_id;
						while ((sCurrentLine = read_br.readLine()) != null) {
							if (sCurrentLine.contains(Long.toString(broker_job_id))){
								redqueen_job_id = sCurrentLine.split(":")[0];
								System.out.println("!!!!!!! cluster job id is " + redqueen_job_id);
								System.out.println("The submitted job has a local job ID in the Cluster: " + redqueen_job_id);
								//to store the data into database
								negotiation.Database.NegotiationDB.insertRedqueenJobId(broker_job_id,Long.parseLong(redqueen_job_id));
								//to delete job_unread.txt?
								break;
							}
					}
					}
		}
	
	public static boolean checkRedqueenComplete(long broker_job_id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException{
		boolean complete = false;
		String redqueen_job_id;
		redqueen_job_id = Long.toString(NegotiationDB.getRedqueenJobId(broker_job_id));
		System.out.println("$$$$$$$$ fetched cluster job id from database is: " + redqueen_job_id);
		//to connect with desktop and execute check_job_desktop.sh
		/*Plain ssh1 = new Shell.Plain(
				new SSHByPassword(
						"rs0.cs.man.ac.uk", 22, "mengz", "mM_20110919")
				);
		
		String command = "/home/MSC11/mengz/Desktop/tests/check_job_complete_desktop.sh " + redqueen_job_id + " " + broker_job_id;
		System.out.println("$$$$$$$$ command 1: " + command);
		String stdout1 = ssh1.exec(command);
		System.out.println(stdout1);*/
		
		Runtime rt = Runtime.getRuntime();
		String command = "/opt/test/check_job_complete_broker.sh " + redqueen_job_id + " " + broker_job_id;
		System.out.println("$$$$$$$$ command : " + command);
		rt.exec(command);
		
		long start_1 = System.currentTimeMillis();
		//System.out.println("sophia 1" + start_1);
        long end_1 = start_1 + 35*1000; // 60 seconds * 1000 ms/sec
        //System.out.println("hi     " + end_1);
        while (System.currentTimeMillis() < end_1)
            {
            // run
        	/*int i = 0;
        	    System.out.println("hello" + start + " " + end + " " + i);
        	    i++;*/
            }
        //System.out.println("lasse 1 " + System.currentTimeMillis());
		
		String command_2 = "/opt/test/fetch_complete_broker.sh " + redqueen_job_id;
		System.out.println("$$$$$$$$ command 2: " + command_2);
		Runtime.getRuntime().exec(command_2);
		//System.out.println(stdout2);
		
		long start = System.currentTimeMillis();
		//System.out.println("sophia " + start);
        long end = start + 6*1000; // 60 seconds * 1000 ms/sec
        //System.out.println("hi     " + end);
        while (System.currentTimeMillis() < end)
            {
            // run
        	/*int i = 0;
        	    System.out.println("hello" + start + " " + end + " " + i);
        	    i++;*/
            }
        //System.out.println("lasse  " + System.currentTimeMillis());
		
		final String path = "/opt/test/";
		File job_file = new File(path + redqueen_job_id + ".txt");
		if(job_file.exists()){
			//job not completed yet
			complete = false;
		}
		else{
			complete = true;
		}
		return complete;
	}

}
