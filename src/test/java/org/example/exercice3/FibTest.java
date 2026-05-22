package org.example.exercice3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de la suite de Fibonacci")
class FibTest {

    @Nested
    @DisplayName("Avec un range de 1")
    class Range1 {

        private final List<Integer> result = new Fib(1).getFibSeries();

        @Test
        @DisplayName("Le résultat n'est pas vide")
        void resultatNonVide() {
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("Le résultat correspond à la liste {0}")
        void resultatEgalA0() {
            List<Integer> expected = List.of(0);
            assertEquals(expected, result);
        }
    }

    @Nested
    @DisplayName("Avec un range de 6")
    class Range6 {

        private final List<Integer> result = new Fib(6).getFibSeries();

        @Test
        @DisplayName("Le résultat contient le chiffre 3")
        void contient3() {
            assertTrue(result.contains(3));
        }

        @Test
        @DisplayName("Le résultat contient 6 éléments")
        void contient6Elements() {
            assertEquals(6, result.size());
        }

        @Test
        @DisplayName("Le résultat ne contient pas le chiffre 4")
        void neContientPas4() {
            assertFalse(result.contains(4));
        }

        @Test
        @DisplayName("Le résultat correspond à la liste {0, 1, 1, 2, 3, 5}")
        void resultatCorrect() {
            List<Integer> expected = Arrays.asList(0, 1, 1, 2, 3, 5);
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Le résultat est trié en ordre ascendant")
        void trieAscendant() {
            for (int i = 0; i < result.size() - 1; i++) {
                assertTrue(result.get(i) <= result.get(i + 1));
            }
        }
    }
}