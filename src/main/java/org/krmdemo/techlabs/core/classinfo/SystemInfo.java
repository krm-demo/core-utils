package org.krmdemo.techlabs.core.classinfo;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Path;

/**
 * A wrapper over {@link org.apache.commons.lang3.SystemUtils},
 * that allows to access important system properties via singleton instance
 * <hr/>
 * TODO: complete it
 */
public final class SystemInfo {

    private static final SystemInfo instance = new SystemInfo();

    private final String hostName = SystemUtils.getHostName();

    private final String userName = SystemProperties.getUserName();

    private final Path javaHomePath = SystemUtils.getJavaHomePath();

    private final Path tempDirPath = SystemUtils.getJavaIoTmpDirPath();

    private final Path currentDirPath = SystemUtils.getUserDirPath();

    private final JavaVersion javaVersion =  JavaVersion.valueOf(SystemProperties.getJavaVersion());


    private SystemInfo() {
        if (instance != null) {
            // prohibit the subsequent creation of a singleton instance via reflection
            throw new UnsupportedOperationException("attempt to duplicate the singleton " + getClass().getName());
        }
    }
}
