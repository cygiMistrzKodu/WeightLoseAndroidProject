package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by CygiMasterProgrammer on 2016-01-19.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DateTimeStringUtility.class)
public class DateTimeStringUtilityTest {

    private static final String  IS_24_HOUR_FORMAT_METHOD_NAME = "is24HourFormat";

    @Test
    public void when24hFormatIsSetAnUserChangeTo12hFormatThenTimeIsConvertTo12hFormatTest()  {

        String time24hFormat = "00:00:01";
        String expected12hFormat ="12:00:01 AM";

        Context mockedContext = Mockito.mock(Context.class);

        PowerMockito.stub(PowerMockito.method(DateTimeStringUtility.class,IS_24_HOUR_FORMAT_METHOD_NAME)).toReturn(false);

        String returnTimeFormat = DateTimeStringUtility
                .convertTimeBaseOnDeviceFormat12or24(mockedContext, time24hFormat);

        assertEquals(expected12hFormat, returnTimeFormat);

    }

    @Test
    public void when12hFormatIsSetAnUserChangeTo24hFormatThenTimeIsConvertTo24hFormatTest(){

        String time12hFormat = "3:20:03 PM";
        String expected12hFormat ="15:20:03";

        Context mockedContext = Mockito.mock(Context.class);

        PowerMockito.stub(PowerMockito.method(DateTimeStringUtility.class,IS_24_HOUR_FORMAT_METHOD_NAME)).toReturn(true);

        String returnTimeFormat = DateTimeStringUtility
                .convertTimeBaseOnDeviceFormat12or24(mockedContext, time12hFormat);

        assertEquals(expected12hFormat, returnTimeFormat);

    }

    @Test
    public void whenTimeFormatIs24hAndTimeSetComeIsAlso24HThenShouldReturnSame24hTimeFormat(){

        String time24hFormat = "3:00:03";
        String expected24hFormat ="3:00:03";

        Context mockedContext = Mockito.mock(Context.class);

        PowerMockito.stub(PowerMockito.method(DateTimeStringUtility.class,IS_24_HOUR_FORMAT_METHOD_NAME)).toReturn(true);

        String returnTimeFormat = DateTimeStringUtility
                .convertTimeBaseOnDeviceFormat12or24(mockedContext, time24hFormat);

        assertEquals(expected24hFormat, returnTimeFormat);

    }

    @Test
    public void whenTimeFormatIs12hAndTimeSetComeIsAlso12HThenShouldReturnSame12hTimeFormat(){

        String time12hFormat = "7:07:03 PM";
        String expected12hFormat ="7:07:03 PM";

        Context mockedContext = Mockito.mock(Context.class);

        PowerMockito.stub(PowerMockito.method(DateTimeStringUtility.class,IS_24_HOUR_FORMAT_METHOD_NAME)).toReturn(false);

        String returnTimeFormat = DateTimeStringUtility
                .convertTimeBaseOnDeviceFormat12or24(mockedContext, time12hFormat);

        assertEquals(expected12hFormat, returnTimeFormat);

    }


}