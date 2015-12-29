package creator.soft.cygi.com.friendlyloseweighthelper;

import java.util.Date;

/**
 * Created by CygiMasterProgrammer on 2015-12-20.
 */
public class DateTimeDTO {


    private static final String TAG = "DateTimeDTO";
    Date date;
    Float weight;

    public void setDate(String date) {

        if(date == null)
        {
            return;
        }

        this.date = DateStringUtility.changeToDate(date);

    }

    public Date getDate() {

    return date;
    }

    public String getDateInString() {
        return DateStringUtility.changeToStringRepresentation(date);
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public Float getWeight(){
        return weight;
    }

}
