package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class DiceScoreTest {

    private Ide deMock;
    private DiceScore diceScore;

    @BeforeEach
    void setUp() {
        deMock = Mockito.mock(Ide.class);
        diceScore = new DiceScore(deMock);
    }

    @Test
    void testDeuxDesIdentiquesRetourneValeurFois2Plus10() {
        when(deMock.getRoll()).thenReturn(4, 4);
        assertEquals(18, diceScore.getScore());
    }

    @Test
    void testDeuxDesSixRetourne30() {
        when(deMock.getRoll()).thenReturn(6, 6);
        assertEquals(30, diceScore.getScore());
    }

    @Test
    void testDesDifferentsRetourneLePlusGrand() {
        when(deMock.getRoll()).thenReturn(3, 5);
        assertEquals(5, diceScore.getScore());
    }

    @Test
    void testDesDifferentsRetourneLePlusGrandInverse() {
        when(deMock.getRoll()).thenReturn(5, 2);
        assertEquals(5, diceScore.getScore());
    }
}