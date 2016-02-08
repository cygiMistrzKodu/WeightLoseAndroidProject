package creator.soft.cygi.com.friendlyloseweighthelper;

/**
 * Created by CygiMasterProgrammer on 2016-02-08.
 */
public interface UserNotificationSubject {

    void addUserNotificationObserver(UserNotificationObserver userNotificationObserver);

    void removeNotificationUserObserver(UserNotificationObserver userNotificationObserver);

    void notifyUserExistAlready(UserData userData);
}
