package com.example.weatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView textView;
    private final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&APPID=862f3e2c40e24fd815e90b4209795b05&lang=ua&units=metric";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editTextCity);
        textView = findViewById(R.id.textViewShowWeatherData);
    }

    public void showWeatherData(View view) {
        String city = editText.getText().toString().trim();
        if (!city.isEmpty()) {
            DownloadWeatherDataTask task = new DownloadWeatherDataTask();
            String url = String.format(WEATHER_URL, city);
            task.execute(url);
        }


    }

    public class DownloadWeatherDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = bufferedReader.readLine();
                while (line != null) {
                    result.append(line);
                    line = bufferedReader.readLine();
                }
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                String city = jsonObject.getString("name");
                String temp = jsonObject.getJSONObject("main").getString("temp");
                String weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String result = String.format("%s\nТемпература: %s\n На улице: %s", city, temp, weather);
                textView.setText(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
