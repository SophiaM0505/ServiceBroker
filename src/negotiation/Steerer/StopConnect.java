package negotiation.Steerer;

import java.io.IOException;

import org.restlet.data.Form;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class StopConnect {
	public static String stopConnect(String uri){
	
		ClientResource resource = new ClientResource("http://" + uri);  
		//ClientResource resource = new ClientResource("http://ec2-52-31-132-128.eu-west-1.compute.amazonaws.com:8080/AwsRestWebTest/test");
 
		//ClientResource resource = new ClientResource("http://ec2-52-31-147-18.eu-west-1.compute.amazonaws.com:8080");
		String response = null;
		Form form = new Form(); 
		//form.add("contract_id", String.valueOf(contract_id));
		
		try {
	        //resource.post(form).write(System.out);
	        
	        response = resource.post(form).getText();
	        //Helper.writeout(response);
			//resource.get().write(System.out);
 
		} catch (ResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return response;
	}
	

}
