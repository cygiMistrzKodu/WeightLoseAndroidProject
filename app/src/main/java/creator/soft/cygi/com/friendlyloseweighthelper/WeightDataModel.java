package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by CygiMasterProgrammer on 2015-12-10.
 */
public class WeightDataModel {

    private static final String USER_POSITION = "user_position";
    private static final String USER_POSITION_PREFERENCES = "user_position_preferences";
    private static String TAG = "WeightDataModel";
    private String latestDate;
    private Float latestWeight;
    private Integer userPosition = null;

    private List<DateTimeDTO> databaseData = new LinkedList<DateTimeDTO>();
    private Context context;


    public WeightDataModel(Context context) {
        this.context = context;
        readLastUserPosition();
    }

    public void setWeightWithCurrentDate(Float weight) {

        Date currentDate = DateTimeStringUtility.getCurrentDate();

        DateTimeDTO dateTimeDTO = new DateTimeDTO();
        dateTimeDTO.setDate(currentDate.toString());
        dateTimeDTO.setWeight(weight);
        databaseData.add(dateTimeDTO);

        setLatestMeasurement(weight, currentDate);

        Log.i(TAG, "current Date: " + currentDate);

    }

    private void setLatestMeasurement(Float weight, Date currentDate) {
        latestWeight = weight;
        latestDate = DateTimeStringUtility.changeToStringRepresentation(currentDate);
    }

    public List<DateTimeDTO> getDatabaseData() {
        return databaseData;
    }

    public String getLatestDate() {
        return latestDate;
    }

    public Float getLatestWeight() {
        return latestWeight;
    }

    public void setTimeAndDate(DateTimeDTO dateTimeDTO) {

        if (isDateSame(dateTimeDTO)) {
            return;
        }

        setLatestMeasurement(dateTimeDTO.getWeight(), dateTimeDTO.getDate());

        databaseData.add(dateTimeDTO);

    }

    private boolean isDateSame(DateTimeDTO dateTimeDTO) {

        if (latestDate == null) {
            return false;
        }

        return latestDate.equals(dateTimeDTO.getDateWithoutFormatting());
    }

    public DateTimeDTO getPreviousMeasurement() {

        DateTimeDTO dateTimeDTO;

        if (userPosition == null) {
            userPosition = getLatestMeasurementPosition();
            dateTimeDTO = databaseData.get(userPosition);
        } else {

            if (userPosition > 0) {
                userPosition--;

            }
            dateTimeDTO = databaseData.get(userPosition);
        }

        rememberOnWhatPositionUserFinished();

        return dateTimeDTO;
    }

    public DateTimeDTO getNextMeasurement() {

        DateTimeDTO dateTimeDTO;

        if (userPosition == null) {
            userPosition = getLatestMeasurementPosition();
            dateTimeDTO = databaseData.get(userPosition);
        } else {
            if (userPosition < getLatestMeasurementPosition()) {
                userPosition++;
            }
            dateTimeDTO = databaseData.get(userPosition);
        }

        rememberOnWhatPositionUserFinished();


        return dateTimeDTO;
    }

    private void rememberOnWhatPositionUserFinished() {

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(USER_POSITION_PREFERENCES, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(USER_POSITION, userPosition);
            editor.commit();


    }

    private void readLastUserPosition() {

            SharedPreferences sharedPreferences =
                    context.getSharedPreferences(USER_POSITION_PREFERENCES, Context.MODE_PRIVATE);
            userPosition = sharedPreferences.getInt(USER_POSITION, getLatestMeasurementPosition());

    }

    private int getLatestMeasurementPosition() {
        return databaseData.size() - 1;
    }

    public DateTimeDTO readDataOnLastPosition() {

        if(getLatestMeasurementPosition() < userPosition){

            userPosition = getLatestMeasurementPosition();
        }

        return databaseData.get(userPosition);
    }
}
