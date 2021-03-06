package creator.soft.cygi.com.friendlyloseweighthelper.dto;

import android.content.Context;

import java.util.Date;

import creator.soft.cygi.com.friendlyloseweighthelper.utility.DateTimeStringUtility;

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

        this.date = DateTimeStringUtility.changeToDateFromDatabaseFormat(date);

    }

    public String getDateWithoutFormatting() {
        return DateTimeStringUtility.changeToStringRepresentationLikeInDatabase(date);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateTimeDTO)) return false;

        DateTimeDTO that = (DateTimeDTO) o;

        if (measurementID != null ? !measurementID.equals(that.measurementID) : that.measurementID != null)
            return false;
        if (!date.equals(that.date)) return false;
        return weight.equals(that.weight);

    }

    @Override
    public int hashCode() {
        int result = measurementID != null ? measurementID.hashCode() : 0;
        result = 31 * result + date.hashCode();
        result = 31 * result + weight.hashCode();
        return result;
    }
}
