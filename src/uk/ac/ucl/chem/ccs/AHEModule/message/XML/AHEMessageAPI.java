package uk.ac.ucl.chem.ccs.AHEModule.message.XML;

import java.io.ByteArrayOutputStream;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import uk.ac.ucl.chem.ccs.AHEModule.message.XML.Objects.ahe;

/**
 * AHE XML Message utility. This class handles the conversion between XML & Java object using SimpleXML library
 * @author davidc
 *
 */

public class AHEMessageAPI {

	/**
	 * Convert an ahe entity to a XML string representation
	 * @param message
	 * @return XML String representation
	 */

	public static String generateXML(ahe message){
				
		Serializer serializer = new Persister();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try {
			serializer.write(message, output);
			return output.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		

		
	}
	
	/**
	 * Parse a XML string message and convert it into a Java ahe message object
	 * @param ahe_xml_message
	 * @return ahe entity
	 * @throws Exception
	 */
	
	public static ahe parseXML(String ahe_xml_message) throws Exception{
		
		Serializer serializer = new Persister();
		ahe message = serializer.read(ahe.class, ahe_xml_message);
		
		return message;
		
	}
	
}
