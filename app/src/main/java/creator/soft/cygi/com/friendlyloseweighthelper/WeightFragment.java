package creator.soft.cygi.com.friendlyloseweighthelper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.ToggleButton;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by CygiMasterProgrammer on 2015-12-09.
 */
public class WeightFragment extends Fragment implements NotificationObserver {

    public static final String WEIGHT_DATA = "weightTimeData";
    public static final String DATE_DATA = "dateData";
    private static final String AUTO_CHECK_BOX = "autoCheckBoxState";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final String DIALOG_TIME = "time";
    private static final String UNDO_LAST_DELETION_BUTTON_STATE = "undoLastDeletionButtonState";
    private static String TAG = "WeightFragment";

    private static IntentFilter intentFilter;

    static {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }

    private WeightDataModel weightDataModel;
    private EditText weightInput;
    private Button acceptButton;
    private Button runChartButtonTest;
    private Button deleteLatestEntryButton;
    private Button undoLastDeletionButton;
    private WeightTrackDatabaseHelper weightTrackDatabaseHelper;
    private DatabaseNotificationSubject databaseNotificationSubject;
    private TextView dateTextView;
    private TextView timeTextView;
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
    private CheckBox autoCheckBox;
    private boolean autoCheckBoxStateBeforeShutDown;
    private ToggleButton modifyModeToggleButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG,"ON CREATE URUCHOMILEM SIE BLA BLA ");


        setRetainInstance(true);
        weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getContext());
        databaseNotificationSubject = weightTrackDatabaseHelper;
        databaseNotificationSubject.addNotificationObserver(this);
        restoreApplicationState();

        updateWeightDataModel();

    }

    private void restoreApplicationState() {
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        autoCheckBoxStateBeforeShutDown = preferences.getBoolean(AUTO_CHECK_BOX, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        Log.d(TAG,"ON CREATE VIEW URUCHOMILEM SIE BLA BLA ");

        final View view = inflater.inflate(R.layout.weight_input_view, null);

        weightInput = (EditText) view.findViewById(R.id.inputWeightField);

        acceptButton = (Button) view.findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processUserMeasurementInput();
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
        undoLastDeletionButton.setEnabled(false);
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

                TimePickerFragment dateDialog = TimePickerFragment.newInstance(getContext(), timeTextView.getText().toString());

                dateDialog.setTargetFragment(WeightFragment.this, REQUEST_TIME);

                dateDialog.show(fm, DIALOG_TIME);

            }
        });

        dateTextView.setText(DateTimeStringUtility.getCurrentFormattedDateStringRepresentation());
        timeTextView.setText(DateTimeStringUtility.getCurrentFormattedTimeStringRepresentation(getContext()));

        autoCheckBox = (CheckBox) view.findViewById(R.id.autoCheckBox);
        autoCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ifDateAndTimeIsGenerateAutomaticallyDisableDateAndTimeTextView();
            }
        });

        if (savedInstanceState != null) {
            autoCheckBox.setChecked(savedInstanceState.getBoolean(AUTO_CHECK_BOX));
            undoLastDeletionButton.setEnabled(savedInstanceState.getBoolean(UNDO_LAST_DELETION_BUTTON_STATE));
        } else {

            autoCheckBox.setChecked(autoCheckBoxStateBeforeShutDown);
        }


        ifDateAndTimeIsGenerateAutomaticallyDisableDateAndTimeTextView();


        modifyModeToggleButton = (ToggleButton) view.findViewById(R.id.modifyViewButton);
        modifyModeToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modifyModeToggleButton.isChecked()){

//                    Log.d(TAG,"Modyfi button is ON");
//
//                    View layoutToReplace = view.findViewById(R.id.reaplyacableLayout);
//                    ViewGroup parent = (ViewGroup) layoutToReplace.getParent();
//                    int index = parent.indexOfChild(layoutToReplace);
//                    parent.removeView(layoutToReplace);
//                    layoutToReplace = getActivity().getLayoutInflater().inflate(R.layout.weight_modification_view,parent,false);
//                    parent.addView(layoutToReplace,index);

                    final android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();


                   // final FragmentManager fm  = getFragmentManager();


                  //  ft.replace(R.id.reaplyacableLayout,new ModificationFragment());
                    ft.add(R.id.weight_input_layout_root, new ModificationFragment());
                  //  ft.replace(R.id.fragmentContainer, new ModificationFragment());
                    ft.commit();


                }else {

//                    Log.d(TAG,"Modyfi button is OFF");
//
//                    View layoutToReplace = view.findViewById(R.id.wieght_modification_id);
//                    ViewGroup parent = (ViewGroup) layoutToReplace.getParent();
//                    int index = parent.indexOfChild(layoutToReplace);
//                    parent.removeView(layoutToReplace);
//                    layoutToReplace = getActivity().getLayoutInflater().inflate(R.layout.weight_input_view,parent,false);
//                    parent.addView(layoutToReplace, index);
//
//                    onCreateView(getActivity().getLayoutInflater(), parent, savedInstanceState);

                    final android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();

                    ft.replace(R.id.fragmentContainer,new WeightFragment());
                    ft.commit();


                }
            }
        });

        return view;
    }

    private void ifDateAndTimeIsGenerateAutomaticallyDisableDateAndTimeTextView() {
        if (isDateGeneratedAutomatically()) {
            dateTextView.setEnabled(false);
            timeTextView.setEnabled(false);
        } else {
            dateTextView.setEnabled(true);
            timeTextView.setEnabled(true);
        }
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

    private boolean isDateGeneratedAutomatically() {
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

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);

        saveInstanceState.putBoolean(AUTO_CHECK_BOX, autoCheckBox.isChecked());
        saveInstanceState.putBoolean(UNDO_LAST_DELETION_BUTTON_STATE, undoLastDeletionButton.isEnabled());

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(timeChangeReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        saveApplicationState();
    }

    private void saveApplicationState() {
        SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        editor.putBoolean(AUTO_CHECK_BOX, autoCheckBox.isChecked());
        editor.apply();
        Log.i(TAG, "Zapisałem stan przycisku");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(timeChangeReceiver);

    }


    @Override
    public void onDatabaseIsEmpty() {

        deleteLatestEntryButton.setEnabled(false);

    }

    @Override
    public void onDatabaseNotEmpty() {
        deleteLatestEntryButton.setEnabled(true);
    }

    @Override
    public void onNoMeasurementToUndo() {
        undoLastDeletionButton.setEnabled(false);
        Log.i(TAG, "Stack is empty one more time hi hi");
    }

    @Override
    public void onUndoStackNotEmpty() {
        undoLastDeletionButton.setEnabled(true);
    }

    @Override
    public void onMeasurementDeletion(DateTimeDTO dateTimeDTO) {

        putDataToInputView(dateTimeDTO);

    }

    @Override
    public void onUndoMeasurementDeletion(DateTimeDTO dateTimeDTO) {

        putDataToInputView(dateTimeDTO);


    }

    private void putDataToInputView(DateTimeDTO dateTimeDTO) {
        Float weight = dateTimeDTO.getWeight();

        String date = dateTimeDTO.getFormattedDate();
        String time = dateTimeDTO.getFormattedTime();

        weightInput.setText(weight.toString());
        dateTextView.setText(date);
        timeTextView.setText(time);
    }

}
