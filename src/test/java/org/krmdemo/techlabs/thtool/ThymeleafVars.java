package org.krmdemo.techlabs.thtool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.krmdemo.techlabs.json.JacksonUtils.jsonTreeFromFile;

public class ThymeleafVars {

    private final static Pattern PATTERN__VAR_FILE_NAME_JSON = Pattern.compile(
        "^var-(?<varName>[^!$:><./\\\\]\\.json$)", Pattern.CASE_INSENSITIVE);

    private final static ObjectMapper jsonTreeMapper = new ObjectMapper();

    final Map<String, Object> vars = new HashMap<>();

    void processDirectory(File varsDir) {
        String[] filesArr = Objects.requireNonNull(varsDir).list();
        if (filesArr == null) {
            throw new IllegalArgumentException("not a directory - " + varsDir);
        }
        for (String fileName : filesArr) {
            Matcher matcher = PATTERN__VAR_FILE_NAME_JSON.matcher(fileName);
            if (!matcher.matches()) {
                continue;
            }
            String varName = matcher.group("varName");
            JsonNode jsonNode = jsonTreeFromFile(new File(fileName));
            if (jsonNode.getNodeType() == JsonNodeType.OBJECT
                  || jsonNode.getNodeType() == JsonNodeType.ARRAY) {
                vars.put(varName, jsonNode);

            } else {
                throw new IllegalStateException("not recognized as JSON-Object or JSON-Array - " + fileName);
            }
        }
    }
}
