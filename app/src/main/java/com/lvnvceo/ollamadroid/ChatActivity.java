package com.lvnvceo.ollamadroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.lvnvceo.ollamadroid.ollama.ChatRequest;
import com.lvnvceo.ollamadroid.ollama.ChatResponse;
import com.lvnvceo.ollamadroid.ollama.Messages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton settingsButton;
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private ChatAdapter adapter;
    private final Messages llamaMessages = new Messages();
    private final Gson gson = new Gson();

    // Constants
    private static final String SETTINGS_KEY = "settings";
    private static final String MODEL_KEY = "model";
    private static final String OLLAMA_URL_KEY = "ollama_url";
    private List<ChatMessage> messages =new ArrayList<>();
    private TextView modelTextView;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SETTINGS_KEY, MODE_PRIVATE);

        modelTextView.setText(sharedPreferences.getString(MODEL_KEY,""));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recycler_view);
        settingsButton = findViewById(R.id.buttonSettings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        modelTextView = findViewById(R.id.modelName);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SETTINGS_KEY, MODE_PRIVATE);
        adapter = new ChatAdapter(messages);
        recyclerView.setAdapter(adapter);

        Button sendButton = findViewById(R.id.button_send);
        Button stopButton = findViewById(R.id.button_stop);

        // Settings button click listener
        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(ChatActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });

        // Stop button click listener
        stopButton.setOnClickListener(v -> {
            stopButton.setVisibility(View.INVISIBLE);
            sendButton.setVisibility(View.VISIBLE);
        });

        sendButton.setOnClickListener(v -> {
            stopButton.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.INVISIBLE);

            EditText editTextMessage = findViewById(R.id.edit_text_message);
            Editable newTextMessage = editTextMessage.getText();
            adapter.addMessage(new ChatMessage(R.drawable.baseline_account_circle_24, sharedPreferences.getString("username", "John Doe"), newTextMessage.toString()));
            adapter.addMessage(new ChatMessage(R.drawable.ic_launcher_foreground,"AI", ""));
            adapter.notifyItemInserted(messages.size() - 1);
            recyclerView.smoothScrollToPosition(messages.size() - 1);
            llamaMessages.messages.add(new Messages.Message("user", editTextMessage.getText().toString()));
            ChatRequest chatRequest = new ChatRequest(sharedPreferences.getString(MODEL_KEY, ""), llamaMessages.messages, true);
            RequestBody body = RequestBody.create(gson.toJson(chatRequest), MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(OLLAMA_URL_KEY, "")+"/api/chat")
                    .post(body)
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        handleResponseError(sendButton, stopButton);
                        throw new IOException("Unexpected code " + response);
                    }

                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody != null) {
                            stopButton.setOnClickListener(v -> {
                                try {
                                    response.close();
                                } catch (IllegalStateException ignored) {
                                }
                            handleResponseError(sendButton,stopButton);
                            });

                            processResponseBody(responseBody, messages, sendButton, stopButton);

                        }
                    }
                }
            });
            editTextMessage.setText("");
        });
    }

    private void handleResponseError(Button sendButton, Button stopButton) {
        runOnUiThread(() -> {
            stopButton.setVisibility(View.INVISIBLE);
            sendButton.setVisibility(View.VISIBLE);
        });
    }

    private void processResponseBody(ResponseBody responseBody, List<ChatMessage> messages, Button sendButton, Button stopButton) {
        TextView textViewMessageContent = recyclerView.getLayoutManager().getChildAt(((LinearLayoutManager) recyclerView.getLayoutManager()).getChildCount() - 1).findViewById(R.id.message_content);
        ChatResponse chatResponse = new ChatResponse();
        String line;
        StringBuilder fullResponse = new StringBuilder();

        try {
            while ((line = responseBody.source().readUtf8Line()) != null) {
                chatResponse = gson.fromJson(line, ChatResponse.class);
                fullResponse.append(chatResponse.message.content);
                runOnUiThread(() -> {
                    textViewMessageContent.setText(fullResponse);
                    recyclerView.smoothScrollToPosition(recyclerView.getScrollBarSize());
                });
            }

            runOnUiThread(() -> {
                textViewMessageContent.setText(fullResponse.toString());
                recyclerView.smoothScrollToPosition(recyclerView.getScrollBarSize());
            });

            if (chatResponse.done) {
                addMessage(chatResponse,fullResponse.toString(),stopButton,sendButton);
            }
        } catch (IOException | IllegalStateException e) {
            addMessage(chatResponse,fullResponse.toString(),stopButton,sendButton);
            e.printStackTrace();
        }
    }
    private void addMessage(ChatResponse finalChatResponse, String fullResponse,Button stopButton, Button sendButton) {
        runOnUiThread(() -> {
            adapter.removeMessage(adapter.getItemCount() - 1);
            adapter.addMessage(new ChatMessage(R.drawable.ic_launcher_foreground, "AI", fullResponse));
            llamaMessages.messages.add(new Messages.Message(finalChatResponse.message.role, fullResponse));
            stopButton.setVisibility(View.INVISIBLE);
            sendButton.setVisibility(View.VISIBLE);
        });
    }
}
