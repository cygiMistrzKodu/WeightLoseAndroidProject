package creator.soft.cygi.com.friendlyloseweighthelper.utility;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import creator.soft.cygi.com.friendlyloseweighthelper.dao.WeightTrackDatabaseHelper;
import creator.soft.cygi.com.friendlyloseweighthelper.model.UserData;
import creator.soft.cygi.com.friendlyloseweighthelper.model.WeightDataModel;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class ProgressMeasurerTestIT {

    private final Context instrumentationContext = InstrumentationRegistry.getTargetContext();
    private final String userNameManiek = "Maniek";


    private WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);

    @Before
    public void prepareDatabaseForTest() {
        fillUserTable();
        fillMeasurementTableForUser(userNameManiek);

    }

    @After
    public void clearDatabaseAfterTest() {

        List<String> userNamesList = Arrays.asList(new String[]{userNameManiek});

        for (String userName : userNamesList) {
            weightTrackDatabaseHelper.setLoginUserName(userName);
            weightTrackDatabaseHelper.deleteCurrentUserAccount();

        }
    }


    @Test
    public void checkIfTheHighestMeasurementIsChoose() {

        ProgressMeasurer progressMeasurer = new ProgressMeasurer();
        progressMeasurer.setWeightTrackDatabaseHelper(weightTrackDatabaseHelper);
        progressMeasurer.makeCalculation();

        float expectedValue = 155.68f;
        float actualHighestWeightMeasurement = progressMeasurer.getTheHighestWeightMeasurement().getWeight();

        assertEquals(expectedValue, actualHighestWeightMeasurement);


    }

    @Test
    public void howManyMeasurementCountFromHighestWeightToLatestAndWhenTheHighestMoreThanOneThenTheOldestIsTakenTest() {

        ProgressMeasurer progressMeasurer = new ProgressMeasurer();
        progressMeasurer.setWeightTrackDatabaseHelper(weightTrackDatabaseHelper);
        progressMeasurer.makeCalculation();

        long actualNumberOfMeasurement = progressMeasurer.getNumberOfMeasurementFromHighestMeasurementToLatestMeasurementIncludedHighest();

        long expectedCount = 5;


        assertEquals(expectedCount, actualNumberOfMeasurement);
    }

    @Test
    public void whatHappensWhenThereIsNoDataInMeasurementTable() {

        weightTrackDatabaseHelper.clearAllDataInMeasurementTable();

        ProgressMeasurer progressMeasurer = new ProgressMeasurer();
        progressMeasurer.setWeightTrackDatabaseHelper(weightTrackDatabaseHelper);
        progressMeasurer.makeCalculation();

        long actualNumberOfMeasurement = progressMeasurer.getNumberOfMeasurementFromHighestMeasurementToLatestMeasurementIncludedHighest();
        long expectedCount = 0;

        assertEquals(expectedCount, actualNumberOfMeasurement);
    }


    private void fillUserTable() {

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);

        UserData userJacekData = new UserData();
        userJacekData.setName(userNameManiek);
        userJacekData.setPassword("");
        userJacekData.setEmail("jacek301@gmail.com");

        weightTrackDatabaseHelper.insertNewUserDataIntoDatabase(userJacekData);

    }

    private void fillMeasurementTableForUser(String userName) {
        weightTrackDatabaseHelper.setLoginUserName(userName);
        clearDataInMeasurementTableForCurrentLoginUser();

        try {

            WeightDataModel weightDataModel = new WeightDataModel(instrumentationContext);
            weightDataModel.setWeightWithCurrentDate(120.78f);
            weightTrackDatabaseHelper.insertOneMeasurementIntoDatabase(weightDataModel);
            Thread.sleep(1000);
            weightDataModel = new WeightDataModel(instrumentationContext);
            weightDataModel.setWeightWithCurrentDate(155.68f);
            weightTrackDatabaseHelper.insertOneMeasurementIntoDatabase(weightDataModel);
            Thread.sleep(1000);
            weightDataModel.setWeightWithCurrentDate(136.68f);
            weightTrackDatabaseHelper.insertOneMeasurementIntoDatabase(weightDataModel);
            Thread.sleep(1000);
            weightDataModel.setWeightWithCurrentDate(113.68f);
            weightTrackDatabaseHelper.insertOneMeasurementIntoDatabase(weightDataModel);
            Thread.sleep(1000);
            weightDataModel.setWeightWithCurrentDate(155.68f);
            weightTrackDatabaseHelper.insertOneMeasurementIntoDatabase(weightDataModel);
            Thread.sleep(1000);
            weightDataModel.setWeightWithCurrentDate(143.68f);
            weightTrackDatabaseHelper.insertOneMeasurementIntoDatabase(weightDataModel);
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void clearDataInMeasurementTableForCurrentLoginUser() {
        weightTrackDatabaseHelper.clearAllMeasurementDataForLoginUser();
    }

}
