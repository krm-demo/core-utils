package org.krmdemo.techlabs.thtool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.thymeleaf.context.AbstractContext;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.krmdemo.techlabs.json.JacksonUtils.dumpAsJsonPrettyPrint;
import static org.krmdemo.techlabs.json.JacksonUtils.jsonTreeFromFile;
import static org.krmdemo.techlabs.json.JacksonUtils.jsonTreeFromResource;
import static org.krmdemo.techlabs.core.utils.PropertiesUtils.propsMapFromFile;
import static org.krmdemo.techlabs.core.utils.PropertiesUtils.propsMapResource;

/**
 * This class represents the context of {@link ThymeleafTool}, which holds the variables
 * that are available later in <a href="https://www.thymeleaf.org/">Thymeleaf</a>-templates.
 * <hr/>
 * The variables could be loaded in following way:<ul>
 *     <li>using {@link #processVarFilePair} from a file for individual variable</li>
 *     <li>using {@link #processVarResourcePair} from a classpath-resource for individual variable</li>
 *     <li>using {@link #processDirectory} from a directory of files for multiple variables</li>
 *     <li>using the inherited API like {@link #setVariable(String, Object)}</li>
 * </ul>
 */
public class ThymeleafToolCtx extends AbstractContext {

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

    void processVarResourcePair(String varResPair) {
        System.out.printf("- processing the var-resource pair '%s' ", varResPair);
        Matcher matcher = PATTERN__VAR_RES_PAIR.matcher(varResPair);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format(
                "var-resource pair '%s' does not match the pattern /%s/i",
                varResPair, PATTERN__VAR_RES_PAIR.pattern()));
        }
        String varName = matcher.group("varName");
        String varResPath = matcher.group("resourcePath");
        String varFileExt = matcher.group("fileExt");
        if (EXT_JSON.equalsIgnoreCase(varFileExt)) {
            putVarResourceJson(varName, varResPath);
        } else if (EXT_PROPS.equalsIgnoreCase(varFileExt)) {
            putVarResourceProperties(varName, varResPath);
        } else {
            throw new IllegalArgumentException(String.format(
                "unrecognized extension '%s' of var-resource '%s'", varFileExt, varResPath));
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
                countProperties, EXT_PROPS, varsDir
            );
        }
        if (countJson + countProperties == 0) {
            System.out.printf("... no 'th-tool' variables were loaded from directory '%s' ...%n", varsDir);
        }
    }

    // --------------------------------------------------------------------------------------------

    private boolean putVarFileJson(String varName, File jsonFile) {
        JsonNode jsonNode = jsonTreeFromFile(jsonFile);
        if (jsonNode.getNodeType() == JsonNodeType.OBJECT
            || jsonNode.getNodeType() == JsonNodeType.ARRAY) {
            setVariable(varName, jsonNode);
            System.out.printf("(loaded as JSON into variable '%s') --> %s%n",
                varName, dumpAsJsonPrettyPrint(jsonNode));
            return true;
        } else {
            System.out.println("(not recognized neither as JSON-Object nor as JSON-Array)");
            return false;
        }
    }

    private boolean putVarFileProperties(String varName, File propsFile) {
        Map<String, String> propsMap = propsMapFromFile(propsFile);
        if (propsMap.isEmpty()) {
            System.out.println("(no properties were loaded)");
            return false;
        } else {
            setVariable(varName, propsMap);
            System.out.printf("(loaded as properties into variable '%s') --> %s%n",
                varName, dumpAsJsonPrettyPrint(propsMap));
            return true;
        }
    }

    private void putVarResourceJson(String varName, String jsonResPath) {
        JsonNode jsonNode = jsonTreeFromResource(jsonResPath);
        if (jsonNode.getNodeType() == JsonNodeType.OBJECT
            || jsonNode.getNodeType() == JsonNodeType.ARRAY) {
            setVariable(varName, jsonNode);
            System.out.printf("(loaded as JSON into variable '%s') --> %s%n",
                varName, dumpAsJsonPrettyPrint(jsonNode));
        } else {
            System.out.println("(not recognized neither as JSON-Object nor as JSON-Array)");
        }
    }

    private void putVarResourceProperties(String varName, String propsResPath) {
        Map<String, String> propsMap = propsMapResource(propsResPath);
        if (propsMap.isEmpty()) {
            System.out.println("(no properties were loaded)");
        } else {
            setVariable(varName, propsMap);
            System.out.printf("(loaded as properties into variable '%s') --> %s%n",
                varName, dumpAsJsonPrettyPrint(propsMap));
        }
    }

    // --------------------------------------------------------------------------------------------

    private final static String EXT_JSON = ".json";
    private final static String EXT_PROPS = ".properties";

    private final static Pattern PATTERN__VAR_FILE_NAME = Pattern.compile(
        "^var-(?<varName>[^./\\\\<>!$=]+)(?<fileExt>\\.(?:json|properties))$",
        Pattern.CASE_INSENSITIVE
    );

    private final static Pattern PATTERN__VAR_FILE_PAIR = Pattern.compile(
        "^(?<varName>[^./\\\\<>!$=]+)=(?<filePath>[^<>!$=]+(?<fileExt>\\.(?:json|properties)))$",
        Pattern.CASE_INSENSITIVE
    );

    private final static Pattern PATTERN__VAR_RES_PAIR = Pattern.compile(
        "^(?<varName>[^./\\\\<>!$=]+)=(?<resourcePath>[^<>!$=]+(?<fileExt>\\.(?:json|properties)))$",
        Pattern.CASE_INSENSITIVE
    );
}
