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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Date;
import java.util.Map;

/**
 * Created by CygiMasterProgrammer on 2016-01-09.
 */
public abstract class WeightCommonViewFragment extends Fragment {

    private WeightDataModel weightDataModel;
    private Button chartButton;
    protected WeightTrackDatabaseHelper weightTrackDatabaseHelper;
    protected TextView dateTextView;
    protected TextView timeTextView;
    private final ChartHelper chartHelper = new ChartHelper();

    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_TIME = "time";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private Button acceptButton;
    protected EditText weightInput;

    private CheckBox autoCheckBox;

    public  String TAG = "WeightCommonViewFragment";

    private static IntentFilter intentFilter;

    static {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }

    private final BroadcastReceiver timeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_CHANGED)) {

                String currentTime = timeTextView.getText().toString();
                String formattedTime12Or24 = DateTimeStringUtility.convertTimeBaseOnDeviceFormat12or24(getContext(), currentTime);
                timeTextView.setText(formattedTime12Or24);
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getContext());
        setTagName();
        updateWeightDataModel();
    }

    private void setTagName() {
        TAG = getTagName();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(getLayoutResource(), null);

        weightInput = (EditText) view.findViewById(R.id.inputWeightField);

        chartButton = (Button) view.findViewById(R.id.schowChart_test);
        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initializeChartHelper();
                chartHelper.displayChart();

            }
        });

        dateTextView = (TextView) view.findViewById(R.id.dateTextView);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getActivity().getSupportFragmentManager();

                DatePickerFragment dateDialog = DatePickerFragment.newInstance(dateTextView.getText().toString());

                dateDialog.setTargetFragment(WeightCommonViewFragment.this, REQUEST_DATE);

                dateDialog.show(fm, DIALOG_DATE);

            }
        });

        timeTextView = (TextView) view.findViewById(R.id.timeTextView);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getActivity().getSupportFragmentManager();

                TimePickerFragment dateDialog = TimePickerFragment.newInstance(getContext(), timeTextView.getText().toString());

                dateDialog.setTargetFragment(WeightCommonViewFragment.this, REQUEST_TIME);

                dateDialog.show(fm, DIALOG_TIME);

            }
        });

        acceptButton = (Button) view.findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processUserMeasurementInput();
            }

        });

        autoCheckBox = (CheckBox) view.findViewById(R.id.autoCheckBox);


        return view;
    }

    private void processUserMeasurementInput() {
        if (checkIfNumber(weightInput.getText().toString())) {

            String numberInText = weightInput.getText().toString();

            if (isDateGeneratedAutomatically()) {
                weightDataModel.setWeightWithCurrentDate(Float.parseFloat(numberInText));
            } else {
                prepareDataToSave(numberInText);
            }

            saveData();
            clearAllValuesInUndoStack();
        } else {
            Log.i(TAG, "Weight should be number");
        }
    }

    private boolean checkIfNumber(String text) {

        if (TextUtils.isEmpty(text)) {
            return false;
        }

        return NumberUtils.isNumber(text);
    }

    private boolean isDateGeneratedAutomatically() {

        if(autoCheckBox == null)
        {
            return false;
        }

        return autoCheckBox.isChecked();
    }

    private void prepareDataToSave(String numberInText) {

        DateTimeDTO dateTimeDTO = new DateTimeDTO();

        String time = timeTextView.getText().toString();
        String date = dateTextView.getText().toString();

        String measurementDate = DateTimeStringUtility.combineTwoDates(getContext(), date, time);

        dateTimeDTO.setWeight(Float.parseFloat(numberInText));
        dateTimeDTO.setDate(measurementDate);

        weightDataModel.setTimeAndDate(dateTimeDTO);
    }

    private void saveData() {
        weightTrackDatabaseHelper.insertOneRecordIntoWeightTrackDatabase(weightDataModel);
    }

    private void clearAllValuesInUndoStack() {
        weightTrackDatabaseHelper.clearLastMeasurementStack();
    }


    protected void updateWeightDataModel() {
        weightDataModel = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();

    }

    public abstract int getLayoutResource();

    public abstract String getTagName();

    private void initializeChartHelper() {
        chartHelper.setContext(getContext());
        chartHelper.setFragment(this);
        chartHelper.setWeightDataModel(weightDataModel);
        chartHelper.setFragmentActivity(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(timeChangeReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(timeChangeReceiver);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {

            String rawDate = (String) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);

            String formattedDate = DateTimeStringUtility.formatStringRawDate(rawDate);

            dateTextView.setText(formattedDate);

        }

        if (requestCode == REQUEST_TIME) {

            String rawTime = (String) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            String formattedTime = DateTimeStringUtility.formatRawTime(getContext(), rawTime);
            timeTextView.setText(formattedTime);
        }

    }
}

