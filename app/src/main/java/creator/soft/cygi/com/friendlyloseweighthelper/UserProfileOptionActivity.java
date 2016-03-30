package creator.soft.cygi.com.friendlyloseweighthelper;


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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by CygiMasterProgrammer on 2016-03-26.
 */
public class UserProfileOptionActivity extends AppCompatActivity {

    private Button deleteAccountButton;
    private Button changeUserNameButton;

    private String newUserName = "";
    private EditText userNewNameEditText;

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
        changeUserNameBuilder.setNegativeButton(R.string.change_user_name_cancel, null);

        final AlertDialog changeUserNameDialog = changeUserNameBuilder.create();

        changeUserNameDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                final Button positiveButton = changeUserNameDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        newUserName = userNewNameEditText.getText().toString();

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
        disablePositiveButtonIfEmptyName(changeUserNameDialog);

    }

    private void disablePositiveButtonIfEmptyName(final AlertDialog changeUserNameDialog) {
        changeUserNameDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        userNewNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (TextUtils.isEmpty(s)) {
                    changeUserNameDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    changeUserNameDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
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

}
