package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by CygiMasterProgrammer on 2016-01-17.
 */

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class DateTimeStringUtilityTest {

    private String TAG = "DateTimeStringUtilityTest";

    private Context instrumentationContext;

    @Before
    public void setup() {
        instrumentationContext = InstrumentationRegistry.getContext();
    }

    @Test
    public void convertTimeBaseOnDeviceFormat12or24() {


        // for a sake of example leave here. But not working
        String timeExpected = "00:32:20";

        String time12hFormat = "12:32:20 PM";

        String timeReturn = DateTimeStringUtility.convertTimeBaseOnDeviceFormat12or24(instrumentationContext, time12hFormat);


      assertEquals(timeExpected,timeReturn);
    }

}
