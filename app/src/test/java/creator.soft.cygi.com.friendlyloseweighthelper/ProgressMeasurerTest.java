package creator.soft.cygi.com.friendlyloseweighthelper;


import android.preference.PreferenceManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import creator.soft.cygi.com.friendlyloseweighthelper.dao.WeightTrackDatabaseHelper;
import creator.soft.cygi.com.friendlyloseweighthelper.dto.DateTimeDTO;
import creator.soft.cygi.com.friendlyloseweighthelper.utility.ProgressMeasurer;
import creator.soft.cygi.com.friendlyloseweighthelper.utility.WeightIndicator;

import static junit.framework.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PreferenceManager.class, ProgressMeasurer.class})
public class ProgressMeasurerTest {

    private static final String UNKNOWN = "Unknown";

    @Mock
    private PreferenceManager preferenceManager;

    @Mock
    private WeightTrackDatabaseHelper weightTrackDatabaseHelper;

    private final ProgressMeasurer progressMeasurer = new ProgressMeasurer();



    @Test
    public void resultsWhenWeightGoDownTest() throws Exception {

        final DateTimeDTO fistDayMeasurement = createMeasurement(110f, MeasurementType.FIRST_DAY);
        final DateTimeDTO latestDayMeasurement = createMeasurement(110f, MeasurementType.LAST_DAY);
        final DateTimeDTO highestWeightMeasurement = createMeasurement(110f, MeasurementType.HIGHEST_WEIGHT);

        final MockRuleCreator mockRuleCreator = new MockRuleCreator.MockRuleCreatorBuilder(weightTrackDatabaseHelper)
                .firstDayMeasurement(fistDayMeasurement)
                .lastDayMeasurement(latestDayMeasurement)
                .highestWeightMeasurement(highestWeightMeasurement)
                .numberOfAllMeasurements(1l)
                .numberOfMeasurementFromHighestToLatest(1)
                .weightGoal(90f)
                .build();

        mockRuleCreator.applyMockRules();

        initializeStartDate();

        progressMeasurer.setWeightTrackDatabaseHelper(mockRuleCreator.getMockWeightTrackDatabaseHelper());
        progressMeasurer.makeCalculation();
        WeightIndicator weightIndicator = progressMeasurer.getWeightIndicator();

        System.out.println("Avreage daly change losing the weight from highest: " + progressMeasurer.getAverageDailyChangeLosingTheWeightFromHighestWeightMeasurementToLatest());
        System.out.println("Avreage daly change for all Measurement: " + progressMeasurer.getAverageDailyChangeForAllMeasurements());
        System.out.println("Weight indicator:  " + weightIndicator);
        System.out.println("The highest Weight Measurement:  " + progressMeasurer.getTheHighestWeightMeasurement().getWeight());
        System.out.println("The Difference between highest and Latest measurement:  " + progressMeasurer.getWeightDifferenceBetweenHighestWeightMeasurementAndLatestDateMeasurement());
        System.out.println("Weight Difference between Oldest and Latest measurement: " + progressMeasurer.getWeightDifferenceBetweenFirstAndLatestDate());
        System.out.println("Days left to achieve goal: " + progressMeasurer.getDaysLeftToAchieveWeightGoal());
        System.out.println("Date of achiving goal: " + progressMeasurer.getDateOfAchievingAGoal());

    }


