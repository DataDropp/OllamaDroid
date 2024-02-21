package com.lvnvceo.ollamadroid;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.profileImage.setImageResource(message.getProfileImage());
        holder.profileName.setText(message.getProfileName());
        holder.messageContent.setText(message.getMessageContent());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView profileName;
        TextView messageContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileName = itemView.findViewById(R.id.profile_name);
            messageContent = itemView.findViewById(R.id.message_content);
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyDataSetChanged(); // Notify adapter of dataset change
    }
    @SuppressLint("NotifyDataSetChanged")
    public void removeMessage(int pos){
        messages.remove(pos);
        notifyDataSetChanged();
    }
}
