package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import creator.soft.cygi.com.friendlyloseweighthelper.utility.DateTimeStringUtility;

/**
 * Created by CygiMasterProgrammer on 2016-01-19.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DateFormat.class, Log.class})
public class DateTimeStringUtilityTest {

    private static final String IS_24_HOUR_FORMAT_METHOD_NAME = "is24HourFormat";

    @Before
    public void mockLogAndroidMethod() {
        PowerMockito.mockStatic(Log.class);
    }


    @Test
    public void when24hFormatIsSetAnUserChangeTo12hFormatThenTimeIsConvertTo12hFormatTest() {

        String time24hFormat = "00:00:01";
        String expected12hFormat = "12:00:01 AM";

        Context mockedContext = Mockito.mock(Context.class);

        PowerMockito.stub(PowerMockito.method(DateFormat.class, IS_24_HOUR_FORMAT_METHOD_NAME)).toReturn(false);

        String returnTimeFormat = DateTimeStringUtility
                .convertTimeBaseOnDeviceFormat12or24(mockedContext, time24hFormat);

        assertEquals(expected12hFormat, returnTimeFormat);

    }

    @Test
    public void when12hFormatIsSetAnUserChangeTo24hFormatThenTimeIsConvertTo24hFormatTest() {

        String time12hFormat = "3:20:03 PM";
        String expected12hFormat = "15:20:03";

        Context mockedContext = Mockito.mock(Context.class);

        PowerMockito.stub(PowerMockito.method(DateFormat.class, IS_24_HOUR_FORMAT_METHOD_NAME)).toReturn(true);

        String returnTimeFormat = DateTimeStringUtility
                .convertTimeBaseOnDeviceFormat12or24(mockedContext, time12hFormat);

        assertEquals(expected12hFormat, returnTimeFormat);

    }

    @Test
    public void whenTimeFormatIs24hAndTimeSetComeIsAlso24HThenShouldReturnSame24hTimeFormat() {

        String time24hFormat = "3:00:03";
        String expected24hFormat = "3:00:03";

        Context mockedContext = Mockito.mock(Context.class);

        PowerMockito.stub(PowerMockito.method(DateFormat.class, IS_24_HOUR_FORMAT_METHOD_NAME)).toReturn(true);

        String returnTimeFormat = DateTimeStringUtility
                .convertTimeBaseOnDeviceFormat12or24(mockedContext, time24hFormat);

        assertEquals(expected24hFormat, returnTimeFormat);

    }

    @Test
    public void whenTimeFormatIs12hAndTimeSetComeIsAlso12HThenShouldReturnSame12hTimeFormat() {

        String time12hFormat = "7:07:03 PM";
        String expected12hFormat = "7:07:03 PM";

        Context mockedContext = Mockito.mock(Context.class);

        PowerMockito.stub(PowerMockito.method(DateFormat.class, IS_24_HOUR_FORMAT_METHOD_NAME)).toReturn(false);

        String returnTimeFormat = DateTimeStringUtility
                .convertTimeBaseOnDeviceFormat12or24(mockedContext, time12hFormat);

        assertEquals(expected12hFormat, returnTimeFormat);

    }

    @Test
    public void changeDateWithTimeWithSecondsToTimeWithZeroSecondsTest() {

        String rawDateStringWithSeconds = "Wed Jan 20 01:22:54 CET 2016";
        String expectedDate = "Wed Jan 20 01:22:00 CET 2016";

        String returnDate = DateTimeStringUtility.changeSecondsToZero(rawDateStringWithSeconds);

        assertEquals(expectedDate, returnDate);
    }

    @Test
    public void whenSetTimeInStringGetObjectDateTest() {

        String rawDateString = "Wed Jan 20 01:22:54 CET 2016";

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("CET"));
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 22);
        calendar.set(Calendar.SECOND, 54);

        Date expectedDate = calendar.getTime();

        Date returnDate = DateTimeStringUtility.changeToDate(rawDateString);

        assertEquals(expectedDate.toString(), returnDate.toString());
    }

    @Test
    public void combineFormattedTimeAndDatesWhenTime24hFormatTest() {

        String time24hFormat = "3:00:03";
        String date = "01-01-2016";

        Context mocContext = Mockito.mock(Context.class);

        PowerMockito
                .stub(PowerMockito.method(DateFormat.class, IS_24_HOUR_FORMAT_METHOD_NAME))
                .toReturn(true);

        String expectedDate = "Fri Jan 01 03:00:03 CET 2016";

        String returnDateString = DateTimeStringUtility
                .combineTwoDates(mocContext, date, time24hFormat);

        assertEquals(expectedDate, returnDateString);
    }

    @Test
    public void combineFormattedTimeAndDatesWhenTime12hFormatTest() {

        String time12hFormat = "3:01:03 AM";
        String date = "01-02-2016";

        Context mocContext = Mockito.mock(Context.class);

        PowerMockito
                .stub(PowerMockito.method(DateFormat.class, IS_24_HOUR_FORMAT_METHOD_NAME))
                .toReturn(true);

        String expectedDate = "Mon Feb 01 03:01:03 CET 2016";

        String returnDateString = DateTimeStringUtility
                .combineTwoDates(mocContext, date, time12hFormat);

        assertEquals(expectedDate, returnDateString);
    }

    @Test
    public void whenTimeIs24hFormatThenReturnDatePatternWith24HourPatternFormat() {

        String expectedDatePatternWith24hFormatPattern = "dd-MM-yyyy HH:mm:ss";

        Context mocContext = Mockito.mock(Context.class);

        PowerMockito
                .stub(PowerMockito.method(DateFormat.class, IS_24_HOUR_FORMAT_METHOD_NAME))
                .toReturn(true);

        SimpleDateFormat returnSimpleDatePattern = DateTimeStringUtility
                .getDateFormattingPattern(mocContext);

        assertEquals(expectedDatePatternWith24hFormatPattern, returnSimpleDatePattern.toPattern());
    }

    @Test
    public void whenTimeIs24hFormatThenReturnDatePatternWith12HourPatternFormat() {

        String expectedDatePatternWith12hFormatPattern = "dd-MM-yyyy h:mm:ss a";

        Context mocContext = Mockito.mock(Context.class);

        PowerMockito
                .stub(PowerMockito.method(DateFormat.class, IS_24_HOUR_FORMAT_METHOD_NAME))
                .toReturn(false);

        SimpleDateFormat returnSimpleDatePattern = DateTimeStringUtility
                .getDateFormattingPattern(mocContext);

        assertEquals(expectedDatePatternWith12hFormatPattern, returnSimpleDatePattern.toPattern());
    }

    @Test
    public void formatRawTimeMethod24hTimeTest() {

        String formattedTime24hExpected = "15:01:03";
        String nonFormatDate = "Mon Feb 01 15:01:03 CET 2016";

        Context mocContext = Mockito.mock(Context.class);

        PowerMockito
                .stub(PowerMockito.method(DateFormat.class, IS_24_HOUR_FORMAT_METHOD_NAME))
                .toReturn(true);

        String returnFormattedTime = DateTimeStringUtility
                .formatRawTime(mocContext, nonFormatDate);

        assertEquals(formattedTime24hExpected, returnFormattedTime);
    }

    @Test
    public void formatRawTimeMethod12hTimeTest() {

        String formattedTime12hExpected = "3:01:03 PM";
        String nonFormatDate = "Mon Feb 01 15:01:03 CET 2016";

        Context mocContext = Mockito.mock(Context.class);

        PowerMockito
                .stub(PowerMockito.method(DateFormat.class, IS_24_HOUR_FORMAT_METHOD_NAME))
                .toReturn(false);

        String returnFormattedTime = DateTimeStringUtility
                .formatRawTime(mocContext,nonFormatDate);

        assertEquals(formattedTime12hExpected, returnFormattedTime);
    }

    @Test
    public void formatDateTest() {

        String formattedDateExpected = "20-01-2016";;

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("CET"));
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 20);

        Date dateObject = calendar.getTime();

        String formattedDate = DateTimeStringUtility
                .formatDate(dateObject);

        assertEquals(formattedDateExpected, formattedDate);
    }

    @Test
    public void formatStringRawDateTest() {

        String formattedDateExpected = "20-01-2016";
        String nonFormatDate = "Wed Jan 20 15:01:03 CET 2016";

        String formattedDate = DateTimeStringUtility
                .formatStringRawDate(nonFormatDate);

        assertEquals(formattedDateExpected, formattedDate);
    }





}