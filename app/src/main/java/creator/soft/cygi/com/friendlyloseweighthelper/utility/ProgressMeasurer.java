package creator.soft.cygi.com.friendlyloseweighthelper.utility;

import android.support.annotation.VisibleForTesting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import creator.soft.cygi.com.friendlyloseweighthelper.dao.WeightTrackDatabaseHelper;
import creator.soft.cygi.com.friendlyloseweighthelper.dto.DateTimeDTO;


public class ProgressMeasurer {

    private static final String UNKNOWN = "Unknown";
    private WeightTrackDatabaseHelper weightTrackDatabaseHelper;
    private DateTimeDTO fistDayMeasurements;
    private DateTimeDTO latestDayMeasurements;
    private DateTimeDTO theHighestWeightMeasurement;
    private WeightIndicator weightIndicator = WeightIndicator.UNDEFINED;
    private float averageDailyChangeFromHighestWeightMeasurementToLatest = 0;
    private float averageDailyChangeForAllMeasurements = 0;
    private float weightDifferenceBetweenFirstAndLatestDate = 0;
    private float weightDifferenceBetweenHighestWeightMeasurementAndLatestDateMeasurement = 0;
    private long numberOfMeasurementFromHighestMeasurementToLatestMeasurementIncludedHighest = 0;
    private long numberOfAllMeasurementMade = 0;
    private float numberOfDaysLeftToAchieveWeighGoal = 0;
    private String dateOfAchievingAGoal;
    private float userWeightGoal = 0;
    private Date todayDate = new Date();

    public ProgressMeasurer() {
    }

    @VisibleForTesting
    public void setTodayDate(Date todayDate) {
        this.todayDate = todayDate;
    }

    public DateTimeDTO getTheHighestWeightMeasurement() {
        return theHighestWeightMeasurement;
    }

    public void setWeightTrackDatabaseHelper(WeightTrackDatabaseHelper weightTrackDatabaseHelper) {
        this.weightTrackDatabaseHelper = weightTrackDatabaseHelper;
    }

    public float getAverageDailyChangeForAllMeasurements() {
        return averageDailyChangeForAllMeasurements;
    }

    public String getDateOfAchievingAGoal() {
        return dateOfAchievingAGoal;
    }

    public long getNumberOfAllMeasurementMade() {
        return numberOfAllMeasurementMade;
    }

    public long getNumberOfMeasurementFromHighestMeasurementToLatestMeasurementIncludedHighest() {
        return numberOfMeasurementFromHighestMeasurementToLatestMeasurementIncludedHighest;
    }

    public WeightIndicator getWeightIndicator() {
        return weightIndicator;
    }

    public float getWeightDifferenceBetweenFirstAndLatestDate() {
        return weightDifferenceBetweenFirstAndLatestDate;
    }

    public String getDaysLeftToAchieveWeightGoal() {

        if (numberOfDaysLeftToAchieveWeighGoal == Float.POSITIVE_INFINITY) {
            return UNKNOWN;
        }

        if (numberOfDaysLeftToAchieveWeighGoal == Float.NEGATIVE_INFINITY) {
            return UNKNOWN;
        }

        float daysRound = Math.round(numberOfDaysLeftToAchieveWeighGoal);
        int onlyDaysWhiteoutPointers = (int) daysRound;

        return String.valueOf(Math.abs(onlyDaysWhiteoutPointers));
    }

    public float getWeightDifferenceBetweenHighestWeightMeasurementAndLatestDateMeasurement() {
        return weightDifferenceBetweenHighestWeightMeasurementAndLatestDateMeasurement;
    }

    public String getAverageDailyChangeLosingTheWeightFromHighestWeightMeasurementToLatest() {
        return String.valueOf(averageDailyChangeFromHighestWeightMeasurementToLatest);
    }

    public void makeCalculation() {

        readOldestAndLatestMeasurement();
        getMeasurementWithHighestWeightFromDatabase();
        calculateWeightDifferenceBetweenOldestAndLatestMeasurement();
        calculateWeightDifferenceBetweenHighestWeightMeasurementAndLatestMeasurement();
        determineWeightDirection();
        countNumberOfMeasurementFromHighestWeightToLatestMeasurement();
        countNumberOfMeasurement();
        readUserWeightGoal();
        averageWeightChangeForAllMeasurement();
        measureAverageChangeInOneDayFromHighestWeightMeasurementToLatest();
        calculateNumberOfDaysLeftToAchieveGoal();
        calculateDateOfAchievingGoal();

    }

