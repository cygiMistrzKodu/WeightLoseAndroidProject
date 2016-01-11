package creator.soft.cygi.com.friendlyloseweighthelper;

import android.util.Log;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by CygiMasterProgrammer on 2015-12-10.
 */
public class WeightDataModel {

    private static String TAG = "WeightDataModel";
    private String latestDate;
    private Float latestWeight;

    private List<DateTimeDTO> databaseData = new LinkedList<DateTimeDTO>();


    public void setWeightWithCurrentDate(Float weight){

        Date currentDate =  DateTimeStringUtility.getCurrentDate();

        DateTimeDTO dateTimeDTO = new DateTimeDTO();
        dateTimeDTO.setDate(currentDate.toString());
        dateTimeDTO.setWeight(weight);
        databaseData.add(dateTimeDTO);

        setLatestMeasurement(weight, currentDate);

        Log.i(TAG, "current Date: " + currentDate);

    }

    private void setLatestMeasurement(Float weight, Date currentDate) {
        latestWeight = weight;
        latestDate =  DateTimeStringUtility.changeToStringRepresentation(currentDate);
    }

    public List<DateTimeDTO> getDatabaseData(){
        return databaseData;
    }

    public String getLatestDate(){
        return latestDate;
    }

    public Float getLatestWeight() {
        return latestWeight;
    }

    public void setTimeAndDate(DateTimeDTO dateTimeDTO){

        if(isDateSame(dateTimeDTO)){
            return;
        }

       setLatestMeasurement(dateTimeDTO.getWeight(), dateTimeDTO.getDate());

        databaseData.add(dateTimeDTO);

    }

    private boolean isDateSame(DateTimeDTO dateTimeDTO) {

        if(latestDate == null){
            return false;
        }

        return latestDate.equals(dateTimeDTO.getDateWithoutFormatting());
    }


}
