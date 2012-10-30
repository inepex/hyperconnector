package com.inepex.hyperconnector.common;


/**
 * Class for commonly used functions.
 * @author Gabor Dicso
 *
 */
public class HyperDateCommon {
	/**
	 * http://code.google.com/p/hypertable/wiki/ArchitecturalOverview#Data_Model
	 * Timestamp "is usually auto assigned by the system and represents the insertion
	 * time of the cell in nanoseconds since the epoch".
	 */

	public static long utcNowInNanoSecs() {
		return System.nanoTime();
	}
}
