package org.krmdemo.techlabs.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MyDumper {

    final ObjectMapper mapper = new ObjectMapper();  // <-- ???
    final JsonNode root;

    public MyDumper(Object value) {
        this.root = mapper.valueToTree(value);
    }
}
