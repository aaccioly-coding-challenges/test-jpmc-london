package com.jpmc.test.london;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class NGram {

    public static final String TEXT_TO_ANALYZE = "Mary had a little lamb its fleece was white as snow;\n" +
            "And everywhere that Mary went, the lamb was sure to go.\n" +
            "It followed her to school one day, which was against the rule;\n" +
            "It made the children laugh and play, to see a lamb at school.\n" +
            "And so the teacher turned it out, but still it lingered near,\n" +
            "And waited patiently about till Mary did appear.\n" +
            "\"Why does the lamb love Mary so?\" the eager children cry;\"Why, Mary loves the lamb, you know\" the teacher did reply.\"";

    public static void main(String[] args) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
             BufferedReader in = new BufferedReader(reader)) {
            String line;
            while ((line = in.readLine()) != null) {
                var inputArguments = line.split(",", 2);
                var n = Integer.parseInt(inputArguments[0]);
                var word = inputArguments[1];

                var ngrams = findNGrams(n, word, TEXT_TO_ANALYZE);
                var predictions = sortedPredictions(ngrams);
                System.out.println(formatPredictions(predictions));
            }
        }
    }


    private static String stripOffNonAlphaNumericCharacters(final String text) {
        return text.replaceAll("[^\\p{L}]", " ");
    }

    private static String[] tokenizeWordsInText(final String text) {
        return stripOffNonAlphaNumericCharacters(text).split("\\s+");
    }

    public static List<String> findNGrams(final int ngrams, final String wordToFind, final String textToAnalyze) {
        var wordsInText = tokenizeWordsInText(textToAnalyze);
        var nGrams = new ArrayList<String>();
        for (int i = 0; i <= wordsInText.length - ngrams; i++) {
            var currentWord = wordsInText[i];
            if (currentWord.equals(wordToFind)) {
                var ngramStartingHere = String.join(" ", Arrays.copyOfRange(wordsInText, i + 1, i + ngrams));
                nGrams.add(ngramStartingHere);
            }
        }

        return nGrams;
    }

    private static Map<String, Long> countWords(final List<String> words) {
        return words.stream().collect(groupingBy(Function.identity(), counting()));
    }

    public static Map<String, Double> sortedPredictions(final List<String> words) {
        var wordCount = countWords(words);

        var comparatorByValueAndThenByKey = Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                .thenComparing(Map.Entry.comparingByKey());

        return wordCount.entrySet().stream()
                .sorted(comparatorByValueAndThenByKey)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().doubleValue() / words.size(),
                        (v1, v2) -> {
                            throw new UnsupportedOperationException("Merging isn't supported");
                        },
                        LinkedHashMap::new
                ));
    }

    public static String formatPredictions(final Map<String, Double> predictions) {
        return predictions.entrySet().stream()
                .map(entry -> String.format("%s,%.03f", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(";"));
    }
}
