package utils;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public abstract class Dates {

	public static String dateToString(Date date, String format) {
		SimpleDateFormat formater = new SimpleDateFormat(format);
		String str = "";

		try {
			if (date != null) {
				str = formater.format(date);
			}
		} catch (Exception ex) {
			Log.e("Dates", "Erro ao converter data em string");
		}
		return str;
	}

	public static String hoje() {
		return Dates.dateToString(new Date(), "yyyy-MM-dd");
	}

	public static Date today(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		String format = sdf.format(date);

		Date today = null;
		try {
			today = sdf.parse(format);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return today;
	}

	
	
	public static String format(Context ctx, String strDate, String strFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
		Date date = null;
		try {
			date = sdf.parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		java.text.DateFormat dateFormat = DateFormat.getDateFormat(ctx);
		String s = dateFormat.format(date);
		return s;
	}

	public static String toSQL(Context context, String strDate){
		java.text.DateFormat dateFormat = DateFormat.getDateFormat(context);
		Date ddate = null;
		try {
			ddate = dateFormat.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return Dates.dateToString(ddate, "yyyy-MM-dd");
	}
	
	public static String toScreen(Context context, String strDate){
		java.text.DateFormat dateFormat = DateFormat.getDateFormat(context);
		Date date = null;
		try {
			date = dateFormat.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return dateFormat.format(date);
	}
	
	public static String toScreen(Context context, Date date){
		java.text.DateFormat dateFormat = DateFormat.getDateFormat(context);
		String dateFormated = dateFormat.format(date);
		
		return dateFormated;
	}
	
	public static Date stringToDate(Context ctx, String strDate, String strFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
		Date date = null;
		try {
			date = sdf.parse(strDate);
		} catch (Exception e) {

		}

		return date;
	}
	
	public static long diff(Date start, Date end) {
		Calendar d1 = Calendar.getInstance();
        d1.setTime(start);
 
        Calendar d2 = Calendar.getInstance();
        long diferenca = d2.getTimeInMillis() - d1.getTimeInMillis();
        
        int tempoDia = 1000 * 60 * 60 * 24;
        long diasDiferenca = diferenca / tempoDia;
        return diasDiferenca;
	}

}
