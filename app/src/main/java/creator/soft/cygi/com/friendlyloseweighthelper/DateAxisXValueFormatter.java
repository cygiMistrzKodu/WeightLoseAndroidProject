package creator.soft.cygi.com.friendlyloseweighthelper;

import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class DateAxisXValueFormatter implements XAxisValueFormatter {
    @Override
    public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
        String dateAndTime [] = original.split(" ",2);

        String date = dateAndTime[0];
        String time = dateAndTime[1];

        String formattedString = date + "\n" +"[ " + time + " ]";

        return formattedString;
    }
}
