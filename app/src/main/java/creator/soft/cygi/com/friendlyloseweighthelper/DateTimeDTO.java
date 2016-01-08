package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;

import java.util.Date;

/**
 * Created by CygiMasterProgrammer on 2015-12-20.
 */
public class DateTimeDTO {
    private static final String TAG = "DateTimeDTO";
    Date date;
    Float weight;
    Context context;

    public void setDate(String date) {

        if(date == null)
        {
            return;
        }

        this.date = DateTimeStringUtility.changeToDate(date);

    }

    public Date getDate() {

    return date;
    }

    public String getDateWithoutFormatting() {
        return DateTimeStringUtility.changeToStringRepresentation(date);
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getWeight(){
        return weight;
    }

    public String getFormattedDate() {

      return DateTimeStringUtility.formatDate(date);
    }

    public String getFormattedTime() {

      return  DateTimeStringUtility.formatTime(context,date);
    }

    public void setAndroidContext(Context context){
        this.context = context;
    }

}
