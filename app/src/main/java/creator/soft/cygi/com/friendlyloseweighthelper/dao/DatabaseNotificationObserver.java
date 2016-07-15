package creator.soft.cygi.com.friendlyloseweighthelper.dao;

import creator.soft.cygi.com.friendlyloseweighthelper.dto.DateTimeDTO;

/**
 * Created by CygiMasterProgrammer on 2016-01-05.
 */
public interface DatabaseNotificationObserver {


    public void onTableMeasurementIsEmpty();
    public void onTableMeasurementNotEmpty();
    public void onNoMeasurementToUndo();
    public void onUndoStackNotEmpty();

    public void onMeasurementDeletion(DateTimeDTO dateTimeDTO);
    public void onUndoMeasurementDeletion(DateTimeDTO dateTimeDTO);

    public void onMeasurementFailToInsertToDatabase();


    void onMeasurementInsertedToDatabase();
}
