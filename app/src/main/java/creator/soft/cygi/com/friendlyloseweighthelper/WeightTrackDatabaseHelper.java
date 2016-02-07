package creator.soft.cygi.com.friendlyloseweighthelper;

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

/**
 * Created by CygiMasterProgrammer on 2015-12-14.
 */
public class WeightTrackDatabaseHelper extends SQLiteOpenHelper implements DatabaseNotificationSubject {

    public static final int ROW_NOT_INSERTED = -1;
    private static final String TAG = "WeightTrackDatabaseH";
    private static final String DB_NAME = "weightTrack.sgl";
    private static final int VERSION = 5;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERS_ID_USER = "id_user";
    private static final String COLUMN_USERS_USER_NAME = "user_name";
    private static final String COLUMN_USERS_PASSWORD = "password";
    private static final String TABLE_MEASUREMENT_DATA = "measurement_data";
    private static final String COLUMN_MEASUREMENT_DATA_MEASUREMENT_ID = "measurement_id";
    private static final String COLUMN_MEASUREMENT_DATA_ID_USER = "id_user";
    private static final String COLUMN_MEASUREMENT_DATA_DATE_TIME = "date_time";
    private static final String COLUMN_MEASUREMENT_DATA_WEIGHT = "weight";
    private static final String COLUMN_USERS_WEIGHT_GOAL = "weight_goal";
    Stack<DateTimeDTO> lastMeasurementDeletionStack = new Stack<DateTimeDTO>();
    private Context context;
    private String loginUserName;

    private List<DatabaseNotificationObserver> DatabaseNotificationObservers = new ArrayList<DatabaseNotificationObserver>();


    WeightTrackDatabaseHelper(Context context) {
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

        }

