package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import creator.soft.cygi.com.friendlyloseweighthelper.dao.WeightTrackDatabaseHelper;
import creator.soft.cygi.com.friendlyloseweighthelper.dto.DateTimeDTO;
import creator.soft.cygi.com.friendlyloseweighthelper.model.UserData;
import creator.soft.cygi.com.friendlyloseweighthelper.model.WeightDataModel;
import creator.soft.cygi.com.friendlyloseweighthelper.utility.DateTimeStringUtility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

        weightTrackDatabaseHelper.deleteEntryWithLatestDate();

        WeightDataModel weightDataModelAfterDeletion = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();
        List<DateTimeDTO> measurementListAfterDeletingLastMeasurement = weightDataModelAfterDeletion.getDatabaseData();

        assertFalse(measurementListAfterDeletingLastMeasurement.contains(lastMeasurementBeforeDeletion));

    }

    @Test
    public void deleteMeasurementHavingTheLatestDateTest() {

        insertAdditionalMeasurement();

        WeightDataModel weightDataModelBeforeMeasurementWithLatestDateDeletion = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();
        List<DateTimeDTO> measurementListBeforeDeletingMeasurementWithLatestDate = weightDataModelBeforeMeasurementWithLatestDateDeletion.getDatabaseData();

        DateTimeDTO measurementWithLatestDateBeforeDeletion = measurementListBeforeDeletingMeasurementWithLatestDate
                .get(measurementListBeforeDeletingMeasurementWithLatestDate.size() - 1);

        weightTrackDatabaseHelper.deleteEntryWithLatestDate();

        WeightDataModel weightDataModelAfterDeletion = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();
        List<DateTimeDTO> measurementListAfterDeletingMeasurementWithLatestDate = weightDataModelAfterDeletion.getDatabaseData();

        assertFalse(measurementListAfterDeletingMeasurementWithLatestDate.contains(measurementWithLatestDateBeforeDeletion));
    }

    @Test
    public void checkUndoLastMeasurementDeletion() {

        WeightDataModel weightDataModelBeforeDeletion = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();
        List<DateTimeDTO> measurementListBeforeDeletingLastMeasurement = weightDataModelBeforeDeletion.getDatabaseData();

        DateTimeDTO expectedMeasurementRecoverAfterDeletion = measurementListBeforeDeletingLastMeasurement
                .get(measurementListBeforeDeletingLastMeasurement.size() - 1);

        weightTrackDatabaseHelper.deleteEntryWithLatestDate();
        weightTrackDatabaseHelper.undoDeleteLastMeasurement();

        WeightDataModel weightDataModelAfterDeleteAndUndo = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();

        List<DateTimeDTO> measurementListAfterDeletingAndRecovering = weightDataModelAfterDeleteAndUndo.getDatabaseData();

        DateTimeDTO returnedDateTimeDTOMeasurement = measurementListAfterDeletingAndRecovering
                .get(measurementListAfterDeletingAndRecovering.size() - 1);

        assertEquals(expectedMeasurementRecoverAfterDeletion.getWeight(), returnedDateTimeDTOMeasurement.getWeight());
        assertEquals(expectedMeasurementRecoverAfterDeletion.getDateWithoutFormatting(), returnedDateTimeDTOMeasurement.getDateWithoutFormatting());

    }

    @Test
    public void updateMeasurementSortingByDateTest() {

        int updateMeasurementLocation = 1;
        int UpdatedLocationMeasurementAfterInsertAndSortedByDate = 0 ;

        WeightDataModel weightDataModelBeforeUpdate = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();
        List<DateTimeDTO> measurementListBeforeUpdate = weightDataModelBeforeUpdate.getDatabaseData();

        DateTimeDTO dateTimeDTOToModify = measurementListBeforeUpdate
                .get(updateMeasurementLocation);

        DateTimeDTO dateTimeDTOToUpdated = new DateTimeDTO();
        String dateUpdated = "2016-01-29 21:52:06";
        Float weightUpdated = 460f;
        dateTimeDTOToUpdated.setMeasurementID(dateTimeDTOToModify.getMeasurementID());
        dateTimeDTOToUpdated.setDate(dateUpdated);
        dateTimeDTOToUpdated.setWeight(weightUpdated);

        weightTrackDatabaseHelper.updatedMeasurement(dateTimeDTOToUpdated);

        WeightDataModel weightDataModelAfterUpdate = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();
        List<DateTimeDTO> measurementListAfterUpdate = weightDataModelAfterUpdate.getDatabaseData();

        DateTimeDTO dateTimeDTOAfterUpdate = measurementListAfterUpdate.get(UpdatedLocationMeasurementAfterInsertAndSortedByDate);

        assertEquals(dateTimeDTOToUpdated.getMeasurementID(), dateTimeDTOAfterUpdate.getMeasurementID());
        assertEquals(dateTimeDTOToUpdated.getDateWithoutFormatting(), dateTimeDTOAfterUpdate.getDateWithoutFormatting());
        assertEquals(dateTimeDTOToUpdated.getWeight(), dateTimeDTOAfterUpdate.getWeight());

    }

    @Test
    public void writeAndReadUserDataInAndOutDatabaseTest() {

        UserData userJanekDataExpected = new UserData();
        userJanekDataExpected.setName("Janek");
        userJanekDataExpected.setPassword("koparka");

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);
        List<UserData> usersData = weightTrackDatabaseHelper.getUsersData();

        UserData actualUserData = usersData.get(0);

        assertEquals(userJanekDataExpected.getName(), actualUserData.getName());
        assertEquals(userJanekDataExpected.getPassword(), actualUserData.getPassword());
    }

    @Test
    public void whenNoPasswordIsSetThenImplicitlyIsEmptyStringAndWhenPasswordReturnFromDatabaseIsEmptyString() {

        UserData userWithNoPasswordSet = new UserData();
        userWithNoPasswordSet.setName("UserWithNoPasswordSet");
        weightTrackDatabaseHelper.insertNewUserDataIntoDatabase(userWithNoPasswordSet);

        List<UserData> usersDataList = weightTrackDatabaseHelper.getUsersData();

        UserData userFromDatabaseWithNullPassword = usersDataList.get(3);

        String passwordReturn = userFromDatabaseWithNullPassword.getPassword();

        assertEquals("", passwordReturn);
    }

    @Test
    public void whenInsertUserWithWeightGoalThenGoalIsInStorage() {

        UserData userWithGoal = new UserData();
        userWithGoal.setName("UserWithGoal");
        userWithGoal.setWeightGoal(250.50f);

        weightTrackDatabaseHelper.insertNewUserDataIntoDatabase(userWithGoal);

        List<UserData> usersDataList = weightTrackDatabaseHelper.getUsersData();
        UserData userFromStorageWithGoal = usersDataList.get(3);

        assertEquals("UserWithGoal",userFromStorageWithGoal.getName());

        float expectedWeightGoal = 250.50f;
        assertEquals(expectedWeightGoal,userFromStorageWithGoal.getWeightGoal(),0.00f);

    }

    @Test
    public void changeUserWeightGoalTest(){

        UserData userWithGoal = new UserData();
        userWithGoal.setName("UserWithGoal");
        userWithGoal.setWeightGoal(140.20f);

        weightTrackDatabaseHelper.insertNewUserDataIntoDatabase(userWithGoal);

        List<UserData> usersDataList = weightTrackDatabaseHelper.getUsersData();
        UserData userFromStorageWithGoalToUpdate = usersDataList.get(3);

        float newWeightGoal = 78.5f;
        userFromStorageWithGoalToUpdate.setWeightGoal(newWeightGoal);
        weightTrackDatabaseHelper.updateUserData(userFromStorageWithGoalToUpdate);

        UserData UserAfterUpdatedGoal = weightTrackDatabaseHelper.getUserDataById(userFromStorageWithGoalToUpdate.getUserId());

        assertEquals(userWithGoal.getName(),UserAfterUpdatedGoal.getName());
        assertEquals(newWeightGoal,UserAfterUpdatedGoal.getWeightGoal(),0.00f);
    }
    @Test
    public void changeUserWeightGoalWhenOnlyWeightGoalIsSetToCurrentUserTest() {

        Float newWeightGoal = 160f;

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);
        weightTrackDatabaseHelper.setLoginUserName("Janek");

        weightTrackDatabaseHelper.updateWeightGoal(newWeightGoal);

        List<UserData> userDataList = weightTrackDatabaseHelper.getUsersData();
        UserData userInStorageAfterWeightGoalUpdate = userDataList.get(0);

        assertEquals("Janek",userInStorageAfterWeightGoalUpdate.getName());
        assertEquals("koparka",userInStorageAfterWeightGoalUpdate.getPassword());

        assertEquals(newWeightGoal,userInStorageAfterWeightGoalUpdate.getWeightGoal(),0.00);
    }

    @Test
    public void IfTryInsertUserWithNameThatAlreadyExistInStorageThenIsNotInserted(){

        UserData userFirst = new UserData();
        userFirst.setName("Wicek");

        UserData userSecondSameName = new UserData();
        userSecondSameName.setName("Wicek");

        weightTrackDatabaseHelper.insertNewUserDataIntoDatabase(userFirst);
        Long numberUsersIsStorageAfterInsertFirstUser = weightTrackDatabaseHelper.countUsersInStorage();

        weightTrackDatabaseHelper.insertNewUserDataIntoDatabase(userSecondSameName);
        Long numberUsersIsStorageAfterInsertSecondUserWithSameName = weightTrackDatabaseHelper.countUsersInStorage();

        assertEquals(numberUsersIsStorageAfterInsertFirstUser, numberUsersIsStorageAfterInsertSecondUserWithSameName);

    }

    @Test
    public void getWeightGoalOfCurrentUserTest() {

        Float expectedWeightGoal = 90f;

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);
        weightTrackDatabaseHelper.setLoginUserName("Ania");

        Float currentUserWeightGoal = weightTrackDatabaseHelper.getWeightGoalOfCurrentUser();

        assertEquals(expectedWeightGoal,currentUserWeightGoal,0.00);
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
        userJanekData.setPassword("koparka");

        weightTrackDatabaseHelper.insertNewUserDataIntoDatabase(userJanekData);

        UserData userJacekData = new UserData();
        userJacekData.setName("Jacek");
        userJacekData.setPassword("");

        weightTrackDatabaseHelper.insertNewUserDataIntoDatabase(userJacekData);

        UserData userAniaData = new UserData();
        userAniaData.setName("Ania");
        userAniaData.setPassword("Password");
        userAniaData.setWeightGoal(90);

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

    private void insertAdditionalMeasurement() {
        WeightDataModel weightDataModel = new WeightDataModel(instrumentationContext);

        DateTimeDTO dateTimeDTOLatestDateBeforeLastIdPositionInDatabase = new DateTimeDTO();
        dateTimeDTOLatestDateBeforeLastIdPositionInDatabase.setWeight(500f);
        String date = DateTimeStringUtility.getCurrentDateInStringFormattedLikeInDatabase();
        dateTimeDTOLatestDateBeforeLastIdPositionInDatabase.setDate(date);
        weightDataModel.setTimeAndDate(dateTimeDTOLatestDateBeforeLastIdPositionInDatabase);
        weightTrackDatabaseHelper.insertOneMeasurementIntoDatabase(weightDataModel);

        weightDataModel = new WeightDataModel(instrumentationContext);

        DateTimeDTO dateTimeDTO_OldestDateLatestIdPositionInDatabase = new DateTimeDTO();
        dateTimeDTO_OldestDateLatestIdPositionInDatabase.setWeight(222f);
        dateTimeDTO_OldestDateLatestIdPositionInDatabase.setDate("2015-01-29 21:52:06");
        weightDataModel.setTimeAndDate(dateTimeDTO_OldestDateLatestIdPositionInDatabase);

        weightTrackDatabaseHelper.insertOneMeasurementIntoDatabase(weightDataModel);
    }

}
