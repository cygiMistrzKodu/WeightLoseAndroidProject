package creator.soft.cygi.com.friendlyloseweighthelper.view;

import android.support.v4.app.Fragment;

/**
 * Created by CygiMasterProgrammer on 2016-02-01.
 */
public class LoginActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new LoginViewFragment();
    }
}
