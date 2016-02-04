package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by CygiMasterProgrammer on 2016-01-17.
 */

@RunWith(AndroidJUnit4.class)
@MediumTest
public class WeightTrackDatabaseHelperTest {

    private final Context instrumentationContext = InstrumentationRegistry.getTargetContext();
    private final WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);
    private String TAG = "WeightTrackDatabaseHelperTest";

    @Before
    public void setup() {
        weightTrackDatabaseHelper.setLoginUserName("Jacek");
        clearDatabase();
        fillDatabase();

    }

    @Test
    public void checkInsertionToDatabase() {

        clearDataInMeasurementTable();

        Float expectedWeight = 1600f;

        WeightDataModel weightDataModel = new WeightDataModel(instrumentationContext);
        weightDataModel.setWeightWithCurrentDate(1600f);

        weightTrackDatabaseHelper.insertOneMeasurementIntoDatabase(weightDataModel);
        WeightDataModel weightDataModelReturnFromDatabase = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();
        List<DateTimeDTO> measurementInDataBase = weightDataModelReturnFromDatabase.getDatabaseData();
        DateTimeDTO returnMeasurementDateTimeDTO = measurementInDataBase.get(0);

        assertEquals(expectedWeight, returnMeasurementDateTimeDTO.getWeight());

    }

    @Test
    public void checkWhenMeasurementClearThenMeasurementTableIsEmpty() {

        fillMeasurementTable();
        clearDataInMeasurementTable();

        assertTrue(weightTrackDatabaseHelper.isMeasurementTableEmpty());
    }

    @Test
    public void insertThreeMeasurementThenCountIsAlsoThree() {
        Long expectedCount = 3l;

        assertEquals(expectedCount, weightTrackDatabaseHelper.numberOfMeasurementDataForCurrentUser());
    }

    @Test
    public void checkLastMeasurementDeletion() {

        WeightDataModel weightDataModelBeforeDeletion = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();
        List<DateTimeDTO> measurementListBeforeDeletingLastMeasurement = weightDataModelBeforeDeletion.getDatabaseData();

        DateTimeDTO lastMeasurementBeforeDeletion = measurementListBeforeDeletingLastMeasurement
                .get(measurementListBeforeDeletingLastMeasurement.size() - 1);

        weightTrackDatabaseHelper.deleteLatestEntry();

        WeightDataModel weightDataModelAfterDeletion = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();
        List<DateTimeDTO> measurementListAfterDeletingLastMeasurement = weightDataModelAfterDeletion.getDatabaseData();

        assertFalse(measurementListAfterDeletingLastMeasurement.contains(lastMeasurementBeforeDeletion));

    }

    @Test
    public void checkUndoLastMeasurementDeletion() {

        WeightDataModel weightDataModelBeforeDeletion = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();
        List<DateTimeDTO> measurementListBeforeDeletingLastMeasurement = weightDataModelBeforeDeletion.getDatabaseData();

        DateTimeDTO expectedMeasurementRecoverAfterDeletion = measurementListBeforeDeletingLastMeasurement
                .get(measurementListBeforeDeletingLastMeasurement.size() - 1);

        weightTrackDatabaseHelper.deleteLatestEntry();
        weightTrackDatabaseHelper.undoDeleteLastMeasurement();

        WeightDataModel weightDataModelAfterDeleteAndUndo = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();

        List<DateTimeDTO> measurementListAfterDeletingAndRecovering = weightDataModelAfterDeleteAndUndo.getDatabaseData();

        DateTimeDTO returnedDateTimeDTOMeasurement = measurementListAfterDeletingAndRecovering
                .get(measurementListAfterDeletingAndRecovering.size() - 1);

        assertEquals(expectedMeasurementRecoverAfterDeletion.getWeight(), returnedDateTimeDTOMeasurement.getWeight());
        assertEquals(expectedMeasurementRecoverAfterDeletion.getDateWithoutFormatting(), returnedDateTimeDTOMeasurement.getDateWithoutFormatting());

    }

    @Test
    public void updateMeasurementTest() {

        int UpdateMeasurementLocation = 1;

        WeightDataModel weightDataModelBeforeUpdate = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();
        List<DateTimeDTO> measurementListBeforeUpdate = weightDataModelBeforeUpdate.getDatabaseData();

        DateTimeDTO dateTimeDTOToModify = measurementListBeforeUpdate
                .get(UpdateMeasurementLocation);

        DateTimeDTO dateTimeDTOToUpdated = new DateTimeDTO();
        String dateUpdated = "Fri Jan 29 21:52:06 GMT 2016";
        Float weightUpdated = 460f;
        dateTimeDTOToUpdated.setMeasurementID(dateTimeDTOToModify.getMeasurementID());
        dateTimeDTOToUpdated.setDate(dateUpdated);
        dateTimeDTOToUpdated.setWeight(weightUpdated);

        weightTrackDatabaseHelper.updatedMeasurement(dateTimeDTOToUpdated);

        WeightDataModel weightDataModelAfterUpdate = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();
        List<DateTimeDTO> measurementListAfterUpdate = weightDataModelAfterUpdate.getDatabaseData();

        DateTimeDTO dateTimeDTOAfterUpdate = measurementListAfterUpdate.get(UpdateMeasurementLocation);

        assertEquals(dateTimeDTOToUpdated.getMeasurementID(), dateTimeDTOAfterUpdate.getMeasurementID());
        assertEquals(dateTimeDTOToUpdated.getDateWithoutFormatting(), dateTimeDTOAfterUpdate.getDateWithoutFormatting());
        assertEquals(dateTimeDTOToUpdated.getWeight(), dateTimeDTOAfterUpdate.getWeight());

    }

    @Test
    public void writeAndReadUserDataInAndOutDatabaseTest() {

        UserData userJanekDataExpected = new UserData();
        userJanekDataExpected.setName("Janek");
        userJanekDataExpected.setPassword("Koparka");

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);
        List<UserData> usersData = weightTrackDatabaseHelper.getUsersData();

        UserData actualUserData = usersData.get(0);

        assertEquals(userJanekDataExpected.getName(), actualUserData.getName());
        assertEquals(userJanekDataExpected.getPassword(), actualUserData.getPassword());
    }

    private void fillDatabase() {
        fillUserTable();
        fillMeasurementTable();
    }

    private void clearDatabase() {

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);
        weightTrackDatabaseHelper.clearDatabase();
    }

    private void fillUserTable() {

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);

        UserData userJanekData = new UserData();
        userJanekData.setName("Janek");
        userJanekData.setPassword("Koparka");

        weightTrackDatabaseHelper.insertNewUserDataIntoDatabase(userJanekData);

        UserData userJacekData = new UserData();
        userJacekData.setName("Jacek");
        userJacekData.setPassword("");

        weightTrackDatabaseHelper.insertNewUserDataIntoDatabase(userJacekData);

        UserData userAniaData = new UserData();
        userAniaData.setName("Ania");
        userAniaData.setPassword("Password");

        weightTrackDatabaseHelper.insertNewUserDataIntoDatabase(userAniaData);

    }


    private void clearDataInMeasurementTable() {
        weightTrackDatabaseHelper.clearAllMeasurementDataForLoginUser();
    }

    private void fillMeasurementTable() {
        clearDataInMeasurementTable();

        try {

            WeightDataModel weightDataModel = new WeightDataModel(instrumentationContext);
            weightDataModel.setWeightWithCurrentDate(120f);
            weightTrackDatabaseHelper.insertOneMeasurementIntoDatabase(weightDataModel);
            Thread.sleep(1000);
            weightDataModel = new WeightDataModel(instrumentationContext);
            weightDataModel.setWeightWithCurrentDate(130f);
            weightTrackDatabaseHelper.insertOneMeasurementIntoDatabase(weightDataModel);
            Thread.sleep(1000);
            weightDataModel.setWeightWithCurrentDate(155f);
            weightTrackDatabaseHelper.insertOneMeasurementIntoDatabase(weightDataModel);
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
