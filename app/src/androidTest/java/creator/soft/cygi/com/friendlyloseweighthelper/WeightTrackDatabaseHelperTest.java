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

    private String TAG = "WeightTrackDatabaseHelperTest";

    private Context instrumentationContext;

    @Before
    public void setup() {
        instrumentationContext = InstrumentationRegistry.getTargetContext();

    }

    @Test
    public void checkInsertionToDatabase() {

        clearDataInMeasurementTable();

        Float expectedWeight = 1600f;

        WeightDataModel weightDataModel = new WeightDataModel(instrumentationContext);
        weightDataModel.setWeightWithCurrentDate(1600f);

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);
        weightTrackDatabaseHelper.insertOneRecordIntoWeightTrackDatabase(weightDataModel);
        WeightDataModel weightDataModelReturnFromDatabase = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();
        List<DateTimeDTO> measurementInDataBase = weightDataModelReturnFromDatabase.getDatabaseData();
        DateTimeDTO returnMeasurementDateTimeDTO = measurementInDataBase.get(0);

        assertEquals(expectedWeight, returnMeasurementDateTimeDTO.getWeight());

    }

    @Test
    public void checkWhenMeasurementClearThenMeasurementTableIsEmpty() {
        fillDatabase();
        clearDataInMeasurementTable();

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);

        assertTrue(weightTrackDatabaseHelper.isMeasurementTableEmpty());
    }

    @Test
    public void insertThreeMeasurementThenCountIsAlsoThree() {
        clearDataInMeasurementTable();
        fillDatabase();
        Long expectedCount = 3l;
        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);

        assertEquals(expectedCount, weightTrackDatabaseHelper.numberOfMeasurementDataForCurrentUser());
    }

    @Test
    public void checkLastMeasurementDeletion() {

        clearDataInMeasurementTable();
        fillDatabase();
        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);

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
    public void checkUndoLastLastMeasurementDeletion() {

        clearDataInMeasurementTable();
        fillDatabase();

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);
        WeightDataModel weightDataModelBeforeDeletion = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();
        List<DateTimeDTO> measurementListBeforeDeletingLastMeasurement = weightDataModelBeforeDeletion.getDatabaseData();

        DateTimeDTO expectedMeasurementRecoverAfterDeletion = measurementListBeforeDeletingLastMeasurement
                .get(measurementListBeforeDeletingLastMeasurement.size() - 1);

        weightTrackDatabaseHelper.deleteLatestEntry();
        weightTrackDatabaseHelper.undoDeleteLastMeasurement();

        WeightDataModel weightDataModelAfterDeleteAndUndo= weightTrackDatabaseHelper.getAllWeightDataFromDatabase();

        List<DateTimeDTO> measurementListAfterDeletingAndRecovering = weightDataModelAfterDeleteAndUndo.getDatabaseData();

        DateTimeDTO returnedDateTimeDTOMeasurement = measurementListAfterDeletingAndRecovering
                .get(measurementListAfterDeletingAndRecovering.size() - 1);

        assertEquals(expectedMeasurementRecoverAfterDeletion.getWeight(),returnedDateTimeDTOMeasurement.getWeight());
        assertEquals(expectedMeasurementRecoverAfterDeletion.getDateWithoutFormatting(),returnedDateTimeDTOMeasurement.getDateWithoutFormatting());

    }

    @Test
    public void updateMeasurementTest() {

        clearDataInMeasurementTable();
        fillDatabase();

        int UpdateMeasurementLocation = 1;

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);
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

        assertEquals(dateTimeDTOToUpdated.getMeasurementID(),dateTimeDTOAfterUpdate.getMeasurementID());
        assertEquals(dateTimeDTOToUpdated.getDateWithoutFormatting(),dateTimeDTOAfterUpdate.getDateWithoutFormatting());
        assertEquals(dateTimeDTOToUpdated.getWeight(),dateTimeDTOAfterUpdate.getWeight());

    }



    private void clearDataInMeasurementTable() {
        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);
        weightTrackDatabaseHelper.clearAllMeasurementData();
    }

    private void fillDatabase() {

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);

        clearDataInMeasurementTable();

        try {

            WeightDataModel weightDataModel = new WeightDataModel(instrumentationContext);
            weightDataModel.setWeightWithCurrentDate(120f);
            weightTrackDatabaseHelper.insertOneRecordIntoWeightTrackDatabase(weightDataModel);
            Thread.sleep(1000);
            weightDataModel = new WeightDataModel(instrumentationContext);
            weightDataModel.setWeightWithCurrentDate(130f);
            weightTrackDatabaseHelper.insertOneRecordIntoWeightTrackDatabase(weightDataModel);
            Thread.sleep(1000);
            weightDataModel.setWeightWithCurrentDate(155f);
            weightTrackDatabaseHelper.insertOneRecordIntoWeightTrackDatabase(weightDataModel);
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
