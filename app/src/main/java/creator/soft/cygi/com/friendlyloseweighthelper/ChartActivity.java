package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * Created by CygiMasterProgrammer on 2015-12-12.
 */
public class ChartActivity extends AppCompatActivity {

    private final static String TAG = "ChartActivity";

    LineChart chart;
    ArrayList<String> dateSeries;
    ArrayList<Float> weight_units;
    Float userWeightGoal;


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
        userWeightGoal = extras.getFloat(ChartHelper.WEIGHT_GOAL_DATA);

    }

    private void CreateChart() {

        ArrayList<Entry> weightUnitEntrySet = new ArrayList<Entry>();
        int xindex = 0;
        for (Float weight : weight_units) {

            Log.d("ChartFloat", "Chart Float values : " + weight );  // to testing
            Entry weightEntry = new Entry(weight, xindex++);
            weightUnitEntrySet.add(weightEntry);
        }

        String weightUnitInChart = getResources().getString(R.string.chart_unit_kg);
        LineDataSet lineDataSet = new LineDataSet(weightUnitEntrySet,weightUnitInChart);
        lineDataSet.setValueFormatter(new ChartValueFormatter());
        lineDataSet.setValueTextSize(17);
        int colorGreenDark = Color.argb(255,0,77,0);
        lineDataSet.setColor(colorGreenDark);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setDrawFilled(true);



        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet);

        LineData data = new LineData(dateSeries, dataSets);

        decorateXaxis();
        decorateYaxis();
        decorateLegend();
        drawWeightGoalLine();

        int yellowOpacity = Color.argb(20,255,255,0);
        chart.setBackgroundColor(yellowOpacity);

        chart.setData(data);
        chart.invalidate();
    }

    private void decorateLegend() {

        Legend legend = chart.getLegend();
        legend.setTextSize(12f);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);


    }

    private void decorateYaxis() {
        removeYaxisRight();

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setTextSize(20f);

    }

    private void removeYaxisRight() {
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);
    }

    private void decorateXaxis() {
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(15f);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DateAxisXValueFormatter());
    }

    private void drawWeightGoalLine() {

        if(userWeightGoal <= 0){
            return;
        }

        YAxis leftAxis = chart.getAxisLeft();
        LimitLine weightGoalLine = new LimitLine(userWeightGoal,"Weight Goal: "+userWeightGoal+" Kg");

        int OpacityBlueColor = Color.argb(80, 0,0,255);
        weightGoalLine.setLineColor(OpacityBlueColor);


        weightGoalLine.enableDashedLine(30, 20, 0);
        weightGoalLine.setLineWidth(2f);
        weightGoalLine.setTextSize(40f);
        int opacityBlackColor = Color.argb(100,0,0,0);
        weightGoalLine.setTextColor(opacityBlackColor);
        leftAxis.addLimitLine(weightGoalLine);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }
}
