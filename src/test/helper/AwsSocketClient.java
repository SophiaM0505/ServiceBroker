package test.helper;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.restlet.resource.ClientResource;
import java.net.URI;

public class AwsSocketClient {
	
	 //public static void callAws(URI uri)
	public static void main(String[] args) {
     {
	   try{
		    //String link = uri.getPath();
		   String link = "130.88.198.146";
		    Socket socketClient= new Socket(link,21370);
		    //InputStream input = null;
		    OutputStream output = null;
		    System.out.println("Client: "+"Connection Established");

		    //input = socketClient.getInputStream();
		    //BufferedReader reader = 
		    	//	new BufferedReader(new InputStreamReader(input));

		    output = socketClient.getOutputStream();
		    BufferedWriter writer= 
	        		new BufferedWriter(new OutputStreamWriter(output));
		    //String serverMsg;
		    writer.write("hello from sophie's pc.");
		    //writer.write("10\r\n");
            writer.flush();
			//while((serverMsg = reader.readLine()) != null){
				//System.out.println("Client: " + serverMsg);
			//}
			//input.close();
			output.close();
			socketClient.close();

	   }catch(Exception e){e.printStackTrace();}
     }
	}

}
