package creator.soft.cygi.com.friendlyloseweighthelper;

import android.nfc.Tag;
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

/**
 * Created by CygiMasterProgrammer on 2015-12-09.
 */
public class WeightFragment extends Fragment {

    private static String TAG = "WeightFragment";

    private WeightData weightData;
    private EditText weightInput;
    private Button acceptButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weightData = new WeightData();

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

        return view;
    }

    private boolean checkIfNumber(String text) {
        return  TextUtils.isDigitsOnly(text);
    }
}
