package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by CygiMasterProgrammer on 2015-12-14.
 */
public class WeightTrackDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "weightTrack.sgl";
    private static final int VERSION = 1;
    Context context;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID_USER = "id_user";
    private static final String COLUMN_USER_NAME  = "user_name";

    private static final String TABLE_MEASUREMENT_DATA = "measurement_data";
    private static final String COLUMN_ID_USER_TABLE_MEASUREMENT_DATA = "id_user";
    private static final String COLUMN_DATE_TIME  = "date_time";
    private static final String COLUMN_WEIGHT  = "weight";


    WeightTrackDatabaseHelper(Context context){
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(readSqlFromResource(R.raw.crate_tabel_users));
        db.execSQL(readSqlFromResource(R.raw.create_table_mesurment_data));
        Log.i("Baza", "Wykonalem tworzenie bazy ****");
    }

    private String readSqlFromResource(int resourceId){

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

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEASUREMENT_DATA);

        Log.i("Baza", "Usunolem Baze danych");

        onCreate(db);
    }

    public void deleteDatabase(){

      boolean isDeleted =    context.deleteDatabase(DB_NAME);

        Log.i("Baza kasowac","Czy baza skasowana  " + isDeleted );

    }
}
