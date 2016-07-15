package creator.soft.cygi.com.friendlyloseweighthelper.dao;

import creator.soft.cygi.com.friendlyloseweighthelper.dao.DatabaseNotificationObserver;
import creator.soft.cygi.com.friendlyloseweighthelper.dto.DateTimeDTO;

/**
 * Created by CygiMasterProgrammer on 2016-01-05.
 */
public interface DatabaseNotificationSubject {

    public void addNotificationObserver(DatabaseNotificationObserver notificationObserver);

    public void removeNotificationObserver(DatabaseNotificationObserver notificationObserver);

    public void notifyTableMeasurementIsEmpty() ;
    public void notifyTableMeasurementIsNotEmpty();
    public void notifyNoMeasurementToUndo();
    public void notifyUndoStackIsNotEmpty();
    public void notifyMeasurementDeletion(DateTimeDTO dateTimeDTO);
    public void notifyMeasurementUndoDeletion(DateTimeDTO dateTimeDTO);
    public void notifyMeasurementNotInserted();
    public void notifyMeasurementInserted();

}
