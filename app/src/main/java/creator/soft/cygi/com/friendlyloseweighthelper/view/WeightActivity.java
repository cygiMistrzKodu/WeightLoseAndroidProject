package creator.soft.cygi.com.friendlyloseweighthelper.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class WeightActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new WeightStandardViewFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

}