    @Test
    public void whenMeasurementWithHighestWeightBiggerThanLastDayMeasurement() throws Exception {

        final DateTimeDTO fistDayMeasurement = createMeasurement(110f, MeasurementType.FIRST_DAY);
        final DateTimeDTO latestDayMeasurement = createMeasurement(120f, MeasurementType.LAST_DAY);
        final DateTimeDTO highestWeightMeasurement = createMeasurement(140f, MeasurementType.HIGHEST_WEIGHT);

        final MockRuleCreator mockRuleCreator = new MockRuleCreator.MockRuleCreatorBuilder(weightTrackDatabaseHelper)
                .firstDayMeasurement(fistDayMeasurement)
                .lastDayMeasurement(latestDayMeasurement)
                .highestWeightMeasurement(highestWeightMeasurement)
                .numberOfAllMeasurements(100l)
                .numberOfMeasurementFromHighestToLatest(60)
                .weightGoal(90f)
                .build();

        mockRuleCreator.applyMockRules();

        initializeStartDate();
        progressMeasurer.setWeightTrackDatabaseHelper(weightTrackDatabaseHelper);
        progressMeasurer.makeCalculation();


        String expectedAverageWeightFromHighest = "-0.34";
        float expectedAverageWeightChangingFromFirstDayToLatest = 0.1f;
        WeightIndicator expectedWeightIndicator = WeightIndicator.UP;
        float expectedDifferenceBetweenOldestAndLatestMeasurement = 10;
        float expectedDifferenceBetweenHighestWeightAndLatestMeasurement = -20;
        String expectedDaysToAchieveGoal = "88";
        String expectedDateOfAchievingAGoal = "17-08-2015";

        assertEquals(expectedAverageWeightFromHighest,progressMeasurer.getAverageDailyChangeLosingTheWeightFromHighestWeightMeasurementToLatest());
        assertEquals(expectedAverageWeightChangingFromFirstDayToLatest,progressMeasurer.getAverageDailyChangeForAllMeasurements());
        assertEquals(expectedWeightIndicator,progressMeasurer.getWeightIndicator());
        assertEquals(expectedDifferenceBetweenOldestAndLatestMeasurement,progressMeasurer.getWeightDifferenceBetweenFirstAndLatestDate());
        assertEquals(expectedDifferenceBetweenHighestWeightAndLatestMeasurement,progressMeasurer.getWeightDifferenceBetweenHighestWeightMeasurementAndLatestDateMeasurement());
        assertEquals(expectedDaysToAchieveGoal,progressMeasurer.getDaysLeftToAchieveWeightGoal());
        assertEquals(expectedDateOfAchievingAGoal,progressMeasurer.getDateOfAchievingAGoal());


    }

    @Test
    public void whenMeasurementWithHighestWeightSameAsThanLastDayMeasurementThenDaysLeftToAchieveGoalNot_known() throws Exception {

        final DateTimeDTO fistDayMeasurement = createMeasurement(110f, MeasurementType.FIRST_DAY);
        final DateTimeDTO latestDayMeasurement = createMeasurement(140f, MeasurementType.LAST_DAY);
        final DateTimeDTO highestWeightMeasurement = createMeasurement(140f, MeasurementType.HIGHEST_WEIGHT);

        final MockRuleCreator mockRuleCreator = new MockRuleCreator.MockRuleCreatorBuilder(weightTrackDatabaseHelper)
                .firstDayMeasurement(fistDayMeasurement)
                .lastDayMeasurement(latestDayMeasurement)
                .highestWeightMeasurement(highestWeightMeasurement)
                .numberOfAllMeasurements(100l)
                .numberOfMeasurementFromHighestToLatest(100)
                .weightGoal(90f)
                .build();

        mockRuleCreator.applyMockRules();
        initializeStartDate();
        progressMeasurer.setWeightTrackDatabaseHelper(weightTrackDatabaseHelper);
        progressMeasurer.makeCalculation();

        String expectedAverageWeightFromHighest = "0.0";
        float expectedAverageWeightChangingFromFirstDayToLatest = 0.3f;
        WeightIndicator expectedWeightIndicator = WeightIndicator.UP;
        float expectedDifferenceBetweenOldestAndLatestMeasurement = 30;
        float expectedDifferenceBetweenHighestWeightAndLatestMeasurement = 0;
        String expectedDaysToAchieveGoal = UNKNOWN;
        String expectedDateOfAchievingAGoal = UNKNOWN;

        assertEquals(expectedAverageWeightFromHighest,progressMeasurer.getAverageDailyChangeLosingTheWeightFromHighestWeightMeasurementToLatest());
        assertEquals(expectedAverageWeightChangingFromFirstDayToLatest,progressMeasurer.getAverageDailyChangeForAllMeasurements());
        assertEquals(expectedWeightIndicator,progressMeasurer.getWeightIndicator());
        assertEquals(expectedDifferenceBetweenOldestAndLatestMeasurement,progressMeasurer.getWeightDifferenceBetweenFirstAndLatestDate());
        assertEquals(expectedDifferenceBetweenHighestWeightAndLatestMeasurement,progressMeasurer.getWeightDifferenceBetweenHighestWeightMeasurementAndLatestDateMeasurement());
        assertEquals(expectedDaysToAchieveGoal,progressMeasurer.getDaysLeftToAchieveWeightGoal());
        assertEquals(expectedDateOfAchievingAGoal,progressMeasurer.getDateOfAchievingAGoal());


    }

