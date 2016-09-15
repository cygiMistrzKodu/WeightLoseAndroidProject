package creator.soft.cygi.com.friendlyloseweighthelper.view;

import android.support.v4.app.Fragment;

/**
 * Created by CygiMasterProgrammer on 2016-07-30.
 */
public class ProgressActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ProgressActivityFragment();
    }
}
