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

    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_view);

        chart = (LineChart) findViewById(R.id.chart);

//        ArrayList<Entry> datelist = new ArrayList<Entry>();
//        ArrayList<Entry> weightValues = new ArrayList<Entry>();

        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp2 = new ArrayList<Entry>();

        Entry cel1 = new Entry(20.000f,0);
        valsComp1.add(cel1);
        Entry cel2 = new Entry(50.000f,1);
        valsComp1.add(cel2);
        Entry cel3 = new Entry(80.000f,2);
        valsComp1.add(cel3);
        Entry cel4 = new Entry(100.000f,3);
        valsComp1.add(cel4);

        Entry c2e1 = new Entry(120.000f,0);
        valsComp2.add(c2e1);
        Entry c2e2 = new Entry(78.000f,1);
        valsComp2.add(c2e2);
        Entry c2e3 = new Entry(10.000f,2);
        valsComp2.add(c2e3);
        Entry c2e4 = new Entry(60.000f,3);
        valsComp2.add(c2e4);


        LineDataSet setComp1 = new LineDataSet(valsComp1,"Company 1");
        setComp1.setColor(Color.RED);

        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        LineDataSet setComp2 = new LineDataSet(valsComp2,"Company 2");
        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("1.Q"); xVals.add("2.Q"); xVals.add("3.Q"); xVals.add("4.Q");
        xVals.add("5.Q");

        LineData data = new LineData(xVals, dataSets);

        chart.setData(data);
        chart.invalidate();



    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }
}
