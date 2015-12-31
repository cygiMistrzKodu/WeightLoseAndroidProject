package creator.soft.cygi.com.friendlyloseweighthelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by CygiMasterProgrammer on 2015-12-28.
 */
public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE = "creator.soft.cygi.com.frendlyLoseweighthelper.date";
    private static String TAG = "DatePickerFragment";

    private Date mDate;

    public DatePickerFragment() {

        String CurrentDate = DateTimeStringUtility.getCurrentNonFormattedDateInStringRepresentation();
        Log.d(TAG,"Current Date : " + CurrentDate);
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE,CurrentDate);
        this.setArguments(args);

    }

    public static DatePickerFragment newInstance(String date) {


        Bundle args = new Bundle();

        String nonFormattedDate = DateTimeStringUtility.convertToRawDate(date);

        args.putSerializable(EXTRA_DATE, nonFormattedDate);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String dateInString = (String) getArguments().getSerializable(EXTRA_DATE);

        mDate = DateTimeStringUtility.changeToDate(dateInString);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);

        DatePicker datePicker = (DatePicker) v.findViewById(R.id.dialog_date_datePickier);


        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int month, int day) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mDate);

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);

                mDate = new GregorianCalendar(year, month, day, hour, minutes).getTime();

                getArguments().putSerializable(EXTRA_DATE, DateTimeStringUtility.changeToStringRepresentation(mDate));

            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);

                    }
                })
                .create();

    }

    private void sendResult(int resultCode) {

        if (getTargetFragment() == null) {
            return;
        }

        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, DateTimeStringUtility.changeToStringRepresentation(mDate));

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

}
