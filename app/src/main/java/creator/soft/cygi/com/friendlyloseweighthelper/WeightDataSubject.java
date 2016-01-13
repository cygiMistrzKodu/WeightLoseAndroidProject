package creator.soft.cygi.com.friendlyloseweighthelper;

/**
 * Created by CygiMasterProgrammer on 2016-01-13.
 */
public interface WeightDataSubject {

    public void addWightDataObserver(WeightDataObserver weightDataObserver);

    public void removeWightDataObserver(WeightDataObserver weightDataObserver);

    public void notifyPositionChanged();
}
