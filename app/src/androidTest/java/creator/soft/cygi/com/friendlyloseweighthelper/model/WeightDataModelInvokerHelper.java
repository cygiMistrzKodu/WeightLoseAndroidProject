package creator.soft.cygi.com.friendlyloseweighthelper.model;

/**
 * Created by CygiMasterProgrammer on 2016-07-26.
 */
public class WeightDataModelInvokerHelper {

    WeightDataModel  weightDataModel;

    public WeightDataModel getWeightDataModel() {
        return weightDataModel;
    }

    public void setWeightDataModel(WeightDataModel weightDataModel) {
        this.weightDataModel = weightDataModel;
    }

    public void nextMeasurementInvokeNumber(int numberToInvoke) {

        for (int i = 0; i < numberToInvoke; i++) {
            weightDataModel.getNextMeasurement();
        }
    }

    public void previousMeasurementInvokeNumber(int numberToInvoke) {

        for (int i = 0; i < numberToInvoke; i++) {
            weightDataModel.getPreviousMeasurement();
        }
    }
}
