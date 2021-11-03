package com.farthestgate.suspensions.android.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Suraj Gopal on 1/9/2017.
 */
public class DateUtils {

    public static final SimpleDateFormat ISO8601_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static String getCurrentDate(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }


    public static String getTime(){
        return new SimpleDateFormat("HH:mm").format(new Date());
    }

    public static String getISO8601DateTime(Date date){
        return ISO8601_DATE_TIME_FORMAT.format(date);
    }

    public static String getDateString(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date getDate(String date, String format) {
        Date parsedDate = null;
        try {
            parsedDate = new SimpleDateFormat(format).parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return parsedDate;

    }

    public static String changeFormat(String date, String fromFormat, String toFormat) {
        String reformattedStr = null;
       /* SimpleDateFormat fromUser = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");*/

        SimpleDateFormat fromUser = new SimpleDateFormat(fromFormat);
        SimpleDateFormat myFormat = new SimpleDateFormat(toFormat);
        try {
            reformattedStr = myFormat.format(fromUser.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }

    public static boolean compareDates(String d1,String d2)
    {
        boolean isValidDate = false;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            // Date object is having 3 methods namely after,before and equals for comparing
            // after() will return true if and only if date1 is after date 2
            if(date1.after(date2)){
                isValidDate = false;
            }
            // before() will return true if and only if date1 is before date2
            if(date1.before(date2)){
                isValidDate = true;
            }

            //equals() returns true if both the dates are equal
            if(date1.equals(date2)){
                isValidDate = false;
            }
        }
        catch(ParseException ex){
            ex.printStackTrace();
        }
        return isValidDate;
    }

    public static int yearsSince(String dateString) {
        Date pastDate = getDate(dateString, "dd/MM/yyyy");
        Calendar present = Calendar.getInstance();
        Calendar past = Calendar.getInstance();
        past.setTime(pastDate);

        int years = 0;

        while (past.before(present)) {
            past.add(Calendar.YEAR, 1);
            if (past.before(present)) {
                years++;
            }
        } return years;
    }

    public static int yearsSince(Date pastDate) {
        Calendar present = Calendar.getInstance();
        Calendar past = Calendar.getInstance();
        past.setTime(pastDate);

        int years = 0;

        while (past.before(present)) {
            past.add(Calendar.YEAR, 1);
            if (past.before(present)) {
                years++;
            }
        } return years;
    }

    public static Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static Date getDefaultDate() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.YEAR, 1970);
        return cal.getTime();
    }

}
