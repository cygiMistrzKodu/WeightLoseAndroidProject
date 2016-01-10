package creator.soft.cygi.com.friendlyloseweighthelper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by CygiMasterProgrammer on 2016-01-08.
 */
public class WeightModificationViewFragment extends WeightCommonViewFragment {

    public static final String TAG = "WeightModificationViewFragment";
    private Button standardModeButton;
    private Button goPreviousButton;
    private Button goNextButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       View view =  super.onCreateView(inflater, container, savedInstanceState);

        standardModeButton = (Button) view.findViewById(R.id.backToStandardModeButton);
        standardModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragmentContainer, new WeightStandardViewFragment());
                ft.commit();
            }
        });

        goPreviousButton = (Button) view.findViewById(R.id.goToPreviousMeasurement);

        goNextButton = (Button) view.findViewById(R.id.goToNextMeasurement);

        DateTimeDTO dateTimeDTO = weightTrackDatabaseHelper.readLatestMeasurement();
        String weight = dateTimeDTO.getWeight().toString();
        weightInput.setText(weight);

        String formattedTime =   dateTimeDTO.getFormattedTime();
        String formattedDate = dateTimeDTO.getFormattedDate();

        dateTextView.setText(formattedDate);
        timeTextView.setText(formattedTime);


        return view;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.weight_modification_view;
    }

    @Override
    public String getTagName() {
        return TAG;
    }

}
