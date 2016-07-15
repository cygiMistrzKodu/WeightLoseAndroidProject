package creator.soft.cygi.com.friendlyloseweighthelper.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import org.apache.commons.lang3.ArrayUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import creator.soft.cygi.com.friendlyloseweighthelper.dto.DateTimeDTO;
import creator.soft.cygi.com.friendlyloseweighthelper.model.WeightDataModel;
import creator.soft.cygi.com.friendlyloseweighthelper.utility.DateTimeStringUtility;

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

        ArrayList<String> dateSeries = new ArrayList<>();
        ArrayList<Float> weight_units = new ArrayList<>();

        SimpleDateFormat dt = DateTimeStringUtility.getDateFormattingPattern(context);
        for (DateTimeDTO dateTimeDTO : weightDataModel.getDatabaseData()){

            Date date = dateTimeDTO.getDate();
            dateSeries.add(dt.format(date));

            Float weight = dateTimeDTO.getWeight();
            weight_units.add(weight);

        }

        float[] weight_measurements = ArrayUtils
                .toPrimitive(weight_units.toArray(new Float[weight_units.size()]));

        Intent i = new Intent(fragmentActivity, ChartActivity.class);
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
