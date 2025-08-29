package org.krmdemo.techlabs.manifest;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Utility-class to work with Java-Manifest files
 * <hr/>
 * <small>it's usually a resource {@code /META-INF/MANIFEST.MF} in JAR-file</small>
 */
public class ManifestUtils {

    public static final String RESOURCE_PATH__MANIFEST = "/META-INF/MANIFEST.MF";

    /**
     * @return Java-Manifest entries as JSON-Object, which corresponds to this class
     */
    public static String dumpManifestAsJson() {
        return dumpManifestAsJson(ManifestUtils.class);
    }

    /**
     * @return Java-Manifest entries as JSON-Object
     */
    public static String dumpManifestAsJson(Class<?> clazz) {
        Manifest manifest = manifestForClass(clazz);
        Attributes manifestMain = manifest.getMainAttributes();
        Map<String, Attributes> manifestEntries = manifest.getEntries();
        return "{}";
    }

    public static Manifest manifestForClass(Class<?> clazz) {
        URL classURL = clazz.getResource(Objects.requireNonNull(clazz).getSimpleName() + ".class");
        System.out.println("classURL = " + classURL);
        if (classURL == null) {
            throw new IllegalArgumentException("could not locate the class " + clazz);
        }
        System.out.println("classURL.protocol = " + classURL.getProtocol());
        System.out.println("classURL.path = " + classURL.getPath());

        URL classLocation = clazz.getProtectionDomain().getCodeSource().getLocation();
        System.out.println("classLocation = " + classLocation);
        if (classLocation == null) {
            throw new IllegalArgumentException("could not get the location of class " + clazz);
        }
        System.out.println("classLocation.path = " + classLocation.getPath());
        System.out.println("classLocation.protocol = " + classLocation.getProtocol());

        try {
            URI classURI = Objects.requireNonNull(classURL).toURI();
            System.out.println("classURI = " + classURI);
            return new Manifest(clazz.getResourceAsStream(RESOURCE_PATH__MANIFEST));
        } catch (IOException | URISyntaxException ioEx) {
            throw new IllegalArgumentException(String.format(
                "could not read the content of resource '%s' for class '%s'",
                RESOURCE_PATH__MANIFEST, clazz), ioEx);
        }
    }
}
