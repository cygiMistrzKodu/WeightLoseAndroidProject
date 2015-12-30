package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by CygiMasterProgrammer on 2015-12-28.
 */
public class DateTimeStringUtility {

    private static String TAG = "DateTimeStringUtility";

    private static DateFormat formatDate = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);


    public static Date changeToDate(String dateInString) {

        Date date = null;
        try {

            date = formatDate.parse(dateInString);

            Log.d(TAG, date.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String changeToStringRepresentation(Date date) {

        return date.toString();
    }

    public static Date getCurrentDate() {

        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();

        return currentDate;
    }

    public static String getCurrentDateInStringRepresentation() {

        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();

        return currentDate.toString();
    }

    public static String formatRawDate(String dateToFormat) {

        Date nonFormatDate = changeToDate(dateToFormat);
        String dateFormatPattern = "dd-MM-yyyy";

        return new SimpleDateFormat(dateFormatPattern).format(nonFormatDate);
    }

    public static String formatRawTime(Context context, String timeToFormat) {

        Date nonFormatTime = changeToDate(timeToFormat);
        String timeFormatPattern = null;

        if (is24HourFormat(context)) {
            timeFormatPattern = "HH:mm:ss";
        } else {
            timeFormatPattern = "h:mm:ss a";
        }

        return new SimpleDateFormat(timeFormatPattern).format(nonFormatTime);
    }

    public static boolean is24HourFormat(Context context) {

        boolean is24hFormat = android.text.format.DateFormat.is24HourFormat(context);
        Log.i(TAG, "IS 24 Hour Format :  " + is24hFormat);

        return is24hFormat;
    }

    public static SimpleDateFormat getDateFormattingPattern(Context context) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

        if (is24HourFormat(context)) {

            simpleDateFormat.applyPattern("dd-MM-yyyy HH:mm:ss");
        } else {

            simpleDateFormat.applyPattern("dd-MM-yyyy h:mm:ss a");
        }
        return simpleDateFormat;
    }
}
