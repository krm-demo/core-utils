package org.krmdemo.techlabs.thtool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.extern.slf4j.Slf4j;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.thymeleaf.context.AbstractContext;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.krmdemo.techlabs.core.utils.PropertiesUtils.propsMapFromFile;
import static org.krmdemo.techlabs.core.utils.PropertiesUtils.propsMapResource;
import static org.krmdemo.techlabs.json.JacksonUtils.dumpAsJsonPrettyPrint;
import static org.krmdemo.techlabs.json.JacksonUtils.jsonArrFromFile;
import static org.krmdemo.techlabs.json.JacksonUtils.jsonArrFromResource;
import static org.krmdemo.techlabs.json.JacksonUtils.jsonObjFromFile;
import static org.krmdemo.techlabs.json.JacksonUtils.jsonObjFromResource;
import static org.krmdemo.techlabs.json.JacksonUtils.jsonTreeFromFile;
import static org.krmdemo.techlabs.json.JacksonUtils.jsonTreeFromResource;

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
@Slf4j
public class ThymeleafToolCtx extends AbstractContext {

    /**
     * The default directory for <b>{@code th-tool}</b> var-files.
     */
    public static final String DEFAULT_VARS_DIR = ".github/th-vars";

    /**
     * The same as {@link #DEFAULT_VARS_DIR} but of type {@link File}
     */
    public static final File DEFAULT_VARS_DIR__AS_FILE = new File(DEFAULT_VARS_DIR);

    // --------------------------------------------------------------------------------------------

    /**
     * A built-in helper to access some variables in thread-safe way from other helpers.
     * <hr/>
     * It's available in th-templates by the name {@code tth}, but in most cases
     * it could be used in other helper to realize what exactly template is processing now.
     * In the future, it could also be used to improve and clarify handling the errors.
     */
    public class Helper {
        public static final String VAR_NAME__HELPER = "tth";
        private final ThreadLocal<File> inputDir = new ThreadLocal<>();
        private final ThreadLocal<File> inputFile = new ThreadLocal<>();

        private Helper() {
            //following line causes the warning "... possible 'this' escape before subclass ..."
            setVariable(VAR_NAME__HELPER, this);
        }

        public File getInputDir() {
            return this.inputDir.get();
        }

        public void setInputDir(File dir) {
            this.inputDir.set(dir);
        }

        public File getInputFile() {
            return this.inputFile.get();
        }

        public void setInputFile(File file) {
            this.inputFile.set(file);
        }

        public boolean isInputFileAvailable() {
            return getInputFile() != null;
        }

        public String getThreadName() {
            return Thread.currentThread().getName();
        }

        @Override
        public String toString() {
            return DumpUtils.dumpAsJsonTxt(this);
        }
    }

    private final Helper thToolHelper = new Helper();

    /**
     * @return the instance of thread-safe internal helper (the same as {@code [[${ tth }]]} in th-template)
     */
    public Helper getThToolHelper() {
        return thToolHelper;
    }

    // --------------------------------------------------------------------------------------------

    /**
     * The same as {@link #typedVar(String, Class, Object)}, but casting to {@link Map Map&lt;String,Object&gt;}
     * and returning {@link Collections#emptyMap() empty map}, if such variable does not exist.
     *
     * @param varName the name of <b>{@code th-tool}</b>-variable
     * @return the value of variable {@code varName} as {@link Map Map&lt;String,Object&gt;} or
     * {@link Collections#emptyMap() empty map} if such variable does not exist
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> propsVar(String varName) {
        return (Map<String, Object>)typedVar(varName, Map.class, Collections.emptyMap());
    }

    /**
     * The same as {@link #typedVar(String, Class)}, but returning {@code defaultValue}
     * if the variable withh name {@code varName} does not exist in this context.
     *
     * @param varName the name of <b>{@code th-tool}</b>-variable
     * @param varClass the class to cast the value of variable to
     * @param defaultValue the default value to return if variable does not exist
     * @return the value of variable {@code varName} of type {@code varClass}
     * @param <T> the type to cast the variable to
     */
    public <T> T typedVar(String varName, Class<T> varClass, T defaultValue) {
        T typedVarObj = typedVar(varName, varClass);
        return typedVarObj == null ? defaultValue : typedVarObj;
    }

