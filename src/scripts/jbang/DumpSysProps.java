///usr/bin/env jbang  --quiet "$0" "$@" ; exit $?
//SOURCES JBangUtils.java

public class DumpSysProps {

    /**
     * JBang and JVM entry-point
     * @param args command-line arguments that are unused in this JBang-script
     */
    public static void main(String... args) {
        System.out.println(JBangUtils.dumpSysPropsAsJson());
    }
}