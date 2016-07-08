package creator.soft.cygi.com.friendlyloseweighthelper;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Created by CygiMasterProgrammer on 2016-03-20.
 */
public class DateXAxisValueFormatterTest {

    @Test
    public void originalValueMustBeChangeToDifferentFormatting() throws Exception {

        DateAxisXValueFormatter dateAxisXValueFormatter = new DateAxisXValueFormatter();

        String originalValue = "18-02-2016 8:44:21 PM";
        String expectedValue = "18-02-2016 [ 8:44:21 PM ]";

        String changeValue = dateAxisXValueFormatter.getXValue(originalValue, 0, null);

        assertEquals(expectedValue, changeValue);
    }

}