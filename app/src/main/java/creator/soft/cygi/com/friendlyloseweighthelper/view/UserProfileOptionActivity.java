package creator.soft.cygi.com.friendlyloseweighthelper.view;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import creator.soft.cygi.com.friendlyloseweighthelper.R;
import creator.soft.cygi.com.friendlyloseweighthelper.dao.WeightTrackDatabaseHelper;

/**
 * Created by CygiMasterProgrammer on 2016-03-26.
 */
public class UserProfileOptionActivity extends AppCompatActivity {

    private Button deleteAccountButton;
    private Button changeUserNameButton;
    private Button changeUserPasswordButton;
    private Button changeUserEmailButton;

    private String newUserName = "";
    private EditText userNewNameEditText;
    private String newUserPassword = "";
    private EditText userNewPasswordEditText;
    private CheckBox disablePasswordCheckBox;

    private View changePasswordView;
    private EditText userEmailEditText;
    private String newUserEmail = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile_option_view);

        deleteAccountButton = (Button) findViewById(R.id.deleteAccountButton);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDeleteUserAccountDialog();

            }
        });

        changeUserNameButton = (Button) findViewById(R.id.changeUserNameButton);
        changeUserNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showChangeUserNameDialog();
            }
        });

        changeUserPasswordButton = (Button) findViewById(R.id.changeUserPasswordButton);
        changeUserPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showChangePasswordDialog();
            }
        });

        changeUserEmailButton = (Button) findViewById(R.id.changeUserEmailButton);
        changeUserEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showChangeEmailDialog();
            }
        });


    }

    private void showChangeEmailDialog() {

        createChangeEmailDialog();
    }

    private void createChangeEmailDialog() {


        AlertDialog.Builder changeUserEmailBuilder = new AlertDialog.Builder(this);
        changeUserEmailBuilder.setTitle(R.string.new_email_dialog_tittle);

        userEmailEditText = new EditText(this);
        userEmailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        changeUserEmailBuilder.setView(userEmailEditText);
        changeUserEmailBuilder.setPositiveButton(R.string.accept_button_ok, null);
        changeUserEmailBuilder.setNegativeButton(R.string.cancel_button, null);

        final AlertDialog changeUserEmailDialog = changeUserEmailBuilder.create();

        changeUserEmailDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                final Button positiveButton = changeUserEmailDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        newUserEmail = userEmailEditText.getText().toString().trim();

                        if(isEmailNotCorrect(newUserEmail)){
                            userEmailEditText.setError(getString(R.string.email_error_email_is_not_correct));
                            return;
                        }


                        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getApplicationContext());
                        boolean isUserEmailUpdated = weightTrackDatabaseHelper.updateUserEmail(newUserEmail);

                        if(isUserEmailUpdated){
                            Resources res = getResources();
                            String userChangeNameInfo = String.format(res.getString(R.string.toast_info_user_email_change), newUserEmail);
                            Toast.makeText(UserProfileOptionActivity.this, userChangeNameInfo, Toast.LENGTH_SHORT).show();
                            changeUserEmailDialog.dismiss();
                        }else {
                            userEmailEditText.setError(getString(R.string.change_user_email_dialog_error_email_already_in_use));
                        }

                    }
                });

            }
        });

        changeUserEmailDialog.show();

    }

    private void showChangePasswordDialog() {

        createChangeUserPasswordDialog();
    }

    private void createChangeUserPasswordDialog() {

        AlertDialog.Builder changeUserPasswordBuilder = new AlertDialog.Builder(this);
        changeUserPasswordBuilder.setTitle(R.string.new_password_dalog_tittle);

        changePasswordView = getLayoutInflater().inflate(R.layout.change_user_password_view, null);

        userNewPasswordEditText = (EditText) changePasswordView.findViewById(R.id.passwordInputField);
        disablePasswordCheckBox = (CheckBox) changePasswordView.findViewById(R.id.disablePasswordCheckBox);

        changeUserPasswordBuilder.setView(changePasswordView);
        changeUserPasswordBuilder.setPositiveButton(R.string.accept_button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                newUserPassword = userNewPasswordEditText.getText().toString().trim();

                WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getApplicationContext());

                Resources res = getResources();
                if (disablePasswordCheckBox.isChecked()) {

                    String passwordDisable = "";
                    weightTrackDatabaseHelper.updateUserPassword(passwordDisable);
                    String userPasswordDisableInfo = String.format(res.getString(R.string.toast_info_user_password_disable));
                    Toast.makeText(UserProfileOptionActivity.this, userPasswordDisableInfo, Toast.LENGTH_SHORT).show();


                } else {

                    weightTrackDatabaseHelper.updateUserPassword(newUserPassword);
                    String userPasswordChangeInfo = String.format(res.getString(R.string.toast_info_user_password_change));
                    Toast.makeText(UserProfileOptionActivity.this, userPasswordChangeInfo, Toast.LENGTH_SHORT).show();
                }


            }
        });

        changeUserPasswordBuilder.setNegativeButton(R.string.cancel_button, null);

        final AlertDialog changeUserPasswordDialog = changeUserPasswordBuilder.create();

        changeUserPasswordDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                ifNoPasswordDisableCheckBoxIsEnable(changeUserPasswordDialog);

            }
        });


        changeUserPasswordDialog.show();

        addDisableEnableButtonsLogickInChangePasswordDialog(changeUserPasswordDialog);

        disablePositiveButtonIfEmptyTextField(changeUserPasswordDialog, userNewPasswordEditText);

    }

    private void ifNoPasswordDisableCheckBoxIsEnable(AlertDialog changeUserPasswordDialog) {
        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getApplicationContext());
        String userPassword = weightTrackDatabaseHelper.getPasswordOfCurrentUser();

        final Button positiveButton = changeUserPasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (userPassword.isEmpty()) {
            disablePasswordCheckBox.setChecked(true);
            userNewPasswordEditText.setEnabled(false);
            positiveButton.setEnabled(true);
        }
    }

    private void addDisableEnableButtonsLogickInChangePasswordDialog(final AlertDialog changeUserPasswordDialog) {
        disablePasswordCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Button positiveButton = changeUserPasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                if (disablePasswordCheckBox.isChecked()) {

                    userNewPasswordEditText.setEnabled(false);
                    positiveButton.setEnabled(true);
                } else {
                    userNewPasswordEditText.setEnabled(true);
                    if (userNewPasswordEditText.getText().toString().isEmpty()) {

                        positiveButton.setEnabled(false);
                    } else {
                        positiveButton.setEnabled(true);
                    }

                }
            }
        });
    }

    private void showChangeUserNameDialog() {

        createChangeUserNameDialog();

    }

    private void createChangeUserNameDialog() {

        final AlertDialog.Builder changeUserNameBuilder = new AlertDialog.Builder(this);
        changeUserNameBuilder.setTitle(R.string.change_user_name_dialog_title);

        userNewNameEditText = new EditText(this);
        userNewNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);

        changeUserNameBuilder.setView(userNewNameEditText);
        changeUserNameBuilder.setPositiveButton(R.string.accept_button_ok, null);
        changeUserNameBuilder.setNegativeButton(R.string.cancel_button, null);

        final AlertDialog changeUserNameDialog = changeUserNameBuilder.create();

        changeUserNameDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                final Button positiveButton = changeUserNameDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        newUserName = userNewNameEditText.getText().toString().trim();

                        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getApplicationContext());
                        boolean isUserNameUpdated = weightTrackDatabaseHelper.updateUserName(newUserName);

                        if (isUserNameUpdated) {
                            setResult(Activity.RESULT_OK);
                            Resources res = getResources();
                            String userChangeNameInfo = String.format(res.getString(R.string.toast_info_user_name_change), newUserName);
                            Toast.makeText(UserProfileOptionActivity.this, userChangeNameInfo, Toast.LENGTH_SHORT).show();
                            changeUserNameDialog.dismiss();
                        } else {
                            userNewNameEditText.setError(getString(R.string.change_user_name_dialog_error_name_already_in_use));
                        }

                    }
                });

            }
        });

        changeUserNameDialog.show();
        disablePositiveButtonIfEmptyTextField(changeUserNameDialog, userNewNameEditText);

    }

    private void disablePositiveButtonIfEmptyTextField(final AlertDialog userDialog, EditText editText) {
        userDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (TextUtils.isEmpty(s)) {
                    userDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    userDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }

            }
        });
    }

    private void showDeleteUserAccountDialog() {

        AlertDialog alertDialog = createDeleteUserAccountDialog();
        alertDialog.show();
    }

    private AlertDialog createDeleteUserAccountDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserProfileOptionActivity.this);
        alertDialogBuilder.setTitle(R.string.delete_user_account_dialog_title);

        alertDialogBuilder.setMessage(R.string.delete_user_account_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteUserAccount();
                        moveToLoginScreen();
                    }
                })
                .setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


        return alertDialogBuilder.create();
    }

    private void deleteUserAccount() {
        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getApplicationContext());
        weightTrackDatabaseHelper.deleteCurrentUserAccount();
    }

    private void moveToLoginScreen() {
        Intent intent = new Intent(UserProfileOptionActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean isEmailNotCorrect(String email) {
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
