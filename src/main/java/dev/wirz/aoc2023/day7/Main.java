package dev.wirz.aoc2023.day7;

import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
enum Card {
    _2('2'),
    _3('3'),
    _4('4'),
    _5('5'),
    _6('6'),
    _7('7'),
    _8('8'),
    _9('9'),
    _10('T'),
    J('J'),
    Q('Q'),
    K('K'),
    A('A');

    private final char ch;

    Card(char ch) {
        this.ch = ch;
    }

    static Card fromChar(char ch) {
        return Arrays.stream(values())
                .filter(card -> card.ch == ch)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown card character: " + ch));
    }
}

record HandAndBid(Card[] hand, long bid) {

    @Override
    public String toString() {
        var cards = Arrays.stream(hand())
                .map(card -> String.valueOf(card.getCh()))
                .collect(Collectors.joining(" "));

        return "[%s] | %d".formatted(cards, bid());
    }
}

class CardsComparator implements Comparator<Card[]> {

    static Map<Card, Long> groupedCards(Card[] cards) {
        return Arrays.stream(cards)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private static int compare(Map<Card, Long> groupedCards1, Map<Card, Long> groupedCards2) {
        // five of a kind
        // A - 5                         | size = 1
        // four of a kind
        // A - 4 B - 1                   | size = 2
        // full house
        // A - 3 B - 2                   | size = 2
        // three of a kind
        // A - 3 B - 1 C - 1             | size = 3
        // two pair
        // A - 2 B - 2 C - 1             | size = 3
        // one pair
        // A - 2 B - 1 C - 1 D - 1       | size = 4
        // all different
        // A - 1 B - 1 C - 1 D - 1 E - 1 | size = 5

        var diff = groupedCards2.size() - groupedCards1.size();
        if (diff != 0) {
            return diff;
        }

        // sizes are the same, so we have to filter out
        // 'four of a kind', 'full house', 'three of a kind' and 'two pair'
        if (groupedCards1.size() == 2) {
            assert groupedCards2.size() == 2;

            // both 'four of a kind' or 'full house'
            if ((groupedCards1.containsValue(4L) && groupedCards2.containsValue(4L)) ||
                    (groupedCards1.containsValue(3L) && groupedCards2.containsValue(3L))) {
                return 0;
            }

            if (groupedCards1.containsValue(4L) && groupedCards2.containsValue(3L)) {
                return 1;
            }

            return -1;
        }

        if (groupedCards1.size() == 3) {
            assert groupedCards2.size() == 3;

            // 'three of a kind' or 'two pair'
            if ((groupedCards1.containsValue(3L) && groupedCards2.containsValue(3L)) ||
                    (groupedCards1.containsValue(2L) && groupedCards2.containsValue(2L))) {
                return 0;
            }

            if (groupedCards1.containsValue(3L) && groupedCards2.containsValue(2L)) {
                return 1;
            }

            return -1;
        }

        return 0;
    }

    @Override
    public int compare(Card[] cards1, Card[] cards2) {
        var diff = compare(groupedCards(cards1), groupedCards(cards2));

        if (diff != 0) {
            return diff;
        }

        // compare in a classical way
        for (int i = 0; i < 5; ++i) {
            if (cards1[i].ordinal() > cards2[i].ordinal()) {
                return 1;
            }

            if (cards1[i].ordinal() < cards2[i].ordinal()) {
                return -1;
            }
        }

        return 0;
    }


}

class CardsParser {

    @SneakyThrows
    public static List<HandAndBid> parseCards(String fileName) {
        var resource = Objects.requireNonNull(
                ClassLoader.getSystemClassLoader().getResource(fileName)
        );

        return Files.readAllLines(Path.of(resource.toURI()))
                .stream()
                .map(CardsParser::parserLine)
                .toList();
    }

    private static HandAndBid parserLine(String line) {
        var parts = line.split("\\s+");
        if (parts.length != 2) {
            throw new IllegalArgumentException("invalid line format: " + line);
        }
        var cards = parts[0].chars().mapToObj(ch -> Card.fromChar((char) ch)).toList();
        if (cards.size() != 5) {
            throw new IllegalArgumentException("expected 5 cards, got: " + cards.size());
        }

        var bid = Integer.parseInt(parts[1].strip());
        return new HandAndBid(cards.toArray(Card[]::new), bid);
    }
}

public class Main {

    private static final Comparator<Card[]> COMPARATOR = new CardsComparator();

    public static void main(String[] args) {

        var handAndBids = CardsParser.parseCards("day7/part1.txt");

        // PART 1
        var rankedBids = handAndBids.stream()
                .sorted((handAndBid1, handAndBid2) ->
                        COMPARATOR.compare(handAndBid1.hand(), handAndBid2.hand()))
                .toList();

        var resultPart1 = IntStream.range(0, rankedBids.size())
                .mapToLong(i -> {
                    var rank = i + 1;
                    var bid = rankedBids.get(i).bid();
                    return rank * bid;
                }).sum();

        assert resultPart1 == 246424613L;
        System.out.println("PART 1 | RESULT = " + resultPart1);
    }
}