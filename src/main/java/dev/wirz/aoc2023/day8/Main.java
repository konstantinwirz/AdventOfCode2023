package dev.wirz.aoc2023.day8;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

enum Instruction {
    LEFT, RIGHT
}

public class Main {

    public static void main(String[] args) {
        var network = NetworkParser.parse("day8/part1.txt");

        // PART 1

        int steps = 0;
        var node = Network.START_NODE;
        while (true) {
            var instruction = network.instructions().get(steps % network.instructions().size());
            ++steps;
            if ((node = network.run(instruction, node)).equals(Network.END_NODE)) {
                break;
            }
            System.out.println("NODE = " + node);
        }

        System.out.println("PART 1 | RESULT = " + steps);
    }
}

record TargetNodes(String left, String right) {
}

record Network(List<Instruction> instructions, Map<String, TargetNodes> nodes) {

    static String START_NODE = "AAA";
    static String END_NODE = "ZZZ";

    String run(Instruction instruction, String node) {
        assert nodes.containsKey(node);

        return switch (instruction) {
            case LEFT -> nodes.get(node).left();
            case RIGHT -> nodes.get(node).right();
        };
    }
}

class NetworkParser {

    @SneakyThrows
    static Network parse(String fileName) {
        var resource = Objects.requireNonNull(
                ClassLoader.getSystemClassLoader().getResource(fileName)
        );

        var lines = Files.readAllLines(Path.of(resource.toURI()));

        // first line is the instructions
        var instructions = lines.getFirst().chars()
                .mapToObj(ch -> switch (ch) {
                    case 'L' -> Instruction.LEFT;
                    case 'R' -> Instruction.RIGHT;
                    default -> throw new IllegalArgumentException("unknown instruction: " + ch);
                }).toList();

        var nodes = parseNodes(lines.subList(1, lines.size()));

        return new Network(instructions, nodes);
    }

    private static Map<String, TargetNodes> parseNodes(List<String> lines) {
        var pattern = Pattern.compile("^(\\w+)\\s*=\\s*\\((\\w+),\\s*(\\w+)\\)");
        return lines.stream().filter(Predicate.not(String::isBlank))
                .map(line -> {
                    var matcher = pattern.matcher(line);
                    if (!matcher.find() || matcher.groupCount() != 3) {
                        throw new IllegalArgumentException("invalid line format: " + line);
                    }

                    return new AbstractMap.SimpleEntry<>(matcher.group(1), new TargetNodes(matcher.group(2), matcher.group(3)));
                }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }
}