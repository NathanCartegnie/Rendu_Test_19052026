package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FrameTest {
    private IGenerateur mockGenerateur;
    private Frame standardFrame;
    private Frame lastFrame;

    @BeforeEach
    void setUp() {
        mockGenerateur = mock(IGenerateur.class);
        standardFrame = new Frame(mockGenerateur, false);
        lastFrame = new Frame(mockGenerateur, true);
    }

    @Test
    @DisplayName("should increase score when first roll is made in standard frame")
    void shouldIncreaseScoreWhenFirstRollIsMadeInStandardFrame() {
        when(mockGenerateur.randomPin(10)).thenReturn(5);
        assertTrue(standardFrame.makeRoll());
        assertEquals(5, standardFrame.getScore());
        assertEquals(1, standardFrame.getRolls().size());
    }

    @Test
    @DisplayName("should increase score when second roll is made in standard frame")
    void shouldIncreaseScoreWhenSecondRollIsMadeInStandardFrame() {
        when(mockGenerateur.randomPin(10)).thenReturn(3).thenReturn(4);
        assertTrue(standardFrame.makeRoll());
        assertTrue(standardFrame.makeRoll());
        assertEquals(7, standardFrame.getScore());
        assertEquals(2, standardFrame.getRolls().size());
    }

    @Test
    @DisplayName("should reject second roll when standard frame starts with strike")
    void shouldRejectSecondRollWhenStandardFrameStartsWithStrike() {
        when(mockGenerateur.randomPin(10)).thenReturn(10).thenReturn(5);
        assertTrue(standardFrame.makeRoll());
        assertTrue(standardFrame.isStrike());
        assertFalse(standardFrame.makeRoll());
        assertEquals(10, standardFrame.getScore());
        assertEquals(1, standardFrame.getRolls().size());
    }

    @Test
    @DisplayName("should reject third roll when standard frame already has two rolls")
    void shouldRejectThirdRollWhenStandardFrameAlreadyHasTwoRolls() {
        when(mockGenerateur.randomPin(10)).thenReturn(3).thenReturn(4).thenReturn(2);
        assertTrue(standardFrame.makeRoll());
        assertTrue(standardFrame.makeRoll());
        assertFalse(standardFrame.makeRoll());
        assertEquals(7, standardFrame.getScore());
        assertEquals(2, standardFrame.getRolls().size());
    }

    @Test
    @DisplayName("should increase score when second roll is made after strike in last frame")
    void shouldIncreaseScoreWhenSecondRollIsMadeAfterStrikeInLastFrame() {
        when(mockGenerateur.randomPin(10)).thenReturn(10).thenReturn(5);
        assertTrue(lastFrame.makeRoll());
        assertTrue(lastFrame.makeRoll());
        assertEquals(15, lastFrame.getScore());
        assertEquals(2, lastFrame.getRolls().size());
    }

    @Test
    @DisplayName("should accept third roll when last frame starts with strike")
    void shouldAcceptThirdRollWhenLastFrameStartsWithStrike() {
        when(mockGenerateur.randomPin(10)).thenReturn(10).thenReturn(5).thenReturn(3);
        assertTrue(lastFrame.makeRoll());
        assertTrue(lastFrame.makeRoll());
        assertTrue(lastFrame.makeRoll());
        assertEquals(18, lastFrame.getScore());
        assertEquals(3, lastFrame.getRolls().size());
    }

    @Test
    @DisplayName("should accept second roll when last frame starts with strike")
    void shouldAcceptSecondRollWhenLastFrameStartsWithStrike() {
        when(mockGenerateur.randomPin(10)).thenReturn(10).thenReturn(5);
        assertTrue(lastFrame.makeRoll());
        assertTrue(lastFrame.makeRoll());
        assertEquals(15, lastFrame.getScore());
        assertEquals(2, lastFrame.getRolls().size());
    }

    @Test
    @DisplayName("should increase score when third roll is made after strike in last frame")
    void shouldIncreaseScoreWhenThirdRollIsMadeAfterStrikeInLastFrame() {
        when(mockGenerateur.randomPin(10)).thenReturn(10).thenReturn(5).thenReturn(3);
        assertTrue(lastFrame.makeRoll());
        assertTrue(lastFrame.makeRoll());
        assertTrue(lastFrame.makeRoll());
        assertEquals(18, lastFrame.getScore());
        assertEquals(3, lastFrame.getRolls().size());
    }

    @Test
    @DisplayName("should accept third roll when last frame starts with spare")
    void shouldAcceptThirdRollWhenLastFrameStartsWithSpare() {
        when(mockGenerateur.randomPin(10)).thenReturn(6).thenReturn(4).thenReturn(3);
        assertTrue(lastFrame.makeRoll());
        assertTrue(lastFrame.makeRoll());
        assertTrue(lastFrame.isSpare());
        assertTrue(lastFrame.makeRoll());
        assertEquals(13, lastFrame.getScore());
        assertEquals(3, lastFrame.getRolls().size());
    }

    @Test
    @DisplayName("should increase score when third roll is made after spare in last frame")
    void shouldIncreaseScoreWhenThirdRollIsMadeAfterSpareInLastFrame() {
        when(mockGenerateur.randomPin(10)).thenReturn(6).thenReturn(4).thenReturn(5);
        assertTrue(lastFrame.makeRoll());
        assertTrue(lastFrame.makeRoll());
        assertTrue(lastFrame.makeRoll());
        assertEquals(15, lastFrame.getScore());
        assertEquals(3, lastFrame.getRolls().size());
    }

    @Test
    @DisplayName("should reject third roll when last frame has no strike or spare")
    void shouldRejectThirdRollWhenLastFrameHasNoStrikeOrSpare() {
        when(mockGenerateur.randomPin(10)).thenReturn(3).thenReturn(4).thenReturn(2);
        assertTrue(lastFrame.makeRoll());
        assertTrue(lastFrame.makeRoll());
        assertFalse(lastFrame.makeRoll());
        assertEquals(7, lastFrame.getScore());
        assertEquals(2, lastFrame.getRolls().size());
    }

    @Test
    @DisplayName("should reject fourth roll in last frame")
    void shouldRejectFourthRollInLastFrame() {
        when(mockGenerateur.randomPin(10)).thenReturn(10).thenReturn(5).thenReturn(3).thenReturn(2);
        assertTrue(lastFrame.makeRoll());
        assertTrue(lastFrame.makeRoll());
        assertTrue(lastFrame.makeRoll());
        assertFalse(lastFrame.makeRoll());
        assertEquals(18, lastFrame.getScore());
        assertEquals(3, lastFrame.getRolls().size());
    }

    @Test
    @DisplayName("should detect strike correctly")
    void shouldDetectStrikeCorrectly() {
        when(mockGenerateur.randomPin(10)).thenReturn(10);
        assertTrue(standardFrame.makeRoll());
        assertTrue(standardFrame.isStrike());
    }

    @Test
    @DisplayName("should not detect strike when no strike")
    void shouldNotDetectStrikeWhenNoStrike() {
        when(mockGenerateur.randomPin(10)).thenReturn(5).thenReturn(3);
        assertTrue(standardFrame.makeRoll());
        assertFalse(standardFrame.isStrike());
        assertTrue(standardFrame.makeRoll());
        assertFalse(standardFrame.isStrike());
    }

    @Test
    @DisplayName("should detect spare correctly")
    void shouldDetectSpareCorrectly() {
        when(mockGenerateur.randomPin(10)).thenReturn(7).thenReturn(3);
        assertTrue(standardFrame.makeRoll());
        assertFalse(standardFrame.isSpare());
        assertTrue(standardFrame.makeRoll());
        assertTrue(standardFrame.isSpare());
    }

    @Test
    @DisplayName("should not detect spare when no spare")
    void shouldNotDetectSpareWhenNoSpare() {
        when(mockGenerateur.randomPin(10)).thenReturn(5).thenReturn(3);
        assertTrue(standardFrame.makeRoll());
        assertTrue(standardFrame.makeRoll());
        assertFalse(standardFrame.isSpare());
    }
}



