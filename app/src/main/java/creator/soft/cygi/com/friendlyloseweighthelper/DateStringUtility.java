package creator.soft.cygi.com.friendlyloseweighthelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by CygiMasterProgrammer on 2015-12-28.
 */
public  class DateStringUtility {

  private static DateFormat  formatDate = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);

    public static Date changeToDate(String dateInString){

        Date date = null;
        try {
             date = formatDate.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String changeToStringRepresentation(Date date){

        return date.toString();
    }

    public static Date getCurrentDate() {

        Calendar cal = Calendar.getInstance();
        Date currentDate =  cal.getTime();

        return currentDate;
    }

    public static String getCurrentDateInStringRepresentation() {

        Calendar cal = Calendar.getInstance();
        Date currentDate =  cal.getTime();

        return currentDate.toString();
    }
}
