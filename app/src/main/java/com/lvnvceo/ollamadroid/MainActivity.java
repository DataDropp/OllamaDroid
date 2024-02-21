package com.lvnvceo.ollamadroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private OkHttpClient client = new OkHttpClient();
    ImageView connectionStatusImage;
    TextView connectionStatusText;
    Button goToChatButton;
    private EditText editOllamaURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton settingsButton = findViewById(R.id.buttonSettings);
        connectionStatusImage = findViewById(R.id.imageConnectionStatus);
        connectionStatusText = findViewById(R.id.textConnectionStatus);
        goToChatButton = findViewById(R.id.goToChatButton);
        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });
        goToChatButton.setOnClickListener(v -> {
            Intent chatIntent = new Intent(MainActivity.this,ChatActivity.class);
            startActivity(chatIntent);
        });
        String ollamaURLStr = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getString("ollama_url", "");
        URL ollamaURL = null;
        try {
            ollamaURL = new URL(ollamaURLStr);
            Request request = new Request.Builder()
                    .url(ollamaURL)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        displayError();
                        e.printStackTrace();
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if(!response.isSuccessful()) {
                        runOnUiThread(() -> {
                            displayError();
                        });
                        throw new IOException("Unexpected code " + response);
                    } else {
                        String responseData = response.body().string();
                        runOnUiThread(() -> {
                            System.out.println(response.code());

                            displayOK();
                        });
                    }
                }
            });

        } catch (MalformedURLException ignored) {
        }

    }
    private void displayError() {
        connectionStatusImage.setImageResource(R.drawable.close_fill0_wght700_grad200_opsz48);
        connectionStatusText.setText(R.string.connection_test_failed);
    }
    private void displayOK() {
        connectionStatusImage.setImageResource(R.drawable.check_fill0_wght700_grad200_opsz48);
        connectionStatusText.setText(R.string.connection_test_successful);
        goToChatButton.setVisibility(View.VISIBLE);

    }
}