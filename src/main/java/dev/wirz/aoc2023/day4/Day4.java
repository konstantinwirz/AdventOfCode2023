package dev.wirz.aoc2023.day4;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day4 {

    public static void main(String[] args) {
        var lines = parseFile("day4/input.txt");

        var originalCards = lines.stream().map(Card::parse).toList();

        // PART 1
        var result = originalCards.stream()
                .map(Card::matchingNumbers)
                .map(List::size)
                .filter(sz -> sz > 0)
                .mapToInt(i -> (int) Math.pow(2, i - 1))
                .sum();

        assert result == 20667;
        System.out.println("PART 1 | RESULT = " + result);

        // PART 2
        var scratchCardsCounter = new HashMap<Integer, AtomicInteger>();

        originalCards.forEach(card -> {
            // how many copies do exist?
            int copies = scratchCardsCounter.computeIfAbsent(card.no(), ignored -> new AtomicInteger(0))
                    .incrementAndGet();
            for (int nextCard = card.no() + 1; nextCard <= card.no() + card.matchingNumbers().size(); ++nextCard) {
                scratchCardsCounter.computeIfAbsent(nextCard, ignored -> new AtomicInteger(0))
                        .addAndGet(copies);
            }
        });

        var resultPart2 = scratchCardsCounter.values().stream()
                .mapToInt(AtomicInteger::get)
                .sum();

        assert resultPart2 == 5833065;
        System.out.println("PART 2 | RESULT = " + resultPart2);
    }

    @SneakyThrows
    static List<String> parseFile(String fileName) {
        var resource =
                Objects.requireNonNull(
                        ClassLoader.getSystemClassLoader().getResource(fileName),
                        "file not found: " + fileName
                );

        return Files.readAllLines(Paths.get(resource.toURI()));
    }
}

record Card(int no, List<Integer> winningNumbers, List<Integer> numbers) {

    static Card parse(String line) {
        var parts = line.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("invalid line: " + line);
        }

        var cardNumber = findNumber(parts[0]);
        if (cardNumber.size() != 1) {
            throw new IllegalArgumentException("invalid card number: " + parts[0]);
        }

        parts = parts[1].split("\\|");
        if (parts.length != 2) {
            throw new IllegalArgumentException("invalid line: " + line);
        }
        var winningNumbers = findNumber(parts[0]);
        var numbers = findNumber(parts[1]);

        return new Card(
                cardNumber.get(0),
                winningNumbers,
                numbers
        );
    }

    private static List<Integer> findNumber(String s) {
        var matcher = Pattern.compile("\\d+").matcher(s);
        var result = new ArrayList<Integer>();
        while (matcher.find()) {
            result.add(Integer.parseInt(matcher.group()));
        }
        return result;
    }

    List<Integer> matchingNumbers() {
        return numbers.stream().filter(winningNumbers::contains).toList();
    }

    @Override
    public String toString() {
        return "Card\t%d: %s | %s".formatted(
                no,
                winningNumbers.stream().map(Object::toString).collect(Collectors.joining(" ")),
                numbers.stream().map(Object::toString).collect(Collectors.joining(" "))
        );
    }
}
