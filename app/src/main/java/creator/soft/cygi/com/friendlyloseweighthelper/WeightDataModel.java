package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by CygiMasterProgrammer on 2015-12-10.
 */
public class WeightDataModel implements WeightDataSubject {

    private static final String USER_POSITION = "user_position";
    private static final String USER_POSITION_PREFERENCES = "user_position_preferences";
    private static String TAG = "WeightDataModel";
    private String latestDate;
    private Float latestWeight;
    private Integer userPosition = null;

    private List<DateTimeDTO> databaseData = new LinkedList<DateTimeDTO>();
    private Context context;

    private List<WeightDataObserver> weightDataModelObservers = new ArrayList<WeightDataObserver>();

    public WeightDataModel() {
        super();
    }

    public WeightDataModel(Context context) {
        this.context = context;
        readLastUserPosition();
    }

    public Integer getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(Integer userPosition) {
        this.userPosition = userPosition;
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

        if (isLatestDateInModelSameWithCurrentDate(dateTimeDTO)) {
            return;
        }

        setLatestMeasurement(dateTimeDTO.getWeight(), dateTimeDTO.getDate());

        databaseData.add(dateTimeDTO);
        if (userPosition == null) {
            userPosition = getLatestMeasurementPosition();
        }

    }

    private boolean isLatestDateInModelSameWithCurrentDate(DateTimeDTO dateTimeDTO) {

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
        notifyPositionChanged();

        return dateTimeDTO;
    }

    public void updateMeasurementInModel(DateTimeDTO dateTimeDTO) {

        databaseData.set(userPosition, dateTimeDTO);
        sortByDate();
        userPosition = databaseData.indexOf(dateTimeDTO);
        notifyPositionChanged();

    }

    public boolean isDateNotRepeated(DateTimeDTO updatedDateTimeDto) {

        for (DateTimeDTO dateTimeDTO : databaseData) {

            if (!dateTimeDTO.getMeasurementID().equals(updatedDateTimeDto.getMeasurementID())) {
                if (dateTimeDTO.getDateWithoutFormatting().equals(updatedDateTimeDto.getDateWithoutFormatting())) {
                    return false;
                }
            }
        }

        return true;
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
        notifyPositionChanged();

        return dateTimeDTO;
    }

    public void sortByDate(){
        Collections.sort(databaseData,new DateSorter());
    }

    private void rememberOnWhatPositionUserFinished() {

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(USER_POSITION_PREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(USER_POSITION, userPosition);
        editor.commit();


    }

    private void readLastUserPosition() {


        Log.d(TAG, "readLastUserPosition: " + userPosition);

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(USER_POSITION_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
           userPosition = sharedPreferences.getInt(USER_POSITION, 0);
        }

        Log.d(TAG, "readLastUserPosition After Read from preferences: " + userPosition);

    }

    private int getLatestMeasurementPosition() {
        return databaseData.size() - 1;
    }

    public DateTimeDTO readDataOnLastPosition() {

        DateTimeDTO dateTimeDTO = null;

        if(databaseData.isEmpty()){
            return dateTimeDTO;
        }

        if (getLatestMeasurementPosition() < userPosition) {

            userPosition = getLatestMeasurementPosition();
            Log.d(TAG, "User Position first IF: " + userPosition);
        }

        notifyPositionChanged();

        Log.d(TAG, "User position: " + userPosition);

        dateTimeDTO = databaseData.get(userPosition);

        return dateTimeDTO;
    }

    public boolean isDatabaseIsEmpty() {

        if (databaseData.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void addWeightDataObserver(WeightDataObserver weightDataObserver) {

        weightDataModelObservers.add(weightDataObserver);
    }

    @Override
    public void removeWightDataObserver(WeightDataObserver weightDataObserver) {

        weightDataModelObservers.remove(weightDataObserver);
    }

    @Override
    public void notifyPositionChanged() {

        for (WeightDataObserver weightDataObserver : weightDataModelObservers) {

            weightDataObserver.notifyPositionChanged(userPosition);
        }

    }


}
