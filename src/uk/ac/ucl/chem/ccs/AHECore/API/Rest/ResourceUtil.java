package uk.ac.ucl.chem.ccs.AHECore.API.Rest;

import java.io.UnsupportedEncodingException;

import org.restlet.data.Form;

import uk.ac.ucl.chem.ccs.AHECore.API.Rest.XML.XMLServerMessageAPI;

/**
 * This utility converts the parameter string into a Java hashmap
 * 
 * @author davidc
 * 
 */

public class ResourceUtil {

	/**
	 * Converts a argument string i.e. arg1=va1&arg2=val2 into a RestArgMap object (Java hashmap)
	 * @param argument
	 * @return
	 */
	
	public static RestArgMap getArgumentMap(String argument) {

		RestArgMap arg_map = new RestArgMap();

		if (argument == null)
			return arg_map;

		if (argument.trim().length() == 0)
			return arg_map;

		// TODO: Assuming argument is in a valid form
		String[] arglist = argument.trim().split("&");

		if (arglist.length > 0) {

			for (String arg_pair : arglist) {

				String[] pair = arg_pair.split("=");

				try {
					// Need to deal with URI special escape characters
					arg_map.put(java.net.URLDecoder.decode(pair[0], "UTF-8"),
							java.net.URLDecoder.decode(pair[1], "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			return arg_map;

		} else {
			System.out.println("Error reading argument information");
			return arg_map;
		}

	}
	
	/**
	 * Converts a Restlet form object into a RestArgMap object
	 * @param form
	 * @return
	 */

	public static RestArgMap getArgumentMap(Form form) {

		RestArgMap arg_map = new RestArgMap();

		if (form == null)
			return arg_map;

		String[] names = form.getNames().toArray(new String[form.size()]);

		if (names.length > 0) {

			for (String key : names) {

				try {

					if (form.getFirstValue(key) != null)
						arg_map.put(
								java.net.URLDecoder.decode(key, "UTF-8"),
								java.net.URLDecoder.decode(
										form.getFirstValue(key), "UTF-8"));
					else
						arg_map.put(java.net.URLDecoder.decode(key, "UTF-8"),
								"");

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			return arg_map;

		} else {

			return arg_map;
		}

	}

	public static String ThrowErrorMessage(String cmd, String errormsg,
			String source) {

		return XMLServerMessageAPI.createExceptiondMessage(errormsg);

	}

}
