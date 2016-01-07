package creator.soft.cygi.com.friendlyloseweighthelper;

/**
 * Created by CygiMasterProgrammer on 2016-01-05.
 */
public interface NotificationObserver {


    public void onDatabaseIsEmpty();
    public void onDatabaseNotEmpty();
    public void onNoMeasurementToUndo();
    public void onUndoStackNotEmpty();

    public void onMeasurementDeletion(DateTimeDTO dateTimeDTO);


}
