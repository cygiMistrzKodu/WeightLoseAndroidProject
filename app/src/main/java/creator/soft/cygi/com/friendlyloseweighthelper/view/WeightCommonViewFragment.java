package creator.soft.cygi.com.friendlyloseweighthelper.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

import creator.soft.cygi.com.friendlyloseweighthelper.R;
import creator.soft.cygi.com.friendlyloseweighthelper.utility.LatestWeightObserver;
import creator.soft.cygi.com.friendlyloseweighthelper.utility.LatestWeightSubject;
import creator.soft.cygi.com.friendlyloseweighthelper.dao.WeightTrackDatabaseHelper;
import creator.soft.cygi.com.friendlyloseweighthelper.dto.DateTimeDTO;
import creator.soft.cygi.com.friendlyloseweighthelper.model.WeightDataModel;
import creator.soft.cygi.com.friendlyloseweighthelper.utility.DateTimeStringUtility;

/**
 * Created by CygiMasterProgrammer on 2016-01-09.
 */
public abstract class WeightCommonViewFragment extends Fragment implements LatestWeightSubject {

    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_TIME = "time";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_USER_PROFILE_CHANGE_USER_NAME = 3;
    private static IntentFilter intentFilter;

    static {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }

    private final ChartHelper chartHelper = new ChartHelper();
    public String TAG = "WeightCommonView";
    protected WeightDataModel weightDataModel;
    protected WeightTrackDatabaseHelper weightTrackDatabaseHelper;
    protected TextView dateTextView;
    protected TextView timeTextView;
    private final BroadcastReceiver timeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_CHANGED)) {

                String currentTime = timeTextView.getText().toString();
                Log.d(TAG, "Broadcast receiver currentTime: " + currentTime);
                Context whatIsContextState = getContext();
                if (whatIsContextState == null) {
                    Log.d(TAG, "Boradcast Context is NULL");
                } else {
                    Log.d(TAG, "Broadcast Context is  NOT NULL");
                }
                String formattedTime12Or24 = DateTimeStringUtility.convertTimeBaseOnDeviceFormat12or24(getContext(), currentTime);
                timeTextView.setText(formattedTime12Or24);
            }

        }
    };
    protected Button acceptButton;
    protected EditText weightInput;
    private Button chartButton;
    private CheckBox autoCheckBox;
    private List<LatestWeightObserver> latestWeightObservers = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getContext());
        setTagName();
        updateWeightDataModel();
        setHasOptionsMenu(true);
    }

    private void setTagName() {
        TAG = getTagName();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(getLayoutResource(), null);

        weightInput = (EditText) view.findViewById(R.id.inputWeightField);

        chartButton = (Button) view.findViewById(R.id.showChart);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_switch_users:

                replaceFragment(new LoginViewFragment());

                return true;
            case R.id.menu_set_weight_goal:

                Intent i = new Intent(getActivity(), WeightGoalActivity.class);
                getActivity().startActivity(i);

                return true;
            case R.id.menu_user_profile_option:
                Intent userProfileIntent = new Intent(getActivity(), UserProfileOptionActivity.class);
                startActivityForResult(userProfileIntent, REQUEST_USER_PROFILE_CHANGE_USER_NAME);
                return true;

            case R.id.menu_show_progress:
                Intent progressActivityIntent = new Intent(getActivity(), ProgressActivity.class);
                getActivity().startActivity(progressActivityIntent);
                return true;

            case R.id.menu_delete_all_measurement:

                showDeleteAllMeasurementAlertDialog();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showDeleteAllMeasurementAlertDialog() {

        AlertDialog alertDialog = createDeleteAllMeasurementDialog();

        alertDialog.show();
    }

    private AlertDialog createDeleteAllMeasurementDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(R.string.delete_all_measurement_dialog_title);

        alertDialogBuilder.setMessage(R.string.delete_all_measurement_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteAllMeasurement();
                    }
                })
                .setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return alertDialogBuilder.create();
    }

    private void deleteAllMeasurement() {
        weightTrackDatabaseHelper.clearAllMeasurementDataForLoginUser();
        replaceFragment(new WeightStandardViewFragment());
    }

    private void replaceFragment(Fragment fragment) {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentContainer, fragment);
        ft.commit();
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

        if (autoCheckBox == null) {
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
        weightTrackDatabaseHelper.insertOneMeasurementIntoDatabase(weightDataModel);
        notifyLatestWeightObservers();

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
        Float weightGoal = weightTrackDatabaseHelper.getWeightGoalOfCurrentUser();
        chartHelper.setWeightGoal(weightGoal);


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

        if (requestCode == REQUEST_USER_PROFILE_CHANGE_USER_NAME) {

            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragmentContainer, new WeightStandardViewFragment());
            ft.commit();

            Log.d(TAG,"New user name updated");

        }

    }

    @Override
    public void addLatestWeightObserver(LatestWeightObserver latestWeightObserver) {
        latestWeightObservers.add(latestWeightObserver);

    }

    @Override
    public void removeLatestWeightObserver(LatestWeightObserver latestWeightObserver) {

        latestWeightObservers.remove(latestWeightObserver);
    }

    @Override
    public void notifyLatestWeightObservers() {

        for (LatestWeightObserver latestWeightObserver : latestWeightObservers){
            latestWeightObserver.updateLatestWeight(weightDataModel.getLatestWeight());
        }
    }
}

