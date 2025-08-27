///usr/bin/env jbang  --quiet "$0" "$@" ; exit $?
//SOURCES JBangUtils.java

public class DumpEnvVars {

    public static void main(String... args) {
        System.out.println(JBangUtils.dumpEnvVarsExAsJson());
    }

    public boolean isIterable(Object someObject) {
        return someObject instanceof Iterable;
    }
}