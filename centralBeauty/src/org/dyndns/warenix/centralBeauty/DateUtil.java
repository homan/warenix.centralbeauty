package org.dyndns.warenix.centralBeauty;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static Date getMondayOfThisWeek() {

		Calendar cal = Calendar.getInstance();
		// Set the calendar to monday of the current week
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		return cal.getTime();
	}

	public static Date getToday() {
		Calendar cal = Calendar.getInstance();
		return cal.getTime();
	}

	public static int dateToNum(Date d) {
		return (d.getYear() + 1900) * 10000 + (d.getMonth() + 1) * 100
				+ d.getDate();
	}

}
