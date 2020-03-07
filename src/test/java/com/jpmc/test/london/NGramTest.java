package com.jpmc.test.london;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;


class NGramTest {

    @Test
    void shouldFindNGrams() {
        var nGrams = NGram.findNGrams(2, "ONE", "ONE TWO THREE ONE FOUR");

        assertThat(nGrams).containsExactly("TWO", "FOUR");
    }

    @Test
    void willNotMatchIncompleteNGrams() {
        var nGrams = NGram.findNGrams(3, "ONE", "ONE TWO THREE ONE FOUR");

        assertThat(nGrams).doesNotContain("FOUR");
        assertThat(nGrams).containsExactly("TWO THREE");
    }

    @Test
    void shouldFindDuplicateNGrams() {
        var nGrams = NGram.findNGrams(2, "ONE","ONE TWO ONE TWO THREE TWO THREE");

        assertThat(nGrams).containsExactly("TWO", "TWO");
    }

    @Test
    void shouldComputeSortedPredictions() {
        var words = List.of("ONE", "ONE", "THREE");
        var predictions = NGram.sortedPredictions(words);

        assertThat(predictions).containsExactly(entry("ONE", 2d/3), entry("THREE", 1d/3));
    }

    @Test
    void shouldSortPredictionsByScoreAndThenAlphabetically() {
        var words = List.of("lamb", "rule", "children", "teacher", "lamb", "eager", "lamb", "teacher");
        var predictions = NGram.sortedPredictions(words);

        assertThat(predictions).containsExactly(
                entry("lamb", 0.375d),
                entry("teacher", 0.250d),
                entry("children", 0.125d),
                entry("eager", 0.125d),
                entry("rule", 0.125d)
        );
    }

    @Test
    void shouldFormatPredictions() {
        var predictions = new LinkedHashMap<String, Double>();
        predictions.put("lamb", 0.375d);
        predictions.put("teacher", 0.250d);
        predictions.put("children", 0.125d);
        predictions.put("eager", 0.125d);
        predictions.put("rule", 0.125d);

        var formattedPredictions = NGram.formatPredictions(predictions);

        assertThat(formattedPredictions).isEqualTo("lamb,0.375;teacher,0.250;children,0.125;eager,0.125;rule,0.125");
    }

    @Test
    void formattedScoreShouldAlwaysHaveThreeDecimalPlaces() {
        var predictions = new LinkedHashMap<String, Double>();
        predictions.put("x", 7d/15); // 0.467
        predictions.put("y", 1d/3); // 0.333
        predictions.put("z", 0.2d); // 0.200

        var formattedPredictions = NGram.formatPredictions(predictions);

        assertThat(formattedPredictions).isEqualTo("x,0.467;y,0.333;z,0.200");
    }
}
