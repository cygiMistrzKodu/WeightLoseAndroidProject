package creator.soft.cygi.com.friendlyloseweighthelper.dao;

import creator.soft.cygi.com.friendlyloseweighthelper.model.UserData;

/**
 * Created by CygiMasterProgrammer on 2016-02-08.
 */
public interface UserNotificationObserver {

    void onUserAlreadyExist(UserData userData);

}
