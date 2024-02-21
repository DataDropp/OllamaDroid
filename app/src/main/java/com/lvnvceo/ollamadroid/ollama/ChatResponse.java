package com.lvnvceo.ollamadroid.ollama;

import java.util.Date;

public class ChatResponse {
    public String model;
    public Date created_at;
    public Messages.Message message;
    public boolean done;
    public long total_duration;
    public int load_duration;
    public int prompt_eval_count;
    public int prompt_eval_duration;
    public int eval_count;
    public long eval_duration;
}