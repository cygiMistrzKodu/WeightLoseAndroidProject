package creator.soft.cygi.com.friendlyloseweighthelper;

import android.nfc.Tag;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CygiMasterProgrammer on 2015-12-10.
 */
public class WeightData {

    private static String TAG = "WeightData";
    private Map<Date,Integer> timeWeightPair = new HashMap<Date,Integer>();


    public void setWeightWithCurrentDate(Integer weight){

        Calendar cal = Calendar.getInstance();
        Date currentDate =  cal.getTime();

        Log.i(TAG,"current Date: "+ currentDate);

        timeWeightPair.put(currentDate,weight);

    }


}
