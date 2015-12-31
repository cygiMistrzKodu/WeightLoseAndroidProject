package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    private static DateFormat formatRawDatePattern = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);
    private static final String dateFormatPattern = "dd-MM-yyyy";


    public static Date changeToDate(String dateInString) {

        Date date = getRawDateBaseOnDatePattern(dateInString, formatRawDatePattern);

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

    public static String getCurrentNonFormattedDateInStringRepresentation() {

        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();

        return currentDate.toString();
    }

    public static String getCurrentFormattedDateStringRepresentation() {

        Date date = getCurrentDate();
        String formattedCurrentDateString = formatDate(date);

        return formattedCurrentDateString;
    }

    public static String getCurrentFormattedTimeStringRepresentation(Context context) {

        Date time = getCurrentDate();
        String formattedCurrentTimeString = formatTime(context, time);

        return formattedCurrentTimeString;
    }

    public static String formatStringRawDate(String dateToFormat) {

        Date nonFormatDate = changeToDate(dateToFormat);
        String formattedDateInString = formatDate(nonFormatDate);

        return formattedDateInString;
    }

    @NonNull
    public static String formatDate(Date Date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatPattern);
        return simpleDateFormat.format(Date);
    }

    public static String formatRawTime(Context context, String timeToFormat) {

        Date nonFormatTime = changeToDate(timeToFormat);

        String FormattedTime = formatTime(context, nonFormatTime);

        return FormattedTime;
    }

    @NonNull
    public static String formatTime(Context context, Date time) {
        String timeFormatPattern = getTimePattern12or24(context);
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(timeFormatPattern);
        return simpleTimeFormat.format(time);
    }

    @NonNull
    private static String getTimePattern12or24(Context context) {
        String timeFormatPattern;
        if (is24HourFormat(context)) {
            timeFormatPattern = "HH:mm:ss";
        } else {
            timeFormatPattern = "h:mm:ss a";
        }
        return timeFormatPattern;
    }

    public static boolean is24HourFormat(Context context) {

        boolean is24hFormat = android.text.format.DateFormat.is24HourFormat(context);
        Log.i(TAG, "IS 24 Hour Format :  " + is24hFormat);

        return is24hFormat;
    }

    public static SimpleDateFormat getDateFormattingPattern(Context context) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

        if (is24HourFormat(context)) {

        //    simpleDateFormat.applyPattern("dd-MM-yyyy HH:mm:ss");
            simpleDateFormat.applyPattern(dateFormatPattern+" "+"HH:mm:ss");
        } else {

            simpleDateFormat.applyPattern(dateFormatPattern+" "+"h:mm:ss a");
//            simpleDateFormat.applyPattern("dd-MM-yyyy h:mm:ss a");
        }
        return simpleDateFormat;
    }

    public static String convertToRawDate(String formattedDate) {

        SimpleDateFormat formattedDatePattern = new SimpleDateFormat(dateFormatPattern);

        Date rawDate = getRawDateBaseOnDatePattern(formattedDate, formattedDatePattern);

        return rawDate.toString();
    }

    @Nullable
    private static Date getRawDateBaseOnDatePattern(String formattedDate, DateFormat datePattern) {
        Date date = null;
        try {
            date = datePattern.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


}
