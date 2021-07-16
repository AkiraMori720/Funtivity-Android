package com.brainyapps.funtivity.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils {
	public static String DATE_STRING_FORMAT = "MM/dd/YY";
	public static String TIME_STRING_FORMAT = "hh:mm aa";
	public static String DATE_TIME_STRING_FORMAT = "MM/dd/yyyy HH:mm";

	public static String dateToString(Date date, String strformat) {
		SimpleDateFormat format = new SimpleDateFormat(strformat);
		return format.format(date);
	}

	public static Date stringToDate(String strDate, String strformat) {
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat(strformat);
		try {
			date = format.parse(strDate);
		} catch (ParseException e) {}
		return date;
	}

	public static Date getDate(Date date, int hour, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		return calendar.getTime();
	}

	public static String getStrTime(int hour, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		return dateToString(calendar.getTime(), TIME_STRING_FORMAT);
	}

	public static Date getStartDate(Date fromDate) {
		String date = dateToString(fromDate, "MM/dd/yyyy") + " 00:00";
		return stringToDate(date, DATE_TIME_STRING_FORMAT);
	}

	public static Date getEndDate(Date toDate) {
		String date = dateToString(toDate, "MM/dd/yyyy") + " 23:59";
		return stringToDate(date, DATE_TIME_STRING_FORMAT);
	}
}
