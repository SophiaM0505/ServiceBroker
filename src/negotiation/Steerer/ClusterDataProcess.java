package negotiation.Steerer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import negotiation.Ontology.OntUpdate;

public class ClusterDataProcess {
	
	public static void main(String[] args) throws IOException, OWLOntologyCreationException, OWLOntologyStorageException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, InterruptedException{
		//read duration_record
		//record format: broker_job_id:duration
		final String path = "/opt/test/";
		File record_file = new File(path + "duration_record.txt");
		
		BufferedReader br = null;
		String sCurrentLine;

		br = new BufferedReader(new FileReader(record_file));

		String duration;
		long cont_id;
		while ((sCurrentLine = br.readLine()) != null) {
			System.out.println(sCurrentLine);
			cont_id = Long.parseLong(sCurrentLine.split(":")[0]);
			duration = sCurrentLine.split(":")[1];
		
			//to update ontologyies, can try to update database if have time
			OntUpdate.mPolicyShareCompleteReduce(cont_id, duration);
		}
		br.close();
	}

}
