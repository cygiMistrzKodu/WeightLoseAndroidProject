package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by CygiMasterProgrammer on 2016-02-12.
 */
public class WeightGoalActivity extends AppCompatActivity {

    private final static String TAG = "WeightGoalActivity";

    private EditText changeGoalEditText;
    private Button changeGoalOkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.change_weight_goal_view);
        changeGoalEditText = (EditText) findViewById(R.id.changeGoalEditText);
        readWeightGoal();


        changeGoalOkButton = (Button) findViewById(R.id.changeGoalOkButton);
        changeGoalOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weightGoalString =  changeGoalEditText.getText().toString();
                Float weightGoal = Float.parseFloat(weightGoalString);

                WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getApplicationContext());

                weightTrackDatabaseHelper.updateWeightGoal(weightGoal);

            }
        });
    }

    private void readWeightGoal() {

        WeightTrackDatabaseHelper weightTrackDatabaseHelper = new WeightTrackDatabaseHelper(getApplicationContext());
        Float userWeightGoal =  weightTrackDatabaseHelper.getWeightGoalOfCurrentUser();

        changeGoalEditText.setText(userWeightGoal.toString());

    }


}
