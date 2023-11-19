package com.example.nutritrack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;

public class SearchActivity extends AppCompatActivity {
    private EditText searchEditText;
    private TextView responseTextView;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.search);
        responseTextView = findViewById(R.id.response);
        searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = searchEditText.getText().toString().trim();
                if (!userInput.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Searching...", Toast.LENGTH_SHORT).show();
                    makeAPIRequest(userInput);
                } else {
                    Toast.makeText(SearchActivity.this, "Please enter a search query", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static class APITask extends AsyncTask<String, Void, String> {

        private WeakReference<SearchActivity> activityReference;

        APITask(SearchActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SearchActivity activity = activityReference.get();
            if (activity != null) {
                Toast.makeText(activity, "Searching...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String userInput = params[0];
            String result = null;
            try {
                String apiUrl = "https://api.ydc-index.io/search?query=" + URLEncoder.encode(userInput + "make it nutrition based ", "UTF-8");
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                Request request = new Request.Builder()
                        .url(apiUrl)
                        .method("GET", null)
                        .addHeader("X-API-Key", "000bfccd-3809-473d-8ccb-1f9e7c7ca92e<__>1OD6O3ETU8N2v5f4sVKzyqWy")
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    result = response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            SearchActivity activity = activityReference.get();
            if (activity != null && result != null) {
                activity.handleResponse(result);
            } else {
                if (activity != null) {
                    activity.responseTextView.setText("Failed to fetch data");
                }
            }
        }
    }

    private void makeAPIRequest(String userInput) {
        APITask apiTask = new APITask(this);
        apiTask.execute(userInput);
    }

    private void handleResponse(String responseData) {
        try {
            JSONObject jsonResponse = new JSONObject(responseData);
            JSONArray hitsArray = jsonResponse.getJSONArray("hits");
            if (hitsArray.length() > 0) {
                JSONObject firstHit = hitsArray.getJSONObject(0);
                JSONArray snippetsArray = firstHit.getJSONArray("snippets");
                if (snippetsArray.length() > 0) {
                    String firstSnippet = snippetsArray.getString(0);
                    String[] paragraphs = firstSnippet.split("\n\n"); // Split by double newline to get paragraphs
                    if (paragraphs.length > 0) {
                        // Display the first paragraph in responseTextView
                        responseTextView.setText(paragraphs[0]);
                    } else {
                        responseTextView.setText("No paragraph found");
                    }
                } else {
                    responseTextView.setText("No snippets found");
                }
            } else {
                responseTextView.setText("No hits found");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            responseTextView.setText("Error parsing response");
        }
    }
}
