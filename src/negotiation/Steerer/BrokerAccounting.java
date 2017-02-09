package negotiation.Steerer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import negotiation.Database.NegotiationDB;
import negotiation.Negotiation.NegState;
import negotiation.Ontology.OntUpdate;

public class BrokerAccounting {
	public static void brokerAccounting() throws IOException{
	//public static void main(String[] args) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException {
		// to execute fetch_file_broker.sh to get accounting data from cluster
		// it will copy job_duration.txt to /opt/AHE3/
		String command = "/opt/test/fetch_file_broker.sh";
		Runtime rt = Runtime.getRuntime();
		Process p = rt.exec(command);
		
		BufferedReader reader =
                new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
        while ((line = reader.readLine())!= null) {
	        //output.append(line + "\n");
	        System.out.println(line);
        }
	    
	    // to read the file and update ontologies
	    final String path = "/opt/test/";
		File job_file = new File(path + "job_duration.txt");
		
		/*long start = System.currentTimeMillis();
		System.out.println("sophia " + start);
        long end = start + 60*1000; // 60 seconds * 1000 ms/sec
        System.out.println("hi     " + end);
        while (System.currentTimeMillis() < end)
            {
            // run
        	int i = 0;
        	    System.out.println("hello" + start + " " + end + " " + i);
        	    i++;
            }
        System.out.println("lasse  " + System.currentTimeMillis());*/
		
		if(job_file.exists()){
			Reader duration_reader = new FileReader(job_file);
			BufferedReader bufferedReader = new BufferedReader(duration_reader);
			String new_line;
			long broker_job_id;
			double duration;
			while((new_line = bufferedReader.readLine()) != null){
				broker_job_id = Long.parseLong(new_line.split(":")[0]);
				duration = Double.parseDouble(new_line.split(":")[1]);
				System.out.println("*********** " + broker_job_id + "**** " + duration);
				//to get contract_id of the broker_job_id
				/*long contract_id = NegotiationDB.getConFromJob(broker_job_id);
				String state = NegotiationDB.getContractStatus(contract_id);
		   		  if(state.equalsIgnoreCase(NegState.Contracted.toString())){
				      OntUpdate.mPolicyShareCompleteReduce(contract_id, duration);
		   		  }*/
			}
		}
	    
	}

}
