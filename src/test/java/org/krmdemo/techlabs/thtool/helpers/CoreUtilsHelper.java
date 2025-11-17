package org.krmdemo.techlabs.thtool.helpers;

import org.krmdemo.techlabs.core.datetime.CoreDateTimeUtils;
import org.krmdemo.techlabs.core.datetime.DateTimeTriplet;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.core.dump.render.RenderSpec;
import org.krmdemo.techlabs.thtool.ThymeleafTool;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/**
 * This class represents a <b>{@code th-tool}</b>-helper that is a delegate to utility-classes of <b>core-utils</b>-library.
 * The properties of this helper are available from <b>{@code th-tool}</b>-templates by name {@code cu}.
 * <hr/>
 * TODO: think about automatic registration and delegation either via <a href="https://asm.ow2.io/">ASM</a>-library<br/>
 * TODO: or via automatic interface generation using annotation-processor like <a href="https://projectlombok.org/">Lombok</a> does.
 * <hr/>
 * TODO: think of using the custom Thymeleaf-Dialect instead of registering the variable in Thymeleaf-Context
 *
 * @see <a href="https://github.com/adityasolge93/G3CodeGenerator">
 *     (GitHub) G3CodeGenerator
 * </a>
 * @see <a href="https://www.baeldung.com/java-annotation-processing-builder">
 *     (Baeldung) Java Annotation Processing and <i>Creating a Builder</i>
 * </a>
 * @see <a href="https://github.com/thymeleaf/thymeleaf-extras-java8time/tree/3.0-master">
 *     (GitHub) Thymeleaf - Module for Java 8 Time API <br/><small>an example of the custom Thymeleaf-Dialect</small>
 * </a>
 */
public class CoreUtilsHelper {

    /**
     * The name of <b>{@code th-tool}</b>-variable for helper-object {@link CoreUtilsHelper}
     */
    public static final String VAR_NAME__HELPER = "cu";

    /**
     * A factory-method that returns an instance of {@link CoreUtilsHelper}
     * that was previously registered with {@link #register(ThymeleafToolCtx)}.
     *
     * @param ttCtx <b>{@code th-tool}</b>-context to wrap
     * @return an instance of {@link CoreUtilsHelper} for access from other helpers
     */
    public static CoreUtilsHelper fromCtx(ThymeleafToolCtx ttCtx) {
        CoreUtilsHelper helper = ttCtx.typedVar(VAR_NAME__HELPER, CoreUtilsHelper.class);
        if (helper == null) {
            register(ttCtx);
            helper = ttCtx.typedVar(VAR_NAME__HELPER, CoreUtilsHelper.class);
        }
        return helper;
    }

    /**
     * Context-registering method of functional type {@link Consumer Consumer&lt;ThymeleafToolCtx&gt;}.
     * Should be used when initializing the instance of {@link ThymeleafTool},
     * which allows to decouple the dependencies between <b>{@code th-tool}</b> and helper-objects.
     *
     * @param ttCtx <b>{@code th-tool}</b>-context to register this helper in
     */
    public static void register(ThymeleafToolCtx ttCtx) {
        ttCtx.setVariable(VAR_NAME__HELPER, new CoreUtilsHelper());
    }

    // --------------------------------------------------------------------------------------------

    /**
     * Delegates to {@link DumpUtils#dumpAsJsonTxt(Object)}
     *
     * @param objToDump object to dump with default highlighting
     * @return JSON-representation of an object in text-format as {@link String}
     */
    public String dumpAsJsonTxt(Object objToDump) {
        return DumpUtils.dumpAsJsonTxt(objToDump);
    }

    /**
     * Delegates to {@link DumpUtils#dumpAsJsonTxt(Object, RenderSpec)}
     *
     * @param objToDump object to dump
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles JSON-elements
     * @return JSON-representation of an object in text-format as {@link String}
     */
    public String dumpAsJsonTxt(Object objToDump, RenderSpec renderSpec) {
        return DumpUtils.dumpAsJsonTxt(objToDump, renderSpec);
    }

    /**
     * Transform the string-representation of {@link Instant}
     * in {@link DateTimeFormatter#ISO_INSTANT ISO_INSTANT}-format
     * into {@link DateTimeTriplet}
     *
     * @param instantStr string-representation of {@link Instant} in format like {@code 2007-12-03T10:15:30.00Z}.
     * @return the instance of {@link DateTimeTriplet}
     *
     * @see Instant#parse(CharSequence)
     */
    public DateTimeTriplet dtt(String instantStr) {
        return CoreDateTimeUtils.parseIsoInstant(instantStr);
    }
}
