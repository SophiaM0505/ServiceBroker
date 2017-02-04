package uk.ac.ucl.chem.ccs.AHEModule.Def;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * AHE Date definition utility
 * @author davidc
 *
 */

public class DateFormat {

	public static final String DateFormatPattern = "yyyy-MM-dd HH:mm:ss Z";

	public static String formatDate(Date date){
		
		SimpleDateFormat format = new SimpleDateFormat(DateFormatPattern);
		return format.format(date);
		
	}
	
}
