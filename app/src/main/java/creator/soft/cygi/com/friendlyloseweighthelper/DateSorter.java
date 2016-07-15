package creator.soft.cygi.com.friendlyloseweighthelper;

import java.util.Comparator;

/**
 * Created by CygiMasterProgrammer on 2016-07-14.
 */
public class DateSorter implements Comparator<DateTimeDTO> {
    @Override
    public int compare(DateTimeDTO dt1, DateTimeDTO dt2) {
        return dt1.getDate().compareTo(dt2.getDate());
    }
}