    @Test
    public void whenOnlyOneMeasurementInDatabase() throws Exception {

        final DateTimeDTO fistDayMeasurement = createMeasurement(110f, MeasurementType.FIRST_DAY);
        final DateTimeDTO latestDayMeasurement = createMeasurement(110f, MeasurementType.LAST_DAY);
        final DateTimeDTO highestWeightMeasurement = createMeasurement(110f, MeasurementType.HIGHEST_WEIGHT);

        final MockRuleCreator mockRuleCreator = new MockRuleCreator.MockRuleCreatorBuilder(weightTrackDatabaseHelper)
                .firstDayMeasurement(fistDayMeasurement)
                .lastDayMeasurement(latestDayMeasurement)
                .highestWeightMeasurement(highestWeightMeasurement)
                .numberOfAllMeasurements(1)
                .numberOfMeasurementFromHighestToLatest(1)
                .weightGoal(90f)
                .build();

        mockRuleCreator.applyMockRules();
        initializeStartDate();
        progressMeasurer.setWeightTrackDatabaseHelper(weightTrackDatabaseHelper);
        progressMeasurer.makeCalculation();

        String expectedAverageWeightFromHighest = "0.0";
        float expectedAverageWeightChangingFromFirstDayToLatest = 0.0f;
        WeightIndicator expectedWeightIndicator = WeightIndicator.SAME;
        float expectedDifferenceBetweenOldestAndLatestMeasurement = 0.0f;
        float expectedDifferenceBetweenHighestWeightAndLatestMeasurement = 0.0f;
        String expectedDaysToAchieveGoal = UNKNOWN;
        String expectedDateOfAchievingAGoal = UNKNOWN;

        assertEquals(expectedAverageWeightFromHighest,progressMeasurer.getAverageDailyChangeLosingTheWeightFromHighestWeightMeasurementToLatest());
        assertEquals(expectedAverageWeightChangingFromFirstDayToLatest,progressMeasurer.getAverageDailyChangeForAllMeasurements());
        assertEquals(expectedWeightIndicator,progressMeasurer.getWeightIndicator());
        assertEquals(expectedDifferenceBetweenOldestAndLatestMeasurement,progressMeasurer.getWeightDifferenceBetweenFirstAndLatestDate());
        assertEquals(expectedDifferenceBetweenHighestWeightAndLatestMeasurement,progressMeasurer.getWeightDifferenceBetweenHighestWeightMeasurementAndLatestDateMeasurement());
        assertEquals(expectedDaysToAchieveGoal,progressMeasurer.getDaysLeftToAchieveWeightGoal());
        assertEquals(expectedDateOfAchievingAGoal,progressMeasurer.getDateOfAchievingAGoal());


    }