    private void calculateDateOfAchievingGoal() {

        final String numberOfDaysToAchieveGoalInText = getDaysLeftToAchieveWeightGoal();

        if(isDigitOnly(numberOfDaysToAchieveGoalInText)) {

            final int numberOfDaysToAchieveGoal = Integer.valueOf(numberOfDaysToAchieveGoalInText);

            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(todayDate);

            calendar.add(Calendar.DATE, numberOfDaysToAchieveGoal);
            dateOfAchievingAGoal = simpleDateFormat.format(calendar.getTime());
        }else {
            dateOfAchievingAGoal = UNKNOWN;
        }
    }

    private boolean isDigitOnly(CharSequence str){
        final int len = str.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    private void calculateNumberOfDaysLeftToAchieveGoal() {

        final float latestDateWeightMeasurement = latestDayMeasurements.getWeight();

        final float numberOfWeightToLoseLeft = latestDateWeightMeasurement - userWeightGoal;

        numberOfDaysLeftToAchieveWeighGoal = numberOfWeightToLoseLeft / averageDailyChangeFromHighestWeightMeasurementToLatest;

    }

    private void getMeasurementWithHighestWeightFromDatabase() {
        theHighestWeightMeasurement = weightTrackDatabaseHelper.getMeasurementWithHighestWeight();
    }

    private void readUserWeightGoal() {
        userWeightGoal = weightTrackDatabaseHelper.getWeightGoalOfCurrentUser();
    }

    private void readOldestAndLatestMeasurement() {
        fistDayMeasurements = weightTrackDatabaseHelper.getMeasurementInAFirstDay();
        latestDayMeasurements = weightTrackDatabaseHelper.getMeasurementInLatestDay();
    }

    private void measureAverageChangeInOneDayFromHighestWeightMeasurementToLatest() {

        final long numberOfIntervalsBetweenDays = numberOfMeasurementFromHighestMeasurementToLatestMeasurementIncludedHighest - 1;
        if (numberOfIntervalsBetweenDays > 0) {

            averageDailyChangeFromHighestWeightMeasurementToLatest = weightDifferenceBetweenHighestWeightMeasurementAndLatestDateMeasurement / numberOfIntervalsBetweenDays;
            averageDailyChangeFromHighestWeightMeasurementToLatest = roundNumberToTwoPrecisionPoint(averageDailyChangeFromHighestWeightMeasurementToLatest);

        }

    }

    protected void averageWeightChangeForAllMeasurement() {

        final long numberOfIntervalsBetweenDays = numberOfAllMeasurementMade - 1;
        if (numberOfIntervalsBetweenDays > 0) {
            averageDailyChangeForAllMeasurements = weightDifferenceBetweenFirstAndLatestDate / numberOfIntervalsBetweenDays;
            averageDailyChangeForAllMeasurements = roundNumberToTwoPrecisionPoint(averageDailyChangeForAllMeasurements);
        }

    }

    private float roundNumberToTwoPrecisionPoint(float number) {
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        float roundNumber = bd.floatValue();
        return roundNumber;
    }

    private void countNumberOfMeasurementFromHighestWeightToLatestMeasurement() {

        numberOfMeasurementFromHighestMeasurementToLatestMeasurementIncludedHighest = weightTrackDatabaseHelper.countNumberOfMeasurementFromHighestWeightMeasurementToLatest();
    }

    private void countNumberOfMeasurement() {
        numberOfAllMeasurementMade = weightTrackDatabaseHelper.numberOfMeasurementDataForCurrentUser();
    }

    private void calculateWeightDifferenceBetweenOldestAndLatestMeasurement() {
        float weightAtTheStart = fistDayMeasurements.getWeight();
        float weightLatest = latestDayMeasurements.getWeight();
        weightDifferenceBetweenFirstAndLatestDate = weightLatest - weightAtTheStart;
    }

    private void calculateWeightDifferenceBetweenHighestWeightMeasurementAndLatestMeasurement() {

        float highestWeightMeasurement = theHighestWeightMeasurement.getWeight();
        float weightLatest = latestDayMeasurements.getWeight();

        weightDifferenceBetweenHighestWeightMeasurementAndLatestDateMeasurement = weightLatest - highestWeightMeasurement;

    }

    private void determineWeightDirection() {

        if (weightDifferenceBetweenFirstAndLatestDate == 0) {
            weightIndicator = WeightIndicator.SAME;
        }
        if (weightDifferenceBetweenFirstAndLatestDate < 0) {
            weightIndicator = WeightIndicator.DOWN;
        }
        if (weightDifferenceBetweenFirstAndLatestDate > 0) {
            weightIndicator = WeightIndicator.UP;
        }

    }

}
