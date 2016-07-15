package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import creator.soft.cygi.com.friendlyloseweighthelper.dto.DateTimeDTO;

/**
 * Created by CygiMasterProgrammer on 2016-01-20.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DateFormat.class,Log.class})
public class DateTimeDTOTest {

    private static final String IS_24_HOUR_FORMAT_METHOD_NAME = "is24HourFormat";
    DateTimeDTO dateTimeDTO;

    @Before
    public void initialize() {

        String rawDateString = "Wed Jan 20 01:22:54 CET 2016";

        Context mocContext = Mockito.mock(Context.class);

        dateTimeDTO = new DateTimeDTO();
        dateTimeDTO.setDate(rawDateString);
        dateTimeDTO.setWeight(100.58f);
        dateTimeDTO.setMeasurementID(34);
        dateTimeDTO.setAndroidContext(mocContext);

        PowerMockito.mockStatic(Log.class);
    }

    @Test
    public void whenGetDateInObjectAreTheSame() {

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("CET"));
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 22);
        calendar.set(Calendar.SECOND, 54);

        Date expectedDate = calendar.getTime();

        Date actualDate = dateTimeDTO.getDate();

        assertEquals(expectedDate.toString(), actualDate.toString());

    }

    @Test
    public void whenGetDateInStringAreTheSame() {

        String expectedRawDate = "Wed Jan 20 01:22:54 CET 2016";

        assertEquals(expectedRawDate, dateTimeDTO.getDateWithoutFormatting());

    }

    @Test
    public void whenGetFormattedDateIsConverted() {

        String expectedFormattedDate = "20-01-2016";
        assertEquals(expectedFormattedDate, dateTimeDTO.getFormattedDate());

    }

    @Test
     public void whenGetFormattedTimeIsConvertedOndDevice24hFormat() {

        String expectedFormatted24HTime = "01:22:54";

        PowerMockito
                .stub(PowerMockito.method(DateFormat.class, IS_24_HOUR_FORMAT_METHOD_NAME))
                .toReturn(true);

        assertEquals(expectedFormatted24HTime, dateTimeDTO.getFormattedTime());
    }

    @Test
    public void whenGetFormattedTimeIsConvertedOndDevice12hFormat() {

        String expectedFormatted12HTime = "1:22:54 AM";

        PowerMockito
                .stub(PowerMockito.method(DateFormat.class, IS_24_HOUR_FORMAT_METHOD_NAME))
                .toReturn(false);

        assertEquals(expectedFormatted12HTime, dateTimeDTO.getFormattedTime());
    }

    @Test
     public void measurementIdSameAsInserted() {
        Integer expectedMeasurementId = 34;
        assertEquals(expectedMeasurementId, dateTimeDTO.getMeasurementID());
    }

    @Test
    public void weightSameAsInserted() {
        Float expectedWeight = 100.58f;
        assertEquals(expectedWeight, dateTimeDTO.getWeight());
    }



}