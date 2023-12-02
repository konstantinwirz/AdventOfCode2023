package dev.wirz.aoc2023.day2;

public enum Color {
    UNDEFINED,
    BLUE,
    RED,
    GREEN;

    public static Color from(String color) {
        return switch (color) {
            case "blue" -> BLUE;
            case "red" -> RED;
            case "green" -> GREEN;
            default -> UNDEFINED;
        };
    }
}
