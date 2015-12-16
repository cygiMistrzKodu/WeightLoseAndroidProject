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

    WeightTrackDatabaseHelper(Context context){
        super(context,DB_NAME,null,VERSION);
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

        Log.i("WEIGHT_TRACK",sqlCommand);

        return sqlCommand;
    }

    public void testReadFromResourcesFile() {

        readSqlFromResource(R.raw.crate_tabel_users);
        readSqlFromResource(R.raw.create_table_mesurment_data);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
