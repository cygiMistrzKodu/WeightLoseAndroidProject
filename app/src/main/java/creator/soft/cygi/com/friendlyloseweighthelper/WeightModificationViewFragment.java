package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by CygiMasterProgrammer on 2016-01-08.
 */
public class WeightModificationViewFragment extends WeightCommonViewFragment implements WeightDataObserver {

    public static final String TAG = "WeightModificationVF";
    private Button standardModeButton;
    private Button previousButton;
    private Button nextButton;
    private TextView positionNumberTextView;
    private Integer textDateAndTimeColor;
    private Integer textInputColor;

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

        standardModeButton = (Button) view.findViewById(R.id.changeModeButton);
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

                    animateInputText();

                }else {
                    Log.d(TAG,"New Date is Repeated");

                    animateDateTimeText();
                    showErrorUpdateDateMessage();

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

            textDateAndTimeColor = timeTextView.getCurrentTextColor();
             textInputColor = weightInput.getCurrentTextColor();

        return view;
    }

    private void animateInputText() {
        TextAnimatorUtilityHelper animateInputText = new TextAnimatorUtilityHelper();
        animateInputText.addTextComponentToAnimate(weightInput);
        animateInputText.animateTextComponents(Color.GREEN, textInputColor);
    }

    private void showErrorUpdateDateMessage() {
        Context context = getContext();
        String message = context.getString(R.string.error_update_date_or_time_will_be_repeated);

        Toast toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void animateDateTimeText() {
        TextAnimatorUtilityHelper animateDateAndTimeText = new TextAnimatorUtilityHelper();
        animateDateAndTimeText.addTextComponentToAnimate(dateTextView);
        animateDateAndTimeText.addTextComponentToAnimate(timeTextView);
        animateDateAndTimeText.animateTextComponents(Color.YELLOW, textDateAndTimeColor);
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
