package creator.soft.cygi.com.friendlyloseweighthelper.utility;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import creator.soft.cygi.com.friendlyloseweighthelper.R;
import creator.soft.cygi.com.friendlyloseweighthelper.dao.WeightTrackDatabaseHelper;

/**
 * Created by CygiMasterProgrammer on 2016-09-28.
 */
public class GoalAchieveInformerActivity extends AppCompatActivity implements LatestWeightObserver {

    public final static String LOGIN_USER_NAME_EXTRA = "LoginUserName";

    private WeightTrackDatabaseHelper weightTrackDatabaseHelper;
    private FragmentActivity fragmentActivity;
    private TextView congratulationTitleTextView;
    private String userName = "Empty Something wrong!!!";
    private Animation rotateTextYou;
    private TextView youTextView;
    private TextView tittleTextView;
    private Animation zoomInTitle;
    private TextView achieveTextView;
    private Animation moveAchieve;
    private TextView goalTextView;
    private Animation moveBack;
    private ImageView congratulationImage;
    private Animation imageAnimation;
    private TextView yourTextView;
    private Animation animateYourTextView;


    public GoalAchieveInformerActivity() {

    }

    public GoalAchieveInformerActivity(WeightTrackDatabaseHelper weightTrackDatabaseHelper, FragmentActivity fragmentActivity) {
        this.weightTrackDatabaseHelper = weightTrackDatabaseHelper;
        this.fragmentActivity = fragmentActivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.congratulations_dialog_view);

        userName = getIntent().getStringExtra(LOGIN_USER_NAME_EXTRA);

        congratulationTitleTextView = (TextView) findViewById(R.id.congratulations_title);
        congratulationTitleTextView.setText(String.format(getString(R.string.congratulations_view_title), userName));

        youTextView = (TextView) findViewById(R.id.congratulations_message_you);
        rotateTextYou = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_you_text_view);
        youTextView.startAnimation(rotateTextYou);

        tittleTextView = (TextView) findViewById(R.id.congratulations_title);
        zoomInTitle = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.title_text_animation);
        tittleTextView.startAnimation(zoomInTitle);

        achieveTextView = (TextView) findViewById(R.id.congratulations_message_achieve);
        moveAchieve = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.movie_achieve_text_view);
        achieveTextView.startAnimation(moveAchieve);

        yourTextView = (TextView) findViewById(R.id.congratulations_message_your);
        animateYourTextView = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_your_text_view);
        yourTextView.startAnimation(animateYourTextView);


        goalTextView = (TextView) findViewById(R.id.congratulations_message_goal);
        moveBack = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_goal_text_view);
        goalTextView.startAnimation(moveBack);

        congratulationImage = (ImageView) findViewById(R.id.congratulations_image);
        imageAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_animation);
        congratulationImage.startAnimation(imageAnimation);

        TextGoalAchieveAnimator textGoalAchieveAnimator = new TextGoalAchieveAnimator();
        textGoalAchieveAnimator.addTextComponentToAnimate(youTextView);
        textGoalAchieveAnimator.addTextComponentToAnimate(achieveTextView);
        textGoalAchieveAnimator.addTextComponentToAnimate(yourTextView);
        textGoalAchieveAnimator.addTextComponentToAnimate(goalTextView);
        textGoalAchieveAnimator.setAnimationDuration(2000);
        textGoalAchieveAnimator.setStartAnimationDelay(800);
        textGoalAchieveAnimator.setRepeatAnimation(20);
        textGoalAchieveAnimator.setRepeatMode(ValueAnimator.REVERSE);
        textGoalAchieveAnimator.animateTextComponents(Color.YELLOW, Color.BLUE);

//        final MediaPlayer mp = MediaPlayer.create(this, R.raw.are_you_online);
//        mp.start();

    }

    @Override
    public void updateLatestWeight(float latestWeight) {
        checkIfUserAchieveGoal(latestWeight);
    }

    private void checkIfUserAchieveGoal(float latestWeight) {
        float weightGoal = weightTrackDatabaseHelper.getWeightGoalOfCurrentUser();
        if (latestWeight <= weightGoal) {

            Intent intent = new Intent(fragmentActivity, GoalAchieveInformerActivity.class);
            intent.putExtra(LOGIN_USER_NAME_EXTRA, weightTrackDatabaseHelper.getLoginUserName());
            fragmentActivity.startActivity(intent);
        }
    }
}
