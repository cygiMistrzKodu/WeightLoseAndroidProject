package creator.soft.cygi.com.friendlyloseweighthelper.utility;

import java.util.Comparator;

import creator.soft.cygi.com.friendlyloseweighthelper.dto.DateTimeDTO;

/**
 * Created by CygiMasterProgrammer on 2016-07-14.
 */
public class DateSorter implements Comparator<DateTimeDTO> {
    @Override
    public int compare(DateTimeDTO dt1, DateTimeDTO dt2) {
        return dt1.getDate().compareTo(dt2.getDate());
    }
}
