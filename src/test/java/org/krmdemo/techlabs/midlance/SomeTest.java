package org.krmdemo.techlabs.midlance;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;

public class SomeTest {

    // N * log(N)
    Integer smallestValue_BruteForce(int K, int[] values) {
        Arrays.sort(values);
        return values[K-1];

    }

    // K * log(N)
    Integer smallestValue(int K, int[] values) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.reverseOrder());
        for (int v : values) {
            pq.add(v);
            if (pq.size() > K) {
                pq.poll();
            }
            System.out.printf("peek is %d, and all pq is --> %s;%n", pq.peek(), pq);
        }
        return pq.peek();
    }

    @Test
    void testSmallestValue_Simple() {
        assertThat(smallestValue(3, new int[] { 2, 3, 12, 5, 6, 3, 1, 2})).isEqualTo(2);
        System.out.println("----------");
        assertThat(smallestValue(4, new int[] { 2, 3, 12, 5, 6, 3, 1, 2})).isEqualTo(3);
        System.out.println("----------");
        assertThat(smallestValue(7, new int[] { 2, 3, 12, 5, 6, 3, 1, 2})).isEqualTo(6);
    }

    @Test
    void testSmallestValue_BF() {
        assertThat(smallestValue_BruteForce(3, new int[] { 2, 3, 12, 5, 6, 3, 1, 2})).isEqualTo(2);
        System.out.println("----------");
        assertThat(smallestValue_BruteForce(4, new int[] { 2, 3, 12, 5, 6, 3, 1, 2})).isEqualTo(3);
        System.out.println("----------");
        assertThat(smallestValue_BruteForce(7, new int[] { 2, 3, 12, 5, 6, 3, 1, 2})).isEqualTo(6);
    }

    Map<Character, Long> charCountingMap(String str) {
        return str.chars().mapToObj(ch -> (char)ch).collect(groupingBy(identity(), counting()));
    }

    boolean areAnagrams(String strOne, String strTwo) {
        return strOne.length() == strTwo.length() && charCountingMap(strOne).equals(charCountingMap(strTwo));
    }

    @Test
    void testCountingMap() {
        System.out.println(charCountingMap("org.opentest4j.AssertionFailedError: "));
    }

    @Test
    void testAreAnagrams() {
        assertThat(areAnagrams("la-la-la", "sdsdsdds")).isFalse();
        assertThat(areAnagrams("la-la-la", "alal--al")).isTrue();
        assertThat(areAnagrams("la-la-la", "alal--ala")).isFalse();
        assertThat(areAnagrams("la-la-la", "alla--al")).isTrue();
        assertThat(areAnagrams("alla--al", "la-la-la")).isTrue();
    }
}
