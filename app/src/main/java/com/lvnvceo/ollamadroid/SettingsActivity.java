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
        username =  findViewById(R.id.editUsername);
        modelSpinner = findViewById(R.id.spinnerModels);

        sharedPreferences = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        systemPrompt.setText(sharedPreferences.getString("system_prompt",""));
        ollamaAPIURL.setText(sharedPreferences.getString("ollama_url",""));
        username.setText(sharedPreferences.getString("username",""));

        System.out.println(sharedPreferences.getString("ollama_url","fdsfsafsa"));
        saveButton.setOnClickListener(v -> {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("ollama_url", ollamaAPIURL.getText().toString());
            editor.putString("system_prompt", systemPrompt.getText().toString());
            editor.putString("username", username.getText().toString());
            editor.putString("model", model);
            editor.apply();
            finish();
        });
        Request request = new Request.Builder()
                .url(sharedPreferences.getString("ollama_url",null)+"/api/tags")
                .build();

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
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Snackbar.make(findViewById(R.id.editSystemPrompt),"Failed to pull modelfiles",2)
                        .show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Gson gson = new Gson();
                OllamaModels ollamaModels = gson.fromJson(response.body().string(), OllamaModels.class);
                ArrayList<String> ollamaStrings = new ArrayList<>();
                for (OllamaModels.OllamaModel ollamaModel:ollamaModels.models ){
                    ollamaStrings.add(ollamaModel.name);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ollamaStrings);
                runOnUiThread(() -> {
                    modelSpinner.setAdapter(arrayAdapter);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    modelSpinner.setSelection(arrayAdapter.getPosition(sharedPreferences.getString("model", "")));
                });
            }
        });


    }
}
