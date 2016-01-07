package creator.soft.cygi.com.friendlyloseweighthelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by CygiMasterProgrammer on 2015-12-30.
 */
public class TimePickerFragment extends DialogFragment {

    public static final String EXTRA_TIME = "creator.soft.cygi.com.frendlyLoseweighthelper.time";
    private static final String TAG = "TimePickerFragment";
    private Date mTime;


    public TimePickerFragment() {

        String CurrentTime = DateTimeStringUtility.getCurrentNonFormattedDateInStringRepresentation();
        Log.d(TAG, "Constructor Current Date : " + CurrentTime);
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TIME, CurrentTime);
        this.setArguments(args);

    }


    public static TimePickerFragment newInstance(Context context, String date) {
        Bundle args = new Bundle();

        String nonFormattedTime = DateTimeStringUtility.convertToRawTime(context, date);

        args.putSerializable(EXTRA_TIME, nonFormattedTime);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String timeInString = (String) getArguments().getSerializable(EXTRA_TIME);

        mTime = DateTimeStringUtility.changeToDate(timeInString);


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);


        View timePickierView = getActivity().getLayoutInflater().inflate(R.layout.dialog_time, null);

        TimePicker timePicker = (TimePicker) timePickierView.findViewById(R.id.dialog_time_picker);


        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minutes);
        }else {

            timePicker.setHour(hour);
            timePicker.setMinute(minutes);
        }

        if (DateTimeStringUtility.is24HourFormat(getContext())) {
            timePicker.setIs24HourView(true);
        }

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                Calendar cal = Calendar.getInstance();
                cal.setTime(mTime);
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);

                mTime = cal.getTime();

                Log.d(TAG, "setOnTimeChangedListener Current Date : " + mTime.toString());

                getArguments().putSerializable(EXTRA_TIME, DateTimeStringUtility.changeToStringRepresentation(mTime));

            }
        });


        return new AlertDialog.Builder(getActivity())
                .setView(timePickierView)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK);

                            }


                        }
                )
                .create();


    }

    private void sendResult(int resultCode) {

        if (getTargetFragment() == null) {
            return;
        }

        Intent i = new Intent();
        i.putExtra(EXTRA_TIME, DateTimeStringUtility.changeToStringRepresentation(mTime));
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);

    }

}

