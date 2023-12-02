package dev.wirz.aoc2023.day1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Summer {

    private static final Map<String, String> REPLACEMENTS = Map.of(
            "one", "o1e",
            "two", "t2o",
            "three", "t3e",
            "four", "f4r",
            "five", "f5e",
            "six", "s6x",
            "seven", "s7n",
            "eight", "e8t",
            "nine", "n9e"
    );

    public static int sumFromFile(String fileName) {
        try (var reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Summer.class.getResourceAsStream(fileName))))) {
            return reader.lines()
                    .map(Summer::replace)
                    .mapToInt(line -> {
                        var firstDigit = findFirstDigit(line);
                        var lastDigit = findFirstDigit(reverse(line));
                        return firstDigit * 10 + lastDigit;
                    }).sum();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static String replace(String line) {
        String previousLine;
        do {
            previousLine = line;
            line = replaceOne(line);
        }
        while (!Objects.equals(previousLine, line));

        return line;
    }

    private static String replaceOne(String line) {
        // find first digit as word -> one with the smallest index
        Map<Integer, String> words = new HashMap<>();
        for (var word : REPLACEMENTS.keySet()) {
            var idx = line.indexOf(word);
            if (idx >= 0) {
                words.put(idx, word);
            }
        }

        return words.entrySet().stream().min(Map.Entry.comparingByKey())
                .map(entry ->
                        line.replace(entry.getValue(), REPLACEMENTS.get(entry.getValue()).toString())
                ).orElse(line);
    }

    private static String reverse(String line) {
        return new StringBuilder(line).reverse().toString();
    }

    private static int findFirstDigit(String line) {
        for (var ch : line.toCharArray()) {
            if (Character.isDigit(ch)) {
                return ch - '0';
            }
        }

        throw new IllegalArgumentException("No digit found in line");
    }
}
