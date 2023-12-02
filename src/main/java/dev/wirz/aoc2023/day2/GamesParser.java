package dev.wirz.aoc2023.day2;

import dev.wirz.aoc2023.day1.Summer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

public class GamesParser {

    public List<Game> parseFile(String fileName) {
        try (var reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Summer.class.getResourceAsStream(fileName))))) {
            return reader.lines()
                    .map(this::parseGame)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Game parseGame(String line) {
        // parse game id
        var idx = line.indexOf(":");
        return new Game(
                parseGameId(line.substring(0, idx)),
                parseCubes(line.substring(idx + 1))
        );
    }

    private Map<Color, Integer> parseCubes(String line) {
        var pattern = Pattern.compile("\\d+ \\w+");
        var matcher = pattern.matcher(line);
        var cubes = new HashMap<Color, Integer>();
        while (matcher.find()) {
            var parts = matcher.group().split(" ");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid cube: " + matcher.group());
            }
            var color = Color.from(parts[1].strip());
            if (color == Color.UNDEFINED) {
                throw new IllegalArgumentException("Invalid color: " + parts[1]);
            }
            var count = Integer.parseInt(parts[0]);
            if (!cubes.containsKey(color) || cubes.get(color) < count) {
                cubes.put(color, count);
            }
        }

        return cubes;
    }

    private static int parseGameId(String s) {
        var parts = s.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid game id: " + s);
        }

        return Integer.parseInt(parts[1]);
    }

}
