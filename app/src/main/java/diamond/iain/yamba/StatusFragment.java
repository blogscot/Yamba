package diamond.iain.yamba;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class StatusFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "StatusActivity";
    private EditText editStatus;
    private TextView textCount;
    private int defaultTextColor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        editStatus = (EditText) view.findViewById(R.id.editStatus);
        Button buttonTweet = (Button) view.findViewById(R.id.buttonTweet);
        textCount = (TextView) view.findViewById(R.id.textCount);

        buttonTweet.setOnClickListener(this);

        defaultTextColor = textCount.getTextColors().getDefaultColor();
        editStatus.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {
                int count = 140 - editStatus.length();
                textCount.setText(Integer.toString(count));
                if (count < 0)
                    textCount.setTextColor(Color.RED);
                else if (count < 25)
                    textCount.setTextColor(Color.CYAN);
                else
                    textCount.setTextColor(defaultTextColor);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        String status = editStatus.getText().toString();
        Log.d(TAG, "onClicked with status:" + status);

        new PostTask().execute(status);
    }

    private final class PostTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String username = prefs.getString("username", "");
            String password = prefs.getString("password", "");

            if (username.isEmpty() || password.isEmpty()) {
                getActivity().startActivity(new Intent(getActivity(), SettingsActivity.class));
                return "Please update your username and password";
            }

            YambaClient yambaClient = new YambaClient(username, password);

            try {
                yambaClient.postStatus(params[0]);
                return "Successfully posted";
            } catch (YambaClientException e) {
                e.printStackTrace();
                return "Failed to post to yamba service";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(StatusFragment.this.getActivity(), s, Toast.LENGTH_LONG).show();
        }
    }
}
