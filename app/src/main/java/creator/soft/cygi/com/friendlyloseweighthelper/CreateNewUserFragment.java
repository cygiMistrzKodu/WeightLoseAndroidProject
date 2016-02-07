package creator.soft.cygi.com.friendlyloseweighthelper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by CygiMasterProgrammer on 2016-02-06.
 */
public class CreateNewUserFragment extends Fragment {

    private Button backButton;
    private Button createNewUserOkButton;
    private EditText weightGoalEditText;
    private EditText passwordEditText;
    private EditText userNameEditText;

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

                String userName = userNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String weightGoal = weightGoalEditText.getText().toString();

                boolean isUserNameEmpty = userName.isEmpty();

                if(isUserNameEmpty){
                    userNameEditText.setError("User name cannot be empty");
                    return;
                }

                WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getContext());
                UserData userData = new UserData();
                userData.setName(userName);
                userData.setPassword(password);
                userData.setWeightGoal(weightGoal); // TODO trzeba jeszcze dodac nowa tabele do bazy GOAL

                weightTrackDatabaseHelper.insertNewUserDataIntoDatabase(userData);

            }
        });

        userNameEditText = (EditText) view.findViewById(R.id.userNameEditText);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        weightGoalEditText = (EditText) view.findViewById(R.id.weightGoalEditText);




        return view;
    }
}


