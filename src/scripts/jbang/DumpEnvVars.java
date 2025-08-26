///usr/bin/env jbang  --quiet "$0" "$@" ; exit $?
//SOURCES JBangUtils.java

public class DumpEnvVars {

    public static void main(String... args) {
        System.out.printf("Hello from %s !!!%n", DumpEnvVars.class.getSimpleName());

        JBangUtils.dumpEnvVarsEx().forEach( (envName, envValue) -> {
            System.out.printf("  \"%s\": \"%s\");
        })
    }

    public boolean isIterable(Object someObject) {
        return someObject instanceof Iterable;
    }
}