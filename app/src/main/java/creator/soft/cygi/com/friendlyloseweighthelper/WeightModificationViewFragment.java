package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by CygiMasterProgrammer on 2016-01-08.
 */
public class WeightModificationViewFragment extends WeightCommonViewFragment implements WeightDataObserver {

    public static final String TAG = "WeightModificationVF";
    private Button standardModeButton;
    private Button previousButton;
    private Button nextButton;
    private TextView positionNumberTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        weightDataModel.addWeightDataObserver(this);


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

        previousButton = (Button) view.findViewById(R.id.goToPreviousMeasurement);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              DateTimeDTO dateTimeDTO =  weightDataModel.getPreviousMeasurement();

                weightInput.setText(dateTimeDTO.getWeight().toString());
                timeTextView.setText(dateTimeDTO.getFormattedTime());
                dateTextView.setText(dateTimeDTO.getFormattedDate());

            }
        });

        nextButton = (Button) view.findViewById(R.id.goToNextMeasurement);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DateTimeDTO dateTimeDTO =  weightDataModel.getNextMeasurement();

                weightInput.setText(dateTimeDTO.getWeight().toString());
                timeTextView.setText(dateTimeDTO.getFormattedTime());
                dateTextView.setText(dateTimeDTO.getFormattedDate());


            }
        });

        positionNumberTextView = (TextView) view.findViewById(R.id.positionNumberTextView);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, " I clicked on AccpetButton in Weight Modification View");
                DateTimeDTO dateTimeDTOExistingMeasurement = weightDataModel.readDataOnLastPosition();

                DateTimeDTO dateTimeDTOUpdateMeasurement = new DateTimeDTO();
                dateTimeDTOUpdateMeasurement.setMeasurementID(dateTimeDTOExistingMeasurement.getMeasurementID());
                dateTimeDTOUpdateMeasurement.setWeight(Float.parseFloat(weightInput.getText().toString()));
                dateTimeDTOUpdateMeasurement.setAndroidContext(getContext());

               String dateString =  DateTimeStringUtility
                       .combineTwoDates(getContext(), dateTextView.getText().toString(), timeTextView.getText().toString());
                dateString = DateTimeStringUtility.changeSecondsToZero(dateString);

                dateTimeDTOUpdateMeasurement.setDate(dateString);

                if(weightDataModel.isDateNotRepeated(dateTimeDTOUpdateMeasurement)) {
                    weightTrackDatabaseHelper.updatedMeasurement(dateTimeDTOUpdateMeasurement);

                    weightDataModel.updateMeasurementInModel(dateTimeDTOUpdateMeasurement);


                    String newFormattedTime = dateTimeDTOUpdateMeasurement.getFormattedTime();

                    timeTextView.setText(newFormattedTime);
                }else {
                    Log.d(TAG,"New Date is Repeated");
                }
            }
        });


        DateTimeDTO dateTimeDTO = weightDataModel.readDataOnLastPosition();

            String weight = dateTimeDTO.getWeight().toString();
            weightInput.setText(weight);

            String formattedTime = dateTimeDTO.getFormattedTime();
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

    @Override
    public void notifyPositionChanged(Integer position) {

        Integer positionOnView = ++position;
        Resources res = getResources();
        String positionText = String.format(res.getString(R.string.position_number),positionOnView);

        positionNumberTextView.setText(positionText);
    }
}