    @Test
    public void whenOldestMeasurementAndHighestMeasurementAreTheSame() throws Exception {

        final DateTimeDTO fistDayMeasurement = createMeasurement(140f, MeasurementType.FIRST_DAY);
        final DateTimeDTO latestDayMeasurement = createMeasurement(110f, MeasurementType.LAST_DAY);
        final DateTimeDTO highestWeightMeasurement = createMeasurement(140f, MeasurementType.HIGHEST_WEIGHT);

        final MockRuleCreator mockRuleCreator = new MockRuleCreator.MockRuleCreatorBuilder(weightTrackDatabaseHelper)
                .firstDayMeasurement(fistDayMeasurement)
                .lastDayMeasurement(latestDayMeasurement)
                .highestWeightMeasurement(highestWeightMeasurement)
                .numberOfAllMeasurements(150)
                .numberOfMeasurementFromHighestToLatest(150)
                .weightGoal(90f)
                .build();

        mockRuleCreator.applyMockRules();
        initializeStartDate();
        progressMeasurer.setWeightTrackDatabaseHelper(weightTrackDatabaseHelper);
        progressMeasurer.makeCalculation();

        String expectedAverageWeightFromHighest = "-0.2";
        float expectedAverageWeightChangingFromFirstDayToLatest = -0.2f;
        WeightIndicator expectedWeightIndicator = WeightIndicator.DOWN;
        float expectedDifferenceBetweenOldestAndLatestMeasurement = -30f;
        float expectedDifferenceBetweenHighestWeightAndLatestMeasurement = -30f;
        String expectedDaysToAchieveGoal = "100";
        String expectedDateOfAchievingAGoal = "29-08-2015";

        assertEquals(expectedAverageWeightFromHighest,progressMeasurer.getAverageDailyChangeLosingTheWeightFromHighestWeightMeasurementToLatest());
        assertEquals(expectedAverageWeightChangingFromFirstDayToLatest,progressMeasurer.getAverageDailyChangeForAllMeasurements());
        assertEquals(expectedWeightIndicator,progressMeasurer.getWeightIndicator());
        assertEquals(expectedDifferenceBetweenOldestAndLatestMeasurement,progressMeasurer.getWeightDifferenceBetweenFirstAndLatestDate());
        assertEquals(expectedDifferenceBetweenHighestWeightAndLatestMeasurement,progressMeasurer.getWeightDifferenceBetweenHighestWeightMeasurementAndLatestDateMeasurement());
        assertEquals(expectedDaysToAchieveGoal,progressMeasurer.getDaysLeftToAchieveWeightGoal());
        assertEquals(expectedDateOfAchievingAGoal,progressMeasurer.getDateOfAchievingAGoal());


    }

    @Test
    public void whenOldestMeasurementAndHighestMeasurementHaveSameWeightButHaveDifferentDate() throws Exception {

        final DateTimeDTO fistDayMeasurement = createMeasurement(140f, MeasurementType.FIRST_DAY);
        final DateTimeDTO latestDayMeasurement = createMeasurement(110f, MeasurementType.LAST_DAY);
        final DateTimeDTO highestWeightMeasurement = createMeasurement(140f, MeasurementType.HIGHEST_WEIGHT);

        final MockRuleCreator mockRuleCreator = new MockRuleCreator.MockRuleCreatorBuilder(weightTrackDatabaseHelper)
                .firstDayMeasurement(fistDayMeasurement)
                .lastDayMeasurement(latestDayMeasurement)
                .highestWeightMeasurement(highestWeightMeasurement)
                .numberOfAllMeasurements(150)
                .numberOfMeasurementFromHighestToLatest(40)
                .weightGoal(90f)
                .build();

        mockRuleCreator.applyMockRules();
        initializeStartDate();
        progressMeasurer.setWeightTrackDatabaseHelper(weightTrackDatabaseHelper);
        progressMeasurer.makeCalculation();

        String expectedAverageWeightFromHighest = "-0.77";
        float expectedAverageWeightChangingFromFirstDayToLatest = -0.2f;
        WeightIndicator expectedWeightIndicator = WeightIndicator.DOWN;
        float expectedDifferenceBetweenOldestAndLatestMeasurement = -30f;
        float expectedDifferenceBetweenHighestWeightAndLatestMeasurement = -30f;
        String expectedDaysToAchieveGoal = "26";
        String expectedDateOfAchievingAGoal = "16-06-2015";

        assertEquals(expectedAverageWeightFromHighest,progressMeasurer.getAverageDailyChangeLosingTheWeightFromHighestWeightMeasurementToLatest());
        assertEquals(expectedAverageWeightChangingFromFirstDayToLatest,progressMeasurer.getAverageDailyChangeForAllMeasurements());
        assertEquals(expectedWeightIndicator,progressMeasurer.getWeightIndicator());
        assertEquals(expectedDifferenceBetweenOldestAndLatestMeasurement,progressMeasurer.getWeightDifferenceBetweenFirstAndLatestDate());
        assertEquals(expectedDifferenceBetweenHighestWeightAndLatestMeasurement,progressMeasurer.getWeightDifferenceBetweenHighestWeightMeasurementAndLatestDateMeasurement());
        assertEquals(expectedDaysToAchieveGoal,progressMeasurer.getDaysLeftToAchieveWeightGoal());
        assertEquals(expectedDateOfAchievingAGoal,progressMeasurer.getDateOfAchievingAGoal());

    }



