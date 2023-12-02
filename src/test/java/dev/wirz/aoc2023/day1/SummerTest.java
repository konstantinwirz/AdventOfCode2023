package dev.wirz.aoc2023.day1;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SummerTest {

    static Stream<Arguments> sumProvider() {
        return Stream.of(
                Arguments.of("/day1/input1.txt", 142),
                Arguments.of("/day1/input2.txt", 281),
                Arguments.of("/day1/input3.txt", 54094)
        );
    }

    @ParameterizedTest
    @MethodSource("sumProvider")
    void testSum(String fileName, int expected) {
        assertEquals(expected, Summer.sumFromFile(fileName));
    }


}