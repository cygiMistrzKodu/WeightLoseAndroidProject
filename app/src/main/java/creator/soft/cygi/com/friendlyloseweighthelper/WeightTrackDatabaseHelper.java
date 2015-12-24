package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

/**
 * Created by CygiMasterProgrammer on 2015-12-14.
 */
public class WeightTrackDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "WeightTrackDatabaseH";

    private static final String DB_NAME = "weightTrack.sgl";
    private static final int VERSION = 1;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERS_ID_USER = "id_user";
    private static final String COLUMN_USERS_USER_NAME = "user_name";

    private static final String TABLE_MEASUREMENT_DATA = "measurement_data";
    private static final String COLUMN_MEASUREMENT_DATA_MEASUREMENT_ID = "measurement_id";
    private static final String COLUMN_MEASUREMENT_DATA_ID_USER = "id_user";
    private static final String COLUMN_MEASUREMENT_DATA_DATE_TIME = "date_time";
    private static final String COLUMN_MEASUREMENT_DATA_WEIGHT = "weight";
    Context context;
    Stack<DateTimeDTO> lastMeasurementDeletionStack = new Stack<DateTimeDTO>();
    private String currentUser = "JacekCygi";   // just for testing Will be more softicated latter
    private int existingUserID;


    WeightTrackDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
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

        Log.i("WEIGHT_TRACK", sqlCommand);

        return sqlCommand;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteDatabase() {

        boolean isDeleted = context.deleteDatabase(DB_NAME);

        Log.i("Baza kasowac", "Czy baza skasowana  " + isDeleted);

    }


    public WeightDataModel getAllWeightDataFromDatabase() {

        Cursor cursor = getMeasurementDataCursor();
        cursor.moveToFirst();
        WeightDataModel weightDataModel = new WeightDataModel();

        String formatDate = "";
        float weight;
        DateTimeDTO dateTimeDTO = new DateTimeDTO();

        if (cursor.getCount() > 0) {
            do {

                formatDate = cursor.getString(cursor.getColumnIndex(COLUMN_MEASUREMENT_DATA_DATE_TIME));
                weight = cursor.getFloat(cursor.getColumnIndex(COLUMN_MEASUREMENT_DATA_WEIGHT));

                Log.i(TAG, "Data From database : " + formatDate + " : " + weight);
                dateTimeDTO.setDate(formatDate);
                dateTimeDTO.setWeight(weight);

                weightDataModel.setTimeAndDate(dateTimeDTO);

            } while (cursor.moveToNext());
        }

        return weightDataModel;
    }

    public Cursor getMeasurementDataCursor() {

        int idOfCurrentUser = getIdOfCurrentUser();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MEASUREMENT_DATA,
                new String[]{COLUMN_MEASUREMENT_DATA_DATE_TIME,
                        COLUMN_MEASUREMENT_DATA_WEIGHT},
                COLUMN_MEASUREMENT_DATA_ID_USER + "=?", new String[]{String.valueOf(idOfCurrentUser)},
                null, null, null);


        return cursor;
    }


    public long insertOneRecordIntoWeightTrackDatabase(WeightDataModel weightDataModel) {

        int currentUserId = getIdOfCurrentUser();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MEASUREMENT_DATA_ID_USER, currentUserId);
        cv.put(COLUMN_MEASUREMENT_DATA_DATE_TIME, weightDataModel.getLatestDate());
        cv.put(COLUMN_MEASUREMENT_DATA_WEIGHT, weightDataModel.getLatestWeight());

        long insertedRowNumber = getWritableDatabase().insert(TABLE_MEASUREMENT_DATA, null, cv);

        Log.i(TAG, "Row inserted on position: " + insertedRowNumber);
        return insertedRowNumber;
    }

    private int getIdOfCurrentUser() {

        if (checkIfUserExist(currentUser)) {

            int existingUserId = getExistingUserID();

            return existingUserId;
        } else {
            createNewUser();
            int newUserId = getNewUserId();

            return newUserId;
        }

    }

    private boolean checkIfUserExist(String userName) {

        Cursor cursor = findUser(userName);
        String userNameInDatabase = "";

        if (cursor != null) {
            cursor.moveToFirst();

            if (cursor.getCount() > 0) {
                userNameInDatabase =
                        cursor.getString(cursor.getColumnIndex(COLUMN_USERS_USER_NAME));
            }
        }

        Log.i(TAG, "User name from database:  " + userNameInDatabase);

        if (userNameInDatabase.equals(userName)) {
            saveExistingUserId(cursor);

            return true;
        }

        return false;
    }

    private Cursor findUser(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USERS_ID_USER,
                        COLUMN_USERS_USER_NAME},
                COLUMN_USERS_USER_NAME + "=?", new String[]{userName},
                null, null, null);
        cursor.moveToFirst();
        db.close();

        return cursor;
    }

    private void saveExistingUserId(Cursor cursor) {

        existingUserID = getUserId(cursor);
        Log.i(TAG, "existingUserID : " + existingUserID);
    }

    private int getExistingUserID() {
        return existingUserID;
    }

    private void createNewUser() {

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERS_USER_NAME, currentUser);
        getWritableDatabase().insert(TABLE_USERS, null, cv);
    }

    private int getNewUserId() {

        Cursor cursor = findUser(currentUser);

        return getUserId(cursor);
    }

    private int getUserId(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(COLUMN_USERS_ID_USER));
    }


    public void deleteLastMeasurement() {

        String whereStatement = readSqlCommandFromResource(R.raw.where_statment_last_entry_to_mesurement_data);
        Integer currentIdUser = getIdOfCurrentUser();
        SQLiteDatabase db = getWritableDatabase();
        saveLastDeletedData();
        db.delete(TABLE_MEASUREMENT_DATA, whereStatement, new String[]{String.valueOf(currentIdUser)});

    }

    public void undoDeleteLastMeasurement() {


        if (lastMeasurementDeletionStack.empty()) {
            Log.d(TAG, "lastMeasurementDeletionStack is empty");
            return;
        }

        WeightDataModel weightDataModel = new WeightDataModel();

        DateTimeDTO dateTimeDTO = lastMeasurementDeletionStack.pop();

        weightDataModel.setTimeAndDate(dateTimeDTO);
        insertOneRecordIntoWeightTrackDatabase(weightDataModel);


    }

    private void saveLastDeletedData() {

        Cursor latestMeasurementCursor = getLatestMeasurementCursor();
        String date = latestMeasurementCursor.getString(latestMeasurementCursor.getColumnIndex(COLUMN_MEASUREMENT_DATA_DATE_TIME));
        Float weight = latestMeasurementCursor.getFloat(latestMeasurementCursor.getColumnIndex(COLUMN_MEASUREMENT_DATA_WEIGHT));

        Log.d(TAG, "Get Last Date : "+ date +" Weight: "+weight);

        DateTimeDTO dateTimeDTO = new DateTimeDTO();
        dateTimeDTO.setDate(date);
        dateTimeDTO.setWeight(weight);

        lastMeasurementDeletionStack.push(dateTimeDTO);

            Log.d(TAG, "Push on stack  Date: " + dateTimeDTO.getDateInString() + " weight " + dateTimeDTO.getWeight());

    }

            private Cursor getLatestMeasurementCursor () {
            int idCurrentUser = getExistingUserID();
            Cursor latestMeasurementCursor = getReadableDatabase().query(TABLE_MEASUREMENT_DATA,
                    new String[]{COLUMN_MEASUREMENT_DATA_MEASUREMENT_ID, COLUMN_MEASUREMENT_DATA_DATE_TIME,
                            COLUMN_MEASUREMENT_DATA_WEIGHT}, COLUMN_MEASUREMENT_DATA_ID_USER + " = ?",
                    new String[]{String.valueOf(idCurrentUser)},
                    null, null, COLUMN_MEASUREMENT_DATA_MEASUREMENT_ID + " DESC", "1");
            latestMeasurementCursor.moveToFirst();
            return latestMeasurementCursor;
        }

        public void clearLastMeasurementStack () {
            lastMeasurementDeletionStack.clear();
            Log.d(TAG, "stack deleted");
        }

    public void updatedMeasurement() {

        SQLiteDatabase db = getWritableDatabase();

        int idCurrentUser = getExistingUserID();

        ContentValues insertValues = new ContentValues();
        insertValues.put(COLUMN_MEASUREMENT_DATA_DATE_TIME, "12-12-2015");
        insertValues.put(COLUMN_MEASUREMENT_DATA_WEIGHT, 220);

        String whereStatement = COLUMN_MEASUREMENT_DATA_ID_USER  +
                " = ? and " + COLUMN_MEASUREMENT_DATA_MEASUREMENT_ID + " = ? " ;

        String [] whereArgs  = new String[]{String.valueOf(idCurrentUser),"11"};

        db.update(TABLE_MEASUREMENT_DATA,insertValues,whereStatement,whereArgs);

    }

    }
