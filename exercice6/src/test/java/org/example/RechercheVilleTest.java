package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RechercheVilleTest {

    private RechercheVille rechercheVille;

    @BeforeEach
    void setUp() {
        rechercheVille = new RechercheVille(List.of(
                "Paris", "Budapest", "Skopje", "Rotterdam", "Valence",
                "Vancouver", "Amsterdam", "Vienne", "Sydney", "New York",
                "Londres", "Bangkok", "Hong Kong", "Dubaï", "Rome", "Istanbul"
        ));
    }

    @Test
    @DisplayName("Une recherche avec moins de 2 caractères doit lever une NotFoundException")
    void testMoinsDe2CaracteresLeveUneException() {
        assertThrows(NotFoundException.class, () -> rechercheVille.Rechercher("P"));
    }

    @Test
    @DisplayName("Une recherche avec une chaîne vide doit lever une NotFoundException")
    void testChaineVideLeveUneException() {
        assertThrows(NotFoundException.class, () -> rechercheVille.Rechercher(""));
    }

    @Test
    @DisplayName("Une recherche par début de mot doit retourner les villes correspondantes")
    void testRechercheParDebut() throws NotFoundException {
        List<String> result = rechercheVille.Rechercher("Va");
        assertEquals(2, result.size());
        assertTrue(result.contains("Valence"));
        assertTrue(result.contains("Vancouver"));
    }

    @Test
    @DisplayName("La recherche doit être insensible à la casse")
    void testRechercheInsensibleCasse() throws NotFoundException {
        List<String> result = rechercheVille.Rechercher("va");
        assertTrue(result.contains("Valence"));
        assertTrue(result.contains("Vancouver"));
    }

    @Test
    @DisplayName("La recherche doit fonctionner sur une partie du nom de la ville")
    void testRecherchePartieDuNom() throws NotFoundException {
        List<String> result = rechercheVille.Rechercher("ape");
        assertTrue(result.contains("Budapest"));
    }

    @Test
    @DisplayName("Une recherche avec un astérisque doit retourner toutes les villes")
    void testAsterisqueRetourneToutesLesVilles() throws NotFoundException {
        List<String> result = rechercheVille.Rechercher("*");
        assertEquals(16, result.size());
    }
}