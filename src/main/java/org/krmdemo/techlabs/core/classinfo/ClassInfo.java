package org.krmdemo.techlabs.core.classinfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.JarFile;

import static org.krmdemo.techlabs.core.dump.DumpUtils.dumpAsJsonTxt;

@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassInfo(
    @JsonIgnore Class<?> clazz
) {
    public ClassInfo {
        Objects.requireNonNull(clazz);
    }
    Module module() {
        return clazz.getModule();
    }
    Optional<ClassLoader> classLoader() {
        return Optional.ofNullable(clazz.getClassLoader());
    }


    public String getClassName() {
        return clazz.getName();
    }
    public String getSimpleName() {
        return clazz.getSimpleName();
    }
    public URL getClassLocation() {
        return clazz.getResource(getSimpleName() + ".class");
    }
    public String getClassLoaderName() {
        return classLoader().map(ClassLoader::getName).orElse("<< bootstrap >>");
    }
    public String getModuleName() {
        return module().isNamed() ? module().getName() : "<< ALL-UNNAMED >>";
    }


    public JarFileInfo getJarFileInfo() {
        try {
            URL classLocation = clazz.getProtectionDomain().getCodeSource().getLocation();
            Path jarPath = Paths.get(classLocation.toURI());
            return new JarFileInfo(new JarFile(jarPath.toFile()));
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String toString() {
        return dumpAsJsonTxt(this);
    }
}
