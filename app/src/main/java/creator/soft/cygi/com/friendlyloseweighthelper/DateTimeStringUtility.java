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

    private static final String dateFormatPattern = "dd-MM-yyyy";
    private static final String time24hourPattern = "HH:mm:ss";
    private static final String time12hourPattern = "h:mm:ss a";
    private static String TAG = "DateTimeStringUtility";
    private static DateFormat formatRawDatePattern = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);

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
    public static String getTimePattern12or24(Context context) {
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


            simpleDateFormat.applyPattern(dateFormatPattern + " " + time24hourPattern);
        } else {

            simpleDateFormat.applyPattern(dateFormatPattern + " " + time12hourPattern);
        }
        return simpleDateFormat;
    }

    public static String convertToRawDate(String formattedDate) {

        SimpleDateFormat formattedDatePattern = new SimpleDateFormat(dateFormatPattern);

        Date rawDate = getRawDateBaseOnDatePattern(formattedDate, formattedDatePattern);

        return rawDate.toString();
    }

    public static String convertToRawTime(Context context, String formattedTIme) {

        SimpleDateFormat formattedDatePattern = new SimpleDateFormat(getTimePattern12or24(context));

        Date rawTime = getRawDateBaseOnDatePattern(formattedTIme, formattedDatePattern);

        return rawTime.toString();
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

    public static String convertTimeBaseOnDeviceFormat12or24(Context context, String currentTime) {

        if (is24HourFormat(context)) {

            String pattern24hourPatternRegex = "\\d{1,2}:\\d{2}:\\d{2}";

            if(currentTime.matches(pattern24hourPatternRegex)){

                Log.d(TAG,"Time format not 12 hour format return current time: " + currentTime);
                return currentTime;

            }

            String formatted24HourTime = performFormattingOperation(context, currentTime, time12hourPattern);
            return formatted24HourTime;

        } else {

            String pattern12hourPatternRegex = "\\d{1,2}:\\d{2}:\\d{2} [AP][M]";

            if(currentTime.matches(pattern12hourPatternRegex)){

                Log.d(TAG,"Time format not 24 hour format return current time: " + currentTime);
                return currentTime;

            }

            String formatted12HourTime = performFormattingOperation(context, currentTime, time24hourPattern);
            return formatted12HourTime;
        }
    }

    @NonNull
    private static String performFormattingOperation(Context context, String currentTime, String formattingTimePattern) {

        SimpleDateFormat timeSimpleDateFormat = new SimpleDateFormat(formattingTimePattern);
        Date rawTime = getRawDateBaseOnDatePattern(currentTime, timeSimpleDateFormat);
        String formatted24or12HourTime = DateTimeStringUtility.formatTime(context, rawTime);

        return formatted24or12HourTime;
    }


    public static String combineTwoDates(Context context, String date, String time) {

        String dateRawString = convertToRawDate(date);
        String timeRawString = convertToRawTime(context, time);

        Log.i(TAG," date raw Stamp : "+ dateRawString);
        Log.i(TAG,"time raw Stamp:  "+ timeRawString);

        Date dateRaw = changeToDate(dateRawString);
        Date timeRaw = changeToDate(timeRawString);

        Calendar dateRawCalendar = Calendar.getInstance();
        dateRawCalendar.setTime(dateRaw);

        Calendar timeRawCalendar = Calendar.getInstance();
        timeRawCalendar.setTime(timeRaw);

        dateRawCalendar.set(Calendar.HOUR_OF_DAY, timeRawCalendar.get(Calendar.HOUR_OF_DAY));
        dateRawCalendar.set(Calendar.MINUTE,timeRawCalendar.get(Calendar.MINUTE));
        dateRawCalendar.set(Calendar.SECOND,timeRawCalendar.get(Calendar.SECOND));

        Date dateAndTimeCombine = dateRawCalendar.getTime();

        Log.i(TAG, "Combine Dates: " + dateAndTimeCombine.toString());



        return changeToStringRepresentation(dateAndTimeCombine);
    }

    public static String changeSecondsToZero(String dateRawString){

        Date dateRaw = changeToDate(dateRawString);

        Calendar dateRawCalender = Calendar.getInstance();
        dateRawCalender.setTime(dateRaw);
        dateRawCalender.set(Calendar.SECOND, 0);

        String dateWithZeroSeconds = dateRawCalender.getTime().toString();

        return dateWithZeroSeconds;
    }
}
