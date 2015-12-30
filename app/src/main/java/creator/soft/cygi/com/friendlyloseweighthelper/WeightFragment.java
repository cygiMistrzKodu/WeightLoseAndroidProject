package creator.soft.cygi.com.friendlyloseweighthelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private Button deleteLaatestEntryButton;
    private Button undoLastDeletionButton;

    private WeightTrackDatabaseHelper weightTrackDatabaseHelper;

    private TextView dateTextView;
    private TextView timeTextView;

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

        deleteLaatestEntryButton = (Button) view.findViewById(R.id.deleteLatestEntryButton);
        deleteLaatestEntryButton.setOnClickListener(new View.OnClickListener() {
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

                DatePickerFragment dateDialog = new DatePickerFragment();
                dateDialog.setTargetFragment(WeightFragment.this,REQUEST_DATE );

                dateDialog.show(fm,DIALOG_DATE);

            }
        });


        timeTextView = (TextView) view.findViewById(R.id.timeTextView);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getActivity().getSupportFragmentManager();

                TimePickerFragment dateDialog = new TimePickerFragment();
                dateDialog.setTargetFragment(WeightFragment.this,REQUEST_TIME );

                dateDialog.show(fm,DIALOG_TIME);

            }
        });

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

            String formattedDate = DateTimeStringUtility.formatRawDate(rawDate);

            dateTextView.setText(formattedDate);

        }

        if(requestCode == REQUEST_TIME){

            String rawTime = (String) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            String formattedTime = DateTimeStringUtility.formatRawTime(getContext(),rawTime);
            timeTextView.setText(formattedTime);
        }
    }
}
