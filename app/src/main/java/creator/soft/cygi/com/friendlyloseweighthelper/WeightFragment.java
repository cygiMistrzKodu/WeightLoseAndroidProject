package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by CygiMasterProgrammer on 2015-12-09.
 */
public class WeightFragment extends Fragment {

    private static String TAG = "WeightFragment";
    public static final String WEIGHT_DATA = "weightTimeData";
    public static final String DATE_DATA = "dateData";

    private WeightData weightData;
    private EditText weightInput;
    private Button acceptButton;

    private Button runChartButtonTest;


    private Button testDatabaseButton;

    private WeightTrackDatabaseHelper weightTrackDatabaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getContext());
        weightData = weightTrackDatabaseHelper.getAllWeightDataFromDatabase();

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
                    weightData.setWeightWithCurrentDate(Float.parseFloat(numberInText));
                    weightTrackDatabaseHelper.insertOneRecordIntoWeightTrackDatabase(weightData);
                }
                else {
                    Log.i(TAG,"Weight should be number");
                }

            }

        });

        runChartButtonTest = (Button) view.findViewById(R.id.schowChart_test);
        runChartButtonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(),ChartActivity.class);

                ArrayList<String> dateSeries = new ArrayList<String>();
                ArrayList<Float> weight_units = new ArrayList<Float>();



                SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                for (Map.Entry<Date,Float> entry : weightData.getWeightAndTimeData().entrySet()){

                    Date date = entry.getKey();
                    dateSeries.add(dt.format(date));

                    Float weight = entry.getValue();
                    weight_units.add(weight);

                }

                float [] weight_measurements = ArrayUtils
                        .toPrimitive(weight_units.toArray(new Float[weight_units.size()]));

                i.putExtra(WEIGHT_DATA,weight_measurements);
                i.putExtra(DATE_DATA,dateSeries);

                startActivity(i);

            }
        });

        testDatabaseButton = (Button) view.findViewById(R.id.testDatabaseButton);
        testDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getContext());
              //  weightTrackDatabaseHelper.getReadableDatabase();
             //   weightTrackDatabaseHelper.deleteDatabase();


            }
        });

        return view;
    }

    private boolean checkIfNumber(String text) {

        if(TextUtils.isEmpty(text)) {
            return false;
        }

        return NumberUtils.isNumber(text);
    }
}
