package creator.soft.cygi.com.friendlyloseweighthelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by CygiMasterProgrammer on 2015-12-14.
 */
public class WeightTrackDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "weightTrack.sgl";
    private static final int VERSION = 1;

    WeightTrackDatabaseHelper(Context context){
        super(context,DB_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE users (" +
                   "id_user integer PRIMARY KEY AUTOINCREMENT,user_name VARCHAR(100))");

        db.execSQL("CREATE TABLE measurement_data (" +
                "measurement_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_user INTEGER REFERENCES users (id_user), " +
                " date_time varchar(30),  weight real" );

        Log.i("Baza", "Wykonalem tworzenie bazy ****");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
