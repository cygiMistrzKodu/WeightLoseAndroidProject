package creator.soft.cygi.com.friendlyloseweighthelper;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by CygiMasterProgrammer on 2015-12-20.
 */
public class ChartValueFormatter implements ValueFormatter {

    private DecimalFormat decimalValueFormat;

    public ChartValueFormatter() {
        String decimalDigitWithForDecimalPlaces = "#.####";
        decimalValueFormat = new DecimalFormat(decimalDigitWithForDecimalPlaces);
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return decimalValueFormat.format(value);
    }
}
