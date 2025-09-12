package org.krmdemo.techlabs.midlance;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Given a string s, you need to find the length of the longest substring that contains no duplicate characters.
 * <hr/>
 * TODO: move to another project as a typical sliding-window puzzle
 * <hr/>
 * <small>This puzzle was given during <b>'Wells Fargo'</b> coding-interview on 9th of September 2025</small>
 */
public class LongestSubStringWithNoDuplicatesTest {

    /**
     * In the 'BRUTE FORCE' approach all substrings are enumerated and checked for duplicates
     * <hr/>
     * it's the most in-efficient - and so far we have:
     * <ul>
     *     <li>the time-complexity <b>{@code O(N^3)}</b></li>
     *     <li>the memory-consumption is linear <b>{@code O(N)}</b></li>
     * </ul>
     *
     * @param str a string to search for longest sub-string
     * @return the length of the longest substring that contains no duplicate characters
     */
    int maxLenOfNuDupSubstr_BF(String str) {
        System.out.printf("--- maxLenOfNuDupSubstr_BF(%s): ---%n", str);
        final int N = str.length();
        if (N <= 1) {
            return N;
        }
        int maxLen = 1;
        for (int left = 0; left < N - 1; left++) {
            for (int right = left + 1; right < N; right++) {
                int len = right - left + 1;
                Set<Character> charsSet = IntStream.range(left, right + 1)
                    .mapToObj(str::charAt)
                    .collect(toSet());
                if (charsSet.size() < len) {
                    continue;
                }
                if (len > maxLen) {
                    maxLen = len;
                    System.out.printf("new maxLen = %d --> [%d, %d] '%s';%n",
                        maxLen, left, right, str.substring(left, right + 1));
                }
            }
        }
        System.out.println("... returning: " + maxLen);
        System.out.println();
        return maxLen;
    }


    /**
     * The optimal solution uses sliding window and keep the set of unique characters across iterations
     * <hr/>
     * the performance is increased considerably, but the memory-consumption remains the same:
     * <ul>
     *     <li>linear time-complexity <b>{@code O(N)}</b></li>
     *     <li>linear memory-consumption <b>{@code O(N)}</b></li>
     * </ul>
     *
     * @return the length of the longest substring that contains no duplicate characters.
     */
    int maxLenOfNuDupSubstr(String str) {
        System.out.printf("--- maxLenOfNuDupSubstr(%s): ---%n", str);
        if (str.length() <= 1) {
            return str.length();
        }
        int maxLen = 1;
        Set<Character> charsSet = new HashSet<>();
        int left = 0;
        for (int right = 0; right < str.length(); right++) {
            char rightChar = str.charAt(right);
            while (charsSet.contains(rightChar) && left < right) {
                char leftChar = str.charAt(left);
                charsSet.remove(leftChar);
                left++;
            }
            charsSet.add(rightChar);
            int len = right - left + 1;
            if (len > maxLen) {
                maxLen = right - left + 1;
                System.out.printf("new maxLen = %d --> [%d, %d] '%s';%n",
                    maxLen, left, right, str.substring(left, right + 1));
            }
            maxLen = Math.max(maxLen, right - left);
        }
        System.out.println("... returning: " + maxLen);
        System.out.println();
        return maxLen;
    }

    @Test
    void testProblem() {
        assertThat(maxLenOfNuDupSubstr("la-la-la")).isEqualTo(3);
        assertThat(maxLenOfNuDupSubstr("la-la-1234567-la")).isEqualTo(10);

        assertThat(maxLenOfNuDupSubstr_BF("la-la-la")).isEqualTo(3);
        assertThat(maxLenOfNuDupSubstr_BF("la-la-1234567-la")).isEqualTo(10);
    }

    @Test
    void testCustomer() {
        assertThat(maxLenOfNuDupSubstr("abcabcbb")).isEqualTo(3);
        assertThat(maxLenOfNuDupSubstr_BF("abcabcbb")).isEqualTo(3);
    }

    final Random rnd = new Random(234);

    @Test
    void testRandom_Len20() {
        for (int i = 0; i < 100; i++) {
            String randomStr = rnd.ints(20, 'a', 'z' + 1)
                .mapToObj(ch -> "" + (char)ch).collect(joining());
            assertThat(maxLenOfNuDupSubstr(randomStr)).isEqualTo(maxLenOfNuDupSubstr_BF(randomStr));
        }
    }

    @Test
    void testRandom_Len120() {
        for (int i = 0; i < 20; i++) {
            String randomStr = rnd.ints(120, 'a', 'z' + 1)
                .mapToObj(ch -> "" + (char)ch).collect(joining());
            assertThat(maxLenOfNuDupSubstr(randomStr)).isEqualTo(maxLenOfNuDupSubstr_BF(randomStr));
        }
    }
}
