package creator.soft.cygi.com.friendlyloseweighthelper.utility;

/**
 * Created by CygiMasterProgrammer on 2016-09-27.
 */
public interface LatestWeightSubject {

    void addLatestWeightObserver(LatestWeightObserver latestWeightObserver);

    void removeLatestWeightObserver(LatestWeightObserver latestWeightObserver);

    void notifyLatestWeightObservers();

}
