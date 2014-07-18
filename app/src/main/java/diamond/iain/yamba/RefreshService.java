package diamond.iain.yamba;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClientException;

import java.util.List;

public class RefreshService extends IntentService {
    static final String TAG = "RefreshService";

    public RefreshService() {
        super(TAG);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String username = prefs.getString("username", "");
        String password = prefs.getString("password", "");

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please update your username and password", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "OnStarted");

        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        YambaClient cloud = new YambaClient(username, password);

        try {
            int count = 0;
            List<Status> timeline = cloud.getTimeline(20);
            for (Status status : timeline) {
                values.clear();

                values.put(StatusContract.Column.ID, status.getId());
                values.put(StatusContract.Column.USER, status.getUser());
                values.put(StatusContract.Column.MESSAGE, status.getMessage());
                values.put(StatusContract.Column.CREATED_AT, status.getCreatedAt().getTime());

                Uri uri = getContentResolver().insert(StatusContract.CONTENT_URI, values);
                if (uri != null) {
                    count++;
                    Log.d(TAG, String.format("%s: %s", status.getUser(), status.getMessage()));
                }
            }
        } catch (YambaClientException e) {
            Log.e(TAG, "Failed to fetch the timeline", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OnDestroy");
    }
}
