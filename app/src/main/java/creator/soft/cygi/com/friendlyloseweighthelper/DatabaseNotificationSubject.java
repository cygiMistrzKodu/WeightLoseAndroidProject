package creator.soft.cygi.com.friendlyloseweighthelper;

/**
 * Created by CygiMasterProgrammer on 2016-01-05.
 */
public interface DatabaseNotificationSubject {

    public void addNotificationObserver(DatabaseNotificationObserver notificationObserver);

    public void removeNotificationObserver(DatabaseNotificationObserver notificationObserver);

    public void notifyDatabaseIsEmpty() ;
    public void notifyDatabaseNotEmpty();
    public void notifyNoMeasurementToUndo();
    public void notifyUndoStackIsNotEmpty();
    public void notifyMeasurementDeletion(DateTimeDTO dateTimeDTO);
    public void notifyMeasurementUndoDeletion(DateTimeDTO dateTimeDTO);
    public void notifyMeasurementNotInserted();

}
