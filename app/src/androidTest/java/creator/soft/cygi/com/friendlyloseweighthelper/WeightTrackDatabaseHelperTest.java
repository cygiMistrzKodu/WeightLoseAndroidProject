package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.mock.MockContext;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by CygiMasterProgrammer on 2016-01-17.
 */

import java.util.List;

import static org.junit.Assert.*;

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
    public void checkIfWhenInsertOneMeasurementToDatabaseTheyAreThere() {


        //TODO Czyszcenie bazy istniejacej


        Float expectedWeight = 1450f;

        WeightDataModel weightDataModel = new WeightDataModel(instrumentationContext);
        weightDataModel.setWeightWithCurrentDate(1450f);

        WeightTrackDatabaseHelper weightTrackDatabaseHelper2 = new WeightTrackDatabaseHelper(instrumentationContext);
        weightTrackDatabaseHelper2.deleteDatabase(); // wole zeby czyscil tabele Pomiaru (dorobie potem)


        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(instrumentationContext);
        weightTrackDatabaseHelper.insertOneRecordIntoWeightTrackDatabase(weightDataModel);

       WeightDataModel weightDataModelReturnFromDatabase = new WeightDataModel(instrumentationContext);

        weightDataModelReturnFromDatabase = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();

       List<DateTimeDTO> measurementInDataBase = weightDataModelReturnFromDatabase.getDatabaseData();

        DateTimeDTO returnMeasurementDateTimeDTO = measurementInDataBase.get(0);

        weightTrackDatabaseHelper2.deleteDatabase();  // kasuje baze drugi raz po tescie (to do zmiany będize tylko sie bawie )

        assertEquals(expectedWeight,returnMeasurementDateTimeDTO.getWeight());


    }

}
