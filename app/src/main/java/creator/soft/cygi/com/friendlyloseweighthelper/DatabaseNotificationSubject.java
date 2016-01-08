package creator.soft.cygi.com.friendlyloseweighthelper;

import java.util.List;

/**
 * Created by CygiMasterProgrammer on 2016-01-05.
 */
public interface DatabaseNotificationSubject {

    public void addNotificationObserver(NotificationObserver notificationObserver);

    public void removeNotificationObserver(NotificationObserver notificationObserver);

    public void notifyDatabaseIsEmpty() ;
    public void notifyDatabaseNotEmpty();
    public void notifyNoMeasurementToUndo();
    public void notifyUndoStackIsNotEmpty();
    public void notifyMeasurementDeletion(DateTimeDTO dateTimeDTO);
    public void notifyMeasurementUndoDeletion(DateTimeDTO dateTimeDTO);

}
