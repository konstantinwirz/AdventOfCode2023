package dev.wirz.aoc2023.day6;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class Main {


    public static void main(String[] args) {
        var races = RacesParser.parse("day6/part1.txt");

        var resultPart1 = races.stream().map(race ->
                race.options().stream().filter(opt -> opt > race.distance()).count()
        ).reduce(1L, (a, b) -> a * b);

        System.out.println("PART 1 | RESULT = " + resultPart1);
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

        var races = new ArrayList<Race>();
        for (int i = 1; i < times.length; ++i) {
            var time = Integer.parseInt(times[i]);
            var distance = Integer.parseInt(distances[i]);
            races.add(new Race(time, distance));
        }

        return races;
    }
}

record Race(int time, int distance) {

    List<Integer> options() {
        return IntStream.range(0, time)
                .map(i -> i * (time - i)).boxed()
                .toList();
    }

}