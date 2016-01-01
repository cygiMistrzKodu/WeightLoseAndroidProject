package creator.soft.cygi.com.friendlyloseweighthelper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by CygiMasterProgrammer on 2015-12-09.
 */
public class WeightFragment extends Fragment {

    public static final String WEIGHT_DATA = "weightTimeData";
    public static final String DATE_DATA = "dateData";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final String DIALOG_TIME = "time" ;
    private static String TAG = "WeightFragment";
    private WeightDataModel weightDataModel;
    private EditText weightInput;
    private Button acceptButton;

    private Button runChartButtonTest;
    private Button deleteLatestEntryButton;
    private Button undoLastDeletionButton;

    private WeightTrackDatabaseHelper weightTrackDatabaseHelper;

    private TextView dateTextView;
    private TextView timeTextView;

    private static IntentFilter intentFilter;

    static {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }

    private final BroadcastReceiver timeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            if(action.equals(Intent.ACTION_TIME_CHANGED)) {

                String Time12HourPattern = DateTimeStringUtility.getTime12hourPattern();
                String Time24HourPattern = DateTimeStringUtility.getTime24hourPattern();
                String currentTime = timeTextView.getText().toString();  // starty format jeszcze

                if (DateTimeStringUtility.is24HourFormat(getContext())){    //TODO Need to refactor this hard


                    SimpleDateFormat time12hourSimpleDateFormat = new SimpleDateFormat(Time12HourPattern);
                    Date rawTime = DateTimeStringUtility.getRawDateBaseOnDatePattern(currentTime,time12hourSimpleDateFormat);

                    String formatted24HourTime = DateTimeStringUtility.formatTime(getContext(),rawTime);

                    timeTextView.setText(formatted24HourTime);

                }else {

                    SimpleDateFormat time24hourSimpleDateFormat = new SimpleDateFormat(Time24HourPattern);
                    Date rawTime = DateTimeStringUtility.getRawDateBaseOnDatePattern(currentTime,time24hourSimpleDateFormat);

                    String formatted12HourTime  = DateTimeStringUtility.formatTime(getContext(),rawTime);
                    timeTextView.setText(formatted12HourTime);
                }


                Log.i(TAG, "Something change");
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getContext());
        updateWeightDataModel();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.weight_input_view, null);


        weightInput = (EditText) view.findViewById(R.id.inputWeightField);
        acceptButton = (Button) view.findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkIfNumber(weightInput.getText().toString())) {

                    String numberInText = weightInput.getText().toString();
                    weightDataModel.setWeightWithCurrentDate(Float.parseFloat(numberInText));
                    weightTrackDatabaseHelper.insertOneRecordIntoWeightTrackDatabase(weightDataModel);
                    weightTrackDatabaseHelper.clearLastMeasurementStack();
                } else {
                    Log.i(TAG, "Weight should be number");
                }

            }

        });

        runChartButtonTest = (Button) view.findViewById(R.id.schowChart_test);
        runChartButtonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), ChartActivity.class);

                ArrayList<String> dateSeries = new ArrayList<String>();
                ArrayList<Float> weight_units = new ArrayList<Float>();

                SimpleDateFormat dt = DateTimeStringUtility.getDateFormattingPattern(getContext());

                for (Map.Entry<Date, Float> entry : weightDataModel.getWeightAndTimeData().entrySet()) {

                    Date date = entry.getKey();
                    dateSeries.add(dt.format(date));

                    Float weight = entry.getValue();
                    weight_units.add(weight);

                }

                float[] weight_measurements = ArrayUtils
                        .toPrimitive(weight_units.toArray(new Float[weight_units.size()]));

                i.putExtra(WEIGHT_DATA, weight_measurements);
                i.putExtra(DATE_DATA, dateSeries);

                startActivity(i);

            }
        });

        undoLastDeletionButton = (Button) view.findViewById(R.id.undoLastDeletionButton);
        undoLastDeletionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightTrackDatabaseHelper.undoDeleteLastMeasurement();
                updateWeightDataModel();
            }
        });

        deleteLatestEntryButton = (Button) view.findViewById(R.id.deleteLatestEntryButton);
        deleteLatestEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightTrackDatabaseHelper.deleteLatestEntry();
                updateWeightDataModel();

            }
        });

        dateTextView = (TextView) view.findViewById(R.id.dateTextView);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getActivity().getSupportFragmentManager();

                DatePickerFragment dateDialog = DatePickerFragment.newInstance(dateTextView.getText().toString());

                dateDialog.setTargetFragment(WeightFragment.this, REQUEST_DATE);

                dateDialog.show(fm, DIALOG_DATE);

            }
        });


        timeTextView = (TextView) view.findViewById(R.id.timeTextView);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getActivity().getSupportFragmentManager();

//                TimePickerFragment dateDialog = new TimePickerFragment();
//
                TimePickerFragment dateDialog = TimePickerFragment.newInstance(getContext(), timeTextView.getText().toString());

                dateDialog.setTargetFragment(WeightFragment.this, REQUEST_TIME);

                dateDialog.show(fm, DIALOG_TIME);

            }
        });

        dateTextView.setText(DateTimeStringUtility.getCurrentFormattedDateStringRepresentation());
        timeTextView.setText(DateTimeStringUtility.getCurrentFormattedTimeStringRepresentation(getContext()));

        return view;
    }

    private void updateWeightDataModel() {
        weightDataModel = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();
    }

    private boolean checkIfNumber(String text) {

        if (TextUtils.isEmpty(text)) {
            return false;
        }

        return NumberUtils.isNumber(text);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode  != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_DATE) {

            String rawDate = (String) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);

            String formattedDate = DateTimeStringUtility.formatStringRawDate(rawDate);

            dateTextView.setText(formattedDate);

        }

        if(requestCode == REQUEST_TIME){

            String rawTime = (String) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            String formattedTime = DateTimeStringUtility.formatRawTime(getContext(),rawTime);
            timeTextView.setText(formattedTime);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(timeChangeReceiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(timeChangeReceiver);
    }
}
