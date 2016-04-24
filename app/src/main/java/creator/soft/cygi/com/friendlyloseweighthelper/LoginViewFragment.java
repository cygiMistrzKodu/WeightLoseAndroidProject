package creator.soft.cygi.com.friendlyloseweighthelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by CygiMasterProgrammer on 2016-01-31.
 */
public class LoginViewFragment extends Fragment {

    public static final String LOGIN_USER_NAME = "loginUserName";
    private Spinner userListSpinner;
    private TextView passwordTextView;
    private EditText passwordEditText;
    private TextView passwordRecoveryTextView;
    private Button okButton;
    private Button createNewUserButton;
    private TextView chooseUserTextView;
    private List<UserData> userList;
    ArrayAdapter<UserData> userDataArrayAdapter;


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

                UserData userData = (UserData) userListSpinner.getSelectedItem();

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(LOGIN_USER_NAME, userData.getName());
                editor.commit();

                if (userData.getPassword().isEmpty()) {

                    passwordTextView.setVisibility(View.GONE);
                    passwordEditText.setVisibility(View.GONE);
                    passwordRecoveryTextView.setVisibility(View.GONE);

                } else {

                    passwordTextView.setVisibility(View.VISIBLE);
                    passwordEditText.setVisibility(View.VISIBLE);
                    passwordRecoveryTextView.setVisibility(View.VISIBLE);
                }

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

                UserData userData = (UserData) userListSpinner.getSelectedItem();
                String password = userData.getPassword();

                String passwordEnter = passwordEditText.getText().toString();

                boolean noPassword = password.isEmpty();

                if (passwordEnter.equals(userData.getPassword()) || noPassword) {

                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.fragmentContainer, new WeightStandardViewFragment());
                    ft.commit();
                } else {

                    passwordEditText.setError("Wrong Password");

                }
            }
        });
        createNewUserButton = (Button) view.findViewById(R.id.createNewUserButton);
        createNewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragmentContainer, new CreateNewUserFragment());
                ft.commit();
            }
        });


        passwordTextView = (TextView) view.findViewById(R.id.passwordTextView);
        chooseUserTextView = (TextView) view.findViewById(R.id.chooseUserTextView);
        passwordRecoveryTextView = (TextView) view.findViewById(R.id.passwordRecoveryTextView);
        passwordRecoveryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder resetPasswordAlertDialogBuilder = new AlertDialog.Builder(getActivity());
                resetPasswordAlertDialogBuilder.setTitle(R.string.reset_password_dialog_tittle);

                resetPasswordAlertDialogBuilder.setMessage(getString(R.string.reset_password_dialog_message));

                resetPasswordAlertDialogBuilder.setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SecureStringCreator secureStringCreator = new SecureStringCreator();
                        int passwordLength = 6;
                        String randomPassword = secureStringCreator.createRandomPassword(passwordLength);

                        // TODO: 2016-04-24 send password to email. Best if in separate thread

                        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getContext());

                        UserData userData = (UserData) userListSpinner.getSelectedItem();
                        int selectedPosition = userListSpinner.getSelectedItemPosition();
                        weightTrackDatabaseHelper.setLoginUserName(userData.getName());
                        weightTrackDatabaseHelper.updateUserPassword(randomPassword);

                        UserData updatedUserDataFromStore = weightTrackDatabaseHelper.getLoginUserData();

                        LinkedList<UserData> userListLinkedList = (LinkedList<UserData>) userList;
                        userListLinkedList.set(selectedPosition,updatedUserDataFromStore);
                        userDataArrayAdapter.notifyDataSetChanged();


                        Log.d("Selected Position"," "+ selectedPosition);
                        Log.d("SecureString", randomPassword);

                    }
                });

                resetPasswordAlertDialogBuilder.setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog resetPasswordAlertDialog = resetPasswordAlertDialogBuilder.create();
                resetPasswordAlertDialog.show();
            }
        });


        if (isNoUserExistYet()) {
            showOnlyCreateNewUserOption();
        }


        return view;
    }

    private boolean isNoUserExistYet() {

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getContext());

        Long userNumber = weightTrackDatabaseHelper.countUsersInStorage();

        if (userNumber <= 0) {
            return true;
        }

        return false;
    }

    private void showOnlyCreateNewUserOption() {

        passwordTextView.setVisibility(View.GONE);
        passwordEditText.setVisibility(View.GONE);
        okButton.setVisibility(View.GONE);
        passwordRecoveryTextView.setVisibility(View.GONE);
        userListSpinner.setVisibility(View.GONE);
        chooseUserTextView.setVisibility(View.GONE);

    }

    private void fillWithUserNames() {

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getContext());
       // List<UserData> userList = weightTrackDatabaseHelper.getUsersData();
         userList = weightTrackDatabaseHelper.getUsersData();

//        ArrayAdapter<UserData> userDataArrayAdapter =
//                new ArrayAdapter<UserData>(getContext(), R.layout.simple_spinner_dropdown_item_custom, userList);
        userDataArrayAdapter =
                new ArrayAdapter<UserData>(getContext(), R.layout.simple_spinner_dropdown_item_custom, userList);
        userDataArrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        userListSpinner.setAdapter(userDataArrayAdapter);
    }
}
