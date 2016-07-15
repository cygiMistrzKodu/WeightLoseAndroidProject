package creator.soft.cygi.com.friendlyloseweighthelper.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import creator.soft.cygi.com.friendlyloseweighthelper.R;
import creator.soft.cygi.com.friendlyloseweighthelper.utility.TextAnimatorUtilityHelper;
import creator.soft.cygi.com.friendlyloseweighthelper.model.UserData;
import creator.soft.cygi.com.friendlyloseweighthelper.dao.UserNotificationObserver;
import creator.soft.cygi.com.friendlyloseweighthelper.dao.WeightTrackDatabaseHelper;

/**
 * Created by CygiMasterProgrammer on 2016-02-06.
 */
public class CreateNewUserFragment extends Fragment {

    private Button backButton;
    private Button createNewUserOkButton;
    private EditText weightGoalEditText;
    private EditText passwordEditText;
    private EditText userNameEditText;
    private EditText userEmailEditText;

    private Integer colorEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.create_new_user_view,null);

        backButton = (Button) view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragmentContainer, new LoginViewFragment());
                ft.commit();
            }
        });

        createNewUserOkButton = (Button) view.findViewById(R.id.createNewUserOKButton);
        createNewUserOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = userNameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String weightGoalContent = weightGoalEditText.getText().toString().trim();
                String userEmail = userEmailEditText.getText().toString().trim();

                float weightGoal = 0;

                if(!weightGoalContent.equals(".")) {
                    if (!weightGoalContent.isEmpty()) {
                        weightGoal = Float.parseFloat(weightGoalEditText.getText().toString());
                    }
                }

                boolean isUserNameEmpty = userName.isEmpty();

                if(isUserNameEmpty){
                    userNameEditText.setError(getString(R.string.create_new_user_error_user_name_cannot_be_empty));
                    return;
                }

                if(isEmailNotCorrect(userEmailEditText.getText().toString())){
                    userEmailEditText.setError(getString(R.string.email_error_email_is_not_correct));
                    return;
                }

                WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getContext());
                weightTrackDatabaseHelper.addUserNotificationObserver(new UserNotificationObserver() {
                    @Override
                    public void onUserAlreadyExist(UserData userData) {
                        userNameEditText.setError(getString(R.string.create_new_user_error_user_already_exisit));
                    }
                });
                UserData userData = new UserData();
                userData.setName(userName);
                userData.setPassword(password);
                userData.setWeightGoal(weightGoal);
                userData.setEmail(userEmail);

                weightTrackDatabaseHelper.insertNewUserDataIntoDatabase(userData);
                animateUserNameText();

            }
        });

        userNameEditText = (EditText) view.findViewById(R.id.userNameEditText);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        weightGoalEditText = (EditText) view.findViewById(R.id.weightGoalEditText);
        userEmailEditText = (EditText) view.findViewById((R.id.emailEditText));

        userEmailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                 if(isEmailNotCorrect(userEmailEditText.getText().toString())) {
                        userEmailEditText.setError(getString(R.string.email_error_email_is_not_correct));
                    }
                }
            }
        });


        colorEditText = userNameEditText.getCurrentTextColor();

        return view;
    }

    private void animateUserNameText() {

        TextAnimatorUtilityHelper textAnimatorUtilityHelper = new TextAnimatorUtilityHelper();
        textAnimatorUtilityHelper.addTextComponentToAnimate(weightGoalEditText);
        textAnimatorUtilityHelper.addTextComponentToAnimate(passwordEditText);
        textAnimatorUtilityHelper.addTextComponentToAnimate(userNameEditText);
        textAnimatorUtilityHelper.addTextComponentToAnimate(userEmailEditText);

        textAnimatorUtilityHelper.animateTextComponents(Color.GREEN, colorEditText);

    }

    private boolean isEmailNotCorrect(String email) {
      return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}


