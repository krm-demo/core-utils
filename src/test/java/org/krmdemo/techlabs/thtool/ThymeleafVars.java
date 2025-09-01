package org.krmdemo.techlabs.thtool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.krmdemo.techlabs.json.JacksonUtils.dumpAsJsonPrettyPrint;
import static org.krmdemo.techlabs.json.JacksonUtils.jsonTreeFromFile;

public class ThymeleafVars {

    private final static Pattern PATTERN__VAR_FILE_NAME_JSON = Pattern.compile(
        "^var-(?<varName>[^./\\\\<>!$]+)\\.json$", Pattern.CASE_INSENSITIVE);

    private final static ObjectMapper jsonTreeMapper = new ObjectMapper();

    final Map<String, Object> vars = new HashMap<>();

    void processDirectory(File varsDir) {
        File[] filesArr = Objects.requireNonNull(varsDir).listFiles();
        if (filesArr == null) {
            throw new IllegalArgumentException("not a directory - " + varsDir);
        }
        int count = 0;
        for (File varFile : filesArr) {
            System.out.printf("- processing the file '%s' ", varFile);
            Matcher matcher = PATTERN__VAR_FILE_NAME_JSON.matcher(varFile.getName());
            if (!matcher.matches()) {
                System.out.println("(skipped)");
                continue;
            }
            String varName = matcher.group("varName");
            JsonNode jsonNode = jsonTreeFromFile(varFile);
            if (jsonNode.getNodeType() == JsonNodeType.OBJECT
                  || jsonNode.getNodeType() == JsonNodeType.ARRAY) {
                vars.put(varName, jsonNode);
                count++;
                System.out.printf("(loaded as variable '%s' from) --> %s%n",
                    varName, dumpAsJsonPrettyPrint(jsonNode));
            } else {
                System.out.println("(not recognized as JSON-Object or JSON-Array)");
            }
        }
        System.out.printf("... %d JSON-files were loaded as 'th-tool' variables from directory '%s' ...%n",
            count, varsDir);
    }
}
