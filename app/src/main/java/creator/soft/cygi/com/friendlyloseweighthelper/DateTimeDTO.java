package creator.soft.cygi.com.friendlyloseweighthelper;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by CygiMasterProgrammer on 2015-12-20.
 */
public class DateTimeDTO {


    private static final String TAG = "DateTimeDTO";
    Date date;
    Float weight;

    public void setDate(String date) {

        DateFormat formatDate = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);

        try {
            this.date = formatDate.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Date getDate() {

    return date;
    }

    public Float getWeight(){
        return weight;
    }

}
