package creator.soft.cygi.com.friendlyloseweighthelper;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by CygiMasterProgrammer on 2015-12-10.
 */
public class WeightData {

    private static String TAG = "WeightData";
    private Map<Date,Float> timeWeightPair = new LinkedHashMap<Date,Float>();
    private String latestDate;
    private Float latestWeight;


    public void setWeightWithCurrentDate(Float weight){

        Calendar cal = Calendar.getInstance();
        Date currentDate =  cal.getTime();
        timeWeightPair.put(currentDate, weight);

        setLatestMeasurement(weight, currentDate);

        Log.i(TAG, "current Date: " + currentDate);

    }

    private void setLatestMeasurement(Float weight, Date currentDate) {
        latestWeight = weight;
        latestDate =  currentDate.toString();
    }

    public Map<Date,Float> getWeightAndTimeData() {
        return timeWeightPair;
    }

    public String getLatestDate(){
        return latestDate;
    }

    public Float getLatestWeight() {
        return latestWeight;
    }

    public void setTimeAndDate(DateTimeDTO dateTimeDTO){

        timeWeightPair.put(dateTimeDTO.getDate(),dateTimeDTO.getWeight());

    }










}
