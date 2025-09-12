package org.krmdemo.techlabs.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

public class JsonGeneratorTest {

    private final DuplicatingJsonGenerator jsonGenerator = DuplicatingJsonGenerator.create();

//    @Test
//    void testWriteObject_List() throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        jsonGenerator.writeObject(List.of(1, 2, 3));
//        System.out.println(jsonGenerator.primary());
//    }
}
