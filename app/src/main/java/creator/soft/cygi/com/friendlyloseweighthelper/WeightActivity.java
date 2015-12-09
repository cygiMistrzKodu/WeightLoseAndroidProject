package creator.soft.cygi.com.friendlyloseweighthelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class WeightActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment() {
        return new WeightFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }




}
