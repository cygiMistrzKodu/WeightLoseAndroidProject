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
import android.widget.Spinner;

/**
 * Created by CygiMasterProgrammer on 2016-01-31.
 */
public class LoginViewFragment extends Fragment {

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
}
