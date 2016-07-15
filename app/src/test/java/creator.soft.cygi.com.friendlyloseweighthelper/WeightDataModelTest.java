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

import creator.soft.cygi.com.friendlyloseweighthelper.dto.DateTimeDTO;
import creator.soft.cygi.com.friendlyloseweighthelper.model.WeightDataModel;
import creator.soft.cygi.com.friendlyloseweighthelper.model.WeightDataObserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by CygiMasterProgrammer on 2016-01-21.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, WeightDataModel.class})
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

        fillDataModelWithMeasurement(weightDataModel);

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

    @Test
    public void checkReplaceMeasurementDataIsReallyUpdated() {

        String date = "Wed Jan 20 01:22:00 CET 2016";

        WeightDataModel weightDataModel = new WeightDataModel();

        DateTimeDTO dateTimeMeasurementInserted =
                createTimeDateObject(110f, date);

        weightDataModel.setTimeAndDate(dateTimeMeasurementInserted);

        DateTimeDTO dateTimeMeasurementUpdate =
                createTimeDateObject(230f, date);


        weightDataModel.updateMeasurementInModel(dateTimeMeasurementUpdate);

        List<DateTimeDTO> databaseData = weightDataModel.getDatabaseData();

        assertEquals(dateTimeMeasurementUpdate.getWeight(), databaseData.get(0).getWeight());
    }

    @Test
    public void ifNewMeasurementHaveSameDateWithAnyExistingMeasurementAndDifferentMeasurementIDThenReturnFalse() {

        String duplicateDateWithExistingMeasurementDate = "Wed Feb 12 01:10:00 CET 2016";
        Integer newMeasurementId = 6;

        WeightDataModel weightDataModel = new WeightDataModel();
        fillDataModelWithMeasurement(weightDataModel);

        DateTimeDTO dateTimeNewMeasurementAboutToBeInserted =
                createTimeDateObject(500f, duplicateDateWithExistingMeasurementDate);
        dateTimeNewMeasurementAboutToBeInserted.setMeasurementID(newMeasurementId);

        boolean isDateNotRepeated = weightDataModel
                .isDateNotRepeated(dateTimeNewMeasurementAboutToBeInserted);

        assertFalse(isDateNotRepeated);
    }

    @Test
    public void ifNewMeasurementHaveSameDateWithExistingAndSameMeasurementIDThenReturnTrue() {

        String sameDate = "Wed Feb 12 01:10:00 CET 2016";
        Integer sameMeasurementId = 3;

        WeightDataModel weightDataModel = new WeightDataModel();
        fillDataModelWithMeasurement(weightDataModel);

        DateTimeDTO dateTimeNewMeasurementAboutToBeInserted =
                createTimeDateObject(500f, sameDate);
        dateTimeNewMeasurementAboutToBeInserted.setMeasurementID(sameMeasurementId);

        boolean isDateNotRepeated = weightDataModel
                .isDateNotRepeated(dateTimeNewMeasurementAboutToBeInserted);

        assertTrue(isDateNotRepeated);
    }

    @Test
    public void whenDatabaseModelHasNoDataAndPositionIsNullThenReturnNull() {

        WeightDataModel weightDataModelNoData = new WeightDataModel();

        DateTimeDTO dateTimeDTO = weightDataModelNoData.readDataOnLastPosition();

        assertNull(dateTimeDTO);
    }

    @Test
    public void whenDatabaseModelHasDataAndPositionIsNullThenReturnFirstPositionInModel() {

        WeightDataModel weightDataModelWithData = new WeightDataModel();
        fillDataModelWithMeasurement(weightDataModelWithData);

        DateTimeDTO dateTimeDTOReturned = weightDataModelWithData.readDataOnLastPosition();

        DateTimeDTO dateTimeTheLastEntryToDataModelExpected =
                createTimeDateObject(110f, "Wed Jan 20 01:22:00 CET 2016");
        dateTimeTheLastEntryToDataModelExpected.setMeasurementID(4);

        assertEquals(dateTimeTheLastEntryToDataModelExpected.getWeight(), dateTimeDTOReturned.getWeight());
    }

    @Test
    public void whenSaveUserPositionNumberIsBiggerThanNumberOfElementInDataModelThenReturnLastElement() {

        WeightDataModel weightDataWithData = new WeightDataModel();
        fillDataModelWithMeasurement(weightDataWithData);

        Integer userPositionBiggerThanAllElementsCountInDataModel = 50;

        weightDataWithData.setUserPosition(userPositionBiggerThanAllElementsCountInDataModel);

        DateTimeDTO dateTimeDTOReturned = weightDataWithData.readDataOnLastPosition();

        DateTimeDTO dateTimeTheLastEntryToDataModelExpected =
                createTimeDateObject(90f, "Wed Feb 12 01:10:00 CET 2015");
        dateTimeTheLastEntryToDataModelExpected.setMeasurementID(4);

        assertEquals(dateTimeTheLastEntryToDataModelExpected.getWeight(), dateTimeDTOReturned.getWeight());
    }


    @Test
    public void whenGetNextMeasurementThenNextDateTimeMeasurementIsReturnAndPositionIsIncrementedBy1() throws Exception {

        WeightDataModel weightDataWithDataSpy = PowerMockito.spy(new WeightDataModel());
        Integer startUserPosition = 2;
        initializeDataAndSetUserPosition(weightDataWithDataSpy, startUserPosition);

        DateTimeDTO dateTimeExpected =
                createTimeDateObject(90f, "Wed Feb 12 01:10:00 CET 2015");
        dateTimeExpected.setMeasurementID(4);
        Integer expectedPosition = 3;

        DateTimeDTO dateTimeDTOReturned = weightDataWithDataSpy.getNextMeasurement();

        assertEquals(expectedPosition, weightDataWithDataSpy.getUserPosition());
        assertEquals(dateTimeExpected.getWeight(), dateTimeDTOReturned.getWeight());
    }

    @Test
    public void whenGetPreviousMeasurementThenPreviousDateTimeMeasurementIsReturnAndPositionIsDecrementBy1() throws Exception {

        WeightDataModel weightDataWithDataSpy = PowerMockito.spy(new WeightDataModel());
        Integer startUserPosition = 2;
        initializeDataAndSetUserPosition(weightDataWithDataSpy, startUserPosition);

        DateTimeDTO dateTimeExpected =
                createTimeDateObject(230f, "Wed Jan 25 01:22:00 CET 2016");
        dateTimeExpected.setMeasurementID(2);
        Integer expectedPosition = 1;

        DateTimeDTO dateTimeDTOReturned = weightDataWithDataSpy.getPreviousMeasurement();

        assertEquals(expectedPosition, weightDataWithDataSpy.getUserPosition());
        assertEquals(dateTimeExpected.getWeight(), dateTimeDTOReturned.getWeight());
    }

    @Test
    public void whenReachFirstPositionThenAlwaysReturnMeasurementOnThisPosition() throws Exception {

        WeightDataModel weightDataWithDataSpy = PowerMockito.spy(new WeightDataModel());
        Integer startUserPosition = 0;
        initializeDataAndSetUserPosition(weightDataWithDataSpy, startUserPosition);

        DateTimeDTO dateTimeExpected =
                createTimeDateObject(110f, "Wed Jan 20 01:22:00 CET 2016");
        dateTimeExpected.setMeasurementID(1);

        Integer expectedPosition = 0;

        weightDataWithDataSpy.getPreviousMeasurement();
        weightDataWithDataSpy.getPreviousMeasurement();

        DateTimeDTO dateTimeDTOReturned = weightDataWithDataSpy.getPreviousMeasurement();

        assertEquals(expectedPosition, weightDataWithDataSpy.getUserPosition());
        assertEquals(dateTimeExpected.getWeight(), dateTimeDTOReturned.getWeight());
    }

    @Test
    public void whenReachLastPositionThenAlwaysReturnMeasurementOnThisPosition() throws Exception {

        WeightDataModel weightDataWithDataSpy = PowerMockito.spy(new WeightDataModel());
        Integer lastUserPosition = 3;
        initializeDataAndSetUserPosition(weightDataWithDataSpy, lastUserPosition);

        DateTimeDTO dateTimeExpected =
                createTimeDateObject(90f, "Wed Feb 12 01:10:00 CET 2015");
        dateTimeExpected.setMeasurementID(4);

        Integer expectedPosition = 3;

        weightDataWithDataSpy.getNextMeasurement();
        weightDataWithDataSpy.getNextMeasurement();

        DateTimeDTO dateTimeDTOReturned = weightDataWithDataSpy.getNextMeasurement();

        assertEquals(expectedPosition, weightDataWithDataSpy.getUserPosition());
        assertEquals(dateTimeExpected.getWeight(), dateTimeDTOReturned.getWeight());
    }

    @Test
    public void weightDataSubjectPositionTest() throws Exception {

        final Integer[] returnPosition = new Integer[1];

          WeightDataObserver weightDataObserverTest = new WeightDataObserver() {
              @Override
              public void notifyPositionChanged(Integer position) {
                  returnPosition[0] = position;
              }
          };

        WeightDataModel weightDataModel = PowerMockito.spy(new WeightDataModel());
        weightDataModel.addWeightDataObserver(weightDataObserverTest);
        Integer initPosition = 2;
       initializeDataAndSetUserPosition(weightDataModel,initPosition);


        weightDataModel.readDataOnLastPosition();

        Integer expectedPosition = 2;

        assertEquals(expectedPosition, returnPosition[0]);

        weightDataModel.getNextMeasurement();

        expectedPosition = 3;
        assertEquals(expectedPosition, returnPosition[0]);

        weightDataModel.getPreviousMeasurement();
        weightDataModel.getPreviousMeasurement();

        expectedPosition = 1;
        assertEquals(expectedPosition,returnPosition[0]);
    }


    private void initializeDataAndSetUserPosition(WeightDataModel weightDataWithDataSpy, Integer userPosition) throws Exception {
        fillDataModelWithMeasurement(weightDataWithDataSpy);
        weightDataWithDataSpy.setUserPosition(userPosition);

        PowerMockito.doNothing().when(weightDataWithDataSpy, "rememberOnWhatPositionUserFinished");
    }


    private void fillDataModelWithMeasurement(WeightDataModel weightDataModel) {

        DateTimeDTO dateTimeMeasurementOne =
                createTimeDateObject(110f, "Wed Jan 20 01:22:00 CET 2016");
        dateTimeMeasurementOne.setMeasurementID(1);

        weightDataModel.setTimeAndDate(dateTimeMeasurementOne);

        DateTimeDTO dateTimeMeasurementTwo =
                createTimeDateObject(230f, "Wed Jan 25 01:22:00 CET 2016");
        dateTimeMeasurementTwo.setMeasurementID(2);

        weightDataModel.setTimeAndDate(dateTimeMeasurementTwo);

        DateTimeDTO dateTimeMeasurementThree =
                createTimeDateObject(260f, "Wed Feb 12 01:10:00 CET 2016");
        dateTimeMeasurementThree.setMeasurementID(3);

        weightDataModel.setTimeAndDate(dateTimeMeasurementThree);

        DateTimeDTO dateTimeMeasurementFour =
                createTimeDateObject(90f, "Wed Feb 12 01:10:00 CET 2015");
        dateTimeMeasurementFour.setMeasurementID(4);

        weightDataModel.setTimeAndDate(dateTimeMeasurementFour);

    }


    private DateTimeDTO createTimeDateObject(Float weight, String date) {

        DateTimeDTO dateTimeDTO = new DateTimeDTO();
        dateTimeDTO.setWeight(weight);
        dateTimeDTO.setDate(date);
        return dateTimeDTO;
    }


}