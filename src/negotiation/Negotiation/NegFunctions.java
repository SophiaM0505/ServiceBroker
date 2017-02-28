package negotiation.Negotiation;

import java.sql.SQLException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import negotiation.Database.NegotiationDB;
import negotiation.Ontology.OntUpdate;

public class NegFunctions {

	public static void updateComplete(long contract_id, String comp_time) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, OWLOntologyCreationException, OWLOntologyStorageException, InterruptedException{
		String state;
		String contracts_comb = NegotiationDB.getOfferSub(contract_id);
   	    if(contracts_comb.isEmpty()){
   		  //to check state first
   		  state = NegotiationDB.getContractStatus(contract_id);
   		  if(state.equalsIgnoreCase(NegState.contracted.toString())){
   			//to update state
   			NegotiationDB.updateContractStateEndT(contract_id, NegState.completed, comp_time);

			//to update balances in ontologies
		    OntUpdate.mPolicyShareCompleteReduce(contract_id, comp_time);
   		 }
   	  }
   	  //if contain sub-contracts
   	  else{
   		 String[] contracts_arr = contracts_comb.split(";");
   		  for(String temp_con: contracts_arr){
 			      long temp_contract_id = Long.parseLong(temp_con);
 			      NegotiationDB.updateContractStateEndT(temp_contract_id, NegState.completed, comp_time);

 			      //to update balances in ontologies
 			      OntUpdate.mPolicyShareCompleteReduce(temp_contract_id, comp_time);
 		      }
 		     //to update the state of combined contract
 		     NegotiationDB.updateContractStateEndT(contract_id, NegState.completed, comp_time);
   	  }
	}
}
