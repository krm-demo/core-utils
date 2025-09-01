package org.krmdemo.techlabs.thtool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.apache.commons.io.FileUtils;
import org.krmdemo.techlabs.sysdump.PropertiesUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.krmdemo.techlabs.json.JacksonUtils.dumpAsJsonPrettyPrint;
import static org.krmdemo.techlabs.json.JacksonUtils.jsonTreeFromFile;
import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.sortedMap;

public class ThymeleafVars {

    /**
     * The result container of 'th-tool' variables by their names to access from templates
     */
    final Map<String, Object> vars = new HashMap<>();

    void processVarFilePair(String varFilePair) {
        System.out.printf("- processing the var-file pair '%s' ", varFilePair);
        Matcher matcher = PATTERN__VAR_FILE_PAIR.matcher(varFilePair);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format(
                "var-file pair '%s' does not match the pattern /%s/i",
                varFilePair, PATTERN__VAR_FILE_PAIR.pattern()));
        }
        String varName = matcher.group("varName");
        String varFilePath = matcher.group("filePath");
        File varFile = new File(varFilePath);
        if (!varFile.isFile()) {
            throw new IllegalArgumentException(String.format(
                "var-file '%s' does not exist, or not a regular file", varFile));
        }
        String varFileExt = matcher.group("fileExt");
        if (EXT_JSON.equalsIgnoreCase(varFileExt)) {
            putVarFileJson(varName, varFile);
        } else if (EXT_PROPS.equalsIgnoreCase(varFileExt)) {
            putVarFileProperties(varName, varFile);
        } else {
            throw new IllegalArgumentException(String.format(
                "unrecognized extension '%s' of var-file '%s'", varFileExt, varFile));
        }
    }

    void processDirectory(File varsDir) {
        File[] filesArr = Objects.requireNonNull(varsDir).listFiles();
        if (filesArr == null) {
            throw new IllegalArgumentException("not a directory - " + varsDir);
        }
        int countJson = 0;
        int countProperties = 0;
        for (File varFile : filesArr) {
            System.out.printf("- processing the var-file '%s' ", varFile);
            Matcher matcher = PATTERN__VAR_FILE_NAME.matcher(varFile.getName());
            if (!matcher.matches()) {
                System.out.println("(skipped)");
                continue;
            }
            String varName = matcher.group("varName");
            String varFileExt = matcher.group("fileExt");
            if (EXT_JSON.equalsIgnoreCase(varFileExt)) {
                countJson += putVarFileJson(varName, varFile) ? 1 : 0;
            } else if (EXT_PROPS.equalsIgnoreCase(varFileExt)) {
                countProperties += putVarFileProperties(varName, varFile) ? 1 : 0;
            } else {
                System.out.printf("(skipped, because of unrecognized extension '%s')%n", varFileExt);
            }
        }
        if (countJson > 0) {
            System.out.printf(
                "... %d '%s'-files were loaded as 'th-tool' variables from directory '%s' ...%n",
                countJson, EXT_JSON, varsDir
            );
        }
        if (countProperties > 0) {
            System.out.printf(
                "... %d '%s'-files were loaded as 'th-tool' variables from directory '%s' ...%n",
                countJson, EXT_PROPS, varsDir
            );
        }
        if (countJson + countProperties == 0) {
            System.out.printf("... no 'th-tool' variables were loaded from directory '%s' ...%n", varsDir);
        }
    }

    private boolean putVarFileJson(String varName, File jsonFile) {
        JsonNode jsonNode = jsonTreeFromFile(jsonFile);
        if (jsonNode.getNodeType() == JsonNodeType.OBJECT
            || jsonNode.getNodeType() == JsonNodeType.ARRAY) {
            vars.put(varName, jsonNode);
            System.out.printf("(loaded as JSON into variable '%s') --> %s%n",
                varName, dumpAsJsonPrettyPrint(jsonNode));
            return true;
        } else {
            System.out.println("(not recognized neither as JSON-Object nor as JSON-Array)");
            return false;
        }
    }

    private boolean putVarFileProperties(String varName, File propsFile) {
        Map<String, String> propsMap = sortedMap(PropertiesUtils.propsEntries(propsFile));
        if (propsMap.isEmpty()) {
            System.out.println("(no properties were loaded)");
            return false;
        } else {
            vars.put(varName, propsMap);
            System.out.printf("(loaded as properties into variable '%s') --> %s%n",
                varName, dumpAsJsonPrettyPrint(propsMap));
            return true;
        }
    }

    private final static String EXT_JSON = ".json";
    private final static String EXT_PROPS = ".properties";

    private final static Pattern PATTERN__VAR_FILE_NAME = Pattern.compile(
        "^var-(?<varName>[^./\\\\<>!$]+)(?<fileExt>\\.(?:json|properties))$",
        Pattern.CASE_INSENSITIVE
    );

    private final static Pattern PATTERN__VAR_FILE_PAIR = Pattern.compile(
        "^(?<varName>[^./\\\\<>!$]+)=(?<filePath>[^./\\\\<>!$]+(?<fileExt>\\.(?:json|properties)))$",
        Pattern.CASE_INSENSITIVE
    );
}
