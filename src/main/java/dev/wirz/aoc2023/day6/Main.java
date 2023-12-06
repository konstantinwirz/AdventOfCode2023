package dev.wirz.aoc2023.day6;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Main {


    public static void main(String[] args) {
        var races = RacesParser.parse("day6/part1.txt");

        // PART 1

        var resultPart1 = races.stream().map(race ->
                race.options().stream().filter(opt -> opt > race.distance()).count()
        ).reduce(1L, (a, b) -> a * b);

        assert resultPart1 == 32076L;
        System.out.println("PART 1 | RESULT = " + resultPart1);

        // PART 2

        var race = races.stream()
                .reduce(new Race(0L, 0L), (a, b) ->
                        new Race(glue(a.time(), b.time()), glue(a.distance(), b.distance())));

        var resultPart2 = race.options().stream()
                .filter(opt -> opt > race.distance()).count();

        assert resultPart2 == 34278221L;
        System.out.println("PART 2 | RESULT = " + resultPart2);
    }

    static long glue(long a, long b) {
        return Long.parseLong(a + String.valueOf(b));
    }
}


class RacesParser {

    @SneakyThrows
    static List<Race> parse(String fileName) {
        var resource = Objects.requireNonNull(
                ClassLoader.getSystemClassLoader().getResource(fileName)
        );

        var lines = Files.readAllLines(Path.of(resource.toURI()));
        if (lines.size() != 2) {
            throw new IllegalArgumentException("Invalid file format, expected exactly 2 lines");
        }

        var times = lines.get(0).split("\\s+");
        var distances = lines.get(1).split("\\s+");
        if (times.length != distances.length) {
            throw new IllegalArgumentException("Invalid file format, expected same number of times and distances");
        }

        return IntStream.range(1, times.length)
                .mapToObj(i -> {
                    var time = Long.parseLong(times[i]);
                    var distance = Long.parseLong(distances[i]);
                    return new Race(time, distance);
                }).toList();
    }
}

record Race(long time, long distance) {

    List<Long> options() {
        return LongStream.range(0, time)
                .map(i -> i * (time - i)).boxed()
                .toList();
    }

}