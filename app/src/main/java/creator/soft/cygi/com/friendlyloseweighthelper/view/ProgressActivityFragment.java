package creator.soft.cygi.com.friendlyloseweighthelper.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import creator.soft.cygi.com.friendlyloseweighthelper.R;
import creator.soft.cygi.com.friendlyloseweighthelper.dao.WeightTrackDatabaseHelper;
import creator.soft.cygi.com.friendlyloseweighthelper.utility.ProgressMeasurer;

public class ProgressActivityFragment extends Fragment {

    private TextView progressViewTitle;
    private TextView userName;
    private TextView userNameWeightGoal;
    private TextView averageDailyWeightChangeFromBeginning;
    private TextView averageDallyWeightChangeValueFromHighestWeightMeasurement;
    private ImageView progressArrowIndicatorFromBeginning;
    private TextView daysLeftToAchieveGoal;
    private TextView predictedDateOfAchieveGoal;
    private TextView weightChangeFromBeginning;
    private TextView weightDifferenceFromHighestMeasurement;
    private TextView numberOfMeasurementMadeFromBeginning;
    private TextView numberOfMeasurementMadeFromHighestWeight;
    private TextView firstWeight;
    private TextView maxWeight;
    private TextView currentWeight;
    private Button records;
    private Button statistic;
    private WeightTrackDatabaseHelper weightTrackDatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.progress_view, null);

        progressViewTitle = (TextView) view.findViewById(R.id.progressViewTittle);
        weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getContext());

        userName = (TextView) view.findViewById(R.id.progressUserName);
        userName.setText(weightTrackDatabaseHelper.getLoginUserName());


        userNameWeightGoal = (TextView) view.findViewById(R.id.progressWeightGoal);
        userNameWeightGoal.setText(String.format(getString(R.string.progress_weight_goal), weightTrackDatabaseHelper.getWeightGoalOfCurrentUser()));

        averageDailyWeightChangeFromBeginning = (TextView) view.findViewById(R.id.progressAverageDailyWeightChangeFromBegining);
        averageDallyWeightChangeValueFromHighestWeightMeasurement = (TextView) view.findViewById(R.id.progressAvgDailyWeightChangeFromHighestWeight);
        progressArrowIndicatorFromBeginning = (ImageView) view.findViewById(R.id.progressArrowIndicatorFromBegining);
        daysLeftToAchieveGoal = (TextView) view.findViewById(R.id.progressHowManyDaysLeftToAchieveGoal);
        predictedDateOfAchieveGoal = (TextView) view.findViewById(R.id.progressDateOfAchievingAGoal);
        weightChangeFromBeginning = (TextView) view.findViewById(R.id.progressOverallWeightChangeFromFirstToLastMeasurement);
        weightDifferenceFromHighestMeasurement = (TextView) view.findViewById(R.id.progressWeightDifferenceFromHighestWeight);
        numberOfMeasurementMadeFromBeginning = (TextView) view.findViewById(R.id.progressNumberOfMeasurementMadeFromBegining);
        numberOfMeasurementMadeFromHighestWeight = (TextView) view.findViewById(R.id.progressNumberOfMeasurementMadeFromHighestWeight);
        firstWeight = (TextView) view.findViewById(R.id.progressFirstWeight);
        maxWeight = (TextView) view.findViewById(R.id.progressMaxWeight);
        currentWeight = (TextView) view.findViewById(R.id.progressCurrentWeight);
//        records = (Button) view.findViewById(R.id.progressRecordButton);
//        statistic = (Button) view.findViewById(R.id.progressStatisticButton);


        ProgressMeasurer progressMeasurer = new ProgressMeasurer();
        progressMeasurer.setWeightTrackDatabaseHelper(weightTrackDatabaseHelper);
        progressMeasurer.makeCalculation();

        averageDallyWeightChangeValueFromHighestWeightMeasurement.setText(String.format(getString(R.string.avg_daily_weight_loose_from_highest_weight), progressMeasurer.getAverageDailyChangeLosingTheWeightFromHighestWeightMeasurementToLatest()));

        averageDailyWeightChangeFromBeginning.setText(String.format(getString(R.string.progress_avg_daily_change_from_begining), progressMeasurer.getAverageDailyChangeForAllMeasurements()));
        daysLeftToAchieveGoal.setText(String.format(getString(R.string.progress_days_left_to_achieve_goal_label), progressMeasurer.getDaysLeftToAchieveWeightGoal()));

        predictedDateOfAchieveGoal.setText(String.format(getString(R.string.progress_prediction_date), progressMeasurer.getDateOfAchievingAGoal()));

        weightChangeFromBeginning.setText(String.format(getString(R.string.weight_difference_from_begining), progressMeasurer.getWeightDifferenceBetweenFirstAndLatestDate()));
        weightDifferenceFromHighestMeasurement.setText(String.format(getString(R.string.weight_difference_from_highest_weight), progressMeasurer.getWeightDifferenceBetweenHighestWeightMeasurementAndLatestDateMeasurement()));
        numberOfMeasurementMadeFromBeginning.setText(String.format(getString(R.string.number_of_measurement_made_from_begining), progressMeasurer.getNumberOfAllMeasurementMade()));
        numberOfMeasurementMadeFromHighestWeight.setText(String.format(getString(R.string.number_of_measurement_made_from_highest_weight), progressMeasurer.getNumberOfMeasurementFromHighestMeasurementToLatestMeasurementIncludedHighest()));
        firstWeight.setText(String.format(getString(R.string.progress_first_weight), progressMeasurer.getFirstWeight()));
        maxWeight.setText(String.format(getString(R.string.progress_max_weight), progressMeasurer.getHighestWeight()));
        currentWeight.setText(String.format(getString(R.string.progress_current_weight),progressMeasurer.getCurrentWeight()));


       // progressArrowIndicatorFromBeginning.setImageResource(R.drawable.yellow_arrow_right);


        chooseArrowImage(progressMeasurer);

        return view;
    }

    private void chooseArrowImage(ProgressMeasurer progressMeasurer) {
        switch (progressMeasurer.getWeightIndicator()){
             case DOWN:
                 progressArrowIndicatorFromBeginning.setImageResource(R.drawable.green_arrow_down);
                 break;
             case SAME:
                 progressArrowIndicatorFromBeginning.setImageResource(R.drawable.yellow_arrow_right);
                 break;
             case UP:
                 progressArrowIndicatorFromBeginning.setImageResource(R.drawable.red_arrow_up);

         }
    }
}
