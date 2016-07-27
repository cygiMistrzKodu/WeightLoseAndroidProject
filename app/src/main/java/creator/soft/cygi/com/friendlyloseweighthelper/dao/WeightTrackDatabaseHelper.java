package creator.soft.cygi.com.friendlyloseweighthelper.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import creator.soft.cygi.com.friendlyloseweighthelper.dto.DateTimeDTO;
import creator.soft.cygi.com.friendlyloseweighthelper.view.LoginViewFragment;
import creator.soft.cygi.com.friendlyloseweighthelper.R;
import creator.soft.cygi.com.friendlyloseweighthelper.model.UserData;
import creator.soft.cygi.com.friendlyloseweighthelper.model.WeightDataModel;

/**
 * Created by CygiMasterProgrammer on 2015-12-14.
 */
public class WeightTrackDatabaseHelper extends SQLiteOpenHelper implements DatabaseNotificationSubject, UserNotificationSubject {

    public static final int ROW_NOT_INSERTED = -1;
    private static final String TAG = "WeightTrackDatabaseH";
    private static final String DB_NAME = "weightTrack.sgl";
    private static final int VERSION = 8;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERS_ID_USER = "id_user";
    private static final String COLUMN_USERS_USER_NAME = "user_name";
    private static final String COLUMN_USERS_PASSWORD = "password";
    private static final String COLUMN_USERS_EMAIL = "email";
    private static final String COLUMN_USERS_WEIGHT_GOAL = "weight_goal";
    private static final String TABLE_MEASUREMENT_DATA = "measurement_data";
    private static final String COLUMN_MEASUREMENT_DATA_MEASUREMENT_ID = "measurement_id";
    private static final String COLUMN_MEASUREMENT_DATA_ID_USER = "id_user";
    private static final String COLUMN_MEASUREMENT_DATA_DATE_TIME = "date_time";
    private static final String COLUMN_MEASUREMENT_DATA_WEIGHT = "weight";

    private static final String TABLE_USERS_PREFERENCES = "users_preferences";
    private static final String COLUMN_USERS_PREFERENCES_ID_PREFERENCES = "user_preferences_id";
    private static final String COLUMN_USERS_PREFERENCES_ID_USER = "id_user";
    private static final String COLUMN_USERS_PREFERENCES_MODIFY_MEASUREMENT_POSITION = "user_modify_measurement_position";
    public static final int ROW_NOT_EXIST = 0;

    Stack<DateTimeDTO> lastMeasurementDeletionStack = new Stack<DateTimeDTO>();
    private Context context;
    private String loginUserName;

    private List<DatabaseNotificationObserver> DatabaseNotificationObservers = new ArrayList<DatabaseNotificationObserver>();
    private List<UserNotificationObserver> userNotificationObservers = new ArrayList<UserNotificationObserver>();


