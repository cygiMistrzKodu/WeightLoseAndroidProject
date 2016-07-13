package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import org.apache.commons.lang3.ArrayUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by CygiMasterProgrammer on 2016-01-09.
 */
public class ChartHelper {

    private Context context;
    private FragmentActivity fragmentActivity;
    private WeightDataModel weightDataModel;
    public static final String WEIGHT_DATA = "weightTimeData";
    public static final String DATE_DATA = "dateData";
    public static final String WEIGHT_GOAL_DATA = "weightGoalData";
    Fragment fragment;
    private Float weightGoal;

    public void displayChart(){

        Intent i = new Intent(fragmentActivity, ChartActivity.class);

        ArrayList<String> dateSeries = new ArrayList<>();
        ArrayList<Float> weight_units = new ArrayList<>();


        List<DateTimeDTO> dateWeightMeasurementListSortedByDate = new LinkedList<>();
        for (DateTimeDTO dateTimeDTO : weightDataModel.getDatabaseData()){
            dateWeightMeasurementListSortedByDate.add(dateTimeDTO);
        }

        Collections.sort(dateWeightMeasurementListSortedByDate, new Comparator<DateTimeDTO>() {
            @Override
            public int compare(DateTimeDTO dt1, DateTimeDTO dt2) {
                return dt1.getDate().compareTo(dt2.getDate());
            }
        });


        SimpleDateFormat dt = DateTimeStringUtility.getDateFormattingPattern(context);
        for (DateTimeDTO dateTimeDTO : dateWeightMeasurementListSortedByDate){

            Date date = dateTimeDTO.getDate();
            dateSeries.add(dt.format(date));

            Float weight = dateTimeDTO.getWeight();
            weight_units.add(weight);

        }

        float[] weight_measurements = ArrayUtils
                .toPrimitive(weight_units.toArray(new Float[weight_units.size()]));

        i.putExtra(WEIGHT_DATA, weight_measurements);
        i.putExtra(DATE_DATA, dateSeries);
        i.putExtra(WEIGHT_GOAL_DATA,weightGoal);

       fragment.startActivity(i);


    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setFragmentActivity(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    public void setWeightDataModel(WeightDataModel weightDataModel) {
        this.weightDataModel = weightDataModel;
    }

    public Float getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(Float weightGoal) {
        this.weightGoal = weightGoal;
    }
}
