package com.inepex.hyperconnector.common;

import java.util.Date;


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

	  private static final long MS_TO_NS_MULTIPLIER = 1000000l;
      public static long nanoSecsToMilliSecs(long nanoSecs) {
              return nanoSecs / MS_TO_NS_MULTIPLIER;
      }

      public static long milliSecsToNanoSecs(long milliSecs) {
              return milliSecs * MS_TO_NS_MULTIPLIER;
      }

      public static Date nanoSecsTimestampToDate(long timestamp) {
              return new Date(nanoSecsToMilliSecs(timestamp));
      }
     
      public static long dateToNanoSecsTimestamp(Date date) {
              if (date == null)
                      return 0l;
              return milliSecsToNanoSecs(date.getTime());
      }

      public static long utcNowInNanoSecs() {
              return milliSecsToNanoSecs(System.currentTimeMillis());
      }

}
