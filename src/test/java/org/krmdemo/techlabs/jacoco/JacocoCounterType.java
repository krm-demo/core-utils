package org.krmdemo.techlabs.jacoco;

/**
 * Type of JaCoCo-counters
 *
 * @see <a href="https://www.eclemma.org/jacoco/trunk/doc/counters.html">
 *     Coverage Counters
 * </a>
 */
public enum JacocoCounterType {

    /**
     * The smallest unit JaCoCo counts are single Java byte code instructions.
     * Instruction coverage provides information about the amount of code that has been executed or missed.
     * This metric is completely independent of source formatting and always available,
     * even in absence of debug information in the class files.
     */
    INSTRUCTION,

    /**
     * JaCoCo also calculates branch coverage for all if and switch statements.
     * This metric counts the total number of such branches in a method
     * and determines the number of executed or missed branches.
     * Branch coverage is always available, even in absence of debug information in the class files.
     * Note that exception handling is not considered as branches in the context of this counter definition.
     * <hr/>
     * If the class files haven been compiled with debug information decision points
     * can be mapped to source lines and highlighted accordingly:
     * <dl>
     *     <dt><b>No coverage</b>:</dt>
     *     <dd>No branches in the line has been executed (red diamond)</dd>
     *     <dt><b>Partial coverage</b>:</dt>
     *     <dd>Only a part of the branches in the line have been executed (yellow diamond)</dd>
     *     <dt><b>Full coverage</b>:</dt>
     *     <dd>All branches in the line have been executed (green diamond)</dd>
     * </dl>
     */
    BRANCH,

    /**
     * For all class files that have been compiled with debug information,
     * coverage information for individual lines can be calculated.
     * A source line is considered executed when at least one instruction that is assigned to this line has been executed.
     * <hr/>
     * Due to the fact that a single line typically compiles to multiple byte code instructions
     * the source code highlighting shows three different status for each line containing source code:
     * <dl>
     *     <dt><b>No coverage</b>:</dt>
     *     <dd>No instruction in the line has been executed (red background)</dd>
     *     <dt><b>Partial coverage</b>:</dt>
     *     <dd>Only a part of the instruction in the line have been executed (yellow background)</dd>
     *     <dt><b>Full coverage</b>:</dt>
     *     <dd>All instructions in the line have been executed (green background)</dd>
     * </dl>
     * Depending on source formatting a single line of a source code may refer to multiple methods or multiple classes.
     * Therefore, the line count of methods cannot be simply added to obtain the total number for the containing class.
     * The same holds true for the lines of multiple classes within a single source file.
     * <b>JaCoCo</b> calculates line coverage for classes and source file based on the actual source lines covered.
     */
    LINE,

    /**
     * <b>JaCoCo</b> also calculates cyclomatic complexity for each non-abstract method and summarizes complexity for classes, packages and groups.
     * According to its definition by <i>McCabe-1996</i> cyclomatic complexity is the minimum number of paths that can,
     * in (linear) combination, generate all possible paths through a method.
     * Thus, the complexity value can serve as an indication for the number of unit test cases to fully cover a certain piece of software.
     * Complexity figures can always be calculated, even in absence of debug information in the class files.
     * <hr/>
     * The formal definition of the cyclomatic complexity {@code v(G)} is based on the representation
     * of a method's control flow graph as a directed graph:
     * <pre>{@code v(G) = E - N + 2}</pre>
     * Where {@code E} is the number of edges and {@code N} the number of nodes. <b>JaCoCo</b> calculates cyclomatic complexity
     * of a method with the following equivalent equation based on the number of branches {@code (B)} and the number of decision points {@code (D)}:
     * <pre>{@code v(G) = B - D + 1}</pre>
     * Based on the coverage status of each branch JaCoCo also calculates covered and missed complexity for each method.
     * Missed complexity again is an indication for the number of test cases missing to fully cover a module.
     * Note that as <b>JaCoCo</b> does not consider exception handling as branches {@code try}/{@code catch} blocks will also not increase complexity.
     */
    COMPLEXITY,

    /**
     * Each non-abstract method contains at least one instruction.
     * A method is considered as executed when at least one instruction has been executed.
     * As JaCoCo works on byte code level also constructors and static initializers are counted as methods.
     * Some of these methods may not have a direct correspondence in Java source code,
     * like implicit and thus generated default constructors or initializers for constants.
     */
    METHOD,

    /**
     * A class is considered as executed when at least one of its methods has been executed.
     * Note that JaCoCo considers constructors as well as static initializers as methods.
     * As Java interface types may contain static initializers such interfaces are also considered as executable classes.
     */
    CLASS;

    /**
     * @param typeObj object of type {@link String} or {@link Integer}
     * @return the instance of this enumeration
     */
    public static JacocoCounterType valueOf(Object typeObj) {
        return switch(typeObj) {
            case String typeStr -> JacocoCounterType.valueOf(typeStr);
            case Integer typeOrdinal -> JacocoCounterType.values()[typeOrdinal];
            case null -> throw new IllegalArgumentException("typeObj is null");
            default -> throw new IllegalArgumentException("unsupported type - " + typeObj.getClass());
        };
    }
}
