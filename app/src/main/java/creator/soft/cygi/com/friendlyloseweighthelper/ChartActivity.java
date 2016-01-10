package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
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
    ArrayList<Float> weight_units;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_view);

        chart = (LineChart) findViewById(R.id.chart);

        ReadDataForChart();
        CreateChart();

    }

    private void ReadDataForChart() {
        Bundle extras = getIntent().getExtras();

        dateSeries = extras.getStringArrayList(ChartHelper.DATE_DATA);

        float [] weight_data = extras.getFloatArray(ChartHelper.WEIGHT_DATA);
        weight_units = new ArrayList<Float>();

        for(Float weight_measurement : weight_data){
            weight_units.add(weight_measurement);
        }



    }

    private void CreateChart() {

        ArrayList<Entry> weightUnitEntrySet = new ArrayList<Entry>();
        int xindex = 0;
        for (Float weight : weight_units) {

            Log.i("ChartFloat", "Chart Float values : " + weight );  // to testing
            Entry weightEntry = new Entry(weight, xindex++);
            weightUnitEntrySet.add(weightEntry);
        }

        String weightUnitInChart = getResources().getString(R.string.chart_unit_kg);
        LineDataSet lineDataSet = new LineDataSet(weightUnitEntrySet,weightUnitInChart);
        lineDataSet.setValueFormatter(new ChartValueFormatter());
        lineDataSet.setValueTextSize(17);
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
