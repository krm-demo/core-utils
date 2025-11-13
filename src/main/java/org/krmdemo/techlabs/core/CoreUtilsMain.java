package org.krmdemo.techlabs.core;

import org.krmdemo.techlabs.core.buildinfo.BuildInfo;
import org.krmdemo.techlabs.core.buildinfo.CoreUtilsBuildInfo;

/**
 * TODO: provide the comprehensive Java-Doc !!!
 */
public class CoreUtilsMain {

    /**
     * JVM entry-point
     *
     * @param args unused command-line arguments
     */
    public static void main(String... args) {
        System.out.println("This is a Main-class of 'core-utils' library (just a test message here)");
        BuildInfo buildInfo = CoreUtilsBuildInfo.getInstance();
        System.out.println(buildInfo);
    }
}
