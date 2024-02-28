package com.lvnvceo.ollamadroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.lvnvceo.ollamadroid.ollama.OllamaModels;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {
    private EditText ollamaAPIURL;
    private EditText systemPrompt;
    private EditText username;
    private Spinner modelSpinner;
    private OkHttpClient okHttpClient;
    private String model;
    private SharedPreferences sharedPreferences;



    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_settings);
        Button saveButton = findViewById(R.id.buttonSave);
        okHttpClient = new OkHttpClient();
        systemPrompt = findViewById(R.id.editSystemPrompt);
        ollamaAPIURL = findViewById(R.id.editOllamaAPIURL);
        username = findViewById(R.id.editUsername);
        modelSpinner = findViewById(R.id.spinnerModels);

        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.settings_key), Context.MODE_PRIVATE);
        systemPrompt.setText(sharedPreferences.getString(getString(R.string.system_prompt_key), null));
        ollamaAPIURL.setText(sharedPreferences.getString(getString(R.string.ollama_url_key),null));
        username.setText(sharedPreferences.getString(getString(R.string.username_key), null));

        System.out.println(sharedPreferences.getString(getString(R.string.ollama_url_key), ""));
        saveButton.setOnClickListener(v -> {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.ollama_url_key), ollamaAPIURL.getText().toString());
            editor.putString(getString(R.string.system_prompt_key), systemPrompt.getText().toString());
            editor.putString(getString(R.string.username_key), username.getText().toString().equals("") ? "John Doe":username.getText().toString());
            editor.putString(getString(R.string.model_key), model);
            editor.apply();
            finish();
        });

        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                model = parent.getItemAtPosition(position).toString();
                ((TextView) parent.getChildAt(0)).setTextColor(getColor(com.google.android.material.R.color.design_default_color_on_primary));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getModels();

    }
    private void getModels() {
        if (sharedPreferences.getString(getString(R.string.ollama_url_key), null) == null) {
            return;
        }
        URL ollamaURL;
        try {
            ollamaURL = new URL(sharedPreferences.getString(getString(R.string.ollama_url_key), null));
        } catch (MalformedURLException e) {
            Snackbar.make(findViewById(R.id.editSystemPrompt), "Invalid URL", 2)
                    .show();
            return;
        }
        if(ollamaURL.getHost().equals("")) {
            return;
        }
        Request request = new Request.Builder()
                .url(ollamaURL+"/api/tags")
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Snackbar.make(findViewById(R.id.editSystemPrompt), "Failed to pull models", 2)
                        .show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()) {
                    return;
                }
                Gson gson = new Gson();
                OllamaModels ollamaModels = gson.fromJson(response.body().string(), OllamaModels.class);
                ArrayList<String> ollamaStrings = new ArrayList<>();
                for (OllamaModels.OllamaModel ollamaModel : ollamaModels.models) {
                    ollamaStrings.add(ollamaModel.name);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ollamaStrings);
                runOnUiThread(() -> {
                    modelSpinner.setAdapter(arrayAdapter);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    modelSpinner.setSelection(arrayAdapter.getPosition(sharedPreferences.getString(getString(R.string.model_key), "")));
                });
            }
        });

    }
}
