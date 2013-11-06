package eu.hydrologis.geodroid.maps;

import java.text.DecimalFormat;


//Utility class for converting units. Converts meters to feet.

public final class ConvertMetric {
	
	  /** Multiplier for meters to feet. **/
	
private static final double FEET_CONVERT = 3.2808399;
	
private ConvertMetric()	{}

/*** Convert a number of meters to feet.
 * @param meters Value to convert in meters.
 * @return meters converted to feet.
 */
	
public static double asFeet(final double meters) {
	DecimalFormat twoDForm = new DecimalFormat("#.##");
	return Double.valueOf(twoDForm.format(meters * FEET_CONVERT));
}

public static String asFeetString(final double meters) {
	return asFeet(meters) + "ft";
}

public static String asMeterString(final int meters) {
	return meters + "m";
}

}
