package dev.wirz.aoc2023.day9;


import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {

        var history = ValuesParser.parse("day9/part1.txt");

        // PART 1
        var resultPart1 = history.stream().mapToInt(values -> {
            var current = values;
            while (!current.solved()) {
                current = current.predictNext();
            }
            return current.solution().getLast();
        }).sum();

        assert resultPart1 == 1834108701;
        System.out.println("PART 1 | RESULT = " + resultPart1);
    }
}

class ValuesParser {

    @SneakyThrows
    static List<Values> parse(String fileName) {
        var resource = Objects.requireNonNull(
                ClassLoader.getSystemClassLoader().getResource(fileName)
        );

        return Files.readAllLines(Path.of(resource.toURI()))
                .stream().map(ValuesParser::parseLine)
                .toList();
    }

    static private Values parseLine(String line) {
        return new Values(Arrays.stream(line.split("\\s+"))
                .map(Integer::parseInt)
                .toList()
        );
    }
}

record Values(List<Integer> sequence, Values previous) {

    Values(List<Integer> sequence) {
        this(sequence, null);
    }

    Values predictNext() {
        if (solved()) {
            throw new IllegalStateException("not able to predict, it's already solved");
        }

        assert sequence.size() > 1;

        return new Values(calculateNext(), this);
    }

    List<Integer> solution() {
        assert solved();

        Values current = this;
        Integer previousValue = 0;
        List<Integer> result = new ArrayList<>();
        while (current != null) {
            var value = current.sequence.getLast() + previousValue;
            previousValue = value;
            result.add(value);
            current = current.previous;
        }

        return result;
    }

    boolean solved() {
        return sequence.stream().allMatch(n -> n == 0);
    }

    private List<Integer> calculateNext() {
        return IntStream.range(0, (sequence.size() - 1))
                .map(i -> sequence.get(i + 1) - sequence.get(i))
                .boxed()
                .toList();
    }
}