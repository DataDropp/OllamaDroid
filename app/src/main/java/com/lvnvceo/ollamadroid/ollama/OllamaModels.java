package com.lvnvceo.ollamadroid.ollama;

import java.util.ArrayList;

public class OllamaModels {
    public ArrayList<OllamaModel> models;
    public class OllamaModel {

        public String name;
        public String modified_at;
        public Object size;
        public String digest;
        public Details details;

        private class Details {
            public String format;
            public String family;
            public Object families;
            public String parameter_size;
            public String quantization_level;
        }
    }
}

