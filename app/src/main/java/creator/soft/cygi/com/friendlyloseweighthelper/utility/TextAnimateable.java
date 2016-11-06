package creator.soft.cygi.com.friendlyloseweighthelper.utility;

import android.widget.TextView;

/**
 * Created by CygiMasterProgrammer on 2016-10-13.
 */
public interface TextAnimateable {
    void addTextComponentToAnimate(TextView textView);

    void animateTextComponents(Integer startColor, Integer endColor);
}
