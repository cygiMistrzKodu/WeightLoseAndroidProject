package creator.soft.cygi.com.friendlyloseweighthelper;

import org.powermock.api.mockito.PowerMockito;

import creator.soft.cygi.com.friendlyloseweighthelper.dao.WeightTrackDatabaseHelper;
import creator.soft.cygi.com.friendlyloseweighthelper.dto.DateTimeDTO;

public class MockRuleCreator {

    private final WeightTrackDatabaseHelper weightTrackDatabaseHelper;
    private final DateTimeDTO fistDayMeasurement;
    private final DateTimeDTO latestDayMeasurement;
    private final DateTimeDTO highestWeightMeasurement;
    private final long numberOfAllMeasurement;
    private final long numberOfMeasurementFromHighestWeightMeasurementToLatest;
    private final float userWeightGoal;

    private MockRuleCreator(MockRuleCreatorBuilder mockRuleCreatorBuilder) {
        this.weightTrackDatabaseHelper = mockRuleCreatorBuilder.weightTrackDatabaseHelper;
        this.fistDayMeasurement = mockRuleCreatorBuilder.firstDayMeasurement;
        this.latestDayMeasurement = mockRuleCreatorBuilder.latestDayMeasurement;
        this.highestWeightMeasurement = mockRuleCreatorBuilder.highestWeightMeasurement;
        this.numberOfAllMeasurement = mockRuleCreatorBuilder.numberOfAllMeasurement;
        this.numberOfMeasurementFromHighestWeightMeasurementToLatest = mockRuleCreatorBuilder.numberOfMeasurementFromHighestWeightMeasurementToLatest;
        this.userWeightGoal = mockRuleCreatorBuilder.userWeightGoal;

    }

    public WeightTrackDatabaseHelper getMockWeightTrackDatabaseHelper() {
        return weightTrackDatabaseHelper;
    }

    public DateTimeDTO getFistDayMeasurement() {
        return fistDayMeasurement;
    }

    public DateTimeDTO getHighestWeightMeasurement() {
        return highestWeightMeasurement;
    }

    public DateTimeDTO getLatestDayMeasurement() {
        return latestDayMeasurement;
    }

    public long getNumberOfAllMeasurement() {
        return numberOfAllMeasurement;
    }

    public long getNumberOfMeasurementFromHighestWeightMeasurementToLatest() {
        return numberOfMeasurementFromHighestWeightMeasurementToLatest;
    }

    public float getUserWeightGoal() {
        return userWeightGoal;
    }

    public void applyMockRules() throws Exception {

        PowerMockito.when(weightTrackDatabaseHelper.getMeasurementInAFirstDay()).thenReturn(fistDayMeasurement);
        PowerMockito.when(weightTrackDatabaseHelper.getMeasurementInLatestDay()).thenReturn(latestDayMeasurement);
        PowerMockito.when(weightTrackDatabaseHelper.getMeasurementWithHighestWeight()).thenReturn(highestWeightMeasurement);
        PowerMockito.when(weightTrackDatabaseHelper.countNumberOfMeasurementFromHighestWeightMeasurementToLatest()).thenReturn(numberOfMeasurementFromHighestWeightMeasurementToLatest);
        PowerMockito.when(weightTrackDatabaseHelper.getWeightGoalOfCurrentUser()).thenReturn(userWeightGoal);
        PowerMockito.when(weightTrackDatabaseHelper.numberOfMeasurementDataForCurrentUser()).thenReturn(numberOfAllMeasurement);

    }

    public static class MockRuleCreatorBuilder {

        private final WeightTrackDatabaseHelper weightTrackDatabaseHelper;
        private DateTimeDTO firstDayMeasurement;
        private DateTimeDTO latestDayMeasurement;
        private DateTimeDTO highestWeightMeasurement;
        private long numberOfAllMeasurement;
        private long numberOfMeasurementFromHighestWeightMeasurementToLatest;
        private float userWeightGoal;

        public MockRuleCreatorBuilder(WeightTrackDatabaseHelper weightTrackDatabaseHelper) {
            this.weightTrackDatabaseHelper = weightTrackDatabaseHelper;
        }

        public MockRuleCreatorBuilder firstDayMeasurement(DateTimeDTO firstDayMeasurement) {
            this.firstDayMeasurement = firstDayMeasurement;
            return this;
        }

        public MockRuleCreatorBuilder lastDayMeasurement(DateTimeDTO lastDayMeasurement) {
            this.latestDayMeasurement = lastDayMeasurement;
            return this;
        }

        public MockRuleCreatorBuilder highestWeightMeasurement(DateTimeDTO highestWeightMeasurement) {
            this.highestWeightMeasurement = highestWeightMeasurement;
            return this;
        }

        public MockRuleCreatorBuilder numberOfAllMeasurements(long allMeasurementsNumber) {
            this.numberOfAllMeasurement = allMeasurementsNumber;
            return this;
        }

        public MockRuleCreatorBuilder numberOfMeasurementFromHighestToLatest(long numberOfMeasurementFromHighestWeightMeasurementToLatest) {
            this.numberOfMeasurementFromHighestWeightMeasurementToLatest = numberOfMeasurementFromHighestWeightMeasurementToLatest;
            return this;
        }

        public MockRuleCreatorBuilder weightGoal(float userWeightGoal) {
            this.userWeightGoal = userWeightGoal;
            return this;
        }

        public MockRuleCreator build() {
            MockRuleCreator mockRuleCreator = new MockRuleCreator(this);
            return mockRuleCreator;
        }


    }
}