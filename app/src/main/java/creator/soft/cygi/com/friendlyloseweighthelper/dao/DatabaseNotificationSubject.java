package creator.soft.cygi.com.friendlyloseweighthelper.dao;

import creator.soft.cygi.com.friendlyloseweighthelper.dao.DatabaseNotificationObserver;
import creator.soft.cygi.com.friendlyloseweighthelper.dto.DateTimeDTO;

/**
 * Created by CygiMasterProgrammer on 2016-01-05.
 */
public interface DatabaseNotificationSubject {

    void addNotificationObserver(DatabaseNotificationObserver notificationObserver);

    void removeNotificationObserver(DatabaseNotificationObserver notificationObserver);

    void notifyTableMeasurementIsEmpty();

    void notifyTableMeasurementIsNotEmpty();

    void notifyNoMeasurementToUndo();

    void notifyUndoStackIsNotEmpty();

    void notifyMeasurementDeletion(DateTimeDTO dateTimeDTO);

    void notifyMeasurementUndoDeletion(DateTimeDTO dateTimeDTO);

    void notifyMeasurementNotInserted();

    void notifyMeasurementInserted();

}
