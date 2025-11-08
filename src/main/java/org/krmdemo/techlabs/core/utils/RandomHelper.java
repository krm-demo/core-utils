package org.krmdemo.techlabs.core.utils;

import java.io.Serial;
import java.util.Random;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static org.apache.commons.lang3.ArrayUtils.swap;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.reversedArr;

/**
 * Helper-wrapper over standard {@link Random} to generate random arrays and strings
 */
public class RandomHelper extends Random {

    @Serial
    private static final long serialVersionUID = 123456789_002L;

    /**
     * Constructs an <b>un-reproducible</b> instance of {@link RandomHelper}
     */
    public RandomHelper() {
        super(); // non-reproducible random sequences !!!
    }

    /**
     * Constructs a <b>reproducible</b> instance of {@link RandomHelper}
     */
    public RandomHelper(long seed) {
        super(seed);
    }

    /**
     * Getting a random lowercased string
     *
     * @param len the length of string to returns
     * @return a random string of lower-cased letters
     */
    public String randomLowerCase(int len) {
        return ints(len, 'a', 'z' + 1)
            .mapToObj(chInt -> (char)chInt)
            .map(String::valueOf)
            .collect(joining());
    }

    /**
     * Getting a random uppercased string
     *
     * @param len the length of string to returns
     * @return a random string of upper-cased letters
     */
    public String randomUpperCase(int len) {
        return ints(len, 'A', 'Z' + 1)
            .mapToObj(chInt -> (char)chInt)
            .map(String::valueOf)
            .collect(joining());
    }

    /**
     * @param lowBound lower bound of the range (inclusive)
     * @param highBound higher bound of the range (exclusive)
     * @return the array of elements in range {@code [ lowBound ; highBound )} that was shuffled
     */
    public int[] randomRangeArr(int lowBound, int highBound) {
        return shuffle(range(lowBound, highBound));
    }

    /**
     * @param lowBound lower bound of the range (inclusive)
     * @param highBound higher bound of the range (inclusive)
     * @return the array of elements in range {@code [ lowBound ; highBound ]} that was shuffled
     */
    public int[] randomRangeClosedArr(int lowBound, int highBound) {
        return shuffle(rangeClosed(lowBound, highBound));
    }

    /**
     * Collects the passed stream of {@code int}s into an array and shuffle it with {@link #shuffleArr(int[])}
     *
     * @param values stream of primitive {@code int}s
     * @return an array of inpiut elements that are shuffled
     */
    public int[] shuffle(IntStream values) {
        int[] valuesArr = values.toArray();
        shuffleArr(valuesArr);
        return valuesArr;
    }

    /**
     * Swap elements of the passed array in random way.
     * The number of swaps equals to the length of array.
     *
     * @param valuesArr an array to shuffle
     */
    public void shuffleArr(int[] valuesArr) {
        // a little bit more efficient than using Collections.shuffle and subsequence copying
        for (int i = valuesArr.length; i > 1; i--) {
            swap(valuesArr, i - 1, nextInt(valuesArr.length));
        }
    }

    /**
     * Getting an array of eventually {@code N} <b>distinct</b> integers in the range {@code [ lowBound, int highBound )}.
     *
     * @param N the size of array to return (in a very rare cases the actual case could be less)
     * @param lowBound low boundary of random range (inclusive)
     * @param highBound high boundary of random range (exclusive)
     * @return an array of eventually {@code N} <b>distinct</b> integers in the range {@code [ lowBound, int highBound )}.
     */
    public int[] randomDistinctIntArr(long N, int lowBound, int highBound) {
        return ints(5 * N, lowBound, highBound)
            .distinct().limit(N).toArray();
    }

    /**
     * Getting an array of eventually {@code N} integers in the range {@code [ lowBound, int highBound )},
     * which are <b>sorted</b> in <b>ascending</b> order, but <b>not distinct</b>.
     *
     * @param N the size of array to return (in a very rare cases the actual case could be less)
     * @param lowBound low boundary of random range (inclusive)
     * @param highBound high boundary of random range (exclusive)
     * @return an array of eventually {@code N} <b>sorted</b> integers in the range {@code [ lowBound, int highBound )}.
     */
    public int[] randomSortedIntArr(long N, int lowBound, int highBound) {
        return ints(N, lowBound, highBound)
            .sorted().limit(N).toArray();
    }

    /**
     * Getting an array of eventually {@code N} integers in the range {@code [ lowBound, int highBound )},
     * which are <b>sorted</b> in <b>descending</b> order, but <b>not distinct</b>.
     *
     * @param N the size of array to return (in a very rare cases the actual case could be less)
     * @param lowBound low boundary of random range (inclusive)
     * @param highBound high boundary of random range (exclusive)
     * @return an array of eventually {@code N} <b>sorted</b> integers in the range {@code [ lowBound, int highBound )}.
     */
    public int[] randomSortedReversedIntArr(long N, int lowBound, int highBound) {
        return reversedArr(randomSortedIntArr(N, lowBound, highBound));
    }

    /**
     * Getting an array of eventually {@code N} <b>distinct</b> integers in the range {@code [ lowBound, int highBound )},
     * which are <b>sorted</b> in <b>ascending</b> order. The bounds and the size are recommended to be large enough.
     *
     * @param N the size of array to return (for small ranges the returning size could be less)
     * @param lowBound low boundary of random range (inclusive)
     * @param highBound high boundary of random range (exclusive)
     * @return an array of eventually {@code N} <b>sorted</b> and <b>distinct</b> integers in the range {@code [ lowBound, int highBound )}.
     */
    public int[] randomIncreasingIntArr(long N, int lowBound, int highBound) {
        return ints(5 * N, lowBound, highBound)
            .distinct().limit(N).sorted().toArray();
    }

    /**
     * Getting an array of eventually {@code N} <b>distinct</b> integers in the range {@code [ lowBound, int highBound )},
     * which are <b>sorted</b> in <b>descending</b> order. The bounds and the size are recommended to be large enough.
     *
     * @param N the size of array to return (for small ranges the returning size could be less)
     * @param lowBound low boundary of random range (inclusive)
     * @param highBound high boundary of random range (exclusive)
     * @return an array of eventually {@code N} <b>sorted</b> and <b>distinct</b> integers in the range {@code [ lowBound, int highBound )}.
     */
    public int[] randomDecreasingIntArr(long N, int lowBound, int highBound) {
        return reversedArr(randomIncreasingIntArr(N, lowBound, highBound));
    }

    /**
     * Getting an array of size {@code N} with zero({@code 0})-elements
     *
     * @param N the size of array to return
     * @return an array of {@code N} zero({@code 0})-elements
     */
    public static int[] zeroIntArr(long N) {
        return constantIntArr(N, 0);
    }

    /**
     * Getting an array of size {@code N} with all elements equal to {@code constant}
     *
     * @param N the size of array to return
     * @return an array of size {@code N} with all elements equal to {@code constant}
     */
    public static int[] constantIntArr(long N, int constant) {
        return IntStream.generate(() -> constant).limit(N).toArray();
    }
}
