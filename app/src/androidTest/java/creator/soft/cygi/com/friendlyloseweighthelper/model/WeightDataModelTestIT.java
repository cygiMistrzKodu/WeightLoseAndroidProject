package creator.soft.cygi.com.friendlyloseweighthelper.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import creator.soft.cygi.com.friendlyloseweighthelper.view.LoginViewFragment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by CygiMasterProgrammer on 2016-07-23.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class WeightDataModelTestIT {

    private final Context instrumentationContext = InstrumentationRegistry.getTargetContext();
    private final WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);
    private final String userNameManiek = "Maniek";
    private final String userNameEwa = "Ewa";
    private final String userNameWaldek = "Waldek";
    private String TAG = "WeightDataModelTestIT";
    WeightDataModelInvokerHelper weightDataModelInvokerHelper = new WeightDataModelInvokerHelper();

    @Before
    public void prepareDatabaseForTest() {
        fillUserTable();
        fillMeasurementTableForUser(userNameManiek);
        fillMeasurementTableForUser(userNameEwa);
        fillMeasurementTableForUser(userNameWaldek);
    }

    @After
    public void clearDatabaseAfterTest() {

        List<String> userNamesList = Arrays.asList(new String[]{userNameManiek, userNameWaldek, userNameEwa});

        for (String userName : userNamesList) {
            weightTrackDatabaseHelper.setLoginUserName(userName);
            weightTrackDatabaseHelper.deleteCurrentUserAccount();

        }
    }


    @Test
    public void modifyPositionsAreUniqueForEachUserOneCannotChangeAnotherUsersTest() {

        replaceDefaultSharedPreferencesWithNewLoginUserName(instrumentationContext,userNameEwa);

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);
        WeightDataModel weightDataModelUserEwa = getWeightDataModelForUser(weightTrackDatabaseHelper,userNameEwa);

        weightDataModelInvokerHelper.setWeightDataModel(weightDataModelUserEwa);
        weightDataModelInvokerHelper.nextMeasurementInvokeNumber(2);

        int userEwaPosition = weightDataModelUserEwa.getUserPosition();

        replaceDefaultSharedPreferencesWithNewLoginUserName(instrumentationContext,userNameWaldek);

        WeightDataModel weightDataModelUserWaldek = getWeightDataModelForUser(weightTrackDatabaseHelper,userNameWaldek);
        weightDataModelInvokerHelper.setWeightDataModel(weightDataModelUserWaldek);
        weightDataModelInvokerHelper.nextMeasurementInvokeNumber(1);

        int userWaldekPosition = weightDataModelUserWaldek.getUserPosition();

        assertNotEquals(userEwaPosition, userWaldekPosition);
    }

    private WeightDataModel getWeightDataModelForUser(WeightTrackDatabaseHelper weightTrackDatabaseHelper, String userName) {
        weightTrackDatabaseHelper.setLoginUserName(userName);
        return weightTrackDatabaseHelper.getAllWeightDataFromDatabase();
    }

    @NonNull
    private void replaceDefaultSharedPreferencesWithNewLoginUserName(Context context ,String userName) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LoginViewFragment.LOGIN_USER_NAME, userName);
        editor.commit();
    }


    private void fillUserTable() {

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);

        UserData userJanekData = new UserData();
        userJanekData.setName(userNameWaldek);
        userJanekData.setPassword("koparka");
        userJanekData.setEmail("jacek301@gmail.com");

        weightTrackDatabaseHelper.insertNewUserDataIntoDatabase(userJanekData);

        UserData userJacekData = new UserData();
        userJacekData.setName(userNameManiek);
        userJacekData.setPassword("");
        userJacekData.setEmail("jacek301@gmail.com");

        weightTrackDatabaseHelper.insertNewUserDataIntoDatabase(userJacekData);

        UserData userAniaData = new UserData();
        userAniaData.setName(userNameEwa);
        userAniaData.setPassword("Password");
        userAniaData.setEmail("jacek301@gmail.com");
        userAniaData.setWeightGoal(90);

        weightTrackDatabaseHelper.insertNewUserDataIntoDatabase(userAniaData);

    }

    private void fillMeasurementTableForUser(String userName) {
        weightTrackDatabaseHelper.setLoginUserName(userName);
        clearDataInMeasurementTableForCurrentLoginUser();

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

    private void clearDataInMeasurementTableForCurrentLoginUser() {
        weightTrackDatabaseHelper.clearAllMeasurementDataForLoginUser();
    }


}
