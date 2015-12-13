package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/**
 * Created by CygiMasterProgrammer on 2015-12-12.
 */
public class ChartActivity extends AppCompatActivity {

    private final static String TAG = "ChartActivity";

    LineChart chart;
    ArrayList<String> dateSeries;
    ArrayList<Integer> weight_units;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_view);

        chart = (LineChart) findViewById(R.id.chart);

        Bundle extras = getIntent().getExtras();

        dateSeries = extras.getStringArrayList(WeightFragment.DATE_DATA);
        weight_units = extras.getIntegerArrayList(WeightFragment.WEIGHT_DATA);

        ArrayList<Entry> weightUnitEntrySet = new ArrayList<Entry>();

        int xindex = 0;
        for (Integer weight : weight_units) {

            Entry weightEntry = new Entry(weight, xindex++);
            weightUnitEntrySet.add(weightEntry);
        }

        LineDataSet lineDataSet = new LineDataSet(weightUnitEntrySet, "Weight in Kg");
        lineDataSet.setColor(Color.RED);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(lineDataSet);

        LineData data = new LineData(dateSeries, dataSets);

        chart.setData(data);
        chart.invalidate();

    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }
}
