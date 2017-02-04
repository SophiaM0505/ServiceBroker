package uk.ac.ucl.chem.ccs.AHEModule.message.JSON;

import java.util.Date;

import com.google.gson.Gson;

import uk.ac.ucl.chem.ccs.AHEModule.Def.MessageResponse;
import uk.ac.ucl.chem.ccs.AHEModule.message.JSON.Objects.AHEMessage;

/**
 * AHE JSON message utility. This utility uses the GSON library to handle the JSON/Java conversion
 * @author davidc
 *
 */

public class JSONMessageUtil {

	/**
	 * Create a AHE JSON information message
	 * @param command
	 * @param information
	 * @param warning
	 * @param exception
	 * @return
	 */

	public static String createMessage(String command, String[] information, String[] warning, String[] exception){
		
		AHEMessage msg = new AHEMessage();
		msg.setCommand(command);
		msg.setInformation(information);
		msg.setWarning(warning);
		msg.setException(exception);
		msg.setTimestamp(new Date().toString());
		
		return (new Gson()).toJson(msg);
		
	}
	
	/**
	 * Create a AHE JSON information message
	 * @param command
	 * @param information
	 * @param warning
	 * @param exception
	 * @param timestamp
	 * @return
	 */
	
	public static String createMessage(String command, String[] information, String[] warning, String[] exception, Date timestamp){
		
		AHEMessage msg = new AHEMessage();
		msg.setCommand(command);
		msg.setInformation(information);
		msg.setWarning(warning);
		msg.setException(exception);
		msg.setTimestamp(timestamp.toString());
		
		return (new Gson()).toJson(msg);
		
	}
	
	/**
	 * Create a AHE JSON information message
	 * @param command
	 * @param information
	 * @return
	 */
	
	public static String createInformationMessage(String command, String[] information){
		return createMessage(command, information, null, null);
	}
	
	/**
	 * Create a AHE JSON information message
	 * @param command
	 * @param information
	 * @param timestamp
	 * @return
	 */
	
	public static String createInformationMessage(String command, String[] information, Date timestamp){
		return createMessage(command, information, null, null, timestamp);
	}
	
	/**
	 * Create a AHE JSON exception message
	 * @param command
	 * @param exception
	 * @return
	 */
	
	public static String createExceptionMessage(String command, String[] exception){
		return createMessage(command,  null, null, exception);
	}
	
	/**
	 * Create a AHE JSON exception message
	 * @param command
	 * @param exception
	 * @param timestamp
	 * @return
	 */
	
	public static String createExceptionMessage(String command, String[] exception, Date timestamp){
		return createMessage(command,  null, null, exception, timestamp);
	}
	
	/**
	 * Parse AHE JSON message and convert it into a Java object
	 * @param json
	 * @return
	 */
	
	public static AHEMessage parseJSONMessage(String json){
		
		AHEMessage msg = (new Gson()).fromJson(json, AHEMessage.class);
		return msg;
		
	}
	
}

