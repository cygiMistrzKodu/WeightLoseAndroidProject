package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

/**
 * Created by CygiMasterProgrammer on 2016-01-31.
 */
public class LoginViewFragment extends Fragment {

    public static final String LOGIN_USER_NAME = "loginUserName";
    private Spinner userListSpinner;
    private EditText passwordEditText;
    private Button okButton;
    private Button createNewUserButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.login_view, null);

        userListSpinner = (Spinner) view.findViewById(R.id.chooseUserSpinner);
        fillWithUserNames();
        userListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getContext() , "Position:  " + userListSpinner.getSelectedItem().toString(), Toast.LENGTH_LONG).show();

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(LOGIN_USER_NAME,userListSpinner.getSelectedItem().toString());
                editor.commit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        okButton = (Button) view.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragmentContainer, new WeightStandardViewFragment());
                ft.commit();
            }
        });
        createNewUserButton = (Button) view.findViewById(R.id.createNewUserButton);



        return view;
    }

    private void fillWithUserNames() {

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getContext());
        List<UserData> userList  = weightTrackDatabaseHelper.getUsersData();

        ArrayAdapter<UserData> userDataArrayAdapter =
                new ArrayAdapter<UserData>(getContext(),android.R.layout.simple_spinner_dropdown_item,userList);
        userDataArrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        userListSpinner.setAdapter(userDataArrayAdapter);
    }
}
