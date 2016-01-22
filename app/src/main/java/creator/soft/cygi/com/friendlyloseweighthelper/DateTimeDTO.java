package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;

import java.util.Date;

/**
 * Created by CygiMasterProgrammer on 2015-12-20.
 */
public class DateTimeDTO {
    private static final String TAG = "DateTimeDTO";
    private Integer measurementID;
    private Context context;
    private Date date;
    private Float weight;

    public Integer getMeasurementID() {
        return measurementID;
    }

    public void setMeasurementID(Integer measurementID) {
        this.measurementID = measurementID;
    }

    public Date getDate() {

        return date;
    }

    public void setDate(String date) {

        if (date == null) {
            return;
        }

        this.date = DateTimeStringUtility.changeToDate(date);

    }

    public String getDateWithoutFormatting() {
        return DateTimeStringUtility.changeToStringRepresentation(date);
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getFormattedDate() {

        return DateTimeStringUtility.formatDate(date);
    }

    public String getFormattedTime() {

        return DateTimeStringUtility.formatTime(context, date);
    }

    public void setAndroidContext(Context context) {
        this.context = context;
    }

}
