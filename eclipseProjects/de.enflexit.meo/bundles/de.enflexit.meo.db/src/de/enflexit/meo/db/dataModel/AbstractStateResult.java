package de.enflexit.meo.db.dataModel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * The Class AbstractStateResult serves as .
 */
public abstract class AbstractStateResult implements Serializable {

	private static final long serialVersionUID = -7983870859052646439L;

	private static SimpleDateFormat sdf;
	
	/**
	 * Has to return the string for an SQL insert in brackets and with comma separated values (e.g '(a, b, c)');  
	 */
	public abstract String getSQLInsertValueArray();
	
	
	public static SimpleDateFormat getSimpleDateFormatter() {
		if (sdf==null) {
			// --- Requires the format '2020-05-19 05:17:15.982' ---- 
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		}
		return sdf;
	}
	
	public static String getTimeStampAsSQLString(Calendar calendar) {
		String timeStampString = null;
		if (calendar!=null) {
			timeStampString = getSimpleDateFormatter().format(calendar.getTime());
		}
		return timeStampString;
	}
	
	
}