package diamond.iain.yamba;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsFragment extends Fragment {
    private static final String TAG = StatusProvider.class.getSimpleName();

    private TextView textUser, textMessage, textCreatedAt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_item, null, false);
        textUser = (TextView) view.findViewById(R.id.list_item_text_user);
        textMessage = (TextView) view.findViewById(R.id.list_item_text_message);
        textCreatedAt = (TextView) view.findViewById(R.id.list_item_text_created_at);
        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        long id = getActivity().getIntent().getLongExtra(StatusContract.Column.ID, -1);
        updateView(id);
    }

    public void updateView(long id) {
        Log.d(TAG, "updateView: id=" + id);

        if (id == -1) {
            textUser.setText("");
            textMessage.setText("");
            textCreatedAt.setText("");
            return;
        }

        Uri uri = ContentUris.withAppendedId(StatusContract.CONTENT_URI, id);
        Log.d(TAG, uri.toString());
        ContentResolver resolver = getActivity().getContentResolver();
        Log.d(TAG, resolver.toString());
        Cursor cursor = resolver.query(uri, null, null, null, null);
        Log.d(TAG, cursor.toString());

        if (!cursor.moveToFirst())
            return;

        String user = cursor.getString(cursor.getColumnIndex(StatusContract.Column.USER));
        String message = cursor.getString(cursor.getColumnIndex(StatusContract.Column.MESSAGE));
        long createdAt = cursor.getLong(cursor.getColumnIndex(StatusContract.Column.CREATED_AT));
        textUser.setText(user);
        textMessage.setText(message);
        textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(createdAt));
    }
}
