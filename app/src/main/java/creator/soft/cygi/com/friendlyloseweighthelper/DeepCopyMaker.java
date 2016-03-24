package creator.soft.cygi.com.friendlyloseweighthelper;

import android.nfc.Tag;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by CygiMasterProgrammer on 2016-03-24.
 */
public class DeepCopyMaker {

    private final static String TAG = "DeepCopyMaker";

    static public Object deepCopy(Object objectToDeepCopy)
    {
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;

        try {
            ByteArrayOutputStream byteArrayOutputStream =
                    new ByteArrayOutputStream();

            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

            objectOutputStream.writeObject(objectToDeepCopy);
            objectOutputStream.flush();
            ByteArrayInputStream byteArrayInputStream =
                    new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            objectInputStream = new ObjectInputStream(byteArrayInputStream);

            return objectInputStream.readObject();
        }catch (Exception e)
        {
            Log.d(TAG,"Deep copy error");

        }finally {
            try {
                objectOutputStream.close();
                objectInputStream.close();
            }catch (IOException e){

                Log.d(TAG,""+e.getStackTrace());
            }
        }
     return null;
    }
}