    @Test
    public void whenDatabaseIsEmptyNoMeasurements() throws Exception {

        final DateTimeDTO fistDayMeasurement = new DateTimeDTO();
        fistDayMeasurement.setWeight(0f);

        final DateTimeDTO latestDayMeasurement = new DateTimeDTO();
        latestDayMeasurement.setWeight(0f);

        final DateTimeDTO highestWeightMeasurement = new DateTimeDTO();
        highestWeightMeasurement.setWeight(0f);


        final MockRuleCreator mockRuleCreator = new MockRuleCreator.MockRuleCreatorBuilder(weightTrackDatabaseHelper)
                .firstDayMeasurement(fistDayMeasurement)
                .lastDayMeasurement(latestDayMeasurement)
                .highestWeightMeasurement(highestWeightMeasurement)
                .numberOfAllMeasurements(0)
                .numberOfMeasurementFromHighestToLatest(0)
                .weightGoal(90f)
                .build();

        mockRuleCreator.applyMockRules();
        initializeStartDate();
        progressMeasurer.setWeightTrackDatabaseHelper(weightTrackDatabaseHelper);
        progressMeasurer.makeCalculation();

        String expectedAverageWeightFromHighest = "0.0";
        float expectedAverageWeightChangingFromFirstDayToLatest = 0.0f;
        WeightIndicator expectedWeightIndicator = WeightIndicator.SAME;
        float expectedDifferenceBetweenOldestAndLatestMeasurement = 0.0f;
        float expectedDifferenceBetweenHighestWeightAndLatestMeasurement = 0.0f;
        String expectedDaysToAchieveGoal = UNKNOWN;
        String expectedDateOfAchievingAGoal = UNKNOWN;

        assertEquals(expectedAverageWeightFromHighest,progressMeasurer.getAverageDailyChangeLosingTheWeightFromHighestWeightMeasurementToLatest());
        assertEquals(expectedAverageWeightChangingFromFirstDayToLatest,progressMeasurer.getAverageDailyChangeForAllMeasurements());
        assertEquals(expectedWeightIndicator,progressMeasurer.getWeightIndicator());
        assertEquals(expectedDifferenceBetweenOldestAndLatestMeasurement,progressMeasurer.getWeightDifferenceBetweenFirstAndLatestDate());
        assertEquals(expectedDifferenceBetweenHighestWeightAndLatestMeasurement,progressMeasurer.getWeightDifferenceBetweenHighestWeightMeasurementAndLatestDateMeasurement());
        assertEquals(expectedDaysToAchieveGoal,progressMeasurer.getDaysLeftToAchieveWeightGoal());
        assertEquals(expectedDateOfAchievingAGoal,progressMeasurer.getDateOfAchievingAGoal());

    }

    private void initializeStartDate() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date todayDate = sdf.parse("21-05-2015");
        progressMeasurer.setTodayDate(todayDate);
    }

    private DateTimeDTO createMeasurement(float weight, MeasurementType measurementType) {

        DateTimeDTO dateTimeDTO = new DateTimeDTO();
        String firstDayMeasurement = "2015-02-12 01:10:00";
        String lastDayMeasurement = "2016-08-12 06:10:00";
        String HighestWeightMeasurement = "2016-01-12 06:10:00";

        if (measurementType == MeasurementType.FIRST_DAY) {
            dateTimeDTO.setWeight(weight);
            dateTimeDTO.setDate(firstDayMeasurement);
            dateTimeDTO.setMeasurementID(1);
        }

        if (measurementType == MeasurementType.LAST_DAY) {
            dateTimeDTO.setWeight(weight);
            dateTimeDTO.setDate(lastDayMeasurement);
            dateTimeDTO.setMeasurementID(100);

        }
        if (measurementType == MeasurementType.HIGHEST_WEIGHT) {
            dateTimeDTO.setWeight(weight);
            dateTimeDTO.setDate(HighestWeightMeasurement);
            dateTimeDTO.setMeasurementID(65);
        }

        return dateTimeDTO;
    }

}
