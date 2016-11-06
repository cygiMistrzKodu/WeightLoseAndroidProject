package creator.soft.cygi.com.friendlyloseweighthelper.utility;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CygiMasterProgrammer on 2016-10-13.
 */

public abstract class TextAnimatorAbstract implements TextAnimateable {

    protected long animationDuration = 0;
    protected long  startAnimationDelay = 0;
    protected int  repeatAnimation = 0;
    protected int repeatMode = ValueAnimator.RESTART;

    protected List<TextView> manyTextFiled = new ArrayList<>();

    @Override
    public void addTextComponentToAnimate(TextView textView) {
        manyTextFiled.add(textView);
    }

    @Override
    public void animateTextComponents(Integer startColor, Integer endColor) {

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                for (TextView textView : manyTextFiled) {

                    textView.setTextColor((Integer) animation.getAnimatedValue());

                }
            }
        });

        colorAnimation.setDuration(animationDuration);
        colorAnimation.setStartDelay(startAnimationDelay);
        colorAnimation.setRepeatCount(repeatAnimation);
        colorAnimation.setRepeatMode(repeatMode);
        colorAnimation.start();

    }

    public long getAnimationDuration() {
        return animationDuration;
    }

    public void setAnimationDuration(long animationDuration) {
        this.animationDuration = animationDuration;
    }

    public ValueAnimator getRepeatMode() {
        return ValueAnimator.ofFloat(repeatMode);
    }

    public void setRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
    }

    public int getRepeatAnimation() {
        return repeatMode;
    }

    public void setRepeatAnimation(int repeatAnimation) {
        this.repeatAnimation = repeatAnimation;
    }

    public long getStartAnimationDelay() {
        return startAnimationDelay;
    }

    public void setStartAnimationDelay(long startAnimationDelay) {
        this.startAnimationDelay = startAnimationDelay;
    }
}
