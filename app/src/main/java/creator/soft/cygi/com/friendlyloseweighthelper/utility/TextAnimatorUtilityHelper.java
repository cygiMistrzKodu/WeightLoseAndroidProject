package creator.soft.cygi.com.friendlyloseweighthelper.utility;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CygiMasterProgrammer on 2016-03-25.
 */
public class TextAnimatorUtilityHelper {

    private static final long ANIMATION_DURATION = 1000;

    private List<TextView> manyTextFiled = new ArrayList<>();

    public void addTextComponentToAnimate(TextView textView) {
        manyTextFiled.add(textView);
    }

    public void animateTextComponents(Integer startColor, Integer endColor) {


        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                for (TextView textView : manyTextFiled){

                    textView.setTextColor((Integer) animation.getAnimatedValue());

                }
            }
        });

        colorAnimation.setDuration(ANIMATION_DURATION);
        colorAnimation.start();

    }



}
