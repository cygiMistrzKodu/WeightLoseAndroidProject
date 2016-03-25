package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.CheckBox;
import android.widget.Toast;

/**
 * Created by CygiMasterProgrammer on 2015-12-09.
 */
public class WeightStandardViewFragment extends WeightCommonViewFragment implements DatabaseNotificationObserver {

    private static final String AUTO_CHECK_BOX = "autoCheckBoxState";
    private static final String UNDO_LAST_DELETION_BUTTON_STATE = "undoLastDeletionButtonState";
    private static String TAG = "WeightStandardViewFragment";

    private Button deleteLatestEntryButton;
    private Button undoLastDeletionButton;
    private DatabaseNotificationSubject databaseNotificationSubject;

    private CheckBox autoCheckBox;
    private boolean autoCheckBoxStateBeforeShutDown;
    private Button modifyModeButton;
    private Integer textInputViewColor;
    private Integer textDateAndTimeColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        databaseNotificationSubject = weightTrackDatabaseHelper;
        databaseNotificationSubject.addNotificationObserver(this);
        restoreApplicationState();

    }

    private void restoreApplicationState() {
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        autoCheckBoxStateBeforeShutDown = preferences.getBoolean(AUTO_CHECK_BOX, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View view = super.onCreateView(inflater,container,savedInstanceState);

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

        modifyModeButton = (Button) view.findViewById(R.id.changeModeButton);
        modifyModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragmentContainer, new WeightModificationViewFragment());
                ft.commit();

            }
        });

        weightTrackDatabaseHelper.isMeasurementTableEmpty();

        textInputViewColor = weightInput.getCurrentTextColor();
        textDateAndTimeColor = timeTextView.getCurrentTextColor();

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

    private boolean isDateGeneratedAutomatically() {
        return autoCheckBox.isChecked();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.weight_input_view;
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);

        saveInstanceState.putBoolean(AUTO_CHECK_BOX, autoCheckBox.isChecked());
        saveInstanceState.putBoolean(UNDO_LAST_DELETION_BUTTON_STATE, undoLastDeletionButton.isEnabled());

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
    public void onTableMeasurementIsEmpty() {

        deleteLatestEntryButton.setEnabled(false);
        modifyModeButton.setEnabled(false);

    }

    @Override
    public void onTableMeasurementNotEmpty() {
        deleteLatestEntryButton.setEnabled(true);
        modifyModeButton.setEnabled(true);
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

        TextAnimatorUtilityHelper textAnimatorUtilityHelper = new TextAnimatorUtilityHelper();
        textAnimatorUtilityHelper.addTextComponentToAnimate(weightInput);
        textAnimatorUtilityHelper.animateTextComponents(Color.RED, textInputViewColor);

    }

    @Override
    public void onUndoMeasurementDeletion(DateTimeDTO dateTimeDTO) {

        putDataToInputView(dateTimeDTO);


    }

    @Override
    public void onMeasurementFailToInsertToDatabase() {
        Log.d(TAG, "**Duplicate Date Value**");

        TextAnimatorUtilityHelper animateDateAndTimeText = new TextAnimatorUtilityHelper();
        animateDateAndTimeText.addTextComponentToAnimate(dateTextView);
        animateDateAndTimeText.addTextComponentToAnimate(timeTextView);
        animateDateAndTimeText.animateTextComponents(Color.YELLOW, textDateAndTimeColor);

        Context context = getContext();
        String message = context.getString(R.string.error_try_insert_duplicated_date);

        Toast toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();


    }

    @Override
    public void onMeasurementInsertedToDatabase() {
        deleteLatestEntryButton.setEnabled(true);
        modifyModeButton.setEnabled(true);

        TextAnimatorUtilityHelper textAnimatorUtilityHelper = new TextAnimatorUtilityHelper();
        textAnimatorUtilityHelper.addTextComponentToAnimate(weightInput);
        textAnimatorUtilityHelper.animateTextComponents(Color.GREEN, textInputViewColor);

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
