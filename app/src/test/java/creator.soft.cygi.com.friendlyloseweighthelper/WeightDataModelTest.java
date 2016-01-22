package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by CygiMasterProgrammer on 2016-01-21.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Log.class)
public class WeightDataModelTest {

    private static final String USER_POSITION_PREFERENCES = "user_position_preferences";
    private static final String USER_POSITION = "user_position";

    private Context mocContext;

    @Before
    public void initialize() {
        PowerMockito.mockStatic(Log.class);
        mocContext = Mockito.mock(Context.class);
    }

    @Test
    public void whenThereIsNoPreviousUserPositionThenUserPositionIsNull() {

        Mockito.when(mocContext
                .getSharedPreferences(USER_POSITION_PREFERENCES, Context.MODE_PRIVATE))
                .thenReturn(null);

        WeightDataModel weightDataModel = new WeightDataModel(mocContext);

        Integer expectedUserPosition = null;

        assertEquals(expectedUserPosition, weightDataModel.getUserPosition());

    }

    @Test
    public void whenThereIsSaveUserPositionPreviouslyThenThisPositionIsSet() {

        SharedPreferences mocSharedPreferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(mocSharedPreferences.getInt(USER_POSITION, 0)).thenReturn(5);

        Mockito.when(mocContext
                .getSharedPreferences(USER_POSITION_PREFERENCES, Context.MODE_PRIVATE))
                .thenReturn(mocSharedPreferences);

        WeightDataModel weightDataModel = new WeightDataModel(mocContext);

        Integer expectedUserPosition = 5;

        assertEquals(expectedUserPosition, weightDataModel.getUserPosition());

    }

    @Test
    public void whenWeightIsSetThenDateIsGeneratedAutomaticallyToIt() {

        Float weight = 150f;
        Float expectedWeight = 150f;
        int firstElement = 0;

        WeightDataModel weightDataModel = new WeightDataModel();
        weightDataModel.setWeightWithCurrentDate(weight);

        List<DateTimeDTO> dataMeasurement = weightDataModel.getDatabaseData();

        DateTimeDTO dateTimeDTO = dataMeasurement.get(firstElement);

        Float returnWeight = dateTimeDTO.getWeight();

        assertEquals(expectedWeight, returnWeight);

        String returnDate = dateTimeDTO.getDateWithoutFormatting();
        assertNotNull(returnDate);
    }

    @Test
    public void whenNewMeasurementIsSetThenThisAreSetAsLatest() {

        String date = "Wed Jan 20 01:22:00 CET 2016";

        Float latestWeightInsertion = 127f;
        String latestDateInsertion = new String(date);

        Float expectedLatestWeight = 127f;
        String expectedLatestDateInsertion = new String(date);

        WeightDataModel weightDataModel = new WeightDataModel();

        DateTimeDTO dateTimeDTOFirstMeasurement =
                createTimeDateObject(110f, "Wed Jan 20 01:22:54 CET 2016");
        DateTimeDTO dateTimeDTOSecondMeasurement =
                createTimeDateObject(latestWeightInsertion, latestDateInsertion);

        weightDataModel.setTimeAndDate(dateTimeDTOFirstMeasurement);
        weightDataModel.setTimeAndDate(dateTimeDTOSecondMeasurement);

        Float returnLatestWeight = weightDataModel.getLatestWeight();
        String returnLatestDate = weightDataModel.getLatestDate();

        assertEquals(expectedLatestWeight, returnLatestWeight);
        assertEquals(expectedLatestDateInsertion, returnLatestDate);
    }

    @Test
    public void whenLatestMeasurementHasSameDateAsPreviousThenIsNotSet() {

        String date = "Wed Jan 20 01:22:00 CET 2016";

        WeightDataModel weightDataModel = new WeightDataModel();

        DateTimeDTO dateTimeDTOFirstMeasurement =
                createTimeDateObject(110f, date);
        DateTimeDTO dateTimeDTOMeasurementWithSameDateAsPrevious =
                createTimeDateObject(120f, date);

        weightDataModel.setTimeAndDate(dateTimeDTOFirstMeasurement);
        weightDataModel.setTimeAndDate(dateTimeDTOMeasurementWithSameDateAsPrevious);

        List<DateTimeDTO> databaseData = weightDataModel.getDatabaseData();

        assertFalse(databaseData.contains(dateTimeDTOMeasurementWithSameDateAsPrevious));
    }


    private DateTimeDTO createTimeDateObject(Float weight, String date) {

        DateTimeDTO dateTimeDTO = new DateTimeDTO();
        dateTimeDTO.setWeight(weight);
        dateTimeDTO.setDate(date);
        return dateTimeDTO;
    }


}