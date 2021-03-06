package creator.soft.cygi.com.friendlyloseweighthelper.model;

import creator.soft.cygi.com.friendlyloseweighthelper.model.WeightDataObserver;

/**
 * Created by CygiMasterProgrammer on 2016-01-13.
 */
public interface WeightDataSubject {

    public void addWeightDataObserver(WeightDataObserver weightDataObserver);

    public void removeWightDataObserver(WeightDataObserver weightDataObserver);

    public void notifyPositionChanged();
}
