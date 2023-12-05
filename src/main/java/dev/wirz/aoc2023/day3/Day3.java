package dev.wirz.aoc2023.day3;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Day3 {

    public static void main(String[] args) {

        var lines = parseFile("day3/part1.txt");
        var engineParser = new EngineParser();
        var engine = engineParser.parse(lines);

        // build a sum
        var sum = engine.symbols().values().stream()
                .flatMap(List::stream)
                .mapToInt(PartNumber::number)
                .sum();

        // find gears
        var gearsSum = engine.symbols().values().stream()
                .filter(partNumbers -> partNumbers.size() >= 2)
                .mapToInt(partNumbers -> partNumbers.stream().mapToInt(PartNumber::number).reduce(1, (a, b) -> a * b))
                .sum();

        assert sum == 553825;
        assert gearsSum == 93994191;

        System.out.println("SUM = " + sum);
        System.out.println("GEARS SUM = " + gearsSum);
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

class EngineParser {

    Engine parse(List<String> lines) {
        var symbolsAndPartNumbers = parseSymbolsAndPartNumbers(lines);
        var symbols = symbolsAndPartNumbers.symbols();
        var partNumbers = symbolsAndPartNumbers.partNumbers();
        Map<Symbol, List<PartNumber>> result = new HashMap<>(symbols.size());

        for (var symbol : symbols) {
            // find part numbers which touches that symbol
            var adjacentPartNumbers = partNumbers.stream()
                    .filter(partNumber -> partNumber.touches(symbol))
                    .toList();

            result.put(symbol, adjacentPartNumbers);
        }

        return new Engine(result);
    }

    private SymbolsAndPartNumbers parseSymbolsAndPartNumbers(List<String> lines) {
        var symbols = new ArrayList<Symbol>();
        var partNumbers = new ArrayList<PartNumber>();

        for (int r = 0; r < lines.size(); ++r) {
            var line = lines.get(r);
            var partNumberBuilder = new StringBuilder();
            for (int c = 0; c < line.length(); ++c) {
                var ch = line.charAt(c);

                if (Character.isDigit(ch)) {
                    partNumberBuilder.append(ch);
                    // perhaps last digit
                    if (c == line.length() - 1) {
                        partNumbers.add(cretePartNumber(partNumberBuilder.toString(), r, c));
                        partNumberBuilder = new StringBuilder();
                    }
                } else {
                    // no digit obviously, but perhaps we have a number in the buffer
                    if (!partNumberBuilder.isEmpty()) {
                        partNumbers.add(cretePartNumber(partNumberBuilder.toString(), r, c));
                        partNumberBuilder = new StringBuilder();
                    }

                    if (ch != '.') {
                        symbols.add(new Symbol(ch, new Coordinate(r, c)));
                    }
                }
            }
        }

        return new SymbolsAndPartNumbers(symbols, partNumbers);
    }

    private PartNumber cretePartNumber(String partNumber, int row, int column) {
        return new PartNumber(
                Integer.parseInt(partNumber),
                new Coordinate(row, column - partNumber.length()),
                new Coordinate(row, column - 1)
        );
    }
}

record SymbolsAndPartNumbers(List<Symbol> symbols, List<PartNumber> partNumbers) {
}

record Coordinate(int row, int column) {
}

record Symbol(char symbol, Coordinate coordinate) {

    @Override
    public String toString() {
        return "%c [%d %d]".formatted(symbol, coordinate.row(), coordinate.column());
    }
}

record PartNumber(int number, Coordinate begin, Coordinate end) {

    boolean touches(Symbol symbol) {
        return symbol.coordinate().column() >= (begin.column() - 1) &&
                symbol.coordinate().column() <= (end.column() + 1) &&
                symbol.coordinate().row() >= (begin.row() - 1) &&
                symbol.coordinate().row() <= (begin.row() + 1);
    }

    @Override
    public String toString() {
        return "%d [%d %d] [%d %d]".formatted(number, begin.row(), begin.column(), end.row(), end.column());
    }
}

record Engine(Map<Symbol, List<PartNumber>> symbols) {
}