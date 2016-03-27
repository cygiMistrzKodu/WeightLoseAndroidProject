package creator.soft.cygi.com.friendlyloseweighthelper;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by CygiMasterProgrammer on 2016-03-26.
 */
public class UserProfileOptionActivity extends AppCompatActivity {

    private Button deleteAccountButton;

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
    }

    private void showDeleteUserAccountDialog() {

        AlertDialog alertDialog =  createDeleteUserAccountDialog();
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