    /**
     * Getting the value of <b>{@code th-tool}</b>-variable in this context with casting it to proper type
     *
     * @param varName the name of <b>{@code th-tool}</b>-variable
     * @param varClass the class to cast the value of variable to
     * @return the value of variable {@code varName} of type {@code varClass}
     * @param <T> the type to cast the variable to
     */
    @SuppressWarnings("unchecked")
    public <T> T typedVar(String varName, Class<T> varClass) {
        Object varObj = getVariable(varName);
        if (varObj == null) {
            return null;
        } else if (varClass.isAssignableFrom(varObj.getClass())){
            return (T)varObj;
        } else {
            throw new IllegalStateException(String.format(
                "Context variable '%s' of type '%s' is not an instance of class %s'",
                varName, varObj.getClass(), varClass
            ));
        }
    }

    /**
     * Creating the variable in <b>{@code th-tool}</b>-context, whose name corresponds to the left side
     * of assignment-expression and whose value corresponds to the content of the file,
     * whose path in the local file-system is on the right side of assignment-expression
     * of form {@code varName=path/to/file}.
     * <hr/>
     * The extension of file must be either {@code .json} or {@code .properties}
     * and the content of the file will be processed according to that extension.
     * In case of any other extension - the {@link IllegalStateException} will be raised.
     *
     * @param varFilePair assignment-expression of form {@code varName=path/to/file}
     * @see ThymeleafTool#varFilePairs
     */
    public void processVarFilePair(String varFilePair) {
        logInfo("- processing the var-file pair '%s' ", varFilePair);
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

    /**
     * Creating the variable in <b>{@code th-tool}</b>-context, whose name corresponds to the left side
     * of assignment-expression and whose value corresponds to the content of the resource,
     * whose path in {@code CLASSPATH} is on the right side of assignment-expression
     * of form {@code varName=path/to/file}.
     * <hr/>
     * The extension of file must be either {@code .json} or {@code .properties}
     * and the content of the file will be processed according to that extension.
     * In case of any other extension - the {@link IllegalStateException} will be raised.
     *
     * @param varResPair assignment-expression of form {@code varName=path/to/file}
     * @see ThymeleafTool#varResPairs
     */
    public void processVarResourcePair(String varResPair) {
        logInfo("- processing the var-resource pair '%s' ", varResPair);
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

    /**
     * Process each file in the directory {@code varsDir} in a way that the name of each file
     * is used to retrieve the name of <b>{@code th-tool}</b>-variable. The prefix {@code var-}
     * and the extension {@code *.json} and {@code *.properties} are used as filter and other files
     * will be ignored. The substring that in the middle of prefix {@code var-} and the extension
     * will be the target variable name (e.g. the file with name {@code var-githubInputs.json}
     * will be deserialized as {@code JSON}-file via <a href="https://github.com/FasterXML/jackson">Jackson</a>
     * and the variable with the name {@code githubInputs} and the value of type {@code Map<String,Object>}
     * or {@code List} will be created in this context.
     *
     * @param varsDir the path of directory on local file-system to process
     * @see ThymeleafTool#varsDir
     */
    public void processDirectory(File varsDir) {
        File[] filesArr = Objects.requireNonNull(varsDir).listFiles();
        if (filesArr == null) {
            throw new IllegalArgumentException("not a directory - " + varsDir);
        }
        int countJson = 0;
        int countProperties = 0;
        for (File varFile : filesArr) {
            Matcher matcher = PATTERN__VAR_FILE_NAME.matcher(varFile.getName());
            if (!matcher.matches()) {
                logInfo("- the var-file '%s' is skipped", varFile);
                continue;
            }
            String varName = matcher.group("varName");
            String varFileExt = matcher.group("fileExt");
            if (EXT_JSON.equalsIgnoreCase(varFileExt)) {
                logInfo("- the var-file '%s' is processing as JSON", varFile);
                countJson += putVarFileJson(varName, varFile) ? 1 : 0;
            } else if (EXT_PROPS.equalsIgnoreCase(varFileExt)) {
                logInfo("- the var-file '%s' is processing as properties-file", varFile);
                countProperties += putVarFileProperties(varName, varFile) ? 1 : 0;
            } else {
                logInfo("- the var-file '%s' is skipped because unrecognized extension",
                    varFile, varFileExt);
            }
        }
        if (countJson > 0) {
            logInfo("... %d '%s'-files were loaded as 'th-tool' variables from directory '%s' ...",
                countJson, EXT_JSON, varsDir
            );
        }
        if (countProperties > 0) {
            logInfo("... %d '%s'-files were loaded as 'th-tool' variables from directory '%s' ...",
                countProperties, EXT_PROPS, varsDir
            );
        }
        if (countJson + countProperties == 0) {
            logInfo("... no 'th-tool' variables were loaded from directory '%s' ...", varsDir);
        }
    }

    // --------------------------------------------------------------------------------------------

    private boolean putVarFileJson(String varName, File jsonFile) {
        JsonNode jsonNode = jsonTreeFromFile(jsonFile);
        switch (jsonNode.getNodeType()) {
            case JsonNodeType.OBJECT:
                setVariable(varName, jsonObjFromFile(jsonFile));
                logDebug("(JSON-file is loaded into variable '%s' as Map<String,?>) --> %s",
                    () -> varName, () -> dumpAsJsonPrettyPrint(jsonNode));
                return true;
            case JsonNodeType.ARRAY:
                setVariable(varName, jsonArrFromFile(jsonFile));
                logDebug("(JSON-file is loaded into variable '%s' as List<?>) --> %s",
                    () -> varName, () -> dumpAsJsonPrettyPrint(jsonNode));
                return true;
            default:
                logDebug("(not recognized neither as JSON-Object nor as JSON-Array)");
                return false;
        }
    }

    private boolean putVarFileProperties(String varName, File propsFile) {
        Map<String, String> propsMap = propsMapFromFile(propsFile);
        if (propsMap.isEmpty()) {
            logDebug("(no properties were loaded)");
            return false;
        } else {
            setVariable(varName, propsMap);
            logDebug("(loaded as properties-file into variable '%s') --> %s",
                () -> varName, () -> dumpAsJsonPrettyPrint(propsMap));
            return true;
        }
    }

    private void putVarResourceJson(String varName, String jsonResPath) {
        JsonNode jsonNode = jsonTreeFromResource(jsonResPath);
        switch (jsonNode.getNodeType()) {
            case JsonNodeType.OBJECT:
                setVariable(varName, jsonObjFromResource(jsonResPath));
                logDebug("(loaded from JSON-resource into variable '%s' as Map<String,?>) --> %s",
                    () -> varName, () -> dumpAsJsonPrettyPrint(jsonNode));
                break;
            case JsonNodeType.ARRAY:
                setVariable(varName, jsonArrFromResource(jsonResPath));
                logDebug("(loaded as JSON-resource into variable '%s' as List<?>) --> %s",
                    () -> varName, () -> dumpAsJsonPrettyPrint(jsonNode));
                break;
            default:
                logDebug("(not recognized neither as JSON-Object nor as JSON-Array)");
        }
    }

    private void putVarResourceProperties(String varName, String propsResPath) {
        Map<String, String> propsMap = propsMapResource(propsResPath);
        if (propsMap.isEmpty()) {
            logDebug("(no properties were loaded)");
        } else {
            setVariable(varName, propsMap);
            logDebug("(loaded as properties-resource into variable '%s') --> %s",
                () -> varName, () -> dumpAsJsonPrettyPrint(propsMap));
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

    // --------------------------------------------------------------------------------------------

    private static void logInfo(String formatString, Object... formatArgs) {
        if (log.isInfoEnabled()) {
            log.info(String.format(formatString, formatArgs));
        }
    }

    private static void logDebug(String formatString, Supplier<?>... formatArgs) {
        if (log.isDebugEnabled()) {
            Object[] args = Arrays.stream(formatArgs).map(Supplier::get).toArray();
            log.debug(String.format(formatString, args));
        }
    }
}
