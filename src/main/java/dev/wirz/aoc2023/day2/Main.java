package dev.wirz.aoc2023.day2;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
        var parser = new GamesParser();
        var games = parser.parseFile("/day2/input2.txt");
        var rules = new Rule(
                Map.of(
                        Color.RED, 12,
                        Color.GREEN, 13,
                        Color.BLUE, 14
                )
        );

        var sum = games.stream()
                .filter(game -> !breaksRules(game, rules))
                .mapToInt(Game::id)
                .sum();

        System.out.println("SUM = " + sum);

        var sumOfProducts = games.stream()
                .mapToInt(Main::calculateProduct)
                .sum();

        System.out.println("SUM OF PRODUCTS = " + sumOfProducts);
    }

    static boolean breaksRules(Game game, Rule rule) {
        for (var cube : game.cubes().entrySet()) {
            if (!rule.cubes().containsKey(cube.getKey())) {
                // no rule for this color -> continue
                continue;
            }
            var max = rule.cubes().get(cube.getKey());
            if (cube.getValue() > max) {
                return true;
            }
        }
        return false;
    }

    static int calculateProduct(Game game) {
        return game.cubes().values().stream()
                .mapToInt(i -> i)
                .reduce(1, (a, b) -> a * b);
    }
}