    public WeightTrackDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
        readCurrentUserNameFromPreferences();
    }

    private void readCurrentUserNameFromPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        loginUserName = sharedPreferences.getString(LoginViewFragment.LOGIN_USER_NAME, null);

    }

    public String getLoginUserName() {
        return loginUserName;
    }

    public void setLoginUserName(String currentUserName) {
        this.loginUserName = currentUserName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(readSqlCommandFromResource(R.raw.crate_tabel_users));
        db.execSQL(readSqlCommandFromResource(R.raw.create_table_mesurment_data));
        db.execSQL(readSqlCommandFromResource(R.raw.create_tabel_users_preferences));
        Log.i("Baza", "Wykonalem tworzenie bazy ****");


    }

    private String readSqlCommandFromResource(int resourceId) {

        InputStream inputStream = context.getResources().openRawResource(resourceId);
        String sqlCommand = "";
        try {
            sqlCommand = IOUtils.toString(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        IOUtils.closeQuietly(inputStream);

        Log.d(TAG, "Command: " + sqlCommand);

        return sqlCommand;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(TAG, "Upgrading database: " + DB_NAME);

        switch (oldVersion) {
            case 3:
                db.execSQL(readSqlCommandFromResource(R.raw.update_table_users_add_password_column));
            case 4:
                db.execSQL(readSqlCommandFromResource(R.raw.update_tabel_users_add_goal_column));
            case 5:
                db.execSQL(readSqlCommandFromResource(R.raw.update_table_users_add_email_column));
            case 6:
                db.execSQL(readSqlCommandFromResource(R.raw.create_tabel_users_preferences));
            case 7:
                db.execSQL(readSqlCommandFromResource(R.raw.delete_table_users_preferences));
                db.execSQL(readSqlCommandFromResource(R.raw.create_tabel_users_preferences));

        }

        Log.d(TAG, "Database Upgraded: " + DB_NAME);

    }

    public void deleteDatabase() {

        boolean isDeleted = context.deleteDatabase(DB_NAME);

        Log.i("Baza kasowac", "Czy baza skasowana  " + isDeleted);

    }


    public WeightDataModel getAllWeightDataFromDatabase() {

        Cursor cursor = getMeasurementDataCursorOrderByDate();
        cursor.moveToFirst();
        WeightDataModel weightDataModel = new WeightDataModel(context);

        String formatDate = "";
        float weight;
        int measurementID;

        if (cursor.getCount() > 0) {
            do {

                measurementID = cursor.getInt(cursor.getColumnIndex(COLUMN_MEASUREMENT_DATA_MEASUREMENT_ID));
                formatDate = cursor.getString(cursor.getColumnIndex(COLUMN_MEASUREMENT_DATA_DATE_TIME));
                weight = cursor.getFloat(cursor.getColumnIndex(COLUMN_MEASUREMENT_DATA_WEIGHT));

                Log.d(TAG, "Data From database : " + "MeasurementID: " + measurementID + " Date: " + formatDate + " Weight: " + weight);
                DateTimeDTO dateTimeDTO = new DateTimeDTO();
                dateTimeDTO.setDate(formatDate);
                dateTimeDTO.setWeight(weight);
                dateTimeDTO.setMeasurementID(measurementID);
                dateTimeDTO.setAndroidContext(context);

                weightDataModel.setTimeAndDate(dateTimeDTO);

            } while (cursor.moveToNext());
        }
        return weightDataModel;
    }

    private Cursor getMeasurementDataCursorOrderByDate() {

        Long idOfCurrentUser = getIdOfCurrentUser();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MEASUREMENT_DATA,
                new String[]{COLUMN_MEASUREMENT_DATA_MEASUREMENT_ID, COLUMN_MEASUREMENT_DATA_DATE_TIME,
                        COLUMN_MEASUREMENT_DATA_WEIGHT},
                COLUMN_MEASUREMENT_DATA_ID_USER + "=?", new String[]{String.valueOf(idOfCurrentUser)},
                null, null, COLUMN_MEASUREMENT_DATA_DATE_TIME);


        return cursor;
    }

    public long insertOneMeasurementIntoDatabase(WeightDataModel weightDataModel) {

        Long currentUserId = getIdOfCurrentUser();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MEASUREMENT_DATA_ID_USER, currentUserId);
        cv.put(COLUMN_MEASUREMENT_DATA_DATE_TIME, weightDataModel.getLatestDate());
        cv.put(COLUMN_MEASUREMENT_DATA_WEIGHT, weightDataModel.getLatestWeight());


        SQLiteDatabase db = this.getWritableDatabase();

        long insertedRowNumber;

        try {
            db.beginTransaction();
            insertedRowNumber = db.insert(TABLE_MEASUREMENT_DATA, null, cv);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        Log.i(TAG, "Row inserted on position: " + insertedRowNumber);

        if (insertedRowNumber == ROW_NOT_INSERTED) {

            notifyMeasurementNotInserted();
        } else {

            notifyMeasurementInserted();
        }

        return insertedRowNumber;
    }

    private long getIdOfCurrentUser() {

        Cursor cursor = findUser(loginUserName);

        long userId = getUserId(cursor);

        return userId;
    }

    private Cursor findUser(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Log.d("USERNAME ***", " " + userName);

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USERS_ID_USER,
                        COLUMN_USERS_USER_NAME, COLUMN_USERS_EMAIL, COLUMN_USERS_PASSWORD, COLUMN_USERS_WEIGHT_GOAL},
                COLUMN_USERS_USER_NAME + "=?", new String[]{userName},
                null, null, null);
        cursor.moveToFirst();

        return cursor;
    }

    private long getUserId(Cursor cursor) {
        cursor.moveToFirst();
        return cursor.getLong(cursor.getColumnIndex(COLUMN_USERS_ID_USER));
    }

    public void deleteEntryWithLatestDate() {

        String whereStatementMostRecentDate = readSqlCommandFromResource(R.raw.where_satement_most_recent_date);
        Long currentIdUser = getIdOfCurrentUser();
        SQLiteDatabase db = getWritableDatabase();
        saveLastDeletedData();
        db.delete(TABLE_MEASUREMENT_DATA, whereStatementMostRecentDate, new String[]{String.valueOf(currentIdUser)});
        isMeasurementTableEmpty();
    }

    public void undoDeleteLastMeasurement() {


        if (lastMeasurementDeletionStack.empty()) {
            Log.d(TAG, "lastMeasurementDeletionStack is empty");
            notifyNoMeasurementToUndo();
            return;
        }

        WeightDataModel weightDataModel = new WeightDataModel(context);

        DateTimeDTO dateTimeDTO = lastMeasurementDeletionStack.pop();
        dateTimeDTO.setAndroidContext(context);

        weightDataModel.setTimeAndDate(dateTimeDTO);
        insertOneMeasurementIntoDatabase(weightDataModel);

        notifyMeasurementUndoDeletion(dateTimeDTO);
        notifyTableMeasurementIsNotEmpty();

    }

    private void saveLastDeletedData() {

        Cursor latestMeasurementCursor = getLatestDateMeasurementCursor();

        Log.d(TAG, "Number fo Row in Cursor " + latestMeasurementCursor.getCount());

        if (latestMeasurementCursor.getCount() <= 0) {
            return;
        }

        String date = latestMeasurementCursor.getString(latestMeasurementCursor.getColumnIndex(COLUMN_MEASUREMENT_DATA_DATE_TIME));
        Float weight = latestMeasurementCursor.getFloat(latestMeasurementCursor.getColumnIndex(COLUMN_MEASUREMENT_DATA_WEIGHT));

        Log.d(TAG, "Get Last Date : " + date + " Weight: " + weight);

        DateTimeDTO dateTimeDTO = new DateTimeDTO();
        dateTimeDTO.setDate(date);
        dateTimeDTO.setWeight(weight);
        dateTimeDTO.setAndroidContext(context);

        lastMeasurementDeletionStack.push(dateTimeDTO);

        Log.d(TAG, "Push on stack  Date: " + dateTimeDTO.getDateWithoutFormatting() + " weight " + dateTimeDTO.getWeight());

        notifyMeasurementDeletion(dateTimeDTO);
        notifyUndoStackIsNotEmpty();

    }

    private Cursor getLatestDateMeasurementCursor() {
        Long idCurrentUser = getIdOfCurrentUser();

        Cursor latestDateMeasurementCursor = getReadableDatabase().query(TABLE_MEASUREMENT_DATA,
                new String[]{COLUMN_MEASUREMENT_DATA_MEASUREMENT_ID, COLUMN_MEASUREMENT_DATA_DATE_TIME,
                        COLUMN_MEASUREMENT_DATA_WEIGHT}, COLUMN_MEASUREMENT_DATA_ID_USER + " = ?",
                new String[]{String.valueOf(idCurrentUser)},
                null, null, COLUMN_MEASUREMENT_DATA_DATE_TIME + " DESC", "1");
        latestDateMeasurementCursor.moveToFirst();
        return latestDateMeasurementCursor;
    }


    public void clearLastMeasurementStack() {
        lastMeasurementDeletionStack.clear();
        notifyNoMeasurementToUndo();
        Log.d(TAG, "stack deleted");
    }

    public void updatedMeasurement(DateTimeDTO dateTimeDTO) {

        SQLiteDatabase db = getWritableDatabase();
        Long idCurrentUser = getIdOfCurrentUser();

        String dateString = dateTimeDTO.getDateWithoutFormatting();
        float weight = dateTimeDTO.getWeight();
        Integer measurementID = dateTimeDTO.getMeasurementID();


        ContentValues insertValues = new ContentValues();
        insertValues.put(COLUMN_MEASUREMENT_DATA_DATE_TIME, dateString);
        insertValues.put(COLUMN_MEASUREMENT_DATA_WEIGHT, weight);

        String whereStatement = COLUMN_MEASUREMENT_DATA_ID_USER +
                " = ? and " + COLUMN_MEASUREMENT_DATA_MEASUREMENT_ID + " = ? ";

        String[] whereArgs = new String[]{String.valueOf(idCurrentUser), measurementID.toString()};

        db.update(TABLE_MEASUREMENT_DATA, insertValues, whereStatement, whereArgs);
        Log.d(TAG, "Measurement in database updated");

    }

    public void clearAllMeasurementDataForLoginUser() {

        SQLiteDatabase db = this.getWritableDatabase();

        String whereStatement = COLUMN_MEASUREMENT_DATA_ID_USER + " = ?";
        String[] whereArgs = new String[]{String.valueOf(getIdOfCurrentUser())};

        db.beginTransaction();
        try {
            db.delete(TABLE_MEASUREMENT_DATA, whereStatement, whereArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public void deleteCurrentUserAccount() {

        clearAllMeasurementDataForLoginUser();
        deleteCurrentUserPreferences();
        deleteCurrentLoginUser();

    }

    private void deleteCurrentUserPreferences() {

        SQLiteDatabase db = this.getWritableDatabase();

        String whereStatement = COLUMN_USERS_PREFERENCES_ID_USER + " = ?";
        String[] whereArgs = new String[]{String.valueOf(getIdOfCurrentUser())};

        db.beginTransaction();
        try {
            db.delete(TABLE_USERS_PREFERENCES, whereStatement, whereArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    private void deleteCurrentLoginUser() {

        Long idOfCurrentUser = getIdOfCurrentUser();

        String whereStatement = COLUMN_USERS_ID_USER + "= ?";
        String[] whereArgs = new String[]{String.valueOf(idOfCurrentUser)};

        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            db.delete(TABLE_USERS, whereStatement, whereArgs);
            db.setTransactionSuccessful();
            Log.d(TAG, "User account : " + loginUserName + " deleted.");
        } finally {
            db.endTransaction();
        }

    }

    public void saveUserPositionInModifyMode(int userPosition) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {

            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_USERS_PREFERENCES_ID_USER, getIdOfCurrentUser());
            contentValues.put(COLUMN_USERS_PREFERENCES_MODIFY_MEASUREMENT_POSITION, userPosition);

            long isRowExist = updateModifyPositionInDatabase(db, contentValues);

            if (isRowExist == ROW_NOT_EXIST) {

                insertNewModifyPositionRecordToDatabse(db, contentValues);
            }

            db.setTransactionSuccessful();
            Log.d(TAG, "Position of User : " + loginUserName + " saved.");
        } finally {
            db.endTransaction();
        }
    }

    private void insertNewModifyPositionRecordToDatabse(SQLiteDatabase db, ContentValues contentValues) {
        db.insertWithOnConflict(TABLE_USERS_PREFERENCES, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private long updateModifyPositionInDatabase(SQLiteDatabase database, ContentValues contentValues) {

        Long idOfCurrentUser = getIdOfCurrentUser();

        String whereStatement = COLUMN_USERS_PREFERENCES_ID_USER + " = ?";
        String[] whereArgs = new String[]{idOfCurrentUser.toString()};

        return database.updateWithOnConflict(TABLE_USERS_PREFERENCES, contentValues, whereStatement, whereArgs, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public int readUserPositionInModifyMode() {

        Long idOfCurrentUser = getIdOfCurrentUser();

        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.query(TABLE_USERS_PREFERENCES,
                new String[]{COLUMN_USERS_PREFERENCES_MODIFY_MEASUREMENT_POSITION},
                COLUMN_USERS_PREFERENCES_ID_USER + "=?", new String[]{String.valueOf(idOfCurrentUser)},
                null, null, null);
        cursor.moveToFirst();

        if (cursor.getCount() <= 0) {
            return 0;
        }

        int userModifyPosition = 0;

        userModifyPosition = cursor.getInt(cursor.getColumnIndex(COLUMN_USERS_PREFERENCES_MODIFY_MEASUREMENT_POSITION));


        Log.d(TAG, "Position of User : " + loginUserName + " is " + userModifyPosition);


        return userModifyPosition;
    }


    @Override
    public void addNotificationObserver(DatabaseNotificationObserver notificationObserver) {
        DatabaseNotificationObservers.add(notificationObserver);

    }

    @Override
    public void removeNotificationObserver(DatabaseNotificationObserver notificationObserver) {
        DatabaseNotificationObservers.remove(notificationObserver);
    }

    @Override
    public void notifyTableMeasurementIsEmpty() {

        notifyDatabaseObserver("TableMeasurementIsEmpty");
    }

    @Override
    public void notifyTableMeasurementIsNotEmpty() {

        notifyDatabaseObserver("TableMeasurementNotEmpty");
    }

    @Override
    public void notifyNoMeasurementToUndo() {
        notifyDatabaseObserver("NoMeasurementToUndo");
    }

    @Override
    public void notifyUndoStackIsNotEmpty() {
        notifyDatabaseObserver("UndoStackIsNotEmpty");
    }

    private void notifyDatabaseObserver(String observersType) {

        for (DatabaseNotificationObserver notificationObserver : DatabaseNotificationObservers) {

            if (observersType.equals("TableMeasurementIsEmpty")) {
                notificationObserver.onTableMeasurementIsEmpty();
            }

            if (observersType.equals("TableMeasurementNotEmpty")) {
                notificationObserver.onTableMeasurementNotEmpty();
            }

            if (observersType.equals("NoMeasurementToUndo")) {
                notificationObserver.onNoMeasurementToUndo();
            }

            if (observersType.equals("UndoStackIsNotEmpty")) {
                notificationObserver.onUndoStackNotEmpty();
            }

        }
    }

    @Override
    public void notifyMeasurementDeletion(DateTimeDTO dateTimeDTO) {

        for (DatabaseNotificationObserver notificationObserver : DatabaseNotificationObservers) {
            notificationObserver.onMeasurementDeletion(dateTimeDTO);
        }

    }

    @Override
    public void notifyMeasurementUndoDeletion(DateTimeDTO dateTimeDTO) {

        for (DatabaseNotificationObserver notificationObserver : DatabaseNotificationObservers) {
            notificationObserver.onUndoMeasurementDeletion(dateTimeDTO);
        }
    }

    @Override
    public void notifyMeasurementNotInserted() {
        for (DatabaseNotificationObserver notificationObserver : DatabaseNotificationObservers) {
            notificationObserver.onMeasurementFailToInsertToDatabase();
        }
    }

    @Override
    public void notifyMeasurementInserted() {

        for (DatabaseNotificationObserver notificationObserver : DatabaseNotificationObservers) {
            notificationObserver.onMeasurementInsertedToDatabase();
        }

    }

    public boolean isMeasurementTableEmpty() {

        Long rowCount = numberOfMeasurementDataForCurrentUser();

        if (rowCount > 0) {
            return false;
        }

        notifyTableMeasurementIsEmpty();
        return true;
    }

    public Long numberOfMeasurementDataForCurrentUser() {

        SQLiteDatabase db = this.getReadableDatabase();

        String whereStatement = COLUMN_MEASUREMENT_DATA_ID_USER + " = ?";

        Long idOfCurrentUser = getIdOfCurrentUser();

        String[] whereArgs = new String[]{idOfCurrentUser.toString()};

        long rowCount = DatabaseUtils.queryNumEntries(db, TABLE_MEASUREMENT_DATA, whereStatement, whereArgs);

        return rowCount;
    }

    public List<UserData> getUsersData() {

        List<UserData> allUsersData = new LinkedList<>();

        Cursor allUserCursor = getAllUserCursor();
        allUserCursor.moveToFirst();

        long userId;
        String userName;
        String userPassword;
        String userEmail;
        float userGoal;

        if (allUserCursor.getCount() > 0) {

            do {

                userId = allUserCursor.getLong(allUserCursor.getColumnIndex(COLUMN_USERS_ID_USER));
                userName = allUserCursor.getString(allUserCursor.getColumnIndex(COLUMN_USERS_USER_NAME));
                userPassword = allUserCursor.getString(allUserCursor.getColumnIndex(COLUMN_USERS_PASSWORD));
                userEmail = allUserCursor.getString(allUserCursor.getColumnIndex(COLUMN_USERS_EMAIL));
                userGoal = allUserCursor.getFloat(allUserCursor.getColumnIndex(COLUMN_USERS_WEIGHT_GOAL));

                Log.d(TAG, "Data From database : " + "userId: " + userId + " userName: "
                        + userName + " userPassword: " + userPassword + " userEmail: " + userEmail + " weightGoal: " + userGoal);

                UserData userData = new UserData();
                userData.setUserId(userId);
                userData.setName(userName);
                userData.setPassword(userPassword);
                userData.setWeightGoal(userGoal);

                allUsersData.add(userData);

            } while (allUserCursor.moveToNext());

        }


        return allUsersData;
    }

    private Cursor getAllUserCursor() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_MEASUREMENT_DATA_ID_USER, COLUMN_USERS_USER_NAME,
                        COLUMN_USERS_PASSWORD, COLUMN_USERS_EMAIL, COLUMN_USERS_WEIGHT_GOAL},
                null, null,
                null, null, null);

        return cursor;
    }

    public void clearDatabase() {

        clearAllDataInMeasurementTable();
        clearAllUserDataInUsersPreferencesTable();
        clearAllUserDataInUsersTable();

    }

    private void clearAllDataInMeasurementTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_MEASUREMENT_DATA, null, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void clearAllUserDataInUsersTable() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_USERS, null, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void clearAllUserDataInUsersPreferencesTable() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_USERS_PREFERENCES, null, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void insertNewUserDataIntoDatabase(UserData userData) {

        String userName = userData.getName();
        String password = userData.getPassword();
        String email = userData.getEmail();
        float weightGoal = userData.getWeightGoal();

        if (isUserExist(userName)) {
            notifyUserExistAlready(userData);
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERS_USER_NAME, userName);
        cv.put(COLUMN_USERS_PASSWORD, password);
        cv.put(COLUMN_USERS_EMAIL, email);
        cv.put(COLUMN_USERS_WEIGHT_GOAL, weightGoal);
        getWritableDatabase().insert(TABLE_USERS, null, cv);

    }

    public UserData getLoginUserData() {

        long currentUserId = getIdOfCurrentUser();
        UserData logInUserData = getUserDataById(currentUserId);

        return logInUserData;
    }

    @Override
    public void addUserNotificationObserver(UserNotificationObserver userNotificationObserver) {

        userNotificationObservers.add(userNotificationObserver);

    }

    @Override
    public void removeNotificationUserObserver(UserNotificationObserver userNotificationObserver) {

        userNotificationObservers.remove(userNotificationObserver);
    }

    @Override
    public void notifyUserExistAlready(UserData userData) {

        for (UserNotificationObserver userNotificationObserver : userNotificationObservers) {
            userNotificationObserver.onUserAlreadyExist(userData);
        }
    }

    private boolean isUserExist(String userName) {
        Cursor cursor = findUser(userName);

        if (cursor.getCount() > 0) {
            return true;
        }

        return false;
    }

    public boolean updateUserEmail(String userEmail) {

        if (isOtherUserHaveThisEmailAlready(userEmail)) {
            return false;
        }

        Long currentUserId = getIdOfCurrentUser();
        UserData currentUserData = getUserDataById(currentUserId);
        currentUserData.setEmail(userEmail);
        updateUserData(currentUserData);

        return true;
    }

    private boolean isOtherUserHaveThisEmailAlready(String userEmail) {

        Cursor cursor = findEmail(userEmail);

        if (cursor.getCount() > 0) {
            return true;
        }

        return false;
    }

    private Cursor findEmail(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USERS_EMAIL},
                COLUMN_USERS_EMAIL + "=?", new String[]{userEmail}, null, null, null);
        cursor.moveToFirst();

        return cursor;
    }

    public void updateWeightGoal(Float weightGoal) {

        Long currentUserId = getIdOfCurrentUser();
        UserData currentUserData = getUserDataById(currentUserId);
        currentUserData.setWeightGoal(weightGoal);
        updateUserData(currentUserData);

    }

    public String getPasswordOfCurrentUser() {

        Cursor cursor = findUser(loginUserName);

        String password = getPassword(cursor);

        return password;
    }

    private String getPassword(Cursor cursor) {
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(COLUMN_USERS_PASSWORD));

    }

    public void updateUserData(UserData userToUpdate) {

        Long userId = userToUpdate.getUserId();
        String userName = userToUpdate.getName();
        String userEmail = userToUpdate.getEmail();
        String password = userToUpdate.getPassword();
        float weightGoal = userToUpdate.getWeightGoal();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERS_USER_NAME, userName);
        cv.put(COLUMN_USERS_EMAIL, userEmail);
        cv.put(COLUMN_USERS_PASSWORD, password);
        cv.put(COLUMN_USERS_WEIGHT_GOAL, weightGoal);

        String whereStatement = COLUMN_USERS_ID_USER + " = ?";
        String[] whereArgs = new String[]{userId.toString()};

        getWritableDatabase().update(TABLE_USERS, cv, whereStatement, whereArgs);

    }

    public void updateUserPassword(String newUserPassword) {

        Long currentUserId = getIdOfCurrentUser();
        UserData currentUserData = getUserDataById(currentUserId);
        currentUserData.setPassword(newUserPassword);
        updateUserData(currentUserData);

    }

    public boolean updateUserName(String newUserName) {

        if (isUserExist(newUserName)) {

            Log.d(TAG, "User already exist with this name");

            UserData userData = new UserData();
            userData.setName(newUserName);
            notifyUserExistAlready(userData);

            return false;
        }

        Long currentUserId = getIdOfCurrentUser();
        UserData currentUserData = getUserDataById(currentUserId);
        currentUserData.setName(newUserName);
        updateUserData(currentUserData);

        updateSharedPreferencesWithNewUserName(newUserName);

        return true;
    }

    private void updateSharedPreferencesWithNewUserName(String newUserName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LoginViewFragment.LOGIN_USER_NAME, newUserName);
        editor.commit();
    }

    public UserData getUserDataById(Long userId) {

        Cursor userCursor = getUserCursorBaseOnUserID(userId);

        String userName = userCursor.getString(userCursor.getColumnIndex(COLUMN_USERS_USER_NAME));
        String userEmail = userCursor.getString(userCursor.getColumnIndex(COLUMN_USERS_EMAIL));
        String password = userCursor.getString(userCursor.getColumnIndex(COLUMN_USERS_PASSWORD));
        Float weightGoal = userCursor.getFloat(userCursor.getColumnIndex(COLUMN_USERS_WEIGHT_GOAL));

        UserData userData = new UserData();
        userData.setUserId(userId);
        userData.setName(userName);
        userData.setEmail(userEmail);
        userData.setPassword(password);
        userData.setWeightGoal(weightGoal);

        return userData;
    }

    private Cursor getUserCursorBaseOnUserID(Long userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USERS_ID_USER,
                        COLUMN_USERS_USER_NAME, COLUMN_USERS_EMAIL, COLUMN_USERS_PASSWORD, COLUMN_USERS_WEIGHT_GOAL},
                COLUMN_USERS_ID_USER + " = ?", new String[]{userId.toString()},
                null, null, null);
        cursor.moveToFirst();

        return cursor;
    }

    public Long countUsersInStorage() {

        SQLiteDatabase db = this.getReadableDatabase();

        long userCount = DatabaseUtils.queryNumEntries(db, TABLE_USERS);
        return userCount;
    }

    public Float getWeightGoalOfCurrentUser() {

        Cursor cursor = findUser(loginUserName);

        Float weightGoal = getWeightGoal(cursor);

        return weightGoal;
    }

    private Float getWeightGoal(Cursor cursor) {
        cursor.moveToFirst();
        return cursor.getFloat(cursor.getColumnIndex(COLUMN_USERS_WEIGHT_GOAL));

    }
}
