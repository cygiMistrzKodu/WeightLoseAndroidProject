package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by CygiMasterProgrammer on 2016-01-21.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Log.class)
public class WeightDataModelTest {

    private static final String USER_POSITION_PREFERENCES = "user_position_preferences";
    private static final String USER_POSITION = "user_position";

    @Test
    public void whenThereIsNoPreviousUserPositionThenUserPositionIsNull() {

        PowerMockito.mockStatic(Log.class);

        Context mocContext = Mockito.mock(Context.class);

        Mockito.when(mocContext
                .getSharedPreferences(USER_POSITION_PREFERENCES, Context.MODE_PRIVATE))
                .thenReturn(null);

        WeightDataModel weightDataModel = new WeightDataModel(mocContext);

        Integer expectedUserPosition = null;

        assertEquals(expectedUserPosition, weightDataModel.getUserPosition());

    }

    @Test
    public void whenThereIsSaveUserPositionPreviouslyThenThisPositionIsSet() {

        PowerMockito.mockStatic(Log.class);

        Context mocContext = Mockito.mock(Context.class);

        SharedPreferences mocSharedPreferences = Mockito.mock(SharedPreferences.class);
        Mockito.when(mocSharedPreferences.getInt(USER_POSITION,0)).thenReturn(5);

        Mockito.when(mocContext
                .getSharedPreferences(USER_POSITION_PREFERENCES, Context.MODE_PRIVATE))
                .thenReturn(mocSharedPreferences);

        WeightDataModel weightDataModel = new WeightDataModel(mocContext);

        Integer expectedUserPosition = 5;

        assertEquals(expectedUserPosition, weightDataModel.getUserPosition());

    }

}