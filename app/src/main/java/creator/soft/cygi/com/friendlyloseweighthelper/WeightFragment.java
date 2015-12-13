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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weightData = new WeightData();
        setRetainInstance(true);

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
                    weightData.setWeightWithCurrentDate(Integer.parseInt(numberInText));
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
                ArrayList<Integer> weight_units = new ArrayList<Integer>();


                SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                for (Map.Entry<Date,Integer> entry : weightData.getWeightAndTimeData().entrySet()){

                    Date date = entry.getKey();
                    dateSeries.add(dt.format(date));

                    Integer weight = entry.getValue();
                    weight_units.add(weight);

                }

               i.putExtra(WEIGHT_DATA,weight_units);
                i.putExtra(DATE_DATA,dateSeries);

                startActivity(i);

            }
        });

        return view;
    }

    private boolean checkIfNumber(String text) {
        return  TextUtils.isDigitsOnly(text);
    }
}