        Log.d(TAG, "Database Upgraded: " + DB_NAME);

    }

    public void deleteDatabase() {

        boolean isDeleted = context.deleteDatabase(DB_NAME);

        Log.i("Baza kasowac", "Czy baza skasowana  " + isDeleted);

    }


    public WeightDataModel getAllWeightDataFromDatabase() {

        Cursor cursor = getMeasurementDataCursor();
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

    private Cursor getMeasurementDataCursor() {

        int idOfCurrentUser = getIdOfCurrentUser();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MEASUREMENT_DATA,
                new String[]{COLUMN_MEASUREMENT_DATA_MEASUREMENT_ID, COLUMN_MEASUREMENT_DATA_DATE_TIME,
                        COLUMN_MEASUREMENT_DATA_WEIGHT},
                COLUMN_MEASUREMENT_DATA_ID_USER + "=?", new String[]{String.valueOf(idOfCurrentUser)},
                null, null, null);


        return cursor;
    }


    public long insertOneMeasurementIntoDatabase(WeightDataModel weightDataModel) {

        int currentUserId = getIdOfCurrentUser();

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

    private int getIdOfCurrentUser() {

        Cursor cursor = findUser(loginUserName);

        int userId = getUserId(cursor);

        return userId;
    }

    private Cursor findUser(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Log.d("USERNAME ***", " " + userName);

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USERS_ID_USER,
                        COLUMN_USERS_USER_NAME, COLUMN_USERS_PASSWORD},
                COLUMN_USERS_USER_NAME + "=?", new String[]{userName},
                null, null, null);
        cursor.moveToFirst();

        return cursor;
    }

    private int getUserId(Cursor cursor) {
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(COLUMN_USERS_ID_USER));
    }

    public void deleteLatestEntry() {

        String whereStatement = readSqlCommandFromResource(R.raw.where_statment_last_entry_to_mesurement_data);
        Integer currentIdUser = getIdOfCurrentUser();
        SQLiteDatabase db = getWritableDatabase();
        saveLastDeletedData();
        db.delete(TABLE_MEASUREMENT_DATA, whereStatement, new String[]{String.valueOf(currentIdUser)});
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

        Cursor latestMeasurementCursor = getLatestMeasurementCursor();

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

    private Cursor getLatestMeasurementCursor() {
        int idCurrentUser = getIdOfCurrentUser();

        Cursor latestMeasurementCursor = getReadableDatabase().query(TABLE_MEASUREMENT_DATA,
                new String[]{COLUMN_MEASUREMENT_DATA_MEASUREMENT_ID, COLUMN_MEASUREMENT_DATA_DATE_TIME,
                        COLUMN_MEASUREMENT_DATA_WEIGHT}, COLUMN_MEASUREMENT_DATA_ID_USER + " = ?",
                new String[]{String.valueOf(idCurrentUser)},
                null, null, COLUMN_MEASUREMENT_DATA_MEASUREMENT_ID + " DESC", "1");
        latestMeasurementCursor.moveToFirst();
        return latestMeasurementCursor;
    }


    public void clearLastMeasurementStack() {
        lastMeasurementDeletionStack.clear();
        notifyNoMeasurementToUndo();
        Log.d(TAG, "stack deleted");
    }

    public void updatedMeasurement(DateTimeDTO dateTimeDTO) {

        SQLiteDatabase db = getWritableDatabase();
        int idCurrentUser = getIdOfCurrentUser();

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

        Integer idOfCurrentUser = getIdOfCurrentUser();

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
        float userGoal;

        if (allUserCursor.getCount() > 0) {

            do {

                userId = allUserCursor.getLong(allUserCursor.getColumnIndex(COLUMN_USERS_ID_USER));
                userName = allUserCursor.getString(allUserCursor.getColumnIndex(COLUMN_USERS_USER_NAME));
                userPassword = allUserCursor.getString(allUserCursor.getColumnIndex(COLUMN_USERS_PASSWORD));
                userGoal = allUserCursor.getFloat(allUserCursor.getColumnIndex(COLUMN_USERS_WEIGHT_GOAL));

                Log.d(TAG, "Data From database : " + "userId: " + userId + " userName: "
                        + userName + " userPassword: " + userPassword + "weightGoal " + userGoal);

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
                        COLUMN_USERS_PASSWORD, COLUMN_USERS_WEIGHT_GOAL},
                null, null,
                null, null, null);

        return cursor;
    }

    public void clearDatabase() {

        clearAllDataInMeasurementTable();
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

    public void insertNewUserDataIntoDatabase(UserData userData) {

        String userName = userData.getName();
        String password = userData.getPassword();
        float weightGoal = userData.getWeightGoal();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERS_USER_NAME, userName);
        cv.put(COLUMN_USERS_PASSWORD, password);
        cv.put(COLUMN_USERS_WEIGHT_GOAL, weightGoal);
        getWritableDatabase().insert(TABLE_USERS, null, cv);

    }

    public void updateUserData(UserData userToUpdate) {

        Long userId = userToUpdate.getUserId();
        String userName = userToUpdate.getName();
        String password = userToUpdate.getPassword();
        float weightGoal = userToUpdate.getWeightGoal();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERS_USER_NAME, userName);
        cv.put(COLUMN_USERS_PASSWORD, password);
        cv.put(COLUMN_USERS_WEIGHT_GOAL, weightGoal);

        String whereStatement = COLUMN_USERS_ID_USER + " = ?";
        String[] whereArgs = new String[]{userId.toString()};

        getWritableDatabase().update(TABLE_USERS, cv, whereStatement, whereArgs);

    }

    public UserData getUserDataById(Long userId) {

        Cursor userCursor = getUserCursorBaseOnUserID(userId);

        String userName = userCursor.getString(userCursor.getColumnIndex(COLUMN_USERS_USER_NAME));
        String password = userCursor.getString(userCursor.getColumnIndex(COLUMN_USERS_PASSWORD));
        Float weightGoal = userCursor.getFloat(userCursor.getColumnIndex(COLUMN_USERS_WEIGHT_GOAL));

        UserData userData = new UserData();
        userData.setUserId(userId);
        userData.setName(userName);
        userData.setPassword(password);
        userData.setWeightGoal(weightGoal);

        return userData;
    }

    private Cursor getUserCursorBaseOnUserID(Long userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USERS_ID_USER,
                        COLUMN_USERS_USER_NAME, COLUMN_USERS_PASSWORD, COLUMN_USERS_WEIGHT_GOAL},
                COLUMN_USERS_ID_USER + " = ?", new String[]{userId.toString()},
                null, null, null);
        cursor.moveToFirst();

        return cursor;
    }
}
